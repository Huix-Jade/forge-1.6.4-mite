package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISeekLitTorch;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityInvisibleStalker extends EntityMob {
   public EntityInvisibleStalker(World par1World) {
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
      this.tasks.addTask(4, new EntityAISeekLitTorch(this, 1.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0);
   }

   protected void entityInit() {
      super.entityInit();
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public void onLivingUpdate() {
      this.tryDisableNearbyLightSource();
      super.onLivingUpdate();
   }

   protected String getLivingSound() {
      return "imported.mob.invisiblestalker.say";
   }

   protected String getHurtSound() {
      return "imported.mob.invisiblestalker.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.invisiblestalker.death";
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
      return EnumCreatureAttribute.UNDEFINED;
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
