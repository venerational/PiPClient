package dev.lyfw.lyfwclient.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import dev.lyfw.lyfwclient.render.CosmeticLoadout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;

public final class CosmeticSync {
   private static final long REFRESH_MS = 30000L;
   private static final long FORGET_MS = 300000L;
   private static final long SETTLE_MS = 2000L;
   private static final long RETRY_MS = 60000L;
   private static final long REFUSED_MS = 300000L;
   private static final int MAX_LENGTH = 4000;
   private static final Duration TIMEOUT = Duration.ofSeconds(8L);
   private static final CosmeticSync INSTANCE = new CosmeticSync();
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
   private final ExecutorService network = Executors.newFixedThreadPool(2, task -> {
      Thread thread = new Thread(task, "Pip Client Cosmetics");
      thread.setDaemon(true);
      return thread;
   });
   private final Map<UUID, CosmeticSync.Remote> remote = new ConcurrentHashMap<>();
   private final Set<UUID> reading = ConcurrentHashMap.newKeySet();
   private volatile String endpoint = "";
   private volatile String status = "";
   private volatile long refusedUntil;
   private volatile boolean writing;
   private volatile String sent;
   private String waiting;
   private long waitingSince;
   private volatile long lastFailure;

   private CosmeticSync() {
   }

   public static CosmeticSync get() {
      return INSTANCE;
   }

   public String status() {
      return this.status;
   }

   public void tick(String host, String mine) {
      String url = host == null ? "" : host.trim();

      while (url.endsWith("/")) {
         url = url.substring(0, url.length() - 1);
      }

      this.endpoint = url;
      long now = System.currentTimeMillis();
      this.remote.values().removeIf(entry -> now - entry.asked() > 300000L);
      if (!url.isEmpty() && mine != null && mine.length() <= 4000) {
         if (!mine.equals(this.waiting)) {
            this.waiting = mine;
            this.waitingSince = now;
         }

         boolean settled = now - this.waitingSince >= 2000L;
         boolean due = now >= this.refusedUntil && now - this.lastFailure >= 60000L;
         if (!mine.equals(this.sent) && settled && due && !this.writing) {
            this.write(url, mine);
         }
      }
   }

   private void write(String url, String loadout) {
      Minecraft client = Minecraft.getInstance();
      UUID uuid = client.getUser() == null ? null : client.getUser().getProfileId();
      if (uuid != null) {
         JsonObject body = new JsonObject();
         body.addProperty("loadout", loadout);
         body.addProperty("updated", System.currentTimeMillis());
         this.writing = true;
         this.network
            .execute(
               () -> {
                  try {
                     HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/cosmetics/" + uuid + ".json"))
                        .timeout(TIMEOUT)
                        .header("Content-Type", "application/json")
                        .PUT(BodyPublishers.ofString(body.toString()))
                        .build();
                     HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
                     if (response.statusCode() / 100 == 2) {
                        this.sent = loadout;
                        this.status = "";
                     } else {
                        this.refused(response.statusCode());
                     }
                  } catch (Exception var10) {
                     this.lastFailure = System.currentTimeMillis();
                     this.status = "Could not reach the database to share your cosmetics";
                  } finally {
                     this.writing = false;
                  }
               }
            );
      }
   }

   private void refused(int code) {
      this.lastFailure = System.currentTimeMillis();
      if (code != 401 && code != 403) {
         this.status = "The database turned your cosmetics down (" + code + ")";
      } else {
         this.refusedUntil = System.currentTimeMillis() + 300000L;
         this.status = "Sharing cosmetics needs the database rules updated";
      }
   }

   public CosmeticLoadout of(UUID uuid) {
      if (uuid != null && Presence.get().of(uuid) != null) {
         long now = System.currentTimeMillis();
         CosmeticSync.Remote known = this.remote.get(uuid);
         if (known != null && now - known.asked() > 1000L) {
            known = new CosmeticSync.Remote(known.loadout(), known.fetched(), now);
            this.remote.put(uuid, known);
         }

         String url = this.endpoint;
         if ((known == null || now - known.fetched() >= 30000L) && !url.isEmpty() && now >= this.refusedUntil && this.reading.add(uuid)) {
            this.network.execute(() -> this.read(url, uuid));
         }

         return known == null ? null : known.loadout();
      } else {
         return null;
      }
   }

   private void read(String url, UUID uuid) {
      long now = System.currentTimeMillis();
      CosmeticLoadout loadout = null;
      boolean answered = false;

      try {
         HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/cosmetics/" + uuid + ".json")).timeout(TIMEOUT).GET().build();
         HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
         if (response.statusCode() / 100 == 2) {
            answered = true;
            loadout = parse(response.body());
         } else if (response.statusCode() == 401 || response.statusCode() == 403) {
            this.refusedUntil = now + 300000L;
            this.status = "Seeing other players' cosmetics needs the database rules updated";
         }
      } catch (Exception var14) {
      } finally {
         CosmeticSync.Remote previous = this.remote.get(uuid);
         CosmeticLoadout keep = answered ? loadout : (previous == null ? null : previous.loadout());
         this.remote.put(uuid, new CosmeticSync.Remote(keep, now, previous == null ? now : previous.asked()));
         this.reading.remove(uuid);
      }
   }

   public static CosmeticLoadout parse(String json) {
      try {
         JsonElement root = JsonParser.parseString(json == null ? "" : json);
         if (root != null && root.isJsonObject()) {
            JsonElement loadout = root.getAsJsonObject().get("loadout");
            if (loadout != null && loadout.isJsonPrimitive() && loadout.getAsJsonPrimitive().isString()) {
               CosmeticsModule cosmetics = CosmeticsModule.get();
               Set<String> minecraft = (Set<String>)(cosmetics == null ? Set.of() : new HashSet<>(cosmetics.minecraftCapeNames()));
               Set<String> animated = (Set<String>)(cosmetics == null ? Set.of() : new HashSet<>(cosmetics.animatedCapeNames()));
               return CosmeticLoadout.decode(loadout.getAsString(), minecraft, animated);
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (RuntimeException var6) {
         return null;
      }
   }

   private record Remote(CosmeticLoadout loadout, long fetched, long asked) {
   }
}
