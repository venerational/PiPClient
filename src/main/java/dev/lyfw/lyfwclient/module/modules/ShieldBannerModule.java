package dev.lyfw.lyfwclient.module.modules;

import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.EnumSetting;
import dev.lyfw.lyfwclient.setting.TextSetting;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Builder;

public class ShieldBannerModule extends Module {
   private final EnumSetting<ShieldBannerModule.Design> design = this.register(new EnumSetting<>("Design", ShieldBannerModule.Design.CREEPER));
   private final TextSetting command = this.register(new TextSetting("Custom Command", ""));
   private final EnumSetting<DyeColor> baseColor = this.register(new EnumSetting("Base Color", DyeColor.WHITE));
   private final EnumSetting<DyeColor> patternColor = this.register(new EnumSetting("Pattern Color", DyeColor.BLACK));
   private final BooleanSetting replaceDecorated = this.register(new BooleanSetting("Replace Existing Banners", true));
   private DataComponentMap cached;
   private ShieldBannerModule.Design cachedDesign;
   private DyeColor cachedBase;
   private DyeColor cachedPattern;
   private String cachedCommand;

   public ShieldBannerModule() {
      super(
         "Shield Banner",
         "Draws your chosen banner on every shield you can see, including other players. Pick a Design, or set Design to CUSTOM and paste a shield /give command (1.21.1 and older formats both work). Client-side only.",
         Category.RENDER,
         false
      );
      this.design.group = "Design";
      this.command.group = "Design";
      this.baseColor.group = "Design";
      this.patternColor.group = "Design";
      this.replaceDecorated.group = "Design";
   }

   public static DataComponentMap override(DataComponentMap original) {
      if (!(ModuleManager.get("Shield Banner") instanceof ShieldBannerModule module && module.isEnabled())) {
         return null;
      } else {
         return !module.replaceDecorated.get() && isDecorated(original) ? null : module.components();
      }
   }

   private static boolean isDecorated(DataComponentMap original) {
      if (original == null) {
         return false;
      } else {
         BannerPatternLayers patterns = (BannerPatternLayers)original.get(DataComponents.BANNER_PATTERNS);
         return patterns != null && !patterns.layers().isEmpty() || original.get(DataComponents.BASE_COLOR) != null;
      }
   }

   private DataComponentMap components() {
      ShieldBannerModule.Design wanted = this.design.get();
      DyeColor base = (DyeColor)this.baseColor.get();
      DyeColor pattern = (DyeColor)this.patternColor.get();
      String raw = this.command.get();
      if (this.cached != null
         && wanted == this.cachedDesign
         && base == this.cachedBase
         && pattern == this.cachedPattern
         && Objects.equals(raw, this.cachedCommand)) {
         return this.cached;
      } else {
         Minecraft client = Minecraft.getInstance();
         if (client.level == null) {
            return null;
         } else {
            Registry<BannerPattern> registry = client.level.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);
            Builder builder = new Builder();
            DyeColor resolvedBase = base;
            if (wanted == ShieldBannerModule.Design.CUSTOM) {
               BannerCommand parsed = BannerCommand.parse(raw);
               if (parsed == null) {
                  return null;
               }

               resolvedBase = parsed.base();

               for (BannerCommand.Layer layer : parsed.layers()) {
                  ResourceKey<BannerPattern> key = ResourceKey.create(Registries.BANNER_PATTERN, layer.pattern());
                  if (registry.getOptional(layer.pattern()).isPresent()) {
                     builder.addIfRegistered(registry, key, layer.color());
                  }
               }
            } else {
               for (ResourceKey<BannerPattern> layerx : wanted.layers) {
                  builder.addIfRegistered(registry, layerx, pattern);
               }
            }

            this.cached = DataComponentMap.builder().set(DataComponents.BANNER_PATTERNS, builder.build()).set(DataComponents.BASE_COLOR, resolvedBase).build();
            this.cachedDesign = wanted;
            this.cachedBase = base;
            this.cachedPattern = pattern;
            this.cachedCommand = raw;
            return this.cached;
         }
      }
   }

   public static enum Design {
      CUSTOM(),
      PLAIN(),
      CREEPER(BannerPatterns.CREEPER),
      SKULL(BannerPatterns.SKULL),
      FLOWER(BannerPatterns.FLOWER),
      MOJANG(BannerPatterns.MOJANG),
      PIGLIN(BannerPatterns.PIGLIN),
      GLOBE(BannerPatterns.GLOBE),
      FLOW(BannerPatterns.FLOW),
      GUSTER(BannerPatterns.GUSTER),
      CROSS(BannerPatterns.STRAIGHT_CROSS),
      SALTIRE(BannerPatterns.CROSS),
      BORDER(BannerPatterns.BORDER),
      CURLY_BORDER(BannerPatterns.CURLY_BORDER),
      GRADIENT(BannerPatterns.GRADIENT),
      BRICKS(BannerPatterns.BRICKS),
      CHEVRON(BannerPatterns.TRIANGLES_BOTTOM),
      STRIPES(BannerPatterns.STRIPE_SMALL),
      RHOMBUS(BannerPatterns.RHOMBUS_MIDDLE),
      CIRCLE(BannerPatterns.CIRCLE_MIDDLE),
      HALF(BannerPatterns.HALF_HORIZONTAL),
      BORDERED_CREEPER(BannerPatterns.CREEPER, BannerPatterns.BORDER),
      BORDERED_SKULL(BannerPatterns.SKULL, BannerPatterns.BORDER);

      final List<ResourceKey<BannerPattern>> layers;

      @SafeVarargs
      private Design(ResourceKey<BannerPattern>... layers) {
         this.layers = List.of(layers);
      }
   }
}
