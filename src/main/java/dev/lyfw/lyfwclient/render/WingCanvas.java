package dev.lyfw.lyfwclient.render;

import java.util.function.IntBinaryOperator;

final class WingCanvas {
   static final int WIDTH = 56;
   static final int HEIGHT = 40;
   final int[] px = new int[2240];

   int get(int x, int y) {
      return x >= 0 && y >= 0 && x < 56 && y < 40 ? this.px[y * 56 + x] : 0;
   }

   boolean filled(int x, int y) {
      return this.get(x, y) >>> 24 != 0;
   }

   void set(int x, int y, int argb) {
      if (x >= 0 && y >= 0 && x < 56 && y < 40) {
         this.px[y * 56 + x] = argb;
      }
   }

   void over(int x, int y, int argb) {
      int sa = argb >>> 24;
      if (sa != 0 && x >= 0 && y >= 0 && x < 56 && y < 40) {
         int i = y * 56 + x;
         int d = this.px[i];
         int da = d >>> 24;
         if (sa != 255 && da != 0) {
            double a = sa / 255.0;
            double b = da / 255.0 * (1.0 - a);
            double out = a + b;
            int r = (int)Math.round(((argb >> 16 & 0xFF) * a + (d >> 16 & 0xFF) * b) / out);
            int g = (int)Math.round(((argb >> 8 & 0xFF) * a + (d >> 8 & 0xFF) * b) / out);
            int bl = (int)Math.round(((argb & 0xFF) * a + (d & 0xFF) * b) / out);
            this.px[i] = (int)Math.round(out * 255.0) << 24 | r << 16 | g << 8 | bl;
         } else {
            this.px[i] = argb;
         }
      }
   }

   void each(WingCanvas.PixelOp op) {
      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            int i = y * 56 + x;
            if (this.px[i] >>> 24 != 0) {
               this.px[i] = op.apply(x, y, this.px[i]);
            }
         }
      }
   }

   void onShape(int x, int y, int argb) {
      if (this.filled(x, y)) {
         int keep = this.get(x, y) >>> 24;
         this.over(x, y, argb);
         this.px[y * 56 + x] = keep << 24 | this.px[y * 56 + x] & 16777215;
      }
   }

   void disc(double cx, double cy, double r, int argb) {
      double alpha = (argb >>> 24) / 255.0;

      for (int y = (int)Math.floor(cy - r - 1.0); y <= (int)Math.ceil(cy + r + 1.0); y++) {
         for (int x = (int)Math.floor(cx - r - 1.0); x <= (int)Math.ceil(cx + r + 1.0); x++) {
            double coverage = AnimatedCapes.clamp(r + 0.5 - Math.hypot(x + 0.5 - cx, y + 0.5 - cy), 0.0, 1.0);
            if (coverage > 0.0) {
               this.over(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
            }
         }
      }
   }

   void ellipse(double cx, double cy, double rx, double ry, double angle, int argb) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double reach = Math.max(rx, ry) + 1.0;
      double alpha = (argb >>> 24) / 255.0;

      for (int y = (int)Math.floor(cy - reach); y <= (int)Math.ceil(cy + reach); y++) {
         for (int x = (int)Math.floor(cx - reach); x <= (int)Math.ceil(cx + reach); x++) {
            double dx = x + 0.5 - cx;
            double dy = y + 0.5 - cy;
            double u = (dx * cos + dy * sin) / rx;
            double v = (-dx * sin + dy * cos) / ry;
            double coverage = AnimatedCapes.clamp((1.0 - Math.sqrt(u * u + v * v)) * Math.min(rx, ry) + 0.5, 0.0, 1.0);
            if (coverage > 0.0) {
               this.over(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
            }
         }
      }
   }

   void line(double x0, double y0, double x1, double y1, double width, int argb) {
      double length = Math.max(0.001, Math.hypot(x1 - x0, y1 - y0));
      double dx = (x1 - x0) / length;
      double dy = (y1 - y0) / length;
      double half = width / 2.0;
      double alpha = (argb >>> 24) / 255.0;

      for (int y = (int)Math.floor(Math.min(y0, y1) - half - 1.0); y <= (int)Math.ceil(Math.max(y0, y1) + half + 1.0); y++) {
         for (int x = (int)Math.floor(Math.min(x0, x1) - half - 1.0); x <= (int)Math.ceil(Math.max(x0, x1) + half + 1.0); x++) {
            double along = AnimatedCapes.clamp((x + 0.5 - x0) * dx + (y + 0.5 - y0) * dy, 0.0, length);
            double d = Math.hypot(x + 0.5 - (x0 + dx * along), y + 0.5 - (y0 + dy * along));
            double coverage = AnimatedCapes.clamp(half + 0.5 - d, 0.0, 1.0);
            if (coverage > 0.0) {
               this.over(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
            }
         }
      }
   }

   void polygon(double[][] points, IntBinaryOperator color) {
      double minX = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double maxY = -Double.MAX_VALUE;

      for (double[] p : points) {
         minX = Math.min(minX, p[0]);
         maxX = Math.max(maxX, p[0]);
         minY = Math.min(minY, p[1]);
         maxY = Math.max(maxY, p[1]);
      }

      for (int y = Math.max(0, (int)Math.floor(minY)); y <= Math.min(39, (int)Math.ceil(maxY)); y++) {
         for (int x = Math.max(0, (int)Math.floor(minX)); x <= Math.min(55, (int)Math.ceil(maxX)); x++) {
            if (inside(points, x + 0.5, y + 0.5)) {
               this.over(x, y, color.applyAsInt(x, y));
            }
         }
      }
   }

   void polygon(double[][] points, int argb) {
      this.polygon(points, (x, y) -> argb);
   }

   void clearDisc(double cx, double cy, double r) {
      for (int y = (int)Math.floor(cy - r); y <= (int)Math.ceil(cy + r); y++) {
         for (int x = (int)Math.floor(cx - r); x <= (int)Math.ceil(cx + r); x++) {
            if (Math.hypot(x + 0.5 - cx, y + 0.5 - cy) <= r) {
               this.set(x, y, 0);
            }
         }
      }
   }

   void rim(double factor) {
      int[] copy = (int[])this.px.clone();

      for (int y = 0; y < 40; y++) {
         for (int x = 0; x < 56; x++) {
            int p = copy[y * 56 + x];
            if (p >>> 24 != 0) {
               boolean edge = false;

               for (int k = 0; k < 4 && !edge; k++) {
                  int nx = x + (k == 0 ? 1 : (k == 1 ? -1 : 0));
                  int ny = y + (k == 2 ? 1 : (k == 3 ? -1 : 0));
                  edge = nx < 0 || ny < 0 || nx >= 56 || ny >= 40 || copy[ny * 56 + nx] >>> 24 == 0;
               }

               if (edge) {
                  this.px[y * 56 + x] = p & 0xFF000000 | AnimatedCapes.shade(p, factor) & 16777215;
               }
            }
         }
      }
   }

   private static boolean inside(double[][] points, double x, double y) {
      boolean in = false;
      int i = 0;

      for (int j = points.length - 1; i < points.length; j = i++) {
         if (points[i][1] > y != points[j][1] > y && x < (points[j][0] - points[i][0]) * (y - points[i][1]) / (points[j][1] - points[i][1]) + points[i][0]) {
            in = !in;
         }
      }

      return in;
   }

   interface PixelOp {
      int apply(int i, int j, int k);
   }
}
