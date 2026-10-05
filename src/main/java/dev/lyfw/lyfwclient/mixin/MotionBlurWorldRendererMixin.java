package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.modules.MotionBlurModule;
import dev.lyfw.lyfwclient.motionblur.BlurAlgorithm;
import dev.lyfw.lyfwclient.motionblur.MotionBlurShaderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MotionBlurWorldRendererMixin {
   @Unique
   private final Matrix4f prevModelView = new Matrix4f();
   @Unique
   private final Matrix4f prevProjection = new Matrix4f();
   @Unique
   private final Matrix4f scratchModelView = new Matrix4f();
   @Unique
   private final Matrix4f scratchProjection = new Matrix4f();
   @Unique
   private double prevCamX;
   @Unique
   private double prevCamY;
   @Unique
   private double prevCamZ;
   @Unique
   private boolean previousFrameReady = false;

   @Inject(
      method = {"renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$onRenderHead(
      GraphicsResourceAllocator resourceAllocator,
      DeltaTracker deltaTracker,
      boolean renderOutline,
      Camera camera,
      Matrix4f modelViewMatrix,
      Matrix4f projectionMatrix,
      Matrix4f frustumMatrix,
      GpuBufferSlice terrainFog,
      Vector4f fogColor,
      boolean shouldRenderSky,
      CallbackInfo ci
   ) {
      MotionBlurModule module = MotionBlurModule.getInstance();
      boolean blurActive = module != null && module.isEnabled() && module.effectiveStrength() != 0.0F;
      boolean needsVelocityState = blurActive && module.algorithm().usesVelocityBlur();
      Vec3 camPos = camera.position();
      double cx = camPos.x();
      double cy = camPos.y();
      double cz = camPos.z();
      if (!blurActive) {
         MotionBlurShaderManager.clearFrameAllocator();
         this.lyfwclient$rememberCurrentFrameState(modelViewMatrix, projectionMatrix, cx, cy, cz);
      } else {
         MotionBlurShaderManager.captureAllocator(resourceAllocator);
         MotionBlurShaderManager.beginFrame();
         if (!needsVelocityState) {
            this.lyfwclient$rememberCurrentFrameState(modelViewMatrix, projectionMatrix, cx, cy, cz);
         } else {
            this.scratchModelView.set(modelViewMatrix);
            this.scratchProjection.set(projectionMatrix);
            if (!this.previousFrameReady) {
               MotionBlurShaderManager.setFrameMotionBlur(
                  this.scratchModelView, this.scratchModelView, this.scratchProjection, this.scratchProjection, 0.0F, 0.0F, 0.0F
               );
               this.lyfwclient$rememberCurrentFrameState(this.scratchModelView, this.scratchProjection, cx, cy, cz);
            } else {
               float dx = (float)(cx - this.prevCamX);
               float dy = (float)(cy - this.prevCamY);
               float dz = (float)(cz - this.prevCamZ);
               MotionBlurShaderManager.setFrameMotionBlur(this.scratchModelView, this.prevModelView, this.scratchProjection, this.prevProjection, dx, dy, dz);
               this.lyfwclient$rememberCurrentFrameState(this.scratchModelView, this.scratchProjection, cx, cy, cz);
            }
         }
      }
   }

   @Unique
   private void lyfwclient$rememberCurrentFrameState(Matrix4fc modelViewMatrix, Matrix4fc projectionMatrix, double cx, double cy, double cz) {
      this.prevModelView.set(modelViewMatrix);
      this.prevProjection.set(projectionMatrix);
      this.prevCamX = cx;
      this.prevCamY = cy;
      this.prevCamZ = cz;
      this.previousFrameReady = true;
   }

   @Inject(
      method = {"submitEntities"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$beforePushEntityRenders(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector output, CallbackInfo ci) {
      MotionBlurModule module = MotionBlurModule.getInstance();
      if (module != null && module.isEnabled() && module.effectiveStrength() != 0.0F && module.algorithm().usesVelocityBlur()) {
         if (this.lyfwclient$shouldUseSpecialSingleBlur()) {
            MotionBlurShaderManager.applyF5EntityRideBlur();
         } else {
            MotionBlurShaderManager.applyPreEntityBlur();
         }
      }
   }

   @Inject(
      method = {"renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$onRenderTail(
      GraphicsResourceAllocator resourceAllocator,
      DeltaTracker deltaTracker,
      boolean renderOutline,
      Camera camera,
      Matrix4f modelViewMatrix,
      Matrix4f projectionMatrix,
      Matrix4f frustumMatrix,
      GpuBufferSlice terrainFog,
      Vector4f fogColor,
      boolean shouldRenderSky,
      CallbackInfo ci
   ) {
      MotionBlurModule module = MotionBlurModule.getInstance();
      if (module != null) {
         boolean specialSingleBlur = this.lyfwclient$shouldUseSpecialSingleBlur();
         BlurAlgorithm algo = module.algorithm();
         if (algo == BlurAlgorithm.HYBRID_BLENDING) {
            if (!specialSingleBlur) {
               MotionBlurShaderManager.applyPostRenderVelocityOnly();
            }
         } else if (algo == BlurAlgorithm.VELOCITY_BASED && !specialSingleBlur) {
            MotionBlurShaderManager.applyPostRenderVelocityOnly();
         }
      }
   }

   @Unique
   private boolean lyfwclient$shouldUseSpecialSingleBlur() {
      Minecraft client = Minecraft.getInstance();
      return client.options.getCameraType() != CameraType.FIRST_PERSON || client.player != null && client.player.isPassenger();
   }
}
