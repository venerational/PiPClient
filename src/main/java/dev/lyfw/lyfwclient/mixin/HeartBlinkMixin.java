package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.HurtCamModule;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({Gui.class})
public class HeartBlinkMixin {
   @ModifyVariable(
      method = {"renderHearts"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private boolean lyfwclient$applyHeartBlink(boolean blinking) {
      return ModuleManager.get("Hurt Cam") instanceof HurtCamModule hurtCam && hurtCam.isEnabled() && !hurtCam.heartBlink() ? false : blinking;
   }
}
