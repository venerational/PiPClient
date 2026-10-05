package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.TimeChangerModule;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientLevelData.class})
public class TimeOverrideMixin {
   @Inject(
      method = {"getDayTime"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$overrideTime(CallbackInfoReturnable<Long> cir) {
      if (ModuleManager.get("Time Changer") instanceof TimeChangerModule tc && tc.overrideTime()) {
         cir.setReturnValue(tc.time());
      }
   }
}
