package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ItemGlowMarker;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemModelResolver.class})
public class ItemModelManagerGlowMixin {
   @Inject(
      method = {"updateForLiving"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$markLiving(ItemStackRenderState state, ItemStack stack, ItemDisplayContext displayContext, LivingEntity entity, CallbackInfo ci) {
      mark(state, stack);
   }

   @Inject(
      method = {"updateForNonLiving"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$markNonLiving(ItemStackRenderState state, ItemStack stack, ItemDisplayContext displayContext, Entity entity, CallbackInfo ci) {
      mark(state, stack);
   }

   @Inject(
      method = {"updateForTopItem"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$markCleared(
      ItemStackRenderState state, ItemStack stack, ItemDisplayContext displayContext, Level world, ItemOwner heldItemContext, int seed, CallbackInfo ci
   ) {
      mark(state, stack);
   }

   @Inject(
      method = {"appendItemLayers"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$markGeneral(
      ItemStackRenderState state, ItemStack stack, ItemDisplayContext displayContext, Level world, ItemOwner heldItemContext, int seed, CallbackInfo ci
   ) {
      mark(state, stack);
   }

   private static void mark(ItemStackRenderState state, ItemStack stack) {
      ((ItemGlowMarker)state).lyfwclient$setPlayerHead(stack.is(Items.PLAYER_HEAD));
   }
}
