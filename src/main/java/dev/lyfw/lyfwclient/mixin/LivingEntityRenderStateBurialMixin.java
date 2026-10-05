package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.module.BurialStateMarker;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({LivingEntityRenderState.class})
public class LivingEntityRenderStateBurialMixin implements BurialStateMarker {
   @Unique
   private float lyfwclient$burialSink;
   @Unique
   private float lyfwclient$burialScale = 1.0F;

   @Override
   public void lyfwclient$setBurialSink(float sink) {
      this.lyfwclient$burialSink = sink;
   }

   @Override
   public float lyfwclient$getBurialSink() {
      return this.lyfwclient$burialSink;
   }

   @Override
   public void lyfwclient$setBurialScale(float scale) {
      this.lyfwclient$burialScale = scale;
   }

   @Override
   public float lyfwclient$getBurialScale() {
      return this.lyfwclient$burialScale;
   }
}
