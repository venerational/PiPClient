package dev.lyfw.lyfwclient.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class AlbumArt {
   private static final Duration TIMEOUT = Duration.ofSeconds(8L);
   private static final String SIZE = "256x256bb.jpg";
   private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(TIMEOUT).followRedirects(Redirect.NORMAL).build();
   private static final Map<String, Identifier> CACHE = new HashMap<>();
   private static final Set<String> PENDING = new HashSet<>();
   private static final Set<String> MISSING = new HashSet<>();
   private static int generation;

   private AlbumArt() {
   }

   private static String key(String artist, String album) {
      return (artist == null ? "" : artist.trim().toLowerCase()) + "\u0000" + (album == null ? "" : album.trim().toLowerCase());
   }

   public static Identifier get(String artist, String album, String title) {
      if (artist != null && !artist.isBlank()) {
         String key = key(artist, album + "|" + title);
         synchronized (CACHE) {
            Identifier cached = CACHE.get(key);
            if (cached != null) {
               return cached;
            }

            if (MISSING.contains(key) || PENDING.contains(key)) {
               return null;
            }

            PENDING.add(key);
         }

         fetch(key, artist, album, title);
         return null;
      } else {
         return null;
      }
   }

   private static void fetch(String key, String artist, String album, String title) {
      Thread thread = new Thread(() -> {
         try {
            String url = lookup(artist, album, title);
            if (url == null) {
               markMissing(key);
            } else {
               HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(TIMEOUT).GET().build();
               HttpResponse<byte[]> response = HTTP.send(request, BodyHandlers.ofByteArray());
               if (response.statusCode() / 100 != 2) {
                  markMissing(key);
               } else {
                  byte[] bytes = response.body();
                  Minecraft.getInstance().execute(() -> register(key, bytes));
               }
            }
         } catch (Exception var8) {
            markMissing(key);
         }
      }, "Pip Client Album Art");
      thread.setDaemon(true);
      thread.start();
   }

   private static String lookup(String artist, String album, String title) throws Exception {
      String byTitle = search("song", artist + " " + (title == null ? "" : title), artist);
      return byTitle != null ? byTitle : search("album", artist + " " + (album == null ? "" : album), artist);
   }

   private static String search(String entity, String term, String wanted) throws Exception {
      String trimmed = term.trim();
      if (trimmed.isEmpty()) {
         return null;
      } else {
         String query = "https://itunes.apple.com/search?entity=" + entity + "&limit=8&term=" + URLEncoder.encode(trimmed, StandardCharsets.UTF_8);
         HttpRequest request = HttpRequest.newBuilder(URI.create(query)).timeout(TIMEOUT).GET().build();
         HttpResponse<String> response = HTTP.send(request, BodyHandlers.ofString());
         if (response.statusCode() / 100 != 2) {
            return null;
         } else {
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray results = root.getAsJsonArray("results");
            if (results == null) {
               return null;
            } else {
               for (int i = 0; i < results.size(); i++) {
                  JsonObject entry = results.get(i).getAsJsonObject();
                  if (entry.has("artworkUrl100") && entry.has("artistName") && sameArtist(entry.get("artistName").getAsString(), wanted)) {
                     return entry.get("artworkUrl100").getAsString().replace("100x100bb.jpg", "256x256bb.jpg");
                  }
               }

               return null;
            }
         }
      }
   }

   private static boolean sameArtist(String found, String wanted) {
      String a = normalise(found);
      String b = normalise(wanted);
      return !a.isEmpty() && !b.isEmpty() && (a.contains(b) || b.contains(a));
   }

   private static String normalise(String name) {
      return name == null ? "" : name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
   }

   private static void register(String key, byte[] bytes) {
      try {
         BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(bytes));
         if (decoded == null) {
            markMissing(key);
            return;
         }

         NativeImage image = toNative(decoded);
         String name = "album_art_" + generation++;
         Identifier id = Identifier.fromNamespaceAndPath("lyfw-client", name);
         Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> "lyfw-client/" + name, image));
         synchronized (CACHE) {
            CACHE.put(key, id);
            PENDING.remove(key);
         }
      } catch (Exception var9) {
         markMissing(key);
      }
   }

   private static NativeImage toNative(BufferedImage source) {
      int w = source.getWidth();
      int h = source.getHeight();
      int[] pixels = source.getRGB(0, 0, w, h, null, 0, w);
      NativeImage image = new NativeImage(w, h, false);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            image.setPixel(x, y, 0xFF000000 | pixels[y * w + x] & 16777215);
         }
      }

      return image;
   }

   private static void markMissing(String key) {
      synchronized (CACHE) {
         PENDING.remove(key);
         MISSING.add(key);
      }
   }

   public static void forget() {
      synchronized (CACHE) {
         MISSING.clear();
      }
   }
}
