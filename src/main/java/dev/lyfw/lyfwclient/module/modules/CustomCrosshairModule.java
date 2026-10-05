package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.gui.ModuleSettingsScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.Arrays;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.GameType;

public class CustomCrosshairModule extends Module {
   public static final int CUSTOM_GRID_SIZE = 15;
   private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_full");
   private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_background");
   private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("hud/crosshair_attack_indicator_progress");
   private final EnumSetting<CustomCrosshairModule.CrosshairShape> shape = this.register(new EnumSetting<>("Shape", CustomCrosshairModule.CrosshairShape.CROSS));
   private final TextSetting customPixels = this.register(new TextSetting("Custom Pixels", defaultGridPattern()));
   private final BooleanSetting useCustomColor = this.register(new BooleanSetting("Use Custom Color", false));
   private final ColorSetting customColor = this.register(new ColorSetting("Custom Color", -1));
   private final SliderSetting width = this.register(new SliderSetting("Width", 6.0, 0.0, 40.0, 1.0, "px"));
   private final SliderSetting height = this.register(new SliderSetting("Height", 6.0, 0.0, 40.0, 1.0, "px"));
   private final SliderSetting gap = this.register(new SliderSetting("Gap", 2.0, 0.0, 30.0, 1.0, "px"));
   private final SliderSetting thickness = this.register(new SliderSetting("Thickness", 2.0, 1.0, 10.0, 1.0, "px"));
   private final SliderSetting rotation = this.register(new SliderSetting("Rotation", 0.0, 0.0, 360.0, 1.0, "°"));
   private final SliderSetting scale = this.register(new SliderSetting("Scale", 1.0, 0.1, 5.0, 0.1, "x"));
   private final SliderSetting offsetX = this.register(new SliderSetting("Offset X", 0.0, -200.0, 200.0, 1.0, "px"));
   private final SliderSetting offsetY = this.register(new SliderSetting("Offset Y", 0.0, -200.0, 200.0, 1.0, "px"));
   private final BooleanSetting visibleByDefault = this.register(new BooleanSetting("Visible By Default", true));
   private final BooleanSetting visibleWhenGuiHidden = this.register(new BooleanSetting("Visible When GUI Hidden", false));
   private final BooleanSetting visibleInDebugHud = this.register(new BooleanSetting("Visible In Debug HUD", true));
   private final BooleanSetting visibleInThirdPerson = this.register(new BooleanSetting("Visible In Third Person", true));
   private final BooleanSetting visibleInSpectator = this.register(new BooleanSetting("Visible In Spectator", false));
   private final BooleanSetting visibleWhenHoldingRanged = this.register(new BooleanSetting("Visible When Holding Ranged Weapon", true));
   private final BooleanSetting visibleWhenHoldingThrowable = this.register(new BooleanSetting("Visible When Holding Throwable", true));
   private final BooleanSetting visibleWhenUsingSpyglass = this.register(new BooleanSetting("Visible When Using Spyglass", false));
   private final BooleanSetting dynamicOnAttack = this.register(new BooleanSetting("Dynamic On Attack", false));
   private final BooleanSetting dynamicOnPull = this.register(new BooleanSetting("Dynamic On Pull Progress", false));
   private final BooleanSetting attackIndicatorEnabled = this.register(new BooleanSetting("Attack Indicator", false));
   private final BooleanSetting highlightHostile = this.register(new BooleanSetting("Highlight Hostile Mobs", false));
   private final ColorSetting highlightHostileColor = this.register(new ColorSetting("Hostile Highlight Color", -49088));
   private final BooleanSetting highlightPassive = this.register(new BooleanSetting("Highlight Passive Mobs", false));
   private final ColorSetting highlightPassiveColor = this.register(new ColorSetting("Passive Highlight Color", -12517568));
   private final BooleanSetting highlightPlayer = this.register(new BooleanSetting("Highlight Players", false));
   private final ColorSetting highlightPlayerColor = this.register(new ColorSetting("Player Highlight Color", -12224));

   public CustomCrosshairModule() {
      super(
         "Custom Crosshair",
         "Replaces vanilla's crosshair with a fully customizable one - shape, color, size, visibility rules, dynamic sizing and target highlighting.",
         Category.RENDER,
         false
      );
      this.useCustomColor.group = "Color";
      this.customColor.group = "Color";
      this.width.group = "Size";
      this.height.group = "Size";
      this.gap.group = "Size";
      this.thickness.group = "Size";
      this.rotation.group = "Size";
      this.scale.group = "Size";
      this.offsetX.group = "Size";
      this.offsetY.group = "Size";
      this.visibleByDefault.group = "Visibility";
      this.visibleWhenGuiHidden.group = "Visibility";
      this.visibleInDebugHud.group = "Visibility";
      this.visibleInThirdPerson.group = "Visibility";
      this.visibleInSpectator.group = "Visibility";
      this.visibleWhenHoldingRanged.group = "Visibility";
      this.visibleWhenHoldingThrowable.group = "Visibility";
      this.visibleWhenUsingSpyglass.group = "Visibility";
      this.dynamicOnAttack.group = "Dynamic";
      this.dynamicOnPull.group = "Dynamic";
      this.attackIndicatorEnabled.group = "Attack Indicator";
      this.highlightHostile.group = "Highlight";
      this.highlightHostileColor.group = "Highlight";
      this.highlightPassive.group = "Highlight";
      this.highlightPassiveColor.group = "Highlight";
      this.highlightPlayer.group = "Highlight";
      this.highlightPlayerColor.group = "Highlight";
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return new ModuleSettingsScreen(parent, this);
   }

   public EnumSetting<CustomCrosshairModule.CrosshairShape> shapeSetting() {
      return this.shape;
   }

   public TextSetting customPixelsSetting() {
      return this.customPixels;
   }

   public static String blankGrid() {
      return "0".repeat(225);
   }

   private static String defaultGridPattern() {
      int n = 15;
      int center = n / 2;
      char[] cells = new char[n * n];
      Arrays.fill(cells, '0');

      for (int i = 0; i < n; i++) {
         if (Math.abs(i - center) >= 2) {
            cells[center * n + i] = '1';
            cells[i * n + center] = '1';
         }
      }

      return new String(cells);
   }

   public void render(GuiGraphics context, DeltaTracker tickCounter) {
      Minecraft mc = Minecraft.getInstance();
      if (this.shouldRender(mc)) {
         int centerX = context.guiWidth() / 2 + this.offsetX.getInt();
         int centerY = context.guiHeight() / 2 + this.offsetY.getInt();
         float dynamicGap = (float)this.gap.get().doubleValue() + this.dynamicGapAdjustment(mc);
         this.drawCrosshairAt(context, centerX, centerY, dynamicGap, this.resolveColor(mc));
         this.drawAttackIndicator(context, centerX, centerY, mc);
      }
   }

   public void renderPreview(GuiGraphics context, int centerX, int centerY) {
      this.drawCrosshairAt(context, centerX, centerY, (float)this.gap.get().doubleValue(), this.resolveColor(Minecraft.getInstance()));
      if (this.attackIndicatorEnabled.get()) {
         int x = centerX - 8;
         int y = centerY + 9;
         context.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x, y, 16, 4);
         int progressWidth = 9;
         context.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, progressWidth, 4);
      }
   }

   private void drawAttackIndicator(GuiGraphics context, int centerX, int centerY, Minecraft mc) {
      if (this.attackIndicatorEnabled.get() && mc.player != null) {
         float cooldown = mc.player.getAttackStrengthScale(0.0F);
         boolean full = false;
         if (mc.crosshairPickEntity instanceof LivingEntity livingTarget && cooldown >= 1.0F) {
            full = mc.player.getCurrentItemAttackStrengthDelay() > 5.0F && livingTarget.isAlive();
            AttackRange attackRange = (AttackRange)mc.player.getActiveItem().get(DataComponents.ATTACK_RANGE);
            full &= attackRange == null || mc.hitResult != null && attackRange.isInRange(mc.player, mc.hitResult.getLocation());
         }

         int x = centerX - 8;
         int y = centerY + 9;
         if (full) {
            context.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, x, y, 16, 16);
         } else if (cooldown < 1.0F) {
            int progressWidth = (int)(cooldown * 17.0F);
            context.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x, y, 16, 4);
            context.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, progressWidth, 4);
         }
      }
   }

   private void drawCrosshairAt(GuiGraphics context, int centerX, int centerY, float gapPx, int color) {
      context.pose().pushMatrix();
      context.pose().translate(centerX, centerY);
      context.pose().scale((float)this.scale.get().doubleValue());
      context.pose().rotate((float)Math.toRadians(this.rotation.get()));
      this.drawShape(
         context, gapPx, (float)this.thickness.get().doubleValue(), (float)this.width.get().doubleValue(), (float)this.height.get().doubleValue(), color
      );
      context.pose().popMatrix();
   }

   private boolean shouldRender(Minecraft mc) {
      if (this.isEnabled() && mc.player != null && mc.screen == null) {
         boolean hidden = mc.options.hideGui;
         boolean thirdPerson = !mc.options.getCameraType().isFirstPerson();
         boolean spectator = mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR;
         boolean holdingRanged = isRangedWeapon(mc.player.getMainHandItem()) || isRangedWeapon(mc.player.getOffhandItem());
         boolean holdingThrowable = isThrowable(mc.player.getMainHandItem()) || isThrowable(mc.player.getOffhandItem());
         boolean usingSpyglass = mc.player.isUsingItem() && mc.player.getUseItem().is(Items.SPYGLASS);
         boolean debugOpen = !mc.debugEntries.getCurrentlyEnabled().isEmpty();
         boolean visible;
         if (!hidden && !thirdPerson && !spectator && !holdingRanged && !holdingThrowable && !usingSpyglass) {
            visible = this.visibleByDefault.get();
         } else {
            visible = true;
            if (hidden) {
               visible &= this.visibleWhenGuiHidden.get();
            }

            if (thirdPerson) {
               visible &= this.visibleInThirdPerson.get();
            }

            if (spectator) {
               visible &= this.visibleInSpectator.get();
            }

            if (holdingRanged) {
               visible &= this.visibleWhenHoldingRanged.get();
            }

            if (holdingThrowable) {
               visible &= this.visibleWhenHoldingThrowable.get();
            }

            if (usingSpyglass) {
               visible &= this.visibleWhenUsingSpyglass.get();
            }
         }

         if (debugOpen) {
            visible &= this.visibleInDebugHud.get();
         }

         return visible;
      } else {
         return false;
      }
   }

   private static boolean isRangedWeapon(ItemStack stack) {
      return stack.getItem() instanceof ProjectileWeaponItem;
   }

   private static boolean isThrowable(ItemStack stack) {
      return stack.getItem() instanceof SnowballItem
         || stack.getItem() instanceof EggItem
         || stack.getItem() instanceof EnderpearlItem
         || stack.getItem() instanceof ExperienceBottleItem
         || stack.getItem() instanceof ThrowablePotionItem;
   }

   private float dynamicGapAdjustment(Minecraft mc) {
      float adjustment = 0.0F;
      if (this.dynamicOnAttack.get()) {
         float cooldown = mc.player.getAttackStrengthScale(0.0F);
         adjustment += (1.0F - cooldown) * 6.0F;
      }

      if (this.dynamicOnPull.get() && mc.player.isUsingItem()) {
         ItemStack active = mc.player.getUseItem();
         if (active.getItem() instanceof ProjectileWeaponItem) {
            int maxUseTime = active.getUseDuration(mc.player);
            if (maxUseTime > 0) {
               float pull = Math.min(1.0F, (float)mc.player.getTicksUsingItem() / maxUseTime);
               adjustment -= pull * 4.0F;
            }
         }
      }

      return adjustment;
   }

   private int resolveColor(Minecraft mc) {
      Entity target = mc.crosshairPickEntity;
      if (target != null) {
         if (this.highlightPlayer.get() && target instanceof Player) {
            return this.highlightPlayerColor.get();
         }

         if (this.highlightHostile.get() && target instanceof Enemy) {
            return this.highlightHostileColor.get();
         }

         if (this.highlightPassive.get() && target instanceof AgeableMob) {
            return this.highlightPassiveColor.get();
         }
      }

      return this.useCustomColor.get() ? this.customColor.get() : -1;
   }

   private void drawShape(GuiGraphics context, float gapPx, float thicknessPx, float widthPx, float heightPx, int color) {
      if (this.shape.get() == CustomCrosshairModule.CrosshairShape.VANILLA) {
         this.drawCross(context, 2, 1, 6, 6, color);
      } else if (this.shape.get() == CustomCrosshairModule.CrosshairShape.CUSTOM) {
         this.drawCustomGrid(context, color);
      } else {
         int t = Math.round(thicknessPx);
         switch ((CustomCrosshairModule.CrosshairShape)this.shape.get()) {
            case VANILLA:
            case CUSTOM:
            default:
               break;
            case CROSS:
               this.drawCross(context, t, Math.round(gapPx), Math.round(widthPx), Math.round(heightPx), color);
               break;
            case ARROW:
               this.drawArrowArms(context, Math.round(gapPx), t, Math.round(widthPx), Math.round(heightPx), color);
               break;
            case DOT:
               int w = Math.max(t, Math.round(widthPx));
               int h = Math.max(t, Math.round(heightPx));
               context.fill(-w / 2, -h / 2, w - w / 2, h - h / 2, color);
         }
      }
   }

   private void drawCustomGrid(GuiGraphics context, int color) {
      String bits = this.customPixels.get();
      int n = 15;
      if (bits.length() == n * n) {
         int half = n / 2;

         for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
               if (bits.charAt(y * n + x) == '1') {
                  int lx = x - half;
                  int ly = y - half;
                  context.fill(lx, ly, lx + 1, ly + 1, color);
               }
            }
         }
      }
   }

   private void drawCross(GuiGraphics context, int t, int g, int w, int h, int color) {
      int halfT = t / 2;
      context.fill(-halfT, -g - h, t - halfT, -g, color);
      context.fill(-halfT, g, t - halfT, g + h, color);
      context.fill(-g - w, -halfT, -g, t - halfT, color);
      context.fill(g, -halfT, g + w, t - halfT, color);
   }

   private void drawArrowArms(GuiGraphics context, int gapPx, int thicknessPx, int lengthW, int lengthH, int color) {
      int rows = 3;
      int rowH = Math.max(1, lengthH / rows);
      int rowW = Math.max(1, lengthW / rows);

      for (int i = 0; i < rows; i++) {
         int rowThickness = Math.max(1, thicknessPx * (rows - i));
         int halfRowThickness = rowThickness / 2;
         int yTop = -gapPx - lengthH + i * rowH;
         context.fill(-halfRowThickness, yTop, rowThickness - halfRowThickness, yTop + rowH, color);
         int yBottom = gapPx + lengthH - (i + 1) * rowH;
         context.fill(-halfRowThickness, yBottom, rowThickness - halfRowThickness, yBottom + rowH, color);
         int xLeft = -gapPx - lengthW + i * rowW;
         context.fill(xLeft, -halfRowThickness, xLeft + rowW, rowThickness - halfRowThickness, color);
         int xRight = gapPx + lengthW - (i + 1) * rowW;
         context.fill(xRight, -halfRowThickness, xRight + rowW, rowThickness - halfRowThickness, color);
      }
   }

   public static enum CrosshairShape {
      VANILLA,
      CROSS,
      ARROW,
      DOT,
      CUSTOM;
   }
}
