package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class NostalgiaModule extends Module {
   private final EnumSetting<NostalgiaModule.CrystalMotion> crystalMotion = this.register(
      new EnumSetting<>("Crystal Motion", NostalgiaModule.CrystalMotion.NORMAL)
   );
   private final BooleanSetting oldLighting = this.register(new BooleanSetting("Old Lighting", false));
   private final BooleanSetting oldGlint = this.register(new BooleanSetting("Old Glint", false));
   private boolean appliedLighting;
   private boolean appliedGlint;
   private boolean reconciled;

   public NostalgiaModule() {
      super("Nostalgia", "Crystal motion, Old Lighting and Old Glint - the older look, one switch at a time.", Category.RENDER, false);
      this.crystalMotion.group = "Switches";
      this.oldLighting.group = "Switches";
      this.oldGlint.group = "Switches";
   }

   public static NostalgiaModule get() {
      return ModuleManager.get("Nostalgia") instanceof NostalgiaModule nostalgia ? nostalgia : null;
   }

   public NostalgiaModule.CrystalMotion crystalMotion() {
      return this.isEnabled() ? this.crystalMotion.get() : NostalgiaModule.CrystalMotion.NORMAL;
   }

   @Override
   public void tick() {
      boolean wantLighting = this.isEnabled() && this.oldLighting.get();
      boolean wantGlint = this.isEnabled() && this.oldGlint.get();
      if (!this.reconciled || wantLighting != this.appliedLighting || wantGlint != this.appliedGlint) {
         this.reconciled = true;
         this.appliedLighting = wantLighting;
         this.appliedGlint = wantGlint;
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         boolean changed = apply(manager, "old_lighting", wantLighting);
         changed |= apply(manager, "old_glint", wantGlint);
         if (changed) {
            mc.reloadResourcePacks();
         }
      }
   }

   private static boolean apply(PackRepository manager, String name, boolean wanted) {
      String id = manager.getAvailableIds().stream().filter(candidate -> candidate.endsWith(name)).findFirst().orElse(null);
      return id != null && (wanted ? manager.addPack(id) : manager.removePack(id));
   }

   public static enum CrystalMotion {
      NORMAL("Normal"),
      NO_BOB("No Bob"),
      STATIC("Static");

      public final String title;

      private CrystalMotion(String title) {
         this.title = title;
      }
   }
}
