package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ReferenceFileWriter;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block {
   protected BlockSnowBlock(int par1) {
      super(par1, Material.craftedSnow, new BlockConstants());
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setHardness(BlockHardness.blockSnow);
      this.setCushioning(0.8F);
   }

   public boolean melt(World world, int x, int y, int z) {
      return world.setBlock(x, y, z, Block.snow.blockID, 6, 3);
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else if ((!par1World.isFreezing(par2, par4) || par1World.getSavedLightValue(EnumSkyBlock.Block, par2, par3 + 1, par4) > 11) && BlockSnow.canMelt(par1World, par2, par3, par4)) {
         this.melt(par1World, par2, par3, par4);
         return true;
      } else {
         return false;
      }
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.snowball.itemID, 0, 8, 1.0F);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return ReferenceFileWriter.running ? "block" : null;
   }

   public String getUnlocalizedName() {
      return super.getUnlocalizedName() + (ReferenceFileWriter.running ? "" : ".block");
   }
}
