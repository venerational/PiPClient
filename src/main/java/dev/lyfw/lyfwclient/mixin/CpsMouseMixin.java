package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.CpsTracker;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MouseHandler.class})
public class CpsMouseMixin {
   @Inject(
      method = {"onButton"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$onMouseButton(long window, MouseButtonInfo input, int action, CallbackInfo ci) {
      if (action == 1) {
         if (input.button() == 0) {
            CpsTracker.recordLeftClick();
         } else if (input.button() == 1) {
            CpsTracker.recordRightClick();
         }
      }
   }
}
