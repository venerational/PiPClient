package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.InventoryScaleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({InventoryScreen.class})
public abstract class InventoryScreenEntityPreviewMixin {
   private static InventoryScaleModule lyfwclient$module() {
      return ModuleManager.get("Inventory Scale") instanceof InventoryScaleModule ism && ism.isActive() ? ism : null;
   }

   private static HandledScreenAccessor lyfwclient$screen() {
      return Minecraft.getInstance().screen instanceof HandledScreenAccessor accessor ? accessor : null;
   }

   private static double lyfwclient$fixX(int value) {
      InventoryScaleModule module = lyfwclient$module();
      HandledScreenAccessor screen = lyfwclient$screen();
      if (module != null && screen != null) {
         float pivotX = screen.lyfwclient$getX() + screen.lyfwclient$getBackgroundWidth() / 2.0F;
         return pivotX + (value - pivotX) * module.scale();
      } else {
         return value;
      }
   }

   private static double lyfwclient$fixY(int value) {
      InventoryScaleModule module = lyfwclient$module();
      HandledScreenAccessor screen = lyfwclient$screen();
      if (module != null && screen != null) {
         float pivotY = screen.lyfwclient$getY() + screen.lyfwclient$getBackgroundHeight() / 2.0F;
         return pivotY + (value - pivotY) * module.scale();
      } else {
         return value;
      }
   }

   @ModifyVariable(
      method = {"renderEntityInInventoryFollowsMouse"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private static int lyfwclient$fixX1(int x1) {
      return (int)Math.round(lyfwclient$fixX(x1));
   }

   @ModifyVariable(
      method = {"renderEntityInInventoryFollowsMouse"},
      at = @At("HEAD"),
      ordinal = 1,
      argsOnly = true
   )
   private static int lyfwclient$fixY1(int y1) {
      return (int)Math.round(lyfwclient$fixY(y1));
   }

   @ModifyVariable(
      method = {"renderEntityInInventoryFollowsMouse"},
      at = @At("HEAD"),
      ordinal = 2,
      argsOnly = true
   )
   private static int lyfwclient$fixX2(int x2) {
      return (int)Math.round(lyfwclient$fixX(x2));
   }

   @ModifyVariable(
      method = {"renderEntityInInventoryFollowsMouse"},
      at = @At("HEAD"),
      ordinal = 3,
      argsOnly = true
   )
   private static int lyfwclient$fixY2(int y2) {
      return (int)Math.round(lyfwclient$fixY(y2));
   }

   @ModifyVariable(
      method = {"renderEntityInInventoryFollowsMouse"},
      at = @At("HEAD"),
      ordinal = 4,
      argsOnly = true
   )
   private static int lyfwclient$fixSize(int size) {
      InventoryScaleModule module = lyfwclient$module();
      return module == null ? size : Math.round(size * module.scale());
   }
}
