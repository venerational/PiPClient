package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;

public class SoundControlModule extends Module {
   private final SliderSetting master = this.register(this.volume("All Sounds", "General"));
   private final SliderSetting totem = this.register(this.volume("Totem Pop", "Combat"));
   private final SliderSetting crystal = this.register(this.volume("End Crystals", "Combat"));
   private final SliderSetting anchor = this.register(this.volume("Respawn Anchor", "Combat"));
   private final SliderSetting explosion = this.register(this.volume("Explosions", "Combat"));
   private final SliderSetting shieldBreak = this.register(this.volume("Shield Break", "Combat"));
   private final SliderSetting shieldBlock = this.register(this.volume("Shield Block", "Combat"));
   private final SliderSetting bow = this.register(this.volume("Bows & Arrows", "Combat"));
   private final SliderSetting hurt = this.register(this.volume("Damage & Death", "Combat"));
   private final SliderSetting anvil = this.register(this.volume("Anvil", "World"));
   private final SliderSetting experience = this.register(this.volume("Experience", "World"));
   private final SliderSetting eating = this.register(this.volume("Eating & Drinking", "World"));
   private final SliderSetting fire = this.register(this.volume("Fire & Lava", "World"));
   private final SliderSetting portal = this.register(this.volume("Portals & Pearls", "World"));
   private final SliderSetting steps = this.register(this.volume("Footsteps", "World"));
   private final SliderSetting doors = this.register(this.volume("Doors & Chests", "World"));
   private final SliderSetting water = this.register(this.volume("Water & Bubbles", "World"));
   private final SliderSetting villagers = this.register(this.volume("Villagers", "World"));
   private final SliderSetting notes = this.register(this.volume("Note Blocks", "World"));
   private final SliderSetting music = this.register(this.volume("Music & Records", "World"));
   private final List<SoundControlModule.Rule> rules = new ArrayList<>();

   public SoundControlModule() {
      super("Sound Control", "Volume for individual sounds - totem pops, crystals, anchors, shield breaks - not just whole categories.", Category.MISC, false);
      this.rule(this.totem, "totem");
      this.rule(this.crystal, "end_crystal", "crystal");
      this.rule(this.anchor, "respawn_anchor");
      this.rule(this.shieldBreak, "shield.break");
      this.rule(this.shieldBlock, "shield.block");
      this.rule(this.explosion, "explode", "explosion");
      this.rule(this.bow, "arrow", "bow", "trident");
      this.rule(this.hurt, "hurt", "death", "damage");
      this.rule(this.anvil, "anvil");
      this.rule(this.experience, "experience");
      this.rule(this.eating, "eat", "drink", "burp");
      this.rule(this.fire, "fire", "lava", "extinguish");
      this.rule(this.portal, "portal", "ender_pearl", "chorus_fruit", "enderman.teleport");
      this.rule(this.doors, "door", "chest", "barrel", "shulker_box");
      this.rule(this.water, "water", "bubble", "swim", "splash");
      this.rule(this.villagers, "villager");
      this.rule(this.notes, "note_block");
      this.rule(this.music, "music", "record");
      this.rule(this.steps, ".step");
   }

   private SliderSetting volume(String name, String group) {
      SliderSetting setting = new SliderSetting(name, 100.0, 0.0, 300.0, 5.0, "%");
      setting.group = group;
      return setting;
   }

   private void rule(SliderSetting setting, String... matches) {
      this.rules.add(new SoundControlModule.Rule(setting, matches));
   }

   public float multiplier(Identifier id) {
      if (this.isEnabled() && id != null) {
         String path = id.getPath();

         for (SoundControlModule.Rule rule : this.rules) {
            if (rule.matches(path)) {
               return fraction(rule.setting()) * fraction(this.master);
            }
         }

         return fraction(this.master);
      } else {
         return 1.0F;
      }
   }

   private static float fraction(SliderSetting setting) {
      return (float)(setting.get() / 100.0);
   }

   private record Rule(SliderSetting setting, String[] matches) {
      boolean matches(String path) {
         for (String match : this.matches) {
            if (path.contains(match)) {
               return true;
            }
         }

         return false;
      }
   }
}
