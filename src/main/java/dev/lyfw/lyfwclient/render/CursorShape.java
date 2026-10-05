package dev.lyfw.lyfwclient.render;

import net.minecraft.client.gui.GuiGraphics;

public enum CursorShape {
   DOT("Dot") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         disc(context, cx, cy, Math.max(1, size / 2), color);
      }
   },
   SQUARE("Square") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int h = Math.max(1, size / 2);
         context.fill(cx - h, cy - h, cx + h, cy + h, color);
      }
   },
   RING("Ring") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         ring(context, cx, cy, Math.max(2, size / 2), Math.max(1, size / 5), color);
      }
   },
   CROSS("Cross") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(2, size / 2);
         int t = Math.max(1, size / 5);
         line(context, cx - r, cy - r, cx + r, cy + r, t, color);
         line(context, cx + r, cy - r, cx - r, cy + r, t, color);
      }
   },
   PLUS("Plus") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(2, size / 2);
         int t = Math.max(1, size / 4);
         context.fill(cx - t / 2 - 1, cy - r, cx + t / 2 + 1, cy + r, color);
         context.fill(cx - r, cy - t / 2 - 1, cx + r, cy + t / 2 + 1, color);
      }
   },
   STAR("Star") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         star(context, cx, cy, Math.max(3, size / 2), 5, 0.42F, 0.0F, color);
      }
   },
   SPARKLE("Sparkle") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         star(context, cx, cy, Math.max(3, size / 2), 4, 0.22F, 0.0F, color);
      }
   },
   HEART("Heart") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(2, size / 2);
         int lobe = Math.max(1, Math.round(r * 0.52F));
         disc(context, cx - lobe / 2 - 1, cy - lobe / 2, lobe, color);
         disc(context, cx + lobe / 2 + 1, cy - lobe / 2, lobe, color);

         for (int row = 0; row < r; row++) {
            int w = Math.max(1, Math.round(r * (1.0F - (float)row / r)));
            context.fill(cx - w, cy - lobe / 2 + row, cx + w, cy - lobe / 2 + row + 1, color);
         }
      }
   },
   DIAMOND("Diamond") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(2, size / 2);

         for (int dy = -r; dy <= r; dy++) {
            int w = r - Math.abs(dy);
            if (w > 0) {
               context.fill(cx - w, cy + dy, cx + w, cy + dy + 1, color);
            }
         }
      }
   },
   TRIANGLE("Triangle") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(2, size / 2);

         for (int row = 0; row < r * 2; row++) {
            int w = Math.max(1, Math.round((float)(r * row) / (r * 2)));
            context.fill(cx - w, cy - r + row, cx + w, cy - r + row + 1, color);
         }
      }
   },
   ARROW("Arrow") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(3, size / 2);

         for (int row = 0; row < r * 2; row++) {
            int w = Math.max(1, Math.round(r * 0.75F * (1.0F - (float)row / (r * 2))));
            context.fill(cx - r / 2, cy - r + row, cx - r / 2 + w + 1, cy - r + row + 1, color);
         }
      }
   },
   SKULL("Skull") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(3, size / 2);
         disc(context, cx, cy - r / 4, r, color);
         context.fill(cx - r / 2, cy + r / 2, cx + r / 2, cy + r, color);
         int socket = Math.max(1, r / 3);
         context.fill(cx - r / 2, cy - r / 3, cx - r / 2 + socket, cy - r / 3 + socket, -16777216);
         context.fill(cx + r / 2 - socket, cy - r / 3, cx + r / 2, cy - r / 3 + socket, -16777216);
      }
   },
   CIRCLE_DOT("Circle + Dot") {
      @Override
      public void draw(GuiGraphics context, int cx, int cy, int size, int color) {
         int r = Math.max(3, size / 2);
         ring(context, cx, cy, r, Math.max(1, r / 4), color);
         disc(context, cx, cy, Math.max(1, r / 3), color);
      }
   };

   public final String title;

   private CursorShape(String title) {
      this.title = title;
   }

   public abstract void draw(GuiGraphics guiGraphics, int i, int j, int k, int l);

   static void star(GuiGraphics context, int cx, int cy, int radius, int points, float innerRatio, float rotation, int color) {
      int steps = points * 2;
      float[] xs = new float[steps];
      float[] ys = new float[steps];

      for (int i = 0; i < steps; i++) {
         float r = i % 2 == 0 ? radius : radius * innerRatio;
         double angle = rotation - (Math.PI / 2) + Math.PI * i / points;
         xs[i] = (float)(cx + Math.cos(angle) * r);
         ys[i] = (float)(cy + Math.sin(angle) * r);
      }

      for (int i = 0; i < steps; i++) {
         triangle(context, cx, cy, xs[i], ys[i], xs[(i + 1) % steps], ys[(i + 1) % steps], color);
      }
   }

   private static void triangle(GuiGraphics context, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
      int top = (int)Math.floor(Math.min(y1, Math.min(y2, y3)));
      int bottom = (int)Math.ceil(Math.max(y1, Math.max(y2, y3)));

      for (int y = top; y <= bottom; y++) {
         float cy = y + 0.5F;
         float minX = Float.MAX_VALUE;
         float maxX = -Float.MAX_VALUE;
         float[][] edges = new float[][]{{x1, y1, x2, y2}, {x2, y2, x3, y3}, {x3, y3, x1, y1}};

         for (float[] e : edges) {
            float ay = e[1];
            float by = e[3];
            if (cy >= Math.min(ay, by) && cy <= Math.max(ay, by) && ay != by) {
               float t = (cy - ay) / (by - ay);
               float x = e[0] + (e[2] - e[0]) * t;
               minX = Math.min(minX, x);
               maxX = Math.max(maxX, x);
            }
         }

         if (maxX >= minX) {
            context.fill(Math.round(minX), y, Math.max(Math.round(minX) + 1, Math.round(maxX)), y + 1, color);
         }
      }
   }

   static void disc(GuiGraphics context, int cx, int cy, int r, int color) {
      for (int dx = -r; dx <= r; dx++) {
         int half = (int)Math.round(Math.sqrt(Math.max(0.0, (double)(r * r - dx * dx))));
         if (half > 0) {
            context.fill(cx + dx, cy - half, cx + dx + 1, cy + half, color);
         }
      }
   }

   static void ring(GuiGraphics context, int cx, int cy, int r, int t, int color) {
      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d <= r && d >= r - t) {
               context.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
            }
         }
      }
   }

   static void line(GuiGraphics context, int x1, int y1, int x2, int y2, int t, int color) {
      int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
      if (steps == 0) {
         context.fill(x1, y1, x1 + t, y1 + t, color);
      } else {
         for (int i = 0; i <= steps; i++) {
            int px = x1 + Math.round((float)(x2 - x1) * i / steps);
            int py = y1 + Math.round((float)(y2 - y1) * i / steps);
            context.fill(px - t / 2, py - t / 2, px - t / 2 + t, py - t / 2 + t, color);
         }
      }
   }
}
