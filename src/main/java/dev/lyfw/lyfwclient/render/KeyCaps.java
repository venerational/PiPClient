package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.gui.RoundedCorners;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class KeyCaps {
   private final Map<String, Float> levels = new HashMap<>();
   private long lastFrame;
   private float delta;
   private float fade = 0.15F;

   public void begin(float fadeSeconds) {
      long now = System.nanoTime();
      this.delta = this.lastFrame == 0L ? 0.0F : Math.min(0.25F, (float)(now - this.lastFrame) / 1.0E9F);
      this.lastFrame = now;
      this.fade = Math.max(0.02F, fadeSeconds);
   }

   private float level(String id, boolean pressed) {
      float now = pressed ? 1.0F : Math.max(0.0F, this.levels.getOrDefault(id, 0.0F) - this.delta / this.fade);
      this.levels.put(id, now);
      return now;
   }

   public void draw(GuiGraphics context, String id, int x, int y, int w, int h, String label, boolean pressed, KeyCaps.Style style) {
      float lit = this.level(id, pressed);
      int fill = lerp(style.rest(), style.pressed(), lit);
      int radius = Math.max(0, Math.min(style.radius(), Math.min(w, h) / 2));
      if (lit <= 0.01F) {
         roundedRect(context, x, y, w, h, fill, radius);
      } else {
         int alpha = Math.round((style.outline() >>> 24 & 0xFF) * lit);
         roundedRect(context, x, y, w, h, alpha << 24 | style.outline() & 16777215, radius);
         roundedRect(context, x + 1, y + 1, w - 2, h - 2, fill, Math.max(0, radius - 1));
      }

      label(context, x, y, w, h, label, lerp(style.restText(), style.pressedText(), lit));
   }

   private static void label(GuiGraphics context, int x, int y, int w, int h, String text, int color) {
      Minecraft mc = Minecraft.getInstance();
      int width = mc.font.width(text);
      int room = w - 4;
      float scale = width > room && width > 0 ? (float)room / width : 1.0F;
      if (scale >= 0.999F) {
         context.drawString(mc.font, text, x + (w - width) / 2, y + (h - 8) / 2, color, false);
      } else {
         context.pose().pushMatrix();
         context.pose().translate(x + (w - width * scale) / 2.0F, y + (h - 8.0F * scale) / 2.0F);
         context.pose().scale(scale, scale);
         context.drawString(mc.font, text, 0, 0, color, false);
         context.pose().popMatrix();
      }
   }

   public static int lerp(int from, int to, float t) {
      if (t <= 0.0F) {
         return from;
      } else if (t >= 1.0F) {
         return to;
      } else {
         int out = 0;

         for (int shift = 0; shift < 32; shift += 8) {
            int a = from >>> shift & 0xFF;
            int b = to >>> shift & 0xFF;
            out |= Math.round(a + (b - a) * t) << shift;
         }

         return out;
      }
   }

   private static void roundedRect(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      if ((color >>> 24 & 0xFF) != 0 && w > 0 && h > 0) {
         int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));
         Identifier disc = r == 0 ? null : RoundedCorners.of(r);
         if (disc == null) {
            context.fill(x, y, x + w, y + h, color);
         } else {
            int d = r * 2;
            context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y, 0.0F, 0.0F, r, r, d, d, color);
            context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y, r, 0.0F, r, r, d, d, color);
            context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y + h - r, 0.0F, r, r, r, d, d, color);
            context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y + h - r, r, r, r, r, d, d, color);
            if (w > d) {
               context.fill(x + r, y, x + w - r, y + r, color);
               context.fill(x + r, y + h - r, x + w - r, y + h, color);
            }

            if (h > d) {
               context.fill(x, y + r, x + w, y + h - r, color);
            }
         }
      }
   }

   public record Style(int rest, int pressed, int restText, int pressedText, int outline, int radius) {
   }
}
