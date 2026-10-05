package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;

public class PlayerHeadGlowModule extends Module {
   private final BooleanSetting animated = this.register(new BooleanSetting("Animated", true));
   private final ColorSetting color = this.register(new ColorSetting("Color", -6202113));

   public PlayerHeadGlowModule() {
      super("Player Head Glow", "Highlights player head items in your inventory - matches \"Glow Player Heads\".", Category.RENDER, false);
   }

   public boolean isAnimated() {
      return this.animated.get();
   }

   public int tintColor() {
      return this.color.get();
   }
}
