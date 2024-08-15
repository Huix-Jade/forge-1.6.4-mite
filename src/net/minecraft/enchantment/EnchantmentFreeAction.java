package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemLeggings;

public class EnchantmentFreeAction extends Enchantment {
   protected EnchantmentFreeAction(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 4;
   }

   public String getNameSuffix() {
      return "free_action";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemLeggings;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
