package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class TrailRenderer {
   private static final Map<TrailRenderer.Strand, Deque<TrailRenderer.Pt>> TRAILS = new HashMap<>();
   private static final Map<TrailRenderer.Strand, Integer> NEXT_SEQ = new HashMap<>();
   private static final int[] FEET = new int[]{-1, 1};
   private static final double MIN_STEP_SQ = 0.0025;
   private static final double LIFT = 0.1;
   private static final double FOOT_OFFSET = 0.14;
   private static final int SUB = 4;
   private static final double SHORT_SQ = 0.0064;
   private static final int MIN_VISIBLE_ALPHA = 2;
   private static final float PREVIEW_STEP = 0.05F;
   private static final float TAU = (float) (Math.PI * 2);
   private static final TrailRenderer.Path EMPTY_PATH = new TrailRenderer.Path(new Vec3[0], new float[0], new float[0]);

   private TrailRenderer() {
   }

   private static long lifeMs(Trails.Trail trail) {
      return Math.round(trail.length() * 50.0);
   }

   private static Trails.Trail trailOf(CosmeticsModule cosmetics, Player player) {
      if (player != null && !player.isSpectator()) {
         CosmeticLoadout look = cosmetics.loadoutFor(player);
         return look == null ? null : Trails.byId(look.trail());
      } else {
         return null;
      }
   }

   private static Vec3 foot(Player player, int side, float delta) {
      float yaw = Mth.rotLerp(delta, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
      Vec3 at = player.getPosition(delta);
      return at.add(-Mth.cos(yaw) * 0.14 * side, 0.1, -Mth.sin(yaw) * 0.14 * side);
   }

   public static void tick() {
      Minecraft mc = Minecraft.getInstance();
      CosmeticsModule cosmetics = CosmeticsModule.get();
      if (mc.level != null && mc.player != null && cosmetics != null) {
         Set<TrailRenderer.Strand> present = new HashSet<>();
         long now = System.currentTimeMillis();

         for (Player player : mc.level.players()) {
            Trails.Trail trail = trailOf(cosmetics, player);
            if (trail != null && (player != mc.player || !mc.options.getCameraType().isFirstPerson())) {
               for (int side : FEET) {
                  TrailRenderer.Strand strand = new TrailRenderer.Strand(player.getUUID(), side);
                  present.add(strand);
                  Deque<TrailRenderer.Pt> pts = TRAILS.computeIfAbsent(strand, k -> new ArrayDeque<>());
                  Vec3 at = foot(player, side, 1.0F);
                  if (pts.isEmpty() || pts.peekLast().pos().distanceToSqr(at) > 0.0025) {
                     int seq = NEXT_SEQ.merge(strand, 1, Integer::sum);
                     pts.addLast(new TrailRenderer.Pt(at, seq, now));
                  }

                  long cutoff = now - lifeMs(trail);

                  while (!pts.isEmpty() && pts.peekFirst().ms() < cutoff) {
                     pts.removeFirst();
                  }

                  while (pts.size() > trail.length()) {
                     pts.removeFirst();
                  }
               }
            }
         }

         TRAILS.keySet().removeIf(strandx -> !present.contains(strandx) || TRAILS.get(strandx).isEmpty());
         NEXT_SEQ.keySet().retainAll(TRAILS.keySet());
      } else {
         TRAILS.clear();
         NEXT_SEQ.clear();
      }
   }

   public static void render(WorldRenderContext context) {
      Minecraft mc = Minecraft.getInstance();
      CosmeticsModule cosmetics = CosmeticsModule.get();
      if (!TRAILS.isEmpty() && mc.level != null && cosmetics != null && context.consumers() != null) {
         Vec3 camPos = mc.gameRenderer.getMainCamera().position();
         float delta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
         float time = ((float)mc.level.getGameTime() + delta) / 20.0F;
         Pose entry = context.matrices().last();
         VertexConsumer vc = context.consumers().getBuffer(RenderTypes.debugQuads());

         for (Entry<TrailRenderer.Strand, Deque<TrailRenderer.Pt>> e : TRAILS.entrySet()) {
            TrailRenderer.Strand strand = e.getKey();
            Player player = mc.level.getPlayerByUUID(strand.player());
            Trails.Trail trail = trailOf(cosmetics, player);
            if (trail != null) {
               TrailRenderer.Path path = smooth(e.getValue(), foot(player, strand.side(), delta), lifeMs(trail));
               if (path.pts().length >= 2) {
                  drawTrail(vc, entry, path, trail, camPos, camPos, time, strand.side());
               }
            }
         }
      }
   }

   public static void drawPreview(VertexConsumer vc, Pose entry, Trails.Trail trail, float feetY, Vec3 eye, float seconds) {
      int n = Math.max(2, trail.length());
      float ticks = seconds * 20.0F;
      int base = (int)Math.floor(ticks);
      float frac = ticks - base;
      double y = feetY + 0.1;

      for (int side : FEET) {
         double x = 0.14 * side;
         Vec3[] pts = new Vec3[n + 1];
         float[] seq = new float[n + 1];
         float[] fade = new float[n + 1];

         for (int k = 0; k < n; k++) {
            int i = n - 1 - k;
            pts[i] = new Vec3(x, y, (k + frac) * 0.05F);
            seq[i] = base - k;
            fade[i] = Math.max(0.0F, 1.0F - (k + frac) / n);
         }

         pts[n] = new Vec3(x, y, 0.0);
         seq[n] = base + 1;
         fade[n] = 1.0F;
         drawTrail(vc, entry, new TrailRenderer.Path(pts, seq, fade), trail, Vec3.ZERO, eye, seconds, side);
      }
   }

   private static TrailRenderer.Path smooth(Deque<TrailRenderer.Pt> stored, Vec3 live, long life) {
      TrailRenderer.Pt[] c = new TrailRenderer.Pt[stored.size() + 1];
      int n = 0;

      for (TrailRenderer.Pt pt : stored) {
         c[n++] = pt;
      }

      long now = System.currentTimeMillis();
      if (live != null) {
         if (n == 0) {
            c[n++] = new TrailRenderer.Pt(live, 0, now);
         } else if (c[n - 1].pos().distanceToSqr(live) < 0.0025) {
            c[n - 1] = new TrailRenderer.Pt(live, c[n - 1].seq(), now);
         } else {
            c[n] = new TrailRenderer.Pt(live, c[n - 1].seq() + 1, now);
            n++;
         }
      }

      if (n < 2) {
         return EMPTY_PATH;
      } else if (n == 2) {
         return new TrailRenderer.Path(
            new Vec3[]{c[0].pos(), c[1].pos()}, new float[]{c[0].seq(), c[1].seq()}, new float[]{fadeOf(c[0], now, life), fadeOf(c[1], now, life)}
         );
      } else {
         int cap = (n - 1) * 4 + 1;
         Vec3[] out = new Vec3[cap];
         float[] ids = new float[cap];
         float[] fades = new float[cap];
         int w = 0;

         for (int i = 0; i < n - 1; i++) {
            Vec3 p0 = c[Math.max(0, i - 1)].pos();
            Vec3 p1 = c[i].pos();
            Vec3 p2 = c[i + 1].pos();
            Vec3 p3 = c[Math.min(n - 1, i + 2)].pos();
            float s0 = c[i].seq();
            float f0 = fadeOf(c[i], now, life);
            float f1 = fadeOf(c[i + 1], now, life);
            if (p1.distanceToSqr(p2) < 0.0064) {
               ids[w] = s0;
               fades[w] = f0;
               out[w++] = p1;
            } else {
               for (int k = 0; k < 4; k++) {
                  float f = k / 4.0F;
                  ids[w] = s0 + f;
                  fades[w] = f0 + (f1 - f0) * f;
                  out[w++] = catmullRom(p0, p1, p2, p3, f);
               }
            }
         }

         ids[w] = c[n - 1].seq();
         fades[w] = fadeOf(c[n - 1], now, life);
         out[w++] = c[n - 1].pos();
         return w == cap
            ? new TrailRenderer.Path(out, ids, fades)
            : new TrailRenderer.Path(Arrays.copyOf(out, w), Arrays.copyOf(ids, w), Arrays.copyOf(fades, w));
      }
   }

   private static Vec3 catmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, float t) {
      double t0 = 0.0;
      double t1 = t0 + Math.sqrt(p0.distanceTo(p1));
      double t2 = t1 + Math.sqrt(p1.distanceTo(p2));
      double t3 = t2 + Math.sqrt(p2.distanceTo(p3));
      if (!(t1 <= t0) && !(t2 <= t1) && !(t3 <= t2)) {
         double tt = t1 + (t2 - t1) * t;
         Vec3 a1 = blend(p0, p1, (t1 - tt) / (t1 - t0), (tt - t0) / (t1 - t0));
         Vec3 a2 = blend(p1, p2, (t2 - tt) / (t2 - t1), (tt - t1) / (t2 - t1));
         Vec3 a3 = blend(p2, p3, (t3 - tt) / (t3 - t2), (tt - t2) / (t3 - t2));
         Vec3 b1 = blend(a1, a2, (t2 - tt) / (t2 - t0), (tt - t0) / (t2 - t0));
         Vec3 b2 = blend(a2, a3, (t3 - tt) / (t3 - t1), (tt - t1) / (t3 - t1));
         return blend(b1, b2, (t2 - tt) / (t2 - t1), (tt - t1) / (t2 - t1));
      } else {
         return p1.add(p2.subtract(p1).scale(t));
      }
   }

   private static Vec3 blend(Vec3 a, Vec3 b, double wa, double wb) {
      return new Vec3(a.x * wa + b.x * wb, a.y * wa + b.y * wb, a.z * wa + b.z * wb);
   }

   private static float fadeOf(TrailRenderer.Pt pt, long now, long life) {
      float age = (float)(now - pt.ms()) / (float)life;
      return age <= 0.0F ? 1.0F : (age >= 1.0F ? 0.0F : 1.0F - age);
   }

   private static int colorAt(Trails.Trail trail, float life, float seq, float time, int salt) {
      int rgb = trail.argb() & 16777215;

      return switch (trail.flow()) {
         case SOLID -> rgb;
         case GRADIENT -> mix(rgb, trail.tail(), 1.0F - life);
         case RAINBOW -> Color.HSBtoRGB(fract(seq * 0.035F - time * 0.6F), 0.75F, 1.0F) & 16777215;
         case PULSE -> {
            float wave = Math.max(0.0F, Mth.sin(seq * 0.45F - time * 9.0F));
            yield mix(rgb, 16777215, 0.7F * wave * wave * wave * wave * wave * wave);
         }
         case FLICKER -> hash((int)Math.floor(seq) * 31 + salt + (int)Math.floor(time * 10.0F) * 131) > 0.62F ? trail.tail() & 16777215 : rgb;
         case SHIMMER -> {
            float[] hsb = Color.RGBtoHSB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, null);
            float hue = hsb[0] + 0.06F * Mth.sin(seq * 0.2F - time * 1.5F);
            yield Color.HSBtoRGB(fract(hue), hsb[1], Math.max(hsb[2], 0.35F)) & 16777215;
         }
      };
   }

   private static void drawTrail(VertexConsumer vc, Pose m, TrailRenderer.Path path, Trails.Trail trail, Vec3 origin, Vec3 eye, float time, int side) {
      Vec3[] pts = path.pts();
      float[] seq = path.seq();
      float[] fade = path.fade();
      float width = trail.width() / 100.0F;
      int baseA = trail.argb() >>> 24 & 0xFF;
      int salt = side * 1013;
      int last = pts.length - 1;

      for (int i = 0; i < last; i++) {
         Vec3 a = pts[i];
         Vec3 c = pts[i + 1];
         float t0 = fade[i];
         float t1 = fade[i + 1];
         int a0 = Math.round(baseA * t0 * t0);
         int a1 = Math.round(baseA * t1 * t1);
         if (a0 > 2 || a1 > 2) {
            int rgb = colorAt(trail, t0, seq[i], time, salt);
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            double dx = c.x - a.x;
            double dy = c.y - a.y;
            double dz = c.z - a.z;
            double flat = Math.sqrt(dx * dx + dz * dz);
            float px = flat < 1.0E-4 ? 1.0F : (float)(-dz / flat);
            float pz = flat < 1.0E-4 ? 0.0F : (float)(dx / flat);
            double vx = eye.x - a.x;
            double vy = eye.y - a.y;
            double vz = eye.z - a.z;
            double sxr = dy * vz - dz * vy;
            double syr = dz * vx - dx * vz;
            double szr = dx * vy - dy * vx;
            double sl = Math.sqrt(sxr * sxr + syr * syr + szr * szr);
            float sx = sl < 1.0E-6 ? px : (float)(sxr / sl);
            float sy = sl < 1.0E-6 ? 0.0F : (float)(syr / sl);
            float sz = sl < 1.0E-6 ? pz : (float)(szr / sl);
            float ax = (float)(a.x - origin.x);
            float ay = (float)(a.y - origin.y);
            float az = (float)(a.z - origin.z);
            float cx = (float)(c.x - origin.x);
            float cy = (float)(c.y - origin.y);
            float cz = (float)(c.z - origin.z);
            float w0 = width * t0;
            float w1 = width * t1;
            boolean onPoint = isSample(seq[i]);
            int seed = Math.round(seq[i]) + salt;
            if (trail.glow()) {
               band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 2.4F, w1 * 2.4F, r, g, b, Math.round(a0 * 0.28F), Math.round(a1 * 0.28F));
            }

            switch (trail.style()) {
               case HELIX: {
                  float ph0 = seq[i] * 0.25F * 2.6F + time * 3.0F;
                  float ph1 = seq[i + 1] * 0.25F * 2.6F + time * 3.0F;
                  float rad = width * 3.0F;

                  for (int sgn = -1; sgn <= 1; sgn += 2) {
                     float o0x = Mth.cos(ph0) * rad * sgn;
                     float o1x = Mth.cos(ph1) * rad * sgn;
                     float v0 = Mth.sin(ph0) * rad * sgn;
                     float v1 = Mth.sin(ph1) * rad * sgn;
                     band(
                        vc,
                        m,
                        ax + px * o0x,
                        ay + v0,
                        az + pz * o0x,
                        cx + px * o1x,
                        cy + v1,
                        cz + pz * o1x,
                        sx,
                        sy,
                        sz,
                        w0 * 0.45F,
                        w1 * 0.45F,
                        r,
                        g,
                        b,
                        a0,
                        a1
                     );
                  }
                  break;
               }
               case BEADS:
                  if (onPoint) {
                     float grow = Math.min(1.0F, (1.0F - t1) * 6.0F);
                     speck(vc, m, ax, ay, az, sx, sy, sz, vx, vy, vz, width * 2.6F * t1 * grow, r, g, b, a1);
                  }
                  break;
               case COMET:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * t0 * t0 * t0, width * t1 * t1 * t1, r, g, b, a0, a1);
                  break;
               case PLUME: {
                  float p0 = width * (0.4F + 3.0F * (1.0F - t0));
                  float p1 = width * (0.4F + 3.0F * (1.0F - t1));
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, p0, p1, r, g, b, Math.round(a0 * 0.55F), Math.round(a1 * 0.55F));
                  if (onPoint) {
                     for (int kx = 0; kx < 2; kx++) {
                        int sd = seed * 2 + kx;
                        float spread = (1.0F - t1) * width * 5.0F;
                        speck(
                           vc,
                           m,
                           ax + px * (hash(sd) - 0.5F) * spread * 2.0F,
                           ay + (hash(sd + 31) - 0.5F) * spread,
                           az + pz * (hash(sd + 67) - 0.5F) * spread * 2.0F,
                           sx,
                           sy,
                           sz,
                           vx,
                           vy,
                           vz,
                           width * 0.7F * t1,
                           r,
                           g,
                           b,
                           Math.round(a1 * 0.7F)
                        );
                     }
                  }
                  break;
               }
               case DASH:
                  if (Math.floorMod((int)Math.floor(seq[i]), 5) < 3) {
                     band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 1.35F, w1 * 1.35F, r, g, b, a0, a1);
                  }
                  break;
               case HALO:
                  thread(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width, r, g, b, a0, a1);
                  if (onPoint && Math.floorMod(seed, 3) == 0) {
                     ring(vc, m, ax, ay, az, sx, sy, sz, vx, vy, vz, width * (0.5F + 5.5F * (1.0F - t1)), width * 0.55F * t1, r, g, b, a1);
                  }
                  break;
               case SPARKS:
                  thread(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width, r, g, b, a0, a1);
                  if (onPoint) {
                     for (int k = 0; k < 2; k++) {
                        int sd = seed * 2 + k;
                        float ox = (hash(sd) - 0.5F) * width * 7.0F;
                        float oy = (hash(sd + 91) - 0.5F) * width * 7.0F + Mth.sin(time * 0.8F + sd) * width * 1.2F;
                        float oz = (hash(sd + 57) - 0.5F) * width * 7.0F;
                        int sa = Math.round(a1 * (0.55F + 0.45F * Mth.sin(time * 5.0F + sd * 1.7F)));
                        if (sa > 2) {
                           speck(vc, m, ax + ox, ay + oy, az + oz, sx, sy, sz, vx, vy, vz, width * 0.9F * t1, r, g, b, sa);
                        }
                     }
                  }
                  break;
               case WINGS:
                  float sp0 = width * 6.0F * (1.0F - t0) * side;
                  float sp1 = width * 6.0F * (1.0F - t1) * side;
                  band(
                     vc,
                     m,
                     ax + px * sp0,
                     ay + width * 2.5F * (1.0F - t0),
                     az + pz * sp0,
                     cx + px * sp1,
                     cy + width * 2.5F * (1.0F - t1),
                     cz + pz * sp1,
                     sx,
                     sy,
                     sz,
                     w0 * 0.75F,
                     w1 * 0.75F,
                     r,
                     g,
                     b,
                     a0,
                     a1
                  );
                  break;
               case BOLT: {
                  float k0 = (hash(seed) - 0.5F) * width * 7.0F;
                  float k1 = (hash(seed + 1) - 0.5F) * width * 7.0F;
                  float f0 = seq[i] - (float)Math.floor(seq[i]);
                  float f1 = seq[i + 1] - (float)Math.floor(seq[i + 1]);
                  float o0 = k0 * (1.0F - f0) + k1 * f0;
                  float o1 = k0 * (1.0F - f1) + k1 * f1;
                  band(
                     vc,
                     m,
                     ax + px * o0,
                     ay + o0 * 0.5F,
                     az + pz * o0,
                     cx + px * o1,
                     cy + o1 * 0.5F,
                     cz + pz * o1,
                     sx,
                     sy,
                     sz,
                     w0 * 0.7F,
                     w1 * 0.7F,
                     r,
                     g,
                     b,
                     a0,
                     a1
                  );
                  break;
               }
               case PETALS:
                  thread(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width, r, g, b, a0, a1);
                  if (onPoint) {
                     float fall = (1.0F - t1) * (1.0F - t1) * width * 9.0F;
                     float drift = (1.0F - t1) * width * 6.0F;
                     speck(
                        vc,
                        m,
                        ax + px * (hash(seed) - 0.5F) * drift * 2.0F,
                        ay - fall,
                        az + pz * (hash(seed + 17) - 0.5F) * drift * 2.0F,
                        sx,
                        sy,
                        sz,
                        vx,
                        vy,
                        vz,
                        width * 2.2F * t1,
                        r,
                        g,
                        b,
                        a1
                     );
                  }
                  break;
               case PRISM: {
                  float sep = width * 2.4F;
                  band(
                     vc,
                     m,
                     ax + px * sep,
                     ay,
                     az + pz * sep,
                     cx + px * sep,
                     cy,
                     cz + pz * sep,
                     sx,
                     sy,
                     sz,
                     w0 * 0.5F,
                     w1 * 0.5F,
                     (r + 255) / 2,
                     g / 2,
                     b,
                     a0,
                     a1
                  );
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 0.5F, w1 * 0.5F, r, g, b, a0, a1);
                  band(
                     vc,
                     m,
                     ax - px * sep,
                     ay,
                     az - pz * sep,
                     cx - px * sep,
                     cy,
                     cz - pz * sep,
                     sx,
                     sy,
                     sz,
                     w0 * 0.5F,
                     w1 * 0.5F,
                     r / 2,
                     (g + 255) / 2,
                     b / 2,
                     a0,
                     a1
                  );
                  break;
               }
               case FLAME:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 0.8F, w1 * 0.8F, r, g, b, a0, a1);
                  if (onPoint) {
                     float rise = (1.0F - t1) * width * 11.0F;
                     speck(
                        vc,
                        m,
                        ax + px * (hash(seed) - 0.5F) * width * 3.0F,
                        ay + rise,
                        az + pz * (hash(seed + 41) - 0.5F) * width * 3.0F,
                        sx,
                        sy,
                        sz,
                        vx,
                        vy,
                        vz,
                        width * 1.6F * t1 * t1,
                        r,
                        g,
                        b,
                        Math.round(a1 * 0.8F)
                     );
                  }
                  break;
               case CONSTELLATION:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 0.18F, width * 0.18F, r, g, b, Math.round(a0 * 0.55F), Math.round(a1 * 0.55F));
                  if (onPoint && Math.floorMod(seed, 2) == 0) {
                     float twinkle = 0.6F + 0.4F * Mth.sin(time * 4.5F + seq[i] * 1.9F);
                     speck(vc, m, ax, ay, az, sx, sy, sz, vx, vy, vz, width * 2.4F * t1, r, g, b, Math.round(a1 * twinkle));
                  }
                  break;
               case RUNGS:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 0.22F, width * 0.22F, r, g, b, a0, a1);
                  if (onPoint) {
                     float arm = width * 3.2F;
                     band(vc, m, ax - px * arm, ay, az - pz * arm, ax + px * arm, ay, az + pz * arm, sx, sy, sz, w0 * 0.6F, w0 * 0.6F, r, g, b, a1, a1);
                  }
                  break;
               case RIBBON:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0, w1, r, g, b, a0, a1);
                  break;
               case WAVE: {
                  float o0 = Mth.sin(seq[i] * 0.9F - time * 5.0F + side) * width * 2.2F;
                  float o1 = Mth.sin(seq[i + 1] * 0.9F - time * 5.0F + side) * width * 2.2F;
                  band(vc, m, ax + px * o0, ay, az + pz * o0, cx + px * o1, cy, cz + pz * o1, sx, sy, sz, w0 * 0.6F, w1 * 0.6F, r, g, b, a0, a1);
                  break;
               }
               case ZIGZAG: {
                  float o0 = triangle(seq[i] * 0.5F) * width * 2.4F;
                  float o1 = triangle(seq[i + 1] * 0.5F) * width * 2.4F;
                  band(vc, m, ax + px * o0, ay, az + pz * o0, cx + px * o1, cy, cz + pz * o1, sx, sy, sz, w0 * 0.5F, w1 * 0.5F, r, g, b, a0, a1);
                  break;
               }
               case BRAID:
                  for (int k = 0; k < 3; k++) {
                     float ph0x = seq[i] * 0.7F - time * 2.0F + k * (float) (Math.PI * 2) / 3.0F;
                     float ph1x = seq[i + 1] * 0.7F - time * 2.0F + k * (float) (Math.PI * 2) / 3.0F;
                     float o0x = Mth.sin(ph0x) * width * 2.0F;
                     float o1x = Mth.sin(ph1x) * width * 2.0F;
                     float v0 = Mth.sin(ph0x * 2.0F) * width;
                     float v1 = Mth.sin(ph1x * 2.0F) * width;
                     band(
                        vc,
                        m,
                        ax + px * o0x,
                        ay + v0,
                        az + pz * o0x,
                        cx + px * o1x,
                        cy + v1,
                        cz + pz * o1x,
                        sx,
                        sy,
                        sz,
                        w0 * 0.35F,
                        w1 * 0.35F,
                        r,
                        g,
                        b,
                        a0,
                        a1
                     );
                  }
                  break;
               case TWIN: {
                  float sep = width * 1.5F;
                  band(vc, m, ax + px * sep, ay, az + pz * sep, cx + px * sep, cy, cz + pz * sep, sx, sy, sz, w0 * 0.4F, w1 * 0.4F, r, g, b, a0, a1);
                  band(vc, m, ax - px * sep, ay, az - pz * sep, cx - px * sep, cy, cz - pz * sep, sx, sy, sz, w0 * 0.4F, w1 * 0.4F, r, g, b, a0, a1);
                  break;
               }
               case RIPPLE:
                  float m0 = 0.55F + 0.45F * Mth.sin(seq[i] * 1.1F - time * 7.0F);
                  float m1 = 0.55F + 0.45F * Mth.sin(seq[i + 1] * 1.1F - time * 7.0F);
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * m0 * 1.3F, w1 * m1 * 1.3F, r, g, b, a0, a1);
                  break;
               case STREAK:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 1.6F, width * 1.6F, r, g, b, Math.round(a0 * 0.22F), Math.round(a1 * 0.22F));
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 0.3F, width * 0.3F, r, g, b, a0, a1);
                  break;
               case NEON:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 1.8F, w1 * 1.8F, r, g, b, Math.round(a0 * 0.4F), Math.round(a1 * 0.4F));
                  int core = mix(rgb, 16777215, 0.55F);
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 0.35F, w1 * 0.35F, core >> 16 & 0xFF, core >> 8 & 0xFF, core & 0xFF, a0, a1);
                  break;
               case SMOKE: {
                  float p0 = width * (0.35F + 2.6F * (1.0F - t0));
                  float p1 = width * (0.35F + 2.6F * (1.0F - t1));
                  float up0 = (float)Math.pow(1.0F - t0, 1.5) * width * 5.0F;
                  float up1 = (float)Math.pow(1.0F - t1, 1.5) * width * 5.0F;
                  band(vc, m, ax, ay + up0, az, cx, cy + up1, cz, sx, sy, sz, p0, p1, r, g, b, Math.round(a0 * 0.7F), Math.round(a1 * 0.7F));
                  break;
               }
               case DRIP:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 0.7F, w1 * 0.7F, r, g, b, a0, a1);
                  if (onPoint && hash(seed + 7) < 0.4F) {
                     float fall = (1.0F - t1) * (1.0F - t1) * width * 9.0F;
                     speck(vc, m, ax, ay - fall, az, sx, sy, sz, vx, vy, vz, width * 0.55F * t1, r, g, b, a1);
                  }
                  break;
               case STITCH: {
                  float o0 = triangle(seq[i]) * width * 1.8F;
                  float o1 = triangle(seq[i + 1]) * width * 1.8F;
                  band(vc, m, ax + px * o0, ay, az + pz * o0, cx + px * o1, cy, cz + pz * o1, sx, sy, sz, w0 * 0.3F, w1 * 0.3F, r, g, b, a0, a1);
                  band(vc, m, ax - px * o0, ay, az - pz * o0, cx - px * o1, cy, cz - pz * o1, sx, sy, sz, w0 * 0.3F, w1 * 0.3F, r, g, b, a0, a1);
                  break;
               }
               case SERPENT: {
                  float amp0 = width * (0.3F + 3.2F * (1.0F - t0));
                  float amp1 = width * (0.3F + 3.2F * (1.0F - t1));
                  float o0 = Mth.sin(seq[i] * 0.55F - time * 4.0F + side) * amp0;
                  float o1 = Mth.sin(seq[i + 1] * 0.55F - time * 4.0F + side) * amp1;
                  band(vc, m, ax + px * o0, ay, az + pz * o0, cx + px * o1, cy, cz + pz * o1, sx, sy, sz, w0 * 0.9F, w1 * 0.9F, r, g, b, a0, a1);
                  break;
               }
               case SPINE:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 0.35F, w1 * 0.35F, r, g, b, a0, a1);
                  if (onPoint) {
                     float rib = width * 2.8F * (Math.floorMod(seed, 2) == 0 ? 1.0F : -1.0F);
                     band(vc, m, ax, ay, az, ax + px * rib - (cx - ax), ay, az + pz * rib - (cz - az), sx, sy, sz, w1 * 0.35F, w1 * 0.1F, r, g, b, a1, a1);
                  }
                  break;
               case SHARDS: {
                  float f0 = seq[i] - (float)Math.floor(seq[i]);
                  float f1 = seq[i + 1] - (float)Math.floor(seq[i + 1]);
                  if (f1 == 0.0F && seq[i + 1] > seq[i]) {
                     f1 = 1.0F;
                  }

                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * (0.1F + 1.6F * (1.0F - f0)), w1 * (0.1F + 1.6F * (1.0F - f1)), r, g, b, a0, a1);
                  break;
               }
               case FROST:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, w0 * 0.55F, w1 * 0.55F, r, g, b, a0, a1);
                  if (onPoint && Math.floorMod(seed, 3) == 0) {
                     float arm = width * 1.8F * t1;
                     band(vc, m, ax, ay - arm, az, ax, ay + arm, az, sx, sy, sz, width * 0.25F * t1, width * 0.25F * t1, r, g, b, a1, a1);
                     band(
                        vc,
                        m,
                        ax - px * arm,
                        ay,
                        az - pz * arm,
                        ax + px * arm,
                        ay,
                        az + pz * arm,
                        sx,
                        sy,
                        sz,
                        width * 0.25F * t1,
                        width * 0.25F * t1,
                        r,
                        g,
                        b,
                        a1,
                        a1
                     );
                  }
                  break;
               case METEOR:
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 1.2F * t0 * t0, width * 1.2F * t1 * t1, r, g, b, a0, a1);
                  if (onPoint) {
                     float spread = (1.0F - t1) * width * 4.0F;
                     speck(
                        vc,
                        m,
                        ax + px * (hash(seed) - 0.5F) * spread,
                        ay + (hash(seed + 13) - 0.5F) * spread,
                        az + pz * (hash(seed + 29) - 0.5F) * spread,
                        sx,
                        sy,
                        sz,
                        vx,
                        vy,
                        vz,
                        width * 0.5F * t1,
                        r,
                        g,
                        b,
                        a1
                     );
                  }
                  break;
               case ECHO:
                  for (int k = 0; k < 3; k++) {
                     float up = width * 2.2F * k;
                     float fadeK = k == 0 ? 1.0F : (k == 1 ? 0.5F : 0.25F);
                     band(vc, m, ax, ay + up, az, cx, cy + up, cz, sx, sy, sz, w0 * 0.6F, w1 * 0.6F, r, g, b, Math.round(a0 * fadeK), Math.round(a1 * fadeK));
                  }
                  break;
               case ORBIT: {
                  band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 0.2F, width * 0.2F, r, g, b, Math.round(a0 * 0.6F), Math.round(a1 * 0.6F));
                  float ph0 = seq[i] * 0.5F + time * 4.0F + side;
                  float ph1 = seq[i + 1] * 0.5F + time * 4.0F + side;
                  float rad = width * 3.2F;
                  band(
                     vc,
                     m,
                     ax + px * Mth.cos(ph0) * rad,
                     ay + Mth.sin(ph0) * rad,
                     az + pz * Mth.cos(ph0) * rad,
                     cx + px * Mth.cos(ph1) * rad,
                     cy + Mth.sin(ph1) * rad,
                     cz + pz * Mth.cos(ph1) * rad,
                     sx,
                     sy,
                     sz,
                     w0 * 0.5F,
                     w1 * 0.5F,
                     r,
                     g,
                     b,
                     a0,
                     a1
                  );
               }
            }
         }
      }
   }

   private static void thread(
      VertexConsumer vc,
      Pose m,
      float ax,
      float ay,
      float az,
      float cx,
      float cy,
      float cz,
      float sx,
      float sy,
      float sz,
      float width,
      int r,
      int g,
      int b,
      int a0,
      int a1
   ) {
      band(vc, m, ax, ay, az, cx, cy, cz, sx, sy, sz, width * 0.18F, width * 0.18F, r, g, b, Math.round(a0 * 0.5F), Math.round(a1 * 0.5F));
   }

   private static float triangle(float x) {
      return 2.0F * Math.abs(2.0F * (x - (float)Math.floor(x + 0.5F))) - 1.0F;
   }

   private static float fract(float x) {
      return x - (float)Math.floor(x);
   }

   private static int mix(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int r = Math.round((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
      int g = Math.round((from >> 8 & 0xFF) + ((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
      int b = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
      return r << 16 | g << 8 | b;
   }

   private static void ring(
      VertexConsumer vc,
      Pose m,
      float x,
      float y,
      float z,
      float sx,
      float sy,
      float sz,
      double vx,
      double vy,
      double vz,
      float radius,
      float thick,
      int r,
      int g,
      int b,
      int alpha
   ) {
      if (alpha > 2 && !(radius <= 0.0F)) {
         double ux = sy * vz - sz * vy;
         double uy = sz * vx - sx * vz;
         double uz = sx * vy - sy * vx;
         double ul = Math.sqrt(ux * ux + uy * uy + uz * uz);
         if (!(ul < 1.0E-6)) {
            float upx = (float)(ux / ul);
            float upy = (float)(uy / ul);
            float upz = (float)(uz / ul);
            float inner = Math.max(0.0F, radius - thick);
            float outer = radius + thick;
            int segments = 14;

            for (int k = 0; k < segments; k++) {
               double t0 = (double)k / segments * Math.PI * 2.0;
               double t1 = (double)(k + 1) / segments * Math.PI * 2.0;
               float c0 = (float)Math.cos(t0);
               float s0 = (float)Math.sin(t0);
               float c1 = (float)Math.cos(t1);
               float s1 = (float)Math.sin(t1);
               float[][] corner = new float[][]{
                  edge(x, y, z, sx, sy, sz, upx, upy, upz, c0, s0, inner),
                  edge(x, y, z, sx, sy, sz, upx, upy, upz, c0, s0, outer),
                  edge(x, y, z, sx, sy, sz, upx, upy, upz, c1, s1, outer),
                  edge(x, y, z, sx, sy, sz, upx, upy, upz, c1, s1, inner)
               };

               for (int wind = 0; wind < 2; wind++) {
                  for (int i = 0; i < 4; i++) {
                     float[] p = corner[wind == 0 ? i : 3 - i];
                     vc.addVertex(m, p[0], p[1], p[2]).setColor(r, g, b, alpha);
                  }
               }
            }
         }
      }
   }

   private static float[] edge(float x, float y, float z, float sx, float sy, float sz, float upx, float upy, float upz, float c, float s, float rad) {
      return new float[]{x + (sx * c + upx * s) * rad, y + (sy * c + upy * s) * rad, z + (sz * c + upz * s) * rad};
   }

   private static void speck(
      VertexConsumer vc,
      Pose m,
      float x,
      float y,
      float z,
      float sx,
      float sy,
      float sz,
      double vx,
      double vy,
      double vz,
      float size,
      int r,
      int g,
      int b,
      int alpha
   ) {
      double ux = sy * vz - sz * vy;
      double uy = sz * vx - sx * vz;
      double uz = sx * vy - sy * vx;
      double ul = Math.sqrt(ux * ux + uy * uy + uz * uz);
      if (!(ul < 1.0E-6)) {
         float upx = (float)(ux / ul);
         float upy = (float)(uy / ul);
         float upz = (float)(uz / ul);
         float[][] corners = new float[][]{
            {x - sx * size - upx * size, y - sy * size - upy * size, z - sz * size - upz * size},
            {x + sx * size - upx * size, y + sy * size - upy * size, z + sz * size - upz * size},
            {x + sx * size + upx * size, y + sy * size + upy * size, z + sz * size + upz * size},
            {x - sx * size + upx * size, y - sy * size + upy * size, z - sz * size + upz * size}
         };

         for (int wind = 0; wind < 2; wind++) {
            for (int i = 0; i < 4; i++) {
               float[] p = corners[wind == 0 ? i : 3 - i];
               vc.addVertex(m, p[0], p[1], p[2]).setColor(r, g, b, alpha);
            }
         }
      }
   }

   private static void band(
      VertexConsumer vc,
      Pose m,
      float ax,
      float ay,
      float az,
      float cx,
      float cy,
      float cz,
      float sx,
      float sy,
      float sz,
      float w0,
      float w1,
      int r,
      int g,
      int b,
      int a0,
      int a1
   ) {
      for (int side = -1; side <= 1; side += 2) {
         for (int wind = 0; wind < 2; wind++) {
            half(vc, m, ax, ay, az, cx, cy, cz, sx * side, sy * side, sz * side, w0, w1, r, g, b, a0, a1, wind == 1);
         }
      }
   }

   private static void half(
      VertexConsumer vc,
      Pose m,
      float ax,
      float ay,
      float az,
      float cx,
      float cy,
      float cz,
      float sx,
      float sy,
      float sz,
      float w0,
      float w1,
      int r,
      int g,
      int b,
      int a0,
      int a1,
      boolean flip
   ) {
      float ox = ax + sx * w0;
      float oy = ay + sy * w0;
      float oz = az + sz * w0;
      float qx = cx + sx * w1;
      float qy = cy + sy * w1;
      float qz = cz + sz * w1;
      if (flip) {
         vc.addVertex(m, ax, ay, az).setColor(r, g, b, a0);
         vc.addVertex(m, ox, oy, oz).setColor(r, g, b, 0);
         vc.addVertex(m, qx, qy, qz).setColor(r, g, b, 0);
         vc.addVertex(m, cx, cy, cz).setColor(r, g, b, a1);
      } else {
         vc.addVertex(m, ax, ay, az).setColor(r, g, b, a0);
         vc.addVertex(m, cx, cy, cz).setColor(r, g, b, a1);
         vc.addVertex(m, qx, qy, qz).setColor(r, g, b, 0);
         vc.addVertex(m, ox, oy, oz).setColor(r, g, b, 0);
      }
   }

   private static boolean isSample(float seq) {
      return Math.abs(seq - Math.round(seq)) < 0.001F;
   }

   private static float hash(int n) {
      int h = n * 374761393 + 668265263;
      h = (h ^ h >> 13) * 1274126177;
      return ((h ^ h >> 16) & 65535) / 65535.0F;
   }

   private record Path(Vec3[] pts, float[] seq, float[] fade) {
   }

   private record Pt(Vec3 pos, int seq, long ms) {
   }

   private record Strand(UUID player, int side) {
   }
}
