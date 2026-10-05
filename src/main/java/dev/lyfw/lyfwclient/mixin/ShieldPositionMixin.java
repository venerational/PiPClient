package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.ShieldPositionModule;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemInHandRenderer.class})
public class ShieldPositionMixin {
   @Inject(
      method = {"renderArmWithItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         shift = Shift.AFTER
      )}
   )
   private void lyfwclient$transformShield(
      AbstractClientPlayer player,
      float tickProgress,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      PoseStack matrices,
      SubmitNodeCollector orderedRenderCommandQueue,
      int light,
      CallbackInfo ci
   ) {
      if (item.getItem() instanceof ShieldItem && ModuleManager.get("Shield Position") instanceof ShieldPositionModule shieldPos && shieldPos.isEnabled()) {
         matrices.translate(shieldPos.offsetX(), shieldPos.offsetY(), shieldPos.offsetZ());
         matrices.mulPose(Axis.XP.rotationDegrees(shieldPos.rotationX()));
         matrices.mulPose(Axis.YP.rotationDegrees(shieldPos.rotationY()));
         matrices.mulPose(Axis.ZP.rotationDegrees(shieldPos.rotationZ()));
         matrices.scale(shieldPos.scale(), shieldPos.scale(), shieldPos.scale());
      }
   }
}
