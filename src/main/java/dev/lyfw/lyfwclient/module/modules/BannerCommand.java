package dev.lyfw.lyfwclient.module.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public record BannerCommand(DyeColor base, List<BannerCommand.Layer> layers) {
   private static final Map<String, String> LEGACY_CODES = Map.ofEntries(
      Map.entry("b", "base"),
      Map.entry("bl", "square_bottom_left"),
      Map.entry("br", "square_bottom_right"),
      Map.entry("tl", "square_top_left"),
      Map.entry("tr", "square_top_right"),
      Map.entry("bs", "stripe_bottom"),
      Map.entry("ts", "stripe_top"),
      Map.entry("ls", "stripe_left"),
      Map.entry("rs", "stripe_right"),
      Map.entry("cs", "stripe_center"),
      Map.entry("ms", "stripe_middle"),
      Map.entry("drs", "stripe_downright"),
      Map.entry("dls", "stripe_downleft"),
      Map.entry("ss", "small_stripes"),
      Map.entry("cr", "cross"),
      Map.entry("sc", "straight_cross"),
      Map.entry("bt", "triangle_bottom"),
      Map.entry("tt", "triangle_top"),
      Map.entry("bts", "triangles_bottom"),
      Map.entry("tts", "triangles_top"),
      Map.entry("ld", "diagonal_left"),
      Map.entry("rud", "diagonal_up_right"),
      Map.entry("lud", "diagonal_up_left"),
      Map.entry("rd", "diagonal_right"),
      Map.entry("mc", "circle"),
      Map.entry("mr", "rhombus"),
      Map.entry("vh", "half_vertical"),
      Map.entry("hh", "half_horizontal"),
      Map.entry("vhr", "half_vertical_right"),
      Map.entry("hhb", "half_horizontal_bottom"),
      Map.entry("bo", "border"),
      Map.entry("cbo", "curly_border"),
      Map.entry("gra", "gradient"),
      Map.entry("gru", "gradient_up"),
      Map.entry("bri", "bricks"),
      Map.entry("glb", "globe"),
      Map.entry("cre", "creeper"),
      Map.entry("sku", "skull"),
      Map.entry("flo", "flower"),
      Map.entry("moj", "mojang"),
      Map.entry("pig", "piglin"),
      Map.entry("flw", "flow"),
      Map.entry("gus", "guster")
   );
   private static final Pattern BLOCK = Pattern.compile("\\{([^{}]*)\\}");
   private static final Pattern MODERN_PATTERN = Pattern.compile("pattern\\s*[:=]\\s*\"?([A-Za-z0-9_:./-]+)\"?", 2);
   private static final Pattern MODERN_COLOR = Pattern.compile("color\\s*[:=]\\s*\"?([A-Za-z0-9_:]+)\"?", 2);
   private static final Pattern MODERN_BASE = Pattern.compile("base_color\\s*[:=]\\s*\"?([A-Za-z0-9_:]+)\"?", 2);
   private static final Pattern LEGACY_BASE = Pattern.compile("\\bBase\\s*:\\s*(\\d+)");

   public static BannerCommand parse(String text) {
      if (text != null && !text.isBlank()) {
         boolean legacy = !text.contains("banner_patterns");
         String list = extractList(text, legacy ? "Patterns" : "banner_patterns");
         if (list == null) {
            return null;
         } else {
            List<BannerCommand.Layer> layers = new ArrayList<>();
            Matcher blocks = BLOCK.matcher(list);

            while (blocks.find()) {
               String block = blocks.group(1);
               Identifier pattern = patternOf(block, legacy);
               if (pattern != null) {
                  layers.add(new BannerCommand.Layer(pattern, colorOf(block)));
               }
            }

            return layers.isEmpty() ? null : new BannerCommand(baseOf(text, legacy), List.copyOf(layers));
         }
      } else {
         return null;
      }
   }

   private static String extractList(String text, String key) {
      int keyAt = text.toLowerCase(Locale.ROOT).indexOf(key.toLowerCase(Locale.ROOT));
      if (keyAt < 0) {
         return null;
      } else {
         int open = text.indexOf(91, keyAt);
         if (open < 0) {
            return null;
         } else {
            int depth = 0;

            for (int i = open; i < text.length(); i++) {
               char c = text.charAt(i);
               if (c == '[') {
                  depth++;
               } else if (c == ']') {
                  if (--depth == 0) {
                     return text.substring(open, i + 1);
                  }
               }
            }

            return text.substring(open);
         }
      }
   }

   private static Identifier patternOf(String block, boolean legacy) {
      Matcher m = (legacy ? Pattern.compile("Pattern\\s*:\\s*\"?([A-Za-z0-9_:./-]+)\"?") : MODERN_PATTERN).matcher(block);
      if (!m.find()) {
         return null;
      } else {
         String raw = m.group(1).trim();
         String name = raw.contains(":") ? raw.substring(raw.indexOf(58) + 1) : raw;
         name = name.toLowerCase(Locale.ROOT);
         name = LEGACY_CODES.getOrDefault(name, name);
         return Identifier.tryParse("minecraft:" + name);
      }
   }

   private static DyeColor colorOf(String block) {
      Matcher m = MODERN_COLOR.matcher(block);
      return m.find() ? dye(m.group(1), DyeColor.WHITE) : DyeColor.WHITE;
   }

   private static DyeColor baseOf(String text, boolean legacy) {
      if (legacy) {
         Matcher m = LEGACY_BASE.matcher(text);
         return m.find() ? dye(m.group(1), DyeColor.WHITE) : DyeColor.WHITE;
      } else {
         Matcher m = MODERN_BASE.matcher(text);
         return m.find() ? dye(m.group(1), DyeColor.WHITE) : DyeColor.WHITE;
      }
   }

   private static DyeColor dye(String raw, DyeColor fallback) {
      String cleaned = raw.trim().toLowerCase(Locale.ROOT);
      if (cleaned.contains(":")) {
         cleaned = cleaned.substring(cleaned.indexOf(58) + 1);
      }

      for (DyeColor color : DyeColor.values()) {
         if (color.getName().equals(cleaned)) {
            return color;
         }
      }

      try {
         int index = Integer.parseInt(cleaned);

         for (DyeColor colorx : DyeColor.values()) {
            if (colorx.getId() == index) {
               return colorx;
            }
         }
      } catch (NumberFormatException var8) {
      }

      return fallback;
   }

   public record Layer(Identifier pattern, DyeColor color) {
   }
}
