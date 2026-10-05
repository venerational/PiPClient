package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.CustomFogModule;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({FogRenderer.class})
public class CustomFogMixin {
   @ModifyArgs(
      method = {"setupFog(Lnet/minecraft/client/Camera;ILnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lorg/joml/Vector4f;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"
      )
   )
   private void lyfwclient$pullFogIn(Args args) {
      if (CustomFogModule.changesDistance()) {
         float end = CustomFogModule.fogEnd((Float)args.get(6));
         float start = CustomFogModule.fogStart(end);
         args.set(5, start);
         args.set(6, end);
         float environmentalEnd = (Float)args.get(4);
         if (environmentalEnd > end) {
            args.set(3, Math.min((Float)args.get(3), start));
            args.set(4, end);
         }

         if (CustomFogModule.skyTinted()) {
            args.set(7, Math.min((Float)args.get(7), end));
            args.set(8, Math.min((Float)args.get(8), end));
         }
      }
   }

   @Inject(
      method = {"computeFogColor"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void lyfwclient$tintFog(
      Camera camera, float tickProgress, ClientLevel world, int viewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir
   ) {
      boolean inFluid = camera.getFluidInCamera() != FogType.NONE;
      Vector4f tinted = CustomFogModule.tint((Vector4f)cir.getReturnValue(), inFluid);
      if (tinted != null) {
         cir.setReturnValue(tinted);
      }
   }
}
