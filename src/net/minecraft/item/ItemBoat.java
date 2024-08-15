package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   public ItemBoat(int par1) {
      super(par1, Material.wood, "boat");
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   public boolean onItemRightClick(EntityPlayer par3EntityPlayer, float partial_tick, boolean ctrl_is_down) {
      World par2World = par3EntityPlayer.worldObj;
      float var4 = 1.0F;
      float var5 = par3EntityPlayer.prevRotationPitch + (par3EntityPlayer.rotationPitch - par3EntityPlayer.prevRotationPitch) * var4;
      float var6 = par3EntityPlayer.prevRotationYaw + (par3EntityPlayer.rotationYaw - par3EntityPlayer.prevRotationYaw) * var4;
      double var7 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * (double)var4;
      double var9 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * (double)var4 + 1.62 - (double)par3EntityPlayer.yOffset;
      double var11 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * (double)var4;
      Vec3 var13 = par2World.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
      float var14 = MathHelper.cos(-var6 * 0.017453292F - 3.1415927F);
      float var15 = MathHelper.sin(-var6 * 0.017453292F - 3.1415927F);
      float var16 = -MathHelper.cos(-var5 * 0.017453292F);
      float var17 = MathHelper.sin(-var5 * 0.017453292F);
      float var18 = var15 * var16;
      float var20 = var14 * var16;
      double var21 = 5.0;
      Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
      RaycastCollision var24 = par2World.getBlockCollisionForSelection(var13, var23, true);
      if (var24 == null) {
         return false;
      } else {
         Vec3 var25 = par3EntityPlayer.getLook(var4);
         boolean var26 = false;
         float var27 = 1.0F;
         List var28 = par2World.getEntitiesWithinAABBExcludingEntity(par3EntityPlayer, par3EntityPlayer.boundingBox.addCoord(var25.xCoord * var21, var25.yCoord * var21, var25.zCoord * var21).expand((double)var27, (double)var27, (double)var27));

         int var29;
         for(var29 = 0; var29 < var28.size(); ++var29) {
            Entity var30 = (Entity)var28.get(var29);
            if (var30.canBeCollidedWith()) {
               float var31 = var30.getCollisionBorderSize(par3EntityPlayer);
               AxisAlignedBB var32 = var30.boundingBox.expand((double)var31, (double)var31, (double)var31);
               if (var32.isVecInside(var13)) {
                  var26 = true;
               }
            }
         }

         if (var26) {
            return false;
         } else if (var24.isBlock()) {
            var29 = var24.block_hit_x;
            int var33 = var24.block_hit_y;
            int var34 = var24.block_hit_z;
            if (par2World.getBlockId(var29, var33, var34) == Block.snow.blockID) {
               --var33;
            }

            EntityBoat var35 = new EntityBoat(par2World, (double)((float)var29 + 0.5F), (double)((float)var33 + 1.0F), (double)((float)var34 + 0.5F));
            var35.rotationYaw = (float)(((MathHelper.floor_double((double)(par3EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5) & 3) - 1) * 90);
            if (!par2World.getCollidingBoundingBoxes(var35, var35.boundingBox.expand(-0.1, -0.1, -0.1)).isEmpty()) {
               return false;
            } else {
               if (par3EntityPlayer.onServer()) {
                  par2World.spawnEntityInWorld(var35);
                  if (!par3EntityPlayer.inCreativeMode()) {
                     par3EntityPlayer.convertOneOfHeldItem((ItemStack)null);
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public int getBurnTime(ItemStack item_stack) {
      return 1200;
   }
}
