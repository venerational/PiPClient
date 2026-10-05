package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import dev.lyfw.lyfwclient.render.CosmeticLoadout;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AvatarRenderer.class})
public class CosmeticsCapeMixin {
   private static final Texture PLAIN_ELYTRA = new ResourceTexture(
      Identifier.withDefaultNamespace("textures/entity/equipment/wings/elytra.png"),
      Identifier.withDefaultNamespace("textures/entity/equipment/wings/elytra.png")
   );

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$cosmeticCape(Avatar entity, AvatarRenderState state, float tickDelta, CallbackInfo ci) {
      CosmeticsModule cosmetics = CosmeticsModule.get();
      if (cosmetics != null && entity instanceof Player player && state.skin != null) {
         CosmeticLoadout look = cosmetics.loadoutFor(player);
         Identifier texture = look == null ? null : cosmetics.capeTexture(look.cape());
         if (texture != null) {
            Texture asset = new ResourceTexture(texture, texture);
            PlayerSkin skin = state.skin;
            Texture elytra = look.cape().style() == CosmeticsModule.CapeStyle.MINECRAFT ? asset : (skin.elytra() != null ? skin.elytra() : PLAIN_ELYTRA);
            state.skin = new PlayerSkin(skin.body(), asset, elytra, skin.model(), skin.secure());
            state.showCape = true;
         }
      }
   }
}
