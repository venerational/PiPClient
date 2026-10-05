package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.InventoryScaleModule;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({GuiRenderer.class})
public abstract class ItemAtlasResolutionMixin {
   private static float lyfwclient$lastMultiplier = 1.0F;

   @Invoker("invalidateItemAtlas")
   public abstract void lyfwclient$invokeOnItemAtlasChanged();

   @ModifyVariable(
      method = {"prepareItemElements"},
      at = @At("STORE"),
      ordinal = 1
   )
   private int lyfwclient$scaleItemAtlasResolution(int pixelsPerItem) {
      float multiplier = ModuleManager.get("Inventory Scale") instanceof InventoryScaleModule ism && ism.isActive() ? ism.scale() : 1.0F;
      if (multiplier != lyfwclient$lastMultiplier) {
         lyfwclient$lastMultiplier = multiplier;
         this.lyfwclient$invokeOnItemAtlasChanged();
      }

      return Math.round(pixelsPerItem * multiplier);
   }
}
