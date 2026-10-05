package dev.lyfw.lyfwclient.module.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.player.Player;

public final class TotemPopTracker {
   private static final Map<UUID, Integer> POP_COUNTS = new HashMap<>();
   private static final Map<UUID, Long> LAST_POP_TIME = new HashMap<>();
   private static final Set<UUID> NAMETAG_ENTITY_HANDLED = ConcurrentHashMap.newKeySet();
   private static final Map<UUID, TextDisplay> VERIFIED_NAMETAG_ENTITY = new ConcurrentHashMap<>();

   private TotemPopTracker() {
   }

   public static void recordPop(Player player) {
      UUID id = player.getUUID();
      long tick = player.level().getGameTime();
      Long last = LAST_POP_TIME.get(id);
      if (last == null || last != tick) {
         LAST_POP_TIME.put(id, tick);
         POP_COUNTS.merge(id, 1, Integer::sum);
         PopChamsModule chams = PopChamsModule.get();
         if (chams != null) {
            chams.onPop(player);
         }
      }
   }

   public static int getPopCount(UUID playerId) {
      return POP_COUNTS.getOrDefault(playerId, 0);
   }

   public static void resetCounts() {
      POP_COUNTS.clear();
      LAST_POP_TIME.clear();
   }

   public static void markNametagEntityHandled(UUID playerId) {
      NAMETAG_ENTITY_HANDLED.add(playerId);
   }

   public static boolean isNametagEntityHandled(UUID playerId) {
      return NAMETAG_ENTITY_HANDLED.contains(playerId);
   }

   public static void clearNametagEntityHandledMarks() {
      NAMETAG_ENTITY_HANDLED.clear();
   }

   public static void recordVerifiedNametagEntity(UUID playerId, TextDisplay entity) {
      VERIFIED_NAMETAG_ENTITY.put(playerId, entity);
   }

   public static TextDisplay getVerifiedNametagEntity(UUID playerId) {
      TextDisplay entity = VERIFIED_NAMETAG_ENTITY.get(playerId);
      if (entity != null && entity.isRemoved()) {
         VERIFIED_NAMETAG_ENTITY.remove(playerId);
         return null;
      } else {
         return entity;
      }
   }

   public static void tickDeathResets(Minecraft client) {
      if (client.level != null && !POP_COUNTS.isEmpty()) {
         Set<UUID> toReset = new HashSet<>();

         for (UUID id : POP_COUNTS.keySet()) {
            Player player = client.level.getPlayerByUUID(id);
            if (player != null && player.isDeadOrDying()) {
               toReset.add(id);
            }
         }

         for (UUID idx : toReset) {
            POP_COUNTS.remove(idx);
            LAST_POP_TIME.remove(idx);
         }
      }
   }

   public static enum PopIcon {
      HEART("♥"),
      STAR("★"),
      DIAMOND("♦"),
      DASH("-");

      public final String symbol;

      private PopIcon(String symbol) {
         this.symbol = symbol;
      }
   }
}
