package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.PopCounterModule;
import dev.lyfw.lyfwclient.module.modules.TotemPopTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.DisplayRenderer.TextDisplayRenderer;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.Display.TextDisplay.CachedInfo;
import net.minecraft.world.entity.Display.TextDisplay.CachedLine;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TextDisplayRenderer.class})
public class TextDisplayNametagMixin {
   private static final Logger LOGGER = LoggerFactory.getLogger("lyfw-nametag");
   private static final Set<UUID> LYFWCLIENT$LOGGED_MISMATCH = ConcurrentHashMap.newKeySet();

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/client/renderer/entity/state/TextDisplayEntityRenderState;F)V"},
      at = {@At("RETURN")}
   )
   private void lyfwclient$appendPopSuffix(TextDisplay entity, TextDisplayEntityRenderState state, float tickDelta, CallbackInfo ci) {
      if (ModuleManager.get("Pop Counter") instanceof PopCounterModule pc && pc.isEnabled() && state.cachedInfo != null && !state.cachedInfo.lines().isEmpty()) {
         List<CachedLine> lines = state.cachedInfo.lines();
         List<String> lineTexts = new ArrayList<>(lines.size());

         for (CachedLine line : lines) {
            lineTexts.add(lyfwclient$orderedTextToString(line.contents()));
         }

         Player player;
         int targetIndex;
         if (entity.getVehicle() instanceof Player ridden) {
            player = ridden;
            targetIndex = lyfwclient$indexOfUsername(lineTexts, ridden.getName().getString());
            if (targetIndex == -1) {
               if (!lyfwclient$isOnlyTextDisplayPassenger(ridden)) {
                  if (LYFWCLIENT$LOGGED_MISMATCH.add(ridden.getUUID())) {
                     LOGGER.warn(
                        "Pop Counter: no line on {}'s ridden TextDisplayEntity matched username '{}' - lines were: {}",
                        new Object[]{ridden.getName().getString(), ridden.getName().getString(), lineTexts}
                     );
                  }

                  return;
               }

               targetIndex = lines.size() - 1;
            }
         } else {
            TextDisplayNametagMixin.Match match = lyfwclient$findNearbyMatch(entity, lineTexts);
            if (match == null) {
               return;
            }

            player = match.player();
            targetIndex = match.lineIndex();
         }

         if (player != Minecraft.getInstance().player) {
            TotemPopTracker.recordVerifiedNametagEntity(player.getUUID(), entity);
            int count = TotemPopTracker.getPopCount(player.getUUID());
            if (count > 0) {
               FormattedCharSequence countPart = FormattedCharSequence.forward(pc.nametagIconSymbol() + count, Style.EMPTY.withColor(pc.colorForCount(count)));
               FormattedCharSequence suffix;
               if (pc.showSeparator()) {
                  FormattedCharSequence separatorPart = FormattedCharSequence.forward(" | ", Style.EMPTY.withColor(pc.separatorColor()));
                  suffix = FormattedCharSequence.composite(separatorPart, countPart);
               } else {
                  suffix = FormattedCharSequence.composite(FormattedCharSequence.forward(" ", Style.EMPTY), countPart);
               }

               CachedLine original = lines.get(targetIndex);
               FormattedCharSequence combined = FormattedCharSequence.composite(original.contents(), suffix);
               int newWidth = Minecraft.getInstance().font.width(combined);
               List<CachedLine> newLines = new ArrayList<>(lines);
               newLines.set(targetIndex, new CachedLine(combined, newWidth));
               int newMaxWidth = Math.max(state.cachedInfo.width(), newWidth);
               state.cachedInfo = new CachedInfo(newLines, newMaxWidth);
               TotemPopTracker.markNametagEntityHandled(player.getUUID());
            }
         }
      }
   }

   private static boolean lyfwclient$isOnlyTextDisplayPassenger(Player ridden) {
      int count = 0;

      for (Entity passenger : ridden.getPassengers()) {
         if (passenger instanceof TextDisplay) {
            if (++count > 1) {
               return false;
            }
         }
      }

      return count == 1;
   }

   private static TextDisplayNametagMixin.Match lyfwclient$findNearbyMatch(TextDisplay entity, List<String> lineTexts) {
      for (Player candidate : entity.level().players()) {
         if (TotemPopTracker.getPopCount(candidate.getUUID()) > 0 && !(entity.distanceToSqr(candidate) > 9.0)) {
            int index = lyfwclient$indexOfUsername(lineTexts, candidate.getName().getString());
            if (index != -1) {
               return new TextDisplayNametagMixin.Match(candidate, index);
            }
         }
      }

      return null;
   }

   private static int lyfwclient$indexOfUsername(List<String> lineTexts, String username) {
      for (int i = 0; i < lineTexts.size(); i++) {
         if (lineTexts.get(i).contains(username)) {
            return i;
         }
      }

      String usernameLower = username.toLowerCase(Locale.ROOT);

      for (int ix = 0; ix < lineTexts.size(); ix++) {
         if (lineTexts.get(ix).toLowerCase(Locale.ROOT).contains(usernameLower)) {
            return ix;
         }
      }

      return -1;
   }

   private static String lyfwclient$orderedTextToString(FormattedCharSequence text) {
      StringBuilder sb = new StringBuilder();
      text.accept((index, style, codePoint) -> {
         sb.appendCodePoint(codePoint);
         return true;
      });
      return sb.toString();
   }

   private record Match(Player player, int lineIndex) {
   }
}
