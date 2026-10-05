package dev.lyfw.lyfwclient.gui;

import net.minecraft.util.Mth;

public final class Anim {
   private final float seconds;
   private long startNanos;

   public Anim(float seconds) {
      this.seconds = seconds;
      this.startNanos = System.nanoTime();
   }

   public void restart() {
      this.startNanos = System.nanoTime();
   }

   public float linear() {
      return this.seconds <= 0.0F ? 1.0F : Mth.clamp((float)(System.nanoTime() - this.startNanos) / 1.0E9F / this.seconds, 0.0F, 1.0F);
   }

   public float eased() {
      float t = 1.0F - this.linear();
      return 1.0F - t * t * t;
   }

   public int drop(int pixels) {
      return Math.round((1.0F - this.eased()) * pixels);
   }

   public int fade(int argb) {
      return Math.round((argb >>> 24) * this.eased()) << 24 | argb & 16777215;
   }
}
