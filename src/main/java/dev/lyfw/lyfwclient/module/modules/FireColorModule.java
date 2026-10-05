package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class FireColorModule extends Module {
   private static final int SETTLE_TICKS = 5;
   private final ColorSetting fireColor = this.register(new ColorSetting("Fire Color", -11549697).exemptFromGlobalColor());
   private boolean applied;
   private boolean reconciled;
   private int builtColor = Integer.MIN_VALUE;
   private int settling;

   public FireColorModule() {
      super("Fire Color", "Recolors fire to any color - blocks, burning players and your own screen.", Category.RENDER, false);
      this.fireColor.group = "Fire";
   }

   public static FireColorModule get() {
      return ModuleManager.get("Fire Color") instanceof FireColorModule fire ? fire : null;
   }

   public int rgb() {
      return 0xFF000000 | this.fireColor.get() & 16777215;
   }

   public int tint(int original) {
      return original & 0xFF000000 | this.fireColor.get() & 16777215;
   }

   @Override
   public void tick() {
      boolean wanted = this.isEnabled();
      if (!this.reconciled || wanted != this.applied) {
         this.reconciled = true;
         this.applied = wanted;
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         String id = manager.getAvailableIds().stream().filter(candidate -> candidate.endsWith("fire_color")).findFirst().orElse(null);
         if (id != null && (wanted ? manager.addPack(id) : manager.removePack(id))) {
            mc.reloadResourcePacks();
         }
      }

      this.rebuildTerrainWhenColorSettles();
   }

   private void rebuildTerrainWhenColorSettles() {
      int color = this.isEnabled() ? this.rgb() : -1;
      if (color == this.builtColor) {
         this.settling = 0;
      } else if (this.settling++ >= 5) {
         this.builtColor = color;
         this.settling = 0;
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            mc.levelRenderer.allChanged();
         }
      }
   }
}
