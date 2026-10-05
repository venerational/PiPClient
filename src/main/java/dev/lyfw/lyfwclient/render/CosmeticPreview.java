package dev.lyfw.lyfwclient.render;

import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerSkin;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class CosmeticPreview {
   private CosmeticPreview() {
   }

   public static void draw(
      GuiGraphics context,
      CosmeticPreview.State state,
      PlayerSkin skin,
      CosmeticLoadout look,
      Identifier cape,
      int x1,
      int y1,
      int x2,
      int y2,
      float yaw,
      float seconds
   ) {
      draw(context, state, skin, look, cape, x1, y1, x2, y2, yaw, seconds, false, null);
   }

   public static int naturalPetColor(CosmeticsModule.Pet pet) {
      return PetModels.natural(pet)[0];
   }

   public static float petTrickSeconds(CosmeticsModule.Pet pet) {
      return PetModels.midTrickSeconds(pet);
   }

   public static int petTrickCount() {
      return PetModels.trickCount();
   }

   public static void drawPet(
      GuiGraphics context, CosmeticPreview.State state, PlayerSkin skin, CosmeticLoadout look, int x1, int y1, int x2, int y2, float yaw, float seconds
   ) {
      draw(context, state, skin, look, null, x1, y1, x2, y2, yaw, seconds, true, null);
   }

   public static void drawEmote(
      GuiGraphics context,
      CosmeticPreview.State state,
      PlayerSkin skin,
      CosmeticLoadout look,
      Identifier cape,
      int x1,
      int y1,
      int x2,
      int y2,
      float yaw,
      float seconds,
      CosmeticsModule.Emote emote
   ) {
      draw(context, state, skin, look, cape, x1, y1, x2, y2, yaw, seconds, false, emote);
   }

   private static void draw(
      GuiGraphics context,
      CosmeticPreview.State state,
      PlayerSkin skin,
      CosmeticLoadout look,
      Identifier cape,
      int x1,
      int y1,
      int x2,
      int y2,
      float yaw,
      float seconds,
      boolean petFocus,
      CosmeticsModule.Emote emote
   ) {
      if (x2 - x1 >= 12 && y2 - y1 >= 16 && skin != null) {
         if (cape != null) {
            Texture asset = new ResourceTexture(cape, cape);
            state.skin = new PlayerSkin(skin.body(), asset, asset, skin.model(), skin.secure());
         } else {
            state.skin = new PlayerSkin(skin.body(), null, null, skin.model(), skin.secure());
         }

         state.look = look;
         state.showCape = cape != null;
         state.ageInTicks = seconds * 20.0F;
         state.bodyRot = 180.0F + yaw;
         state.yRot = 0.0F;
         state.xRot = 0.0F;
         state.id = Integer.MIN_VALUE;
         state.petFocus = petFocus;
         state.emote = emote;
         state.emoteSeconds = seconds;
         state.isInvisible = petFocus;
         state.isInvisibleToPlayer = petFocus;
         state.walkAnimationPos = petFocus ? seconds * 7.0F : 0.0F;
         state.walkAnimationSpeed = petFocus ? 0.7F : 0.0F;
         state.lightCoords = 15728880;
         state.boundingBoxWidth = 0.6F;
         state.boundingBoxHeight = 1.8F;
         state.capeFlap = 5.0F + 3.0F * Mth.sin(seconds * 1.7F);
         state.capeLean = 0.0F;
         state.capeLean2 = 7.0F * Mth.sin(seconds * 0.9F);
         float size = Math.min((y2 - y1) / 2.75F, (x2 - x1) / (petFocus ? 1.7F : reach(look)));
         Quaternionf flip = new Quaternionf().rotateZ((float) Math.PI);
         Quaternionf tilt = new Quaternionf().rotateX(-0.13962634F);
         flip.mul(tilt);
         context.submitEntityRenderState(state, size, new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.12F, 0.0F), flip, tilt, x1, y1, x2, y2);
      }
   }

   private static float reach(CosmeticLoadout look) {
      float reach = 0.85F;

      for (int i = 0; i < look.pets().size(); i++) {
         float[] spot = CosmeticsFeatureRenderer.petSpot(i);
         float z = Math.abs(spot[1]) + 0.45F;
         reach = Math.max(reach, (float)Math.sqrt(spot[0] * spot[0] + z * z) + 0.1F);
      }

      return reach * 2.0F;
   }

   public static final class State extends AvatarRenderState {
      public CosmeticLoadout look = CosmeticLoadout.EMPTY;
      public boolean petFocus;
      public CosmeticsModule.Emote emote;
      public float emoteSeconds;
   }
}
