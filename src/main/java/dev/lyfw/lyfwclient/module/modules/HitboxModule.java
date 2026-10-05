package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.gizmos.GizmoStyle;

public class HitboxModule extends Module {
   private final ColorSetting lineColor = this.register(new ColorSetting("Line Color", -1));
   private final SliderSetting lineWidth = this.register(new SliderSetting("Line Width", 2.0, 0.5, 6.0, 0.1, "px"));
   private final BooleanSetting fill = this.register(new BooleanSetting("Fill", false));
   private final ColorSetting fillColor = this.register(new ColorSetting("Fill Color", -2130706433));
   private final BooleanSetting playersOnly = this.register(new BooleanSetting("Players Only", false));
   private final BooleanSetting showSelf = this.register(new BooleanSetting("Show Self", false));
   private final BooleanSetting highlightInReach = this.register(new BooleanSetting("Highlight In Reach", false));
   private final ColorSetting inReachColor = this.register(new ColorSetting("In Reach Color", -65536));
   private final BooleanSetting fillUsesReachColor = this.register(new BooleanSetting("Fill Uses In Reach Color", false));

   public HitboxModule() {
      super("Hitboxes", "Draws colored hitbox outlines around entities, without needing to open F3 and enable it manually.", Category.RENDER, false);
   }

   @Override
   public boolean isEnabled() {
      Minecraft mc = Minecraft.getInstance();
      return mc.debugEntries != null && mc.debugEntries.isCurrentlyEnabled(DebugScreenEntries.ENTITY_HITBOXES);
   }

   @Override
   public void setEnabled(boolean enabled) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.debugEntries != null) {
         mc.debugEntries.setStatus(DebugScreenEntries.ENTITY_HITBOXES, enabled ? DebugScreenEntryStatus.ALWAYS_ON : DebugScreenEntryStatus.NEVER);
      }
   }

   public GizmoStyle style(boolean inReach) {
      boolean rangeColored = this.highlightInReach.get() && inReach;
      int stroke = rangeColored ? this.inReachColor.get() : this.lineColor.get();
      float width = (float)this.lineWidth.get().doubleValue();
      if (!this.fill.get()) {
         return GizmoStyle.stroke(stroke, width);
      } else {
         int fill = this.fillColor.get();
         if (rangeColored && this.fillUsesReachColor.get()) {
            fill = fill & 0xFF000000 | this.inReachColor.get() & 16777215;
         }

         return GizmoStyle.strokeAndFill(stroke, width, fill);
      }
   }

   public boolean playersOnly() {
      return this.playersOnly.get();
   }

   public boolean showSelf() {
      return this.showSelf.get();
   }
}
