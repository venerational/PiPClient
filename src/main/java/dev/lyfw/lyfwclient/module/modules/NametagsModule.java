package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;

public class NametagsModule extends Module {
   private final ColorSetting color = this.register(new ColorSetting("Color", -1));
   private final SliderSetting scale = this.register(new SliderSetting("Scale", 1.0, 0.25, 3.0, 0.05, "x"));

   public NametagsModule() {
      super("Nametags", "Recolors and resizes player nametags.", Category.RENDER, false);
   }

   public int color() {
      return this.color.get();
   }

   public float scale() {
      return (float)this.scale.get().doubleValue();
   }
}
