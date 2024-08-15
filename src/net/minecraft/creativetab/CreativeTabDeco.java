package net.minecraft.creativetab;

import net.minecraft.item.Item;

final class CreativeTabDeco extends CreativeTabs {
   CreativeTabDeco(int var1, String var2) {
      super(var1, var2);
   }

   public int getTabIconItemIndex() {
      return Item.redstone.itemID;
   }
}
