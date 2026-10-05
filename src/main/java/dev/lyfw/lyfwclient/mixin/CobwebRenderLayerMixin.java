package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.CobwebModule;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemBlockRenderTypes.class})
public class CobwebRenderLayerMixin {
   @Inject(
      method = {"getChunkRenderType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void lyfwclient$cobwebLayer(BlockState state, CallbackInfoReturnable<ChunkSectionLayer> cir) {
      if (state.getBlock() == Blocks.COBWEB && ModuleManager.get("Cobweb") instanceof CobwebModule cobweb && cobweb.wantsTranslucentLayer()) {
         cir.setReturnValue(ChunkSectionLayer.TRANSLUCENT);
      }
   }
}
