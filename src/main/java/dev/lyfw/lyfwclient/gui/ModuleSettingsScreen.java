package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.modules.BlockColorsModule;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import dev.lyfw.lyfwclient.module.modules.CustomCrosshairModule;
import dev.lyfw.lyfwclient.module.modules.NameProtectModule;
import dev.lyfw.lyfwclient.render.BlockSaturation;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ChoiceSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.KeybindSetting;
import dev.lyfw.lyfwclient.setting.Setting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ModuleSettingsScreen extends Screen {
   private static final int WIDTH = 300;
   private static final int HEADER_H = 38;
   private static final int PREVIEW_H = 56;
   private static final int ROW_H = 22;
   private static final int SECTION_H = 20;
   private static final int BOTTOM_PAD = 10;
   private static final int SCREEN_MARGIN = 80;
   private static final int CLOSE_BTN = 18;
   private static final int CORNER_RADIUS = 10;
   private static final int SMALL_RADIUS = 3;
   private static final int COLOR_POPUP_WIDTH = 130;
   private static final int MAX_TEXT_LENGTH = 512;
   private final Screen parent;
   private final Module module;
   private int columnX;
   private int columnY;
   private int panelH;
   private int bodyTop;
   private int bodyBottom;
   private int closeX;
   private int closeY;
   private int resetX;
   private int resetW;
   private final Scroll scrollOffset = new Scroll();
   private static final String GENERAL_TAB = "General";
   private int selectedTab;
   private SliderSetting draggingSlider;
   private ColorSetting openColorSetting;
   private int popupX;
   private int popupY;
   private int popupRestY;
   private float colorWheelHue;
   private float colorWheelSat;
   private float colorWheelVal;
   private boolean colorPopupSliderMode;
   private boolean draggingColorWheel;
   private boolean draggingColorValue;
   private boolean draggingColorAlpha;
   private int draggingColorChannel = -1;
   private TextSetting editingText;
   private StringBuilder editBuffer;
   private KeybindSetting listeningKeybind;
   private boolean paintingGrid;
   private boolean paintValue;
   private final Anim open = new Anim(0.2F);
   private final Anim popup = new Anim(0.14F);
   private static final int PANEL_DROP = 22;
   private static final int POPUP_DROP = 10;
   private static final int PIXEL_CELL = 12;
   private static final int WHEEL_RADIUS = 34;
   private static final int WHEEL_BLOCK = 3;
   private static final int WHEEL_PADDING = 8;
   private static final int VALUE_SLIDER_WIDTH = 12;
   private static final int VALUE_SLIDER_GAP = 8;
   private static final int MARKER_SIZE = 5;
   private static final int TAB_AREA_HEIGHT = 24;
   private static final int TAB_HEIGHT = 14;
   private static final int TAB_GAP = 4;
   private static final int POPUP_HEADER_HEIGHT = 24;
   private static final String[] CHANNEL_LABELS = new String[]{"R", "G", "B", "A"};
   private static final int GRADIENT_BAND = 4;
   private static final int CAPE_CELL = 9;

   public ModuleSettingsScreen(Screen parent, Module module) {
      super(Component.literal(module.name + " Settings"));
      this.parent = parent;
      this.module = module;
   }

   private boolean hasPreview() {
      return this.module instanceof CustomCrosshairModule;
   }

   private List<String> tabs() {
      List<String> out = new ArrayList<>();
      boolean ungrouped = this.module instanceof HudModule;

      for (Setting<?> setting : this.module.getSettings()) {
         if (this.rowTypeFor(setting) != null) {
            if (setting.group == null) {
               ungrouped = true;
            } else if (!out.contains(setting.group)) {
               out.add(setting.group);
            }
         }
      }

      if (ungrouped && !out.isEmpty()) {
         out.add(0, "General");
      }

      return out;
   }

   private boolean hasTabs() {
      return this.tabs().size() >= 2;
   }

   private String tabOf(Setting<?> setting) {
      return setting.group == null ? "General" : setting.group;
   }

   private String currentTab() {
      List<String> tabs = this.tabs();
      if (tabs.isEmpty()) {
         return null;
      } else {
         return this.selectedTab >= 0 && this.selectedTab < tabs.size() ? tabs.get(this.selectedTab) : tabs.get(0);
      }
   }

   private List<ModuleSettingsScreen.Row> buildRows() {
      List<ModuleSettingsScreen.Row> rows = new ArrayList<>();
      int y = 0;
      String lastGroup = null;
      boolean tabbed = this.hasTabs();
      String tab = this.currentTab();

      for (Setting<?> setting : this.module.getSettings()) {
         if (!(this.module instanceof HudModule hud && (setting == hud.x || setting == hud.y))) {
            ModuleSettingsScreen.RowType type = this.rowTypeFor(setting);
            if (type != null) {
               if (tabbed) {
                  if (!this.tabOf(setting).equals(tab)) {
                     continue;
                  }
               } else if (setting.group != null && !setting.group.equals(lastGroup)) {
                  rows.add(new ModuleSettingsScreen.Row(ModuleSettingsScreen.RowType.SECTION_HEADER, y, 20, null, setting.group));
                  y += 20;
               }

               lastGroup = setting.group;
               int h = this.rowHeightFor(type);
               rows.add(new ModuleSettingsScreen.Row(type, y, h, setting, null));
               y += h;
            }
         } else if (setting == hud.x && (!tabbed || "General".equals(tab))) {
            rows.add(new ModuleSettingsScreen.Row(ModuleSettingsScreen.RowType.GUI_MOVER, y, 22, null, "Position"));
            y += 22;
         }
      }

      return rows;
   }

   private int tabRowHeight() {
      return this.hasTabs() ? 26 : 0;
   }

   private int[] tabBounds(int index) {
      List<String> tabs = this.tabs();
      int gap = 4;
      int usable = 276 - (tabs.size() - 1) * gap;
      int w = Math.max(30, usable / Math.max(1, tabs.size()));
      return new int[]{this.columnX + 12 + index * (w + gap), this.columnY + 38 + (this.hasPreview() ? 56 : 0) + 3, w, 18};
   }

   private void renderTabs(GuiGraphics context, int mouseX, int mouseY) {
      List<String> tabs = this.tabs();
      String current = this.currentTab();

      for (int i = 0; i < tabs.size(); i++) {
         int[] b = this.tabBounds(i);
         boolean active = tabs.get(i).equals(current);
         boolean hovered = !active && isInside(mouseX, mouseY, b);
         ThemeRenderer.tab(context, b[0], b[1], b[2], b[3], active, hovered, Theme.accent(), Theme.trackBg(), Theme.rowBgHover(), 5);
         String label = this.font.plainSubstrByWidth(tabs.get(i), b[2] - 6);
         int lw = this.font.width(label);
         int color = active ? ThemeRenderer.onAccent() : ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary());
         context.drawString(this.font, label, b[0] + (b[2] - lw) / 2, b[1] + 5, color, !active);
      }
   }

   private int rowHeightFor(ModuleSettingsScreen.RowType type) {
      if (type == ModuleSettingsScreen.RowType.CAPE_GRID) {
         return 168;
      } else {
         return type == ModuleSettingsScreen.RowType.PIXEL_GRID ? this.pixelGridRowHeight() : 22;
      }
   }

   private int pixelGridRowHeight() {
      return 204;
   }

   private ModuleSettingsScreen.RowType rowTypeFor(Setting<?> setting) {
      if (this.module instanceof CustomCrosshairModule cc && setting == cc.customPixelsSetting()) {
         return cc.shapeSetting().get() == CustomCrosshairModule.CrosshairShape.CUSTOM ? ModuleSettingsScreen.RowType.PIXEL_GRID : null;
      } else if (this.module instanceof NameProtectModule nameProtect && setting == nameProtect.skinPlayerSetting()) {
         return null;
      } else if (this.module instanceof CosmeticsModule cosmetics && !cosmetics.showsSetting(setting)) {
         return null;
      } else if (this.module instanceof CosmeticsModule cosmetics && setting == cosmetics.capePixelsSetting()) {
         return ModuleSettingsScreen.RowType.CAPE_GRID;
      } else if (setting instanceof BooleanSetting) {
         return ModuleSettingsScreen.RowType.SETTING_BOOL;
      } else if (setting instanceof SliderSetting) {
         return ModuleSettingsScreen.RowType.SETTING_SLIDER;
      } else if (setting instanceof ColorSetting) {
         return ModuleSettingsScreen.RowType.SETTING_COLOR;
      } else if (setting instanceof EnumSetting || setting instanceof ChoiceSetting) {
         return ModuleSettingsScreen.RowType.SETTING_ENUM;
      } else if (setting instanceof TextSetting) {
         return ModuleSettingsScreen.RowType.SETTING_TEXT;
      } else {
         return setting instanceof KeybindSetting ? ModuleSettingsScreen.RowType.SETTING_KEYBIND : null;
      }
   }

   private int contentHeight(List<ModuleSettingsScreen.Row> rows) {
      if (rows.isEmpty()) {
         return 0;
      } else {
         ModuleSettingsScreen.Row last = rows.get(rows.size() - 1);
         return last.y() + last.h();
      }
   }

   private int maxScroll(List<ModuleSettingsScreen.Row> rows) {
      return Math.max(0, this.contentHeight(rows) - (this.bodyBottom - this.bodyTop));
   }

   private ModuleSettingsScreen.Row findSettingRow(List<ModuleSettingsScreen.Row> rows, Setting<?> setting) {
      for (ModuleSettingsScreen.Row row : rows) {
         if (row.setting() == setting) {
            return row;
         }
      }

      return null;
   }

   private ModuleSettingsScreen.Row findPixelGridRow(List<ModuleSettingsScreen.Row> rows) {
      for (ModuleSettingsScreen.Row row : rows) {
         if (row.type() == ModuleSettingsScreen.RowType.PIXEL_GRID) {
            return row;
         }
      }

      return null;
   }

   private void computeLayout() {
      List<ModuleSettingsScreen.Row> rows = this.buildRows();
      int previewH = this.hasPreview() ? 56 : 0;
      int contentH = this.contentHeight(rows);
      int maxBodyH = Math.max(60, this.height - 80 - 38 - previewH - this.tabRowHeight() - 10);
      int bodyH = Math.min(Math.max(contentH, 1), maxBodyH);
      if (contentH == 0) {
         bodyH = 0;
      }

      int tabH = this.tabRowHeight();
      this.panelH = 38 + previewH + tabH + bodyH + 10;
      this.columnX = (this.width - 300) / 2;
      this.columnY = (this.height - this.panelH) / 2 - this.open.drop(22);
      this.bodyTop = this.columnY + 38 + previewH + tabH;
      this.bodyBottom = this.bodyTop + bodyH;
      this.closeX = this.columnX + 300 - 18 - 10;
      this.closeY = this.columnY + 10;
      this.resetW = this.font.width("Reset") + 12;
      this.resetX = this.closeX - 6 - this.resetW;
      this.popupY = this.popupRestY - this.popup.drop(10);
      this.scrollOffset.setTarget(Mth.clamp(this.scrollOffset.target(), 0, this.maxScroll(rows)));
   }

   private boolean setPixelAt(CustomCrosshairModule cc, int mx, int my, boolean value) {
      List<ModuleSettingsScreen.Row> rows = this.buildRows();
      ModuleSettingsScreen.Row row = this.findPixelGridRow(rows);
      if (row == null) {
         return false;
      } else {
         int screenY = this.bodyTop + (row.y() - this.scrollOffset.shown());
         int grid = 15;
         int gridW = grid * 12;
         int gx = this.columnX + (300 - gridW) / 2;
         int gy = this.gridTop(screenY);
         if (mx >= gx && mx < gx + gridW && my >= gy && my < gy + gridW) {
            int px = (mx - gx) / 12;
            int py = (my - gy) / 12;
            String bits = cc.customPixelsSetting().get();
            if (bits.length() != grid * grid) {
               bits = CustomCrosshairModule.blankGrid();
            }

            int idx = py * grid + px;
            char[] chars = bits.toCharArray();
            chars[idx] = (char)(value ? 49 : 48);
            cc.customPixelsSetting().set(new String(chars));
            return true;
         } else {
            return false;
         }
      }
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      fillRounded(context, this.columnX + 3, this.columnY + 5, 300, this.panelH, Theme.shadow(), 10);
      fillRoundedBorder(context, this.columnX, this.columnY, 300, this.panelH, Theme.modalBorder(), Theme.modalPanelBg(), 10);
      fillRoundedTop(context, this.columnX + 1, this.columnY + 1, 298, 37, Theme.modalHeaderBg(), 9);
      context.drawString(this.font, this.title, this.columnX + 12, this.columnY + 15, Theme.textPrimary());
      if (!this.module.getSettings().isEmpty()) {
         boolean resetHovered = isInside(mouseX, mouseY, this.resetX, this.columnY + 8, this.resetW, 22);
         fillRounded(context, this.resetX, this.columnY + 8, this.resetW, 22, resetHovered ? Theme.accentRowBg() : 0, 3);
         int rw = this.font.width("Reset");
         context.drawString(this.font, "Reset", this.resetX + (this.resetW - rw) / 2, this.columnY + 15, resetHovered ? Theme.accent() : Theme.textSecondary());
      }

      boolean closeHovered = isInside(mouseX, mouseY, this.closeX, this.closeY, 18, 18);
      fillRounded(context, this.closeX, this.closeY, 18, 18, closeHovered ? Theme.rowBgHover() : Theme.closeBg(), 5);
      context.drawCenteredString(this.font, "x", this.closeX + 9, this.closeY + 5, Theme.textMuted());
      if (this.hasPreview()) {
         this.renderPreview(context);
      }

      if (this.hasTabs()) {
         this.renderTabs(context, mouseX, mouseY);
      }

      List<ModuleSettingsScreen.Row> rows = this.buildRows();
      if (rows.isEmpty()) {
         context.drawString(this.font, "No settings for this module.", this.columnX + 12, this.bodyTop + 6, Theme.textSecondary());
      } else {
         context.enableScissor(this.columnX, this.bodyTop, this.columnX + 300, this.bodyBottom);

         for (ModuleSettingsScreen.Row row : rows) {
            int screenY = this.bodyTop + (row.y() - this.scrollOffset.shown());
            if (screenY + row.h() >= this.bodyTop && screenY <= this.bodyBottom) {
               this.renderRow(context, row, screenY, mouseX, mouseY);
            }
         }

         context.disableScissor();
         int maxScroll = this.maxScroll(rows);
         if (maxScroll > 0) {
            this.renderScrollbar(context, maxScroll);
         }
      }

      if (this.openColorSetting != null) {
         this.renderColorPopup(context, this.openColorSetting, mouseX, mouseY);
      }
   }

   private void renderScrollbar(GuiGraphics context, int maxScroll) {
      int trackX = this.columnX + 300 - 7;
      int trackH = this.bodyBottom - this.bodyTop;
      fillRounded(context, trackX, this.bodyTop, 3, trackH, Theme.trackBg(), 1);
      int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
      int handleY = this.bodyTop + (int)((trackH - handleH) * ((float)this.scrollOffset.shown() / maxScroll));
      fillRounded(context, trackX, handleY, 3, handleH, Theme.accent(), 1);
   }

   private void renderPreview(GuiGraphics context) {
      int top = this.columnY + 38;
      fillRounded(context, this.columnX + 1, top, 298, 56, Theme.panelBg(), 0);
      if (this.module instanceof CustomCrosshairModule crosshair) {
         crosshair.renderPreview(context, this.columnX + 150, top + 28);
      }
   }

   private void renderRow(GuiGraphics context, ModuleSettingsScreen.Row row, int screenY, int mouseX, int mouseY) {
      int x = this.columnX;
      int w = 300;
      boolean hovered = row.type() != ModuleSettingsScreen.RowType.SECTION_HEADER
         && mouseX >= x
         && mouseX < x + w
         && mouseY >= screenY
         && mouseY < screenY + row.h()
         && this.openColorSetting == null;
      if (hovered) {
         fillRounded(context, x + 4, screenY, w - 8, row.h(), Theme.hoverBg(), 3);
      }

      switch (row.type()) {
         case SECTION_HEADER:
            int dividerY = screenY + row.h() - 6;
            context.fill(x + 12, dividerY, x + w - 12, dividerY + 1, Theme.sectionDivider());
            context.drawString(this.font, row.label(), x + 12, screenY + 4, Theme.sectionText());
            break;
         case SETTING_BOOL:
            BooleanSetting b = (BooleanSetting)row.setting();
            context.drawString(this.font, b.name, x + 14, screenY + (row.h() - 8) / 2, Theme.textMuted());
            if (this.module instanceof NameProtectModule nameProtect && b == nameProtect.changeSkinSetting()) {
               this.renderRandomSkinButton(context, nameProtect, b, screenY, row.h(), mouseX, mouseY);
            }

            int pillW = 26;
            int pillH = 13;
            int pillX = x + w - pillW - 12;
            int pillY = screenY + (row.h() - pillH) / 2;
            if (b.get()) {
               fillGradientRounded(context, pillX, pillY, pillW, pillH, 6, Theme.accent(), Theme.accent2());
            } else {
               fillRounded(context, pillX, pillY, pillW, pillH, Theme.trackBg(), 6);
            }

            int knobD = 9;
            int knobX = b.get() ? pillX + pillW - knobD - 2 : pillX + 2;
            fillRounded(context, knobX, pillY + 2, knobD, knobD, b.get() ? Theme.knob() : Theme.textSecondary(), 4);
            break;
         case SETTING_SLIDER:
            SliderSetting s = (SliderSetting)row.setting();
            int trackX = x + 12;
            int trackW = w - 24;
            int trackH = 3;
            int trackY = screenY + row.h() - trackH - 4;
            int fillW = (int)(trackW * Mth.clamp(s.getFraction(), 0.0, 1.0));
            fillRounded(context, trackX, trackY, trackW, trackH, Theme.trackBg(), 1);
            if (fillW > 0) {
               fillRounded(context, trackX, trackY, fillW, trackH, Theme.accent(), 1);
            }

            context.drawString(this.font, s.name + ": " + s.display(), x + 14, screenY + 4, Theme.textMuted());
            break;
         case SETTING_COLOR:
            ColorSetting c = (ColorSetting)row.setting();
            context.drawString(this.font, c.name, x + 14, screenY + (row.h() - 8) / 2, Theme.textMuted());
            int boxW = 22;
            int boxH = 12;
            int boxX = x + w - boxW - 10;
            int boxY = screenY + (row.h() - boxH) / 2;
            fillRoundedBorder(context, boxX, boxY, boxW, boxH, Theme.modalBorder(), 0xFF000000 | c.rgb(), 3);
            break;
         case SETTING_ENUM: {
            String label = row.setting().name;
            String value = row.setting() instanceof ChoiceSetting pick
               ? pick.get()
               : (
                  this.module instanceof CosmeticsModule cosmetics && row.setting() == cosmetics.capeStyleSetting()
                     ? cosmetics.capeStyleLabel()
                     : ((Enum)((EnumSetting)row.setting()).get()).name()
               );
            context.drawString(this.font, label, x + 14, screenY + (row.h() - 8) / 2, Theme.textMuted());
            value = this.font.plainSubstrByWidth(value, Math.max(40, w - 30 - this.font.width(label)));
            int valueWidth = this.font.width(value);
            context.drawString(this.font, value, x + w - 12 - valueWidth, screenY + (row.h() - 8) / 2, Theme.accent());
            break;
         }
         case SETTING_TEXT:
            TextSetting t = (TextSetting)row.setting();
            if (this.module instanceof BlockColorsModule colors && t == colors.desaturatedSetting()) {
               int blocks = BlockSaturation.count(t.get());
               String summary = "Pick blocks to grey out: " + (blocks == 0 ? "none yet" : blocks + (blocks == 1 ? " block" : " blocks")) + "  ›";
               context.drawString(this.font, this.font.plainSubstrByWidth(summary, w - 24), x + 14, screenY + (row.h() - 8) / 2, Theme.accent());
            } else {
               boolean editing = this.editingText == t;
               String valuex = editing ? this.editBuffer.toString() : t.get();
               if (editing && System.currentTimeMillis() / 500L % 2L == 0L) {
                  valuex = valuex + "_";
               }

               int colorx = editing ? Theme.textPrimary() : Theme.accent();
               int textY = screenY + (row.h() - 8) / 2;
               String labelx = t.name + ": ";
               int hintRoom = editing ? this.font.width("ctrl+V") + 8 : 0;
               int room = w - 14 - 10 - hintRoom - this.font.width(labelx);
               String shown = editing ? trimToEnd(this.font, valuex, room) : this.font.plainSubstrByWidth(valuex, room);
               context.drawString(this.font, labelx + shown, x + 14, textY, colorx);
               if (editing) {
                  context.drawString(this.font, "ctrl+V", x + w - 10 - this.font.width("ctrl+V"), textY, Theme.textMuted());
               }
            }
            break;
         case SETTING_KEYBIND: {
            KeybindSetting k = (KeybindSetting)row.setting();
            boolean listening = this.listeningKeybind == k;
            context.drawString(this.font, k.name, x + 14, screenY + (row.h() - 8) / 2, Theme.textMuted());
            String value = listening ? "> Press a key <" : k.display();
            int color = listening ? Theme.textPrimary() : (k.isUnbound() ? Theme.textMuted() : Theme.accent());
            int valueWidth = this.font.width(value);
            context.drawString(this.font, value, x + w - 12 - valueWidth, screenY + (row.h() - 8) / 2, color);
            break;
         }
         case PIXEL_GRID:
            this.renderPixelGridRow(context, row, screenY, mouseX, mouseY);
            break;
         case CAPE_GRID:
            this.renderCapeGridRow(context, screenY, mouseX, mouseY);
            break;
         case GUI_MOVER: {
            int[] mover = this.moverButtonBounds(screenY);
            boolean moverHovered = isInside(mouseX, mouseY, mover);
            ThemeRenderer.row(context, mover[0], mover[1], mover[2], mover[3], moverHovered, Theme.trackBg(), Theme.rowBgHover(), 5);
            String label = "GUI Mover";
            int lw = this.font.width(label);
            context.drawString(
               this.font,
               label,
               mover[0] + (mover[2] - lw) / 2,
               mover[1] + 4,
               ThemeRenderer.rowTextColor(moverHovered, Theme.textSecondary(), Theme.textPrimary())
            );
         }
      }
   }

   private int[] randomSkinBounds(int screenY, int rowH) {
      int w = this.font.width("Random") + 12;
      int h = 14;
      int pillX = this.columnX + 300 - 26 - 12;
      return new int[]{pillX - 6 - w, screenY + (rowH - h) / 2, w, h};
   }

   private void renderRandomSkinButton(
      GuiGraphics context, NameProtectModule nameProtect, BooleanSetting setting, int screenY, int rowH, int mouseX, int mouseY
   ) {
      int[] button = this.randomSkinBounds(screenY, rowH);
      boolean hovered = isInside(mouseX, mouseY, button) && this.openColorSetting == null;
      ThemeRenderer.row(context, button[0], button[1], button[2], button[3], hovered, Theme.trackBg(), Theme.rowBgHover(), 4);
      int lw = this.font.width("Random");
      context.drawString(
         this.font, "Random", button[0] + (button[2] - lw) / 2, button[1] + 3, ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary())
      );
      int left = this.columnX + 14 + this.font.width(setting.name) + 6;
      String who = this.font.plainSubstrByWidth(nameProtect.skinLabel(), Math.max(0, button[0] - 6 - left));
      context.drawString(this.font, who, left, screenY + (rowH - 8) / 2, Theme.accent());
   }

   private void renderPixelGridRow(GuiGraphics context, ModuleSettingsScreen.Row row, int screenY, int mouseX, int mouseY) {
      CustomCrosshairModule cc = (CustomCrosshairModule)this.module;
      int x = this.columnX;
      String label = "Draw Crosshair (L: draw, R: erase)";
      context.drawString(this.font, label, x + 14, screenY + 4, Theme.textMuted());
      String clearLabel = "Clear";
      int[] clearBounds = this.clearButtonBounds(screenY);
      boolean clearHovered = isInside(mouseX, mouseY, clearBounds);
      context.drawString(this.font, clearLabel, clearBounds[0], clearBounds[1], clearHovered ? Theme.textPrimary() : Theme.accent());
      int grid = 15;
      int gridW = grid * 12;
      int gx = x + (300 - gridW) / 2;
      int gy = this.gridTop(screenY);
      fillRoundedBorder(context, gx - 1, gy - 1, gridW + 2, gridW + 2, Theme.modalBorder(), Theme.trackBg(), 3);
      String bits = cc.customPixelsSetting().get();

      for (int py = 0; py < grid; py++) {
         for (int px = 0; px < grid; px++) {
            int idx = py * grid + px;
            int cx = gx + px * 12;
            int cy = gy + py * 12;
            if (idx < bits.length() && bits.charAt(idx) == '1') {
               context.fill(cx, cy, cx + 12 - 1, cy + 12 - 1, -1);
            } else if (mouseX >= cx && mouseX < cx + 12 - 1 && mouseY >= cy && mouseY < cy + 12 - 1) {
               context.fill(cx, cy, cx + 12 - 1, cy + 12 - 1, Theme.hoverBg());
            }
         }
      }
   }

   private void renderCapeGridRow(GuiGraphics context, int screenY, int mouseX, int mouseY) {
      CosmeticsModule cosmetics = (CosmeticsModule)this.module;
      int x = this.columnX;
      context.drawString(this.font, "Draw Cape (L: paint, R: erase)", x + 14, screenY + 4, Theme.textMuted());
      int[] clearBounds = this.clearButtonBounds(screenY);
      boolean clearHovered = isInside(mouseX, mouseY, clearBounds);
      context.drawString(this.font, "Clear", clearBounds[0], clearBounds[1], clearHovered ? Theme.textPrimary() : Theme.accent());
      int[] grid = this.capeGridBounds(screenY);
      int columns = 10;
      int rows = 16;
      fillRoundedBorder(context, grid[0] - 1, grid[1] - 1, grid[2] + 2, grid[3] + 2, Theme.modalBorder(), Theme.trackBg(), 3);
      int base = 0xFF000000 | cosmetics.capeColor() & 16777215;

      for (int py = 0; py < rows; py++) {
         for (int px = 0; px < columns; px++) {
            int cx = grid[0] + px * 9;
            int cy = grid[1] + py * 9;
            int texel = cosmetics.capePixel(px, py);
            context.fill(cx, cy, cx + 9 - 1, cy + 9 - 1, texel >>> 24 != 0 ? 0xFF000000 | texel & 16777215 : base);
            if (mouseX >= cx && mouseX < cx + 9 - 1 && mouseY >= cy && mouseY < cy + 9 - 1) {
               context.fill(cx, cy, cx + 9 - 1, cy + 9 - 1, Theme.hoverBg());
            }
         }
      }
   }

   private int[] capeGridBounds(int screenY) {
      int w = 90;
      int h = 144;
      return new int[]{this.columnX + (300 - w) / 2, screenY + 16, w, h};
   }

   private boolean setCapePixelAt(CosmeticsModule cosmetics, int mx, int my, boolean paint) {
      for (ModuleSettingsScreen.Row row : this.buildRows()) {
         if (row.type() == ModuleSettingsScreen.RowType.CAPE_GRID) {
            int[] grid = this.capeGridBounds(this.bodyTop + (row.y() - this.scrollOffset.shown()));
            if (my >= this.bodyTop && my < this.bodyBottom && isInside(mx, my, grid)) {
               int px = (mx - grid[0]) / 9;
               int py = (my - grid[1]) / 9;
               cosmetics.setCapePixel(px, py, paint ? 0xFF000000 | cosmetics.paintColorSetting().get() & 16777215 : 0);
               return true;
            }
         }
      }

      return false;
   }

   private int gridTop(int screenY) {
      return screenY + 16;
   }

   private int[] moverButtonBounds(int screenY) {
      return new int[]{this.columnX + 12, screenY + 2, 276, 18};
   }

   private int[] clearButtonBounds(int screenY) {
      String clearLabel = "Clear";
      int clearW = this.font.width(clearLabel);
      return new int[]{this.columnX + 300 - clearW - 12, screenY + 4, clearW, 10};
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)click.x();
      int my = (int)click.y();
      if (click.button() == 1) {
         if (this.module instanceof CustomCrosshairModule cc && this.setPixelAt(cc, mx, my, false)) {
            this.paintingGrid = true;
            this.paintValue = false;
            return true;
         } else if (this.module instanceof CosmeticsModule cosmetics && this.setCapePixelAt(cosmetics, mx, my, false)) {
            this.paintingGrid = true;
            this.paintValue = false;
            return true;
         } else {
            return super.mouseClicked(click, doubled);
         }
      } else if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         this.commitTextEdit();
         this.listeningKeybind = null;
         if (this.openColorSetting != null) {
            boolean insidePopup = mx >= this.popupX && mx < this.popupX + 130 && my >= this.popupY && my < this.popupY + this.colorPopupHeight();
            if (insidePopup) {
               int[] wheelTab = this.wheelTabBounds();
               int[] slidersTab = this.slidersTabBounds();
               if (isInside(mx, my, wheelTab)) {
                  this.enterWheelMode();
               } else if (isInside(mx, my, slidersTab)) {
                  this.colorPopupSliderMode = true;
               } else if (this.colorPopupSliderMode) {
                  int channel = this.channelAtY(my);
                  if (channel >= 0) {
                     this.draggingColorChannel = channel;
                     this.updateColorChannelFromMouse(channel, mx);
                     Config.save();
                  }
               } else if (this.isInsideColorWheel(mx, my)) {
                  this.draggingColorWheel = true;
                  this.updateColorFromWheel(mx, my);
                  Config.save();
               } else if (this.isInsideValueSlider(mx, my)) {
                  this.draggingColorValue = true;
                  this.updateColorFromValueSlider(my);
                  Config.save();
               } else if (this.isInsideAlphaSlider(mx, my)) {
                  this.draggingColorAlpha = true;
                  this.updateAlphaFromMouse(mx);
                  Config.save();
               }

               return true;
            }

            this.openColorSetting = null;
         }

         if (isInside(mx, my, this.closeX, this.closeY, 18, 18)) {
            this.onClose();
            return true;
         } else if (!this.module.getSettings().isEmpty() && isInside(mx, my, this.resetX, this.columnY + 8, this.resetW, 22)) {
            this.module.resetSettingsToDefault();
            Config.save();
            return true;
         } else {
            if (this.hasTabs()) {
               List<String> tabs = this.tabs();

               for (int i = 0; i < tabs.size(); i++) {
                  if (isInside(mx, my, this.tabBounds(i))) {
                     this.selectedTab = i;
                     this.scrollOffset.jump(0);
                     return true;
                  }
               }
            }

            List<ModuleSettingsScreen.Row> rows = this.buildRows();
            if (mx >= this.columnX && mx < this.columnX + 300 && my >= this.columnY && my < this.columnY + this.panelH) {
               if (my >= this.bodyTop && my < this.bodyBottom) {
                  for (ModuleSettingsScreen.Row row : rows) {
                     int screenY = this.bodyTop + (row.y() - this.scrollOffset.shown());
                     if (my >= screenY && my < screenY + row.h()) {
                        if (row.type() == ModuleSettingsScreen.RowType.GUI_MOVER && this.module instanceof HudModule hud) {
                           Config.save();
                           Minecraft.getInstance().setScreen(new GuiMoverScreen(this, hud));
                           return true;
                        }

                        if (row.type() == ModuleSettingsScreen.RowType.PIXEL_GRID && this.module instanceof CustomCrosshairModule cc) {
                           if (isInside(mx, my, this.clearButtonBounds(screenY))) {
                              cc.customPixelsSetting().set(CustomCrosshairModule.blankGrid());
                              Config.save();
                           } else if (this.setPixelAt(cc, mx, my, true)) {
                              this.paintingGrid = true;
                              this.paintValue = true;
                           }

                           return true;
                        }

                        if (row.type() == ModuleSettingsScreen.RowType.CAPE_GRID && this.module instanceof CosmeticsModule cosmetics) {
                           if (isInside(mx, my, this.clearButtonBounds(screenY))) {
                              cosmetics.clearCape();
                              Config.save();
                           } else if (this.setCapePixelAt(cosmetics, mx, my, true)) {
                              this.paintingGrid = true;
                              this.paintValue = true;
                           }

                           return true;
                        }

                        if (row.type() == ModuleSettingsScreen.RowType.SETTING_BOOL
                           && this.module instanceof NameProtectModule nameProtect
                           && row.setting() == nameProtect.changeSkinSetting()
                           && isInside(mx, my, this.randomSkinBounds(screenY, row.h()))) {
                           nameProtect.randomizeSkin();
                           return true;
                        }

                        this.handleRowClick(row, mx, screenY);
                        return true;
                     }
                  }

                  return true;
               } else {
                  return true;
               }
            } else {
               this.onClose();
               return true;
            }
         }
      }
   }

   private void handleRowClick(ModuleSettingsScreen.Row row, int mx, int screenY) {
      switch (row.type()) {
         case SETTING_BOOL:
            BooleanSetting b = (BooleanSetting)row.setting();
            b.set(!b.get());
            Config.save();
            break;
         case SETTING_SLIDER:
            this.draggingSlider = (SliderSetting)row.setting();
            this.draggingSlider.setFraction((mx - (this.columnX + 12)) / 276.0);
            break;
         case SETTING_COLOR:
            this.openColorSetting = (ColorSetting)row.setting();
            this.popup.restart();
            this.enterWheelMode();
            this.popupX = Math.min(this.columnX + 300 + 4, this.width - 130 - 2);
            this.popupRestY = Mth.clamp(screenY, 0, Math.max(0, this.height - this.colorPopupHeight()));
            break;
         case SETTING_ENUM:
            if (this.module instanceof CosmeticsModule cosmetics && row.setting() == cosmetics.capeStyleSetting()) {
               Minecraft.getInstance().setScreen(new CapePickerScreen(this, cosmetics));
            } else if (row.setting() instanceof ChoiceSetting pick) {
               Minecraft.getInstance().setScreen(new ChoicePickerScreen(this, pick));
            } else {
               ((EnumSetting)row.setting()).cycle();
               Config.save();
            }
            break;
         case SETTING_TEXT:
            if (this.module instanceof BlockColorsModule colors && row.setting() == colors.desaturatedSetting()) {
               Minecraft.getInstance().setScreen(new BlockSaturationScreen(this, colors.desaturatedSetting()));
               break;
            }

            this.editingText = (TextSetting)row.setting();
            this.editBuffer = new StringBuilder(this.editingText.get());
            break;
         case SETTING_KEYBIND:
            this.listeningKeybind = (KeybindSetting)row.setting();
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      int mx = (int)click.x();
      int my = (int)click.y();
      if (this.paintingGrid) {
         if (this.module instanceof CustomCrosshairModule cc) {
            this.setPixelAt(cc, mx, my, this.paintValue);
         } else if (this.module instanceof CosmeticsModule cosmetics) {
            this.setCapePixelAt(cosmetics, mx, my, this.paintValue);
         }

         return true;
      } else if (this.draggingSlider != null) {
         this.draggingSlider.setFraction((mx - (this.columnX + 12)) / 276.0);
         return true;
      } else {
         if (this.openColorSetting != null) {
            if (this.draggingColorWheel) {
               this.updateColorFromWheel(mx, my);
               return true;
            }

            if (this.draggingColorValue) {
               this.updateColorFromValueSlider(my);
               return true;
            }

            if (this.draggingColorAlpha) {
               this.updateAlphaFromMouse(mx);
               return true;
            }

            if (this.draggingColorChannel >= 0) {
               this.updateColorChannelFromMouse(this.draggingColorChannel, mx);
               return true;
            }
         }

         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      boolean wasDragging = this.draggingSlider != null
         || this.paintingGrid
         || this.draggingColorWheel
         || this.draggingColorValue
         || this.draggingColorAlpha
         || this.draggingColorChannel >= 0;
      this.draggingSlider = null;
      this.paintingGrid = false;
      this.draggingColorWheel = false;
      this.draggingColorValue = false;
      this.draggingColorAlpha = false;
      this.draggingColorChannel = -1;
      if (wasDragging) {
         Config.save();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.computeLayout();
      if (this.openColorSetting == null && mouseY >= this.bodyTop && mouseY < this.bodyBottom) {
         this.scrollOffset.setTarget(Mth.clamp(this.scrollOffset.target() - (int)(verticalAmount * 22.0), 0, this.maxScroll(this.buildRows())));
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public boolean charTyped(CharacterEvent input) {
      if (this.listeningKeybind != null) {
         return true;
      } else if (this.editingText != null) {
         if (this.editBuffer.length() < 512 && input.isAllowedChatCharacter() && input.codepoint() != 167) {
            this.editBuffer.appendCodePoint(input.codepoint());
         }

         return true;
      } else {
         return super.charTyped(input);
      }
   }

   private static String trimToEnd(Font textRenderer, String value, int room) {
      if (room <= 0) {
         return "";
      } else if (textRenderer.width(value) <= room) {
         return value;
      } else {
         int from = 0;

         while (from < value.length() && textRenderer.width(value.substring(from)) > room) {
            from++;
         }

         return from > 0 ? "..." + value.substring(Math.min(value.length(), from + 3)) : value;
      }
   }

   private static String sanitisePaste(String text) {
      StringBuilder out = new StringBuilder();

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c != '\n' && c != '\r' && c != '\t' && c != 167) {
            out.append(c);
         }
      }

      return out.toString().trim();
   }

   public boolean keyPressed(KeyEvent input) {
      if (this.listeningKeybind != null) {
         int keyCode = input.key();
         if (keyCode == 256) {
            this.listeningKeybind = null;
            return true;
         } else if (keyCode != 259 && keyCode != 261) {
            this.listeningKeybind.bind(input);
            this.listeningKeybind = null;
            Config.save();
            return true;
         } else {
            this.listeningKeybind.unbind();
            this.listeningKeybind = null;
            Config.save();
            return true;
         }
      } else if (this.editingText == null) {
         if (input.key() == 256) {
            this.onClose();
            return true;
         } else {
            return super.keyPressed(input);
         }
      } else {
         int keyCode = input.key();
         boolean ctrl = (input.modifiers() & 2) != 0 || (input.modifiers() & 8) != 0;
         if (ctrl && keyCode == 86) {
            String clip = sanitisePaste(Minecraft.getInstance().keyboardHandler.getClipboard());
            int room = 512 - this.editBuffer.length();
            if (room > 0 && !clip.isEmpty()) {
               this.editBuffer.append(clip, 0, Math.min(clip.length(), room));
            }

            return true;
         } else if (ctrl && keyCode == 67) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.editBuffer.toString());
            return true;
         } else if (ctrl && keyCode == 88) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.editBuffer.toString());
            this.editBuffer.setLength(0);
            return true;
         } else if (keyCode == 259) {
            if (this.editBuffer.length() > 0) {
               this.editBuffer.setLength(this.editBuffer.length() - 1);
            }

            return true;
         } else if (keyCode == 257 || keyCode == 335) {
            this.commitTextEdit();
            return true;
         } else if (keyCode == 256) {
            this.editingText = null;
            this.editBuffer = null;
            return true;
         } else {
            return true;
         }
      }
   }

   private void commitTextEdit() {
      if (this.editingText != null) {
         TextSetting committed = this.editingText;
         committed.set(this.editBuffer.toString());
         this.editingText = null;
         this.editBuffer = null;
         Config.save();
         if (this.module instanceof NameProtectModule nameProtect && committed == nameProtect.nameSetting()) {
            nameProtect.useTypedNameSkin();
         }
      }
   }

   public void onClose() {
      this.commitTextEdit();
      Config.save();
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent);
      }
   }

   private void renderColorPopup(GuiGraphics context, ColorSetting setting, int mouseX, int mouseY) {
      int h = this.colorPopupHeight();
      fillRounded(context, this.popupX + 2, this.popupY + 2, 130, h, Theme.shadow(), 10);
      fillRoundedBorder(context, this.popupX, this.popupY, 130, h, Theme.modalBorder(), Theme.modalPanelBg(), 10);
      fillRoundedTop(context, this.popupX + 1, this.popupY + 1, 128, 23, 0xFF000000 | setting.rgb(), 9);
      context.drawString(this.font, setting.name, this.popupX + 8, this.popupY + 8, Theme.textPrimary());
      this.renderColorModeTabs(context, mouseX, mouseY);
      if (this.colorPopupSliderMode) {
         this.renderChannelSliders(context, setting);
      } else {
         this.renderColorWheel(context);
         this.renderValueSlider(context);
         this.renderAlphaSlider(context, setting);
      }
   }

   private int bodyTopOfPopup() {
      return this.popupY + 24 + 24;
   }

   private int[] wheelTabBounds() {
      int tabWidth = 59;
      return new int[]{this.popupX + 4, this.popupY + 24 + 5, tabWidth, 14};
   }

   private int[] slidersTabBounds() {
      int[] wheelTab = this.wheelTabBounds();
      return new int[]{wheelTab[0] + wheelTab[2] + 4, wheelTab[1], wheelTab[2], 14};
   }

   private void renderColorModeTabs(GuiGraphics context, int mouseX, int mouseY) {
      this.renderColorModeTab(context, this.wheelTabBounds(), "Wheel", !this.colorPopupSliderMode, mouseX, mouseY);
      this.renderColorModeTab(context, this.slidersTabBounds(), "Sliders", this.colorPopupSliderMode, mouseX, mouseY);
   }

   private void renderColorModeTab(GuiGraphics context, int[] bounds, String label, boolean active, int mouseX, int mouseY) {
      boolean hovered = mouseX >= bounds[0] && mouseX < bounds[0] + bounds[2] && mouseY >= bounds[1] && mouseY < bounds[1] + bounds[3];
      int fillColor = active ? Theme.accent() : (hovered ? Theme.hoverBg() : Theme.trackBg());
      fillRounded(context, bounds[0], bounds[1], bounds[2], bounds[3], fillColor, 3);
      int textColor = active ? ThemeRenderer.onAccent() : Theme.textMuted();
      int textWidth = this.font.width(label);
      context.drawString(this.font, label, bounds[0] + (bounds[2] - textWidth) / 2, bounds[1] + 3, textColor);
   }

   private void renderChannelSliders(GuiGraphics context, ColorSetting setting) {
      for (int i = 0; i < CHANNEL_LABELS.length; i++) {
         int rowY = this.channelRowY(i);
         int trackX = this.popupX + 20;
         int trackW = 98;
         int trackH = 3;
         int trackY = rowY + 22 - trackH - 3;
         int amount = setting.channel(i);
         int fillW = trackW * amount / 255;
         fillRounded(context, trackX, trackY, trackW, trackH, Theme.trackBg(), 1);
         if (fillW > 0) {
            fillRounded(context, trackX, trackY, fillW, trackH, Theme.accent(), 1);
         }

         context.drawString(this.font, CHANNEL_LABELS[i] + " " + amount, this.popupX + 8, rowY + 3, Theme.textMuted());
      }
   }

   private int channelRowY(int channel) {
      return this.bodyTopOfPopup() + channel * 22;
   }

   private int channelAtY(int my) {
      int relative = my - this.bodyTopOfPopup();
      if (relative < 0) {
         return -1;
      } else {
         int channel = relative / 22;
         return channel >= 0 && channel < CHANNEL_LABELS.length ? channel : -1;
      }
   }

   private void updateColorChannelFromMouse(int channel, int mx) {
      int trackX = this.popupX + 20;
      int trackW = 98;
      double fraction = Mth.clamp((double)(mx - trackX) / trackW, 0.0, 1.0);
      this.openColorSetting.setChannel(channel, (int)Math.round(fraction * 255.0));
   }

   private void enterWheelMode() {
      this.colorPopupSliderMode = false;
      float[] hsv = rgbToHsv(this.openColorSetting.rgb());
      this.colorWheelHue = hsv[0];
      this.colorWheelSat = hsv[1];
      this.colorWheelVal = hsv[2];
   }

   private int wheelCenterX() {
      return this.popupX + 8 + 34;
   }

   private int wheelCenterY() {
      return this.bodyTopOfPopup() + 8 + 34;
   }

   private void renderColorWheel(GuiGraphics context) {
      int cx = this.wheelCenterX();
      int cy = this.wheelCenterY();
      context.blit(
         RenderPipelines.GUI_TEXTURED,
         ColorWheel.texture(34),
         cx - 34,
         cy - 34,
         0.0F,
         0.0F,
         ColorWheel.size(34),
         ColorWheel.size(34),
         ColorWheel.size(34),
         ColorWheel.size(34),
         -1
      );
      double markerAngle = Math.toRadians(this.colorWheelHue);
      double markerDist = this.colorWheelSat * 34.0F;
      int markerX = cx + (int)Math.round(Math.cos(markerAngle) * markerDist);
      int markerY = cy + (int)Math.round(Math.sin(markerAngle) * markerDist);
      context.fill(markerX - 2 - 1, markerY - 2 - 1, markerX + 2 + 2, markerY + 2 + 2, -16777216);
      context.fill(markerX - 2, markerY - 2, markerX + 2 + 1, markerY + 2 + 1, -1);
   }

   private int valueSliderX() {
      return this.wheelCenterX() + 34 + 8;
   }

   private int valueSliderTop() {
      return this.wheelCenterY() - 34;
   }

   private int valueSliderHeight() {
      return 68;
   }

   private void renderValueSlider(GuiGraphics context) {
      int x = this.valueSliderX();
      int top = this.valueSliderTop();
      int hueRgb = hsvToRgb(this.colorWheelHue, this.colorWheelSat, 1.0F);
      int sliderHeight = this.valueSliderHeight();
      context.fillGradient(x, top, x + 12, top + sliderHeight, hueRgb, mixWithBlack(hueRgb, 0.0F));
      int handleY = top + Math.round((1.0F - this.colorWheelVal) * (sliderHeight - 1));
      context.fill(x - 2, handleY - 1, x + 12 + 2, handleY + 2, -16777216);
      context.fill(x - 1, handleY, x + 12 + 1, handleY + 1, -1);
   }

   private int alphaRowY() {
      return this.wheelCenterY() + 34 + 8;
   }

   private void renderAlphaSlider(GuiGraphics context, ColorSetting setting) {
      int rowY = this.alphaRowY();
      int trackX = this.popupX + 20;
      int trackW = 98;
      int trackH = 3;
      int trackY = rowY + 22 - trackH - 3;
      int amount = setting.a();
      int fillW = trackW * amount / 255;
      fillRounded(context, trackX, trackY, trackW, trackH, Theme.trackBg(), 1);
      if (fillW > 0) {
         fillRounded(context, trackX, trackY, fillW, trackH, Theme.accent(), 1);
      }

      context.drawString(this.font, "A " + amount, this.popupX + 8, rowY + 3, Theme.textMuted());
   }

   private int colorPopupHeight() {
      return this.colorPopupSliderMode ? 48 + CHANNEL_LABELS.length * 22 + 6 : 162;
   }

   private static boolean isInside(int mx, int my, int[] bounds) {
      return mx >= bounds[0] && mx < bounds[0] + bounds[2] && my >= bounds[1] && my < bounds[1] + bounds[3];
   }

   private static boolean isInside(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   private boolean isInsideColorWheel(int mx, int my) {
      double dist = Math.sqrt(Math.pow(mx - this.wheelCenterX(), 2.0) + Math.pow(my - this.wheelCenterY(), 2.0));
      return dist <= 34.0;
   }

   private boolean isInsideValueSlider(int mx, int my) {
      int x = this.valueSliderX();
      int top = this.valueSliderTop();
      return mx >= x - 4 && mx < x + 12 + 4 && my >= top && my < top + this.valueSliderHeight();
   }

   private boolean isInsideAlphaSlider(int mx, int my) {
      int rowY = this.alphaRowY();
      return mx >= this.popupX && mx < this.popupX + 130 && my >= rowY && my < rowY + 22;
   }

   private void updateColorFromWheel(int mx, int my) {
      int dx = mx - this.wheelCenterX();
      int dy = my - this.wheelCenterY();
      double dist = Math.min(34.0, Math.sqrt((double)dx * dx + (double)dy * dy));
      this.colorWheelHue = angleToHue(dx, dy);
      this.colorWheelSat = (float)(dist / 34.0);
      this.applyWheelColor();
   }

   private void updateColorFromValueSlider(int my) {
      int top = this.valueSliderTop();
      float fraction = 1.0F - (float)(my - top) / (this.valueSliderHeight() - 1);
      this.colorWheelVal = Mth.clamp(fraction, 0.0F, 1.0F);
      this.applyWheelColor();
   }

   private void updateAlphaFromMouse(int mx) {
      int trackX = this.popupX + 20;
      int trackW = 98;
      double fraction = Mth.clamp((double)(mx - trackX) / trackW, 0.0, 1.0);
      this.openColorSetting.setChannel(3, (int)Math.round(fraction * 255.0));
   }

   private void applyWheelColor() {
      int rgb = hsvToRgb(this.colorWheelHue, this.colorWheelSat, this.colorWheelVal) & 16777215;
      int alpha = this.openColorSetting.a();
      this.openColorSetting.set(alpha << 24 | rgb);
   }

   private static int mixWithBlack(int rgb, float fraction) {
      int r = Math.round((rgb >> 16 & 0xFF) * fraction);
      int g = Math.round((rgb >> 8 & 0xFF) * fraction);
      int b = Math.round((rgb & 0xFF) * fraction);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private static float angleToHue(int x, int y) {
      double degrees = Math.toDegrees(Math.atan2(y, x));
      if (degrees < 0.0) {
         degrees += 360.0;
      }

      return (float)degrees;
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

   private static void fillRounded(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      ThemeRenderer.fillRounded(context, x, y, w, h, color, radius);
   }

   private static void fillRoundedTop(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      ThemeRenderer.fillRoundedTop(context, x, y, w, h, color, radius);
   }

   private static void fillRoundedBorder(GuiGraphics context, int x, int y, int w, int h, int borderColor, int fillColor, int radius) {
      ThemeRenderer.fillRoundedBorder(context, x, y, w, h, borderColor, fillColor, radius);
   }

   private static void fillGradientRounded(GuiGraphics context, int x, int y, int w, int h, int radius, int colorA, int colorB) {
      ThemeRenderer.fillGradientRounded(context, x, y, w, h, radius, colorA, colorB);
   }

   private record Row(ModuleSettingsScreen.RowType type, int y, int h, Setting<?> setting, String label) {
   }

   private static enum RowType {
      SECTION_HEADER,
      SETTING_BOOL,
      SETTING_SLIDER,
      SETTING_COLOR,
      SETTING_ENUM,
      SETTING_TEXT,
      SETTING_KEYBIND,
      PIXEL_GRID,
      CAPE_GRID,
      GUI_MOVER;
   }
}
