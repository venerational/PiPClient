package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

public class HealthRingModule extends Module {
   private final SliderSetting radius = this.register(new SliderSetting("Radius", 11.0, 5.0, 40.0, 1.0, "px"));
   private final SliderSetting thickness = this.register(new SliderSetting("Thickness", 2.0, 1.0, 8.0, 0.5, "px"));
   private final BooleanSetting colorByHealth = this.register(new BooleanSetting("Color By Health", true));
   private final ColorSetting color = this.register(new ColorSetting("Color", -45730));
   private final SliderSetting background = this.register(new SliderSetting("Background", 30.0, 0.0, 100.0, 5.0, "%"));
   private final BooleanSetting smooth = this.register(new BooleanSetting("Smooth", true));
   private float shown = -1.0F;
   private long lastFrame;

   public HealthRingModule() {
      super("Health Ring", "Your health as a ring round the crosshair - the arc shrinks as you take damage, and turns from green to red.", Category.HUD, false);
      this.radius.group = "Shape";
      this.thickness.group = "Shape";
      this.colorByHealth.group = "Color";
      this.color.group = "Color";
      this.background.group = "Color";
      this.smooth.group = "Shape";
   }

   public static HealthRingModule get() {
      return ModuleManager.get("Health Ring") instanceof HealthRingModule ring ? ring : null;
   }

   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (this.isEnabled() && player != null && !mc.options.hideGui && mc.gameMode != null && mc.gameMode.getPlayerMode().isSurvival()) {
         float target = Math.max(0.0F, Math.min(1.0F, player.getHealth() / Math.max(1.0F, player.getMaxHealth())));
         long now = System.nanoTime();
         float dt = this.lastFrame == 0L ? 0.0F : Math.min(0.25F, (float)(now - this.lastFrame) / 1.0E9F);
         this.lastFrame = now;
         if (!(this.shown < 0.0F) && this.smooth.get()) {
            this.shown = this.shown + (target - this.shown) * (1.0F - (float)Math.exp(-dt * 12.0F));
         } else {
            this.shown = target;
         }

         int arcColor = this.colorByHealth.get() ? healthColor(this.shown) : this.color.get();
         int trackAlpha = Math.round(this.background.getInt() / 100.0F * 255.0F);
         drawRing(
            context,
            context.guiWidth() / 2,
            context.guiHeight() / 2,
            (float)this.radius.get().doubleValue(),
            (float)this.thickness.get().doubleValue(),
            this.shown,
            arcColor,
            trackAlpha << 24 | 2105376
         );
      } else {
         this.shown = -1.0F;
      }
   }

   private static int healthColor(float fraction) {
      return Color.HSBtoRGB(fraction / 3.0F, 0.85F, 1.0F) | 0xFF000000;
   }

   private static void drawRing(GuiGraphics context, int cx, int cy, float radius, float thickness, float fraction, int arc, int track) {
      float inner = radius - thickness / 2.0F;
      float outer = radius + thickness / 2.0F;
      int reach = (int)Math.ceil(outer) + 1;
      float filled = fraction * (float) (Math.PI * 2);

      for (int py = -reach; py < reach; py++) {
         for (int px = -reach; px < reach; px++) {
            int onArc = 0;
            int onTrack = 0;

            for (int s = 0; s < 4; s++) {
               float x = px + 0.25F + (s & 1) * 0.5F;
               float y = py + 0.25F + (s >> 1) * 0.5F;
               float d = (float)Math.sqrt(x * x + y * y);
               if (d >= inner && d <= outer) {
                  float angle = (float)Math.atan2(x, -y);
                  if (angle < 0.0F) {
                     angle += (float) (Math.PI * 2);
                  }

                  if (angle <= filled) {
                     onArc++;
                  } else {
                     onTrack++;
                  }
               }
            }

            if (onArc > 0) {
               context.fill(cx + px, cy + py, cx + px + 1, cy + py + 1, scaleAlpha(arc, onArc / 4.0F));
            }

            if (onTrack > 0 && track >>> 24 > 0) {
               context.fill(cx + px, cy + py, cx + px + 1, cy + py + 1, scaleAlpha(track, onTrack / 4.0F));
            }
         }
      }
   }

   private static int scaleAlpha(int color, float coverage) {
      return Math.round((color >>> 24) * coverage) << 24 | color & 16777215;
   }
}
