package dev.lyfw.lyfwclient.render;

import java.util.function.IntBinaryOperator;

final class WingsElements {
   private WingsElements() {
   }

   static void tint(WingCanvas c, IntBinaryOperator pattern) {
      c.each((x, y, p) -> {
         double light = Math.max(p >> 16 & 0xFF, Math.max(p >> 8 & 0xFF, p & 0xFF)) / 255.0;
         int col = pattern.applyAsInt(x, y);
         int a = (int)((p >>> 24) * ((col >>> 24) / 255.0));
         return a << 24 | AnimatedCapes.shade(col, light) & 16777215;
      });
   }

   static void soulFire(WingCanvas c, int base, int tip) {
      for (int i = 0; i < 10; i++) {
         int index = i;
         double t = 0.05 + 0.9 * i / 9.0;
         double len = 14.0 + 20.0 * Math.sin(Math.PI * Math.min(1.0, t * 1.05));
         MoreWings.feather(c, MoreWings.boneX(t), MoreWings.boneY(t), Math.toRadians(-4.0 + 30.0 * t), len, 3.6, (along, across) -> {
            double edge = 0.62 + 0.32 * Math.sin(along * 13.0 + index * 1.7);
            if (Math.abs(across) > edge) {
               return 0;
            } else {
               double core = (1.0 - Math.abs(across) / edge) * (1.0 - along);
               int col = AnimatedCapes.lerp(AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.45), along), tip, core * core);
               return AnimatedCapes.alpha(col, AnimatedCapes.clamp(0.9 - along * 0.6 + core * 0.3, 0.0, 1.0));
            }
         });
      }

      for (double t = 0.0; t <= 1.0; t += 0.01) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t), 1.6 - 0.8 * t, AnimatedCapes.alpha(tip, 0.85));
      }
   }

   static void voidWing(WingCanvas c, int base, int tip) {
      MoreWings.membrane(c, MoreWings.DRAGON_TIPS, 22.0, 3.0, 27.0, 0.3, (xx, yx) -> {
         double d = Math.hypot(xx - 26, yx - 16);
         double a = Math.atan2(yx - 16, xx - 26);
         double swirl = 0.5 + 0.5 * Math.sin(a * 3.0 + d * 0.35);
         return AnimatedCapes.alpha(AnimatedCapes.lerp(base, tip, AnimatedCapes.clamp(swirl * d / 28.0, 0.0, 1.0) * 0.85), 0.95);
      }, AnimatedCapes.lerp(tip, -16777216, 0.35), false);

      for (int k = 0; k < 16; k++) {
         double x = 6.0 + AnimatedCapes.hash(k, 1) * 46.0;
         double y = 3.0 + AnimatedCapes.hash(k, 2) * 32.0;
         if (c.filled((int)x, (int)y)) {
            double dx = 26.0 - x;
            double dy = 16.0 - y;
            double l = Math.max(1.0, Math.hypot(dx, dy));
            MoreWings.lineOnShape(c, x, y, x + dx / l * 3.0, y + dy / l * 3.0, 0.5, AnimatedCapes.alpha(-1517313, 0.5));
            c.set((int)x, (int)y, -1);
         }
      }

      c.each((xx, yx, p) -> {
         boolean edge = !c.filled(xx + 1, yx) || !c.filled(xx - 1, yx) || !c.filled(xx, yx + 1) || !c.filled(xx, yx - 1);
         return edge ? AnimatedCapes.lerp(p, tip, 0.7) | 0xFF000000 : p;
      });
   }

   static void starlight(WingCanvas c, int base, int tip) {
      MoreWings.silhouette(c, 1.0, 1.0);
      c.each(
         (x, y, p) -> AnimatedCapes.alpha(
            AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 2.2F), y / 60.0 + AnimatedCapes.fbm(x * 0.1, y * 0.1, 2) * 0.3), 0.5
         )
      );
      c.rim(3.0);
      double[][] stars = new double[][]{
         {3.0, 10.0}, {14.0, 7.0}, {26.0, 5.0}, {38.0, 3.5}, {51.0, 2.5}, {45.0, 16.0}, {41.0, 28.0}, {33.0, 24.0}, {24.0, 20.0}, {13.0, 22.0}, {50.0, 30.0}
      };
      int[][] links = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {3, 5}, {5, 6}, {5, 7}, {7, 8}, {8, 9}, {6, 10}};

      for (int[] link : links) {
         c.line(stars[link[0]][0], stars[link[0]][1], stars[link[1]][0], stars[link[1]][1], 0.5, AnimatedCapes.alpha(tip, 0.55));
      }

      for (int k = 0; k < stars.length; k++) {
         double[] s = stars[k];
         c.disc(s[0], s[1], k % 3 == 0 ? 1.3 : 0.9, tip);
         c.line(s[0] - 2.2, s[1], s[0] + 2.2, s[1], 0.4, AnimatedCapes.alpha(tip, 0.6));
         c.line(s[0], s[1] - 2.2, s[0], s[1] + 2.2, 0.4, AnimatedCapes.alpha(tip, 0.6));
      }

      for (int k = 0; k < 30; k++) {
         int x = (int)(AnimatedCapes.hash(k, 9) * 56.0);
         int y = (int)(AnimatedCapes.hash(k, 10) * 40.0);
         if (c.filled(x, y)) {
            c.over(x, y, AnimatedCapes.alpha(-1, 0.5));
         }
      }
   }

   static void sunburst(WingCanvas c, int base, int tip) {
      for (int k = 12; k >= 0; k--) {
         double a = Math.toRadians(-38.0 + k * 8.5);
         double len = (k % 2 == 0 ? 52 : 36) * (1.0 - Math.abs(k - 5) * 0.03);
         double ux = Math.cos(a);
         double uy = Math.sin(a);
         double rx = 2.0;
         double ry = 12.0;
         double half = k % 2 == 0 ? 2.6 : 1.8;
         c.polygon(new double[][]{{rx - uy * half, ry + ux * half}, {rx + ux * len, ry + uy * len}, {rx + uy * half, ry - ux * half}}, (x, y) -> {
            double along = Math.hypot(x - rx, y - ry) / len;
            return AnimatedCapes.alpha(AnimatedCapes.lerp(base, tip, along), AnimatedCapes.clamp(1.05 - along * 0.7, 0.0, 1.0));
         });
      }

      c.disc(3.0, 12.0, 6.0, AnimatedCapes.alpha(-16, 0.9));
      c.disc(3.0, 12.0, 9.0, AnimatedCapes.alpha(base, 0.4));
   }

   static void aurora(WingCanvas c, int base, int tip) {
      MoreWings.silhouette(c, 1.05, 1.0);
      c.each((x, y, p) -> {
         double streak = 0.5 + 0.5 * Math.sin(x * 1.1 + AnimatedCapes.fbm(x * 0.08, y * 0.05, 3) * 7.0);
         double height = AnimatedCapes.clamp(1.0 - y / 38.0, 0.0, 1.0);
         int col = AnimatedCapes.lerp(base, tip, height * 0.9 + AnimatedCapes.fbm(x * 0.05 + 3.0, 0.0, 2) * 0.2);
         return AnimatedCapes.alpha(AnimatedCapes.lerp(col, -1, streak * 0.25), 0.3 + 0.55 * streak * (0.4 + 0.6 * (1.0 - height * 0.5)));
      });
   }

   static void stormCloud(WingCanvas c, int base, int tip) {
      double[][] rows = new double[][]{{0.0, 1.0, 0.0, 1.0}, {0.0, 1.0, 5.5, 0.9}, {0.15, 0.95, 11.0, 0.8}, {0.4, 0.9, 16.5, 0.72}, {0.55, 0.85, 22.0, 0.62}};

      for (int r = rows.length - 1; r >= 0; r--) {
         double[] row = rows[r];

         for (double t = row[0]; t <= row[1]; t += 0.07) {
            double radius = (3.4 + 1.8 * Math.sin(Math.PI * t)) * row[3] * (0.8 + 0.4 * AnimatedCapes.hash((int)(t * 100.0), r));
            double x = MoreWings.boneX(t);
            double y = MoreWings.boneY(t) + 3.0 + row[2];
            c.disc(x, y + 0.8, radius, AnimatedCapes.shade(base, 0.62F + r * 0.02F));
            c.disc(x - radius * 0.2, y - radius * 0.2, radius * 0.8, AnimatedCapes.lerp(AnimatedCapes.shade(base, 0.9F), -1, 0.25 - r * 0.04));
         }
      }

      double x = 20.0;
      double y = 12.0;

      for (int s = 0; s < 5; s++) {
         double nx = x + 5.0 + s % 2 * 2;
         double ny = y + (s % 2 == 0 ? 4.0 : -1.5);
         MoreWings.lineOnShape(c, x, y, nx, ny, 1.4, tip);
         x = nx;
         y = ny;
      }

      for (int k = 0; k < 14; k++) {
         double rx = 18.0 + k * 2.6 + AnimatedCapes.hash(k, 3) * 1.5;
         double ry = 30.0 + AnimatedCapes.hash(k, 4) * 6.0 - (rx - 18.0) * 0.2;
         c.line(rx, ry, rx - 0.8, ry + 3.0, 0.6, -6637344);
      }

      c.rim(0.7);
   }

   static void sand(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> MoreWings.plume(-1, along, across), -986896, 1.0, 1.0);
      tint(c, (xx, yx) -> {
         double band = 0.5 + 0.5 * Math.sin(yx * 0.85 + AnimatedCapes.fbm(xx * 0.1, yx * 0.2, 3) * 4.0);
         int col = AnimatedCapes.lerp(base, tip, band);
         return AnimatedCapes.hash(xx, yx) > 0.82 ? AnimatedCapes.shade(col, 0.9F) : col;
      });

      for (int k = 0; k < 40; k++) {
         int x = (int)(8.0 + AnimatedCapes.hash(k, 5) * 46.0);
         int y = (int)(8.0 + AnimatedCapes.hash(k, 6) * 32.0);
         if (c.filled(x, y) && (!c.filled(x, y + 1) || !c.filled(x + 1, y))) {
            c.set(x, y, 0);
         }
      }

      c.rim(0.72);
   }

   static void wind(WingCanvas c, int base, int tip) {
      for (int k = 0; k < 11; k++) {
         double t = k / 10.0;
         double sx = MoreWings.boneX(t * 0.9);
         double sy = MoreWings.boneY(t * 0.9) + 1.0;
         double reach = 16.0 + 16.0 * Math.sin(Math.PI * Math.min(1.0, t * 1.1));
         double px = sx;
         double py = sy;

         for (int s = 1; s <= 16; s++) {
            double f = s / 16.0;
            double nx = sx + f * reach * 0.5;
            double ny = sy + f * reach + Math.sin(f * 5.0 + k) * 2.0;
            c.line(px, py, nx, ny, 1.8 * (1.0 - f * 0.7), AnimatedCapes.alpha(AnimatedCapes.lerp(base, tip, f), 0.75 * (1.0 - f * 0.6)));
            px = nx;
            py = ny;
         }

         for (int s = 0; s < 10; s++) {
            double a = s * 0.6 + k;
            double r = 2.5 - s * 0.2;
            c.disc(px + Math.cos(a) * r, py + Math.sin(a) * r, 0.5, AnimatedCapes.alpha(tip, 0.6 * (1.0 - s / 10.0)));
         }
      }

      for (double t = 0.0; t <= 1.0; t += 0.01) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t), 1.2 - 0.6 * t, AnimatedCapes.alpha(-1, 0.8));
      }
   }

   static void earth(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> across > 0.55 ? -5197648 : -986896, -2039584, 1.0, 1.05);
      tint(c, (xx, yx) -> {
         int col = AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.7F), AnimatedCapes.fbm(xx * 0.2, yx * 0.2, 3));
         double moss = AnimatedCapes.fbm(xx * 0.15 + 9.0, yx * 0.2, 3);
         return moss > 0.58 && yx < 22 ? AnimatedCapes.lerp(col, tip, 0.8) : col;
      });

      for (int k = 0; k < 8; k++) {
         double x = 8.0 + AnimatedCapes.hash(k, 1) * 42.0;
         double y = 8.0 + AnimatedCapes.hash(k, 2) * 24.0;
         MoreWings.lineOnShape(c, x, y, x + (AnimatedCapes.hash(k, 3) - 0.5) * 8.0, y + 4.0 + AnimatedCapes.hash(k, 4) * 4.0, 0.6, -14015456);
      }

      for (int k = 0; k < 5; k++) {
         double x = 10.0 + AnimatedCapes.hash(k, 7) * 38.0;
         double y = 12.0 + AnimatedCapes.hash(k, 8) * 18.0;
         if (c.filled((int)x, (int)y)) {
            c.polygon(new double[][]{{x, y - 2.0}, {x + 1.2, y}, {x, y + 2.0}, {x - 1.2, y}}, -5213984);
            c.set((int)x, (int)y - 1, -995073);
         }
      }

      c.rim(0.55);
   }

   static void coral(WingCanvas c, int base, int tip) {
      branch(c, 2.0, 10.0, -0.2, 17.0, 5, base, tip);
      branch(c, 2.0, 11.0, 0.55, 14.0, 5, base, tip);
      branch(c, 2.0, 12.0, 1.1, 10.0, 4, base, tip);
      c.rim(0.7);
   }

   private static void branch(WingCanvas c, double x, double y, double angle, double length, int depth, int base, int tip) {
      double nx = x + Math.cos(angle) * length;
      double ny = y + Math.sin(angle) * length;
      int col = AnimatedCapes.lerp(tip, base, depth / 5.0);
      c.line(x, y, nx, ny, depth * 0.8 + 0.8, col);
      if (depth == 0) {
         c.disc(nx, ny, 1.2, AnimatedCapes.lerp(base, -1, 0.5));
      } else {
         for (int k = 0; k < 3; k++) {
            c.disc(x + (nx - x) * (0.3 + k * 0.25), y + (ny - y) * (0.3 + k * 0.25), 0.5, AnimatedCapes.lerp(col, -1, 0.4));
         }

         branch(c, nx, ny, angle - 0.42, length * 0.72, depth - 1, base, tip);
         branch(c, nx, ny, angle + 0.38, length * 0.7, depth - 1, base, tip);
      }
   }

   static void seashell(WingCanvas c, int base, int tip) {
      double hx = 2.0;
      double hy = 17.0;

      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double dx = x + 0.5 - hx;
            double dy = y + 0.5 - hy;
            double r = Math.hypot(dx, dy);
            double a = Math.atan2(dy, dx);
            if (!(Math.abs(a) > 1.05)) {
               double reach = 44.0 * (0.82 + 0.18 * Math.cos(a * 1.6)) - Math.abs(Math.sin(a * 13.0)) * 2.2;
               if (!(r > reach)) {
                  double rib = 0.5 + 0.5 * Math.cos(a * 26.0);
                  int col = (int)(r / 5.0) % 2 == 0 ? base : AnimatedCapes.lerp(base, tip, 0.65);
                  col = AnimatedCapes.shade(col, 0.78 + rib * 0.3);
                  c.set(x, y, col);
               }
            }
         }
      }

      c.polygon(new double[][]{{0.0, 12.0}, {7.0, 13.0}, {7.0, 21.0}, {0.0, 22.0}}, AnimatedCapes.lerp(base, tip, 0.4));
      c.rim(0.7);
   }

   static void fern(WingCanvas c, int base, int tip) {
      double[][] fronds = new double[][]{{-0.3, 50.0}, {0.3, 42.0}, {0.9, 32.0}, {1.35, 22.0}};

      for (int f = 0; f < fronds.length; f++) {
         double angle = fronds[f][0];
         double len = fronds[f][1];
         double px = 2.0;
         double py = 10 + f * 2;

         for (int s = 1; s <= 24; s++) {
            double t = s / 24.0;
            double a = angle + t * 0.35;
            double nx = px + Math.cos(a) * len / 24.0;
            double ny = py + Math.sin(a) * len / 24.0;
            c.line(px, py, nx, ny, 1.1 - t * 0.6, AnimatedCapes.shade(tip, 0.8F));
            double leaf = 7.0 * (1.0 - t * 0.8);

            for (int side = -1; side <= 1; side += 2) {
               double la = a + side * 1.1 - side * 0.25;
               c.ellipse(
                  nx + Math.cos(la) * leaf * 0.55,
                  ny + Math.sin(la) * leaf * 0.55,
                  leaf * 0.6,
                  Math.max(0.7, leaf * 0.3),
                  la,
                  AnimatedCapes.lerp(base, tip, t * 0.6 + (side > 0 ? 0.15 : 0.0))
               );
            }

            px = nx;
            py = ny;
         }
      }

      c.rim(0.72);
   }

   static void lotus(WingCanvas c, int base, int tip) {
      double[][] petals = new double[][]{
         {-0.55, 48.0, 5.5},
         {-0.15, 50.0, 6.0},
         {0.25, 46.0, 6.0},
         {0.65, 38.0, 5.5},
         {1.05, 28.0, 5.0},
         {-0.35, 34.0, 4.8},
         {0.05, 36.0, 5.0},
         {0.45, 32.0, 5.0},
         {0.85, 24.0, 4.4}
      };

      for (double[] petal : petals) {
         double a = petal[0];
         double len = petal[1];
         double w = petal[2];
         double ux = Math.cos(a);
         double uy = Math.sin(a);
         double[][] outline = new double[26][];

         for (int i = 0; i <= 12; i++) {
            double s = i / 12.0;
            double width = Math.pow(Math.sin(Math.PI * s), 0.8) * w * (s < 0.5 ? 1.0 : 1.0 - (s - 0.5) * 0.4);
            outline[i] = new double[]{2.0 + ux * len * s - uy * width, 11.0 + uy * len * s + ux * width};
            outline[25 - i] = new double[]{2.0 + ux * len * s + uy * width, 11.0 + uy * len * s - ux * width};
         }

         c.polygon(outline, (x, y) -> {
            double along = AnimatedCapes.clamp(((x - 2) * ux + (y - 11) * uy) / len, 0.0, 1.0);
            double across = ((x - 2) * -uy + (y - 11) * ux) / w;
            int col = AnimatedCapes.lerp(base, tip, Math.pow(along, 1.6));
            return AnimatedCapes.shade(col, 1.0 - Math.abs(across) * 0.15 + (Math.abs(Math.sin(across * 4.0)) < 0.2 ? 0.06 : 0.0));
         });
      }

      c.rim(0.8);
   }

   static void cherryBlossom(WingCanvas c, int base, int tip) {
      double[][] limbs = new double[][]{
         {2.0, 10.0, 52.0, 3.0}, {18.0, 7.0, 30.0, 22.0}, {32.0, 5.0, 44.0, 26.0}, {10.0, 9.0, 16.0, 26.0}, {40.0, 4.0, 53.0, 16.0}, {26.0, 16.0, 22.0, 34.0}
      };

      for (double[] l : limbs) {
         c.line(l[0], l[1], l[2], l[3], 1.6, tip);
      }

      for (int k = 0; k < 64; k++) {
         double[] l = limbs[k % limbs.length];
         double f = AnimatedCapes.hash(k, 1);
         double x = l[0] + (l[2] - l[0]) * f + (AnimatedCapes.hash(k, 2) - 0.5) * 7.0;
         double y = l[1] + (l[3] - l[1]) * f + (AnimatedCapes.hash(k, 3) - 0.5) * 7.0;
         int col = AnimatedCapes.lerp(base, -1, AnimatedCapes.hash(k, 4) * 0.5);

         for (int p = 0; p < 5; p++) {
            double a = p * Math.PI * 2.0 / 5.0 + k;
            c.disc(x + Math.cos(a) * 1.2, y + Math.sin(a) * 1.2, 1.1, col);
         }

         c.disc(x, y, 0.6, -2076566);
      }

      c.rim(0.72);
   }

   static void maple(WingCanvas c, int base, int tip) {
      double[][] leaves = new double[][]{
         {-0.35, 44.0, 11.0}, {0.0, 46.0, 11.5}, {0.35, 42.0, 11.0}, {0.7, 33.0, 9.5}, {1.05, 22.0, 8.0}, {-0.1, 24.0, 8.0}, {0.45, 22.0, 7.5}
      };

      for (int k = 0; k < leaves.length; k++) {
         double a = leaves[k][0];
         double dist = leaves[k][1];
         double size = leaves[k][2];
         double cx = 2.0 + Math.cos(a) * (dist - size);
         double cy = 10.0 + Math.sin(a) * (dist - size);
         c.line(2.0, 10.0, cx, cy, 0.5, -9815526);
         double[][] star = new double[10][];

         for (int p = 0; p < 10; p++) {
            double pa = a - (Math.PI / 2) + p * Math.PI / 5.0;
            double r = p % 2 == 0 ? size : size * 0.45;
            star[p] = new double[]{cx + Math.cos(pa + (Math.PI / 2)) * r, cy + Math.sin(pa + (Math.PI / 2)) * r};
         }

         int col = AnimatedCapes.lerp(base, tip, (double)k / (leaves.length - 1));
         c.polygon(star, (x, y) -> AnimatedCapes.shade(col, 0.85 + 0.25 * (1.0 - Math.hypot(x - cx, y - cy) / size)));

         for (int p = 0; p < 10; p += 2) {
            MoreWings.lineOnShape(c, cx, cy, star[p][0], star[p][1], 0.4, AnimatedCapes.shade(col, 0.7F));
         }
      }

      c.rim(0.7);
   }
}
