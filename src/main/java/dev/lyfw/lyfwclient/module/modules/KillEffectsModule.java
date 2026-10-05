package dev.lyfw.lyfwclient.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.lyfw.lyfwclient.LyfwClient;
import dev.lyfw.lyfwclient.gui.ModuleSettingsScreen;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.KeybindSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class KillEffectsModule extends Module {
   private final EnumSetting<KillEffectsModule.EffectStyle> style = this.register(new EnumSetting<>("Effect", KillEffectsModule.EffectStyle.LIGHTNING));
   private final BooleanSetting affectSelf = this.register(new BooleanSetting("Affect Self", true));
   private final SliderSetting scale = this.register(new SliderSetting("Scale", 1.0, 0.25, 3.0, 0.05, "x"));
   private final SliderSetting range = this.register(new SliderSetting("Range", 48.0, 8.0, 128.0, 1.0, "blocks"));
   private final KeybindSetting previewKeybind = this.register(new KeybindSetting("Preview Keybind", LyfwClient.previewKillEffectKey));
   private final BooleanSetting randomIncludeLightning = this.register(new BooleanSetting("Include Lightning", true));
   private final BooleanSetting randomIncludeShard = this.register(new BooleanSetting("Include Shard", true));
   private final BooleanSetting randomIncludeVortex = this.register(new BooleanSetting("Include Vortex", true));
   private final BooleanSetting randomIncludeSpiral = this.register(new BooleanSetting("Include Spiral", true));
   private final BooleanSetting randomIncludeUfo = this.register(new BooleanSetting("Include UFO", true));
   private final BooleanSetting randomIncludeAscension = this.register(new BooleanSetting("Include Ascension", true));
   private final BooleanSetting randomIncludeRings = this.register(new BooleanSetting("Include Rings", true));
   private final BooleanSetting randomIncludeFirework = this.register(new BooleanSetting("Include Firework", true));
   private final BooleanSetting randomIncludeTombstone = this.register(new BooleanSetting("Include Tombstone", true));
   private final BooleanSetting randomIncludeSplash = this.register(new BooleanSetting("Include Splash", true));
   private final ColorSetting boltColor = this.register(new ColorSetting("Bolt Color", -3148801));
   private final EnumSetting<KillEffectsModule.BoltStyle> boltStyle = this.register(new EnumSetting<>("Bolt Style", KillEffectsModule.BoltStyle.SMOOTH));
   private final SliderSetting boltHeight = this.register(new SliderSetting("Bolt Height", 12.0, 4.0, 24.0, 0.5, "blocks"));
   private final SliderSetting boltSegments = this.register(new SliderSetting("Bolt Segments", 8.0, 3.0, 16.0, 1.0, ""));
   private final SliderSetting boltJitter = this.register(new SliderSetting("Bolt Jitter", 0.6, 0.1, 2.0, 0.05, ""));
   private final SliderSetting boltWidth = this.register(new SliderSetting("Bolt Width", 0.12, 0.02, 0.5, 0.01, ""));
   private final SliderSetting boltBlockSize = this.register(new SliderSetting("Bolt Block Size", 0.22, 0.05, 0.6, 0.01, ""));
   private final SliderSetting boltBlockGap = this.register(new SliderSetting("Bolt Block Gap", 0.15, 0.0, 0.6, 0.01, ""));
   private final SliderSetting boltBlockGapJitter = this.register(new SliderSetting("Bolt Block Gap Jitter", 0.6, 0.0, 1.0, 0.05, ""));
   private final SliderSetting boltDuration = this.register(new SliderSetting("Bolt Duration", 0.25, 0.05, 1.0, 0.01, "s"));
   private final SliderSetting impactRadius = this.register(new SliderSetting("Impact Radius", 1.2, 0.1, 3.0, 0.05, "blocks"));
   private final SliderSetting impactCount = this.register(new SliderSetting("Impact Count", 6.0, 1.0, 40.0, 1.0, ""));
   private final SliderSetting impactSize = this.register(new SliderSetting("Impact Size", 0.12, 0.03, 0.4, 0.01, ""));
   private final SliderSetting impactDuration = this.register(new SliderSetting("Impact Duration", 0.3, 0.1, 1.0, 0.01, "s"));
   private final ColorSetting shardColor = this.register(new ColorSetting("Shard Color", -21965));
   private final SliderSetting shardCount = this.register(new SliderSetting("Shard Count", 18.0, 4.0, 50.0, 1.0, ""));
   private final SliderSetting shardSize = this.register(new SliderSetting("Shard Size", 0.15, 0.03, 0.6, 0.01, ""));
   private final SliderSetting shardSpeed = this.register(new SliderSetting("Shard Speed", 5.0, 0.5, 14.0, 0.1, "blocks/s"));
   private final SliderSetting shardGravity = this.register(new SliderSetting("Shard Gravity", 9.0, 1.0, 30.0, 0.5, "blocks/s²"));
   private final SliderSetting shardDuration = this.register(new SliderSetting("Shard Duration", 0.8, 0.2, 2.5, 0.05, "s"));
   private final ColorSetting vortexColor = this.register(new ColorSetting("Vortex Color", -5211393));
   private final SliderSetting vortexCount = this.register(new SliderSetting("Vortex Count", 18.0, 4.0, 40.0, 1.0, ""));
   private final SliderSetting vortexSize = this.register(new SliderSetting("Vortex Size", 0.13, 0.03, 0.4, 0.01, ""));
   private final SliderSetting vortexMaxRadius = this.register(new SliderSetting("Vortex Max Radius", 2.0, 0.3, 6.0, 0.1, "blocks"));
   private final SliderSetting vortexRiseHeight = this.register(new SliderSetting("Vortex Rise Height", 1.2, 0.0, 4.0, 0.05, "blocks"));
   private final SliderSetting vortexSpinSpeed = this.register(new SliderSetting("Vortex Spin Speed", 6.0, 1.0, 20.0, 0.5, "rad/s"));
   private final SliderSetting vortexDuration = this.register(new SliderSetting("Vortex Duration", 0.9, 0.2, 2.5, 0.05, "s"));
   private final ColorSetting spiralColor = this.register(new ColorSetting("Spiral Color", -10823169));
   private final SliderSetting spiralWidth = this.register(new SliderSetting("Spiral Width", 0.08, 0.02, 0.4, 0.01, ""));
   private final SliderSetting spiralTurns = this.register(new SliderSetting("Spiral Turns", 3.0, 0.5, 8.0, 0.1, ""));
   private final SliderSetting spiralHeadHeight = this.register(new SliderSetting("Spiral Head Height", 1.8, 0.5, 3.0, 0.05, "blocks"));
   private final SliderSetting spiralStartRadius = this.register(new SliderSetting("Spiral Top Radius", 0.5, 0.05, 2.0, 0.05, "blocks"));
   private final SliderSetting spiralEndRadius = this.register(new SliderSetting("Spiral Bottom Radius", 0.5, 0.0, 2.0, 0.05, "blocks"));
   private final SliderSetting spiralSegments = this.register(new SliderSetting("Spiral Segments", 40.0, 8.0, 100.0, 1.0, ""));
   private final SliderSetting spiralSpeed = this.register(new SliderSetting("Spiral Speed", 4.0, 0.5, 15.0, 0.1, "turns/s"));
   private final SliderSetting spiralFadeDuration = this.register(new SliderSetting("Spiral Fade Duration", 0.4, 0.1, 2.0, 0.05, "s"));
   private final ColorSetting ascensionColor = this.register(new ColorSetting("Ascension Color", -4158));
   private final ColorSetting ascensionBeamColor = this.register(new ColorSetting("Ascension Beam Color", 1442837184));
   private final ColorSetting ascensionCloudColor = this.register(new ColorSetting("Cloud Color", -420088331));
   private final SliderSetting ascensionCloudSize = this.register(new SliderSetting("Cloud Size", 1.6, 0.3, 4.0, 0.05, "blocks"));
   private final SliderSetting ascensionRingCount = this.register(new SliderSetting("Ring Count", 9.0, 2.0, 20.0, 1.0, ""));
   private final SliderSetting ascensionRingRadius = this.register(new SliderSetting("Ring Radius", 1.1, 0.3, 3.0, 0.05, "blocks"));
   private final SliderSetting ascensionRingThickness = this.register(new SliderSetting("Ring Thickness", 0.1, 0.02, 0.5, 0.01, "blocks"));
   private final SliderSetting ascensionRiseHeight = this.register(new SliderSetting("Rise Height", 7.0, 3.0, 30.0, 0.5, "blocks"));
   private final SliderSetting ascensionRiseSpeed = this.register(new SliderSetting("Rise Speed", 6.0, 1.0, 20.0, 0.5, "blocks/s"));
   private final SliderSetting ascensionRingSpacing = this.register(new SliderSetting("Ring Spacing", 0.15, 0.02, 1.0, 0.01, "s"));
   private final SliderSetting ascensionBeamWidth = this.register(new SliderSetting("Beam Width", 0.7, 0.1, 3.0, 0.05, "blocks"));
   private final ColorSetting ringsColor = this.register(new ColorSetting("Rings Color", -8396545));
   private final SliderSetting ringsThickness = this.register(new SliderSetting("Rings Thickness", 0.045, 0.02, 0.4, 0.01, "blocks"));
   private final SliderSetting ringsCount = this.register(new SliderSetting("Rings Count", 5.0, 2.0, 15.0, 1.0, ""));
   private final SliderSetting ringsStartHeight = this.register(new SliderSetting("Rings Start Height", 4.5, 1.0, 30.0, 0.5, "blocks"));
   private final SliderSetting ringsFallSpeed = this.register(new SliderSetting("Rings Fall Speed", 9.0, 1.0, 40.0, 0.5, "blocks/s"));
   private final SliderSetting ringsStartRadius = this.register(new SliderSetting("Rings Start Radius", 0.05, 0.0, 1.0, 0.01, "blocks"));
   private final SliderSetting ringsTargetRadius = this.register(new SliderSetting("Rings Target Radius", 0.5, 0.1, 3.0, 0.05, "blocks"));
   private final SliderSetting ringsStagger = this.register(new SliderSetting("Rings Stagger", 0.12, 0.0, 1.0, 0.01, "s"));
   private final SliderSetting fireworkShellCount = this.register(new SliderSetting("Shell Count", 4.0, 1.0, 8.0, 1.0, ""));
   private final SliderSetting fireworkLaunchSpeed = this.register(new SliderSetting("Launch Speed", 7.0, 2.0, 20.0, 0.5, "blocks/s"));
   private final SliderSetting fireworkLaunchStagger = this.register(new SliderSetting("Launch Stagger", 0.18, 0.0, 1.0, 0.01, "s"));
   private final SliderSetting fireworkMinHeight = this.register(new SliderSetting("Min Burst Height", 4.0, 1.0, 15.0, 0.5, "blocks"));
   private final SliderSetting fireworkMaxHeight = this.register(new SliderSetting("Max Burst Height", 8.0, 1.0, 25.0, 0.5, "blocks"));
   private final SliderSetting fireworkShellSize = this.register(new SliderSetting("Shell Size", 0.12, 0.03, 0.4, 0.01, ""));
   private final SliderSetting fireworkBurstShardCount = this.register(new SliderSetting("Burst Shard Count", 16.0, 4.0, 40.0, 1.0, ""));
   private final SliderSetting fireworkBurstSize = this.register(new SliderSetting("Burst Shard Size", 0.13, 0.03, 0.5, 0.01, ""));
   private final SliderSetting fireworkBurstSpeed = this.register(new SliderSetting("Burst Speed", 4.0, 0.5, 12.0, 0.1, "blocks/s"));
   private final SliderSetting fireworkBurstGravity = this.register(new SliderSetting("Burst Gravity", 6.0, 0.5, 20.0, 0.5, "blocks/s²"));
   private final SliderSetting fireworkBurstDuration = this.register(new SliderSetting("Burst Duration", 0.6, 0.1, 2.0, 0.05, "s"));
   private final ColorSetting tombstoneColor = this.register(new ColorSetting("Tombstone Color", -9539982));
   private final ColorSetting tombstoneDirtColor = this.register(new ColorSetting("Tombstone Dirt Color", -11914460));
   private final SliderSetting tombstoneWidth = this.register(new SliderSetting("Tombstone Width", 0.8, 0.3, 2.0, 0.05, "blocks"));
   private final SliderSetting tombstoneDepth = this.register(new SliderSetting("Tombstone Depth", 0.18, 0.05, 0.6, 0.01, "blocks"));
   private final SliderSetting tombstoneBodyHeight = this.register(new SliderSetting("Tombstone Body Height", 0.9, 0.3, 2.0, 0.05, "blocks"));
   private final SliderSetting tombstoneCapHeight = this.register(new SliderSetting("Tombstone Cap Height", 0.25, 0.05, 1.0, 0.01, "blocks"));
   private final SliderSetting tombstoneRiseDuration = this.register(new SliderSetting("Tombstone Rise Duration", 0.5, 0.1, 2.0, 0.05, "s"));
   private final SliderSetting tombstoneHoldDuration = this.register(new SliderSetting("Tombstone Hold Duration", 1.5, 0.2, 5.0, 0.05, "s"));
   private final SliderSetting tombstoneFadeDuration = this.register(new SliderSetting("Tombstone Fade Duration", 0.6, 0.1, 2.0, 0.05, "s"));
   private final SliderSetting tombstoneDustCount = this.register(new SliderSetting("Tombstone Dust Count", 10.0, 0.0, 30.0, 1.0, ""));
   private final SliderSetting tombstoneBurialDepth = this.register(new SliderSetting("Tombstone Burial Depth", 1.9, 0.5, 3.0, 0.1, "blocks"));
   private final SliderSetting tombstoneBurialStandDuration = this.register(new SliderSetting("Tombstone Burial Stand Duration", 0.8, 0.0, 3.0, 0.05, "s"));
   private final SliderSetting tombstoneBurialSinkDuration = this.register(new SliderSetting("Tombstone Burial Sink Duration", 1.2, 0.2, 4.0, 0.05, "s"));
   private final ColorSetting splashColor = this.register(new ColorSetting("Splash Color", -1338265360));
   private final ColorSetting splashGlowColor = this.register(new ColorSetting("Splash Glow Color", -1378305));
   private final SliderSetting splashColumnHeight = this.register(new SliderSetting("Splash Column Height", 7.0, 3.0, 14.0, 0.5, "blocks"));
   private final SliderSetting splashColumnWidth = this.register(new SliderSetting("Splash Column Width", 0.14, 0.04, 0.6, 0.02, "blocks"));
   private final SliderSetting splashFallDuration = this.register(new SliderSetting("Splash Fall Duration", 0.35, 0.1, 1.0, 0.05, "s"));
   private final SliderSetting splashDropletCount = this.register(new SliderSetting("Splash Droplet Count", 26.0, 4.0, 60.0, 1.0, ""));
   private final SliderSetting splashDropletSize = this.register(new SliderSetting("Splash Droplet Size", 0.09, 0.02, 0.3, 0.01, ""));
   private final SliderSetting splashDropletSpeed = this.register(new SliderSetting("Splash Droplet Speed", 4.5, 0.5, 12.0, 0.1, "blocks/s"));
   private final SliderSetting splashDropletGravity = this.register(new SliderSetting("Splash Droplet Gravity", 10.0, 1.0, 30.0, 0.5, "blocks/s²"));
   private final SliderSetting splashDropletDuration = this.register(new SliderSetting("Splash Droplet Duration", 0.6, 0.2, 2.0, 0.05, "s"));
   private final SliderSetting splashRingRadius = this.register(new SliderSetting("Splash Ring Radius", 1.4, 0.3, 4.0, 0.1, "blocks"));
   private final SliderSetting splashRingDuration = this.register(new SliderSetting("Splash Ring Duration", 0.5, 0.1, 1.5, 0.05, "s"));
   private final ColorSetting ufoColor = this.register(new ColorSetting("UFO Color", -4602156));
   private final ColorSetting ufoDomeColor = this.register(new ColorSetting("Dome Color", -864034561));
   private final ColorSetting ufoLightColor = this.register(new ColorSetting("Light Color", -11654));
   private final SliderSetting ufoLightCount = this.register(new SliderSetting("Light Count", 8.0, 4.0, 16.0, 1.0, ""));
   private final ColorSetting ufoBeamColor = this.register(new ColorSetting("Beam Color", 1722810304));
   private final SliderSetting ufoSize = this.register(new SliderSetting("UFO Size", 1.6, 0.6, 4.0, 0.05, ""));
   private final SliderSetting ufoStartHeight = this.register(new SliderSetting("Start Height", 14.0, 4.0, 30.0, 0.5, "blocks"));
   private final SliderSetting ufoHoverHeight = this.register(new SliderSetting("Hover Height", 6.0, 1.5, 15.0, 0.5, "blocks"));
   private final SliderSetting ufoDescendDuration = this.register(new SliderSetting("Descend Duration", 0.6, 0.1, 2.0, 0.05, "s"));
   private final SliderSetting ufoBeamDuration = this.register(new SliderSetting("Beam Duration", 0.9, 0.1, 3.0, 0.05, "s"));
   private final SliderSetting ufoAscendDuration = this.register(new SliderSetting("Ascend Duration", 0.6, 0.1, 2.0, 0.05, "s"));
   private final SliderSetting ufoBeamWidth = this.register(new SliderSetting("Beam Width", 0.9, 0.1, 3.0, 0.05, "blocks"));
   private final SliderSetting ufoGlowCount = this.register(new SliderSetting("Abduct Glow Count", 5.0, 2.0, 10.0, 1.0, ""));
   private final Map<UUID, Boolean> deadState;
   private final List<KillEffectsModule.ActiveEffect> activeEffects;
   private final Random random;
   private final Map<UUID, Long> burialStartMillis;
   private final Map<UUID, Long> standingUprightMillis;
   private static final int SPLASH_PATH_POINTS = 7;
   private static final int SPLASH_STRAND_COUNT = 3;

   public KillEffectsModule() {
      super(
         "Kill Effects",
         "Custom client-side visual effects that play at a player's feet the moment they die - no vanilla lightning or particles involved.",
         Category.RENDER,
         false
      );
      this.style.group = "General";
      this.affectSelf.group = "General";
      this.scale.group = "General";
      this.range.group = "General";
      this.previewKeybind.group = "General";
      this.randomIncludeLightning.group = "Random";
      this.randomIncludeShard.group = "Random";
      this.randomIncludeVortex.group = "Random";
      this.randomIncludeSpiral.group = "Random";
      this.randomIncludeUfo.group = "Random";
      this.randomIncludeAscension.group = "Random";
      this.randomIncludeRings.group = "Random";
      this.randomIncludeFirework.group = "Random";
      this.randomIncludeTombstone.group = "Random";
      this.randomIncludeSplash.group = "Random";
      this.boltColor.group = "Lightning";
      this.boltStyle.group = "Lightning";
      this.boltHeight.group = "Lightning";
      this.boltSegments.group = "Lightning";
      this.boltJitter.group = "Lightning";
      this.boltWidth.group = "Lightning";
      this.boltBlockSize.group = "Lightning";
      this.boltBlockGap.group = "Lightning";
      this.boltBlockGapJitter.group = "Lightning";
      this.boltDuration.group = "Lightning";
      this.impactRadius.group = "Lightning";
      this.impactCount.group = "Lightning";
      this.impactSize.group = "Lightning";
      this.impactDuration.group = "Lightning";
      this.shardColor.group = "Shard";
      this.shardCount.group = "Shard";
      this.shardSize.group = "Shard";
      this.shardSpeed.group = "Shard";
      this.shardGravity.group = "Shard";
      this.shardDuration.group = "Shard";
      this.vortexColor.group = "Vortex";
      this.vortexCount.group = "Vortex";
      this.vortexSize.group = "Vortex";
      this.vortexMaxRadius.group = "Vortex";
      this.vortexRiseHeight.group = "Vortex";
      this.vortexSpinSpeed.group = "Vortex";
      this.vortexDuration.group = "Vortex";
      this.spiralColor.group = "Spiral";
      this.spiralWidth.group = "Spiral";
      this.spiralTurns.group = "Spiral";
      this.spiralHeadHeight.group = "Spiral";
      this.spiralStartRadius.group = "Spiral";
      this.spiralEndRadius.group = "Spiral";
      this.spiralSegments.group = "Spiral";
      this.spiralSpeed.group = "Spiral";
      this.spiralFadeDuration.group = "Spiral";
      this.ascensionColor.group = "Ascension";
      this.ascensionBeamColor.group = "Ascension";
      this.ascensionRingCount.group = "Ascension";
      this.ascensionRingRadius.group = "Ascension";
      this.ascensionRingThickness.group = "Ascension";
      this.ascensionRiseHeight.group = "Ascension";
      this.ascensionRiseSpeed.group = "Ascension";
      this.ascensionRingSpacing.group = "Ascension";
      this.ascensionBeamWidth.group = "Ascension";
      this.ascensionCloudColor.group = "Ascension";
      this.ascensionCloudSize.group = "Ascension";
      this.ringsColor.group = "Rings";
      this.ringsThickness.group = "Rings";
      this.ringsCount.group = "Rings";
      this.ringsStartHeight.group = "Rings";
      this.ringsFallSpeed.group = "Rings";
      this.ringsStartRadius.group = "Rings";
      this.ringsTargetRadius.group = "Rings";
      this.ringsStagger.group = "Rings";
      this.fireworkShellCount.group = "Firework";
      this.fireworkLaunchSpeed.group = "Firework";
      this.fireworkLaunchStagger.group = "Firework";
      this.fireworkMinHeight.group = "Firework";
      this.fireworkMaxHeight.group = "Firework";
      this.fireworkShellSize.group = "Firework";
      this.fireworkBurstShardCount.group = "Firework";
      this.fireworkBurstSize.group = "Firework";
      this.fireworkBurstSpeed.group = "Firework";
      this.fireworkBurstGravity.group = "Firework";
      this.fireworkBurstDuration.group = "Firework";
      this.tombstoneColor.group = "Tombstone";
      this.tombstoneDirtColor.group = "Tombstone";
      this.tombstoneWidth.group = "Tombstone";
      this.tombstoneDepth.group = "Tombstone";
      this.tombstoneBodyHeight.group = "Tombstone";
      this.tombstoneCapHeight.group = "Tombstone";
      this.tombstoneRiseDuration.group = "Tombstone";
      this.tombstoneHoldDuration.group = "Tombstone";
      this.tombstoneFadeDuration.group = "Tombstone";
      this.tombstoneDustCount.group = "Tombstone";
      this.tombstoneBurialDepth.group = "Tombstone";
      this.tombstoneBurialStandDuration.group = "Tombstone";
      this.tombstoneBurialSinkDuration.group = "Tombstone";
      this.splashColor.group = "Splash";
      this.splashGlowColor.group = "Splash";
      this.splashColumnHeight.group = "Splash";
      this.splashColumnWidth.group = "Splash";
      this.splashFallDuration.group = "Splash";
      this.splashDropletCount.group = "Splash";
      this.splashDropletSize.group = "Splash";
      this.splashDropletSpeed.group = "Splash";
      this.splashDropletGravity.group = "Splash";
      this.splashDropletDuration.group = "Splash";
      this.splashRingRadius.group = "Splash";
      this.splashRingDuration.group = "Splash";
      this.ufoColor.group = "UFO";
      this.ufoDomeColor.group = "UFO";
      this.ufoLightColor.group = "UFO";
      this.ufoLightCount.group = "UFO";
      this.ufoBeamColor.group = "UFO";
      this.ufoSize.group = "UFO";
      this.ufoStartHeight.group = "UFO";
      this.ufoHoverHeight.group = "UFO";
      this.ufoDescendDuration.group = "UFO";
      this.ufoBeamDuration.group = "UFO";
      this.ufoAscendDuration.group = "UFO";
      this.ufoBeamWidth.group = "UFO";
      this.ufoGlowCount.group = "UFO";
      this.deadState = new HashMap<>();
      this.activeEffects = new ArrayList<>();
      this.random = new Random();
      this.burialStartMillis = new HashMap<>();
      this.standingUprightMillis = new HashMap<>();
   }

   @Override
   public void init() {
      WorldRenderEvents.END_MAIN.register(this::render);
   }

   @Override
   public Screen dedicatedSettingsScreen(Screen parent) {
      return new ModuleSettingsScreen(parent, this);
   }

   @Override
   public void tick() {
      if (!this.isEnabled()) {
         this.deadState.clear();
      } else {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.player != null) {
            long nowMillis = System.currentTimeMillis();
            this.burialStartMillis.entrySet().removeIf(e -> nowMillis - e.getValue() > 60000L);
            this.standingUprightMillis.entrySet().removeIf(e -> nowMillis - e.getValue() > 60000L);
            double maxDistSq = this.range.get() * this.range.get();
            Set<UUID> seen = new HashSet<>();

            for (Player player : mc.level.players()) {
               boolean isSelf = player == mc.player;
               if ((!isSelf || this.affectSelf.get()) && (isSelf || !(mc.player.distanceToSqr(player) > maxDistSq))) {
                  seen.add(player.getUUID());
                  boolean deadNow = player.isDeadOrDying();
                  Boolean wasDead = this.deadState.put(player.getUUID(), deadNow);
                  if (wasDead != null && !wasDead && deadNow) {
                     this.spawnEffect(player.position(), player.getVisualRotationYInDegrees(), player.getUUID());
                  } else if (wasDead != null && wasDead && !deadNow) {
                     this.burialStartMillis.remove(player.getUUID());
                     this.standingUprightMillis.remove(player.getUUID());
                  }
               }
            }

            this.deadState.keySet().removeIf(id -> !seen.contains(id));
         } else {
            this.deadState.clear();
         }
      }
   }

   public void preview(Vec3 origin) {
      Minecraft mc = Minecraft.getInstance();
      float yaw = mc.player != null ? mc.player.getVisualRotationYInDegrees() : 0.0F;
      this.spawnEffect(origin, yaw, null);
   }

   public boolean isKillEffectDeath(UUID id) {
      return this.isEnabled() && this.standingUprightMillis.containsKey(id);
   }

   private float burialProgress(UUID id) {
      if (!this.isEnabled()) {
         return -1.0F;
      } else {
         Long start = this.burialStartMillis.get(id);
         if (start == null) {
            return -1.0F;
         } else {
            float stand = (float)this.tombstoneBurialStandDuration.get().doubleValue();
            float sinkDuration = (float)this.tombstoneBurialSinkDuration.get().doubleValue();
            float elapsed = (float)(System.currentTimeMillis() - start) / 1000.0F;
            if (elapsed <= stand) {
               return 0.0F;
            } else {
               float t = elapsed - stand;
               return t >= sinkDuration ? 1.0F : t / sinkDuration;
            }
         }
      }
   }

   public float getBurialSink(UUID id) {
      float progress = this.burialProgress(id);
      if (progress < 0.0F) {
         return 0.0F;
      } else {
         float depth = (float)this.tombstoneBurialDepth.get().doubleValue();
         return depth * progress * progress;
      }
   }

   public float getBurialScale(UUID id) {
      float progress = this.burialProgress(id);
      if (progress < 0.0F) {
         return 1.0F;
      } else {
         float minScale = 0.02F;
         return 1.0F - progress * (1.0F - minScale);
      }
   }

   private void spawnEffect(Vec3 origin, float deathYawDegrees, UUID burialTarget) {
      KillEffectsModule.EffectStyle chosen = this.style.get();

      KillEffectsModule.EffectType type = switch (chosen) {
         case LIGHTNING -> KillEffectsModule.EffectType.LIGHTNING;
         case SHARD -> KillEffectsModule.EffectType.SHARD;
         case VORTEX -> KillEffectsModule.EffectType.VORTEX;
         case SPIRAL -> KillEffectsModule.EffectType.SPIRAL;
         case UFO -> KillEffectsModule.EffectType.UFO;
         case ASCENSION -> KillEffectsModule.EffectType.ASCENSION;
         case RINGS -> KillEffectsModule.EffectType.RINGS;
         case FIREWORK -> KillEffectsModule.EffectType.FIREWORK;
         case TOMBSTONE -> KillEffectsModule.EffectType.TOMBSTONE;
         case SPLASH -> KillEffectsModule.EffectType.SPLASH;
         case RANDOM -> this.pickRandomType();
      };
      KillEffectsModule.ActiveEffect effect = new KillEffectsModule.ActiveEffect();
      effect.type = type;
      effect.origin = origin;
      effect.startMillis = System.currentTimeMillis();
      effect.scaleAtSpawn = (float)this.scale.get().doubleValue();
      effect.boltStyleAtSpawn = this.boltStyle.get();
      if (burialTarget != null) {
         this.standingUprightMillis.put(burialTarget, effect.startMillis);
      }

      switch (type) {
         case LIGHTNING:
            this.generateBolt(effect);
            this.generateImpact(effect);
            break;
         case SHARD:
            this.generateDebris(effect, Math.max(1, this.shardCount.getInt()));
            break;
         case VORTEX:
            this.generateVortex(effect);
            break;
         case SPIRAL:
            this.generateSpiral(effect);
            break;
         case UFO:
            this.generateUfo(effect);
         case ASCENSION:
         case RINGS:
         default:
            break;
         case FIREWORK:
            this.generateFirework(effect);
            break;
         case TOMBSTONE:
            this.generateTombstone(effect, (float)Math.toRadians(deathYawDegrees), burialTarget);
            break;
         case SPLASH:
            this.generateSplash(effect);
      }

      this.activeEffects.add(effect);
   }

   private KillEffectsModule.EffectType pickRandomType() {
      List<KillEffectsModule.EffectType> eligible = new ArrayList<>(KillEffectsModule.EffectType.values().length);
      if (this.randomIncludeLightning.get()) {
         eligible.add(KillEffectsModule.EffectType.LIGHTNING);
      }

      if (this.randomIncludeShard.get()) {
         eligible.add(KillEffectsModule.EffectType.SHARD);
      }

      if (this.randomIncludeVortex.get()) {
         eligible.add(KillEffectsModule.EffectType.VORTEX);
      }

      if (this.randomIncludeSpiral.get()) {
         eligible.add(KillEffectsModule.EffectType.SPIRAL);
      }

      if (this.randomIncludeUfo.get()) {
         eligible.add(KillEffectsModule.EffectType.UFO);
      }

      if (this.randomIncludeAscension.get()) {
         eligible.add(KillEffectsModule.EffectType.ASCENSION);
      }

      if (this.randomIncludeRings.get()) {
         eligible.add(KillEffectsModule.EffectType.RINGS);
      }

      if (this.randomIncludeFirework.get()) {
         eligible.add(KillEffectsModule.EffectType.FIREWORK);
      }

      if (this.randomIncludeTombstone.get()) {
         eligible.add(KillEffectsModule.EffectType.TOMBSTONE);
      }

      if (this.randomIncludeSplash.get()) {
         eligible.add(KillEffectsModule.EffectType.SPLASH);
      }

      return eligible.isEmpty()
         ? KillEffectsModule.EffectType.values()[this.random.nextInt(KillEffectsModule.EffectType.values().length)]
         : eligible.get(this.random.nextInt(eligible.size()));
   }

   private void generateBolt(KillEffectsModule.ActiveEffect effect) {
      int segments = Math.max(1, this.boltSegments.getInt());
      float height = (float)this.boltHeight.get().doubleValue() * effect.scaleAtSpawn;
      float jitter = (float)this.boltJitter.get().doubleValue() * effect.scaleAtSpawn;
      boolean blocky = effect.boltStyleAtSpawn == KillEffectsModule.BoltStyle.BLOCKY;
      float grid = Math.max(0.05F, jitter * 0.5F);
      List<Vec3> points = new ArrayList<>(segments + 1);

      for (int i = 0; i <= segments; i++) {
         float t = (float)i / segments;
         float y = height * (1.0F - t);
         float jx = (this.random.nextFloat() * 2.0F - 1.0F) * jitter * t;
         float jz = (this.random.nextFloat() * 2.0F - 1.0F) * jitter * t;
         if (blocky) {
            jx = Math.round(jx / grid) * grid;
            jz = Math.round(jz / grid) * grid;
         }

         points.add(new Vec3(jx, y, jz));
      }

      points.set(segments, new Vec3(0.0, 0.0, 0.0));
      effect.boltPoints = points;
      if (blocky) {
         float blockSize = (float)this.boltBlockSize.get().doubleValue() * effect.scaleAtSpawn;
         float gap = (float)this.boltBlockGap.get().doubleValue() * effect.scaleAtSpawn;
         float gapJitter = (float)this.boltBlockGapJitter.get().doubleValue();
         effect.boltBlockPositions = buildBlockyPositions(points, Math.max(0.05F, blockSize + gap), gapJitter, this.random);
      }
   }

   private static List<Vec3> buildBlockyPositions(List<Vec3> path, float baseStep, float gapJitter, Random random) {
      int n = path.size();
      double[] cum = new double[n];

      for (int i = 1; i < n; i++) {
         cum[i] = cum[i - 1] + path.get(i - 1).distanceTo(path.get(i));
      }

      double total = cum[n - 1];
      List<Vec3> result = new ArrayList<>();
      double dist = 0.0;
      int segIdx = 0;

      while (dist <= total) {
         while (segIdx < n - 2 && cum[segIdx + 1] < dist) {
            segIdx++;
         }

         double segStart = cum[segIdx];
         double segLen = Math.max(1.0E-6, cum[segIdx + 1] - segStart);
         float f = (float)Math.min(1.0, Math.max(0.0, (dist - segStart) / segLen));
         Vec3 a = path.get(segIdx);
         Vec3 b = path.get(segIdx + 1);
         result.add(a.add(b.subtract(a).scale(f)));
         float jitterMul = 1.0F + (random.nextFloat() * 2.0F - 1.0F) * gapJitter;
         dist += Math.max(baseStep * 0.15F, baseStep * jitterMul);
      }

      Vec3 last = path.get(n - 1);
      if (result.isEmpty() || result.get(result.size() - 1).distanceTo(last) > baseStep * 0.3F) {
         result.add(last);
      }

      return result;
   }

   private void generateImpact(KillEffectsModule.ActiveEffect effect) {
      int count = Math.max(1, this.impactCount.getInt());
      effect.impactDir = new Vec3[count];
      effect.impactTargetDist = new float[count];
      float maxDist = (float)this.impactRadius.get().doubleValue() * effect.scaleAtSpawn;

      for (int i = 0; i < count; i++) {
         float azimuth = (float)i / count * (float) (Math.PI * 2) + this.random.nextFloat() * 0.5F;
         float elevation = this.random.nextFloat() * (float) (Math.PI * 2.0 / 5.0);
         float horiz = (float)Math.cos(elevation);
         float vert = (float)Math.sin(elevation);
         effect.impactDir[i] = new Vec3(Math.cos(azimuth) * horiz, vert, Math.sin(azimuth) * horiz);
         effect.impactTargetDist[i] = maxDist * (0.3F + this.random.nextFloat() * 0.7F);
      }
   }

   private void generateDebris(KillEffectsModule.ActiveEffect effect, int count) {
      effect.shardDir = new Vec3[count];
      effect.shardSpeedMul = new float[count];

      for (int i = 0; i < count; i++) {
         float azimuth = (float)i / count * (float) (Math.PI * 2) + this.random.nextFloat() * 0.4F;
         float elevation = this.random.nextFloat() * (float) (Math.PI / 2) * 0.9F;
         float horiz = (float)Math.cos(elevation);
         float vert = (float)Math.sin(elevation);
         effect.shardDir[i] = new Vec3(Math.cos(azimuth) * horiz, vert, Math.sin(azimuth) * horiz);
         effect.shardSpeedMul[i] = 0.7F + this.random.nextFloat() * 0.6F;
      }
   }

   private void generateVortex(KillEffectsModule.ActiveEffect effect) {
      int count = Math.max(1, this.vortexCount.getInt());
      effect.vortexBaseAngle = new float[count];
      effect.vortexRadiusMul = new float[count];
      effect.vortexHeightMul = new float[count];

      for (int i = 0; i < count; i++) {
         effect.vortexBaseAngle[i] = (float)i / count * (float) (Math.PI * 2);
         effect.vortexRadiusMul[i] = 0.6F + this.random.nextFloat() * 0.4F;
         effect.vortexHeightMul[i] = 0.5F + this.random.nextFloat() * 0.5F;
      }
   }

   private void generateSpiral(KillEffectsModule.ActiveEffect effect) {
      int segments = Math.max(2, this.spiralSegments.getInt());
      float headHeight = (float)this.spiralHeadHeight.get().doubleValue() * effect.scaleAtSpawn;
      float turns = (float)this.spiralTurns.get().doubleValue();
      float startR = (float)this.spiralStartRadius.get().doubleValue() * effect.scaleAtSpawn;
      float endR = (float)this.spiralEndRadius.get().doubleValue() * effect.scaleAtSpawn;
      float speed = (float)this.spiralSpeed.get().doubleValue();
      effect.spiralDrawDurationAtSpawn = turns / Math.max(0.05F, speed);
      float startAngle = this.random.nextFloat() * (float) (Math.PI * 2);
      List<Vec3> points = new ArrayList<>(segments + 1);

      for (int i = 0; i <= segments; i++) {
         float t = (float)i / segments;
         float y = headHeight * (1.0F - t);
         float angle = startAngle + t * turns * (float) (Math.PI * 2);
         float radius = startR + (endR - startR) * t;
         points.add(new Vec3(Math.cos(angle) * radius, y, Math.sin(angle) * radius));
      }

      effect.spiralPoints = points;
   }

   private void generateUfo(KillEffectsModule.ActiveEffect effect) {
      int count = Math.max(1, this.ufoGlowCount.getInt());
      effect.ufoOrbAngleOffset = new float[count];

      for (int i = 0; i < count; i++) {
         effect.ufoOrbAngleOffset[i] = (float)i / count * (float) (Math.PI * 2) + this.random.nextFloat() * 0.5F;
      }
   }

   private void generateFirework(KillEffectsModule.ActiveEffect effect) {
      int shellCount = Math.max(1, this.fireworkShellCount.getInt());
      float minH = (float)this.fireworkMinHeight.get().doubleValue();
      float maxH = (float)this.fireworkMaxHeight.get().doubleValue();
      float stagger = (float)this.fireworkLaunchStagger.get().doubleValue();
      effect.fwShellDrift = new Vec3[shellCount];
      effect.fwShellDelay = new float[shellCount];
      effect.fwShellHeight = new float[shellCount];
      effect.fwShellColor = new int[shellCount];

      for (int i = 0; i < shellCount; i++) {
         effect.fwShellDrift[i] = new Vec3((this.random.nextFloat() * 2.0F - 1.0F) * 0.6, 0.0, (this.random.nextFloat() * 2.0F - 1.0F) * 0.6);
         effect.fwShellDelay[i] = i * stagger + this.random.nextFloat() * 0.05F;
         effect.fwShellHeight[i] = minH + (maxH - minH) * this.random.nextFloat();
         effect.fwShellColor[i] = 0xFF000000 | Color.HSBtoRGB(this.random.nextFloat(), 0.85F, 1.0F);
      }

      int burstCount = Math.max(1, this.fireworkBurstShardCount.getInt());
      effect.fwBurstDir = new Vec3[burstCount];
      effect.fwBurstSpeedMul = new float[burstCount];

      for (int i = 0; i < burstCount; i++) {
         float azimuth = (float)i / burstCount * (float) (Math.PI * 2) + this.random.nextFloat() * 0.4F;
         float elevation = (this.random.nextFloat() * 2.0F - 1.0F) * (float) (Math.PI / 2) * 0.95F;
         float horiz = (float)Math.cos(elevation);
         float vert = (float)Math.sin(elevation);
         effect.fwBurstDir[i] = new Vec3(Math.cos(azimuth) * horiz, vert, Math.sin(azimuth) * horiz);
         effect.fwBurstSpeedMul[i] = 0.7F + this.random.nextFloat() * 0.6F;
      }
   }

   private void generateTombstone(KillEffectsModule.ActiveEffect effect, float deathYaw, UUID burialTarget) {
      effect.tombstoneYaw = deathYaw;
      if (burialTarget != null) {
         this.burialStartMillis.put(burialTarget, effect.startMillis);
      }

      int dustCount = Math.max(0, this.tombstoneDustCount.getInt());
      effect.tombstoneDustDir = new Vec3[dustCount];
      effect.tombstoneDustSpeedMul = new float[dustCount];

      for (int i = 0; i < dustCount; i++) {
         float azimuth = (float)i / dustCount * (float) (Math.PI * 2) + this.random.nextFloat() * 0.4F;
         float elevation = this.random.nextFloat() * (float) (Math.PI * 2.0 / 5.0);
         float horiz = (float)Math.cos(elevation);
         float vert = (float)Math.sin(elevation);
         effect.tombstoneDustDir[i] = new Vec3(Math.cos(azimuth) * horiz, vert, Math.sin(azimuth) * horiz);
         effect.tombstoneDustSpeedMul[i] = 0.6F + this.random.nextFloat() * 0.6F;
      }
   }

   private void generateSplash(KillEffectsModule.ActiveEffect effect) {
      int count = Math.max(0, this.splashDropletCount.getInt());
      effect.splashDropletDir = new Vec3[count];
      effect.splashDropletSpeedMul = new float[count];

      for (int i = 0; i < count; i++) {
         float azimuth = (float)i / count * (float) (Math.PI * 2) + this.random.nextFloat() * 0.4F;
         float elevation = this.random.nextFloat() * (float) (Math.PI * 5.0 / 11.0);
         float horiz = (float)Math.cos(elevation);
         float vert = (float)Math.sin(elevation);
         effect.splashDropletDir[i] = new Vec3(Math.cos(azimuth) * horiz, vert, Math.sin(azimuth) * horiz);
         effect.splashDropletSpeedMul[i] = 0.6F + this.random.nextFloat() * 0.7F;
      }

      float columnHeight = (float)this.splashColumnHeight.get().doubleValue() * effect.scaleAtSpawn;
      effect.splashColumnHeightAtSpawn = columnHeight;
      float wobble = 0.09F * effect.scaleAtSpawn;
      float bundleRadius = 0.05F * effect.scaleAtSpawn;
      List<List<Vec3>> strands = new ArrayList<>(3);

      for (int s = 0; s < 3; s++) {
         float strandAngle = s / 3.0F * (float) (Math.PI * 2) + this.random.nextFloat() * 0.6F;
         float baseX = (float)Math.cos(strandAngle) * bundleRadius;
         float baseZ = (float)Math.sin(strandAngle) * bundleRadius;
         List<Vec3> path = new ArrayList<>(7);

         for (int i = 0; i < 7; i++) {
            float f = i / 6.0F;
            float y = columnHeight * f;
            float jx = i == 0 ? 0.0F : baseX * f + (this.random.nextFloat() * 2.0F - 1.0F) * wobble * f;
            float jz = i == 0 ? 0.0F : baseZ * f + (this.random.nextFloat() * 2.0F - 1.0F) * wobble * f;
            path.add(new Vec3(jx, y, jz));
         }

         strands.add(path);
      }

      effect.splashStrands = strands;
   }

   private void render(WorldRenderContext context) {
      if (!this.activeEffects.isEmpty()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 camPos = camera.position();
            PoseStack matrices = context.matrices();
            matrices.pushPose();
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);

            try {
               VertexConsumer vc = context.consumers().getBuffer(RenderTypes.debugQuads());
               Pose entry = matrices.last();
               long now = System.currentTimeMillis();
               Iterator<KillEffectsModule.ActiveEffect> it = this.activeEffects.iterator();

               while (it.hasNext()) {
                  KillEffectsModule.ActiveEffect effect = it.next();
                  float elapsed = (float)(now - effect.startMillis) / 1000.0F;

                  boolean done = switch (effect.type) {
                     case LIGHTNING -> this.renderLightning(vc, entry, effect, elapsed, camPos);
                     case SHARD -> {
                        this.renderDebrisBurst(vc, entry, effect, elapsed, camPos);
                        yield elapsed >= (float)this.shardDuration.get().doubleValue();
                     }
                     case VORTEX -> this.renderVortex(vc, entry, effect, elapsed, camPos);
                     case SPIRAL -> this.renderSpiral(vc, entry, effect, elapsed, camPos);
                     case UFO -> this.renderUfo(vc, entry, effect, elapsed, camPos);
                     case ASCENSION -> this.renderAscension(vc, entry, effect, elapsed, camPos);
                     case RINGS -> this.renderRings(vc, entry, effect, elapsed);
                     case FIREWORK -> this.renderFirework(vc, entry, effect, elapsed, camPos);
                     case TOMBSTONE -> this.renderTombstone(vc, entry, effect, elapsed, camPos);
                     case SPLASH -> this.renderSplash(vc, entry, effect, elapsed, camPos);
                  };
                  if (done) {
                     it.remove();
                  }
               }
            } finally {
               matrices.popPose();
            }
         }
      }
   }

   private boolean renderLightning(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float duration = (float)this.boltDuration.get().doubleValue();
      float impactDelay = duration * 0.55F;
      float impactDur = (float)this.impactDuration.get().doubleValue();
      float totalDuration = Math.max(duration, impactDelay + impactDur);
      if (elapsed < duration) {
         float t = elapsed / duration;
         float alphaMul = t < 0.5F ? 1.0F : 1.0F - (t - 0.5F) / 0.5F;
         int color = withAlphaMul(this.boltColor.get(), alphaMul);
         if (effect.boltStyleAtSpawn == KillEffectsModule.BoltStyle.BLOCKY) {
            this.renderBoltBlocky(vc, entry, effect, color, camPos);
         } else {
            this.renderBoltSmooth(vc, entry, effect, color, camPos);
         }
      }

      if (elapsed >= impactDelay) {
         this.renderImpactBurst(vc, entry, effect, elapsed - impactDelay, camPos);
      }

      return elapsed >= totalDuration;
   }

   private void renderBoltSmooth(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, int color, Vec3 camPos) {
      float width = (float)this.boltWidth.get().doubleValue() * effect.scaleAtSpawn;
      List<Vec3> points = effect.boltPoints;

      for (int i = 0; i < points.size() - 1; i++) {
         Vec3 a = effect.origin.add(points.get(i));
         Vec3 b = effect.origin.add(points.get(i + 1));
         renderStroke(vc, entry, a, b, width, color, color, camPos);
      }
   }

   private void renderBoltBlocky(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, int color, Vec3 camPos) {
      float blockSize = (float)this.boltBlockSize.get().doubleValue() * effect.scaleAtSpawn;

      for (Vec3 p : effect.boltBlockPositions) {
         emitBillboard(vc, entry, effect.origin.add(p), blockSize / 2.0F, color, camPos);
      }
   }

   private void renderImpactBurst(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float t, Vec3 camPos) {
      float duration = (float)this.impactDuration.get().doubleValue();
      if (!(t < 0.0F) && !(t >= duration) && effect.impactDir != null) {
         float progress = t / duration;
         float ease = 1.0F - (1.0F - progress) * (1.0F - progress);
         float alphaMul = 1.0F - progress;
         int color = withAlphaMul(this.boltColor.get(), alphaMul);
         float size = (float)this.impactSize.get().doubleValue() * effect.scaleAtSpawn;
         int n = effect.impactDir.length;

         for (int i = 0; i < n; i++) {
            float dist = effect.impactTargetDist[i] * ease;
            Vec3 pos = effect.origin.add(effect.impactDir[i].scale(dist));
            emitBillboard(vc, entry, pos, size / 2.0F, color, camPos);
         }
      }
   }

   private boolean renderVortex(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float duration = (float)this.vortexDuration.get().doubleValue();
      if (elapsed >= duration) {
         return true;
      } else {
         float progress = elapsed / duration;
         float ease = 1.0F - (1.0F - progress) * (1.0F - progress);
         float alphaMul = 1.0F - progress;
         int color = withAlphaMul(this.vortexColor.get(), alphaMul);
         float size = (float)this.vortexSize.get().doubleValue() * effect.scaleAtSpawn;
         float maxRadius = (float)this.vortexMaxRadius.get().doubleValue() * effect.scaleAtSpawn;
         float riseHeight = (float)this.vortexRiseHeight.get().doubleValue() * effect.scaleAtSpawn;
         float spinSpeed = (float)this.vortexSpinSpeed.get().doubleValue();
         int n = effect.vortexBaseAngle.length;

         for (int i = 0; i < n; i++) {
            float radius = maxRadius * ease * effect.vortexRadiusMul[i];
            float angle = effect.vortexBaseAngle[i] + spinSpeed * elapsed;
            float height = riseHeight * ease * effect.vortexHeightMul[i];
            Vec3 pos = effect.origin.add(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
            emitBillboard(vc, entry, pos, size / 2.0F, color, camPos);
         }

         return false;
      }
   }

   private boolean renderSpiral(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float drawDuration = effect.spiralDrawDurationAtSpawn;
      float fadeDuration = (float)this.spiralFadeDuration.get().doubleValue();
      float total = drawDuration + fadeDuration;
      if (elapsed >= total) {
         return true;
      } else {
         List<Vec3> full = effect.spiralPoints;
         int segCount = full.size() - 1;
         float drawT = Math.min(1.0F, elapsed / drawDuration);
         float revealFloat = drawT * segCount;
         int revealIdx = Math.min(segCount, (int)revealFloat);
         float revealFrac = revealFloat - revealIdx;
         List<Vec3> visible = new ArrayList<>(revealIdx + 2);

         for (int i = 0; i <= revealIdx; i++) {
            visible.add(full.get(i));
         }

         if (revealIdx < segCount && revealFrac > 1.0E-4F) {
            Vec3 a = full.get(revealIdx);
            Vec3 b = full.get(revealIdx + 1);
            visible.add(a.add(b.subtract(a).scale(revealFrac)));
         }

         if (visible.size() < 2) {
            return false;
         } else {
            float alphaMul = elapsed > drawDuration ? Math.max(0.0F, 1.0F - (elapsed - drawDuration) / fadeDuration) : 1.0F;
            int baseColor = this.spiralColor.get();
            float width = (float)this.spiralWidth.get().doubleValue() * effect.scaleAtSpawn;
            int glowColor = withAlphaMul(baseColor, alphaMul * 0.3F);
            renderPolylineRibbon(vc, entry, effect.origin, visible, width * 2.5F, glowColor, camPos);
            int coreColor = withAlphaMul(baseColor, alphaMul);
            renderTube(vc, entry, effect.origin, visible, width / 2.0F, 6, coreColor);
            if (elapsed < drawDuration) {
               Vec3 tipPos = effect.origin.add(visible.get(visible.size() - 1));
               int tipColor = withAlphaMul(baseColor | 0xFF000000, alphaMul);
               emitBillboard(vc, entry, tipPos, width * 1.5F, tipColor, camPos);
            }

            return false;
         }
      }
   }

   private static void renderPolylineRibbon(VertexConsumer vc, Pose entry, Vec3 origin, List<Vec3> localPoints, float width, int color, Vec3 camPos) {
      int n = localPoints.size();
      Vec3[] tangent = new Vec3[n];

      for (int i = 0; i < n; i++) {
         Vec3 raw;
         if (i == 0) {
            raw = localPoints.get(1).subtract(localPoints.get(0));
         } else if (i == n - 1) {
            raw = localPoints.get(n - 1).subtract(localPoints.get(n - 2));
         } else {
            raw = localPoints.get(i + 1).subtract(localPoints.get(i - 1));
         }

         tangent[i] = safeNormalize(raw, new Vec3(1.0, 0.0, 0.0));
      }

      Vec3[] left = new Vec3[n];
      Vec3[] right = new Vec3[n];

      for (int i = 0; i < n; i++) {
         Vec3 worldPos = origin.add(localPoints.get(i));
         Vec3 camDir = safeNormalize(camPos.subtract(worldPos), new Vec3(0.0, 1.0, 0.0));
         Vec3 perp = tangent[i].cross(camDir);
         if (perp.lengthSqr() < 1.0E-6) {
            perp = tangent[i].cross(new Vec3(0.0, 1.0, 0.0));
         }

         perp = safeNormalize(perp, new Vec3(1.0, 0.0, 0.0)).scale(width / 2.0);
         left[i] = worldPos.add(perp);
         right[i] = worldPos.subtract(perp);
      }

      for (int i = 0; i < n - 1; i++) {
         emitQuad(vc, entry, left[i], right[i], right[i + 1], left[i + 1], color, color, color, color);
      }
   }

   private static void renderTube(VertexConsumer vc, Pose entry, Vec3 origin, List<Vec3> localPoints, float radius, int tubeSegments, int color) {
      int[] colors = new int[localPoints.size()];

      for (int i = 0; i < colors.length; i++) {
         colors[i] = color;
      }

      renderTube(vc, entry, origin, localPoints, radius, tubeSegments, colors);
   }

   private static void renderTube(VertexConsumer vc, Pose entry, Vec3 origin, List<Vec3> localPoints, float radius, int tubeSegments, int[] colors) {
      int n = localPoints.size();
      if (n >= 2) {
         Vec3[] tangent = new Vec3[n];

         for (int i = 0; i < n; i++) {
            Vec3 raw;
            if (i == 0) {
               raw = localPoints.get(1).subtract(localPoints.get(0));
            } else if (i == n - 1) {
               raw = localPoints.get(n - 1).subtract(localPoints.get(n - 2));
            } else {
               raw = localPoints.get(i + 1).subtract(localPoints.get(i - 1));
            }

            tangent[i] = safeNormalize(raw, new Vec3(1.0, 0.0, 0.0));
         }

         Vec3[][] ring = new Vec3[n][tubeSegments];

         for (int i = 0; i < n; i++) {
            Vec3 worldPos = origin.add(localPoints.get(i));
            Vec3 t = tangent[i];
            Vec3 up = Math.abs(t.y) > 0.99 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
            Vec3 u = safeNormalize(t.cross(up), new Vec3(1.0, 0.0, 0.0));
            Vec3 v = safeNormalize(u.cross(t), new Vec3(0.0, 1.0, 0.0));

            for (int s = 0; s < tubeSegments; s++) {
               float a = (float)s / tubeSegments * (float) (Math.PI * 2);
               ring[i][s] = worldPos.add(u.scale(Math.cos(a) * radius)).add(v.scale(Math.sin(a) * radius));
            }
         }

         for (int i = 0; i < n - 1; i++) {
            for (int s = 0; s < tubeSegments; s++) {
               int s2 = (s + 1) % tubeSegments;
               emitQuad(vc, entry, ring[i][s], ring[i][s2], ring[i + 1][s2], ring[i + 1][s], colors[i], colors[i], colors[i + 1], colors[i + 1]);
            }
         }
      }
   }

   private boolean renderUfo(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float descend = (float)this.ufoDescendDuration.get().doubleValue();
      float beam = (float)this.ufoBeamDuration.get().doubleValue();
      float ascend = (float)this.ufoAscendDuration.get().doubleValue();
      float total = descend + beam + ascend;
      if (elapsed >= total) {
         return true;
      } else {
         float startH = (float)this.ufoStartHeight.get().doubleValue() * effect.scaleAtSpawn;
         float hoverH = (float)this.ufoHoverHeight.get().doubleValue() * effect.scaleAtSpawn;
         float size = (float)this.ufoSize.get().doubleValue() * effect.scaleAtSpawn;
         float beamEnvelope = 0.0F;
         float glowProgress = -1.0F;
         float ufoY;
         if (elapsed < descend) {
            float p = elapsed / descend;
            float ease = 1.0F - (1.0F - p) * (1.0F - p);
            ufoY = startH + (hoverH - startH) * ease;
         } else if (elapsed < descend + beam) {
            ufoY = hoverH;
            float bt = Math.min(1.0F, Math.max(0.0F, (elapsed - descend) / beam));
            beamEnvelope = (float)Math.sin(Math.PI * bt);
            glowProgress = bt;
         } else {
            float p = (elapsed - descend - beam) / ascend;
            ufoY = hoverH + (startH - hoverH) * (p * p);
         }

         Vec3 ufoCenter = effect.origin.add(0.0, ufoY, 0.0);
         int lightCount = Math.max(1, this.ufoLightCount.getInt());
         renderSaucer(vc, entry, ufoCenter, size, this.ufoColor.get(), this.ufoDomeColor.get(), this.ufoLightColor.get(), lightCount, camPos);
         if (beamEnvelope > 0.01F) {
            float beamBottomR = (float)this.ufoBeamWidth.get().doubleValue() * effect.scaleAtSpawn;
            float beamTopR = size * 0.2F;
            float beamTopY = ufoY - size * 0.14F;
            int outerCol = withAlphaMul(this.ufoBeamColor.get(), beamEnvelope * 0.6F);
            emitCone(vc, entry, effect.origin, beamTopY, 0.05F, beamTopR, beamBottomR, 24, outerCol);
            emitFilledDisc(vc, entry, effect.origin, 0.03F, beamBottomR, 24, outerCol);
            int coreCol = withAlphaMul(this.ufoBeamColor.get() | 0xFF000000, beamEnvelope * 0.85F);
            emitCone(vc, entry, effect.origin, beamTopY, 0.06F, beamTopR * 0.5F, beamBottomR * 0.35F, 16, coreCol);
            int strutCol = withAlphaMul(this.ufoLightColor.get(), beamEnvelope);
            int strutCount = 6;

            for (int i = 0; i < strutCount; i++) {
               float angle = (float)i / strutCount * (float) (Math.PI * 2);
               Vec3 top = effect.origin.add(Math.cos(angle) * beamTopR, beamTopY, Math.sin(angle) * beamTopR);
               Vec3 bot = effect.origin.add(Math.cos(angle) * beamBottomR, 0.04, Math.sin(angle) * beamBottomR);
               renderStroke(vc, entry, top, bot, size * 0.025F, strutCol, strutCol, camPos);
            }
         }

         if (glowProgress >= 0.0F) {
            float glowY = hoverH * (glowProgress * glowProgress);
            Vec3 glowCenter = effect.origin.add(0.0, glowY, 0.0);
            int glowColor = withAlphaMul(this.ufoBeamColor.get() | 0xFF000000, beamEnvelope);
            float orbRadius = size * 0.18F * (1.0F - glowProgress * 0.5F);
            float orbSize = size * 0.09F;
            float spin = 10.0F;
            int n = effect.ufoOrbAngleOffset.length;

            for (int i = 0; i < n; i++) {
               float angle = effect.ufoOrbAngleOffset[i] + spin * elapsed;
               Vec3 pos = glowCenter.add(Math.cos(angle) * orbRadius, 0.0, Math.sin(angle) * orbRadius);
               emitBillboard(vc, entry, pos, orbSize / 2.0F, glowColor, camPos);
            }
         }

         return false;
      }
   }

   private static void renderSaucer(
      VertexConsumer vc, Pose entry, Vec3 center, float size, int hullColor, int domeColor, int lightColor, int lightCount, Vec3 camPos
   ) {
      float bodyBottomR = size * 0.9F;
      float bodyTopR = size * 0.6F;
      float bodyHalfThick = size * 0.14F;
      float domeBottomR = size * 0.35F;
      float domeTopR = size * 0.06F;
      float domeHeight = size * 0.35F;
      emitFilledDisc(vc, entry, center, -bodyHalfThick, bodyBottomR, 20, darken(hullColor, 0.55F));
      emitCone(vc, entry, center, bodyHalfThick, -bodyHalfThick, bodyTopR, bodyBottomR, 20, hullColor);
      emitFilledDisc(vc, entry, center, bodyHalfThick, bodyTopR, 20, hullColor);
      float lightSize = size * 0.09F;

      for (int i = 0; i < lightCount; i++) {
         float angle = (float)i / lightCount * (float) (Math.PI * 2);
         Vec3 pos = center.add(Math.cos(angle) * bodyBottomR * 1.03F, 0.0, Math.sin(angle) * bodyBottomR * 1.03F);
         emitBillboard(vc, entry, pos, lightSize / 2.0F, lightColor, camPos);
      }

      float domeBottomY = bodyHalfThick + 0.01F;
      emitCone(vc, entry, center, domeBottomY + domeHeight, domeBottomY, domeTopR, domeBottomR, 16, domeColor);
      emitFilledDisc(vc, entry, center, domeBottomY + domeHeight, domeTopR, 16, domeColor);
   }

   private static int darken(int argb, float factor) {
      int alpha = argb >>> 24 & 0xFF;
      int r = Math.round((argb >> 16 & 0xFF) * factor);
      int g = Math.round((argb >> 8 & 0xFF) * factor);
      int b = Math.round((argb & 0xFF) * factor);
      return alpha << 24 | r << 16 | g << 8 | b;
   }

   private boolean renderAscension(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      int ringCount = Math.max(1, this.ascensionRingCount.getInt());
      float riseHeight = (float)this.ascensionRiseHeight.get().doubleValue() * effect.scaleAtSpawn;
      float riseSpeed = (float)this.ascensionRiseSpeed.get().doubleValue() * effect.scaleAtSpawn;
      float spacing = (float)this.ascensionRingSpacing.get().doubleValue();
      float radius = (float)this.ascensionRingRadius.get().doubleValue() * effect.scaleAtSpawn;
      float thickness = (float)this.ascensionRingThickness.get().doubleValue() * effect.scaleAtSpawn;
      float riseDuration = riseHeight / Math.max(0.01F, riseSpeed);
      float total = (ringCount - 1) * spacing + riseDuration;
      if (elapsed >= total) {
         return true;
      } else {
         int ringColor = this.ascensionColor.get();

         for (int i = 0; i < ringCount; i++) {
            float localT = elapsed - i * spacing;
            if (!(localT < 0.0F) && !(localT > riseDuration)) {
               float progress = localT / riseDuration;
               float y = riseHeight * progress;
               float alphaMul = progress < 0.1F ? progress / 0.1F : (progress > 0.8F ? Math.max(0.0F, 1.0F - (progress - 0.8F) / 0.2F) : 1.0F);
               emitTorus(vc, entry, effect.origin.add(0.0, y, 0.0), radius, thickness / 2.0F, 28, 6, withAlphaMul(ringColor, alphaMul));
            }
         }

         float p = elapsed / total;
         float beamEnvelope = p < 0.1F ? p / 0.1F : (p > 0.85F ? Math.max(0.0F, 1.0F - (p - 0.85F) / 0.15F) : 1.0F);
         if (beamEnvelope > 0.01F) {
            float beamWidth = (float)this.ascensionBeamWidth.get().doubleValue() * effect.scaleAtSpawn;
            int outerCol = withAlphaMul(this.ascensionBeamColor.get(), beamEnvelope * 0.6F);
            emitCone(vc, entry, effect.origin, riseHeight, 0.0F, beamWidth * 0.6F, beamWidth, 20, outerCol);
            emitFilledDisc(vc, entry, effect.origin, 0.02F, beamWidth, 20, outerCol);
            int coreCol = withAlphaMul(this.ascensionBeamColor.get() | 0xFF000000, beamEnvelope * 0.85F);
            emitCone(vc, entry, effect.origin, riseHeight, 0.03F, beamWidth * 0.25F, beamWidth * 0.4F, 16, coreCol);
            int strutCol = withAlphaMul(ringColor, beamEnvelope);
            int strutCount = 5;

            for (int ix = 0; ix < strutCount; ix++) {
               float angle = (float)ix / strutCount * (float) (Math.PI * 2);
               Vec3 top = effect.origin.add(Math.cos(angle) * beamWidth * 0.6F, riseHeight, Math.sin(angle) * beamWidth * 0.6F);
               Vec3 bot = effect.origin.add(Math.cos(angle) * beamWidth, 0.04, Math.sin(angle) * beamWidth);
               renderStroke(vc, entry, top, bot, beamWidth * 0.04F, strutCol, strutCol, camPos);
            }

            float cloudSize = (float)this.ascensionCloudSize.get().doubleValue() * effect.scaleAtSpawn;
            int cloudColor = withAlphaMul(this.ascensionCloudColor.get(), beamEnvelope);
            renderCloud(vc, entry, effect.origin.add(0.0, riseHeight, 0.0), cloudSize, cloudColor);
         }

         return false;
      }
   }

   private static void renderCloud(VertexConsumer vc, Pose entry, Vec3 center, float size, int color) {
      float[] offX = new float[]{0.0F, 0.55F, -0.55F, 0.25F, -0.3F, 0.15F, -0.2F, 0.0F};
      float[] offZ = new float[]{0.0F, 0.15F, -0.15F, -0.45F, 0.4F, -0.1F, 0.25F, 0.0F};
      float[] offY = new float[]{0.0F, -0.15F, -0.15F, -0.25F, -0.25F, 0.25F, 0.2F, -0.4F};
      float[] radiusMul = new float[]{1.0F, 0.75F, 0.75F, 0.6F, 0.6F, 0.6F, 0.55F, 0.5F};

      for (int i = 0; i < offX.length; i++) {
         Vec3 puffCenter = center.add(offX[i] * size, offY[i] * size, offZ[i] * size);
         renderPuff(vc, entry, puffCenter, size * radiusMul[i] * 0.55F, color);
      }
   }

   private static void renderPuff(VertexConsumer vc, Pose entry, Vec3 center, float radius, int color) {
      float thickness = radius * 0.85F;
      float capR = radius * 0.55F;
      int segments = 10;
      emitFilledDisc(vc, entry, center, -thickness / 2.0F, capR, segments, color);
      emitCone(vc, entry, center, 0.0F, -thickness / 2.0F, radius, capR, segments, color);
      emitCone(vc, entry, center, thickness / 2.0F, 0.0F, capR, radius, segments, color);
      emitFilledDisc(vc, entry, center, thickness / 2.0F, capR, segments, color);
   }

   private static void emitTorus(VertexConsumer vc, Pose entry, Vec3 center, float ringRadius, float tubeRadius, int ringSegments, int tubeSegments, int color) {
      List<Vec3> loop = new ArrayList<>(ringSegments + 1);

      for (int i = 0; i <= ringSegments; i++) {
         float a = (float)i / ringSegments * (float) (Math.PI * 2);
         loop.add(new Vec3(Math.cos(a) * ringRadius, 0.0, Math.sin(a) * ringRadius));
      }

      renderTube(vc, entry, center, loop, tubeRadius, tubeSegments, color);
   }

   private boolean renderRings(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed) {
      int count = Math.max(1, this.ringsCount.getInt());
      float startHeight = (float)this.ringsStartHeight.get().doubleValue() * effect.scaleAtSpawn;
      float fallSpeed = (float)this.ringsFallSpeed.get().doubleValue() * effect.scaleAtSpawn;
      float stagger = (float)this.ringsStagger.get().doubleValue();
      float fallDuration = startHeight / Math.max(0.01F, fallSpeed);
      float total = (count - 1) * stagger + fallDuration;
      if (elapsed >= total) {
         return true;
      } else {
         float startRadius = (float)this.ringsStartRadius.get().doubleValue() * effect.scaleAtSpawn;
         float targetRadius = (float)this.ringsTargetRadius.get().doubleValue() * effect.scaleAtSpawn;
         float thickness = (float)this.ringsThickness.get().doubleValue() * effect.scaleAtSpawn;
         int ringColor = this.ringsColor.get();

         for (int i = 0; i < count; i++) {
            float localT = elapsed - i * stagger;
            if (!(localT < 0.0F) && !(localT > fallDuration)) {
               float progress = localT / fallDuration;
               float y = startHeight * (1.0F - progress);
               float growT = Math.min(1.0F, progress / 0.92F);
               float growEase = 1.0F - (1.0F - growT) * (1.0F - growT);
               float radius = startRadius + (targetRadius - startRadius) * growEase;
               float alphaMul = progress > 0.94F ? Math.max(0.0F, 1.0F - (progress - 0.94F) / 0.06F) : 1.0F;
               Vec3 ringCenter = effect.origin.add(0.0, y, 0.0);
               emitTorus(vc, entry, ringCenter, radius, thickness * 1.6F, 28, 6, withAlphaMul(ringColor, alphaMul * 0.3F));
               emitTorus(vc, entry, ringCenter, radius, thickness / 2.0F, 28, 6, withAlphaMul(ringColor, alphaMul));
            }
         }

         return false;
      }
   }

   private boolean renderFirework(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float launchSpeed = (float)this.fireworkLaunchSpeed.get().doubleValue() * effect.scaleAtSpawn;
      float shellSize = (float)this.fireworkShellSize.get().doubleValue() * effect.scaleAtSpawn;
      float burstDuration = (float)this.fireworkBurstDuration.get().doubleValue();
      float burstSpeed = (float)this.fireworkBurstSpeed.get().doubleValue() * effect.scaleAtSpawn;
      float burstGravity = (float)this.fireworkBurstGravity.get().doubleValue();
      float burstShardSize = (float)this.fireworkBurstSize.get().doubleValue() * effect.scaleAtSpawn;
      int n = effect.fwShellDelay.length;
      boolean anyAlive = false;

      for (int i = 0; i < n; i++) {
         float delay = effect.fwShellDelay[i];
         float local = elapsed - delay;
         float targetHeight = effect.fwShellHeight[i];
         float riseTime = targetHeight / Math.max(0.01F, launchSpeed);
         if (local < 0.0F) {
            anyAlive = true;
         } else if (local < riseTime) {
            anyAlive = true;
            float driftFrac = Math.min(1.0F, local / riseTime);
            Vec3 horiz = effect.fwShellDrift[i].scale(driftFrac);
            Vec3 pos = effect.origin.add(horiz.x, launchSpeed * local, horiz.z);
            emitBillboard(vc, entry, pos, shellSize / 2.0F, effect.fwShellColor[i], camPos);
         } else {
            float burstT = local - riseTime;
            if (!(burstT >= burstDuration)) {
               anyAlive = true;
               Vec3 burstOrigin = effect.origin.add(effect.fwShellDrift[i].x, targetHeight, effect.fwShellDrift[i].z);
               float alphaMul = Math.max(0.0F, 1.0F - burstT / burstDuration);
               int color = withAlphaMul(effect.fwShellColor[i], alphaMul);
               int shardN = effect.fwBurstDir.length;

               for (int j = 0; j < shardN; j++) {
                  float s = burstSpeed * effect.fwBurstSpeedMul[j];
                  Vec3 dir = effect.fwBurstDir[j];
                  float dx = (float)(dir.x * s * burstT);
                  float dz = (float)(dir.z * s * burstT);
                  float dy = (float)(dir.y * s * burstT) - 0.5F * burstGravity * burstT * burstT;
                  emitBillboard(vc, entry, burstOrigin.add(dx, dy, dz), burstShardSize / 2.0F, color, camPos);
               }
            }
         }
      }

      return !anyAlive;
   }

   private boolean renderTombstone(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float rise = (float)this.tombstoneRiseDuration.get().doubleValue();
      float hold = (float)this.tombstoneHoldDuration.get().doubleValue();
      float fade = (float)this.tombstoneFadeDuration.get().doubleValue();
      float total = rise + hold + fade;
      if (elapsed >= total) {
         return true;
      } else {
         float width = (float)this.tombstoneWidth.get().doubleValue() * effect.scaleAtSpawn;
         float depth = (float)this.tombstoneDepth.get().doubleValue() * effect.scaleAtSpawn;
         float bodyHeight = (float)this.tombstoneBodyHeight.get().doubleValue() * effect.scaleAtSpawn;
         float capHeight = (float)this.tombstoneCapHeight.get().doubleValue() * effect.scaleAtSpawn;
         float plinthHeight = bodyHeight * 0.16F;
         float fullHeight = bodyHeight + capHeight + plinthHeight;
         float riseY;
         float alphaMul;
         if (elapsed < rise) {
            float p = elapsed / rise;
            float ease = 1.0F - (1.0F - p) * (1.0F - p);
            riseY = -fullHeight * (1.0F - ease);
            alphaMul = 1.0F;
         } else if (elapsed < rise + hold) {
            riseY = 0.0F;
            alphaMul = 1.0F;
         } else {
            riseY = 0.0F;
            alphaMul = Math.max(0.0F, 1.0F - (elapsed - rise - hold) / fade);
         }

         Vec3 base = effect.origin.add(0.0, riseY, 0.0);
         Vec3 right = new Vec3(Math.cos(effect.tombstoneYaw), 0.0, Math.sin(effect.tombstoneYaw));
         Vec3 forward = new Vec3(-Math.sin(effect.tombstoneYaw), 0.0, Math.cos(effect.tombstoneYaw));
         int stoneColor = withAlphaMul(this.tombstoneColor.get(), alphaMul);
         float plinthWidth = width * 1.2F;
         float plinthDepth = depth * 1.7F;
         int plinthColor = darken(stoneColor, 0.6F);
         renderPlinth(vc, entry, base, right, forward, plinthWidth, plinthDepth, plinthHeight, plinthColor);
         renderTombstoneSlab(vc, entry, base.add(0.0, plinthHeight, 0.0), right, forward, width, depth, bodyHeight, capHeight, stoneColor);
         int dirtColor = withAlphaMul(this.tombstoneDirtColor.get(), alphaMul);
         Vec3 moundCenter = effect.origin.add(forward.scale(depth * 0.5 + width * 0.35));
         renderDirtMound(vc, entry, moundCenter, right, forward, width * 0.9F, dirtColor, camPos);
         float dustT = elapsed - rise;
         float dustDuration = 0.4F;
         if (dustT >= 0.0F && dustT < dustDuration && effect.tombstoneDustDir.length > 0) {
            float dustAlphaMul = alphaMul * Math.max(0.0F, 1.0F - dustT / dustDuration);
            int dustColor = withAlphaMul(this.tombstoneDirtColor.get() | 0xFF000000, dustAlphaMul);
            float dustSpeed = 2.5F * effect.scaleAtSpawn;
            float dustGravity = 6.0F;
            float dustSize = 0.1F * effect.scaleAtSpawn;
            int n = effect.tombstoneDustDir.length;

            for (int i = 0; i < n; i++) {
               float s = dustSpeed * effect.tombstoneDustSpeedMul[i];
               Vec3 dir = effect.tombstoneDustDir[i];
               float dx = (float)(dir.x * s * dustT);
               float dz = (float)(dir.z * s * dustT);
               float dy = (float)(dir.y * s * dustT) - 0.5F * dustGravity * dustT * dustT;
               emitBillboard(vc, entry, effect.origin.add(dx, Math.max(0.0F, dy), dz), dustSize / 2.0F, dustColor, camPos);
            }
         }

         return false;
      }
   }

   private static void renderTombstoneSlab(
      VertexConsumer vc, Pose entry, Vec3 base, Vec3 right, Vec3 forward, float width, float depth, float bodyHeight, float capHeight, int color
   ) {
      float hw = width / 2.0F;
      float hd = depth / 2.0F;
      Vec3 blf = slabCorner(base, right, forward, -hw, 0.0F, hd);
      Vec3 brf = slabCorner(base, right, forward, hw, 0.0F, hd);
      Vec3 blb = slabCorner(base, right, forward, -hw, 0.0F, -hd);
      Vec3 brb = slabCorner(base, right, forward, hw, 0.0F, -hd);
      Vec3 tlf = slabCorner(base, right, forward, -hw, bodyHeight, hd);
      Vec3 trf = slabCorner(base, right, forward, hw, bodyHeight, hd);
      Vec3 tlb = slabCorner(base, right, forward, -hw, bodyHeight, -hd);
      Vec3 trb = slabCorner(base, right, forward, hw, bodyHeight, -hd);
      Vec3 ridgeF = slabCorner(base, right, forward, 0.0F, bodyHeight + capHeight, hd);
      Vec3 ridgeB = slabCorner(base, right, forward, 0.0F, bodyHeight + capHeight, -hd);
      int backColor = darken(color, 0.55F);
      int sideColor = darken(color, 0.75F);
      int roofColor = darken(color, 0.88F);
      int gableColor = darken(color, 0.7F);
      emitQuad(vc, entry, tlf, trf, brf, blf, color, color, color, color);
      emitQuad(vc, entry, trb, tlb, blb, brb, backColor, backColor, backColor, backColor);
      emitQuad(vc, entry, tlb, tlf, blf, blb, sideColor, sideColor, sideColor, sideColor);
      emitQuad(vc, entry, trf, trb, brb, brf, sideColor, sideColor, sideColor, sideColor);
      emitQuad(vc, entry, ridgeF, ridgeB, tlb, tlf, roofColor, roofColor, roofColor, roofColor);
      emitQuad(vc, entry, trf, trb, ridgeB, ridgeF, roofColor, roofColor, roofColor, roofColor);
      emitQuad(vc, entry, tlf, trf, ridgeF, ridgeF, gableColor, gableColor, gableColor, gableColor);
      emitQuad(vc, entry, trb, tlb, ridgeB, ridgeB, gableColor, gableColor, gableColor, gableColor);
      float plaqueHalfW = hw * 0.62F;
      float plaqueBottom = bodyHeight * 0.14F;
      float plaqueTop = bodyHeight * 0.82F;
      float plaqueDepth = hd + Math.max(0.006F, depth * 0.04F);
      int plaqueColor = darken(color, 0.4F);
      Vec3 plTl = slabCorner(base, right, forward, -plaqueHalfW, plaqueTop, plaqueDepth);
      Vec3 plTr = slabCorner(base, right, forward, plaqueHalfW, plaqueTop, plaqueDepth);
      Vec3 plBr = slabCorner(base, right, forward, plaqueHalfW, plaqueBottom, plaqueDepth);
      Vec3 plBl = slabCorner(base, right, forward, -plaqueHalfW, plaqueBottom, plaqueDepth);
      emitQuad(vc, entry, plTl, plTr, plBr, plBl, plaqueColor, plaqueColor, plaqueColor, plaqueColor);
      int lineColor = darken(color, 0.25F);
      float lineDepth = plaqueDepth + Math.max(0.004F, depth * 0.02F);
      float lineHalfW = plaqueHalfW * 0.72F;

      for (int i = 0; i < 3; i++) {
         float ly = plaqueTop - (plaqueTop - plaqueBottom) * (0.22F + i * 0.26F);
         float lh = (plaqueTop - plaqueBottom) * 0.05F;
         Vec3 lTl = slabCorner(base, right, forward, -lineHalfW, ly + lh, lineDepth);
         Vec3 lTr = slabCorner(base, right, forward, lineHalfW, ly + lh, lineDepth);
         Vec3 lBr = slabCorner(base, right, forward, lineHalfW, ly - lh, lineDepth);
         Vec3 lBl = slabCorner(base, right, forward, -lineHalfW, ly - lh, lineDepth);
         emitQuad(vc, entry, lTl, lTr, lBr, lBl, lineColor, lineColor, lineColor, lineColor);
      }
   }

   private static void renderPlinth(VertexConsumer vc, Pose entry, Vec3 base, Vec3 right, Vec3 forward, float width, float depth, float height, int color) {
      float hw = width / 2.0F;
      float hd = depth / 2.0F;
      Vec3 blf = slabCorner(base, right, forward, -hw, 0.0F, hd);
      Vec3 brf = slabCorner(base, right, forward, hw, 0.0F, hd);
      Vec3 blb = slabCorner(base, right, forward, -hw, 0.0F, -hd);
      Vec3 brb = slabCorner(base, right, forward, hw, 0.0F, -hd);
      Vec3 tlf = slabCorner(base, right, forward, -hw, height, hd);
      Vec3 trf = slabCorner(base, right, forward, hw, height, hd);
      Vec3 tlb = slabCorner(base, right, forward, -hw, height, -hd);
      Vec3 trb = slabCorner(base, right, forward, hw, height, -hd);
      emitQuad(vc, entry, tlf, trf, brf, blf, color, color, color, color);
      emitQuad(vc, entry, trb, tlb, blb, brb, color, color, color, color);
      emitQuad(vc, entry, tlb, tlf, blf, blb, color, color, color, color);
      emitQuad(vc, entry, trf, trb, brb, brf, color, color, color, color);
      emitQuad(vc, entry, tlf, trf, trb, tlb, color, color, color, color);
   }

   private static Vec3 slabCorner(Vec3 base, Vec3 right, Vec3 forward, float x, float y, float z) {
      return base.add(right.scale(x)).add(0.0, y, 0.0).add(forward.scale(z));
   }

   private static void renderDirtMound(VertexConsumer vc, Pose entry, Vec3 center, Vec3 right, Vec3 forward, float size, int baseColor, Vec3 camPos) {
      float[] offX = new float[]{0.0F, 0.5F, -0.45F, 0.2F, -0.15F, 0.4F};
      float[] offZ = new float[]{0.0F, 0.2F, 0.15F, -0.35F, -0.2F, -0.05F};
      float[] radiusMul = new float[]{1.0F, 0.62F, 0.58F, 0.5F, 0.45F, 0.4F};
      float[] shadeMul = new float[]{1.0F, 0.8F, 0.95F, 0.85F, 0.9F, 0.75F};

      for (int i = 0; i < offX.length; i++) {
         Vec3 clumpCenter = center.add(right.scale(offX[i] * size)).add(forward.scale(offZ[i] * size));
         int clumpColor = darken(baseColor, shadeMul[i]);
         renderPuff(vc, entry, clumpCenter, size * radiusMul[i] * 0.5F, clumpColor);
      }

      float[] pebbleX = new float[]{0.35F, -0.4F, 0.1F, -0.15F, 0.5F};
      float[] pebbleZ = new float[]{-0.1F, 0.25F, 0.4F, -0.3F, 0.05F};
      int pebbleColor = darken(baseColor, 0.5F);

      for (int i = 0; i < pebbleX.length; i++) {
         Vec3 pebblePos = center.add(right.scale(pebbleX[i] * size)).add(forward.scale(pebbleZ[i] * size)).add(0.0, size * 0.08, 0.0);
         emitBillboard(vc, entry, pebblePos, size * 0.06F, pebbleColor, camPos);
      }
   }

   private boolean renderSplash(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float elapsed, Vec3 camPos) {
      float fallDuration = (float)this.splashFallDuration.get().doubleValue();
      float dropletDuration = (float)this.splashDropletDuration.get().doubleValue();
      float ringDuration = (float)this.splashRingDuration.get().doubleValue();
      float total = fallDuration + Math.max(dropletDuration, ringDuration);
      if (elapsed >= total) {
         return true;
      } else {
         if (elapsed < fallDuration && effect.splashStrands != null) {
            float columnHeight = effect.splashColumnHeightAtSpawn;
            float columnWidth = (float)this.splashColumnWidth.get().doubleValue() * effect.scaleAtSpawn * 0.6F;
            float t = elapsed / fallDuration;
            float leadY = columnHeight * (1.0F - t * t);
            float streamLen = columnHeight * 0.35F;
            float topY = Math.min(columnHeight, leadY + streamLen);
            if (topY > leadY) {
               int outerColor = darken(this.splashColor.get(), 0.55F);
               int coreColor = this.splashColor.get();

               for (List<Vec3> strand : effect.splashStrands) {
                  List<Vec3> visible = clipPathByHeight(strand, leadY, topY);
                  if (visible.size() >= 2) {
                     renderTube(vc, entry, effect.origin, visible, columnWidth, 6, outerColor);
                     renderTube(vc, entry, effect.origin, visible, columnWidth * 0.5F, 6, coreColor);
                     this.renderSplashBeads(vc, entry, effect, strand, leadY, topY, camPos);
                  }
               }
            }
         }

         float splashT = elapsed - fallDuration;
         if (splashT >= 0.0F) {
            if (splashT < dropletDuration) {
               this.renderSplashDroplets(vc, entry, effect, splashT, camPos);
            }

            if (splashT < ringDuration) {
               this.renderSplashRing(vc, entry, effect, splashT);
            }
         }

         return false;
      }
   }

   private void renderSplashDroplets(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float t, Vec3 camPos) {
      if (effect.splashDropletDir != null && effect.splashDropletDir.length != 0) {
         float duration = (float)this.splashDropletDuration.get().doubleValue();
         float speed = (float)this.splashDropletSpeed.get().doubleValue() * effect.scaleAtSpawn;
         float gravity = (float)this.splashDropletGravity.get().doubleValue();
         float size = (float)this.splashDropletSize.get().doubleValue() * effect.scaleAtSpawn;
         float alphaMul = Math.max(0.0F, 1.0F - t / duration);
         int color = withAlphaMul(this.splashColor.get() | 0xFF000000, alphaMul);
         int n = effect.splashDropletDir.length;

         for (int i = 0; i < n; i++) {
            float s = speed * effect.splashDropletSpeedMul[i];
            Vec3 dir = effect.splashDropletDir[i];
            float dx = (float)(dir.x * s * t);
            float dz = (float)(dir.z * s * t);
            float dy = (float)(dir.y * s * t) - 0.5F * gravity * t * t;
            emitBillboard(vc, entry, effect.origin.add(dx, Math.max(0.0F, dy), dz), size / 2.0F, color, camPos);
         }
      }
   }

   private void renderSplashBeads(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, List<Vec3> strand, float leadY, float topY, Vec3 camPos) {
      float beadSize = (float)this.splashColumnWidth.get().doubleValue() * effect.scaleAtSpawn * 0.85F;
      int color = withAlphaMul(this.splashColor.get() | 0xFF000000, 0.9F);

      for (Vec3 p : strand) {
         if (p.y >= leadY && p.y <= topY) {
            emitBillboard(vc, entry, effect.origin.add(p), beadSize / 2.0F, color, camPos);
         }
      }
   }

   private void renderSplashRing(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float t) {
      float duration = (float)this.splashRingDuration.get().doubleValue();
      float maxRadius = (float)this.splashRingRadius.get().doubleValue() * effect.scaleAtSpawn;
      float p = t / duration;
      float ease = 1.0F - (1.0F - p) * (1.0F - p);
      float radius = maxRadius * ease;
      if (!(radius < 0.02F)) {
         float alphaMul = Math.max(0.0F, 1.0F - p);
         int color = withAlphaMul(this.splashGlowColor.get() | 0xFF000000, alphaMul * 0.8F);
         float tubeRadius = Math.max(0.02F, radius * 0.06F);
         emitTorus(vc, entry, effect.origin.add(0.0, 0.02, 0.0), radius, tubeRadius, 24, 5, color);
      }
   }

   private static List<Vec3> clipPathByHeight(List<Vec3> path, float lowY, float highY) {
      List<Vec3> out = new ArrayList<>();

      for (int i = 0; i < path.size() - 1; i++) {
         Vec3 a = path.get(i);
         Vec3 b = path.get(i + 1);
         double ay = a.y;
         double by = b.y;
         if (!(by < lowY) && !(ay > highY)) {
            double segLow = Math.max(ay, (double)lowY);
            double segHigh = Math.min(by, (double)highY);
            if (!(segHigh <= segLow)) {
               double span = by - ay;
               double fLow = span > 1.0E-6 ? (segLow - ay) / span : 0.0;
               double fHigh = span > 1.0E-6 ? (segHigh - ay) / span : 1.0;
               Vec3 pLow = a.add(b.subtract(a).scale(fLow));
               Vec3 pHigh = a.add(b.subtract(a).scale(fHigh));
               if (out.isEmpty() || out.get(out.size() - 1).distanceTo(pLow) > 1.0E-6) {
                  out.add(pLow);
               }

               out.add(pHigh);
            }
         }
      }

      return out;
   }

   private static void emitCone(VertexConsumer vc, Pose entry, Vec3 center, float topY, float botY, float topR, float botR, int segments, int color) {
      for (int i = 0; i < segments; i++) {
         float a0 = (float)i / segments * (float) (Math.PI * 2);
         float a1 = (float)(i + 1) / segments * (float) (Math.PI * 2);
         Vec3 t0 = center.add(Math.cos(a0) * topR, topY, Math.sin(a0) * topR);
         Vec3 t1 = center.add(Math.cos(a1) * topR, topY, Math.sin(a1) * topR);
         Vec3 b0 = center.add(Math.cos(a0) * botR, botY, Math.sin(a0) * botR);
         Vec3 b1 = center.add(Math.cos(a1) * botR, botY, Math.sin(a1) * botR);
         emitQuad(vc, entry, t0, t1, b1, b0, color, color, color, color);
      }
   }

   private static void emitFilledDisc(VertexConsumer vc, Pose entry, Vec3 center, float y, float radius, int segments, int color) {
      Vec3 mid = center.add(0.0, y, 0.0);

      for (int i = 0; i < segments; i++) {
         float a0 = (float)i / segments * (float) (Math.PI * 2);
         float a1 = (float)(i + 1) / segments * (float) (Math.PI * 2);
         Vec3 e0 = center.add(Math.cos(a0) * radius, y, Math.sin(a0) * radius);
         Vec3 e1 = center.add(Math.cos(a1) * radius, y, Math.sin(a1) * radius);
         emitQuad(vc, entry, mid, mid, e0, e1, color, color, color, color);
      }
   }

   private void renderDebrisBurst(VertexConsumer vc, Pose entry, KillEffectsModule.ActiveEffect effect, float t, Vec3 camPos) {
      float duration = (float)this.shardDuration.get().doubleValue();
      if (!(t < 0.0F) && !(t >= duration) && effect.shardDir != null) {
         float speed = (float)this.shardSpeed.get().doubleValue() * effect.scaleAtSpawn;
         float gravity = (float)this.shardGravity.get().doubleValue();
         float size = (float)this.shardSize.get().doubleValue() * effect.scaleAtSpawn;
         int baseColor = this.shardColor.get();
         float alphaMul = Math.max(0.0F, 1.0F - t / duration);
         int color = withAlphaMul(baseColor, alphaMul);
         int n = effect.shardDir.length;

         for (int i = 0; i < n; i++) {
            float s = speed * effect.shardSpeedMul[i];
            Vec3 dir = effect.shardDir[i];
            float horizX = (float)(dir.x * s * t);
            float horizZ = (float)(dir.z * s * t);
            float vertLaunch = (float)(dir.y * s);
            float height = Math.max(0.0F, vertLaunch * t - 0.5F * gravity * t * t);
            Vec3 pos = effect.origin.add(horizX, height, horizZ);
            emitBillboard(vc, entry, pos, size / 2.0F, color, camPos);
         }
      }
   }

   private static int withAlphaMul(int argb, float mul) {
      int baseAlpha = argb >>> 24 & 0xFF;
      int alpha = Math.max(0, Math.min(255, Math.round(baseAlpha * mul)));
      return alpha << 24 | argb & 16777215;
   }

   private static void renderStroke(VertexConsumer vc, Pose entry, Vec3 a, Vec3 b, float thickness, int colorA, int colorB, Vec3 camPos) {
      Vec3 dir = safeNormalize(b.subtract(a), new Vec3(1.0, 0.0, 0.0));
      Vec3 mid = a.add(b).scale(0.5);
      Vec3 camDir = safeNormalize(camPos.subtract(mid), new Vec3(0.0, 1.0, 0.0));
      Vec3 perp = dir.cross(camDir);
      if (perp.lengthSqr() < 1.0E-6) {
         perp = dir.cross(new Vec3(0.0, 1.0, 0.0));
      }

      perp = safeNormalize(perp, new Vec3(1.0, 0.0, 0.0)).scale(thickness / 2.0);
      Vec3 p1 = a.add(perp);
      Vec3 p2 = a.subtract(perp);
      Vec3 p3 = b.subtract(perp);
      Vec3 p4 = b.add(perp);
      emitQuad(vc, entry, p1, p2, p3, p4, colorA, colorA, colorB, colorB);
   }

   private static void emitBillboard(VertexConsumer vc, Pose entry, Vec3 center, float half, int color, Vec3 camPos) {
      Vec3 normal = safeNormalize(camPos.subtract(center), new Vec3(0.0, 1.0, 0.0));
      Vec3 up = Math.abs(normal.y) > 0.99 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
      Vec3 right = safeNormalize(normal.cross(up), new Vec3(1.0, 0.0, 0.0));
      Vec3 billboardUp = safeNormalize(right.cross(normal), new Vec3(0.0, 1.0, 0.0));
      Vec3 p1 = center.add(right.scale(-half)).add(billboardUp.scale(-half));
      Vec3 p2 = center.add(right.scale(half)).add(billboardUp.scale(-half));
      Vec3 p3 = center.add(right.scale(half)).add(billboardUp.scale(half));
      Vec3 p4 = center.add(right.scale(-half)).add(billboardUp.scale(half));
      emitQuad(vc, entry, p1, p2, p3, p4, color, color, color, color);
   }

   private static void emitQuad(VertexConsumer vc, Pose entry, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, int c1, int c2, int c3, int c4) {
      vc.addVertex(entry, (float)p1.x, (float)p1.y, (float)p1.z).setColor(c1);
      vc.addVertex(entry, (float)p2.x, (float)p2.y, (float)p2.z).setColor(c2);
      vc.addVertex(entry, (float)p3.x, (float)p3.y, (float)p3.z).setColor(c3);
      vc.addVertex(entry, (float)p4.x, (float)p4.y, (float)p4.z).setColor(c4);
   }

   private static Vec3 safeNormalize(Vec3 v, Vec3 fallback) {
      return v.lengthSqr() < 1.0E-8 ? fallback : v.normalize();
   }

   private static class ActiveEffect {
      KillEffectsModule.EffectType type;
      Vec3 origin;
      long startMillis;
      float scaleAtSpawn;
      KillEffectsModule.BoltStyle boltStyleAtSpawn;
      List<Vec3> boltPoints;
      List<Vec3> boltBlockPositions;
      Vec3[] impactDir;
      float[] impactTargetDist;
      Vec3[] shardDir;
      float[] shardSpeedMul;
      float[] vortexBaseAngle;
      float[] vortexRadiusMul;
      float[] vortexHeightMul;
      List<Vec3> spiralPoints;
      float spiralDrawDurationAtSpawn;
      float[] ufoOrbAngleOffset;
      Vec3[] fwShellDrift;
      float[] fwShellDelay;
      float[] fwShellHeight;
      int[] fwShellColor;
      Vec3[] fwBurstDir;
      float[] fwBurstSpeedMul;
      float tombstoneYaw;
      Vec3[] tombstoneDustDir;
      float[] tombstoneDustSpeedMul;
      Vec3[] splashDropletDir;
      float[] splashDropletSpeedMul;
      List<List<Vec3>> splashStrands;
      float splashColumnHeightAtSpawn;
   }

   public static enum BoltStyle {
      SMOOTH,
      BLOCKY;
   }

   public static enum EffectStyle {
      LIGHTNING,
      SHARD,
      VORTEX,
      SPIRAL,
      UFO,
      ASCENSION,
      RINGS,
      FIREWORK,
      TOMBSTONE,
      SPLASH,
      RANDOM;
   }

   private static enum EffectType {
      LIGHTNING,
      SHARD,
      VORTEX,
      SPIRAL,
      UFO,
      ASCENSION,
      RINGS,
      FIREWORK,
      TOMBSTONE,
      SPLASH;
   }
}
