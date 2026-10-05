package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.HealthRingModule;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public class HealthRingMixin {
   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$renderHealthRing(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      HealthRingModule ring = HealthRingModule.get();
      if (ring != null) {
         ring.render(context);
      }
   }
}
