package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lyfw.lyfwclient.mixin.PlayerTransformInvoker;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;

public class PlayerGlowModule extends Module {
   private static final ModelLayerLocation SLIM_LAYER = new ModelLayerLocation(Identifier.withDefaultNamespace("player_slim"), "main");
   private static final int FULL_BRIGHT = 15728880;
   private static final float MIDDLE = 0.5F;
   private static final float FEET = 1.501F;
   private static final float PUSH = 0.7F;
   private static final float HALF_WIDTH = 0.3F;
   private static final float HALF_HEIGHT = 0.95F;
   private final EnumSetting<PlayerGlowModule.Who> who = this.register(new EnumSetting<>("Show On", PlayerGlowModule.Who.OTHERS));
   private final BooleanSetting rainbow = this.register(new BooleanSetting("Color Changing", true));
   private final SliderSetting speed = this.register(new SliderSetting("Change Speed", 1.0, 0.1, 5.0, 0.1, "x"));
   private final BooleanSetting perPlayer = this.register(new BooleanSetting("Different Per Player", false));
   private final ColorSetting color = this.register(new ColorSetting("Color", -4821761));
   private final SliderSetting range = this.register(new SliderSetting("Range", 0.0, 0.0, 128.0, 1.0, " blocks"));
   private final SliderSetting fill = this.register(new SliderSetting("Fill", 50.0, 0.0, 100.0, 1.0, "%"));
   private final SliderSetting outline = this.register(new SliderSetting("Outline", 100.0, 0.0, 100.0, 1.0, "%"));
   private final SliderSetting outlineWidth = this.register(new SliderSetting("Outline Width", 2.0, 1.0, 6.0, 0.5, "px"));
   private static final CubeDeformation OVER_ARMOUR = new CubeDeformation(1.25F);
   private final PlayerModel[] models = new PlayerModel[4];

   public PlayerGlowModule() {
      super(
         "Player Glow",
         "cpvp.gg's glow: players coloured in, skin and armour, with a bright line round their edge. Leave Color Changing on for a colour that runs through the spectrum, or switch it off and pick your own. It never shows through blocks.",
         Category.RENDER,
         false
      );
      this.who.group = "Who";
      this.range.group = "Who";
      this.rainbow.group = "Color";
      this.speed.group = "Color";
      this.perPlayer.group = "Color";
      this.color.group = "Color";
      this.fill.group = "Look";
      this.outline.group = "Look";
      this.outlineWidth.group = "Look";
   }

   public static PlayerGlowModule get() {
      return ModuleManager.get("Player Glow") instanceof PlayerGlowModule glow ? glow : null;
   }

   @Override
   public void init() {
      WorldRenderEvents.AFTER_ENTITIES.register(this::render);
   }

   public boolean glows(Player player) {
      Minecraft client = Minecraft.getInstance();
      if (this.isEnabled() && client.player != null && !player.isInvisible() && !player.isSpectator()) {
         boolean self = player == client.player;

         boolean wanted = switch ((PlayerGlowModule.Who)this.who.get()) {
            case OTHERS -> !self;
            case EVERYONE -> true;
            case ONLY_ME -> self;
         };
         if (!wanted) {
            return false;
         } else {
            double limit = this.range.get();
            return limit <= 0.0 || self || client.player.distanceToSqr(player) <= limit * limit;
         }
      } else {
         return false;
      }
   }

   public int glowColor(Player player) {
      if (!this.rainbow.get()) {
         return 0xFF000000 | this.color.get() & 16777215;
      } else {
         double turn = System.currentTimeMillis() / 1000.0 * this.speed.get();
         float offset = this.perPlayer.get() ? (player.getUUID().hashCode() & 0xFF) / 255.0F : 0.0F;
         float hue = (float)(turn % 1.0) + offset;
         return 0xFF000000 | Color.HSBtoRGB(hue - (float)Math.floor(hue), 0.85F, 1.0F) & 16777215;
      }
   }

   private void render(WorldRenderContext context) {
      Minecraft client = Minecraft.getInstance();
      if (this.isEnabled() && client.level != null && client.player != null) {
         CosmeticsModule cosmetics = CosmeticsModule.get();
         if (cosmetics != null) {
            Vec3 camera = client.gameRenderer.getMainCamera().position();
            float tickDelta = client.getDeltaTracker().getGameTimeDeltaPartialTick(false);

            for (Player player : client.level.players()) {
               if (this.glows(player)) {
                  this.draw(context, player, camera, tickDelta, cosmetics.whiteTexture());
               }
            }
         }
      }
   }

   private void draw(WorldRenderContext context, Player player, Vec3 camera, float tickDelta, Identifier white) {
      Minecraft client = Minecraft.getInstance();
      if (client.getEntityRenderDispatcher().getRenderer(player) instanceof AvatarRenderer renderer) {
         AvatarRenderState state = (AvatarRenderState)renderer.createRenderState(player, tickDelta);
         double px = state.x - camera.x;
         double py = state.y - camera.y;
         double pz = state.z - camera.z;
         double middleY = py + 0.9;
         double distance = Math.sqrt(px * px + middleY * middleY + pz * pz);
         if (state.skin != null && distance > 0.8) {
            boolean slim = state.skin.model() == PlayerModelType.SLIM;
            PlayerModel bare = this.model(slim, false);
            PlayerModel armoured = this.model(slim, true);
            bare.setupAnim(state);
            armoured.setupAnim(state);
            boolean helmet = !state.headEquipment.isEmpty();
            boolean chest = !state.chestEquipment.isEmpty();
            boolean legs = !state.legsEquipment.isEmpty() || !state.feetEquipment.isEmpty();
            ModelPart[] model = new ModelPart[]{
               (helmet ? armoured : bare).head,
               (chest ? armoured : bare).body,
               (chest ? armoured : bare).rightArm,
               (chest ? armoured : bare).leftArm,
               (legs ? armoured : bare).rightLeg,
               (legs ? armoured : bare).leftLeg
            };
            int rgb = this.glowColor(player) & 16777215;
            VertexConsumer vc = context.consumers().getBuffer(RenderTypes.entityTranslucent(white));
            PoseStack matrices = context.matrices();
            int edge = Math.round(this.outline.get().floatValue() * 2.55F);
            if (edge > 0) {
               float pixel = worldPerPixel(client, distance + 0.7F);
               float line = pixel * this.outlineWidth.get().floatValue();
               float keep = (float)((distance + 0.7F) / distance);
               double away = 0.7F / distance;
               matrices.pushPose();
               matrices.translate((float)(px + px * away), (float)(py + middleY * away), (float)(pz + pz * away));
               this.place(matrices, renderer, state);
               this.shell(matrices, model, vc, keep * (1.0F + line / 0.3F), keep * (1.0F + line / 0.95F), edge << 24 | brighter(rgb));
               matrices.popPose();
            }
         }
      }
   }

   public int fillTint(Player player) {
      float amount = this.fill.get().floatValue() / 100.0F;
      int rgb = this.glowColor(player);
      int r = Math.round(255.0F + ((rgb >> 16 & 0xFF) - 255) * amount);
      int g = Math.round(255.0F + ((rgb >> 8 & 0xFF) - 255) * amount);
      int b = Math.round(255.0F + ((rgb & 0xFF) - 255) * amount);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private void place(PoseStack matrices, AvatarRenderer<?> renderer, AvatarRenderState state) {
      PlayerTransformInvoker transforms = (PlayerTransformInvoker)renderer;
      transforms.lyfwclient$setupTransforms(state, matrices, state.bodyRot, 1.0F);
      matrices.scale(-1.0F, -1.0F, 1.0F);
      transforms.lyfwclient$scale(state, matrices);
      matrices.translate(0.0F, -1.501F, 0.0F);
   }

   private void shell(PoseStack matrices, ModelPart[] model, VertexConsumer vc, float across, float up, int color) {
      matrices.pushPose();
      matrices.translate(0.0F, 0.5F, 0.0F);
      matrices.scale(across, up, across);
      matrices.translate(0.0F, -0.5F, 0.0F);

      for (ModelPart part : model) {
         part.render(matrices, vc, 15728880, OverlayTexture.NO_OVERLAY, color);
      }

      matrices.popPose();
   }

   private static float worldPerPixel(Minecraft client, double distance) {
      double fov = Math.toRadians(((Integer)client.options.fov().get()).intValue());
      int height = Math.max(1, client.getWindow().getHeight());
      return (float)(2.0 * distance * Math.tan(fov / 2.0) / height);
   }

   private static int brighter(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      r += (255 - r) * 2 / 5;
      g += (255 - g) * 2 / 5;
      b += (255 - b) * 2 / 5;
      return r << 16 | g << 8 | b;
   }

   private PlayerModel model(boolean slim, boolean armoured) {
      int which = (slim ? 1 : 0) + (armoured ? 2 : 0);
      if (this.models[which] == null) {
         Minecraft client = Minecraft.getInstance();
         ModelPart root = armoured
            ? LayerDefinition.create(PlayerModel.createMesh(OVER_ARMOUR, slim), 64, 64).bakeRoot()
            : client.getEntityModels().bakeLayer(slim ? SLIM_LAYER : ModelLayers.PLAYER);
         this.models[which] = new PlayerModel(root, slim);
      }

      return this.models[which];
   }

   public static enum Who {
      OTHERS("Everyone Else"),
      EVERYONE("Everyone"),
      ONLY_ME("Only Me");

      public final String title;

      private Who(String title) {
         this.title = title;
      }
   }
}
