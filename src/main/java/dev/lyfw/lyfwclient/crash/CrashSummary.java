package dev.lyfw.lyfwclient.crash;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CrashSummary {
   public static final String FILE = "pip-crash-summary.txt";
   public static final String PENDING = "pip-crash-pending";
   public String title = "Minecraft crashed";
   public String subtitle = "";
   public String advice = "";
   public String detail = "";
   public String reportPath = "";
   public String modsDir = "";
   public final List<CrashSummary.Suspect> suspects = new ArrayList<>();

   public void write(Path file) throws IOException {
      StringBuilder out = new StringBuilder();
      line(out, "title", this.title);
      line(out, "subtitle", this.subtitle);
      line(out, "advice", this.advice);
      line(out, "detail", this.detail);
      line(out, "report", this.reportPath);
      line(out, "mods", this.modsDir);

      for (CrashSummary.Suspect suspect : this.suspects) {
         line(out, "suspect", suspect.id() + "|" + suspect.name());
      }

      Files.writeString(file, out.toString(), StandardCharsets.UTF_8);
   }

   public static CrashSummary read(Path file) throws IOException {
      CrashSummary summary = new CrashSummary();

      for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
         int eq = raw.indexOf(61);
         if (eq > 0) {
            String value = unescape(raw.substring(eq + 1));
            String var6 = raw.substring(0, eq);
            switch (var6) {
               case "title":
                  summary.title = value;
                  break;
               case "subtitle":
                  summary.subtitle = value;
                  break;
               case "advice":
                  summary.advice = value;
                  break;
               case "detail":
                  summary.detail = value;
                  break;
               case "report":
                  summary.reportPath = value;
                  break;
               case "mods":
                  summary.modsDir = value;
                  break;
               case "suspect":
                  int bar = value.indexOf(124);
                  if (bar > 0) {
                     summary.suspects.add(new CrashSummary.Suspect(value.substring(0, bar), value.substring(bar + 1)));
                  }
            }
         }
      }

      return summary;
   }

   private static void line(StringBuilder out, String key, String value) {
      out.append(key).append('=').append(value == null ? "" : value.replace("\\", "\\\\").replace("\r", "").replace("\n", "\\n")).append('\n');
   }

   private static String unescape(String value) {
      StringBuilder out = new StringBuilder(value.length());

      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         if (c == '\\' && i + 1 < value.length()) {
            char next = value.charAt(++i);
            out.append(next == 'n' ? '\n' : next);
         } else {
            out.append(c);
         }
      }

      return out.toString();
   }

   public record Suspect(String id, String name) {
   }
}
