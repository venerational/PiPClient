package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.PipPresenceModule;
import dev.lyfw.lyfwclient.module.modules.PlaytimeModule;
import dev.lyfw.lyfwclient.profile.ProfileStorage;
import dev.lyfw.lyfwclient.stats.Leaderboard;
import dev.lyfw.lyfwclient.stats.PlaytimeTracker;
import dev.lyfw.lyfwclient.stats.Presence;
import dev.lyfw.lyfwclient.stats.PublicProfiles;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class LeaderboardScreen extends Screen {
   private static final int PANEL_W = 320;
   private static final int PAD = 14;
   private static final int HEADER_H = 40;
   private static final int STAT_H = 14;
   private static final int ROW_H = 16;
   private static final int MAX_ROWS = 8;
   private static final int SIDE_W = 216;
   private static final int SIDE_GAP = 8;
   private static final int SIDE_ROW_H = 24;
   private static final int PROFILES_W = 240;
   private static final int PROFILE_ROW_H = 28;
   private static final int BUTTON_H = 16;
   private static final int FOOTER_H = 12;
   private final Screen parent;
   private final PlaytimeModule module;
   private int panelX;
   private int panelY;
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int panelH;
   private int sideX;
   private int sideY;
   private int profilesX;
   private int profilesY;
   private String dragging;
   private int dragOffX;
   private int dragOffY;
   private static final String BOARD_KEY = "leaderboard";
   private static final String SIDE_KEY = "leaderboard_online";
   private static final String PROFILES_KEY = "leaderboard_profiles";
   private final Scroll scroll = new Scroll();
   private int sideScroll;
   private final Scroll profilesScroll = new Scroll();
   private String profilesMessage = "";
   private String pendingRemove;

   public LeaderboardScreen(Screen parent, PlaytimeModule module) {
      super(Component.literal("Leaderboard"));
      this.parent = parent;
      this.module = module;
   }

   protected void init() {
      Leaderboard.get().refresh(this.module.endpointUrl());
      PublicProfiles.get().refresh(PublicProfiles.endpoint());
   }

   private int statsTop() {
      return this.panelY + 40;
   }

   private int boardLabelY() {
      return this.statsTop() + 42 + 10;
   }

   private int boardTop() {
      return this.boardLabelY() + 14;
   }

   private int visibleRows() {
      return Math.min(8, Math.max(1, Leaderboard.get().entries().size()));
   }

   private int boardBottom() {
      return this.boardTop() + this.visibleRows() * 16;
   }

   private int maxScroll() {
      return Math.max(0, Leaderboard.get().entries().size() * 16 - this.visibleRows() * 16);
   }

   private boolean showSide() {
      return ModuleManager.get("Pip Presence") instanceof PipPresenceModule side && side.isEnabled() && this.width >= 568;
   }

   private boolean showProfiles() {
      return this.width >= 240;
   }

   private int sideX() {
      return this.sideX;
   }

   private int sideTop() {
      return this.sideY + 40 + 4;
   }

   private int sideBottom() {
      return this.sideY + this.panelH - 14;
   }

   private int sideMaxScroll() {
      return Math.max(0, Presence.get().everyone().size() * 24 - (this.sideBottom() - this.sideTop()));
   }

   private int profilesTop() {
      return this.profilesY + 40 + 4;
   }

   private int profilesBottom() {
      return this.profilesY + this.panelH - 14 - 12;
   }

   private int profilesMaxScroll() {
      return Math.max(0, PublicProfiles.get().entries().size() * 28 - (this.profilesBottom() - this.profilesTop()));
   }

   private void computeLayout() {
      this.panelH = this.boardBottom() - this.panelY + 14;
      this.panelH = Math.min(Math.max(this.panelH, 150), Math.max(150, this.height - 24));
      int pair = this.showSide() ? 544 : 320;
      boolean profilesInRow = this.showProfiles() && this.width >= pair + 8 + 240 + 24;
      int total = profilesInRow ? pair + 8 + 240 : pair;
      int defaultX = (this.width - total) / 2;
      int defaultY = Math.max(12, (this.height - this.panelH) / 2);
      this.panelX = PanelState.x("leaderboard", defaultX);
      this.panelY = PanelState.y("leaderboard", defaultY) - this.open.drop(20);
      this.sideX = PanelState.x("leaderboard_online", defaultX + 320 + 8);
      this.sideY = PanelState.y("leaderboard_online", defaultY) - this.open.drop(20);
      int rightEdge = this.showSide() ? this.sideX + 216 : this.panelX + 320;
      int profilesDefaultX = Mth.clamp(rightEdge + 8, 0, Math.max(0, this.width - 240));
      this.profilesX = PanelState.x("leaderboard_profiles", profilesDefaultX);
      this.profilesY = PanelState.y("leaderboard_profiles", defaultY) - this.open.drop(20);
   }

   private String panelUnderGrab(int mx, int my) {
      if (this.showProfiles() && inside(mx, my, this.profilesX, this.profilesY, 240, 40)) {
         return "leaderboard_profiles";
      } else if (this.showSide() && inside(mx, my, this.sideX, this.sideY, 216, 40)) {
         return "leaderboard_online";
      } else {
         return inside(mx, my, this.panelX, this.panelY, 320, 40) ? "leaderboard" : null;
      }
   }

   private int[] refreshBounds() {
      int w = this.font.width("Refresh") + 16;
      return new int[]{this.panelX + 320 - 14 - w, this.panelY + 10, w, 20};
   }

   private int[] settingsBounds() {
      int w = this.font.width("Options") + 16;
      return new int[]{this.refreshBounds()[0] - 6 - w, this.panelY + 10, w, 20};
   }

   private int[] copyBounds(int rowY) {
      int w = this.font.width("Copy") + 12;
      return new int[]{this.profilesX + 240 - 14 - w, rowY + 6, w, 16};
   }

   private int[] removeBounds(int rowY) {
      int[] copy = this.copyBounds(rowY);
      int w = this.font.width("Confirm?") + 10;
      return new int[]{copy[0] - 4 - w, copy[1], w, 16};
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      this.computeLayout();
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      ThemeRenderer.fillRounded(context, this.panelX + 3, this.panelY + 4, 320, this.panelH, Theme.shadow(), 12);
      ThemeRenderer.panel(context, this.panelX, this.panelY, 320, this.panelH, Theme.border(), Theme.panelBg(), 12);
      int backX = this.panelX + 14 - 4;
      boolean backHovered = inside(mouseX, mouseY, backX, this.panelY + 10, 20, 20);
      ThemeRenderer.row(context, backX, this.panelY + 10, 20, 20, backHovered, Theme.trackBg(), Theme.rowBgHover(), 6);
      context.drawString(this.font, "<", backX + 7, this.panelY + 16, ThemeRenderer.rowTextColor(backHovered, Theme.textSecondary(), Theme.textPrimary()));
      context.drawString(this.font, "Leaderboard", backX + 30, this.panelY + 16, Theme.textPrimary());
      this.renderButton(context, this.refreshBounds(), "Refresh", mouseX, mouseY);
      this.renderButton(context, this.settingsBounds(), "Options", mouseX, mouseY);
      this.renderStats(context);
      this.renderBoard(context, mouseX, mouseY);
      if (this.showSide()) {
         this.sideScroll = Mth.clamp(this.sideScroll, 0, this.sideMaxScroll());
         this.renderSide(context);
      }

      if (this.showProfiles()) {
         this.profilesScroll.setTarget(Mth.clamp(this.profilesScroll.target(), 0, this.profilesMaxScroll()));
         this.renderProfiles(context, mouseX, mouseY);
      }
   }

   private void renderButton(GuiGraphics context, int[] b, String label, int mouseX, int mouseY) {
      boolean hovered = inside(mouseX, mouseY, b[0], b[1], b[2], b[3]);
      ThemeRenderer.row(context, b[0], b[1], b[2], b[3], hovered, Theme.trackBg(), Theme.rowBgHover(), 6);
      int lw = this.font.width(label);
      context.drawString(this.font, label, b[0] + (b[2] - lw) / 2, b[1] + 6, ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary()));
   }

   private void renderStats(GuiGraphics context) {
      PlaytimeTracker tracker = PlaytimeTracker.get();
      int x = this.panelX + 14;
      int y = this.statsTop();
      this.statLine(context, x, y, "Total", PlaytimeTracker.format(tracker.totalSeconds()));
      this.statLine(context, x, y + 14, "This session", PlaytimeTracker.format(tracker.sessionSeconds()));
      this.statLine(context, x, y + 28, "Longest session", PlaytimeTracker.format(tracker.longestSessionSeconds()));
   }

   private void statLine(GuiGraphics context, int x, int y, String label, String value) {
      context.drawString(this.font, label, x, y, Theme.textSecondary());
      int vw = this.font.width(value);
      context.drawString(this.font, value, this.panelX + 320 - 14 - vw, y, Theme.textPrimary());
   }

   private void renderBoard(GuiGraphics context, int mouseX, int mouseY) {
      int x = this.panelX + 14;
      int labelY = this.boardLabelY();
      context.drawString(this.font, "LEADERBOARD", x, labelY, Theme.sectionText());
      context.fill(x, labelY + 11, this.panelX + 320 - 14, labelY + 12, Theme.sectionDivider());
      List<Leaderboard.Entry> entries = Leaderboard.get().entries();
      int top = this.boardTop();
      if (entries.isEmpty()) {
         String message;
         if (this.module.endpointUrl().isEmpty()) {
            message = "Not connected - this needs a web host, not a Minecraft server.";
         } else {
            message = Leaderboard.get().status().isEmpty() ? "Nobody on the board yet." : Leaderboard.get().status();
         }

         context.drawString(this.font, this.font.plainSubstrByWidth(message, 292), x, top + 4, Theme.textMuted());
      } else {
         int bottom = this.boardBottom();
         context.enableScissor(x, top, this.panelX + 320 - 14, bottom);

         for (int i = 0; i < entries.size(); i++) {
            Leaderboard.Entry entry = entries.get(i);
            int rowY = top + i * 16 - this.scroll.shown();
            if (rowY + 16 >= top && rowY <= bottom) {
               if (entry.self()) {
                  ThemeRenderer.fillRounded(context, x - 4, rowY, 300, 15, Theme.accentRowBg(), 4);
               }

               int color = entry.self() ? Theme.accent() : Theme.textPrimary();
               String rank = "#" + entry.rank();
               context.drawString(this.font, rank, x, rowY + 4, Theme.textMuted());
               context.drawString(this.font, this.font.plainSubstrByWidth(entry.name(), 150), x + 28, rowY + 4, color);
               String time = PlaytimeTracker.format(entry.seconds());
               int tw = this.font.width(time);
               context.drawString(this.font, time, this.panelX + 320 - 14 - tw, rowY + 4, Theme.textSecondary());
            }
         }

         context.disableScissor();
      }
   }

   private void renderSide(GuiGraphics context) {
      int x = this.sideX();
      ThemeRenderer.fillRounded(context, x + 3, this.sideY + 4, 216, this.panelH, Theme.shadow(), 12);
      ThemeRenderer.panel(context, x, this.sideY, 216, this.panelH, Theme.border(), Theme.panelBg(), 12);
      int textX = x + 14;
      int right = x + 216 - 14;
      List<Presence.Entry> people = Presence.get().everyone();
      context.drawString(this.font, "ON PIP CLIENT", textX, this.sideY + 16, Theme.sectionText());
      String count = String.valueOf(people.size());
      context.drawString(this.font, count, right - this.font.width(count), this.sideY + 16, Theme.textMuted());
      context.fill(textX, this.sideY + 27, right, this.sideY + 28, Theme.sectionDivider());
      int top = this.sideTop();
      int bottom = this.sideBottom();
      if (people.isEmpty()) {
         String status = Presence.get().status();
         String message = status.isEmpty() ? "Nobody else online right now." : status;
         context.drawString(this.font, this.font.plainSubstrByWidth(message, 188), textX, top + 4, Theme.textMuted());
      } else {
         UUID self = Minecraft.getInstance().getUser() == null ? null : Minecraft.getInstance().getUser().getProfileId();
         context.enableScissor(textX, top, right, bottom);

         for (int i = 0; i < people.size(); i++) {
            Presence.Entry person = people.get(i);
            int rowY = top + i * 24 - this.sideScroll;
            if (rowY + 24 >= top && rowY <= bottom) {
               boolean mine = self != null && self.equals(person.id());
               if (mine) {
                  ThemeRenderer.fillRounded(context, textX - 4, rowY, 196, 22, Theme.accentRowBg(), 4);
               }

               context.drawString(this.font, this.font.plainSubstrByWidth(person.name(), 188), textX, rowY + 2, mine ? Theme.accent() : Theme.textPrimary());
               String song = person.song().isEmpty() ? "not sharing" : "♪ " + person.song();
               context.drawString(
                  this.font, this.font.plainSubstrByWidth(song, 188), textX, rowY + 12, person.song().isEmpty() ? Theme.textMuted() : Theme.textSecondary()
               );
            }
         }

         context.disableScissor();
      }
   }

   private void renderProfiles(GuiGraphics context, int mouseX, int mouseY) {
      int x = this.profilesX;
      ThemeRenderer.fillRounded(context, x + 3, this.profilesY + 4, 240, this.panelH, Theme.shadow(), 12);
      ThemeRenderer.panel(context, x, this.profilesY, 240, this.panelH, Theme.border(), Theme.panelBg(), 12);
      int textX = x + 14;
      int right = x + 240 - 14;
      List<PublicProfiles.Entry> entries = PublicProfiles.get().entries();
      String status = PublicProfiles.get().status();
      context.drawString(this.font, "PUBLIC PROFILES", textX, this.profilesY + 16, Theme.sectionText());
      String count = String.valueOf(entries.size());
      context.drawString(this.font, count, right - this.font.width(count), this.profilesY + 16, Theme.textMuted());
      context.fill(textX, this.profilesY + 27, right, this.profilesY + 28, Theme.sectionDivider());
      int top = this.profilesTop();
      int bottom = this.profilesBottom();
      if (entries.isEmpty()) {
         String message = status.isEmpty() ? "Nobody has shared a profile yet." : status;
         context.drawString(this.font, this.font.plainSubstrByWidth(message, 212), textX, top + 4, Theme.textMuted());
      } else {
         boolean overList = mouseY >= top && mouseY < bottom;
         context.enableScissor(x + 1, top, x + 240 - 1, bottom);

         for (int i = 0; i < entries.size(); i++) {
            PublicProfiles.Entry entry = entries.get(i);
            int rowY = top + i * 28 - this.profilesScroll.shown();
            if (rowY + 28 >= top && rowY <= bottom) {
               int[] copy = this.copyBounds(rowY);
               int textRight = (entry.mine() ? this.removeBounds(rowY)[0] : copy[0]) - 4;
               context.drawString(
                  this.font,
                  this.font.plainSubstrByWidth(entry.name(), textRight - textX),
                  textX,
                  rowY + 4,
                  entry.mine() ? Theme.accent() : Theme.textPrimary()
               );
               String by = entry.mine() ? "shared by you" : "by " + (entry.author().isEmpty() ? "someone" : entry.author());
               context.drawString(this.font, this.font.plainSubstrByWidth(by, textRight - textX), textX, rowY + 15, Theme.textMuted());
               boolean copyHovered = overList && inside(mouseX, mouseY, copy[0], copy[1], copy[2], copy[3]);
               ThemeRenderer.fillRounded(context, copy[0], copy[1], copy[2], copy[3], copyHovered ? Theme.accent() : Theme.accentRowBg(), 3);
               context.drawString(this.font, "Copy", copy[0] + 6, copy[1] + 4, copyHovered ? ThemeRenderer.onAccent() : Theme.accent());
               if (entry.mine()) {
                  int[] remove = this.removeBounds(rowY);
                  boolean confirming = key(entry).equals(this.pendingRemove);
                  boolean removeHovered = overList && inside(mouseX, mouseY, remove[0], remove[1], remove[2], remove[3]);
                  int fill = confirming ? Theme.danger() : (removeHovered ? Theme.danger() & 16777215 | 1426063360 : Theme.dangerBg());
                  ThemeRenderer.fillRounded(context, remove[0], remove[1], remove[2], remove[3], fill, 3);
                  String label = confirming ? "Confirm?" : "Remove";
                  context.drawString(
                     this.font,
                     label,
                     remove[0] + (remove[2] - this.font.width(label)) / 2,
                     remove[1] + 4,
                     confirming ? ThemeRenderer.onAccent() : Theme.danger()
                  );
               }
            }
         }

         context.disableScissor();
      }

      String footer = !this.profilesMessage.isEmpty()
         ? this.profilesMessage
         : (!entries.isEmpty() && !status.isEmpty() ? status : "Copy saves it to Profile Presets");
      context.drawString(this.font, this.font.plainSubstrByWidth(footer, 212), textX, this.profilesY + this.panelH - 14 - 9, Theme.textSecondary());
   }

   private static String key(PublicProfiles.Entry entry) {
      return entry.uuid() + "/" + entry.slug();
   }

   private static boolean inside(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)Math.round(click.x());
      int my = (int)Math.round(click.y());
      if (this.showProfiles() && inside(mx, my, this.profilesX, this.profilesY, 240, this.panelH)) {
         if (!this.clickProfiles(mx, my)) {
            this.pendingRemove = null;
            if (my < this.profilesY + 40) {
               this.startDrag("leaderboard_profiles", mx, my);
            }
         }

         return true;
      } else {
         this.pendingRemove = null;
         if (inside(mx, my, this.panelX + 14 - 4, this.panelY + 10, 20, 20)) {
            this.onClose();
            return true;
         } else {
            int[] refresh = this.refreshBounds();
            if (inside(mx, my, refresh[0], refresh[1], refresh[2], refresh[3])) {
               Leaderboard.get().refresh(this.module.endpointUrl());
               this.profilesMessage = "";
               PublicProfiles.get().refresh(PublicProfiles.endpoint());
               return true;
            } else {
               int[] settings = this.settingsBounds();
               if (inside(mx, my, settings[0], settings[1], settings[2], settings[3])) {
                  Minecraft.getInstance().setScreen(new ModuleSettingsScreen(this, this.module));
                  return true;
               } else {
                  String grabbed = this.panelUnderGrab(mx, my);
                  if (grabbed != null) {
                     this.startDrag(grabbed, mx, my);
                     return true;
                  } else {
                     return super.mouseClicked(click, doubled);
                  }
               }
            }
         }
      }
   }

   private void startDrag(String key, int mx, int my) {
      this.dragging = key;
      this.dragOffX = mx - (key.equals("leaderboard_profiles") ? this.profilesX : (key.equals("leaderboard_online") ? this.sideX : this.panelX));
      this.dragOffY = my - (key.equals("leaderboard_profiles") ? this.profilesY : (key.equals("leaderboard_online") ? this.sideY : this.panelY));
   }

   private boolean clickProfiles(int mx, int my) {
      int top = this.profilesTop();
      if (my >= top && my < this.profilesBottom()) {
         List<PublicProfiles.Entry> entries = PublicProfiles.get().entries();
         int index = (my - top + this.profilesScroll.shown()) / 28;
         if (index >= 0 && index < entries.size()) {
            PublicProfiles.Entry entry = entries.get(index);
            int rowY = top - this.profilesScroll.shown() + index * 28;
            int[] copy = this.copyBounds(rowY);
            if (inside(mx, my, copy[0], copy[1], copy[2], copy[3])) {
               this.pendingRemove = null;
               this.profilesMessage = "";
               PublicProfilesScreen.copyToPresets(entry, text -> this.profilesMessage = text);
               return true;
            }

            int[] remove = this.removeBounds(rowY);
            if (entry.mine() && inside(mx, my, remove[0], remove[1], remove[2], remove[3])) {
               if (key(entry).equals(this.pendingRemove)) {
                  this.pendingRemove = null;
                  this.profilesMessage = "";
                  PublicProfiles.get().unpublish(PublicProfiles.endpoint(), entry.uuid(), entry.slug(), () -> ProfileStorage.clearPublishedSlug(entry.slug()));
               } else {
                  this.pendingRemove = key(entry);
                  this.profilesMessage = "Click Confirm? to take it down";
               }

               return true;
            }
         }
      }

      return false;
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      if (this.dragging == null) {
         return super.mouseDragged(click, offsetX, offsetY);
      } else {
         int width = this.dragging.equals("leaderboard_profiles") ? 240 : (this.dragging.equals("leaderboard_online") ? 216 : 320);
         int x = Mth.clamp((int)click.x() - this.dragOffX, 0, Math.max(0, this.width - width));
         int y = Mth.clamp((int)click.y() - this.dragOffY, 0, Math.max(0, this.height - 40));
         PanelState.set(this.dragging, x, y);
         return true;
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.dragging == null) {
         return super.mouseReleased(click);
      } else {
         this.dragging = null;
         Config.save();
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.computeLayout();
      int mx = (int)mouseX;
      int my = (int)mouseY;
      if (this.showProfiles() && inside(mx, my, this.profilesX, this.profilesY, 240, this.panelH)) {
         this.profilesScroll.setTarget(Mth.clamp(this.profilesScroll.target() - (int)(verticalAmount * 28.0), 0, this.profilesMaxScroll()));
      } else if (this.showSide() && inside(mx, my, this.sideX, this.sideY, 216, this.panelH)) {
         this.sideScroll = Mth.clamp(this.sideScroll - (int)(verticalAmount * 24.0), 0, this.sideMaxScroll());
      } else {
         this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 16.0), 0, this.maxScroll()));
      }

      return true;
   }

   public boolean keyPressed(KeyEvent input) {
      if (input.key() == 256) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   public void onClose() {
      Config.save();
      Minecraft.getInstance().setScreen(this.parent);
   }
}
