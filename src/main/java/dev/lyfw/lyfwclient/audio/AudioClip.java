package dev.lyfw.lyfwclient.audio;

public record AudioClip(short[] samples, int sampleRate, int channels) {
   public int frames() {
      return this.samples.length / Math.max(1, this.channels);
   }

   public double durationSeconds() {
      return (double)this.frames() / Math.max(1, this.sampleRate);
   }

   public void readMono(int fromFrame, float[] out) {
      int ch = Math.max(1, this.channels);

      for (int i = 0; i < out.length; i++) {
         int frame = fromFrame + i;
         if (frame >= 0 && frame < this.frames()) {
            int base = frame * ch;
            int sum = 0;

            for (int c = 0; c < ch; c++) {
               sum += this.samples[base + c];
            }

            out[i] = (float)sum / ch / 32768.0F;
         } else {
            out[i] = 0.0F;
         }
      }
   }
}
