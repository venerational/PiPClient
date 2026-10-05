package dev.lyfw.lyfwclient;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class PipMixinPlugin implements IMixinConfigPlugin {
   private static final Set<String> NEEDS_POST_PROCESSING = Set.of(
      "dev.lyfw.lyfwclient.mixin.PostEffectProcessorAccessor",
      "dev.lyfw.lyfwclient.mixin.PostEffectPassAccessor",
      "dev.lyfw.lyfwclient.mixin.ShaderLoaderAccessor",
      "dev.lyfw.lyfwclient.mixin.MotionBlurGameRendererMixin",
      "dev.lyfw.lyfwclient.mixin.MotionBlurWorldRendererMixin"
   );
   private static boolean announced;

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (vulkan() && NEEDS_POST_PROCESSING.contains(mixinClassName)) {
         if (!announced) {
            announced = true;
            System.out.println("[Pip Client] VulkanMod detected: Motion Blur is off, since it needs a post-processing pass. Everything else is unaffected.");
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean vulkan() {
      return FabricLoader.getInstance().isModLoaded("vulkanmod");
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
