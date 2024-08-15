package net.minecraft.enchantment;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBoots;
import net.minecraft.item.ItemCuirass;
import net.minecraft.item.ItemLeggings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EnchantmentProtection extends Enchantment {
   public EnchantmentProtection(int id, EnumRarity rarity, int difficulty) {
      super(id, rarity, difficulty);
   }

   public int getNumLevels() {
      return 4;
   }

   private String getProtectionType() {
      if (this == protection) {
         return "all";
      } else if (this == fireProtection) {
         return "fire";
      } else if (this == featherFalling) {
         return "fall";
      } else if (this == blastProtection) {
         return "explosion";
      } else if (this == projectileProtection) {
         return "projectile";
      } else {
         Minecraft.setErrorMessage("getProtectionType: no handler for " + this);
         return null;
      }
   }

   private boolean canReduceDamage(DamageSource damage_source) {
      if (damage_source == null) {
         return this == protection;
      } else if (damage_source.canHarmInCreative()) {
         return false;
      } else if (this == protection) {
         return true;
      } else if (this == fireProtection) {
         return damage_source.isFireDamage();
      } else if (this == featherFalling) {
         return damage_source == DamageSource.fall;
      } else if (this == blastProtection) {
         return damage_source.isExplosion();
      } else if (this == projectileProtection) {
         return damage_source.isProjectile();
      } else {
         Minecraft.setErrorMessage("canReduceDamage: no handler for " + this);
         return false;
      }
   }

   public float getDefenseSqPerLevel() {
      if (this == protection) {
         return 4.0F;
      } else if (this == fireProtection) {
         return 6.0F;
      } else if (this == featherFalling) {
         return 16.0F;
      } else if (this == blastProtection) {
         return 8.0F;
      } else if (this == projectileProtection) {
         return 8.0F;
      } else {
         Minecraft.setErrorMessage("getDefensePerLevel: no handler for " + this);
         return 0.0F;
      }
   }

   public static float getTotalProtectionOfEnchantments(ItemStack[] armors, DamageSource damage_source, EntityLivingBase owner) {
      EnchantmentProtection[] protection_enchantments = new EnchantmentProtection[]{Enchantment.fireProtection, Enchantment.featherFalling, Enchantment.blastProtection, Enchantment.projectileProtection};
      int max_enchantment_level = protection_enchantments[1].getNumLevels();
      float total_protection = 0.0F;

      for(int i = 0; i < protection_enchantments.length; ++i) {
         EnchantmentProtection protection_enchantment = protection_enchantments[i];
         if (protection_enchantment.getNumLevels() != max_enchantment_level) {
            Minecraft.setErrorMessage("getTotalDefenseOfEnchantments: " + protection_enchantment + " has a different number of levels!");
         }

         if (protection_enchantment.canReduceDamage(damage_source)) {
            for(int j = 0; j < armors.length; ++j) {
               ItemStack item_stack = armors[j];
               if (item_stack != null) {
                  float enchantment_level_fraction = item_stack.getEnchantmentLevelFraction(protection_enchantment);
                  if (enchantment_level_fraction > 0.0F) {
                     Item item = item_stack.getItem();
                     if (item instanceof ItemArmor) {
                        ItemArmor armor = item_stack.getItemAsArmor();
                        if (protection_enchantment == Enchantment.featherFalling) {
                           return 15.0F * enchantment_level_fraction * armor.getDamageFactor(item_stack, owner);
                        }

                        total_protection += armor.getProtectionAfterDamageFactor(item_stack, owner) * enchantment_level_fraction;
                     } else {
                        Minecraft.setErrorMessage("getTotalProtectionOfEnchantments: don't know how to handle enchanted items that aren't armor");
                     }
                  }
               }
            }
         }
      }

      return total_protection;
   }

   public String getNameSuffix() {
      return "protect." + this.getProtectionType();
   }

   public boolean canApplyTogether(Enchantment par1Enchantment) {
      if (!(par1Enchantment instanceof EnchantmentProtection)) {
         return super.canApplyTogether(par1Enchantment);
      } else {
         EnchantmentProtection var2 = (EnchantmentProtection)par1Enchantment;
         return var2 == this ? false : this == featherFalling || var2 == featherFalling;
      }
   }

   public static int getFireTimeForEntity(Entity par0Entity, int par1) {
      int var2 = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.fireProtection.effectId, par0Entity.getLastActiveItems());
      if (var2 > 0) {
         par1 -= MathHelper.floor_float((float)par1 * (float)var2 * 0.15F);
      }

      return par1;
   }

   public static double func_92092_a(Entity par0Entity, double par1) {
      int var3 = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.blastProtection.effectId, par0Entity.getLastActiveItems());
      if (var3 > 0) {
         par1 -= (double)MathHelper.floor_double(par1 * (double)((float)var3 * 0.15F));
      }

      return par1;
   }

   public boolean canEnchantItem(Item item) {
      if (!item.isArmor()) {
         return false;
      } else {
         ItemArmor armor = (ItemArmor)item;
         if (this == protection) {
            return item instanceof ItemArmor;
         } else if (this == fireProtection) {
            return item instanceof ItemArmor;
         } else if (this == featherFalling) {
            return item instanceof ItemBoots;
         } else if (this == blastProtection) {
            return (item instanceof ItemCuirass || item instanceof ItemLeggings) && armor.isSolidMetal();
         } else if (this != projectileProtection) {
            Minecraft.setErrorMessage("canEnchantItem: no handler for " + this);
            return false;
         } else {
            return (item instanceof ItemCuirass || item instanceof ItemLeggings) && armor.isSolidMetal();
         }
      }
   }

   public boolean isOnCreativeTab(CreativeTabs creative_tab) {
      return creative_tab == CreativeTabs.tabCombat;
   }
}
