package dev.lyfw.lyfwclient.module;

import dev.lyfw.lyfwclient.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;

public abstract class Module {
   public final String name;
   public final String description;
   public final Category category;
   public boolean tagNew;
   public boolean tagUpdated;
   public boolean favorite;
   public boolean hidden;
   private final List<Setting<?>> settings = new ArrayList<>();
   private boolean enabled;
   private final boolean enabledByDefault;

   protected Module(String name, String description, Category category, boolean enabledByDefault) {
      this.name = name;
      this.description = description;
      this.category = category;
      this.enabled = enabledByDefault;
      this.enabledByDefault = enabledByDefault;
   }

   public boolean enabledByDefault() {
      return this.enabledByDefault;
   }

   protected <S extends Setting<?>> S register(S setting) {
      this.settings.add(setting);
      return setting;
   }

   public List<Setting<?>> getSettings() {
      return this.settings;
   }

   public void resetSettingsToDefault() {
      for (Setting<?> setting : this.settings) {
         setting.resetToDefault();
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void init() {
   }

   public void tick() {
   }

   public Screen dedicatedSettingsScreen(Screen parent) {
      return null;
   }
}
