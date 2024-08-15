package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDagger;
import net.minecraft.item.ItemKnife;

public class EnchantmentButchering extends Enchantment {
   protected EnchantmentButchering(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 3;
   }

   public String getNameSuffix() {
      return "butchering";
   }

   public boolean canEnchantItem(Item item) {
      return item.getClass() == ItemKnife.class || item.getClass() == ItemDagger.class;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools;
   }
}
