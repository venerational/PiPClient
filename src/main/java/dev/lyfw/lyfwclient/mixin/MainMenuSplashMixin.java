package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.MainMenuModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SplashRenderer.class})
public class MainMenuSplashMixin {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$hideSplash(GuiGraphics context, int centerX, Font textRenderer, float alpha, CallbackInfo ci) {
      if (ModuleManager.get("Main Menu") instanceof MainMenuModule menu && menu.isEnabled() && menu.hideSplash()) {
         ci.cancel();
      }
   }
}
