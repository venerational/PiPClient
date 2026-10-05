package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ChoiceSetting extends Setting<String> {
   private final Supplier<List<String>> options;

   public ChoiceSetting(String name, String defaultValue, Supplier<List<String>> options) {
      super(name, defaultValue);
      this.options = options;
   }

   public List<String> options() {
      List<String> found = this.options.get();
      if (found.isEmpty()) {
         return List.of(this.get());
      } else if (found.contains(this.get())) {
         return found;
      } else {
         List<String> withCurrent = new ArrayList<>(found);
         withCurrent.add(0, this.get());
         return List.copyOf(withCurrent);
      }
   }

   public void cycle(boolean back) {
      List<String> all = this.options();
      int at = all.indexOf(this.get());
      int next = at < 0 ? 0 : (at + (back ? -1 : 1) + all.size()) % all.size();
      this.set(all.get(next));
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
