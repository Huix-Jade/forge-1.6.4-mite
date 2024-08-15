package net.minecraft.client.renderer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;

class CallableParticlePositionInfo implements Callable {
   // $FF: synthetic field
   final double posX;
   // $FF: synthetic field
   final double posY;
   // $FF: synthetic field
   final double posZ;
   // $FF: synthetic field
   final RenderGlobal globalRenderer;

   CallableParticlePositionInfo(RenderGlobal var1, double var2, double var4, double var6) {
      this.globalRenderer = var1;
      this.posX = var2;
      this.posY = var4;
      this.posZ = var6;
   }

   public String callParticlePositionInfo() {
      return CrashReportCategory.func_85074_a(this.posX, this.posY, this.posZ);
   }

   // $FF: synthetic method
   public Object call() {
      return this.callParticlePositionInfo();
   }
}
