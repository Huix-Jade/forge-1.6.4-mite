package net.minecraft.entity.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   public EntityExpBottle(World par1World) {
      super(par1World);
   }

   public EntityExpBottle(World par1World, EntityLivingBase par2EntityLivingBase) {
      super(par1World, par2EntityLivingBase);
   }

   public EntityExpBottle(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   protected float getGravityVelocity() {
      return 0.07F;
   }

   protected float func_70182_d() {
      return 0.7F;
   }

   protected float func_70183_g() {
      return -20.0F;
   }

   protected void onImpact(RaycastCollision rc) {
      if (!this.worldObj.isRemote) {
         this.worldObj.playAuxSFX(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), 0);
         int var2 = Enchantment.getExperienceCost(2);

         while(var2 > 0) {
            int var3 = EntityXPOrb.getXPSplit(var2);
            var2 -= var3;
            this.worldObj.spawnEntityInWorld((new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var3)).setCreatedByBottleOfEnchanting());
         }

         this.setDead();
      }

   }

   public Item getModelItem() {
      return Item.expBottle;
   }
}
