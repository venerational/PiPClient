package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.InventoryScaleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MouseHandler.class})
public class MouseCoordinateFixMixin {
   private static InventoryScaleModule lyfwclient$module() {
      return ModuleManager.get("Inventory Scale") instanceof InventoryScaleModule ism && ism.isActive() ? ism : null;
   }

   private static HandledScreenAccessor lyfwclient$screen() {
      return Minecraft.getInstance().screen instanceof HandledScreenAccessor accessor ? accessor : null;
   }

   @Inject(
      method = {"getScaledXPos(Lcom/mojang/blaze3d/platform/Window;D)D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void lyfwclient$fixScaleX(Window window, double x, CallbackInfoReturnable<Double> cir) {
      InventoryScaleModule module = lyfwclient$module();
      HandledScreenAccessor screen = lyfwclient$screen();
      if (module != null && screen != null) {
         float pivotX = screen.lyfwclient$getX() + screen.lyfwclient$getBackgroundWidth() / 2.0F;
         cir.setReturnValue((cir.getReturnValueD() - pivotX) / module.scale() + pivotX);
      }
   }

   @Inject(
      method = {"getScaledYPos(Lcom/mojang/blaze3d/platform/Window;D)D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void lyfwclient$fixScaleY(Window window, double y, CallbackInfoReturnable<Double> cir) {
      InventoryScaleModule module = lyfwclient$module();
      HandledScreenAccessor screen = lyfwclient$screen();
      if (module != null && screen != null) {
         float pivotY = screen.lyfwclient$getY() + screen.lyfwclient$getBackgroundHeight() / 2.0F;
         cir.setReturnValue((cir.getReturnValueD() - pivotY) / module.scale() + pivotY);
      }
   }
}
