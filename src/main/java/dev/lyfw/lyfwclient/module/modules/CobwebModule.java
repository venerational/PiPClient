package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class CobwebModule extends Module {
   private static final String TRANSLUCENT_PACK_FRAGMENT = "cobweb_translucent";
   private static final String HITBOX_OPAQUE_PACK_FRAGMENT = "cobweb_hitbox_opaque";
   private static final String HITBOX_TRANSLUCENT_PACK_FRAGMENT = "cobweb_hitbox";
   private final BooleanSetting translucent = this.register(new BooleanSetting("Translucent", true));
   private final BooleanSetting showHitbox = this.register(new BooleanSetting("Show Hitbox", false));
   private CobwebModule.PackState lastApplied;

   public CobwebModule() {
      super("Cobweb", "Translucent cobwebs, with an optional hitbox-outline look matching the \"Cobweb Outlines\" resource pack.", Category.RENDER, false);
   }

   public boolean wantsTranslucentLayer() {
      return this.isEnabled() && (this.translucent.get() || this.showHitbox.get());
   }

   @Override
   public void tick() {
      CobwebModule.PackState desired = this.desiredState();
      if (this.lastApplied != desired) {
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         String translucentId = findPackId(manager, "cobweb_translucent");
         String hitboxOpaqueId = findPackId(manager, "cobweb_hitbox_opaque");
         String hitboxTranslucentId = findPackId(manager, "cobweb_hitbox");
         if (translucentId != null && hitboxOpaqueId != null && hitboxTranslucentId != null) {
            boolean changed = setPackEnabled(manager, translucentId, desired == CobwebModule.PackState.TRANSLUCENT_ONLY);
            changed |= setPackEnabled(manager, hitboxOpaqueId, desired == CobwebModule.PackState.HITBOX_ONLY);
            changed |= setPackEnabled(manager, hitboxTranslucentId, desired == CobwebModule.PackState.HITBOX_TRANSLUCENT);
            if (changed) {
               mc.reloadResourcePacks();
            }

            this.lastApplied = desired;
         }
      }
   }

   private CobwebModule.PackState desiredState() {
      if (!this.isEnabled()) {
         return CobwebModule.PackState.NONE;
      } else if (this.translucent.get() && this.showHitbox.get()) {
         return CobwebModule.PackState.HITBOX_TRANSLUCENT;
      } else if (this.showHitbox.get()) {
         return CobwebModule.PackState.HITBOX_ONLY;
      } else {
         return this.translucent.get() ? CobwebModule.PackState.TRANSLUCENT_ONLY : CobwebModule.PackState.NONE;
      }
   }

   private static boolean setPackEnabled(PackRepository manager, String packId, boolean wanted) {
      return wanted ? manager.addPack(packId) : manager.removePack(packId);
   }

   private static String findPackId(PackRepository manager, String fragment) {
      return manager.getAvailableIds().stream().filter(id -> id.endsWith(fragment)).findFirst().orElse(null);
   }

   private static enum PackState {
      NONE,
      TRANSLUCENT_ONLY,
      HITBOX_ONLY,
      HITBOX_TRANSLUCENT;
   }
}
