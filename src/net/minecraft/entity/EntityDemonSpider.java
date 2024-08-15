package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIGetOutOfLava;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityDemonSpider extends EntityArachnid {
   public EntityDemonSpider(World par1World) {
      super(par1World, 1.0F);
      this.tasks.addTask(4, new EntityAIGetOutOfLava(this, 1.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 18.0);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 5.0);
   }

   protected String getLivingSound() {
      return "imported.mob.demonspider.say";
   }

   protected String getHurtSound() {
      return "imported.mob.demonspider.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.demonspider.death";
   }

   public boolean peacefulDuringDay() {
      return false;
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && target instanceof EntityLivingBase) {
            target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.poison.id, 480, 0));
            target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 50, 5));
         }

         return result;
      } else {
         return result;
      }
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public boolean canSpawnInShallowWater() {
      return false;
   }
}
