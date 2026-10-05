package dev.lyfw.lyfwclient.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lyfw.lyfwclient.Config;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProfileStorage {
   private static final Logger LOGGER = LoggerFactory.getLogger("profile_presets");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Pattern VALID_NAME_PATTERN = Pattern.compile("[A-Za-z0-9 ]+");
   private static final String RESOURCE_PACK_PREFIX = "file/";
   private static final String OPTIONS_FILE = "options.txt";
   private static final String KEYBINDS_FILE = "keybinds.txt";
   private static final String KEYBIND_OPTION_PREFIX = "key_";
   private static final List<String> MOUSE_OPTION_PREFIXES = List.of(
      "mouseSensitivity:", "invertYMouse:", "mouseWheelSensitivity:", "rawMouseInput:", "discrete_mouse_scroll:"
   );
   private static final String ACTIVE_RESOURCE_PACKS_FILE = "active_resource_packs.json";
   private static final String PROFILE_META_FILE = "profile_meta.json";
   private static final String ROOT_CONFIG_FILES_DIR = "root_config_files";
   private static final List<String> CONFIG_FILE_EXTENSIONS = List.of(
      ".json5", ".json", ".toml", ".yaml", ".yml", ".properties", ".ini", ".conf", ".cfg", ".txt", ".xml"
   );
   private static final List<String> CONFIG_NAME_SUFFIXES = List.of(
      "-config",
      "_config",
      " config",
      "-settings",
      "_settings",
      " settings",
      "-options",
      "_options",
      " options",
      "-client",
      "_client",
      " client",
      "-common",
      "_common",
      " common"
   );
   private static final List<String> KNOWN_GAME_ROOT_CONFIG_FILES = List.of("crosshair_config.ccmcfg");
   public static final ProfileStorage.SaveSelection DEFAULT_SELECTION = new ProfileStorage.SaveSelection(true, true, true, true);
   public static final ProfileStorage.LoadSelection DEFAULT_LOAD_SELECTION = new ProfileStorage.LoadSelection(true, true, true, true);
   private static final String PUBLIC_PIP_FILE = "pip_public.json";
   private static final String PUBLISHED_FILE = "published.json";
   private static final String PUBLIC_SOURCE_FILE = "public_source.json";

   private ProfileStorage() {
   }

   public static ProfileStorage.LoadSelection loadAllSaved(ProfileStorage.ProfileDetails profileDetails) {
      return profileDetails == null
         ? DEFAULT_LOAD_SELECTION
         : new ProfileStorage.LoadSelection(
            profileDetails.hasConfig(), profileDetails.hasOptions(), profileDetails.hasKeybinds(), profileDetails.hasResourcePacks()
         );
   }

   public static Path getProfilesRoot() {
      return FabricLoader.getInstance().getGameDir().resolve("profilesaves");
   }

   public static String sanitizeProfileName(String name) {
      return name == null ? "" : name.trim().replaceAll("\\s+", " ");
   }

   public static boolean isValidProfileName(String name) {
      String normalizedName = sanitizeProfileName(name);
      return !normalizedName.isEmpty() && VALID_NAME_PATTERN.matcher(normalizedName).matches();
   }

   public static List<String> listProfiles() throws IOException {
      Path root = getProfilesRoot();
      if (!Files.isDirectory(root)) {
         return List.of();
      } else {
         List var2;
         try (Stream<Path> paths = Files.list(root)) {
            var2 = paths.filter(x$0 -> Files.isDirectory(x$0)).map(path -> path.getFileName().toString()).sorted(String.CASE_INSENSITIVE_ORDER).toList();
         }

         return var2;
      }
   }

   public static ProfileStorage.ProfileDetails readProfileDetails(String name) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         Path optionsPath = profileDir.resolve("options.txt");
         Path keybindsPath = profileDir.resolve("keybinds.txt");
         boolean hasConfig = Files.exists(profileDir.resolve("config"))
            || hasSavedGameRootConfigFiles(profileDir.resolve("root_config_files"))
            || Files.exists(profileDir.resolve("pip_public.json"));
         boolean hasOptions = Files.exists(optionsPath);
         boolean hasLegacyControlsInOptions = hasSavedControlLinesInOptions(optionsPath);
         boolean hasKeybinds = Files.exists(keybindsPath) || hasLegacyControlsInOptions;
         boolean hasResourcePacks = Files.exists(profileDir.resolve("resourcepacks")) || Files.exists(profileDir.resolve("active_resource_packs.json"));
         long savedAt = 0L;

         try {
            savedAt = Files.getLastModifiedTime(profileDir).toMillis();
         } catch (IOException var15) {
         }

         Path metaPath = profileDir.resolve("profile_meta.json");
         if (Files.isRegularFile(metaPath)) {
            try {
               String json = Files.readString(metaPath, StandardCharsets.UTF_8);
               ProfileStorage.StoredProfileMeta meta = (ProfileStorage.StoredProfileMeta)GSON.fromJson(json, ProfileStorage.StoredProfileMeta.class);
               if (meta != null) {
                  return new ProfileStorage.ProfileDetails(
                     normalizedName,
                     meta.saveConfig() || hasConfig,
                     meta.saveOptions() || hasOptions,
                     meta.saveKeybinds() || hasKeybinds,
                     meta.saveResourcePacks() || hasResourcePacks,
                     meta.savedAtEpochMs() > 0L ? meta.savedAtEpochMs() : savedAt
                  );
               }
            } catch (RuntimeException | IOException var16) {
               LOGGER.warn("Failed to parse profile metadata for {}", normalizedName, var16);
            }
         }

         return new ProfileStorage.ProfileDetails(normalizedName, hasConfig, hasOptions, hasKeybinds, hasResourcePacks, savedAt);
      }
   }

   public static void saveProfile(String name, ProfileStorage.SaveSelection selection) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      if (!isValidProfileName(normalizedName)) {
         throw new IllegalArgumentException("Profile name must contain only letters, numbers, and spaces.");
      } else {
         ProfileStorage.SaveSelection effectiveSelection = selection == null ? DEFAULT_SELECTION : selection;
         if (!effectiveSelection.anyEnabled()) {
            throw new IllegalArgumentException("Select at least one thing to save.");
         } else {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path root = getProfilesRoot();
            Path profileDir = root.resolve(normalizedName);
            Files.createDirectories(root);
            if (profileExists(root, normalizedName)) {
               throw new IllegalArgumentException("A profile with this name already exists.");
            } else {
               Files.createDirectories(profileDir);
               if (effectiveSelection.saveConfig()) {
                  Path configDir = gameDir.resolve("config");
                  if (Files.exists(configDir)) {
                     copyRecursively(configDir, profileDir.resolve("config"));
                  }

                  copyKnownGameRootConfigFiles(gameDir, profileDir.resolve("root_config_files"));
               }

               Path optionsFile = gameDir.resolve("options.txt");
               if ((effectiveSelection.saveOptions() || effectiveSelection.saveKeybinds()) && Files.exists(optionsFile)) {
                  List<String> optionsLines = Files.readAllLines(optionsFile, StandardCharsets.UTF_8);
                  if (effectiveSelection.saveOptions()) {
                     Files.write(
                        profileDir.resolve("options.txt"),
                        filterOptionLines(optionsLines, false),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                     );
                  }

                  if (effectiveSelection.saveKeybinds()) {
                     Files.write(
                        profileDir.resolve("keybinds.txt"),
                        filterOptionLines(optionsLines, true),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                     );
                  }
               }

               if (effectiveSelection.saveResourcePacks()) {
                  List<String> activePackIds = normalizePackIdList(readActiveResourcePackIds(optionsFile));
                  Files.writeString(
                     profileDir.resolve("active_resource_packs.json"),
                     GSON.toJson(activePackIds),
                     StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING
                  );
                  copyActiveResourcePackFiles(gameDir.resolve("resourcepacks"), profileDir.resolve("resourcepacks"), activePackIds);
               }

               writeProfileMetadata(profileDir, effectiveSelection);
            }
         }
      }
   }

   public static void loadProfile(String name) throws IOException {
      loadProfile(name, DEFAULT_LOAD_SELECTION);
   }

   public static void loadProfile(String name, ProfileStorage.LoadSelection selection) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         ProfileStorage.LoadSelection effectiveSelection = selection == null ? DEFAULT_LOAD_SELECTION : selection;
         if (!effectiveSelection.anyEnabled()) {
            throw new IllegalArgumentException("Select at least one thing to load.");
         } else if (!Files.isRegularFile(profileDir.resolve("pip_public.json")) && !Files.isRegularFile(profileDir.resolve("public_source.json"))) {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            Path sourceConfig = profileDir.resolve("config");
            Path targetConfig = gameDir.resolve("config");
            if (effectiveSelection.loadConfig() && Files.exists(sourceConfig)) {
               replaceWithBestEffort(sourceConfig, targetConfig, "config");
            }

            if (effectiveSelection.loadConfig()) {
               restoreKnownGameRootConfigFiles(profileDir.resolve("root_config_files"), gameDir);
            }

            Path sourceOptions = profileDir.resolve("options.txt");
            Path sourceKeybinds = profileDir.resolve("keybinds.txt");
            Path targetOptions = gameDir.resolve("options.txt");
            if ((effectiveSelection.loadOptions() || effectiveSelection.loadKeybinds()) && (Files.exists(sourceOptions) || Files.exists(sourceKeybinds))) {
               try {
                  applySavedOptionsWithKeybinds(
                     sourceOptions, sourceKeybinds, targetOptions, effectiveSelection.loadOptions(), effectiveSelection.loadKeybinds()
                  );
               } catch (IOException var12) {
                  LOGGER.warn("Failed to apply saved options/keybinds from profile {}", normalizedName, var12);
               }
            }
         } else {
            loadPublicProfile(profileDir, effectiveSelection);
         }
      }
   }

   public static void deleteProfile(String name) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      deleteRecursively(profileDir);
   }

   public static void renameProfile(String currentName, String newName) throws IOException {
      String normalizedCurrentName = sanitizeProfileName(currentName);
      String normalizedNewName = sanitizeProfileName(newName);
      if (!isValidProfileName(normalizedNewName)) {
         throw new IllegalArgumentException("Profile name must contain only letters, numbers, and spaces.");
      } else {
         Path root = getProfilesRoot();
         Path currentProfileDir = root.resolve(normalizedCurrentName);
         if (!Files.isDirectory(currentProfileDir)) {
            throw new IOException("Saved profile folder is missing.");
         } else {
            if (!normalizedCurrentName.equals(normalizedNewName)) {
               Files.createDirectories(root);
               Path targetProfileDir = root.resolve(normalizedNewName);
               if (!normalizedCurrentName.equalsIgnoreCase(normalizedNewName) && profileExists(root, normalizedNewName)) {
                  throw new IllegalArgumentException("A profile with this name already exists.");
               }

               if (!normalizedCurrentName.equalsIgnoreCase(normalizedNewName)) {
                  Files.move(currentProfileDir, targetProfileDir);
               } else {
                  if (Files.exists(targetProfileDir) && !Files.isSameFile(currentProfileDir, targetProfileDir)) {
                     throw new IllegalArgumentException("A profile with this name already exists.");
                  }

                  Path tempProfileDir = root.resolve(normalizedCurrentName + "__rename_tmp__");

                  for (int suffix = 1; Files.exists(tempProfileDir); suffix++) {
                     tempProfileDir = root.resolve(normalizedCurrentName + "__rename_tmp__" + suffix);
                  }

                  Files.move(currentProfileDir, tempProfileDir);
                  Files.move(tempProfileDir, targetProfileDir);
               }
            }
         }
      }
   }

   public static List<String> readSavedActiveResourcePackIds(String name) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         Path packsFile = profileDir.resolve("active_resource_packs.json");
         if (Files.isRegularFile(packsFile)) {
            try {
               JsonArray jsonArray = (JsonArray)GSON.fromJson(Files.readString(packsFile, StandardCharsets.UTF_8), JsonArray.class);
               if (jsonArray != null) {
                  List<String> parsed = new ArrayList<>();

                  for (JsonElement element : jsonArray) {
                     if (element.isJsonPrimitive()) {
                        String value = element.getAsString();
                        if (value != null) {
                           parsed.add(value);
                        }
                     }
                  }

                  List<String> normalized = normalizePackIdList(parsed);
                  if (!normalized.isEmpty()) {
                     return normalized;
                  }
               }
            } catch (RuntimeException | IOException var9) {
               LOGGER.warn("Failed reading saved active packs for profile {}", normalizedName, var9);
            }
         }

         return normalizePackIdList(readActiveResourcePackIds(profileDir.resolve("options.txt")));
      }
   }

   public static List<String> readImportableMissingResourcePackIds(String name) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         List<String> savedPackIds = readSavedActiveResourcePackIds(normalizedName);
         if (savedPackIds.isEmpty()) {
            return List.of();
         } else {
            Path gameResourcePacksDir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
            Path savedResourcePacksDir = profileDir.resolve("resourcepacks");
            Map<String, List<Path>> installedPacks = Files.isDirectory(gameResourcePacksDir) ? indexResourcePackFiles(gameResourcePacksDir) : Map.of();
            Map<String, List<Path>> savedPacks = Files.isDirectory(savedResourcePacksDir) ? indexResourcePackFiles(savedResourcePacksDir) : Map.of();
            LinkedHashSet<String> missingPackIds = new LinkedHashSet<>();

            for (String packId : savedPackIds) {
               if (packId.startsWith("file/")
                  && resolvePackSourcePath(gameResourcePacksDir, installedPacks, packId) == null
                  && resolvePackSourcePath(savedResourcePacksDir, savedPacks, packId) != null) {
                  missingPackIds.add(packId);
               }
            }

            return List.copyOf(missingPackIds);
         }
      }
   }

   public static List<String> importSavedResourcePacks(String name, List<String> packIds) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         List<String> normalizedPackIds = normalizePackIdList(packIds);
         if (normalizedPackIds.isEmpty()) {
            return List.of();
         } else {
            Path sourceRoot = profileDir.resolve("resourcepacks");
            Path destinationRoot = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
            Files.createDirectories(destinationRoot);
            return copyActiveResourcePackFiles(sourceRoot, destinationRoot, normalizedPackIds);
         }
      }
   }

   public static List<String> readMissingConfigTargets(String name) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         LinkedHashMap<String, String> savedTargets = collectSavedConfigTargets(profileDir);
         if (savedTargets.isEmpty()) {
            return List.of();
         } else {
            List<String> installedAliases = buildInstalledModAliases();
            List<String> missingTargets = new ArrayList<>();

            for (Entry<String, String> entry : savedTargets.entrySet()) {
               if (!matchesInstalledAlias(entry.getKey(), installedAliases)) {
                  missingTargets.add(entry.getValue());
               }
            }

            return List.copyOf(missingTargets);
         }
      }
   }

   private static void writeProfileMetadata(Path profileDir, ProfileStorage.SaveSelection selection) {
      try {
         ProfileStorage.StoredProfileMeta meta = new ProfileStorage.StoredProfileMeta(
            selection.saveConfig(), selection.saveOptions(), selection.saveKeybinds(), selection.saveResourcePacks(), System.currentTimeMillis()
         );
         Files.writeString(
            profileDir.resolve("profile_meta.json"), GSON.toJson(meta), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
         );
      } catch (IOException var3) {
         LOGGER.warn("Failed writing profile metadata {}", profileDir, var3);
      }
   }

   private static LinkedHashMap<String, String> collectSavedConfigTargets(Path profileDir) throws IOException {
      LinkedHashMap<String, String> savedTargets = new LinkedHashMap<>();
      Path configDir = profileDir.resolve("config");
      if (Files.isDirectory(configDir)) {
         try (Stream<Path> children = Files.list(configDir)) {
            for (Path child : children.sorted(Comparator.comparing(path -> path.getFileName().toString(), String.CASE_INSENSITIVE_ORDER)).toList()) {
               addConfigTarget(savedTargets, child.getFileName().toString());
            }
         }
      }

      Path rootConfigDir = profileDir.resolve("root_config_files");
      if (Files.isDirectory(rootConfigDir)) {
         try (Stream<Path> children = Files.list(rootConfigDir)) {
            for (Path child : children.filter(x$0 -> Files.isRegularFile(x$0))
               .sorted(Comparator.comparing(path -> path.getFileName().toString(), String.CASE_INSENSITIVE_ORDER))
               .toList()) {
               addConfigTarget(savedTargets, child.getFileName().toString());
            }
         }
      }

      return savedTargets;
   }

   private static void addConfigTarget(Map<String, String> savedTargets, String rawName) {
      if (rawName != null && !rawName.isBlank()) {
         String normalized = normalizeConfigTarget(rawName);
         if (!normalized.isBlank() && normalized.length() >= 3) {
            savedTargets.putIfAbsent(normalized, toDisplayConfigTarget(rawName));
         }
      }
   }

   private static List<String> buildInstalledModAliases() {
      LinkedHashSet<String> aliases = new LinkedHashSet<>();

      for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
         aliases.add(normalizeConfigTarget(modContainer.getMetadata().getId()));
         aliases.add(normalizeConfigTarget(modContainer.getMetadata().getName()));
      }

      aliases.removeIf(String::isBlank);
      return List.copyOf(aliases);
   }

   private static boolean matchesInstalledAlias(String candidate, List<String> installedAliases) {
      if (candidate != null && !candidate.isBlank()) {
         for (String alias : installedAliases) {
            if (alias != null && !alias.isBlank()) {
               if (candidate.equals(alias)) {
                  return true;
               }

               if (alias.length() >= 4 && candidate.contains(alias)) {
                  return true;
               }

               if (candidate.length() >= 4 && alias.contains(candidate)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static String normalizeConfigTarget(String rawName) {
      if (rawName == null) {
         return "";
      } else {
         String normalized = rawName.trim().toLowerCase(Locale.ROOT);

         while (true) {
            String stripped = stripKnownConfigExtension(normalized);
            if (stripped.equals(normalized)) {
               boolean changed;
               do {
                  changed = false;

                  for (String suffix : CONFIG_NAME_SUFFIXES) {
                     if (normalized.endsWith(suffix)) {
                        normalized = normalized.substring(0, normalized.length() - suffix.length()).trim();
                        changed = true;
                     }
                  }
               } while (changed);

               return normalized.replaceAll("[^a-z0-9]+", "");
            }

            normalized = stripped;
         }
      }
   }

   private static String stripKnownConfigExtension(String value) {
      for (String extension : CONFIG_FILE_EXTENSIONS) {
         if (value.endsWith(extension)) {
            return value.substring(0, value.length() - extension.length());
         }
      }

      return value;
   }

   private static String toDisplayConfigTarget(String rawName) {
      String display = rawName == null ? "" : rawName.trim();

      while (true) {
         String stripped = stripKnownConfigExtension(display);
         if (stripped.equals(display)) {
            display = display.replace('_', ' ').replace('-', ' ').replace('.', ' ').trim();
            display = display.replaceAll("\\s+", " ");
            if (display.isEmpty()) {
               return rawName;
            } else {
               String[] words = display.split(" ");
               StringBuilder formatted = new StringBuilder(display.length());

               for (String word : words) {
                  if (!word.isEmpty()) {
                     if (!formatted.isEmpty()) {
                        formatted.append(' ');
                     }

                     formatted.append(Character.toUpperCase(word.charAt(0)));
                     if (word.length() > 1) {
                        formatted.append(word.substring(1));
                     }
                  }
               }

               return formatted.toString();
            }
         }

         display = stripped;
      }
   }

   private static void applySavedOptionsWithKeybinds(Path sourceOptions, Path sourceKeybinds, Path targetOptions, boolean loadOptions, boolean loadKeybinds) throws IOException {
      List<String> existingTargetLines = Files.isRegularFile(targetOptions) ? Files.readAllLines(targetOptions, StandardCharsets.UTF_8) : List.of();
      List<String> mergedOptionLines = filterOptionLines(existingTargetLines, false);
      List<String> mergedKeybindLines = filterOptionLines(existingTargetLines, true);
      boolean hasSourceOptions = Files.isRegularFile(sourceOptions);
      boolean hasSourceKeybinds = Files.isRegularFile(sourceKeybinds);
      List<String> sourceOptionLines = List.of();
      boolean sourceOptionsContainControlLines = false;
      if (hasSourceOptions && (loadOptions || loadKeybinds)) {
         sourceOptionLines = Files.readAllLines(sourceOptions, StandardCharsets.UTF_8);
         sourceOptionsContainControlLines = containsControlLines(sourceOptionLines);
      }

      if (loadOptions && hasSourceOptions) {
         mergedOptionLines = filterOptionLines(sourceOptionLines, false);
      }

      if (loadKeybinds) {
         if (hasSourceKeybinds) {
            mergedKeybindLines = Files.readAllLines(sourceKeybinds, StandardCharsets.UTF_8);
         } else if (hasSourceOptions && sourceOptionsContainControlLines) {
            mergedKeybindLines = filterOptionLines(sourceOptionLines, true);
         }
      }

      List<String> mergedLines = new ArrayList<>(mergedOptionLines.size() + mergedKeybindLines.size());
      mergedLines.addAll(mergedOptionLines);
      mergedLines.addAll(mergedKeybindLines);
      Files.write(targetOptions, mergedLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   }

   private static boolean hasSavedControlLinesInOptions(Path optionsPath) {
      if (!Files.isRegularFile(optionsPath)) {
         return false;
      } else {
         try {
            return containsControlLines(Files.readAllLines(optionsPath, StandardCharsets.UTF_8));
         } catch (IOException var2) {
            return false;
         }
      }
   }

   private static List<String> filterOptionLines(List<String> lines, boolean controlLines) {
      if (lines != null && !lines.isEmpty()) {
         List<String> filtered = new ArrayList<>(lines.size());

         for (String line : lines) {
            if (line != null) {
               boolean isControl = isControlLine(line);
               if (isControl == controlLines) {
                  filtered.add(line);
               }
            }
         }

         return filtered;
      } else {
         return List.of();
      }
   }

   private static boolean containsControlLines(List<String> lines) {
      if (lines != null && !lines.isEmpty()) {
         for (String line : lines) {
            if (line != null && isControlLine(line)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean isControlLine(String line) {
      return isKeybindLine(line) || isMouseSettingLine(line);
   }

   private static boolean isKeybindLine(String line) {
      return line.startsWith("key_");
   }

   private static boolean isMouseSettingLine(String line) {
      for (String prefix : MOUSE_OPTION_PREFIXES) {
         if (line.startsWith(prefix)) {
            return true;
         }
      }

      return false;
   }

   private static boolean profileExists(Path root, String profileName) throws IOException {
      boolean var3;
      try (Stream<Path> paths = Files.list(root)) {
         var3 = paths.filter(x$0 -> Files.isDirectory(x$0))
            .map(path -> path.getFileName().toString())
            .anyMatch(existingName -> existingName.equalsIgnoreCase(profileName));
      }

      return var3;
   }

   private static boolean hasSavedGameRootConfigFiles(Path rootConfigDir) {
      if (!Files.isDirectory(rootConfigDir)) {
         return false;
      } else {
         for (String fileName : KNOWN_GAME_ROOT_CONFIG_FILES) {
            if (Files.isRegularFile(rootConfigDir.resolve(fileName))) {
               return true;
            }
         }

         return false;
      }
   }

   private static void copyKnownGameRootConfigFiles(Path gameDir, Path rootConfigDir) throws IOException {
      for (String fileName : KNOWN_GAME_ROOT_CONFIG_FILES) {
         Path sourceFile = gameDir.resolve(fileName);
         if (Files.isRegularFile(sourceFile)) {
            Path destinationFile = rootConfigDir.resolve(fileName);
            Path destinationParent = destinationFile.getParent();
            if (destinationParent != null) {
               Files.createDirectories(destinationParent);
            }

            Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
         }
      }
   }

   private static void restoreKnownGameRootConfigFiles(Path sourceRootConfigDir, Path gameDir) {
      if (Files.isDirectory(sourceRootConfigDir)) {
         for (String fileName : KNOWN_GAME_ROOT_CONFIG_FILES) {
            Path sourceFile = sourceRootConfigDir.resolve(fileName);
            if (Files.isRegularFile(sourceFile)) {
               try {
                  Files.copy(sourceFile, gameDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
               } catch (IOException var6) {
                  LOGGER.warn("Failed restoring game root config file {}", fileName, var6);
               }
            }
         }
      }
   }

   private static List<String> readActiveResourcePackIds(Path optionsFile) {
      if (!Files.exists(optionsFile)) {
         return List.of();
      } else {
         try {
            for (String line : Files.readAllLines(optionsFile, StandardCharsets.UTF_8)) {
               if (line.startsWith("resourcePacks:")) {
                  String encodedArray = line.substring("resourcePacks:".length());
                  JsonArray jsonArray = (JsonArray)GSON.fromJson(encodedArray, JsonArray.class);
                  if (jsonArray == null) {
                     return List.of();
                  }

                  List<String> ids = new ArrayList<>();

                  for (JsonElement element : jsonArray) {
                     if (element.isJsonPrimitive()) {
                        ids.add(element.getAsString());
                     }
                  }

                  return ids;
               }
            }
         } catch (RuntimeException | IOException var8) {
            return List.of();
         }

         return List.of();
      }
   }

   private static List<String> copyActiveResourcePackFiles(Path sourceRoot, Path destinationRoot, List<String> activePackIds) throws IOException {
      if (Files.isDirectory(sourceRoot) && !activePackIds.isEmpty()) {
         Map<String, List<Path>> filesByName = indexResourcePackFiles(sourceRoot);
         LinkedHashSet<String> copiedPackIds = new LinkedHashSet<>();

         for (String packId : activePackIds) {
            Path sourcePath = resolvePackSourcePath(sourceRoot, filesByName, packId);
            if (sourcePath != null) {
               String relativePath = normalizeResourcePackPath(packId.substring("file/".length()));
               Path destinationPath = destinationRoot.resolve(relativePath).normalize();
               if (destinationPath.startsWith(destinationRoot)) {
                  copyRecursively(sourcePath, destinationPath);
                  copiedPackIds.add(packId);
               }
            }
         }

         return List.copyOf(copiedPackIds);
      } else {
         return List.of();
      }
   }

   private static List<String> normalizePackIdList(List<String> ids) {
      if (ids != null && !ids.isEmpty()) {
         LinkedHashSet<String> deduped = new LinkedHashSet<>();

         for (String id : ids) {
            if (id != null) {
               String trimmed = id.trim();
               if (!trimmed.isEmpty()) {
                  deduped.add(trimmed);
               }
            }
         }

         return List.copyOf(deduped);
      } else {
         return List.of();
      }
   }

   private static String normalizeResourcePackPath(String value) {
      String decoded = decodePercentEscapes(value).replace('\\', '/').trim();

      while (decoded.startsWith("/")) {
         decoded = decoded.substring(1);
      }

      return decoded;
   }

   private static String decodePercentEscapes(String value) {
      StringBuilder decoded = new StringBuilder(value.length());

      for (int i = 0; i < value.length(); i++) {
         char current = value.charAt(i);
         if (current == '%' && i + 2 < value.length()) {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int j;
            for (j = i; j + 2 < value.length() && value.charAt(j) == '%'; j += 3) {
               int high = hexValue(value.charAt(j + 1));
               int low = hexValue(value.charAt(j + 2));
               if (high < 0 || low < 0) {
                  break;
               }

               byteBuffer.write((high << 4) + low);
            }

            if (byteBuffer.size() == 0) {
               decoded.append(current);
            } else {
               decoded.append(byteBuffer.toString(StandardCharsets.UTF_8));
               i = j - 1;
            }
         } else {
            decoded.append(current);
         }
      }

      return decoded.toString();
   }

   private static int hexValue(char character) {
      if (character >= '0' && character <= '9') {
         return character - 48;
      } else if (character >= 'A' && character <= 'F') {
         return character - 65 + 10;
      } else {
         return character >= 97 && character <= 102 ? character - 97 + 10 : -1;
      }
   }

   private static Map<String, List<Path>> indexResourcePackFiles(Path sourceRoot) throws IOException {
      Map<String, List<Path>> filesByName = new HashMap<>();

      try (Stream<Path> walk = Files.walk(sourceRoot)) {
         for (Path path : walk.filter(x$0 -> Files.isRegularFile(x$0)).toList()) {
            String key = path.getFileName().toString().toLowerCase(Locale.ROOT);
            filesByName.computeIfAbsent(key, ignored -> new ArrayList<>()).add(path);
         }
      }

      Comparator<Path> pathPriority = Comparator.comparingInt(Path::getNameCount).thenComparing(Path::toString, String.CASE_INSENSITIVE_ORDER);

      for (List<Path> paths : filesByName.values()) {
         paths.sort(pathPriority);
      }

      return filesByName;
   }

   private static Path resolvePackSourcePath(Path sourceRoot, Map<String, List<Path>> filesByName, String packId) {
      if (!packId.startsWith("file/")) {
         return null;
      } else {
         String relativePath = normalizeResourcePackPath(packId.substring("file/".length()));
         if (relativePath.isBlank()) {
            return null;
         } else {
            Path directPath = sourceRoot.resolve(relativePath).normalize();
            if (directPath.startsWith(sourceRoot) && Files.exists(directPath)) {
               return directPath;
            } else {
               String fileName;
               try {
                  Path relative = Path.of(relativePath);
                  if (relative.getFileName() == null) {
                     return null;
                  }

                  fileName = relative.getFileName().toString().toLowerCase(Locale.ROOT);
               } catch (InvalidPathException var9) {
                  return null;
               }

               List<Path> matches = filesByName.get(fileName);
               if (matches != null && !matches.isEmpty()) {
                  for (Path match : matches) {
                     if (match.startsWith(sourceRoot)) {
                        return match;
                     }
                  }

                  return null;
               } else {
                  return null;
               }
            }
         }
      }
   }

   private static void replaceWithBestEffort(Path source, Path target, String label) {
      try {
         deleteRecursively(target);
      } catch (IOException var4) {
         LOGGER.warn("Failed to clear target {} directory before loading profile", label, var4);
      }

      copyRecursivelyBestEffort(source, target, label);
   }

   private static void copyRecursivelyBestEffort(Path source, Path destination, String label) {
      if (Files.exists(source)) {
         try (Stream<Path> stream = Files.walk(source)) {
            for (Path current : stream.toList()) {
               Path relative = source.relativize(current);
               Path target = destination.resolve(relative);

               try {
                  if (Files.isDirectory(current)) {
                     Files.createDirectories(target);
                  } else {
                     Path parent = target.getParent();
                     if (parent != null) {
                        Files.createDirectories(parent);
                     }

                     Files.copy(current, target, StandardCopyOption.REPLACE_EXISTING);
                  }
               } catch (IOException var10) {
                  LOGGER.warn("Failed copying {} file {} while loading profile", new Object[]{label, current, var10});
               }
            }
         } catch (IOException var12) {
            LOGGER.warn("Failed walking {} directory while loading profile", label, var12);
         }
      }
   }

   private static void copyRecursively(Path source, Path destination) throws IOException {
      if (Files.exists(source)) {
         try (Stream<Path> stream = Files.walk(source)) {
            for (Path current : stream.toList()) {
               Path relative = source.relativize(current);
               Path target = destination.resolve(relative);
               if (Files.isDirectory(current)) {
                  Files.createDirectories(target);
               } else {
                  Path parent = target.getParent();
                  if (parent != null) {
                     Files.createDirectories(parent);
                  }

                  Files.copy(current, target, StandardCopyOption.REPLACE_EXISTING);
               }
            }
         }
      }
   }

   private static void deleteRecursively(Path target) throws IOException {
      if (Files.exists(target)) {
         try (Stream<Path> walk = Files.walk(target)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).toList()) {
               Files.deleteIfExists(path);
            }
         }
      }
   }

   public static ProfileStorage.PublishPayload buildPublishPayload(String name) throws IOException {
      String normalizedName = sanitizeProfileName(name);
      Path profileDir = getProfilesRoot().resolve(normalizedName);
      if (!Files.isDirectory(profileDir)) {
         throw new IOException("Saved profile folder is missing.");
      } else {
         String pip = "";
         Path savedPip = profileDir.resolve("config").resolve("lyfw-client.json");
         Path importedPip = profileDir.resolve("pip_public.json");

         try {
            if (Files.isRegularFile(savedPip)) {
               pip = PublicProfileData.scrubPipConfig(Files.readString(savedPip, StandardCharsets.UTF_8));
            } else if (Files.isRegularFile(importedPip)) {
               pip = PublicProfileData.scrubPipConfig(Files.readString(importedPip, StandardCharsets.UTF_8));
            }
         } catch (RuntimeException var10) {
            throw new IOException("Pip's settings in that profile are not readable.");
         }

         String options = "";
         Path optionsPath = profileDir.resolve("options.txt");
         if (Files.isRegularFile(optionsPath)) {
            options = String.join("\n", PublicProfileData.scrubOptions(filterOptionLines(Files.readAllLines(optionsPath, StandardCharsets.UTF_8), false)));
         }

         String keybinds = "";
         Path keybindsPath = profileDir.resolve("keybinds.txt");
         if (Files.isRegularFile(keybindsPath)) {
            keybinds = String.join("\n", PublicProfileData.scrubOptions(Files.readAllLines(keybindsPath, StandardCharsets.UTF_8)));
         }

         return new ProfileStorage.PublishPayload(normalizedName, pip, options, keybinds);
      }
   }

   public static String importPublicProfile(String desiredName, String author, String pip, String options, String keybinds) throws IOException {
      Path root = getProfilesRoot();
      Files.createDirectories(root);
      String base = sanitizeProfileName((desiredName == null ? "" : desiredName).replaceAll("[^A-Za-z0-9 ]", " "));
      if (base.length() > 32) {
         base = base.substring(0, 32).trim();
      }

      if (!isValidProfileName(base)) {
         base = "Shared";
      }

      String name = base;

      for (int n = 2; profileExists(root, name); n++) {
         name = base + " " + n;
      }

      Path profileDir = root.resolve(name);
      Files.createDirectories(profileDir);
      boolean hasPip = pip != null && !pip.isBlank();
      boolean hasOptions = options != null && !options.isBlank();
      boolean hasKeybinds = keybinds != null && !keybinds.isBlank();

      try {
         if (hasPip) {
            Files.writeString(profileDir.resolve("pip_public.json"), PublicProfileData.scrubPipConfig(pip), StandardCharsets.UTF_8);
         }
      } catch (RuntimeException var13) {
         deleteRecursively(profileDir);
         throw new IOException("that profile's settings are not readable");
      }

      if (hasOptions) {
         Files.write(profileDir.resolve("options.txt"), PublicProfileData.scrubOptions(Arrays.asList(options.split("\\R"))), StandardCharsets.UTF_8);
      }

      if (hasKeybinds) {
         Files.write(profileDir.resolve("keybinds.txt"), PublicProfileData.scrubOptions(Arrays.asList(keybinds.split("\\R"))), StandardCharsets.UTF_8);
      }

      writeProfileMetadata(profileDir, new ProfileStorage.SaveSelection(hasPip, hasOptions, hasKeybinds, false));
      JsonObject source = new JsonObject();
      source.addProperty("author", author == null ? "" : author);
      Files.writeString(profileDir.resolve("public_source.json"), source.toString(), StandardCharsets.UTF_8);
      return name;
   }

   private static void loadPublicProfile(Path profileDir, ProfileStorage.LoadSelection selection) throws IOException {
      Path importedPip = profileDir.resolve("pip_public.json");
      if (selection.loadConfig() && Files.isRegularFile(importedPip)) {
         Config.save();
         Path live = FabricLoader.getInstance().getConfigDir().resolve("lyfw-client.json");
         String current = Files.isRegularFile(live) ? Files.readString(live, StandardCharsets.UTF_8) : "";

         try {
            String merged = PublicProfileData.mergePipConfig(current, Files.readString(importedPip, StandardCharsets.UTF_8));
            Files.writeString(live, merged, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         } catch (RuntimeException var8) {
            throw new IOException("Pip's settings in that profile are not readable.");
         }

         Config.load();
      }

      List<String> shared = new ArrayList<>();
      Path optionsPath = profileDir.resolve("options.txt");
      Path keybindsPath = profileDir.resolve("keybinds.txt");
      if (selection.loadOptions() && Files.isRegularFile(optionsPath)) {
         shared.addAll(Files.readAllLines(optionsPath, StandardCharsets.UTF_8));
      }

      if (selection.loadKeybinds() && Files.isRegularFile(keybindsPath)) {
         shared.addAll(Files.readAllLines(keybindsPath, StandardCharsets.UTF_8));
      }

      if (!shared.isEmpty()) {
         Path targetOptions = FabricLoader.getInstance().getGameDir().resolve("options.txt");
         List<String> current = Files.isRegularFile(targetOptions) ? Files.readAllLines(targetOptions, StandardCharsets.UTF_8) : List.of();
         Files.write(
            targetOptions,
            PublicProfileData.overlayOptions(current, shared),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
         );
      }
   }

   public static void markPublished(String name, String uuid, String slug) throws IOException {
      Path profileDir = getProfilesRoot().resolve(sanitizeProfileName(name));
      if (Files.isDirectory(profileDir)) {
         JsonObject marker = new JsonObject();
         marker.addProperty("uuid", uuid);
         marker.addProperty("slug", slug);
         Files.writeString(profileDir.resolve("published.json"), marker.toString(), StandardCharsets.UTF_8);
      }
   }

   public static String[] publishedAs(String name) {
      Path marker = getProfilesRoot().resolve(sanitizeProfileName(name)).resolve("published.json");
      if (!Files.isRegularFile(marker)) {
         return null;
      } else {
         try {
            JsonObject json = JsonParser.parseString(Files.readString(marker, StandardCharsets.UTF_8)).getAsJsonObject();
            String uuid = json.has("uuid") ? json.get("uuid").getAsString() : "";
            String slug = json.has("slug") ? json.get("slug").getAsString() : "";
            return !uuid.isEmpty() && !slug.isEmpty() ? new String[]{uuid, slug} : null;
         } catch (RuntimeException | IOException var5) {
            return null;
         }
      }
   }

   public static void clearPublished(String name) {
      try {
         Files.deleteIfExists(getProfilesRoot().resolve(sanitizeProfileName(name)).resolve("published.json"));
      } catch (IOException var2) {
         LOGGER.warn("Failed clearing shared marker for {}", name, var2);
      }
   }

   public static void clearPublishedSlug(String slug) {
      try {
         for (String name : listProfiles()) {
            String[] shared = publishedAs(name);
            if (shared != null && shared[1].equals(slug)) {
               clearPublished(name);
            }
         }
      } catch (IOException var4) {
         LOGGER.warn("Failed clearing shared marker for slug {}", slug, var4);
      }
   }

   public record LoadSelection(boolean loadConfig, boolean loadOptions, boolean loadKeybinds, boolean loadResourcePacks) {
      public boolean anyEnabled() {
         return this.loadConfig || this.loadOptions || this.loadKeybinds || this.loadResourcePacks;
      }
   }

   public record ProfileDetails(String name, boolean hasConfig, boolean hasOptions, boolean hasKeybinds, boolean hasResourcePacks, long savedAtEpochMs) {
   }

   public record PublishPayload(String name, String pip, String options, String keybinds) {
      public boolean isEmpty() {
         return this.pip.isEmpty() && this.options.isEmpty() && this.keybinds.isEmpty();
      }
   }

   public record SaveSelection(boolean saveConfig, boolean saveOptions, boolean saveKeybinds, boolean saveResourcePacks) {
      public boolean anyEnabled() {
         return this.saveConfig || this.saveOptions || this.saveKeybinds || this.saveResourcePacks;
      }
   }

   private record StoredProfileMeta(boolean saveConfig, boolean saveOptions, boolean saveKeybinds, boolean saveResourcePacks, long savedAtEpochMs) {
   }
}
