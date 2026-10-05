package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.BlockOpacityModule;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockStateBase.class})
public class BlockOpacityShapeMixin {
   @Inject(
      method = {"getFaceOcclusionShape"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$emptyCullingFace(Direction direction, CallbackInfoReturnable<VoxelShape> cir) {
      BlockStateBase self = (BlockStateBase)(Object)this;
      Block block = self.getBlock();
      if (ModuleManager.get("Block Opacity") instanceof BlockOpacityModule blockOpacity && blockOpacity.wantsCullFix(block)) {
         cir.setReturnValue(Shapes.empty());
      }
   }
}
