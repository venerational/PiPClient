package dev.lyfw.lyfwclient.module;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;

public abstract class EnchantGlintTintModule extends Module {
   private static final int DEBOUNCE_TICKS = 5;
   private final List<EnchantGlintTintModule.Channel> channels = new ArrayList<>();

   protected EnchantGlintTintModule(String name, String description) {
      super(name, description, Category.RENDER, false);
   }

   protected void channel(Identifier texture, IntSupplier tint, DoubleSupplier strength) {
      this.channels.add(new EnchantGlintTintModule.Channel(texture, tint, strength));
   }

   public void markDirty() {
      for (EnchantGlintTintModule.Channel channel : this.channels) {
         channel.dirty = true;
      }
   }

   @Override
   public void tick() {
      for (EnchantGlintTintModule.Channel channel : this.channels) {
         channel.tick();
      }
   }

   private static boolean apply(Identifier id, int tint, float strength) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.getResourceManager() != null && mc.getTextureManager() != null && mc.getTextureManager().getTexture(id) instanceof ReloadableTexture reloadable) {
         try {
            TextureContents original = TextureContents.load(mc.getResourceManager(), id);
            NativeImage image = original.image();
            if (tint != 0 || strength != 1.0F) {
               int tr = tint >> 16 & 0xFF;
               int tg = tint >> 8 & 0xFF;
               int tb = tint & 0xFF;
               int width = image.getWidth();
               int height = image.getHeight();

               for (int y = 0; y < height; y++) {
                  for (int x = 0; x < width; x++) {
                     int src = image.getPixel(x, y);
                     int a = src >>> 24 & 0xFF;
                     int r = src >> 16 & 0xFF;
                     int g = src >> 8 & 0xFF;
                     int b = src & 0xFF;
                     if (tint != 0) {
                        int luma = Math.max(b, Math.max(g, r));
                        r = luma * tr / 255;
                        g = luma * tg / 255;
                        b = luma * tb / 255;
                     }

                     r = Math.min(255, Math.round(r * strength));
                     g = Math.min(255, Math.round(g * strength));
                     b = Math.min(255, Math.round(b * strength));
                     image.setPixel(x, y, a << 24 | r << 16 | g << 8 | b);
                  }
               }
            }

            reloadable.apply(new TextureContents(image, original.metadata()));
            return true;
         } catch (IOException var20) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static final class Channel {
      private final Identifier texture;
      private final IntSupplier tint;
      private final DoubleSupplier strength;
      private long lastApplied;
      private boolean initialized;
      private long lastSeen = Long.MIN_VALUE;
      private int stableTicks;
      private boolean dirty = true;

      private Channel(Identifier texture, IntSupplier tint, DoubleSupplier strength) {
         this.texture = texture;
         this.tint = tint;
         this.strength = strength;
      }

      private void tick() {
         int tint = this.tint.getAsInt();
         int strength = (int)Math.round(this.strength.getAsDouble() * 100.0);
         long current = (long)tint << 32 | strength & 4294967295L;
         if (current == this.lastSeen) {
            if (this.stableTicks < 5) {
               this.stableTicks++;
            }
         } else {
            this.lastSeen = current;
            this.stableTicks = 0;
         }

         boolean ready = !this.initialized || this.stableTicks == 5;
         boolean needsApply = this.dirty || current != this.lastApplied;
         if (ready && needsApply && EnchantGlintTintModule.apply(this.texture, tint, strength / 100.0F)) {
            this.lastApplied = current;
            this.initialized = true;
            this.dirty = false;
         }
      }
   }
}
