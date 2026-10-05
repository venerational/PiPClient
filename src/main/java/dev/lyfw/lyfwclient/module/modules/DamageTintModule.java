package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.platform.NativeImage;
import dev.lyfw.lyfwclient.mixin.OverlayTextureAccessor;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class DamageTintModule extends Module {
   private static final int VANILLA_OVERLAY_COLOR = -1308622848;
   private final ColorSetting color = this.register(new ColorSetting("Color", -1291911168));
   private final BooleanSetting showOnArmor = this.register(new BooleanSetting("Show On Armor", true));
   private int lastAppliedOverlay;
   private boolean overlayInitialized;

   public DamageTintModule() {
      super("Damage Tint", "Tints entities while they're flashing hurt - matches Damage Tint Plus's own Damage Tint feature.", Category.RENDER, false);
   }

   public boolean usesArmorModelTint() {
      return this.isEnabled() && this.showOnArmor.get();
   }

   public int tintColor() {
      return this.color.get();
   }

   @Override
   public void tick() {
      int target = this.isEnabled() ? this.color.get() : -1308622848;
      if ((!this.overlayInitialized || target != this.lastAppliedOverlay) && this.paintOverlay(target)) {
         this.lastAppliedOverlay = target;
         this.overlayInitialized = true;
      }
   }

   private boolean paintOverlay(int argb) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.gameRenderer == null) {
         return false;
      } else {
         OverlayTexture overlay = mc.gameRenderer.overlayTexture();
         if (overlay == null) {
            return false;
         } else {
            DynamicTexture backed = ((OverlayTextureAccessor)overlay).lyfwclient$getTexture();
            NativeImage image = backed.getPixels();

            for (int y = 0; y < 8; y++) {
               for (int x = 0; x < 16; x++) {
                  image.setPixel(x, y, argb);
               }
            }

            backed.upload();
            return true;
         }
      }
   }
}
