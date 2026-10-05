package dev.lyfw.lyfwclient.render;

final class CapeScenesB {
   private CapeScenesB() {
   }

   static final class CoralReef implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 1.0}, new int[]{-11876128, -15045976, -16111014});

         for (int x = 0; x < 80; x++) {
            double top = 108.0 + 4.0 * Math.sin(x * 0.08) + 2.0 * AnimatedCapes.fbm(x * 0.1, 3.0, 3);

            for (int y = (int)top; y < 128; y++) {
               double ripple = Math.sin(x * 0.4 + y * 1.2 + AnimatedCapes.fbm(x * 0.1, y * 0.1, 2) * 4.0) * 0.5 + 0.5;
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-2572150, -7701926, ripple * 0.4 + (y - top) / 40.0), x, y));
            }
         }

         c.ellipse(14.0, 110.0, 12.0, 6.0, 0.0, -11908518);
         c.ellipse(66.0, 112.0, 10.0, 5.0, 0.0, -12961206);
         c.disc(20.0, 104.0, 6.0, -3638694);

         for (int k = 0; k < 10; k++) {
            double a = k * 0.63;
            c.ring(20.0, 104.0, 1.0 + k * 0.5, 0.4, AnimatedCapes.alpha(-7714246, 0.6));
            c.line(
               20.0 + Math.cos(a) * 2.0,
               104.0 + Math.sin(a) * 2.0,
               20.0 + Math.cos(a) * 5.5,
               104.0 + Math.sin(a) * 5.5,
               0.4,
               AnimatedCapes.alpha(-1533318, 0.5)
            );
         }

         this.branch(c, 58.0, 110.0, -Math.PI / 2, 14.0, 4, -38262);
         this.branch(c, 70.0, 112.0, -1.2707963267948965, 10.0, 3, -24512);
         this.branch(c, 8.0, 108.0, -1.7707963267948965, 11.0, 3, -2073904);

         for (int k = 0; k < 4; k++) {
            double x = 34 + k * 4;
            double h = 10.0 + AnimatedCapes.hash(k, 9) * 8.0;
            c.rect(x, 112.0 - h, 3.0, h, AnimatedCapes.lerp(-7716672, -10868080, k / 4.0));
            c.ellipse(x + 1.5, 112.0 - h, 1.5, 0.7, 0.0, -14020544);
         }

         for (int k = 0; k < 16; k++) {
            double a = -2.4707963267948965 + k * 0.12;
            c.line(46.0, 106.0, 46.0 + Math.cos(a) * 16.0, 106.0 + Math.sin(a) * 16.0, 0.5, AnimatedCapes.alpha(-12224, 0.8));
         }
      }

      private void branch(AnimatedCapes.Canvas c, double x, double y, double angle, double length, int depth, int color) {
         double nx = x + Math.cos(angle) * length;
         double ny = y + Math.sin(angle) * length;
         c.line(x, y, nx, ny, depth * 0.7 + 0.5, AnimatedCapes.shade(color, 0.7 + depth * 0.08));
         if (depth == 0) {
            c.disc(nx, ny, 1.0, AnimatedCapes.lerp(color, -1, 0.5));
         } else {
            this.branch(c, nx, ny, angle - 0.45, length * 0.7, depth - 1, color);
            this.branch(c, nx, ny, angle + 0.4, length * 0.72, depth - 1, color);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 110; y++) {
            for (int x = 0; x < 80; x++) {
               double ray = Math.pow(AnimatedCapes.noise((x + y * 0.35) * 0.09 + t * 0.12, 0.5), 4.0);
               c.add(x, y, -3606273, ray * 0.45 * (1.0 - y / 120.0));
            }
         }

         for (int y = 106; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.noise(x * 0.2 + t * 0.4, y * 0.3) + AnimatedCapes.noise(x * 0.2 - t * 0.3 + 4.0, y * 0.3 + t * 0.2);
               c.add(x, y, -1840, Math.pow(1.0 - Math.abs(n - 1.0), 10.0) * 0.35);
            }
         }

         for (int k = 0; k < 5; k++) {
            double baseX = 4 + k * 17 + AnimatedCapes.hash(k, 3) * 5.0;
            double px = baseX;
            double py = 128.0;

            for (int s = 1; s <= 12; s++) {
               double nx = baseX + Math.sin(s * 0.5 - t * 1.4 + k) * s * 0.4;
               double ny = 128.0 - s * (4.0 + AnimatedCapes.hash(k, 4) * 1.5);
               c.line(px, py, nx, ny, 1.3, -13993414);
               if (s % 2 == 0) {
                  c.ellipse(nx + 2.0, ny, 2.2, 0.8, 0.5 + Math.sin(t + s) * 0.2, -12936630);
               }

               px = nx;
               py = ny;
            }
         }

         double turtleX = AnimatedCapes.wrap(t * 4.0, 120.0) - 20.0;
         double turtleY = 36.0 + Math.sin(t * 0.4) * 4.0;
         double paddle = Math.sin(t * 2.5);
         c.ellipse(turtleX - 5.0, turtleY - 3.0 - paddle * 2.0, 4.0, 1.4, -0.5 - paddle * 0.3, -9790870);
         c.ellipse(turtleX - 5.0, turtleY + 3.0 + paddle * 2.0, 4.0, 1.4, 0.5 + paddle * 0.3, -9790870);
         c.ellipse(turtleX, turtleY, 7.0, 5.0, 0.0, -10851782);

         for (int k = 0; k < 6; k++) {
            double a = k * Math.PI / 3.0;
            c.ring(turtleX + Math.cos(a) * 3.0, turtleY + Math.sin(a) * 2.2, 1.3, 0.4, -12957142);
         }

         c.ring(turtleX, turtleY, 1.4, 0.4, -12957142);
         c.ellipse(turtleX + 8.0, turtleY, 2.2, 1.6, 0.0, -8738182);
         double schoolX = 40.0 + Math.sin(t * 0.3) * 22.0;
         double schoolY = 72.0 + Math.cos(t * 0.23) * 10.0;

         for (int i = 0; i < 20; i++) {
            double a = t * 0.9 + i * 0.314;
            double r = 6.0 + AnimatedCapes.hash(i, 30) * 8.0;
            double fx = schoolX + Math.cos(a) * r;
            double fy = schoolY + Math.sin(a) * r * 0.5;
            double dir = -Math.sin(a) >= 0.0 ? 1.0 : -1.0;
            c.ellipse(fx, fy, 1.8, 0.9, 0.0, -10182);
            c.rect(fx - 0.3, fy - 0.9, 0.6, 1.8, -15058294);
            c.polygon(new double[][]{{fx - dir * 1.6, fy}, {fx - dir * 3.0, fy - 1.0}, {fx - dir * 3.0, fy + 1.0}}, -20438);
         }

         for (int i = 0; i < 18; i++) {
            double life = AnimatedCapes.wrap(t * (0.2 + AnimatedCapes.hash(i, 40) * 0.2) + AnimatedCapes.hash(i, 41), 1.0);
            double x = (i % 3 == 0 ? 58 : (i % 3 == 1 ? 20 : 38)) + Math.sin(life * 12.0 + i) * 1.5;
            double y = 104.0 - life * 104.0;
            c.ring(x, y, 0.6 + AnimatedCapes.hash(i, 42), 0.4, AnimatedCapes.alpha(-1507329, 0.7));
         }
      }
   }

   static final class Glitch implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 1.0}, new int[]{-16119254, -11920790, -46470});

         for (int k = 0; k < 7; k++) {
            double y = 92 + k * 5;
            c.rect(0.0, y, 80.0, 1.0 + k * 0.4, AnimatedCapes.alpha(-16119254, 0.8));
         }

         for (int x = 0; x < 80; x += 8) {
            c.line(x, 0.0, x, 128.0, 0.5, AnimatedCapes.alpha(-9811201, 0.12));
         }

         for (int y = 0; y < 128; y += 8) {
            c.line(0.0, y, 80.0, y, 0.5, AnimatedCapes.alpha(-9811201, 0.12));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cx = 40.0;
         double cy = 58.0;
         double blinkPhase = AnimatedCapes.wrap(t, 5.0);
         double lid = blinkPhase < 0.25 ? Math.sin(blinkPhase / 0.25 * Math.PI) : 0.0;
         double lookX = Math.sin(t * 0.7) * 5.0 + (AnimatedCapes.hash((int)(t * 0.8), 3) - 0.5) * 4.0;
         double lookY = Math.cos(t * 0.5) * 2.0;
         double openness = 16.0 * (1.0 - lid);

         for (int y = (int)(cy - 18.0); y <= cy + 18.0; y++) {
            for (int x = 4; x < 76; x++) {
               double u = (x - cx) / 34.0;
               double edge = openness * (1.0 - u * u);
               if (Math.abs(u) < 1.0 && Math.abs(y - cy) < edge) {
                  c.set(x, y, AnimatedCapes.lerp(-986881, -5201712, Math.abs(y - cy) / Math.max(1.0, edge)));
               }
            }
         }

         if (openness > 2.0) {
            double ix = cx + lookX;
            double iy = cy + lookY;
            double dilate = 3.5 + Math.sin(t * 1.3) * 1.2;

            for (int y = (int)(iy - 12.0); y <= iy + 12.0; y++) {
               for (int xx = (int)(ix - 12.0); xx <= ix + 12.0; xx++) {
                  double u = (xx - cx) / 34.0;
                  double d = Math.hypot(xx + 0.5 - ix, y + 0.5 - iy);
                  if (d < 11.0 && Math.abs(y - cy) < openness * (1.0 - u * u)) {
                     double angle = Math.atan2(y - iy, xx - ix);
                     double fiber = 0.5 + 0.5 * Math.sin(angle * 14.0 + AnimatedCapes.noise(angle * 3.0, d * 0.3) * 3.0);
                     int col = d < dilate ? -16382960 : AnimatedCapes.lerp(AnimatedCapes.lerp(-14622465, -7722241, d / 11.0), -1, fiber * 0.25);
                     c.set(xx, y, col);
                  }
               }
            }

            c.disc(ix - 3.0, iy - 3.0, 1.6, AnimatedCapes.alpha(-1, 0.9));
         }

         double u0 = openness;

         for (int xxx = 4; xxx < 76; xxx++) {
            double u = (xxx - cx) / 34.0;
            double edge = u0 * (1.0 - u * u);
            c.blend(xxx, (int)Math.floor(cy - edge - 1.0), -14622465);
            c.blend(xxx, (int)Math.floor(cy + edge), -48992);
         }

         int tick = (int)Math.floor(t * 9.0);
         boolean glitching = AnimatedCapes.hash(tick / 3, 1) > 0.55;
         int[] src = (int[])c.px.clone();
         int split = glitching ? 2 + (int)(AnimatedCapes.hash(tick, 2) * 3.0) : 1;

         for (int y = 0; y < 128; y++) {
            int band = y / (3 + (int)(AnimatedCapes.hash(tick, 4) * 6.0));
            int shift = glitching && AnimatedCapes.hash(band, tick + 5) > 0.75 ? (int)((AnimatedCapes.hash(band, tick + 6) - 0.5) * 30.0) : 0;

            for (int xxx = 0; xxx < 80; xxx++) {
               int base = y * 80;
               int r = src[base + Math.floorMod(xxx + shift + split, 80)] >> 16 & 0xFF;
               int g = src[base + Math.floorMod(xxx + shift, 80)] >> 8 & 0xFF;
               int b = src[base + Math.floorMod(xxx + shift - split, 80)] & 0xFF;
               double scan = (y & 1) == 0 ? 0.82 : 1.0;
               c.px[base + xxx] = 0xFF000000 | (int)(r * scan) << 16 | (int)(g * scan) << 8 | (int)(b * scan);
            }
         }

         if (glitching) {
            for (int i = 0; i < 6; i++) {
               double bx = AnimatedCapes.hash(i, tick + 20) * 80.0;
               double by = AnimatedCapes.hash(i, tick + 21) * 128.0;
               int col = i % 2 == 0 ? -14622465 : -48992;
               c.rect(
                  Math.floor(bx),
                  Math.floor(by),
                  2.0 + AnimatedCapes.hash(i, tick + 22) * 14.0,
                  1.0 + AnimatedCapes.hash(i, tick + 23) * 3.0,
                  AnimatedCapes.alpha(col, 0.7)
               );
            }
         }

         double roll = AnimatedCapes.wrap(t * 30.0, 148.0) - 10.0;

         for (int y = (int)roll; y < roll + 6.0; y++) {
            for (int xxx = 0; xxx < 80; xxx++) {
               c.add(xxx, y, -1, 0.08);
            }
         }

         for (int i = 0; i < 30; i++) {
            if (AnimatedCapes.hash(i, tick + 40) > 0.7) {
               c.set(
                  (int)(AnimatedCapes.hash(i, tick + 41) * 80.0),
                  (int)(AnimatedCapes.hash(i, tick + 42) * 128.0),
                  AnimatedCapes.hash(i, tick + 43) > 0.5 ? -1 : -16777216
               );
            }
         }
      }
   }

   static final class KoiPond implements AnimatedCapes.Scene {
      private static final double[][] PADS = new double[][]{
         {16.0, 22.0, 8.0, 0.4}, {62.0, 40.0, 7.0, 2.2}, {22.0, 92.0, 9.0, 4.0}, {58.0, 106.0, 6.5, 1.1}, {44.0, 64.0, 5.0, 5.3}
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.06, y * 0.05, 4);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-15844800, -16375254, n), x, y));
            }
         }

         for (int i = 0; i < 70; i++) {
            c.ellipse(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               2.0 + AnimatedCapes.hash(i, 3) * 4.0,
               1.5 + AnimatedCapes.hash(i, 4) * 3.0,
               AnimatedCapes.hash(i, 5) * 3.0,
               AnimatedCapes.alpha(AnimatedCapes.lerp(-14001584, -16246756, AnimatedCapes.hash(i, 6)), 0.5)
            );
         }

         for (int x = 0; x < 80; x++) {
            double top = 5.0 + 3.0 * AnimatedCapes.fbm(x * 0.15, 1.0, 3);
            double bottom = 122.0 - 3.0 * AnimatedCapes.fbm(x * 0.15, 8.0, 3);

            for (int y = 0; y < 128; y++) {
               if (y < top || y > bottom) {
                  double n = AnimatedCapes.fbm(x * 0.35, y * 0.35, 3);
                  c.set(x, y, AnimatedCapes.dither(n > 0.55 ? AnimatedCapes.lerp(-12948950, -10843590, n) : AnimatedCapes.lerp(-11908536, -7697792, n), x, y));
               } else if (y < top + 2.0 || y > bottom - 2.0) {
                  c.blend(x, y, AnimatedCapes.alpha(-16777216, 0.4));
               }
            }
         }
      }

      private static double girth(int s) {
         return s == 0 ? 2.1 : 2.8 * (1.0 - Math.max(0, s - 2) / 11.0);
      }

      private static double[] koi(double t, int k) {
         double s = 0.32 + k * 0.05;
         return new double[]{40.0 + 28.0 * Math.sin(t * s + k * 1.9), 64.0 + 46.0 * Math.sin(t * s * 0.63 + k * 2.7)};
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 8; y < 120; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.noise(x * 0.16 + t * 0.35, y * 0.16 - t * 0.2) + AnimatedCapes.noise(x * 0.16 - t * 0.25 + 7.0, y * 0.16 + t * 0.3);
               double caustic = Math.pow(1.0 - Math.abs(n - 1.0), 8.0);
               c.add(x, y, -6623008, caustic * 0.35);
            }
         }

         int[][] patterns = new int[][]{{-724760, -42470}, {-30176, -8032}, {-724760, -15066598}, {-1523648, -2864}};

         for (int k = 0; k < 4; k++) {
            double[][] spine = new double[12][];

            for (int s = 0; s < spine.length; s++) {
               spine[s] = koi(t - s * 0.2, k);
            }

            for (int s = spine.length - 1; s > 0; s--) {
               c.line(spine[s][0] + 2.5, spine[s][1] + 3.0, spine[s - 1][0] + 2.5, spine[s - 1][1] + 3.0, girth(s) * 2.0, AnimatedCapes.alpha(-16777216, 0.18));
            }

            double tailAngle = Math.atan2(spine[10][1] - spine[11][1], spine[10][0] - spine[11][0]) + Math.sin(t * 6.0 + k) * 0.5;
            double tx = spine[11][0];
            double ty = spine[11][1];
            c.polygon(
               new double[][]{
                  {tx, ty},
                  {tx - Math.cos(tailAngle + 0.5) * 5.0, ty - Math.sin(tailAngle + 0.5) * 5.0},
                  {tx - Math.cos(tailAngle - 0.5) * 5.0, ty - Math.sin(tailAngle - 0.5) * 5.0}
               },
               AnimatedCapes.alpha(patterns[k][0], 0.8)
            );
            double fin = Math.atan2(spine[1][1] - spine[2][1], spine[1][0] - spine[2][0]);

            for (int side = -1; side <= 1; side += 2) {
               double a = fin + side * (1.9 + Math.sin(t * 4.0 + k) * 0.3);
               c.ellipse(spine[2][0] + Math.cos(a) * 2.8, spine[2][1] + Math.sin(a) * 2.8, 2.2, 1.0, a, AnimatedCapes.alpha(patterns[k][0], 0.75));
            }

            for (int s = spine.length - 1; s > 0; s--) {
               int col = AnimatedCapes.hash(s / 2, k + 60) > 0.5 ? patterns[k][1] : patterns[k][0];
               c.line(spine[s][0], spine[s][1], spine[s - 1][0], spine[s - 1][1], girth(s) * 2.0, col);
            }

            c.disc(spine[0][0], spine[0][1], 2.2, patterns[k][0]);

            for (int s = spine.length - 2; s > 0; s--) {
               c.line(spine[s][0] - 0.5, spine[s][1] - 0.5, spine[s - 1][0] - 0.5, spine[s - 1][1] - 0.5, girth(s) * 0.5, AnimatedCapes.alpha(-1, 0.22));
            }
         }

         for (int i = 0; i < 4; i++) {
            double cycle = t * 0.45 + AnimatedCapes.hash(i, 70) * 5.0;
            double life = AnimatedCapes.wrap(cycle, 1.0);
            int drop = (int)Math.floor(cycle);
            double x = 10.0 + AnimatedCapes.hash(i, drop + 71) * 60.0;
            double y = 14.0 + AnimatedCapes.hash(i, drop + 72) * 100.0;
            c.ring(x, y, life * 9.0, 0.7, AnimatedCapes.alpha(-2555912, 0.45 * (1.0 - life)));
            c.ring(x, y, life * 5.0, 0.5, AnimatedCapes.alpha(-2555912, 0.3 * (1.0 - life)));
         }

         for (int p = 0; p < PADS.length; p++) {
            double[] pad = PADS[p];
            double px = pad[0] + Math.sin(t * 0.2 + p) * 1.2;
            double py = pad[1] + Math.cos(t * 0.17 + p) * 1.0;
            double notch = pad[3] + Math.sin(t * 0.1 + p) * 0.2;
            c.disc(px + 1.5, py + 2.0, pad[2], AnimatedCapes.alpha(-16777216, 0.25));
            double[][] outline = new double[26][];

            for (int s = 0; s < 25; s++) {
               double a = notch + 0.35 + s * 5.583185307179586 / 24.0;
               outline[s] = new double[]{px + Math.cos(a) * pad[2], py + Math.sin(a) * pad[2]};
            }

            outline[25] = new double[]{px, py};
            c.polygon(
               outline, (x, y) -> AnimatedCapes.lerp(-11886534, -14783958, Math.hypot(x - px, y - py) / pad[2] * 0.7 + (x - px + pad[2]) / (pad[2] * 4.0))
            );

            for (int v = 0; v < 6; v++) {
               double a = notch + 0.6 + v * 0.95;
               c.line(px, py, px + Math.cos(a) * pad[2] * 0.85, py + Math.sin(a) * pad[2] * 0.85, 0.4, AnimatedCapes.alpha(-7681430, 0.5));
            }

            if (p == 2) {
               for (int f = 0; f < 8; f++) {
                  double a = f * Math.PI / 4.0 + t * 0.1;
                  c.ellipse(px + Math.cos(a) * 2.6, py + Math.sin(a) * 2.6, 2.6, 1.2, a, -745280);
               }

               for (int f = 0; f < 5; f++) {
                  double a = f * Math.PI * 0.4 + 0.3;
                  c.ellipse(px + Math.cos(a) * 1.3, py + Math.sin(a) * 1.3, 1.6, 0.8, a, -10012);
               }

               c.disc(px, py, 1.1, -12224);
            }
         }
      }
   }

   static final class LiquidChrome implements AnimatedCapes.Scene {
      private static final double[] STOPS = new double[]{0.0, 0.18, 0.32, 0.42, 0.5, 0.62, 0.78, 1.0};
      private static final int[] METAL = new int[]{-16118768, -12958638, -5589308, -1, -9799544, -15066078, -3630480, -14013904};

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double fx = x * 0.028;
               double fy = y * 0.024;
               double qx = AnimatedCapes.fbm(fx + t * 0.07, fy, 3);
               double qy = AnimatedCapes.fbm(fx + 5.2, fy + 1.3 - t * 0.05, 3);
               double v = AnimatedCapes.fbm(fx + 3.5 * qx + 1.7, fy + 3.5 * qy + 9.2, 4);
               double band = AnimatedCapes.wrap(v * 3.2 + t * 0.05, 1.0);
               int col = AnimatedCapes.ramp(band, STOPS, METAL);
               col = AnimatedCapes.lerp(col, AnimatedCapes.hsv(v * 540.0 + t * 25.0, 0.5, 1.0), 0.12);
               c.set(x, y, AnimatedCapes.dither(col, x, y));
            }
         }

         for (int i = 0; i < 30; i++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) * 80.0 + t * (3.0 + AnimatedCapes.hash(i, 2) * 4.0), 80.0);
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 3) * 128.0 + Math.sin(t * 0.6 + i) * 6.0, 128.0);
            int under = c.get((int)x, (int)y);
            if ((under >> 16 & 0xFF) > 180) {
               c.star(x, y, 2.0, -1, 0.8 * (0.5 + 0.5 * Math.sin(t * 4.0 + i)));
            }
         }

         c.vignette(0.35);
      }
   }

   static final class MeteorDesert implements AnimatedCapes.Scene {
      private boolean[] open;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.55, 0.75}, new int[]{-16513510, -15066560, -12965296});

         for (int y = 0; y < 90; y++) {
            for (int x = 0; x < 80; x++) {
               double along = (x - y * 0.6 + 20.0) / 30.0;
               double band = Math.exp(-along * along * 3.0) * AnimatedCapes.fbm(x * 0.08, y * 0.06, 4);
               c.add(x, y, -5201696, band * 0.45);
               c.blend(x, y, AnimatedCapes.alpha(-16513510, AnimatedCapes.smoothstep(0.55, 0.8, AnimatedCapes.fbm(x * 0.15 + 9.0, y * 0.1, 3)) * band));
            }
         }

         for (int i = 0; i < 200; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 80.0,
               AnimatedCapes.hash(i, 4) > 0.97 ? 1.0 : 0.0,
               -988929,
               0.1 + AnimatedCapes.hash(i, 3) * 0.5
            );
         }

         c.glow(64.0, 16.0, 16.0, -8355664, 0.3);
         c.disc(64.0, 16.0, 5.5, -462628);
         c.disc(66.5, 14.5, 5.0, -16118750);
         int[] before = (int[])c.px.clone();
         c.polygon(
            new double[][]{{-2.0, 84.0}, {6.0, 70.0}, {22.0, 70.0}, {26.0, 80.0}, {34.0, 84.0}},
            (xx, yx) -> AnimatedCapes.lerp(-12965312, -14016976, (yx - 70) / 14.0)
         );

         for (int layer = 0; layer < 3; layer++) {
            int l = layer;
            double baseY = 86 + layer * 12;
            int lit = AnimatedCapes.lerp(-9807238, -6255990, layer / 2.0);
            int dark = AnimatedCapes.lerp(-14015944, -12965320, layer / 2.0);

            for (int x = 0; x < 80; x++) {
               double top = baseY - 6.0 * Math.sin(x * (0.06 + l * 0.02) + l * 2) - 3.0 * Math.sin(x * 0.13 + l);
               double slope = Math.cos(x * (0.06 + l * 0.02) + l * 2);

               for (int y = Math.max(0, (int)top); y < 128; y++) {
                  double depth = y - top;
                  int col = slope > 0.0 ? AnimatedCapes.lerp(lit, dark, AnimatedCapes.clamp(depth / 12.0, 0.0, 1.0)) : dark;
                  if (depth < 1.0) {
                     col = AnimatedCapes.lerp(c.get(x, y), AnimatedCapes.lerp(col, -3096368, 0.4), 1.0 - (top - Math.floor(top)));
                  }

                  c.set(x, y, AnimatedCapes.dither(col, x, y));
               }
            }
         }

         this.saguaro(c, 14.0, 108.0, 22.0);
         this.saguaro(c, 66.0, 100.0, 14.0);
         this.open = AnimatedCapes.unchanged(c, before);
      }

      private void saguaro(AnimatedCapes.Canvas c, double x, double baseY, double height) {
         int col = -15988206;
         c.line(x, baseY, x, baseY - height, height * 0.14, col);
         c.disc(x, baseY - height, height * 0.07, col);
         c.line(x, baseY - height * 0.45, x - height * 0.25, baseY - height * 0.45, height * 0.1, col);
         c.line(x - height * 0.25, baseY - height * 0.45, x - height * 0.25, baseY - height * 0.75, height * 0.1, col);
         c.line(x, baseY - height * 0.6, x + height * 0.22, baseY - height * 0.6, height * 0.1, col);
         c.line(x + height * 0.22, baseY - height * 0.6, x + height * 0.22, baseY - height * 0.85, height * 0.1, col);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         AnimatedCapes.stars(c, t, 40, 0.0, 80.0, -1, 13, this.open);

         for (int i = 0; i < 7; i++) {
            double period = 1.6 + AnimatedCapes.hash(i, 50) * 1.8;
            double cycle = t / period + AnimatedCapes.hash(i, 51);
            double life = AnimatedCapes.wrap(cycle, 1.0);
            int n = (int)Math.floor(cycle);
            if (!(life > 0.55)) {
               double p = life / 0.55;
               double sx = 20.0 + AnimatedCapes.hash(i, n + 52) * 70.0;
               double sy = -4.0 + AnimatedCapes.hash(i, n + 53) * 30.0;
               double dist = 30.0 + AnimatedCapes.hash(i, n + 54) * 30.0;
               double hx = sx - dist * 0.8 * p;
               double hy = sy + dist * 0.6 * p;
               double fade = Math.sin(p * Math.PI);
               c.beam(hx, hy, hx + 9.0 * (0.4 + p), hy - 6.75 * (0.4 + p), 0.5, -6619168, fade * 0.7);
               c.star(hx, hy, 1.0, -1, fade);
            }
         }

         double big = AnimatedCapes.wrap(t, 12.0);
         if (big < 2.5) {
            double p = big / 2.5;
            double hx = 86.0 - p * 70.0;
            double hy = 4.0 + p * 52.0;
            c.beam(hx, hy, hx + 22.0, hy - 16.0, 1.3, -20400, 0.8 * (1.0 - p * 0.5));
            c.glow(hx, hy, 8.0, -8032, 0.9);

            for (int s = 0; s < 10; s++) {
               double back = AnimatedCapes.hash(s, (int)(t * 10.0)) * 20.0;
               c.add((int)(hx + back + (AnimatedCapes.hash(s, 7) - 0.5) * 3.0), (int)(hy - back * 0.73 + (AnimatedCapes.hash(s, 8) - 0.5) * 3.0), -16288, 0.7);
            }
         }

         double fireX = 40.0;
         double fireY = 118.0;
         double flicker = AnimatedCapes.noise(t * 6.0, 3.0);
         c.glow(fireX, fireY, 22.0, -32720, 0.35 + 0.15 * flicker);
         c.line(fireX - 4.0, fireY + 1.0, fireX + 4.0, fireY - 1.0, 1.2, -12967914);
         c.line(fireX - 4.0, fireY - 1.0, fireX + 4.0, fireY + 1.0, 1.2, -14019058);

         for (int k = 0; k < 5; k++) {
            double h = 5.0 + AnimatedCapes.noise(t * 5.0 + k, k) * 5.0;
            double sway = Math.sin(t * 7.0 + k * 2) * 1.2;
            c.polygon(
               new double[][]{{fireX - 2.5 + k * 1.2, fireY}, {fireX - 0.8 + k * 1.2, fireY}, {fireX - 1.6 + k * 1.2 + sway, fireY - h}},
               k % 2 == 0 ? -34272 : -16320
            );
         }

         c.polygon(new double[][]{{fireX - 1.2, fireY}, {fireX + 1.2, fireY}, {fireX + Math.sin(t * 9.0), fireY - 4.0 - flicker * 2.0}}, -3920);

         for (int k = 0; k < 8; k++) {
            double life = AnimatedCapes.wrap(t * 0.35 + k / 8.0, 1.0);
            c.disc(
               fireX + Math.sin(life * 5.0 + t) * 2.0 + life * 6.0,
               fireY - 8.0 - life * 34.0,
               0.8 + life * 3.0,
               AnimatedCapes.alpha(-10858400, 0.3 * (1.0 - life))
            );
         }

         for (int k = 0; k < 6; k++) {
            double life = AnimatedCapes.wrap(t * 0.9 + AnimatedCapes.hash(k, 80), 1.0);
            c.add(
               (int)(fireX + (AnimatedCapes.hash(k, (int)(t * 0.9 + AnimatedCapes.hash(k, 80)) + 81) - 0.5) * 10.0),
               (int)(fireY - 6.0 - life * 20.0),
               -24512,
               1.0 - life
            );
         }
      }
   }

   static final class NeonHearts implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            int row = y / 6;

            for (int x = 0; x < 80; x++) {
               int offset = row % 2 == 0 ? 0 : 6;
               int bx = (x + offset) / 12;
               boolean mortar = y % 6 == 5 || (x + offset) % 12 == 11;
               int brick = AnimatedCapes.lerp(-11919846, -9818588, AnimatedCapes.hash(bx, row));
               brick = AnimatedCapes.shade(brick, 0.8 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.4);
               int col = mortar ? -14805484 : brick;
               col = AnimatedCapes.shade(col, 1.0 - AnimatedCapes.smoothstep(0.5, 0.8, AnimatedCapes.fbm(x * 0.05, y * 0.04 + 3.0, 3)) * 0.5);
               c.set(x, y, AnimatedCapes.dither(col, x, y));
            }
         }

         c.vignette(0.5);
         c.rect(0.0, 116.0, 80.0, 12.0, -15857140);
         c.rect(0.0, 116.0, 80.0, 1.0, -14016476);
         c.rect(72.0, 0.0, 3.0, 116.0, -14013906);
         c.rect(72.0, 0.0, 1.0, 116.0, -11908528);

         for (int y = 10; y < 116; y += 22) {
            c.rect(71.0, y, 5.0, 2.0, -15066594);
         }

         c.rect(20.0, 18.0, 2.0, 5.0, -12961216);
         c.rect(58.0, 18.0, 2.0, 5.0, -12961216);
         c.line(21.0, 18.0, 21.0, 0.0, 0.5, -14013904);
         c.line(59.0, 18.0, 59.0, 0.0, 0.5, -14013904);
      }

      private static double[] heart(double a, double cx, double cy, double scale) {
         double s = Math.sin(a);
         return new double[]{
            cx + 16.0 * s * s * s * scale, cy - (13.0 * Math.cos(a) - 5.0 * Math.cos(2.0 * a) - 2.0 * Math.cos(3.0 * a) - Math.cos(4.0 * a)) * scale
         };
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         boolean buzz = AnimatedCapes.hash((int)Math.floor(t * 12.0), 5) > 0.93;
         double outer = buzz ? 0.25 : 1.0;
         double beat = Math.pow(Math.max(0.0, Math.sin(t * 2.6)), 10.0) + 0.6 * Math.pow(Math.max(0.0, Math.sin(t * 2.6 - 0.5)), 12.0);
         c.glow(40.0, 50.0, 42.0, -50550, 0.28 * outer);
         int segments = 90;

         for (int i = 0; i < segments; i++) {
            double[] a = heart(i * Math.PI * 2.0 / segments, 40.0, 50.0, 1.9);
            double[] b = heart((i + 1) * Math.PI * 2.0 / segments, 40.0, 50.0, 1.9);
            boolean broken = i > 66 && i < 72 && AnimatedCapes.hash((int)Math.floor(t * 20.0), 9) > 0.5;
            c.beam(a[0], a[1], b[0], b[1], 1.4, -46432, broken ? 0.1 : outer);
         }

         double inner = 1.05 + beat * 0.12;
         c.glow(40.0, 48.0, 22.0, -12525313, 0.2 + beat * 0.35);

         for (int i = 0; i < 60; i++) {
            double[] a = heart(i * Math.PI * 2.0 / 60.0, 40.0, 48.0, inner);
            double[] b = heart((i + 1) * Math.PI * 2.0 / 60.0, 40.0, 48.0, inner);
            c.beam(a[0], a[1], b[0], b[1], 1.0, -11474689, 0.6 + beat * 0.4);
         }

         double[][][] letters = new double[][][]{
            {{17.0, 86.0, 17.0, 98.0}, {17.0, 98.0, 24.0, 98.0}},
            {{28.0, 86.0, 34.0, 86.0}, {34.0, 86.0, 34.0, 98.0}, {34.0, 98.0, 28.0, 98.0}, {28.0, 98.0, 28.0, 86.0}},
            {{38.0, 86.0, 41.5, 98.0}, {41.5, 98.0, 45.0, 86.0}},
            {{49.0, 86.0, 49.0, 98.0}, {49.0, 86.0, 55.0, 86.0}, {49.0, 92.0, 54.0, 92.0}, {49.0, 98.0, 55.0, 98.0}}
         };
         boolean flickerV = AnimatedCapes.hash((int)Math.floor(t * 8.0), 17) > 0.85;

         for (int l = 0; l < letters.length; l++) {
            double strength = l == 2 && flickerV ? 0.2 : 0.9;

            for (double[] stroke : letters[l]) {
               c.beam(stroke[0], stroke[1], stroke[2], stroke[3], 0.9, -12224, strength);
            }
         }

         for (int y = 116; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.add(x, y, AnimatedCapes.lerp(-50550, -12525313, 0.3 + 0.3 * Math.sin(x * 0.2 + t)), 0.18 * (1.0 - (y - 116) / 12.0) * outer);
            }
         }

         for (int i = 0; i < 16; i++) {
            double life = AnimatedCapes.wrap(t * 1.3 + AnimatedCapes.hash(i, 30), 1.0);
            double burst = Math.floor(t * 1.3 + AnimatedCapes.hash(i, 30));
            double vx = (AnimatedCapes.hash(i, (int)burst + 31) - 0.5) * 22.0;
            double x = 60.0 + vx * life;
            double y = 76.0 + life * 6.0 + life * life * 34.0;
            c.add((int)x, (int)y, -10096, 1.0 - life);
            c.add((int)(x - vx * 0.03), (int)(y - 1.0), -26048, (1.0 - life) * 0.5);
         }

         for (int i = 0; i < 8; i++) {
            double life = AnimatedCapes.wrap(t * 0.2 + i / 8.0, 1.0);
            double x = 10.0 + AnimatedCapes.hash(i, 50) * 60.0 + Math.sin(life * 8.0 + i) * 3.0;
            double y = 112.0 - life * 110.0;
            double size = 1.2 + AnimatedCapes.hash(i, 51);
            int col = AnimatedCapes.alpha(-38224, 0.6 * Math.sin(life * Math.PI));
            c.disc(x - size * 0.5, y, size * 0.6, col);
            c.disc(x + size * 0.5, y, size * 0.6, col);
            c.polygon(new double[][]{{x - size, y + 0.2}, {x + size, y + 0.2}, {x, y + size * 1.4}}, col);
         }
      }
   }

   static final class Phoenix implements AnimatedCapes.Scene {
      private static final double[] STOPS = new double[]{0.0, 0.35, 0.7, 1.0};
      private static final int[] FIRE = new int[]{-2368, -16320, -42480, -6682096};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16252406, -14023152, -11923958});
         c.glow(40.0, 52.0, 60.0, -49136, 0.35);
         AnimatedCapes.mountains(c, 14, 122.0, 20.0, 0.05, -15596022, 0);

         for (int i = 0; i < 30; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 60.0, 0.0, -16224, 0.1 + AnimatedCapes.hash(i, 3) * 0.2);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double flap = Math.sin(t * 2.2);
         double bodyX = 40.0;
         double bodyY = 46.0 - flap * 2.0;

         for (int y = 10; y < 100; y++) {
            for (int x = 0; x < 80; x++) {
               double d = Math.hypot(x - bodyX, (y - bodyY) * 0.8) / 34.0;
               if (d < 1.0) {
                  double f = AnimatedCapes.fbm(x * 0.1, y * 0.09 + t * 1.8, 3);
                  c.add(x, y, -38368, AnimatedCapes.smoothstep(0.45, 0.8, f) * (1.0 - d) * 0.55);
               }
            }
         }

         for (int k = 0; k < 5; k++) {
            double px = bodyX;
            double py = bodyY + 8.0;

            for (int s = 1; s <= 14; s++) {
               double f = s / 14.0;
               double nx = bodyX + (k - 2) * f * 6.0 + Math.sin(f * 5.0 - t * 3.0 + k) * f * 7.0;
               double ny = bodyY + 8.0 + f * 70.0;
               c.beam(px, py, nx, ny, 2.2 * (1.0 - f * 0.8), AnimatedCapes.ramp(f, STOPS, FIRE), 0.8 * (1.0 - f * 0.6));
               px = nx;
               py = ny;
            }

            c.glow(px, py, 4.0, -49136, 0.4);
         }

         for (int side = -1; side <= 1; side += 2) {
            double shoulderX = bodyX + side * 3;
            double shoulderY = bodyY - 1.0;
            double elbowX = bodyX + side * 16;
            double elbowY = bodyY - 10.0 - flap * 12.0;
            double tipX = bodyX + side * 34;
            double tipY = bodyY - 16.0 - flap * 22.0;

            for (int f = 0; f < 14; f++) {
               double u = f / 13.0;
               double rx = u < 0.5 ? shoulderX + (elbowX - shoulderX) * u * 2.0 : elbowX + (tipX - elbowX) * (u - 0.5) * 2.0;
               double ry = u < 0.5 ? shoulderY + (elbowY - shoulderY) * u * 2.0 : elbowY + (tipY - elbowY) * (u - 0.5) * 2.0;
               double length = 8.0 + u * 10.0 + Math.sin(t * 9.0 + f) * 0.8;
               double angle = (Math.PI / 2) + side * (0.25 + u * 0.9) + flap * side * 0.15;
               double fx = rx + Math.cos(angle) * length;
               double fy = ry + Math.sin(angle) * length;
               c.beam(rx, ry, fx, fy, 1.6 - u * 0.6, AnimatedCapes.ramp(0.2 + u * 0.7, STOPS, FIRE), 0.85);
            }

            c.beam(shoulderX, shoulderY, elbowX, elbowY, 1.8, -6000, 0.9);
            c.beam(elbowX, elbowY, tipX, tipY, 1.2, -16304, 0.9);
         }

         c.ellipse(bodyX, bodyY + 3.0, 3.4, 7.0, 0.0, -26064);
         c.ellipse(bodyX, bodyY + 2.0, 2.0, 5.0, 0.0, -5984);
         c.disc(bodyX, bodyY - 6.0, 2.6, -20416);
         c.polygon(new double[][]{{bodyX - 1.0, bodyY - 5.0}, {bodyX + 1.0, bodyY - 5.0}, {bodyX, bodyY - 2.0}}, -3904);
         c.rect(bodyX - 1.5, bodyY - 7.0, 1.0, 1.0, -1);
         c.rect(bodyX + 0.5, bodyY - 7.0, 1.0, 1.0, -1);

         for (int k = 0; k < 3; k++) {
            double sway = Math.sin(t * 5.0 + k) * 1.2;
            c.beam(bodyX + (k - 1) * 1.2, bodyY - 8.0, bodyX + (k - 1) * 3 + sway, bodyY - 15.0 - k % 2 * 2, 0.8, -12192, 0.8);
         }

         c.glow(bodyX, bodyY, 16.0, -16288, 0.5);

         for (int i = 0; i < 50; i++) {
            double life = AnimatedCapes.wrap(t * 0.5 + AnimatedCapes.hash(i, 20), 1.0);
            int wave = (int)Math.floor(t * 0.5 + AnimatedCapes.hash(i, 20));
            double xx = bodyX + (AnimatedCapes.hash(i, wave + 21) - 0.5) * 70.0;
            double y = bodyY - 10.0 + AnimatedCapes.hash(i, wave + 22) * 30.0 + life * 60.0;
            c.add((int)(xx + Math.sin(life * 6.0 + i) * 3.0), (int)y, AnimatedCapes.ramp(life, STOPS, FIRE), 0.9 * (1.0 - life));
         }
      }
   }

   static final class Synthwave implements AnimatedCapes.Scene {
      private static final int HORIZON = 68;
      private static final double[] SKY_AT = new double[]{0.0, 0.35, 0.53};
      private static final int[] SKY = new int[]{-16120284, -12971936, -2084742};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(SKY_AT, SKY);

         for (int i = 0; i < 70; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 44.0,
               AnimatedCapes.hash(i, 5) > 0.95 ? 1.0 : 0.0,
               -7937,
               0.2 + AnimatedCapes.hash(i, 3) * 0.4
            );
         }

         for (int y = 68; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-14022080, -16120808, (y - 68) / 60.0), x, y));
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double sunY = 52.0;
         double r = 19.0;
         c.glow(40.0, sunY, 44.0, -53120, 0.45);

         for (int y = (int)(sunY - r); y < 68; y++) {
            double rel = (y - (sunY - r)) / (2.0 * r);
            double band = AnimatedCapes.wrap(y - t * 3.0, 7.0);
            double gap = rel > 0.45 ? (rel - 0.45) * 7.0 : 0.0;
            if (!(band < gap)) {
               int col = AnimatedCapes.ramp(rel, new double[]{0.0, 0.5, 1.0}, new int[]{-3968, -26048, -54646});
               double half = Math.sqrt(Math.max(0.0, r * r - (y + 0.5 - sunY) * (y + 0.5 - sunY)));

               for (int x = (int)Math.floor(40.0 - half); x <= (int)Math.ceil(40.0 + half); x++) {
                  double cover = AnimatedCapes.clamp(half + 0.5 - Math.abs(x + 0.5 - 40.0), 0.0, 1.0);
                  c.blend(x, y, AnimatedCapes.alpha(col, cover));
               }
            }
         }

         double[][] left = new double[][]{{-2.0, 69.0}, {-2.0, 50.0}, {6.0, 44.0}, {12.0, 52.0}, {20.0, 40.0}, {30.0, 56.0}, {36.0, 62.0}, {40.0, 69.0}};
         double[][] right = new double[][]{{38.0, 69.0}, {46.0, 60.0}, {54.0, 46.0}, {62.0, 54.0}, {70.0, 38.0}, {82.0, 50.0}, {82.0, 69.0}};

         for (double[][] range : new double[][][]{left, right}) {
            c.polygon(range, (xx, yx) -> AnimatedCapes.lerp(-15071696, -12973488, (69 - yx) / 30.0));

            for (int i = 1; i < range.length - 2; i++) {
               c.beam(range[i][0], range[i][1], range[i + 1][0], range[i + 1][1], 0.6, -48960, 0.8);
               c.line(range[i][0], range[i][1], range[i][0] + (40.0 - range[i][0]) * 0.2, 69.0, 0.5, AnimatedCapes.alpha(-48960, 0.35));
            }
         }

         c.beam(0.0, 68.0, 80.0, 68.0, 0.8, -40752, 1.0);

         for (int i = -12; i <= 12; i++) {
            c.beam(40.0 + i * 1.6, 68.0, 40 + i * 16, 132.0, 0.5, -4177665, 0.55);
         }

         for (int k = 0; k < 12; k++) {
            double z = AnimatedCapes.wrap(k - t * 1.6, 12.0) + 0.6;
            double yx = 68.0 + 56.0 / z;
            if (yx < 130.0) {
               c.beam(0.0, yx, 80.0, yx, 0.4 + 0.5 / z, -48928, AnimatedCapes.clamp(0.25 + 1.2 / z, 0.0, 1.0));
            }
         }

         for (int yx = 69; yx < 128; yx++) {
            for (int x = 0; x < 80; x++) {
               if ((yx & 1) == 0) {
                  c.blend(x, yx, AnimatedCapes.alpha(-16777216, 0.12));
               }
            }
         }

         for (int side = 0; side < 2; side++) {
            double px = side == 0 ? 10.0 : 70.0;
            double baseX = side == 0 ? 3.0 : 77.0;
            double crownY = 60.0;
            double sway = Math.sin(t * 0.9 + side) * 0.06;
            double lastX = baseX;
            double lastY = 128.0;

            for (int s = 1; s <= 6; s++) {
               double f = s / 6.0;
               double tx = baseX + (px - baseX) * f + Math.sin(f * Math.PI) * (side == 0 ? 2 : -2);
               double ty = 128.0 - (128.0 - crownY) * f;
               c.line(lastX, lastY, tx, ty, 2.6 - f, -16383476);
               lastX = tx;
               lastY = ty;
            }

            for (int kx = 0; kx < 9; kx++) {
               double angle = -Math.PI + kx * (Math.PI / 8) + sway;
               double fx = px;
               double fy = crownY;

               for (int s = 1; s <= 6; s++) {
                  double f = s / 6.0;
                  double nx = px + Math.cos(angle) * 16.0 * f;
                  double ny = crownY + Math.sin(angle) * 16.0 * f * 0.55 + f * f * (4.0 + Math.abs(Math.cos(angle)) * 5.0);
                  c.line(fx, fy, nx, ny, 1.1 * (1.0 - f * 0.5), -16383476);
                  c.line(nx, ny, nx + Math.cos(angle + 1.2) * 2.4 * (1.0 - f * 0.6), ny + 2.4 * (1.0 - f * 0.6), 0.5, -16383476);
                  fx = nx;
                  fy = ny;
               }
            }
         }
      }
   }

   static final class TheEnd implements AnimatedCapes.Scene {
      private static final double[][] PILLARS = new double[][]{{8.0, 5.0, 52.0}, {26.0, 4.0, 38.0}, {52.0, 6.0, 64.0}, {70.0, 4.0, 34.0}};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-16382964, -15463906});

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.add(x, y, -11916694, AnimatedCapes.smoothstep(0.6, 0.9, AnimatedCapes.fbm(x * 0.04, y * 0.03 + 9.0, 4)) * 0.3);
            }
         }

         for (int i = 0; i < 70; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 128.0, 0.0, -2570000, 0.08 + AnimatedCapes.hash(i, 3) * 0.25);
         }

         c.ellipse(66.0, 30.0, 7.0, 2.0, 0.0, -9803192);
         c.polygon(new double[][]{{59.0, 30.0}, {73.0, 30.0}, {67.0, 38.0}}, -14014416);
         c.ellipse(12.0, 72.0, 5.0, 1.4, 0.0, -10855872);
         c.polygon(new double[][]{{7.0, 72.0}, {17.0, 72.0}, {12.0, 78.0}}, -14540758);
         c.polygon(
            new double[][]{
               {-4.0, 100.0},
               {10.0, 94.0},
               {40.0, 92.0},
               {72.0, 94.0},
               {84.0, 100.0},
               {70.0, 110.0},
               {54.0, 120.0},
               {42.0, 132.0},
               {30.0, 118.0},
               {12.0, 110.0}
            },
            (xx, y) -> {
               double n = AnimatedCapes.fbm(xx * 0.3, y * 0.3, 3);
               return y < 98
                  ? AnimatedCapes.lerp(-1514324, -5199240, n)
                  : AnimatedCapes.lerp(-9804728, -14014432, AnimatedCapes.clamp((y - 98) / 26.0, 0.0, 1.0) + n * 0.2);
            }
         );

         for (int i = 0; i < 40; i++) {
            c.rect(
               Math.floor(AnimatedCapes.hash(i, 20) * 70.0 + 5.0),
               Math.floor(93.0 + AnimatedCapes.hash(i, 21) * 5.0),
               1.0,
               1.0,
               AnimatedCapes.alpha(-7699368, 0.6)
            );
         }

         for (double[] pillar : PILLARS) {
            double x = pillar[0];
            double w = pillar[1];
            double top = 96.0 - pillar[2];
            c.polygon(new double[][]{{x, top}, {x + w, top}, {x + w, 97.0}, {x, 97.0}}, (px, py) -> {
               int col = AnimatedCapes.lerp(-14806992, -16120302, (px - x) / w);
               return AnimatedCapes.hash(px, py) > 0.9 ? AnimatedCapes.lerp(col, -11916694, 0.5) : col;
            });
            c.rect(x, top, 1.0, 97.0 - top, AnimatedCapes.alpha(-12965286, 0.7));
         }

         double[][] endermen = new double[][]{{36.0, 92.0}, {62.0, 93.0}};

         for (double[] e : endermen) {
            c.rect(e[0] - 1.5, e[1] - 16.0, 3.0, 3.0, -16382968);
            c.rect(e[0] - 1.0, e[1] - 13.0, 2.0, 6.0, -16382968);
            c.line(e[0] - 1.0, e[1] - 12.0, e[0] - 1.6, e[1] - 4.0, 0.6, -16382968);
            c.line(e[0] + 1.0, e[1] - 12.0, e[0] + 1.6, e[1] - 4.0, 0.6, -16382968);
            c.line(e[0] - 0.5, e[1] - 7.0, e[0] - 0.7, e[1], 0.7, -16382968);
            c.line(e[0] + 0.5, e[1] - 7.0, e[0] + 0.7, e[1], 0.7, -16382968);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double dragonX = 40.0 + Math.sin(t * 0.35) * 26.0;
         double dragonY = 38.0 + Math.sin(t * 0.7) * 9.0;

         for (int k = 0; k < PILLARS.length; k++) {
            double[] pillar = PILLARS[k];
            double cx = pillar[0] + pillar[1] / 2.0;
            double cy = 96.0 - pillar[2] - 5.0 + Math.sin(t * 2.0 + k) * 1.2;
            double spin = t * 2.2 + k;
            double half = 2.6 * Math.abs(Math.cos(spin)) + 0.8;
            if (Math.hypot(cx - dragonX, cy - dragonY) < 50.0) {
               c.beam(cx, cy, dragonX, dragonY, 0.6, -25888, 0.35 + 0.15 * Math.sin(t * 9.0 + k));
            }

            c.glow(cx, cy, 10.0, -40768, 0.45);
            c.polygon(new double[][]{{cx, cy - 3.5}, {cx + half, cy}, {cx, cy + 3.5}, {cx - half, cy}}, -25894);
            c.polygon(new double[][]{{cx, cy - 2.0}, {cx + half * 0.5, cy}, {cx, cy + 2.0}, {cx - half * 0.5, cy}}, -5896);
            c.rect(cx - 2.0, cy + 4.5, 4.0, 1.5, -12961216);
         }

         double[][] trail = new double[10][];

         for (int s = 0; s < trail.length; s++) {
            double ts = t - s * 0.09;
            trail[s] = new double[]{40.0 + Math.sin(ts * 0.35) * 26.0, 38.0 + Math.sin(ts * 0.7) * 9.0};
         }

         c.glow(dragonX, dragonY, 28.0, -7714104, 0.65);

         for (int s = trail.length - 1; s > 0; s--) {
            c.line(trail[s][0], trail[s][1], trail[s - 1][0], trail[s - 1][1], 3.8 - s * 0.32, -15726058);
         }

         for (int s = trail.length - 1; s > 0; s--) {
            c.line(trail[s][0], trail[s][1] - 1.2, trail[s - 1][0], trail[s - 1][1] - 1.2, 0.6, AnimatedCapes.alpha(-6653240, 0.55));
         }

         double heading = Math.signum(trail[0][0] - trail[2][0] + 1.0E-4);
         double flap = Math.sin(t * 5.0);

         for (int side = -1; side <= 1; side += 2) {
            double tipX = dragonX - heading * 3.0 + side * 17;
            double tipY = dragonY - 9.0 * flap;
            double[][] wing = new double[][]{
               {dragonX - heading * 2.0, dragonY - 0.5},
               {tipX, tipY},
               {tipX - side * 4, tipY + 5.0},
               {dragonX + side * 7 - heading * 4.0, dragonY + 3.5},
               {dragonX - heading * 6.0, dragonY + 1.5}
            };
            c.polygon(wing, -15069146);
            c.line(wing[0][0], wing[0][1], tipX, tipY, 0.8, -8758630);
            c.line((wing[0][0] + tipX) / 2.0, (wing[0][1] + tipY) / 2.0, wing[2][0], wing[2][1], 0.5, -8758630);
            c.line((wing[0][0] + tipX) / 2.0, (wing[0][1] + tipY) / 2.0, wing[3][0], wing[3][1], 0.5, -8758630);
         }

         c.ellipse(dragonX + heading * 5.0, dragonY - 0.5, 2.8, 1.7, 0.0, -15726058);
         c.line(dragonX + heading * 4.0, dragonY - 2.0, dragonX + heading * 2.5, dragonY - 4.0, 0.6, -15726058);
         c.rect(dragonX + heading * 6.0 - 0.5, dragonY - 1.5, 1.0, 1.0, -2068225);
         c.glow(dragonX + heading * 6.0, dragonY - 1.0, 3.0, -5226241, 0.6);
         double[][] endermen = new double[][]{{36.0, 92.0}, {62.0, 93.0}};

         for (int k = 0; k < endermen.length; k++) {
            double open = Math.sin(t * 0.9 + k * 2) > -0.8 ? 1.0 : 0.0;
            c.rect(endermen[k][0] - 1.5, endermen[k][1] - 14.5, 1.0, 0.8 * open, -2064129);
            c.rect(endermen[k][0] + 0.5, endermen[k][1] - 14.5, 1.0, 0.8 * open, -2064129);
         }

         for (int i = 0; i < 40; i++) {
            double life = AnimatedCapes.wrap(t * 0.4 + AnimatedCapes.hash(i, 40), 1.0);
            double x = AnimatedCapes.hash(i, 41) * 80.0 + Math.sin(t + i) * 2.0;
            double y = 60.0 + AnimatedCapes.hash(i, 42) * 60.0 - life * 20.0;
            c.rect(x, y, 1.0, 1.0, AnimatedCapes.alpha(-4169473, 0.8 * Math.sin(life * Math.PI)));
         }
      }
   }

   static final class TropicalSunset implements AnimatedCapes.Scene {
      private static final int HORIZON = 70;
      private static final double[][] CROWNS = new double[][]{{14.0, 40.0}, {70.0, 52.0}};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.25, 0.42, 0.55}, new int[]{-14020016, -6280584, -759216, -12176});
         c.glow(40.0, 68.0, 46.0, -32704, 0.6);
         c.disc(40.0, 66.0, 15.0, -8054);
         c.disc(40.0, 66.0, 11.0, -3384);

         for (int k = 0; k < 7; k++) {
            double x = AnimatedCapes.hash(k, 3) * 90.0 - 5.0;
            double y = 16 + k * 7 + AnimatedCapes.hash(k, 4) * 3.0;
            c.ellipse(x, y, 16.0 + AnimatedCapes.hash(k, 5) * 10.0, 1.6, 0.0, AnimatedCapes.alpha(AnimatedCapes.lerp(-11920806, -5223830, k / 7.0), 0.9));
            c.ellipse(x - 2.0, y + 1.0, 11.0, 0.6, 0.0, AnimatedCapes.alpha(-20352, 0.75));
         }

         for (int y = 70; y < 102; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-2068374, -14005638, Math.pow((y - 70) / 32.0, 0.7)), x, y));
            }
         }

         c.polygon(new double[][]{{56.0, 71.0}, {60.0, 66.0}, {70.0, 64.0}, {80.0, 67.0}, {80.0, 71.0}}, -12969414);
         c.line(66.0, 65.0, 67.0, 58.0, 0.6, -12969414);
         c.line(67.0, 58.0, 64.0, 59.0, 0.6, -12969414);
         c.line(67.0, 58.0, 70.0, 59.5, 0.6, -12969414);

         for (int y = 100; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.2, y * 0.3, 3);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(AnimatedCapes.lerp(-1529736, -6661552, (y - 100) / 28.0), -8762808, n * 0.35), x, y));
            }
         }

         for (int i = 0; i < 20; i++) {
            c.disc(
               AnimatedCapes.hash(i, 12) * 80.0,
               110.0 + AnimatedCapes.hash(i, 13) * 18.0,
               0.6 + AnimatedCapes.hash(i, 14) * 0.6,
               AnimatedCapes.lerp(-995136, -9815488, AnimatedCapes.hash(i, 15))
            );
         }

         double[][][] trunks = new double[][][]{
            {{6.0, 128.0}, {9.0, 104.0}, {10.0, 80.0}, {12.0, 60.0}, {14.0, 40.0}}, {{76.0, 128.0}, {74.0, 108.0}, {72.0, 88.0}, {71.0, 70.0}, {70.0, 52.0}}
         };

         for (double[][] trunk : trunks) {
            for (int i = 0; i < trunk.length - 1; i++) {
               double w = 3.4 - i * 0.5;
               c.line(trunk[i][0], trunk[i][1], trunk[i + 1][0], trunk[i + 1][1], w, -15069670);

               for (double s = 0.0; s < 1.0; s += 0.25) {
                  double rx = trunk[i][0] + (trunk[i + 1][0] - trunk[i][0]) * s;
                  double ry = trunk[i][1] + (trunk[i + 1][1] - trunk[i][1]) * s;
                  c.line(rx - w / 2.0, ry, rx + w / 2.0, ry - 0.8, 0.5, -13755864);
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 71; y < 100; y++) {
            double d = (y - 70) / 30.0;
            double path = 5.0 + d * 14.0;

            for (int x = 0; x < 80; x++) {
               double dx = Math.abs(x - 40 + Math.sin(y * 0.9 + t * 1.3) * 1.5);
               double wave = Math.sin(x * 0.5 / (d + 0.3) + y * 1.3 - t * 1.6) * 0.5 + 0.5;
               c.add(x, y, -16256, dx < path ? (1.0 - dx / path) * wave * 0.45 : wave * 0.05);
            }
         }

         double reach = 101.0 + 2.5 * Math.sin(t * 0.8);

         for (int x = 0; x < 80; x++) {
            double edge = reach + Math.sin(x * 0.3 + t * 0.8) * 1.2 + AnimatedCapes.noise(x * 0.4, t * 0.5) * 1.5;

            for (int y = 99; y < edge + 4.0; y++) {
               if (y < edge) {
                  c.blend(x, y, AnimatedCapes.alpha(-11896160, 0.55));
               } else {
                  c.blend(x, y, AnimatedCapes.alpha(-10864064, 0.35 * (1.0 - (y - edge) / 4.0)));
               }
            }

            c.blend(x, (int)Math.floor(edge), AnimatedCapes.alpha(-2836, 0.55 + 0.4 * AnimatedCapes.noise(x * 0.8, t)));
         }

         for (int p = 0; p < CROWNS.length; p++) {
            double cx = CROWNS[p][0];
            double cy = CROWNS[p][1];

            for (int k = 0; k < 9; k++) {
               double angle = -Math.PI + k * (Math.PI / 8) + Math.sin(t * 1.1 + k * 0.7 + p) * 0.07;
               double length = 15.0 + AnimatedCapes.hash(k, p + 7) * 6.0;
               double px = cx;
               double py = cy;

               for (int s = 1; s <= 8; s++) {
                  double f = s / 8.0;
                  double nx = cx + Math.cos(angle) * length * f;
                  double ny = cy + Math.sin(angle) * length * f * 0.6 + f * f * (6.0 + Math.abs(Math.cos(angle)) * 6.0);
                  c.line(px, py, nx, ny, 1.2 * (1.0 - f * 0.6), -15463912);
                  double leaf = 3.2 * (1.0 - f * 0.7);
                  c.line(nx, ny, nx + Math.cos(angle + 1.2) * leaf, ny + leaf, 0.6, -15463912);
                  c.line(nx, ny, nx + Math.cos(angle - 1.2) * leaf * 0.7, ny + leaf * 0.9, 0.6, -15463912);
                  px = nx;
                  py = ny;
               }
            }

            c.disc(cx - 1.0, cy + 2.0, 1.5, -14019568);
            c.disc(cx + 1.2, cy + 2.4, 1.4, -14019568);
         }

         for (int k = 0; k < 4; k++) {
            double bx = AnimatedCapes.wrap(t * (5 + k) + k * 25, 110.0) - 15.0;
            AnimatedCapes.bird(c, bx, 30 + k * 6 + Math.sin(t * 0.5 + k) * 2.0, 1.6, t * 7.0 + k, -803206624);
         }
      }
   }
}
