package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.render.KeyCaps;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.Locale;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;

public class KeystrokesModule extends HudModule {
   private static final int SIZE = 18;
   private static final int GAP = 2;
   private final BooleanSetting showMovement = this.register(new BooleanSetting("Show WASD", true));
   private final BooleanSetting showMouse = this.register(new BooleanSetting("Show Mouse", true));
   private final BooleanSetting showSneakJump = this.register(new BooleanSetting("Show Shift + Space", true));
   private final BooleanSetting showHotbar = this.register(new BooleanSetting("Show Hotbar", false));
   private final SliderSetting rounding = this.register(new SliderSetting("Rounding", 4.0, 0.0, 9.0, 1.0, "px"));
   private final SliderSetting pressFade = this.register(new SliderSetting("Press Fade", 150.0, 0.0, 500.0, 10.0, "ms"));
   private final ColorSetting activeColor = this.register(new ColorSetting("Active Color", -7554095));
   private final ColorSetting inactiveColor = this.register(new ColorSetting("Inactive Color", -266527457));
   private final ColorSetting outlineColor = this.register(new ColorSetting("Pressed Outline", -855310));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -4605511));
   private final ColorSetting textPressedColor = this.register(new ColorSetting("Text Pressed Color", -1));
   private final KeyCaps caps = new KeyCaps();

   public KeystrokesModule() {
      super("Keystrokes", "Shows movement/mouse/hotbar input; each row can be toggled independently.", false, 4.0, 100.0);
      this.showMovement.group = "Layout";
      this.showMouse.group = "Layout";
      this.showSneakJump.group = "Layout";
      this.showHotbar.group = "Layout";
      this.rounding.group = "Layout";
      this.pressFade.group = "Layout";
      this.activeColor.group = "Colors";
      this.inactiveColor.group = "Colors";
      this.outlineColor.group = "Colors";
      this.textColor.group = "Colors";
      this.textPressedColor.group = "Colors";
   }

   private KeyCaps.Style style() {
      return new KeyCaps.Style(
         this.inactiveColor.get(), this.activeColor.get(), this.textColor.get(), this.textPressedColor.get(), this.outlineColor.get(), this.rounding.getInt()
      );
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      Options o = mc.options;
      this.caps.begin((float)(this.pressFade.get() / 1000.0));
      int step = 20;
      int y = 0;
      if (this.showMouse.get() || this.showMovement.get()) {
         if (this.showMouse.get()) {
            this.drawKey(context, "m1", 0, y, 18, 18, label(o.keyAttack, "M1"), o.keyAttack.isDown());
            this.drawKey(context, "m2", step * 2, y, 18, 18, label(o.keyUse, "M2"), o.keyUse.isDown());
         }

         if (this.showMovement.get()) {
            this.drawKey(context, "w", step, y, 18, 18, label(o.keyUp, "W"), o.keyUp.isDown());
         }

         y += step;
      }

      if (this.showMovement.get()) {
         this.drawKey(context, "a", 0, y, 18, 18, label(o.keyLeft, "A"), o.keyLeft.isDown());
         this.drawKey(context, "s", step, y, 18, 18, label(o.keyDown, "S"), o.keyDown.isDown());
         this.drawKey(context, "d", step * 2, y, 18, 18, label(o.keyRight, "D"), o.keyRight.isDown());
         y += step;
      }

      if (this.showSneakJump.get()) {
         int wide = (this.rowWidth() - 2) / 2;
         this.drawKey(context, "shift", 0, y, wide, 18, label(o.keyShift, "SHIFT"), o.keyShift.isDown());
         this.drawKey(context, "space", wide + 2, y, this.rowWidth() - wide - 2, 18, label(o.keyJump, "SPACE"), o.keyJump.isDown());
         y += step;
      }

      if (this.showHotbar.get() && mc.player != null) {
         int selected = mc.player.getInventory().getSelectedSlot();

         for (int i = 0; i < o.keyHotbarSlots.length && i < 9; i++) {
            boolean active = o.keyHotbarSlots[i].isDown() || i == selected;
            this.drawKey(context, "hotbar" + i, i * 18, y, 16, 16, label(o.keyHotbarSlots[i], String.valueOf(i + 1)), active);
         }
      }
   }

   private void drawKey(GuiGraphics context, String id, int x, int y, int w, int h, String label, boolean pressed) {
      this.caps.draw(context, id, x, y, w, h, label, pressed, this.style());
   }

   private static String label(KeyMapping binding, String fallback) {
      try {
         String translationKey = binding.saveString();
         if (translationKey.startsWith("key.mouse.")) {
            String suffix = translationKey.substring("key.mouse.".length());

            return switch (suffix) {
               case "left" -> "M1";
               case "right" -> "M2";
               case "middle" -> "M3";
               default -> "M" + suffix;
            };
         } else {
            String bound = binding.getTranslatedKeyMessage().getString().toUpperCase(Locale.ROOT);
            return !bound.isEmpty() && bound.length() <= 5 ? bound : fallback;
         }
      } catch (Exception var6) {
         return fallback;
      }
   }

   private int rowWidth() {
      return 58;
   }

   @Override
   protected int contentWidth() {
      return this.showHotbar.get() ? 160 : this.rowWidth();
   }

   @Override
   protected int contentHeight() {
      int step = 20;
      int h = 0;
      if (this.showMouse.get() || this.showMovement.get()) {
         h += step;
      }

      if (this.showMovement.get()) {
         h += step;
      }

      if (this.showSneakJump.get()) {
         h += step;
      }

      if (this.showHotbar.get()) {
         h += 16;
      } else if (h > 0) {
         h -= 2;
      }

      return Math.max(h, 18);
   }
}
