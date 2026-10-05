package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CosmeticsFeatureRenderer extends RenderLayer<AvatarRenderState, PlayerModel> {
   private static final float PX = 0.0625F;
   private static final float RIDER = 0.45F;

   public CosmeticsFeatureRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
      super(context);
   }

   @Override
   public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, AvatarRenderState state, float limbAngle, float limbDistance) {
      CosmeticsModule cosmetics = CosmeticsModule.get();
      Minecraft mc = Minecraft.getInstance();
      boolean petFocus = state instanceof CosmeticPreview.State focus && focus.petFocus;
      if (cosmetics != null && (!state.isInvisible || petFocus)) {
         CosmeticLoadout look;
         if (state instanceof CosmeticPreview.State preview) {
            look = preview.look;
         } else {
            if (mc.level == null || !(mc.level.getEntity(state.id) instanceof Player player)) {
               return;
            }

            look = cosmetics.loadoutFor(player);
            if (look == null) {
               return;
            }
         }

         PlayerModel model = (PlayerModel)this.getParentModel();
         boolean helmet = !state.headEquipment.isEmpty();

         for (CosmeticLoadout.Piece<CosmeticsModule.Headwear> piece : look.headwear()) {
            matrices.pushPose();
            model.head.translateAndRotate(matrices);
            this.submitHeadwear(matrices, queue, light, cosmetics, piece, helmet, state.ageInTicks);
            matrices.popPose();
         }

         for (CosmeticLoadout.Piece<CosmeticsModule.WingType> piece : look.wings()) {
            matrices.pushPose();
            model.body.translateAndRotate(matrices);
            this.submitWings(matrices, queue, light, cosmetics, piece, look.flapSpeed(), state);
            matrices.popPose();
         }

         if (!look.pets().isEmpty()) {
            this.submitPets(matrices, queue, light, cosmetics, look, state, petFocus);
         }

         if (state instanceof CosmeticPreview.State && !petFocus) {
            this.submitPreviewTrail(matrices, queue, look, state);
         }
      }
   }

   private void submitPreviewTrail(PoseStack matrices, SubmitNodeCollector queue, CosmeticLoadout look, AvatarRenderState state) {
      Trails.Trail trail = Trails.byId(look.trail());
      if (trail != null) {
         matrices.pushPose();
         matrices.scale(1.0F, -1.0F, 1.0F);
         Vector3f toViewer = new Matrix4f(matrices.last().pose()).invert().transformDirection(new Vector3f(0.0F, 0.0F, 1.0F));
         if (toViewer.lengthSquared() > 1.0E-12F) {
            toViewer.normalize(1000.0F);
            Vec3 eye = new Vec3(toViewer.x, toViewer.y, toViewer.z);
            float seconds = state.ageInTicks / 20.0F;
            queue.submitCustomGeometry(matrices, RenderTypes.debugQuads(), (entry, vc) -> TrailRenderer.drawPreview(vc, entry, trail, -1.501F, eye, seconds));
         }

         matrices.popPose();
      }
   }

   private void submitHeadwear(
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      CosmeticsModule cosmetics,
      CosmeticLoadout.Piece<CosmeticsModule.Headwear> piece,
      boolean helmet,
      float age
   ) {
      CosmeticsModule.Headwear kind = piece.kind();
      if (kind != CosmeticsModule.Headwear.NONE) {
         RenderType layer = RenderTypes.entityCutoutNoCull(cosmetics.whiteTexture());
         float s = piece.size();
         float base = helmet ? -9.25F : -8.25F;
         int outer = piece.main();
         int inner = piece.accent();
         matrices.pushPose();
         matrices.translate(0.0F, base * 0.0625F, 0.0F);
         matrices.mulPose(Axis.YP.rotationDegrees(piece.yAngle()));
         matrices.mulPose(Axis.XP.rotationDegrees(piece.xAngle()));
         if (HeadwearModels.handles(kind)) {
            matrices.scale(s, s, s);
            matrices.translate(0.0F, -base * 0.0625F, 0.0F);
            queue.submitCustomGeometry(matrices, layer, (entry, vc) -> new HeadwearModels(entry, vc, light).draw(kind, base, outer, inner, age));
            matrices.popPose();
         } else {
            matrices.translate(0.0F, -base * 0.0625F, 0.0F);
            queue.submitCustomGeometry(matrices, layer, (entry, vc) -> {
               CosmeticsFeatureRenderer.Shapes shapes = new CosmeticsFeatureRenderer.Shapes(entry, vc, light);
               switch (kind) {
                  case HALO:
                     shapes.halo(base - 4.8F * s, 3.84F * s, 0.6F * s, inner);
                     break;
                  case CROWN:
                     shapes.crown(base, s, outer, inner);
                     break;
                  case CAT_EARS:
                     shapes.earPair(base, s, 3.2F, 5.44F, 0.88F, 0.0F, outer, inner);
                     break;
                  case FOX_EARS:
                     shapes.earPair(base, s, 2.72F, 6.72F, 0.72F, 0.0F, outer, inner);
                     break;
                  case WOLF_EARS:
                     shapes.earPair(base, s, 4.16F, 4.48F, 1.28F, 0.0F, outer, inner);
                     break;
                  case BUNNY_EARS:
                     shapes.earPair(base, s, 2.4F, 9.92F, 1.76F, 0.32F, outer, inner);
                     break;
                  case BEAR_EARS:
                     shapes.roundEars(base, s, 2.56F, outer, inner);
                     break;
                  case MOUSE_EARS:
                     shapes.roundEars(base, s, 3.52F, outer, inner);
               }
            });
            matrices.popPose();
         }
      }
   }

   private static boolean petsCanWalk(AvatarRenderState state) {
      return !state.isFallFlying && !state.isVisuallySwimming && !state.isPassenger && !state.hasPose(Pose.SLEEPING);
   }

   private void submitPets(
      PoseStack matrices, SubmitNodeCollector queue, int light, CosmeticsModule cosmetics, CosmeticLoadout look, AvatarRenderState state, boolean petFocus
   ) {
      PlayerModel model = (PlayerModel)this.getParentModel();
      int ground = 0;
      int shoulder = 0;
      float stack = 0.0F;

      for (CosmeticLoadout.Piece<CosmeticsModule.Pet> piece : look.pets()) {
         int spot = petFocus ? 0 : piece.accent();
         float amplitude = state.walkAnimationSpeed;
         float limb = state.walkAnimationPos;
         matrices.pushPose();
         if (petFocus) {
            float grow = 1.9F;
            matrices.translate(0.0F, 1.001F, 0.0F);
            matrices.scale(grow, grow, grow);
            matrices.translate(0.0F, -1.501F, 0.0F);
         } else if (spot == CosmeticsModule.PetSpot.SHOULDER.ordinal()) {
            model.body.translateAndRotate(matrices);
            matrices.translate((shoulder++ % 2 == 0 ? -6.0F : 6.0F) * 0.0625F, -1.501F, 0.0F);
            piece = rider(piece);
            amplitude = 0.0F;
         } else if (spot == CosmeticsModule.PetSpot.HEAD.ordinal()) {
            model.head.translateAndRotate(matrices);
            float top = (state.headEquipment.isEmpty() ? -8.25F : -9.25F) - stack;
            matrices.translate(0.0F, top * 0.0625F - 1.501F, 0.0F);
            piece = rider(piece);
            stack += PetModels.height(piece.kind()) * piece.size();
            amplitude = 0.0F;
         } else {
            if (!petsCanWalk(state)) {
               matrices.popPose();
               continue;
            }

            float[] at = petSpot(ground++);
            if (state instanceof CosmeticPreview.State) {
               matrices.translate(at[0], 0.0F, at[1]);
            } else {
               PetWalk.Pose walk = PetWalk.follow(state.id, ground - 1, state.x, state.y, state.z, state.bodyRot, at);
               matrices.translate(walk.dx(), walk.dy() + (state.isCrouching ? -0.125F : 0.0F), walk.dz());
               matrices.mulPose(Axis.YP.rotationDegrees(walk.turn()));
               limb = walk.limb();
               amplitude = walk.amplitude();
            }
         }

         PetModels.submit(matrices, queue, light, piece, limb, amplitude, state.ageInTicks, cosmetics.whiteTexture());
         matrices.popPose();
      }
   }

   private static CosmeticLoadout.Piece<CosmeticsModule.Pet> rider(CosmeticLoadout.Piece<CosmeticsModule.Pet> piece) {
      return new CosmeticLoadout.Piece<>(piece.kind(), piece.size() * 0.45F, piece.xAngle(), piece.yAngle(), piece.main(), piece.accent());
   }

   static float[] petSpot(int index) {
      float side = index % 2 == 0 ? -1.0F : 1.0F;
      return new float[]{side * 0.85F, 0.15F + index / 2 * 0.8F};
   }

   private void submitWings(
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      CosmeticsModule cosmetics,
      CosmeticLoadout.Piece<CosmeticsModule.WingType> piece,
      float speed,
      AvatarRenderState state
   ) {
      CosmeticsModule.WingType type = piece.kind();
      Identifier texture = cosmetics.wingTexture(type, piece.main(), piece.accent());
      RenderType layer = type.translucent ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutoutNoCull(texture);
      boolean insect = type.insect;
      float s = piece.size();
      float span = 16.0F * s;
      float height = span * 40.0F / 56.0F;
      float innerSpan = span * 20.0F / 56.0F;
      float outerSpan = span - innerSpan;
      float split = 0.35714287F;
      float top = -height * 10.0F / 40.0F;
      boolean cape = state.showCape && state.skin != null && state.skin.cape() != null;
      boolean chestplate = !state.chestEquipment.isEmpty();
      float back = 2.4F + (chestplate ? 1.0F : 0.0F) + (cape ? 1.2F : 0.0F);
      float open;
      if (state.isFallFlying) {
         open = 1.0F;
      } else if (speed <= 0.0F) {
         open = 0.5F;
      } else {
         float rate = speed * type.beatRate;
         open = 0.5F + 0.5F * Mth.sin(state.ageInTicks / 20.0F * (float) (Math.PI * 2) * rate);
      }

      float spread = state.isFallFlying ? 75.0F : (insect ? 12.0F + 50.0F * open : 18.0F + 32.0F * open);
      float fold = state.isFallFlying ? 5.0F : 8.0F + 26.0F * (1.0F - open);
      float lift = insect ? 4.0F + 6.0F * open : 8.0F + 10.0F * open;
      matrices.translate(0.0F, -0.03125F, back * 0.0625F);
      matrices.mulPose(Axis.YP.rotationDegrees(piece.yAngle()));
      matrices.mulPose(Axis.XP.rotationDegrees(piece.xAngle()));

      for (int side = -1; side <= 1; side += 2) {
         matrices.pushPose();
         matrices.mulPose(Axis.YP.rotationDegrees(-side * spread));
         matrices.mulPose(Axis.ZP.rotationDegrees(-side * lift));
         if (insect) {
            submitPanel(matrices, queue, layer, light, side, span, top, height, 0.0F, 1.0F);
         } else {
            submitPanel(matrices, queue, layer, light, side, innerSpan, top, height, 0.0F, split);
            matrices.translate(side * innerSpan * 0.0625F, 0.0F, 0.0F);
            matrices.mulPose(Axis.YP.rotationDegrees(-side * fold));
            submitPanel(matrices, queue, layer, light, side, outerSpan, top, height, split, 1.0F);
         }

         matrices.popPose();
      }
   }

   private static void submitPanel(
      PoseStack matrices, SubmitNodeCollector queue, RenderType layer, int light, int side, float span, float top, float height, float u0, float u1
   ) {
      queue.submitCustomGeometry(matrices, layer, (entry, vc) -> {
         float x1 = side * span * 0.0625F;
         float yTop = top * 0.0625F;
         float yBottom = (top + height) * 0.0625F;
         vertex(entry, vc, 0.0F, yTop, 0.0F, u0, 0.0F, light, -1, 0.0F, 0.0F, 1.0F);
         vertex(entry, vc, x1, yTop, 0.0F, u1, 0.0F, light, -1, 0.0F, 0.0F, 1.0F);
         vertex(entry, vc, x1, yBottom, 0.0F, u1, 1.0F, light, -1, 0.0F, 0.0F, 1.0F);
         vertex(entry, vc, 0.0F, yBottom, 0.0F, u0, 1.0F, light, -1, 0.0F, 0.0F, 1.0F);
      });
   }

   static void vertex(
      com.mojang.blaze3d.vertex.PoseStack.Pose entry,
      VertexConsumer vc,
      float x,
      float y,
      float z,
      float u,
      float v,
      int light,
      int color,
      float nx,
      float ny,
      float nz
   ) {
      vc.addVertex(entry, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, nx, ny, nz);
   }

   private record Shapes(com.mojang.blaze3d.vertex.PoseStack.Pose entry, VertexConsumer vc, int light) {
      void earPair(float base, float s, float width, float height, float taper, float lean, int outer, int inner) {
         float top = base - height * s;

         for (int side = -1; side <= 1; side += 2) {
            float x0 = 0.9F * s * side;
            float x1 = (0.9F + width) * s * side;
            float shift = lean * s * side;
            float narrow = taper * s * side;
            this.prism(x0, x1, -0.72F * s, 0.72F * s, base, x0 + shift + narrow, x1 + shift - narrow, -0.32F * s, 0.32F * s, top, outer);
            float inset = 0.48F * s * side;
            float innerNarrow = (taper + 0.32F) * s * side;
            this.prism(
               x0 + inset,
               x1 - inset,
               -0.95F * s,
               -0.55F * s,
               base - 0.32F * s,
               x0 + shift + innerNarrow,
               x1 + shift - innerNarrow,
               -0.62F * s,
               -0.3F * s,
               top + 0.8F * s,
               inner
            );
         }
      }

      void roundEars(float base, float s, float radius, int outer, int inner) {
         float r = radius * s;

         for (int side = -1; side <= 1; side += 2) {
            float cx = (1.28F + radius) * s * side;
            float cy = base - r * 0.7F;

            for (int i = 0; i < 3; i++) {
               float t = i / 3.0F;
               float half = r * Mth.cos(t * 1.15F);
               float bottom = cy + r - t * r * 1.6F;
               float upper = bottom - r * 0.55F;
               this.prism(cx - half, cx + half, -0.8F * s, 0.8F * s, bottom, cx - half * 0.92F, cx + half * 0.92F, -0.72F * s, 0.72F * s, upper, outer);
            }

            float innerHalf = r * 0.5F;
            this.prism(
               cx - innerHalf,
               cx + innerHalf,
               -1.2F * s,
               -0.8F * s,
               cy + r * 0.45F,
               cx - innerHalf * 0.8F,
               cx + innerHalf * 0.8F,
               -1.12F * s,
               -0.72F * s,
               cy - r * 0.5F,
               inner
            );
         }
      }

      void halo(float y, float radius, float thickness, int color) {
         int segments = 16;

         for (int i = 0; i < segments; i++) {
            double a0 = (Math.PI * 2) * i / segments;
            double a1 = (Math.PI * 2) * (i + 1) / segments;
            float x0 = (float)Math.cos(a0) * radius;
            float z0 = (float)Math.sin(a0) * radius;
            float x1 = (float)Math.cos(a1) * radius;
            float z1 = (float)Math.sin(a1) * radius;
            float half = thickness / 2.0F;
            float minX = Math.min(x0, x1) - half;
            float maxX = Math.max(x0, x1) + half;
            float minZ = Math.min(z0, z1) - half;
            float maxZ = Math.max(z0, z1) + half;
            this.prism(minX, maxX, minZ, maxZ, y, minX, maxX, minZ, maxZ, y - thickness, color);
         }
      }

      void crown(float base, float s, int band, int point) {
         float half = 4.7F;
         float wall = 0.8F;
         float bottom = base + 1.65F;
         float bandTop = base - 1.1F * s;
         this.prism(-half, half, -half, -half + wall, bottom, -half, half, -half, -half + wall, bandTop, band);
         this.prism(-half, half, half - wall, half, bottom, -half, half, half - wall, half, bandTop, band);
         this.prism(-half, -half + wall, -half, half, bottom, -half, -half + wall, -half, half, bandTop, band);
         this.prism(half - wall, half, -half, half, bottom, half - wall, half, -half, half, bandTop, band);
         float[][] spots = new float[][]{{-1.0F, -1.0F}, {1.0F, -1.0F}, {1.0F, 1.0F}, {-1.0F, 1.0F}, {0.0F, -1.0F}, {0.0F, 1.0F}, {-1.0F, 0.0F}, {1.0F, 0.0F}};

         for (float[] spot : spots) {
            float px = spot[0] * (half - 0.4F);
            float pz = spot[1] * (half - 0.4F);
            float w = 0.56F;
            this.prism(px - w, px + w, pz - w, pz + w, bandTop, px - w * 0.25F, px + w * 0.25F, pz - w * 0.25F, pz + w * 0.25F, bandTop - 1.76F * s, point);
         }
      }

      void prism(float bx0, float bx1, float bz0, float bz1, float by, float tx0, float tx1, float tz0, float tz1, float ty, int color) {
         float[] b00 = new float[]{Math.min(bx0, bx1), by, Math.min(bz0, bz1)};
         float[] b10 = new float[]{Math.max(bx0, bx1), by, Math.min(bz0, bz1)};
         float[] b11 = new float[]{Math.max(bx0, bx1), by, Math.max(bz0, bz1)};
         float[] b01 = new float[]{Math.min(bx0, bx1), by, Math.max(bz0, bz1)};
         float[] t00 = new float[]{Math.min(tx0, tx1), ty, Math.min(tz0, tz1)};
         float[] t10 = new float[]{Math.max(tx0, tx1), ty, Math.min(tz0, tz1)};
         float[] t11 = new float[]{Math.max(tx0, tx1), ty, Math.max(tz0, tz1)};
         float[] t01 = new float[]{Math.min(tx0, tx1), ty, Math.max(tz0, tz1)};
         int solid = 0xFF000000 | color & 16777215;
         this.face(t00, t10, t11, t01, 0.0F, -1.0F, 0.0F, solid);
         this.face(b01, b11, b10, b00, 0.0F, 1.0F, 0.0F, solid);
         this.face(b00, b10, t10, t00, 0.0F, 0.0F, -1.0F, solid);
         this.face(b11, b01, t01, t11, 0.0F, 0.0F, 1.0F, solid);
         this.face(b01, b00, t00, t01, -1.0F, 0.0F, 0.0F, solid);
         this.face(b10, b11, t11, t10, 1.0F, 0.0F, 0.0F, solid);
      }

      private void face(float[] a, float[] b, float[] c, float[] d, float nx, float ny, float nz, int color) {
         CosmeticsFeatureRenderer.vertex(this.entry, this.vc, a[0] * 0.0625F, a[1] * 0.0625F, a[2] * 0.0625F, 0.0F, 0.0F, this.light, color, nx, ny, nz);
         CosmeticsFeatureRenderer.vertex(this.entry, this.vc, b[0] * 0.0625F, b[1] * 0.0625F, b[2] * 0.0625F, 1.0F, 0.0F, this.light, color, nx, ny, nz);
         CosmeticsFeatureRenderer.vertex(this.entry, this.vc, c[0] * 0.0625F, c[1] * 0.0625F, c[2] * 0.0625F, 1.0F, 1.0F, this.light, color, nx, ny, nz);
         CosmeticsFeatureRenderer.vertex(this.entry, this.vc, d[0] * 0.0625F, d[1] * 0.0625F, d[2] * 0.0625F, 0.0F, 1.0F, this.light, color, nx, ny, nz);
      }
   }
}
