package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.OptimizersModule;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"hero/bane/herosanchoroptimizer/HerosAnchorOptimizer"},
   remap = false
)
public class OptimizersAnchorMixin {
   @Inject(
      method = {"lambda$onInitializeClient$0"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0,
      remap = false
   )
   private static void pip$optimizersAnchor(Player player, Level world, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
      if (!OptimizersModule.allowsAnchor()) {
         cir.setReturnValue(InteractionResult.PASS);
      }
   }
}
