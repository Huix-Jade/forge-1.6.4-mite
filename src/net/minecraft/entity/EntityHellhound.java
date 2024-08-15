package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIGetOutOfLava;
import net.minecraft.entity.ai.EntityAIGetOutOfWater;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityHellhound extends EntityWolf implements IMob {
   public EntityHellhound(World par1World) {
      super(par1World);
      this.setSize(0.6F, 0.8F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.clear();
      this.targetTasks.clear();
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, 1.0, true));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 20, true));
      this.tasks.addTask(4, new EntityAIGetOutOfWater(this, 1.0F));
      this.tasks.addTask(4, new EntityAIGetOutOfLava(this, 1.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.4000000059604645);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 20.0);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 4.0);
   }

   protected String getLivingSound() {
      return "imported.mob.hellhound.say";
   }

   protected String getHurtSound() {
      return "imported.mob.hellhound.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.hellhound.death";
   }

   protected String getLongDistanceLivingSound() {
      return null;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityWasNegativelyAffected() && this.getRNG().nextFloat() < 0.4F) {
            this.makeSound("imported.mob.hellhound.breath", 4.0F, 1.0F);
            target.setFire(1 + this.rand.nextInt(8));
         }

         return result;
      } else {
         return result;
      }
   }

   public boolean a(EntityPlayer par1EntityPlayer) {
      return false;
   }

   public boolean isHostileToPlayers() {
      return true;
   }

   public boolean canMateWith(EntityAnimal par1EntityAnimal) {
      return false;
   }

   public boolean isTamed() {
      return false;
   }

   public float getBlockPathWeight(int par1, int par2, int par3) {
      return this.worldObj.isDaytime() && this.worldObj.canBlockSeeTheSky(par1, par2, par3) ? -0.5F : 10.0F;
   }

   public boolean isEntityBiologicallyAlive() {
      return false;
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return false;
   }

   protected boolean isValidLightLevel() {
      return EntityMob.isValidLightLevel(this);
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }

   public boolean canSpawnInShallowWater() {
      return false;
   }
}
