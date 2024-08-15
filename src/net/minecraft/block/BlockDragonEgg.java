package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.SignalData;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {
   public BlockDragonEgg(int par1) {
      super(par1, Material.dragonEgg, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setBlockBoundsForAllThreads(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
      return false;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      return this.fallIfPossible(world, x, y, z);
   }

   private boolean fallIfPossible(World par1World, int par2, int par3, int par4) {
      if (Block.sand.canFallDownTo(par1World, par2, par3 - 1, par4, 0) && par3 >= 0) {
         byte var5 = 32;
         if (BlockFalling.fallInstantly || !par1World.checkChunksExist(par2 - var5, par3 - var5, par4 - var5, par2 + var5, par3 + var5, par4 + var5)) {
            par1World.setBlockToAir(par2, par3, par4);

            while(Block.sand.canFallDownTo(par1World, par2, par3 - 1, par4, 0) && par3 > 0) {
               --par3;
            }

            if (par3 > 0) {
               par1World.setBlock(par2, par3, par4, this.blockID, 0, 2);
            }

            return true;
         }

         EntityFallingSand var6 = new EntityFallingSand(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), this.blockID);
         par1World.spawnEntityInWorld(var6);
      }

      return false;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         this.teleportNearby(world, x, y, z);
      }

      return true;
   }

   public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
      if (player.onServer()) {
         this.teleportNearby(world, x, y, z);
      }

   }

   private void teleportNearby(World world, int x, int y, int z) {
      if (world.isRemote) {
         Minecraft.setErrorMessage("teleportNearby: not meant to be called on client");
      }

      if (world.getBlockId(x, y, z) == this.blockID) {
         for(int attempts = 0; attempts < 1000; ++attempts) {
            int try_x = x + world.rand.nextInt(16) - world.rand.nextInt(16);
            int try_y = y + world.rand.nextInt(8) - world.rand.nextInt(8);
            int try_z = z + world.rand.nextInt(16) - world.rand.nextInt(16);
            if (world.isAirBlock(try_x, try_y, try_z)) {
               world.setBlock(try_x, try_y, try_z, this.blockID, world.getBlockMetadata(x, y, z), 2);
               world.setBlockToAir(x, y, z);
               world.blockFX(EnumBlockFX.particle_trail, x, y, z, (new SignalData()).setByte(EnumParticle.portal_underworld.ordinal()).setShort(128).setApproxPosition((double)try_x, (double)try_y, (double)try_z));
               return;
            }
         }

      }
   }

   public int tickRate(World par1World) {
      return 5;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return true;
   }

   public int getRenderType() {
      return 27;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return 0;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return super.dropBlockAsItself(info);
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
