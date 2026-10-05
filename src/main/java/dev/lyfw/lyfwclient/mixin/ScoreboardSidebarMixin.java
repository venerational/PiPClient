package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.ScoreboardModule;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public class ScoreboardSidebarMixin {
   @Inject(
      method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$handOverSidebar(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      ScoreboardModule scoreboard = ScoreboardModule.get();
      if (scoreboard != null && scoreboard.takesOver()) {
         ci.cancel();
      }
   }
}
