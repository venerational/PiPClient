package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TotemParticlesModule;
import dev.lyfw.lyfwclient.particle.TotemParticleState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TotemParticle.class})
public abstract class TotemParticleMixin extends SimpleAnimatedParticle implements TotemParticleState {
   @Unique
   private float lyfwclient$rotationSpeed;
   @Unique
   private int lyfwclient$mainColor;
   @Unique
   private boolean lyfwclient$active;

   protected TotemParticleMixin(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, float upwardsAcceleration) {
      super(world, x, y, z, spriteProvider, upwardsAcceleration);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$initTotemParticle(
      ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider, CallbackInfo ci
   ) {
      if (ModuleManager.get("Totem Particles") instanceof TotemParticlesModule tp && tp.isEnabled()) {
         this.lyfwclient$active = true;
         this.hasPhysics = tp.useCollisions();
         if (tp.useMovement()) {
            this.friction = this.lyfwclient$safeRandom(tp.minVelocityMultiplier(), tp.maxVelocityMultiplier());
            if (tp.customVelocity()) {
               this.xd = this.lyfwclient$safeRandom(tp.minXVelocity(), tp.maxXVelocity());
               this.yd = this.lyfwclient$safeRandom(tp.minYVelocity(), tp.maxYVelocity());
               this.zd = this.lyfwclient$safeRandom(tp.minZVelocity(), tp.maxZVelocity());
            }

            if (tp.useGravity()) {
               this.gravity = this.lyfwclient$safeRandom(tp.minUpwardsAccel(), tp.maxUpwardsAccel());
            }

            if (tp.useRotation()) {
               this.roll = this.lyfwclient$safeRandom(tp.minStartRotation(), tp.maxStartRotation());
               this.oRoll = this.lyfwclient$safeRandom(tp.minStartRotation(), tp.maxStartRotation());
            }
         }

         if (tp.useScale()) {
            this.quadSize = this.quadSize * this.lyfwclient$safeRandom(tp.minScale(), tp.maxScale());
         }

         if (tp.useAge()) {
            this.lifetime = Math.round(this.lyfwclient$safeRandom(tp.minAge(), tp.maxAge()));
         }

         if (tp.useColor()) {
            this.lyfwclient$mainColor = tp.mainColor();
            if (tp.doRainbow() && tp.startColorRainbow()) {
               this.setColor(Mth.hsvToRgb(this.random.nextFloat(), 1.0F, 1.0F));
            } else if (tp.doStartColor()) {
               this.setColor(tp.startColor());
            } else {
               this.setColor(this.lyfwclient$mainColor);
            }

            if (tp.useAlpha()) {
               this.alpha = this.lyfwclient$safeRandom(tp.minAlpha(), tp.maxAlpha());
            }
         }

         this.lyfwclient$rotationSpeed = this.lyfwclient$safeRandom(tp.minRotationSpeed(), tp.maxRotationSpeed());
      }
   }

   @Unique
   private float lyfwclient$safeRandom(float min, float max) {
      if (min == max) {
         return min;
      } else {
         float lo = Math.min(min, max);
         float hi = Math.max(min, max);
         return lo + this.random.nextFloat() * (hi - lo);
      }
   }

   @Override
   public boolean lyfwclient$isActive() {
      return this.lyfwclient$active;
   }

   @Override
   public float lyfwclient$getRotationSpeed() {
      return this.lyfwclient$rotationSpeed;
   }

   @Override
   public void lyfwclient$setRotationSpeed(float speed) {
      this.lyfwclient$rotationSpeed = speed;
   }

   @Override
   public int lyfwclient$getMainColor() {
      return this.lyfwclient$mainColor;
   }
}
