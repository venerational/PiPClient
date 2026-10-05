package dev.lyfw.lyfwclient.render;

final class CapeScenesD {
   private CapeScenesD() {
   }

   static final class BlackHole implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 60.0;
      private int[] sky;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.04, y * 0.035 + 20.0, 4);
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(
                     AnimatedCapes.lerp(
                        -16645622,
                        AnimatedCapes.lerp(-14022086, -16111030, AnimatedCapes.fbm(x * 0.06 + 3.0, y * 0.05, 3)),
                        AnimatedCapes.smoothstep(0.5, 0.85, n) * 0.7
                     ),
                     x,
                     y
                  )
               );
            }
         }

         for (int i = 0; i < 260; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               AnimatedCapes.hash(i, 4) > 0.97 ? 1.0 : 0.0,
               i % 3 == 0 ? -8000 : -2037505,
               0.2 + AnimatedCapes.hash(i, 3) * 0.6
            );
         }

         this.sky = (int[])c.px.clone();
      }

      private static double disk(double angle, double r, double t) {
         return AnimatedCapes.fbm(Math.cos(angle - t * 0.6) * 2.0 + r * 0.25, Math.sin(angle - t * 0.6) * 2.0 + r * 0.25, 3);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double dx = x + 0.5 - 40.0;
               double dy = y + 0.5 - 60.0;
               double r = Math.hypot(dx, dy);
               if (!(r > 50.0) && !(r < 1.0)) {
                  double bend = r - 70.0 / r * (1.0 - r / 50.0);
                  double angle = Math.atan2(dy, dx) + 0.4 / (r * 0.1 + 1.0);
                  int sx = (int)Math.floor(40.0 + Math.cos(angle) * bend);
                  int sy = (int)Math.floor(60.0 + Math.sin(angle) * bend);
                  c.px[y * 80 + x] = sx >= 0 && sy >= 0 && sx < 80 && sy < 128 ? this.sky[sy * 80 + sx] : -16777216;
               }
            }
         }

         double[] stops = new double[]{0.0, 0.3, 0.65, 1.0};
         int[] heat = new int[]{-16, -10096, -30160, -7726582};

         for (int pass = 0; pass < 2; pass++) {
            for (int y = 20; y < 100; y++) {
               for (int xx = 0; xx < 80; xx++) {
                  double dx = xx + 0.5 - 40.0;
                  double dy = y + 0.5 - 60.0;
                  double rr = Math.hypot(dx, dy);
                  if (pass == 0) {
                     double v = dy / 0.2;
                     double rd = Math.hypot(dx, v);
                     if (v < 0.0 && rd > 10.0 && rd < 34.0) {
                        double f = (rd - 10.0) / 24.0;
                        double doppler = 1.0 + 0.5 * (-dx / rd);
                        double amount = (1.0 - f) * (0.55 + 0.45 * disk(Math.atan2(v, dx), rd, t)) * doppler;
                        c.blend(xx, y, AnimatedCapes.alpha(AnimatedCapes.ramp(f, stops, heat), AnimatedCapes.clamp(amount, 0.0, 1.0)));
                     }

                     if (rr > 9.5 && rr < 17.0) {
                        double band = Math.exp(-Math.pow((rr - 12.0) / 2.2, 2.0)) * (0.5 + 0.5 * Math.max(0.0, -dy / rr) + 0.25);
                        double tex = 0.6 + 0.4 * disk(Math.atan2(dy, dx) * 2.0, rr * 2.0, t);
                        c.add(xx, y, AnimatedCapes.ramp((rr - 9.5) / 8.0, stops, heat), band * tex * (1.0 + 0.4 * (-dx / rr)));
                     }

                     if (rr < 8.8) {
                        c.px[y * 80 + xx] = -16777216;
                     } else if (rr < 9.8) {
                        c.add(xx, y, -3888, (9.8 - rr) * 0.9);
                     }
                  } else {
                     double vx = dy / 0.2;
                     double rdx = Math.hypot(dx, vx);
                     if (vx >= 0.0 && rdx > 10.0 && rdx < 34.0) {
                        double f = (rdx - 10.0) / 24.0;
                        double doppler = 1.0 + 0.5 * (-dx / rdx);
                        double amount = (1.0 - f) * (0.55 + 0.45 * disk(Math.atan2(vx, dx), rdx, t)) * doppler;
                        c.blend(xx, y, AnimatedCapes.alpha(AnimatedCapes.ramp(f, stops, heat), AnimatedCapes.clamp(amount, 0.0, 1.0)));
                     }
                  }
               }
            }
         }

         c.glow(40.0, 60.0, 40.0, -28608, 0.18);

         for (int y = 0; y < 128; y++) {
            double f = Math.abs(y - 60.0) / 60.0;
            if (Math.abs(y - 60.0) > 9.0) {
               double width = 1.0 + f * 4.0;

               for (int xxx = (int)(40.0 - width); xxx <= 40.0 + width; xxx++) {
                  c.add(xxx, y, -9789185, 0.12 * (1.0 - f) * (1.0 - Math.abs(xxx - 40.0) / (width + 1.0)) * (0.7 + 0.3 * Math.sin(y * 0.3 - t * 6.0)));
               }
            }
         }
      }
   }

   static final class ButterflyGarden implements AnimatedCapes.Scene {
      private static final int[][] WINGS = new int[][]{{-30182, -15066598}, {-12940545, -16115126}, {-8128, -14013926}, {-40784, -10872262}, {-1, -10855830}};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.55}, new int[]{-10833680, -3085057});
         AnimatedCapes.cloud(c, 18.0, 18.0, 6.0, -1, -3088144, 1);
         AnimatedCapes.cloud(c, 62.0, 30.0, 5.0, -1, -3088144, 2);
         c.fillBelow(xx -> 64.0 + 5.0 * AnimatedCapes.fbm(xx * 0.1, 1.0, 3), -11892150);

         for (int i = 0; i < 60; i++) {
            c.disc(
               AnimatedCapes.hash(i, 5) * 80.0,
               62.0 + AnimatedCapes.hash(i, 6) * 6.0,
               2.0 + AnimatedCapes.hash(i, 7) * 2.5,
               AnimatedCapes.lerp(-12944838, -9786790, AnimatedCapes.hash(i, 8))
            );
         }

         for (int x = 0; x < 80; x += 5) {
            c.polygon(new double[][]{{x + 1, 80.0}, {x + 4, 80.0}, {x + 4, 70.0}, {x + 2.5, 68.0}, {x + 1, 70.0}}, -723728);
         }

         c.rect(0.0, 72.0, 80.0, 1.2, -2039590);
         c.rect(0.0, 77.0, 80.0, 1.2, -2039590);

         for (int y = 80; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-10837952, -13997526, (y - 80) / 48.0 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.3), x, y));
            }
         }

         for (int i = 0; i < 70; i++) {
            double x = AnimatedCapes.hash(i, 10) * 80.0;
            double y = 84.0 + Math.pow(AnimatedCapes.hash(i, 11), 0.8) * 42.0;
            double size = 1.2 + (y - 80.0) / 48.0 * 1.6;
            int type = (int)(AnimatedCapes.hash(i, 12) * 3.0);
            c.line(x, y, x, y + size * 3.0, 0.5, -13997526);
            if (type != 0) {
               if (type == 1) {
                  int col = AnimatedCapes.hash(i, 13) > 0.5 ? -2084790 : -34128;
                  c.polygon(
                     new double[][]{
                        {x - size, y - size},
                        {x - size * 0.4, y - size * 0.3},
                        {x, y - size * 1.1},
                        {x + size * 0.4, y - size * 0.3},
                        {x + size, y - size},
                        {x + size * 0.8, y + size * 0.4},
                        {x - size * 0.8, y + size * 0.4}
                     },
                     col
                  );
               } else {
                  for (int s = 0; s < 5; s++) {
                     c.disc(x + (s % 2 == 0 ? -0.4 : 0.4), y - s * size * 0.6, size * 0.4, AnimatedCapes.lerp(-7710000, -5207312, s / 5.0));
                  }
               }
            } else {
               for (int p = 0; p < 6; p++) {
                  double a = p * Math.PI / 3.0;
                  c.ellipse(x + Math.cos(a) * size, y + Math.sin(a) * size * 0.8, size * 0.8, size * 0.35, a, -1);
               }

               c.disc(x, y, size * 0.5, -16352);
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 3; k++) {
            double sway = Math.sin(t * 1.1 + k) * 1.5;
            double baseX = k == 0 ? 6.0 : (k == 1 ? 72.0 : 14.0);
            double top = k == 2 ? 70.0 : 60 + k * 2;
            c.line(baseX, 128.0, baseX + sway, top, 1.2, -12944854);
            c.ellipse(baseX + sway * 0.6 + 3.0, (128.0 + top) / 2.0, 3.0, 1.2, 0.5, -11888070);
            double fx = baseX + sway;

            for (int p = 0; p < 12; p++) {
               double a = p * Math.PI / 6.0 + sway * 0.05;
               c.ellipse(fx + Math.cos(a) * 4.0, top + Math.sin(a) * 4.0, 2.6, 1.0, a, -14304);
            }

            c.disc(fx, top, 2.8, -10864102);
            c.disc(fx - 0.8, top - 0.8, 1.0, -8758742);
         }

         for (int i = 0; i < 7; i++) {
            double x = 40.0 + 34.0 * (AnimatedCapes.noise(i * 3.1, t * 0.18) - 0.5) * 2.0;
            double y = 60.0 + 46.0 * (AnimatedCapes.noise(t * 0.15, i * 2.7) - 0.5) * 2.0;
            double flap = Math.abs(Math.cos(t * 11.0 + i * 2));
            int[] wing = WINGS[i % WINGS.length];
            double s = 2.6 + AnimatedCapes.hash(i, 20) * 1.2;

            for (int side = -1; side <= 1; side += 2) {
               double wx = side * s * flap;
               c.ellipse(x + wx * 0.9, y - s * 0.4, Math.max(0.4, s * flap), s * 0.8, side * 0.3, wing[1]);
               c.ellipse(x + wx * 0.9, y - s * 0.4, Math.max(0.3, s * flap * 0.7), s * 0.55, side * 0.3, wing[0]);
               c.ellipse(x + wx * 0.6, y + s * 0.5, Math.max(0.3, s * flap * 0.6), s * 0.5, -side * 0.4, wing[1]);
               c.ellipse(x + wx * 0.6, y + s * 0.5, Math.max(0.2, s * flap * 0.4), s * 0.35, -side * 0.4, wing[0]);
               c.rect(x + wx * 1.3, y - s * 0.8, 0.6, 0.6, -1);
            }

            c.line(x, y - s * 0.8, x, y + s * 0.8, 0.7, -15066598);
            c.line(x, y - s * 0.8, x - 1.0, y - s * 1.5, 0.3, -15066598);
            c.line(x, y - s * 0.8, x + 1.0, y - s * 1.5, 0.3, -15066598);
         }

         for (int i = 0; i < 3; i++) {
            double x = AnimatedCapes.wrap(t * (8 + i * 3) + i * 30, 100.0) - 10.0;
            double y = 90 + i * 10 + Math.sin(t * 5.0 + i) * 3.0;
            c.ellipse(x + 1.5, y - 1.0, 1.2, 0.8, 0.0, AnimatedCapes.alpha(-1, 0.6));
            c.ellipse(x, y, 1.6, 1.1, 0.0, -12256);
            c.rect(x - 0.3, y - 1.0, 0.7, 2.0, -15066598);
         }

         for (int i = 0; i < 14; i++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 80.0 + t * 2.0, 80.0);
            double y = 50.0 + AnimatedCapes.hash(i, 31) * 70.0 + Math.sin(t + i) * 3.0;
            c.add((int)x, (int)y, -2896, 0.4 * (0.5 + 0.5 * Math.sin(t * 3.0 + i)));
         }
      }
   }

   static final class Candlelight implements AnimatedCapes.Scene {
      private static final double[][] CANDLES = new double[][]{{22.0, 100.0, 26.0, 5.0}, {42.0, 100.0, 40.0, 6.0}, {60.0, 100.0, 18.0, 4.5}};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int row = y / 7;
               boolean seam = y % 7 == 0 || (x + row % 2 * 6) % 12 == 0;
               int stone = AnimatedCapes.lerp(
                  -14805484, -13753312, AnimatedCapes.hash((x + row % 2 * 6) / 12, row) * 0.6 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.4
               );
               c.set(x, y, AnimatedCapes.dither(seam ? -15725558 : stone, x, y));
            }
         }

         c.rect(50.0, 8.0, 26.0, 34.0, -14018032);

         for (int y = 10; y < 40; y++) {
            for (int x = 52; x < 74; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-16116688, -15062448, (y - 10) / 30.0), x, y));
            }
         }

         c.disc(66.0, 18.0, 4.0, -987944);
         c.glow(66.0, 18.0, 10.0, -9797440, 0.3);
         c.rect(62.0, 10.0, 1.5, 30.0, -14018032);
         c.rect(52.0, 24.0, 22.0, 1.5, -14018032);

         for (int i = 0; i < 10; i++) {
            c.star(52.0 + AnimatedCapes.hash(i, 5) * 22.0, 10.0 + AnimatedCapes.hash(i, 6) * 30.0, 0.0, -1, 0.3);
         }

         for (int y = 100; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(
                     AnimatedCapes.lerp(-12966892, -15069688, (y - 100) / 28.0 + Math.sin(x * 0.4 + AnimatedCapes.fbm(x * 0.05, y * 0.4, 2) * 5.0) * 0.08),
                     x,
                     y
                  )
               );
            }
         }

         c.rect(0.0, 100.0, 80.0, 1.0, -10864094);
         c.rect(4.0, 94.0, 12.0, 6.0, -11920870);
         c.rect(4.0, 94.0, 12.0, 1.0, -7706054);
         c.rect(5.0, 88.0, 10.0, 6.0, -15058390);
         c.rect(15.0, 88.0, 1.0, 6.0, -1516352);
         c.polygon(
            new double[][]{{68.0, 100.0}, {76.0, 100.0}, {75.0, 90.0}, {73.0, 88.0}, {73.0, 84.0}, {71.0, 84.0}, {71.0, 88.0}, {69.0, 90.0}},
            AnimatedCapes.alpha(-12935062, 0.7)
         );
         c.rect(70.5, 82.0, 3.0, 2.0, -9811414);

         for (double[] candle : CANDLES) {
            double cx = candle[0];
            double top = candle[1] - candle[2];
            double hw = candle[3];
            c.ellipse(cx, candle[1] + 0.5, hw + 3.0, 1.5, 0.0, -7706064);
            c.polygon(
               new double[][]{{cx - hw, top + 1.0}, {cx + hw, top + 1.0}, {cx + hw, candle[1]}, {cx - hw, candle[1]}},
               (xx, yx) -> AnimatedCapes.lerp(-726320, -4675448, (xx - cx + hw) / (hw * 2.0) * 0.8)
            );

            for (int d = 0; d < 3; d++) {
               double dx = cx - hw + 1.0 + AnimatedCapes.hash((int)cx, d) * (hw * 2.0 - 2.0);
               double len = 3.0 + AnimatedCapes.hash(d, (int)cx) * 8.0;
               c.line(dx, top + 1.0, dx, top + 1.0 + len, 1.2, -2848);
               c.disc(dx, top + 1.0 + len, 0.8, -2848);
            }

            c.ellipse(cx, top + 1.0, hw, 1.2, 0.0, -1816);
            c.line(cx, top + 1.0, cx, top - 1.5, 0.5, -15068144);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < CANDLES.length; k++) {
            double[] candle = CANDLES[k];
            double cx = candle[0];
            double top = candle[1] - candle[2] - 1.5;
            double flicker = AnimatedCapes.noise(t * 7.0 + k * 13, k);
            double sway = (AnimatedCapes.noise(t * 2.0 + k * 5, 3.0) - 0.5) * 2.2;
            double height = 7.0 + flicker * 3.0;
            c.glow(cx, top - 3.0, 40.0, -26048, 0.22 + 0.1 * flicker);
            c.glow(cx, top - 3.0, 10.0, -10096, 0.35 + 0.1 * flicker);
            double tipX = cx + sway;
            double tipY = top - height;
            c.polygon(
               new double[][]{{cx - 2.2, top}, {cx - 2.0, top - height * 0.4}, {tipX, tipY}, {cx + 2.0, top - height * 0.4}, {cx + 2.2, top}, {cx, top + 1.5}},
               -26070
            );
            c.polygon(
               new double[][]{
                  {cx - 1.3, top},
                  {cx - 1.1, top - height * 0.4},
                  {tipX * 0.7 + cx * 0.3, tipY + 2.0},
                  {cx + 1.1, top - height * 0.4},
                  {cx + 1.3, top},
                  {cx, top + 1.0}
               },
               -5984
            );
            c.ellipse(cx, top - 1.5, 0.8, 1.6, 0.0, -1);
            c.ellipse(cx, top + 0.3, 1.2, 0.8, 0.0, AnimatedCapes.alpha(-11896065, 0.8));
            c.line(cx - candle[3] + 0.8, top + 3.0, cx - candle[3] + 0.8, candle[1] - 2.0, 0.6, AnimatedCapes.alpha(-1, 0.35 + 0.2 * flicker));
         }

         double smoke = AnimatedCapes.wrap(t, 7.0);
         if (smoke < 4.0) {
            double cx = CANDLES[2][0];
            double top = CANDLES[2][1] - CANDLES[2][2] - 12.0;

            for (int s = 0; s < 14; s++) {
               double f = s / 14.0;
               c.disc(
                  cx + Math.sin(f * 8.0 - t * 2.0) * f * 5.0,
                  top - f * 30.0,
                  0.6 + f * 1.5,
                  AnimatedCapes.alpha(-5199712, 0.25 * (1.0 - f) * Math.sin(smoke / 4.0 * Math.PI))
               );
            }
         }

         double ma = t * 2.3;
         double mothX = CANDLES[1][0] + Math.cos(ma) * 9.0 + Math.sin(t * 5.0) * 1.5;
         double mothY = CANDLES[1][1] - CANDLES[1][2] - 14.0 + Math.sin(ma * 1.7) * 5.0;
         double flap = Math.abs(Math.sin(t * 25.0));
         c.ellipse(mothX - 1.2 * flap, mothY, 1.4 * flap + 0.3, 1.0, 0.3, -3622752);
         c.ellipse(mothX + 1.2 * flap, mothY, 1.4 * flap + 0.3, 1.0, -0.3, -3622752);
         c.rect(mothX - 0.3, mothY - 0.6, 0.6, 1.4, -10859974);

         for (int i = 0; i < 16; i++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 80.0 + Math.sin(t * 0.2 + i) * 5.0, 80.0);
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 31) * 100.0 - t * (0.5 + AnimatedCapes.hash(i, 32)), 100.0);
            c.add((int)x, (int)y, -10080, 0.25 * (0.5 + 0.5 * Math.sin(t + i)));
         }
      }
   }

   static final class CrystalCave implements AnimatedCapes.Scene {
      private static final double[][] CRYSTALS = new double[][]{
         {12.0, 118.0, -1.25, 26.0, 4.5, 185.0},
         {20.0, 120.0, -1.7, 18.0, 3.5, 200.0},
         {6.0, 116.0, -0.9, 14.0, 3.0, 285.0},
         {62.0, 120.0, -1.9, 30.0, 5.0, 300.0},
         {70.0, 118.0, -2.3, 20.0, 3.5, 270.0},
         {54.0, 121.0, -1.45, 14.0, 3.0, 190.0},
         {36.0, 122.0, -1.6, 10.0, 2.5, 320.0},
         {44.0, 121.0, -1.1, 12.0, 2.5, 175.0},
         {28.0, 122.0, -1.9, 16.0, 3.0, 250.0},
         {81.0, 62.0, -2.9, 16.0, 3.5, 190.0},
         {81.0, 70.0, -2.5, 11.0, 2.8, 300.0},
         {-1.0, 50.0, -0.25, 15.0, 3.2, 290.0},
         {-1.0, 58.0, -0.7, 10.0, 2.6, 185.0},
         {-1.0, 86.0, -0.35, 16.0, 3.5, 320.0},
         {81.0, 92.0, -2.75, 17.0, 3.8, 285.0},
         {31.0, 0.0, 1.5, 14.0, 3.0, 200.0},
         {50.0, 0.0, 1.75, 18.0, 3.5, 290.0},
         {66.0, 1.0, 1.35, 11.0, 2.5, 185.0}
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.06, y * 0.05, 5);
               double open = Math.exp(-Math.pow((x - 40) / 28.0, 2.0)) * Math.exp(-Math.pow((y - 70) / 50.0, 2.0));
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(AnimatedCapes.lerp(-14804946, -12305834, n), 0.5 + 0.35 * (1.0 - open) + n * 0.3), x, y));
            }
         }

         for (int k = 0; k < 9; k++) {
            double sx = 4 + k * 9 + AnimatedCapes.hash(k, 1) * 4.0;
            double len = 8.0 + AnimatedCapes.hash(k, 2) * 18.0;
            c.polygon(
               new double[][]{{sx - 3.0, 0.0}, {sx + 3.0, 0.0}, {sx + 0.4, len}, {sx - 0.4, len}},
               (xx, yx) -> AnimatedCapes.lerp(-14015432, -11911080, (xx - sx + 3.0) / 6.0)
            );
         }

         for (int y = 116; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-15722448, -16381937, (y - 116) / 12.0), x, y));
            }
         }

         for (double[] cr : CRYSTALS) {
            this.crystal(c, cr, false);
         }
      }

      private void crystal(AnimatedCapes.Canvas c, double[] cr, boolean glowOnly) {
         double dx = Math.cos(cr[2]);
         double dy = Math.sin(cr[2]);
         double nx = -dy * cr[4];
         double ny = dx * cr[4];
         double tipX = cr[0] + dx * cr[3];
         double tipY = cr[1] + dy * cr[3];
         double midX = cr[0] + dx * cr[3] * 0.78;
         double midY = cr[1] + dy * cr[3] * 0.78;
         int base = AnimatedCapes.hsv(cr[5], 0.65, 0.85);
         if (!glowOnly) {
            c.polygon(
               new double[][]{{cr[0] + nx, cr[1] + ny}, {midX + nx, midY + ny}, {tipX, tipY}, {midX, midY}, {cr[0], cr[1]}},
               AnimatedCapes.alpha(AnimatedCapes.shade(base, 0.6), 0.95)
            );
            c.polygon(
               new double[][]{{cr[0] - nx, cr[1] - ny}, {midX - nx, midY - ny}, {tipX, tipY}, {midX, midY}, {cr[0], cr[1]}},
               AnimatedCapes.alpha(AnimatedCapes.lerp(base, -1, 0.25), 0.95)
            );
            c.line(cr[0], cr[1], midX, midY, 0.5, AnimatedCapes.alpha(-1, 0.6));
            c.line(midX, midY, tipX, tipY, 0.5, AnimatedCapes.alpha(-1, 0.8));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < CRYSTALS.length; k++) {
            double[] cr = CRYSTALS[k];
            double wave = 0.5 + 0.5 * Math.sin(t * 1.2 - cr[0] * 0.08);
            int col = AnimatedCapes.hsv(cr[5], 0.6, 1.0);
            double cx = cr[0] + Math.cos(cr[2]) * cr[3] * 0.5;
            double cy = cr[1] + Math.sin(cr[2]) * cr[3] * 0.5;
            c.glow(cx, cy, cr[3] * 0.9, col, 0.2 + 0.3 * wave);
            double sweep = AnimatedCapes.wrap(t * 0.5 + k * 0.17, 1.0);
            c.glow(cr[0] + Math.cos(cr[2]) * cr[3] * sweep, cr[1] + Math.sin(cr[2]) * cr[3] * sweep, cr[4] * 1.2, -1, 0.5 * Math.sin(sweep * Math.PI));
         }

         for (int y = 117; y < 128; y++) {
            int src = 116 - (y - 116) * 2;

            for (int x = 0; x < 80; x++) {
               c.add(x, y, c.get(x + (int)Math.round(Math.sin(y * 1.4 + t * 2.0) * 0.8), src), 0.3);
            }
         }

         for (int k = 0; k < 4; k++) {
            double sx = 4 + (k * 2 + 1) * 9 + AnimatedCapes.hash(k * 2 + 1, 1) * 4.0;
            double len = 8.0 + AnimatedCapes.hash(k * 2 + 1, 2) * 18.0;
            double life = AnimatedCapes.wrap(t * 0.4 + k * 0.29, 1.0);
            double y = len + life * life * (116.0 - len) * 1.2;
            if (y < 116.0) {
               c.disc(sx, y, 0.7, AnimatedCapes.alpha(-4658945, 0.9));
            } else {
               double ripple = (y - 116.0) / 20.0;
               c.ellipse(sx, 118.0, 1.0 + ripple * 8.0, 0.5 + ripple, 0.0, AnimatedCapes.alpha(-4658945, 0.5 * (1.0 - ripple)));
            }
         }

         for (int i = 0; i < 20; i++) {
            double x = AnimatedCapes.hash(i, 30) * 80.0 + Math.sin(t * 0.4 + i) * 4.0;
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 31) * 128.0 - t * (1.0 + AnimatedCapes.hash(i, 32) * 2.0), 128.0);
            c.glow(x, y, 2.0, AnimatedCapes.hsv(180.0 + AnimatedCapes.hash(i, 33) * 140.0, 0.5, 1.0), 0.5 * (0.5 + 0.5 * Math.sin(t * 2.0 + i)));
         }
      }
   }

   static final class HotAirBalloons implements AnimatedCapes.Scene {
      private static final int[][] COLORS = new int[][]{{-2084806, -12224}, {-12948768, -1}, {-12537760, -30176}, {-6668080, -12529440}, {-38240, -3936}};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 0.72}, new int[]{-9785624, -4663056, -10064});
         c.glow(14.0, 88.0, 40.0, -12160, 0.5);
         c.disc(14.0, 88.0, 6.0, -2856);

         for (int layer = 0; layer < 3; layer++) {
            final int layerIndex = layer;
            c.fillBelow(xx -> 86 + layerIndex * 9 - 7.0 * AnimatedCapes.fbm(xx * 0.05 + layerIndex * 4, layerIndex, 3), AnimatedCapes.lerp(-6637368, -12944838, layer / 2.0));
         }

         for (int y = 104; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int field = AnimatedCapes.hash(Math.floorDiv(x + y / 3, 9), y / 6) > 0.5 ? -10839494 : -7688118;
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(field, -14001622, AnimatedCapes.fbm(x * 0.2, y * 0.2, 2) * 0.4), x, y));
            }
         }

         for (int y = 96; y < 128; y++) {
            double rx = 40.0 + Math.sin(y * 0.12) * 14.0 + (y - 96) * 0.3;
            double w = 0.6 + (y - 96) * 0.08;
            c.rect(rx - w, y, w * 2.0, 1.0, -8734496);
         }

         for (int i = 0; i < 30; i++) {
            double x = AnimatedCapes.hash(i, 50) * 80.0;
            double y = 104.0 + AnimatedCapes.hash(i, 51) * 22.0;
            c.disc(x, y, 1.2 + (y - 104.0) / 22.0, -14001622);
         }
      }

      private static void balloon(AnimatedCapes.Canvas c, double cx, double cy, double r, int[] colors, double burner, int seed) {
         for (int y = (int)(cy - r - 1.0); y <= cy + r * 1.4; y++) {
            for (int x = (int)(cx - r - 1.0); x <= cx + r + 1.0; x++) {
               double u = (x + 0.5 - cx) / r;
               double v = (y + 0.5 - cy) / r;
               double width;
               if (v <= 0.3) {
                  width = Math.sqrt(Math.max(0.0, 1.0 - v * v));
               } else {
                  width = Math.sqrt(0.91) * (1.0 - (v - 0.3) / 1.05 * 0.72);
               }

               if (!(v < -1.0) && !(v > 1.35) && !(Math.abs(u) > width)) {
                  double across = Math.asin(AnimatedCapes.clamp(u / width, -1.0, 1.0)) / Math.PI + 0.5;
                  int gore = (int)Math.floor(across * 8.0);
                  int col = (gore + seed) % 2 == 0 ? colors[0] : colors[1];
                  if (v > 0.9 && v < 1.0) {
                     col = colors[1];
                  }

                  double light = 0.7 + 0.4 * (0.5 - across) + 0.15 * (1.0 - Math.abs(v));
                  c.blend(x, y, AnimatedCapes.shade(col, light));
               }
            }
         }

         double basketY = cy + r * 1.35 + r * 0.45;
         double bw = r * 0.22;
         c.line(cx - r * 0.19, cy + r * 1.35, cx - bw, basketY, 0.4, -12965350);
         c.line(cx + r * 0.19, cy + r * 1.35, cx + bw, basketY, 0.4, -12965350);
         c.rect(cx - bw, basketY, bw * 2.0, Math.max(1.2, r * 0.25), -7710166);
         if (burner > 0.0) {
            c.glow(cx, cy + r * 1.3, r * 0.6, -20416, burner * 0.8);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 3; k++) {
            double x = AnimatedCapes.wrap(k * 37 + t * (1.5 + k * 0.5), 120.0) - 20.0;
            AnimatedCapes.cloud(c, x, 20 + k * 22, 4 + k, AnimatedCapes.alpha(-1, 0.85), AnimatedCapes.alpha(-2043688, 0.8), k + 7);
         }

         double[] order = new double[]{0.35, 0.5, 0.65, 0.8, 1.0};

         for (int k = 0; k < order.length; k++) {
            double depth = order[k];
            double r = 4.0 + depth * 8.0;
            double cycle = 128.0 + r * 4.0;
            double y = 128.0 + r * 2.0 - AnimatedCapes.wrap(t * (2.0 + depth * 3.0) + AnimatedCapes.hash(k, 1) * cycle, cycle);
            double x = 10.0 + AnimatedCapes.hash(k, 2) * 60.0 + Math.sin(t * 0.2 + k) * 6.0;
            double burner = AnimatedCapes.wrap(t * 0.5 + AnimatedCapes.hash(k, 3), 1.0) < 0.15 ? 1.0 : 0.0;
            balloon(c, x, y, r, COLORS[k], burner, k);
         }

         for (int k = 0; k < 3; k++) {
            AnimatedCapes.bird(c, AnimatedCapes.wrap(t * 5.0 + k * 30, 100.0) - 10.0, 56 + k * 5, 1.4, t * 7.0 + k, -1339413958);
         }
      }
   }

   static final class Hourglass implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double NECK = 64.0;

      private static double halfWidth(double y) {
         double d = Math.min(1.0, Math.abs(y - 64.0) / 46.0);
         return 1.4 + 17.0 * Math.pow(Math.sin(d * Math.PI), 0.7) + 4.0 * Math.pow(d, 6.0);
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-16381926, -15462352});

         for (int i = 0; i < 140; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               AnimatedCapes.hash(i, 4) > 0.95 ? 1.0 : 0.0,
               -2039553,
               0.1 + AnimatedCapes.hash(i, 3) * 0.4
            );
         }

         double[][] constellation = new double[][]{
            {8.0, 12.0}, {16.0, 20.0}, {12.0, 30.0}, {22.0, 34.0}, {66.0, 96.0}, {72.0, 104.0}, {64.0, 112.0}, {74.0, 118.0}
         };

         for (int i = 0; i < constellation.length - 1; i++) {
            if (i != 3) {
               c.line(constellation[i][0], constellation[i][1], constellation[i + 1][0], constellation[i + 1][1], 0.4, AnimatedCapes.alpha(-7697728, 0.4));
            }
         }

         for (double[] s : constellation) {
            c.star(s[0], s[1], 1.0, -1, 0.8);
         }

         for (int side = -1; side <= 1; side += 2) {
            double px = 40.0 + side * 24;

            for (int y = 16; y < 112; y++) {
               double twist = Math.sin(y * 0.6) * 1.2;
               c.rect(px - 1.5, y, 3.0, 1.0, AnimatedCapes.lerp(-9811430, -1523600, 0.5 + 0.5 * Math.sin(y * 0.6 + side)));
               c.rect(px + twist - 0.5, y, 1.0, 1.0, -11915248);
            }
         }

         for (int k = 0; k < 2; k++) {
            double py = k == 0 ? 10.0 : 110.0;
            c.polygon(
               new double[][]{{10.0, py}, {70.0, py}, {68.0, py + 7.0}, {12.0, py + 7.0}},
               (x, y) -> AnimatedCapes.lerp(-11916782, -7710166, 0.5 + 0.5 * Math.sin(x * 0.3))
            );
            c.rect(10.0, py, 60.0, 1.0, -1523600);
            c.rect(12.0, py + 6.0, 56.0, 1.0, -14018550);

            for (int d = 0; d < 5; d++) {
               c.disc(18 + d * 11, py + 3.5, 1.2, -2578352);
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycle = AnimatedCapes.wrap(t, 12.0);
         double p = Math.min(1.0, cycle / 10.0);
         double reset = cycle > 10.0 ? (cycle - 10.0) / 2.0 : 0.0;
         double topLevel = 62.0 - (1.0 - p) * 38.0;
         double pileTop = 107.0 - p * 34.0;

         for (int y = 18; y < 110; y++) {
            double hw = halfWidth(y);

            for (int x = (int)(40.0 - hw - 1.0); x <= (int)(40.0 + hw + 1.0); x++) {
               double cover = AnimatedCapes.clamp(hw + 0.5 - Math.abs(x + 0.5 - 40.0), 0.0, 1.0);
               if (!(cover <= 0.0)) {
                  double dx = Math.abs(x + 0.5 - 40.0);
                  boolean sand;
                  if (y < 64.0) {
                     double sag = Math.max(0.0, 4.0 * (1.0 - dx / 10.0)) * (p < 1.0 ? 1 : 0);
                     sand = p < 1.0 && y > topLevel + sag && y < 64.0;
                  } else {
                     sand = y > pileTop + dx * 0.45 && p > 0.01;
                  }

                  int col;
                  if (sand && reset < 0.5) {
                     col = AnimatedCapes.lerp(-997256, -5211590, AnimatedCapes.hash(x, y) * 0.5 + dx / 40.0);
                     if (AnimatedCapes.hash(x * 3, y + (int)(t * 4.0)) > 0.985) {
                        col = -1;
                     }
                  } else {
                     col = AnimatedCapes.alpha(-9794880, 0.18);
                  }

                  c.blend(x, y, AnimatedCapes.alpha(col, (col >>> 24) / 255.0 * cover));
                  double u = (x + 0.5 - 40.0) / hw;
                  if (Math.abs(u + 0.6) < 0.1) {
                     c.add(x, y, -1, 0.2);
                  }

                  if (Math.abs(Math.abs(u) - 0.96) < 0.05) {
                     c.add(x, y, -4667152, 0.25);
                  }
               }
            }
         }

         if (p < 1.0 && reset == 0.0) {
            for (double y = 64.0; y < pileTop + 1.0; y++) {
               double jitter = (AnimatedCapes.hash((int)y, (int)(t * 20.0)) - 0.5) * 0.8;
               c.add((int)Math.floor(40.0 + jitter), (int)y, -997256, 0.9);
            }

            c.glow(40.0, pileTop, 4.0, -10096, 0.35);
         }

         if (reset > 0.0) {
            c.glow(40.0, 64.0, 30.0 * reset + 5.0, -5952, Math.sin(reset * Math.PI) * 1.2);
         }

         for (int i = 0; i < 8; i++) {
            double life = AnimatedCapes.wrap(t * 0.3 + AnimatedCapes.hash(i, 20), 1.0);
            double a = life * Math.PI * 2.0 + i;
            c.star(40.0 + Math.cos(a) * 28.0, 64.0 + Math.sin(a) * 50.0, 1.0, -8032, 0.5 * Math.sin(life * Math.PI));
         }
      }
   }

   static final class JungleWaterfall implements AnimatedCapes.Scene {
      private static double fallLeft(double y) {
         return 31.0 - Math.max(0.0, y - 80.0) * 0.12;
      }

      private static double fallRight(double y) {
         return 49.0 + Math.max(0.0, y - 80.0) * 0.12;
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.25}, new int[]{-7677720, -2559776});
         AnimatedCapes.mountains(c, 4, 34.0, 14.0, 0.08, -10839414, 0);
         c.fillBelow(x -> 30.0 + 4.0 * AnimatedCapes.fbm(x * 0.2, 5.0, 3), -12944822);

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double edgeL = 26.0 + 6.0 * AnimatedCapes.fbm(y * 0.08, 1.0, 3) - Math.max(0, y - 80) * 0.1;
               double edgeR = 54.0 - 6.0 * AnimatedCapes.fbm(y * 0.08, 9.0, 3) + Math.max(0, y - 80) * 0.1;
               if ((x < edgeL || x > edgeR) && y > 18) {
                  double n = AnimatedCapes.fbm(x * 0.12, y * 0.1, 4);
                  int rock = AnimatedCapes.lerp(-12961228, -9803174, n);
                  rock = AnimatedCapes.shade(rock, 0.7 + Math.abs(Math.sin(y * 0.3 + n * 5.0)) * 0.35);
                  if (AnimatedCapes.fbm(x * 0.2 + 5.0, y * 0.15, 3) > 0.55) {
                     rock = AnimatedCapes.lerp(rock, -12940742, 0.8);
                  }

                  c.set(x, y, AnimatedCapes.dither(rock, x, y));
               }
            }
         }

         for (int y = 104; y < 128; y++) {
            for (int xx = 0; xx < 80; xx++) {
               c.set(xx, y, AnimatedCapes.dither(AnimatedCapes.lerp(-12924736, -16094598, (y - 104) / 24.0), xx, y));
            }
         }

         for (int k = 0; k < 7; k++) {
            double vx = 4 + k * 12 + AnimatedCapes.hash(k, 1) * 4.0;
            double len = 20.0 + AnimatedCapes.hash(k, 2) * 40.0;
            double px = vx;

            for (double y = 18.0; y < 18.0 + len; y += 2.0) {
               double nx = vx + Math.sin(y * 0.15 + k) * 1.5;
               c.line(px, y - 2.0, nx, y, 0.6, -13997526);
               if (((int)y & 6) == 0) {
                  c.ellipse(nx + 1.2, y, 1.4, 0.7, 0.6, -11883974);
               }

               px = nx;
            }
         }

         double[][] leaves = new double[][]{
            {-4.0, 108.0, 0.2, 16.0},
            {6.0, 120.0, -0.4, 14.0},
            {84.0, 106.0, 2.941592653589793, 16.0},
            {74.0, 122.0, 3.541592653589793, 13.0},
            {-2.0, 90.0, 0.5, 12.0},
            {82.0, 88.0, 2.641592653589793, 12.0}
         };

         for (double[] leaf : leaves) {
            double lx = leaf[0];
            double ly = leaf[1];
            double a = leaf[2];
            double len = leaf[3];

            for (int s = 0; s < 10; s++) {
               double f = s / 9.0;
               double cx = lx + Math.cos(a) * len * f;
               double cy = ly + Math.sin(a) * len * f - Math.sin(f * Math.PI) * 3.0;
               double half = Math.sin(f * Math.PI) * len * 0.35;

               for (int side = -1; side <= 1; side += 2) {
                  double ex = cx + Math.cos(a + side * Math.PI / 2.0) * half;
                  double ey = cy + Math.sin(a + side * Math.PI / 2.0) * half;
                  c.line(cx, cy, ex, ey, 1.4, side < 0 ? -13993430 : -12936646);
               }
            }

            c.line(lx, ly, lx + Math.cos(a) * len, ly + Math.sin(a) * len - 1.0, 0.5, -7681430);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 20; y < 106; y++) {
            double left = fallLeft(y);
            double right = fallRight(y);

            for (int x = (int)left; x <= (int)right; x++) {
               double u = (x + 0.5 - left) / (right - left);
               double streak = AnimatedCapes.noise(x * 0.7, y * 0.06 - t * 3.5) * 0.6 + AnimatedCapes.noise(x * 1.9, y * 0.15 - t * 5.0) * 0.4;
               double edge = Math.min(u, 1.0 - u);
               int col = AnimatedCapes.ramp(streak, new double[]{0.2, 0.5, 0.8}, new int[]{-12936520, -5707532, -1});
               c.blend(x, y, AnimatedCapes.alpha(col, AnimatedCapes.clamp(edge * 8.0, 0.0, 1.0) * 0.95));
            }
         }

         for (int y = 18; y < 22; y++) {
            for (int x = 30; x < 50; x++) {
               c.blend(x, y, AnimatedCapes.alpha(-1, 0.6 * AnimatedCapes.noise(x * 0.8 + t * 2.0, y)));
            }
         }

         for (int y = 104; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.noise(x * 0.2 + Math.sin(t) * 0.3, (y - t * 4.0) * 0.4);
               c.add(x, y, -4653064, Math.pow(n, 4.0) * 0.5);
            }
         }

         for (int y = 80; y < 118; y++) {
            for (int x = 10; x < 70; x++) {
               double spread = 1.0 - Math.abs(x - 40) / 30.0;
               double n = AnimatedCapes.fbm(x * 0.1, y * 0.1 + t * 0.6, 3);
               c.blend(x, y, AnimatedCapes.alpha(-984321, AnimatedCapes.smoothstep(0.4, 0.7, n) * spread * (1.0 - Math.abs(y - 104) / 16.0) * 0.8));
            }
         }

         for (int y = 70; y < 100; y++) {
            for (int x = 14; x < 66; x++) {
               double r = Math.hypot(x - 40, (y - 104) * 1.2);
               if (r > 26.0 && r < 33.0) {
                  c.add(x, y, AnimatedCapes.hsv((r - 26.0) / 7.0 * 280.0, 0.8, 1.0), 0.1 * (0.6 + 0.4 * Math.sin(t * 0.7)));
               }
            }
         }

         for (int i = 0; i < 24; i++) {
            double life = AnimatedCapes.wrap(t * 1.2 + AnimatedCapes.hash(i, 40), 1.0);
            double vx = (AnimatedCapes.hash(i, 41) - 0.5) * 30.0;
            c.add((int)(40.0 + vx * life), (int)(104.0 - life * 14.0 + life * life * 10.0), -1, 0.7 * (1.0 - life));
         }

         double parrot = AnimatedCapes.wrap(t, 12.0);
         if (parrot < 4.0) {
            double px = -8.0 + parrot * 24.0;
            double py = 44.0 + Math.sin(parrot * 2.0) * 4.0;
            double flap = Math.sin(t * 14.0);
            c.ellipse(px, py, 3.0, 1.5, 0.2, -2088918);
            c.polygon(new double[][]{{px - 1.0, py}, {px + 1.0, py}, {px, py - 5.0 * flap}}, -14001440);
            c.polygon(new double[][]{{px - 3.0, py}, {px - 8.0, py + 2.0}, {px - 3.0, py + 1.0}}, -14001440);
            c.disc(px + 3.0, py - 0.8, 1.3, -2088918);
            c.rect(px + 4.0, py - 0.8, 1.0, 1.0, -12224);
         }
      }
   }

   static final class PixelQuest implements AnimatedCapes.Scene {
      private static final String[] KNIGHT = new String[]{
         "..HHHH..", ".HHHHHH.", ".HSSSSH.", ".HSKSKH.", "..SSSS..", "RRBBBBR.", ".BBBBBB.", ".BBYYBB.", "..L..L..", ".LL..LL."
      };
      private static final String[] STRIDE = new String[]{"...LL...", "..L..L.."};
      private static final String[] SLIME = new String[]{"..GGGG..", ".GGGGGG.", "GGKGGKGG", "GGGGGGGG"};
      private static final int[] DIGITS = new int[]{31599, 11415, 29671, 29647, 23497, 31183, 31215, 29257, 31727, 31695};
      private static final int GROUND = 108;
      private static final int TILE = 8;

      private static int color(char ch) {
         return switch (ch) {
            case 'B' -> -12952896;
            default -> 0;
            case 'G' -> -11874230;
            case 'H' -> -4669232;
            case 'K' -> -15066582;
            case 'L' -> -10864102;
            case 'R' -> -2086342;
            case 'S' -> -997216;
            case 'Y' -> -12224;
         };
      }

      private static void sprite(AnimatedCapes.Canvas c, String[] rows, double x, double y, int scale) {
         for (int r = 0; r < rows.length; r++) {
            for (int col = 0; col < rows[r].length(); col++) {
               int argb = color(rows[r].charAt(col));
               if (argb != 0) {
                  c.rect(Math.floor(x) + col * scale, Math.floor(y) + r * scale, scale, scale, argb);
               }
            }
         }
      }

      private static boolean gap(int tile) {
         return tile > 3 && AnimatedCapes.hash(tile, 1) > 0.86 && AnimatedCapes.hash(tile - 1, 1) <= 0.86;
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-10839297, -6631169});
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double scroll = t * 18.0;

         for (int k = 0; k < 5; k++) {
            double x = AnimatedCapes.wrap(k * 23 - scroll * 0.2, 104.0) - 12.0;
            double y = 14.0 + AnimatedCapes.hash(k, 3) * 30.0;
            c.rect(Math.floor(x), Math.floor(y), 12.0, 4.0, -1);
            c.rect(Math.floor(x) + 3.0, Math.floor(y) - 3.0, 6.0, 3.0, -1);
            c.rect(Math.floor(x), Math.floor(y) + 4.0, 12.0, 1.0, -3612424);
         }

         for (int x = 0; x < 80; x++) {
            double world = x + scroll * 0.45;
            int step = (int)Math.floor(world / 4.0);
            double top = 80.0 - Math.abs(Math.sin(step * 0.35)) * 16.0;

            for (int y = (int)top; y < 108; y++) {
               c.set(x, y, (step + y / 4) % 5 == 0 ? -12936630 : -11882406);
            }
         }

         int firstTile = (int)Math.floor(scroll / 8.0);

         for (int tile = firstTile; tile <= firstTile + 10 + 1; tile++) {
            double sx = tile * 8 - scroll;
            if (!gap(tile)) {
               for (int y = 108; y < 128; y++) {
                  for (int px = 0; px < 8; px++) {
                     int col;
                     if (y < 111) {
                        col = y == 108 ? -7671702 : -11878336;
                     } else {
                        boolean mortar = (y - 108) % 5 == 0 || (px + (y - 108) / 5 % 2 * 4) % 8 == 0;
                        col = mortar ? -9815526 : -5215696;
                     }

                     c.set((int)Math.floor(sx) + px, y, col);
                  }
               }

               if (AnimatedCapes.hash(tile, 5) > 0.8 && !gap(tile + 1)) {
                  double bump = Math.max(0.0, Math.sin(t * 3.0 + tile)) > 0.95 ? 2.0 : 0.0;
                  double by = 70.0 - bump;
                  c.rect(Math.floor(sx), by, 8.0, 8.0, -2056160);
                  c.rect(Math.floor(sx), by, 8.0, 1.0, -8080);
                  c.rect(Math.floor(sx) + 3.0, by + 2.0, 2.0, 1.0, -1);
                  c.rect(Math.floor(sx) + 4.0, by + 3.0, 1.0, 1.0, -1);
                  c.rect(Math.floor(sx) + 3.0, by + 5.0, 1.0, 1.0, -1);
               }

               if (AnimatedCapes.hash(tile, 6) > 0.6) {
                  double spin = Math.abs(Math.cos(t * 5.0 + tile));
                  double cy = 86.0 - Math.sin(t * 3.0 + tile) * 1.5;
                  c.ellipse(sx + 4.0, cy, Math.max(0.6, 2.5 * spin), 3.0, 0.0, -12256);
                  c.ellipse(sx + 4.0 - spin * 0.6, cy - 0.8, Math.max(0.3, 1.0 * spin), 1.2, 0.0, -1856);
               }

               if (AnimatedCapes.hash(tile, 7) > 0.85 && tile > firstTile + 5 && !gap(tile - 1) && !gap(tile + 1)) {
                  double hop = Math.abs(Math.sin(t * 4.0 + tile)) * 3.0;
                  sprite(c, SLIME, sx, 100.0 - hop, 2);
               }
            }
         }

         double heroWorld = scroll + 20.0;
         double jump = 0.0;

         for (int tilex = (int)Math.floor(heroWorld / 8.0) - 2; tilex <= (int)Math.floor(heroWorld / 8.0) + 3; tilex++) {
            boolean hazard = gap(tilex) || AnimatedCapes.hash(tilex, 7) > 0.85;
            if (hazard) {
               double p = (heroWorld + 8.0 - (tilex * 8 - 12)) / 36.0;
               if (p > 0.0 && p < 1.0) {
                  jump = Math.max(jump, Math.sin(p * Math.PI) * 24.0);
               }
            }
         }

         String[] hero = (String[])KNIGHT.clone();
         if (jump == 0.0) {
            int frame = (int)Math.floor(t * 8.0) % 2;
            hero[8] = frame == 0 ? KNIGHT[8] : STRIDE[0];
            hero[9] = frame == 0 ? KNIGHT[9] : STRIDE[1];
         }

         double heroY = 88.0 - jump;
         c.rect(22.0, 107.0, 12.0 - jump * 0.2, 1.0, AnimatedCapes.alpha(-16777216, 0.25));
         double scarf = Math.sin(t * 12.0) > 0.0 ? 0.0 : 2.0;
         c.rect(18.0, heroY + 11.0 + scarf, 4.0, 2.0, -2086342);
         sprite(c, hero, 20.0, heroY, 2);
         int score = (int)Math.floor(t * 37.0) % 100000;
         c.rect(0.0, 0.0, 80.0, 9.0, AnimatedCapes.alpha(-16777216, 0.35));

         for (int d = 0; d < 5; d++) {
            int digit = score / (int)Math.pow(10.0, 4 - d) % 10;

            for (int bit = 0; bit < 15; bit++) {
               if ((DIGITS[digit] >> 14 - bit & 1) != 0) {
                  c.set(4 + d * 4 + bit % 3, 2 + bit / 3, -1);
               }
            }
         }

         for (int h = 0; h < 3; h++) {
            double hx = 56 + h * 7;
            c.rect(hx, 3.0, 2.0, 2.0, -2086342);
            c.rect(hx + 3.0, 3.0, 2.0, 2.0, -2086342);
            c.rect(hx, 4.0, 5.0, 2.0, -2086342);
            c.rect(hx + 1.0, 6.0, 3.0, 1.0, -2086342);
            c.rect(hx + 2.0, 7.0, 1.0, 1.0, -2086342);
         }
      }
   }

   static final class RuneCircle implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 66.0;
      private static final int[] RUNES = new int[]{186, 341, 487, 313, 214, 410, 175, 466};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int tx = Math.floorDiv(x + (Math.floorDiv(y, 16) % 2 == 0 ? 0 : 10), 20);
               int ty = Math.floorDiv(y, 16);
               boolean seam = (x + (ty % 2 == 0 ? 0 : 10)) % 20 == 0 || y % 16 == 0;
               int stone = AnimatedCapes.lerp(-14014416, -12172722, AnimatedCapes.hash(tx, ty) * 0.6 + AnimatedCapes.fbm(x * 0.2, y * 0.2, 3) * 0.4);
               if (AnimatedCapes.fbm(x * 0.1 + 30.0, y * 0.1, 3) > 0.62) {
                  stone = AnimatedCapes.lerp(stone, -14005718, 0.5);
               }

               c.set(x, y, AnimatedCapes.dither(seam ? -15593450 : stone, x, y));
            }
         }

         for (int k = 0; k < 5; k++) {
            double x = AnimatedCapes.hash(k, 1) * 80.0;
            double y = AnimatedCapes.hash(k, 2) * 128.0;
            double a = AnimatedCapes.hash(k, 3) * 6.28;

            for (int s = 0; s < 5; s++) {
               double nx = x + Math.cos(a) * 3.0;
               double ny = y + Math.sin(a) * 3.0;
               c.line(x, y, nx, ny, 0.5, -15856622);
               x = nx;
               y = ny;
               a += (AnimatedCapes.hash(k, s + 4) - 0.5) * 1.4;
            }
         }

         c.vignette(0.6);
      }

      private static void runeAt(AnimatedCapes.Canvas c, double x, double y, int rune, int color, double amount) {
         for (int py = 0; py < 3; py++) {
            for (int px = 0; px < 3; px++) {
               if ((rune >> 8 - (py * 3 + px) & 1) != 0) {
                  c.add((int)Math.floor(x) + px - 1, (int)Math.floor(y) + py - 1, color, amount);
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double pulse = 0.5 + 0.5 * Math.sin(t * 2.0);
         c.glow(40.0, 66.0, 46.0, -9815297, 0.3 + 0.15 * pulse);
         int cyan = -11474689;
         int violet = -5213953;

         for (int s = 0; s < 120; s++) {
            double a0 = s * Math.PI * 2.0 / 120.0;
            double a1 = (s + 1) * Math.PI * 2.0 / 120.0;

            for (double r : new double[]{36.0, 33.0, 25.0, 16.0}) {
               c.line(
                  40.0 + Math.cos(a0) * r,
                  66.0 + Math.sin(a0) * r,
                  40.0 + Math.cos(a1) * r,
                  66.0 + Math.sin(a1) * r,
                  0.7,
                  AnimatedCapes.alpha(r > 30.0 ? cyan : violet, 0.85)
               );
            }
         }

         double outer = t * 0.3;

         for (int k = 0; k < 18; k++) {
            double a = outer + k * Math.PI * 2.0 / 18.0;
            runeAt(c, 40.0 + Math.cos(a) * 34.5, 66.0 + Math.sin(a) * 34.5, RUNES[k % RUNES.length], cyan, 0.9);
         }

         double middle = -t * 0.45;

         for (int k = 0; k < 2; k++) {
            double[][] tri = new double[3][];

            for (int v = 0; v < 3; v++) {
               double a = middle + k * Math.PI / 3.0 + v * Math.PI * 2.0 / 3.0;
               tri[v] = new double[]{40.0 + Math.cos(a) * 25.0, 66.0 + Math.sin(a) * 25.0};
            }

            for (int v = 0; v < 3; v++) {
               c.beam(tri[v][0], tri[v][1], tri[(v + 1) % 3][0], tri[(v + 1) % 3][1], 0.6, violet, 0.8);
            }
         }

         for (int k = 0; k < 6; k++) {
            double a = middle + k * Math.PI / 3.0 + (Math.PI / 6);
            c.ring(40.0 + Math.cos(a) * 20.5, 66.0 + Math.sin(a) * 20.5, 2.0, 0.6, AnimatedCapes.alpha(violet, 0.9));
            runeAt(c, 40.0 + Math.cos(a) * 20.5, 66.0 + Math.sin(a) * 20.5, RUNES[(k + 3) % RUNES.length], -1, 0.7);
         }

         double inner = t * 0.8;

         for (int k = 0; k < 8; k++) {
            double a = inner + k * Math.PI / 4.0;
            c.line(
               40.0 + Math.cos(a) * 6.0, 66.0 + Math.sin(a) * 6.0, 40.0 + Math.cos(a) * 15.0, 66.0 + Math.sin(a) * 15.0, 0.5, AnimatedCapes.alpha(cyan, 0.7)
            );
         }

         c.glow(40.0, 66.0, 12.0, -1, 0.5 + 0.4 * pulse);
         c.disc(40.0, 66.0, 2.5 + pulse, -1509121);

         for (int k = 0; k < 4; k++) {
            double a = outer * 2.0 + k * Math.PI / 2.0;
            double px = 40.0 + Math.cos(a) * 36.0;
            double py = 66.0 + Math.sin(a) * 36.0;

            for (int y = (int)py; y > py - 50.0; y--) {
               double f = (py - y) / 50.0;
               c.add((int)px, y, cyan, 0.5 * (1.0 - f));
               c.add((int)px - 1, y, cyan, 0.2 * (1.0 - f));
               c.add((int)px + 1, y, cyan, 0.2 * (1.0 - f));
            }
         }

         if (AnimatedCapes.wrap(t, 3.0) < 0.25) {
            int n = (int)Math.floor(t / 3.0);
            double a0 = AnimatedCapes.hash(n, 1) * 6.28;
            double a1 = a0 + 1.5 + AnimatedCapes.hash(n, 2) * 2.0;
            double x = 40.0 + Math.cos(a0) * 34.0;
            double y = 66.0 + Math.sin(a0) * 34.0;

            for (int s = 1; s <= 6; s++) {
               double f = s / 6.0;
               double a = a0 + (a1 - a0) * f;
               double r = 34.0 - Math.sin(f * Math.PI) * 18.0 + (AnimatedCapes.hash(n, s + 3) - 0.5) * 5.0;
               double nx = 40.0 + Math.cos(a) * r;
               double ny = 66.0 + Math.sin(a) * r;
               c.beam(x, y, nx, ny, 0.5, -1511169, 0.9);
               x = nx;
               y = ny;
            }
         }

         for (int i = 0; i < 30; i++) {
            double life = AnimatedCapes.wrap(t * 0.3 + AnimatedCapes.hash(i, 20), 1.0);
            double a = AnimatedCapes.hash(i, 21) * 6.28 + life * 4.0;
            double r = 8.0 + AnimatedCapes.hash(i, 22) * 26.0;
            c.add(
               (int)(40.0 + Math.cos(a) * r),
               (int)(66.0 + Math.sin(a) * r * 0.9 - life * 40.0),
               AnimatedCapes.hash(i, 23) > 0.5 ? cyan : violet,
               Math.sin(life * Math.PI)
            );
         }
      }
   }

   static final class SolarSystem implements AnimatedCapes.Scene {
      private static final double ROT = 1.05;
      private static final double MINOR = 0.36;
      private static final double[][] PLANETS = new double[][]{
         {10.0, 1.6, 0.9, -6645094.0},
         {15.0, 1.2, 1.4, -1519462.0},
         {21.0, 1.0, 1.6, -1.2944672E7},
         {27.0, 0.8, 1.2, -3121094.0},
         {37.0, 0.45, 3.4, -2576248.0},
         {48.0, 0.33, 2.8, -1519456.0},
         {57.0, 0.24, 2.0, -7677728.0}
      };

      private static double[] orbit(double r, double angle) {
         double lx = Math.cos(angle) * r;
         double ly = Math.sin(angle) * r * 0.36;
         return new double[]{40.0 + lx * Math.cos(1.05) - ly * Math.sin(1.05), 64.0 + lx * Math.sin(1.05) + ly * Math.cos(1.05)};
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double along = (x - y * 0.4 - 10.0) / 18.0;
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(AnimatedCapes.lerp(-16645366, -15067088, Math.exp(-along * along) * AnimatedCapes.fbm(x * 0.05, y * 0.05, 4)), x, y)
               );
            }
         }

         for (int i = 0; i < 180; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               AnimatedCapes.hash(i, 4) > 0.97 ? 1.0 : 0.0,
               -986881,
               0.1 + AnimatedCapes.hash(i, 3) * 0.4
            );
         }

         for (double[] planet : PLANETS) {
            for (int s = 0; s < 160; s++) {
               double[] p = orbit(planet[0], s * Math.PI * 2.0 / 160.0);
               c.add((int)p[0], (int)p[1], -9798992, 0.12);
            }
         }
      }

      private void planet(AnimatedCapes.Canvas c, double[] pos, double[] planet, int index, double t) {
         double size = planet[2];
         int col = (int)planet[3];
         double sunDx = 40.0 - pos[0];
         double sunDy = 64.0 - pos[1];
         double len = Math.max(0.01, Math.hypot(sunDx, sunDy));
         c.disc(pos[0], pos[1], size, AnimatedCapes.shade(col, 0.35));
         c.disc(pos[0] + sunDx / len * size * 0.35, pos[1] + sunDy / len * size * 0.35, size * 0.72, col);
         if (index == 4) {
            for (int b = -1; b <= 1; b++) {
               c.line(pos[0] - size * 0.8, pos[1] + b * 1.1, pos[0] + size * 0.8, pos[1] + b * 1.1, 0.5, AnimatedCapes.alpha(-7710150, 0.6));
            }

            c.disc(pos[0] + size * 0.3, pos[1] + size * 0.4, 0.7, -4173782);
         } else if (index == 5) {
            c.ellipse(pos[0], pos[1], size * 2.3, size * 0.6, -0.22079632679489652, AnimatedCapes.alpha(-1517392, 0.7));
            c.disc(pos[0] + sunDx / len * size * 0.35, pos[1] + sunDy / len * size * 0.35, size * 0.72, col);
         } else if (index == 2) {
            double a = t * 3.0;
            c.disc(pos[0] + Math.cos(a) * 3.0, pos[1] + Math.sin(a) * 1.4, 0.6, -3092272);
            c.disc(pos[0] + sunDx / len * 0.5, pos[1] + sunDy / len * 0.5, size * 0.4, AnimatedCapes.alpha(-12930982, 0.8));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] positions = new double[PLANETS.length][];
         double[] depth = new double[PLANETS.length];

         for (int k = 0; k < PLANETS.length; k++) {
            double angle = t * PLANETS[k][1] * 0.35 + k * 1.9;
            positions[k] = orbit(PLANETS[k][0], angle);
            depth[k] = Math.sin(angle);
         }

         for (int i = 0; i < 140; i++) {
            double a = t * 0.12 * (1.0 + AnimatedCapes.hash(i, 40) * 0.2) + AnimatedCapes.hash(i, 41) * Math.PI * 2.0;
            double[] p = orbit(31.0 + AnimatedCapes.hash(i, 42) * 3.0, a);
            c.add((int)p[0], (int)p[1], -5201776, 0.35);
         }

         for (int k = 0; k < PLANETS.length; k++) {
            if (depth[k] < 0.0) {
               this.planet(c, positions[k], PLANETS[k], k, t);
            }
         }

         for (int y = 50; y < 78; y++) {
            for (int x = 26; x < 54; x++) {
               double dx = x + 0.5 - 40.0;
               double dy = y + 0.5 - 64.0;
               double d = Math.hypot(dx, dy);
               double a = Math.atan2(dy, dx);
               double flame = 6.0 + 2.5 * AnimatedCapes.fbm(a * 3.0 + 10.0, t * 0.8 + d * 0.1, 3);
               if (d < 5.5) {
                  double n = AnimatedCapes.fbm(x * 0.4 + t * 0.3, y * 0.4, 3);
                  c.set(x, y, AnimatedCapes.ramp(n + (1.0 - d / 5.5) * 0.3, new double[]{0.3, 0.6, 0.9}, new int[]{-2076656, -20432, -2880}));
               } else if (d < flame) {
                  c.add(x, y, -32736, (flame - d) / (flame - 5.5) * 0.8);
               }
            }
         }

         c.glow(40.0, 64.0, 22.0, -24512, 0.5);

         for (int kx = 0; kx < PLANETS.length; kx++) {
            if (depth[kx] >= 0.0) {
               this.planet(c, positions[kx], PLANETS[kx], kx, t);
            }
         }

         double ca = AnimatedCapes.wrap(t * 0.09, Math.PI * 2);
         double cr = 22.0 / (1.0 + 0.8 * Math.cos(ca));
         double cxl = Math.cos(ca) * cr - 12.0;
         double cyl = Math.sin(ca) * cr * 0.7;
         double cometX = 40.0 + cxl * Math.cos(-0.5) - cyl * Math.sin(-0.5);
         double cometY = 64.0 + cxl * Math.sin(-0.5) + cyl * Math.cos(-0.5);
         double awayX = cometX - 40.0;
         double awayY = cometY - 64.0;
         double al = Math.max(0.01, Math.hypot(awayX, awayY));
         double tail = 4.0 + 180.0 / (al + 6.0);
         c.beam(cometX, cometY, cometX + awayX / al * tail, cometY + awayY / al * tail, 0.8, -6627073, 0.6);
         c.glow(cometX, cometY, 3.0, -2033409, 0.8);
      }
   }
}
