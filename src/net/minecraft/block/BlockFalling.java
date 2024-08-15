package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean fallInstantly;

   public BlockFalling(int par1, Material material, BlockConstants constants) {
      super(par1, material, constants);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
      return Minecraft.allow_new_sand_physics && super.onNeighborBlockChange(world, x, y, z, neighbor_block_id);
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      return this.tryToFall(par1World, par2, par3, par4);
   }

   public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (!par1World.isRemote) {
         this.tryToFall(par1World, par2, par3, par4);
      }

   }

   public final boolean tryToFall(World par1World, int par2, int par3, int par4) {
      int metadata = par1World.getBlockMetadata(par2, par3, par4);
      if (this.canFallDownTo(par1World, par2, par3 - 1, par4, metadata) && par3 >= 0) {
         byte var8 = 32;
         if (fallInstantly || !par1World.checkChunksExist(par2 - var8, par3 - var8, par4 - var8, par2 + var8, par3 + var8, par4 + var8)) {
            par1World.setBlockToAir(par2, par3, par4);

            while(this.canFallDownTo(par1World, par2, par3 - 1, par4, metadata) && par3 > 0) {
               --par3;
            }

            if (par3 > 0) {
               par1World.setBlock(par2, par3, par4, this.blockID, metadata, 2);
            }

            return true;
         }

         if (!par1World.isRemote) {
            EntityFallingSand var9 = new EntityFallingSand(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), this.blockID, par1World.getBlockMetadata(par2, par3, par4));
            this.onStartFalling(par1World, par2, par3, par4, var9);
            par1World.spawnEntityInWorld(var9);
            Block block = Block.blocksList[par1World.getBlockId(par2, par3 + 1, par4)];
            if (block instanceof BlockUnderminable) {
               BlockUnderminable block_underminable = (BlockUnderminable)block;
               block_underminable.scheduleUndermine(par1World, par2, par3 + 1, par4);
            }

            return true;
         }
      }

      return false;
   }

   protected void onStartFalling(World world, int x, int y, int z, EntityFallingSand entity_falling_sand) {
   }

   public int tickRate(World par1World) {
      return 2;
   }

   public boolean canFallDownTo(World world, int x, int y, int z, int metadata) {
      Block block_below = world.getBlock(x, y, z);
      int block_below_metadata = world.getBlockMetadata(x, y, z);
      return block_below == null || !block_below.isSolid(block_below_metadata) || EntityFallingSand.canDislodgeOrCrushBlockAt(world, this, metadata, x, y, z);
   }

   public void onFinishFalling(World par1World, int par2, int par3, int par4, int par5, EntityFallingSand entity_falling_sand) {
      if (this.usesNewSandPhysics()) {
         this.checkIfNotLegal(par1World, par2, par3, par4);
      }

   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      if (this.usesNewSandPhysics() && (!this.canFallDownTo(world, x, y - 1, z, metadata) || y < 0)) {
         for(int dx = -1; dx <= 1; ++dx) {
            for(int dz = -1; dz <= 1; ++dz) {
               if ((dx == 0 || dz == 0) && (dx == 0 && dz == 0 || !world.doesBlockBlockFluids(x + dx, y, z + dz)) && !world.isBlockTopFlatAndSolid(x + dx, y - 1, z + dz)) {
                  return false;
               }
            }
         }
      }

      return super.isLegalAt(world, x, y, z, metadata);
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      if (!this.usesNewSandPhysics()) {
         return super.onNotLegal(world, x, y, z, metadata);
      } else {
         if (world.isRemote) {
            Minecraft.setErrorMessage("onNotLegal: not meant to be called on client");
         }

         world.destroyBlock((new BlockBreakInfo(world, x, y, z)).setWasNotLegal(), true, true);
         return true;
      }
   }

   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      this.tryToFall(world, x, y, z);
   }
}
