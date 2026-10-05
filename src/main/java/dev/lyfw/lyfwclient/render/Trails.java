package dev.lyfw.lyfwclient.render;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Trails {
   private static final Trails.Style HELIX = Trails.Style.HELIX;
   private static final Trails.Style BEADS = Trails.Style.BEADS;
   private static final Trails.Style COMET = Trails.Style.COMET;
   private static final Trails.Style SPARKS = Trails.Style.SPARKS;
   private static final Trails.Style WINGS = Trails.Style.WINGS;
   private static final Trails.Style PLUME = Trails.Style.PLUME;
   private static final Trails.Style DASH = Trails.Style.DASH;
   private static final Trails.Style HALO = Trails.Style.HALO;
   private static final Trails.Style BOLT = Trails.Style.BOLT;
   private static final Trails.Style PETALS = Trails.Style.PETALS;
   private static final Trails.Style PRISM = Trails.Style.PRISM;
   private static final Trails.Style FLAME = Trails.Style.FLAME;
   private static final Trails.Style CONSTELLATION = Trails.Style.CONSTELLATION;
   private static final Trails.Style RUNGS = Trails.Style.RUNGS;
   private static final Trails.Flow SOLID = Trails.Flow.SOLID;
   private static final Trails.Flow GRADIENT = Trails.Flow.GRADIENT;
   private static final Trails.Flow RAINBOW = Trails.Flow.RAINBOW;
   private static final Trails.Flow PULSE = Trails.Flow.PULSE;
   private static final Trails.Flow FLICKER = Trails.Flow.FLICKER;
   private static final Trails.Flow SHIMMER = Trails.Flow.SHIMMER;
   public static final List<Trails.Trail> ALL = List.of(
      new Trails.Trail(
         "void",
         "Void",
         -427074602,
         11,
         40,
         HELIX,
         List.of(
            way("violet", "Violet", 9133014),
            way("crimson", "Crimson", 16726862),
            way("cyan", "Cyan", 3989759),
            way("toxic", "Toxic", 10288956),
            way("ink", "Ink", 2760252)
         )
      ),
      new Trails.Trail(
         "magma",
         "Magma",
         -419472866,
         22,
         20,
         COMET,
         List.of(
            way("magma", "Magma", 16734750),
            way("blood", "Blood", 10883626),
            way("sunset", "Sunset", 16747100),
            way("plasma", "Plasma", 12602367),
            way("glacier", "Glacier", 13627391)
         )
      ),
      new Trails.Trail(
         "plasma",
         "Plasma",
         -423605249,
         13,
         32,
         SPARKS,
         List.of(
            way("plasma", "Plasma", 12602367),
            way("frost", "Frost", 9361663),
            way("toxic", "Toxic", 10288956),
            way("solar", "Solar", 16767051),
            way("rose", "Rose", 16739996)
         )
      ),
      new Trails.Trail("spirit", "Spirit", -1191182337, 9, 44, WINGS, List.of()),
      new Trails.Trail(
         "storm",
         "Storm",
         -594565432,
         10,
         38,
         PLUME,
         List.of(
            way("storm", "Storm", 9414344),
            way("abyss", "Abyss", 2374284),
            way("mint", "Mint", 4980648),
            way("ember", "Ember", 16742972),
            way("orchid", "Orchid", 13659135)
         )
      ),
      new Trails.Trail(
         "viper",
         "Viper",
         -595787986,
         13,
         30,
         DASH,
         List.of(
            way("viper", "Viper", 8191790),
            way("neon", "Neon", 3997664),
            way("magma", "Magma", 16734750),
            way("orchid", "Orchid", 13659135),
            way("bone", "Bone", 15262414)
         )
      ),
      new Trails.Trail(
         "abyss",
         "Abyss",
         -534496628,
         10,
         42,
         HALO,
         List.of(
            way("abyss", "Abyss", 2374284),
            way("void", "Void", 9133014),
            way("glacier", "Glacier", 13627391),
            way("jade", "Jade", 3069600),
            way("obsidian", "Obsidian", 2760252)
         )
      ),
      new Trails.Trail(
         "tempest",
         "Tempest",
         -423630337,
         11,
         28,
         BOLT,
         List.of(
            way("tempest", "Tempest", 12577279),
            way("solar", "Solar", 16767051),
            way("crimson", "Crimson", 16726862),
            way("neon", "Neon", 3997664),
            way("dusk", "Dusk", 6970582)
         )
      ),
      new Trails.Trail(
         "bloom",
         "Bloom",
         -520130916,
         12,
         34,
         PETALS,
         List.of(
            way("bloom", "Bloom", 16739996),
            way("lotus", "Lotus", 16754884),
            way("mint", "Mint", 4980648),
            way("amber", "Amber", 16756796),
            way("orchid", "Orchid", 13659135)
         )
      ),
      new Trails.Trail(
         "prism",
         "Prism",
         -532881153,
         12,
         30,
         PRISM,
         List.of(
            way("prism", "Prism", 3989759),
            way("aurora", "Aurora", 12031222),
            way("solar", "Solar", 16767051),
            way("toxic", "Toxic", 10288956),
            way("rose", "Rose", 16739996)
         )
      ),
      new Trails.Trail(
         "ember",
         "Ember",
         -419464644,
         16,
         26,
         FLAME,
         List.of(
            way("ember", "Ember", 16742972),
            way("magma", "Magma", 16734750),
            way("gold", "Gold", 15252026),
            way("plasma", "Plasma", 12602367),
            way("frost", "Frost", 9361663)
         )
      ),
      new Trails.Trail(
         "nebula",
         "Nebula",
         -591948554,
         10,
         42,
         CONSTELLATION,
         List.of(
            way("nebula", "Nebula", 12031222),
            way("abyss", "Abyss", 2374284),
            way("seafoam", "Seafoam", 9240534),
            way("rose", "Rose", 16739996),
            way("bone", "Bone", 15262414)
         )
      ),
      new Trails.Trail(
         "circuit",
         "Circuit",
         -431226968,
         11,
         32,
         RUNGS,
         List.of(
            way("circuit", "Circuit", 4980648),
            way("neon", "Neon", 3997664),
            way("solar", "Solar", 16767051),
            way("viper", "Viper", 8191790),
            way("plasma", "Plasma", 12602367)
         )
      ),
      t("dna", "DNA", -432217857, 10, 42, HELIX, RAINBOW, 0, false),
      t("candy_cane", "Candy Cane", -419480754, 12, 36, HELIX, FLICKER, 16777215, false),
      t("pearls", "Pearls", -420155144, 12, 30, BEADS, SHIMMER, 0, true),
      t("bubbles", "Bubbles", -1198731009, 16, 34, BEADS, GRADIENT, 15268607, false),
      t("wisp", "Wisp", -594739242, 18, 30, COMET, SOLID, 0, true),
      t("inferno", "Inferno", -419440565, 24, 22, COMET, GRADIENT, 16719390, false),
      t("firefly", "Firefly", -421986496, 11, 40, SPARKS, FLICKER, 6982160, true),
      t("glitter", "Glitter", -419440565, 10, 34, SPARKS, SHIMMER, 0, false),
      t("seraph", "Seraph", -922749732, 10, 46, WINGS, GRADIENT, 16767051, true),
      t("raven", "Raven", -602137566, 11, 40, WINGS, GRADIENT, 4864606, false),
      t("dust", "Dust", -925321096, 12, 36, PLUME, GRADIENT, 9071162, false),
      t("poison", "Poison", -593690820, 11, 34, PLUME, PULSE, 0, true),
      t("morse", "Morse", -420154124, 10, 40, DASH, SOLID, 0, true),
      t("highway", "Highway", -419440565, 12, 44, DASH, GRADIENT, 16747034, false),
      t("sonar", "Sonar", -599990017, 10, 44, HALO, PULSE, 0, true),
      t("ripples", "Ripples", -930109752, 12, 40, HALO, GRADIENT, 15267071, false),
      t("thunder", "Thunder", -419434400, 12, 26, BOLT, SOLID, 0, true),
      t("plasma_arc", "Plasma Arc", -423605249, 10, 30, BOLT, FLICKER, 3989759, true),
      t("sakura", "Sakura", -520111920, 13, 40, PETALS, GRADIENT, 16777215, false),
      t("autumn", "Autumn", -421511126, 14, 38, PETALS, FLICKER, 15773744, false),
      t("spectre", "Spectre", -520093697, 12, 34, PRISM, RAINBOW, 0, false),
      t("hologram", "Hologram", -935337729, 11, 32, PRISM, FLICKER, 2059263, true),
      t("soulfire", "Soulfire", -432348929, 16, 28, FLAME, GRADIENT, 1723135, true),
      t("hellfire", "Hellfire", -419480802, 18, 26, FLAME, FLICKER, 16767051, false),
      t("zodiac", "Zodiac", -587212725, 10, 44, CONSTELLATION, SOLID, 0, true),
      t("galaxy", "Galaxy", -592020225, 11, 44, CONSTELLATION, SHIMMER, 0, true),
      t("ladder", "Ladder", -423059350, 12, 34, RUNGS, SOLID, 0, false),
      t("matrix", "Matrix", -432210066, 10, 40, RUNGS, FLICKER, 678430, true),
      t("silk", "Silk", -419444516, 12, 36, Trails.Style.RIBBON, SOLID, 0, false),
      t("velvet", "Velvet", -427091414, 16, 30, Trails.Style.RIBBON, GRADIENT, 3803666, false),
      t("borealis", "Borealis", -431226968, 14, 44, Trails.Style.RIBBON, GRADIENT, 12031222, true),
      t("spectrum", "Spectrum", -419430401, 12, 40, Trails.Style.RIBBON, RAINBOW, 0, false),
      t("tide", "Tide", -432232193, 10, 40, Trails.Style.WAVE, GRADIENT, 670348, false),
      t("siren", "Siren", -419467620, 10, 36, Trails.Style.WAVE, SHIMMER, 0, true),
      t("mirage", "Mirage", -922746881, 11, 42, Trails.Style.WAVE, RAINBOW, 0, true),
      t("voltage", "Voltage", -419437749, 9, 30, Trails.Style.ZIGZAG, PULSE, 0, true),
      t("fang", "Fang", -419430401, 10, 28, Trails.Style.ZIGZAG, GRADIENT, 10883626, false),
      t("glitch", "Glitch", -432209952, 11, 32, Trails.Style.ZIGZAG, FLICKER, 16728020, false),
      t("rope", "Rope", -423059350, 12, 36, Trails.Style.BRAID, SOLID, 0, false),
      t("trinity", "Trinity", -419440565, 10, 40, Trails.Style.BRAID, GRADIENT, 16777215, true),
      t("chroma", "Chroma", -419430401, 11, 38, Trails.Style.BRAID, RAINBOW, 0, false),
      t("rails", "Rails", -424099640, 8, 44, Trails.Style.TWIN, SOLID, 0, false),
      t("tracer", "Tracer", -419480754, 9, 36, Trails.Style.TWIN, GRADIENT, 16767051, false),
      t("laser", "Laser", -432210066, 7, 40, Trails.Style.TWIN, SOLID, 0, true),
      t("pulse", "Pulse", -419480722, 14, 34, Trails.Style.RIPPLE, PULSE, 0, false),
      t("heartbeat", "Heartbeat", -422172612, 12, 38, Trails.Style.RIPPLE, PULSE, 0, true),
      t("jelly", "Jelly", -925849345, 16, 32, Trails.Style.RIPPLE, GRADIENT, 6349055, false),
      t("photon", "Photon", -419430401, 8, 50, Trails.Style.STREAK, SOLID, 0, true),
      t("jet", "Jet", -419450820, 10, 36, Trails.Style.STREAK, GRADIENT, 16726814, false),
      t("ghost", "Ghost", -1765224228, 12, 44, Trails.Style.STREAK, SHIMMER, 0, false),
      t("neon_pink", "Neon Pink", -419479596, 9, 38, Trails.Style.NEON, SOLID, 0, true),
      t("neon_blue", "Neon Blue", -432042753, 9, 38, Trails.Style.NEON, SOLID, 0, true),
      t("neon_lime", "Neon Lime", -424607937, 9, 38, Trails.Style.NEON, SOLID, 0, true),
      t("synthwave", "Synthwave", -419479596, 10, 40, Trails.Style.NEON, GRADIENT, 4190975, true),
      t("smog", "Smog", -592400200, 12, 40, Trails.Style.SMOKE, SOLID, 0, false),
      t("ash", "Ash", -598058398, 12, 40, Trails.Style.SMOKE, GRADIENT, 11579568, false),
      t("incense", "Incense", -591948554, 10, 44, Trails.Style.SMOKE, SHIMMER, 0, false),
      t("steam", "Steam", -1594558209, 14, 34, Trails.Style.SMOKE, SOLID, 0, true),
      t("slime", "Slime", -428015826, 13, 34, Trails.Style.DRIP, SOLID, 0, false),
      t("honey", "Honey", -419450820, 14, 38, Trails.Style.DRIP, GRADIENT, 12610064, false),
      t("ink_drip", "Ink Drip", -432395684, 12, 36, Trails.Style.DRIP, GRADIENT, 9133014, true),
      t("blood_drip", "Blood Drip", -425323990, 12, 34, Trails.Style.DRIP, GRADIENT, 4851216, false),
      t("stitch", "Stitch", -419459460, 9, 36, Trails.Style.STITCH, SOLID, 0, false),
      t("lace", "Lace", -587927304, 8, 42, Trails.Style.STITCH, SHIMMER, 0, true),
      t("barbed", "Barbed", -427126116, 9, 34, Trails.Style.STITCH, GRADIENT, 3817028, false),
      t("serpent", "Serpent", -433138016, 12, 40, Trails.Style.SERPENT, GRADIENT, 678458, false),
      t("hydra", "Hydra", -429248592, 12, 40, Trails.Style.SERPENT, GRADIENT, 3997664, true),
      t("eel", "Eel", -432239361, 10, 44, Trails.Style.SERPENT, PULSE, 0, true),
      t("spine", "Spine", -420945202, 10, 36, Trails.Style.SPINE, SOLID, 0, false),
      t("thorn", "Thorn", -431322566, 10, 34, Trails.Style.SPINE, GRADIENT, 8003102, false),
      t("fishbone", "Fishbone", -422580225, 9, 40, Trails.Style.SPINE, SHIMMER, 0, true),
      t("shards", "Shards", -422580225, 14, 30, Trails.Style.SHARDS, SOLID, 0, true),
      t("obsidian", "Obsidian", -432395684, 14, 30, Trails.Style.SHARDS, GRADIENT, 12031222, true),
      t("ruby", "Ruby", -419480754, 13, 32, Trails.Style.SHARDS, SHIMMER, 0, true),
      t("emerald", "Emerald", -433138016, 13, 32, Trails.Style.SHARDS, GRADIENT, 12124128, false),
      t("frost", "Frost", -590352385, 10, 40, Trails.Style.FROST, SOLID, 0, true),
      t("starlight", "Starlight", -587205440, 10, 44, Trails.Style.FROST, SHIMMER, 0, true),
      t("pixie", "Pixie", -587224892, 9, 38, Trails.Style.FROST, RAINBOW, 0, false),
      t("meteor", "Meteor", -419464644, 16, 30, Trails.Style.METEOR, GRADIENT, 16767051, true),
      t("comet_blue", "Comet Blue", -426845953, 16, 32, Trails.Style.METEOR, SOLID, 0, true),
      t("supernova", "Supernova", -419430401, 18, 28, Trails.Style.METEOR, RAINBOW, 0, true),
      t("echo", "Echo", -594846762, 10, 34, Trails.Style.ECHO, SOLID, 0, false),
      t("afterimage", "Afterimage", -599990017, 10, 30, Trails.Style.ECHO, FLICKER, 16728020, false),
      t("mirror", "Mirror", -924259083, 11, 36, Trails.Style.ECHO, GRADIENT, 6978188, true),
      t("orbit", "Orbit", -419440565, 10, 40, Trails.Style.ORBIT, SOLID, 0, false),
      t("atom", "Atom", -432209952, 9, 40, Trails.Style.ORBIT, PULSE, 0, true),
      t("gyro", "Gyro", -419460582, 10, 36, Trails.Style.ORBIT, GRADIENT, 3832544, false)
   );

   private static Trails.Way way(String id, String label, int rgb) {
      return new Trails.Way(id, label, rgb);
   }

   private static Trails.Trail t(String id, String label, int argb, int width, int length, Trails.Style style, Trails.Flow flow, int tail, boolean glow) {
      return new Trails.Trail(id, label, argb, width, length, style, List.of(), flow, tail, glow);
   }

   private Trails() {
   }

   public static Trails.Trail design(String stored) {
      if (stored != null && !stored.isEmpty()) {
         int dot = stored.indexOf(46);
         String id = dot < 0 ? stored : stored.substring(0, dot);

         for (Trails.Trail trail : ALL) {
            if (trail.id().equals(id)) {
               return trail;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static String wayOf(String stored) {
      int dot = stored == null ? -1 : stored.indexOf(46);
      return dot < 0 ? null : stored.substring(dot + 1);
   }

   public static Trails.Trail byId(String stored) {
      Trails.Trail trail = design(stored);
      return trail == null ? null : trail.inWay(wayOf(stored));
   }

   public static String normalize(String stored) {
      Trails.Trail trail = design(stored);
      return trail == null ? "" : trail.store(wayOf(stored));
   }

   static {
      Set<String> ids = new HashSet<>();

      for (Trails.Trail trail : ALL) {
         if (!ids.add(trail.id()) || !trail.id().matches("[a-z_]+")) {
            throw new IllegalStateException("Bad or repeated trail id " + trail.id());
         }
      }
   }

   public static enum Flow {
      SOLID,
      GRADIENT,
      RAINBOW,
      PULSE,
      FLICKER,
      SHIMMER;
   }

   public static enum Style {
      HELIX,
      BEADS,
      COMET,
      SPARKS,
      WINGS,
      PLUME,
      DASH,
      HALO,
      BOLT,
      PETALS,
      PRISM,
      FLAME,
      CONSTELLATION,
      RUNGS,
      RIBBON,
      WAVE,
      ZIGZAG,
      BRAID,
      TWIN,
      RIPPLE,
      STREAK,
      NEON,
      SMOKE,
      DRIP,
      STITCH,
      SERPENT,
      SPINE,
      SHARDS,
      FROST,
      METEOR,
      ECHO,
      ORBIT;
   }

   public record Trail(
      String id, String label, int argb, int width, int length, Trails.Style style, List<Trails.Way> ways, Trails.Flow flow, int tail, boolean glow
   ) {
      public Trail(String id, String label, int argb, int width, int length, Trails.Style style, List<Trails.Way> ways) {
         this(id, label, argb, width, length, style, ways, Trails.Flow.SOLID, argb, false);
      }

      public Trails.Trail inWay(String wayId) {
         if (this.ways.isEmpty()) {
            return this;
         } else {
            Trails.Way way = this.way(wayId);
            return new Trails.Trail(
               this.id,
               this.label,
               this.argb & 0xFF000000 | way.rgb() & 16777215,
               this.width,
               this.length,
               this.style,
               this.ways,
               this.flow,
               this.tail,
               this.glow
            );
         }
      }

      public Trails.Way way(String wayId) {
         for (Trails.Way way : this.ways) {
            if (way.id().equals(wayId)) {
               return way;
            }
         }

         return this.ways.isEmpty() ? null : this.ways.get(0);
      }

      public String store(String wayId) {
         Trails.Way way = this.way(wayId);
         return way != null && way != this.ways.get(0) ? this.id + "." + way.id() : this.id;
      }
   }

   public record Way(String id, String label, int rgb) {
   }
}
