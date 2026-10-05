package dev.lyfw.lyfwclient.render;

final class WingsCrafted {
   private WingsCrafted() {
   }

   private static boolean heartAt(double x, double y, double cx, double cy, double size, double angle) {
      double dx = (x - cx) / size;
      double dy = (y - cy) / size;
      double u = dx * Math.cos(angle) + dy * Math.sin(angle);
      double v = -(-dx * Math.sin(angle) + dy * Math.cos(angle)) * 1.1 + 0.25;
      double q = u * u + v * v - 1.0;
      return q * q * q - u * u * v * v * v < 0.0;
   }

   static void heart(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            boolean big = heartAt(x + 0.5, y + 0.5, 32.0, 15.0, 15.0, -0.35);
            boolean small = heartAt(x + 0.5, y + 0.5, 15.0, 30.0, 8.5, 0.25);
            if (big || small) {
               double cx = big ? 32.0 : 15.0;
               double cy = big ? 15.0 : 30.0;
               double size = big ? 15.0 : 8.5;
               double d = Math.hypot(x + 0.5 - cx + size * 0.3, y + 0.5 - cy + size * 0.3) / (size * 1.6);
               c.set(x, y, AnimatedCapes.lerp(AnimatedCapes.lerp(base, tip, 0.35), AnimatedCapes.shade(base, 0.8F), AnimatedCapes.clamp(d, 0.0, 1.0)));
            }
         }
      }

      c.ellipse(24.0, 7.0, 4.0, 2.0, -0.6, AnimatedCapes.alpha(-1, 0.65));
      c.ellipse(11.0, 26.0, 2.0, 1.0, -0.4, AnimatedCapes.alpha(-1, 0.6));
      c.line(1.0, 14.0, 18.0, 14.0, 1.2, AnimatedCapes.shade(base, 0.7F));
      c.rim(0.62);
   }

   static void kite(WingCanvas c, int base, int tip) {
      double[] a = new double[]{2.0, 14.0};
      double[] b = new double[]{24.0, 1.0};
      double[] cc = new double[]{54.0, 11.0};
      double[] d = new double[]{22.0, 32.0};
      double[] mid = new double[]{22.0, 13.0};
      int[][] tris = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}};
      double[][] corners = new double[][]{a, b, cc, d};
      int[] colors = new int[]{base, tip, AnimatedCapes.lerp(base, -1, 0.35), AnimatedCapes.lerp(tip, -1, 0.35)};

      for (int k = 0; k < 4; k++) {
         int col = colors[k];
         c.polygon(new double[][]{mid, corners[tris[k][0]], corners[tris[k][1]]}, (x, y) -> AnimatedCapes.shade(col, 0.9 + 0.1 * Math.sin(x * 0.4)));
      }

      c.line(a[0], a[1], cc[0], cc[1], 1.0, -9811414);
      c.line(b[0], b[1], d[0], d[1], 1.0, -9811414);

      for (int k = 0; k < 4; k++) {
         double[] p = corners[k];
         double[] q = corners[(k + 1) % 4];

         for (double s = 0.05; s < 1.0; s += 0.1) {
            c.set((int)(p[0] + (q[0] - p[0]) * s), (int)(p[1] + (q[1] - p[1]) * s), -724764);
         }
      }

      double px = d[0];
      double py = d[1];

      for (int s = 1; s <= 4; s++) {
         double nx = d[0] + s * 6;
         double ny = d[1] + Math.sin(s * 1.4) * 2.0 + s * 0.6;
         c.line(px, py, nx, ny, 0.5, -12961222);
         c.polygon(new double[][]{{nx - 1.6, ny - 1.2}, {nx + 1.6, ny + 1.2}, {nx + 1.6, ny - 1.2}, {nx - 1.6, ny + 1.2}}, s % 2 == 0 ? base : tip);
         px = nx;
         py = ny;
      }

      c.rim(0.7);
   }

   static void paperPlane(WingCanvas c, int base, int tip) {
      double[][] top = new double[][]{{1.0, 7.0}, {54.0, 17.0}, {3.0, 16.0}};
      double[][] under = new double[][]{{3.0, 16.0}, {54.0, 17.0}, {5.0, 25.0}};
      c.polygon(top, (x, y) -> {
         int col = AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.9F), x / 60.0);
         return Math.abs(Math.sin((y - x * 0.19) * 1.1)) < 0.08 ? AnimatedCapes.lerp(col, tip, 0.6) : col;
      });
      c.polygon(under, (x, y) -> {
         int col = AnimatedCapes.shade(AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.8F), x / 60.0), 0.86F);
         return Math.abs(Math.sin((y + x * 0.02) * 1.1)) < 0.08 ? AnimatedCapes.lerp(col, tip, 0.5) : col;
      });
      c.line(3.0, 16.0, 54.0, 17.0, 0.6, AnimatedCapes.shade(base, 0.62F));
      MoreWings.lineOnShape(c, 8.0, 8.0, 9.0, 23.0, 0.5, -2069910);
      c.rim(0.65);
   }

   static void patchwork(WingCanvas c, int base, int tip) {
      MoreWings.silhouette(c, 1.0, 1.05);
      c.each((x, y, p) -> {
         int row = y / 7;
         int col = (x + row % 2 * 4) / 8;
         double kind = AnimatedCapes.hash(col, row + 40);
         int fabric = AnimatedCapes.lerp(base, tip, AnimatedCapes.hash(col, row));
         fabric = AnimatedCapes.lerp(fabric, -1, AnimatedCapes.hash(row, col) * 0.3);
         int lx = (x + row % 2 * 4) % 8;
         int ly = y % 7;
         if (kind < 0.25) {
            fabric = lx % 3 == 0 ? AnimatedCapes.shade(fabric, 0.75) : fabric;
         } else if (kind < 0.5) {
            fabric = (lx - 3) * (lx - 3) + (ly - 3) * (ly - 3) < 3 ? AnimatedCapes.lerp(fabric, -1, 0.6) : fabric;
         } else if (kind < 0.7) {
            fabric = (lx / 2 + ly / 2) % 2 == 0 ? AnimatedCapes.shade(fabric, 0.8F) : fabric;
         }

         if ((lx == 0 || ly == 0) && (x + y) % 2 == 0) {
            fabric = -724764;
         }

         return fabric;
      });
      c.rim(0.6);
   }

   static void goldFiligree(WingCanvas c, int base, int tip) {
      WingCanvas mask = new WingCanvas();
      MoreWings.silhouette(mask, 1.0, 1.0);
      MoreWings.birdWing(
         c, (layer, index, along, across) -> !(Math.abs(across) > 0.72) && !(along > 0.93) ? 0 : AnimatedCapes.lerp(base, tip, along * 0.5), 0, 1.0, 1.0
      );

      for (int y = 3; y < 40; y += 6) {
         for (int x = 4 + y / 6 % 2 * 3; x < 56; x += 6) {
            if (mask.filled(x, y) && mask.filled(x + 2, y + 2) && mask.filled(x - 2, y - 2)) {
               double px = x;
               double py = y;

               for (double a = 0.0; a < Math.PI * 3; a += 0.4) {
                  double r = 0.3 + a * 0.3;
                  double nx = x + Math.cos(a) * r;
                  double ny = y + Math.sin(a) * r;
                  c.line(px, py, nx, ny, 0.5, AnimatedCapes.lerp(base, tip, 0.2));
                  px = nx;
                  py = ny;
               }

               c.disc(x, y, 0.6, AnimatedCapes.lerp(base, -1, 0.5));
            }
         }
      }

      for (double t = 0.0; t <= 1.0; t += 0.01) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t), 1.5 - 0.6 * t, base);
      }

      for (double t = 0.0; t <= 1.0; t += 0.1) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t), 1.1, AnimatedCapes.lerp(base, -1, 0.55));
      }
   }

   static void chainmail(WingCanvas c, int base, int tip) {
      MoreWings.silhouette(c, 1.0, 1.0);
      c.each((x, y, p) -> {
         int row = y / 3;
         double cx = Math.floor((x + row % 2 * 1.5) / 3.0) * 3.0 + 1.5 - row % 2 * 1.5;
         double cy = row * 3 + 1.5;
         double d = Math.hypot(x + 0.5 - cx, y + 0.5 - cy);
         if (d > 0.7 && d < 1.7) {
            boolean lit = y + 0.5 < cy;
            return AnimatedCapes.shade(base, lit ? 1.15F : 0.78F);
         } else {
            return AnimatedCapes.shade(base, 0.32F);
         }
      });

      for (double t = 0.0; t <= 1.0; t += 0.01) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t) + 1.0, 2.0 - 0.8 * t, tip);
      }

      for (double t = 0.05; t < 1.0; t += 0.12) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t) + 1.0, 0.6, -2572160);
      }

      c.rim(0.5);
   }

   static void wooden(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> MoreWings.plume(-1, along, across), -1513240, 1.0, 1.0);
      WingsElements.tint(c, (x, y) -> AnimatedCapes.lerp(base, tip, 0.5 + 0.45 * Math.sin(y * 1.1 + AnimatedCapes.fbm(x * 0.08, y * 0.2, 3) * 6.0)));

      for (int i = 0; i < 6; i++) {
         double[] a = MoreWings.primary(i, 0.12, 1.0);
         double[] b = MoreWings.primary(i, 0.88, 1.0);
         MoreWings.lineOnShape(c, a[0], a[1], b[0], b[1], 0.5, AnimatedCapes.alpha(AnimatedCapes.shade(tip, 0.6F), 0.8));
      }

      c.rim(0.5);
   }

   static void lantern(WingCanvas c, int base, int tip) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            double upper = MoreWings.forewing(x + 0.5, y + 0.5);
            double lower = MoreWings.hindwing(x + 0.5, y + 0.5);
            double t = Math.min(upper, lower);
            if (!(t > 1.0)) {
               int col = AnimatedCapes.lerp(AnimatedCapes.lerp(-2880, base, t * 0.8), tip, Math.max(0.0, t - 0.75) * 3.0);
               double ribs = upper < lower ? Math.sin((x - 27) * 0.9 - (y - 12) * 0.2) : Math.sin((x - 18) * 0.9 + (y - 27) * 0.4);
               if (Math.abs(ribs) < 0.12) {
                  col = AnimatedCapes.shade(col, 0.72F);
               }

               c.set(x, y, AnimatedCapes.alpha(col, 0.78 + 0.2 * t));
            }
         }
      }

      double[][] tassels = new double[][]{{51.0, 13.0}, {31.0, 36.0}};

      for (double[] ts : tassels) {
         c.line(ts[0], ts[1], ts[0] + 1.0, ts[1] + 3.0, 0.5, tip);

         for (int k = -1; k <= 1; k++) {
            c.line(ts[0] + 1.0, ts[1] + 3.0, ts[0] + 1.0 + k * 0.8, ts[1] + 6.0, 0.4, tip);
         }
      }
   }

   static void jet(WingCanvas c, int base, int tip) {
      double[][] outline = new double[][]{{1.0, 8.0}, {50.0, 19.0}, {55.0, 22.0}, {52.0, 24.0}, {4.0, 30.0}};
      c.polygon(outline, (x, y) -> AnimatedCapes.lerp(AnimatedCapes.shade(base, 1.08F), AnimatedCapes.shade(base, 0.8F), (y - 8 - x * 0.22) / 22.0));

      for (int k = 1; k < 5; k++) {
         double f = k / 5.0;
         MoreWings.lineOnShape(c, 1.0 + 49.0 * f, 8.0 + 11.0 * f, 4.0 + 48.0 * f, 30.0 - 6.0 * f, 0.4, AnimatedCapes.shade(base, 0.7F));
      }

      MoreWings.lineOnShape(c, 3.0, 25.0, 52.0, 22.0, 0.5, AnimatedCapes.shade(base, 0.6F));
      MoreWings.lineOnShape(c, 2.0, 16.0, 51.0, 20.5, 0.4, AnimatedCapes.shade(base, 0.75));
      c.polygon(new double[][]{{40.0, 16.5}, {45.0, 17.6}, {47.5, 24.5}, {42.0, 25.4}}, tip);
      c.disc(26.0, 20.0, 3.4, -14005584);
      c.disc(26.0, 20.0, 2.3, -723724);
      c.disc(26.0, 20.0, 1.2, -2082246);
      c.ellipse(16.0, 29.0, 6.5, 2.4, 0.2, AnimatedCapes.shade(base, 0.6F));
      c.ellipse(10.5, 28.0, 1.4, 2.0, 0.2, -14013904);
      c.disc(54.0, 22.0, 1.2, -12918678);
      c.rim(0.55);
   }

   static void solar(WingCanvas c, int base, int tip) {
      for (int p = 0; p < 5; p++) {
         double px = 5 + p * 10;
         double axis = 14.0 - p * 0.8;

         for (int row = -1; row <= 1; row += 2) {
            double top = row < 0 ? axis - 11.0 : axis + 1.5;
            c.polygon(new double[][]{{px, top}, {px + 9.0, top}, {px + 9.0, top + 9.5}, {px, top + 9.5}}, (x, y) -> {
               boolean grid = (x - (int)px) % 3 == 0 || (int)(y - top) % 3 == 0;
               int col = grid ? AnimatedCapes.shade(base, 0.6F) : AnimatedCapes.lerp(base, -11896096, 0.2);
               double shine = Math.abs(x - px - (y - top) * 0.9 - 3.0);
               return shine < 1.2 ? AnimatedCapes.lerp(col, -1, 0.35) : col;
            });
            c.line(px, top, px + 9.0, top, 0.6, tip);
            c.line(px, top + 9.5, px + 9.0, top + 9.5, 0.6, tip);
            c.line(px, top, px, top + 9.5, 0.6, tip);
            c.line(px + 9.0, top, px + 9.0, top + 9.5, 0.6, tip);
         }
      }

      c.line(0.0, 14.0, 54.0, 10.0, 1.4, -4671296);

      for (int k = 0; k < 6; k++) {
         c.disc(4.5 + k * 10, 14.0 - k * 0.8, 0.8, -9803152);
      }
   }

   static void energyBlade(WingCanvas c, int base, int tip) {
      for (int k = 0; k < 7; k++) {
         double t = 0.12 + k * 0.13;
         double rx = MoreWings.boneX(t);
         double ry = MoreWings.boneY(t) + 1.0;
         double angle = Math.toRadians(-4 + k * 6);
         double len = 24.0 + k * 2.2 - Math.max(0, k - 4) * 3;
         double ux = Math.sin(angle);
         double uy = Math.cos(angle);
         double half = 1.8;
         int col = AnimatedCapes.lerp(base, tip, k / 6.0);
         c.polygon(new double[][]{{rx - uy * half, ry + ux * half}, {rx + ux * len, ry + uy * len}, {rx + uy * half, ry - ux * half}}, (x, y) -> {
            double along = ((x - rx) * ux + (y - ry) * uy) / len;
            return AnimatedCapes.alpha(col, 0.55 + 0.35 * (1.0 - along));
         });
         c.line(rx, ry, rx + ux * len * 0.85, ry + uy * len * 0.85, 0.6, AnimatedCapes.alpha(-1, 0.9));
         c.disc(rx, ry, 1.6, col);
         c.disc(rx, ry, 0.8, -1);
      }

      for (double t = 0.0; t <= 0.95; t += 0.01) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t), 0.9, AnimatedCapes.alpha(base, 0.85));
      }
   }

   static void hexgrid(WingCanvas c, int base, int tip) {
      MoreWings.silhouette(c, 1.0, 1.05);
      double size = 3.2;
      c.each((x, y, p) -> {
         double qy = (y + 0.5) / (size * 1.5);
         int row = (int)Math.floor(qy);
         double qx = (x + 0.5) / (size * Math.sqrt(3.0)) - row % 2 * 0.5;
         int colIndex = (int)Math.floor(qx);
         double best = 99.0;
         int bestRow = row;
         int bestCol = colIndex;

         for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
               int r = row + dr;
               int cc = colIndex + dc;
               double cx = (cc + 0.5 + Math.floorMod(r, 2) * 0.5) * size * Math.sqrt(3.0);
               double cy = (r + 0.5) * size * 1.5;
               double d = Math.hypot(x + 0.5 - cx, y + 0.5 - cy);
               if (d < best) {
                  best = d;
                  bestRow = r;
                  bestCol = cc;
               }
            }
         }

         boolean edge = best > size * 0.72;
         boolean bright = AnimatedCapes.hash(bestCol, bestRow) > 0.82;
         int col = AnimatedCapes.lerp(base, tip, y / 40.0 * 0.7);
         return edge ? AnimatedCapes.alpha(AnimatedCapes.lerp(col, -1, 0.3), 0.95) : AnimatedCapes.alpha(col, bright ? 0.6 : 0.2);
      });
      c.rim(1.4);
   }

   static void music(WingCanvas c, int base, int tip) {
      double[][] staves = new double[][]{{0.0, 54.0, 0.0}, {3.0, 46.0, 11.0}, {6.0, 34.0, 22.0}};

      for (int s = 0; s < staves.length; s++) {
         double x0 = staves[s][0];
         double x1 = staves[s][1];
         double drop = staves[s][2];

         for (int line = 0; line < 5; line++) {
            double px = x0;

            for (double x = x0 + 1.0; x <= x1; px = x++) {
               double t = (x - 2.0) / 50.0;
               double y0 = MoreWings.boneY(AnimatedCapes.clamp((px - 2.0) / 50.0, 0.0, 1.0)) + drop + line * 2.1 + drop * 0.25 * ((px - x0) / 50.0);
               double y1 = MoreWings.boneY(AnimatedCapes.clamp(t, 0.0, 1.0)) + drop + line * 2.1 + drop * 0.25 * ((x - x0) / 50.0);
               c.line(px, y0, x, y1, 0.4, base);
            }
         }

         for (int n = 0; n < 6 - s; n++) {
            double nx = x0 + 10.0 + n * (x1 - x0 - 12.0) / (5.0 - s);
            double t = (nx - 2.0) / 50.0;
            double step = (int)(AnimatedCapes.hash(n, s) * 7.0) * 0.8;
            double ny = MoreWings.boneY(AnimatedCapes.clamp(t, 0.0, 1.0)) + drop + drop * 0.25 * ((nx - x0) / 50.0) + step;
            int col = n % 3 == 1 ? tip : base;
            c.ellipse(nx, ny, 1.4, 1.0, -0.4, col);
            c.line(nx + 1.2, ny, nx + 1.2, ny - 5.5, 0.5, col);
            if (n % 2 == 0) {
               c.line(nx + 1.2, ny - 5.5, nx + 3.0, ny - 3.8, 0.7, col);
            }
         }
      }

      double[][] clef = new double[][]{
         {4.0, 14.0}, {6.0, 12.0}, {5.0, 9.0}, {3.0, 10.0}, {3.0, 13.0}, {5.0, 16.0}, {7.0, 15.0}, {6.0, 5.0}, {5.0, 3.0}, {4.0, 6.0}, {5.0, 18.0}
      };

      for (int k = 0; k < clef.length - 1; k++) {
         c.line(clef[k][0], clef[k][1], clef[k + 1][0], clef[k + 1][1], 0.8, tip);
      }

      c.disc(4.5, 18.5, 0.9, tip);
   }

   static void opal(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> MoreWings.plume(-1, along, across), -723724, 1.0, 1.0);
      WingsElements.tint(
         c,
         (xx, yx) -> {
            double patch = AnimatedCapes.fbm(xx * 0.12 + 5.0, yx * 0.12, 3);
            int fire = AnimatedCapes.hsv(patch * 720.0, 0.55, 1.0);
            return AnimatedCapes.lerp(
               base, AnimatedCapes.lerp(fire, tip, 0.25), AnimatedCapes.smoothstep(0.42, 0.68, AnimatedCapes.fbm(xx * 0.2, yx * 0.2 + 9.0, 3)) * 0.75
            );
         }
      );

      for (int k = 0; k < 18; k++) {
         int x = (int)(4.0 + AnimatedCapes.hash(k, 1) * 50.0);
         int y = (int)(3.0 + AnimatedCapes.hash(k, 2) * 34.0);
         if (c.filled(x, y)) {
            c.set(x, y, -1);
         }
      }

      c.rim(0.8);
   }
}
