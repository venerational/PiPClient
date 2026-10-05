package dev.lyfw.lyfwclient.motionblur;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class GpuBufferUtil {
   private static final int UBO_USAGE = 130;
   private static Method createBufferMethod = null;

   private GpuBufferUtil() {
   }

   public static GpuBuffer createUBO(String debugName, int sizeBytes) {
      Object device = RenderSystem.getDevice();
      Supplier<String> label = () -> "lyfw-client:motionblur:" + debugName;

      try {
         if (createBufferMethod == null) {
            createBufferMethod = device.getClass().getMethod("createBuffer", Supplier.class, int.class, long.class);
         }

         return (GpuBuffer)createBufferMethod.invoke(device, label, 130, (long)sizeBytes);
      } catch (NoSuchMethodException var5) {
         throw new RuntimeException("[MotionBlur] No compatible createBuffer found on " + device.getClass(), var5);
      } catch (ReflectiveOperationException var6) {
         throw new RuntimeException("[MotionBlur] GpuBufferUtil.createUBO failed", var6);
      }
   }

   public static void closeQuietly(GpuBuffer buffer) {
      if (buffer != null) {
         try {
            buffer.close();
         } catch (RuntimeException var2) {
         }
      }
   }

   public static boolean isClosedBufferException(RuntimeException e) {
      String message = e.getMessage();
      return message != null && message.toLowerCase().contains("closed");
   }
}
