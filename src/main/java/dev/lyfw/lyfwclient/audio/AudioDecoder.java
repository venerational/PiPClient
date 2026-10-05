package dev.lyfw.lyfwclient.audio;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

public final class AudioDecoder {
   private static final int MAX_FRAMES = 39690000;

   private AudioDecoder() {
   }

   public static boolean isSupported(Path path) {
      String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
      return name.endsWith(".ogg") || name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".aif");
   }

   public static AudioClip decode(Path path) {
      try {
         String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
         return name.endsWith(".ogg") ? decodeVorbis(path) : decodePcm(path);
      } catch (Throwable var2) {
         System.out.println("[Pip Client] Could not read " + path.getFileName() + " (" + var2 + ")");
         return null;
      }
   }

   private static AudioClip decodeVorbis(Path path) {
      MemoryStack stack = MemoryStack.stackPush();

      Object var21;
      label154: {
         Object var22;
         label155: {
            AudioClip var25;
            label156: {
               try {
                  IntBuffer error = stack.mallocInt(1);
                  long handle = STBVorbis.stb_vorbis_open_filename(path.toString(), error, null);
                  if (handle == 0L) {
                     System.out.println("[Pip Client] " + path.getFileName() + " is not readable Vorbis (code " + error.get(0) + ")");
                     var21 = null;
                     break label154;
                  }

                  try {
                     STBVorbisInfo info = STBVorbisInfo.malloc(stack);
                     STBVorbis.stb_vorbis_get_info(handle, info);
                     int channels = Math.max(1, info.channels());
                     int sampleRate = info.sample_rate();
                     int frames = Math.min(39690000, STBVorbis.stb_vorbis_stream_length_in_samples(handle));
                     if (frames <= 0) {
                        var22 = null;
                        break label155;
                     }

                     short[] samples = new short[frames * channels];
                     short[] chunk = new short[4096 * channels];
                     int written = 0;

                     while (written < samples.length) {
                        int got = STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, channels, chunk);
                        if (got <= 0) {
                           break;
                        }

                        int values = Math.min(got * channels, samples.length - written);
                        System.arraycopy(chunk, 0, samples, written, values);
                        written += values;
                     }

                     if (written == 0) {
                        var25 = null;
                        break label156;
                     }

                     if (written < samples.length) {
                        short[] exact = new short[written];
                        System.arraycopy(samples, 0, exact, 0, written);
                        samples = exact;
                     }

                     var25 = new AudioClip(samples, sampleRate, channels);
                  } finally {
                     STBVorbis.stb_vorbis_close(handle);
                  }
               } catch (Throwable var20) {
                  if (stack != null) {
                     try {
                        stack.close();
                     } catch (Throwable var18) {
                        var20.addSuppressed(var18);
                     }
                  }

                  throw var20;
               }

               if (stack != null) {
                  stack.close();
               }

               return var25;
            }

            if (stack != null) {
               stack.close();
            }

            return var25;
         }

         if (stack != null) {
            stack.close();
         }

         return (AudioClip)var22;
      }

      if (stack != null) {
         stack.close();
      }

      return (AudioClip)var21;
   }

   private static AudioClip decodePcm(Path path) throws Exception {
      AudioClip var13;
      try (AudioInputStream source = AudioSystem.getAudioInputStream(new File(path.toString()))) {
         AudioFormat in = source.getFormat();
         AudioFormat target = new AudioFormat(Encoding.PCM_SIGNED, in.getSampleRate(), 16, in.getChannels(), in.getChannels() * 2, in.getSampleRate(), false);

         try (AudioInputStream converted = AudioSystem.getAudioInputStream(target, source)) {
            byte[] bytes = converted.readAllBytes();
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            short[] samples = new short[bytes.length / 2];

            for (int i = 0; i < samples.length; i++) {
               samples[i] = buffer.getShort(i * 2);
            }

            var13 = clamp(new AudioClip(samples, (int)target.getSampleRate(), target.getChannels()));
         }
      }

      return var13;
   }

   private static AudioClip clamp(AudioClip clip) {
      if (clip.frames() <= 39690000) {
         return clip;
      } else {
         int keep = 39690000 * clip.channels();
         short[] cut = new short[keep];
         System.arraycopy(clip.samples(), 0, cut, 0, keep);
         return new AudioClip(cut, clip.sampleRate(), clip.channels());
      }
   }
}
