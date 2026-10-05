package dev.lyfw.lyfwclient.gui;

import java.util.HashMap;
import java.util.Map;

public final class Motion {
   private final Map<Object, float[]> values = new HashMap<>();
   private long lastNanos = System.nanoTime();
   private float dt;

   public void frame() {
      long now = System.nanoTime();
      this.dt = Math.min(0.1F, (float)(now - this.lastNanos) / 1.0E9F);
      this.lastNanos = now;
   }

   public float dt() {
      return this.dt;
   }

   public float follow(Object key, float target, float rate) {
      return this.follow(key, target, target, rate);
   }

   public float follow(Object key, float start, float target, float rate) {
      float[] value = this.values.computeIfAbsent(key, k -> new float[]{start});
      value[0] += (target - value[0]) * (1.0F - (float)Math.exp(-this.dt * rate));
      if (Math.abs(target - value[0]) < 0.002F) {
         value[0] = target;
      }

      return value[0];
   }

   public void set(Object key, float value) {
      this.values.computeIfAbsent(key, k -> new float[1])[0] = value;
   }
}
