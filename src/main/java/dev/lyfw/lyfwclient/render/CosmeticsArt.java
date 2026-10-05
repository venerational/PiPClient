package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.platform.NativeImage;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.function.IntBinaryOperator;

public final class CosmeticsArt {
   private static final double[][] DRAGON_TIPS = new double[][]{{54.0, 1.0}, {53.0, 19.0}, {42.0, 33.0}, {27.0, 38.0}};
   private static final double[][] BAT_TIPS = new double[][]{{50.0, 3.0}, {55.0, 16.0}, {47.0, 29.0}, {34.0, 37.0}, {19.0, 38.0}};

   private CosmeticsArt() {
   }

   public static void paintWing(NativeImage image, CosmeticsModule.WingType type, int base, int tip) {
      clear(image);
      switch (type) {
         case DRAGON:
            paintMembrane(image, base, tip, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.5, 0.34, true, null);
            break;
         case BAT:
            paintMembrane(image, base, tip, BAT_TIPS, 19.0, 4.0, 24.0, 1.1, 0.55, false, null);
            break;
         case BUTTERFLY:
            paintButterfly(image, base, tip);
            break;
         case FAIRY:
            paintFairy(image, base, tip);
            break;
         case PHOENIX:
            paintPhoenix(image, base, tip);
            break;
         case DEMON:
            paintDemon(image, base, tip);
            break;
         case SKELETON:
            paintSkeleton(image, base, tip);
            break;
         case MECH:
            paintMech(image, base, tip);
            break;
         case CRYSTAL:
            paintCrystal(image, base, tip);
            break;
         case MOTH:
            paintMoth(image, base, tip);
            break;
         case BEE:
            paintBee(image, base, tip);
            break;
         case HAWK:
            paintHawk(image, base, tip);
            break;
         case ENDER:
            paintEnder(image, base, tip);
            break;
         case LEAF:
            paintLeaf(image, base, tip);
            break;
         case FROST:
            paintFrost(image, base, tip);
            break;
         case DRAGONFLY:
            paintDragonfly(image, base, tip);
            break;
         case LUNA_MOTH:
            paintLunaMoth(image, base, tip);
            break;
         case RAINBOW:
            paintRainbow(image, base, tip);
            break;
         case CLOCKWORK:
            paintClockwork(image, base, tip);
            break;
         case LIGHTNING:
            paintLightning(image, base, tip);
            break;
         case SHADOW:
            paintShadow(image, base, tip);
            break;
         case PEACOCK:
            paintPeacock(image, base, tip);
            break;
         case PETAL:
            paintPetal(image, base, tip);
            break;
         case HOLOGRAM:
            paintHologram(image, base, tip);
            break;
         case STAINED_GLASS:
            paintStainedGlass(image, base, tip);
            break;
         case OWL:
            paintOwl(image, base, tip);
            break;
         case PARROT:
            paintParrot(image, base, tip);
            break;
         case CROW:
            paintCrow(image, base, tip);
            break;
         case PTERODACTYL:
            paintPterodactyl(image, base, tip);
            break;
         case GARGOYLE:
            paintGargoyle(image, base, tip);
            break;
         case SWALLOWTAIL:
            paintSwallowtail(image, base, tip);
            break;
         case LADYBUG:
            paintLadybug(image, base, tip);
            break;
         case INFERNO:
            paintInferno(image, base, tip);
            break;
         case AQUA:
            paintAqua(image, base, tip);
            break;
         case GALAXY:
            paintGalaxy(image, base, tip);
            break;
         case ORIGAMI:
            paintOrigami(image, base, tip);
            break;
         case PIXEL:
            paintPixel(image, base, tip);
            break;
         case CIRCUIT:
            paintCircuit(image, base, tip);
            break;
         case MAGMA:
            paintMagma(image, base, tip);
            break;
         case CANDY:
            paintCandy(image, base, tip);
            break;
         case SNOWFLAKE:
            paintSnowflake(image, base, tip);
            break;
         case VINE:
            paintVine(image, base, tip);
            break;
         case TATTERED:
            paintTattered(image, base, tip);
            break;
         case NEON:
            paintNeon(image, base, tip);
            break;
         case BUBBLE:
            paintBubble(image, base, tip);
            break;
         default:
            int[] more = MoreWings.render(type, base, tip);
            if (more == null) {
               paintAngel(image, base, tip);
            } else {
               for (int y = 0; y < Math.min(image.getHeight(), 40); y++) {
                  for (int x = 0; x < Math.min(image.getWidth(), 56); x++) {
                     image.setPixel(x, y, more[y * 56 + x]);
                  }
               }
            }
      }
   }

   private static void clear(NativeImage image) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            image.setPixel(x, y, 0);
         }
      }
   }

   private static void paintAngel(NativeImage image, int base, int tip) {
      for (int i = 0; i < 6; i++) {
         double t = 0.45 + 0.47 * i / 5.0;
         double length = 30.0 - 14.0 * Math.abs(t - 0.72) / 0.27;
         double angle = Math.toRadians(4.0 + 16.0 * (t - 0.45) / 0.47);
         feather(image, boneX(t), boneY(t), angle, length, 3.3, lerp(base, tip, 0.45 + 0.55 * (t - 0.45) / 0.47), true);
      }

      for (int i = 0; i < 7; i++) {
         double t = 0.02 + 0.48 * i / 6.0;
         double length = 13.0 + 10.0 * (t / 0.5);
         double angle = Math.toRadians(-8.0 + 12.0 * t / 0.5);
         feather(image, boneX(t), boneY(t) + 1.0, angle, length, 3.0, lerp(base, tip, 0.2 * t / 0.5), true);
      }

      for (int i = 0; i < 9; i++) {
         double t = i / 8.0;
         double length = 6.5 + 3.5 * Math.sin(Math.PI * t);
         feather(image, boneX(t), boneY(t) + 0.5, Math.toRadians(12.0), length, 2.6, shade(base, 1.06F), false);
      }

      int bone = shade(base, 1.14F);
      int boneRim = shade(base, 0.78F);

      for (double t = 0.0; t <= 1.0; t += 0.004) {
         disc(image, boneX(t), boneY(t), 2.0 - 0.9 * t, bone, boneRim);
      }
   }

   private static double boneX(double t) {
      return 2.0 + 50.0 * t;
   }

   private static double boneY(double t) {
      return 10.0 - 8.0 * Math.pow(t, 0.9);
   }

   private static void paintMembrane(
      NativeImage image,
      int base,
      int tip,
      double[][] tips,
      double wristX,
      double wristY,
      double bodyY,
      double boneRadius,
      double bite,
      boolean spikes,
      double[][] holes
   ) {
      double rootX = 1.5;
      double rootY = 10.0;
      double[][] outline = new double[tips.length + 3][];
      outline[0] = new double[]{rootX, rootY};
      outline[1] = new double[]{wristX, wristY};

      for (int i = 0; i < tips.length; i++) {
         outline[2 + i] = tips[i];
      }

      outline[outline.length - 1] = new double[]{2.5, bodyY};
      int skin = shade(base, 0.9F);
      int skinEdge = lerp(skin, tip, 0.35);
      fillPolygon(image, outline, (x, y) -> lerp(skin, skinEdge, Math.hypot(x - wristX, y - wristY) / 45.0));
      double[][] trailing = new double[tips.length + 1][];

      for (int i = 0; i < tips.length; i++) {
         trailing[i] = tips[i];
      }

      trailing[tips.length] = outline[outline.length - 1];

      for (int i = 0; i < trailing.length - 1; i++) {
         carveBetween(image, trailing[i], trailing[i + 1], wristX, wristY, bite);
      }

      if (holes != null) {
         for (double[] hole : holes) {
            clearDisc(image, hole[0], hole[1], hole[2]);
         }
      }

      rimEdges(image, 0.6F);
      int veinColor = shade(skin, 0.82F);

      for (int i = 0; i < tips.length - 1; i++) {
         double mx = (tips[i][0] + tips[i + 1][0]) / 2.0;
         double my = (tips[i][1] + tips[i + 1][1]) / 2.0;
         line(image, wristX, wristY, wristX + (mx - wristX) * 0.7, wristY + (my - wristY) * 0.7, 0.35, veinColor, 0, false);
      }

      int bone = shade(lerp(base, tip, 0.75), 1.05F);
      int boneRim = shade(bone, 0.58F);
      line(image, rootX, rootY, wristX, wristY, boneRadius * 1.4, bone, boneRim, true);

      for (int i = 0; i < tips.length; i++) {
         line(image, wristX, wristY, tips[i][0], tips[i][1], boneRadius * (1.0 - 0.25 * i / tips.length), bone, boneRim, true);
         if (spikes) {
            double dx = tips[i][0] - wristX;
            double dy = tips[i][1] - wristY;
            double len = Math.hypot(dx, dy);
            double ux = dx / len;
            double uy = dy / len;
            fillPolygon(
               image,
               new double[][]{
                  {tips[i][0] - uy * 1.3, tips[i][1] + ux * 1.3},
                  {tips[i][0] + ux * 3.5, tips[i][1] + uy * 3.5},
                  {tips[i][0] + uy * 1.3, tips[i][1] - ux * 1.3}
               },
               (x, y) -> bone
            );
         }
      }

      fillPolygon(image, new double[][]{{wristX - 1.2, wristY + 0.5}, {wristX - 0.6, wristY - 3.5}, {wristX + 1.4, wristY}}, (x, y) -> boneRim);
   }

   private static void paintButterfly(NativeImage image, int base, int tip) {
      int border = shade(tip, 0.35F);
      int spot = shade(base, 1.45F);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double cx = x + 0.5;
            double cy = y + 0.5;
            double upper = lobe(cx, cy, 27.0, 12.0, 25.0, 11.0, -12.0);
            double lower = lobe(cx, cy, 18.0, 27.0, 15.0, 10.0, 25.0);
            double t = Math.min(upper, lower);
            if (t <= 1.0) {
               int color = t > 0.82 ? border : lerp(base, tip, Math.pow(t, 1.5));
               image.setPixel(x, y, color);
            }
         }
      }

      for (int i = 0; i < 9; i++) {
         double angle = Math.toRadians(-100.0 + 25.0 * i);
         spotOnLobe(image, 27.0, 12.0, 25.0, 11.0, -12.0, angle, 0.9, 1.2, spot);
      }

      for (int i = 0; i < 5; i++) {
         double angle = Math.toRadians(20.0 + 35.0 * i);
         spotOnLobe(image, 18.0, 27.0, 15.0, 10.0, 25.0, angle, 0.9, 1.0, spot);
      }

      disc(image, 35.0, 10.0, 4.0, tip, border);
      disc(image, 35.0, 10.0, 1.8, border, border);
      disc(image, 20.0, 29.0, 2.8, tip, border);
      disc(image, 20.0, 29.0, 1.2, border, border);

      for (int y = 8; y < 26; y++) {
         for (int xx = 0; xx < 5; xx++) {
            if (lobe(xx + 0.5, y + 0.5, 2.5, 17.0, 3.5, 8.5, 0.0) <= 1.0) {
               image.setPixel(xx, y, border);
            }
         }
      }
   }

   private static void paintFairy(NativeImage image, int base, int tip) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double cx = x + 0.5;
            double cy = y + 0.5;
            int color = fairyTexel(cx, cy, 27.0, 8.0, 26.0, 6.5, -10.0, base, tip);
            if (color == 0) {
               color = fairyTexel(cx, cy, 19.0, 22.0, 18.0, 5.0, 25.0, base, tip);
            }

            if (color != 0) {
               image.setPixel(x, y, color);
            }
         }
      }
   }

   private static int fairyTexel(double x, double y, double cx, double cy, double rx, double ry, double degrees, int base, int tip) {
      double rad = Math.toRadians(degrees);
      double dx = x - cx;
      double dy = y - cy;
      double ux = dx * Math.cos(rad) + dy * Math.sin(rad);
      double uy = -dx * Math.sin(rad) + dy * Math.cos(rad);
      double t = Math.sqrt(ux / rx * (ux / rx) + uy / ry * (uy / ry));
      if (t > 1.0) {
         return 0;
      } else if (t > 0.86) {
         return -436207616 | shade(lerp(base, tip, 0.6), 1.2F) & 16777215;
      } else {
         boolean vein = Math.abs(uy) < 0.45 || ux > rx * -0.2 && Math.abs(uy - 0.32 * (ux + rx * 0.2)) < 0.4 || ux > 0.0 && Math.abs(uy + 0.28 * ux) < 0.4;
         return vein ? -771751936 | shade(lerp(base, tip, 0.5), 1.15F) & 16777215 : -1946157056 | lerp(base, tip, t) & 16777215;
      }
   }

   private static void paintPhoenix(NativeImage image, int base, int tip) {
      int hot = shade(lerp(base, tip, 0.15), 1.3F);

      for (int i = 0; i < 9; i++) {
         double t = 0.35 + 0.62 * i / 8.0;
         double f = (t - 0.35) / 0.62;
         double length = 26.0 - 10.0 * f + 6.0 * Math.sin(Math.PI * f);
         flame(image, boneX(t), boneY(t), Math.toRadians(8.0 + 22.0 * f), length, 2.7, lerp(base, tip, 0.3 + 0.7 * f), hot);
      }

      for (int i = 0; i < 6; i++) {
         double t = 0.03 + 0.34 * i / 5.0;
         double f = t / 0.37;
         flame(image, boneX(t), boneY(t) + 1.0, Math.toRadians(-6.0 + 14.0 * f), 12.0 + 11.0 * f, 2.5, lerp(base, tip, 0.2 * f), hot);
      }

      for (int i = 0; i < 9; i++) {
         double t = i / 8.0;
         feather(image, boneX(t), boneY(t) + 0.5, Math.toRadians(14.0), 5.5 + 3.0 * Math.sin(Math.PI * t), 2.4, shade(base, 1.1F), false);
      }

      int rim = shade(base, 0.72F);

      for (double t = 0.0; t <= 1.0; t += 0.004) {
         disc(image, boneX(t), boneY(t), 1.9 - 0.9 * t, hot, rim);
      }
   }

   private static void flame(NativeImage image, double ax, double ay, double angle, double length, double halfWidth, int color, int core) {
      double dx = Math.sin(angle);
      double dy = Math.cos(angle);
      double ex = ax + dx * length;
      double ey = ay + dy * length;
      int rim = shade(color, 0.62F);
      int minX = Math.max(0, (int)Math.floor(Math.min(ax, ex) - halfWidth - 2.0));
      int maxX = Math.min(image.getWidth() - 1, (int)Math.ceil(Math.max(ax, ex) + halfWidth + 2.0));
      int minY = Math.max(0, (int)Math.floor(Math.min(ay, ey) - halfWidth - 2.0));
      int maxY = Math.min(image.getHeight() - 1, (int)Math.ceil(Math.max(ay, ey) + halfWidth + 2.0));

      for (int py = minY; py <= maxY; py++) {
         for (int px = minX; px <= maxX; px++) {
            double cx = px + 0.5;
            double cy = py + 0.5;
            double along = Math.max(0.0, Math.min(1.0, ((cx - ax) * dx + (cy - ay) * dy) / length));
            double dist = Math.hypot(cx - (ax + dx * along * length), cy - (ay + dy * along * length));
            double radius = halfWidth * (1.0 - 0.7 * Math.pow(along, 1.4)) * (1.0 + 0.2 * Math.sin(along * 15.0));
            if (dist <= radius) {
               image.setPixel(px, py, dist < radius * 0.38 && along < 0.8 ? core : color);
            } else if (dist <= radius + 0.9) {
               image.setPixel(px, py, rim);
            }
         }
      }
   }

   private static void paintDemon(NativeImage image, int base, int tip) {
      double[][] tips = new double[][]{{55.0, 3.0}, {52.0, 17.0}, {45.0, 29.0}, {34.0, 37.0}, {22.0, 39.0}};
      double[][] holes = new double[][]{{31.0, 15.0, 1.7}, {39.0, 24.0, 1.3}, {25.0, 26.0, 1.5}, {44.0, 12.0, 1.1}};
      paintMembrane(image, base, tip, tips, 20.0, 5.0, 27.0, 1.3, 0.7, true, holes);
      int horn = shade(lerp(base, tip, 0.75), 0.6F);
      fillPolygon(image, new double[][]{{18.2, 6.5}, {14.8, 0.2}, {21.8, 5.0}}, (x, y) -> horn);

      for (double t : new double[]{0.3, 0.62}) {
         double bx = 1.5 + 18.5 * t;
         double by = 10.0 + -5.0 * t;
         fillPolygon(image, new double[][]{{bx - 1.2, by + 0.4}, {bx - 0.2, by - 3.0}, {bx + 1.2, by - 0.2}}, (x, y) -> horn);
      }
   }

   private static void paintSkeleton(NativeImage image, int base, int tip) {
      double rootX = 1.5;
      double rootY = 10.0;
      double wristX = 21.0;
      double wristY = 3.5;
      int bone = shade(lerp(base, tip, 0.15), 1.08F);
      int rim = shade(bone, 0.5F);
      int joint = shade(bone, 1.15F);
      line(image, rootX, rootY, wristX, wristY, 1.9, bone, rim, true);
      double[][] knuckles = new double[DRAGON_TIPS.length][];

      for (int i = 0; i < DRAGON_TIPS.length; i++) {
         double[] end = DRAGON_TIPS[i];
         double kx = wristX + (end[0] - wristX) * 0.45;
         double ky = wristY + (end[1] - wristY) * 0.45;
         double r = 1.35 - 0.2 * i;
         knuckles[i] = new double[]{kx, ky, r};
         line(image, wristX, wristY, kx, ky, r, bone, rim, true);
         line(image, kx, ky, end[0], end[1], r * 0.8, bone, rim, true);
         double len = Math.hypot(end[0] - kx, end[1] - ky);
         double ux = (end[0] - kx) / len;
         double uy = (end[1] - ky) / len;
         fillPolygon(image, new double[][]{{end[0] - uy, end[1] + ux}, {end[0] + ux * 3.0, end[1] + uy * 3.0}, {end[0] + uy, end[1] - ux}}, (x, y) -> rim);
      }

      for (double[] knuckle : knuckles) {
         disc(image, knuckle[0], knuckle[1], knuckle[2] + 0.7, joint, rim);
      }

      disc(image, wristX, wristY, 2.4, joint, rim);
      disc(image, rootX + 1.0, rootY, 2.2, joint, rim);
   }

   private static void paintMech(NativeImage image, int base, int tip) {
      int plates = 7;
      int glow = shade(tip, 1.3F);

      for (int i = 0; i < plates; i++) {
         double t0 = (double)i / plates + 0.01;
         double t1 = (double)(i + 1) / plates - 0.01;
         double f = (i + 0.5) / plates;
         double length = 12.0 + 20.0 * Math.sin(Math.PI * (0.2 + 0.7 * f));
         double sweep = 2.0 + 4.0 * f;
         double x0 = boneX(t0);
         double y0 = boneY(t0) + 1.0;
         double x1 = boneX(t1);
         double y1 = boneY(t1) + 1.0;
         int plate = shade(lerp(base, tip, 0.25 * f), i % 2 == 0 ? 1.0F : 0.88F);
         int light = shade(plate, 1.22F);
         fillPolygon(
            image, new double[][]{{x0, y0}, {x1, y1}, {x1 + sweep, y1 + length}, {x0 + sweep + 0.8, y0 + length - 1.5}}, (x, y) -> y - y0 < 2.0 ? light : plate
         );
         line(image, x0 + sweep * 0.5, y0 + length * 0.5, x1 + sweep * 0.5, y1 + length * 0.5, 0.35, shade(plate, 0.7F), 0, false);
         line(image, x0 + sweep + 0.8, y0 + length - 2.0, x1 + sweep, y1 + length - 0.5, 0.5, glow, 0, false);
         disc(image, x0 + 1.4, y0 + 2.4, 0.6, shade(plate, 1.4F), shade(plate, 1.4F));
      }

      rimEdges(image, 0.55F);
      int arm = shade(base, 0.62F);
      int armRim = shade(arm, 0.6F);

      for (double t = 0.0; t <= 1.0; t += 0.004) {
         disc(image, boneX(t), boneY(t), 2.1 - 0.8 * t, arm, armRim);
      }

      disc(image, boneX(0.36), boneY(0.36), 1.5, glow, armRim);
   }

   private static void paintCrystal(NativeImage image, int base, int tip) {
      for (int i = 0; i < 9; i++) {
         double t = 0.04 + 0.92 * i / 8.0;
         double length = 11.0 + 20.0 * Math.sin(Math.PI * (0.18 + 0.72 * t));
         shard(image, boneX(t), boneY(t) + 0.5, Math.toRadians(-4.0 + 32.0 * t), length, 2.6 + 1.2 * Math.sin(Math.PI * t), lerp(base, tip, t));
      }

      for (double t = 0.0; t <= 1.0; t += 0.004) {
         int rod = -301989888 | shade(lerp(base, tip, t), 1.3F) & 16777215;
         disc(image, boneX(t), boneY(t), 1.3 - 0.5 * t, rod, rod);
      }
   }

   private static void shard(NativeImage image, double ax, double ay, double angle, double length, double width, int color) {
      double dx = Math.sin(angle);
      double dy = Math.cos(angle);
      double py = -dx;
      double mx = ax + dx * length * 0.32;
      double my = ay + dy * length * 0.32;
      int light = -771751936 | shade(color, 1.35F) & 16777215;
      int dark = -1275068416 | shade(color, 0.8F) & 16777215;
      int edge = -268435456 | shade(color, 1.6F) & 16777215;
      fillPolygon(
         image,
         new double[][]{{ax, ay}, {mx + dy * width, my + py * width}, {ax + dx * length, ay + dy * length}, {mx - dy * width, my - py * width}},
         (x, y) -> {
            double side = (x + 0.5 - ax) * dy + (y + 0.5 - ay) * py;
            return Math.abs(side) < 0.45 ? edge : (side > 0.0 ? light : dark);
         }
      );
   }

   private static void paintMoth(NativeImage image, int base, int tip) {
      int band = shade(base, 0.72F);
      int fuzz = shade(lerp(base, tip, 0.7), 0.85F);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double cx = x + 0.5;
            double cy = y + 0.5;
            double t = Math.min(lobe(cx, cy, 26.0, 13.0, 25.0, 12.5, -8.0), lobe(cx, cy, 17.0, 28.0, 15.5, 10.5, 22.0));
            int noise = (x * 73856093 ^ y * 19349663) & 0xFF;
            if (!(t > 1.0) && (!(t > 0.88) || noise % 3 != 0)) {
               int color;
               if (t > 0.88) {
                  color = fuzz;
               } else if (!(Math.abs(t - 0.62) < 0.05) && !(Math.abs(t - 0.38) < 0.035)) {
                  color = lerp(base, tip, t * t * 0.7);
               } else {
                  color = band;
               }

               image.setPixel(x, y, shade(color, 0.94F + noise / 255.0F * 0.12F));
            }
         }
      }

      disc(image, 32.0, 12.0, 3.4, shade(tip, 0.55F), shade(tip, 0.4F));
      disc(image, 32.0, 12.0, 1.5, shade(base, 1.35F), shade(base, 1.35F));
      disc(image, 19.0, 28.0, 2.0, shade(tip, 0.55F), shade(tip, 0.4F));
      int body = shade(base, 0.55F);

      for (int y = 8; y < 26; y++) {
         for (int xx = 0; xx < 5; xx++) {
            if (lobe(xx + 0.5, y + 0.5, 2.5, 17.0, 3.5, 8.5, 0.0) <= 1.0) {
               image.setPixel(xx, y, body);
            }
         }
      }
   }

   private static void paintBee(NativeImage image, int base, int tip) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double cx = x + 0.5;
            double cy = y + 0.5;
            int color = beeTexel(cx, cy, 24.0, 10.0, 22.0, 7.5, -12.0, base, tip);
            if (color == 0) {
               color = beeTexel(cx, cy, 16.0, 20.0, 13.0, 5.5, 20.0, base, tip);
            }

            if (color != 0) {
               image.setPixel(x, y, color);
            }
         }
      }
   }

   private static int beeTexel(double x, double y, double cx, double cy, double rx, double ry, double degrees, int base, int tip) {
      double rad = Math.toRadians(degrees);
      double dx = x - cx;
      double dy = y - cy;
      double ux = dx * Math.cos(rad) + dy * Math.sin(rad);
      double uy = -dx * Math.sin(rad) + dy * Math.cos(rad);
      double t = Math.sqrt(ux / rx * (ux / rx) + uy / ry * (uy / ry));
      if (t > 1.0) {
         return 0;
      } else if (t > 0.86) {
         return -436207616 | shade(tip, 0.4F) & 16777215;
      } else {
         boolean vein = Math.abs(uy) < 0.4
            || Math.abs(ux + rx * 0.35) < 0.4
            || Math.abs(ux - rx * 0.15) < 0.4
            || Math.abs(ux - rx * 0.55) < 0.35 && Math.abs(uy) < ry * 0.7
            || Math.abs(uy - ry * 0.45) < 0.35 && ux > -rx * 0.35;
         return vein ? -1107296256 | shade(tip, 0.5F) & 16777215 : 1426063360 | shade(lerp(base, tip, t * 0.4), 1.15F) & 16777215;
      }
   }

   private static void paintHawk(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 9; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0 && Math.sin(x * 0.55 + y * 1.1) > 0.72) {
               image.setPixel(x, y, shade(argb, 0.62F));
            }
         }
      }
   }

   private static void paintEnder(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, BAT_TIPS, 19.0, 4.0, 24.0, 1.0, 0.45, true, null);
      int glow = shade(tip, 1.25F);

      for (int i = 0; i < BAT_TIPS.length - 1; i++) {
         double mx = (BAT_TIPS[i][0] + BAT_TIPS[i + 1][0]) / 2.0;
         double my = (BAT_TIPS[i][1] + BAT_TIPS[i + 1][1]) / 2.0;
         line(image, 19.0, 4.0, 19.0 + (mx - 19.0) * 0.62, 4.0 + (my - 4.0) * 0.62, 0.45, glow, 0, false);
      }

      for (double[] mote : new double[][]{{30.0, 12.0}, {38.0, 20.0}, {26.0, 22.0}, {44.0, 10.0}, {34.0, 28.0}, {22.0, 15.0}}) {
         if (image.getPixel((int)mote[0], (int)mote[1]) >>> 24 != 0) {
            disc(image, mote[0], mote[1], 0.7, glow, glow);
         }
      }
   }

   private static void paintLeaf(NativeImage image, int base, int tip) {
      leafLobe(image, 27.0, 11.0, 25.0, 9.5, -14.0, base, tip);
      leafLobe(image, 17.0, 26.0, 15.0, 6.5, 28.0, base, tip);
      int stem = shade(tip, 0.7F);

      for (int y = 8; y < 26; y++) {
         for (int x = 0; x < 4; x++) {
            if (lobe(x + 0.5, y + 0.5, 2.0, 17.0, 3.0, 8.5, 0.0) <= 1.0) {
               image.setPixel(x, y, stem);
            }
         }
      }
   }

   private static void leafLobe(NativeImage image, double cx, double cy, double rx, double ry, double degrees, int base, int tip) {
      double rad = Math.toRadians(degrees);
      int vein = shade(base, 1.3F);
      int edge = shade(tip, 0.75F);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double dx = x + 0.5 - cx;
            double dy = y + 0.5 - cy;
            double ux = dx * Math.cos(rad) + dy * Math.sin(rad);
            double uy = -dx * Math.sin(rad) + dy * Math.cos(rad);
            double f = ux / rx;
            if (!(Math.abs(f) > 1.0)) {
               double half = ry * Math.pow(1.0 - f * f, 0.75) * (1.0 + 0.06 * Math.sin(ux * 2.2));
               double across = Math.abs(uy);
               if (!(across > half)) {
                  boolean midrib = across < 0.45;
                  boolean sideVein = ((ux + across * 1.2) % 4.5 + 4.5) % 4.5 < 0.5 && across < half * 0.85;
                  int color = across > half - 0.8 ? edge : (!midrib && !sideVein ? lerp(base, tip, across / ry * 0.8 + (f + 1.0) * 0.15) : vein);
                  image.setPixel(x, y, color);
               }
            }
         }
      }
   }

   private static void paintFrost(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int w = image.getWidth();
      int h = image.getHeight();
      boolean[] edge = edgeMask(image);
      int rim = shade(base, 1.2F) & 16777215;

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               image.setPixel(x, y, edge[y * w + x] ? -234881024 | rim : -1476395008 | lerp(argb, tip, 0.2) & 16777215);
            }
         }
      }

      for (double[] glint : new double[][]{{14.0, 8.0}, {30.0, 20.0}, {44.0, 14.0}, {24.0, 30.0}, {38.0, 6.0}}) {
         if (image.getPixel((int)glint[0], (int)glint[1]) >>> 24 != 0) {
            disc(image, glint[0], glint[1], 0.6, -1, -1);
         }
      }
   }

   private static void paintDragonfly(NativeImage image, int base, int tip) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int color = dragonflyTexel(x + 0.5, y + 0.5, 27.0, 9.0, 26.0, 4.2, -6.0, base, tip);
            if (color == 0) {
               color = dragonflyTexel(x + 0.5, y + 0.5, 24.0, 20.0, 23.0, 3.8, 9.0, base, tip);
            }

            if (color != 0) {
               image.setPixel(x, y, color);
            }
         }
      }
   }

   private static int dragonflyTexel(double x, double y, double cx, double cy, double rx, double ry, double degrees, int base, int tip) {
      double rad = Math.toRadians(degrees);
      double dx = x - cx;
      double dy = y - cy;
      double ux = dx * Math.cos(rad) + dy * Math.sin(rad);
      double uy = -dx * Math.sin(rad) + dy * Math.cos(rad);
      double t = Math.sqrt(ux / rx * (ux / rx) + uy / ry * (uy / ry));
      if (t > 1.0) {
         return 0;
      } else if (t > 0.84) {
         return -436207616 | shade(tip, 0.6F) & 16777215;
      } else if (ux > rx * 0.72 && Math.abs(uy + ry * 0.35) < 0.9) {
         return -603979776 | shade(tip, 0.45F) & 16777215;
      } else {
         boolean vein = Math.abs(uy) < 0.35 || Math.abs(uy + ry * 0.5) < 0.3 || (ux % 2.6 + 2.6) % 2.6 < 0.35;
         return vein ? -1275068416 | shade(tip, 0.7F) & 16777215 : 1073741824 | lerp(base, tip, t * 0.3) & 16777215;
      }
   }

   private static void paintLunaMoth(NativeImage image, int base, int tip) {
      int edge = shade(tip, 0.8F);
      fillPolygon(image, new double[][]{{17.0, 26.0}, {23.0, 24.0}, {44.0, 37.0}, {41.0, 39.5}}, (xx, yx) -> lerp(base, tip, (xx - 17.0) / 40.0));

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double t = Math.min(lobe(x + 0.5, y + 0.5, 26.0, 12.0, 24.0, 11.0, -10.0), lobe(x + 0.5, y + 0.5, 15.0, 24.0, 12.0, 8.0, 30.0));
            if (t <= 1.0) {
               image.setPixel(x, y, t > 0.9 ? shade(base, 0.82F) : lerp(base, shade(base, 1.12F), 1.0 - t));
            }
         }
      }

      lineOnShape(image, 3.0, 11.0, 47.0, 3.5, 0.8, edge);
      int pale = lerp(base, -2896, 0.7);
      disc(image, 30.0, 13.5, 2.5, edge, edge);
      disc(image, 30.0, 13.5, 1.2, pale, pale);
      disc(image, 15.5, 24.0, 1.6, edge, edge);
      int body = shade(base, 0.65F);

      for (int y = 8; y < 26; y++) {
         for (int xx = 0; xx < 5; xx++) {
            if (lobe(xx + 0.5, y + 0.5, 2.5, 17.0, 3.5, 8.5, 0.0) <= 1.0) {
               image.setPixel(xx, y, body);
            }
         }
      }
   }

   private static void paintRainbow(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               double lum = ((argb >> 16 & 0xFF) * 0.3 + (argb >> 8 & 0xFF) * 0.59 + (argb & 0xFF) * 0.11) / 255.0;
               image.setPixel(x, y, hsv(x * 5.2 + y * 1.5, 0.7, 0.45 + 0.55 * lum));
            }
         }
      }
   }

   private static void paintClockwork(NativeImage image, int base, int tip) {
      int dark = shade(base, 0.55F);
      double[][] ends = new double[][]{{54.0, 2.0}, {53.0, 14.0}, {47.0, 26.0}, {37.0, 34.0}, {25.0, 38.0}, {13.0, 34.0}};
      int mesh = shade(tip, 0.9F);
      fillPolygon(
         image,
         new double[][]{{2.0, 10.0}, {54.0, 2.0}, {53.0, 14.0}, {47.0, 26.0}, {37.0, 34.0}, {25.0, 38.0}, {13.0, 34.0}, {3.0, 24.0}},
         (x, y) -> (x + y) % 3 != 0 && (x - y + 99) % 3 != 0 ? 0 : mesh
      );

      for (double[] end : ends) {
         line(image, 2.0, 10.0, end[0], end[1], 0.55, dark, 0, false);
      }

      gear(image, 16.0, 16.0, 5.5, 10, base);
      gear(image, 30.0, 22.0, 7.5, 12, tip);
      gear(image, 44.0, 11.0, 4.5, 8, base);
      gear(image, 38.0, 31.0, 4.0, 8, base);
      gear(image, 22.0, 31.0, 3.6, 7, tip);

      for (double t = 0.0; t <= 1.0; t += 0.004) {
         disc(image, boneX(t), boneY(t), 1.6 - 0.6 * t, base, dark);
      }
   }

   private static void gear(NativeImage image, double cx, double cy, double r, int teeth, int color) {
      int dark = shade(color, 0.7F);

      for (int i = 0; i < teeth; i++) {
         double a = (Math.PI * 2) * i / teeth;
         double ca = Math.cos(a);
         double sa = Math.sin(a);
         fillPolygon(
            image,
            new double[][]{
               {cx + ca * (r - 0.5) - sa, cy + sa * (r - 0.5) + ca},
               {cx + ca * (r + 1.6) - sa * 0.7, cy + sa * (r + 1.6) + ca * 0.7},
               {cx + ca * (r + 1.6) + sa * 0.7, cy + sa * (r + 1.6) - ca * 0.7},
               {cx + ca * (r - 0.5) + sa, cy + sa * (r - 0.5) - ca}
            },
            (x, y) -> color
         );
      }

      disc(image, cx, cy, r, color, dark);
      disc(image, cx, cy, r * 0.62, dark, dark);
      disc(image, cx, cy, r * 0.45, color, color);
      clearDisc(image, cx, cy, r * 0.22);
   }

   private static void paintLightning(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.2, 0.34, false, null);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               image.setPixel(x, y, 1006632960 | lerp(argb, base, 0.6) & 16777215);
            }
         }
      }

      int glow = -536870912 | base & 16777215;
      int core = 0xFF000000 | tip & 16777215;
      bolt(image, 1.5, 10.0, 22.0, 3.0, 5, 1.3, glow, core);

      for (double[] end : DRAGON_TIPS) {
         bolt(image, 22.0, 3.0, end[0], end[1], 7, 1.6, glow, core);
      }
   }

   private static void bolt(NativeImage image, double x0, double y0, double x1, double y1, int segments, double amplitude, int glow, int core) {
      double length = Math.max(0.001, Math.hypot(x1 - x0, y1 - y0));
      double nx = -(y1 - y0) / length;
      double ny = (x1 - x0) / length;
      double[][] points = new double[segments + 1][];
      points[0] = new double[]{x0, y0};

      for (int i = 1; i <= segments; i++) {
         double t = (double)i / segments;
         double offset = i == segments ? 0.0 : (i % 2 == 0 ? 1.0 : -1.0) * amplitude * (0.6 + 0.4 * (i * 37 % 5) / 4.0);
         points[i] = new double[]{x0 + (x1 - x0) * t + nx * offset, y0 + (y1 - y0) * t + ny * offset};
      }

      for (int i = 0; i < segments; i++) {
         line(image, points[i][0], points[i][1], points[i + 1][0], points[i + 1][1], 0.9, glow, 0, false);
      }

      for (int i = 0; i < segments; i++) {
         line(image, points[i][0], points[i][1], points[i + 1][0], points[i + 1][1], 0.35, core, 0, false);
      }
   }

   private static void paintShadow(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int w = image.getWidth();

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               double f = (double)x / w;
               int noise = (x * 73856093 ^ y * 19349663) & 0xFF;
               if ((!(f > 0.5) || noise % 4 != 0) && (!(f > 0.8) || noise % 3 != 0)) {
                  int alpha = (int)Math.round(235.0 - 175.0 * f);
                  image.setPixel(x, y, alpha << 24 | lerp(base, tip, f * 0.8 + noise / 255.0 * 0.2) & 16777215);
               } else {
                  image.setPixel(x, y, 0);
               }
            }
         }
      }
   }

   private static void paintPeacock(NativeImage image, int base, int tip) {
      int gold = lerp(tip, -2052032, 0.75);

      for (int i = 0; i < 9; i++) {
         double t = 0.12 + 0.86 * i / 8.0;
         double angle = Math.toRadians(-4.0 + 30.0 * t);
         double length = 20.0 + 13.0 * Math.sin(Math.PI * Math.min(1.0, t * 1.1));
         double ax = boneX(t);
         double ay = boneY(t);
         feather(image, ax, ay, angle, length, 2.9, lerp(base, tip, t), true);
         double ex = ax + Math.sin(angle) * length * 0.8;
         double ey = ay + Math.cos(angle) * length * 0.8;
         disc(image, ex, ey, 2.4, gold, shade(gold, 0.6F));
         disc(image, ex, ey, 1.5, shade(base, 1.15F), shade(base, 1.15F));
         disc(image, ex, ey, 0.8, shade(tip, 0.35F), shade(tip, 0.35F));
      }

      for (int i = 0; i < 8; i++) {
         double t = i / 7.0;
         feather(image, boneX(t), boneY(t) + 0.5, Math.toRadians(12.0), 6.0 + 3.0 * Math.sin(Math.PI * t), 2.5, shade(base, 0.85F), false);
      }

      int bone = shade(base, 1.1F);
      int boneRim = shade(base, 0.7F);

      for (double t = 0.0; t <= 1.0; t += 0.004) {
         disc(image, boneX(t), boneY(t), 1.8 - 0.8 * t, bone, boneRim);
      }
   }

   private static void paintPetal(NativeImage image, int base, int tip) {
      double rootX = 3.0;
      double rootY = 17.0;

      for (double[] petal : new double[][]{{-38.0, 30.0}, {-12.0, 40.0}, {16.0, 36.0}, {44.0, 26.0}}) {
         double a = Math.toRadians(petal[0]);
         double rx = petal[1] / 2.0;
         double ry = 6.0;
         double cx = rootX + Math.cos(a) * rx;
         double cy = rootY + Math.sin(a) * rx;

         for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
               double dx = x + 0.5 - cx;
               double dy = y + 0.5 - cy;
               double ux = dx * Math.cos(a) + dy * Math.sin(a);
               double uy = -dx * Math.sin(a) + dy * Math.cos(a);
               double f = ux / rx;
               if (!(Math.abs(f) > 1.0)) {
                  double half = ry * Math.sqrt(1.0 - f * f);
                  double across = Math.abs(uy);
                  if (!(across > half) && (!(f > 0.88) || !(across < 0.9))) {
                     int color;
                     if (across > half - 0.9) {
                        color = shade(base, 0.88F);
                     } else if (across < 0.3 && f < 0.6) {
                        color = shade(base, 0.92F);
                     } else {
                        color = lerp(base, tip, (f + 1.0) / 2.0 * 0.9);
                     }

                     image.setPixel(x, y, color);
                  }
               }
            }
         }
      }

      disc(image, rootX + 1.0, rootY, 2.6, lerp(base, -8090, 0.7), shade(base, 0.7F));
   }

   private static void paintHologram(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int w = image.getWidth();
      int h = image.getHeight();
      boolean[] edge = edgeMask(image);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               int argb;
               if (edge[y * w + x]) {
                  argb = -268435456 | shade(base, 1.3F) & 16777215;
               } else if (x % 4 != 0 && y % 4 != 0) {
                  argb = (y % 2 == 0 ? 973078528 : 637534208) | lerp(base, tip, (double)y / h) & 16777215;
               } else {
                  argb = -1073741824 | base & 16777215;
               }

               image.setPixel(x, y, argb);
            }
         }
      }
   }

   private static void paintStainedGlass(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.5, 0.34, false, null);
      double[][] seeds = new double[][]{
         {8.0, 12.0},
         {16.0, 6.0},
         {26.0, 4.0},
         {38.0, 3.0},
         {48.0, 6.0},
         {14.0, 20.0},
         {26.0, 14.0},
         {38.0, 12.0},
         {50.0, 14.0},
         {20.0, 28.0},
         {32.0, 22.0},
         {44.0, 22.0},
         {30.0, 33.0},
         {40.0, 30.0},
         {10.0, 30.0}
      };
      int[] panes = new int[]{base, tip, lerp(base, tip, 0.5), shade(base, 1.3F), shade(tip, 1.3F), lerp(base, -1523648, 0.55)};
      int lead = -15066594;
      int w = image.getWidth();
      boolean[] edge = edgeMask(image);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               double nearest = Double.MAX_VALUE;
               double second = Double.MAX_VALUE;
               int cell = 0;

               for (int i = 0; i < seeds.length; i++) {
                  double d = Math.hypot(x + 0.5 - seeds[i][0], y + 0.5 - seeds[i][1]);
                  if (d < nearest) {
                     second = nearest;
                     nearest = d;
                     cell = i;
                  } else if (d < second) {
                     second = d;
                  }
               }

               image.setPixel(x, y, !edge[y * w + x] && !(second - nearest < 0.9) ? -1275068416 | panes[cell % panes.length] & 16777215 : lead);
            }
         }
      }

      line(image, 1.5, 10.0, 22.0, 3.0, 0.7, lead, 0, false);

      for (double[] end : DRAGON_TIPS) {
         line(image, 22.0, 3.0, end[0], end[1], 0.6, lead, 0, false);
      }
   }

   private static boolean[] edgeMask(NativeImage image) {
      int w = image.getWidth();
      int h = image.getHeight();
      boolean[] edge = new boolean[w * h];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               edge[y * w + x] = x == 0
                  || x == w - 1
                  || y == 0
                  || y == h - 1
                  || image.getPixel(x - 1, y) >>> 24 == 0
                  || image.getPixel(x + 1, y) >>> 24 == 0
                  || image.getPixel(x, y - 1) >>> 24 == 0
                  || image.getPixel(x, y + 1) >>> 24 == 0;
            }
         }
      }

      return edge;
   }

   private static void lineOnShape(NativeImage image, double x0, double y0, double x1, double y1, double radius, int color) {
      double length = Math.max(0.001, Math.hypot(x1 - x0, y1 - y0));
      double dx = (x1 - x0) / length;
      double dy = (y1 - y0) / length;

      for (int py = Math.max(0, (int)Math.floor(Math.min(y0, y1) - radius));
         py <= Math.min(image.getHeight() - 1, (int)Math.ceil(Math.max(y0, y1) + radius));
         py++
      ) {
         for (int px = Math.max(0, (int)Math.floor(Math.min(x0, x1) - radius));
            px <= Math.min(image.getWidth() - 1, (int)Math.ceil(Math.max(x0, x1) + radius));
            px++
         ) {
            double along = Math.max(0.0, Math.min(length, (px + 0.5 - x0) * dx + (py + 0.5 - y0) * dy));
            double dist = Math.hypot(px + 0.5 - (x0 + dx * along), py + 0.5 - (y0 + dy * along));
            if (dist <= radius && image.getPixel(px, py) >>> 24 != 0) {
               image.setPixel(px, py, color);
            }
         }
      }
   }

   private static int hsv(double hue, double saturation, double value) {
      double h = (hue % 360.0 + 360.0) % 360.0 / 60.0;
      double c = value * saturation;
      double x = c * (1.0 - Math.abs(h % 2.0 - 1.0));
      double r = 0.0;
      double g = 0.0;
      double b = 0.0;
      int sector = (int)h;
      if (sector == 0) {
         r = c;
         g = x;
      } else if (sector == 1) {
         r = x;
         g = c;
      } else if (sector == 2) {
         g = c;
         b = x;
      } else if (sector == 3) {
         g = x;
         b = c;
      } else if (sector == 4) {
         r = x;
         b = c;
      } else {
         r = c;
         b = x;
      }

      double m = value - c;
      return 0xFF000000 | (int)Math.round((r + m) * 255.0) << 16 | (int)Math.round((g + m) * 255.0) << 8 | (int)Math.round((b + m) * 255.0);
   }

   private static int noise(int x, int y) {
      return (x * 73856093 ^ y * 19349663) & 0xFF;
   }

   private static double luminance(int argb) {
      return ((argb >> 16 & 0xFF) * 0.3 + (argb >> 8 & 0xFF) * 0.59 + (argb & 0xFF) * 0.11) / 255.0;
   }

   private static boolean opaqueAt(NativeImage image, int x, int y) {
      return x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight() && image.getPixel(x, y) >>> 24 != 0;
   }

   private static double[][] rect(double cx, double cy, double w, double h) {
      return new double[][]{{cx - w / 2.0, cy - h / 2.0}, {cx + w / 2.0, cy - h / 2.0}, {cx + w / 2.0, cy + h / 2.0}, {cx - w / 2.0, cy + h / 2.0}};
   }

   private static void paintOwl(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int fleck = shade(tip, 0.75F);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               int n = noise(x, y);
               if (n < 30) {
                  image.setPixel(x, y, shade(argb, 0.62F));
               } else if ((x + y * 2) % 7 == 0 && n < 140) {
                  image.setPixel(x, y, fleck);
               }
            }
         }
      }
   }

   private static void paintParrot(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int gold = -865972;
      int h = image.getHeight();

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               double t = (double)y / h + x * 0.004;
               int band = t < 0.4 ? base : (t < 0.52 ? lerp(base, gold, 0.85) : tip);
               image.setPixel(x, y, shade(band, (float)(0.6 + 0.55 * luminance(argb))));
            }
         }
      }
   }

   private static void paintCrow(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      boolean[] edge = edgeMask(image);
      int w = image.getWidth();

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < w; x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               if (edge[y * w + x] && y > 24 && noise(x, y) % 3 == 0) {
                  image.setPixel(x, y, 0);
               } else {
                  int color = lerp(base, tip, luminance(argb) * 0.8);
                  image.setPixel(x, y, (x * 2 + y) % 11 == 0 ? shade(color, 1.6F) : color);
               }
            }
         }
      }
   }

   private static void paintPterodactyl(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, new double[][]{{55.0, 3.0}, {44.0, 22.0}, {24.0, 36.0}}, 18.0, 5.0, 30.0, 1.6, 0.25, false, null);
      int claw = shade(tip, 0.6F);

      for (int i = 0; i < 3; i++) {
         double cx = 16.5 + i * 1.6;
         double peak = 0.5 + i * 0.4;
         fillPolygon(image, new double[][]{{cx - 0.7, 4.5}, {cx - 0.2, peak}, {cx + 0.7, 4.0}}, (x, y) -> claw);
      }
   }

   private static void paintGargoyle(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.6, 0.34, true, null);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               image.setPixel(x, y, shade(argb, 0.85F + noise(x, y) / 255.0F * 0.28F));
            }
         }
      }

      int crack = shade(tip, 0.55F);

      for (double[][] c : new double[][][]{
         {{24.0, 10.0}, {29.0, 14.0}, {27.0, 18.0}, {32.0, 23.0}},
         {{40.0, 8.0}, {37.0, 13.0}, {41.0, 17.0}},
         {{18.0, 22.0}, {22.0, 26.0}, {20.0, 31.0}},
         {{34.0, 28.0}, {38.0, 25.0}, {43.0, 27.0}}
      }) {
         for (int i = 0; i < c.length - 1; i++) {
            lineOnShape(image, c[i][0], c[i][1], c[i + 1][0], c[i + 1][1], 0.5, crack);
         }
      }
   }

   private static void paintSwallowtail(NativeImage image, int base, int tip) {
      fillPolygon(image, new double[][]{{21.0, 29.0}, {26.0, 29.0}, {34.0, 39.5}, {31.0, 39.5}}, (xx, yx) -> tip);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            double upper = lobe(x + 0.5, y + 0.5, 27.0, 12.0, 25.0, 11.0, -12.0);
            double lower = lobe(x + 0.5, y + 0.5, 18.0, 27.0, 15.0, 10.0, 25.0);
            double t = Math.min(upper, lower);
            if (t <= 1.0) {
               boolean bar = upper <= 1.0 && ((x - y * 0.35) % 8.0 + 8.0) % 8.0 < 2.0;
               image.setPixel(x, y, !(t > 0.8) && !bar ? lerp(base, shade(base, 0.9F), t) : tip);
            }
         }
      }

      for (double a : new double[]{0.4, 1.0, 1.6, 2.2}) {
         spotOnLobe(image, 18.0, 27.0, 15.0, 10.0, 25.0, a, 0.9, 1.0, -12947496);
      }

      disc(image, 22.5, 31.0, 1.3, -2602454, -2602454);

      for (int y = 8; y < 26; y++) {
         for (int xx = 0; xx < 5; xx++) {
            if (lobe(xx + 0.5, y + 0.5, 2.5, 17.0, 3.5, 8.5, 0.0) <= 1.0) {
               image.setPixel(xx, y, tip);
            }
         }
      }
   }

   private static void paintLadybug(NativeImage image, int base, int tip) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int color = fairyTexel(x + 0.5, y + 0.5, 32.0, 18.0, 23.0, 8.5, 14.0, -1511694, tip);
            if (color != 0) {
               image.setPixel(x, y, color);
            }
         }
      }

      int rim = shade(base, 0.6F);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int xx = 0; xx < image.getWidth(); xx++) {
            double t = lobe(xx + 0.5, y + 0.5, 14.0, 13.0, 13.0, 9.0, -20.0);
            if (t <= 1.0) {
               image.setPixel(xx, y, t > 0.88 ? rim : lerp(shade(base, 1.08F), base, t));
            }
         }
      }

      for (double[] spot : new double[][]{{9.0, 11.0, 1.8}, {16.0, 9.0, 1.6}, {20.0, 15.0, 1.9}, {12.0, 17.0, 1.5}, {6.0, 15.0, 1.2}}) {
         disc(image, spot[0], spot[1], spot[2], tip, tip);
      }

      disc(image, 9.0, 8.0, 0.8, -5912, -5912);
   }

   private static void paintInferno(NativeImage image, int base, int tip) {
      for (int i = 0; i < 11; i++) {
         double t = 0.02 + 0.96 * i / 10.0;
         double length = 18.0 + 14.0 * Math.sin(Math.PI * Math.min(1.0, t * 1.15));
         flame(image, boneX(t), boneY(t), Math.toRadians(4.0 + 30.0 * t), length, 3.2, shade(base, 0.9F), tip);
      }

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               double lum = luminance(argb);
               int alpha = (int)Math.round(90.0 + 165.0 * Math.min(1.0, lum * 1.2));
               image.setPixel(x, y, alpha << 24 | lerp(base, tip, lum) & 16777215);
            }
         }
      }
   }

   private static void paintAqua(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int w = image.getWidth();
      int h = image.getHeight();
      boolean[] edge = edgeMask(image);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               int color;
               if (edge[y * w + x]) {
                  color = -536870912 | shade(base, 1.25F) & 16777215;
               } else if (Math.sin(x * 0.5 + y * 0.9) > 0.55) {
                  color = -1342177280 | tip & 16777215;
               } else {
                  color = 2013265920 | lerp(base, tip, (double)y / h * 0.4) & 16777215;
               }

               image.setPixel(x, y, color);
            }
         }
      }

      int drop = 0xFF000000 | tip & 16777215;

      for (int xx = 10; xx < w; xx += 7) {
         for (int y = h - 1; y >= 0; y--) {
            if (image.getPixel(xx, y) >>> 24 != 0) {
               if (y + 3 < h) {
                  disc(image, xx + 0.5, y + 2.5, 0.9, drop, drop);
               }
               break;
            }
         }
      }
   }

   private static void paintGalaxy(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, BAT_TIPS, 19.0, 4.0, 24.0, 0.9, 0.5, false, null);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               int n = noise(x, y);
               int color = lerp(base, shade(tip, 0.35F), n / 255.0 * 0.5);
               double swirl = Math.sin(x * 0.25 + Math.sin(y * 0.3) * 2.0);
               if (swirl > 0.55) {
                  color = lerp(color, tip, (swirl - 0.55) * 1.4);
               }

               image.setPixel(x, y, n < 6 ? -1 : color);
            }
         }
      }

      for (double[] star : new double[][]{{30.0, 14.0}, {42.0, 9.0}, {22.0, 24.0}, {36.0, 27.0}}) {
         if (opaqueAt(image, (int)star[0], (int)star[1])) {
            disc(image, star[0], star[1], 0.8, -1, -1);
         }
      }

      rimEdges(image, 0.6F);
   }

   private static void paintOrigami(NativeImage image, int base, int tip) {
      double[] root = new double[]{2.0, 10.0};
      double[][] outer = new double[][]{{54.0, 2.0}, {52.0, 14.0}, {46.0, 26.0}, {36.0, 34.0}, {24.0, 38.0}, {12.0, 32.0}, {3.0, 22.0}};

      for (int i = 0; i < outer.length - 1; i++) {
         double[] a = outer[i];
         double[] b = outer[i + 1];
         double[] middle = new double[]{(a[0] + b[0]) / 2.0 + (b[1] - a[1]) * 0.12, (a[1] + b[1]) / 2.0 - (b[0] - a[0]) * 0.12};
         int light = lerp(base, tip, i * 0.08);
         int dark = shade(light, 0.8F);
         fillPolygon(image, new double[][]{root, a, middle}, (x, y) -> light);
         fillPolygon(image, new double[][]{root, middle, b}, (x, y) -> dark);
      }

      int fold = shade(tip, 0.75F);

      for (double[] corner : outer) {
         lineOnShape(image, root[0], root[1], corner[0], corner[1], 0.35, fold);
      }

      rimEdges(image, 0.7F);
   }

   private static void paintPixel(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int w = image.getWidth();
      int h = image.getHeight();
      int block = 3;
      int[] out = new int[w * h];

      for (int by = 0; by < h; by += block) {
         for (int bx = 0; bx < w; bx += block) {
            int covered = 0;
            double lum = 0.0;

            for (int dy = 0; dy < block && by + dy < h; dy++) {
               for (int dx = 0; dx < block && bx + dx < w; dx++) {
                  int argb = image.getPixel(bx + dx, by + dy);
                  if (argb >>> 24 != 0) {
                     covered++;
                     lum += luminance(argb);
                  }
               }
            }

            if (covered * 2 >= block * block) {
               int color = shade(lerp(base, tip, (double)by / h), (float)(0.7 + 0.5 * lum / covered));

               for (int dy = 0; dy < block && by + dy < h; dy++) {
                  for (int dxx = 0; dxx < block && bx + dxx < w; dxx++) {
                     out[(by + dy) * w + bx + dxx] = color;
                  }
               }
            }
         }
      }

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            image.setPixel(x, y, out[y * w + x]);
         }
      }

      rimEdges(image, 0.55F);
   }

   private static void paintCircuit(NativeImage image, int base, int tip) {
      paintMembrane(image, base, base, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.2, 0.34, false, null);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               image.setPixel(x, y, x % 4 == 0 && y % 4 == 0 ? shade(base, 1.2F) : shade(base, 0.92F + noise(x, y) / 255.0F * 0.12F));
            }
         }
      }

      for (int[] route : new int[][]{
         {4, 11, 20, 11, 20, 4, 40, 4},
         {6, 14, 30, 14, 30, 20, 48, 20},
         {5, 18, 14, 18, 14, 28, 34, 28},
         {10, 22, 10, 33, 22, 33},
         {24, 8, 24, 16, 38, 16, 38, 10, 50, 10}
      }) {
         for (int i = 0; i + 3 < route.length; i += 2) {
            lineOnShape(image, route[i], route[i + 1], route[i + 2], route[i + 3], 0.45, tip);
         }

         for (int i = 0; i + 1 < route.length; i += 2) {
            if (opaqueAt(image, route[i], route[i + 1])) {
               disc(image, route[i] + 0.5, route[i + 1] + 0.5, 0.9, tip, tip);
            }
         }
      }

      for (double[] chip : new double[][]{{30.0, 22.0}, {18.0, 24.0}}) {
         if (opaqueAt(image, (int)chip[0], (int)chip[1])) {
            fillPolygon(image, rect(chip[0], chip[1], 5.0, 3.5), (xx, yx) -> -15066594);
         }
      }

      rimEdges(image, 0.6F);
   }

   private static void paintMagma(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.5, 0.34, true, null);
      double[][] seeds = new double[][]{
         {8.0, 11.0},
         {15.0, 5.0},
         {24.0, 4.0},
         {35.0, 3.0},
         {46.0, 6.0},
         {14.0, 19.0},
         {25.0, 13.0},
         {37.0, 12.0},
         {49.0, 15.0},
         {21.0, 27.0},
         {32.0, 21.0},
         {43.0, 23.0},
         {29.0, 33.0},
         {39.0, 31.0}
      };
      int glow = lerp(tip, -8090, 0.35);
      int w = image.getWidth();
      boolean[] edge = edgeMask(image);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               double nearest = Double.MAX_VALUE;
               double second = Double.MAX_VALUE;

               for (double[] seed : seeds) {
                  double d = Math.hypot(x + 0.5 - seed[0], y + 0.5 - seed[1]);
                  if (d < nearest) {
                     second = nearest;
                     nearest = d;
                  } else if (d < second) {
                     second = d;
                  }
               }

               double gap = second - nearest;
               int color = gap < 1.1 ? (gap < 0.5 ? glow : tip) : shade(base, 0.9F + noise(x, y) / 255.0F * 0.3F);
               image.setPixel(x, y, edge[y * w + x] ? shade(base, 0.6F) : color);
            }
         }
      }
   }

   private static void paintCandy(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               int stripe = (x + y) / 4 % 2 == 0 ? base : tip;
               int color = shade(stripe, (float)(0.72 + 0.4 * luminance(argb)));
               image.setPixel(x, y, y < 12 && (x - y) % 13 == 0 ? lerp(color, -1, 0.6) : color);
            }
         }
      }
   }

   private static void paintSnowflake(NativeImage image, int base, int tip) {
      paintAngel(image, base, tip);
      int w = image.getWidth();
      boolean[] edge = edgeMask(image);
      int rim = shade(base, 1.1F) & 16777215;

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               image.setPixel(x, y, edge[y * w + x] ? -536870912 | rim : 1543503872 | base & 16777215);
            }
         }
      }

      int flake = 0xFF000000 | shade(tip, 0.8F) & 16777215;

      for (double[] f : new double[][]{{14.0, 14.0, 4.5}, {28.0, 22.0, 5.5}, {42.0, 12.0, 4.5}, {34.0, 32.0, 4.0}, {20.0, 30.0, 3.5}, {48.0, 24.0, 3.5}}) {
         for (int arm = 0; arm < 6; arm++) {
            double a = Math.PI * arm / 3.0;
            lineOnShape(image, f[0], f[1], f[0] + Math.cos(a) * f[2], f[1] + Math.sin(a) * f[2], 0.6, flake);
            double mx = f[0] + Math.cos(a) * f[2] * 0.6;
            double my = f[1] + Math.sin(a) * f[2] * 0.6;

            for (int side = -1; side <= 1; side += 2) {
               double b = a + side * 0.7;
               lineOnShape(image, mx, my, mx + Math.cos(b) * f[2] * 0.3, my + Math.sin(b) * f[2] * 0.3, 0.5, flake);
            }
         }
      }
   }

   private static void paintVine(NativeImage image, int base, int tip) {
      double[][] ends = new double[][]{{54.0, 3.0}, {52.0, 14.0}, {46.0, 25.0}, {36.0, 33.0}, {24.0, 37.0}, {12.0, 31.0}};
      int stem = shade(tip, 0.9F);

      for (int e = 0; e < ends.length; e++) {
         double dx = ends[e][0] - 2.0;
         double dy = ends[e][1] - 10.0;
         double length = Math.hypot(dx, dy);
         double nx = -dy / length;
         double ny = dx / length;

         for (double t = 0.0; t <= 1.0; t += 0.01) {
            double wobble = Math.sin(t * 9.0 + e) * 1.4;
            disc(image, 2.0 + dx * t + nx * wobble, 10.0 + dy * t + ny * wobble, 0.95 - 0.45 * t, stem, stem);
         }

         for (int k = 1; k <= 8; k++) {
            double t = k / 9.0;
            double wobble = Math.sin(t * 9.0 + e) * 1.4;
            double px = 2.0 + dx * t + nx * wobble;
            double py = 10.0 + dy * t + ny * wobble;
            int side = k % 2 == 0 ? 1 : -1;
            double tipX = px + nx * side * 5.5 + dx / length * 2.0;
            double tipY = py + ny * side * 5.5 + dy / length * 2.0;
            double midX = (px + tipX) / 2.0;
            double midY = (py + tipY) / 2.0;
            double leafLength = Math.max(0.001, Math.hypot(tipX - px, tipY - py));
            double wx = -(tipY - py) / leafLength * 1.6;
            double wy = (tipX - px) / leafLength * 1.6;
            int leaf = lerp(shade(base, 1.15F), shade(base, 1.45F), t);
            fillPolygon(image, new double[][]{{px, py}, {midX + wx, midY + wy}, {tipX, tipY}, {midX - wx, midY - wy}}, (x, y) -> leaf);
         }
      }
   }

   private static void paintTattered(NativeImage image, int base, int tip) {
      paintMembrane(
         image,
         base,
         tip,
         BAT_TIPS,
         19.0,
         4.0,
         24.0,
         1.0,
         0.5,
         false,
         new double[][]{{30.0, 16.0, 1.6}, {22.0, 24.0, 1.2}, {40.0, 22.0, 1.4}, {34.0, 8.0, 1.0}}
      );
      int w = image.getWidth();
      boolean[] edge = edgeMask(image);

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < w; x++) {
            int argb = image.getPixel(x, y);
            if (argb >>> 24 != 0) {
               int n = noise(x, y);
               if (edge[y * w + x] && y > 8 && n % 2 == 0) {
                  image.setPixel(x, y, 0);
               } else {
                  image.setPixel(x, y, shade(argb, (y % 2 == 0 ? 0.95F : 0.88F) + n / 255.0F * 0.1F));
               }
            }
         }
      }

      int patch = shade(base, 1.2F);
      fillPolygon(image, new double[][]{{12.0, 12.0}, {19.0, 11.0}, {20.0, 17.0}, {13.0, 18.0}}, (xx, y) -> patch);

      for (int i = 0; i < 4; i++) {
         lineOnShape(image, 12.5 + i * 2.0, 11.6, 13.3 + i * 2.0, 11.4, 0.35, tip);
      }

      rimEdges(image, 0.7F);
   }

   private static void paintNeon(NativeImage image, int base, int tip) {
      paintMembrane(image, base, tip, DRAGON_TIPS, 22.0, 3.0, 27.0, 1.0, 0.34, false, null);
      int w = image.getWidth();
      int h = image.getHeight();
      boolean[] edge = edgeMask(image);
      boolean[] near = new boolean[w * h];

      for (int y = 1; y < h - 1; y++) {
         for (int x = 1; x < w - 1; x++) {
            int i = y * w + x;
            near[i] = !edge[i] && image.getPixel(x, y) >>> 24 != 0 && (edge[i - 1] || edge[i + 1] || edge[i - w] || edge[i + w]);
         }
      }

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               int i = y * w + x;
               image.setPixel(x, y, edge[i] ? 0xFF000000 | base & 16777215 : (near[i] ? -1879048192 | base & 16777215 : 469762048 | tip & 16777215));
            }
         }
      }

      int bright = 0xFF000000 | tip & 16777215;
      line(image, 1.5, 10.0, 22.0, 3.0, 0.45, bright, 0, false);

      for (double[] end : DRAGON_TIPS) {
         line(image, 22.0, 3.0, end[0], end[1], 0.4, bright, 0, false);
      }
   }

   private static void paintBubble(NativeImage image, int base, int tip) {
      double[][] bubbles = new double[][]{
         {9.0, 10.0, 6.0},
         {20.0, 8.0, 7.0},
         {32.0, 7.0, 6.5},
         {43.0, 6.0, 5.5},
         {51.0, 8.0, 4.0},
         {10.0, 22.0, 5.0},
         {19.0, 27.0, 5.5},
         {28.0, 32.0, 4.5},
         {35.0, 22.0, 4.5},
         {26.0, 17.0, 4.0}
      };

      for (double[] bubble : bubbles) {
         bubble(image, bubble[0], bubble[1], bubble[2], base, tip);
      }
   }

   private static void bubble(NativeImage image, double cx, double cy, double r, int base, int tip) {
      for (int py = Math.max(0, (int)Math.floor(cy - r)); py <= Math.min(image.getHeight() - 1, (int)Math.ceil(cy + r)); py++) {
         for (int px = Math.max(0, (int)Math.floor(cx - r)); px <= Math.min(image.getWidth() - 1, (int)Math.ceil(cx + r)); px++) {
            double dx = px + 0.5 - cx;
            double dy = py + 0.5 - cy;
            double d = Math.hypot(dx, dy);
            if (!(d > r)) {
               if (d > r - 1.2) {
                  image.setPixel(px, py, -402653184 | tip & 16777215);
               } else {
                  int film = lerp(hsv(Math.toDegrees(Math.atan2(dy, dx)) + px * 4.0, 0.55, 1.0), base, 0.45);
                  image.setPixel(px, py, 1342177280 | film & 16777215);
               }
            }
         }
      }

      disc(image, cx - r * 0.4, cy - r * 0.4, Math.max(0.6, r * 0.18), -385875969, -385875969);
   }

   private static void clearDisc(NativeImage image, double cx, double cy, double radius) {
      for (int py = Math.max(0, (int)Math.floor(cy - radius)); py <= Math.min(image.getHeight() - 1, (int)Math.ceil(cy + radius)); py++) {
         for (int px = Math.max(0, (int)Math.floor(cx - radius)); px <= Math.min(image.getWidth() - 1, (int)Math.ceil(cx + radius)); px++) {
            if (Math.hypot(px + 0.5 - cx, py + 0.5 - cy) <= radius) {
               image.setPixel(px, py, 0);
            }
         }
      }
   }

   private static double lobe(double x, double y, double cx, double cy, double rx, double ry, double degrees) {
      double rad = Math.toRadians(degrees);
      double dx = x - cx;
      double dy = y - cy;
      double ux = dx * Math.cos(rad) + dy * Math.sin(rad);
      double uy = -dx * Math.sin(rad) + dy * Math.cos(rad);
      return Math.sqrt(ux / rx * (ux / rx) + uy / ry * (uy / ry));
   }

   private static void spotOnLobe(
      NativeImage image, double cx, double cy, double rx, double ry, double degrees, double angle, double at, double radius, int color
   ) {
      double rad = Math.toRadians(degrees);
      double ux = Math.cos(angle) * rx * at;
      double uy = Math.sin(angle) * ry * at;
      double x = cx + ux * Math.cos(rad) - uy * Math.sin(rad);
      double y = cy + ux * Math.sin(rad) + uy * Math.cos(rad);
      if (x >= 0.0 && x < image.getWidth() && y >= 0.0 && y < image.getHeight() && image.getPixel((int)x, (int)y) >>> 24 != 0) {
         disc(image, x, y, radius, color, color);
      }
   }

   private static void fillPolygon(NativeImage image, double[][] points, IntBinaryOperator color) {
      double minX = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double maxY = -Double.MAX_VALUE;

      for (double[] p : points) {
         minX = Math.min(minX, p[0]);
         maxX = Math.max(maxX, p[0]);
         minY = Math.min(minY, p[1]);
         maxY = Math.max(maxY, p[1]);
      }

      for (int py = Math.max(0, (int)Math.floor(minY)); py <= Math.min(image.getHeight() - 1, (int)Math.ceil(maxY)); py++) {
         for (int px = Math.max(0, (int)Math.floor(minX)); px <= Math.min(image.getWidth() - 1, (int)Math.ceil(maxX)); px++) {
            if (inside(points, px + 0.5, py + 0.5)) {
               image.setPixel(px, py, color.applyAsInt(px, py));
            }
         }
      }
   }

   private static boolean inside(double[][] points, double x, double y) {
      boolean in = false;
      int i = 0;

      for (int j = points.length - 1; i < points.length; j = i++) {
         double xi = points[i][0];
         double yi = points[i][1];
         double xj = points[j][0];
         double yj = points[j][1];
         if (yi > y != yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
            in = !in;
         }
      }

      return in;
   }

   private static void carveBetween(NativeImage image, double[] a, double[] b, double wristX, double wristY, double bite) {
      double mx = (a[0] + b[0]) / 2.0;
      double my = (a[1] + b[1]) / 2.0;
      double length = Math.hypot(b[0] - a[0], b[1] - a[1]);
      double nx = -(b[1] - a[1]) / length;
      double ny = (b[0] - a[0]) / length;
      if (nx * (mx - wristX) + ny * (my - wristY) < 0.0) {
         nx = -nx;
         ny = -ny;
      }

      double radius = length * 0.55;
      double offset = radius - length * bite * 0.5;
      double cx = mx + nx * offset;
      double cy = my + ny * offset;

      for (int py = Math.max(0, (int)Math.floor(cy - radius)); py <= Math.min(image.getHeight() - 1, (int)Math.ceil(cy + radius)); py++) {
         for (int px = Math.max(0, (int)Math.floor(cx - radius)); px <= Math.min(image.getWidth() - 1, (int)Math.ceil(cx + radius)); px++) {
            if (Math.hypot(px + 0.5 - cx, py + 0.5 - cy) <= radius) {
               image.setPixel(px, py, 0);
            }
         }
      }
   }

   private static void rimEdges(NativeImage image, float factor) {
      int w = image.getWidth();
      int h = image.getHeight();
      boolean[] edge = new boolean[w * h];

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            if (image.getPixel(x, y) >>> 24 != 0) {
               boolean touches = x > 1 && image.getPixel(x - 1, y) >>> 24 == 0
                  || x < w - 1 && image.getPixel(x + 1, y) >>> 24 == 0
                  || y > 0 && image.getPixel(x, y - 1) >>> 24 == 0
                  || y < h - 1 && image.getPixel(x, y + 1) >>> 24 == 0;
               edge[y * w + x] = touches;
            }
         }
      }

      for (int y = 0; y < h; y++) {
         for (int xx = 0; xx < w; xx++) {
            if (edge[y * w + xx]) {
               image.setPixel(xx, y, shade(image.getPixel(xx, y), factor));
            }
         }
      }
   }

   private static void line(NativeImage image, double x0, double y0, double x1, double y1, double radius, int color, int rim, boolean withRim) {
      double length = Math.max(0.001, Math.hypot(x1 - x0, y1 - y0));
      double dx = (x1 - x0) / length;
      double dy = (y1 - y0) / length;
      int minX = Math.max(0, (int)Math.floor(Math.min(x0, x1) - radius - 1.0));
      int maxX = Math.min(image.getWidth() - 1, (int)Math.ceil(Math.max(x0, x1) + radius + 1.0));
      int minY = Math.max(0, (int)Math.floor(Math.min(y0, y1) - radius - 1.0));
      int maxY = Math.min(image.getHeight() - 1, (int)Math.ceil(Math.max(y0, y1) + radius + 1.0));

      for (int py = minY; py <= maxY; py++) {
         for (int px = minX; px <= maxX; px++) {
            double cx = px + 0.5;
            double cy = py + 0.5;
            double along = Math.max(0.0, Math.min(length, (cx - x0) * dx + (cy - y0) * dy));
            double dist = Math.hypot(cx - (x0 + dx * along), cy - (y0 + dy * along));
            if (dist <= radius) {
               image.setPixel(px, py, color);
            } else if (withRim && dist <= radius + 0.8) {
               image.setPixel(px, py, rim);
            }
         }
      }
   }

   private static void feather(NativeImage image, double ax, double ay, double angle, double length, double halfWidth, int color, boolean shaft) {
      double dx = Math.sin(angle);
      double dy = Math.cos(angle);
      double ex = ax + dx * length;
      double ey = ay + dy * length;
      int rim = shade(color, 0.7F);
      int shaftColor = shade(color, 1.12F);
      int minX = Math.max(0, (int)Math.floor(Math.min(ax, ex) - halfWidth - 2.0));
      int maxX = Math.min(image.getWidth() - 1, (int)Math.ceil(Math.max(ax, ex) + halfWidth + 2.0));
      int minY = Math.max(0, (int)Math.floor(Math.min(ay, ey) - halfWidth - 2.0));
      int maxY = Math.min(image.getHeight() - 1, (int)Math.ceil(Math.max(ay, ey) + halfWidth + 2.0));

      for (int py = minY; py <= maxY; py++) {
         for (int px = minX; px <= maxX; px++) {
            double cx = px + 0.5;
            double cy = py + 0.5;
            double along = Math.max(0.0, Math.min(1.0, ((cx - ax) * dx + (cy - ay) * dy) / length));
            double qx = ax + dx * along * length;
            double qy = ay + dy * along * length;
            double dist = Math.hypot(cx - qx, cy - qy);
            double radius = halfWidth * (1.0 - 0.45 * along * along);
            if (dist <= radius) {
               boolean onShaft = shaft && along < 0.85 && Math.abs((cx - ax) * dy - (cy - ay) * dx) < 0.5;
               image.setPixel(px, py, onShaft ? shaftColor : color);
            } else if (dist <= radius + 1.0) {
               image.setPixel(px, py, rim);
            }
         }
      }
   }

   private static void disc(NativeImage image, double cx, double cy, double radius, int color, int rim) {
      int minX = Math.max(0, (int)Math.floor(cx - radius - 1.0));
      int maxX = Math.min(image.getWidth() - 1, (int)Math.ceil(cx + radius + 1.0));
      int minY = Math.max(0, (int)Math.floor(cy - radius - 1.0));
      int maxY = Math.min(image.getHeight() - 1, (int)Math.ceil(cy + radius + 1.0));

      for (int py = minY; py <= maxY; py++) {
         for (int px = minX; px <= maxX; px++) {
            double dist = Math.hypot(px + 0.5 - cx, py + 0.5 - cy);
            if (dist <= radius) {
               image.setPixel(px, py, color);
            } else if (dist <= radius + 0.8 && image.getPixel(px, py) >>> 24 == 0) {
               image.setPixel(px, py, rim);
            }
         }
      }
   }

   public static void paintCape(NativeImage image, int color, int[] drawn) {
      int solid = 0xFF000000 | color & 16777215;

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            image.setPixel(x, y, solid);
         }
      }

      fill(image, 1, 0, 10, 1, shade(solid, 0.82F));
      fill(image, 11, 0, 10, 1, shade(solid, 0.7F));
      fill(image, 0, 1, 1, 16, shade(solid, 0.76F));
      fill(image, 11, 1, 1, 16, shade(solid, 0.76F));
      fill(image, 12, 1, 10, 16, shade(solid, 0.88F));
      if (drawn != null) {
         for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 10; x++) {
               int argb = drawn[y * 10 + x];
               if (argb >>> 24 != 0) {
                  image.setPixel(1 + x, 1 + y, 0xFF000000 | argb & 16777215);
               }
            }
         }
      }
   }

   private static void fill(NativeImage image, int x, int y, int w, int h, int color) {
      for (int py = y; py < y + h; py++) {
         for (int px = x; px < x + w; px++) {
            image.setPixel(px, py, color);
         }
      }
   }

   public static int lerp(int from, int to, double t) {
      double k = Math.max(0.0, Math.min(1.0, t));
      int r = (int)Math.round((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * k);
      int g = (int)Math.round((from >> 8 & 0xFF) + ((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * k);
      int b = (int)Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * k);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   public static int shade(int color, float factor) {
      int r = Math.max(0, Math.min(255, Math.round((color >> 16 & 0xFF) * factor)));
      int g = Math.max(0, Math.min(255, Math.round((color >> 8 & 0xFF) * factor)));
      int b = Math.max(0, Math.min(255, Math.round((color & 0xFF) * factor)));
      return 0xFF000000 | r << 16 | g << 8 | b;
   }
}
