package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BooleanSetting extends Setting<Boolean> {
   public BooleanSetting(String name, boolean defaultValue) {
      super(name, defaultValue);
   }

   @Override
   public void write(JsonObject json) {
      json.addProperty(this.name, this.get());
   }

   @Override
   public void read(JsonElement element) {
      this.set(element.getAsBoolean());
   }
}
