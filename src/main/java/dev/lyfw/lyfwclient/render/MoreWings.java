package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.function.IntBinaryOperator;

final class MoreWings {
   static final double[][] DRAGON_TIPS = new double[][]{{54.0, 1.0}, {53.0, 19.0}, {42.0, 33.0}, {27.0, 38.0}};

   private MoreWings() {
   }

   static int[] render(CosmeticsModule.WingType type, int base, int tip) {
      WingCanvas c = new WingCanvas();
      switch (type) {
         case SWAN:
            WingsBirds.swan(c, base, tip);
            break;
         case EAGLE:
            WingsBirds.eagle(c, base, tip);
            break;
         case HUMMINGBIRD:
            WingsBirds.hummingbird(c, base, tip);
            break;
         case FLAMINGO:
            WingsBirds.flamingo(c, base, tip);
            break;
         case BLUE_JAY:
            WingsBirds.blueJay(c, base, tip);
            break;
         case ARCHANGEL:
            WingsBirds.archangel(c, base, tip);
            break;
         case SERAPH:
            WingsBirds.seraph(c, base, tip);
            break;
         case FALLEN:
            WingsBirds.fallen(c, base, tip);
            break;
         case VALKYRIE:
            WingsBirds.valkyrie(c, base, tip);
            break;
         case CHERUB:
            WingsBirds.cherub(c, base, tip);
            break;
         case GRIFFIN:
            WingsBirds.griffin(c, base, tip);
            break;
         case WYVERN:
            WingsBirds.wyvern(c, base, tip);
            break;
         case ICE_DRAGON:
            WingsBirds.iceDragon(c, base, tip);
            break;
         case THUNDERBIRD:
            WingsBirds.thunderbird(c, base, tip);
            break;
         case SOUL_FIRE:
            WingsElements.soulFire(c, base, tip);
            break;
         case VOID:
            WingsElements.voidWing(c, base, tip);
            break;
         case STARLIGHT:
            WingsElements.starlight(c, base, tip);
            break;
         case SUNBURST:
            WingsElements.sunburst(c, base, tip);
            break;
         case AURORA:
            WingsElements.aurora(c, base, tip);
            break;
         case STORM_CLOUD:
            WingsElements.stormCloud(c, base, tip);
            break;
         case SAND:
            WingsElements.sand(c, base, tip);
            break;
         case WIND:
            WingsElements.wind(c, base, tip);
            break;
         case EARTH:
            WingsElements.earth(c, base, tip);
            break;
         case CORAL:
            WingsElements.coral(c, base, tip);
            break;
         case SEASHELL:
            WingsElements.seashell(c, base, tip);
            break;
         case FERN:
            WingsElements.fern(c, base, tip);
            break;
         case LOTUS:
            WingsElements.lotus(c, base, tip);
            break;
         case CHERRY_BLOSSOM:
            WingsElements.cherryBlossom(c, base, tip);
            break;
         case MAPLE:
            WingsElements.maple(c, base, tip);
            break;
         case BLUE_MORPHO:
            WingsInsects.blueMorpho(c, base, tip);
            break;
         case GLASSWING:
            WingsInsects.glasswing(c, base, tip);
            break;
         case ATLAS_MOTH:
            WingsInsects.atlasMoth(c, base, tip);
            break;
         case CICADA:
            WingsInsects.cicada(c, base, tip);
            break;
         case FIREFLY:
            WingsInsects.firefly(c, base, tip);
            break;
         case WASP:
            WingsInsects.wasp(c, base, tip);
            break;
         case SCARAB:
            WingsInsects.scarab(c, base, tip);
            break;
         case MANTA:
            WingsInsects.manta(c, base, tip);
            break;
         case FLYING_FISH:
            WingsInsects.flyingFish(c, base, tip);
            break;
         case JELLYFISH:
            WingsInsects.jellyfish(c, base, tip);
            break;
         case SPIDER_WEB:
            WingsInsects.spiderWeb(c, base, tip);
            break;
         case HEART:
            WingsCrafted.heart(c, base, tip);
            break;
         case KITE:
            WingsCrafted.kite(c, base, tip);
            break;
         case PAPER_PLANE:
            WingsCrafted.paperPlane(c, base, tip);
            break;
         case PATCHWORK:
            WingsCrafted.patchwork(c, base, tip);
            break;
         case GOLD_FILIGREE:
            WingsCrafted.goldFiligree(c, base, tip);
            break;
         case CHAINMAIL:
            WingsCrafted.chainmail(c, base, tip);
            break;
         case WOODEN:
            WingsCrafted.wooden(c, base, tip);
            break;
         case LANTERN:
            WingsCrafted.lantern(c, base, tip);
            break;
         case JET:
            WingsCrafted.jet(c, base, tip);
            break;
         case SOLAR:
            WingsCrafted.solar(c, base, tip);
            break;
         case ENERGY_BLADE:
            WingsCrafted.energyBlade(c, base, tip);
            break;
         case HEXGRID:
            WingsCrafted.hexgrid(c, base, tip);
            break;
         case MUSIC:
            WingsCrafted.music(c, base, tip);
            break;
         case OPAL:
            WingsCrafted.opal(c, base, tip);
            break;
         default:
            return null;
      }

      return c.px;
   }

   static double boneX(double t) {
      return 2.0 + 50.0 * t;
   }

   static double boneY(double t) {
      return 10.0 - 8.0 * Math.pow(t, 0.9);
   }

   static void feather(WingCanvas c, double ax, double ay, double angle, double length, double halfWidth, MoreWings.FeatherShade shade) {
      double dx = Math.sin(angle);
      double dy = Math.cos(angle);
      double px = Math.cos(angle);
      double py = -Math.sin(angle);
      int steps = 12;
      double[][] outline = new double[steps * 2 + 2][];

      for (int i = 0; i <= steps; i++) {
         double s = (double)i / steps;
         double profile = s < 0.12 ? 0.5 + s / 0.12 * 0.5 : Math.sqrt(Math.max(0.0, 1.0 - Math.pow((s - 0.12) / 0.88, 3.0)));
         double cx = ax + dx * s * length;
         double cy = ay + dy * s * length;
         outline[i] = new double[]{cx - px * halfWidth * profile, cy - py * halfWidth * profile};
         outline[steps * 2 + 1 - i] = new double[]{cx + px * halfWidth * profile, cy + py * halfWidth * profile};
      }

      c.polygon(outline, (x, y) -> {
         double vx = x + 0.5 - ax;
         double vy = y + 0.5 - ay;
         double along = AnimatedCapes.clamp((vx * dx + vy * dy) / length, 0.0, 1.0);
         double across = AnimatedCapes.clamp((vx * px + vy * py) / halfWidth, -1.0, 1.0);
         return shade.color(along, across);
      });
   }

   static int plume(int color, double along, double across) {
      int shaded = AnimatedCapes.shade(color, across > 0.0 ? 0.82 + 0.1 * (1.0 - across) : 1.05 - 0.08 * Math.abs(across));
      if (Math.abs(across) < 0.12 && along < 0.92) {
         shaded = AnimatedCapes.lerp(shaded, -1, 0.25);
      }

      return shaded;
   }

   static void birdWing(WingCanvas c, MoreWings.FeatherPaint paint, int bone, double length, double width) {
      for (int i = 0; i < 6; i++) {
         int index = i;
         double t = 0.45 + 0.47 * i / 5.0;
         double len = (30.0 - 14.0 * Math.abs(t - 0.72) / 0.27) * length;
         double angle = Math.toRadians(4.0 + 16.0 * (t - 0.45) / 0.47);
         feather(c, boneX(t), boneY(t), angle, len, 3.3 * width, (al, ac) -> paint.color(0, index, al, ac));
      }

      for (int i = 0; i < 7; i++) {
         int index = i;
         double t = 0.02 + 0.48 * i / 6.0;
         double len = (13.0 + 10.0 * (t / 0.5)) * length;
         double angle = Math.toRadians(-8.0 + 12.0 * t / 0.5);
         feather(c, boneX(t), boneY(t) + 1.0, angle, len, 3.0 * width, (al, ac) -> paint.color(1, index, al, ac));
      }

      for (int i = 0; i < 9; i++) {
         int index = i;
         double t = i / 8.0;
         double len = 6.5 + 3.5 * Math.sin(Math.PI * t);
         feather(c, boneX(t), boneY(t) + 0.5, Math.toRadians(12.0), len, 2.6 * width, (al, ac) -> paint.color(2, index, al, ac));
      }

      if (bone != 0) {
         for (double t = 0.0; t <= 1.0; t += 0.01) {
            c.disc(boneX(t), boneY(t), 2.0 - 0.9 * t, bone);
         }
      }
   }

   static double[] primary(int index, double along, double length) {
      double t = 0.45 + 0.47 * index / 5.0;
      double len = (30.0 - 14.0 * Math.abs(t - 0.72) / 0.27) * length;
      double angle = Math.toRadians(4.0 + 16.0 * (t - 0.45) / 0.47);
      return new double[]{boneX(t) + Math.sin(angle) * len * along, boneY(t) + Math.cos(angle) * len * along};
   }

   static void lineOnShape(WingCanvas c, double x0, double y0, double x1, double y1, double width, int argb) {
      double length = Math.max(0.001, Math.hypot(x1 - x0, y1 - y0));
      double dx = (x1 - x0) / length;
      double dy = (y1 - y0) / length;
      double half = width / 2.0;
      double alpha = (argb >>> 24) / 255.0;

      for (int y = (int)Math.floor(Math.min(y0, y1) - half - 1.0); y <= (int)Math.ceil(Math.max(y0, y1) + half + 1.0); y++) {
         for (int x = (int)Math.floor(Math.min(x0, x1) - half - 1.0); x <= (int)Math.ceil(Math.max(x0, x1) + half + 1.0); x++) {
            double along = AnimatedCapes.clamp((x + 0.5 - x0) * dx + (y + 0.5 - y0) * dy, 0.0, length);
            double d = Math.hypot(x + 0.5 - (x0 + dx * along), y + 0.5 - (y0 + dy * along));
            double coverage = AnimatedCapes.clamp(half + 0.5 - d, 0.0, 1.0);
            if (coverage > 0.0) {
               c.onShape(x, y, AnimatedCapes.alpha(argb, coverage * alpha));
            }
         }
      }
   }

   static void silhouette(WingCanvas c, double length, double width) {
      birdWing(c, (layer, index, along, across) -> -1, -1, length, width);
   }

   static void membrane(
      WingCanvas c, double[][] tips, double wristX, double wristY, double bodyY, double bite, IntBinaryOperator skin, int bone, boolean spikes
   ) {
      double[][] outline = new double[tips.length + 3][];
      outline[0] = new double[]{1.5, 10.0};
      outline[1] = new double[]{wristX, wristY};

      for (int i = 0; i < tips.length; i++) {
         outline[2 + i] = tips[i];
      }

      outline[outline.length - 1] = new double[]{2.5, bodyY};
      c.polygon(outline, skin);
      double[][] trailing = new double[tips.length + 1][];
      System.arraycopy(tips, 0, trailing, 0, tips.length);
      trailing[tips.length] = outline[outline.length - 1];

      for (int i = 0; i < trailing.length - 1; i++) {
         double[] a = trailing[i];
         double[] b = trailing[i + 1];
         double mx = (a[0] + b[0]) / 2.0;
         double my = (a[1] + b[1]) / 2.0;
         double gap = Math.hypot(a[0] - b[0], a[1] - b[1]);
         double ox = mx - wristX;
         double oy = my - wristY;
         double ol = Math.max(0.001, Math.hypot(ox, oy));
         double r = gap * 0.5;
         double push = r * (1.0 - bite * 1.8);
         c.clearDisc(mx + ox / ol * push, my + oy / ol * push, r);
      }

      c.rim(0.62);
      int rim = AnimatedCapes.shade(bone, 0.6);
      c.line(1.5, 10.0, wristX, wristY, 2.2, bone);

      for (int i = 0; i < tips.length; i++) {
         c.line(wristX, wristY, tips[i][0], tips[i][1], 1.4 - 0.4 * i / tips.length, bone);
         if (spikes) {
            double dx = tips[i][0] - wristX;
            double dy = tips[i][1] - wristY;
            double len = Math.hypot(dx, dy);
            double ux = dx / len;
            double uy = dy / len;
            c.polygon(
               new double[][]{
                  {tips[i][0] - uy * 1.2, tips[i][1] + ux * 1.2},
                  {tips[i][0] + ux * 3.2, tips[i][1] + uy * 3.2},
                  {tips[i][0] + uy * 1.2, tips[i][1] - ux * 1.2}
               },
               bone
            );
         }
      }

      c.polygon(new double[][]{{wristX - 1.2, wristY + 0.5}, {wristX - 0.6, wristY - 3.5}, {wristX + 1.4, wristY}}, rim);
   }

   static double lobe(double x, double y, double cx, double cy, double rx, double ry, double degrees) {
      double a = Math.toRadians(degrees);
      double dx = x - cx;
      double dy = y - cy;
      double u = (dx * Math.cos(a) + dy * Math.sin(a)) / rx;
      double v = (-dx * Math.sin(a) + dy * Math.cos(a)) / ry;
      return Math.sqrt(u * u + v * v);
   }

   static double forewing(double x, double y) {
      return lobe(x, y, 27.0, 12.0, 25.0, 11.0, -12.0);
   }

   static double hindwing(double x, double y) {
      return lobe(x, y, 18.0, 27.0, 15.0, 10.0, 25.0);
   }

   interface FeatherPaint {
      int color(int i, int j, double d, double e);
   }

   interface FeatherShade {
      int color(double d, double e);
   }
}
