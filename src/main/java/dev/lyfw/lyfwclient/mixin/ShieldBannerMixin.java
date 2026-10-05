package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.ShieldBannerModule;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ShieldSpecialRenderer.class})
public class ShieldBannerMixin {
   @Inject(
      method = {"extractArgument(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/component/DataComponentMap;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void lyfwclient$overrideShieldBanner(ItemStack stack, CallbackInfoReturnable<DataComponentMap> cir) {
      DataComponentMap replacement = ShieldBannerModule.override((DataComponentMap)cir.getReturnValue());
      if (replacement != null) {
         cir.setReturnValue(replacement);
      }
   }
}
