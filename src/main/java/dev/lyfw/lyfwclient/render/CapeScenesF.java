package dev.lyfw.lyfwclient.render;

import java.util.Arrays;

final class CapeScenesF {
   private CapeScenesF() {
   }

   static final class AntColony implements AnimatedCapes.Scene {
      private static final double[][][] TUNNELS = new double[][][]{
         {{40.0, 14.0}, {38.0, 26.0}, {30.0, 36.0}, {24.0, 48.0}, {28.0, 60.0}},
         {{30.0, 36.0}, {46.0, 42.0}, {58.0, 50.0}, {62.0, 64.0}},
         {{28.0, 60.0}, {20.0, 74.0}, {26.0, 90.0}, {40.0, 100.0}},
         {{62.0, 64.0}, {56.0, 80.0}, {48.0, 92.0}, {40.0, 100.0}},
         {{40.0, 100.0}, {44.0, 112.0}, {58.0, 118.0}}
      };
      private static final double[][] CHAMBERS = new double[][]{
         {18.0, 50.0, 9.0, 5.0}, {64.0, 66.0, 9.0, 5.5}, {40.0, 102.0, 11.0, 6.0}, {14.0, 88.0, 8.0, 4.5}, {62.0, 118.0, 10.0, 5.0}
      };

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.1}, new int[]{-8732432, -4661000});

         for (int y = 12; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double layer = y + 4.0 * AnimatedCapes.fbm(x * 0.05, y * 0.01, 3);
               int soil = layer < 40.0 ? -8760784 : (layer < 80.0 ? -9813466 : -10865122);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(soil, 0.85 + AnimatedCapes.fbm(x * 0.4, y * 0.4, 2) * 0.3), x, y));
            }
         }

         for (int x = 0; x < 80; x++) {
            c.rect(x, 11.0, 1.0, 2.0, -11892182);
            c.line(
               x,
               12.0,
               x + (AnimatedCapes.hash(x, 1) - 0.5) * 2.0,
               8.0 - AnimatedCapes.hash(x, 2) * 4.0,
               0.5,
               AnimatedCapes.lerp(-12944854, -9786822, AnimatedCapes.hash(x, 3))
            );
         }

         c.line(66.0, 12.0, 66.0, 2.0, 0.6, -12944854);

         for (int p = 0; p < 5; p++) {
            double a = p * Math.PI * 2.0 / 5.0;
            c.disc(66.0 + Math.cos(a) * 1.8, 2.0 + Math.sin(a) * 1.8, 1.3, -8128);
         }

         c.disc(66.0, 2.0, 0.9, -2061792);

         for (int i = 0; i < 40; i++) {
            c.ellipse(
               AnimatedCapes.hash(i, 5) * 80.0,
               16.0 + AnimatedCapes.hash(i, 6) * 110.0,
               1.0 + AnimatedCapes.hash(i, 7) * 2.0,
               0.8 + AnimatedCapes.hash(i, 8) * 1.2,
               AnimatedCapes.hash(i, 9) * 3.0,
               AnimatedCapes.lerp(-7701910, -11912662, AnimatedCapes.hash(i, 10))
            );
         }

         c.line(6.0, 12.0, 10.0, 40.0, 0.8, -5207456);
         c.line(10.0, 40.0, 4.0, 58.0, 0.6, -5207456);
         c.line(10.0, 30.0, 16.0, 36.0, 0.4, -5207456);

         for (double[][] tunnel : TUNNELS) {
            for (int i = 0; i < tunnel.length - 1; i++) {
               c.line(tunnel[i][0], tunnel[i][1], tunnel[i + 1][0], tunnel[i + 1][1], 4.2, -12966892);
            }
         }

         for (double[][] tunnel : TUNNELS) {
            for (int i = 0; i < tunnel.length - 1; i++) {
               c.line(tunnel[i][0], tunnel[i][1] + 0.8, tunnel[i + 1][0], tunnel[i + 1][1] + 0.8, 2.6, -14412276);
            }
         }

         for (double[] ch : CHAMBERS) {
            c.ellipse(ch[0], ch[1], ch[2] + 1.2, ch[3] + 1.2, 0.0, -12966892);
            c.ellipse(ch[0], ch[1] + 0.8, ch[2], ch[3], 0.0, -14412276);
         }

         for (int s = 0; s < 10; s++) {
            c.ellipse(
               64.0 + (AnimatedCapes.hash(s, 20) - 0.5) * 14.0, 68.0 + AnimatedCapes.hash(s, 21) * 3.0, 1.0, 0.6, AnimatedCapes.hash(s, 22) * 3.0, -2574214
            );
         }

         for (int e = 0; e < 9; e++) {
            c.ellipse(12.0 + e * 1.6 + e % 2 * 0.4, 90.0 - e % 2 * 1.2, 0.9, 0.6, 0.3, -461592);
         }

         for (int l = 0; l < 5; l++) {
            c.ellipse(58.0 + l * 2.5, 120.0, 1.6, 0.8, l, -10837958);
         }

         c.polygon(new double[][]{{36.0, 13.0}, {44.0, 13.0}, {41.0, 10.0}, {39.0, 10.0}}, -9811414);
      }

      private static double[] along(double[][] path, double f) {
         double total = 0.0;

         for (int i = 0; i < path.length - 1; i++) {
            total += Math.hypot(path[i + 1][0] - path[i][0], path[i + 1][1] - path[i][1]);
         }

         double want = f * total;

         for (int i = 0; i < path.length - 1; i++) {
            double seg = Math.hypot(path[i + 1][0] - path[i][0], path[i + 1][1] - path[i][1]);
            if (want <= seg) {
               double k = want / seg;
               return new double[]{
                  path[i][0] + (path[i + 1][0] - path[i][0]) * k,
                  path[i][1] + (path[i + 1][1] - path[i][1]) * k,
                  Math.atan2(path[i + 1][1] - path[i][1], path[i + 1][0] - path[i][0])
               };
            }

            want -= seg;
         }

         double[] last = path[path.length - 1];
         return new double[]{last[0], last[1], 0.0};
      }

      private static void ant(AnimatedCapes.Canvas c, double x, double y, double heading, double t, int cargo) {
         double cos = Math.cos(heading);
         double sin = Math.sin(heading);

         for (int leg = -1; leg <= 1; leg++) {
            double wiggle = Math.sin(t * 16.0 + leg * 2) * 0.7;
            double lx = x + cos * leg * 0.7;
            double ly = y + sin * leg * 0.7;
            c.line(lx, ly, lx - sin * 1.6 + cos * wiggle, ly + cos * 1.6 + sin * wiggle, 0.3, -15726588);
            c.line(lx, ly, lx + sin * 1.6 - cos * wiggle, ly - cos * 1.6 - sin * wiggle, 0.3, -15726588);
         }

         c.disc(x - cos * 1.6, y - sin * 1.6, 1.1, -15069690);
         c.disc(x, y, 0.7, -14018034);
         c.disc(x + cos * 1.3, y + sin * 1.3, 0.8, -15069690);
         if (cargo != 0) {
            c.ellipse(x + cos * 2.6, y + sin * 2.6 - 1.0, 1.6, 1.0, heading + 0.6, cargo);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int[] cargos = new int[]{0, -10837958, -2574214, 0};

         for (int k = 0; k < TUNNELS.length; k++) {
            for (int a = 0; a < 3; a++) {
               double speed = 0.05 + AnimatedCapes.hash(k, a) * 0.03;
               double f = AnimatedCapes.wrap(t * speed + a / 3.0, 1.0);
               boolean back = (k + a) % 2 == 1;
               double[] pos = along(TUNNELS[k], back ? 1.0 - f : f);
               ant(c, pos[0], pos[1] + 0.5, back ? pos[2] + Math.PI : pos[2], t, cargos[(k + a) % cargos.length]);
            }
         }

         double pulse = 1.0 + 0.08 * Math.sin(t * 2.0);
         c.ellipse(34.0, 103.0, 4.2 * pulse, 2.4 * pulse, 0.0, -12967920);
         c.ellipse(34.0, 102.5, 3.0 * pulse, 1.4 * pulse, 0.0, AnimatedCapes.alpha(-9813984, 0.6));
         c.disc(38.8, 102.5, 1.5, -15069690);
         c.disc(40.8, 102.3, 1.2, -15069690);
         c.ellipse(38.0, 101.0, 2.0, 0.6, -0.4, AnimatedCapes.alpha(-1511169, 0.5));

         for (int n = 0; n < 2; n++) {
            double wander = Math.sin(t * 0.7 + n * 3) * 3.0;
            ant(c, 16.0 + wander + n * 3, 87 + n, Math.cos(t * 0.7 + n * 3) > 0.0 ? 0.0 : Math.PI, t, n == 0 ? -461592 : 0);
         }

         double surface = AnimatedCapes.wrap(t * 0.07, 1.0);
         ant(c, 40.0 + (surface - 0.5) * 70.0, 10.5, 0.0, t, -10837958);
         ant(c, 40.0 - (surface - 0.5) * 70.0, 10.5, Math.PI, t, 0);
      }
   }

   static final class AsteroidField implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-16579572, -15988194, AnimatedCapes.fbm(x * 0.04, y * 0.04, 4)), x, y));
            }
         }

         for (int i = 0; i < 180; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               AnimatedCapes.hash(i, 3) > 0.97 ? 1.0 : 0.0,
               -1513217,
               0.1 + AnimatedCapes.hash(i, 4) * 0.45
            );
         }

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 50; x++) {
               double d = Math.hypot(x - 6, y - 134);
               if (d < 40.0) {
                  double band = Math.sin((y - 134) * 0.35 + AnimatedCapes.fbm(x * 0.1, y * 0.1, 3) * 3.0);
                  int col = AnimatedCapes.lerp(-12952950, -7687984, 0.5 + 0.5 * band);
                  double light = AnimatedCapes.clamp((x - 6 + 20) / 40.0, 0.15, 1.0);
                  c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(col, light), x, y));
               }
            }
         }

         c.ellipse(6.0, 134.0, 58.0, 10.0, -0.35, AnimatedCapes.alpha(-3616544, 0.25));
      }

      private static void rock(AnimatedCapes.Canvas c, double x, double y, double r, double spin, int seed, double depth) {
         double[][] points = new double[10][];

         for (int k = 0; k < 10; k++) {
            double a = spin + k * Math.PI / 5.0;
            double rr = r * (0.72 + 0.28 * AnimatedCapes.hash(k, seed));
            points[k] = new double[]{x + Math.cos(a) * rr, y + Math.sin(a) * rr};
         }

         int base = AnimatedCapes.shade(AnimatedCapes.lerp(-9807282, -11908526, AnimatedCapes.hash(seed, 3)), 0.55 + depth * 0.45);
         c.polygon(
            points,
            (px, py) -> AnimatedCapes.shade(
               base, 1.25 - AnimatedCapes.clamp((px - x + r) / (2.0 * r), 0.0, 1.0) * 0.7 + AnimatedCapes.fbm(px * 0.4 + seed, py * 0.4, 2) * 0.2
            )
         );

         for (int k = 0; k < 3; k++) {
            double a = spin * 1.0 + AnimatedCapes.hash(seed, k + 5) * 6.28;
            double d = AnimatedCapes.hash(k, seed + 6) * r * 0.5;
            c.disc(x + Math.cos(a) * d, y + Math.sin(a) * d, r * 0.18, AnimatedCapes.alpha(-16777216, 0.3));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int n = 12;
         double[][] rocks = new double[n][];

         for (int i = 0; i < n; i++) {
            double depth = 0.3 + AnimatedCapes.hash(i, 10) * 0.7;
            double speed = 6.0 + depth * 14.0;
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 11) * 140.0 - t * speed, 140.0) - 30.0;
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 12) * 160.0 + t * speed * 0.6, 160.0) - 16.0;
            rocks[i] = new double[]{x, y, 3.0 + depth * 11.0, depth};
         }

         Arrays.sort(rocks, (a, b) -> Double.compare(a[3], b[3]));

         for (int i = 0; i < n; i++) {
            rock(c, rocks[i][0], rocks[i][1], rocks[i][2], t * (0.3 + AnimatedCapes.hash(i, 13)) * (i % 2 == 0 ? 1 : -1), i * 7 + 3, rocks[i][3]);
         }

         double sx = 40.0 + Math.sin(t * 0.6) * 18.0;
         double sy = 70.0 + Math.sin(t * 0.9) * 10.0;
         double bank = Math.cos(t * 0.6) * 0.4;
         c.glow(sx, sy + 7.0, 6.0, -12533505, 0.7 + 0.2 * Math.sin(t * 20.0));
         c.polygon(new double[][]{{sx, sy - 7.0}, {sx + 5.0 + bank, sy + 5.0}, {sx, sy + 3.0}, {sx - 5.0 + bank, sy + 5.0}}, -2564892);
         c.polygon(new double[][]{{sx, sy - 7.0}, {sx + 1.5, sy + 2.0}, {sx - 1.5, sy + 2.0}}, -1);
         c.ellipse(sx, sy - 2.0, 1.0, 1.8, 0.0, -12541728);
         c.rect(sx - 5.0 + bank, sy + 3.0, 1.0, 3.0, -2082246);
         c.rect(sx + 4.0 + bank, sy + 3.0, 1.0, 3.0, -2082246);
         double shot = AnimatedCapes.wrap(t, 1.6);
         if (shot < 0.6) {
            double p = shot / 0.6;
            c.beam(sx, sy - 8.0 - p * 50.0, sx, sy - 14.0 - p * 50.0, 0.6, -10420352, 1.0 - p * 0.5);
         } else if (shot < 0.9) {
            double p = (shot - 0.6) / 0.3;

            for (int k = 0; k < 8; k++) {
               double a = k * Math.PI / 4.0 + AnimatedCapes.hash((int)(t / 1.6), k);
               c.rect(sx + Math.cos(a) * p * 8.0, sy - 62.0 + Math.sin(a) * p * 8.0, 1.0, 1.0, AnimatedCapes.alpha(-20400, 1.0 - p));
            }

            c.glow(sx, sy - 62.0, 6.0 * (1.0 - p) + 2.0, -12160, 1.0 - p);
         }

         for (int i = 0; i < 20; i++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 80.0 - t * 40.0, 90.0);
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 31) * 128.0 + t * 24.0, 138.0);
            c.line(x, y, x + 3.0, y - 1.8, 0.4, AnimatedCapes.alpha(-4142880, 0.4));
         }
      }
   }

   static final class Beehive implements AnimatedCapes.Scene {
      private static final double R = 4.2;
      private static final double CELL_W = 4.2 * Math.sqrt(3.0);
      private static final double ROW_H = 6.300000000000001;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.fill(-12967416);

         for (int row = -1; row * 6.300000000000001 < 132.2; row++) {
            for (int col = -1; col * CELL_W < 84.2; col++) {
               double cx = col * CELL_W + (row % 2 == 0 ? 0.0 : CELL_W / 2.0);
               double cy = row * 6.300000000000001;
               double[][] hex = new double[6][];

               for (int k = 0; k < 6; k++) {
                  double a = (Math.PI / 6) + k * Math.PI / 3.0;
                  hex[k] = new double[]{cx + Math.cos(a) * 3.6, cy + Math.sin(a) * 3.6};
               }

               double kind = AnimatedCapes.hash(col + 40, row + 40);
               if (kind < 0.45) {
                  c.polygon(hex, (x, y) -> AnimatedCapes.lerp(-20448, -4691448, Math.hypot(x - cx + 1.0, y - cy + 1.0) / 4.2));
                  c.disc(cx - 1.2, cy - 1.2, 0.8, AnimatedCapes.alpha(-3920, 0.8));
               } else if (kind < 0.7) {
                  c.polygon(hex, (x, y) -> AnimatedCapes.lerp(-729968, -2576288, Math.hypot(x - cx, y - cy) / 4.2));
                  c.ring(cx, cy, 1.4, 0.5, AnimatedCapes.alpha(-5207488, 0.6));
               } else if (kind < 0.8) {
                  c.polygon(hex, -14019068);
                  c.ellipse(cx, cy, 1.8, 1.2, kind * 20.0, -462624);
                  c.ellipse(cx + 0.4, cy - 0.3, 0.8, 0.5, 0.0, -1);
               } else {
                  c.polygon(hex, (x, y) -> AnimatedCapes.lerp(-15069692, -12967416, Math.hypot(x - cx, y - cy) / 4.2));
               }
            }
         }

         for (int row = -1; row * 6.300000000000001 < 132.2; row++) {
            for (int col = -1; col * CELL_W < 84.2; col++) {
               double cx = col * CELL_W + (row % 2 == 0 ? 0.0 : CELL_W / 2.0);
               double cy = row * 6.300000000000001;

               for (int k = 0; k < 6; k++) {
                  double a0 = (Math.PI / 6) + k * Math.PI / 3.0;
                  double a1 = a0 + (Math.PI / 3);
                  c.line(cx + Math.cos(a0) * 4.2, cy + Math.sin(a0) * 4.2, cx + Math.cos(a1) * 4.2, cy + Math.sin(a1) * 4.2, 0.8, k < 3 ? -1521544 : -5734336);
               }
            }
         }

         c.vignette(0.45);
      }

      private static void bee(AnimatedCapes.Canvas c, double x, double y, double heading, double t, boolean flying, double size) {
         double cos = Math.cos(heading);
         double sin = Math.sin(heading);

         for (int side = -1; side <= 1; side += 2) {
            double flap = flying ? Math.sin(t * 50.0) * 0.5 : 0.2;
            double wa = heading + Math.PI + side * (1.2 + flap);
            c.ellipse(
               x + Math.cos(wa) * 1.8 * size, y + Math.sin(wa) * 1.8 * size, 2.0 * size, 1.0 * size, wa, AnimatedCapes.alpha(-1510145, flying ? 0.45 : 0.7)
            );
         }

         for (int s = 0; s < 3; s++) {
            double off = (1 - s) * 1.6 * size;
            double px = x + cos * off;
            double py = y + sin * off;
            double r = (s == 2 ? 1.6 : (s == 1 ? 1.1 : 0.9)) * size;
            c.disc(px, py, r, s == 2 ? -1529832 : (s == 1 ? -7706080 : -15068664));
         }

         double ax = x - cos * 1.6 * size;
         double ay = y - sin * 1.6 * size;
         c.line(ax - cos * 0.6 - sin * 0.9, ay - sin * 0.6 + cos * 0.9, ax + cos * 0.6 - sin * 0.9, ay + sin * 0.6 + cos * 0.9, 0.7 * size, -15068664);
         c.line(
            ax - cos * 0.1 - sin * 0.9 * 1.8,
            ay - sin * 0.1 + cos * 0.9 * 1.8,
            ax + cos * 0.1 - sin * 0.9 * 1.8,
            ay + sin * 0.1 + cos * 0.9 * 1.8,
            0.6 * size,
            -15068664
         );
         if (!flying) {
            for (int leg = -1; leg <= 1; leg++) {
               double lx = x + cos * leg * 0.9 * size;
               double ly = y + sin * leg * 0.9 * size;
               double wiggle = Math.sin(t * 12.0 + leg * 2) * 0.6;
               c.line(lx, ly, lx - sin * (2.0 + wiggle) * size, ly + cos * (2.0 + wiggle) * size, 0.3, -15068664);
               c.line(lx, ly, lx + sin * (2.0 - wiggle) * size, ly - cos * (2.0 - wiggle) * size, 0.3, -15068664);
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int i = 0; i < 16; i++) {
            int tick = (int)Math.floor(t * 0.8 + AnimatedCapes.hash(i, 1) * 5.0);
            double x = AnimatedCapes.hash(i, tick + 2) * 80.0;
            double y = AnimatedCapes.hash(i, tick + 3) * 128.0;
            c.star(x, y, 1.0, -2880, 0.6 * Math.sin(AnimatedCapes.wrap(t * 0.8 + AnimatedCapes.hash(i, 1) * 5.0, 1.0) * Math.PI));
         }

         for (int d = 0; d < 4; d++) {
            double x = 8 + d * 20 + AnimatedCapes.hash(d, 5) * 6.0;
            double startY = 10.0 + AnimatedCapes.hash(d, 6) * 50.0;
            double life = AnimatedCapes.wrap(t * 0.2 + d * 0.25, 1.0);
            double stretch = Math.min(1.0, life * 3.0) * 6.0;
            c.line(x, startY, x, startY + stretch, 1.2, -1533424);
            if (life < 0.33) {
               c.disc(x, startY + stretch, 1.3, -18400);
            } else {
               double fall = (life - 0.33) / 0.67;
               double y = startY + 6.0 + fall * fall * 90.0;
               c.ellipse(x, y, 1.1, 1.6, 0.0, -18400);
               c.add((int)x, (int)y - 1, -1, 0.6);
            }
         }

         for (int b = 0; b < 9; b++) {
            double sp = 0.15 + AnimatedCapes.hash(b, 10) * 0.2;
            double x = AnimatedCapes.wrap(
               AnimatedCapes.hash(b, 11) * 80.0 + 30.0 * AnimatedCapes.noise(b * 3.1, t * sp) - 15.0 + t * (AnimatedCapes.hash(b, 12) - 0.5) * 3.0, 80.0
            );
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(b, 13) * 128.0 + 40.0 * (AnimatedCapes.noise(t * sp, b * 2.3) - 0.5), 128.0);
            double nx = 30.0 * AnimatedCapes.noise(b * 3.1, (t + 0.1) * sp) + (t + 0.1) * (AnimatedCapes.hash(b, 12) - 0.5) * 3.0;
            double ny = 40.0 * AnimatedCapes.noise((t + 0.1) * sp, b * 2.3);
            double heading = Math.atan2(
               ny - 40.0 * AnimatedCapes.noise(t * sp, b * 2.3), nx - 30.0 * AnimatedCapes.noise(b * 3.1, t * sp) - t * (AnimatedCapes.hash(b, 12) - 0.5) * 3.0
            );
            bee(c, x, y, heading, t, false, 1.1);
         }

         double qx = 40.0 + Math.sin(t * 0.12) * 14.0;
         double qy = 70.0 + Math.cos(t * 0.09) * 20.0;
         bee(c, qx, qy, t * 0.12 + (Math.PI / 2), t, false, 1.6);
         c.disc(qx - Math.cos(t * 0.12 + (Math.PI / 2)) * 5.0, qy - Math.sin(t * 0.12 + (Math.PI / 2)) * 5.0, 1.4, -3635176);

         for (int b = 0; b < 3; b++) {
            double x = AnimatedCapes.wrap(t * (14 + b * 5) + b * 40, 110.0) - 15.0;
            double y = 20 + b * 35 + Math.sin(t * 3.0 + b) * 4.0;
            bee(c, x, y, 0.0, t, true, 1.3);
         }
      }
   }

   static final class GreatWhale implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.4, 1.0}, new int[]{-10829592, -15045976, -16442824});

         for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 5; y++) {
               c.add(x, y, -1, (0.25 - y * 0.05) * (0.5 + 0.5 * Math.sin(x * 0.6 + AnimatedCapes.fbm(x * 0.1, y, 2) * 5.0)));
            }
         }

         c.fillBelow(xx -> 120.0 + 5.0 * AnimatedCapes.fbm(xx * 0.08, 5.0, 3), -16509912);

         for (int i = 0; i < 20; i++) {
            double x = 10.0 + AnimatedCapes.hash(i, 1) * 60.0;
            double y = 110.0 + AnimatedCapes.hash(i, 2) * 10.0;
            c.ellipse(x, y, 1.5, 0.6, 0.0, AnimatedCapes.alpha(-12948854, 0.4));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 115; y++) {
            for (int x = 0; x < 80; x++) {
               double ray = Math.pow(AnimatedCapes.noise((x - y * 0.25) * 0.07 + t * 0.08, 7.7), 4.0);
               c.add(x, y, -4132609, ray * 0.4 * (1.0 - y / 115.0));
            }
         }

         double cycle = AnimatedCapes.wrap(t * 0.045 + 0.35, 1.0);
         double headX = 110.0 - cycle * 190.0;
         double headY = 70.0 - cycle * 20.0;
         int segments = 26;
         double length = 66.0;
         double[][] spine = new double[segments + 1][];

         for (int s = 0; s <= segments; s++) {
            double f = (double)s / segments;
            spine[s] = new double[]{headX + f * length, headY + f * 8.0 + Math.sin(t * 1.1 - f * 3.0) * f * f * 6.0};
         }

         for (int s = segments; s >= 0; s--) {
            double f = (double)s / segments;
            double th = 10.0 * Math.pow(Math.sin(Math.PI * Math.min(1.0, f * 1.5 + 0.12)), 0.6) * (1.0 - f * 0.82);
            c.disc(spine[s][0] + 3.0, spine[s][1] + th * 0.9 + 4.0, th, AnimatedCapes.alpha(-16642018, 0.12));
         }

         double[] tail = spine[segments];
         double flick = Math.sin(t * 1.1 - 3.0) * 0.35;
         c.polygon(
            new double[][]{
               {tail[0] - 2.0, tail[1]},
               {tail[0] + 9.0, tail[1] - 6.0 + flick * 8.0},
               {tail[0] + 6.0, tail[1] + flick * 5.0},
               {tail[0] + 10.0, tail[1] + 5.0 + flick * 8.0}
            },
            -14797752
         );

         for (int s = segments; s >= 0; s--) {
            double f = (double)s / segments;
            double th = 10.0 * Math.pow(Math.sin(Math.PI * Math.min(1.0, f * 1.5 + 0.12)), 0.6) * (1.0 - f * 0.82);
            c.disc(spine[s][0], spine[s][1], th, AnimatedCapes.lerp(-14008232, -15062466, f));
            c.disc(spine[s][0], spine[s][1] + th * 0.45, th * 0.62, AnimatedCapes.lerp(-4667184, -7693656, f));
         }

         for (int s = 2; s < segments / 2; s++) {
            double th = 10.0 * Math.pow(Math.sin(Math.PI * Math.min(1.0, (double)s / segments * 1.5 + 0.12)), 0.6);
            c.line(spine[s][0], spine[s][1] + th * 0.35, spine[s + 1][0], spine[s + 1][1] + th * 0.35, 0.3, AnimatedCapes.alpha(-10851720, 0.7));
            c.line(spine[s][0], spine[s][1] + th * 0.6, spine[s + 1][0], spine[s + 1][1] + th * 0.6, 0.3, AnimatedCapes.alpha(-10851720, 0.7));
         }

         for (int i = 0; i < 8; i++) {
            int s = 2 + i * 2;
            c.disc(spine[s][0] + AnimatedCapes.hash(i, 1) * 2.0, spine[s][1] - 4.0 - AnimatedCapes.hash(i, 2) * 3.0, 0.5, -2565936);
         }

         double finSweep = Math.sin(t * 0.8) * 0.35;
         double[] root = spine[7];
         double fa = 1.2 + finSweep;
         c.polygon(
            new double[][]{
               {root[0] - 2.0, root[1] + 4.0}, {root[0] + 3.0, root[1] + 5.0}, {root[0] - 4.0 + Math.cos(fa) * 22.0, root[1] + 5.0 + Math.sin(fa) * 22.0}
            },
            -14008232
         );
         c.line(root[0] + 1.0, root[1] + 5.0, root[0] - 4.0 + Math.cos(fa) * 22.0, root[1] + 5.0 + Math.sin(fa) * 22.0, 1.0, -3615526);
         c.disc(spine[2][0] + 1.0, spine[2][1] + 1.0, 0.8, -16118252);
         c.line(spine[0][0] - 3.0, spine[0][1] + 3.0, spine[4][0], spine[4][1] + 4.5, 0.5, -16116704);

         for (int b = 0; b < 10; b++) {
            double life = AnimatedCapes.wrap(t * 0.5 + b / 10.0, 1.0);
            double bx = spine[3][0] + life * 30.0 + Math.sin(life * 9.0 + b) * 2.0;
            double by = spine[3][1] - 8.0 - life * 60.0;
            c.ring(bx, by, 0.6 + life * 1.2, 0.4, AnimatedCapes.alpha(-2031617, 0.7 * (1.0 - life)));
         }

         for (int f = 0; f < 18; f++) {
            double a = t * 0.4 + f * 0.35;
            double x = 20.0 + Math.cos(a) * 14.0 + Math.sin(t * 0.7) * 8.0;
            double y = 24.0 + Math.sin(a * 1.3) * 6.0;
            c.ellipse(x, y, 1.2, 0.5, Math.cos(a) > 0.0 ? 0.3 : -0.3, -3612432);
         }

         for (int i = 0; i < 40; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 50) * 128.0 + t * 1.5, 128.0);
            c.add((int)(AnimatedCapes.hash(i, 51) * 80.0), (int)y, -3084033, 0.25);
         }
      }
   }

   static final class MistyLake implements AnimatedCapes.Scene {
      private static final int SHORE = 74;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.35, 0.58}, new int[]{-8746304, -1527624, -10064});
         c.glow(50.0, 56.0, 30.0, -8016, 0.45);
         AnimatedCapes.mountains(c, 31, 60.0, 26.0, 0.05, -7698264, -1514256);

         for (int layer = 0; layer < 3; layer++) {
            int col = AnimatedCapes.lerp(-8750440, -14011328, layer / 2.0);
            double baseY = 62 + layer * 5;

            for (int i = 0; i < 26; i++) {
               AnimatedCapes.pine(
                  c,
                  AnimatedCapes.hash(i, layer + 1) * 80.0,
                  baseY + AnimatedCapes.hash(i, layer + 2) * 4.0,
                  7 + layer * 3 + AnimatedCapes.hash(i, layer + 3) * 5.0,
                  col
               );
            }

            for (int y = (int)baseY - 4; y < baseY + 6.0; y++) {
               for (int x = 0; x < 80; x++) {
                  c.blend(x, y, AnimatedCapes.alpha(-728872, 0.25 * (1.0 - Math.abs(y - baseY - 1.0) / 6.0)));
               }
            }
         }

         c.rect(0.0, 72.0, 80.0, 3.0, -14012360);

         for (int y = 74; y < 128; y++) {
            int src = 73 - (int)((y - 74) * 1.1);

            for (int x = 0; x < 80; x++) {
               int col = src >= 0 ? c.get(x, src) : -8746304;
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(AnimatedCapes.shade(col, 0.82), -12957592, 0.15 + (y - 74) / 120.0), x, y));
            }
         }

         c.polygon(new double[][]{{-2.0, 104.0}, {30.0, 96.0}, {32.0, 99.0}, {-2.0, 108.0}}, -10862544);

         for (int p = 0; p < 4; p++) {
            double px = p * 9;
            c.rect(px, 98.0 + p * -2.2 + 4.0, 1.5, 12.0, -12965346);
         }

         for (int k = 0; k < 7; k++) {
            c.line(k * 4.5, 105.0 - k * 1.1, k * 4.5 + 3.0, 104.3 - k * 1.1, 0.4, -12965346);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         int[] row = new int[80];

         for (int y = 75; y < 128; y++) {
            int shift = (int)Math.round(Math.sin(y * 0.7 + t * 1.5) * (y - 74) / 40.0);
            if (shift != 0) {
               for (int x = 0; x < 80; x++) {
                  row[x] = c.get(Math.floorMod(x + shift, 80), y);
               }

               for (int x = 0; x < 80; x++) {
                  if (y <= 94 || x >= 34) {
                     c.set(x, y, row[x]);
                  }
               }
            }
         }

         double bob = Math.sin(t * 1.2) * 0.6;
         c.polygon(new double[][]{{26.0, 108.0 + bob}, {46.0, 108.0 + bob}, {42.0, 113.0 + bob}, {30.0, 113.0 + bob}}, -8766934);
         c.rect(28.0, 107.5 + bob, 17.0, 1.0, -4691382);
         c.line(33.0, 108.0 + bob, 30.0, 104.0, 0.5, -12965346);
         c.ellipse(36.0, 115.0 + bob, 10.0, 1.2, 0.0, AnimatedCapes.alpha(-15065040, 0.4));
         double lx = AnimatedCapes.wrap(t * 2.2, 110.0) - 10.0;
         double ly = 88.0;

         for (int k = 1; k < 8; k++) {
            c.line(lx - k * 3, ly + 1.0 - k * 0.2, lx - k * 3 - 2.0, ly + 1.0 + k * 0.9, 0.4, AnimatedCapes.alpha(-1515280, 0.4 * (1.0 - k / 8.0)));
            c.line(lx - k * 3, ly + 1.0 + k * 0.2, lx - k * 3 - 2.0, ly + 1.0 - k * 0.6, 0.4, AnimatedCapes.alpha(-1515280, 0.3 * (1.0 - k / 8.0)));
         }

         c.ellipse(lx, ly, 3.4, 1.4, 0.0, -15065564);
         c.line(lx + 2.0, ly - 0.5, lx + 3.5, ly - 3.0, 1.0, -15065564);
         c.disc(lx + 3.8, ly - 3.3, 1.1, -15065564);
         c.line(lx + 4.5, ly - 3.3, lx + 6.3, ly - 3.0, 0.4, -14013904);

         for (int i = 0; i < 6; i++) {
            c.rect(lx - 2.0 + i, ly - 0.6, 0.5, 0.5, -1513240);
         }

         for (int yx = 56; yx < 128; yx++) {
            for (int xx = 0; xx < 80; xx++) {
               double band = Math.exp(-Math.pow((yx - 78) / 10.0, 2.0)) + Math.exp(-Math.pow((yx - 64) / 4.0, 2.0)) * 0.6;
               double n = AnimatedCapes.fbm(xx * 0.05 + t * 0.12, yx * 0.15, 3);
               c.blend(xx, yx, AnimatedCapes.alpha(-3860, AnimatedCapes.smoothstep(0.35, 0.7, n) * band * 0.6));
            }
         }

         for (int k = 0; k < 3; k++) {
            AnimatedCapes.bird(c, AnimatedCapes.wrap(t * 4.0 + k * 12, 100.0) - 10.0, 30 + k * 3, 1.4, t * 6.0 + k, -1606796726);
         }
      }
   }

   static final class MoonBase implements AnimatedCapes.Scene {
      private static final int GROUND = 86;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.fill(-16645626);

         for (int i = 0; i < 220; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 86.0,
               AnimatedCapes.hash(i, 3) > 0.97 ? 1.0 : 0.0,
               -1,
               0.1 + AnimatedCapes.hash(i, 4) * 0.5
            );
         }

         for (int y = 8; y < 48; y++) {
            for (int x = 36; x < 76; x++) {
               double dx = (x + 0.5 - 56.0) / 17.0;
               double dy = (y + 0.5 - 27.0) / 17.0;
               double d2 = dx * dx + dy * dy;
               if (d2 < 1.0) {
                  double nz = Math.sqrt(1.0 - d2);
                  double lon = Math.atan2(dx, nz);
                  double land = AnimatedCapes.fbm(lon * 2.0 + 3.0, dy * 3.0, 4);
                  int col = land > 0.55 ? AnimatedCapes.lerp(-11892166, -4677520, (land - 0.55) * 3.0) : AnimatedCapes.lerp(-15054182, -13997376, land);
                  double clouds = AnimatedCapes.smoothstep(0.5, 0.75, AnimatedCapes.fbm(lon * 3.0 + 9.0, dy * 5.0, 4));
                  col = AnimatedCapes.lerp(col, -1, clouds * 0.85);
                  double light = AnimatedCapes.clamp(-dx * 0.7 - dy * 0.2 + nz * 0.5, 0.0, 1.0);
                  c.set(x, y, AnimatedCapes.shade(col, 0.05 + light * 1.05));
                  c.add(x, y, -9785089, Math.pow(1.0 - nz, 2.0) * 0.5 * light);
               }
            }
         }

         c.glow(56.0, 27.0, 24.0, -11892000, 0.15);

         for (int y = 80; y < 128; y++) {
            for (int xx = 0; xx < 80; xx++) {
               double top = 86.0 - 4.0 * Math.sin(xx * 0.06) - 3.0 * AnimatedCapes.fbm(xx * 0.1, 2.0, 3);
               if (y >= top) {
                  c.set(
                     xx,
                     y,
                     AnimatedCapes.dither(
                        AnimatedCapes.shade(AnimatedCapes.lerp(-7697778, -11908528, AnimatedCapes.fbm(xx * 0.2, y * 0.2, 3)), 0.8 + (128 - y) / 200.0), xx, y
                     )
                  );
               }
            }
         }

         double[][] craters = new double[][]{{14.0, 100.0, 7.0}, {60.0, 116.0, 9.0}, {38.0, 122.0, 5.0}, {70.0, 94.0, 4.0}, {6.0, 120.0, 4.0}};

         for (double[] cr : craters) {
            c.ellipse(cr[0], cr[1], cr[2], cr[2] * 0.35, 0.0, -12961216);
            c.ellipse(cr[0] + cr[2] * 0.15, cr[1] + cr[2] * 0.08, cr[2] * 0.8, cr[2] * 0.25, 0.0, -10855840);
            c.ellipse(cr[0] - cr[2] * 0.2, cr[1] - cr[2] * 0.3, cr[2] * 0.9, cr[2] * 0.12, 0.0, -5723988);
         }

         c.rect(8.0, 76.0, 1.0, 14.0, -9803152);
         c.rect(7.0, 76.0, 3.0, 1.0, -9803152);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] domes = new double[][]{{26.0, 88.0, 9.0}, {44.0, 90.0, 6.0}, {12.0, 90.0, 4.0}};

         for (int k = 0; k < domes.length; k++) {
            double[] d = domes[k];

            for (int y = (int)(d[1] - d[2]); y <= d[1]; y++) {
               for (int x = (int)(d[0] - d[2]); x <= d[0] + d[2]; x++) {
                  double u = (x + 0.5 - d[0]) / d[2];
                  double v = (d[1] - y - 0.5) / d[2];
                  if (u * u + v * v <= 1.0) {
                     c.set(x, y, AnimatedCapes.lerp(-9793360, -3087116, AnimatedCapes.clamp(0.5 - u * 0.5 + v * 0.3, 0.0, 1.0)));
                  }
               }
            }

            for (int w = 0; w < 3; w++) {
               boolean lit = AnimatedCapes.hash(k * 3 + w, (int)Math.floor(t * 0.7)) > 0.3;
               c.rect(d[0] - d[2] * 0.6 + w * d[2] * 0.5, d[1] - 2.0, Math.max(1.0, d[2] * 0.25), 1.2, lit ? -10128 : -12957094);
            }

            c.rect(d[0] - d[2], d[1], d[2] * 2.0, 1.0, -10855838);
         }

         c.rect(34.0, 89.0, 3.0, 1.0, -6645088);
         if (AnimatedCapes.wrap(t, 1.2) < 0.25) {
            c.glow(8.5, 76.0, 3.0, -53200, 1.0);
         }

         double dish = Math.sin(t * 0.5) * 0.8;
         c.line(60.0, 90.0, 60.0, 82.0, 0.6, -6645088);
         c.ellipse(60.0 + dish, 80.0, 3.0, 1.2, 0.3 + dish * 0.4, -3092264);
         double rx = AnimatedCapes.wrap(t * 3.0, 110.0) - 15.0;
         double ry = 104.0 + Math.sin(rx * 0.1) * 0.5;

         for (int k = 1; k < 20; k++) {
            c.rect(rx - k * 2 - 4.0, ry + 3.5, 1.2, 0.5, AnimatedCapes.alpha(-12961216, 0.6 * (1.0 - k / 20.0)));
         }

         c.rect(rx - 5.0, ry - 2.0, 10.0, 4.0, -2039578);
         c.rect(rx - 5.0, ry - 2.0, 10.0, 1.0, -1);
         c.rect(rx + 2.0, ry - 5.0, 3.0, 3.0, AnimatedCapes.alpha(-9785120, 0.9));
         c.line(rx - 3.0, ry - 2.0, rx - 4.0, ry - 7.0, 0.4, -6645088);

         for (int w = -1; w <= 1; w++) {
            double wx = rx + w * 3.5;
            c.disc(wx, ry + 2.5, 1.5, -14013904);
            double spin = t * 3.0 + w;
            c.line(wx, ry + 2.5, wx + Math.cos(spin) * 1.3, ry + 2.5 + Math.sin(spin) * 1.3, 0.3, -7697776);
         }

         double hopCycle = AnimatedCapes.wrap(t * 0.8, 1.0);
         double ax = 60.0 - AnimatedCapes.wrap(t * 2.0, 50.0);
         double ay = 118.0 - Math.sin(hopCycle * Math.PI) * 8.0;
         if (hopCycle < 0.08) {
            for (int p = 0; p < 6; p++) {
               double a = Math.PI + p * Math.PI / 5.0;
               c.disc(ax + Math.cos(a) * hopCycle * 60.0, 124.0 + Math.sin(a) * hopCycle * 20.0, 0.8, AnimatedCapes.alpha(-5723988, 0.6));
            }
         }

         c.rect(ax - 2.5, ay - 4.0, 5.0, 6.0, -986892);
         c.rect(ax - 3.5, ay - 3.0, 1.5, 4.0, -3092264);
         c.disc(ax, ay - 6.5, 2.6, -986892);
         c.ellipse(ax + 0.6, ay - 6.5, 1.6, 1.2, 0.0, -2056144);
         c.rect(ax - 2.0, ay + 2.0, 1.5, 3.0, -2565922);
         c.rect(ax + 0.5, ay + 2.0, 1.5, 3.0, -2565922);
         c.line(ax + 2.5, ay - 2.0, ax + 5.0, ay - 12.0, 0.5, -5197640);
         c.rect(ax + 5.0, ay - 12.0, 5.0, 3.0, -14005584);
         c.rect(ax + 5.0, ay - 11.0, 5.0, 0.6, -2082246);
      }
   }

   static final class Penguins implements AnimatedCapes.Scene {
      private static final int SEA = 78;

      private static double shelf(double x) {
         return x < 44.0 ? 88.0 : 88.0 + (x - 44.0) * 0.9;
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 0.6}, new int[]{-9787168, -2561804, -727848});
         c.glow(20.0, 60.0, 30.0, -5952, 0.5);
         c.disc(20.0, 60.0, 5.0, -1816);

         for (int y = 78; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-12944720, -16111024, (y - 78) / 50.0), x, y));
            }
         }

         double[][][] bergs = new double[][][]{
            {{50.0, 79.0}, {54.0, 64.0}, {60.0, 58.0}, {66.0, 62.0}, {70.0, 54.0}, {76.0, 66.0}, {80.0, 79.0}},
            {{4.0, 79.0}, {8.0, 70.0}, {14.0, 72.0}, {18.0, 79.0}}
         };

         for (double[][] berg : bergs) {
            c.polygon(
               berg,
               (xx, y) -> xx < berg[2][0] ? AnimatedCapes.lerp(-722177, -3087116, (79 - y) / 30.0) : AnimatedCapes.lerp(-5715740, -7819052, (79 - y) / 30.0)
            );
            double[][] under = new double[berg.length][];

            for (int i = 0; i < berg.length; i++) {
               under[i] = new double[]{berg[i][0] + (berg[i][0] - 40.0) * 0.05, 79.0 + (79.0 - berg[i][1]) * 0.7};
            }

            c.polygon(under, AnimatedCapes.alpha(-10839352, 0.45));
         }

         c.polygon(
            new double[][]{{-2.0, 88.0}, {44.0, 88.0}, {82.0, 122.0}, {82.0, 132.0}, {-2.0, 132.0}},
            (xx, y) -> AnimatedCapes.lerp(-985348, -4665116, AnimatedCapes.fbm(xx * 0.1, y * 0.15, 3) * 0.6 + (y - 88) / 80.0)
         );
         c.line(-2.0, 88.0, 44.0, 88.0, 0.8, -1);
         c.line(44.0, 88.0, 82.0, 122.0, 0.8, -7821112);
      }

      private static void penguin(AnimatedCapes.Canvas c, double x, double y, double sway, double scale, boolean lying) {
         if (lying) {
            c.ellipse(x, y, 6.0 * scale, 2.6 * scale, sway, -15065562);
            c.ellipse(x, y + 0.8 * scale, 4.6 * scale, 1.5 * scale, sway, -723728);
            c.disc(x + 5.6 * scale * Math.cos(sway), y + 5.6 * scale * Math.sin(sway) - 0.5, 2.0 * scale, -15065562);
            c.polygon(
               new double[][]{
                  {x + 7.3 * scale, y - 0.2 + 7.0 * scale * Math.sin(sway)},
                  {x + 9.0 * scale, y + 0.5 + 7.0 * scale * Math.sin(sway)},
                  {x + 7.3 * scale, y + 0.9 + 7.0 * scale * Math.sin(sway)}
               },
               -1013216
            );
         } else {
            c.ellipse(x - 1.2 * scale, y + 7.5 * scale, 1.4 * scale, 0.6 * scale, 0.0, -1013216);
            c.ellipse(x + 1.2 * scale, y + 7.5 * scale, 1.4 * scale, 0.6 * scale, 0.0, -1013216);
            c.ellipse(x, y + 1.5 * scale, 3.6 * scale, 6.0 * scale, sway, -15065562);
            c.ellipse(x + 0.4 * scale, y + 2.2 * scale, 2.4 * scale, 4.6 * scale, sway, -723728);
            c.ellipse(x - 3.0 * scale, y + 2.0 * scale, 1.0 * scale, 3.4 * scale, 0.35 + sway, -15065562);
            c.disc(x + sway * 3.0, y - 5.0 * scale, 2.5 * scale, -15065562);
            c.disc(x + sway * 3.0 + 0.9 * scale, y - 5.4 * scale, 0.5 * scale, -1);
            c.polygon(
               new double[][]{
                  {x + sway * 3.0 + 2.0 * scale, y - 5.2 * scale},
                  {x + sway * 3.0 + 4.0 * scale, y - 4.5 * scale},
                  {x + sway * 3.0 + 2.0 * scale, y - 4.0 * scale}
               },
               -1013216
            );
            c.ellipse(x + sway * 3.0 + 0.5 * scale, y - 3.2 * scale, 1.2 * scale, 0.6 * scale, 0.0, -997312);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 79; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double wave = AnimatedCapes.noise(x * 0.3 + t * 0.5, y * 0.9 - t);
               if (wave > 0.7) {
                  c.add(x, y, -1, (wave - 0.7) * 0.8 * (1.0 - (y - 78) / 60.0));
               }
            }
         }

         double[][] standing = new double[][]{{8.0, 80.0, 0.9}, {18.0, 81.0, 1.0}, {30.0, 80.0, 0.85}};

         for (int k = 0; k < standing.length; k++) {
            double waddle = Math.sin(t * 4.0 + k * 2) * 0.12;
            double hop = Math.abs(Math.sin(t * 4.0 + k * 2)) * 0.6;
            penguin(c, standing[k][0] + Math.sin(t * 0.3 + k) * 2.0, standing[k][1] - hop, waddle, standing[k][2], false);
         }

         double slide = AnimatedCapes.wrap(t, 7.0) / 7.0;
         if (slide < 0.25) {
            double p = slide / 0.25;
            penguin(c, 40.0 - p * 2.0, 81.0 - Math.sin(p * Math.PI) * 2.0, Math.sin(p * 20.0) * 0.2, 1.0, false);
         } else if (slide < 0.55) {
            double p = (slide - 0.25) / 0.3;
            double xx = 44.0 + p * 34.0;
            penguin(c, xx, shelf(xx) - 2.5, Math.atan(0.9), 0.9, true);
            c.line(xx - 8.0, shelf(xx - 8.0) - 1.0, xx - 3.0, shelf(xx - 3.0) - 1.0, 0.5, AnimatedCapes.alpha(-1, 0.7));
         } else if (slide < 0.7) {
            double p = (slide - 0.55) / 0.15;

            for (int d = 0; d < 10; d++) {
               double a = (-Math.PI / 2) + (d - 4.5) * 0.25;
               c.disc(76.0 + Math.cos(a) * p * 10.0, 118.0 + Math.sin(a) * p * 12.0 + p * p * 10.0, 0.7, AnimatedCapes.alpha(-1509121, 1.0 - p));
            }

            c.ring(76.0, 120.0, p * 8.0, 0.6, AnimatedCapes.alpha(-1, 0.6 * (1.0 - p)));
         }

         double leap = AnimatedCapes.wrap(t + 2.5, 5.0);
         if (leap < 1.1) {
            double p = leap / 1.1;
            double xx = 70.0 - p * 26.0;
            double y = 108.0 - Math.sin(p * Math.PI) * 26.0;
            penguin(c, xx, y, -Math.PI + Math.atan2(-Math.cos(p * Math.PI) * 26.0, -26.0) + Math.PI, 0.8, true);
            if (p < 0.1 || p > 0.9) {
               c.ring(p < 0.5 ? 70.0 : 44.0, 108.0, 4.0, 0.6, AnimatedCapes.alpha(-1, 0.7));
            }
         }

         for (int i = 0; i < 10; i++) {
            int tick = (int)Math.floor(t * 1.5 + AnimatedCapes.hash(i, 1) * 5.0);
            double xx = AnimatedCapes.hash(i, tick + 2) * 80.0;
            double y = 88.0 + AnimatedCapes.hash(i, tick + 3) * 40.0;
            if (y > shelf(xx)) {
               c.star(xx, y, 1.0, -1, 0.8 * Math.sin(AnimatedCapes.wrap(t * 1.5 + AnimatedCapes.hash(i, 1) * 5.0, 1.0) * Math.PI));
            }
         }
      }
   }

   static final class RainbowMeadow implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6}, new int[]{-11887896, -3611400});
         AnimatedCapes.cloud(c, 14.0, 12.0, 8.0, -4669236, -8747884, 3);
         AnimatedCapes.cloud(c, 34.0, 6.0, 6.0, -3616552, -7695200, 4);
         c.fillBelow(x -> 76.0 + 6.0 * Math.sin(x * 0.07 + 1.0), -8734614);
         c.fillBelow(x -> 88.0 + 5.0 * Math.sin(x * 0.05 + 3.0), -10837944);

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= 88.0 + 5.0 * Math.sin(x * 0.05 + 3.0)) {
                  c.set(
                     x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-9785270, -12944854, (y - 90) / 40.0 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.25), x, y)
                  );
               }
            }
         }

         double[][] puddles = new double[][]{{24.0, 112.0, 10.0, 2.5}, {50.0, 122.0, 8.0, 2.0}};

         for (double[] p : puddles) {
            c.ellipse(p[0], p[1], p[2], p[3], 0.0, -7683856);
            c.ellipse(p[0] - 2.0, p[1] - 0.5, p[2] * 0.5, p[3] * 0.4, 0.0, AnimatedCapes.alpha(-1, 0.5));
         }

         for (int i = 0; i < 50; i++) {
            double xx = AnimatedCapes.hash(i, 5) * 80.0;
            double y = 94.0 + AnimatedCapes.hash(i, 6) * 34.0;
            int col = new int[]{-1, -8128, -2073974, -5207312}[i % 4];
            c.disc(xx, y, 0.8 + (y - 94.0) / 60.0, col);
         }

         c.line(70.0, 104.0, 68.0, 70.0, 2.4, -10864094);
         c.line(68.0, 80.0, 58.0, 70.0, 1.2, -10864094);

         for (int i = 0; i < 40; i++) {
            double a = AnimatedCapes.hash(i, 8) * Math.PI * 2.0;
            double d = Math.sqrt(AnimatedCapes.hash(i, 9)) * 13.0;
            c.disc(66.0 + Math.cos(a) * d, 62.0 + Math.sin(a) * d * 0.8, 2.6, AnimatedCapes.lerp(-13993430, -10833846, AnimatedCapes.hash(i, 10)));
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double breathe = 0.75 + 0.25 * Math.sin(t * 0.5);

         for (int y = 0; y < 92; y++) {
            for (int x = 0; x < 80; x++) {
               double r = Math.hypot(x - 30, (y - 100) * 1.05);
               if (r > 54.0 && r < 68.0) {
                  int band = AnimatedCapes.hsv((r - 54.0) / 14.0 * 290.0, 0.85, 1.0);
                  double edge = Math.min(r - 54.0, 68.0 - r) / 2.0;
                  c.add(x, y, band, 0.28 * breathe * AnimatedCapes.clamp(edge, 0.0, 1.0) * (1.0 - y / 110.0));
               }
            }
         }

         for (int y = 0; y < 60; y++) {
            for (int xx = 40; xx < 80; xx++) {
               double ray = Math.pow(AnimatedCapes.noise((xx + y * 0.6) * 0.08 + t * 0.05, 5.5), 4.0);
               c.add(xx, y, -32, ray * 0.14);
            }
         }

         for (int d = 0; d < 6; d++) {
            double life = AnimatedCapes.wrap(t * 0.6 + AnimatedCapes.hash(d, 20), 1.0);
            double xx = 58.0 + AnimatedCapes.hash(d, 21) * 18.0;
            double y = 70.0 + life * life * 50.0;
            c.ellipse(xx, y, 0.6, 1.1, 0.0, AnimatedCapes.alpha(-2559745, 0.9));
            c.add((int)xx, (int)y, -1, 0.6);
         }

         for (int k = 0; k < 2; k++) {
            double life = AnimatedCapes.wrap(t * 0.8 + k * 0.5, 1.0);
            c.ring(24.0 + (k - 0.5) * 8.0, 112.0, life * 6.0, 0.5, AnimatedCapes.alpha(-1, 0.6 * (1.0 - life)));
         }

         for (int b = 0; b < 2; b++) {
            double cycle = t * 0.9 + b * 1.7;
            double hop = AnimatedCapes.wrap(cycle, 1.0);
            double xx = AnimatedCapes.wrap(Math.floor(cycle) * 7.0 + hop * 7.0 + b * 40, 100.0) - 10.0;
            double y = 106 + b * 10 - Math.sin(hop * Math.PI) * 5.0;
            int fur = b == 0 ? -6260134 : -1515304;
            c.ellipse(xx, y, 3.4, 2.4, 0.0, fur);
            c.disc(xx + 3.0, y - 2.0, 1.8, fur);
            c.ellipse(xx + 2.4, y - 5.0, 0.6, 2.2, -0.2, fur);
            c.ellipse(xx + 3.6, y - 5.0, 0.6, 2.2, 0.2, fur);
            c.disc(xx - 3.2, y - 0.5, 1.0, -1);
            c.rect(xx + 3.6, y - 2.5, 0.6, 0.6, -15066598);
         }

         for (int f = 0; f < 3; f++) {
            double xx = 40.0 + 30.0 * (AnimatedCapes.noise(f * 2.1, t * 0.2) - 0.5) * 2.0;
            double y = 80.0 + 20.0 * (AnimatedCapes.noise(t * 0.2, f * 3.3) - 0.5) * 2.0;
            double flap = Math.abs(Math.cos(t * 10.0 + f));
            int col = f == 1 ? -8096 : -24376;
            c.ellipse(xx - 1.2 * flap, y, 1.4 * flap + 0.2, 1.2, 0.3, col);
            c.ellipse(xx + 1.2 * flap, y, 1.4 * flap + 0.2, 1.2, -0.3, col);
         }

         double drift = t * 1.5;
         c.glow(AnimatedCapes.wrap(-drift, 120.0) - 20.0, 8.0, 14.0, -1, 0.1);
      }
   }

   static final class RocketLaunch implements AnimatedCapes.Scene {
      private static final int PAD = 104;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.45, 0.8}, new int[]{-15459782, -10864022, -2061718});
         AnimatedCapes.mountains(c, 41, 100.0, 12.0, 0.05, -12965312, 0);
         c.fillBelow(x -> 104.0, -14013904);

         for (int y = 104; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-12961216, -15066592, (y - 104) / 24.0), x, y));
            }
         }

         c.rect(22.0, 100.0, 36.0, 5.0, -11908528);
         c.rect(22.0, 100.0, 36.0, 1.0, -8750464);
         int steel = -7718358;

         for (int side = 0; side < 2; side++) {
            double tx = 50 + side * 5;
            c.line(tx, 100.0, tx, 40.0, 0.8, steel);
         }

         for (double y = 42.0; y < 100.0; y += 5.0) {
            c.line(50.0, y, 55.0, y + 5.0, 0.5, steel);
            c.line(55.0, y, 50.0, y + 5.0, 0.5, steel);
         }

         c.rect(49.0, 38.0, 7.0, 2.0, steel);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycle = AnimatedCapes.wrap(t, 14.0);
         double rise = cycle < 4.0 ? 0.0 : Math.pow((cycle - 4.0) / 8.0, 2.2) * 150.0;
         double burn = cycle < 4.0 ? 0.0 : Math.min(1.0, (cycle - 4.0) * 2.0);
         double rocketBase = 100.0 - rise;
         if (burn > 0.0) {
            for (int y = 0; y < 128; y++) {
               for (int x = 0; x < 80; x++) {
                  c.add(x, y, -30144, 0.12 * burn * Math.max(0.0, 1.0 - Math.hypot(x - 40, y - rocketBase) / 90.0));
               }
            }
         }

         if (burn > 0.0 && rise < 180.0) {
            for (int y = (int)Math.max(0.0, rocketBase); y < 104; y++) {
               double f = (y - rocketBase) / Math.max(1.0, 104.0 - rocketBase);
               double w = 1.5 + f * 2.5;
               c.rect(40.0 - w, y, w * 2.0, 1.0, AnimatedCapes.alpha(-1515288, 0.55 * (1.0 - f * 0.4)));
            }
         }

         if (burn > 0.0) {
            double spread = Math.min(1.0, (cycle - 4.0) / 3.0);

            for (int y = 80; y < 106; y++) {
               for (int x = 0; x < 80; x++) {
                  double dx = (x - 40) / (10.0 + spread * 34.0);
                  double dy = (y - 100) / (4.0 + spread * 12.0);
                  double d = dx * dx + dy * dy;
                  if (d < 1.3) {
                     double n = AnimatedCapes.fbm(x * 0.12 + (x < 40 ? t * 0.8 : -t * 0.8), y * 0.15 - t * 0.2, 4);
                     c.blend(
                        x,
                        y,
                        AnimatedCapes.alpha(
                           AnimatedCapes.lerp(-725784, -6649200, n), AnimatedCapes.smoothstep(0.25, 0.6, n) * AnimatedCapes.clamp(1.3 - d, 0.0, 1.0)
                        )
                     );
                  }
               }
            }
         } else {
            for (int k = 0; k < 5; k++) {
               double life = AnimatedCapes.wrap(t * 0.8 + k / 5.0, 1.0);
               c.disc(37.0 + (k % 2 == 0 ? -life * 6.0 : life * 6.0), 96.0 - life * 4.0, 1.0 + life * 3.0, AnimatedCapes.alpha(-986892, 0.5 * (1.0 - life)));
            }
         }

         c.rect(36.0, rocketBase - 34.0, 8.0, 34.0, -986894);
         c.rect(41.5, rocketBase - 34.0, 2.5, 34.0, -3618612);
         c.rect(36.0, rocketBase - 20.0, 8.0, 3.0, -14013904);
         c.rect(36.0, rocketBase - 8.0, 8.0, 2.0, -14013904);
         c.polygon(new double[][]{{36.0, rocketBase - 34.0}, {44.0, rocketBase - 34.0}, {40.0, rocketBase - 44.0}}, -1513236);
         c.polygon(new double[][]{{40.0, rocketBase - 44.0}, {44.0, rocketBase - 34.0}, {41.5, rocketBase - 34.0}}, -4671300);
         c.polygon(new double[][]{{36.0, rocketBase - 8.0}, {32.0, rocketBase + 1.0}, {36.0, rocketBase}}, -4179398);
         c.polygon(new double[][]{{44.0, rocketBase - 8.0}, {48.0, rocketBase + 1.0}, {44.0, rocketBase}}, -6280662);
         c.disc(40.0, rocketBase - 27.0, 1.2, -14005622);
         if (burn > 0.0) {
            double flicker = AnimatedCapes.noise(t * 20.0, 1.0);
            double flame = 8.0 + flicker * 5.0 + burn * 6.0;
            c.glow(40.0, rocketBase + 4.0, 14.0, -20400, 0.9 * burn);
            c.polygon(new double[][]{{36.5, rocketBase}, {43.5, rocketBase}, {40.0 + Math.sin(t * 30.0), rocketBase + flame}}, -30176);
            c.polygon(new double[][]{{38.0, rocketBase}, {42.0, rocketBase}, {40.0, rocketBase + flame * 0.6}}, -3920);
         }

         int arm = -7718358;
         double retract = burn > 0.0 ? Math.min(1.0, (cycle - 4.0) * 1.5) : 0.0;
         c.rect(44.0 + retract * 5.0, 60.0, 6.0 - retract * 5.0, 1.2, arm);
         c.rect(44.0 + retract * 5.0, 78.0, 6.0 - retract * 5.0, 1.2, arm);
         if (AnimatedCapes.wrap(t, 1.0) < 0.3) {
            c.glow(52.5, 37.0, 2.5, -57312, 1.0);
         }
      }
   }

   static final class Supernova implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 60.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.04 + 7.0, y * 0.035, 4);
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(
                     AnimatedCapes.lerp(
                        -16645366,
                        AnimatedCapes.lerp(-15070678, -16115142, AnimatedCapes.fbm(x * 0.05, y * 0.04 + 3.0, 3)),
                        AnimatedCapes.smoothstep(0.45, 0.85, n) * 0.8
                     ),
                     x,
                     y
                  )
               );
            }
         }

         for (int i = 0; i < 200; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               AnimatedCapes.hash(i, 3) > 0.96 ? 1.0 : 0.0,
               i % 4 == 0 ? -10048 : -2562817,
               0.12 + AnimatedCapes.hash(i, 4) * 0.5
            );
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycle = AnimatedCapes.wrap(t, 12.0);
         AnimatedCapes.stars(c, t, 30, 0.0, 128.0, -1, 3);
         if (cycle < 4.0) {
            double p = cycle / 4.0;
            double r = 6.0 + p * 8.0 + Math.sin(t * 6.0) * 0.6 * p;
            c.glow(40.0, 60.0, r * 3.0, -40912, 0.5 + p * 0.3);

            for (int y = (int)(60.0 - r - 1.0); y <= 60.0 + r + 1.0; y++) {
               for (int x = (int)(40.0 - r - 1.0); x <= 40.0 + r + 1.0; x++) {
                  double d = Math.hypot(x + 0.5 - 40.0, y + 0.5 - 60.0);
                  if (d < r) {
                     double n = AnimatedCapes.fbm(x * 0.3 + t * 0.8, y * 0.3 - t * 0.5, 3);
                     int col = AnimatedCapes.ramp(
                        n + (1.0 - d / r) * 0.35, new double[]{0.3, 0.6, 0.9}, new int[]{-6678006, AnimatedCapes.lerp(-38368, -50672, p), -8032}
                     );
                     c.blend(x, y, AnimatedCapes.alpha(col, AnimatedCapes.clamp(r - d, 0.0, 1.0)));
                  }
               }
            }
         } else if (cycle < 4.4) {
            double p = (cycle - 4.0) / 0.4;
            double flash = Math.sin(p * Math.PI);

            for (int i = 0; i < c.px.length; i++) {
               c.add(i % 80, i / 80, -1, flash * 0.6);
            }

            c.glow(40.0, 60.0, 20.0 + p * 40.0, -1, 1.5 * flash);
         } else {
            double p = (cycle - 4.4) / 7.6;
            double r = 4.0 + Math.pow(p, 0.6) * 46.0;
            double fade = 1.0 - p * 0.6;

            for (int y = 0; y < 128; y++) {
               for (int xx = 0; xx < 80; xx++) {
                  double dx = xx + 0.5 - 40.0;
                  double dy = y + 0.5 - 60.0;
                  double d = Math.hypot(dx, dy);
                  if (!(d > r + 8.0) && !(d < r * 0.3)) {
                     double a = Math.atan2(dy, dx);
                     double filaments = AnimatedCapes.fbm(Math.cos(a) * 3.0 + 11.0, Math.sin(a) * 3.0 + d * 0.05, 4);
                     double shell = Math.exp(-Math.pow((d - r * (0.8 + filaments * 0.3)) / (3.0 + r * 0.08), 2.0));
                     int col = AnimatedCapes.ramp(filaments, new double[]{0.3, 0.5, 0.7}, new int[]{-50550, -26048, -12525313});
                     c.add(xx, y, col, shell * fade * 0.9);
                     if (Math.abs(d - r) < 1.2) {
                        c.add(xx, y, -2034433, 0.35 * fade);
                     }
                  }
               }
            }

            double spin = t * 5.0;
            double pulse = 0.6 + 0.4 * Math.pow(Math.max(0.0, Math.sin(t * 20.0)), 4.0);
            c.beam(40.0, 60.0, 40.0 + Math.cos(spin) * 30.0, 60.0 + Math.sin(spin) * 30.0 * 0.4, 1.0, -7677697, 0.7 * fade);
            c.beam(40.0, 60.0, 40.0 - Math.cos(spin) * 30.0, 60.0 - Math.sin(spin) * 30.0 * 0.4, 1.0, -7677697, 0.7 * fade);
            c.glow(40.0, 60.0, 8.0, -5183233, pulse);
            c.disc(40.0, 60.0, 1.2, -1);
         }
      }
   }
}
