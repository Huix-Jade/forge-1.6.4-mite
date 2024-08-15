package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoots;

public class EnchantmentSpeed extends Enchantment {
   protected EnchantmentSpeed(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "speed";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemBoots;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
