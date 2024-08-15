package net.minecraft.entity;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityDireWolf extends EntityWolf {
   public EntityDireWolf(World par1World) {
      super(par1World);
      this.setSize(0.6F, 0.8F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, this.isTamed() ? 24.0 : 16.0);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 5.0);
   }

   protected String getLivingSound() {
      return this.isChild() || this.isTamed() || this.isInLove() || this.worldObj.isBlueMoonNight() || this.getClosestVulnerablePlayer(4.0) == null && !(this.rand.nextFloat() < 0.1F) ? super.getLivingSound() : "mob.wolf.growl";
   }

   protected float getSoundVolume(String sound) {
      return super.getSoundVolume(sound) * 1.5F;
   }

   protected float getSoundPitch(String sound) {
      return super.getSoundPitch(sound) * 0.8F;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && !this.isChild() && !this.isTamed() && !this.isInLove() && !this.worldObj.isBlueMoonNight() && this.rand.nextFloat() < 0.004F) {
         EntityPlayer player = this.getClosestVulnerablePlayer(4.0);
         if (player != null) {
            this.setAttackTarget(player);
         }
      }

   }

   protected int getTamingOutcome(EntityPlayer player) {
      float roll = this.rand.nextFloat();
      if (roll < 0.2F) {
         return -1;
      } else if (roll < 0.4F) {
         return 0;
      } else if (roll > 0.95F) {
         return 1;
      } else {
         roll += this.rand.nextFloat() * (float)player.getExperienceLevel() * 0.02F;
         return roll < 0.5F ? -1 : (roll < 1.0F ? 0 : 1);
      }
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 2;
   }
}
