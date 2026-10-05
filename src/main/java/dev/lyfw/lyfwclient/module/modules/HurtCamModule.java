package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;

public class HurtCamModule extends Module {
   private final BooleanSetting hurtCamEnabled = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting multiplier = this.register(new SliderSetting("Multiplier", 0.3, 0.0, 2.0, 0.05, "x"));
   private final EnumSetting<HurtCamModule.Style> style = this.register(new EnumSetting<>("Style", HurtCamModule.Style.YAW_BASED));
   private final BooleanSetting heartBlink = this.register(new BooleanSetting("Heart Blink", true));

   public HurtCamModule() {
      super("Hurt Cam", "Controls the camera tilt that plays when you take damage - matches \"BetterHurtCam\".", Category.RENDER, false);
   }

   public boolean hurtCamEnabled() {
      return this.hurtCamEnabled.get();
   }

   public double multiplier() {
      return this.multiplier.get();
   }

   public boolean isOldStyle() {
      return this.style.get() == HurtCamModule.Style.OLD;
   }

   public boolean heartBlink() {
      return this.heartBlink.get();
   }

   public static enum Style {
      OLD,
      YAW_BASED;
   }
}
