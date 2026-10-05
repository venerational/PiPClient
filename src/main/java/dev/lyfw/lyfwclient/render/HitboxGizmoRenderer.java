package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.HitboxModule;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HitboxGizmoRenderer implements SimpleDebugRenderer {
   public void emitGizmos(double cameraX, double cameraY, double cameraZ, DebugValueAccess store, Frustum frustum, float tickProgress) {
      Minecraft mc = Minecraft.getInstance();
      if (ModuleManager.get("Hitboxes") instanceof HitboxModule hitboxes && mc.level != null && hitboxes.isEnabled()) {
         boolean playersOnly = hitboxes.playersOnly();
         boolean showSelf = hitboxes.showSelf();

         for (Entity entity : mc.level.entitiesForRendering()) {
            if (!entity.isInvisible() && frustum.isVisible(entity.getBoundingBox()) && (!playersOnly || entity instanceof Player)) {
               boolean isFirstPersonSelf = entity == mc.getCameraEntity() && mc.options.getCameraType() == CameraType.FIRST_PERSON;
               if (!isFirstPersonSelf || showSelf) {
                  boolean inReach = entity == mc.crosshairPickEntity;
                  GizmoStyle style = hitboxes.style(inReach);
                  Vec3 basePos = entity.position();
                  Vec3 lerpedPos = entity.getPosition(tickProgress);
                  Vec3 delta = lerpedPos.subtract(basePos);
                  Gizmos.cuboid(entity.getBoundingBox().move(delta), style);
               }
            }
         }
      }
   }
}
