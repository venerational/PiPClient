package dev.lyfw.lyfwclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

public class KeybindSetting extends Setting<KeyMapping> {
   public KeybindSetting(String name, KeyMapping keyBinding) {
      super(name, keyBinding);
   }

   public KeyMapping keyBinding() {
      return this.value;
   }

   public String display() {
      return this.keyBinding().getTranslatedKeyMessage().getString();
   }

   public boolean isUnbound() {
      return this.keyBinding().saveString().equals(InputConstants.UNKNOWN.getName());
   }

   public void bind(KeyEvent input) {
      this.keyBinding().setKey(InputConstants.getKey(input));
      this.apply();
   }

   public void unbind() {
      this.keyBinding().setKey(InputConstants.UNKNOWN);
      this.apply();
   }

   @Override
   public void resetToDefault() {
      this.keyBinding().setKey(this.keyBinding().getDefaultKey());
      this.apply();
   }

   private void apply() {
      KeyMapping.resetMapping();
      Minecraft.getInstance().options.save();
   }

   @Override
   public void write(JsonObject json) {
   }

   @Override
   public void read(JsonElement element) {
   }
}
