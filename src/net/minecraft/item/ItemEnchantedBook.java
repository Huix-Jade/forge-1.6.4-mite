package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item {
   public ItemEnchantedBook(int par1) {
      super(par1, new Material[]{Material.paper, Material.leather}, "book_enchanted");
   }

   public boolean hasEffect(ItemStack par1ItemStack) {
      return true;
   }

   public boolean isItemTool(ItemStack par1ItemStack) {
      return false;
   }

   public EnumRarity getRarity(ItemStack par1ItemStack) {
      return par1ItemStack.hasStoredEnchantments() ? EnumRarity.uncommon : super.getRarity(par1ItemStack);
   }

   public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4, Slot slot) {
      super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4, slot);
      NBTTagList var5 = par1ItemStack.getStoredEnchantmentTagList();
      if (var5 != null) {
         for(int var6 = 0; var6 < var5.tagCount(); ++var6) {
            short var7 = ((NBTTagCompound)var5.tagAt(var6)).getShort("id");
            short var8 = ((NBTTagCompound)var5.tagAt(var6)).getShort("lvl");
            if (Enchantment.enchantmentsList[var7] != null) {
               par3List.add(Enchantment.enchantmentsList[var7].getTranslatedName(var8, par1ItemStack));
            }
         }
      }

   }

   public void addEnchantment(ItemStack par1ItemStack, EnchantmentData par2EnchantmentData) {
      NBTTagList var3 = par1ItemStack.getStoredEnchantmentTagList();
      if (var3 == null) {
         var3 = new NBTTagList();
      }

      boolean var4 = true;

      for(int var5 = 0; var5 < var3.tagCount(); ++var5) {
         NBTTagCompound var6 = (NBTTagCompound)var3.tagAt(var5);
         if (var6.getShort("id") == par2EnchantmentData.enchantmentobj.effectId) {
            if (var6.getShort("lvl") < par2EnchantmentData.enchantmentLevel) {
               var6.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
            }

            var4 = false;
            break;
         }
      }

      if (var4) {
         NBTTagCompound var7 = new NBTTagCompound();
         var7.setShort("id", (short)par2EnchantmentData.enchantmentobj.effectId);
         var7.setShort("lvl", (short)par2EnchantmentData.enchantmentLevel);
         var3.appendTag(var7);
      }

      if (!par1ItemStack.hasTagCompound()) {
         par1ItemStack.setTagCompound(new NBTTagCompound());
      }

      par1ItemStack.getTagCompound().setTag("StoredEnchantments", var3);
   }

   public ItemStack getEnchantedItemStack(EnchantmentData par1EnchantmentData) {
      ItemStack var2 = new ItemStack(this);
      this.addEnchantment(var2, par1EnchantmentData);
      return var2;
   }

   public void func_92113_a(Enchantment par1Enchantment, List par2List) {
      for(int var3 = 1; var3 <= par1Enchantment.getNumLevels(); ++var3) {
         par2List.add(this.getEnchantedItemStack(new EnchantmentData(par1Enchantment, var3)));
      }

   }

   public WeightedRandomChestContent func_92114_b(Random par1Random) {
      return this.func_92112_a(par1Random, 1, 1, 1);
   }

   public WeightedRandomChestContent func_92112_a(Random par1Random, int par2, int par3, int par4) {
      Enchantment var5 = Enchantment.enchantmentsBookList[par1Random.nextInt(Enchantment.enchantmentsBookList.length)];
      ItemStack var6 = new ItemStack(this.itemID, 1, 0);
      int var7 = MathHelper.getRandomIntegerInRange(par1Random, 1, var5.getNumLevels());
      this.addEnchantment(var6, new EnchantmentData(var5, var7));
      return new WeightedRandomChestContent(var6, par2, par3, par4);
   }
}
