package dev.lyfw.lyfwclient.render;

final class CapeScenesH {
   private CapeScenesH() {
   }

   static final class Cauldron implements AnimatedCapes.Scene {
      private static final double[][] JARS = new double[][]{
         {8.0, 30.0, 0.0}, {18.0, 30.0, 120.0}, {28.0, 31.0, 280.0}, {58.0, 30.0, 60.0}, {68.0, 29.0, 200.0}, {12.0, 56.0, 320.0}, {64.0, 56.0, 160.0}
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            int row = y / 8;

            for (int x = 0; x < 80; x++) {
               boolean mortar = y % 8 == 0 || (x + row % 2 * 7) % 14 == 0;
               int stone = AnimatedCapes.lerp(
                  -14015456, -12700114, AnimatedCapes.hash((x + row % 2 * 7) / 14, row) * 0.6 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.4
               );
               c.set(x, y, AnimatedCapes.dither(mortar ? -15462388 : stone, x, y));
            }
         }

         c.vignette(0.5);

         for (int s = 0; s < 2; s++) {
            double sy = 36 + s * 26;
            c.rect(0.0, sy, 80.0, 2.0, -11915750);
            c.rect(0.0, sy + 2.0, 80.0, 1.0, -14018034);
         }

         for (double[] jar : JARS) {
            int col = AnimatedCapes.hsv(jar[2], 0.7, 0.8);
            c.rect(jar[0] - 3.0, jar[1] - 1.0, 6.0, 7.0, AnimatedCapes.alpha(col, 0.85));
            c.rect(jar[0] - 2.0, jar[1] - 3.0, 4.0, 2.0, -7706038);
            c.rect(jar[0] - 2.5, jar[1], 1.0, 5.0, AnimatedCapes.alpha(-1, 0.3));
         }

         for (int h = 0; h < 5; h++) {
            double hx = 30 + h * 5;
            c.line(hx, 0.0, hx, 8 + h % 2 * 3, 0.4, -11912662);

            for (int l = 0; l < 4; l++) {
               c.ellipse(hx + (l % 2 == 0 ? -1.2 : 1.2), 9 + h % 2 * 3 + l * 1.5, 1.4, 0.6, l % 2 == 0 ? 0.6 : -0.6, -11900374);
            }
         }

         c.rect(0.0, 112.0, 80.0, 16.0, -12965348);

         for (int x = 0; x < 80; x += 9) {
            c.rect(x, 112.0, 0.6, 16.0, -14017518);
         }

         c.ellipse(40.0, 118.0, 18.0, 3.0, 0.0, -14013910);

         for (int k = 0; k < 6; k++) {
            c.ellipse(26.0 + k * 5.5, 117.0, 2.6, 1.6, 0.0, AnimatedCapes.lerp(-11908536, -9803162, AnimatedCapes.hash(k, 50)));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < JARS.length; k++) {
            c.glow(JARS[k][0], JARS[k][1] + 2.0, 6.0, AnimatedCapes.hsv(JARS[k][2], 0.6, 1.0), 0.25 + 0.1 * Math.sin(t * 1.5 + k));
         }

         c.glow(40.0, 112.0, 26.0, -30160, 0.35 + 0.1 * AnimatedCapes.noise(t * 5.0, 1.0));

         for (int f = 0; f < 7; f++) {
            double fx = 28 + f * 4;
            double h = 5.0 + AnimatedCapes.noise(t * 6.0 + f, f) * 6.0;
            c.polygon(new double[][]{{fx - 2.0, 116.0}, {fx + 2.0, 116.0}, {fx + Math.sin(t * 9.0 + f), 116.0 - h}}, f % 2 == 0 ? -34278 : -16320);
         }

         double hue = AnimatedCapes.wrap(t * 20.0, 360.0);
         int brew = AnimatedCapes.hsv(hue, 0.8, 0.9);
         c.polygon(
            new double[][]{{20.0, 84.0}, {60.0, 84.0}, {58.0, 102.0}, {50.0, 110.0}, {30.0, 110.0}, {22.0, 102.0}},
            (xx, y) -> AnimatedCapes.lerp(-14013904, -16119282, AnimatedCapes.clamp((xx - 20) / 40.0 + (y - 84) / 60.0, 0.0, 1.0))
         );
         c.ellipse(28.0, 92.0, 4.0, 6.0, 0.3, AnimatedCapes.alpha(-9803142, 0.4));
         c.rect(24.0, 108.0, 3.0, 6.0, -15461350);
         c.rect(53.0, 108.0, 3.0, 6.0, -15461350);
         c.ellipse(40.0, 84.0, 20.0, 4.2, 0.0, -12961212);
         c.ellipse(40.0, 84.5, 17.5, 3.0, 0.0, brew);
         c.glow(40.0, 82.0, 22.0, brew, 0.4);

         for (int b = 0; b < 8; b++) {
            double life = AnimatedCapes.wrap(t * (0.8 + AnimatedCapes.hash(b, 1) * 0.6) + AnimatedCapes.hash(b, 2), 1.0);
            double bx = 26.0 + AnimatedCapes.hash(b, 3 + (int)(t * 0.8 + AnimatedCapes.hash(b, 2))) * 28.0;
            double r = life * 2.6;
            if (life < 0.85) {
               c.disc(bx, 84.5, r, AnimatedCapes.lerp(brew, -1, 0.3));
               c.add((int)(bx - r * 0.4), (int)(84.5 - r * 0.4), -1, 0.6);
            } else {
               c.ring(bx, 84.5, r + (life - 0.85) * 12.0, 0.4, AnimatedCapes.alpha(-1, 0.6));
            }
         }

         for (int y = 0; y < 84; y++) {
            for (int x = 10; x < 70; x++) {
               double rise = 84 - y;
               double cx = 40.0 + Math.sin(y * 0.08 + t * 0.8) * rise * 0.12;
               double spread = 10.0 + rise * 0.25;
               double dx = Math.abs(x - cx) / spread;
               if (dx < 1.0) {
                  double n = AnimatedCapes.fbm(x * 0.1, y * 0.08 + t * 0.8, 3);
                  c.blend(
                     x,
                     y,
                     AnimatedCapes.alpha(
                        AnimatedCapes.lerp(brew, -1515280, rise / 84.0), AnimatedCapes.smoothstep(0.45, 0.75, n) * (1.0 - dx) * (1.0 - rise / 90.0) * 0.7
                     )
                  );
               }
            }
         }

         double drop = AnimatedCapes.wrap(t, 5.0);
         if (drop < 1.2) {
            double p = drop / 1.2;
            if (p < 0.8) {
               double y = 20.0 + p / 0.8 * 64.0;
               c.polygon(new double[][]{{44.0, y - 2.0}, {47.0, y}, {44.0, y + 2.0}, {41.0, y}}, -5227958);
               c.rect(43.5, y - 3.0, 1.0, 1.5, -11900374);
            } else {
               double s = (p - 0.8) / 0.2;

               for (int d = 0; d < 6; d++) {
                  double a = (-Math.PI / 2) + (d - 2.5) * 0.4;
                  c.disc(44.0 + Math.cos(a) * s * 8.0, 84.0 + Math.sin(a) * s * 8.0, 0.8, brew);
               }
            }
         }
      }
   }

   static final class CrystalBall implements AnimatedCapes.Scene {
      private static final double BX = 40.0;
      private static final double BY = 66.0;
      private static final double BR = 20.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double fold = Math.sin(x * 0.35 + Math.sin(y * 0.05) * 2.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-14022096, -11920816, 0.5 + 0.5 * fold), x, y));
            }
         }

         c.vignette(0.6);
         c.polygon(
            new double[][]{{-2.0, 96.0}, {82.0, 96.0}, {82.0, 130.0}, {-2.0, 130.0}}, (x, y) -> AnimatedCapes.lerp(-15070662, -16120808, (y - 96) / 34.0)
         );

         for (int x = 0; x < 80; x += 2) {
            c.line(x, 96.0, x, 99.0 + x % 4 * 0.5, 0.6, -2578368);
         }

         c.rect(0.0, 95.5, 80.0, 1.0, -1525680);
         c.polygon(new double[][]{{30.0, 98.0}, {50.0, 98.0}, {46.0, 88.0}, {34.0, 88.0}}, (x, y) -> AnimatedCapes.lerp(-2052032, -7708656, (x - 30) / 20.0));
         c.ellipse(40.0, 88.0, 7.0, 1.8, 0.0, -997280);

         for (int k = 0; k < 2; k++) {
            double cx = k == 0 ? 10.0 : 70.0;
            c.rect(cx - 2.5, 76.0, 5.0, 20.0, -988972);
            c.rect(cx - 2.5, 76.0, 1.5, 20.0, -16);
            c.ellipse(cx, 96.0, 5.0, 1.5, 0.0, -5207494);
            c.line(cx, 76.0, cx, 74.0, 0.4, -15066598);
         }

         double[][] cards = new double[][]{{20.0, 108.0, -0.3}, {58.0, 110.0, 0.25}};

         for (double[] card : cards) {
            double ccos = Math.cos(card[2]);
            double csin = Math.sin(card[2]);
            c.polygon(
               new double[][]{
                  {card[0] - 4.0 * ccos + 6.0 * csin, card[1] - 4.0 * csin - 6.0 * ccos},
                  {card[0] + 4.0 * ccos + 6.0 * csin, card[1] + 4.0 * csin - 6.0 * ccos},
                  {card[0] + 4.0 * ccos - 6.0 * csin, card[1] + 4.0 * csin + 6.0 * ccos},
                  {card[0] - 4.0 * ccos - 6.0 * csin, card[1] - 4.0 * csin + 6.0 * ccos}
               },
               -990008
            );
            c.disc(card[0], card[1], 2.0, -12965238);
            c.disc(card[0] + 0.7, card[1] - 0.5, 1.6, -990008);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 2; k++) {
            double cx = k == 0 ? 10.0 : 70.0;
            double flicker = AnimatedCapes.noise(t * 7.0 + k * 9, k);
            c.glow(cx, 70.0, 22.0, -24512, 0.25 + 0.1 * flicker);
            c.polygon(new double[][]{{cx - 1.3, 74.0}, {cx + 1.3, 74.0}, {cx + Math.sin(t * 5.0 + k), 67.0 - flicker * 2.0}}, -20432);
            c.ellipse(cx, 72.0, 0.6, 1.3, 0.0, -2864);
         }

         c.glow(40.0, 66.0, 36.0, -6659841, 0.35 + 0.1 * Math.sin(t));
         int vision = (int)Math.floor(t / 5.0) % 3;
         double show = Math.sin(AnimatedCapes.wrap(t, 5.0) / 5.0 * Math.PI);

         for (int y = 46; y <= 86.0; y++) {
            for (int x = 20; x <= 60.0; x++) {
               double dx = x + 0.5 - 40.0;
               double dy = y + 0.5 - 66.0;
               double d = Math.hypot(dx, dy) / 20.0;
               if (!(d > 1.0)) {
                  double a = Math.atan2(dy, dx) + t * 0.5 + d * 2.0;
                  double n = AnimatedCapes.fbm(Math.cos(a) * d * 3.0 + 7.0, Math.sin(a) * d * 3.0 + t * 0.1, 4);
                  int mist = AnimatedCapes.lerp(AnimatedCapes.lerp(-15070662, -7714080, n), -2045697, AnimatedCapes.smoothstep(0.6, 0.8, n));
                  double shape = 0.0;
                  if (vision == 0) {
                     shape = AnimatedCapes.clamp(8.0 - Math.hypot(dx, dy), 0.0, 1.0)
                        * (1.0 - AnimatedCapes.clamp(7.5 - Math.hypot(dx + 4.0, dy - 2.0), 0.0, 1.0));
                  } else if (vision == 1) {
                     double eye = Math.pow(dx / 10.0, 2.0) + Math.pow(dy / 5.0, 2.0);
                     shape = eye < 1.0 ? (Math.hypot(dx, dy) < 3.0 ? 0.2 : 1.0) : 0.0;
                  } else {
                     double ang = Math.atan2(dy, dx);
                     double star = 5.0 + 3.0 * Math.cos(ang * 5.0);
                     shape = Math.hypot(dx, dy) < star ? 1.0 : 0.0;
                  }

                  mist = AnimatedCapes.lerp(mist, -2856, shape * show * 0.8);
                  c.set(x, y, AnimatedCapes.lerp(c.get(x, y), mist, AnimatedCapes.clamp((1.0 - d) * 8.0, 0.0, 1.0) * 0.92));
               }
            }
         }

         c.ring(40.0, 66.0, 19.7, 0.8, AnimatedCapes.alpha(-2043649, 0.6));
         c.ellipse(32.0, 56.0, 6.0, 3.0, -0.7, AnimatedCapes.alpha(-1, 0.45));
         c.disc(50.0, 72.0, 1.0, AnimatedCapes.alpha(-8032, 0.7));
         c.disc(28.0, 71.0, 0.8, AnimatedCapes.alpha(-8032, 0.6));

         for (int i = 0; i < 12; i++) {
            double life = AnimatedCapes.wrap(t * 0.4 + AnimatedCapes.hash(i, 10), 1.0);
            double a = AnimatedCapes.hash(i, 11) * Math.PI * 2.0 + life;
            double r = 22.0 + life * 10.0;
            c.star(40.0 + Math.cos(a) * r, 66.0 + Math.sin(a) * r, 1.0, -2045697, Math.sin(life * Math.PI) * 0.7);
         }
      }
   }

   static final class DiamondMine implements AnimatedCapes.Scene {
      private static final int B = 10;

      private static int ore(int bx, int by) {
         double h = AnimatedCapes.hash(bx + 50, by + 50);
         return h > 0.93 ? 1 : (h > 0.87 ? 2 : (h > 0.82 ? 3 : (h > 0.78 ? 4 : 0)));
      }

      private static void block(AnimatedCapes.Canvas c, int bx, int by, int kind) {
         for (int py = 0; py < 10; py++) {
            for (int px = 0; px < 10; px++) {
               int stone = AnimatedCapes.lerp(-9013636, -7566190, AnimatedCapes.hash(bx * 11 + px / 3, by * 13 + py / 3));
               if (AnimatedCapes.hash(bx * 7 + px / 2, by * 5 + py / 2) > 0.93) {
                  stone = -10592668;
               }

               if (px == 0 || py == 0) {
                  stone = AnimatedCapes.shade(stone, 1.12);
               } else if (px == 9 || py == 9) {
                  stone = AnimatedCapes.shade(stone, 0.72);
               }

               int col = stone;
               int[] ores = new int[]{0, -11867936, -997312, -2086358, -12922774};
               if (kind != 0 && AnimatedCapes.hash(bx * 3 + px / 2 * 5, by * 7 + py / 2 * 3) > 0.55 && px > 1 && px < 8 && py > 1 && py < 8) {
                  col = (px + py) % 3 == 0 ? AnimatedCapes.lerp(ores[kind], -1, 0.4) : ores[kind];
               }

               c.set(bx * 10 + px, by * 10 + py - 2, col);
            }
         }
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int by = 0; by <= 12; by++) {
            for (int bx = 0; bx < 8; bx++) {
               block(c, bx, by, ore(bx, by));
            }
         }

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double open = Math.exp(-Math.pow((x - 40) / 30.0, 2.0) - Math.pow((y - 80) / 40.0, 2.0));
               c.px[y * 80 + x] = AnimatedCapes.shade(c.px[y * 80 + x], 0.5 + open * 0.5);
            }
         }

         for (int y = 108; y < 118; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.shade(-10855842, 0.5));
            }
         }

         for (int x = 0; x < 80; x += 5) {
            c.rect(x, 114.0, 3.0, 2.0, -9811414);
         }

         c.rect(0.0, 113.0, 80.0, 0.8, -6645088);
         c.rect(0.0, 116.0, 80.0, 0.8, -6645088);
         c.rect(12.0, 50.0, 2.0, 8.0, -9811414);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double flicker = AnimatedCapes.noise(t * 5.0, 2.0);
         c.glow(13.0, 48.0, 34.0, -24512, 0.35 + 0.1 * flicker);
         c.rect(12.0, 46.0, 2.0, 3.0, AnimatedCapes.lerp(-26080, -8080, flicker));
         double cycle = AnimatedCapes.wrap(t, 5.0);
         int tbx = 4;
         int tby = 8;
         double tx = tbx * 10;
         double ty = tby * 10 - 2;
         block(c, tbx, tby, 1);
         double progress = Math.min(1.0, cycle / 3.5);
         if (cycle < 3.5) {
            int stage = (int)(progress * 5.0);

            for (int s = 0; s < stage; s++) {
               double a = AnimatedCapes.hash(s, 7) * Math.PI * 2.0;
               c.line(tx + 5.0, ty + 5.0, tx + 5.0 + Math.cos(a) * (3 + s), ty + 5.0 + Math.sin(a) * (3 + s), 0.5, -15066594);
            }

            double swing = Math.pow(Math.max(0.0, Math.sin(t * 5.0)), 3.0);
            double pa = -1.2 + swing * 1.4;
            double hx = tx + 24.0;
            double hy = ty + 16.0;
            c.line(hx, hy, hx + Math.cos(pa + Math.PI) * 14.0, hy + Math.sin(pa + Math.PI) * 14.0, 1.4, -8760784);
            double headX = hx + Math.cos(pa + Math.PI) * 14.0;
            double headY = hy + Math.sin(pa + Math.PI) * 14.0;
            double perp = pa + (Math.PI / 2);
            c.polygon(
               new double[][]{
                  {headX + Math.cos(perp) * 6.0, headY + Math.sin(perp) * 6.0},
                  {headX, headY - 1.0},
                  {headX - Math.cos(perp) * 6.0, headY - Math.sin(perp) * 6.0},
                  {headX, headY + 1.5}
               },
               -9770784
            );
            if (swing > 0.95) {
               for (int p = 0; p < 6; p++) {
                  double a = AnimatedCapes.hash(p, (int)(t * 5.0)) * Math.PI * 2.0;
                  c.rect(tx + 5.0 + Math.cos(a) * 5.0, ty + 5.0 + Math.sin(a) * 5.0, 1.0, 1.0, -7697778);
               }
            }
         } else {
            double p = (cycle - 3.5) / 1.5;
            c.rect(tx, ty, 10.0, 10.0, -15592940);

            for (int k = 0; k < 10; k++) {
               double a = k * Math.PI / 5.0;
               c.rect(
                  tx + 5.0 + Math.cos(a) * p * 12.0,
                  ty + 5.0 + Math.sin(a) * p * 12.0 + p * p * 10.0,
                  1.2,
                  1.2,
                  AnimatedCapes.alpha(k % 2 == 0 ? -7697778 : -11867936, 1.0 - p)
               );
            }

            double bob = Math.sin(t * 4.0) * 1.2;
            c.polygon(new double[][]{{tx + 5.0, ty + 1.0 + bob}, {tx + 9.0, ty + 5.0 + bob}, {tx + 5.0, ty + 9.0 + bob}, {tx + 1.0, ty + 5.0 + bob}}, -9768728);
            c.polygon(new double[][]{{tx + 5.0, ty + 2.5 + bob}, {tx + 7.0, ty + 4.5 + bob}, {tx + 5.0, ty + 5.0 + bob}}, -2031617);
            c.glow(tx + 5.0, ty + 5.0, 8.0, -9768728, 0.5);
         }

         for (int i = 0; i < 12; i++) {
            int tick = (int)Math.floor(t * 0.9 + AnimatedCapes.hash(i, 20) * 5.0);
            int bx = (int)(AnimatedCapes.hash(i, tick + 21) * 8.0);
            int by = (int)(AnimatedCapes.hash(i, tick + 22) * 12.0);
            if (ore(bx, by) != 0) {
               c.star(
                  bx * 10 + 2 + AnimatedCapes.hash(i, 23) * 6.0,
                  by * 10 + AnimatedCapes.hash(i, 24) * 6.0,
                  1.0,
                  -1,
                  Math.sin(AnimatedCapes.wrap(t * 0.9 + AnimatedCapes.hash(i, 20) * 5.0, 1.0) * Math.PI)
               );
            }
         }

         double cart = AnimatedCapes.wrap(t * 0.12, 1.0);
         double cx = -20.0 + cart * 130.0;
         c.rect(cx - 7.0, 104.0, 14.0, 8.0, -10855838);
         c.rect(cx - 6.0, 105.0, 12.0, 3.0, -14013906);
         c.rect(cx - 5.0, 103.0, 10.0, 2.0, -11867936);
         c.rect(cx - 7.0, 104.0, 14.0, 1.0, -7697774);
         c.disc(cx - 4.0, 113.0, 1.6, -14013906);
         c.disc(cx + 4.0, 113.0, 1.6, -14013906);
      }
   }

   static final class DragonEgg implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.07, y * 0.05, 5);
               c.set(
                  x, y, AnimatedCapes.dither(AnimatedCapes.shade(AnimatedCapes.lerp(-15069164, -12965336, n), 0.6 + 0.4 * Math.abs(Math.sin(n * 10.0))), x, y)
               );
            }
         }

         c.vignette(0.6);

         for (int i = 0; i < 140; i++) {
            double a = AnimatedCapes.hash(i, 1) * Math.PI;
            double r = 18.0 + AnimatedCapes.hash(i, 2) * 12.0;
            double x = 40.0 + Math.cos(a) * r;
            double y = 104.0 + Math.sin(a) * r * 0.35;
            double angle = AnimatedCapes.hash(i, 3) * Math.PI;
            c.line(
               x - Math.cos(angle) * 4.0,
               y - Math.sin(angle) * 1.5,
               x + Math.cos(angle) * 4.0,
               y + Math.sin(angle) * 1.5,
               0.8,
               AnimatedCapes.lerp(-7706054, -11914208, AnimatedCapes.hash(i, 4))
            );
         }

         c.ellipse(40.0, 106.0, 22.0, 6.0, 0.0, -14018032);

         for (int i = 0; i < 10; i++) {
            c.ellipse(
               20.0 + AnimatedCapes.hash(i, 8) * 40.0,
               106.0 + AnimatedCapes.hash(i, 9) * 4.0,
               3.0 + AnimatedCapes.hash(i, 10) * 3.0,
               2.0,
               0.0,
               AnimatedCapes.lerp(-11910072, -14015448, AnimatedCapes.hash(i, 11))
            );
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double stir = AnimatedCapes.wrap(t, 6.0);
         double rock = stir < 1.2 ? Math.sin(stir / 1.2 * Math.PI * 4.0) * 0.08 * (1.0 - stir / 1.2) : 0.0;
         double charge = 0.5 + 0.5 * Math.sin(t * 0.9);
         c.glow(40.0, 104.0, 30.0, -42480, 0.3 + 0.2 * charge);

         for (int i = 0; i < 14; i++) {
            double x = 22.0 + AnimatedCapes.hash(i, 20) * 36.0;
            double y = 104.0 + AnimatedCapes.hash(i, 21) * 5.0;
            c.add((int)x, (int)y, -38374, 0.5 + 0.5 * AnimatedCapes.noise(t * 3.0 + i, i));
         }

         double cx = 40.0;
         double cy = 76.0;
         double rx = 15.0;
         double ry = 24.0;
         double cos = Math.cos(rock);
         double sin = Math.sin(rock);

         for (int y = (int)(cy - ry - 2.0); y <= cy + ry + 2.0; y++) {
            for (int x = (int)(cx - rx - 3.0); x <= cx + rx + 3.0; x++) {
               double dx = x + 0.5 - cx;
               double dy = y + 0.5 - (cy + ry);
               double lx = dx * cos + dy * sin;
               double ly = -dx * sin + dy * cos + ry;
               double taper = 1.0 - 0.18 * Math.max(0.0, -ly / ry);
               double u = lx / (rx * taper);
               double v = ly / ry;
               double d = u * u + v * v;
               if (!(d > 1.0)) {
                  double scaleU = (lx + 40.0) / 4.5;
                  double scaleV = (ly + 40.0) / 3.5 + Math.floor(scaleU) % 2.0 * 0.5;
                  double inScale = Math.hypot(scaleU - Math.floor(scaleU) - 0.5, (scaleV - Math.floor(scaleV)) * 1.2 - 0.3);
                  int shell = AnimatedCapes.lerp(-14001558, -11916694, 0.5 + 0.5 * Math.sin(ly * 0.08 + lx * 0.05));
                  shell = AnimatedCapes.shade(shell, 0.55 + 0.6 * AnimatedCapes.clamp(0.5 - u * 0.5 - v * 0.2, 0.0, 1.0));
                  if (inScale > 0.45) {
                     shell = AnimatedCapes.shade(shell, 0.7);
                  }

                  if (AnimatedCapes.hash((int)scaleU, (int)scaleV) > 0.9) {
                     shell = AnimatedCapes.lerp(shell, -1523616, 0.6);
                  }

                  c.set(x, y, shell);
               }
            }
         }

         double[][] crack = new double[][]{{36.0, 56.0}, {40.0, 64.0}, {37.0, 70.0}, {43.0, 78.0}, {40.0, 86.0}, {45.0, 92.0}};

         for (int s = 0; s < crack.length - 1; s++) {
            double[] a = crack[s];
            double[] b = crack[s + 1];
            double glow = 0.5 + 0.5 * Math.sin(t * 2.0 - s * 0.5);
            c.beam(a[0], a[1], b[0], b[1], 0.6 + charge * 0.4, -26064, 0.5 + glow * 0.5 * charge);
         }

         c.beam(40.0, 64.0, 47.0, 60.0, 0.4, -26064, 0.6 * charge);
         c.beam(43.0, 78.0, 50.0, 80.0, 0.4, -26064, 0.6 * charge);
         c.ellipse(34.0, 64.0, 3.0, 8.0, 0.2, AnimatedCapes.alpha(-1, 0.15));
         double pulse = AnimatedCapes.wrap(t, 4.0);
         if (pulse < 0.6) {
            c.glow(41.0, 74.0, 26.0 * pulse / 0.6 + 6.0, -16288, (1.0 - pulse / 0.6) * 0.9);
         }

         for (int i = 0; i < 24; i++) {
            double life = AnimatedCapes.wrap(t * 0.35 + AnimatedCapes.hash(i, 40), 1.0);
            double xx = 20.0 + AnimatedCapes.hash(i, 41) * 40.0 + Math.sin(life * 7.0 + i) * 3.0;
            double y = 108.0 - life * 100.0;
            c.add((int)xx, (int)y, AnimatedCapes.lerp(-12192, -46576, life), 1.0 - life);
         }
      }
   }

   static final class HauntedHouse implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16119270, -14800342, -14009808});
         c.glow(52.0, 30.0, 30.0, -4136784, 0.35);
         c.disc(52.0, 30.0, 13.0, -985892);
         c.disc(48.0, 26.0, 3.0, AnimatedCapes.alpha(-3091264, 0.7));
         c.disc(56.0, 34.0, 2.0, AnimatedCapes.alpha(-3091264, 0.6));
         c.fillBelow(xx -> 96.0 - 10.0 * Math.exp(-Math.pow((xx - 40.0) / 30.0, 2.0)), -15855088);
         int wall = -15460838;
         c.polygon(new double[][]{{22.0, 90.0}, {58.0, 88.0}, {57.0, 62.0}, {23.0, 63.0}}, wall);
         c.polygon(new double[][]{{19.0, 64.0}, {61.0, 62.0}, {42.0, 44.0}}, -15987182);
         c.rect(44.0, 38.0, 10.0, 26.0, wall);
         c.polygon(new double[][]{{42.0, 39.0}, {56.0, 38.0}, {49.0, 24.0}}, -15987182);
         c.line(49.0, 24.0, 50.0, 19.0, 0.5, wall);
         c.rect(28.0, 50.0, 3.0, 10.0, wall);
         c.rect(34.0, 79.0, 6.0, 10.0, -16382198);
         c.polygon(new double[][]{{30.0, 79.0}, {44.0, 78.0}, {44.0, 76.5}, {30.0, 77.5}}, wall);

         for (int k = 0; k < 4; k++) {
            double gx = 8 + k * 20 + (k > 1 ? 6 : 0);
            double gy = 104 + k % 2 * 6;
            c.rect(gx - 2.0, gy - 6.0, 4.0, 7.0, -12960198);
            c.disc(gx, gy - 6.0, 2.0, -12960198);
            c.line(gx, gy - 7.0, gx, gy - 3.0, 0.3, -15065574);
            c.line(gx - 1.2, gy - 5.5, gx + 1.2, gy - 5.5, 0.3, -15065574);
         }

         for (int x = 0; x < 80; x += 3) {
            c.line(x, 118.0, x, 110.0, 0.5, -16250612);
            c.polygon(new double[][]{{x - 0.8, 110.0}, {x + 0.8, 110.0}, {x, 108.5}}, -16250612);
         }

         c.rect(0.0, 112.0, 80.0, 0.6, -16250612);
         c.line(6.0, 96.0, 10.0, 58.0, 1.8, -16250612);
         c.line(9.0, 70.0, 2.0, 56.0, 0.9, -16250612);
         c.line(9.0, 66.0, 18.0, 50.0, 0.8, -16250612);
         c.line(15.0, 55.0, 20.0, 54.0, 0.5, -16250612);
         c.line(10.0, 58.0, 12.0, 46.0, 0.6, -16250612);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] windows = new double[][]{{27.0, 68.0, 0.0}, {50.0, 68.0, 1.0}, {48.0, 46.0, 2.0}, {27.0, 80.0, 3.0}, {50.0, 80.0, 4.0}};

         for (double[] w : windows) {
            double flicker = AnimatedCapes.noise(t * 3.0 + w[2] * 7.0, w[2]);
            boolean ghostly = w[2] == 2.0;
            int col = ghostly ? AnimatedCapes.lerp(-12910710, -15037878, flicker) : (flicker > 0.35 ? AnimatedCapes.lerp(-26064, -12176, flicker) : -15066598);
            c.rect(w[0] - 2.0, w[1] - 3.0, 4.0, 5.0, col);
            c.line(w[0], w[1] - 3.0, w[0], w[1] + 2.0, 0.4, -16382198);
            c.line(w[0] - 2.0, w[1] - 0.5, w[0] + 2.0, w[1] - 0.5, 0.4, -16382198);
            if (flicker > 0.35 || ghostly) {
               c.glow(w[0], w[1], 6.0, col, 0.2);
            }
         }

         c.disc(37.0, 91.0, 2.0, AnimatedCapes.lerp(-2065894, -24512, AnimatedCapes.noise(t * 6.0, 3.0)));
         c.glow(37.0, 91.0, 5.0, -30176, 0.4);

         for (int g = 0; g < 3; g++) {
            double gx = AnimatedCapes.wrap(t * (3 + g) + g * 30, 110.0) - 15.0;
            double gy = 60 + g * 16 + Math.sin(t * 1.5 + g * 2) * 5.0;
            double a = 0.45 + 0.15 * Math.sin(t * 2.0 + g);
            c.disc(gx, gy, 4.0, AnimatedCapes.alpha(-984844, a));
            c.rect(gx - 4.0, gy, 8.0, 5.0, AnimatedCapes.alpha(-984844, a));

            for (int s = 0; s < 4; s++) {
               c.disc(gx - 3.0 + s * 2, gy + 5.0 + Math.sin(t * 6.0 + s + g) * 0.8, 1.0, AnimatedCapes.alpha(-984844, a));
            }

            c.ellipse(gx - 1.5, gy - 0.5, 0.7, 1.1, 0.0, AnimatedCapes.alpha(-16119286, 0.8));
            c.ellipse(gx + 1.5, gy - 0.5, 0.7, 1.1, 0.0, AnimatedCapes.alpha(-16119286, 0.8));
            c.ellipse(gx, gy + 2.0, 0.8, 0.6, 0.0, AnimatedCapes.alpha(-16119286, 0.6));
         }

         for (int b = 0; b < 5; b++) {
            double bx = AnimatedCapes.wrap(t * (12 + b * 3) + b * 25, 110.0) - 15.0;
            double by = 18 + b * 7 + Math.sin(t * 3.0 + b) * 4.0;
            double flap = Math.sin(t * 16.0 + b);
            c.polygon(new double[][]{{bx, by}, {bx - 3.0, by - 1.8 * flap}, {bx - 1.5, by + 0.5}}, -16382198);
            c.polygon(new double[][]{{bx, by}, {bx + 3.0, by - 1.8 * flap}, {bx + 1.5, by + 0.5}}, -16382198);
         }

         for (int y = 92; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05 + t * 0.1, y * 0.15 - t * 0.02, 3);
               c.blend(x, y, AnimatedCapes.alpha(-4665152, AnimatedCapes.smoothstep(0.4, 0.75, n) * 0.45 * ((y - 92) / 36.0 + 0.3)));
            }
         }

         if (AnimatedCapes.wrap(t, 9.0) < 0.1) {
            for (int i = 0; i < c.px.length; i++) {
               c.add(i % 80, i / 80, -3088129, 0.35);
            }
         }
      }
   }

   static final class Kraken implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05, y * 0.06, 5);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-16118248, -12958632, AnimatedCapes.smoothstep(0.35, 0.8, n) * (1.0 - y / 180.0)), x, y));
            }
         }
      }

      private static double wave(double x, double t, int layer) {
         return 96 + layer * 8 + Math.sin(x * 0.12 + t * 1.2 + layer) * (3.0 - layer * 0.6) + Math.sin(x * 0.31 - t * 1.7 + layer * 2) * 1.2;
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         boolean flash = AnimatedCapes.wrap(t, 6.0) < 0.12 || AnimatedCapes.wrap(t, 6.0) > 0.2 && AnimatedCapes.wrap(t, 6.0) < 0.28;
         if (flash) {
            for (int i = 0; i < c.px.length; i++) {
               c.add(i % 80, i / 80, -5193504, 0.3);
            }

            double bx = 10.0 + AnimatedCapes.hash((int)(t / 6.0), 1) * 60.0;
            double y = 0.0;

            for (int s = 0; s < 6; s++) {
               double nx = bx + (AnimatedCapes.hash((int)(t / 6.0), s + 3) - 0.5) * 12.0;
               c.beam(bx, y, nx, y + 12.0, 0.7, -1511169, 1.0);
               bx = nx;
               y += 12.0;
            }
         }

         c.glow(40.0, 116.0, 14.0, -12256, 0.5 + 0.2 * Math.sin(t * 2.0));
         c.ellipse(40.0, 116.0, 6.0, 2.2, 0.0, -8128);
         c.ellipse(40.0, 116.0, 1.2, 2.0, 0.0, -15070720);
         double[][] tentacles = new double[][]{{8.0, 0.0}, {70.0, 1.7}, {24.0, 3.1}, {60.0, 4.4}};

         for (int k = 0; k < tentacles.length; k++) {
            double bx = tentacles[k][0];
            double phase = tentacles[k][1];
            double rise = 0.75 + 0.25 * Math.sin(t * 0.5 + phase);
            double len = 70.0 * rise;
            double[][] pts = new double[22][];

            for (int s = 0; s < pts.length; s++) {
               double f = s / (pts.length - 1.0);
               double curl = Math.sin(f * 4.0 + t * 1.3 + phase) * f * 12.0 + (bx < 40.0 ? f * f * 16.0 : -f * f * 16.0);
               pts[s] = new double[]{bx + curl, 104.0 - f * len};
            }

            for (int s = 0; s < pts.length - 1; s++) {
               double f = s / (pts.length - 1.0);
               double w = 5.5 * (1.0 - f) + 0.6;
               c.line(pts[s][0], pts[s][1], pts[s + 1][0], pts[s + 1][1], w, AnimatedCapes.lerp(-10872262, -7722422, f));
               if (s % 2 == 0 && f < 0.85) {
                  double side = bx < 40.0 ? 1.0 : -1.0;
                  c.disc(pts[s][0] + side * w * 0.3, pts[s][1], w * 0.18 + 0.3, -1531728);
               }
            }
         }

         double tilt = Math.sin(t * 1.2) * 0.12;
         double sx = 40.0;
         double sy = wave(40.0, t, 0) - 4.0;
         double cos = Math.cos(tilt);
         double sin = Math.sin(tilt);
         c.polygon(
            new double[][]{
               {sx - 14.0 * cos, sy - 14.0 * sin},
               {sx + 14.0 * cos, sy + 14.0 * sin},
               {sx + 10.0 * cos - 5.0 * sin, sy + 10.0 * sin + 5.0 * cos},
               {sx - 11.0 * cos - 5.0 * sin, sy - 11.0 * sin + 5.0 * cos}
            },
            -12966892
         );
         c.line(sx - 14.0 * cos, sy - 14.0 * sin - 1.0, sx + 14.0 * cos, sy + 14.0 * sin - 1.0, 0.6, -9811414);

         for (int m = -1; m <= 1; m++) {
            double mx = sx + m * 8 * cos;
            double my = sy + m * 8 * sin;
            double height = m == 0 ? 30.0 : 22.0;
            c.line(mx, my, mx + height * sin, my - height * cos, 0.8, -14018034);

            for (int sail = 0; sail < 2; sail++) {
               double sy0 = 6 + sail * 9;
               double billow = 3.0 + Math.sin(t * 2.0 + m) * 1.2;
               c.polygon(
                  new double[][]{
                     {mx + sy0 * sin - 5.0 * cos, my - sy0 * cos - 5.0 * sin},
                     {mx + sy0 * sin + 5.0 * cos, my - sy0 * cos + 5.0 * sin},
                     {mx + (sy0 + 7.0) * sin + 5.0 * cos + billow, my - (sy0 + 7.0) * cos + 5.0 * sin},
                     {mx + (sy0 + 7.0) * sin - 5.0 * cos + billow, my - (sy0 + 7.0) * cos - 5.0 * sin}
                  },
                  flash ? -987936 : -5726064
               );
            }
         }

         for (int layer = 0; layer < 3; layer++) {
            for (int x = 0; x < 80; x++) {
               double top = wave(x, t, layer);

               for (int y = (int)top; y < 128; y++) {
                  double depth = (y - top) / 30.0;
                  int col = AnimatedCapes.lerp(AnimatedCapes.lerp(-14005670, -15062470, layer / 2.0), -16446960, depth);
                  c.blend(x, y, AnimatedCapes.alpha(col, layer == 0 ? 0.55 : 0.9));
               }

               if (AnimatedCapes.noise(x * 0.3 + t, layer) > 0.45) {
                  c.blend(x, (int)top, AnimatedCapes.alpha(-1511180, 0.7));
               }
            }
         }

         for (int i = 0; i < 90; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 60) * 128.0 + t * 80.0, 128.0);
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 61) * 80.0 - t * 25.0, 80.0);
            c.line(x, y, x - 1.5, y + 4.0, 0.4, AnimatedCapes.alpha(-7693648, 0.35));
         }
      }
   }

   static final class Pegasus implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.4, 0.7}, new int[]{-12965254, -2069878, -14224});
         c.glow(60.0, 84.0, 40.0, -10096, 0.5);
         c.disc(60.0, 84.0, 10.0, -3896);

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.06, y * 0.12, 4);
               double top = 88.0 + n * 14.0;
               if (y > top) {
                  c.set(
                     x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-8000, -3110246, AnimatedCapes.clamp((y - top) / 20.0 + (1.0 - n) * 0.3, 0.0, 1.0)), x, y)
                  );
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 4; k++) {
            double x = AnimatedCapes.wrap(-t * (8 + k * 6) + k * 30, 120.0) - 20.0;
            c.ellipse(x, 30 + k * 16, 12 + k * 3, 1.2, 0.0, AnimatedCapes.alpha(-5920, 0.5));
         }

         double gallop = t * 4.0;
         double hx = 38.0 + Math.sin(t * 0.5) * 4.0;
         double hy = 60.0 + Math.sin(gallop) * 1.5;
         int coat = -461062;
         int shade = -2567960;
         double wingA = Math.sin(t * 2.2);

         for (int f = 0; f < 9; f++) {
            double a = -2.4707963267948965 + f * 0.16 + wingA * 0.5;
            double len = 16.0 + f * 1.6 - Math.abs(f - 5) * 1.2;
            c.line(hx + 2.0, hy - 4.0, hx + 2.0 + Math.cos(a) * len, hy - 4.0 + Math.sin(a) * len, 2.2, AnimatedCapes.lerp(shade, coat, f / 9.0));
         }

         for (int s = 0; s < 6; s++) {
            double lag = s * 0.15;
            double tx = hx - 13.0 - s * 2.8;
            double ty = hy - 2.0 + s * 1.2 + Math.sin(t * 5.0 - lag * 8.0) * s * 0.6;
            c.disc(tx, ty, 1.8 - s * 0.2, AnimatedCapes.lerp(-1517313, -4677392, s / 6.0));
         }

         for (int leg = 0; leg < 4; leg++) {
            boolean front = leg >= 2;
            double lx = hx + (front ? 7 : -8) + leg % 2 * 2;
            double swing = Math.sin(gallop + leg * 0.8) * 0.7;
            double kneeX = lx + Math.sin(swing) * 4.0;
            double kneeY = hy + 5.0 + Math.cos(swing) * 3.0;
            c.line(lx, hy + 2.0, kneeX, kneeY, 1.6, leg % 2 == 0 ? shade : coat);
            c.line(kneeX, kneeY, kneeX + (front ? 2.5 : -2.5) * Math.cos(swing), kneeY + 4.0, 1.1, leg % 2 == 0 ? shade : coat);
         }

         c.ellipse(hx, hy, 11.0, 5.0, -0.05, coat);
         c.ellipse(hx, hy + 2.0, 9.0, 2.5, 0.0, shade);
         c.polygon(new double[][]{{hx + 6.0, hy - 3.0}, {hx + 11.0, hy - 2.0}, {hx + 15.0, hy - 11.0}, {hx + 11.0, hy - 13.0}}, coat);
         c.ellipse(hx + 16.0, hy - 12.0, 4.0, 2.2, 0.4, coat);
         c.polygon(new double[][]{{hx + 13.0, hy - 14.0}, {hx + 14.5, hy - 17.0}, {hx + 15.0, hy - 13.5}}, coat);
         c.rect(hx + 16.0, hy - 13.0, 0.8, 0.8, -14017990);

         for (int m = 0; m < 6; m++) {
            double mx = hx + 12.0 - m * 1.5;
            double my = hy - 13.0 + m * 1.6;
            c.line(mx, my, mx - 4.0 - Math.sin(t * 6.0 + m) * 1.5, my - 1.0 + Math.cos(t * 6.0 + m), 0.8, AnimatedCapes.lerp(-2569985, -5730072, m / 6.0));
         }

         for (int f = 0; f < 11; f++) {
            double a = -2.6707963267948966 + f * 0.17 + wingA * 0.65;
            double len = 18 + f * 2 - Math.abs(f - 6) * 1.5;
            double rx = hx + 1.0;
            double ry = hy - 5.0;
            c.line(rx, ry, rx + Math.cos(a) * len, ry + Math.sin(a) * len, 2.4, AnimatedCapes.lerp(coat, -3880, f / 11.0));
            c.line(
               rx + Math.cos(a) * len * 0.5,
               ry + Math.sin(a) * len * 0.5,
               rx + Math.cos(a) * len,
               ry + Math.sin(a) * len,
               0.4,
               AnimatedCapes.alpha(-4675376, 0.6)
            );
         }

         for (int i = 0; i < 18; i++) {
            double life = AnimatedCapes.wrap(t * 0.8 + i / 18.0, 1.0);
            c.star(hx - 14.0 - life * 40.0, hy + Math.sin(i * 2.3) * 8.0 + life * 6.0, 1.0, -3920, 0.8 * (1.0 - life));
         }
      }
   }

   static final class PumpkinPatch implements AnimatedCapes.Scene {
      private static final double[][] CARVED = new double[][]{{40.0, 108.0, 12.0}, {14.0, 118.0, 7.0}, {66.0, 114.0, 8.0}};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 0.75}, new int[]{-14017974, -7718310, -1013184});
         c.glow(62.0, 64.0, 22.0, -20400, 0.4);
         c.disc(62.0, 64.0, 8.0, -10096);
         c.fillBelow(xx -> 80.0 + 2.0 * Math.sin(xx * 0.1), -14018028);

         for (int y = 78; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= 80.0 + 2.0 * Math.sin(x * 0.1)) {
                  c.set(
                     x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-12965350, -15068660, (y - 80) / 48.0 + AnimatedCapes.fbm(x * 0.2, y * 0.3, 2) * 0.3), x, y)
                  );
               }
            }
         }

         for (int i = 0; i < 9; i++) {
            double xx = 4 + i * 9;
            c.line(xx, 82.0, xx + (AnimatedCapes.hash(i, 1) - 0.5) * 3.0, 58.0 + AnimatedCapes.hash(i, 2) * 8.0, 0.8, -12965352);
            c.line(xx, 70.0, xx + 5.0, 64.0, 0.5, -12965352);
         }

         c.line(24.0, 96.0, 24.0, 54.0, 1.2, -12966892);
         c.line(12.0, 64.0, 36.0, 62.0, 1.0, -12966892);
         c.polygon(new double[][]{{19.0, 66.0}, {29.0, 66.0}, {30.0, 84.0}, {18.0, 84.0}}, -12957078);
         c.disc(24.0, 58.0, 4.0, -2576272);
         c.polygon(new double[][]{{17.0, 56.0}, {31.0, 56.0}, {24.0, 48.0}}, -11912672);
         c.rect(16.0, 55.0, 16.0, 1.5, -11912672);
         c.line(12.0, 64.0, 9.0, 68.0, 0.8, -2576272);
         c.line(36.0, 62.0, 39.0, 66.0, 0.8, -2576272);

         for (int i = 0; i < 22; i++) {
            double xx = AnimatedCapes.hash(i, 5) * 80.0;
            double y = 90.0 + AnimatedCapes.hash(i, 6) * 36.0;
            c.line(xx, y, xx + 8.0 * (AnimatedCapes.hash(i, 7) - 0.5), y + 3.0, 0.5, -12953046);
         }

         double[][] pumpkins = new double[][]{
            {30.0, 94.0, 4.0}, {56.0, 96.0, 5.0}, {6.0, 102.0, 4.0}, {72.0, 100.0, 3.5}, {50.0, 124.0, 5.0}, {28.0, 126.0, 4.0}
         };

         for (double[] p : pumpkins) {
            pumpkin(c, p[0], p[1], p[2]);
         }

         for (double[] p : CARVED) {
            pumpkin(c, p[0], p[1], p[2]);
         }
      }

      private static void pumpkin(AnimatedCapes.Canvas c, double x, double y, double r) {
         for (int rib = -2; rib <= 2; rib++) {
            c.ellipse(x + rib * r * 0.35, y, r * 0.45, r * 0.8, 0.0, AnimatedCapes.lerp(-2067430, -5223926, Math.abs(rib) / 3.0));
         }

         c.ellipse(x - r * 0.3, y - r * 0.35, r * 0.25, r * 0.15, 0.0, AnimatedCapes.alpha(-20384, 0.6));
         c.line(x, y - r * 0.75, x + r * 0.2, y - r * 1.1, r * 0.15 + 0.3, -11904470);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < CARVED.length; k++) {
            double x = CARVED[k][0];
            double y = CARVED[k][1];
            double r = CARVED[k][2];
            double flicker = AnimatedCapes.noise(t * 6.0 + k * 5, k);
            int fire = AnimatedCapes.lerp(-34288, -8080, flicker);
            c.glow(x, y, r * 2.4, -30176, 0.3 + 0.2 * flicker);

            for (int side = -1; side <= 1; side += 2) {
               c.polygon(new double[][]{{x + side * r * 0.45, y - r * 0.4}, {x + side * r * 0.15, y - r * 0.05}, {x + side * r * 0.7, y - r * 0.05}}, fire);
            }

            c.polygon(new double[][]{{x, y + r * 0.02}, {x - r * 0.15, y + r * 0.2}, {x + r * 0.15, y + r * 0.2}}, fire);
            double[][] mouth = new double[9][];

            for (int m = 0; m < 9; m++) {
               double f = m / 8.0;
               double mx = x - r * 0.6 + f * r * 1.2;
               double my = y + r * 0.35 + Math.sin(f * Math.PI) * r * 0.25 + (m % 2 == 1 ? -r * 0.12 : 0.0);
               mouth[m] = new double[]{mx, my};
            }

            double[][] shape = new double[18][];

            for (int m = 0; m < 9; m++) {
               shape[m] = mouth[m];
               shape[17 - m] = new double[]{mouth[m][0], y + r * 0.3 + Math.sin(m / 8.0 * Math.PI) * r * 0.45};
            }

            c.polygon(shape, fire);
         }

         double hop = Math.max(0.0, Math.sin(t * 2.5));
         double crowX = 12.0 + AnimatedCapes.wrap(t * 0.4, 1.0) * 20.0;
         c.ellipse(crowX, 60.0 - hop * 3.0, 2.4, 1.4, 0.0, -16119282);
         c.disc(crowX + 2.0, 58.5 - hop * 3.0, 1.1, -16119282);
         c.polygon(new double[][]{{crowX + 3.0, 58.3 - hop * 3.0}, {crowX + 4.5, 58.8 - hop * 3.0}, {crowX + 3.0, 59.3 - hop * 3.0}}, -12961232);
         c.line(crowX - 2.0, 60.0 - hop * 3.0, crowX - 4.5, 61.0 - hop * 3.0, 0.8, -16119282);
         double fx = AnimatedCapes.wrap(-t * 8.0, 110.0) - 15.0;
         AnimatedCapes.bird(c, fx, 30.0 + Math.sin(t) * 3.0, 2.2, t * 7.0, -16119282);

         for (int w = 0; w < 3; w++) {
            double wx = 20 + w * 20 + Math.sin(t * 0.6 + w * 2) * 8.0;
            double wy = 96.0 + Math.sin(t * 1.1 + w) * 6.0;
            c.glow(wx, wy, 5.0, -10436353, 0.5 + 0.2 * Math.sin(t * 4.0 + w));
            c.polygon(new double[][]{{wx - 1.2, wy + 1.0}, {wx + 1.2, wy + 1.0}, {wx + Math.sin(t * 8.0 + w) * 0.8, wy - 3.0}}, -5183233);
         }

         for (int i = 0; i < 8; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 128.0 + t * (5.0 + AnimatedCapes.hash(i, 31) * 4.0), 128.0);
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 32) * 80.0 + t * 3.0 + Math.sin(t + i) * 4.0, 80.0);
            c.ellipse(x, y, 1.6, Math.max(0.4, Math.abs(Math.cos(t * 2.0 + i)) * 1.2), t + i, i % 2 == 0 ? -5219814 : -7718384);
         }
      }
   }

   static final class Redstone implements AnimatedCapes.Scene {
      private static final double[][] PATH = new double[][]{
         {10.0, 24.0}, {10.0, 56.0}, {40.0, 56.0}, {40.0, 34.0}, {64.0, 34.0}, {64.0, 80.0}, {24.0, 80.0}, {24.0, 108.0}, {56.0, 108.0}
      };
      private static final double[][] LAMPS = new double[][]{{52.0, 22.0}, {72.0, 58.0}, {12.0, 94.0}, {66.0, 108.0}};
      private static final double[][] REPEATERS = new double[][]{{26.0, 56.0}, {52.0, 34.0}, {44.0, 80.0}};

      private static double[] at(double f) {
         double total = 0.0;

         for (int i = 0; i < PATH.length - 1; i++) {
            total += Math.hypot(PATH[i + 1][0] - PATH[i][0], PATH[i + 1][1] - PATH[i][1]);
         }

         double want = f * total;

         for (int i = 0; i < PATH.length - 1; i++) {
            double seg = Math.hypot(PATH[i + 1][0] - PATH[i][0], PATH[i + 1][1] - PATH[i][1]);
            if (want <= seg) {
               return new double[]{PATH[i][0] + (PATH[i + 1][0] - PATH[i][0]) * want / seg, PATH[i][1] + (PATH[i + 1][1] - PATH[i][1]) * want / seg};
            }

            want -= seg;
         }

         return PATH[PATH.length - 1];
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int bx = x / 8;
               int by = y / 8;
               int stone = AnimatedCapes.lerp(-12961218, -11645358, AnimatedCapes.hash(bx * 3 + x / 2, by * 5 + y / 2));
               if (x % 8 == 0 || y % 8 == 0) {
                  stone = AnimatedCapes.shade(stone, 0.8);
               }

               c.set(x, y, stone);
            }
         }

         for (int i = 0; i < PATH.length - 1; i++) {
            c.line(PATH[i][0], PATH[i][1], PATH[i + 1][0], PATH[i + 1][1], 2.2, -11925496);
         }

         c.rect(4.0, 14.0, 12.0, 6.0, -10855842);
         c.line(10.0, 17.0, 6.0, 10.0, 1.0, -9811414);
         c.rect(5.0, 9.0, 2.0, 2.0, -12961218);

         for (double[] r : REPEATERS) {
            c.rect(r[0] - 4.0, r[1] - 3.0, 8.0, 6.0, -7697778);
            c.rect(r[0] - 4.0, r[1] - 3.0, 8.0, 1.0, -5723988);
         }

         c.rect(62.0, 84.0, 12.0, 12.0, -9807294);
         c.rect(62.0, 84.0, 12.0, 3.0, -5731752);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycle = AnimatedCapes.wrap(t, 6.0);
         boolean on = cycle < 4.5;
         double head = AnimatedCapes.clamp(cycle / 2.2, 0.0, 1.0);
         c.line(10.0, 17.0, on ? 14.0 : 6.0, 10.0, 1.0, -9811414);
         c.rect(on ? 13.0 : 5.0, 9.0, 2.0, 2.0, -12961218);
         if (on) {
            for (double f = 0.0; f < head; f += 0.004) {
               double[] p = at(f);
               double strength = 0.55 + 0.45 * Math.max(0.0, 1.0 - (head - f) * 6.0);
               c.add((int)p[0], (int)p[1], -54758, strength);
               c.add((int)p[0] - 1, (int)p[1], -54758, strength * 0.5);
               c.add((int)p[0], (int)p[1] - 1, -54758, strength * 0.5);
            }

            double[] tip = at(head);
            c.glow(tip[0], tip[1], 6.0, -50662, 0.8);

            for (int i = 0; i < 10; i++) {
               double f = AnimatedCapes.hash(i, (int)(t * 6.0)) * head;
               double[] p = at(f);
               c.rect(p[0] + (AnimatedCapes.hash(i, 3) - 0.5) * 3.0, p[1] - 2.0 - AnimatedCapes.hash(i, (int)(t * 6.0) + 1) * 3.0, 1.0, 1.0, -38310);
            }
         }

         double[] lampAt = new double[]{0.18, 0.5, 0.8, 1.0};

         for (int k = 0; k < LAMPS.length; k++) {
            boolean lit = on && head >= lampAt[k];
            double lx = LAMPS[k][0];
            double ly = LAMPS[k][1];
            if (lit) {
               c.glow(lx, ly, 16.0, -16288, 0.6);
            }

            for (int py = 0; py < 10; py++) {
               for (int px = 0; px < 10; px++) {
                  boolean frame = px == 0 || py == 0 || px == 9 || py == 9 || (px + py) % 5 == 0;
                  int col = lit ? (frame ? -7710166 : AnimatedCapes.lerp(-12176, -3920, AnimatedCapes.hash(px, py))) : (frame ? -12966892 : -9811414);
                  c.set((int)lx - 5 + px, (int)ly - 5 + py, col);
               }
            }
         }

         double[] repeaterAt = new double[]{0.28, 0.47, 0.72};

         for (int k = 0; k < REPEATERS.length; k++) {
            boolean lit = on && head >= repeaterAt[k];
            double rx = REPEATERS[k][0];
            double ry = REPEATERS[k][1];

            for (int s = -1; s <= 1; s += 2) {
               c.rect(rx + s * 2 - 0.5, ry - 5.0, 1.2, 3.0, -9811414);
               c.rect(rx + s * 2 - 0.8, ry - 6.0, 1.8, 1.8, lit ? -50646 : -10872294);
               if (lit) {
                  c.glow(rx + s * 2, ry - 5.5, 3.0, -50646, 0.6);
               }
            }
         }

         double push = on && head >= 0.66 ? Math.min(1.0, (head - 0.66) * 8.0) : 0.0;
         c.rect(62.0 - push * 8.0, 86.0, 2.0 + push * 8.0, 8.0, -7701926);
         c.rect(54.0 - push * 0.5 + (1.0 - push) * 8.0, 84.0, 8.0, 12.0, -5731752);
      }
   }

   static final class SwordInStone implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7, 1.0}, new int[]{-16115168, -15060952, -15720422});

         for (int i = 0; i < 12; i++) {
            double x = AnimatedCapes.hash(i, 1) * 80.0;
            double w = 3.0 + AnimatedCapes.hash(i, 2) * 6.0;
            c.rect(x, 0.0, w, 108.0, AnimatedCapes.lerp(-16379890, -15720420, AnimatedCapes.hash(i, 3)));
         }

         c.fillBelow(xx -> 106.0 + 3.0 * AnimatedCapes.fbm(xx * 0.1, 2.0, 3), -15060960);

         for (int y = 102; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= 106.0 + 3.0 * AnimatedCapes.fbm(x * 0.1, 2.0, 3)) {
                  c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-14005718, -15720434, AnimatedCapes.fbm(x * 0.3, y * 0.3, 3)), x, y));
               }
            }
         }

         c.polygon(new double[][]{{18.0, 116.0}, {22.0, 96.0}, {32.0, 86.0}, {50.0, 86.0}, {60.0, 96.0}, {64.0, 116.0}}, (xx, yx) -> {
            int rock = AnimatedCapes.lerp(-7697788, -11908536, (xx - 18) / 46.0 * 0.7 + AnimatedCapes.fbm(xx * 0.25, yx * 0.25, 3) * 0.4);
            return AnimatedCapes.fbm(xx * 0.2 + 9.0, yx * 0.2, 3) > 0.58 && yx < 102 ? AnimatedCapes.lerp(rock, -11896262, 0.7) : rock;
         });
         c.line(30.0, 90.0, 36.0, 110.0, 0.5, -12961224);
         c.line(52.0, 92.0, 48.0, 104.0, 0.5, -12961224);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double pulse = 0.5 + 0.5 * Math.sin(t * 1.5);
         c.polygon(
            new double[][]{{30.0, 0.0}, {48.0, 0.0}, {62.0, 110.0}, {20.0, 110.0}},
            (xx, yx) -> AnimatedCapes.alpha(-1509168, (0.1 + 0.04 * Math.sin(yx * 0.1 - t * 2.0)) * (1.0 - yx / 140.0))
         );
         c.glow(41.0, 60.0, 34.0, -7675649, 0.2 + 0.15 * pulse);
         double bladeTop = 50.0;
         double bladeBottom = 88.0;
         c.polygon(
            new double[][]{{38.5, bladeTop}, {43.5, bladeTop}, {43.0, bladeBottom}, {39.0, bladeBottom}},
            (xx, yx) -> AnimatedCapes.lerp(-985864, -6642512, (xx - 38.5) / 5.0)
         );
         c.line(41.0, bladeTop, 41.0, bladeBottom, 0.5, -1);

         for (int r = 0; r < 6; r++) {
            double ry = bladeTop + 4.0 + r * 5.5;
            double glow = Math.max(0.0, Math.sin(t * 3.0 - r * 0.8));
            c.add(40, (int)ry, -10428161, 0.4 + glow);
            c.add(41, (int)ry + 1, -10428161, 0.4 + glow);
            c.add(40, (int)ry + 2, -10428161, 0.3 + glow * 0.8);
            if (glow > 0.8) {
               c.glow(41.0, ry + 1.0, 4.0, -10428161, glow * 0.4);
            }
         }

         c.polygon(new double[][]{{31.0, bladeTop - 1.0}, {51.0, bladeTop - 1.0}, {49.0, bladeTop + 1.5}, {33.0, bladeTop + 1.5}}, -2578374);
         c.disc(31.5, bladeTop, 1.4, -1525686);
         c.disc(50.5, bladeTop, 1.4, -1525686);
         c.rect(39.5, bladeTop - 11.0, 3.0, 10.0, -10864094);

         for (int w = 0; w < 5; w++) {
            c.line(39.5, bladeTop - 10.0 + w * 2, 42.5, bladeTop - 9.0 + w * 2, 0.4, -12966892);
         }

         c.disc(41.0, bladeTop - 13.0, 2.2, -2578374);
         c.disc(41.0, bladeTop - 13.0, 1.3, -12918608);
         c.glow(41.0, bladeTop - 13.0, 5.0, -12918608, 0.5 + 0.4 * pulse);
         double shock = AnimatedCapes.wrap(t, 5.0);
         if (shock < 1.2) {
            double p = shock / 1.2;
            c.ring(41.0, 86.0, p * 30.0, 1.0, AnimatedCapes.alpha(-7671553, 0.6 * (1.0 - p)));
            c.glow(41.0, 70.0, 20.0, -1, (1.0 - p) * 0.5);
         }

         for (int i = 0; i < 26; i++) {
            double life = AnimatedCapes.wrap(t * 0.15 + AnimatedCapes.hash(i, 10), 1.0);
            double x = 41.0 + (AnimatedCapes.hash(i, 11) - 0.5) * 50.0 + Math.sin(life * 6.0 + i) * 4.0;
            double y = 108.0 - life * 100.0;
            c.glow(x, y, 1.6, -2031664, 0.8 * Math.sin(life * Math.PI));
         }
      }
   }
}
