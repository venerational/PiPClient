package dev.lyfw.lyfwclient.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public final class MediaWatcher {
   private static final MediaWatcher INSTANCE = new MediaWatcher();
   private volatile String app = "";
   private volatile String title = "";
   private volatile String artist = "";
   private volatile String album = "";
   private volatile boolean playing;
   private volatile boolean available;
   private volatile String status = "";
   private static final double RESYNC_SECONDS = 0.75;
   private volatile double position;
   private volatile int duration;
   private volatile long positionAt;
   private Process process;
   private Thread reader;
   private volatile boolean running;
   private boolean hooked;
   private static final String MAC_SCRIPT = "if application \"Spotify\" is running then\n  tell application \"Spotify\"\n    set st to player state\n    if st is stopped then return \"NONE\"\n    set s to \"Paused\"\n    if st is playing then set s to \"Playing\"\n    set t to name of current track\n    set a to artist of current track\n    set al to album of current track\n    set p to ((player position) * 1000) as integer\n    set d to duration of current track\n    if d > 10000 then set d to d / 1000\n    set d to d as integer\n    return \"T|Spotify|\" & s & \"|\" & p & \"|0|\" & d & \"|\" & t & \"|\" & a & \"|\" & al\n  end tell\nelse\n  return \"OFF\"\nend if";
   private static final String MAC_POLL_LOOP = "while true; do\n  out=$(osascript -e '"
      + "if application \"Spotify\" is running then\n  tell application \"Spotify\"\n    set st to player state\n    if st is stopped then return \"NONE\"\n    set s to \"Paused\"\n    if st is playing then set s to \"Playing\"\n    set t to name of current track\n    set a to artist of current track\n    set al to album of current track\n    set p to ((player position) * 1000) as integer\n    set d to duration of current track\n    if d > 10000 then set d to d / 1000\n    set d to d as integer\n    return \"T|Spotify|\" & s & \"|\" & p & \"|0|\" & d & \"|\" & t & \"|\" & a & \"|\" & al\n  end tell\nelse\n  return \"OFF\"\nend if"
         .replace("'", "'\\''")
      + "' 2>&1)\n  if [ $? -ne 0 ]; then\n    case \"$out\" in\n      *-1743*|*\"Not authorized\"*|*\"not allowed\"*) echo DENIED ;;\n      *\"-600\"*|*\"isn't running\"*) echo OFF ;;\n      *) echo ERR ;;\n    esac\n  else\n    echo \"$out\"\n  fi\n  sleep 1\ndone";
   private static final String CONTROL_TAIL = "$s = pickSession $mgr\nif ($s) { $s.CALL | Out-Null }\n";
   private static final String PREAMBLE = "$ErrorActionPreference = 'Stop'\nAdd-Type -AssemblyName System.Runtime.WindowsRuntime\n$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {\n   $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and\n   $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\nfunction Await($op, $type) { $t = $asTask.MakeGenericMethod($type).Invoke($null, @($op)); $t.Wait(-1) | Out-Null; $t.Result }\n$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]\n$propType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]\n$mgr = Await ($mgrType::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\nfunction pickSession($m) {\n   $all = @($m.GetSessions())\n   $pick = $all | Where-Object { $_.SourceAppUserModelId -like '*potify*' } | Select-Object -First 1\n   if (-not $pick) { $pick = $m.GetCurrentSession() }\n   return $pick\n}\n";
   private static final String POLL_LOOP = "while ($true) {\n   try {\n      $s = pickSession $mgr\n      if (-not $s) { Write-Output 'NONE' }\n      else {\n         $p = Await ($s.TryGetMediaPropertiesAsync()) $propType\n         $tl = $s.GetTimelineProperties()\n         $pb = $s.GetPlaybackInfo()\n         $t  = ($p.Title -replace '[\\r\\n\\t|]', ' ')\n         $a  = ($p.Artist -replace '[\\r\\n\\t|]', ' ')\n         $al = ($p.AlbumTitle -replace '[\\r\\n\\t|]', ' ')\n         $posMs = [int]$tl.Position.TotalMilliseconds\n         $ageMs = [int]((([datetimeoffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds))\n         if ($ageMs -lt 0 -or $ageMs -gt 30000) { $ageMs = 0 }\n         Write-Output ('T|' + $s.SourceAppUserModelId + '|' + $pb.PlaybackStatus + '|' +\n            $posMs + '|' + $ageMs + '|' + [int]$tl.EndTime.TotalSeconds + '|' + $t + '|' + $a + '|' + $al)\n      }\n   } catch { Write-Output 'ERR' }\n   Start-Sleep -Milliseconds 500\n}\n";

   private MediaWatcher() {
   }

   public static MediaWatcher get() {
      return INSTANCE;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public boolean available() {
      return this.available;
   }

   public String title() {
      return this.title;
   }

   public String artist() {
      return this.artist;
   }

   public String album() {
      return this.album;
   }

   public String status() {
      return this.status;
   }

   public String problem() {
      String current = this.status;
      return current.equals("Nothing playing") ? "" : current;
   }

   public String appName() {
      String raw = this.app.trim();
      int bang = raw.indexOf(33);
      if (bang >= 0) {
         raw = raw.substring(bang + 1);
      }

      int underscore = raw.indexOf(95);
      if (underscore > 0) {
         raw = raw.substring(0, underscore);
      }

      int exe = raw.toLowerCase(Locale.ROOT).lastIndexOf(".exe");
      if (exe > 0) {
         raw = raw.substring(0, exe);
      }

      int lastDot = raw.lastIndexOf(46);
      if (lastDot >= 0 && lastDot < raw.length() - 1) {
         raw = raw.substring(lastDot + 1);
      }

      return raw.isEmpty() ? "Media" : Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
   }

   public int duration() {
      return this.duration;
   }

   public double position() {
      if (!this.playing) {
         return this.position;
      } else {
         double drift = (System.currentTimeMillis() - this.positionAt) / 1000.0;
         return Math.min(this.duration <= 0 ? Double.MAX_VALUE : this.duration, this.position + drift);
      }
   }

   public String display() {
      if (!this.available) {
         return "Nothing detected";
      } else {
         return this.playing ? (this.artist.isEmpty() ? this.title : this.artist) : "Paused";
      }
   }

   private static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   }

   private static boolean isMac() {
      String name = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      return name.contains("mac") || name.contains("darwin");
   }

   private static void removeOldScripts() {
      for (String name : new String[]{"pip-media.ps1", "pip-media-control.ps1"}) {
         try {
            Files.deleteIfExists(FabricLoader.getInstance().getConfigDir().resolve(name));
         } catch (Exception var5) {
         }
      }
   }

   public synchronized void start() {
      if (!this.running) {
         removeOldScripts();
         ProcessBuilder builder = this.buildWatcher();
         if (builder == null) {
            this.status = "Media detection needs Windows or macOS";
         } else {
            try {
               builder.redirectErrorStream(true);
               this.process = builder.start();
               this.running = true;
               this.reader = new Thread(this::read, "Pip Client Media");
               this.reader.setDaemon(true);
               this.reader.start();
               this.registerShutdownHook();
            } catch (Exception var3) {
               this.status = "Could not start media detection";
               System.out.println("[Pip Client] Song Player could not start the media watcher (" + var3 + ")");
            }
         }
      }
   }

   private ProcessBuilder buildWatcher() {
      if (isWindows()) {
         return new ProcessBuilder(
            "powershell.exe",
            "-NoProfile",
            "-NoLogo",
            "-Command",
            "$ErrorActionPreference = 'Stop'\nAdd-Type -AssemblyName System.Runtime.WindowsRuntime\n$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {\n   $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and\n   $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\nfunction Await($op, $type) { $t = $asTask.MakeGenericMethod($type).Invoke($null, @($op)); $t.Wait(-1) | Out-Null; $t.Result }\n$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]\n$propType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]\n$mgr = Await ($mgrType::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\nfunction pickSession($m) {\n   $all = @($m.GetSessions())\n   $pick = $all | Where-Object { $_.SourceAppUserModelId -like '*potify*' } | Select-Object -First 1\n   if (-not $pick) { $pick = $m.GetCurrentSession() }\n   return $pick\n}\nwhile ($true) {\n   try {\n      $s = pickSession $mgr\n      if (-not $s) { Write-Output 'NONE' }\n      else {\n         $p = Await ($s.TryGetMediaPropertiesAsync()) $propType\n         $tl = $s.GetTimelineProperties()\n         $pb = $s.GetPlaybackInfo()\n         $t  = ($p.Title -replace '[\\r\\n\\t|]', ' ')\n         $a  = ($p.Artist -replace '[\\r\\n\\t|]', ' ')\n         $al = ($p.AlbumTitle -replace '[\\r\\n\\t|]', ' ')\n         $posMs = [int]$tl.Position.TotalMilliseconds\n         $ageMs = [int]((([datetimeoffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds))\n         if ($ageMs -lt 0 -or $ageMs -gt 30000) { $ageMs = 0 }\n         Write-Output ('T|' + $s.SourceAppUserModelId + '|' + $pb.PlaybackStatus + '|' +\n            $posMs + '|' + $ageMs + '|' + [int]$tl.EndTime.TotalSeconds + '|' + $t + '|' + $a + '|' + $al)\n      }\n   } catch { Write-Output 'ERR' }\n   Start-Sleep -Milliseconds 500\n}\n"
         );
      } else {
         return isMac() ? new ProcessBuilder("/bin/sh", "-c", MAC_POLL_LOOP) : null;
      }
   }

   private void registerShutdownHook() {
      if (!this.hooked) {
         this.hooked = true;
         Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Pip Client Media Shutdown"));
      }
   }

   public synchronized void stop() {
      this.running = false;
      Process p = this.process;
      this.process = null;
      if (p != null) {
         p.destroy();
      }

      Thread t = this.reader;
      this.reader = null;
      if (t != null) {
         t.interrupt();
      }

      this.playing = false;
      this.available = false;
   }

   private void read() {
      Process p = this.process;
      if (p != null) {
         String line;
         try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            while (this.running && (line = in.readLine()) != null) {
               this.accept(line.trim());
            }
         } catch (IOException var7) {
            if (this.running) {
               this.status = "Media detection stopped";
            }
         }
      }
   }

   private void accept(String line) {
      if (line.equals("NONE")) {
         this.available = false;
         this.playing = false;
         this.status = "Nothing playing";
      } else if (line.equals("OFF")) {
         this.available = false;
         this.playing = false;
         this.status = "Spotify is not open";
      } else if (line.equals("DENIED")) {
         this.available = false;
         this.playing = false;
         this.status = "Allow Minecraft to control Spotify: System Settings > Privacy & Security > Automation";
      } else if (!line.startsWith("T|")) {
         if (line.equals("ERR")) {
            this.status = "Could not read the media session";
         }
      } else {
         String[] parts = line.split("\\|", 9);
         if (parts.length >= 9) {
            boolean nowPlaying = parts[2].equalsIgnoreCase("Playing");
            double sampled = parseInt(parts[3]) / 1000.0;
            double age = nowPlaying ? Math.max(0.0, parseInt(parts[4]) / 1000.0) : 0.0;
            double reported = Math.max(0.0, sampled + age);
            String newTitle = parts[6];
            double predicted = this.available ? this.position() : Double.NaN;
            boolean resync = Double.isNaN(predicted) || nowPlaying != this.playing || !newTitle.equals(this.title) || Math.abs(reported - predicted) > 0.75;
            this.app = parts[1];
            this.playing = nowPlaying;
            this.duration = parseInt(parts[5]);
            this.title = newTitle;
            this.artist = parts[7];
            this.album = parts[8];
            this.available = true;
            this.status = "";
            if (resync) {
               this.position = reported;
               this.positionAt = System.currentTimeMillis();
            }
         }
      }
   }

   private static int parseInt(String value) {
      try {
         return Math.max(0, Integer.parseInt(value.trim()));
      } catch (NumberFormatException var2) {
         return 0;
      }
   }

   private static String macCommand(String command) {
      return switch (command) {
         case "next" -> "next track";
         case "previous" -> "previous track";
         default -> "playpause";
      };
   }

   private static String macControl(String verb) {
      return "if application \"Spotify\" is running then tell application \"Spotify\" to " + verb;
   }

   public void control(String command) {
      if (this.available) {
         String call = switch (command) {
            case "next" -> "TrySkipNextAsync()";
            case "previous" -> "TrySkipPreviousAsync()";
            default -> "TryTogglePlayPauseAsync()";
         };
         new Thread(
               () -> {
                  try {
                     ProcessBuilder builder = isWindows()
                        ? new ProcessBuilder(
                           "powershell.exe",
                           "-NoProfile",
                           "-NoLogo",
                           "-Command",
                           "$ErrorActionPreference = 'Stop'\nAdd-Type -AssemblyName System.Runtime.WindowsRuntime\n$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {\n   $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and\n   $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\nfunction Await($op, $type) { $t = $asTask.MakeGenericMethod($type).Invoke($null, @($op)); $t.Wait(-1) | Out-Null; $t.Result }\n$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]\n$propType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]\n$mgr = Await ($mgrType::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\nfunction pickSession($m) {\n   $all = @($m.GetSessions())\n   $pick = $all | Where-Object { $_.SourceAppUserModelId -like '*potify*' } | Select-Object -First 1\n   if (-not $pick) { $pick = $m.GetCurrentSession() }\n   return $pick\n}\n"
                              + "$s = pickSession $mgr\nif ($s) { $s.CALL | Out-Null }\n".replace("CALL", call)
                        )
                        : new ProcessBuilder("osascript", "-e", macControl(macCommand(command)));
                     builder.redirectErrorStream(true);
                     Process one = builder.start();
                     one.waitFor();
                  } catch (Exception var4) {
                     System.out.println("[Pip Client] Song Player could not send " + command + " (" + var4 + ")");
                  }
               },
               "Pip Client Media Control"
            )
            .start();
      }
   }
}
