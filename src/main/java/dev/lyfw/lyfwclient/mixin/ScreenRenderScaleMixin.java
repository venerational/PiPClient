package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.InventoryScaleModule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class})
public class ScreenRenderScaleMixin {
   private boolean lyfwclient$pushed;

   private static InventoryScaleModule lyfwclient$module(Screen screen) {
      if (!(screen instanceof AbstractContainerScreen)) {
         return null;
      } else {
         return ModuleManager.get("Inventory Scale") instanceof InventoryScaleModule ism && ism.isEnabled() ? ism : null;
      }
   }

   @Inject(
      method = {"renderWithTooltipAndSubtitles"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$pushScale(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
      Screen self = (Screen)(Object)this;
      InventoryScaleModule module = lyfwclient$module(self);
      this.lyfwclient$pushed = module != null;
      if (this.lyfwclient$pushed) {
         HandledScreenAccessor accessor = (HandledScreenAccessor)self;
         float pivotX = accessor.lyfwclient$getX() + accessor.lyfwclient$getBackgroundWidth() / 2.0F;
         float pivotY = accessor.lyfwclient$getY() + accessor.lyfwclient$getBackgroundHeight() / 2.0F;
         context.pose().pushMatrix();
         context.pose().translate(pivotX, pivotY);
         context.pose().scale(module.scale());
         context.pose().translate(-pivotX, -pivotY);
      }
   }

   @Inject(
      method = {"renderWithTooltipAndSubtitles"},
      at = {@At("RETURN")}
   )
   private void lyfwclient$popScale(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
      if (this.lyfwclient$pushed) {
         context.pose().popMatrix();
         this.lyfwclient$pushed = false;
      }
   }
}
