package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import net.minecraft.util.Mth;

final class MoreHeadwear {
   private static final int GOLD = -1525696;
   private static final float PI = (float) Math.PI;

   private MoreHeadwear() {
   }

   static void draw(HeadwearModels m, CosmeticsModule.Headwear kind, float t, int main, int accent, float age) {
      switch (kind) {
         case ASTRONAUT_HELMET:
            astronautHelmet(m, t, main, accent);
            break;
         case DIVING_HELMET:
            divingHelmet(m, t, main, accent);
            break;
         case ICE_CROWN:
            iceCrown(m, t, main, accent);
            break;
         case CAPTAIN_HAT:
            captainHat(m, t, main, accent);
            break;
         case FEDORA:
            fedora(m, t, main, accent);
            break;
         case BOWLER_HAT:
            bowlerHat(m, t, main, accent);
            break;
         case STRAW_HAT:
            strawHat(m, t, main, accent);
            break;
         case WITCH_HAT:
            witchHat(m, t, main, accent);
            break;
         case STEGOSAURUS_PLATES:
            stegosaurusPlates(m, t, main, accent);
            break;
         case CHICKEN:
            chicken(m, t, main, accent);
            break;
         case RUBBER_DUCK:
            rubberDuck(m, t, main, accent);
            break;
         case CAT_NAP:
            catNap(m, t, main, accent, age);
            break;
         case AXOLOTL_GILLS:
            axolotlGills(m, t, main, accent, age);
            break;
         case ANGLER_LURE:
            anglerLure(m, t, main, accent, age);
            break;
         case CANDLE:
            candle(m, t, main, accent, age);
            break;
         case MINI_PLANET:
            miniPlanet(m, t, main, accent, age);
            break;
         case CRESCENT_MOON:
            crescentMoon(m, t, main, accent, age);
            break;
         case SUN_HALO:
            sunHalo(m, t, main, accent, age);
            break;
         case RAINBOW:
            rainbow(m, t, main, accent);
            break;
         case SNOW_GLOBE:
            snowGlobe(m, t, main, accent, age);
            break;
         case VR_HEADSET:
            vrHeadset(m, t, main, accent, age);
            break;
         case CYBER_VISOR:
            cyberVisor(m, t, main, accent, age);
            break;
         case SHADES:
            shades(m, t, main, accent);
            break;
         case EYEPATCH:
            eyepatch(m, t, main, accent);
            break;
         case ARROW_GAG:
            arrowGag(m, t, main, accent);
            break;
         case ROBOT_ANTENNA:
            robotAntenna(m, t, main, accent, age);
            break;
         case OCTOPUS:
            octopus(m, t, main, accent, age);
            break;
         case POTTED_CACTUS:
            pottedCactus(m, t, main, accent);
            break;
         case BIRD_NEST:
            birdNest(m, t, main, accent, age);
            break;
         case BURGER:
            burger(m, t, main, accent);
            break;
         case TIN_FOIL_HAT:
            tinFoilHat(m, t, main, accent);
            break;
         case SPARTAN_HELMET:
            spartanHelmet(m, t, main, accent);
            break;
         case PHARAOH_HEADDRESS:
            pharaohHeaddress(m, t, main, accent);
            break;
         case DEERSTALKER:
            deerstalker(m, t, main, accent);
            break;
         case FIREFIGHTER_HELMET:
            firefighterHelmet(m, t, main, accent);
            break;
         case EARMUFFS:
            earmuffs(m, t, main, accent);
            break;
         case USHANKA:
            ushanka(m, t, main, accent);
            break;
         case BROKEN_HALO:
            brokenHalo(m, t, main, accent, age);
            break;
         case NIGHTCAP:
            nightcap(m, t, main, accent, age);
      }
   }

   private static int shade(int color, float factor) {
      return CosmeticsArt.shade(color, factor);
   }

   private static int lerp(int a, int b, double t) {
      return CosmeticsArt.lerp(a, b, t);
   }

   private static void sphere(HeadwearModels m, float cx, float cy, float cz, float r, int slices, int sides, int... colors) {
      for (int i = 0; i < slices; i++) {
         float a0 = (float) Math.PI * i / slices;
         float a1 = (float) Math.PI * (i + 1) / slices;
         m.frustum(cx, cz, cy + r * Mth.cos(a0), r * Mth.sin(a0), cx, cz, cy + r * Mth.cos(a1), r * Mth.sin(a1), sides, colors[i % colors.length], false, false);
      }
   }

   private static void ring(HeadwearModels m, float cx, float y, float cz, float r, int segments, float thickness, int color) {
      float[] previous = null;

      for (int i = 0; i <= segments; i++) {
         float a = (float) (Math.PI * 2) * i / segments;
         float[] point = HeadwearModels.p(cx + Mth.cos(a) * r, y, cz + Mth.sin(a) * r);
         if (previous != null) {
            m.beam(previous, point, thickness, thickness, color);
         }

         previous = point;
      }
   }

   private static void astronautHelmet(HeadwearModels m, float t, int shell, int visor) {
      int trim = shade(shell, 0.72F);
      float cy = t + 4.0F;
      float r = 7.2F;
      float start = (float)Math.acos((t + 8.8F - cy) / r);

      for (int i = 0; i < 9; i++) {
         float a0 = start + ((float) Math.PI - start) * i / 9.0F;
         float a1 = start + ((float) Math.PI - start) * (i + 1) / 9.0F;
         m.frustum(0.0F, 0.0F, cy + r * Mth.cos(a0), r * Mth.sin(a0), 0.0F, 0.0F, cy + r * Mth.cos(a1), r * Mth.sin(a1), 16, shell, false, false);
      }

      m.box(-4.4F, 4.4F, t + 1.0F, t + 7.4F, -7.3F, -6.5F, trim);
      m.box(-3.9F, 3.9F, t + 1.5F, t + 6.9F, -7.4F, -7.2F, visor);
      m.box(-3.2F, -1.2F, t + 2.0F, t + 3.0F, -7.45F, -7.38F, lerp(visor, -1, 0.6));

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 6.6F, side * 7.4F, t + 2.6F, t + 4.8F, -3.4F, -1.0F, trim);
         m.box(side * 7.4F, side * 7.55F, t + 3.0F, t + 4.4F, -3.0F, -1.4F, -2880);
      }

      m.box(-6.2F, 6.2F, t + 8.6F, t + 9.9F, -6.2F, 6.2F, trim);
      m.box(-1.6F, 1.6F, t + 8.8F, t + 9.7F, -6.35F, -6.2F, -3130822);
      m.beam(HeadwearModels.p(4.6F, t - 1.6F, 3.0F), HeadwearModels.p(5.2F, t - 5.8F, 3.4F), 0.15F, 0.1F, trim);
      m.cube(5.2F, t - 6.0F, 3.4F, 0.35F, -53200);
   }

   private static void divingHelmet(HeadwearModels m, float t, int brass, int glass) {
      float cy = t + 3.8F;
      float r = 7.4F;
      float start = (float)Math.acos((t + 8.6F - cy) / r);
      int dark = shade(brass, 0.72F);

      for (int i = 0; i < 8; i++) {
         float a0 = start + ((float) Math.PI - start) * i / 8.0F;
         float a1 = start + ((float) Math.PI - start) * (i + 1) / 8.0F;
         m.frustum(
            0.0F, 0.0F, cy + r * Mth.cos(a0), r * Mth.sin(a0), 0.0F, 0.0F, cy + r * Mth.cos(a1), r * Mth.sin(a1), 16, i % 3 == 2 ? dark : brass, false, false
         );
      }

      m.box(-3.4F, 3.4F, t + 1.0F, t + 6.6F, -7.7F, -6.9F, dark);
      m.box(-2.8F, 2.8F, t + 1.6F, t + 6.0F, -7.8F, -7.6F, glass);

      for (int b = -1; b <= 1; b++) {
         m.box(b * 1.2F - 0.15F, b * 1.2F + 0.15F, t + 1.6F, t + 6.0F, -7.95F, -7.7F, brass);
      }

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 6.9F, side * 7.7F, t + 2.0F, t + 5.4F, -1.7F, 1.7F, dark);
         m.box(side * 7.6F, side * 7.8F, t + 2.5F, t + 4.9F, -1.2F, 1.2F, glass);
      }

      m.box(-6.4F, 6.4F, t + 8.0F, t + 9.6F, -6.4F, 6.4F, dark);

      for (int k = 0; k < 8; k++) {
         float a = (float) (Math.PI * 2) * k / 8.0F;
         m.cube(Mth.cos(a) * 6.5F, t + 8.8F, Mth.sin(a) * 6.5F, 0.4F, brass);
      }

      m.frustum(0.0F, 0.0F, t - 3.5F, 1.1F, 0.0F, 0.0F, t - 4.5F, 0.9F, 8, dark, true, false);
   }

   private static void iceCrown(HeadwearModels m, float t, int ice, int deep) {
      float y = t + 1.2F;
      float r = 4.9F;
      ring(m, 0.0F, y, 0.0F, r, 16, 0.45F, deep);

      for (int i = 0; i < 12; i++) {
         float a = (float) (Math.PI * 2) * i / 12.0F - (float) (Math.PI / 2);
         float front = 0.5F + 0.5F * -Mth.sin(a);
         float height = 2.2F + 4.2F * front * front + i % 2 * 0.6F;
         float x = Mth.cos(a) * r;
         float z = Mth.sin(a) * r;
         m.beam(HeadwearModels.p(x, y, z), HeadwearModels.p(x * 1.08F, y - height, z * 1.08F), 0.7F, 0.04F, i % 2 == 0 ? ice : lerp(ice, -1, 0.45));
         float half = a + (float) (Math.PI / 12);
         m.beam(
            HeadwearModels.p(Mth.cos(half) * r, y, Mth.sin(half) * r),
            HeadwearModels.p(Mth.cos(half) * r * 1.25F, y - height * 0.4F, Mth.sin(half) * r * 1.25F),
            0.35F,
            0.03F,
            lerp(ice, deep, 0.3)
         );
      }

      m.cube(0.0F, y - 0.7F, -r - 0.25F, 0.6F, deep);
      m.cube(-0.2F, y - 0.9F, -r - 0.8F, 0.2F, -1);
   }

   private static void captainHat(HeadwearModels m, float t, int white, int navy) {
      m.frustum(0.0F, 0.0F, t + 0.8F, 4.6F, 0.0F, 0.0F, t - 0.9F, 4.62F, 16, navy, false, false);
      m.frustum(0.0F, 0.0F, t - 0.9F, 4.62F, 0.0F, 0.0F, t - 2.6F, 5.7F, 16, white, false, false);
      m.frustum(0.0F, 0.0F, t - 2.6F, 5.7F, 0.0F, 0.0F, t - 2.9F, 5.5F, 16, white, true, false);
      m.prism(-4.3F, 4.3F, -7.6F, -4.2F, t + 1.3F, -4.3F, 4.3F, -7.2F, -4.2F, t + 0.8F, -15395560);
      m.box(-4.3F, 4.3F, t + 0.35F, t + 0.6F, -4.85F, -4.6F, -1525696);
      m.box(-0.18F, 0.18F, t - 2.0F, t + 0.1F, -4.95F, -4.75F, -1525696);
      m.box(-1.0F, 1.0F, t - 1.6F, t - 1.3F, -4.95F, -4.75F, -1525696);
      m.box(-1.2F, 1.2F, t - 0.2F, t + 0.1F, -4.95F, -4.75F, -1525696);
      m.box(-1.2F, -0.9F, t - 0.8F, t + 0.1F, -4.95F, -4.75F, -1525696);
      m.box(0.9F, 1.2F, t - 0.8F, t + 0.1F, -4.95F, -4.75F, -1525696);
      m.cube(0.0F, t - 2.3F, -4.85F, 0.3F, -1525696);

      for (int side = -1; side <= 1; side += 2) {
         m.cube(side * 4.45F, t + 0.45F, -3.8F, 0.3F, -1525696);
      }
   }

   private static void fedora(HeadwearModels m, float t, int felt, int band) {
      int dark = shade(felt, 0.72F);
      m.frustum(0.0F, 0.0F, t + 0.25F, 7.0F, 0.0F, 0.0F, t - 0.15F, 7.0F, 20, felt, true, true);

      for (int l = 0; l < 5; l++) {
         float t0 = l / 5.0F;
         float t1 = (l + 1) / 5.0F;
         m.frustum(0.0F, 0.0F, t - 0.15F - t0 * 4.3F, 4.45F - t0 * 0.8F, 0.0F, 0.0F, t - 0.15F - t1 * 4.3F, 4.45F - t1 * 0.8F, 16, felt, l == 4, false);
      }

      m.box(-0.35F, 0.35F, t - 4.55F, t - 4.3F, -3.0F, 3.0F, dark);

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 1.4F, side * 2.6F, t - 3.8F, t - 2.4F, -3.95F, -3.72F, dark);
      }

      m.frustum(0.0F, 0.0F, t - 0.15F, 4.52F, 0.0F, 0.0F, t - 1.4F, 4.47F, 16, band, false, false);
      m.slabAlong(HeadwearModels.p(4.4F, t - 0.8F, 1.0F), HeadwearModels.p(5.0F, t - 3.8F, 2.8F), 0.12F, 0.7F, 0.1F, 0.08F, HeadwearModels.AXIS_Z, -4703702);
   }

   private static void bowlerHat(HeadwearModels m, float t, int felt, int band) {
      m.frustum(0.0F, 0.0F, t + 0.2F, 5.9F, 0.0F, 0.0F, t - 0.15F, 5.9F, 18, felt, true, true);
      m.frustum(0.0F, 0.0F, t - 0.15F, 5.9F, 0.0F, 0.0F, t - 0.6F, 6.25F, 18, shade(felt, 1.1F), false, false);

      for (int k = 0; k < 6; k++) {
         float a0 = (float) (Math.PI / 2) * k / 6.0F;
         float a1 = (float) (Math.PI / 2) * (k + 1) / 6.0F;
         m.frustum(
            0.0F,
            0.0F,
            t - 0.15F - 4.9F * Mth.sin(a0),
            4.45F * Mth.cos(a0),
            0.0F,
            0.0F,
            t - 0.15F - 4.9F * Mth.sin(a1),
            4.45F * Mth.cos(a1),
            16,
            felt,
            false,
            false
         );
      }

      m.frustum(0.0F, 0.0F, t - 0.15F, 4.52F, 0.0F, 0.0F, t - 1.1F, 4.45F, 16, band, false, false);
   }

   private static void strawHat(HeadwearModels m, float t, int straw, int ribbon) {
      int dark = shade(straw, 0.8F);

      for (int k = 0; k < 4; k++) {
         float r0 = 4.4F + k * 1.1F;
         float r1 = r0 + 1.1F;
         m.frustum(0.0F, 0.0F, t + 0.3F + k * 0.14F, r0, 0.0F, 0.0F, t + 0.3F + (k + 1) * 0.14F - 0.02F, r1, 22, k % 2 == 0 ? straw : dark, false, false);
      }

      m.frustum(0.0F, 0.0F, t + 0.3F, 4.45F, 0.0F, 0.0F, t - 3.2F, 4.0F, 16, straw, true, false);

      for (int k = 0; k < 3; k++) {
         float y = t - 1.4F - k * 0.8F;
         m.frustum(0.0F, 0.0F, y, 4.3F - k * 0.13F, 0.0F, 0.0F, y - 0.2F, 4.28F - k * 0.13F, 16, dark, false, false);
      }

      m.frustum(0.0F, 0.0F, t + 0.3F, 4.5F, 0.0F, 0.0F, t - 0.9F, 4.47F, 16, ribbon, false, false);

      for (int side = -1; side <= 1; side += 2) {
         m.slabAlong(
            HeadwearModels.p(side * 0.6F, t - 0.3F, 4.5F),
            HeadwearModels.p(side * 1.8F, t + 3.4F, 5.6F),
            0.5F,
            0.45F,
            0.08F,
            0.08F,
            HeadwearModels.AXIS_X,
            ribbon
         );
      }
   }

   private static void witchHat(HeadwearModels m, float t, int cloth, int band) {
      m.frustum(0.0F, 0.0F, t + 0.15F, 7.4F, 0.0F, 0.0F, t - 0.35F, 7.4F, 20, cloth, true, true);
      int levels = 10;
      float height = 12.5F;

      for (int l = 0; l < levels; l++) {
         float s0 = (float)l / levels;
         float s1 = (float)(l + 1) / levels;
         float z0 = 3.6F * s0 * s0 + Math.max(0.0F, s0 - 0.72F) * 12.0F;
         float z1 = 3.6F * s1 * s1 + Math.max(0.0F, s1 - 0.72F) * 12.0F;
         float y0 = t - 0.35F - s0 * height + Math.max(0.0F, s0 - 0.72F) * 6.0F;
         float y1 = t - 0.35F - s1 * height + Math.max(0.0F, s1 - 0.72F) * 6.0F;
         m.frustum(
            0.0F,
            z0,
            y0,
            4.4F * (float)Math.pow(1.0F - s0, 1.1) + 0.1F,
            0.0F,
            z1,
            y1,
            4.4F * (float)Math.pow(1.0F - s1, 1.1) + 0.1F,
            12,
            cloth,
            l == levels - 1,
            false
         );
      }

      m.frustum(0.0F, 0.0F, t - 0.35F, 4.52F, 0.0F, 0.0F, t - 1.6F, 4.2F, 12, band, false, false);
      m.box(-1.2F, 1.2F, t - 1.55F, t - 0.3F, -4.65F, -4.45F, -1525696);
      m.box(-0.65F, 0.65F, t - 1.15F, t - 0.7F, -4.7F, -4.6F, band);
   }

   private static void stegosaurusPlates(HeadwearModels m, float t, int skin, int plate) {
      m.box(-0.9F, 0.9F, t + 0.35F, t - 0.25F, -3.8F, 4.4F, skin);

      for (int k = 0; k < 8; k++) {
         float z = -3.4F + k * 1.1F;
         float wave = Mth.sin((float) Math.PI * (k + 0.5F) / 8.0F);
         float h = 2.4F + 3.2F * wave;
         float w = 0.8F + 0.6F * wave;
         float side = k % 2 == 0 ? -1.0F : 1.0F;
         float x = side * 0.5F;
         float lean = side * 1.6F * (h / 5.6F);
         float mid = t - 0.25F - h * 0.45F;
         float midX = x + lean * 0.45F;
         m.prism(x - 0.42F, x + 0.42F, z - w * 0.6F, z + w * 0.6F, t - 0.25F, midX - 0.36F, midX + 0.36F, z - w, z + w, mid, lerp(skin, plate, 0.4));
         m.prism(midX - 0.36F, midX + 0.36F, z - w, z + w, mid, x + lean - 0.08F, x + lean + 0.08F, z - 0.1F, z + 0.1F, t - 0.25F - h, plate);
      }

      for (int side = -1; side <= 1; side += 2) {
         m.beam(HeadwearModels.p(side * 0.6F, t + 1.2F, 4.4F), HeadwearModels.p(side * 1.9F, t - 0.5F, 7.6F), 0.35F, 0.04F, plate);
         m.beam(HeadwearModels.p(side * 0.5F, t + 2.2F, 4.4F), HeadwearModels.p(side * 1.6F, t + 1.6F, 7.2F), 0.3F, 0.04F, plate);
      }
   }

   private static void chicken(HeadwearModels m, float t, int feather, int comb) {
      int beak = -1003472;
      m.box(-2.8F, 2.8F, t + 0.1F, t - 3.8F, -2.0F, 3.6F, feather);
      m.prism(-2.8F, 2.8F, -2.0F, 3.6F, t - 3.8F, -2.2F, 2.2F, -1.4F, 3.0F, t - 4.6F, feather);

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 2.8F, side * 3.3F, t - 0.6F, t - 3.2F, -1.2F, 3.0F, shade(feather, 0.9F));
         m.slabAlong(
            HeadwearModels.p(side * 0.7F, t - 3.4F, 3.3F),
            HeadwearModels.p(side * 1.4F, t - 7.2F, 5.2F),
            0.9F,
            0.5F,
            0.25F,
            0.15F,
            HeadwearModels.AXIS_X,
            shade(feather, 0.94F)
         );
         m.box(side * 2.2F, side * 2.9F, t - 5.9F, t - 6.5F, -2.6F, -2.0F, -15724528);
         m.box(side * 0.6F, side * 1.5F, t + 0.1F, t - 0.3F, -2.9F, -1.9F, beak);
      }

      m.slabAlong(HeadwearModels.p(0.0F, t - 3.4F, 3.3F), HeadwearModels.p(0.0F, t - 7.8F, 5.0F), 1.1F, 0.6F, 0.25F, 0.15F, HeadwearModels.AXIS_X, feather);
      m.box(-1.3F, 1.3F, t - 3.4F, t - 7.2F, -3.4F, -0.8F, feather);
      m.box(-0.3F, 0.3F, t - 7.2F, t - 7.9F, -3.0F, -1.7F, comb);
      m.cube(0.0F, t - 8.2F, -2.8F, 0.38F, comb);
      m.cube(0.0F, t - 8.5F, -2.2F, 0.45F, comb);
      m.cube(0.0F, t - 8.2F, -1.6F, 0.35F, comb);
      m.beam(HeadwearModels.p(0.0F, t - 5.5F, -3.4F), HeadwearModels.p(0.0F, t - 5.3F, -4.9F), 0.5F, 0.05F, beak);
      m.box(-0.28F, 0.28F, t - 4.9F, t - 4.1F, -3.7F, -3.35F, comb);
   }

   private static void rubberDuck(HeadwearModels m, float t, int yellow, int bill) {
      float[] bodyY = new float[]{t + 0.2F, t - 0.6F, t - 1.6F, t - 2.8F, t - 3.8F, t - 4.4F};
      float[] bodyR = new float[]{2.9F, 3.5F, 3.7F, 3.5F, 2.6F, 0.0F};

      for (int i = 0; i < bodyY.length - 1; i++) {
         m.frustum(0.0F, 0.8F, bodyY[i], bodyR[i], 0.0F, 0.8F, bodyY[i + 1], bodyR[i + 1], 14, yellow, false, i == 0);
      }

      m.beam(HeadwearModels.p(0.0F, t - 2.6F, 3.6F), HeadwearModels.p(0.0F, t - 4.8F, 5.3F), 1.2F, 0.15F, yellow);
      float[] headY = new float[]{t - 3.9F, t - 4.6F, t - 5.6F, t - 6.8F, t - 7.8F, t - 8.4F};
      float[] headR = new float[]{1.2F, 2.0F, 2.3F, 2.2F, 1.5F, 0.0F};

      for (int i = 0; i < headY.length - 1; i++) {
         m.frustum(0.0F, -1.4F, headY[i], headR[i], 0.0F, -1.4F, headY[i + 1], headR[i + 1], 12, yellow, false, false);
      }

      m.box(-1.0F, 1.0F, t - 5.8F, t - 5.2F, -4.9F, -3.2F, bill);
      m.box(-0.8F, 0.8F, t - 5.2F, t - 4.8F, -4.5F, -3.2F, shade(bill, 0.85F));

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 0.8F, side * 1.6F, t - 7.2F, t - 6.4F, -3.6F, -3.3F, -15724528);
         m.box(side * 1.25F, side * 1.5F, t - 7.1F, t - 6.85F, -3.65F, -3.55F, -1);
         m.slabAlong(
            HeadwearModels.p(side * 3.4F, t - 2.0F, -0.2F),
            HeadwearModels.p(side * 3.6F, t - 2.8F, 2.6F),
            1.1F,
            0.4F,
            0.3F,
            0.2F,
            HeadwearModels.AXIS_Y,
            shade(yellow, 0.88F)
         );
      }
   }

   private static void catNap(HeadwearModels m, float t, int fur, int belly, float age) {
      int stripe = shade(fur, 0.68F);
      m.box(-3.0F, 3.0F, t + 0.2F, t - 3.2F, -2.6F, 4.2F, fur);

      for (int k = 0; k < 4; k++) {
         m.box(-3.05F, 3.05F, t - 3.25F, t - 1.4F, -1.6F + k * 1.5F, -1.0F + k * 1.5F, stripe);
      }

      m.box(-2.2F, 2.2F, t + 0.1F, t - 3.6F, -5.4F, -2.4F, fur);

      for (int side = -1; side <= 1; side += 2) {
         m.beam(HeadwearModels.p(side * 1.4F, t - 3.6F, -4.1F), HeadwearModels.p(side * 1.7F, t - 5.0F, -4.1F), 0.75F, 0.05F, fur);
         m.box(side * 0.8F, side * 1.8F, t - 2.35F, t - 2.15F, -5.5F, -5.38F, -14018032);
         m.box(side * 0.5F, side * 1.9F, t + 0.25F, t - 0.45F, -6.2F, -4.8F, belly);
      }

      m.box(-1.0F, 1.0F, t - 0.6F, t - 1.8F, -5.7F, -5.3F, belly);
      m.box(-0.3F, 0.3F, t - 1.55F, t - 1.95F, -5.78F, -5.6F, -1537382);
      float twitch = Mth.sin(age * 0.25F) > 0.7F ? Mth.sin(age * 1.4F) * 0.5F : 0.0F;
      m.chain(
         new float[][]{
            HeadwearModels.p(3.0F, t - 0.6F, 3.8F),
            HeadwearModels.p(3.7F, t - 0.4F, 1.2F),
            HeadwearModels.p(3.6F, t - 0.3F, -1.6F),
            HeadwearModels.p(2.8F, t - 0.3F + twitch * 0.3F, -3.8F + twitch)
         },
         new float[]{0.7F, 0.6F, 0.55F, 0.4F},
         fur,
         stripe,
         1
      );
   }

   private static void axolotlGills(HeadwearModels m, float t, int pink, int dark, float age) {
      for (int side = -1; side <= 1; side += 2) {
         for (int k = 0; k < 3; k++) {
            float sway = Mth.sin(age * 0.1F + k + side) * 0.35F;
            float[] base = HeadwearModels.p(side * 4.3F, t + 2.4F + k * 1.3F, 0.6F + k * 0.6F);
            float[] tip = HeadwearModels.p(side * (7.4F - k * 0.4F), t - 0.6F + k * 2.8F + sway, 2.2F + k * 0.4F);
            m.beam(base, tip, 0.45F, 0.25F, pink);

            for (int s = 0; s < 5; s++) {
               float[] pt = HeadwearModels.along(base, tip, 0.25F + s * 0.17F);
               m.cube(pt[0], pt[1] - 0.45F, pt[2], 0.28F, dark);
               m.cube(pt[0], pt[1] + 0.45F, pt[2], 0.28F, dark);
            }
         }
      }
   }

   private static void anglerLure(HeadwearModels m, float t, int stalk, int glow, float age) {
      float bob = Mth.sin(age * 0.12F) * 0.5F;
      m.chain(
         new float[][]{
            HeadwearModels.p(0.0F, t + 0.3F, -2.0F),
            HeadwearModels.p(0.0F, t - 2.4F, -2.8F),
            HeadwearModels.p(0.0F, t - 4.4F, -4.6F),
            HeadwearModels.p(0.0F, t - 4.9F, -7.0F),
            HeadwearModels.p(0.0F, t - 4.0F, -9.0F),
            HeadwearModels.p(0.0F, t - 2.4F + bob, -9.8F)
         },
         new float[]{0.35F, 0.3F, 0.26F, 0.22F, 0.18F, 0.15F},
         stalk,
         stalk,
         0
      );
      m.cube(0.0F, t - 1.6F + bob, -9.8F, 0.85F, glow);
      m.beam(HeadwearModels.p(0.0F, t - 0.9F + bob, -9.8F), HeadwearModels.p(0.6F, t + 0.2F + bob, -9.6F), 0.12F, 0.04F, glow);
      m.beam(HeadwearModels.p(0.0F, t - 0.9F + bob, -9.8F), HeadwearModels.p(-0.6F, t + 0.2F + bob, -9.9F), 0.12F, 0.04F, glow);
   }

   private static void candle(HeadwearModels m, float t, int wax, int flame, float age) {
      int brass = -3628992;
      m.frustum(0.0F, 0.0F, t + 0.3F, 3.0F, 0.0F, 0.0F, t - 0.1F, 3.2F, 14, brass, true, true);
      m.frustum(0.0F, 0.0F, t - 0.1F, 3.2F, 0.0F, 0.0F, t - 0.5F, 3.35F, 14, shade(brass, 0.8F), false, false);
      m.chain(
         new float[][]{
            HeadwearModels.p(3.2F, t - 0.2F, 0.0F),
            HeadwearModels.p(4.3F, t - 0.5F, 0.0F),
            HeadwearModels.p(4.8F, t - 1.5F, 0.0F),
            HeadwearModels.p(4.3F, t - 2.4F, 0.0F),
            HeadwearModels.p(3.4F, t - 2.3F, 0.0F)
         },
         new float[]{0.25F, 0.25F, 0.25F, 0.25F, 0.25F},
         brass,
         brass,
         0
      );
      m.frustum(0.0F, 0.0F, t - 0.1F, 1.5F, 0.0F, 0.0F, t - 5.6F, 1.4F, 12, wax, true, false);

      for (int k = 0; k < 4; k++) {
         float a = (float) (Math.PI * 2) * k / 4.0F + 0.4F;
         float x = Mth.cos(a) * 1.45F;
         float z = Mth.sin(a) * 1.45F;
         m.box(x - 0.25F, x + 0.25F, t - 5.6F, t - 5.6F + 1.2F + k * 0.7F, z - 0.25F, z + 0.25F, lerp(wax, -1, 0.3));
      }

      m.box(-0.1F, 0.1F, t - 5.6F, t - 6.2F, -0.1F, 0.1F, -15066598);
      float flick = Mth.sin(age * 0.9F) * 0.15F + Mth.sin(age * 1.7F) * 0.1F;
      m.beam(HeadwearModels.p(flick, t - 6.0F, 0.0F), HeadwearModels.p(flick * 2.5F, t - 8.8F, 0.0F), 0.55F, 0.02F, flame);
      m.box(-0.22F + flick, 0.22F + flick, t - 6.2F, t - 7.0F, -0.62F, -0.5F, -2880);
      m.box(-0.22F + flick, 0.22F + flick, t - 6.2F, t - 7.0F, 0.5F, 0.62F, -2880);
   }

   private static void miniPlanet(HeadwearModels m, float t, int planet, int ring, float age) {
      float bob = Mth.sin(age * 0.08F) * 0.5F;
      float cy = t - 5.4F + bob;
      sphere(m, 0.0F, cy, 0.0F, 2.6F, 7, 12, planet, shade(planet, 0.8F), lerp(planet, -1, 0.25));
      float[] previous = null;

      for (int i = 0; i <= 24; i++) {
         float a = (float) (Math.PI * 2) * i / 24.0F;
         float x = Mth.cos(a) * 4.4F;
         float[] point = HeadwearModels.p(x, cy + x * 0.28F, Mth.sin(a) * 4.4F);
         if (previous != null) {
            m.beam(previous, point, 0.24F, 0.24F, ring);
         }

         previous = point;
      }

      float moon = age * 0.05F;
      m.cube(Mth.cos(moon) * 5.8F, cy - 1.0F, Mth.sin(moon) * 5.8F, 0.42F, -2565928);
   }

   private static void crescentMoon(HeadwearModels m, float t, int moon, int star, float age) {
      float bob = Mth.sin(age * 0.07F) * 0.4F;
      float cx = -0.6F;
      float cy = t - 6.0F + bob;
      float z = 1.0F;
      float[] previous = null;
      float previousWidth = 0.0F;

      for (int i = 0; i <= 14; i++) {
         float a = (float)Math.toRadians(100.0 + 160.0 * i / 14.0);
         float width = 0.15F + 0.85F * Mth.sin((float) Math.PI * i / 14.0F);
         float[] point = HeadwearModels.p(cx + Mth.cos(a) * 3.3F, cy + Mth.sin(a) * 3.3F, z);
         if (previous != null) {
            m.beam(previous, point, previousWidth, width, moon);
         }

         previous = point;
         previousWidth = width;
      }

      float sx = cx + 3.4F;
      float sy = cy - 2.6F;
      float spin = age * 0.06F;
      m.cube(sx, sy, z, 0.4F, star);

      for (int k = 0; k < 4; k++) {
         float a = spin + (float) (Math.PI / 2) * k;
         m.beam(HeadwearModels.p(sx, sy, z), HeadwearModels.p(sx + Mth.cos(a) * 1.4F, sy + Mth.sin(a) * 1.4F, z), 0.22F, 0.02F, star);
      }
   }

   private static void sunHalo(HeadwearModels m, float t, int gold, int orange, float age) {
      float cy = t + 2.2F;
      float z = 5.6F;
      float spin = age * 0.02F;

      for (int ringIndex = 0; ringIndex < 2; ringIndex++) {
         float r = ringIndex == 0 ? 5.2F : 4.3F;
         float[] previous = null;

         for (int i = 0; i <= 24; i++) {
            float a = (float) (Math.PI * 2) * i / 24.0F;
            float[] point = HeadwearModels.p(Mth.cos(a) * r, cy + Mth.sin(a) * r, z);
            if (previous != null) {
               m.beam(previous, point, 0.32F, 0.32F, ringIndex == 0 ? gold : orange);
            }

            previous = point;
         }
      }

      for (int k = 0; k < 16; k++) {
         float a = spin + (float) (Math.PI * 2) * k / 16.0F;
         float len = k % 2 == 0 ? 3.2F : 2.0F;
         float cos = Mth.cos(a);
         float sin = Mth.sin(a);
         m.slabAlong(
            HeadwearModels.p(cos * 5.4F, cy + sin * 5.4F, z),
            HeadwearModels.p(cos * (5.4F + len), cy + sin * (5.4F + len), z),
            0.6F,
            0.05F,
            0.15F,
            0.15F,
            new float[]{-sin, cos, 0.0F},
            k % 2 == 0 ? gold : orange
         );
      }
   }

   private static void rainbow(HeadwearModels m, float t, int cloud, int accent) {
      int[] bands = new int[]{-1560518, -1011670, -729024, -11878320, -12944672, -7714096};
      float cy = t + 3.0F;
      float z = 0.5F;

      for (int b = 0; b < bands.length; b++) {
         float r = 10.2F - b * 0.75F;

         for (int i = 0; i < 18; i++) {
            float a0 = (float) Math.PI + (float) Math.PI * i / 18.0F;
            float a1 = (float) Math.PI + (float) Math.PI * (i + 1) / 18.0F;
            float mid = (a0 + a1) / 2.0F;
            m.slabAlong(
               HeadwearModels.p(Mth.cos(a0) * r, cy + Mth.sin(a0) * r, z),
               HeadwearModels.p(Mth.cos(a1) * r, cy + Mth.sin(a1) * r, z),
               0.4F,
               0.4F,
               0.2F,
               0.2F,
               new float[]{Mth.cos(mid), Mth.sin(mid), 0.0F},
               bands[b]
            );
         }
      }

      for (int side = -1; side <= 1; side += 2) {
         m.cube(side * 7.3F, cy + 0.2F, z, 1.4F, cloud);
         m.cube(side * 8.7F, cy + 0.8F, z + 0.3F, 1.0F, accent);
         m.cube(side * 6.0F, cy + 0.9F, z - 0.3F, 1.0F, cloud);
         m.cube(side * 7.4F, cy - 1.0F, z, 0.9F, accent);
      }
   }

   private static void snowGlobe(HeadwearModels m, float t, int glass, int wood, float age) {
      m.frustum(0.0F, 0.0F, t + 0.3F, 4.2F, 0.0F, 0.0F, t - 1.4F, 3.6F, 16, wood, true, true);
      m.frustum(0.0F, 0.0F, t - 1.4F, 3.6F, 0.0F, 0.0F, t - 1.8F, 3.7F, 16, shade(wood, 1.25F), false, false);
      m.frustum(0.0F, 0.0F, t - 1.8F, 3.3F, 0.0F, 0.0F, t - 2.2F, 2.9F, 14, -722689, true, false);
      m.box(-0.25F, 0.25F, t - 2.2F, t - 2.8F, -0.25F, 0.25F, -10864094);
      m.frustum(0.0F, 0.0F, t - 2.6F, 1.7F, 0.0F, 0.0F, t - 4.8F, 0.15F, 10, -13993414, false, true);
      m.frustum(0.0F, 0.0F, t - 4.0F, 1.2F, 0.0F, 0.0F, t - 6.2F, 0.1F, 10, -12940726, false, true);
      float cy = t - 5.4F;
      float r = 3.7F;

      for (int meridian = 0; meridian < 6; meridian++) {
         float phi = (float) Math.PI * meridian / 6.0F;
         float[] previous = null;

         for (int i = 0; i <= 10; i++) {
            float theta = 0.45F + 2.6915927F * i / 10.0F;
            float[] point = HeadwearModels.p(Mth.cos(phi) * r * Mth.sin(theta), cy - r * Mth.cos(theta), Mth.sin(phi) * r * Mth.sin(theta));
            if (previous != null) {
               m.beam(previous, point, 0.08F, 0.08F, glass);
            }

            previous = point;
         }
      }

      ring(m, 0.0F, cy, 0.0F, r, 20, 0.08F, glass);

      for (int k = 0; k < 10; k++) {
         float phase = (age * 0.02F + k * 0.37F) % 1.0F;
         float a = k * 2.3F;
         float d = 0.8F + k * 7 % 5 * 0.45F;
         m.cube(Mth.cos(a) * d, cy - 3.0F + phase * 4.6F, Mth.sin(a) * d, 0.16F, -1);
      }

      m.cube(-1.8F, cy - 2.0F, -2.6F, 0.2F, -1);
   }

   private static void vrHeadset(HeadwearModels m, float t, int body, int light, float age) {
      m.box(-4.4F, 4.4F, t + 2.8F, t + 6.4F, -7.6F, -4.3F, body);
      m.box(-4.2F, 4.2F, t + 3.0F, t + 6.2F, -7.8F, -7.6F, -15066592);
      float on = 0.5F + 0.5F * Mth.sin(age * 0.2F);
      m.box(-3.4F, 3.4F, t + 4.4F, t + 4.9F, -7.9F, -7.8F, lerp(shade(light, 0.4F), light, on));

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 2.2F, side * 3.0F, t + 3.3F, t + 3.9F, -7.95F, -7.8F, -12961212);
      }

      m.squareFrustum(4.45F, t + 5.4F, 4.45F, t + 3.4F, -14013904);
      m.box(-0.8F, 0.8F, t - 0.15F, t + 0.3F, -4.4F, 4.4F, -14013904);
   }

   private static void cyberVisor(HeadwearModels m, float t, int frame, int glow, float age) {
      m.box(-4.8F, 4.8F, t + 3.4F, t + 5.6F, -5.2F, -4.4F, frame);
      m.box(-4.4F, 4.4F, t + 3.9F, t + 5.1F, -5.3F, -5.2F, shade(glow, 0.4F));
      float x = Mth.sin(age * 0.15F) * 3.4F;
      m.box(x - 0.9F, x + 0.9F, t + 3.9F, t + 5.1F, -5.36F, -5.26F, glow);

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 4.4F, side * 5.0F, t + 3.4F, t + 5.6F, -4.4F, 1.5F, frame);
         float inner = Math.min(side * 4.9F, side * 5.2F);
         float outer = Math.max(side * 4.9F, side * 5.2F);
         m.prism(inner, outer, -0.5F, 1.5F, t + 3.4F, inner + 0.05F, outer - 0.05F, 1.0F, 2.4F, t + 1.4F, frame);
         m.box(side * 5.0F, side * 5.12F, t + 4.2F, t + 4.8F, 0.2F, 1.0F, glow);
      }
   }

   private static void shades(HeadwearModels m, float t, int lens, int frame) {
      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 0.55F, side * 3.8F, t + 3.7F, t + 5.8F, -4.78F, -4.58F, lens);
         m.box(side * 0.55F, side * 3.8F, t + 3.45F, t + 3.72F, -4.84F, -4.58F, frame);
         m.box(side * 3.8F, side * 4.05F, t + 3.45F, t + 5.3F, -4.84F, -4.58F, frame);
         m.box(side * 4.3F, side * 4.52F, t + 3.6F, t + 3.9F, -4.6F, 1.8F, frame);
         m.box(side * 1.1F, side * 1.7F, t + 4.0F, t + 4.5F, -4.83F, -4.79F, -1511169);
      }

      m.box(-0.55F, 0.55F, t + 3.9F, t + 4.2F, -4.84F, -4.6F, frame);
   }

   private static void eyepatch(HeadwearModels m, float t, int patch, int strap) {
      m.box(-3.7F, -0.9F, t + 3.5F, t + 6.2F, -4.78F, -4.52F, patch);
      m.box(-3.4F, -1.2F, t + 3.8F, t + 5.9F, -4.82F, -4.78F, shade(patch, 1.5F));
      float[][] path = new float[][]{
         HeadwearModels.p(-0.9F, t + 3.6F, -4.6F),
         HeadwearModels.p(4.58F, t + 1.6F, -4.58F),
         HeadwearModels.p(4.58F, t + 2.4F, 4.58F),
         HeadwearModels.p(-4.58F, t + 4.4F, 4.58F),
         HeadwearModels.p(-4.58F, t + 4.8F, -4.58F),
         HeadwearModels.p(-3.7F, t + 4.3F, -4.6F)
      };

      for (int i = 0; i < path.length - 1; i++) {
         m.beam(path[i], path[i + 1], 0.2F, 0.2F, strap);
      }
   }

   private static void arrowGag(HeadwearModels m, float t, int shaft, int fletch) {
      m.box(-10.0F, -4.4F, t + 2.6F, t + 3.2F, 0.3F, 0.9F, shaft);
      m.box(4.4F, 8.6F, t + 2.6F, t + 3.2F, 0.3F, 0.9F, shaft);
      m.beam(HeadwearModels.p(8.4F, t + 2.9F, 0.6F), HeadwearModels.p(11.0F, t + 2.9F, 0.6F), 0.95F, 0.02F, -5723984);
      m.slabAlong(HeadwearModels.p(-10.2F, t + 2.9F, 0.6F), HeadwearModels.p(-7.2F, t + 2.9F, 0.6F), 1.4F, 0.4F, 0.06F, 0.06F, HeadwearModels.AXIS_Y, fletch);
      m.slabAlong(
         HeadwearModels.p(-10.2F, t + 2.9F, 0.6F),
         HeadwearModels.p(-7.2F, t + 2.9F, 0.6F),
         1.4F,
         0.4F,
         0.06F,
         0.06F,
         HeadwearModels.AXIS_Z,
         lerp(fletch, -1, 0.6)
      );
      m.box(-10.5F, -10.0F, t + 2.6F, t + 3.2F, 0.3F, 0.9F, shade(shaft, 0.6F));
   }

   private static void robotAntenna(HeadwearModels m, float t, int metal, int light, float age) {
      int dark = shade(metal, 0.7F);
      m.box(-3.0F, 3.0F, t + 0.3F, t - 0.3F, -3.0F, 3.0F, metal);

      for (int cx = -1; cx <= 1; cx += 2) {
         for (int cz = -1; cz <= 1; cz += 2) {
            m.cube(cx * 2.4F, t - 0.4F, cz * 2.4F, 0.25F, dark);
         }
      }

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 4.3F, side * 5.3F, t + 4.0F, t + 6.0F, -1.0F, 1.0F, metal);
         m.box(side * 5.3F, side * 5.6F, t + 4.5F, t + 5.5F, -0.5F, 0.5F, dark);
      }

      float[] previous = HeadwearModels.p(0.0F, t - 0.3F, 0.0F);

      for (int i = 1; i <= 8; i++) {
         float[] point = HeadwearModels.p(i % 2 == 0 ? -0.35F : 0.35F, t - 0.3F - i * 0.55F, 0.0F);
         m.beam(previous, point, 0.1F, 0.1F, dark);
         previous = point;
      }

      m.beam(HeadwearModels.p(0.0F, t - 4.7F, 0.0F), HeadwearModels.p(0.0F, t - 6.4F, 0.0F), 0.12F, 0.1F, metal);
      boolean on = (int)(age * 0.1F) % 2 == 0;
      m.cube(0.0F, t - 6.8F, 0.0F, 0.55F, on ? light : shade(light, 0.4F));
   }

   private static void octopus(HeadwearModels m, float t, int skin, int spots, float age) {
      float[] ys = new float[]{t - 0.4F, t - 1.4F, t - 2.6F, t - 3.8F, t - 5.0F, t - 6.0F, t - 6.9F, t - 7.4F};
      float[] rs = new float[]{3.6F, 3.9F, 3.8F, 3.4F, 2.8F, 1.9F, 0.9F, 0.0F};

      for (int i = 0; i < ys.length - 1; i++) {
         m.frustum(0.0F, i * 0.25F, ys[i], rs[i], 0.0F, (i + 1) * 0.25F, ys[i + 1], rs[i + 1], 12, skin, false, i == 0);
      }

      for (int k = 0; k < 7; k++) {
         float a = k * 2.4F;
         float y = t - 1.6F - k * 0.7F;
         float r = rs[Math.min(ys.length - 1, 1 + k / 2)] + 0.05F;
         m.cube(Mth.cos(a) * r, y, Mth.sin(a) * r + k * 0.12F, 0.3F, spots);
      }

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 0.9F, side * 2.7F, t - 0.9F, t - 2.9F, -4.0F, -3.1F, -723728);
         m.box(side * 1.3F, side * 2.3F, t - 1.3F, t - 2.4F, -4.1F, -3.95F, -15724528);
      }

      for (int k = 0; k < 8; k++) {
         float a = (float) (Math.PI * 2) * k / 8.0F + (float) (Math.PI / 8);
         float front = -Mth.sin(a);
         int steps = front > 0.5F ? 2 : 5;
         float[][] points = new float[steps + 1][];
         float[] radii = new float[steps + 1];

         for (int s = 0; s <= steps; s++) {
            float curl = 0.8F * Mth.sin(age * 0.1F + k + s * 0.8F);
            float r = 3.3F + s * 0.7F + curl * s * 0.2F;
            points[s] = HeadwearModels.p(Mth.cos(a) * r, t + 0.2F + s * 1.4F, Mth.sin(a) * r);
            radii[s] = 0.6F - s * 0.09F;
         }

         m.chain(points, radii, skin, skin, 0);

         for (int s = 1; s < steps; s++) {
            m.cube(points[s][0] * 0.94F, points[s][1] + 0.4F, points[s][2] * 0.94F, 0.16F, spots);
         }
      }
   }

   private static void pottedCactus(HeadwearModels m, float t, int green, int pot) {
      m.frustum(0.0F, 0.0F, t + 0.2F, 3.0F, 0.0F, 0.0F, t - 3.0F, 3.6F, 12, pot, false, true);
      m.frustum(0.0F, 0.0F, t - 3.0F, 3.9F, 0.0F, 0.0F, t - 3.8F, 3.9F, 12, shade(pot, 1.12F), true, false);
      m.frustum(0.0F, 0.0F, t - 3.8F, 3.4F, 0.0F, 0.0F, t - 3.9F, 3.4F, 12, -11915232, true, false);
      m.frustum(0.0F, 0.0F, t - 3.9F, 1.4F, 0.0F, 0.0F, t - 9.4F, 1.3F, 10, green, false, false);
      m.frustum(0.0F, 0.0F, t - 9.4F, 1.3F, 0.0F, 0.0F, t - 10.3F, 0.5F, 10, green, true, false);
      m.beam(HeadwearModels.p(-1.2F, t - 6.0F, 0.0F), HeadwearModels.p(-3.0F, t - 6.0F, 0.0F), 0.6F, 0.6F, green);
      m.beam(HeadwearModels.p(-3.0F, t - 5.7F, 0.0F), HeadwearModels.p(-3.0F, t - 8.4F, 0.0F), 0.6F, 0.5F, green);
      m.beam(HeadwearModels.p(1.2F, t - 5.0F, 0.2F), HeadwearModels.p(2.6F, t - 5.0F, 0.2F), 0.55F, 0.55F, green);
      m.beam(HeadwearModels.p(2.6F, t - 4.8F, 0.2F), HeadwearModels.p(2.6F, t - 6.9F, 0.2F), 0.55F, 0.45F, green);

      for (int k = 0; k < 16; k++) {
         float a = k * 2.4F;
         float y = t - 4.4F - k * 7 % 11 * 0.45F;
         m.cube(Mth.cos(a) * 1.5F, y, Mth.sin(a) * 1.5F, 0.08F, -724768);
      }

      m.cube(0.0F, t - 10.5F, 0.0F, 0.5F, -1021286);

      for (int k = 0; k < 4; k++) {
         float a = (float) (Math.PI / 2) * k;
         m.cube(Mth.cos(a) * 0.7F, t - 10.4F, Mth.sin(a) * 0.7F, 0.35F, -750928);
      }

      m.cube(0.0F, t - 11.0F, 0.0F, 0.22F, -8128);
   }

   private static void birdNest(HeadwearModels m, float t, int twigs, int egg, float age) {
      m.frustum(0.0F, 0.0F, t + 0.2F, 2.6F, 0.0F, 0.0F, t - 0.4F, 3.0F, 12, shade(twigs, 0.6F), false, true);

      for (int ringIndex = 0; ringIndex < 4; ringIndex++) {
         float y = t - 0.2F - ringIndex * 0.55F;
         float r = 3.2F + ringIndex * 0.35F;

         for (int i = 0; i < 12; i++) {
            float a0 = (float) (Math.PI * 2) * i / 12.0F + ringIndex * 0.3F;
            float a1 = a0 + 0.7330383F;
            float jitter = ((i * 7 + ringIndex * 3) % 5 - 2) * 0.12F;
            m.beam(
               HeadwearModels.p(Mth.cos(a0) * r, y + jitter, Mth.sin(a0) * r),
               HeadwearModels.p(Mth.cos(a1) * (r + 0.2F), y - jitter, Mth.sin(a1) * (r + 0.2F)),
               0.28F,
               0.22F,
               (i + ringIndex) % 2 == 0 ? twigs : shade(twigs, 0.8F)
            );
         }
      }

      float[][] eggs = new float[][]{{-0.9F, -0.4F}, {0.9F, -0.2F}, {0.0F, 0.9F}};

      for (float[] e : eggs) {
         sphere(m, e[0], t - 1.2F, e[1], 0.85F, 4, 8, egg);
         m.cube(e[0] + 0.3F, t - 1.5F, e[1] - 0.75F, 0.1F, shade(egg, 0.6F));
      }

      float hop = Math.max(0.0F, Mth.sin(age * 0.2F)) * 0.3F;
      int blue = -12944672;
      m.cube(3.6F, t - 3.0F - hop, 0.4F, 0.85F, blue);
      m.cube(3.6F, t - 2.6F - hop, 0.4F, 0.55F, -1535926);
      m.cube(3.4F, t - 4.2F - hop, -0.2F, 0.6F, blue);
      m.beam(HeadwearModels.p(3.4F, t - 4.1F - hop, -0.8F), HeadwearModels.p(3.3F, t - 4.0F - hop, -1.6F), 0.2F, 0.02F, -1003472);
      m.slabAlong(
         HeadwearModels.p(3.8F, t - 3.1F - hop, 1.2F),
         HeadwearModels.p(4.2F, t - 3.8F - hop, 2.6F),
         0.5F,
         0.3F,
         0.1F,
         0.1F,
         HeadwearModels.AXIS_X,
         shade(blue, 0.8F)
      );
   }

   private static void burger(HeadwearModels m, float t, int bun, int patty) {
      int cheese = -737232;
      m.frustum(0.0F, 0.0F, t + 0.25F, 4.4F, 0.0F, 0.0F, t - 0.9F, 4.5F, 16, bun, true, true);
      m.frustum(0.0F, 0.0F, t - 0.9F, 4.8F, 0.0F, 0.0F, t - 2.0F, 4.8F, 16, patty, true, true);
      m.box(-4.9F, 4.9F, t - 2.0F, t - 2.35F, -4.9F, 4.9F, cheese);
      m.box(4.6F, 4.95F, t - 2.2F, t - 1.0F, -1.2F, 0.0F, cheese);
      m.box(-4.95F, -4.6F, t - 2.2F, t - 0.6F, 1.0F, 2.2F, cheese);
      m.box(-1.8F, -0.6F, t - 2.2F, t - 1.2F, -4.95F, -4.6F, cheese);
      m.frustum(0.0F, 0.0F, t - 2.35F, 5.1F, 0.0F, 0.0F, t - 2.65F, 4.6F, 14, -10833862, false, true);

      for (int k = 0; k < 14; k++) {
         float a = (float) (Math.PI * 2) * k / 14.0F;
         m.cube(Mth.cos(a) * 5.0F, t - 2.4F + k % 2 * 0.25F, Mth.sin(a) * 5.0F, 0.35F, -9781174);
      }

      m.frustum(0.0F, 0.0F, t - 2.65F, 4.3F, 0.0F, 0.0F, t - 3.05F, 4.3F, 14, -2606550, true, false);

      for (int k = 0; k < 5; k++) {
         float a0 = (float) (Math.PI / 2) * k / 5.0F;
         float a1 = (float) (Math.PI / 2) * (k + 1) / 5.0F;
         m.frustum(
            0.0F,
            0.0F,
            t - 3.05F - 2.8F * Mth.sin(a0),
            4.6F * Mth.cos(a0),
            0.0F,
            0.0F,
            t - 3.05F - 2.8F * Mth.sin(a1),
            4.6F * Mth.cos(a1),
            16,
            bun,
            false,
            false
         );
      }

      for (int k = 0; k < 10; k++) {
         float a = k * 2.4F;
         float rr = 1.0F + k * 3 % 4 * 0.8F;
         float x = Mth.cos(a) * rr;
         float z = Mth.sin(a) * rr;
         float y = t - 3.05F - 2.8F * Mth.sqrt(Math.max(0.0F, 1.0F - rr / 4.6F * (rr / 4.6F)));
         m.box(x - 0.22F, x + 0.22F, y + 0.05F, y - 0.12F, z - 0.12F, z + 0.12F, -462628);
      }
   }

   private static void tinFoilHat(HeadwearModels m, float t, int foil, int dark) {
      m.frustum(0.0F, 0.0F, t + 0.4F, 4.95F, 0.0F, 0.0F, t - 0.3F, 4.75F, 7, lerp(foil, -1, 0.3F), false, false);
      float px = 0.0F;
      float pz = 0.0F;

      for (int l = 0; l < 8; l++) {
         float s0 = l / 8.0F;
         float s1 = (l + 1) / 8.0F;
         float nx = Mth.sin(l * 12.9898F) * 0.35F;
         float nz = Mth.cos(l * 7.233F) * 0.35F;
         m.frustum(
            px,
            pz,
            t - 0.3F - s0 * 7.0F,
            4.7F * (1.0F - s0) + 0.15F + l % 2 * 0.25F,
            nx,
            nz,
            t - 0.3F - s1 * 7.0F,
            4.7F * (1.0F - s1) + 0.15F,
            7,
            l % 2 == 0 ? foil : dark,
            l == 7,
            false
         );
         px = nx;
         pz = nz;
      }
   }

   private static void spartanHelmet(HeadwearModels m, float t, int bronze, int crest) {
      int dark = shade(bronze, 0.72F);
      m.squareFrustum(4.8F, t + 1.0F, 4.1F, t - 0.9F, bronze);
      m.box(-4.8F, 4.8F, t + 1.0F, t + 8.2F, 4.1F, 4.8F, bronze);

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 4.1F, side * 4.8F, t + 1.0F, t + 8.2F, -4.8F, 4.1F, bronze);
         m.box(side * 1.3F, side * 4.1F, t + 5.3F, t + 8.4F, -4.9F, -4.1F, bronze);
      }

      m.box(-4.8F, 4.8F, t + 1.0F, t + 3.4F, -4.9F, -4.1F, bronze);
      m.box(-0.55F, 0.55F, t + 3.4F, t + 6.4F, -5.0F, -4.2F, bronze);
      m.box(-4.85F, 4.85F, t + 1.0F, t + 1.3F, -4.95F, 4.85F, dark);
      m.box(-0.5F, 0.5F, t - 0.9F, t - 1.6F, -3.0F, 4.0F, dark);

      for (int k = 0; k < 12; k++) {
         float z0 = -4.4F + k * 0.95F;
         float h = 3.6F + 1.4F * Mth.sin((float) Math.PI * k / 11.0F);
         m.box(-0.7F, 0.7F, t - 1.6F, t - 1.6F - h, z0, z0 + 0.95F, k % 2 == 0 ? crest : shade(crest, 0.85F));
      }

      m.box(-0.6F, 0.6F, t - 1.6F, t + 3.6F, 6.0F, 6.9F, crest);
   }

   private static void pharaohHeaddress(HeadwearModels m, float t, int blue, int gold) {
      m.squareFrustum(4.7F, t + 2.4F, 4.2F, t - 0.6F, blue);

      for (int k = 0; k < 3; k++) {
         float y = t + 1.7F - k * 1.0F;
         float r = 4.7F - (t + 2.4F - y) / 3.0F * 0.5F + 0.05F;
         m.squareFrustum(r, y + 0.2F, r - 0.03F, y - 0.2F, gold);
      }

      m.box(-4.8F, 4.8F, t + 1.8F, t + 2.6F, -4.85F, -4.6F, gold);
      m.box(-4.4F, 4.4F, t + 2.4F, t + 8.8F, 4.2F, 5.0F, blue);
      m.prism(-2.0F, 2.0F, 4.4F, 5.2F, t + 11.0F, -3.0F, 3.0F, 4.4F, 5.0F, t + 8.8F, blue);

      for (int side = -1; side <= 1; side += 2) {
         float in0 = Math.min(side * 4.2F, side * 5.4F);
         float out0 = Math.max(side * 4.2F, side * 5.4F);
         float in1 = Math.min(side * 4.3F, side * 5.0F);
         float out1 = Math.max(side * 4.3F, side * 5.0F);
         m.prism(in0, out0, -3.4F, -1.0F, t + 11.0F, in1, out1, -2.2F, -0.4F, t + 2.4F, blue);

         for (int k = 0; k < 5; k++) {
            float y = t + 3.4F + k * 1.6F;
            float f = (y - (t + 2.4F)) / 8.6F;
            float a = Math.min(side * (4.25F - 0.05F * f), side * (5.05F + 0.4F * f));
            float b = Math.max(side * (4.25F - 0.05F * f), side * (5.05F + 0.4F * f));
            m.box(a - 0.05F, b + 0.05F, y, y + 0.5F, -2.3F - 1.15F * f, -0.35F - 0.65F * f, gold);
         }
      }

      m.chain(
         new float[][]{
            HeadwearModels.p(0.0F, t + 1.8F, -4.9F),
            HeadwearModels.p(0.0F, t + 0.6F, -5.3F),
            HeadwearModels.p(0.0F, t - 0.4F, -5.1F),
            HeadwearModels.p(0.0F, t - 0.9F, -5.5F)
         },
         new float[]{0.35F, 0.3F, 0.28F, 0.2F},
         gold,
         gold,
         0
      );
      m.slabAlong(HeadwearModels.p(0.0F, t + 0.3F, -5.25F), HeadwearModels.p(0.0F, t - 0.8F, -5.35F), 0.95F, 0.5F, 0.08F, 0.08F, HeadwearModels.AXIS_X, gold);
   }

   private static void deerstalker(HeadwearModels m, float t, int tweed, int dark) {
      int check = lerp(tweed, dark, 0.35);

      for (int k = 0; k < 5; k++) {
         float a0 = (float) (Math.PI / 2) * k / 5.0F;
         float a1 = (float) (Math.PI / 2) * (k + 1) / 5.0F;
         m.frustum(
            0.0F,
            0.0F,
            t + 1.0F - 3.6F * Mth.sin(a0),
            4.62F * Mth.cos(a0),
            0.0F,
            0.0F,
            t + 1.0F - 3.6F * Mth.sin(a1),
            4.62F * Mth.cos(a1),
            16,
            k % 2 == 0 ? tweed : check,
            false,
            false
         );
      }

      m.prism(-3.4F, 3.4F, -7.2F, -4.4F, t + 1.4F, -3.4F, 3.4F, -6.8F, -4.4F, t + 0.9F, tweed);
      m.prism(-3.4F, 3.4F, 4.4F, 7.2F, t + 1.4F, -3.4F, 3.4F, 4.4F, 6.8F, t + 0.9F, tweed);

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 3.4F, side * 4.7F, t - 1.9F, t - 1.3F, -1.8F, 1.8F, check);
         m.beam(HeadwearModels.p(0.0F, t - 2.9F, 0.0F), HeadwearModels.p(side * 1.3F, t - 3.3F, 0.0F), 0.32F, 0.2F, dark);
      }

      m.cube(0.0F, t - 2.9F, 0.0F, 0.4F, dark);
   }

   private static void firefighterHelmet(HeadwearModels m, float t, int red, int gold) {
      for (int k = 0; k < 6; k++) {
         float a0 = (float) (Math.PI / 2) * k / 6.0F;
         float a1 = (float) (Math.PI / 2) * (k + 1) / 6.0F;
         m.frustum(
            0.0F,
            0.0F,
            t + 1.2F - 3.4F * Mth.sin(a0),
            4.85F * Mth.cos(a0),
            0.0F,
            0.0F,
            t + 1.2F - 3.4F * Mth.sin(a1),
            4.85F * Mth.cos(a1),
            16,
            red,
            false,
            false
         );
      }

      for (int c = -1; c <= 1; c++) {
         m.chain(
            new float[][]{
               HeadwearModels.p(c * 1.3F, t + 0.6F, -4.7F),
               HeadwearModels.p(c * 1.3F, t - 1.8F, -3.2F),
               HeadwearModels.p(c * 1.3F, t - 2.4F, 0.0F),
               HeadwearModels.p(c * 1.3F, t - 1.8F, 3.2F),
               HeadwearModels.p(c * 1.3F, t + 0.6F, 4.7F)
            },
            new float[]{0.25F, 0.25F, 0.25F, 0.25F, 0.25F},
            shade(red, 0.8F),
            shade(red, 0.8F),
            0
         );
      }

      m.frustum(0.0F, 0.0F, t + 1.4F, 5.4F, 0.0F, 0.0F, t + 1.0F, 5.4F, 18, shade(red, 0.82F), true, true);
      m.prism(-4.8F, 4.8F, 6.0F, 9.4F, t + 2.8F, -4.8F, 4.8F, 4.6F, 8.0F, t + 1.3F, red);
      m.box(-0.5F, 0.5F, t - 2.4F, t - 0.4F, -5.2F, -4.6F, shade(gold, 0.8F));
      m.box(-2.0F, 2.0F, t - 2.8F, t + 0.6F, -5.6F, -5.2F, gold);
      m.box(-1.4F, 1.4F, t - 2.2F, t + 0.0F, -5.66F, -5.56F, red);
      m.box(-0.2F, 0.2F, t - 1.8F, t - 0.4F, -5.72F, -5.62F, gold);
   }

   private static void earmuffs(HeadwearModels m, float t, int fluff, int band) {
      float lift = t + 8.25F;
      float[] previous = null;

      for (int i = 0; i <= 12; i++) {
         double a = Math.PI * i / 12.0;
         float[] point = HeadwearModels.p((float)Math.cos(a) * 5.0F, -4.2F - (float)Math.sin(a) * 5.0F + lift, 0.4F);
         if (previous != null) {
            m.slabAlong(previous, point, 0.7F, 0.7F, 0.3F, 0.3F, HeadwearModels.AXIS_Z, band);
         }

         previous = point;
      }

      for (int side = -1; side <= 1; side += 2) {
         m.cube(side * 5.2F, t + 4.8F, 0.4F, 1.5F, fluff);
         m.cube(side * 5.5F, t + 3.4F, 0.4F, 1.0F, fluff);
         m.cube(side * 5.5F, t + 6.2F, 0.4F, 1.0F, fluff);
         m.cube(side * 5.5F, t + 4.8F, -1.0F, 1.0F, fluff);
         m.cube(side * 5.5F, t + 4.8F, 1.8F, 1.0F, fluff);
         m.cube(side * 6.2F, t + 4.8F, 0.4F, 1.0F, shade(fluff, 0.94F));
      }
   }

   private static void ushanka(HeadwearModels m, float t, int fur, int cloth) {
      m.squareFrustum(4.75F, t + 1.2F, 4.3F, t - 2.2F, cloth);
      m.box(-4.95F, 4.95F, t - 0.9F, t + 1.9F, -5.5F, -4.6F, fur);
      m.box(-4.95F, 4.95F, t + 0.2F, t + 1.9F, 4.6F, 5.5F, fur);

      for (int side = -1; side <= 1; side += 2) {
         m.box(side * 4.6F, side * 5.5F, t + 0.4F, t + 6.6F, -2.6F, 3.4F, fur);
         m.cube(side * 5.55F, t + 3.0F, 0.4F, 0.3F, shade(fur, 0.85F));
      }

      m.cube(-2.6F, t + 0.2F, -5.55F, 0.25F, shade(fur, 0.85F));
      m.cube(2.9F, t + 1.1F, -5.55F, 0.25F, shade(fur, 0.85F));
      m.box(-0.8F, 0.8F, t - 0.3F, t + 1.1F, -5.62F, -5.5F, -2606550);
      m.cube(0.0F, t + 0.4F, -5.66F, 0.25F, -1525696);
   }

   private static void brokenHalo(HeadwearModels m, float t, int stone, int dark, float age) {
      float bob = Mth.sin(age * 0.06F) * 0.3F;
      float y = t - 3.2F + bob;
      float r = 4.2F;
      float[] previous = null;

      for (int i = 0; i <= 20; i++) {
         float a = (float) (Math.PI * 2) * i / 20.0F;
         float[] point = HeadwearModels.p(Mth.cos(a) * r, y - Mth.sin(a) * r * 0.4F + Mth.cos(a) * r * 0.12F, Mth.sin(a) * r);
         if (previous != null && (i < 14 || i > 16)) {
            float thick = i % 5 == 0 ? 0.22F : 0.34F;
            m.beam(previous, point, thick, thick, i % 3 == 0 ? lerp(stone, dark, 0.3) : stone);
         }

         previous = point;
      }

      for (int k = 0; k < 3; k++) {
         float a = (float) (Math.PI * 2) * (14.5F + k * 0.7F) / 20.0F;
         float drift = Mth.sin(age * 0.05F + k * 2.0F) * 0.4F;
         float rr = r + 0.9F + k * 0.3F + drift;
         m.cube(Mth.cos(a) * rr, y - Mth.sin(a) * rr * 0.4F - 0.6F - k * 0.5F + drift, Mth.sin(a) * rr, 0.34F - k * 0.05F, k == 1 ? dark : stone);
      }
   }

   private static void nightcap(HeadwearModels m, float t, int cloth, int pom, float age) {
      m.frustum(0.0F, 0.0F, t + 0.9F, 4.6F, 0.0F, 0.0F, t - 0.4F, 4.62F, 16, pom, false, false);
      int segments = 10;
      float[] previous = null;
      float previousRadius = 0.0F;

      for (int s = 0; s <= segments; s++) {
         float f = (float)s / segments;
         float droop = Mth.sin(age * 0.05F) * 0.3F * f;
         float[] center = HeadwearModels.p(f * f * 5.4F, t - 0.4F - 6.0F * f + 7.2F * f * f * f + droop, 0.8F * f);
         float radius = 4.4F * (float)Math.pow(1.0F - f, 0.9) + 0.25F;
         if (previous != null) {
            m.frustum(
               previous[0],
               previous[2],
               previous[1],
               previousRadius,
               center[0],
               center[2],
               center[1],
               radius,
               12,
               s / 2 % 2 == 0 ? cloth : lerp(cloth, -1, 0.25),
               false,
               false
            );
         }

         previous = center;
         previousRadius = radius;
      }

      m.cube(previous[0], previous[1] + 0.6F, previous[2], 1.0F, pom);

      for (int k = 0; k < 3; k++) {
         float phase = (age * 0.015F + k / 3.0F) % 1.0F;
         float size = (0.9F + phase * 0.6F) * (1.0F - phase * 0.4F);
         float zx = -3.0F - phase * 2.0F;
         float zy = t - 6.0F - phase * 6.0F;
         m.box(zx - size, zx + size, zy - size - 0.12F, zy - size + 0.12F, -1.1F, -0.9F, -723720);
         m.box(zx - size, zx + size, zy + size - 0.12F, zy + size + 0.12F, -1.1F, -0.9F, -723720);
         m.beam(HeadwearModels.p(zx + size, zy - size, -1.0F), HeadwearModels.p(zx - size, zy + size, -1.0F), 0.12F, 0.12F, -723720);
      }
   }
}
