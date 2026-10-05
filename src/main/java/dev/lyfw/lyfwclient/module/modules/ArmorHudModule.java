package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHudModule extends HudModule {
   private static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final Identifier HOTBAR_TEXTURE = Identifier.withDefaultNamespace("hud/hotbar");
   private static final int HOTBAR_TEX_W = 182;
   private static final int HOTBAR_TEX_H = 22;
   private static final int SLOT_W = 20;
   private static final int SLOT_H = 22;
   private static final int SLOT_SAMPLE_U = 41;
   private static final int SLOT_CORNER_RADIUS = 4;
   private static final int OUTLINE_THICKNESS = 1;
   private static final int[] GLOW_ALPHAS = new int[]{64, 255};
   private static final int OUTLINE_BOX_FILL = -1072293344;
   private static final int MICRO_BAR_W = 13;
   private static final int MICRO_BAR_H = 2;
   private static final int MICRO_ROW_PITCH = 3;
   private static final int MICRO_HOTBAR_W = 17;
   private static final int MICRO_HOTBAR_H = 16;
   private static final float LOW_DURABILITY_THRESHOLD = 0.1F;
   private static final int WARN_COLOR = -50384;
   private static final int WARN_ROW_H = 9;
   private static final int NUMBER_COLOR = -1;
   private static final int HEALTH_COLOR_GREEN = -11141291;
   private static final int HEALTH_COLOR_YELLOW = -171;
   private static final int HEALTH_COLOR_RED = -43691;
   private static final int NUMBERS_ICON_SIZE = 16;
   private static final int NUMBERS_ICON_GAP = 3;
   private static final int NUMBERS_ROW_PITCH = 18;
   private static final String NUMBERS_WIDTH_SAMPLE = "999";
   private final EnumSetting<ArmorHudModule.Style> style = this.register(new EnumSetting<>("Style", ArmorHudModule.Style.ICONS));
   private final ColorSetting barColor = this.register(new ColorSetting("Bar Color", -8396724));
   private final BooleanSetting hotbarBackground = this.register(new BooleanSetting("Hotbar Background", false));
   private final BooleanSetting customOutline = this.register(new BooleanSetting("Custom Outline", false));
   private final ColorSetting outlineColor = this.register(new ColorSetting("Outline Color", -16777216));
   private final BooleanSetting outlineGlow = this.register(new BooleanSetting("Outline Glow", true));
   private final BooleanSetting healthColors = this.register(new BooleanSetting("Health Colors", false));
   private static final float HEALTH_GREEN_STOP = 0.6F;
   private static final int HEALTH_FULL_RED_POINTS = 80;
   private static final int HEALTH_FADE_START_POINTS = 160;

   public ArmorHudModule() {
      super(
         "Armor HUD",
         "Shows worn armor as clean minimal icons in vanilla hotbar slots, as tiny per-piece durability bars (\"MicroDurability+\" style) with an optional condensed hotbar-look background or a custom-colored outline, or as a plain readable icon+number list.",
         false,
         4.0,
         160.0
      );
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         switch ((ArmorHudModule.Style)this.style.get()) {
            case MICRO:
               this.renderMicroBars(context, mc);
               break;
            case NUMBERS:
               this.renderNumbers(context, mc);
               break;
            default:
               this.renderIcons(context, mc);
         }
      }
   }

   @Override
   protected int contentWidth() {
      return switch ((ArmorHudModule.Style)this.style.get()) {
         case ICONS -> SLOTS.length * 20;
         case MICRO -> this.hotbarBackground.get() ? 17 : 13;
         case NUMBERS -> 19 + Minecraft.getInstance().font.width("999");
      };
   }

   @Override
   protected int contentHeight() {
      return switch ((ArmorHudModule.Style)this.style.get()) {
         case ICONS -> 22;
         case MICRO -> 9 + (this.hotbarBackground.get() ? 16 : microBarsHeight());
         case NUMBERS -> SLOTS.length * 18;
      };
   }

   private static int microBarsHeight() {
      return (SLOTS.length - 1) * 3 + 2;
   }

   private void renderIcons(GuiGraphics context, Minecraft mc) {
      int totalWidth = SLOTS.length * 20;
      this.drawSlotBox(context, 0, 0, totalWidth, 22, 4);

      for (int i = 0; i < SLOTS.length; i++) {
         ItemStack stack = mc.player.getItemBySlot(SLOTS[i]);
         if (!stack.isEmpty()) {
            int slotX = i * 20;
            int iconX = slotX + 2;
            int iconY = 3;
            context.renderItem(stack, iconX, iconY);
            context.renderItemDecorations(mc.font, stack, iconX, iconY);
         }
      }
   }

   private void renderMicroBars(GuiGraphics context, Minecraft mc) {
      int boxY = 9;
      int barX;
      int barY;
      if (this.hotbarBackground.get()) {
         this.drawSlotBox(context, 0, boxY, 17, 16, 4);
         barX = 2;
         barY = boxY + (16 - microBarsHeight()) / 2;
      } else if (this.customOutline.get()) {
         this.drawSquareOutline(context, 0, boxY, 13, microBarsHeight());
         context.fill(0, boxY, 13, boxY + microBarsHeight(), -1072293344);
         barX = 0;
         barY = boxY;
      } else {
         barX = 0;
         barY = boxY;
      }

      int criticalCount = 0;

      for (int i = 0; i < SLOTS.length; i++) {
         ItemStack stack = mc.player.getItemBySlot(SLOTS[i]);
         if (!stack.isEmpty() && stack.isDamageableItem()) {
            int rowY = barY + i * 3;
            context.fill(barX, rowY, barX + 13, rowY + 2, -16777216);
            int remaining = stack.getMaxDamage() - stack.getDamageValue();
            float fraction = 1.0F - (float)stack.getDamageValue() / stack.getMaxDamage();
            int fillW = stack.getBarWidth();
            if (fillW > 0) {
               int color = this.healthColors.get() ? healthColor(fraction, remaining) : this.barColor.get();
               context.fill(barX, rowY, barX + fillW, rowY + 1, color);
            }

            if (fraction < 0.1F) {
               criticalCount++;
            }
         }
      }

      if (criticalCount > 0) {
         int warnCenterX = (this.hotbarBackground.get() ? 17 : 13) / 2;
         this.drawTinyText(context, mc, "!".repeat(criticalCount), warnCenterX, 0.0F, 9, -50384);
      }
   }

   private void renderNumbers(GuiGraphics context, Minecraft mc) {
      for (int i = 0; i < SLOTS.length; i++) {
         ItemStack stack = mc.player.getItemBySlot(SLOTS[i]);
         if (!stack.isEmpty() && stack.isDamageableItem()) {
            int rowY = i * 18;
            context.renderItem(stack, 0, rowY);
            context.renderItemDecorations(mc.font, stack, 0, rowY);
            int remaining = stack.getMaxDamage() - stack.getDamageValue();
            float fraction = 1.0F - (float)stack.getDamageValue() / stack.getMaxDamage();
            int color = this.healthColors.get() ? healthColor(fraction, remaining) : -1;
            int textY = rowY + 4;
            context.drawString(mc.font, Integer.toString(remaining), 19, textY, color);
         }
      }
   }

   private static int healthColor(float fraction, int remaining) {
      float f = Mth.clamp(fraction, 0.0F, 1.0F);
      if (f >= 0.6F) {
         return lerpColor(-171, -11141291, (f - 0.6F) / 0.39999998F);
      } else if (remaining <= 80) {
         return -43691;
      } else if (remaining >= 160) {
         return -171;
      } else {
         float t = (remaining - 80) / 80.0F;
         return lerpColor(-43691, -171, t);
      }
   }

   private static int lerpColor(int from, int to, float t) {
      float clamped = Mth.clamp(t, 0.0F, 1.0F);
      int a = lerpChannel(from >>> 24 & 0xFF, to >>> 24 & 0xFF, clamped);
      int r = lerpChannel(from >> 16 & 0xFF, to >> 16 & 0xFF, clamped);
      int g = lerpChannel(from >> 8 & 0xFF, to >> 8 & 0xFF, clamped);
      int b = lerpChannel(from & 0xFF, to & 0xFF, clamped);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int lerpChannel(int from, int to, float t) {
      return Math.round(from + (to - from) * t);
   }

   private void drawTinyText(GuiGraphics context, Minecraft mc, String text, float centerX, float rowY, int rowH, int color) {
      int glyphWidth = mc.font.width(text);
      float scale = rowH / 8.0F;
      float drawnWidth = glyphWidth * scale;
      float px = centerX - drawnWidth / 2.0F;
      float py = rowY - (8.0F * scale - rowH) / 2.0F;
      context.pose().pushMatrix();
      context.pose().translate(px, py);
      context.pose().scale(scale, scale);
      context.drawString(mc.font, text, 0, 0, color);
      context.pose().popMatrix();
   }

   private void drawSlotBox(GuiGraphics context, int x, int y, int width, int height, int radius) {
      int r = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
      int color = this.outlineColor.get();
      if (this.outlineGlow.get()) {
         drawGlowOutline(context, x, y, width, height, r, color);
      } else {
         drawOutlineRing(context, x, y, width, height, color, r, 0, 1);
      }

      if (width < 20) {
         drawSlotArtSplit(context, x, y, width, height, r);
      } else {
         drawSlotArtSingle(context, x, y, width, height, r);
      }
   }

   private static void drawSlotArtSingle(GuiGraphics context, int x, int y, int width, int height, int r) {
      if (height - 2 * r > 0) {
         context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 41, r, x, y + r, width, height - 2 * r);
      }

      for (int row = 0; row < r; row++) {
         int cut = cornerCut(row, r);
         int rowW = width - cut * 2;
         if (rowW > 0) {
            int bottomRow = height - 1 - row;
            context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 41 + cut, row, x + cut, y + row, rowW, 1);
            context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 41 + cut, bottomRow, x + cut, y + bottomRow, rowW, 1);
         }
      }
   }

   private static void drawSlotArtSplit(GuiGraphics context, int x, int y, int width, int height, int r) {
      int leftW = (width + 1) / 2;

      for (int row = r; row < height - r; row++) {
         drawSplitRow(context, x, y + row, row, x, x + width, width, leftW);
      }

      for (int row = 0; row < r; row++) {
         int cut = cornerCut(row, r);
         int rowStart = x + cut;
         int rowEnd = x + width - cut;
         if (rowEnd > rowStart) {
            int destBottomRow = height - 1 - row;
            int srcBottomRow = 21 - row;
            drawSplitRow(context, x, y + row, row, rowStart, rowEnd, width, leftW);
            drawSplitRow(context, x, y + destBottomRow, srcBottomRow, rowStart, rowEnd, width, leftW);
         }
      }
   }

   private static void drawSplitRow(GuiGraphics context, int boxX, int destY, int srcY, int destX0, int destX1, int width, int leftW) {
      int leftEnd = Math.min(destX1, boxX + leftW);
      if (leftEnd > destX0) {
         int w = leftEnd - destX0;
         int colInBox = destX0 - boxX;
         context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, 41 + colInBox, srcY, destX0, destY, w, 1);
      }

      int rightStart = Math.max(destX0, boxX + leftW);
      if (destX1 > rightStart) {
         int w = destX1 - rightStart;
         int colInBox = rightStart - boxX;
         int rightSrcBase = 61 - width;
         context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, 182, 22, rightSrcBase + colInBox, srcY, rightStart, destY, w, 1);
      }
   }

   private static void drawGlowOutline(GuiGraphics context, int x, int y, int w, int h, int radius, int rgb) {
      for (int i = 0; i < GLOW_ALPHAS.length; i++) {
         int offset = GLOW_ALPHAS.length - 1 - i;
         int color = GLOW_ALPHAS[i] << 24 | rgb & 16777215;
         drawOutlineRing(context, x, y, w, h, color, radius, offset, 1);
      }
   }

   private void drawSquareOutline(GuiGraphics context, int x, int y, int w, int h) {
      int color = this.outlineColor.get();
      if (this.outlineGlow.get()) {
         for (int i = 0; i < GLOW_ALPHAS.length; i++) {
            int offset = GLOW_ALPHAS.length - 1 - i;
            int c = GLOW_ALPHAS[i] << 24 | color & 16777215;
            fillRectRing(context, x, y, w, h, c, offset, 1);
         }
      } else {
         fillRectRing(context, x, y, w, h, color, 0, 1);
      }
   }

   private static void fillRectRing(GuiGraphics context, int x, int y, int w, int h, int color, int offset, int thickness) {
      int left = x - offset;
      int top = y - offset;
      int right = x + w + offset;
      int bottom = y + h + offset;
      context.fill(left - thickness, top - thickness, right + thickness, top, color);
      context.fill(left - thickness, bottom, right + thickness, bottom + thickness, color);
      context.fill(left - thickness, top, left, bottom, color);
      context.fill(right, top, right + thickness, bottom, color);
   }

   private static void drawOutlineRing(GuiGraphics context, int x, int y, int w, int h, int color, int radius, int offset, int thickness) {
      int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));

      for (int row = 0; row < h; row++) {
         int distFromEdge = Math.min(row, h - 1 - row);
         int cut = cornerCut(distFromEdge, r);
         int leftOuter = x + cut - offset;
         int rightOuter = x + w - cut + offset;
         context.fill(leftOuter - thickness, y + row, leftOuter, y + row + 1, color);
         context.fill(rightOuter, y + row, rightOuter + thickness, y + row + 1, color);
      }
   }

   private static int cornerCut(int distFromEdge, int radius) {
      if (distFromEdge >= radius) {
         return 0;
      } else {
         double dy = distFromEdge + 0.5 - radius;
         int cut = 0;

         for (int col = 0; col < radius; col++) {
            double dx = col + 0.5 - radius;
            if (!(dx * dx + dy * dy > (double)radius * radius)) {
               break;
            }

            cut = col + 1;
         }

         return cut;
      }
   }

   public static enum Style {
      ICONS,
      MICRO,
      NUMBERS;
   }
}
