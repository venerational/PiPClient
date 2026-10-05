package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;

public class NoHandSwayModule extends Module {
   private final BooleanSetting removeSway = this.register(new BooleanSetting("Remove Item Sway", true));
   private final BooleanSetting removeBob = this.register(new BooleanSetting("Remove Hand Bobbing", false));
   private final BooleanSetting removeSwing = this.register(new BooleanSetting("Remove Swing", true));
   private final BooleanSetting removeEquip = this.register(new BooleanSetting("Remove Equip Animation", true));

   public NoHandSwayModule() {
      super(
         "No Hand Sway",
         "Holds the held item still - the tilt when you turn, the bob when you walk, the swing when you click and the dip when you switch.",
         Category.RENDER,
         false
      );
      this.removeSway.group = "Hand";
      this.removeBob.group = "Hand";
      this.removeSwing.group = "Hand";
      this.removeEquip.group = "Hand";
   }

   public static NoHandSwayModule get() {
      return ModuleManager.get("No Hand Sway") instanceof NoHandSwayModule sway ? sway : null;
   }

   public boolean swayRemoved() {
      return this.isEnabled() && this.removeSway.get();
   }

   public boolean bobRemoved() {
      return this.isEnabled() && this.removeBob.get();
   }

   public boolean swingRemoved() {
      return this.isEnabled() && this.removeSwing.get();
   }

   public boolean equipRemoved() {
      return this.isEnabled() && this.removeEquip.get();
   }
}
