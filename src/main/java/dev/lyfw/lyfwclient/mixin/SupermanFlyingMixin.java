package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.SupermanFlyingModule;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerModel.class})
public class SupermanFlyingMixin {
   private static final float REACH = 4.5F;

   @Inject(
      method = {"setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$supermanArms(AvatarRenderState state, CallbackInfo ci) {
      SupermanFlyingModule module = SupermanFlyingModule.get();
      if (module != null && module.isEnabled() && state.isFallFlying) {
         float progress = state.fallFlyingScale();
         HumanoidModel<?> model = (HumanoidModel<?>)(Object)this;
         lyfwclient$reach(model.rightArm, progress);
         lyfwclient$reach(model.leftArm, progress);
      }
   }

   private static void lyfwclient$reach(ModelPart arm, float progress) {
      arm.xRot = Mth.lerp(progress, arm.xRot, (float) -Math.PI);
      arm.yRot = Mth.lerp(progress, arm.yRot, 0.0F);
      arm.zRot = Mth.lerp(progress, arm.zRot, 0.0F);
      arm.y -= 4.5F * progress;
   }
}
