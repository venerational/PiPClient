package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.platform.NativeImage;
import dev.lyfw.lyfwclient.gui.CarrotClickGuiScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.render.AnimatedCapes;
import dev.lyfw.lyfwclient.render.CosmeticLoadout;
import dev.lyfw.lyfwclient.render.CosmeticsArt;
import dev.lyfw.lyfwclient.render.CosmeticsFeatureRenderer;
import dev.lyfw.lyfwclient.render.TrailRenderer;
import dev.lyfw.lyfwclient.render.Trails;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ChoiceSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.Setting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import dev.lyfw.lyfwclient.stats.CosmeticSync;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class CosmeticsModule extends Module {
   public static final int CAPE_WIDTH = 10;
   public static final int CAPE_HEIGHT = 16;
   public static final int WING_TEX_W = 56;
   public static final int WING_TEX_H = 40;
   public static final int WING_SPLIT = 20;
   private static final String MOD_ID = "lyfw-client";
   private static final List<CosmeticsModule.CapeInfo> MINECRAFT_CAPES = List.of(
      new CosmeticsModule.CapeInfo("MINECON 2011", "953cac8b779fe41383e675ee2b86071a71658f2180f56fbce8aa315ea70e2ed6"),
      new CosmeticsModule.CapeInfo("MINECON 2012", "a2e8d97ec79100e90a75d369d1b3ba81273c4f82bc1b737e934eed4a854be1b6"),
      new CosmeticsModule.CapeInfo("MINECON 2013", "153b1a0dfcbae953cdeb6f2c2bf6bf79943239b1372780da44bcbb29273131da"),
      new CosmeticsModule.CapeInfo("MINECON 2015", "b0cc08840700447322d953a02b965f1d65a13a603bf64b17c803c21446fe1635"),
      new CosmeticsModule.CapeInfo("MINECON 2016", "e7dfea16dc83c97df01a12fabbd1216359c0cd0ea42f9999b6e97c584963e980"),
      new CosmeticsModule.CapeInfo("Minecraft Experience", "7658c5025c77cfac7574aab3af94a46a8886e3b7722a895255fbf22ab8652434"),
      new CosmeticsModule.CapeInfo("Moonlight Trail", "fe8a02dfe9e390e44ff33d69feef9d3943f76d3901015bbd50f0b67722d288bd"),
      new CosmeticsModule.CapeInfo("Crafter", "479eacefa3cdd7aca94207f36c0dd449653ddf259daf40544a5866baf05eee22"),
      new CosmeticsModule.CapeInfo("Founder's", "99aba02ef05ec6aa4d42db8ee43796d6cd50e4b2954ab29f0caeb85f96bf52a1"),
      new CosmeticsModule.CapeInfo("Cherry Blossom", "afd553b39358a24edfe3b8a9a939fa5fa4faa4d9a9c3d6af8eafb377fa05c2bb"),
      new CosmeticsModule.CapeInfo("Follower's", "569b7f2a1d00d26f30efe3f9ab9ac817b1e6d35f4f3cfb0324ef2d328223d350"),
      new CosmeticsModule.CapeInfo("Purple Heart", "cb40a92e32b57fd732a00fc325e7afb00a7ca74936ad50d8e860152e482cfbde"),
      new CosmeticsModule.CapeInfo("15th Anniversary", "cd9d82ab17fd92022dbd4a86cde4c382a7540e117fae7b9a2853658505a80625"),
      new CosmeticsModule.CapeInfo("MCC 15th Year", "56c35628fe1c4d59dd52561a3d03bfa4e1a76d397c8b9c476c2f77cb6aebb1df"),
      new CosmeticsModule.CapeInfo("Mojang Office", "5c29410057e32abec02d870ecb52ec25fb45ea81e785a7854ae8429d7236ca26"),
      new CosmeticsModule.CapeInfo("Home", "1de21419009db483900da6298a1e6cbf9f1bc1523a0dcdc16263fab150693edd"),
      new CosmeticsModule.CapeInfo("Menace", "dbc21e222528e30dc88445314f7be6ff12d3aeebc3c192054fba7e3b3f8c77b1"),
      new CosmeticsModule.CapeInfo("Yearn", "308b32a9e303155a0b4262f9e5483ad4a22e3412e84fe8385a0bdd73dc41fa89"),
      new CosmeticsModule.CapeInfo("Copper", "5e6f3193e74cd16cdd6637d9bae5484e3a37ff2a14c2d157c659a07810b1bdca"),
      new CosmeticsModule.CapeInfo("Zombie Horse", "a3f6e4f14801f3ea55e3d95b9b4ef3b5e8802d947f669de93d6ec4b9354a436b"),
      new CosmeticsModule.CapeInfo("Builder", "2c579968c64c1719740fd8c2a451461879b238002574fce48f7d1a7c36a1c7d4"),
      new CosmeticsModule.CapeInfo("Migrator", "2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933"),
      new CosmeticsModule.CapeInfo("Vanilla", "f9a76537647989f9a0b6d001e320dac591c359e9e61a31f4ce11c88f207f0ad4"),
      new CosmeticsModule.CapeInfo("Pan", "28de4a81688ad18b49e735a273e086c18f1e3966956123ccb574034c06f5d336"),
      new CosmeticsModule.CapeInfo("Common", "5ec930cdd2629c8771655c60eebeb867b4b6559b0e6d3bc71c40c96347fa03f0"),
      new CosmeticsModule.CapeInfo("Hero", "5fc841ae5a06cb385851e63ee13dccbeb913e8cbffe6a9a1168b69ff601d2188"),
      new CosmeticsModule.CapeInfo("Twisted", "24aafc451aa2cc34ddc7265211678585c0ef4da4d32edb75ecec1bd8b5408381"),
      new CosmeticsModule.CapeInfo("Mojang", "5786fe99be377dfb6858859f926c4dbc995751e91cee373468c5fbf4865e7151"),
      new CosmeticsModule.CapeInfo("Classic Mojang", "8f120319222a9f4a104e2f5cb97b2cda93199a2ee9e1585cb8d09d6f687cb761"),
      new CosmeticsModule.CapeInfo("Mojang Studios", "9e507afc56359978a3eb3e32367042b853cddd0995d17d0da995662913fb00f7"),
      new CosmeticsModule.CapeInfo("Bacon", "fd14214cd8073059e93d9c626260f5df85e5a959181537119df56cadaf5002cc"),
      new CosmeticsModule.CapeInfo("Millionth Customer", "70efffaf86fe5bc089608d3cb297d3e276b9eb7a8f9f2fe6659c23a2d8b18edf"),
      new CosmeticsModule.CapeInfo("dB", "bcfbe84c6542a4a5c213c1cacf8979b5e913dcb4ad783a8b80e3c4a7d5c8bdac"),
      new CosmeticsModule.CapeInfo("Snowman", "23ec737f18bfe4b547c95935fc297dd767bb84ee55bfd855144d279ac9bfd9fe"),
      new CosmeticsModule.CapeInfo("Cheapsh0t's", "ca29f5dd9e94fb1748203b92e36b66fda80750c87ebc18d6eafdb0e28cc1d05f"),
      new CosmeticsModule.CapeInfo("Spade", "2e002d5e1758e79ba51d08d92a0f3a95119f2f435ae7704916507b6c565a7da8"),
      new CosmeticsModule.CapeInfo("Prismarine", "d8f8d13a1adf9636a16c31d47f3ecc9bb8d8533108aa5ad2a01b13b1a0c55eac"),
      new CosmeticsModule.CapeInfo("Turtle", "5048ea61566353397247d2b7d946034de926b997d5e66c86483dfb1e031aee95"),
      new CosmeticsModule.CapeInfo("Birthday", "2056f2eebd759cce93460907186ef44e9192954ae12b227d817eb4b55627a7fc"),
      new CosmeticsModule.CapeInfo("Valentine", "e578ef995fabcf0a94768f9651ac3aaba30c59ef85d2438e9b3e0cc1d810652b"),
      new CosmeticsModule.CapeInfo("Oxeye", "7706b5f5fc90329691e59277dcc66ba20572219fa8e5da472afd5235fad12cc8"),
      new CosmeticsModule.CapeInfo("Blueprint", "fdcf48f01ec480d1d7cbec27f7ddce48c9da2be6724641109444dae58d4cd013"),
      new CosmeticsModule.CapeInfo("Scrolls Champion", "3efadf6510961830f9fcc077f19b4daf286d502b5f5aafbd807c7bbffcaca245"),
      new CosmeticsModule.CapeInfo("Cobalt", "ca35c56efe71ed290385f4ab5346a1826b546a54d519e6a3ff01efa01acce81"),
      new CosmeticsModule.CapeInfo("Translator", "1bf91499701404e21bd46b0191d63239a4ef76ebde88d27e4d430ac211df681e"),
      new CosmeticsModule.CapeInfo("Chinese Translator", "2262fb1d24912209490586ecae98aca8500df3eff91f2a07da37ee524e7e3cb6"),
      new CosmeticsModule.CapeInfo("Moderator", "ae677f7d98ac70a533713518416df4452fe5700365c09cf45d0d156ea9396551"),
      new CosmeticsModule.CapeInfo("Realms MapMaker", "17912790ff164b93196f08ba71d0e62129304776d0f347334f8a6eae509f8a56")
   );
   private final EnumSetting<CosmeticsModule.Headwear> headwear = this.register(new EnumSetting<>("Headwear", CosmeticsModule.Headwear.CAT_EARS));
   private final BooleanSetting naturalHeadwearColors = this.register(new BooleanSetting("Natural Headwear Colors", true));
   private final ColorSetting earColor = this.register(new ColorSetting("Headwear Color", -13948109));
   private final ColorSetting innerEarColor = this.register(new ColorSetting("Accent Color", -1598795));
   private final SliderSetting earSize = this.register(new SliderSetting("Headwear Size", 1.0, 0.4, 2.5, 0.05, "x"));
   private final BooleanSetting wings = this.register(new BooleanSetting("Wings", false));
   private final EnumSetting<CosmeticsModule.WingType> wingType = this.register(new EnumSetting<>("Wing Type", CosmeticsModule.WingType.ANGEL));
   private final BooleanSetting naturalWingColors = this.register(new BooleanSetting("Natural Wing Colors", true));
   private final ColorSetting wingColor = this.register(new ColorSetting("Wing Color", -1512203));
   private final ColorSetting wingTipColor = this.register(new ColorSetting("Wing Tip Color", -6310168));
   private final SliderSetting wingSize = this.register(new SliderSetting("Wing Size", 1.0, 0.4, 3.0, 0.05, "x"));
   private final SliderSetting flapSpeed = this.register(new SliderSetting("Flap Speed", 1.0, 0.0, 4.0, 0.1, "x"));
   private final BooleanSetting cape = this.register(new BooleanSetting("Cape", false));
   private final EnumSetting<CosmeticsModule.CapeStyle> capeStyle = this.register(new EnumSetting<>("Cape Style", CosmeticsModule.CapeStyle.COLOR));
   private final ChoiceSetting minecraftCape = this.register(new ChoiceSetting("Minecraft Cape", "MINECON 2012", CosmeticsModule::capeNames));
   private final TextSetting animatedCape = this.register(new TextSetting("Animated Cape", "Cherry Blossom Night"));
   private final ColorSetting capeColor = this.register(new ColorSetting("Cape Color", -6610381));
   private final ColorSetting paintColor = this.register(new ColorSetting("Paint Color", -1));
   private final TextSetting capePixels = this.register(new TextSetting("Cape Pixels", ""));
   private final EnumSetting<CosmeticsModule.ShowOn> showOn = this.register(new EnumSetting<>("Show On", CosmeticsModule.ShowOn.EVERYONE));
   private final BooleanSetting showSelf = this.register(new BooleanSetting("Show On Self", true));
   private final BooleanSetting spinPreview = this.register(new BooleanSetting("Spin Preview", true));
   private final SliderSetting renderDistance = this.register(new SliderSetting("Render Distance", 48.0, 8.0, 128.0, 1.0, "blocks"));
   private final TextSetting favoriteCosmetics = this.register(new TextSetting("Favorite Cosmetics", ""));
   private final TextSetting wornHeadwear = this.register(new TextSetting("Worn Headwear", "?"));
   private final TextSetting wornWings = this.register(new TextSetting("Worn Wings", "?"));
   private final TextSetting wornPets = this.register(new TextSetting("Worn Pets", ""));
   private final TextSetting petLooks = this.register(new TextSetting("Pet Looks", ""));
   private final BooleanSetting petTricks = this.register(new BooleanSetting("Pet Tricks", true));
   private final TextSetting wornEmote = this.register(new TextSetting("Emote", ""));
   private final TextSetting wornTrail = this.register(new TextSetting("Trail", ""));
   private final TextSetting wheelEmotes = this.register(new TextSetting("Emote Wheel", ""));
   public static final int WHEEL_MAX = 12;
   private final TextSetting pieceTweaks = this.register(new TextSetting("Cosmetic Tweaks", ""));
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
   private final ExecutorService downloads = Executors.newFixedThreadPool(3, task -> {
      Thread thread = new Thread(task, "Pip Client Capes");
      thread.setDaemon(true);
      return thread;
   });
   private Identifier whiteTexture;
   private final Map<String, CosmeticsModule.WingTexture> wingTextures = new LinkedHashMap<>(16, 0.75F, true);
   private static final int MAX_WING_TEXTURES = 256;
   private final Map<String, CosmeticsModule.PaintedCape> otherCapes = new LinkedHashMap<>(16, 0.75F, true);
   private static final int MAX_OTHER_CAPES = 32;
   private final CosmeticsModule.PaintedCape colorCape = new CosmeticsModule.PaintedCape("cosmetics/cape_color");
   private final CosmeticsModule.PaintedCape drawnCape = new CosmeticsModule.PaintedCape("cosmetics/cape_drawn");
   private final Map<String, Identifier> loadedCapes = new ConcurrentHashMap<>();
   private final Set<String> loadingCapes = ConcurrentHashMap.newKeySet();
   private final Set<String> failedCapes = ConcurrentHashMap.newKeySet();
   private String looksRaw;
   private Map<String, int[]> looks = Map.of();
   private String headwearRaw;
   private List<CosmeticsModule.Headwear> headwearList = List.of();
   private String wingsRaw;
   private List<CosmeticsModule.WingType> wingsList = List.of();
   private String petsRaw;
   private List<CosmeticsModule.Pet> petsList = List.of();
   private String tweaksRaw;
   private Map<String, CosmeticsModule.Tweak> tweaks = Map.of();

   public CosmeticsModule() {
      super("Cosmetics", "Headwear, wings, a cape, pets and a trail, fastened to the player model so they move with it.", Category.RENDER, false);
      this.headwear.group = "Headwear";
      this.naturalHeadwearColors.group = "Headwear";
      this.earColor.group = "Headwear";
      this.innerEarColor.group = "Headwear";
      this.earSize.group = "Headwear";
      this.wings.group = "Wings";
      this.wingType.group = "Wings";
      this.naturalWingColors.group = "Wings";
      this.wingColor.group = "Wings";
      this.wingTipColor.group = "Wings";
      this.wingSize.group = "Wings";
      this.flapSpeed.group = "Wings";
      this.cape.group = "Cape";
      this.capeStyle.group = "Cape";
      this.minecraftCape.group = "Cape";
      this.animatedCape.group = "Cape";
      this.capeColor.group = "Cape";
      this.paintColor.group = "Cape";
      this.capePixels.group = "Cape";
      this.showOn.group = "General";
      this.showSelf.group = "General";
      this.spinPreview.group = "General";
      this.renderDistance.group = "General";
      this.favoriteCosmetics.group = "General";
      this.wornHeadwear.group = "Headwear";
      this.wornWings.group = "Wings";
      this.wornPets.group = "Pets";
      this.petLooks.group = "Pets";
      this.petTricks.group = "Pets";
      this.wornEmote.group = "Emotes";
      this.wornTrail.group = "Trails";
      this.wheelEmotes.group = "Emotes";
      this.pieceTweaks.group = "General";
   }

   public static CosmeticsModule get() {
      return ModuleManager.get("Cosmetics") instanceof CosmeticsModule cosmetics ? cosmetics : null;
   }

   @Override
   public void init() {
      LivingEntityFeatureRendererRegistrationCallback.EVENT.register((LivingEntityFeatureRendererRegistrationCallback)(type, renderer, helper, context) -> {
         if (renderer instanceof AvatarRenderer<?> player) {
            helper.register(new CosmeticsFeatureRenderer(player));
         }
      });
      WorldRenderEvents.END_MAIN.register(TrailRenderer::render);
   }

   @Override
   public void tick() {
      TrailRenderer.tick();
   }

   private static List<String> capeNames() {
      List<String> names = new ArrayList<>();

      for (CosmeticsModule.CapeInfo info : MINECRAFT_CAPES) {
         names.add(info.name());
      }

      return names;
   }

   public List<String> minecraftCapeNames() {
      return capeNames();
   }

   public boolean appliesTo(Player player) {
      Minecraft mc = Minecraft.getInstance();
      boolean justMe = this.showOn.get() == CosmeticsModule.ShowOn.JUST_ME;
      if (!this.isEnabled() || mc.player == null) {
         return false;
      } else if (player == mc.player) {
         return justMe || this.showSelf.get();
      } else if (justMe) {
         return false;
      } else {
         double max = this.renderDistance.get() * this.renderDistance.get();
         return mc.player.distanceToSqr(player) <= max;
      }
   }

   public CosmeticLoadout loadoutFor(Player player) {
      Minecraft mc = Minecraft.getInstance();
      boolean justMe = this.showOn.get() == CosmeticsModule.ShowOn.JUST_ME;
      if (mc.player == null || player == null) {
         return null;
      } else if (player == mc.player) {
         return !this.isEnabled() || !justMe && !this.showSelf.get() ? null : this.outfit();
      } else if (mc.player.distanceToSqr(player) > this.renderDistance.get() * this.renderDistance.get()) {
         return null;
      } else {
         CosmeticLoadout theirs = CosmeticSync.get().of(player.getUUID());
         if (theirs != null) {
            return theirs;
         } else {
            return this.isEnabled() && !justMe ? this.outfit() : null;
         }
      }
   }

   public CosmeticLoadout outfit() {
      List<CosmeticLoadout.Piece<CosmeticsModule.Headwear>> headwear = new ArrayList<>();

      for (CosmeticsModule.Headwear kind : this.wornHeadwear()) {
         headwear.add(this.headwearPiece(kind));
      }

      List<CosmeticLoadout.Piece<CosmeticsModule.WingType>> wings = new ArrayList<>();

      for (CosmeticsModule.WingType type : this.wornWings()) {
         wings.add(this.wingPiece(type));
      }

      List<CosmeticLoadout.Piece<CosmeticsModule.Pet>> pets = new ArrayList<>();

      for (CosmeticsModule.Pet pet : this.wornPets()) {
         pets.add(this.petPiece(pet));
      }

      return new CosmeticLoadout(headwear, wings, this.flapSpeed(), this.cape.get() ? this.myCape() : null, pets, this.trail());
   }

   public String sharedOutfit() {
      return (this.isEnabled() ? this.outfit() : CosmeticLoadout.EMPTY).encode();
   }

   public CosmeticLoadout.Piece<CosmeticsModule.Headwear> headwearPiece(CosmeticsModule.Headwear kind) {
      return new CosmeticLoadout.Piece<>(
         kind, this.pieceSize(kind), this.pieceXAngle(kind), this.pieceYAngle(kind), this.headwearOuter(kind), this.headwearInner(kind)
      );
   }

   public CosmeticLoadout.Piece<CosmeticsModule.WingType> wingPiece(CosmeticsModule.WingType type) {
      return new CosmeticLoadout.Piece<>(type, this.pieceSize(type), this.pieceXAngle(type), this.pieceYAngle(type), this.wingBase(type), this.wingTip(type));
   }

   public CosmeticLoadout.Piece<CosmeticsModule.Pet> petPiece(CosmeticsModule.Pet pet) {
      return new CosmeticLoadout.Piece<>(
         pet, this.pieceSize(pet), this.pieceXAngle(pet), this.pieceYAngle(pet), this.petColor(pet), this.petSpot(pet).ordinal()
      );
   }

   private Map<String, int[]> petLooks() {
      String raw = this.petLooks.get() == null ? "" : this.petLooks.get();
      if (!raw.equals(this.looksRaw)) {
         Map<String, int[]> parsed = new LinkedHashMap<>();

         for (String entry : raw.split(";")) {
            int split = entry.indexOf(61);
            String[] fields = split <= 0 ? new String[0] : entry.substring(split + 1).split(",");
            if (fields.length == 2) {
               try {
                  parsed.put(entry.substring(0, split), new int[]{(int)Long.parseLong(fields[0], 16), Math.max(0, Math.min(2, Integer.parseInt(fields[1])))});
               } catch (NumberFormatException var10) {
               }
            }
         }

         this.looks = parsed;
         this.looksRaw = raw;
      }

      return this.looks;
   }

   public CosmeticsModule.Emote emote() {
      String name = this.wornEmote.get().trim();

      try {
         return name.isEmpty() ? null : CosmeticsModule.Emote.valueOf(name);
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }

   public void setEmote(CosmeticsModule.Emote emote) {
      this.wornEmote.set(emote == null ? "" : emote.name());
   }

   public List<CosmeticsModule.Emote> wheelEmotes() {
      return parseWorn(this.wheelEmotes.get() == null ? "" : this.wheelEmotes.get(), CosmeticsModule.Emote.values(), null);
   }

   public boolean isOnWheel(CosmeticsModule.Emote emote) {
      return this.wheelEmotes().contains(emote);
   }

   public boolean toggleWheel(CosmeticsModule.Emote emote) {
      List<CosmeticsModule.Emote> wheel = new ArrayList<>(this.wheelEmotes());
      if (wheel.contains(emote)) {
         wheel.remove(emote);
      } else {
         if (wheel.size() >= 12) {
            return false;
         }

         wheel.add(emote);
      }

      this.wheelEmotes.set(joinWorn(wheel));
      return true;
   }

   public String trail() {
      return Trails.normalize(this.wornTrail.get());
   }

   public void setTrail(String stored) {
      this.wornTrail.set(Trails.normalize(stored));
   }

   public BooleanSetting petTricksSetting() {
      return this.petTricks;
   }

   public int petColor(CosmeticsModule.Pet pet) {
      int[] look = this.petLooks().get(pet.name());
      return look == null ? 0 : look[0];
   }

   public CosmeticsModule.PetSpot petSpot(CosmeticsModule.Pet pet) {
      int[] look = this.petLooks().get(pet.name());
      return CosmeticsModule.PetSpot.values()[look == null ? 0 : look[1]];
   }

   public void setPetColor(CosmeticsModule.Pet pet, int color) {
      this.setPetLook(pet, color == 0 ? 0 : 0xFF000000 | color, this.petSpot(pet).ordinal());
   }

   public void setPetSpot(CosmeticsModule.Pet pet, CosmeticsModule.PetSpot spot) {
      this.setPetLook(pet, this.petColor(pet), spot.ordinal());
   }

   private void setPetLook(CosmeticsModule.Pet pet, int color, int spot) {
      Map<String, int[]> next = new LinkedHashMap<>(this.petLooks());
      next.put(pet.name(), new int[]{color, spot});
      StringBuilder out = new StringBuilder();

      for (Entry<String, int[]> entry : next.entrySet()) {
         out.append(out.isEmpty() ? "" : ";")
            .append(entry.getKey())
            .append('=')
            .append(String.format(Locale.ROOT, "%08x", entry.getValue()[0]))
            .append(',')
            .append(entry.getValue()[1]);
      }

      this.petLooks.set(out.toString());
   }

   public CosmeticLoadout.Cape myCape() {
      CosmeticsModule.CapeStyle style = this.capeStyle.get();

      String name = switch (style) {
         case MINECRAFT -> (String)this.minecraftCape.get();
         case ANIMATED -> (String)this.animatedCape.get();
         default -> "";
      };
      return new CosmeticLoadout.Cape(style, name, this.capeColor.get(), style == CosmeticsModule.CapeStyle.DRAWN ? this.ownPixels() : "");
   }

   private String ownPixels() {
      String bits = this.capePixels.get();
      int length = 1280;
      return bits != null && bits.length() == length ? bits : "0".repeat(length);
   }

   public List<CosmeticsModule.Headwear> wornHeadwear() {
      String raw = this.wornHeadwear.get();
      if (raw != null && !raw.equals("?")) {
         if (!raw.equals(this.headwearRaw)) {
            this.headwearList = parseWorn(raw, CosmeticsModule.Headwear.values(), CosmeticsModule.Headwear.NONE);
            this.headwearRaw = raw;
         }

         return this.headwearList;
      } else {
         CosmeticsModule.Headwear old = this.headwear.get();
         return old != null && old != CosmeticsModule.Headwear.NONE ? List.of(old) : List.of();
      }
   }

   public List<CosmeticsModule.WingType> wornWings() {
      String raw = this.wornWings.get();
      if (raw != null && !raw.equals("?")) {
         if (!raw.equals(this.wingsRaw)) {
            this.wingsList = parseWorn(raw, CosmeticsModule.WingType.values(), null);
            this.wingsRaw = raw;
         }

         return this.wingsList;
      } else {
         return this.wings.get() && this.wingType.get() != null ? List.of(this.wingType.get()) : List.of();
      }
   }

   public List<CosmeticsModule.Pet> wornPets() {
      String raw = this.wornPets.get() == null ? "" : this.wornPets.get();
      if (!raw.equals(this.petsRaw)) {
         this.petsList = parseWorn(raw, CosmeticsModule.Pet.values(), null);
         this.petsRaw = raw;
      }

      return this.petsList;
   }

   private static <E extends Enum<E>> List<E> parseWorn(String raw, E[] values, E skip) {
      List<E> worn = new ArrayList<>();

      for (String name : raw.split(",")) {
         for (E value : values) {
            if (value != skip && value.name().equals(name.trim()) && !worn.contains(value)) {
               worn.add(value);
            }
         }
      }

      return List.copyOf(worn);
   }

   private static String joinWorn(List<? extends Enum<?>> worn) {
      StringBuilder out = new StringBuilder();

      for (Enum<?> value : worn) {
         out.append(out.isEmpty() ? "" : ",").append(value.name());
      }

      return out.toString();
   }

   public boolean isWearing(Enum<?> kind) {
      if (kind instanceof CosmeticsModule.Pet pet) {
         return this.wornPets().contains(pet);
      } else {
         return kind instanceof CosmeticsModule.Headwear headwear
            ? this.wornHeadwear().contains(headwear)
            : kind instanceof CosmeticsModule.WingType type && this.wornWings().contains(type);
      }
   }

   public void putOn(Enum<?> kind) {
      if (kind instanceof CosmeticsModule.Headwear headwear && headwear != CosmeticsModule.Headwear.NONE && !this.isWearing(headwear)) {
         List<CosmeticsModule.Headwear> worn = new ArrayList<>(this.wornHeadwear());
         worn.add(headwear);
         this.wornHeadwear.set(joinWorn(worn));
      } else if (kind instanceof CosmeticsModule.WingType type && !this.isWearing(type)) {
         List<CosmeticsModule.WingType> worn = new ArrayList<>(this.wornWings());
         worn.add(type);
         this.wornWings.set(joinWorn(worn));
      } else if (kind instanceof CosmeticsModule.Pet pet && !this.isWearing(pet)) {
         List<CosmeticsModule.Pet> worn = new ArrayList<>(this.wornPets());
         worn.add(pet);
         this.wornPets.set(joinWorn(worn));
      }
   }

   public void takeOff(Enum<?> kind) {
      if (kind instanceof CosmeticsModule.Headwear headwear) {
         List<CosmeticsModule.Headwear> worn = new ArrayList<>(this.wornHeadwear());
         worn.remove(headwear);
         this.wornHeadwear.set(joinWorn(worn));
      } else if (kind instanceof CosmeticsModule.WingType type) {
         List<CosmeticsModule.WingType> worn = new ArrayList<>(this.wornWings());
         worn.remove(type);
         this.wornWings.set(joinWorn(worn));
      } else if (kind instanceof CosmeticsModule.Pet pet) {
         List<CosmeticsModule.Pet> worn = new ArrayList<>(this.wornPets());
         worn.remove(pet);
         this.wornPets.set(joinWorn(worn));
      }
   }

   public void takeOffAllHeadwear() {
      this.wornHeadwear.set("");
   }

   public void takeOffAllWings() {
      this.wornWings.set("");
   }

   public void takeOffAllPets() {
      this.wornPets.set("");
   }

   private Map<String, CosmeticsModule.Tweak> tweaks() {
      String raw = this.pieceTweaks.get() == null ? "" : this.pieceTweaks.get();
      if (!raw.equals(this.tweaksRaw)) {
         Map<String, CosmeticsModule.Tweak> parsed = new LinkedHashMap<>();

         for (String entry : raw.split(";")) {
            int split = entry.indexOf(61);
            String[] fields = split <= 0 ? new String[0] : entry.substring(split + 1).split(",");
            if (fields.length == 3) {
               try {
                  parsed.put(
                     entry.substring(0, split),
                     new CosmeticsModule.Tweak(Float.parseFloat(fields[0]), Float.parseFloat(fields[1]), Float.parseFloat(fields[2]))
                  );
               } catch (NumberFormatException var10) {
               }
            }
         }

         this.tweaks = parsed;
         this.tweaksRaw = raw;
      }

      return this.tweaks;
   }

   private static String tweakKey(Enum<?> kind) {
      String prefix = kind instanceof CosmeticsModule.Headwear ? "HEADWEAR:" : (kind instanceof CosmeticsModule.Pet ? "PET:" : "WINGS:");
      return prefix + kind.name();
   }

   private static float finite(float value, float min, float max, float fallback) {
      return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
   }

   public float pieceSize(Enum<?> kind) {
      CosmeticsModule.Tweak tweak = this.tweaks().get(tweakKey(kind));
      boolean head = kind instanceof CosmeticsModule.Headwear;
      boolean pet = kind instanceof CosmeticsModule.Pet;
      float shared = pet ? 1.0F : (float)(head ? this.earSize.get() : this.wingSize.get()).doubleValue();
      float max = pet ? 2.5F : (head ? 2.5F : 3.0F);
      return tweak == null ? finite(shared, 0.4F, max, 1.0F) : finite(tweak.size(), 0.4F, max, 1.0F);
   }

   public float pieceXAngle(Enum<?> kind) {
      CosmeticsModule.Tweak tweak = this.tweaks().get(tweakKey(kind));
      return tweak == null ? 0.0F : finite(tweak.xAngle(), -180.0F, 180.0F, 0.0F);
   }

   public float pieceYAngle(Enum<?> kind) {
      CosmeticsModule.Tweak tweak = this.tweaks().get(tweakKey(kind));
      return tweak == null ? 0.0F : finite(tweak.yAngle(), -180.0F, 180.0F, 0.0F);
   }

   public void setPieceTweak(Enum<?> kind, float size, float xAngle, float yAngle) {
      Map<String, CosmeticsModule.Tweak> next = new LinkedHashMap<>(this.tweaks());
      next.put(tweakKey(kind), new CosmeticsModule.Tweak(size, xAngle, yAngle));
      StringBuilder out = new StringBuilder();

      for (Entry<String, CosmeticsModule.Tweak> entry : next.entrySet()) {
         CosmeticsModule.Tweak tweak = entry.getValue();
         out.append(out.isEmpty() ? "" : ";")
            .append(entry.getKey())
            .append('=')
            .append(String.format(Locale.ROOT, "%.2f,%.1f,%.1f", tweak.size(), tweak.xAngle(), tweak.yAngle()));
      }

      this.pieceTweaks.set(out.toString());
   }

   public float flapSpeed() {
      return (float)this.flapSpeed.get().doubleValue();
   }

   public TextSetting capePixelsSetting() {
      return this.capePixels;
   }

   public ColorSetting paintColorSetting() {
      return this.paintColor;
   }

   public EnumSetting<CosmeticsModule.CapeStyle> capeStyleSetting() {
      return this.capeStyle;
   }

   public int capeColor() {
      return this.capeColor.get();
   }

   public CosmeticsModule.CapeStyle capeStyle() {
      return this.capeStyle.get();
   }

   public String selectedMinecraftCape() {
      return this.minecraftCape.get();
   }

   public String selectedAnimatedCape() {
      return this.animatedCape.get();
   }

   public List<String> animatedCapeNames() {
      return AnimatedCapes.names();
   }

   private Identifier animatedTexture(String name) {
      AnimatedCapes.Style style = AnimatedCapes.byName(name);
      return AnimatedCapes.texture(style == null ? AnimatedCapes.Style.values()[0] : style);
   }

   public String capeStyleLabel() {
      return switch ((CosmeticsModule.CapeStyle)this.capeStyle.get()) {
         case MINECRAFT -> (String)this.minecraftCape.get();
         case DRAWN -> "Drawn";
         case ANIMATED -> (String)this.animatedCape.get();
         default -> "Color";
      };
   }

   public void selectCape(CosmeticsModule.CapeStyle style, String minecraftName) {
      this.capeStyle.set(style);
      if (style == CosmeticsModule.CapeStyle.MINECRAFT && minecraftName != null) {
         this.minecraftCape.set(minecraftName);
      } else if (style == CosmeticsModule.CapeStyle.ANIMATED && minecraftName != null) {
         this.animatedCape.set(minecraftName);
      }
   }

   public boolean showsSetting(Setting<?> setting) {
      CosmeticsModule.CapeStyle style = this.capeStyle.get();
      if (setting == this.showSelf && this.showOn.get() == CosmeticsModule.ShowOn.JUST_ME) {
         return false;
      } else if (setting == this.minecraftCape || setting == this.animatedCape || setting == this.favoriteCosmetics || setting == this.spinPreview) {
         return false;
      } else if (setting == this.headwear
         || setting == this.wings
         || setting == this.wingType
         || setting == this.earSize
         || setting == this.wingSize
         || setting == this.wornHeadwear
         || setting == this.wornWings
         || setting == this.wornPets
         || setting == this.petLooks
         || setting == this.petTricks
         || setting == this.wornEmote
         || setting == this.wornTrail
         || setting == this.wheelEmotes
         || setting == this.pieceTweaks) {
         return false;
      } else {
         return setting == this.capeColor
            ? style != CosmeticsModule.CapeStyle.MINECRAFT && style != CosmeticsModule.CapeStyle.ANIMATED
            : setting != this.paintColor && setting != this.capePixels || style == CosmeticsModule.CapeStyle.DRAWN;
      }
   }

   public int capePixel(int x, int y) {
      String bits = this.capePixels.get();
      int at = (y * 10 + x) * 8;
      if (bits != null && bits.length() >= at + 8) {
         try {
            return (int)Long.parseLong(bits.substring(at, at + 8), 16);
         } catch (NumberFormatException var6) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public void setCapePixel(int x, int y, int argb) {
      if (x >= 0 && x < 10 && y >= 0 && y < 16) {
         String bits = this.capePixels.get();
         int length = 1280;
         StringBuilder builder = new StringBuilder(bits != null && bits.length() == length ? bits : "00000000".repeat(160));
         int at = (y * 10 + x) * 8;
         builder.replace(at, at + 8, String.format("%08X", argb));
         this.capePixels.set(builder.toString());
      }
   }

   public void clearCape() {
      this.capePixels.set("");
   }

   public Identifier whiteTexture() {
      if (this.whiteTexture == null) {
         NativeImage image = new NativeImage(4, 4, false);

         for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
               image.setPixel(x, y, -1);
            }
         }

         this.whiteTexture = Identifier.fromNamespaceAndPath("lyfw-client", "cosmetics/white");
         Minecraft.getInstance().getTextureManager().register(this.whiteTexture, new DynamicTexture(() -> "lyfw-client/cosmetics/white", image));
      }

      return this.whiteTexture;
   }

   public Identifier wingTexture(CosmeticsModule.WingType type, int base, int tip) {
      String key = type.name() + ":" + base + ":" + tip;
      CosmeticsModule.WingTexture wing = this.wingTextures.get(key);
      if (wing == null) {
         if (this.wingTextures.size() >= 256) {
            Iterator<CosmeticsModule.WingTexture> oldest = this.wingTextures.values().iterator();
            wing = oldest.next();
            oldest.remove();
         } else {
            String path = "cosmetics/wings_" + this.wingTextures.size();
            wing = new CosmeticsModule.WingTexture(
               Identifier.fromNamespaceAndPath("lyfw-client", path), new DynamicTexture(() -> "lyfw-client/" + path, new NativeImage(56, 40, true))
            );
            Minecraft.getInstance().getTextureManager().register(wing.id, wing.texture);
         }

         this.wingTextures.put(key, wing);
      }

      if (!key.equals(wing.painted)) {
         wing.painted = key;
         CosmeticsArt.paintWing(wing.texture.getPixels(), type, base, tip);
         wing.texture.upload();
      }

      return wing.id;
   }

   public Identifier capeTexture(CosmeticLoadout.Cape cape) {
      if (cape == null) {
         return null;
      } else {
         switch (cape.style()) {
            case MINECRAFT:
               CosmeticsModule.CapeInfo info = this.find(cape.name());
               if (info != null && !this.failedCapes.contains(info.hash())) {
                  return this.minecraftCapeTexture(info);
               }

               return this.paintedCape(cape.color(), null);
            case DRAWN:
               return this.paintedCape(cape.color(), cape.pixels());
            case ANIMATED:
               return this.animatedTexture(cape.name());
            default:
               return this.paintedCape(cape.color(), null);
         }
      }
   }

   private Identifier paintedCape(int color, String pixels) {
      if (color != this.capeColor.get() || pixels != null && !pixels.equals(this.ownPixels())) {
         String key = pixels == null ? "color" + color : "drawn" + color + pixels;
         CosmeticsModule.PaintedCape slot = this.otherCapes.get(key);
         if (slot == null) {
            if (this.otherCapes.size() >= 32) {
               Iterator<CosmeticsModule.PaintedCape> oldest = this.otherCapes.values().iterator();
               slot = oldest.next();
               oldest.remove();
            } else {
               slot = new CosmeticsModule.PaintedCape("cosmetics/cape_other_" + this.otherCapes.size());
            }

            this.otherCapes.put(key, slot);
         }

         return slot.get(color, () -> pixels == null ? null : texels(pixels), key);
      } else {
         return pixels == null ? this.colorCapeTexture() : this.drawnCapeTexture();
      }
   }

   private static int[] texels(String pixels) {
      int[] texels = new int[160];

      for (int i = 0; i < texels.length && (i + 1) * 8 <= pixels.length(); i++) {
         try {
            texels[i] = (int)Long.parseLong(pixels.substring(i * 8, i * 8 + 8), 16);
         } catch (NumberFormatException var4) {
            texels[i] = 0;
         }
      }

      return texels;
   }

   public Identifier capeTexture(CosmeticsModule.CapeStyle style, String minecraftName) {
      return switch (style) {
         case COLOR -> this.colorCapeTexture();
         case MINECRAFT -> this.capePreview(minecraftName);
         case DRAWN -> this.drawnCapeTexture();
         case ANIMATED -> this.animatedTexture(minecraftName);
      };
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return CarrotClickGuiScreen.wardrobe();
   }

   public BooleanSetting naturalHeadwearColorsSetting() {
      return this.naturalHeadwearColors;
   }

   public int headwearOuter(CosmeticsModule.Headwear kind) {
      return this.naturalHeadwearColors.get() ? kind.naturalMain : this.earColor.get();
   }

   public int headwearInner(CosmeticsModule.Headwear kind) {
      return this.naturalHeadwearColors.get() ? kind.naturalAccent : this.innerEarColor.get();
   }

   public void useCustomHeadwearColors(CosmeticsModule.Headwear kind) {
      if (this.naturalHeadwearColors.get()) {
         this.earColor.set(kind.naturalMain);
         this.innerEarColor.set(kind.naturalAccent);
         this.naturalHeadwearColors.set(false);
      }
   }

   public ColorSetting headwearColorSetting() {
      return this.earColor;
   }

   public ColorSetting accentColorSetting() {
      return this.innerEarColor;
   }

   public ColorSetting wingColorSetting() {
      return this.wingColor;
   }

   public BooleanSetting naturalWingColorsSetting() {
      return this.naturalWingColors;
   }

   public int wingBase(CosmeticsModule.WingType type) {
      return this.naturalWingColors.get() ? type.naturalBase : this.wingColor.get();
   }

   public int wingTip(CosmeticsModule.WingType type) {
      return this.naturalWingColors.get() ? type.naturalTip : this.wingTipColor.get();
   }

   public void useCustomWingColors(CosmeticsModule.WingType type) {
      if (this.naturalWingColors.get()) {
         this.wingColor.set(type.naturalBase);
         this.wingTipColor.set(type.naturalTip);
         this.naturalWingColors.set(false);
      }
   }

   public ColorSetting wingTipColorSetting() {
      return this.wingTipColor;
   }

   public SliderSetting flapSpeedSetting() {
      return this.flapSpeed;
   }

   public BooleanSetting capeSetting() {
      return this.cape;
   }

   public ColorSetting capeColorSetting() {
      return this.capeColor;
   }

   public EnumSetting<CosmeticsModule.ShowOn> showOnSetting() {
      return this.showOn;
   }

   public BooleanSetting showSelfSetting() {
      return this.showSelf;
   }

   public BooleanSetting spinPreviewSetting() {
      return this.spinPreview;
   }

   public SliderSetting renderDistanceSetting() {
      return this.renderDistance;
   }

   public List<String> favoriteKeys() {
      List<String> keys = new ArrayList<>();
      String stored = this.favoriteCosmetics.get();
      if (stored != null) {
         for (String key : stored.split(";")) {
            if (!key.isBlank() && !keys.contains(key)) {
               keys.add(key);
            }
         }
      }

      return keys;
   }

   public boolean isFavorite(String key) {
      return this.favoriteKeys().contains(key);
   }

   public void toggleFavorite(String key) {
      List<String> keys = this.favoriteKeys();
      if (!keys.remove(key)) {
         keys.add(key);
      }

      this.favoriteCosmetics.set(String.join(";", keys));
   }

   public Identifier colorCapeTexture() {
      return this.colorCape.get(this.capeColor.get(), () -> null, "color" + this.capeColor.get());
   }

   public Identifier drawnCapeTexture() {
      String pixels = this.capePixels.get();
      return this.drawnCape.get(this.capeColor.get(), () -> {
         int[] texels = new int[160];

         for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 10; x++) {
               texels[y * 10 + x] = this.capePixel(x, y);
            }
         }

         return texels;
      }, "drawn" + this.capeColor.get() + pixels);
   }

   public Identifier capeTexture() {
      CosmeticsModule.CapeStyle style = this.capeStyle.get();
      if (style == CosmeticsModule.CapeStyle.ANIMATED) {
         return this.animatedTexture(this.animatedCape.get());
      } else if (style == CosmeticsModule.CapeStyle.MINECRAFT) {
         CosmeticsModule.CapeInfo info = this.find(this.minecraftCape.get());
         return info != null && !this.failedCapes.contains(info.hash()) ? this.minecraftCapeTexture(info) : this.colorCapeTexture();
      } else {
         return style == CosmeticsModule.CapeStyle.DRAWN ? this.drawnCapeTexture() : this.colorCapeTexture();
      }
   }

   public Identifier capePreview(String name) {
      CosmeticsModule.CapeInfo info = this.find(name);
      return info == null ? null : this.minecraftCapeTexture(info);
   }

   public boolean capeFailed(String name) {
      CosmeticsModule.CapeInfo info = this.find(name);
      return info != null && this.failedCapes.contains(info.hash());
   }

   public boolean minecraftCapeReady() {
      CosmeticsModule.CapeInfo info = this.find(this.minecraftCape.get());
      return info != null && this.loadedCapes.containsKey(info.hash());
   }

   private CosmeticsModule.CapeInfo find(String name) {
      for (CosmeticsModule.CapeInfo info : MINECRAFT_CAPES) {
         if (info.name().equals(name)) {
            return info;
         }
      }

      return null;
   }

   private Identifier minecraftCapeTexture(CosmeticsModule.CapeInfo info) {
      Identifier loaded = this.loadedCapes.get(info.hash());
      if (loaded == null && !this.failedCapes.contains(info.hash()) && this.loadingCapes.add(info.hash())) {
         Minecraft mc = Minecraft.getInstance();
         this.downloads
            .execute(
               () -> {
                  try {
                     Path cached = FabricLoader.getInstance().getConfigDir().resolve("lyfw-client").resolve("capes").resolve(info.hash() + ".png");
                     byte[] bytes;
                     if (Files.isRegularFile(cached)) {
                        bytes = Files.readAllBytes(cached);
                     } else {
                        HttpRequest request = HttpRequest.newBuilder(URI.create("https://textures.minecraft.net/texture/" + info.hash()))
                           .timeout(Duration.ofSeconds(15L))
                           .GET()
                           .build();
                        HttpResponse<byte[]> response = this.http.send(request, BodyHandlers.ofByteArray());
                        if (response.statusCode() != 200) {
                           throw new IOException("texture server answered " + response.statusCode());
                        }

                        bytes = response.body();
                        Files.createDirectories(cached.getParent());
                        Files.write(cached, bytes);
                     }

                     NativeImage image = NativeImage.read(bytes);
                     mc.execute(() -> {
                        Identifier id = Identifier.fromNamespaceAndPath("lyfw-client", "capes/" + info.hash());
                        mc.getTextureManager().register(id, new DynamicTexture(() -> "lyfw-client/capes/" + info.hash(), image));
                        this.loadedCapes.put(info.hash(), id);
                     });
                  } catch (Exception var10) {
                     System.out.println("[Pip Client] could not load the " + info.name() + " cape: " + var10);
                     this.failedCapes.add(info.hash());
                  } finally {
                     this.loadingCapes.remove(info.hash());
                  }
               }
            );
      }

      return loaded;
   }

   private record CapeInfo(String name, String hash) {
   }

   public static enum CapeStyle {
      COLOR,
      MINECRAFT,
      DRAWN,
      ANIMATED;
   }

   public static enum Emote {
      WAVE,
      BIG_WAVE,
      DOUBLE_WAVE,
      SHY_WAVE,
      SALUTE,
      BOW,
      DEEP_BOW,
      CURTSY,
      HANDSHAKE,
      HIGH_FIVE,
      FIST_BUMP,
      NOD_HELLO,
      CLAP,
      SLOW_CLAP,
      FAST_CLAP,
      APPLAUSE,
      CHEER,
      DOUBLE_CHEER,
      FIST_PUMP,
      VICTORY,
      CHAMPION,
      JUMP_FOR_JOY,
      RAISE_ROOF,
      WOOHOO,
      ARMS_UP,
      HOORAY,
      DAB,
      DAB_LEFT,
      FLOSS,
      SLOW_FLOSS,
      RUNNING_MAN,
      ROBOT,
      TWIST,
      FAST_TWIST,
      DISCO,
      DISCO_DOWN,
      HIP_SWAY,
      SLOW_DANCE,
      SHUFFLE,
      MOONWALK,
      CHARLESTON,
      MACARENA,
      CONGA,
      SPRINKLER,
      LAWNMOWER,
      HEADBANG,
      AIR_GUITAR,
      AIR_DRUMS,
      BREAKDANCE,
      SPIN_DANCE,
      HANDS_UP_DANCE,
      BOUNCE,
      GROOVE,
      WIGGLE,
      STEP_TOUCH,
      LAUGH,
      BIG_LAUGH,
      CRY,
      SOB,
      FACEPALM,
      DOUBLE_FACEPALM,
      SHRUG,
      THINK,
      CONFUSED,
      ANGRY,
      STOMP,
      SULK,
      YAWN,
      SLEEPY,
      CHEEKY,
      BLUSH,
      POINT,
      POINT_UP,
      POINT_DOWN,
      BECKON,
      STOP_HAND,
      THUMBS_UP,
      THUMBS_DOWN,
      CROSS_ARMS,
      HANDS_ON_HIPS,
      T_POSE,
      FLEX,
      DOUBLE_FLEX,
      STRETCH,
      CHECK_WATCH,
      SCRATCH_HEAD,
      WIPE_BROW,
      PEEK,
      SEARCH,
      TIP_HAT,
      THROW,
      PUSH_UPS,
      SIT_UPS,
      SQUATS,
      JUMPING_JACKS,
      RUNNING,
      TOE_TOUCH,
      LUNGES,
      SHADOW_BOX,
      KICK,
      KARATE,
      SIT,
      SIT_LEGS_OUT,
      KNEEL,
      LIE_DOWN,
      SLEEP,
      MEDITATE,
      CROUCH_REST,
      LEAN_BACK,
      FLOAT,
      PLAY_DEAD,
      CHICKEN,
      WORM,
      CARTWHEEL,
      HANDSTAND,
      BACKFLIP,
      ZOMBIE,
      SWIM,
      FISHING,
      ROWING,
      DRIVING,
      TIGHTROPE,
      DISAGREE;
   }

   public static enum Headwear {
      NONE(-13948109, -1598795),
      HALO(-3408, -10402),
      CROWN(-1525968, -3069890),
      TIARA(-2499101, -2079365),
      FLOWER_CROWN(-11628742, -746815),
      CAT_EARS(-13948109, -1598795),
      FOX_EARS(-2066388, -597048),
      WOLF_EARS(-8618106, -2501428),
      BUNNY_EARS(-856086, -874563),
      BEAR_EARS(-9745874, -3562886),
      MOUSE_EARS(-6645086, -1001788),
      DEVIL_HORNS(-4184531, -11923950),
      RAM_HORNS(-3295084, -7439784),
      BULL_HORNS(-1186868, -12963024),
      UNICORN_HORN(-2064, -865972),
      ANTLERS(-8760012, -2571360),
      ANTENNAE(-14803422, -8585365),
      TOP_HAT(-14935006, -5038038),
      WIZARD_HAT(-11850614, -865972),
      PARTY_HAT(-36946, -8090),
      SANTA_HAT(-2940113, -526345),
      BEANIE(-13743494, -1448742),
      BASEBALL_CAP(-3592148, -657931),
      COWBOY_HAT(-7644629, -12900842),
      CHEF_HAT(-328966, -2039584),
      VIKING_HELMET(-7565160, -1055022),
      PROPELLER_CAP(-13664288, -11713),
      HEADPHONES(-14276820, -11546171),
      BOW(-1023342, -4056997),
      SPROUT(-10634422, -8760012),
      SHARK_FIN(-9535603, -1446158),
      FROG_EYES(-10768580, -526350),
      PIRATE_HAT(-14804198, -1522070),
      GRADUATION_CAP(-14934748, -865972),
      HARD_HAT(-605668, -1184275),
      NINJA_HEADBAND(-14012614, -4669752),
      MUSHROOM_CAP(-2677202, -659226),
      ORBITING_STARS(-10166, -2888),
      JESTER_HAT(-9818192, -865972),
      SOMBRERO(-2510230, -4179669),
      GOGGLES(-9745874, -10497050),
      DRAGON_HORNS(-13751754, -7443511),
      ROOSTER_COMB(-2543572, -6414818),
      FEZ(-5038038, -14935006),
      BERET(-13947334, -14934748),
      BUCKET_HAT(-4676998, -9741762),
      THORN_CROWN(-11912662, -8774114),
      FLAME_CROWN(-34278, -8090),
      PUMPKIN(-1543140, -14018038),
      POLICE_CAP(-14734780, -1522070),
      LAUREL_WREATH(-2049988, -7706082),
      ICE_CREAM(-2514842, -543025),
      BIRTHDAY_CAKE(-527640, -1023342),
      TRAFFIC_CONE(-890341, -657931),
      SAMURAI_HELMET(-14013904, -2841030),
      KNIGHT_HELM(-5656906, -14013392),
      LIGHTBULB(-3424, -4669752),
      RAIN_CLOUD(-3156256, -9455873),
      FLOATING_HEARTS(-1555622, -19519),
      BANDANA(-3790808, -657931),
      HEAD_WINGS(-724502, -2841030),
      MOHAWK(-8594882, -13669602),
      ASTRONAUT_HELMET(-986892, -1527744),
      DIVING_HELMET(-3633094, -14005670),
      ICE_CROWN(-3609345, -9785112),
      CAPTAIN_HAT(-723724, -15062454),
      FEDORA(-11912658, -15068140),
      BOWLER_HAT(-15066594, -11912656),
      STRAW_HAT(-1521544, -4181974),
      WITCH_HAT(-14017990, -7709984),
      STEGOSAURUS_PLATES(-11892150, -2064326),
      CHICKEN(-724760, -2084822),
      RUBBER_DUCK(-10182, -1013222),
      CAT_NAP(-1535942, -727856),
      AXOLOTL_GILLS(-743224, -2073974),
      ANGLER_LURE(-14009782, -4653072),
      CANDLE(-725800, -20432),
      MINI_PLANET(-11892000, -1517408),
      CRESCENT_MOON(-3920, -1),
      SUN_HALO(-16336, -34278),
      RAINBOW(-722689, -1515265),
      SNOW_GLOBE(-4660993, -9811414),
      VR_HEADSET(-1513236, -12918529),
      CYBER_VISOR(-14013904, -50582),
      SHADES(-15066594, -1527744),
      EYEPATCH(-15066598, -12965344),
      ARROW_GAG(-7706054, -2084822),
      ROBOT_ANTENNA(-6643540, -53200),
      OCTOPUS(-2069878, -735016),
      POTTED_CACTUS(-11888054, -4167110),
      BIRD_NEST(-7706054, -7679768),
      BURGER(-2582982, -10865632),
      TIN_FOIL_HAT(-3091238, -6643544),
      SPARTAN_HELMET(-3632070, -4181974),
      PHARAOH_HEADDRESS(-14005584, -1525696),
      DEERSTALKER(-7701926, -10859980),
      FIREFIGHTER_HELMET(-3134934, -1525696),
      EARMUFFS(-726800, -9803152),
      USHANKA(-9811404, -2570064),
      BROKEN_HALO(-4673376, -11908534),
      NIGHTCAP(-12952912, -723720);

      public final int naturalMain;
      public final int naturalAccent;

      private Headwear(int naturalMain, int naturalAccent) {
         this.naturalMain = naturalMain;
         this.naturalAccent = naturalAccent;
      }
   }

   private static final class PaintedCape {
      private final String path;
      private DynamicTexture texture;
      private Identifier id;
      private String key = "";

      private PaintedCape(String path) {
         this.path = path;
      }

      private Identifier get(int color, Supplier<int[]> drawn, String key) {
         if (this.texture == null) {
            this.id = Identifier.fromNamespaceAndPath("lyfw-client", this.path);
            this.texture = new DynamicTexture(() -> "lyfw-client/" + this.path, new NativeImage(64, 32, true));
            Minecraft.getInstance().getTextureManager().register(this.id, this.texture);
         }

         if (!key.equals(this.key)) {
            this.key = key;
            CosmeticsArt.paintCape(this.texture.getPixels(), color, drawn.get());
            this.texture.upload();
         }

         return this.id;
      }
   }

   public static enum Pet {
      BABY_GOAT,
      CAT,
      DOG,
      BABY_SHEEP,
      HAMSTER_BALL,
      BUNNY,
      FOX,
      FENNEC_FOX,
      RED_PANDA,
      RACCOON,
      PANDA,
      POLAR_BEAR,
      BEAR_CUB,
      PIGLET,
      CALF,
      HIGHLAND_COW,
      QUOKKA,
      CHINCHILLA,
      HAMSTER,
      CHIPMUNK,
      BADGER,
      TANUKI,
      ELEPHANT,
      PLATYPUS,
      KIWI,
      LION,
      TIGER,
      LEOPARD,
      CHEETAH,
      HIPPO,
      RHINO,
      BUDGIE,
      NARWHAL,
      SEAHORSE,
      SKUNK,
      HEDGEHOG,
      MOUSE,
      GUINEA_PIG,
      FERRET,
      OTTER,
      BEAVER,
      SQUIRREL,
      CAPYBARA,
      ARMADILLO,
      ANTEATER,
      BOAR,
      CORGI,
      DACHSHUND,
      POODLE,
      HUSKY,
      PUG,
      DALMATIAN,
      SHIBA,
      WOLF,
      LYNX,
      PENGUIN,
      OWL,
      MONKEY,
      MEERKAT,
      KOALA,
      KANGAROO,
      SLOTH,
      CHICK,
      DUCKLING,
      PARROT,
      STARFISH,
      PEACOCK,
      TOUCAN,
      CROW,
      EAGLE,
      SWAN,
      ROBIN,
      CHICKEN,
      CATERPILLAR,
      PUFFIN,
      TURTLE,
      TORTOISE,
      FROG,
      AXOLOTL,
      GECKO,
      CROCODILE,
      T_REX,
      TRICERATOPS,
      STEGOSAURUS,
      BABY_DRAGON,
      SNAKE,
      SEAL,
      BUTTERFLY,
      BEE,
      LADYBUG,
      BAT,
      JELLYFISH,
      GOLDFISH,
      OCTOPUS,
      CRAB,
      SNAIL,
      DRAGONFLY,
      PUFFERFISH,
      WHALE,
      SHARK;
   }

   public static enum PetSpot {
      GROUND,
      SHOULDER,
      HEAD;
   }

   public static enum ShowOn {
      EVERYONE,
      JUST_ME;
   }

   private record Tweak(float size, float xAngle, float yAngle) {
   }

   private static final class WingTexture {
      private final Identifier id;
      private final DynamicTexture texture;
      private String painted = "";

      private WingTexture(Identifier id, DynamicTexture texture) {
         this.id = id;
         this.texture = texture;
      }
   }

   public static enum WingType {
      ANGEL(false, false, 0.45F, -329486, -2569304),
      DRAGON(false, false, 0.45F, -6542814, -11922158),
      BAT(false, false, 0.45F, -12766159, -14804455),
      BUTTERFLY(true, false, 0.9F, -1013220, -3911154),
      FAIRY(true, true, 0.9F, -801048, -5708043),
      PHOENIX(false, false, 0.45F, -20181, -2608614),
      DEMON(false, false, 0.4F, -14019560, -8777696),
      SKELETON(false, false, 0.45F, -1515320, -4213866),
      MECH(false, false, 0.35F, -7695716, -12592912),
      CRYSTAL(false, true, 0.4F, -4590849, -4944641),
      MOTH(true, false, 0.7F, -3558518, -8757698),
      BEE(true, true, 2.6F, -854017, -10859984),
      HAWK(false, false, 0.45F, -7710157, -12901356),
      ENDER(false, false, 0.4F, -15069152, -4169473),
      LEAF(true, false, 0.8F, -11625926, -13735132),
      FROST(false, true, 0.45F, -2231041, -8600336),
      DRAGONFLY(true, true, 2.2F, -1509633, -13738374),
      LUNA_MOTH(true, false, 0.6F, -4200026, -8765842),
      RAINBOW(false, false, 0.45F, -1, -5197648),
      CLOCKWORK(false, false, 0.35F, -3562934, -6661590),
      LIGHTNING(false, true, 0.6F, -8399617, -1),
      SHADOW(false, true, 0.4F, -15330788, -11912610),
      PEACOCK(false, false, 0.4F, -14714261, -14004304),
      PETAL(true, false, 0.8F, -543025, -5646),
      HOLOGRAM(false, true, 0.45F, -12590849, -14717953),
      STAINED_GLASS(false, true, 0.4F, -3064998, -12878895),
      OWL(false, false, 0.4F, -3624822, -9546698),
      PARROT(false, false, 0.55F, -2742232, -14721066),
      CROW(false, false, 0.45F, -14935006, -12959915),
      PTERODACTYL(false, false, 0.35F, -7706038, -11650520),
      GARGOYLE(false, false, 0.3F, -8486264, -11907758),
      SWALLOWTAIL(true, false, 0.85F, -863926, -14803422),
      LADYBUG(true, true, 1.8F, -2742242, -15395563),
      INFERNO(false, true, 0.5F, -38374, -10166),
      AQUA(false, true, 0.45F, -12604939, -4657921),
      GALAXY(false, false, 0.4F, -15594962, -4817665),
      ORIGAMI(false, false, 0.4F, -724502, -3554638),
      PIXEL(false, false, 0.45F, -11557889, -14796150),
      CIRCUIT(false, false, 0.4F, -14783686, -1522070),
      MAGMA(false, false, 0.35F, -14018026, -34278),
      CANDY(false, false, 0.5F, -33098, -1),
      SNOWFLAKE(false, true, 0.6F, -1378049, -6498049),
      VINE(false, false, 0.4F, -12682706, -8760012),
      TATTERED(false, false, 0.35F, -11907494, -14013389),
      NEON(false, true, 0.5F, -49196, -12586241),
      BUBBLE(true, true, 0.7F, -4200193, -14608),
      SWAN(false, false, 0.4F, -1, -2564892),
      EAGLE(false, false, 0.35F, -11914720, -3630512),
      HUMMINGBIRD(false, false, 2.4F, -13979542, -5227808),
      FLAMINGO(false, false, 0.4F, -1013080, -15066594),
      BLUE_JAY(false, false, 0.5F, -12944672, -15065558),
      ARCHANGEL(false, false, 0.35F, -780, -1525696),
      SERAPH(false, false, 0.35F, -2852, -11892000),
      FALLEN(false, false, 0.4F, -14013904, -7726550),
      VALKYRIE(false, false, 0.4F, -3616548, -2578374),
      CHERUB(false, false, 0.8F, -3852, -472872),
      GRIFFIN(false, false, 0.4F, -3633094, -11915752),
      WYVERN(false, false, 0.4F, -12944838, -15058406),
      ICE_DRAGON(false, true, 0.4F, -3609345, -9785112),
      THUNDERBIRD(false, false, 0.45F, -14013830, -8128),
      SOUL_FIRE(false, true, 0.5F, -12918529, -2031617),
      VOID(false, true, 0.35F, -16382964, -6274817),
      STARLIGHT(false, true, 0.4F, -15721912, -2880),
      SUNBURST(false, true, 0.45F, -2896, -30176),
      AURORA(false, true, 0.4F, -12517472, -6266625),
      STORM_CLOUD(false, false, 0.35F, -7695196, -4000),
      SAND(false, false, 0.35F, -1525640, -5740486),
      WIND(false, true, 0.6F, -1508609, -7677712),
      EARTH(false, false, 0.3F, -8754598, -11892166),
      CORAL(false, false, 0.35F, -34166, -20384),
      SEASHELL(true, false, 0.4F, -466736, -2061718),
      FERN(false, false, 0.45F, -11888070, -13997532),
      LOTUS(false, false, 0.4F, -3852, -1021286),
      CHERRY_BLOSSOM(false, false, 0.4F, -18224, -10864086),
      MAPLE(false, false, 0.45F, -2080726, -1003472),
      BLUE_MORPHO(true, false, 0.7F, -13985025, -16119276),
      GLASSWING(true, true, 0.75F, -1511184, -12966888),
      ATLAS_MOTH(true, false, 0.5F, -5223894, -993120),
      CICADA(true, true, 2.0F, -1509136, -12948934),
      FIREFLY(true, false, 1.2F, -14015456, -2556096),
      WASP(true, true, 2.4F, -2054080, -12966896),
      SCARAB(true, false, 1.6F, -13979542, -1523648),
      MANTA(true, false, 0.3F, -14011328, -985864),
      FLYING_FISH(true, true, 0.6F, -7679745, -14001488),
      JELLYFISH(true, true, 0.4F, -3102465, -10428161),
      SPIDER_WEB(true, true, 0.3F, -722689, -4663041),
      HEART(true, false, 0.6F, -42358, -12064),
      KITE(true, false, 0.4F, -2082230, -12940576),
      PAPER_PLANE(true, false, 0.3F, -460556, -7690040),
      PATCHWORK(false, false, 0.4F, -2061734, -10843456),
      GOLD_FILIGREE(false, false, 0.4F, -1523632, -7706080),
      CHAINMAIL(false, false, 0.35F, -5721928, -10864094),
      WOODEN(false, false, 0.35F, -5210038, -9812956),
      LANTERN(true, true, 0.3F, -20400, -2082262),
      JET(true, false, 0.2F, -4669240, -2082246),
      SOLAR(true, false, 0.2F, -15058294, -2576320),
      ENERGY_BLADE(false, true, 0.5F, -12521217, -48944),
      HEXGRID(false, true, 0.45F, -12517456, -15054198),
      MUSIC(false, false, 0.5F, -15066590, -1523632),
      OPAL(false, false, 0.4F, -724744, -10428208);

      public final boolean insect;
      public final boolean translucent;
      public final float beatRate;
      public final int naturalBase;
      public final int naturalTip;

      private WingType(boolean insect, boolean translucent, float beatRate, int naturalBase, int naturalTip) {
         this.insect = insect;
         this.translucent = translucent;
         this.beatRate = beatRate;
         this.naturalBase = naturalBase;
         this.naturalTip = naturalTip;
      }
   }
}
