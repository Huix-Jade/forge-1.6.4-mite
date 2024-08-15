package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.Icon;

public class ItemColored extends ItemBlock {
   private String[] blockNames;

   public ItemColored(Block block) {
      super(block);
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      return this.getBlock().getRenderColor(par1ItemStack.getItemSubtype());
   }

   public Icon getIconFromSubtype(int par1) {
      return this.getBlock().getIcon(0, par1);
   }

   public int getMetadata(int par1) {
      return par1;
   }

   public ItemColored setBlockNames(String[] par1ArrayOfStr) {
      this.blockNames = par1ArrayOfStr;
      return this;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return super.getUnlocalizedName();
      } else if (this.blockNames == null) {
         return super.getUnlocalizedName(par1ItemStack);
      } else {
         int var2 = par1ItemStack.getItemSubtype();
         return var2 >= 0 && var2 < this.blockNames.length ? super.getUnlocalizedName(par1ItemStack) + "." + this.blockNames[var2] : super.getUnlocalizedName(par1ItemStack);
      }
   }
}
