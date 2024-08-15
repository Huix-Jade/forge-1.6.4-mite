package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   private String pressurePlateIconName;

   protected BlockBasePressurePlate(int par1, String par2Str, Material par3Material) {
      super(par1, par3Material, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.pressurePlateIconName = par2Str;
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
      this.setMaxStackSize(8);
      this.func_94353_c_(this.getMetaFromWeight(15), true);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.func_94353_c_(par1IBlockAccess.getBlockMetadata(par2, par3, par4), false);
   }

   protected void func_94353_c_(int par1, boolean for_all_threads) {
      boolean var2 = this.getPowerSupply(par1) > 0;
      float var3 = 0.0625F;
      if (var2) {
         this.setBlockBounds((double)var3, 0.0, (double)var3, (double)(1.0F - var3), 0.03125, (double)(1.0F - var3), for_all_threads);
      } else {
         this.setBlockBounds((double)var3, 0.0, (double)var3, (double)(1.0F - var3), 0.0625, (double)(1.0F - var3), for_all_threads);
      }

   }

   public int tickRate(World par1World) {
      return 20;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (block_below == null) {
         return false;
      } else {
         return block_below.isTopFlatAndSolid(block_below_metadata) || BlockFence.isIdAFence(block_below.blockID);
      }
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else {
         int power_level = this.getPowerSupply(world.getBlockMetadata(x, y, z));
         return power_level > 0 ? this.setStateIfMobInteractsWithPlate(world, x, y, z, power_level) : false;
      }
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (!par1World.isRemote) {
         int var6 = this.getPowerSupply(par1World.getBlockMetadata(par2, par3, par4));
         if (var6 == 0) {
            this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4, var6);
         }
      }

   }

   protected boolean setStateIfMobInteractsWithPlate(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = this.getPlateState(par1World, par2, par3, par4);
      boolean var7 = par5 > 0;
      boolean var8 = var6 > 0;
      boolean changed_state = false;
      if (par5 != var6) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, this.getMetaFromWeight(var6), 2);
         this.func_94354_b_(par1World, par2, par3, par4);
         par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
         changed_state = true;
      }

      if (!var8 && var7) {
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.1, (double)par4 + 0.5, "random.click", 0.3F, 0.5F);
      } else if (var8 && !var7) {
         par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.1, (double)par4 + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (var8) {
         par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
      }

      return changed_state;
   }

   protected AxisAlignedBB getSensitiveAABB(int par1, int par2, int par3) {
      float var4 = 0.125F;
      return AxisAlignedBB.getAABBPool().getAABB((double)((float)par1 + var4), (double)par2, (double)((float)par3 + var4), (double)((float)(par1 + 1) - var4), (double)par2 + 0.25, (double)((float)(par3 + 1) - var4));
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if (this.getPowerSupply(par6) > 0) {
         this.func_94354_b_(par1World, par2, par3, par4);
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   protected void func_94354_b_(World par1World, int par2, int par3, int par4) {
      par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
      par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return this.getPowerSupply(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 == 1 ? this.getPowerSupply(par1IBlockAccess.getBlockMetadata(par2, par3, par4)) : 0;
   }

   public boolean canProvidePower() {
      return true;
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      float var1 = 0.5F;
      float var2 = 0.125F;
      float var3 = 0.5F;
      this.setBlockBoundsForCurrentThread((double)(0.5F - var1), (double)(0.5F - var2), (double)(0.5F - var3), (double)(0.5F + var1), (double)(0.5F + var2), (double)(0.5F + var3));
   }

   public int getMobilityFlag() {
      return 1;
   }

   protected abstract int getPlateState(World var1, int var2, int var3, int var4);

   protected abstract int getPowerSupply(int var1);

   protected abstract int getMetaFromWeight(int var1);

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.pressurePlateIconName);
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
