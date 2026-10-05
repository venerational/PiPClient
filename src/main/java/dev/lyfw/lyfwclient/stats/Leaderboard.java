package dev.lyfw.lyfwclient.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public final class Leaderboard {
   public static final String DEFAULT_ENDPOINT = "https://pip-client-d611b-default-rtdb.asia-southeast1.firebasedatabase.app";
   private static final long SUBMIT_INTERVAL_MS = 300000L;
   private static final Duration TIMEOUT = Duration.ofSeconds(8L);
   private static final int MAX_ROWS = 100;
   private static final Leaderboard INSTANCE = new Leaderboard();
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
   private volatile List<Leaderboard.Entry> entries = List.of();
   private volatile String status = "";
   private volatile long lastSubmitAt;
   private volatile boolean busy;

   private Leaderboard() {
   }

   public static Leaderboard get() {
      return INSTANCE;
   }

   public List<Leaderboard.Entry> entries() {
      return this.entries;
   }

   public String status() {
      return this.status;
   }

   public boolean busy() {
      return this.busy;
   }

   private static String trimEndpoint(String endpoint) {
      String url = endpoint == null ? "" : endpoint.trim();

      while (url.endsWith("/")) {
         url = url.substring(0, url.length() - 1);
      }

      if (!url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://")) {
         url = "https://" + url;
      }

      return url;
   }

   public void submitIfDue(String endpoint) {
      String url = trimEndpoint(endpoint);
      if (!url.isEmpty() && !this.busy) {
         long now = System.currentTimeMillis();
         if (now - this.lastSubmitAt >= 300000L && PlaytimeTracker.get().sessionWorthReporting()) {
            this.lastSubmitAt = now;
            this.submit(url);
         }
      }
   }

   public void submit(String endpoint) {
      String url = trimEndpoint(endpoint);
      if (!url.isEmpty()) {
         Minecraft client = Minecraft.getInstance();
         UUID uuid = client.getUser() == null ? null : client.getUser().getProfileId();
         String name = client.getUser() == null ? "Player" : client.getUser().getName();
         if (uuid != null) {
            JsonObject body = new JsonObject();
            body.addProperty("name", name);
            body.addProperty("seconds", PlaytimeTracker.get().totalSeconds());
            body.addProperty("updated", System.currentTimeMillis());
            this.run(
               () -> {
                  HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/players/" + uuid + ".json"))
                     .timeout(TIMEOUT)
                     .header("Content-Type", "application/json")
                     .PUT(BodyPublishers.ofString(body.toString()))
                     .build();
                  HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
                  if (response.statusCode() / 100 != 2) {
                     this.status = describeCode(response.statusCode());
                  } else {
                     this.status = "";
                     this.fetch(url, uuid);
                  }
               }
            );
         }
      }
   }

   public void refresh(String endpoint) {
      String url = trimEndpoint(endpoint);
      if (url.isEmpty()) {
         this.entries = List.of();
         this.status = "No leaderboard host set";
      } else if (!this.busy) {
         Minecraft client = Minecraft.getInstance();
         UUID uuid = client.getUser() == null ? null : client.getUser().getProfileId();
         this.run(() -> this.fetch(url, uuid));
      }
   }

   private void fetch(String url, UUID self) throws Exception {
      HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/players.json")).timeout(TIMEOUT).GET().build();
      HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
      if (response.statusCode() / 100 != 2) {
         this.status = describeCode(response.statusCode());
      } else {
         this.status = "";
         this.parse(response.body(), self);
      }
   }

   private void run(Leaderboard.NetworkTask task) {
      this.busy = true;
      this.status = "Contacting the board...";
      Thread thread = new Thread(() -> {
         try {
            task.run();
         } catch (Exception var6) {
            this.status = explain(var6);
         } finally {
            this.busy = false;
         }
      }, "Pip Client Leaderboard");
      thread.setDaemon(true);
      thread.start();
   }

   private static String explain(Exception e) {
      if (e instanceof UnknownHostException) {
         return "No such address";
      } else if (e instanceof ConnectException) {
         return "Could not connect";
      } else {
         return e instanceof HttpTimeoutException ? "The board did not answer in time" : "Could not reach the board";
      }
   }

   private static String describeCode(int code) {
      return switch (code) {
         case 401, 403 -> "The board refused that";
         default -> "The board answered " + code;
         case 404 -> "The board is not there any more";
      };
   }

   private void parse(String json, UUID self) {
      try {
         JsonElement root = JsonParser.parseString(json);
         if (root == null || root.isJsonNull()) {
            this.entries = List.of();
            this.status = "Nobody on the board yet";
            return;
         }

         List<Leaderboard.Entry> rows = new ArrayList<>();
         String selfId = self == null ? "" : self.toString();

         for (Map.Entry<String, JsonElement> row : root.getAsJsonObject().entrySet()) {
            if (row.getValue().isJsonObject()) {
               JsonObject player = row.getValue().getAsJsonObject();
               String name = player.has("name") ? player.get("name").getAsString() : "?";
               long seconds = player.has("seconds") ? player.get("seconds").getAsLong() : 0L;
               rows.add(new Leaderboard.Entry(0, name, seconds, row.getKey().equalsIgnoreCase(selfId)));
            }
         }

         rows.sort(Comparator.comparingLong(Leaderboard.Entry::seconds).reversed());
         List<Leaderboard.Entry> ranked = new ArrayList<>();

         for (int i = 0; i < Math.min(rows.size(), 100); i++) {
            Leaderboard.Entry rowx = rows.get(i);
            ranked.add(new Leaderboard.Entry(i + 1, rowx.name(), rowx.seconds(), rowx.self()));
         }

         this.entries = List.copyOf(ranked);
         if (ranked.isEmpty()) {
            this.status = "Nobody on the board yet";
         }
      } catch (Exception var12) {
         this.status = "The board sent something unexpected";
      }
   }

   public record Entry(int rank, String name, long seconds, boolean self) {
   }

   @FunctionalInterface
   private interface NetworkTask {
      void run() throws Exception;
   }
}
