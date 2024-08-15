package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityWight extends EntityMob {
   public EntityWight(World par1World) {
      super(par1World);
      this.getNavigator().setBreakDoors(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(5.0);
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      return !damage_source.hasFireAspect() && !damage_source.isLavaDamage() && !damage_source.hasSilverAspect() && !damage_source.hasMagicAspect();
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public void onUpdate() {
      super.onUpdate();
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && target instanceof EntityPlayer && this.getRNG().nextFloat() < 0.4F) {
            int drain = Math.max((target.getAsPlayer().getExperienceLevel() + 1) * 10, 20);
            target.getAsPlayer().addExperience(-target.getAsPlayer().getDrainAfterResistance(drain));
         }

         return result;
      } else {
         return result;
      }
   }

   protected String getLivingSound() {
      return "imported.mob.wight.say";
   }

   protected String getHurtSound() {
      return "imported.mob.wight.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.wight.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.zombie.step", 0.15F, 1.0F);
   }

   protected int getDropItemId() {
      return 0;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (this.rand.nextFloat() < (recently_hit_by_player ? 0.5F : 0.25F)) {
         this.dropItem(Item.rottenFlesh.itemID, 1);
      }

      if (recently_hit_by_player && !this.has_taken_massive_fall_damage && this.rand.nextInt(this.getBaseChanceOfRareDrop()) < 5 + damage_source.getLootingModifier() * 2) {
         switch (this.rand.nextInt(4)) {
            case 0:
               this.dropItem(Item.copperNugget);
               break;
            case 1:
               this.dropItem(Item.silverNugget);
               break;
            case 2:
               this.dropItem(Item.goldNugget);
               break;
            case 3:
               this.dropItem(Item.ironNugget);
         }
      }

   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
   }

   public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
      super.onKillEntity(par1EntityLivingBase);
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      return super.onSpawnWithEgg(par1EntityLivingData);
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 2;
   }
}
