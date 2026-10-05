package dev.lyfw.lyfwclient.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.PlaytimeModule;
import dev.lyfw.lyfwclient.profile.ProfileStorage;
import dev.lyfw.lyfwclient.profile.PublicProfileData;
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
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public final class PublicProfiles {
   private static final Duration TIMEOUT = Duration.ofSeconds(8L);
   private static final int MAX_ENTRIES = 200;
   private static final PublicProfiles INSTANCE = new PublicProfiles();
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
   private volatile List<PublicProfiles.Entry> entries = List.of();
   private volatile String status = "";
   private volatile boolean busy;

   private PublicProfiles() {
   }

   public static PublicProfiles get() {
      return INSTANCE;
   }

   public static String endpoint() {
      return ModuleManager.get("Leaderboard") instanceof PlaytimeModule playtime
         ? playtime.endpointUrl()
         : "https://pip-client-d611b-default-rtdb.asia-southeast1.firebasedatabase.app";
   }

   public List<PublicProfiles.Entry> entries() {
      return this.entries;
   }

   public String status() {
      return this.status;
   }

   public boolean busy() {
      return this.busy;
   }

   public void refresh(String endpoint) {
      String url = trimEndpoint(endpoint);
      if (url.isEmpty()) {
         this.entries = List.of();
         this.status = "No host set";
      } else if (!this.busy) {
         this.run("Loading shared profiles...", () -> this.fetchIndex(url));
      }
   }

   public void publish(String endpoint, ProfileStorage.PublishPayload payload, Consumer<String> onShared) {
      String url = trimEndpoint(endpoint);
      Minecraft client = Minecraft.getInstance();
      UUID uuid = client.getUser() == null ? null : client.getUser().getProfileId();
      String author = client.getUser() == null ? "Player" : client.getUser().getName();
      if (url.isEmpty()) {
         this.status = "No host set";
      } else if (uuid == null) {
         this.status = "Sign in to share profiles";
      } else if (this.busy) {
         this.status = "Still working on the last one";
      } else if (payload.pip().length() > 400000) {
         this.status = "That profile is too big to share";
      } else {
         String name = payload.name().length() > 40 ? payload.name().substring(0, 40) : payload.name();
         String slug = PublicProfileData.slug(name);
         long created = System.currentTimeMillis();
         JsonObject body = new JsonObject();
         body.addProperty("name", name);
         body.addProperty("author", author);
         body.addProperty("created", created);
         body.addProperty("version", version());
         body.addProperty("pip", payload.pip());
         body.addProperty("options", payload.options());
         body.addProperty("keybinds", payload.keybinds());
         JsonObject index = new JsonObject();
         index.addProperty("name", name);
         index.addProperty("author", author);
         index.addProperty("created", created);
         this.run("Sharing \"" + name + "\"...", () -> {
            int code = this.send(URI.create(url + "/profiles/" + uuid + "/" + slug + ".json"), "PUT", body.toString());
            if (code / 100 != 2) {
               this.status = describeCode(code);
            } else {
               code = this.send(URI.create(url + "/profileIndex/" + uuid + "/" + slug + ".json"), "PUT", index.toString());
               if (code / 100 != 2) {
                  this.status = describeCode(code);
               } else {
                  this.status = "Shared \"" + name + "\" - anyone on Pip can copy it";
                  client.execute(() -> onShared.accept(slug));
                  this.fetchIndex(url);
               }
            }
         });
      }
   }

   public void unpublish(String endpoint, String uuid, String slug, Runnable onRemoved) {
      String url = trimEndpoint(endpoint);
      if (url.isEmpty()) {
         this.status = "No host set";
      } else if (this.busy) {
         this.status = "Still working on the last one";
      } else {
         this.run("Removing...", () -> {
            int code = this.send(URI.create(url + "/profileIndex/" + uuid + "/" + slug + ".json"), "DELETE", null);
            if (code / 100 != 2) {
               this.status = describeCode(code);
            } else {
               this.send(URI.create(url + "/profiles/" + uuid + "/" + slug + ".json"), "DELETE", null);
               this.status = "Stopped sharing it";
               Minecraft.getInstance().execute(onRemoved);
               this.fetchIndex(url);
            }
         });
      }
   }

   public void download(String endpoint, PublicProfiles.Entry entry, Consumer<JsonObject> onBody) {
      String url = trimEndpoint(endpoint);
      if (url.isEmpty()) {
         this.status = "No host set";
      } else if (this.busy) {
         this.status = "Still working on the last one";
      } else {
         this.run(
            "Copying \"" + entry.name() + "\"...",
            () -> {
               HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/profiles/" + entry.uuid() + "/" + entry.slug() + ".json"))
                  .timeout(TIMEOUT)
                  .GET()
                  .build();
               HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
               if (response.statusCode() / 100 != 2) {
                  this.status = describeCode(response.statusCode());
               } else {
                  JsonElement parsed = JsonParser.parseString(response.body());
                  if (parsed != null && parsed.isJsonObject()) {
                     this.status = "";
                     JsonObject body = parsed.getAsJsonObject();
                     Minecraft.getInstance().execute(() -> onBody.accept(body));
                  } else {
                     this.status = "That profile is gone";
                  }
               }
            }
         );
      }
   }

   private void fetchIndex(String url) throws Exception {
      HttpRequest request = HttpRequest.newBuilder(URI.create(url + "/profileIndex.json")).timeout(TIMEOUT).GET().build();
      HttpResponse<String> response = this.http.send(request, BodyHandlers.ofString());
      if (response.statusCode() / 100 != 2) {
         this.entries = List.of();
         this.status = describeCode(response.statusCode());
      } else {
         Minecraft client = Minecraft.getInstance();
         UUID self = client.getUser() == null ? null : client.getUser().getProfileId();
         List<PublicProfiles.Entry> fresh = parseIndex(response.body(), self == null ? "" : self.toString());
         this.entries = fresh;
         this.status = fresh.isEmpty() ? "Nobody has shared a profile yet" : "";
      }
   }

   private static List<PublicProfiles.Entry> parseIndex(String json, String self) {
      List<PublicProfiles.Entry> out = new ArrayList<>();
      JsonElement root = JsonParser.parseString(json);
      if (root != null && root.isJsonObject()) {
         for (Map.Entry<String, JsonElement> account : root.getAsJsonObject().entrySet()) {
            if (account.getValue().isJsonObject()) {
               for (Map.Entry<String, JsonElement> share : account.getValue().getAsJsonObject().entrySet()) {
                  if (share.getValue().isJsonObject()) {
                     JsonObject line = share.getValue().getAsJsonObject();
                     String name = text(line, "name");
                     long created = line.has("created") && line.get("created").isJsonPrimitive() && line.get("created").getAsJsonPrimitive().isNumber()
                        ? line.get("created").getAsLong()
                        : 0L;
                     out.add(
                        new PublicProfiles.Entry(
                           account.getKey(),
                           share.getKey(),
                           name.isEmpty() ? share.getKey() : name,
                           text(line, "author"),
                           created,
                           account.getKey().equals(self)
                        )
                     );
                  }
               }
            }
         }
      }

      out.sort(Comparator.comparingLong(PublicProfiles.Entry::created).reversed());
      return out.size() > 200 ? List.copyOf(out.subList(0, 200)) : out;
   }

   public static String text(JsonObject object, String key) {
      return object.has(key) && object.get(key).isJsonPrimitive() && object.get(key).getAsJsonPrimitive().isString() ? object.get(key).getAsString() : "";
   }

   private int send(URI uri, String method, String body) throws Exception {
      HttpRequest request = HttpRequest.newBuilder(uri)
         .timeout(TIMEOUT)
         .header("Content-Type", "application/json")
         .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body))
         .build();
      return this.http.send(request, BodyHandlers.discarding()).statusCode();
   }

   private void run(String working, PublicProfiles.NetworkTask task) {
      this.busy = true;
      this.status = working;
      Thread thread = new Thread(() -> {
         try {
            task.run();
         } catch (Exception var6) {
            this.status = explain(var6);
         } finally {
            this.busy = false;
         }
      }, "Pip Client Public Profiles");
      thread.setDaemon(true);
      thread.start();
   }

   private static String version() {
      return FabricLoader.getInstance().getModContainer("lyfw-client").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
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

   private static String explain(Exception e) {
      if (e instanceof UnknownHostException) {
         return "No such address";
      } else if (e instanceof ConnectException) {
         return "Could not connect";
      } else if (e instanceof HttpTimeoutException) {
         return "The server did not answer in time";
      } else {
         return e instanceof JsonParseException ? "What came back was not readable" : "Could not reach the server";
      }
   }

   private static String describeCode(int code) {
      return switch (code) {
         case 401, 403 -> "Sharing is not switched on for this server yet";
         case 404 -> "That profile is gone";
         case 413 -> "That profile is too big to share";
         default -> "The server answered " + code;
      };
   }

   public record Entry(String uuid, String slug, String name, String author, long created, boolean mine) {
   }

   private interface NetworkTask {
      void run() throws Exception;
   }
}
