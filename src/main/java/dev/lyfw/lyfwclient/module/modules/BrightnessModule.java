package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.mixin.SimpleOptionValueAccessor;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

public class BrightnessModule extends Module {
   private final SliderSetting brightness = this.register(new SliderSetting("Brightness", 300.0, 0.0, 1500.0, 10.0, "%"));
   private final BooleanSetting atNight = this.register(new BooleanSetting("Apply At Night", true));
   private double savedGamma;
   private boolean applied;

   public BrightnessModule() {
      super("Brightness", "Sets the world brightness anywhere you like, well past the vanilla slider's ceiling.", Category.RENDER, false);
      this.brightness.group = "Brightness";
      this.atNight.group = "Brightness";
   }

   public static BrightnessModule get() {
      return ModuleManager.get("Brightness") instanceof BrightnessModule brightness ? brightness : null;
   }

   @Override
   public void tick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.options != null) {
         OptionInstance<Double> gamma = mc.options.gamma();
         if (this.isEnabled()) {
            if (!this.applied) {
               this.savedGamma = (Double)gamma.get();
               this.applied = true;
            }

            double wanted = this.brightness.get() / 100.0;
            if (!this.atNight.get() && mc.level != null && !mc.level.isBrightOutside()) {
               wanted = this.savedGamma;
            }

            setRaw(gamma, wanted);
         } else if (this.applied) {
            setRaw(gamma, this.savedGamma);
            this.applied = false;
         }
      }
   }

   private static void setRaw(OptionInstance<Double> option, double value) {
      ((SimpleOptionValueAccessor)(Object)option).lyfwclient$setRawValue(value);
   }
}
