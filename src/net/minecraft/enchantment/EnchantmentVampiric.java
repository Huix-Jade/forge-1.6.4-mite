package net.minecraft.enchantment;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemScythe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class EnchantmentVampiric extends Enchantment {
   protected EnchantmentVampiric(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getNameSuffix() {
      return "vampiric";
   }

   public boolean canEnchantItem(Item item) {
      if (item instanceof ItemTool) {
         Material material = ((ItemTool)item).getToolMaterial();
         if (material == Material.silver || material == Material.mithril) {
            return false;
         }
      }

      return item instanceof ItemSword || item instanceof ItemScythe;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
