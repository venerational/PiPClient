package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.FireColorModule;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({FlameFeatureRenderer.class})
public class FireColorEntityMixin {
   @ModifyArg(
      method = {"fireVertex"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
      ),
      index = 0
   )
   private static int lyfwclient$tintEntityFire(int color) {
      FireColorModule fire = FireColorModule.get();
      return fire != null && fire.isEnabled() ? fire.tint(color) : color;
   }
}
