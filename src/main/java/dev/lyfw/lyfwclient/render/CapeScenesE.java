package dev.lyfw.lyfwclient.render;

final class CapeScenesE {
   private CapeScenesE() {
   }

   static final class BambooGrove implements AnimatedCapes.Scene {
      private static final double[] NEAR = new double[]{6.0, 20.0, 60.0, 73.0};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-2298148, -5713760, -9790886});

         for (int layer = 0; layer < 2; layer++) {
            int col = layer == 0 ? -4664144 : -7687042;

            for (int i = 0; i < 14; i++) {
               double x = AnimatedCapes.hash(i, layer + 1) * 80.0;
               double w = 1.5 + layer;
               c.rect(x, 0.0, w, 116.0, col);

               for (double y = 6.0 + AnimatedCapes.hash(i, 9) * 10.0; y < 116.0; y += 14.0) {
                  c.rect(x - 0.3, y, w + 0.6, 1.0, AnimatedCapes.shade(col, 0.85));
               }
            }

            for (int y = 0; y < 128; y++) {
               for (int x = 0; x < 80; x++) {
                  c.blend(x, y, AnimatedCapes.alpha(-1510176, 0.22 * AnimatedCapes.fbm(x * 0.05, y * 0.03 + layer * 7, 3)));
               }
            }
         }

         c.fillBelow(xx -> 112.0 + 3.0 * AnimatedCapes.fbm(xx * 0.1, 3.0, 3), -11900368);

         for (int y = 110; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (c.get(x, y) == -11900368) {
                  c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-10847686, -14005730, AnimatedCapes.fbm(x * 0.3, y * 0.3, 3)), x, y));
               }
            }
         }

         c.rect(34.0, 100.0, 10.0, 3.0, -7697788);
         c.rect(36.0, 103.0, 6.0, 9.0, -8750476);
         c.polygon(new double[][]{{32.0, 100.0}, {46.0, 100.0}, {39.0, 94.0}}, -9803164);
         c.rect(37.5, 104.0, 3.0, 3.0, -10096);
         c.ellipse(70.0, 118.0, 6.0, 3.0, 0.0, -9803162);
         c.ellipse(12.0, 120.0, 5.0, 2.5, 0.0, -8750478);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 110; y++) {
            for (int x = 0; x < 80; x++) {
               double ray = Math.pow(AnimatedCapes.noise((x + y * 0.4) * 0.08 + t * 0.05, 2.2), 4.0);
               c.add(x, y, -32, ray * 0.35);
            }
         }

         c.glow(39.0, 105.0, 6.0, -16288, 0.3 + 0.1 * AnimatedCapes.noise(t * 4.0, 1.0));

         for (int k = 0; k < NEAR.length; k++) {
            double baseX = NEAR[k];
            double sway = Math.sin(t * 0.8 + k * 1.3) * 3.0;
            double px = baseX;
            double py = 118.0;

            for (int s = 1; s <= 12; s++) {
               double f = s / 12.0;
               double nx = baseX + sway * f * f;
               double ny = 118.0 - f * 124.0;
               c.line(px, py, nx, ny, 3.2, -10843606);
               c.line(px - 1.0, py, nx - 1.0, ny, 0.8, -7685558);
               c.rect(nx - 2.0, ny - 0.5, 4.0, 1.3, -12948962);
               if (s % 3 == 0) {
                  for (int leaf = 0; leaf < 3; leaf++) {
                     double a = (k % 2 == 0 ? 0.3 : 2.8415926535897933) + (leaf - 1) * 0.35 + Math.sin(t * 1.5 + s + leaf) * 0.08;
                     c.ellipse(
                        nx + Math.cos(a) * 5.0,
                        ny + Math.sin(a) * 5.0 + 1.0,
                        5.0,
                        1.1,
                        a,
                        AnimatedCapes.lerp(-12944854, -9786822, AnimatedCapes.hash(s, leaf + k))
                     );
                  }
               }

               px = nx;
               py = ny;
            }
         }

         for (int i = 0; i < 10; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 40) * 128.0 + t * (6.0 + AnimatedCapes.hash(i, 41) * 4.0), 138.0) - 5.0;
            double x = AnimatedCapes.hash(i, 42) * 80.0 + Math.sin(t + i) * 5.0;
            c.ellipse(x, y, 2.4, Math.max(0.4, Math.abs(Math.cos(t * 2.0 + i))), t + i, -8738230);
         }

         double chew = Math.sin(t * 3.0);
         double pandaX = 40.0;
         double pandaY = 114.0;
         c.ellipse(pandaX, pandaY, 9.0, 7.0, 0.0, -723728);
         c.ellipse(pandaX - 6.0, pandaY + 5.0, 3.5, 2.5, 0.0, -15066594);
         c.ellipse(pandaX + 6.0, pandaY + 5.0, 3.5, 2.5, 0.0, -15066594);
         c.ellipse(pandaX, pandaY - 2.0, 9.5, 3.0, 0.0, -15066594);
         double headY = pandaY - 9.0 + chew * 0.3;
         c.disc(pandaX - 5.0, headY - 5.0, 2.2, -15066594);
         c.disc(pandaX + 5.0, headY - 5.0, 2.2, -15066594);
         c.disc(pandaX, headY, 6.0, -460556);
         c.ellipse(pandaX - 2.5, headY - 0.5, 1.8, 2.2, 0.4, -15066594);
         c.ellipse(pandaX + 2.5, headY - 0.5, 1.8, 2.2, -0.4, -15066594);
         c.rect(pandaX - 2.5, headY - 1.0, 1.0, 1.0, -1);
         c.rect(pandaX + 2.0, headY - 1.0, 1.0, 1.0, -1);
         c.disc(pandaX, headY + 2.2, 0.9, -15066594);
         double stickY = headY + 3.0 - chew;
         c.line(pandaX + 3.0, stickY, pandaX + 12.0, stickY - 9.0, 1.2, -9790918);
         c.ellipse(pandaX + 13.0, stickY - 11.0, 3.0, 1.0, -0.9, -11892182);
         c.ellipse(pandaX + 5.0, stickY + 1.0, 2.6, 2.0, 0.0, -15066594);
      }
   }

   static final class DesertOasis implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.45, 0.62}, new int[]{-11887904, -4662032, -727880});
         c.glow(58.0, 20.0, 30.0, -1840, 0.5);
         c.disc(58.0, 20.0, 6.0, -784);

         for (int layer = 0; layer < 3; layer++) {
            int l = layer;
            int lit = AnimatedCapes.lerp(-997256, -1529776, layer / 2.0);
            int dark = AnimatedCapes.lerp(-3630502, -5212104, layer / 2.0);

            for (int x = 0; x < 80; x++) {
               double top = 70 + l * 14 - 8.0 * Math.sin(x * (0.05 + l * 0.015) + l * 2.3) - 4.0 * Math.sin(x * 0.11 + l);
               double slope = Math.cos(x * (0.05 + l * 0.015) + l * 2.3);

               for (int y = Math.max(0, (int)top); y < 128; y++) {
                  int col = slope > 0.0 ? lit : dark;
                  if ((int)(y + x * 0.4 + Math.sin(x * 0.3) * 2.0) % 5 == 0) {
                     col = AnimatedCapes.shade(col, 0.93);
                  }

                  c.set(x, y, AnimatedCapes.dither(col, x, y));
               }
            }
         }

         c.ellipse(34.0, 108.0, 20.0, 5.0, 0.0, -13981520);
         c.ellipse(34.0, 107.0, 16.0, 3.0, 0.0, -11876144);

         for (int i = 0; i < 16; i++) {
            double rx = 14.0 + AnimatedCapes.hash(i, 5) * 40.0;
            if (Math.abs(rx - 34.0) > 12.0) {
               c.line(rx, 110.0, rx + (AnimatedCapes.hash(i, 6) - 0.5) * 2.0, 102.0 - AnimatedCapes.hash(i, 7) * 5.0, 0.6, -11896278);
            }
         }

         double[][] palms = new double[][]{{16.0, 108.0, 30.0}, {52.0, 110.0, 36.0}, {62.0, 112.0, 24.0}};

         for (double[] palm : palms) {
            double x = palm[0];
            double baseY = palm[1];
            double h = palm[2];

            for (int s = 0; s < 10; s++) {
               double f = s / 10.0;
               double f1 = (s + 1) / 10.0;
               double x0 = x + Math.sin(f * 2.0) * 4.0;
               double x1 = x + Math.sin(f1 * 2.0) * 4.0;
               c.line(x0, baseY - f * h, x1, baseY - f1 * h, 2.2 - f, s % 2 == 0 ? -9811414 : -10864098);
            }

            double cx = x + Math.sin(2.0) * 4.0;
            double cy = baseY - h;

            for (int k = 0; k < 8; k++) {
               double a = -Math.PI + k * Math.PI / 7.0;
               double px = cx;
               double py = cy;

               for (int s = 1; s <= 6; s++) {
                  double f = s / 6.0;
                  double nx = cx + Math.cos(a) * 12.0 * f;
                  double ny = cy + Math.sin(a) * 7.0 * f + f * f * 5.0;
                  c.line(px, py, nx, ny, 1.4 * (1.0 - f * 0.5), k % 2 == 0 ? -13993430 : -12940752);
                  c.line(nx, ny, nx + Math.cos(a + 1.3) * 2.0, ny + 2.0, 0.5, -13997532);
                  px = nx;
                  py = ny;
               }
            }

            c.disc(cx - 1.0, cy + 1.5, 1.2, -9811430);
            c.disc(cx + 1.0, cy + 1.8, 1.1, -9811430);
         }

         c.polygon(new double[][]{{64.0, 104.0}, {78.0, 104.0}, {71.0, 94.0}}, -1517376);
         c.polygon(new double[][]{{69.5, 104.0}, {72.5, 104.0}, {71.0, 99.0}}, -10864086);
      }

      private static void camel(AnimatedCapes.Canvas c, double x, double y, double step, int color) {
         c.ellipse(x, y, 4.5, 2.0, 0.0, color);
         c.disc(x - 0.5, y - 2.0, 2.0, color);
         c.line(x + 3.5, y - 0.5, x + 5.5, y - 5.0, 1.2, color);
         c.ellipse(x + 6.3, y - 5.4, 1.4, 0.8, 0.2, color);

         for (int leg = 0; leg < 4; leg++) {
            double lx = x - 3.0 + leg * 2;
            double swing = Math.sin(step + leg * Math.PI / 2.0) * 1.2;
            c.line(lx, y + 1.0, lx + swing, y + 6.0, 0.6, color);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int[] rows = new int[80];

         for (int y = 60; y < 78; y++) {
            int shift = (int)Math.round(Math.sin(y * 0.9 + t * 4.0) * 0.8);
            if (shift != 0) {
               for (int x = 0; x < 80; x++) {
                  rows[x] = c.get(Math.floorMod(x + shift, 80), y);
               }

               for (int x = 0; x < 80; x++) {
                  c.set(x, y, rows[x]);
               }
            }
         }

         for (int k = 0; k < 4; k++) {
            double x = AnimatedCapes.wrap(t * 2.5 - k * 11, 140.0) - 20.0;
            double ridge = 70.0 - 8.0 * Math.sin(x * 0.05) - 4.0 * Math.sin(x * 0.11);
            camel(c, x, ridge - 6.0, t * 3.0 + k, -11915232);
            if (k == 0 || k == 2) {
               c.disc(x - 0.5, ridge - 10.0, 1.0, -11915232);
               c.rect(x - 1.0, ridge - 12.5, 1.2, 1.5, -1515312);
            }
         }

         for (int kx = 0; kx < 3; kx++) {
            double a = t * 0.5 + kx * 2.1;
            AnimatedCapes.bird(c, 30.0 + Math.cos(a) * 12.0, 16.0 + Math.sin(a) * 4.0, 1.5, t * 3.0 + kx, -1339416544);
         }

         for (int x = 20; x < 50; x++) {
            if (AnimatedCapes.hash(x, (int)(t * 4.0)) > 0.9) {
               c.add(x, 106 + (int)(AnimatedCapes.hash(x, 3) * 3.0), -1, 0.7);
            }
         }

         for (int i = 0; i < 26; i++) {
            double xx = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 80.0 + t * (18.0 + AnimatedCapes.hash(i, 31) * 10.0), 80.0);
            double yx = 96.0 + AnimatedCapes.hash(i, 32) * 30.0 + Math.sin(t * 3.0 + i) * 1.5;
            c.line(xx, yx, xx - 3.0, yx + 0.3, 0.4, AnimatedCapes.alpha(-728912, 0.5));
         }
      }
   }

   static final class FrogPond implements AnimatedCapes.Scene {
      private static final int WATER = 70;
      private static final double[][] PADS = new double[][]{
         {40.0, 96.0, 14.0}, {12.0, 84.0, 9.0}, {66.0, 88.0, 10.0}, {22.0, 114.0, 12.0}, {62.0, 118.0, 11.0}
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.3, 0.55}, new int[]{-12965286, -5219718, -1007504});

         for (int i = 0; i < 18; i++) {
            AnimatedCapes.pine(c, AnimatedCapes.hash(i, 1) * 80.0, 71.0, 10.0 + AnimatedCapes.hash(i, 2) * 12.0, -14016972);
         }

         c.fillBelow(x -> 67.0 + 2.0 * AnimatedCapes.fbm(x * 0.2, 3.0, 2), -14016972);

         for (int y = 70; y < 128; y++) {
            double d = (y - 70) / 58.0;
            int mirror = AnimatedCapes.ramp(1.0 - d * 0.55, new double[]{0.0, 0.3, 0.55}, new int[]{-12965286, -5219718, -1007504});

            for (int x = 0; x < 80; x++) {
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(
                     AnimatedCapes.lerp(AnimatedCapes.lerp(AnimatedCapes.shade(mirror, 0.6), -14009766, 0.45), -15854040, 0.2 + d * 0.7), x, y
                  )
               );
            }
         }

         for (int k = 0; k < 2; k++) {
            double baseX = k == 0 ? 3.0 : 76.0;

            for (int r = 0; r < 6; r++) {
               double rx = baseX + (r - 3) * 1.3;
               double h = 30.0 + AnimatedCapes.hash(r, k) * 18.0;
               c.line(rx, 128.0, rx + (AnimatedCapes.hash(r, k + 5) - 0.5) * 3.0, 128.0 - h, 0.6, -14005718);
               if (r % 2 == 0) {
                  c.ellipse(rx + (AnimatedCapes.hash(r, k + 5) - 0.5) * 2.4, 128.0 - h + 4.0, 1.1, 3.2, 0.0, -10864098);
               }
            }
         }
      }

      private static void pad(AnimatedCapes.Canvas c, double x, double y, double r, int seed) {
         c.ellipse(x + 1.0, y + 1.5, r, r * 0.35, 0.0, AnimatedCapes.alpha(-16777216, 0.3));
         c.ellipse(x, y, r, r * 0.35, 0.0, AnimatedCapes.lerp(-12940742, -10835382, AnimatedCapes.hash(seed, 1)));
         c.polygon(new double[][]{{x, y}, {x + r * 0.9, y - r * 0.12}, {x + r * 0.9, y + r * 0.16}}, AnimatedCapes.lerp(-14009798, -15062486, 0.5));
         c.ellipse(x - r * 0.3, y - r * 0.1, r * 0.4, r * 0.08, 0.0, AnimatedCapes.alpha(-4659040, 0.4));
      }

      private static void frog(AnimatedCapes.Canvas c, double x, double y, double croak, boolean blink) {
         c.ellipse(x - 4.0, y + 1.5, 3.0, 1.6, 0.3, -12940758);
         c.ellipse(x + 4.0, y + 1.5, 3.0, 1.6, -0.3, -12940758);
         c.ellipse(x, y, 5.5, 3.6, 0.0, -11886534);
         c.ellipse(x, y + 1.2, 3.8, 2.0, 0.0, -3612534);
         if (croak > 0.0) {
            c.disc(x, y + 2.8, 1.5 + croak * 2.5, -986960);
         }

         for (int side = -1; side <= 1; side += 2) {
            c.disc(x + side * 2.6, y - 3.0, 1.9, -11886534);
            c.disc(x + side * 2.6, y - 3.2, 1.2, blink ? -11886534 : -6000);
            if (!blink) {
               c.rect(x + side * 2.6 - 0.8, y - 3.5, 1.6, 0.6, -16119286);
            }
         }

         c.line(x - 2.0, y - 0.8, x + 2.0, y - 0.8, 0.4, -14001638);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 71; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double wave = AnimatedCapes.noise(x * 0.3 / (0.3 + (y - 70) / 58.0) + t * 0.4, y * 0.8 - t * 0.6);
               if (wave > 0.72) {
                  c.add(x, y, -20336, (wave - 0.72) * 0.9 * (1.0 - (y - 70) / 70.0));
               }
            }
         }

         for (int p = 0; p < PADS.length; p++) {
            pad(c, PADS[p][0], PADS[p][1] + Math.sin(t * 0.8 + p) * 0.3, PADS[p][2], p);
         }

         double croakCycle = AnimatedCapes.wrap(t, 3.0);
         double croak = croakCycle < 1.2 ? Math.sin(croakCycle / 1.2 * Math.PI) : 0.0;
         frog(c, 40.0, 92.0, croak, AnimatedCapes.wrap(t, 4.3) < 0.15);
         double jump = AnimatedCapes.wrap(t, 5.0);
         double[] from = new double[]{12.0, 81.0};
         double[] to = new double[]{66.0, 85.0};
         boolean forward = (int)Math.floor(t / 5.0) % 2 == 0;
         double[] a = forward ? from : to;
         double[] b = forward ? to : from;
         if (jump < 1.2) {
            double p = jump / 1.2;
            double jx = a[0] + (b[0] - a[0]) * p;
            double jy = a[1] + (b[1] - a[1]) * p - Math.sin(p * Math.PI) * 26.0;
            c.ellipse(jx, jy, 4.0, 2.0, forward ? -0.4 : 0.4, -11886534);
            c.line(jx - 3.0, jy + 1.0, jx - 7 * (forward ? 1 : -1), jy + 3.0, 0.8, -12940758);
            if (p > 0.95) {
               c.ring(b[0], b[1] + 2.0, (p - 0.95) * 100.0, 0.5, AnimatedCapes.alpha(-1, 0.5));
            }
         } else {
            frog(c, b[0], b[1] - 3.0, 0.0, AnimatedCapes.wrap(t + 1.1, 3.7) < 0.15);
            double since = jump - 1.2;
            if (since < 1.0) {
               c.ring(b[0], b[1] + 2.0, 3.0 + since * 8.0, 0.5, AnimatedCapes.alpha(-1, 0.5 * (1.0 - since)));
            }
         }

         for (int d = 0; d < 2; d++) {
            double dart = Math.floor(t * 0.7 + d * 0.5);
            double f = AnimatedCapes.smoothstep(0.0, 0.3, AnimatedCapes.wrap(t * 0.7 + d * 0.5, 1.0));
            double xx = lerp2(10.0 + AnimatedCapes.hash(d, (int)dart) * 60.0, 10.0 + AnimatedCapes.hash(d, (int)dart + 1) * 60.0, f);
            double y = lerp2(40.0 + AnimatedCapes.hash(d + 5, (int)dart) * 30.0, 40.0 + AnimatedCapes.hash(d + 5, (int)dart + 1) * 30.0, f);
            c.line(xx - 3.0, y, xx + 3.0, y, 0.6, -13993296);
            c.ellipse(xx - 0.5, y - 1.5, 2.5, 0.8, 0.3 + Math.sin(t * 40.0) * 0.3, AnimatedCapes.alpha(-2035457, 0.6));
            c.ellipse(xx - 0.5, y + 1.5, 2.5, 0.8, -0.3 - Math.sin(t * 40.0) * 0.3, AnimatedCapes.alpha(-2035457, 0.6));
         }

         for (int i = 0; i < 3; i++) {
            double life = AnimatedCapes.wrap(t * 0.4 + i / 3.0, 1.0);
            int n = (int)Math.floor(t * 0.4 + i / 3.0);
            c.ring(
               10.0 + AnimatedCapes.hash(i, n + 60) * 60.0,
               76.0 + AnimatedCapes.hash(i, n + 61) * 40.0,
               life * 6.0,
               0.5,
               AnimatedCapes.alpha(-7984, 0.4 * (1.0 - life))
            );
         }
      }

      private static double lerp2(double a, double b, double t) {
         return a + (b - a) * t;
      }
   }

   static final class GlowingMushrooms implements AnimatedCapes.Scene {
      private static final double[][] SHROOMS = new double[][]{
         {22.0, 116.0, 58.0, 16.0, 180.0},
         {58.0, 118.0, 42.0, 13.0, 300.0},
         {40.0, 124.0, 22.0, 8.0, 120.0},
         {8.0, 122.0, 18.0, 6.0, 300.0},
         {72.0, 124.0, 14.0, 5.0, 180.0}
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16513004, -16115164, -16247792});

         for (int i = 0; i < 9; i++) {
            double x = AnimatedCapes.hash(i, 1) * 80.0;
            double w = 3.0 + AnimatedCapes.hash(i, 2) * 5.0;
            c.rect(x, 0.0, w, 120.0, AnimatedCapes.lerp(-16116200, -15457238, AnimatedCapes.hash(i, 3)));
            c.line(
               x + w * 0.5,
               30.0 + AnimatedCapes.hash(i, 4) * 30.0,
               x + w * 0.5 + (AnimatedCapes.hash(i, 5) > 0.5 ? 10 : -10),
               20.0 + AnimatedCapes.hash(i, 6) * 30.0,
               1.2,
               -15984100
            );
         }

         c.fillBelow(xx -> 116.0 + 3.0 * AnimatedCapes.fbm(xx * 0.12, 2.0, 3), -15720422);

         for (int y = 112; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (c.get(x, y) == -15720422) {
                  c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-15058390, -16247794, AnimatedCapes.fbm(x * 0.3, y * 0.3, 3)), x, y));
               }
            }
         }

         for (double[] m : SHROOMS) {
            double xx = m[0];
            double baseY = m[1];
            double h = m[2];
            double r = m[3];
            c.polygon(
               new double[][]{{xx - r * 0.18, baseY}, {xx + r * 0.18, baseY}, {xx + r * 0.12, baseY - h}, {xx - r * 0.12, baseY - h}},
               (px, py) -> AnimatedCapes.lerp(-2568000, -7699336, (px - xx + r * 0.18) / (r * 0.36))
            );
            c.ellipse(xx, baseY - h * 0.55, r * 0.22, 1.2, 0.0, -3620688);

            for (int g = -6; g <= 6; g++) {
               c.line(xx, baseY - h + r * 0.2, xx + g * r / 7.0, baseY - h + r * 0.05, 0.4, -9805736);
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < SHROOMS.length; k++) {
            double[] m = SHROOMS[k];
            double x = m[0];
            double capY = m[1] - m[2];
            double r = m[3];
            double pulse = 0.5 + 0.5 * Math.sin(t * 1.3 + k * 1.7);
            int col = AnimatedCapes.hsv(m[4] + Math.sin(t * 0.3 + k) * 20.0, 0.7, 1.0);
            c.glow(x, capY, r * 2.4, col, 0.3 + 0.25 * pulse);

            for (int y = (int)(capY - r * 0.7); y <= capY + 1.0; y++) {
               for (int px = (int)(x - r - 1.0); px <= x + r + 1.0; px++) {
                  double u = (px + 0.5 - x) / r;
                  double v = (capY - (y + 0.5)) / (r * 0.7);
                  if (u * u + v * v <= 1.0 && v >= -0.05) {
                     double light = 0.55 + 0.45 * (1.0 - Math.hypot(u + 0.3, v - 0.5));
                     c.set(px, y, AnimatedCapes.lerp(AnimatedCapes.shade(col, 0.45), col, light * (0.7 + 0.3 * pulse)));
                  }
               }
            }

            for (int s = 0; s < 7; s++) {
               double a = AnimatedCapes.hash(s, k) * Math.PI;
               double d = Math.sqrt(AnimatedCapes.hash(k, s)) * 0.8;
               c.disc(x + Math.cos(a) * d * r, capY - Math.sin(a) * d * r * 0.7, 0.6 + r * 0.06, AnimatedCapes.lerp(col, -1, 0.6));
            }

            c.ellipse(x, capY + 0.3, r, 1.0, 0.0, AnimatedCapes.shade(col, 0.4));
         }

         for (int i = 0; i < 40; i++) {
            int k = i % SHROOMS.length;
            double life = AnimatedCapes.wrap(t * (0.08 + AnimatedCapes.hash(i, 1) * 0.08) + AnimatedCapes.hash(i, 2), 1.0);
            double x = SHROOMS[k][0] + (AnimatedCapes.hash(i, 3) - 0.5) * SHROOMS[k][3] * 2.0 + Math.sin(life * 8.0 + i) * 3.0;
            double y = SHROOMS[k][1] - SHROOMS[k][2] - life * 60.0;
            c.glow(x, y, 2.0, AnimatedCapes.hsv(SHROOMS[k][4], 0.5, 1.0), 0.6 * Math.sin(life * Math.PI));
         }

         for (int y = 104; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.06 + t * 0.15, y * 0.2, 3);
               c.blend(x, y, AnimatedCapes.alpha(-9789280, AnimatedCapes.smoothstep(0.45, 0.75, n) * 0.35));
            }
         }
      }
   }

   static final class Hummingbird implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-7681414, -12944822});
         int[] bokeh = new int[]{-3936, -3084128, -6233920, -12064};

         for (int i = 0; i < 40; i++) {
            double x = AnimatedCapes.hash(i, 1) * 80.0;
            double y = AnimatedCapes.hash(i, 2) * 128.0;
            double r = 3.0 + AnimatedCapes.hash(i, 3) * 8.0;
            c.disc(x, y, r, AnimatedCapes.alpha(bokeh[i % bokeh.length], 0.12 + AnimatedCapes.hash(i, 4) * 0.12));
         }

         c.line(80.0, 0.0, 56.0, 30.0, 1.2, -12953046);
         c.line(66.0, 18.0, 50.0, 40.0, 0.9, -12953046);

         for (int k = 0; k < 7; k++) {
            double lx = 60.0 + AnimatedCapes.hash(k, 5) * 18.0;
            double ly = 4.0 + AnimatedCapes.hash(k, 6) * 30.0;
            c.ellipse(lx, ly, 4.5, 2.0, AnimatedCapes.hash(k, 7) * 3.0, AnimatedCapes.lerp(-13997526, -11888070, AnimatedCapes.hash(k, 8)));
         }
      }

      private static void flower(AnimatedCapes.Canvas c, double x, double y, double sway) {
         double fx = x + sway;
         c.line(x, y - 8.0, fx, y, 0.5, -12953046);

         for (int p = -1; p <= 1; p++) {
            c.ellipse(fx + p * 2.5, y + 1.5, 1.4, 3.0, p * 0.6, -2086294);
         }

         c.polygon(new double[][]{{fx - 2.2, y + 2.0}, {fx + 2.2, y + 2.0}, {fx + 1.4, y + 9.0}, {fx - 1.4, y + 9.0}}, -8770896);
         c.line(fx, y + 9.0, fx - 0.5, y + 13.0, 0.4, -7952);
         c.line(fx + 0.6, y + 9.0, fx + 1.0, y + 12.5, 0.4, -7952);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] flowers = new double[][]{{56.0, 30.0}, {50.0, 42.0}, {64.0, 22.0}};

         for (int k = 0; k < flowers.length; k++) {
            flower(c, flowers[k][0], flowers[k][1], Math.sin(t * 1.2 + k) * 0.8);
         }

         double feed = 0.5 + 0.5 * Math.sin(t * 0.9);
         double bx = 30.0 + feed * 10.0 + Math.sin(t * 3.1) * 0.8;
         double by = 60.0 + Math.sin(t * 2.3) * 2.5 - feed * 6.0;
         double beakX = bx + 9.0;
         double beakY = by - 7.0;
         c.polygon(new double[][]{{bx - 8.0, by + 4.0}, {bx - 15.0, by + 11.0}, {bx - 11.0, by + 12.0}, {bx - 6.0, by + 6.0}}, -14005702);
         c.ellipse(bx - 1.0, by + 1.0, 7.0, 4.0, -0.6, -12935062);
         c.ellipse(bx - 1.0, by + 3.0, 5.0, 2.2, -0.6, -1513256);
         c.disc(bx + 4.5, by - 4.0, 3.3, -12935062);
         c.ellipse(bx + 3.5, by - 1.5, 2.6, 1.6, -0.5, AnimatedCapes.hsv(320.0 + Math.sin(t * 2.0) * 40.0, 0.9, 0.95));
         c.line(bx + 7.0, by - 5.0, beakX + 4.0, beakY - 1.0, 0.6, -15066598);
         c.disc(bx + 5.5, by - 5.0, 0.8, -16119286);
         c.rect(bx + 5.5, by - 5.6, 0.5, 0.5, -1);

         for (int k = 0; k < 7; k++) {
            double a = -2.4707963267948965 + k * 0.3 + Math.sin(t * 60.0 + k) * 0.1;
            c.ellipse(bx - 2.0 + Math.cos(a) * 7.0, by - 3.0 + Math.sin(a) * 7.0, 7.0, 1.6, a, AnimatedCapes.alpha(-4659000, 0.16));
         }

         for (int i = 0; i < 14; i++) {
            double life = AnimatedCapes.wrap(t * 0.6 + AnimatedCapes.hash(i, 30), 1.0);
            double px = 50.0 + (AnimatedCapes.hash(i, 31) - 0.5) * 30.0 + Math.sin(life * 6.0 + i) * 3.0;
            double py = 30.0 + AnimatedCapes.hash(i, 32) * 30.0 + life * 20.0;
            c.star(px, py, 1.0, -3936, 0.8 * Math.sin(life * Math.PI));
         }

         double dart = AnimatedCapes.wrap(t, 7.0);
         if (dart < 0.8) {
            double p = dart / 0.8;
            double x = 90.0 - p * 110.0;
            double y = 100.0 - p * 20.0;
            c.ellipse(x, y, 2.5, 1.2, 0.2, AnimatedCapes.alpha(-13997494, 0.7));
            c.ellipse(x + 0.5, y - 1.5, 3.0, 0.8, -0.3, AnimatedCapes.alpha(-4659000, 0.25));
         }
      }
   }

   static final class RainyWindow implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16118750, -14804422, -14018006});
         int[] lights = new int[]{-20400, -38262, -9778945, -8054, -5207297};

         for (int i = 0; i < 46; i++) {
            double x = AnimatedCapes.hash(i, 1) * 80.0;
            double y = 30.0 + AnimatedCapes.hash(i, 2) * 80.0;
            double r = 2.5 + AnimatedCapes.hash(i, 3) * 6.0;
            int col = lights[i % lights.length];
            c.glow(x, y, r * 1.8, col, 0.12);
            c.disc(x, y, r, AnimatedCapes.alpha(col, 0.18 + AnimatedCapes.hash(i, 4) * 0.2));
            c.ring(x, y, r - 0.5, 0.8, AnimatedCapes.alpha(col, 0.15));
         }

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.blend(x, y, AnimatedCapes.alpha(-7693632, AnimatedCapes.fbm(x * 0.05, y * 0.04, 4) * 0.12));
            }
         }

         c.rect(0.0, 0.0, 4.0, 128.0, -14017516);
         c.rect(76.0, 0.0, 4.0, 128.0, -14017516);
         c.rect(3.0, 0.0, 1.0, 128.0, -11914204);
         c.rect(76.0, 0.0, 1.0, 128.0, -11914204);
         c.rect(0.0, 112.0, 80.0, 16.0, -12965860);
         c.rect(0.0, 112.0, 80.0, 1.5, -10862544);
         c.polygon(new double[][]{{56.0, 112.0}, {68.0, 112.0}, {66.0, 100.0}, {58.0, 100.0}}, -7714256);
         c.rect(56.5, 99.0, 11.0, 2.0, -6268358);

         for (int k = 0; k < 7; k++) {
            double a = (-Math.PI / 2) + (k - 3) * 0.35;
            c.ellipse(62.0 + Math.cos(a) * 6.0, 94.0 + Math.sin(a) * 5.0, 3.5, 1.4, a, AnimatedCapes.lerp(-13997510, -11892150, AnimatedCapes.hash(k, 9)));
         }

         c.rect(14.0, 102.0, 9.0, 10.0, -1515304);
         c.ring(24.0, 106.0, 2.5, 1.2, -1515304);
         c.rect(14.0, 102.0, 9.0, 1.5, -11915232);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 2; k++) {
            double life = AnimatedCapes.wrap(t * 0.08 + k * 0.5, 1.0);
            double x = -10.0 + life * 100.0;
            c.glow(x, 104.0, 5.0, -3904, 0.4);
            c.glow(x + 6.0, 104.0, 5.0, -3904, 0.4);
         }

         for (int k = 0; k < 4; k++) {
            double steam = AnimatedCapes.wrap(t * 0.3 + k / 4.0, 1.0);
            c.disc(18.5 + Math.sin(steam * 6.0 + t) * 1.5, 100.0 - steam * 14.0, 0.8 + steam * 1.5, AnimatedCapes.alpha(-1, 0.25 * (1.0 - steam)));
         }

         int bucket = (int)Math.floor(t / 6.0);

         for (int i = 0; i < 70; i++) {
            double x = 5.0 + AnimatedCapes.hash(i, bucket + 10) * 70.0;
            double y = AnimatedCapes.hash(i, bucket + 11) * 110.0;
            double r = 0.5 + AnimatedCapes.hash(i, bucket + 12) * 0.9;
            c.disc(x, y, r, AnimatedCapes.alpha(-16118758, 0.35));
            c.add((int)(x - r * 0.3), (int)(y - r * 0.3), -1, 0.5);
         }

         for (int d = 0; d < 12; d++) {
            double speed = 8.0 + AnimatedCapes.hash(d, 20) * 14.0;
            double cycle = 140.0;
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(d, 21) * cycle + t * speed, cycle) - 12.0;
            double x = 6.0 + AnimatedCapes.hash(d, 22) * 68.0 + Math.sin(y * 0.15 + d) * 1.2;
            double r = 1.2 + AnimatedCapes.hash(d, 23) * 0.8;

            for (int s = 1; s < 18; s++) {
               double ty = y - s * 1.5;
               double tx = 6.0 + AnimatedCapes.hash(d, 22) * 68.0 + Math.sin(ty * 0.15 + d) * 1.2;
               c.add((int)tx, (int)ty, -7692080, 0.12 * (1.0 - s / 18.0));
            }

            c.disc(x, y, r, AnimatedCapes.alpha(-15460304, 0.55));
            c.ring(x, y, r, 0.5, AnimatedCapes.alpha(-4667152, 0.5));
            c.add((int)(x - r * 0.4), (int)(y - r * 0.4), -1, 0.9);
         }
      }
   }

   static final class Savanna implements AnimatedCapes.Scene {
      private static final int GROUND = 96;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.35, 0.72}, new int[]{-9819574, -2074054, -16288});
         c.glow(46.0, 76.0, 44.0, -24512, 0.55);
         c.disc(46.0, 76.0, 20.0, -10112);
         c.disc(46.0, 76.0, 17.0, -5976);

         for (int k = 0; k < 4; k++) {
            c.ellipse(46.0 + (k - 1.5) * 10.0, 64 + k * 4, 26.0, 0.9, 0.0, AnimatedCapes.alpha(-5223878, 0.6));
         }

         AnimatedCapes.mountains(c, 23, 90.0, 14.0, 0.05, -6665654, 0);
         c.polygon(new double[][]{{50.0, 91.0}, {60.0, 80.0}, {64.0, 79.0}, {70.0, 81.0}, {78.0, 91.0}}, -6268336);
         c.polygon(new double[][]{{59.0, 81.5}, {60.0, 80.0}, {64.0, 79.0}, {70.0, 81.0}, {71.5, 83.0}, {66.0, 82.0}, {63.0, 83.0}}, -993080);

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= 92.0 + 2.0 * Math.sin(x * 0.08)) {
                  c.set(
                     x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-3635142, -10864102, (y - 96) / 32.0 + AnimatedCapes.fbm(x * 0.2, y * 0.4, 2) * 0.2), x, y)
                  );
               }
            }
         }

         int tree = -15069680;
         c.line(18.0, 110.0, 20.0, 72.0, 2.6, tree);
         c.line(20.0, 80.0, 8.0, 64.0, 1.4, tree);
         c.line(20.0, 76.0, 34.0, 62.0, 1.4, tree);
         c.line(20.0, 74.0, 22.0, 60.0, 1.2, tree);
         double[][] canopy = new double[][]{{8.0, 62.0, 12.0, 3.0}, {24.0, 58.0, 16.0, 3.5}, {36.0, 61.0, 10.0, 2.5}, {16.0, 56.0, 10.0, 2.5}};

         for (double[] leaf : canopy) {
            c.ellipse(leaf[0], leaf[1], leaf[2], leaf[3], 0.0, tree);

            for (int i = 0; i < 12; i++) {
               c.disc(
                  leaf[0] + (AnimatedCapes.hash(i, (int)leaf[0]) - 0.5) * leaf[2] * 1.8,
                  leaf[1] - leaf[3] + AnimatedCapes.hash((int)leaf[0], i) * 1.5,
                  1.2,
                  tree
               );
            }
         }
      }

      private static void giraffe(AnimatedCapes.Canvas c, double x, double y, double step, double bob) {
         int col = -15069680;
         c.ellipse(x, y, 5.0, 3.0, -0.15, col);

         for (int leg = 0; leg < 4; leg++) {
            double lx = x - 3.5 + leg * 2.3;
            c.line(lx, y + 1.5, lx + Math.sin(step + leg * 1.6) * 1.4, y + 13.0, 0.8, col);
         }

         double headX = x + 6.0 + bob * 0.4;
         double headY = y - 17.0 + bob;
         c.line(x + 3.5, y - 1.5, headX, headY, 1.6, col);
         c.ellipse(headX + 1.5, headY, 2.2, 1.1, 0.4, col);
         c.line(headX - 0.3, headY - 1.0, headX - 0.6, headY - 3.0, 0.4, col);
         c.line(x - 4.5, y, x - 6.0, y + 5.0, 0.4, col);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 2; k++) {
            double x = AnimatedCapes.wrap(t * 1.8 + k * 22, 130.0) - 15.0;
            giraffe(c, x, 82 + k * 3, t * 2.5 + k, Math.sin(t * 1.2 + k) * 0.8);
         }

         double ex = 105.0 - AnimatedCapes.wrap(t * 1.2 + 30.0, 130.0);
         int col = -15069680;
         c.ellipse(ex, 90.0, 7.0, 4.5, 0.0, col);
         c.disc(ex - 7.0, 88.0, 3.5, col);
         c.ellipse(ex - 5.0, 88.0, 2.2, 3.2, 0.2, col);
         double trunk = Math.sin(t * 0.8) * 1.5;
         c.line(ex - 9.5, 89.0, ex - 10.5 + trunk, 96.0, 1.0, col);

         for (int leg = 0; leg < 4; leg++) {
            double lx = ex - 5.0 + leg * 3.3;
            c.line(lx, 92.0, lx + Math.sin(t * 2.0 + leg * 1.6) * 0.8, 99.0, 1.4, col);
         }

         for (int b = 0; b < 7; b++) {
            double fx = AnimatedCapes.wrap(t * 4.0, 120.0) - 20.0;
            double row = Math.abs(b - 3);
            AnimatedCapes.bird(c, fx - row * 4.0, 30.0 + (b - 3) * 2.2 + row * 0.5, 1.3, t * 8.0 + b, -803598832);
         }

         for (int i = 0; i < 90; i++) {
            double x = AnimatedCapes.hash(i, 70) * 80.0;
            double baseY = 108.0 + AnimatedCapes.hash(i, 71) * 20.0;
            double sway = Math.sin(t * 1.4 + x * 0.1) * 1.5;
            c.line(x, baseY, x + sway, baseY - 5.0 - AnimatedCapes.hash(i, 72) * 6.0, 0.5, AnimatedCapes.lerp(-12966896, -6264278, AnimatedCapes.hash(i, 73)));
         }
      }
   }

   static final class SnowyOwl implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16446946, -15457206, -14799280});

         for (int i = 0; i < 90; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 90.0,
               AnimatedCapes.hash(i, 3) > 0.95 ? 1.0 : 0.0,
               -1511169,
               0.1 + AnimatedCapes.hash(i, 4) * 0.4
            );
         }

         c.glow(64.0, 18.0, 18.0, -7692080, 0.35);
         c.disc(64.0, 18.0, 7.0, -986376);
         c.disc(66.0, 16.0, 1.2, AnimatedCapes.alpha(-3617576, 0.8));

         for (int i = 0; i < 8; i++) {
            double x = AnimatedCapes.hash(i, 10) * 80.0;
            AnimatedCapes.pine(c, x, 132.0, 30.0 + AnimatedCapes.hash(i, 11) * 30.0, -16116696);
         }

         c.line(-2.0, 92.0, 70.0, 86.0, 5.0, -14018030);
         c.line(40.0, 88.0, 80.0, 72.0, 2.4, -14018030);
         c.line(12.0, 91.0, 4.0, 80.0, 1.6, -14018030);

         for (int x = -2; x < 72; x++) {
            double y = 92.0 - (x + 2) * 6.0 / 72.0 - 2.5;
            c.ellipse(x, y, 1.6, 1.2 + AnimatedCapes.fbm(x * 0.2, 1.0, 2), 0.0, -1511174);
         }

         for (int x = 42; x < 80; x++) {
            double y = 88.0 - (x - 40) * 16.0 / 40.0 - 1.5;
            c.ellipse(x, y, 1.2, 0.9, 0.0, -2037516);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double ox = 34.0;
         double oy = 70.0;
         double ruffle = AnimatedCapes.wrap(t, 9.0) < 0.6 ? Math.sin(AnimatedCapes.wrap(t, 9.0) / 0.6 * Math.PI * 4.0) * 0.8 : 0.0;
         c.ellipse(ox, oy + 2.0, 10.0 + Math.abs(ruffle), 14.0, 0.0, -723206);
         c.ellipse(ox - 7.0, oy + 4.0, 4.0, 11.0, 0.15, -2038546);
         c.ellipse(ox + 7.0, oy + 4.0, 4.0, 11.0, -0.15, -2038546);

         for (int i = 0; i < 26; i++) {
            double sx = ox + (AnimatedCapes.hash(i, 20) - 0.5) * 16.0;
            double sy = oy - 2.0 + AnimatedCapes.hash(i, 21) * 16.0;
            c.rect(sx, sy, 1.2, 0.7, AnimatedCapes.alpha(-12961212, 0.6));
         }

         double turn = Math.sin(t * 0.35) > 0.6 ? 3.0 : (Math.sin(t * 0.35) < -0.6 ? -3.0 : 0.0);
         double hx = ox + turn;
         double hy = oy - 12.0;
         c.disc(ox + turn * 0.3, hy + 1.0, 8.0, -460292);
         c.ellipse(hx, hy + 1.0, 6.0, 5.0, 0.0, -1);
         boolean blink = AnimatedCapes.wrap(t, 4.0) < 0.18;

         for (int side = -1; side <= 1; side += 2) {
            double ex = hx + side * 2.8;
            if (blink) {
               c.line(ex - 1.5, hy, ex + 1.5, hy, 0.7, -12961212);
            } else {
               c.disc(ex, hy, 1.9, -12256);
               c.disc(ex, hy, 1.0, -16119282);
               c.rect(ex - 0.8, hy - 1.0, 0.6, 0.6, -1);
            }
         }

         c.polygon(new double[][]{{hx - 0.8, hy + 1.8}, {hx + 0.8, hy + 1.8}, {hx, hy + 3.4}}, -14013904);

         for (int f = -1; f <= 1; f += 2) {
            c.line(ox + f * 2.5, oy + 15.0, ox + f * 2.5, oy + 17.5, 1.0, -11908528);
         }

         c.glow(hx, hy, 14.0, -4667152, 0.08);

         for (int layer = 0; layer < 3; layer++) {
            for (int i = 0; i < 26 + layer * 10; i++) {
               double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, layer * 10 + 40) * 128.0 + t * (5 + layer * 5), 132.0) - 2.0;
               double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, layer * 10 + 41) * 80.0 + Math.sin(t * 0.7 + i) * 2.0 + t * 1.5, 80.0);
               c.disc(x, y, 0.35 + layer * 0.3, AnimatedCapes.alpha(-1, 0.45 + layer * 0.2));
            }
         }
      }
   }

   static final class SunkenShip implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.55, 1.0}, new int[]{-15045990, -16106908, -16375750});
         c.fillBelow(xx -> 110.0 + 3.0 * AnimatedCapes.fbm(xx * 0.1, 1.0, 3), -5203350);

         for (int y = 106; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (c.get(x, y) == -5203350) {
                  c.set(
                     x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-7701928, -11910606, (y - 108) / 20.0 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.3), x, y)
                  );
               }
            }
         }

         c.polygon(new double[][]{{6.0, 112.0}, {14.0, 70.0}, {58.0, 58.0}, {76.0, 74.0}, {70.0, 112.0}}, (xx, yx) -> {
            int plank = (int)((yx + xx * 0.25) / 4.0) % 2 == 0 ? -11912662 : -12964832;
            return AnimatedCapes.shade(plank, 0.75 + AnimatedCapes.fbm(xx * 0.4, yx * 0.1, 2) * 0.4);
         });

         for (int k = 0; k < 9; k++) {
            double y = 72.0 + k * 4.5;
            c.line(10.0 - k * 0.5, y, 74.0 - k * 0.3, y - 10.0 + k * 1.2, 0.4, AnimatedCapes.alpha(-15068146, 0.6));
         }

         c.polygon(new double[][]{{30.0, 78.0}, {40.0, 76.0}, {44.0, 90.0}, {34.0, 94.0}}, -16118252);
         c.line(30.0, 78.0, 40.0, 76.0, 0.6, -14016488);
         c.ring(58.0, 80.0, 3.4, 1.2, -7706054);
         c.disc(58.0, 80.0, 2.8, -16381938);
         c.line(40.0, 60.0, 22.0, 16.0, 2.2, -12964832);
         c.line(31.0, 38.0, 50.0, 32.0, 1.2, -12964832);
         c.polygon(new double[][]{{32.0, 39.0}, {49.0, 33.5}, {52.0, 46.0}, {44.0, 50.0}, {38.0, 44.0}, {34.0, 49.0}}, AnimatedCapes.alpha(-4675448, 0.6));

         for (int i = 0; i < 16; i++) {
            double xx = 12.0 + AnimatedCapes.hash(i, 5) * 56.0;
            double y = 96.0 + AnimatedCapes.hash(i, 6) * 14.0;
            int col = i % 3 == 0 ? -2069878 : (i % 3 == 1 ? -9781088 : -2056118);

            for (int b = 0; b < 3; b++) {
               c.disc(xx + (b - 1) * 1.2, y - b % 2 * 1.4, 0.8, col);
            }
         }

         c.line(70.0, 94.0, 72.0, 116.0, 0.6, -12961216);
         c.line(66.0, 116.0, 78.0, 116.0, 1.4, -11908528);
         c.line(72.0, 108.0, 72.0, 116.0, 1.4, -11908528);
         c.ring(72.0, 106.0, 1.5, 0.8, -11908528);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 110; y++) {
            for (int x = 0; x < 80; x++) {
               double ray = Math.pow(AnimatedCapes.noise((x - y * 0.3) * 0.09 + t * 0.1, 1.1), 4.0);
               c.add(x, y, -7677697, ray * 0.3 * (1.0 - y / 120.0));
            }
         }

         for (int y = 108; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.noise(x * 0.25 + t * 0.4, y * 0.35) + AnimatedCapes.noise(x * 0.25 - t * 0.3 + 3.0, y * 0.35 + t * 0.2);
               c.add(x, y, -2557697, Math.pow(1.0 - Math.abs(n - 1.0), 10.0) * 0.35);
            }
         }

         double open = AnimatedCapes.smoothstep(0.2, 0.6, 0.5 + 0.5 * Math.sin(t * 0.5));
         double chestX = 18.0;
         double chestY = 114.0;
         c.rect(chestX - 7.0, chestY - 6.0, 14.0, 7.0, -9815526);
         c.rect(chestX - 7.0, chestY - 4.0, 14.0, 1.0, -2578374);
         c.rect(chestX - 1.0, chestY - 6.0, 2.0, 3.0, -2578374);
         double lidTilt = open * 5.0;
         c.polygon(
            new double[][]{
               {chestX - 7.0, chestY - 6.0},
               {chestX + 7.0, chestY - 6.0},
               {chestX + 7.0 - lidTilt * 0.4, chestY - 10.0 - lidTilt},
               {chestX - 7.0 - lidTilt * 0.4, chestY - 10.0 - lidTilt * 0.6}
            },
            -8762846
         );
         if (open > 0.1) {
            c.glow(chestX, chestY - 7.0, 14.0 * open, -12208, 0.7 * open);

            for (int g = 0; g < 6; g++) {
               c.disc(chestX - 5.0 + g * 2, chestY - 6.5, 1.0, -8080);
            }
         }

         for (int b = 0; b < 12; b++) {
            double life = AnimatedCapes.wrap(t * 0.35 + AnimatedCapes.hash(b, 10), 1.0);
            double bx = (b % 2 == 0 ? chestX : 36.0) + Math.sin(life * 10.0 + b) * 2.0;
            double by = (b % 2 == 0 ? chestY - 8.0 : 90.0) - life * 100.0;
            c.ring(bx, by, 0.6 + AnimatedCapes.hash(b, 11) * 1.2, 0.4, AnimatedCapes.alpha(-2031617, 0.7));
         }

         double peek = Math.max(0.0, Math.sin(t * 0.6));
         double ex = 58.0 - peek * 8.0;
         c.line(58.0, 80.0, ex, 80.0 + Math.sin(t * 3.0) * 1.5, 2.0, -11900358);
         c.disc(ex, 80.0 + Math.sin(t * 3.0) * 1.5, 1.6, -10847670);
         c.rect(ex - 0.8, 79.0 + Math.sin(t * 3.0) * 1.5, 0.7, 0.7, -8128);

         for (int f = 0; f < 7; f++) {
            double speed = 6.0 + AnimatedCapes.hash(f, 20) * 5.0;
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(f, 21) * 120.0 + t * speed, 120.0) - 20.0;
            double y = 40.0 + AnimatedCapes.hash(f, 22) * 50.0 + Math.sin(t + f) * 3.0;
            int col = AnimatedCapes.hsv(AnimatedCapes.hash(f, 23) * 60.0 + 170.0, 0.5, 0.9);
            c.ellipse(x, y, 2.4, 1.1, 0.0, col);
            c.polygon(new double[][]{{x - 2.0, y}, {x - 4.0, y - 1.2}, {x - 4.0, y + 1.2}}, col);
            c.rect(x + 1.2, y - 0.4, 0.6, 0.6, -16119286);
         }

         for (int i = 0; i < 40; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 128.0 + t * 1.5, 128.0);
            c.add((int)(AnimatedCapes.hash(i, 31) * 80.0 + Math.sin(t * 0.4 + i) * 2.0), (int)y, -4136720, 0.2);
         }
      }
   }

   static final class Tornado implements AnimatedCapes.Scene {
      private static final int GROUND = 96;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 96; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05, y * 0.07, 5);
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(
                     AnimatedCapes.lerp(AnimatedCapes.lerp(-14009804, -9799070, n), -7693712, AnimatedCapes.smoothstep(50.0, 96.0, y) * 0.5), x, y
                  )
               );
            }
         }

         for (int y = 20; y < 36; y++) {
            for (int x = 0; x < 80; x++) {
               c.blend(x, y, AnimatedCapes.alpha(-15064546, 0.5 * (1.0 - Math.abs(y - 28) / 8.0) * AnimatedCapes.fbm(x * 0.1, 4.0, 3)));
            }
         }

         for (int y = 96; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double row = Math.sin((x - 40) * 18.0 / (y - 80) + 0.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-5729734, -8754648, 0.5 + 0.3 * row + (y - 96) / 80.0), x, y));
            }
         }

         c.rect(8.0, 88.0, 10.0, 8.0, -12967904);
         c.polygon(new double[][]{{7.0, 88.5}, {19.0, 88.5}, {13.0, 83.0}}, -14018536);
         c.rect(22.0, 91.0, 6.0, 5.0, -14013910);
         c.polygon(new double[][]{{21.0, 91.5}, {29.0, 91.5}, {25.0, 88.0}}, -14803426);
         c.line(66.0, 96.0, 66.0, 80.0, 1.0, -14013910);

         for (int x = 0; x < 80; x += 4) {
            c.line(x, 100.0, x, 104.0, 0.5, -11912662);
         }

         c.line(0.0, 101.0, 80.0, 101.0, 0.5, -11912662);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double drift = Math.sin(t * 0.15) * 10.0;

         for (int y = 30; y < 98; y++) {
            double f = (y - 30) / 66.0;
            double cx = 44.0 + drift * f + Math.sin(y * 0.06 + t * 0.9) * 4.0 * f;
            double width = 16.0 - 13.0 * Math.pow(f, 0.6);

            for (int x = (int)(cx - width - 2.0); x <= cx + width + 2.0; x++) {
               double u = (x + 0.5 - cx) / width;
               if (!(Math.abs(u) > 1.15)) {
                  double swirl = Math.sin(Math.asin(AnimatedCapes.clamp(u, -1.0, 1.0)) * 3.0 + t * 9.0 + y * 0.35);
                  int col = AnimatedCapes.lerp(-12959688, -7696252, 0.5 + 0.35 * swirl - u * 0.2);
                  double a = AnimatedCapes.clamp((1.15 - Math.abs(u)) * 3.0, 0.0, 1.0) * (0.75 + 0.2 * AnimatedCapes.fbm(x * 0.2, y * 0.2 - t * 2.0, 2));
                  c.blend(x, y, AnimatedCapes.alpha(col, a));
               }
            }
         }

         double baseX = 44.0 + drift;

         for (int y = 82; y < 108; y++) {
            for (int xx = (int)baseX - 20; xx < baseX + 20.0; xx++) {
               double d = Math.hypot((xx - baseX) / 18.0, (y - 96) / 9.0);
               if (d < 1.0) {
                  double n = AnimatedCapes.fbm(xx * 0.15 + t * 1.5, y * 0.2, 3);
                  c.blend(xx, y, AnimatedCapes.alpha(-8754614, AnimatedCapes.smoothstep(0.4, 0.7, n) * (1.0 - d) * 0.9));
               }
            }
         }

         for (int i = 0; i < 22; i++) {
            double h = AnimatedCapes.hash(i, 1);
            double y = 94.0 - h * 60.0;
            double a = t * (4.0 - h * 2.0) + i * 1.7;
            double r = 5.0 + (1.0 - h) * 3.0 + h * 16.0;
            double xxx = baseX - drift * h + Math.cos(a) * r;
            double depth = Math.sin(a);
            if (depth > -0.3) {
               c.rect(xxx, y + Math.sin(a * 0.5) * 2.0, 1.0 + AnimatedCapes.hash(i, 2), 1.0, AnimatedCapes.hash(i, 3) > 0.5 ? -12965350 : -9807302);
            }
         }

         double spin = t * 7.0;

         for (int b = 0; b < 4; b++) {
            double a = spin + b * Math.PI / 2.0;
            c.line(66.0, 80.0, 66.0 + Math.cos(a) * 5.0, 80.0 + Math.sin(a) * 5.0, 0.6, -14013910);
         }

         for (int xxx = 0; xxx < 80; xxx++) {
            double wave = Math.sin(xxx * 0.4 + t * 5.0) * 1.5;
            c.line(xxx, 99.0 + wave, xxx + 0.5, 97.0 + wave, 0.5, AnimatedCapes.alpha(-3624880, 0.4));
         }

         if (AnimatedCapes.wrap(t, 5.0) < 0.15) {
            for (int p = 0; p < c.px.length; p++) {
               c.add(p % 80, p / 80, -4140864, 0.25);
            }

            c.beam(18.0 + AnimatedCapes.hash((int)(t / 5.0), 1) * 50.0, 30.0, 20.0 + AnimatedCapes.hash((int)(t / 5.0), 2) * 50.0, 60.0, 0.8, -2035488, 0.9);
         }

         for (int ix = 0; ix < 60; ix++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(ix, 50) * 128.0 + t * 70.0, 128.0);
            double xxx = AnimatedCapes.wrap(AnimatedCapes.hash(ix, 51) * 80.0 + t * 20.0, 80.0);
            c.line(xxx, y, xxx + 1.5, y + 3.5, 0.4, AnimatedCapes.alpha(-6246240, 0.25));
         }
      }
   }
}
