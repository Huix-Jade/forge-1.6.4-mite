package net.minecraft.creativetab;

import net.minecraft.block.Block;

final class CreativeTabCombat extends CreativeTabs {
   CreativeTabCombat(int var1, String var2) {
      super(var1, var2);
   }

   public int getTabIconItemIndex() {
      return Block.brick.blockID;
   }
}
