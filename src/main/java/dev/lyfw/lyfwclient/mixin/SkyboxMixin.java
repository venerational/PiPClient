package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.SkyboxModule;
import dev.lyfw.lyfwclient.render.SkyImage;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SkyRenderer.class})
public class SkyboxMixin {
   @Inject(
      method = {"extractRenderState"},
      at = {@At("RETURN")}
   )
   private void lyfwclient$overrideSky(ClientLevel world, float tickProgress, Camera camera, SkyRenderState state, CallbackInfo ci) {
      SkyboxModule skybox = SkyboxModule.get();
      if (skybox != null) {
         if (skybox.skyOverridden()) {
            state.skyColor = skybox.sky();
         }

         if (skybox.starsOverridden()) {
            state.starBrightness = skybox.stars();
         }
      }
   }

   @Inject(
      method = {"renderSkyDisc"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$drawSkyImage(int color, CallbackInfo ci) {
      SkyboxModule skybox = SkyboxModule.get();
      Identifier frame = skybox == null ? null : skybox.imageFrame();
      if (frame != null) {
         SkyImage.draw(frame, skybox.imageOpacity());
      }
   }
}
