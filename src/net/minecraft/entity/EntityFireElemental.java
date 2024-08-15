package net.minecraft.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIGetOutOfWater;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISeekShelterFromRain;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFireElemental extends EntityMob {
   private int ticks_until_next_fire_sound;
   private int ticks_until_next_fizz_sound;

   public EntityFireElemental(World par1World) {
      super(par1World);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.tasks.addTask(1, new EntityAIGetOutOfWater(this, 1.0F));
      this.tasks.addTask(2, new EntityAISeekShelterFromRain(this, 1.0F, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(5.0);
   }

   protected void entityInit() {
      super.entityInit();
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      if (!damage_source.isWater() && !(damage_source.getImmediateEntity() instanceof EntitySnowball)) {
         if (damage_source.hasMagicAspect()) {
            if (damage_source.isArrowDamage()) {
               EntityArrow arrow = (EntityArrow)damage_source.getImmediateEntity();
               if (arrow.getLauncher() == null || !arrow.getLauncher().hasEnchantment(Enchantment.flame, true)) {
                  return false;
               }
            } else {
               ItemStack item_stack = damage_source.getItemAttackedWith();
               if (item_stack == null || !item_stack.hasEnchantment(Enchantment.fireAspect, true)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public void onLivingUpdate() {
      if (this.worldObj.isRemote && this.isWet()) {
         this.spawnSteamParticles(this.inWater ? 10 : 1);
      }

      if (!this.worldObj.isRemote) {
         if (this.getTicksExistedWithOffset() % 40 == 0) {
            this.attackEntityFrom(new Damage(DamageSource.water, 1.0F));
         }

         if (this.isWet()) {
            if (--this.ticks_until_next_fizz_sound <= 0) {
               this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               this.ticks_until_next_fizz_sound = this.rand.nextInt(7) + 2;
               if (this.rand.nextInt(this.inWater ? 1 : 4) == 0) {
                  this.attackEntityFrom(new Damage(DamageSource.water, 1.0F));
               }
            }
         } else if (--this.ticks_until_next_fire_sound <= 0) {
            this.playSound("fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
            this.ticks_until_next_fire_sound = this.rand.nextInt(21) + 30;
            if (this.handleLavaMovement()) {
               this.heal(4.0F);
            }
         }
      }

      super.onLivingUpdate();
   }

   public void onUpdate() {
      super.onUpdate();
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth()) {
            target.setFire(6);
         }

         return result;
      } else {
         return result;
      }
   }

   protected String getLivingSound() {
      return null;
   }

   protected String getHurtSound() {
      return null;
   }

   protected String getDeathSound() {
      return null;
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
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

   public boolean isBurning() {
      return true;
   }

   public boolean healsWithTime() {
      return false;
   }

   public boolean isEntityBiologicallyAlive() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public boolean isComfortableInLava() {
      return true;
   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }

   public boolean canSpawnInShallowWater() {
      return false;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }
}
