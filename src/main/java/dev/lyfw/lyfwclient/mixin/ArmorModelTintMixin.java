package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.DamageTintModule;
import dev.lyfw.lyfwclient.render.ArmorHurtRenderLayers;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SubmitNodeCollection.class})
public class ArmorModelTintMixin {
   private boolean lyfwclient$pendingDamageTintArmor;

   @Inject(
      method = {"submitModel"},
      at = {@At("HEAD")}
   )
   private <S> void lyfwclient$stashTint(
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
      this.lyfwclient$pendingDamageTintArmor = false;
      if (state instanceof LivingEntityRenderState les
         && les.hasRedOverlay
         && ModuleManager.get("Damage Tint") instanceof DamageTintModule tint
         && tint.usesArmorModelTint()) {
         this.lyfwclient$pendingDamageTintArmor = layer != RenderTypes.armorEntityGlint();
      }
   }

   @ModifyVariable(
      method = {"submitModel"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private RenderType lyfwclient$applyLayer(RenderType layer) {
      if (!this.lyfwclient$pendingDamageTintArmor) {
         return layer;
      } else {
         RenderType hurt = ArmorHurtRenderLayers.withHurtOverlay(layer);
         return hurt != null ? hurt : layer;
      }
   }

   @ModifyVariable(
      method = {"submitModel"},
      at = @At("HEAD"),
      ordinal = 1,
      argsOnly = true
   )
   private int lyfwclient$applyOverlay(int overlay) {
      return this.lyfwclient$pendingDamageTintArmor ? OverlayTexture.pack(0, OverlayTexture.v(true)) : overlay;
   }
}
