package dev.lyfw.lyfwclient.motionblur;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.GpuBuffer.MappedView;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lyfw.lyfwclient.mixin.PostEffectPassAccessor;
import dev.lyfw.lyfwclient.mixin.PostEffectProcessorAccessor;
import dev.lyfw.lyfwclient.mixin.ShaderLoaderAccessor;
import dev.lyfw.lyfwclient.module.modules.MotionBlurModule;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.ShaderManager.CompilationCache;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class MotionBlurShaderManager {
   private static final FrameTimer frameTimer = new FrameTimer();
   private static final CameraState cameraState = new CameraState();
   private static final BlurStrengthCalculator strengthCalc = new BlurStrengthCalculator();
   private static GraphicsResourceAllocator frameAllocator = null;
   private static boolean deferredTemporalBlurApplied = false;
   private static PostChain cachedPreProcessor = null;
   private static PostChain cachedF5Processor = null;
   private static PostChain cachedPostProcessor = null;
   private static final Set<String> loadErrorLogged = new HashSet<>();
   private static final int UBO_SIZE = 304;
   private static final ManagedUniformBuffer preEntityUBO = new ManagedUniformBuffer("PreEntityBlurUniforms", 304);
   private static final ManagedUniformBuffer f5EntityUBO = new ManagedUniformBuffer("PreEntityBlurUniforms", 304);
   private static final ManagedUniformBuffer postRenderUBO = new ManagedUniformBuffer("PostRenderBlurUniforms", 304);

   public static void captureAllocator(GraphicsResourceAllocator allocator) {
      frameAllocator = allocator;
   }

   public static void clearFrameAllocator() {
      frameAllocator = null;
   }

   public static void beginFrame() {
      frameTimer.beginFrame();
      deferredTemporalBlurApplied = false;
   }

   public static float getCurrentFPS() {
      return frameTimer.getFPS();
   }

   public static void invalidate() {
      preEntityUBO.reset();
      f5EntityUBO.reset();
      postRenderUBO.reset();
      FrameBlendingManager.invalidate();
   }

   public static void setFrameMotionBlur(Matrix4f modelView, Matrix4f prevModelView, Matrix4f projection, Matrix4f prevProjection, float dx, float dy, float dz) {
      cameraState.setFrame(modelView, prevModelView, projection, prevProjection, dx, dy, dz);
   }

   public static void applyPreEntityBlur() {
      if (shouldRun()) {
         applyBlurInternal(MotionBlurShaderManager.BlurPass.NORMAL_PRE, true);
      }
   }

   public static void applyF5EntityRideBlur() {
      if (shouldRun()) {
         applyBlurInternal(MotionBlurShaderManager.BlurPass.SPECIAL_F5, true);
      }
   }

   public static void applyPostRenderVelocityOnly() {
      if (shouldRun()) {
         applyBlurInternal(MotionBlurShaderManager.BlurPass.NORMAL_POST, false);
      }
   }

   public static void applyDeferredTemporalBlur() {
      MotionBlurModule module = MotionBlurModule.getInstance();
      if (!deferredTemporalBlurApplied && frameAllocator != null && module != null && shouldRun()) {
         BlurAlgorithm algorithm = module.algorithm();
         switch (algorithm) {
            case FRAME_BLENDING:
            case HYBRID_BLENDING:
               applyFrameBlendingInternal();
               deferredTemporalBlurApplied = true;
               break;
            case ACCUMULATION_MAX:
               FrameBlendingManager.applyAccumulationMax(frameAllocator, module.effectiveStrength());
               deferredTemporalBlurApplied = true;
               break;
            case ACCUMULATION_MIX:
               FrameBlendingManager.applyAccumulationMix(frameAllocator, module.effectiveStrength());
               deferredTemporalBlurApplied = true;
         }
      }
   }

   private static boolean shouldRun() {
      MotionBlurModule module = MotionBlurModule.getInstance();
      return module != null && module.isEnabled() && module.effectiveStrength() != 0.0F;
   }

   private static void applyBlurInternal(MotionBlurShaderManager.BlurPass pass, boolean includeTemporal) {
      if (frameAllocator != null) {
         MotionBlurModule module = MotionBlurModule.getInstance();
         if (module != null) {
            Minecraft client = Minecraft.getInstance();
            if (includeTemporal) {
               BlurAlgorithm algo = module.algorithm();
               if (algo == BlurAlgorithm.FRAME_BLENDING || algo == BlurAlgorithm.ACCUMULATION_MAX || algo == BlurAlgorithm.ACCUMULATION_MIX) {
                  return;
               }
            } else if (!module.algorithm().usesVelocityBlur()) {
               return;
            }

            BlurStrengthCalculator.Result blur = strengthCalc.calculate(
               module.effectiveStrength(),
               frameTimer.getFPS(),
               frameTimer.getRefreshRate(),
               module.refreshRateScaling() && module.algorithm().allowsRefreshRateScaling()
            );
            float viewW = client.getMainRenderTarget().width;
            float viewH = client.getMainRenderTarget().height;
            int algo = module.algorithm().ordinal();
            switch (pass) {
               case NORMAL_PRE:
                  PostChain px = getPreProcessor();
                  if (px != null) {
                     writeAndRun(px, "PreEntityBlurUniforms", preEntityUBO, blur.strength(), viewW, viewH, algo, blur.sampleAmount(), client);
                  }
                  break;
               case SPECIAL_F5:
                  PostChain p = getF5Processor();
                  if (p != null) {
                     writeAndRun(p, "PreEntityBlurUniforms", f5EntityUBO, blur.strength(), viewW, viewH, algo, blur.sampleAmount(), client);
                  }
                  break;
               case NORMAL_POST:
                  PostChain pp = getPostProcessor();
                  if (pp != null) {
                     writeAndRun(pp, "PostRenderBlurUniforms", postRenderUBO, blur.strength(), viewW, viewH, algo, blur.sampleAmount(), client);
                  }

                  if (module.algorithm() == BlurAlgorithm.HYBRID_BLENDING) {
                     applyFrameBlendingInternal();
                  }
            }
         }
      }
   }

   private static void applyFrameBlendingInternal() {
      if (frameAllocator != null) {
         FrameBlendingManager.applyFrameBlending(frameAllocator, frameTimer.getFPS(), frameTimer.getRefreshRate());
      }
   }

   private static PostChain getPreProcessor() {
      PostChain result = loadProcessor("velocity_pre", "pre-entity");
      if (result == null) {
         cachedPreProcessor = null;
         return null;
      } else {
         cachedPreProcessor = result;
         return cachedPreProcessor;
      }
   }

   private static PostChain getF5Processor() {
      PostChain result = loadProcessor("velocity_f5", "F5/entity-riding");
      if (result == null) {
         cachedF5Processor = null;
         return null;
      } else {
         cachedF5Processor = result;
         return cachedF5Processor;
      }
   }

   private static PostChain getPostProcessor() {
      PostChain result = loadProcessor("velocity_post", "post-render");
      if (result == null) {
         cachedPostProcessor = null;
         return null;
      } else {
         cachedPostProcessor = result;
         return cachedPostProcessor;
      }
   }

   static PostChain loadProcessor(String shaderName, String displayName) {
      try {
         Minecraft client = Minecraft.getInstance();
         CompilationCache cache = ((ShaderLoaderAccessor)client.getShaderManager()).lyfwclient$getCompilationCache();
         if (cache == null) {
            return null;
         } else {
            PostChain chain = cache.getOrLoadPostChain(
               Identifier.fromNamespaceAndPath("lyfw-client", "motion_blur_" + shaderName), LevelTargetBundle.MAIN_TARGETS
            );
            loadErrorLogged.remove(shaderName);
            return chain;
         }
      } catch (Exception var5) {
         if (loadErrorLogged.add(shaderName)) {
            System.err.println("[MotionBlur] Failed to load " + displayName + " shader: " + var5.getMessage());
         }

         return null;
      }
   }

   private static void writeAndRun(
      PostChain processor,
      String uboKey,
      ManagedUniformBuffer managedUBO,
      float blendFactor,
      float viewW,
      float viewH,
      int blurAlgorithm,
      int sampleAmount,
      Minecraft client
   ) {
      List<PostPass> passes = ((PostEffectProcessorAccessor)processor).lyfwclient$getPasses();
      if (!passes.isEmpty()) {
         Map<String, GpuBuffer> uniformBuffers = ((PostEffectPassAccessor)passes.get(0)).lyfwclient$getCustomUniforms();
         if (uniformBuffers.containsKey(uboKey)) {
            GpuBuffer ubo = managedUBO.put(processor, uniformBuffers, uboKey);

            try {
               MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(ubo, false, true);

               try {
                  Std140Builder b = Std140Builder.intoBuffer(view.data());
                  b.putMat4f(cameraState.getMvInverse());
                  b.putMat4f(cameraState.getProjInverse());
                  b.putMat4f(cameraState.getPrevModelView());
                  b.putMat4f(cameraState.getPrevProjection());
                  b.putVec3(cameraState.getDx(), cameraState.getDy(), cameraState.getDz());
                  b.putVec2(viewW, viewH);
                  b.putFloat(blendFactor);
                  b.putInt(sampleAmount);
                  b.putInt(blurAlgorithm);
                  b.putInt(1);
               } finally {
                  view.close();
               }

               processor.process(client.getMainRenderTarget(), frameAllocator);
            } catch (RuntimeException var18) {
               if (!managedUBO.resetIfClosed(var18)) {
                  throw var18;
               }
            }
         }
      }
   }

   private static enum BlurPass {
      NORMAL_PRE,
      SPECIAL_F5,
      NORMAL_POST;
   }
}
