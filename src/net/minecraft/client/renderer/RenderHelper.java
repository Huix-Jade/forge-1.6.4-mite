package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
   private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
   private static final Vec3 field_82884_b = Vec3.createVectorHelper(0.20000000298023224, 1.0, -0.699999988079071).normalize();
   private static final Vec3 field_82885_c = Vec3.createVectorHelper(-0.20000000298023224, 1.0, 0.699999988079071).normalize();

   public static void disableStandardItemLighting() {
      GL11.glDisable(2896);
      GL11.glDisable(16384);
      GL11.glDisable(16385);
      GL11.glDisable(2903);
   }

   public static void enableStandardItemLighting() {
      GL11.glEnable(2896);
      GL11.glEnable(16384);
      GL11.glEnable(16385);
      GL11.glEnable(2903);
      GL11.glColorMaterial(1032, 5634);
      float var0 = 0.4F;
      float var1 = 0.6F;
      float var2 = 0.0F;
      GL11.glLight(16384, 4611, setColorBuffer(field_82884_b.xCoord, field_82884_b.yCoord, field_82884_b.zCoord, 0.0));
      GL11.glLight(16384, 4609, setColorBuffer(var1, var1, var1, 1.0F));
      GL11.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16384, 4610, setColorBuffer(var2, var2, var2, 1.0F));
      GL11.glLight(16385, 4611, setColorBuffer(field_82885_c.xCoord, field_82885_c.yCoord, field_82885_c.zCoord, 0.0));
      GL11.glLight(16385, 4609, setColorBuffer(var1, var1, var1, 1.0F));
      GL11.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GL11.glLight(16385, 4610, setColorBuffer(var2, var2, var2, 1.0F));
      GL11.glShadeModel(7424);
      GL11.glLightModel(2899, setColorBuffer(var0, var0, var0, 1.0F));
   }

   private static FloatBuffer setColorBuffer(double var0, double var2, double var4, double var6) {
      return setColorBuffer((float)var0, (float)var2, (float)var4, (float)var6);
   }

   private static FloatBuffer setColorBuffer(float var0, float var1, float var2, float var3) {
      colorBuffer.clear();
      colorBuffer.put(var0).put(var1).put(var2).put(var3);
      colorBuffer.flip();
      return colorBuffer;
   }

   public static void enableGUIStandardItemLighting() {
      GL11.glPushMatrix();
      GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
      enableStandardItemLighting();
      GL11.glPopMatrix();
   }
}
