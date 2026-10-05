package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class TransparentPlayersModule extends Module {
   private final SliderSetting bodyOpacity = this.register(new SliderSetting("Body Opacity", 55.0, 0.0, 100.0, 1.0, "%"));
   private final SliderSetting armorOpacity = this.register(new SliderSetting("Armor Opacity", 55.0, 0.0, 100.0, 1.0, "%"));
   private final EnumSetting<TransparentPlayersModule.Who> appliesTo = this.register(new EnumSetting<>("Applies To", TransparentPlayersModule.Who.OTHERS));
   private final BooleanSetting onlyWhenDamaged = this.register(new BooleanSetting("Only When Damaged", false));

   public TransparentPlayersModule() {
      super("Transparent Players", "Fades players out so you can see through them, leaving worn armor solid so gear stays readable.", Category.RENDER, false);
   }

   public float armorAlpha() {
      return (float)(this.armorOpacity.get() / 100.0);
   }

   public float bodyAlpha() {
      return (float)(this.bodyOpacity.get() / 100.0);
   }

   public boolean appliesTo(LivingEntityRenderState state) {
      if (this.isEnabled() && state instanceof AvatarRenderState player) {
         if (this.onlyWhenDamaged.get() && !state.hasRedOverlay) {
            return false;
         } else {
            Minecraft mc = Minecraft.getInstance();
            boolean isSelf = mc.player != null && player.id == mc.player.getId();

            return switch ((TransparentPlayersModule.Who)this.appliesTo.get()) {
               case OTHERS -> !isSelf;
               case EVERYONE -> true;
               case ONLY_ME -> isSelf;
            };
         }
      } else {
         return false;
      }
   }

   public static int withAlpha(int color, float alpha) {
      int existing = color >>> 24;
      int scaled = Math.round(existing * Math.max(0.0F, Math.min(1.0F, alpha)));
      return Math.max(0, Math.min(255, scaled)) << 24 | color & 16777215;
   }

   public static enum Who {
      OTHERS("Everyone Else"),
      EVERYONE("Everyone"),
      ONLY_ME("Only Me");

      public final String title;

      private Who(String title) {
         this.title = title;
      }
   }
}
