package dev.lyfw.lyfwclient.audio;

public final class Spectrum {
   private static final int FFT_SIZE = 1024;
   private static final int HISTORY = 43;
   private static final long MIN_BEAT_GAP_MS = 120L;
   private final int bands;
   private final float[] levels;
   private final float[] window;
   private final float[] real = new float[1024];
   private final float[] imaginary = new float[1024];
   private final float[] hann = new float[1024];
   private final float[] energyHistory = new float[43];
   private int historyIndex;
   private long lastBeatAt;
   private float beatPulse;

   public Spectrum(int bands) {
      this.bands = Math.max(1, bands);
      this.levels = new float[this.bands];
      this.window = new float[1024];

      for (int i = 0; i < 1024; i++) {
         this.hann[i] = 0.5F * (1.0F - (float)Math.cos((Math.PI * 2) * i / 1023.0));
      }
   }

   public int bands() {
      return this.bands;
   }

   public float level(int band) {
      return this.levels[Math.floorMod(band, this.bands)];
   }

   public float beatPulse() {
      return this.beatPulse;
   }

   public void decay() {
      for (int i = 0; i < this.bands; i++) {
         this.levels[i] = this.levels[i] * 0.85F;
      }

      this.beatPulse *= 0.85F;
   }

   public void update(AudioClip clip, int frame, float smoothing, float gain) {
      clip.readMono(frame, this.window);

      for (int i = 0; i < 1024; i++) {
         this.real[i] = this.window[i] * this.hann[i];
         this.imaginary[i] = 0.0F;
      }

      fft(this.real, this.imaginary);
      int usable = 512;
      float bass = 0.0F;

      for (int band = 0; band < this.bands; band++) {
         int from = binFor(band, this.bands, usable);
         int to = Math.max(from + 1, binFor(band + 1, this.bands, usable));
         float peak = 0.0F;

         for (int bin = from; bin < to && bin < usable; bin++) {
            float magnitude = (float)Math.sqrt(this.real[bin] * this.real[bin] + this.imaginary[bin] * this.imaginary[bin]) / usable;
            peak = Math.max(peak, magnitude);
            if (bin < usable / 16) {
               bass += magnitude;
            }
         }

         float scaled = (float)Math.min(1.0, Math.log10(1.0 + peak * 9.0 * gain));
         this.levels[band] = this.levels[band] * smoothing + scaled * (1.0F - smoothing);
      }

      this.detectBeat(bass);
   }

   private static int binFor(int band, int bands, int usable) {
      double t = (double)band / bands;
      return Math.min(usable - 1, 1 + (int)Math.round(Math.pow(usable, t)) - 1);
   }

   private void detectBeat(float bassEnergy) {
      float average = 0.0F;

      for (float e : this.energyHistory) {
         average += e;
      }

      average /= 43.0F;
      this.energyHistory[this.historyIndex] = bassEnergy;
      this.historyIndex = (this.historyIndex + 1) % 43;
      long now = System.currentTimeMillis();
      boolean loudEnough = bassEnergy > average * 1.35F && bassEnergy > 8.0E-4F;
      if (loudEnough && now - this.lastBeatAt > 120L) {
         this.lastBeatAt = now;
         this.beatPulse = 1.0F;
      } else {
         this.beatPulse *= 0.82F;
      }
   }

   private static void fft(float[] real, float[] imaginary) {
      int n = real.length;
      int i = 1;

      for (int j = 0; i < n; i++) {
         int bit;
         for (bit = n >> 1; (j & bit) != 0; bit >>= 1) {
            j ^= bit;
         }

         j ^= bit;
         if (i < j) {
            float tr = real[i];
            real[i] = real[j];
            real[j] = tr;
            float ti = imaginary[i];
            imaginary[i] = imaginary[j];
            imaginary[j] = ti;
         }
      }

      for (int len = 2; len <= n; len <<= 1) {
         double angle = (-Math.PI * 2) / len;
         float wr = (float)Math.cos(angle);
         float wi = (float)Math.sin(angle);

         for (int ix = 0; ix < n; ix += len) {
            float curR = 1.0F;
            float curI = 0.0F;

            for (int k = 0; k < len / 2; k++) {
               int a = ix + k;
               int b = a + len / 2;
               float xr = real[b] * curR - imaginary[b] * curI;
               float xi = real[b] * curI + imaginary[b] * curR;
               real[b] = real[a] - xr;
               imaginary[b] = imaginary[a] - xi;
               real[a] += xr;
               imaginary[a] += xi;
               float nextR = curR * wr - curI * wi;
               curI = curR * wi + curI * wr;
               curR = nextR;
            }
         }
      }
   }
}
