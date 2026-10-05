package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.modules.NoHandSwayModule;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class HandBobMixin {
   private static boolean lyfwclient$inHand;

   @Inject(
      method = {"renderItemInHand"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$handStart(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
      lyfwclient$inHand = true;
   }

   @Inject(
      method = {"renderItemInHand"},
      at = {@At("RETURN")}
   )
   private void lyfwclient$handEnd(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
      lyfwclient$inHand = false;
   }

   @Inject(
      method = {"bobView"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$skipHandBob(PoseStack matrices, float tickProgress, CallbackInfo ci) {
      NoHandSwayModule sway = NoHandSwayModule.get();
      if (lyfwclient$inHand && sway != null && sway.bobRemoved()) {
         ci.cancel();
      }
   }
}
