package dev.lyfw.lyfwclient.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractContainerScreen.class})
public interface HandledScreenAccessor {
   @Accessor("leftPos")
   int lyfwclient$getX();

   @Accessor("topPos")
   int lyfwclient$getY();

   @Accessor("imageWidth")
   int lyfwclient$getBackgroundWidth();

   @Accessor("imageHeight")
   int lyfwclient$getBackgroundHeight();
}
