package dev.lyfw.lyfwclient.crash;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public final class CrashWatcher implements PreLaunchEntrypoint {
   private static final CrashAnalyzer.Mods NO_MODS = new CrashAnalyzer.Mods() {
      @Override
      public String modOf(String className) {
         return null;
      }

      @Override
      public String nameOf(String id) {
         return null;
      }
   };

   public void onPreLaunch() {
      long started = System.currentTimeMillis();
      Path gameDir = FabricLoader.getInstance().getGameDir();
      CrashAnalyzer.class.getName();
      CrashSummary.class.getName();
      CrashSummary.Suspect.class.getName();
      CrashWatcher.FabricMods.class.getName();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> onExit(gameDir, started), "Pip Client crash check"));
   }

   private static void onExit(Path gameDir, long started) {
      try {
         Path reports = gameDir.resolve("crash-reports");
         Path report = newestReport(reports, started);
         if (report == null) {
            return;
         }

         String text;
         try {
            text = Files.readString(report, StandardCharsets.UTF_8);
         } catch (MalformedInputException var10) {
            text = Files.readString(report, StandardCharsets.ISO_8859_1);
         }

         CrashAnalyzer.Mods mods;
         try {
            mods = new CrashWatcher.FabricMods();
         } catch (Throwable var9) {
            mods = NO_MODS;
         }

         CrashSummary summary = CrashAnalyzer.analyze(text, mods);
         summary.reportPath = report.toAbsolutePath().toString();
         summary.modsDir = gameDir.resolve("mods").toAbsolutePath().toString();
         Path summaryFile = reports.resolve("pip-crash-summary.txt");
         summary.write(summaryFile);
         Files.writeString(reports.resolve("pip-crash-pending"), report.getFileName().toString());
         System.out.println("[Pip Client] the game crashed: " + summary.title);
         openWindow(summaryFile);
      } catch (Throwable var11) {
         System.out.println("[Pip Client] could not explain the crash: " + var11);
      }
   }

   private static Path newestReport(Path reports, long started) throws IOException {
      if (!Files.isDirectory(reports)) {
         return null;
      } else {
         Path newest = null;
         long newestTime = started - 2000L;

         try (Stream<Path> files = Files.list(reports)) {
            for (Path file : files.toList()) {
               String name = file.getFileName().toString();
               long modified = Files.getLastModifiedTime(file).toMillis();
               if (name.startsWith("crash-") && name.endsWith(".txt") && modified >= newestTime) {
                  newest = file;
                  newestTime = modified;
               }
            }
         }

         return newest;
      }
   }

   private static void openWindow(Path summaryFile) throws IOException {
      String java = ProcessHandle.current().info().command().orElse(null);
      String classpath = pipClasspath();
      if (java != null && classpath != null) {
         Path windowless = Path.of(java).resolveSibling("javaw.exe");
         if (java.endsWith("java.exe") && Files.exists(windowless)) {
            java = windowless.toString();
         }

         new ProcessBuilder(java, "-Djava.awt.headless=false", "-cp", classpath, CrashWindow.class.getName(), summaryFile.toAbsolutePath().toString())
            .redirectErrorStream(true)
            .redirectOutput(summaryFile.resolveSibling("pip-crash-window.log").toFile())
            .start();
      }
   }

   private static String pipClasspath() {
      List<String> paths = new ArrayList<>();

      try {
         paths.add(new File(CrashWatcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath());
      } catch (Exception var2) {
      }

      FabricLoader.getInstance().getModContainer("lyfw-client").ifPresent(mod -> {
         for (Path path : normal(originPaths(mod))) {
            if (!paths.contains(path.toString())) {
               paths.add(path.toString());
            }
         }
      });
      return paths.isEmpty() ? null : String.join(File.pathSeparator, paths);
   }

   private static List<Path> originPaths(ModContainer mod) {
      try {
         return mod.getOrigin().getPaths();
      } catch (RuntimeException var2) {
         return List.of();
      }
   }

   private static List<Path> normal(List<Path> paths) {
      List<Path> out = new ArrayList<>();

      for (Path path : paths) {
         out.add(path.toAbsolutePath().normalize());
      }

      return out;
   }

   public static CrashSummary takePendingNotice() {
      try {
         Path reports = FabricLoader.getInstance().getGameDir().resolve("crash-reports");
         Path pending = reports.resolve("pip-crash-pending");
         if (!Files.exists(pending)) {
            return null;
         } else {
            Files.deleteIfExists(pending);
            Path summary = reports.resolve("pip-crash-summary.txt");
            return Files.exists(summary) ? CrashSummary.read(summary) : null;
         }
      } catch (RuntimeException | IOException var3) {
         return null;
      }
   }

   private static final class FabricMods implements CrashAnalyzer.Mods {
      private final Map<String, String> owners = new HashMap<>();
      private final List<ModContainer> mods = new ArrayList<>(FabricLoader.getInstance().getAllMods());

      @Override
      public String modOf(String className) {
         if (!className.startsWith("net.minecraft.")
            && !className.startsWith("com.mojang.")
            && !className.startsWith("java.")
            && !className.startsWith("jdk.")
            && !className.startsWith("sun.")) {
            String owner = this.owners.computeIfAbsent(className, name -> {
               String path = name.replace('.', '/');
               int inner = path.indexOf(36);
               path = (inner >= 0 ? path.substring(0, inner) : path) + ".class";

               for (ModContainer mod : this.mods) {
                  String id = mod.getMetadata().getId();
                  if (!id.equals("minecraft") && !id.equals("java")) {
                     for (Path root : mod.getRootPaths()) {
                        if (Files.exists(root.resolve(path))) {
                           return id;
                        }
                     }
                  }
               }

               Path location = locationOf(CrashWatcher.class.getClassLoader(), path);
               if (location != null) {
                  for (ModContainer modx : this.mods) {
                     String id = modx.getMetadata().getId();
                     if (!id.equals("minecraft") && !id.equals("java")) {
                        List<Path> places = new ArrayList<>(CrashWatcher.normal(CrashWatcher.originPaths(modx)));

                        for (Path rootx : modx.getRootPaths()) {
                           if (rootx.getFileSystem() == FileSystems.getDefault()) {
                              places.add(rootx.toAbsolutePath().normalize());
                           }
                        }

                        if (places.contains(location)) {
                           return id;
                        }
                     }
                  }
               }

               return "";
            });
            return owner.isEmpty() ? null : owner;
         } else {
            return null;
         }
      }

      private static Path locationOf(ClassLoader loader, String resource) {
         try {
            URL url = loader == null ? null : loader.getResource(resource);
            if (url == null) {
               return null;
            }

            String text = url.toString();
            if (text.startsWith("jar:") && text.contains("!/")) {
               return Path.of(new URI(text.substring(4, text.indexOf("!/")))).toAbsolutePath().normalize();
            }

            if (text.startsWith("file:")) {
               Path root = Path.of(url.toURI());

               for (int i = 0; i < resource.split("/").length; i++) {
                  root = root.getParent();
               }

               return root.toAbsolutePath().normalize();
            }
         } catch (Exception var6) {
         }

         return null;
      }

      @Override
      public String nameOf(String id) {
         return FabricLoader.getInstance().getModContainer(id).map(mod -> mod.getMetadata().getName()).orElse(null);
      }
   }
}
