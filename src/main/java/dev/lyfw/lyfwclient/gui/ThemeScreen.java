package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ThemeScreen extends Screen {
   private static final int WHEEL_RADIUS = 64;
   private static final int WHEEL_BLOCK = 3;
   private static final int SLIDER_WIDTH = 18;
   private static final int SLIDER_HEIGHT = 128;
   private static final int LIST_WIDTH = 120;
   private static final int LIST_ROW_H = 16;
   private static final int LIST_ROW_GAP = 3;
   private static final int LABEL_H = 12;
   private static final int SECTION_GAP = 10;
   private final Screen parent;
   private float hue;
   private float saturation;
   private float value;
   private boolean draggingWheel;
   private boolean draggingSlider;
   private int panelLeft;
   private int panelTop;
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int panelWidth;
   private int panelHeight;
   private int listLeft;
   private int themeListTop;
   private int wheelCx;
   private int wheelCy;
   private int sliderLeft;
   private int sliderTop;
   private int resetLeft;
   private int resetTop;
   private int effectsTop;
   private int resetWidth;
   private int resetHeight;
   private int swatchLeft;
   private int swatchTop;
   private int swatchSize;
   private String hoveredDescription;
   private int rowH = 15;

   public ThemeScreen(Screen parent) {
      super(Component.literal("GUI Theme"));
      this.parent = parent;
      float[] hsv = rgbToHsv(Theme.accent());
      this.hue = hsv[0];
      this.saturation = hsv[1];
      this.value = hsv[2];
   }

   private int listHeight(int rows) {
      return rows * this.rowH + (rows - 1) * 2;
   }

   private void computeLayout() {
      this.swatchSize = 24;
      this.panelWidth = 380 + this.swatchSize + 12;
      int rows = Theme.Preset.values().length;
      int chrome = 106;
      int listRoom = this.height - 8 - chrome - 2 * (rows - 1);
      this.rowH = Mth.clamp(rows > 0 ? listRoom / rows : 15, 11, 15);
      int contentHeight = Math.max(12 + this.listHeight(rows), 128);
      this.panelHeight = 36 + contentHeight + 8 + 10 + 8 + 20 + 12;
      this.panelLeft = Math.max(2, this.width / 2 - this.panelWidth / 2);
      this.panelTop = Math.max(2, this.height / 2 - this.panelHeight / 2) - this.open.drop(20);
      int contentTop = this.panelTop + 24 + 12;
      this.listLeft = this.panelLeft + 12;
      this.themeListTop = contentTop + 12;
      this.wheelCx = this.listLeft + 88 + 14 + 64;
      this.wheelCy = contentTop + 64;
      this.sliderLeft = this.wheelCx + 64 + 14;
      this.sliderTop = contentTop;
      this.swatchLeft = this.sliderLeft + 18 + 10;
      this.swatchTop = contentTop;
      this.effectsTop = contentTop + contentHeight + 8 + 10 + 4;
      this.resetWidth = 150;
      this.resetHeight = 20;
      this.resetLeft = this.panelLeft + (this.panelWidth - this.resetWidth) / 2;
      this.resetTop = this.effectsTop + 24;
   }

   private int[] effectBounds(int index) {
      int w = 108;
      int gap = 8;
      int startX = this.panelLeft + (this.panelWidth - (w * 2 + gap)) / 2;
      return new int[]{startX + index * (w + gap), this.effectsTop, w, 18};
   }

   private void renderEffectToggles(GuiGraphics context, int mouseX, int mouseY) {
      String[] labels = new String[]{"Shadow: " + (Theme.shadowsEnabled() ? "ON" : "OFF"), "Dim: " + (Theme.scrimEnabled() ? "ON" : "OFF")};
      boolean[] states = new boolean[]{Theme.shadowsEnabled(), Theme.scrimEnabled()};

      for (int i = 0; i < 2; i++) {
         int[] b = this.effectBounds(i);
         boolean hovered = mouseX >= b[0] && mouseX < b[0] + b[2] && mouseY >= b[1] && mouseY < b[1] + b[3];
         ThemeRenderer.tab(context, b[0], b[1], b[2], b[3], states[i], hovered, Theme.accent(), Theme.trackBg(), Theme.rowBgHover(), 4);
         int tw = this.font.width(labels[i]);
         int color = states[i] ? ThemeRenderer.onAccent() : ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary());
         context.drawString(this.font, labels[i], b[0] + (b[2] - tw) / 2, b[1] + 5, color, !states[i]);
      }
   }

   private int[] themeRowBounds(int index) {
      return new int[]{this.listLeft, this.themeListTop + index * (this.rowH + 2), 88, this.rowH};
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      super.render(context, mouseX, mouseY, deltaTicks);
      this.hoveredDescription = null;
      ThemeRenderer.fillRounded(context, this.panelLeft + 2, this.panelTop + 2, this.panelWidth, this.panelHeight, Theme.shadow(), 3);
      ThemeRenderer.panel(context, this.panelLeft, this.panelTop, this.panelWidth, this.panelHeight, Theme.modalBorder(), Theme.modalPanelBg(), 3);
      ThemeRenderer.fillRoundedTop(context, this.panelLeft + 1, this.panelTop + 1, this.panelWidth - 2, 24, Theme.modalHeaderBg(), 2);
      context.drawCenteredString(this.font, "GUI Theme", this.panelLeft + this.panelWidth / 2, this.panelTop + 8, Theme.textPrimary());
      this.renderThemeList(context, mouseX, mouseY);
      this.renderWheel(context);
      this.renderSlider(context);
      this.renderSwatch(context);
      this.renderDescription(context);
      this.renderEffectToggles(context, mouseX, mouseY);
      this.renderResetButton(context, mouseX, mouseY);
   }

   private void renderThemeList(GuiGraphics context, int mouseX, int mouseY) {
      context.drawString(this.font, "COLORS", this.listLeft, this.themeListTop - 11, Theme.sectionText());
      Theme.Preset[] presets = Theme.Preset.values();

      for (int i = 0; i < presets.length; i++) {
         int[] b = this.themeRowBounds(i);
         boolean active = presets[i] == Theme.preset();
         boolean hovered = isInside(mouseX, mouseY, b[0], b[1], b[2], b[3]);
         if (hovered) {
            this.hoveredDescription = presets[i].description;
         }

         if (active) {
            ThemeRenderer.activeRow(context, b[0], b[1], b[2], b[3], Theme.accent(), 3);
         } else {
            ThemeRenderer.row(context, b[0], b[1], b[2], b[3], hovered, Theme.trackBg(), Theme.rowBgHover(), 3);
         }

         int textColor = active ? ThemeRenderer.onAccent() : ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary());
         context.drawString(this.font, presets[i].title, b[0] + 7, b[1] + (this.rowH - 8) / 2, textColor);
         int dotX = b[0] + b[2] - 7 - 5;
         ThemeRenderer.fillRounded(context, dotX, b[1] + (this.rowH - 5) / 2, 5, 5, presets[i].palette.defaultAccent(), 2);
      }
   }

   private void renderDescription(GuiGraphics context) {
      String text = this.hoveredDescription != null ? this.hoveredDescription : Theme.preset().description;
      context.drawString(this.font, this.font.plainSubstrByWidth(text, this.panelWidth - 24), this.panelLeft + 12, this.resetTop - 22, Theme.textSecondary());
   }

   private void renderWheel(GuiGraphics context) {
      int size = ColorWheel.size(64);
      context.blit(RenderPipelines.GUI_TEXTURED, ColorWheel.texture(64), this.wheelCx - 64, this.wheelCy - 64, 0.0F, 0.0F, size, size, size, size, -1);
      double markerAngle = Math.toRadians(this.hue);
      double markerDist = this.saturation * 64.0F;
      int markerX = this.wheelCx + (int)Math.round(Math.cos(markerAngle) * markerDist);
      int markerY = this.wheelCy + (int)Math.round(Math.sin(markerAngle) * markerDist);
      context.fill(markerX - 3, markerY - 3, markerX + 4, markerY + 4, -16777216);
      context.fill(markerX - 2, markerY - 2, markerX + 3, markerY + 3, -1);
   }

   private void renderSlider(GuiGraphics context) {
      int hueRgb = hsvToRgb(this.hue, this.saturation, 1.0F);
      context.fillGradient(this.sliderLeft, this.sliderTop, this.sliderLeft + 18, this.sliderTop + 128, hueRgb, mixWithBlack(hueRgb, 0.0F));
      int handleY = this.sliderTop + Math.round((1.0F - this.value) * 127.0F);
      context.fill(this.sliderLeft - 2, handleY - 1, this.sliderLeft + 20, handleY + 2, -16777216);
      context.fill(this.sliderLeft - 1, handleY, this.sliderLeft + 19, handleY + 1, -1);
   }

   private static int mixWithBlack(int rgb, float fraction) {
      int r = Math.round((rgb >> 16 & 0xFF) * fraction);
      int g = Math.round((rgb >> 8 & 0xFF) * fraction);
      int b = Math.round((rgb & 0xFF) * fraction);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private void renderSwatch(GuiGraphics context) {
      int color = hsvToRgb(this.hue, this.saturation, this.value);
      ThemeRenderer.fillRoundedBorder(context, this.swatchLeft, this.swatchTop, this.swatchSize, this.swatchSize, Theme.modalBorder(), color, 2);
      String hex = String.format("#%06X", color & 16777215);
      context.drawString(this.font, hex, this.swatchLeft - 8, this.swatchTop + this.swatchSize + 6, Theme.textSecondary());
   }

   private void renderResetButton(GuiGraphics context, int mouseX, int mouseY) {
      boolean hovered = isInside(mouseX, mouseY, this.resetLeft, this.resetTop, this.resetWidth, this.resetHeight);
      ThemeRenderer.row(context, this.resetLeft, this.resetTop, this.resetWidth, this.resetHeight, hovered, Theme.dangerBg(), Theme.dangerBg(), 2);
      String label = "Reset Layout + Theme";
      int textWidth = this.font.width(label);
      context.drawString(
         this.font,
         label,
         this.resetLeft + (this.resetWidth - textWidth) / 2,
         this.resetTop + 6,
         ThemeRenderer.rowTextColor(hovered, Theme.danger(), Theme.danger())
      );
   }

   private static boolean isInside(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   private void syncFromAccent() {
      float[] hsv = rgbToHsv(Theme.accent());
      this.hue = hsv[0];
      this.saturation = hsv[1];
      this.value = hsv[2];
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)click.x();
      int my = (int)click.y();
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         for (int i = 0; i < 2; i++) {
            int[] e = this.effectBounds(i);
            if (isInside(mx, my, e[0], e[1], e[2], e[3])) {
               if (i == 0) {
                  Theme.setShadows(!Theme.shadowsEnabled());
               } else {
                  Theme.setScrim(!Theme.scrimEnabled());
               }

               Config.save();
               return true;
            }
         }

         Theme.Preset[] presets = Theme.Preset.values();

         for (int ix = 0; ix < presets.length; ix++) {
            int[] b = this.themeRowBounds(ix);
            if (isInside(mx, my, b[0], b[1], b[2], b[3])) {
               Theme.setPreset(presets[ix]);
               this.syncFromAccent();
               Config.save();
               return true;
            }
         }

         if (this.isInsideWheel(mx, my)) {
            this.draggingWheel = true;
            this.updateFromWheel(mx, my);
            return true;
         } else if (this.isInsideSlider(mx, my)) {
            this.draggingSlider = true;
            this.updateFromSlider(my);
            return true;
         } else if (isInside(mx, my, this.resetLeft, this.resetTop, this.resetWidth, this.resetHeight)) {
            Theme.resetAll();
            this.syncFromAccent();
            Config.save();
            return true;
         } else {
            return super.mouseClicked(click, doubled);
         }
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      int mx = (int)click.x();
      int my = (int)click.y();
      if (this.draggingWheel) {
         this.updateFromWheel(mx, my);
         return true;
      } else if (this.draggingSlider) {
         this.updateFromSlider(my);
         return true;
      } else {
         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      boolean wasDragging = this.draggingWheel || this.draggingSlider;
      this.draggingWheel = false;
      this.draggingSlider = false;
      if (wasDragging) {
         Config.save();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   private boolean isInsideWheel(int mx, int my) {
      double dist = Math.sqrt(Math.pow(mx - this.wheelCx, 2.0) + Math.pow(my - this.wheelCy, 2.0));
      return dist <= 64.0;
   }

   private boolean isInsideSlider(int mx, int my) {
      return mx >= this.sliderLeft - 4 && mx < this.sliderLeft + 22 && my >= this.sliderTop && my < this.sliderTop + 128;
   }

   private void updateFromWheel(int mx, int my) {
      int dx = mx - this.wheelCx;
      int dy = my - this.wheelCy;
      double dist = Math.min(64.0, Math.sqrt((double)dx * dx + (double)dy * dy));
      this.hue = angleToHue(dx, dy);
      this.saturation = (float)(dist / 64.0);
      this.applyLive();
   }

   private void updateFromSlider(int my) {
      float fraction = 1.0F - (my - this.sliderTop) / 127.0F;
      this.value = Math.max(0.0F, Math.min(1.0F, fraction));
      this.applyLive();
   }

   private void applyLive() {
      Theme.setAccent(hsvToRgb(this.hue, this.saturation, this.value));
   }

   private static float angleToHue(int x, int y) {
      double degrees = Math.toDegrees(Math.atan2(y, x));
      if (degrees < 0.0) {
         degrees += 360.0;
      }

      return (float)degrees;
   }

   private static int hsvToRgb(float hue, float saturation, float value) {
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

      int ri = Math.round((r + m) * 255.0F);
      int gi = Math.round((g + m) * 255.0F);
      int bi = Math.round((b + m) * 255.0F);
      return 0xFF000000 | ri << 16 | gi << 8 | bi;
   }

   private static float[] rgbToHsv(int argb) {
      int r = argb >> 16 & 0xFF;
      int g = argb >> 8 & 0xFF;
      int b = argb & 0xFF;
      float rf = r / 255.0F;
      float gf = g / 255.0F;
      float bf = b / 255.0F;
      float max = Math.max(rf, Math.max(gf, bf));
      float min = Math.min(rf, Math.min(gf, bf));
      float delta = max - min;
      float h;
      if (delta == 0.0F) {
         h = 0.0F;
      } else if (max == rf) {
         h = 60.0F * ((gf - bf) / delta % 6.0F);
      } else if (max == gf) {
         h = 60.0F * ((bf - rf) / delta + 2.0F);
      } else {
         h = 60.0F * ((rf - gf) / delta + 4.0F);
      }

      if (h < 0.0F) {
         h += 360.0F;
      }

      float s = max == 0.0F ? 0.0F : delta / max;
      return new float[]{h, s, max};
   }

   public boolean keyPressed(KeyEvent input) {
      if (input.key() == 256) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   public void onClose() {
      Config.save();
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent instanceof ClickGuiRoot ? Theme.layout().create() : this.parent);
      }
   }
}
