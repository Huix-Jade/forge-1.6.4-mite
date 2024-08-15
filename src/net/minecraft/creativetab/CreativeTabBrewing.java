package net.minecraft.creativetab;

import net.minecraft.item.Item;

final class CreativeTabBrewing extends CreativeTabs {
   CreativeTabBrewing(int var1, String var2) {
      super(var1, var2);
   }

   public int getTabIconItemIndex() {
      return Item.potion.itemID;
   }
}
