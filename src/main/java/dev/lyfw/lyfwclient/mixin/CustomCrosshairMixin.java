package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.CustomCrosshairModule;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public class CustomCrosshairMixin {
   @Inject(
      method = {"renderCrosshair"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$cancelVanillaCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      Module module = ModuleManager.get("Custom Crosshair");
      if (module != null && module.isEnabled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$renderCustomCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      if (ModuleManager.get("Custom Crosshair") instanceof CustomCrosshairModule crosshair) {
         crosshair.render(context, tickCounter);
      }
   }
}
