package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.HurtCamModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GameRenderer.class},
   priority = 2000
)
public class DamageTiltLockMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      method = {"bobHurt"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$pushGuard(PoseStack matrices, float tickProgress, CallbackInfo ci) {
      if (lyfwclient$hurtCam() != null) {
         matrices.pushPose();
      }
   }

   @Inject(
      method = {"bobHurt"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$applyRealTilt(PoseStack matrices, float tickProgress, CallbackInfo ci) {
      HurtCamModule hurtCam = lyfwclient$hurtCam();
      if (hurtCam != null) {
         matrices.popPose();
         if (hurtCam.hurtCamEnabled() && this.minecraft.getCameraEntity() instanceof LivingEntity livingEntity) {
            double var11 = hurtCam.multiplier();
            if (livingEntity.isDeadOrDying()) {
               float deathProgress = Math.min(livingEntity.deathTime + tickProgress, 20.0F);
               float deathDeg = (float)((40.0F - 8000.0F / (deathProgress + 200.0F)) * var11);
               matrices.mulPose(Axis.ZP.rotationDegrees(deathDeg));
            }

            float hurt = livingEntity.hurtTime - tickProgress;
            if (!(hurt < 0.0F)) {
               hurt /= livingEntity.hurtDuration;
               hurt = Mth.sin(hurt * hurt * hurt * hurt * (float) Math.PI);
               float yaw = hurtCam.isOldStyle() ? 0.0F : livingEntity.getHurtDir();
               matrices.mulPose(Axis.YP.rotationDegrees((float)(-yaw * var11)));
               float strength = (float)(-hurt * 14.0 * (Double)this.minecraft.options.damageTiltStrength().get() * var11);
               matrices.mulPose(Axis.ZP.rotationDegrees(strength));
               matrices.mulPose(Axis.YP.rotationDegrees((float)(yaw * var11)));
            }
         }
      }
   }

   private static HurtCamModule lyfwclient$hurtCam() {
      return ModuleManager.get("Hurt Cam") instanceof HurtCamModule hurtCam && hurtCam.isEnabled() ? hurtCam : null;
   }
}
