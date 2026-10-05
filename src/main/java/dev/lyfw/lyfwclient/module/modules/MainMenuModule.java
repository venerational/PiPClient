package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.ThemeRenderer;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.render.PadImage;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.awt.Color;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;

public class MainMenuModule extends Module {
   private final TextSetting backgroundImage = this.register(new TextSetting("Background Image", ""));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", -16052970));
   private final SliderSetting dim = this.register(new SliderSetting("Dim", 25.0, 0.0, 90.0, 1.0, "%"));
   private final TextSetting title = this.register(new TextSetting("Title", "PIP"));
   private final SliderSetting titleScale = this.register(new SliderSetting("Title Scale", 6.0, 1.0, 14.0, 0.5, "x"));
   private final SliderSetting titleY = this.register(new SliderSetting("Title Y", 56.0, 0.0, 260.0, 2.0, "px"));
   private final SliderSetting rainbowSpeed = this.register(new SliderSetting("Rainbow Speed", 1.0, 0.1, 5.0, 0.1, "x"));
   private final SliderSetting rainbowSpread = this.register(new SliderSetting("Rainbow Spread", 8.0, 0.0, 40.0, 1.0, "%"));
   private final BooleanSetting shadow = this.register(new BooleanSetting("Title Shadow", true));
   private final BooleanSetting hideSplash = this.register(new BooleanSetting("Hide Splash Text", true));
   private final BooleanSetting styleButtons = this.register(new BooleanSetting("Style Buttons", true));
   private final ColorSetting buttonColor = this.register(new ColorSetting("Button Color", 1494225954));
   private final ColorSetting buttonBorder = this.register(new ColorSetting("Button Border", 1090519039));
   private final ColorSetting buttonHover = this.register(new ColorSetting("Button Hover", -1976814256));
   private String loadedImageFor;
   private PadImage loadedImage;
   private int imageGeneration;

   public MainMenuModule() {
      super("Main Menu", "Your own background and title on the main menu. Takes any PNG or GIF, animated ones included.", Category.MISC, false);
      this.backgroundImage.group = "Background";
      this.backgroundColor.group = "Background";
      this.dim.group = "Background";
      this.title.group = "Title";
      this.titleScale.group = "Title";
      this.titleY.group = "Title";
      this.rainbowSpeed.group = "Title";
      this.rainbowSpread.group = "Title";
      this.shadow.group = "Title";
      this.hideSplash.group = "Title";
      this.styleButtons.group = "Buttons";
      this.buttonColor.group = "Buttons";
      this.buttonBorder.group = "Buttons";
      this.buttonHover.group = "Buttons";
   }

   public boolean hideSplash() {
      return this.hideSplash.get();
   }

   public boolean styleButtons() {
      return this.styleButtons.get();
   }

   public void drawButton(GuiGraphics context, AbstractWidget button) {
      int x = button.getX();
      int y = button.getY();
      int w = button.getWidth();
      int h = button.getHeight();
      boolean hovered = button.isHovered() && button.active;
      ThemeRenderer.fillRounded(context, x, y, w, h, hovered ? this.buttonHover.get() : this.buttonColor.get(), 3);
      ThemeRenderer.fillRoundedBorder(context, x, y, w, h, this.buttonBorder.get(), 0, 3);
   }

   public void drawBackground(GuiGraphics context, int width, int height) {
      PadImage image = this.image();
      if (image != null) {
         context.blit(RenderPipelines.GUI_TEXTURED, image.currentFrame(), 0, 0, 0.0F, 0.0F, width, height, width, height);
      } else {
         context.fill(0, 0, width, height, this.backgroundColor.get());
      }

      int shade = (int)Math.round(this.dim.get() / 100.0 * 255.0);
      if (shade > 0) {
         context.fill(0, 0, width, height, shade << 24);
      }
   }

   public void drawTitle(GuiGraphics context, int width) {
      String text = this.title.get() == null ? "" : this.title.get();
      if (!text.isEmpty()) {
         Minecraft mc = Minecraft.getInstance();
         float scale = (float)this.titleScale.get().doubleValue();
         float spread = (float)(this.rainbowSpread.get() / 100.0);
         float phase = (float)(System.currentTimeMillis() / 1000.0 * this.rainbowSpeed.get() % 1.0);
         int textWidth = mc.font.width(text);
         context.pose().pushMatrix();
         context.pose().translate((width - textWidth * scale) / 2.0F, (float)this.titleY.get().doubleValue());
         context.pose().scale(scale, scale);
         int x = 0;

         for (int i = 0; i < text.length(); i++) {
            String glyph = String.valueOf(text.charAt(i));
            int color = 0xFF000000 | Color.HSBtoRGB(phase + i * spread, 0.72F, 1.0F) & 16777215;
            if (this.shadow.get()) {
               context.drawString(mc.font, glyph, x, 0, color, true);
            } else {
               context.drawString(mc.font, glyph, x, 0, color, false);
            }

            x += mc.font.width(glyph);
         }

         context.pose().popMatrix();
      }
   }

   private PadImage image() {
      String name = this.backgroundImage.get() == null ? "" : this.backgroundImage.get().trim();
      if (PadImage.isUrl(name)) {
         if (!name.equals(this.loadedImageFor)) {
            this.loadedImageFor = name;
            this.loadedImage = null;
            this.imageGeneration++;
         }

         if (this.loadedImage == null) {
            this.loadedImage = PadImage.fromUrl(name, "main_menu_" + this.imageGeneration);
         }

         return this.loadedImage;
      } else if (name.equals(this.loadedImageFor)) {
         return this.loadedImage;
      } else {
         this.loadedImageFor = name;
         this.loadedImage = null;
         if (!name.isEmpty()) {
            try {
               Path path = FabricLoader.getInstance().getConfigDir().resolve(name);
               this.loadedImage = PadImage.load(path, "main_menu_" + this.imageGeneration++);
            } catch (Exception var3) {
               System.out.println("[Pip Client] \"" + name + "\" is not a usable file name (" + var3 + ")");
            }
         }

         return this.loadedImage;
      }
   }
}
