package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.lyfw.lyfwclient.mixin.RenderLayerInvoker;
import dev.lyfw.lyfwclient.mixin.RenderLayerStateAccessor;
import dev.lyfw.lyfwclient.mixin.RenderSetupTextureAccessor;
import dev.lyfw.lyfwclient.mixin.RenderSetupTextureSpecAccessor;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.resources.Identifier;

public final class ArmorHurtRenderLayers {
   private static final Map<Identifier, RenderType> CUTOUT_CACHE = new HashMap<>();
   private static final Map<Identifier, RenderType> DECAL_CACHE = new HashMap<>();
   private static final Map<Identifier, RenderType> TRANSLUCENT_CACHE = new HashMap<>();

   private ArmorHurtRenderLayers() {
   }

   public static RenderType withHurtOverlay(RenderType layer) {
      Identifier texture = sampler0Texture(layer);
      if (texture == null) {
         return null;
      } else {
         RenderPipeline pipeline = layer.pipeline();
         if (pipeline == RenderPipelines.ARMOR_CUTOUT_NO_CULL) {
            return CUTOUT_CACHE.computeIfAbsent(texture, t -> build("lyfwclient_hurt_armor_cutout", RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET, t, false));
         } else if (pipeline == RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL) {
            return DECAL_CACHE.computeIfAbsent(texture, t -> build("lyfwclient_hurt_armor_decal", RenderPipelines.ENTITY_DECAL, t, false));
         } else {
            return pipeline == RenderPipelines.ARMOR_TRANSLUCENT
               ? TRANSLUCENT_CACHE.computeIfAbsent(texture, t -> build("lyfwclient_hurt_armor_translucent", RenderPipelines.ENTITY_TRANSLUCENT, t, true))
               : null;
         }
      }
   }

   private static RenderType build(String name, RenderPipeline pipeline, Identifier texture, boolean translucent) {
      RenderSetupBuilder builder = RenderSetup.builder(pipeline)
         .withTexture("Sampler0", texture)
         .useLightmap()
         .useOverlay()
         .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
         .affectsCrumbling()
         .setOutline(OutlineProperty.AFFECTS_OUTLINE);
      if (translucent) {
         builder = builder.sortOnUpload();
      }

      return RenderLayerInvoker.lyfwclient$of(name, builder.createRenderSetup());
   }

   private static Identifier sampler0Texture(RenderType layer) {
      RenderSetup setup = ((RenderLayerStateAccessor)(Object)layer).lyfwclient$getRenderSetup();
      Object spec = ((RenderSetupTextureAccessor)(Object)setup).lyfwclient$getTextures().get("Sampler0");
      return spec != null ? ((RenderSetupTextureSpecAccessor)spec).lyfwclient$getLocation() : null;
   }
}
