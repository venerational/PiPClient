package dev.lyfw.lyfwclient.gui;

public final class Theme {
   public static final int DEFAULT_ACCENT = -11567873;
   private static final float ACCENT2_HUE_SHIFT = 36.0F;
   private static Theme.Preset preset = Theme.Preset.MIDNIGHT;
   private static GuiLayout layout = GuiLayout.CARROT;
   private static int accent = -11567873;
   private static boolean shadows = true;
   private static boolean scrim = true;

   private Theme() {
   }

   public static GuiLayout layout() {
      return layout;
   }

   public static void setLayout(GuiLayout next) {
      layout = next;
   }

   public static void setLayoutByName(String name) {
      for (GuiLayout l : GuiLayout.values()) {
         if (l.name().equals(name)) {
            layout = l;
            return;
         }
      }
   }

   public static Theme.Preset preset() {
      return preset;
   }

   public static void setPreset(Theme.Preset next) {
      boolean accentWasDefault = accent == preset.palette.defaultAccent();
      preset = next;
      if (accentWasDefault) {
         accent = next.palette.defaultAccent();
      }
   }

   public static void setPresetByName(String name) {
      for (Theme.Preset p : Theme.Preset.values()) {
         if (p.name().equals(name)) {
            setPreset(p);
            return;
         }
      }
   }

   public static Theme.Palette palette() {
      return preset.palette;
   }

   public static Theme.Style style() {
      return preset.palette.style();
   }

   public static boolean isMinecraftStyle() {
      return preset.palette.style() == Theme.Style.MINECRAFT;
   }

   public static int accent() {
      return accent;
   }

   public static int accentRowBg() {
      return accent & 16777215 | 855638016;
   }

   public static int accent2() {
      float[] hsv = rgbToHsv(accent);
      float hue = (hsv[0] + 36.0F) % 360.0F;
      return hsvToRgb(hue, hsv[1], hsv[2]);
   }

   public static void setAccent(int argb) {
      accent = 0xFF000000 | argb & 16777215;
   }

   public static void resetAccent() {
      accent = preset.palette.defaultAccent();
   }

   public static void resetAll() {
      preset = Theme.Preset.MIDNIGHT;
      layout = GuiLayout.CARROT;
      shadows = true;
      scrim = true;
      accent = Theme.Preset.MIDNIGHT.palette.defaultAccent();
   }

   public static int panelBg() {
      return preset.palette.panelBg();
   }

   public static int headerBg() {
      return preset.palette.headerBg();
   }

   public static int border() {
      return preset.palette.border();
   }

   public static int rowBg() {
      return preset.palette.rowBg();
   }

   public static int rowBgHover() {
      return preset.palette.rowBgHover();
   }

   public static int trackBg() {
      return preset.palette.trackBg();
   }

   public static int closeBg() {
      return preset.palette.closeBg();
   }

   public static int modalPanelBg() {
      return preset.palette.modalPanelBg();
   }

   public static int modalHeaderBg() {
      return preset.palette.modalHeaderBg();
   }

   public static int modalBorder() {
      return preset.palette.modalBorder();
   }

   public static int shadow() {
      return shadows ? preset.palette.shadow() : 0;
   }

   public static boolean shadowsEnabled() {
      return shadows;
   }

   public static void setShadows(boolean enabled) {
      shadows = enabled;
   }

   public static boolean scrimEnabled() {
      return scrim;
   }

   public static void setScrim(boolean enabled) {
      scrim = enabled;
   }

   public static int hoverBg() {
      return preset.palette.hoverBg();
   }

   public static int scrim() {
      return scrim ? preset.palette.scrim() : 0;
   }

   public static int textPrimary() {
      return preset.palette.textPrimary();
   }

   public static int textSecondary() {
      return preset.palette.textSecondary();
   }

   public static int textMuted() {
      return preset.palette.textMuted();
   }

   public static int sectionText() {
      return preset.palette.sectionText();
   }

   public static int sectionDivider() {
      return preset.palette.sectionDivider();
   }

   public static int accentText() {
      return preset.palette.accentText();
   }

   public static int knob() {
      return preset.palette.knob();
   }

   public static int danger() {
      return preset.palette.danger();
   }

   public static int dangerBg() {
      return preset.palette.dangerBg();
   }

   public static int panelRadius() {
      return preset.palette.panelRadius();
   }

   public static int rowRadius() {
      return preset.palette.rowRadius();
   }

   public static int smallRadius() {
      return preset.palette.smallRadius();
   }

   private static int hsvToRgb(float hue, float saturation, float value) {
      float c = value * saturation;
      float x = c * (1.0F - Math.abs(hue / 60.0F % 2.0F - 1.0F));
      float m = value - c;
      float r;
      float g;
      float b;
      if (hue < 60.0F) {
         r = c;
         g = x;
         b = 0.0F;
      } else if (hue < 120.0F) {
         r = x;
         g = c;
         b = 0.0F;
      } else if (hue < 180.0F) {
         r = 0.0F;
         g = c;
         b = x;
      } else if (hue < 240.0F) {
         r = 0.0F;
         g = x;
         b = c;
      } else if (hue < 300.0F) {
         r = x;
         g = 0.0F;
         b = c;
      } else {
         r = c;
         g = 0.0F;
         b = x;
      }

      int ri = Math.round((r + m) * 255.0F);
      int gi = Math.round((g + m) * 255.0F);
      int bi = Math.round((b + m) * 255.0F);
      return 0xFF000000 | ri << 16 | gi << 8 | bi;
   }

   private static float[] rgbToHsv(int argb) {
      int r = argb >> 16 & 0xFF;
      int g = argb >> 8 & 0xFF;
      int b = argb & 0xFF;
      float rf = r / 255.0F;
      float gf = g / 255.0F;
      float bf = b / 255.0F;
      float max = Math.max(rf, Math.max(gf, bf));
      float min = Math.min(rf, Math.min(gf, bf));
      float delta = max - min;
      float h;
      if (delta == 0.0F) {
         h = 0.0F;
      } else if (max == rf) {
         h = 60.0F * ((gf - bf) / delta % 6.0F);
      } else if (max == gf) {
         h = 60.0F * ((bf - rf) / delta + 2.0F);
      } else {
         h = 60.0F * ((rf - gf) / delta + 4.0F);
      }

      if (h < 0.0F) {
         h += 360.0F;
      }

      float s = max == 0.0F ? 0.0F : delta / max;
      return new float[]{h, s, max};
   }

   public record Palette(
      Theme.Style style,
      int defaultAccent,
      int panelBg,
      int headerBg,
      int border,
      int rowBg,
      int rowBgHover,
      int trackBg,
      int closeBg,
      int modalPanelBg,
      int modalHeaderBg,
      int modalBorder,
      int shadow,
      int hoverBg,
      int scrim,
      int textPrimary,
      int textSecondary,
      int textMuted,
      int sectionText,
      int sectionDivider,
      int accentText,
      int knob,
      int danger,
      int dangerBg,
      int panelRadius,
      int rowRadius,
      int smallRadius
   ) {
   }

   public static enum Preset {
      MIDNIGHT(
         "Midnight",
         "The original Pip look - deep blue-gray with soft rounded panels.",
         new Theme.Palette(
            Theme.Style.FLAT,
            -11567873,
            -15329249,
            -15000025,
            -14013128,
            -14868439,
            -14276042,
            -14276040,
            -13686738,
            -233432801,
            -15065562,
            1090519039,
            1610612736,
            587202559,
            -1610612736,
            -1,
            -6643520,
            -3683361,
            -6386433,
            687865855,
            -16051680,
            -657921,
            -41892,
            872373340,
            8,
            4,
            5
         )
      ),
      OBSIDIAN(
         "Obsidian",
         "Near-black panels with a violet accent. Easiest on an OLED screen.",
         new Theme.Palette(
            Theme.Style.FLAT,
            -5214977,
            -16119284,
            -15724525,
            -14474455,
            -15461352,
            -14803420,
            -14935006,
            -14016208,
            -234223092,
            -15724525,
            1090519039,
            1879048192,
            587202559,
            -1342177280,
            -1,
            -6645082,
            -3158056,
            -3695617,
            687865855,
            -15594980,
            -593153,
            -41892,
            872373340,
            8,
            4,
            5
         )
      ),
      NORD(
         "Nord",
         "Cool arctic slate with a pale cyan accent.",
         new Theme.Palette(
            Theme.Style.FLAT,
            -7814960,
            -13749184,
            -12893614,
            -11774358,
            -12893614,
            -12366754,
            -12366754,
            -11779502,
            -231852992,
            -12893614,
            1090519039,
            1610612736,
            587202559,
            -1610612736,
            -1249292,
            -5655612,
            -2564375,
            -8281663,
            687865855,
            -14670289,
            -1249292,
            -4234902,
            868180330,
            8,
            4,
            5
         )
      ),
      DAYLIGHT(
         "Daylight",
         "A light theme for bright rooms and screenshots.",
         new Theme.Palette(
            Theme.Style.FLAT,
            -13668371,
            -854792,
            -1643790,
            -3419167,
            -1,
            -1446153,
            -2300946,
            -992550,
            -218958600,
            -1643790,
            1073741824,
            805306368,
            335544320,
            1610612736,
            -15459804,
            -10720640,
            -13945530,
            -13668371,
            570425344,
            -1,
            -1,
            -3003348,
            651308076,
            8,
            4,
            5
         )
      ),
      TERMINAL(
         "Terminal",
         "Black background, phosphor green text. Maximum contrast.",
         new Theme.Palette(
            Theme.Style.FLAT,
            -13369498,
            -16447483,
            -16118006,
            -14996453,
            -16051701,
            -15524077,
            -15524077,
            -14412780,
            -234551291,
            -16118006,
            1090519039,
            1879048192,
            587202559,
            -1342177280,
            -4653112,
            -10508177,
            -7350628,
            -13369498,
            674496358,
            -16509942,
            -3145766,
            -43691,
            872371541,
            8,
            4,
            5
         )
      ),
      MINECRAFT(
         "Minecraft",
         "Drawn like a vanilla screen - real button sprites, square corners, no rounding.",
         new Theme.Palette(
            Theme.Style.MINECRAFT,
            -11207852,
            -1072689136,
            -804253680,
            -16777216,
            -7631989,
            -6250336,
            -16777216,
            -7631989,
            -535818224,
            -401600496,
            -16777216,
            Integer.MIN_VALUE,
            822083583,
            -1072689136,
            -1,
            -6250336,
            -2039584,
            -171,
            1090519039,
            -16777216,
            -1,
            -43691,
            1090475349,
            0,
            0,
            0
         )
      );

      public final String title;
      public final String description;
      public final Theme.Palette palette;

      private Preset(String title, String description, Theme.Palette palette) {
         this.title = title;
         this.description = description;
         this.palette = palette;
      }
   }

   public static enum Style {
      FLAT,
      MINECRAFT;
   }
}
