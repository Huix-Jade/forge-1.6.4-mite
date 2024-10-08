package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
   private boolean torchActive;
   private static Map redstoneUpdateInfoCache = new HashMap();

   private boolean checkForBurnout(World par1World, int par2, int par3, int par4, boolean par5) {
      if (!redstoneUpdateInfoCache.containsKey(par1World)) {
         redstoneUpdateInfoCache.put(par1World, new ArrayList());
      }

      List var6 = (List)redstoneUpdateInfoCache.get(par1World);
      if (par5) {
         var6.add(new RedstoneUpdateInfo(par2, par3, par4, par1World.getTotalWorldTime()));
      }

      int var7 = 0;

      for(int var8 = 0; var8 < var6.size(); ++var8) {
         RedstoneUpdateInfo var9 = (RedstoneUpdateInfo)var6.get(var8);
         if (var9.x == par2 && var9.y == par3 && var9.z == par4) {
            ++var7;
            if (var7 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   protected BlockRedstoneTorch(int par1, boolean par2) {
      super(par1);
      this.torchActive = par2;
      this.setTickRandomly(true);
      this.setCreativeTab((CreativeTabs)null);
   }

   public int tickRate(World par1World) {
      return 2;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (par1World.getBlockMetadata(par2, par3, par4) == 0) {
         super.onBlockAdded(par1World, par2, par3, par4);
      }

      if (this.torchActive) {
         par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
      }

   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if (this.torchActive) {
         par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
      }

   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (!this.torchActive) {
         return 0;
      } else {
         int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
         return var6 == 5 && par5 == 1 ? 0 : (var6 == 3 && par5 == 3 ? 0 : (var6 == 4 && par5 == 2 ? 0 : (var6 == 1 && par5 == 5 ? 0 : (var6 == 2 && par5 == 4 ? 0 : 15))));
      }
   }

   private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      return var5 == 5 && par1World.getIndirectPowerOutput(par2, par3 - 1, par4, 0) ? true : (var5 == 3 && par1World.getIndirectPowerOutput(par2, par3, par4 - 1, 2) ? true : (var5 == 4 && par1World.getIndirectPowerOutput(par2, par3, par4 + 1, 3) ? true : (var5 == 1 && par1World.getIndirectPowerOutput(par2 - 1, par3, par4, 4) ? true : var5 == 2 && par1World.getIndirectPowerOutput(par2 + 1, par3, par4, 5))));
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else {
         boolean var6 = this.isIndirectlyPowered(par1World, par2, par3, par4);
         List var7 = (List)redstoneUpdateInfoCache.get(par1World);

         while(var7 != null && !var7.isEmpty() && par1World.getTotalWorldTime() - ((RedstoneUpdateInfo)var7.get(0)).updateTime > 60L) {
            var7.remove(0);
         }

         if (this.torchActive) {
            if (var6) {
               par1World.setBlock(par2, par3, par4, Block.torchRedstoneIdle.blockID, par1World.getBlockMetadata(par2, par3, par4), 3);
               if (this.checkForBurnout(par1World, par2, par3, par4, true)) {
                  par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                  for(int var8 = 0; var8 < 5; ++var8) {
                     double var9 = (double)par2 + par5Random.nextDouble() * 0.6 + 0.2;
                     double var11 = (double)par3 + par5Random.nextDouble() * 0.6 + 0.2;
                     double var13 = (double)par4 + par5Random.nextDouble() * 0.6 + 0.2;
                     par1World.spawnParticle(EnumParticle.smoke, var9, var11, var13, 0.0, 0.0, 0.0);
                  }
               }

               return true;
            }
         } else if (!var6 && !this.checkForBurnout(par1World, par2, par3, par4, false)) {
            par1World.setBlock(par2, par3, par4, Block.torchRedstoneActive.blockID, par1World.getBlockMetadata(par2, par3, par4), 3);
            return true;
         }

         return false;
      }
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else {
         boolean indirectly_powered = this.isIndirectlyPowered(world, x, y, z);
         if (this.torchActive && indirectly_powered || !this.torchActive && !indirectly_powered) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
         }

         return false;
      }
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 == 0 ? this.isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5) : 0;
   }

   public boolean canBeCarried() {
      return this.torchActive;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? super.dropBlockAsEntityItem(info) : this.dropBlockAsEntityItem(info, Block.torchRedstoneActive);
   }

   public boolean canProvidePower() {
      return true;
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (this.torchActive) {
         int var6 = par1World.getBlockMetadata(par2, par3, par4);
         double var7 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2;
         double var9 = (double)((float)par3 + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2;
         double var11 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2;
         double var13 = 0.2199999988079071;
         double var15 = 0.27000001072883606;
         if (var6 == 1) {
            par1World.spawnParticle(EnumParticle.reddust, var7 - var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         } else if (var6 == 2) {
            par1World.spawnParticle(EnumParticle.reddust, var7 + var15, var9 + var13, var11, 0.0, 0.0, 0.0);
         } else if (var6 == 3) {
            par1World.spawnParticle(EnumParticle.reddust, var7, var9 + var13, var11 - var15, 0.0, 0.0, 0.0);
         } else if (var6 == 4) {
            par1World.spawnParticle(EnumParticle.reddust, var7, var9 + var13, var11 + var15, 0.0, 0.0, 0.0);
         } else {
            par1World.spawnParticle(EnumParticle.reddust, var7, var9, var11, 0.0, 0.0, 0.0);
         }
      }

   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Block.torchRedstoneActive.blockID;
   }

   public boolean isAssociatedBlockID(int par1) {
      return par1 == Block.torchRedstoneIdle.blockID || par1 == Block.torchRedstoneActive.blockID;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.torchActive ? "lit" : "unlit";
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.redstone});
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return !(other_block instanceof BlockRedstoneTorch) && super.canBeReplacedBy(metadata, other_block, other_block_metadata);
   }
}
