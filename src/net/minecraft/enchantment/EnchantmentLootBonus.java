package net.minecraft.enchantment;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCudgel;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShovel;
import net.minecraft.item.ItemSword;

public class EnchantmentLootBonus extends Enchantment {
   protected EnchantmentLootBonus(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 3;
   }

   public String getNameSuffix() {
      if (this == looting) {
         return "lootBonus";
      } else if (this == fortune) {
         return "lootBonusDigger";
      } else {
         Minecraft.setErrorMessage("getName: no handler for " + this);
         return null;
      }
   }

   public boolean canApplyTogether(Enchantment par1Enchantment) {
      return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != silkTouch.effectId;
   }

   public boolean canEnchantItem(Item item) {
      if (this == looting) {
         return item instanceof ItemCudgel || item instanceof ItemSword;
      } else if (this != fortune) {
         return false;
      } else {
         return item.getClass() == ItemPickaxe.class || item.getClass() == ItemShovel.class;
      }
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      if (this == looting) {
         return creative_tab == CreativeTabs.tabCombat;
      } else {
         return creative_tab == CreativeTabs.tabTools;
      }
   }
}
