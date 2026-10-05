package dev.lyfw.lyfwclient.motionblur;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.util.Map;
import net.minecraft.client.renderer.PostChain;

public final class ManagedUniformBuffer {
   private final String debugName;
   private final int sizeBytes;
   private PostChain owner = null;
   private GpuBuffer buffer = null;

   public ManagedUniformBuffer(String debugName, int sizeBytes) {
      this.debugName = debugName;
      this.sizeBytes = sizeBytes;
   }

   public GpuBuffer put(PostChain chain, Map<String, GpuBuffer> uniformBuffers, String uniformName) {
      GpuBuffer ubo = this.get(chain);
      GpuBuffer old = uniformBuffers.get(uniformName);
      if (old == ubo) {
         return ubo;
      } else {
         old = uniformBuffers.put(uniformName, ubo);
         if (old != null && old != ubo) {
            GpuBufferUtil.closeQuietly(old);
         }

         return ubo;
      }
   }

   public void reset() {
      GpuBufferUtil.closeQuietly(this.buffer);
      this.owner = null;
      this.buffer = null;
   }

   public boolean resetIfClosed(RuntimeException e) {
      if (!GpuBufferUtil.isClosedBufferException(e)) {
         return false;
      } else {
         this.reset();
         return true;
      }
   }

   private GpuBuffer get(PostChain chain) {
      if (chain != this.owner) {
         this.reset();
         this.owner = chain;
      }

      if (this.buffer == null) {
         this.buffer = GpuBufferUtil.createUBO(this.debugName, this.sizeBytes);
      }

      return this.buffer;
   }

   public static final class Ring {
      private final String debugName;
      private final int sizeBytes;
      private final GpuBuffer[] buffers;
      private PostChain owner = null;
      private int index = 0;

      public Ring(String debugName, int sizeBytes, int count) {
         this.debugName = debugName;
         this.sizeBytes = sizeBytes;
         this.buffers = new GpuBuffer[count];
      }

      public GpuBuffer putNext(PostChain chain, Map<String, GpuBuffer> uniformBuffers, String uniformName) {
         GpuBuffer ubo = this.next(chain);
         GpuBuffer old = uniformBuffers.get(uniformName);
         if (old == ubo) {
            return ubo;
         } else {
            old = uniformBuffers.put(uniformName, ubo);
            if (old != null && old != ubo && !this.owns(old)) {
               GpuBufferUtil.closeQuietly(old);
            }

            return ubo;
         }
      }

      public void reset() {
         for (int i = 0; i < this.buffers.length; i++) {
            GpuBufferUtil.closeQuietly(this.buffers[i]);
            this.buffers[i] = null;
         }

         this.owner = null;
         this.index = 0;
      }

      public boolean resetIfClosed(RuntimeException e) {
         if (!GpuBufferUtil.isClosedBufferException(e)) {
            return false;
         } else {
            this.reset();
            return true;
         }
      }

      private GpuBuffer next(PostChain chain) {
         if (chain != this.owner) {
            this.reset();
            this.owner = chain;
         }

         GpuBuffer ubo = this.buffers[this.index];
         if (ubo == null) {
            ubo = GpuBufferUtil.createUBO(this.debugName, this.sizeBytes);
            this.buffers[this.index] = ubo;
         }

         this.index = (this.index + 1) % this.buffers.length;
         return ubo;
      }

      private boolean owns(GpuBuffer buffer) {
         for (GpuBuffer owned : this.buffers) {
            if (owned == buffer) {
               return true;
            }
         }

         return false;
      }
   }
}
