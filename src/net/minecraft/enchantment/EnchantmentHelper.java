package net.minecraft.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper
{
   /** Is the random seed of enchantment effects. */
   private static final Random enchantmentRand = new Random();

   public static boolean hasEnchantment(ItemStack item_stack, Enchantment enchantment)
   {
      return getEnchantmentLevel(enchantment.effectId, item_stack) > 0;
   }

   /**
    * Returns the level of enchantment on the ItemStack passed.
    */
   public static int getEnchantmentLevel(int par0, ItemStack par1ItemStack)
   {
      if (par1ItemStack == null)
      {
         return 0;
      }
      else
      {
         NBTTagList var2 = par1ItemStack.getEnchantmentTagList();

         if (var2 == null)
         {
            return 0;
         }
         else
         {
            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
               short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
               short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");

               if (var4 == par0)
               {
                  return var5;
               }
            }

            return 0;
         }
      }
   }

   public static int getEnchantmentLevel(Enchantment enchantment, ItemStack item_stack)
   {
      return getEnchantmentLevel(enchantment.effectId, item_stack);
   }

   public static float getEnchantmentLevelFraction(Enchantment enchantment, ItemStack item_stack)
   {
      return !hasEnchantment(item_stack, enchantment) ? 0.0F : (enchantment.hasLevels() ? (float)getEnchantmentLevel(enchantment, item_stack) / (float)enchantment.getNumLevels() : 1.0F);
   }

   public static int getEnchantmentLevelFractionOfInteger(Enchantment enchantment, ItemStack item_stack, int integer)
   {
      return !hasEnchantment(item_stack, enchantment) ? 0 : (enchantment.hasLevels() ? Math.round((float)integer * getEnchantmentLevelFraction(enchantment, item_stack)) : integer);
   }

   public static int getEnchantmentLevels(Enchantment enchantment, ItemStack[] item_stacks)
   {
      int levels = 0;

      for (int i = 0; i < item_stacks.length; ++i)
      {
         levels += getEnchantmentLevel(enchantment, item_stacks[i]);
      }

      return levels;
   }

   public static Map getEnchantmentsMapFromTags(NBTTagList enchantment_tag_list)
   {
      LinkedHashMap map = new LinkedHashMap();

      if (enchantment_tag_list == null)
      {
         return map;
      }
      else
      {
         for (int i = 0; i < enchantment_tag_list.tagCount(); ++i)
         {
            NBTTagCompound tag = (NBTTagCompound)enchantment_tag_list.tagAt(i);
            map.put(Integer.valueOf(tag.getShort("id")), Integer.valueOf(tag.getShort("lvl")));
         }

         return map;
      }
   }

   public static Map getEnchantmentsMap(ItemStack item_stack)
   {
      if (item_stack.getItem() == Item.enchantedBook)
      {
         Minecraft.setErrorMessage("getEnchantmentsMap: item is enchanted book, wrong func?");
      }

      return getEnchantmentsMapFromTags(item_stack.getEnchantmentTagList());
   }

   public static Map getStoredEnchantmentsMap(ItemStack item_stack)
   {
      if (item_stack.getItem() != Item.enchantedBook)
      {
         Minecraft.setErrorMessage("getStoredEnchantmentsMap: item is not enchanted book, wrong func?");
      }

      return getEnchantmentsMapFromTags(item_stack.getStoredEnchantmentTagList());
   }

   /**
    * Set the enchantments for the specified stack.
    */
   public static void setEnchantments(Map par0Map, ItemStack par1ItemStack)
   {
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = par0Map.keySet().iterator();

      while (var3.hasNext())
      {
         int var4 = ((Integer)var3.next()).intValue();
         NBTTagCompound var5 = new NBTTagCompound();
         var5.setShort("id", (short)var4);
         var5.setShort("lvl", (short)((Integer)par0Map.get(Integer.valueOf(var4))).intValue());
         var2.appendTag(var5);

         if (par1ItemStack.itemID == Item.enchantedBook.itemID)
         {
            Item.enchantedBook.addEnchantment(par1ItemStack, new EnchantmentData(var4, ((Integer)par0Map.get(Integer.valueOf(var4))).intValue()));
         }
      }

      if (var2.tagCount() > 0)
      {
         if (par1ItemStack.itemID != Item.enchantedBook.itemID)
         {
            par1ItemStack.setTagInfo("ench", var2);
         }
      }
      else if (par1ItemStack.hasTagCompound())
      {
         par1ItemStack.getTagCompound().removeTag("ench");
      }
   }

   public static boolean hasValidEnchantmentForItem(NBTTagList enchantments, Item item)
   {
      for (int i = 0; i < enchantments.tagCount(); ++i)
      {
         NBTTagCompound tag = (NBTTagCompound)enchantments.tagAt(i);
         short id = tag.getShort("id");
         Enchantment enchantment = Enchantment.enchantmentsList[id];

         if (enchantment.canEnchantItem(item))
         {
            return true;
         }
      }

      return false;
   }

   /**
    * Returns the biggest level of the enchantment on the array of ItemStack passed.
    */
   public static int getMaxEnchantmentLevel(int par0, ItemStack[] par1ArrayOfItemStack)
   {
      if (par1ArrayOfItemStack == null)
      {
         return 0;
      }
      else
      {
         int var2 = 0;
         ItemStack[] var3 = par1ArrayOfItemStack;
         int var4 = par1ArrayOfItemStack.length;

         for (int var5 = 0; var5 < var4; ++var5)
         {
            ItemStack var6 = var3[var5];
            int var7 = getEnchantmentLevel(par0, var6);

            if (var7 > var2)
            {
               var2 = var7;
            }
         }

         return var2;
      }
   }

   /**
    * Returns the knockback value of enchantments on equipped player item.
    */
   public static int getKnockbackModifier(EntityLivingBase par0EntityLivingBase, EntityLivingBase par1EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.knockback.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   public static int getFireAspectModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.fireAspect.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   /**
    * Returns the 'Water Breathing' modifier of enchantments on player equipped armors.
    */
   public static int getRespiration(EntityLivingBase par0EntityLivingBase)
   {
      return getMaxEnchantmentLevel(Enchantment.respiration.effectId, par0EntityLivingBase.getLastActiveItems());
   }

   /**
    * Return the extra efficiency of tools based on enchantments on equipped player item.
    */
   public static int getEfficiencyModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.efficiency.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   /**
    * Returns the silk touch status of enchantments on current equipped item of player.
    */
   public static boolean getSilkTouchModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.silkTouch.effectId, par0EntityLivingBase.getHeldItemStack()) > 0;
   }

   /**
    * Returns the fortune enchantment modifier of the current equipped item of player.
    */
   public static int getFortuneModifier(EntityLivingBase par0EntityLivingBase)
   {
      return par0EntityLivingBase == null ? 0 : getEnchantmentLevel(Enchantment.fortune.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   /**
    * Returns the looting enchantment modifier of the current equipped item of player.
    */
   public static int getLootingModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.looting.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   /**
    * Returns the aqua affinity status of enchantments on current equipped item of player.
    */
   public static boolean getAquaAffinityModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, par0EntityLivingBase.getLastActiveItems()) > 0;
   }

   public static int func_92098_i(EntityLivingBase par0EntityLivingBase)
   {
      return getMaxEnchantmentLevel(Enchantment.thorns.effectId, par0EntityLivingBase.getLastActiveItems());
   }

   public static ItemStack func_92099_a(Enchantment par0Enchantment, EntityLivingBase par1EntityLivingBase)
   {
      ItemStack[] var2 = par1EntityLivingBase.getLastActiveItems();
      int var3 = var2.length;

      for (int var4 = 0; var4 < var3; ++var4)
      {
         ItemStack var5 = var2[var4];

         if (var5 != null && getEnchantmentLevel(par0Enchantment.effectId, var5) > 0)
         {
            return var5;
         }
      }

      return null;
   }

   /**
    * Adds a random enchantment to the specified item. Args: random, itemStack, enchantabilityLevel
    */
   public static ItemStack addRandomEnchantment(Random par0Random, ItemStack par1ItemStack, int par2)
   {
      par2 = getEnchantmentLevelsAlteredByItemEnchantability(par2, par1ItemStack.getItem());

      if (par2 < 1)
      {
         return par1ItemStack;
      }
      else
      {
         List var3 = buildEnchantmentList(par0Random, par1ItemStack, par2);
         boolean var4 = par1ItemStack.itemID == Item.book.itemID;

         if (var4)
         {
            par1ItemStack.itemID = Item.enchantedBook.itemID;
         }

         if (var3 != null)
         {
            Iterator var5 = var3.iterator();

            while (var5.hasNext())
            {
               EnchantmentData var6 = (EnchantmentData)var5.next();

               if (var4)
               {
                  Item.enchantedBook.addEnchantment(par1ItemStack, var6);
               }
               else
               {
                  par1ItemStack.addEnchantment(var6.enchantmentobj, var6.enchantmentLevel);
               }
            }
         }

         return par1ItemStack;
      }
   }

   public static int getEnchantmentLevelsAlteredByItemEnchantability(int enchantment_levels, Item item)
   {
      int item_enchantability = item.getItemEnchantability();

      if (item_enchantability < 1)
      {
         return 0;
      }
      else if (enchantment_levels <= item_enchantability)
      {
         return enchantment_levels;
      }
      else
      {
         float enchantment_levels_float = (float)item_enchantability;

         for (int i = item_enchantability + 1; i <= enchantment_levels; ++i)
         {
            if (i <= item_enchantability * 2)
            {
               enchantment_levels_float += 0.5F;
            }
            else
            {
               if (i > item_enchantability * 3)
               {
                  break;
               }

               enchantment_levels_float += 0.25F;
            }
         }

         return Math.round(enchantment_levels_float);
      }
   }

   private static void removeEnchantmentsFromMapThatConflict(Map map, ArrayList enchantments)
   {
      for (int i = 0; i < enchantments.size(); ++i)
      {
         EnchantmentData enchantment_data = (EnchantmentData)enchantments.get(i);
         Enchantment enchantment = enchantment_data.enchantmentobj;
         Iterator iterator = map.keySet().iterator();

         while (iterator.hasNext())
         {
            int id = ((Integer)iterator.next()).intValue();

            if (!enchantment.canApplyTogether(Enchantment.get(id)))
            {
               iterator.remove();
            }
         }
      }
   }

   /**
    * Create a list of random EnchantmentData (enchantments) that can be added together to the ItemStack, the 3rd
    * parameter is the total enchantability level.
    */
   public static List buildEnchantmentList(Random par0Random, ItemStack par1ItemStack, int par2)
   {
      Item var3 = par1ItemStack.getItem();
      int var4 = var3.getItemEnchantability();

      if (var4 <= 0)
      {
         return null;
      }
      else
      {
         float var5 = 1.0F + (par0Random.nextFloat() - 0.5F) * 0.5F;
         int var6 = (int)((float)par2 * var5);

         if (var6 < 1)
         {
            var6 = 1;
         }

         ArrayList var7 = new ArrayList();

         while (var6 > 0)
         {
            Map var8 = mapEnchantmentData(var6, par1ItemStack);

            if (var8 == null)
            {
               break;
            }

            removeEnchantmentsFromMapThatConflict(var8, var7);

            if (var8.isEmpty())
            {
               break;
            }

            EnchantmentData var9 = (EnchantmentData) WeightedRandom.getRandomItem(par0Random, var8.values());

            if (var9 == null)
            {
               break;
            }

            Enchantment var10 = var9.enchantmentobj;

            if (var7.size() < 2 && var8.size() > 1 && var10.hasLevels() && par0Random.nextInt(2) == 0)
            {
               var9.enchantmentLevel = par0Random.nextInt(var9.enchantmentLevel) + 1;
            }

            var7.add(var9);
            var6 -= var10.hasLevels() ? var10.getMinEnchantmentLevelsCost(var9.enchantmentLevel) : var10.getMinEnchantmentLevelsCost();
            var6 -= 5;

            if (var6 < 5 || var7.size() > 2)
            {
               break;
            }
         }

         ArrayList var12 = new ArrayList();
         int var13 = var7.size();

         while (var13 > 0)
         {
            int var14 = par0Random.nextInt(var7.size());
            EnchantmentData var11 = (EnchantmentData)var7.get(var14);

            if (var11 != null)
            {
               var12.add(var11);
               var7.set(var14, (Object)null);
               --var13;
            }
         }

         return var12.size() == 0 ? null : var12;
      }
   }

   /**
    * Creates a 'Map' of EnchantmentData (enchantments) possible to add on the ItemStack and the enchantability level
    * passed.
    */
   private static Map mapEnchantmentData(int par0, ItemStack par1ItemStack)
   {
      Item var2 = par1ItemStack.getItem();
      boolean var3 = var2 == Item.book;
      HashMap var4 = new HashMap();

      for (int var5 = 0; var5 < Enchantment.enchantmentsList.length; ++var5)
      {
         Enchantment var6 = Enchantment.get(var5);

         if (var6 != null && (var3 || var6.canEnchantItem(var2)))
         {
            if (var6.hasLevels())
            {
               for (int var7 = var6.getNumLevels(); var7 > 0; --var7)
               {
                  if (var6.getMinEnchantmentLevelsCost(var7) <= par0)
                  {
                     var4.put(Integer.valueOf(var6.effectId), new EnchantmentData(var6, var7));
                     break;
                  }
               }
            }
            else if (var6.getMinEnchantmentLevelsCost() <= par0)
            {
               var4.put(Integer.valueOf(var6.effectId), new EnchantmentData(var6, 1));
            }
         }
      }

      return var4.size() == 0 ? null : var4;
   }

   public static int getStunModifier(EntityLivingBase par0EntityLivingBase, EntityLivingBase par1EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.stun.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   public static int getFishingFortuneModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.fishing_fortune.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   public static int getFertilityModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.fertility.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   public static int getTreeFellingModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.tree_felling.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   public static int getVampiricTransfer(EntityLivingBase par0EntityLivingBase, EntityLivingBase par1EntityLivingBase, float inflicted_damage)
   {
      if (inflicted_damage > 0.0F && par1EntityLivingBase != null && par1EntityLivingBase.isEntityBiologicallyAlive())
      {
         int potential_effect = getEnchantmentLevel(Enchantment.vampiric.effectId, par0EntityLivingBase.getHeldItemStack());

         if (Math.random() * 10.0D > (double)potential_effect)
         {
            return 0;
         }
         else
         {
            int transfer = (int)((double)(inflicted_damage * 0.5F) * Math.random());

            if (transfer < 1)
            {
               transfer = 1;
            }

            return transfer;
         }
      }
      else
      {
         return 0;
      }
   }

   public static float getSpeedModifier(EntityLivingBase par0EntityLivingBase)
   {
      return 1.0F + (float)getMaxEnchantmentLevel(Enchantment.speed.effectId, par0EntityLivingBase.getLastActiveItems()) * 0.05F;
   }

   public static float getRegenerationModifier(EntityLivingBase par0EntityLivingBase)
   {
      return 1.0F + (float)getMaxEnchantmentLevel(Enchantment.regeneration.effectId, par0EntityLivingBase.getLastActiveItems()) * 0.5F;
   }

   public static int getFreeActionModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getMaxEnchantmentLevel(Enchantment.free_action.effectId, par0EntityLivingBase.getLastActiveItems());
   }

   public static int getButcheringModifier(EntityLivingBase par0EntityLivingBase)
   {
      return getEnchantmentLevel(Enchantment.butchering.effectId, par0EntityLivingBase.getHeldItemStack());
   }

   public static float getEnduranceModifier(EntityLivingBase par0EntityLivingBase)
   {
      return 1.0F - (float)getMaxEnchantmentLevel(Enchantment.endurance.effectId, par0EntityLivingBase.getLastActiveItems()) * 0.2F;
   }
}
