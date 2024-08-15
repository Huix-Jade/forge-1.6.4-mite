package net.minecraft.entity;

import net.minecraft.item.ItemDamageResult;

public class EntityDamageResult {
   public final Entity entity;
   private boolean entity_was_affected;
   private boolean entity_was_knocked_back;
   private boolean entity_lost_health;
   private boolean entity_was_destroyed;
   private float amount_of_health_lost;
   private boolean entity_held_item_was_affected;
   private boolean entity_held_item_lost_durability;
   private boolean entity_held_item_was_destroyed;
   private boolean entity_armor_was_affected;
   private boolean entity_armor_lost_durability;
   private boolean entity_armor_was_destroyed;
   private float health_before;

   public EntityDamageResult(Entity entity) {
      this.entity = entity;
   }

   public boolean entityWasNegativelyAffected() {
      return this.entity_was_knocked_back || this.entity_lost_health || this.entity_was_destroyed || this.entity_held_item_lost_durability || this.entity_held_item_was_destroyed || this.entity_armor_lost_durability || this.entity_armor_was_destroyed;
   }

   public boolean entityWasNegativelyAffectedButNotDestroyed() {
      return this.entityWasNegativelyAffected() && !this.entityWasDestroyed();
   }

   public EntityDamageResult setEntityWasAffected() {
      this.entity_was_affected = true;
      return this;
   }

   public boolean entityWasAffected() {
      return this.entity_was_affected;
   }

   public EntityDamageResult setEntityWasKnockedBack() {
      this.entity_was_knocked_back = true;
      return this.setEntityWasAffected();
   }

   public boolean entityWasKnockedBack() {
      return this.entity_was_knocked_back;
   }

   public EntityDamageResult setEntityLostHealth(float amount) {
      if (amount <= 0.0F) {
         return this;
      } else {
         this.entity_lost_health = true;
         this.amount_of_health_lost = amount;
         return this.setEntityWasAffected();
      }
   }

   public void startTrackingHealth(float health_before) {
      this.health_before = health_before;
   }

   public EntityDamageResult finishTrackingHealth(float health_after) {
      float amount_lost = this.health_before - health_after;
      if (amount_lost < 0.0F) {
         return this.setEntityWasAffected();
      } else {
         return amount_lost == 0.0F ? this : this.setEntityLostHealth(amount_lost);
      }
   }

   public boolean entityLostHealth() {
      return this.entity_lost_health;
   }

   public float getAmountOfHealthLost() {
      return this.amount_of_health_lost;
   }

   public EntityDamageResult setEntityWasDestroyed() {
      this.entity_was_destroyed = true;
      return this.setEntityWasAffected();
   }

   public boolean entityWasDestroyed() {
      return this.entity_was_destroyed;
   }

   public boolean entityLostHealthButWasNotDestroyed() {
      return this.entityLostHealth() && !this.entityWasDestroyed();
   }

   public EntityDamageResult setEntityArmorWasAffected() {
      this.entity_armor_was_affected = true;
      return this.setEntityWasAffected();
   }

   public boolean entityArmorWasAffected() {
      return this.entity_armor_was_affected;
   }

   public EntityDamageResult setEntityArmorLostDurability() {
      this.entity_armor_lost_durability = true;
      return this.setEntityArmorWasAffected();
   }

   public boolean entityArmorLostDurability() {
      return this.entity_armor_lost_durability;
   }

   public EntityDamageResult setEntityArmorWasDestroyed() {
      this.entity_armor_was_destroyed = true;
      return this.setEntityArmorWasAffected();
   }

   public boolean entityArmorWasDestroyed() {
      return this.entity_armor_was_destroyed;
   }

   public EntityDamageResult setEntityHeldItemWasAffected() {
      this.entity_held_item_was_affected = true;
      return this.setEntityWasAffected();
   }

   public boolean entityHeldItemWasAffected() {
      return this.entity_held_item_was_affected;
   }

   public EntityDamageResult setEntityHeldItemLostDurability() {
      this.entity_held_item_lost_durability = true;
      return this.setEntityHeldItemWasAffected();
   }

   public boolean entityHeldItemLostDurability() {
      return this.entity_held_item_lost_durability;
   }

   public EntityDamageResult setEntityHeldItemWasDestroyed() {
      this.entity_held_item_was_destroyed = true;
      return this.setEntityHeldItemWasAffected();
   }

   public boolean entityHeldItemWasDestroyed() {
      return this.entity_held_item_was_destroyed;
   }

   public boolean hadNoEffect() {
      return !this.entity_was_affected && !this.entity_held_item_was_affected && !this.entity_armor_was_affected;
   }

   public EntityDamageResult applyHeldItemDamageResult(ItemDamageResult result) {
      if (result != null) {
         if (result.itemLostDurability()) {
            this.setEntityHeldItemLostDurability();
         }

         if (result.itemWasDestroyed()) {
            this.setEntityHeldItemWasDestroyed();
         }
      }

      return this;
   }

   public EntityDamageResult applyArmorDamageResult(ItemDamageResult result) {
      if (result != null) {
         if (result.itemLostDurability()) {
            this.setEntityArmorLostDurability();
         }

         if (result.itemWasDestroyed()) {
            this.setEntityArmorWasDestroyed();
         }
      }

      return this;
   }
}
