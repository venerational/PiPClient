package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.AttackCooldownDesyncModule;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LocalPlayer.class})
public class CooldownDesyncMixin {
   @Inject(
      method = {"swing"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$resetOnSwing(InteractionHand hand, CallbackInfo ci) {
      if (ModuleManager.get("Attack Cooldown Desync") instanceof AttackCooldownDesyncModule desync && desync.isEnabled()) {
         ((Player)(Object)this).resetAttackStrengthTicker();
      }
   }
}
