package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import dev.lyfw.lyfwclient.stats.CosmeticSync;
import dev.lyfw.lyfwclient.stats.Presence;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class PipPresenceModule extends Module {
   private final BooleanSetting showStar = this.register(new BooleanSetting("Star On Pip Users", true));
   private final TextSetting star = this.register(new TextSetting("Star", "★"));
   private final ColorSetting starColor = this.register(new ColorSetting("Star Color", -21931));
   private final TextSetting host = this.register(new TextSetting("Host", "https://pip-client-d611b-default-rtdb.asia-southeast1.firebasedatabase.app"));

   public PipPresenceModule() {
      super("Pip Presence", "Puts a star on other Pip Client users, and shows what they are listening to.", Category.MISC, true);
      this.hidden = true;
      this.showStar.group = "Star";
      this.star.group = "Star";
      this.starColor.group = "Star";
      this.host.group = "Connection";
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public void tick() {
      if (this.visibleToOthers()) {
         Presence.get().tick(this.host.get(), this.sharingAllowed() ? this.mySong() : "");
      }

      CosmeticsModule cosmetics = CosmeticsModule.get();
      CosmeticSync.get().tick(this.host.get(), this.visibleToOthers() && cosmetics != null ? cosmetics.sharedOutfit() : null);
   }

   private boolean sharingAllowed() {
      return ModuleManager.get("Song Player") instanceof SongPlayerModule song && song.shareToOthers();
   }

   private boolean visibleToOthers() {
      return !(ModuleManager.get("Song Player") instanceof SongPlayerModule song && !song.appearToOthers());
   }

   private String mySong() {
      if (ModuleManager.get("Song Player") instanceof SongPlayerModule song && song.isEnabled() && song.isPlaying()) {
         String title = song.nowPlayingTitle();
         String artist = song.nowPlayingArtist();
         if (title != null && !title.isEmpty() && !title.equals("Nothing playing")) {
            return artist != null && !artist.isEmpty() ? title + " - " + artist : title;
         }
      }

      return "";
   }

   public Component tabName(UUID id, Component name) {
      if (name != null && this.showStar.get() && Presence.get().of(id) != null) {
         String mark = this.star.get() != null && !this.star.get().isEmpty() ? this.star.get() : "★";
         return Component.empty().append(name).append(Component.literal(" " + mark).withStyle(s -> s.withColor(this.starColor.get())));
      } else {
         return null;
      }
   }

   public Component decorate(Player player, Component name) {
      UUID id = player.getUUID();
      Presence.Entry entry = Presence.get().of(id);
      if (entry == null) {
         return name;
      } else {
         MutableComponent out = Component.empty().append(name);
         if (this.showStar.get()) {
            String mark = this.star.get() != null && !this.star.get().isEmpty() ? this.star.get() : "★";
            out = out.append(Component.literal(" " + mark).withStyle(s -> s.withColor(this.starColor.get())));
         }

         if (ModuleManager.get("Song Player") instanceof SongPlayerModule song && song.showOthersSongs() && !entry.song().isEmpty()) {
            int color = song.othersSongColor();
            out = out.append(Component.literal(" ♪ " + entry.song()).withStyle(s -> s.withColor(color)));
         }

         return out;
      }
   }
}
