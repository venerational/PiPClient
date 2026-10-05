package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TotemParticlesModule;
import dev.lyfw.lyfwclient.particle.TotemParticleState;
import java.awt.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SimpleAnimatedParticle.class})
public abstract class TotemParticleTickMixin extends SingleQuadParticle {
   protected TotemParticleTickMixin(ClientLevel world, double x, double y, double z, TextureAtlasSprite sprite) {
      super(world, x, y, z, sprite);
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$tickTotemParticle(CallbackInfo ci) {
      if (this instanceof TotemParticleState active
         && active.lyfwclient$isActive()
         && !this.removed
         && ModuleManager.get("Totem Particles") instanceof TotemParticlesModule tp
         && tp.isEnabled()) {
         if ((!tp.hideOnGround() || !this.onGround) && !(this.alpha <= 0.0F) && !(this.quadSize <= 0.0F)) {
            if (tp.useColor()) {
               if (tp.doRainbow() && tp.rainbowOverTime() && this.lyfwclient$rainbowActive(tp)) {
                  this.lyfwclient$setRainbowColor(tp);
               } else {
                  this.lyfwclient$updateColor(tp, active.lyfwclient$getMainColor());
               }

               if (tp.useAlpha()) {
                  if (tp.fadeOnGround() && this.onGround) {
                     this.alpha = Mth.clamp(this.alpha + tp.onGroundFade(), 0.0F, 1.0F);
                  }

                  if (this.age > this.lifetime * tp.alphaOutTime() && tp.loseAlpha()) {
                     this.alpha = Mth.clamp(this.alpha + tp.alphaOutSpeed(), 0.0F, 1.0F);
                  }
               }
            }

            if (tp.useMovement()) {
               if (tp.useGravity() && tp.gravityOverTime() && this.age > this.lifetime * tp.changeGravityAtPercent()) {
                  this.gravity = this.gravity + tp.gravityOverTimeAmount();
               }

               if (tp.useRotation() && tp.rotateOverTime()) {
                  float rotationSpeed = active.lyfwclient$getRotationSpeed();
                  if (!this.onGround || tp.rotateOnGround()) {
                     this.roll += rotationSpeed;
                  }

                  if (this.age > this.lifetime * tp.rotateAtPercent()) {
                     if (tp.smartRotation()) {
                        if (rotationSpeed != 0.0F) {
                           if (tp.rotateOverTimeAmount() > 0.0F) {
                              if (rotationSpeed > 0.0F) {
                                 rotationSpeed += tp.rotateOverTimeAmount();
                              } else {
                                 rotationSpeed -= tp.rotateOverTimeAmount();
                              }
                           } else if (tp.rotateOverTimeAmount() < 0.0F) {
                              if (rotationSpeed > 0.0F) {
                                 rotationSpeed = Mth.clamp(rotationSpeed + tp.rotateOverTimeAmount(), 0.0F, 360.0F);
                              } else {
                                 rotationSpeed = Mth.clamp(rotationSpeed - tp.rotateOverTimeAmount(), -360.0F, 0.0F);
                              }
                           }
                        }
                     } else {
                        rotationSpeed += tp.rotateOverTimeAmount();
                     }

                     active.lyfwclient$setRotationSpeed(rotationSpeed);
                  }
               }
            }

            if (tp.useScale()) {
               if (tp.scaleOnGround() && this.onGround) {
                  this.quadSize = Mth.clamp(this.quadSize + tp.onGroundScale(), 0.0F, 5.0F);
               }

               if (tp.scaleOverTime() && this.age > this.lifetime * tp.scaleAtPercent()) {
                  this.quadSize = Mth.clamp(this.quadSize + tp.scaleAmount(), 0.0F, 5.0F);
               }
            }
         } else {
            this.remove();
         }
      }
   }

   private void lyfwclient$updateColor(TotemParticlesModule tp, int mainColor) {
      if (this.age > this.lifetime * tp.fadeToTime() && this.age < this.lifetime * 0.5F && tp.doStartColor()) {
         this.rCol = Mth.lerp(tp.fadeToSpeed(), this.rCol, (mainColor >> 16 & 0xFF) / 255.0F);
         this.gCol = Mth.lerp(tp.fadeToSpeed(), this.gCol, (mainColor >> 8 & 0xFF) / 255.0F);
         this.bCol = Mth.lerp(tp.fadeToSpeed(), this.bCol, (mainColor & 0xFF) / 255.0F);
      } else if (this.age > this.lifetime * tp.fadeOutTime() && tp.doOutColor() && this.age > this.lifetime * 0.5F) {
         int target = tp.outTargetColor();
         this.rCol = Mth.lerp(tp.fadeOutSpeed(), this.rCol, (target >> 16 & 0xFF) / 255.0F);
         this.gCol = Mth.lerp(tp.fadeOutSpeed(), this.gCol, (target >> 8 & 0xFF) / 255.0F);
         this.bCol = Mth.lerp(tp.fadeOutSpeed(), this.bCol, (target & 0xFF) / 255.0F);
      }
   }

   private boolean lyfwclient$rainbowActive(TotemParticlesModule tp) {
      return switch (tp.rainbowMode()) {
         case END -> this.age > this.lifetime * tp.fadeOutTime() && tp.doOutColor();
         case START -> this.age < this.lifetime * tp.fadeToTime() && tp.doStartColor();
         case MAIN -> (!tp.doStartColor() || this.age > this.lifetime * tp.fadeToTime()) && this.age < this.lifetime * tp.fadeOutTime();
         case UNTIL_END -> this.age < this.lifetime * tp.fadeOutTime();
         case AFTER_START -> !tp.doStartColor() || this.age > this.lifetime * tp.fadeToTime();
         case EXCLUDING_MAIN -> this.age > this.lifetime * tp.fadeOutTime() || this.age < this.lifetime * tp.fadeToTime();
         case ALL -> true;
      };
   }

   private void lyfwclient$setColor(int rgbHex) {
      this.setColor((rgbHex >> 16 & 0xFF) / 255.0F, (rgbHex >> 8 & 0xFF) / 255.0F, (rgbHex & 0xFF) / 255.0F);
   }

   private void lyfwclient$setRainbowColor(TotemParticlesModule tp) {
      if (tp.syncRainbow()) {
         this.lyfwclient$setColor(lyfwclient$rainbowColorAt(0, tp));
      } else {
         float[] hsb = new float[3];
         Color.RGBtoHSB(Math.round(this.rCol * 255.0F), Math.round(this.gCol * 255.0F), Math.round(this.bCol * 255.0F), hsb);
         hsb[0] += tp.rainbowSpeed() / 100.0F;
         this.lyfwclient$setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB());
      }
   }

   private static int lyfwclient$rainbowColorAt(int delayMillis, TotemParticlesModule tp) {
      double percent = -((System.currentTimeMillis() + delayMillis) % 10000L) / 10000.0 * tp.rainbowSpeed();
      double offset = Math.PI * 2.0 / 3.0;
      double pos = percent * (Math.PI * 2);
      int r = (int)(Math.sin(pos) * 127.0 + 128.0);
      int g = (int)(Math.sin(pos + offset) * 127.0 + 128.0);
      int b = (int)(Math.sin(pos + offset * 2.0) * 127.0 + 128.0);
      return new Color(r, g, b, 255).getRGB();
   }

   @Inject(
      method = {"getLightColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$overrideBrightness(float tint, CallbackInfoReturnable<Integer> cir) {
      if (this instanceof TotemParticleState active
         && active.lyfwclient$isActive()
         && ModuleManager.get("Totem Particles") instanceof TotemParticlesModule tp
         && tp.isEnabled()) {
         if (tp.lightLevel() != -1) {
            cir.setReturnValue(tp.lightLevel());
         } else {
            BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
            cir.setReturnValue(this.level.hasChunkAt(blockPos) ? LevelRenderer.getLightColor(this.level, blockPos) : 0);
         }
      }
   }
}
