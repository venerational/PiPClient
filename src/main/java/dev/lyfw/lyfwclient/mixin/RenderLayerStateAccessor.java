package dev.lyfw.lyfwclient.mixin;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({RenderType.class})
public interface RenderLayerStateAccessor {
   @Accessor("state")
   RenderSetup lyfwclient$getRenderSetup();
}
