package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TextSetting extends Setting<String> {
   public TextSetting(String name, String defaultValue) {
      super(name, defaultValue);
   }

   @Override
   public void write(JsonObject json) {
      json.addProperty(this.name, this.get());
   }

   @Override
   public void read(JsonElement element) {
      this.set(element.getAsString());
   }
}
