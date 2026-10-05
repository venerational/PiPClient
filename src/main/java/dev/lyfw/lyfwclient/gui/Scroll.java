package dev.lyfw.lyfwclient.gui;

public final class Scroll {
   private static final float RATE = 18.0F;
   private float shown;
   private int target;
   private long lastNanos = System.nanoTime();

   public int target() {
      return this.target;
   }

   public void setTarget(int value) {
      this.target = value;
   }

   public void jump(int value) {
      this.target = value;
      this.shown = value;
      this.lastNanos = System.nanoTime();
   }

   public int shown() {
      long now = System.nanoTime();
      float dt = Math.min(0.25F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
      this.shown = this.shown + (this.target - this.shown) * (1.0F - (float)Math.exp(-dt * 18.0F));
      if (Math.abs(this.target - this.shown) < 0.5F) {
         this.shown = this.target;
      }

      return Math.round(this.shown);
   }
}
