package dev.lyfw.lyfwclient.render;

import net.minecraft.client.gui.GuiGraphics;

public enum TrailStyle {
   CLASSIC("Classic") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         int s = Math.max(1, size / 2);
         fillCentred(context, x, y, s, withAlpha(color, fade(age)));
      }
   },
   STARS("Stars") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         int r = Math.max(2, Math.round(size * (0.5F + seed * 0.5F) * (1.0F - age * 0.6F)));
         CursorShape.star(context, Math.round(x), Math.round(y), r, 4, 0.3F, seed * 6.28F + age * 3.0F, withAlpha(color, fade(age)));
      }
   },
   BLOOD("Blood") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         float drip = age * age * size * 2.0F;
         int r = Math.max(1, Math.round(size * (0.35F + seed * 0.5F) * (1.0F - age * 0.4F)));
         int dark = mix(-4975588, -10876400, age);
         CursorShape.disc(context, Math.round(x), Math.round(y + drip), r, withAlpha(dark, fade(age)));
      }
   },
   LIGHTNING("Lightning") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         float dx = x - px;
         float dy = y - py;
         float len = (float)Math.sqrt(dx * dx + dy * dy);
         if (!(len < 0.01F)) {
            float nx = -dy / len;
            float ny = dx / len;
            float kink = (seed - 0.5F) * size * 2.4F * (1.0F - age);
            int alpha = fade(age);
            int glow = withAlpha(-8758017, alpha / 2);
            int core = withAlpha(-856833, alpha);
            int mx = Math.round((x + px) / 2.0F + nx * kink);
            int my = Math.round((y + py) / 2.0F + ny * kink);
            CursorShape.line(context, Math.round(px), Math.round(py), mx, my, Math.max(2, size / 2), glow);
            CursorShape.line(context, mx, my, Math.round(x), Math.round(y), Math.max(2, size / 2), glow);
            CursorShape.line(context, Math.round(px), Math.round(py), mx, my, 1, core);
            CursorShape.line(context, mx, my, Math.round(x), Math.round(y), 1, core);
         }
      }
   },
   FIRE("Fire") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         float lift = age * size * 2.5F;
         float wobble = (float)Math.sin(seed * 12.0 + age * 6.0) * size * 0.4F;
         int hot = mix(-7286, -35560, Math.min(1.0F, age * 2.0F));
         int cooled = mix(hot, -8774907, age);
         int r = Math.max(1, Math.round(size * (0.6F + seed * 0.4F) * (1.0F - age * 0.5F)));
         CursorShape.disc(context, Math.round(x + wobble), Math.round(y - lift), r, withAlpha(cooled, fade(age)));
      }
   },
   SPARKS("Sparks") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         double angle = seed * Math.PI * 2.0;
         float dist = age * size * 3.0F * (0.4F + seed);
         float sx = x + (float)Math.cos(angle) * dist;
         float sy = y + (float)Math.sin(angle) * dist + age * age * size * 1.5F;
         fillCentred(context, sx, sy, Math.max(1, Math.round(size * 0.4F * (1.0F - age))), withAlpha(color, fade(age)));
      }
   },
   RAINBOW("Rainbow") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         int hue = hsv((age * 300.0F + seed * 40.0F) % 360.0F, 0.85F, 1.0F);
         fillCentred(context, x, y, Math.max(1, Math.round(size * 0.6F)), withAlpha(hue, fade(age)));
      }
   },
   BUBBLES("Bubbles") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         float rise = age * size * 3.0F;
         int r = Math.max(2, Math.round(size * (0.4F + seed * 0.6F) * (0.6F + age)));
         CursorShape.ring(context, Math.round(x), Math.round(y - rise), r, 1, withAlpha(color, Math.round(fade(age) * 0.8F)));
      }
   },
   SMOKE("Smoke") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         float rise = age * size * 2.0F;
         float wobble = (float)Math.sin(seed * 9.0 + age * 4.0) * size * 0.8F;
         int r = Math.max(1, Math.round(size * (0.5F + age * 1.2F)));
         int grey = mix(-4210753, -11908534, age);
         CursorShape.disc(context, Math.round(x + wobble), Math.round(y - rise), r, withAlpha(grey, Math.round(fade(age) * 0.55F)));
      }
   },
   RIBBON("Ribbon") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
         int t = Math.max(1, Math.round(size * (1.0F - age)));
         CursorShape.line(context, Math.round(px), Math.round(py), Math.round(x), Math.round(y), t, withAlpha(color, fade(age)));
      }
   },
   NONE("None") {
      @Override
      public void draw(GuiGraphics context, float x, float y, float px, float py, float age, float seed, int color, int size) {
      }
   };

   public final String title;

   private TrailStyle(String title) {
      this.title = title;
   }

   public abstract void draw(GuiGraphics guiGraphics, float f, float g, float h, float i, float j, float k, int l, int m);

   public boolean usesTrailColor() {
      return this != BLOOD && this != FIRE && this != LIGHTNING && this != RAINBOW && this != SMOKE;
   }

   public int spacing() {
      return switch (this) {
         case STARS -> 6;
         case BLOOD, SMOKE -> 5;
         case LIGHTNING -> 2;
         case FIRE -> 3;
         case SPARKS -> 4;
         default -> 1;
         case BUBBLES -> 7;
      };
   }

   static int fade(float age) {
      return Math.max(0, Math.min(255, Math.round((1.0F - age) * 235.0F)));
   }

   static void fillCentred(GuiGraphics context, float x, float y, int r, int color) {
      int ix = Math.round(x);
      int iy = Math.round(y);
      context.fill(ix - r, iy - r, ix + r, iy + r, color);
   }

   static int withAlpha(int color, int alpha) {
      return Math.max(0, Math.min(255, alpha)) << 24 | color & 16777215;
   }

   static int mix(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int r = Math.round((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
      int g = Math.round((from >> 8 & 0xFF) + ((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
      int b = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   static int hsv(float hue, float saturation, float value) {
      float c = value * saturation;
      float x = c * (1.0F - Math.abs(hue / 60.0F % 2.0F - 1.0F));
      float m = value - c;
      float r;
      float g;
      float b;
      if (hue < 60.0F) {
         r = c;
         g = x;
         b = 0.0F;
      } else if (hue < 120.0F) {
         r = x;
         g = c;
         b = 0.0F;
      } else if (hue < 180.0F) {
         r = 0.0F;
         g = c;
         b = x;
      } else if (hue < 240.0F) {
         r = 0.0F;
         g = x;
         b = c;
      } else if (hue < 300.0F) {
         r = x;
         g = 0.0F;
         b = c;
      } else {
         r = c;
         g = 0.0F;
         b = x;
      }

      return 0xFF000000 | Math.round((r + m) * 255.0F) << 16 | Math.round((g + m) * 255.0F) << 8 | Math.round((b + m) * 255.0F);
   }
}
