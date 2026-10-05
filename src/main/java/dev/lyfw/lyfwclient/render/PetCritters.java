package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import net.minecraft.util.Mth;

final class PetCritters {
   private static final int BLACK = -14804455;
   private static final int WHITE = -1;
   private static final int PINK = -876368;
   private static final float PI = (float) Math.PI;

   private PetCritters() {
   }

   static int[] natural(CosmeticsModule.Pet kind) {
      int[] c = switch (kind) {
         case TURTLE -> new int[]{6267738, 4091450, 13219952};
         case TORTOISE -> new int[]{10259036, 7232054, 14202992};
         case FROG -> new int[]{6012746, 4098612, 15921352};
         case T_REX -> new int[]{6986314, 4877876, 14734496};
         case SNAKE -> new int[]{5220426, 3042858, 15263904};
         case SEAL -> new int[]{10134190, 8028814, 13160150};
         case BUTTERFLY -> new int[]{15901242, 1973794, 16777215};
         case BEE -> new int[]{16106776, 1973276, 15267071};
         case LADYBUG -> new int[]{14692394, 1973276, 16777215};
         case BAT -> new int[]{4865612, 3024430, 15249600};
         case JELLYFISH -> new int[]{13213936, 15906536, 16777215};
         case GOLDFISH -> new int[]{15895082, 16757850, 16773340};
         case OCTOPUS -> new int[]{14837850, 13125692, 16171184};
         case CRAB -> new int[]{14831162, 12072234, 16180950};
         case SNAIL -> new int[]{14272680, 11561530, 9062946};
         case DRAGONFLY -> new int[]{2797768, 1986186, 14218495};
         case PUFFERFISH -> new int[]{15913834, 13213760, 16776170};
         case WHALE -> new int[]{4091568, 3034752, 14214898};
         case SHARK -> new int[]{8030874, 6188670, 15922422};
         case NARWHAL -> new int[]{10269896, 7243418, 15134452};
         case SEAHORSE -> new int[]{15901242, 14711338, 16769192};
         case STARFISH -> new int[]{15891034, 14178874, 16765624};
         case CATERPILLAR -> new int[]{8047434, 15913546, 3832362};
         default -> new int[]{10526880, 7368816, 14737632};
      };
      return new int[]{0xFF000000 | c[0], 0xFF000000 | c[1], 0xFF000000 | c[2]};
   }

   static void draw(CosmeticsModule.Pet kind, PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      switch (kind) {
         case TURTLE:
            shell(k, mo, p, false);
            break;
         case TORTOISE:
            shell(k, mo, p, true);
            break;
         case FROG:
            frog(k, mo, p);
            break;
         case T_REX:
            tRex(k, mo, p);
            break;
         case SNAKE:
            snake(k, mo, p);
            break;
         case SEAL:
            seal(k, mo, p);
            break;
         case BUTTERFLY:
            butterfly(k, mo, p);
            break;
         case BEE:
            bee(k, mo, p);
            break;
         case LADYBUG:
            ladybug(k, mo, p);
            break;
         case BAT:
            bat(k, mo, p);
            break;
         case JELLYFISH:
            jellyfish(k, mo, p);
            break;
         case GOLDFISH:
            goldfish(k, mo, p);
            break;
         case OCTOPUS:
            octopus(k, mo, p);
            break;
         case CRAB:
            crab(k, mo, p);
            break;
         case SNAIL:
            snail(k, mo, p);
            break;
         case DRAGONFLY:
            dragonfly(k, mo, p);
            break;
         case PUFFERFISH:
            pufferfish(k, mo, p);
            break;
         case WHALE:
            whale(k, mo, p);
            break;
         case SHARK:
            shark(k, mo, p);
            break;
         case NARWHAL:
            narwhal(k, mo, p);
            break;
         case SEAHORSE:
            seahorse(k, mo, p);
            break;
         case STARFISH:
            starfish(k, mo, p);
            break;
         case CATERPILLAR:
            caterpillar(k, mo, p);
      }
   }

   private static void eyes(PetModels.Kit k, float gap, float size, float h, float face) {
      k.eye(-gap - size, -gap, h, h + size * 1.1F, face, -14804455);
      k.eye(gap, gap + size, h, h + size * 1.1F, face, -14804455);
   }

   private static float hover(PetModels.Motion mo, float height) {
      return height + Mth.sin(mo.age() * 0.1F) * 0.7F;
   }

   private static void shell(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, boolean tortoise) {
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() * 0.5F;
      float bot = tortoise ? 1.8F : 1.2F;
      float bw = 2.6F;
      float bl = 3.0F;
      float dome = tortoise ? 3.2F : 2.2F;

      for (int sx = -1; sx <= 1; sx += 2) {
         for (int sz = -1; sz <= 1; sz += 2) {
            k.beam(
               sx * (bw - 0.6F), bot + 0.4F, sz * (bl - 0.8F), sx * (bw + 0.4F), 0.4F, sz * (bl - 0.8F) - sx * sz * s * 1.2F, tortoise ? 0.75F : 0.6F, p.main()
            );
         }
      }

      k.box(-bw + 0.3F, bw - 0.3F, bot - 0.3F, bot + 0.5F, -bl + 0.3F, bl - 0.3F, p.accent());
      k.box(-bw, bw, bot + 0.4F + br, bot + 0.4F + dome * 0.55F + br, -bl, bl, p.second());
      k.box(-bw + 0.8F, bw - 0.8F, bot + 0.4F + dome * 0.55F + br, bot + 0.4F + dome + br, -bl + 0.9F, bl - 0.9F, p.second());
      k.box(-bw - 0.06F, bw + 0.06F, bot + 0.4F + br, bot + 0.8F + br, -bl - 0.06F, bl + 0.06F, p.accent());
      float roof = bot + 0.4F + dome + br;

      for (float z = -bl + 1.4F; z <= bl - 1.3F; z++) {
         k.pair(0.45F, 1.55F, roof - 0.05F, roof + 0.08F, z - 0.55F, z + 0.55F, p.accent());
      }

      k.beam(0.0F, bot + 0.9F, -bl + 0.4F, 0.0F, bot + 1.6F + hb, -bl - 1.4F, 0.7F, p.main());
      k.box(-1.1F, 1.1F, bot + 1.0F + hb, bot + 2.9F + hb, -bl - 3.1F, -bl - 1.2F, p.main());
      eyes(k, 0.3F, 0.6F, bot + 2.0F + hb, -bl - 3.1F);
      k.beam(0.0F, bot + 0.6F, bl, 0.0F, bot + 0.2F, bl + 1.0F, 0.35F, p.main());
   }

   private static void frog(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float br = mo.breathe();
      k.box(-2.4F, 2.4F, 0.8F + br, 3.6F + br, -2.2F, 2.6F, p.main());
      k.box(-1.6F, 1.6F, 1.0F + br, 2.4F + br, -2.35F, -2.1F, p.accent());
      k.pair(2.0F, 3.0F, 0.0F, 1.8F, 0.0F, 3.0F, p.main());
      k.pair(2.2F, 3.4F, 0.0F, 0.4F, -0.8F, 1.2F, p.second());
      k.pair(1.4F, 2.0F, 0.0F, 1.4F, -2.2F, -1.4F, p.main());
      k.pair(0.9F, 2.3F, 3.4F + br, 4.8F + br, -2.0F, -0.6F, p.main());
      eyes(k, 1.05F, 1.05F, 3.7F + br, -2.05F);
      k.box(-2.0F, 2.0F, 2.5F + br, 2.65F + br, -2.26F, -2.18F, p.second());
      k.cube(-1.0F, 3.65F + br, 1.2F, 0.4F, p.second());
      k.cube(1.2F, 3.65F + br, 0.2F, 0.35F, p.second());
      k.pair(1.4F, 1.9F, 1.8F + br, 2.2F + br, -2.28F, -2.2F, -876368);
   }

   private static void tRex(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() + br;
      float w = Mth.sin(mo.age() * 0.08F) * 1.2F;
      k.leg(-1.6F, 5.0F, 0.8F, s, 0.9F, p.main(), p.second());
      k.leg(1.6F, 5.0F, 0.8F, -s, 0.9F, p.main(), p.second());
      k.box(-2.0F, 2.0F, 4.2F + br, 8.0F + br, -2.6F, 2.8F, p.main());
      k.box(-1.5F, 1.5F, 4.0F + br, 7.0F + br, -2.75F, -2.3F, p.accent());

      for (float z : new float[]{-1.6F, 0.0F, 1.6F}) {
         k.box(-2.05F, 2.05F, 7.3F + br, 8.05F + br, z - 0.3F, z + 0.3F, p.second());
      }

      k.chain(new float[][]{{0.0F, 6.0F + br, 2.6F}, {w * 0.4F, 5.4F + br, 5.0F}, {w, 4.6F + br, 7.4F}}, new float[]{1.6F, 1.0F, 0.3F}, p.main(), p.main());
      k.box(-2.0F, 2.0F, 8.0F + hb, 11.2F + hb, -6.6F, -1.8F, p.main());
      float jaw = mo.mouth() * 1.6F;
      if (jaw > 0.05F) {
         k.box(-1.5F, 1.5F, 7.0F + hb - jaw, 8.1F + hb, -6.2F, -2.6F, -11920350);
      }

      k.box(-1.7F, 1.7F, 6.9F + hb - jaw, 8.2F + hb - jaw, -6.3F, -2.4F, p.accent());

      for (float x = -1.2F; x <= 1.3F; x += 0.8F) {
         k.box(x - 0.15F, x + 0.15F, 7.8F + hb, 8.3F + hb, -6.45F, -6.2F, -1);
      }

      eyes(k, 0.6F, 0.75F, 9.9F + hb, -6.6F);
      k.pair(0.3F, 0.7F, 10.7F + hb, 11.0F + hb, -6.68F, -6.55F, -14804455);

      for (int side = -1; side <= 1; side += 2) {
         k.beam(side * 1.9F, 6.6F + br, -2.2F, side * 2.2F, 5.4F + br, -3.4F + side * s * 0.3F, 0.35F, p.main());
      }
   }

   private static void snake(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float sway = Mth.sin(mo.age() * 0.1F) * 0.6F;
      int n = 14;
      float[][] pts = new float[n + 2][];
      float[] radii = new float[n + 2];

      for (int i = 0; i < n; i++) {
         float a = i * 0.62F;
         float r = 3.0F - i * 0.13F;
         pts[i] = new float[]{Mth.cos(a) * r, 0.7F + Math.max(0, i - 9) * 0.55F, Mth.sin(a) * r + 0.6F};
         radii[i] = 0.95F - i * 0.02F;
      }

      pts[n] = new float[]{sway, 4.6F, -1.0F};
      pts[n + 1] = new float[]{sway, 5.4F, -2.0F};
      radii[n] = 0.65F;
      radii[n + 1] = 0.6F;
      k.chain(pts, radii, p.main(), p.main());

      for (int i = 0; i < n; i += 2) {
         k.cube(pts[i][0], pts[i][1] + radii[i] * 0.85F, pts[i][2], 0.35F, p.second());
      }

      k.box(sway - 1.1F, sway + 1.1F, 5.0F, 6.4F, -4.2F, -1.9F, p.main());
      k.box(sway - 0.8F, sway + 0.8F, 4.9F, 5.2F, -4.0F, -2.2F, p.accent());
      eyes(k, 0.25F, 0.6F, 5.7F, -4.2F);
      if (Mth.sin(mo.age() * 0.4F) > 0.3F) {
         k.box(sway - 0.1F, sway + 0.1F, 5.2F, 5.35F, -5.3F, -4.2F, -2080678);
      }
   }

   private static void seal(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float br = mo.breathe();
      float hb = mo.bob() * 0.5F + br;
      float flap = Mth.sin(mo.age() * 0.12F) * 0.4F;
      k.box(-2.2F, 2.2F, 0.2F + br, 3.4F + br, -2.4F, 2.6F, p.main());
      k.box(-1.6F, 1.6F, 0.2F, 2.4F, 2.6F, 4.4F, p.main());
      k.flat(0.0F, 0.8F, 4.2F, 1.6F, 0.3F, 6.0F + flap, 0.9F, 0.25F, HeadwearModels.AXIS_Y, p.second());
      k.flat(0.0F, 0.8F, 4.2F, -1.6F, 0.3F, 6.0F - flap, 0.9F, 0.25F, HeadwearModels.AXIS_Y, p.second());
      k.box(-1.6F, 1.6F, 0.1F, 0.4F, -2.2F, 2.4F, p.accent());
      k.box(-2.0F, 2.0F, 0.8F + br, 4.6F + br, -3.2F, -1.2F, p.main());
      k.box(-1.8F, 1.8F, 3.6F + hb, 6.8F + hb, -4.4F, -1.4F, p.main());
      k.box(-1.0F, 1.0F, 3.8F + hb, 5.0F + hb, -5.0F, -4.3F, p.accent());
      if (mo.mouth() > 0.05F) {
         k.box(-0.8F, 0.8F, 3.8F + hb - mo.mouth() * 0.8F, 3.9F + hb, -4.9F, -4.3F, -11920350);
      }

      k.box(-0.4F, 0.4F, 4.6F + hb, 5.1F + hb, -5.1F, -4.95F, -14804455);
      eyes(k, 0.55F, 0.9F, 5.3F + hb, -4.4F);

      for (int side = -1; side <= 1; side += 2) {
         k.flat(side * 2.0F, 1.6F + br, -2.0F, side * 3.4F, 0.2F, -1.4F + side * flap, 1.0F, 0.25F, HeadwearModels.AXIS_Z, p.main());
         k.beam(side * 0.8F, 4.4F + hb, -5.0F, side * 2.6F, 4.7F + hb, -5.4F, 0.06F, -1);
      }

      k.cube(-1.2F, 3.4F + br, 0.6F, 0.4F, p.second());
      k.cube(1.0F, 3.4F + br, 1.8F, 0.35F, p.second());
   }

   private static void butterfly(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 5.5F);
      float flap = Mth.abs(Mth.sin(mo.age() * 0.45F)) * 0.9F + 0.15F;
      float cos = Mth.cos(flap);
      float sin = Mth.sin(flap);
      k.beam(0.0F, h, -1.6F, 0.0F, h, 1.8F, 0.35F, p.second());
      k.cube(0.0F, h + 0.1F, -2.0F, 0.5F, p.second());

      for (int side = -1; side <= 1; side += 2) {
         k.beam(side * 0.2F, h + 0.4F, -2.2F, side * 0.9F, h + 1.8F, -3.0F, 0.07F, p.second());
         k.flat(side * 0.2F, h, -0.6F, side * 3.8F * cos, h + 3.8F * sin, -1.4F, 2.4F, 0.12F, HeadwearModels.AXIS_Z, p.main());
         k.flat(side * 0.2F, h - 0.1F, 0.6F, side * 3.0F * cos, h + 3.0F * sin - 0.6F, 1.6F, 1.8F, 0.12F, HeadwearModels.AXIS_Z, p.main());
         k.cube(side * 2.6F * cos, h + 2.6F * sin + 0.1F, -1.1F, 0.35F, p.accent());
         k.cube(side * 3.5F * cos, h + 3.5F * sin, -1.4F, 0.3F, p.second());
      }
   }

   private static void bee(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 5.0F);
      float buzz = Mth.sin(mo.age() * 2.1F) * 0.5F;
      k.box(-1.4F, 1.4F, h - 1.3F, h + 1.3F, -1.6F, 2.2F, p.main());

      for (float z : new float[]{-0.4F, 1.2F}) {
         k.box(-1.45F, 1.45F, h - 1.35F, h + 1.35F, z - 0.35F, z + 0.35F, p.second());
      }

      k.beam2(0.0F, h - 0.2F, 2.2F, 0.0F, h - 0.4F, 3.2F, 0.4F, 0.05F, p.second());
      k.box(-1.2F, 1.2F, h - 1.0F, h + 1.2F, -3.2F, -1.6F, p.second());
      k.pair(0.35F, 1.15F, h - 0.3F, h + 0.8F, -3.25F, -3.15F, -1);
      k.eye(-0.95F, -0.5F, h - 0.1F, h + 0.6F, -3.3F, -14804455);
      k.eye(0.5F, 0.95F, h - 0.1F, h + 0.6F, -3.3F, -14804455);

      for (int side = -1; side <= 1; side += 2) {
         k.beam(side * 0.4F, h + 1.1F, -2.8F, side * 1.0F, h + 2.4F, -3.4F, 0.08F, p.second());
         k.flat(side * 0.6F, h + 1.2F, -0.4F, side * 2.8F, h + 2.6F + buzz, 0.4F, 1.3F, 0.1F, HeadwearModels.AXIS_Z, p.accent());
      }
   }

   private static void ladybug(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float s = mo.swing();
      float br = mo.breathe();

      for (int side = -1; side <= 1; side += 2) {
         for (int i = -1; i <= 1; i++) {
            k.beam(side * 1.4F, 0.8F, i * 1.1F, side * 2.6F, 0.0F, i * 1.1F + side * i * s, 0.15F, p.second());
         }
      }

      k.box(-2.0F, 2.0F, 0.6F + br, 2.2F + br, -1.8F, 2.4F, p.main());
      k.box(-1.5F, 1.5F, 2.2F + br, 2.9F + br, -1.2F, 1.8F, p.main());
      k.box(-0.1F, 0.1F, 0.6F + br, 2.95F + br, -1.3F, 2.45F, p.second());

      for (float[] spot : new float[][]{{1.0F, 2.95F, -0.4F}, {1.1F, 2.3F, 1.4F}, {0.6F, 2.95F, 1.0F}, {1.6F, 1.6F, 0.2F}}) {
         k.cube(spot[0], spot[1] + br, spot[2], 0.35F, p.second());
         k.cube(-spot[0], spot[1] + br, spot[2], 0.35F, p.second());
      }

      k.box(-1.2F, 1.2F, 0.6F, 2.0F, -2.8F, -1.7F, p.second());
      k.pair(0.45F, 0.85F, 1.3F, 1.7F, -2.85F, -2.75F, p.accent());
   }

   private static void bat(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 6.0F);
      float flap = Mth.sin(mo.age() * 0.5F) * 0.8F;
      k.box(-1.2F, 1.2F, h - 1.4F, h + 1.2F, -1.0F, 1.2F, p.main());
      k.box(-1.3F, 1.3F, h + 0.8F, h + 3.0F, -1.4F, 0.8F, p.main());
      k.taperPair(0.4F, 1.3F, -0.6F, 0.4F, h + 2.9F, 0.8F, 1.1F, -0.2F, 0.0F, h + 4.6F, p.main());
      k.taperPair(0.65F, 1.1F, -0.7F, -0.6F, h + 3.0F, 0.85F, 1.0F, -0.68F, -0.64F, h + 4.2F, p.accent());
      eyes(k, 0.25F, 0.65F, h + 1.8F, -1.4F);
      k.pair(0.2F, 0.4F, h + 0.9F, h + 1.3F, -1.45F, -1.35F, -1);

      for (int side = -1; side <= 1; side += 2) {
         k.flat(side * 1.1F, h + 0.6F, 0.0F, side * 4.8F, h + 1.4F + flap * 2.0F, 0.4F, 2.2F, 0.12F, HeadwearModels.AXIS_Y, p.second());
         k.beam(side * 1.1F, h + 0.8F, 0.0F, side * 4.8F, h + 1.4F + flap * 2.0F, 0.4F, 0.12F, p.main());
      }
   }

   private static void jellyfish(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 5.5F);
      float pulse = Mth.sin(mo.age() * 0.18F) * 0.3F;

      for (int i = 0; i < 6; i++) {
         float a = i * (float) Math.PI / 3.0F;
         float x = Mth.cos(a) * 1.4F;
         float z = Mth.sin(a) * 1.4F;
         float wave = Mth.sin(mo.age() * 0.15F + i) * 0.6F;
         k.chain(
            new float[][]{{x, h, z}, {x + wave * 0.5F, h - 1.8F, z}, {x - wave * 0.3F, h - 3.4F, z}, {x + wave, h - 4.8F, z}},
            new float[]{0.3F, 0.25F, 0.2F, 0.15F},
            p.second(),
            p.accent()
         );
      }

      k.box(-2.4F - pulse, 2.4F + pulse, h, h + 2.2F, -2.4F - pulse, 2.4F + pulse, p.main());
      k.box(-1.6F, 1.6F, h + 2.2F, h + 3.0F, -1.6F, 1.6F, p.main());
      k.box(-2.6F - pulse, 2.6F + pulse, h - 0.2F, h + 0.4F, -2.6F - pulse, 2.6F + pulse, p.second());
      eyes(k, 0.5F, 0.7F, h + 0.8F, -2.45F - pulse);
      k.pair(1.3F, 1.8F, h + 0.6F, h + 0.9F, -2.47F - pulse, -2.4F - pulse, -876368);
   }

   private static void goldfish(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 4.5F);
      float wag = Mth.sin(mo.age() * 0.35F);
      k.box(-1.1F, 1.1F, h - 1.6F, h + 1.6F, -2.2F, 1.8F, p.main());
      k.box(-0.9F, 0.9F, h - 1.1F, h + 1.1F, -3.0F, -2.1F, p.main());
      k.box(-0.8F, 0.8F, h - 1.65F, h - 0.8F, -2.0F, 1.4F, p.accent());
      eyes(k, 0.25F, 0.6F, h, -3.0F);
      k.flat(0.0F, h, 1.6F, wag, h + 1.6F, 4.0F, 1.2F, 0.12F, HeadwearModels.AXIS_Y, p.second());
      k.flat(0.0F, h, 1.6F, wag, h - 1.6F, 4.0F, 1.2F, 0.12F, HeadwearModels.AXIS_Y, p.second());
      k.flat(0.0F, h + 1.5F, -0.8F, 0.0F, h + 2.6F, 0.8F, 0.9F, 0.12F, HeadwearModels.AXIS_Z, p.second());
      k.cube(0.2F, h + 2.2F + mo.age() * 0.05F % 3.0F, -3.2F, 0.25F, -1);
   }

   private static void octopus(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float br = mo.breathe();

      for (int i = 0; i < 8; i++) {
         float a = i * (float) Math.PI / 4.0F;
         float cos = Mth.cos(a);
         float sin = Mth.sin(a);
         float wave = Mth.sin(mo.age() * 0.12F + i) * 0.5F;
         k.chain(
            new float[][]{
               {cos * 1.6F, 2.4F, sin * 1.6F}, {cos * 3.0F, 0.9F, sin * 3.0F}, {cos * 4.0F, 0.4F, sin * 4.0F}, {cos * 4.4F, 1.3F + wave, sin * 4.4F}
            },
            new float[]{0.75F, 0.55F, 0.35F, 0.25F},
            p.main(),
            p.second()
         );
      }

      k.box(-2.2F, 2.2F, 2.2F + br, 6.2F + br, -2.0F, 2.4F, p.main());
      k.box(-1.6F, 1.6F, 6.2F + br, 6.9F + br, -1.4F, 1.8F, p.main());
      eyes(k, 0.45F, 1.2F, 3.6F + br, -2.0F);
      k.cube(-1.2F, 6.0F + br, 0.8F, 0.45F, p.accent());
      k.cube(1.0F, 5.4F + br, 1.6F, 0.35F, p.accent());
   }

   private static void crab(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float s = mo.swing();
      float br = mo.breathe();
      float snap = Mth.abs(Mth.sin(mo.age() * 0.3F)) * 0.4F;

      for (int side = -1; side <= 1; side += 2) {
         for (int i = -1; i <= 1; i++) {
            float z = i * 1.0F + 0.4F;
            k.chain(
               new float[][]{{side * 2.4F, 2.0F, z}, {side * 4.2F, 1.6F, z}, {side * 4.8F, 0.0F, z + side * i * s}},
               new float[]{0.3F, 0.25F, 0.15F},
               p.second(),
               p.second()
            );
         }

         k.beam(side * 2.0F, 2.4F, -1.6F, side * 3.0F, 2.8F, -3.2F, 0.4F, p.second());
         float x0 = side > 0 ? 2.4F : -3.8F;
         k.box(x0, x0 + 1.4F, 2.2F, 3.4F, -4.8F, -3.0F, p.main());
         k.box(x0, x0 + 1.4F, 3.5F + snap, 4.1F + snap, -4.8F, -3.6F, p.main());
         k.beam(side * 0.8F, 3.4F, -1.4F, side * 1.0F, 5.0F, -1.6F, 0.18F, p.second());
         k.cube(side * 1.0F, 5.2F, -1.6F, 0.45F, -1);
         k.box(side * 1.0F - 0.2F, side * 1.0F + 0.2F, 5.05F, 5.45F, -2.1F, -1.95F, -14804455);
      }

      k.box(-2.6F, 2.6F, 1.6F + br, 3.2F + br, -1.8F, 1.8F, p.main());
      k.box(-2.0F, 2.0F, 3.2F + br, 3.7F + br, -1.3F, 1.3F, p.main());
      k.cube(-1.2F, 3.7F + br, 0.3F, 0.3F, p.accent());
      k.cube(1.1F, 3.7F + br, -0.4F, 0.3F, p.accent());
      k.box(-0.8F, 0.8F, 2.0F + br, 2.2F + br, -1.86F, -1.8F, -14804455);
   }

   private static void snail(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float hb = mo.bob() * 0.5F + mo.breathe();
      k.box(-1.2F, 1.2F, 0.0F, 1.2F, -3.4F, 3.0F, p.main());
      k.box(-1.1F, 1.1F, 0.6F, 3.2F + hb, -3.8F, -2.2F, p.main());

      for (int side = -1; side <= 1; side += 2) {
         k.beam(side * 0.6F, 3.0F + hb, -3.0F, side * 1.0F, 5.2F + hb, -3.4F, 0.18F, p.main());
         k.cube(side * 1.0F, 5.4F + hb, -3.4F, 0.4F, -14804455);
         k.cube(side * 1.0F - 0.15F, 5.6F + hb, -3.75F, 0.12F, -1);
      }

      k.box(-0.5F, 0.5F, 1.4F + hb, 1.7F + hb, -3.85F, -3.78F, -9811398);
      k.box(-1.5F, 1.5F, 1.2F, 5.6F, -1.8F, 2.6F, p.second());
      k.box(-1.55F, 1.55F, 2.2F, 4.6F, -0.8F, 1.6F, p.accent());
      k.box(-1.6F, 1.6F, 3.0F, 3.8F, 0.0F, 0.8F, p.second());
   }

   private static void dragonfly(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 5.5F);
      float buzz = Mth.sin(mo.age() * 2.3F) * 0.3F;
      k.chain(
         new float[][]{{0.0F, h, -1.8F}, {0.0F, h, 0.6F}, {0.0F, h - 0.2F, 3.2F}, {0.0F, h - 0.4F, 5.4F}},
         new float[]{0.55F, 0.45F, 0.3F, 0.22F},
         p.main(),
         p.second()
      );
      k.box(-0.9F, 0.9F, h - 0.6F, h + 0.8F, -3.0F, -1.8F, p.main());
      k.pair(0.2F, 1.1F, h - 0.2F, h + 0.9F, -3.1F, -2.2F, p.second());

      for (int side = -1; side <= 1; side += 2) {
         for (float fwd : new float[]{-0.8F, 0.6F}) {
            k.flat(side * 0.3F, h + 0.4F, fwd, side * 4.6F, h + 0.9F + buzz, fwd + (fwd < 0.0F ? -0.4F : 0.6F), 1.0F, 0.08F, HeadwearModels.AXIS_Z, p.accent());
         }
      }
   }

   private static void pufferfish(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 4.5F);
      float r = 2.3F * (1.0F + Math.max(0.0F, Mth.sin(mo.age() * 0.05F)) * 0.25F);
      float wag = Mth.sin(mo.age() * 0.3F) * 0.6F;
      k.box(-r, r, h - r, h + r, -r, r, p.main());
      k.box(-r * 0.8F, r * 0.8F, h - r - 0.06F, h - r * 0.4F, -r * 0.8F, r * 0.8F, p.accent());

      for (int i = 0; i < 18; i++) {
         float y = 1.0F - (i + 0.5F) / 9.0F;
         float rad = Mth.sqrt(Math.max(0.0F, 1.0F - y * y));
         float a = i * 2.4F;
         float dx = Mth.cos(a) * rad;
         float dz = Mth.sin(a) * rad;
         k.beam2(dx * r, h + y * r, dz * r, dx * (r + 1.0F), h + y * (r + 1.0F), dz * (r + 1.0F), 0.25F, 0.03F, p.accent());
      }

      k.cube(-1.0F, h + r, 0.6F, 0.4F, p.second());
      k.cube(1.1F, h + r * 0.6F, 1.4F, 0.35F, p.second());
      eyes(k, r * 0.25F, r * 0.45F, h + 0.1F, -r);
      k.box(-0.4F, 0.4F, h - 0.6F, h - 0.1F, -r - 0.1F, -r + 0.05F, -2068358);
      k.flat(0.0F, h, r, wag, h, r + 1.8F, 1.2F, 0.12F, HeadwearModels.AXIS_Y, p.second());
   }

   private static void whale(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 4.2F);
      float wag = Mth.sin(mo.age() * 0.15F) * 0.5F;
      k.box(-2.4F, 2.4F, h - 2.0F, h + 2.2F, -3.6F, 2.4F, p.main());
      k.box(-2.0F, 2.0F, h - 2.08F, h - 1.2F, -3.4F, 1.8F, p.accent());
      k.box(-1.3F, 1.3F, h - 1.0F, h + 1.2F, 2.4F, 4.6F, p.main());
      k.flat(0.0F, h + 0.2F + wag, 4.4F, 0.0F, h + 0.6F + wag, 6.0F, 2.4F, 0.2F, HeadwearModels.AXIS_X, p.second());
      eyes(k, 1.3F, 0.7F, h - 0.4F, -3.6F);
      k.box(-1.0F, 1.0F, h - 1.1F, h - 0.95F, -3.66F, -3.58F, p.second());
      if ((int)(mo.age() % 60.0F) < 25) {
         k.beam(0.0F, h + 2.2F, -1.6F, 0.0F, h + 4.2F, -1.6F, 0.25F, -6366990);
         k.cube(-0.6F, h + 4.4F, -1.6F, 0.35F, -6366990);
         k.cube(0.6F, h + 4.4F, -1.6F, 0.35F, -6366990);
      }

      for (int side = -1; side <= 1; side += 2) {
         k.flat(side * 2.2F, h - 1.0F, -1.6F, side * 3.8F, h - 1.8F, -0.6F, 0.9F, 0.2F, HeadwearModels.AXIS_Z, p.main());
      }
   }

   private static void narwhal(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 4.2F);
      float wag = Mth.sin(mo.age() * 0.2F) * 0.5F;
      k.box(-1.9F, 1.9F, h - 1.6F, h + 1.8F, -3.0F, 2.0F, p.main());
      k.box(-1.6F, 1.6F, h - 1.68F, h - 0.9F, -2.8F, 1.6F, p.accent());
      k.box(-1.1F, 1.1F, h - 0.8F, h + 1.0F, 2.0F, 3.8F, p.main());
      k.flat(0.0F, h + 0.1F + wag, 3.6F, 0.0F, h + 0.4F + wag, 5.2F, 2.0F, 0.2F, HeadwearModels.AXIS_X, p.second());
      k.cube(-0.8F, h + 1.8F, -0.6F, 0.35F, p.second());
      k.cube(0.9F, h + 1.8F, 0.6F, 0.3F, p.second());
      k.cube(0.0F, h + 1.8F, 1.4F, 0.3F, p.second());
      eyes(k, 0.9F, 0.8F, h - 0.1F, -3.0F);
      k.pair(1.2F, 1.7F, h - 0.6F, h - 0.3F, -3.06F, -3.0F, -876368);
      k.beam2(0.0F, h + 0.6F, -3.0F, 0.0F, h + 1.5F, -7.0F, 0.32F, 0.05F, -725284);

      for (int side = -1; side <= 1; side += 2) {
         k.flat(side * 1.8F, h - 0.8F, -1.2F, side * 3.2F, h - 1.4F + wag * side, -0.4F, 0.8F, 0.2F, HeadwearModels.AXIS_Z, p.main());
      }
   }

   private static void seahorse(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 5.0F);
      float fin = Mth.sin(mo.age() * 0.6F) * 0.3F;
      k.chain(
         new float[][]{
            {0.0F, h + 3.0F, -0.6F}, {0.0F, h + 1.4F, 0.2F}, {0.0F, h - 0.4F, 0.4F}, {0.0F, h - 2.0F, -0.2F}, {0.0F, h - 2.8F, -1.2F}, {0.0F, h - 2.2F, -2.0F}
         },
         new float[]{1.1F, 1.3F, 1.1F, 0.8F, 0.5F, 0.3F},
         p.main(),
         p.second()
      );

      for (float y : new float[]{1.4F, 0.4F, -0.6F}) {
         k.cube(0.0F, h + y, -0.9F, 0.45F, p.accent());
      }

      k.box(-0.95F, 0.95F, h + 2.6F, h + 4.3F, -1.6F, 0.3F, p.main());
      k.beam2(0.0F, h + 3.2F, -1.5F, 0.0F, h + 3.0F, -3.4F, 0.5F, 0.32F, p.main());
      k.cube(0.0F, h + 4.5F, -0.4F, 0.35F, p.second());
      k.cube(0.0F, h + 4.4F, 0.3F, 0.3F, p.second());
      eyes(k, 0.2F, 0.6F, h + 3.3F, -1.6F);
      k.flat(0.0F, h + 1.2F, 0.9F, 0.0F, h + 0.6F, 2.2F + fin, 1.0F, 0.1F, HeadwearModels.AXIS_Y, p.second());
   }

   private static void starfish(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float br = mo.breathe();

      for (int i = 0; i < 5; i++) {
         float a = i * (float) Math.PI * 0.4F - (float) (Math.PI / 2);
         float cos = Mth.cos(a);
         float sin = Mth.sin(a);
         float curl = Mth.sin(mo.age() * 0.1F + i) * 0.3F;
         k.flat(0.0F, 0.8F + br, 0.0F, cos * 4.2F, 0.6F + curl, sin * 4.2F, 1.5F, 0.45F, null, p.main());
         k.cube(cos * 2.2F, 1.2F + br, sin * 2.2F, 0.28F, p.accent());
         k.cube(cos * 3.3F, 1.0F + br + curl * 0.5F, sin * 3.3F, 0.22F, p.accent());
      }

      k.box(-1.3F, 1.3F, 0.3F, 1.6F + br, -1.3F, 1.3F, p.main());
      eyes(k, 0.2F, 0.5F, 0.75F + br, -1.3F);
      k.box(-0.3F, 0.3F, 0.4F + br, 0.55F + br, -1.34F, -1.28F, p.second());
   }

   private static void caterpillar(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float lead = 0.0F;

      for (int i = 0; i < 6; i++) {
         float z = -2.4F + i * 1.3F;
         float lift = Math.max(0.0F, Mth.sin(mo.age() * 0.15F - i * 0.8F)) * 0.9F;
         if (i == 0) {
            lead = lift;
         }

         k.cube(0.0F, 1.1F + lift, z, 1.1F, i % 2 == 0 ? p.main() : p.second());
         k.pair(0.45F, 0.85F, 0.0F, 0.4F + lift * 0.5F, z - 0.2F, z + 0.2F, -14804455);
      }

      k.cube(0.0F, 1.7F + lead, -3.6F, 1.35F, p.main());
      eyes(k, 0.3F, 0.6F, 1.6F + lead, -4.95F);
      k.pair(0.9F, 1.3F, 1.2F + lead, 1.5F + lead, -5.0F, -4.93F, -876368);

      for (int side = -1; side <= 1; side += 2) {
         k.beam(side * 0.5F, 2.9F + lead, -3.8F, side * 1.0F, 4.2F + lead, -4.4F, 0.1F, p.accent());
         k.cube(side * 1.0F, 4.3F + lead, -4.4F, 0.25F, p.accent());
      }
   }

   private static void shark(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      float h = hover(mo, 4.5F);
      float wag = Mth.sin(mo.age() * 0.25F) * 0.8F;
      k.box(-1.8F, 1.8F, h - 1.6F, h + 1.6F, -3.2F, 2.2F, p.main());
      k.box(-1.2F, 1.2F, h - 0.9F, h + 1.2F, -4.6F, -3.1F, p.main());
      k.box(-1.5F, 1.5F, h - 1.68F, h - 0.4F, -4.5F, 2.0F, p.accent());
      k.chain(new float[][]{{0.0F, h, 2.2F}, {wag * 0.5F, h + 0.2F, 4.0F}, {wag, h + 0.3F, 5.4F}}, new float[]{1.3F, 0.8F, 0.4F}, p.main(), p.main());
      k.flat(wag, h, 5.2F, wag * 1.2F, h + 2.4F, 6.4F, 0.9F, 0.15F, HeadwearModels.AXIS_Z, p.main());
      k.flat(wag, h, 5.2F, wag * 1.2F, h - 1.6F, 6.0F, 0.7F, 0.15F, HeadwearModels.AXIS_Z, p.main());
      k.taper(-0.25F, 0.25F, -1.2F, 1.2F, h + 1.5F, -0.08F, 0.08F, 0.8F, 1.3F, h + 3.6F, p.main());

      for (int side = -1; side <= 1; side += 2) {
         k.flat(side * 1.7F, h - 1.0F, -1.4F, side * 3.4F, h - 2.2F, 0.0F, 1.0F, 0.15F, HeadwearModels.AXIS_Z, p.main());

         for (float z = -2.6F; z <= -1.6F; z += 0.5F) {
            k.box(side > 0 ? 1.78F : -1.86F, side > 0 ? 1.86F : -1.78F, h - 0.6F, h + 0.6F, z - 0.1F, z + 0.1F, p.second());
         }
      }

      eyes(k, 0.7F, 0.6F, h + 0.2F, -4.6F);

      for (float x = -0.8F; x <= 0.85F; x += 0.4F) {
         k.box(x - 0.12F, x + 0.12F, h - 0.7F, h - 0.3F, -4.65F, -4.55F, -1);
      }
   }
}
