package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.Axis;
import dev.lyfw.lyfwclient.module.modules.DeathAnimationModule;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LivingEntityRenderer.class})
public class DeathAnimationMixin {
   @WrapOperation(
      method = {"setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;",
         ordinal = 1
      )}
   )
   private Quaternionf lyfwclient$timeDeathFall(
      Axis axis, float degrees, Operation<Quaternionf> original, @Local(argsOnly = true) LivingEntityRenderState state
   ) {
      DeathAnimationModule death = DeathAnimationModule.get();
      if (death != null && death.covers(state instanceof AvatarRenderState)) {
         float vanilla = Math.min(1.0F, (float)Math.sqrt(Math.max(0.0F, (state.deathTime - 1.0F) / 20.0F * 1.6F)));
         float lying = vanilla > 1.0E-4F ? degrees / vanilla : 90.0F;
         return (Quaternionf)original.call(new Object[]{axis, death.fall(state.deathTime) * lying});
      } else {
         return (Quaternionf)original.call(new Object[]{axis, degrees});
      }
   }
}
