package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;

public class HideArmorModule extends Module {
   private final BooleanSetting onlySelf = this.register(new BooleanSetting("Only Hide Self", false));

   public HideArmorModule() {
      super("Hide Armor", "Hides worn armor on yourself and other players - stays visible on invisible players.", Category.RENDER, false);
   }

   public boolean onlySelf() {
      return this.onlySelf.get();
   }
}
