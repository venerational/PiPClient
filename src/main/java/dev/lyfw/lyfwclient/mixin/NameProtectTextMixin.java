package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.NameProtectModule;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({StringDecomposer.class})
public class NameProtectTextMixin {
   @ModifyVariable(
      method = {"iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private static String lyfwclient$replaceName(String text, @Local(argsOnly = true) int startIndex) {
      return startIndex == 0 && ModuleManager.get("Name Protect") instanceof NameProtectModule nameProtect ? nameProtect.replaceName(text) : text;
   }
}
