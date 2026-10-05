package dev.lyfw.lyfwclient.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;

public final class Presence {
   private static final long STALE_MS = 90000L;
   private static final long PUBLISH_INTERVAL_MS = 20000L;
   private static final long FETCH_INTERVAL_MS = 20000L;
   private static final Duration TIMEOUT = Duration.ofSeconds(8L);
   private static final Presence INSTANCE = new Presence();
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
   private final Map<UUID, Presence.Entry> online = new ConcurrentHashMap<>();
   private volatile boolean busy;
   private volatile String status = "";
   private long lastPublish;
   private long lastFetch;
   private String lastPublished = "";

   private Presence() {
   }

   public static Presence get() {
      return INSTANCE;
   }

   public String status() {
      return this.status;
   }

   public Presence.Entry of(UUID uuid) {
      return this.online.get(uuid);
   }

   public List<Presence.Entry> everyone() {
      List<Presence.Entry> list = new ArrayList<>(this.online.values());
      list.sort(Comparator.<Presence.Entry, Boolean>comparing(row -> row.song().isEmpty()).thenComparing(Presence.Entry::name, String.CASE_INSENSITIVE_ORDER));
      return list;
   }

   private static String trim(String endpoint) {
      String url = endpoint == null ? "" : endpoint.trim();

      while (url.endsWith("/")) {
         url = url.substring(0, url.length() - 1);
      }

      return url;
   }

   public void tick(String endpoint, String song) {
      String url = trim(endpoint);
      if (!url.isEmpty() && !this.busy) {
         long now = System.currentTimeMillis();
         String share = song == null ? "" : song;
         boolean changed = !share.equals(this.lastPublished);
         if (changed || now - this.lastPublish >= 20000L) {
            this.lastPublish = now;
            this.lastPublished = share;
            this.publish(url, share);
         } else if (now - this.lastFetch >= 20000L) {
            this.lastFetch = now;
            this.run(() -> this.fetch(url));
         }
      }
   }

   private void publish(String url, String song) {
      Minecraft client = Minecraft.getInstance();
      UUID uuid = client.getUser() == null ? null : client.getUser().getProfileId();
      String name = client.getUser() == null ? "Player" : client.getUser().getName();
      if (uuid != null) {
         JsonObject body = new JsonObject();
         body.addProperty("name", name);
         body.addProperty("song", song);
         body.addProperty("updated", System.currentTimeMillis());
         this.run(
            () -> {
               HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/presence/" + uuid + ".json"))
                  .timeout(TIMEOUT)
                  .header("Content-Type", "application/json")
                  .PUT(BodyPublishers.ofString(body.toString()))
                  .build();
               HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
               if (response.statusCode() / 100 != 2) {
                  this.status = "Presence rejected (" + response.statusCode() + ")";
               } else {
                  this.status = "";
                  this.fetch(url);
               }
            }
         );
      }
   }

   private void fetch(String url) throws Exception {
      HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/presence.json")).timeout(TIMEOUT).GET().build();
      HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
      if (response.statusCode() / 100 != 2) {
         this.status = "Presence unreadable (" + response.statusCode() + ")";
      } else {
         this.status = "";
         this.parse(response.body());
      }
   }

   private void parse(String json) {
      Map<UUID, Presence.Entry> fresh = new HashMap<>();

      try {
         JsonElement root = JsonParser.parseString(json == null ? "" : json);
         if (root != null && root.isJsonObject()) {
            long now = System.currentTimeMillis();

            for (Map.Entry<String, JsonElement> row : root.getAsJsonObject().entrySet()) {
               if (row.getValue().isJsonObject()) {
                  JsonObject value = row.getValue().getAsJsonObject();
                  long updated = value.has("updated") ? value.get("updated").getAsLong() : 0L;
                  if (now - updated <= 90000L) {
                     try {
                        UUID id = UUID.fromString(row.getKey());
                        String name = value.has("name") ? value.get("name").getAsString() : "?";
                        String song = value.has("song") ? value.get("song").getAsString() : "";
                        fresh.put(id, new Presence.Entry(id, name, song));
                     } catch (IllegalArgumentException var14) {
                     }
                  }
               }
            }
         }
      } catch (RuntimeException var15) {
         this.status = "Presence data unreadable";
         return;
      }

      this.online.keySet().retainAll(fresh.keySet());
      this.online.putAll(fresh);
   }

   private void run(Presence.NetworkTask task) {
      this.busy = true;
      Thread thread = new Thread(() -> {
         try {
            task.run();
         } catch (Exception var6) {
            this.status = var6.getClass().getSimpleName();
         } finally {
            this.busy = false;
         }
      }, "Pip Client Presence");
      thread.setDaemon(true);
      thread.start();
   }

   public record Entry(UUID id, String name, String song) {
   }

   @FunctionalInterface
   private interface NetworkTask {
      void run() throws Exception;
   }
}
