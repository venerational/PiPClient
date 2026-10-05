package dev.lyfw.lyfwclient.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class ThemeRenderer {
   private static final Identifier BUTTON = Identifier.withDefaultNamespace("widget/button");
   private static final Identifier BUTTON_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/button_highlighted");
   private static final Identifier BUTTON_DISABLED = Identifier.withDefaultNamespace("widget/button_disabled");
   private static final Identifier TAB_SELECTED = Identifier.withDefaultNamespace("widget/tab_selected");
   private static final Identifier TAB = Identifier.withDefaultNamespace("widget/tab");
   private static final Identifier TAB_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/tab_highlighted");
   private static final int MC_TEXT_HOVER = -96;
   public static final int STAR_W = 10;
   private static final int STAR_ON = -14017;
   private static final int STAR_HOVER = -8054;

   private ThemeRenderer() {
   }

   public static int radius(int requested) {
      return Theme.isMinecraftStyle() ? 0 : requested;
   }

   public static void panel(GuiGraphics context, int x, int y, int w, int h, int borderColor, int fillColor, int requestedRadius) {
      if (!Theme.isMinecraftStyle()) {
         fillRoundedBorder(context, x, y, w, h, borderColor, fillColor, requestedRadius);
      } else {
         context.fill(x, y, x + w, y + h, fillColor);
         bevel(context, x, y, w, h);
      }
   }

   public static void row(GuiGraphics context, int x, int y, int w, int h, boolean hovered, int idleColor, int hoverColor, int requestedRadius) {
      if (!Theme.isMinecraftStyle()) {
         fillRounded(context, x, y, w, h, hovered ? hoverColor : idleColor, requestedRadius);
      } else {
         context.blitSprite(RenderPipelines.GUI_TEXTURED, hovered ? BUTTON_HIGHLIGHTED : BUTTON, x, y, w, h);
      }
   }

   public static void activeRow(GuiGraphics context, int x, int y, int w, int h, int accentColor, int requestedRadius) {
      if (!Theme.isMinecraftStyle()) {
         fillRounded(context, x, y, w, h, accentColor, requestedRadius);
      } else {
         context.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_HIGHLIGHTED, x, y, w, h);
      }
   }

   public static void tab(
      GuiGraphics context, int x, int y, int w, int h, boolean active, boolean hovered, int activeColor, int idleColor, int hoverColor, int requestedRadius
   ) {
      if (!Theme.isMinecraftStyle()) {
         fillRounded(context, x, y, w, h, active ? activeColor : (hovered ? hoverColor : idleColor), requestedRadius);
      } else {
         Identifier sprite = active ? TAB_SELECTED : (hovered ? TAB_HIGHLIGHTED : TAB);
         context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, w, h);
      }
   }

   public static void disabledSurface(GuiGraphics context, int x, int y, int w, int h, int color, int requestedRadius) {
      if (!Theme.isMinecraftStyle()) {
         fillRounded(context, x, y, w, h, color, requestedRadius);
      } else {
         context.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_DISABLED, x, y, w, h);
      }
   }

   public static int rowTextColor(boolean hovered, int idleColor, int hoverColor) {
      if (!Theme.isMinecraftStyle()) {
         return hovered ? hoverColor : idleColor;
      } else {
         return hovered ? -96 : -1;
      }
   }

   public static int onAccent() {
      return Theme.isMinecraftStyle() ? -1 : Theme.accentText();
   }

   public static void star(GuiGraphics context, Font textRenderer, int x, int y, boolean favorite, boolean hovered) {
      int color = favorite ? -14017 : (hovered ? -8054 : Theme.textSecondary());
      AuroraIcons.draw(context, favorite ? "star" : "star_outline", x, y - 1, 10, color);
   }

   public static boolean inStar(int mouseX, int mouseY, int x, int y) {
      return mouseX >= x && mouseX < x + 10 && mouseY >= y - 1 && mouseY < y + 10 + 1;
   }

   private static void bevel(GuiGraphics context, int x, int y, int w, int h) {
      context.fill(x, y, x + w, y + 1, -16777216);
      context.fill(x, y + h - 1, x + w, y + h, -16777216);
      context.fill(x, y, x + 1, y + h, -16777216);
      context.fill(x + w - 1, y, x + w, y + h, -16777216);
      context.fill(x + 1, y + 1, x + w - 1, y + 2, 1358954495);
      context.fill(x + 1, y + 1, x + 2, y + h - 1, 1358954495);
      context.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, 1342177280);
      context.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, 1342177280);
   }

   public static void fillRounded(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      int r = Math.max(0, Math.min(radius(radius), Math.min(w, h) / 2));
      Identifier disc = r == 0 ? null : RoundedCorners.of(r);
      if (disc == null) {
         context.fill(x, y, x + w, y + h, color);
      } else {
         corners(context, disc, x, y, w, h, r, color);
         if (w > r * 2) {
            context.fill(x + r, y, x + w - r, y + r, color);
            context.fill(x + r, y + h - r, x + w - r, y + h, color);
         }

         if (h > r * 2) {
            context.fill(x, y + r, x + w, y + h - r, color);
         }
      }
   }

   public static void fillRoundedTop(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      int r = Math.max(0, Math.min(radius(radius), Math.min(w / 2, h)));
      Identifier disc = r == 0 ? null : RoundedCorners.of(r);
      if (disc == null) {
         context.fill(x, y, x + w, y + h, color);
      } else {
         int d = r * 2;
         context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y, 0.0F, 0.0F, r, r, d, d, color);
         context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y, r, 0.0F, r, r, d, d, color);
         if (w > r * 2) {
            context.fill(x + r, y, x + w - r, y + r, color);
         }

         if (h > r) {
            context.fill(x, y + r, x + w, y + h, color);
         }
      }
   }

   private static void corners(GuiGraphics context, Identifier disc, int x, int y, int w, int h, int r, int color) {
      int d = r * 2;
      context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y, 0.0F, 0.0F, r, r, d, d, color);
      context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y, r, 0.0F, r, r, d, d, color);
      context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y + h - r, 0.0F, r, r, r, d, d, color);
      context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y + h - r, r, r, r, r, d, d, color);
   }

   public static void fillRoundedBorder(GuiGraphics context, int x, int y, int w, int h, int borderColor, int fillColor, int radius) {
      fillRounded(context, x, y, w, h, borderColor, radius);
      fillRounded(context, x + 1, y + 1, w - 2, h - 2, fillColor, Math.max(0, radius(radius) - 1));
   }

   public static void fillGradientRounded(GuiGraphics context, int x, int y, int w, int h, int radius, int colorA, int colorB) {
      int r = Math.max(0, Math.min(radius(radius), Math.min(w, h) / 2));
      Identifier disc = r == 0 ? null : RoundedCorners.of(r);
      if (disc == null) {
         context.fillGradient(x, y, x + w, y + h, colorA, colorB);
      } else {
         int d = r * 2;
         int top = lerpColor(colorA, colorB, r / (2.0F * h));
         int bottom = lerpColor(colorA, colorB, 1.0F - r / (2.0F * h));
         context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y, 0.0F, 0.0F, r, r, d, d, top);
         context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y, r, 0.0F, r, r, d, d, top);
         context.blit(RenderPipelines.GUI_TEXTURED, disc, x, y + h - r, 0.0F, r, r, r, d, d, bottom);
         context.blit(RenderPipelines.GUI_TEXTURED, disc, x + w - r, y + h - r, r, r, r, r, d, d, bottom);
         if (w > r * 2) {
            context.fillGradient(x + r, y, x + w - r, y + r, colorA, top);
            context.fillGradient(x + r, y + h - r, x + w - r, y + h, bottom, colorB);
         }

         if (h > r * 2) {
            context.fillGradient(x, y + r, x + w, y + h - r, top, bottom);
         }
      }
   }

   private static int lerpColor(int colorA, int colorB, float t) {
      t = Mth.clamp(t, 0.0F, 1.0F);
      int a = lerpChannel(colorA >> 24 & 0xFF, colorB >> 24 & 0xFF, t);
      int r = lerpChannel(colorA >> 16 & 0xFF, colorB >> 16 & 0xFF, t);
      int g = lerpChannel(colorA >> 8 & 0xFF, colorB >> 8 & 0xFF, t);
      int b = lerpChannel(colorA & 0xFF, colorB & 0xFF, t);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int lerpChannel(int a, int b, float t) {
      return Math.round(a + (b - a) * t);
   }
}
