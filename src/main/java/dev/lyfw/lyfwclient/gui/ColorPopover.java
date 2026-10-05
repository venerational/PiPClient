package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.setting.ColorSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;

final class ColorPopover {
   static final int W = 132;
   static final int H = 122;
   private static final int RADIUS = 30;
   private static final int[] PRESETS = new int[]{-1, -6643532, -13948109, -15724524, -1598795, -1750963, -680437, -340971, -13315175, -10443270};
   private ColorSetting setting;
   private int x;
   private int y;
   private float hue;
   private float sat;
   private float val;
   private boolean draggingWheel;
   private boolean draggingValue;
   private final Anim open = new Anim(0.14F);

   boolean isOpen() {
      return this.setting != null;
   }

   ColorSetting setting() {
      return this.setting;
   }

   void open(ColorSetting setting, int anchorX, int anchorY, int screenW, int screenH) {
      this.setting = setting;
      float[] hsv = rgbToHsv(setting.raw());
      this.hue = hsv[0];
      this.sat = hsv[1];
      this.val = hsv[2];
      this.x = Mth.clamp(anchorX, 4, Math.max(4, screenW - 132 - 4));
      this.y = anchorY + 122 + 4 > screenH ? Math.max(4, anchorY - 122 - 18) : anchorY;
      this.open.restart();
   }

   void close() {
      this.setting = null;
      this.draggingWheel = false;
      this.draggingValue = false;
   }

   private int top() {
      return this.y + this.open.drop(6);
   }

   private int wheelX() {
      return this.x + 8 + 30;
   }

   private int wheelY() {
      return this.top() + 20 + 30;
   }

   private int[] valueBounds() {
      return new int[]{this.x + 8 + 60 + 10, this.top() + 20, 10, 61};
   }

   private int[] presetBounds(int i) {
      return new int[]{this.x + 8 + i % 5 * 24, this.top() + 90 + i / 5 * 14, 20, 10};
   }

   void render(GuiGraphics context, Font tr, int mouseX, int mouseY) {
      if (this.setting != null) {
         int top = this.top();
         ThemeRenderer.fillRounded(context, this.x + 2, top + 3, 132, 122, Theme.shadow(), 9);
         ThemeRenderer.fillRoundedBorder(context, this.x, top, 132, 122, Theme.modalBorder(), Theme.modalPanelBg(), 9);
         context.drawString(tr, tr.plainSubstrByWidth(this.setting.name, 80), this.x + 8, top + 7, Theme.textPrimary(), false);
         String hex = String.format("#%06X", this.setting.raw() & 16777215);
         context.drawString(tr, hex, this.x + 132 - 8 - tr.width(hex), top + 7, Theme.textSecondary(), false);
         int size = ColorWheel.size(30);
         context.blit(RenderPipelines.GUI_TEXTURED, ColorWheel.texture(30), this.wheelX() - 30, this.wheelY() - 30, 0.0F, 0.0F, size, size, size, size, -1);
         double angle = Math.toRadians(this.hue);
         int mx = this.wheelX() + (int)Math.round(Math.cos(angle) * this.sat * 30.0);
         int my = this.wheelY() + (int)Math.round(Math.sin(angle) * this.sat * 30.0);
         ThemeRenderer.fillRounded(context, mx - 3, my - 3, 7, 7, -16777216, 3);
         ThemeRenderer.fillRounded(context, mx - 2, my - 2, 5, 5, -1, 2);
         int[] v = this.valueBounds();
         int bright = hsvToRgb(this.hue, this.sat, 1.0F);
         context.fillGradient(v[0], v[1], v[0] + v[2], v[1] + v[3], bright, -16777216);
         int handleY = v[1] + Math.round((1.0F - this.val) * (v[3] - 1));
         context.fill(v[0] - 2, handleY - 1, v[0] + v[2] + 2, handleY + 2, -16777216);
         context.fill(v[0] - 1, handleY, v[0] + v[2] + 1, handleY + 1, -1);
         int previewX = v[0] + v[2] + 8;
         ThemeRenderer.fillRounded(context, previewX, v[1], this.x + 132 - 8 - previewX, v[3], Theme.border(), 5);
         ThemeRenderer.fillRounded(context, previewX + 1, v[1] + 1, this.x + 132 - 10 - previewX, v[3] - 2, 0xFF000000 | this.setting.raw(), 4);

         for (int i = 0; i < PRESETS.length; i++) {
            int[] b = this.presetBounds(i);
            Ui.swatch(context, b[0], b[1], b[2], b[3], PRESETS[i], Ui.inside(mouseX, mouseY, b));
         }
      }
   }

   boolean mouseClicked(double mx, double my) {
      if (this.setting == null) {
         return false;
      } else if (!Ui.inside(mx, my, this.x, this.top(), 132, 122)) {
         this.close();
         return false;
      } else {
         double dx = mx - this.wheelX();
         double dy = my - this.wheelY();
         if (dx * dx + dy * dy <= 1024.0) {
            this.draggingWheel = true;
            this.mouseDragged(mx, my);
         } else if (Ui.inside(mx, my, this.valueBounds()[0] - 3, this.valueBounds()[1], this.valueBounds()[2] + 6, this.valueBounds()[3])) {
            this.draggingValue = true;
            this.mouseDragged(mx, my);
         } else {
            for (int i = 0; i < PRESETS.length; i++) {
               if (Ui.inside(mx, my, this.presetBounds(i))) {
                  this.setting.set(0xFF000000 | PRESETS[i]);
                  float[] hsv = rgbToHsv(PRESETS[i]);
                  this.hue = hsv[0];
                  this.sat = hsv[1];
                  this.val = hsv[2];
               }
            }
         }

         return true;
      }
   }

   boolean mouseDragged(double mx, double my) {
      if (this.draggingWheel) {
         double dx = mx - this.wheelX();
         double dy = my - this.wheelY();
         double degrees = Math.toDegrees(Math.atan2(dy, dx));
         this.hue = (float)(degrees < 0.0 ? degrees + 360.0 : degrees);
         this.sat = (float)Math.min(1.0, Math.sqrt(dx * dx + dy * dy) / 30.0);
         if (this.val < 0.05F) {
            this.val = 1.0F;
         }
      } else {
         if (!this.draggingValue) {
            return false;
         }

         int[] v = this.valueBounds();
         this.val = Mth.clamp(1.0F - (float)(my - v[1]) / (v[3] - 1), 0.0F, 1.0F);
      }

      this.setting.set(this.setting.raw() & 0xFF000000 | hsvToRgb(this.hue, this.sat, this.val) & 16777215);
      return true;
   }

   void mouseReleased() {
      this.draggingWheel = false;
      this.draggingValue = false;
   }

   static int hsvToRgb(float hue, float saturation, float value) {
      float c = value * saturation;
      float h = (hue % 360.0F + 360.0F) % 360.0F / 60.0F;
      float x = c * (1.0F - Math.abs(h % 2.0F - 1.0F));
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;
      switch ((int)h) {
         case 0:
            r = c;
            g = x;
            break;
         case 1:
            r = x;
            g = c;
            break;
         case 2:
            g = c;
            b = x;
            break;
         case 3:
            g = x;
            b = c;
            break;
         case 4:
            r = x;
            b = c;
            break;
         default:
            r = c;
            b = x;
      }

      float m = value - c;
      return 0xFF000000 | Math.round((r + m) * 255.0F) << 16 | Math.round((g + m) * 255.0F) << 8 | Math.round((b + m) * 255.0F);
   }

   static float[] rgbToHsv(int argb) {
      float r = (argb >> 16 & 0xFF) / 255.0F;
      float g = (argb >> 8 & 0xFF) / 255.0F;
      float b = (argb & 0xFF) / 255.0F;
      float max = Math.max(r, Math.max(g, b));
      float delta = max - Math.min(r, Math.min(g, b));
      float h;
      if (delta == 0.0F) {
         h = 0.0F;
      } else if (max == r) {
         h = 60.0F * ((g - b) / delta % 6.0F);
      } else if (max == g) {
         h = 60.0F * ((b - r) / delta + 2.0F);
      } else {
         h = 60.0F * ((r - g) / delta + 4.0F);
      }

      return new float[]{h < 0.0F ? h + 360.0F : h, max == 0.0F ? 0.0F : delta / max, max};
   }
}
