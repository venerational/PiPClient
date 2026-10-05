package dev.lyfw.lyfwclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lyfw.lyfwclient.module.modules.NostalgiaModule;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EndCrystalRenderer.class})
public class OldCrystalsMixin {
   @Inject(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void lyfwclient$crystalMotion(EndCrystalRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      NostalgiaModule nostalgia = NostalgiaModule.get();
      if (nostalgia != null && nostalgia.crystalMotion() == NostalgiaModule.CrystalMotion.STATIC) {
         state.ageInTicks = 0.0F;
      }
   }

   @ModifyVariable(
      method = {"getY"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private static float lyfwclient$crystalBob(float age) {
      NostalgiaModule nostalgia = NostalgiaModule.get();
      return nostalgia != null && nostalgia.crystalMotion() == NostalgiaModule.CrystalMotion.NO_BOB ? 0.0F : age;
   }
}
