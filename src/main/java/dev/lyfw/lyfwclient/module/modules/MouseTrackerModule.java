package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.render.CursorShape;
import dev.lyfw.lyfwclient.render.PadImage;
import dev.lyfw.lyfwclient.render.TrailStyle;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;

public class MouseTrackerModule extends HudModule {
   private static final int TRAIL_MAX = 160;
   private final SliderSetting padWidth = this.register(new SliderSetting("Width", 150.0, 40.0, 400.0, 2.0, "px"));
   private final SliderSetting padHeight = this.register(new SliderSetting("Height", 84.0, 30.0, 300.0, 2.0, "px"));
   private final SliderSetting sensitivity = this.register(new SliderSetting("Sensitivity", 40.0, 5.0, 200.0, 1.0, "%"));
   private final SliderSetting returnSpeed = this.register(new SliderSetting("Return Speed", 0.0, 0.0, 40.0, 1.0, "%"));
   private final SliderSetting trailLength = this.register(new SliderSetting("Trail Length", 60.0, 0.0, 160.0, 1.0, ""));
   private final EnumSetting<CursorShape> cursorShape = this.register(new EnumSetting<>("Cursor Shape", CursorShape.DOT));
   private final SliderSetting cursorSize = this.register(new SliderSetting("Cursor Size", 5.0, 2.0, 20.0, 1.0, "px"));
   private final EnumSetting<TrailStyle> trailStyle = this.register(new EnumSetting<>("Trails", TrailStyle.CLASSIC));
   private final ColorSetting padColor = this.register(new ColorSetting("Pad Color", -14803426).exemptFromGlobalColor());
   private final ColorSetting trailColor = this.register(new ColorSetting("Trail Color", -1).exemptFromGlobalColor());
   private final TextSetting backgroundImage = this.register(new TextSetting("Background Image", ""));
   private final Deque<float[]> trail = new ArrayDeque<>();
   private float posX;
   private float posY;
   private float lastYaw;
   private float lastPitch;
   private boolean haveLast;
   private String loadedImageFor;
   private PadImage loadedImage;
   private int imageGeneration;

   public MouseTrackerModule() {
      super("Mouse Tracker", "A panel that draws your mouse movement, with a shaped cursor and a trail behind it.", false, 100.0, 160.0);
      this.padWidth.group = "Layout";
      this.padHeight.group = "Layout";
      this.sensitivity.group = "Motion";
      this.returnSpeed.group = "Motion";
      this.backgroundImage.group = "Motion";
      this.cursorShape.group = "Cursor";
      this.cursorSize.group = "Cursor";
      this.trailStyle.group = "Cursor";
      this.trailLength.group = "Cursor";
      this.padColor.group = "Colors";
      this.trailColor.group = "Colors";
   }

   @Override
   protected int contentWidth() {
      return this.padWidth.getInt();
   }

   @Override
   protected int contentHeight() {
      return this.padHeight.getInt();
   }

   @Override
   public void render(GuiGraphics context) {
      this.drawPad(context, 0, 0, this.contentWidth(), this.contentHeight(), Minecraft.getInstance());
   }

   private void drawPad(GuiGraphics context, int x, int y, int w, int h, Minecraft mc) {
      PadImage image = this.image();
      if (image != null) {
         context.blit(RenderPipelines.GUI_TEXTURED, image.currentFrame(), x, y, 0.0F, 0.0F, w, h, w, h);
      } else {
         context.fill(x, y, x + w, y + h, this.padColor.get());
      }

      this.updateMotion(mc);
      int cx = x + w / 2;
      int cy = y + h / 2;
      int halfW = Math.max(1, w / 2 - 3);
      int halfH = Math.max(1, h / 2 - 3);
      int keep = this.trailLength.getInt();
      int index = 0;
      int size = this.trail.size();
      int base = this.trailColor.get();
      TrailStyle style = this.trailStyle.get();
      int cursorPx = this.cursorSize.getInt();
      context.enableScissor(x, y, x + w, y + h);
      float prevX = Float.NaN;
      float prevY = Float.NaN;
      int spacing = Math.max(1, style.spacing());
      boolean broke = false;

      for (float[] point : this.trail) {
         index++;
         int fromHead = size - index;
         if (point.length > 3 && point[3] != 0.0F) {
            broke = true;
         }

         if (fromHead < keep && fromHead % spacing == 0) {
            float px = cx + point[0] * halfW;
            float py = cy + point[1] * halfH;
            float age = (float)fromHead / Math.max(1, keep);
            float seed = point.length > 2 ? point[2] : 0.5F;
            boolean start = broke || Float.isNaN(prevX);
            style.draw(context, px, py, start ? px : prevX, start ? py : prevY, age, seed, base, cursorPx);
            broke = false;
            prevX = px;
            prevY = py;
         }
      }

      context.disableScissor();
      int dx = cx + Math.round(this.posX * halfW);
      int dy = cy + Math.round(this.posY * halfH);
      this.cursorShape.get().draw(context, dx, dy, cursorPx, base);
   }

   private void updateMotion(Minecraft mc) {
      if (mc.player != null) {
         float yaw = mc.player.getYRot();
         float pitch = mc.player.getXRot();
         boolean wrapped = false;
         if (this.haveLast) {
            float dYaw = Mth.wrapDegrees(yaw - this.lastYaw);
            float dPitch = pitch - this.lastPitch;
            float scale = (float)(this.sensitivity.get() / 100.0) * 0.06F;
            float nx = this.posX + dYaw * scale;
            float ny = this.posY + dPitch * scale;
            wrapped = nx < -1.0F || nx > 1.0F || ny < -1.0F || ny > 1.0F;
            this.posX = wrap(nx);
            this.posY = wrap(ny);
            float back = 1.0F - (float)(this.returnSpeed.get() / 100.0);
            if (back < 1.0F) {
               this.posX *= back;
               this.posY *= back;
            }
         }

         this.lastYaw = yaw;
         this.lastPitch = pitch;
         this.haveLast = true;
         this.trail.addLast(new float[]{this.posX, this.posY, (float)Math.random(), wrapped ? 1.0F : 0.0F});

         while (this.trail.size() > 160) {
            this.trail.removeFirst();
         }
      }
   }

   private static float wrap(float v) {
      float shifted = (v + 1.0F) % 2.0F;
      return (shifted < 0.0F ? shifted + 2.0F : shifted) - 1.0F;
   }

   private PadImage image() {
      String name = this.backgroundImage.get() == null ? "" : this.backgroundImage.get().trim();
      if (PadImage.isUrl(name)) {
         if (!name.equals(this.loadedImageFor)) {
            this.loadedImageFor = name;
            this.loadedImage = null;
            this.imageGeneration++;
         }

         if (this.loadedImage == null) {
            this.loadedImage = PadImage.fromUrl(name, "mouse_tracker_" + this.imageGeneration);
         }

         return this.loadedImage;
      } else if (name.equals(this.loadedImageFor)) {
         return this.loadedImage;
      } else {
         this.loadedImageFor = name;
         this.loadedImage = null;
         if (!name.isEmpty()) {
            try {
               Path path = FabricLoader.getInstance().getConfigDir().resolve(name);
               this.loadedImage = PadImage.load(path, "mouse_tracker_" + this.imageGeneration++);
            } catch (Exception var3) {
               System.out.println("[Pip Client] \"" + name + "\" is not a usable file name (" + var3 + ")");
            }
         }

         return this.loadedImage;
      }
   }
}
