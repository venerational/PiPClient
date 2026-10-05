package dev.lyfw.lyfwclient.module;

import dev.lyfw.lyfwclient.module.modules.AnchorCustomizerModule;
import dev.lyfw.lyfwclient.module.modules.ArmorHudModule;
import dev.lyfw.lyfwclient.module.modules.AttackCooldownDesyncModule;
import dev.lyfw.lyfwclient.module.modules.BlockColorsModule;
import dev.lyfw.lyfwclient.module.modules.BlockOpacityModule;
import dev.lyfw.lyfwclient.module.modules.BlockOutlineModule;
import dev.lyfw.lyfwclient.module.modules.BrightnessModule;
import dev.lyfw.lyfwclient.module.modules.CleanViewModule;
import dev.lyfw.lyfwclient.module.modules.CobwebModule;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import dev.lyfw.lyfwclient.module.modules.CpsModule;
import dev.lyfw.lyfwclient.module.modules.CrystalCustomizerModule;
import dev.lyfw.lyfwclient.module.modules.CustomCrosshairModule;
import dev.lyfw.lyfwclient.module.modules.CustomFogModule;
import dev.lyfw.lyfwclient.module.modules.CustomFontModule;
import dev.lyfw.lyfwclient.module.modules.CustomHotbarModule;
import dev.lyfw.lyfwclient.module.modules.DamageTintModule;
import dev.lyfw.lyfwclient.module.modules.DeathAnimationModule;
import dev.lyfw.lyfwclient.module.modules.EffectTimersModule;
import dev.lyfw.lyfwclient.module.modules.ExactHealthModule;
import dev.lyfw.lyfwclient.module.modules.FireColorModule;
import dev.lyfw.lyfwclient.module.modules.FoodOverlayModule;
import dev.lyfw.lyfwclient.module.modules.FpsModule;
import dev.lyfw.lyfwclient.module.modules.GlobalColorModule;
import dev.lyfw.lyfwclient.module.modules.GreyscaleModule;
import dev.lyfw.lyfwclient.module.modules.GuiThemeModule;
import dev.lyfw.lyfwclient.module.modules.HealthRingModule;
import dev.lyfw.lyfwclient.module.modules.HideActionBarModule;
import dev.lyfw.lyfwclient.module.modules.HideArmorModule;
import dev.lyfw.lyfwclient.module.modules.HidePlayerModelModule;
import dev.lyfw.lyfwclient.module.modules.HitboxModule;
import dev.lyfw.lyfwclient.module.modules.HotbarModule;
import dev.lyfw.lyfwclient.module.modules.HurtCamModule;
import dev.lyfw.lyfwclient.module.modules.InventoryScaleModule;
import dev.lyfw.lyfwclient.module.modules.ItemCounterModule;
import dev.lyfw.lyfwclient.module.modules.ItemGlintModule;
import dev.lyfw.lyfwclient.module.modules.KeystrokesModule;
import dev.lyfw.lyfwclient.module.modules.KeystrokesPadModule;
import dev.lyfw.lyfwclient.module.modules.KillEffectsModule;
import dev.lyfw.lyfwclient.module.modules.MainMenuModule;
import dev.lyfw.lyfwclient.module.modules.MotionBlurModule;
import dev.lyfw.lyfwclient.module.modules.MouseTrackerModule;
import dev.lyfw.lyfwclient.module.modules.NameProtectModule;
import dev.lyfw.lyfwclient.module.modules.NametagPingModule;
import dev.lyfw.lyfwclient.module.modules.NametagsModule;
import dev.lyfw.lyfwclient.module.modules.NoHandSwayModule;
import dev.lyfw.lyfwclient.module.modules.NostalgiaModule;
import dev.lyfw.lyfwclient.module.modules.OptimizersModule;
import dev.lyfw.lyfwclient.module.modules.PingModule;
import dev.lyfw.lyfwclient.module.modules.PipPresenceModule;
import dev.lyfw.lyfwclient.module.modules.PlayerGlowModule;
import dev.lyfw.lyfwclient.module.modules.PlayerHeadCounterModule;
import dev.lyfw.lyfwclient.module.modules.PlayerHeadGlowModule;
import dev.lyfw.lyfwclient.module.modules.PlaytimeModule;
import dev.lyfw.lyfwclient.module.modules.PopChamsModule;
import dev.lyfw.lyfwclient.module.modules.PopCounterModule;
import dev.lyfw.lyfwclient.module.modules.ProfilePresetsModule;
import dev.lyfw.lyfwclient.module.modules.ReachDisplayModule;
import dev.lyfw.lyfwclient.module.modules.ScoreboardModule;
import dev.lyfw.lyfwclient.module.modules.ShieldBannerModule;
import dev.lyfw.lyfwclient.module.modules.ShieldPositionModule;
import dev.lyfw.lyfwclient.module.modules.SkyboxModule;
import dev.lyfw.lyfwclient.module.modules.SnowModule;
import dev.lyfw.lyfwclient.module.modules.SongPlayerModule;
import dev.lyfw.lyfwclient.module.modules.SoundControlModule;
import dev.lyfw.lyfwclient.module.modules.SprintStatusModule;
import dev.lyfw.lyfwclient.module.modules.SupermanFlyingModule;
import dev.lyfw.lyfwclient.module.modules.TimeChangerModule;
import dev.lyfw.lyfwclient.module.modules.ToggleBrightnessModule;
import dev.lyfw.lyfwclient.module.modules.TotemCounterModule;
import dev.lyfw.lyfwclient.module.modules.TotemParticlesModule;
import dev.lyfw.lyfwclient.module.modules.TotemTransparencyModule;
import dev.lyfw.lyfwclient.module.modules.TotemTweaksModule;
import dev.lyfw.lyfwclient.module.modules.TransparentPlayersModule;
import dev.lyfw.lyfwclient.module.modules.WatermarkModule;

public class Modules {
   public static void registerAll() {
      ModuleManager.register(new WatermarkModule());
      ModuleManager.register(new KeystrokesModule());
      ModuleManager.register(new KeystrokesPadModule());
      ModuleManager.register(new MouseTrackerModule());
      ModuleManager.register(new MainMenuModule());
      ModuleManager.register(new CustomFontModule());
      ModuleManager.register(new GlobalColorModule());
      ModuleManager.register(new NametagPingModule());
      ModuleManager.register(new PipPresenceModule());
      ModuleManager.register(new SoundControlModule());
      ModuleManager.register(new CosmeticsModule());
      ModuleManager.register(new NostalgiaModule());
      ModuleManager.register(new FireColorModule());
      ModuleManager.register(new ScoreboardModule());
      ModuleManager.register(new TotemTransparencyModule());
      ModuleManager.register(new NoHandSwayModule());
      ModuleManager.register(new BrightnessModule());
      ModuleManager.register(new SkyboxModule());
      ModuleManager.register(new SnowModule());
      ModuleManager.register(new OptimizersModule());
      ModuleManager.register(new BlockColorsModule());
      ModuleManager.register(new FoodOverlayModule());
      ModuleManager.register(new PopChamsModule());
      ModuleManager.register(new ArmorHudModule());
      ModuleManager.register(new HotbarModule());
      ModuleManager.register(new CustomHotbarModule());
      ModuleManager.register(new CpsModule());
      ModuleManager.register(new FpsModule());
      ModuleManager.register(new PingModule());
      ModuleManager.register(new EffectTimersModule());
      ModuleManager.register(new ExactHealthModule());
      ModuleManager.register(new HealthRingModule());
      ModuleManager.register(new SprintStatusModule());
      ModuleManager.register(new PlayerHeadCounterModule());
      ModuleManager.register(new TotemCounterModule());
      ModuleManager.register(new PopCounterModule());
      ModuleManager.register(new ItemCounterModule());
      ModuleManager.register(new HideArmorModule());
      ModuleManager.register(new CleanViewModule());
      ModuleManager.register(new HideActionBarModule());
      ModuleManager.register(new HidePlayerModelModule());
      ModuleManager.register(new InventoryScaleModule());
      ModuleManager.register(new ItemGlintModule());
      ModuleManager.register(new DamageTintModule());
      ModuleManager.register(new PlayerGlowModule());
      ModuleManager.register(new PlayerHeadGlowModule());
      ModuleManager.register(new GreyscaleModule());
      ModuleManager.register(new NametagsModule());
      ModuleManager.register(new CobwebModule());
      ModuleManager.register(new BlockOpacityModule());
      ModuleManager.register(new CrystalCustomizerModule());
      ModuleManager.register(new AnchorCustomizerModule());
      ModuleManager.register(new DeathAnimationModule());
      ModuleManager.register(new BlockOutlineModule());
      ModuleManager.register(new KillEffectsModule());
      ModuleManager.register(new ToggleBrightnessModule());
      ModuleManager.register(new NameProtectModule());
      ModuleManager.register(new HitboxModule());
      ModuleManager.register(new HurtCamModule());
      ModuleManager.register(new AttackCooldownDesyncModule());
      ModuleManager.register(new ShieldPositionModule());
      ModuleManager.register(new ProfilePresetsModule());
      ModuleManager.register(new GuiThemeModule());
      ModuleManager.register(new CustomCrosshairModule());
      ModuleManager.register(new TotemTweaksModule());
      ModuleManager.register(new TimeChangerModule());
      ModuleManager.register(new TotemParticlesModule());
      ModuleManager.register(new ReachDisplayModule());
      ModuleManager.register(new MotionBlurModule());
      ModuleManager.register(new CustomFogModule());
      ModuleManager.register(new ShieldBannerModule());
      ModuleManager.register(new SongPlayerModule());
      ModuleManager.register(new PlaytimeModule());
      ModuleManager.register(new TransparentPlayersModule());
      ModuleManager.register(new SupermanFlyingModule());
      tagNew(
         "Health Ring",
         "Crystal Customizer",
         "Anchor Customizer",
         "Nostalgia",
         "Pop Chams",
         "Custom Hotbar",
         "Cosmetics",
         "Sound Control",
         "Global Color",
         "Nametag Ping",
         "Custom Font",
         "Main Menu",
         "Mouse Tracker",
         "Transparent Players",
         "Song Player",
         "Keystrokes Pad",
         "Item Counter",
         "Shield Banner",
         "Custom Fog",
         "Kill Effects",
         "Block Opacity",
         "Totem Tweaks",
         "Fire Color",
         "Scoreboard",
         "Totem Transparency",
         "No Hand Sway",
         "Snow",
         "Food Overlay",
         "Optimizers",
         "Leaderboard",
         "Superman Flying",
         "Effect Timers",
         "Player Glow"
      );
      tagUpdated(
         "Cosmetics",
         "Death Animation",
         "Item Glint",
         "Block Colors",
         "Grayscale",
         "Hitboxes",
         "GUI Theme",
         "Brightness",
         "Skybox",
         "Name Protect",
         "Profile Presets"
      );
   }

   private static void tagNew(String... names) {
      for (String name : names) {
         Module module = ModuleManager.get(name);
         if (module != null) {
            module.tagNew = true;
         }
      }
   }

   private static void tagUpdated(String... names) {
      for (String name : names) {
         Module module = ModuleManager.get(name);
         if (module != null) {
            module.tagUpdated = true;
         }
      }
   }
}
