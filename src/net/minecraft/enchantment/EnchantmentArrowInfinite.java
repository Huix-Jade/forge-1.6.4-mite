package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;

public class EnchantmentArrowInfinite extends Enchantment {
   public EnchantmentArrowInfinite(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 1;
   }

   public String getNameSuffix() {
      return "arrowInfinite";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemBow;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
