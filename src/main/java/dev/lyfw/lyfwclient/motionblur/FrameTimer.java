package dev.lyfw.lyfwclient.motionblur;

public class FrameTimer {
   private long lastNano = 0L;
   private float currentFPS = 0.0F;

   public void beginFrame() {
      long now = System.nanoTime();
      float delta = (float)(now - this.lastNano) / 1.0E9F;
      this.lastNano = now;
      this.currentFPS = delta > 0.0F && delta < 1.0F ? 1.0F / delta : 0.0F;
      MonitorInfoProvider.updateDisplayInfo();
   }

   public float getFPS() {
      return this.currentFPS;
   }

   public int getRefreshRate() {
      return MonitorInfoProvider.getRefreshRate();
   }
}
