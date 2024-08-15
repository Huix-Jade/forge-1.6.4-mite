package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

public class Damage {
   private DamageSource source;
   private float amount;
   private float MAXIMUM_DAMAGE = 1000.0F;
   private boolean ignore_specific_immunities;
   private boolean knockback_only;

   public Damage(DamageSource source, float amount) {
      this.source = source;
      this.amount = amount;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public Damage setAmount(float amount) {
      this.amount = amount;
      return this;
   }

   public Damage setToMaximumAmount() {
      this.amount = this.MAXIMUM_DAMAGE;
      return this;
   }

   public Damage addAmount(float amount) {
      this.setAmount(this.getAmount() + amount);
      return this;
   }

   public float getAmount() {
      return this.amount;
   }

   public Damage scaleAmount(float factor) {
      this.amount *= factor;
      return this;
   }

   public Damage scaleAmount(float factor, float floor) {
      this.amount *= factor;
      if (this.amount < floor) {
         this.amount = floor;
      }

      return this;
   }

   public Damage setNil() {
      this.amount = 0.0F;
      return this;
   }

   public boolean isNil() {
      return this.amount <= 0.0F;
   }

   public boolean isLessThanHalfAHeart() {
      return this.amount < 1.0F;
   }

   public String toString() {
      return this.getSource().toString() + " @ " + this.amount;
   }

   public Entity getResponsibleEntity() {
      return this.source.getResponsibleEntity();
   }

   public Entity getImmediateEntity() {
      return this.source.getImmediateEntity();
   }

   public boolean isExplosion() {
      return this.source.isExplosion();
   }

   public boolean isFireDamage() {
      return this.source.isFireDamage();
   }

   public boolean isLavaDamage() {
      return this.source.isLavaDamage();
   }

   public boolean isAnvil() {
      return this.source.isAnvil();
   }

   public boolean isFallingBlock() {
      return this.source.isFallingBlock();
   }

   public boolean isDrowning() {
      return this.source.isDrowning();
   }

   public boolean hasMagicAspect() {
      return this.source.hasMagicAspect();
   }

   public boolean isStarving() {
      return this.source.isStarving();
   }

   public boolean isIndirect() {
      return this.source.isIndirect();
   }

   public boolean wasCausedByPlayer() {
      return this.source.wasCausedByPlayer();
   }

   public boolean wasCausedByPlayerInCreative() {
      return this.source.wasCausedByPlayerInCreative();
   }

   public boolean isFireballFromPlayer() {
      return this.source.isFireballFromPlayer();
   }

   public boolean isAbsolute() {
      return this.source.isAbsolute();
   }

   public boolean isSnowball() {
      return this.source.isSnowball();
   }

   public boolean isPlayerThrownSnowball() {
      return this.source.isPlayerThrownSnowball();
   }

   public boolean isSunlight() {
      return this.source.isSunlight();
   }

   public boolean isArrowDamage() {
      return this.source.isArrowDamage();
   }

   public boolean isArrowFromPlayer() {
      return this.source.isArrowFromPlayer();
   }

   public boolean isMelee() {
      return this.source.isMelee();
   }

   public boolean isEggDamage() {
      return this.source.isEggDamage();
   }

   public boolean isPepsinDamage() {
      return this.source.isPepsinDamage();
   }

   public boolean isAcidDamage() {
      return this.source.isAcidDamage();
   }

   public boolean isFallDamage() {
      return this.source.isFallDamage();
   }

   public boolean isPoison() {
      return this.source.isPoison();
   }

   public boolean bypassesMundaneArmor() {
      return this.source.bypassesMundaneArmor();
   }

   public ItemStack getItemAttackedWith() {
      return this.source.getItemAttackedWith();
   }

   public float applyTargetDefenseModifiers(EntityLivingBase target, EntityDamageResult result) {
      if (target.onClient()) {
         Minecraft.setErrorMessage("applyTargetDefenseModifiers: called on client?");
      }

      if (this.amount <= 0.0F) {
         return 0.0F;
      } else if (this.isAbsolute()) {
         return this.amount;
      } else {
         if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)target;
            if (!this.bypassesMundaneArmor() && player.isBlocking()) {
               this.amount /= 2.0F;
               if (this.amount < 1.0F) {
                  this.amount = 1.0F;
               }

               ItemStack item_stack = player.getHeldItemStack();
               if (item_stack != null && item_stack.getItem() instanceof ItemTool) {
                  ItemTool item_tool = (ItemTool)item_stack.getItem();
                  result.applyHeldItemDamageResult(item_stack.tryDamageItem(DamageSource.generic, (int)(this.amount * (float)item_tool.getToolDecayFromAttackingEntity(item_stack, (EntityLivingBase)null)), target));
               }
            }
         }

         float total_protection = target.getTotalProtection(this.getSource());
         DebugAttack.setTargetProtection(total_protection);
         float amount_dealt_to_armor = Math.min(target.getProtectionFromArmor(this.getSource(), false), this.amount);
         target.tryDamageArmor(this.getSource(), amount_dealt_to_armor, result);
         DebugAttack.setDamageDealtToArmor(amount_dealt_to_armor);
         float piercing = Enchantment.piercing.getLevelFraction(this.getItemAttackedWith()) * 5.0F;
         float effective_protection = Math.max(total_protection - piercing, 0.0F);
         DebugAttack.setPiercing(piercing);
         if (target instanceof EntityPlayer && effective_protection >= this.amount) {
            int delta = (int)(effective_protection - this.amount);

            for(int i = -1; i < delta; ++i) {
               if (target.rand.nextFloat() < 0.2F) {
                  return 0.0F;
               }
            }
         }

         return Math.max(this.amount - effective_protection, 1.0F);
      }
   }

   public Damage setIgnoreSpecificImmunities() {
      this.ignore_specific_immunities = true;
      return this;
   }

   public boolean ignoreSpecificImmunities() {
      return this.ignore_specific_immunities;
   }

   public Damage setKnockbackOnly() {
      this.knockback_only = true;
      return this;
   }

   public boolean isKnockbackOnly() {
      return this.knockback_only;
   }

   public Damage setFireAspect(boolean has_fire_aspect) {
      this.source.setFireAspect(has_fire_aspect);
      return this;
   }

   public boolean canHarmInCreative() {
      return this.source.canHarmInCreative();
   }

   public static boolean wasCausedByPlayer(Damage damage) {
      return damage != null && damage.wasCausedByPlayer();
   }
}
