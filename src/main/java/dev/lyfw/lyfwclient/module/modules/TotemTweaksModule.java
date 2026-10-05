package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class TotemTweaksModule extends Module {
   private static final int[] TIERS = new int[]{20, 40, 60, 80};
   private final BooleanSetting changeTotemSize = this.register(new BooleanSetting("Change Totem Size", false));
   private final SliderSetting totemSize = this.register(new SliderSetting("Totem Size", 1.0, 0.1, 3.0, 0.05, "x"));
   private final BooleanSetting changePopSize = this.register(new BooleanSetting("Change Pop Size", false));
   private final SliderSetting popSize = this.register(new SliderSetting("Pop Size", 1.0, 0.1, 3.0, 0.05, "x"));
   private final BooleanSetting disableEquipAnimation = this.register(new BooleanSetting("Disable Equip Animation", false));
   private final BooleanSetting disablePopAnimation = this.register(new BooleanSetting("Disable Pop Animation", false));
   private final SliderSetting popAnimationSpeed = this.register(new SliderSetting("Pop Animation Speed", 40.0, 5.0, 100.0, 1.0, " ticks"));
   private final BooleanSetting lockRotationPosition = this.register(new BooleanSetting("Lock Rotation Position", false));
   private final BooleanSetting disableRotations = this.register(new BooleanSetting("Disable Rotations", false));
   private final BooleanSetting totemOpacity = this.register(new BooleanSetting("Totem Opacity", false));
   private final SliderSetting totemOpacityAmount = this.register(new SliderSetting("Totem Opacity Amount", 60.0, 20.0, 80.0, 20.0, "%"));
   private int lastAppliedTier = -1;
   private boolean lastAppliedEnabled = false;

   public TotemTweaksModule() {
      super(
         "Totem Tweaks",
         "Customizes the held totem's size and equip animation, the totem-pop overlay's speed/size/rotation, and the totem's opacity while held.",
         Category.RENDER,
         false
      );
      this.changeTotemSize.group = "General";
      this.totemSize.group = "General";
      this.changePopSize.group = "General";
      this.popSize.group = "General";
      this.disableEquipAnimation.group = "General";
      this.disablePopAnimation.group = "Totem Pop Animation";
      this.popAnimationSpeed.group = "Totem Pop Animation";
      this.lockRotationPosition.group = "Totem Pop Animation";
      this.disableRotations.group = "Totem Pop Animation";
      this.totemOpacity.group = "Totem Opacity";
      this.totemOpacityAmount.group = "Totem Opacity";
   }

   public boolean changeTotemSize() {
      return this.isEnabled() && this.changeTotemSize.get();
   }

   public float totemSize() {
      return (float)this.totemSize.get().doubleValue();
   }

   public boolean changePopSize() {
      return this.isEnabled() && this.changePopSize.get();
   }

   public float popSize() {
      return (float)this.popSize.get().doubleValue();
   }

   public boolean disableEquipAnimation() {
      return this.isEnabled() && this.disableEquipAnimation.get();
   }

   public boolean disablePopAnimation() {
      return this.isEnabled() && this.disablePopAnimation.get();
   }

   public int popAnimationSpeed() {
      return this.popAnimationSpeed.getInt();
   }

   public boolean lockRotationPosition() {
      return this.isEnabled() && this.lockRotationPosition.get();
   }

   public boolean disableRotations() {
      return this.isEnabled() && this.disableRotations.get();
   }

   @Override
   public void tick() {
      boolean enabled = this.isEnabled() && this.totemOpacity.get();
      int tier = nearestTier(this.totemOpacityAmount.getInt());
      if (enabled != this.lastAppliedEnabled || tier != this.lastAppliedTier) {
         this.lastAppliedEnabled = enabled;
         this.lastAppliedTier = tier;
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         boolean packsChanged = false;

         for (int t : TIERS) {
            String id = findPackId(manager, "totem_opacity_" + t);
            if (id != null) {
               boolean wanted = enabled && t == tier;
               packsChanged |= wanted ? manager.addPack(id) : manager.removePack(id);
            }
         }

         if (packsChanged) {
            mc.reloadResourcePacks();
         }
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
