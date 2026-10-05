package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SliderSetting extends Setting<Double> {
   public final double min;
   public final double max;
   public final double step;
   public final String suffix;

   public SliderSetting(String name, double defaultValue, double min, double max, double step, String suffix) {
      super(name, defaultValue);
      this.min = min;
      this.max = max;
      this.step = step;
      this.suffix = suffix;
   }

   public void set(Double value) {
      super.set(Math.max(this.min, Math.min(this.max, value)));
   }

   public double getFraction() {
      return (this.get() - this.min) / (this.max - this.min);
   }

   public void setFraction(double fraction) {
      double raw = this.min + Math.max(0.0, Math.min(1.0, fraction)) * (this.max - this.min);
      if (this.step > 0.0) {
         raw = Math.round(raw / this.step) * this.step;
      }

      this.set(Math.max(this.min, Math.min(this.max, raw)));
   }

   public int getInt() {
      return (int)Math.round(this.get());
   }

   public String display() {
      String formatted = this.step < 1.0 ? String.format("%.2f", this.get()) : String.valueOf(this.getInt());
      return formatted + this.suffix;
   }

   @Override
   public void write(JsonObject json) {
      json.addProperty(this.name, this.get());
   }

   @Override
   public void read(JsonElement element) {
      this.set(element.getAsDouble());
   }
}
