package dev.lyfw.lyfwclient.profile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public final class PublicProfileData {
   public static final int MAX_PIP_CHARS = 400000;
   private static final Set<String> SHARED_ROOT_KEYS = Set.of("themePreset", "guiLayout", "themeAccent", "themeShadows", "themeScrim");
   private static final Set<String> PRIVATE_MODULES = Set.of("Name Protect", "Pip Presence", "Leaderboard");
   private static final Map<String, Set<String>> LOCAL_SETTINGS = Map.of(
      "Main Menu",
      Set.of("Background Image"),
      "Mouse Tracker",
      Set.of("Background Image"),
      "Custom Hotbar",
      Set.of("Background Image"),
      "Skybox",
      Set.of("Image"),
      "Song Player",
      Set.of("Music Folder")
   );
   private static final Pattern LOCAL_VALUE = Pattern.compile("(?i)(\\b[a-z]:[\\\\/])|([\\\\/](users|home)[\\\\/])|([a-z][a-z0-9+.-]*://)");
   private static final Set<String> PRIVATE_OPTION_KEYS = Set.of(
      "version",
      "lastServer",
      "resourcePacks",
      "incompatibleResourcePacks",
      "soundDevice",
      "fullscreenResolution",
      "overrideWidth",
      "overrideHeight",
      "tutorialStep",
      "joinedFirstServer",
      "onboardAccessibility",
      "skipMultiplayerWarning"
   );

   private PublicProfileData() {
   }

   public static String scrubPipConfig(String json) {
      JsonElement parsed = JsonParser.parseString(json != null && !json.isBlank() ? json : "{}");
      JsonObject root = parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
      JsonObject out = new JsonObject();

      for (String key : SHARED_ROOT_KEYS) {
         if (root.has(key) && root.get(key).isJsonPrimitive()) {
            out.add(key, root.get(key));
         }
      }

      JsonObject modules = root.has("modules") && root.get("modules").isJsonObject() ? root.getAsJsonObject("modules") : new JsonObject();
      JsonObject sharedModules = new JsonObject();

      for (Entry<String, JsonElement> module : modules.entrySet()) {
         if (!PRIVATE_MODULES.contains(module.getKey()) && module.getValue().isJsonObject()) {
            JsonObject data = module.getValue().getAsJsonObject();
            JsonObject shared = new JsonObject();
            if (data.has("enabled") && data.get("enabled").isJsonPrimitive()) {
               shared.add("enabled", data.get("enabled"));
            }

            if (data.has("settings") && data.get("settings").isJsonObject()) {
               Set<String> local = LOCAL_SETTINGS.getOrDefault(module.getKey(), Set.of());
               JsonObject settings = new JsonObject();

               for (Entry<String, JsonElement> setting : data.getAsJsonObject("settings").entrySet()) {
                  if (!local.contains(setting.getKey()) && !looksLocal(setting.getValue())) {
                     settings.add(setting.getKey(), setting.getValue());
                  }
               }

               shared.add("settings", settings);
            }

            sharedModules.add(module.getKey(), shared);
         }
      }

      out.add("modules", sharedModules);
      return out.toString();
   }

   private static boolean looksLocal(JsonElement value) {
      return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && LOCAL_VALUE.matcher(value.getAsString()).find();
   }

   public static String mergePipConfig(String currentJson, String sharedJson) {
      JsonElement parsedCurrent = JsonParser.parseString(currentJson != null && !currentJson.isBlank() ? currentJson : "{}");
      JsonObject current = parsedCurrent.isJsonObject() ? parsedCurrent.getAsJsonObject() : new JsonObject();
      JsonObject shared = JsonParser.parseString(scrubPipConfig(sharedJson)).getAsJsonObject();

      for (String key : SHARED_ROOT_KEYS) {
         if (shared.has(key)) {
            current.add(key, shared.get(key));
         }
      }

      JsonObject currentModules = current.has("modules") && current.get("modules").isJsonObject() ? current.getAsJsonObject("modules") : new JsonObject();

      for (Entry<String, JsonElement> module : shared.getAsJsonObject("modules").entrySet()) {
         JsonObject source = module.getValue().getAsJsonObject();
         JsonObject target = currentModules.has(module.getKey()) && currentModules.get(module.getKey()).isJsonObject()
            ? currentModules.getAsJsonObject(module.getKey())
            : new JsonObject();
         if (source.has("enabled")) {
            target.add("enabled", source.get("enabled"));
         }

         if (source.has("settings")) {
            JsonObject targetSettings = target.has("settings") && target.get("settings").isJsonObject() ? target.getAsJsonObject("settings") : new JsonObject();

            for (Entry<String, JsonElement> setting : source.getAsJsonObject("settings").entrySet()) {
               targetSettings.add(setting.getKey(), setting.getValue());
            }

            target.add("settings", targetSettings);
         }

         currentModules.add(module.getKey(), target);
      }

      current.add("modules", currentModules);
      return current.toString();
   }

   public static List<String> scrubOptions(List<String> lines) {
      List<String> out = new ArrayList<>();

      for (String line : lines) {
         if (line != null && !line.isBlank() && !PRIVATE_OPTION_KEYS.contains(optionKey(line))) {
            out.add(line);
         }
      }

      return out;
   }

   public static List<String> overlayOptions(List<String> current, List<String> shared) {
      Map<String, String> merged = new LinkedHashMap<>();

      for (String line : current) {
         if (line != null && !line.isBlank()) {
            merged.put(optionKey(line), line);
         }
      }

      for (String linex : scrubOptions(shared)) {
         merged.put(optionKey(linex), linex);
      }

      return new ArrayList<>(merged.values());
   }

   private static String optionKey(String line) {
      int colon = line.indexOf(58);
      return colon < 0 ? line.trim() : line.substring(0, colon).trim();
   }

   public static String slug(String name) {
      String slug = (name == null ? "" : name).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
      if (slug.isEmpty()) {
         return "profile";
      } else {
         return slug.length() > 40 ? slug.substring(0, 40) : slug;
      }
   }
}
