package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityHanging extends Entity {
   private int tickCounter1;
   public int hangingDirection;
   public int xPosition;
   public int yPosition;
   public int zPosition;

   public EntityHanging(World par1World) {
      super(par1World);
      this.yOffset = 0.0F;
      this.setSize(0.5F, 0.5F);
   }

   public EntityHanging(World par1World, int par2, int par3, int par4, int par5) {
      this(par1World);
      this.xPosition = par2;
      this.yPosition = par3;
      this.zPosition = par4;
   }

   protected void entityInit() {
   }

   public void setDirection(int par1) {
      this.hangingDirection = par1;
      this.prevRotationYaw = this.rotationYaw = (float)(par1 * 90);
      float var2 = (float)this.getWidthPixels();
      float var3 = (float)this.getHeightPixels();
      float var4 = (float)this.getWidthPixels();
      if (par1 != 2 && par1 != 0) {
         var2 = 0.5F;
      } else {
         var4 = 0.5F;
         this.rotationYaw = this.prevRotationYaw = (float)(Direction.rotateOpposite[par1] * 90);
      }

      var2 /= 32.0F;
      var3 /= 32.0F;
      var4 /= 32.0F;
      float var5 = (float)this.xPosition + 0.5F;
      float var6 = (float)this.yPosition + 0.5F;
      float var7 = (float)this.zPosition + 0.5F;
      float var8 = 0.5625F;
      if (par1 == 2) {
         var7 -= var8;
      }

      if (par1 == 1) {
         var5 -= var8;
      }

      if (par1 == 0) {
         var7 += var8;
      }

      if (par1 == 3) {
         var5 += var8;
      }

      if (par1 == 2) {
         var5 -= this.func_70517_b(this.getWidthPixels());
      }

      if (par1 == 1) {
         var7 += this.func_70517_b(this.getWidthPixels());
      }

      if (par1 == 0) {
         var5 += this.func_70517_b(this.getWidthPixels());
      }

      if (par1 == 3) {
         var7 -= this.func_70517_b(this.getWidthPixels());
      }

      var6 += this.func_70517_b(this.getHeightPixels());
      this.setPosition((double)var5, (double)var6, (double)var7);
      float var9 = -0.03125F;
      this.boundingBox.setBounds((double)(var5 - var2 - var9), (double)(var6 - var3 - var9), (double)(var7 - var4 - var9), (double)(var5 + var2 + var9), (double)(var6 + var3 + var9), (double)(var7 + var4 + var9));
   }

   private float func_70517_b(int par1) {
      return par1 == 32 ? 0.5F : (par1 == 64 ? 0.5F : 0.0F);
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
         this.tickCounter1 = 0;
         if (!this.isDead && !this.onValidSurface()) {
            this.setDead();
            this.onBroken((Entity)null);
         }
      }

   }

   public boolean onValidSurface() {
      if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
         return false;
      } else {
         int var1 = Math.max(1, this.getWidthPixels() / 16);
         int var2 = Math.max(1, this.getHeightPixels() / 16);
         int var3 = this.xPosition;
         int var4 = this.yPosition;
         int var5 = this.zPosition;
         if (this.hangingDirection == 2) {
            var3 = MathHelper.floor_double(this.posX - (double)((float)this.getWidthPixels() / 32.0F));
         }

         if (this.hangingDirection == 1) {
            var5 = MathHelper.floor_double(this.posZ - (double)((float)this.getWidthPixels() / 32.0F));
         }

         if (this.hangingDirection == 0) {
            var3 = MathHelper.floor_double(this.posX - (double)((float)this.getWidthPixels() / 32.0F));
         }

         if (this.hangingDirection == 3) {
            var5 = MathHelper.floor_double(this.posZ - (double)((float)this.getWidthPixels() / 32.0F));
         }

         EnumFace face = this.hangingDirection == 0 ? EnumFace.SOUTH : (this.hangingDirection == 1 ? EnumFace.WEST : (this.hangingDirection == 2 ? EnumFace.NORTH : EnumFace.EAST));
         var4 = MathHelper.floor_double(this.posY - (double)((float)this.getHeightPixels() / 32.0F));

         for(int var6 = 0; var6 < var1; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
               Block block;
               int metadata;
               Material var8;
               if (this.hangingDirection != 2 && this.hangingDirection != 0) {
                  block = this.worldObj.getBlock(this.xPosition, var4 + var7, var5 + var6);
                  metadata = this.worldObj.getBlockMetadata(this.xPosition, var4 + var7, var5 + var6);
                  var8 = this.worldObj.getBlockMaterial(this.xPosition, var4 + var7, var5 + var6);
               } else {
                  block = this.worldObj.getBlock(var3 + var6, var4 + var7, this.zPosition);
                  metadata = this.worldObj.getBlockMetadata(var3 + var6, var4 + var7, this.zPosition);
                  var8 = this.worldObj.getBlockMaterial(var3 + var6, var4 + var7, this.zPosition);
               }

               if (!var8.isSolid()) {
                  return false;
               }

               if (!block.isFaceFlatAndSolid(metadata, face)) {
                  return false;
               }
            }
         }

         List var9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
         Iterator var10 = var9.iterator();

         while(var10.hasNext()) {
            Entity var11 = (Entity)var10.next();
            if (var11 instanceof EntityHanging) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         this.setDead();
         this.setBeenAttacked();
         this.onBroken(damage.getResponsibleEntity());
         return result.setEntityWasDestroyed();
      }
   }

   public void moveEntity(double par1, double par3, double par5) {
      if (!this.worldObj.isRemote && !this.isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }

   }

   public void addVelocity(double par1, double par3, double par5) {
      if (!this.worldObj.isRemote && !this.isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setByte("Direction", (byte)this.hangingDirection);
      par1NBTTagCompound.setInteger("TileX", this.xPosition);
      par1NBTTagCompound.setInteger("TileY", this.yPosition);
      par1NBTTagCompound.setInteger("TileZ", this.zPosition);
      switch (this.hangingDirection) {
         case 0:
            par1NBTTagCompound.setByte("Dir", (byte)2);
            break;
         case 1:
            par1NBTTagCompound.setByte("Dir", (byte)1);
            break;
         case 2:
            par1NBTTagCompound.setByte("Dir", (byte)0);
            break;
         case 3:
            par1NBTTagCompound.setByte("Dir", (byte)3);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      if (par1NBTTagCompound.hasKey("Direction")) {
         this.hangingDirection = par1NBTTagCompound.getByte("Direction");
      } else {
         switch (par1NBTTagCompound.getByte("Dir")) {
            case 0:
               this.hangingDirection = 2;
               break;
            case 1:
               this.hangingDirection = 1;
               break;
            case 2:
               this.hangingDirection = 0;
               break;
            case 3:
               this.hangingDirection = 3;
         }
      }

      this.xPosition = par1NBTTagCompound.getInteger("TileX");
      this.yPosition = par1NBTTagCompound.getInteger("TileY");
      this.zPosition = par1NBTTagCompound.getInteger("TileZ");
      this.setDirection(this.hangingDirection);
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   public abstract void onBroken(Entity var1);

   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   public float getCollisionBorderSize(Entity for_raycast_from_this_entity) {
      return 0.0F;
   }
}
