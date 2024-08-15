package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;

public class EnchantmentFireAspect extends Enchantment {
   protected EnchantmentFireAspect(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 2;
   }

   public String getNameSuffix() {
      return "fire";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemSword;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
