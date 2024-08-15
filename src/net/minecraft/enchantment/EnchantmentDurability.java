package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemCudgel;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShovel;

public class EnchantmentDurability extends Enchantment {
   protected EnchantmentDurability(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "durability";
   }

   public boolean canEnchantItem(Item item) {
      if (item instanceof ItemArmor) {
         ItemArmor item_armor = (ItemArmor)item;
         return item_armor.isSolidMetal();
      } else {
         return item instanceof ItemCudgel || item instanceof ItemPickaxe || item instanceof ItemShovel || item instanceof ItemHoe || item instanceof ItemAxe || item instanceof ItemFishingRod || item instanceof ItemBow;
      }
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools || creative_tab == CreativeTabs.tabCombat;
   }
}
