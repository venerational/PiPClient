package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.PostPass.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PostPass.class})
public interface PostEffectPassAccessor {
   @Accessor("customUniforms")
   Map<String, GpuBuffer> lyfwclient$getCustomUniforms();

   @Accessor("inputs")
   List<Input> lyfwclient$getSamplers();
}
