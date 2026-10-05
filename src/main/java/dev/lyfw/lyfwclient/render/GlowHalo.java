package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.world.phys.Vec3;

public final class GlowHalo {
   private static final int SEGMENTS = 20;

   private GlowHalo() {
   }

   public static void draw(VertexConsumer vc, Pose entry, Vec3 center, Vec3 camera, float radius, int rgb, int alpha) {
      if (alpha > 2 && !(radius <= 0.0F)) {
         Vec3 toEye = camera.subtract(center);
         double length = toEye.length();
         if (!(length < 1.0E-4)) {
            Vec3 forward = toEye.scale(1.0 / length);
            Vec3 helper = Math.abs(forward.y) > 0.95 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
            Vec3 side = forward.cross(helper).normalize();
            Vec3 up = side.cross(forward).normalize();
            float cx = (float)(center.x - camera.x);
            float cy = (float)(center.y - camera.y);
            float cz = (float)(center.z - camera.z);
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;

            for (int k = 0; k < 20; k++) {
               double a0 = (Math.PI * 2) * k / 20.0;
               double a1 = (Math.PI * 2) * (k + 1) / 20.0;
               float x0 = (float)((side.x * Math.cos(a0) + up.x * Math.sin(a0)) * radius);
               float y0 = (float)((side.y * Math.cos(a0) + up.y * Math.sin(a0)) * radius);
               float z0 = (float)((side.z * Math.cos(a0) + up.z * Math.sin(a0)) * radius);
               float x1 = (float)((side.x * Math.cos(a1) + up.x * Math.sin(a1)) * radius);
               float y1 = (float)((side.y * Math.cos(a1) + up.y * Math.sin(a1)) * radius);
               float z1 = (float)((side.z * Math.cos(a1) + up.z * Math.sin(a1)) * radius);
               vc.addVertex(entry, cx, cy, cz).setColor(r, g, b, alpha);
               vc.addVertex(entry, cx, cy, cz).setColor(r, g, b, alpha);
               vc.addVertex(entry, cx + x1, cy + y1, cz + z1).setColor(r, g, b, 0);
               vc.addVertex(entry, cx + x0, cy + y0, cz + z0).setColor(r, g, b, 0);
            }
         }
      }
   }
}
