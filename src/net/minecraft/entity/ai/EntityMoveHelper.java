package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper {
   private EntityLiving entity;
   private double posX;
   private double posY;
   private double posZ;
   private double speed;
   private boolean update;

   public EntityMoveHelper(EntityLiving par1EntityLiving) {
      this.entity = par1EntityLiving;
      this.posX = par1EntityLiving.posX;
      this.posY = par1EntityLiving.posY;
      this.posZ = par1EntityLiving.posZ;
   }

   public boolean isUpdating() {
      return this.update;
   }

   public double getSpeed() {
      return this.speed;
   }

   public void setMoveTo(double par1, double par3, double par5, double par7) {
      this.posX = par1;
      this.posY = par3;
      this.posZ = par5;
      this.speed = par7;
      this.update = true;
   }

   public void onUpdateMoveHelper() {
      this.entity.setMoveForward(0.0F);
      if (this.update) {
         double step_height = this.getStepHeightSimply();
         this.update = false;
         int var1 = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5);
         double var2 = this.posX - this.entity.posX;
         double var4 = this.posZ - this.entity.posZ;
         double var6 = this.posY - (double)var1;
         double var8 = var2 * var2 + var6 * var6 + var4 * var4;
         if (var8 >= 2.500000277905201E-7) {
            float var10 = (float)(Math.atan2(var4, var2) * 180.0 / Math.PI) - 90.0F;
            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, var10, 30.0F);
            float speed_boost = 1.0F;
            if (this.entity.isFrenzied()) {
               speed_boost *= 1.2F;
            }

            this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() * (double)speed_boost));
            if (!this.entity.onGround) {
               return;
            }

            boolean in_cauldron = this.entity.worldObj.getBlock(this.entity.getBlockPosX(), this.entity.getBlockPosY(), this.entity.getBlockPosZ()) == Block.cauldron;
            boolean should_jump = in_cauldron || step_height > (double)this.entity.stepHeight && step_height <= 1.25 && var2 * var2 + var4 * var4 < 1.0;
            if (should_jump) {
               this.entity.getJumpHelper().setJumping();
            }
         }
      }

   }

   private float limitAngle(float par1, float par2, float par3) {
      float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);
      if (var4 > par3) {
         var4 = par3;
      }

      if (var4 < -par3) {
         var4 = -par3;
      }

      return par1 + var4;
   }

   private double getStepHeightSimply() {
      double dx = this.posX - this.entity.posX;
      double dz = this.posZ - this.entity.posZ;
      AxisAlignedBB bb = this.entity.boundingBox.copy();
      bb.minY -= 2.0;
      ++bb.maxY;
      List list = this.entity.worldObj.getCollidingBlockBounds(bb.translate(dx, 0.0, dz), this.entity);
      double block_top_y = (double)(MathHelper.floor_double(this.posY) - 2);
      Iterator i = list.iterator();

      while(i.hasNext()) {
         bb = (AxisAlignedBB)i.next();
         if (bb.maxY > block_top_y) {
            block_top_y = bb.maxY;
         }
      }

      double step_height = block_top_y - this.entity.boundingBox.minY;
      return step_height;
   }
}
