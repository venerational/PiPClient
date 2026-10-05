package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;

public class DeathAnimationModule extends Module {
   private final BooleanSetting playersOnly = this.register(new BooleanSetting("Players Only", true));
   private final BooleanSetting animation = this.register(new BooleanSetting("Fall Animation", false));
   private final SliderSetting duration = this.register(new SliderSetting("Duration", 1.0, 0.0, 5.0, 0.1, "s"));
   private final Set<Integer> held = new HashSet<>();
   private boolean releasing;

   public DeathAnimationModule() {
      super(
         "Death Animation",
         "How long dead players stay on screen (0 to 5 seconds) and whether they fall over - or switch the fall off, like \"NoDeathAnimation\" by Walksy.",
         Category.RENDER,
         false
      );
   }

   public static DeathAnimationModule get() {
      return ModuleManager.get("Death Animation") instanceof DeathAnimationModule death && death.isEnabled() ? death : null;
   }

   public boolean covers(boolean player) {
      return player || !this.playersOnly.get();
   }

   private boolean covers(Entity entity) {
      return entity instanceof LivingEntity && this.covers(entity instanceof Player);
   }

   private float durationTicks() {
      return (float)(this.duration.get() * 20.0);
   }

   public float fall(float deathTime) {
      if (!this.animation.get()) {
         return 0.0F;
      } else {
         float ticks = Math.max(1.0F, this.durationTicks());
         float f = Mth.sqrt(Math.max(0.0F, (deathTime - 1.0F) / ticks * 1.6F));
         return Math.min(1.0F, f);
      }
   }

   public boolean shouldHold(Entity entity) {
      Minecraft mc = Minecraft.getInstance();
      if (!this.releasing && entity != null && entity != mc.player && this.covers(entity)) {
         LivingEntity living = (LivingEntity)entity;
         if (living.isDeadOrDying() && !(living.deathTime >= this.durationTicks())) {
            this.held.add(entity.getId());
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void tick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
         this.held.clear();
      } else {
         boolean on = this.isEnabled();
         Iterator<Integer> it = this.held.iterator();

         while (it.hasNext()) {
            int id = it.next();
            Entity entity = mc.level.getEntity(id);
            if (!(entity instanceof LivingEntity living && living.isDeadOrDying())) {
               it.remove();
            } else if (!on || !this.covers(entity) || living.deathTime >= this.durationTicks()) {
               it.remove();
               this.release(mc, id);
            }
         }

         if (on && this.durationTicks() < 20.0F) {
            Set<Integer> due = new HashSet<>();

            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity != mc.player
                  && this.covers(entity)
                  && entity instanceof LivingEntity living
                  && living.isDeadOrDying()
                  && living.deathTime >= this.durationTicks()) {
                  due.add(entity.getId());
               }
            }

            for (int id : due) {
               this.release(mc, id);
            }
         }
      }
   }

   private void release(Minecraft mc, int id) {
      this.releasing = true;

      try {
         mc.level.removeEntity(id, RemovalReason.DISCARDED);
      } finally {
         this.releasing = false;
      }
   }
}
