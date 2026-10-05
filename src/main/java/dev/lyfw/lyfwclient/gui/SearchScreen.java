package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
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

public class SearchScreen extends Screen implements ClickGuiRoot {
   private static final int PANEL_W = 320;
   private static final int HEADER_H = 26;
   private static final int FIELD_H = 24;
   private static final int ROW_H = 22;
   private static final int PAD = 8;
   private static final int MAX_QUERY = 48;
   private final Screen parent;
   private final StringBuilder query = new StringBuilder();
   private final Scroll scroll = new Scroll();
   private int panelX;
   private int panelY;
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int panelH;

   public SearchScreen(Screen parent) {
      super(Component.literal("Search"));
      this.parent = parent;
   }

   public static boolean opensFrom(KeyEvent input, Screen from) {
      boolean ctrl = (input.modifiers() & 2) != 0 || (input.modifiers() & 8) != 0;
      if (ctrl && input.key() == 70) {
         Minecraft.getInstance().setScreen(new SearchScreen(from));
         return true;
      } else {
         return false;
      }
   }

   private List<Module> results() {
      String needle = this.query.toString().trim().toLowerCase(Locale.ROOT);
      List<Module> byName = new ArrayList<>();
      List<Module> byOther = new ArrayList<>();

      for (Module module : ModuleManager.all()) {
         if (needle.isEmpty()) {
            byName.add(module);
         } else if (module.name.toLowerCase(Locale.ROOT).contains(needle)) {
            byName.add(module);
         } else if (module.description.toLowerCase(Locale.ROOT).contains(needle) || module.category.title.toLowerCase(Locale.ROOT).contains(needle)) {
            byOther.add(module);
         }
      }

      byName.addAll(byOther);
      return byName;
   }

   private void computeLayout() {
      int rows = Math.max(1, this.results().size());
      int wanted = 58 + rows * 22 + 8;
      this.panelH = Math.min(Math.max(120, wanted), Math.max(120, this.height - 40));
      this.panelX = Math.max(2, (this.width - 320) / 2);
      this.panelY = Math.max(2, (this.height - this.panelH) / 2) - this.open.drop(20);
   }

   private int listTop() {
      return this.panelY + 26 + 24 + 8;
   }

   private int listBottom() {
      return this.panelY + this.panelH - 8;
   }

   private int maxScroll() {
      return Math.max(0, this.results().size() * 22 - (this.listBottom() - this.listTop()));
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      ThemeRenderer.panel(context, this.panelX, this.panelY, 320, this.panelH, Theme.border(), Theme.panelBg(), 8);
      ThemeRenderer.fillRoundedTop(context, this.panelX + 1, this.panelY + 1, 318, 25, Theme.headerBg(), 7);
      context.drawString(this.font, "SEARCH", this.panelX + 12, this.panelY + 9, Theme.textPrimary());
      List<Module> results = this.results();
      String count = results.size() + (results.size() == 1 ? " module" : " modules");
      context.drawString(this.font, count, this.panelX + 320 - 12 - this.font.width(count), this.panelY + 9, Theme.textSecondary());
      int fieldY = this.panelY + 26;
      ThemeRenderer.fillRounded(context, this.panelX + 8, fieldY, 304, 24, Theme.trackBg(), 5);
      ThemeRenderer.fillRounded(context, this.panelX + 8, fieldY, 304, 1, Theme.accent(), 0);
      ThemeRenderer.fillRounded(context, this.panelX + 8, fieldY + 23, 304, 1, Theme.accent(), 0);
      String shown = this.query.isEmpty() ? "Type to search every module..." : this.query.toString();
      if (System.currentTimeMillis() / 500L % 2L == 0L) {
         shown = shown + "_";
      }

      context.drawString(
         this.font, this.font.plainSubstrByWidth(shown, 288), this.panelX + 16, fieldY + 8, this.query.isEmpty() ? Theme.textSecondary() : Theme.textPrimary()
      );
      context.enableScissor(this.panelX + 1, this.listTop(), this.panelX + 319, this.listBottom());
      int y = this.listTop() - this.scroll.shown();
      if (results.isEmpty()) {
         context.drawString(this.font, "No matches.", this.panelX + 16, y + 7, Theme.textSecondary());
      }

      for (Module module : results) {
         this.renderRow(context, module, y, mouseX, mouseY);
         y += 22;
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackH = this.listBottom() - this.listTop();
         int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
         int handleY = this.listTop() + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         ThemeRenderer.fillRounded(context, this.panelX + 320 - 6, handleY, 3, handleH, Theme.accent(), 1);
      }

      String hint = "enter toggles the top match  •  right-click for settings  •  esc closes";
      context.drawCenteredString(this.font, hint, this.width / 2, this.panelY + this.panelH + 6, Theme.textSecondary());
   }

   private void renderRow(GuiGraphics context, Module module, int y, int mouseX, int mouseY) {
      if (y + 22 >= this.listTop() && y <= this.listBottom()) {
         int x = this.panelX + 8;
         int w = 304;
         boolean hovered = isInside(mouseX, mouseY, x, y, w, 22) && mouseY >= this.listTop() && mouseY < this.listBottom();
         boolean enabled = module.isEnabled();
         ThemeRenderer.row(context, x, y, w, 22, hovered, Theme.rowBg(), Theme.rowBgHover(), 4);
         int starX = x + 6;
         ThemeRenderer.star(context, this.font, starX, y + 7, module.favorite, ThemeRenderer.inStar(mouseX, mouseY, starX, y + 7));
         String category = module.category.title.toUpperCase(Locale.ROOT);
         int categoryW = this.font.width(category);
         context.drawString(this.font, category, x + w - 8 - categoryW, y + 7, Theme.sectionText());
         int pillW = 26;
         int pillX = x + w - 8 - categoryW - 8 - pillW;
         ThemeRenderer.fillRounded(context, pillX, y + 5, pillW, 12, enabled ? Theme.accent() : Theme.trackBg(), 6);
         int knob = 8;
         int knobX = enabled ? pillX + pillW - knob - 2 : pillX + 2;
         ThemeRenderer.fillRounded(context, knobX, y + 7, knob, knob, enabled ? Theme.knob() : Theme.textSecondary(), 4);
         int color = enabled ? Theme.accent() : ThemeRenderer.rowTextColor(hovered, Theme.textMuted(), Theme.textPrimary());
         String name = this.font.plainSubstrByWidth(module.name, pillX - x - 22);
         context.drawString(this.font, name, x + 19, y + 7, color);
      }
   }

   private static boolean isInside(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)Math.round(click.x());
      int my = (int)Math.round(click.y());
      if (my >= this.listTop() && my < this.listBottom()) {
         int y = this.listTop() - this.scroll.shown();

         for (Module module : this.results()) {
            if (isInside(mx, my, this.panelX + 8, y, 304, 22)) {
               if (click.button() == 1) {
                  this.openSettings(module);
               } else if (click.button() == 0) {
                  if (ThemeRenderer.inStar(mx, my, this.panelX + 8 + 6, y + 7)) {
                     module.favorite = !module.favorite;
                  } else {
                     module.setEnabled(!module.isEnabled());
                  }

                  Config.save();
               }

               return true;
            }

            y += 22;
         }
      }

      return true;
   }

   private void openSettings(Module module) {
      Screen dedicated = module.dedicatedSettingsScreen(this);
      if (dedicated != null) {
         Minecraft.getInstance().setScreen(dedicated);
      } else if (!module.getSettings().isEmpty()) {
         Minecraft.getInstance().setScreen(new ModuleSettingsScreen(this, module));
      }
   }

   public boolean charTyped(CharacterEvent input) {
      if (this.query.length() < 48 && input.isAllowedChatCharacter() && input.codepoint() != 167) {
         this.query.appendCodePoint(input.codepoint());
         this.scroll.jump(0);
      }

      return true;
   }

   public boolean keyPressed(KeyEvent input) {
      int keyCode = input.key();
      boolean ctrl = (input.modifiers() & 2) != 0 || (input.modifiers() & 8) != 0;
      if (ctrl && keyCode == 86) {
         String clip = Minecraft.getInstance().keyboardHandler.getClipboard();
         if (clip != null) {
            for (int i = 0; i < clip.length() && this.query.length() < 48; i++) {
               char c = clip.charAt(i);
               if (c != '\n' && c != '\r' && c != '\t') {
                  this.query.append(c);
               }
            }
         }

         return true;
      } else if (keyCode == 259) {
         if (this.query.length() > 0) {
            this.query.setLength(this.query.length() - 1);
            this.scroll.jump(0);
         }

         return true;
      } else if (keyCode == 256) {
         this.onClose();
         return true;
      } else if (keyCode != 257 && keyCode != 335) {
         return true;
      } else {
         List<Module> results = this.results();
         if (!results.isEmpty()) {
            results.get(0).setEnabled(!results.get(0).isEnabled());
            Config.save();
         }

         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 22.0), 0, this.maxScroll()));
      return true;
   }

   public void onClose() {
      Config.save();
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent != null ? this.parent : Theme.layout().create());
      }
   }
}
