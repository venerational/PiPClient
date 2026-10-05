package dev.lyfw.lyfwclient.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class AuroraIcons {
   private static final Map<String, Identifier> CACHE = new HashMap<>();

   private AuroraIcons() {
   }

   public static String glyphFor(String name) {
      String n = name.toLowerCase();
      if (n.contains("hitbox") || n.contains("outline")) {
         return "box";
      } else if (n.contains("keystroke") || n.contains("cps")) {
         return "keys";
      } else if (n.contains("totem") || n.contains("pop")) {
         return "totem";
      } else if (n.contains("fps") || n.contains("counter")) {
         return "chart";
      } else if (n.contains("coordinate") || n.contains("position")) {
         return "pin";
      } else if (n.contains("ping") || n.contains("reach")) {
         return "wifi";
      } else if (n.contains("potion") || n.contains("item")) {
         return "flask";
      } else if (n.contains("bright") || n.contains("light") || n.contains("fog")) {
         return "sun";
      } else if (n.contains("time") || n.contains("nostalgia")) {
         return "clock";
      } else if (n.contains("zoom")) {
         return "zoom";
      } else if (n.contains("shield") || n.contains("banner")) {
         return "shield";
      } else if (n.contains("tint") || n.contains("saturation") || n.contains("greyscale") || n.contains("opacity")) {
         return "droplet";
      } else if (n.contains("armor") || n.contains("armour")) {
         return "shield";
      } else if (n.contains("hide") || n.contains("clean") || n.contains("protect") || n.contains("transparent")) {
         return "eyeoff";
      } else if (n.contains("nametag") || n.contains("player") || n.contains("head") || n.contains("emote")) {
         return "person";
      } else if (n.contains("crosshair")) {
         return "crosshair";
      } else if (n.contains("heart") || n.contains("health") || n.contains("damage") || n.contains("hurt")) {
         return "heart";
      } else if (n.contains("hotbar") || n.contains("inventory") || n.contains("scale")) {
         return "hotbar";
      } else if (n.contains("hud") || n.contains("watermark")) {
         return "move";
      } else if (n.contains("trail") || n.contains("particle") || n.contains("blur") || n.contains("kill")) {
         return "spark";
      } else if (n.contains("theme") || n.contains("profile") || n.contains("preset")) {
         return "sliders";
      } else if (n.contains("song") || n.contains("music")) {
         return "note";
      } else if (!n.contains("leaderboard") && !n.contains("playtime")) {
         return !n.contains("sprint") && !n.contains("cooldown") && !n.contains("desync") ? "" : "bolt";
      } else {
         return "chart";
      }
   }

   public static void draw(GuiGraphics context, String glyph, int x, int y, int size, int color) {
      if (glyph != null && !glyph.isEmpty()) {
         Identifier texture = texture(glyph, size);
         if (texture != null) {
            context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, size, size, size, size, color);
         }
      }
   }

   private static Identifier texture(String glyph, int size) {
      String key = glyph + "@" + size;
      Identifier cached = CACHE.get(key);
      if (cached != null) {
         return cached;
      } else {
         IconRaster raster = new IconRaster(size);
         paint(raster, glyph, size);
         NativeImage image = new NativeImage(size, size, false);

         for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
               image.setPixel(px, py, raster.pixels[py * size + px]);
            }
         }

         String name = "icon_" + glyph.toLowerCase() + "_" + size;
         Identifier id = Identifier.fromNamespaceAndPath("lyfw-client", name);
         Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "lyfw-client/" + name, image));
         CACHE.put(key, id);
         return id;
      }
   }

   private static void paint(IconRaster r, String glyph, int size) {
      int cx = size / 2;
      int cy = size / 2;
      int w = -1;
      switch (glyph) {
         case "box":
            r.corners(2, 2, size - 4, size - 4, 5, 2, w);
            break;
         case "keys":
            r.rect(1, 3, 6, 5, w);
            r.rect(9, 3, 6, 5, w);
            r.rect(1, 10, 14, 5, w);
            break;
         case "totem":
            r.disc(cx, 4, 3, w);
            r.rect(cx - 1, 8, 3, 8, w);
            r.line(cx - 2, 11, cx - 6, 6, 2, w);
            r.line(cx + 3, 11, cx + 7, 6, 2, w);
            break;
         case "chart":
            r.rect(2, cy + 2, 3, 5, w);
            r.rect(7, cy - 2, 3, 9, w);
            r.rect(12, cy - 6, 3, 13, w);
            break;
         case "pin":
            r.ring(cx, 6, 5, 2, w);
            r.triangle(cx, size - 2, 4, w);
            break;
         case "wifi":
            r.arc(cx, cy + 5, 8, 2, w);
            r.arc(cx, cy + 5, 5, 2, w);
            r.disc(cx, cy + 4, 2, w);
            break;
         case "flask":
            r.rect(cx - 2, 2, 5, 4, w);
            r.triangle(cx, 4, -7, w);
            r.rect(cx - 6, 11, 13, 4, w);
            break;
         case "sun":
            r.disc(cx, cy, 4, w);
            r.rays(cx, cy, 6, 8, w);
            break;
         case "clock":
            r.ring(cx, cy, 7, 2, w);
            r.rect(cx - 1, cy - 4, 2, 5, w);
            r.rect(cx - 1, cy - 1, 5, 2, w);
            break;
         case "zoom":
            r.ring(cx - 1, cy - 1, 6, 2, w);
            r.line(cx + 3, cy + 3, cx + 7, cy + 7, 2, w);
            break;
         case "shield":
            r.shield(cx, 2, size - 5, w);
            break;
         case "droplet":
            r.triangle(cx, 2, -6, w);
            r.disc(cx, 10, 5, w);
            break;
         case "eyeoff":
            r.eye(cx, cy, w);
            r.line(2, size - 2, size - 2, 2, 2, w);
            break;
         case "person":
            r.disc(cx, 5, 3, w);
            r.rect(cx - 4, 10, 9, 6, w);
            break;
         case "crosshair":
            r.ring(cx, cy, 6, 2, w);
            r.rect(cx - 1, 1, 2, 4, w);
            r.rect(cx - 1, size - 5, 2, 4, w);
            r.rect(1, cy - 1, 4, 2, w);
            r.rect(size - 5, cy - 1, 4, 2, w);
            break;
         case "heart":
            r.disc(cx - 3, cy - 2, 4, w);
            r.disc(cx + 3, cy - 2, 4, w);
            r.triangle(cx, cy + 7, -7, w);
            break;
         case "hotbar":
            r.rect(1, cy - 4, 4, 9, w);
            r.rect(6, cy - 6, 4, 13, w);
            r.rect(11, cy - 4, 4, 9, w);
            break;
         case "move":
            r.rect(cx - 1, 2, 2, size - 4, w);
            r.rect(2, cy - 1, size - 4, 2, w);
            r.triangle(cx, 1, 3, w);
            r.triangle(cx, size - 1, -3, w);
            break;
         case "spark":
            r.rect(cx - 1, 1, 2, size - 2, w);
            r.rect(1, cy - 1, size - 2, 2, w);
            r.line(cx - 4, cy - 4, cx + 4, cy + 4, 2, w);
            r.line(cx + 4, cy - 4, cx - 4, cy + 4, 2, w);
            break;
         case "sliders":
            r.rect(1, cy - 5, size - 2, 2, w);
            r.rect(1, cy + 3, size - 2, 2, w);
            r.rect(4, cy - 7, 2, 6, w);
            r.rect(size - 7, cy + 1, 2, 6, w);
            break;
         case "bolt":
            r.triangle(cx + 1, 1, 6, w);
            r.triangle(cx - 1, size - 1, -6, w);
            break;
         case "refresh": {
            int rr = Math.max(3, size / 2 - 1);
            r.openRing(cx, cy, rr, 2, -1.5, -0.15, w);
            r.triangle(cx + rr, cy - rr + 2, 4, w);
            break;
         }
         case "note":
            r.disc(cx - 3, cy + 4, 3, w);
            r.rect(cx - 1, cy - 6, 2, 10, w);
            r.rect(cx - 1, cy - 6, 8, 2, w);
            r.rect(cx + 5, cy - 6, 2, 5, w);
            break;
         case "star":
            r.star(cx, cy, Math.max(3.0F, size * 0.47F), 5, 0.44F);
            break;
         case "star_outline":
            r.starOutline(cx, cy, Math.max(3.0F, size * 0.47F), 5, 0.44F, Math.max(1.0F, size / 9.0F));
            break;
         case "cat_render": {
            int rr = Math.max(3, Math.round(size * 0.36F));
            r.ring(cx, cy, rr, Math.max(2, size / 10), w);
            r.disc(cx, cy, Math.max(1, Math.round(size * 0.13F)), w);
            break;
         }
         case "cat_hud": {
            int m = Math.max(2, size / 8);
            int t = Math.max(1, size / 12);
            int bottom = size - m - Math.max(2, size / 7);
            r.rect(m, m, size - m * 2, t, w);
            r.rect(m, bottom, size - m * 2, t, w);
            r.rect(m, m, t, bottom - m, w);
            r.rect(size - m - t, m, t, bottom - m, w);
            r.rect(m + t + 2, bottom - t - Math.max(2, size / 6), Math.round(size * 0.34F), t, w);
            break;
         }
         case "cat_misc": {
            int t = Math.max(1, size / 10);
            int m = Math.max(2, size / 8);
            int knob = Math.max(2, size / 8);

            for (int i = 0; i < 3; i++) {
               int ly = Math.round(size * (0.28F + i * 0.22F));
               r.rect(m, ly, size - m * 2, t, w);
               r.disc(Math.round(size * (i == 1 ? 0.66F : 0.36F)), ly + t / 2, knob, w);
            }
            break;
         }
         case "cat_new": {
            int t = Math.max(2, Math.round(size * 0.16F));
            int arm = Math.round(size * 0.34F);
            r.rect(cx - t / 2, cy - arm, t, arm * 2, w);
            r.rect(cx - arm, cy - t / 2, arm * 2, t, w);
            break;
         }
         case "cat_updated": {
            int rr = Math.max(3, Math.round(size * 0.34F));
            r.openRing(cx, cy, rr, Math.max(2, size / 10), -1.9, -0.4, w);
            r.triangle(cx + rr, cy - rr + Math.max(2, size / 6), Math.max(3, size / 4), w);
            break;
         }
         case "cat_all": {
            int m = Math.max(2, size / 8);
            int gap = Math.max(1, size / 10);
            int tile = (size - m * 2 - gap) / 2;

            for (int i = 0; i < 4; i++) {
               r.rect(m + i % 2 * (tile + gap), m + i / 2 * (tile + gap), tile, tile, w);
            }
            break;
         }
         case "cat_favorite":
            r.star(cx, cy, Math.max(3, Math.round(size * 0.42F)), 5, 0.45F);
      }
   }
}
