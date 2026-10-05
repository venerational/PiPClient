package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.HitboxModule;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityHitboxDebugRenderer.class})
public class VanillaHitboxMixin {
   @Inject(
      method = {"showHitboxes"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$yieldToModule(Entity entity, float tickProgress, boolean visible, CallbackInfo ci) {
      if (ModuleManager.get("Hitboxes") instanceof HitboxModule hitboxes && hitboxes.isEnabled()) {
         ci.cancel();
      }
   }
}
