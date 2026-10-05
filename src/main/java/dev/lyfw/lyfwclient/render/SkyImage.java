package dev.lyfw.lyfwclient.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class SkyImage {
   private static final int AROUND = 48;
   private static final int DOWN = 24;
   private static final float RADIUS = 100.0F;
   private static final int QUADS = 2304;
   private static GpuBuffer sphere;

   private SkyImage() {
   }

   public static void draw(Identifier frame, float opacity) {
      Minecraft mc = Minecraft.getInstance();
      AbstractTexture texture = mc.getTextureManager().getTexture(frame);
      if (texture != null && !(opacity <= 0.0F)) {
         if (sphere == null) {
            sphere = buildSphere();
         }

         AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(Mode.QUADS);
         GpuBuffer indexBuffer = indices.getBuffer(13824);
         GpuBufferSlice transforms = RenderSystem.getDynamicUniforms()
            .writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, opacity), new Vector3f(), new Matrix4f());
         RenderPass pass = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
               () -> "Pip sky image",
               mc.getMainRenderTarget().getColorTextureView(),
               OptionalInt.empty(),
               mc.getMainRenderTarget().getDepthTextureView(),
               OptionalDouble.empty()
            );

         try {
            pass.setPipeline(RenderPipelines.END_SKY);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
            pass.setVertexBuffer(0, sphere);
            pass.setIndexBuffer(indexBuffer, indices.type());
            pass.drawIndexed(0, 0, 13824, 1);
         } catch (Throwable var11) {
            if (pass != null) {
               try {
                  pass.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (pass != null) {
            pass.close();
         }
      }
   }

   private static GpuBuffer buildSphere() {
      ByteBufferBuilder allocator = ByteBufferBuilder.exactlySized(9216 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize());

      GpuBuffer var17;
      try {
         BufferBuilder builder = new BufferBuilder(allocator, Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int j = 0; j < 24; j++) {
            for (int i = 0; i < 48; i++) {
               float u0 = i / 48.0F;
               float u1 = (i + 1) / 48.0F;
               float v0 = j / 24.0F;
               float v1 = (j + 1) / 24.0F;
               float[] a = point(u0, v0);
               float[] b = point(u1, v0);
               float[] c = point(u1, v1);
               float[] d = point(u0, v1);
               builder.addVertex(a[0], a[1], a[2]).setUv(u0, v0).setColor(-1);
               builder.addVertex(b[0], b[1], b[2]).setUv(u1, v0).setColor(-1);
               builder.addVertex(c[0], c[1], c[2]).setUv(u1, v1).setColor(-1);
               builder.addVertex(d[0], d[1], d[2]).setUv(u0, v1).setColor(-1);
               builder.addVertex(d[0], d[1], d[2]).setUv(u0, v1).setColor(-1);
               builder.addVertex(c[0], c[1], c[2]).setUv(u1, v1).setColor(-1);
               builder.addVertex(b[0], b[1], b[2]).setUv(u1, v0).setColor(-1);
               builder.addVertex(a[0], a[1], a[2]).setUv(u0, v0).setColor(-1);
            }
         }

         MeshData built = builder.buildOrThrow();

         try {
            var17 = RenderSystem.getDevice().createBuffer(() -> "Pip sky image sphere", 40, built.vertexBuffer());
         } catch (Throwable var14) {
            if (built != null) {
               try {
                  built.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }
            }

            throw var14;
         }

         if (built != null) {
            built.close();
         }
      } catch (Throwable var15) {
         if (allocator != null) {
            try {
               allocator.close();
            } catch (Throwable var12) {
               var15.addSuppressed(var12);
            }
         }

         throw var15;
      }

      if (allocator != null) {
         allocator.close();
      }

      return var17;
   }

   private static float[] point(float u, float v) {
      float yaw = u * (float) (Math.PI * 2);
      float pitch = v * (float) Math.PI;
      float ring = Mth.sin(pitch) * 100.0F;
      return new float[]{-Mth.sin(yaw) * ring, Mth.cos(pitch) * 100.0F, Mth.cos(yaw) * ring};
   }
}
