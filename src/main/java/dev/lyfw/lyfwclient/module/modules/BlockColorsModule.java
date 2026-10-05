package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.render.BlockSaturation;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockColorsModule extends Module {
   private static final Object[][] TARGETS = new Object[][]{
      {new Block[]{Blocks.DIRT}, "Dirt", "Ground"},
      {new Block[]{Blocks.COARSE_DIRT}, "Coarse Dirt", "Ground"},
      {new Block[]{Blocks.SAND}, "Sand", "Ground"},
      {new Block[]{Blocks.GRAVEL}, "Gravel", "Ground"},
      {new Block[]{Blocks.SNOW, Blocks.SNOW_BLOCK}, "Snow", "Ground"},
      {new Block[]{Blocks.STONE}, "Stone", "Stone"},
      {new Block[]{Blocks.COBBLESTONE}, "Cobblestone", "Stone"},
      {new Block[]{Blocks.OBSIDIAN}, "Obsidian", "Stone"},
      {new Block[]{Blocks.BEDROCK}, "Bedrock", "Stone"},
      {new Block[]{Blocks.NETHERRACK}, "Netherrack", "Other"},
      {new Block[]{Blocks.END_STONE}, "End Stone", "Other"},
      {new Block[]{Blocks.OAK_PLANKS}, "Oak Planks", "Other"}
   };
   private final Map<Block, BlockColorsModule.Entry> entries = new LinkedHashMap<>();
   private final BooleanSetting tintGrass = this.register(new BooleanSetting("Grass", false));
   private final ColorSetting grassColor = this.register(new ColorSetting("Grass Color", -8602261).exemptFromGlobalColor());
   private final BooleanSetting tintFoliage = this.register(new BooleanSetting("Leaves", false));
   private final ColorSetting foliageColor = this.register(new ColorSetting("Leaves Color", -10899920).exemptFromGlobalColor());
   private final BooleanSetting tintWater = this.register(new BooleanSetting("Water", false));
   private final ColorSetting waterColor = this.register(new ColorSetting("Water Color", -12618012).exemptFromGlobalColor());
   private final TextSetting desaturated = this.register(new TextSetting("Desaturate Blocks", ""));
   private boolean appliedPack;
   private boolean reconciled;
   private int builtSignature;
   private int settling;
   private static final int SETTLE_TICKS = 5;
   private String greyRaw;
   private Map<Block, Integer> greyList = Map.of();

   public BlockColorsModule() {
      super(
         "Block Colors",
         "Recolors dirt, stone, sand, snow and more - plus grass, leaves and water - and takes the colour out of any blocks you pick.",
         Category.RENDER,
         false
      );

      for (Object[] target : TARGETS) {
         String name = (String)target[1];
         String group = (String)target[2];
         BooleanSetting on = this.register(new BooleanSetting(name, false));
         ColorSetting color = this.register(new ColorSetting(name + " Color", -7640246).exemptFromGlobalColor());
         on.group = group;
         color.group = group;
         BlockColorsModule.Entry entry = new BlockColorsModule.Entry(on, color);

         for (Block block : (Block[])target[0]) {
            this.entries.put(block, entry);
         }
      }

      this.tintGrass.group = "Biome";
      this.grassColor.group = "Biome";
      this.tintFoliage.group = "Biome";
      this.foliageColor.group = "Biome";
      this.tintWater.group = "Biome";
      this.waterColor.group = "Biome";
      this.desaturated.group = "Saturation";
   }

   public static BlockColorsModule get() {
      return ModuleManager.get("Block Colors") instanceof BlockColorsModule colors ? colors : null;
   }

   public static Block[] targets() {
      List<Block> blocks = new ArrayList<>();

      for (Object[] target : TARGETS) {
         blocks.addAll(List.of((Block[])target[0]));
      }

      return blocks.toArray(new Block[0]);
   }

   public int colorFor(Block block) {
      BlockColorsModule.Entry entry = this.entries.get(block);
      return this.isEnabled() && entry != null && entry.on.get() ? 0xFF000000 | entry.color.get() & 16777215 : -1;
   }

   public int grass() {
      return this.isEnabled() && this.tintGrass.get() ? 0xFF000000 | this.grassColor.get() & 16777215 : -1;
   }

   public int foliage() {
      return this.isEnabled() && this.tintFoliage.get() ? 0xFF000000 | this.foliageColor.get() & 16777215 : -1;
   }

   public int water() {
      return this.isEnabled() && this.tintWater.get() ? 0xFF000000 | this.waterColor.get() & 16777215 : -1;
   }

   public int greyTint(Block block, int color) {
      if (color != -1 && this.isEnabled()) {
         String raw = this.desaturated.get();
         if (!raw.equals(this.greyRaw)) {
            this.greyList = BlockSaturation.parse(raw);
            this.greyRaw = raw;
         }

         Integer saturation = this.greyList.get(block);
         if (saturation != null && saturation < 100) {
            float s = saturation.intValue() / 100.0F;
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            float grey = r * 0.299F + g * 0.587F + b * 0.114F;
            return color & 0xFF000000 | Math.round(grey + (r - grey) * s) << 16 | Math.round(grey + (g - grey) * s) << 8 | Math.round(grey + (b - grey) * s);
         } else {
            return color;
         }
      } else {
         return color;
      }
   }

   public TextSetting desaturatedSetting() {
      return this.desaturated;
   }

   @Override
   public void tick() {
      boolean wanted = this.isEnabled();
      Minecraft mc = Minecraft.getInstance();
      BlockSaturation.sync(wanted ? this.desaturated.get() : "");
      if (!this.reconciled || wanted != this.appliedPack) {
         this.reconciled = true;
         this.appliedPack = wanted;
         PackRepository manager = mc.getResourcePackRepository();
         String id = manager.getAvailableIds().stream().filter(candidate -> candidate.endsWith("block_colors")).findFirst().orElse(null);
         if (id != null && (wanted ? manager.addPack(id) : manager.removePack(id))) {
            mc.reloadResourcePacks();
         }
      }

      int signature = this.signature();
      if (signature == this.builtSignature) {
         this.settling = 0;
      } else if (this.settling++ >= 5) {
         this.builtSignature = signature;
         this.settling = 0;
         if (mc.level != null) {
            mc.levelRenderer.allChanged();
         }
      }
   }

   private int signature() {
      int signature = this.isEnabled() ? 1 : 0;

      for (Block block : this.entries.keySet()) {
         signature = signature * 31 + this.colorFor(block);
      }

      signature = signature * 31 + this.desaturated.get().hashCode();
      return (signature * 31 + this.grass()) * 31 + this.foliage() * 31 + this.water();
   }

   private record Entry(BooleanSetting on, ColorSetting color) {
   }
}
