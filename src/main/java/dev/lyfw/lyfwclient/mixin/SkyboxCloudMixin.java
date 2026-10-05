package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.SkyboxModule;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({CloudRenderer.class})
public class SkyboxCloudMixin {
   @ModifyVariable(
      method = {"render"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private int lyfwclient$overrideClouds(int color) {
      SkyboxModule skybox = SkyboxModule.get();
      return skybox != null && skybox.cloudsOverridden() ? skybox.clouds() : color;
   }
}
