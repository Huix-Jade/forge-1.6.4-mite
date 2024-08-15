package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShovel;

public class EnchantmentDigging extends Enchantment {
   protected EnchantmentDigging(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "digging";
   }

   public boolean canEnchantItem(Item item) {
      return item.getClass() == ItemPickaxe.class || item instanceof ItemAxe || item instanceof ItemShovel || item instanceof ItemHoe;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools;
   }
}
