package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import net.minecraft.util.Mth;

final class HeadwearModels {
   private static final float PX = 0.0625F;
   private final Pose entry;
   private final VertexConsumer vc;
   private final int light;
   static final float[] AXIS_X = new float[]{1.0F, 0.0F, 0.0F};
   static final float[] AXIS_Y = new float[]{0.0F, 1.0F, 0.0F};
   static final float[] AXIS_Z = new float[]{0.0F, 0.0F, 1.0F};

   HeadwearModels(Pose entry, VertexConsumer vc, int light) {
      this.entry = entry;
      this.vc = vc;
      this.light = light;
   }

   static boolean handles(CosmeticsModule.Headwear kind) {
      return switch (kind) {
         case NONE, HALO, CROWN, CAT_EARS, FOX_EARS, WOLF_EARS, BUNNY_EARS, BEAR_EARS, MOUSE_EARS -> false;
         default -> true;
      };
   }

   void draw(CosmeticsModule.Headwear kind, float top, int main, int accent, float age) {
      switch (kind) {
         case TIARA:
            this.tiara(top, main, accent);
            break;
         case FLOWER_CROWN:
            this.flowerCrown(top, main, accent);
            break;
         case DEVIL_HORNS:
            this.devilHorns(top, main, accent);
            break;
         case RAM_HORNS:
            this.ramHorns(top, main, accent);
            break;
         case BULL_HORNS:
            this.bullHorns(top, main, accent);
            break;
         case UNICORN_HORN:
            this.unicornHorn(top, main, accent);
            break;
         case ANTLERS:
            this.antlers(top, main, accent);
            break;
         case ANTENNAE:
            this.antennae(top, main, accent, age);
            break;
         case TOP_HAT:
            this.topHat(top, main, accent);
            break;
         case WIZARD_HAT:
            this.wizardHat(top, main, accent);
            break;
         case PARTY_HAT:
            this.partyHat(top, main, accent);
            break;
         case SANTA_HAT:
            this.santaHat(top, main, accent);
            break;
         case BEANIE:
            this.beanie(top, main, accent);
            break;
         case BASEBALL_CAP:
            this.baseballCap(top, main, accent);
            break;
         case COWBOY_HAT:
            this.cowboyHat(top, main, accent);
            break;
         case CHEF_HAT:
            this.chefHat(top, main, accent);
            break;
         case VIKING_HELMET:
            this.vikingHelmet(top, main, accent);
            break;
         case PROPELLER_CAP:
            this.propellerCap(top, main, accent, age);
            break;
         case HEADPHONES:
            this.headphones(top, main, accent);
            break;
         case BOW:
            this.bow(top, main, accent);
            break;
         case SPROUT:
            this.sprout(top, main, accent);
            break;
         case SHARK_FIN:
            this.sharkFin(top, main, accent);
            break;
         case FROG_EYES:
            this.frogEyes(top, main, accent);
            break;
         case PIRATE_HAT:
            this.pirateHat(top, main, accent);
            break;
         case GRADUATION_CAP:
            this.graduationCap(top, main, accent);
            break;
         case HARD_HAT:
            this.hardHat(top, main, accent);
            break;
         case NINJA_HEADBAND:
            this.ninjaHeadband(top, main, accent);
            break;
         case MUSHROOM_CAP:
            this.mushroomCap(top, main, accent);
            break;
         case ORBITING_STARS:
            this.orbitingStars(top, main, accent, age);
            break;
         case JESTER_HAT:
            this.jesterHat(top, main, accent);
            break;
         case SOMBRERO:
            this.sombrero(top, main, accent);
            break;
         case GOGGLES:
            this.goggles(top, main, accent);
            break;
         case DRAGON_HORNS:
            this.dragonHorns(top, main, accent);
            break;
         case ROOSTER_COMB:
            this.roosterComb(top, main, accent);
            break;
         case FEZ:
            this.fez(top, main, accent);
            break;
         case BERET:
            this.beret(top, main, accent);
            break;
         case BUCKET_HAT:
            this.bucketHat(top, main, accent);
            break;
         case THORN_CROWN:
            this.thornCrown(top, main, accent);
            break;
         case FLAME_CROWN:
            this.flameCrown(top, main, accent, age);
            break;
         case PUMPKIN:
            this.pumpkin(top, main, accent);
            break;
         case POLICE_CAP:
            this.policeCap(top, main, accent);
            break;
         case LAUREL_WREATH:
            this.laurelWreath(top, main, accent);
            break;
         case ICE_CREAM:
            this.iceCream(top, main, accent);
            break;
         case BIRTHDAY_CAKE:
            this.birthdayCake(top, main, accent, age);
            break;
         case TRAFFIC_CONE:
            this.trafficCone(top, main, accent);
            break;
         case SAMURAI_HELMET:
            this.samuraiHelmet(top, main, accent);
            break;
         case KNIGHT_HELM:
            this.knightHelm(top, main, accent);
            break;
         case LIGHTBULB:
            this.lightbulb(top, main, accent, age);
            break;
         case RAIN_CLOUD:
            this.rainCloud(top, main, accent, age);
            break;
         case FLOATING_HEARTS:
            this.floatingHearts(top, main, accent, age);
            break;
         case BANDANA:
            this.bandana(top, main, accent);
            break;
         case HEAD_WINGS:
            this.headWings(top, main, accent);
            break;
         case MOHAWK:
            this.mohawk(top, main, accent);
            break;
         default:
            MoreHeadwear.draw(this, kind, top, main, accent, age);
      }
   }

   private void tiara(float top, int metal, int gem) {
      float y = top + 1.6F;
      float r = 4.75F;
      float[] previous = null;

      for (int i = 0; i <= 8; i++) {
         double a = Math.toRadians(195.0 + 150.0 * i / 8.0);
         float[] point = p((float)Math.cos(a) * r, y, (float)Math.sin(a) * r);
         if (previous != null) {
            this.beam(previous, point, 0.3F, 0.3F, metal);
         }

         previous = point;
      }

      this.spike(270.0, r, y, top - 2.6F, 0.55F, metal);
      this.spike(240.0, r, y, top - 0.9F, 0.42F, metal);
      this.spike(300.0, r, y, top - 0.9F, 0.42F, metal);
      this.spike(215.0, r, y, top + 0.2F, 0.3F, metal);
      this.spike(325.0, r, y, top + 0.2F, 0.3F, metal);
      this.box(-0.55F, 0.55F, top + 0.2F, top + 1.3F, -5.1F, -4.6F, gem);
   }

   private void spike(double degrees, float r, float y, float tipY, float width, int color) {
      double a = Math.toRadians(degrees);
      float x = (float)Math.cos(a) * r;
      float z = (float)Math.sin(a) * r;
      this.beam(p(x, y, z), p(x * 1.02F, tipY, z * 1.02F), width, 0.05F, color);
   }

   private void flowerCrown(float top, int vine, int petal) {
      float y = top + 0.9F;
      float r = 4.85F;
      float[] previous = null;

      for (int i = 0; i <= 16; i++) {
         double a = (Math.PI * 2) * i / 16.0;
         float[] point = p((float)Math.cos(a) * r, y + (i % 2 == 0 ? 0.0F : 0.25F), (float)Math.sin(a) * r);
         if (previous != null) {
            this.beam(previous, point, 0.33F, 0.33F, vine);
         }

         previous = point;
      }

      int middle = CosmeticsArt.lerp(petal, -6528, 0.7);

      for (int i = 0; i < 7; i++) {
         double a = Math.toRadians(270.0 + 360.0 * i / 7.0);
         float cos = (float)Math.cos(a);
         float sin = (float)Math.sin(a);
         float cx = cos * (r + 0.25F);
         float cz = sin * (r + 0.25F);
         float fy = y - 0.3F;
         this.cube(cx - sin * 0.75F, fy, cz + cos * 0.75F, 0.45F, petal);
         this.cube(cx + sin * 0.75F, fy, cz - cos * 0.75F, 0.45F, petal);
         this.cube(cx, fy - 0.75F, cz, 0.45F, petal);
         this.cube(cx, fy + 0.75F, cz, 0.45F, petal);
         this.cube(cx + cos * 0.25F, fy, cz + sin * 0.25F, 0.4F, middle);
      }
   }

   private void devilHorns(float top, int horn, int tip) {
      for (int side = -1; side <= 1; side += 2) {
         this.chain(
            new float[][]{
               p(side * 2.3F, top + 0.4F, -1.6F),
               p(side * 3.0F, top - 1.4F, -1.9F),
               p(side * 3.5F, top - 3.0F, -1.5F),
               p(side * 3.4F, top - 4.4F, -0.7F),
               p(side * 2.9F, top - 5.3F, 0.3F)
            },
            new float[]{1.0F, 0.8F, 0.58F, 0.35F, 0.06F},
            horn,
            tip,
            2
         );
      }
   }

   private void ramHorns(float top, int horn, int ridge) {
      for (int side = -1; side <= 1; side += 2) {
         float[][] points = new float[13][];
         points[0] = p(side * 3.4F, top + 0.2F, -0.6F);
         points[1] = p(side * 4.6F, top - 0.6F, -0.2F);
         float cy = top + 3.0F;
         float cz = 0.4F;

         for (int i = 0; i <= 10; i++) {
            float t = i / 10.0F;
            double a = Math.toRadians(250.0 + 330.0 * t);
            float r = 3.9F * (1.0F - 0.5F * t);
            float cos = (float)Math.cos(a);
            points[i + 2] = p(side * (5.4F + 1.2F * t + cos * r * 0.5F), cy + (float)Math.sin(a) * r, cz + cos * r * 0.85F);
         }

         for (int i = 0; i < points.length - 1; i++) {
            float ra = 1.4F - 0.95F * i / (points.length - 1);
            float rb = 1.4F - 0.95F * (i + 1) / (points.length - 1);
            this.beam(points[i], points[i + 1], ra, rb, i % 3 == 2 ? ridge : horn);
         }
      }
   }

   private void bullHorns(float top, int horn, int tip) {
      for (int side = -1; side <= 1; side += 2) {
         this.chain(
            new float[][]{
               p(side * 3.9F, top + 1.4F, -0.4F),
               p(side * 5.6F, top + 1.0F, -0.7F),
               p(side * 7.0F, top - 0.2F, -0.9F),
               p(side * 7.7F, top - 1.9F, -0.8F),
               p(side * 7.6F, top - 3.4F, -0.4F)
            },
            new float[]{1.05F, 0.9F, 0.7F, 0.45F, 0.1F},
            horn,
            tip,
            2
         );
      }
   }

   private void unicornHorn(float top, int pearl, int gold) {
      float[] from = p(0.0F, top + 2.6F, -4.2F);
      float[] to = p(0.0F, top - 6.4F, -6.6F);
      int steps = 10;

      for (int i = 0; i < steps; i++) {
         float t0 = (float)i / steps;
         float t1 = (float)(i + 1) / steps;
         this.beam(along(from, to, t0), along(from, to, t1), 1.1F * (1.0F - t0) + 0.06F, 1.1F * (1.0F - t1) + 0.06F, i % 2 == 0 ? pearl : gold);
      }
   }

   private void antlers(float top, int wood, int tip) {
      for (int side = -1; side <= 1; side += 2) {
         float[] knee = p(side * 3.6F, top - 1.8F, 0.2F);
         float[] fork = p(side * 4.9F, top - 3.9F, 0.7F);
         this.chain(
            new float[][]{p(side * 2.4F, top + 0.3F, -0.2F), knee, fork, p(side * 5.6F, top - 6.0F, 1.5F), p(side * 5.5F, top - 7.4F, 2.4F)},
            new float[]{0.6F, 0.52F, 0.45F, 0.35F, 0.12F},
            wood,
            tip,
            1
         );
         this.chain(new float[][]{knee, p(side * 3.3F, top - 3.6F, -1.5F), p(side * 3.0F, top - 4.6F, -2.0F)}, new float[]{0.4F, 0.25F, 0.08F}, wood, tip, 1);
         this.chain(new float[][]{fork, p(side * 4.3F, top - 5.9F, -0.5F), p(side * 4.0F, top - 6.9F, -0.8F)}, new float[]{0.38F, 0.22F, 0.08F}, wood, tip, 1);
         this.chain(new float[][]{fork, p(side * 6.8F, top - 5.0F, 0.8F), p(side * 7.6F, top - 6.2F, 1.0F)}, new float[]{0.36F, 0.22F, 0.08F}, wood, tip, 1);
      }
   }

   private void antennae(float top, int stalk, int glow, float age) {
      float sway = Mth.sin(age * 0.15F) * 0.35F;

      for (int side = -1; side <= 1; side += 2) {
         float[] tip = p(side * 2.7F + sway * 1.6F, top - 4.8F, -1.6F);
         this.chain(
            new float[][]{p(side * 1.4F, top + 0.3F, -1.2F), p(side * 2.0F + sway, top - 2.4F, -2.0F), tip}, new float[]{0.28F, 0.24F, 0.2F}, stalk, stalk, 0
         );
         this.cube(tip[0], tip[1] - 0.4F, tip[2], 0.85F, glow);
      }
   }

   private void sprout(float top, int leaf, int stem) {
      float[] head = p(0.6F, top - 3.6F, 0.3F);
      this.chain(new float[][]{p(0.0F, top + 0.4F, 0.5F), p(0.2F, top - 2.2F, 0.5F), head}, new float[]{0.3F, 0.28F, 0.25F}, stem, stem, 0);
      this.slabAlong(head, p(3.4F, top - 4.8F, 0.1F), 0.3F, 1.3F, 0.12F, 0.1F, AXIS_Z, leaf);
      this.slabAlong(p(0.5F, top - 3.2F, 0.4F), p(-2.4F, top - 4.4F, 0.8F), 0.3F, 1.1F, 0.12F, 0.1F, AXIS_Z, leaf);
      this.slabAlong(head, p(1.0F, top - 5.6F, -0.4F), 0.25F, 0.9F, 0.12F, 0.1F, AXIS_X, leaf);
   }

   private void sharkFin(float top, int fin, int belly) {
      this.box(-0.7F, 0.7F, top + 0.3F, top - 0.3F, -3.0F, 3.4F, belly);
      this.prism(-0.6F, 0.6F, -2.6F, 3.2F, top - 0.3F, -0.12F, 0.12F, 1.6F, 2.9F, top - 5.4F, fin);
   }

   private void frogEyes(float top, int skin, int eye) {
      for (int side = -1; side <= 1; side += 2) {
         this.box(side * 1.0F, side * 3.8F, top + 0.2F, top - 1.4F, -3.8F, -1.0F, skin);
         this.box(side * 1.3F, side * 3.5F, top - 1.0F, top - 3.4F, -4.0F, -1.3F, eye);
         this.box(side * 1.9F, side * 2.9F, top - 1.6F, top - 2.7F, -4.12F, -3.98F, -15395563);
      }
   }

   private void topHat(float top, int felt, int band) {
      this.frustum(0.0F, 0.0F, top, 6.4F, 0.0F, 0.0F, top - 0.55F, 6.4F, 16, felt, true, true);
      this.frustum(0.0F, 0.0F, top - 0.55F, 4.1F, 0.0F, 0.0F, top - 7.6F, 4.35F, 16, felt, true, false);
      this.frustum(0.0F, 0.0F, top - 0.5F, 4.22F, 0.0F, 0.0F, top - 1.9F, 4.3F, 16, band, false, false);
   }

   private void wizardHat(float top, int cloth, int trim) {
      this.frustum(0.0F, 0.0F, top + 0.1F, 6.9F, 0.0F, 0.0F, top - 0.45F, 6.9F, 16, cloth, true, true);
      int levels = 9;
      float height = 11.5F;

      for (int l = 0; l < levels; l++) {
         float t0 = (float)l / levels;
         float t1 = (float)(l + 1) / levels;
         this.frustum(
            1.2F * t0 * t0 * t0,
            3.2F * t0 * t0,
            top - 0.45F - t0 * height,
            wizardRadius(t0),
            1.2F * t1 * t1 * t1,
            3.2F * t1 * t1,
            top - 0.45F - t1 * height,
            wizardRadius(t1),
            12,
            cloth,
            l == levels - 1,
            false
         );
      }

      this.frustum(0.0F, 0.0F, top - 0.4F, 4.45F, 0.0F, 0.0F, top - 1.5F, 4.1F, 12, trim, false, false);
      this.cube(1.2F, top - 0.85F - height, 3.2F, 0.6F, trim);
   }

   private static float wizardRadius(float t) {
      return 4.3F * (float)Math.pow(1.0F - t, 1.15) + 0.12F;
   }

   private void partyHat(float top, int stripe, int other) {
      int levels = 6;
      float height = 7.2F;

      for (int l = 0; l < levels; l++) {
         float t0 = (float)l / levels;
         float t1 = (float)(l + 1) / levels;
         this.frustum(
            1.0F + 1.4F * t0,
            -0.5F,
            top + 0.3F - t0 * height,
            2.8F * (1.0F - t0) + 0.15F,
            1.0F + 1.4F * t1,
            -0.5F,
            top + 0.3F - t1 * height,
            2.8F * (1.0F - t1) + 0.15F,
            10,
            l % 2 == 0 ? stripe : other,
            l == levels - 1,
            l == 0
         );
      }

      this.cube(2.4F, top - 0.2F - height, -0.5F, 0.85F, other);
   }

   private void santaHat(float top, int red, int white) {
      this.squareFrustum(4.85F, top + 1.3F, 4.85F, top - 0.5F, white);
      int levels = 6;
      float height = 5.6F;

      for (int l = 0; l < levels; l++) {
         float t0 = (float)l / levels;
         float t1 = (float)(l + 1) / levels;
         this.frustum(
            3.4F * t0 * t0,
            0.0F,
            top - 0.5F - height * t0,
            4.7F * (1.0F - t0) + 0.75F,
            3.4F * t1 * t1,
            0.0F,
            top - 0.5F - height * t1,
            4.7F * (1.0F - t1) + 0.75F,
            12,
            red,
            l == levels - 1,
            l == 0
         );
      }

      float[] end = p(3.4F, top - 0.5F - height, 0.0F);
      float[] tip = p(6.0F, top - 3.9F, 0.0F);
      this.beam(end, tip, 0.75F, 0.5F, red);
      this.cube(tip[0] + 0.4F, tip[1] + 0.5F, tip[2], 1.1F, white);
   }

   private void beanie(float top, int knit, int trim) {
      this.squareFrustum(4.85F, top + 2.4F, 4.85F, top - 0.5F, knit);
      this.squareFrustum(4.85F, top - 0.5F, 3.5F, top - 2.0F, knit);
      this.squareFrustum(5.0F, top + 2.6F, 5.0F, top + 1.1F, trim);
      this.cube(0.0F, top - 2.8F, 0.0F, 1.0F, trim);
   }

   private void baseballCap(float top, int cloth, int accent) {
      this.squareFrustum(4.85F, top + 2.0F, 4.85F, top - 0.2F, cloth);
      this.squareFrustum(4.85F, top - 0.2F, 3.9F, top - 1.4F, cloth);
      this.box(-4.1F, 4.1F, top + 1.75F, top + 2.25F, -9.2F, -4.85F, cloth);
      this.box(-0.45F, 0.45F, top - 1.35F, top - 1.9F, -0.45F, 0.45F, accent);
      this.box(-1.3F, 1.3F, top + 0.1F, top + 1.5F, -4.97F, -4.85F, accent);
   }

   private void cowboyHat(float top, int leather, int band) {
      this.frustum(0.0F, 0.0F, top + 0.5F, 8.2F, 0.0F, 0.0F, top, 8.2F, 16, leather, true, true);

      for (int side = -1; side <= 1; side += 2) {
         this.box(side * 7.3F, side * 8.6F, top + 0.1F, top - 1.3F, -4.2F, 4.2F, leather);
      }

      this.frustum(0.0F, 0.0F, top, 4.4F, 0.0F, 0.0F, top - 4.8F, 3.8F, 12, leather, true, false);
      this.box(-0.3F, 0.3F, top - 4.85F, top - 4.6F, -2.6F, 2.6F, CosmeticsArt.shade(leather, 0.75F));
      this.frustum(0.0F, 0.0F, top + 0.05F, 4.5F, 0.0F, 0.0F, top - 1.0F, 4.38F, 12, band, false, false);
   }

   private void chefHat(float top, int white, int band) {
      this.squareFrustum(4.75F, top + 1.0F, 4.75F, top - 1.0F, band);
      this.frustum(0.0F, 0.0F, top - 1.0F, 4.2F, 0.0F, 0.0F, top - 5.0F, 5.6F, 12, white, false, true);
      this.frustum(0.0F, 0.0F, top - 5.0F, 5.6F, 0.0F, 0.0F, top - 6.6F, 4.4F, 12, white, true, false);
   }

   private void vikingHelmet(float top, int steel, int horn) {
      this.squareFrustum(4.9F, top + 2.2F, 4.9F, top - 0.3F, steel);
      this.squareFrustum(4.9F, top - 0.3F, 3.2F, top - 2.2F, steel);
      this.squareFrustum(5.05F, top + 2.5F, 5.05F, top + 1.4F, CosmeticsArt.shade(steel, 0.82F));
      this.box(-0.55F, 0.55F, top + 2.4F, top + 5.4F, -5.2F, -4.95F, steel);

      for (int side = -1; side <= 1; side += 2) {
         this.chain(
            new float[][]{
               p(side * 4.7F, top + 0.2F, 0.0F), p(side * 6.6F, top - 1.0F, -0.2F), p(side * 7.4F, top - 3.2F, 0.2F), p(side * 7.1F, top - 4.8F, 0.8F)
            },
            new float[]{1.0F, 0.75F, 0.45F, 0.08F},
            horn,
            horn,
            0
         );
      }
   }

   private void propellerCap(float top, int cap, int blade, float age) {
      this.squareFrustum(4.85F, top + 1.6F, 4.85F, top - 0.3F, cap);
      this.squareFrustum(4.85F, top - 0.3F, 3.7F, top - 1.6F, cap);
      this.box(-0.3F, 0.3F, top - 1.6F, top - 3.1F, -0.3F, 0.3F, blade);
      this.cube(0.0F, top - 3.3F, 0.0F, 0.55F, cap);
      float[] hub = p(0.0F, top - 3.3F, 0.0F);

      for (int i = 0; i < 2; i++) {
         double a = age * 0.9 + Math.PI * i;
         this.slabAlong(hub, p((float)Math.cos(a) * 4.3F, top - 3.3F, (float)Math.sin(a) * 4.3F), 0.35F, 1.1F, 0.12F, 0.1F, null, blade);
      }
   }

   private void headphones(float top, int shell, int light) {
      float lift = top + 8.25F;
      float[] previous = null;

      for (int i = 0; i <= 12; i++) {
         double a = Math.PI * i / 12.0;
         float[] point = p((float)Math.cos(a) * 5.3F, -4.2F - (float)Math.sin(a) * 5.0F + lift, 0.4F);
         if (previous != null) {
            this.slabAlong(previous, point, 0.9F, 0.9F, 0.35F, 0.35F, AXIS_Z, shell);
         }

         previous = point;
      }

      for (int side = -1; side <= 1; side += 2) {
         this.box(side * 4.55F, side * 5.9F, -5.8F, -2.2F, -1.8F, 1.8F, shell);
         this.box(side * 5.9F, side * 6.15F, -5.2F, -2.8F, -1.2F, 1.2F, light);
      }
   }

   private void bow(float top, int ribbon, int knot) {
      float[] c = p(2.2F, top - 0.4F, -1.2F);
      this.slabAlong(c, p(c[0] + 3.3F, c[1] - 1.5F, c[2]), 0.45F, 1.5F, 0.5F, 0.6F, AXIS_Y, ribbon);
      this.slabAlong(c, p(c[0] - 3.1F, c[1] - 1.1F, c[2]), 0.45F, 1.4F, 0.5F, 0.6F, AXIS_Y, ribbon);
      this.slabAlong(c, p(c[0] + 1.2F, c[1] + 2.3F, c[2] - 0.3F), 0.4F, 0.55F, 0.2F, 0.2F, AXIS_X, ribbon);
      this.slabAlong(c, p(c[0] - 1.0F, c[1] + 2.5F, c[2] - 0.1F), 0.4F, 0.55F, 0.2F, 0.2F, AXIS_X, ribbon);
      this.cube(c[0], c[1], c[2], 0.75F, knot);
   }

   private void pirateHat(float top, int felt, int trim) {
      this.squareFrustum(4.85F, top + 1.2F, 4.85F, top - 0.5F, felt);
      this.squareFrustum(4.7F, top - 0.5F, 3.4F, top - 2.8F, felt);

      for (int side = -1; side <= 1; side += 2) {
         this.prism(-6.2F, 6.2F, side * 4.7F, side * 5.3F, top + 0.2F, -3.0F, 3.0F, side * 5.3F, side * 5.9F, top - 3.8F, felt);
         this.box(-3.0F, 3.0F, top - 3.8F, top - 3.3F, side * 5.25F, side * 5.95F, trim);
      }

      this.box(-0.8F, 0.8F, top - 2.2F, top - 0.8F, -5.75F, -5.45F, trim);
   }

   private void graduationCap(float top, int cloth, int tassel) {
      this.squareFrustum(4.85F, top + 1.4F, 4.85F, top - 0.4F, cloth);
      this.box(-6.4F, 6.4F, top - 0.4F, top - 0.95F, -6.4F, 6.4F, cloth);
      this.cube(0.0F, top - 1.15F, 0.0F, 0.45F, tassel);
      this.beam(p(0.0F, top - 1.05F, 0.0F), p(6.3F, top - 1.05F, -6.3F), 0.18F, 0.18F, tassel);
      this.beam(p(6.3F, top - 1.05F, -6.3F), p(6.7F, top + 2.4F, -6.7F), 0.25F, 0.45F, tassel);
      this.cube(6.7F, top + 2.9F, -6.7F, 0.55F, tassel);
   }

   private void hardHat(float top, int shell, int stripe) {
      this.squareFrustum(4.9F, top + 2.0F, 4.9F, top - 0.2F, shell);
      this.squareFrustum(4.9F, top - 0.2F, 3.6F, top - 2.4F, shell);
      this.box(-0.7F, 0.7F, top - 2.3F, top - 2.9F, -3.4F, 3.4F, shell);
      this.box(-5.4F, 5.4F, top + 1.7F, top + 2.2F, -5.4F, 5.4F, shell);
      this.box(-4.4F, 4.4F, top + 1.7F, top + 2.2F, -7.6F, -5.4F, shell);
      this.squareFrustum(4.95F, top + 1.05F, 4.95F, top + 0.55F, stripe);
   }

   private void ninjaHeadband(float top, int cloth, int plate) {
      this.squareFrustum(4.72F, top + 3.0F, 4.72F, top + 1.6F, cloth);
      this.box(-1.6F, 1.6F, top + 1.8F, top + 2.8F, -4.95F, -4.7F, plate);
      float[] knot = p(0.4F, top + 2.3F, 4.9F);
      this.cube(knot[0], knot[1], knot[2], 0.6F, cloth);
      this.slabAlong(knot, p(2.6F, top + 6.4F, 8.2F), 0.6F, 0.5F, 0.1F, 0.1F, AXIS_X, cloth);
      this.slabAlong(knot, p(-1.4F, top + 7.0F, 7.2F), 0.6F, 0.5F, 0.1F, 0.1F, AXIS_X, cloth);
   }

   private void mushroomCap(float top, int cap, int spots) {
      this.frustum(0.0F, 0.0F, top + 1.2F, 6.8F, 0.0F, 0.0F, top - 0.8F, 7.0F, 16, cap, false, true);
      this.frustum(0.0F, 0.0F, top - 0.8F, 7.0F, 0.0F, 0.0F, top - 3.0F, 5.4F, 16, cap, false, false);
      this.frustum(0.0F, 0.0F, top - 3.0F, 5.4F, 0.0F, 0.0F, top - 4.2F, 2.8F, 16, cap, true, false);

      for (float[] spot : new float[][]{
         {250.0F, 1.8F}, {320.0F, 1.2F}, {200.0F, 2.6F}, {40.0F, 2.0F}, {120.0F, 1.4F}, {290.0F, 3.4F}, {160.0F, 3.5F}, {15.0F, 3.6F}
      }) {
         float h = spot[1];
         float r = h <= 3.0F ? 7.0F - 1.6F * Math.max(0.0F, h - 0.8F) / 2.2F : 5.4F - 2.6F * (h - 3.0F) / 1.2F;
         double a = Math.toRadians(spot[0]);
         this.cube((float)Math.cos(a) * r, top - h, (float)Math.sin(a) * r, 0.8F, spots);
      }

      this.cube(0.0F, top - 4.3F, 0.0F, 0.9F, spots);
   }

   private void orbitingStars(float top, int star, int glow, float age) {
      for (int i = 0; i < 5; i++) {
         double a = age * 0.08 + (Math.PI * 2) * i / 5.0;
         float y = top - 1.4F + Mth.sin(age * 0.1F + i * 1.3F) * 0.5F;
         this.star((float)Math.cos(a) * 6.2F, y, (float)Math.sin(a) * 6.2F, 0.95F, star, glow);
      }
   }

   private void star(float x, float y, float z, float size, int color, int middle) {
      this.box(x - 0.22F, x + 0.22F, y - size, y + size, z - 0.22F, z + 0.22F, color);
      this.box(x - size, x + size, y - 0.22F, y + 0.22F, z - 0.22F, z + 0.22F, color);
      this.box(x - 0.22F, x + 0.22F, y - 0.22F, y + 0.22F, z - size, z + size, color);
      this.cube(x, y, z, 0.42F, middle);
   }

   private void jesterHat(float top, int one, int two) {
      this.squareFrustum(4.85F, top + 1.4F, 4.85F, top - 0.6F, one);
      this.squareFrustum(5.0F, top + 1.5F, 5.0F, top + 0.6F, two);

      for (int side = -1; side <= 1; side += 2) {
         int color = side < 0 ? one : two;
         float[] end = p(side * 8.6F, top - 3.0F, 0.0F);
         this.chain(
            new float[][]{p(side * 2.2F, top - 0.6F, 0.0F), p(side * 4.4F, top - 3.6F, 0.0F), p(side * 7.0F, top - 4.6F, 0.0F), end},
            new float[]{2.2F, 1.4F, 0.8F, 0.4F},
            color,
            color,
            0
         );
         this.cube(end[0], end[1] + 0.9F, end[2], 0.8F, side < 0 ? two : one);
      }
   }

   private void sombrero(float top, int straw, int trim) {
      this.frustum(0.0F, 0.0F, top + 0.3F, 9.5F, 0.0F, 0.0F, top - 0.3F, 9.5F, 20, straw, true, true);
      this.frustum(0.0F, 0.0F, top - 0.3F, 9.5F, 0.0F, 0.0F, top - 1.3F, 10.2F, 20, straw, false, false);
      this.frustum(0.0F, 0.0F, top - 1.1F, 10.15F, 0.0F, 0.0F, top - 1.4F, 10.3F, 20, trim, false, false);
      this.frustum(0.0F, 0.0F, top - 0.3F, 4.4F, 0.0F, 0.0F, top - 5.2F, 2.6F, 16, straw, false, false);
      this.frustum(0.0F, 0.0F, top - 5.2F, 2.6F, 0.0F, 0.0F, top - 6.0F, 1.6F, 16, straw, true, false);
      this.frustum(0.0F, 0.0F, top - 0.3F, 4.5F, 0.0F, 0.0F, top - 1.4F, 4.25F, 16, trim, false, false);
   }

   private void goggles(float top, int strap, int lens) {
      int frame = CosmeticsArt.shade(strap, 0.7F);
      this.squareFrustum(4.72F, top + 1.9F, 4.72F, top + 0.9F, strap);

      for (int side = -1; side <= 1; side += 2) {
         this.box(side * 0.4F, side * 3.4F, top + 0.3F, top + 2.9F, -5.6F, -4.7F, frame);
         this.box(side * 0.8F, side * 3.0F, top + 0.7F, top + 2.5F, -5.75F, -5.55F, lens);
      }

      this.box(-0.4F, 0.4F, top + 1.2F, top + 2.0F, -5.3F, -4.7F, frame);
   }

   private void dragonHorns(float top, int horn, int tip) {
      for (int side = -1; side <= 1; side += 2) {
         this.chain(
            new float[][]{
               p(side * 2.6F, top + 0.6F, -2.4F),
               p(side * 3.4F, top - 1.0F, -1.2F),
               p(side * 3.9F, top - 2.2F, 1.2F),
               p(side * 4.1F, top - 2.8F, 4.0F),
               p(side * 3.9F, top - 2.6F, 6.6F)
            },
            new float[]{0.95F, 0.8F, 0.6F, 0.35F, 0.06F},
            horn,
            tip,
            2
         );
         this.chain(
            new float[][]{p(side * 3.9F, top + 1.6F, -1.0F), p(side * 4.9F, top + 0.6F, 0.6F), p(side * 5.4F, top + 0.4F, 2.8F)},
            new float[]{0.55F, 0.35F, 0.05F},
            horn,
            tip,
            1
         );
      }

      for (int i = 0; i < 3; i++) {
         float z = -1.6F + 2.4F * i;
         this.beam(p(0.0F, top + 0.2F, z), p(0.0F, top - 1.6F + 0.3F * i, z + 1.2F), 0.45F, 0.05F, tip);
      }
   }

   private void roosterComb(float top, int comb, int base) {
      this.box(-0.8F, 0.8F, top + 0.3F, top - 0.4F, -4.2F, 4.2F, base);

      for (int i = 0; i < 5; i++) {
         float z = -3.2F + 1.6F * i;
         float h = 2.8F + 1.8F * Mth.sin((float) Math.PI * (i + 0.5F) / 5.0F);
         this.prism(-0.9F, 0.9F, z - 0.85F, z + 0.85F, top + 0.3F, -0.6F, 0.6F, z - 0.55F, z + 0.55F, top - h, comb);
      }
   }

   private void fez(float top, int felt, int tassel) {
      this.frustum(0.0F, 0.0F, top + 0.2F, 3.4F, 0.0F, 0.0F, top - 4.2F, 2.9F, 14, felt, true, true);
      float[] knot = p(0.0F, top - 4.4F, 0.0F);
      this.cube(knot[0], knot[1], knot[2], 0.4F, tassel);
      this.chain(new float[][]{knot, p(2.3F, top - 4.0F, 1.2F), p(3.2F, top - 1.2F, 1.6F)}, new float[]{0.2F, 0.2F, 0.2F}, tassel, tassel, 0);
      this.cube(3.2F, top - 0.6F, 1.6F, 0.5F, tassel);
   }

   private void beret(float top, int felt, int stalk) {
      this.squareFrustum(4.7F, top + 0.3F, 4.7F, top - 0.3F, felt);
      this.frustum(0.8F, 0.3F, top - 0.2F, 4.9F, 1.2F, 0.4F, top - 1.0F, 5.8F, 16, felt, false, true);
      this.frustum(1.2F, 0.4F, top - 1.0F, 5.8F, 1.4F, 0.4F, top - 1.7F, 4.8F, 16, felt, true, false);
      this.cube(1.4F, top - 2.0F, 0.4F, 0.35F, stalk);
   }

   private void bucketHat(float top, int cloth, int band) {
      this.squareFrustum(4.8F, top + 1.6F, 4.2F, top - 2.2F, cloth);
      this.prism(-6.6F, 6.6F, -6.6F, 6.6F, top + 3.0F, -4.8F, 4.8F, -4.8F, 4.8F, top + 1.6F, cloth);
      this.squareFrustum(4.9F, top + 1.4F, 4.75F, top + 0.3F, band);
   }

   private void thornCrown(float top, int vine, int thorn) {
      for (int strand = 0; strand < 2; strand++) {
         float[] previous = null;

         for (int i = 0; i <= 20; i++) {
            double a = (Math.PI * 2) * i / 20.0;
            float wobble = strand == 0 ? Mth.sin(i * 1.7F) * 0.35F : Mth.cos(i * 1.7F) * 0.35F;
            float r = 4.85F + strand * 0.1F;
            float[] point = p((float)Math.cos(a) * r, top + 0.8F - strand * 0.4F + wobble, (float)Math.sin(a) * r);
            if (previous != null) {
               this.beam(previous, point, 0.32F, 0.32F, vine);
            }

            previous = point;
         }
      }

      for (int i = 0; i < 16; i++) {
         double a = (Math.PI * 2) * i / 16.0 + 0.2;
         float cos = (float)Math.cos(a);
         float sin = (float)Math.sin(a);
         float[] root = p(cos * 4.9F, top + 0.6F, sin * 4.9F);
         this.beam(root, p(root[0] + cos * 1.6F, root[1] - 1.2F - i % 3 * 0.5F, root[2] + sin * 1.6F), 0.28F, 0.04F, thorn);
      }
   }

   private void flameCrown(float top, int flame, int core, float age) {
      float[] previous = null;

      for (int i = 0; i <= 16; i++) {
         double a = (Math.PI * 2) * i / 16.0;
         float[] point = p((float)Math.cos(a) * 4.8F, top + 0.9F, (float)Math.sin(a) * 4.8F);
         if (previous != null) {
            this.beam(previous, point, 0.4F, 0.4F, CosmeticsArt.shade(flame, 0.7F));
         }

         previous = point;
      }

      for (int i = 0; i < 9; i++) {
         double a = (Math.PI * 2) * i / 9.0;
         float cos = (float)Math.cos(a);
         float sin = (float)Math.sin(a);
         float h = 2.6F + 1.2F * Mth.sin(age * 0.35F + i * 1.9F) + i % 2 * 0.8F;
         float[] root = p(cos * 4.8F, top + 0.9F, sin * 4.8F);
         this.beam(root, p(cos * 4.9F, top + 0.9F - h, sin * 4.9F), 0.9F, 0.05F, flame);
         this.beam(p(cos * 5.1F, top + 0.9F, sin * 5.1F), p(cos * 5.15F, top + 0.9F - h * 0.55F, sin * 5.15F), 0.5F, 0.05F, core);
      }
   }

   private void pumpkin(float top, int rind, int face) {
      int rib = CosmeticsArt.shade(rind, 0.85F);
      this.box(-5.1F, 5.1F, top + 8.35F, top - 0.6F, -5.1F, 5.1F, rind);

      for (float x = -2.6F; x <= 2.7F; x += 2.6F) {
         this.box(x - 0.5F, x + 0.5F, top + 8.2F, top - 0.5F, -5.25F, 5.25F, rib);
         this.box(-5.25F, 5.25F, top + 8.2F, top - 0.5F, x - 0.5F, x + 0.5F, rib);
      }

      for (int side = -1; side <= 1; side += 2) {
         this.box(side * 1.2F, side * 3.4F, top + 2.4F, top + 4.0F, -5.4F, -5.26F, face);
      }

      this.box(-0.5F, 0.5F, top + 4.5F, top + 5.3F, -5.4F, -5.26F, face);
      this.box(-3.2F, 3.2F, top + 6.0F, top + 6.8F, -5.4F, -5.26F, face);
      this.box(-1.4F, -0.8F, top + 6.0F, top + 6.4F, -5.45F, -5.38F, rind);
      this.box(0.8F, 1.4F, top + 6.0F, top + 6.4F, -5.45F, -5.38F, rind);
      this.box(-0.6F, 0.6F, top - 0.6F, top - 2.2F, -0.6F, 0.6F, -10847698);
   }

   private void policeCap(float top, int cloth, int badge) {
      this.squareFrustum(4.8F, top + 1.8F, 4.8F, top + 0.4F, CosmeticsArt.shade(cloth, 0.7F));
      this.prism(-4.8F, 4.8F, -4.8F, 4.8F, top + 0.4F, -5.6F, 5.6F, -5.4F, 5.8F, top - 1.6F, cloth);
      this.box(-5.6F, 5.6F, top - 1.6F, top - 2.0F, -5.4F, 5.8F, cloth);
      this.box(-4.2F, 4.2F, top + 1.6F, top + 2.1F, -8.2F, -4.8F, CosmeticsArt.shade(cloth, 0.45F));
      this.box(-0.9F, 0.9F, top - 1.2F, top, -5.35F, -5.05F, badge);
   }

   private void laurelWreath(float top, int leaf, int stem) {
      float[] previous = null;

      for (int i = 0; i <= 16; i++) {
         double a = Math.toRadians(290.0 + 320.0 * i / 16.0);
         float cos = (float)Math.cos(a);
         float sin = (float)Math.sin(a);
         float[] point = p(cos * 4.8F, top + 1.0F, sin * 4.8F);
         if (previous != null) {
            this.beam(previous, point, 0.25F, 0.25F, stem);
         }

         previous = point;
         float tx = -sin;
         float tz = cos;
         if (cos > 0.0F) {
            tx = -tx;
            tz = -cos;
         }

         for (int row = -1; row <= 1; row += 2) {
            float[] tip = p(point[0] + tx * 1.8F + cos * 0.5F, point[1] + row * 0.9F, point[2] + tz * 1.8F + sin * 0.5F);
            this.slabAlong(point, tip, 0.2F, 0.55F, 0.1F, 0.08F, AXIS_Y, leaf);
         }
      }
   }

   private void iceCream(float top, int cone, int scoop) {
      int levels = 5;

      for (int l = 0; l < levels; l++) {
         float t0 = (float)l / levels;
         float t1 = (float)(l + 1) / levels;
         this.frustum(
            0.0F,
            0.0F,
            top + 0.4F - 5.4F * t0,
            0.3F + 2.9F * t0,
            0.0F,
            0.0F,
            top + 0.4F - 5.4F * t1,
            0.3F + 2.9F * t1,
            12,
            l % 2 == 0 ? cone : CosmeticsArt.shade(cone, 0.85F),
            false,
            false
         );
      }

      this.frustum(0.0F, 0.0F, top - 5.0F, 3.6F, 0.0F, 0.0F, top - 6.6F, 3.2F, 14, scoop, false, true);
      this.frustum(0.0F, 0.0F, top - 6.6F, 3.2F, 0.0F, 0.0F, top - 7.8F, 1.6F, 14, scoop, true, false);

      for (int i = 0; i < 6; i++) {
         double a = (Math.PI * 2) * i / 6.0 + 0.4;
         this.cube((float)Math.cos(a) * 3.3F, top - 4.6F + i % 2 * 0.4F, (float)Math.sin(a) * 3.3F, 0.45F, scoop);
      }

      this.cube(0.0F, top - 8.3F, 0.0F, 0.7F, -3137494);
   }

   private void birthdayCake(float top, int cake, int frosting, float age) {
      this.squareFrustum(4.8F, top + 0.3F, 4.8F, top - 0.1F, frosting);
      this.frustum(0.0F, 0.0F, top - 0.1F, 4.6F, 0.0F, 0.0F, top - 2.6F, 4.6F, 16, cake, false, true);
      this.frustum(0.0F, 0.0F, top - 2.6F, 4.7F, 0.0F, 0.0F, top - 3.1F, 4.7F, 16, frosting, true, false);
      this.frustum(0.0F, 0.0F, top - 0.2F, 4.75F, 0.0F, 0.0F, top - 0.8F, 4.75F, 16, frosting, false, false);

      for (int i = 0; i < 8; i++) {
         double a = (Math.PI * 2) * i / 8.0;
         this.cube((float)Math.cos(a) * 4.7F, top - 2.3F + i % 2 * 0.3F, (float)Math.sin(a) * 4.7F, 0.38F, frosting);
      }

      float[][] candles = new float[][]{{-2.0F, -1.0F}, {0.0F, 1.5F}, {2.0F, -1.0F}};

      for (int i = 0; i < candles.length; i++) {
         float cx = candles[i][0];
         float cz = candles[i][1];
         this.box(cx - 0.3F, cx + 0.3F, top - 3.1F, top - 5.0F, cz - 0.3F, cz + 0.3F, CosmeticsArt.shade(frosting, 1.1F));
         float flicker = 0.3F + 0.08F * Mth.sin(age * 0.6F + i * 2.1F);
         this.cube(cx, top - 5.45F, cz, flicker, -20432);
      }
   }

   private void trafficCone(float top, int orange, int stripe) {
      this.box(-4.9F, 4.9F, top + 0.3F, top - 0.4F, -4.9F, 4.9F, orange);
      this.frustum(0.0F, 0.0F, top - 0.4F, 3.6F, 0.0F, 0.0F, top - 8.0F, 0.6F, 12, orange, true, false);

      for (float[] band : new float[][]{{0.29F, 0.42F}, {0.58F, 0.68F}}) {
         this.frustum(
            0.0F,
            0.0F,
            top - 0.4F - 7.6F * band[0],
            3.66F - 3.0F * band[0],
            0.0F,
            0.0F,
            top - 0.4F - 7.6F * band[1],
            3.66F - 3.0F * band[1],
            12,
            stripe,
            false,
            false
         );
      }
   }

   private void samuraiHelmet(float top, int lacquer, int gold) {
      this.squareFrustum(4.9F, top + 1.6F, 4.9F, top - 0.4F, lacquer);
      this.squareFrustum(4.9F, top - 0.4F, 3.4F, top - 2.2F, lacquer);
      this.box(-5.3F, 5.3F, top + 1.5F, top + 1.9F, -5.3F, 5.3F, lacquer);
      this.prism(-5.6F, 5.6F, -3.0F, 5.9F, top + 4.6F, -4.9F, 4.9F, -2.6F, 4.9F, top + 1.6F, lacquer);

      for (int side = -1; side <= 1; side += 2) {
         this.chain(
            new float[][]{p(side * 0.6F, top + 0.6F, -5.0F), p(side * 2.4F, top - 2.6F, -5.6F), p(side * 3.2F, top - 5.2F, -5.2F)},
            new float[]{0.35F, 0.28F, 0.12F},
            gold,
            gold,
            0
         );
      }

      this.box(-0.9F, 0.9F, top - 0.2F, top + 1.4F, -5.3F, -5.0F, gold);
   }

   private void knightHelm(float top, int steel, int slit) {
      this.box(-4.95F, 4.95F, top + 8.45F, top - 0.4F, -4.95F, 4.95F, steel);
      this.squareFrustum(4.95F, top - 0.4F, 3.6F, top - 1.6F, steel);
      this.box(-3.8F, 3.8F, top + 3.2F, top + 3.9F, -5.05F, -4.98F, slit);
      this.box(-0.35F, 0.35F, top + 4.0F, top + 8.3F, -5.2F, -4.95F, CosmeticsArt.shade(steel, 1.12F));

      for (int i = 0; i < 3; i++) {
         float bx = 1.4F + i * 0.9F;
         this.box(bx, bx + 0.45F, top + 5.8F, top + 6.3F, -5.05F, -4.98F, slit);
      }
   }

   private void lightbulb(float top, int glass, int metal, float age) {
      float y = top - 3.0F + Mth.sin(age * 0.12F) * 0.4F;
      this.frustum(0.0F, 0.0F, y, 1.1F, 0.0F, 0.0F, y - 1.4F, 1.25F, 10, metal, false, true);
      this.frustum(0.0F, 0.0F, y - 0.35F, 1.3F, 0.0F, 0.0F, y - 0.65F, 1.3F, 10, CosmeticsArt.shade(metal, 0.8F), false, false);
      this.frustum(0.0F, 0.0F, y - 1.4F, 1.25F, 0.0F, 0.0F, y - 2.6F, 2.4F, 12, glass, false, false);
      this.frustum(0.0F, 0.0F, y - 2.6F, 2.4F, 0.0F, 0.0F, y - 4.4F, 2.5F, 12, glass, false, false);
      this.frustum(0.0F, 0.0F, y - 4.4F, 2.5F, 0.0F, 0.0F, y - 5.6F, 1.3F, 12, glass, true, false);
   }

   private void rainCloud(float top, int cloud, int rain, float age) {
      float[][] puffs = new float[][]{
         {0.0F, -4.6F, 0.0F, 1.9F},
         {-2.2F, -4.2F, 0.3F, 1.5F},
         {2.3F, -4.3F, -0.2F, 1.6F},
         {-0.9F, -5.6F, -0.4F, 1.4F},
         {1.1F, -5.4F, 0.6F, 1.3F},
         {-3.4F, -4.0F, -0.3F, 1.0F},
         {3.6F, -4.0F, 0.3F, 1.0F}
      };

      for (float[] puff : puffs) {
         this.cube(puff[0], top + puff[1], puff[2], puff[3], cloud);
      }

      for (int i = 0; i < 6; i++) {
         float dx = -3.0F + 1.2F * i;
         float dz = (i * 37 % 5 - 2) * 0.6F;
         float phase = (age * 0.25F + i * 0.37F) % 1.0F;
         float dy = top - 2.6F + phase * 2.4F;
         this.box(dx - 0.12F, dx + 0.12F, dy, dy + 0.8F, dz - 0.12F, dz + 0.12F, rain);
      }
   }

   private void floatingHearts(float top, int heart, int shine, float age) {
      for (int i = 0; i < 4; i++) {
         double a = -age * 0.06 + (Math.PI * 2) * i / 4.0;
         float hx = (float)Math.cos(a) * 6.0F;
         float hz = (float)Math.sin(a) * 6.0F;
         float hy = top - 1.0F + Mth.sin(age * 0.12F + i) * 0.6F;
         this.cube(hx - 0.55F, hy - 0.45F, hz, 0.55F, heart);
         this.cube(hx + 0.55F, hy - 0.45F, hz, 0.55F, heart);
         this.box(hx - 1.1F, hx + 1.1F, hy - 0.3F, hy + 0.4F, hz - 0.4F, hz + 0.4F, heart);
         this.box(hx - 0.65F, hx + 0.65F, hy + 0.4F, hy + 0.9F, hz - 0.4F, hz + 0.4F, heart);
         this.box(hx - 0.25F, hx + 0.25F, hy + 0.9F, hy + 1.3F, hz - 0.35F, hz + 0.35F, heart);
         this.cube(hx - 0.65F, hy - 0.6F, hz, 0.22F, shine);
      }
   }

   private void bandana(float top, int cloth, int dots) {
      this.squareFrustum(4.8F, top + 2.6F, 4.8F, top - 0.2F, cloth);
      this.squareFrustum(4.8F, top - 0.2F, 4.1F, top - 0.9F, cloth);
      float[] knot = p(0.0F, top + 2.0F, 5.1F);
      this.cube(knot[0], knot[1], knot[2], 0.7F, cloth);
      this.slabAlong(p(0.3F, top + 2.2F, 5.2F), p(1.4F, top + 5.4F, 6.8F), 0.7F, 0.6F, 0.1F, 0.1F, AXIS_X, cloth);
      this.slabAlong(p(-0.4F, top + 2.2F, 5.2F), p(-1.2F, top + 5.8F, 6.2F), 0.7F, 0.6F, 0.1F, 0.1F, AXIS_X, cloth);

      for (float[] dot : new float[][]{{-3.0F, 1.4F}, {-1.0F, 0.4F}, {1.2F, 1.8F}, {3.1F, 0.6F}, {0.1F, 2.3F}}) {
         this.box(dot[0] - 0.3F, dot[0] + 0.3F, top + dot[1] - 0.3F, top + dot[1] + 0.3F, -4.9F, -4.82F, dots);

         for (int side = -1; side <= 1; side += 2) {
            this.box(side * 4.82F, side * 4.9F, top + dot[1] - 0.3F, top + dot[1] + 0.3F, -dot[0] - 0.3F, -dot[0] + 0.3F, dots);
         }
      }
   }

   private void headWings(float top, int feather, int gold) {
      float[][] feathers = new float[][]{{0.8F, -2.2F, 2.4F, 0.62F}, {0.9F, -3.0F, 1.4F, 0.58F}, {0.8F, -3.4F, 0.2F, 0.52F}, {0.6F, -2.6F, -0.8F, 0.42F}};

      for (int side = -1; side <= 1; side += 2) {
         float[] root = p(side * 4.9F, top + 2.4F, 0.4F);
         float[] facing = new float[]{side * 0.7F, 0.0F, 0.7F};

         for (float[] f : feathers) {
            this.slabAlong(
               root, p(root[0] + side * f[0] * 1.8F, root[1] + f[1] * 1.4F, root[2] + f[2] * 1.2F), 0.35F, f[3] * 1.6F, 0.1F, 0.08F, facing, feather
            );
         }

         this.cube(root[0], root[1], root[2], 0.6F, gold);
      }
   }

   private void mohawk(float top, int hair, int tips) {
      this.box(-1.0F, 1.0F, top + 0.3F, top - 0.3F, -4.4F, 4.4F, hair);

      for (int i = 0; i < 7; i++) {
         float z = -3.9F + 1.3F * i;
         float h = 4.2F + 1.2F * Mth.sin((float) Math.PI * i / 6.0F);
         this.prism(-0.9F, 0.9F, z - 0.6F, z + 0.6F, top + 0.3F, -0.2F, 0.2F, z + 0.3F, z + 0.55F, top - h, hair);
         this.prism(-0.5F, 0.5F, z, z + 0.7F, top - h + 1.5F, -0.2F, 0.2F, z + 0.3F, z + 0.55F, top - h - 0.05F, tips);
      }
   }

   static float[] p(float x, float y, float z) {
      return new float[]{x, y, z};
   }

   static float[] along(float[] a, float[] b, float t) {
      return p(a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, a[2] + (b[2] - a[2]) * t);
   }

   void chain(float[][] points, float[] radii, int color, int tip, int tipSegments) {
      for (int i = 0; i < points.length - 1; i++) {
         this.beam(points[i], points[i + 1], radii[i], radii[i + 1], i >= points.length - 1 - tipSegments ? tip : color);
      }
   }

   void cube(float x, float y, float z, float half, int color) {
      this.box(x - half, x + half, y - half, y + half, z - half, z + half, color);
   }

   void beam(float[] a, float[] b, float ra, float rb, int color) {
      this.slabAlong(a, b, ra, rb, ra, rb, null, color);
   }

   void slabAlong(float[] a, float[] b, float wa, float wb, float ta, float tb, float[] widthAxis, int color) {
      float dx = b[0] - a[0];
      float dy = b[1] - a[1];
      float dz = b[2] - a[2];
      float length = Mth.sqrt(dx * dx + dy * dy + dz * dz);
      if (!(length < 1.0E-4F)) {
         dx /= length;
         dy /= length;
         dz /= length;
         float ux;
         float uy;
         float uz;
         if (widthAxis != null) {
            ux = widthAxis[0];
            uy = widthAxis[1];
            uz = widthAxis[2];
         } else if (Math.abs(dy) < 0.95F) {
            ux = -dz;
            uy = 0.0F;
            uz = dx;
         } else {
            ux = 1.0F;
            uy = 0.0F;
            uz = 0.0F;
         }

         float dot = ux * dx + uy * dy + uz * dz;
         ux -= dx * dot;
         uy -= dy * dot;
         uz -= dz * dot;
         float ul = Mth.sqrt(ux * ux + uy * uy + uz * uz);
         if (ul < 1.0E-4F) {
            ux = -dz;
            uy = 0.0F;
            uz = dx;
            ul = Math.max(1.0E-4F, Mth.sqrt(ux * ux + dx * dx));
         }

         ux /= ul;
         uy /= ul;
         uz /= ul;
         float vx = dy * uz - dz * uy;
         float vy = dz * ux - dx * uz;
         float vz = dx * uy - dy * ux;
         float[][] ca = section(a, ux, uy, uz, vx, vy, vz, wa, ta);
         float[][] cb = section(b, ux, uy, uz, vx, vy, vz, wb, tb);
         float[] middle = along(a, b, 0.5F);

         for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            this.quad(ca[i], ca[j], cb[j], cb[i], color, middle);
         }

         this.quad(ca[0], ca[1], ca[2], ca[3], color, middle);
         this.quad(cb[0], cb[1], cb[2], cb[3], color, middle);
      }
   }

   private static float[][] section(float[] c, float ux, float uy, float uz, float vx, float vy, float vz, float w, float t) {
      return new float[][]{
         p(c[0] + ux * w + vx * t, c[1] + uy * w + vy * t, c[2] + uz * w + vz * t),
         p(c[0] - ux * w + vx * t, c[1] - uy * w + vy * t, c[2] - uz * w + vz * t),
         p(c[0] - ux * w - vx * t, c[1] - uy * w - vy * t, c[2] - uz * w - vz * t),
         p(c[0] + ux * w - vx * t, c[1] + uy * w - vy * t, c[2] + uz * w - vz * t)
      };
   }

   void box(float x0, float x1, float y0, float y1, float z0, float z1, int color) {
      float minX = Math.min(x0, x1);
      float maxX = Math.max(x0, x1);
      float minZ = Math.min(z0, z1);
      float maxZ = Math.max(z0, z1);
      this.prism(minX, maxX, minZ, maxZ, Math.max(y0, y1), minX, maxX, minZ, maxZ, Math.min(y0, y1), color);
   }

   void squareFrustum(float hb, float yb, float ht, float yt, int color) {
      this.prism(-hb, hb, -hb, hb, yb, -ht, ht, -ht, ht, yt, color);
   }

   void prism(float bx0, float bx1, float bz0, float bz1, float by, float tx0, float tx1, float tz0, float tz1, float ty, int color) {
      float[] b00 = p(bx0, by, bz0);
      float[] b10 = p(bx1, by, bz0);
      float[] b11 = p(bx1, by, bz1);
      float[] b01 = p(bx0, by, bz1);
      float[] t00 = p(tx0, ty, tz0);
      float[] t10 = p(tx1, ty, tz0);
      float[] t11 = p(tx1, ty, tz1);
      float[] t01 = p(tx0, ty, tz1);
      float[] middle = p((bx0 + bx1 + tx0 + tx1) / 4.0F, (by + ty) / 2.0F, (bz0 + bz1 + tz0 + tz1) / 4.0F);
      this.quad(t00, t10, t11, t01, color, middle);
      this.quad(b00, b10, b11, b01, color, middle);
      this.quad(b00, b10, t10, t00, color, middle);
      this.quad(b01, b11, t11, t01, color, middle);
      this.quad(b00, b01, t01, t00, color, middle);
      this.quad(b10, b11, t11, t10, color, middle);
   }

   void frustum(float bx, float bz, float by, float br, float tx, float tz, float ty, float tr, int sides, int color, boolean capTop, boolean capBottom) {
      float[] middle = p((bx + tx) / 2.0F, (by + ty) / 2.0F, (bz + tz) / 2.0F);
      float[] topCentre = p(tx, ty, tz);
      float[] bottomCentre = p(bx, by, bz);

      for (int i = 0; i < sides; i++) {
         double a0 = (Math.PI * 2) * i / sides;
         double a1 = (Math.PI * 2) * (i + 1) / sides;
         float c0 = (float)Math.cos(a0);
         float s0 = (float)Math.sin(a0);
         float c1 = (float)Math.cos(a1);
         float s1 = (float)Math.sin(a1);
         float[] b0 = p(bx + c0 * br, by, bz + s0 * br);
         float[] b1 = p(bx + c1 * br, by, bz + s1 * br);
         float[] t0 = p(tx + c0 * tr, ty, tz + s0 * tr);
         float[] t1 = p(tx + c1 * tr, ty, tz + s1 * tr);
         this.quad(b0, b1, t1, t0, color, middle);
         if (capTop) {
            this.quad(topCentre, t0, t1, t1, color, middle);
         }

         if (capBottom) {
            this.quad(bottomCentre, b0, b1, b1, color, middle);
         }
      }
   }

   private void quad(float[] a, float[] b, float[] c, float[] d, int color, float[] inside) {
      float e1x = c[0] - a[0];
      float e1y = c[1] - a[1];
      float e1z = c[2] - a[2];
      float e2x = d[0] - b[0];
      float e2y = d[1] - b[1];
      float e2z = d[2] - b[2];
      float nx = e1y * e2z - e1z * e2y;
      float ny = e1z * e2x - e1x * e2z;
      float nz = e1x * e2y - e1y * e2x;
      float nl = Mth.sqrt(nx * nx + ny * ny + nz * nz);
      if (nl < 1.0E-6F) {
         nx = 0.0F;
         ny = -1.0F;
         nz = 0.0F;
      } else {
         nx /= nl;
         ny /= nl;
         nz /= nl;
      }

      float ox = (a[0] + b[0] + c[0] + d[0]) / 4.0F - inside[0];
      float oy = (a[1] + b[1] + c[1] + d[1]) / 4.0F - inside[1];
      float oz = (a[2] + b[2] + c[2] + d[2]) / 4.0F - inside[2];
      if (nx * ox + ny * oy + nz * oz < 0.0F) {
         nx = -nx;
         ny = -ny;
         nz = -nz;
      }

      int solid = 0xFF000000 | color & 16777215;
      CosmeticsFeatureRenderer.vertex(this.entry, this.vc, a[0] * 0.0625F, a[1] * 0.0625F, a[2] * 0.0625F, 0.0F, 0.0F, this.light, solid, nx, ny, nz);
      CosmeticsFeatureRenderer.vertex(this.entry, this.vc, b[0] * 0.0625F, b[1] * 0.0625F, b[2] * 0.0625F, 1.0F, 0.0F, this.light, solid, nx, ny, nz);
      CosmeticsFeatureRenderer.vertex(this.entry, this.vc, c[0] * 0.0625F, c[1] * 0.0625F, c[2] * 0.0625F, 1.0F, 1.0F, this.light, solid, nx, ny, nz);
      CosmeticsFeatureRenderer.vertex(this.entry, this.vc, d[0] * 0.0625F, d[1] * 0.0625F, d[2] * 0.0625F, 0.0F, 1.0F, this.light, solid, nx, ny, nz);
   }
}
