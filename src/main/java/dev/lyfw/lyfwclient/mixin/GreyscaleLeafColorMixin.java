package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.GreyscaleModule;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockColors.class})
public class GreyscaleLeafColorMixin {
   private static final int SPRUCE_LEAVES_COLOR = -10380959;
   private static final int BIRCH_LEAVES_COLOR = -8345771;

   @Inject(
      method = {"createDefault"},
      at = {@At("RETURN")}
   )
   private static void lyfwclient$overrideFixedLeafColors(CallbackInfoReturnable<BlockColors> cir) {
      BlockColors blockColors = (BlockColors)cir.getReturnValue();
      blockColors.register((state, world, pos, tintIndex) -> GreyscaleModule.applyPalette(-10380959), new Block[]{Blocks.SPRUCE_LEAVES});
      blockColors.register((state, world, pos, tintIndex) -> GreyscaleModule.applyPalette(-8345771), new Block[]{Blocks.BIRCH_LEAVES});
   }
}
