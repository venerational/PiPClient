package dev.lyfw.lyfwclient.audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;

public final class MusicPlayer {
   private static final int CHUNK_FRAMES = 1024;
   private static final MusicPlayer INSTANCE = new MusicPlayer();
   private final List<Path> playlist = new ArrayList<>();
   private final Object lock = new Object();
   private Thread thread;
   private SourceDataLine line;
   private volatile AudioClip clip;
   private volatile int trackIndex = -1;
   private volatile int playHead;
   private volatile boolean playing;
   private volatile boolean stopRequested;
   private volatile boolean shuffle;
   private volatile boolean repeat;
   private volatile float volume = 0.6F;
   private volatile String status = "";
   private volatile int seekRequest = -1;

   private MusicPlayer() {
   }

   public static MusicPlayer get() {
      return INSTANCE;
   }

   public List<Path> playlist() {
      synchronized (this.lock) {
         return List.copyOf(this.playlist);
      }
   }

   public int trackIndex() {
      return this.trackIndex;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public String status() {
      return this.status;
   }

   public AudioClip clip() {
      return this.clip;
   }

   public int playHead() {
      return this.playHead;
   }

   public boolean shuffle() {
      return this.shuffle;
   }

   public void setShuffle(boolean shuffle) {
      this.shuffle = shuffle;
   }

   public boolean repeat() {
      return this.repeat;
   }

   public void setRepeat(boolean repeat) {
      this.repeat = repeat;
   }

   public float volume() {
      return this.volume;
   }

   public void setVolume(float volume) {
      this.volume = Math.max(0.0F, Math.min(1.0F, volume));
      this.applyVolume();
   }

   public String currentTitle() {
      Path path = this.currentPath();
      if (path == null) {
         return "Nothing playing";
      } else {
         String name = path.getFileName().toString();
         int dot = name.lastIndexOf(46);
         return dot > 0 ? name.substring(0, dot) : name;
      }
   }

   public Path currentPath() {
      synchronized (this.lock) {
         int i = this.trackIndex;
         return i >= 0 && i < this.playlist.size() ? this.playlist.get(i) : null;
      }
   }

   public double positionSeconds() {
      AudioClip c = this.clip;
      return c == null ? 0.0 : (double)this.playHead / c.sampleRate();
   }

   public double durationSeconds() {
      AudioClip c = this.clip;
      return c == null ? 0.0 : c.durationSeconds();
   }

   public int loadFolder(Path folder) {
      List<Path> found = new ArrayList<>();
      if (folder != null && Files.isDirectory(folder)) {
         try (Stream<Path> walk = Files.list(folder)) {
            walk.filter(x$0 -> Files.isRegularFile(x$0)).filter(AudioDecoder::isSupported).sorted((a, b) -> {
               String an = a.getFileName().toString().toLowerCase(Locale.ROOT);
               String bn = b.getFileName().toString().toLowerCase(Locale.ROOT);
               return an.compareTo(bn);
            }).forEach(found::add);
         } catch (IOException var12) {
            this.status = "Could not read that folder";
            return 0;
         }

         synchronized (this.lock) {
            this.playlist.clear();
            this.playlist.addAll(found);
         }

         this.status = found.isEmpty() ? "No .ogg or .wav files in that folder" : found.size() + " tracks";
         return found.size();
      } else {
         synchronized (this.lock) {
            this.playlist.clear();
         }

         this.status = folder == null ? "" : "Folder not found";
         return 0;
      }
   }

   public void playIndex(int index) {
      synchronized (this.lock) {
         if (index < 0 || index >= this.playlist.size()) {
            return;
         }

         this.trackIndex = index;
      }

      this.restart();
   }

   public void toggle() {
      if (this.thread != null && this.clip != null) {
         this.playing = !this.playing;
      } else if (this.trackIndex < 0) {
         this.playIndex(0);
      } else {
         this.restart();
      }
   }

   public void next() {
      this.step(1);
   }

   public void previous() {
      if (this.positionSeconds() > 3.0) {
         this.seekTo(0.0);
      } else {
         this.step(-1);
      }
   }

   private void step(int direction) {
      int size;
      synchronized (this.lock) {
         size = this.playlist.size();
      }

      if (size != 0) {
         int next;
         if (this.shuffle && size > 1) {
            do {
               next = (int)(Math.random() * size);
            } while (next == this.trackIndex);
         } else {
            next = Math.floorMod(this.trackIndex + direction, size);
         }

         this.playIndex(next);
      }
   }

   public void seekTo(double seconds) {
      AudioClip c = this.clip;
      if (c != null) {
         this.seekRequest = Math.max(0, Math.min(c.frames() - 1, (int)(seconds * c.sampleRate())));
      }
   }

   public void stop() {
      this.stopRequested = true;
      this.playing = false;
      Thread t = this.thread;
      if (t != null) {
         t.interrupt();
      }
   }

   private void restart() {
      this.stop();
      Thread previous = this.thread;
      if (previous != null) {
         try {
            previous.join(500L);
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
         }
      }

      this.stopRequested = false;
      Path path = this.currentPath();
      if (path != null) {
         this.thread = new Thread(() -> this.run(path), "Pip Client Music");
         this.thread.setDaemon(true);
         this.thread.start();
      }
   }

   private void run(Path path) {
      this.status = "Loading " + path.getFileName();
      AudioClip decoded = AudioDecoder.decode(path);
      if (decoded == null) {
         this.status = "Could not play " + path.getFileName();
         this.clip = null;
         this.playing = false;
      } else {
         this.clip = decoded;
         this.playHead = 0;
         this.status = "";

         try {
            AudioFormat format = new AudioFormat(decoded.sampleRate(), 16, decoded.channels(), true, false);
            Info info = new Info(SourceDataLine.class, format);
            this.line = (SourceDataLine)AudioSystem.getLine(info);
            this.line.open(format, 1024 * decoded.channels() * 2 * 8);
            this.line.start();
            this.applyVolume();
            this.playing = true;
            byte[] buffer = new byte[1024 * decoded.channels() * 2];

            while (!this.stopRequested && this.playHead < decoded.frames()) {
               if (!this.playing) {
                  Thread.sleep(30L);
               } else {
                  int seek = this.seekRequest;
                  if (seek >= 0) {
                     this.seekRequest = -1;
                     this.playHead = seek;
                     this.line.flush();
                  }

                  int frames = Math.min(1024, decoded.frames() - this.playHead);
                  int bytes = frames * decoded.channels() * 2;
                  int base = this.playHead * decoded.channels();

                  for (int i = 0; i < frames * decoded.channels(); i++) {
                     short s = decoded.samples()[base + i];
                     buffer[i * 2] = (byte)(s & 255);
                     buffer[i * 2 + 1] = (byte)(s >> 8 & 0xFF);
                  }

                  this.line.write(buffer, 0, bytes);
                  this.playHead += frames;
               }
            }

            this.line.drain();
         } catch (InterruptedException var16) {
            Thread.currentThread().interrupt();
         } catch (Exception var17) {
            this.status = "Audio device unavailable";
            System.out.println("[Pip Client] Song Player could not open an audio line (" + var17 + ")");
         } finally {
            this.closeLine();
         }

         boolean finished = !this.stopRequested;
         this.playing = false;
         if (finished) {
            if (this.repeat) {
               this.playIndex(this.trackIndex);
            } else {
               this.next();
            }
         }
      }
   }

   private void applyVolume() {
      SourceDataLine l = this.line;
      if (l != null && l.isOpen()) {
         try {
            if (l.isControlSupported(Type.MASTER_GAIN)) {
               FloatControl gain = (FloatControl)l.getControl(Type.MASTER_GAIN);
               float db = this.volume <= 0.0F ? gain.getMinimum() : (float)(20.0 * Math.log10(this.volume));
               gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
            }
         } catch (Exception var4) {
         }
      }
   }

   private void closeLine() {
      SourceDataLine l = this.line;
      this.line = null;
      if (l != null) {
         try {
            l.stop();
            l.close();
         } catch (Exception var3) {
         }
      }
   }

   public void sortPlaylist() {
      synchronized (this.lock) {
         Collections.sort(this.playlist);
      }
   }
}
