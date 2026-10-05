package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.w3c.dom.Node;

public final class PadImage {
   private static final int MAX_EDGE = 256;
   private static final long FRAME_BUDGET_BYTES = 50331648L;
   private static final int MAX_FRAMES = 256;
   private static final int DEFAULT_DELAY_MS = 100;
   private final List<Identifier> frames;
   private final List<Integer> delays;
   private final int totalMs;
   private static final Map<String, PadImage> REMOTE = new HashMap<>();
   private static final Set<String> FETCHING = new HashSet<>();
   private static final Set<String> FAILED = new HashSet<>();
   private static final int MAX_DOWNLOAD_BYTES = 33554432;
   private static final Pattern CONTENT = Pattern.compile("content\\s*=\\s*[\"']([^\"']+)[\"']", 2);

   private PadImage(List<Identifier> frames, List<Integer> delays) {
      this.frames = frames;
      this.delays = delays;
      int total = 0;

      for (int delay : delays) {
         total += delay;
      }

      this.totalMs = Math.max(1, total);
   }

   public boolean animated() {
      return this.frames.size() > 1;
   }

   public int frameCount() {
      return this.frames.size();
   }

   public Identifier currentFrame() {
      if (this.frames.size() == 1) {
         return this.frames.get(0);
      } else {
         long into = System.currentTimeMillis() % this.totalMs;
         int elapsed = 0;

         for (int i = 0; i < this.frames.size(); i++) {
            elapsed += this.delays.get(i);
            if (into < elapsed) {
               return this.frames.get(i);
            }
         }

         return this.frames.get(this.frames.size() - 1);
      }
   }

   public static PadImage load(Path path, String idBase) {
      return load(path, idBase, 256);
   }

   public static PadImage load(Path path, String idBase, int maxEdge) {
      try {
         byte[] bytes = Files.readAllBytes(path);
         PadImage result = isGif(bytes) ? loadGif(bytes, idBase, maxEdge) : loadStill(bytes, idBase);
         if (result != null) {
            System.out
               .println(
                  "[Pip Client] background image: "
                     + path.getFileName()
                     + " - "
                     + (result.frameCount() == 1 ? "still" : result.frameCount() + " frames, " + result.totalMs / 1000.0F + "s loop")
               );
         }

         return result;
      } catch (Exception var5) {
         System.out.println("[Pip Client] background image could not be read: " + path + " (" + var5 + ")");
         return null;
      }
   }

   public static boolean isUrl(String source) {
      String s = source == null ? "" : source.trim().toLowerCase(Locale.ROOT);
      return s.startsWith("http://") || s.startsWith("https://");
   }

   public static PadImage fromUrl(String url, String idBase) {
      return fromUrl(url, idBase, 256);
   }

   public static PadImage fromUrl(String url, String idBase, int maxEdge) {
      synchronized (REMOTE) {
         PadImage cached = REMOTE.get(url);
         if (cached != null) {
            return cached;
         }

         if (FAILED.contains(url) || FETCHING.contains(url)) {
            return null;
         }

         FETCHING.add(url);
      }

      Thread thread = new Thread(
         () -> {
            try {
               HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(Redirect.NORMAL).build();
               byte[] bytes = download(http, url);
               if (!looksLikeImage(bytes)) {
                  String pointed = pageImageLink(bytes);
                  if (pointed == null) {
                     fail(url, describe(bytes));
                     return;
                  }

                  System.out.println("[Pip Client] that link is a page; using the picture it points at: " + pointed);
                  bytes = download(http, pointed);
                  if (!looksLikeImage(bytes)) {
                     fail(url, describe(bytes));
                     return;
                  }
               }

               byte[] decoded = bytes;
               Minecraft.getInstance()
                  .execute(
                     () -> {
                        try {
                           PadImage image = read(decoded, idBase, maxEdge);
                           synchronized (REMOTE) {
                              FETCHING.remove(url);
                              if (image == null) {
                                 FAILED.add(url);
                              } else {
                                 REMOTE.put(url, image);
                              }
                           }

                           if (image != null) {
                              System.out
                                 .println(
                                    "[Pip Client] background image from link: "
                                       + (image.frameCount() == 1 ? "still" : image.frameCount() + " frames, " + image.totalMs / 1000.0F + "s loop")
                                 );
                           }
                        } catch (Exception var8) {
                           fail(url, String.valueOf(var8));
                        }
                     }
                  );
            } catch (Exception var6) {
               fail(url, var6.getMessage() == null ? String.valueOf(var6) : var6.getMessage());
            }
         },
         "Pip Client Pad Image"
      );
      thread.setDaemon(true);
      thread.start();
      return null;
   }

   private static void fail(String url, String why) {
      synchronized (REMOTE) {
         FETCHING.remove(url);
         FAILED.add(url);
      }

      System.out.println("[Pip Client] Could not use " + url + " as a background: " + why);
   }

   private static byte[] download(HttpClient http, String url) throws Exception {
      HttpRequest request = HttpRequest.newBuilder(URI.create(url))
         .timeout(Duration.ofSeconds(30L))
         .header("User-Agent", "Mozilla/5.0 (compatible; PipClient)")
         .GET()
         .build();
      HttpResponse<byte[]> response = http.send(request, BodyHandlers.ofByteArray());
      if (response.statusCode() / 100 != 2) {
         throw new IOException(
            response.statusCode() == 404
               ? "the server said 404 - the link has expired or been deleted (Discord attachment links do expire)"
               : "the server said " + response.statusCode()
         );
      } else if (response.body().length > 33554432) {
         throw new IOException("it is over 32MB");
      } else {
         return response.body();
      }
   }

   private static boolean looksLikeImage(byte[] bytes) {
      return isGif(bytes) ? true : bytes.length > 8 && (bytes[0] & 255) == 137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71;
   }

   private static String pageImageLink(byte[] body) {
      String html = new String(body, 0, Math.min(body.length, 524288), StandardCharsets.UTF_8);

      for (String key : new String[]{"og:image", "twitter:image"}) {
         Matcher tag = Pattern.compile("<meta[^>]*(?:property|name)\\s*=\\s*[\"']" + key + "[\"'][^>]*>", 2).matcher(html);

         while (tag.find()) {
            Matcher content = CONTENT.matcher(tag.group());
            if (content.find()) {
               String found = content.group(1).replace("&amp;", "&").trim();
               if (isUrl(found)) {
                  return found;
               }
            }
         }
      }

      return null;
   }

   private static String describe(byte[] body) {
      String head = new String(body, 0, Math.min(body.length, 200), StandardCharsets.UTF_8).trim().toLowerCase(Locale.ROOT);
      return !head.startsWith("<!doctype html") && !head.startsWith("<html")
         ? "it is not a GIF or a PNG"
         : "that link is a web page, not a picture - right-click the image itself and copy its link";
   }

   public static void forgetFailures() {
      synchronized (REMOTE) {
         FAILED.clear();
      }
   }

   public static PadImage read(byte[] bytes, String idBase) throws Exception {
      return read(bytes, idBase, 256);
   }

   public static PadImage read(byte[] bytes, String idBase, int maxEdge) throws Exception {
      return isGif(bytes) ? loadGif(bytes, idBase, maxEdge) : loadStill(bytes, idBase);
   }

   private static boolean isGif(byte[] bytes) {
      return bytes.length > 3 && bytes[0] == 71 && bytes[1] == 73 && bytes[2] == 70;
   }

   private static PadImage loadStill(byte[] bytes, String idBase) throws Exception {
      NativeImage image = NativeImage.read(bytes);
      return new PadImage(List.of(register(image, idBase, 0)), List.of(100));
   }

   private static PadImage loadGif(byte[] bytes, String idBase, int maxEdge) throws Exception {
      PadImage var36;
      try (
         InputStream in = new ByteArrayInputStream(bytes);
         ImageInputStream stream = ImageIO.createImageInputStream(in);
      ) {
         Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
         if (!readers.hasNext()) {
            return null;
         }

         ImageReader reader = readers.next();
         reader.setInput(stream);
         int count = Math.min(reader.getNumImages(true), 256);
         if (count <= 0) {
            return null;
         }

         int width = reader.getWidth(0);
         int height = reader.getHeight(0);
         int scaledW = Math.max(1, width);
         int scaledH = Math.max(1, height);
         if (Math.max(scaledW, scaledH) > maxEdge) {
            float factor = (float)maxEdge / Math.max(scaledW, scaledH);
            scaledW = Math.max(1, Math.round(scaledW * factor));
            scaledH = Math.max(1, Math.round(scaledH * factor));
         }

         long perFrame = (long)scaledW * scaledH * 4L;
         int affordable = (int)Math.max(1L, 50331648L / Math.max(1L, perFrame));
         int stride = Math.max(1, (count + affordable - 1) / affordable);
         BufferedImage canvas = new BufferedImage(width, height, 2);
         Graphics2D g = canvas.createGraphics();
         List<Identifier> ids = new ArrayList<>();
         List<Integer> delays = new ArrayList<>();
         int pendingDelay = 0;

         for (int i = 0; i < count; i++) {
            BufferedImage frame = reader.read(i);
            int[] meta = frameMeta(reader, i);
            int left = meta[0];
            int top = meta[1];
            int delay = meta[2];
            int disposal = meta[3];
            g.drawImage(frame, left, top, null);
            pendingDelay += delay <= 0 ? 100 : delay;
            if (i % stride == 0) {
               ids.add(register(toNative(scale(canvas, scaledW, scaledH)), idBase, ids.size()));
               delays.add(pendingDelay);
               pendingDelay = 0;
            }

            if (disposal == 2) {
               Composite previous = g.getComposite();
               g.setComposite(AlphaComposite.Clear);
               g.fillRect(left, top, frame.getWidth(), frame.getHeight());
               g.setComposite(previous);
            }
         }

         g.dispose();
         reader.dispose();
         if (pendingDelay > 0 && !delays.isEmpty()) {
            delays.set(delays.size() - 1, delays.get(delays.size() - 1) + pendingDelay);
         }

         var36 = ids.isEmpty() ? null : new PadImage(ids, delays);
      }

      return var36;
   }

   private static int[] frameMeta(ImageReader reader, int index) {
      int left = 0;
      int top = 0;
      int delay = 100;
      int disposal = 0;

      try {
         IIOMetadata metadata = reader.getImageMetadata(index);
         IIOMetadataNode root = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());

         for (int i = 0; i < root.getLength(); i++) {
            Node node = root.item(i);
            String name = node.getNodeName();
            if ("ImageDescriptor".equalsIgnoreCase(name)) {
               left = intAttr(node, "imageLeftPosition", 0);
               top = intAttr(node, "imageTopPosition", 0);
            } else if ("GraphicControlExtension".equalsIgnoreCase(name)) {
               delay = intAttr(node, "delayTime", 10) * 10;
               String method = attr(node, "disposalMethod");
               disposal = method != null && method.startsWith("restoreToBackground") ? 2 : 0;
            }
         }
      } catch (Exception var12) {
      }

      return new int[]{left, top, delay, disposal};
   }

   private static String attr(Node node, String name) {
      Node item = node.getAttributes().getNamedItem(name);
      return item == null ? null : item.getNodeValue();
   }

   private static int intAttr(Node node, String name, int fallback) {
      try {
         String value = attr(node, name);
         return value == null ? fallback : Integer.parseInt(value);
      } catch (NumberFormatException var41) {
         return fallback;
      }
   }

   private static BufferedImage scale(BufferedImage source, int w, int h) {
      if (source.getWidth() == w && source.getHeight() == h) {
         return source;
      } else {
         BufferedImage out = new BufferedImage(w, h, 2);
         Graphics2D g = out.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         g.drawImage(source, 0, 0, w, h, null);
         g.dispose();
         return out;
      }
   }

   private static NativeImage toNative(BufferedImage source) {
      int w = source.getWidth();
      int h = source.getHeight();
      int[] pixels = source.getRGB(0, 0, w, h, null, 0, w);
      NativeImage image = new NativeImage(w, h, false);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            image.setPixel(x, y, pixels[y * w + x]);
         }
      }

      return image;
   }

   private static Identifier register(NativeImage image, String idBase, int index) {
      Identifier id = Identifier.fromNamespaceAndPath("lyfw-client", idBase + "_" + index);
      DynamicTexture texture = new DynamicTexture(() -> "lyfw-client/" + idBase + "_" + index, image);
      Minecraft.getInstance().getTextureManager().register(id, texture);
      return id;
   }
}
