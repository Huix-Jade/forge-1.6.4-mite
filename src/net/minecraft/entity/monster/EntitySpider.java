package net.minecraft.entity.monster;

import net.minecraft.entity.EntityArachnid;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EntityLongdead;
import net.minecraft.entity.SpiderEffectsGroupData;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntitySpider extends EntityArachnid {
   public EntitySpider(World par1World) {
      super(par1World, 1.0F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
   }

   private EntitySkeleton getMountedSkeleton() {
      Object var2;
      do {
         var2 = this.worldObj.isUnderworld() ? new EntityLongdead(this.worldObj) : new EntitySkeleton(this.worldObj);
         ((EntitySkeleton)var2).setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
         ((EntitySkeleton)var2).onSpawnWithEgg((EntityLivingData)null);
      } while(((EntitySkeleton)var2).getSkeletonType() != 0);

      return (EntitySkeleton)var2;
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
      if (this.worldObj.rand.nextInt(100) == 0) {
         EntitySkeleton var2 = this.getMountedSkeleton();
         this.worldObj.spawnEntityInWorld(var2);
         var2.mountEntity(this);
      }

      if (par1EntityLivingData1 == null) {
         par1EntityLivingData1 = new SpiderEffectsGroupData();
         if (this.worldObj.difficultySetting > 2 && this.worldObj.rand.nextFloat() < 0.1F * this.worldObj.getLocationTensionFactor(this.posX, this.posY, this.posZ)) {
            ((SpiderEffectsGroupData)par1EntityLivingData1).func_111104_a(this.worldObj.rand);
         }
      }

      if (par1EntityLivingData1 instanceof SpiderEffectsGroupData) {
         int var4 = ((SpiderEffectsGroupData)par1EntityLivingData1).field_111105_a;
         if (var4 > 0 && Potion.potionTypes[var4] != null) {
            this.addPotionEffect(new PotionEffect(var4, Integer.MAX_VALUE));
         }
      }

      return (EntityLivingData)par1EntityLivingData1;
   }
}
