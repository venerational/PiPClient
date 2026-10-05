package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.audio.MediaWatcher;
import dev.lyfw.lyfwclient.audio.MusicPlayer;
import dev.lyfw.lyfwclient.module.modules.SongPlayerModule;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SongPlayerScreen extends Screen {
   private static final int PANEL_W = 340;
   private static final int PAD = 16;
   private static final int HEADER_H = 40;
   private static final int ART = 54;
   private static final int ROW_H = 19;
   private static final int SEEK_H = 4;
   private static final int BTN = 24;
   private static final int ICON_BTN = 20;
   private static final int MAX_LIST_ROWS = 5;
   private static final float EASE_RATE = 14.0F;
   private final Screen parent;
   private final SongPlayerModule module;
   private int panelX;
   private int panelY;
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int panelH;
   private final Scroll scroll = new Scroll();
   private float easedSeek;
   private float easedVolume = -1.0F;
   private boolean draggingSeek;
   private boolean draggingVolume;
   private long lastFrameAt = System.nanoTime();

   public SongPlayerScreen(Screen parent, SongPlayerModule module) {
      super(Component.literal("Song Player"));
      this.parent = parent;
      this.module = module;
   }

   protected void init() {
      this.module.syncLibrary();
      if (this.easedVolume < 0.0F) {
         this.easedVolume = MusicPlayer.get().volume();
      }
   }

   private boolean localMode() {
      return this.module.activeSource() == SongPlayerModule.Source.LOCAL;
   }

   private int cardTop() {
      return this.panelY + 40;
   }

   private int seekY() {
      return this.cardTop() + 54 + 14;
   }

   private int transportY() {
      return this.seekY() + 4 + 14;
   }

   private int listLabelY() {
      return this.transportY() + 24 + 14;
   }

   private int listTop() {
      return this.listLabelY() + 14;
   }

   private int listRows() {
      return Math.min(5, MusicPlayer.get().playlist().size());
   }

   private int listBottom() {
      return this.listTop() + this.listRows() * 19;
   }

   private int listW() {
      return 302;
   }

   private int maxScroll() {
      return Math.max(0, MusicPlayer.get().playlist().size() * 19 - this.listRows() * 19);
   }

   private void computeLayout() {
      int bottom;
      if (this.localMode() && !MusicPlayer.get().playlist().isEmpty()) {
         bottom = this.listBottom();
      } else {
         bottom = this.transportY() + 24;
      }

      this.panelH = bottom - this.panelY + 16;
      this.panelH = Math.min(this.panelH, Math.max(150, this.height - 24));
      this.panelX = (this.width - 340) / 2;
      this.panelY = Math.max(12, (this.height - this.panelH) / 2) - this.open.drop(20);
   }

   private static float ease(float current, float target, float dt) {
      return current + (target - current) * (1.0F - (float)Math.exp(-14.0F * dt));
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      long now = System.nanoTime();
      float dt = Math.min(0.1F, (float)(now - this.lastFrameAt) / 1.0E9F);
      this.lastFrameAt = now;
      this.computeLayout();
      this.computeLayout();
      MusicPlayer player = MusicPlayer.get();
      ThemeRenderer.panel(context, this.panelX, this.panelY, 340, this.panelH, Theme.border(), Theme.panelBg(), 12);
      this.renderHeader(context, mouseX, mouseY);
      this.renderCard(context, mouseX, mouseY);
      this.renderSeek(context, player, mouseX, dt);
      this.renderTransport(context, player, mouseX, mouseY, dt);
      if (this.localMode()) {
         this.renderPlaylist(context, player, mouseX, mouseY);
      }
   }

   private void renderHeader(GuiGraphics context, int mouseX, int mouseY) {
      int backX = this.panelX + 16 - 4;
      int backY = this.panelY + 10;
      boolean backHovered = inside(mouseX, mouseY, backX, backY, 20, 20);
      ThemeRenderer.row(context, backX, backY, 20, 20, backHovered, Theme.trackBg(), Theme.rowBgHover(), 6);
      context.drawString(this.font, "<", backX + 7, backY + 6, ThemeRenderer.rowTextColor(backHovered, Theme.textSecondary(), Theme.textPrimary()));
      context.drawString(this.font, "Song Player", backX + 20 + 10, this.panelY + 16, Theme.textPrimary());
      String source = this.module.sourceLabel();
      int pillW = this.font.width(source) + 18;
      int[] pill = new int[]{this.iconButtonX(1) - 8 - pillW, this.panelY + 10, pillW, 20};
      ThemeRenderer.fillRounded(context, pill[0], pill[1], pill[2], pill[3], Theme.trackBg(), 6);
      ThemeRenderer.fillRounded(context, pill[0] + 7, pill[1] + 8, 5, 5, Theme.accent(), 2);
      context.drawString(this.font, source, pill[0] + 16, pill[1] + 6, Theme.textSecondary());
      this.renderIconButton(context, this.iconButtonX(1), "move", mouseX, mouseY);
      this.renderIconButton(context, this.iconButtonX(0), "sliders", mouseX, mouseY);
   }

   private int iconButtonX(int indexFromRight) {
      return this.panelX + 340 - 16 + 4 - (indexFromRight + 1) * 24;
   }

   private void renderIconButton(GuiGraphics context, int x, String glyph, int mouseX, int mouseY) {
      int y = this.panelY + 10;
      boolean hovered = inside(mouseX, mouseY, x, y, 20, 20);
      ThemeRenderer.row(context, x, y, 20, 20, hovered, Theme.trackBg(), Theme.rowBgHover(), 6);
      AuroraIcons.draw(context, glyph, x + 2, y + 2, 16, ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary()));
   }

   private void renderCard(GuiGraphics context, int mouseX, int mouseY) {
      int x = this.panelX + 16;
      int y = this.cardTop();
      ThemeRenderer.fillRounded(context, x, y, 54, 54, Theme.trackBg(), 10);
      AuroraIcons.draw(context, "note", x + 18, y + 18, 18, Theme.accent());
      int textX = x + 54 + 12;
      int textW = 324 - textX + this.panelX;
      String title = this.module.nowPlayingTitle();
      String artist = this.module.nowPlayingArtist();
      context.drawString(this.font, this.font.plainSubstrByWidth(title, textW), textX, y + 2, Theme.textPrimary());
      String second = artist.isEmpty() ? this.secondLine() : artist;
      String note = !this.module.waveIsLive() && MediaWatcher.get().isPlaying() ? "not synced" : "";
      int noteW = note.isEmpty() ? 0 : this.font.width(note) + 8;
      context.drawString(this.font, this.font.plainSubstrByWidth(second, textW - noteW), textX, y + 14, Theme.textSecondary());
      if (!note.isEmpty()) {
         context.drawString(this.font, note, textX + textW - this.font.width(note), y + 14, Theme.textMuted());
      }

      this.module.renderWaveInto(context, textX, y + 28, textW, 26);
   }

   private String secondLine() {
      MusicPlayer player = MusicPlayer.get();
      if (!this.localMode()) {
         return MediaWatcher.get().display();
      } else {
         String trouble = MediaWatcher.get().problem();
         if (!trouble.isEmpty() && player.clip() == null) {
            return trouble;
         } else if (!player.status().isEmpty()) {
            return player.status();
         } else {
            return player.clip() == null ? "Nothing playing" : (player.isPlaying() ? "Playing" : "Paused");
         }
      }
   }

   private void renderSeek(GuiGraphics context, MusicPlayer player, int mouseX, float dt) {
      int x = this.panelX + 16;
      int y = this.seekY();
      int timeW = this.font.width("00:00 · 00:00") + 8;
      int w = 308 - timeW;
      double duration = this.module.duration();
      double position = this.module.position();
      float target = duration <= 0.0 ? 0.0F : (float)Math.min(1.0, position / duration);
      if (this.draggingSeek) {
         target = Mth.clamp((float)(mouseX - x) / w, 0.0F, 1.0F);
      }

      this.easedSeek = ease(this.easedSeek, target, dt);
      ThemeRenderer.fillRounded(context, x, y, w, 4, Theme.trackBg(), 2);
      float exact = w * this.easedSeek;
      int done = (int)exact;
      float part = exact - done;
      if (done > 0) {
         ThemeRenderer.fillRounded(context, x, y, done, 4, Theme.accent(), 2);
      }

      if (part > 0.02F && done < w) {
         int accent = Theme.accent();
         context.fill(x + done, y, x + done + 1, y + 4, Math.round((accent >>> 24) * part) << 24 | accent & 16777215);
      }

      if (this.localMode()) {
         context.pose().pushMatrix();
         context.pose().translate(part, 0.0F);
         ThemeRenderer.fillRounded(context, x + done - 3, y - 2, 6, 8, Theme.knob(), 3);
         context.pose().popMatrix();
      }

      String clock = SongPlayerModule.formatTime(position) + " · " + SongPlayerModule.formatTime(duration);
      context.drawString(this.font, clock, this.panelX + 340 - 16 - this.font.width(clock), y - 2, Theme.textSecondary());
   }

   private void renderTransport(GuiGraphics context, MusicPlayer player, int mouseX, int mouseY, float dt) {
      int y = this.transportY();
      String[] labels = new String[]{"|<", this.module.isPlaying() ? "||" : ">", ">|"};
      int startX = this.panelX + 16;

      for (int i = 0; i < 3; i++) {
         int bx = startX + i * 29;
         boolean hovered = inside(mouseX, mouseY, bx, y, 24, 24);
         boolean primary = i == 1;
         if (primary) {
            ThemeRenderer.activeRow(context, bx, y, 24, 24, Theme.accent(), 7);
         } else {
            ThemeRenderer.row(context, bx, y, 24, 24, hovered, Theme.trackBg(), Theme.rowBgHover(), 7);
         }

         int lw = this.font.width(labels[i]);
         int color = primary ? ThemeRenderer.onAccent() : ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary());
         context.drawString(this.font, labels[i], bx + (24 - lw) / 2, y + 8, color, !primary);
      }

      if (!this.localMode()) {
         String note = "controlling " + this.module.sourceLabel();
         context.drawString(this.font, note, startX + 87 + 8, y + 8, Theme.textMuted());
      } else {
         int[] shuffle = this.toggleBounds(0);
         this.renderToggleChip(context, shuffle, "Shuffle", player.shuffle(), mouseX, mouseY);
         int[] repeat = this.toggleBounds(1);
         this.renderToggleChip(context, repeat, "Repeat", player.repeat(), mouseX, mouseY);
         int[] vol = this.volumeBounds();
         float targetVolume = this.draggingVolume ? Mth.clamp((float)(mouseX - vol[0]) / vol[2], 0.0F, 1.0F) : player.volume();
         this.easedVolume = ease(this.easedVolume, targetVolume, dt);
         ThemeRenderer.fillRounded(context, vol[0], vol[1], vol[2], 3, Theme.trackBg(), 1);
         int filled = Math.round(vol[2] * this.easedVolume);
         if (filled > 0) {
            ThemeRenderer.fillRounded(context, vol[0], vol[1], filled, 3, Theme.accent(), 1);
         }
      }
   }

   private int[] toggleBounds(int index) {
      String label = index == 0 ? "Shuffle" : "Repeat";
      int w = this.font.width(label) + 12;
      int x = this.panelX + 16 + 87 + 8 + (index == 0 ? 0 : this.font.width("Shuffle") + 12 + 4);
      return new int[]{x, this.transportY() + 4, w, 16};
   }

   private int[] volumeBounds() {
      int w = 54;
      return new int[]{this.panelX + 340 - 16 - w, this.transportY() + 12 - 1, w, 3};
   }

   private void renderToggleChip(GuiGraphics context, int[] b, String label, boolean on, int mouseX, int mouseY) {
      boolean hovered = inside(mouseX, mouseY, b[0], b[1], b[2], b[3]);
      ThemeRenderer.tab(context, b[0], b[1], b[2], b[3], on, hovered, Theme.accent(), Theme.trackBg(), Theme.rowBgHover(), 5);
      int lw = this.font.width(label);
      int color = on ? ThemeRenderer.onAccent() : ThemeRenderer.rowTextColor(hovered, Theme.textSecondary(), Theme.textPrimary());
      context.drawString(this.font, label, b[0] + (b[2] - lw) / 2, b[1] + 4, color, !on);
   }

   private void renderPlaylist(GuiGraphics context, MusicPlayer player, int mouseX, int mouseY) {
      List<Path> tracks = player.playlist();
      int x = this.panelX + 16;
      if (tracks.isEmpty()) {
         context.drawString(
            this.font,
            this.font.plainSubstrByWidth("No music yet - put .ogg or .wav files in your music folder.", 308),
            x,
            this.transportY() + 24 + 10,
            Theme.textMuted()
         );
      } else {
         int labelY = this.listLabelY();
         context.drawString(this.font, "LIBRARY", x, labelY, Theme.sectionText());
         int[] rescan = this.rescanBounds();
         boolean rescanHovered = inside(mouseX, mouseY, rescan[0], rescan[1], rescan[2], rescan[3]);
         AuroraIcons.draw(context, "refresh", rescan[0], rescan[1], 14, rescanHovered ? Theme.textPrimary() : Theme.textMuted());
         context.fill(x, labelY + 11, this.panelX + 340 - 16, labelY + 12, Theme.sectionDivider());
         int top = this.listTop();
         int bottom = this.listBottom();
         context.enableScissor(x, top, x + this.listW() + 6, bottom);

         for (int i = 0; i < tracks.size(); i++) {
            int rowY = top + i * 19 - this.scroll.shown();
            if (rowY + 19 >= top && rowY <= bottom) {
               boolean current = i == player.trackIndex();
               boolean hovered = inside(mouseX, mouseY, x, rowY, this.listW(), 19) && mouseY >= top && mouseY < bottom;
               if (hovered) {
                  ThemeRenderer.fillRounded(context, x - 4, rowY, this.listW() + 8, 18, Theme.rowBgHover(), 4);
               }

               String name = tracks.get(i).getFileName().toString();
               int dot = name.lastIndexOf(46);
               if (dot > 0) {
                  name = name.substring(0, dot);
               }

               if (current) {
                  ThemeRenderer.fillRounded(context, x - 4, rowY + 5, 2, 8, Theme.accent(), 1);
               }

               int color = current ? Theme.accent() : (hovered ? Theme.textPrimary() : Theme.textSecondary());
               context.drawString(this.font, this.font.plainSubstrByWidth(name, this.listW() - 14), x + 4, rowY + 5, color);
            }
         }

         context.disableScissor();
         int maxScroll = this.maxScroll();
         if (maxScroll > 0) {
            int trackX = x + this.listW() + 2;
            int trackH = bottom - top;
            ThemeRenderer.fillRounded(context, trackX, top, 2, trackH, Theme.trackBg(), 1);
            int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
            int handleY = top + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
            ThemeRenderer.fillRounded(context, trackX, handleY, 2, handleH, Theme.accent(), 1);
         }
      }
   }

   private int[] rescanBounds() {
      return new int[]{this.panelX + 340 - 16 - 14, this.listLabelY() - 2, 14, 14};
   }

   private static boolean inside(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)Math.round(click.x());
      int my = (int)Math.round(click.y());
      MusicPlayer player = MusicPlayer.get();
      if (inside(mx, my, this.panelX + 16 - 4, this.panelY + 10, 20, 20)) {
         this.onClose();
         return true;
      } else if (inside(mx, my, this.iconButtonX(1), this.panelY + 10, 20, 20)) {
         Config.save();
         Minecraft.getInstance().setScreen(new GuiMoverScreen(this, this.module));
         return true;
      } else if (inside(mx, my, this.iconButtonX(0), this.panelY + 10, 20, 20)) {
         Minecraft.getInstance().setScreen(new ModuleSettingsScreen(this, this.module));
         return true;
      } else {
         int ty = this.transportY();
         int transportX = this.panelX + 16;

         for (int i = 0; i < 3; i++) {
            if (inside(mx, my, transportX + i * 29, ty, 24, 24)) {
               this.transport(i);
               return true;
            }
         }

         if (!this.localMode()) {
            return super.mouseClicked(click, doubled);
         } else {
            int[] rescan = this.rescanBounds();
            if (inside(mx, my, rescan[0] - 3, rescan[1] - 3, 20, 20)) {
               this.module.rescan();
               return true;
            } else {
               int seekY = this.seekY();
               int seekW = 308 - this.font.width("00:00 · 00:00") - 8;
               if (my >= seekY - 5 && my < seekY + 4 + 5 && mx >= this.panelX + 16 && mx <= this.panelX + 16 + seekW) {
                  this.draggingSeek = true;
                  this.seekFromMouse(mx, player);
                  return true;
               } else {
                  int[] vol = this.volumeBounds();
                  if (inside(mx, my, vol[0], vol[1] - 7, vol[2], 17)) {
                     this.draggingVolume = true;
                     player.setVolume(Mth.clamp((float)(mx - vol[0]) / vol[2], 0.0F, 1.0F));
                     return true;
                  } else {
                     int[] shuffle = this.toggleBounds(0);
                     if (inside(mx, my, shuffle[0], shuffle[1], shuffle[2], shuffle[3])) {
                        player.setShuffle(!player.shuffle());
                        return true;
                     } else {
                        int[] repeat = this.toggleBounds(1);
                        if (inside(mx, my, repeat[0], repeat[1], repeat[2], repeat[3])) {
                           player.setRepeat(!player.repeat());
                           return true;
                        } else {
                           if (my >= this.listTop() && my < this.listBottom() && mx >= this.panelX + 16 - 4 && mx < this.panelX + 16 + this.listW()) {
                              int index = (my - this.listTop() + this.scroll.shown()) / 19;
                              if (index >= 0 && index < player.playlist().size()) {
                                 player.playIndex(index);
                                 return true;
                              }
                           }

                           return super.mouseClicked(click, doubled);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void transport(int button) {
      if (this.localMode()) {
         MusicPlayer player = MusicPlayer.get();
         switch (button) {
            case 0:
               player.previous();
               break;
            case 1:
               player.toggle();
               break;
            default:
               player.next();
         }
      } else {
         MediaWatcher.get().control(switch (button) {
            case 0 -> "previous";
            case 1 -> "toggle";
            default -> "next";
         });
      }
   }

   private void seekFromMouse(int mx, MusicPlayer player) {
      int x = this.panelX + 16;
      int w = 308 - this.font.width("00:00 · 00:00") - 8;
      player.seekTo(Mth.clamp((float)(mx - x) / w, 0.0F, 1.0F) * player.durationSeconds());
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      int mx = (int)Math.round(click.x());
      if (this.draggingSeek) {
         this.seekFromMouse(mx, MusicPlayer.get());
         return true;
      } else if (this.draggingVolume) {
         int[] vol = this.volumeBounds();
         MusicPlayer.get().setVolume(Mth.clamp((float)(mx - vol[0]) / vol[2], 0.0F, 1.0F));
         return true;
      } else {
         return super.mouseDragged(click, offsetX, offsetY);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (!this.draggingSeek && !this.draggingVolume) {
         return super.mouseReleased(click);
      } else {
         this.draggingSeek = false;
         this.draggingVolume = false;
         Config.save();
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 19.0), 0, this.maxScroll()));
      return true;
   }

   public boolean keyPressed(KeyEvent input) {
      int key = input.key();
      if (key == 256) {
         this.onClose();
         return true;
      } else if (!this.localMode()) {
         return super.keyPressed(input);
      } else if (key == 32) {
         MusicPlayer.get().toggle();
         return true;
      } else if (key == 262) {
         MusicPlayer.get().next();
         return true;
      } else if (key == 263) {
         MusicPlayer.get().previous();
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
