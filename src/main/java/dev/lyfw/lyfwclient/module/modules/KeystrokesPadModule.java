package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.platform.InputConstants;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.render.KeyCaps;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

public class KeystrokesPadModule extends HudModule {
   private static final int MAX_ROW = 9;
   private static final String[] EXTRA_ROW = new String[]{"F", "R", "C", "2", "M4", "3", "X", "4", "M5"};
   private final SliderSetting keySize = this.register(new SliderSetting("Key Size", 26.0, 14.0, 48.0, 1.0, "px"));
   private final SliderSetting gap = this.register(new SliderSetting("Gap", 4.0, 0.0, 12.0, 1.0, "px"));
   private final TextSetting keys = this.register(new TextSetting("Keys", "M1,W,M2/A,S,D/SHIFT,SPACE"));
   private final SliderSetting columns = this.register(new SliderSetting("Columns", 3.0, 1.0, 9.0, 1.0, ""));
   private final BooleanSetting showExtraRow = this.register(new BooleanSetting("Show Bottom Row", true));
   private final EnumSetting<KeystrokesPadModule.BottomRow> bottomRowMode = this.register(new EnumSetting<>("Bottom Row", KeystrokesPadModule.BottomRow.AUTO));
   private final TextSetting extraRowKeys = this.register(new TextSetting("Bottom Row Keys", "F,R,C,2,M4,3,X,4,M5"));
   private final SliderSetting rounding = this.register(new SliderSetting("Rounding", 5.0, 0.0, 12.0, 1.0, "px"));
   private final SliderSetting pressFade = this.register(new SliderSetting("Press Fade", 150.0, 0.0, 500.0, 10.0, "ms"));
   private final ColorSetting keyColor = this.register(new ColorSetting("Key Color", -266527457));
   private final ColorSetting keyPressedColor = this.register(new ColorSetting("Key Pressed Color", -7554095));
   private final ColorSetting outlineColor = this.register(new ColorSetting("Pressed Outline", -855310));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -4605511));
   private final ColorSetting textPressedColor = this.register(new ColorSetting("Text Pressed Color", -1));
   private final KeyCaps caps = new KeyCaps();

   public KeystrokesPadModule() {
      super("Keystrokes Pad", "Keystrokes laid out as a pad, labelled from your actual bindings.", false, 4.0, 160.0);
      this.keySize.group = "Layout";
      this.gap.group = "Layout";
      this.keys.group = "Layout";
      this.columns.group = "Layout";
      this.rounding.group = "Layout";
      this.showExtraRow.group = "Layout";
      this.bottomRowMode.group = "Layout";
      this.extraRowKeys.group = "Layout";
      this.pressFade.group = "Layout";
      this.keyColor.group = "Colors";
      this.keyPressedColor.group = "Colors";
      this.outlineColor.group = "Colors";
      this.textColor.group = "Colors";
      this.textPressedColor.group = "Colors";
   }

   private KeyCaps.Style style() {
      return new KeyCaps.Style(
         this.keyColor.get(), this.keyPressedColor.get(), this.textColor.get(), this.textPressedColor.get(), this.outlineColor.get(), this.rounding.getInt()
      );
   }

   private int key() {
      return this.keySize.getInt();
   }

   private int gapPx() {
      return this.gap.getInt();
   }

   private int columnCount() {
      return Math.max(1, this.columns.getInt());
   }

   private int leftWidth() {
      return this.key() * this.columnCount() + this.gapPx() * (this.columnCount() - 1);
   }

   private int rowsHeight() {
      int rows = this.rows().size();
      return rows == 0 ? 0 : this.key() * rows + this.gapPx() * (rows - 1);
   }

   private int bottomRowY() {
      int above = this.rowsHeight();
      return above == 0 ? 0 : above + this.gapPx();
   }

   private List<List<String>> rows() {
      String raw = this.keys.get() == null ? "" : this.keys.get().trim();
      List<List<String>> out = new ArrayList<>();
      if (raw.isEmpty()) {
         return out;
      } else {
         for (String line : raw.split("/")) {
            List<String> row = new ArrayList<>();

            for (String cell : line.split(",", -1)) {
               row.add(cell.trim().toUpperCase(Locale.ROOT));
            }

            out.add(row);
         }

         return out;
      }
   }

   @Override
   protected int contentWidth() {
      return this.leftWidth();
   }

   @Override
   protected int contentHeight() {
      return this.showExtraRow.get() ? this.bottomRowY() + this.key() : this.rowsHeight();
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      Options o = mc.options;
      int k = this.key();
      int g = this.gapPx();
      long handle = mc.getWindow().handle();
      this.caps.begin((float)(this.pressFade.get() / 1000.0));
      int total = this.contentWidth();
      List<List<String>> rows = this.rows();

      for (int r = 0; r < rows.size(); r++) {
         List<String> row = rows.get(r);
         int count = row.size();
         if (count > 0) {
            int cell = (total - (count - 1) * g) / count;
            int y = r * (k + g);

            for (int i = 0; i < count; i++) {
               String token = row.get(i);
               if (!token.isEmpty()) {
                  KeystrokesPadModule.RowKey rk = resolve(token, o, handle);
                  this.drawKey(context, "r" + r + "c" + i, i * (cell + g), y, cell, k, rk.label(), rk.pressed());
               }
            }
         }
      }

      if (this.showExtraRow.get()) {
         List<KeystrokesPadModule.RowKey> row = this.bottomRow(o, handle);
         int count = row.size();
         if (count > 0) {
            int cell = (total - (count - 1) * g) / count;
            int y = this.bottomRowY();

            for (int ix = 0; ix < count; ix++) {
               KeystrokesPadModule.RowKey rk = row.get(ix);
               this.drawKey(context, "row" + ix, ix * (cell + g), y, cell, k, rk.label(), rk.pressed());
            }
         }
      }
   }

   private static KeystrokesPadModule.RowKey resolve(String token, Options o, long handle) {
      KeyMapping bound = switch (token) {
         case "M1", "LMB" -> o.keyAttack;
         case "M2", "RMB" -> o.keyUse;
         case "W" -> o.keyUp;
         case "A" -> o.keyLeft;
         case "S" -> o.keyDown;
         case "D" -> o.keyRight;
         case "SHIFT" -> o.keyShift;
         case "SPACE" -> o.keyJump;
         case "CTRL" -> o.keySprint;
         default -> null;
      };
      return bound == null ? new KeystrokesPadModule.RowKey(token, isDown(handle, token)) : new KeystrokesPadModule.RowKey(label(bound, token), bound.isDown());
   }

   private List<KeystrokesPadModule.RowKey> bottomRow(Options o, long handle) {
      List<KeystrokesPadModule.RowKey> out = new ArrayList<>();
      if (this.bottomRowMode.get() != KeystrokesPadModule.BottomRow.CUSTOM) {
         for (KeyMapping binding : o.keyHotbarSlots) {
            if (!binding.isUnbound() && out.size() < 9) {
               out.add(new KeystrokesPadModule.RowKey(label(binding, "?"), binding.isDown()));
            }
         }

         for (KeyMapping bindingx : new KeyMapping[]{o.keyDrop, o.keySwapOffhand, o.keySprint}) {
            if (!bindingx.isUnbound() && out.size() < 9) {
               out.add(new KeystrokesPadModule.RowKey(label(bindingx, "?"), bindingx.isDown()));
            }
         }

         return out;
      } else {
         for (String key : this.extraRow()) {
            out.add(new KeystrokesPadModule.RowKey(key, isDown(handle, key)));
         }

         return out;
      }
   }

   private static String label(KeyMapping binding, String fallback) {
      try {
         String bound = binding.getTranslatedKeyMessage().getString().toUpperCase(Locale.ROOT);
         return !bound.isEmpty() && bound.length() <= 5 ? bound : fallback;
      } catch (Exception var3) {
         return fallback;
      }
   }

   private String[] extraRow() {
      String raw = this.extraRowKeys.get() == null ? "" : this.extraRowKeys.get().trim();
      if (raw.isEmpty()) {
         return EXTRA_ROW;
      } else {
         String[] parts = raw.split(",");
         List<String> out = new ArrayList<>();

         for (String part : parts) {
            String key = part.trim().toUpperCase(Locale.ROOT);
            if (!key.isEmpty() && key.length() <= 3) {
               out.add(key);
            }
         }

         return out.isEmpty() ? EXTRA_ROW : out.toArray(new String[0]);
      }
   }

   private static boolean isDown(long handle, String label) {
      if (label.startsWith("M")) {
         int button = switch (label) {
            case "M1" -> 0;
            case "M2" -> 1;
            case "M3" -> 2;
            case "M4" -> 3;
            case "M5" -> 4;
            default -> -1;
         };
         return button >= 0 && GLFW.glfwGetMouseButton(handle, button) == 1;
      } else {
         int code = label.length() == 1 ? label.charAt(0) : -1;
         return code >= 0 && InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), code);
      }
   }

   private void drawKey(GuiGraphics context, String id, int x, int y, int w, int h, String label, boolean pressed) {
      this.caps.draw(context, id, x, y, w, h, label, pressed, this.style());
   }

   public static enum BottomRow {
      AUTO,
      CUSTOM;
   }

   private record RowKey(String label, boolean pressed) {
   }
}
