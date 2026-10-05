package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class CleanViewModule extends Module {
   private static final Identifier[] HIDDEN_ELEMENTS = new Identifier[]{
      VanillaHudElements.HOTBAR,
      VanillaHudElements.EXPERIENCE_LEVEL,
      VanillaHudElements.AIR_BAR,
      VanillaHudElements.STATUS_EFFECTS,
      VanillaHudElements.BOSS_BAR,
      VanillaHudElements.SCOREBOARD,
      VanillaHudElements.MISC_OVERLAYS,
      VanillaHudElements.INFO_BAR,
      VanillaHudElements.MOUNT_HEALTH
   };
   private static final Identifier[] REPOSITIONED_ELEMENTS = new Identifier[]{
      VanillaHudElements.HEALTH_BAR, VanillaHudElements.FOOD_BAR, VanillaHudElements.ARMOR_BAR
   };
   private final SliderSetting offset = this.register(new SliderSetting("Bottom Offset", 90.0, 0.0, 400.0, 1.0, "px"));

   public CleanViewModule() {
      super(
         "Clean View", "Hides HUD clutter (hotbar, XP, status effects, etc.) and drops health/food/armor down to where the hotbar was.", Category.RENDER, false
      );
   }

   @Override
   public void init() {
      for (Identifier id : HIDDEN_ELEMENTS) {
         HudElementRegistry.replaceElement(id, original -> (context, tickCounter) -> {
            if (!this.isEnabled()) {
               original.render(context, tickCounter);
            }
         });
      }

      for (Identifier id : REPOSITIONED_ELEMENTS) {
         HudElementRegistry.replaceElement(id, original -> (context, tickCounter) -> {
            if (this.isEnabled()) {
               context.pose().pushMatrix();
               context.pose().translate(0.0F, (float)this.offset.get().doubleValue());
               original.render(context, tickCounter);
               context.pose().popMatrix();
            } else {
               original.render(context, tickCounter);
            }
         });
      }
   }
}
