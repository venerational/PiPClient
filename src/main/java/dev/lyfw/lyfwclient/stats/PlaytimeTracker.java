package dev.lyfw.lyfwclient.stats;

public final class PlaytimeTracker {
   private static final PlaytimeTracker INSTANCE = new PlaytimeTracker();
   private static final long MIN_SESSION_SECONDS = 30L;
   private long totalSeconds;
   private long longestSessionSeconds;
   private long firstSeenEpoch;
   private final long sessionStartedAt = System.currentTimeMillis();
   private long lastTickAt = System.currentTimeMillis();
   private long accrued;

   private PlaytimeTracker() {
   }

   public static PlaytimeTracker get() {
      return INSTANCE;
   }

   public void tick() {
      long now = System.currentTimeMillis();
      long delta = now - this.lastTickAt;
      this.lastTickAt = now;
      if (delta > 0L && delta < 5000L) {
         for (this.accrued += delta; this.accrued >= 1000L; this.totalSeconds++) {
            this.accrued -= 1000L;
         }
      }

      long session = this.sessionSeconds();
      if (session > this.longestSessionSeconds) {
         this.longestSessionSeconds = session;
      }
   }

   public long totalSeconds() {
      return this.totalSeconds;
   }

   public long sessionSeconds() {
      return Math.max(0L, (System.currentTimeMillis() - this.sessionStartedAt) / 1000L);
   }

   public long longestSessionSeconds() {
      return Math.max(this.longestSessionSeconds, this.sessionSeconds());
   }

   public long firstSeenEpoch() {
      return this.firstSeenEpoch;
   }

   public boolean sessionWorthReporting() {
      return this.sessionSeconds() >= 30L;
   }

   public void restore(long total, long longestSession, long firstSeen) {
      this.totalSeconds = Math.max(0L, total);
      this.longestSessionSeconds = Math.max(0L, longestSession);
      this.firstSeenEpoch = firstSeen > 0L ? firstSeen : System.currentTimeMillis();
   }

   public void reset() {
      this.totalSeconds = 0L;
      this.longestSessionSeconds = 0L;
      this.accrued = 0L;
      this.firstSeenEpoch = System.currentTimeMillis();
   }

   public static String format(long seconds) {
      long hours = seconds / 3600L;
      long minutes = seconds % 3600L / 60L;
      if (hours > 0L) {
         return hours + "h " + minutes + "m";
      } else {
         return minutes > 0L ? minutes + "m " + seconds % 60L + "s" : seconds + "s";
      }
   }
}
