package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;

public class AttackCooldownDesyncModule extends Module {
   public AttackCooldownDesyncModule() {
      super(
         "Attack Cooldown Desync",
         "Resets your attack cooldown on every swing, not just real hits - matches cookeymod's \"Fix Cooldown Desync\".",
         Category.MISC,
         true
      );
   }
}
