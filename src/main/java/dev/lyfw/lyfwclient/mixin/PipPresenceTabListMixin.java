package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.PipPresenceModule;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerTabOverlay.class})
public class PipPresenceTabListMixin {
   @Inject(
      method = {"getNameForDisplay"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void lyfwclient$star(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
      if (entry != null && entry.getProfile() != null && ModuleManager.get("Pip Presence") instanceof PipPresenceModule presence && presence.isEnabled()) {
         Component starred = presence.tabName(entry.getProfile().id(), (Component)cir.getReturnValue());
         if (starred != null) {
            cir.setReturnValue(starred);
         }
      }
   }
}
