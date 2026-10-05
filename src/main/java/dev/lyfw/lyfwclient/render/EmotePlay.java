package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public final class EmotePlay {
   private static final double MOVED = 0.0016;
   private static CosmeticsModule.Emote playing;
   private static long started;
   private static double lastX;
   private static double lastZ;

   private EmotePlay() {
   }

   public static void toggle(Minecraft client) {
      if (playing != null) {
         stop();
      } else {
         CosmeticsModule cosmetics = CosmeticsModule.get();
         CosmeticsModule.Emote picked = cosmetics == null ? null : cosmetics.emote();
         if (picked == null) {
            if (client.player != null) {
               client.player.displayClientMessage(Component.literal("Pick an emote in the Wardrobe first."), true);
            }
         } else {
            play(picked, client.player);
         }
      }
   }

   public static void play(CosmeticsModule.Emote emote, LocalPlayer player) {
      playing = emote;
      started = System.currentTimeMillis();
      if (player != null) {
         lastX = player.getX();
         lastZ = player.getZ();
      }
   }

   public static void stop() {
      playing = null;
   }

   public static CosmeticsModule.Emote playing() {
      return playing;
   }

   public static float seconds() {
      return (float)(System.currentTimeMillis() - started) / 1000.0F;
   }

   public static void tick(Minecraft client) {
      if (playing != null) {
         LocalPlayer player = client.player;
         if (player == null || client.level == null) {
            stop();
            return;
         }

         double dx = player.getX() - lastX;
         double dz = player.getZ() - lastZ;
         lastX = player.getX();
         lastZ = player.getZ();
         boolean busy = dx * dx + dz * dz > 0.0016
            || !player.onGround()
            || player.swinging
            || player.hurtTime > 0
            || player.isUsingItem()
            || player.isPassenger()
            || player.isFallFlying()
            || player.isSwimming()
            || player.isShiftKeyDown();
         if (busy) {
            stop();
         }
      }
   }
}
