package net.minecraft.util;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
   private Entity immediate_entity;
   private Entity responsible_entity;

   public EntityDamageSource(String par1Str, Entity entity) {
      this(par1Str, entity, entity);
   }

   public EntityDamageSource(String par1Str, Entity immediate_entity, Entity responsible_entity) {
      super(par1Str);
      this.immediate_entity = immediate_entity;
      this.responsible_entity = responsible_entity;
      if (immediate_entity instanceof EntityThrowable) {
         EntityThrowable throwable = (EntityThrowable)immediate_entity;
         if (throwable.isMagical()) {
            this.setMagicAspect();
         }
      } else if (immediate_entity instanceof EntityArrow) {
         EntityArrow entity_arrow = (EntityArrow)immediate_entity;
         if (entity_arrow.item_arrow.hasMaterial(Material.silver)) {
            this.setSilverAspect();
         }

         if (entity_arrow.isBurning()) {
            this.setFireAspect();
         }

         if (entity_arrow.getLauncher() != null && entity_arrow.getLauncher().isItemEnchanted()) {
            this.setMagicAspect();
         }
      } else if (!(immediate_entity instanceof EntityFireball) && responsible_entity instanceof EntityLivingBase) {
         EntityLivingBase entity_living_base = (EntityLivingBase)responsible_entity;
         ItemStack held_item = entity_living_base.getHeldItemStack();
         if (held_item != null) {
            if (held_item.hasMaterial(Material.silver)) {
               this.setSilverAspect();
            }

            if (held_item.isItemEnchanted()) {
               this.setMagicAspect();
            }
         }
      }

   }

   public Entity getImmediateEntity() {
      return this.immediate_entity;
   }

   public Entity getResponsibleEntity() {
      return this.responsible_entity;
   }

   public boolean isProjectile() {
      return this.immediate_entity != null && (this.immediate_entity instanceof IProjectile || this.immediate_entity instanceof EntityFireball);
   }

   public boolean isIndirect() {
      return this.responsible_entity != null && this.responsible_entity != this.immediate_entity && !this.isProjectile();
   }

   public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase) {
      Entity entity = this.responsible_entity == null ? this.immediate_entity : this.responsible_entity;
      ItemStack var2 = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItemStack() : null;
      String var3 = "death.attack." + (this.is_hand_damage ? "hand_damage" : this.damageType);
      String var4 = var3 + ".item";
      return var2 != null && var2.hasDisplayName() && StatCollector.func_94522_b(var4) ? ChatMessageComponent.createFromTranslationWithSubstitutions(var4, par1EntityLivingBase.getTranslatedEntityName(), entity.getTranslatedEntityName(), var2.getDisplayName()) : ChatMessageComponent.createFromTranslationWithSubstitutions(var3, par1EntityLivingBase.getTranslatedEntityName(), entity.getTranslatedEntityName());
   }
}
