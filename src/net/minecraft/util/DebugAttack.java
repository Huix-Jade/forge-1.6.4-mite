package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;

public class DebugAttack {
   public static DebugAttack instance;
   public EntityLivingBase attacker;
   public Entity target;
   public DamageSource damage_source;
   public float raw_damage;
   public float target_protection;
   public float damage_dealt_to_armor;
   public float piercing;
   public float resulting_damage;
   public float health_before;
   public float health_after;
   public ItemStack item_attacked_with;

   private DebugAttack(Entity target, Damage damage) {
      Entity responsible_entity = damage.getResponsibleEntity();
      this.attacker = responsible_entity instanceof EntityLivingBase ? responsible_entity.getAsEntityLivingBase() : null;
      this.target = target;
      this.damage_source = damage.getSource();
      if (target instanceof EntityLivingBase) {
         this.health_before = target.getAsEntityLivingBase().getHealth();
      }

      this.raw_damage = damage.getAmount();
      this.item_attacked_with = damage.getItemAttackedWith();
   }

   public static void start(Entity target, Damage damage) {
      if (target.onClient()) {
         Minecraft.setErrorMessage("DebugAttack.start: called on client?");
      }

      if (Minecraft.inDevMode()) {
         if (instance != null) {
            flush();
         }

         instance = new DebugAttack(target, damage);
      }
   }

   public static void setTargetProtection(float target_protection) {
      if (instance != null) {
         instance.target_protection = target_protection;
      }
   }

   public static void setDamageDealtToArmor(float damage_dealt_to_armor) {
      if (instance != null) {
         instance.damage_dealt_to_armor = damage_dealt_to_armor;
      }
   }

   public static void setPiercing(float piercing) {
      if (instance != null) {
         instance.piercing = piercing;
      }
   }

   public static void setResultingDamage(float resulting_damage) {
      if (instance != null) {
         instance.resulting_damage = resulting_damage;
      }
   }

   public static void setHealthAfter(float health_after) {
      if (instance != null) {
         instance.health_after = health_after;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      String target_descriptor = this.target.getEntityName() + " (id=" + this.target.entityId + ")";
      if (this.attacker != null) {
         sb.append((this.attacker.isEntityPlayer() ? "Player" : this.attacker.getEntityName()) + " attacked " + this.target.getEntityName() + (this.item_attacked_with == null ? "" : " with " + this.item_attacked_with.getDisplayName()) + " dealing ");
      } else if (this.damage_source.getImmediateEntity() instanceof EntityArrow) {
         sb.append(target_descriptor + " hit by " + ((EntityArrow)this.damage_source.getImmediateEntity()).getModelItem().getItemDisplayName() + " taking ");
      } else if (this.damage_source.getImmediateEntity() != null) {
         sb.append(target_descriptor + " hit by " + this.damage_source.getImmediateEntity().getEntityName() + " taking ");
      } else {
         sb.append(target_descriptor + " hit by " + this.damage_source + " taking ");
      }

      sb.append(StringHelper.formatFloat(this.raw_damage, 1, 1) + " raw damage " + (this.piercing == 0.0F ? "" : "(+" + this.piercing + " piercing) ") + "vs " + StringHelper.formatFloat(this.target_protection, 1, 1) + " protection, resulting in a loss of " + StringHelper.formatFloat(this.resulting_damage, 1, 1) + " health: " + StringHelper.formatFloat(this.health_before, 1, 1) + "->" + StringHelper.formatFloat(this.health_after, 1, 1));
      return sb.toString();
   }

   public static void flush() {
      instance.flushInstance();
      instance = null;
   }

   private void flushInstance() {
      if (this.target.onClient()) {
         Minecraft.setErrorMessage("flushInstance: called on client?");
      }

      if (this.damage_dealt_to_armor != 0.0F || this.resulting_damage != 0.0F) {
         System.out.println(this);
      }

   }
}
