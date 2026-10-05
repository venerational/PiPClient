package dev.lyfw.lyfwclient.render;

import java.util.ArrayList;
import java.util.List;

final class CapeScenesC {
   private CapeScenesC() {
   }

   static final class AutumnLeaves implements AnimatedCapes.Scene {
      private static final int[] PALETTE = new int[]{-2606566, -1013222, -737238, -5223910, -7722480};

      private static double hill(int layer, double x) {
         return 62 + layer * 10 - 8.0 * AnimatedCapes.fbm(x * 0.05 + layer * 3, layer, 3);
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5}, new int[]{-7685928, -733016});
         c.glow(66.0, 14.0, 42.0, -8032, 0.4);

         for (int layer = 0; layer < 2; layer++) {
            final int layerIndex = layer;
            c.fillBelow(x -> hill(layerIndex, x), AnimatedCapes.lerp(-4679030, -6657462, layer));

            for (int i = 0; i < 120; i++) {
               double x = AnimatedCapes.hash(i, layer + 30) * 80.0;
               c.disc(
                  x,
                  hill(layer, x) + 1.5 + AnimatedCapes.hash(i, layer + 31) * 10.0,
                  1.4 + AnimatedCapes.hash(i, layer + 32),
                  AnimatedCapes.alpha(AnimatedCapes.lerp(PALETTE[i % 3], -5201776, 0.4 - layer * 0.3), 0.85)
               );
            }
         }

         c.fillBelow(x -> 88.0 + 3.0 * Math.sin(x * 0.08), -7701958);

         for (int y = 86; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (c.get(x, y) == -7701958) {
                  c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-6649280, -10855896, AnimatedCapes.fbm(x * 0.3, y * 0.5, 3)), x, y));
               }
            }
         }

         c.polygon(
            new double[][]{{37.0, 88.0}, {43.0, 88.0}, {66.0, 128.0}, {12.0, 128.0}},
            (xx, y) -> AnimatedCapes.lerp(-2574198, -6653350, AnimatedCapes.fbm(xx * 0.2, y * 0.2, 3))
         );

         for (int i = 0; i < 280; i++) {
            double xx = AnimatedCapes.hash(i, 40) * 80.0;
            double y = 90.0 + AnimatedCapes.hash(i, 41) * 38.0;
            boolean onPath = Math.abs(xx - 40.0) < 3.0 + (y - 88.0) * 0.68;
            if (!onPath || AnimatedCapes.hash(i, 42) > 0.65) {
               c.ellipse(
                  xx, y, 1.1 + AnimatedCapes.hash(i, 43), 0.7, AnimatedCapes.hash(i, 44) * 3.0, PALETTE[(int)(AnimatedCapes.hash(i, 45) * PALETTE.length)]
               );
            }
         }

         for (int k = 0; k < 2; k++) {
            double bx = 62 + k * 10;
            double top = 26 + k * 6;
            double bottom = 100 + k * 6;
            c.rect(bx, top, 3.0, bottom - top, -1514278);
            c.rect(bx + 2.0, top, 1.0, bottom - top, -4672342);

            for (int m = 0; m < 9; m++) {
               c.rect(
                  bx + AnimatedCapes.hash(m, k) * 2.0, top + AnimatedCapes.hash(m, k + 1) * (bottom - top), 1.5 + AnimatedCapes.hash(m, k + 2), 1.0, -14015456
               );
            }

            c.line(bx + 1.0, top + 12.0, bx - 6.0, top + 4.0, 0.6, -9807280);
            c.line(bx + 2.0, top + 20.0, bx + 8.0, top + 12.0, 0.6, -9807280);
         }

         c.rect(46.0, 95.0, 13.0, 1.5, -10864096);
         c.rect(46.0, 91.0, 13.0, 1.2, -9811414);
         c.rect(47.0, 96.0, 1.0, 4.0, -14018032);
         c.rect(57.0, 96.0, 1.0, 4.0, -14018032);
         c.polygon(
            new double[][]{{6.0, 118.0}, {14.0, 118.0}, {12.0, 70.0}, {10.0, 40.0}, {8.0, 70.0}},
            (xx, y) -> AnimatedCapes.lerp(-12966888, -14018546, (xx - 6) / 8.0)
         );
         c.line(10.0, 60.0, 26.0, 46.0, 2.0, -12966888);
         c.line(10.0, 52.0, 0.0, 40.0, 1.6, -12966888);
         c.line(10.0, 44.0, 20.0, 26.0, 1.4, -12966888);
         double[][] clusters = new double[][]{
            {4.0, 30.0, 9.0},
            {18.0, 22.0, 9.0},
            {30.0, 36.0, 8.0},
            {12.0, 44.0, 8.0},
            {26.0, 52.0, 7.0},
            {0.0, 56.0, 7.0},
            {62.0, 28.0, 7.0},
            {74.0, 22.0, 7.0},
            {70.0, 40.0, 6.0}
         };

         for (int pass = 0; pass < 2; pass++) {
            for (int k = 0; k < clusters.length; k++) {
               double[] cl = clusters[k];
               boolean birch = k >= 6;

               for (int ix = 0; ix < 44; ix++) {
                  double angle = AnimatedCapes.hash(ix, k * 7 + 50) * Math.PI * 2.0;
                  double dist = Math.sqrt(AnimatedCapes.hash(ix, k * 7 + 51)) * cl[2];
                  double lx = cl[0] + Math.cos(angle) * dist;
                  double ly = cl[1] + Math.sin(angle) * dist * 0.8;
                  double light = AnimatedCapes.clamp(0.5 - Math.sin(angle) * dist / cl[2] * 0.5 + Math.cos(angle) * 0.2, 0.0, 1.0);
                  if (pass == 0) {
                     c.disc(lx, ly + 1.0, 2.2, AnimatedCapes.shade(birch ? -5207520 : -7722480, 0.8 + light * 0.3));
                  } else if (AnimatedCapes.hash(ix, k + 60) > 0.35) {
                     int base = birch ? -733120 : PALETTE[(int)(AnimatedCapes.hash(ix, k + 61) * 3.0)];
                     c.disc(lx, ly, 1.0 + AnimatedCapes.hash(ix, k + 62) * 1.2, AnimatedCapes.lerp(AnimatedCapes.shade(base, 0.8), -5968, light * 0.45));
                  }
               }
            }
         }
      }

      private static void mapleLeaf(AnimatedCapes.Canvas c, double x, double y, double size, double rotation, double flip, int color) {
         double[][] points = new double[10][];
         double cos = Math.cos(rotation);
         double sin = Math.sin(rotation);
         double squash = Math.max(0.2, Math.abs(flip));

         for (int k = 0; k < 10; k++) {
            double a = k * Math.PI / 5.0 - (Math.PI / 2);
            double r = k % 2 == 0 ? size : size * 0.48;
            double lx = Math.cos(a) * r * squash;
            double ly = Math.sin(a) * r;
            points[k] = new double[]{x + lx * cos - ly * sin, y + lx * sin + ly * cos};
         }

         c.polygon(points, flip < 0.0 ? AnimatedCapes.shade(color, 0.75) : color);
         c.line(x, y, x - Math.sin(rotation) * -size * 0.9, y + Math.cos(rotation) * size * 0.9, 0.4, AnimatedCapes.shade(color, 0.55));
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 80; x++) {
               double ray = Math.pow(AnimatedCapes.noise((x - y * 0.5) * 0.07 + t * 0.05, 3.3), 3.0);
               c.add(x, y, -8032, ray * 0.25 * (1.0 - y / 110.0));
            }
         }

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double spot = AnimatedCapes.noise(x * 0.15 + t * 0.2, y * 0.25);
               if (spot > 0.62) {
                  c.add(x, y, -10096, (spot - 0.62) * 0.8);
               }
            }
         }

         double wind = 4.0 + 4.0 * Math.sin(t * 0.35);

         for (int i = 0; i < 34; i++) {
            double fall = 8.0 + AnimatedCapes.hash(i, 1) * 6.0;
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 2) * 148.0 + t * fall, 148.0) - 10.0;
            double xx = AnimatedCapes.wrap(AnimatedCapes.hash(i, 3) * 110.0 + t * wind + 6.0 * Math.sin(t * 1.1 + i * 1.3), 110.0) - 15.0;
            mapleLeaf(
               c,
               xx,
               y,
               2.0 + AnimatedCapes.hash(i, 4) * 1.5,
               t * (1.0 + AnimatedCapes.hash(i, 5) * 2.0) + i,
               Math.cos(t * (1.5 + AnimatedCapes.hash(i, 6)) + i),
               PALETTE[i % PALETTE.length]
            );
         }

         double gust = AnimatedCapes.wrap(t, 8.0);
         if (gust < 3.0) {
            double p = gust / 3.0;

            for (int k = 0; k < 8; k++) {
               double a = t * 4.0 + k * 0.8;
               double r = 2.0 + p * 8.0;
               mapleLeaf(c, 30.0 + Math.cos(a) * r, 118.0 - p * 14.0 - k * 1.2 + Math.sin(a) * r * 0.3, 1.6, a, Math.sin(a * 2.0), PALETTE[k % PALETTE.length]);
            }
         }
      }
   }

   static final class DragonsHoard implements AnimatedCapes.Scene {
      private static double pile(double x) {
         return 84.0 + Math.pow(Math.abs(x - 38.0) / 40.0, 1.6) * 36.0;
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.07, y * 0.05, 5);
               double ridges = Math.abs(Math.sin(n * 12.0 + y * 0.05));
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(AnimatedCapes.lerp(-15068648, -12965334, n), 0.7 + ridges * 0.35), x, y));
            }
         }

         for (int side = 0; side < 2; side++) {
            double tx = side == 0 ? 8.0 : 72.0;
            c.glow(tx, 40.0, 34.0, -28624, 0.35);
            c.rect(tx - 1.0, 40.0, 2.0, 10.0, -12966892);
            c.rect(tx - 3.0, 48.0, 6.0, 1.5, -14013906);
         }

         c.fillBelow(CapeScenesC.DragonsHoard::pile, -5207510);

         for (int y = 80; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= pile(x + 0.5)) {
                  c.set(
                     x,
                     y,
                     AnimatedCapes.dither(AnimatedCapes.lerp(-1525696, -8761328, (y - pile(x)) / 50.0 + AnimatedCapes.fbm(x * 0.3, y * 0.3, 2) * 0.3), x, y)
                  );
               }
            }
         }

         for (int i = 0; i < 240; i++) {
            double xx = AnimatedCapes.hash(i, 10) * 80.0;
            double y = pile(xx) + 1.0 + Math.pow(AnimatedCapes.hash(i, 11), 1.5) * 40.0;
            if (y < 128.0) {
               c.ellipse(xx, y, 1.5, 0.8, 0.0, AnimatedCapes.lerp(-8080, -5211624, AnimatedCapes.hash(i, 12)));
               c.ellipse(xx, y + 0.2, 1.1, 0.45, 0.0, AnimatedCapes.lerp(-3936, -2580432, AnimatedCapes.hash(i, 13)));
            }
         }

         int[] gems = new int[]{-2088902, -14630816, -12951297, -5226272};

         for (int ix = 0; ix < 14; ix++) {
            double xx = 6.0 + AnimatedCapes.hash(ix, 20) * 68.0;
            double y = pile(xx) + 3.0 + AnimatedCapes.hash(ix, 21) * 22.0;
            double s = 1.2 + AnimatedCapes.hash(ix, 22);
            c.polygon(new double[][]{{xx, y - s}, {xx + s, y}, {xx, y + s}, {xx - s, y}}, gems[ix % gems.length]);
            c.rect(xx - 0.3, y - s * 0.6, 0.6, 0.6, -1);
         }

         c.polygon(new double[][]{{30.0, 104.0}, {36.0, 104.0}, {37.0, 97.0}, {33.0, 99.0}, {29.0, 97.0}}, -12224);
         c.rect(29.5, 103.0, 7.0, 2.0, -2056160);
         c.disc(33.0, 101.0, 0.8, -2088902);
         c.polygon(new double[][]{{14.0, 112.0}, {20.0, 112.0}, {19.0, 104.0}, {15.0, 104.0}}, -2580432);
         c.rect(16.5, 112.0, 1.0, 4.0, -2580432);
         c.rect(14.5, 116.0, 5.0, 1.0, -2580432);
         c.line(52.0, 96.0, 60.0, 72.0, 1.0, -3617576);
         c.line(49.0, 93.0, 55.0, 95.0, 1.2, -9811414);
         c.disc(51.0, 97.0, 0.9, -12224);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int side = 0; side < 2; side++) {
            double tx = side == 0 ? 8.0 : 72.0;

            for (int k = 0; k < 4; k++) {
               double h = 4.0 + AnimatedCapes.noise(t * 6.0 + k + side * 5, k) * 4.0;
               c.polygon(
                  new double[][]{{tx - 2.0 + k, 40.0}, {tx - 1.0 + k, 40.0}, {tx - 1.5 + k + Math.sin(t * 8.0 + k) * 0.8, 40.0 - h}},
                  k % 2 == 0 ? -30176 : -12208
               );
            }

            c.glow(tx, 37.0, 10.0, -20416, 0.4 + 0.2 * AnimatedCapes.noise(t * 5.0, side));
         }

         double breath = 0.5 + 0.5 * Math.sin(t * 0.9);
         double heaveY = breath * 1.2;

         for (int s = 0; s <= 70; s++) {
            double f = s / 70.0;
            double x = 6.0 + f * 56.0;
            double y = 92.0 - Math.sin(f * Math.PI) * 13.0 - heaveY * Math.sin(f * Math.PI) + (f < 0.2 ? (0.2 - f) * 20.0 : 0.0);
            double thick = (2.0 + f * 7.5) * (1.0 + 0.06 * breath * Math.sin(f * Math.PI));
            c.disc(x, y, thick, AnimatedCapes.shade(-8775142, 0.8 + 0.25 * Math.sin(s * 1.9)));
            c.disc(x, y + thick * 0.45, thick * 0.55, AnimatedCapes.lerp(-5207478, -7710166, 0.5 + 0.5 * Math.sin(s * 2.4)));
            if (s % 6 == 3 && f > 0.1) {
               c.polygon(new double[][]{{x - 1.4, y - thick + 0.6}, {x + 1.4, y - thick + 0.6}, {x + 0.4, y - thick - 2.5 - f * 2.0}}, -11923952);
            }
         }

         double lift = heaveY * 0.8;
         double[][] wing = new double[][]{
            {52.0, 78.0 - lift}, {42.0, 55.0 - lift}, {34.0, 52.0 - lift}, {27.0, 59.0 - lift}, {20.0, 72.0 - lift}, {30.0, 69.0 - lift}, {40.0, 75.0 - lift}
         };
         c.polygon(wing, (xx, yx) -> AnimatedCapes.lerp(-9824232, -12973554, (yx - 52) / 26.0));

         for (int b = 1; b <= 4; b++) {
            c.line(wing[0][0], wing[0][1], wing[b][0], wing[b][1], 0.9, -6672336);
         }

         c.line(wing[1][0], wing[1][1], wing[2][0], wing[2][1], 1.1, -6672336);
         c.polygon(new double[][]{{42.0, 55.0 - lift}, {43.5, 51.0 - lift}, {44.0, 56.0 - lift}}, -2570080);
         double hx = 66.0;
         double hy = 82.0 - heaveY * 0.4;
         c.line(hx - 6.0, hy - 2.0, hx - 12.0, hy - 11.0, 1.2, -2570080);
         c.line(hx - 3.0, hy - 3.0, hx - 6.0, hy - 13.0, 1.0, -2570080);
         c.ellipse(hx, hy, 7.5, 4.8, 0.1, -7725024);
         c.polygon(new double[][]{{hx + 3.0, hy - 3.0}, {hx + 13.0, hy + 1.0}, {hx + 12.0, hy + 4.0}, {hx + 2.0, hy + 4.0}}, -7725024);
         c.line(hx + 2.0, hy + 3.5, hx + 12.0, hy + 3.5, 0.5, -12973558);
         c.line(hx - 3.0, hy - 3.5, hx + 6.0, hy - 2.2, 1.2, -10874864);
         c.disc(hx + 11.0, hy + 1.2, 0.6, -14023162);
         double peek = AnimatedCapes.wrap(t, 11.0);
         double open = peek > 7.0 && peek < 9.5 ? Math.sin((peek - 7.0) / 2.5 * Math.PI) : 0.0;
         if (open > 0.05) {
            c.ellipse(hx + 1.0, hy - 1.0, 2.2, 1.2 * open, 0.0, -12256);
            c.rect(hx + 0.7, hy - 1.0 - open, 0.6, open * 2.0, -15070720);
            c.glow(hx + 1.0, hy - 1.0, 5.0, -16352, 0.5 * open);
         } else {
            c.line(hx - 1.0, hy - 1.0, hx + 3.0, hy - 0.5, 0.6, -14023162);
         }

         double exhale = Math.cos(t * 0.9);
         if (exhale < 0.0) {
            for (int k = 0; k < 4; k++) {
               double life = AnimatedCapes.wrap(t * 0.6 + k / 4.0, 1.0);
               c.disc(hx + 12.0 + life * 8.0, hy + 1.0 - life * 6.0, 0.8 + life * 2.2, AnimatedCapes.alpha(-6649216, 0.35 * (1.0 - life) * -exhale));
            }
         }

         for (int i = 0; i < 12; i++) {
            int tick = (int)Math.floor(t * 1.5 + AnimatedCapes.hash(i, 40) * 5.0);
            double x = AnimatedCapes.hash(i, tick + 41) * 80.0;
            double y = pile(x) + 2.0 + AnimatedCapes.hash(i, tick + 42) * 30.0;
            if (y < 128.0) {
               c.star(x, y, 2.0, -2880, Math.sin(AnimatedCapes.wrap(t * 1.5 + AnimatedCapes.hash(i, 40) * 5.0, 1.0) * Math.PI));
            }
         }
      }
   }

   static final class EnchantedBook implements AnimatedCapes.Scene {
      private static final int[] RUNES = new int[]{186, 341, 495, 313, 214, 410, 170, 469};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-15594984, -14806496});

         for (int shelf = 0; shelf < 4; shelf++) {
            double sy = 6 + shelf * 22;
            c.rect(0.0, sy + 18.0, 80.0, 2.5, -12966890);
            double x = 1.0;

            for (int b = 0; x < 80.0; b++) {
               double w = 2.5 + AnimatedCapes.hash(b, shelf) * 3.0;
               double h = 12.0 + AnimatedCapes.hash(b, shelf + 9) * 6.0;
               int col = AnimatedCapes.lerp(
                  new int[]{-11920870, -15058390, -15062454, -11912678, -12969414}[b % 5], -15726070, 0.35 + AnimatedCapes.hash(b, shelf + 3) * 0.3
               );
               c.rect(x, sy + 18.0 - h, w - 0.5, h, col);
               c.rect(x, sy + 18.0 - h + 2.0, w - 0.5, 0.6, AnimatedCapes.alpha(-2576304, 0.35));
               x += w;
            }
         }

         c.vignette(0.55);
         c.polygon(
            new double[][]{{26.0, 128.0}, {54.0, 128.0}, {50.0, 112.0}, {30.0, 112.0}}, (x, y) -> AnimatedCapes.lerp(-12966892, -14806518, (x - 26) / 28.0)
         );
         c.polygon(new double[][]{{40.0, 106.0}, {10.0, 102.0}, {8.0, 118.0}, {40.0, 123.0}, {72.0, 118.0}, {70.0, 102.0}}, -12969398);

         for (int side = -1; side <= 1; side += 2) {
            int s = side;
            c.polygon(
               new double[][]{{40.0, 104.0}, {40 + side * 27, 99.0}, {40 + side * 29, 115.0}, {40.0, 120.0}},
               (x, y) -> AnimatedCapes.lerp(-727352, -3624816, Math.abs(x - 40) / 30.0 * 0.4 + (40 - Math.abs(x - 40)) / 80.0)
            );

            for (int line = 0; line < 6; line++) {
               double ly = 103.0 + line * 2.4;

               for (double lx = 4.0; lx < 24.0; lx += 3.0 + AnimatedCapes.hash(line, (int)lx + side) * 2.0) {
                  c.line(40.0 + s * lx, ly - lx * 0.15 + 0.5, 40.0 + s * (lx + 1.8), ly - (lx + 1.8) * 0.15 + 0.5, 0.5, AnimatedCapes.alpha(-12965312, 0.6));
               }
            }
         }

         c.line(40.0, 104.0, 40.0, 120.0, 0.8, -7701920);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double pulse = 0.5 + 0.5 * Math.sin(t * 1.4);
         c.glow(40.0, 108.0, 40.0, -6663937, 0.3 + 0.15 * pulse);
         c.glow(40.0, 106.0, 16.0, -10428161, 0.3 + 0.2 * pulse);
         double turn = AnimatedCapes.wrap(t, 6.0) / 1.4;
         if (turn < 1.0) {
            double tipX = 40.0 + Math.cos(turn * Math.PI) * 27.0;
            double lift = Math.sin(turn * Math.PI);
            c.polygon(
               new double[][]{{40.0, 104.0}, {tipX, 99.0 - lift * 12.0}, {tipX + (turn < 0.5 ? 2 : -2) * lift, 115.0 - lift * 10.0}, {40.0, 120.0}},
               AnimatedCapes.lerp(-727352, -4677504, Math.abs(Math.cos(turn * Math.PI)) < 0.3 ? 0.5 : 0.1)
            );
         }

         for (int i = 0; i < 26; i++) {
            double life = AnimatedCapes.wrap(t * 0.18 + i / 26.0, 1.0);
            double angle = life * 9.0 + i * 0.7;
            double radius = 6.0 + life * 22.0;
            double depth = Math.sin(angle);
            double x = 40.0 + Math.cos(angle) * radius;
            double y = 104.0 - life * 92.0;
            double fade = Math.sin(life * Math.PI) * (0.55 + 0.45 * depth);
            int rune = RUNES[i % RUNES.length];
            int col = AnimatedCapes.lerp(-10428161, -4161281, life);

            for (int py = 0; py < 3; py++) {
               for (int px = 0; px < 3; px++) {
                  if ((rune >> 8 - (py * 3 + px) & 1) != 0) {
                     c.add((int)x + px - 1, (int)y + py - 1, col, fade);
                  }
               }
            }

            if (depth > 0.6) {
               c.glow(x, y, 4.0, col, fade * 0.4);
            }
         }

         double spin = t * 0.6;

         for (int k = 0; k < 12; k++) {
            double a = spin + k * Math.PI / 6.0;
            double x = 40.0 + Math.cos(a) * 26.0;
            double y = 36.0 + Math.sin(a) * 7.0;
            int rune = RUNES[k * 3 % RUNES.length];
            double depth = 0.5 + 0.5 * Math.sin(a);

            for (int py = 0; py < 3; py++) {
               for (int pxx = 0; pxx < 3; pxx++) {
                  if ((rune >> 8 - (py * 3 + pxx) & 1) != 0) {
                     c.add((int)x + pxx - 1, (int)y + py - 1, -12176, 0.3 + 0.6 * depth);
                  }
               }
            }
         }

         c.ellipse(40.0, 36.0, 26.0, 7.0, 0.0, AnimatedCapes.alpha(-12176, 0.08));

         for (int i = 0; i < 16; i++) {
            double life = AnimatedCapes.wrap(t * 0.8 + AnimatedCapes.hash(i, 50), 1.0);
            int wave = (int)Math.floor(t * 0.8 + AnimatedCapes.hash(i, 50));
            c.star(14.0 + AnimatedCapes.hash(i, wave + 51) * 52.0, 70.0 + AnimatedCapes.hash(i, wave + 52) * 40.0, 1.0, -1, Math.sin(life * Math.PI) * 0.8);
         }
      }
   }

   static final class Jellyfish implements AnimatedCapes.Scene {
      private static final double[] HUES = new double[]{185.0, 305.0, 255.0, 35.0};
      private static final double[] SIZES = new double[]{13.0, 9.0, 11.0, 7.0};
      private static final double[] LANES = new double[]{28.0, 60.0, 46.0, 14.0};
      private static final double[] SPEEDS = new double[]{3.2, 4.4, 3.6, 5.0};

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16109496, -16509918, -16710130});

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double shaft = (x - 28 - y * 0.25) / 10.0;
               c.add(x, y, -11886400, 0.12 * Math.exp(-shaft * shaft) * (1.0 - y / 128.0));
            }
         }

         c.fillBelow(x -> 118.0 + 4.0 * AnimatedCapes.fbm(x * 0.1, 2.0, 3), -16643568);

         for (int k = 0; k < 5; k++) {
            double ax = 6 + k * 17;

            for (int s = 0; s < 7; s++) {
               double a = (-Math.PI / 2) + (s - 3) * 0.22;
               c.line(ax, 120.0, ax + Math.cos(a) * 6.0, 120.0 + Math.sin(a) * 6.0, 0.6, -16378852);
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int i = 0; i < 60; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) * 128.0 + t * (1.5 + AnimatedCapes.hash(i, 2) * 2.0), 128.0);
            double x = AnimatedCapes.hash(i, 3) * 80.0 + Math.sin(t * 0.3 + i) * 2.0;
            c.add((int)x, (int)y, -5187352, 0.2 + AnimatedCapes.hash(i, 4) * 0.15);
         }

         for (int k = 0; k < HUES.length; k++) {
            double size = SIZES[k];
            double contract = Math.pow(0.5 + 0.5 * Math.sin(t * 1.6 + k * 1.3), 2.0);
            double cycle = 128.0 + size * 5.0 + 20.0;
            double y = 128.0 + size * 3.0 - AnimatedCapes.wrap(AnimatedCapes.hash(k, 1) * cycle + t * SPEEDS[k] + contract * 1.5, cycle);
            double x = LANES[k] + Math.sin(t * 0.25 + k * 2) * 6.0;
            double rx = size * (1.0 - 0.18 * contract);
            double ry = size * 0.8 * (1.0 + 0.15 * contract);
            int col = AnimatedCapes.hsv(HUES[k], 0.6, 1.0);
            int rim = AnimatedCapes.hsv(HUES[k] + 25.0, 0.35, 1.0);
            c.glow(x, y, size * 2.6, col, 0.3 + 0.2 * contract);

            for (int j = 0; j < 9; j++) {
               double tx = x - rx + j * (2.0 * rx / 8.0);
               double px = tx;
               double py = y + 0.5;

               for (int s = 1; s <= 12; s++) {
                  double nx = tx + Math.sin(s * 0.6 - t * 2.5 + j + k) * s * 0.35;
                  double ny = y + 0.5 + s * size * 0.28;
                  c.line(px, py, nx, ny, 0.5, AnimatedCapes.alpha(col, 0.5 * (1.0 - s / 12.0)));
                  px = nx;
                  py = ny;
               }
            }

            for (int j = 0; j < 4; j++) {
               double offset = (j - 1.5) * size * 0.2;
               double px = x + offset;
               double py = y + 0.5;

               for (int s = 1; s <= 10; s++) {
                  double nx = x + offset + Math.sin(s * 0.8 - t * 1.8 + j) * 1.5 * (s / 10.0 + 0.3);
                  double ny = y + 0.5 + s * size * 0.18;
                  c.line(px, py, nx, ny, 1.4 * (1.0 - s / 12.0), AnimatedCapes.alpha(AnimatedCapes.lerp(col, -1, 0.3), 0.55));
                  if (s % 2 == 0) {
                     c.disc(nx + 0.8, ny, 0.7, AnimatedCapes.alpha(rim, 0.4));
                  }

                  px = nx;
                  py = ny;
               }
            }

            for (int py = (int)Math.floor(y - ry); py <= (int)Math.ceil(y + 1.0); py++) {
               for (int px = (int)Math.floor(x - rx); px <= (int)Math.ceil(x + rx); px++) {
                  double u = (px + 0.5 - x) / rx;
                  double v = (y - (py + 0.5)) / ry;
                  double dist = Math.sqrt(u * u + v * v);
                  if (!(dist > 1.0) && !(v < -0.1)) {
                     double edge = AnimatedCapes.smoothstep(0.55, 1.0, dist);
                     double vein = Math.pow(Math.abs(Math.cos(Math.atan2(v, u) * 4.0)), 30.0) * 0.3 * (1.0 - dist);
                     double body = (0.42 + edge * 0.5 + (v < 0.15 ? 0.25 : 0.0)) * (0.8 + 0.2 * contract);
                     c.blend(px, py, AnimatedCapes.alpha(AnimatedCapes.lerp(col, -1, edge * 0.4 + vein), Math.min(0.95, body)));
                  }
               }
            }

            for (int g = 0; g < 4; g++) {
               double a = Math.PI * (0.2 + g * 0.2);
               c.ellipse(x + Math.cos(a) * rx * 0.45, y - Math.sin(a) * ry * 0.45, 1.3, 0.8, -a, AnimatedCapes.alpha(rim, 0.6));
            }

            for (int d = 0; d < 10; d++) {
               double a = Math.PI * d / 9.0;
               c.add((int)(x + Math.cos(a) * rx), (int)(y - Math.sin(a) * ry * 0.12 + 0.5), rim, 0.8 * (0.5 + 0.5 * Math.sin(t * 4.0 + d)));
            }
         }

         for (int i = 0; i < 20; i++) {
            int tick = (int)Math.floor(t * 3.0 + AnimatedCapes.hash(i, 30) * 3.0);
            c.add(
               (int)(AnimatedCapes.hash(i, tick + 31) * 80.0),
               (int)(AnimatedCapes.hash(i, tick + 32) * 128.0),
               -7667728,
               0.6 * Math.sin(AnimatedCapes.wrap(t * 3.0 + AnimatedCapes.hash(i, 30) * 3.0, 1.0) * Math.PI)
            );
         }
      }
   }

   static final class LavaLamp implements AnimatedCapes.Scene {
      private static final double CX = 40.0;

      private static double halfWidth(double y) {
         return 9.0 + 8.0 * Math.sin((y - 20.0) / 80.0 * Math.PI * 0.85);
      }

      private static int metal(double x, double left, double width) {
         double u = (x - left) / width;
         double shine = Math.exp(-Math.pow((u - 0.3) * 7.0, 2.0));
         return AnimatedCapes.lerp(AnimatedCapes.lerp(-12961212, -6645080, Math.sin(AnimatedCapes.clamp(u, 0.0, 1.0) * Math.PI)), -1, shine * 0.7);
      }

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 1.0}, new int[]{-15069666, -14019552});

         for (int y = 0; y < 116; y++) {
            for (int x = 0; x < 80; x++) {
               if ((x + y) % 12 == 0 || (x - y + 120) % 12 == 0) {
                  c.blend(x, y, AnimatedCapes.alpha(-12967888, 0.5));
               }

               if ((x + y) % 12 == 6 && (x - y + 120) % 12 == 6) {
                  c.blend(x, y, AnimatedCapes.alpha(-9815478, 0.6));
               }
            }
         }

         for (int y = 116; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double grain = 0.5 + 0.5 * Math.sin(y * 1.3 + AnimatedCapes.fbm(x * 0.05, y * 0.5, 3) * 6.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(AnimatedCapes.lerp(-11916772, -14018546, (y - 116) / 12.0), -10864090, grain * 0.3), x, y));
            }
         }

         c.rect(0.0, 116.0, 80.0, 1.0, -9811406);
         c.ellipse(40.0, 118.0, 22.0, 2.2, 0.0, AnimatedCapes.alpha(-16777216, 0.55));
         c.polygon(new double[][]{{29.0, 100.0}, {51.0, 100.0}, {58.0, 117.0}, {22.0, 117.0}}, (xx, yx) -> metal(xx, 22.0, 36.0));
         c.rect(22.0, 116.0, 36.0, 2.0, -14013904);
         c.rect(28.0, 99.0, 24.0, 2.0, -10855834);
         c.polygon(new double[][]{{32.0, 8.0}, {48.0, 8.0}, {51.0, 20.0}, {29.0, 20.0}}, (xx, yx) -> metal(xx, 29.0, 22.0));
         c.rect(33.0, 6.0, 14.0, 2.0, -10855834);
         c.rect(29.0, 19.0, 22.0, 1.5, -14013904);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] blobs = new double[7][];

         for (int k = 0; k < blobs.length; k++) {
            double speed = 0.18 + AnimatedCapes.hash(k, 1) * 0.14;
            double phase = AnimatedCapes.hash(k, 2) * 6.28;
            double y = 60.0 - Math.sin(t * speed + phase) * 34.0;
            double x = 40.0 + Math.sin(t * 0.37 + k * 1.7) * halfWidth(y) * 0.35;
            double stretch = 1.0 + Math.abs(Math.cos(t * speed + phase)) * 0.4;
            blobs[k] = new double[]{x, y, 4.5 + AnimatedCapes.hash(k, 3) * 3.5, stretch};
         }

         c.glow(40.0, 62.0, 52.0, -46560, 0.22);

         for (int y = 20; y < 100; y++) {
            double hw = halfWidth(y);

            for (int x = (int)(40.0 - hw - 1.0); x <= (int)(40.0 + hw + 1.0); x++) {
               double cover = AnimatedCapes.clamp(hw + 0.5 - Math.abs(x + 0.5 - 40.0), 0.0, 1.0);
               if (!(cover <= 0.0)) {
                  double field = 60.0 / ((y - 101.0) * (y - 101.0) + 4.0) + 14.0 / ((y - 19.0) * (y - 19.0) + 6.0);

                  for (double[] b : blobs) {
                     double dx = x + 0.5 - b[0];
                     double dy = (y + 0.5 - b[1]) / b[3];
                     field += b[2] * b[2] / (dx * dx + dy * dy + 0.5);
                  }

                  double u = (x + 0.5 - 40.0) / hw;
                  double light = 0.65 + 0.35 * Math.cos((u + 0.35) * 1.4);
                  int col = field > 1.0
                     ? AnimatedCapes.shade(
                        AnimatedCapes.ramp(AnimatedCapes.clamp((field - 1.0) / 3.0, 0.0, 1.0), new double[]{0.0, 0.5, 1.0}, new int[]{-4183536, -34278, -12208}),
                        light
                     )
                     : AnimatedCapes.shade(AnimatedCapes.lerp(-11924934, -6280614, field * 0.6), light * 0.9);
                  c.blend(x, y, AnimatedCapes.alpha(col, cover));
                  if (Math.abs(u + 0.55) < 0.09) {
                     c.add(x, y, -1, 0.22 * cover);
                  }

                  if (Math.abs(Math.abs(u) - 0.95) < 0.06) {
                     c.add(x, y, -16176, 0.18);
                  }
               }
            }
         }

         for (int i = 0; i < 10; i++) {
            double life = AnimatedCapes.wrap(t * 0.15 + AnimatedCapes.hash(i, 9), 1.0);
            double y = 98.0 - life * 76.0;
            double xx = 40.0 + (AnimatedCapes.hash(i, 10) - 0.5) * halfWidth(y) * 1.4 + Math.sin(life * 9.0 + i) * 0.8;
            c.add((int)xx, (int)y, -20320, 0.35 * Math.sin(life * Math.PI));
         }
      }
   }

   static final class MoonWolf implements AnimatedCapes.Scene {
      private boolean[] open;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 1.0}, new int[]{-16446946, -15457718, -16116688});

         for (int i = 0; i < 100; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 128.0, 0.0, -2037505, 0.1 + AnimatedCapes.hash(i, 3) * 0.35);
         }

         c.glow(40.0, 46.0, 50.0, -7692080, 0.45);

         for (int y = 18; y < 76; y++) {
            for (int x = 12; x < 68; x++) {
               double d = Math.hypot(x + 0.5 - 40.0, y + 0.5 - 46.0);
               double cover = AnimatedCapes.clamp(27.5 - d, 0.0, 1.0);
               if (!(cover <= 0.0)) {
                  double maria = AnimatedCapes.smoothstep(0.5, 0.7, AnimatedCapes.fbm(x * 0.09, y * 0.09 + 3.0, 4));
                  int col = AnimatedCapes.lerp(-1816, -4672336, maria * 0.7);
                  col = AnimatedCapes.shade(col, 1.0 - Math.pow(d / 27.0, 4.0) * 0.25);
                  c.blend(x, y, AnimatedCapes.alpha(col, cover));
               }
            }
         }

         for (int i = 0; i < 16; i++) {
            double a = AnimatedCapes.hash(i, 10) * Math.PI * 2.0;
            double r = Math.sqrt(AnimatedCapes.hash(i, 11)) * 23.0;
            double size = 0.8 + AnimatedCapes.hash(i, 12) * 2.0;
            c.ring(40.0 + Math.cos(a) * r, 46.0 + Math.sin(a) * r, size, 0.6, AnimatedCapes.alpha(-6646128, 0.5));
            c.disc(40.0 + Math.cos(a) * r + 0.4, 46.0 + Math.sin(a) * r + 0.4, size * 0.7, AnimatedCapes.alpha(-1, 0.2));
         }

         int[] before = (int[])c.px.clone();

         for (int i = 0; i < 22; i++) {
            double xx = AnimatedCapes.hash(i, 20) * 80.0;
            AnimatedCapes.pine(c, xx, 130.0, 12.0 + AnimatedCapes.hash(i, 21) * 16.0, -16513004);
         }

         c.polygon(
            new double[][]{{24.0, 128.0}, {28.0, 106.0}, {34.0, 99.0}, {58.0, 98.0}, {64.0, 102.0}, {70.0, 112.0}, {82.0, 114.0}, {82.0, 128.0}},
            (xx, y) -> AnimatedCapes.lerp(-16118244, -16579316, (y - 98) / 30.0)
         );
         c.line(34.0, 99.0, 58.0, 98.0, 0.6, AnimatedCapes.alpha(-9799008, 0.6));
         this.open = AnimatedCapes.unchanged(c, before);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 10; y < 90; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05 - t * 0.08, y * 0.12, 4);
               double band = Math.exp(-Math.pow((y - 56 - Math.sin(x * 0.05) * 6.0) / 12.0, 2.0)) + Math.exp(-Math.pow((y - 24) / 6, 2.0)) * 0.7;
               double a = AnimatedCapes.smoothstep(0.45, 0.7, n) * band;
               if (a > 0.01 && AnimatedCapes.isOpen(this.open, x, y)) {
                  c.blend(x, y, AnimatedCapes.alpha(AnimatedCapes.lerp(-15064000, -11904390, n), a * 0.85));
               }
            }
         }

         AnimatedCapes.stars(c, t, 30, 0.0, 100.0, -1, 3, this.open);
         double howl = AnimatedCapes.wrap(t, 8.0);
         double raise = howl < 5.0 ? Math.sin(Math.min(1.0, howl / 1.2) * Math.PI / 2.0) * (howl > 4.0 ? 5.0 - howl : 1.0) : 0.0;
         int wolf = -16645366;
         double tailSway = Math.sin(t * 1.5) * 1.2;

         for (int s = 0; s <= 8; s++) {
            double f = s / 8.0;
            c.disc(35.0 - f * 6.0, 88.0 + f * 7.0 + f * f * tailSway, 2.2 - f * 0.9 + Math.sin(f * Math.PI) * 0.6, wolf);
         }

         c.ellipse(44.0, 90.0, 9.0, 4.0, -0.05, wolf);
         c.disc(37.0, 90.0, 4.0, wolf);
         c.disc(51.0, 88.5, 4.4, wolf);
         c.line(36.0, 92.0, 34.0, 98.0, 1.9, wolf);
         c.line(40.0, 92.0, 40.0, 98.0, 1.5, wolf);
         c.line(50.0, 91.0, 51.0, 98.0, 1.7, wolf);
         c.line(53.0, 91.0, 54.5, 98.0, 1.4, wolf);
         double headX = 55.5 + raise * 1.5;
         double headY = 82.0 - raise * 5.0;
         c.polygon(new double[][]{{47.0, 86.0}, {54.0, 85.5}, {headX + 1.5, headY + 2.0}, {headX - 2.8, headY - 0.5}}, wolf);
         c.disc(headX, headY, 2.9, wolf);
         double snoutAngle = -0.15 - raise * 0.95;
         c.polygon(
            new double[][]{
               {headX + 1.0, headY - 1.8}, {headX + 1.5, headY + 1.8}, {headX + 1.5 + Math.cos(snoutAngle) * 6.5, headY + Math.sin(snoutAngle) * 6.5 + 0.6}
            },
            wolf
         );
         c.polygon(new double[][]{{headX - 2.2, headY - 1.4}, {headX - 0.4, headY - 2.4}, {headX - 2.8, headY - 5.8}}, wolf);
         c.polygon(new double[][]{{headX - 0.8, headY - 2.2}, {headX + 0.8, headY - 2.4}, {headX - 0.6, headY - 5.4}}, wolf);

         for (int k = 0; k < 8; k++) {
            double fx = 38.0 + k * 1.8;
            c.polygon(new double[][]{{fx - 1.0, 86.5}, {fx + 1.0, 86.5}, {fx + 0.3 + Math.sin(t * 3.0 + k) * 0.4, 84.6}}, wolf);
         }

         c.polygon(new double[][]{{48.0, 90.0}, {54.0, 90.0}, {51.0, 94.5}}, wolf);

         for (int k = 0; k < 2; k++) {
            double bx = AnimatedCapes.wrap(t * (9 + k * 3) + k * 50, 120.0) - 20.0;
            double by = 30 + k * 18 + Math.sin(t * 2.0 + k) * 5.0;
            double flap = Math.sin(t * 14.0 + k);
            c.polygon(new double[][]{{bx, by}, {bx - 4.0, by - 2.0 * flap}, {bx - 2.5, by + 0.5}, {bx - 1.0, by + 0.3}}, -16645366);
            c.polygon(new double[][]{{bx, by}, {bx + 4.0, by - 2.0 * flap}, {bx + 2.5, by + 0.5}, {bx + 1.0, by + 0.3}}, -16645366);
            c.disc(bx, by, 0.9, -16645366);
         }
      }
   }

   static final class NeonCity implements AnimatedCapes.Scene {
      private static final int STREET = 110;
      private final List<double[]> windows = new ArrayList<>();

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.55, 0.85}, new int[]{-16120288, -14020544, -9819558});
         double[][] layers = new double[][]{{70.0, 6.0, 18.0, -1.5069136E7}, {92.0, 9.0, 32.0, -1.5594974E7}, {110.0, 14.0, 52.0, -1.6251377E7}};

         for (int l = 0; l < layers.length; l++) {
            double x = -3.0;

            for (int b = 0; x < 80.0; b++) {
               double w = layers[l][1] + AnimatedCapes.hash(b, l) * layers[l][1];
               double h = layers[l][2] * (0.5 + AnimatedCapes.hash(b, l + 5) * 0.8);
               double top = layers[l][0] - h;
               c.rect(x, top, w, layers[l][0] - top + (l == 2 ? 0 : 20), (int)layers[l][3]);
               if (l <= 0) {
                  for (int i = 0; i < 6; i++) {
                     c.rect(x + AnimatedCapes.hash(i, b) * w, top + AnimatedCapes.hash(b, i) * h, 1.0, 1.0, AnimatedCapes.alpha(-10096, 0.5));
                  }
               } else {
                  for (double wy = top + 3.0; wy < layers[l][0] - 3.0; wy += 4.0) {
                     for (double wx = x + 1.5; wx < x + w - 2.0; wx += 3.0) {
                        this.windows.add(new double[]{wx, wy, l, b * 131 + wx * 7.0 + wy});
                        c.rect(wx, wy, 1.5, 2.0, -15066582);
                     }
                  }
               }

               if (l == 2 && AnimatedCapes.hash(b, 77) > 0.5) {
                  c.line(x + w / 2.0, top, x + w / 2.0, top - 6.0, 0.5, (int)layers[l][3]);
               }

               x += w + (l == 2 ? 1 : 0);
            }
         }

         for (int y = 110; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-15857128, -16448502, (y - 110) / 18.0), x, y));
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int k = 0; k < 2; k++) {
            double a = Math.sin(t * 0.4 + k * 2) * 0.5 - (Math.PI / 2);
            double bx = 20 + k * 40;
            c.polygon(
               new double[][]{
                  {bx, 70.0},
                  {bx + Math.cos(a - 0.05) * 90.0, 70.0 + Math.sin(a - 0.05) * 90.0},
                  {bx + Math.cos(a + 0.05) * 90.0, 70.0 + Math.sin(a + 0.05) * 90.0}
               },
               AnimatedCapes.alpha(-5193473, 0.08)
            );
         }

         int bucket = (int)Math.floor(t / 2.5);

         for (double[] w : this.windows) {
            int seed = (int)w[3];
            double lit = AnimatedCapes.hash(seed, bucket) > 0.55 ? 1.0 : 0.0;
            if (lit > 0.0) {
               int col = AnimatedCapes.hash(seed, 3) > 0.7 ? -7675649 : (AnimatedCapes.hash(seed, 4) > 0.8 ? -30000 : -10096);
               c.rect(w[0], w[1], 1.5, 2.0, AnimatedCapes.alpha(col, w[2] == 1.0 ? 0.55 : 0.9));
            }
         }

         boolean buzz = AnimatedCapes.hash((int)Math.floor(t * 10.0), 2) > 0.9;
         c.rect(9.0, 64.0, 5.0, 30.0, -15070694);

         for (int k = 0; k < 5; k++) {
            c.beam(11.5, 67.0 + k * 5.5, 11.5 + (k % 2 == 0 ? 1 : -1), 69.5 + k * 5.5, 0.8, -48976, buzz && k == 3 ? 0.15 : 0.9);
         }

         c.glow(11.5, 79.0, 18.0, -48976, 0.25);
         c.ring(62.0, 72.0, 5.0, 1.0, AnimatedCapes.alpha(-12521217, 0.9));
         c.glow(62.0, 72.0, 14.0, -12521217, 0.25 + 0.1 * Math.sin(t * 3.0));
         c.rect(40.0, 80.0, 14.0, 7.0, -15594982);
         c.rect(40.5, 80.5, 13.0, 6.0, AnimatedCapes.hsv(t * 40.0, 0.7, 0.8));
         c.rect(40.5 + AnimatedCapes.wrap(t * 6.0, 13.0), 80.5, 1.0, 6.0, AnimatedCapes.alpha(-1, 0.6));

         for (int k = 0; k < 6; k++) {
            boolean right = k % 2 == 0;
            double speed = 18.0 + AnimatedCapes.hash(k, 1) * 16.0;
            double x = right
               ? AnimatedCapes.wrap(t * speed + AnimatedCapes.hash(k, 2) * 120.0, 120.0) - 20.0
               : 100.0 - AnimatedCapes.wrap(t * speed + AnimatedCapes.hash(k, 2) * 120.0, 120.0);
            double y = 30 + k * 9 + Math.sin(t + k) * 1.5;
            int col = right ? -44976 : -32;
            c.beam(x, y, x + (right ? -9 : 9), y, 0.5, col, 0.7);
            c.rect(x - 1.5, y - 0.8, 3.0, 1.6, -14671830);
         }

         if (AnimatedCapes.wrap(t, 1.4) < 0.3) {
            c.glow(70.0, 44.0, 3.0, -57312, 0.9);
         }

         for (int y = 111; y < 128; y++) {
            int src = 110 - (y - 110) * 2;

            for (int x = 0; x < 80; x++) {
               int sx = x + (int)Math.round(Math.sin(y * 1.3 + t * 3.0) * 1.2);
               c.add(x, y, c.get(sx, src), 0.38 * (1.0 - (y - 110) / 20.0));
            }
         }

         for (int i = 0; i < 90; i++) {
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 60) * 128.0 + t * (60.0 + AnimatedCapes.hash(i, 61) * 30.0), 128.0);
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 62) * 80.0 - t * 8.0, 80.0);
            c.line(x, y, x - 0.6, y + 3.0, 0.4, AnimatedCapes.alpha(-5195552, 0.3));
         }
      }
   }

   static final class NetherPortal implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int col = AnimatedCapes.lerp(-11920870, -8770006, AnimatedCapes.hash(x / 4, y / 4));
               if (AnimatedCapes.hash(x, y) > 0.8) {
                  col = AnimatedCapes.shade(col, 0.72);
               }

               c.set(x, y, AnimatedCapes.shade(col, 0.45 + 0.5 * AnimatedCapes.fbm(x * 0.04, y * 0.04, 3)));
            }
         }

         double[][] glowstone = new double[][]{{12.0, 4.0}, {62.0, 6.0}, {38.0, 2.0}};

         for (int k = 0; k < glowstone.length; k++) {
            for (int by = 0; by < 4; by++) {
               for (int bx = -3; bx <= 3; bx++) {
                  if (Math.abs(bx) + by < 4.0 + AnimatedCapes.hash(bx, k) * 2.0) {
                     c.rect(
                        glowstone[k][0] + bx * 3,
                        glowstone[k][1] + by * 3 - 3.0,
                        3.0,
                        3.0,
                        AnimatedCapes.lerp(-8054, -5211590, AnimatedCapes.hash(bx * 7 + k, by))
                     );
                  }
               }
            }

            c.glow(glowstone[k][0], glowstone[k][1], 22.0, -20416, 0.28);
         }

         for (int by = 16; by <= 104; by += 8) {
            for (int bxx = 8; bxx <= 64; bxx += 8) {
               if (bxx == 8 || bxx == 64 || by == 16 || by == 104) {
                  for (int py = 0; py < 8; py++) {
                     for (int px = 0; px < 8; px++) {
                        int col = AnimatedCapes.lerp(-15988970, -14806480, AnimatedCapes.hash(bxx + px, by + py) * 0.7);
                        if (AnimatedCapes.hash(bxx * 3 + px, by * 5 + py) > 0.9) {
                           col = -12965286;
                        }

                        if (px == 0 || py == 0) {
                           col = AnimatedCapes.shade(col, 1.35);
                        } else if (px == 7 || py == 7) {
                           col = AnimatedCapes.shade(col, 0.6);
                        }

                        c.set(bxx + px, by + py, col);
                     }
                  }
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         c.glow(40.0, 64.0, 54.0, -7720705, 0.22 + 0.05 * Math.sin(t * 2.0));

         for (int y = 24; y < 104; y += 2) {
            for (int x = 16; x < 64; x += 2) {
               double dx = x + 1 - 40;
               double dy = y + 1 - 64;
               double d = Math.hypot(dx, dy * 0.7);
               double swirl = Math.atan2(dy, dx) + d * 0.06 - t * 0.9;
               double n = AnimatedCapes.fbm(Math.cos(swirl) * d * 0.07 + 10.0, Math.sin(swirl) * d * 0.07 + t * 0.15, 4);
               double v = n + 0.12 * Math.sin(d * 0.35 - t * 3.0);
               int col = AnimatedCapes.ramp(v, new double[]{0.3, 0.5, 0.62, 0.76}, new int[]{-14022576, -9824064, -6272784, -1525505});
               c.rect(x, y, 2.0, 2.0, AnimatedCapes.alpha(col, 0.94));
            }
         }

         for (int i = 0; i < 34; i++) {
            double life = AnimatedCapes.wrap(t * 0.45 + AnimatedCapes.hash(i, 20), 1.0);
            int wave = (int)Math.floor(t * 0.45 + AnimatedCapes.hash(i, 20));
            double ox = 18.0 + AnimatedCapes.hash(i, wave + 21) * 44.0;
            double oy = 26.0 + AnimatedCapes.hash(i, wave + 22) * 76.0;
            double angle = AnimatedCapes.hash(i, wave + 23) * Math.PI * 2.0;
            double x = ox + Math.cos(angle) * life * 14.0;
            double y = oy + Math.sin(angle) * life * 14.0 - life * 6.0;
            c.rect(Math.floor(x), Math.floor(y), 1.0, 1.0, AnimatedCapes.alpha(AnimatedCapes.lerp(-2055937, -7720705, life), Math.sin(life * Math.PI)));
         }

         for (int y = 118; y < 128; y += 2) {
            for (int x = 0; x < 20.0 - (y - 118) * 0.3; x += 2) {
               double n = AnimatedCapes.fbm(x * 0.2 + t * 0.3, y * 0.3 - t * 0.1, 3);
               c.rect(x, y, 2.0, 2.0, AnimatedCapes.ramp(n, new double[]{0.3, 0.6, 0.8}, new int[]{-5230582, -34278, -10160}));
            }
         }

         c.glow(8.0, 122.0, 20.0, -40944, 0.3 + 0.08 * AnimatedCapes.noise(t * 3.0, 1.0));

         for (int cy = 104; cy < 122; cy += 2) {
            for (int cx = 70; cx < 80; cx += 2) {
               double heat = AnimatedCapes.fbm(cx * 0.25, cy * 0.2 + t * 3.0, 3) - (122 - cy) / 22.0 - Math.abs(cx - 75) / 14.0;
               if (heat > 0.18) {
                  c.rect(cx, cy, 2.0, 2.0, AnimatedCapes.ramp(heat, new double[]{0.18, 0.3, 0.45}, new int[]{-4183542, -30182, -8080}));
               }
            }
         }

         c.glow(75.0, 116.0, 14.0, -36832, 0.3 + 0.1 * AnimatedCapes.noise(t * 5.0, 2.0));
      }
   }

   static final class PocketWatch implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 66.0;
      private static final double R = 30.0;
      private static final double WX = 40.0;
      private static final double WY = 77.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.15, y * 0.15, 3);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(AnimatedCapes.lerp(-16111062, -15449028, n), (x + y & 1) == 0 ? 0.96 : 1.0), x, y));
            }
         }

         c.vignette(0.6);

         for (int k = 0; k < 13; k++) {
            double s = k / 12.0;
            double px = 40.0 + Math.sin(s * 2.6) * 14.0 * s;
            double py = 31.0 - s * 38.0;
            double angle = Math.atan2(-38.0, Math.cos(s * 2.6) * 2.6 * 14.0 * s + Math.sin(s * 2.6) * 14.0);
            c.ellipse(px + 1.5, py + 2.0, 2.4, k % 2 == 0 ? 1.4 : 0.6, angle, AnimatedCapes.alpha(-16777216, 0.35));
            c.ellipse(px, py, 2.4, k % 2 == 0 ? 1.4 : 0.6, angle, k % 2 == 0 ? -2576304 : -5734352);
            if (k % 2 == 0) {
               c.ellipse(px, py, 1.3, 0.5, angle, -15978964);
            }
         }

         c.disc(43.0, 70.0, 32.0, AnimatedCapes.alpha(-16777216, 0.45));
         c.ring(40.0, 27.5, 2.8, 1.4, -2576304);
         c.polygon(new double[][]{{36.5, 29.5}, {43.5, 29.5}, {43.5, 37.0}, {36.5, 37.0}}, (x, y) -> (x & 1) == 0 ? -5207494 : -1521552);

         for (int y = 35; y <= 97.0; y++) {
            for (int x = 9; x <= 71.0; x++) {
               double d = Math.hypot(x + 0.5 - 40.0, y + 0.5 - 66.0);
               double a = Math.atan2(y + 0.5 - 66.0, x + 0.5 - 40.0);
               double cover = AnimatedCapes.clamp(30.5 - d, 0.0, 1.0);
               if (!(cover <= 0.0)) {
                  int col;
                  if (d > 26.0) {
                     col = AnimatedCapes.lerp(-9811432, -6000, 0.5 + 0.5 * Math.cos(a + 2.3));
                     if (Math.abs(d - 28.0) < 0.4) {
                        col = AnimatedCapes.shade(col, 0.8);
                     }
                  } else if (d > 25.0) {
                     col = -10862574;
                  } else {
                     col = AnimatedCapes.lerp(-2330, -2044248, d / 25.0);
                     col = AnimatedCapes.shade(col, 0.97 + 0.03 * Math.sin(a * 36.0 + d * 0.9));
                  }

                  c.blend(x, y, AnimatedCapes.alpha(col, cover));
               }
            }
         }

         for (int kx = 0; kx < 60; kx++) {
            double a = kx * Math.PI / 30.0;
            double inner = kx % 5 == 0 ? 20.0 : 22.5;
            double width = kx % 15 == 0 ? 1.6 : (kx % 5 == 0 ? 0.9 : 0.4);
            c.line(40.0 + Math.sin(a) * inner, 66.0 - Math.cos(a) * inner, 40.0 + Math.sin(a) * 24.0, 66.0 - Math.cos(a) * 24.0, width, -14016464);
         }

         for (int kx = 0; kx < 4; kx++) {
            double a = kx * Math.PI / 2.0;
            double px = 40.0 + Math.sin(a) * 17.0;
            double py = 66.0 - Math.cos(a) * 17.0;
            c.polygon(new double[][]{{px, py - 1.8}, {px + 1.2, py}, {px, py + 1.8}, {px - 1.2, py}}, -15062438);
         }

         c.disc(40.0, 77.0, 9.5, -15068144);
      }

      private static void hand(AnimatedCapes.Canvas c, double angle, double length, double tail, double width, int color) {
         double sx = Math.sin(angle);
         double cy = -Math.cos(angle);
         double px = Math.cos(angle) * width;
         double py = Math.sin(angle) * width;
         c.polygon(
            new double[][]{
               {40.0 - sx * tail, 66.0 - cy * tail},
               {40.0 + px + sx * length * 0.3, 66.0 + py + cy * length * 0.3},
               {40.0 + sx * length, 66.0 + cy * length},
               {40.0 - px + sx * length * 0.3, 66.0 - py + cy * length * 0.3}
            },
            color
         );
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double big = t * 1.1;
         double small = -big * 10.0 / 7.0 + 0.2;
         double balance = Math.sin(t * 9.0) * 2.4;

         for (int y = 68; y <= 86.0; y++) {
            for (int x = 31; x <= 49.0; x++) {
               double wd = Math.hypot(x + 0.5 - 40.0, y + 0.5 - 77.0);
               if (!(wd > 9.0)) {
                  int col = 0;
                  double gd = Math.hypot(x + 0.5 - 37.0, y + 0.5 - 79.0);
                  double ga = Math.atan2(y + 0.5 - 79.0, x + 0.5 - 37.0) - big;
                  double gEdge = 5.0 + (Math.cos(ga * 10.0) > 0.1 ? 1.2 : 0.0);
                  if (gd < gEdge && (gd > 4.0 || gd < 1.2 || Math.abs(Math.sin(ga * 2.0)) < 0.25)) {
                     col = AnimatedCapes.lerp(-4681670, -995200, 0.5 + 0.5 * Math.cos(ga + big + 2.3));
                  }

                  double hd = Math.hypot(x + 0.5 - 44.5, y + 0.5 - 82.0);
                  double ha = Math.atan2(y + 0.5 - 82.0, x + 0.5 - 44.5) - small;
                  double hEdge = 3.2 + (Math.cos(ha * 7.0) > 0.1 ? 1.0 : 0.0);
                  if (hd < hEdge && (hd > 2.4 || hd < 0.9 || Math.abs(Math.sin(ha * 1.5)) < 0.3)) {
                     col = AnimatedCapes.lerp(-6653392, -2047888, 0.5 + 0.5 * Math.cos(ha + small + 2.3));
                  }

                  double bd = Math.hypot(x + 0.5 - 42.0, y + 0.5 - 73.0);
                  double ba = Math.atan2(y + 0.5 - 73.0, x + 0.5 - 42.0) - balance;
                  if (bd > 3.3 && bd < 4.3 || bd < 3.3 && Math.abs(Math.sin(ba * 1.5)) < 0.12) {
                     col = -2565920;
                  }

                  if (col != 0) {
                     c.blend(x, y, col);
                  }
               }
            }
         }

         for (int s = 0; s < 18; s++) {
            double a = s * 0.7 + balance * 0.3;
            double r = 0.4 + s * 0.14;
            c.add((int)(42.0 + Math.cos(a) * r), (int)(73.0 + Math.sin(a) * r), -7692096, 0.4);
         }

         c.disc(37.0, 79.0, 0.9, -2088896);
         c.disc(44.5, 82.0, 0.7, -2088896);
         c.disc(42.0, 73.0, 0.7, -2088896);
         c.ring(40.0, 77.0, 9.3, 1.3, -3628976);
         double hour = t * 0.012 + 2.0;
         double minute = t * 0.144 + 0.6;
         double second = (Math.floor(t) + AnimatedCapes.smoothstep(0.0, 0.12, t - Math.floor(t))) * Math.PI / 30.0;
         hand(c, hour + 0.02, 14.0, 3.0, 1.8, AnimatedCapes.alpha(-16777216, 0.25));
         hand(c, hour, 14.0, 3.0, 1.7, -15458224);
         hand(c, minute, 21.0, 3.5, 1.2, -15458224);
         double sx = Math.sin(second);
         double sy = -Math.cos(second);
         c.line(40.0 - sx * 6.0, 66.0 - sy * 6.0, 40.0 + sx * 23.0, 66.0 + sy * 23.0, 0.55, -4186080);
         c.disc(40.0 - sx * 4.5, 66.0 - sy * 4.5, 1.1, -4186080);
         c.disc(40.0, 66.0, 1.8, -2576304);
         c.disc(40.0, 66.0, 0.7, -12965360);
         c.ellipse(30.0, 52.0, 13.0, 4.0, -0.6, AnimatedCapes.alpha(-1, 0.12 + 0.03 * Math.sin(t * 0.5)));
         double glint = t * 0.4;
         c.star(40.0 + Math.cos(glint) * 28.0, 66.0 + Math.sin(glint) * 28.0, 2.0, -1, 0.7);

         for (int i = 0; i < 14; i++) {
            double xx = AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) * 80.0 + t * (0.6 + AnimatedCapes.hash(i, 2)), 80.0);
            double y = AnimatedCapes.wrap(AnimatedCapes.hash(i, 3) * 128.0 + Math.sin(t * 0.3 + i) * 4.0 - t * 0.4, 128.0);
            c.add((int)xx, (int)y, -3904, 0.2 + 0.2 * Math.sin(t * 2.0 + i));
         }
      }
   }

   static final class RingedPlanet implements AnimatedCapes.Scene {
      private static final double PX = 40.0;
      private static final double PY = 64.0;
      private static final double PR = 22.0;
      private static final double TILT = -0.35;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.03 + 40.0, y * 0.03, 4);
               c.set(
                  x,
                  y,
                  AnimatedCapes.dither(
                     AnimatedCapes.lerp(
                        -16645366,
                        AnimatedCapes.lerp(-15070678, -16113616, AnimatedCapes.fbm(x * 0.05, y * 0.05 + 7.0, 3)),
                        AnimatedCapes.smoothstep(0.5, 0.85, n) * 0.8
                     ),
                     x,
                     y
                  )
               );
            }
         }

         for (int i = 0; i < 170; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 128.0,
               AnimatedCapes.hash(i, 4) > 0.96 ? 2.0 : 0.0,
               -986881,
               0.12 + AnimatedCapes.hash(i, 3) * 0.5
            );
         }
      }

      private static void ring(AnimatedCapes.Canvas c, boolean front) {
         double cos = Math.cos(-0.35);
         double sin = Math.sin(-0.35);

         for (int y = 30; y < 100; y++) {
            for (int x = 0; x < 80; x++) {
               double dx = x + 0.5 - 40.0;
               double dy = y + 0.5 - 64.0;
               double u = dx * cos + dy * sin;
               double v = (-dx * sin + dy * cos) / 0.26;
               if (v > 0.0 == front) {
                  double r = Math.hypot(u, v);
                  if (!(r < 28.0) && !(r > 42.0)) {
                     double band = 0.55 + 0.45 * Math.sin(r * 2.1) * Math.sin(r * 0.7 + 1.0);
                     if (r > 34.5 && r < 35.8) {
                        band = 0.08;
                     }

                     int col = AnimatedCapes.lerp(-7701910, -991040, band);
                     double a = band * 0.85 * AnimatedCapes.smoothstep(28.0, 30.0, r) * (1.0 - AnimatedCapes.smoothstep(40.0, 42.0, r));
                     if (!front && Math.hypot(dx, dy) < 23.0 && u > 0.0) {
                        col = AnimatedCapes.shade(col, 0.35);
                     }

                     c.blend(x, y, AnimatedCapes.alpha(col, a));
                  }
               }
            }
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] moons = new double[2][];

         for (int k = 0; k < 2; k++) {
            double a = t * (0.35 - k * 0.12) + k * 2.5;
            double orbit = 48 + k * 8;
            double mx = 40.0 + Math.cos(a) * orbit * Math.cos(-0.14999999999999997) - Math.sin(a) * orbit * 0.2 * Math.sin(-0.14999999999999997);
            double my = 64.0 + Math.cos(a) * orbit * Math.sin(-0.14999999999999997) + Math.sin(a) * orbit * 0.2 * Math.cos(-0.14999999999999997);
            moons[k] = new double[]{mx, my, Math.sin(a), k == 0 ? 2.6 : 1.8};
         }

         for (double[] m : moons) {
            if (m[2] < 0.0) {
               this.moon(c, m);
            }
         }

         ring(c, false);
         c.glow(40.0, 64.0, 30.0, -2051968, 0.25);
         double spotLon = AnimatedCapes.wrap(t * 0.2, Math.PI * 2) - Math.PI;

         for (int y = 41; y <= 87.0; y++) {
            for (int x = 17; x <= 63.0; x++) {
               double nx = (x + 0.5 - 40.0) / 22.0;
               double ny = (y + 0.5 - 64.0) / 22.0;
               double d2 = nx * nx + ny * ny;
               if (!(d2 >= 1.0)) {
                  double nz = Math.sqrt(1.0 - d2);
                  double lat = Math.asin(ny);
                  double lon = Math.atan2(nx, nz) + t * 0.15;
                  double flow = AnimatedCapes.fbm(lon * 1.5 + 20.0, lat * 6.0, 3);
                  double bands = Math.sin(lat * 9.0 + flow * 2.5) * 0.5 + 0.5;
                  int col = AnimatedCapes.ramp(bands, new double[]{0.0, 0.35, 0.65, 1.0}, new int[]{-7710150, -2049912, -3634598, -729936});
                  double dl = Math.atan2(Math.sin(Math.atan2(nx, nz) - spotLon), Math.cos(Math.atan2(nx, nz) - spotLon));
                  double spot = Math.pow(dl / 0.3, 2.0) + Math.pow((lat - 0.35) / 0.1, 2.0);
                  if (spot < 1.0 && nz > 0.05) {
                     col = AnimatedCapes.lerp(col, -4173782, (1.0 - spot) * 0.9);
                  }

                  double light = AnimatedCapes.clamp(-nx * 0.6 - ny * 0.3 + nz * 0.74, 0.0, 1.0);
                  col = AnimatedCapes.shade(col, 0.08 + 1.0 * light);
                  double ringShadow = Math.abs(ny + nx * 0.36 - 0.12);
                  if (ringShadow < 0.05) {
                     col = AnimatedCapes.shade(col, 0.6);
                  }

                  c.set(x, y, AnimatedCapes.dither(col, x, y));
                  c.add(x, y, -8016, Math.pow(1.0 - nz, 3.0) * 0.4 * light);
               }
            }
         }

         ring(c, true);

         for (double[] mx : moons) {
            if (mx[2] >= 0.0) {
               this.moon(c, mx);
            }
         }

         AnimatedCapes.stars(c, t, 30, 0.0, 128.0, -1, 9);
      }

      private void moon(AnimatedCapes.Canvas c, double[] m) {
         c.disc(m[0], m[1], m[3], -7697772);
         c.disc(m[0] - m[3] * 0.35, m[1] - m[3] * 0.2, m[3] * 0.75, -2565920);
         c.disc(m[0] + m[3] * 0.5, m[1] + m[3] * 0.2, m[3] * 0.6, AnimatedCapes.alpha(-15724520, 0.7));
      }
   }
}
