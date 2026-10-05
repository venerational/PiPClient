package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class EffectTimersModule extends HudModule {
   private static final Identifier FRAME = Identifier.withDefaultNamespace("hud/effect_background");
   private static final Identifier FRAME_AMBIENT = Identifier.withDefaultNamespace("hud/effect_background_ambient");
   private static final int LOW_TICKS = 100;
   private static final int LUNAR_ROW = 25;
   private static final int BAR_ROW = 18;
   private static final int VANILLA_COL = 25;
   private static final int SHADE = -1728053248;
   private static final String[] ROMAN = new String[]{"", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
   private final EnumSetting<EffectTimersModule.Style> style = this.register(new EnumSetting<>("Style", EffectTimersModule.Style.LUNAR));
   private final BooleanSetting hideVanilla = this.register(new BooleanSetting("Hide Vanilla Icons", true));
   private final BooleanSetting showName = this.register(new BooleanSetting("Show Name", true));
   private final BooleanSetting showLevel = this.register(new BooleanSetting("Show Level", true));
   private final BooleanSetting effectColors = this.register(new BooleanSetting("Effect Colors", true));
   private final ColorSetting color = this.register(new ColorSetting("Color", -1));
   private final BooleanSetting background = this.register(new BooleanSetting("Background", true));
   private final BooleanSetting flashLow = this.register(new BooleanSetting("Flash When Low", true));
   private final BooleanSetting hideAmbient = this.register(new BooleanSetting("Hide Beacon Effects", false));
   private final Map<MobEffect, Integer> longest = new HashMap<>();
   private boolean previewing;

   public EffectTimersModule() {
      super(
         "Effect Timers",
         "A timer for your potion effects in the corner - Lunar's list, a bar that empties as each effect runs out, or the vanilla icons with the time written under them.",
         false,
         4.0,
         4.0
      );
   }

   @Override
   public void init() {
      super.init();
      HudElementRegistry.replaceElement(VanillaHudElements.STATUS_EFFECTS, original -> (context, tickCounter) -> {
         if (!this.isEnabled() || !this.hideVanilla.get()) {
            original.render(context, tickCounter);
         }
      });
   }

   @Override
   public void previewRender(GuiGraphics context) {
      this.previewing = true;

      try {
         super.previewRender(context);
      } finally {
         this.previewing = false;
      }
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      List<EffectTimersModule.Row> rows = this.rows(mc);
      if (!rows.isEmpty()) {
         switch ((EffectTimersModule.Style)this.style.get()) {
            case BARS:
               this.renderBars(context, mc, rows);
               break;
            case VANILLA:
               this.renderVanilla(context, mc, rows);
               break;
            default:
               this.renderLunar(context, mc, rows);
         }
      }
   }

   @Override
   protected int contentWidth() {
      Minecraft mc = Minecraft.getInstance();
      List<EffectTimersModule.Row> rows = this.rows(mc);
      if (rows.isEmpty()) {
         return 24;
      } else {
         return switch ((EffectTimersModule.Style)this.style.get()) {
            case LUNAR -> 26 + this.textWidth(mc.font, rows);
            case BARS -> Math.max(90, 20 + this.textWidth(mc.font, rows) + 6);
            case VANILLA -> rows.size() * 25 - 1;
         };
      }
   }

   @Override
   protected int contentHeight() {
      List<EffectTimersModule.Row> rows = this.rows(Minecraft.getInstance());
      if (rows.isEmpty()) {
         return 24;
      } else {
         return switch ((EffectTimersModule.Style)this.style.get()) {
            case LUNAR -> rows.size() * 25 - 1;
            case BARS -> rows.size() * 18 - 2;
            case VANILLA -> 34;
         };
      }
   }

   private int textWidth(Font tr, List<EffectTimersModule.Row> rows) {
      int widest = 0;

      for (EffectTimersModule.Row row : rows) {
         widest = Math.max(widest, tr.width(this.title(row)));
         widest = Math.max(widest, tr.width(row.time()));
      }

      return widest;
   }

   private void renderLunar(GuiGraphics context, Minecraft mc, List<EffectTimersModule.Row> rows) {
      int width = this.contentWidth();

      for (int i = 0; i < rows.size(); i++) {
         EffectTimersModule.Row row = rows.get(i);
         int top = i * 25;
         if (this.background.get()) {
            context.fill(0, top, width, top + 24, -1728053248);
         }

         context.blitSprite(RenderPipelines.GUI_TEXTURED, row.icon(), 2, top + 2, 20, 20);
         int tint = this.tint(row);
         if (this.showName.get()) {
            context.drawString(mc.font, this.title(row), 26, top + 3, tint);
            context.drawString(mc.font, row.time(), 26, top + 14, this.timeColor(row));
         } else {
            context.drawString(mc.font, row.time(), 26, top + 8, this.timeColor(row));
         }
      }
   }

   private void renderBars(GuiGraphics context, Minecraft mc, List<EffectTimersModule.Row> rows) {
      int width = this.contentWidth();

      for (int i = 0; i < rows.size(); i++) {
         EffectTimersModule.Row row = rows.get(i);
         int top = i * 18;
         if (this.background.get()) {
            context.fill(0, top, width, top + 16, -1728053248);
         }

         int filled = Math.round(width * Mth.clamp(row.fraction(), 0.0F, 1.0F));
         if (filled > 0) {
            context.fill(0, top, filled, top + 16, this.tint(row) & 16777215 | this.fade(row, 160) << 24);
         }

         context.blitSprite(RenderPipelines.GUI_TEXTURED, row.icon(), 1, top + 1, 14, 14);
         int timeWidth = mc.font.width(row.time());
         if (this.showName.get()) {
            context.drawString(mc.font, this.title(row), 18, top + 4, -1);
         }

         context.drawString(mc.font, row.time(), width - timeWidth - 2, top + 4, this.timeColor(row));
      }
   }

   private void renderVanilla(GuiGraphics context, Minecraft mc, List<EffectTimersModule.Row> rows) {
      for (int i = 0; i < rows.size(); i++) {
         EffectTimersModule.Row row = rows.get(i);
         int left = i * 25;
         if (this.background.get()) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, row.ambient() ? FRAME_AMBIENT : FRAME, left, 0, 24, 24);
         }

         context.blitSprite(RenderPipelines.GUI_TEXTURED, row.icon(), left + 3, 3, 18, 18);
         String time = row.time();
         if (this.showLevel.get() && !row.level().isEmpty()) {
            context.drawString(mc.font, row.level(), left + 22 - mc.font.width(row.level()), 15, -1);
         }

         context.drawString(mc.font, time, left + 12 - mc.font.width(time) / 2, 25, this.timeColor(row));
      }
   }

   private String title(EffectTimersModule.Row row) {
      return this.showLevel.get() ? row.title() : row.name();
   }

   private int tint(EffectTimersModule.Row row) {
      return this.effectColors.get() ? 0xFF000000 | row.color() : this.color.get();
   }

   private int timeColor(EffectTimersModule.Row row) {
      int rgb = this.tint(row) & 16777215;
      return this.fade(row, 255) << 24 | (row.low() && this.flashLow.get() ? 16733525 : rgb);
   }

   private int fade(EffectTimersModule.Row row, int full) {
      if (row.low() && this.flashLow.get()) {
         float pulse = 0.55F + 0.45F * Mth.sin((float)(System.currentTimeMillis() % 1000L) / 1000.0F * (float) (Math.PI * 2));
         return Math.max(40, Math.round(full * pulse));
      } else {
         return full;
      }
   }

   private List<EffectTimersModule.Row> rows(Minecraft mc) {
      List<MobEffectInstance> effects = new ArrayList<>();
      if (mc.player != null) {
         for (MobEffectInstance effect : mc.player.getActiveEffects()) {
            if (!this.hideAmbient.get() || !effect.isAmbient()) {
               effects.add(effect);
            }
         }
      }

      if (effects.isEmpty()) {
         return this.previewing ? this.samples() : List.of();
      } else {
         this.longest.keySet().removeIf(kept -> effects.stream().noneMatch(effectx -> effectx.getEffect().value() == kept));
         effects.sort(
            (a, b) -> {
               if (a.isInfiniteDuration() != b.isInfiniteDuration()) {
                  return a.isInfiniteDuration() ? 1 : -1;
               } else {
                  return a.getDuration() != b.getDuration()
                     ? Integer.compare(a.getDuration(), b.getDuration())
                     : ((MobEffect)a.getEffect().value())
                        .getDisplayName()
                        .getString()
                        .compareTo(((MobEffect)b.getEffect().value()).getDisplayName().getString());
               }
            }
         );
         List<EffectTimersModule.Row> rows = new ArrayList<>(effects.size());

         for (MobEffectInstance effectx : effects) {
            rows.add(this.row(effectx));
         }

         return rows;
      }
   }

   private EffectTimersModule.Row row(MobEffectInstance effect) {
      Holder<MobEffect> type = effect.getEffect();
      int full = Math.max(effect.getDuration(), this.longest.getOrDefault(type.value(), 0));
      this.longest.put((MobEffect)type.value(), full);
      float fraction = !effect.isInfiniteDuration() && full > 0 ? (float)effect.getDuration() / full : 1.0F;
      return new EffectTimersModule.Row(
         Gui.getMobEffectSprite(type),
         ((MobEffect)type.value()).getDisplayName().getString(),
         level(effect.getAmplifier()),
         time(effect),
         ((MobEffect)type.value()).getColor(),
         fraction,
         effect.isAmbient(),
         !effect.isInfiniteDuration() && effect.getDuration() <= 100
      );
   }

   private List<EffectTimersModule.Row> samples() {
      return List.of(
         this.sample(MobEffects.SPEED, "II", "1:30", 0.75F, false),
         this.sample(MobEffects.STRENGTH, "", "0:42", 0.35F, false),
         this.sample(MobEffects.REGENERATION, "II", "0:04", 0.08F, true)
      );
   }

   private EffectTimersModule.Row sample(Holder<MobEffect> type, String level, String time, float fraction, boolean low) {
      return new EffectTimersModule.Row(
         Gui.getMobEffectSprite(type),
         ((MobEffect)type.value()).getDisplayName().getString(),
         level,
         time,
         ((MobEffect)type.value()).getColor(),
         fraction,
         false,
         low
      );
   }

   private static String level(int amplifier) {
      return amplifier >= 0 && amplifier < ROMAN.length ? ROMAN[amplifier] : Integer.toString(amplifier + 1);
   }

   private static String time(MobEffectInstance effect) {
      if (effect.isInfiniteDuration()) {
         return "**:**";
      } else {
         int seconds = Math.max(0, effect.getDuration()) / 20;
         int minutes = seconds / 60;
         return minutes >= 60 ? String.format("%d:%02d:%02d", minutes / 60, minutes % 60, seconds % 60) : String.format("%d:%02d", minutes, seconds % 60);
      }
   }

   private record Row(Identifier icon, String name, String level, String time, int color, float fraction, boolean ambient, boolean low) {
      String title() {
         return this.level.isEmpty() ? this.name : this.name + " " + this.level;
      }
   }

   public static enum Style {
      LUNAR,
      BARS,
      VANILLA;
   }
}
