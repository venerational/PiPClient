package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;

public class TimeChangerModule extends Module {
   private final BooleanSetting overrideTime = this.register(new BooleanSetting("Override Time", false));
   private final SliderSetting time = this.register(new TimeChangerModule.TimeSlider());
   private final BooleanSetting overrideWeather = this.register(new BooleanSetting("Override Weather", false));
   private final EnumSetting<TimeChangerModule.WeatherState> weather = this.register(new EnumSetting<>("Weather", TimeChangerModule.WeatherState.CLEAR));

   public TimeChangerModule() {
      super(
         "Time Changer",
         "Overrides the rendered time of day and/or weather - purely visual, client-side only, matches \"Time & Weather Changer\".",
         Category.RENDER,
         false
      );
   }

   public boolean overrideTime() {
      return this.isEnabled() && this.overrideTime.get();
   }

   public long time() {
      return this.time.getInt();
   }

   public boolean overrideWeather() {
      return this.isEnabled() && this.overrideWeather.get();
   }

   public TimeChangerModule.WeatherState weather() {
      return this.weather.get();
   }

   private static String timeOfDayName(int ticks) {
      if (ticks <= 1000) {
         return "Sunrise";
      } else if (ticks <= 12000) {
         return "Daytime";
      } else if (ticks <= 13000) {
         return "Sunset";
      } else {
         return ticks <= 23000 ? "Night" : "Sunrise";
      }
   }

   private static final class TimeSlider extends SliderSetting {
      TimeSlider() {
         super("Time", 1000.0, 0.0, 24000.0, 100.0, "");
      }

      @Override
      public String display() {
         int ticks = this.getInt();
         return ticks + " (" + TimeChangerModule.timeOfDayName(ticks) + ")";
      }
   }

   public static enum WeatherState {
      CLEAR,
      RAIN,
      THUNDER;
   }
}
