package dev.lyfw.lyfwclient.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.lyfw.lyfwclient.LyfwClient;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import dev.lyfw.lyfwclient.render.CosmeticLoadout;
import dev.lyfw.lyfwclient.render.CosmeticPreview;
import dev.lyfw.lyfwclient.render.EmotePlay;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class EmoteWheelScreen extends Screen {
   private static final int DEAD_ZONE = 26;
   private static final int RADIUS = 122;
   private static final int SLOT_W = 92;
   private static final int SLOT_H = 20;
   private static final CosmeticsModule.Emote[] FALLBACK = new CosmeticsModule.Emote[]{
      CosmeticsModule.Emote.WAVE,
      CosmeticsModule.Emote.CLAP,
      CosmeticsModule.Emote.DAB,
      CosmeticsModule.Emote.FLOSS,
      CosmeticsModule.Emote.LAUGH,
      CosmeticsModule.Emote.SIT,
      CosmeticsModule.Emote.T_POSE,
      CosmeticsModule.Emote.BACKFLIP
   };
   private final List<CosmeticsModule.Emote> wheel = new ArrayList<>();
   private final CosmeticPreview.State preview = new CosmeticPreview.State();
   private int picked = -1;
   private boolean keyWasDown;
   private final long opened = System.currentTimeMillis();

   public EmoteWheelScreen() {
      super(Component.literal("Emote Wheel"));
      CosmeticsModule cosmetics = CosmeticsModule.get();
      if (cosmetics != null) {
         this.wheel.addAll(cosmetics.wheelEmotes());
      }

      if (cosmetics != null && this.wheel.isEmpty()) {
         for (String key : cosmetics.favoriteKeys()) {
            if (key.startsWith("EMOTE:")) {
               try {
                  this.wheel.add(CosmeticsModule.Emote.valueOf(key.substring(6)));
               } catch (IllegalArgumentException var6) {
               }
            }
         }
      }

      if (this.wheel.isEmpty()) {
         for (CosmeticsModule.Emote emote : FALLBACK) {
            this.wheel.add(emote);
         }
      }

      while (this.wheel.size() > 12) {
         this.wheel.remove(this.wheel.size() - 1);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      int cx = this.width / 2;
      int cy = this.height / 2;
      this.picked = this.pointingAt(mouseX, mouseY, cx, cy);
      float grown = Mth.clamp((float)(System.currentTimeMillis() - this.opened) / 140.0F, 0.0F, 1.0F);
      context.fill(0, 0, this.width, this.height, 1140850688);
      ThemeRenderer.fillRounded(context, cx - 46, cy - 52, 92, 104, Ui.alpha(Theme.panelBg(), 0.9F), 10);
      if (this.picked >= 0) {
         CosmeticPreview.drawEmote(
            context,
            this.preview,
            PlayerHead.skinTextures(),
            CosmeticLoadout.EMPTY,
            null,
            cx - 40,
            cy - 46,
            cx + 40,
            cy + 34,
            0.0F,
            Ui.seconds(),
            this.wheel.get(this.picked)
         );
      } else {
         context.drawCenteredString(this.font, "Point at", cx, cy - 12, Theme.textSecondary());
         context.drawCenteredString(this.font, "an emote", cx, cy, Theme.textSecondary());
      }

      String hint = this.picked >= 0 ? pretty(this.wheel.get(this.picked)) : "let go to cancel";
      context.drawCenteredString(this.font, hint, cx, cy + 58, this.picked >= 0 ? Theme.accent() : Theme.textMuted());

      for (int i = 0; i < this.wheel.size(); i++) {
         double angle = this.angleOf(i);
         int sx = cx + (int)Math.round(Math.cos(angle) * 122.0 * grown) - 46;
         int sy = cy + (int)Math.round(Math.sin(angle) * 122.0 * grown) - 10;
         boolean on = i == this.picked;
         ThemeRenderer.fillRounded(context, sx, sy, 92, 20, on ? Ui.mix(Theme.panelBg(), Theme.accent(), 0.5F) : Ui.alpha(Theme.panelBg(), 0.85F), 8);
         ThemeRenderer.fillRounded(context, sx, sy, 92, 1, on ? Theme.accent() : Theme.border(), 0);
         String label = this.font.plainSubstrByWidth(pretty(this.wheel.get(i)), 84);
         context.drawCenteredString(this.font, label, sx + 46, sy + 6, on ? Theme.textPrimary() : Theme.textSecondary());
      }

      this.watchKey();
   }

   private void watchKey() {
      Minecraft client = Minecraft.getInstance();
      int code = KeyBindingHelper.getBoundKeyOf(LyfwClient.emoteWheelKey).getValue();
      boolean down = code != InputConstants.UNKNOWN.getValue() && InputConstants.isKeyDown(client.getWindow(), code);
      if (down) {
         this.keyWasDown = true;
      } else if (this.keyWasDown) {
         this.playAndClose();
      }
   }

   private void playAndClose() {
      Minecraft client = Minecraft.getInstance();
      if (this.picked >= 0) {
         EmotePlay.play(this.wheel.get(this.picked), client.player);
         CosmeticsModule cosmetics = CosmeticsModule.get();
         if (cosmetics != null) {
            cosmetics.setEmote(this.wheel.get(this.picked));
         }
      }

      this.onClose();
   }

   private int pointingAt(int mouseX, int mouseY, int cx, int cy) {
      double dx = mouseX - cx;
      double dy = mouseY - cy;
      if (!(dx * dx + dy * dy < 676.0) && !this.wheel.isEmpty()) {
         double angle = Math.atan2(dy, dx);
         double step = (Math.PI * 2) / this.wheel.size();
         int slot = (int)Math.round((angle - this.angleOf(0)) / step);
         return Math.floorMod(slot, this.wheel.size());
      } else {
         return -1;
      }
   }

   private double angleOf(int index) {
      return (-Math.PI / 2) + index * ((Math.PI * 2) / this.wheel.size());
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      if (click.button() == 0) {
         this.playAndClose();
         return true;
      } else {
         this.onClose();
         return true;
      }
   }

   private static String pretty(CosmeticsModule.Emote emote) {
      StringBuilder out = new StringBuilder();

      for (String word : emote.name().split("_")) {
         out.append(out.isEmpty() ? "" : " ").append(word.charAt(0)).append(word.substring(1).toLowerCase());
      }

      return out.toString();
   }
}
