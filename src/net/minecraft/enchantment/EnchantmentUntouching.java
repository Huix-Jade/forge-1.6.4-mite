package net.minecraft.enchantment;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDagger;
import net.minecraft.item.ItemKnife;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemShovel;

public class EnchantmentUntouching extends Enchantment {
   protected EnchantmentUntouching(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 1;
   }

   public String getNameSuffix() {
      return "untouching";
   }

   public boolean canApplyTogether(Enchantment par1Enchantment) {
      return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != fortune.effectId;
   }

   public boolean canEnchantItem(Item item) {
      return item.getClass() == ItemPickaxe.class || item.getClass() == ItemShovel.class || item instanceof ItemShears || item.getClass() == ItemKnife.class || item.getClass() == ItemDagger.class;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabTools;
   }
}
