package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.PlayerHeadGlowModule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiGraphics.class})
public class DrawContextGlowMixin {
   @Inject(
      method = {"renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$drawHeadGlow(LivingEntity entity, Level world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
      if (ModuleManager.get("Player Head Glow") instanceof PlayerHeadGlowModule glow && glow.isEnabled() && stack.is(Items.PLAYER_HEAD)) {
         GuiGraphics context = (GuiGraphics)(Object)this;
         int color = glow.tintColor();
         float pulse = glow.isAnimated() ? 0.75F + 0.25F * (float)Math.sin(System.currentTimeMillis() / 200.0) : 1.0F;
         context.renderOutline(x - 4, y - 4, 24, 24, withAlpha(color, Math.round(24.0F * pulse)));
         context.renderOutline(x - 3, y - 3, 22, 22, withAlpha(color, Math.round(46.0F * pulse)));
         context.renderOutline(x - 2, y - 2, 20, 20, withAlpha(color, Math.round(82.0F * pulse)));
         context.renderOutline(x - 1, y - 1, 18, 18, withAlpha(color, Math.round(132.0F * pulse)));
      }
   }

   private static int withAlpha(int color, int alpha) {
      return color & 16777215 | Math.max(0, Math.min(255, alpha)) << 24;
   }
}
