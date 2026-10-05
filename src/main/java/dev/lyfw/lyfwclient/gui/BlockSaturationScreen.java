package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.render.BlockSaturation;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class BlockSaturationScreen extends Screen {
   private static final int PANEL_W = 380;
   private static final int HEADER_H = 26;
   private static final int FIELD_H = 24;
   private static final int ROW_H = 22;
   private static final int PAD = 8;
   private static final int SLIDER_W = 110;
   private final Screen parent;
   private final TextSetting setting;
   private final List<Block> all;
   private final StringBuilder query = new StringBuilder();
   private final Scroll scroll = new Scroll();
   private final Anim open = new Anim(0.18F);
   private int panelX;
   private int panelY;
   private int panelH;
   private Block dragging;

   public BlockSaturationScreen(Screen parent, TextSetting setting) {
      super(Component.literal("Block Saturation"));
      this.parent = parent;
      this.setting = setting;
      this.all = BlockSaturation.allBlocks();
   }

   private Map<Block, Integer> picked() {
      return BlockSaturation.parse(this.setting.get());
   }

   private void save(Map<Block, Integer> picked) {
      this.setting.set(BlockSaturation.write(picked));
   }

   private List<Block> rows() {
      String needle = this.query.toString().trim();
      Map<Block, Integer> picked = this.picked();
      List<Block> out = new ArrayList<>();

      for (Block block : picked.keySet()) {
         if (needle.isEmpty() || BlockSaturation.matches(block, needle)) {
            out.add(block);
         }
      }

      for (Block blockx : this.all) {
         if (!picked.containsKey(blockx) && (needle.isEmpty() || BlockSaturation.matches(blockx, needle))) {
            out.add(blockx);
         }
      }

      return out;
   }

   private void computeLayout() {
      this.panelH = Math.min(this.height - 40, 320);
      this.panelX = (this.width - 380) / 2;
      this.panelY = (this.height - this.panelH) / 2 - this.open.drop(18);
   }

   private int listTop() {
      return this.panelY + 26 + 24 + 8;
   }

   private int listBottom() {
      return this.panelY + this.panelH - 8;
   }

   private int maxScroll() {
      return Math.max(0, this.rows().size() * 22 - (this.listBottom() - this.listTop()));
   }

   private int sliderX() {
      return this.panelX + 380 - 8 - 22 - 110;
   }

   private int removeX() {
      return this.panelX + 380 - 8 - 18;
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      ThemeRenderer.panel(context, this.panelX, this.panelY, 380, this.panelH, Theme.border(), Theme.panelBg(), 8);
      ThemeRenderer.fillRoundedTop(context, this.panelX + 1, this.panelY + 1, 378, 25, Theme.headerBg(), 7);
      context.drawString(this.font, "BLOCK SATURATION", this.panelX + 12, this.panelY + 9, Theme.textPrimary());
      Map<Block, Integer> picked = this.picked();
      String count = picked.size() + (picked.size() == 1 ? " block greyed" : " blocks greyed");
      context.drawString(this.font, count, this.panelX + 380 - 12 - this.font.width(count), this.panelY + 9, Theme.textSecondary());
      int fieldY = this.panelY + 26;
      ThemeRenderer.fillRounded(context, this.panelX + 8, fieldY, 364, 24, Theme.trackBg(), 5);
      String shown = this.query.isEmpty() ? "Search blocks..." : this.query.toString();
      if (System.currentTimeMillis() / 500L % 2L == 0L) {
         shown = shown + "_";
      }

      context.drawString(
         this.font,
         this.font.plainSubstrByWidth(shown, 348),
         this.panelX + 8 + 8,
         fieldY + 8,
         this.query.isEmpty() ? Theme.textSecondary() : Theme.textPrimary()
      );
      List<Block> rows = this.rows();
      context.enableScissor(this.panelX + 1, this.listTop(), this.panelX + 380 - 1, this.listBottom());
      int y = this.listTop() - this.scroll.shown();
      if (rows.isEmpty()) {
         context.drawString(this.font, "Nothing matches.", this.panelX + 16, y + 6, Theme.textSecondary());
      }

      for (Block block : rows) {
         if (y + 22 >= this.listTop() && y <= this.listBottom()) {
            int rowX = this.panelX + 8;
            int rowW = 364;
            boolean inList = mouseY >= this.listTop() && mouseY < this.listBottom();
            boolean hovered = inList && mouseX >= rowX && mouseX < rowX + rowW && mouseY >= y && mouseY < y + 22;
            Integer saturation = picked.get(block);
            ThemeRenderer.row(context, rowX, y, rowW, 20, hovered, Theme.rowBg(), Theme.rowBgHover(), 4);
            if (block.asItem() != Items.AIR) {
               context.renderItem(new ItemStack(block.asItem()), rowX + 3, y + 2);
            }

            int nameRoom = saturation == null ? rowW - 60 : this.sliderX() - rowX - 30;
            context.drawString(
               this.font,
               this.font.plainSubstrByWidth(BlockSaturation.name(block), nameRoom),
               rowX + 24,
               y + 6,
               saturation == null ? Theme.textPrimary() : Theme.accent()
            );
            if (saturation == null) {
               String add = "+ Add";
               context.drawString(this.font, add, rowX + rowW - 8 - this.font.width(add), y + 6, hovered ? Theme.accent() : Theme.textMuted());
            } else {
               int sx = this.sliderX();
               float fraction = saturation.intValue() / 100.0F;
               ThemeRenderer.fillRounded(context, sx, y + 8, 110, 4, Theme.trackBg(), 2);
               ThemeRenderer.fillRounded(context, sx, y + 8, Math.max(4, Math.round(110.0F * fraction)), 4, Theme.accent(), 2);
               ThemeRenderer.fillRounded(context, sx + Math.round(104.0F * fraction), y + 5, 6, 10, Theme.textPrimary(), 3);
               String value = saturation + "%";
               context.drawString(this.font, value, sx - 6 - this.font.width(value), y + 6, Theme.textSecondary());
               boolean overRemove = hovered && mouseX >= this.removeX() && mouseX < this.removeX() + 14;
               context.drawString(this.font, "×", this.removeX() + 4, y + 6, overRemove ? Theme.danger() : Theme.textMuted());
            }
         }

         y += 22;
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackH = this.listBottom() - this.listTop();
         int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
         int handleY = this.listTop() + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         ThemeRenderer.fillRounded(context, this.panelX + 380 - 6, handleY, 3, handleH, Theme.accent(), 1);
      }

      context.drawCenteredString(this.font, "0% is fully grey  •  esc goes back", this.width / 2, this.panelY + this.panelH + 6, Theme.textSecondary());
   }

   private Block rowAt(int mx, int my) {
      if (my >= this.listTop() && my < this.listBottom() && mx >= this.panelX + 8 && mx < this.panelX + 380 - 8) {
         List<Block> rows = this.rows();
         int index = (my - this.listTop() + this.scroll.shown()) / 22;
         return index >= 0 && index < rows.size() ? rows.get(index) : null;
      } else {
         return null;
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      int mx = (int)click.x();
      int my = (int)click.y();
      Block block = this.rowAt(mx, my);
      if (block == null) {
         return super.mouseClicked(click, doubled);
      } else {
         Map<Block, Integer> picked = this.picked();
         if (!picked.containsKey(block)) {
            picked.put(block, 0);
            this.save(picked);
            Config.save();
         } else if (mx >= this.removeX()) {
            picked.remove(block);
            this.save(picked);
            Config.save();
         } else if (mx >= this.sliderX() - 4) {
            this.dragging = block;
            this.drag(mx);
         }

         return true;
      }
   }

   private void drag(int mx) {
      Map<Block, Integer> picked = this.picked();
      if (this.dragging != null && picked.containsKey(this.dragging)) {
         int value = Math.round(Mth.clamp((mx - this.sliderX()) / 110.0F, 0.0F, 1.0F) * 100.0F);
         picked.put(this.dragging, value);
         this.save(picked);
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      if (this.dragging != null) {
         this.drag((int)click.x());
         return true;
      } else {
         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.dragging != null) {
         this.dragging = null;
         Config.save();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 22.0), 0, this.maxScroll()));
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
         return super.keyPressed(input);
      }
   }

   public void onClose() {
      Config.save();
      Minecraft.getInstance().setScreen(this.parent);
   }
}
