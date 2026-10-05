package dev.lyfw.lyfwclient.module;

import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public abstract class HudModule extends Module {
   public static boolean drawing;
   public final SliderSetting x;
   public final SliderSetting y;
   public final SliderSetting scale;

   protected HudModule(String name, String description, boolean enabledByDefault, double defaultX, double defaultY) {
      super(name, description, Category.HUD, enabledByDefault);
      this.x = this.register(new SliderSetting("X", defaultX, 0.0, 1920.0, 1.0, "px"));
      this.y = this.register(new SliderSetting("Y", defaultY, 0.0, 1080.0, 1.0, "px"));
      this.scale = this.register(new SliderSetting("Scale", 1.0, 0.5, 3.0, 0.1, "x"));
   }

   protected void pushTransform(GuiGraphics context) {
      context.pose().pushMatrix();
      context.pose().translate((float)this.x.get().doubleValue(), (float)this.y.get().doubleValue());
      context.pose().scale((float)this.scale.get().doubleValue());
   }

   protected void popTransform(GuiGraphics context) {
      context.pose().popMatrix();
   }

   public abstract void render(GuiGraphics guiGraphics);

   protected int contentWidth() {
      return 90;
   }

   protected int contentHeight() {
      return 12;
   }

   protected int contentOffsetX() {
      return 0;
   }

   protected int contentOffsetY() {
      return 0;
   }

   public final int boxWidth() {
      return Math.max(24, Math.round(this.contentWidth() * (float)this.scale.get().doubleValue()));
   }

   public final int boxHeight() {
      return Math.max(24, Math.round(this.contentHeight() * (float)this.scale.get().doubleValue()));
   }

   public final int visualOffsetX() {
      return Math.round(this.contentOffsetX() * (float)this.scale.get().doubleValue());
   }

   public final int visualOffsetY() {
      return Math.round(this.contentOffsetY() * (float)this.scale.get().doubleValue());
   }

   public final int visualX() {
      return this.x.getInt() + this.visualOffsetX();
   }

   public final int visualY() {
      return this.y.getInt() + this.visualOffsetY();
   }

   public void previewRender(GuiGraphics context) {
      this.pushTransform(context);
      drawing = true;

      try {
         this.render(context);
      } finally {
         drawing = false;
         this.popTransform(context);
      }
   }

   @Override
   public void init() {
      HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lyfw-client", this.idSuffix()), (context, tickCounter) -> {
         if (this.isEnabled()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && !mc.options.hideGui) {
               this.pushTransform(context);
               drawing = true;

               try {
                  this.render(context);
               } finally {
                  drawing = false;
                  this.popTransform(context);
               }
            }
         }
      });
   }

   private String idSuffix() {
      return this.name.toLowerCase().replace(' ', '_');
   }
}
