package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.NameProtectModule;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

public final class PlayerHead {
   private static final int SHEET = 64;
   private static final int FACE_U = 8;
   private static final int FACE_V = 8;
   private static final int HAT_U = 40;
   private static final int HAT_V = 8;
   private static final int FACE = 8;
   private static Supplier<PlayerSkin> cachedSupplier;
   private static String cachedFor;

   private PlayerHead() {
   }

   public static String username() {
      Minecraft client = Minecraft.getInstance();
      String name = client.getUser() == null ? null : client.getUser().getName();
      return name != null && !name.isEmpty() ? name : "Player";
   }

   public static int drawChip(GuiGraphics context, int x, int y, int size, boolean pill) {
      int width = headWidth(size, pill);
      int inset = pill ? 4 : 0;
      if (pill) {
         ThemeRenderer.fillRounded(context, x, y - 3, width, size + 6, Theme.trackBg(), 5);
      }

      draw(context, x + inset, y, size);
      return width;
   }

   public static int headWidth(int size, boolean pill) {
      return size + (pill ? 8 : 0);
   }

   public static void draw(GuiGraphics context, int x, int y, int size) {
      drawFace(context, skinTexture(), x, y, size);
   }

   public static void drawFace(GuiGraphics context, Identifier skin, int x, int y, int size) {
      if (skin != null) {
         context.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 8.0F, 8.0F, size, size, 8, 8, 64, 64);
         context.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 40.0F, 8.0F, size, size, 8, 8, 64, 64);
      }
   }

   private static Identifier skinTexture() {
      return skinTextures().body().texturePath();
   }

   public static PlayerSkin skinTextures() {
      Minecraft client = Minecraft.getInstance();

      try {
         if (client.player != null) {
            return client.player.getSkin();
         } else {
            String key = String.valueOf(client.getUser() == null ? "" : client.getUser().getProfileId());
            if (cachedSupplier == null || !key.equals(cachedFor)) {
               cachedFor = key;
               cachedSupplier = client.getSkinManager().createLookup(client.getGameProfile(), false);
            }

            PlayerSkin textures = cachedSupplier.get();
            if (textures == null) {
               textures = DefaultPlayerSkin.get(client.getGameProfile());
            }

            if (ModuleManager.get("Name Protect") instanceof NameProtectModule nameProtect) {
               textures = nameProtect.skinFor(textures);
            }

            return textures;
         }
      } catch (Exception var5) {
         return DefaultPlayerSkin.get(client.getGameProfile());
      }
   }
}
