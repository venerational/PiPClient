package dev.lyfw.lyfwclient;

import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.lyfw.lyfwclient.accounts.PipAccounts;
import dev.lyfw.lyfwclient.crash.CrashSummary;
import dev.lyfw.lyfwclient.crash.CrashWatcher;
import dev.lyfw.lyfwclient.gui.CrashNoticeScreen;
import dev.lyfw.lyfwclient.gui.EmoteWheelScreen;
import dev.lyfw.lyfwclient.gui.HudEditorScreen;
import dev.lyfw.lyfwclient.gui.Theme;
import dev.lyfw.lyfwclient.module.GlintTintReloadListener;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.Modules;
import dev.lyfw.lyfwclient.module.modules.BlockColorsModule;
import dev.lyfw.lyfwclient.module.modules.FireColorModule;
import dev.lyfw.lyfwclient.module.modules.KillEffectsModule;
import dev.lyfw.lyfwclient.module.modules.ToggleBrightnessModule;
import dev.lyfw.lyfwclient.module.modules.TotemPopTracker;
import dev.lyfw.lyfwclient.render.EmotePlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LyfwClient implements ClientModInitializer {
   public static KeyMapping clickGuiKey;
   public static KeyMapping hudEditorKey;
   public static KeyMapping toggleBrightnessKey;
   public static KeyMapping resetPopCounterKey;
   public static KeyMapping previewKillEffectKey;
   public static KeyMapping emoteKey;
   public static KeyMapping emoteWheelKey;
   private static final Category CATEGORY = Category.register(Identifier.fromNamespaceAndPath("lyfw-client", "keys"));
   private static boolean crashNoticeChecked;

   private static void registerPacks(String... names) {
      ModContainer container = (ModContainer)FabricLoader.getInstance().getModContainer("lyfw-client").orElseThrow();

      for (String name : names) {
         ResourceLoader.registerBuiltinPack(Identifier.fromNamespaceAndPath("lyfw-client", name), container, PackActivationType.NORMAL);
      }
   }

   public void onInitializeClient() {
      clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.clickgui", Type.KEYSYM, 344, CATEGORY));
      hudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.hudeditor", Type.KEYSYM, 72, CATEGORY));
      toggleBrightnessKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.togglebrightness", Type.KEYSYM, 71, CATEGORY));
      resetPopCounterKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.resetpopcounter", Type.KEYSYM, -1, CATEGORY));
      previewKillEffectKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.previewkilleffect", Type.KEYSYM, -1, CATEGORY));
      emoteKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.emote", Type.KEYSYM, 66, CATEGORY));
      emoteWheelKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.lyfwclient.emotewheel", Type.KEYSYM, 86, CATEGORY));
      Modules.registerAll();
      Config.load();
      ModuleManager.initAll();
      PipAccounts.init();
      registerPacks(
         "cobweb_translucent",
         "cobweb_hitbox",
         "cobweb_hitbox_opaque",
         "greyscale_blocks",
         "greyscale_primary",
         "greyscale_keep_fire",
         "greyscale_keep_glowstone",
         "greyscale_keep_obsidian",
         "greyscale_keep_anchor",
         "greyscale_keep_lava",
         "side_shield_overlay",
         "old_glint",
         "old_lighting",
         "fire_color",
         "block_colors"
      );

      for (String blockPrefix : new String[]{"anchor", "obsidian", "glowstone", "totem"}) {
         for (int tier : new int[]{20, 40, 60, 80}) {
            registerPacks(blockPrefix + "_opacity_" + tier);
         }
      }

      ColorProviderRegistry.BLOCK.register((BlockColor)(state, world, pos, tintIndex) -> {
         FireColorModule fire = FireColorModule.get();
         return fire != null && fire.isEnabled() ? fire.rgb() : -1;
      }, new Block[]{Blocks.FIRE});
      ColorProviderRegistry.BLOCK.register((BlockColor)(state, world, pos, tintIndex) -> {
         BlockColorsModule colors = BlockColorsModule.get();
         int color = colors == null ? -1 : colors.colorFor(state.getBlock());
         return colors == null ? color : colors.greyTint(state.getBlock(), color);
      }, BlockColorsModule.targets());
      ColorProviderRegistry.BLOCK.register((BlockColor)(state, world, pos, tintIndex) -> {
         BlockColorsModule colors = BlockColorsModule.get();
         int chosen = colors == null ? -1 : colors.grass();
         if (chosen != -1) {
            return colors.greyTint(state.getBlock(), chosen);
         } else {
            int biome = world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor();
            return colors == null ? biome : colors.greyTint(state.getBlock(), biome);
         }
      }, new Block[]{Blocks.GRASS_BLOCK, Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN});
      ColorProviderRegistry.BLOCK.register((BlockColor)(state, world, pos, tintIndex) -> {
         BlockColorsModule colors = BlockColorsModule.get();
         int chosen = colors == null ? -1 : colors.foliage();
         if (chosen != -1) {
            return colors.greyTint(state.getBlock(), chosen);
         } else {
            int biome = world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : -12012264;
            return colors == null ? biome : colors.greyTint(state.getBlock(), biome);
         }
      }, new Block[]{Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES});
      ColorProviderRegistry.BLOCK.register((BlockColor)(state, world, pos, tintIndex) -> {
         BlockColorsModule colors = BlockColorsModule.get();
         int chosen = colors == null ? -1 : colors.water();
         if (chosen != -1) {
            return colors.greyTint(state.getBlock(), chosen);
         } else {
            int biome = world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) : -1;
            return colors == null ? biome : colors.greyTint(state.getBlock(), biome);
         }
      }, new Block[]{Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON});
      ResourceLoader.get(PackType.CLIENT_RESOURCES)
         .registerReloader(Identifier.fromNamespaceAndPath("lyfw-client", "glint_tint_reapply"), new GlintTintReloadListener());
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         if (!crashNoticeChecked && client.level == null && client.getOverlay() == null && client.screen != null) {
            crashNoticeChecked = true;
            CrashSummary notice = CrashWatcher.takePendingNotice();
            if (notice != null) {
               client.setScreen(new CrashNoticeScreen(client.screen, notice));
            }
         }

         while (clickGuiKey.consumeClick()) {
            if (client.screen == null) {
               client.setScreen(Theme.layout().create());
            }
         }

         while (hudEditorKey.consumeClick()) {
            if (client.screen == null) {
               client.setScreen(new HudEditorScreen());
            }
         }

         while (toggleBrightnessKey.consumeClick()) {
            if (client.screen == null && ModuleManager.get("Toggle Brightness") instanceof ToggleBrightnessModule brightness) {
               brightness.setEnabled(!brightness.isEnabled());
            }
         }

         while (resetPopCounterKey.consumeClick()) {
            if (client.screen == null) {
               TotemPopTracker.resetCounts();
               if (client.player != null) {
                  client.player.displayClientMessage(Component.literal("Pop Counter reset."), true);
               }
            }
         }

         while (previewKillEffectKey.consumeClick()) {
            if (client.screen == null && client.player != null && ModuleManager.get("Kill Effects") instanceof KillEffectsModule killEffects) {
               killEffects.preview(client.player.position());
            }
         }

         while (emoteWheelKey.consumeClick()) {
            if (client.screen == null) {
               client.setScreen(new EmoteWheelScreen());
            }
         }

         while (emoteKey.consumeClick()) {
            if (client.screen == null) {
               EmotePlay.toggle(client);
            }
         }

         EmotePlay.tick(client);
         TotemPopTracker.tickDeathResets(client);
         ModuleManager.tick();
      });
   }
}
