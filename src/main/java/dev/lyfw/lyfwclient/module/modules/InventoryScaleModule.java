package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class InventoryScaleModule extends Module {
   private final SliderSetting scale = this.register(new SliderSetting("Scale", 1.5, 1.0, 2.5, 0.05, "x"));

   public InventoryScaleModule() {
      super("Inventory Scale", "Scales container screens (inventory, chests, etc.) without affecting anything else on screen.", Category.MISC, false);
   }

   public float scale() {
      return (float)this.scale.get().doubleValue();
   }

   public boolean isActive() {
      return this.isEnabled() && Minecraft.getInstance().screen instanceof AbstractContainerScreen;
   }
}
