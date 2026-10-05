package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TotemParticlesModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TrackingEmitter.class})
public abstract class TotemEmitterMixin extends NoRenderParticle {
   @Shadow
   @Final
   private Entity entity;
   @Shadow
   @Final
   private ParticleOptions particleType;
   @Shadow
   private int life;
   @Shadow
   @Final
   @Mutable
   private int lifeTime;

   protected TotemEmitterMixin(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$customEmitterTick(CallbackInfo ci) {
      if (this.particleType == ParticleTypes.TOTEM_OF_UNDYING && ModuleManager.get("Totem Particles") instanceof TotemParticlesModule tp && tp.isEnabled()) {
         Minecraft mc = Minecraft.getInstance();
         if (!tp.showOwnParticles() && this.entity == mc.player) {
            this.remove();
            ci.cancel();
         } else {
            if (tp.useEmitter()) {
               this.lifeTime = tp.emitterLifetime();
            }

            int count = Math.round(16.0F * tp.multiplier());

            for (int i = 0; i < count; i++) {
               double d = this.random.nextFloat() * 2.0F - 1.0F;
               double e = this.random.nextFloat() * 2.0F - 1.0F;
               double f = this.random.nextFloat() * 2.0F - 1.0F;
               if (!(d * d + e * e + f * f > 1.0)) {
                  double g = this.entity.getX(d / 4.0);
                  double h = this.entity.getY(0.5 + e / 4.0);
                  double j = this.entity.getZ(f / 4.0);
                  if (tp.useEmitter()) {
                     if (!tp.emitterMovesWithPlayer()) {
                        g = this.x;
                        h = this.y;
                        j = this.z;
                     }

                     h += tp.emitterYOffset();
                  }

                  this.level.addParticle(this.particleType, g, h, j, d, e + 0.2, f);
               }
            }

            this.life++;
            if (this.life >= this.lifeTime) {
               this.remove();
            }

            ci.cancel();
         }
      }
   }
}
