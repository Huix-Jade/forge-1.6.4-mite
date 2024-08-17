package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneLogic extends BlockDirectional {
   protected final boolean isRepeaterPowered;

   protected BlockRedstoneLogic(int par1, boolean par2) {
      super(par1, Material.circuits, (new BlockConstants()).setNotAlwaysLegal());
      this.isRepeaterPowered = par2;
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below != null && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata);
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else {
         int var6 = par1World.getBlockMetadata(par2, par3, par4);
         if (!this.func_94476_e(par1World, par2, par3, par4, var6)) {
            boolean var7 = this.isGettingInput(par1World, par2, par3, par4, var6);
            if (this.isRepeaterPowered && !var7) {
               par1World.setBlock(par2, par3, par4, this.func_94484_i().blockID, var6, 2);
               return true;
            }

            if (!this.isRepeaterPowered) {
               par1World.setBlock(par2, par3, par4, this.func_94485_e().blockID, var6, 2);
               if (!var7) {
                  par1World.scheduleBlockUpdateWithPriority(par2, par3, par4, this.func_94485_e().blockID, this.func_94486_g(var6), -1);
               }

               return true;
            }
         }

         return false;
      }
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 0 ? (this.isRepeaterPowered ? Block.torchRedstoneActive.getBlockTextureFromSide(par1) : Block.torchRedstoneIdle.getBlockTextureFromSide(par1)) : (par1 == 1 ? this.blockIcon : Block.stoneDoubleSlab.getBlockTextureFromSide(1));
   }

   public static int j(int metadata) {
      return metadata & 3;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 != 0 && par5 != 1;
   }

   public int getRenderType() {
      return 36;
   }

   protected boolean func_96470_c(int par1) {
      return this.isRepeaterPowered;
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return this.isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5);
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      if (!this.func_96470_c(var6)) {
         return 0;
      } else {
         int var7 = j(var6);
         return var7 == 0 && par5 == 3 ? this.func_94480_d(par1IBlockAccess, par2, par3, par4, var6) : (var7 == 1 && par5 == 4 ? this.func_94480_d(par1IBlockAccess, par2, par3, par4, var6) : (var7 == 2 && par5 == 2 ? this.func_94480_d(par1IBlockAccess, par2, par3, par4, var6) : (var7 == 3 && par5 == 5 ? this.func_94480_d(par1IBlockAccess, par2, par3, par4, var6) : 0)));
      }
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
         world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         this.func_94479_f(world, x, y, z, neighbor_block_id);
         return world.getBlock(x, y, z) != this || world.getBlockMetadata(x, y, z) != metadata;
      }
   }

   protected void func_94479_f(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = par1World.getBlockMetadata(par2, par3, par4);
      if (!this.func_94476_e(par1World, par2, par3, par4, var6)) {
         boolean var7 = this.isGettingInput(par1World, par2, par3, par4, var6);
         if ((this.isRepeaterPowered && !var7 || !this.isRepeaterPowered && var7) && !par1World.isBlockTickScheduledThisTick(par2, par3, par4, this.blockID)) {
            byte var8 = -1;
            if (this.func_83011_d(par1World, par2, par3, par4, var6)) {
               var8 = -3;
            } else if (this.isRepeaterPowered) {
               var8 = -2;
            }

            par1World.scheduleBlockUpdateWithPriority(par2, par3, par4, this.blockID, this.func_94481_j_(var6), var8);
         }
      }

   }

   public boolean func_94476_e(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return false;
   }

   protected boolean isGettingInput(World par1World, int par2, int par3, int par4, int par5) {
      return this.getInputStrength(par1World, par2, par3, par4, par5) > 0;
   }

   protected int getInputStrength(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = j(par5);
      int var7 = par2 + Direction.offsetX[var6];
      int var8 = par4 + Direction.offsetZ[var6];
      int var9 = par1World.getIndirectPowerLevelTo(var7, par3, var8, Direction.directionToFacing[var6]);
      return var9 >= 15 ? var9 : Math.max(var9, par1World.getBlockId(var7, par3, var8) == Block.redstoneWire.blockID ? par1World.getBlockMetadata(var7, par3, var8) : 0);
   }

   protected int func_94482_f(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = j(par5);
      switch (var6) {
         case 0:
         case 2:
            return Math.max(this.func_94488_g(par1IBlockAccess, par2 - 1, par3, par4, 4), this.func_94488_g(par1IBlockAccess, par2 + 1, par3, par4, 5));
         case 1:
         case 3:
            return Math.max(this.func_94488_g(par1IBlockAccess, par2, par3, par4 + 1, 3), this.func_94488_g(par1IBlockAccess, par2, par3, par4 - 1, 2));
         default:
            return 0;
      }
   }

   protected int func_94488_g(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
      return this.func_94477_d(var6) ? (var6 == Block.redstoneWire.blockID ? par1IBlockAccess.getBlockMetadata(par2, par3, par4) : par1IBlockAccess.isBlockProvidingPowerTo(par2, par3, par4, par5)) : 0;
   }

   public boolean canProvidePower() {
      return true;
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer != null) {
         int placer_direction = ((MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5) & 3) + 2) % 4;
         world.setBlockMetadataWithNotify(x, y, z, placer_direction, 3);
         if (this.isGettingInput(world, x, y, z, placer_direction)) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 1);
         }
      }

      return super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      this.func_94483_i_(par1World, par2, par3, par4);
   }

   protected void func_94483_i_(World par1World, int par2, int par3, int par4) {
      int var5 = j(par1World.getBlockMetadata(par2, par3, par4));
      if (var5 == 1) {
         par1World.notifyBlockOfNeighborChange(par2 + 1, par3, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID, 4);
      }

      if (var5 == 3) {
         par1World.notifyBlockOfNeighborChange(par2 - 1, par3, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID, 5);
      }

      if (var5 == 2) {
         par1World.notifyBlockOfNeighborChange(par2, par3, par4 + 1, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID, 2);
      }

      if (var5 == 0) {
         par1World.notifyBlockOfNeighborChange(par2, par3, par4 - 1, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID, 3);
      }

   }

   protected boolean func_94477_d(int par1) {
      Block var2 = Block.blocksList[par1];
      return var2 != null && var2.canProvidePower();
   }

   protected int func_94480_d(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return 15;
   }

   public static boolean isRedstoneRepeaterBlockID(int par0) {
      return Block.redstoneRepeaterIdle.func_94487_f(par0) || Block.redstoneComparatorIdle.func_94487_f(par0);
   }

   public boolean func_94487_f(int par1) {
      return par1 == this.func_94485_e().blockID || par1 == this.func_94484_i().blockID;
   }

   public boolean func_83011_d(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = j(par5);
      if (isRedstoneRepeaterBlockID(par1World.getBlockId(par2 - Direction.offsetX[var6], par3, par4 - Direction.offsetZ[var6]))) {
         int var7 = par1World.getBlockMetadata(par2 - Direction.offsetX[var6], par3, par4 - Direction.offsetZ[var6]);
         int var8 = j(var7);
         return var8 != var6;
      } else {
         return false;
      }
   }

   protected int func_94486_g(int par1) {
      return this.func_94481_j_(par1);
   }

   protected abstract int func_94481_j_(int var1);

   protected abstract BlockRedstoneLogic func_94485_e();

   protected abstract BlockRedstoneLogic func_94484_i();

   public boolean isAssociatedBlockID(int par1) {
      return this.func_94487_f(par1);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (this.isRepeaterPowered && info.getResponsiblePlayer() != null) {
         World world = info.world;
         int x = info.x;
         int y = info.y;
         int z = info.z;
         world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
         world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
      }

      return super.dropBlockAsEntityItem(info);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
