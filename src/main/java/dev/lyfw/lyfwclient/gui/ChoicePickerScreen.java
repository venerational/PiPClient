package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.setting.ChoiceSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ChoicePickerScreen extends Screen {
   private static final int PANEL_W = 320;
   private static final int HEADER_H = 26;
   private static final int FIELD_H = 24;
   private static final int ROW_H = 20;
   private static final int PAD = 8;
   private final Screen parent;
   private final ChoiceSetting setting;
   private final List<String> all;
   private final StringBuilder query = new StringBuilder();
   private final Scroll scroll = new Scroll();
   private int panelX;
   private int panelY;
   private final Anim open = new Anim(0.18F);
   private static final int DROP = 18;
   private int panelH;

   public ChoicePickerScreen(Screen parent, ChoiceSetting setting) {
      super(Component.literal(setting.name));
      this.parent = parent;
      this.setting = setting;
      this.all = setting.options();
   }

   private List<String> results() {
      String needle = this.query.toString().trim().toLowerCase(Locale.ROOT);
      if (needle.isEmpty()) {
         return this.all;
      } else {
         List<String> out = new ArrayList<>();

         for (String option : this.all) {
            if (option.toLowerCase(Locale.ROOT).contains(needle)) {
               out.add(option);
            }
         }

         return out;
      }
   }

   private void computeLayout() {
      this.panelH = Math.min(this.height - 40, 300);
      this.panelX = (this.width - 320) / 2;
      this.panelY = (this.height - this.panelH) / 2 - this.open.drop(18);
   }

   private int listTop() {
      return this.panelY + 26 + 24 + 8;
   }

   private int listBottom() {
      return this.panelY + this.panelH - 8;
   }

   private int maxScroll() {
      return Math.max(0, this.results().size() * 20 - (this.listBottom() - this.listTop()));
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      ThemeRenderer.panel(context, this.panelX, this.panelY, 320, this.panelH, Theme.border(), Theme.panelBg(), 8);
      ThemeRenderer.fillRoundedTop(context, this.panelX + 1, this.panelY + 1, 318, 25, Theme.headerBg(), 7);
      context.drawString(this.font, this.setting.name.toUpperCase(Locale.ROOT), this.panelX + 12, this.panelY + 9, Theme.textPrimary());
      List<String> results = this.results();
      String count = results.size() + (results.size() == 1 ? " option" : " options");
      context.drawString(this.font, count, this.panelX + 320 - 12 - this.font.width(count), this.panelY + 9, Theme.textSecondary());
      int fieldY = this.panelY + 26;
      ThemeRenderer.fillRounded(context, this.panelX + 8, fieldY, 304, 24, Theme.trackBg(), 5);
      String shown = this.query.isEmpty() ? "Type to filter..." : this.query.toString();
      if (System.currentTimeMillis() / 500L % 2L == 0L) {
         shown = shown + "_";
      }

      context.drawString(
         this.font,
         this.font.plainSubstrByWidth(shown, 288),
         this.panelX + 8 + 8,
         fieldY + 8,
         this.query.isEmpty() ? Theme.textSecondary() : Theme.textPrimary()
      );
      context.enableScissor(this.panelX + 1, this.listTop(), this.panelX + 320 - 1, this.listBottom());
      int y = this.listTop() - this.scroll.shown();
      if (results.isEmpty()) {
         context.drawString(this.font, "Nothing matches.", this.panelX + 16, y + 6, Theme.textSecondary());
      }

      for (String option : results) {
         if (y + 20 >= this.listTop() && y <= this.listBottom()) {
            int rowX = this.panelX + 8;
            int rowW = 304;
            boolean hovered = mouseX >= rowX
               && mouseX < rowX + rowW
               && mouseY >= y
               && mouseY < y + 20
               && mouseY >= this.listTop()
               && mouseY < this.listBottom();
            boolean picked = option.equals(this.setting.get());
            ThemeRenderer.row(context, rowX, y, rowW, 20, hovered, Theme.rowBg(), Theme.rowBgHover(), 4);
            if (picked) {
               ThemeRenderer.fillRounded(context, rowX, y, 3, 20, Theme.accent(), 1);
            }

            context.drawString(this.font, this.font.plainSubstrByWidth(option, rowW - 16), rowX + 8, y + 6, picked ? Theme.accent() : Theme.textPrimary());
         }

         y += 20;
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackH = this.listBottom() - this.listTop();
         int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
         int handleY = this.listTop() + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         ThemeRenderer.fillRounded(context, this.panelX + 320 - 6, handleY, 3, handleH, Theme.accent(), 1);
      }

      context.drawCenteredString(this.font, "click to pick  •  esc goes back", this.width / 2, this.panelY + this.panelH + 6, Theme.textSecondary());
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      int mx = (int)click.x();
      int my = (int)click.y();
      if (my >= this.listTop() && my < this.listBottom()) {
         List<String> results = this.results();
         int index = (my - this.listTop() + this.scroll.shown()) / 20;
         if (index >= 0 && index < results.size() && mx >= this.panelX + 8 && mx < this.panelX + 320 - 8) {
            this.setting.set(results.get(index));
            Config.save();
            this.onClose();
            return true;
         }
      }

      return super.mouseClicked(click, doubled);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 20.0), 0, this.maxScroll()));
      return true;
   }

   public boolean charTyped(CharacterEvent input) {
      if (input.isAllowedChatCharacter() && this.query.length() < 40) {
         this.query.appendCodePoint(input.codepoint());
         this.scroll.jump(0);
      }

      return true;
   }

   public boolean keyPressed(KeyEvent input) {
      int key = input.key();
      if (key == 259) {
         if (this.query.length() > 0) {
            this.query.deleteCharAt(this.query.length() - 1);
            this.scroll.jump(0);
         }

         return true;
      } else if (key == 256) {
         this.onClose();
         return true;
      } else {
         if (key == 257 || key == 335) {
            List<String> results = this.results();
            if (!results.isEmpty()) {
               this.setting.set(results.get(0));
               Config.save();
               this.onClose();
               return true;
            }
         }

         return super.keyPressed(input);
      }
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }
}
