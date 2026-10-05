package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.NametagsModule;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AvatarRenderer.class})
public class PlayerEntityRendererMixin {
   @Inject(
      method = {"submitNameTag(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$renderLabel(AvatarRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      if (ModuleManager.get("Nametags") instanceof NametagsModule np && np.isEnabled()) {
         float scale = np.scale();
         if (scale != 1.0F) {
            matrices.scale(scale, scale, scale);
         }
      }
   }
}
