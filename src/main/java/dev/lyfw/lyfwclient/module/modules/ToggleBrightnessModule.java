package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.LyfwClient;
import dev.lyfw.lyfwclient.mixin.SimpleOptionValueAccessor;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.KeybindSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

public class ToggleBrightnessModule extends Module {
   private static final double FULL_BRIGHT_GAMMA = 15.0;
   private final KeybindSetting keybind = this.register(new KeybindSetting("Keybind", LyfwClient.toggleBrightnessKey));
   private double savedGamma;
   private boolean applied;

   public ToggleBrightnessModule() {
      super("Toggle Brightness", "Instantly toggles full brightness - matches \"Full Brightness Toggle\".", Category.RENDER, false);
   }

   @Override
   public void tick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.options != null) {
         OptionInstance<Double> gammaOption = mc.options.gamma();
         if (this.isEnabled()) {
            if (!this.applied || (Double)gammaOption.get() != this.savedGamma && (Double)gammaOption.get() != 15.0) {
               this.savedGamma = (Double)gammaOption.get();
               this.applied = true;
            }

            setRaw(gammaOption, 15.0);
         } else if (this.applied) {
            setRaw(gammaOption, this.savedGamma);
            this.applied = false;
         }
      }
   }

   private static void setRaw(OptionInstance<Double> option, double value) {
      ((SimpleOptionValueAccessor)(Object)option).lyfwclient$setRawValue(value);
   }
}
