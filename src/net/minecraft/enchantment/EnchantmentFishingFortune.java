package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;

public class EnchantmentFishingFortune extends Enchantment {
   protected EnchantmentFishingFortune(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "fishing_fortune";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemFishingRod;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools;
   }
}
