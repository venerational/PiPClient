package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class WatermarkModule extends HudModule {
   private final ColorSetting color = this.register(new ColorSetting("Color", -6202113));

   public WatermarkModule() {
      super("Watermark", "Displays a small client watermark.", true, 4.0, 4.0);
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      context.drawString(mc.font, Component.literal("Pip Client"), 0, 0, this.color.get());
   }

   @Override
   protected int contentWidth() {
      return Minecraft.getInstance().font.width("Pip Client");
   }

   @Override
   protected int contentHeight() {
      return 9;
   }
}
