package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class AnimatedCapes {
   static final int SCALE = 8;
   static final int W = 80;
   static final int H = 128;
   private static final long FRAME_NANOS = 50000000L;
   private static final long START = System.nanoTime();
   private static final Map<AnimatedCapes.Style, AnimatedCapes.Animated> TEXTURES = new EnumMap<>(AnimatedCapes.Style.class);
   private static final ExecutorService PAINTERS = Executors.newFixedThreadPool(2, task -> {
      Thread thread = new Thread(task, "Pip Client Cape Painter");
      thread.setDaemon(true);
      thread.setPriority(4);
      return thread;
   });
   private static final double[] BAYER = new double[]{0.0, 8.0, 2.0, 10.0, 12.0, 4.0, 14.0, 6.0, 3.0, 11.0, 1.0, 9.0, 15.0, 7.0, 13.0, 5.0};

   private AnimatedCapes() {
   }

   public static AnimatedCapes.Style byName(String name) {
      for (AnimatedCapes.Style style : AnimatedCapes.Style.values()) {
         if (style.title.equals(name)) {
            return style;
         }
      }

      return null;
   }

   public static List<String> names() {
      List<String> names = new ArrayList<>();

      for (AnimatedCapes.Style style : AnimatedCapes.Style.values()) {
         names.add(style.title);
      }

      return names;
   }

   public static Identifier texture(AnimatedCapes.Style style) {
      AnimatedCapes.Animated animated = TEXTURES.computeIfAbsent(style, AnimatedCapes.Animated::new);
      long now = System.nanoTime();
      if (!animated.started) {
         animated.started = true;
         animated.requested = now;

         try {
            animated.paint((now - START) / 1.0E9);
         } catch (RuntimeException var7) {
            animated.failed = true;
            System.out.println("[Pip Client] the " + style.title + " cape could not be painted: " + var7);
         }
      }

      int[] finished = animated.ready;
      if (finished != null) {
         animated.ready = null;
         animated.write(finished);
         animated.texture.upload();
      }

      if (!animated.painting && now - animated.requested >= 50000000L) {
         animated.requested = now;
         animated.painting = true;
         double seconds = (now - START) / 1.0E9;
         PAINTERS.execute(() -> {
            try {
               animated.paint(seconds);
            } catch (RuntimeException var8) {
               if (!animated.failed) {
                  animated.failed = true;
                  System.out.println("[Pip Client] the " + style.title + " cape could not be painted: " + var8);
               }
            } finally {
               animated.painting = false;
            }
         });
      }

      return animated.id;
   }

   private static boolean inside(double[][] points, double x, double y) {
      boolean in = false;
      int i = 0;

      for (int j = points.length - 1; i < points.length; j = i++) {
         double xi = points[i][0];
         double yi = points[i][1];
         double xj = points[j][0];
         double yj = points[j][1];
         if (yi > y != yj > y && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
            in = !in;
         }
      }

      return in;
   }

   static int dither(int argb, int x, int y) {
      int offset = (int)Math.round((BAYER[(y & 3) * 4 + (x & 3)] / 16.0 - 0.5) * 6.0);
      int r = clampByte((argb >> 16 & 0xFF) + offset);
      int g = clampByte((argb >> 8 & 0xFF) + offset);
      int b = clampByte((argb & 0xFF) + offset);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private static int clampByte(int v) {
      return Math.max(0, Math.min(255, v));
   }

   static double clamp(double v, double min, double max) {
      return Math.max(min, Math.min(max, v));
   }

   static double smoothstep(double edge0, double edge1, double x) {
      double t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
      return t * t * (3.0 - 2.0 * t);
   }

   static int lerp(int a, int b, double t) {
      double k = clamp(t, 0.0, 1.0);
      int aa = (int)Math.round((a >>> 24) + ((b >>> 24) - (a >>> 24)) * k);
      int r = (int)Math.round((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
      int g = (int)Math.round((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
      int bl = (int)Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
      return aa << 24 | r << 16 | g << 8 | bl;
   }

   static int shade(int argb, double factor) {
      int r = (int)Math.min(255L, Math.max(0L, Math.round((argb >> 16 & 0xFF) * factor)));
      int g = (int)Math.min(255L, Math.max(0L, Math.round((argb >> 8 & 0xFF) * factor)));
      int b = (int)Math.min(255L, Math.max(0L, Math.round((argb & 0xFF) * factor)));
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   static int alpha(int argb, double a) {
      return (int)Math.round(clamp(a, 0.0, 1.0) * 255.0) << 24 | argb & 16777215;
   }

   static int hsv(double hue, double saturation, double value) {
      double h = (hue % 360.0 + 360.0) % 360.0 / 60.0;
      double c = value * saturation;
      double x = c * (1.0 - Math.abs(h % 2.0 - 1.0));
      double r = 0.0;
      double g = 0.0;
      double b = 0.0;
      int sector = (int)h;
      if (sector == 0) {
         r = c;
         g = x;
      } else if (sector == 1) {
         r = x;
         g = c;
      } else if (sector == 2) {
         g = c;
         b = x;
      } else if (sector == 3) {
         g = x;
         b = c;
      } else if (sector == 4) {
         r = x;
         b = c;
      } else {
         r = c;
         b = x;
      }

      double m = value - c;
      return 0xFF000000 | (int)Math.round((r + m) * 255.0) << 16 | (int)Math.round((g + m) * 255.0) << 8 | (int)Math.round((b + m) * 255.0);
   }

   static double hash(int a, int b) {
      int h = a * 374761393 + b * 668265263;
      h = (h ^ h >>> 13) * 1274126177;
      return ((h ^ h >>> 16) & 16777215) / 1.6777215E7;
   }

   static double noise(double x, double y) {
      int xi = (int)Math.floor(x);
      int yi = (int)Math.floor(y);
      double fx = x - xi;
      double fy = y - yi;
      double sx = fx * fx * (3.0 - 2.0 * fx);
      double sy = fy * fy * (3.0 - 2.0 * fy);
      double top = hash(xi, yi) + (hash(xi + 1, yi) - hash(xi, yi)) * sx;
      double bottom = hash(xi, yi + 1) + (hash(xi + 1, yi + 1) - hash(xi, yi + 1)) * sx;
      return top + (bottom - top) * sy;
   }

   static double fbm(double x, double y, int octaves) {
      double total = 0.0;
      double amplitude = 0.5;
      double weight = 0.0;

      for (int i = 0; i < octaves; i++) {
         total += noise(x, y) * amplitude;
         weight += amplitude;
         x = x * 2.03 + 17.1;
         y = y * 2.03 + 5.3;
         amplitude *= 0.5;
      }

      return total / weight;
   }

   static int ramp(double v, double[] at, int[] colors) {
      if (v <= at[0]) {
         return colors[0];
      } else {
         for (int i = 1; i < at.length; i++) {
            if (v <= at[i]) {
               return lerp(colors[i - 1], colors[i], (v - at[i - 1]) / (at[i] - at[i - 1]));
            }
         }

         return colors[colors.length - 1];
      }
   }

   static double wrap(double v, double size) {
      return (v % size + size) % size;
   }

   static void stars(AnimatedCapes.Canvas c, double t, int count, double top, double bottom, int rgb, int seed) {
      stars(c, t, count, top, bottom, rgb, seed, null);
   }

   static void stars(AnimatedCapes.Canvas c, double t, int count, double top, double bottom, int rgb, int seed, boolean[] open) {
      for (int i = 0; i < count; i++) {
         double x = hash(i, seed) * 80.0;
         double y = top + hash(i, seed + 1) * (bottom - top);
         if (open == null || isOpen(open, x, y)) {
            double twinkle = 0.55 + 0.45 * Math.sin(t * (1.0 + hash(i, seed + 2) * 3.0) + i * 2.7);
            double size = hash(i, seed + 3) > 0.9 ? 2.0 : 0.0;
            c.star(x, y, size, rgb, (0.35 + 0.65 * hash(i, seed + 4)) * twinkle);
         }
      }
   }

   static boolean[] unchanged(AnimatedCapes.Canvas c, int[] before) {
      boolean[] open = new boolean[c.px.length];

      for (int i = 0; i < open.length; i++) {
         open[i] = c.px[i] == before[i];
      }

      return open;
   }

   static boolean isOpen(boolean[] open, double x, double y) {
      int px = (int)Math.floor(x);
      int py = (int)Math.floor(y);
      return px >= 0 && py >= 0 && px < 80 && py < 128 && open[py * 80 + px];
   }

   static void mountains(AnimatedCapes.Canvas c, int seed, double baseY, double height, double roughness, int color, int snow) {
      double[] ridge = new double[80];
      int peaks = 3 + (int)(hash(seed, 99) * 3.0);

      for (int x = 0; x < 80; x++) {
         double top = baseY;

         for (int p = 0; p < peaks; p++) {
            double peakX = (p + 0.2 + hash(seed, p) * 0.6) * 80.0 * 1.2 / peaks - 80.0 * 0.1;
            double peakHeight = height * (0.55 + 0.45 * hash(p, seed + 1));
            top = Math.min(top, baseY - peakHeight + Math.abs(x + 0.5 - peakX) * (0.55 + hash(p, seed + 2) * 0.8));
         }

         ridge[x] = top + (fbm(x * roughness * 3.0 + seed * 13.1, seed * 3.7, 3) - 0.5) * height * 0.22;
      }

      double snowLine = baseY - height * 0.62;

      for (int x = 0; x < 80; x++) {
         double slope = ridge[Math.min(80 - 1, x + 2)] - ridge[Math.max(0, x - 2)];
         boolean lit = slope < 0.0;

         for (int y = Math.max(0, (int)Math.floor(ridge[x])); y < 128; y++) {
            double depth = y - ridge[x];
            double streak = fbm(x * 0.35 + seed, y * 0.09, 3);
            int face = shade(color, (lit ? 1.1 : 0.78) * (0.88 + streak * 0.24));
            if (snow != 0 && y < snowLine + (fbm(x * 0.3, y * 0.2 + seed, 2) - 0.5) * 8.0) {
               face = shade(snow, lit ? 1.0 : 0.78);
            }

            int pixel = depth < 1.0 ? lerp(c.get(x, y), face, 1.0 - (ridge[x] - Math.floor(ridge[x]))) : face;
            c.px[y * 80 + x] = 0xFF000000 | pixel;
         }
      }
   }

   static void pine(AnimatedCapes.Canvas c, double x, double baseY, double height, int color) {
      int light = shade(color, 1.35);
      c.rect(x - 0.8, baseY - height * 0.15, 1.6, height * 0.15 + 1.0, shade(color, 0.7));
      int tiers = Math.max(3, (int)(height / 4.0));

      for (int i = 0; i < tiers; i++) {
         double tierTop = baseY - height + i * (height * 0.85 / tiers);
         double tierBottom = tierTop + height * 0.85 / tiers * 1.6;
         double half = 1.0 + (i + 1) * (height * 0.36 / tiers);
         c.polygon(new double[][]{{x, tierTop}, {x + half, tierBottom}, {x - half, tierBottom}}, color);
         c.line(x, tierTop, x - half * 0.8, tierBottom - 0.5, 0.6, alpha(light, 0.6));
      }
   }

   static void cloud(AnimatedCapes.Canvas c, double cx, double cy, double size, int light, int shadow, int seed) {
      for (int i = 0; i < 7; i++) {
         double px = cx + (hash(i, seed) - 0.5) * size * 2.2;
         double py = cy + (hash(seed, i) - 0.5) * size * 0.6;
         double r = size * (0.45 + hash(i, seed + 1) * 0.35);
         c.disc(px, py + r * 0.25, r, shadow);
      }

      for (int i = 0; i < 7; i++) {
         double px = cx + (hash(i, seed) - 0.5) * size * 2.2;
         double py = cy + (hash(seed, i) - 0.5) * size * 0.6;
         double r = size * (0.45 + hash(i, seed + 1) * 0.35);
         c.disc(px - r * 0.15, py - r * 0.1, r * 0.8, light);
      }
   }

   static void bird(AnimatedCapes.Canvas c, double x, double y, double size, double flap, int argb) {
      double lift = Math.sin(flap) * size * 0.5;
      c.line(x - size, y - lift, x, y, 0.8, argb);
      c.line(x, y, x + size, y - lift, 0.8, argb);
   }

   private static final class Animated {
      private final Identifier id;
      private final DynamicTexture texture;
      private final AnimatedCapes.Scene scene;
      private int[] base;
      private volatile int[] ready;
      private volatile boolean painting;
      private boolean started;
      private boolean failed;
      private long requested;

      private Animated(AnimatedCapes.Style style) {
         String path = "capes/animated_" + style.name().toLowerCase();
         this.id = Identifier.fromNamespaceAndPath("lyfw-client", path);
         this.scene = style.scene;
         this.texture = new DynamicTexture(() -> "lyfw-client/" + path, new NativeImage(512, 256, true));
         Minecraft.getInstance().getTextureManager().register(this.id, this.texture);
      }

      private void paint(double seconds) {
         if (this.base == null) {
            AnimatedCapes.Canvas still = new AnimatedCapes.Canvas();
            this.scene.base(still);
            this.base = still.px;
         }

         AnimatedCapes.Canvas canvas = new AnimatedCapes.Canvas();
         System.arraycopy(this.base, 0, canvas.px, 0, canvas.px.length);
         this.scene.frame(canvas, seconds);
         this.ready = canvas.px;
      }

      private void write(int[] px) {
         NativeImage image = this.texture.getPixels();

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               int argb = px[y * 80 + x];
               image.setPixel(8 + x, 8 + y, argb);
               image.setPixel(96 + (79 - x), 8 + y, AnimatedCapes.shade(argb, 0.5));
            }

            for (int s = 0; s < 8; s++) {
               image.setPixel(s, 8 + y, AnimatedCapes.shade(px[y * 80], 0.7));
               image.setPixel(88 + s, 8 + y, AnimatedCapes.shade(px[y * 80 + 80 - 1], 0.7));
            }
         }

         for (int x = 0; x < 80; x++) {
            for (int s = 0; s < 8; s++) {
               image.setPixel(8 + x, s, AnimatedCapes.shade(px[x], 0.8));
               image.setPixel(88 + x, s, AnimatedCapes.shade(px[10160 + x], 0.6));
            }
         }
      }
   }

   static final class Canvas {
      final int w = 80;
      final int h = 128;
      final int[] px = new int[10240];

      Canvas() {
         Arrays.fill(this.px, -16777216);
      }

      void fill(int argb) {
         Arrays.fill(this.px, 0xFF000000 | argb);
      }

      int get(int x, int y) {
         return x >= 0 && y >= 0 && x < 80 && y < 128 ? this.px[y * 80 + x] : -16777216;
      }

      void set(int x, int y, int argb) {
         if (x >= 0 && y >= 0 && x < 80 && y < 128) {
            this.px[y * 80 + x] = 0xFF000000 | argb;
         }
      }

      void blend(int x, int y, int argb) {
         int a = argb >>> 24;
         if (a != 0 && x >= 0 && y >= 0 && x < 80 && y < 128) {
            this.px[y * 80 + x] = a == 255 ? argb : AnimatedCapes.lerp(this.px[y * 80 + x], argb, a / 255.0);
         }
      }

      void add(int x, int y, int rgb, double amount) {
         if (amount > 0.0 && x >= 0 && y >= 0 && x < 80 && y < 128) {
            int c = this.px[y * 80 + x];
            int r = Math.min(255, (c >> 16 & 0xFF) + (int)((rgb >> 16 & 0xFF) * amount));
            int g = Math.min(255, (c >> 8 & 0xFF) + (int)((rgb >> 8 & 0xFF) * amount));
            int b = Math.min(255, (c & 0xFF) + (int)((rgb & 0xFF) * amount));
            this.px[y * 80 + x] = 0xFF000000 | r << 16 | g << 8 | b;
         }
      }

      void sky(double[] at, int[] colors) {
         for (int y = 0; y < 128; y++) {
            double t = (double)y / (128 - 1);

            for (int x = 0; x < 80; x++) {
               this.px[y * 80 + x] = AnimatedCapes.dither(AnimatedCapes.ramp(t, at, colors), x, y);
            }
         }
      }

      void disc(double cx, double cy, double r, int argb) {
         double alpha = (argb >>> 24) / 255.0;

         for (int y = (int)Math.floor(cy - r - 1.0); y <= (int)Math.ceil(cy + r + 1.0); y++) {
            for (int x = (int)Math.floor(cx - r - 1.0); x <= (int)Math.ceil(cx + r + 1.0); x++) {
               double coverage = AnimatedCapes.clamp(r + 0.5 - Math.hypot(x + 0.5 - cx, y + 0.5 - cy), 0.0, 1.0);
               if (coverage > 0.0) {
                  this.blend(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
               }
            }
         }
      }

      void ring(double cx, double cy, double r, double width, int argb) {
         double alpha = (argb >>> 24) / 255.0;
         double reach = r + width;

         for (int y = (int)Math.floor(cy - reach - 1.0); y <= (int)Math.ceil(cy + reach + 1.0); y++) {
            for (int x = (int)Math.floor(cx - reach - 1.0); x <= (int)Math.ceil(cx + reach + 1.0); x++) {
               double d = Math.abs(Math.hypot(x + 0.5 - cx, y + 0.5 - cy) - r);
               double coverage = AnimatedCapes.clamp(width / 2.0 + 0.5 - d, 0.0, 1.0);
               if (coverage > 0.0) {
                  this.blend(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
               }
            }
         }
      }

      void ellipse(double cx, double cy, double rx, double ry, double angle, int argb) {
         double cos = Math.cos(angle);
         double sin = Math.sin(angle);
         double reach = Math.max(rx, ry) + 1.0;
         double alpha = (argb >>> 24) / 255.0;

         for (int y = (int)Math.floor(cy - reach); y <= (int)Math.ceil(cy + reach); y++) {
            for (int x = (int)Math.floor(cx - reach); x <= (int)Math.ceil(cx + reach); x++) {
               double dx = x + 0.5 - cx;
               double dy = y + 0.5 - cy;
               double ux = (dx * cos + dy * sin) / rx;
               double uy = (-dx * sin + dy * cos) / ry;
               double d = Math.sqrt(ux * ux + uy * uy);
               double coverage = AnimatedCapes.clamp((1.0 - d) * Math.min(rx, ry) + 0.5, 0.0, 1.0);
               if (coverage > 0.0) {
                  this.blend(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
               }
            }
         }
      }

      void glow(double cx, double cy, double r, int rgb, double strength) {
         for (int y = (int)Math.floor(cy - r); y <= (int)Math.ceil(cy + r); y++) {
            for (int x = (int)Math.floor(cx - r); x <= (int)Math.ceil(cx + r); x++) {
               double d = Math.hypot(x + 0.5 - cx, y + 0.5 - cy) / r;
               if (d < 1.0) {
                  this.add(x, y, rgb, strength * (1.0 - d) * (1.0 - d));
               }
            }
         }
      }

      void line(double x0, double y0, double x1, double y1, double width, int argb) {
         double length = Math.max(0.001, Math.hypot(x1 - x0, y1 - y0));
         double dx = (x1 - x0) / length;
         double dy = (y1 - y0) / length;
         double half = width / 2.0;
         double alpha = (argb >>> 24) / 255.0;

         for (int y = (int)Math.floor(Math.min(y0, y1) - half - 1.0); y <= (int)Math.ceil(Math.max(y0, y1) + half + 1.0); y++) {
            for (int x = (int)Math.floor(Math.min(x0, x1) - half - 1.0); x <= (int)Math.ceil(Math.max(x0, x1) + half + 1.0); x++) {
               double along = AnimatedCapes.clamp((x + 0.5 - x0) * dx + (y + 0.5 - y0) * dy, 0.0, length);
               double d = Math.hypot(x + 0.5 - (x0 + dx * along), y + 0.5 - (y0 + dy * along));
               double coverage = AnimatedCapes.clamp(half + 0.5 - d, 0.0, 1.0);
               if (coverage > 0.0) {
                  this.blend(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
               }
            }
         }
      }

      void beam(double x0, double y0, double x1, double y1, double width, int rgb, double strength) {
         this.line(x0, y0, x1, y1, width * 3.0, AnimatedCapes.alpha(rgb, 0.25 * strength));
         this.line(x0, y0, x1, y1, width, AnimatedCapes.alpha(rgb, strength));
         this.line(x0, y0, x1, y1, Math.max(0.5, width * 0.35), AnimatedCapes.alpha(AnimatedCapes.lerp(0xFF000000 | rgb, -1, 0.6), strength));
      }

      void rect(double x, double y, double w, double h, int argb) {
         for (int py = (int)Math.floor(y); py < (int)Math.ceil(y + h); py++) {
            for (int px = (int)Math.floor(x); px < (int)Math.ceil(x + w); px++) {
               this.blend(px, py, argb);
            }
         }
      }

      void polygon(double[][] points, IntBinaryOperator color) {
         double minX = Double.MAX_VALUE;
         double maxX = -Double.MAX_VALUE;
         double minY = Double.MAX_VALUE;
         double maxY = -Double.MAX_VALUE;

         for (double[] p : points) {
            minX = Math.min(minX, p[0]);
            maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
         }

         for (int y = Math.max(0, (int)Math.floor(minY)); y <= Math.min(128 - 1, (int)Math.ceil(maxY)); y++) {
            for (int x = Math.max(0, (int)Math.floor(minX)); x <= Math.min(80 - 1, (int)Math.ceil(maxX)); x++) {
               if (AnimatedCapes.inside(points, x + 0.5, y + 0.5)) {
                  this.blend(x, y, color.applyAsInt(x, y));
               }
            }
         }
      }

      void polygon(double[][] points, int argb) {
         this.polygon(points, (x, y) -> argb);
      }

      void fillBelow(DoubleUnaryOperator skyline, int argb) {
         for (int x = 0; x < 80; x++) {
            double top = skyline.applyAsDouble(x + 0.5);
            int first = (int)Math.floor(top);
            this.blend(x, first, AnimatedCapes.alpha(argb, (1.0 - (top - first)) * ((argb >>> 24) / 255.0)));

            for (int y = Math.max(0, first + 1); y < 128; y++) {
               this.blend(x, y, argb);
            }
         }
      }

      void vignette(double strength) {
         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double dx = (x + 0.5) / 80.0 - 0.5;
               double dy = (y + 0.5) / 128.0 - 0.5;
               double d = Math.sqrt(dx * dx * 1.6 + dy * dy);
               this.px[y * 80 + x] = AnimatedCapes.shade(this.px[y * 80 + x], 1.0 - strength * AnimatedCapes.smoothstep(0.25, 0.75, d));
            }
         }
      }

      void star(double x, double y, double size, int rgb, double brightness) {
         int cx = (int)Math.floor(x);
         int cy = (int)Math.floor(y);
         this.add(cx, cy, rgb, brightness);
         if (size > 1.0) {
            for (int i = 1; i <= (int)size; i++) {
               double falloff = brightness * (1.0 - i / (size + 1.0)) * 0.6;
               this.add(cx + i, cy, rgb, falloff);
               this.add(cx - i, cy, rgb, falloff);
               this.add(cx, cy + i, rgb, falloff);
               this.add(cx, cy - i, rgb, falloff);
            }
         }
      }
   }

   interface Scene {
      default void base(AnimatedCapes.Canvas c) {
      }

      void frame(AnimatedCapes.Canvas canvas, double d);
   }

   public static enum Style {
      CHERRY_BLOSSOM_NIGHT("Cherry Blossom Night", new CapeScenesA.CherryBlossomNight()),
      AURORA("Aurora", new CapeScenesA.Aurora()),
      GALAXY("Galaxy", new CapeScenesA.Galaxy()),
      OCEAN_WAVES("Ocean Waves", new CapeScenesA.OceanWaves()),
      VOLCANO("Volcano", new CapeScenesA.Volcano()),
      CODE_RAIN("Code Rain", new CapeScenesA.CodeRain()),
      PRISM("Prism", new CapeScenesA.Prism()),
      FIREFLIES("Fireflies", new CapeScenesA.Fireflies()),
      WINTER_VILLAGE("Winter Village", new CapeScenesA.WinterVillage()),
      THUNDERSTORM("Thunderstorm", new CapeScenesA.Thunderstorm()),
      TROPICAL_SUNSET("Tropical Sunset", new CapeScenesB.TropicalSunset()),
      THE_END("The End", new CapeScenesB.TheEnd()),
      SYNTHWAVE("Synthwave", new CapeScenesB.Synthwave()),
      LIQUID_CHROME("Liquid Chrome", new CapeScenesB.LiquidChrome()),
      NEON_HEARTS("Neon Hearts", new CapeScenesB.NeonHearts()),
      KOI_POND("Koi Pond", new CapeScenesB.KoiPond()),
      PHOENIX("Phoenix", new CapeScenesB.Phoenix()),
      CORAL_REEF("Coral Reef", new CapeScenesB.CoralReef()),
      METEOR_DESERT("Meteor Desert", new CapeScenesB.MeteorDesert()),
      GLITCH("Glitch", new CapeScenesB.Glitch()),
      LAVA_LAMP("Lava Lamp", new CapeScenesC.LavaLamp()),
      POCKET_WATCH("Pocket Watch", new CapeScenesC.PocketWatch()),
      NETHER_PORTAL("Nether Portal", new CapeScenesC.NetherPortal()),
      AUTUMN_LEAVES("Autumn Leaves", new CapeScenesC.AutumnLeaves()),
      JELLYFISH("Jellyfish", new CapeScenesC.Jellyfish()),
      DRAGONS_HOARD("Dragon's Hoard", new CapeScenesC.DragonsHoard()),
      RINGED_PLANET("Ringed Planet", new CapeScenesC.RingedPlanet()),
      MOON_WOLF("Moon Wolf", new CapeScenesC.MoonWolf()),
      ENCHANTED_BOOK("Enchanted Book", new CapeScenesC.EnchantedBook()),
      NEON_CITY("Neon City", new CapeScenesC.NeonCity()),
      JUNGLE_WATERFALL("Jungle Waterfall", new CapeScenesD.JungleWaterfall()),
      HOURGLASS("Hourglass", new CapeScenesD.Hourglass()),
      CRYSTAL_CAVE("Crystal Cave", new CapeScenesD.CrystalCave()),
      SOLAR_SYSTEM("Solar System", new CapeScenesD.SolarSystem()),
      BUTTERFLY_GARDEN("Butterfly Garden", new CapeScenesD.ButterflyGarden()),
      RUNE_CIRCLE("Rune Circle", new CapeScenesD.RuneCircle()),
      CANDLELIGHT("Candlelight", new CapeScenesD.Candlelight()),
      HOT_AIR_BALLOONS("Hot Air Balloons", new CapeScenesD.HotAirBalloons()),
      PIXEL_QUEST("Pixel Quest", new CapeScenesD.PixelQuest()),
      BLACK_HOLE("Black Hole", new CapeScenesD.BlackHole()),
      RAINY_WINDOW("Rainy Window", new CapeScenesE.RainyWindow()),
      DESERT_OASIS("Desert Oasis", new CapeScenesE.DesertOasis()),
      BAMBOO_GROVE("Bamboo Grove", new CapeScenesE.BambooGrove()),
      TORNADO("Tornado", new CapeScenesE.Tornado()),
      SUNKEN_SHIP("Sunken Ship", new CapeScenesE.SunkenShip()),
      FROG_POND("Frog Pond", new CapeScenesE.FrogPond()),
      GLOWING_MUSHROOMS("Glowing Mushrooms", new CapeScenesE.GlowingMushrooms()),
      SAVANNA("Savanna", new CapeScenesE.Savanna()),
      SNOWY_OWL("Snowy Owl", new CapeScenesE.SnowyOwl()),
      HUMMINGBIRD("Hummingbird", new CapeScenesE.Hummingbird()),
      BEEHIVE("Beehive", new CapeScenesF.Beehive()),
      ANT_COLONY("Ant Colony", new CapeScenesF.AntColony()),
      GREAT_WHALE("Great Whale", new CapeScenesF.GreatWhale()),
      PENGUINS("Penguins", new CapeScenesF.Penguins()),
      MISTY_LAKE("Misty Lake", new CapeScenesF.MistyLake()),
      RAINBOW_MEADOW("Rainbow Meadow", new CapeScenesF.RainbowMeadow()),
      SUPERNOVA("Supernova", new CapeScenesF.Supernova()),
      ASTEROID_FIELD("Asteroid Field", new CapeScenesF.AsteroidField()),
      MOON_BASE("Moon Base", new CapeScenesF.MoonBase()),
      ROCKET_LAUNCH("Rocket Launch", new CapeScenesF.RocketLaunch()),
      UFO("UFO", new CapeScenesG.Ufo()),
      WORMHOLE("Wormhole", new CapeScenesG.Wormhole()),
      HYPERSPACE("Hyperspace", new CapeScenesG.Hyperspace()),
      MARS("Mars", new CapeScenesG.Mars()),
      EARTH_ORBIT("Earth Orbit", new CapeScenesG.EarthOrbit()),
      ECLIPSE("Eclipse", new CapeScenesG.Eclipse()),
      WIZARD_TOWER("Wizard Tower", new CapeScenesG.WizardTower()),
      SKY_ISLANDS("Sky Islands", new CapeScenesG.SkyIslands()),
      CASTLE_FIREWORKS("Castle Fireworks", new CapeScenesG.CastleFireworks()),
      FAIRY_RING("Fairy Ring", new CapeScenesG.FairyRing()),
      KRAKEN("Kraken", new CapeScenesH.Kraken()),
      PEGASUS("Pegasus", new CapeScenesH.Pegasus()),
      SWORD_IN_STONE("Sword in the Stone", new CapeScenesH.SwordInStone()),
      CAULDRON("Cauldron", new CapeScenesH.Cauldron()),
      HAUNTED_HOUSE("Haunted House", new CapeScenesH.HauntedHouse()),
      PUMPKIN_PATCH("Pumpkin Patch", new CapeScenesH.PumpkinPatch()),
      DRAGON_EGG("Dragon Egg", new CapeScenesH.DragonEgg()),
      CRYSTAL_BALL("Crystal Ball", new CapeScenesH.CrystalBall()),
      DIAMOND_MINE("Diamond Mine", new CapeScenesH.DiamondMine()),
      REDSTONE("Redstone", new CapeScenesH.Redstone()),
      BEACON("Beacon", new CapeScenesI.Beacon()),
      SCULK("Sculk", new CapeScenesI.Sculk()),
      ALIEN_ARCADE("Alien Arcade", new CapeScenesI.AlienArcade()),
      RETRO_TV("Retro TV", new CapeScenesI.RetroTv()),
      FALLING_BLOCKS("Falling Blocks", new CapeScenesI.FallingBlocks()),
      SNAKE("Snake", new CapeScenesI.Snake()),
      VINYL_RECORD("Vinyl Record", new CapeScenesI.VinylRecord()),
      EQUALIZER("Equalizer", new CapeScenesI.Equalizer()),
      STREET_LAMP("Street Lamp", new CapeScenesI.StreetLamp()),
      LANTERN_FESTIVAL("Lantern Festival", new CapeScenesI.LanternFestival()),
      FIREPLACE("Fireplace", new CapeScenesJ.Fireplace()),
      COFFEE("Coffee", new CapeScenesJ.Coffee()),
      GOLDFISH_BOWL("Goldfish Bowl", new CapeScenesJ.GoldfishBowl()),
      FERRIS_WHEEL("Ferris Wheel", new CapeScenesJ.FerrisWheel()),
      KALEIDOSCOPE("Kaleidoscope", new CapeScenesJ.Kaleidoscope()),
      FRACTAL("Fractal", new CapeScenesJ.Fractal()),
      INK_IN_WATER("Ink in Water", new CapeScenesJ.InkInWater()),
      DNA_HELIX("DNA Helix", new CapeScenesJ.DnaHelix()),
      PENDULUM_WAVE("Pendulum Wave", new CapeScenesJ.PendulumWave()),
      CHRISTMAS_TREE("Christmas Tree", new CapeScenesJ.ChristmasTree());

      public final String title;
      final AnimatedCapes.Scene scene;

      private Style(String title, AnimatedCapes.Scene scene) {
         this.title = title;
         this.scene = scene;
      }
   }
}
