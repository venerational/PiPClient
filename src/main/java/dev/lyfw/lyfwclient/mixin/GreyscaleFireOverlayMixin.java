package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.GreyscaleModule;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ScreenEffectRenderer.class})
public class GreyscaleFireOverlayMixin {
   @Inject(
      method = {"renderFire"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void lyfwclient$hideFireOverlay(CallbackInfo ci) {
      Module module = ModuleManager.get("Grayscale");
      if (module instanceof GreyscaleModule && module.isEnabled()) {
         ci.cancel();
      }
   }
}
