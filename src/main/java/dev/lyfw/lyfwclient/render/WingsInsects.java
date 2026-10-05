package dev.lyfw.lyfwclient.render;

final class WingsInsects {
   private WingsInsects() {
   }

   private static double[] lobePoint(double cx, double cy, double rx, double ry, double degrees, double angle, double at) {
      double a = Math.toRadians(degrees);
      double ux = Math.cos(angle) * rx * at;
      double uy = Math.sin(angle) * ry * at;
      return new double[]{cx + ux * Math.cos(a) - uy * Math.sin(a), cy + ux * Math.sin(a) + uy * Math.cos(a)};
   }

   private static void body(WingCanvas c, int color) {
      for (int y = 8; y < 28; y++) {
         for (int x = 0; x < 4; x++) {
            if (MoreWings.lobe(x + 0.5, y + 0.5, 1.5, 18.0, 3.0, 9.0, 0.0) <= 1.0) {
               c.set(x, y, color);
            }
         }
      }
   }

   static void blueMorpho(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double t = Math.min(MoreWings.forewing(x + 0.5, y + 0.5), MoreWings.hindwing(x + 0.5, y + 0.5));
            if (!(t > 1.0)) {
               int col;
               if (t > 0.8) {
                  col = tip;
               } else {
                  col = AnimatedCapes.lerp(base, -8722177, 0.25 + 0.25 * Math.sin(x * 0.3 + y * 0.22));
                  col = AnimatedCapes.lerp(AnimatedCapes.shade(base, 0.55F), col, AnimatedCapes.clamp((x - 2) / 14.0, 0.0, 1.0));
                  double angle = Math.atan2(y - 18, x - 2);
                  if (Math.abs(Math.sin(angle * 9.0)) < 0.07) {
                     col = AnimatedCapes.shade(col, 0.72F);
                  }
               }

               c.set(x, y, col);
            }
         }
      }

      for (int i = 0; i < 9; i++) {
         double[] p = lobePoint(27.0, 12.0, 25.0, 11.0, -12.0, Math.toRadians(-100 + 25 * i), 0.9);
         c.disc(p[0], p[1], 0.8, -722689);
      }

      for (int i = 0; i < 5; i++) {
         double[] p = lobePoint(18.0, 27.0, 15.0, 10.0, 25.0, Math.toRadians(20 + 35 * i), 0.9);
         c.disc(p[0], p[1], 0.7, -722689);
      }

      body(c, tip);
   }

   static void glasswing(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double upper = MoreWings.forewing(x + 0.5, y + 0.5);
            double lower = MoreWings.hindwing(x + 0.5, y + 0.5);
            double t = Math.min(upper, lower);
            if (!(t > 1.0)) {
               int col;
               if (t > 0.86) {
                  col = tip;
               } else {
                  double angle = Math.atan2(y - 18, x - 2);
                  col = Math.abs(Math.sin(angle * 8.0)) < 0.06
                     ? AnimatedCapes.alpha(tip, 0.85)
                     : AnimatedCapes.alpha(lower < upper ? AnimatedCapes.lerp(base, -20368, 0.25) : base, 0.16);
               }

               if (upper <= 1.0 && Math.abs(x - 44 + (y - 6) * 0.9) < 2.2 && upper > 0.45) {
                  col = -460556;
               }

               c.set(x, y, col);
            }
         }
      }

      body(c, tip);
   }

   static void atlasMoth(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double upper = MoreWings.lobe(x + 0.5, y + 0.5, 28.0, 12.0, 27.0, 12.0, -10.0);
            double lower = MoreWings.lobe(x + 0.5, y + 0.5, 19.0, 28.0, 17.0, 11.0, 25.0);
            double t = Math.min(upper, lower);
            if (!(t > 1.0) && (!(lower <= 1.0) || !(lower > 0.9) || !(Math.sin(Math.atan2(y - 28, x - 19) * 12.0) > 0.6))) {
               int col = AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.7F), t);
               if (Math.abs(t - 0.62 - 0.03 * Math.sin(x * 1.2)) < 0.05) {
                  col = tip;
               }

               if (t > 0.88) {
                  col = AnimatedCapes.lerp(tip, base, 0.35);
               }

               c.set(x, y, col);
            }
         }
      }

      double[][] windows = new double[][]{{26.0, 11.0}, {17.0, 27.0}};

      for (double[] w : windows) {
         c.polygon(new double[][]{{w[0] - 2.0, w[1] + 1.5}, {w[0] + 2.0, w[1] + 1.5}, {w[0], w[1] - 2.0}}, -725800);
      }

      c.ellipse(49.0, 7.0, 5.0, 3.4, -0.4, -1521568);
      c.disc(50.5, 6.5, 1.3, -15066598);
      c.set(50, 6, -1);
      c.line(45.0, 9.0, 53.0, 5.0, 0.5, -5223894);

      for (int i = 0; i < 6; i++) {
         double[] p = lobePoint(19.0, 28.0, 17.0, 11.0, 25.0, Math.toRadians(20 + 28 * i), 0.8);
         c.disc(p[0], p[1], 0.8, -12969456);
      }

      body(c, AnimatedCapes.shade(base, 0.6F));
      c.rim(0.7);
   }

   static void cicada(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double upper = MoreWings.lobe(x + 0.5, y + 0.5, 28.0, 11.0, 27.0, 6.5, -8.0);
            double lower = MoreWings.lobe(x + 0.5, y + 0.5, 22.0, 20.0, 19.0, 5.0, 12.0);
            double t = Math.min(upper, lower);
            if (!(t > 1.0)) {
               boolean vein = t > 0.9 || Math.abs(Math.sin((x - 2) * 0.55 + y * 0.12)) < 0.06 || Math.abs(Math.sin(Math.atan2(y - 14, x - 2) * 10.0)) < 0.05;
               int col = vein ? AnimatedCapes.alpha(tip, 0.9) : AnimatedCapes.alpha(AnimatedCapes.lerp(base, -4659008, x / 60.0), 0.2 + x / 400.0);
               c.set(x, y, col);
            }
         }
      }

      c.line(2.0, 9.0, 52.0, 5.0, 1.2, tip);
      c.line(2.0, 16.0, 38.0, 22.0, 0.8, tip);
      body(c, AnimatedCapes.shade(tip, 0.7F));
   }

   static void firefly(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double hind = MoreWings.lobe(x + 0.5, y + 0.5, 25.0, 24.0, 23.0, 8.5, 18.0);
            if (hind <= 1.0) {
               boolean vein = Math.abs(Math.sin(Math.atan2(y - 18, x - 2) * 9.0)) < 0.08 || hind > 0.9;
               c.set(x, y, vein ? -9804704 : AnimatedCapes.lerp(-2566964, -4672340, hind));
            }

            double shell = MoreWings.lobe(x + 0.5, y + 0.5, 22.0, 13.0, 21.0, 7.0, -8.0);
            if (shell <= 1.0) {
               int col = AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.6F), shell);
               if (y + 0.5 < 8.8 + (x - 22) * -0.14 || shell > 0.86) {
                  col = -1531840;
               }

               if (Math.abs(Math.sin((x + 0.5) * 0.9)) < 0.1 && shell < 0.8) {
                  col = AnimatedCapes.shade(col, 1.3F);
               }

               c.set(x, y, col);
            }
         }
      }

      for (int k = 0; k < 4; k++) {
         double x = 34 + k * 5;
         double y = 16.0 + k * 1.5;
         c.disc(x, y, 2.4, AnimatedCapes.alpha(tip, 0.5));
         c.disc(x, y, 1.3, tip);
         c.disc(x, y, 0.5, -32);
      }

      body(c, -15068144);
      c.rim(0.7);
   }

   static void wasp(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double upper = MoreWings.lobe(x + 0.5, y + 0.5, 27.0, 10.0, 26.0, 6.0, -10.0);
            double lower = MoreWings.lobe(x + 0.5, y + 0.5, 20.0, 19.0, 17.0, 4.5, 14.0);
            double t = Math.min(upper, lower);
            if (!(t > 1.0)) {
               boolean vein = t > 0.9 || Math.abs(Math.sin(Math.atan2(y - 12, x - 2) * 7.0)) < 0.06 || Math.abs(Math.sin(x * 0.4)) < 0.05 && x < 34;
               c.set(
                  x, y, vein ? AnimatedCapes.alpha(tip, 0.92) : AnimatedCapes.alpha(AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.8F), x / 56.0), 0.42)
               );
            }
         }
      }

      c.line(2.0, 8.0, 50.0, 3.0, 1.1, tip);
      c.ellipse(38.0, 5.5, 3.0, 1.3, -0.1, tip);
      body(c, -14017008);
   }

   static void scarab(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double hind = MoreWings.lobe(x + 0.5, y + 0.5, 27.0, 25.0, 24.0, 8.0, 16.0);
            if (hind <= 1.0) {
               boolean vein = Math.abs(Math.sin(Math.atan2(y - 18, x - 2) * 8.0)) < 0.08 || hind > 0.9;
               c.set(x, y, vein ? -11912662 : AnimatedCapes.lerp(-4679056, -7706040, hind));
            }

            double shell = MoreWings.lobe(x + 0.5, y + 0.5, 22.0, 14.0, 21.0, 8.0, -6.0);
            if (shell <= 1.0) {
               int col = AnimatedCapes.lerp(base, tip, 0.5 + 0.5 * Math.sin(x * 0.22 - y * 0.18));
               col = AnimatedCapes.shade(col, 1.15 - shell * 0.5);
               double highlight = Math.abs(y - 10 - (x - 20) * -0.12);
               if (highlight < 1.2 && shell < 0.85) {
                  col = AnimatedCapes.lerp(col, -1, 0.55);
               }

               if (x % 4 == 1 && y % 3 == 1 && shell < 0.85) {
                  col = AnimatedCapes.shade(col, 0.72F);
               }

               c.set(x, y, col);
            }
         }
      }

      body(c, AnimatedCapes.shade(base, 0.4F));
      c.rim(0.6);
   }

   static void manta(WingCanvas c, int base, int tip) {
      double[][] outline = new double[30][];

      for (int i = 0; i < 15; i++) {
         double s = i / 14.0;
         outline[i] = new double[]{1.0 + 53.0 * s, 5.0 + 15.0 * s * s - 5.0 * Math.sin(Math.PI * s)};
         outline[29 - i] = new double[]{1.0 + 53.0 * s, 34.0 - 14.0 * s + 6.0 * Math.sin(Math.PI * s * 0.9)};
      }

      c.polygon(outline, (xx, yx) -> {
         double s = AnimatedCapes.clamp((xx - 1) / 53.0, 0.0, 1.0);
         double top = 5.0 + 15.0 * s * s - 5.0 * Math.sin(Math.PI * s);
         double bottom = 34.0 - 14.0 * s + 6.0 * Math.sin(Math.PI * s * 0.9);
         double v = (yx - top) / Math.max(1.0, bottom - top);
         int col = AnimatedCapes.lerp(AnimatedCapes.shade(base, 1.2F), base, v);
         return v > 0.8 ? AnimatedCapes.lerp(col, tip, (v - 0.8) * 5.0) : col;
      });

      for (int k = 0; k < 22; k++) {
         double x = 6.0 + AnimatedCapes.hash(k, 1) * 40.0;
         double y = 10.0 + AnimatedCapes.hash(k, 2) * 18.0;
         if (c.filled((int)x, (int)y) && c.filled((int)x, (int)y + 3)) {
            c.disc(x, y, 0.5 + AnimatedCapes.hash(k, 3) * 0.6, AnimatedCapes.lerp(base, tip, 0.55));
         }
      }

      c.line(3.0, 7.0, 50.0, 19.0, 0.5, AnimatedCapes.shade(base, 1.5));
      c.rim(0.6);
   }

   static void flyingFish(WingCanvas c, int base, int tip) {
      int rays = 10;
      double[][] ends = new double[rays][];

      for (int r = 0; r < rays; r++) {
         double a = -0.38 + r * 0.155;
         double len = 50.0 - Math.abs(r - 3) * 3.2;
         ends[r] = new double[]{2.0 + Math.cos(a) * len, 11.0 + Math.sin(a) * len};
      }

      for (int r = 0; r < rays - 1; r++) {
         double[] a = ends[r];
         double[] b = ends[r + 1];
         double mx = (a[0] + b[0]) / 2.0;
         double my = (a[1] + b[1]) / 2.0;
         double dx = mx - 2.0;
         double dy = my - 11.0;
         double l = Math.hypot(dx, dy);
         double[] dip = new double[]{2.0 + dx / l * (l - 3.0), 11.0 + dy / l * (l - 3.0)};
         c.polygon(new double[][]{{2.0, 11.0}, a, dip, b}, (x, y) -> {
            double d = Math.hypot(x - 2, y - 11);
            return AnimatedCapes.alpha(AnimatedCapes.lerp(base, tip, (int)(d / 7.0) % 2 == 0 ? 0.15 : 0.55), 0.5);
         });
      }

      for (double[] e : ends) {
         c.line(2.0, 11.0, e[0], e[1], 0.7, AnimatedCapes.alpha(tip, 0.95));
      }

      c.disc(2.0, 11.0, 2.2, tip);
   }

   static void jellyfish(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double upper = MoreWings.forewing(x + 0.5, y + 0.5);
            double lower = MoreWings.hindwing(x + 0.5, y + 0.5);
            double t = Math.min(upper, lower);
            double frill = 0.93 + 0.07 * Math.sin(Math.atan2(y - 18, x - 10) * 26.0);
            if (!(t > frill)) {
               double angle = Math.atan2(y - 18, x - 2);
               boolean canal = Math.abs(Math.sin(angle * 6.0)) < 0.08;
               int col = AnimatedCapes.lerp(base, tip, t * t);
               double a = t > frill - 0.1 ? 0.85 : (canal ? 0.6 : 0.3 + t * 0.2);
               c.set(x, y, AnimatedCapes.alpha(t > frill - 0.1 ? AnimatedCapes.lerp(col, -1, 0.4) : col, a));
            }
         }
      }

      for (int k = 0; k < 7; k++) {
         double xx = 10 + k * 5;
         double top = 20.0 + k * 1.5;
         double px = xx;

         for (int s = 1; s <= 6; s++) {
            double ny = top + s * 2.8;
            double nx = xx + Math.sin(s * 0.9 + k) * 1.6;
            c.line(px, top + (s - 1) * 2.8, nx, ny, 0.5, AnimatedCapes.alpha(tip, 0.65 * (1.0 - s / 7.0)));
            px = nx;
         }
      }

      for (int k = 0; k < 12; k++) {
         double xx = 6.0 + AnimatedCapes.hash(k, 1) * 44.0;
         double y = 3.0 + AnimatedCapes.hash(k, 2) * 30.0;
         if (c.filled((int)xx, (int)y)) {
            c.disc(xx, y, 1.2, AnimatedCapes.alpha(-1, 0.5));
            c.set((int)xx, (int)y, -1);
         }
      }
   }

   static void spiderWeb(WingCanvas c, int base, int tip) {
      int spokes = 9;
      double[] angles = new double[spokes];
      double[] lengths = new double[spokes];

      for (int s = 0; s < spokes; s++) {
         angles[s] = -0.32 + s * 0.19;
         lengths[s] = 52.0 - Math.abs(s - 2.5) * 3.8;
         c.line(1.0, 10.0, 1.0 + Math.cos(angles[s]) * lengths[s], 10.0 + Math.sin(angles[s]) * lengths[s], 0.55, AnimatedCapes.alpha(base, 0.9));
      }

      for (double r = 5.0; r < 52.0; r += 4.2) {
         for (int s = 0; s < spokes - 1; s++) {
            double r0 = Math.min(r + s * 0.25, lengths[s]);
            double r1 = Math.min(r + (s + 1) * 0.25, lengths[s + 1]);
            if (!(r > lengths[s]) && !(r > lengths[s + 1])) {
               double x0 = 1.0 + Math.cos(angles[s]) * r0;
               double y0 = 10.0 + Math.sin(angles[s]) * r0;
               double x1 = 1.0 + Math.cos(angles[s + 1]) * r1;
               double y1 = 10.0 + Math.sin(angles[s + 1]) * r1;
               double mx = (x0 + x1) / 2.0 - Math.cos((angles[s] + angles[s + 1]) / 2.0) * 0.8;
               double my = (y0 + y1) / 2.0 - Math.sin((angles[s] + angles[s + 1]) / 2.0) * 0.8;
               c.line(x0, y0, mx, my, 0.45, AnimatedCapes.alpha(base, 0.75));
               c.line(mx, my, x1, y1, 0.45, AnimatedCapes.alpha(base, 0.75));
            }
         }
      }

      for (int k = 0; k < 14; k++) {
         int sx = (int)(AnimatedCapes.hash(k, 1) * spokes);
         double r = 8.0 + AnimatedCapes.hash(k, 2) * (lengths[sx] - 10.0);
         double x = 1.0 + Math.cos(angles[sx]) * r;
         double y = 10.0 + Math.sin(angles[sx]) * r;
         c.disc(x, y, 0.9 + AnimatedCapes.hash(k, 3) * 0.5, AnimatedCapes.alpha(tip, 0.85));
         c.set((int)(x - 0.3), (int)(y - 0.3), -1);
      }
   }
}
