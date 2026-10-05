package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class TotemTransparencyModule extends Module {
   private static final int[] TIERS = new int[]{20, 40, 60, 80};
   private final SliderSetting opacity = this.register(new SliderSetting("Opacity", 40.0, 20.0, 80.0, 20.0, "%"));
   private int appliedTier = -1;
   private boolean appliedEnabled;
   private boolean reconciled;

   public TotemTransparencyModule() {
      super("Totem Transparency", "Fades the totem in your hand so it stops covering the screen.", Category.RENDER, false);
      this.opacity.group = "Totem";
   }

   @Override
   public void tick() {
      int tier = nearestTier(this.opacity.getInt());
      boolean wanted = this.isEnabled();
      if (!this.reconciled || wanted != this.appliedEnabled || tier != this.appliedTier) {
         this.reconciled = true;
         this.appliedEnabled = wanted;
         this.appliedTier = tier;
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         boolean changed = false;

         for (int t : TIERS) {
            String id = manager.getAvailableIds().stream().filter(candidate -> candidate.endsWith("totem_opacity_" + t)).findFirst().orElse(null);
            if (id != null) {
               changed |= wanted && t == tier ? manager.addPack(id) : manager.removePack(id);
            }
         }

         if (changed) {
            mc.reloadResourcePacks();
         }
      }
   }

   private static int nearestTier(int value) {
      int best = TIERS[0];

      for (int t : TIERS) {
         if (Math.abs(value - t) < Math.abs(value - best)) {
            best = t;
         }
      }

      return best;
   }
}
