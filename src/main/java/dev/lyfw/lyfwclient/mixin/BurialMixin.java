package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.BurialStateMarker;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.KillEffectsModule;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public class BurialMixin {
   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$captureBurialState(LivingEntity entity, LivingEntityRenderState state, float tickDelta, CallbackInfo ci) {
      float sink = 0.0F;
      float scale = 1.0F;
      if (entity instanceof Player player && player.isDeadOrDying() && ModuleManager.get("Kill Effects") instanceof KillEffectsModule killEffects) {
         if (killEffects.isKillEffectDeath(player.getUUID())) {
            state.deathTime = 0.0F;
         }

         sink = killEffects.getBurialSink(player.getUUID());
         scale = killEffects.getBurialScale(player.getUUID());
      }

      BurialStateMarker marker = (BurialStateMarker)state;
      marker.lyfwclient$setBurialSink(sink);
      marker.lyfwclient$setBurialScale(scale);
   }

   @Inject(
      method = {"setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$applyBurialSink(LivingEntityRenderState state, PoseStack matrices, float bodyYaw, float baseHeight, CallbackInfo ci) {
      BurialStateMarker marker = (BurialStateMarker)state;
      float sink = marker.lyfwclient$getBurialSink();
      if (sink > 0.0F) {
         matrices.translate(0.0F, -sink, 0.0F);
      }

      float scale = marker.lyfwclient$getBurialScale();
      if (scale < 1.0F) {
         matrices.scale(scale, scale, scale);
      }
   }
}
