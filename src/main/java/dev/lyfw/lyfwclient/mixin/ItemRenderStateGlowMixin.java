package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ItemGlowMarker;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.PlayerHeadGlowModule;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemStackRenderState.class})
public class ItemRenderStateGlowMixin implements ItemGlowMarker {
   @Shadow
   private int activeLayerCount;
   @Shadow
   private LayerRenderState[] layers;
   @Unique
   private boolean lyfwclient$playerHead;

   @Override
   public void lyfwclient$setPlayerHead(boolean playerHead) {
      this.lyfwclient$playerHead = playerHead;
   }

   @Override
   public boolean lyfwclient$isPlayerHead() {
      return this.lyfwclient$playerHead;
   }

   @Inject(
      method = {"submit"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$applyGlint(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, int seed, CallbackInfo ci) {
      if (this.lyfwclient$playerHead) {
         boolean enabled = ModuleManager.get("Player Head Glow") instanceof PlayerHeadGlowModule glow && glow.isEnabled();
         FoilType glint = enabled ? FoilType.STANDARD : FoilType.NONE;

         for (int i = 0; i < this.activeLayerCount; i++) {
            this.layers[i].setFoilType(glint);
         }
      }
   }

   @ModifyVariable(
      method = {"submit"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private int lyfwclient$fullBright(int light) {
      if (!this.lyfwclient$playerHead) {
         return light;
      } else {
         return ModuleManager.get("Player Head Glow") instanceof PlayerHeadGlowModule glow && glow.isEnabled() ? LightTexture.pack(15, 15) : light;
      }
   }
}
