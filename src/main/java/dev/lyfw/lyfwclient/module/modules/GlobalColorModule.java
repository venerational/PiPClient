package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.awt.Color;

public class GlobalColorModule extends Module {
   private final ColorSetting color = this.register(new ColorSetting("Color", -49766));
   private final BooleanSetting rainbow = this.register(new BooleanSetting("Rainbow", false));
   private final SliderSetting rainbowSpeed = this.register(new SliderSetting("Rainbow Speed", 1.0, 0.1, 5.0, 0.1, "x"));
   private final BooleanSetting keepAlpha = this.register(new BooleanSetting("Keep Transparency", true));
   private boolean applied;

   public GlobalColorModule() {
      super("Global Color", "Sets one color across every module at once, without touching what each of them had set.", Category.RENDER, false);
      this.color.group = "Color";
      this.rainbow.group = "Color";
      this.rainbowSpeed.group = "Color";
      this.keepAlpha.group = "Color";
   }

   @Override
   public void tick() {
      if (this.isEnabled()) {
         int wanted = this.wanted();
         boolean alpha = this.keepAlpha.get();
         ColorSetting.override = stored -> alpha ? stored & 0xFF000000 | wanted & 16777215 : wanted;
         this.applied = true;
      } else if (this.applied) {
         ColorSetting.override = null;
         this.applied = false;
      }
   }

   private int wanted() {
      if (!this.rainbow.get()) {
         return this.color.raw();
      } else {
         float hue = (float)(System.currentTimeMillis() / 1000.0 * this.rainbowSpeed.get() % 1.0);
         return 0xFF000000 | Color.HSBtoRGB(hue, 0.8F, 1.0F) & 16777215;
      }
   }
}
