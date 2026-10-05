package dev.lyfw.lyfwclient.render;

final class CapeScenesJ {
   private CapeScenesJ() {
   }

   static final class ChristmasTree implements AnimatedCapes.Scene {
      private static final double[][] LIGHTS = buildLights();

      private static double[][] buildLights() {
         double[][] lights = new double[46][];

         for (int i = 0; i < lights.length; i++) {
            double f = (i + 0.5) / lights.length;
            double y = 30.0 + f * 76.0;
            double halfWidth = 2.0 + f * 24.0;
            double x = 40.0 + Math.sin(f * 30.0) * halfWidth * 0.9;
            lights[i] = new double[]{x, y, Math.cos(f * 30.0)};
         }

         return lights;
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, x / 6 % 2 == 0 ? -12965318 : -13360076);
            }
         }

         c.rect(50.0, 10.0, 26.0, 36.0, -10864094);
         c.rect(52.0, 12.0, 22.0, 32.0, -16116688);
         c.rect(62.5, 12.0, 1.0, 32.0, -10864094);
         c.rect(52.0, 27.5, 22.0, 1.0, -10864094);
         c.rect(52.0, 40.0, 22.0, 4.0, -1511176);

         for (int y = 112; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, (x + y / 4 * 7) % 14 < 1 ? -12966892 : AnimatedCapes.lerp(-9813464, -10865120, AnimatedCapes.hash((x + y / 4 * 7) / 14, y / 4)));
            }
         }

         c.rect(37.0, 104.0, 6.0, 8.0, -11915752);

         for (int tier = 0; tier < 5; tier++) {
            double top = 26 + tier * 15;
            double bottom = top + 22.0;
            double half = 8.0 + tier * 5.5;
            c.polygon(new double[][]{{40.0, top}, {40.0 + half, bottom}, {40.0 - half, bottom}}, (x, y) -> {
               double shade = 0.7 + 0.35 * (1.0 - Math.abs(x - 40) / half) - (x > 40 ? 0.15 : 0.0);
               int col = AnimatedCapes.shade(-13993414, shade);
               return (x * 3 + y * 5) % 7 == 0 ? AnimatedCapes.shade(col, 0.75) : col;
            });

            for (int n = 0; n < half * 1.6; n++) {
               double nx = 40.0 - half + AnimatedCapes.hash(n, tier) * half * 2.0;
               c.line(nx, bottom, nx + (AnimatedCapes.hash(tier, n) - 0.5) * 2.0, bottom + 1.5, 0.5, -14788054);
            }
         }

         double[][] gifts = new double[][]{
            {24.0, 104.0, 12.0, 8.0, -4183494.0, -995264.0},
            {50.0, 102.0, 14.0, 10.0, -1.4001472E7, -1513240.0},
            {38.0, 106.0, 9.0, 6.0, -1.398519E7, -2082230.0},
            {62.0, 106.0, 8.0, 6.0, -2056144.0, -6280512.0}
         };

         for (double[] g : gifts) {
            c.rect(g[0] - g[2] / 2.0, g[1], g[2], g[3], (int)g[4]);
            c.rect(g[0] - 0.8, g[1], 1.6, g[3], (int)g[5]);
            c.rect(g[0] - g[2] / 2.0, g[1] + g[3] / 2.0 - 0.8, g[2], 1.6, (int)g[5]);
            c.ellipse(g[0] - 1.8, g[1] - 0.8, 1.8, 1.1, 0.4, (int)g[5]);
            c.ellipse(g[0] + 1.8, g[1] - 0.8, 1.8, 1.1, -0.4, (int)g[5]);
         }

         double[][] baubles = new double[][]{
            {34.0, 58.0, -2082230.0},
            {50.0, 74.0, -1523648.0},
            {28.0, 88.0, -1.2940576E7},
            {48.0, 96.0, -2082230.0},
            {44.0, 50.0, -1.2940576E7},
            {58.0, 92.0, -1523648.0},
            {24.0, 100.0, -5222176.0}
         };

         for (double[] b : baubles) {
            c.disc(b[0], b[1], 2.4, (int)b[2]);
            c.disc(b[0] - 0.8, b[1] - 0.8, 0.7, -1);
            c.rect(b[0] - 0.6, b[1] - 3.2, 1.2, 1.0, -2572160);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int i = 0; i < 30; i++) {
            double y = 12.0 + AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) * 30.0 + t * (3.0 + AnimatedCapes.hash(i, 2) * 3.0), 28.0);
            double x = 52.0 + AnimatedCapes.hash(i, 3) * 22.0 + Math.sin(t + i) * 1.0;
            c.disc(x, y, 0.5, -1);
         }

         int[] colors = new int[]{-46502, -11869953, -8118, -9765014, -34080};
         double chase = t * 1.5;

         for (int i = 0; i < LIGHTS.length; i++) {
            double[] l = LIGHTS[i];
            if (!(l[2] < -0.2)) {
               int col = colors[i % colors.length];
               double on = 0.35 + 0.65 * Math.pow(0.5 + 0.5 * Math.sin(chase - i * 0.5), 3.0);
               c.glow(l[0], l[1], 3.5, col, on * 0.8);
               c.disc(l[0], l[1], 0.8, AnimatedCapes.lerp(AnimatedCapes.shade(col, 0.5), -1, on * 0.6));
            }
         }

         double pulse = 0.7 + 0.3 * Math.sin(t * 2.0);
         c.glow(40.0, 24.0, 16.0, -8080, 0.5 * pulse);
         double[][] star = new double[10][];

         for (int k = 0; k < 10; k++) {
            double a = (-Math.PI / 2) + k * Math.PI / 5.0 + Math.sin(t * 0.5) * 0.05;
            double r = k % 2 == 0 ? 5.5 : 2.3;
            star[k] = new double[]{40.0 + Math.cos(a) * r, 24.0 + Math.sin(a) * r};
         }

         c.polygon(star, -10166);
         c.disc(40.0, 24.0, 1.2, -32);
         c.beam(40.0 - 10.0 * pulse, 24.0, 40.0 + 10.0 * pulse, 24.0, 0.4, -3904, 0.5);
         c.beam(40.0, 14.0 - 2.0 * pulse, 40.0, 34.0 + 2.0 * pulse, 0.4, -3904, 0.4);

         for (int ix = 0; ix < 10; ix++) {
            int tick = (int)Math.floor(t * 1.5 + AnimatedCapes.hash(ix, 20) * 5.0);
            double f = AnimatedCapes.hash(ix, tick + 21);
            double y = 30.0 + f * 76.0;
            double x = 40.0 + (AnimatedCapes.hash(ix, tick + 22) - 0.5) * (4.0 + f * 44.0);
            c.star(x, y, 1.0, -1, 0.8 * Math.sin(AnimatedCapes.wrap(t * 1.5 + AnimatedCapes.hash(ix, 20) * 5.0, 1.0) * Math.PI));
         }

         c.glow(40.0, 80.0, 50.0, -20384, 0.08 + 0.02 * Math.sin(t * 3.0));
      }
   }

   static final class Coffee implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 76.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-14018030, -11915746});
         int[] bokeh = new int[]{-14224, -24496, -8032, -2061728};

         for (int i = 0; i < 36; i++) {
            double x = AnimatedCapes.hash(i, 1) * 80.0;
            double y = AnimatedCapes.hash(i, 2) * 60.0;
            double r = 2.5 + AnimatedCapes.hash(i, 3) * 6.0;
            c.disc(x, y, r, AnimatedCapes.alpha(bokeh[i % bokeh.length], 0.12 + AnimatedCapes.hash(i, 4) * 0.18));
         }

         for (int y = 88; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double grain = Math.sin(x * 0.2 + AnimatedCapes.fbm(x * 0.05, y * 0.3, 3) * 6.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-9813464, -7710154, 0.5 + 0.3 * grain - (y - 88) / 120.0), x, y));
            }
         }

         c.ellipse(40.0, 100.0, 30.0, 7.0, 0.0, AnimatedCapes.alpha(-16777216, 0.3));
         c.ellipse(40.0, 98.0, 28.0, 6.5, 0.0, -1514276);
         c.ellipse(40.0, 97.0, 22.0, 4.5, 0.0, -461072);
         c.polygon(
            new double[][]{{23.0, 76.0}, {57.0, 76.0}, {53.0, 97.0}, {27.0, 97.0}},
            (xx, yx) -> AnimatedCapes.lerp(-1, -3619652, AnimatedCapes.clamp((xx - 40.0 + 17.0) / 34.0, 0.0, 1.0) * 0.8)
         );
         c.ellipse(40.0, 97.0, 13.0, 3.0, 0.0, -2566964);
         c.ring(60.0, 85.0, 5.0, 2.2, -987416);
         c.ellipse(40.0, 76.0, 17.0, 5.0, 0.0, -1514276);
         c.ellipse(68.0, 110.0, 10.0, 3.5, 0.0, -987932);
         c.polygon(new double[][]{{60.0, 108.0}, {66.0, 102.0}, {72.0, 102.0}, {77.0, 107.0}, {70.0, 110.0}}, -2582982);

         for (int k = 0; k < 4; k++) {
            c.line(62.0 + k * 3.5, 108.0, 65 + k * 3, 102.5, 0.5, -5740000);
         }

         for (int k = 0; k < 3; k++) {
            c.rect(8 + k * 5, 104 - k % 2 * 4, 4.0, 4.0, -460556);
            c.rect(8 + k * 5, 104 - k % 2 * 4, 4.0, 1.0, -1);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double spin = t * 0.15;

         for (int y = 71; y <= 81.0; y++) {
            for (int x = 24; x <= 56.0; x++) {
               double u = (x + 0.5 - 40.0) / 15.0;
               double v = (y + 0.5 - 76.0) / 4.2;
               if (!(u * u + v * v > 1.0)) {
                  double lx = u * Math.cos(spin) - v * Math.sin(spin);
                  double ly = u * Math.sin(spin) + v * Math.cos(spin);
                  double hx = lx * 1.4;
                  double hy = -ly * 1.4 + 0.25;
                  double heart = Math.pow(hx * hx + hy * hy - 0.35, 3.0) - hx * hx * Math.pow(hy, 3.0) * 0.9;
                  int col = AnimatedCapes.lerp(-7714272, -4688838, 0.5 + 0.5 * Math.sin(Math.hypot(u, v) * 9.0 - t));
                  if (heart < 0.0) {
                     col = AnimatedCapes.lerp(-727864, -2045792, Math.hypot(hx, hy));
                  }

                  c.set(x, y, col);
               }
            }
         }

         c.ring(40.0, 76.0, 15.5, 0.8, AnimatedCapes.alpha(-9815526, 0.6));
         double stir = AnimatedCapes.wrap(t, 9.0);
         if (stir < 2.5) {
            double a = stir * 8.0;
            double sx = 40.0 + Math.cos(a) * 8.0;
            double sy = 76.0 + Math.sin(a) * 2.0;
            c.line(sx, sy, sx + 10.0, sy - 18.0, 1.2, -3618608);
            c.ellipse(sx, sy, 2.0, 0.8, 0.0, -5723984);
            c.ring(40.0, 76.0, 6.0 + stir * 2.0, 0.4, AnimatedCapes.alpha(-727864, 0.4 * (1.0 - stir / 2.5)));
         }

         for (int w = 0; w < 3; w++) {
            for (int y = 0; y < 70; y++) {
               double f = (70 - y) / 70.0;
               double xx = 34.0 + w * 6 + Math.sin(y * 0.12 + t * 1.2 + w * 2) * (2.0 + f * 6.0);
               double a = 0.25 * Math.sin(f * Math.PI) * (0.6 + 0.4 * AnimatedCapes.noise(y * 0.1 - t, w));
               c.disc(xx, y, 1.2 + f * 1.8, AnimatedCapes.alpha(-1, a * 0.35));
            }
         }

         for (int i = 0; i < 8; i++) {
            int tick = (int)Math.floor(t * 0.5 + AnimatedCapes.hash(i, 20) * 4.0);
            c.star(
               AnimatedCapes.hash(i, tick + 21) * 80.0,
               AnimatedCapes.hash(i, tick + 22) * 60.0,
               1.0,
               -5968,
               0.5 * Math.sin(AnimatedCapes.wrap(t * 0.5 + AnimatedCapes.hash(i, 20) * 4.0, 1.0) * Math.PI)
            );
         }
      }
   }

   static final class DnaHelix implements AnimatedCapes.Scene {
      private static final int[] PAIRS = new int[]{-42374, -11869953, -12214, -8716438};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-16510934, -16119264});

         for (int row = 0; row < 24; row++) {
            for (int col = 0; col < 12; col++) {
               double hx = col * 8 + row % 2 * 4;
               double hy = row * 7;

               for (int k = 0; k < 6; k++) {
                  double a0 = k * Math.PI / 3.0;
                  double a1 = a0 + (Math.PI / 3);
                  c.line(
                     hx + Math.cos(a0) * 4.0,
                     hy + Math.sin(a0) * 4.0,
                     hx + Math.cos(a1) * 4.0,
                     hy + Math.sin(a1) * 4.0,
                     0.3,
                     AnimatedCapes.alpha(-14005622, 0.2)
                  );
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double scroll = t * 6.0;
         double spin = t * 1.2;
         double amp = 18.0;

         for (int pass = 0; pass < 2; pass++) {
            for (double y = -8.0; y < 136.0; y++) {
               double wy = y + scroll;
               double a = wy * 0.11 + spin;

               for (int strand = 0; strand < 2; strand++) {
                  double phase = a + strand * Math.PI;
                  double depth = Math.sin(phase);
                  if (depth < 0.0 == (pass == 0)) {
                     double x = 40.0 + Math.cos(phase) * amp;
                     double r = 1.6 + depth * 0.7;
                     int col = strand == 0 ? -9789185 : -4166913;
                     c.disc(x, y, r, AnimatedCapes.shade(col, 0.55 + 0.45 * (depth + 1.0) / 2.0));
                  }
               }
            }

            if (pass == 0) {
               for (double wy = Math.floor(scroll / 6.0) * 6.0 - 12.0; wy < scroll + 128.0 + 12.0; wy += 6.0) {
                  double y = wy - scroll;
                  double a = wy * 0.11 + spin;
                  double x0 = 40.0 + Math.cos(a) * amp;
                  double x1 = 40.0 + Math.cos(a + Math.PI) * amp;
                  int pair = (int)(AnimatedCapes.hash((int)(wy / 6.0), 7) * 4.0);
                  double mid = (x0 + x1) / 2.0;
                  double depth = Math.cos(a) * 0.5 + 0.5;
                  c.line(x0, y, mid, y, 1.2, AnimatedCapes.shade(PAIRS[pair], 0.5 + depth * 0.5));
                  c.line(mid, y, x1, y, 1.2, AnimatedCapes.shade(PAIRS[3 - pair], 0.5 + (1.0 - depth) * 0.5));
               }
            }
         }

         for (int y = 0; y < 128; y += 3) {
            double wy = y + scroll;

            for (int strandx = 0; strandx < 2; strandx++) {
               double phase = wy * 0.11 + spin + strandx * Math.PI;
               if (Math.sin(phase) > 0.6) {
                  c.glow(40.0 + Math.cos(phase) * amp, y, 3.0, strandx == 0 ? -9789185 : -4166913, 0.25);
               }
            }
         }

         for (int i = 0; i < 20; i++) {
            double life = AnimatedCapes.wrap(t * 0.1 + AnimatedCapes.hash(i, 10), 1.0);
            double x = AnimatedCapes.hash(i, 11) * 80.0;
            double y = 128.0 - life * 128.0;
            c.glow(x, y, 1.5, -7675649, 0.5 * Math.sin(life * Math.PI));
         }
      }
   }

   static final class FerrisWheel implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 56.0;
      private static final double R = 30.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.8}, new int[]{-16381922, -14017976});

         for (int i = 0; i < 100; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 90.0, 0.0, -1512193, 0.1 + AnimatedCapes.hash(i, 3) * 0.35);
         }

         double[][] tents = new double[][]{{8.0, 112.0, 12.0}, {70.0, 110.0, 14.0}};

         for (double[] tent : tents) {
            c.polygon(
               new double[][]{{tent[0] - tent[2] / 2.0, tent[1]}, {tent[0] + tent[2] / 2.0, tent[1]}, {tent[0], tent[1] - tent[2] * 0.8}},
               (x, y) -> (int)((x - tent[0]) / 2.5 + 20.0) % 2 == 0 ? -4183494 : -1515304
            );
            c.rect(tent[0] - tent[2] / 2.0, tent[1], tent[2], 8.0, -7726550);
         }

         c.rect(0.0, 118.0, 80.0, 10.0, -15462374);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         c.glow(40.0, 56.0, 46.0, -40800, 0.18);
         c.line(22.0, 118.0, 40.0, 56.0, 1.6, -12961206);
         c.line(58.0, 118.0, 40.0, 56.0, 1.6, -12961206);
         c.line(30.0, 90.0, 50.0, 90.0, 1.0, -12961206);
         double rot = t * 0.25;
         c.ring(40.0, 56.0, 30.0, 1.2, -7697760);
         c.ring(40.0, 56.0, 26.0, 0.6, -9803136);

         for (int s = 0; s < 12; s++) {
            double a = rot + s * Math.PI / 6.0;
            c.line(40.0, 56.0, 40.0 + Math.cos(a) * 30.0, 56.0 + Math.sin(a) * 30.0, 0.5, -9803136);
         }

         for (int b = 0; b < 36; b++) {
            double a = rot + b * Math.PI / 18.0;
            double chase = AnimatedCapes.wrap(b / 36.0 - t * 0.4, 1.0);
            int col = AnimatedCapes.hsv(b * 10 + t * 30.0, 0.7, 1.0);
            double on = chase < 0.3 ? 1.0 : 0.35;
            c.glow(40.0 + Math.cos(a) * 30.0, 56.0 + Math.sin(a) * 30.0, 2.2, col, on * 0.9);
         }

         c.disc(40.0, 56.0, 3.0, -5197632);
         c.glow(40.0, 56.0, 5.0, -8032, 0.6);
         int[] cars = new int[]{-2082230, -12930848, -999360, -10432416, -5218080, -30150};

         for (int g = 0; g < 6; g++) {
            double a = rot + g * Math.PI / 3.0;
            double px = 40.0 + Math.cos(a) * 30.0;
            double py = 56.0 + Math.sin(a) * 30.0;
            double swing = Math.sin(t * 1.3 + g) * 0.15;
            double gx = px + Math.sin(swing) * 6.0;
            double gy = py + Math.cos(swing) * 6.0;
            c.line(px, py, gx, gy - 2.0, 0.4, -6645072);
            c.polygon(new double[][]{{gx - 4.0, gy - 2.0}, {gx + 4.0, gy - 2.0}, {gx + 3.0, gy + 3.0}, {gx - 3.0, gy + 3.0}}, cars[g]);
            c.rect(gx - 4.5, gy - 3.0, 9.0, 1.2, AnimatedCapes.shade(cars[g], 0.7));
            c.rect(gx - 2.0, gy - 1.0, 4.0, 1.6, -5984);
         }

         for (int k = 0; k < 2; k++) {
            double x0 = k == 0 ? 2.0 : 63.0;
            double x1 = k == 0 ? 14.0 : 77.0;
            double y0 = 112.0 - (k == 0 ? 9.6 : 11.2);

            for (int i = 0; i <= 8; i++) {
               double f = i / 8.0;
               double x = (k == 0 ? -4 : 60) + f * 20.0;
               double y = y0 + 6.0 + Math.sin(f * Math.PI) * 3.0;
               boolean lit = AnimatedCapes.hash(i + k * 10, (int)Math.floor(t * 3.0)) > 0.3;
               c.glow(x, y, 1.5, lit ? -8080 : -7706064, lit ? 0.9 : 0.3);
            }

            c.rect(x0, 118.0, x1 - x0, 1.0, -16777216);
         }

         double pop = AnimatedCapes.wrap(t, 4.0);
         if (pop > 1.0 && pop < 2.8) {
            double p = (pop - 1.0) / 1.8;
            int n = (int)Math.floor(t / 4.0);
            double bx = AnimatedCapes.hash(n, 1) > 0.5 ? 12.0 : 68.0;
            double by = 16.0 + AnimatedCapes.hash(n, 2) * 12.0;

            for (int s = 0; s < 16; s++) {
               double a = s * Math.PI / 8.0;
               c.add(
                  (int)(bx + Math.cos(a) * p * 9.0),
                  (int)(by + Math.sin(a) * p * 9.0 + p * p * 4.0),
                  AnimatedCapes.hsv(AnimatedCapes.hash(n, 3) * 360.0, 0.6, 1.0),
                  1.0 - p
               );
            }
         }
      }
   }

   static final class Fireplace implements AnimatedCapes.Scene {
      private static final int FX0 = 20;
      private static final int FX1 = 60;
      private static final int FY0 = 58;
      private static final int FY1 = 100;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               boolean stripe = x / 5 % 2 == 0;
               c.set(x, y, stripe ? -10868178 : -11656152);
            }
         }

         for (int y = 38; y < 108; y++) {
            int row = y / 4;

            for (int x = 8; x < 72; x++) {
               boolean mortar = y % 4 == 0 || (x + row % 2 * 4) % 8 == 0;
               c.set(x, y, mortar ? -9807280 : AnimatedCapes.lerp(-7718358, -6272460, AnimatedCapes.hash((x + row % 2 * 4) / 8, row)));
            }
         }

         c.rect(4.0, 34.0, 72.0, 5.0, -10864094);
         c.rect(4.0, 34.0, 72.0, 1.0, -7710156);
         c.rect(4.0, 38.0, 72.0, 1.0, -12966892);

         for (int y = 58; y < 100; y++) {
            for (int x = 20; x < 60; x++) {
               double arch = y - 58 < 6 ? Math.sqrt(Math.max(0.0, 1.0 - Math.pow((x - 40) / 20.0, 2.0))) * 6.0 : 6.0;
               if (y - 58 >= 6.0 - arch) {
                  c.set(x, y, AnimatedCapes.lerp(-16120316, -15069688, (y - 58) / 42.0));
               }
            }
         }

         c.polygon(new double[][]{{24.0, 98.0}, {56.0, 94.0}, {57.0, 98.0}, {25.0, 102.0}}, -11915750);
         c.polygon(new double[][]{{26.0, 94.0}, {54.0, 99.0}, {53.0, 102.0}, {25.0, 97.0}}, -10864094);
         c.ellipse(24.5, 100.0, 1.5, 2.0, 0.0, -3630486);
         c.ellipse(55.0, 96.0, 1.5, 2.0, 0.0, -3630486);
         c.rect(0.0, 112.0, 80.0, 16.0, -12966888);
         c.ellipse(40.0, 120.0, 34.0, 7.0, 0.0, -9819590);
         c.ellipse(40.0, 120.0, 30.0, 5.5, 0.0, -7718326);
         c.ellipse(40.0, 120.0, 26.5, 4.8, 0.0, -3104688);
         c.ellipse(40.0, 120.0, 25.5, 4.1, 0.0, -7718326);
         c.rect(34.0, 26.0, 12.0, 8.0, -12966892);
         c.disc(40.0, 30.0, 3.0, -988968);
         c.line(40.0, 30.0, 40.0, 28.0, 0.4, -15066598);
         c.line(40.0, 30.0, 41.5, 30.5, 0.4, -15066598);
         c.rect(12.0, 24.0, 4.0, 10.0, -988968);
         c.rect(64.0, 26.0, 4.0, 8.0, -988968);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double flicker = AnimatedCapes.noise(t * 5.0, 1.0);

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double d = Math.hypot(x - 40, (y - 90) * 1.3) / 70.0;
               if (d < 1.0) {
                  c.add(x, y, -30160, (1.0 - d) * (0.1 + 0.06 * flicker));
               }
            }
         }

         for (int y = 64; y < 98; y++) {
            for (int xx = 22; xx < 58; xx++) {
               double center = 1.0 - Math.abs(xx - 40) / 18.0;
               double heat = AnimatedCapes.fbm(xx * 0.15, y * 0.12 + t * 2.4, 4) + center * 0.45 - (98 - y) / 38.0;
               if (heat > 0.35) {
                  c.blend(
                     xx,
                     y,
                     AnimatedCapes.alpha(
                        AnimatedCapes.ramp(heat, new double[]{0.35, 0.5, 0.7, 0.9}, new int[]{-7726584, -2076646, -20416, -2880}),
                        AnimatedCapes.clamp((heat - 0.35) * 5.0, 0.0, 1.0)
                     )
                  );
               }
            }
         }

         for (int k = 0; k < 8; k++) {
            c.add(28 + k * 3, 99 - k % 2, -38374, 0.5 + 0.5 * AnimatedCapes.noise(t * 3.0 + k, k));
         }

         for (int i = 0; i < 12; i++) {
            double life = AnimatedCapes.wrap(t * 0.8 + AnimatedCapes.hash(i, 10), 1.0);
            double xxx = 40.0 + (AnimatedCapes.hash(i, 11) - 0.5) * 24.0 + Math.sin(life * 9.0 + i) * 3.0;
            double y = 92.0 - life * 34.0;
            if (y > 60.0) {
               c.add((int)xxx, (int)y, -16288, 1.0 - life);
            }
         }

         for (int s = 0; s < 3; s++) {
            double sx = 18 + s * 22;
            double sway = Math.sin(t * 0.8 + s) * 0.6;
            int sock = s == 1 ? -13993414 : -4183510;
            c.rect(sx - 3.0 + sway * 0.3, 39.0, 6.0, 3.0, -724760);
            c.polygon(
               new double[][]{
                  {sx - 2.6 + sway * 0.3, 42.0},
                  {sx + 2.6 + sway * 0.3, 42.0},
                  {sx + 2.6 + sway, 52.0},
                  {sx + 6.0 + sway, 54.0},
                  {sx + 5.0 + sway, 57.0},
                  {sx - 2.0 + sway, 56.0}
               },
               sock
            );
            c.rect(sx - 2.0 + sway, 47.0, 4.0, 1.0, AnimatedCapes.alpha(-1, 0.4));
         }

         double breath = 1.0 + 0.05 * Math.sin(t * 1.6);
         c.ellipse(46.0, 116.0, 9.0 * breath, 4.5 * breath, 0.0, -1535942);
         c.ellipse(46.0, 114.0, 7.0 * breath, 2.6, 0.0, -1005488);

         for (int s = 0; s < 4; s++) {
            c.line(40.0 + s * 3.5, 112.5, 41.0 + s * 3.5, 118.0, 0.6, -4165590);
         }

         c.disc(37.0, 116.0, 3.5, -1535942);
         c.polygon(new double[][]{{34.0, 114.0}, {35.5, 110.5}, {37.0, 113.5}}, -1535942);
         c.polygon(new double[][]{{37.5, 113.0}, {39.5, 110.0}, {40.0, 113.5}}, -1535942);
         c.line(35.5, 116.5, 37.0, 116.8, 0.4, -10864102);
         double twitch = Math.sin(t * 0.7) > 0.8 ? Math.sin(t * 12.0) * 1.5 : 0.0;
         c.line(55.0, 118.0, 60.0, 121.0 + twitch, 1.4, -1535942);
         c.line(60.0, 121.0 + twitch, 64.0, 119.0 + twitch, 1.1, -4165590);

         for (int z = 0; z < 3; z++) {
            double life = AnimatedCapes.wrap(t * 0.3 + z / 3.0, 1.0);
            double zx = 34.0 + life * 6.0;
            double zy = 108.0 - life * 16.0;
            int col = AnimatedCapes.alpha(-1, 0.7 * Math.sin(life * Math.PI));
            double s = 1.0 + life;
            c.line(zx, zy, zx + 2.0 * s, zy, 0.4, col);
            c.line(zx + 2.0 * s, zy, zx, zy + 2.0 * s, 0.4, col);
            c.line(zx, zy + 2.0 * s, zx + 2.0 * s, zy + 2.0 * s, 0.4, col);
         }
      }
   }

   static final class Fractal implements AnimatedCapes.Scene {
      private static final double TX = -0.743643887037151;
      private static final double TY = 0.13182590420533;

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double phase = AnimatedCapes.wrap(t * 0.18, 8.0);
         double width = 3.0 * Math.exp(-phase);
         int maxIter = 50 + (int)(phase * 14.0);
         double scale = width / 80.0;
         double rot = t * 0.05;
         double cos = Math.cos(rot);
         double sin = Math.sin(rot);
         double fade = Math.min(1.0, Math.min(phase * 3.0, (8.0 - phase) * 3.0));

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double ux = (x + 0.5 - 40.0) * scale;
               double uy = (y + 0.5 - 64.0) * scale;
               double cr = -0.743643887037151 + ux * cos - uy * sin;
               double ci = 0.13182590420533 + ux * sin + uy * cos;
               double zr = 0.0;
               double zi = 0.0;

               int i;
               for (i = 0; i < maxIter && zr * zr + zi * zi < 16.0; i++) {
                  double next = zr * zr - zi * zi + cr;
                  zi = 2.0 * zr * zi + ci;
                  zr = next;
               }

               int col;
               if (i >= maxIter) {
                  col = -16645624;
               } else {
                  double smooth = i + 1 - Math.log(Math.log(Math.sqrt(zr * zr + zi * zi))) / Math.log(2.0);
                  double v = Math.sqrt(smooth) * 0.2 + t * 0.05;
                  col = AnimatedCapes.ramp(
                     AnimatedCapes.wrap(v, 1.0), new double[]{0.0, 0.25, 0.5, 0.75, 1.0}, new int[]{-16446934, -14005584, -12924688, -3920, -16446934}
                  );
               }

               c.set(x, y, AnimatedCapes.shade(col, 0.35 + 0.65 * fade));
            }
         }
      }
   }

   static final class GoldfishBowl implements AnimatedCapes.Scene {
      private static final double BX = 40.0;
      private static final double BY = 66.0;
      private static final double BR = 30.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               boolean dot = (x + y / 8 % 2 * 4) % 8 == 0 && y % 8 == 4;
               c.set(x, y, dot ? -4663088 : -5715776);
            }
         }

         c.vignette(0.4);
         c.rect(0.0, 96.0, 80.0, 32.0, -8758726);
         c.rect(0.0, 96.0, 80.0, 2.0, -6653358);
         c.ellipse(40.0, 98.0, 26.0, 3.0, 0.0, AnimatedCapes.alpha(-16777216, 0.3));
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double waterTop = 44.400000000000006;

         for (int y = 36; y <= 96.0; y++) {
            for (int x = 10; x <= 70.0; x++) {
               double d = Math.hypot(x + 0.5 - 40.0, (y + 0.5 - 66.0) * 1.02);
               if (!(d > 30.0) && y <= 96) {
                  if (y > waterTop + Math.sin(x * 0.3 + t * 2.0) * 0.5) {
                     double depth = (y - waterTop) / 51.0;
                     int col = AnimatedCapes.lerp(-7676696, -12936520, depth);
                     double caustic = Math.pow(
                        1.0 - Math.abs(AnimatedCapes.noise(x * 0.2 + t * 0.4, y * 0.2) + AnimatedCapes.noise(x * 0.2 - t * 0.3, y * 0.2 + 5.0) - 1.0), 8.0
                     );
                     col = AnimatedCapes.lerp(col, -1, caustic * 0.25);
                     c.blend(x, y, AnimatedCapes.alpha(col, 0.85));
                  } else {
                     c.blend(x, y, AnimatedCapes.alpha(-1509121, 0.15));
                  }
               }
            }
         }

         for (int xx = 19; xx < 61.0; xx++) {
            c.blend(xx, (int)(waterTop + Math.sin(xx * 0.3 + t * 2.0) * 0.5), AnimatedCapes.alpha(-1, 0.7));
         }

         for (int i = 0; i < 40; i++) {
            double gx = 18.0 + AnimatedCapes.hash(i, 1) * 44.0;
            double gy = 90.0 + AnimatedCapes.hash(i, 2) * 4.0;
            if (Math.hypot(gx - 40.0, gy - 66.0) < 28.0) {
               c.disc(gx, gy, 1.2, AnimatedCapes.lerp(-2056000, -8339216, AnimatedCapes.hash(i, 3)));
            }
         }

         c.rect(46.0, 78.0, 10.0, 13.0, -6645080);
         c.rect(44.0, 74.0, 4.0, 17.0, -7697768);
         c.rect(54.0, 74.0, 4.0, 17.0, -7697768);

         for (int k = 0; k < 3; k++) {
            c.rect(44.0 + k * 5.2, 72.0, 2.0, 2.0, -7697768);
         }

         c.rect(49.0, 84.0, 4.0, 7.0, -14013894);

         for (int k = 0; k < 3; k++) {
            double baseX = 26 + k * 3;
            double px = baseX;
            double py = 92.0;

            for (int s = 1; s < 8; s++) {
               double nx = baseX + Math.sin(t * 1.2 + s * 0.6 + k) * s * 0.4;
               double ny = 92.0 - s * 3.2;
               c.line(px, py, nx, ny, 1.2, -12936630);
               px = nx;
               py = ny;
            }
         }

         for (int b = 0; b < 8; b++) {
            double life = AnimatedCapes.wrap(t * 0.5 + b / 8.0, 1.0);
            double y = 88.0 - life * (88.0 - waterTop);
            c.ring(51.0 + Math.sin(life * 8.0 + b) * 1.2, y, 0.6 + life * 0.6, 0.4, AnimatedCapes.alpha(-1, 0.8));
         }

         double food = AnimatedCapes.wrap(t, 10.0);
         double foodY = waterTop + Math.min(food, 6.0) * 4.0;
         if (food < 7.0) {
            for (int f = 0; f < 4; f++) {
               c.rect(34 + f * 3 + Math.sin(t * 2.0 + f) * 0.8, foodY + f % 2 * 1.5, 1.0, 1.0, -5218262);
            }
         }

         double a = t * 0.5;
         double fx = 40.0 + Math.cos(a) * 16.0;
         double fy = 70.0 + Math.sin(a * 2.0) * 6.0;
         if (food > 2.0 && food < 6.0) {
            double p = (food - 2.0) / 4.0;
            fx += (38.0 - fx) * Math.sin(p * Math.PI);
            fy += (foodY + 4.0 - fy) * Math.sin(p * Math.PI);
         }

         double dir = -Math.sin(a) >= 0.0 ? 1.0 : -1.0;
         double depth = 0.75 + 0.25 * Math.cos(a);
         double s = 1.2 * depth;
         double tail = Math.sin(t * 8.0) * 0.5;
         c.polygon(
            new double[][]{
               {fx - dir * 5.0 * s, fy}, {fx - dir * 10.0 * s, fy - (4.0 + tail) * s}, {fx - dir * 9.0 * s, fy}, {fx - dir * 10.0 * s, fy + (4.0 - tail) * s}
            },
            -26048
         );
         c.ellipse(fx, fy, 6.0 * s, 3.4 * s, 0.0, -34272);
         c.ellipse(fx + dir * 1.0 * s, fy + 1.0 * s, 4.0 * s, 1.6 * s, 0.0, -20384);
         c.polygon(new double[][]{{fx - dir * 1.0 * s, fy - 3.0 * s}, {fx - dir * 4.0 * s, fy - 6.0 * s}, {fx + dir * 2.0 * s, fy - 3.0 * s}}, -26048);
         c.disc(fx + dir * 3.5 * s, fy - 1.0 * s, 1.0 * s, -1);
         c.disc(fx + dir * 3.8 * s, fy - 1.0 * s, 0.5 * s, -16119286);
         c.ring(40.0, 66.0, 29.5, 1.0, AnimatedCapes.alpha(-1509121, 0.5));
         c.ellipse(24.0, 54.0, 4.0, 10.0, 0.5, AnimatedCapes.alpha(-1, 0.3));
         c.ellipse(40.0, 37.5, 16.5, 2.0, 0.0, AnimatedCapes.alpha(-1509121, 0.6));
      }
   }

   static final class InkInWater implements AnimatedCapes.Scene {
      private static final int[] INKS = new int[]{-15066518, -6284742, -16094614};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-723212, -2562840});
         c.rect(0.0, 0.0, 2.0, 128.0, -4667188);
         c.rect(78.0, 0.0, 2.0, 128.0, -4667188);
         c.rect(0.0, 8.0, 80.0, 1.0, AnimatedCapes.alpha(-7690056, 0.6));
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycle = AnimatedCapes.wrap(t, 15.0);

         for (int y = 9; y < 10; y++) {
            for (int x = 2; x < 78; x++) {
               c.blend(x, y + (int)Math.round(Math.sin(x * 0.4 + t * 2.0) * 0.5), AnimatedCapes.alpha(-1, 0.6));
            }
         }

         for (int d = 0; d < 3; d++) {
            double start = d * 3.0;
            double age = cycle - start;
            if (!(age < 0.0)) {
               double dropX = 22 + d * 18;
               int ink = INKS[d];
               if (age < 0.6) {
                  double y = -4.0 + age / 0.6 * 22.0;
                  c.ellipse(dropX, y, 1.4, 2.2, 0.0, ink);
               } else {
                  double grow = Math.min(1.0, (age - 0.6) / 8.0);
                  double fadeOut = cycle > 13.0 ? 1.0 - (cycle - 13.0) / 2.0 : 1.0;
                  double cy = 18.0 + grow * 60.0;
                  double radius = 4.0 + grow * 34.0;

                  for (int y = 9; y < 128; y++) {
                     for (int x = 2; x < 78; x++) {
                        double dx = (x - dropX) / radius;
                        double dy = (y - cy) / (radius * 1.3);
                        double dist = Math.sqrt(dx * dx + dy * dy);
                        if (!(dist > 1.4)) {
                           double wx = AnimatedCapes.fbm(x * 0.05 + d * 9, y * 0.05 - t * 0.12, 3) * 3.0;
                           double wy = AnimatedCapes.fbm(x * 0.05 + 4.0 + d * 9, y * 0.05 + t * 0.1, 3) * 3.0;
                           double n = AnimatedCapes.fbm(x * 0.04 + wx, y * 0.04 + wy - age * 0.05, 4);
                           double density = AnimatedCapes.smoothstep(0.35, 0.7, n) * AnimatedCapes.clamp(1.3 - dist, 0.0, 1.0) * (1.0 - grow * 0.45) * fadeOut;
                           if (density > 0.01) {
                              c.blend(x, y, AnimatedCapes.alpha(ink, Math.min(1.0, density * 1.25)));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static final class Kaleidoscope implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.fill(-16119802);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cx = 40.0;
         double cy = 64.0;
         double wedge = Math.PI / 6;

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double dx = x + 0.5 - cx;
               double dy = y + 0.5 - cy;
               double r = Math.hypot(dx, dy);
               if (!(r > 39.0)) {
                  double a = Math.atan2(dy, dx) + t * 0.1;
                  a = AnimatedCapes.wrap(a, wedge * 2.0);
                  if (a > wedge) {
                     a = wedge * 2.0 - a;
                  }

                  double px = Math.cos(a) * r;
                  double py = Math.sin(a) * r;
                  double n = AnimatedCapes.fbm(px * 0.07 + t * 0.2, py * 0.07 - t * 0.1, 3);
                  double shards = Math.abs(Math.sin(px * 0.25 + t * 0.6) + Math.cos(py * 0.3 - t * 0.4));
                  double facet = Math.floor((n * 3.0 + shards) * 3.0) / 3.0;
                  int col = AnimatedCapes.hsv(facet * 90.0 + r * 4.0 + t * 25.0, 0.75, 0.55 + 0.45 * AnimatedCapes.wrap(facet, 1.0));
                  if (Math.abs(shards - 1.0) < 0.06) {
                     col = AnimatedCapes.lerp(col, -16119802, 0.7);
                  }

                  double edge = AnimatedCapes.clamp((39.0 - r) / 3.0, 0.0, 1.0);
                  c.set(x, y, AnimatedCapes.shade(col, edge));
               }
            }
         }

         c.ring(cx, cy, 39.5, 2.5, -5207494);
         c.ring(cx, cy, 41.5, 1.0, -9811430);
         c.glow(cx, cy, 10.0, -1, 0.25 + 0.15 * Math.sin(t));
         c.ellipse(cx - 14.0, cy - 18.0, 8.0, 3.0, -0.6, AnimatedCapes.alpha(-1, 0.1));
      }
   }

   static final class PendulumWave implements AnimatedCapes.Scene {
      private static final int COUNT = 12;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double grain = Math.sin(x * 0.25 + AnimatedCapes.fbm(x * 0.02, y * 0.2, 3) * 6.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-14018030, -12966376, 0.5 + 0.3 * grain), x, y));
            }
         }

         c.vignette(0.5);
         c.rect(4.0, 8.0, 72.0, 4.0, -3628992);
         c.rect(4.0, 8.0, 72.0, 1.0, -995216);
         c.rect(4.0, 11.0, 72.0, 1.0, -7706080);
         c.rect(6.0, 12.0, 3.0, 116.0, -15068660);
         c.rect(71.0, 12.0, 3.0, 116.0, -15068660);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycleTime = 30.0;

         for (int i = 0; i < 12; i++) {
            double oscillations = 24 + i;
            double period = cycleTime / oscillations;
            double length = 108.0 * Math.pow(period / (cycleTime / 24.0), 2.0);
            double angle = 0.42 * Math.cos((Math.PI * 2) * t / period);
            double ax = 12.0 + i * 5.1;
            double ay = 12.0;
            double bx = ax + Math.sin(angle) * length * 0.55;
            double by = ay + Math.cos(angle) * length;
            int col = AnimatedCapes.hsv(i * 30, 0.75, 1.0);

            for (int k = 1; k < 5; k++) {
               double past = 0.42 * Math.cos((Math.PI * 2) * (t - k * 0.03) / period);
               c.disc(ax + Math.sin(past) * length * 0.55, ay + Math.cos(past) * length, 2.2, AnimatedCapes.alpha(col, 0.12 * (5 - k) / 4.0));
            }

            c.ellipse(bx + 2.0, by + 3.0, 2.4, 1.2, 0.0, AnimatedCapes.alpha(-16777216, 0.35));
            c.line(ax, ay, bx, by, 0.4, -2568000);
            c.disc(bx, by, 2.4, AnimatedCapes.shade(col, 0.7));
            c.disc(bx - 0.5, by - 0.5, 1.8, col);
            c.disc(bx - 1.0, by - 1.0, 0.6, -1);
            c.disc(ax, ay, 0.8, -7706080);
         }
      }
   }
}
