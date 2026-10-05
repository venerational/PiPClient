package dev.lyfw.lyfwclient.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class PanelState {
   private static final Map<String, int[]> POSITIONS = new HashMap<>();

   private PanelState() {
   }

   public static boolean has(String key) {
      return POSITIONS.containsKey(key);
   }

   public static int x(String key, int fallback) {
      int[] p = POSITIONS.get(key);
      return p == null ? fallback : p[0];
   }

   public static int y(String key, int fallback) {
      int[] p = POSITIONS.get(key);
      return p == null ? fallback : p[1];
   }

   public static void set(String key, int x, int y) {
      POSITIONS.put(key, new int[]{x, y});
   }

   public static Iterable<Entry<String, int[]>> all() {
      return POSITIONS.entrySet();
   }
}
