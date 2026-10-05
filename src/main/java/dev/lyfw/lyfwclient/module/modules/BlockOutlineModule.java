package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.lyfw.lyfwclient.gui.ModuleSettingsScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.BeforeBlockOutline;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockOutlineModule extends Module {
   private final BooleanSetting outlineEnabled = this.register(new BooleanSetting("Outline Enabled", true));
   private final ColorSetting outlineColor = this.register(new ColorSetting("Outline Color", -16777216));
   private final EnumSetting<BlockOutlineModule.OutlineType> outlineType = this.register(
      new EnumSetting<>("Outline Type", BlockOutlineModule.OutlineType.AIR_EXPOSED)
   );
   private final EnumSetting<BlockOutlineModule.DepthTestMode> lineDepthTest = this.register(
      new EnumSetting<>("Line Depth Test", BlockOutlineModule.DepthTestMode.ALWAYS_PASS)
   );
   private final SliderSetting lineWidth = this.register(new SliderSetting("Line Width", 2.5, 0.5, 10.0, 0.5, "px"));
   private final SliderSetting lineExpand = this.register(new SliderSetting("Line Expand", 0.0, -0.25, 0.25, 0.01, ""));
   private final SliderSetting cutFromCenter = this.register(new SliderSetting("Line Center Gap", 0.25, 0.0, 0.49, 0.01, ""));
   private final BooleanSetting fillEnabled = this.register(new BooleanSetting("Fill Enabled", true));
   private final ColorSetting fillColor = this.register(new ColorSetting("Fill Color", Integer.MIN_VALUE));
   private final EnumSetting<BlockOutlineModule.OutlineType> fillType = this.register(new EnumSetting<>("Fill Type", BlockOutlineModule.OutlineType.ALL));
   private final EnumSetting<BlockOutlineModule.DepthTestMode> fillDepthTest = this.register(
      new EnumSetting<>("Fill Depth Test", BlockOutlineModule.DepthTestMode.HIDDEN_ONLY)
   );
   private final SliderSetting fillExpand = this.register(new SliderSetting("Fill Expand", 0.001, -0.25, 0.25, 0.01, ""));
   private final BooleanSetting doEasing = this.register(new BooleanSetting("Smooth Movement", true));
   private final SliderSetting easeSpeed = this.register(new SliderSetting("Movement Speed", 20.0, 1.0, 60.0, 1.0, ""));
   private final BooleanSetting fadeIn = this.register(new BooleanSetting("Fade In", true));
   private final SliderSetting fadeInSpeed = this.register(new SliderSetting("Fade In Speed", 15.0, 1.0, 60.0, 1.0, ""));
   private final BooleanSetting fadeOut = this.register(new BooleanSetting("Fade Out", true));
   private final SliderSetting fadeOutSpeed = this.register(new SliderSetting("Fade Out Speed", 15.0, 1.0, 60.0, 1.0, ""));
   private final BooleanSetting allowEntities = this.register(new BooleanSetting("Highlight Entities", true));
   private static final RenderPipeline LINES_ALWAYS_PASS = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.LINES_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("lyfw-client", "pipeline/block_outline_lines_always_pass"))
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   private static final RenderPipeline LINES_HIDDEN_ONLY = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.LINES_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("lyfw-client", "pipeline/block_outline_lines_hidden_only"))
         .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
         .withDepthWrite(false)
         .build()
   );
   private static final RenderPipeline FILL_ALWAYS_PASS = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("lyfw-client", "pipeline/block_outline_fill_always_pass"))
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withCull(false)
         .build()
   );
   private static final RenderPipeline FILL_HIDDEN_ONLY = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET})
         .withLocation(Identifier.fromNamespaceAndPath("lyfw-client", "pipeline/block_outline_fill_hidden_only"))
         .withDepthTestFunction(DepthTestFunction.GREATER_DEPTH_TEST)
         .withCull(false)
         .build()
   );
   private static final RenderType LINES_ALWAYS_PASS_LAYER = RenderType.create(
      "lyfw-client:block_outline_lines_always_pass",
      RenderSetup.builder(LINES_ALWAYS_PASS)
         .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
         .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
         .createRenderSetup()
   );
   private static final RenderType LINES_HIDDEN_ONLY_LAYER = RenderType.create(
      "lyfw-client:block_outline_lines_hidden_only",
      RenderSetup.builder(LINES_HIDDEN_ONLY)
         .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
         .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
         .createRenderSetup()
   );
   private static final RenderType FILL_ALWAYS_PASS_LAYER = RenderType.create(
      "lyfw-client:block_outline_fill_always_pass", RenderSetup.builder(FILL_ALWAYS_PASS).sortOnUpload().createRenderSetup()
   );
   private static final RenderType FILL_HIDDEN_ONLY_LAYER = RenderType.create(
      "lyfw-client:block_outline_fill_hidden_only", RenderSetup.builder(FILL_HIDDEN_ONLY).sortOnUpload().createRenderSetup()
   );
   private static final Set<Direction> ALL_FACES = EnumSet.allOf(Direction.class);
   private static final BlockOutlineModule.Edge[] BOX_EDGES = new BlockOutlineModule.Edge[]{
      new BlockOutlineModule.Edge(0, 1, Direction.DOWN, Direction.NORTH),
      new BlockOutlineModule.Edge(1, 2, Direction.DOWN, Direction.EAST),
      new BlockOutlineModule.Edge(2, 3, Direction.DOWN, Direction.SOUTH),
      new BlockOutlineModule.Edge(3, 0, Direction.DOWN, Direction.WEST),
      new BlockOutlineModule.Edge(4, 5, Direction.UP, Direction.NORTH),
      new BlockOutlineModule.Edge(5, 6, Direction.UP, Direction.EAST),
      new BlockOutlineModule.Edge(6, 7, Direction.UP, Direction.SOUTH),
      new BlockOutlineModule.Edge(7, 4, Direction.UP, Direction.WEST),
      new BlockOutlineModule.Edge(0, 4, Direction.NORTH, Direction.WEST),
      new BlockOutlineModule.Edge(1, 5, Direction.NORTH, Direction.EAST),
      new BlockOutlineModule.Edge(2, 6, Direction.SOUTH, Direction.EAST),
      new BlockOutlineModule.Edge(3, 7, Direction.SOUTH, Direction.WEST)
   };
   private AABB easedBox;
   private float alpha = 0.0F;

   public BlockOutlineModule() {
      super(
         "Block Outline",
         "Custom-styled outline for the block or entity you're looking at - matches \"Custom Block Highlight\" (core feature set). Configurable colors, shape, line width, and see-through-walls modes.",
         Category.RENDER,
         false
      );
      this.outlineEnabled.group = "Outline";
      this.outlineColor.group = "Outline";
      this.outlineType.group = "Outline";
      this.lineDepthTest.group = "Outline";
      this.lineWidth.group = "Outline";
      this.lineExpand.group = "Outline";
      this.cutFromCenter.group = "Outline";
      this.fillEnabled.group = "Fill";
      this.fillColor.group = "Fill";
      this.fillType.group = "Fill";
      this.fillDepthTest.group = "Fill";
      this.fillExpand.group = "Fill";
      this.doEasing.group = "Animation";
      this.easeSpeed.group = "Animation";
      this.fadeIn.group = "Animation";
      this.fadeInSpeed.group = "Animation";
      this.fadeOut.group = "Animation";
      this.fadeOutSpeed.group = "Animation";
      this.allowEntities.group = "General";
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return new ModuleSettingsScreen(parent, this);
   }

   @Override
   public void init() {
      WorldRenderEvents.END_MAIN.register(this::render);
      WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((BeforeBlockOutline)(context, outlineRenderState) -> !this.isEnabled());
   }

   private void render(WorldRenderContext context) {
      if (this.isEnabled()) {
         Minecraft mc = Minecraft.getInstance();
         ClientLevel world = mc.level;
         if (world != null && mc.player != null) {
            HitResult hit = mc.hitResult;
            AABB targetBox = null;
            BlockPos blockPos = null;
            Direction lookAtSide = null;
            VoxelShape blockShape = null;
            if (hit instanceof BlockHitResult blockHit && hit.getType() == Type.BLOCK) {
               blockPos = blockHit.getBlockPos();
               lookAtSide = blockHit.getDirection();
               BlockState state = world.getBlockState(blockPos);
               blockShape = state.getShape(world, blockPos);
               AABB localBox = blockShape.isEmpty() ? new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) : blockShape.bounds();
               targetBox = localBox.move(blockPos);
            } else if (hit instanceof EntityHitResult entityHit && hit.getType() == Type.ENTITY && this.allowEntities.get()) {
               Entity entity = entityHit.getEntity();
               targetBox = entity.getBoundingBox();
            }

            boolean hasTarget = targetBox != null;
            if (hasTarget) {
               this.easedBox = this.easedBox != null && this.doEasing.get()
                  ? new AABB(
                     ease(this.easedBox.minX, targetBox.minX, this.easeSpeed.get()),
                     ease(this.easedBox.minY, targetBox.minY, this.easeSpeed.get()),
                     ease(this.easedBox.minZ, targetBox.minZ, this.easeSpeed.get()),
                     ease(this.easedBox.maxX, targetBox.maxX, this.easeSpeed.get()),
                     ease(this.easedBox.maxY, targetBox.maxY, this.easeSpeed.get()),
                     ease(this.easedBox.maxZ, targetBox.maxZ, this.easeSpeed.get())
                  )
                  : targetBox;
            }

            if (hasTarget) {
               this.alpha = this.fadeIn.get() ? (float)ease(this.alpha, 1.0, this.fadeInSpeed.get()) : 1.0F;
            } else {
               this.alpha = this.fadeOut.get() ? (float)ease(this.alpha, 0.0, this.fadeOutSpeed.get()) : 0.0F;
            }

            if (!(this.alpha < 0.004F) && this.easedBox != null) {
               Camera camera = mc.gameRenderer.getMainCamera();
               Vec3 camPos = camera.position();
               PoseStack matrices = context.matrices();
               matrices.pushPose();
               matrices.translate(-camPos.x, -camPos.y, -camPos.z);

               try {
                  Set<Direction> outlineFaces = blockPos != null ? visibleFaces(this.outlineType.get(), world, blockPos, lookAtSide) : ALL_FACES;
                  Set<Direction> fillFaces = blockPos != null ? visibleFaces(this.fillType.get(), world, blockPos, lookAtSide) : ALL_FACES;
                  if (this.fillEnabled.get()) {
                     AABB box = this.easedBox.inflate(this.fillExpand.get());
                     int argb = withAlpha(this.fillColor.get(), this.alpha);
                     VertexConsumer vc = context.consumers().getBuffer(fillLayer(this.fillDepthTest.get()));
                     drawFillFaces(matrices, vc, box, argb, fillFaces);
                  }

                  if (this.outlineEnabled.get()) {
                     int argb = withAlpha(this.outlineColor.get(), this.alpha);
                     VertexConsumer vc = context.consumers().getBuffer(lineLayer(this.lineDepthTest.get()));
                     float width = (float)this.lineWidth.get().doubleValue();
                     if (blockPos != null && this.outlineType.get() == BlockOutlineModule.OutlineType.EDGES && blockShape != null && !blockShape.isEmpty()) {
                        ShapeRenderer.renderShape(matrices, vc, blockShape, blockPos.getX(), blockPos.getY(), blockPos.getZ(), argb, width);
                     } else {
                        AABB box = this.easedBox.inflate(this.lineExpand.get());
                        drawBoxEdges(matrices, vc, box, argb, width, outlineFaces, (float)this.cutFromCenter.get().doubleValue());
                     }
                  }
               } finally {
                  matrices.popPose();
               }
            }
         }
      }
   }

   private static double ease(double start, double end, double speed) {
      int fps = Math.max(1, Minecraft.getInstance().getFps());
      return start + (end - start) * (1.0 - Math.exp(-(1.0 / fps) * speed));
   }

   private static int withAlpha(int argb, float multiplier) {
      int a = argb >>> 24 & 0xFF;
      int newA = Math.round(a * Mth.clamp(multiplier, 0.0F, 1.0F));
      return newA << 24 | argb & 16777215;
   }

   private static RenderType lineLayer(BlockOutlineModule.DepthTestMode mode) {
      return switch (mode) {
         case NORMAL -> RenderTypes.lines();
         case ALWAYS_PASS -> LINES_ALWAYS_PASS_LAYER;
         case HIDDEN_ONLY -> LINES_HIDDEN_ONLY_LAYER;
      };
   }

   private static RenderType fillLayer(BlockOutlineModule.DepthTestMode mode) {
      return switch (mode) {
         case NORMAL -> RenderTypes.debugQuads();
         case ALWAYS_PASS -> FILL_ALWAYS_PASS_LAYER;
         case HIDDEN_ONLY -> FILL_HIDDEN_ONLY_LAYER;
      };
   }

   private static Set<Direction> visibleFaces(BlockOutlineModule.OutlineType type, ClientLevel world, BlockPos pos, Direction lookAtSide) {
      return (Set<Direction>)(switch (type) {
         case AIR_EXPOSED -> {
            Set<Direction> concealed = concealedFaces(world, pos);
            yield EnumSet.complementOf(concealed.isEmpty() ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(concealed));
         }
         case CONCEALED -> concealedFaces(world, pos);
         case LOOKAT -> lookAtSide != null ? EnumSet.of(lookAtSide) : ALL_FACES;
         default -> ALL_FACES;
      });
   }

   private static Set<Direction> concealedFaces(ClientLevel world, BlockPos pos) {
      Set<Direction> faces = EnumSet.noneOf(Direction.class);

      for (Direction dir : Direction.values()) {
         if (!world.isEmptyBlock(pos.relative(dir))) {
            faces.add(dir);
         }
      }

      return faces;
   }

   private static void drawFillFaces(PoseStack matrices, VertexConsumer vc, AABB box, int argb, Set<Direction> faces) {
      Pose entry = matrices.last();
      float minX = (float)box.minX;
      float minY = (float)box.minY;
      float minZ = (float)box.minZ;
      float maxX = (float)box.maxX;
      float maxY = (float)box.maxY;
      float maxZ = (float)box.maxZ;
      if (faces.contains(Direction.DOWN)) {
         quad(entry, vc, argb, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ);
      }

      if (faces.contains(Direction.UP)) {
         quad(entry, vc, argb, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ);
      }

      if (faces.contains(Direction.NORTH)) {
         quad(entry, vc, argb, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ);
      }

      if (faces.contains(Direction.SOUTH)) {
         quad(entry, vc, argb, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ);
      }

      if (faces.contains(Direction.WEST)) {
         quad(entry, vc, argb, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ);
      }

      if (faces.contains(Direction.EAST)) {
         quad(entry, vc, argb, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ);
      }
   }

   private static void quad(
      Pose entry,
      VertexConsumer vc,
      int argb,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float x4,
      float y4,
      float z4
   ) {
      vc.addVertex(entry, x1, y1, z1).setColor(argb);
      vc.addVertex(entry, x2, y2, z2).setColor(argb);
      vc.addVertex(entry, x3, y3, z3).setColor(argb);
      vc.addVertex(entry, x4, y4, z4).setColor(argb);
   }

   private static void drawBoxEdges(PoseStack matrices, VertexConsumer vc, AABB box, int argb, float width, Set<Direction> faces, float centerGap) {
      Pose entry = matrices.last();
      float[][] corners = new float[][]{
         {(float)box.minX, (float)box.minY, (float)box.minZ},
         {(float)box.maxX, (float)box.minY, (float)box.minZ},
         {(float)box.maxX, (float)box.minY, (float)box.maxZ},
         {(float)box.minX, (float)box.minY, (float)box.maxZ},
         {(float)box.minX, (float)box.maxY, (float)box.minZ},
         {(float)box.maxX, (float)box.maxY, (float)box.minZ},
         {(float)box.maxX, (float)box.maxY, (float)box.maxZ},
         {(float)box.minX, (float)box.maxY, (float)box.maxZ}
      };

      for (BlockOutlineModule.Edge edge : BOX_EDGES) {
         if (faces.contains(edge.faceA()) || faces.contains(edge.faceB())) {
            float[] p1 = corners[edge.c1()];
            float[] p2 = corners[edge.c2()];
            edgeLine(entry, vc, argb, width, p1, p2, centerGap);
         }
      }
   }

   private static void edgeLine(Pose entry, VertexConsumer vc, int argb, float width, float[] p1, float[] p2, float centerGap) {
      float nx = p2[0] - p1[0];
      float ny = p2[1] - p1[1];
      float nz = p2[2] - p1[2];
      float len = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
      if (len > 1.0E-5F) {
         nx /= len;
         ny /= len;
         nz /= len;
      }

      if (centerGap <= 0.0F) {
         vertex(entry, vc, argb, width, p1, nx, ny, nz);
         vertex(entry, vc, argb, width, p2, nx, ny, nz);
      } else {
         float[] mid = lerp(p1, p2, 0.5F);
         float[] near1 = lerp(p1, mid, 1.0F - centerGap);
         float[] near2 = lerp(p2, mid, 1.0F - centerGap);
         vertex(entry, vc, argb, width, p1, nx, ny, nz);
         vertex(entry, vc, argb, width, near1, nx, ny, nz);
         vertex(entry, vc, argb, width, near2, nx, ny, nz);
         vertex(entry, vc, argb, width, p2, nx, ny, nz);
      }
   }

   private static float[] lerp(float[] a, float[] b, float t) {
      return new float[]{a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, a[2] + (b[2] - a[2]) * t};
   }

   private static void vertex(Pose entry, VertexConsumer vc, int argb, float width, float[] p, float nx, float ny, float nz) {
      vc.addVertex(entry, p[0], p[1], p[2]).setColor(argb).setNormal(entry, nx, ny, nz).setLineWidth(width);
   }

   public static enum DepthTestMode {
      NORMAL,
      ALWAYS_PASS,
      HIDDEN_ONLY;
   }

   private record Edge(int c1, int c2, Direction faceA, Direction faceB) {
   }

   public static enum OutlineType {
      ALL,
      EDGES,
      AIR_EXPOSED,
      CONCEALED,
      LOOKAT;
   }
}
