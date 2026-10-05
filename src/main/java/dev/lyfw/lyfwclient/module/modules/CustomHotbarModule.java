package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.render.PadImage;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CustomHotbarModule extends HudModule {
   private static final int SLOTS = 9;
   private static final int ITEM = 16;
   private final SliderSetting slotSize = this.register(new SliderSetting("Slot Size", 20.0, 12.0, 40.0, 1.0, "px"));
   private final SliderSetting gap = this.register(new SliderSetting("Gap", 3.0, 0.0, 24.0, 1.0, "px"));
   private final TextSetting backgroundImage = this.register(new TextSetting("Background Image", ""));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", 0));
   private final SliderSetting backgroundPad = this.register(new SliderSetting("Background Padding", 4.0, 0.0, 24.0, 1.0, "px"));
   private final ColorSetting defaultSlot = this.register(new ColorSetting("Default Slot Color", -1072424924));
   private final ColorSetting defaultBorder = this.register(new ColorSetting("Default Border", -2143270306));
   private final ColorSetting selectedSlot = this.register(new ColorSetting("Selected Slot Color", -534762938));
   private final ColorSetting selectedBorder = this.register(new ColorSetting("Selected Border", -6242305));
   private final BooleanSetting selectedGlow = this.register(new BooleanSetting("Selected Glow", true));
   private final SliderSetting editingSlot = this.register(new SliderSetting("Editing Slot", 1.0, 1.0, 9.0, 1.0, ""));
   private final SliderSetting slotOffsetX = this.register(new SliderSetting("Offset X", 0.0, -120.0, 120.0, 1.0, "px"));
   private final SliderSetting slotOffsetY = this.register(new SliderSetting("Offset Y", 0.0, -60.0, 60.0, 1.0, "px"));
   private final BooleanSetting slotCustomColor = this.register(new BooleanSetting("Custom Color", false));
   private final ColorSetting slotColor = this.register(new ColorSetting("Slot Color", -1072424924));
   private final ColorSetting slotBorder = this.register(new ColorSetting("Slot Border", -2143270306));
   private final TextSetting slotData = this.register(new TextSetting("Slot Data", ""));
   private final int[] offsetX = new int[9];
   private final int[] offsetY = new int[9];
   private final int[] colors = new int[9];
   private final int[] borders = new int[9];
   private final boolean[] custom = new boolean[9];
   private String parsedFrom;
   private int lastEditing = -1;
   private String loadedImageFor;
   private PadImage loadedImage;
   private int imageGeneration;

   public CustomHotbarModule() {
      super("Custom Hotbar", "A hotbar drawn from scratch: its own background, and every slot movable and colorable on its own.", false, 4.0, 190.0);
      this.slotSize.group = "Layout";
      this.gap.group = "Layout";
      this.backgroundImage.group = "Background";
      this.backgroundColor.group = "Background";
      this.backgroundPad.group = "Background";
      this.defaultSlot.group = "Colors";
      this.defaultBorder.group = "Colors";
      this.selectedSlot.group = "Colors";
      this.selectedBorder.group = "Colors";
      this.selectedGlow.group = "Colors";
      this.editingSlot.group = "Per Slot";
      this.slotOffsetX.group = "Per Slot";
      this.slotOffsetY.group = "Per Slot";
      this.slotCustomColor.group = "Per Slot";
      this.slotColor.group = "Per Slot";
      this.slotBorder.group = "Per Slot";
      this.slotData.group = "Per Slot";
   }

   private int size() {
      return this.slotSize.getInt();
   }

   private int step() {
      return this.size() + this.gap.getInt();
   }

   private void syncEditor() {
      this.parse();
      int index = Math.max(0, Math.min(8, this.editingSlot.getInt() - 1));
      if (index != this.lastEditing) {
         this.lastEditing = index;
         this.slotOffsetX.set((double)this.offsetX[index]);
         this.slotOffsetY.set((double)this.offsetY[index]);
         this.slotCustomColor.set(this.custom[index]);
         this.slotColor.set(this.colors[index]);
         this.slotBorder.set(this.borders[index]);
      } else {
         int x = this.slotOffsetX.getInt();
         int y = this.slotOffsetY.getInt();
         boolean on = this.slotCustomColor.get();
         int color = this.slotColor.get();
         int border = this.slotBorder.get();
         if (x != this.offsetX[index] || y != this.offsetY[index] || on != this.custom[index] || color != this.colors[index] || border != this.borders[index]) {
            this.offsetX[index] = x;
            this.offsetY[index] = y;
            this.custom[index] = on;
            this.colors[index] = color;
            this.borders[index] = border;
            this.write();
         }
      }
   }

   private void parse() {
      String raw = this.slotData.get() == null ? "" : this.slotData.get();
      if (!raw.equals(this.parsedFrom)) {
         this.parsedFrom = raw;
         this.lastEditing = -1;

         for (int i = 0; i < 9; i++) {
            this.offsetX[i] = 0;
            this.offsetY[i] = 0;
            this.custom[i] = false;
            this.colors[i] = this.defaultSlot.get();
            this.borders[i] = this.defaultBorder.get();
         }

         String[] entries = raw.split(";", -1);

         for (int i = 0; i < Math.min(9, entries.length); i++) {
            String[] parts = entries[i].split(",", -1);
            if (parts.length >= 2) {
               try {
                  this.offsetX[i] = Integer.parseInt(parts[0].trim());
                  this.offsetY[i] = Integer.parseInt(parts[1].trim());
                  String color = parts.length > 2 ? parts[2].trim() : "";
                  String border = parts.length > 3 ? parts[3].trim() : "";
                  this.custom[i] = !color.isEmpty();
                  if (this.custom[i]) {
                     this.colors[i] = (int)Long.parseLong(color, 16);
                     this.borders[i] = border.isEmpty() ? this.defaultBorder.get() : (int)Long.parseLong(border, 16);
                  }
               } catch (NumberFormatException var7) {
               }
            }
         }
      }
   }

   private void write() {
      StringBuilder out = new StringBuilder();

      for (int i = 0; i < 9; i++) {
         if (i > 0) {
            out.append(';');
         }

         out.append(this.offsetX[i]).append(',').append(this.offsetY[i]).append(',');
         if (this.custom[i]) {
            out.append(Integer.toHexString(this.colors[i])).append(',').append(Integer.toHexString(this.borders[i]));
         } else {
            out.append(',');
         }
      }

      String text = out.toString();
      this.parsedFrom = text;
      this.slotData.set(text);
   }

   private int slotX(int index) {
      return index * this.step() + this.offsetX[index];
   }

   private int slotY(int index) {
      return this.offsetY[index];
   }

   private int[] bounds() {
      this.parse();
      int size = this.size();
      int pad = this.backgroundPad.getInt();
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (int i = 0; i < 9; i++) {
         minX = Math.min(minX, this.slotX(i));
         minY = Math.min(minY, this.slotY(i));
         maxX = Math.max(maxX, this.slotX(i) + size);
         maxY = Math.max(maxY, this.slotY(i) + size);
      }

      return new int[]{minX - pad, minY - pad, maxX - minX + pad * 2, maxY - minY + pad * 2};
   }

   @Override
   protected int contentWidth() {
      return this.bounds()[2];
   }

   @Override
   protected int contentHeight() {
      return this.bounds()[3];
   }

   @Override
   protected int contentOffsetX() {
      return this.bounds()[0];
   }

   @Override
   protected int contentOffsetY() {
      return this.bounds()[1];
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         this.syncEditor();
         int[] box = this.bounds();
         PadImage image = this.image();
         if (image != null) {
            context.blit(RenderPipelines.GUI_TEXTURED, image.currentFrame(), box[0], box[1], 0.0F, 0.0F, box[2], box[3], box[2], box[3]);
         } else if (this.backgroundColor.get() >>> 24 != 0) {
            context.fill(box[0], box[1], box[0] + box[2], box[1] + box[3], this.backgroundColor.get());
         }

         Inventory inventory = mc.player.getInventory();
         NonNullList<ItemStack> stacks = inventory.getNonEquipmentItems();
         int selected = inventory.getSelectedSlot();
         int size = this.size();

         for (int i = 0; i < 9 && i < stacks.size(); i++) {
            int x = this.slotX(i);
            int y = this.slotY(i);
            boolean isSelected = i == selected;
            this.drawSlot(context, x, y, size, i, isSelected);
            ItemStack stack = (ItemStack)stacks.get(i);
            if (!stack.isEmpty()) {
               int itemX = x + (size - 16) / 2;
               int itemY = y + (size - 16) / 2;
               context.renderItem(stack, itemX, itemY);
               context.renderItemDecorations(mc.font, stack, itemX, itemY);
            }
         }
      }
   }

   private void drawSlot(GuiGraphics context, int x, int y, int size, int index, boolean selected) {
      int fill = selected ? this.selectedSlot.get() : (this.custom[index] ? this.colors[index] : this.defaultSlot.get());
      int edge = selected ? this.selectedBorder.get() : (this.custom[index] ? this.borders[index] : this.defaultBorder.get());
      context.fill(x, y, x + size, y + size, fill);
      cutCorners(context, x, y, size, size);
      if (selected && this.selectedGlow.get()) {
         glowBorder(context, x, y, size, size, edge);
      } else {
         border(context, x, y, size, size, edge);
      }
   }

   private static void cutCorners(GuiGraphics context, int x, int y, int w, int h) {
      context.fill(x, y, x + 1, y + 1, 0);
      context.fill(x + w - 1, y, x + w, y + 1, 0);
      context.fill(x, y + h - 1, x + 1, y + h, 0);
      context.fill(x + w - 1, y + h - 1, x + w, y + h, 0);
   }

   private static void border(GuiGraphics context, int x, int y, int w, int h, int color) {
      context.fill(x + 1, y, x + w - 1, y + 1, color);
      context.fill(x + 1, y + h - 1, x + w - 1, y + h, color);
      context.fill(x, y + 1, x + 1, y + h - 1, color);
      context.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
   }

   private static void glowBorder(GuiGraphics context, int x, int y, int w, int h, int rgb) {
      int[] alphas = new int[]{48, 96, 255};

      for (int i = 0; i < alphas.length; i++) {
         int inset = alphas.length - 1 - i;
         int color = alphas[i] << 24 | rgb & 16777215;
         border(context, x - inset, y - inset, w + inset * 2, h + inset * 2, color);
      }
   }

   private PadImage image() {
      String name = this.backgroundImage.get() == null ? "" : this.backgroundImage.get().trim();
      if (PadImage.isUrl(name)) {
         if (!name.equals(this.loadedImageFor)) {
            this.loadedImageFor = name;
            this.loadedImage = null;
            this.imageGeneration++;
         }

         if (this.loadedImage == null) {
            this.loadedImage = PadImage.fromUrl(name, "custom_hotbar_" + this.imageGeneration);
         }

         return this.loadedImage;
      } else if (name.equals(this.loadedImageFor)) {
         return this.loadedImage;
      } else {
         this.loadedImageFor = name;
         this.loadedImage = null;
         if (!name.isEmpty()) {
            try {
               Path path = FabricLoader.getInstance().getConfigDir().resolve(name);
               this.loadedImage = PadImage.load(path, "custom_hotbar_" + this.imageGeneration++);
            } catch (Exception var3) {
               System.out.println("[Pip Client] \"" + name + "\" is not a usable file name (" + var3 + ")");
            }
         }

         return this.loadedImage;
      }
   }
}
