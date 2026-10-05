package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({AvatarRenderer.class})
public interface PlayerTransformInvoker {
   @Invoker("setupRotations")
   void lyfwclient$setupTransforms(AvatarRenderState avatarRenderState, PoseStack poseStack, float f, float g);

   @Invoker("scale")
   void lyfwclient$scale(AvatarRenderState avatarRenderState, PoseStack poseStack);
}
