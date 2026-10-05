package dev.lyfw.lyfwclient.module;

import java.util.Arrays;

public enum Category {
   RENDER("Render"),
   HUD("HUD"),
   ALL("All"),
   ACTIVE("Active Mods"),
   FAVORITE("Favorite"),
   MISC("Misc");

   private static final Category[] TABS = Arrays.stream(values()).filter(category -> category != MISC).toArray(Category[]::new);
   public final String title;

   private Category(String title) {
      this.title = title;
   }

   public static Category[] tabs() {
      return TABS;
   }

   public boolean isTagView() {
      return this == ALL || this == ACTIVE || this == FAVORITE;
   }
}
