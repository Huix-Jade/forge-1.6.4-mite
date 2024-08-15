package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.util.Icon;

public class ItemCloth extends ItemBlock {
   public ItemCloth(Block block) {
      super(block);
   }

   public Icon getIconFromSubtype(int par1) {
      return Block.cloth.getIcon(2, BlockColored.getBlockFromDye(par1));
   }

   public int getMetadata(int par1) {
      return par1;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return par1ItemStack == null ? super.getUnlocalizedName() : super.getUnlocalizedName() + "." + ItemDye.dyeColorNames[BlockColored.getBlockFromDye(par1ItemStack.getItemSubtype())];
   }
}
