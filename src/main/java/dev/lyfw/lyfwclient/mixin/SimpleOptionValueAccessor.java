package dev.lyfw.lyfwclient.mixin;

import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({OptionInstance.class})
public interface SimpleOptionValueAccessor {
   @Accessor("value")
   @Mutable
   void lyfwclient$setRawValue(Object object);
}
