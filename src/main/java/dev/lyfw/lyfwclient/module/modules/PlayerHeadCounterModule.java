package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerHeadCounterModule extends HudModule {
   public PlayerHeadCounterModule() {
      super("Head Counter", "Shows how many player heads are currently in your inventory.", false, 4.0, 118.0);
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      context.drawString(mc.font, Component.literal("Heads: " + countPlayerHeads()), 0, 0, -1);
   }

   @Override
   protected int contentWidth() {
      return Minecraft.getInstance().font.width("Heads: " + countPlayerHeads());
   }

   @Override
   protected int contentHeight() {
      return 9;
   }

   private static int countPlayerHeads() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return 0;
      } else {
         Inventory inventory = mc.player.getInventory();
         int count = 0;

         for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.PLAYER_HEAD)) {
               count += stack.getCount();
            }
         }

         return count;
      }
   }
}
