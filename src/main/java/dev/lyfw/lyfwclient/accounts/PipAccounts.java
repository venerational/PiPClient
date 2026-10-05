package dev.lyfw.lyfwclient.accounts;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import ru.vidtu.ias.IAS;
import ru.vidtu.ias.IASMinecraft;
import ru.vidtu.ias.account.Account;
import ru.vidtu.ias.account.MicrosoftAccount;
import ru.vidtu.ias.auth.LoginData;
import ru.vidtu.ias.auth.handlers.LoginHandler;
import ru.vidtu.ias.config.IASStorage;
import ru.vidtu.ias.utils.exceptions.FriendlyException;

public final class PipAccounts {
   private static final Map<UUID, Identifier> SKINS = new ConcurrentHashMap<>();
   private static final Set<UUID> FETCHING = ConcurrentHashMap.newKeySet();
   private static volatile String status = "";
   private static volatile long statusUntil;
   private static volatile Account pending;
   private static volatile boolean busy;

   private PipAccounts() {
   }

   public static void init() {
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         Account waiting = pending;
         if (waiting != null && !busy && !inWorld(client)) {
            pending = null;
            switchNow(waiting);
         }
      });
   }

   public static boolean available() {
      try {
         return !IAS.disabled();
      } catch (Throwable var1) {
         return false;
      }
   }

   public static List<Account> accounts() {
      return List.copyOf(IASStorage.ACCOUNTS);
   }

   public static boolean isCurrent(Account account) {
      Minecraft mc = Minecraft.getInstance();
      UUID now = mc.getUser() == null ? null : mc.getUser().getProfileId();
      return now != null && now.equals(account.uuid());
   }

   public static boolean isPending(Account account) {
      return pending == account;
   }

   public static String status() {
      return !busy && System.currentTimeMillis() >= statusUntil ? "" : status;
   }

   private static void say(String text, long millis) {
      status = text;
      statusUntil = System.currentTimeMillis() + millis;
   }

   public static void remove(Account account) {
      IASStorage.ACCOUNTS.remove(account);
      if (pending == account) {
         pending = null;
      }

      save();
      say("Removed " + account.name(), 3000L);
   }

   public static void add(MicrosoftAccount account) {
      IASStorage.ACCOUNTS.removeIf(existing -> existing.uuid().equals(account.uuid()));
      IASStorage.ACCOUNTS.add(account);
      save();
      say("Added " + account.name(), 4000L);
   }

   private static void save() {
      try {
         IAS.disclaimersStorage();
         IAS.saveStorage();
      } catch (Throwable var1) {
         say("Could not save your accounts", 5000L);
      }
   }

   public static void select(Account account) {
      Minecraft mc = Minecraft.getInstance();
      if (isCurrent(account)) {
         say("Already signed in as " + account.name(), 3000L);
      } else if (busy) {
         say("Still signing in - one moment", 3000L);
      } else if (inWorld(mc)) {
         pending = account;
         say("Switches to " + account.name() + " when you leave the world", 6000L);
      } else {
         switchNow(account);
      }
   }

   private static boolean inWorld(Minecraft mc) {
      return mc.player != null
         || mc.level != null
         || mc.getConnection() != null
         || mc.getCameraEntity() != null
         || mc.gameMode != null
         || mc.getSingleplayerServer() != null;
   }

   private static void switchNow(Account account) {
      final Minecraft mc = Minecraft.getInstance();
      busy = true;
      status = "Signing in as " + account.name() + "...";

      try {
         account.login(new LoginHandler() {
            public boolean cancelled() {
               return false;
            }

            public void stage(String key, Object... args) {
            }

            public CompletableFuture<String> password() {
               return CompletableFuture.failedFuture(new IllegalStateException("password crypt"));
            }

            public void success(LoginData data, boolean changed) {
               if (data == null) {
                  mc.execute(() -> PipAccounts.finish("Could not sign in as " + account.name()));
               } else {
                  if (changed) {
                     PipAccounts.save();
                  }

                  IASMinecraft.account(mc, data).thenRunAsync(() -> PipAccounts.finish("Signed in as " + data.name()), mc).exceptionally(t -> {
                     mc.execute(() -> PipAccounts.finish(PipAccounts.describe(t)));
                     return null;
                  });
               }
            }

            public void error(Throwable t) {
               mc.execute(() -> PipAccounts.finish(PipAccounts.describe(t)));
            }
         }, () -> {});
      } catch (Throwable var3) {
         finish(describe(var3));
      }
   }

   private static void finish(String text) {
      busy = false;
      say(text, 6000L);
   }

   public static String describe(Throwable t) {
      if (t != null && String.valueOf(t.getMessage()).contains("password crypt")) {
         return "That account has an IAS password - remove it and add it again";
      } else {
         FriendlyException friendly = t == null ? null : FriendlyException.friendlyInChain(t);
         String key = friendly == null ? "" : friendly.key();

         return switch (key) {
            case "ias.error.world" -> "Leave the world first";
            case "ias.error.decrypt" -> "Could not unlock that account - remove it and add it again";
            case "ias.error.connect" -> "Could not reach Microsoft";
            case "ias.error.cancel" -> "Sign-in was cancelled";
            case "ias.error.noXbox", "ias.error.noProfile" -> "That Microsoft account does not own Minecraft";
            case "ias.error.xboxAdult" -> "That Xbox account needs a parent to approve it";
            case "ias.error.xboxAvailable" -> "Xbox Live is not available in that region";
            default -> "Sign-in failed";
         };
      }
   }

   public static Identifier skin(Account account) {
      UUID id = account.skin();
      Identifier cached = SKINS.get(id);
      if (cached != null) {
         return cached;
      } else {
         if (FETCHING.add(id)) {
            Minecraft mc = Minecraft.getInstance();
            CompletableFuture.<GameProfile>supplyAsync(() -> {
                  ProfileResult result = mc.services().sessionService().fetchProfile(id, false);
                  return result == null ? null : result.profile();
               }, IAS.executor())
               .thenCompose(profile -> profile == null ? CompletableFuture.completedFuture(Optional.empty()) : mc.getSkinManager().get(profile))
               .thenAccept(found -> found.ifPresent(textures -> SKINS.put(id, textures.body().texturePath())))
               .exceptionally(t -> null);
         }

         return DefaultPlayerSkin.get(id).body().texturePath();
      }
   }
}
