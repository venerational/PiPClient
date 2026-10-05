package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.NametagPingModule;
import dev.lyfw.lyfwclient.module.modules.NametagsModule;
import dev.lyfw.lyfwclient.module.modules.PipPresenceModule;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public class PlayerDisplayNameMixin {
   @Inject(
      method = {"getDisplayName"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void lyfwclient$recolor(CallbackInfoReturnable<Component> cir) {
      Component result = (Component)cir.getReturnValue();
      if (result != null) {
         if (ModuleManager.get("Nametags") instanceof NametagsModule np && np.isEnabled()) {
            result = Component.literal(result.getString()).withStyle(s -> s.withColor(np.color()));
         }

         if (ModuleManager.get("Nametag Ping") instanceof NametagPingModule ping && ping.isEnabled()) {
            result = ping.decorate((Player)(Object)this, result);
         }

         if (ModuleManager.get("Pip Presence") instanceof PipPresenceModule presence && presence.isEnabled()) {
            result = presence.decorate((Player)(Object)this, result);
         }

         cir.setReturnValue(result);
      }
   }
}
