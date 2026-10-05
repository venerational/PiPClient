package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.audio.AudioClip;
import dev.lyfw.lyfwclient.audio.MediaWatcher;
import dev.lyfw.lyfwclient.audio.MusicPlayer;
import dev.lyfw.lyfwclient.audio.Spectrum;
import dev.lyfw.lyfwclient.gui.SongPlayerScreen;
import dev.lyfw.lyfwclient.gui.Theme;
import dev.lyfw.lyfwclient.gui.ThemeRenderer;
import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.render.AlbumArt;
import dev.lyfw.lyfwclient.render.PanelBackground;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class SongPlayerModule extends HudModule {
   private static final double LOOKAHEAD_SECONDS = 0.09;
   private static final int PAD = 7;
   private static final int ART = 40;
   private final EnumSetting<SongPlayerModule.Source> source = this.register(new EnumSetting<>("Source", SongPlayerModule.Source.AUTO));
   private final TextSetting folder = this.register(new TextSetting("Music Folder", "music"));
   private final BooleanSetting autoPlay = this.register(new BooleanSetting("Play On Enable", true));
   private final BooleanSetting shareSong = this.register(new BooleanSetting("Show My Song To Others", true));
   private final BooleanSetting sharePresence = this.register(new BooleanSetting("Appear To Other Pip Users", true));
   private final BooleanSetting showOthersSongs = this.register(new BooleanSetting("Show Others' Songs", true));
   private final ColorSetting othersSongColor = this.register(new ColorSetting("Others' Song Color", -6710887));
   private final EnumSetting<PanelBackground> background = this.register(new EnumSetting<>("Background", PanelBackground.GRADIENT));
   private final SliderSetting panelWidth = this.register(new SliderSetting("Panel Width", 220.0, 140.0, 400.0, 2.0, "px"));
   private final BooleanSetting showArt = this.register(new BooleanSetting("Show Art", true));
   private final BooleanSetting albumArt = this.register(new BooleanSetting("Album Art", true));
   private final BooleanSetting showProgress = this.register(new BooleanSetting("Show Progress", true));
   private final BooleanSetting showWave = this.register(new BooleanSetting("Show Wave", true));
   private final EnumSetting<SongPlayerModule.WaveStyle> style = this.register(new EnumSetting<>("Wave Style", SongPlayerModule.WaveStyle.BARS));
   private final SliderSetting waveHeight = this.register(new SliderSetting("Wave Height", 14.0, 6.0, 60.0, 1.0, "px"));
   private final SliderSetting barCount = this.register(new SliderSetting("Bars", 34.0, 6.0, 64.0, 1.0, ""));
   private final SliderSetting smoothing = this.register(new SliderSetting("Smoothing", 62.0, 0.0, 95.0, 1.0, "%"));
   private final SliderSetting sensitivity = this.register(new SliderSetting("Sensitivity", 100.0, 20.0, 400.0, 5.0, "%"));
   private final BooleanSetting beatPulse = this.register(new BooleanSetting("Beat Pulse", true));
   private final SliderSetting bpm = this.register(new SliderSetting("Tempo", 0.0, 0.0, 200.0, 1.0, " BPM"));
   private final ColorSetting waveColor = this.register(new ColorSetting("Wave Color", -8497665));
   private final ColorSetting waveColor2 = this.register(new ColorSetting("Wave Peak Color", -1));
   private final ColorSetting textColor = this.register(new ColorSetting("Text Color", -1));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", -1878982656));
   private Spectrum spectrum;
   private float[] decorLevels;
   private String loadedFolder;
   private long lastAnalysisAt;

   public SongPlayerModule() {
      super(
         "Song Player", "A now-playing panel for Spotify or your own files, with an animated background and a wave that moves with the music.", false, 4.0, 4.0
      );
      this.shareSong.group = "Source";
      this.sharePresence.group = "Source";
      this.showOthersSongs.group = "Source";
      this.othersSongColor.group = "Source";
      this.source.group = "Source";
      this.folder.group = "Source";
      this.autoPlay.group = "Source";
      this.background.group = "Display";
      this.panelWidth.group = "Display";
      this.showArt.group = "Display";
      this.albumArt.group = "Display";
      this.showProgress.group = "Display";
      this.showWave.group = "Display";
      this.style.group = "Wave";
      this.waveHeight.group = "Wave";
      this.barCount.group = "Wave";
      this.smoothing.group = "Wave";
      this.sensitivity.group = "Wave";
      this.beatPulse.group = "Wave";
      this.bpm.group = "Wave";
      this.waveColor.group = "Colors";
      this.waveColor2.group = "Colors";
      this.textColor.group = "Colors";
      this.backgroundColor.group = "Colors";
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return new SongPlayerScreen(parent, this);
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (enabled) {
         this.syncLibrary();
         this.updateSystemWatch();
         if (this.autoPlay.get()
            && this.source.get() == SongPlayerModule.Source.LOCAL
            && !MusicPlayer.get().isPlaying()
            && !MusicPlayer.get().playlist().isEmpty()) {
            MusicPlayer.get().playIndex(Math.max(0, MusicPlayer.get().trackIndex()));
         }
      } else {
         MusicPlayer.get().stop();
         MediaWatcher.get().stop();
      }
   }

   public Path musicFolder() {
      String raw = this.folder.get() == null ? "" : this.folder.get().trim();
      if (raw.isEmpty()) {
         return null;
      } else {
         Path path = Path.of(raw);
         return path.isAbsolute() ? path : FabricLoader.getInstance().getConfigDir().resolve(raw);
      }
   }

   public void syncLibrary() {
      String raw = this.folder.get() == null ? "" : this.folder.get().trim();
      if (!raw.equals(this.loadedFolder)) {
         this.loadedFolder = raw;
         MusicPlayer.get().loadFolder(this.musicFolder());
      }
   }

   public void rescan() {
      this.loadedFolder = null;
      this.syncLibrary();
   }

   public int bars() {
      return this.barCount.getInt();
   }

   public SongPlayerModule.Source activeSource() {
      SongPlayerModule.Source chosen = this.source.get();
      if (chosen == SongPlayerModule.Source.SYSTEM) {
         return SongPlayerModule.Source.SYSTEM;
      } else {
         return chosen == SongPlayerModule.Source.LOCAL
            ? SongPlayerModule.Source.LOCAL
            : (MediaWatcher.get().available() ? SongPlayerModule.Source.SYSTEM : SongPlayerModule.Source.LOCAL);
      }
   }

   public boolean shareToOthers() {
      return this.shareSong.get();
   }

   public boolean appearToOthers() {
      return this.sharePresence.get();
   }

   public boolean showOthersSongs() {
      return this.showOthersSongs.get();
   }

   public int othersSongColor() {
      return this.othersSongColor.get();
   }

   public boolean waveIsLive() {
      return this.activeSource() == SongPlayerModule.Source.LOCAL;
   }

   public String nowPlayingTitle() {
      if (this.activeSource() != SongPlayerModule.Source.SYSTEM) {
         MusicPlayer player = MusicPlayer.get();
         String trouble = this.source.get() == SongPlayerModule.Source.LOCAL ? "" : MediaWatcher.get().problem();
         if (!trouble.isEmpty() && player.clip() == null) {
            return trouble;
         } else {
            return player.status().isEmpty() ? player.currentTitle() : player.status();
         }
      } else {
         MediaWatcher media = MediaWatcher.get();
         return media.available() && !media.title().isEmpty() ? media.title() : "Nothing playing";
      }
   }

   public String nowPlayingArtist() {
      if (this.activeSource() == SongPlayerModule.Source.SYSTEM) {
         MediaWatcher media = MediaWatcher.get();
         return media.artist().isEmpty() ? media.display() : media.artist();
      } else {
         MusicPlayer player = MusicPlayer.get();
         return player.clip() == null ? "" : (player.isPlaying() ? "Playing" : "Paused");
      }
   }

   public String sourceLabel() {
      return this.activeSource() == SongPlayerModule.Source.SYSTEM ? MediaWatcher.get().appName() : "Local";
   }

   public double position() {
      return this.activeSource() == SongPlayerModule.Source.SYSTEM ? MediaWatcher.get().position() : MusicPlayer.get().positionSeconds();
   }

   public double duration() {
      return this.activeSource() == SongPlayerModule.Source.SYSTEM ? MediaWatcher.get().duration() : MusicPlayer.get().durationSeconds();
   }

   public boolean isPlaying() {
      return this.activeSource() == SongPlayerModule.Source.SYSTEM ? MediaWatcher.get().isPlaying() : MusicPlayer.get().isPlaying();
   }

   public void updateSystemWatch() {
      if (this.source.get() == SongPlayerModule.Source.LOCAL) {
         MediaWatcher.get().stop();
      } else {
         MediaWatcher.get().start();
      }
   }

   @Override
   protected int contentWidth() {
      return this.panelWidth.getInt();
   }

   @Override
   protected int contentHeight() {
      int textBlock = 20 + (this.showProgress.get() ? 12 : 0) + (this.showWave.get() ? this.waveHeight.getInt() + 4 : 0);
      int body = this.showArt.get() ? Math.max(40, textBlock) : textBlock;
      return body + 14;
   }

   public float beat() {
      if (!this.beatPulse.get()) {
         return 0.0F;
      } else if (this.waveIsLive()) {
         return this.spectrum == null ? 0.0F : this.spectrum.beatPulse();
      } else {
         int tempo = this.bpm.getInt();
         if (tempo > 0 && this.isPlaying()) {
            double beats = this.position() * tempo / 60.0;
            float phase = (float)(beats - Math.floor(beats));
            return Math.max(0.0F, 1.0F - phase * 3.0F);
         } else {
            return 0.0F;
         }
      }
   }

   private void updateSpectrum() {
      long now = System.nanoTime();
      if (now - this.lastAnalysisAt >= 4000000L) {
         this.lastAnalysisAt = now;
         int wanted = this.bars();
         if (this.spectrum == null || this.spectrum.bands() != wanted) {
            this.spectrum = new Spectrum(wanted);
         }

         if (this.decorLevels == null || this.decorLevels.length != wanted) {
            this.decorLevels = new float[wanted];
         }

         if (this.activeSource() == SongPlayerModule.Source.SYSTEM) {
            this.updateAnimatedWave(wanted);
         } else {
            MusicPlayer player = MusicPlayer.get();
            AudioClip clip = player.clip();
            if (clip != null && player.isPlaying()) {
               int frame = player.playHead() + (int)(0.09 * clip.sampleRate());
               this.spectrum.update(clip, frame, (float)(this.smoothing.get() / 100.0), (float)(this.sensitivity.get() / 100.0));
            } else {
               this.spectrum.decay();
            }
         }
      }
   }

   private void updateAnimatedWave(int bands) {
      boolean playing = this.isPlaying();
      float t = (float)this.position();
      float beat = this.beat();

      for (int i = 0; i < bands; i++) {
         float phase = (float)i / bands;
         float wave = (float)(Math.sin(t * 1.9 + phase * 8.0) * 0.32 + Math.sin(t * 3.1 - phase * 13.0) * 0.2 + Math.sin(t * 0.7 + phase * 3.0) * 0.16);
         float arch = (float)Math.sin(phase * Math.PI) * 0.38F + 0.22F;
         float target = playing ? Math.max(0.02F, (arch + wave * 0.5F) * (1.0F + beat * 0.45F)) : 0.0F;
         this.decorLevels[i] = this.decorLevels[i] * 0.8F + target * 0.2F;
      }
   }

   private float levelAt(int band) {
      if (this.activeSource() == SongPlayerModule.Source.SYSTEM) {
         return this.decorLevels == null ? 0.0F : this.decorLevels[Math.floorMod(band, this.decorLevels.length)];
      } else {
         return this.spectrum == null ? 0.0F : this.spectrum.level(band);
      }
   }

   @Override
   public void render(GuiGraphics context) {
      this.updateSpectrum();
      this.updateSystemWatch();
      Minecraft mc = Minecraft.getInstance();
      int w = this.contentWidth();
      int h = this.contentHeight();
      float beat = this.beat();
      this.background.get().draw(context, 0, 0, w, h, this.backgroundColor.get(), this.waveColor.get(), this.position(), beat);
      int x = 7;
      if (this.showArt.get()) {
         this.drawArt(context, x, 7, 40, beat);
         x += 48;
      }

      int textW = w - 7 - x;
      int y = 7;
      String title = this.nowPlayingTitle();
      context.drawString(mc.font, mc.font.plainSubstrByWidth(title, textW), x, y, this.textColor.get());
      y += 10;
      String artist = this.nowPlayingArtist();
      if (!artist.isEmpty()) {
         context.drawString(mc.font, mc.font.plainSubstrByWidth("by " + artist, textW), x, y, Theme.textSecondary());
      }

      y += 10;
      if (this.showWave.get()) {
         this.renderWave(context, x, y, textW, this.waveHeight.getInt());
         y += this.waveHeight.getInt() + 4;
      }

      if (this.showProgress.get()) {
         this.drawProgress(context, mc, x, y, textW);
      }
   }

   private void drawArt(GuiGraphics context, int x, int y, int size, float beat) {
      if (this.albumArt.get() && this.activeSource() == SongPlayerModule.Source.SYSTEM) {
         Identifier cover = AlbumArt.get(MediaWatcher.get().artist(), MediaWatcher.get().album(), MediaWatcher.get().title());
         if (cover != null) {
            context.blit(RenderPipelines.GUI_TEXTURED, cover, x, y, 0.0F, 0.0F, size, size, size, size);
            if (beat > 0.05F) {
               ring(context, x + size / 2, y + size / 2, size / 2 - 1, 1, Math.round(beat * 160.0F) << 24 | this.waveColor.get() & 16777215);
            }

            return;
         }
      }

      String key = this.activeSource() == SongPlayerModule.Source.SYSTEM
         ? MediaWatcher.get().album() + MediaWatcher.get().artist()
         : MusicPlayer.get().currentTitle();
      int tint = tintFor(key);
      ThemeRenderer.fillRounded(context, x, y, size, size, tint, 8);
      int cx = x + size / 2;
      int cy = y + size / 2;
      int outer = size / 2 - 5;
      disc(context, cx, cy, outer, -15724524);
      ring(context, cx, cy, outer - 2, 1, 822083583);
      ring(context, cx, cy, outer - 5, 1, 620756991);
      disc(context, cx, cy, Math.max(2, outer / 3), tint);
      double turn = this.isPlaying() ? this.position() * 1.4 : 0.0;
      int mx = cx + (int)Math.round(Math.cos(turn) * (outer - 3));
      int my = cy + (int)Math.round(Math.sin(turn) * (outer - 3));
      context.fill(mx, my, mx + 2, my + 2, -2130706433);
      disc(context, cx, cy, 1, -15724524);
      if (beat > 0.05F) {
         ring(context, cx, cy, outer, 1, Math.round(beat * 160.0F) << 24 | this.waveColor.get() & 16777215);
      }
   }

   private static int tintFor(String key) {
      int hash = key != null && !key.isEmpty() ? key.hashCode() : 0;
      float hue = Math.floorMod(hash, 360);
      return hsv(hue, 0.45F, 0.5F);
   }

   private void drawProgress(GuiGraphics context, Minecraft mc, int x, int y, int w) {
      double duration = this.duration();
      double position = this.position();
      String left = formatTime(position);
      String right = formatTime(duration);
      int lw = mc.font.width(left);
      int rw = mc.font.width(right);
      context.drawString(mc.font, left, x, y + 1, Theme.textSecondary());
      context.drawString(mc.font, right, x + w - rw, y + 1, Theme.textSecondary());
      int trackX = x + lw + 6;
      int trackW = w - lw - rw - 12;
      if (trackW > 6) {
         ThemeRenderer.fillRounded(context, trackX, y + 3, trackW, 3, 1358954495, 1);
         float exact = duration <= 0.0 ? 0.0F : (float)(trackW * Math.min(1.0, position / duration));
         int done = (int)exact;
         float part = exact - done;
         int color = this.waveColor.get();
         if (done > 0) {
            ThemeRenderer.fillRounded(context, trackX, y + 3, done, 3, color, 1);
         }

         if (part > 0.02F && done < trackW) {
            context.fill(trackX + done, y + 3, trackX + done + 1, y + 6, fade(color, part));
         }

         context.pose().pushMatrix();
         context.pose().translate(part, 0.0F);
         context.fill(trackX + done - 2, y, trackX + done + 3, y + 9, color);
         context.pose().popMatrix();
      }
   }

   private static int fade(int color, float amount) {
      int alpha = Math.round((color >>> 24) * Math.max(0.0F, Math.min(1.0F, amount)));
      return alpha << 24 | color & 16777215;
   }

   public void renderWaveInto(GuiGraphics context, int x, int y, int w, int h) {
      this.updateSpectrum();
      this.renderWave(context, x, y, w, h);
   }

   private void renderWave(GuiGraphics context, int x, int y, int w, int h) {
      int bars = this.bars();
      float lift = 1.0F + this.beat() * 0.25F;
      int base = this.waveColor.get();
      int peak = this.waveColor2.get();
      int bottom = y + h;
      float cellW = (float)w / bars;
      int barW = Math.max(1, Math.round(cellW) - 1);

      for (int i = 0; i < bars; i++) {
         float level = Math.min(1.0F, this.levelAt(i) * lift);
         int bx = x + Math.round(i * cellW);
         int barH = Math.max(1, Math.round(level * h));
         int color = blend(base, peak, Math.max(0.0F, level - 0.55F) / 0.45F);
         switch ((SongPlayerModule.WaveStyle)this.style.get()) {
            case BARS:
               context.fill(bx, bottom - barH, bx + barW, bottom, color);
               break;
            case MIRROR:
               int half = Math.max(1, barH / 2);
               int mid = y + h / 2;
               context.fill(bx, mid - half, bx + barW, mid + half, color);
               break;
            case LINE: {
               int top = bottom - barH;
               context.fill(bx, top, bx + barW, top + 2, color);
               break;
            }
            case DOTS: {
               int top = bottom - barH;
               context.fill(bx, top, bx + barW, top + barW, color);
            }
         }
      }
   }

   private static void disc(GuiGraphics context, int cx, int cy, int r, int color) {
      for (int dx = -r; dx <= r; dx++) {
         int half = (int)Math.round(Math.sqrt(Math.max(0.0, (double)(r * r - dx * dx))));
         if (half > 0) {
            context.fill(cx + dx, cy - half, cx + dx + 1, cy + half, color);
         }
      }
   }

   private static void ring(GuiGraphics context, int cx, int cy, int r, int t, int color) {
      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d <= r && d >= r - t) {
               context.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
            }
         }
      }
   }

   private static int hsv(float hue, float saturation, float value) {
      float c = value * saturation;
      float xx = c * (1.0F - Math.abs(hue / 60.0F % 2.0F - 1.0F));
      float m = value - c;
      float r;
      float g;
      float b;
      if (hue < 60.0F) {
         r = c;
         g = xx;
         b = 0.0F;
      } else if (hue < 120.0F) {
         r = xx;
         g = c;
         b = 0.0F;
      } else if (hue < 180.0F) {
         r = 0.0F;
         g = c;
         b = xx;
      } else if (hue < 240.0F) {
         r = 0.0F;
         g = xx;
         b = c;
      } else if (hue < 300.0F) {
         r = xx;
         g = 0.0F;
         b = c;
      } else {
         r = c;
         g = 0.0F;
         b = xx;
      }

      return 0xFF000000 | Math.round((r + m) * 255.0F) << 16 | Math.round((g + m) * 255.0F) << 8 | Math.round((b + m) * 255.0F);
   }

   private static int blend(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int a = lerp(from >>> 24, to >>> 24, t);
      int r = lerp(from >> 16 & 0xFF, to >> 16 & 0xFF, t);
      int g = lerp(from >> 8 & 0xFF, to >> 8 & 0xFF, t);
      int b = lerp(from & 0xFF, to & 0xFF, t);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int lerp(int a, int b, float t) {
      return Math.round(a + (b - a) * t);
   }

   public static String formatTime(double seconds) {
      int total = (int)Math.max(0.0, seconds);
      return String.format("%d:%02d", total / 60, total % 60);
   }

   public static enum Source {
      AUTO("Auto"),
      LOCAL("Local Files"),
      SYSTEM("Spotify / System");

      public final String title;

      private Source(String title) {
         this.title = title;
      }
   }

   public static enum WaveStyle {
      BARS("Bars"),
      MIRROR("Mirror"),
      LINE("Line"),
      DOTS("Dots");

      public final String title;

      private WaveStyle(String title) {
         this.title = title;
      }
   }
}
