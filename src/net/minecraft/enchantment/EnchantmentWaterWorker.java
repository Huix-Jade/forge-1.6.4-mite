package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHelmet;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 1;
   }

   public String getNameSuffix() {
      return "waterWorker";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemHelmet;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
