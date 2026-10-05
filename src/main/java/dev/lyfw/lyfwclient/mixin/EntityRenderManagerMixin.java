package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityRenderDispatcher.class})
public class EntityRenderManagerMixin {
   @Inject(
      method = {"shouldRender"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void lyfwclient$hidePlayerModel(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue() && entity == Minecraft.getInstance().player) {
         Module module = ModuleManager.get("Hide Player Model");
         if (module != null && module.isEnabled()) {
            cir.setReturnValue(false);
         }
      }
   }
}
