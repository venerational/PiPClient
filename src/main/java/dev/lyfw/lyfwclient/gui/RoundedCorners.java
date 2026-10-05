package dev.lyfw.lyfwclient.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class RoundedCorners {
   private static final int SUPERSAMPLE = 4;
   private static final Map<Integer, Identifier> CACHE = new HashMap<>();

   private RoundedCorners() {
   }

   public static Identifier of(int radius) {
      Identifier cached = CACHE.get(radius);
      if (cached != null) {
         return cached;
      } else if (radius < 1) {
         return null;
      } else {
         int size = radius * 2;
         NativeImage image = new NativeImage(size, size, false);

         for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
               image.setPixel(x, y, coverage(x, y, radius) << 24 | 16777215);
            }
         }

         String name = "rounded_" + radius;
         Identifier id = Identifier.fromNamespaceAndPath("lyfw-client", name);
         Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "lyfw-client/" + name, image));
         CACHE.put(radius, id);
         return id;
      }
   }

   private static int coverage(int x, int y, int radius) {
      int inside = 0;

      for (int sy = 0; sy < 4; sy++) {
         for (int sx = 0; sx < 4; sx++) {
            double px = x + (sx + 0.5) / 4.0 - radius;
            double py = y + (sy + 0.5) / 4.0 - radius;
            if (px * px + py * py <= (double)radius * radius) {
               inside++;
            }
         }
      }

      return inside * 255 / 16;
   }
}
