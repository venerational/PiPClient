package dev.lyfw.lyfwclient.mixin;

import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.ShaderManager.CompilationCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ShaderManager.class})
public interface ShaderLoaderAccessor {
   @Accessor("compilationCache")
   CompilationCache lyfwclient$getCompilationCache();
}
