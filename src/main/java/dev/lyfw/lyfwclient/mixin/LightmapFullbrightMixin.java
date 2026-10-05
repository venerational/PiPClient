package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.ToggleBrightnessModule;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LightTexture.class})
public class LightmapFullbrightMixin {
   @Shadow
   @Final
   private GpuTexture texture;
   @Shadow
   private boolean updateLightTexture;

   @Inject(
      method = {"updateLightTexture"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$forceFullbright(float tickProgress, CallbackInfo ci) {
      if (ModuleManager.get("Toggle Brightness") instanceof ToggleBrightnessModule tb && tb.isEnabled()) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.texture, -1);
         this.updateLightTexture = false;
         ci.cancel();
      }
   }
}
