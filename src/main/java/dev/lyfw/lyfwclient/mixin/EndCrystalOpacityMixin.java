package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.BlockOpacityModule;
import dev.lyfw.lyfwclient.module.modules.CrystalCustomizerModule;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EndCrystalRenderer.class})
public class EndCrystalOpacityMixin {
   @Unique
   private static final Identifier lyfwclient$texture = Identifier.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
   @Unique
   private static final RenderType lyfwclient$translucentLayer = RenderTypes.entityTranslucent(lyfwclient$texture);

   @WrapOperation(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
      )}
   )
   private void lyfwclient$submitEndCrystal(
      SubmitNodeCollector queue,
      Model model,
      Object state,
      PoseStack matrices,
      RenderType renderLayer,
      int light,
      int overlay,
      int outlineColor,
      CrumblingOverlay crumblingOverlay,
      Operation<Void> original
   ) {
      CrystalCustomizerModule crystals = CrystalCustomizerModule.get();
      if (crystals != null) {
         queue.submitModel(
            model, state, matrices, lyfwclient$translucentLayer, crystals.light(light), overlay, crystals.tint(), null, outlineColor, crumblingOverlay
         );
      } else if (ModuleManager.get("Block Opacity") instanceof BlockOpacityModule blockOpacity && blockOpacity.wantsEndCrystalTranslucent()) {
         queue.submitModel(
            model, state, matrices, lyfwclient$translucentLayer, light, overlay, blockOpacity.endCrystalTintedColor(), null, outlineColor, crumblingOverlay
         );
      } else {
         original.call(new Object[]{queue, model, state, matrices, renderLayer, light, overlay, outlineColor, crumblingOverlay});
      }
   }
}
