package net.minecraft.entity;

import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToFoodItem;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMeat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityGhoul extends EntityAnimalWatcher {
   public EntityGhoul(World par1World) {
      super(par1World);
      this.getNavigator().setBreakDoors(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIBreakDoor(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 0.8));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
      this.tasks.addTask(1, new EntityAIMoveToFoodItem(this, 1.0F, true));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityAnimal.class, 1.0, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 10, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.2800000011920929);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(5.0);
   }

   protected void entityInit() {
      super.entityInit();
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && target instanceof EntityLivingBase) {
            target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 50, 5));
         }

         return result;
      } else {
         return result;
      }
   }

   protected String getLivingSound() {
      return "imported.mob.ghoul.say";
   }

   protected String getHurtSound() {
      return "imported.mob.ghoul.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.ghoul.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.zombie.step", 0.15F, 1.0F);
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

   public boolean preysUpon(Entity entity) {
      return entity instanceof EntityAnimal;
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() instanceof ItemMeat;
   }

   public boolean drawBackFaces() {
      return this.isWearingItems(true);
   }

   public int getCooloffForBlock() {
      return super.getCooloffForBlock() / 2;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 2;
   }
}
