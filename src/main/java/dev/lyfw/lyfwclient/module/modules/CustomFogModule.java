package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.util.Mth;
import org.joml.Vector4f;

public class CustomFogModule extends Module {
   private final ColorSetting fogColor = this.register(new ColorSetting("Fog Color", -1591570));
   private final SliderSetting strength = this.register(new SliderSetting("Strength", 100.0, 0.0, 100.0, 1.0, "%"));
   private final SliderSetting thickness = this.register(new SliderSetting("Thickness", 50.0, 0.0, 100.0, 1.0, "%"));
   private final BooleanSetting keepBrightness = this.register(new BooleanSetting("Keep Brightness", true));
   private final BooleanSetting includeFluids = this.register(new BooleanSetting("Include Water & Lava", true));
   private final BooleanSetting tintSky = this.register(new BooleanSetting("Tint Sky", true));

   public CustomFogModule() {
      super(
         "Custom Fog",
         "Recolors the fog and pulls it in close, so you stand inside it instead of seeing a rim on the horizon. Works on any server - it recolors the fog on your own client.",
         Category.RENDER,
         false
      );
      this.fogColor.group = "Color";
      this.strength.group = "Color";
      this.keepBrightness.group = "Color";
      this.includeFluids.group = "Color";
      this.thickness.group = "Distance";
      this.tintSky.group = "Distance";
   }

   private static CustomFogModule active() {
      return ModuleManager.get("Custom Fog") instanceof CustomFogModule fog && fog.isEnabled() ? fog : null;
   }

   public boolean tintSky() {
      return this.tintSky.get();
   }

   public static boolean changesDistance() {
      CustomFogModule fog = active();
      return fog != null && fog.thickness.get() > 1.0;
   }

   public static boolean skyTinted() {
      CustomFogModule fog = active();
      return fog != null && fog.tintSky();
   }

   public static float fogEnd(float vanillaEnd) {
      CustomFogModule fog = active();
      if (fog == null) {
         return vanillaEnd;
      } else {
         float t = (float)(fog.thickness.get() / 100.0);
         return vanillaEnd * Mth.lerp(t, 1.0F, 0.2F);
      }
   }

   public static float fogStart(float end) {
      CustomFogModule fog = active();
      if (fog == null) {
         return end * 0.9F;
      } else {
         float t = (float)(fog.thickness.get() / 100.0);
         return end * Mth.lerp(t, 0.9F, 0.1F);
      }
   }

   public static Vector4f tint(Vector4f original, boolean inFluid) {
      CustomFogModule fog = active();
      if (original != null && fog != null) {
         float amount = (float)(fog.strength.get() / 100.0);
         if (amount <= 0.0F) {
            return null;
         } else if (inFluid && !fog.includeFluids.get()) {
            return null;
         } else {
            int rgb = fog.fogColor.get();
            float red = (rgb >> 16 & 0xFF) / 255.0F;
            float green = (rgb >> 8 & 0xFF) / 255.0F;
            float blue = (rgb & 0xFF) / 255.0F;
            if (fog.keepBrightness.get()) {
               float target = luminance(original.x, original.y, original.z);
               float source = luminance(red, green, blue);
               float scale = source <= 0.001F ? 0.0F : target / source;
               red *= scale;
               green *= scale;
               blue *= scale;
            }

            return new Vector4f(mix(original.x, red, amount), mix(original.y, green, amount), mix(original.z, blue, amount), original.w);
         }
      } else {
         return null;
      }
   }

   private static float luminance(float red, float green, float blue) {
      return 0.2126F * red + 0.7152F * green + 0.0722F * blue;
   }

   private static float mix(float from, float to, float amount) {
      return Mth.clamp(Mth.lerp(amount, from, to), 0.0F, 1.0F);
   }
}
