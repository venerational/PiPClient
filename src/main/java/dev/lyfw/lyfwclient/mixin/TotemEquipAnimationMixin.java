package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TotemTweaksModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ItemInHandRenderer.class})
public class TotemEquipAnimationMixin {
   @ModifyArgs(
      method = {"applyItemArmTransform"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
      )
   )
   private void lyfwclient$freezeEquipOffset(Args args, PoseStack matrices, HumanoidArm arm, float equipProgress) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         ItemStack heldStack = arm == mc.player.getMainArm() ? mc.player.getMainHandItem() : mc.player.getOffhandItem();
         if (heldStack.is(Items.TOTEM_OF_UNDYING) && ModuleManager.get("Totem Tweaks") instanceof TotemTweaksModule tweaks && tweaks.disableEquipAnimation()) {
            args.set(0, arm == HumanoidArm.RIGHT ? 0.56F : -0.56F);
            args.set(1, -0.52F);
            args.set(2, -0.72F);
         }
      }
   }
}
