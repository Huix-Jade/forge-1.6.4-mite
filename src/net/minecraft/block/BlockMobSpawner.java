package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer {
   protected BlockMobSpawner(int par1) {
      super(par1, Material.iron, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setMinHarvestLevel(2);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityMobSpawner();
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      this.dropXpOnBlockBreak(info.world, info.x, info.y, info.z, 15 + info.world.rand.nextInt(15) + info.world.rand.nextInt(15));
      return 0;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return 0;
   }

   public static void incrementSpawnsKilled(World world, int x, int y, int z) {
      if (world.getBlock(x, y, z) instanceof BlockMobSpawner) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (metadata < 15) {
            ++metadata;
            world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
         }
      }

   }

   public boolean canBeCarried() {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return false;
   }
}
