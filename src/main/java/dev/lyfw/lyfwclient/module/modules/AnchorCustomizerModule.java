package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.render.GlowHalo;
import dev.lyfw.lyfwclient.render.SpriteTweaks;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;

public class AnchorCustomizerModule extends Module {
   private static final int SCAN_RADIUS = 32;
   private static final int SCAN_EVERY = 10;
   private final ColorSetting color = this.register(new ColorSetting("Color", -6599169).exemptFromGlobalColor());
   private final SliderSetting opacity = this.register(new SliderSetting("Opacity", 100.0, 10.0, 100.0, 5.0, "%"));
   private final BooleanSetting glow = this.register(new BooleanSetting("Glow", true));
   private final SliderSetting glowSize = this.register(new SliderSetting("Glow Size", 1.3, 0.5, 4.0, 0.1, " blocks"));
   private final SliderSetting glowStrength = this.register(new SliderSetting("Glow Strength", 50.0, 5.0, 100.0, 5.0, "%"));
   private final List<BlockPos> anchors = new ArrayList<>();
   private int scanTimer;
   private boolean builtTranslucent;

   public AnchorCustomizerModule() {
      super("Anchor Customizer", "Respawn anchors in any colour, see-through to any degree, with a glow round them if you want one.", Category.RENDER, false);
      this.color.group = "Look";
      this.opacity.group = "Look";
      this.glow.group = "Glow";
      this.glowSize.group = "Glow";
      this.glowStrength.group = "Glow";
   }

   public static AnchorCustomizerModule get() {
      return ModuleManager.get("Anchor Customizer") instanceof AnchorCustomizerModule anchors && anchors.isEnabled() ? anchors : null;
   }

   public static boolean wantsTranslucent() {
      AnchorCustomizerModule anchors = get();
      return anchors != null && anchors.opacity.getInt() < 100;
   }

   @Override
   public void init() {
      WorldRenderEvents.END_MAIN.register(this::renderHalos);
   }

   @Override
   public void tick() {
      boolean on = this.isEnabled();
      int alpha = Math.round(this.opacity.getInt() / 100.0F * 255.0F);
      SpriteTweaks.request("anchor_customizer", on ? Map.of(Blocks.RESPAWN_ANCHOR, new SpriteTweaks.Tweak(100, this.color.get() & 16777215, alpha)) : Map.of());
      Minecraft mc = Minecraft.getInstance();
      boolean translucent = wantsTranslucent();
      if (translucent != this.builtTranslucent) {
         this.builtTranslucent = translucent;
         if (mc.level != null) {
            mc.levelRenderer.allChanged();
         }
      }

      if (on && this.glow.get() && mc.level != null && mc.player != null) {
         if (this.scanTimer-- <= 0) {
            this.scanTimer = 10;
            this.scan(mc);
         }
      } else {
         this.anchors.clear();
      }
   }

   private void scan(Minecraft mc) {
      this.anchors.clear();
      BlockPos center = mc.player.blockPosition();
      int minCx = center.getX() - 32 >> 4;
      int maxCx = center.getX() + 32 >> 4;
      int minCz = center.getZ() - 32 >> 4;
      int maxCz = center.getZ() + 32 >> 4;
      int minY = Math.max(mc.level.getMinY(), center.getY() - 32);
      int maxY = Math.min(mc.level.getMaxY(), center.getY() + 32);

      for (int cx = minCx; cx <= maxCx; cx++) {
         for (int cz = minCz; cz <= maxCz; cz++) {
            LevelChunk chunk = mc.level.getChunkSource().getChunkNow(cx, cz);
            if (chunk != null) {
               for (int sy = minY >> 4; sy <= maxY >> 4; sy++) {
                  int index = chunk.getSectionIndexFromSectionY(sy);
                  if (index >= 0 && index < chunk.getSections().length) {
                     LevelChunkSection section = chunk.getSections()[index];
                     if (section != null && !section.hasOnlyAir() && section.maybeHas(state -> state.is(Blocks.RESPAWN_ANCHOR))) {
                        for (int y = 0; y < 16; y++) {
                           for (int z = 0; z < 16; z++) {
                              for (int x = 0; x < 16; x++) {
                                 if (section.getBlockState(x, y, z).is(Blocks.RESPAWN_ANCHOR)) {
                                    this.anchors
                                       .add(
                                          new BlockPos(
                                             SectionPos.sectionToBlockCoord(cx) + x,
                                             SectionPos.sectionToBlockCoord(sy) + y,
                                             SectionPos.sectionToBlockCoord(cz) + z
                                          )
                                       );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void renderHalos(WorldRenderContext context) {
      Minecraft mc = Minecraft.getInstance();
      if (this.isEnabled() && this.glow.get() && !this.anchors.isEmpty() && mc.level != null && context.consumers() != null) {
         Vec3 camera = mc.gameRenderer.getMainCamera().position();
         VertexConsumer vc = context.consumers().getBuffer(RenderTypes.lightning());
         int alpha = Math.round(this.glowStrength.getInt() / 100.0F * 255.0F);

         for (BlockPos pos : this.anchors) {
            if (mc.level.getBlockState(pos).is(Blocks.RESPAWN_ANCHOR)) {
               GlowHalo.draw(
                  vc, context.matrices().last(), Vec3.atCenterOf(pos), camera, (float)this.glowSize.get().doubleValue(), this.color.get() & 16777215, alpha
               );
            }
         }
      }
   }
}
