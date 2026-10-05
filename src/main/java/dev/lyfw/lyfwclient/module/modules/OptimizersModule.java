package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;

public class OptimizersModule extends Module {
   private final BooleanSetting marlow = this.register(new BooleanSetting("Marlow's Crystal Optimizer", true));
   private final BooleanSetting clientSideCrystals = this.register(new BooleanSetting("Client Side Crystals", true));
   private final BooleanSetting anchor = this.register(new BooleanSetting("Hero's Anchor Optimizer", true));
   private Boolean appliedCrystals;
   private Method crystalsSetEnabled;

   public OptimizersModule() {
      super("Optimizers", "Turns Marlow's Crystal Optimizer, Client Side Crystals and Hero's Anchor Optimizer on and off.", Category.MISC, true);
   }

   public static OptimizersModule get() {
      return ModuleManager.get("Optimizers") instanceof OptimizersModule optimizers ? optimizers : null;
   }

   public static boolean allowsMarlow() {
      OptimizersModule optimizers = get();
      return optimizers == null || optimizers.isEnabled() && optimizers.marlow.get();
   }

   public static boolean allowsAnchor() {
      OptimizersModule optimizers = get();
      return optimizers == null || optimizers.isEnabled() && optimizers.anchor.get();
   }

   @Override
   public void tick() {
      boolean want = this.isEnabled() && this.clientSideCrystals.get();
      if ((this.appliedCrystals == null || this.appliedCrystals != want) && FabricLoader.getInstance().isModLoaded("clientsidecrystals")) {
         try {
            if (this.crystalsSetEnabled == null) {
               this.crystalsSetEnabled = Class.forName("me.clientsidecrystals.core.CrystalPredictor").getMethod("setEnabled", boolean.class);
            }

            this.crystalsSetEnabled.invoke(null, want);
            this.appliedCrystals = want;
         } catch (RuntimeException | ReflectiveOperationException var3) {
         }
      }
   }
}
