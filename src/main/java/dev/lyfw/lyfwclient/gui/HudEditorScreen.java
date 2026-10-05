package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class HudEditorScreen extends Screen {
   private static final int SNAP_THRESHOLD = 6;
   private static boolean snapEnabled = true;
   private HudModule dragging;
   private int dragOffX;
   private int dragOffY;
   private double snapLineX = -1.0;
   private double snapLineY = -1.0;
   private int snapToggleX;
   private int snapToggleW;
   private int snapToggleY;
   private int snapToggleH;
   private final Screen returnTo;

   public static boolean isSnapEnabled() {
      return snapEnabled;
   }

   public static void setSnapEnabled(boolean enabled) {
      snapEnabled = enabled;
   }

   public HudEditorScreen() {
      this(null);
   }

   public HudEditorScreen(Screen returnTo) {
      super(Component.literal("HUD Editor"));
      this.returnTo = returnTo;
   }

   private List<HudModule> hudModules() {
      List<HudModule> list = new ArrayList<>();

      for (Module module : ModuleManager.all()) {
         if (module instanceof HudModule hudModule) {
            list.add(hudModule);
         }
      }

      return list;
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      context.drawCenteredString(this.font, Component.literal("Drag any element to reposition it — Escape to close"), this.width / 2, 6, Theme.textMuted());
      Component snapLabel = Component.literal("Snap: " + (snapEnabled ? "ON" : "OFF") + " (click to toggle)");
      int snapLabelWidth = this.font.width(snapLabel);
      this.snapToggleX = this.width / 2 - snapLabelWidth / 2;
      this.snapToggleY = 18;
      this.snapToggleW = snapLabelWidth;
      this.snapToggleH = 10;
      boolean toggleHovered = mouseX >= this.snapToggleX
         && mouseX < this.snapToggleX + this.snapToggleW
         && mouseY >= this.snapToggleY
         && mouseY < this.snapToggleY + this.snapToggleH;
      int snapLabelColor = snapEnabled ? -14032437 : Theme.textSecondary();
      if (toggleHovered) {
         snapLabelColor = snapEnabled ? -8589086 : Theme.textMuted();
      }

      context.drawCenteredString(this.font, snapLabel, this.width / 2, this.snapToggleY, snapLabelColor);

      for (HudModule module : this.hudModules()) {
         try {
            module.previewRender(context);
         } catch (Exception var12) {
         }
      }

      if (this.dragging != null) {
         if (this.snapLineX >= 0.0) {
            int lineX = (int)this.snapLineX;
            context.fill(lineX, 0, lineX + 1, this.height, -1063166721);
         }

         if (this.snapLineY >= 0.0) {
            int lineY = (int)this.snapLineY;
            context.fill(0, lineY, this.width, lineY + 1, -1063166721);
         }
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         int mx = (int)click.x();
         int my = (int)click.y();
         if (mx >= this.snapToggleX && mx < this.snapToggleX + this.snapToggleW && my >= this.snapToggleY && my < this.snapToggleY + this.snapToggleH) {
            snapEnabled = !snapEnabled;
            return true;
         } else {
            for (HudModule module : this.hudModules()) {
               int x = module.visualX();
               int y = module.visualY();
               int w = module.boxWidth();
               int h = module.boxHeight();
               if (mx >= x && mx < x + w && my >= y && my < y + h) {
                  this.dragging = module;
                  this.dragOffX = mx - x;
                  this.dragOffY = my - y;
                  return true;
               }
            }

            return super.mouseClicked(click, doubled);
         }
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      if (this.dragging != null) {
         int mx = (int)click.x();
         int my = (int)click.y();
         double newVisX = mx - this.dragOffX;
         double newVisY = my - this.dragOffY;
         int w = this.dragging.boxWidth();
         int h = this.dragging.boxHeight();
         if (snapEnabled) {
            HudEditorScreen.SnapResult snapX = resolveSnap(newVisX, w, this.width);
            if (snapX != null) {
               newVisX = snapX.position();
            }

            this.snapLineX = snapX != null ? snapX.guideLine() : -1.0;
            HudEditorScreen.SnapResult snapY = resolveSnap(newVisY, h, this.height);
            if (snapY != null) {
               newVisY = snapY.position();
            }

            this.snapLineY = snapY != null ? snapY.guideLine() : -1.0;
         } else {
            this.snapLineX = -1.0;
            this.snapLineY = -1.0;
         }

         double newX = Mth.clamp(newVisX - this.dragging.visualOffsetX(), this.dragging.x.min, this.dragging.x.max);
         double newY = Mth.clamp(newVisY - this.dragging.visualOffsetY(), this.dragging.y.min, this.dragging.y.max);
         this.dragging.x.set(newX);
         this.dragging.y.set(newY);
         return true;
      } else {
         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   private static HudEditorScreen.SnapResult resolveSnap(double pos, int size, int screenSize) {
      double third = screenSize / 3.0;
      double twoThirds = screenSize * 2.0 / 3.0;
      double center = screenSize / 2.0;
      if (Math.abs(pos - 0.0) <= 6.0) {
         return new HudEditorScreen.SnapResult(0.0, 0.0);
      } else if (Math.abs(pos + size - screenSize) <= 6.0) {
         return new HudEditorScreen.SnapResult((double)(screenSize - size), (double)screenSize);
      } else if (Math.abs(pos + size / 2.0 - third) <= 6.0) {
         return new HudEditorScreen.SnapResult(third - size / 2.0, third);
      } else if (Math.abs(pos + size / 2.0 - center) <= 6.0) {
         return new HudEditorScreen.SnapResult(center - size / 2.0, center);
      } else {
         return Math.abs(pos + size / 2.0 - twoThirds) <= 6.0 ? new HudEditorScreen.SnapResult(twoThirds - size / 2.0, twoThirds) : null;
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.dragging != null) {
         this.dragging = null;
         this.snapLineX = -1.0;
         this.snapLineY = -1.0;
         Config.save();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   public void onClose() {
      Config.save();
      if (this.returnTo != null) {
         this.minecraft.setScreen(this.returnTo);
      } else {
         super.onClose();
      }
   }

   private record SnapResult(double position, double guideLine) {
   }
}
