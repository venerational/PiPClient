package dev.lyfw.lyfwclient.mixin;

import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net/minecraft/client/renderer/rendertype/RenderSetup$TextureBinding"}
)
public interface RenderSetupTextureSpecAccessor {
   @Accessor("location")
   Identifier lyfwclient$getLocation();
}
