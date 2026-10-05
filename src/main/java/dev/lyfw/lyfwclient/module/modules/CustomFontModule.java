package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.render.CustomFont;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ChoiceSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.awt.GraphicsEnvironment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

public class CustomFontModule extends Module {
   private static final float VANILLA_CAP = 7.0F;
   public static boolean bypass;
   private final ChoiceSetting fontSource = this.register(new ChoiceSetting("Font", "Arial", CustomFontModule::availableFonts));
   private final SliderSetting sizeAdjust = this.register(new SliderSetting("Size", 100.0, 60.0, 160.0, 1.0, "%"));
   private final SliderSetting baseline = this.register(new SliderSetting("Baseline", 0.0, -4.0, 4.0, 0.5, "px"));
   private final BooleanSetting matchWidths = this.register(new BooleanSetting("Match Vanilla Widths", true));
   private final BooleanSetting inPipHud = this.register(new BooleanSetting("Pip Client HUD", true));
   private final BooleanSetting inHud = this.register(new BooleanSetting("Game HUD & Chat", true));
   private final BooleanSetting inScreens = this.register(new BooleanSetting("Menus & Client GUI", true));

   public CustomFontModule() {
      super("Custom Font", "Draws the HUD, chat, menus and the client's own screens in a font of your choosing.", Category.RENDER, false);
      this.fontSource.group = "Font";
      this.sizeAdjust.group = "Font";
      this.baseline.group = "Font";
      this.matchWidths.group = "Font";
      this.inPipHud.group = "Where";
      this.inHud.group = "Where";
      this.inScreens.group = "Where";
   }

   public boolean appliesToPipHud() {
      return this.inPipHud.get();
   }

   public boolean appliesToHud() {
      return this.inHud.get();
   }

   public boolean appliesToScreens() {
      return this.inScreens.get();
   }

   public static List<String> availableFonts() {
      List<String> out = new ArrayList<>();

      try (Stream<Path> files = Files.list(FabricLoader.getInstance().getConfigDir())) {
         files.<String>map(path -> path.getFileName().toString())
            .filter(name -> name.toLowerCase(Locale.ROOT).endsWith(".ttf") || name.toLowerCase(Locale.ROOT).endsWith(".otf"))
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .forEach(out::add);
      } catch (Exception var8) {
      }

      try {
         for (String family : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!out.contains(family)) {
               out.add(family);
            }
         }
      } catch (Exception var6) {
         System.out.println("[Pip Client] could not list the installed fonts (" + var6 + ")");
      }

      return List.copyOf(out);
   }

   public CustomFont font() {
      return this.isEnabled() ? CustomFont.get(this.fontSource.get()) : null;
   }

   public static List<CustomFontModule.Run> runsOf(FormattedCharSequence text, int fallback) {
      List<CustomFontModule.Run> runs = new ArrayList<>();
      StringBuilder current = new StringBuilder();
      int[] currentColor = new int[]{Integer.MIN_VALUE};
      text.accept((index, style, codePoint) -> {
         TextColor styled = style.getColor();
         int color = styled == null ? fallback : 0xFF000000 | styled.getValue();
         if (color != currentColor[0]) {
            if (current.length() > 0) {
               runs.add(new CustomFontModule.Run(current.toString(), currentColor[0]));
               current.setLength(0);
            }

            currentColor[0] = color;
         }

         current.appendCodePoint(codePoint);
         return true;
      });
      if (current.length() > 0) {
         runs.add(new CustomFontModule.Run(current.toString(), currentColor[0]));
      }

      return runs;
   }

   public boolean draw(GuiGraphics context, List<CustomFontModule.Run> runs, float x, float y, boolean shadow, int vanillaWidth) {
      CustomFont font = this.font();
      if (font != null && !runs.isEmpty()) {
         float scale = font.scaleFor(7.0F) * (float)(this.sizeAdjust.get() / 100.0);
         float squeeze = 1.0F;
         if (this.matchWidths.get() && vanillaWidth > 0) {
            float natural = 0.0F;

            for (CustomFontModule.Run run : runs) {
               natural += font.width(run.text(), scale);
            }

            if (natural > 0.0F) {
               squeeze = Math.min(1.0F, vanillaWidth / natural);
            }
         }

         float pen = x;

         for (CustomFontModule.Run run : runs) {
            pen = this.drawSegments(context, font, run.text(), pen, y, run.color(), shadow, scale, squeeze);
         }

         return true;
      } else {
         return false;
      }
   }

   private float drawSegments(GuiGraphics context, CustomFont font, String text, float x, float y, int color, boolean shadow, float scale, float squeeze) {
      float pen = x;
      float dy = (float)this.baseline.get().doubleValue();
      int start = 0;

      while (start < text.length()) {
         boolean supported = CustomFont.supports(text.charAt(start));
         int end = start + 1;

         while (end < text.length() && CustomFont.supports(text.charAt(end)) == supported) {
            end++;
         }

         String segment = text.substring(start, end);
         if (supported) {
            font.draw(context, segment, pen, y + dy, color, shadow, scale, squeeze);
            pen += font.width(segment, scale) * squeeze;
         } else {
            Font vanilla = Minecraft.getInstance().font;
            bypass = true;

            try {
               context.drawString(vanilla, segment, Math.round(pen), Math.round(y), color, shadow);
            } finally {
               bypass = false;
            }

            pen += vanilla.width(segment);
         }

         start = end;
      }

      return pen;
   }

   public boolean draw(GuiGraphics context, String text, float x, float y, int color, boolean shadow, int vanillaWidth) {
      CustomFont font = this.font();
      if (font != null && !text.isEmpty()) {
         float scale = font.scaleFor(7.0F) * (float)(this.sizeAdjust.get() / 100.0);
         float squeeze = 1.0F;
         if (this.matchWidths.get() && vanillaWidth > 0) {
            float natural = font.width(text, scale);
            if (natural > 0.0F) {
               squeeze = Math.min(1.0F, vanillaWidth / natural);
            }
         }

         this.drawSegments(context, font, text, x, y, color, shadow, scale, squeeze);
         return true;
      } else {
         return false;
      }
   }

   public record Run(String text, int color) {
   }
}
