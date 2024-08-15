package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCudgel;
import net.minecraft.item.ItemWarHammer;

public class EnchantmentKnockback extends Enchantment {
   protected EnchantmentKnockback(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 2;
   }

   public String getNameSuffix() {
      return "knockback";
   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemCudgel || item instanceof ItemWarHammer;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
