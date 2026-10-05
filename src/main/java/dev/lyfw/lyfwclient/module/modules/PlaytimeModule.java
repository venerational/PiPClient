package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.LeaderboardScreen;
import dev.lyfw.lyfwclient.gui.ThemeRenderer;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import dev.lyfw.lyfwclient.stats.Leaderboard;
import dev.lyfw.lyfwclient.stats.PlaytimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class PlaytimeModule extends HudModule {
   private final EnumSetting<PlaytimeModule.Display> display = this.register(new EnumSetting<>("Show", PlaytimeModule.Display.TOTAL));
   private final BooleanSetting showLabel = this.register(new BooleanSetting("Show Label", true));
   private final BooleanSetting showRank = this.register(new BooleanSetting("Show Rank", false));
   private final TextSetting endpoint = this.register(new TextSetting("Leaderboard Host", ""));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -1));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", -1878982656));
   private final BooleanSetting background = this.register(new BooleanSetting("Background", true));
   private int tickCounter;

   public PlaytimeModule() {
      super("Leaderboard", "Your hours with Pip Client, ranked against everyone else running it. Right-click for the standings.", false, 4.0, 220.0);
      this.display.group = "Display";
      this.showLabel.group = "Display";
      this.showRank.group = "Display";
      this.background.group = "Display";
      this.endpoint.group = "Leaderboard";
      this.textColor.group = "Colors";
      this.backgroundColor.group = "Colors";
      this.hidden = true;
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return new LeaderboardScreen(parent, this);
   }

   public String endpointUrl() {
      String set = this.endpoint.get() == null ? "" : this.endpoint.get().trim();
      return set.isEmpty() ? "https://pip-client-d611b-default-rtdb.asia-southeast1.firebasedatabase.app" : set;
   }

   public boolean sharingEnabled() {
      return !this.endpointUrl().isEmpty();
   }

   @Override
   public void tick() {
      PlaytimeTracker.get().tick();
      if (this.sharingEnabled() && ++this.tickCounter % 200 == 0) {
         Leaderboard.get().submitIfDue(this.endpointUrl());
      }
   }

   private String line() {
      PlaytimeTracker tracker = PlaytimeTracker.get();
      String label = this.showLabel.get() ? "Playtime: " : "";

      return switch ((PlaytimeModule.Display)this.display.get()) {
         case SESSION -> label + PlaytimeTracker.format(tracker.sessionSeconds());
         case BOTH -> label + PlaytimeTracker.format(tracker.totalSeconds()) + " (" + PlaytimeTracker.format(tracker.sessionSeconds()) + ")";
         default -> label + PlaytimeTracker.format(tracker.totalSeconds());
      };
   }

   private String rankSuffix() {
      if (!this.showRank.get()) {
         return "";
      } else {
         for (Leaderboard.Entry entry : Leaderboard.get().entries()) {
            if (entry.self()) {
               return "  #" + entry.rank();
            }
         }

         return "";
      }
   }

   @Override
   protected int contentWidth() {
      Minecraft mc = Minecraft.getInstance();
      return mc.font.width(this.line() + this.rankSuffix()) + 10;
   }

   @Override
   protected int contentHeight() {
      return 14;
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      String text = this.line() + this.rankSuffix();
      int w = this.contentWidth();
      if (this.background.get()) {
         ThemeRenderer.fillRounded(context, 0, 0, w, 14, this.backgroundColor.get(), 4);
      }

      context.drawString(mc.font, text, 5, 3, this.textColor.get());
   }

   public static enum Display {
      TOTAL("Total"),
      SESSION("This Session"),
      BOTH("Both");

      public final String title;

      private Display(String title) {
         this.title = title;
      }
   }
}
