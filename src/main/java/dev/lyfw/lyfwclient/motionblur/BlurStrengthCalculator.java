package dev.lyfw.lyfwclient.motionblur;

public class BlurStrengthCalculator {
   public BlurStrengthCalculator.Result calculate(float baseStrength, float fps, int refreshRate, boolean scalingEnabled) {
      if (!scalingEnabled) {
         return new BlurStrengthCalculator.Result(baseStrength, 100);
      } else {
         float fpsOverRefresh = refreshRate > 0 ? fps / refreshRate : 1.0F;
         if (fpsOverRefresh < 1.0F) {
            fpsOverRefresh = 1.0F;
         }

         float scaledStrength = baseStrength * fpsOverRefresh;
         int sampleAmount = fpsOverRefresh > 1.0F ? (int)(100.0F * fpsOverRefresh) : 100;
         return new BlurStrengthCalculator.Result(scaledStrength, sampleAmount);
      }
   }

   public record Result(float strength, int sampleAmount) {
   }
}
