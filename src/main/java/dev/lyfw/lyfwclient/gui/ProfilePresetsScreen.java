package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.profile.ProfileStorage;
import dev.lyfw.lyfwclient.stats.PublicProfiles;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ProfilePresetsScreen extends Screen {
   private static final int PANEL_WIDTH = 280;
   private static final int PANEL_MARGIN_TOP = 20;
   private static final int SECTION_PADDING = 10;
   private static final int ROW_HEIGHT = 20;
   private static final int FIELD_HEIGHT = 20;
   private static final int CHECKBOX_SIZE = 12;
   private static final int BUTTON_HEIGHT = 20;
   private static final int LIST_ROW_HEIGHT = 22;
   private static final int LIST_VISIBLE_ROWS = 5;
   private static final int CORNER_RADIUS = 3;
   private static final String[] TOGGLE_LABELS = new String[]{"Configs", "Options", "Keys+Mouse", "Packs"};
   private final Screen parent;
   private String profileName = "";
   private boolean saveConfigs = true;
   private boolean saveOptions = true;
   private boolean saveKeybinds = true;
   private boolean savePacks = true;
   private List<String> profiles = List.of();
   private int scrollOffset;
   private String pendingDeleteName;
   private String pendingPublishName;
   private Map<String, String[]> published = Map.of();
   private boolean watchingNetwork;
   private int publicButtonY;
   private String status = "";
   private int statusColor = Theme.textSecondary();
   private int panelLeft;
   private int panelRight;
   private int panelTop;
   private final Anim open = new Anim(0.2F);
   private static final int DROP = 20;
   private int nameFieldY;
   private int toggleRowY;
   private int saveButtonY;
   private int listHeaderY;
   private int listTop;
   private int listBottom;
   private int statusY;

   public ProfilePresetsScreen(Screen parent) {
      super(Component.literal("Profile Presets"));
      this.parent = parent;
   }

   protected void init() {
      this.refreshProfiles();
   }

   private void refreshProfiles() {
      try {
         this.profiles = new ArrayList<>(ProfileStorage.listProfiles());
         Map<String, String[]> shared = new HashMap<>();

         for (String name : this.profiles) {
            String[] as = ProfileStorage.publishedAs(name);
            if (as != null) {
               shared.put(name, as);
            }
         }

         this.published = shared;
      } catch (IOException var5) {
         this.profiles = List.of();
         this.setStatus("Failed to list saved profiles: " + var5.getMessage(), Theme.danger());
      }

      this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.maxScroll());
   }

   private int maxScroll() {
      return Math.max(0, this.profiles.size() - 5);
   }

   private void setStatus(String message, int color) {
      this.status = message;
      this.statusColor = color;
      this.watchingNetwork = false;
   }

   private void computeLayout() {
      this.panelLeft = this.width / 2 - 140;
      this.panelRight = this.panelLeft + 280;
      this.panelTop = 20 - this.open.drop(20);
      this.nameFieldY = this.panelTop + 34;
      this.toggleRowY = this.nameFieldY + 20 + 20;
      this.saveButtonY = this.toggleRowY + 20;
      this.listHeaderY = this.saveButtonY + 20 + 10 + 4;
      this.listTop = this.listHeaderY + 12;
      this.listBottom = this.listTop + Math.min(this.profiles.size(), 5) * 22;
      if (this.profiles.isEmpty()) {
         this.listBottom = this.listTop + 22;
      }

      this.publicButtonY = this.listBottom + 8;
      this.statusY = this.publicButtonY + 20 + 10;
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.computeLayout();
      super.render(context, mouseX, mouseY, deltaTicks);
      int panelBottom = this.statusY + 16;
      fillRounded(context, this.panelLeft + 2, this.panelTop + 2, 280, panelBottom - this.panelTop, Theme.shadow(), 3);
      fillRoundedBorder(context, this.panelLeft, this.panelTop, 280, panelBottom - this.panelTop, Theme.modalBorder(), Theme.modalPanelBg(), 3);
      fillRoundedTop(context, this.panelLeft + 1, this.panelTop + 1, 278, 24, Theme.modalHeaderBg(), 2);
      context.drawCenteredString(this.font, "Profile Presets", this.width / 2, this.panelTop + 8, Theme.textPrimary());
      this.renderNameField(context, mouseX, mouseY);
      this.renderToggles(context, mouseX, mouseY);
      this.renderSaveButton(context, mouseX, mouseY);
      this.renderProfileList(context, mouseX, mouseY);
      boolean publicHovered = this.isInside(mouseX, mouseY, this.panelLeft + 10, this.publicButtonY, 260, 20);
      ThemeRenderer.row(context, this.panelLeft + 10, this.publicButtonY, 260, 20, publicHovered, Theme.trackBg(), Theme.rowBgHover(), 3);
      String publicLabel = "Public Profiles";
      context.drawString(
         this.font,
         publicLabel,
         this.panelLeft + 10 + (260 - this.font.width(publicLabel)) / 2,
         this.publicButtonY + 6,
         ThemeRenderer.rowTextColor(publicHovered, Theme.textSecondary(), Theme.textPrimary())
      );
      String networkStatus = PublicProfiles.get().status();
      String shownStatus = this.watchingNetwork && !networkStatus.isEmpty() ? networkStatus : this.status;
      if (!shownStatus.isEmpty()) {
         context.drawCenteredString(this.font, shownStatus, this.width / 2, this.statusY, this.watchingNetwork ? Theme.accent() : this.statusColor);
      }
   }

   private void renderNameField(GuiGraphics context, int mouseX, int mouseY) {
      int fieldLeft = this.panelLeft + 10;
      int fieldRight = this.panelRight - 10;
      fillRoundedBorder(context, fieldLeft, this.nameFieldY, fieldRight - fieldLeft, 20, Theme.modalBorder(), Theme.trackBg(), 2);
      String display = this.profileName;
      if (System.currentTimeMillis() / 500L % 2L == 0L) {
         display = display + "_";
      }

      if (this.profileName.isEmpty()) {
         context.drawString(this.font, "Profile name...", fieldLeft + 6, this.nameFieldY + 6, Theme.textSecondary());
      } else {
         context.drawString(this.font, display, fieldLeft + 6, this.nameFieldY + 6, Theme.textPrimary());
      }
   }

   private int checkboxX(int index) {
      int totalWidth = 260;
      int slot = totalWidth / TOGGLE_LABELS.length;
      return this.panelLeft + 10 + slot * index;
   }

   private void renderToggles(GuiGraphics context, int mouseX, int mouseY) {
      for (int i = 0; i < TOGGLE_LABELS.length; i++) {
         int x = this.checkboxX(i);
         boolean checked = this.toggleState(i);
         fillRoundedBorder(context, x, this.toggleRowY, 12, 12, Theme.modalBorder(), checked ? Theme.accent() : Theme.trackBg(), 2);
         context.drawString(this.font, TOGGLE_LABELS[i], x + 12 + 4, this.toggleRowY + 2, Theme.textMuted());
      }
   }

   private boolean toggleState(int index) {
      return switch (index) {
         case 0 -> this.saveConfigs;
         case 1 -> this.saveOptions;
         case 2 -> this.saveKeybinds;
         default -> this.savePacks;
      };
   }

   private void flipToggle(int index) {
      switch (index) {
         case 0:
            this.saveConfigs = !this.saveConfigs;
            break;
         case 1:
            this.saveOptions = !this.saveOptions;
            break;
         case 2:
            this.saveKeybinds = !this.saveKeybinds;
            break;
         default:
            this.savePacks = !this.savePacks;
      }
   }

   private void renderSaveButton(GuiGraphics context, int mouseX, int mouseY) {
      int buttonLeft = this.panelLeft + 10;
      int buttonWidth = 260;
      boolean hovered = this.isInside(mouseX, mouseY, buttonLeft, this.saveButtonY, buttonWidth, 20);
      fillRounded(context, buttonLeft, this.saveButtonY, buttonWidth, 20, hovered ? Theme.accent() : Theme.accentRowBg(), 2);
      int textColor = hovered ? ThemeRenderer.onAccent() : Theme.accent();
      String label = "Save Profile";
      int textWidth = this.font.width(label);
      context.drawString(this.font, label, buttonLeft + (buttonWidth - textWidth) / 2, this.saveButtonY + 6, textColor);
   }

   private void renderProfileList(GuiGraphics context, int mouseX, int mouseY) {
      context.drawString(this.font, "Saved Profiles (" + this.profiles.size() + ")", this.panelLeft + 10, this.listHeaderY, Theme.textSecondary());
      if (this.profiles.isEmpty()) {
         context.drawString(this.font, "No profiles saved yet.", this.panelLeft + 10, this.listTop + 6, Theme.textSecondary());
      } else {
         int visibleCount = Math.min(this.profiles.size() - this.scrollOffset, 5);

         for (int i = 0; i < visibleCount; i++) {
            String name = this.profiles.get(this.scrollOffset + i);
            int rowY = this.listTop + i * 22;
            boolean hovered = mouseY >= rowY && mouseY < rowY + 22 && mouseX >= this.panelLeft + 10 && mouseX < this.panelRight - 10;
            if (hovered) {
               context.fill(this.panelLeft + 10, rowY, this.panelRight - 10, rowY + 22, Theme.hoverBg());
            }

            int[] publicBounds = this.publicButtonBounds(rowY);
            context.drawString(
               this.font,
               this.font.plainSubstrByWidth(name, publicBounds[0] - (this.panelLeft + 14) - 4),
               this.panelLeft + 10 + 4,
               rowY + 6,
               Theme.textPrimary()
            );
            boolean isShared = this.published.containsKey(name);
            boolean confirmingPublish = name.equals(this.pendingPublishName);
            boolean publicHovered = this.isInside(mouseX, mouseY, publicBounds[0], publicBounds[1], publicBounds[2], publicBounds[3]);
            int publicFill = !confirmingPublish && !publicHovered ? (isShared ? Theme.trackBg() : Theme.accentRowBg()) : Theme.accent();
            fillRounded(context, publicBounds[0], publicBounds[1], publicBounds[2], publicBounds[3], publicFill, 2);
            String publicLabel = confirmingPublish ? "Confirm?" : (isShared ? "Unshare" : "Public");
            int publicText = !confirmingPublish && !publicHovered ? (isShared ? Theme.textSecondary() : Theme.accent()) : ThemeRenderer.onAccent();
            context.drawString(this.font, publicLabel, publicBounds[0] + (publicBounds[2] - this.font.width(publicLabel)) / 2, publicBounds[1] + 4, publicText);
            boolean confirmingDelete = name.equals(this.pendingDeleteName);
            int[] deleteBounds = this.deleteButtonBounds(rowY);
            int[] loadBounds = this.loadButtonBounds(rowY);
            boolean deleteHovered = this.isInside(mouseX, mouseY, deleteBounds[0], deleteBounds[1], deleteBounds[2], deleteBounds[3]);
            boolean loadHovered = this.isInside(mouseX, mouseY, loadBounds[0], loadBounds[1], loadBounds[2], loadBounds[3]);
            fillRounded(context, loadBounds[0], loadBounds[1], loadBounds[2], loadBounds[3], loadHovered ? Theme.accent() : Theme.accentRowBg(), 2);
            String loadLabel = "Load";
            context.drawString(
               this.font,
               loadLabel,
               loadBounds[0] + (loadBounds[2] - this.font.width(loadLabel)) / 2,
               loadBounds[1] + 4,
               loadHovered ? ThemeRenderer.onAccent() : Theme.accent()
            );
            int deleteFill = confirmingDelete ? Theme.danger() : (deleteHovered ? Theme.danger() & 16777215 | 1426063360 : Theme.dangerBg());
            fillRounded(context, deleteBounds[0], deleteBounds[1], deleteBounds[2], deleteBounds[3], deleteFill, 2);
            String deleteLabel = confirmingDelete ? "Confirm?" : "Delete";
            int deleteTextColor = confirmingDelete ? ThemeRenderer.onAccent() : Theme.danger();
            context.drawString(
               this.font, deleteLabel, deleteBounds[0] + (deleteBounds[2] - this.font.width(deleteLabel)) / 2, deleteBounds[1] + 4, deleteTextColor
            );
         }
      }
   }

   private int[] loadButtonBounds(int rowY) {
      int width = 42;
      int height = 16;
      int x = this.panelRight - 10 - 42 - 4 - 58;
      int y = rowY + (22 - height) / 2;
      return new int[]{x, y, width, height};
   }

   private int[] deleteButtonBounds(int rowY) {
      int width = 58;
      int height = 16;
      int x = this.panelRight - 10 - width;
      int y = rowY + (22 - height) / 2;
      return new int[]{x, y, width, height};
   }

   private int[] publicButtonBounds(int rowY) {
      int width = 48;
      int height = 16;
      int x = this.loadButtonBounds(rowY)[0] - 4 - width;
      int y = rowY + (22 - height) / 2;
      return new int[]{x, y, width, height};
   }

   private boolean isInside(int mouseX, int mouseY, int x, int y, int w, int h) {
      return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      int mx = (int)click.x();
      int my = (int)click.y();
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         for (int i = 0; i < TOGGLE_LABELS.length; i++) {
            if (this.isInside(mx, my, this.checkboxX(i), this.toggleRowY, 12, 12)) {
               this.flipToggle(i);
               return true;
            }
         }

         int buttonLeft = this.panelLeft + 10;
         int buttonWidth = 260;
         if (this.isInside(mx, my, buttonLeft, this.saveButtonY, buttonWidth, 20)) {
            this.doSave();
            return true;
         } else if (this.isInside(mx, my, this.panelLeft + 10, this.publicButtonY, 260, 20)) {
            this.pendingDeleteName = null;
            this.pendingPublishName = null;
            Minecraft.getInstance().setScreen(new PublicProfilesScreen(this));
            return true;
         } else {
            if (!this.profiles.isEmpty()) {
               int visibleCount = Math.min(this.profiles.size() - this.scrollOffset, 5);

               for (int ix = 0; ix < visibleCount; ix++) {
                  String name = this.profiles.get(this.scrollOffset + ix);
                  int rowY = this.listTop + ix * 22;
                  int[] publicBounds = this.publicButtonBounds(rowY);
                  if (this.isInside(mx, my, publicBounds[0], publicBounds[1], publicBounds[2], publicBounds[3])) {
                     this.pendingDeleteName = null;
                     if (name.equals(this.pendingPublishName)) {
                        this.pendingPublishName = null;
                        if (this.published.containsKey(name)) {
                           this.doUnshare(name);
                        } else {
                           this.doPublish(name);
                        }
                     } else {
                        this.pendingPublishName = name;
                        this.setStatus(
                           this.published.containsKey(name)
                              ? "Click Confirm? to stop sharing \"" + name + "\"."
                              : "Shares Pip settings, options and keybinds only. Click Confirm?",
                           Theme.accent()
                        );
                     }

                     return true;
                  }

                  int[] loadBounds = this.loadButtonBounds(rowY);
                  int[] deleteBounds = this.deleteButtonBounds(rowY);
                  if (this.isInside(mx, my, loadBounds[0], loadBounds[1], loadBounds[2], loadBounds[3])) {
                     this.doLoad(name);
                     return true;
                  }

                  if (this.isInside(mx, my, deleteBounds[0], deleteBounds[1], deleteBounds[2], deleteBounds[3])) {
                     if (name.equals(this.pendingDeleteName)) {
                        this.doDelete(name);
                     } else {
                        this.pendingDeleteName = name;
                        this.setStatus("Click Delete again on \"" + name + "\" to confirm.", Theme.danger());
                     }

                     return true;
                  }
               }
            }

            this.pendingDeleteName = null;
            this.pendingPublishName = null;
            return super.mouseClicked(click, doubled);
         }
      }
   }

   private void doSave() {
      String name = ProfileStorage.sanitizeProfileName(this.profileName);
      ProfileStorage.SaveSelection selection = new ProfileStorage.SaveSelection(this.saveConfigs, this.saveOptions, this.saveKeybinds, this.savePacks);
      if (!selection.anyEnabled()) {
         this.setStatus("Enable at least one save option first.", Theme.danger());
      } else if (!ProfileStorage.isValidProfileName(name)) {
         this.setStatus("Name must use only letters, numbers, and spaces.", Theme.danger());
      } else {
         try {
            ProfileStorage.saveProfile(name, selection);
            this.setStatus("Saved \"" + name + "\".", Theme.accent());
            this.profileName = "";
            this.refreshProfiles();
         } catch (IllegalArgumentException | IOException var5) {
            String reason = var5.getMessage() == null ? "Unknown error." : var5.getMessage();
            this.setStatus("Save failed: " + reason, Theme.danger());
         }
      }
   }

   private void doPublish(String name) {
      try {
         ProfileStorage.PublishPayload payload = ProfileStorage.buildPublishPayload(name);
         if (payload.isEmpty()) {
            this.setStatus("Nothing in \"" + name + "\" to share.", Theme.danger());
         } else {
            Minecraft client = Minecraft.getInstance();
            String uuid = client.getUser() != null && client.getUser().getProfileId() != null ? client.getUser().getProfileId().toString() : "";
            PublicProfiles.get().publish(PublicProfiles.endpoint(), payload, slug -> {
               try {
                  ProfileStorage.markPublished(name, uuid, slug);
               } catch (IOException var5x) {
               }

               this.refreshProfiles();
            });
            this.watchingNetwork = true;
         }
      } catch (IOException var5) {
         this.setStatus("Share failed: " + var5.getMessage(), Theme.danger());
      }
   }

   private void doUnshare(String name) {
      String[] shared = this.published.get(name);
      if (shared != null) {
         PublicProfiles.get().unpublish(PublicProfiles.endpoint(), shared[0], shared[1], () -> {
            ProfileStorage.clearPublished(name);
            this.refreshProfiles();
         });
         this.watchingNetwork = true;
      }
   }

   private void doLoad(String name) {
      try {
         ProfileStorage.loadProfile(name);
         Config.reloadForProfile();
         this.applyLoadedOptionsLive();
         this.setStatus("Loaded \"" + name + "\". Other mods' configs may still need a restart.", Theme.accent());
      } catch (IllegalArgumentException | IOException var4) {
         String reason = var4.getMessage() == null ? "Unknown error." : var4.getMessage();
         this.setStatus("Load failed: " + reason, Theme.danger());
      }
   }

   private void applyLoadedOptionsLive() {
      Minecraft client = Minecraft.getInstance();
      if (client.options != null) {
         client.options.load();
         client.options.loadSelectedResourcePacks(client.getResourcePackRepository());
         client.reloadResourcePacks();
      }
   }

   private void doDelete(String name) {
      try {
         ProfileStorage.deleteProfile(name);
         this.setStatus("Deleted \"" + name + "\".", Theme.textSecondary());
      } catch (IOException var3) {
         this.setStatus("Delete failed: " + var3.getMessage(), Theme.danger());
      }

      this.pendingDeleteName = null;
      this.refreshProfiles();
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.profiles.isEmpty()) {
         this.scrollOffset = Mth.clamp(this.scrollOffset - (int)Math.signum(verticalAmount), 0, this.maxScroll());
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public boolean charTyped(CharacterEvent input) {
      if (this.profileName.length() < 48 && input.isAllowedChatCharacter() && input.codepoint() != 167) {
         this.profileName = this.profileName + Character.toString(input.codepoint());
         return true;
      } else {
         return super.charTyped(input);
      }
   }

   public boolean keyPressed(KeyEvent input) {
      int keyCode = input.key();
      if (keyCode == 259) {
         if (!this.profileName.isEmpty()) {
            this.profileName = this.profileName.substring(0, this.profileName.length() - 1);
         }

         return true;
      } else if (keyCode == 257 || keyCode == 335) {
         this.doSave();
         return true;
      } else if (keyCode == 256) {
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

   private static void fillRounded(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      ThemeRenderer.fillRounded(context, x, y, w, h, color, radius);
   }

   private static void fillRoundedTop(GuiGraphics context, int x, int y, int w, int h, int color, int radius) {
      ThemeRenderer.fillRoundedTop(context, x, y, w, h, color, radius);
   }

   private static void fillRoundedBorder(GuiGraphics context, int x, int y, int w, int h, int borderColor, int fillColor, int radius) {
      ThemeRenderer.fillRoundedBorder(context, x, y, w, h, borderColor, fillColor, radius);
   }
}
