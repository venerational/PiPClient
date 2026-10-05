package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;

public class GreyscaleModule extends Module {
   private static final String BASE_PACK_FRAGMENT = "greyscale_blocks";
   private static final String PRIMARY_PACK_FRAGMENT = "greyscale_primary";
   private static final String KEEP_FIRE_FRAGMENT = "greyscale_keep_fire";
   private static final String KEEP_GLOWSTONE_FRAGMENT = "greyscale_keep_glowstone";
   private static final String KEEP_OBSIDIAN_FRAGMENT = "greyscale_keep_obsidian";
   private static final String KEEP_ANCHOR_FRAGMENT = "greyscale_keep_anchor";
   private static final String KEEP_LAVA_FRAGMENT = "greyscale_keep_lava";
   private static final float PRIMARY_SAT_MIN = 0.35F;
   private static final float PRIMARY_HUE_TOL = 35.0F;
   private static final float[] PRIMARY_HUES = new float[]{0.0F, 120.0F, 240.0F};
   private final EnumSetting<GreyscaleModule.ColorMode> colorMode = this.register(new EnumSetting<>("Color Mode", GreyscaleModule.ColorMode.ALL_GREY));
   private final BooleanSetting desaturateFire = this.register(new BooleanSetting("Desaturate Fire", true));
   private final BooleanSetting desaturateGlowstone = this.register(new BooleanSetting("Desaturate Glowstone", true));
   private final BooleanSetting desaturateObsidian = this.register(new BooleanSetting("Desaturate Obsidian", true));
   private final BooleanSetting desaturateAnchor = this.register(new BooleanSetting("Desaturate Respawn Anchor", true));
   private final BooleanSetting desaturateLava = this.register(new BooleanSetting("Desaturate Lava", true));
   private GreyscaleModule.DesiredState lastApplied;

   public GreyscaleModule() {
      super(
         "Grayscale",
         "Desaturates block textures only - entities, the sky, and inventory items keep their real color. Color Mode picks between all-gray and keeping only red/green/blue. Individual toggles let fire/glowstone/obsidian/respawn anchors/lava keep their color too.",
         Category.RENDER,
         false
      );
      this.colorMode.group = "General";
   }

   public GreyscaleModule.ColorMode colorMode() {
      return this.colorMode.get();
   }

   @Override
   public void tick() {
      boolean on = this.isEnabled();
      boolean primary = on && this.colorMode.get() == GreyscaleModule.ColorMode.PRIMARY_COLORS;
      GreyscaleModule.DesiredState desired = new GreyscaleModule.DesiredState(
         on && !primary,
         primary,
         on && !this.desaturateFire.get(),
         on && !this.desaturateGlowstone.get(),
         on && !this.desaturateObsidian.get(),
         on && !this.desaturateAnchor.get(),
         on && !this.desaturateLava.get()
      );
      if (!desired.equals(this.lastApplied)) {
         Minecraft mc = Minecraft.getInstance();
         PackRepository manager = mc.getResourcePackRepository();
         String baseId = findPackId(manager, "greyscale_blocks");
         String primaryId = findPackId(manager, "greyscale_primary");
         String fireId = findPackId(manager, "greyscale_keep_fire");
         String glowstoneId = findPackId(manager, "greyscale_keep_glowstone");
         String obsidianId = findPackId(manager, "greyscale_keep_obsidian");
         String anchorId = findPackId(manager, "greyscale_keep_anchor");
         String lavaId = findPackId(manager, "greyscale_keep_lava");
         if (baseId != null) {
            applyPack(manager, baseId, desired.baseOn());
            applyPack(manager, primaryId, desired.primaryOn());
            applyPack(manager, fireId, desired.keepFire());
            applyPack(manager, glowstoneId, desired.keepGlowstone());
            applyPack(manager, obsidianId, desired.keepObsidian());
            applyPack(manager, anchorId, desired.keepAnchor());
            applyPack(manager, lavaId, desired.keepLava());
            mc.reloadResourcePacks();
            this.lastApplied = desired;
         }
      }
   }

   public static int applyPalette(int argb) {
      if (ModuleManager.get("Grayscale") instanceof GreyscaleModule greyscale && greyscale.isEnabled()) {
         return greyscale.colorMode() == GreyscaleModule.ColorMode.PRIMARY_COLORS && isPrimary(argb) ? argb : toGrey(argb);
      } else {
         return argb;
      }
   }

   public static boolean isPrimary(int argb) {
      int r = argb >> 16 & 0xFF;
      int g = argb >> 8 & 0xFF;
      int b = argb & 0xFF;
      float[] hsv = rgbToHsv(r, g, b);
      if (hsv[1] < 0.35F) {
         return false;
      } else {
         for (float prim : PRIMARY_HUES) {
            if (Math.abs((hsv[0] - prim + 540.0F) % 360.0F - 180.0F) <= 35.0F) {
               return true;
            }
         }

         return false;
      }
   }

   public static int toGrey(int argb) {
      int a = argb >> 24 & 0xFF;
      int r = argb >> 16 & 0xFF;
      int g = argb >> 8 & 0xFF;
      int b = argb & 0xFF;
      int lum = Math.round(0.299F * r + 0.587F * g + 0.114F * b);
      return a << 24 | lum << 16 | lum << 8 | lum;
   }

   private static float[] rgbToHsv(int r, int g, int b) {
      float rf = r / 255.0F;
      float gf = g / 255.0F;
      float bf = b / 255.0F;
      float max = Math.max(rf, Math.max(gf, bf));
      float min = Math.min(rf, Math.min(gf, bf));
      float delta = max - min;
      float h;
      if (delta == 0.0F) {
         h = 0.0F;
      } else if (max == rf) {
         h = 60.0F * ((gf - bf) / delta % 6.0F);
      } else if (max == gf) {
         h = 60.0F * ((bf - rf) / delta + 2.0F);
      } else {
         h = 60.0F * ((rf - gf) / delta + 4.0F);
      }

      if (h < 0.0F) {
         h += 360.0F;
      }

      return new float[]{h, max == 0.0F ? 0.0F : delta / max, max};
   }

   private static void applyPack(PackRepository manager, String id, boolean enabled) {
      if (id != null) {
         if (enabled) {
            manager.addPack(id);
         } else {
            manager.removePack(id);
         }
      }
   }

   private static String findPackId(PackRepository manager, String fragment) {
      return manager.getAvailableIds().stream().filter(id -> id.endsWith(fragment)).findFirst().orElse(null);
   }

   public static enum ColorMode {
      ALL_GREY,
      PRIMARY_COLORS;
   }

   private record DesiredState(
      boolean baseOn, boolean primaryOn, boolean keepFire, boolean keepGlowstone, boolean keepObsidian, boolean keepAnchor, boolean keepLava
   ) {
   }
}
