package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EntityAIControlledByPlayer extends EntityAIBase {
   private final EntityLiving thisEntity;
   private final float maxSpeed;
   private float currentSpeed;
   private boolean speedBoosted;
   private int speedBoostTime;
   private int maxSpeedBoostTime;

   public EntityAIControlledByPlayer(EntityLiving par1EntityLiving, float par2) {
      this.thisEntity = par1EntityLiving;
      this.maxSpeed = par2;
      this.setMutexBits(7);
   }

   public void startExecuting() {
      this.currentSpeed = 0.0F;
   }

   public void resetTask() {
      this.speedBoosted = false;
      this.currentSpeed = 0.0F;
   }

   public boolean shouldExecute() {
      return this.thisEntity.isEntityAlive() && this.thisEntity.riddenByEntity != null && this.thisEntity.riddenByEntity instanceof EntityPlayer && (this.speedBoosted || this.thisEntity.canBeSteered());
   }

   public void updateTask() {
      EntityPlayer var1 = (EntityPlayer)this.thisEntity.riddenByEntity;
      EntityCreature var2 = (EntityCreature)this.thisEntity;
      float var3 = MathHelper.wrapAngleTo180_float(var1.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;
      if (var3 > 5.0F) {
         var3 = 5.0F;
      }

      if (var3 < -5.0F) {
         var3 = -5.0F;
      }

      this.thisEntity.rotationYaw = MathHelper.wrapAngleTo180_float(this.thisEntity.rotationYaw + var3);
      if (this.currentSpeed < this.maxSpeed) {
         this.currentSpeed += (this.maxSpeed - this.currentSpeed) * 0.01F;
      }

      if (this.currentSpeed > this.maxSpeed) {
         this.currentSpeed = this.maxSpeed;
      }

      int var4 = MathHelper.floor_double(this.thisEntity.posX);
      int var5 = MathHelper.floor_double(this.thisEntity.posY);
      int var6 = MathHelper.floor_double(this.thisEntity.posZ);
      float var7 = this.currentSpeed;
      if (this.speedBoosted) {
         if (this.speedBoostTime++ > this.maxSpeedBoostTime) {
            this.speedBoosted = false;
         }

         var7 += var7 * 1.15F * MathHelper.sin((float)this.speedBoostTime / (float)this.maxSpeedBoostTime * 3.1415927F);
      }

      float var8 = 0.91F;
      if (this.thisEntity.onGround) {
         var8 = 0.54600006F;
         int var9 = this.thisEntity.worldObj.getBlockId(MathHelper.floor_float((float)var4), MathHelper.floor_float((float)var5) - 1, MathHelper.floor_float((float)var6));
         if (var9 > 0) {
            var8 = Block.blocksList[var9].slipperiness * 0.91F;
         }
      }

      float var23 = 0.16277136F / (var8 * var8 * var8);
      float var10 = MathHelper.sin(var2.rotationYaw * 3.1415927F / 180.0F);
      float var11 = MathHelper.cos(var2.rotationYaw * 3.1415927F / 180.0F);
      float var12 = var2.getAIMoveSpeed() * var23;
      float var13 = Math.max(var7, 1.0F);
      var13 = var12 / var13;
      float var14 = var7 * var13;
      float var15 = -(var14 * var10);
      float var16 = var14 * var11;
      if (MathHelper.abs(var15) > MathHelper.abs(var16)) {
         if (var15 < 0.0F) {
            var15 -= this.thisEntity.width / 2.0F;
         }

         if (var15 > 0.0F) {
            var15 += this.thisEntity.width / 2.0F;
         }

         var16 = 0.0F;
      } else {
         var15 = 0.0F;
         if (var16 < 0.0F) {
            var16 -= this.thisEntity.width / 2.0F;
         }

         if (var16 > 0.0F) {
            var16 += this.thisEntity.width / 2.0F;
         }
      }

      int var17 = MathHelper.floor_double(this.thisEntity.posX + (double)var15);
      int var18 = MathHelper.floor_double(this.thisEntity.posZ + (double)var16);
      PathPoint var19 = new PathPoint(MathHelper.floor_float(this.thisEntity.width + 1.0F), MathHelper.floor_float(this.thisEntity.height + var1.height + 1.0F), MathHelper.floor_float(this.thisEntity.width + 1.0F));
      if (var4 != var17 || var6 != var18) {
         int var20 = this.thisEntity.worldObj.getBlockId(var4, var5, var6);
         int var21 = this.thisEntity.worldObj.getBlockId(var4, var5 - 1, var6);
         boolean var22 = this.func_98216_b(var20, var4, var5, var6) || Block.blocksList[var20] == null && this.func_98216_b(var21, var4, var5 - 1, var6);
         if (!var22 && PathFinder.func_82565_a(this.thisEntity, var17, var5, var18, var19, false, false, true) == 0 && PathFinder.func_82565_a(this.thisEntity, var4, var5 + 1, var6, var19, false, false, true) == 1 && PathFinder.func_82565_a(this.thisEntity, var17, var5 + 1, var18, var19, false, false, true) == 1) {
            var2.getJumpHelper().setJumping();
         }
      }

      if (!var1.capabilities.isCreativeMode && this.currentSpeed >= this.maxSpeed * 0.5F && this.thisEntity.getRNG().nextFloat() < 0.006F && !this.speedBoosted) {
         ItemStack var24 = var1.getHeldItemStack();
         if (var24 != null && var24.getItem() instanceof ItemCarrotOnAStick) {
            var24.tryDamageItem(DamageSource.pig_nibble, 1, var1);
         }
      }

      this.thisEntity.moveEntityWithHeading(0.0F, var7);
   }

   private boolean func_98216_b(int par1) {
      return Block.blocksList[par1] != null && (Block.blocksList[par1].getRenderType() == 10 || Block.blocksList[par1] instanceof BlockSlab);
   }

   private boolean func_98216_b(int block_id, int x, int y, int z) {
      Block block = Block.getBlock(block_id);
      if (block == null) {
         return false;
      } else {
         return block.getRenderType() == 10 ? true : block.isSingleSlabLower(this.thisEntity.worldObj.getBlockMetadata(x, y, z));
      }
   }

   public boolean isSpeedBoosted() {
      return this.speedBoosted;
   }

   public void boostSpeed() {
      this.speedBoosted = true;
      this.speedBoostTime = 0;
      this.maxSpeedBoostTime = this.thisEntity.getRNG().nextInt(841) + 140;
   }

   public boolean isControlledByPlayer() {
      return !this.isSpeedBoosted() && this.currentSpeed > this.maxSpeed * 0.3F;
   }
}
