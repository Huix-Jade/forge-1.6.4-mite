package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

public class BlockPoweredOre extends BlockOreStorage {
   public BlockPoweredOre(int par1) {
      super(par1, Material.redstone);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setMaxStackSize(4);
   }

   public boolean canProvidePower() {
      return true;
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return 15;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.redstone.itemID, 0, 9, 0.5F) : super.dropBlockAsEntityItem(info);
   }

   public boolean canDropExperienceOrbs() {
      return false;
   }
}
