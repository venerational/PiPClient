package dev.lyfw.lyfwclient.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class ColorWheel {
   private static final Map<Integer, Identifier> CACHE = new HashMap<>();

   private ColorWheel() {
   }

   public static int size(int radius) {
      return radius * 2 + 1;
   }

   public static Identifier texture(int radius) {
      Identifier cached = CACHE.get(radius);
      if (cached != null) {
         return cached;
      } else {
         int size = size(radius);
         NativeImage image = new NativeImage(size, size, false);

         for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
               image.setPixel(px, py, pixel(px - radius, py - radius, radius));
            }
         }

         String name = "color_wheel_" + radius;
         Identifier id = Identifier.fromNamespaceAndPath("lyfw-client", name);
         Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "lyfw-client/" + name, image));
         CACHE.put(radius, id);
         return id;
      }
   }

   private static int pixel(int dx, int dy, int radius) {
      double dist = Math.sqrt((double)dx * dx + (double)dy * dy);
      if (dist > radius + 0.5) {
         return 0;
      } else {
         double degrees = Math.toDegrees(Math.atan2(dy, dx));
         if (degrees < 0.0) {
            degrees += 360.0;
         }

         int rgb = hsvToRgb((float)degrees, (float)Math.min(1.0, dist / radius));
         int alpha = (int)Math.round(255.0 * Math.min(1.0, Math.max(0.0, radius + 0.5 - dist)));
         return alpha << 24 | rgb & 16777215;
      }
   }

   private static int hsvToRgb(float hue, float saturation) {
      int sector = (int)(hue / 60.0F) % 6;
      float fraction = hue / 60.0F - (int)(hue / 60.0F);
      int full = 255;
      int p = Math.round(255.0F * (1.0F - saturation));
      int q = Math.round(255.0F * (1.0F - saturation * fraction));
      int t = Math.round(255.0F * (1.0F - saturation * (1.0F - fraction)));

      return switch (sector) {
         case 0 -> full << 16 | t << 8 | p;
         case 1 -> q << 16 | full << 8 | p;
         case 2 -> p << 16 | full << 8 | t;
         case 3 -> p << 16 | q << 8 | full;
         case 4 -> t << 16 | p << 8 | full;
         default -> full << 16 | p << 8 | q;
      };
   }
}
