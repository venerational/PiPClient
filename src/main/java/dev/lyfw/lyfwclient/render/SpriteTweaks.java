package dev.lyfw.lyfwclient.render;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import dev.lyfw.lyfwclient.mixin.SpriteContentsAccessor;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class SpriteTweaks {
   private static final Map<String, Map<Block, SpriteTweaks.Tweak>> REQUESTS = new TreeMap<>();
   private static final Map<TextureAtlasSprite, SpriteTweaks.Tweak> APPLIED = new IdentityHashMap<>();
   private static GpuTexture appliedTo;
   private static String appliedSignature;

   private SpriteTweaks() {
   }

   public static void request(String who, Map<Block, SpriteTweaks.Tweak> blocks) {
      if (blocks.isEmpty()) {
         REQUESTS.remove(who);
      } else {
         REQUESTS.put(who, blocks);
      }

      sync();
   }

   private static void sync() {
      if (!REQUESTS.isEmpty() || !APPLIED.isEmpty()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS) instanceof TextureAtlas atlas) {
            GpuTexture sheet;
            try {
               sheet = atlas.getTexture();
            } catch (IllegalStateException var11) {
               return;
            }

            if (sheet != appliedTo) {
               APPLIED.clear();
               appliedTo = sheet;
               appliedSignature = null;
            }

            String signature = REQUESTS.toString();
            if (!signature.equals(appliedSignature)) {
               appliedSignature = signature;
               Map<TextureAtlasSprite, SpriteTweaks.Tweak> target = new IdentityHashMap<>();

               for (Map<Block, SpriteTweaks.Tweak> blocks : REQUESTS.values()) {
                  for (Entry<Block, SpriteTweaks.Tweak> entry : blocks.entrySet()) {
                     if (!entry.getValue().equals(SpriteTweaks.Tweak.NONE)) {
                        for (TextureAtlasSprite sprite : spritesOf(mc, entry.getKey())) {
                           target.merge(sprite, entry.getValue(), SpriteTweaks.Tweak::merge);
                        }
                     }
                  }
               }

               for (TextureAtlasSprite sprite : new ArrayList<>(APPLIED.keySet())) {
                  if (!target.containsKey(sprite)) {
                     write(sheet, sprite, SpriteTweaks.Tweak.NONE);
                     APPLIED.remove(sprite);
                  }
               }

               for (Entry<TextureAtlasSprite, SpriteTweaks.Tweak> entryx : target.entrySet()) {
                  if (!entryx.getValue().equals(APPLIED.get(entryx.getKey()))) {
                     write(sheet, entryx.getKey(), entryx.getValue());
                     APPLIED.put(entryx.getKey(), entryx.getValue());
                  }
               }
            }
         }
      }
   }

   private static List<TextureAtlasSprite> spritesOf(Minecraft mc, Block block) {
      Map<TextureAtlasSprite, Boolean> found = new IdentityHashMap<>();
      RandomSource random = RandomSource.create(42L);
      UnmodifiableIterator out = block.getStateDefinition().getPossibleStates().iterator();

      while (out.hasNext()) {
         BlockState state = (BlockState)out.next();
         BlockStateModel model = mc.getBlockRenderer().getBlockModelShaper().getBlockModel(state);
         found.put(model.particleIcon(), true);

         for (BlockModelPart part : model.collectParts(random)) {
            for (Direction face : Direction.values()) {
               for (BakedQuad quad : part.getQuads(face)) {
                  found.put(quad.sprite(), true);
               }
            }

            for (BakedQuad quad : part.getQuads(null)) {
               found.put(quad.sprite(), true);
            }
         }
      }

      List<TextureAtlasSprite> outx = new ArrayList<>();
      Identifier own = BuiltInRegistries.BLOCK.getKey(block);

      for (TextureAtlasSprite sprite : found.keySet()) {
         if (sprite.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS) && !sprite.contents().isAnimated() && !belongsElsewhere(sprite, own)) {
            outx.add(sprite);
         }
      }

      return outx;
   }

   private static boolean belongsElsewhere(TextureAtlasSprite sprite, Identifier own) {
      Identifier id = sprite.contents().name();
      if (!id.getPath().startsWith("block/")) {
         return false;
      } else {
         Identifier named = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().substring("block/".length()));
         return !named.equals(own) && BuiltInRegistries.BLOCK.containsKey(named);
      }
   }

   private static void write(GpuTexture sheet, TextureAtlasSprite sprite, SpriteTweaks.Tweak tweak) {
      NativeImage[] levels = ((SpriteContentsAccessor)sprite.contents()).lyfwclient$mipmaps();
      float s = tweak.saturation() / 100.0F;
      float a = tweak.alpha() / 255.0F;
      int tr = tweak.tint() >> 16 & 0xFF;
      int tg = tweak.tint() >> 8 & 0xFF;
      int tb = tweak.tint() & 0xFF;

      for (int mip = 0; mip < levels.length && mip < sheet.getMipLevels(); mip++) {
         NativeImage original = levels[mip];
         int w = original.getWidth();
         int h = original.getHeight();
         NativeImage out = new NativeImage(w, h, false);

         try {
            for (int y = 0; y < h; y++) {
               for (int x = 0; x < w; x++) {
                  int c = original.getPixel(x, y);
                  int r = c >> 16 & 0xFF;
                  int g = c >> 8 & 0xFF;
                  int b = c & 0xFF;
                  float grey = r * 0.299F + g * 0.587F + b * 0.114F;
                  if (tweak.tint() != -1) {
                     float light = 0.35F + 0.65F * grey / 255.0F;
                     r = Math.round(tr * light);
                     g = Math.round(tg * light);
                     b = Math.round(tb * light);
                     grey = r * 0.299F + g * 0.587F + b * 0.114F;
                  }

                  r = clamp(Math.round(grey + (r - grey) * s));
                  g = clamp(Math.round(grey + (g - grey) * s));
                  b = clamp(Math.round(grey + (b - grey) * s));
                  int alpha = Math.round((c >>> 24) * a);
                  out.setPixel(x, y, alpha << 24 | r << 16 | g << 8 | b);
               }
            }

            int x = Math.round(sprite.getU0() * sheet.getWidth(0)) >> mip;
            int y = Math.round(sprite.getV0() * sheet.getHeight(0)) >> mip;
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(sheet, out, mip, 0, x, y, w, h, 0, 0);
         } catch (Throwable var23) {
            try {
               out.close();
            } catch (Throwable var22) {
               var23.addSuppressed(var22);
            }

            throw var23;
         }

         out.close();
      }
   }

   private static int clamp(int v) {
      return Math.max(0, Math.min(255, v));
   }

   private static int multiply(int a, int b) {
      int r = (a >> 16 & 0xFF) * (b >> 16 & 0xFF) / 255;
      int g = (a >> 8 & 0xFF) * (b >> 8 & 0xFF) / 255;
      int bl = (a & 0xFF) * (b & 0xFF) / 255;
      return r << 16 | g << 8 | bl;
   }

   public record Tweak(int saturation, int tint, int alpha) {
      public static final SpriteTweaks.Tweak NONE = new SpriteTweaks.Tweak(100, -1, 255);

      SpriteTweaks.Tweak merge(SpriteTweaks.Tweak other) {
         int mixed = this.tint == -1 ? other.tint : (other.tint == -1 ? this.tint : SpriteTweaks.multiply(this.tint, other.tint));
         return new SpriteTweaks.Tweak(Math.min(this.saturation, other.saturation), mixed, Math.min(this.alpha, other.alpha));
      }
   }
}
