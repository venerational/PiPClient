package dev.lyfw.lyfwclient.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.accounts.PipAccounts;
import dev.lyfw.lyfwclient.module.Category;
import dev.lyfw.lyfwclient.module.Module;
import dev.lyfw.lyfwclient.module.ModuleManager;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import ru.vidtu.ias.account.Account;

public class CarrotClickGuiScreen extends Screen implements ClickGuiRoot {
   private static final int MARGIN = 14;
   private static final int MAX_W = 940;
   private static final int MAX_H = 600;
   private static final int PAD = 14;
   private static final int HEADER_H = 60;
   private static final int NAV_H = 22;
   private static final int CARD_MIN_W = 200;
   private static final int CARD_H = 68;
   private static final int GAP = 8;
   private static final int HUD_BANNER_H = 36;
   private static final int MAX_SEARCH_LEN = 32;
   private static final int MENU_W = 196;
   private static final int MENU_ROW = 24;
   private static final int MENU_MAX_ROWS = 8;
   private static final String[] TOOL_GLYPHS = new String[]{"sliders", "move", "zoom", "chart"};
   private static final String[] TOOL_NAMES = new String[]{"Theme", "HUD editor", "Search (Ctrl F)", "Leaderboard"};
   private static Category selectedCategory = Category.values()[0];
   private static boolean wardrobePage;
   private String identityNotice = "";
   private long identityNoticeUntil;
   private boolean accountsOpen;
   private final Anim accountsAnim = new Anim(0.16F);
   private final Scroll[] scrollOffsets = new Scroll[Category.values().length];
   private String searchText;
   private boolean searchFocused;
   private boolean cosmeticSearchFocused;
   private int panelX;
   private int panelY;
   private int panelW;
   private int panelH;
   private final Anim open;
   private final Motion motion;
   private long cardsStart;
   private Object shownList;
   private WardrobePanel wardrobe;
   private final Map<Module, CarrotClickGuiScreen.WrappedText> wrapped;
   private boolean pointer;

   public CarrotClickGuiScreen() {
      super(Component.translatable("screen.lyfwclient.title"));

      for (int i = 0; i < this.scrollOffsets.length; i++) {
         this.scrollOffsets[i] = new Scroll();
      }

      this.searchText = "";
      this.open = new Anim(0.26F);
      this.motion = new Motion();
      this.cardsStart = System.nanoTime();
      this.wrapped = new HashMap<>();
   }

   public static Screen wardrobe() {
      wardrobePage = true;
      return new CarrotClickGuiScreen();
   }

   public static Category lastCategory() {
      return selectedCategory;
   }

   public static void setLastCategory(Category category) {
      if (category != null) {
         selectedCategory = category;
      }
   }

   protected void init() {
      if (this.wardrobe == null && CosmeticsModule.get() != null) {
         this.wardrobe = new WardrobePanel(CosmeticsModule.get());
         this.wardrobe.prefetch();
      }
   }

   private boolean onWardrobe() {
      return wardrobePage && this.wardrobe != null;
   }

   private List<Module> modules() {
      List<Module> all = ModuleManager.byCategory(selectedCategory);
      if (this.searchText.isEmpty()) {
         return all;
      } else {
         String needle = this.searchText.toLowerCase();
         List<Module> out = new ArrayList<>();

         for (Module module : all) {
            if (module.name.toLowerCase().contains(needle) || module.description.toLowerCase().contains(needle)) {
               out.add(module);
            }
         }

         return out;
      }
   }

   private boolean hasHudRow() {
      return selectedCategory == Category.HUD && this.searchText.isEmpty();
   }

   private static String glyphFor(Category category) {
      return switch (category) {
         case RENDER -> "cat_render";
         case HUD -> "cat_hud";
         case MISC -> "cat_misc";
         case ALL -> "cat_all";
         case ACTIVE -> "bolt";
         case FAVORITE -> "cat_favorite";
      };
   }

   private String version() {
      return FabricLoader.getInstance().getModContainer("lyfw-client").map(c -> "v" + c.getMetadata().getVersion().getFriendlyString()).orElse("Pip");
   }

   private void computeLayout() {
      this.panelW = Math.min(940, Math.max(360, this.width - 28));
      this.panelH = Math.min(600, Math.max(220, this.height - 28));
      this.panelX = (this.width - this.panelW) / 2;
      this.panelY = (this.height - this.panelH) / 2 - this.open.drop(22);
      int i = selectedCategory.ordinal();
      this.scrollOffsets[i].setTarget(Mth.clamp(this.scrollOffsets[i].target(), 0, this.maxScroll()));
   }

   private int railW() {
      return this.panelW < 560 ? 124 : 150;
   }

   private int railInnerX() {
      return this.panelX + 10;
   }

   private int railInnerW() {
      return this.railW() - 20;
   }

   private int contentX() {
      return this.panelX + this.railW() + 14;
   }

   private int contentW() {
      return this.panelW - this.railW() - 28;
   }

   private boolean compactRail() {
      return this.panelH < 380;
   }

   private int[] brandBounds() {
      return this.compactRail()
         ? new int[]{this.railInnerX(), this.panelY + 7, this.railInnerW(), 18}
         : new int[]{this.railInnerX(), this.panelY + 10, this.railInnerW(), 26};
   }

   private int[] identityBounds() {
      return this.compactRail()
         ? new int[]{this.railInnerX(), this.panelY + 29, this.railInnerW(), 22}
         : new int[]{this.railInnerX(), this.panelY + 42, this.railInnerW(), 30};
   }

   private int navTop() {
      return this.compactRail() ? this.panelY + 57 : this.panelY + 96;
   }

   private int styleGap() {
      return this.wardrobe == null ? 0 : (this.compactRail() ? 4 : 20);
   }

   private int navStep() {
      int count = Category.tabs().length + (this.wardrobe == null ? 0 : 1);
      int room = this.toolBounds(0)[1] - 6 - this.navTop() - this.styleGap();
      return Mth.clamp(room / count, 14, 24);
   }

   private int navH() {
      return this.navStep() - 2;
   }

   private int[] navBounds(int index) {
      return new int[]{this.railInnerX(), this.navTop() + index * this.navStep(), this.railInnerW(), this.navH()};
   }

   private int wardrobeLabelY() {
      return this.navTop() + Category.tabs().length * this.navStep() + 6;
   }

   private int[] wardrobeNavBounds() {
      return new int[]{this.railInnerX(), this.navTop() + Category.tabs().length * this.navStep() + this.styleGap(), this.railInnerW(), this.navH()};
   }

   private int[] toolBounds(int index) {
      int w = (this.railInnerW() - 12) / 4;
      return new int[]{this.railInnerX() + index * (w + 4), this.panelY + this.panelH - 10 - 22, w, 22};
   }

   private int[] searchBounds() {
      int w = this.contentW() < 360 ? 120 : 176;
      return new int[]{this.contentX() + this.contentW() - w, this.panelY + 18, w, 22};
   }

   private int gridTop() {
      return this.panelY + 60 + 4;
   }

   private int gridBottom() {
      return this.panelY + this.panelH - 14;
   }

   private int gridW() {
      return this.contentW() - 8;
   }

   private int columns() {
      return Math.max(1, (this.gridW() + 8) / 208);
   }

   private int cardW() {
      int cols = this.columns();
      return (this.gridW() - (cols - 1) * 8) / cols;
   }

   private int cardsOffset() {
      return this.hasHudRow() ? 44 : 0;
   }

   private int maxScroll() {
      int count = this.modules().size();
      int rows = (count + this.columns() - 1) / this.columns();
      int content = this.cardsOffset() + rows * 76 - 8;
      return Math.max(0, content - (this.gridBottom() - this.gridTop()));
   }

   private int scroll() {
      return this.scrollOffsets[selectedCategory.ordinal()].shown();
   }

   private int[] hudBannerBounds() {
      return new int[]{this.contentX(), this.gridTop() - this.scroll(), this.gridW(), 36};
   }

   private int[] cardBounds(int index) {
      int cols = this.columns();
      return new int[]{
         this.contentX() + index % cols * (this.cardW() + 8), this.gridTop() + this.cardsOffset() + index / cols * 76 - this.scroll(), this.cardW(), 68
      };
   }

   private int[] starBounds(int[] card) {
      return new int[]{card[0] + card[2] - 10 - 10, card[1] + 10, 10, 10};
   }

   private int[] settingsLinkBounds(int[] card) {
      return new int[]{card[0] + 6, card[1] + card[3] - 19, this.font.width("Settings ›") + 8, 16};
   }

   private int[] toggleBounds(int[] card) {
      return new int[]{card[0] + card[2] - 10 - 24, card[1] + card[3] - 17, 24, 13};
   }

   private static boolean hasSettings(Module module) {
      return !module.getSettings().isEmpty() || module instanceof CosmeticsModule || module.name.equals("Leaderboard");
   }

   protected void renderBlurredBackground(GuiGraphics context) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
      this.motion.frame();
      this.computeLayout();
      this.pointer = false;
      Object list = this.onWardrobe() ? "wardrobe" : selectedCategory.name() + "|" + this.searchText;
      if (!list.equals(this.shownList)) {
         this.shownList = list;
         this.cardsStart = System.nanoTime();
      }

      boolean menuCovers = this.accountsOpen && Ui.inside(mouseX, mouseY, this.accountsBounds());
      int mx = menuCovers ? -10000 : mouseX;
      int my = menuCovers ? -10000 : mouseY;
      context.fill(0, 0, this.width, this.height, this.open.fade(Theme.scrim()));
      ThemeRenderer.fillRounded(context, this.panelX + 2, this.panelY + 5, this.panelW, this.panelH, Theme.shadow(), 14);
      ThemeRenderer.panel(context, this.panelX, this.panelY, this.panelW, this.panelH, Theme.border(), Theme.panelBg(), 12);
      this.renderRail(context, mx, my);
      if (this.onWardrobe()) {
         this.renderWardrobeHeader(context, mx, my);
         this.wardrobe
            .render(context, this.font, this.contentX(), this.gridTop(), this.contentW(), this.gridBottom() - this.gridTop(), mx, my, this.width, this.height);
      } else {
         this.renderHeader(context, mx, my);
         this.renderCards(context, mx, my);
      }

      context.nextStratum();
      if (this.onWardrobe()) {
         this.wardrobe.renderOverlay(context, mouseX, mouseY);
      }

      for (int i = 0; i < TOOL_GLYPHS.length; i++) {
         int[] b = this.toolBounds(i);
         if (Ui.inside(mx, my, b)) {
            Ui.tooltip(context, this.font, TOOL_NAMES[i], b[0] + b[2] / 2, b[1] - 2, this.width);
         }
      }

      if (Ui.inside(mx, my, this.brandBounds())) {
         int[] b = this.brandBounds();
         Ui.tooltip(context, this.font, "What's new", b[0] + b[2] / 2, b[1] - 1, this.width);
      }

      if (this.accountsOpen) {
         this.renderAccounts(context, mouseX, mouseY);
      }

      if (this.pointer) {
         context.requestCursor(CursorTypes.POINTING_HAND);
      }
   }

   private boolean hover(int mouseX, int mouseY, int[] b) {
      boolean inside = Ui.inside(mouseX, mouseY, b);
      this.pointer |= inside;
      return inside;
   }

   private void renderRail(GuiGraphics context, int mouseX, int mouseY) {
      int railW = this.railW();
      int x = this.panelX + 1;
      int y = this.panelY + 1;
      int h = this.panelH - 2;
      ThemeRenderer.fillRounded(context, x, y, railW, h, Theme.headerBg(), 11);
      context.fill(x + railW - 11, y, x + railW, y + h, Theme.headerBg());
      context.fill(this.panelX + railW, y + 10, this.panelX + railW + 1, y + h - 10, Theme.sectionDivider());
      float intro = this.open.eased();
      int[] brand = this.brandBounds();
      if (this.hover(mouseX, mouseY, brand)) {
         ThemeRenderer.fillRounded(context, brand[0] - 2, brand[1], brand[2] + 4, brand[3], Theme.hoverBg(), 7);
      }

      int versionColor = Ui.inside(mouseX, mouseY, brand) ? Theme.accent() : Theme.textMuted();
      if (this.compactRail()) {
         ThemeRenderer.fillGradientRounded(context, brand[0] + 2, brand[1] + 2, 14, 14, 4, Theme.accent(), Theme.accent2());
         context.drawCenteredString(this.font, "P", brand[0] + 9, brand[1] + 5, ThemeRenderer.onAccent());
         context.drawString(this.font, "Pip", brand[0] + 21, brand[1] + 5, Theme.textPrimary(), false);
         context.drawString(this.font, this.font.plainSubstrByWidth(this.version(), brand[2] - 44), brand[0] + 41, brand[1] + 5, versionColor, false);
      } else {
         ThemeRenderer.fillGradientRounded(context, brand[0] + 2, brand[1] + 4, 18, 18, 5, Theme.accent(), Theme.accent2());
         context.drawCenteredString(this.font, "P", brand[0] + 11, brand[1] + 9, ThemeRenderer.onAccent());
         context.drawString(this.font, "Pip Client", brand[0] + 26, brand[1] + 4, Theme.textPrimary(), false);
         context.drawString(this.font, this.version(), brand[0] + 26, brand[1] + 15, versionColor, false);
      }

      int[] id = this.identityBounds();
      boolean idHovered = this.hover(mouseX, mouseY, id);
      int idBorder = this.accountsOpen ? Theme.accent() : (idHovered ? Ui.mix(Theme.border(), Theme.accent(), 0.4F) : Theme.border());
      Ui.surface(context, id[0], id[1], id[2], id[3], 8, idBorder, Ui.rowColor(idHovered), Ui.rowColor(idHovered), idHovered, 1.0F);
      boolean notice = System.currentTimeMillis() < this.identityNoticeUntil;
      String arrow = this.accountsOpen ? "▴" : "▾";
      if (this.compactRail()) {
         PlayerHead.draw(context, id[0] + 5, id[1] + 4, 14);
         String label = notice ? this.identityNotice : PlayerHead.username();
         int room = id[2] - 24 - this.font.width(arrow) - 6;
         context.drawString(this.font, this.font.plainSubstrByWidth(label, room), id[0] + 24, id[1] + 7, notice ? Theme.danger() : Theme.textPrimary(), false);
         context.drawString(this.font, arrow, id[0] + id[2] - 5 - this.font.width(arrow), id[1] + 7, Theme.textMuted(), false);
      } else {
         PlayerHead.draw(context, id[0] + 6, id[1] + 7, 16);
         int textRoom = id[2] - 30;
         context.drawString(this.font, this.font.plainSubstrByWidth(PlayerHead.username(), textRoom), id[0] + 28, id[1] + 6, Theme.textPrimary(), false);
         String sub = notice ? this.identityNotice : "Switch account " + arrow;
         context.drawString(this.font, this.font.plainSubstrByWidth(sub, textRoom), id[0] + 28, id[1] + 17, notice ? Theme.danger() : Theme.textMuted(), false);
      }

      if (!this.compactRail()) {
         context.drawString(this.font, "MODULES", this.railInnerX() + 4, this.navTop() - 12, Theme.textMuted(), false);
      }

      Category[] categories = Category.tabs();
      int navH = this.navH();
      int icon = navH < 16 ? 10 : 14;
      int[] target = this.onWardrobe() ? this.wardrobeNavBounds() : this.navBounds(selectedCategory.ordinal());
      int highlightY = Math.round(this.motion.follow("nav", target[1], 18.0F));
      ThemeRenderer.fillRounded(context, target[0], highlightY, target[2], navH, Theme.accentRowBg(), Math.min(6, navH / 2));
      ThemeRenderer.fillRounded(context, target[0], highlightY + navH / 4, 3, navH - navH / 2, Theme.accent(), 1);

      for (int i = 0; i < categories.length; i++) {
         Category category = categories[i];
         int[] b = this.navBounds(i);
         float p = Ui.stagger(intro * 0.6F, i, 0.03F, 0.25F);
         int bx = b[0] - Math.round((1.0F - p) * 8.0F);
         boolean active = !this.onWardrobe() && category == selectedCategory;
         boolean hovered = !active && this.hover(mouseX, mouseY, b);
         if (hovered) {
            ThemeRenderer.fillRounded(context, b[0], b[1], b[2], b[3], Theme.hoverBg(), 6);
         }

         int color = active ? Theme.accent() : (hovered ? Theme.textPrimary() : Theme.textSecondary());
         int textY = b[1] + (navH - 8) / 2 + 1;
         AuroraIcons.draw(context, glyphFor(category), bx + 8, b[1] + (navH - icon) / 2, icon, Ui.alpha(color, p));
         context.drawString(this.font, category.title, bx + 28, textY, Ui.alpha(active ? Theme.textPrimary() : color, p), false);
         String count = String.valueOf(ModuleManager.byCategory(category).size());
         context.drawString(this.font, count, b[0] + b[2] - 6 - this.font.width(count), textY, Ui.alpha(Theme.textMuted(), p), false);
      }

      if (this.wardrobe != null) {
         if (!this.compactRail()) {
            context.drawString(this.font, "STYLE", this.railInnerX() + 4, this.wardrobeLabelY(), Theme.textMuted(), false);
         } else {
            int lineY = this.wardrobeNavBounds()[1] - 3;
            context.fill(this.railInnerX() + 6, lineY, this.railInnerX() + this.railInnerW() - 6, lineY + 1, Theme.sectionDivider());
         }

         int[] b = this.wardrobeNavBounds();
         boolean active = this.onWardrobe();
         boolean hovered = !active && this.hover(mouseX, mouseY, b);
         if (hovered) {
            ThemeRenderer.fillRounded(context, b[0], b[1], b[2], b[3], Theme.hoverBg(), 6);
         }

         int color = active ? Theme.accent() : (hovered ? Theme.textPrimary() : Theme.textSecondary());
         AuroraIcons.draw(context, "spark", b[0] + 8, b[1] + (navH - icon) / 2, icon, color);
         context.drawString(this.font, "Cosmetics", b[0] + 28, b[1] + (navH - 8) / 2 + 1, active ? Theme.textPrimary() : color, false);
      }

      for (int i = 0; i < TOOL_GLYPHS.length; i++) {
         int[] b = this.toolBounds(i);
         boolean hovered = this.hover(mouseX, mouseY, b);
         ThemeRenderer.row(context, b[0], b[1], b[2], b[3], hovered, Theme.trackBg(), Theme.rowBgHover(), 6);
         AuroraIcons.draw(context, TOOL_GLYPHS[i], b[0] + (b[2] - 14) / 2, b[1] + 4, 14, hovered ? Theme.accent() : Theme.textSecondary());
      }
   }

   private void renderHeader(GuiGraphics context, int mouseX, int mouseY) {
      int x = this.contentX();
      float p = Ui.ease((float)(System.nanoTime() - this.cardsStart) / 1.0E9F / 0.25F);
      int slide = Math.round((1.0F - p) * 6.0F);
      String title = this.searchText.isEmpty() ? selectedCategory.title : "Search";
      Ui.bigText(context, this.font, title, x + slide, this.panelY + 17, 2.0F, Ui.alpha(Theme.textPrimary(), p));
      List<Module> modules = this.modules();
      int on = 0;

      for (Module module : modules) {
         if (module.isEnabled()) {
            on++;
         }
      }

      String subtitle = this.searchText.isEmpty()
         ? modules.size() + (modules.size() == 1 ? " module" : " modules") + "  ·  " + on + " on"
         : modules.size() + (modules.size() == 1 ? " match" : " matches") + " in " + selectedCategory.title;
      context.drawString(this.font, subtitle, x + slide, this.panelY + 39, Ui.alpha(Theme.textSecondary(), p), false);
      this.renderSearchBox(context, mouseX, mouseY, this.searchFocused, this.searchText, "Search modules", "Ctrl F");
      context.fill(x, this.panelY + 60 - 6, x + this.contentW(), this.panelY + 60 - 5, Theme.sectionDivider());
   }

   private void renderSearchBox(GuiGraphics context, int mouseX, int mouseY, boolean focused, String text, String placeholder, String hint) {
      int[] s = this.searchBounds();
      boolean hovered = this.hover(mouseX, mouseY, s);
      int border = focused ? Theme.accent() : (hovered ? Ui.mix(Theme.border(), Theme.textSecondary(), 0.3F) : Theme.border());
      ThemeRenderer.fillRoundedBorder(context, s[0], s[1], s[2], s[3], border, Theme.trackBg(), 7);
      AuroraIcons.draw(context, "zoom", s[0] + 6, s[1] + 5, 12, focused ? Theme.accent() : Theme.textMuted());
      String shown = text.isEmpty() ? placeholder : text;
      if (focused && System.currentTimeMillis() / 500L % 2L == 0L) {
         shown = shown + "_";
      }

      boolean showHint = text.isEmpty() && hint != null;
      int hintRoom = showHint ? this.font.width(hint) + 18 : 8;
      context.drawString(
         this.font,
         this.font.plainSubstrByWidth(shown, s[2] - 22 - hintRoom),
         s[0] + 22,
         s[1] + 7,
         text.isEmpty() ? Theme.textMuted() : Theme.textPrimary(),
         false
      );
      if (showHint) {
         int hw = this.font.width(hint) + 8;
         ThemeRenderer.fillRounded(context, s[0] + s[2] - hw - 5, s[1] + 5, hw, 12, Ui.rowColor(false), 4);
         context.drawString(this.font, hint, s[0] + s[2] - hw - 1, s[1] + 7, Theme.textMuted(), false);
      }
   }

   private void renderWardrobeHeader(GuiGraphics context, int mouseX, int mouseY) {
      int x = this.contentX();
      float p = Ui.ease((float)(System.nanoTime() - this.cardsStart) / 1.0E9F / 0.25F);
      int room = this.searchBounds()[0] - x - 10;
      Ui.bigText(context, this.font, "Cosmetics", x + Math.round((1.0F - p) * 6.0F), this.panelY + 17, 2.0F, Ui.alpha(Theme.textPrimary(), p));
      context.drawString(
         this.font,
         this.font.plainSubstrByWidth("Click anything to put it on. Every figure is you.", room),
         x,
         this.panelY + 39,
         Ui.alpha(Theme.textSecondary(), p),
         false
      );
      this.renderSearchBox(context, mouseX, mouseY, this.cosmeticSearchFocused, this.wardrobe.query(), "Search cosmetics", null);
      context.fill(x, this.panelY + 60 - 6, x + this.contentW(), this.panelY + 60 - 5, Theme.sectionDivider());
   }

   private void renderCards(GuiGraphics context, int mouseX, int mouseY) {
      int top = this.gridTop();
      int bottom = this.gridBottom();
      float elapsed = (float)(System.nanoTime() - this.cardsStart) / 1.0E9F;
      boolean inGrid = mouseY >= top && mouseY < bottom;
      context.enableScissor(this.panelX + this.railW() + 1, top - 3, this.panelX + this.panelW - 1, bottom);
      if (this.hasHudRow()) {
         this.renderHudBanner(context, this.hudBannerBounds(), inGrid ? mouseX : -10000, mouseY, elapsed);
      }

      List<Module> modules = this.modules();

      for (int i = 0; i < modules.size(); i++) {
         int[] b = this.cardBounds(i);
         if (b[1] + b[3] + 4 >= top && b[1] - 4 <= bottom) {
            int row = Math.max(0, (b[1] - top) / 76);
            float p = Ui.stagger(elapsed, row * this.columns() + i % this.columns() + (this.hasHudRow() ? 1 : 0), 0.025F, 0.32F);
            this.renderCard(context, modules.get(i), b, inGrid ? mouseX : -10000, mouseY, p);
         }
      }

      if (modules.isEmpty() && !this.hasHudRow()) {
         int cx = this.contentX() + this.gridW() / 2;
         int cy = top + (bottom - top) / 2 - 20;
         AuroraIcons.draw(context, this.searchText.isEmpty() ? glyphFor(selectedCategory) : "zoom", cx - 12, cy, 24, Theme.textMuted());
         String message;
         if (!this.searchText.isEmpty()) {
            message = "Nothing matches \"" + this.searchText + "\"";
         } else if (selectedCategory == Category.FAVORITE) {
            message = "Star a module and it will wait for you here";
         } else if (selectedCategory == Category.ACTIVE) {
            message = "Switch a module on and it will show up here";
         } else {
            message = "Nothing in " + selectedCategory.title + " yet";
         }

         context.drawCenteredString(this.font, message, cx, cy + 32, Theme.textSecondary());
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackX = this.contentX() + this.contentW() - 3;
         int trackH = bottom - top;
         ThemeRenderer.fillRounded(context, trackX, top, 3, trackH, Theme.trackBg(), 1);
         int handleH = Math.max(24, trackH * trackH / (trackH + maxScroll));
         int handleY = top + Math.round((trackH - handleH) * ((float)this.scroll() / maxScroll));
         ThemeRenderer.fillRounded(context, trackX, handleY, 3, handleH, Theme.accent(), 1);
      }
   }

   private void renderHudBanner(GuiGraphics context, int[] b, int mouseX, int mouseY, float elapsed) {
      float p = Ui.stagger(elapsed, 0, 0.025F, 0.32F);
      int y = b[1] + Math.round((1.0F - p) * 12.0F);
      boolean hovered = this.hover(mouseX, mouseY, b);
      float lift = this.motion.follow("hudBanner", hovered ? 1.0F : 0.0F, 14.0F);
      Ui.surface(
         context,
         b[0],
         y,
         b[2],
         b[3],
         9,
         Ui.mix(Theme.border(), Theme.accent(), 0.5F + lift * 0.5F),
         Ui.mix(Ui.rowColor(false), Theme.accent(), 0.22F + lift * 0.1F),
         Ui.rowColor(false),
         hovered,
         p
      );
      AuroraIcons.draw(context, "move", b[0] + 12, y + 10, 16, Ui.alpha(Theme.accent(), p));
      context.drawString(this.font, "Edit HUD Positions", b[0] + 36, y + 8, Ui.alpha(Theme.textPrimary(), p), false);
      context.drawString(this.font, "Drag every HUD element where you want it", b[0] + 36, y + 19, Ui.alpha(Theme.textSecondary(), p), false);
      String open = "Open editor  →";
      int arrowShift = Math.round(lift * 3.0F);
      context.drawString(this.font, open, b[0] + b[2] - 14 - this.font.width(open) + arrowShift, y + 14, Ui.alpha(Theme.accent(), p), false);
   }

   private void renderCard(GuiGraphics context, Module module, int[] b, int mouseX, int mouseY, float p) {
      boolean hovered = this.hover(mouseX, mouseY, b);
      float lift = this.motion.follow("lift" + module.name, hovered ? 1.0F : 0.0F, 14.0F);
      float on = this.motion.follow(module, module.isEnabled() ? 1.0F : 0.0F, 14.0F);
      int x = b[0];
      int y = b[1] + Math.round((1.0F - p) * 14.0F) - Math.round(lift * 2.0F);
      int w = b[2];
      int h = b[3];
      if (lift > 0.01F) {
         ThemeRenderer.fillRounded(context, x + 1, y + 4, w, h, Ui.alpha(Theme.shadow(), lift * p), 10);
      }

      if (on > 0.01F) {
         ThemeRenderer.fillRounded(context, x - 2, y - 2, w + 4, h + 4, Ui.alpha(Theme.accent(), 0.16F * on * p), 11);
      }

      int border = Ui.mix(Ui.mix(Theme.border(), Theme.textMuted(), lift * 0.35F), Theme.accent(), on * 0.8F);
      int topColor = Ui.mix(Ui.mix(Ui.rowColor(false), Ui.rowColor(true), lift), Theme.accent(), 0.1F * on);
      Ui.surface(context, x, y, w, h, 9, border, topColor, Ui.mix(Ui.rowColor(false), Theme.panelBg(), 0.5F), hovered, p);
      int badge = Ui.mix(Theme.trackBg(), Theme.accent(), 0.28F * on + 0.04F);
      ThemeRenderer.fillRounded(context, x + 10, y + 10, 24, 24, Ui.alpha(badge, p), 7);
      String glyph = AuroraIcons.glyphFor(module.name);
      int glyphColor = Ui.alpha(Ui.mix(Theme.textSecondary(), Theme.accent(), on), p);
      if (glyph.isEmpty()) {
         context.drawCenteredString(this.font, module.name.substring(0, 1), x + 22, y + 18, glyphColor);
      } else {
         AuroraIcons.draw(context, glyph, x + 14, y + 14, 16, glyphColor);
      }

      int[] star = this.starBounds(new int[]{x, y, w, h});
      int textW = star[0] - 6 - (x + 42);
      String tag = module.tagNew ? "New" : (module.tagUpdated ? "Updated" : null);
      int tagW = tag == null ? 0 : this.font.width(tag) + 8;
      String name = this.font.plainSubstrByWidth(module.name, tag == null ? textW : Math.max(10, textW - tagW - 5));
      context.drawString(this.font, name, x + 42, y + 11, Ui.alpha(Theme.textPrimary(), p), false);
      if (tag != null) {
         int tagX = x + 42 + this.font.width(name) + 5;
         ThemeRenderer.fillRounded(context, tagX, y + 9, tagW, 11, Ui.alpha(Ui.mix(Theme.trackBg(), Theme.accent2(), 0.35F), p), 4);
         context.drawString(this.font, tag, tagX + 4, y + 11, Ui.alpha(Theme.accent2(), p), false);
      }

      if (p > 0.05F) {
         ThemeRenderer.star(context, this.font, star[0], star[1], module.favorite, Ui.inside(mouseX, mouseY, star));
      }

      List<FormattedCharSequence> lines = this.descriptionLines(module, w - 52);

      for (int i = 0; i < Math.min(2, lines.size()); i++) {
         context.drawString(this.font, lines.get(i), x + 42, y + 23 + i * 10, Ui.alpha(Theme.textSecondary(), p), false);
      }

      int footerY = y + h - 21;
      context.fill(x + 10, footerY, x + w - 10, footerY + 1, Ui.alpha(Theme.sectionDivider(), p));
      int[] link = this.settingsLinkBounds(new int[]{x, y, w, h});
      if (hasSettings(module)) {
         boolean linkHovered = Ui.inside(mouseX, mouseY, link);
         if (linkHovered) {
            ThemeRenderer.fillRounded(context, link[0], link[1], link[2], link[3], Theme.hoverBg(), 4);
         }

         context.drawString(this.font, "Settings ›", link[0] + 4, link[1] + 4, Ui.alpha(linkHovered ? Theme.accent() : Theme.textMuted(), p), false);
      }

      int[] toggle = this.toggleBounds(new int[]{x, y, w, h});
      String state = module.isEnabled() ? "On" : "Off";
      context.drawString(
         this.font, state, toggle[0] - 6 - this.font.width(state), toggle[1] + 3, Ui.alpha(Ui.mix(Theme.textMuted(), Theme.accent(), on), p), false
      );
      Ui.toggle(context, toggle[0], toggle[1], on, false);
   }

   private List<FormattedCharSequence> descriptionLines(Module module, int width) {
      CarrotClickGuiScreen.WrappedText cached = this.wrapped.get(module);
      if (cached == null || cached.width() != width) {
         List<FormattedCharSequence> lines = new ArrayList<>(this.font.split(FormattedText.of(module.description), width));
         if (lines.size() > 2) {
            String rest = module.description;
            List<FormattedText> parts = this.font.getSplitter().splitLines(rest, width, Style.EMPTY);
            String second = parts.size() > 1 ? parts.get(1).getString() : "";
            lines.set(1, FormattedCharSequence.forward(this.font.plainSubstrByWidth(second, width - this.font.width("…")) + "…", Style.EMPTY));
         }

         cached = new CarrotClickGuiScreen.WrappedText(width, lines);
         this.wrapped.put(module, cached);
      }

      return cached.lines();
   }

   private void openAccountSwitcher() {
      if (PipAccounts.available()) {
         this.accountsOpen = !this.accountsOpen;
         this.accountsAnim.restart();
      } else {
         this.identityNotice = "Unavailable";
         this.identityNoticeUntil = System.currentTimeMillis() + 3000L;
      }
   }

   private String accountsStatus() {
      String status = PipAccounts.status();
      return !status.isEmpty() ? status : (PipAccounts.accounts().isEmpty() ? "No accounts added yet" : "");
   }

   private int menuRows() {
      return Math.min(8, PipAccounts.accounts().size());
   }

   private int[] accountsBounds() {
      int statusH = this.accountsStatus().isEmpty() ? 0 : 14;
      int h = 24 + this.menuRows() * 26 + 24 + 8 + statusH;
      int[] identity = this.identityBounds();
      int y = Math.min(identity[1], this.height - h - 4);
      return new int[]{identity[0] + identity[2] + 16, y + this.accountsAnim.drop(6), 196, h};
   }

   private int[] accountRowBounds(int index) {
      int[] menu = this.accountsBounds();
      return new int[]{menu[0] + 6, menu[1] + 22 + index * 26, 184, 24};
   }

   private int[] accountRemoveBounds(int[] row) {
      int w = this.font.width("Remove") + 12;
      return new int[]{row[0] + row[2] - w - 4, row[1] + 5, w, row[3] - 10};
   }

   private int[] addAccountBounds() {
      int[] menu = this.accountsBounds();
      return new int[]{menu[0] + 6, menu[1] + 24 + this.menuRows() * 26, 184, 22};
   }

   private void renderAccounts(GuiGraphics context, int mouseX, int mouseY) {
      int[] menu = this.accountsBounds();
      float p = this.accountsAnim.eased();
      ThemeRenderer.fillRounded(context, menu[0] + 3, menu[1] + 5, menu[2], menu[3], Ui.alpha(Theme.shadow(), p), 11);
      ThemeRenderer.fillRoundedBorder(context, menu[0], menu[1], menu[2], menu[3], Theme.modalBorder(), Theme.modalPanelBg() | 0xFF000000, 10);
      context.drawString(this.font, "ACCOUNTS", menu[0] + 10, menu[1] + 8, Theme.textMuted(), false);
      List<Account> accounts = PipAccounts.accounts();
      String count = String.valueOf(accounts.size());
      context.drawString(this.font, count, menu[0] + menu[2] - 10 - this.font.width(count), menu[1] + 8, Theme.textMuted(), false);

      for (int i = 0; i < this.menuRows(); i++) {
         Account account = accounts.get(i);
         int[] row = this.accountRowBounds(i);
         boolean current = PipAccounts.isCurrent(account);
         boolean hovered = this.hover(mouseX, mouseY, row);
         ThemeRenderer.fillRounded(context, row[0], row[1], row[2], row[3], current ? Theme.accentRowBg() : Ui.rowColor(hovered), 6);
         if (current) {
            ThemeRenderer.fillRounded(context, row[0], row[1] + 6, 3, row[3] - 12, Theme.accent(), 1);
         }

         PlayerHead.drawFace(context, PipAccounts.skin(account), row[0] + 6, row[1] + 4, 16);
         int[] remove = this.accountRemoveBounds(row);
         String name = account.name() + (PipAccounts.isPending(account) ? " (next)" : "");
         context.drawString(
            this.font,
            this.font.plainSubstrByWidth(name, remove[0] - 6 - (row[0] + 28)),
            row[0] + 28,
            row[1] + 8,
            current ? Theme.accent() : Theme.textPrimary(),
            false
         );
         boolean removeHovered = Ui.inside(mouseX, mouseY, remove);
         int redTint = Theme.danger() & 16777215 | (removeHovered ? 1073741824 : 503316480);
         ThemeRenderer.fillRounded(context, remove[0], remove[1], remove[2], remove[3], Theme.danger(), 5);
         ThemeRenderer.fillRounded(context, remove[0] + 1, remove[1] + 1, remove[2] - 2, remove[3] - 2, Theme.modalPanelBg() | 0xFF000000, 4);
         ThemeRenderer.fillRounded(context, remove[0] + 1, remove[1] + 1, remove[2] - 2, remove[3] - 2, redTint, 4);
         context.drawCenteredString(this.font, "Remove", remove[0] + remove[2] / 2, remove[1] + (remove[3] - 8) / 2, Theme.danger());
      }

      int[] add = this.addAccountBounds();
      boolean addHovered = this.hover(mouseX, mouseY, add);
      ThemeRenderer.fillRounded(context, add[0], add[1], add[2], add[3], addHovered ? Theme.accent() : Ui.mix(Theme.border(), Theme.accent(), 0.6F), 6);
      ThemeRenderer.fillRounded(
         context,
         add[0] + 1,
         add[1] + 1,
         add[2] - 2,
         add[3] - 2,
         addHovered ? Ui.mix(Theme.modalPanelBg() | 0xFF000000, Theme.accent(), 0.25F) : Theme.modalPanelBg() | 0xFF000000,
         5
      );
      context.drawCenteredString(this.font, "+  Add account", add[0] + add[2] / 2, add[1] + 7, addHovered ? Theme.textPrimary() : Theme.accent());
      String status = this.accountsStatus();
      if (!status.isEmpty()) {
         context.drawCenteredString(this.font, this.font.plainSubstrByWidth(status, 184), menu[0] + 98, menu[1] + menu[3] - 13, Theme.textSecondary());
      }
   }

   private boolean clickAccounts(int mx, int my) {
      int[] menu = this.accountsBounds();
      if (Ui.inside(mx, my, menu)) {
         List<Account> accounts = PipAccounts.accounts();

         for (int i = 0; i < this.menuRows(); i++) {
            int[] row = this.accountRowBounds(i);
            if (Ui.inside(mx, my, row)) {
               if (Ui.inside(mx, my, this.accountRemoveBounds(row))) {
                  PipAccounts.remove(accounts.get(i));
               } else {
                  PipAccounts.select(accounts.get(i));
               }

               return true;
            }
         }

         if (Ui.inside(mx, my, this.addAccountBounds())) {
            Minecraft.getInstance().setScreen(new AccountSignInScreen(this));
         }

         return true;
      } else if (!Ui.inside(mx, my, this.identityBounds())) {
         this.accountsOpen = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      this.computeLayout();
      int mx = (int)Math.round(click.x());
      int my = (int)Math.round(click.y());
      if (this.accountsOpen && this.clickAccounts(mx, my)) {
         return true;
      } else {
         for (int i = 0; i < TOOL_GLYPHS.length; i++) {
            if (Ui.inside(mx, my, this.toolBounds(i))) {
               this.useTool(i);
               return true;
            }
         }

         if (Ui.inside(mx, my, this.brandBounds())) {
            Minecraft.getInstance().setScreen(new PatchNotesScreen(this));
            return true;
         } else if (Ui.inside(mx, my, this.identityBounds())) {
            this.openAccountSwitcher();
            return true;
         } else {
            Category[] categories = Category.tabs();

            for (int ix = 0; ix < categories.length; ix++) {
               if (Ui.inside(mx, my, this.navBounds(ix))) {
                  selectedCategory = categories[ix];
                  wardrobePage = false;
                  this.searchFocused = false;
                  this.cosmeticSearchFocused = false;
                  return true;
               }
            }

            if (this.wardrobe != null && Ui.inside(mx, my, this.wardrobeNavBounds())) {
               wardrobePage = true;
               this.searchFocused = false;
               this.cosmeticSearchFocused = false;
               return true;
            } else if (this.onWardrobe()) {
               if (Ui.inside(mx, my, this.searchBounds())) {
                  this.cosmeticSearchFocused = true;
                  return true;
               } else {
                  this.cosmeticSearchFocused = false;
                  return this.wardrobe.mouseClicked(click.x(), click.y(), click.button()) || super.mouseClicked(click, doubled);
               }
            } else if (Ui.inside(mx, my, this.searchBounds())) {
               this.searchFocused = true;
               return true;
            } else {
               this.searchFocused = false;
               if (my >= this.gridTop() && my < this.gridBottom()) {
                  if (this.hasHudRow() && Ui.inside(mx, my, this.hudBannerBounds())) {
                     Config.save();
                     Minecraft.getInstance().setScreen(new HudEditorScreen(this));
                     return true;
                  }

                  List<Module> modules = this.modules();

                  for (int ixx = 0; ixx < modules.size(); ixx++) {
                     int[] b = this.cardBounds(ixx);
                     if (Ui.inside(mx, my, b)) {
                        Module module = modules.get(ixx);
                        if (click.button() != 1 && (!hasSettings(module) || !Ui.inside(mx, my, this.settingsLinkBounds(b)))) {
                           if (ThemeRenderer.inStar(mx, my, this.starBounds(b)[0], this.starBounds(b)[1])) {
                              module.favorite = !module.favorite;
                              this.wrapped.remove(module);
                              Config.save();
                           } else {
                              module.setEnabled(!module.isEnabled());
                              Config.save();
                           }
                        } else {
                           this.openModuleSettings(module);
                        }

                        return true;
                     }
                  }
               }

               return super.mouseClicked(click, doubled);
            }
         }
      }
   }

   private void useTool(int index) {
      Minecraft client = Minecraft.getInstance();
      switch (index) {
         case 0:
            client.setScreen(new ThemeScreen(this));
            break;
         case 1:
            Config.save();
            client.setScreen(new HudEditorScreen(this));
            break;
         case 2:
            client.setScreen(new SearchScreen(this));
            break;
         default:
            Module board = ModuleManager.get("Leaderboard");
            Screen screen = board == null ? null : board.dedicatedSettingsScreen(this);
            if (screen != null) {
               client.setScreen(screen);
            }
      }
   }

   private void openModuleSettings(Module module) {
      if (module instanceof CosmeticsModule && this.wardrobe != null) {
         wardrobePage = true;
      } else {
         Screen dedicated = module.dedicatedSettingsScreen(this);
         if (dedicated != null) {
            Minecraft.getInstance().setScreen(dedicated);
         } else if (!module.getSettings().isEmpty()) {
            Minecraft.getInstance().setScreen(new ModuleSettingsScreen(this, module));
         }
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
      return this.onWardrobe() && this.wardrobe.mouseDragged(click.x(), click.y()) ? true : super.mouseDragged(click, offsetX, offsetY);
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (this.onWardrobe()) {
         this.wardrobe.mouseReleased();
      }

      return super.mouseReleased(click);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.computeLayout();
      if (this.onWardrobe()) {
         return this.wardrobe.mouseScrolled(mouseX, mouseY, verticalAmount);
      } else {
         int i = selectedCategory.ordinal();
         this.scrollOffsets[i].setTarget(Mth.clamp(this.scrollOffsets[i].target() - (int)(verticalAmount * 76.0 / 2.0), 0, this.maxScroll()));
         return true;
      }
   }

   public boolean charTyped(CharacterEvent input) {
      if (this.onWardrobe()) {
         if (!this.cosmeticSearchFocused) {
            if (!input.isAllowedChatCharacter() || input.codepoint() == 167 || Character.isWhitespace(input.codepoint())) {
               return super.charTyped(input);
            }

            this.cosmeticSearchFocused = true;
         }

         String query = this.wardrobe.query();
         if (query.length() < 32 && input.isAllowedChatCharacter() && input.codepoint() != 167) {
            this.wardrobe.setQuery(new StringBuilder(query).appendCodePoint(input.codepoint()).toString());
         }

         return true;
      } else {
         if (!this.searchFocused) {
            if (!input.isAllowedChatCharacter() || input.codepoint() == 167 || Character.isWhitespace(input.codepoint())) {
               return super.charTyped(input);
            }

            this.searchFocused = true;
            wardrobePage = false;
         }

         if (this.searchText.length() < 32 && input.isAllowedChatCharacter() && input.codepoint() != 167) {
            this.searchText = new StringBuilder(this.searchText).appendCodePoint(input.codepoint()).toString();
            this.scrollOffsets[selectedCategory.ordinal()].jump(0);
         }

         return true;
      }
   }

   public boolean keyPressed(KeyEvent input) {
      int key = input.key();
      if (SearchScreen.opensFrom(input, this)) {
         return true;
      } else if (this.onWardrobe() && this.cosmeticSearchFocused) {
         String query = this.wardrobe.query();
         if (key == 259) {
            if (!query.isEmpty()) {
               this.wardrobe.setQuery(query.substring(0, query.length() - 1));
            }
         } else if (key == 256) {
            if (query.isEmpty()) {
               this.cosmeticSearchFocused = false;
            } else {
               this.wardrobe.setQuery("");
            }
         } else if (key == 257 || key == 335) {
            this.cosmeticSearchFocused = false;
         }

         return true;
      } else if (this.searchFocused) {
         if (key == 259) {
            if (!this.searchText.isEmpty()) {
               this.searchText = this.searchText.substring(0, this.searchText.length() - 1);
            }
         } else if (key == 256) {
            if (this.searchText.isEmpty()) {
               this.searchFocused = false;
            } else {
               this.searchText = "";
            }
         } else if (key == 257 || key == 335) {
            this.searchFocused = false;
         }

         return true;
      } else if (key != 256) {
         return super.keyPressed(input);
      } else {
         if (this.accountsOpen) {
            this.accountsOpen = false;
         } else if (!this.onWardrobe() || !this.wardrobe.escape()) {
            this.onClose();
         }

         return true;
      }
   }

   public void onClose() {
      Config.save();
      super.onClose();
   }

   private record WrappedText(int width, List<FormattedCharSequence> lines) {
   }
}
