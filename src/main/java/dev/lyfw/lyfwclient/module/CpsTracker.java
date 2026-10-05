package dev.lyfw.lyfwclient.module;

import java.util.ArrayList;
import java.util.List;

public class CpsTracker {
   private static final List<Long> leftClicks = new ArrayList<>();
   private static final List<Long> rightClicks = new ArrayList<>();

   public static void recordLeftClick() {
      leftClicks.add(System.currentTimeMillis());
   }

   public static void recordRightClick() {
      rightClicks.add(System.currentTimeMillis());
   }

   public static void tick() {
      long now = System.currentTimeMillis();
      leftClicks.removeIf(t -> t < now - 1000L);
      rightClicks.removeIf(t -> t < now - 1000L);
   }

   public static int getLeftCps() {
      return leftClicks.size();
   }

   public static int getRightCps() {
      return rightClicks.size();
   }
}
