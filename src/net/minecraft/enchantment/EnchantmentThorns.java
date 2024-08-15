package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCuirass;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;

public class EnchantmentThorns extends Enchantment {
   public EnchantmentThorns(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 3;
   }

   public String getNameSuffix() {
      return "thorns";
   }

   public static boolean func_92094_a(int par0, Random par1Random) {
      return par0 <= 0 ? false : par1Random.nextFloat() < 0.15F * (float)par0;
   }

   public static int func_92095_b(int par0, Random par1Random) {
      return par0 > 10 ? par0 - 10 : 1 + par1Random.nextInt(4);
   }

   public static void func_92096_a(Entity par0Entity, EntityLivingBase par1EntityLivingBase, Random par2Random) {
      if (par1EntityLivingBase.onClient() && (Minecraft.theMinecraft.thePlayer == null || !Minecraft.theMinecraft.thePlayer.isMITEmigo())) {
         Minecraft.setErrorMessage("Thorns.func_92096_a: called on client? (" + (par0Entity == null ? "null" : par0Entity.getEntityName()) + " vs " + (par1EntityLivingBase == null ? "null" : par1EntityLivingBase.getEntityName()) + ", " + Minecraft.temp_debug + ")");
      }

      int var3 = EnchantmentHelper.func_92098_i(par1EntityLivingBase);
      ItemStack var4 = EnchantmentHelper.func_92099_a(Enchantment.thorns, par1EntityLivingBase);
      if (func_92094_a(var3, par2Random)) {
         par0Entity.attackEntityFrom(new Damage(DamageSource.causeThornsDamage(par1EntityLivingBase), (float)func_92095_b(var3, par2Random)));
         if (var4 != null) {
            var4.tryDamageItem(DamageSource.generic, 3, par1EntityLivingBase);
         }
      } else if (var4 != null) {
         var4.tryDamageItem(DamageSource.generic, 1, par1EntityLivingBase);
      }

   }

   public boolean canEnchantItem(Item item) {
      return item instanceof ItemCuirass;
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
