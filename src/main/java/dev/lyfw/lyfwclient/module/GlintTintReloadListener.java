package dev.lyfw.lyfwclient.module;

import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.minecraft.server.packs.resources.PreparableReloadListener.SharedState;

public class GlintTintReloadListener extends SimpleResourceReloader<Void> {
   protected Void prepare(SharedState store) {
      return null;
   }

   protected void apply(Void prepared, SharedState store) {
      for (Module module : ModuleManager.all()) {
         if (module instanceof EnchantGlintTintModule glintTint) {
            glintTint.markDirty();
         }
      }
   }
}
