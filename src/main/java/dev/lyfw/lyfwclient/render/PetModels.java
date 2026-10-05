package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

final class PetModels {
   static final float GROUND = 1.501F;
   private static final float PX = 0.0625F;
   private static final int BLACK = -14804455;
   private static final int WHITE = -1;
   private static final int PINK = -876368;
   private static final Map<CosmeticsModule.Pet, PetModels.Trick> TRICKS = new EnumMap<>(CosmeticsModule.Pet.class);
   private static final float TRICK_EVERY = 160.0F;
   private static final float TRICK_LENGTH = 40.0F;
   private static final float PI = (float) Math.PI;

   private PetModels() {
   }

   static PetModels.Trick trick(CosmeticsModule.Pet kind) {
      return TRICKS.getOrDefault(kind, PetModels.Trick.NOD);
   }

   static int trickCount() {
      return TRICKS.size();
   }

   static float trickProgress(CosmeticsModule.Pet kind, float age) {
      float phase = (age + kind.ordinal() * 23.0F) % 160.0F;
      return phase < 40.0F ? phase / 40.0F : -1.0F;
   }

   static float midTrickSeconds(CosmeticsModule.Pet kind) {
      float start = (160.0F - kind.ordinal() * 23.0F % 160.0F) % 160.0F;
      return (start + 20.0F) / 20.0F;
   }

   private static float part(float t, float from, float to) {
      float x = Mth.clamp((t - from) / (to - from), 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static PetModels.Effect trick(PoseStack matrices, CosmeticsModule.Pet kind, float age) {
      float t = tricksOn() ? trickProgress(kind, age) : -1.0F;
      if (t < 0.0F) {
         return PetModels.Effect.NONE;
      } else {
         float arc = Mth.sin(t * (float) Math.PI);
         float h = height(kind) * 0.0625F;
         float centre = 1.501F - h * 0.5F;
         switch (trick(kind)) {
            case BARK:
               float bow = part(t, 0.0F, 0.2F) * (1.0F - part(t, 0.8F, 1.0F));
               float yap = t > 0.2F && t < 0.85F ? Mth.abs(Mth.sin((t - 0.2F) / 0.65F * (float) Math.PI * 3.0F)) : 0.0F;
               about(matrices, 1.501F, Axis.XP.rotationDegrees(10.0F * bow - 12.0F * yap));
               return new PetModels.Effect(yap, 0.0F);
            case ROAR:
               float inhale = part(t, 0.0F, 0.2F) * (1.0F - part(t, 0.2F, 0.32F));
               float roar = part(t, 0.2F, 0.32F) * (1.0F - part(t, 0.82F, 1.0F));
               float sweep = t > 0.32F && t < 0.82F ? Mth.sin((t - 0.32F) / 0.5F * (float) Math.PI * 1.5F) * 14.0F : 0.0F;
               matrices.translate(0.0F, 0.0F, inhale * 0.12F - roar * 0.12F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(-14.0F * inhale + 9.0F * roar));
               about(matrices, centre, Axis.YP.rotationDegrees(sweep * roar));
               about(matrices, centre, Axis.ZP.rotationDegrees(Mth.sin(t * (float) Math.PI * 40.0F) * 1.2F * roar));
               return new PetModels.Effect(Math.max(inhale * 0.15F, roar), 0.0F);
            case ROLL:
               float settle = part(t, 0.0F, 0.15F) * (1.0F - part(t, 0.15F, 0.3F)) + part(t, 0.85F, 0.93F) * (1.0F - part(t, 0.93F, 1.0F));
               float angle = 180.0F * part(t, 0.15F, 0.38F) + 180.0F * part(t, 0.7F, 0.88F);
               float wriggle = t > 0.38F && t < 0.7F ? Mth.sin((t - 0.38F) / 0.32F * (float) Math.PI * 4.0F) * 22.0F : 0.0F;
               matrices.translate(0.0F, 1.501F, 0.0F);
               matrices.scale(1.0F + settle * 0.08F, 1.0F - settle * 0.2F, 1.0F);
               matrices.translate(0.0F, -1.501F, 0.0F);
               about(matrices, centre, Axis.ZP.rotationDegrees(angle + wriggle));
               return new PetModels.Effect(0.0F, t > 0.2F && t < 0.85F ? 1.0F : 0.0F);
            case SPIN:
               about(matrices, 1.501F, Axis.YP.rotationDegrees(720.0F * part(t, 0.05F, 0.95F)));
               about(matrices, centre, Axis.ZP.rotationDegrees(-14.0F * arc));
               return new PetModels.Effect(0.0F, 1.0F);
            case JUMP:
               float crouch = part(t, 0.0F, 0.22F) * (1.0F - part(t, 0.22F, 0.32F)) + part(t, 0.8F, 0.9F) * (1.0F - part(t, 0.9F, 1.0F));
               float air = Mth.clamp((t - 0.28F) / 0.55F, 0.0F, 1.0F);
               float lift = Mth.sin(air * (float) Math.PI);
               matrices.translate(0.0F, -lift * 0.55F, 0.0F);
               matrices.translate(0.0F, 1.501F, 0.0F);
               matrices.scale(1.0F + crouch * 0.08F, 1.0F - crouch * 0.22F, 1.0F);
               matrices.translate(0.0F, -1.501F, 0.0F);
               if (air > 0.0F && air < 1.0F) {
                  about(matrices, centre, Axis.XP.rotationDegrees(-22.0F * Mth.cos(air * (float) Math.PI)));
               }
               break;
            case FLIP:
               matrices.translate(0.0F, -arc * 0.6F, 0.0F);
               about(matrices, centre, Axis.XP.rotationDegrees(-360.0F * part(t, 0.15F, 0.85F)));
               break;
            case BINKY:
               matrices.translate(0.0F, -arc * 0.5F, 0.0F);
               about(matrices, centre, Axis.YP.rotationDegrees(Mth.sin(t * (float) Math.PI * 2.0F) * 40.0F));
               about(matrices, centre, Axis.ZP.rotationDegrees(Mth.sin(t * (float) Math.PI * 2.0F) * 20.0F));
               return new PetModels.Effect(0.0F, arc);
            case BREACH:
               matrices.translate(0.0F, -arc * 0.7F, -arc * 0.2F);
               about(matrices, centre, Axis.XP.rotationDegrees(-40.0F * Mth.cos(t * (float) Math.PI)));
               break;
            case HANDSTAND: {
               float up = part(t, 0.0F, 0.25F) * (1.0F - part(t, 0.75F, 1.0F));
               matrices.translate(0.0F, 0.0F, -0.15F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(70.0F * up));
               matrices.translate(0.0F, 0.0F, 0.15F);
               break;
            }
            case REAR: {
               float up = part(t, 0.0F, 0.3F) * (1.0F - part(t, 0.72F, 1.0F));
               matrices.translate(0.0F, 0.0F, 0.2F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(-42.0F * up));
               matrices.translate(0.0F, 0.0F, -0.2F);
               return new PetModels.Effect(up * 0.5F, up);
            }
            case SHAKE:
               float shake = Mth.sin(t * (float) Math.PI * 14.0F) * (1.0F - t);
               about(matrices, centre, Axis.ZP.rotationDegrees(24.0F * shake));
               about(matrices, centre, Axis.YP.rotationDegrees(8.0F * shake));
               break;
            case PECK:
               float peck = Mth.abs(Mth.sin(t * (float) Math.PI * 3.0F));
               matrices.translate(0.0F, -Mth.abs(Mth.sin(t * (float) Math.PI * 3.0F + (float) (Math.PI / 2))) * 0.04F, 0.0F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(36.0F * peck));
               break;
            case FLAP:
               matrices.translate(0.0F, -arc * 0.4F, 0.0F);
               about(matrices, centre, Axis.XP.rotationDegrees(-10.0F * arc));
               about(matrices, centre, Axis.ZP.rotationDegrees(Mth.sin(t * (float) Math.PI * 10.0F) * 6.0F));
               break;
            case CIRCLE: {
               float a = (float) (Math.PI * 2) * part(t, 0.0F, 1.0F);
               matrices.translate(Mth.sin(a) * 0.45F, 0.0F, (Mth.cos(a) - 1.0F) * 0.45F);
               about(matrices, 1.501F, Axis.YP.rotationDegrees(a * 180.0F / (float) Math.PI));
               about(matrices, centre, Axis.ZP.rotationDegrees(-22.0F * arc));
               return new PetModels.Effect(0.0F, 1.0F);
            }
            case PUFF:
               float grow = 1.0F + 0.4F * part(t, 0.0F, 0.2F) * (1.0F - part(t, 0.75F, 1.0F));
               matrices.translate(0.0F, centre, 0.0F);
               matrices.scale(grow, grow, grow);
               matrices.translate(0.0F, -centre, 0.0F);
               break;
            case SQUASH:
               float tuck = part(t, 0.0F, 0.3F) * (1.0F - part(t, 0.7F, 1.0F));
               matrices.translate(0.0F, 1.501F, 0.0F);
               matrices.scale(1.0F + 0.12F * tuck, 1.0F - 0.28F * tuck, 1.0F + 0.12F * tuck);
               matrices.translate(0.0F, -1.501F, 0.0F);
               break;
            case STRIKE:
               float coil = part(t, 0.0F, 0.35F) * (1.0F - part(t, 0.35F, 0.5F));
               float strike = part(t, 0.35F, 0.5F) * (1.0F - part(t, 0.7F, 1.0F));
               matrices.translate(0.0F, 0.0F, coil * 0.15F - strike * 0.4F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(-8.0F * coil + 14.0F * strike));
               return new PetModels.Effect(strike, strike);
            case SIDESTEP:
               matrices.translate(Mth.sin(t * (float) Math.PI * 2.0F) * 0.35F, 0.0F, 0.0F);
               return new PetModels.Effect(0.0F, 1.0F);
            case NOD:
               float graze = Mth.abs(Mth.sin(part(t, 0.0F, 0.8F) * (float) Math.PI * 2.0F));
               about(matrices, 1.501F, Axis.XP.rotationDegrees(20.0F * graze - 6.0F * part(t, 0.8F, 1.0F) * (1.0F - t) * 5.0F));
               break;
            case HOWL: {
               float up = part(t, 0.0F, 0.25F) * (1.0F - part(t, 0.8F, 1.0F));
               matrices.translate(0.0F, 0.0F, 0.25F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(-32.0F * up));
               matrices.translate(0.0F, 0.0F, -0.25F);
               return new PetModels.Effect(up * (0.55F + 0.1F * Mth.sin(t * (float) Math.PI * 6.0F)), 0.0F);
            }
            case YAWN: {
               float open = part(t, 0.1F, 0.45F) * (1.0F - part(t, 0.7F, 0.95F));
               about(matrices, 1.501F, Axis.XP.rotationDegrees(-10.0F * open));
               return new PetModels.Effect(open, 0.0F);
            }
            case SNAP: {
               float cycle = t * 2.0F % 1.0F;
               float open = cycle < 0.75F ? part(cycle, 0.0F, 0.75F) : 1.0F - part(cycle, 0.75F, 0.85F);
               float lunge = cycle > 0.75F && cycle < 0.95F ? Mth.sin((cycle - 0.75F) / 0.2F * (float) Math.PI) : 0.0F;
               matrices.translate(0.0F, 0.0F, -lunge * 0.15F);
               about(matrices, 1.501F, Axis.XP.rotationDegrees(-6.0F * open));
               return new PetModels.Effect(open, 0.0F);
            }
            case SLIDE: {
               float flop = part(t, 0.0F, 0.15F) * (1.0F - part(t, 0.85F, 1.0F));
               float a = (float) (Math.PI * 2) * part(t, 0.12F, 0.88F);
               matrices.translate(Mth.sin(a) * 0.5F, 0.0F, (Mth.cos(a) - 1.0F) * 0.5F);
               about(matrices, 1.501F, Axis.YP.rotationDegrees(a * 180.0F / (float) Math.PI));
               about(matrices, 1.501F - h * 0.22F, Axis.XP.rotationDegrees(80.0F * flop));
            }
         }

         return PetModels.Effect.NONE;
      }
   }

   private static void mud(PoseStack matrices, SubmitNodeCollector queue, int light, Identifier white, float t) {
      if (!(t < 0.0F)) {
         float spread = part(t, 0.0F, 0.15F) * (1.0F - part(t, 0.9F, 1.0F));
         queue.submitCustomGeometry(matrices, RenderTypes.entityCutoutNoCull(white), (entry, vc) -> {
            PetModels.Kit k = new PetModels.Kit(new HeadwearModels(entry, vc, light), 24.016F);
            int mud = -10601438;
            int dark = -11915750;
            int wet = -8366545;
            k.box(-5.5F * spread, 5.5F * spread, 0.06F, 0.16F, -3.2F * spread, 3.2F * spread, mud);
            k.box(-4.5F * spread, 4.5F * spread, 0.06F, 0.17F, -4.6F * spread, 4.6F * spread, mud);
            k.box(-3.0F * spread, 3.0F * spread, 0.06F, 0.18F, -5.4F * spread, 5.4F * spread, dark);
            k.box(-1.8F * spread, -0.6F * spread, 0.18F, 0.22F, -2.4F * spread, -1.2F * spread, wet);
            k.box(1.2F * spread, 2.6F * spread, 0.18F, 0.22F, 1.0F * spread, 2.0F * spread, wet);

            for (float[] splash : new float[][]{{0.3F, 0.55F}, {0.66F, 0.9F}}) {
               float s = Mth.clamp((t - splash[0]) / (splash[1] - splash[0]), 0.0F, 1.0F);
               if (!(s <= 0.0F) && !(s >= 1.0F)) {
                  for (int i = 0; i < 9; i++) {
                     float a = i * 0.698F + splash[0] * 5.0F;
                     float r = 2.5F + 5.0F * s * (0.7F + i % 3 * 0.15F);
                     float up = Mth.sin(s * (float) Math.PI) * (3.0F + i % 4 * 1.2F);
                     k.cube(Mth.cos(a) * r, 0.4F + up, Mth.sin(a) * r, 0.35F * (1.0F - s * 0.5F), i % 2 == 0 ? mud : dark);
                  }
               }
            }
         });
      }
   }

   private static boolean tricksOn() {
      CosmeticsModule cosmetics = CosmeticsModule.get();
      return cosmetics == null || cosmetics.petTricksSetting().get();
   }

   private static void about(PoseStack matrices, float y, Quaternionf rotation) {
      matrices.translate(0.0F, y, 0.0F);
      matrices.mulPose(rotation);
      matrices.translate(0.0F, -y, 0.0F);
   }

   static void submit(
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      CosmeticLoadout.Piece<CosmeticsModule.Pet> piece,
      float limb,
      float amplitude,
      float age,
      Identifier white
   ) {
      CosmeticsModule.Pet kind = piece.kind();
      float s = piece.size();
      PetModels.Pal pal = PetModels.Pal.of(natural(kind), piece.main());
      matrices.pushPose();
      matrices.translate(0.0F, 1.501F, 0.0F);
      matrices.mulPose(Axis.YP.rotationDegrees(piece.yAngle()));
      matrices.mulPose(Axis.XP.rotationDegrees(piece.xAngle()));
      matrices.scale(s, s, s);
      matrices.translate(0.0F, -1.501F, 0.0F);
      if (kind == CosmeticsModule.Pet.PIGLET && piece.accent() == CosmeticsModule.PetSpot.GROUND.ordinal()) {
         mud(matrices, queue, light, white, tricksOn() ? trickProgress(kind, age) : -1.0F);
      }

      PetModels.Effect fx = trick(matrices, kind, age);
      float mouth = fx.mouth();
      float legLimb = fx.legs() > 0.0F ? age * 1.1F : limb;
      float legAmp = Math.max(amplitude, fx.legs());
      if (kind == CosmeticsModule.Pet.HAMSTER_BALL) {
         PetModels.HamsterBall.submit(matrices, queue, light, white, legLimb, legAmp, pal);
      } else {
         float hop = 0.0F;
         if (kind == CosmeticsModule.Pet.BUNNY || kind == CosmeticsModule.Pet.FROG || kind == CosmeticsModule.Pet.KANGAROO) {
            hop = Mth.abs(Mth.sin(legLimb * 0.3331F)) * Math.min(1.0F, legAmp * 1.6F);
            matrices.translate(0.0F, -hop * 0.3F, 0.0F);
         }

         float bounce = hop;
         queue.submitCustomGeometry(matrices, RenderTypes.entityCutoutNoCull(white), (entry, vc) -> {
            PetModels.Kit kit = new PetModels.Kit(new HeadwearModels(entry, vc, light), 24.016F);
            PetModels.Motion motion = new PetModels.Motion(legLimb, legAmp, age, bounce, mouth);
            switch (kind) {
               case DOG:
                  puppy(kit, motion, pal);
                  break;
               case CAT:
                  kitten(kit, motion, pal);
                  break;
               case BABY_GOAT:
                  kid(kit, motion, pal);
                  break;
               case BABY_SHEEP:
                  lamb(kit, motion, pal);
                  break;
               case BUNNY:
                  bunny(kit, motion, pal);
                  break;
               default:
                  if (!PetSpecies.draw(kind, kit, motion, pal)) {
                     PetCritters.draw(kind, kit, motion, pal);
                  }
            }
         });
      }

      matrices.popPose();
   }

   private static int relight(int color, int base, int part, float saturation) {
      float[] c = hsl(color);
      float shift = hsl(part)[2] - hsl(base)[2];
      float l = c[2] + shift;
      if (l < 0.05F || l > 0.95F) {
         l = c[2] - shift * 0.6F;
      }

      return rgb(c[0], c[1] * saturation, Mth.clamp(l, 0.04F, 0.96F));
   }

   private static float[] hsl(int argb) {
      float r = (argb >> 16 & 0xFF) / 255.0F;
      float g = (argb >> 8 & 0xFF) / 255.0F;
      float b = (argb & 0xFF) / 255.0F;
      float max = Math.max(r, Math.max(g, b));
      float min = Math.min(r, Math.min(g, b));
      float l = (max + min) / 2.0F;
      float d = max - min;
      if (d < 1.0E-4F) {
         return new float[]{0.0F, 0.0F, l};
      } else {
         float s = d / (1.0F - Math.abs(2.0F * l - 1.0F));
         float h = max == r ? ((g - b) / d + 6.0F) % 6.0F : (max == g ? (b - r) / d + 2.0F : (r - g) / d + 4.0F);
         return new float[]{h / 6.0F, Math.min(1.0F, s), l};
      }
   }

   private static int rgb(float h, float s, float l) {
      float c = (1.0F - Math.abs(2.0F * l - 1.0F)) * s;
      float hh = h * 6.0F;
      float x = c * (1.0F - Math.abs(hh % 2.0F - 1.0F));
      float r = hh < 1.0F ? c : (hh < 2.0F ? x : (hh < 4.0F ? 0.0F : (hh < 5.0F ? x : c)));
      float g = hh < 1.0F ? x : (hh < 3.0F ? c : (hh < 4.0F ? x : 0.0F));
      float b = hh < 2.0F ? 0.0F : (hh < 3.0F ? x : (hh < 5.0F ? c : x));
      float m = l - c / 2.0F;
      return 0xFF000000 | Math.round((r + m) * 255.0F) << 16 | Math.round((g + m) * 255.0F) << 8 | Math.round((b + m) * 255.0F);
   }

   static int[] natural(CosmeticsModule.Pet kind) {
      return switch (kind) {
         case DOG -> new int[]{-1856917, -4688065, -3878};
         case CAT -> new int[]{-875428, -3377870, -267309};
         case BABY_GOAT -> new int[]{-725020, -5935547, -1450296};
         case BABY_SHEEP -> new int[]{-197900, -1383466, -992826};
         case BUNNY -> new int[]{-2967652, -5074314, -132104};
         case HAMSTER_BALL -> new int[]{-2516398, -4883910, -530485};
         default -> {
            int[] species = PetSpecies.natural(kind);
            yield species != null ? species : PetCritters.natural(kind);
         }
      };
   }

   static float height(CosmeticsModule.Pet kind) {
      float species = PetSpecies.height(kind);
      return species > 0.0F ? species : 10.0F;
   }

   private static void puppy(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      int fur = p.main();
      int ear = p.second();
      int cream = p.accent();
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() + br;
      k.leg(-1.3F, 3.6F, -2.1F, s, 0.75F, fur, cream);
      k.leg(1.3F, 3.6F, -2.1F, -s, 0.75F, fur, cream);
      k.leg(-1.3F, 3.6F, 2.3F, -s, 0.75F, fur, cream);
      k.leg(1.3F, 3.6F, 2.3F, s, 0.75F, fur, cream);
      k.box(-2.2F, 2.2F, 3.0F + br, 6.8F + br, -3.2F, 3.4F, fur);
      k.box(-1.7F, 1.7F, 3.3F + br, 6.2F + br, -3.45F, -2.6F, cream);
      k.box(-1.5F, 1.5F, 2.8F + br, 3.1F + br, -2.4F, 2.6F, cream);
      float wag = Mth.sin(mo.age() * (0.7F + mo.amplitude() * 0.5F)) * 1.6F;
      k.chain(new float[][]{{0.0F, 6.2F + br, 3.3F}, {wag * 0.5F, 7.6F + br, 4.6F}, {wag, 9.0F + br, 5.0F}}, new float[]{0.7F, 0.6F, 0.45F}, fur, cream);
      k.box(-2.35F, 2.35F, 5.3F + hb, 6.3F + hb, -3.4F, -2.5F, -2608838);
      k.box(-0.45F, 0.45F, 4.4F + hb, 5.3F + hb, -3.6F, -3.35F, -669620);
      k.box(-2.6F, 2.6F, 5.9F + hb, 10.5F + hb, -7.3F, -2.7F, fur);
      k.box(-1.4F, 1.4F, 5.9F + hb, 7.9F + hb, -8.9F, -7.1F, cream);
      k.box(-0.6F, 0.6F, 7.3F + hb, 8.1F + hb, -9.15F, -8.8F, -14804455);
      k.box(-0.55F, 0.55F, 5.1F + hb, 6.1F + hb, -8.4F, -7.6F, -876368);
      if (mo.mouth() > 0.05F) {
         float open = mo.mouth() * 1.2F;
         k.box(-1.2F, 1.2F, 5.9F + hb - open, 6.0F + hb, -8.7F, -7.2F, -11920350);
         k.box(-1.3F, 1.3F, 5.4F + hb - open, 5.9F + hb - open, -8.6F, -7.1F, cream);
      }

      k.box(-0.5F, 0.5F, 8.1F + hb, 10.55F + hb, -7.4F, -7.25F, cream);
      k.eye(-1.95F, -0.95F, 8.3F + hb, 9.4F + hb, -7.3F, -14804455);
      k.eye(0.95F, 1.95F, 8.3F + hb, 9.4F + hb, -7.3F, -14804455);
      float flop = mo.bob() * 0.8F;
      k.flat(2.55F, 10.0F + hb, -5.2F, 3.3F, 7.1F + hb - flop, -4.9F, 1.0F, 0.35F, HeadwearModels.AXIS_Z, ear);
      k.flat(-2.55F, 10.0F + hb, -5.2F, -3.3F, 7.1F + hb - flop, -4.9F, 1.0F, 0.35F, HeadwearModels.AXIS_Z, ear);
   }

   private static void kitten(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      int fur = p.main();
      int stripe = p.second();
      int cream = p.accent();
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() + br;
      k.leg(-1.1F, 3.3F, -2.5F, s, 0.6F, fur, cream);
      k.leg(1.1F, 3.3F, -2.5F, -s, 0.6F, fur, cream);
      k.leg(-1.1F, 3.3F, 2.6F, -s, 0.6F, fur, cream);
      k.leg(1.1F, 3.3F, 2.6F, s, 0.6F, fur, cream);
      k.box(-1.8F, 1.8F, 3.0F + br, 6.2F + br, -3.6F, 3.7F, fur);

      for (float z : new float[]{-2.4F, -0.8F, 0.8F, 2.4F}) {
         k.box(-1.85F, 1.85F, 5.2F + br, 6.25F + br, z - 0.35F, z + 0.35F, stripe);
      }

      k.box(-1.4F, 1.4F, 2.8F + br, 3.1F + br, -3.0F, 3.0F, cream);
      float sway = Mth.sin(mo.age() * 0.09F) * 1.3F + s * 0.8F;
      k.chain(
         new float[][]{{0.0F, 5.4F + br, 3.7F}, {sway * 0.3F, 6.8F + br, 5.3F}, {sway * 0.7F, 8.8F + br, 6.0F}, {sway, 10.6F + br, 5.5F}},
         new float[]{0.6F, 0.55F, 0.5F, 0.45F},
         fur,
         stripe
      );
      k.box(-2.5F, 2.5F, 5.0F + hb, 9.4F + hb, -7.4F, -3.1F, fur);
      k.box(-1.4F, 1.4F, 5.1F + hb, 6.9F + hb, -7.75F, -7.3F, cream);
      k.box(-0.45F, 0.45F, 6.6F + hb, 7.1F + hb, -7.85F, -7.7F, -876368);
      k.eye(-2.0F, -0.8F, 7.2F + hb, 8.5F + hb, -7.4F, -8990891);
      k.eye(0.8F, 2.0F, 7.2F + hb, 8.5F + hb, -7.4F, -8990891);
      k.box(-1.5F, -1.3F, 7.3F + hb, 8.2F + hb, -7.62F, -7.5F, -14804455);
      k.box(1.3F, 1.5F, 7.3F + hb, 8.2F + hb, -7.62F, -7.5F, -14804455);
      k.box(-0.25F, 0.25F, 8.5F + hb, 9.45F + hb, -7.46F, -7.36F, stripe);
      k.pair(0.9F, 1.3F, 8.8F + hb, 9.45F + hb, -7.46F, -7.36F, stripe);
      k.taper(0.7F, 2.4F, -6.0F, -4.4F, 9.3F + hb, 1.7F, 2.0F, -5.35F, -5.05F, 11.6F + hb, fur);
      k.taper(-2.4F, -0.7F, -6.0F, -4.4F, 9.3F + hb, -2.0F, -1.7F, -5.35F, -5.05F, 11.6F + hb, fur);
      k.taper(1.0F, 2.1F, -6.15F, -5.95F, 9.5F + hb, 1.65F, 1.9F, -6.1F, -6.0F, 11.0F + hb, -876368);
      k.taper(-2.1F, -1.0F, -6.15F, -5.95F, 9.5F + hb, -1.9F, -1.65F, -6.1F, -6.0F, 11.0F + hb, -876368);

      for (int side = -1; side <= 1; side += 2) {
         k.beam(side * 1.3F, 6.3F + hb, -7.7F, side * 3.8F, 6.8F + hb, -8.0F, 0.07F, -1);
         k.beam(side * 1.3F, 6.0F + hb, -7.7F, side * 3.7F, 5.6F + hb, -7.9F, 0.07F, -1);
      }
   }

   private static void kid(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      int coat = p.main();
      int brown = p.second();
      int hoof = -11912397;
      int cream = p.accent();
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() + br;
      k.leg(-1.2F, 4.0F, -2.2F, s, 0.5F, coat, hoof);
      k.leg(1.2F, 4.0F, -2.2F, -s, 0.5F, coat, hoof);
      k.leg(-1.2F, 4.0F, 2.2F, -s, 0.5F, coat, hoof);
      k.leg(1.2F, 4.0F, 2.2F, s, 0.5F, coat, hoof);
      k.box(-2.0F, 2.0F, 3.6F + br, 6.9F + br, -3.1F, 3.1F, coat);
      k.box(-2.05F, 2.05F, 5.9F + br, 6.95F + br, -1.4F, 1.8F, brown);
      float flick = Mth.sin(mo.age() * 0.45F) * 0.5F;
      k.beam(0.0F, 6.5F + br, 3.1F, 0.0F, 8.0F + br + flick, 3.9F, 0.45F, brown);
      k.box(-1.9F, 1.9F, 6.3F + hb, 10.0F + hb, -6.4F, -2.7F, coat);
      k.box(-1.2F, 1.2F, 6.4F + hb, 8.2F + hb, -7.5F, -6.2F, cream);
      k.box(-0.55F, 0.55F, 7.7F + hb, 8.2F + hb, -7.6F, -7.45F, -876368);
      k.box(-0.4F, 0.4F, 5.3F + hb, 6.5F + hb, -7.3F, -6.7F, cream);
      k.eye(-1.75F, -0.85F, 8.4F + hb, 9.35F + hb, -6.4F, -2844100);
      k.eye(0.85F, 1.75F, 8.4F + hb, 9.35F + hb, -6.4F, -2844100);
      k.pair(0.9F, 1.7F, 8.75F + hb, 8.95F + hb, -6.62F, -6.5F, -14804455);
      k.box(-0.7F, 0.7F, 9.0F + hb, 10.05F + hb, -6.48F, -6.38F, brown);
      k.pair(0.6F, 1.3F, 10.0F + hb, 10.9F + hb, -4.8F, -4.0F, -4018542);
      float flop = mo.bob() * 0.8F + Mth.sin(mo.age() * 0.12F) * 0.2F;
      k.flat(1.9F, 9.2F + hb, -4.3F, 4.2F, 8.4F + hb - flop, -4.0F, 0.9F, 0.3F, HeadwearModels.AXIS_Z, brown);
      k.flat(-1.9F, 9.2F + hb, -4.3F, -4.2F, 8.4F + hb - flop, -4.0F, 0.9F, 0.3F, HeadwearModels.AXIS_Z, brown);
   }

   private static void lamb(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      int wool = p.main();
      int shade = p.second();
      int face = p.accent();
      int hoof = -10794172;
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() + br;
      k.leg(-1.3F, 3.0F, -2.0F, s, 0.55F, face, hoof);
      k.leg(1.3F, 3.0F, -2.0F, -s, 0.55F, face, hoof);
      k.leg(-1.3F, 3.0F, 2.0F, -s, 0.55F, face, hoof);
      k.leg(1.3F, 3.0F, 2.0F, s, 0.55F, face, hoof);
      k.box(-2.5F, 2.5F, 2.6F + br, 7.0F + br, -3.2F, 3.2F, wool);

      for (int side = -1; side <= 1; side += 2) {
         k.cube(side * 2.3F, 5.8F + br, -2.2F, 1.25F, shade);
         k.cube(side * 2.3F, 4.0F + br, 1.9F, 1.25F, shade);
         k.cube(side * 1.3F, 7.1F + br, 0.9F, 1.25F, wool);
         k.cube(side * 2.5F, 4.2F + br, -0.6F, 1.1F, wool);
      }

      k.cube(0.0F, 7.2F + br, -1.6F, 1.25F, shade);
      k.cube(0.0F, 6.9F + br, 2.3F, 1.25F, wool);
      k.cube(0.0F, 5.6F + br, 3.6F, 0.9F, shade);
      k.box(-1.6F, 1.6F, 4.8F + hb, 8.0F + hb, -5.8F, -3.0F, face);
      k.cube(0.0F, 8.4F + hb, -4.1F, 1.35F, wool);
      k.cube(-1.1F, 8.0F + hb, -3.7F, 1.0F, shade);
      k.cube(1.1F, 8.0F + hb, -3.7F, 1.0F, shade);
      k.eye(-1.25F, -0.5F, 6.3F + hb, 7.25F + hb, -5.8F, -14804455);
      k.eye(0.5F, 1.25F, 6.3F + hb, 7.25F + hb, -5.8F, -14804455);
      k.box(-0.4F, 0.4F, 5.3F + hb, 5.8F + hb, -5.92F, -5.8F, -876368);
      k.pair(1.05F, 1.55F, 5.5F + hb, 5.95F + hb, -5.88F, -5.8F, -876368);
      float flop = mo.bob() * 0.8F;
      k.flat(1.6F, 7.2F + hb, -4.6F, 3.3F, 6.2F + hb - flop, -4.3F, 0.75F, 0.3F, HeadwearModels.AXIS_Z, face);
      k.flat(-1.6F, 7.2F + hb, -4.6F, -3.3F, 6.2F + hb - flop, -4.3F, 0.75F, 0.3F, HeadwearModels.AXIS_Z, face);
   }

   private static void bunny(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      int fur = p.main();
      int belly = p.accent();
      float br = mo.breathe();
      float ears = mo.hop() * 1.2F;
      k.pair(1.0F, 2.5F, 0.0F, 1.0F, -0.4F, 3.0F, fur);
      k.pair(0.4F, 1.3F, 0.0F, 1.2F, -2.8F, -1.9F, belly);
      k.box(-2.2F, 2.2F, 0.9F + br, 5.4F + br, -2.5F, 2.8F, fur);
      k.box(-1.5F, 1.5F, 1.2F + br, 4.2F + br, -2.65F, -2.4F, belly);
      k.cube(0.0F, 3.0F + br, 3.2F, 1.0F, belly);
      k.box(-2.0F, 2.0F, 4.0F + br, 7.6F + br, -5.0F, -1.4F, fur);
      k.box(-1.1F, 1.1F, 4.2F + br, 5.5F + br, -5.25F, -4.95F, belly);
      k.box(-0.35F, 0.35F, 5.2F + br, 5.6F + br, -5.33F, -5.2F, -876368);
      k.eye(-1.7F, -0.85F, 5.8F + br, 6.85F + br, -5.0F, -14804455);
      k.eye(0.85F, 1.7F, 5.8F + br, 6.85F + br, -5.0F, -14804455);
      k.pair(1.4F, 1.9F, 4.9F + br, 5.3F + br, -5.08F, -5.0F, -876368);
      k.flat(-0.9F, 7.4F + br, -3.0F, -1.4F, 12.4F + br - ears, -2.4F, 0.75F, 0.35F, HeadwearModels.AXIS_X, fur);
      k.flat(-0.9F, 7.7F + br, -3.4F, -1.4F, 11.9F + br - ears, -2.8F, 0.4F, 0.1F, HeadwearModels.AXIS_X, -876368);
      k.flat(0.9F, 7.4F + br, -3.0F, 3.2F, 10.2F + br - ears * 0.5F, -1.4F, 0.75F, 0.35F, HeadwearModels.AXIS_Z, fur);
   }

   static {
      String[][] table = new String[][]{
         {"BARK", "DOG FENNEC_FOX DACHSHUND DALMATIAN SHIBA SEAL"},
         {"ROAR", "POLAR_BEAR BEAR_CUB LION TIGER T_REX BABY_DRAGON"},
         {"HOWL", "HUSKY WOLF"},
         {"YAWN", "HIPPO"},
         {"SNAP", "CROCODILE"},
         {"SLIDE", "PENGUIN"},
         {"ROLL", "RACCOON PANDA PIGLET HEDGEHOG FERRET OTTER ARMADILLO LADYBUG CHINCHILLA TANUKI"},
         {"SPIN", "HAMSTER_BALL MOUSE SQUIRREL CORGI POODLE OWL PEACOCK AXOLOTL OCTOPUS STARFISH"},
         {"JUMP", "CAT BABY_GOAT BABY_SHEEP FOX LEOPARD GUINEA_PIG LYNX KANGAROO CHICK FROG JELLYFISH QUOKKA HAMSTER"},
         {"FLIP", "MONKEY BAT"},
         {"BINKY", "BUNNY"},
         {"BREACH", "WHALE NARWHAL"},
         {"HANDSTAND", "SKUNK"},
         {"REAR", "RED_PANDA ELEPHANT ANTEATER CHIPMUNK"},
         {"SHAKE", "CALF BEAVER PUG DUCKLING STEGOSAURUS BADGER"},
         {"PECK", "TOUCAN ROBIN CHICKEN KIWI"},
         {"FLAP", "PARROT CROW EAGLE SWAN PUFFIN BUDGIE"},
         {"CIRCLE", "CHEETAH BUTTERFLY BEE GOLDFISH DRAGONFLY SHARK SEAHORSE PLATYPUS"},
         {"PUFF", "PUFFERFISH"},
         {"SQUASH", "KOALA SLOTH TURTLE TORTOISE SNAIL CAPYBARA CATERPILLAR"},
         {"STRIKE", "RHINO BOAR TRICERATOPS SNAKE"},
         {"SIDESTEP", "CRAB"},
         {"NOD", "HIGHLAND_COW MEERKAT GECKO"}
      };

      for (String[] row : table) {
         for (String name : row[1].split(" ")) {
            TRICKS.put(CosmeticsModule.Pet.valueOf(name), PetModels.Trick.valueOf(row[0]));
         }
      }
   }

   record Effect(float mouth, float legs) {
      static final PetModels.Effect NONE = new PetModels.Effect(0.0F, 0.0F);
   }

   static final class HamsterBall {
      private static final float PX = 0.0625F;
      private static final float RADIUS = 4.6F;
      private static final int PINK = -876370;
      private static final int EYE = -15068140;
      private static final int SHELL = 1356395263;
      private static final int BAND = -1616323856;

      private HamsterBall() {
      }

      static void submit(PoseStack matrices, SubmitNodeCollector queue, int light, Identifier white, float limb, float amplitude, PetModels.Pal pal) {
         int FUR = pal.main();
         int CREAM = pal.accent();
         float ground = 24.016F;
         float centre = ground - 4.6F;
         float run = Mth.sin(limb * 1.3F) * 0.9F * Math.min(1.0F, amplitude * 2.0F + 0.15F);
         queue.submitCustomGeometry(matrices, RenderTypes.entityCutoutNoCull(white), (entry, vc) -> {
            HeadwearModels m = new HeadwearModels(entry, vc, light);
            float floor = ground - 0.7F;
            m.box(-1.7F, 1.7F, floor - 0.9F, floor - 3.9F, -1.4F, 2.4F, FUR);
            m.box(-1.3F, 1.3F, floor - 0.6F, floor - 1.2F, -1.2F, 2.0F, CREAM);
            m.box(-1.5F, 1.5F, floor - 1.3F, floor - 3.9F, -3.4F, -1.3F, FUR);
            m.box(-1.2F, 1.2F, floor - 1.1F, floor - 2.4F, -3.7F, -2.2F, CREAM);
            m.box(-1.9F, -1.4F, floor - 1.6F, floor - 2.7F, -3.2F, -2.2F, CREAM);
            m.box(1.4F, 1.9F, floor - 1.6F, floor - 2.7F, -3.2F, -2.2F, CREAM);
            m.box(-1.5F, -0.7F, floor - 3.8F, floor - 4.6F, -2.4F, -1.9F, -876370);
            m.box(0.7F, 1.5F, floor - 3.8F, floor - 4.6F, -2.4F, -1.9F, -876370);
            m.box(-1.0F, -0.5F, floor - 2.8F, floor - 3.4F, -3.55F, -3.35F, -15068140);
            m.box(0.5F, 1.0F, floor - 2.8F, floor - 3.4F, -3.55F, -3.35F, -15068140);
            m.box(-0.35F, 0.35F, floor - 2.2F, floor - 2.7F, -3.85F, -3.6F, -876370);
            m.box(-1.4F, -0.6F, floor, floor - 0.7F, -1.2F + run, -0.4F + run, -876370);
            m.box(0.6F, 1.4F, floor, floor - 0.7F, -1.2F - run, -0.4F - run, -876370);
            m.box(-1.4F, -0.6F, floor, floor - 0.7F, 1.2F - run, 2.0F - run, -876370);
            m.box(0.6F, 1.4F, floor, floor - 0.7F, 1.2F + run, 2.0F + run, -876370);
         });
         matrices.pushPose();
         matrices.translate(0.0F, centre * 0.0625F, 0.0F);
         matrices.mulPose(Axis.XP.rotationDegrees(-limb * 38.0F));
         queue.order(1).submitCustomGeometry(matrices, RenderTypes.entityTranslucent(white), (entry, vc) -> {
            shell(entry, vc, light);
            band(entry, vc, light, true);
            band(entry, vc, light, false);
         });
         matrices.popPose();
      }

      private static void shell(Pose entry, VertexConsumer vc, int light) {
         int stacks = 10;
         int slices = 16;

         for (int i = 0; i < stacks; i++) {
            float a0 = (float) Math.PI * i / stacks;
            float a1 = (float) Math.PI * (i + 1) / stacks;

            for (int j = 0; j < slices; j++) {
               float b0 = (float) (Math.PI * 2) * j / slices;
               float b1 = (float) (Math.PI * 2) * (j + 1) / slices;
               sphereVertex(entry, vc, a0, b0, light, 1356395263);
               sphereVertex(entry, vc, a0, b1, light, 1356395263);
               sphereVertex(entry, vc, a1, b1, light, 1356395263);
               sphereVertex(entry, vc, a1, b0, light, 1356395263);
            }
         }
      }

      private static void sphereVertex(Pose entry, VertexConsumer vc, float polar, float around, int light, int color) {
         float nx = Mth.sin(polar) * Mth.cos(around);
         float ny = Mth.cos(polar);
         float nz = Mth.sin(polar) * Mth.sin(around);
         CosmeticsFeatureRenderer.vertex(entry, vc, nx * 4.6F * 0.0625F, ny * 4.6F * 0.0625F, nz * 4.6F * 0.0625F, 0.0F, 0.0F, light, color, nx, ny, nz);
      }

      private static void band(Pose entry, VertexConsumer vc, int light, boolean rolling) {
         int segments = 24;
         float r0 = 4.646F;
         float r1 = 4.8759995F;
         float half = 0.45F;

         for (int i = 0; i < segments; i++) {
            float a0 = (float) (Math.PI * 2) * i / segments;
            float a1 = (float) (Math.PI * 2) * (i + 1) / segments;
            float c0 = Mth.cos(a0);
            float s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1);
            float s1 = Mth.sin(a1);

            for (float side : new float[]{-half, half}) {
               float[] p0 = rolling ? new float[]{side, c0, s0} : new float[]{c0, s0, side};
               float[] p1 = rolling ? new float[]{side, c1, s1} : new float[]{c1, s1, side};
               float[] n0 = rolling ? new float[]{0.0F, c0, s0} : new float[]{c0, s0, 0.0F};
               float[] n1 = rolling ? new float[]{0.0F, c1, s1} : new float[]{c1, s1, 0.0F};
               bandVertex(entry, vc, p0, n0, r1, rolling, light);
               bandVertex(entry, vc, p1, n1, r1, rolling, light);
               bandVertex(entry, vc, p1, n1, r0, rolling, light);
               bandVertex(entry, vc, p0, n0, r0, rolling, light);
            }

            float[] q0 = rolling ? new float[]{-half, c0, s0} : new float[]{c0, s0, -half};
            float[] q1 = rolling ? new float[]{-half, c1, s1} : new float[]{c1, s1, -half};
            float[] q2 = rolling ? new float[]{half, c1, s1} : new float[]{c1, s1, half};
            float[] q3 = rolling ? new float[]{half, c0, s0} : new float[]{c0, s0, half};
            float[] n0 = rolling ? new float[]{0.0F, c0, s0} : new float[]{c0, s0, 0.0F};
            float[] n1 = rolling ? new float[]{0.0F, c1, s1} : new float[]{c1, s1, 0.0F};
            bandVertex(entry, vc, q0, n0, r1, rolling, light);
            bandVertex(entry, vc, q1, n1, r1, rolling, light);
            bandVertex(entry, vc, q2, n1, r1, rolling, light);
            bandVertex(entry, vc, q3, n0, r1, rolling, light);
         }
      }

      private static void bandVertex(Pose entry, VertexConsumer vc, float[] p, float[] n, float r, boolean rolling, int light) {
         float x = rolling ? p[0] : p[0] * r;
         float y = p[1] * r;
         float z = rolling ? p[2] * r : p[2];
         CosmeticsFeatureRenderer.vertex(entry, vc, x * 0.0625F, y * 0.0625F, z * 0.0625F, 0.0F, 0.0F, light, -1616323856, n[0], n[1], n[2]);
      }
   }

   record Kit(HeadwearModels m, float ground) {
      void box(float x0, float x1, float h0, float h1, float z0, float z1, int color) {
         this.m.box(x0, x1, this.ground - h0, this.ground - h1, z0, z1, color);
      }

      void pair(float x0, float x1, float h0, float h1, float z0, float z1, int color) {
         this.box(x0, x1, h0, h1, z0, z1, color);
         this.box(-x1, -x0, h0, h1, z0, z1, color);
      }

      void cube(float x, float h, float z, float half, int color) {
         this.m.cube(x, this.ground - h, z, half, color);
      }

      void beam(float ax, float ah, float az, float bx, float bh, float bz, float radius, int color) {
         this.m.beam(HeadwearModels.p(ax, this.ground - ah, az), HeadwearModels.p(bx, this.ground - bh, bz), radius, radius, color);
      }

      void beam2(float ax, float ah, float az, float bx, float bh, float bz, float ra, float rb, int color) {
         this.m.beam(HeadwearModels.p(ax, this.ground - ah, az), HeadwearModels.p(bx, this.ground - bh, bz), ra, rb, color);
      }

      void flat(float ax, float ah, float az, float bx, float bh, float bz, float width, float thick, float[] axis, int color) {
         this.m
            .slabAlong(HeadwearModels.p(ax, this.ground - ah, az), HeadwearModels.p(bx, this.ground - bh, bz), width, width * 0.7F, thick, thick, axis, color);
      }

      void taper(float bx0, float bx1, float bz0, float bz1, float h0, float tx0, float tx1, float tz0, float tz1, float h1, int color) {
         this.m.prism(bx0, bx1, bz0, bz1, this.ground - h0, tx0, tx1, tz0, tz1, this.ground - h1, color);
      }

      void taperPair(float bx0, float bx1, float bz0, float bz1, float h0, float tx0, float tx1, float tz0, float tz1, float h1, int color) {
         this.taper(bx0, bx1, bz0, bz1, h0, tx0, tx1, tz0, tz1, h1, color);
         this.taper(-bx1, -bx0, bz0, bz1, h0, -tx1, -tx0, tz0, tz1, h1, color);
      }

      void chain(float[][] points, float[] radii, int color, int tip) {
         float[][] converted = new float[points.length][];

         for (int i = 0; i < points.length; i++) {
            converted[i] = HeadwearModels.p(points[i][0], this.ground - points[i][1], points[i][2]);
         }

         this.m.chain(converted, radii, color, tip, 1);
      }

      void leg(float x, float hip, float z, float swing, float half, int color, int foot) {
         float fh = hip - hip * Mth.cos(swing);
         float fz = z - hip * Mth.sin(swing);
         this.beam(x, hip, z, x, fh + 0.6F, fz, half, color);
         this.box(x - half - 0.1F, x + half + 0.1F, fh, fh + 0.8F, fz - half - 0.35F, fz + half + 0.1F, foot);
      }

      void eye(float x0, float x1, float h0, float h1, float face, int color) {
         this.box(x0, x1, h0, h1, face - 0.15F, face, color);
         float w = (x1 - x0) * 0.35F;
         float outer = x0 < 0.0F ? x0 + 0.1F : x1 - 0.1F - w;
         this.box(outer, outer + w, h1 - 0.45F, h1 - 0.1F, face - 0.22F, face - 0.1F, -1);
      }
   }

   record Motion(float limb, float amplitude, float age, float hop, float mouth) {
      float swing() {
         return Mth.sin(this.limb * 0.6662F) * 0.7F * this.amplitude;
      }

      float bob() {
         return Mth.abs(Mth.cos(this.limb * 0.6662F)) * 0.45F * this.amplitude;
      }

      float breathe() {
         return Mth.sin(this.age * 0.08F) * 0.12F;
      }
   }

   record Pal(int main, int second, int accent) {
      static PetModels.Pal of(int[] natural, int picked) {
         if (picked == 0) {
            return new PetModels.Pal(natural[0], natural[1], natural[2]);
         } else {
            int main = 0xFF000000 | picked;
            return new PetModels.Pal(main, PetModels.relight(main, natural[0], natural[1], 1.0F), PetModels.relight(main, natural[0], natural[2], 0.7F));
         }
      }
   }

   static enum Trick {
      BARK,
      ROAR,
      ROLL,
      SPIN,
      JUMP,
      FLIP,
      BINKY,
      BREACH,
      HANDSTAND,
      REAR,
      SHAKE,
      PECK,
      FLAP,
      CIRCLE,
      PUFF,
      SQUASH,
      STRIKE,
      SIDESTEP,
      NOD,
      HOWL,
      YAWN,
      SNAP,
      SLIDE;
   }
}
