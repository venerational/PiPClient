package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.PipMixinPlugin;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.motionblur.BlurAlgorithm;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;

public class MotionBlurModule extends Module {
   private final BooleanSetting refreshRateScaling = this.register(new BooleanSetting("Refresh Rate Scaling", true));
   private final SliderSetting strength = this.register(new SliderSetting("Strength", 1.0, 0.0, 3.0, 0.05, "x"));
   private final EnumSetting<BlurAlgorithm> algorithm = this.register(new EnumSetting<>("Algorithm", BlurAlgorithm.VELOCITY_BASED));

   public MotionBlurModule() {
      super(
         "Motion Blur",
         "Camera and entity motion blur - matches \"Natural Motion Blur\". 5 algorithms: Velocity-Based, Frame Blending, Hybrid, Accumulation Max, Accumulation Mix.",
         Category.RENDER,
         false
      );
      if (PipMixinPlugin.vulkan()) {
         this.hidden = true;
      }
   }

   public static MotionBlurModule getInstance() {
      return ModuleManager.get("Motion Blur") instanceof MotionBlurModule motionBlurModule ? motionBlurModule : null;
   }

   public BlurAlgorithm algorithm() {
      return this.algorithm.get();
   }

   public boolean refreshRateScaling() {
      return this.refreshRateScaling.get();
   }

   public float effectiveStrength() {
      return this.algorithm.get().locksStrengthToOne() ? 1.0F : (float)this.strength.get().doubleValue();
   }
}
