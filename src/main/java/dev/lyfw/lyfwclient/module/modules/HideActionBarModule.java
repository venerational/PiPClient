package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class HideActionBarModule extends Module {
   public HideActionBarModule() {
      super("Hide Action Bar", "Hides the action bar overlay message (e.g. \"Cannot sleep now\").", Category.MISC, false);
   }

   @Override
   public void init() {
      HudElementRegistry.replaceElement(VanillaHudElements.OVERLAY_MESSAGE, original -> (context, tickCounter) -> {
         if (!this.isEnabled()) {
            original.render(context, tickCounter);
         }
      });
   }
}
