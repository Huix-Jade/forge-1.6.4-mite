package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandomItem;

public class EnchantmentData extends WeightedRandomItem {
   public final Enchantment enchantmentobj;
   public int enchantmentLevel;

   public EnchantmentData(Enchantment par1Enchantment, int par2) {
      super(par1Enchantment.getWeight());
      this.enchantmentobj = par1Enchantment;
      this.enchantmentLevel = par2;
   }

   public EnchantmentData(int par1, int par2) {
      this(Enchantment.enchantmentsList[par1], par2);
   }
}
