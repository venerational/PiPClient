package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TimeChangerModule;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Level.class})
public class WeatherOverrideMixin {
   @Inject(
      method = {"getRainLevel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$overrideRain(float tickDelta, CallbackInfoReturnable<Float> cir) {
      if (ModuleManager.get("Time Changer") instanceof TimeChangerModule tc && tc.overrideWeather()) {
         cir.setReturnValue(tc.weather() == TimeChangerModule.WeatherState.CLEAR ? 0.0F : 1.0F);
      }
   }

   @Inject(
      method = {"getThunderLevel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$overrideThunder(float tickDelta, CallbackInfoReturnable<Float> cir) {
      if (ModuleManager.get("Time Changer") instanceof TimeChangerModule tc && tc.overrideWeather()) {
         cir.setReturnValue(tc.weather() == TimeChangerModule.WeatherState.THUNDER ? 1.0F : 0.0F);
      }
   }
}
