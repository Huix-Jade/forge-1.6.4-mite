package net.minecraft.village;

import net.minecraft.entity.EntityLivingBase;

class VillageAgressor {
   public EntityLivingBase agressor;
   public int agressionTime;
   // $FF: synthetic field
   final Village villageObj;

   VillageAgressor(Village var1, EntityLivingBase var2, int var3) {
      this.villageObj = var1;
      this.agressor = var2;
      this.agressionTime = var3;
   }
}
