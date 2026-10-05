package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.SoundControlModule;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractSoundInstance.class})
public class SoundControlMixin {
   @Inject(
      method = {"getVolume"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void lyfwclient$scaleVolume(CallbackInfoReturnable<Float> cir) {
      SoundInstance sound = (SoundInstance)this;
      if (sound.getIdentifier() != null && ModuleManager.get("Sound Control") instanceof SoundControlModule control) {
         float factor = control.multiplier(sound.getIdentifier());
         if (factor != 1.0F) {
            cir.setReturnValue(Math.max(0.0F, (Float)cir.getReturnValue() * factor));
         }
      }
   }
}
