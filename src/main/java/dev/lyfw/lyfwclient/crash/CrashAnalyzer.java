package dev.lyfw.lyfwclient.crash;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CrashAnalyzer {
   private static final Pattern FRAME = Pattern.compile("^\\s*at\\s+(?:[\\w.\\-]+/{1,2})?([\\w.$]+)\\.([^.(\\s]+)\\(");
   private static final Pattern MIXIN_METHOD = Pattern.compile("\\$[a-z]{3}\\d{3}\\$([a-z0-9_\\-]+)\\$");
   private static final Pattern FROM_MOD = Pattern.compile("from mod \\[?([a-z0-9_]+(?:[-.][a-z0-9_]+)*)\\]?");
   private static final Pattern LINKAGE = Pattern.compile(
      "(NoSuchMethodError|NoSuchFieldError|NoClassDefFoundError|ClassNotFoundException|AbstractMethodError|IncompatibleClassChangeError|VerifyError)"
   );
   private static final Set<String> NOT_MODS = Set.of("minecraft", "java", "fabricloader", "mixinextras");

   private CrashAnalyzer() {
   }

   public static CrashSummary analyze(String report, CrashAnalyzer.Mods mods) {
      List<String> section = mainSection(report);
      List<String> headers = new ArrayList<>();
      Set<String> frameMods = new LinkedHashSet<>();
      Set<String> fabricApi = new LinkedHashSet<>();

      for (String line : section) {
         Matcher frame = FRAME.matcher(line);
         if (frame.find()) {
            String id = null;
            Matcher mixin = MIXIN_METHOD.matcher(frame.group(2));
            if (mixin.find()) {
               id = mixin.group(1);
            } else {
               try {
                  id = mods.modOf(frame.group(1));
               } catch (RuntimeException var19) {
               }
            }

            if (id != null && !id.isEmpty() && !NOT_MODS.contains(id)) {
               (isFabricApi(id) ? fabricApi : frameMods).add(id);
            }
         } else if (!line.isBlank() && !line.trim().startsWith("...")) {
            headers.add(line.trim());
         }
      }

      if (frameMods.isEmpty() && !fabricApi.isEmpty()) {
         frameMods.add("fabric-api");
      }

      String allHeaders = String.join("\n", headers);
      Set<String> mixinMods = new LinkedHashSet<>();
      Matcher from = FROM_MOD.matcher(allHeaders);

      while (from.find()) {
         String idx = from.group(1);
         if (!NOT_MODS.contains(idx)) {
            mixinMods.add(isFabricApi(idx) ? "fabric-api" : idx);
         }
      }

      CrashSummary summary = new CrashSummary();
      String first = headers.isEmpty() ? "" : headers.get(0);
      String root = first;

      for (String header : headers) {
         if (header.startsWith("Caused by: ")) {
            root = header.substring(11);
         }
      }

      summary.detail = shorten(stripPackage(root), 110);
      String lower = allHeaders.toLowerCase(Locale.ROOT);
      boolean mixinError = lower.contains("mixin") || lower.contains("injection");
      List<String> suspects = new ArrayList<>(frameMods);
      Matcher linkage = LINKAGE.matcher(allHeaders);
      if (mixinError && mixinMods.size() >= 2) {
         List<String> pair = new ArrayList<>(mixinMods);
         String a = name(mods, pair.get(0));
         String b = name(mods, pair.get(1));
         summary.title = a + " can't run alongside " + b;
         summary.subtitle = "Both mods change the same part of the game.";
         summary.advice = "Remove one of them from your mods folder, then restart the game.";
         suspects = pair.subList(0, 2);
      } else if (mixinError && !mixinMods.isEmpty()) {
         String a = name(mods, mixinMods.iterator().next());
         summary.title = a + " couldn't load";
         summary.subtitle = "It doesn't fit this version of Minecraft, or another mod changed the code it needs.";
         summary.advice = "Update " + a + ", or remove it from your mods folder, then restart the game.";
         suspects = new ArrayList<>(mixinMods).subList(0, 1);
      } else if (allHeaders.contains("OutOfMemoryError")) {
         summary.title = "Minecraft ran out of memory";
         summary.subtitle = "The game used up all the memory it was given.";
         summary.advice = "Give Minecraft more memory in your launcher's instance settings, then restart the game.";
         suspects = List.of();
      } else if (linkage.find()) {
         String missing = missingClass(allHeaders);
         String owner = missing == null ? null : mods.modOf(missing);
         String culprit = suspects.isEmpty() ? null : name(mods, suspects.get(0));
         summary.title = culprit == null ? "A mod is out of date" : culprit + " is out of date";
         if (owner == null || owner.isEmpty() || NOT_MODS.contains(owner) || culprit != null && owner.equals(suspects.get(0))) {
            if (missing == null || missing.startsWith("net.minecraft.") || owner != null && !owner.isEmpty()) {
               summary.subtitle = "It was made for a different version of Minecraft or of a mod it uses.";
            } else {
               summary.subtitle = "It needs another mod, or a version of one, that isn't installed.";
            }
         } else {
            summary.subtitle = "It was made for a different version of " + name(mods, owner) + ".";
         }

         summary.advice = (culprit == null ? "Update your mods" : "Update " + culprit + ", or remove it from your mods folder,") + " then restart the game.";
      } else if (!lower.contains("opengl")
         && !lower.contains("glfw error")
         && !lower.contains("pixel format not accelerated")
         && !lower.contains("exception_access_violation")) {
         if (!suspects.isEmpty()) {
            String culprit = name(mods, suspects.get(0));
            summary.title = culprit + " crashed the game";
            summary.subtitle = plainly(first);
            summary.advice = "Update " + culprit + " or remove it from your mods folder, then restart the game.";
         } else {
            summary.title = "Minecraft crashed";
            summary.subtitle = "Nothing in the crash points at a mod.";
            summary.advice = "Open the crash report to see the details.";
         }
      } else {
         String culprit = suspects.isEmpty() ? null : name(mods, suspects.get(0));
         summary.title = "Your graphics driver hit a problem";
         summary.subtitle = culprit == null ? "It happened while the game was drawing." : culprit + " was drawing when it happened.";
         summary.advice = "Update your graphics driver." + (culprit == null ? "" : " If it keeps happening, try removing " + culprit + ".");
      }

      for (String idx : suspects.subList(0, Math.min(3, suspects.size()))) {
         summary.suspects.add(new CrashSummary.Suspect(idx, name(mods, idx)));
      }

      return summary;
   }

   private static List<String> mainSection(String report) {
      String[] lines = report.replace("\r", "").split("\n");
      int start = 0;
      int end = lines.length;

      for (int i = 0; i < lines.length; i++) {
         if (lines[i].startsWith("Description:")) {
            start = i + 1;
            break;
         }
      }

      for (int ix = start; ix < lines.length; ix++) {
         if (lines[ix].startsWith("A detailed walkthrough")) {
            end = ix;
            break;
         }
      }

      return List.of(lines).subList(start, end);
   }

   private static boolean isFabricApi(String id) {
      return id.equals("fabric-api") || id.startsWith("fabric-") && !id.equals("fabric-language-kotlin");
   }

   private static String name(CrashAnalyzer.Mods mods, String id) {
      String name = null;

      try {
         name = mods.nameOf(id);
      } catch (RuntimeException var4) {
      }

      if (name == null && id.equals("fabric-api")) {
         return "Fabric API";
      } else {
         return name != null && !name.isBlank() ? name : id;
      }
   }

   private static String missingClass(String headers) {
      Matcher missing = Pattern.compile("(?:NoClassDefFoundError|ClassNotFoundException): ([\\w/.$]+)").matcher(headers);
      if (missing.find()) {
         return missing.group(1).replace('/', '.');
      } else {
         Matcher method = Pattern.compile("NoSuchMethodError: '[\\w.$\\[\\]<>]+ ([\\w.$]+)\\.[\\w$<>]+\\(").matcher(headers);
         return method.find() ? method.group(1) : null;
      }
   }

   private static String plainly(String header) {
      String h = header.startsWith("Caused by: ") ? header.substring(11) : header;
      if (h.contains("NullPointerException")) {
         return "It tried to use something that wasn't there.";
      } else if (h.contains("ConcurrentModificationException")) {
         return "It changed a list while the game was still reading it.";
      } else if (h.contains("IndexOutOfBounds")) {
         return "It looked past the end of a list.";
      } else if (h.contains("ClassCastException")) {
         return "It mixed up two different kinds of game object.";
      } else if (h.contains("StackOverflowError")) {
         return "It got stuck calling itself over and over.";
      } else {
         return !h.contains("IllegalStateException") && !h.contains("IllegalArgumentException")
            ? "It ran into an error it couldn't recover from."
            : "It ended up in a state it wasn't built to handle.";
      }
   }

   private static String stripPackage(String header) {
      int colon = header.indexOf(58);
      String type = colon < 0 ? header : header.substring(0, colon);
      int dot = type.lastIndexOf(46);
      return dot < 0 ? header : header.substring(dot + 1);
   }

   private static String shorten(String text, int max) {
      return text.length() <= max ? text : text.substring(0, max - 1) + "…";
   }

   public interface Mods {
      String modOf(String string);

      String nameOf(String string);
   }
}
