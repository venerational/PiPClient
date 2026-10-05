package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class NametagPingModule extends Module {
   private final EnumSetting<NametagPingModule.Position> position = this.register(new EnumSetting<>("Position", NametagPingModule.Position.AFTER));
   private final TextSetting format = this.register(new TextSetting("Format", "[%dms]"));
   private final BooleanSetting showSelf = this.register(new BooleanSetting("Show On Self", false));
   private final SliderSetting goodBelow = this.register(new SliderSetting("Good Below", 60.0, 10.0, 300.0, 5.0, "ms"));
   private final SliderSetting badAbove = this.register(new SliderSetting("Bad Above", 150.0, 20.0, 600.0, 5.0, "ms"));
   private final ColorSetting goodColor = this.register(new ColorSetting("Good Color", -16711792));
   private final ColorSetting okColor = this.register(new ColorSetting("Medium Color", -256));
   private final ColorSetting badColor = this.register(new ColorSetting("Bad Color", -49088));
   private final ColorSetting unknownColor = this.register(new ColorSetting("Unknown Color", -8355712));
   private final BooleanSetting hideUnknown = this.register(new BooleanSetting("Hide When Unknown", true));

   public NametagPingModule() {
      super("Nametag Ping", "Shows each player's ping on their nametag, colored by how good it is.", Category.RENDER, false);
      this.position.group = "Layout";
      this.format.group = "Layout";
      this.showSelf.group = "Layout";
      this.hideUnknown.group = "Layout";
      this.goodBelow.group = "Thresholds";
      this.badAbove.group = "Thresholds";
      this.goodColor.group = "Colors";
      this.okColor.group = "Colors";
      this.badColor.group = "Colors";
      this.unknownColor.group = "Colors";
   }

   public Component decorate(Player player, Component name) {
      Minecraft mc = Minecraft.getInstance();
      if (!this.showSelf.get() && player == mc.player) {
         return name;
      } else {
         int ping = this.pingOf(player, mc);
         if (ping < 0 && this.hideUnknown.get()) {
            return name;
         } else {
            String text = this.formatted(ping);
            int color = ping < 0 ? this.unknownColor.get() : this.colorFor(ping);
            MutableComponent tag = Component.literal(text).withStyle(s -> s.withColor(color));
            return this.position.get() == NametagPingModule.Position.BEFORE
               ? Component.empty().append(tag).append(Component.literal(" ")).append(name)
               : Component.empty().append(name).append(Component.literal(" ")).append(tag);
         }
      }
   }

   private int pingOf(Player player, Minecraft mc) {
      if (mc.getConnection() == null) {
         return -1;
      } else {
         PlayerInfo entry = mc.getConnection().getPlayerInfo(player.getUUID());
         return entry == null ? -1 : entry.getLatency();
      }
   }

   private String formatted(int ping) {
      String pattern = this.format.get() != null && !this.format.get().isEmpty() ? this.format.get() : "[%dms]";
      String shown = ping < 0 ? "?" : String.valueOf(ping);

      try {
         return pattern.contains("%d") ? pattern.replace("%d", shown) : pattern + shown;
      } catch (RuntimeException var5) {
         return "[" + shown + "ms]";
      }
   }

   private int colorFor(int ping) {
      return ping <= this.goodBelow.get() ? this.goodColor.get() : ping >= this.badAbove.get() ? this.badColor.get() : this.okColor.get();
   }

   public static enum Position {
      BEFORE,
      AFTER;
   }
}
