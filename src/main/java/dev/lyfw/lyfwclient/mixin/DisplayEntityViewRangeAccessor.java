package dev.lyfw.lyfwclient.mixin;

import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Display.class})
public interface DisplayEntityViewRangeAccessor {
   @Invoker("getViewRange")
   float lyfwclient$getViewRange();
}
