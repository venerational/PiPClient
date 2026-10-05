package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.HudModule;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TotemCounterModule extends HudModule {
   private static final int ROW_HEIGHT = 18;
   private static final int PADDING_X = 8;
   private static final int ICON_GAP = 5;
   private static final int COUNT_GAP = 2;
   private final ColorSetting borderColor = this.register(new ColorSetting("Border Color", -49766));
   private final ColorSetting backgroundColor = this.register(new ColorSetting("Background Color", -15328736));
   private final SliderSetting backgroundOpacity = this.register(new SliderSetting("Background Opacity", 75.0, 0.0, 100.0, 1.0, "%"));
   private final ColorSetting nameColor = this.register(new ColorSetting("Name Color", -1));
   private final ColorSetting panelIconColor = this.register(new ColorSetting("Panel Icon Color", -41892));
   private final EnumSetting<TotemPopTracker.PopIcon> panelIcon = this.register(new EnumSetting<>("Panel Icon", TotemPopTracker.PopIcon.HEART));
   private final BooleanSetting customSize = this.register(new BooleanSetting("Custom Size", false));
   private final SliderSetting boxWidth = this.register(new SliderSetting("Box Width", 120.0, 40.0, 400.0, 1.0, "px"));
   private final SliderSetting boxHeight = this.register(new SliderSetting("Box Height", 36.0, 10.0, 200.0, 1.0, "px"));
   private UUID targetId;
   private String targetName = "";

   public TotemCounterModule() {
      super("Totem Counter", "Shows how many times you and your current target have popped a totem.", false, 4.0, 130.0);
   }

   @Override
   public void init() {
      super.init();
      AttackEntityCallback.EVENT.register((AttackEntityCallback)(player, world, hand, entity, hitResult) -> {
         if (world.isClientSide() && entity instanceof Player target && target != player) {
            this.targetId = target.getUUID();
            this.targetName = target.getName().getString();
         }

         return InteractionResult.PASS;
      });
   }

   @Override
   public void render(GuiGraphics context) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         List<String> names = new ArrayList<>();
         List<Integer> counts = new ArrayList<>();
         names.add(mc.player.getName().getString());
         counts.add(TotemPopTracker.getPopCount(mc.player.getUUID()));
         if (this.isTargetLoaded(mc)) {
            names.add(this.targetName);
            counts.add(TotemPopTracker.getPopCount(this.targetId));
         }

         int[] size = this.customSize.get() ? new int[]{this.boxWidth.getInt(), this.boxHeight.getInt()} : this.computeBoxSize(mc.font, names, counts);
         int alpha = (int)Math.round(this.backgroundOpacity.get() / 100.0 * 255.0);
         int fillColor = alpha << 24 | this.backgroundColor.get() & 16777215;
         drawSquareBorder(context, 0, 0, size[0], size[1], this.borderColor.get(), fillColor);

         for (int i = 0; i < names.size(); i++) {
            this.drawRowText(context, mc.font, 8, i * 18 + 5, names.get(i), counts.get(i));
         }
      }
   }

   private void drawRowText(GuiGraphics context, Font textRenderer, int x, int y, String name, int count) {
      context.drawString(textRenderer, name, x, y, this.nameColor.get());
      x += textRenderer.width(name) + 5;
      String symbol = this.panelIcon.get().symbol;
      context.drawString(textRenderer, symbol, x, y, this.panelIconColor.get());
      x += textRenderer.width(symbol) + 2;
      context.drawString(textRenderer, String.valueOf(count), x, y, this.nameColor.get());
   }

   private int[] computeBoxSize(Font textRenderer, List<String> names, List<Integer> counts) {
      int maxWidth = 0;
      String symbol = this.panelIcon.get().symbol;

      for (int i = 0; i < names.size(); i++) {
         int w = textRenderer.width(names.get(i)) + 5 + textRenderer.width(symbol) + 2 + textRenderer.width(String.valueOf(counts.get(i)));
         maxWidth = Math.max(maxWidth, w);
      }

      return new int[]{maxWidth + 16, names.size() * 18};
   }

   @Override
   protected int contentWidth() {
      if (this.customSize.get()) {
         return this.boxWidth.getInt();
      } else {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player == null) {
            return 90;
         } else {
            List<String> names = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            names.add(mc.player.getName().getString());
            counts.add(TotemPopTracker.getPopCount(mc.player.getUUID()));
            if (this.isTargetLoaded(mc)) {
               names.add(this.targetName);
               counts.add(TotemPopTracker.getPopCount(this.targetId));
            }

            return this.computeBoxSize(mc.font, names, counts)[0];
         }
      }
   }

   @Override
   protected int contentHeight() {
      if (this.customSize.get()) {
         return this.boxHeight.getInt();
      } else {
         return this.isTargetLoaded(Minecraft.getInstance()) ? 36 : 18;
      }
   }

   private boolean isTargetLoaded(Minecraft mc) {
      if (this.targetId != null && mc.level != null) {
         Player target = mc.level.getPlayerByUUID(this.targetId);
         if (target == null) {
            return false;
         } else {
            Vec3 cameraPos = mc.gameRenderer.getMainCamera().position();
            return target.shouldRender(cameraPos.x, cameraPos.y, cameraPos.z);
         }
      } else {
         return false;
      }
   }

   private static void drawSquareBorder(GuiGraphics context, int x, int y, int w, int h, int borderColor, int fillColor) {
      context.fill(x + 1, y + 1, x + w - 1, y + h - 1, fillColor);
      context.fill(x, y, x + w, y + 1, borderColor);
      context.fill(x, y + h - 1, x + w, y + h, borderColor);
      context.fill(x, y + 1, x + 1, y + h - 1, borderColor);
      context.fill(x + w - 1, y + 1, x + w, y + h - 1, borderColor);
   }
}
