package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.CustomFontModule;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiGraphics.class})
public class CustomFontMixin {
   @Inject(
      method = {"drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$fontString(Font renderer, String text, int x, int y, int color, boolean shadow, CallbackInfo ci) {
      CustomFontModule font = this.lyfwclient$font();
      if (font != null && text != null && font.draw((GuiGraphics)(Object)this, text, x, y, color, shadow, renderer.width(text))) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$fontText(Font renderer, Component text, int x, int y, int color, boolean shadow, CallbackInfo ci) {
      if (text != null) {
         this.lyfwclient$drawOrdered(renderer, text.getVisualOrderText(), x, y, color, shadow, ci);
      }
   }

   @Inject(
      method = {"drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$fontOrdered(Font renderer, FormattedCharSequence text, int x, int y, int color, boolean shadow, CallbackInfo ci) {
      if (text != null) {
         this.lyfwclient$drawOrdered(renderer, text, x, y, color, shadow, ci);
      }
   }

   @Unique
   private void lyfwclient$drawOrdered(Font renderer, FormattedCharSequence text, int x, int y, int color, boolean shadow, CallbackInfo ci) {
      CustomFontModule font = this.lyfwclient$font();
      if (font != null) {
         List<CustomFontModule.Run> runs = CustomFontModule.runsOf(text, color);
         if (font.draw((GuiGraphics)(Object)this, runs, x, y, shadow, renderer.width(text))) {
            ci.cancel();
         }
      }
   }

   @Unique
   private CustomFontModule lyfwclient$font() {
      if (CustomFontModule.bypass) {
         return null;
      } else if (ModuleManager.get("Custom Font") instanceof CustomFontModule font && font.isEnabled()) {
         boolean applies;
         if (HudModule.drawing) {
            applies = font.appliesToPipHud();
         } else if (Minecraft.getInstance().screen != null) {
            applies = font.appliesToScreens();
         } else {
            applies = font.appliesToHud();
         }

         return applies ? font : null;
      } else {
         return null;
      }
   }
}
