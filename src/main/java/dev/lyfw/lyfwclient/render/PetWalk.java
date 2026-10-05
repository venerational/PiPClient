package dev.lyfw.lyfwclient.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.Mth;

public final class PetWalk {
   private static final double STOP = 0.5;
   private static final double WALK = 4.2;
   private static final double RUN = 8.5;
   private static final double TELEPORT = 14.0;
   private static final double CLIMB = 12.0;
   private static final long FORGET_MS = 10000L;
   private static final Map<Long, PetWalk.Step> STEPS = new HashMap<>();
   private static long swept;

   private PetWalk() {
   }

   public static PetWalk.Pose follow(int owner, int slot, double px, double py, double pz, float bodyYaw, float[] spot) {
      long now = System.currentTimeMillis();
      sweep(now);
      long key = (long)owner << 8 | slot & 255L;
      float yawRad = bodyYaw * (float) (Math.PI / 180.0);
      float cos = Mth.cos(yawRad);
      float sin = Mth.sin(yawRad);
      double tx = px + spot[0] * cos + spot[1] * sin;
      double tz = pz + spot[0] * sin - spot[1] * cos;
      PetWalk.Step step = STEPS.get(key);
      if (step == null) {
         step = new PetWalk.Step();
         step.x = tx;
         step.y = py;
         step.z = tz;
         step.yaw = bodyYaw;
         step.when = now;
         STEPS.put(key, step);
      }

      float dt = Mth.clamp((float)(now - step.when) / 1000.0F, 0.0F, 0.25F);
      step.when = now;
      double dx = tx - step.x;
      double dz = tz - step.z;
      double gap = Math.sqrt(dx * dx + dz * dz);
      if (gap > 14.0) {
         step.x = tx;
         step.z = tz;
         step.y = py;
         gap = 0.0;
      }

      double moved = 0.0;
      if (gap > 0.5 && dt > 0.0F) {
         double speed = Math.min(gap * 2.6, gap > 3.0 ? 8.5 : 4.2);
         moved = Math.min(gap - 0.3, speed * dt);
         step.x += dx / gap * moved;
         step.z += dz / gap * moved;
      }

      step.y = step.y + Mth.clamp(py - step.y, -12.0 * dt, 12.0 * dt);
      float pace = dt > 0.0F ? (float)(moved / dt) : 0.0F;
      step.limb += (float)moved * 4.5F;
      step.amplitude = step.amplitude + (Mth.clamp(pace / 4.2F, 0.0F, 1.0F) - step.amplitude) * Math.min(1.0F, dt * 10.0F);
      float facing = moved > 0.0 ? (float)Math.toDegrees(Math.atan2(-dx, dz)) : look(step, px, pz, bodyYaw);
      step.yaw = step.yaw + Mth.wrapDegrees(facing - step.yaw) * Math.min(1.0F, dt * 9.0F);
      float wdx = (float)(step.x - px);
      float wdz = (float)(step.z - pz);
      return new PetWalk.Pose(wdx * cos + wdz * sin, (float)(py - step.y), wdx * sin - wdz * cos, step.yaw - bodyYaw, step.limb, step.amplitude);
   }

   private static float look(PetWalk.Step step, double px, double pz, float bodyYaw) {
      double dx = px - step.x;
      double dz = pz - step.z;
      return dx * dx + dz * dz < 0.09 ? bodyYaw : (float)Math.toDegrees(Math.atan2(-dx, dz));
   }

   private static void sweep(long now) {
      if (now - swept >= 10000L) {
         swept = now;
         Iterator<PetWalk.Step> steps = STEPS.values().iterator();

         while (steps.hasNext()) {
            if (now - steps.next().when > 10000L) {
               steps.remove();
            }
         }
      }
   }

   public record Pose(float dx, float dy, float dz, float turn, float limb, float amplitude) {
   }

   private static final class Step {
      double x;
      double y;
      double z;
      float yaw;
      float limb;
      float amplitude;
      long when;
   }
}
