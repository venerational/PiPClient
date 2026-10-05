package dev.lyfw.lyfwclient.gui;

import dev.lyfw.lyfwclient.module.Category;
import java.util.EnumMap;
import java.util.Map;

public final class WindowState {
   private static final int FALLBACK_X = 20;
   private static final int FALLBACK_Y = 20;
   private static final int WINDOW_W = 128;
   private static final int WINDOW_GAP = 12;
   private static final Map<Category, int[]> POSITIONS = new EnumMap<>(Category.class);
   private static final Map<Category, Boolean> COLLAPSED = new EnumMap<>(Category.class);

   private WindowState() {
   }

   public static void ensureDefaults(int screenWidth, int screenHeight) {
      Category[] categories = Category.values();
      int stride = 140;
      int perRow = Math.max(1, (screenWidth - 16) / stride);
      int rows = (categories.length + perRow - 1) / perRow;
      int rowStride = Math.max(60, (screenHeight - 32) / Math.max(1, rows));
      int usedW = Math.min(categories.length, perRow) * stride - 12;
      int startX = Math.max(8, (screenWidth - usedW) / 2);
      int startY = Math.max(8, rows > 1 ? 16 : screenHeight / 6);

      for (int i = 0; i < categories.length; i++) {
         if (!POSITIONS.containsKey(categories[i])) {
            int col = i % perRow;
            int row = i / perRow;
            int x = Math.min(startX + col * stride, Math.max(0, screenWidth - 128));
            int y = Math.min(startY + row * rowStride, Math.max(0, screenHeight - 24));
            POSITIONS.put(categories[i], new int[]{x, y});
         }
      }
   }

   public static boolean hasPosition(Category category) {
      return POSITIONS.containsKey(category);
   }

   public static int x(Category category) {
      int[] p = POSITIONS.get(category);
      return p == null ? 20 : p[0];
   }

   public static int y(Category category) {
      int[] p = POSITIONS.get(category);
      return p == null ? 20 : p[1];
   }

   public static void set(Category category, int x, int y) {
      POSITIONS.put(category, new int[]{x, y});
   }

   public static boolean isCollapsed(Category category) {
      return COLLAPSED.getOrDefault(category, Boolean.FALSE);
   }

   public static void toggleCollapsed(Category category) {
      COLLAPSED.put(category, !isCollapsed(category));
   }

   public static void setCollapsed(Category category, boolean collapsed) {
      COLLAPSED.put(category, collapsed);
   }

   public static void resetPositions() {
      POSITIONS.clear();
   }
}
