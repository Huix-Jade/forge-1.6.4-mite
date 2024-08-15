package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public final class BlockNetherStalk extends BlockPlant {
   private Icon[] iconArray;

   protected BlockNetherStalk(int par1) {
      super(par1);
      this.setTickRandomly(true);
      float var2 = 0.5F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), 0.25, (double)(0.5F + var2));
      this.setCreativeTab((CreativeTabs)null);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used to track growth";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below == slowSand;
   }

   public int getMinAllowedLightValue() {
      return 0;
   }

   public int getMaxAllowedLightValue() {
      return 15;
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      return world.isOutdoors(x, y, z) ? false : super.canBePlacedAt(world, x, y, z, metadata);
   }

   public int tickRate(World par1World) {
      return 200;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else if (!world.provider.isHellWorld) {
         this.dropBlockAsEntityItem((new BlockBreakInfo(world, x, y, z)).setDroppedSelf());
         world.setBlockToAir(x, y, z);
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         if (metadata < 3 && random.nextInt(40) == 0) {
            ++metadata;
            return world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
         } else {
            return false;
         }
      }
   }

   public Icon getIcon(int par1, int par2) {
      return par2 >= 3 ? this.iconArray[2] : (par2 > 0 ? this.iconArray[1] : this.iconArray[0]);
   }

   public int getRenderType() {
      return 6;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      int quantity = 1;
      if (info.getMetadata() >= 3) {
         quantity = 2 + info.world.rand.nextInt(3);
         int fortune = info.getHarvesterFortune();
         if (fortune > 0) {
            quantity += info.world.rand.nextInt(fortune + 1);
         }
      }

      return this.dropBlockAsEntityItem(info, Item.netherStalkSeeds.itemID, 0, quantity, 1.0F);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.netherStalkSeeds.itemID;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[3];

      for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
         this.iconArray[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_stage_" + var2);
      }

   }
}
