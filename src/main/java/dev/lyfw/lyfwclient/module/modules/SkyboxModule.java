package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.render.PadImage;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class SkyboxModule extends Module {
   private final BooleanSetting customSky = this.register(new BooleanSetting("Custom Sky", false));
   private final ColorSetting skyColor = this.register(new ColorSetting("Sky Color", -12947496).exemptFromGlobalColor());
   private final BooleanSetting customClouds = this.register(new BooleanSetting("Custom Clouds", false));
   private final ColorSetting cloudColor = this.register(new ColorSetting("Cloud Color", -1).exemptFromGlobalColor());
   private final BooleanSetting customStars = this.register(new BooleanSetting("Custom Stars", false));
   private final SliderSetting starBrightness = this.register(new SliderSetting("Star Brightness", 100.0, 0.0, 100.0, 5.0, "%"));
   private final BooleanSetting customImage = this.register(new BooleanSetting("Sky Image", false));
   private final TextSetting imageFile = this.register(new TextSetting("Image", ""));
   private final SliderSetting imageOpacity = this.register(new SliderSetting("Image Opacity", 100.0, 0.0, 100.0, 5.0, "%"));
   private static final int IMAGE_EDGE = 1024;
   private String loadedImageFor;
   private PadImage loadedImage;
   private int imageGeneration;

   public SkyboxModule() {
      super(
         "Skybox",
         "Your own sky, cloud and star settings instead of the ones the dimension picks - or a picture or GIF of your own round the sky. Fog lives in Custom Fog.",
         Category.RENDER,
         false
      );
      this.customSky.group = "Sky";
      this.skyColor.group = "Sky";
      this.customClouds.group = "Clouds";
      this.cloudColor.group = "Clouds";
      this.customStars.group = "Stars";
      this.starBrightness.group = "Stars";
      this.customImage.group = "Image";
      this.imageFile.group = "Image";
      this.imageOpacity.group = "Image";
   }

   public static SkyboxModule get() {
      return ModuleManager.get("Skybox") instanceof SkyboxModule skybox ? skybox : null;
   }

   public int sky() {
      return this.isEnabled() && this.customSky.get() ? 0xFF000000 | this.skyColor.get() & 16777215 : -1;
   }

   public boolean skyOverridden() {
      return this.isEnabled() && this.customSky.get();
   }

   public boolean cloudsOverridden() {
      return this.isEnabled() && this.customClouds.get();
   }

   public int clouds() {
      return this.cloudColor.get();
   }

   public boolean starsOverridden() {
      return this.isEnabled() && this.customStars.get();
   }

   public float stars() {
      return (float)(this.starBrightness.get() / 100.0);
   }

   public float imageOpacity() {
      return (float)(this.imageOpacity.get() / 100.0);
   }

   public Identifier imageFrame() {
      if (this.isEnabled() && this.customImage.get()) {
         PadImage image = this.image();
         return image == null ? null : image.currentFrame();
      } else {
         return null;
      }
   }

   private PadImage image() {
      String name = this.imageFile.get() == null ? "" : this.imageFile.get().trim();
      if (PadImage.isUrl(name)) {
         if (!name.equals(this.loadedImageFor)) {
            this.loadedImageFor = name;
            this.loadedImage = null;
            this.imageGeneration++;
         }

         if (this.loadedImage == null) {
            this.loadedImage = PadImage.fromUrl(name, "sky_image_" + this.imageGeneration, 1024);
         }

         return this.loadedImage;
      } else {
         if (!name.equals(this.loadedImageFor)) {
            this.loadedImageFor = name;
            this.loadedImage = null;
            if (!name.isEmpty()) {
               try {
                  Path path = FabricLoader.getInstance().getConfigDir().resolve(name);
                  this.loadedImage = PadImage.load(path, "sky_image_" + this.imageGeneration++, 1024);
               } catch (Exception var3) {
                  System.out.println("[Pip Client] \"" + name + "\" is not a usable file name (" + var3 + ")");
               }
            }
         }

         return this.loadedImage;
      }
   }
}
