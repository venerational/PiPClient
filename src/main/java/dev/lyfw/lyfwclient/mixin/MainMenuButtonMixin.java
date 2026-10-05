package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.MainMenuModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractButton.class})
public class MainMenuButtonMixin {
   @Inject(
      method = {"renderDefaultSprite"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$customMenuButton(GuiGraphics context, CallbackInfo ci) {
      if (Minecraft.getInstance().screen instanceof TitleScreen
         && ModuleManager.get("Main Menu") instanceof MainMenuModule menu
         && menu.isEnabled()
         && menu.styleButtons()) {
         menu.drawButton(context, (AbstractWidget)(Object)this);
         ci.cancel();
      }
   }
}
