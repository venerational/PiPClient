package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.BlockOpacityModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public class BlockOpacityOcclusionMixin {
   @Inject(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void lyfwclient$hideOccludedPlayers(
      LivingEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci
   ) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null
         && mc.player != null
         && ModuleManager.get("Block Opacity") instanceof BlockOpacityModule opacity
         && opacity.isEnabled()
         && opacity.hidesPlayersBehind()) {
         Vec3 eye = mc.player.getEyePosition();
         Vec3 target = new Vec3(state.x, state.y + state.boundingBoxHeight * 0.5, state.z);
         if (!(eye.distanceToSqr(target) < 4.0)) {
            ClipContext context = new ClipContext(eye, target, Block.COLLIDER, Fluid.NONE, mc.player);
            if (mc.level.clip(context).getType() == Type.BLOCK) {
               ci.cancel();
            }
         }
      }
   }
}
