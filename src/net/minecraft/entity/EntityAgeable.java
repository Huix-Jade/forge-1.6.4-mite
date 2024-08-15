package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
   private float field_98056_d = -1.0F;
   private float field_98057_e;

   public EntityAgeable(World par1World) {
      super(par1World);
   }

   public abstract EntityAgeable createChild(EntityAgeable var1);

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(12, new Integer(0));
   }

   public int getGrowingAge() {
      return this.dataWatcher.getWatchableObjectInt(12);
   }

   public void addGrowth(int par1) {
      int var2 = this.getGrowingAge();
      var2 += par1 * 5;
      if (var2 > 0) {
         var2 = 0;
      }

      this.setGrowingAge(var2);
   }

   public void setGrowingAge(int par1) {
      boolean was_child = this.isChild();
      if (was_child && par1 >= 0 && this.worldObj.getBlock(this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ()) == Block.cauldron && !this.worldObj.isAirOrPassableBlock(this.getBlockPosX(), this.getBlockPosY() + 1, this.getBlockPosZ(), true)) {
         par1 = -1;
      }

      this.dataWatcher.updateObject(12, par1);
      this.setScaleForAge(this.isChild());
      if (was_child && !this.isChild()) {
         this.pushOutOfBlocks();
      }

   }

   public static int getGrowingAgeOfNewborn() {
      return -64000;
   }

   public void setGrowingAgeToNewborn() {
      this.setGrowingAge(getGrowingAgeOfNewborn());
   }

   public void setGrowingAgeAfterBreeding() {
      this.setGrowingAge(16000);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("Age", this.getGrowingAge());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setGrowingAge(par1NBTTagCompound.getInteger("Age"));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.worldObj.isRemote) {
         this.setScaleForAge(this.isChild());
      } else {
         int var1 = this.getGrowingAge();
         if (var1 < 0) {
            if (this instanceof EntityHorse) {
               if (this.getTicksExistedWithOffset() % 200 == 0) {
                  this.setGrowingAge(Math.min(var1 + 200, 0));
               }
            } else {
               ++var1;
               this.setGrowingAge(var1);
            }
         } else if (var1 > 0) {
            --var1;
            this.setGrowingAge(var1);
         }
      }

   }

   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   public void setScaleForAge(boolean par1) {
      this.setScale(par1 ? 0.5F : 1.0F);
   }

   protected final void setSize(float par1, float par2) {
      boolean var3 = this.field_98056_d > 0.0F;
      this.field_98056_d = par1;
      this.field_98057_e = par2;
      if (!var3) {
         this.setScale(1.0F);
      }

   }

   protected final void setScale(float par1) {
      super.setSize(this.field_98056_d * par1, this.field_98057_e * par1);
   }
}
