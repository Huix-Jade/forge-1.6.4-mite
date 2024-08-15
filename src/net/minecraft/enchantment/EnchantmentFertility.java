package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemMattock;
import net.minecraft.item.ItemScythe;

public class EnchantmentFertility extends Enchantment {
   protected EnchantmentFertility(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "fertility";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemHoe || item instanceof ItemMattock || item instanceof ItemScythe;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools;
   }
}
