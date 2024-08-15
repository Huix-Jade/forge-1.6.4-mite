package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityWoodSpider extends EntityArachnid {
   public EntityWoodSpider(World par1World) {
      super(par1World, 0.6F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 6.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.800000011920929);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 1.0);
   }

   protected float getSoundVolume(String sound) {
      return super.getSoundVolume(sound) * 0.6F;
   }

   protected float getSoundPitch(String sound) {
      return super.getSoundPitch(sound) * 1.2F;
   }

   public boolean peacefulDuringDay() {
      return false;
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && target instanceof EntityLivingBase) {
            if (this.getClass() == EntityWoodSpider.class) {
               target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.poison.id, 240, 0));
            } else if (this.isBlackWidowSpider()) {
               target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.poison.id, 960, 0));
            }
         }

         return result;
      } else {
         return result;
      }
   }

   protected int getDropItemId() {
      return this.rand.nextFloat() < 0.4F ? Item.silk.itemID : -1;
   }

   public float getMaxTargettingRange() {
      return this.getBrightness(1.0F) < 0.65F ? super.getMaxTargettingRange() : super.getMaxTargettingRange() * 0.5F;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }
}
