package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Setting<T> {
   public final String name;
   public String group;
   public final T defaultValue;
   protected T value;

   protected Setting(String name, T defaultValue) {
      this.name = name;
      this.value = defaultValue;
      this.defaultValue = defaultValue;
   }

   public T get() {
      return this.value;
   }

   public void set(T value) {
      this.value = value;
   }

   public void resetToDefault() {
      this.set(this.defaultValue);
   }

   public abstract void write(JsonObject jsonObject);

   public abstract void read(JsonElement jsonElement);
}
