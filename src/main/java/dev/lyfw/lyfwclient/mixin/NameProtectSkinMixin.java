package dev.lyfw.lyfwclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.NameProtectModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractClientPlayer.class})
public class NameProtectSkinMixin {
   @ModifyReturnValue(
      method = {"getSkin"},
      at = {@At("RETURN")}
   )
   private PlayerSkin lyfwclient$changeSkin(PlayerSkin original) {
      return (Object)this == Minecraft.getInstance().player && ModuleManager.get("Name Protect") instanceof NameProtectModule nameProtect
         ? nameProtect.skinFor(original)
         : original;
   }
}
