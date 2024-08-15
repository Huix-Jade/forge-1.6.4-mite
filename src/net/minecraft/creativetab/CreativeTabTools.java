package net.minecraft.creativetab;

import net.minecraft.item.Item;

final class CreativeTabTools extends CreativeTabs {
   CreativeTabTools(int par1, String par2Str) {
      super(par1, par2Str);
   }

   public int getTabIconItemIndex() {
      return Item.swordIron.itemID;
   }
}
