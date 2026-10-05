package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.Config;
import dev.lyfw.lyfwclient.LyfwClient;
import dev.lyfw.lyfwclient.module.modules.CosmeticsModule;
import dev.lyfw.lyfwclient.render.CosmeticLoadout;
import dev.lyfw.lyfwclient.render.CosmeticPreview;
import dev.lyfw.lyfwclient.render.Trails;
import dev.lyfw.lyfwclient.setting.BooleanSetting;
import dev.lyfw.lyfwclient.setting.ColorSetting;
import dev.lyfw.lyfwclient.setting.SliderSetting;
import dev.lyfw.lyfwclient.stats.CosmeticSync;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerSkin;

public final class WardrobePanel {
   private static final int GAP = 6;
   private static final int MIN_TILE = 76;
   private static final int CELL = 6;
   private static WardrobePanel.Section section = WardrobePanel.Section.HEADWEAR;
   private final CosmeticsModule cosmetics;
   private final Motion motion = new Motion();
   private final Scroll scroll = new Scroll();
   private final ColorPopover popover = new ColorPopover();
   private final Map<String, CosmeticPreview.State> states = new HashMap<>();
   private final Map<Integer, float[]> tileSpin = new HashMap<>();
   private long sectionStart = System.nanoTime();
   private boolean drawerOpen;
   private float stageSpin = 25.0F;
   private float stageVelocity = 24.0F;
   private boolean draggingStage;
   private double lastDragX;
   private SliderSetting draggingSlider;
   private int[] draggingSliderBounds;
   private int paintMode;
   private CosmeticsModule.Headwear focusHeadwear;
   private CosmeticsModule.WingType focusWings;
   private CosmeticsModule.Pet focusPet;
   private final SliderSetting headwearSize = new SliderSetting("Size", 1.0, 0.4F, 2.5, 0.05, "x");
   private final SliderSetting wingSize = new SliderSetting("Size", 1.0, 0.4F, 3.0, 0.05, "x");
   private final SliderSetting petSize = new SliderSetting("Size", 1.0, 0.4F, 2.5, 0.05, "x");
   private final BooleanSetting petNatural = new BooleanSetting("Natural colors", true);
   private final ColorSetting petColor = new ColorSetting("Color", -1).exemptFromGlobalColor();
   private static final String[] PLACES = new String[]{"Ground", "Shoulder", "Head"};
   private final SliderSetting xAngle = new SliderSetting("X angle", 0.0, -180.0, 180.0, 1.0, "°");
   private final SliderSetting yAngle = new SliderSetting("Y angle", 0.0, -180.0, 180.0, 1.0, "°");
   private Font tr;
   private int x;
   private int y;
   private int w;
   private int h;
   private int screenW;
   private int screenH;
   private static WardrobePanel.CapeFilter capeFilter = WardrobePanel.CapeFilter.ANIMATED;
   private WardrobePanel.Section lastKind;
   private String query = "";

   public WardrobePanel(CosmeticsModule cosmetics) {
      this.cosmetics = cosmetics;
      capeFilter = WardrobePanel.CapeFilter.ANIMATED;
   }

   private WardrobePanel.Section drawerKind() {
      return section != WardrobePanel.Section.FAVORITES && !this.searching() ? section : this.lastKind;
   }

   public String query() {
      return this.query;
   }

   public void setQuery(String query) {
      if (!query.equals(this.query)) {
         this.query = query;
         this.sectionStart = System.nanoTime();
         this.scroll.jump(0);
         this.drawerOpen = false;
      }
   }

   private boolean searching() {
      return !this.query.isBlank();
   }

   private static String pretty(Enum<?> value) {
      StringBuilder out = new StringBuilder();

      for (String word : value.name().split("_")) {
         out.append(out.isEmpty() ? "" : " ").append(word.charAt(0)).append(word.substring(1).toLowerCase());
      }

      return out.toString();
   }

   private List<WardrobePanel.Item> items() {
      if (this.searching()) {
         String needle = this.query.trim().toLowerCase(Locale.ROOT);
         List<WardrobePanel.Item> matches = new ArrayList<>();

         for (WardrobePanel.Section kind : new WardrobePanel.Section[]{
            WardrobePanel.Section.HEADWEAR,
            WardrobePanel.Section.WINGS,
            WardrobePanel.Section.CAPE,
            WardrobePanel.Section.TRAILS,
            WardrobePanel.Section.PETS,
            WardrobePanel.Section.EMOTES
         }) {
            boolean wholeKind = kind.title.toLowerCase(Locale.ROOT).startsWith(needle);

            for (WardrobePanel.Item item : this.itemsOf(kind)) {
               if (!item.none() && (wholeKind || item.label().toLowerCase(Locale.ROOT).contains(needle))) {
                  matches.add(item);
               }
            }
         }

         return matches;
      } else if (section == WardrobePanel.Section.CAPE) {
         List<WardrobePanel.Item> filtered = new ArrayList<>();

         for (WardrobePanel.Item itemx : this.itemsOf(section)) {
            boolean keep = itemx.none()
               || capeFilter == WardrobePanel.CapeFilter.ALL
               || capeFilter == WardrobePanel.CapeFilter.MINECRAFT && itemx.capeStyle() == CosmeticsModule.CapeStyle.MINECRAFT
               || capeFilter == WardrobePanel.CapeFilter.ANIMATED && itemx.capeStyle() == CosmeticsModule.CapeStyle.ANIMATED;
            if (keep) {
               filtered.add(itemx);
            }
         }

         return filtered;
      } else if (section != WardrobePanel.Section.FAVORITES) {
         return this.itemsOf(section);
      } else {
         List<WardrobePanel.Item> starred = new ArrayList<>();

         for (String key : this.cosmetics.favoriteKeys()) {
            for (WardrobePanel.Section kind : new WardrobePanel.Section[]{
               WardrobePanel.Section.HEADWEAR,
               WardrobePanel.Section.WINGS,
               WardrobePanel.Section.CAPE,
               WardrobePanel.Section.TRAILS,
               WardrobePanel.Section.PETS,
               WardrobePanel.Section.EMOTES
            }) {
               for (WardrobePanel.Item itemxx : this.itemsOf(kind)) {
                  if (!itemxx.none() && itemxx.key().equals(key)) {
                     starred.add(itemxx);
                  }
               }
            }
         }

         return starred;
      }
   }

   private List<WardrobePanel.Item> itemsOf(WardrobePanel.Section kind) {
      List<WardrobePanel.Item> items = new ArrayList<>();
      switch (kind) {
         case HEADWEAR:
            for (CosmeticsModule.Headwear headwear : CosmeticsModule.Headwear.values()) {
               items.add(new WardrobePanel.Item(kind, pretty(headwear), headwear, null, null, null, null, null, headwear == CosmeticsModule.Headwear.NONE));
            }
            break;
         case WINGS:
            items.add(new WardrobePanel.Item(kind, "None", null, null, null, null, null, null, true));

            for (CosmeticsModule.WingType type : CosmeticsModule.WingType.values()) {
               items.add(new WardrobePanel.Item(kind, pretty(type), null, type, null, null, null, null, false));
            }
            break;
         case CAPE:
            items.add(new WardrobePanel.Item(kind, "None", null, null, null, null, null, null, true));
            items.add(new WardrobePanel.Item(kind, "Solid Color", null, null, CosmeticsModule.CapeStyle.COLOR, null, null, null, false));
            items.add(new WardrobePanel.Item(kind, "Your Drawing", null, null, CosmeticsModule.CapeStyle.DRAWN, null, null, null, false));

            for (String name : this.cosmetics.animatedCapeNames()) {
               items.add(new WardrobePanel.Item(kind, name, null, null, CosmeticsModule.CapeStyle.ANIMATED, name, null, null, false));
            }

            for (String name : this.cosmetics.minecraftCapeNames()) {
               items.add(new WardrobePanel.Item(kind, name, null, null, CosmeticsModule.CapeStyle.MINECRAFT, name, null, null, false));
            }
            break;
         case TRAILS:
            items.add(new WardrobePanel.Item(kind, "None", null, null, null, null, null, null, true));

            for (Trails.Trail trail : Trails.ALL) {
               items.add(new WardrobePanel.Item(kind, trail.label(), null, null, null, null, null, null, false, trail));
            }
            break;
         case PETS:
            items.add(new WardrobePanel.Item(kind, "None", null, null, null, null, null, null, true));

            for (CosmeticsModule.Pet pet : CosmeticsModule.Pet.values()) {
               items.add(new WardrobePanel.Item(kind, pretty(pet), null, null, null, null, pet, null, false));
            }
            break;
         case EMOTES:
            items.add(new WardrobePanel.Item(kind, "None", null, null, null, null, null, null, true));

            for (CosmeticsModule.Emote emote : CosmeticsModule.Emote.values()) {
               items.add(new WardrobePanel.Item(kind, pretty(emote), null, null, null, null, null, emote, false));
            }
      }

      return items;
   }

   private boolean isWorn(WardrobePanel.Item item) {
      return switch (item.kind()) {
         case HEADWEAR -> item.none() ? this.cosmetics.wornHeadwear().isEmpty() : this.cosmetics.isWearing(item.headwear());
         case WINGS -> item.none() ? this.cosmetics.wornWings().isEmpty() : this.cosmetics.isWearing(item.wings());
         case CAPE -> item.none()
            ? !this.cosmetics.capeSetting().get()
            : this.cosmetics.capeSetting().get()
               && this.cosmetics.capeStyle() == item.capeStyle()
               && (
                  item.capeStyle() == CosmeticsModule.CapeStyle.MINECRAFT
                     ? item.capeName().equals(this.cosmetics.selectedMinecraftCape())
                     : item.capeStyle() != CosmeticsModule.CapeStyle.ANIMATED || item.capeName().equals(this.cosmetics.selectedAnimatedCape())
               );
         case TRAILS -> item.none() ? this.cosmetics.trail().isEmpty() : item.trail() == Trails.design(this.cosmetics.trail());
         case PETS -> item.none() ? this.cosmetics.wornPets().isEmpty() : this.cosmetics.isWearing(item.pet());
         case EMOTES -> item.none() ? this.cosmetics.emote() == null : this.cosmetics.emote() == item.emote();
         case FAVORITES, OPTIONS -> false;
      };
   }

   private Identifier capeFor(WardrobePanel.Item item) {
      return item.none() ? null : this.cosmetics.capeTexture(item.capeStyle(), item.capeName());
   }

   private CosmeticLoadout lookFor(WardrobePanel.Item item) {
      if (item.none()) {
         return CosmeticLoadout.EMPTY;
      } else {
         return switch (item.kind()) {
            case HEADWEAR -> new CosmeticLoadout(List.of(this.cosmetics.headwearPiece(item.headwear())), List.of(), this.cosmetics.flapSpeed(), null);
            case WINGS -> new CosmeticLoadout(List.of(), List.of(this.cosmetics.wingPiece(item.wings())), this.cosmetics.flapSpeed(), null);
            case CAPE, EMOTES, FAVORITES, OPTIONS -> CosmeticLoadout.EMPTY;
            case TRAILS -> new CosmeticLoadout(List.of(), List.of(), this.cosmetics.flapSpeed(), null, List.of(), this.trailFor(item));
            case PETS -> new CosmeticLoadout(List.of(), List.of(), this.cosmetics.flapSpeed(), null, List.of(this.cosmetics.petPiece(item.pet())));
         };
      }
   }

   private String trailFor(WardrobePanel.Item item) {
      String worn = this.cosmetics.trail();
      return item.trail() == Trails.design(worn) ? worn : item.trail().id();
   }

   private Identifier tileCape(WardrobePanel.Item item) {
      return item.kind() == WardrobePanel.Section.CAPE ? this.capeFor(item) : null;
   }

   private void pick(WardrobePanel.Item item) {
      this.lastKind = item.kind();
      if (item.kind() == WardrobePanel.Section.EMOTES) {
         this.cosmetics.setEmote(item.none() ? null : item.emote());
         this.drawerOpen = false;
         if (!item.none()) {
            this.cosmetics.setEnabled(true);
         }

         Config.save();
      } else {
         if (item.kind() == WardrobePanel.Section.TRAILS) {
            boolean already = this.isWorn(item);
            if (!already) {
               this.cosmetics.setTrail(item.none() ? "" : item.trail().id());
            }

            this.drawerOpen = this.hasDrawer(item) && (!already || !this.drawerOpen);
         } else if (item.kind() == WardrobePanel.Section.CAPE) {
            boolean already = this.isWorn(item);
            this.cosmetics.capeSetting().set(!item.none());
            if (!item.none()) {
               this.cosmetics.selectCape(item.capeStyle(), item.capeName());
            }

            this.drawerOpen = this.hasDrawer(item) && (!already || !this.drawerOpen);
         } else if (item.none()) {
            if (item.kind() == WardrobePanel.Section.HEADWEAR) {
               this.cosmetics.takeOffAllHeadwear();
            } else if (item.kind() == WardrobePanel.Section.PETS) {
               this.cosmetics.takeOffAllPets();
            } else {
               this.cosmetics.takeOffAllWings();
            }

            this.drawerOpen = false;
         } else {
            Enum<?> piece = item.piece();
            if (!this.cosmetics.isWearing(piece)) {
               this.cosmetics.putOn(piece);
               this.focus(piece);
               this.drawerOpen = true;
            } else if (this.drawerOpen && this.isFocused(piece)) {
               this.cosmetics.takeOff(piece);
               this.drawerOpen = false;
            } else {
               this.focus(piece);
               this.drawerOpen = true;
            }
         }

         if (!item.none() && this.isWorn(item)) {
            this.cosmetics.setEnabled(true);
         }

         Config.save();
      }
   }

   private void focus(Enum<?> piece) {
      if (piece instanceof CosmeticsModule.Headwear headwear) {
         this.focusHeadwear = headwear;
      } else if (piece instanceof CosmeticsModule.WingType type) {
         this.focusWings = type;
      } else if (piece instanceof CosmeticsModule.Pet pet) {
         this.focusPet = pet;
      }
   }

   private boolean isFocused(Enum<?> piece) {
      WardrobePanel.Item worn = this.wornItem();
      return worn != null && worn.piece() == piece;
   }

   private void takeOffShown() {
      WardrobePanel.Item worn = this.wornItem();
      if (worn != null) {
         if (worn.kind() == WardrobePanel.Section.TRAILS) {
            this.cosmetics.setTrail("");
         } else if (worn.kind() == WardrobePanel.Section.CAPE) {
            this.cosmetics.capeSetting().set(false);
         } else {
            this.cosmetics.takeOff(worn.piece());
         }

         this.drawerOpen = false;
         Config.save();
      }
   }

   private boolean hasDrawer(WardrobePanel.Item item) {
      return item != null && item.kind() == WardrobePanel.Section.TRAILS
         ? !item.none() && !item.trail().ways().isEmpty()
         : item != null
            && !item.none()
            && item.kind() != WardrobePanel.Section.EMOTES
            && (
               item.kind() != WardrobePanel.Section.CAPE
                  || item.capeStyle() == CosmeticsModule.CapeStyle.COLOR
                  || item.capeStyle() == CosmeticsModule.CapeStyle.DRAWN
            );
   }

   private WardrobePanel.Item wornItem() {
      WardrobePanel.Section kind = this.drawerKind();
      if (kind == null || kind == WardrobePanel.Section.EMOTES || kind == WardrobePanel.Section.FAVORITES || kind == WardrobePanel.Section.OPTIONS) {
         return null;
      } else if (kind == WardrobePanel.Section.HEADWEAR) {
         List<CosmeticsModule.Headwear> worn = this.cosmetics.wornHeadwear();
         CosmeticsModule.Headwear shown = this.focusHeadwear != null && worn.contains(this.focusHeadwear)
            ? this.focusHeadwear
            : (worn.isEmpty() ? null : worn.get(worn.size() - 1));
         return shown == null ? null : new WardrobePanel.Item(kind, pretty(shown), shown, null, null, null, null, null, false);
      } else if (kind == WardrobePanel.Section.WINGS) {
         List<CosmeticsModule.WingType> worn = this.cosmetics.wornWings();
         CosmeticsModule.WingType shown = this.focusWings != null && worn.contains(this.focusWings)
            ? this.focusWings
            : (worn.isEmpty() ? null : worn.get(worn.size() - 1));
         return shown == null ? null : new WardrobePanel.Item(kind, pretty(shown), null, shown, null, null, null, null, false);
      } else if (kind != WardrobePanel.Section.PETS) {
         for (WardrobePanel.Item item : this.itemsOf(kind)) {
            if (!item.none() && this.isWorn(item)) {
               return item;
            }
         }

         return null;
      } else {
         List<CosmeticsModule.Pet> worn = this.cosmetics.wornPets();
         CosmeticsModule.Pet shown = this.focusPet != null && worn.contains(this.focusPet)
            ? this.focusPet
            : (worn.isEmpty() ? null : worn.get(worn.size() - 1));
         return shown == null ? null : new WardrobePanel.Item(kind, pretty(shown), null, null, null, null, shown, null, false);
      }
   }

   private int stageW() {
      return this.w >= 420 ? Mth.clamp(Math.round(this.w * 0.34F), 130, 230) : Math.max(84, Math.round(this.w * 0.36F));
   }

   private int rightX() {
      return this.x + this.stageW() + 12;
   }

   private int rightW() {
      return this.w - this.stageW() - 12;
   }

   private boolean justMe() {
      return this.cosmetics.showOnSetting().get() == CosmeticsModule.ShowOn.JUST_ME;
   }

   private int[] stageBounds() {
      return new int[]{this.x, this.y, this.stageW(), this.h};
   }

   private int[] spinBounds() {
      int[] s = this.stageBounds();
      return new int[]{s[0] + s[2] - 8 - 16, s[1] + 5, 16, 13};
   }

   private int[] masterBounds() {
      int[] s = this.stageBounds();
      return new int[]{s[0] + 8, s[1] + s[3] - 26, s[2] - 16, 20};
   }

   private int[] figureBounds() {
      int[] s = this.stageBounds();
      return new int[]{s[0] + 6, s[1] + 20, s[2] - 12, s[3] - 64};
   }

   private int[] optionRow(int index) {
      int top = this.gridTop() + 4;
      int[] heights = new int[]{this.stackShowOn() ? 36 : 24, this.justMe() ? 0 : 22, 28, 22};

      for (int i = 0; i < index; i++) {
         top += heights[i];
      }

      return new int[]{this.rightX() + 6, top, Math.min(this.rightW() - 12, 280), heights[index]};
   }

   private boolean stackShowOn() {
      int rowW = Math.min(this.rightW() - 12, 280);
      return this.tr.width("Show on") + 8 + this.tr.width("Everyone") + 12 + this.tr.width("Just me") + 12 > rowW;
   }

   private int[] segmentBounds(int index) {
      int[] row = this.optionRow(0);
      int meW = this.tr.width("Just me") + 12;
      int allW = this.tr.width("Everyone") + 12;
      int right = row[0] + row[2];
      int y = row[1] + (this.stackShowOn() ? 16 : 2);
      return index == 1 ? new int[]{right - meW, y, meW, 16} : new int[]{right - meW - allW, y, allW, 16};
   }

   private static String emoteKeys() {
      String play = LyfwClient.emoteKey.getTranslatedKeyMessage().getString();
      String wheel = LyfwClient.emoteWheelKey.getTranslatedKeyMessage().getString();
      return play + " to play, " + wheel + " for the wheel";
   }

   private boolean roomForCount() {
      return this.rightW() >= 340;
   }

   private int[] tabBounds(int index) {
      int room = this.rightW() - 4 - (this.roomForCount() ? 64 : 0);
      int tabW = Math.min(76, room / WardrobePanel.Section.values().length);
      return new int[]{this.rightX() + 2 + index * tabW, this.y + 2, tabW, 18};
   }

   private int[] capeFilterBounds(int index) {
      WardrobePanel.CapeFilter[] filters = WardrobePanel.CapeFilter.values();
      int total = 0;

      for (WardrobePanel.CapeFilter filter : filters) {
         total += this.tr.width(filter.title) + 12;
      }

      boolean full = total + 4 <= this.rightW();
      int x = this.rightX() + 2;

      for (int i = 0; i < index; i++) {
         x += this.tr.width(full ? filters[i].title : filters[i].shortTitle) + 12;
      }

      return new int[]{x, this.y + 26, this.tr.width(full ? filters[index].title : filters[index].shortTitle) + 12, 16};
   }

   private void renderCapeFilter(GuiGraphics context, int mouseX, int mouseY) {
      WardrobePanel.CapeFilter[] filters = WardrobePanel.CapeFilter.values();
      int[] first = this.capeFilterBounds(0);
      int[] last = this.capeFilterBounds(filters.length - 1);
      ThemeRenderer.fillRounded(context, first[0] - 2, first[1] - 2, last[0] + last[2] - first[0] + 4, first[3] + 4, Theme.trackBg(), 7);
      int[] active = this.capeFilterBounds(capeFilter.ordinal());
      int pillX = Math.round(this.motion.follow("capeFilterX", active[0], 16.0F));
      int pillW = Math.round(this.motion.follow("capeFilterW", active[2], 16.0F));
      ThemeRenderer.fillRounded(context, pillX, active[1], pillW, active[3], Ui.alpha(Theme.accent(), 0.85F), 6);

      for (WardrobePanel.CapeFilter filter : filters) {
         int[] b = this.capeFilterBounds(filter.ordinal());
         boolean on = filter == capeFilter;
         int color = on ? ThemeRenderer.onAccent() : (Ui.inside(mouseX, mouseY, b) ? Theme.textPrimary() : Theme.textSecondary());
         String label = this.tr.plainSubstrByWidth(this.tr.width(filter.title) + 12 <= b[2] ? filter.title : filter.shortTitle, b[2] - 4);
         context.drawString(this.tr, label, b[0] + (b[2] - this.tr.width(label)) / 2, b[1] + 4, color, !on);
      }
   }

   private float drawerProgress() {
      return this.motion.follow("drawer", this.drawerOpen && this.hasDrawer(this.wornItem()) ? 1.0F : 0.0F, 13.0F);
   }

   private int drawerH() {
      WardrobePanel.Item worn = this.wornItem();
      if (worn == null) {
         return 60;
      } else {
         int wanted = 30 + switch (this.drawerKind()) {
            case HEADWEAR -> this.twoColumns() ? 96 : 144;
            case WINGS -> this.twoColumns() ? 96 : 172;
            case CAPE -> worn.capeStyle() == CosmeticsModule.CapeStyle.DRAWN ? 100 : 22;
            case TRAILS -> 22;
            case PETS -> this.twoColumns() ? 96 : 144;
            case EMOTES, FAVORITES, OPTIONS -> 0;
         } + 8;
         return Math.max(40, Math.min(wanted, this.h - 28 - 40));
      }
   }

   private boolean twoColumns() {
      return this.rightW() >= 300;
   }

   private int drawerY() {
      return this.y + this.h - Math.round(this.drawerH() * Ui.ease(this.drawerProgress()));
   }

   private int gridTop() {
      return this.y + 28 + (section == WardrobePanel.Section.CAPE && !this.searching() ? 22 : 0);
   }

   private int gridBottom() {
      float p = this.drawerProgress();
      return Math.max(this.gridTop() + 30, p <= 0.0F ? this.y + this.h : this.drawerY() - 6);
   }

   private int columns() {
      return Math.max(2, (this.rightW() - 6 + 6) / 82);
   }

   private int tileW() {
      int cols = this.columns();
      return (this.rightW() - 6 - (cols - 1) * 6) / cols;
   }

   private int tileH() {
      return Math.round(this.tileW() * 1.12F) + 20;
   }

   private int maxScroll() {
      int rows = (this.items().size() + this.columns() - 1) / this.columns();
      return Math.max(0, rows * (this.tileH() + 6) - 6 - (this.gridBottom() - this.gridTop()));
   }

   private int[] tileBounds(int index) {
      int cols = this.columns();
      return new int[]{
         this.rightX() + index % cols * (this.tileW() + 6),
         this.gridTop() + index / cols * (this.tileH() + 6) - this.scroll.shown(),
         this.tileW(),
         this.tileH()
      };
   }

   public void render(GuiGraphics context, Font tr, int x, int y, int w, int h, int mouseX, int mouseY, int screenW, int screenH) {
      this.tr = tr;
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.screenW = screenW;
      this.screenH = screenH;
      this.motion.frame();
      this.scroll.setTarget(Mth.clamp(this.scroll.target(), 0, this.maxScroll()));
      boolean popoverOpen = this.popover.isOpen();
      int mx = popoverOpen ? -10000 : mouseX;
      int my = popoverOpen ? -10000 : mouseY;
      PlayerSkin skin = PlayerHead.skinTextures();
      this.renderStage(context, skin, mx, my);
      this.renderTabs(context, mx, my);
      if (section == WardrobePanel.Section.CAPE && !this.searching()) {
         this.renderCapeFilter(context, mx, my);
      }

      if (section == WardrobePanel.Section.OPTIONS && !this.searching()) {
         this.renderOptions(context, mx, my);
      } else {
         this.renderGrid(context, skin, mx, my);
         this.renderDrawer(context, mx, my);
      }
   }

   public void renderOverlay(GuiGraphics context, int mouseX, int mouseY) {
      this.popover.render(context, this.tr, mouseX, mouseY);
   }

   private void renderStage(GuiGraphics context, PlayerSkin skin, int mouseX, int mouseY) {
      int[] s = this.stageBounds();
      Ui.surface(context, s[0], s[1], s[2], s[3], 10, Theme.border(), Ui.mix(Ui.rowColor(false), Theme.accent(), 0.16F), Theme.panelBg(), false, 1.0F);
      context.drawString(this.tr, s[2] >= 120 ? "WARDROBE" : "YOU", s[0] + 8, s[1] + 8, Theme.textSecondary(), false);
      int[] spin = this.spinBounds();
      boolean spinning = this.cosmetics.spinPreviewSetting().get();
      boolean spinHovered = Ui.inside(mouseX, mouseY, spin);
      ThemeRenderer.fillRounded(
         context, spin[0], spin[1], spin[2], spin[3], spinning ? Ui.alpha(Theme.accent(), 0.28F) : (spinHovered ? Theme.hoverBg() : Theme.trackBg()), 5
      );
      AuroraIcons.draw(
         context,
         "refresh",
         spin[0] + (spin[2] - 10) / 2,
         spin[1] + (spin[3] - 10) / 2,
         10,
         spinning ? Theme.accent() : (spinHovered ? Theme.textPrimary() : Theme.textMuted())
      );
      if (s[2] >= 150) {
         String state = this.cosmetics.isEnabled() ? "Wearing" : "Off";
         int stateW = this.tr.width(state) + 10;
         int stateColor = this.cosmetics.isEnabled() ? Theme.accent() : Theme.textMuted();
         ThemeRenderer.fillRounded(context, spin[0] - 4 - stateW, s[1] + 5, stateW, 13, Ui.alpha(stateColor, 0.22F), 6);
         context.drawString(this.tr, state, spin[0] + 1 - stateW, s[1] + 8, stateColor, false);
      }

      int[] f = this.figureBounds();
      int floorY = f[1] + f[3] - 8;
      int cx = f[0] + f[2] / 2;

      for (int i = 0; i < 4; i++) {
         int ew = Math.round(f[2] * (0.78F - i * 0.16F));
         int eh = 12 - i * 3;
         ThemeRenderer.fillRounded(context, cx - ew / 2, floorY - eh / 2, ew, eh, Ui.alpha(Theme.accent(), 0.07F + i * 0.05F), eh / 2);
      }

      if (!this.draggingStage) {
         float target = this.cosmetics.spinPreviewSetting().get() ? 24.0F : 0.0F;
         this.stageVelocity = this.stageVelocity + (target - this.stageVelocity) * (1.0F - (float)Math.exp(-this.motion.dt() * 1.6F));
         this.stageSpin = this.stageSpin + this.stageVelocity * this.motion.dt();
      }

      boolean hovered = Ui.inside(mouseX, mouseY, f);
      CosmeticsModule.Emote onStage = section == WardrobePanel.Section.EMOTES ? this.cosmetics.emote() : null;
      CosmeticPreview.drawEmote(
         context,
         this.state("stage"),
         skin,
         this.cosmetics.outfit(),
         this.cosmetics.capeSetting().get() ? this.cosmetics.capeTexture() : null,
         f[0],
         f[1],
         f[0] + f[2],
         f[1] + f[3],
         this.stageSpin,
         Ui.seconds(),
         onStage
      );
      boolean hint = hovered || this.draggingStage;
      String idle = section == WardrobePanel.Section.EMOTES && !this.searching() ? emoteKeys() : PlayerHead.username();
      String name = spinHovered
         ? (spinning ? "click to stop spinning" : "click to spin")
         : (hint ? "drag to turn" : this.tr.plainSubstrByWidth(idle, s[2] - 16));
      context.drawCenteredString(
         this.tr, this.tr.plainSubstrByWidth(name, s[2] - 8), cx, s[1] + s[3] - 40, !hint && !spinHovered ? Theme.textPrimary() : Theme.textSecondary()
      );
      int[] master = this.masterBounds();
      boolean masterHovered = Ui.inside(mouseX, mouseY, master);
      ThemeRenderer.fillRounded(context, master[0], master[1], master[2], master[3], Ui.rowColor(masterHovered), 6);
      String label = this.tr.plainSubstrByWidth(master[2] >= 100 ? "Cosmetics" : (this.cosmetics.isEnabled() ? "On" : "Off"), master[2] - 24 - 14);
      context.drawString(this.tr, label, master[0] + 6, master[1] + 6, Theme.textPrimary(), false);
      float on = this.motion.follow("master", this.cosmetics.isEnabled() ? 1.0F : 0.0F, 16.0F);
      Ui.toggle(context, master[0] + master[2] - 24 - 4, master[1] + 4, on, masterHovered);
   }

   private void renderOptions(GuiGraphics context, int mouseX, int mouseY) {
      int[] showOn = this.optionRow(0);
      context.drawString(this.tr, "Show on", showOn[0], showOn[1] + (this.stackShowOn() ? 2 : 6), Theme.textPrimary(), false);
      int[] all = this.segmentBounds(0);
      int[] me = this.segmentBounds(1);
      ThemeRenderer.fillRounded(context, all[0], all[1], all[2] + me[2], all[3], Theme.trackBg(), 6);
      float slide = this.motion.follow("showOn", this.justMe() ? 1.0F : 0.0F, 14.0F);
      int pillX = Math.round(all[0] + (me[0] - all[0]) * slide);
      int pillW = Math.round(all[2] + (me[2] - all[2]) * slide);
      ThemeRenderer.fillRounded(context, pillX, all[1], pillW, all[3], Theme.accent(), 6);
      context.drawString(this.tr, "Everyone", all[0] + 6, all[1] + 4, this.justMe() ? Theme.textSecondary() : ThemeRenderer.onAccent(), false);
      context.drawString(this.tr, "Just me", me[0] + 6, me[1] + 4, this.justMe() ? ThemeRenderer.onAccent() : Theme.textSecondary(), false);
      String note;
      if (!this.justMe()) {
         int[] self = this.optionRow(1);
         context.drawString(this.tr, "Show on me too", self[0], self[1] + 6, Theme.textPrimary(), false);
         float selfOn = this.motion.follow("self", this.cosmetics.showSelfSetting().get() ? 1.0F : 0.0F, 16.0F);
         Ui.toggle(context, self[0] + self[2] - 24, self[1] + 4, selfOn, Ui.inside(mouseX, mouseY, self));
         note = "Pip Client players within the distance show their own cosmetics. Everyone else there is shown wearing yours.";
      } else {
         note = "Pip Client players within the distance show their own cosmetics. Only you are shown wearing yours.";
      }

      int[] distance = this.optionRow(2);
      SliderSetting setting = this.cosmetics.renderDistanceSetting();
      Ui.slider(
         context,
         this.tr,
         distance[0],
         distance[1] + 3,
         distance[2],
         "Distance",
         setting.getInt() + " blocks",
         (float)setting.getFraction(),
         Ui.inside(mouseX, mouseY, distance) || this.draggingSlider == setting
      );
      int[] tricks = this.optionRow(3);
      context.drawString(this.tr, "Pet tricks", tricks[0], tricks[1] + 6, Theme.textPrimary(), false);
      float tricksOn = this.motion.follow("petTricks", this.cosmetics.petTricksSetting().get() ? 1.0F : 0.0F, 16.0F);
      Ui.toggle(context, tricks[0] + tricks[2] - 24, tricks[1] + 4, tricksOn, Ui.inside(mouseX, mouseY, tricks));
      int noteY = tricks[1] + tricks[3] + 8;
      context.drawWordWrap(this.tr, FormattedText.of(note), showOn[0], noteY, showOn[2], Theme.textMuted(), false);
      String sharing = CosmeticSync.get().status();
      if (!sharing.isEmpty()) {
         int lines = this.tr.split(FormattedText.of(note), showOn[2]).size();
         context.drawWordWrap(this.tr, FormattedText.of(sharing), showOn[0], noteY + lines * 9 + 6, showOn[2], Theme.danger(), false);
      }
   }

   private void renderTabs(GuiGraphics context, int mouseX, int mouseY) {
      WardrobePanel.Section[] sections = WardrobePanel.Section.values();
      int[] first = this.tabBounds(0);
      ThemeRenderer.fillRounded(context, first[0] - 2, first[1] - 2, first[2] * sections.length + 4, first[3] + 4, Theme.trackBg(), 8);
      int[] active = this.tabBounds(section.ordinal());
      int pillX = Math.round(this.motion.follow("tab", active[0], 16.0F));
      if (!this.searching()) {
         ThemeRenderer.fillRounded(context, pillX, active[1], active[2], active[3], Theme.accent(), 6);
      }

      for (WardrobePanel.Section s : sections) {
         int[] b = this.tabBounds(s.ordinal());
         boolean on = s == section && !this.searching();
         int color = on ? ThemeRenderer.onAccent() : (Ui.inside(mouseX, mouseY, b) ? Theme.textPrimary() : Theme.textSecondary());
         String title = this.tr.plainSubstrByWidth(this.tr.width(s.title) + 6 <= b[2] ? s.title : s.shortTitle, b[2] - 4);
         context.drawString(this.tr, title, b[0] + (b[2] - this.tr.width(title)) / 2, b[1] + 5, color, !on);
      }

      if (this.roomForCount() && (this.searching() || section != WardrobePanel.Section.OPTIONS)) {
         int size = this.items().size() - (section != WardrobePanel.Section.FAVORITES && !this.searching() ? 1 : 0);
         String count = this.searching() ? size + (size == 1 ? " match" : " matches") : size + " " + section.noun;
         context.drawString(this.tr, count, this.rightX() + this.rightW() - this.tr.width(count), this.y + 7, Theme.textMuted(), false);
      }
   }

   private void renderGrid(GuiGraphics context, PlayerSkin skin, int mouseX, int mouseY) {
      List<WardrobePanel.Item> items = this.items();
      int top = this.gridTop();
      int bottom = this.gridBottom();
      float elapsed = (float)(System.nanoTime() - this.sectionStart) / 1.0E9F;
      float time = Ui.seconds();
      float dt = this.motion.dt();
      context.enableScissor(this.rightX() - 2, top, this.rightX() + this.rightW() + 2, bottom);

      for (int i = 0; i < items.size(); i++) {
         int[] b = this.tileBounds(i);
         if (b[1] + b[3] >= top && b[1] <= bottom) {
            WardrobePanel.Item item = items.get(i);
            int visibleIndex = Math.max(0, (b[1] - top) / (this.tileH() + 6)) * this.columns() + i % this.columns();
            float p = Ui.stagger(elapsed, visibleIndex, 0.03F, 0.3F);
            boolean hovered = Ui.inside(mouseX, mouseY, b) && mouseY >= top && mouseY < bottom;
            boolean worn = this.isWorn(item);
            float lift = this.motion.follow("lift" + section + i, hovered ? 1.0F : 0.0F, 14.0F);
            int ty = b[1] + Math.round((1.0F - p) * 12.0F) - Math.round(lift * 2.0F);
            int border = worn ? Theme.accent() : Ui.mix(Theme.border(), Theme.accent(), lift * 0.45F);
            int topColor = worn ? Ui.mix(Ui.rowColor(false), Theme.accent(), 0.2F) : Ui.mix(Ui.rowColor(false), Ui.rowColor(true), lift);
            Ui.surface(context, b[0], ty, b[2], b[3], 8, border, topColor, Theme.panelBg(), hovered, p);
            int previewH = b[3] - 20;
            float[] spin = this.tileSpin.computeIfAbsent(i * 3 + section.ordinal(), k -> new float[1]);
            if (hovered) {
               spin[0] += 150.0F * dt;
            } else {
               float home = Math.round(spin[0] / 360.0F) * 360.0F;
               spin[0] += (home - spin[0]) * (1.0F - (float)Math.exp(-dt * 5.0F));
            }
            float base = switch (item.kind()) {
               case HEADWEAR -> 30.0F;
               case WINGS -> 160.0F;
               case CAPE -> 180.0F;
               case TRAILS -> 115.0F;
               case PETS -> 40.0F;
               case EMOTES -> 18.0F;
               case FAVORITES, OPTIONS -> 0.0F;
            };
            float yaw = base + 22.0F * Mth.sin(time * 0.8F + i * 0.9F) + spin[0];
            if (p > 0.35F) {
               if (item.kind() == WardrobePanel.Section.PETS && !item.none()) {
                  CosmeticPreview.drawPet(
                     context, this.state(section.name() + i), skin, this.lookFor(item), b[0] + 3, ty + 4, b[0] + b[2] - 3, ty + previewH, yaw, time + i * 0.37F
                  );
               } else if (item.kind() == WardrobePanel.Section.EMOTES && !item.none()) {
                  CosmeticPreview.drawEmote(
                     context,
                     this.state(section.name() + i),
                     skin,
                     this.lookFor(item),
                     null,
                     b[0] + 3,
                     ty + 4,
                     b[0] + b[2] - 3,
                     ty + previewH,
                     yaw,
                     time + i * 0.37F,
                     item.emote()
                  );
               } else {
                  CosmeticPreview.draw(
                     context,
                     this.state(section.name() + i),
                     skin,
                     this.lookFor(item),
                     this.tileCape(item),
                     b[0] + 3,
                     ty + 4,
                     b[0] + b[2] - 3,
                     ty + previewH,
                     yaw,
                     time + i * 0.37F
                  );
               }
            }

            String label = this.tr.plainSubstrByWidth(item.label(), b[2] - 8);
            int labelColor = worn ? Theme.accent() : Theme.textPrimary();
            context.drawCenteredString(this.tr, label, b[0] + b[2] / 2, ty + previewH + 5, Ui.alpha(labelColor, p));
            if (item.kind() == WardrobePanel.Section.CAPE && item.capeStyle() == CosmeticsModule.CapeStyle.MINECRAFT && this.capeFor(item) == null) {
               boolean failed = this.cosmetics.capeFailed(item.capeName());
               String note = failed ? "offline" : "loading" + ".".repeat((int)(time * 3.0F) % 4);
               context.drawCenteredString(this.tr, note, b[0] + b[2] / 2, ty + 6, Theme.textMuted());
            }

            if (worn) {
               int bx = b[0] + b[2] - 15;
               ThemeRenderer.fillRounded(context, bx, ty + 4, 11, 11, Theme.accent(), 5);
               context.drawString(this.tr, "✔", bx + 2, ty + 6, ThemeRenderer.onAccent(), false);
            }

            if (!item.none() && p > 0.05F) {
               int[] star = this.starBounds(b, ty);
               ThemeRenderer.star(
                  context, this.tr, star[0], star[1], this.cosmetics.isFavorite(item.key()), ThemeRenderer.inStar(mouseX, mouseY, star[0], star[1])
               );
            }

            if (item.kind() == WardrobePanel.Section.EMOTES && !item.none() && p > 0.05F) {
               this.renderWheelButton(context, item.emote(), this.wheelBounds(b, ty), mouseX, mouseY, mouseY >= top && mouseY < bottom);
            }
         }
      }

      if (items.isEmpty() && this.searching()) {
         int cx = this.rightX() + this.rightW() / 2;
         int cy = top + Math.min(60, (bottom - top) / 2 - 14);
         AuroraIcons.draw(context, "zoom", cx - 12, cy, 24, Theme.textMuted());
         context.drawCenteredString(
            this.tr, this.tr.plainSubstrByWidth("Nothing matches \"" + this.query.trim() + "\"", this.rightW() - 8), cx, cy + 32, Theme.textSecondary()
         );
         context.drawCenteredString(
            this.tr, this.tr.plainSubstrByWidth("Try part of a name, or headwear, wings or cape", this.rightW() - 8), cx, cy + 44, Theme.textMuted()
         );
      } else if (items.isEmpty() && section == WardrobePanel.Section.FAVORITES) {
         int cx = this.rightX() + this.rightW() / 2;
         int cy = top + Math.min(60, (bottom - top) / 2 - 14);
         AuroraIcons.draw(context, "star_outline", cx - 12, cy, 24, Theme.textMuted());
         context.drawCenteredString(this.tr, this.tr.plainSubstrByWidth("Nothing starred yet", this.rightW() - 8), cx, cy + 32, Theme.textSecondary());
         context.drawCenteredString(
            this.tr, this.tr.plainSubstrByWidth("Click the star on anything you like", this.rightW() - 8), cx, cy + 44, Theme.textMuted()
         );
      }

      context.disableScissor();
      int maxScroll = this.maxScroll();
      if (maxScroll > 0) {
         int trackH = bottom - top;
         int handleH = Math.max(18, trackH * trackH / (trackH + maxScroll));
         int handleY = top + Math.round((trackH - handleH) * ((float)this.scroll.shown() / maxScroll));
         ThemeRenderer.fillRounded(context, this.rightX() + this.rightW() - 3, handleY, 3, handleH, Ui.alpha(Theme.accent(), 0.8F), 1);
      }
   }

   private void renderWheelButton(GuiGraphics context, CosmeticsModule.Emote emote, int[] w, int mouseX, int mouseY, boolean inGrid) {
      boolean on = this.cosmetics.isOnWheel(emote);
      boolean full = !on && this.cosmetics.wheelEmotes().size() >= 12;
      boolean hovered = inGrid && Ui.inside(mouseX, mouseY, w);
      String label = on ? "On wheel" : (full ? (hovered ? "Wheel full" : "+ Wheel") : "+ Wheel");
      int bg = on ? Theme.accent() : (hovered ? Theme.hoverBg() : Ui.alpha(Theme.trackBg(), 0.85F));
      ThemeRenderer.fillRounded(context, w[0], w[1], w[2], w[3], bg, 5);
      int color = on ? ThemeRenderer.onAccent() : (full && hovered ? Theme.textMuted() : (hovered ? Theme.textPrimary() : Theme.textSecondary()));
      context.drawString(
         this.tr, this.tr.plainSubstrByWidth(label, w[2] - 4), w[0] + (w[2] - Math.min(w[2] - 4, this.tr.width(label))) / 2, w[1] + 2, color, false
      );
   }

   private int[] wheelBounds(int[] tile, int tileY) {
      int w = Math.min(tile[2] - 10, this.tr.width("Wheel full") + 10);
      return new int[]{tile[0] + 5, tileY + tile[3] - 20 - 15, w, 12};
   }

   private int[] starBounds(int[] tile, int tileY) {
      return new int[]{tile[0] + 5, tileY + 6};
   }

   private CosmeticPreview.State state(String key) {
      return this.states.computeIfAbsent(key, k -> new CosmeticPreview.State());
   }

   private List<WardrobePanel.Control> controls() {
      List<WardrobePanel.Control> controls = new ArrayList<>();
      WardrobePanel.Item worn = this.wornItem();
      if (worn == null) {
         return controls;
      } else {
         int left = this.rightX() + 10;
         int inner = this.rightW() - 20;
         int top = this.drawerY() + 28;
         boolean two = this.twoColumns();
         int colW = two ? (inner - 16) / 2 : inner;
         if (worn.piece() != null) {
            this.loadTweak(worn.piece());
         }

         switch (this.drawerKind()) {
            case HEADWEAR: {
               controls.add(new WardrobePanel.Control("Natural colors", this.cosmetics.naturalHeadwearColorsSetting(), new int[]{left, top, colW, 20}));
               int rows = top + 22;
               controls.add(new WardrobePanel.Control("Main color", this.cosmetics.headwearColorSetting(), new int[]{left, rows, colW, 20}));
               controls.add(
                  new WardrobePanel.Control(
                     "Accent color",
                     this.cosmetics.accentColorSetting(),
                     two ? new int[]{left + colW + 16, rows, colW, 20} : new int[]{left, rows + 20, colW, 20}
                  )
               );
               controls.add(
                  new WardrobePanel.Control("Size", this.headwearSize, two ? new int[]{left, rows + 22, inner, 24} : new int[]{left, rows + 42, colW, 24})
               );
               this.addAngles(controls, left, two ? rows + 50 : rows + 70, colW, two);
               break;
            }
            case WINGS: {
               controls.add(new WardrobePanel.Control("Natural colors", this.cosmetics.naturalWingColorsSetting(), new int[]{left, top, colW, 20}));
               int rows = top + 22;
               controls.add(new WardrobePanel.Control("Wing color", this.cosmetics.wingColorSetting(), new int[]{left, rows, colW, 20}));
               controls.add(
                  new WardrobePanel.Control(
                     "Tip color",
                     this.cosmetics.wingTipColorSetting(),
                     two ? new int[]{left + colW + 16, rows, colW, 20} : new int[]{left, rows + 20, colW, 20}
                  )
               );
               controls.add(new WardrobePanel.Control("Size", this.wingSize, two ? new int[]{left, rows + 22, colW, 24} : new int[]{left, rows + 42, colW, 24}));
               controls.add(
                  new WardrobePanel.Control(
                     "Flap speed",
                     this.cosmetics.flapSpeedSetting(),
                     two ? new int[]{left + colW + 16, rows + 22, colW, 24} : new int[]{left, rows + 70, colW, 24}
                  )
               );
               this.addAngles(controls, left, two ? rows + 50 : rows + 98, colW, two);
               break;
            }
            case CAPE:
               if (worn.capeStyle() == CosmeticsModule.CapeStyle.DRAWN) {
                  int gridW = 60;
                  int side = left + gridW + 16;
                  int sideW = Math.max(60, left + inner - side);
                  controls.add(new WardrobePanel.Control("paint", null, new int[]{left, top, gridW, 96}));
                  controls.add(new WardrobePanel.Control("Brush", this.cosmetics.paintColorSetting(), new int[]{side, top, sideW, 20}));
                  controls.add(new WardrobePanel.Control("Background", this.cosmetics.capeColorSetting(), new int[]{side, top + 22, sideW, 20}));
                  controls.add(new WardrobePanel.Control("clear", null, new int[]{side, top + 48, Math.min(sideW, 70), 16}));
               } else {
                  controls.add(new WardrobePanel.Control("Cape color", this.cosmetics.capeColorSetting(), new int[]{left, top, colW, 20}));
               }
               break;
            case TRAILS:
               controls.add(new WardrobePanel.Control("ways", null, new int[]{left, top, inner, 18}));
               break;
            case PETS: {
               controls.add(new WardrobePanel.Control("place", null, new int[]{left, top, inner, 18}));
               controls.add(new WardrobePanel.Control("Natural colors", this.petNatural, new int[]{left, top + 22, colW, 20}));
               controls.add(
                  new WardrobePanel.Control("Color", this.petColor, two ? new int[]{left + colW + 16, top + 22, colW, 20} : new int[]{left, top + 42, colW, 20})
               );
               int rows = top + (two ? 44 : 64);
               controls.add(new WardrobePanel.Control("Size", this.petSize, new int[]{left, rows, inner, 24}));
               this.addAngles(controls, left, rows + 28, colW, two);
            }
         }

         return controls;
      }
   }

   private void renderWays(GuiGraphics context, int[] b, WardrobePanel.Item worn, int mouseX, int mouseY) {
      if (worn.trail() != null) {
         List<Trails.Way> ways = worn.trail().ways();
         Trails.Way current = worn.trail().way(Trails.wayOf(this.cosmetics.trail()));
         context.drawString(this.tr, "Color", b[0], b[1] + 5, Theme.textPrimary(), false);

         for (int i = 0; i < ways.size(); i++) {
            int[] s = this.wayBounds(b, i);
            boolean on = ways.get(i) == current;
            boolean hovered = Ui.inside(mouseX, mouseY, s);
            if (on || hovered) {
               ThemeRenderer.fillRounded(context, s[0] - 2, s[1] - 2, s[2] + 4, s[3] + 4, on ? Theme.accent() : Theme.hoverBg(), 6);
            }

            ThemeRenderer.fillRounded(context, s[0], s[1], s[2], s[3], Theme.border(), 5);
            ThemeRenderer.fillRounded(context, s[0] + 1, s[1] + 1, s[2] - 2, s[3] - 2, 0xFF000000 | ways.get(i).rgb(), 4);
         }

         int[] lastSwatch = this.wayBounds(b, ways.size() - 1);
         int nameX = lastSwatch[0] + lastSwatch[2] + 10;
         if (current != null && nameX + this.tr.width(current.label()) <= b[0] + b[2]) {
            context.drawString(this.tr, current.label(), nameX, b[1] + 5, Theme.textSecondary(), false);
         }
      }
   }

   private int[] wayBounds(int[] row, int index) {
      int start = row[0] + this.tr.width("Color") + 10;
      return new int[]{start + index * 20, row[1] + 2, 14, 14};
   }

   private void renderPlace(GuiGraphics context, int[] b, WardrobePanel.Item worn, int mouseX, int mouseY) {
      if (worn.pet() != null) {
         int current = this.cosmetics.petSpot(worn.pet()).ordinal();
         context.drawString(this.tr, "Place", b[0], b[1] + 5, Theme.textPrimary(), false);

         for (int i = 0; i < PLACES.length; i++) {
            int[] seg = this.placeBounds(b, i);
            boolean on = i == current;
            ThemeRenderer.fillRounded(
               context, seg[0], seg[1], seg[2], seg[3], on ? Theme.accent() : (Ui.inside(mouseX, mouseY, seg) ? Theme.hoverBg() : Theme.trackBg()), 5
            );
            String label = this.tr.plainSubstrByWidth(PLACES[i], seg[2] - 4);
            context.drawString(
               this.tr, label, seg[0] + (seg[2] - this.tr.width(label)) / 2, seg[1] + 4, on ? ThemeRenderer.onAccent() : Theme.textSecondary(), false
            );
         }
      }
   }

   private int[] placeBounds(int[] row, int index) {
      int start = row[0] + this.tr.width("Place") + 10;
      int w = (row[0] + row[2] - start - 8) / 3;
      return new int[]{start + index * (w + 4), row[1], w, 16};
   }

   private void addAngles(List<WardrobePanel.Control> controls, int left, int top, int colW, boolean two) {
      controls.add(new WardrobePanel.Control("X angle", this.xAngle, new int[]{left, top, colW, 24}));
      controls.add(new WardrobePanel.Control("Y angle", this.yAngle, two ? new int[]{left + colW + 16, top, colW, 24} : new int[]{left, top + 28, colW, 24}));
   }

   private SliderSetting sizeSlider(Enum<?> piece) {
      return piece instanceof CosmeticsModule.Headwear ? this.headwearSize : (piece instanceof CosmeticsModule.Pet ? this.petSize : this.wingSize);
   }

   private void loadTweak(Enum<?> piece) {
      if (piece instanceof CosmeticsModule.Pet pet && this.popover.setting() != this.petColor) {
         int picked = this.cosmetics.petColor(pet);
         this.petNatural.set(picked == 0);
         this.petColor.set(picked == 0 ? CosmeticPreview.naturalPetColor(pet) : picked);
      }

      this.sizeSlider(piece).set((double)this.cosmetics.pieceSize(piece));
      this.xAngle.set((double)this.cosmetics.pieceXAngle(piece));
      this.yAngle.set((double)this.cosmetics.pieceYAngle(piece));
   }

   private void saveTweak(SliderSetting moved) {
      WardrobePanel.Item worn = this.wornItem();
      boolean tweak = moved == this.headwearSize || moved == this.wingSize || moved == this.petSize || moved == this.xAngle || moved == this.yAngle;
      if (tweak && worn != null && worn.piece() != null) {
         SliderSetting size = this.sizeSlider(worn.piece());
         this.cosmetics
            .setPieceTweak(worn.piece(), (float)size.get().doubleValue(), (float)this.xAngle.get().doubleValue(), (float)this.yAngle.get().doubleValue());
      }
   }

   private int[] takeOffBounds() {
      int[] close = this.closeBounds();
      int w = this.tr.width("Take off") + 10;
      return new int[]{close[0] - 6 - w, close[1] - 1, w, 16};
   }

   private int[] swatchBounds(int[] row) {
      return new int[]{row[0] + row[2] - 30, row[1] + 3, 30, 13};
   }

   private int[] closeBounds() {
      return new int[]{this.rightX() + this.rightW() - 22, this.drawerY() + 6, 14, 14};
   }

   private void renderDrawer(GuiGraphics context, int mouseX, int mouseY) {
      float p = this.drawerProgress();
      WardrobePanel.Item worn = this.wornItem();
      if (!(p <= 0.01F) && worn != null) {
         int dx = this.rightX();
         int dy = this.drawerY();
         int dw = this.rightW();
         int dh = this.y + this.h - dy;
         if (worn.pet() != null
            && this.popover.isOpen()
            && this.popover.setting() == this.petColor
            && this.cosmetics.petColor(worn.pet()) != this.petColor.raw()) {
            this.cosmetics.setPetColor(worn.pet(), this.petColor.raw());
         }

         context.enableScissor(dx - 4, dy - 8, dx + dw + 4, this.y + this.h);
         context.fillGradient(dx, dy - 8, dx + dw, dy, 0, Theme.shadow());
         ThemeRenderer.fillRounded(context, dx, dy, dw, this.drawerH(), Ui.mix(Theme.border(), Theme.accent(), 0.35F), 10);
         ThemeRenderer.fillGradientRounded(
            context, dx + 1, dy + 1, dw - 2, this.drawerH() - 2, 9, Ui.mix(Theme.modalHeaderBg(), Theme.accent(), 0.08F), Theme.modalPanelBg() | 0xFF000000
         );
         int[] takeOff = this.takeOffBounds();
         String title = this.tr.plainSubstrByWidth(worn.label(), Math.max(20, takeOff[0] - dx - 20));
         context.drawString(this.tr, title, dx + 10, dy + 9, Theme.textPrimary(), false);
         int kindX = dx + 16 + this.tr.width(title);
         String kind = "·  " + this.drawerKind().title;
         if (kindX + this.tr.width(kind) < takeOff[0] - 6) {
            context.drawString(this.tr, kind, kindX, dy + 9, Theme.textMuted(), false);
         }

         boolean takeOffHovered = Ui.inside(mouseX, mouseY, takeOff);
         ThemeRenderer.fillRounded(context, takeOff[0], takeOff[1], takeOff[2], takeOff[3], takeOffHovered ? Theme.dangerBg() : Theme.trackBg(), 5);
         context.drawCenteredString(this.tr, "Take off", takeOff[0] + takeOff[2] / 2, takeOff[1] + 4, takeOffHovered ? Theme.danger() : Theme.textSecondary());
         int[] close = this.closeBounds();
         boolean closeHovered = Ui.inside(mouseX, mouseY, close);
         if (closeHovered) {
            ThemeRenderer.fillRounded(context, close[0], close[1], close[2], close[3], Theme.hoverBg(), 4);
         }

         context.drawCenteredString(this.tr, "×", close[0] + 7, close[1] + 3, closeHovered ? Theme.textPrimary() : Theme.textSecondary());

         for (WardrobePanel.Control control : this.controls()) {
            int[] b = control.bounds();
            if (control.setting() instanceof BooleanSetting toggle) {
               context.drawString(this.tr, control.label(), b[0], b[1] + 5, Theme.textPrimary(), false);
               float on = this.motion.follow(toggle, toggle.get() ? 1.0F : 0.0F, 16.0F);
               Ui.toggle(context, b[0] + b[2] - 24, b[1] + 3, on, Ui.inside(mouseX, mouseY, b));
            } else if (control.setting() instanceof ColorSetting color) {
               int[] swatch = this.swatchBounds(b);
               context.drawString(this.tr, control.label(), b[0], b[1] + 5, Theme.textPrimary(), false);
               Ui.swatch(
                  context,
                  swatch[0],
                  swatch[1],
                  swatch[2],
                  swatch[3],
                  this.shownColor(color),
                  Ui.inside(mouseX, mouseY, swatch) || this.popover.setting() == color
               );
            } else if (!(control.setting() instanceof SliderSetting slider)) {
               if (control.label().equals("place")) {
                  this.renderPlace(context, b, worn, mouseX, mouseY);
               } else if (control.label().equals("paint")) {
                  this.renderPaintGrid(context, b, mouseX, mouseY);
               } else if (control.label().equals("ways")) {
                  this.renderWays(context, b, worn, mouseX, mouseY);
               } else if (control.label().equals("clear")) {
                  boolean hovered = Ui.inside(mouseX, mouseY, b);
                  ThemeRenderer.fillRounded(context, b[0], b[1], b[2], b[3], hovered ? Theme.dangerBg() : Theme.trackBg(), 5);
                  context.drawCenteredString(this.tr, "Clear", b[0] + b[2] / 2, b[1] + 4, hovered ? Theme.danger() : Theme.textSecondary());
               }
            } else {
               Ui.slider(
                  context,
                  this.tr,
                  b[0],
                  b[1] + 2,
                  b[2],
                  control.label(),
                  slider.display(),
                  (float)slider.getFraction(),
                  Ui.inside(mouseX, mouseY, b) || this.draggingSlider == slider
               );
            }
         }

         if (worn.capeStyle() == CosmeticsModule.CapeStyle.DRAWN) {
            int[] clear = this.controls().get(3).bounds();
            context.drawString(this.tr, "Left click paints", clear[0], clear[1] + 24, Theme.textMuted(), false);
            context.drawString(this.tr, "Right click erases", clear[0], clear[1] + 36, Theme.textMuted(), false);
         }

         context.disableScissor();
      }
   }

   private int shownColor(ColorSetting setting) {
      WardrobePanel.Item worn = this.wornItem();
      CosmeticsModule.WingType type = worn != null && worn.wings() != null ? worn.wings() : CosmeticsModule.WingType.ANGEL;
      CosmeticsModule.Headwear kind = worn != null && worn.headwear() != null ? worn.headwear() : CosmeticsModule.Headwear.CAT_EARS;
      if (setting == this.cosmetics.wingColorSetting()) {
         return this.cosmetics.wingBase(type);
      } else if (setting == this.cosmetics.wingTipColorSetting()) {
         return this.cosmetics.wingTip(type);
      } else if (setting == this.cosmetics.headwearColorSetting()) {
         return this.cosmetics.headwearOuter(kind);
      } else {
         return setting == this.cosmetics.accentColorSetting() ? this.cosmetics.headwearInner(kind) : setting.raw();
      }
   }

   private void renderPaintGrid(GuiGraphics context, int[] b, int mouseX, int mouseY) {
      context.fill(b[0] - 1, b[1] - 1, b[0] + b[2] + 1, b[1] + b[3] + 1, Theme.border());
      int background = 0xFF000000 | this.cosmetics.capeColor();

      for (int py = 0; py < 16; py++) {
         for (int px = 0; px < 10; px++) {
            int pixel = this.cosmetics.capePixel(px, py);
            int cx = b[0] + px * 6;
            int cy = b[1] + py * 6;
            context.fill(cx, cy, cx + 6, cy + 6, pixel >>> 24 == 0 ? background : pixel);
         }
      }

      if (Ui.inside(mouseX, mouseY, b)) {
         int cx = b[0] + (mouseX - b[0]) / 6 * 6;
         int cy = b[1] + (mouseY - b[1]) / 6 * 6;
         context.fill(cx, cy, cx + 6, cy + 1, -1);
         context.fill(cx, cy + 6 - 1, cx + 6, cy + 6, -1);
         context.fill(cx, cy, cx + 1, cy + 6, -1);
         context.fill(cx + 6 - 1, cy, cx + 6, cy + 6, -1);
      }
   }

   public boolean mouseClicked(double mx, double my, int button) {
      if (this.popover.isOpen()) {
         this.popover.mouseClicked(mx, my);
         return true;
      } else if (this.drawerProgress() > 0.5F && Ui.inside(mx, my, this.rightX(), this.drawerY(), this.rightW(), this.y + this.h - this.drawerY())) {
         this.clickDrawer(mx, my, button);
         return true;
      } else {
         for (WardrobePanel.Section s : WardrobePanel.Section.values()) {
            if (Ui.inside(mx, my, this.tabBounds(s.ordinal()))) {
               if (s != section || this.searching()) {
                  section = s;
                  this.query = "";
                  this.sectionStart = System.nanoTime();
                  this.scroll.jump(0);
                  this.drawerOpen = false;
               }

               return true;
            }
         }

         if (section == WardrobePanel.Section.CAPE && !this.searching()) {
            for (WardrobePanel.CapeFilter filter : WardrobePanel.CapeFilter.values()) {
               if (Ui.inside(mx, my, this.capeFilterBounds(filter.ordinal()))) {
                  if (filter != capeFilter) {
                     capeFilter = filter;
                     this.sectionStart = System.nanoTime();
                     this.scroll.jump(0);
                     this.drawerOpen = false;
                  }

                  return true;
               }
            }
         }

         if ((section != WardrobePanel.Section.OPTIONS || this.searching())
            && Ui.inside(mx, my, this.rightX(), this.gridTop(), this.rightW(), this.gridBottom() - this.gridTop())) {
            List<WardrobePanel.Item> items = this.items();

            for (int i = 0; i < items.size(); i++) {
               int[] tile = this.tileBounds(i);
               if (Ui.inside(mx, my, tile)) {
                  WardrobePanel.Item item = items.get(i);
                  if (item.kind() == WardrobePanel.Section.EMOTES && !item.none() && Ui.inside(mx, my, this.wheelBounds(tile, tile[1]))) {
                     if (this.cosmetics.toggleWheel(item.emote())) {
                        Config.save();
                     }

                     return true;
                  }

                  int[] star = this.starBounds(tile, tile[1]);
                  if (!item.none() && ThemeRenderer.inStar((int)mx, (int)my, star[0], star[1])) {
                     this.cosmetics.toggleFavorite(item.key());
                     Config.save();
                     return true;
                  }

                  this.pick(item);
                  return true;
               }
            }

            return true;
         } else if (Ui.inside(mx, my, this.spinBounds())) {
            this.cosmetics.spinPreviewSetting().set(!this.cosmetics.spinPreviewSetting().get());
            Config.save();
            return true;
         } else if (Ui.inside(mx, my, this.figureBounds())) {
            this.draggingStage = true;
            this.lastDragX = mx;
            this.stageVelocity = 0.0F;
            return true;
         } else {
            return this.clickGeneral(mx, my);
         }
      }
   }

   private boolean clickGeneral(double mx, double my) {
      boolean options = section == WardrobePanel.Section.OPTIONS && !this.searching();
      if (Ui.inside(mx, my, this.masterBounds())) {
         this.cosmetics.setEnabled(!this.cosmetics.isEnabled());
      } else if (options && Ui.inside(mx, my, this.segmentBounds(0))) {
         this.cosmetics.showOnSetting().set(CosmeticsModule.ShowOn.EVERYONE);
      } else if (options && Ui.inside(mx, my, this.segmentBounds(1))) {
         this.cosmetics.showOnSetting().set(CosmeticsModule.ShowOn.JUST_ME);
      } else if (options && !this.justMe() && Ui.inside(mx, my, this.optionRow(1))) {
         this.cosmetics.showSelfSetting().set(!this.cosmetics.showSelfSetting().get());
      } else if (options && Ui.inside(mx, my, this.optionRow(3))) {
         this.cosmetics.petTricksSetting().set(!this.cosmetics.petTricksSetting().get());
      } else {
         if (!options || !Ui.inside(mx, my, this.optionRow(2))) {
            return Ui.inside(mx, my, this.x, this.y, this.w, this.h);
         }

         int[] row = this.optionRow(2);
         this.draggingSlider = this.cosmetics.renderDistanceSetting();
         this.draggingSliderBounds = row;
         this.draggingSlider.setFraction(Ui.fraction(mx, row[0], row[2]));
      }

      Config.save();
      return true;
   }

   private void clickDrawer(double mx, double my, int button) {
      if (Ui.inside(mx, my, this.closeBounds())) {
         this.drawerOpen = false;
      } else if (Ui.inside(mx, my, this.takeOffBounds())) {
         this.takeOffShown();
      } else {
         for (WardrobePanel.Control control : this.controls()) {
            int[] b = control.bounds();
            if (control.setting() instanceof BooleanSetting toggle && Ui.inside(mx, my, b)) {
               toggle.set(!toggle.get());
               WardrobePanel.Item shown = this.wornItem();
               if (toggle == this.petNatural && shown != null && shown.pet() != null) {
                  this.cosmetics.setPetColor(shown.pet(), this.petNatural.get() ? 0 : this.petColor.raw());
               }

               Config.save();
               return;
            }

            if (control.setting() instanceof ColorSetting color && Ui.inside(mx, my, b)) {
               WardrobePanel.Item worn = this.wornItem();
               if ((color == this.cosmetics.wingColorSetting() || color == this.cosmetics.wingTipColorSetting()) && worn != null && worn.wings() != null) {
                  this.cosmetics.useCustomWingColors(worn.wings());
               } else if ((color == this.cosmetics.headwearColorSetting() || color == this.cosmetics.accentColorSetting())
                  && worn != null
                  && worn.headwear() != null) {
                  this.cosmetics.useCustomHeadwearColors(worn.headwear());
               } else if (color == this.petColor && worn != null && worn.pet() != null) {
                  this.petNatural.set(false);
                  this.cosmetics.setPetColor(worn.pet(), this.petColor.raw());
               }

               int[] swatch = this.swatchBounds(b);
               this.popover.open(color, swatch[0] + swatch[2] - 132, swatch[1] - 122 - 4, this.screenW, this.screenH);
               return;
            }

            if (control.setting() instanceof SliderSetting slider && Ui.inside(mx, my, b)) {
               this.draggingSlider = slider;
               this.draggingSliderBounds = b;
               slider.setFraction(Ui.fraction(mx, b[0], b[2]));
               this.saveTweak(slider);
               return;
            }

            if (control.label().equals("place") && Ui.inside(mx, my, b)) {
               WardrobePanel.Item shown = this.wornItem();

               for (int i = 0; i < PLACES.length && shown != null && shown.pet() != null; i++) {
                  if (Ui.inside(mx, my, this.placeBounds(b, i))) {
                     this.cosmetics.setPetSpot(shown.pet(), CosmeticsModule.PetSpot.values()[i]);
                     Config.save();
                  }
               }

               return;
            }

            if (control.label().equals("paint") && Ui.inside(mx, my, b)) {
               this.paintMode = button == 1 ? 2 : 1;
               this.paint(b, mx, my);
               return;
            }

            if (control.label().equals("ways") && Ui.inside(mx, my, b)) {
               WardrobePanel.Item shown = this.wornItem();

               for (int ix = 0; shown != null && shown.trail() != null && ix < shown.trail().ways().size(); ix++) {
                  if (Ui.inside(mx, my, this.wayBounds(b, ix))) {
                     this.cosmetics.setTrail(shown.trail().store(shown.trail().ways().get(ix).id()));
                     Config.save();
                  }
               }

               return;
            }

            if (control.label().equals("clear") && Ui.inside(mx, my, b)) {
               this.cosmetics.clearCape();
               Config.save();
               return;
            }
         }
      }
   }

   private void paint(int[] grid, double mx, double my) {
      int px = (int)Math.floor((mx - grid[0]) / 6.0);
      int py = (int)Math.floor((my - grid[1]) / 6.0);
      this.cosmetics.setCapePixel(px, py, this.paintMode == 2 ? 0 : 0xFF000000 | this.cosmetics.paintColorSetting().raw());
   }

   public boolean mouseDragged(double mx, double my) {
      if (this.popover.mouseDragged(mx, my)) {
         return true;
      } else if (this.draggingSlider != null) {
         this.draggingSlider.setFraction(Ui.fraction(mx, this.draggingSliderBounds[0], this.draggingSliderBounds[2]));
         this.saveTweak(this.draggingSlider);
         return true;
      } else if (this.paintMode != 0) {
         for (WardrobePanel.Control control : this.controls()) {
            if (control.label().equals("paint")) {
               this.paint(control.bounds(), mx, my);
            }
         }

         return true;
      } else if (this.draggingStage) {
         float delta = (float)(mx - this.lastDragX);
         this.stageSpin += delta * 1.4F;
         this.stageVelocity = Mth.clamp(delta * 1.4F / Math.max(0.008F, this.motion.dt()), -720.0F, 720.0F);
         this.lastDragX = mx;
         return true;
      } else {
         return false;
      }
   }

   public void mouseReleased() {
      boolean changed = this.draggingSlider != null || this.paintMode != 0 || this.popover.isOpen();
      this.popover.mouseReleased();
      this.draggingSlider = null;
      this.paintMode = 0;
      this.draggingStage = false;
      if (changed) {
         Config.save();
      }
   }

   public boolean mouseScrolled(double mx, double my, double amount) {
      if (Ui.inside(mx, my, this.rightX(), this.gridTop(), this.rightW(), this.gridBottom() - this.gridTop())) {
         this.scroll.setTarget(Mth.clamp(this.scroll.target() - (int)(amount * (this.tileH() + 6) / 2.0), 0, this.maxScroll()));
         return true;
      } else {
         return false;
      }
   }

   public boolean escape() {
      if (this.popover.isOpen()) {
         this.popover.close();
         Config.save();
         return true;
      } else if (this.drawerOpen) {
         this.drawerOpen = false;
         return true;
      } else {
         return false;
      }
   }

   public void prefetch() {
      for (String name : this.cosmetics.minecraftCapeNames()) {
         this.cosmetics.capePreview(name);
      }
   }

   static boolean available() {
      return Minecraft.getInstance() != null && CosmeticsModule.get() != null;
   }

   static enum CapeFilter {
      MINECRAFT("Minecraft Capes", "Minecraft"),
      ANIMATED("Animated Capes", "Animated"),
      ALL("All", "All");

      final String title;
      final String shortTitle;

      private CapeFilter(String title, String shortTitle) {
         this.title = title;
         this.shortTitle = shortTitle;
      }
   }

   private record Control(String label, Object setting, int[] bounds) {
   }

   private record Item(
      WardrobePanel.Section kind,
      String label,
      CosmeticsModule.Headwear headwear,
      CosmeticsModule.WingType wings,
      CosmeticsModule.CapeStyle capeStyle,
      String capeName,
      CosmeticsModule.Pet pet,
      CosmeticsModule.Emote emote,
      boolean none,
      Trails.Trail trail
   ) {
      Item(
         WardrobePanel.Section kind,
         String label,
         CosmeticsModule.Headwear headwear,
         CosmeticsModule.WingType wings,
         CosmeticsModule.CapeStyle capeStyle,
         String capeName,
         CosmeticsModule.Pet pet,
         CosmeticsModule.Emote emote,
         boolean none
      ) {
         this(kind, label, headwear, wings, capeStyle, capeName, pet, emote, none, null);
      }

      String key() {
         return switch (this.kind) {
            case HEADWEAR -> "HEADWEAR:" + this.headwear.name();
            case WINGS -> "WINGS:" + this.wings.name();
            default -> "CAPE:" + this.capeStyle.name() + ":" + (this.capeName == null ? "" : this.capeName);
            case TRAILS -> "TRAIL:" + this.trail.id();
            case PETS -> "PET:" + this.pet.name();
            case EMOTES -> "EMOTE:" + this.emote.name();
         };
      }

      Enum<?> piece() {
         return (Enum<?>)(this.headwear != null ? this.headwear : (this.wings != null ? this.wings : this.pet));
      }
   }

   static enum Section {
      HEADWEAR("Headwear", "Head", "styles"),
      WINGS("Wings", "Wings", "wings"),
      CAPE("Cape", "Cape", "capes"),
      TRAILS("Trails", "Trail", "trails"),
      PETS("Pets", "Pets", "pets"),
      EMOTES("Emotes", "Emotes", "emotes"),
      FAVORITES("Favorites", "Favs", "starred"),
      OPTIONS("Options", "More", "");

      final String title;
      final String shortTitle;
      final String noun;

      private Section(String title, String shortTitle, String noun) {
         this.title = title;
         this.shortTitle = shortTitle;
         this.noun = noun;
      }
   }
}
