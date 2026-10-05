package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class GuiMoverScreen extends Screen {
   private static final int SNAP_THRESHOLD = 6;
   private final Screen parent;
   private final HudModule module;
   private boolean dragging;
   private int dragOffX;
   private int dragOffY;
   private double snapLineX = -1.0;
   private double snapLineY = -1.0;
   private boolean freePlacement;

   public GuiMoverScreen(Screen parent, HudModule module) {
      super(Component.literal("GUI Mover"));
      this.parent = parent;
      this.module = module;
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      int x = this.module.visualX();
      int y = this.module.visualY();
      int w = this.module.boxWidth();
      int h = this.module.boxHeight();
      boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
      if (this.dragging) {
         if (this.snapLineX >= 0.0) {
            context.fill((int)this.snapLineX, 0, (int)this.snapLineX + 1, this.height, -1065722625);
         }

         if (this.snapLineY >= 0.0) {
            context.fill(0, (int)this.snapLineY, this.width, (int)this.snapLineY + 1, -1065722625);
         }
      }

      int outline = this.dragging ? Theme.accent() : (hovered ? Theme.accent() : Theme.border());
      context.fill(x - 1, y - 1, x + w + 1, y, outline);
      context.fill(x - 1, y + h, x + w + 1, y + h + 1, outline);
      context.fill(x - 1, y, x, y + h, outline);
      context.fill(x + w, y, x + w + 1, y + h, outline);

      try {
         this.module.previewRender(context);
      } catch (Exception var12) {
      }

      this.renderHint(context);
   }

   private void renderHint(GuiGraphics context) {
      String title = "Moving: " + this.module.name;
      String hint = "Drag to place  ·  arrow keys nudge  ·  hold Alt to ignore snapping  ·  Escape when done";
      int w = Math.max(this.font.width(title), this.font.width(hint)) + 20;
      int x = (this.width - w) / 2;
      ThemeRenderer.panel(context, x, 8, w, 34, Theme.border(), Theme.panelBg(), 8);
      context.drawCenteredString(this.font, title, this.width / 2, 14, Theme.textPrimary());
      context.drawCenteredString(this.font, hint, this.width / 2, 26, Theme.textSecondary());
      String position = this.module.x.getInt() + ", " + this.module.y.getInt();
      context.drawCenteredString(this.font, position, this.width / 2, this.height - 14, Theme.textMuted());
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         int mx = (int)click.x();
         int my = (int)click.y();
         int x = this.module.visualX();
         int y = this.module.visualY();
         if (mx >= x && mx < x + this.module.boxWidth() && my >= y && my < y + this.module.boxHeight()) {
            this.dragging = true;
            this.dragOffX = mx - x;
            this.dragOffY = my - y;
            return true;
         } else {
            this.dragging = true;
            this.dragOffX = this.module.boxWidth() / 2;
            this.dragOffY = this.module.boxHeight() / 2;
            this.moveTo(mx, my);
            return true;
         }
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      if (!this.dragging) {
         return super.mouseDragged(click, offsetX, offsetY);
      } else {
         this.moveTo((int)click.x(), (int)click.y());
         return true;
      }
   }

   private void moveTo(int mx, int my) {
      double visX = mx - this.dragOffX;
      double visY = my - this.dragOffY;
      int w = this.module.boxWidth();
      int h = this.module.boxHeight();
      if (!this.freePlacement) {
         double[] snapX = resolveSnap(visX, w, this.width);
         if (snapX != null) {
            visX = snapX[0];
         }

         this.snapLineX = snapX == null ? -1.0 : snapX[1];
         double[] snapY = resolveSnap(visY, h, this.height);
         if (snapY != null) {
            visY = snapY[0];
         }

         this.snapLineY = snapY == null ? -1.0 : snapY[1];
      } else {
         this.snapLineX = -1.0;
         this.snapLineY = -1.0;
      }

      this.module.x.set(Mth.clamp(visX - this.module.visualOffsetX(), this.module.x.min, this.module.x.max));
      this.module.y.set(Mth.clamp(visY - this.module.visualOffsetY(), this.module.y.min, this.module.y.max));
   }

   private static double[] resolveSnap(double pos, int size, int screenSize) {
      double third = screenSize / 3.0;
      double twoThirds = screenSize * 2.0 / 3.0;
      double centre = screenSize / 2.0;
      if (Math.abs(pos) <= 6.0) {
         return new double[]{0.0, 0.0};
      } else if (Math.abs(pos + size - screenSize) <= 6.0) {
         return new double[]{screenSize - size, screenSize};
      } else if (Math.abs(pos + size / 2.0 - third) <= 6.0) {
         return new double[]{third - size / 2.0, third};
      } else if (Math.abs(pos + size / 2.0 - centre) <= 6.0) {
         return new double[]{centre - size / 2.0, centre};
      } else {
         return Math.abs(pos + size / 2.0 - twoThirds) <= 6.0 ? new double[]{twoThirds - size / 2.0, twoThirds} : null;
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.dragging) {
         this.dragging = false;
         this.snapLineX = -1.0;
         this.snapLineY = -1.0;
         Config.save();
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   public boolean keyPressed(KeyEvent input) {
      this.freePlacement = (input.modifiers() & 4) != 0;
      int key = input.key();
      int step = (input.modifiers() & 1) != 0 ? 10 : 1;
      switch (key) {
         case 256:
            this.onClose();
            return true;
         case 257:
         case 258:
         case 259:
         case 260:
         case 261:
         default:
            return super.keyPressed(input);
         case 262:
            this.nudge(step, 0);
            return true;
         case 263:
            this.nudge(-step, 0);
            return true;
         case 264:
            this.nudge(0, step);
            return true;
         case 265:
            this.nudge(0, -step);
            return true;
      }
   }

   public boolean keyReleased(KeyEvent input) {
      this.freePlacement = (input.modifiers() & 4) != 0;
      return super.keyReleased(input);
   }

   private void nudge(int dx, int dy) {
      this.module.x.set(Mth.clamp(this.module.x.get() + dx, this.module.x.min, this.module.x.max));
      this.module.y.set(Mth.clamp(this.module.y.get() + dy, this.module.y.min, this.module.y.max));
      Config.save();
   }

   public void onClose() {
      Config.save();
      Minecraft.getInstance().setScreen(this.parent);
   }
}
