package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class ShieldPositionModule extends Module {
   private static final String SIDE_SHIELD_PACK_FRAGMENT = "side_shield_overlay";
   private final SliderSetting offsetX = this.register(new SliderSetting("X Offset", 0.0, -1.0, 1.0, 0.01, ""));
   private final SliderSetting offsetY = this.register(new SliderSetting("Y Offset", 0.0, -1.0, 1.0, 0.01, ""));
   private final SliderSetting offsetZ = this.register(new SliderSetting("Z Offset", 0.0, -1.0, 1.0, 0.01, ""));
   private final SliderSetting rotationX = this.register(new SliderSetting("X Rotation", 0.0, -180.0, 180.0, 1.0, "°"));
   private final SliderSetting rotationY = this.register(new SliderSetting("Y Rotation", 0.0, -180.0, 180.0, 1.0, "°"));
   private final SliderSetting rotationZ = this.register(new SliderSetting("Z Rotation", 0.0, -180.0, 180.0, 1.0, "°"));
   private final SliderSetting scale = this.register(new SliderSetting("Scale", 1.0, 0.2, 3.0, 0.05, "x"));
   private final BooleanSetting sideShieldOverlay = this.register(new BooleanSetting("Side Shield", false));
   private Boolean lastAppliedSideShield;

   public ShieldPositionModule() {
      super("Shield Position", "Lets you move, rotate and resize your first-person shield.", Category.RENDER, false);
      this.offsetX.group = "Position";
      this.offsetY.group = "Position";
      this.offsetZ.group = "Position";
      this.rotationX.group = "Rotation";
      this.rotationY.group = "Rotation";
      this.rotationZ.group = "Rotation";
      this.scale.group = "Scale";
      this.sideShieldOverlay.group = "Overlay";
   }

   @Override
   public void tick() {
      boolean desired = this.isEnabled() && this.sideShieldOverlay.get();
      if (this.lastAppliedSideShield == null || this.lastAppliedSideShield != desired) {
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         String packId = findPackId(manager, "side_shield_overlay");
         if (packId != null) {
            if (desired) {
               manager.addPack(packId);
            } else {
               manager.removePack(packId);
            }

            mc.reloadResourcePacks();
            this.lastAppliedSideShield = desired;
         }
      }
   }

   private static String findPackId(PackRepository manager, String fragment) {
      return manager.getAvailableIds().stream().filter(id -> id.endsWith(fragment)).findFirst().orElse(null);
   }

   public float offsetX() {
      return (float)this.offsetX.get().doubleValue();
   }

   public float offsetY() {
      return (float)this.offsetY.get().doubleValue();
   }

   public float offsetZ() {
      return (float)this.offsetZ.get().doubleValue();
   }

   public float rotationX() {
      return (float)this.rotationX.get().doubleValue();
   }

   public float rotationY() {
      return (float)this.rotationY.get().doubleValue();
   }

   public float rotationZ() {
      return (float)this.rotationZ.get().doubleValue();
   }

   public float scale() {
      return (float)this.scale.get().doubleValue();
   }
}
