package net.minecraft.creativetab;

import net.minecraft.block.Block;

final class CreativeTabInventory extends CreativeTabs {
   CreativeTabInventory(int var1, String var2) {
      super(var1, var2);
   }

   public int getTabIconItemIndex() {
      return Block.chest.blockID;
   }
}
