package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBattleAxe;
import net.minecraft.item.ItemPickaxe;

public class EnchantmentPiercing extends Enchantment {
   public EnchantmentPiercing(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getTranslatedName(Item item) {
      return item instanceof ItemAxe ? "Cleaving" : super.getTranslatedName(item);
   }

   public String getNameSuffix() {
      return "piercing";
   }

   public boolean canEnchantItem(Item item) {
      return item.getClass() == ItemPickaxe.class || item.getClass() == ItemBattleAxe.class;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
