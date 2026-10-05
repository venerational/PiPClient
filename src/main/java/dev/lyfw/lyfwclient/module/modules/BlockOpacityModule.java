package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockOpacityModule extends Module {
   private static final int[] TIERS = new int[]{20, 40, 60, 80};
   private final BooleanSetting hidePlayers = this.register(new BooleanSetting("Hide Players Behind", true));
   private final BooleanSetting respawnAnchor = this.register(new BooleanSetting("Respawn Anchor", true));
   private final BooleanSetting obsidian = this.register(new BooleanSetting("Obsidian", false));
   private final BooleanSetting glowstone = this.register(new BooleanSetting("Glowstone", false));
   private final BooleanSetting endCrystal = this.register(new BooleanSetting("End Crystal", false));
   private final SliderSetting opacity = this.register(new SliderSetting("Opacity", 60.0, 20.0, 80.0, 20.0, "%"));
   private final int[] lastAppliedTiers = new int[]{-1, -1, -1};
   private final boolean[] lastAppliedEnabled = new boolean[]{false, false, false};

   public BlockOpacityModule() {
      super("Block Opacity", "Makes Respawn Anchor, Obsidian, Glowstone and End Crystal see-through, with an adjustable opacity tier.", Category.RENDER, false);
   }

   public boolean wantsTranslucentLayer(Block block) {
      return block == Blocks.RESPAWN_ANCHOR && AnchorCustomizerModule.wantsTranslucent()
         ? true
         : this.isEnabled() && targetIndex(block) >= 0 && this.blockToggle(targetIndex(block)).get();
   }

   public boolean hidesPlayersBehind() {
      return this.hidePlayers.get();
   }

   public boolean wantsCullFix(Block block) {
      return this.wantsTranslucentLayer(block);
   }

   public boolean wantsEndCrystalTranslucent() {
      return this.isEnabled() && this.endCrystal.get();
   }

   public int endCrystalTintedColor() {
      int alpha = Math.round(this.opacity.getInt() / 100.0F * 255.0F);
      return alpha << 24 | 16777215;
   }

   private static int targetIndex(Block block) {
      if (block == Blocks.RESPAWN_ANCHOR) {
         return 0;
      } else if (block == Blocks.OBSIDIAN) {
         return 1;
      } else {
         return block == Blocks.GLOWSTONE ? 2 : -1;
      }
   }

   private BooleanSetting blockToggle(int index) {
      return switch (index) {
         case 0 -> this.respawnAnchor;
         case 1 -> this.obsidian;
         case 2 -> this.glowstone;
         default -> throw new IllegalArgumentException("index " + index);
      };
   }

   private static String packPrefix(int index) {
      return switch (index) {
         case 0 -> "anchor_opacity_";
         case 1 -> "obsidian_opacity_";
         case 2 -> "glowstone_opacity_";
         default -> throw new IllegalArgumentException("index " + index);
      };
   }

   @Override
   public void tick() {
      int tier = nearestTier(this.opacity.getInt());
      Minecraft mc = Minecraft.getInstance();
      PackRepository manager = mc.getResourcePackRepository();
      boolean packsChanged = false;
      boolean layerRelevantChanged = false;

      for (int i = 0; i < 3; i++) {
         boolean enabled = this.isEnabled() && this.blockToggle(i).get();
         if (enabled != this.lastAppliedEnabled[i] || tier != this.lastAppliedTiers[i]) {
            if (enabled != this.lastAppliedEnabled[i]) {
               layerRelevantChanged = true;
            }

            String prefix = packPrefix(i);

            for (int t : TIERS) {
               String id = findPackId(manager, prefix + t);
               if (id != null) {
                  boolean wanted = enabled && t == tier;
                  packsChanged |= wanted ? manager.addPack(id) : manager.removePack(id);
               }
            }

            this.lastAppliedTiers[i] = tier;
            this.lastAppliedEnabled[i] = enabled;
         }
      }

      if (packsChanged) {
         mc.reloadResourcePacks();
      }

      if (layerRelevantChanged) {
         mc.levelRenderer.allChanged();
      }
   }

   private static int nearestTier(int value) {
      int best = TIERS[0];
      int bestDist = Math.abs(value - best);

      for (int t : TIERS) {
         int dist = Math.abs(value - t);
         if (dist < bestDist) {
            best = t;
            bestDist = dist;
         }
      }

      return best;
   }

   private static String findPackId(PackRepository manager, String fragment) {
      return manager.getAvailableIds().stream().filter(id -> id.endsWith(fragment)).findFirst().orElse(null);
   }
}
