package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lyfw.lyfwclient.module.modules.NoHandSwayModule;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({ItemInHandRenderer.class})
public class HandSwayMixin {
   private static final String RENDER_ITEM = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V";
   private static final Quaternionf LYFWCLIENT$IDENTITY = new Quaternionf();

   @ModifyArg(
      method = {"renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V"
      ),
      index = 0
   )
   private Quaternionfc lyfwclient$flattenSway(Quaternionfc rotation) {
      NoHandSwayModule sway = NoHandSwayModule.get();
      return (Quaternionfc)(sway != null && sway.swayRemoved() ? LYFWCLIENT$IDENTITY : rotation);
   }

   @ModifyExpressionValue(
      method = {"renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getAttackAnim(F)F"
      )}
   )
   private float lyfwclient$flattenSwing(float progress) {
      NoHandSwayModule sway = NoHandSwayModule.get();
      return sway != null && sway.swingRemoved() ? 0.0F : progress;
   }

   @ModifyExpressionValue(
      method = {"renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;swapAnimationScale(Lnet/minecraft/world/item/ItemStack;)F"
      )}
   )
   private float lyfwclient$flattenEquip(float scale) {
      NoHandSwayModule sway = NoHandSwayModule.get();
      return sway != null && sway.equipRemoved() ? 0.0F : scale;
   }
}
