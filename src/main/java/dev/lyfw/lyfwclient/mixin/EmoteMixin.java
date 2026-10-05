package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.render.CosmeticPreview;
import dev.lyfw.lyfwclient.render.EmoteMoves;
import dev.lyfw.lyfwclient.render.EmotePlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerModel.class})
public class EmoteMixin {
   @Inject(
      method = {"setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$emote(AvatarRenderState state, CallbackInfo ci) {
      PlayerModel model = (PlayerModel)(Object)this;
      if (state instanceof CosmeticPreview.State preview) {
         if (preview.emote != null) {
            EmoteMoves.pose(model, preview.emote, preview.emoteSeconds);
         }
      } else {
         Minecraft client = Minecraft.getInstance();
         if (EmotePlay.playing() != null && client.player != null && state.id == client.player.getId()) {
            EmoteMoves.pose(model, EmotePlay.playing(), EmotePlay.seconds());
         }
      }
   }
}
