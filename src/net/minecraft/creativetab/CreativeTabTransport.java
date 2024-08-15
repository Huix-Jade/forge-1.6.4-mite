package net.minecraft.creativetab;

import net.minecraft.item.Item;

final class CreativeTabTransport extends CreativeTabs {
   CreativeTabTransport(int par1, String par2Str) {
      super(par1, par2Str);
   }

   public int getTabIconItemIndex() {
      return Item.bucketIronLava.itemID;
   }
}
