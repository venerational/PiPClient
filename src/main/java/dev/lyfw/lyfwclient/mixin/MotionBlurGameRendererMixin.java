package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.motionblur.MotionBlurShaderManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class MotionBlurGameRendererMixin {
   @Inject(
      method = {"renderLevel"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$afterRenderWorld(DeltaTracker tickCounter, CallbackInfo ci) {
      MotionBlurShaderManager.applyDeferredTemporalBlur();
      MotionBlurShaderManager.clearFrameAllocator();
   }
}
