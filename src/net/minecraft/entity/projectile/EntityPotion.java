package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityPotion extends EntityThrowable {
   private ItemStack potionType;

   public EntityPotion(World par1World) {
      super(par1World);
   }

   public EntityPotion(World par1World, EntityLivingBase par2EntityLivingBase, int par3) {
      this(par1World, par2EntityLivingBase, new ItemStack(Item.potion, 1, par3));
   }

   public EntityPotion(World par1World, EntityLivingBase par2EntityLivingBase, ItemStack par3ItemStack) {
      super(par1World, par2EntityLivingBase);
      this.potionType = par3ItemStack;
   }

   public EntityPotion(World par1World, double par2, double par4, double par6, int par8) {
      this(par1World, par2, par4, par6, new ItemStack(Item.potion, 1, par8));
   }

   public EntityPotion(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
      super(par1World, par2, par4, par6);
      this.potionType = par8ItemStack;
   }

   protected float getGravityVelocity() {
      return 0.05F;
   }

   protected float func_70182_d() {
      return 0.5F;
   }

   protected float func_70183_g() {
      return -20.0F;
   }

   public void setPotionType(int par1) {
      if (this.potionType == null) {
         this.potionType = new ItemStack(Item.potion, 1, 0);
      }

      this.potionType.setItemSubtype(par1);
   }

   public int getPotionType() {
      if (this.potionType == null) {
         this.potionType = new ItemStack(Item.potion, 1, 0);
      }

      return this.potionType.getItemSubtype();
   }

   protected void onImpact(RaycastCollision rc) {
      if (!this.worldObj.isRemote) {
         List var2 = Item.potion.getEffects(this.potionType);
         if (var2 != null && !var2.isEmpty()) {
            AxisAlignedBB var3 = this.boundingBox.expand(4.0, 2.0, 4.0);
            List var4 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, var3);
            if (var4 != null && !var4.isEmpty()) {
               Iterator var5 = var4.iterator();

               label68:
               while(true) {
                  EntityLivingBase var6;
                  double var7;
                  do {
                     if (!var5.hasNext()) {
                        break label68;
                     }

                     var6 = (EntityLivingBase)var5.next();
                     var7 = this.getDistanceSqToEntity(var6);
                  } while(!(var7 < 16.0));

                  double var9 = 1.0 - Math.sqrt(var7) / 4.0;
                  if (var6 == rc.getEntityHit()) {
                     var9 = 1.0;
                  }

                  Iterator var11 = var2.iterator();

                  while(var11.hasNext()) {
                     PotionEffect var12 = (PotionEffect)var11.next();
                     int var13 = var12.getPotionID();
                     Potion potion = Potion.get(var13);
                     boolean apply_effect = true;
                     if (potion.isBadEffect()) {
                        EntityLivingBase thrower = this.getThrower();
                        if (var6 == thrower && !thrower.isEntityPlayer()) {
                           apply_effect = false;
                        } else if (thrower instanceof EntityWitch && var6 instanceof EntityWolf && ((EntityWolf)var6).is_witch_ally) {
                           apply_effect = false;
                        }
                     }

                     if (apply_effect) {
                        if (Potion.potionTypes[var13].isInstant()) {
                           Potion.potionTypes[var13].affectEntity(this.getThrower(), var6, var12.getAmplifier(), var9);
                        } else {
                           int var14 = (int)(var9 * (double)var12.getDuration() + 0.5);
                           if (var14 > 20) {
                              var6.addPotionEffect(new PotionEffect(var13, var14, var12.getAmplifier()));
                           }
                        }
                     }
                  }
               }
            }
         }

         this.worldObj.playAuxSFX(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), this.getPotionType());
         this.setDead();
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("Potion")) {
         this.potionType = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("Potion"));
      } else {
         this.setPotionType(par1NBTTagCompound.getInteger("potionValue"));
      }

      if (this.potionType == null) {
         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.potionType != null) {
         par1NBTTagCompound.setCompoundTag("Potion", this.potionType.writeToNBT(new NBTTagCompound()));
      }

   }

   public Item getModelItem() {
      return Item.potion;
   }
}
