package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.NameProtectModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InventoryScreen.class})
public class NameProtectInventoryMixin {
   @Inject(
      method = {"renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void lyfwclient$blankPreview(
      GuiGraphics context, int x1, int y1, int x2, int y2, int size, float scale, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci
   ) {
      if (entity == Minecraft.getInstance().player
         && ModuleManager.get("Name Protect") instanceof NameProtectModule nameProtect
         && nameProtect.hidesInventoryPlayer()) {
         ci.cancel();
      }
   }
}
