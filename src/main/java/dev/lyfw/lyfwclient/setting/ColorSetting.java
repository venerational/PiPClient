package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.IntUnaryOperator;

public class ColorSetting extends Setting<Integer> {
   public static IntUnaryOperator override;
   private boolean exempt;

   public ColorSetting(String name, int argb) {
      super(name, argb);
   }

   public ColorSetting exemptFromGlobalColor() {
      this.exempt = true;
      return this;
   }

   public Integer get() {
      int stored = (Integer)super.get();
      return override != null && !this.exempt ? override.applyAsInt(stored) : stored;
   }

   public int raw() {
      return (Integer)super.get();
   }

   public int a() {
      return this.get() >> 24 & 0xFF;
   }

   public int r() {
      return this.get() >> 16 & 0xFF;
   }

   public int g() {
      return this.get() >> 8 & 0xFF;
   }

   public int b() {
      return this.get() & 0xFF;
   }

   public int channel(int channel) {
      return switch (channel) {
         case 0 -> this.r();
         case 1 -> this.g();
         case 2 -> this.b();
         case 3 -> this.a();
         default -> 0;
      };
   }

   public void setChannel(int channel, int amount) {
      amount = Math.max(0, Math.min(255, amount));
      int stored = this.raw();
      int a = stored >> 24 & 0xFF;
      int r = stored >> 16 & 0xFF;
      int g = stored >> 8 & 0xFF;
      int b = stored & 0xFF;
      switch (channel) {
         case 0:
            r = amount;
            break;
         case 1:
            g = amount;
            break;
         case 2:
            b = amount;
            break;
         case 3:
            a = amount;
      }

      this.set(a << 24 | r << 16 | g << 8 | b);
   }

   public int rgb() {
      return this.get() & 16777215;
   }

   @Override
   public void write(JsonObject json) {
      json.addProperty(this.name, this.raw());
   }

   @Override
   public void read(JsonElement element) {
      this.set(element.getAsInt());
   }
}
