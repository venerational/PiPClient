package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.FireColorModule;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ScreenEffectRenderer.class})
public class FireColorOverlayMixin {
   @ModifyArgs(
      method = {"renderFire"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      )
   )
   private static void lyfwclient$tintFireOverlay(Args args) {
      FireColorModule fire = FireColorModule.get();
      if (fire != null && fire.isEnabled()) {
         int rgb = fire.rgb();
         args.set(0, (rgb >> 16 & 0xFF) / 255.0F);
         args.set(1, (rgb >> 8 & 0xFF) / 255.0F);
         args.set(2, (rgb & 0xFF) / 255.0F);
      }
   }
}
