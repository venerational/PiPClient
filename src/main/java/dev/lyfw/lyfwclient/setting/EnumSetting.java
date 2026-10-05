package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
   private final Class<E> type;

   public EnumSetting(String name, E defaultValue) {
      super(name, defaultValue);
      this.type = defaultValue.getDeclaringClass();
   }

   public E[] options() {
      return this.type.getEnumConstants();
   }

   public void cycle() {
      E[] values = this.options();
      int next = (this.get().ordinal() + 1) % values.length;
      this.set(values[next]);
   }

   public void setByName(String name) {
      for (E e : this.options()) {
         if (e.name().equals(name)) {
            this.set(e);
            return;
         }
      }
   }

   @Override
   public void write(JsonObject json) {
      json.addProperty(this.name, this.get().name());
   }

   @Override
   public void read(JsonElement element) {
      this.setByName(element.getAsString());
   }
}
