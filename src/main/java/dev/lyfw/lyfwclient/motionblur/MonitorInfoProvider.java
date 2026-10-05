package dev.lyfw.lyfwclient.motionblur;

import net.minecraft.client.Minecraft;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class MonitorInfoProvider {
   private static long lastMonitorHandle = 0L;
   private static int lastRefreshRate = 60;
   private static long lastCheckTime = 0L;
   private static final long CHECK_INTERVAL_NS = 1000000000L;

   public static void updateDisplayInfo() {
      long now = System.nanoTime();
      if (now - lastCheckTime >= 1000000000L) {
         lastCheckTime = now;
         Minecraft client = Minecraft.getInstance();
         long window = client.getWindow().handle();
         long monitor = GLFW.glfwGetWindowMonitor(window);
         if (monitor == 0L) {
            monitor = getMonitorFromWindowPosition(window, client.getWindow().getScreenWidth(), client.getWindow().getScreenHeight());
         }

         if (monitor != lastMonitorHandle) {
            lastRefreshRate = detectRefreshRate(monitor);
            lastMonitorHandle = monitor;
         }
      }
   }

   public static int getRefreshRate() {
      return lastRefreshRate;
   }

   private static long getMonitorFromWindowPosition(long window, int windowWidth, int windowHeight) {
      int[] winX = new int[1];
      int[] winY = new int[1];
      GLFW.glfwGetWindowPos(window, winX, winY);
      int centerX = winX[0] + windowWidth / 2;
      int centerY = winY[0] + windowHeight / 2;
      long result = GLFW.glfwGetPrimaryMonitor();
      PointerBuffer monitors = GLFW.glfwGetMonitors();
      if (monitors != null) {
         for (int i = 0; i < monitors.limit(); i++) {
            long m = monitors.get(i);
            int[] mx = new int[1];
            int[] my = new int[1];
            GLFW.glfwGetMonitorPos(m, mx, my);
            GLFWVidMode mode = GLFW.glfwGetVideoMode(m);
            if (mode != null && centerX >= mx[0] && centerX < mx[0] + mode.width() && centerY >= my[0] && centerY < my[0] + mode.height()) {
               result = m;
               break;
            }
         }
      }

      return result;
   }

   private static int detectRefreshRate(long monitor) {
      GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
      return vidMode != null ? vidMode.refreshRate() : 60;
   }
}
