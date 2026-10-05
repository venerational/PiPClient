package dev.lyfw.lyfwclient.mixin;

import java.util.Map;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({RenderSetup.class})
public interface RenderSetupTextureAccessor {
   @Accessor("textures")
   Map<String, Object> lyfwclient$getTextures();
}
