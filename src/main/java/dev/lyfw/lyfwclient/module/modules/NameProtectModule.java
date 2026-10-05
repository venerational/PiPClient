package dev.lyfw.lyfwclient.module.modules;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import dev.lyfw.lyfwclient.stats.Leaderboard;
import dev.lyfw.lyfwclient.stats.Presence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

public class NameProtectModule extends Module {
   private static final int ATTEMPTS = 8;
   private static final int BOARD_WAIT_MS = 10000;
   private final TextSetting name = this.register(new TextSetting("Name", "Player"));
   private final BooleanSetting changeSkin = this.register(new BooleanSetting("Change Skin", false));
   private final BooleanSetting hideInventory = this.register(new BooleanSetting("Hide Inventory Player", false));
   private final TextSetting skinPlayer = this.register(new TextSetting("Skin Player", ""));
   private volatile PlayerSkin skin;
   private String loadedFor = "";
   private volatile boolean picking;
   private volatile String status = "";

   public NameProtectModule() {
      super("Name Protect", "Replaces your own username wherever the client renders it - matches \"NameProtect\".", Category.MISC, false);
   }

   public TextSetting nameSetting() {
      return this.name;
   }

   public BooleanSetting changeSkinSetting() {
      return this.changeSkin;
   }

   public TextSetting skinPlayerSetting() {
      return this.skinPlayer;
   }

   public boolean isPicking() {
      return this.picking;
   }

   public boolean hidesInventoryPlayer() {
      return this.isEnabled() && this.hideInventory.get();
   }

   public String skinLabel() {
      String now = this.status;
      return now.isEmpty() ? this.pickedName() : now;
   }

   private String pickedName() {
      String pick = this.skinPlayer.get();
      int bar = pick == null ? -1 : pick.indexOf(124);
      return bar < 0 ? "" : pick.substring(0, bar);
   }

   private static UUID pickedId(String pick) {
      int bar = pick.indexOf(124);

      try {
         return bar < 0 ? null : UUID.fromString(pick.substring(bar + 1));
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }

   public PlayerSkin skinFor(PlayerSkin original) {
      if (this.isEnabled() && this.changeSkin.get()) {
         String pick = this.skinPlayer.get();
         if (pick != null && !pick.isEmpty()) {
            if (!pick.equals(this.loadedFor)) {
               this.loadedFor = pick;
               this.skin = null;
               this.load(pick);
            }

            PlayerSkin loaded = this.skin;
            return loaded != null ? loaded : DefaultPlayerSkin.getDefaultSkin();
         } else {
            return DefaultPlayerSkin.getDefaultSkin();
         }
      } else {
         return original;
      }
   }

   public void randomizeSkin() {
      if (!this.picking) {
         Minecraft mc = Minecraft.getInstance();
         UUID self = mc.getUser() == null ? null : mc.getUser().getProfileId();
         String selfName = mc.getUser() == null ? "" : mc.getUser().getName();
         String current = this.pickedName();
         List<GameProfile> tabList = new ArrayList<>();
         if (mc.getConnection() != null) {
            for (PlayerInfo entry : mc.getConnection().getOnlinePlayers()) {
               GameProfile profile = entry.getProfile();
               if (profile != null && profile.name() != null && !profile.id().equals(self)) {
                  tabList.add(profile);
               }
            }
         }

         this.picking = true;
         this.status = "Picking...";
         Thread thread = new Thread(() -> {
            try {
               List<NameProtectModule.Candidate> candidates = new ArrayList<>();
               List<String> seen = new ArrayList<>();

               for (GameProfile profilex : tabList) {
                  addCandidate(candidates, seen, profilex, null, profilex.name(), selfName, current);
               }

               for (Presence.Entry entryxx : Presence.get().everyone()) {
                  addCandidate(candidates, seen, null, entryxx.id(), entryxx.name(), selfName, current);
               }

               if (Leaderboard.get().entries().isEmpty()) {
                  waitForBoard();
               }

               for (Leaderboard.Entry entryx : Leaderboard.get().entries()) {
                  addCandidate(candidates, seen, null, null, entryx.name(), selfName, current);
               }

               if (candidates.isEmpty()) {
                  this.status = "No players to pick from";
               } else {
                  Collections.shuffle(candidates);

                  for (int i = 0; i < Math.min(8, candidates.size()); i++) {
                     if (this.tryCandidate(mc, candidates.get(i))) {
                        return;
                     }
                  }

                  this.status = "Could not load a skin - try again";
               }
            } finally {
               this.picking = false;
            }
         }, "Pip Client Random Skin");
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static void waitForBoard() {
      String endpoint = ModuleManager.get("Leaderboard") instanceof PlaytimeModule playtime
         ? playtime.endpointUrl()
         : "https://pip-client-d611b-default-rtdb.asia-southeast1.firebasedatabase.app";
      Leaderboard.get().refresh(endpoint);

      try {
         for (int waited = 0; waited < 10000 && Leaderboard.get().busy(); waited += 200) {
            Thread.sleep(200L);
         }
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   private boolean tryCandidate(Minecraft mc, NameProtectModule.Candidate candidate) {
      try {
         GameProfile profile = candidate.profile();
         Optional<PlayerSkin> textures = profile == null ? Optional.empty() : (Optional)mc.getSkinManager().get(profile).join();
         if (textures.isEmpty()) {
            UUID id = candidate.id() != null
               ? candidate.id()
               : mc.services().profileResolver().fetchByName(candidate.name()).<UUID>map(GameProfile::id).orElse(null);
            profile = id == null ? null : fetchProfile(mc, id);
            textures = profile == null ? Optional.empty() : (Optional)mc.getSkinManager().get(profile).join();
         }

         if (textures.isEmpty()) {
            return false;
         } else {
            GameProfile chosen = profile;
            PlayerSkin found = textures.get();
            mc.execute(() -> this.wear(chosen, found, true));
            return true;
         }
      } catch (RuntimeException var7) {
         return false;
      }
   }

   private void wear(GameProfile chosen, PlayerSkin found, boolean takeName) {
      String pick = chosen.name() + "|" + chosen.id();
      this.skinPlayer.set(pick);
      if (takeName) {
         this.name.set(chosen.name());
      }

      this.loadedFor = pick;
      this.skin = found;
      this.status = "";
      Config.save();
   }

   public void useTypedNameSkin() {
      String typed = stripFormatting(this.name.get());
      if (!this.picking && typed.matches("[A-Za-z0-9_]{3,16}") && !typed.equalsIgnoreCase(this.pickedName())) {
         Minecraft mc = Minecraft.getInstance();
         this.picking = true;
         this.status = "Looking up " + typed + "...";
         Thread thread = new Thread(() -> {
            try {
               UUID id = mc.services().profileResolver().fetchByName(typed).<UUID>map(GameProfile::id).orElse(null);
               if (id == null) {
                  this.status = "No player called " + typed;
                  return;
               }

               GameProfile profile = fetchProfile(mc, id);
               if (profile == null) {
                  this.status = "Could not load " + typed + "'s skin, try again in a moment";
                  return;
               }

               Optional<PlayerSkin> textures = (Optional<PlayerSkin>)mc.getSkinManager().get(profile).join();
               if (!textures.isEmpty()) {
                  PlayerSkin found = textures.get();
                  mc.execute(() -> this.wear(profile, found, false));
                  return;
               }

               this.status = "Could not load " + typed + "'s skin";
            } catch (RuntimeException var11) {
               this.status = "Could not load " + typed + "'s skin";
               return;
            } finally {
               this.picking = false;
            }
         }, "Pip Client Name Skin");
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static String stripFormatting(String text) {
      return text == null ? "" : text.replaceAll("&#[A-Fa-f0-9]{6}", "").replaceAll("(?i)[&§][0-9a-fk-orx]", "").trim();
   }

   private static void addCandidate(
      List<NameProtectModule.Candidate> candidates, List<String> seen, GameProfile profile, UUID id, String name, String selfName, String current
   ) {
      if (name != null && !name.isEmpty() && !name.equalsIgnoreCase(selfName) && !name.equalsIgnoreCase(current)) {
         String key = name.toLowerCase();
         if (!seen.contains(key)) {
            seen.add(key);
            candidates.add(new NameProtectModule.Candidate(profile, id, name));
         }
      }
   }

   private void load(String pick) {
      UUID id = pickedId(pick);
      if (id != null) {
         Minecraft mc = Minecraft.getInstance();
         Thread thread = new Thread(() -> {
            try {
               GameProfile profile = fetchProfile(mc, id);
               Optional<PlayerSkin> textures = profile == null ? Optional.empty() : (Optional)mc.getSkinManager().get(profile).join();
               textures.ifPresent(found -> mc.execute(() -> {
                  if (pick.equals(this.skinPlayer.get())) {
                     this.skin = found;
                  }
               }));
            } catch (RuntimeException var6) {
            }
         }, "Pip Client Skin");
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static GameProfile fetchProfile(Minecraft mc, UUID id) {
      ProfileResult result = mc.services().sessionService().fetchProfile(id, false);
      return result == null ? null : result.profile();
   }

   public String replaceName(String text) {
      if (text != null && this.isEnabled()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.getUser() == null) {
            return text;
         } else {
            String username = mc.getUser().getName();
            if (username == null) {
               return text;
            } else {
               String replacement = translateColorCodes(this.name.get()) + "§r";
               return text.contains(replacement) ? text : text.replace(username, replacement);
            }
         }
      } else {
         return text;
      }
   }

   private static String translateColorCodes(String text) {
      if (text == null) {
         return null;
      } else {
         Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(text);
         StringBuilder sb = new StringBuilder();

         while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacementBuilder = new StringBuilder("§x");

            for (char c : hex.toCharArray()) {
               replacementBuilder.append('§').append(c);
            }

            matcher.appendReplacement(sb, replacementBuilder.toString());
         }

         matcher.appendTail(sb);
         String hexTranslated = sb.toString();
         return hexTranslated.replaceAll("(?i)&([0-9a-fk-or])", "§$1");
      }
   }

   private record Candidate(GameProfile profile, UUID id, String name) {
   }
}
