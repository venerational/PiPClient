package dev.lyfw.lyfwclient.render;

final class CapeScenesA {
   private CapeScenesA() {
   }

   static final class Aurora implements AnimatedCapes.Scene {
      private static final int SHORE = 104;
      private boolean[] open;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.45, 0.8}, new int[]{-16710644, -16444890, -16047562});

         for (int i = 0; i < 120; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 84.0, 0.0, -2561793, 0.12 + AnimatedCapes.hash(i, 3) * 0.35);
         }

         int[] before = (int[])c.px.clone();
         AnimatedCapes.mountains(c, 7, 84.0, 36.0, 0.04, -14930880, -2299662);
         AnimatedCapes.mountains(c, 21, 99.0, 14.0, 0.07, -15589330, -4929834);

         for (int y = 99; y < 107; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-15455682, -16115674, (y - 99) / 8.0), x, y));
            }
         }

         c.fillBelow(xx -> 104.0 + 2.0 * Math.sin(xx * 0.15) + 1.5 * AnimatedCapes.noise(xx * 0.3, 4.0), -3746590);

         for (int y = 104; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (c.get(x, y) == -3746590) {
                  c.set(
                     x,
                     y,
                     AnimatedCapes.dither(
                        AnimatedCapes.lerp(-2826004, -8218448, AnimatedCapes.smoothstep(0.4, 0.8, AnimatedCapes.fbm(x * 0.12, y * 0.25, 3)) * 0.7), x, y
                     )
                  );
               }
            }
         }

         for (int i = 0; i < 16; i++) {
            double xx = AnimatedCapes.hash(i, 40) * 80.0;
            if (!(xx > 50.0) || !(xx < 72.0)) {
               double baseY = 107.0 + AnimatedCapes.hash(i, 41) * 5.0;
               double height = 12.0 + AnimatedCapes.hash(i, 42) * 14.0;
               AnimatedCapes.pine(c, xx, baseY, height, -16115680);

               for (int k = 0; k < 4; k++) {
                  double sy = baseY - height * (0.2 + k * 0.2);
                  c.line(xx - 1.5 - (3 - k), sy, xx + 0.5, sy - 1.0, 0.7, AnimatedCapes.alpha(-2036490, 0.8));
               }
            }
         }

         c.rect(54.0, 103.0, 13.0, 8.0, -12966376);

         for (int k = 0; k < 4; k++) {
            c.rect(54.0, 104 + k * 2, 13.0, 0.6, -14280690);
         }

         c.polygon(new double[][]{{52.0, 104.0}, {69.0, 104.0}, {60.5, 96.0}}, -14018030);
         c.polygon(new double[][]{{52.0, 104.0}, {60.5, 95.5}, {69.0, 104.0}, {66.5, 104.0}, {60.5, 98.0}, {54.5, 104.0}}, -1511176);
         c.rect(64.0, 96.0, 2.0, 4.0, -14016480);
         c.rect(63.5, 95.5, 3.0, 1.0, -1511176);
         c.rect(62.0, 106.0, 3.0, 5.0, -14806518);
         this.open = AnimatedCapes.unchanged(c, before);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int[] colors = new int[]{-12517472, -13575992, -6266625};
         double[] stops = new double[]{0.0, 0.35, 1.0};

         for (int x = 0; x < 80; x++) {
            for (int b = 0; b < 3; b++) {
               double center = 26 + b * 11 + 7.0 * Math.sin(x * 0.06 + t * 0.5 + b * 1.7) + 14.0 * (AnimatedCapes.fbm(x * 0.03 + t * 0.08, b * 4.3, 3) - 0.5);
               double strength = AnimatedCapes.smoothstep(0.35, 0.72, AnimatedCapes.fbm(x * 0.045 - t * 0.12 * (b + 1), b * 7.1 + t * 0.05, 3))
                  * (0.65 + 0.35 * Math.sin(x * 0.9 + t * 1.7 + b * 2.0));
               if (!(strength < 0.01)) {
                  for (int y = Math.max(0, (int)(center - 36.0)); y < Math.min(98.0, center + 5.0); y++) {
                     if (this.open[y * 80 + x]) {
                        double k = y < center ? Math.exp(-(center - y) / 11.0) : Math.exp(-(y - center) * 0.8);
                        c.add(x, y, AnimatedCapes.ramp(AnimatedCapes.clamp((center - y) / 36.0, 0.0, 1.0), stops, colors), k * strength * 0.55);
                     }
                  }
               }
            }
         }

         for (int yx = 99; yx < 104; yx++) {
            for (int x = 0; x < 80; x++) {
               double glint = AnimatedCapes.smoothstep(0.35, 0.72, AnimatedCapes.fbm(x * 0.045 - t * 0.12, 0.0, 3));
               c.add(x, yx, -12525408, glint * (0.06 + 0.06 * Math.sin(x * 0.5 + t * 1.1 + yx * 1.9)));
            }
         }

         AnimatedCapes.stars(c, t, 36, 0.0, 84.0, -1, 5, this.open);
         c.rect(57.0, 106.0, 3.0, 3.0, AnimatedCapes.lerp(-24528, -8048, AnimatedCapes.noise(t * 5.0, 2.0)));
         c.glow(58.5, 107.5, 8.0, -28624, 0.25 + 0.1 * AnimatedCapes.noise(t * 6.0, 3.0));

         for (int k = 0; k < 7; k++) {
            double p = AnimatedCapes.wrap(t * 0.22 + k / 7.0, 1.0);
            c.disc(65.0 + p * 7.0 + Math.sin(p * 6.0 + t) * 1.5, 95.0 - p * 24.0, 1.0 + p * 2.8, AnimatedCapes.alpha(-8088408, 0.4 * (1.0 - p)));
         }

         double cycle = AnimatedCapes.wrap(t, 9.0);
         if (cycle < 0.9) {
            double p = cycle / 0.9;
            double sx = 72.0 - p * 46.0;
            double sy = 6.0 + p * 18.0;
            c.beam(sx, sy, sx + 8.0, sy - 3.0, 0.6, -1, 1.0 - p);
         }
      }
   }

   static final class CherryBlossomNight implements AnimatedCapes.Scene {
      private static final double MOON_X = 57.0;
      private static final double MOON_Y = 21.0;
      private static final int LAKE = 94;
      private boolean[] open;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.38, 0.66, 1.0}, new int[]{-16251106, -14543290, -8635798, -11916710});

         for (int i = 0; i < 80; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 70.0, 0.0, -1516289, 0.12 + AnimatedCapes.hash(i, 3) * 0.3);
         }

         c.glow(57.0, 21.0, 36.0, -5209912, 0.45);
         c.disc(57.0, 21.0, 10.0, -3878);
         c.disc(59.5, 22.5, 8.5, AnimatedCapes.alpha(-1518408, 0.45));
         c.disc(54.0, 18.0, 2.2, AnimatedCapes.alpha(-3623784, 0.55));
         c.disc(60.0, 25.0, 1.6, AnimatedCapes.alpha(-3623784, 0.5));
         c.disc(61.0, 17.0, 1.1, AnimatedCapes.alpha(-3623784, 0.45));
         c.ellipse(47.0, 29.0, 22.0, 1.8, 0.04, AnimatedCapes.alpha(-12965800, 0.8));
         c.ellipse(64.0, 34.0, 16.0, 1.4, -0.03, AnimatedCapes.alpha(-11915160, 0.7));
         int[] before = (int[])c.px.clone();
         AnimatedCapes.mountains(c, 3, 90.0, 26.0, 0.045, -13754552, 0);
         AnimatedCapes.mountains(c, 9, 95.0, 12.0, 0.08, -14739914, 0);
         int roof = -15397340;

         for (int k = 0; k < 4; k++) {
            double ty = 93 - k * 7;
            double half = 6 - k;
            c.rect(62.0 - half, ty - 7.0, half * 2.0, 7.0, roof);
            c.polygon(
               new double[][]{{62.0 - half - 4.0, ty - 6.0}, {62.0 + half + 4.0, ty - 6.0}, {62.0 + half + 1.0, ty - 9.0}, {62.0 - half - 1.0, ty - 9.0}}, roof
            );
            c.line(62.0 - half - 4.0, ty - 6.0, 62.0 - half - 5.0, ty - 8.0, 0.8, roof);
            c.line(62.0 + half + 4.0, ty - 6.0, 62.0 + half + 5.0, ty - 8.0, 0.8, roof);
         }

         c.line(62.0, 65.0, 62.0, 57.0, 0.9, roof);
         c.rect(61.0, 88.0, 2.0, 2.0, -20384);
         c.rect(61.0, 74.0, 2.0, 2.0, -24496);

         for (int y = 94; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-13885360, -16119784, (y - 94) / 34.0), x, y));
            }
         }

         c.rect(0.0, 94.0, 80.0, 1.0, AnimatedCapes.alpha(-7710054, 0.5));
         c.polygon(
            new double[][]{{0.0, 99.0}, {12.0, 96.0}, {28.0, 100.0}, {42.0, 109.0}, {50.0, 128.0}, {0.0, 128.0}},
            (x, y) -> AnimatedCapes.lerp(-14412756, -15857644, (y - 96) / 32.0)
         );

         for (int i = 0; i < 18; i++) {
            double gx = AnimatedCapes.hash(i, 8) * 44.0;
            double gy = 100.0 + gx * 0.2 + AnimatedCapes.hash(i, 9) * 4.0;
            c.line(gx, gy, gx + (AnimatedCapes.hash(i, 10) - 0.5) * 2.0, gy - 2.0 - AnimatedCapes.hash(i, 11) * 2.0, 0.6, -12966848);
         }

         int bark = -13887456;
         double[][] limbs = new double[][]{
            {15.0, 128.0, 18.0, 104.0, 6.0},
            {18.0, 104.0, 15.0, 88.0, 5.0},
            {15.0, 88.0, 20.0, 72.0, 4.0},
            {20.0, 72.0, 30.0, 58.0, 3.0},
            {30.0, 58.0, 44.0, 50.0, 2.0},
            {20.0, 72.0, 10.0, 60.0, 2.5},
            {10.0, 60.0, 4.0, 50.0, 1.5},
            {15.0, 88.0, 32.0, 80.0, 2.5},
            {32.0, 80.0, 43.0, 72.0, 1.6},
            {18.0, 104.0, 6.0, 97.0, 2.0},
            {20.0, 72.0, 22.0, 48.0, 2.0},
            {22.0, 48.0, 15.0, 38.0, 1.2},
            {30.0, 58.0, 34.0, 43.0, 1.4}
         };

         for (double[] l : limbs) {
            c.line(l[0], l[1], l[2], l[3], l[4], bark);
            c.line(l[0] - l[4] * 0.25, l[1], l[2] - l[4] * 0.25, l[3], l[4] * 0.3, AnimatedCapes.alpha(-10865600, 0.8));
         }

         double[][] clusters = new double[][]{
            {44.0, 50.0, 8.0},
            {42.0, 71.0, 7.0},
            {34.0, 43.0, 7.0},
            {15.0, 37.0, 8.0},
            {4.0, 50.0, 7.0},
            {10.0, 60.0, 6.0},
            {29.0, 57.0, 7.0},
            {22.0, 47.0, 8.0},
            {32.0, 80.0, 6.0},
            {6.0, 96.0, 5.0},
            {24.0, 66.0, 6.0}
         };

         for (int pass = 0; pass < 2; pass++) {
            for (int k = 0; k < clusters.length; k++) {
               double[] cl = clusters[k];

               for (int i = 0; i < 46; i++) {
                  double angle = AnimatedCapes.hash(i, k * 7 + 20) * Math.PI * 2.0;
                  double dist = Math.sqrt(AnimatedCapes.hash(i, k * 7 + 21)) * cl[2];
                  double bx = cl[0] + Math.cos(angle) * dist;
                  double by = cl[1] + Math.sin(angle) * dist * 0.75;
                  double lightness = AnimatedCapes.clamp(0.5 - Math.sin(angle) * dist / cl[2] * 0.5 + (AnimatedCapes.hash(i, k + 90) - 0.5) * 0.4, 0.0, 1.0);
                  if (pass == 0) {
                     c.disc(bx, by + 1.2, 2.2 + AnimatedCapes.hash(i, k + 91), AnimatedCapes.lerp(-9819566, -5748616, lightness));
                  } else if (AnimatedCapes.hash(i, k + 92) > 0.3) {
                     c.disc(
                        bx,
                        by,
                        1.0 + AnimatedCapes.hash(i, k + 93) * 1.3,
                        AnimatedCapes.ramp(lightness, new double[]{0.0, 0.55, 1.0}, new int[]{-3647344, -745272, -7954})
                     );
                  }
               }
            }
         }

         this.open = AnimatedCapes.unchanged(c, before);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         AnimatedCapes.stars(c, t, 34, 0.0, 70.0, -1, 11, this.open);

         for (int y = 95; y < 128; y++) {
            int row = y - 94;
            double width = 2.5 + row * 0.14;
            double wobble = Math.sin(y * 0.8 + t * 2.4) * 1.4 + Math.sin(y * 0.31 - t * 1.3);
            double bright = (0.4 - row * 0.008) * (0.55 + 0.45 * Math.sin(y * 1.7 + t * 3.1));

            for (double dx = -width; dx <= width && bright > 0.0; dx++) {
               c.add((int)Math.floor(57.0 + wobble + dx), y, -6448, bright * (1.0 - Math.abs(dx) / width));
            }
         }

         for (int k = 0; k < 2; k++) {
            c.glow(62.0, 89 - k * 14, 6.0, -28608, 0.22 + 0.12 * AnimatedCapes.noise(t * 4.0 + k * 9, 1.0));
         }

         for (int i = 0; i < 48; i++) {
            double speed = 7.0 + AnimatedCapes.hash(i, 31) * 8.0;
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 32) * 148.0 + t * speed, 148.0) - 10.0;
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 33) * 100.0 + t * (5.0 + AnimatedCapes.hash(i, 34) * 4.0) + 4.0 * Math.sin(t * 1.3 + i), 100.0)
               - 10.0;
            double spin = t * (1.5 + AnimatedCapes.hash(i, 35) * 2.0) + i;
            double size = 1.1 + AnimatedCapes.hash(i, 37) * 0.9;
            c.ellipse(
               x, y, size * 1.4, Math.max(0.45, size * Math.abs(Math.cos(spin))), spin * 0.5, AnimatedCapes.lerp(-10006, -1017170, AnimatedCapes.hash(i, 36))
            );
         }
      }
   }

   static final class CodeRain implements AnimatedCapes.Scene {
      private static final int[] GLYPHS = new int[]{
         29671, 31695, 23497, 29847, 25251, 11926, 31140, 19828, 14478, 21845, 29330, 11245, 5393, 27566, 13715, 18889
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-16644091, -16378868});

         for (int i = 0; i < 26; i++) {
            double x = Math.round(AnimatedCapes.hash(i, 1) * 20.0) * 4L + 1L;
            double y = Math.round(AnimatedCapes.hash(i, 2) * 32.0) * 4L + 1L;
            double len = 6.0 + AnimatedCapes.hash(i, 3) * 24.0;
            boolean across = AnimatedCapes.hash(i, 4) > 0.5;
            c.line(x, y, across ? x + len : x, across ? y : y + len, 0.7, AnimatedCapes.alpha(-15050188, 0.35));
            c.ring(across ? x + len : x, across ? y : y + len, 1.1, 0.7, AnimatedCapes.alpha(-13989296, 0.45));
         }

         double[][] hex = new double[6][];

         for (int k = 0; k < 6; k++) {
            hex[k] = new double[]{40.0 + Math.cos(k * Math.PI / 3.0 + (Math.PI / 6)) * 22.0, 62.0 + Math.sin(k * Math.PI / 3.0 + (Math.PI / 6)) * 22.0};
         }

         for (int k = 0; k < 6; k++) {
            double[] a = hex[k];
            double[] b = hex[(k + 1) % 6];
            c.line(a[0], a[1], b[0], b[1], 1.6, AnimatedCapes.alpha(-14779836, 0.55));
            c.line(
               40.0 + (a[0] - 40.0) * 0.72,
               62.0 + (a[1] - 62.0) * 0.72,
               40.0 + (b[0] - 40.0) * 0.72,
               62.0 + (b[1] - 62.0) * 0.72,
               0.8,
               AnimatedCapes.alpha(-14779836, 0.4)
            );
         }

         c.line(33.0, 54.0, 47.0, 54.0, 1.4, AnimatedCapes.alpha(-13985192, 0.5));
         c.line(40.0, 54.0, 40.0, 72.0, 1.4, AnimatedCapes.alpha(-13985192, 0.5));
         c.ring(40.0, 62.0, 5.0, 1.0, AnimatedCapes.alpha(-13985192, 0.4));
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double scan = AnimatedCapes.wrap(t * 22.0, 168.0) - 20.0;

         for (int y = (int)scan - 6; y <= scan + 1.0; y++) {
            for (int x = 0; x < 80; x++) {
               c.add(x, y, -13566080, 0.05 * (1.0 - (scan - y) / 7.0));
            }
         }

         c.glow(40.0, 62.0, 30.0, -14630816, 0.1 + 0.06 * Math.sin(t * 2.0));

         for (int col = 0; col < 20; col++) {
            for (int d = 0; d < 2; d++) {
               double speed = 2.4 + AnimatedCapes.hash(col, d * 3 + 10) * 3.2;
               int length = 5 + (int)(AnimatedCapes.hash(col, d * 3 + 11) * 10.0);
               int head = (int)Math.floor(AnimatedCapes.wrap(AnimatedCapes.hash(col, d * 3 + 12) * 40.0 + t * speed, 34 + d * 6));

               for (int row = head - length; row <= head; row++) {
                  if (row >= 0 && row <= 21) {
                     double k = 1.0 - (double)(head - row) / length;
                     boolean lead = row == head;
                     int glyph = GLYPHS[(int)(
                           AnimatedCapes.hash(col * 13 + row, (int)Math.floor(t * (lead ? 14.0 : 1.5) + AnimatedCapes.hash(row, col) * 9.0)) * GLYPHS.length
                        )
                        % GLYPHS.length];
                     int color = lead ? -2031640 : AnimatedCapes.lerp(-15832528, -11468912, k);
                     int gx = col * 4;
                     int gy = row * 6;

                     for (int py = 0; py < 5; py++) {
                        for (int px = 0; px < 3; px++) {
                           if ((glyph >> 14 - (py * 3 + px) & 1) != 0) {
                              c.add(gx + px, gy + py, color, lead ? 1.0 : k * 0.85 + 0.1);
                           }
                        }
                     }

                     if (lead) {
                        c.glow(gx + 1.5, gy + 2.5, 5.0, -10420336, 0.35);
                     }
                  }
               }
            }
         }
      }
   }

   static final class Fireflies implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.45, 1.0}, new int[]{-16116696, -14399910, -15720406});
         c.disc(60.0, 18.0, 5.0, -985888);
         c.disc(62.5, 16.5, 4.6, -15457224);
         c.glow(60.0, 18.0, 16.0, -10452832, 0.25);

         for (int layer = 0; layer < 4; layer++) {
            double baseY = 58 + layer * 13;
            int color = AnimatedCapes.lerp(-13743528, -16248306, layer / 3.0);
            int l = layer;
            c.fillBelow(
               xx -> baseY - (7 + l * 3) * AnimatedCapes.fbm(xx * 0.12 + l * 5, l * 2.3, 3) - Math.abs(Math.sin(xx * (0.7 - l * 0.1) + l)) * (3 + l), color
            );

            for (int y = (int)baseY - 6; y < baseY + 8.0; y++) {
               for (int x = 0; x < 80; x++) {
                  c.blend(x, y, AnimatedCapes.alpha(-9794928, 0.12 * (1.0 - Math.abs(y - baseY - 1.0) / 7.0) * (layer < 3 ? 1 : 0)));
               }
            }
         }

         c.polygon(
            new double[][]{{-2.0, 118.0}, {34.0, 110.0}, {38.0, 113.0}, {36.0, 119.0}, {-2.0, 126.0}},
            (xx, yx) -> AnimatedCapes.lerp(-14017518, -15463416, (yx - 110) / 14.0)
         );
         c.ellipse(35.5, 114.5, 2.4, 4.2, 0.1, -12965348);
         c.ring(35.5, 114.5, 1.4, 0.6, -15068662);

         for (int i = 0; i < 40; i++) {
            double mx = AnimatedCapes.hash(i, 20) * 34.0;
            c.disc(
               mx,
               110.0 + (34.0 - mx) * 0.02 * 8.0 - 8.5 + AnimatedCapes.hash(i, 21) * 1.5,
               0.9 + AnimatedCapes.hash(i, 22),
               AnimatedCapes.lerp(-14001632, -11892176, AnimatedCapes.hash(i, 23))
            );
         }

         double[][] shrooms = new double[][]{{8.0, 116.0, 2.4}, {13.0, 115.0, 1.6}, {24.0, 112.0, 2.0}, {50.0, 124.0, 2.6}, {55.0, 125.0, 1.7}};

         for (double[] m : shrooms) {
            c.rect(m[0] - 0.5, m[1] - m[2] * 1.4, 1.0, m[2] * 1.4, -3616576);
            c.ellipse(m[0], m[1] - m[2] * 1.4, m[2], m[2] * 0.6, 0.0, -12932944);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] shrooms = new double[][]{{8.0, 116.0, 2.4}, {13.0, 115.0, 1.6}, {24.0, 112.0, 2.0}, {50.0, 124.0, 2.6}, {55.0, 125.0, 1.7}};

         for (int k = 0; k < shrooms.length; k++) {
            double[] m = shrooms[k];
            c.glow(m[0], m[1] - m[2] * 1.4, 7.0, -12525344, 0.25 + 0.12 * Math.sin(t * 1.3 + k));
         }

         for (int i = 0; i < 110; i++) {
            double x = AnimatedCapes.hash(i, 40) * 84.0 - 2.0;
            double baseY = 129.0;
            double height = 6.0 + AnimatedCapes.hash(i, 41) * 12.0;
            double sway = Math.sin(t * 1.2 + x * 0.15) * 1.6 + Math.sin(t * 2.3 + i) * 0.4;
            int color = AnimatedCapes.lerp(-16114672, -14796256, AnimatedCapes.hash(i, 42));
            c.line(x, baseY, x + sway * 0.4, baseY - height * 0.55, 1.0, color);
            c.line(x + sway * 0.4, baseY - height * 0.55, x + sway, baseY - height, 0.7, AnimatedCapes.lerp(color, -12948940, 0.5));
         }

         for (int i = 0; i < 38; i++) {
            double x = AnimatedCapes.hash(i, 50) * 80.0 + 16.0 * (AnimatedCapes.noise(i * 1.7, t * 0.25) - 0.5);
            double y = 40.0 + AnimatedCapes.hash(i, 51) * 84.0 + 14.0 * (AnimatedCapes.noise(t * 0.25, i * 2.3) - 0.5);
            double blink = AnimatedCapes.smoothstep(0.45, 0.95, Math.sin(t * (1.2 + AnimatedCapes.hash(i, 52)) + AnimatedCapes.hash(i, 53) * 6.28) * 0.5 + 0.5);
            if (blink > 0.01) {
               c.glow(x, y, 6.0, -4653248, 0.55 * blink);
               c.add((int)x, (int)y, -64, blink);
            }
         }
      }
   }

   static final class Galaxy implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 60.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.035, y * 0.028, 5);
               double m = AnimatedCapes.fbm(x * 0.05 + 13.0, y * 0.04 + 7.0, 4);
               double dust = AnimatedCapes.fbm(x * 0.07 + 31.0, y * 0.05, 4);
               int col = AnimatedCapes.lerp(-16579830, -16119260, y / 128.0);
               col = AnimatedCapes.lerp(col, AnimatedCapes.lerp(-12970912, -15582600, m), AnimatedCapes.smoothstep(0.42, 0.78, n) * 0.85);
               col = AnimatedCapes.lerp(col, -3125104, AnimatedCapes.smoothstep(0.66, 0.9, n) * m * 0.7);
               col = AnimatedCapes.shade(col, 1.0 - AnimatedCapes.smoothstep(0.55, 0.8, dust) * 0.7);
               c.set(x, y, AnimatedCapes.dither(col, x, y));
            }
         }

         int[] tints = new int[]{-1, -4205313, -6472, -15144};

         for (int i = 0; i < 190; i++) {
            c.star(
               AnimatedCapes.hash(i, 61) * 80.0,
               AnimatedCapes.hash(i, 62) * 128.0,
               AnimatedCapes.hash(i, 63) > 0.96 ? 2.0 : 0.0,
               tints[i % tints.length],
               0.15 + AnimatedCapes.hash(i, 64) * 0.5
            );
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         c.glow(40.0, 60.0, 34.0, -8363880, 0.35);

         for (int i = 0; i < 950; i++) {
            double r = Math.pow(AnimatedCapes.hash(i, 71), 0.8) * 36.0 + 1.0;
            int arm = i % 3;
            double theta = arm * 2.094 + Math.log(r) * 2.4 + (AnimatedCapes.hash(i, 72) - 0.5) * (1.0 - r / 60.0) - t * 0.18;
            double lx = Math.cos(theta) * r;
            double ly = Math.sin(theta) * r;
            double x = 40.0 + lx * 0.95 + ly * 0.25;
            double y = 60.0 + ly * 0.55 - lx * 0.3;
            int col = AnimatedCapes.hash(i, 75) > 0.93 ? -25912 : AnimatedCapes.lerp(-7504, -8410369, r / 36.0);
            double amount = (0.16 + 0.5 * (1.0 - r / 37.0)) * (0.6 + 0.4 * AnimatedCapes.hash(i, 73));
            if (AnimatedCapes.hash(i, 74) > 0.975) {
               c.star(x, y, 1.0, col, amount + 0.3);
            } else {
               c.add((int)Math.floor(x), (int)Math.floor(y), col, amount);
            }
         }

         c.glow(40.0, 60.0, 10.0, -3888, 0.9 + 0.15 * Math.sin(t * 1.5));
         c.disc(40.0, 60.0, 1.4, -1);
         AnimatedCapes.stars(c, t, 44, 0.0, 128.0, -1, 5);
         double cycle = AnimatedCapes.wrap(t, 11.0);
         if (cycle < 2.2) {
            double p = cycle / 2.2;
            double hx = -6.0 + p * 94.0;
            double hy = 12.0 + p * 34.0;
            c.beam(hx, hy, hx - 16.0, hy - 5.8, 1.0, -7350017, 0.7);
            c.glow(hx, hy, 5.0, -3149569, 0.8);
         }
      }
   }

   static final class OceanWaves implements AnimatedCapes.Scene {
      private static final int HORIZON = 62;
      private boolean[] open;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.3, 0.46, 0.49}, new int[]{-14805952, -7718286, -1015712, -16272});
         c.glow(34.0, 60.0, 42.0, -28608, 0.55);
         c.disc(34.0, 58.0, 11.0, -6496);
         c.disc(34.0, 58.0, 8.0, -2858);

         for (int k = 0; k < 6; k++) {
            double y = 12 + k * 8;

            for (int j = 0; j < 3; j++) {
               double x = AnimatedCapes.hash(k, j + 20) * 90.0 - 5.0;
               double len = 12.0 + AnimatedCapes.hash(k, j + 21) * 12.0;
               c.ellipse(x, y, len, 1.7 + AnimatedCapes.hash(k, j + 22), 0.0, AnimatedCapes.alpha(AnimatedCapes.lerp(-12967336, -5219720, k / 6.0), 0.9));
               c.ellipse(x + 2.0, y + 1.1, len * 0.7, 0.6, 0.0, AnimatedCapes.alpha(AnimatedCapes.lerp(-29568, -14192, k / 6.0), 0.8));
            }
         }

         for (int y = 62; y < 128; y++) {
            double d = (y - 62) / 65.0;

            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-5218182, -15065008, Math.pow(d, 0.6)), x, y));
            }
         }

         c.rect(0.0, 62.0, 80.0, 1.0, AnimatedCapes.alpha(-12128, 0.6));
         int[] before = (int[])c.px.clone();
         c.polygon(
            new double[][]{{57.0, 128.0}, {55.0, 104.0}, {59.0, 86.0}, {57.0, 72.0}, {62.0, 60.0}, {70.0, 53.0}, {80.0, 51.0}, {80.0, 128.0}}, (xx, y) -> {
               int rock = AnimatedCapes.lerp(-15068640, -12702660, AnimatedCapes.fbm(xx * 0.2, y * 0.15, 3));
               return xx < 62 ? AnimatedCapes.lerp(rock, -5218214, 0.25) : rock;
            }
         );
         c.polygon(new double[][]{{63.0, 53.0}, {73.0, 53.0}, {71.5, 28.0}, {64.5, 28.0}}, (xx, y) -> {
            int col = (int)((y - 28) / 6.0) % 2 == 0 ? -857888 : -4179398;
            return AnimatedCapes.lerp(col, -14675944, (xx - 63) / 10.0 * 0.5);
         });
         c.rect(62.5, 26.0, 11.0, 2.0, -14013904);
         c.rect(65.0, 20.0, 6.0, 6.0, -13619142);
         c.rect(66.0, 21.0, 4.0, 4.0, -5984);
         c.polygon(new double[][]{{64.0, 20.5}, {72.0, 20.5}, {68.0, 15.0}}, -5230544);
         c.line(68.0, 15.0, 68.0, 12.0, 0.6, -14013904);
         c.rect(73.0, 47.0, 7.0, 6.0, -2042164);
         c.polygon(new double[][]{{72.0, 47.5}, {80.0, 47.5}, {80.0, 43.0}, {76.0, 43.0}}, -7722454);
         c.rect(75.0, 49.0, 2.0, 2.0, -10096);
         this.open = AnimatedCapes.unchanged(c, before);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 63; y < 128; y++) {
            double d = (y - 62) / 65.0;

            for (int x = 0; x < 80; x++) {
               if (this.open[y * 80 + x]) {
                  double swell = (Math.sin(x * (0.9 - d * 0.6) + y * 1.7 - t * (1.2 + d)) * 0.5 + 0.5)
                     * AnimatedCapes.noise(x * 0.25 * (1.2 - d) + t * 0.4, y * 0.6 - t * 0.8);
                  c.add(x, y, -26000, swell * 0.2 * (1.0 - d * 0.7));
                  double path = 4.0 + d * 16.0;
                  double dx = Math.abs(x - 34 + Math.sin(y * 0.7 + t) * 1.2);
                  if (dx < path) {
                     double g = AnimatedCapes.hash(x * 7 + (int)(t * 6.0), y);
                     c.add(x, y, g > 0.86 ? -6480 : -28592, g > 0.86 ? (1.0 - dx / path) * (g - 0.86) * 7.0 : 0.12 * (1.0 - dx / path));
                  }
               }
            }
         }

         for (int k = 0; k < 6; k++) {
            double p = AnimatedCapes.wrap(t * 0.07 + k / 6.0, 1.0);
            double y0 = 63.0 + p * p * 64.0;
            double amp = 0.4 + p * 2.0;

            for (int xx = 0; xx < 80; xx++) {
               double yy = y0 + Math.sin(xx * 0.3 / (p + 0.2) + k * 3 + t * 0.8) * amp;
               if (AnimatedCapes.isOpen(this.open, xx, yy)) {
                  c.blend(xx, (int)Math.floor(yy), AnimatedCapes.alpha(-5408, (0.12 + p * 0.55) * (0.6 + 0.4 * AnimatedCapes.noise(xx * 0.4, k + t))));
               }

               if (AnimatedCapes.isOpen(this.open, xx, yy + 1.0)) {
                  c.blend(xx, (int)Math.floor(yy + 1.0), AnimatedCapes.alpha(-15724496, 0.25 * p));
               }
            }
         }

         double bx = 20.0 + 3.0 * Math.sin(t * 0.2);
         double by = 74.0 + Math.sin(t * 1.7) * 0.7;
         double tilt = Math.sin(t * 1.3) * 0.12;
         c.rect(bx - 5.0, by + 2.5, 10.0, 1.0, AnimatedCapes.alpha(-16777216, 0.3));
         c.polygon(new double[][]{{bx - 6.0, by}, {bx + 6.0, by}, {bx + 4.0, by + 2.5}, {bx - 4.0, by + 2.5}}, -14018014);
         c.line(bx, by, bx + tilt * 12.0, by - 13.0, 0.8, -14018014);
         c.polygon(new double[][]{{bx + 0.6, by - 1.0}, {bx + 0.6 + tilt * 12.0, by - 13.0}, {bx + 7.0 + tilt * 5.0, by - 1.5}}, -727336);
         c.polygon(new double[][]{{bx - 0.6, by - 2.0}, {bx - 0.6 + tilt * 10.0, by - 11.0}, {bx - 5.0 + tilt * 4.0, by - 2.0}}, -1525584);
         double angle = t * 1.1;
         double facing = Math.cos(angle);
         if (Math.abs(facing) > 0.05) {
            double far = facing * 72.0;
            c.polygon(new double[][]{{68.0, 22.0}, {68.0, 24.0}, {68.0 + far, 32.0}, {68.0 + far, 14.0}}, AnimatedCapes.alpha(-5984, 0.2 * Math.abs(facing)));
         }

         double towards = Math.max(0.0, Math.sin(angle));
         c.glow(68.0, 23.0, 6.0 + 12.0 * towards, -8032, 0.5 + 0.9 * towards);

         for (int k = 0; k < 3; k++) {
            double gx = AnimatedCapes.wrap(t * (6 + k * 2) + k * 30, 100.0) - 10.0;
            AnimatedCapes.bird(c, gx, 28 + k * 7 + Math.sin(t * 0.7 + k) * 3.0, 1.8 + k * 0.3, t * 8.0 + k * 2, -535162848);
         }
      }
   }

   static final class Prism implements AnimatedCapes.Scene {
      private static final double[] ENTRY = new double[]{33.5, 59.0};
      private static final double[] EXIT = new double[]{46.5, 61.0};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7, 1.0}, new int[]{-16119784, -15462352, -14936008});

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.add(x, y, -12570464, AnimatedCapes.smoothstep(0.55, 0.85, AnimatedCapes.fbm(x * 0.05, y * 0.04, 4)) * 0.18);
            }
         }

         for (int y = 96; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int tile = (x / 10 + y / 8 & 1) == 0 ? -15067600 : -15396314;
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(tile, -16251378, (y - 96) / 40.0), x, y));
            }
         }

         c.rect(0.0, 96.0, 80.0, 1.0, AnimatedCapes.alpha(-9807200, 0.5));
         c.polygon(
            new double[][]{{30.0, 96.0}, {50.0, 96.0}, {48.0, 76.0}, {32.0, 76.0}}, (xx, yx) -> AnimatedCapes.lerp(-11910056, -14409682, (xx - 30) / 20.0)
         );
         c.rect(28.0, 74.0, 24.0, 3.0, -10857368);
         c.rect(28.0, 74.0, 24.0, 1.0, -8751992);
         c.rect(29.0, 93.0, 22.0, 3.0, -12962744);
         c.polygon(
            new double[][]{{40.0, 44.0}, {27.0, 72.0}, {53.0, 72.0}},
            (xx, yx) -> AnimatedCapes.alpha(AnimatedCapes.lerp(-4663041, -9930552, (yx - 44) / 28.0), 0.45)
         );
         c.line(40.0, 44.0, 27.0, 72.0, 0.9, AnimatedCapes.alpha(-1, 0.85));
         c.line(40.0, 44.0, 53.0, 72.0, 0.9, AnimatedCapes.alpha(-3088129, 0.6));
         c.line(27.0, 72.0, 53.0, 72.0, 0.9, AnimatedCapes.alpha(-3088129, 0.5));
         c.line(38.0, 50.0, 31.0, 66.0, 0.6, AnimatedCapes.alpha(-1, 0.35));
         c.line(42.0, 52.0, 47.0, 63.0, 0.5, AnimatedCapes.alpha(-1, 0.2));
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double pulse = 0.85 + 0.15 * Math.sin(t * 2.2);
         c.beam(-2.0, 20.0, ENTRY[0], ENTRY[1], 2.2, -1, 0.8 * pulse);
         c.line(ENTRY[0], ENTRY[1], EXIT[0], EXIT[1], 3.0, AnimatedCapes.alpha(-1, 0.4 * pulse));
         c.glow(ENTRY[0], ENTRY[1], 7.0, -1, 0.5 * pulse);
         double spread = 0.08 + 0.012 * Math.sin(t * 0.7);

         for (int band = 0; band < 7; band++) {
            double a0 = 0.35 + band * spread + Math.sin(t * 0.5) * 0.03;
            double a1 = a0 + spread;
            int color = AnimatedCapes.hsv(band * 45.0, 0.85, 1.0);
            double far = 90.0;
            c.polygon(
               new double[][]{
                  {EXIT[0], EXIT[1] - 0.5},
                  {EXIT[0], EXIT[1] + 0.5},
                  {EXIT[0] + Math.cos(a1) * far, EXIT[1] + Math.sin(a1) * far},
                  {EXIT[0] + Math.cos(a0) * far, EXIT[1] + Math.sin(a0) * far}
               },
               AnimatedCapes.alpha(color, 0.3 * pulse)
            );
            double hitY = 96.0;
            double mid = (a0 + a1) / 2.0;
            double hitX = EXIT[0] + (hitY - EXIT[1]) / Math.tan(mid);
            c.glow(hitX, hitY + 2.0, 6.0, color, 0.35 * pulse);

            for (int s = 0; s < 4; s++) {
               double p = AnimatedCapes.wrap(t * 0.35 + AnimatedCapes.hash(band, s), 1.0);
               double d = p * 60.0;
               c.star(EXIT[0] + Math.cos(mid) * d, EXIT[1] + Math.sin(mid) * d, 1.0, color, 0.6 * (1.0 - p));
            }

            for (int y = 97; y < 128; y++) {
               double ry = 96 - (y - 96);
               double rx = EXIT[0] + (ry - EXIT[1]) / Math.tan(mid);
               c.add((int)Math.floor(rx + Math.sin(y * 0.9 + t * 2.0) * 0.8), y, color, 0.18 * (1.0 - (y - 96) / 32.0));
            }
         }

         c.glow(EXIT[0], EXIT[1], 6.0, -1, 0.45 * pulse);

         for (int i = 0; i < 26; i++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 5) * 40.0 + t * (1.0 + AnimatedCapes.hash(i, 6)), 40.0) - 4.0;
            double y = 20.0 + x * 1.0985915492957747 + (AnimatedCapes.hash(i, 7) - 0.5) * 5.0 + Math.sin(t + i) * 1.5;
            c.add((int)x, (int)y, -1, 0.25 + 0.25 * Math.sin(t * 3.0 + i));
         }
      }
   }

   static final class Thunderstorm implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.55, 1.0}, new int[]{-15328216, -12958632, -14011838});
         AnimatedCapes.mountains(c, 17, 86.0, 14.0, 0.06, -14406600, 0);
         c.fillBelow(x -> 88.0 + 7.0 * Math.sin(x * 0.05) + 3.0 * AnimatedCapes.fbm(x * 0.1, 1.0, 3), -14799836);

         for (int i = 0; i < 26; i++) {
            double px = AnimatedCapes.hash(i, 40) * 80.0;
            AnimatedCapes.pine(c, px, 90.0 + 7.0 * Math.sin(px * 0.05), 5.0 + AnimatedCapes.hash(i, 41) * 5.0, -15590378);
         }

         c.fillBelow(x -> 102.0 + 10.0 * Math.sin(x * 0.04 + 2.5) + 2.0 * AnimatedCapes.noise(x * 0.2, 5.0), -15195108);

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if ((x + y / 3) % 7 == 0) {
                  c.blend(x, y, AnimatedCapes.alpha(-13351366, 0.5));
               }
            }
         }

         c.line(58.0, 96.0, 58.0, 78.0, 2.2, -16447481);
         c.line(58.0, 84.0, 50.0, 76.0, 1.2, -16447481);
         c.line(58.0, 81.0, 66.0, 73.0, 1.2, -16447481);
         c.line(58.0, 78.0, 57.0, 70.0, 1.0, -16447481);

         for (int i = 0; i < 30; i++) {
            double angle = AnimatedCapes.hash(i, 9) * Math.PI * 2.0;
            double d = Math.sqrt(AnimatedCapes.hash(i, 10)) * 10.0;
            c.disc(58.0 + Math.cos(angle) * d, 72.0 + Math.sin(angle) * d * 0.6, 2.2, -16315639);
         }

         c.rect(14.0, 84.0, 12.0, 8.0, -15067628);
         c.polygon(new double[][]{{12.0, 84.5}, {28.0, 84.5}, {20.0, 78.0}}, -15857142);
         c.rect(17.0, 87.0, 2.0, 2.0, -2056128);
         c.glow(18.0, 88.0, 5.0, -28624, 0.3);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 70; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.04 + t * 0.06, y * 0.07, 4);
               c.blend(x, y, AnimatedCapes.alpha(AnimatedCapes.lerp(-10853256, -15591906, n), AnimatedCapes.smoothstep(0.4, 0.75, n) * 0.8 * (1.0 - y / 90.0)));
            }
         }

         int strike = (int)Math.floor(t / 4.2);
         double since = AnimatedCapes.wrap(t, 4.2);
         double flash = since < 0.07 ? 1.0 : (since < 0.13 ? 0.25 : (since < 0.22 ? 0.85 : (since < 0.6 ? 0.85 * (1.0 - (since - 0.22) / 0.38) : 0.0)));
         if (flash > 0.0) {
            for (int y = 0; y < 128; y++) {
               for (int x = 0; x < 80; x++) {
                  c.add(x, y, -8351552, flash * (y < 90 ? 0.28 : 0.12));
               }
            }

            double x = 12.0 + AnimatedCapes.hash(strike, 1) * 56.0;
            double y = 4.0;
            double groundY = 90.0 + AnimatedCapes.hash(strike, 2) * 6.0;

            for (int seg = 0; y < groundY; seg++) {
               double nx = x + (AnimatedCapes.hash(strike, seg + 10) - 0.5) * 9.0;
               double ny = Math.min(groundY, y + 5.0 + AnimatedCapes.hash(strike, seg + 40) * 5.0);
               c.beam(x, y, nx, ny, 1.0, -2563841, flash);
               if (AnimatedCapes.hash(strike, seg + 70) > 0.72) {
                  double bx = nx + (AnimatedCapes.hash(strike, seg + 90) - 0.5) * 16.0;
                  c.beam(nx, ny, bx, ny + 6.0 + AnimatedCapes.hash(strike, seg + 91) * 6.0, 0.5, -5193473, flash * 0.7);
               }

               x = nx;
               y = ny;
            }

            c.glow(x, groundY, 12.0, -5193473, flash);
         }

         for (int i = 0; i < 150; i++) {
            double speed = 70.0 + AnimatedCapes.hash(i, 20) * 40.0;
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 21) * 128.0 + t * speed, 136.0) - 4.0;
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 22) * 80.0 - t * speed * 0.25, 80.0);
            c.line(x, y, x - 1.1, y + 4.5, 0.5, AnimatedCapes.alpha(-5719856, 0.28 + 0.2 * AnimatedCapes.hash(i, 23)));
         }

         for (int i = 0; i < 12; i++) {
            double life = AnimatedCapes.wrap(t * 3.0 + AnimatedCapes.hash(i, 30), 1.0);
            double sx = AnimatedCapes.hash(i, (int)Math.floor(t * 3.0 + AnimatedCapes.hash(i, 30)) + 31) * 80.0;
            double sy = 104.0 + AnimatedCapes.hash(i, (int)Math.floor(t * 3.0 + AnimatedCapes.hash(i, 30)) + 32) * 22.0;
            c.ring(sx, sy, life * 2.5, 0.5, AnimatedCapes.alpha(-5719856, 0.4 * (1.0 - life)));
         }
      }
   }

   static final class Volcano implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 1.0}, new int[]{-15858424, -12973040, -8772592});

         for (int y = 0; y < 76; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05, y * 0.08, 5);
               c.blend(
                  x,
                  y,
                  AnimatedCapes.alpha(
                     AnimatedCapes.lerp(-15069680, -9819622, AnimatedCapes.smoothstep(0.2, 1.0, y / 76.0)), AnimatedCapes.smoothstep(0.45, 0.75, n) * 0.85
                  )
               );
            }
         }

         AnimatedCapes.mountains(c, 5, 102.0, 16.0, 0.06, -14939122, 0);
         c.polygon(
            new double[][]{
               {0.0, 128.0}, {0.0, 112.0}, {14.0, 90.0}, {26.0, 66.0}, {33.0, 52.0}, {47.0, 52.0}, {54.0, 66.0}, {66.0, 90.0}, {80.0, 108.0}, {80.0, 128.0}
            },
            (xx, yx) -> {
               double nx = AnimatedCapes.fbm(xx * 0.15, yx * 0.12, 4);
               int rock = AnimatedCapes.lerp(-15068656, -12703198, nx);
               double ridge = Math.abs(Math.sin(xx * 0.45 + nx * 4.0));
               rock = AnimatedCapes.shade(rock, 0.8 + ridge * 0.3);
               return AnimatedCapes.lerp(rock, -8771052, AnimatedCapes.clamp(1.0 - (yx - 52) / 34.0, 0.0, 1.0) * 0.45 * nx);
            }
         );
         c.ellipse(40.0, 52.5, 7.5, 1.8, 0.0, -26048);
         c.ellipse(40.0, 52.2, 6.0, 1.1, 0.0, -8064);
         c.fillBelow(xx -> 117.0 + 5.0 * AnimatedCapes.fbm(xx * 0.1, 9.0, 3), -15594998);
         c.line(9.0, 120.0, 10.0, 104.0, 1.4, -15594998);
         c.line(10.0, 110.0, 5.0, 104.0, 0.8, -15594998);
         c.line(10.0, 107.0, 14.0, 101.0, 0.8, -15594998);
         c.line(14.0, 101.0, 16.0, 102.0, 0.6, -15594998);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double erupt = Math.pow(0.5 + 0.5 * Math.sin(t * 0.9), 3.0);

         for (int y = 0; y < 54; y++) {
            double spread = 6.0 + (54 - y) * 0.45;
            double drift = (54 - y) * 0.15;

            for (int x = 0; x < 80; x++) {
               double dx = (x - 40 - drift) / spread;
               if (!(Math.abs(dx) > 1.2)) {
                  double n = AnimatedCapes.fbm(x * 0.09, y * 0.07 + t * 0.6, 4);
                  double a = AnimatedCapes.smoothstep(0.35, 0.7, n) * (1.0 - dx * dx / 1.44) * 0.85;
                  c.blend(x, y, AnimatedCapes.alpha(AnimatedCapes.lerp(-14018022, -6667746, AnimatedCapes.clamp((y - 18) / 36.0, 0.0, 1.0) * 0.85), a));
               }
            }
         }

         c.glow(40.0, 52.0, 26.0, -45040, 0.45 + 0.35 * erupt);

         for (int river = 0; river < 2; river++) {
            for (int y = 53; y < 118; y++) {
               double pathX = river == 0 ? 40.0 + (y - 52) * 0.38 + 2.5 * Math.sin(y * 0.13) : 40.0 - (y - 52) * 0.3 + 2.0 * Math.sin(y * 0.17 + 1.0);
               double width = 1.1 + (y - 52) * 0.025;

               for (int xx = (int)(pathX - width - 1.0); xx <= (int)(pathX + width + 1.0); xx++) {
                  double k = AnimatedCapes.clamp(width + 0.5 - Math.abs(xx + 0.5 - pathX), 0.0, 1.0);
                  double n = AnimatedCapes.fbm(xx * 0.25, y * 0.2 - t * 1.4, 3);
                  c.blend(xx, y, AnimatedCapes.alpha(AnimatedCapes.ramp(n, new double[]{0.3, 0.55, 0.75}, new int[]{-10876416, -2082296, -12208}), k));
               }

               if (y % 5 == 0) {
                  c.glow(pathX, y, 5.0, -49152, 0.12);
               }
            }
         }

         for (int i = 0; i < 64; i++) {
            double p = AnimatedCapes.wrap(t + AnimatedCapes.hash(i, 51) * 1.8, 1.8);
            double vx = (AnimatedCapes.hash(i, 52) - 0.5) * 26.0 * (0.5 + erupt);
            double vy = -(24.0 + AnimatedCapes.hash(i, 53) * 22.0) * (0.6 + 0.6 * erupt);
            double y = 51.0 + vy * p + 24.0 * p * p;
            if (!(y > 58.0)) {
               int col = AnimatedCapes.lerp(-8064, -8384512, p / 1.8);
               c.disc(40.0 + vx * p, y, 0.9, col);
               c.add((int)(40.0 + vx * p), (int)y, col, 0.4);
            }
         }

         for (int ix = 0; ix < 30; ix++) {
            double y = 128.0 - AnimatedCapes.wrap(AnimatedCapes.hash(ix, 81) * 128.0 + t * (8.0 + AnimatedCapes.hash(ix, 82) * 6.0), 128.0);
            double xx = AnimatedCapes.hash(ix, 83) * 80.0 + Math.sin(t + ix) * 3.0;
            c.glow(xx, y, 2.0, -36832, 0.5 * (y / 128.0));
         }
      }
   }

   static final class WinterVillage implements AnimatedCapes.Scene {
      private static final double[][] HOUSES = new double[][]{
         {6.0, 101.0, 15.0, 10.0}, {27.0, 97.0, 12.0, 9.0}, {46.0, 99.0, 11.0, 15.0}, {63.0, 104.0, 14.0, 9.0}, {32.0, 115.0, 16.0, 11.0}
      };
      private boolean[] open;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16446948, -15062454, -14009766});

         for (int i = 0; i < 90; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 70.0, 0.0, -2037505, 0.1 + AnimatedCapes.hash(i, 3) * 0.35);
         }

         c.glow(18.0, 20.0, 22.0, -8351552, 0.3);
         c.disc(18.0, 20.0, 7.0, -724764);
         c.disc(16.0, 18.5, 1.6, AnimatedCapes.alpha(-3619656, 0.6));
         c.disc(20.5, 22.0, 1.1, AnimatedCapes.alpha(-3619656, 0.5));
         int[] before = (int[])c.px.clone();
         c.fillBelow(xx -> 76.0 + 8.0 * Math.sin(xx * 0.06 + 2.0) + 4.0 * AnimatedCapes.fbm(xx * 0.08, 3.0, 3), -7693640);
         c.fillBelow(xx -> 92.0 + 6.0 * Math.sin(xx * 0.05 + 1.0) + 3.0 * AnimatedCapes.noise(xx * 0.1, 2.0), -2562834);

         for (int y = 80; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int px = c.get(x, y);
               if (px == -2562834 || px == -7693640) {
                  c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(px, 0.8 + 0.25 * AnimatedCapes.fbm(x * 0.1, y * 0.2, 3)), x, y));
               }
            }
         }

         for (int i = 0; i < 12; i++) {
            double xx = AnimatedCapes.hash(i, 30) * 80.0;
            AnimatedCapes.pine(c, xx, 86.0 + AnimatedCapes.hash(i, 31) * 4.0, 10.0 + AnimatedCapes.hash(i, 32) * 8.0, -15325638);
         }

         for (double[] house : HOUSES) {
            double xx = house[0];
            double gy = house[1];
            double w = house[2];
            double h = house[3];
            double peak = gy - h - w * 0.45;
            c.rect(xx + w - 4.0, peak + 1.0, 2.5, w * 0.35 + 2.0, -12965334);
            c.polygon(
               new double[][]{{xx, gy - h}, {xx + w, gy - h}, {xx + w, gy}, {xx, gy}},
               (pxx, py) -> AnimatedCapes.lerp(-9811402, -11915228, (pxx - xx) / w + AnimatedCapes.fbm(pxx * 0.5, py * 0.9, 2) * 0.3)
            );
            c.polygon(new double[][]{{xx - 2.0, gy - h + 0.5}, {xx + w + 2.0, gy - h + 0.5}, {xx + w / 2.0, peak}}, -12967904);
            c.polygon(
               new double[][]{
                  {xx - 2.5, gy - h + 0.8},
                  {xx + w / 2.0, peak - 1.0},
                  {xx + w + 2.5, gy - h + 0.8},
                  {xx + w + 0.5, gy - h + 0.8},
                  {xx + w / 2.0, peak + 1.5},
                  {xx - 0.5, gy - h + 0.8}
               },
               -985348
            );
            c.rect(xx + w / 2.0 - 1.5, gy - 4.5, 3.0, 4.5, -14018544);
            c.rect(xx + 1.5, gy - h + 2.5, 3.0, 3.0, -14016488);
            c.rect(xx + w - 4.5, gy - h + 2.5, 3.0, 3.0, -14016488);
            c.ellipse(xx + w / 2.0, gy + 0.5, w * 0.7, 1.4, 0.0, -1511178);
         }

         c.polygon(new double[][]{{49.0, 84.0}, {54.0, 84.0}, {51.5, 70.0}}, -12967904);
         c.line(51.5, 70.0, 51.5, 66.0, 0.6, -5205920);
         c.line(66.0, 112.0, 66.0, 124.0, 0.8, -15066590);
         c.rect(64.5, 110.0, 3.0, 2.0, -15066590);
         this.open = AnimatedCapes.unchanged(c, before);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         AnimatedCapes.stars(c, t, 30, 0.0, 70.0, -1, 7, this.open);

         for (int k = 0; k < HOUSES.length; k++) {
            double[] house = HOUSES[k];
            double x = house[0];
            double gy = house[1];
            double w = house[2];
            double h = house[3];

            for (int side = 0; side < 2; side++) {
               double wx = side == 0 ? x + 1.5 : x + w - 4.5;
               double flicker = AnimatedCapes.noise(t * 3.0 + k * 5 + side * 11, 1.0);
               c.rect(wx + 0.5, gy - h + 3.0, 2.0, 2.0, AnimatedCapes.lerp(-26064, -8054, flicker));
               c.glow(wx + 1.5, gy - h + 4.0, 6.0, -28624, 0.18 + 0.1 * flicker);
            }

            double chimneyX = x + w - 2.75;
            double chimneyY = gy - h - w * 0.45 + 1.0;

            for (int p = 0; p < 6; p++) {
               double life = AnimatedCapes.wrap(t * 0.2 + p / 6.0 + k * 0.13, 1.0);
               c.disc(
                  chimneyX + life * 8.0 + Math.sin(life * 5.0 + t + k) * 1.4,
                  chimneyY - life * 20.0,
                  0.8 + life * 2.6,
                  AnimatedCapes.alpha(-7694678, 0.38 * (1.0 - life))
               );
            }
         }

         c.glow(66.0, 111.0, 10.0, -16288, 0.35 + 0.05 * Math.sin(t * 7.0));

         for (int layer = 0; layer < 3; layer++) {
            int count = 30 + layer * 12;
            double speed = 6 + layer * 7;
            double size = 0.35 + layer * 0.35;

            for (int i = 0; i < count; i++) {
               double y = AnimatedCapes.wrap(
                     AnimatedCapes.hash(i, layer * 10 + 60) * 128.0 + t * speed * (0.8 + AnimatedCapes.hash(i, layer + 70) * 0.4), 132.0
                  )
                  - 2.0;
               double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, layer * 10 + 61) * 80.0 + Math.sin(t * 0.6 + i) * (2 + layer) + t * 2.0, 80.0);
               c.disc(x, y, size, AnimatedCapes.alpha(-1, 0.45 + layer * 0.2));
            }
         }
      }
   }
}
