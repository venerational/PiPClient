package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.lyfw.lyfwclient.module.modules.SnowModule;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({WeatherEffectRenderer.class})
public class SnowMixin {
   @ModifyReturnValue(
      method = {"getPrecipitationAt"},
      at = {@At("RETURN")}
   )
   private Precipitation lyfwclient$alwaysSnow(Precipitation original) {
      SnowModule snow = SnowModule.get();
      return snow != null && snow.everywhere() && original != Precipitation.NONE ? Precipitation.SNOW : original;
   }

   @ModifyExpressionValue(
      method = {"extractRenderState"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getRainLevel(F)F"
      )}
   )
   private float lyfwclient$forceWeather(float original) {
      SnowModule snow = SnowModule.get();
      return snow == null ? original : Math.max(original, snow.forcedIntensity());
   }

   @ModifyExpressionValue(
      method = {"extractRenderState"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"
      )}
   )
   private int lyfwclient$ignoreRoof(int original, Level world, int ticks, float tickProgress, Vec3 cameraPos, WeatherRenderState state) {
      SnowModule snow = SnowModule.get();
      return snow != null && snow.underground() ? world.getMinY() : original;
   }
}
