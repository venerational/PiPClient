package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.BlockOpacityModule;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemBlockRenderTypes.class})
public class BlockOpacityRenderLayerMixin {
   @Inject(
      method = {"getChunkRenderType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void lyfwclient$opacityLayer(BlockState state, CallbackInfoReturnable<ChunkSectionLayer> cir) {
      Block block = state.getBlock();
      if (ModuleManager.get("Block Opacity") instanceof BlockOpacityModule blockOpacity && blockOpacity.wantsTranslucentLayer(block)) {
         cir.setReturnValue(ChunkSectionLayer.TRANSLUCENT);
      }
   }
}
