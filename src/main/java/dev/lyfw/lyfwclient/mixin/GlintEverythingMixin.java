package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.lyfw.lyfwclient.module.modules.ItemGlintModule;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ItemStack.class})
public class GlintEverythingMixin {
   @ModifyReturnValue(
      method = {"hasFoil"},
      at = {@At("RETURN")}
   )
   private boolean lyfwclient$glintEverything(boolean original) {
      return original || !((ItemStack)(Object)this).isEmpty() && ItemGlintModule.glintEverything();
   }
}
