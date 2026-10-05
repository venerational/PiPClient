package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ReachDisplayModule extends HudModule {
   private final EnumSetting<ReachDisplayModule.DistanceFormat> format = this.register(new EnumSetting<>("Format", ReachDisplayModule.DistanceFormat.NUMBER));
   private final BooleanSetting playersOnly = this.register(new BooleanSetting("Players Only", true));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -1));
   private final BooleanSetting textShadow = this.register(new BooleanSetting("Text Shadow", true));
   private final BooleanSetting showBackground = this.register(new BooleanSetting("Show Background", true));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", Integer.MIN_VALUE));
   private final BooleanSetting gradientByDistance = this.register(new BooleanSetting("Gradient By Distance", false));
   private final BooleanSetting keepLast = this.register(new BooleanSetting("Keep Last Hit", true));
   private final SliderSetting resetDelay = this.register(new SliderSetting("Reset Delay", 60.0, 5.0, 200.0, 5.0, " ticks"));
   private float lastDistance;
   private long lastHitTick = -1L;
   private long ticks;

   public ReachDisplayModule() {
      super("Reach Display", "Shows the distance of your latest hit on an entity, matching \"Player Reach Display\".", false, 4.0, 165.0);
   }

   @Override
   public void tick() {
      this.ticks++;
   }

   public void recordHit(float distance) {
      this.lastDistance = distance;
      this.lastHitTick = this.ticks;
   }

   public boolean playersOnly() {
      return this.playersOnly.get();
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      String text = this.buildText();
      int textWidth = mc.font.width(text);
      if (this.showBackground.get()) {
         context.fill(-2, -2, textWidth + 2, 10, this.backgroundColor.get());
      }

      context.drawString(mc.font, text, 0, 0, this.colorFor(this.displayDistance()), this.textShadow.get());
   }

   private float displayDistance() {
      if (this.lastHitTick < 0L) {
         return 0.0F;
      } else {
         return !this.keepLast.get() && this.ticks - this.lastHitTick > this.resetDelay.getInt() ? 0.0F : this.lastDistance;
      }
   }

   private String buildText() {
      String number = String.format("%.2f", this.displayDistance());

      return switch ((ReachDisplayModule.DistanceFormat)this.format.get()) {
         case NUMBER -> number;
         case WITH_UNIT -> number + " blocks";
         case ABBREVIATED -> number + " M";
      };
   }

   private int colorFor(float distance) {
      if (!this.gradientByDistance.get()) {
         return this.textColor.get();
      } else {
         int green = 65280;
         int yellow = 16776960;
         int red = 16711680;
         int rgb;
         if (distance <= 3.0F) {
            rgb = green;
         } else if (distance < 4.5F) {
            rgb = lerpRgb(green, yellow, (distance - 3.0F) / 1.5F);
         } else if (distance < 6.0F) {
            rgb = lerpRgb(yellow, red, (distance - 4.5F) / 1.5F);
         } else {
            rgb = red;
         }

         return 0xFF000000 | rgb;
      }
   }

   private static int lerpRgb(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int r1 = from >> 16 & 0xFF;
      int g1 = from >> 8 & 0xFF;
      int b1 = from & 0xFF;
      int r2 = to >> 16 & 0xFF;
      int g2 = to >> 8 & 0xFF;
      int b2 = to & 0xFF;
      int r = Math.round(r1 + (r2 - r1) * t);
      int g = Math.round(g1 + (g2 - g1) * t);
      int b = Math.round(b1 + (b2 - b1) * t);
      return r << 16 | g << 8 | b;
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

   public static enum DistanceFormat {
      NUMBER,
      WITH_UNIT,
      ABBREVIATED;
   }
}
