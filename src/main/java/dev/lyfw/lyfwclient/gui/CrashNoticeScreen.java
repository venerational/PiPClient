package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.crash.CrashSummary;
import java.io.File;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

public class CrashNoticeScreen extends Screen {
   private static final int W = 300;
   private static final String[] LABELS = new String[]{"Open mods folder", "Open crash report", "Continue"};
   private final Screen parent;
   private final CrashSummary summary;
   private final Anim open = new Anim(0.22F);
   private int panelX;
   private int panelY;
   private int panelH;

   public CrashNoticeScreen(Screen parent, CrashSummary summary) {
      super(Component.literal("Minecraft crashed"));
      this.parent = parent;
      this.summary = summary;
   }

   private List<FormattedCharSequence> wrap(String text, int width) {
      return this.font.split(FormattedText.of(text == null ? "" : text), width);
   }

   private int contentHeight() {
      int textW = 250;
      int h = 12 + Math.max(24, this.wrap(this.summary.title, textW).size() * 10 + this.wrap(this.summary.subtitle, textW).size() * 10) + 10;
      h += this.summary.suspects.size() * 24 + (this.summary.suspects.isEmpty() ? 0 : 2);
      h += this.wrap(this.summary.advice, 276).size() * 10;
      h += this.summary.detail.isEmpty() ? 0 : 12;
      return h + 10 + 20 + 12;
   }

   private int[] buttonBounds(int index) {
      int gap = 6;
      int bw = (276 - gap * 2) / 3;
      return new int[]{this.panelX + 12 + index * (bw + gap), this.panelY + this.panelH - 12 - 20, bw, 20};
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.panelH = this.contentHeight();
      this.panelX = (this.width - 300) / 2;
      this.panelY = (this.height - this.panelH) / 2 - this.open.drop(16);
      context.fill(0, 0, this.width, this.height, this.open.fade(-1073741824));
      ThemeRenderer.fillRounded(context, this.panelX + 2, this.panelY + 4, 300, this.panelH, Theme.shadow(), 12);
      ThemeRenderer.fillRoundedBorder(context, this.panelX, this.panelY, 300, this.panelH, Theme.border(), Theme.panelBg(), 12);
      ThemeRenderer.fillGradientRounded(context, this.panelX + 12, this.panelY + 12, 22, 22, 6, Theme.accent(), Theme.accent2());
      context.drawCenteredString(this.font, "P", this.panelX + 23, this.panelY + 19, ThemeRenderer.onAccent());
      int textX = this.panelX + 42;
      int textW = 250;
      int y = this.panelY + 12;

      for (FormattedCharSequence line : this.wrap(this.summary.title, textW)) {
         context.drawString(this.font, line, textX, y, Theme.textPrimary(), false);
         y += 10;
      }

      for (FormattedCharSequence line : this.wrap(this.summary.subtitle, textW)) {
         context.drawString(this.font, line, textX, y, Theme.textSecondary(), false);
         y += 10;
      }

      y = Math.max(y, this.panelY + 36) + 10;

      for (CrashSummary.Suspect suspect : this.summary.suspects) {
         ThemeRenderer.fillRounded(context, this.panelX + 12, y, 276, 20, Ui.rowColor(false), 6);
         int idW = this.font.width(suspect.id());
         context.drawString(this.font, suspect.id(), this.panelX + 300 - 20 - idW, y + 6, Theme.textMuted(), false);
         context.drawString(this.font, this.font.plainSubstrByWidth(suspect.name(), 256 - idW - 8), this.panelX + 20, y + 6, Theme.textPrimary(), false);
         y += 24;
      }

      if (!this.summary.suspects.isEmpty()) {
         y += 2;
      }

      for (FormattedCharSequence line : this.wrap(this.summary.advice, 276)) {
         context.drawString(this.font, line, this.panelX + 12, y, Theme.textSecondary(), false);
         y += 10;
      }

      if (!this.summary.detail.isEmpty()) {
         context.drawString(this.font, this.font.plainSubstrByWidth(this.summary.detail, 276), this.panelX + 12, y + 2, Theme.textMuted(), false);
      }

      for (int i = 0; i < LABELS.length; i++) {
         int[] b = this.buttonBounds(i);
         boolean hovered = Ui.inside(mouseX, mouseY, b);
         boolean primary = i == LABELS.length - 1;
         ThemeRenderer.fillRounded(context, b[0], b[1], b[2], b[3], primary ? Theme.accent() : (hovered ? Theme.textSecondary() : Theme.border()), 6);
         ThemeRenderer.fillRounded(context, b[0] + 1, b[1] + 1, b[2] - 2, b[3] - 2, Ui.rowColor(hovered), 5);
         String label = this.font.plainSubstrByWidth(LABELS[i], b[2] - 6);
         context.drawCenteredString(this.font, label, b[0] + b[2] / 2, b[1] + 6, primary ? Theme.accent() : Theme.textPrimary());
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      for (int i = 0; i < LABELS.length; i++) {
         if (Ui.inside(click.x(), click.y(), this.buttonBounds(i))) {
            switch (i) {
               case 0:
                  openPath(this.summary.modsDir);
                  break;
               case 1:
                  openPath(this.summary.reportPath);
                  break;
               default:
                  this.onClose();
            }

            return true;
         }
      }

      return super.mouseClicked(click, doubled);
   }

   private static void openPath(String path) {
      if (path != null && !path.isEmpty() && new File(path).exists()) {
         Util.getPlatform().openFile(new File(path));
      }
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
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent);
      }
   }
}
