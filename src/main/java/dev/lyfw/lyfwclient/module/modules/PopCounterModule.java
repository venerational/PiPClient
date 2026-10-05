package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.LyfwClient;
import dev.lyfw.lyfwclient.mixin.DisplayEntityViewRangeAccessor;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.KeybindSetting;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.Display.TextDisplay.TextRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopCounterModule extends Module {
   private static final Logger LOGGER = LoggerFactory.getLogger("lyfw-nametag");
   private static final Map<UUID, Long> LYFWCLIENT$LAST_LOGGED = new ConcurrentHashMap<>();
   private final ColorSetting nametagIconColor = this.register(new ColorSetting("Nametag Icon Color", -41892));
   private final EnumSetting<TotemPopTracker.PopIcon> nametagIcon = this.register(new EnumSetting<>("Nametag Icon", TotemPopTracker.PopIcon.HEART));
   private final BooleanSetting gradientColor = this.register(new BooleanSetting("Gradient By Pops", false));
   private final BooleanSetting showSeparator = this.register(new BooleanSetting("Show Separator", true));
   private final ColorSetting separatorColor = this.register(new ColorSetting("Separator Color", -5592406));
   private final KeybindSetting resetKeybind = this.register(new KeybindSetting("Reset Keybind", LyfwClient.resetPopCounterKey));

   public PopCounterModule() {
      super("Pop Counter", "Appends the totem-pop count next to a player's nametag once they've popped at least once.", Category.RENDER, false);
   }

   @Override
   public void init() {
      super.init();
      WorldRenderEvents.AFTER_ENTITIES.register(this::renderNametagSuffixes);
   }

   public String nametagIconSymbol() {
      return this.nametagIcon.get().symbol;
   }

   public int nametagIconColor() {
      return this.nametagIconColor.get();
   }

   public boolean showSeparator() {
      return this.showSeparator.get();
   }

   public int separatorColor() {
      return this.separatorColor.get();
   }

   public int colorForCount(int count) {
      if (!this.gradientColor.get()) {
         return this.nametagIconColor();
      } else {
         int green = 65280;
         int yellow = 16776960;
         int red = 16711680;
         int rgb;
         if (count <= 1) {
            rgb = green;
         } else if (count < 5) {
            rgb = lerpRgb(green, yellow, (count - 1) / 4.0F);
         } else if (count < 8) {
            rgb = lerpRgb(yellow, red, (count - 5) / 3.0F);
         } else {
            rgb = red;
         }

         return 0xFF000000 | rgb;
      }
   }

   private static int lerpRgb(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int r1 = from >> 16 & 0xFF;
      int g1 = from >> 8 & 0xFF;
      int b1 = from & 0xFF;
      int r2 = to >> 16 & 0xFF;
      int g2 = to >> 8 & 0xFF;
      int b2 = to & 0xFF;
      int r = Math.round(r1 + (r2 - r1) * t);
      int g = Math.round(g1 + (g2 - g1) * t);
      int b = Math.round(b1 + (b2 - b1) * t);
      return r << 16 | g << 8 | b;
   }

   private void renderNametagSuffixes(WorldRenderContext context) {
      if (this.isEnabled()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.player != null) {
            CameraRenderState cameraState = context.worldState().cameraRenderState;
            PoseStack matrices = context.matrices();
            MultiBufferSource consumers = context.consumers();
            Font textRenderer = mc.font;
            String symbol = this.nametagIconSymbol();
            int bgAlpha = (int)(mc.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            float tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);

            for (Player player : mc.level.players()) {
               if (player != mc.player && !(mc.player.distanceToSqr(player) > 40000.0) && !TotemPopTracker.isNametagEntityHandled(player.getUUID())) {
                  int count = TotemPopTracker.getPopCount(player.getUUID());
                  if (count > 0) {
                     Vec3 lerpedPos = player.getPosition(tickDelta);
                     TextDisplay anchorDisplay = findDisplayAnchor(player, mc.player);
                     Vec3 anchor = anchorDisplay != null ? anchorDisplay.getPosition(tickDelta) : null;
                     String tier;
                     if (anchor != null) {
                        tier = "DISPLAY_ANCHOR";
                     } else if (hasVanillaLabel(player, mc.player)) {
                        Vec3 labelOffset = player.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, player.getYRot(tickDelta));
                        if (labelOffset != null) {
                           anchor = lerpedPos.add(labelOffset.x, labelOffset.y + 0.5, labelOffset.z);
                           tier = "VANILLA_LABEL";
                        } else {
                           tier = "SKIPPED(no NAME_TAG point)";
                        }
                     } else {
                        tier = "SKIPPED(no real nametag found)";
                     }

                     long now = System.currentTimeMillis();
                     Long lastLogged = LYFWCLIENT$LAST_LOGGED.get(player.getUUID());
                     if (lastLogged == null || now - lastLogged > 3000L) {
                        LYFWCLIENT$LAST_LOGGED.put(player.getUUID(), now);
                        StringBuilder nearbyDump = new StringBuilder();
                        if (anchor == null) {
                           AABB wideBox = player.getBoundingBox().inflate(32.0, 32.0, 32.0);

                           for (TextDisplay display : player.level().getEntitiesOfClass(TextDisplay.class, wideBox, e -> true)) {
                              Vec3 rel = display.position().subtract(player.position());
                              nearbyDump.append(String.format("[rel=(%.2f,%.2f,%.2f) vehicle=%s] ", rel.x, rel.y, rel.z, display.getVehicle()));
                           }
                        }

                        LOGGER.warn(
                           "Pop Counter overlay: {} tier={} playerPos=({}, {}, {}) height={} anchorWorldPos=({}) hasPassengers={} team={} nearbyDisplays(32blk)=[{}]",
                           new Object[]{
                              player.getName().getString(),
                              tier,
                              lerpedPos.x,
                              lerpedPos.y,
                              lerpedPos.z,
                              player.getBbHeight(),
                              anchor,
                              player.isVehicle(),
                              player.getTeam(),
                              nearbyDump
                           }
                        );
                     }

                     if (anchor != null) {
                        double cutoffSquared = anchorDisplay != null
                           ? Mth.square(((DisplayEntityViewRangeAccessor)anchorDisplay).lyfwclient$getViewRange() * 64.0 * Entity.getViewScale())
                           : 4096.0;
                        Entity distanceTarget = (Entity)(anchorDisplay != null ? anchorDisplay : player);
                        if (!(mc.player.distanceToSqr(distanceTarget) > cutoffSquared)) {
                           double renderX = anchor.x - cameraState.pos.x;
                           double renderY = anchor.y - cameraState.pos.y;
                           double renderZ = anchor.z - cameraState.pos.z;
                           MutableComponent label = Component.empty();
                           if (this.showSeparator()) {
                              label = label.append(Component.literal("| ").withStyle(s -> s.withColor(this.separatorColor())));
                           }

                           label = label.append(Component.literal(symbol + count).withStyle(s -> s.withColor(this.colorForCount(count))));
                           TextRenderState data = anchorDisplay != null ? anchorDisplay.textRenderState() : null;
                           float offsetX;
                           if (data != null) {
                              List<FormattedCharSequence> wrapped = textRenderer.split(data.text(), Math.max(1, data.lineWidth()));
                              int firstLineWidth = wrapped.isEmpty() ? 0 : textRenderer.width(wrapped.get(0));
                              offsetX = firstLineWidth / 2.0F + 4.0F;
                           } else {
                              offsetX = textRenderer.width(player.getDisplayName()) / 2.0F + 4.0F;
                           }

                           float offsetY = 0.0F;
                           int light = LevelRenderer.getLightColor(mc.level, BlockPos.containing(lerpedPos.add(0.0, player.getBbHeight(), 0.0)));
                           matrices.pushPose();
                           matrices.translate(renderX, renderY, renderZ);
                           matrices.mulPose(cameraState.orientation);
                           matrices.scale(0.025F, -0.025F, 0.025F);
                           Matrix4f matrix = new Matrix4f(matrices.last().pose());
                           textRenderer.drawInBatch(
                              label,
                              offsetX,
                              offsetY,
                              -1,
                              false,
                              matrix,
                              consumers,
                              DisplayMode.NORMAL,
                              bgAlpha,
                              LightTexture.lightCoordsWithEmission(light, 2)
                           );
                           matrices.popPose();
                        }
                     }
                  }
               }
            }

            TotemPopTracker.clearNametagEntityHandledMarks();
         }
      }
   }

   private static TextDisplay findDisplayAnchor(Player player, Player localPlayer) {
      TextDisplay verified = TotemPopTracker.getVerifiedNametagEntity(player.getUUID());
      if (verified != null) {
         return verified;
      } else {
         for (Entity passenger : player.getPassengers()) {
            if (passenger instanceof TextDisplay display) {
               return display;
            }
         }

         TextDisplay byVehicleName = findByVehicleName(player, localPlayer);
         if (byVehicleName != null) {
            return byVehicleName;
         } else {
            AABB searchBox = player.getBoundingBox().inflate(1.5, 3.0, 1.5);
            List<TextDisplay> nearby = player.level().getEntitiesOfClass(TextDisplay.class, searchBox, e -> true);
            TextDisplay nearest = null;
            double nearestDistanceSquared = Double.MAX_VALUE;

            for (TextDisplay display : nearby) {
               double distanceSquared = display.distanceToSqr(player);
               if (distanceSquared < nearestDistanceSquared) {
                  nearestDistanceSquared = distanceSquared;
                  nearest = display;
               }
            }

            return nearest;
         }
      }
   }

   private static TextDisplay findByVehicleName(Player player, Player localPlayer) {
      AABB wideBox = localPlayer.getBoundingBox().inflate(64.0, 64.0, 64.0);
      List<TextDisplay> nearby = localPlayer.level().getEntitiesOfClass(TextDisplay.class, wideBox, e -> true);
      String name = player.getName().getString();
      TextDisplay nearest = null;
      double nearestDistanceSquared = Double.MAX_VALUE;

      for (TextDisplay display : nearby) {
         if (display.getVehicle() instanceof Player ridden && ridden != player && ridden.getName().getString().equals(name)) {
            double distanceSquared = display.distanceToSqr(localPlayer);
            if (distanceSquared < nearestDistanceSquared) {
               nearestDistanceSquared = distanceSquared;
               nearest = display;
            }
         }
      }

      return nearest;
   }

   private static boolean hasVanillaLabel(Player target, Player localPlayer) {
      if (target.isVehicle()) {
         return false;
      } else {
         boolean visibleToLocal = !target.isInvisibleTo(localPlayer);
         Team team = target.getTeam();
         if (team == null) {
            return visibleToLocal;
         } else {
            Team localTeam = localPlayer.getTeam();

            return switch (team.getNameTagVisibility()) {
               case ALWAYS -> visibleToLocal;
               case NEVER -> false;
               case HIDE_FOR_OTHER_TEAMS -> localTeam == null
                  ? visibleToLocal
                  : team.isAlliedTo(localTeam) && (team.canSeeFriendlyInvisibles() || visibleToLocal);
               case HIDE_FOR_OWN_TEAM -> localTeam == null ? visibleToLocal : !team.isAlliedTo(localTeam) && visibleToLocal;
               default -> throw new MatchException(null, null);
            };
         }
      }
   }
}
