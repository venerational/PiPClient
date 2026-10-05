package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.util.Mth;

final class PetSpecies {
   private static final int BLACK = -14804455;
   private static final int WHITE = -1;
   private static final int PINK = -876368;
   private static final int GOLD = -797845;
   private static final int HORN = -1186868;
   private static final long MANE_LION = 1L;
   private static final long MANE_HORSE = 2L;
   private static final long UNICORN = 4L;
   private static final long ANTLERS = 8L;
   private static final long MOOSE = 16L;
   private static final long CURVED_HORNS = 32L;
   private static final long NUBS = 64L;
   private static final long RHINO = 128L;
   private static final long TUSKS = 256L;
   private static final long TRUNK = 512L;
   private static final long WOOL = 1024L;
   private static final long FRINGE = 2048L;
   private static final long SPIKES = 4096L;
   private static final long HUMP = 8192L;
   private static final long MASK = 16384L;
   private static final long EYE_PATCH = 32768L;
   private static final long FOREHEAD_STRIPE = 65536L;
   private static final long BLAZE = 131072L;
   private static final long CHEEKS = 262144L;
   private static final long TEAR = 524288L;
   private static final long WHISKERS = 1048576L;
   private static final long COLLAR = 2097152L;
   private static final long TONGUE = 4194304L;
   private static final long PIG_NOSE = 8388608L;
   private static final long BUCK_TEETH = 16777216L;
   private static final long DARK_MUZZLE = 33554432L;
   private static final long EAR_TUFTS = 67108864L;
   private static final long CHEEK_FLUFF = 134217728L;
   private static final long FLUFF_HEAD = 268435456L;
   private static final long POM = 536870912L;
   private static final long FRILL = 1073741824L;
   private static final long TRI_HORNS = 2147483648L;
   private static final long PLATES = 4294967296L;
   private static final long DRAGON_WINGS = 8589934592L;
   private static final long TEETH = 17179869184L;
   private static final long RIDGES = 34359738368L;
   private static final long BIG_EYES = 68719476736L;
   private static final long GILLS = 137438953472L;
   private static final long TAIL_SPIKES = 274877906944L;
   private static final long BIG_NOSE = 549755813888L;
   private static final long FLIPPERS = 1099511627776L;
   private static final long ARMS = 2199023255552L;
   private static final long POUCH = 4398046511104L;
   private static final long FACE = 8796093022208L;
   private static final long BEAK = 17592186044416L;
   private static final long CLAWS = 35184372088832L;
   private static final long BREAST = 70368744177664L;
   private static final long COMB = 140737488355328L;
   private static final long CROWN = 281474976710656L;
   private static final long WHITE_HEAD = 562949953421312L;
   private static final long BIG_FEET = 1125899906842624L;
   private static final long LONG_ARMS = 2251799813685248L;
   private static final long BIG_MUZZLE = 4503599627370496L;
   private static final long LOW_HEAD = 9007199254740992L;
   private static final long BLOCK_SNOUT = 18014398509481984L;
   private static final long FOLDS = 36028797018963968L;
   private static final long DUCK_BILL = 72057594037927936L;
   private static final Map<CosmeticsModule.Pet, PetSpecies.Spec> SPECS = new EnumMap<>(CosmeticsModule.Pet.class);

   private PetSpecies() {
   }

   private static PetSpecies.Spec quad(int m, int s, int a) {
      return new PetSpecies.Spec(PetSpecies.Plan.QUAD, m, s, a);
   }

   private static PetSpecies.Spec upright(int m, int s, int a) {
      return new PetSpecies.Spec(PetSpecies.Plan.UPRIGHT, m, s, a).tail(PetSpecies.Tail.NONE, 0).mark(PetSpecies.Mark.BELLY, 2);
   }

   private static PetSpecies.Spec bird(int m, int s, int a) {
      return new PetSpecies.Spec(PetSpecies.Plan.BIRD, m, s, a).ear(PetSpecies.Ear.NONE, 0).mark(PetSpecies.Mark.NONE, 1);
   }

   static int[] natural(CosmeticsModule.Pet kind) {
      PetSpecies.Spec spec = SPECS.get(kind);
      return spec == null ? null : spec.natural;
   }

   static float height(CosmeticsModule.Pet kind) {
      PetSpecies.Spec sp = SPECS.get(kind);
      if (sp == null) {
         return 0.0F;
      } else {
         return switch (sp.plan) {
            case QUAD -> sp.legH + Math.max(sp.bh, sp.bh * 0.45F + sp.neck + sp.hs * 2.0F);
            case UPRIGHT -> sp.legH + sp.bh + sp.hs * 2.0F;
            case BIRD -> sp.legH + sp.bh + sp.neck + sp.hs;
         };
      }
   }

   static boolean draw(CosmeticsModule.Pet kind, PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p) {
      PetSpecies.Spec sp = SPECS.get(kind);
      if (sp == null) {
         return false;
      } else {
         switch (sp.plan) {
            case QUAD:
               quad(k, mo, p, sp);
               break;
            case UPRIGHT:
               upright(k, mo, p, sp);
               break;
            case BIRD:
               bird(k, mo, p, sp);
         }

         return true;
      }
   }

   private static int c(PetModels.Pal p, int which) {
      return switch (which) {
         case 1 -> p.second();
         case 2 -> p.accent();
         case 3 -> -12634832;
         case 4 -> -878034;
         case 5 -> -9541018;
         case 6 -> -1533272;
         case 7 -> -867792;
         case 8 -> -461070;
         case 9 -> -14804455;
         default -> p.main();
      };
   }

   private static float hash(int i, int salt) {
      float v = Mth.sin(i * 12.9898F + salt * 78.233F) * 43758.547F;
      return v - (float)Math.floor(v);
   }

   private static void quad(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, PetSpecies.Spec sp) {
      float s = mo.swing();
      float br = mo.breathe();
      float hb = mo.bob() + br;
      float bw = sp.bw;
      float bl = sp.bl;
      float bot = sp.legH + br;
      float top = sp.legH + sp.bh + br;
      int legC = c(p, sp.legColor);
      int footC = c(p, sp.footColor < 0 ? sp.legColor : sp.footColor);
      float lx = Math.max(0.5F, bw - sp.legW - 0.25F);
      float lz = Math.max(0.6F, bl - sp.legW - 0.4F);
      float hip = sp.legH + 0.5F;
      k.leg(-lx, hip, -lz, s, sp.legW, legC, footC);
      k.leg(lx, hip, -lz, -s, sp.legW, legC, footC);
      k.leg(-lx, hip, lz, -s, sp.legW, legC, footC);
      k.leg(lx, hip, lz, s, sp.legW, legC, footC);
      if (sp.has(536870912L)) {
         for (float x : new float[]{-lx, lx}) {
            k.cube(x, 0.9F, -lz, sp.legW + 0.45F, p.main());
            k.cube(x, 0.9F, lz, sp.legW + 0.45F, p.main());
         }
      }

      k.box(-bw, bw, bot, top, -bl, bl, p.main());
      marks(k, p, sp, bot, top);
      if (sp.has(36028797018963968L)) {
         for (float z : new float[]{-bl * 0.4F, bl * 0.35F}) {
            k.box(-bw - 0.08F, bw + 0.08F, bot + 0.2F, top + 0.08F, z - 0.2F, z + 0.2F, p.second());
         }
      }

      if (sp.has(8192L)) {
         k.box(-bw * 0.7F, bw * 0.7F, top - 0.2F, top + sp.bh * 0.4F, -bl * 0.4F, bl * 0.3F, p.main());
         k.box(-bw * 0.45F, bw * 0.45F, top, top + sp.bh * 0.65F, -bl * 0.25F, bl * 0.15F, p.main());
      }

      if (sp.has(1024L)) {
         for (float z = -bl + 1.0F; z <= bl - 0.8F; z++) {
            k.cube(-bw + 0.3F, top - 0.7F, z, 1.05F, p.second());
            k.cube(bw - 0.3F, top - 0.7F, z + 0.5F, 1.05F, p.main());
            k.cube(0.0F, top + 0.1F, z + 0.4F, 1.0F, p.main());
         }
      }

      if (sp.has(4096L)) {
         for (float z = -bl + 0.4F; z <= bl + 0.3F; z++) {
            for (float x = -bw - 0.2F; x <= bw + 0.25F; x++) {
               float h = top - Math.abs(x) / bw * 1.4F;
               k.taper(x - 0.4F, x + 0.4F, z - 0.4F, z + 0.4F, h - 0.2F, x * 1.15F - 0.05F, x * 1.15F + 0.05F, z + 0.5F, z + 0.6F, h + 1.5F, p.second());
            }
         }
      }

      if (sp.has(4294967296L)) {
         for (int i = 0; i < 5; i++) {
            float z = -bl + 0.8F + i * (2.0F * bl - 1.6F) / 4.0F;
            float size = 1.2F + Mth.sin(i / 4.0F * (float) Math.PI) * 1.2F;
            k.taper(-0.25F, 0.25F, z - size * 0.6F, z + size * 0.6F, top - 0.2F, -0.05F, 0.05F, z - 0.1F, z + 0.1F, top + size * 1.6F, p.second());
         }
      }

      if (sp.has(34359738368L)) {
         for (float z = -bl + 0.5F; z <= bl; z++) {
            k.taper(-0.35F, 0.35F, z - 0.35F, z + 0.35F, top - 0.1F, -0.05F, 0.05F, z, z + 0.1F, top + 0.9F, p.second());
         }
      }

      if (sp.has(8589934592L)) {
         float flap = Mth.sin(mo.age() * 0.2F) * 0.6F;

         for (int side = -1; side <= 1; side += 2) {
            k.beam(side * bw * 0.6F, top, -bl * 0.3F, side * (bw + 3.4F), top + 2.6F + flap, bl * 0.2F, 0.22F, p.second());
            k.flat(side * bw * 0.6F, top, -bl * 0.1F, side * (bw + 3.0F), top + 1.4F + flap, bl * 0.8F, 1.8F, 0.12F, HeadwearModels.AXIS_Z, p.second());
         }
      }

      tail(k, mo, p, sp, top - sp.bh * 0.25F, bl);
      float hs = sp.hs;
      float hz;
      float h0;
      if (sp.neck > 0.0F) {
         hz = -bl - hs * 0.3F;
         h0 = top + sp.neck - hs * 0.2F + mo.bob();
         float r = Math.min(bw, hs) * 0.55F;
         k.beam2(0.0F, top - 0.8F, -bl + 1.0F, 0.0F, h0 + hs * 0.6F, hz + hs * 0.5F, r * 1.1F, r * 0.9F, p.main());
         if (sp.has(2L)) {
            k.beam2(0.0F, top + 0.2F, -bl + 1.7F, 0.0F, h0 + hs * 2.0F + 0.2F, hz + hs * 0.9F, 0.6F, 0.5F, p.second());
         }
      } else {
         hz = -bl - hs * 0.55F;
         h0 = sp.legH + sp.bh * (sp.has(9007199254740992L) ? 0.08F : 0.45F) + hb;
      }

      head(k, mo, p, sp, hz, h0, bw, bl);
   }

   private static void marks(PetModels.Kit k, PetModels.Pal p, PetSpecies.Spec sp, float bot, float top) {
      float bw = sp.bw;
      float bl = sp.bl;
      int mc = c(p, sp.markColor);
      if (sp.mark == PetSpecies.Mark.BELLY || sp.mark == PetSpecies.Mark.STRIPES) {
         k.box(-bw + 0.5F, bw - 0.5F, bot - 0.12F, bot + 0.3F, -bl + 0.8F, bl - 0.8F, p.accent());
         k.box(-bw * 0.65F, bw * 0.65F, bot + 0.4F, top - 0.9F, -bl - 0.12F, -bl + 0.4F, p.accent());
      }

      switch (sp.mark) {
         case STRIPES:
            for (float z = -bl + 0.9F; z < bl - 0.4F; z++) {
               k.box(-bw - 0.06F, bw + 0.06F, bot + sp.bh * 0.25F, top + 0.06F, z - 0.28F, z + 0.28F, mc);
            }
            break;
         case SPOTS:
         case PATCHES:
            boolean big = sp.mark == PetSpecies.Mark.PATCHES;

            for (int i = 0; i < (big ? 7 : 14); i++) {
               float size = (big ? 1.4F : 0.6F) + (big ? 0.8F : 0.35F) * hash(i, 4);
               float z = -bl + 0.6F + (2.0F * bl - 1.2F) * hash(i, 3);
               if (i % 3 == 2) {
                  float x = -bw + 0.6F + (2.0F * bw - 1.2F) * hash(i, 1);
                  k.box(x - size / 2.0F, x + size / 2.0F, top - 0.02F, top + 0.08F, z - size / 2.0F, z + size / 2.0F, mc);
               } else {
                  float h = bot + 0.5F + (sp.bh - 1.0F) * hash(i, 2);
                  float x0 = i % 3 == 0 ? -bw - 0.08F : bw - 0.02F;
                  k.box(x0, x0 + 0.1F, h - size / 2.0F, h + size / 2.0F, z - size / 2.0F, z + size / 2.0F, mc);
               }
            }
            break;
         case SADDLE:
            k.box(-bw - 0.06F, bw + 0.06F, top - sp.bh * 0.35F, top + 0.06F, -bl * 0.45F, bl * 0.55F, mc);
            break;
         case BACK_STRIPE:
            k.box(-0.9F, 0.9F, top - 0.02F, top + 0.1F, -bl - 0.05F, bl + 0.05F, p.accent());
            break;
         case PANDA:
            k.box(-bw - 0.06F, bw + 0.06F, bot + sp.bh * 0.15F, top + 0.06F, -bl + 0.9F, -bl + 2.3F, p.second());
            break;
         case BANDS:
            for (float z = -bl + 0.8F; z < bl - 0.3F; z++) {
               k.box(-bw - 0.18F, bw + 0.18F, bot + sp.bh * 0.2F, top + 0.22F, z - 0.3F, z + 0.3F, mc);
            }
      }
   }

   private static void tail(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, PetSpecies.Spec sp, float th, float bl) {
      float w = Mth.sin(mo.age() * 0.1F) + mo.swing() * 0.6F;
      int m = p.main();
      int tip = c(p, sp.tailTip);
      switch (sp.tail) {
         case NUB:
            k.cube(0.0F, th, bl + 0.3F, 0.6F, tip);
            break;
         case SHORT:
            k.beam(0.0F, th, bl, 0.0F, th + 1.2F, bl + 1.1F, 0.45F, tip);
            break;
         case LONG:
            k.chain(
               new float[][]{{0.0F, th, bl}, {w * 0.3F, th - 0.6F, bl + 1.8F}, {w * 0.7F, th - 1.6F, bl + 3.2F}, {w, th - 2.2F, bl + 4.4F}},
               new float[]{0.45F, 0.4F, 0.35F, 0.3F},
               m,
               tip
            );
            break;
         case CURL:
            k.chain(
               new float[][]{{0.0F, th, bl}, {w * 0.3F, th + 1.2F, bl + 1.6F}, {w * 0.7F, th + 3.2F, bl + 2.2F}, {w, th + 4.4F, bl + 1.2F}},
               new float[]{0.65F, 0.6F, 0.55F, 0.45F},
               m,
               tip
            );
            break;
         case BUSHY:
            k.chain(
               new float[][]{{0.0F, th, bl}, {w * 0.4F, th - 0.3F, bl + 2.0F}, {w * 0.8F, th + 0.2F, bl + 3.8F}, {w, th + 0.9F, bl + 5.0F}},
               new float[]{0.8F, 1.1F, 1.0F, 0.7F},
               m,
               tip
            );
            break;
         case RINGED:
            float[][] pts = new float[][]{
               {0.0F, th, bl}, {w * 0.2F, th - 0.4F, bl + 1.2F}, {w * 0.45F, th - 0.6F, bl + 2.4F}, {w * 0.7F, th - 0.5F, bl + 3.6F}, {w, th - 0.1F, bl + 4.8F}
            };

            for (int i = 0; i < pts.length - 1; i++) {
               k.beam(pts[i][0], pts[i][1], pts[i][2], pts[i + 1][0], pts[i + 1][1], pts[i + 1][2], 0.8F, i % 2 == 0 ? m : p.second());
            }
            break;
         case PADDLE:
            k.flat(0.0F, th - 0.6F, bl, w * 0.3F, th - 1.4F, bl + 3.4F, 1.2F, 0.3F, HeadwearModels.AXIS_X, p.second());
            break;
         case TUFT:
            k.chain(new float[][]{{0.0F, th, bl}, {w * 0.3F, th - 1.2F, bl + 1.0F}, {w * 0.6F, th - 2.6F, bl + 1.5F}}, new float[]{0.22F, 0.2F, 0.18F}, m, m);
            k.cube(w * 0.6F, th - 3.0F, bl + 1.6F, 0.55F, tip);
            break;
         case PIG:
            k.chain(
               new float[][]{{0.0F, th, bl}, {0.6F, th + 0.6F, bl + 0.6F}, {0.0F, th + 1.0F, bl + 0.9F}, {-0.4F, th + 0.5F, bl + 1.1F}},
               new float[]{0.25F, 0.25F, 0.22F, 0.2F},
               m,
               m
            );
            break;
         case PUFF:
            k.cube(0.0F, th, bl + 0.5F, 0.95F, tip == p.main() ? p.accent() : tip);
            break;
         case SKUNK:
            k.chain(
               new float[][]{{0.0F, th, bl}, {w * 0.2F, th + 2.4F, bl + 2.2F}, {w * 0.5F, th + 5.4F, bl + 2.4F}, {w, th + 7.2F, bl + 1.0F}},
               new float[]{1.0F, 1.5F, 1.6F, 1.1F},
               m,
               tip
            );
            break;
         case HORSE:
            k.chain(
               new float[][]{{0.0F, th + 0.4F, bl}, {w * 0.3F, th - 1.4F, bl + 1.4F}, {w * 0.6F, th - 3.4F, bl + 1.9F}, {w, th - 4.6F, bl + 1.8F}},
               new float[]{0.7F, 0.65F, 0.55F, 0.45F},
               tip,
               tip
            );
            break;
         case THICK:
            k.chain(
               new float[][]{{0.0F, th, bl - 0.5F}, {w * 0.4F, th - 0.6F, bl + 2.5F}, {w * 0.9F, th - 1.2F, bl + 5.0F}, {w * 1.2F, th - 1.5F, bl + 7.0F}},
               new float[]{sp.bw * 0.7F, sp.bw * 0.5F, sp.bw * 0.32F, 0.2F},
               m,
               m
            );
            if (sp.has(274877906944L)) {
               for (int side = -1; side <= 1; side += 2) {
                  k.beam2(w * 1.0F, th - 1.3F, bl + 5.6F, w + side * 1.6F, th - 0.2F, bl + 6.4F, 0.3F, 0.05F, -1186868);
               }
            }
            break;
         case SQUIRREL:
            k.chain(
               new float[][]{{0.0F, th - 0.5F, bl}, {w * 0.2F, th + 1.6F, bl + 2.0F}, {w * 0.4F, th + 4.6F, bl + 2.2F}, {w * 0.6F, th + 6.2F, bl + 0.8F}},
               new float[]{0.9F, 1.4F, 1.5F, 1.0F},
               m,
               tip
            );
      }
   }

   private static void head(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, PetSpecies.Spec sp, float hz, float h0, float bw, float bl) {
      float hs = sp.hs;
      float face = hz - hs;
      float ht = h0 + 2.0F * hs;
      if (sp.has(1L)) {
         k.box(-hs - 1.0F, hs + 1.0F, h0 - 0.8F, ht + 0.9F, hz - hs * 0.35F, hz + hs + 0.8F, p.second());
      }

      if (sp.has(1073741824L)) {
         k.box(-hs - 2.2F, hs + 2.2F, h0 + hs * 0.6F, ht + 2.6F, hz + hs * 0.4F, hz + hs * 0.9F, p.second());
      }

      k.box(-hs, hs, h0, ht, hz - hs, hz + hs, p.main());
      float e = Math.max(0.55F, hs * (sp.has(68719476736L) ? 0.48F : 0.4F));
      float eh = h0 + hs * (!sp.has(4503599627370496L) && !sp.has(18014398509481984L) ? 1.05F : 1.4F);
      float ex = hs * 0.5F;
      if (sp.has(16384L)) {
         k.box(-hs - 0.05F, hs + 0.05F, eh - 0.35F, eh + e + 0.35F, face - 0.06F, hz + hs * 0.2F, p.second());
      }

      if (sp.has(32768L)) {
         k.pair(ex - e * 0.9F, ex + e * 0.9F, eh - 0.5F, eh + e + 0.5F, face - 0.06F, face + 0.2F, p.second());
      }

      if (sp.has(65536L)) {
         k.box(-0.45F, 0.45F, eh, ht + 0.05F, face - 0.06F, hz + hs, p.accent());
      }

      if (sp.has(131072L)) {
         k.box(-0.45F, 0.45F, h0 + hs * 0.6F, ht + 0.02F, face - 0.07F, face + 0.1F, p.accent());
      }

      if (sp.has(262144L)) {
         k.box(-hs + 0.1F, hs - 0.1F, h0 - 0.05F, h0 + hs * 0.85F, face - 0.07F, face + 0.4F, p.accent());
      }

      if (sp.has(524288L)) {
         k.pair(ex - 0.15F, ex + 0.15F, h0 + hs * 0.3F, eh, face - 0.09F, face + 0.05F, p.second());
      }

      k.eye(-ex - e / 2.0F, -ex + e / 2.0F, eh, eh + e * 1.1F, face, -14804455);
      k.eye(ex - e / 2.0F, ex + e / 2.0F, eh, eh + e * 1.1F, face, -14804455);
      if (sp.has(2048L)) {
         k.box(-hs - 0.15F, hs + 0.15F, eh - 0.2F, ht + 0.6F, face - 0.45F, hz + hs * 0.3F, p.main());
      }

      if (sp.has(4503599627370496L)) {
         float mw = hs * 1.12F;
         float mt = h0 + hs * 1.1F;
         float mf = face - sp.sn;
         float open = mo.mouth() * 1.4F;
         if (open > 0.05F) {
            k.box(-mw + 0.3F, mw - 0.3F, h0 - 0.2F - open, h0 + 0.2F, mf + 0.1F, face + 0.3F, -11920350);
            k.pair(mw * 0.45F, mw * 0.7F, h0 - 0.2F - open, h0 + 0.3F - open, mf + 0.05F, mf + 0.4F, -1);
         }

         k.box(-mw, mw, h0 - 0.3F, mt, mf, face + 0.8F, p.accent());
         k.box(-mw + 0.45F, mw - 0.45F, mt, mt + 0.4F, mf + 0.35F, face + 0.4F, p.accent());
         k.box(-mw + 0.45F, mw - 0.45F, h0 - 0.7F - open, h0 - 0.3F - open, mf + 0.35F, face + 0.4F, p.accent());
         k.pair(mw * 0.3F, mw * 0.62F, mt + 0.35F, mt + 0.5F, mf + 0.55F, mf + 1.15F, -10864056);
         k.pair(ex - e * 0.85F, ex + e * 0.85F, eh - 0.35F, eh + e * 1.1F + 0.45F, face + 0.15F, hz, p.main());
      } else if (sp.has(18014398509481984L)) {
         float sw = hs * 0.9F;
         float tip = face - sp.sn;
         k.box(-sw, sw, h0 - 0.2F, h0 + hs * 1.35F, tip, face + 0.4F, p.main());
         k.box(-sw * 0.55F, sw * 0.55F, h0 + hs * 0.95F, h0 + hs * 1.2F, tip - 0.08F, tip + 0.1F, p.second());
         k.box(-sw * 0.25F, sw * 0.25F, h0 + hs * 0.35F, h0 + hs * 0.95F, tip - 0.06F, tip + 0.05F, p.second());
      } else if (sp.sn > 0.0F) {
         float sw = hs * 0.55F;
         float tip = face - sp.sn;
         float st = h0 + hs * 0.95F;
         int sc = sp.has(33554432L) ? p.second() : p.accent();
         k.box(-sw, sw, h0 + 0.05F, st, tip, face + 0.3F, sc);
         if (sp.has(8388608L)) {
            k.box(-sw * 0.8F, sw * 0.8F, h0 + 0.3F, st - 0.1F, tip - 0.25F, tip + 0.05F, p.second());
            k.pair(0.25F, 0.55F, h0 + hs * 0.45F, h0 + hs * 0.6F, tip - 0.32F, tip - 0.2F, -14804455);
         } else {
            k.box(-0.5F, 0.5F, st - 0.75F, st + 0.05F, tip - 0.18F, tip + 0.1F, -14804455);
         }

         if (mo.mouth() > 0.05F) {
            float open = mo.mouth() * Math.min(1.6F, hs * 0.6F);
            k.box(-sw * 0.85F, sw * 0.85F, h0 - open, h0 + 0.12F, tip + 0.02F, face + 0.2F, -11920350);
            k.box(-sw * 0.9F, sw * 0.9F, h0 - open - 0.45F, h0 - open, tip + 0.1F, face + 0.3F, sc);
            k.box(-sw * 0.5F, sw * 0.5F, h0 - open + 0.02F, h0 - open + 0.25F, tip + 0.4F, face, -876368);
            if (mo.mouth() > 0.55F) {
               k.pair(sw * 0.35F, sw * 0.62F, h0 - 0.35F, h0 + 0.12F, tip + 0.12F, tip + 0.42F, -1);
               k.pair(sw * 0.35F, sw * 0.62F, h0 - open, h0 - open + 0.42F, tip + 0.18F, tip + 0.48F, -1);
            }
         }

         if (sp.has(4194304L)) {
            k.box(-0.45F, 0.45F, h0 - 0.7F, h0 + 0.2F, tip + 0.1F, tip + 0.9F, -876368);
         }

         if (sp.has(256L)) {
            for (int side = -1; side <= 1; side += 2) {
               k.beam2(side * sw * 0.8F, h0 + 0.3F, tip + 0.4F, side * (sw + 0.3F), h0 + 1.6F, tip - 0.5F, 0.25F, 0.08F, -1186868);
            }
         }

         if (sp.has(16777216L)) {
            k.box(-0.45F, 0.45F, h0 - 0.6F, h0 + 0.1F, tip + 0.05F, tip + 0.3F, -1);
         }

         if (sp.has(17179869184L)) {
            for (float z = tip + 0.3F; z < face - 0.2F; z += 0.8F) {
               k.pair(sw - 0.1F, sw + 0.08F, h0 - 0.1F, h0 + 0.4F, z - 0.15F, z + 0.15F, -1);
            }
         }

         if (sp.has(128L)) {
            k.taper(-0.7F, 0.7F, tip + 0.1F, tip + 1.3F, st, -0.12F, 0.12F, tip + 0.3F, tip + 0.55F, st + 2.6F, -1186868);
            k.taper(-0.5F, 0.5F, tip + 1.6F, tip + 2.4F, st, -0.1F, 0.1F, tip + 1.9F, tip + 2.1F, st + 1.2F, -1186868);
         }

         if (sp.has(2147483648L)) {
            k.taper(-0.5F, 0.5F, tip + 0.1F, tip + 1.0F, st, -0.08F, 0.08F, tip + 0.2F, tip + 0.35F, st + 1.4F, -1186868);
         }
      } else if (sp.has(72057594037927936L)) {
         k.box(-hs * 0.72F, hs * 0.72F, h0 + hs * 0.3F, h0 + hs * 0.78F, face - 2.6F, face + 0.3F, p.second());
         k.pair(hs * 0.18F, hs * 0.34F, h0 + hs * 0.78F, h0 + hs * 0.84F, face - 2.3F, face - 1.9F, -14804455);
      } else if (sp.has(512L)) {
         float w = Mth.sin(mo.age() * 0.07F) * 0.8F;
         k.chain(
            new float[][]{{0.0F, h0 + hs * 0.9F, face + 0.3F}, {0.0F, h0 - 0.4F, face - 1.0F}, {w * 0.5F, h0 - 2.2F, face - 1.2F}, {w, h0 - 3.0F, face - 2.0F}},
            new float[]{hs * 0.38F, hs * 0.32F, hs * 0.26F, hs * 0.22F},
            p.main(),
            p.main()
         );
         if (sp.has(256L)) {
            for (int side = -1; side <= 1; side += 2) {
               k.beam2(side * hs * 0.45F, h0 + 0.5F, face, side * hs * 0.55F, h0 - 1.0F, face - 1.4F, 0.32F, 0.1F, -1186868);
            }
         }
      } else {
         k.box(-0.45F, 0.45F, h0 + hs * 0.55F, h0 + hs * 0.85F, face - 0.12F, face + 0.05F, -14804455);
         if (mo.mouth() > 0.05F) {
            k.box(-hs * 0.35F, hs * 0.35F, h0 + hs * 0.2F - mo.mouth() * hs * 0.3F, h0 + hs * 0.45F, face - 0.14F, face + 0.05F, -11920350);
         }
      }

      if (sp.has(1048576L)) {
         float zf = sp.sn > 0.0F ? face - sp.sn * 0.6F : face;

         for (int side = -1; side <= 1; side += 2) {
            k.beam(side * hs * 0.45F, h0 + hs * 0.55F, zf, side * (hs + 1.6F), h0 + hs * 0.75F, zf - 0.4F, 0.06F, -1);
            k.beam(side * hs * 0.45F, h0 + hs * 0.4F, zf, side * (hs + 1.5F), h0 + hs * 0.2F, zf - 0.3F, 0.06F, -1);
         }
      }

      if (sp.has(137438953472L)) {
         for (int side = -1; side <= 1; side += 2) {
            for (int i = 0; i < 3; i++) {
               float wave = Mth.sin(mo.age() * 0.15F + i) * 0.3F;
               k.beam(side * hs, h0 + hs * (0.9F + i * 0.45F), hz, side * (hs + 1.8F), h0 + hs * (0.9F + i * 0.8F) + wave, hz + 0.8F, 0.28F, p.second());
            }
         }
      }

      ears(k, mo, p, sp, hz, h0, ht);
      horns(k, p, sp, hz, h0, ht);
      if (sp.has(2097152L)) {
         float cw = Math.min(bw, hs) + 0.15F;
         k.box(-cw, cw, h0 - 0.1F, h0 + 0.9F, -bl - 0.4F, -bl + 0.5F, -2608838);
         k.box(-0.4F, 0.4F, h0 - 0.9F, h0 - 0.1F, -bl - 0.6F, -bl - 0.35F, -669620);
      }
   }

   private static void ears(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, PetSpecies.Spec sp, float hz, float h0, float ht) {
      float hs = sp.hs;
      int ec = c(p, sp.earColor);
      float flop = mo.bob() * 0.8F;
      switch (sp.ear) {
         case POINTED:
            k.taperPair(hs * 0.2F, hs * 0.95F, hz - 0.7F, hz + 0.7F, ht - 0.1F, hs * 0.5F, hs * 0.75F, hz - 0.15F, hz + 0.15F, ht + hs * 0.8F, ec);
            break;
         case BIG:
            k.taperPair(hs * 0.1F, hs * 1.1F, hz - 0.9F, hz + 0.9F, ht - 0.1F, hs * 0.6F, hs * 0.95F, hz - 0.2F, hz + 0.2F, ht + hs * 1.7F, ec);
            k.taperPair(hs * 0.35F, hs * 0.85F, hz - 1.0F, hz - 0.85F, ht, hs * 0.65F, hs * 0.8F, hz - 0.95F, hz - 0.9F, ht + hs * 1.35F, -876368);
            break;
         case ROUND:
            k.pair(hs * 0.45F, hs * 1.05F, ht - 0.3F, ht + hs * 0.5F, hz - 0.3F, hz + 0.4F, ec);
            break;
         case MOUSE:
            k.pair(hs * 0.35F, hs * 1.35F, ht - 0.8F, ht + hs * 0.9F, hz - 0.1F, hz + 0.3F, ec);
            k.pair(hs * 0.55F, hs * 1.15F, ht - 0.5F, ht + hs * 0.7F, hz - 0.22F, hz - 0.08F, -876368);
            break;
         case FLOPPY:
            for (int side = -1; side <= 1; side += 2) {
               k.flat(
                  side * hs * 0.95F, ht - 0.3F, hz - 0.2F, side * (hs + 0.6F), ht - hs * 1.3F - flop, hz + 0.1F, hs * 0.42F, 0.3F, HeadwearModels.AXIS_Z, ec
               );
            }
            break;
         case SIDE:
            for (int side = -1; side <= 1; side += 2) {
               k.flat(
                  side * hs * 0.85F,
                  ht - hs * 0.4F,
                  hz + hs * 0.4F,
                  side * (hs + 1.5F),
                  ht - hs * 0.1F + flop * 0.3F,
                  hz + hs * 0.5F,
                  0.6F,
                  0.28F,
                  HeadwearModels.AXIS_Z,
                  ec
               );
            }
            break;
         case LONG:
            for (int side = -1; side <= 1; side += 2) {
               k.flat(
                  side * hs * 0.45F, ht - 0.3F, hz + hs * 0.3F, side * hs * 0.8F, ht + hs * 1.5F - flop, hz + hs * 0.5F, 0.6F, 0.3F, HeadwearModels.AXIS_X, ec
               );
            }
            break;
         case FLUFFY:
            for (int side = -1; side <= 1; side += 2) {
               k.cube(side * hs * 1.05F, ht - 0.4F, hz + 0.3F, hs * 0.55F, ec);
               k.cube(side * hs * 1.05F, ht - 0.4F, hz - 0.1F, hs * 0.35F, p.accent());
            }
            break;
         case ELEPHANT:
            float flap = Mth.sin(mo.age() * 0.08F) * 0.5F;

            for (int side = -1; side <= 1; side += 2) {
               k.flat(side * hs * 0.95F, ht - 0.4F, hz + 0.3F, side * (hs + 0.6F + flap), h0 + 0.3F, hz + 0.9F, hs * 0.9F, 0.3F, HeadwearModels.AXIS_Z, ec);
            }
            break;
         case TINY:
            k.pair(hs * 0.55F, hs * 0.85F, ht - 0.1F, ht + 0.65F, hz + 0.2F, hz + 0.7F, ec);
      }
   }

   private static void horns(PetModels.Kit k, PetModels.Pal p, PetSpecies.Spec sp, float hz, float h0, float ht) {
      float hs = sp.hs;
      if (sp.has(4L)) {
         k.beam2(0.0F, ht - 0.3F, hz - hs * 0.4F, 0.0F, ht + 3.2F, hz - hs - 1.0F, 0.5F, 0.08F, -797845);
      }

      if (sp.has(2147483648L)) {
         for (int side = -1; side <= 1; side += 2) {
            k.beam2(side * hs * 0.5F, ht - 0.4F, hz - hs * 0.6F, side * hs * 0.6F, ht + 1.4F, hz - hs - 2.2F, 0.45F, 0.06F, -1186868);
         }
      }

      int sec = p.second();

      for (int side = -1; side <= 1; side += 2) {
         if (sp.has(8L)) {
            k.chain(
               new float[][]{{side * hs * 0.45F, ht - 0.2F, hz + 0.2F}, {side * (hs + 1.0F), ht + 1.8F, hz + 0.6F}, {side * (hs + 1.6F), ht + 3.8F, hz + 0.1F}},
               new float[]{0.32F, 0.28F, 0.22F},
               sec,
               sec
            );
            k.beam(side * (hs + 1.0F), ht + 1.8F, hz + 0.6F, side * (hs + 0.3F), ht + 3.2F, hz - 0.6F, 0.22F, sec);
            k.beam(side * (hs + 1.35F), ht + 2.8F, hz + 0.35F, side * (hs + 2.4F), ht + 3.6F, hz + 0.9F, 0.2F, sec);
         }

         if (sp.has(16L)) {
            k.beam(side * hs * 0.5F, ht - 0.2F, hz + 0.3F, side * (hs + 0.8F), ht + 0.6F, hz + 0.4F, 0.35F, sec);
            k.flat(side * (hs + 0.6F), ht + 0.6F, hz + 0.4F, side * (hs + 3.4F), ht + 2.0F, hz + 0.2F, 1.5F, 0.3F, HeadwearModels.AXIS_Z, sec);

            for (int t = 0; t < 3; t++) {
               float tx = side * (hs + 1.4F + t * 0.9F);
               k.beam(tx, ht + 1.2F + t * 0.35F, hz + 0.3F, tx + side * 0.3F, ht + 2.9F + t * 0.3F, hz + 0.3F, 0.18F, sec);
            }
         }

         if (sp.has(32L)) {
            k.chain(
               new float[][]{
                  {side * hs * 0.8F, ht - 0.8F, hz},
                  {side * (hs + 1.6F), ht - 0.4F, hz - 0.2F},
                  {side * (hs + 2.6F), ht + 0.8F, hz - 0.6F},
                  {side * (hs + 2.8F), ht + 2.0F, hz - 1.2F}
               },
               new float[]{0.5F, 0.42F, 0.32F, 0.2F},
               -1186868,
               -1186868
            );
         }

         if (sp.has(67108864L) && sp.plan == PetSpecies.Plan.QUAD) {
            k.beam(side * hs * 0.62F, ht + hs * 0.8F, hz, side * hs * 0.7F, ht + hs * 0.8F + 1.3F, hz, 0.12F, sec);
         }

         if (sp.has(134217728L)) {
            k.flat(
               side * hs * 0.9F, h0 + hs * 0.5F, hz - hs * 0.5F, side * (hs + 1.3F), h0 - 0.3F, hz - hs * 0.2F, 0.9F, 0.4F, HeadwearModels.AXIS_Z, p.accent()
            );
         }
      }

      if (sp.has(64L)) {
         k.pair(hs * 0.35F, hs * 0.8F, ht, ht + 0.9F, hz - 0.1F, hz + 0.6F, sec);
      }

      if (sp.has(268435456L)) {
         k.cube(0.0F, ht + 0.4F, hz + 0.2F, hs * 0.6F, p.main());
         k.cube(-hs * 0.5F, ht + 0.1F, hz + 0.4F, hs * 0.45F, p.second());
         k.cube(hs * 0.5F, ht + 0.1F, hz + 0.4F, hs * 0.45F, p.second());
      }
   }

   private static void upright(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, PetSpecies.Spec sp) {
      float s = mo.swing();
      float br = mo.breathe();
      float bw = sp.bw;
      float bd = sp.bl;
      float bot = sp.legH + br;
      float top = sp.legH + sp.bh + br;
      int legC = c(p, sp.legColor);
      int footC = c(p, sp.footColor < 0 ? sp.legColor : sp.footColor);
      if (sp.has(1125899906842624L)) {
         k.pair(bw * 0.2F, bw * 0.9F, 0.0F, 0.8F, -bd - 2.2F, bd * 0.5F, p.main());
      }

      if (sp.legH > 1.0F) {
         k.leg(-bw * 0.5F, sp.legH + 0.3F, 0.0F, s, sp.legW, legC, footC);
         k.leg(bw * 0.5F, sp.legH + 0.3F, 0.0F, -s, sp.legW, legC, footC);
      } else {
         k.box(-bw * 0.85F, -bw * 0.15F, 0.0F, 0.7F, -bd - 0.9F + s, bd * 0.3F, footC);
         k.box(bw * 0.15F, bw * 0.85F, 0.0F, 0.7F, -bd - 0.9F - s, bd * 0.3F, footC);
      }

      k.box(-bw, bw, bot, top, -bd, bd, p.main());
      k.box(-bw + 0.4F, bw - 0.4F, top, top + 0.4F, -bd + 0.3F, bd - 0.3F, p.main());
      k.box(-bw * 0.7F, bw * 0.7F, bot + 0.4F, top - 0.6F, -bd - 0.12F, -bd + 0.3F, p.accent());
      if (sp.has(4398046511104L)) {
         k.box(-bw * 0.55F, bw * 0.55F, bot + 0.5F, bot + sp.bh * 0.4F, -bd - 0.4F, -bd + 0.1F, p.second());
      }

      float arm = -s * 0.8F;

      for (int side = -1; side <= 1; side += 2) {
         if (sp.has(1099511627776L)) {
            float flap = Mth.abs(Mth.sin(mo.age() * 0.15F)) * 0.5F;
            k.flat(side * bw, top - 0.6F, 0.0F, side * (bw + 0.7F + flap), bot + sp.bh * 0.3F, side * arm, 1.2F, 0.3F, HeadwearModels.AXIS_Z, p.second());
         }

         if (sp.has(2199023255552L)) {
            float len = sp.bh * (sp.has(2251799813685248L) ? 0.85F : (sp.has(4398046511104L) ? 0.45F : 0.65F));
            float hx = side * (bw + 0.6F);
            float hh = top - 0.5F - len;
            float hz = side * arm * len * 0.5F - 0.6F;
            k.beam(side * (bw + 0.4F), top - 0.5F, 0.0F, hx, hh, hz, sp.legW * 0.9F, p.main());
            if (sp.has(35184372088832L)) {
               k.box(hx - 0.3F, hx + 0.3F, hh - 0.8F, hh, hz - 0.4F, hz - 0.1F, -1515312);
            }
         }
      }

      float w = Mth.sin(mo.age() * 0.1F);
      switch (sp.tail) {
         case LONG:
            k.chain(
               new float[][]{{0.0F, bot + 0.8F, bd}, {w * 0.3F, 0.4F, bd + 1.6F}, {w, 0.3F, bd + 3.4F}}, new float[]{0.35F, 0.3F, 0.2F}, p.main(), p.second()
            );
            break;
         case CURL:
            k.chain(
               new float[][]{{0.0F, bot + 1.0F, bd}, {w * 0.3F, bot - 0.2F, bd + 2.0F}, {w, bot + 2.4F, bd + 3.2F}, {w * 0.6F, bot + 4.2F, bd + 2.4F}},
               new float[]{0.45F, 0.4F, 0.35F, 0.3F},
               p.main(),
               p.main()
            );
            break;
         case THICK:
            k.chain(
               new float[][]{{0.0F, bot + 1.2F, bd}, {w * 0.2F, 0.9F, bd + 2.4F}, {w * 0.4F, 0.3F, bd + 4.8F}},
               new float[]{1.0F, 0.7F, 0.35F},
               p.main(),
               p.main()
            );
      }

      float hs = sp.hs;
      float hz = -bd * 0.2F;
      float h0 = top + mo.bob() * 0.3F - 0.2F;
      float face = hz - hs;
      float ht = h0 + 2.0F * hs;
      k.box(-hs, hs, h0, ht, hz - hs, hz + hs, p.main());
      float e = Math.max(0.55F, hs * (sp.has(68719476736L) ? 0.42F : 0.32F));
      float eh = h0 + hs * 1.0F;
      float ex = hs * 0.48F;
      if (sp.has(8796093022208L)) {
         k.box(-hs * 0.85F, hs * 0.85F, h0 + 0.25F, ht - 0.35F, face - 0.08F, face + 0.3F, p.accent());
      }

      if (sp.has(16384L)) {
         k.pair(hs * 0.1F, hs * 0.85F, eh - 0.3F, eh + e + 0.2F, face - 0.14F, face + 0.1F, p.second());
      }

      if (sp.has(32768L)) {
         k.pair(ex - e * 0.8F, ex + e * 0.8F, eh - 0.4F, eh + e + 0.4F, face - 0.12F, face + 0.1F, p.second());
      }

      if (sp.has(68719476736L)) {
         k.pair(ex - e * 0.75F, ex + e * 0.75F, eh - e * 0.35F, eh + e * 1.45F, face - 0.16F, face + 0.05F, -871885);
      }

      k.eye(-ex - e / 2.0F, -ex + e / 2.0F, eh, eh + e * 1.1F, face - (sp.has(68719476736L) ? 0.1F : 0.0F), -14804455);
      k.eye(ex - e / 2.0F, ex + e / 2.0F, eh, eh + e * 1.1F, face - (sp.has(68719476736L) ? 0.1F : 0.0F), -14804455);
      if (sp.has(17592186044416L)) {
         k.beam2(0.0F, h0 + hs * 0.8F, face + 0.2F, 0.0F, h0 + hs * 0.55F, face - 1.4F, hs * 0.3F, 0.08F, c(p, sp.beakColor));
      } else if (sp.sn > 0.0F) {
         k.box(-hs * 0.5F, hs * 0.5F, h0 + 0.1F, h0 + hs * 0.9F, face - sp.sn, face + 0.3F, p.accent());
         k.box(-0.45F, 0.45F, h0 + hs * 0.6F, h0 + hs * 0.95F, face - sp.sn - 0.15F, face - sp.sn + 0.05F, -14804455);
      } else if (sp.has(549755813888L)) {
         k.box(-hs * 0.35F, hs * 0.35F, h0 + hs * 0.4F, h0 + hs * 1.0F, face - 0.55F, face + 0.1F, p.second());
      } else {
         k.box(-0.4F, 0.4F, h0 + hs * 0.55F, h0 + hs * 0.8F, face - 0.12F, face + 0.05F, -14804455);
      }

      if (sp.has(67108864L)) {
         k.taperPair(hs * 0.45F, hs * 1.0F, hz - 0.6F, hz + 0.6F, ht - 0.1F, hs * 0.8F, hs * 0.95F, hz - 0.1F, hz + 0.1F, ht + hs * 0.7F, p.second());
      }

      ears(k, mo, p, sp, hz, h0, ht);
   }

   private static void bird(PetModels.Kit k, PetModels.Motion mo, PetModels.Pal p, PetSpecies.Spec sp) {
      float s = mo.swing();
      float br = mo.breathe();
      float bw = sp.bw;
      float bl = sp.bl;
      float bot = sp.legH + br;
      float top = sp.legH + sp.bh + br;
      int legC = c(p, sp.legColor);

      for (int side = -1; side <= 1; side += 2) {
         float step = side * s * Math.min(2.0F, sp.legH) * 0.5F;
         k.beam(side * bw * 0.45F, bot + 0.4F, 0.2F, side * bw * 0.45F, 0.3F, 0.2F - step, Math.max(0.2F, sp.legW), legC);
         k.box(side * bw * 0.45F - 0.5F, side * bw * 0.45F + 0.5F, 0.0F, 0.3F, -0.9F - step, 0.4F - step, legC);
      }

      k.box(-bw, bw, bot, top, -bl, bl, p.main());
      k.box(-bw + 0.5F, bw - 0.5F, bot - 0.4F, top + 0.4F, -bl + 0.5F, bl - 0.5F, p.main());
      if (sp.has(70368744177664L)) {
         k.box(-bw * 0.75F, bw * 0.75F, bot + 0.4F, top - 0.6F, -bl - 0.12F, -bl + 0.5F, p.accent());
      }

      float flap = Mth.abs(Mth.sin(mo.age() * 0.12F)) * 0.2F;
      k.box(bw - 0.15F, bw + 0.45F + flap, bot + 0.5F, top - 0.3F, -bl + 0.6F, bl + 0.9F, p.second());
      k.box(-bw - 0.45F - flap, -bw + 0.15F, bot + 0.5F, top - 0.3F, -bl + 0.6F, bl + 0.9F, p.second());
      switch (sp.tail) {
         case SHORT:
            k.flat(0.0F, top - 0.8F, bl, 0.0F, top + 0.2F, bl + 2.0F, bw * 0.7F, 0.3F, HeadwearModels.AXIS_X, c(p, sp.tailTip == 0 ? 1 : sp.tailTip));
            break;
         case LONG:
            k.flat(0.0F, top - 1.0F, bl, 0.0F, bot - 1.6F, bl + 4.0F, bw * 0.55F, 0.3F, HeadwearModels.AXIS_X, c(p, sp.tailTip == 0 ? 1 : sp.tailTip));
         case CURL:
         case BUSHY:
         case RINGED:
         case PADDLE:
         case TUFT:
         case PIG:
         case SKUNK:
         case HORSE:
         case THICK:
         case SQUIRREL:
         default:
            break;
         case PUFF:
            k.cube(0.0F, top - 0.2F, bl + 0.6F, 1.3F, p.second());
            break;
         case FAN:
            for (int i = -4; i <= 4; i++) {
               float x = i * 1.3F;
               float h = top + 5.5F - Math.abs(i) * 0.6F;
               k.flat(0.0F, top - 0.6F, bl, x, h, bl + 2.2F, 0.9F, 0.2F, HeadwearModels.AXIS_X, p.second());
               k.cube(x, h - 0.3F, bl + 2.25F, 0.5F, p.accent());
               k.cube(x, h - 0.3F, bl + 2.1F, 0.28F, -867792);
            }
            break;
         case CHICKEN:
            k.flat(0.0F, top - 0.4F, bl, 0.0F, top + 2.2F, bl + 1.6F, bw * 0.8F, 0.35F, HeadwearModels.AXIS_X, p.second());
      }

      float hs = sp.hs;
      float hz;
      float h0;
      if (sp.neck > 0.0F) {
         hz = -bl - hs * 0.2F;
         h0 = top + sp.neck - hs * 0.4F + mo.bob() * 0.4F;
         int nc = sp.legColor == 2 ? p.accent() : p.main();
         k.chain(
            new float[][]{{0.0F, top - 0.6F, -bl + 0.8F}, {0.0F, top + sp.neck * 0.45F, -bl - 1.0F}, {0.0F, h0 + hs * 0.4F, hz + hs * 0.6F}},
            new float[]{0.8F, 0.6F, 0.5F},
            nc,
            nc
         );
      } else {
         hz = -bl - hs * 0.2F + (sp.bh > sp.bl * 1.6F ? bl * 0.7F : 0.0F);
         h0 = top - hs * 0.5F + mo.bob() * 0.4F;
      }

      float face = hz - hs;
      float ht = h0 + 2.0F * hs;
      k.box(-hs, hs, h0, ht, hz - hs, hz + hs, sp.has(562949953421312L) ? p.accent() : p.main());
      if (sp.has(8796093022208L)) {
         k.pair(hs * 0.2F, hs * 0.9F, h0 + hs * 0.4F, h0 + hs * 1.5F, face - 0.08F, face + 0.3F, p.second());
      }

      if (sp.has(262144L)) {
         k.pair(hs * 0.4F, hs * 1.02F, h0 + hs * 0.3F, h0 + hs * 1.1F, face - 0.06F, face + 0.6F, -461072);
      }

      float e = Math.max(0.5F, hs * 0.36F);
      float eh = h0 + hs * 1.05F;
      float ex = hs * 0.52F;
      k.eye(-ex - e / 2.0F, -ex + e / 2.0F, eh, eh + e * 1.1F, face, -14804455);
      k.eye(ex - e / 2.0F, ex + e / 2.0F, eh, eh + e * 1.1F, face, -14804455);
      int beak = c(p, sp.beakColor);
      if (sp.sn >= 3.9F) {
         k.box(-hs * 0.4F, hs * 0.4F, h0 + hs * 0.4F, h0 + hs * 1.4F, face - sp.sn + 0.8F, face + 0.2F, p.accent());
         k.box(-hs * 0.42F, hs * 0.42F, h0 + hs * 0.4F, h0 + hs * 1.4F, face - sp.sn, face - sp.sn + 0.8F, -14804455);
      } else if (sp.sn >= 2.9F && sp.has(8796093022208L)) {
         k.box(-hs * 0.4F, hs * 0.4F, h0 + hs * 0.3F, h0 + hs * 1.3F, face - 1.6F, face + 0.2F, p.accent());
         k.box(-hs * 0.42F, hs * 0.42F, h0 + hs * 0.3F, h0 + hs * 1.3F, face - 0.9F, face - 0.6F, -865972);
      } else if (sp.sn >= 2.9F) {
         k.chain(
            new float[][]{{0.0F, h0 + hs, face + 0.1F}, {0.0F, h0 + hs * 0.8F, face - 1.2F}, {0.0F, h0 - 0.4F, face - 1.6F}},
            new float[]{hs * 0.35F, hs * 0.28F, 0.15F},
            -726816,
            beak
         );
      } else if (sp.sn >= 1.9F) {
         k.chain(
            new float[][]{{0.0F, h0 + hs * 1.0F, face + 0.2F}, {0.0F, h0 + hs * 1.0F, face - 1.0F}, {0.0F, h0 + hs * 0.3F, face - 1.5F}},
            new float[]{hs * 0.38F, hs * 0.25F, 0.12F},
            beak,
            beak
         );
      } else if (sp.sn >= 0.9F) {
         k.box(-hs * 0.45F, hs * 0.45F, h0 + hs * 0.45F, h0 + hs * 0.8F, face - 1.8F, face + 0.2F, beak);
         if (sp.legColor == 9) {
            k.box(-hs * 0.5F, hs * 0.5F, h0 + hs * 0.45F, h0 + hs * 0.85F, face - 0.3F, face + 0.15F, -14804455);
         }
      } else {
         k.beam2(0.0F, h0 + hs * 0.8F, face + 0.2F, 0.0F, h0 + hs * 0.7F, face - 1.2F, hs * 0.35F, 0.08F, beak);
      }

      if (sp.has(140737488355328L)) {
         k.box(-0.3F, 0.3F, ht, ht + 1.2F, hz - hs * 0.6F, hz + hs * 0.4F, p.accent());
         k.box(-0.3F, 0.3F, h0 - 0.8F, h0 + 0.4F, face - 0.9F, face - 0.4F, p.accent());
      }

      if (sp.has(281474976710656L)) {
         for (int i = -1; i <= 1; i++) {
            k.beam(i * 0.5F, ht, hz, i * 0.8F, ht + 1.6F, hz, 0.1F, p.main());
            k.cube(i * 0.8F, ht + 1.7F, hz, 0.3F, p.accent());
         }
      }
   }

   static {
      PetSpecies.Ear P = PetSpecies.Ear.POINTED;
      PetSpecies.Ear R = PetSpecies.Ear.ROUND;
      PetSpecies.Ear F = PetSpecies.Ear.FLOPPY;
      PetSpecies.Ear S = PetSpecies.Ear.SIDE;
      PetSpecies.Ear L = PetSpecies.Ear.LONG;
      SPECS.put(
         CosmeticsModule.Pet.FOX,
         quad(15235386, 2826784, 16774376)
            .body(1.9F, 3.0F, 3.4F)
            .legs(3.0F, 0.5F)
            .paint(1, 1)
            .head(2.3F, 1.9F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.BUSHY, 2)
            .f(1048576L)
      );
      SPECS.put(
         CosmeticsModule.Pet.FENNEC_FOX,
         quad(15320207, 13081182, 16774886)
            .body(1.6F, 2.6F, 2.8F)
            .legs(2.6F, 0.45F)
            .head(2.1F, 1.4F)
            .ear(PetSpecies.Ear.BIG, 0)
            .tail(PetSpecies.Tail.BUSHY, 1)
            .f(1048576L)
      );
      SPECS.put(
         CosmeticsModule.Pet.RED_PANDA,
         quad(13127722, 3811878, 16773600)
            .body(2.0F, 2.8F, 3.2F)
            .legs(2.4F, 0.6F)
            .paint(1, 1)
            .head(2.4F, 1.0F)
            .ear(R, 2)
            .tail(PetSpecies.Tail.RINGED, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(262144L)
      );
      SPECS.put(
         CosmeticsModule.Pet.RACCOON,
         quad(9407109, 2762278, 15262942)
            .body(2.1F, 3.0F, 3.3F)
            .legs(2.4F, 0.55F)
            .paint(1, 1)
            .head(2.4F, 1.3F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.RINGED, 0)
            .f(1064960L)
      );
      SPECS.put(
         CosmeticsModule.Pet.PANDA,
         quad(16052974, 2236449, 16052974)
            .body(2.6F, 3.6F, 3.4F)
            .legs(2.6F, 0.9F)
            .paint(1, 1)
            .head(2.9F, 0.8F)
            .ear(R, 1)
            .tail(PetSpecies.Tail.NUB, 0)
            .mark(PetSpecies.Mark.PANDA, 1)
            .f(32768L)
      );
      SPECS.put(
         CosmeticsModule.Pet.POLAR_BEAR,
         quad(15920867, 14472644, 16250092)
            .body(2.6F, 3.6F, 3.8F)
            .legs(3.0F, 0.9F)
            .head(2.5F, 1.4F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.NUB, 0)
            .mark(PetSpecies.Mark.NONE, 1)
      );
      SPECS.put(
         CosmeticsModule.Pet.BEAR_CUB,
         quad(8016436, 5913124, 12159586)
            .body(2.5F, 3.4F, 3.4F)
            .legs(2.8F, 0.85F)
            .head(2.6F, 1.2F)
            .ear(R, 1)
            .tail(PetSpecies.Tail.NUB, 0)
            .mark(PetSpecies.Mark.NONE, 1)
      );
      SPECS.put(
         CosmeticsModule.Pet.PIGLET,
         quad(15905462, 14716565, 16238282)
            .body(2.4F, 3.2F, 3.4F)
            .legs(2.2F, 0.6F)
            .paint(0, 1)
            .head(2.4F, 1.0F)
            .ear(F, 1)
            .tail(PetSpecies.Tail.PIG, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(8388608L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CALF,
         quad(16118506, 3025448, 15780287)
            .body(2.2F, 3.2F, 3.6F)
            .legs(3.8F, 0.6F)
            .paint(0, 3)
            .head(2.3F, 1.4F)
            .ear(S, 0)
            .tail(PetSpecies.Tail.TUFT, 1)
            .mark(PetSpecies.Mark.PATCHES, 1)
            .f(64L)
      );
      SPECS.put(
         CosmeticsModule.Pet.HIGHLAND_COW,
         quad(11887919, 9062944, 14927528)
            .body(2.6F, 3.6F, 3.8F)
            .legs(3.0F, 0.75F)
            .paint(0, 3)
            .head(2.5F, 1.4F)
            .ear(S, 0)
            .tail(PetSpecies.Tail.TUFT, 1)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(3104L)
      );
      SPECS.put(
         CosmeticsModule.Pet.ELEPHANT,
         quad(10396587, 8291468, 13159634)
            .body(3.0F, 4.2F, 4.0F)
            .legs(3.6F, 1.1F)
            .head(2.8F, 0.0F)
            .ear(PetSpecies.Ear.ELEPHANT, 0)
            .tail(PetSpecies.Tail.TUFT, 1)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(768L)
      );
      SPECS.put(
         CosmeticsModule.Pet.LION,
         quad(14262350, 9062942, 15982005).body(2.4F, 3.4F, 3.8F).legs(3.6F, 0.8F).head(2.5F, 1.3F).ear(R, 0).tail(PetSpecies.Tail.TUFT, 1).f(1048577L)
      );
      SPECS.put(
         CosmeticsModule.Pet.TIGER,
         quad(15632938, 2038296, 16774890)
            .body(2.3F, 3.2F, 4.0F)
            .legs(3.4F, 0.75F)
            .head(2.5F, 1.2F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.LONG, 1)
            .mark(PetSpecies.Mark.STRIPES, 1)
            .f(1048576L)
      );
      SPECS.put(
         CosmeticsModule.Pet.LEOPARD,
         quad(14922332, 3811868, 16510936)
            .body(2.1F, 3.0F, 4.0F)
            .legs(3.4F, 0.65F)
            .head(2.3F, 1.1F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.LONG, 0)
            .mark(PetSpecies.Mark.SPOTS, 1)
            .f(1048576L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CHEETAH,
         quad(15254138, 2761248, 16774886)
            .body(1.8F, 2.8F, 4.0F)
            .legs(4.2F, 0.5F)
            .head(2.0F, 1.0F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.LONG, 1)
            .mark(PetSpecies.Mark.SPOTS, 1)
            .f(1572864L)
      );
      SPECS.put(
         CosmeticsModule.Pet.HIPPO,
         quad(10193840, 8154258, 15710408)
            .body(3.2F, 3.8F, 4.0F)
            .legs(1.8F, 1.1F)
            .head(2.9F, 2.2F)
            .ear(PetSpecies.Ear.TINY, 0)
            .tail(PetSpecies.Tail.NUB, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(13510798882111488L)
      );
      SPECS.put(
         CosmeticsModule.Pet.RHINO,
         quad(10130830, 8025454, 11051932)
            .body(2.9F, 3.8F, 4.4F)
            .legs(2.6F, 1.05F)
            .head(2.3F, 2.8F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.TUFT, 1)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(45035996273705088L)
      );
      SPECS.put(
         CosmeticsModule.Pet.SKUNK,
         quad(2039070, 3025708, 16250869)
            .body(1.9F, 2.8F, 3.2F)
            .legs(2.0F, 0.5F)
            .head(2.1F, 1.4F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.SKUNK, 2)
            .mark(PetSpecies.Mark.BACK_STRIPE, 2)
            .f(65536L)
      );
      SPECS.put(
         CosmeticsModule.Pet.HEDGEHOG,
         quad(13214595, 7033412, 15718850)
            .body(2.2F, 2.8F, 3.0F)
            .legs(1.4F, 0.45F)
            .head(1.8F, 1.8F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.NONE, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(4096L)
      );
      SPECS.put(
         CosmeticsModule.Pet.MOUSE,
         quad(11117472, 9077889, 15907010)
            .body(1.6F, 2.2F, 2.4F)
            .legs(1.2F, 0.35F)
            .head(1.7F, 1.2F)
            .ear(PetSpecies.Ear.MOUSE, 0)
            .tail(PetSpecies.Tail.LONG, 2)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(1048576L)
      );
      SPECS.put(
         CosmeticsModule.Pet.GUINEA_PIG,
         quad(14722405, 9067056, 16315112)
            .body(2.2F, 2.6F, 3.0F)
            .legs(0.9F, 0.45F)
            .head(2.2F, 0.8F)
            .ear(R, 1)
            .tail(PetSpecies.Tail.NONE, 0)
            .mark(PetSpecies.Mark.PATCHES, 2)
      );
      SPECS.put(
         CosmeticsModule.Pet.FERRET,
         quad(14207144, 5917244, 16051936)
            .body(1.4F, 2.2F, 4.2F)
            .legs(1.8F, 0.45F)
            .paint(1, 1)
            .head(1.7F, 1.4F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.LONG, 1)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(16384L)
      );
      SPECS.put(
         CosmeticsModule.Pet.OTTER,
         quad(8016952, 6176294, 13215368).body(1.7F, 2.4F, 4.0F).legs(1.6F, 0.5F).head(1.9F, 1.2F).ear(R, 0).tail(PetSpecies.Tail.THICK, 0).f(1048576L)
      );
      SPECS.put(
         CosmeticsModule.Pet.BEAVER,
         quad(9132596, 4078140, 12884592)
            .body(2.5F, 3.4F, 3.0F)
            .legs(1.2F, 0.6F)
            .head(2.1F, 1.0F)
            .ear(PetSpecies.Ear.TINY, 1)
            .tail(PetSpecies.Tail.PADDLE, 1)
            .f(17825792L)
      );
      SPECS.put(
         CosmeticsModule.Pet.SQUIRREL,
         quad(12150063, 9062946, 15916226)
            .body(1.6F, 2.6F, 2.6F)
            .legs(1.8F, 0.45F)
            .head(1.8F, 1.2F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.SQUIRREL, 0)
            .f(68719476736L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CAPYBARA,
         quad(10516046, 7228976, 12029026)
            .body(2.6F, 3.6F, 3.8F)
            .legs(1.8F, 0.75F)
            .head(2.2F, 2.6F)
            .ear(PetSpecies.Ear.TINY, 1)
            .tail(PetSpecies.Tail.NONE, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(27021597764222976L)
      );
      SPECS.put(
         CosmeticsModule.Pet.ARMADILLO,
         quad(12165258, 9336930, 14207156)
            .body(2.2F, 2.6F, 3.2F)
            .legs(1.2F, 0.5F)
            .head(1.4F, 2.2F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .mark(PetSpecies.Mark.BANDS, 1)
      );
      SPECS.put(
         CosmeticsModule.Pet.ANTEATER,
         quad(7233106, 2499104, 15591132)
            .body(2.2F, 3.2F, 3.8F)
            .legs(2.8F, 0.65F)
            .head(1.5F, 4.2F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.BUSHY, 0)
            .mark(PetSpecies.Mark.SADDLE, 1)
      );
      SPECS.put(
         CosmeticsModule.Pet.BOAR,
         quad(6178358, 3022872, 8020048)
            .body(2.4F, 3.6F, 3.6F)
            .legs(2.2F, 0.65F)
            .paint(0, 3)
            .head(2.3F, 2.2F)
            .ear(P, 1)
            .tail(PetSpecies.Tail.TUFT, 1)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(9007233622868224L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CORGI,
         quad(14715452, 12610088, 16774890)
            .body(2.2F, 2.8F, 3.8F)
            .legs(1.6F, 0.6F)
            .paint(0, 2)
            .head(2.4F, 1.6F)
            .ear(PetSpecies.Ear.BIG, 0)
            .tail(PetSpecies.Tail.NUB, 0)
            .f(4325376L)
      );
      SPECS.put(
         CosmeticsModule.Pet.DACHSHUND,
         quad(9194018, 6172180, 11561530)
            .body(1.6F, 2.6F, 4.4F)
            .legs(1.5F, 0.5F)
            .head(1.9F, 2.0F)
            .ear(F, 1)
            .tail(PetSpecies.Tail.LONG, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(2097152L)
      );
      SPECS.put(
         CosmeticsModule.Pet.POODLE,
         quad(16447474, 15591391, 16447474)
            .body(2.0F, 3.0F, 3.0F)
            .legs(3.6F, 0.45F)
            .head(2.0F, 1.6F)
            .ear(F, 0)
            .tail(PetSpecies.Tail.PUFF, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(805307392L)
      );
      SPECS.put(
         CosmeticsModule.Pet.HUSKY,
         quad(7239040, 3948616, 16250871)
            .body(2.2F, 3.4F, 3.6F)
            .legs(3.4F, 0.65F)
            .paint(0, 2)
            .head(2.5F, 1.8F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.CURL, 2)
            .f(4456448L)
      );
      SPECS.put(
         CosmeticsModule.Pet.PUG,
         quad(15124378, 3813420, 15785148)
            .body(2.3F, 3.0F, 3.0F)
            .legs(2.4F, 0.65F)
            .head(2.7F, 0.6F)
            .ear(F, 1)
            .tail(PetSpecies.Tail.PIG, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(68757225472L)
      );
      SPECS.put(
         CosmeticsModule.Pet.DALMATIAN,
         quad(16448248, 1973276, 16448248)
            .body(2.1F, 3.2F, 3.6F)
            .legs(3.8F, 0.6F)
            .head(2.3F, 1.9F)
            .ear(F, 1)
            .tail(PetSpecies.Tail.LONG, 0)
            .mark(PetSpecies.Mark.SPOTS, 1)
            .f(2097152L)
      );
      SPECS.put(
         CosmeticsModule.Pet.SHIBA,
         quad(14916168, 12613676, 16774372)
            .body(2.1F, 3.2F, 3.4F)
            .legs(3.2F, 0.6F)
            .paint(0, 2)
            .head(2.5F, 1.6F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.CURL, 2)
            .f(262144L)
      );
      SPECS.put(
         CosmeticsModule.Pet.WOLF,
         quad(9342869, 5132117, 15263456).body(2.3F, 3.6F, 4.0F).legs(4.0F, 0.7F).head(2.4F, 2.2F).ear(P, 1).tail(PetSpecies.Tail.BUSHY, 1)
      );
      SPECS.put(
         CosmeticsModule.Pet.LYNX,
         quad(13017722, 3812902, 15787218)
            .body(2.1F, 3.2F, 3.4F)
            .legs(3.8F, 0.7F)
            .head(2.3F, 1.0F)
            .ear(P, 0)
            .tail(PetSpecies.Tail.NUB, 1)
            .mark(PetSpecies.Mark.SPOTS, 1)
            .f(202375168L)
      );
      SPECS.put(
         CosmeticsModule.Pet.QUOKKA,
         upright(11569768, 8281668, 15258298)
            .body(1.8F, 3.2F, 1.6F)
            .legs(1.4F, 0.55F)
            .head(2.1F, 0.8F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .f(2267742732288L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CHINCHILLA,
         quad(11053230, 8289926, 15921908)
            .body(2.0F, 2.6F, 2.4F)
            .legs(1.0F, 0.45F)
            .head(2.2F, 0.6F)
            .ear(PetSpecies.Ear.MOUSE, 0)
            .tail(PetSpecies.Tail.SQUIRREL, 0)
            .f(68720525312L)
      );
      SPECS.put(
         CosmeticsModule.Pet.HAMSTER,
         quad(14722656, 11827770, 16511198).body(2.0F, 2.4F, 2.4F).legs(0.8F, 0.45F).head(2.1F, 0.6F).ear(R, 0).tail(PetSpecies.Tail.NONE, 0).f(68720787456L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CHIPMUNK,
         quad(12877886, 4862498, 16180942)
            .body(1.5F, 2.4F, 2.4F)
            .legs(1.4F, 0.4F)
            .head(1.8F, 1.0F)
            .ear(R, 0)
            .tail(PetSpecies.Tail.SQUIRREL, 1)
            .mark(PetSpecies.Mark.BACK_STRIPE, 2)
            .f(68719738880L)
      );
      SPECS.put(
         CosmeticsModule.Pet.BADGER,
         quad(9079432, 2762792, 16053488)
            .body(2.4F, 2.8F, 3.2F)
            .legs(1.4F, 0.6F)
            .paint(1, 1)
            .head(2.1F, 1.4F)
            .ear(R, 1)
            .tail(PetSpecies.Tail.SHORT, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(98304L)
      );
      SPECS.put(
         CosmeticsModule.Pet.TANUKI,
         quad(10123866, 3812902, 15128256)
            .body(2.2F, 3.0F, 3.0F)
            .legs(1.8F, 0.55F)
            .paint(1, 1)
            .head(2.3F, 1.2F)
            .ear(R, 1)
            .tail(PetSpecies.Tail.BUSHY, 1)
            .f(278528L)
      );
      SPECS.put(
         CosmeticsModule.Pet.PLATYPUS,
         quad(8016952, 3815998, 13215368)
            .body(2.2F, 2.2F, 3.4F)
            .legs(0.9F, 0.5F)
            .head(1.9F, 0.0F)
            .ear(PetSpecies.Ear.NONE, 0)
            .tail(PetSpecies.Tail.PADDLE, 0)
            .mark(PetSpecies.Mark.NONE, 1)
            .f(72057662757404672L)
      );
      SPECS.put(
         CosmeticsModule.Pet.KIWI,
         bird(9071178, 6966838, 11044970).body(2.4F, 3.0F, 2.6F).legs(1.4F, 0.3F).paint(6, 6).head(1.5F, 3.0F).tail(PetSpecies.Tail.NONE, 0).beak(6)
      );
      SPECS.put(
         CosmeticsModule.Pet.BUDGIE,
         bird(8048714, 1973794, 15917642)
            .body(1.5F, 3.0F, 1.6F)
            .legs(0.8F, 0.2F)
            .paint(5, 5)
            .head(1.6F, 0.0F)
            .tail(PetSpecies.Tail.LONG, 1)
            .beak(7)
            .f(562949953683456L)
      );
      SPECS.put(
         CosmeticsModule.Pet.AXOLOTL,
         quad(16230600, 14704782, 16636134)
            .body(1.8F, 2.0F, 3.4F)
            .legs(1.0F, 0.4F)
            .head(2.4F, 0.4F)
            .ear(PetSpecies.Ear.NONE, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .mark(PetSpecies.Mark.BELLY, 2)
            .f(206158430208L)
      );
      SPECS.put(
         CosmeticsModule.Pet.GECKO,
         quad(10144586, 6195754, 15265976)
            .body(1.6F, 1.8F, 3.2F)
            .legs(1.0F, 0.4F)
            .head(1.8F, 1.2F)
            .ear(PetSpecies.Ear.NONE, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .mark(PetSpecies.Mark.SPOTS, 1)
            .f(68719476736L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CROCODILE,
         quad(5143098, 3824170, 13224080)
            .body(2.4F, 2.2F, 4.6F)
            .legs(1.2F, 0.6F)
            .head(1.9F, 4.2F)
            .ear(PetSpecies.Ear.NONE, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .f(51539607552L)
      );
      SPECS.put(
         CosmeticsModule.Pet.TRICERATOPS,
         quad(7311280, 4876942, 15261896)
            .body(2.8F, 3.8F, 4.2F)
            .legs(2.8F, 1.0F)
            .head(2.4F, 1.8F)
            .ear(PetSpecies.Ear.NONE, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .f(3221225472L)
      );
      SPECS.put(
         CosmeticsModule.Pet.STEGOSAURUS,
         quad(8036442, 14182458, 15261888)
            .body(2.6F, 3.8F, 4.4F)
            .legs(3.0F, 0.9F)
            .head(1.6F, 1.6F)
            .ear(PetSpecies.Ear.NONE, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .f(279172874240L)
      );
      SPECS.put(
         CosmeticsModule.Pet.BABY_DRAGON,
         quad(8015824, 5123738, 15913056).body(2.2F, 3.2F, 3.4F).legs(2.6F, 0.7F).head(2.4F, 1.6F).ear(P, 1).tail(PetSpecies.Tail.THICK, 0).f(111669149760L)
      );
      SPECS.put(
         CosmeticsModule.Pet.PENGUIN,
         upright(2106408, 2764597, 16119282)
            .body(2.6F, 5.2F, 2.2F)
            .legs(0.8F, 0.6F)
            .paint(4, 4)
            .head(2.2F, 0.0F)
            .ear(PetSpecies.Ear.NONE, 0)
            .f(27487790694400L)
      );
      SPECS.put(
         CosmeticsModule.Pet.OWL,
         upright(10123866, 6967352, 15259838)
            .body(2.6F, 4.4F, 2.4F)
            .legs(0.8F, 0.5F)
            .paint(7, 7)
            .head(2.6F, 0.0F)
            .ear(PetSpecies.Ear.NONE, 0)
            .beak(7)
            .f(27556577280000L)
      );
      SPECS.put(
         CosmeticsModule.Pet.MONKEY,
         upright(8016180, 5913122, 15255976)
            .body(2.0F, 3.8F, 1.8F)
            .legs(2.0F, 0.55F)
            .head(2.3F, 0.0F)
            .ear(PetSpecies.Ear.MOUSE, 2)
            .tail(PetSpecies.Tail.CURL, 0)
            .f(10995116277760L)
      );
      SPECS.put(
         CosmeticsModule.Pet.MEERKAT,
         upright(13215864, 5916210, 15127732)
            .body(1.3F, 5.0F, 1.2F)
            .legs(1.6F, 0.4F)
            .head(1.5F, 1.2F)
            .ear(R, 1)
            .tail(PetSpecies.Tail.LONG, 1)
            .f(2199023288320L)
      );
      SPECS.put(
         CosmeticsModule.Pet.KOALA,
         upright(10264484, 3814964, 15592938).body(2.4F, 4.2F, 2.0F).legs(1.4F, 0.7F).head(2.7F, 0.0F).ear(PetSpecies.Ear.FLUFFY, 0).f(2748779069440L)
      );
      SPECS.put(
         CosmeticsModule.Pet.KANGAROO,
         upright(12157263, 9067056, 15125414)
            .body(2.0F, 5.0F, 1.8F)
            .legs(2.4F, 0.65F)
            .head(1.8F, 1.6F)
            .ear(L, 0)
            .tail(PetSpecies.Tail.THICK, 0)
            .f(1132496976609280L)
      );
      SPECS.put(
         CosmeticsModule.Pet.SLOTH,
         upright(10521716, 5916728, 14734012).body(2.2F, 4.2F, 1.8F).legs(1.6F, 0.6F).head(2.2F, 0.0F).ear(PetSpecies.Ear.NONE, 0).f(2297979302068224L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CHICK,
         bird(16769126, 15911244, 16772761).body(1.8F, 2.6F, 1.9F).legs(0.9F, 0.2F).paint(4, 4).head(1.6F, 0.0F).tail(PetSpecies.Tail.NONE, 0).beak(4)
      );
      SPECS.put(
         CosmeticsModule.Pet.DUCKLING,
         bird(16769902, 15911244, 16773544).body(2.0F, 2.6F, 2.4F).legs(0.6F, 0.2F).paint(4, 4).head(1.7F, 1.0F).tail(PetSpecies.Tail.SHORT, 0).beak(4)
      );
      SPECS.put(
         CosmeticsModule.Pet.PARROT,
         bird(3129178, 2781142, 15217706)
            .body(1.8F, 3.8F, 2.0F)
            .legs(0.8F, 0.25F)
            .paint(5, 5)
            .head(1.8F, 2.0F)
            .tail(PetSpecies.Tail.LONG, 2)
            .beak(9)
            .f(262144L)
      );
      SPECS.put(
         CosmeticsModule.Pet.PEACOCK,
         bird(2056137, 2001514, 2899594)
            .body(1.8F, 3.0F, 2.6F)
            .legs(2.6F, 0.22F)
            .paint(5, 5)
            .head(1.3F, 0.0F)
            .neck(2.4F)
            .tail(PetSpecies.Tail.FAN, 0)
            .beak(5)
            .f(281474976710656L)
      );
      SPECS.put(
         CosmeticsModule.Pet.TOUCAN,
         bird(1973794, 16316144, 16751146)
            .body(1.8F, 3.6F, 2.0F)
            .legs(0.8F, 0.25F)
            .paint(5, 5)
            .head(1.8F, 4.0F)
            .tail(PetSpecies.Tail.SHORT, 0)
            .beak(4)
            .f(70368744177664L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CROW,
         bird(1974310, 3028032, 3817552).body(1.8F, 2.8F, 2.6F).legs(1.8F, 0.22F).paint(9, 9).head(1.6F, 0.0F).tail(PetSpecies.Tail.LONG, 0).beak(9)
      );
      SPECS.put(
         CosmeticsModule.Pet.EAGLE,
         bird(5913124, 4073496, 16316144)
            .body(2.0F, 3.4F, 2.6F)
            .legs(1.2F, 0.3F)
            .paint(7, 7)
            .head(1.9F, 2.0F)
            .tail(PetSpecies.Tail.SHORT, 2)
            .beak(7)
            .f(562949953421312L)
      );
      SPECS.put(
         CosmeticsModule.Pet.SWAN,
         bird(16645627, 15263972, 16645627)
            .body(2.4F, 2.6F, 3.4F)
            .legs(0.5F, 0.3F)
            .paint(9, 9)
            .head(1.3F, 1.0F)
            .neck(4.6F)
            .tail(PetSpecies.Tail.SHORT, 0)
            .beak(4)
      );
      SPECS.put(
         CosmeticsModule.Pet.ROBIN,
         bird(8022620, 5918276, 15229994)
            .body(1.7F, 2.6F, 2.2F)
            .legs(1.4F, 0.2F)
            .paint(9, 9)
            .head(1.6F, 0.0F)
            .tail(PetSpecies.Tail.SHORT, 0)
            .beak(9)
            .f(70368744177664L)
      );
      SPECS.put(
         CosmeticsModule.Pet.CHICKEN,
         bird(16777215, 15592941, 15217706)
            .body(2.2F, 3.2F, 2.8F)
            .legs(2.0F, 0.25F)
            .paint(7, 7)
            .head(1.6F, 0.0F)
            .tail(PetSpecies.Tail.CHICKEN, 0)
            .beak(7)
            .f(140737488355328L)
      );
      SPECS.put(
         CosmeticsModule.Pet.PUFFIN,
         bird(1973794, 16184560, 15887146)
            .body(1.8F, 3.4F, 1.9F)
            .legs(0.8F, 0.25F)
            .paint(4, 4)
            .head(1.8F, 3.0F)
            .tail(PetSpecies.Tail.SHORT, 0)
            .beak(4)
            .f(79164837199872L)
      );
   }

   static enum Ear {
      NONE,
      POINTED,
      BIG,
      ROUND,
      MOUSE,
      FLOPPY,
      SIDE,
      LONG,
      FLUFFY,
      ELEPHANT,
      TINY;
   }

   static enum Mark {
      NONE,
      BELLY,
      STRIPES,
      SPOTS,
      SADDLE,
      BACK_STRIPE,
      PANDA,
      BANDS,
      PATCHES;
   }

   static enum Plan {
      QUAD,
      UPRIGHT,
      BIRD;
   }

   static final class Spec {
      final PetSpecies.Plan plan;
      final int[] natural;
      float bw = 2.0F;
      float bh = 3.2F;
      float bl = 3.2F;
      float legH = 3.0F;
      float legW = 0.6F;
      float hs = 2.3F;
      float sn = 1.2F;
      float neck;
      PetSpecies.Ear ear = PetSpecies.Ear.POINTED;
      PetSpecies.Tail tail = PetSpecies.Tail.SHORT;
      PetSpecies.Mark mark = PetSpecies.Mark.BELLY;
      long flags;
      int earColor;
      int legColor;
      int footColor = -1;
      int markColor = 1;
      int tailTip;
      int beakColor = 4;

      Spec(PetSpecies.Plan plan, int main, int second, int accent) {
         this.plan = plan;
         this.natural = new int[]{0xFF000000 | main, 0xFF000000 | second, 0xFF000000 | accent};
      }

      PetSpecies.Spec body(float bw, float bh, float bl) {
         this.bw = bw;
         this.bh = bh;
         this.bl = bl;
         return this;
      }

      PetSpecies.Spec legs(float h, float w) {
         this.legH = h * 0.85F;
         this.legW = w;
         return this;
      }

      PetSpecies.Spec head(float hs, float sn) {
         this.hs = hs * 1.15F;
         this.sn = sn;
         return this;
      }

      PetSpecies.Spec neck(float neck) {
         this.neck = neck;
         return this;
      }

      PetSpecies.Spec ear(PetSpecies.Ear ear, int color) {
         this.ear = ear;
         this.earColor = color;
         return this;
      }

      PetSpecies.Spec tail(PetSpecies.Tail tail, int tip) {
         this.tail = tail;
         this.tailTip = tip;
         return this;
      }

      PetSpecies.Spec mark(PetSpecies.Mark mark, int color) {
         this.mark = mark;
         this.markColor = color;
         return this;
      }

      PetSpecies.Spec paint(int leg, int foot) {
         this.legColor = leg;
         this.footColor = foot;
         return this;
      }

      PetSpecies.Spec beak(int color) {
         this.beakColor = color;
         return this;
      }

      PetSpecies.Spec f(long flags) {
         this.flags |= flags;
         return this;
      }

      boolean has(long flag) {
         return (this.flags & flag) != 0L;
      }
   }

   static enum Tail {
      NONE,
      NUB,
      SHORT,
      LONG,
      CURL,
      BUSHY,
      RINGED,
      PADDLE,
      TUFT,
      PIG,
      PUFF,
      SKUNK,
      HORSE,
      THICK,
      SQUIRREL,
      FAN,
      CHICKEN;
   }
}
