package dev.lyfw.lyfwclient.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

public final class Ui {
   public static final int TOGGLE_W = 24;
   public static final int TOGGLE_H = 13;
   public static final int SLIDER_H = 20;
   private static final long START = System.nanoTime();
   private static final Identifier SLIDER = Identifier.withDefaultNamespace("widget/slider");
   private static final Identifier SLIDER_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/slider_highlighted");
   private static final Identifier HANDLE = Identifier.withDefaultNamespace("widget/slider_handle");
   private static final Identifier HANDLE_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/slider_handle_highlighted");

   private Ui() {
   }

   public static boolean inside(double mx, double my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public static boolean inside(double mx, double my, int[] b) {
      return inside(mx, my, b[0], b[1], b[2], b[3]);
   }

   public static float seconds() {
      return (float)(System.nanoTime() - START) / 1.0E9F;
   }

   public static float ease(float t) {
      float u = 1.0F - Mth.clamp(t, 0.0F, 1.0F);
      return 1.0F - u * u * u;
   }

   public static float stagger(float elapsed, int index, float step, float duration) {
      return ease((elapsed - Math.min(index, 14) * step) / duration);
   }

   public static int alpha(int argb, float p) {
      return Math.round((argb >>> 24) * Mth.clamp(p, 0.0F, 1.0F)) << 24 | argb & 16777215;
   }

   public static int mix(int a, int b, float t) {
      t = Mth.clamp(t, 0.0F, 1.0F);
      int aa = Math.round((a >>> 24) + ((b >>> 24) - (a >>> 24)) * t);
      int r = Math.round((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * t);
      int g = Math.round((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * t);
      int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
      return aa << 24 | r << 16 | g << 8 | bl;
   }

   public static int rowColor(boolean hovered) {
      if (Theme.isMinecraftStyle()) {
         return hovered ? -802148298 : -1206643688;
      } else {
         return hovered ? Theme.rowBgHover() : Theme.rowBg();
      }
   }

   public static void surface(GuiGraphics context, int x, int y, int w, int h, int radius, int border, int top, int bottom, boolean hovered, float p) {
      if (Theme.isMinecraftStyle()) {
         int edge = alpha(hovered ? mix(border, -1, 0.45F) : border, p);
         context.fill(x + 1, y + 1, x + w - 1, y + h - 1, alpha(rowColor(hovered), p));
         context.fill(x, y, x + w, y + 1, edge);
         context.fill(x, y + h - 1, x + w, y + h, edge);
         context.fill(x, y + 1, x + 1, y + h - 1, edge);
         context.fill(x + w - 1, y + 1, x + w, y + h - 1, edge);
         context.fill(x + 1, y + 1, x + w - 1, y + 2, alpha(788529151, p));
         context.fill(x + 1, y + 2, x + 2, y + h - 1, alpha(486539263, p));
         context.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, alpha(1342177280, p));
         context.fill(x + w - 2, y + 2, x + w - 1, y + h - 2, alpha(939524096, p));
      } else {
         ThemeRenderer.fillRounded(context, x, y, w, h, alpha(border, p), radius);
         ThemeRenderer.fillGradientRounded(context, x + 1, y + 1, w - 2, h - 2, Math.max(0, radius - 1), alpha(top, p), alpha(bottom, p));
      }
   }

   public static void toggle(GuiGraphics context, int x, int y, float on, boolean hovered) {
      if (Theme.isMinecraftStyle()) {
         int handleW = 8;
         int handleX = x + Math.round((24 - handleW) * on);
         context.blitSprite(RenderPipelines.GUI_TEXTURED, hovered ? SLIDER_HIGHLIGHTED : SLIDER, x, y, 24, 13);
         if (on > 0.02F) {
            context.fill(x + 2, y + 2, handleX + 2, y + 13 - 2, alpha(Theme.accent(), 0.75F * on));
         }

         context.blitSprite(RenderPipelines.GUI_TEXTURED, hovered ? HANDLE_HIGHLIGHTED : HANDLE, handleX, y, handleW, 13);
      } else {
         int idle = hovered ? Theme.rowBgHover() : Theme.trackBg();
         ThemeRenderer.fillRounded(context, x, y, 24, 13, mix(idle, Theme.accent(), on), 6);
         int knob = 9;
         int knobX = x + 2 + Math.round((20 - knob) * on);
         ThemeRenderer.fillRounded(context, knobX, y + 2, knob, knob, mix(Theme.textSecondary(), Theme.knob(), on), 4);
      }
   }

   public static void slider(GuiGraphics context, Font tr, int x, int y, int w, String label, String value, float fraction, boolean hovered) {
      context.drawString(tr, tr.plainSubstrByWidth(label, w - tr.width(value) - 6), x, y, Theme.textPrimary(), false);
      context.drawString(tr, value, x + w - tr.width(value), y, Theme.textSecondary(), false);
      int trackY = y + 14;
      int filled = Math.round(w * Mth.clamp(fraction, 0.0F, 1.0F));
      ThemeRenderer.fillRounded(context, x, trackY, w, 4, Theme.trackBg(), 2);
      ThemeRenderer.fillRounded(context, x, trackY, Math.max(4, filled), 4, Theme.accent(), 2);
      int knob = hovered ? 8 : 6;
      ThemeRenderer.fillRounded(context, x + filled - knob / 2, trackY + 2 - knob / 2, knob, knob, Theme.knob(), knob / 2);
   }

   public static double fraction(double mx, int x, int w) {
      return Mth.clamp((mx - x) / w, 0.0, 1.0);
   }

   public static void swatch(GuiGraphics context, int x, int y, int w, int h, int argb, boolean hovered) {
      ThemeRenderer.fillRounded(context, x, y, w, h, hovered ? Theme.textPrimary() : Theme.border(), 4);
      ThemeRenderer.fillRounded(context, x + 1, y + 1, w - 2, h - 2, 0xFF000000 | argb, 3);
   }

   public static void bigText(GuiGraphics context, Font tr, String text, int x, int y, float scale, int color) {
      Matrix3x2fStack matrices = context.pose();
      matrices.pushMatrix();
      matrices.translate(x, y);
      matrices.scale(scale, scale);
      context.drawString(tr, text, 0, 0, color, false);
      matrices.popMatrix();
   }

   public static void tooltip(GuiGraphics context, Font tr, String text, int centerX, int bottomY, int screenW) {
      int w = tr.width(text) + 10;
      int x = Mth.clamp(centerX - w / 2, 2, Math.max(2, screenW - w - 2));
      int y = bottomY - 15;
      ThemeRenderer.fillRounded(context, x, y, w, 13, -267382250, 4);
      context.drawString(tr, text, x + 5, y + 3, -986376, false);
   }
}
