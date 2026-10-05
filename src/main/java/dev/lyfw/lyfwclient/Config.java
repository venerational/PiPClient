package dev.lyfw.lyfwclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lyfw.lyfwclient.gui.CarrotClickGuiScreen;
import dev.lyfw.lyfwclient.gui.HudEditorScreen;
import dev.lyfw.lyfwclient.gui.PanelState;
import dev.lyfw.lyfwclient.gui.Theme;
import dev.lyfw.lyfwclient.gui.WindowState;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.Setting;
import dev.lyfw.lyfwclient.stats.PlaytimeTracker;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.loader.api.FabricLoader;

public class Config {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("lyfw-client.json");
   private static final Map<String, String> LEGACY_NAMES = Map.of(
      "Nametags", "Nameplates", "Grayscale", "Greyscale", "Custom Fog", "Pink Fog", "Item Glint", "Armor Tint"
   );

   public static void reloadForProfile() {
      if (Files.exists(PATH)) {
         for (Module module : ModuleManager.all()) {
            if (!module.hidden) {
               module.setEnabled(module.enabledByDefault());
            }

            module.resetSettingsToDefault();
         }

         load();
      }
   }

   public static void load() {
      try {
         if (!Files.exists(PATH)) {
            return;
         }

         JsonObject root = JsonParser.parseString(Files.readString(PATH)).getAsJsonObject();
         JsonObject modules = root.has("modules") ? root.getAsJsonObject("modules") : null;
         if (modules != null) {
            for (Module module : ModuleManager.all()) {
               String saved = savedNameOf(modules, module.name);
               if (saved != null) {
                  JsonObject data = modules.getAsJsonObject(saved);
                  if (data.has("enabled")) {
                     module.setEnabled(data.get("enabled").getAsBoolean());
                  }

                  if (data.has("favorite")) {
                     module.favorite = data.get("favorite").getAsBoolean();
                  }

                  JsonObject settingsJson = data.has("settings") ? data.getAsJsonObject("settings") : null;
                  if (settingsJson != null) {
                     for (Setting<?> setting : module.getSettings()) {
                        if (settingsJson.has(setting.name)) {
                           try {
                              setting.read(settingsJson.get(setting.name));
                           } catch (RuntimeException var14) {
                              System.out.println("[Pip Client] " + module.name + " / " + setting.name + " would not load (" + var14 + ")");
                           }
                        }
                     }
                  }
               }
            }
         }

         if (modules != null) {
            migrateNostalgia(modules);
         }

         if (!root.has("colorBakeRepaired")) {
            repairBakedColors();
         }

         if (root.has("snapEnabled")) {
            HudEditorScreen.setSnapEnabled(root.get("snapEnabled").getAsBoolean());
         }

         if (root.has("themePreset")) {
            Theme.setPresetByName(root.get("themePreset").getAsString());
         }

         if (root.has("guiCategory")) {
            if ("TESTING".equals(root.get("guiCategory").getAsString()) || "MISC".equals(root.get("guiCategory").getAsString())) {
               CarrotClickGuiScreen.setLastCategory(Category.ALL);
            }

            for (Category category : Category.values()) {
               if (category.name().equals(root.get("guiCategory").getAsString())) {
                  CarrotClickGuiScreen.setLastCategory(category);
                  break;
               }
            }
         }

         if (root.has("guiLayout")) {
            Theme.setLayoutByName(root.get("guiLayout").getAsString());
         }

         if (root.has("guiWindows")) {
            JsonObject windows = root.getAsJsonObject("guiWindows");
            int loaded = 0;
            boolean allSameSpot = true;
            int firstX = 0;
            int firstY = 0;

            for (Category categoryx : Category.values()) {
               if (windows.has(categoryx.name())) {
                  JsonObject w = windows.getAsJsonObject(categoryx.name());
                  if (w.has("x") && w.has("y")) {
                     int wx = w.get("x").getAsInt();
                     int wy = w.get("y").getAsInt();
                     if (loaded == 0) {
                        firstX = wx;
                        firstY = wy;
                     } else if (wx != firstX || wy != firstY) {
                        allSameSpot = false;
                     }

                     loaded++;
                     WindowState.set(categoryx, wx, wy);
                  }

                  if (w.has("collapsed")) {
                     WindowState.setCollapsed(categoryx, w.get("collapsed").getAsBoolean());
                  }
               }
            }

            if (loaded > 1 && allSameSpot) {
               WindowState.resetPositions();
            }
         }

         if (root.has("guiPanels")) {
            JsonObject panels = root.getAsJsonObject("guiPanels");

            for (String key : panels.keySet()) {
               JsonObject p = panels.getAsJsonObject(key);
               if (p.has("x") && p.has("y")) {
                  PanelState.set(key, p.get("x").getAsInt(), p.get("y").getAsInt());
               }
            }
         }

         if (root.has("themeAccent")) {
            Theme.setAccent(root.get("themeAccent").getAsInt());
         }

         if (root.has("playtimeTotal")) {
            PlaytimeTracker.get()
               .restore(
                  root.get("playtimeTotal").getAsLong(),
                  root.has("playtimeLongest") ? root.get("playtimeLongest").getAsLong() : 0L,
                  root.has("playtimeFirstSeen") ? root.get("playtimeFirstSeen").getAsLong() : 0L
               );
         }

         if (root.has("themeShadows")) {
            Theme.setShadows(root.get("themeShadows").getAsBoolean());
         }

         if (root.has("themeScrim")) {
            Theme.setScrim(root.get("themeScrim").getAsBoolean());
         }
      } catch (Exception var15) {
         System.out.println("[Pip Client] could not read " + PATH + " (" + var15 + "); anything it had not got to yet is on defaults");
      }
   }

   private static String savedNameOf(JsonObject modules, String name) {
      if (modules.has(name)) {
         return name;
      } else {
         String legacy = LEGACY_NAMES.get(name);
         return legacy != null && modules.has(legacy) ? legacy : null;
      }
   }

   private static void migrateNostalgia(JsonObject modules) {
      Module nostalgia = ModuleManager.get("Nostalgia");
      if (nostalgia != null && !modules.has("Nostalgia")) {
         boolean carried = false;
         if (modules.has("Old Lighting")) {
            JsonObject legacy = modules.getAsJsonObject("Old Lighting");
            carried |= legacy.has("enabled") && legacy.get("enabled").getAsBoolean() && setBoolean(nostalgia, "Old Lighting", true);
         }

         if (modules.has("Clean View")) {
            JsonObject legacy = modules.getAsJsonObject("Clean View");
            JsonObject settings = legacy.has("settings") ? legacy.getAsJsonObject("settings") : null;
            if (settings != null && settings.has("Old Crystals") && settings.get("Old Crystals").getAsBoolean()) {
               carried |= setEnumByName(nostalgia, "Crystal Motion", "STATIC");
            }
         }

         if (carried) {
            nostalgia.setEnabled(true);
            System.out.println("[Pip Client] Old Lighting / Old Crystals carried over into Nostalgia");
         }
      }
   }

   private static boolean setEnumByName(Module module, String name, String value) {
      for (Setting<?> setting : module.getSettings()) {
         if (setting.name.equals(name) && setting instanceof EnumSetting option) {
            option.setByName(value);
            return true;
         }
      }

      return false;
   }

   private static boolean setBoolean(Module module, String name, boolean value) {
      for (Setting<?> setting : module.getSettings()) {
         if (setting.name.equals(name) && setting instanceof BooleanSetting flag) {
            flag.set(value);
            return true;
         }
      }

      return false;
   }

   private static void repairBakedColors() {
      Module global = ModuleManager.get("Global Color");
      if (global != null && global.isEnabled()) {
         int baked = -1;

         for (Setting<?> setting : global.getSettings()) {
            if (setting.name.equals("Color") && setting instanceof ColorSetting c) {
               baked = c.raw() & 16777215;
            }
         }

         if (baked >= 0) {
            int repaired = 0;

            for (Module module : ModuleManager.all()) {
               if (module != global) {
                  for (Setting<?> settingx : module.getSettings()) {
                     if (settingx instanceof ColorSetting c && (c.raw() & 16777215) == baked) {
                        c.resetToDefault();
                        repaired++;
                     }
                  }
               }
            }

            if (repaired > 0) {
               System.out
                  .println(
                     "[Pip Client] "
                        + repaired
                        + " colors had been overwritten by Global Color and are back on their defaults. Turning Global Color off will show them."
                  );
            }
         }
      }
   }

   public static void save() {
      JsonObject root = new JsonObject();
      JsonObject modules = new JsonObject();

      for (Module module : ModuleManager.all()) {
         JsonObject data = new JsonObject();
         data.addProperty("enabled", module.isEnabled());
         data.addProperty("favorite", module.favorite);
         JsonObject settingsJson = new JsonObject();

         for (Setting<?> setting : module.getSettings()) {
            setting.write(settingsJson);
         }

         data.add("settings", settingsJson);
         modules.add(module.name, data);
      }

      root.add("modules", modules);
      root.addProperty("snapEnabled", HudEditorScreen.isSnapEnabled());
      root.addProperty("colorBakeRepaired", true);
      root.addProperty("themePreset", Theme.preset().name());
      root.addProperty("guiLayout", Theme.layout().name());
      root.addProperty("guiCategory", CarrotClickGuiScreen.lastCategory().name());
      root.addProperty("themeAccent", Theme.accent());
      root.addProperty("playtimeTotal", PlaytimeTracker.get().totalSeconds());
      root.addProperty("playtimeLongest", PlaytimeTracker.get().longestSessionSeconds());
      root.addProperty("playtimeFirstSeen", PlaytimeTracker.get().firstSeenEpoch());
      root.addProperty("themeShadows", Theme.shadowsEnabled());
      root.addProperty("themeScrim", Theme.scrimEnabled());
      JsonObject windows = new JsonObject();

      for (Category category : Category.values()) {
         if (WindowState.hasPosition(category)) {
            JsonObject w = new JsonObject();
            w.addProperty("x", WindowState.x(category));
            w.addProperty("y", WindowState.y(category));
            w.addProperty("collapsed", WindowState.isCollapsed(category));
            windows.add(category.name(), w);
         }
      }

      root.add("guiWindows", windows);
      JsonObject panels = new JsonObject();

      for (Entry<String, int[]> entry : PanelState.all()) {
         JsonObject p = new JsonObject();
         p.addProperty("x", entry.getValue()[0]);
         p.addProperty("y", entry.getValue()[1]);
         panels.add(entry.getKey(), p);
      }

      root.add("guiPanels", panels);

      try {
         Files.writeString(PATH, GSON.toJson(root));
      } catch (Exception var8) {
         System.out.println("[Pip Client] could not write " + PATH + " (" + var8 + "); this session's settings are not saved");
      }
   }
}
