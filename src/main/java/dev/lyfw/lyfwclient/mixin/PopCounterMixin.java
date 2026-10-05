package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.modules.TotemPopTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPacketListener.class})
public class PopCounterMixin {
   @Inject(
      method = {"handleEntityEvent"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$trackTotemPop(ClientboundEntityEventPacket packet, CallbackInfo ci) {
      if (packet.getEventId() == 35) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && packet.getEntity(mc.level) instanceof Player player) {
            TotemPopTracker.recordPop(player);
         }
      }
   }
}
