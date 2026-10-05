package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.ModuleSettingsScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.gui.screens.Screen;

public class TotemParticlesModule extends Module {
   private final SliderSetting multiplier = this.register(new SliderSetting("Multiplier", 0.9, 0.0, 5.0, 0.1, "x"));
   private final BooleanSetting showOwnParticles = this.register(new BooleanSetting("Show Own Particles", true));
   private final BooleanSetting useEmitter = this.register(new BooleanSetting("Custom Emitter", true));
   private final SliderSetting emitterLifetime = this.register(new SliderSetting("Emitter Lifetime", 25.0, 0.0, 50.0, 5.0, " ticks"));
   private final SliderSetting emitterYOffset = this.register(new SliderSetting("Vertical Offset", -0.2, -0.8, 2.0, 0.1, " blocks"));
   private final BooleanSetting emitterMovesWithPlayer = this.register(new BooleanSetting("Follow Entity", false));
   private final BooleanSetting hideOnGround = this.register(new BooleanSetting("Hide On Ground", false));
   private final BooleanSetting useCollisions = this.register(new BooleanSetting("Particle Collisions", true));
   private final SliderSetting lightLevel = this.register(new SliderSetting("Light Level (-1 = World)", 255.0, -1.0, 255.0, 1.0, ""));
   private final BooleanSetting useColor = this.register(new BooleanSetting("Custom Color", true));
   private final ColorSetting mainColor = this.register(new ColorSetting("Main Color", -65281));
   private final BooleanSetting doStartColor = this.register(new BooleanSetting("Enabled", true));
   private final ColorSetting startColor = this.register(new ColorSetting("Starting Color", -1));
   private final SliderSetting fadeToSpeed = this.register(new SliderSetting("Fade Speed", 0.3, 0.1, 1.0, 0.1, "x"));
   private final SliderSetting fadeToTime = this.register(new SliderSetting("Fade At", 0.0, 0.0, 0.5, 0.05, ""));
   private final BooleanSetting doOutColor = this.register(new BooleanSetting("Enabled", true));
   private final ColorSetting outTargetColor = this.register(new ColorSetting("Fade To Color", -16777216));
   private final SliderSetting fadeOutSpeed = this.register(new SliderSetting("Fade Speed", 0.2, 0.0, 1.0, 0.1, "x"));
   private final SliderSetting fadeOutTime = this.register(new SliderSetting("Fade At", 0.65, 0.5, 1.0, 0.05, ""));
   private final BooleanSetting doRainbow = this.register(new BooleanSetting("Enabled", false));
   private final BooleanSetting startColorRainbow = this.register(new BooleanSetting("Random Start Color", true));
   private final BooleanSetting rainbowOverTime = this.register(new BooleanSetting("Rainbow Over Time", true));
   private final EnumSetting<TotemParticlesModule.RainbowMode> rainbowMode = this.register(
      new EnumSetting<>("Rainbow Mode", TotemParticlesModule.RainbowMode.MAIN)
   );
   private final SliderSetting rainbowSpeed = this.register(new SliderSetting("Speed", 2.0, 1.0, 10.0, 1.0, "x"));
   private final BooleanSetting syncRainbow = this.register(new BooleanSetting("Sync", true));
   private final BooleanSetting useAlpha = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minAlpha = this.register(new SliderSetting("Minimum Alpha", 0.75, 0.0, 1.0, 0.05, ""));
   private final SliderSetting maxAlpha = this.register(new SliderSetting("Maximum Alpha", 1.0, 0.0, 1.0, 0.05, ""));
   private final BooleanSetting loseAlpha = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting alphaOutSpeed = this.register(new SliderSetting("Speed", -0.03, -0.1, 0.1, 0.01, ""));
   private final SliderSetting alphaOutTime = this.register(new SliderSetting("Fade At", 0.5, 0.0, 1.0, 0.05, ""));
   private final BooleanSetting fadeOnGround = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting onGroundFade = this.register(new SliderSetting("Speed", -0.05, -0.1, 0.1, 0.01, ""));
   private final BooleanSetting useScale = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minScale = this.register(new SliderSetting("Minimum Scale", 0.25, 0.05, 2.0, 0.05, ""));
   private final SliderSetting maxScale = this.register(new SliderSetting("Maximum Scale", 0.75, 0.05, 2.0, 0.05, ""));
   private final BooleanSetting scaleOverTime = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting scaleAmount = this.register(new SliderSetting("Amount", -0.02, -0.1, 0.1, 0.01, ""));
   private final SliderSetting scaleAtPercent = this.register(new SliderSetting("Scale At", 0.75, 0.0, 1.0, 0.05, ""));
   private final BooleanSetting scaleOnGround = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting onGroundScale = this.register(new SliderSetting("Amount", -0.01, -0.2, 0.2, 0.01, ""));
   private final BooleanSetting useAge = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minAge = this.register(new SliderSetting("Minimum Age", 40.0, 10.0, 200.0, 5.0, " ticks"));
   private final SliderSetting maxAge = this.register(new SliderSetting("Maximum Age", 45.0, 10.0, 200.0, 5.0, " ticks"));
   private final BooleanSetting useMovement = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minVelocityMultiplier = this.register(new SliderSetting("Minimum Velocity Multiplier", 0.25, 0.0, 1.0, 0.05, ""));
   private final SliderSetting maxVelocityMultiplier = this.register(new SliderSetting("Maximum Velocity Multiplier", 0.6, 0.0, 1.0, 0.05, ""));
   private final BooleanSetting customVelocity = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minXVelocity = this.register(new SliderSetting("Minimum X Velocity", -0.65, -3.0, 3.0, 0.05, ""));
   private final SliderSetting maxXVelocity = this.register(new SliderSetting("Maximum X Velocity", 0.65, -3.0, 3.0, 0.05, ""));
   private final SliderSetting minYVelocity = this.register(new SliderSetting("Minimum Y Velocity", 0.25, -3.0, 3.0, 0.05, ""));
   private final SliderSetting maxYVelocity = this.register(new SliderSetting("Maximum Y Velocity", 1.5, -3.0, 3.0, 0.05, ""));
   private final SliderSetting minZVelocity = this.register(new SliderSetting("Minimum Z Velocity", -0.65, -3.0, 3.0, 0.05, ""));
   private final SliderSetting maxZVelocity = this.register(new SliderSetting("Maximum Z Velocity", 0.65, -3.0, 3.0, 0.05, ""));
   private final BooleanSetting useGravity = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minUpwardsAccel = this.register(new SliderSetting("Minimum Gravity Multiplier", -0.3, -3.0, 3.0, 0.05, ""));
   private final SliderSetting maxUpwardsAccel = this.register(new SliderSetting("Maximum Gravity Multiplier", 0.75, -3.0, 3.0, 0.05, ""));
   private final BooleanSetting gravityOverTime = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting changeGravityAtPercent = this.register(new SliderSetting("Change At", 0.65, 0.0, 1.0, 0.05, ""));
   private final SliderSetting gravityOverTimeAmount = this.register(new SliderSetting("Amount", -0.5, -0.5, 0.5, 0.01, ""));
   private final BooleanSetting useRotation = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minStartRotation = this.register(new SliderSetting("Minimum Start Rotation", -360.0, -360.0, 360.0, 5.0, "°"));
   private final SliderSetting maxStartRotation = this.register(new SliderSetting("Maximum Start Rotation", 360.0, -360.0, 360.0, 5.0, "°"));
   private final BooleanSetting rotateOverTime = this.register(new BooleanSetting("Enabled", true));
   private final SliderSetting minRotationSpeed = this.register(new SliderSetting("Minimum Rotation Speed", -0.35, -1.0, 1.0, 0.05, ""));
   private final SliderSetting maxRotationSpeed = this.register(new SliderSetting("Maximum Rotation Speed", 0.35, -1.0, 1.0, 0.05, ""));
   private final SliderSetting rotateAtPercent = this.register(new SliderSetting("Change At", 0.7, 0.0, 1.0, 0.05, ""));
   private final SliderSetting rotateOverTimeAmount = this.register(new SliderSetting("Amount", -0.15, -0.5, 0.5, 0.01, ""));
   private final BooleanSetting smartRotation = this.register(new BooleanSetting("Smart Mode", true));
   private final BooleanSetting rotateOnGround = this.register(new BooleanSetting("Rotate On Ground", false));

   public TotemParticlesModule() {
      super(
         "Totem Particles",
         "Fully customizes the totem-of-undying particle burst - color, alpha, scale, age, velocity, gravity, and rotation, all over the particle's lifetime.",
         Category.RENDER,
         false
      );
      this.multiplier.group = "General";
      this.showOwnParticles.group = "General";
      this.useEmitter.group = "Emitter";
      this.emitterLifetime.group = "Emitter";
      this.emitterYOffset.group = "Emitter";
      this.emitterMovesWithPlayer.group = "Emitter";
      this.hideOnGround.group = "Misc";
      this.useCollisions.group = "Misc";
      this.lightLevel.group = "Misc";
      this.useColor.group = "Color";
      this.mainColor.group = "Color";
      this.doStartColor.group = "Start Color";
      this.startColor.group = "Start Color";
      this.fadeToSpeed.group = "Start Color";
      this.fadeToTime.group = "Start Color";
      this.doOutColor.group = "End Color";
      this.outTargetColor.group = "End Color";
      this.fadeOutSpeed.group = "End Color";
      this.fadeOutTime.group = "End Color";
      this.doRainbow.group = "Rainbow";
      this.startColorRainbow.group = "Rainbow";
      this.rainbowOverTime.group = "Rainbow";
      this.rainbowMode.group = "Rainbow";
      this.rainbowSpeed.group = "Rainbow";
      this.syncRainbow.group = "Rainbow";
      this.useAlpha.group = "Alpha";
      this.minAlpha.group = "Alpha";
      this.maxAlpha.group = "Alpha";
      this.loseAlpha.group = "Fade Out";
      this.alphaOutSpeed.group = "Fade Out";
      this.alphaOutTime.group = "Fade Out";
      this.fadeOnGround.group = "Fade On Ground";
      this.onGroundFade.group = "Fade On Ground";
      this.useScale.group = "Scale";
      this.minScale.group = "Scale";
      this.maxScale.group = "Scale";
      this.scaleOverTime.group = "Scale Over Time";
      this.scaleAmount.group = "Scale Over Time";
      this.scaleAtPercent.group = "Scale Over Time";
      this.scaleOnGround.group = "Scale On Ground";
      this.onGroundScale.group = "Scale On Ground";
      this.useAge.group = "Age";
      this.minAge.group = "Age";
      this.maxAge.group = "Age";
      this.useMovement.group = "Motion";
      this.minVelocityMultiplier.group = "Motion";
      this.maxVelocityMultiplier.group = "Motion";
      this.customVelocity.group = "Custom Velocity";
      this.minXVelocity.group = "Custom Velocity";
      this.maxXVelocity.group = "Custom Velocity";
      this.minYVelocity.group = "Custom Velocity";
      this.maxYVelocity.group = "Custom Velocity";
      this.minZVelocity.group = "Custom Velocity";
      this.maxZVelocity.group = "Custom Velocity";
      this.useGravity.group = "Gravity";
      this.minUpwardsAccel.group = "Gravity";
      this.maxUpwardsAccel.group = "Gravity";
      this.gravityOverTime.group = "Gravity Over Time";
      this.changeGravityAtPercent.group = "Gravity Over Time";
      this.gravityOverTimeAmount.group = "Gravity Over Time";
      this.useRotation.group = "Rotation";
      this.minStartRotation.group = "Rotation";
      this.maxStartRotation.group = "Rotation";
      this.rotateOverTime.group = "Rotate Over Time";
      this.minRotationSpeed.group = "Rotate Over Time";
      this.maxRotationSpeed.group = "Rotate Over Time";
      this.rotateAtPercent.group = "Rotate Over Time";
      this.rotateOverTimeAmount.group = "Rotate Over Time";
      this.smartRotation.group = "Rotate Over Time";
      this.rotateOnGround.group = "Rotate Over Time";
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return new ModuleSettingsScreen(parent, this);
   }

   public float multiplier() {
      return (float)this.multiplier.get().doubleValue();
   }

   public boolean showOwnParticles() {
      return this.showOwnParticles.get();
   }

   public boolean useEmitter() {
      return this.useEmitter.get();
   }

   public int emitterLifetime() {
      return this.emitterLifetime.getInt();
   }

   public float emitterYOffset() {
      return (float)this.emitterYOffset.get().doubleValue();
   }

   public boolean emitterMovesWithPlayer() {
      return this.emitterMovesWithPlayer.get();
   }

   public boolean hideOnGround() {
      return this.hideOnGround.get();
   }

   public boolean useCollisions() {
      return this.useCollisions.get();
   }

   public int lightLevel() {
      return this.lightLevel.getInt();
   }

   public boolean useColor() {
      return this.useColor.get();
   }

   public int mainColor() {
      return this.mainColor.get();
   }

   public boolean doStartColor() {
      return this.doStartColor.get();
   }

   public int startColor() {
      return this.startColor.get();
   }

   public float fadeToSpeed() {
      return (float)this.fadeToSpeed.get().doubleValue();
   }

   public float fadeToTime() {
      return (float)this.fadeToTime.get().doubleValue();
   }

   public boolean doOutColor() {
      return this.doOutColor.get();
   }

   public int outTargetColor() {
      return this.outTargetColor.get();
   }

   public float fadeOutSpeed() {
      return (float)this.fadeOutSpeed.get().doubleValue();
   }

   public float fadeOutTime() {
      return (float)this.fadeOutTime.get().doubleValue();
   }

   public boolean doRainbow() {
      return this.doRainbow.get();
   }

   public boolean startColorRainbow() {
      return this.startColorRainbow.get();
   }

   public boolean rainbowOverTime() {
      return this.rainbowOverTime.get();
   }

   public TotemParticlesModule.RainbowMode rainbowMode() {
      return this.rainbowMode.get();
   }

   public int rainbowSpeed() {
      return this.rainbowSpeed.getInt();
   }

   public boolean syncRainbow() {
      return this.syncRainbow.get();
   }

   public boolean useAlpha() {
      return this.useAlpha.get();
   }

   public float minAlpha() {
      return (float)this.minAlpha.get().doubleValue();
   }

   public float maxAlpha() {
      return (float)this.maxAlpha.get().doubleValue();
   }

   public boolean loseAlpha() {
      return this.loseAlpha.get();
   }

   public float alphaOutSpeed() {
      return (float)this.alphaOutSpeed.get().doubleValue();
   }

   public float alphaOutTime() {
      return (float)this.alphaOutTime.get().doubleValue();
   }

   public boolean fadeOnGround() {
      return this.fadeOnGround.get();
   }

   public float onGroundFade() {
      return (float)this.onGroundFade.get().doubleValue();
   }

   public boolean useScale() {
      return this.useScale.get();
   }

   public float minScale() {
      return (float)this.minScale.get().doubleValue();
   }

   public float maxScale() {
      return (float)this.maxScale.get().doubleValue();
   }

   public boolean scaleOverTime() {
      return this.scaleOverTime.get();
   }

   public float scaleAmount() {
      return (float)this.scaleAmount.get().doubleValue();
   }

   public float scaleAtPercent() {
      return (float)this.scaleAtPercent.get().doubleValue();
   }

   public boolean scaleOnGround() {
      return this.scaleOnGround.get();
   }

   public float onGroundScale() {
      return (float)this.onGroundScale.get().doubleValue();
   }

   public boolean useAge() {
      return this.useAge.get();
   }

   public int minAge() {
      return this.minAge.getInt();
   }

   public int maxAge() {
      return this.maxAge.getInt();
   }

   public boolean useMovement() {
      return this.useMovement.get();
   }

   public float minVelocityMultiplier() {
      return (float)this.minVelocityMultiplier.get().doubleValue();
   }

   public float maxVelocityMultiplier() {
      return (float)this.maxVelocityMultiplier.get().doubleValue();
   }

   public boolean customVelocity() {
      return this.customVelocity.get();
   }

   public float minXVelocity() {
      return (float)this.minXVelocity.get().doubleValue();
   }

   public float maxXVelocity() {
      return (float)this.maxXVelocity.get().doubleValue();
   }

   public float minYVelocity() {
      return (float)this.minYVelocity.get().doubleValue();
   }

   public float maxYVelocity() {
      return (float)this.maxYVelocity.get().doubleValue();
   }

   public float minZVelocity() {
      return (float)this.minZVelocity.get().doubleValue();
   }

   public float maxZVelocity() {
      return (float)this.maxZVelocity.get().doubleValue();
   }

   public boolean useGravity() {
      return this.useGravity.get();
   }

   public float minUpwardsAccel() {
      return (float)this.minUpwardsAccel.get().doubleValue();
   }

   public float maxUpwardsAccel() {
      return (float)this.maxUpwardsAccel.get().doubleValue();
   }

   public boolean gravityOverTime() {
      return this.gravityOverTime.get();
   }

   public float changeGravityAtPercent() {
      return (float)this.changeGravityAtPercent.get().doubleValue();
   }

   public float gravityOverTimeAmount() {
      return (float)this.gravityOverTimeAmount.get().doubleValue();
   }

   public boolean useRotation() {
      return this.useRotation.get();
   }

   public int minStartRotation() {
      return this.minStartRotation.getInt();
   }

   public int maxStartRotation() {
      return this.maxStartRotation.getInt();
   }

   public boolean rotateOverTime() {
      return this.rotateOverTime.get();
   }

   public float minRotationSpeed() {
      return (float)this.minRotationSpeed.get().doubleValue();
   }

   public float maxRotationSpeed() {
      return (float)this.maxRotationSpeed.get().doubleValue();
   }

   public float rotateAtPercent() {
      return (float)this.rotateAtPercent.get().doubleValue();
   }

   public float rotateOverTimeAmount() {
      return (float)this.rotateOverTimeAmount.get().doubleValue();
   }

   public boolean smartRotation() {
      return this.smartRotation.get();
   }

   public boolean rotateOnGround() {
      return this.rotateOnGround.get();
   }

   public static enum RainbowMode {
      ALL,
      START,
      MAIN,
      END,
      UNTIL_END,
      AFTER_START,
      EXCLUDING_MAIN;
   }
}
