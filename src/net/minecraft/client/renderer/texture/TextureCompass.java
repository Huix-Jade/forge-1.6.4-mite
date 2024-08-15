package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TextureCompass extends TextureAtlasSprite {
   public double currentAngle;
   public double angleDelta;

   public TextureCompass(String var1) {
      super(var1);
   }

   public void updateAnimation() {
      Minecraft var1 = Minecraft.getMinecraft();
      if (var1.theWorld != null && var1.thePlayer != null) {
         this.updateCompass(var1.theWorld, var1.thePlayer.posX, var1.thePlayer.posZ, (double)var1.thePlayer.rotationYaw, false, false);
      } else {
         this.updateCompass((World)null, 0.0, 0.0, 0.0, true, false);
      }

   }

   public void updateCompass(World var1, double var2, double var4, double var6, boolean var8, boolean var9) {
      if (!this.framesTextureData.isEmpty()) {
         double var10 = 0.0;
         if (var1 != null && !var8) {
            ChunkCoordinates var12 = var1.getSpawnPoint();
            double var13 = (double)var12.posX - var2;
            double var15 = (double)var12.posZ - var4;
            var6 %= 360.0;
            var10 = -((var6 - 90.0) * Math.PI / 180.0 - Math.atan2(var15, var13));
            if (!var1.provider.isSurfaceWorld()) {
               var10 = Math.random() * 3.1415927410125732 * 2.0;
            }
         }

         if (var9) {
            this.currentAngle = var10;
         } else {
            double var17;
            for(var17 = var10 - this.currentAngle; var17 < -3.141592653589793; var17 += 6.283185307179586) {
            }

            while(var17 >= Math.PI) {
               var17 -= 6.283185307179586;
            }

            if (var17 < -1.0) {
               var17 = -1.0;
            }

            if (var17 > 1.0) {
               var17 = 1.0;
            }

            this.angleDelta += var17 * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle += this.angleDelta;
         }

         int var18;
         for(var18 = (int)((this.currentAngle / 6.283185307179586 + 1.0) * (double)this.framesTextureData.size()) % this.framesTextureData.size(); var18 < 0; var18 = (var18 + this.framesTextureData.size()) % this.framesTextureData.size()) {
         }

         if (var18 != this.frameCounter) {
            this.frameCounter = var18;
            TextureUtil.uploadTextureSub((int[])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
         }

      }
   }
}
