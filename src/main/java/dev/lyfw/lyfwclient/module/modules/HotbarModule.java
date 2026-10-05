package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class HotbarModule extends Module {
   private static final Identifier[] ELEMENTS = new Identifier[]{
      VanillaHudElements.HOTBAR,
      VanillaHudElements.HEALTH_BAR,
      VanillaHudElements.ARMOR_BAR,
      VanillaHudElements.FOOD_BAR,
      VanillaHudElements.EXPERIENCE_LEVEL,
      VanillaHudElements.INFO_BAR,
      VanillaHudElements.HELD_ITEM_TOOLTIP,
      VanillaHudElements.AIR_BAR
   };
   private final SliderSetting xOffset = this.register(new SliderSetting("X Offset", 0.0, -400.0, 400.0, 1.0, "px"));
   private final SliderSetting yOffset = this.register(new SliderSetting("Y Offset", 0.0, -400.0, 400.0, 1.0, "px"));
   private final SliderSetting scale = this.register(new SliderSetting("Scale", 1.0, 0.5, 3.0, 0.1, "x"));

   public HotbarModule() {
      super("Hotbar", "Moves and rescales the real vanilla hotbar, health, armor, food, and XP bars together.", Category.HUD, false);
   }

   @Override
   public void init() {
      for (Identifier id : ELEMENTS) {
         HudElementRegistry.replaceElement(id, original -> (context, tickCounter) -> {
            if (!this.isEnabled()) {
               original.render(context, tickCounter);
            } else {
               Minecraft mc = Minecraft.getInstance();
               float pivotX = mc.getWindow().getGuiScaledWidth() / 2.0F;
               float pivotY = mc.getWindow().getGuiScaledHeight();
               context.pose().pushMatrix();
               context.pose().translate(pivotX + (float)this.xOffset.get().doubleValue(), pivotY + (float)this.yOffset.get().doubleValue());
               context.pose().scale((float)this.scale.get().doubleValue());
               context.pose().translate(-pivotX, -pivotY);
               original.render(context, tickCounter);
               context.pose().popMatrix();
            }
         });
      }
   }
}
