package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.OptimizersModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"com/deathmotion/marlowcrystal/handler/InteractHandler"},
   remap = false
)
public class OptimizersMarlowMixin {
   @Inject(
      method = {"attack", "method_34218"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0,
      remap = false
   )
   private void pip$optimizersMarlow(CallbackInfo ci) {
      if (!OptimizersModule.allowsMarlow()) {
         ci.cancel();
      }
   }
}
