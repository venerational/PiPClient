package dev.lyfw.lyfwclient.gui;

final class IconRaster {
   static final int WHITE = -1;
   final int[] pixels;
   final int size;

   IconRaster(int size) {
      this.size = size;
      this.pixels = new int[size * size];
   }

   private void set(int x, int y, int color) {
      if (x >= 0 && y >= 0 && x < this.size && y < this.size) {
         this.pixels[y * this.size + x] = color;
      }
   }

   void rect(int x, int y, int w, int h, int color) {
      for (int dy = 0; dy < h; dy++) {
         for (int dx = 0; dx < w; dx++) {
            this.set(x + dx, y + dy, color);
         }
      }
   }

   void disc(int cx, int cy, int r, int color) {
      for (int dx = -r; dx <= r; dx++) {
         int half = (int)Math.round(Math.sqrt(Math.max(0.0, (double)(r * r - dx * dx))));

         for (int dy = -half; dy < half; dy++) {
            this.set(cx + dx, cy + dy, color);
         }
      }
   }

   void ring(int cx, int cy, int r, int t, int color) {
      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d <= r && d >= r - t) {
               this.set(cx + dx, cy + dy, color);
            }
         }
      }
   }

   void arc(int cx, int cy, int r, int t, int color) {
      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= 0; dy++) {
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d <= r && d >= r - t) {
               this.set(cx + dx, cy + dy, color);
            }
         }
      }
   }

   void openRing(int cx, int cy, int r, int t, double gapFrom, double gapTo, int color) {
      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d <= r && d >= r - t) {
               double a = Math.atan2(dy, dx);
               if (a <= gapFrom || a >= gapTo) {
                  this.set(cx + dx, cy + dy, color);
               }
            }
         }
      }
   }

   void line(int x1, int y1, int x2, int y2, int t, int color) {
      int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
      if (steps == 0) {
         this.rect(x1, y1, t, t, color);
      } else {
         for (int i = 0; i <= steps; i++) {
            int px = x1 + Math.round((float)(x2 - x1) * i / steps);
            int py = y1 + Math.round((float)(y2 - y1) * i / steps);
            this.rect(px - t / 2, py - t / 2, t, t, color);
         }
      }
   }

   void triangle(int cx, int baseY, int height, int color) {
      int steps = Math.abs(height);
      int dir = height > 0 ? -1 : 1;

      for (int i = 0; i < steps; i++) {
         int w = Math.max(1, Math.round((steps - i) * 0.9F));
         this.rect(cx - w, baseY + i * dir, w * 2, 1, color);
      }
   }

   void rays(int cx, int cy, int inner, int outer, int color) {
      for (int i = 0; i < 8; i++) {
         double a = Math.PI * i / 4.0;
         this.line(
            cx + (int)Math.round(Math.cos(a) * inner),
            cy + (int)Math.round(Math.sin(a) * inner),
            cx + (int)Math.round(Math.cos(a) * outer),
            cy + (int)Math.round(Math.sin(a) * outer),
            2,
            color
         );
      }
   }

   void corners(int x, int y, int w, int h, int len, int t, int color) {
      this.rect(x, y, len, t, color);
      this.rect(x, y, t, len, color);
      this.rect(x + w - len, y, len, t, color);
      this.rect(x + w - t, y, t, len, color);
      this.rect(x, y + h - t, len, t, color);
      this.rect(x, y + h - len, t, len, color);
      this.rect(x + w - len, y + h - t, len, t, color);
      this.rect(x + w - t, y + h - len, t, len, color);
   }

   void eye(int cx, int cy, int color) {
      for (int dx = -8; dx <= 8; dx++) {
         int half = (int)Math.round(Math.sqrt(Math.max(0.0, 64.0 - dx * dx)) * 0.62);
         if (half > 0) {
            this.rect(cx + dx, cy - half, 1, 2, color);
            this.rect(cx + dx, cy + half - 1, 1, 2, color);
         }
      }

      this.disc(cx, cy, 3, color);
   }

   void shield(int cx, int top, int size, int color) {
      int half = size / 2;

      for (int row = 0; row < size; row++) {
         float t = (float)row / size;
         int w = t < 0.55F ? half : Math.round(half * (1.0F - (t - 0.55F) / 0.45F));
         if (w > 0) {
            this.rect(cx - w, top + row, w * 2, 1, color);
         }
      }
   }

   void star(float cx, float cy, float radius, int points, float innerRatio) {
      this.fillSmooth(starPoints(cx, cy, radius, radius * innerRatio, points), null);
   }

   void starOutline(float cx, float cy, float radius, int points, float innerRatio, float thickness) {
      float holeOuter = Math.max(0.5F, radius - thickness * 1.7F);
      float holeInner = Math.max(0.3F, radius * innerRatio - thickness * 0.8F);
      this.fillSmooth(starPoints(cx, cy, radius, radius * innerRatio, points), starPoints(cx, cy, holeOuter, holeInner, points));
   }

   private static float[][] starPoints(float cx, float cy, float outer, float inner, int points) {
      float[][] corners = new float[points * 2][];

      for (int i = 0; i < corners.length; i++) {
         float r = i % 2 == 0 ? outer : inner;
         double angle = (-Math.PI / 2) + Math.PI * i / points;
         corners[i] = new float[]{(float)(cx + Math.cos(angle) * r), (float)(cy + Math.sin(angle) * r)};
      }

      return corners;
   }

   private void fillSmooth(float[][] shape, float[][] hole) {
      for (int y = 0; y < this.size; y++) {
         for (int x = 0; x < this.size; x++) {
            int covered = 0;

            for (int sy = 0; sy < 4; sy++) {
               for (int sx = 0; sx < 4; sx++) {
                  float px = x + (sx + 0.5F) / 4.0F;
                  float py = y + (sy + 0.5F) / 4.0F;
                  if (inside(shape, px, py) && (hole == null || !inside(hole, px, py))) {
                     covered++;
                  }
               }
            }

            int alpha = covered * 255 / 16;
            if (alpha > this.pixels[y * this.size + x] >>> 24) {
               this.pixels[y * this.size + x] = alpha << 24 | 16777215;
            }
         }
      }
   }

   private static boolean inside(float[][] polygon, float x, float y) {
      boolean in = false;
      int i = 0;

      for (int j = polygon.length - 1; i < polygon.length; j = i++) {
         float xi = polygon[i][0];
         float yi = polygon[i][1];
         float xj = polygon[j][0];
         float yj = polygon[j][1];
         if (yi > y != yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
            in = !in;
         }
      }

      return in;
   }
}
