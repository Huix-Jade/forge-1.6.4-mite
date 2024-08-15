package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHatchet;

public class EnchantmentTreeFelling extends Enchantment {
   protected EnchantmentTreeFelling(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "tree_felling";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemAxe && !(item instanceof ItemHatchet);
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools;
   }
}
