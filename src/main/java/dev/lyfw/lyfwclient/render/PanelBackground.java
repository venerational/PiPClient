package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.gui.ThemeRenderer;
import net.minecraft.client.gui.GuiGraphics;

public enum PanelBackground {
   NONE("None") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
      }
   },
   SOLID("Solid") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);
      }
   },
   GRADIENT("Gradient") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);
         float shift = (float)((Math.sin(time * 0.35) + 1.0) * 0.5);

         for (int i = 0; i < w; i += 3) {
            float t = (float)i / w;
            int alpha = Math.round(Math.max(0.0F, 1.0F - Math.abs(t - shift) * 2.2F) * 70.0F * (0.7F + beat * 0.5F));
            if (alpha > 1) {
               context.fill(x + i, y, x + Math.min(w, i + 3), y + h, alpha << 24 | accent & 16777215);
            }
         }
      }
   },
   PULSE("Pulse") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);
         int alpha = Math.round(beat * 90.0F);
         if (alpha > 1) {
            int inset = Math.round((1.0F - beat) * h * 0.45F);
            ThemeRenderer.fillRounded(context, x + inset, y + inset, Math.max(2, w - inset * 2), Math.max(2, h - inset * 2), alpha << 24 | accent & 16777215, 6);
         }
      }
   },
   EQUALIZER("Equalizer") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);
         int bars = Math.max(6, w / 9);
         float cell = (float)w / bars;

         for (int i = 0; i < bars; i++) {
            double phase = i * 0.7;
            float level = (float)(Math.sin(time * 2.1 + phase) * 0.3 + Math.sin(time * 3.3 - phase * 1.7) * 0.2 + 0.45);
            level = Math.max(0.05F, level * (0.75F + beat * 0.45F));
            int bh = Math.round(level * h);
            int bx = x + Math.round(i * cell);
            context.fill(bx, y + h - bh, bx + Math.max(1, Math.round(cell) - 1), y + h, 687865855 | accent & 16777215);
         }
      }
   },
   SCANLINES("Scanlines") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);
         int offset = (int)(time * 14.0) % 4;

         for (int row = offset; row < h; row += 4) {
            context.fill(x, y + row, x + w, y + row + 1, Math.round(28.0F + beat * 30.0F) << 24 | accent & 16777215);
         }
      }
   },
   AURORA("Aurora") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);

         for (int i = 0; i < w; i += 2) {
            float t = (float)i / w;
            float bandA = (float)Math.sin(t * 6.0 + time * 0.8) * 0.5F + 0.5F;
            float bandB = (float)Math.sin(t * 3.5 - time * 1.15 + 2.0) * 0.5F + 0.5F;
            int topA = y + Math.round(bandA * h * 0.5F);
            int topB = y + Math.round(bandB * h * 0.6F);
            int alpha = Math.round(26.0F + beat * 26.0F);
            context.fill(x + i, topA, x + Math.min(w, i + 2), y + h, alpha << 24 | accent & 16777215);
            context.fill(x + i, topB, x + Math.min(w, i + 2), y + h, alpha / 2 << 24 | accent & 16777215);
         }
      }
   },
   STARFIELD("Starfield") {
      @Override
      public void draw(GuiGraphics context, int x, int y, int w, int h, int base, int accent, double time, float beat) {
         ThemeRenderer.fillRounded(context, x, y, w, h, base, 6);
         int stars = Math.max(10, w / 6);

         for (int i = 0; i < stars; i++) {
            float lane = PanelBackground.fract(i * 0.6180339F);
            float speed = 0.25F + PanelBackground.fract(i * 0.3141592F) * 0.75F;
            float px = PanelBackground.fract((float)(1.0 - (time * speed * 0.08 + i * 0.137)));
            int sx = x + Math.round(px * w);
            int sy = y + Math.round(lane * h);
            int size = PanelBackground.fract(i * 0.7182818F) > 0.8F ? 2 : 1;
            int alpha = Math.round((90.0F + beat * 90.0F) * (0.4F + PanelBackground.fract(i * 0.271F) * 0.6F));
            context.fill(sx, sy, sx + size, sy + size, Math.min(255, alpha) << 24 | accent & 16777215);
         }
      }
   };

   private static final int RADIUS = 6;
   public final String title;

   private PanelBackground(String title) {
      this.title = title;
   }

   public abstract void draw(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, double d, float f);

   private static float fract(float v) {
      return v - (float)Math.floor(v);
   }
}
