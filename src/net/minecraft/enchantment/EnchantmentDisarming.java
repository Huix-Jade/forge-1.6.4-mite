package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;

public class EnchantmentDisarming extends Enchantment {
   public EnchantmentDisarming(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "disarming";
   }

   public boolean canEnchantItem(Item item) {
      return item.getClass() == ItemSword.class;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
