package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CapePickerScreen extends Screen {
   private static final int PANEL_W = 340;
   private static final int HEADER_H = 26;
   private static final int PAD = 8;
   private static final int TILE_W = 60;
   private static final int TILE_H = 84;
   private static final int GAP = 6;
   private static final int IMAGE_W = 30;
   private static final int IMAGE_H = 48;
   private final Screen parent;
   private final CosmeticsModule cosmetics;
   private final List<CapePickerScreen.Entry> entries = new ArrayList<>();
   private final Scroll scroll = new Scroll();
   private final Anim open = new Anim(0.18F);
   private static final int DROP = 18;
   private int panelX;
   private int panelY;
   private int panelH;

   public CapePickerScreen(Screen parent, CosmeticsModule cosmetics) {
      super(Component.literal("Cape"));
      this.parent = parent;
      this.cosmetics = cosmetics;
      this.entries.add(new CapePickerScreen.Entry(CosmeticsModule.CapeStyle.COLOR, null, "Color"));
      this.entries.add(new CapePickerScreen.Entry(CosmeticsModule.CapeStyle.DRAWN, null, "Drawn"));

      for (String name : cosmetics.minecraftCapeNames()) {
         this.entries.add(new CapePickerScreen.Entry(CosmeticsModule.CapeStyle.MINECRAFT, name, name));
      }
   }

   private void computeLayout() {
      this.panelH = Math.min(this.height - 40, 320);
      this.panelX = (this.width - 340) / 2;
      this.panelY = (this.height - this.panelH) / 2 - this.open.drop(18);
   }

   private int columns() {
      return Math.max(1, 5);
   }

   private int gridLeft() {
      int columns = this.columns();
      return this.panelX + (340 - (columns * 60 + (columns - 1) * 6)) / 2;
   }

   private int listTop() {
      return this.panelY + 26 + 8;
   }

   private int listBottom() {
      return this.panelY + this.panelH - 8;
   }

   private int maxScroll() {
      int rows = (this.entries.size() + this.columns() - 1) / this.columns();
      return Math.max(0, rows * 90 - 6 - (this.listBottom() - this.listTop()));
   }

   private boolean isPicked(CapePickerScreen.Entry entry) {
      return entry.style() == this.cosmetics.capeStyle()
         && (entry.style() != CosmeticsModule.CapeStyle.MINECRAFT || entry.name().equals(this.cosmetics.selectedMinecraftCape()));
   }

   private Identifier texture(CapePickerScreen.Entry entry) {
      return switch (entry.style()) {
         case COLOR -> this.cosmetics.colorCapeTexture();
         case DRAWN -> this.cosmetics.drawnCapeTexture();
         case MINECRAFT -> this.cosmetics.capePreview(entry.name());
         case ANIMATED -> this.cosmetics.capeTexture(entry.style(), entry.name());
      };
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      ThemeRenderer.panel(context, this.panelX, this.panelY, 340, this.panelH, Theme.border(), Theme.panelBg(), 8);
      ThemeRenderer.fillRoundedTop(context, this.panelX + 1, this.panelY + 1, 338, 25, Theme.headerBg(), 7);
      context.drawString(this.font, "CAPE STYLE", this.panelX + 12, this.panelY + 9, Theme.textPrimary());
      String count = this.entries.size() + " capes";
      context.drawString(this.font, count, this.panelX + 340 - 12 - this.font.width(count), this.panelY + 9, Theme.textSecondary());
      int top = this.listTop();
      int bottom = this.listBottom();
      int columns = this.columns();
      int left = this.gridLeft();
      context.enableScissor(this.panelX + 1, top, this.panelX + 340 - 1, bottom);

      for (int i = 0; i < this.entries.size(); i++) {
         CapePickerScreen.Entry entry = this.entries.get(i);
         int x = left + i % columns * 66;
         int y = top + i / columns * 90 - this.scroll.shown();
         if (y + 84 >= top && y <= bottom) {
            boolean hovered = mouseX >= x && mouseX < x + 60 && mouseY >= y && mouseY < y + 84 && mouseY >= top && mouseY < bottom;
            boolean picked = this.isPicked(entry);
            int background = hovered ? Theme.rowBgHover() : Theme.rowBg();
            ThemeRenderer.panel(context, x, y, 60, 84, picked ? Theme.accent() : background, background, 5);
            int imageX = x + 15;
            int imageY = y + 6;
            Identifier texture = this.texture(entry);
            if (texture != null) {
               context.blit(RenderPipelines.GUI_TEXTURED, texture, imageX, imageY, 1.0F, 1.0F, 30, 48, 10, 16, 64, 32);
            } else {
               context.fill(imageX, imageY, imageX + 30, imageY + 48, Theme.trackBg());
               String waiting = entry.style() == CosmeticsModule.CapeStyle.MINECRAFT && this.cosmetics.capeFailed(entry.name()) ? "offline" : "...";
               context.drawCenteredString(this.font, waiting, imageX + 15, imageY + 24 - 4, Theme.textSecondary());
            }

            this.drawLabel(context, entry.label(), x + 30, y + 58, picked ? Theme.accent() : Theme.textPrimary());
         }
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackH = bottom - top;
         int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
         int handleY = top + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         ThemeRenderer.fillRounded(context, this.panelX + 340 - 6, handleY, 3, handleH, Theme.accent(), 1);
      }

      context.drawCenteredString(this.font, "click a cape to wear it  •  esc goes back", this.width / 2, this.panelY + this.panelH + 6, Theme.textSecondary());
   }

   private void drawLabel(GuiGraphics context, String label, int centerX, int y, int color) {
      int room = 56;
      if (this.font.width(label) <= room) {
         context.drawCenteredString(this.font, label, centerX, y + 5, color);
      } else {
         int space = label.lastIndexOf(32);

         while (space > 0 && this.font.width(label.substring(0, space)) > room) {
            space = label.lastIndexOf(32, space - 1);
         }

         String first = space > 0 ? label.substring(0, space) : this.font.plainSubstrByWidth(label, room);
         String second = space > 0 ? label.substring(space + 1) : label.substring(first.length());
         context.drawCenteredString(this.font, first, centerX, y, color);
         context.drawCenteredString(this.font, this.font.plainSubstrByWidth(second, room), centerX, y + 11, color);
      }
   }

   protected void init() {
      for (CapePickerScreen.Entry entry : this.entries) {
         if (entry.style() == CosmeticsModule.CapeStyle.MINECRAFT) {
            this.cosmetics.capePreview(entry.name());
         }
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)click.x();
      int my = (int)click.y();
      if (my >= this.listTop() && my < this.listBottom()) {
         int relX = mx - this.gridLeft();
         int relY = my - this.listTop() + this.scroll.shown();
         if (relX >= 0 && relX % 66 < 60 && relY >= 0 && relY % 90 < 84) {
            int column = relX / 66;
            int index = relY / 90 * this.columns() + column;
            if (column < this.columns() && index < this.entries.size()) {
               CapePickerScreen.Entry entry = this.entries.get(index);
               this.cosmetics.selectCape(entry.style(), entry.name());
               Config.save();
               this.onClose();
               return true;
            }
         }
      }

      return super.mouseClicked(click, doubled);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 90.0 / 2.0), 0, this.maxScroll()));
      return true;
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
      Minecraft.getInstance().setScreen(this.parent);
   }

   private record Entry(CosmeticsModule.CapeStyle style, String name, String label) {
   }
}
