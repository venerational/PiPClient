package dev.lyfw.lyfwclient.motionblur;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.GpuBuffer.MappedView;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import dev.lyfw.lyfwclient.mixin.PostEffectPassAccessor;
import dev.lyfw.lyfwclient.mixin.PostEffectProcessorAccessor;
import dev.lyfw.lyfwclient.mixin.ShaderLoaderAccessor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.PostPass.Input;
import net.minecraft.client.renderer.ShaderManager.CompilationCache;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class FrameBlendingManager {
   private static final int FRAME_BLEND_UBO_SIZE = 64;
   private static final int ACCUM_UBO_SIZE = 16;
   private static final int MAX_HISTORY = 12;
   private static final int UBO_RING_SIZE = 3;
   private static final String FRAME_BLEND_UBO = "FrameBlendParamsUniforms";
   private static final String ACCUM_UBO = "AccumulationUniforms";
   private static final Identifier FRAME_BLENDING_ID = Identifier.fromNamespaceAndPath("lyfw-client", "motion_blur_frame_blending");
   private static final Identifier ACCUMULATION_MAX_ID = Identifier.fromNamespaceAndPath("lyfw-client", "motion_blur_accumulation_max");
   private static final Identifier ACCUMULATION_MIX_ID = Identifier.fromNamespaceAndPath("lyfw-client", "motion_blur_accumulation_mix");
   private static final String[] SAMPLE_NAMES = new String[12];
   private static PostChain cachedCombineChain;
   private static final ManagedUniformBuffer.Ring combineUBORing;
   private static final RenderTarget[] historyTargets;
   private static final FrameBlendingManager.MutableTextureInput[] historyInputs;
   private static final double[] historyTimestamps;
   private static final int[] weightedHistoryIndices;
   private static final float[] weightedHistoryWeights;
   private static int historyWriteIndex;
   private static int historyFilled;
   private static float smoothedFPS;
   private static PostChain cachedAccumMaxChain;
   private static PostChain cachedAccumMixChain;
   private static final ManagedUniformBuffer accumSimpleUBO;
   private static RenderTarget accumReadTarget;
   private static RenderTarget accumWriteTarget;
   private static FrameBlendingManager.MutableTextureInput injectedMainInput;
   private static FrameBlendingManager.MutableTextureInput injectedPrevInput;
   private static boolean accumHasPrevious;
   private static int targetW;
   private static int targetH;
   private static final Set<String> loadErrorLogged;

   public static void applyFrameBlending(GraphicsResourceAllocator allocator, float fps, int refreshRate) {
      Minecraft client = Minecraft.getInstance();
      RenderTarget main = client.getMainRenderTarget();
      updateSmoothedFPS(fps);
      if (refreshRate <= 0) {
         historyWriteIndex = 0;
         historyFilled = 0;
      } else {
         ensureTargets(main.width, main.height);
         double now = currentTimeSeconds();
         pushHistoryFrame(main, now);
         int sampleCount = buildWeightedSampleList(now, refreshRate, smoothedFPS);
         if (sampleCount > 1) {
            PostChain combineChain = loadFrameBlendChain();
            if (combineChain != null) {
               PostPass combinePass = firstPass(combineChain);
               if (combinePass != null) {
                  Map<String, GpuBuffer> combineUniforms = ((PostEffectPassAccessor)combinePass).lyfwclient$getCustomUniforms();
                  if (combineUniforms.containsKey("FrameBlendParamsUniforms")) {
                     GpuBuffer combineUBO = combineUBORing.putNext(combineChain, combineUniforms, "FrameBlendParamsUniforms");

                     try {
                        writeBlendParamsUBO(combineUBO, inverseTotalWeight(sampleCount), sampleCount);
                        RenderTarget fallback = historyTargets[weightedHistoryIndices[sampleCount - 1]];

                        for (int i = 0; i < 12; i++) {
                           RenderTarget target = i < sampleCount ? historyTargets[weightedHistoryIndices[i]] : fallback;
                           FrameBlendingManager.MutableTextureInput input = historyInputs[i];
                           if (input == null) {
                              input = new FrameBlendingManager.MutableTextureInput(SAMPLE_NAMES[i], target);
                              historyInputs[i] = input;
                           } else {
                              input.setTarget(target);
                           }

                           setSampler(combinePass, SAMPLE_NAMES[i], input);
                        }

                        combineChain.process(main, allocator);
                     } catch (RuntimeException var16) {
                        if (!combineUBORing.resetIfClosed(var16)) {
                           throw var16;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void applyAccumulationMax(GraphicsResourceAllocator allocator, float strength) {
      applyAccumulationInternal(allocator, strength * 7.0F, "accumulation_max", true);
   }

   public static void applyAccumulationMix(GraphicsResourceAllocator allocator, float strength) {
      applyAccumulationInternal(allocator, strength * 5.0F, "accumulation_mix", false);
   }

   public static void invalidate() {
      for (int i = 0; i < historyTargets.length; i++) {
         if (historyTargets[i] != null) {
            historyTargets[i].destroyBuffers();
            historyTargets[i] = null;
         }

         historyInputs[i] = null;
         historyTimestamps[i] = 0.0;
      }

      if (accumReadTarget != null) {
         accumReadTarget.destroyBuffers();
         accumReadTarget = null;
      }

      if (accumWriteTarget != null) {
         accumWriteTarget.destroyBuffers();
         accumWriteTarget = null;
      }

      combineUBORing.reset();
      accumSimpleUBO.reset();
      targetW = 0;
      targetH = 0;
      historyWriteIndex = 0;
      historyFilled = 0;
      smoothedFPS = 0.0F;
      Arrays.fill(weightedHistoryIndices, 0);
      Arrays.fill(weightedHistoryWeights, 0.0F);
      cachedCombineChain = null;
      cachedAccumMaxChain = null;
      cachedAccumMixChain = null;
      injectedMainInput = null;
      injectedPrevInput = null;
      accumHasPrevious = false;
      loadErrorLogged.clear();
   }

   private static void updateSmoothedFPS(float fps) {
      if (fps > 0.0F) {
         smoothedFPS = smoothedFPS <= 0.0F ? fps : smoothedFPS * 0.85F + fps * 0.15F;
      }
   }

   private static void pushHistoryFrame(RenderTarget src, double timestamp) {
      if (historyTargets[historyWriteIndex] != null) {
         copyTexture(src, historyTargets[historyWriteIndex]);
         historyTimestamps[historyWriteIndex] = timestamp;
         historyWriteIndex = (historyWriteIndex + 1) % 12;
         if (historyFilled < 12) {
            historyFilled++;
         }
      }
   }

   private static int buildWeightedSampleList(double exposureEnd, int refreshRate, float fps) {
      Arrays.fill(weightedHistoryWeights, 0.0F);
      if (historyFilled <= 0) {
         return 0;
      } else {
         double exposureStart = exposureEnd - 1.0 / refreshRate;
         double estimatedFrameTime = fps > 0.0F ? 1.0 / fps : 1.0 / refreshRate;
         double totalWeight = 0.0;
         int sampleCount = 0;
         int firstIndex = historyWriteIndex - historyFilled;
         if (firstIndex < 0) {
            firstIndex += 12;
         }

         for (int i = 0; i < historyFilled; i++) {
            int idx = (firstIndex + i) % 12;
            double frameEnd = historyTimestamps[idx];
            if (!(frameEnd <= 0.0)) {
               double frameStart;
               if (i > 0) {
                  int prevIdx = (firstIndex + i - 1) % 12;
                  frameStart = historyTimestamps[prevIdx];
               } else {
                  frameStart = frameEnd - estimatedFrameTime;
               }

               if (frameStart >= frameEnd) {
                  frameStart = frameEnd - estimatedFrameTime;
               }

               double overlap = Math.min(frameEnd, exposureEnd) - Math.max(frameStart, exposureStart);
               if (overlap > 1.0E-7) {
                  weightedHistoryIndices[sampleCount] = idx;
                  weightedHistoryWeights[sampleCount] = (float)overlap;
                  totalWeight += overlap;
                  sampleCount++;
               }
            }
         }

         return sampleCount > 0 && !(totalWeight <= 1.0E-7) ? sampleCount : 0;
      }
   }

   private static float inverseTotalWeight(int sampleCount) {
      float totalWeight = 0.0F;

      for (int i = 0; i < sampleCount; i++) {
         totalWeight += weightedHistoryWeights[i];
      }

      return totalWeight > 0.0F ? 1.0F / totalWeight : 1.0F;
   }

   private static double currentTimeSeconds() {
      return System.nanoTime() * 1.0E-9;
   }

   private static void applyAccumulationInternal(GraphicsResourceAllocator allocator, float strength, String shaderName, boolean isMax) {
      Minecraft client = Minecraft.getInstance();
      RenderTarget main = client.getMainRenderTarget();
      ensureTargets(main.width, main.height);
      if (!accumHasPrevious) {
         copyTexture(main, accumReadTarget);
         accumHasPrevious = true;
      } else {
         PostChain chain = loadAccumSimpleChain(shaderName, isMax);
         if (chain != null) {
            PostPass pass = firstPass(chain);
            if (pass != null) {
               Map<String, GpuBuffer> uniforms = ((PostEffectPassAccessor)pass).lyfwclient$getCustomUniforms();
               if (uniforms.containsKey("AccumulationUniforms")) {
                  GpuBuffer ubo = accumSimpleUBO.put(chain, uniforms, "AccumulationUniforms");

                  try {
                     writeFloatUBO(ubo, strengthToBlendFactor(strength));
                     if (injectedMainInput == null) {
                        injectedMainInput = new FrameBlendingManager.MutableTextureInput("Main", main);
                     } else {
                        injectedMainInput.setTarget(main);
                     }

                     setSampler(pass, "Main", injectedMainInput);
                     if (injectedPrevInput == null) {
                        injectedPrevInput = new FrameBlendingManager.MutableTextureInput("Prev", accumReadTarget);
                     } else {
                        injectedPrevInput.setTarget(accumReadTarget);
                     }

                     setSampler(pass, "Prev", injectedPrevInput);
                     chain.process(accumWriteTarget, allocator);
                     copyTexture(accumWriteTarget, main);
                     swapAccumTargets();
                  } catch (RuntimeException var11) {
                     if (!accumSimpleUBO.resetIfClosed(var11)) {
                        throw var11;
                     }
                  }
               }
            }
         }
      }
   }

   private static float strengthToBlendFactor(float strength) {
      return (float)(1.0 - Math.pow(0.5, strength / 3.0));
   }

   private static PostChain loadAccumSimpleChain(String shaderName, boolean isMax) {
      try {
         Minecraft client = Minecraft.getInstance();
         CompilationCache cache = ((ShaderLoaderAccessor)client.getShaderManager()).lyfwclient$getCompilationCache();
         if (cache == null) {
            return null;
         } else {
            PostChain result = cache.getOrLoadPostChain(isMax ? ACCUMULATION_MAX_ID : ACCUMULATION_MIX_ID, LevelTargetBundle.MAIN_TARGETS);
            if (isMax && result != cachedAccumMaxChain) {
               cachedAccumMaxChain = result;
               injectedMainInput = null;
               injectedPrevInput = null;
            } else if (!isMax && result != cachedAccumMixChain) {
               cachedAccumMixChain = result;
               injectedMainInput = null;
               injectedPrevInput = null;
            }

            loadErrorLogged.remove(shaderName);
            return result;
         }
      } catch (Exception var5) {
         if (loadErrorLogged.add(shaderName)) {
            System.err.println("[MotionBlur] Failed to load " + shaderName + " shader: " + var5.getMessage());
         }

         return null;
      }
   }

   private static void ensureTargets(int w, int h) {
      if (targetW != w || targetH != h || historyTargets[0] == null || accumReadTarget == null || accumWriteTarget == null) {
         for (int i = 0; i < historyTargets.length; i++) {
            if (historyTargets[i] != null) {
               historyTargets[i].destroyBuffers();
            }

            historyTargets[i] = new MainTarget(w, h);
            historyInputs[i] = null;
            historyTimestamps[i] = 0.0;
         }

         if (accumReadTarget != null) {
            accumReadTarget.destroyBuffers();
         }

         if (accumWriteTarget != null) {
            accumWriteTarget.destroyBuffers();
         }

         accumReadTarget = new MainTarget(w, h);
         accumWriteTarget = new MainTarget(w, h);
         targetW = w;
         targetH = h;
         historyWriteIndex = 0;
         historyFilled = 0;
         Arrays.fill(weightedHistoryIndices, 0);
         Arrays.fill(weightedHistoryWeights, 0.0F);
         injectedMainInput = null;
         injectedPrevInput = null;
         accumHasPrevious = false;
      }
   }

   private static void swapAccumTargets() {
      RenderTarget temp = accumReadTarget;
      accumReadTarget = accumWriteTarget;
      accumWriteTarget = temp;
   }

   private static void copyTexture(RenderTarget src, RenderTarget dst) {
      if (src != null && dst != null) {
         RenderSystem.getDevice()
            .createCommandEncoder()
            .copyTextureToTexture(src.getColorTexture(), dst.getColorTexture(), 0, 0, 0, 0, 0, dst.width, dst.height);
      }
   }

   private static PostPass firstPass(PostChain chain) {
      List<PostPass> passes = ((PostEffectProcessorAccessor)chain).lyfwclient$getPasses();
      return passes.isEmpty() ? null : passes.get(0);
   }

   private static void setSampler(PostPass pass, String samplerName, Input replacement) {
      List<Input> samplers = ((PostEffectPassAccessor)pass).lyfwclient$getSamplers();

      for (int i = 0; i < samplers.size(); i++) {
         if (samplerName.equals(samplers.get(i).samplerName())) {
            if (samplers.get(i) != replacement) {
               samplers.set(i, replacement);
            }

            return;
         }
      }
   }

   private static void writeFloatUBO(GpuBuffer ubo, float value) {
      MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(ubo, false, true);

      try {
         Std140Builder b = Std140Builder.intoBuffer(view.data());
         b.putFloat(value);
         b.putInt(0);
         b.putInt(0);
         b.putInt(0);
      } finally {
         view.close();
      }
   }

   private static void writeBlendParamsUBO(GpuBuffer ubo, float invTotalWeight, int sampleCount) {
      MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(ubo, false, true);

      try {
         Std140Builder b = Std140Builder.intoBuffer(view.data());
         b.putFloat(invTotalWeight);
         b.putInt(sampleCount);

         for (int i = 0; i < 12; i++) {
            b.putFloat(i < sampleCount ? weightedHistoryWeights[i] : 0.0F);
         }

         b.putFloat(0.0F);
         b.putFloat(0.0F);
      } finally {
         view.close();
      }
   }

   private static PostChain loadFrameBlendChain() {
      try {
         Minecraft client = Minecraft.getInstance();
         CompilationCache cache = ((ShaderLoaderAccessor)client.getShaderManager()).lyfwclient$getCompilationCache();
         if (cache == null) {
            return null;
         } else {
            PostChain result = cache.getOrLoadPostChain(FRAME_BLENDING_ID, LevelTargetBundle.MAIN_TARGETS);
            if (result != cachedCombineChain) {
               cachedCombineChain = result;
            }

            loadErrorLogged.remove("frame_blending");
            return result;
         }
      } catch (Exception var3) {
         if (loadErrorLogged.add("frame_blending")) {
            System.err.println("[MotionBlur] Failed to load frame_blending shader: " + var3.getMessage());
         }

         return null;
      }
   }

   static {
      for (int i = 0; i < 12; i++) {
         SAMPLE_NAMES[i] = "Sample" + i;
      }

      cachedCombineChain = null;
      combineUBORing = new ManagedUniformBuffer.Ring("FrameBlendParamsUniforms", 64, 3);
      historyTargets = new RenderTarget[12];
      historyInputs = new FrameBlendingManager.MutableTextureInput[12];
      historyTimestamps = new double[12];
      weightedHistoryIndices = new int[12];
      weightedHistoryWeights = new float[12];
      historyWriteIndex = 0;
      historyFilled = 0;
      smoothedFPS = 0.0F;
      cachedAccumMaxChain = null;
      cachedAccumMixChain = null;
      accumSimpleUBO = new ManagedUniformBuffer("AccumulationUniforms", 16);
      accumReadTarget = null;
      accumWriteTarget = null;
      injectedMainInput = null;
      injectedPrevInput = null;
      accumHasPrevious = false;
      targetW = 0;
      targetH = 0;
      loadErrorLogged = new HashSet<>();
   }

   private static class MutableTextureInput implements Input {
      private final String samplerName;
      private RenderTarget target;

      MutableTextureInput(String samplerName, RenderTarget target) {
         this.samplerName = samplerName;
         this.target = target;
      }

      void setTarget(RenderTarget target) {
         this.target = target;
      }

      public void addToPass(@NonNull FramePass pass, @NonNull Map<Identifier, ResourceHandle<RenderTarget>> internalTargets) {
      }

      @NonNull
      public GpuTextureView texture(@NonNull Map<Identifier, ResourceHandle<RenderTarget>> internalTargets) {
         return this.target.getColorTextureView();
      }

      @NonNull
      public String samplerName() {
         return this.samplerName;
      }

      public boolean bilinear() {
         return false;
      }
   }
}
