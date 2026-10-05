package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.EnchantGlintTintModule;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class ItemGlintModule extends EnchantGlintTintModule {
   private final ColorSetting armorColor = this.register(new ColorSetting("Color", -7722014));
   private final SliderSetting armorStrength = this.register(new SliderSetting("Armor Strength", 100.0, 0.0, 300.0, 5.0, "%"));
   private final BooleanSetting glintEverything = this.register(new BooleanSetting("Glint Everything", false));
   private final BooleanSetting recolorItems = this.register(new BooleanSetting("Recolor Items", false));
   private final ColorSetting itemColor = this.register(new ColorSetting("Item Color", -7722014));
   private final SliderSetting itemStrength = this.register(new SliderSetting("Item Strength", 100.0, 0.0, 300.0, 5.0, "%"));

   public ItemGlintModule() {
      super("Item Glint", "The enchantment glint's colour and strength on armour and items - and a switch to put it on everything.");
      this.armorColor.group = "Armor";
      this.armorStrength.group = "Armor";
      this.glintEverything.group = "Items";
      this.recolorItems.group = "Items";
      this.itemColor.group = "Items";
      this.itemStrength.group = "Items";
      this.channel(
         ItemRenderer.ENCHANTED_GLINT_ARMOR,
         () -> this.isEnabled() ? this.armorColor.get() : 0,
         () -> this.isEnabled() ? this.armorStrength.get() / 100.0 : 1.0
      );
      this.channel(
         ItemRenderer.ENCHANTED_GLINT_ITEM,
         () -> this.isEnabled() && this.recolorItems.get() ? this.itemColor.get() : 0,
         () -> this.isEnabled() ? this.itemStrength.get() / 100.0 : 1.0
      );
   }

   public static boolean glintEverything() {
      return ModuleManager.get("Item Glint") instanceof ItemGlintModule glint && glint.isEnabled() && glint.glintEverything.get();
   }
}
