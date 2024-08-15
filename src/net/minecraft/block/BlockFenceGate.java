package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPudding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
   public BlockFenceGate(int par1) {
      super(par1, Material.wood, (new BlockConstants()).setNeverHidesAdjacentFaces().setAlwaysConnectsWithFence());
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bit 4 set if gate is open";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 8;
   }

   public Icon getIcon(int par1, int par2) {
      return Block.planks.getBlockTextureFromSide(par1);
   }

   public static int j(int metadata) {
      return metadata & 3;
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      if (entity instanceof EntityPudding) {
         AxisAlignedBB bounds = (AxisAlignedBB)this.getCollisionBounds(world, x, y, z, (Entity)null);
         return bounds == null ? null : bounds.setMaxY((double)y + 1.5);
      } else {
         return this.isSolid(world, x, y, z) ? (this.useFullBlockForCollisions(entity) ? AxisAlignedBB.getBoundingBoxFromPool(x, y, z, 0.0, 0.0, 0.0, 1.0, 1.5, 1.0) : super.getCollisionBounds(world, x, y, z, entity)) : null;
      }
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = j(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
      if (var5 != 2 && var5 != 0) {
         this.setBlockBoundsForCurrentThread(0.375, 0.32499998807907104, 0.0, 0.625, 1.0, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.32499998807907104, 0.375, 1.0, 1.0, 0.625);
      }

   }

   public int getRenderType() {
      return 21;
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard4(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata &= -4;
      metadata |= direction.isSouth() ? 0 : (direction.isWest() ? 1 : (direction.isNorth() ? 2 : (direction.isEast() ? 3 : -1)));
      return metadata;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (isFenceGateOpen(metadata)) {
            metadata &= -5;
         } else {
            float yaw;
            for(yaw = player.rotationYaw; yaw < 0.0F; yaw += 360.0F) {
            }

            while(yaw >= 360.0F) {
               yaw -= 360.0F;
            }

            if (metadata != 0 && metadata != 2) {
               metadata = yaw >= 0.0F && yaw < 180.0F ? 5 : 7;
            } else {
               metadata = (!(yaw >= 0.0F) || !(yaw < 90.0F)) && !(yaw >= 270.0F) ? 6 : 4;
            }

            metadata |= 4;
         }

         world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
         this.makeOpenOrCloseSound(world, x, y, z, metadata);
      }

      return true;
   }

   private void makeOpenOrCloseSound(World world, int x, int y, int z, int metadata_after) {
      if (isFenceGateOpen(metadata_after)) {
         world.playSoundAtBlock(x, y, z, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
      } else {
         world.playSoundAtBlock(x, y, z, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
      }

   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      int metadata = world.getBlockMetadata(x, y, z);
      boolean is_indirectly_powered = world.isBlockIndirectlyGettingPowered(x, y, z);
      if (is_indirectly_powered || neighbor_block_id > 0 && Block.blocksList[neighbor_block_id].canProvidePower()) {
         if (is_indirectly_powered && !isFenceGateOpen(metadata)) {
            world.setBlockMetadataWithNotify(x, y, z, metadata | 4, 2);
            this.makeOpenOrCloseSound(world, x, y, z, metadata | 4);
            return true;
         }

         if (!is_indirectly_powered && isFenceGateOpen(metadata)) {
            world.setBlockMetadataWithNotify(x, y, z, metadata & -5, 2);
            this.makeOpenOrCloseSound(world, x, y, z, metadata & -5);
            return true;
         }
      }

      return false;
   }

   public static boolean isFenceGateOpen(int par0) {
      return (par0 & 4) != 0;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return true;
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public boolean isPortal() {
      return true;
   }

   public boolean isOpenPortal(World world, int x, int y, int z) {
      return isFenceGateOpen(world.getBlockMetadata(x, y, z));
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return !isFenceGateOpen(metadata);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
