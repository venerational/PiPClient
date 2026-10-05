package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public class ItemCounterModule extends Module {
   private static final int DYNAMIC_ZERO_COLOR = -5592406;
   private static final int DYNAMIC_LOW_COLOR = -43691;
   private static final int DYNAMIC_OK_COLOR = -11141291;
   private static final int WHITE_COLOR = -1;
   private static final int GOLD_COLOR = -22016;
   private static final int AQUA_COLOR = -11141121;
   private static final Map<String, String> POTION_ALIASES = Map.ofEntries(
      Map.entry("health", "healing"),
      Map.entry("heal", "healing"),
      Map.entry("instant_health", "healing"),
      Map.entry("harm", "harming"),
      Map.entry("damage", "harming"),
      Map.entry("instant_damage", "harming"),
      Map.entry("speed", "swiftness"),
      Map.entry("jump", "leaping"),
      Map.entry("jump_boost", "leaping"),
      Map.entry("regen", "regeneration"),
      Map.entry("invis", "invisibility"),
      Map.entry("fire_res", "fire_resistance"),
      Map.entry("night_vis", "night_vision"),
      Map.entry("str", "strength")
   );
   private static final Item[] POTION_ITEMS = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};
   private final TextSetting itemName = this.register(new TextSetting("Item", "totem_of_undying"));
   private final BooleanSetting countOffhand = this.register(new BooleanSetting("Count Offhand", true));
   private final SliderSetting lowCount = this.register(new SliderSetting("Low Count", 9.0, 0.0, 64.0, 1.0, ""));
   private final BooleanSetting hideOnZero = this.register(new BooleanSetting("Hide On Zero", false));
   private final SliderSetting size = this.register(new SliderSetting("Size", 1.0, 0.25, 3.0, 0.05, "x"));
   private final EnumSetting<ItemCounterModule.Position> position = this.register(new EnumSetting<>("Position", ItemCounterModule.Position.TOP_HOTBAR));
   private final BooleanSetting showItemIcon = this.register(new BooleanSetting("Show Item Icon", true));
   private final BooleanSetting showTextShadow = this.register(new BooleanSetting("Show Text Shadow", true));
   private final EnumSetting<ItemCounterModule.ColorMode> colorMode = this.register(new EnumSetting<>("Color Mode", ItemCounterModule.ColorMode.DYNAMIC));
   private String resolvedFor;
   private Item resolved;
   private Identifier resolvedPotion;
   private boolean resolvedExactTier;
   private ItemStack iconStack = ItemStack.EMPTY;

   public ItemCounterModule() {
      super(
         "Item Counter",
         "Counts one item in your inventory beside the hotbar. Type any item id in the Item setting - totem_of_undying, ender_pearl, health_potion.",
         Category.HUD,
         false
      );
   }

   @Override
   public void init() {
      HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lyfw-client", "item_counter"), this::render);
   }

   private void resolve() {
      String raw = this.itemName.get();
      if (!Objects.equals(raw, this.resolvedFor)) {
         this.resolvedFor = raw;
         this.resolved = lookup(raw);
         ItemCounterModule.PotionTarget target = this.resolved == null ? lookupPotion(raw) : null;
         this.resolvedPotion = target == null ? null : target.id();
         this.resolvedExactTier = target != null && target.exactTier();
         this.iconStack = this.buildIcon();
      }
   }

   private ItemStack buildIcon() {
      if (this.resolved != null) {
         return new ItemStack(this.resolved);
      } else if (this.resolvedPotion == null) {
         return ItemStack.EMPTY;
      } else {
         Reference<Potion> entry = (Reference<Potion>)BuiltInRegistries.POTION.get(ResourceKey.create(Registries.POTION, this.resolvedPotion)).orElse(null);
         return entry == null ? new ItemStack(Items.POTION) : PotionContents.createItemStack(Items.POTION, entry);
      }
   }

   private boolean resolved() {
      this.resolve();
      return this.resolved != null || this.resolvedPotion != null;
   }

   private static String normalise(String raw) {
      return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
   }

   private static Item lookup(String raw) {
      String cleaned = normalise(raw);
      if (cleaned.isEmpty()) {
         return null;
      } else {
         Identifier id = Identifier.tryParse(cleaned.contains(":") ? cleaned : "minecraft:" + cleaned);
         if (id == null) {
            return null;
         } else {
            Item item = (Item)BuiltInRegistries.ITEM.getOptional(id).orElse(null);
            return item == Items.AIR ? null : item;
         }
      }
   }

   private static ItemCounterModule.PotionTarget lookupPotion(String raw) {
      String cleaned = normalise(raw);
      String variant = null;
      if (cleaned.startsWith("potion:")) {
         variant = cleaned.substring("potion:".length());
      } else if (cleaned.startsWith("potion_of_")) {
         variant = cleaned.substring("potion_of_".length());
      } else if (cleaned.endsWith("_potion")) {
         variant = cleaned.substring(0, cleaned.length() - "_potion".length());
      }

      if (variant != null && !variant.isEmpty()) {
         variant = POTION_ALIASES.getOrDefault(variant, variant);
         boolean exactTier = variant.startsWith("strong_") || variant.startsWith("long_");
         Identifier id = Identifier.tryParse(variant.contains(":") ? variant : "minecraft:" + variant);
         return id != null && BuiltInRegistries.POTION.getOptional(id).isPresent() ? new ItemCounterModule.PotionTarget(id, exactTier) : null;
      } else {
         return null;
      }
   }

   private int itemCount(Minecraft mc) {
      if (!this.resolved()) {
         return 0;
      } else {
         Inventory inventory = mc.player.getInventory();
         int total = 0;

         for (int i = 0; i < inventory.getContainerSize(); i++) {
            total += this.countIn(inventory.getItem(i));
         }

         if (!this.countOffhand.get()) {
            total -= this.countIn(mc.player.getOffhandItem());
         }

         return total;
      }
   }

   private int countIn(ItemStack stack) {
      if (this.resolved != null) {
         return stack.is(this.resolved) ? stack.getCount() : 0;
      } else if (this.resolvedPotion == null) {
         return 0;
      } else {
         boolean isPotion = false;

         for (Item form : POTION_ITEMS) {
            if (stack.is(form)) {
               isPotion = true;
               break;
            }
         }

         if (!isPotion) {
            return 0;
         } else {
            PotionContents contents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && !contents.potion().isEmpty()) {
               return contents.potion().get().unwrapKey().map(key -> this.matchesPotion(key.identifier())).orElse(false) ? stack.getCount() : 0;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean matchesPotion(Identifier id) {
      if (this.resolvedExactTier) {
         return id.equals(this.resolvedPotion);
      } else if (!id.getNamespace().equals(this.resolvedPotion.getNamespace())) {
         return false;
      } else {
         String base = this.resolvedPotion.getPath();
         String path = id.getPath();
         return path.equals(base) || path.equals("strong_" + base) || path.equals("long_" + base);
      }
   }

   private int resolveColor(int count) {
      return switch ((ItemCounterModule.ColorMode)this.colorMode.get()) {
         case DYNAMIC -> count == 0 ? -5592406 : (count <= this.lowCount.getInt() ? -43691 : -11141291);
         case WHITE -> -1;
         case GOLD -> -22016;
         case AQUA -> -11141121;
      };
   }

   private void render(GuiGraphics context, DeltaTracker tickCounter) {
      if (this.isEnabled()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null && !mc.options.hideGui) {
            boolean known = this.resolved();
            int count = this.itemCount(mc);
            if (count != 0 || !this.hideOnZero.get() || !known) {
               int color = known ? this.resolveColor(count) : -43691;
               String countText = known ? String.valueOf(count) : "?";
               Font textRenderer = mc.font;
               int textWidth = textRenderer.width(countText);
               int scaledWidth = mc.getWindow().getGuiScaledWidth();
               int scaledHeight = mc.getWindow().getGuiScaledHeight();
               float widgetSize = (float)this.size.get().doubleValue();
               boolean icon = this.showItemIcon.get() && !this.iconStack.isEmpty();
               int guiWidth = (int)((icon ? 16 : textWidth) * widgetSize);
               int guiHeight = (int)((icon ? 26 : 9) * widgetSize);
               int x = 0;
               int y = 0;
               switch ((ItemCounterModule.Position)this.position.get()) {
                  case TOP_HOTBAR:
                     x = scaledWidth / 2 - guiWidth / 2;
                     y = scaledHeight - 40 - guiHeight;
                     break;
                  case LEFT_HOTBAR:
                     x = scaledWidth / 2 - 91 - guiWidth - 5;
                     y = scaledHeight - guiHeight - 2;
                     break;
                  case RIGHT_HOTBAR:
                     x = scaledWidth / 2 + 91 + 5;
                     y = scaledHeight - guiHeight - 2;
                     break;
                  case TOP_LEFT:
                     x = 5;
                     y = 5;
                     break;
                  case TOP_RIGHT:
                     x = scaledWidth - guiWidth - 5;
                     y = 5;
                     break;
                  case BOTTOM_LEFT:
                     x = 5;
                     y = scaledHeight - guiHeight - 5;
                     break;
                  case BOTTOM_RIGHT:
                     x = scaledWidth - guiWidth - 5;
                     y = scaledHeight - guiHeight - 5;
               }

               context.pose().pushMatrix();
               context.pose().translate(x, y);
               context.pose().scale(widgetSize, widgetSize);
               if (icon) {
                  context.renderItem(this.iconStack, 0, 0);
                  context.drawString(textRenderer, countText, 20, 4, color, this.showTextShadow.get());
               } else {
                  context.drawString(textRenderer, countText, 0, 0, color, this.showTextShadow.get());
               }

               context.pose().popMatrix();
            }
         }
      }
   }

   public static enum ColorMode {
      DYNAMIC,
      WHITE,
      GOLD,
      AQUA;
   }

   public static enum Position {
      TOP_HOTBAR,
      LEFT_HOTBAR,
      RIGHT_HOTBAR,
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT;
   }

   private record PotionTarget(Identifier id, boolean exactTier) {
   }
}
