package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.ProfilePresetsScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import net.minecraft.client.Minecraft;

public class ProfilePresetsModule extends Module {
   public ProfilePresetsModule() {
      super(
         "Profile Presets", "Opens a screen to save/load named snapshots of your configs, options, keybinds, and active resource packs.", Category.MISC, false
      );
   }

   @Override
   public void setEnabled(boolean enabled) {
      if (enabled) {
         Minecraft client = Minecraft.getInstance();
         client.setScreen(new ProfilePresetsScreen(client.screen));
      }
   }
}
