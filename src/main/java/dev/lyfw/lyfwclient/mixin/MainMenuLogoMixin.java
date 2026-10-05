package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.MainMenuModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LogoRenderer.class})
public class MainMenuLogoMixin {
   @Inject(
      method = {"renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$customMenuLogo(GuiGraphics context, int screenWidth, float alpha, CallbackInfo ci) {
      if (Minecraft.getInstance().screen instanceof TitleScreen && ModuleManager.get("Main Menu") instanceof MainMenuModule menu && menu.isEnabled()) {
         menu.drawTitle(context, screenWidth);
         ci.cancel();
      }
   }
}
