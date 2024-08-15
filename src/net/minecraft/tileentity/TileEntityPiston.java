package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;

public class TileEntityPiston extends TileEntity {
   private int storedBlockID;
   private int storedMetadata;
   private int storedOrientation;
   private boolean extending;
   private boolean shouldHeadBeRendered;
   private float progress;
   private float lastProgress;
   private List pushedObjects = new ArrayList();

   public TileEntityPiston() {
   }

   public TileEntityPiston(int par1, int par2, int par3, boolean par4, boolean par5) {
      this.storedBlockID = par1;
      this.storedMetadata = par2;
      this.storedOrientation = par3;
      this.extending = par4;
      this.shouldHeadBeRendered = par5;
   }

   public int getStoredBlockID() {
      return this.storedBlockID;
   }

   public int getBlockMetadata() {
      return this.storedMetadata;
   }

   public boolean isExtending() {
      return this.extending;
   }

   public int getPistonOrientation() {
      return this.storedOrientation;
   }

   public boolean shouldRenderHead() {
      return this.shouldHeadBeRendered;
   }

   public float getProgress(float par1) {
      if (par1 > 1.0F) {
         par1 = 1.0F;
      }

      return this.lastProgress + (this.progress - this.lastProgress) * par1;
   }

   public float getOffsetX(float par1) {
      return this.extending ? (this.getProgress(par1) - 1.0F) * (float)Facing.offsetsXForSide[this.storedOrientation] : (1.0F - this.getProgress(par1)) * (float)Facing.offsetsXForSide[this.storedOrientation];
   }

   public float getOffsetY(float par1) {
      return this.extending ? (this.getProgress(par1) - 1.0F) * (float)Facing.offsetsYForSide[this.storedOrientation] : (1.0F - this.getProgress(par1)) * (float)Facing.offsetsYForSide[this.storedOrientation];
   }

   public float getOffsetZ(float par1) {
      return this.extending ? (this.getProgress(par1) - 1.0F) * (float)Facing.offsetsZForSide[this.storedOrientation] : (1.0F - this.getProgress(par1)) * (float)Facing.offsetsZForSide[this.storedOrientation];
   }

   private void updatePushedObjects(float par1, float par2) {
      if (this.extending) {
         par1 = 1.0F - par1;
      } else {
         --par1;
      }

      AxisAlignedBB var3 = Block.pistonMoving.getAxisAlignedBB(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, par1, this.storedOrientation);
      if (var3 != null) {
         List var4 = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, var3);
         if (!var4.isEmpty()) {
            this.pushedObjects.addAll(var4);

            Entity var6;
            double dx;
            double dy;
            double dz;
            for(Iterator var5 = this.pushedObjects.iterator(); var5.hasNext(); var6.moveEntity(dx, dy, dz)) {
               var6 = (Entity)var5.next();
               dx = 0.0;
               dy = 0.0;
               dz = 0.0;
               if (this.storedOrientation == 0) {
                  dy = var3.minY - var6.boundingBox.maxY;
                  if (dy > 0.0) {
                     dy = 0.0;
                  }
               } else if (this.storedOrientation == 1) {
                  dy = var3.maxY - var6.boundingBox.minY;
                  if (dy < 0.0) {
                     dy = 0.0;
                  }
               } else if (this.storedOrientation == 2) {
                  dz = var3.minZ - var6.boundingBox.maxZ;
                  if (dz > 0.0) {
                     dz = 0.0;
                  }
               } else if (this.storedOrientation == 3) {
                  dz = var3.maxZ - var6.boundingBox.minZ;
                  if (dz < 0.0) {
                     dz = 0.0;
                  }
               } else if (this.storedOrientation == 4) {
                  dx = var3.minX - var6.boundingBox.maxX;
                  if (dx > 0.0) {
                     dx = 0.0;
                  }
               } else if (this.storedOrientation == 5) {
                  dx = var3.maxX - var6.boundingBox.minX;
                  if (dx < 0.0) {
                     dx = 0.0;
                  }
               }
            }

            this.pushedObjects.clear();
         }
      }

   }

   public void clearPistonTileEntity() {
      if (this.lastProgress < 1.0F && this.worldObj != null) {
         this.lastProgress = this.progress = 1.0F;
         this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
         this.invalidate();
         if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
            this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, this.storedMetadata, 3);
            this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID);
         }
      }

   }

   public void updateEntity() {
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.updatePushedObjects(1.0F, this.extending ? 1.0F : 0.25F);
         this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
         this.invalidate();
         if (this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID) {
            this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID, this.storedMetadata, 3);
            this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.storedBlockID);
         }
      } else {
         this.progress += 0.5F;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

         if (this.extending) {
            this.updatePushedObjects(this.progress, 0.25F + this.progress * 0.75F);
         }
      }

   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      this.storedBlockID = par1NBTTagCompound.getInteger("blockId");
      this.storedMetadata = par1NBTTagCompound.getInteger("blockData");
      this.storedOrientation = par1NBTTagCompound.getInteger("facing");
      this.lastProgress = this.progress = par1NBTTagCompound.getFloat("progress");
      this.extending = par1NBTTagCompound.getBoolean("extending");
   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("blockId", this.storedBlockID);
      par1NBTTagCompound.setInteger("blockData", this.storedMetadata);
      par1NBTTagCompound.setInteger("facing", this.storedOrientation);
      par1NBTTagCompound.setFloat("progress", this.lastProgress);
      par1NBTTagCompound.setBoolean("extending", this.extending);
   }
}
