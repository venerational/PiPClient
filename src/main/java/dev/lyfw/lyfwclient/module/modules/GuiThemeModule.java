package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.ThemeScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import net.minecraft.client.Minecraft;

public class GuiThemeModule extends Module {
   public GuiThemeModule() {
      super(
         "GUI Theme",
         "Opens the theme picker: swap the whole GUI look between presets (including a vanilla Minecraft style) and set the accent color.",
         Category.MISC,
         false
      );
   }

   @Override
   public void setEnabled(boolean enabled) {
      if (enabled) {
         Minecraft client = Minecraft.getInstance();
         client.setScreen(new ThemeScreen(client.screen));
      }
   }
}
