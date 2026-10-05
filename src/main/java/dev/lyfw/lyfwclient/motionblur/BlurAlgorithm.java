package dev.lyfw.lyfwclient.motionblur;

public enum BlurAlgorithm {
   VELOCITY_BASED,
   FRAME_BLENDING,
   HYBRID_BLENDING,
   ACCUMULATION_MAX,
   ACCUMULATION_MIX;

   public boolean usesVelocityBlur() {
      return this == VELOCITY_BASED || this == HYBRID_BLENDING;
   }

   public boolean allowsRefreshRateScaling() {
      return this == VELOCITY_BASED;
   }

   public boolean locksStrengthToOne() {
      return this == HYBRID_BLENDING;
   }

   public boolean showsStrengthSlider() {
      return this != FRAME_BLENDING && !this.locksStrengthToOne();
   }
}
