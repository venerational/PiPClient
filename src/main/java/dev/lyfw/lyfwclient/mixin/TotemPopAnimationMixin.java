package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TotemTweaksModule;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ScreenEffectRenderer.class})
public class TotemPopAnimationMixin {
   @Shadow
   private int itemActivationTicks;
   @Shadow
   private float itemActivationOffX;
   @Shadow
   private float itemActivationOffY;

   @Inject(
      method = {"displayItemActivation"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$onSetFloatingItem(CallbackInfo ci) {
      if (ModuleManager.get("Totem Tweaks") instanceof TotemTweaksModule tweaks && tweaks.isEnabled()) {
         if (tweaks.disablePopAnimation()) {
            this.itemActivationTicks = 0;
         } else {
            this.itemActivationTicks = tweaks.popAnimationSpeed();
         }

         if (tweaks.lockRotationPosition()) {
            this.itemActivationOffX = 0.0F;
            this.itemActivationOffY = 0.0F;
         }
      }
   }

   @ModifyVariable(
      method = {"renderItemActivationAnimation"},
      at = @At("STORE"),
      ordinal = 0
   )
   private int lyfwclient$modifyTicksElapsed(int i) {
      return ModuleManager.get("Totem Tweaks") instanceof TotemTweaksModule tweaks && tweaks.isEnabled()
         ? tweaks.popAnimationSpeed() - this.itemActivationTicks
         : i;
   }

   @ModifyVariable(
      method = {"renderItemActivationAnimation"},
      at = @At("STORE"),
      ordinal = 1
   )
   private float lyfwclient$modifyProgressFraction(float f) {
      return ModuleManager.get("Totem Tweaks") instanceof TotemTweaksModule tweaks && tweaks.isEnabled() ? f * 40.0F / tweaks.popAnimationSpeed() : f;
   }

   @ModifyArgs(
      method = {"renderItemActivationAnimation"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"
      )
   )
   private void lyfwclient$modifyPopScale(Args args) {
      if (ModuleManager.get("Totem Tweaks") instanceof TotemTweaksModule tweaks && tweaks.changePopSize()) {
         float scale = 0.8F * tweaks.popSize();
         args.set(0, scale);
         args.set(1, scale);
         args.set(2, scale);
      }
   }

   @WrapOperation(
      method = {"renderItemActivationAnimation"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V",
         ordinal = 0
      )}
   )
   private void lyfwclient$redirectSpinRotation(PoseStack matrices, Quaternionfc rotation, Operation<Void> original) {
      if (lyfwclient$rotationsEnabled()) {
         original.call(new Object[]{matrices, rotation});
      }
   }

   @WrapOperation(
      method = {"renderItemActivationAnimation"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V",
         ordinal = 1
      )}
   )
   private void lyfwclient$redirectWobbleXRotation(PoseStack matrices, Quaternionfc rotation, Operation<Void> original) {
      if (lyfwclient$rotationsEnabled()) {
         original.call(new Object[]{matrices, rotation});
      }
   }

   @WrapOperation(
      method = {"renderItemActivationAnimation"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V",
         ordinal = 2
      )}
   )
   private void lyfwclient$redirectWobbleZRotation(PoseStack matrices, Quaternionfc rotation, Operation<Void> original) {
      if (lyfwclient$rotationsEnabled()) {
         original.call(new Object[]{matrices, rotation});
      }
   }

   private static boolean lyfwclient$rotationsEnabled() {
      return !(ModuleManager.get("Totem Tweaks") instanceof TotemTweaksModule tweaks && tweaks.disableRotations());
   }
}
