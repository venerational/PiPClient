package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;

public class SupermanFlyingModule extends Module {
   public SupermanFlyingModule() {
      super("Superman Flying", "Holds your arms out in front of you while you fly with an elytra, like Superman.", Category.RENDER, true);
   }

   public static SupermanFlyingModule get() {
      return ModuleManager.get("Superman Flying") instanceof SupermanFlyingModule module ? module : null;
   }
}
