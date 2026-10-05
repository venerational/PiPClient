package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.render.GlowHalo;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;

public class CrystalCustomizerModule extends Module {
   private static final int FULL_BRIGHT = 15728880;
   private static final double HALO_RANGE = 64.0;
   private static final double CRYSTAL_MIDDLE = 1.2;
   private final ColorSetting color = this.register(new ColorSetting("Color", -37136).exemptFromGlobalColor());
   private final SliderSetting opacity = this.register(new SliderSetting("Opacity", 100.0, 10.0, 100.0, 5.0, "%"));
   private final BooleanSetting glow = this.register(new BooleanSetting("Glow", true));
   private final SliderSetting glowSize = this.register(new SliderSetting("Glow Size", 1.4, 0.5, 4.0, 0.1, " blocks"));
   private final SliderSetting glowStrength = this.register(new SliderSetting("Glow Strength", 60.0, 5.0, 100.0, 5.0, "%"));

   public CrystalCustomizerModule() {
      super("Crystal Customizer", "End crystals in any colour, see-through to any degree, with a glow round them if you want one.", Category.RENDER, false);
      this.color.group = "Look";
      this.opacity.group = "Look";
      this.glow.group = "Glow";
      this.glowSize.group = "Glow";
      this.glowStrength.group = "Glow";
   }

   public static CrystalCustomizerModule get() {
      return ModuleManager.get("Crystal Customizer") instanceof CrystalCustomizerModule crystals && crystals.isEnabled() ? crystals : null;
   }

   @Override
   public void init() {
      WorldRenderEvents.END_MAIN.register(this::renderHalos);
   }

   public int tint() {
      int alpha = Math.round(this.opacity.getInt() / 100.0F * 255.0F);
      return alpha << 24 | this.color.get() & 16777215;
   }

   public int light(int light) {
      return this.glow.get() ? 15728880 : light;
   }

   private void renderHalos(WorldRenderContext context) {
      Minecraft mc = Minecraft.getInstance();
      if (this.isEnabled() && this.glow.get() && mc.level != null && context.consumers() != null) {
         Vec3 camera = mc.gameRenderer.getMainCamera().position();
         VertexConsumer vc = context.consumers().getBuffer(RenderTypes.lightning());
         float delta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
         int alpha = Math.round(this.glowStrength.getInt() / 100.0F * 255.0F);

         for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof EndCrystal crystal && crystal.distanceToSqr(camera) < 4096.0) {
               Vec3 center = crystal.getPosition(delta).add(0.0, 1.2, 0.0);
               GlowHalo.draw(vc, context.matrices().last(), center, camera, (float)this.glowSize.get().doubleValue(), this.color.get() & 16777215, alpha);
            }
         }
      }
   }
}
