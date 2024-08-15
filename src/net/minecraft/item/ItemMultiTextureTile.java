package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.Debug;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;

public class ItemMultiTextureTile extends ItemBlock {
   private final String[] field_82804_b;

   public ItemMultiTextureTile(Block block, String[] names) {
      super(block);
      this.field_82804_b = names;
      if (this.getBlock().getNumSubBlocks() != StringHelper.getNumNonNullStrings(names)) {
         Debug.setErrorMessage("ItemMultiTextureTile: getNumSubBlocks()=" + this.getBlock().getNumSubBlocks() + ", but length of array is " + names.length);
      }

   }

   public Icon getIconFromSubtype(int par1) {
      return this.getBlock().getIcon(2, par1);
   }

   public int getMetadata(int par1) {
      return par1;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return super.getUnlocalizedName();
      } else {
         int var2 = par1ItemStack.getItemSubtype();
         if (var2 < 0 || var2 >= this.field_82804_b.length) {
            var2 = 0;
         }

         return super.getUnlocalizedName() + "." + this.field_82804_b[var2];
      }
   }
}
