package dev.lyfw.lyfwclient.render;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;

public final class BlockSaturation {
   private BlockSaturation() {
   }

   public static Map<Block, Integer> parse(String raw) {
      Map<Block, Integer> out = new LinkedHashMap<>();
      if (raw == null) {
         return out;
      } else {
         for (String entry : raw.split(";")) {
            int eq = entry.indexOf(61);
            if (eq > 0) {
               Identifier id = Identifier.tryParse(entry.substring(0, eq).trim());
               if (id != null && BuiltInRegistries.BLOCK.containsKey(id)) {
                  try {
                     out.put((Block)BuiltInRegistries.BLOCK.getValue(id), Math.max(0, Math.min(100, Integer.parseInt(entry.substring(eq + 1).trim()))));
                  } catch (NumberFormatException var9) {
                  }
               }
            }
         }

         return out;
      }
   }

   public static String write(Map<Block, Integer> blocks) {
      StringBuilder out = new StringBuilder();

      for (Entry<Block, Integer> entry : blocks.entrySet()) {
         out.append(out.isEmpty() ? "" : ";").append(BuiltInRegistries.BLOCK.getKey(entry.getKey())).append('=').append(entry.getValue());
      }

      return out.toString();
   }

   public static void sync(String wanted) {
      Map<Block, SpriteTweaks.Tweak> blocks = new LinkedHashMap<>();

      for (Entry<Block, Integer> entry : parse(wanted).entrySet()) {
         blocks.put(entry.getKey(), new SpriteTweaks.Tweak(entry.getValue(), -1, 255));
      }

      SpriteTweaks.request("block_colors", blocks);
   }

   public static List<Block> allBlocks() {
      List<Block> out = new ArrayList<>();

      for (Block block : BuiltInRegistries.BLOCK) {
         if (block.asItem() != Items.AIR || block.defaultBlockState().getRenderShape() == RenderShape.MODEL) {
            out.add(block);
         }
      }

      out.removeIf(blockx -> blockx.defaultBlockState().isAir());
      out.sort((a, b) -> name(a).compareToIgnoreCase(name(b)));
      return out;
   }

   public static String name(Block block) {
      return block.getName().getString();
   }

   public static boolean matches(Block block, String needle) {
      String n = needle.toLowerCase(Locale.ROOT);
      return name(block).toLowerCase(Locale.ROOT).contains(n) || BuiltInRegistries.BLOCK.getKey(block).toString().contains(n);
   }

   public static int count(String raw) {
      return parse(raw).size();
   }
}
