package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.profile.ProfileStorage;
import dev.lyfw.lyfwclient.stats.PublicProfiles;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PublicProfilesScreen extends Screen {
   private static final int PANEL_W = 320;
   private static final int HEADER_H = 30;
   private static final int ROW_H = 28;
   private static final int PAD = 8;
   private static final int BUTTON_H = 18;
   private final Screen parent;
   private final Scroll scroll = new Scroll();
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int panelX;
   private int panelY;
   private int panelH;
   private String message = "";
   private String pendingRemove;

   public PublicProfilesScreen(Screen parent) {
      super(Component.literal("Public Profiles"));
      this.parent = parent;
   }

   protected void init() {
      PublicProfiles.get().refresh(PublicProfiles.endpoint());
   }

   private void computeLayout() {
      this.panelH = Math.min(this.height - 40, 300);
      this.panelX = (this.width - 320) / 2;
      this.panelY = (this.height - this.panelH) / 2 - this.open.drop(20);
   }

   private int listTop() {
      return this.panelY + 30 + 4;
   }

   private int listBottom() {
      return this.panelY + this.panelH - 8;
   }

   private int maxScroll() {
      return Math.max(0, PublicProfiles.get().entries().size() * 28 - (this.listBottom() - this.listTop()));
   }

   private int[] refreshBounds() {
      int w = this.font.width("Refresh") + 14;
      return new int[]{this.panelX + 320 - 8 - w, this.panelY + 6, w, 18};
   }

   private int[] copyBounds(int rowY) {
      int w = this.font.width("Copy") + 16;
      return new int[]{this.panelX + 320 - 8 - 4 - w, rowY + 5, w, 18};
   }

   private int[] removeBounds(int rowY) {
      int[] copy = this.copyBounds(rowY);
      int w = this.font.width("Confirm?") + 12;
      return new int[]{copy[0] - 4 - w, copy[1], w, 18};
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      ThemeRenderer.panel(context, this.panelX, this.panelY, 320, this.panelH, Theme.border(), Theme.panelBg(), 8);
      ThemeRenderer.fillRoundedTop(context, this.panelX + 1, this.panelY + 1, 318, 29, Theme.headerBg(), 7);
      context.drawString(this.font, "PUBLIC PROFILES", this.panelX + 12, this.panelY + 11, Theme.textPrimary());
      int[] refresh = this.refreshBounds();
      boolean refreshHovered = inside(mouseX, mouseY, refresh);
      ThemeRenderer.row(context, refresh[0], refresh[1], refresh[2], refresh[3], refreshHovered, Theme.trackBg(), Theme.rowBgHover(), 4);
      context.drawString(
         this.font, "Refresh", refresh[0] + 7, refresh[1] + 5, ThemeRenderer.rowTextColor(refreshHovered, Theme.textSecondary(), Theme.textPrimary())
      );
      List<PublicProfiles.Entry> entries = PublicProfiles.get().entries();
      context.enableScissor(this.panelX + 1, this.listTop(), this.panelX + 320 - 1, this.listBottom());
      int y = this.listTop() - this.scroll.shown();
      if (entries.isEmpty()) {
         String status = PublicProfiles.get().status();
         context.drawString(
            this.font, this.font.plainSubstrByWidth(status.isEmpty() ? "Nothing shared yet." : status, 288), this.panelX + 16, y + 8, Theme.textSecondary()
         );
      }

      for (PublicProfiles.Entry entry : entries) {
         if (y + 28 >= this.listTop() && y <= this.listBottom()) {
            int rowX = this.panelX + 8;
            int rowW = 304;
            boolean rowHovered = mouseX >= rowX
               && mouseX < rowX + rowW
               && mouseY >= y
               && mouseY < y + 28
               && mouseY >= this.listTop()
               && mouseY < this.listBottom();
            ThemeRenderer.row(context, rowX, y + 1, rowW, 26, rowHovered, Theme.rowBg(), Theme.rowBgHover(), 4);
            int[] copy = this.copyBounds(y);
            int textRight = entry.mine() ? this.removeBounds(y)[0] - 6 : copy[0] - 6;
            context.drawString(
               this.font,
               this.font.plainSubstrByWidth(entry.name(), textRight - rowX - 10),
               rowX + 8,
               y + 5,
               entry.mine() ? Theme.accent() : Theme.textPrimary()
            );
            String by = entry.mine() ? "shared by you" : "by " + (entry.author().isEmpty() ? "someone" : entry.author());
            context.drawString(this.font, this.font.plainSubstrByWidth(by, textRight - rowX - 10), rowX + 8, y + 16, Theme.textSecondary());
            boolean copyHovered = inside(mouseX, mouseY, copy) && mouseY >= this.listTop() && mouseY < this.listBottom();
            ThemeRenderer.fillRounded(context, copy[0], copy[1], copy[2], copy[3], copyHovered ? Theme.accent() : Theme.accentRowBg(), 3);
            context.drawString(this.font, "Copy", copy[0] + 8, copy[1] + 5, copyHovered ? ThemeRenderer.onAccent() : Theme.accent());
            if (entry.mine()) {
               int[] remove = this.removeBounds(y);
               boolean confirming = key(entry).equals(this.pendingRemove);
               boolean removeHovered = inside(mouseX, mouseY, remove) && mouseY >= this.listTop() && mouseY < this.listBottom();
               int fill = confirming ? Theme.danger() : (removeHovered ? Theme.danger() & 16777215 | 1426063360 : Theme.dangerBg());
               ThemeRenderer.fillRounded(context, remove[0], remove[1], remove[2], remove[3], fill, 3);
               String label = confirming ? "Confirm?" : "Remove";
               context.drawString(
                  this.font, label, remove[0] + (remove[2] - this.font.width(label)) / 2, remove[1] + 5, confirming ? ThemeRenderer.onAccent() : Theme.danger()
               );
            }
         }

         y += 28;
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackH = this.listBottom() - this.listTop();
         int handleH = Math.max(16, trackH * trackH / (trackH + maxScroll));
         int handleY = this.listTop() + (int)((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         ThemeRenderer.fillRounded(context, this.panelX + 320 - 5, handleY, 3, handleH, Theme.accent(), 1);
      }

      String below = !this.message.isEmpty() ? this.message : (entries.isEmpty() ? "" : PublicProfiles.get().status());
      if (below.isEmpty()) {
         below = "Copy saves it to Profile Presets - nothing changes until you Load it";
      }

      context.drawCenteredString(this.font, below, this.width / 2, this.panelY + this.panelH + 6, Theme.textSecondary());
   }

   private static String key(PublicProfiles.Entry entry) {
      return entry.uuid() + "/" + entry.slug();
   }

   private static boolean inside(int mx, int my, int[] b) {
      return mx >= b[0] && mx < b[0] + b[2] && my >= b[1] && my < b[1] + b[3];
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)click.x();
      int my = (int)click.y();
      if (inside(mx, my, this.refreshBounds())) {
         this.message = "";
         this.pendingRemove = null;
         PublicProfiles.get().refresh(PublicProfiles.endpoint());
         return true;
      } else {
         if (my >= this.listTop() && my < this.listBottom()) {
            List<PublicProfiles.Entry> entries = PublicProfiles.get().entries();
            int index = (my - this.listTop() + this.scroll.shown()) / 28;
            if (index >= 0 && index < entries.size()) {
               PublicProfiles.Entry entry = entries.get(index);
               int rowY = this.listTop() - this.scroll.shown() + index * 28;
               if (inside(mx, my, this.copyBounds(rowY))) {
                  this.pendingRemove = null;
                  this.copy(entry);
                  return true;
               }

               if (entry.mine() && inside(mx, my, this.removeBounds(rowY))) {
                  if (key(entry).equals(this.pendingRemove)) {
                     this.pendingRemove = null;
                     this.message = "";
                     PublicProfiles.get()
                        .unpublish(PublicProfiles.endpoint(), entry.uuid(), entry.slug(), () -> ProfileStorage.clearPublishedSlug(entry.slug()));
                  } else {
                     this.pendingRemove = key(entry);
                     this.message = "Click Confirm? to take \"" + entry.name() + "\" down";
                  }

                  return true;
               }
            }
         }

         this.pendingRemove = null;
         return super.mouseClicked(click, doubled);
      }
   }

   private void copy(PublicProfiles.Entry entry) {
      this.message = "";
      copyToPresets(entry, text -> this.message = text);
   }

   static void copyToPresets(PublicProfiles.Entry entry, Consumer<String> report) {
      PublicProfiles.get()
         .download(
            PublicProfiles.endpoint(),
            entry,
            body -> {
               try {
                  String saved = ProfileStorage.importPublicProfile(
                     PublicProfiles.text(body, "name").isEmpty() ? entry.name() : PublicProfiles.text(body, "name"),
                     PublicProfiles.text(body, "author"),
                     PublicProfiles.text(body, "pip"),
                     PublicProfiles.text(body, "options"),
                     PublicProfiles.text(body, "keybinds")
                  );
                  report.accept("Copied as \"" + saved + "\" - Load it from Profile Presets");
               } catch (RuntimeException | IOException var4) {
                  report.accept("Copy failed: " + (var4.getMessage() == null ? "that profile is not readable" : var4.getMessage()));
               }
            }
         );
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(verticalAmount * 28.0), 0, this.maxScroll()));
      return true;
   }

   public boolean keyPressed(KeyEvent input) {
      if (input.key() == 256) {
         this.onClose();
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   public void onClose() {
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent);
      }
   }
}
