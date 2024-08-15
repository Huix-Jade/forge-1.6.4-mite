package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCuirass;

public class EnchantmentEndurance extends Enchantment {
   protected EnchantmentEndurance(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 4;
   }

   public String getNameSuffix() {
      return "endurance";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemCuirass;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
