package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISeekLitTorch;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityShadow extends EntityMob {
   public EntityShadow(World par1World) {
      super(par1World);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIRestrictSun(this));
      this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.tasks.addTask(3, new EntityAISeekLitTorch(this, 1.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(5.0);
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      return !damage_source.hasSilverAspect() && !damage_source.hasMagicAspect() && !damage_source.isSunlight();
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public void onLivingUpdate() {
      if (!this.worldObj.isRemote) {
         if (this.isInSunlight()) {
            this.attackEntityFrom(new Damage(DamageSource.sunlight, 1000.0F));
         } else if (this.ticksExisted % 40 == 0) {
            float brightness = this.getBrightness(1.0F);
            int amount_to_heal = (int)((0.4F - brightness) * 10.0F);
            if (amount_to_heal > 0) {
               this.heal((float)amount_to_heal);
            }
         }
      }

      this.tryDisableNearbyLightSource();
      super.onLivingUpdate();
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result == null) {
         return result;
      } else {
         if (result.entityWasNegativelyAffected() && target instanceof EntityPlayer) {
            EntityPlayer var10000 = target.getAsPlayer();
            var10000.vision_dimming += target.getAsEntityLivingBase().getAmountAfterResistance(2.0F, 4);
         }

         if (result.entityLostHealth() && target instanceof EntityLivingBase) {
            target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.weakness.id, 600, 0));
         }

         return result;
      }
   }

   protected String getLivingSound() {
      return "imported.mob.shadow.say";
   }

   protected String getHurtSound() {
      return "imported.mob.shadow.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.shadow.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
   }

   protected float getSoundVolume(String sound) {
      return 0.2F;
   }

   protected int getDropItemId() {
      return -1;
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

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }
}
