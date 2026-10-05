package dev.lyfw.lyfwclient.render;

final class CapeScenesG {
   private CapeScenesG() {
   }

   static final class CastleFireworks implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7, 1.0}, new int[]{-16447720, -15066560, -14015928});

         for (int i = 0; i < 80; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 90.0, 0.0, -1512193, 0.08 + AnimatedCapes.hash(i, 3) * 0.3);
         }

         c.fillBelow(x -> 104.0 - 10.0 * Math.exp(-Math.pow((x - 40.0) / 26.0, 2.0)), -15856102);
         int stone = -15066582;
         c.rect(22.0, 82.0, 36.0, 14.0, stone);

         for (int x = 22; x < 58; x += 3) {
            c.rect(x, 80.0, 1.8, 2.0, stone);
         }

         double[][] towers = new double[][]{{20.0, 64.0, 7.0}, {56.0, 64.0, 7.0}, {36.0, 54.0, 9.0}};

         for (double[] tw : towers) {
            c.rect(tw[0] - tw[2] / 2.0, tw[1], tw[2], 96.0 - tw[1], stone);
            c.polygon(new double[][]{{tw[0] - tw[2] / 2.0 - 1.0, tw[1]}, {tw[0] + tw[2] / 2.0 + 1.0, tw[1]}, {tw[0], tw[1] - tw[2] * 1.4}}, -15592930);
            c.line(tw[0], tw[1] - tw[2] * 1.4, tw[0], tw[1] - tw[2] * 1.4 - 5.0, 0.4, stone);
         }

         c.polygon(new double[][]{{35.0, 92.0}, {41.0, 92.0}, {41.0, 88.0}, {38.0, 86.0}, {35.0, 88.0}}, -16382452);

         for (int i = 0; i < 26; i++) {
            c.rect(
               AnimatedCapes.hash(i, 8) * 80.0,
               108.0 + AnimatedCapes.hash(i, 9) * 18.0,
               1.0,
               1.0,
               AnimatedCapes.alpha(-14224, 0.5 + AnimatedCapes.hash(i, 10) * 0.5)
            );
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] windows = new double[][]{{20.0, 70.0}, {56.0, 70.0}, {36.0, 62.0}, {28.0, 86.0}, {48.0, 86.0}, {20.0, 78.0}};

         for (int k = 0; k < windows.length; k++) {
            c.rect(windows[k][0] - 0.8, windows[k][1], 1.6, 2.4, AnimatedCapes.lerp(-26064, -8048, AnimatedCapes.noise(t * 3.0 + k * 5, 2.0)));
         }

         double[][] flags = new double[][]{{20.0, 50.0}, {56.0, 50.0}, {36.0, 36.0}};

         for (double[] f : flags) {
            for (int s = 0; s < 5; s++) {
               double wave = Math.sin(t * 5.0 - s * 0.9) * 0.8;
               c.rect(f[0] + s, f[1] + wave, 1.2, 2.5, s % 2 == 0 ? -2082230 : -5232070);
            }
         }

         int[] palette = new int[]{-46486, -11869953, -12224, -7667862, -3118337, -1};

         for (int k = 0; k < 5; k++) {
            double period = 2.6 + k * 0.3;
            double cycle = t / period + k * 0.37;
            int n = (int)Math.floor(cycle);
            double life = (cycle - n) * period;
            double bx = 10.0 + AnimatedCapes.hash(k, n) * 60.0;
            double by = 14.0 + AnimatedCapes.hash(k, n + 1) * 36.0;
            int col = palette[(int)(AnimatedCapes.hash(k, n + 2) * palette.length)];
            double launchX = 30.0 + AnimatedCapes.hash(k, n + 3) * 20.0;
            if (life < 0.6) {
               double p = life / 0.6;
               double x = launchX + (bx - launchX) * p;
               double y = 100.0 + (by - 100.0) * (1.0 - (1.0 - p) * (1.0 - p));
               c.beam(x, y, x - (bx - launchX) * 0.05, y + 6.0, 0.4, -8016, 0.8);
            } else {
               double p = (life - 0.6) / (period - 0.6);
               double fade = 1.0 - p;
               int count = 28;

               for (int s = 0; s < count; s++) {
                  double a = s * Math.PI * 2.0 / count + AnimatedCapes.hash(s, n);
                  double speed = 20.0 + AnimatedCapes.hash(s, k + n) * 6.0;
                  double dist = speed * (1.0 - Math.exp(-p * 3.0));
                  double x = bx + Math.cos(a) * dist;
                  double y = by + Math.sin(a) * dist + p * p * 18.0;
                  c.star(x, y, p < 0.45 ? 1.0 : 0.0, col, Math.min(1.0, fade * 1.3));
                  c.add((int)(x - Math.cos(a) * 1.5), (int)(y - Math.sin(a) * 1.5 - 0.5), col, fade * 0.5);
                  if (p > 0.4 && AnimatedCapes.hash(s, (int)(t * 20.0)) > 0.7) {
                     c.add((int)x, (int)y, -1, fade);
                  }
               }

               if (p < 0.15) {
                  c.glow(bx, by, 20.0, col, (0.15 - p) / 0.15 * 0.8);

                  for (int y = 50; y < 100; y++) {
                     for (int x = 10; x < 70; x++) {
                        c.add(x, y, col, 0.04 * (0.15 - p) / 0.15);
                     }
                  }
               }
            }
         }
      }
   }

   static final class EarthOrbit implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 196.0;
      private static final double R = 130.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.fill(-16711416);

         for (int i = 0; i < 160; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 80.0,
               AnimatedCapes.hash(i, 3) > 0.96 ? 1.0 : 0.0,
               -1,
               0.15 + AnimatedCapes.hash(i, 4) * 0.5
            );
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 58; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double nx = (x + 0.5 - 40.0) / 130.0;
               double ny = (y + 0.5 - 196.0) / 130.0;
               double d2 = nx * nx + ny * ny;
               double d = Math.sqrt(d2);
               if (!(d > 1.04)) {
                  if (d > 1.0) {
                     double rim = (1.04 - d) / 0.04;
                     double lit = AnimatedCapes.clamp(0.3 + nx * 1.5, 0.0, 1.0);
                     c.add(x, y, -11887873, rim * rim * 0.8 * lit + rim * 0.15);
                  } else {
                     double nz = Math.sqrt(1.0 - d2);
                     double lon = Math.atan2(nx, nz) + t * 0.02;
                     double lat = Math.asin(ny);
                     double land = AnimatedCapes.fbm(lon * 4.0 + 10.0, lat * 4.0, 5);
                     int col = land > 0.52
                        ? AnimatedCapes.lerp(-12944838, -4151184, AnimatedCapes.smoothstep(0.52, 0.75, land))
                        : AnimatedCapes.lerp(-16110998, -15050072, land);
                     double clouds = AnimatedCapes.smoothstep(0.48, 0.72, AnimatedCapes.fbm(lon * 6.0 + t * 0.015 + 30.0, lat * 8.0, 4));
                     col = AnimatedCapes.lerp(col, -1, clouds * 0.9);
                     double light = AnimatedCapes.clamp(nx * 1.4 + 0.25 - ny * 0.2 + nz * 0.1, 0.0, 1.0);
                     col = AnimatedCapes.shade(col, 0.03 + light);
                     if (light < 0.08 && land > 0.55 && clouds < 0.3 && AnimatedCapes.hash((int)(lon * 90.0), (int)(lat * 90.0)) > 0.78) {
                        col = AnimatedCapes.lerp(col, -14224, 0.8 * (1.0 - light / 0.08));
                     }

                     c.set(x, y, AnimatedCapes.dither(col, x, y));
                     c.add(x, y, -9785089, Math.pow(1.0 - nz, 3.0) * 0.6 * light);
                  }
               }
            }
         }

         double sx = 74.0;
         double sy = 69.0;
         double flare = 0.7 + 0.3 * Math.sin(t * 0.5);
         c.glow(sx, sy, 18.0, -5952, flare);
         c.beam(sx - 20.0, sy, sx + 20.0, sy, 0.6, -3888, 0.5 * flare);
         double stationX = AnimatedCapes.wrap(t * 3.0 + 60.0, 120.0) - 20.0;
         double stationY = 26.0 + Math.sin(t * 0.3) * 2.0;
         c.rect(stationX - 4.0, stationY - 1.0, 8.0, 2.0, -2565920);
         c.rect(stationX - 1.0, stationY - 3.0, 2.0, 6.0, -4671296);

         for (int side = -1; side <= 1; side += 2) {
            for (int p = 0; p < 2; p++) {
               double px = stationX + side * (6 + p * 5);
               c.rect(px - 2.0, stationY - 5.0, 4.0, 10.0, -12957046);
               c.rect(px - 2.0, stationY - 5.0, 4.0, 10.0, AnimatedCapes.alpha(-5193473, 0.25 + 0.2 * Math.sin(t + p)));
               c.line(px - 2.0, stationY, px + 2.0, stationY, 0.3, -7697776);
            }

            c.line(stationX + side * 4, stationY, stationX + side * 14, stationY, 0.4, -4671296);
         }

         if (AnimatedCapes.wrap(t, 1.4) < 0.2) {
            c.glow(stationX + 4.0, stationY, 2.0, -53200, 1.0);
         }
      }
   }

   static final class Eclipse implements AnimatedCapes.Scene {
      private static final double SX = 40.0;
      private static final double SY = 44.0;
      private static final double MOON = 12.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 0.78}, new int[]{-16513510, -15065528, -2066358});

         for (int i = 0; i < 90; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 80.0, 0.0, -1512193, 0.1 + AnimatedCapes.hash(i, 3) * 0.35);
         }

         c.fillBelow(x -> 100.0 - 8.0 * Math.sin(x * 0.05 + 1.0) - 3.0 * AnimatedCapes.fbm(x * 0.1, 2.0, 3), -16119278);
         c.fillBelow(x -> 112.0 - 4.0 * Math.sin(x * 0.07 + 4.0), -16448248);
         c.line(62.0, 110.0, 63.0, 90.0, 1.4, -16448248);

         for (int i = 0; i < 30; i++) {
            double a = AnimatedCapes.hash(i, 5) * Math.PI * 2.0;
            double d = Math.sqrt(AnimatedCapes.hash(i, 6)) * 9.0;
            c.disc(63.0 + Math.cos(a) * d, 86.0 + Math.sin(a) * d * 0.7, 1.8, -16448248);
         }

         for (int k = 0; k < 4; k++) {
            double px = 14 + k * 5;
            c.disc(px, 104 - k % 2, 1.2, -16448248);
            c.rect(px - 1.0, 105 - k % 2, 2.0, 5.0, -16448248);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 0; y < 96; y++) {
            for (int x = 0; x < 80; x++) {
               double dx = x + 0.5 - 40.0;
               double dy = y + 0.5 - 44.0;
               double r = Math.hypot(dx, dy);
               if (!(r < 12.0) && !(r > 60.0)) {
                  double a = Math.atan2(dy, dx);
                  double streamers = AnimatedCapes.fbm(Math.cos(a) * 2.5 + 5.0, Math.sin(a) * 2.5 + t * 0.03, 4);
                  double reach = 10.0 + streamers * 30.0;
                  double fall = Math.exp(-(r - 12.0) / reach) * (0.5 + streamers);
                  c.add(x, y, AnimatedCapes.lerp(-4665089, -1, fall), fall * 0.55);
               }
            }
         }

         c.glow(40.0, 44.0, 18.0, -1511169, 0.35);

         for (int k = 0; k < 5; k++) {
            double a = AnimatedCapes.hash(k, 10) * Math.PI * 2.0 + Math.sin(t * 0.2 + k) * 0.05;
            double h = 1.5 + AnimatedCapes.noise(t * 0.8 + k, k) * 2.5;

            for (double s = 0.0; s < 1.0; s += 0.1) {
               double ang = a + (s - 0.5) * 0.35;
               double rr = 12.0 + Math.sin(s * Math.PI) * h;
               c.add((int)(40.0 + Math.cos(ang) * rr), (int)(44.0 + Math.sin(ang) * rr), -46486, 0.7);
            }
         }

         c.disc(40.0, 44.0, 12.0, -16645628);
         c.ring(40.0, 44.0, 12.3, 0.6, AnimatedCapes.alpha(-1, 0.8));
         double cycle = AnimatedCapes.wrap(t, 11.0);
         if (cycle < 1.6) {
            double p = cycle / 1.6;
            double flash = Math.sin(p * Math.PI);
            double bx = 40.0 + Math.cos(-0.8) * 12.0;
            double by = 44.0 + Math.sin(-0.8) * 12.0;
            c.glow(bx, by, 10.0 + flash * 14.0, -1, 1.2 * flash);
            c.beam(bx - 22.0 * flash, by, bx + 22.0 * flash, by, 0.6, -1, flash);
            c.beam(bx, by - 16.0 * flash, bx, by + 16.0 * flash, 0.6, -1, flash * 0.8);

            for (int b = 0; b < 5; b++) {
               double ang = -0.8 + (b - 2) * 0.18;
               c.glow(40.0 + Math.cos(ang) * 12.0, 44.0 + Math.sin(ang) * 12.0, 1.8, -1, flash * 0.8);
            }
         }
      }
   }

   static final class FairyRing implements AnimatedCapes.Scene {
      private static final double CX = 40.0;
      private static final double CY = 104.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7}, new int[]{-16115158, -15058368});

         for (int i = 0; i < 50; i++) {
            c.star(AnimatedCapes.hash(i, 1) * 80.0, AnimatedCapes.hash(i, 2) * 50.0, 0.0, -2035457, 0.1 + AnimatedCapes.hash(i, 3) * 0.3);
         }

         for (int side = 0; side < 2; side++) {
            for (int i = 0; i < 6; i++) {
               double x = side == 0 ? AnimatedCapes.hash(i, 5) * 20.0 - 4.0 : 64.0 + AnimatedCapes.hash(i, 5) * 20.0;
               AnimatedCapes.pine(
                  c,
                  x,
                  96.0 + AnimatedCapes.hash(i, 6) * 10.0,
                  40.0 + AnimatedCapes.hash(i, 7) * 30.0,
                  AnimatedCapes.lerp(-16115176, -15456220, AnimatedCapes.hash(i, 8))
               );
            }
         }

         c.fillBelow(xx -> 88.0 + 2.0 * Math.sin(xx * 0.1), -14796246);

         for (int y = 86; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= 88.0 + 2.0 * Math.sin(x * 0.1)) {
                  c.set(
                     x,
                     y,
                     AnimatedCapes.dither(
                        AnimatedCapes.lerp(-14005708, -15455714, AnimatedCapes.fbm(x * 0.3, y * 0.3, 3) * 0.6 + Math.hypot(x - 40.0, (y - 104.0) * 2.0) / 80.0),
                        x,
                        y
                     )
                  );
               }
            }
         }

         for (int k = 0; k < 16; k++) {
            double a = k * Math.PI * 2.0 / 16.0;
            double mx = 40.0 + Math.cos(a) * 24.0;
            double my = 104.0 + Math.sin(a) * 8.0;
            double s = 0.8 + (my - 96.0) / 16.0;
            c.rect(mx - 0.4 * s, my - 2.5 * s, 0.8 * s, 2.5 * s, -1515312);
            c.ellipse(mx, my - 2.5 * s, 2.0 * s, 1.3 * s, 0.0, -3134934);
            c.disc(mx - 0.6 * s, my - 3.0 * s, 0.35 * s, -1);
            c.disc(mx + 0.7 * s, my - 2.6 * s, 0.3 * s, -1);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         c.polygon(new double[][]{{30.0, 0.0}, {42.0, 0.0}, {68.0, 112.0}, {12.0, 112.0}}, AnimatedCapes.alpha(-4660993, 0.07 + 0.02 * Math.sin(t * 0.7)));
         c.glow(40.0, 100.0, 30.0, -8323152, 0.15 + 0.05 * Math.sin(t));

         for (int f = 0; f < 7; f++) {
            double a = t * 0.9 + f * Math.PI * 2.0 / 7.0;
            double x = 40.0 + Math.cos(a) * 20.0;
            double y = 86.0 + Math.sin(a) * 6.0 + Math.sin(t * 3.0 + f) * 4.0;
            int col = AnimatedCapes.hsv(80 + f * 40, 0.6, 1.0);

            for (int s = 1; s < 8; s++) {
               double pa = a - s * 0.08;
               double px = 40.0 + Math.cos(pa) * 20.0;
               double py = 86.0 + Math.sin(pa) * 6.0 + Math.sin((t - s * 0.09) * 3.0 + f) * 4.0;
               c.add((int)px, (int)py, col, 0.5 * (1.0 - s / 8.0));
            }

            c.glow(x, y, 5.0, col, 0.8);
            double flap = Math.abs(Math.sin(t * 25.0 + f));
            c.ellipse(x - 1.5, y - 1.0, 1.6 * flap + 0.3, 1.0, 0.5, AnimatedCapes.alpha(-1, 0.7));
            c.ellipse(x + 1.5, y - 1.0, 1.6 * flap + 0.3, 1.0, -0.5, AnimatedCapes.alpha(-1, 0.7));
            c.disc(x, y, 0.9, -1);
         }

         for (int i = 0; i < 20; i++) {
            double life = AnimatedCapes.wrap(t * 0.2 + AnimatedCapes.hash(i, 30), 1.0);
            double x = AnimatedCapes.hash(i, 31) * 80.0 + Math.sin(life * 6.0 + i) * 4.0;
            double y = 110.0 - life * 90.0;
            c.star(x, y, AnimatedCapes.hash(i, 32) > 0.8 ? 1.0 : 0.0, -2031664, 0.6 * Math.sin(life * Math.PI));
         }

         for (int k = 0; k < 5; k++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(k, 40) * 80.0 + t * 3.0, 80.0);
            double y = 60.0 + AnimatedCapes.hash(k, 41) * 30.0 + Math.sin(t + k) * 4.0;
            c.line(x, y, x, y + 1.5, 0.3, AnimatedCapes.alpha(-1, 0.6));

            for (int p = 0; p < 6; p++) {
               double pa = p * Math.PI / 3.0;
               c.line(x, y, x + Math.cos(pa) * 1.2, y + Math.sin(pa) * 1.2, 0.2, AnimatedCapes.alpha(-1, 0.5));
            }
         }
      }
   }

   static final class Hyperspace implements AnimatedCapes.Scene {
      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cycle = AnimatedCapes.wrap(t, 10.0);
         double warp = cycle < 2.0 ? AnimatedCapes.smoothstep(0.0, 2.0, cycle) : (cycle < 8.0 ? 1.0 : 1.0 - AnimatedCapes.smoothstep(8.0, 10.0, cycle));
         c.fill(-16645366);
         c.glow(40.0, 50.0, 40.0, -14005568, 0.3 + 0.4 * warp);

         for (int i = 0; i < 170; i++) {
            double z = AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) - t * (0.08 + warp * 0.9), 1.0) + 0.02;
            double a = AnimatedCapes.hash(i, 2) * Math.PI * 2.0;
            double r0 = 4.0 / z;
            double stretch = 1.0 + warp * 1.4 * (1.0 - z);
            double r1 = r0 * stretch;
            double x0 = 40.0 + Math.cos(a) * r0;
            double y0 = 50.0 + Math.sin(a) * r0;
            double x1 = 40.0 + Math.cos(a) * r1;
            double y1 = 50.0 + Math.sin(a) * r1;
            int col = AnimatedCapes.hash(i, 3) > 0.7 ? -4665089 : -1;
            if (warp < 0.05) {
               c.star(x0, y0, 0.0, col, 0.7 * (1.0 - z));
            } else {
               c.beam(x0, y0, x1, y1, 0.3 + (1.0 - z) * 0.4, col, AnimatedCapes.clamp(1.2 - z, 0.1, 1.0));
            }
         }

         if (cycle > 1.9 && cycle < 2.2) {
            double flash = Math.sin((cycle - 1.9) / 0.3 * Math.PI);
            c.glow(40.0, 50.0, 60.0, -1, flash * 1.5);
         }

         c.polygon(
            new double[][]{{-2.0, 92.0}, {82.0, 92.0}, {82.0, 130.0}, {-2.0, 130.0}}, (xx, y) -> AnimatedCapes.lerp(-14012872, -15592422, (y - 92) / 36.0)
         );
         c.polygon(new double[][]{{-2.0, 0.0}, {10.0, 0.0}, {-2.0, 70.0}}, -15066076);
         c.polygon(new double[][]{{82.0, 0.0}, {70.0, 0.0}, {82.0, 70.0}}, -15066076);
         c.polygon(new double[][]{{-2.0, 92.0}, {18.0, 82.0}, {62.0, 82.0}, {82.0, 92.0}}, -12960182);
         c.line(18.0, 82.0, 62.0, 82.0, 0.6, -9801600);

         for (int k = 0; k < 12; k++) {
            double bx = 8.0 + k * 5.6;
            double by = 98 + k % 3 * 6;
            boolean lit = AnimatedCapes.hash(k, (int)Math.floor(t * (1 + k % 4))) > 0.4;
            int col = new int[]{-49088, -12517504, -12224, -12533505}[k % 4];
            c.rect(bx, by, 3.0, 2.0, lit ? col : AnimatedCapes.shade(col, 0.3));
            if (lit) {
               c.glow(bx + 1.5, by + 1.0, 3.0, col, 0.3);
            }
         }

         c.rect(28.0, 110.0, 24.0, 12.0, -16115180);

         for (int x = 0; x < 24; x++) {
            double wave = Math.sin(x * 0.5 + t * 4.0) * 3.0 * (0.3 + warp);
            c.set(28 + x, (int)(116.0 + wave), -12517488);
         }

         c.rect(38.0, 88.0, 4.0, 4.0, -11907494);
         c.line(40.0, 88.0, 40.0 - Math.sin(t * 0.7) * 3.0, 80.0, 1.2, -8749430);
      }
   }

   static final class Mars implements AnimatedCapes.Scene {
      private static final int GROUND = 88;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.5, 0.68}, new int[]{-11912646, -4158870, -1525616});
         c.glow(22.0, 30.0, 10.0, -3088144, 0.35);
         c.disc(22.0, 30.0, 2.2, -1511176);
         AnimatedCapes.mountains(c, 51, 80.0, 20.0, 0.05, -5214134, 0);

         for (int y = 50; y < 86; y++) {
            for (int x = 0; x < 80; x++) {
               c.blend(x, y, AnimatedCapes.alpha(-2054016, 0.35 * (1.0 - (y - 50) / 36.0)));
            }
         }

         c.polygon(new double[][]{{50.0, 88.0}, {54.0, 70.0}, {76.0, 70.0}, {80.0, 88.0}}, (xx, yx) -> yx % 4 < 2 ? -6661574 : -7714256);
         c.rect(54.0, 69.0, 22.0, 1.5, -4691384);

         for (int y = 86; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.lerp(-4691390, -9817056, (y - 88) / 40.0 + AnimatedCapes.fbm(x * 0.2, y * 0.25, 3) * 0.3), x, y));
            }
         }

         for (int i = 0; i < 40; i++) {
            double y = 90.0 + Math.pow(AnimatedCapes.hash(i, 5), 1.4) * 38.0;
            double x = AnimatedCapes.hash(i, 6) * 80.0;
            double r = 0.6 + (y - 88.0) / 18.0 + AnimatedCapes.hash(i, 7);
            c.ellipse(x + r * 0.6, y + r * 0.4, r * 1.2, r * 0.35, 0.0, AnimatedCapes.alpha(-12969456, 0.5));
            c.ellipse(x, y, r, r * 0.65, 0.0, AnimatedCapes.lerp(-8762828, -10865628, AnimatedCapes.hash(i, 8)));
            c.ellipse(x - r * 0.3, y - r * 0.3, r * 0.4, r * 0.2, 0.0, -5738416);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double phobos = AnimatedCapes.wrap(t * 1.2, 100.0) - 10.0;
         c.ellipse(phobos, 12.0, 1.6, 1.1, 0.3, -9807280);
         double devilX = AnimatedCapes.wrap(t * 2.5 + 75.0, 120.0) - 20.0;

         for (int y = 40; y < 92; y++) {
            double f = (y - 40) / 52.0;
            double cx = devilX + Math.sin(y * 0.15 + t * 3.0) * 2.0 * (1.0 - f);
            double w = 7.0 - f * 5.0;

            for (int x = (int)(cx - w); x <= cx + w; x++) {
               double u = (x + 0.5 - cx) / w;
               double swirl = 0.5 + 0.5 * Math.sin(Math.asin(AnimatedCapes.clamp(u, -1.0, 1.0)) * 3.0 + t * 10.0 + y * 0.4);
               c.blend(x, y, AnimatedCapes.alpha(-2580360, (1.0 - Math.abs(u)) * 0.35 * (0.5 + swirl * 0.5) * f));
            }
         }

         double rx = AnimatedCapes.wrap(t * 1.6 + 50.0, 120.0) - 20.0;
         double ry = 108.0;
         c.ellipse(rx + 2.0, ry + 6.0, 10.0, 1.5, 0.0, AnimatedCapes.alpha(-12969456, 0.5));
         c.rect(rx - 7.0, ry - 3.0, 14.0, 5.0, -1514276);
         c.rect(rx - 7.0, ry - 3.0, 14.0, 1.0, -1);
         c.rect(rx - 6.0, ry - 7.0, 5.0, 4.0, -3619656);
         c.rect(rx + 4.0, ry - 14.0, 1.0, 11.0, -2566964);
         c.rect(rx + 2.5, ry - 16.0, 4.0, 2.5, -1514276);
         c.rect(rx + 5.5, ry - 15.5, 1.0, 1.0, -15062454);
         c.line(rx - 7.0, ry - 1.0, rx - 11.0, ry - 5.0 + Math.sin(t) * 1.5, 0.5, -5198688);

         for (int w = 0; w < 3; w++) {
            double wx = rx - 5.5 + w * 5.5;
            c.disc(wx, ry + 4.0, 2.0, -14013906);
            c.disc(wx, ry + 4.0, 0.8, -7697776);
            double s = t * 2.0 + w;
            c.line(wx, ry + 4.0, wx + Math.cos(s) * 1.8, ry + 4.0 + Math.sin(s) * 1.8, 0.3, -9803152);
         }

         c.line(rx - 5.5, ry + 1.0, rx + 5.5, ry + 1.0, 0.6, -9803152);
         double hx = 30.0 + Math.sin(t * 0.4) * 16.0;
         double hy = 60.0 + Math.sin(t * 0.9) * 5.0;
         c.ellipse(hx, 114.0 - (hy - 60.0) * 0.2, 3.0, 0.7, 0.0, AnimatedCapes.alpha(-12969456, 0.35));
         c.rect(hx - 2.0, hy - 1.5, 4.0, 3.0, -2570072);
         c.line(hx, hy - 1.5, hx, hy - 5.0, 0.4, -11908528);

         for (int blade = 0; blade < 2; blade++) {
            double spin = t * 40.0 + blade * Math.PI / 2.0;
            c.ellipse(hx, hy - 4.0 - blade, 8.0, 0.6, Math.sin(spin) * 0.08, AnimatedCapes.alpha(-9803152, 0.45));
         }

         for (int leg = -1; leg <= 1; leg += 2) {
            c.line(hx + leg, hy + 1.5, hx + leg * 3.5, hy + 4.5, 0.4, -9803152);
         }

         for (int i = 0; i < 40; i++) {
            double x = AnimatedCapes.wrap(AnimatedCapes.hash(i, 30) * 80.0 + t * (10.0 + AnimatedCapes.hash(i, 31) * 10.0), 80.0);
            double y = 70.0 + AnimatedCapes.hash(i, 32) * 58.0 + Math.sin(t * 2.0 + i) * 1.5;
            c.add((int)x, (int)y, -1525616, 0.25);
         }
      }
   }

   static final class SkyIslands implements AnimatedCapes.Scene {
      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7, 1.0}, new int[]{-12940576, -4662024, -985348});

         for (int k = 0; k < 4; k++) {
            double x = 10.0 + AnimatedCapes.hash(k, 1) * 60.0;
            double y = 20.0 + AnimatedCapes.hash(k, 2) * 40.0;
            c.ellipse(x, y, 6.0, 1.5, 0.0, AnimatedCapes.alpha(-7687984, 0.5));
            c.polygon(new double[][]{{x - 6.0, y}, {x + 6.0, y}, {x, y + 6.0}}, AnimatedCapes.alpha(-7687984, 0.5));
         }
      }

      private static void island(AnimatedCapes.Canvas c, double x, double y, double w, int seed, boolean house) {
         c.polygon(
            new double[][]{
               {x - w, y},
               {x + w, y},
               {x + w * 0.6, y + w * 0.5},
               {x + w * 0.2, y + w * 0.9},
               {x - w * 0.1, y + w * 1.3},
               {x - w * 0.4, y + w * 0.7},
               {x - w * 0.8, y + w * 0.4}
            },
            (px, py) -> AnimatedCapes.lerp(-7706038, -11914204, (py - y) / (w * 1.3) + AnimatedCapes.fbm(px * 0.3 + seed, py * 0.3, 2) * 0.3)
         );

         for (int r = 0; r < 4; r++) {
            double rx = x - w * 0.6 + r * w * 0.4;
            double len = w * (0.4 + AnimatedCapes.hash(r, seed) * 0.6);
            c.line(rx, y + w * 0.3, rx + Math.sin(r + seed) * 2.0, y + w * 0.3 + len, 0.5, -10862552);
         }

         c.disc(x + w * 0.1, y + w * 0.8, 1.2, -7675649);
         c.ellipse(x, y, w, w * 0.18, 0.0, -11886534);
         c.ellipse(x - w * 0.2, y - w * 0.06, w * 0.7, w * 0.08, 0.0, -9781174);

         for (int k = 0; k < (int)(w / 3.0); k++) {
            double tx = x - w * 0.8 + AnimatedCapes.hash(k, seed + 5) * w * 1.6;
            c.rect(tx - 0.4, y - 3.0, 0.8, 3.0, -10864096);
            c.disc(tx, y - 4.0, 1.8 + AnimatedCapes.hash(k, seed + 6), AnimatedCapes.lerp(-13993430, -11886534, AnimatedCapes.hash(k, seed + 7)));
         }

         if (house) {
            c.rect(x + w * 0.2, y - 5.0, 6.0, 5.0, -1516344);
            c.polygon(new double[][]{{x + w * 0.2 - 1.0, y - 5.0}, {x + w * 0.2 + 7.0, y - 5.0}, {x + w * 0.2 + 3.0, y - 9.0}}, -4175302);
            c.rect(x + w * 0.2 + 2.5, y - 3.0, 1.5, 3.0, -9811414);
         }
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         for (int y = 100; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05 + t * 0.05, y * 0.1, 4);
               c.blend(x, y, AnimatedCapes.alpha(AnimatedCapes.lerp(-1, -3614484, n), AnimatedCapes.smoothstep(0.3, 0.6, n + (y - 100) / 40.0)));
            }
         }

         double bobA = Math.sin(t * 0.6) * 1.5;
         double bobB = Math.sin(t * 0.5 + 2.0) * 1.2;
         double bobC = Math.sin(t * 0.7 + 4.0) * 1.0;
         island(c, 62.0, 30.0 + bobB, 11.0, 3, false);
         island(c, 14.0, 50.0 + bobC, 8.0, 7, false);
         double mainY = 72.0 + bobA;

         for (int y = (int)mainY; y < 128; y++) {
            double f = (y - mainY) / (128.0 - mainY);
            double x0 = 52.0 + Math.sin(y * 0.1) * 0.5;

            for (int x = (int)x0 - 2; x <= x0 + 1.0 + f * 3.0; x++) {
               double streak = AnimatedCapes.noise(x * 0.8, y * 0.2 - t * 4.0);
               c.blend(x, y, AnimatedCapes.alpha(AnimatedCapes.lerp(-7679760, -1, streak), 0.85 * (1.0 - f)));
            }
         }

         island(c, 40.0, mainY, 18.0, 11, true);

         for (int s = 0; s < 8; s++) {
            double life = AnimatedCapes.wrap(t * 0.6 + s / 8.0, 1.0);
            c.disc(52.0 + (AnimatedCapes.hash(s, 40) - 0.5) * 8.0, mainY + 30.0 + life * 40.0, 1.0 + life * 3.0, AnimatedCapes.alpha(-1, 0.4 * (1.0 - life)));
         }

         double ax = AnimatedCapes.wrap(t * 3.0, 130.0) - 25.0;
         double ay = 16.0 + Math.sin(t * 0.8) * 2.0;
         c.ellipse(ax, ay, 12.0, 4.5, 0.0, -4691398);
         c.ellipse(ax, ay - 1.5, 11.0, 2.2, 0.0, -2586032);

         for (int k = -2; k <= 2; k++) {
            c.line(ax + k * 4, ay - 4.0, ax + k * 4.5, ay + 4.0, 0.3, AnimatedCapes.alpha(-8762838, 0.8));
         }

         c.line(ax - 5.0, ay + 4.0, ax - 3.0, ay + 8.0, 0.4, -11912662);
         c.line(ax + 5.0, ay + 4.0, ax + 3.0, ay + 8.0, 0.4, -11912662);
         c.rect(ax - 4.0, ay + 8.0, 8.0, 3.0, -9811414);
         double spin = Math.sin(t * 20.0);
         c.ellipse(ax - 13.0, ay, 0.6, 3.0 * Math.abs(spin) + 0.3, 0.0, -12961216);
         c.polygon(new double[][]{{ax + 12.0, ay}, {ax + 16.0, ay - 4.0}, {ax + 16.0, ay + 4.0}}, -6268368);

         for (int k = 0; k < 3; k++) {
            double x = AnimatedCapes.wrap(t * (5 + k * 2) + k * 30, 110.0) - 15.0;
            AnimatedCapes.cloud(c, x, 60 + k * 18, 3 + k, AnimatedCapes.alpha(-1, 0.9), AnimatedCapes.alpha(-2562832, 0.85), k + 20);
         }

         for (int b = 0; b < 3; b++) {
            AnimatedCapes.bird(c, AnimatedCapes.wrap(t * 6.0 + b * 8, 100.0) - 10.0, 46 + b * 3, 1.3, t * 8.0 + b, -1070974390);
         }
      }
   }

   static final class Ufo implements AnimatedCapes.Scene {
      private static final int FIELD = 92;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.7}, new int[]{-16447464, -15063992});

         for (int i = 0; i < 120; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 92.0,
               AnimatedCapes.hash(i, 3) > 0.96 ? 1.0 : 0.0,
               -1512193,
               0.1 + AnimatedCapes.hash(i, 4) * 0.45
            );
         }

         c.disc(12.0, 14.0, 5.0, -986912);
         c.disc(14.0, 13.0, 4.5, -16117724);
         c.fillBelow(xx -> 92.0 - 3.0 * Math.sin(xx * 0.05), -12961254);

         for (int y = 88; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               if (y >= 92.0 - 3.0 * Math.sin(x * 0.05)) {
                  double rows = Math.sin((x - 40) * 22.0 / (y - 70));
                  double ring = Math.hypot((x - 40) / 1.0, (y - 112) * 2.6);
                  int col = AnimatedCapes.lerp(-11910112, -14014960, 0.5 + 0.3 * rows + (y - 92) / 90.0);
                  if (Math.abs(ring - 20.0) < 2.0 || Math.abs(ring - 34.0) < 1.5 || ring < 8.0) {
                     col = AnimatedCapes.lerp(col, -8753088, 0.6);
                  }

                  c.set(x, y, AnimatedCapes.dither(col, x, y));
               }
            }
         }

         c.rect(58.0, 76.0, 14.0, 14.0, -11920876);
         c.polygon(new double[][]{{56.0, 76.5}, {74.0, 76.5}, {65.0, 68.0}}, -14021110);
         c.rect(63.0, 82.0, 4.0, 8.0, -15070714);
         c.rect(74.0, 62.0, 5.0, 28.0, -9803152);
         c.ellipse(76.5, 62.0, 2.5, 1.5, 0.0, -7697776);

         for (int xx = 0; xx < 80; xx += 5) {
            c.rect(xx, 94.0, 0.8, 5.0, -14016492);
         }

         c.rect(0.0, 95.5, 80.0, 0.6, -14016492);
      }

      private static void cow(AnimatedCapes.Canvas c, double x, double y, double tilt) {
         double cos = Math.cos(tilt);
         double sin = Math.sin(tilt);
         c.polygon(
            new double[][]{
               {x - 5.0 * cos + 3.0 * sin, y - 5.0 * sin - 3.0 * cos},
               {x + 5.0 * cos + 3.0 * sin, y + 5.0 * sin - 3.0 * cos},
               {x + 5.0 * cos - 3.0 * sin, y + 5.0 * sin + 3.0 * cos},
               {x - 5.0 * cos - 3.0 * sin, y - 5.0 * sin + 3.0 * cos}
            },
            -724760
         );
         c.disc(x - 1.5 * cos, y - 1.5 * sin - 0.5, 1.6, -15066598);
         c.disc(x + 2.5 * cos, y + 2.5 * sin + 0.8, 1.2, -15066598);

         for (int leg = -1; leg <= 1; leg += 2) {
            for (int pair = 0; pair < 2; pair++) {
               double lx = x + leg * (3.5 - pair * 1.2) * cos;
               double ly = y + leg * (3.5 - pair * 1.2) * sin;
               c.line(lx - 3.0 * sin, ly + 3.0 * cos, lx - 5.5 * sin + Math.sin(tilt * 5.0 + leg) * 0.6, ly + 5.5 * cos, 0.6, -1514276);
            }
         }

         c.disc(x + 6.2 * cos - 1.0 * sin, y + 6.2 * sin + 1.0 * cos - 1.0, 2.0, -724760);
         c.ellipse(x + 7.4 * cos - 0.4 * sin, y + 7.4 * sin - 0.3, 1.2, 0.9, tilt, -1005392);
         c.rect(x + 6.0 * cos - 1.5 * sin, y + 6.0 * sin - 2.6, 0.6, 0.6, -16119286);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double ux = 40.0 + Math.sin(t * 0.5) * 4.0;
         double uy = 30.0 + Math.sin(t * 1.3) * 1.5;
         double cycle = AnimatedCapes.wrap(t, 9.0);
         double beam = cycle < 7.5 ? AnimatedCapes.smoothstep(0.0, 0.5, cycle) : 1.0 - AnimatedCapes.smoothstep(7.5, 8.2, cycle);
         if (beam > 0.0) {
            c.polygon(new double[][]{{ux - 5.0, uy + 3.0}, {ux + 5.0, uy + 3.0}, {56.0, 116.0}, {24.0, 116.0}}, (x, y) -> {
               double band = 0.5 + 0.5 * Math.sin(y * 0.8 + t * 8.0);
               return AnimatedCapes.alpha(AnimatedCapes.lerp(-10420288, -4128784, band), (0.18 + band * 0.1) * beam);
            });
            c.ellipse(40.0, 114.0, 16.0, 3.0, 0.0, AnimatedCapes.alpha(-8323120, 0.35 * beam));
         }

         if (cycle < 7.5) {
            double p = AnimatedCapes.smoothstep(0.8, 7.2, cycle);
            double cy = 108.0 - p * 72.0;
            cow(c, 40.0 + Math.sin(t * 1.4) * 2.0 * p, cy, Math.sin(t * 0.9) * 0.6 * p);
            if (p > 0.0 && p < 1.0) {
               for (int s = 0; s < 8; s++) {
                  double life = AnimatedCapes.wrap(t * 0.8 + s / 8.0, 1.0);
                  c.star(
                     40.0 + (AnimatedCapes.hash(s, (int)(t * 0.8)) - 0.5) * 22.0 * (1.0 - life * 0.6), 114.0 - life * 80.0, 1.0, -5177376, 0.8 * (1.0 - life)
                  );
               }
            }
         } else {
            c.glow(ux, uy + 2.0, 12.0, -1, 1.2 * (1.0 - (cycle - 7.5) / 1.5));
         }

         c.glow(ux, uy, 22.0, -9772864, 0.25);
         c.ellipse(ux, uy - 3.0, 5.5, 4.5, 0.0, AnimatedCapes.alpha(-6231824, 0.85));
         c.ellipse(ux - 1.5, uy - 4.5, 2.0, 1.2, -0.4, AnimatedCapes.alpha(-1, 0.6));
         c.ellipse(ux, uy, 15.0, 3.5, 0.0, -7696228);
         c.ellipse(ux, uy - 0.8, 14.0, 2.4, 0.0, -3090720);
         c.ellipse(ux, uy + 1.8, 9.0, 1.6, 0.0, -11906980);

         for (int k = 0; k < 8; k++) {
            double a = t * 3.0 + k * Math.PI / 4.0;
            double depth = Math.sin(a);
            if (depth > -0.2) {
               c.glow(ux + Math.cos(a) * 13.0, uy + 0.5 + depth * 1.5, 2.0, AnimatedCapes.hsv(k * 45 + t * 60.0, 0.8, 1.0), 0.9 * (depth + 0.2));
            }
         }

         if (AnimatedCapes.hash((int)Math.floor(t * 4.0), 7) > 0.2) {
            c.rect(62.0, 79.0, 2.0, 2.0, -10128);
         }
      }
   }

   static final class WizardTower implements AnimatedCapes.Scene {
      private static final double TX = 46.0;

      @Override
      public void base(AnimatedCapes.Canvas c) {
         c.sky(new double[]{0.0, 0.6, 1.0}, new int[]{-16120288, -14017974, -12965288});

         for (int i = 0; i < 110; i++) {
            c.star(
               AnimatedCapes.hash(i, 1) * 80.0,
               AnimatedCapes.hash(i, 2) * 100.0,
               AnimatedCapes.hash(i, 3) > 0.96 ? 1.0 : 0.0,
               -1515265,
               0.1 + AnimatedCapes.hash(i, 4) * 0.45
            );
         }

         c.glow(16.0, 24.0, 20.0, -6254384, 0.3);
         c.disc(16.0, 24.0, 9.0, -988424);
         c.disc(13.0, 21.0, 2.0, AnimatedCapes.alpha(-3620648, 0.7));
         c.disc(19.0, 27.0, 1.4, AnimatedCapes.alpha(-3620648, 0.6));
         AnimatedCapes.mountains(c, 61, 112.0, 20.0, 0.05, -14804940, 0);
         c.polygon(
            new double[][]{{20.0, 130.0}, {26.0, 110.0}, {34.0, 104.0}, {60.0, 102.0}, {70.0, 110.0}, {82.0, 116.0}, {82.0, 130.0}},
            (x, y) -> AnimatedCapes.lerp(-14015440, -15462374, AnimatedCapes.fbm(x * 0.15, y * 0.15, 3))
         );
         c.polygon(new double[][]{{37.0, 104.0}, {55.0, 104.0}, {53.0, 36.0}, {39.0, 36.0}}, (x, y) -> {
            int row = (y - 36) / 3;
            boolean mortar = y % 3 == 0 || (int)(x + row % 2 * 2.5) % 5 == 0;
            int stone = AnimatedCapes.lerp(-9804680, -12962744, (x - 46.0 + 9.0) / 18.0);
            return mortar ? AnimatedCapes.shade(stone, 0.7) : AnimatedCapes.shade(stone, 0.92 + AnimatedCapes.hash(row, x / 5) * 0.16);
         });
         c.polygon(new double[][]{{35.0, 37.0}, {57.0, 37.0}, {46.0, 8.0}}, (x, y) -> {
            boolean tile = y % 3 == 0;
            return AnimatedCapes.shade(AnimatedCapes.lerp(-10868086, -12969382, (x - 46.0 + 11.0) / 22.0), tile ? 0.75 : 1.0);
         });
         c.rect(34.0, 36.0, 24.0, 2.0, -11910056);
         c.rect(32.0, 60.0, 8.0, 1.5, -11910056);
         c.line(32.0, 60.0, 32.0, 57.0, 0.5, -11910056);

         for (int k = 0; k < 4; k++) {
            c.line(32.5 + k * 2, 60.0, 32.5 + k * 2, 57.5, 0.4, -11910056);
         }

         c.rect(32.0, 57.0, 8.0, 0.6, -11910056);
      }

      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double[][] windows = new double[][]{{44.0, 46.0}, {49.0, 66.0}, {43.0, 84.0}};

         for (int k = 0; k < windows.length; k++) {
            double flicker = AnimatedCapes.noise(t * 4.0 + k * 7, k);
            c.polygon(
               new double[][]{
                  {windows[k][0] - 1.5, windows[k][1] + 4.0},
                  {windows[k][0] + 1.5, windows[k][1] + 4.0},
                  {windows[k][0] + 1.5, windows[k][1]},
                  {windows[k][0], windows[k][1] - 1.5},
                  {windows[k][0] - 1.5, windows[k][1]}
               },
               AnimatedCapes.lerp(-26064, -8048, flicker)
            );
            c.glow(windows[k][0], windows[k][1] + 2.0, 7.0, -24512, 0.2 + 0.1 * flicker);
         }

         double pulse = 0.5 + 0.5 * Math.sin(t * 2.5);
         c.line(46.0, 8.0, 46.0, 4.0, 0.6, -7697766);
         c.glow(46.0, 2.0, 10.0 + pulse * 4.0, -10428161, 0.6 + 0.3 * pulse);
         c.disc(46.0, 2.0, 2.2, -2032897);

         for (int i = 0; i < 16; i++) {
            double a = t * 1.5 + i * Math.PI / 8.0;
            double h = AnimatedCapes.wrap(t * 0.3 + i / 16.0, 1.0);
            double r = 12.0 - h * 6.0;
            double x = 46.0 + Math.cos(a) * r;
            double y = 36.0 - h * 30.0;
            if (Math.sin(a) > -0.3 || x < 38.0 || x > 54.0) {
               c.star(x, y, 1.0, -5181185, 0.8 * Math.sin(h * Math.PI));
            }
         }

         double cast = AnimatedCapes.wrap(t, 6.0);
         double raise = cast < 3.0 ? Math.sin(cast / 3.0 * Math.PI) : 0.0;
         double wx = 35.0;
         c.polygon(new double[][]{{wx - 2.0, 60.0}, {wx + 2.0, 60.0}, {wx + 1.0, 52.0}, {wx - 1.0, 52.0}}, -12965254);
         c.disc(wx, 51.0, 1.3, -1517376);
         c.polygon(new double[][]{{wx - 2.0, 51.0}, {wx + 2.0, 51.0}, {wx + 0.5, 45.0}}, -12965254);
         double staffTopX = wx - 3.0 - raise * 2.0;
         double staffTopY = 50.0 - raise * 5.0;
         c.line(wx - 1.5, 58.0, staffTopX, staffTopY, 0.6, -9811414);
         c.glow(staffTopX, staffTopY, 4.0 + raise * 4.0, -32513, 0.5 + raise * 0.5);
         if (raise > 0.5) {
            double p = (raise - 0.5) * 2.0;
            c.beam(staffTopX, staffTopY, staffTopX - 20.0 * p, staffTopY - 30.0 * p, 0.6, -28417, p);
         }

         if (AnimatedCapes.wrap(t, 7.0) < 0.3) {
            int n = (int)Math.floor(t / 7.0);
            double x = 46.0;
            double y = 2.0;

            for (int s = 0; s < 5; s++) {
               double nx = x + (AnimatedCapes.hash(n, s) - 0.5) * 10.0;
               double ny = y - 5.0;
               c.beam(x, y, nx, ny < 0.0 ? 0.0 : ny, 0.5, -5179137, 1.0);
               x = nx;
               y = ny;
            }
         }

         for (int b = 0; b < 3; b++) {
            double a = t * (0.8 + b * 0.2) + b * 2;
            double bx = 46.0 + Math.cos(a) * (18 + b * 4);
            double by = 30.0 + Math.sin(a * 1.3) * 10.0 + b * 6;
            double flap = Math.sin(t * 14.0 + b);
            c.polygon(new double[][]{{bx, by}, {bx - 3.0, by - 1.5 * flap}, {bx - 2.0, by + 0.5}}, -16120304);
            c.polygon(new double[][]{{bx, by}, {bx + 3.0, by - 1.5 * flap}, {bx + 2.0, by + 0.5}}, -16120304);
         }

         for (int y = 90; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double n = AnimatedCapes.fbm(x * 0.05 - t * 0.1, y * 0.12, 3);
               c.blend(x, y, AnimatedCapes.alpha(-7700304, AnimatedCapes.smoothstep(0.5, 0.75, n) * 0.35));
            }
         }
      }
   }

   static final class Wormhole implements AnimatedCapes.Scene {
      @Override
      public void frame(AnimatedCapes.Canvas c, double t) {
         double cx = 40.0 + Math.sin(t * 0.4) * 5.0;
         double cy = 64.0 + Math.cos(t * 0.3) * 8.0;

         for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 80; x++) {
               double dx = x + 0.5 - cx;
               double dy = y + 0.5 - cy;
               double r = Math.max(0.8, Math.hypot(dx, dy * 0.85));
               double a = Math.atan2(dy, dx);
               double depth = 26.0 / r + t * 1.4;
               double twist = a / Math.PI * 3.0 + depth * 0.25 + t * 0.2;
               double n = AnimatedCapes.fbm(depth * 1.2, Math.cos(twist * Math.PI / 1.5) * 1.5 + Math.sin(twist * Math.PI / 1.5) * 1.5 + 4.0, 3);
               double rings = 0.5 + 0.5 * Math.sin(depth * 5.0);
               double streak = Math.pow(0.5 + 0.5 * Math.sin(twist * 6.0), 6.0);
               double v = n * 0.7 + rings * 0.2 + streak * 0.35;
               int col = AnimatedCapes.ramp(v, new double[]{0.2, 0.45, 0.7, 0.95}, new int[]{-16383462, -12969334, -12934913, -1509121});
               double fog = AnimatedCapes.clamp(1.4 - r / 70.0, 0.1, 1.0);
               c.set(x, y, AnimatedCapes.dither(AnimatedCapes.shade(col, fog), x, y));
            }
         }

         c.glow(cx, cy, 16.0, -1, 1.2);
         c.glow(cx, cy, 34.0, -7681793, 0.4);

         for (int i = 0; i < 30; i++) {
            double z = AnimatedCapes.wrap(AnimatedCapes.hash(i, 1) - t * 0.35, 1.0);
            double a = AnimatedCapes.hash(i, 2) * Math.PI * 2.0;
            double r0 = 6.0 / (z + 0.05);
            double r1 = 6.0 / (z + 0.12);
            c.beam(cx + Math.cos(a) * r0, cy + Math.sin(a) * r0, cx + Math.cos(a) * r1, cy + Math.sin(a) * r1, 0.4, -2035457, 0.6 * (1.0 - z));
         }
      }
   }
}
