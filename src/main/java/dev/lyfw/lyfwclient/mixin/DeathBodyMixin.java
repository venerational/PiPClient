package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.DeathAnimationModule;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public abstract class DeathBodyMixin {
   @Unique
   private int lyfwclient$adding;

   @Inject(
      method = {"addEntity"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$startAdding(Entity entity, CallbackInfo ci) {
      this.lyfwclient$adding++;
   }

   @Inject(
      method = {"addEntity"},
      at = {@At("RETURN")}
   )
   private void lyfwclient$stopAdding(Entity entity, CallbackInfo ci) {
      this.lyfwclient$adding--;
   }

   @Inject(
      method = {"removeEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$holdBody(int entityId, RemovalReason reason, CallbackInfo ci) {
      DeathAnimationModule death = DeathAnimationModule.get();
      if (death != null && this.lyfwclient$adding == 0 && death.shouldHold(((ClientLevel)(Object)this).getEntity(entityId))) {
         ci.cancel();
      }
   }
}
