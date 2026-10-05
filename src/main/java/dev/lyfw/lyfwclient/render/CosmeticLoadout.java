package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public record CosmeticLoadout(
   List<CosmeticLoadout.Piece<CosmeticsModule.Headwear>> headwear,
   List<CosmeticLoadout.Piece<CosmeticsModule.WingType>> wings,
   float flapSpeed,
   CosmeticLoadout.Cape cape,
   List<CosmeticLoadout.Piece<CosmeticsModule.Pet>> pets,
   String trail
) {
   public static final CosmeticLoadout EMPTY = new CosmeticLoadout(List.of(), List.of(), 1.0F, null, List.of(), "");
   public static final int MAX_HEADWEAR = 24;
   public static final int MAX_WINGS = 12;
   public static final int MAX_PETS = 6;
   public static final float MIN_SIZE = 0.4F;
   public static final float MAX_HEADWEAR_SIZE = 2.5F;
   public static final float MAX_WING_SIZE = 3.0F;
   public static final float MAX_PET_SIZE = 2.5F;
   public static final float MAX_ANGLE = 180.0F;
   private static final String VERSION = "v1";
   private static final String TRAIL_TAG = "TRAIL:";

   public CosmeticLoadout(
      List<CosmeticLoadout.Piece<CosmeticsModule.Headwear>> headwear,
      List<CosmeticLoadout.Piece<CosmeticsModule.WingType>> wings,
      float flapSpeed,
      CosmeticLoadout.Cape cape
   ) {
      this(headwear, wings, flapSpeed, cape, List.of(), "");
   }

   public CosmeticLoadout(
      List<CosmeticLoadout.Piece<CosmeticsModule.Headwear>> headwear,
      List<CosmeticLoadout.Piece<CosmeticsModule.WingType>> wings,
      float flapSpeed,
      CosmeticLoadout.Cape cape,
      List<CosmeticLoadout.Piece<CosmeticsModule.Pet>> pets
   ) {
      this(headwear, wings, flapSpeed, cape, pets, "");
   }

   public CosmeticLoadout(
      List<CosmeticLoadout.Piece<CosmeticsModule.Headwear>> headwear,
      List<CosmeticLoadout.Piece<CosmeticsModule.WingType>> wings,
      float flapSpeed,
      CosmeticLoadout.Cape cape,
      List<CosmeticLoadout.Piece<CosmeticsModule.Pet>> pets,
      String trail
   ) {
      trail = Trails.normalize(trail);
      this.headwear = headwear;
      this.wings = wings;
      this.flapSpeed = flapSpeed;
      this.cape = cape;
      this.pets = pets;
      this.trail = trail;
   }

   public boolean isEmpty() {
      return this.headwear.isEmpty() && this.wings.isEmpty() && this.cape == null && this.pets.isEmpty() && this.trail.isEmpty();
   }

   public String encode() {
      StringBuilder out = new StringBuilder("v1").append('|');
      pieces(out, this.headwear, 24);
      out.append('|');
      pieces(out, this.wings, 12);
      out.append('|').append(number(this.flapSpeed)).append('|');
      if (this.cape != null) {
         out.append(this.cape.style().name())
            .append(',')
            .append(this.cape.name() == null ? "" : this.cape.name())
            .append(',')
            .append(hex(this.cape.color()))
            .append(',');
         if (this.cape.style() == CosmeticsModule.CapeStyle.DRAWN && this.cape.pixels() != null) {
            out.append(this.cape.pixels());
         }
      }

      if (!this.pets.isEmpty() || !this.trail.isEmpty()) {
         out.append('|');
         pieces(out, this.pets, 6);
         if (!this.trail.isEmpty()) {
            out.append(this.pets.isEmpty() ? "" : ";").append("TRAIL:").append(this.trail).append(",1,0,0,00000000,00000000");
         }
      }

      return out.toString();
   }

   private static void pieces(StringBuilder out, List<? extends CosmeticLoadout.Piece<?>> pieces, int max) {
      for (int i = 0; i < Math.min(max, pieces.size()); i++) {
         CosmeticLoadout.Piece<?> piece = (CosmeticLoadout.Piece<?>)pieces.get(i);
         if (i > 0) {
            out.append(';');
         }

         out.append(piece.kind().name())
            .append(',')
            .append(number(piece.size()))
            .append(',')
            .append(number(piece.xAngle()))
            .append(',')
            .append(number(piece.yAngle()))
            .append(',')
            .append(hex(piece.main()))
            .append(',')
            .append(hex(piece.accent()));
      }
   }

   private static String number(float value) {
      String text = String.format(Locale.ROOT, "%.2f", value);
      if (text.contains(".")) {
         text = text.replaceAll("0+$", "");
         if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
         }
      }

      return text;
   }

   private static String hex(int color) {
      return String.format(Locale.ROOT, "%08x", color);
   }

   public static CosmeticLoadout decode(String text, Set<String> minecraftCapes, Set<String> animatedCapes) {
      if (text != null && text.length() <= 4000) {
         String[] parts = text.split("\\|", -1);
         if ((parts.length == 5 || parts.length == 6) && parts[0].equals("v1")) {
            try {
               List<CosmeticLoadout.Piece<CosmeticsModule.Headwear>> headwear = new ArrayList<>();

               for (String entry : split(parts[1], 24)) {
                  String[] f = entry.split(",", -1);
                  CosmeticsModule.Headwear kind = f.length == 6 ? byName(CosmeticsModule.Headwear.class, f[0]) : null;
                  if (f.length != 6) {
                     return null;
                  }

                  if (kind != null && kind != CosmeticsModule.Headwear.NONE && headwear.stream().noneMatch(p -> p.kind() == kind)) {
                     headwear.add(new CosmeticLoadout.Piece<>(kind, size(f[1], 2.5F), angle(f[2]), angle(f[3]), color(f[4]), color(f[5])));
                  }
               }

               List<CosmeticLoadout.Piece<CosmeticsModule.WingType>> wings = new ArrayList<>();

               for (String entry : split(parts[2], 12)) {
                  String[] fx = entry.split(",", -1);
                  CosmeticsModule.WingType kindx = fx.length == 6 ? byName(CosmeticsModule.WingType.class, fx[0]) : null;
                  if (fx.length != 6) {
                     return null;
                  }

                  if (kindx != null && wings.stream().noneMatch(p -> p.kind() == kindx)) {
                     wings.add(new CosmeticLoadout.Piece<>(kindx, size(fx[1], 3.0F), angle(fx[2]), angle(fx[3]), color(fx[4]), color(fx[5])));
                  }
               }

               float flap = clamp(parse(parts[3]), 0.0F, 4.0F);
               CosmeticLoadout.Cape cape = null;
               if (!parts[4].isEmpty()) {
                  String[] fxx = parts[4].split(",", -1);
                  if (fxx.length != 4) {
                     return null;
                  }

                  CosmeticsModule.CapeStyle style = byName(CosmeticsModule.CapeStyle.class, fxx[0]);
                  int color = color(fxx[2]) | 0xFF000000;
                  if ((style != CosmeticsModule.CapeStyle.MINECRAFT || !minecraftCapes.contains(fxx[1]))
                     && (style != CosmeticsModule.CapeStyle.ANIMATED || !animatedCapes.contains(fxx[1]))
                     && style != CosmeticsModule.CapeStyle.COLOR) {
                     if (style == CosmeticsModule.CapeStyle.DRAWN && fxx[3].length() == 1280 && fxx[3].matches("[0-9a-fA-F]+")) {
                        cape = new CosmeticLoadout.Cape(style, "", color, fxx[3]);
                     }
                  } else {
                     cape = new CosmeticLoadout.Cape(style, style == CosmeticsModule.CapeStyle.COLOR ? "" : fxx[1], color, "");
                  }
               }

               List<CosmeticLoadout.Piece<CosmeticsModule.Pet>> pets = new ArrayList<>();

               for (String entry : split(parts.length >= 6 ? parts[5] : "", 6)) {
                  String[] fxxx = entry.split(",", -1);
                  CosmeticsModule.Pet kindxx = fxxx.length == 6 ? byName(CosmeticsModule.Pet.class, fxxx[0]) : null;
                  if (fxxx.length != 6) {
                     return null;
                  }

                  int tint = color(fxxx[4]);
                  int spot = Math.max(0, Math.min(2, color(fxxx[5])));
                  if (kindxx != null && pets.stream().noneMatch(p -> p.kind() == kindxx)) {
                     pets.add(new CosmeticLoadout.Piece<>(kindxx, size(fxxx[1], 2.5F), angle(fxxx[2]), angle(fxxx[3]), tint == 0 ? 0 : 0xFF000000 | tint, spot));
                  }
               }

               String trail = "";

               for (String entry : parts.length == 6 ? parts[5].split(";", -1) : new String[0]) {
                  if (entry.startsWith("TRAIL:") && entry.indexOf(44) > 0) {
                     trail = entry.substring("TRAIL:".length(), entry.indexOf(44));
                  }
               }

               return new CosmeticLoadout(List.copyOf(headwear), List.copyOf(wings), flap, cape, List.copyOf(pets), trail);
            } catch (NumberFormatException var15) {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static List<String> split(String section, int max) {
      List<String> out = new ArrayList<>();
      if (!section.isEmpty()) {
         for (String entry : section.split(";", -1)) {
            if (out.size() < max) {
               out.add(entry);
            }
         }
      }

      return out;
   }

   private static <E extends Enum<E>> E byName(Class<E> type, String name) {
      for (E value : type.getEnumConstants()) {
         if (value.name().equals(name)) {
            return value;
         }
      }

      return null;
   }

   private static float parse(String text) {
      float value = Float.parseFloat(text);
      if (!Float.isFinite(value)) {
         throw new NumberFormatException("not a finite number");
      } else {
         return value;
      }
   }

   private static float size(String text, float max) {
      return clamp(parse(text), 0.4F, max);
   }

   private static float angle(String text) {
      return clamp(parse(text), -180.0F, 180.0F);
   }

   private static int color(String text) {
      if (text.length() != 8) {
         throw new NumberFormatException("not a colour");
      } else {
         return (int)Long.parseLong(text, 16);
      }
   }

   private static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }

   public record Cape(CosmeticsModule.CapeStyle style, String name, int color, String pixels) {
   }

   public record Piece<T extends Enum<T>>(T kind, float size, float xAngle, float yAngle, int main, int accent) {
   }
}
