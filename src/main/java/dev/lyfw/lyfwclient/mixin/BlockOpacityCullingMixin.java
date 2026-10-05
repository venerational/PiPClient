package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.BlockOpacityModule;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public class BlockOpacityCullingMixin {
   @Inject(
      method = {"shouldRenderFace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void lyfwclient$dontCullAgainstOpacityTarget(BlockState state, BlockState otherState, Direction side, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleManager.get("Block Opacity") instanceof BlockOpacityModule blockOpacity) {
         boolean stateIsTarget = blockOpacity.wantsCullFix(state.getBlock());
         boolean otherIsTarget = blockOpacity.wantsCullFix(otherState.getBlock());
         if (!stateIsTarget && otherIsTarget) {
            cir.setReturnValue(true);
         }
      }
   }
}
