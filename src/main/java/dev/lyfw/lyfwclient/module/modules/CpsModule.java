package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.CpsTracker;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class CpsModule extends HudModule {
   private final EnumSetting<CpsModule.DisplayMode> displayMode = this.register(new EnumSetting<>("Display Mode", CpsModule.DisplayMode.BOTH));
   private final EnumSetting<CpsModule.TextStyle> textStyle = this.register(new EnumSetting<>("Text Style", CpsModule.TextStyle.NONE));
   private final BooleanSetting showLabel = this.register(new BooleanSetting("Show Label", true));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -1));
   private final BooleanSetting textShadow = this.register(new BooleanSetting("Text Shadow", true));
   private final BooleanSetting showBackground = this.register(new BooleanSetting("Show Background", true));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", Integer.MIN_VALUE));
   private final SliderSetting cornerRadius = this.register(new SliderSetting("Corner Radius", 0.0, 0.0, 10.0, 1.0, "px"));

   public CpsModule() {
      super("CPS Counter", "Shows left/right clicks-per-second.", false, 5.0, 5.0);
   }

   @Override
   public void tick() {
      CpsTracker.tick();
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      String text = this.buildText();
      int textWidth = mc.font.width(text);
      if (this.showBackground.get()) {
         int radius = this.cornerRadius.getInt();
         int bg = this.backgroundColor.get();
         int x1 = -2;
         int y1 = -2;
         int x2 = textWidth + 2;
         int y2 = 10;
         if (radius <= 0) {
            context.fill(x1, y1, x2, y2, bg);
         } else {
            context.fill(x1 + radius, y1, x2 - radius, y2, bg);
            context.fill(x1, y1 + radius, x1 + radius, y2 - radius, bg);
            context.fill(x2 - radius, y1 + radius, x2, y2 - radius, bg);
         }
      }

      context.drawString(mc.font, text, 0, 0, this.textColor.get(), this.textShadow.get());
   }

   private String buildText() {
      int leftCps = CpsTracker.getLeftCps();
      int rightCps = CpsTracker.getRightCps();
      String label = this.showLabel.get() ? " CPS" : "";

      String text = switch ((CpsModule.DisplayMode)this.displayMode.get()) {
         case BOTH -> leftCps + " | " + rightCps + label;
         case LEFT -> leftCps + label;
         case RIGHT -> rightCps + label;
      };

      return switch ((CpsModule.TextStyle)this.textStyle.get()) {
         case NONE -> text;
         case BOLD -> "§l" + text;
         case ITALIC -> "§o" + text;
         case UNDERLINED -> "§n" + text;
      };
   }

   @Override
   protected int contentWidth() {
      int textWidth = Minecraft.getInstance().font.width(this.buildText());
      return this.showBackground.get() ? textWidth + 4 : textWidth;
   }

   @Override
   protected int contentHeight() {
      return this.showBackground.get() ? 12 : 9;
   }

   @Override
   protected int contentOffsetX() {
      return this.showBackground.get() ? -2 : 0;
   }

   @Override
   protected int contentOffsetY() {
      return this.showBackground.get() ? -2 : 0;
   }

   public static enum DisplayMode {
      BOTH,
      LEFT,
      RIGHT;
   }

   public static enum TextStyle {
      NONE,
      BOLD,
      ITALIC,
      UNDERLINED;
   }
}
