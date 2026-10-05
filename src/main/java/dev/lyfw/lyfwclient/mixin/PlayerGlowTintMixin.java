package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.modules.PlayerGlowModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SubmitNodeCollection.class})
public class PlayerGlowTintMixin {
   private int lyfwclient$glowTint = -1;

   @Inject(
      method = {"submitModel"},
      at = {@At("HEAD")}
   )
   private <S> void lyfwclient$pickTint(
      Model<? super S> model,
      S state,
      PoseStack matrices,
      RenderType layer,
      int light,
      int overlay,
      int color,
      TextureAtlasSprite sprite,
      int outlineColor,
      CrumblingOverlay crumbling,
      CallbackInfo ci
   ) {
      this.lyfwclient$glowTint = -1;
      PlayerGlowModule glow = PlayerGlowModule.get();
      Minecraft client = Minecraft.getInstance();
      if (glow != null
         && glow.isEnabled()
         && client.level != null
         && state instanceof AvatarRenderState player
         && client.level.getEntity(player.id) instanceof Player wearer
         && glow.glows(wearer)) {
         this.lyfwclient$glowTint = glow.fillTint(wearer);
      }
   }

   @ModifyVariable(
      method = {"submitModel"},
      at = @At("HEAD"),
      ordinal = 2,
      argsOnly = true
   )
   private int lyfwclient$applyTint(int color) {
      int tint = this.lyfwclient$glowTint;
      if (tint == -1) {
         return color;
      } else {
         int r = (color >> 16 & 0xFF) * (tint >> 16 & 0xFF) / 255;
         int g = (color >> 8 & 0xFF) * (tint >> 8 & 0xFF) / 255;
         int b = (color & 0xFF) * (tint & 0xFF) / 255;
         return color & 0xFF000000 | r << 16 | g << 8 | b;
      }
   }
}
