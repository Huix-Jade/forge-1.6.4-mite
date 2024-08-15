package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCudgel;
import net.minecraft.item.ItemWarHammer;

public class EnchantmentStun extends Enchantment {
   protected EnchantmentStun(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "stun";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemCudgel || item instanceof ItemWarHammer;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
