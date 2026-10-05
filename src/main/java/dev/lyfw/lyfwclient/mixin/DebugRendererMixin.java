package dev.lyfw.lyfwclient.mixin;

import dev.lyfw.lyfwclient.render.HitboxGizmoRenderer;
import java.util.List;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer.SimpleDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DebugRenderer.class})
public class DebugRendererMixin {
   @Shadow
   @Final
   private List<SimpleDebugRenderer> renderers;

   @Inject(
      method = {"refreshRendererList"},
      at = {@At("TAIL")}
   )
   private void lyfwclient$addHitboxRenderer(CallbackInfo ci) {
      this.renderers.add(new HitboxGizmoRenderer());
   }
}
