package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lyfw.lyfwclient.mixin.PlayerTransformInvoker;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;

public class PopChamsModule extends Module {
   private static final int MAX_IMAGES = 24;
   private static final ModelLayerLocation SLIM_LAYER = new ModelLayerLocation(Identifier.withDefaultNamespace("player_slim"), "main");
   private final ColorSetting color = this.register(new ColorSetting("Color", -1275118756));
   private final SliderSetting lifetime = this.register(new SliderSetting("Lifetime", 1.5, 0.2, 8.0, 0.1, "s"));
   private final BooleanSetting fadeOut = this.register(new BooleanSetting("Fade Out", true));
   private final BooleanSetting showSelf = this.register(new BooleanSetting("Show On Self", true));
   private final SliderSetting renderDistance = this.register(new SliderSetting("Render Distance", 48.0, 8.0, 128.0, 1.0, "blocks"));
   private final Set<Integer> pending = ConcurrentHashMap.newKeySet();
   private final List<PopChamsModule.Ghost> ghosts = new ArrayList<>();
   private PlayerModel wideModel;
   private PlayerModel slimModel;

   public PopChamsModule() {
      super("Pop Chams", "Leaves a fading ghost of a player where they popped a totem, in their own pose.", Category.RENDER, false);
      this.color.group = "Ghost";
      this.lifetime.group = "Ghost";
      this.fadeOut.group = "Ghost";
      this.showSelf.group = "Ghost";
      this.renderDistance.group = "Ghost";
   }

   public static PopChamsModule get() {
      return ModuleManager.get("Pop Chams") instanceof PopChamsModule chams ? chams : null;
   }

   @Override
   public void init() {
      WorldRenderEvents.END_MAIN.register(this::render);
   }

   public void onPop(Player player) {
      Minecraft mc = Minecraft.getInstance();
      boolean mine = mc.player != null && player.getId() == mc.player.getId();
      if (this.isEnabled() && (!mine || this.showSelf.get())) {
         this.pending.add(player.getId());
      }
   }

   public void captureIfPending(AvatarRenderState live, AvatarRenderer<?> renderer) {
      if (!this.pending.isEmpty() && this.pending.remove(live.id)) {
         AvatarRenderState frozen = copyOf(live);
         if (frozen != null) {
            boolean slim = live.skin != null && live.skin.model() == PlayerModelType.SLIM;
            Identifier texture = renderer.getTextureLocation(live);
            synchronized (this.ghosts) {
               this.ghosts.add(new PopChamsModule.Ghost(frozen, slim, texture, new Vec3(live.x, live.y, live.z), System.nanoTime()));

               while (this.ghosts.size() > 24) {
                  this.ghosts.remove(0);
               }
            }
         }
      }
   }

   private static AvatarRenderState copyOf(AvatarRenderState live) {
      try {
         AvatarRenderState copy = new AvatarRenderState();

         for (Class<?> type = live.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
               if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                  field.setAccessible(true);
                  field.set(copy, field.get(live));
               }
            }
         }

         return copy;
      } catch (RuntimeException | ReflectiveOperationException var7) {
         System.out.println("[Pip Client] Pop Chams could not copy the player's pose (" + var7 + ")");
         return null;
      }
   }

   private PlayerModel model(boolean slim) {
      Minecraft mc = Minecraft.getInstance();
      if (slim) {
         if (this.slimModel == null) {
            this.slimModel = new PlayerModel(mc.getEntityModels().bakeLayer(SLIM_LAYER), true);
         }

         return this.slimModel;
      } else {
         if (this.wideModel == null) {
            this.wideModel = new PlayerModel(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
         }

         return this.wideModel;
      }
   }

   private void render(WorldRenderContext context) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null && !this.ghosts.isEmpty()) {
         long now = System.nanoTime();
         long span = Math.max(1L, (long)(this.lifetime.get() * 1.0E9));
         Vec3 camPos = mc.gameRenderer.getMainCamera().position();
         double maxDistSq = this.renderDistance.get() * this.renderDistance.get();
         int base = this.color.get();
         PoseStack matrices = context.matrices();
         synchronized (this.ghosts) {
            Iterator<PopChamsModule.Ghost> it = this.ghosts.iterator();

            while (it.hasNext()) {
               PopChamsModule.Ghost ghost = it.next();
               float life = (float)(now - ghost.takenAt()) / (float)span;
               if (!(life >= 1.0F) && this.isEnabled()) {
                  if (!(camPos.distanceToSqr(ghost.pos()) > maxDistSq)) {
                     int alpha = Math.round((base >>> 24 & 0xFF) * (this.fadeOut.get() ? 1.0F - life : 1.0F));
                     if (alpha > 2) {
                        this.draw(context, matrices, ghost, camPos, alpha << 24 | base & 16777215);
                     }
                  }
               } else {
                  it.remove();
               }
            }
         }
      }
   }

   private void draw(WorldRenderContext context, PoseStack matrices, PopChamsModule.Ghost ghost, Vec3 camPos, int color) {
      PlayerModel model = this.model(ghost.slim());
      AvatarRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(Minecraft.getInstance().player) instanceof AvatarRenderer<?> found
         ? found
         : null;
      if (renderer != null) {
         VertexConsumer vc = context.consumers().getBuffer(RenderTypes.entityTranslucent(ghost.texture()));
         matrices.pushPose();
         matrices.translate((float)(ghost.pos().x - camPos.x), (float)(ghost.pos().y - camPos.y), (float)(ghost.pos().z - camPos.z));
         PlayerTransformInvoker transforms = (PlayerTransformInvoker)renderer;
         transforms.lyfwclient$setupTransforms(ghost.state(), matrices, ghost.state().bodyRot, 1.0F);
         matrices.scale(-1.0F, -1.0F, 1.0F);
         transforms.lyfwclient$scale(ghost.state(), matrices);
         matrices.translate(0.0F, -1.501F, 0.0F);
         model.setupAnim(ghost.state());
         model.renderToBuffer(matrices, vc, 15728880, OverlayTexture.NO_OVERLAY, color);
         matrices.popPose();
      }
   }

   private record Ghost(AvatarRenderState state, boolean slim, Identifier texture, Vec3 pos, long takenAt) {
   }
}
