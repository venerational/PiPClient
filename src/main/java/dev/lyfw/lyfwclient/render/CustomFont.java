package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class CustomFont {
   private static final int FIRST = 32;
   private static final int LAST = 383;
   private static final int COUNT = 352;
   private static final int COLUMNS = 20;
   private static final int CELL = 64;
   private static final int RASTER_POINTS = 48;
   private static CustomFont current;
   private static String currentSource;
   private static int generation;
   private final Identifier texture;
   private final int atlasWidth;
   private final int atlasHeight;
   private final int[] glyphWidth = new int[352];
   private final int[] glyphBearing = new int[352];
   private final int[] advance = new int[352];
   private final int ascent;
   private final int lineHeight;
   private final int capHeight;

   private CustomFont(Identifier texture, int atlasWidth, int atlasHeight, int ascent, int lineHeight, int capHeight) {
      this.texture = texture;
      this.atlasWidth = atlasWidth;
      this.atlasHeight = atlasHeight;
      this.ascent = ascent;
      this.lineHeight = lineHeight;
      this.capHeight = capHeight;
   }

   public static CustomFont get(String source) {
      String wanted = source == null ? "" : source.trim();
      if (wanted.isEmpty()) {
         current = null;
         currentSource = "";
         return null;
      } else {
         if (!wanted.equals(currentSource)) {
            currentSource = wanted;
            current = build(wanted);
         }

         return current;
      }
   }

   private static CustomFont build(String source) {
      try {
         Font font = load(source);
         if (font == null) {
            return null;
         } else {
            font = font.deriveFont(0, 48.0F);
            int rows = 18;
            int width = 1280;
            int height = rows * 64;
            BufferedImage sheet = new BufferedImage(width, height, 2);
            Graphics2D g = sheet.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setFont(font);
            g.setColor(Color.WHITE);
            FontMetrics metrics = g.getFontMetrics();
            Rectangle2D capBounds = font.createGlyphVector(g.getFontRenderContext(), "H").getVisualBounds();
            int cap = Math.max(1, (int)Math.round(capBounds.getHeight()));
            CustomFont built = new CustomFont(
               Identifier.fromNamespaceAndPath("lyfw-client", "custom_font_" + generation++), width, height, metrics.getAscent(), metrics.getHeight(), cap
            );

            for (int i = 0; i < 352; i++) {
               char c = (char)(32 + i);
               int cx = i % 20 * 64;
               int cy = i / 20 * 64;
               g.drawString(String.valueOf(c), cx + 2, cy + metrics.getAscent());
               built.advance[i] = metrics.charWidth(c);
               built.glyphWidth[i] = Math.min(64, metrics.charWidth(c) + 4);
               built.glyphBearing[i] = 2;
            }

            g.dispose();
            NativeImage image = new NativeImage(width, height, false);

            for (int y = 0; y < height; y++) {
               for (int x = 0; x < width; x++) {
                  image.setPixel(x, y, sheet.getRGB(x, y));
               }
            }

            String name = built.texture.getPath();
            Minecraft.getInstance().getTextureManager().register(built.texture, new DynamicTexture(() -> "lyfw-client/" + name, image));
            return built;
         }
      } catch (Exception var15) {
         System.out.println("[Pip Client] could not load font \"" + source + "\" (" + var15 + ")");
         return null;
      }
   }

   private static Font load(String source) throws Exception {
      String lower = source.toLowerCase(Locale.ROOT);
      if (!lower.endsWith(".ttf") && !lower.endsWith(".otf")) {
         Font font = new Font(source, 0, 48);
         if (!font.getFamily().equalsIgnoreCase(source) && font.getFamily().equalsIgnoreCase("Dialog")) {
            System.out.println("[Pip Client] no font installed called \"" + source + "\"");
            return null;
         } else {
            return font;
         }
      } else {
         Path path = FabricLoader.getInstance().getConfigDir().resolve(source);
         if (!Files.isRegularFile(path)) {
            System.out.println("[Pip Client] no font file at " + path);
            return null;
         } else {
            Font var4;
            try (InputStream in = Files.newInputStream(path)) {
               var4 = Font.createFont(0, in);
            }

            return var4;
         }
      }
   }

   public static boolean supports(char c) {
      return c >= ' ' && c <= 383;
   }

   private int rawWidth(String text) {
      int total = 0;

      for (int i = 0; i < text.length(); i++) {
         total += this.advanceOf(text.charAt(i));
      }

      return total;
   }

   private int advanceOf(char c) {
      int index = c - ' ';
      return index >= 0 && index < 352 ? this.advance[index] : this.advance[31];
   }

   public int rawHeight() {
      return this.lineHeight;
   }

   public float scaleFor(float targetHeight) {
      return targetHeight / Math.max(1, this.capHeight);
   }

   public float width(String text, float scale) {
      return this.rawWidth(text) * scale;
   }

   public void draw(GuiGraphics context, String text, float x, float y, int color, boolean shadow, float scale, float squeeze) {
      if (shadow) {
         int alpha = color >>> 24;
         int dark = (alpha == 0 ? 255 : alpha) << 24 | color >> 2 & 4144959;
         this.drawLayer(context, text, x + scale, y + scale, dark, scale, squeeze);
      }

      this.drawLayer(context, text, x, y, color, scale, squeeze);
   }

   private void drawLayer(GuiGraphics context, String text, float x, float y, int color, float scale, float squeeze) {
      int tint = color >>> 24 == 0 ? 0xFF000000 | color : color;
      context.pose().pushMatrix();
      context.pose().translate(x, y - (this.ascent - this.capHeight) * scale);
      context.pose().scale(scale * squeeze, scale);
      int pen = 0;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         int index = c - ' ';
         if (index >= 0 && index < 352) {
            int cx = index % 20 * 64;
            int cy = index / 20 * 64;
            int w = this.glyphWidth[index];
            if (w > 0 && c != ' ') {
               context.blit(
                  RenderPipelines.GUI_TEXTURED, this.texture, pen - this.glyphBearing[index], 0, cx, cy, w, 64, this.atlasWidth, this.atlasHeight, tint
               );
            }
         }

         pen += this.advanceOf(c);
      }

      context.pose().popMatrix();
   }
}
