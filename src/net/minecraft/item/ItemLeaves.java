package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.util.Icon;
import net.minecraft.world.ColorizerFoliage;

public class ItemLeaves extends ItemBlock {
   public ItemLeaves(Block block) {
      super(block);
   }

   public int getMetadata(int par1) {
      return par1 | 4;
   }

   public Icon getIconFromSubtype(int par1) {
      return Block.leaves.getIcon(0, par1);
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      int var3 = par1ItemStack.getItemSubtype();
      return (var3 & 1) == 1 ? ColorizerFoliage.getFoliageColorPine() : ((var3 & 2) == 2 ? ColorizerFoliage.getFoliageColorBirch() : ColorizerFoliage.getFoliageColorBasic());
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return super.getUnlocalizedName();
      } else {
         int var2 = par1ItemStack.getItemSubtype();
         if (var2 < 0 || var2 >= BlockLeaves.LEAF_TYPES.length) {
            var2 = 0;
         }

         return super.getUnlocalizedName() + "." + BlockLeaves.LEAF_TYPES[var2];
      }
   }
}
