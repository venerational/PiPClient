package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.MainMenuModule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class})
public class MainMenuBackgroundMixin {
   @Inject(
      method = {"renderPanorama"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$customMenuPanorama(GuiGraphics context, float delta, CallbackInfo ci) {
      if (this.lyfwclient$drawMenuBackground(context)) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$customMenuBackground(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.lyfwclient$drawMenuBackground(context)) {
         ci.cancel();
      }
   }

   @Unique
   private boolean lyfwclient$drawMenuBackground(GuiGraphics context) {
      if ((Object)this instanceof TitleScreen screen && ModuleManager.get("Main Menu") instanceof MainMenuModule menu && menu.isEnabled()) {
         menu.drawBackground(context, screen.width, screen.height);
         return true;
      } else {
         return false;
      }
   }
}
