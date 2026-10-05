package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class FoodOverlayModule extends Module {
   private static final int ICONS = 10;
   private static final int ICON_STEP = 8;
   private final BooleanSetting saturation = this.register(new BooleanSetting("Saturation", true));
   private final ColorSetting saturationColor = this.register(new ColorSetting("Saturation Color", -11702).exemptFromGlobalColor());
   private final BooleanSetting restored = this.register(new BooleanSetting("Food Preview", true));
   private final ColorSetting restoredColor = this.register(new ColorSetting("Preview Color", -10879122).exemptFromGlobalColor());
   private final BooleanSetting waste = this.register(new BooleanSetting("Show Waste", true));
   private final ColorSetting wasteColor = this.register(new ColorSetting("Waste Color", -43691).exemptFromGlobalColor());
   private final SliderSetting offsetX = this.register(new SliderSetting("Offset X", 0.0, -200.0, 200.0, 1.0, "px"));
   private final SliderSetting offsetY = this.register(new SliderSetting("Offset Y", 0.0, -200.0, 200.0, 1.0, "px"));

   public FoodOverlayModule() {
      super("Food Overlay", "Saturation on the hunger bar, and what the food in your hand would restore.", Category.HUD, false);
      this.saturation.group = "Saturation";
      this.saturationColor.group = "Saturation";
      this.restored.group = "Preview";
      this.restoredColor.group = "Preview";
      this.waste.group = "Preview";
      this.wasteColor.group = "Preview";
      this.offsetX.group = "Position";
      this.offsetY.group = "Position";
   }

   @Override
   public void init() {
      HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lyfw-client", "food_overlay"), (context, tickCounter) -> {
         Minecraft mc = Minecraft.getInstance();
         if (this.isEnabled() && mc.player != null && !mc.options.hideGui && !mc.player.isCreative() && !mc.player.isSpectator()) {
            this.draw(context, mc.player);
         }
      });
   }

   private void draw(GuiGraphics context, Player player) {
      FoodData hunger = player.getFoodData();
      int right = context.guiWidth() / 2 + 91 + this.offsetX.getInt();
      int top = context.guiHeight() - 39 + this.offsetY.getInt();
      if (this.saturation.get()) {
         this.drawSaturation(context, hunger, right, top);
      }

      if (this.restored.get()) {
         this.drawPreview(context, player, hunger, right, top);
      }
   }

   private void drawSaturation(GuiGraphics context, FoodData hunger, int right, int top) {
      float level = Math.min(hunger.getSaturationLevel(), 20.0F);
      int pixels = Math.round(level / 20.0F * 80.0F);
      if (pixels > 0) {
         int left = right - pixels + 1;
         context.fill(left, top - 2, right + 1, top - 1, this.saturationColor.get());
      }
   }

   private void drawPreview(GuiGraphics context, Player player, FoodData hunger, int right, int top) {
      FoodProperties food = heldFood(player);
      if (food != null) {
         int current = hunger.getFoodLevel();
         int gained = Math.min(20 - current, food.nutrition());
         int wasted = food.nutrition() - gained;
         int startX = right - Math.round(current / 20.0F * 80.0F);
         int gainedW = Math.round(gained / 20.0F * 80.0F);
         int barTop = top + 8 + 1;
         if (gainedW > 0) {
            context.fill(startX - gainedW, barTop, startX, barTop + 2, this.restoredColor.get());
         }

         if (this.waste.get() && wasted > 0) {
            int wastedW = Math.round(wasted / 20.0F * 80.0F);
            int wasteLeft = right - 80 - wastedW;
            context.fill(wasteLeft, barTop, wasteLeft + wastedW, barTop + 2, this.wasteColor.get());
         }
      }
   }

   private static FoodProperties heldFood(Player player) {
      for (ItemStack stack : new ItemStack[]{player.getMainHandItem(), player.getOffhandItem()}) {
         FoodProperties food = (FoodProperties)stack.get(DataComponents.FOOD);
         if (food != null) {
            return food;
         }
      }

      return null;
   }
}
