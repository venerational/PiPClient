package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;

public class ExactHealthModule extends HudModule {
   private final SliderSetting decimals = this.register(new SliderSetting("Decimals", 2.0, 0.0, 4.0, 1.0, ""));
   private final BooleanSetting shadow = this.register(new BooleanSetting("Text Shadow", true));
   private final BooleanSetting hideInCreative = this.register(new BooleanSetting("Hide In Creative", false));
   private final EnumSetting<ExactHealthModule.AbsorptionMode> absorption = this.register(
      new EnumSetting<>("Absorption", ExactHealthModule.AbsorptionMode.SEPARATE)
   );
   private final ColorSetting absorptionColor = this.register(new ColorSetting("Absorption Color", -22016));
   private final BooleanSetting smoothGradient = this.register(new BooleanSetting("Smooth Gradient", false));
   private final SliderSetting lowThreshold = this.register(new SliderSetting("Low Threshold", 30.0, 0.0, 100.0, 1.0, "%"));
   private final SliderSetting mediumThreshold = this.register(new SliderSetting("Medium Threshold", 60.0, 0.0, 100.0, 1.0, "%"));
   private final ColorSetting lowColor = this.register(new ColorSetting("Low Color", -43691));
   private final ColorSetting mediumColor = this.register(new ColorSetting("Medium Color", -171));
   private final ColorSetting highColor = this.register(new ColorSetting("High Color", -11141291));

   public ExactHealthModule() {
      super("Exact Health", "Shows your exact health (with decimals) as a HUD number instead of vanilla's whole-heart display.", false, 150.0, 320.0);
      this.decimals.group = "General";
      this.shadow.group = "General";
      this.hideInCreative.group = "General";
      this.absorption.group = "Absorption";
      this.absorptionColor.group = "Absorption";
      this.smoothGradient.group = "Color";
      this.lowThreshold.group = "Color";
      this.mediumThreshold.group = "Color";
      this.lowColor.group = "Color";
      this.mediumColor.group = "Color";
      this.highColor.group = "Color";
   }

   private List<ExactHealthModule.Segment> segments(float health, float maxHealth, float absorptionAmount) {
      List<ExactHealthModule.Segment> out = new ArrayList<>(2);
      boolean add = this.absorption.get() == ExactHealthModule.AbsorptionMode.ADD;
      float shown = add ? health + absorptionAmount : health;
      out.add(new ExactHealthModule.Segment(this.formatHealth(shown), this.colorFor(health / Math.max(1.0F, maxHealth))));
      if (this.absorption.get() == ExactHealthModule.AbsorptionMode.SEPARATE && absorptionAmount > 0.0F) {
         out.add(new ExactHealthModule.Segment(" +" + this.formatHealth(absorptionAmount), this.absorptionColor.get()));
      }

      return out;
   }

   private String formatHealth(float value) {
      return String.format(Locale.ROOT, "%." + this.decimals.getInt() + "f", value);
   }

   private int colorFor(float fraction) {
      int low = this.lowColor.get();
      int medium = this.mediumColor.get();
      int high = this.highColor.get();
      float lowT = this.lowThreshold.getInt() / 100.0F;
      float mediumT = this.mediumThreshold.getInt() / 100.0F;
      if (fraction <= lowT) {
         return low;
      } else if (!this.smoothGradient.get()) {
         return fraction <= mediumT ? medium : high;
      } else if (fraction >= mediumT) {
         return high;
      } else {
         float span = Math.max(1.0E-4F, mediumT - lowT);
         float t = (fraction - lowT) / span;
         return t < 0.5F ? lerpColor(low, medium, t * 2.0F) : lerpColor(medium, high, (t - 0.5F) * 2.0F);
      }
   }

   private static int lerpColor(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int a = (int)((from >> 24 & 0xFF) + ((to >> 24 & 0xFF) - (from >> 24 & 0xFF)) * t);
      int r = (int)((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
      int g = (int)((from >> 8 & 0xFF) + ((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
      int b = (int)((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private List<ExactHealthModule.Segment> currentOrSampleSegments() {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      return player == null ? this.segments(12.34F, 20.0F, 8.0F) : this.segments(player.getHealth(), player.getMaxHealth(), player.getAbsorptionAmount());
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && (!this.hideInCreative.get() || mc.gameMode == null || mc.gameMode.getPlayerMode() != GameType.CREATIVE)) {
         List<ExactHealthModule.Segment> segments = this.segments(player.getHealth(), player.getMaxHealth(), player.getAbsorptionAmount());
         int cursor = 0;

         for (ExactHealthModule.Segment segment : segments) {
            context.drawString(mc.font, segment.text(), cursor, 0, segment.color(), this.shadow.get());
            cursor += mc.font.width(segment.text());
         }
      }
   }

   @Override
   protected int contentWidth() {
      Minecraft mc = Minecraft.getInstance();
      int width = 0;

      for (ExactHealthModule.Segment segment : this.currentOrSampleSegments()) {
         width += mc.font.width(segment.text());
      }

      return Math.max(1, width);
   }

   @Override
   protected int contentHeight() {
      return 9;
   }

   public static enum AbsorptionMode {
      SEPARATE,
      ADD,
      OFF;
   }

   private record Segment(String text, int color) {
   }
}
