package dev.lyfw.lyfwclient.render;

final class WingsBirds {
   private WingsBirds() {
   }

   static void swan(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(
         c,
         (layer, index, along, across) -> MoreWings.plume(AnimatedCapes.lerp(base, tip, layer == 0 ? along * 0.7 : along * 0.35), along, across),
         AnimatedCapes.shade(base, 0.97F),
         1.12,
         1.05
      );
      c.rim(0.82);
   }

   static void eagle(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(
         c,
         (layer, index, along, across) -> {
            int col = layer == 2
               ? AnimatedCapes.lerp(tip, AnimatedCapes.shade(tip, 0.8F), along)
               : (
                  layer == 1
                     ? AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.8F), along)
                     : AnimatedCapes.lerp(AnimatedCapes.shade(base, 0.9F), AnimatedCapes.shade(base, 0.55F), along)
               );
            if (layer < 2 && (int)(along * 7.0) % 2 == 1) {
               col = AnimatedCapes.shade(col, 0.88F);
            }

            return MoreWings.plume(col, along, across);
         },
         AnimatedCapes.shade(tip, 0.9F),
         1.1,
         0.8
      );

      for (int i = 0; i < 5; i++) {
         double[] a = MoreWings.primary(i, 0.9, 1.1);
         double[] b = MoreWings.primary(i + 1, 0.9, 1.1);
         c.clearDisc((a[0] + b[0]) / 2.0, (a[1] + b[1]) / 2.0 + 1.5, 1.3);
      }

      c.rim(0.7);
   }

   static void hummingbird(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> {
         double x = layer == 0 ? 0.55 + index * 0.08 : (layer == 1 ? index * 0.07 : index * 0.1);
         int col = AnimatedCapes.lerp(base, tip, AnimatedCapes.clamp(x * 0.9 + along * 0.25, 0.0, 1.0));
         col = AnimatedCapes.lerp(col, -1, (0.5 + 0.5 * Math.sin(x * 7.0 + along * 3.0)) * 0.18);
         return MoreWings.plume(col, along, across);
      }, AnimatedCapes.shade(base, 0.8F), 0.95, 0.72);
      c.rim(0.65);
   }

   static void flamingo(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(
         c,
         (layer, index, along, across) -> {
            int col = layer == 0
               ? AnimatedCapes.lerp(tip, AnimatedCapes.shade(tip, 1.6F), 0.2)
               : (layer == 1 ? (along > 0.72 ? tip : AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.85F), along)) : AnimatedCapes.lerp(base, -1, 0.35));
            return MoreWings.plume(col, along, across);
         },
         AnimatedCapes.lerp(base, -1, 0.4),
         1.05,
         1.0
      );
      c.rim(0.72);
   }

   static void blueJay(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> {
         int col = layer == 2 ? AnimatedCapes.lerp(base, -1, 0.2) : base;
         if (layer < 2 && Math.sin(along * 13.0) > 0.82) {
            col = tip;
         }

         if (layer == 1 && along > 0.85) {
            col = -723206;
         }

         return MoreWings.plume(col, along, across);
      }, AnimatedCapes.shade(base, 1.12F), 1.0, 1.0);
      c.rim(0.62);
   }

   static void archangel(WingCanvas c, int base, int tip) {
      for (int i = 0; i < 5; i++) {
         double t = 0.55 + 0.4 * i / 4.0;
         double len = 35.0 - Math.abs(t - 0.75) * 22.0;
         MoreWings.feather(
            c,
            MoreWings.boneX(t),
            MoreWings.boneY(t),
            Math.toRadians(12.0 + 18.0 * (t - 0.55) / 0.4),
            len,
            2.6,
            (along, across) -> Math.abs(across) > 0.8 ? tip : MoreWings.plume(AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.9F), along), along, across)
         );
      }

      MoreWings.birdWing(c, (layer, index, along, across) -> {
         if (Math.abs(across) > 0.78) {
            return AnimatedCapes.shade(tip, 1.05F);
         } else {
            int col = MoreWings.plume(AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.92F), along), along, across);
            return Math.abs(across) < 0.1 && along < 0.9 ? AnimatedCapes.lerp(col, tip, 0.5) : col;
         }
      }, tip, 1.05, 1.05);
      c.rim(0.85);
   }

   static void seraph(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> MoreWings.plume(AnimatedCapes.lerp(base, -5968, along * 0.35), along, across), -1523632, 1.05, 1.1);

      for (int i = 0; i < 6; i++) {
         double[] eye = MoreWings.primary(i, 0.68, 1.05);
         c.disc(eye[0], eye[1], 2.3, -1523632);
         c.disc(eye[0], eye[1], 1.6, -12);
         c.disc(eye[0], eye[1], 1.1, tip);
         c.disc(eye[0], eye[1], 0.45, -16119276);
         c.set((int)(eye[0] - 0.8), (int)(eye[1] - 0.8), -1);
      }

      c.rim(0.78);
   }

   static void fallen(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> {
         int col = AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.7F), along);
         if (along > 0.72) {
            col = AnimatedCapes.lerp(col, tip, (along - 0.72) * 2.6);
         }

         return MoreWings.plume(col, along, across);
      }, AnimatedCapes.shade(base, 1.35F), 1.0, 0.95);

      for (double s = 0.25; s <= 1.0; s += 0.08) {
         double[] p = MoreWings.primary(3, s, 1.0);
         c.clearDisc(p[0], p[1], 1.5);
      }

      for (int k = 0; k < 40; k++) {
         double x = 8.0 + AnimatedCapes.hash(k, 1) * 46.0;
         double y = 10.0 + AnimatedCapes.hash(k, 2) * 30.0;
         int ix = (int)x;
         int iy = (int)y;
         if (c.filled(ix, iy) && (!c.filled(ix + 2, iy + 2) || !c.filled(ix, iy + 3) || !c.filled(ix + 3, iy))) {
            c.clearDisc(x, y, 0.7 + AnimatedCapes.hash(k, 3) * 1.1);
         }
      }

      c.rim(0.55);
   }

   static void valkyrie(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> {
         int col = AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 0.72F), along * 0.6 + (across + 1.0) * 0.2);
         if (across < -0.7) {
            col = AnimatedCapes.lerp(col, -1, 0.5);
         }

         if (Math.abs(across) < 0.12) {
            col = AnimatedCapes.shade(col, 0.7F);
         }

         return layer == 2 ? AnimatedCapes.lerp(col, tip, 0.6) : col;
      }, tip, 1.0, 1.0);

      for (double t = 0.05; t < 1.0; t += 0.11) {
         c.disc(MoreWings.boneX(t), MoreWings.boneY(t) + 3.2, 0.7, AnimatedCapes.shade(tip, 0.65F));
      }

      c.rim(0.5);
   }

   static void cherub(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> MoreWings.plume(AnimatedCapes.lerp(base, tip, along * 0.8), along, across), base, 0.62, 1.4);

      for (int k = 0; k < 50; k++) {
         double x = 3.0 + AnimatedCapes.hash(k, 1) * 46.0;
         double y = 3.0 + AnimatedCapes.hash(k, 2) * 22.0;
         if (c.filled((int)x, (int)y)) {
            c.disc(x, y, 1.1 + AnimatedCapes.hash(k, 3) * 0.6, AnimatedCapes.alpha(-1, 0.35));
         }
      }

      c.rim(0.88);
   }

   static void griffin(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(c, (layer, index, along, across) -> {
         int col;
         if (layer == 2) {
            col = AnimatedCapes.hash(index, (int)(along * 4.0)) > 0.6 ? AnimatedCapes.shade(base, 0.7F) : AnimatedCapes.lerp(base, -8032, 0.25);
         } else if (layer == 1) {
            col = (int)(along * 5.0) % 2 == 1 ? tip : AnimatedCapes.shade(base, 0.85F);
         } else {
            col = along > 0.84 ? -988968 : AnimatedCapes.lerp(AnimatedCapes.shade(tip, 1.4F), tip, along);
         }

         return MoreWings.plume(col, along, across);
      }, AnimatedCapes.lerp(base, -5968, 0.3), 1.08, 0.95);
      c.rim(0.62);
   }

   static void wyvern(WingCanvas c, int base, int tip) {
      double[][] tips = new double[][]{{55.0, 3.0}, {52.0, 17.0}, {44.0, 30.0}, {30.0, 37.0}};
      MoreWings.membrane(c, tips, 21.0, 4.0, 27.0, 0.4, (xx, yx) -> {
         int col = AnimatedCapes.lerp(base, tip, Math.hypot(xx - 21, yx - 4) / 55.0);
         double sx = xx / 3.0;
         double sy = yx / 2.5 + (int)sx % 2 * 0.5;
         double fx = sx - Math.floor(sx) - 0.5;
         double fy = sy - Math.floor(sy);
         return Math.hypot(fx, fy - 0.2) > 0.42 && fy < 0.65 ? AnimatedCapes.shade(col, 0.78F) : col;
      }, AnimatedCapes.lerp(base, -2041688, 0.55), true);

      for (int k = 0; k < 6; k++) {
         double f = (k + 0.5) / 6.0;
         double x = 1.5 + 19.5 * f;
         double y = 10.0 - 6.0 * f;
         c.polygon(new double[][]{{x - 1.2, y}, {x + 1.2, y - 0.4}, {x - 0.5, y - 3.2}}, AnimatedCapes.shade(tip, 0.8F));
      }
   }

   static void iceDragon(WingCanvas c, int base, int tip) {
      MoreWings.membrane(c, MoreWings.DRAGON_TIPS, 22.0, 3.0, 27.0, 0.34, (x, yx) -> {
         double crystal = Math.abs(Math.sin(x * 0.9 + yx * 0.5)) + Math.abs(Math.sin(x * 0.45 - yx * 0.8));
         int col = AnimatedCapes.lerp(base, tip, AnimatedCapes.fbm(x * 0.1, yx * 0.1, 3));
         return crystal > 1.62 ? AnimatedCapes.alpha(AnimatedCapes.lerp(col, -1, 0.6), 0.9) : AnimatedCapes.alpha(col, 0.58);
      }, -984321, true);
      double[][] drips = new double[][]{{27.0, 38.0}, {34.0, 36.0}, {42.0, 33.0}, {48.0, 27.0}, {53.0, 19.0}, {14.0, 31.0}, {20.0, 35.0}};

      for (double[] d : drips) {
         if (c.filled((int)d[0], (int)d[1] - 1)) {
            c.polygon(new double[][]{{d[0] - 1.1, d[1] - 1.0}, {d[0] + 1.1, d[1] - 1.0}, {d[0], d[1] + 3.0}}, AnimatedCapes.alpha(-1509121, 0.9));
         }
      }

      for (int k = 0; k < 10; k++) {
         double x = 8.0 + AnimatedCapes.hash(k, 5) * 44.0;
         double y = 4.0 + AnimatedCapes.hash(k, 6) * 30.0;
         if (c.filled((int)x, (int)y)) {
            c.set((int)x, (int)y, -1);
         }
      }
   }

   static void thunderbird(WingCanvas c, int base, int tip) {
      MoreWings.birdWing(
         c,
         (layer, index, along, across) -> MoreWings.plume(
            AnimatedCapes.lerp(base, AnimatedCapes.shade(base, 1.5), layer == 2 ? 0.35 : along * 0.4), along, across
         ),
         AnimatedCapes.shade(base, 1.4F),
         1.05,
         1.0
      );

      for (int b = 0; b < 3; b++) {
         double x = 4 + b * 3;
         double y = 12 + b * 5;

         for (int s = 0; s < 7; s++) {
            double nx = x + 6.5;
            double ny = y + (s % 2 == 0 ? 3.5 : -2.5) + b * 0.8;
            MoreWings.lineOnShape(c, x, y, nx, ny, 1.8, tip);
            MoreWings.lineOnShape(c, x, y, nx, ny, 0.6, -1);
            x = nx;
            y = ny;
         }
      }

      c.rim(0.6);
   }
}
