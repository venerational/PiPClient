package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;

public class SnowModule extends Module {
   private final BooleanSetting everywhere = this.register(new BooleanSetting("Snow Everywhere", true));
   private final BooleanSetting always = this.register(new BooleanSetting("Always Snowing", false));
   private final SliderSetting strength = this.register(new SliderSetting("Strength", 60.0, 10.0, 100.0, 5.0, "%"));
   private final BooleanSetting underground = this.register(new BooleanSetting("Snow Underground", false));

   public SnowModule() {
      super("Snow", "Makes it snow in any biome, in any weather, and under any roof - for you only.", Category.RENDER, false);
      this.everywhere.group = "Snow";
      this.always.group = "Snow";
      this.strength.group = "Snow";
      this.underground.group = "Where";
   }

   public static SnowModule get() {
      return ModuleManager.get("Snow") instanceof SnowModule snow ? snow : null;
   }

   public boolean everywhere() {
      return this.isEnabled() && this.everywhere.get();
   }

   public float forcedIntensity() {
      return this.isEnabled() && this.always.get() ? (float)(this.strength.get() / 100.0) : 0.0F;
   }

   public boolean underground() {
      return this.isEnabled() && this.underground.get();
   }
}
