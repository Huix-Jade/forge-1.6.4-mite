package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityArachnid;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityCaveSpider extends EntityArachnid {
   public EntityCaveSpider(World par1World) {
      super(par1World, 0.7F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16.0);
   }

   public boolean peacefulDuringDay() {
      return false;
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && target instanceof EntityLivingBase) {
            target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.poison.id, 480, 0));
         }

         return result;
      } else {
         return result;
      }
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 2;
   }
}
