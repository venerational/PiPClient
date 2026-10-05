package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SprintStatusModule extends HudModule {
   public SprintStatusModule() {
      super("Sprint Status", "Shows whether you're sprinting, matching \"Sprint Display Plus\".", false, 4.0, 145.0);
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      context.drawString(mc.font, statusText(mc), 0, 0, -1);
   }

   @Override
   protected int contentWidth() {
      Minecraft mc = Minecraft.getInstance();
      return mc.font.width(statusText(mc));
   }

   @Override
   protected int contentHeight() {
      return 9;
   }

   private static Component statusText(Minecraft mc) {
      if (mc.player == null) {
         return Component.literal("§7§oNot Sprinting");
      } else if (mc.player.isSprinting()) {
         return Component.literal("§7§oSprint Toggled");
      } else {
         return mc.options.keySprint.isDown()
            ? Component.literal(mc.options.toggleSprint().get() ? "§7§oSprint Toggled" : "§7§oSprint Held")
            : Component.literal("§7§oNot Sprinting");
      }
   }
}
