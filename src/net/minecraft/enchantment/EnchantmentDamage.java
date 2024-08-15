package net.minecraft.enchantment;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBattleAxe;
import net.minecraft.item.ItemScythe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemWarHammer;

public class EnchantmentDamage extends Enchantment {
   public EnchantmentDamage(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public String getTranslatedName(Item item) {
      return this == sharpness && item instanceof ItemAxe ? "Slaying" : super.getTranslatedName(item);
   }

   public float getDamageModifier(int level, EntityLivingBase target) {
      if (this == sharpness) {
         return (float)level * 1.0F;
      } else if (this == smite) {
         return target != null && target.isEntityUndead() ? (float)level * 2.0F : 0.0F;
      } else if (this != baneOfArthropods) {
         Minecraft.setErrorMessage("getDamageModifier: no handler for " + this);
         return 0.0F;
      } else {
         return target != null && target.isArthropod() ? (float)level * 2.0F : 0.0F;
      }
   }

   public static float getDamageModifiers(ItemStack weapon, EntityLivingBase target) {
      float damage_modifiers = 0.0F;
      if (weapon != null && weapon.isItemEnchanted() && weapon.getItem() != Item.enchantedBook) {
         for(int i = 0; i < Enchantment.enchantmentsList.length; ++i) {
            Enchantment enchantment = Enchantment.get(i);
            if (enchantment instanceof EnchantmentDamage) {
               EnchantmentDamage enchantment_damage = (EnchantmentDamage)enchantment;
               int level = weapon.getEnchantmentLevel(enchantment_damage);
               if (level > 0) {
                  damage_modifiers += enchantment_damage.getDamageModifier(level, target);
               }
            }
         }

         return damage_modifiers;
      } else {
         return damage_modifiers;
      }
   }

   public String getNameSuffix() {
      if (this == sharpness) {
         return "damage.all";
      } else if (this == smite) {
         return "damage.undead";
      } else if (this == baneOfArthropods) {
         return "damage.arthropods";
      } else {
         Minecraft.setErrorMessage("getNameSuffix: no handler for " + this);
         return null;
      }
   }

   public boolean canApplyTogether(Enchantment par1Enchantment) {
      return !(par1Enchantment instanceof EnchantmentDamage);
   }

   public boolean canEnchantItem(Item item) {
      if (this != sharpness) {
         if (this == baneOfArthropods) {
            return item instanceof ItemSword;
         } else if (this == smite) {
            return item.getClass() == ItemWarHammer.class;
         } else {
            return false;
         }
      } else {
         return item instanceof ItemSword || item.getClass() == ItemBattleAxe.class || item instanceof ItemScythe;
      }
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
