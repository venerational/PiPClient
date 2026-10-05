package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.ReachDisplayModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MultiPlayerGameMode.class})
public class ReachDisplayMixin {
   @Inject(
      method = {"attack"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$recordReach(Player player, Entity target, CallbackInfo ci) {
      if (ModuleManager.get("Reach Display") instanceof ReachDisplayModule reach && reach.isEnabled() && (!reach.playersOnly() || target instanceof Player)) {
         Minecraft mc = Minecraft.getInstance();
         HitResult hit = mc.hitResult;
         double distance;
         if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() == target) {
            distance = player.getEyePosition().distanceTo(hit.getLocation());
         } else {
            distance = player.distanceTo(target);
         }

         reach.recordHit((float)distance);
      }
   }
}
