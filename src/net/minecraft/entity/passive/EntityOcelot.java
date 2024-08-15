package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOcelot extends EntityTameable {
   private EntityAITempt aiTempt;

   public EntityOcelot(World par1World) {
      super(par1World);
      this.setSize(0.6F, 0.8F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(3, this.aiTempt = new EntityAITempt(this, 0.6, Item.fishRaw.itemID, true));
      this.tasks.addTask(3, this.aiTempt = new EntityAITempt(this, 0.6, Item.fishLargeRaw.itemID, true));
      this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityPlayer.class, 16.0F, 0.8, 1.33));
      this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0F, 5.0F));
      this.tasks.addTask(6, new EntityAIOcelotSit(this, 1.33));
      this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
      this.tasks.addTask(8, new EntityAIOcelotAttack(this));
      this.tasks.addTask(9, new EntityAIMate(this, 0.8));
      this.tasks.addTask(10, new EntityAIWander(this, 0.8));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.targetTasks.addTask(1, new EntityAITargetNonTamed(this, EntityChicken.class, 750, false));
      this.targetTasks.addTask(1, new EntityAITargetNonTamed(this, EntityBat.class, 750, false));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, (byte)0);
   }

   public void updateAITick() {
      if (this.getMoveHelper().isUpdating()) {
         double var1 = this.getMoveHelper().getSpeed();
         if (var1 == 0.6) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if (var1 == 1.33) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }

   }

   protected boolean canDespawn() {
      return !this.isTamed() && this.ticksExisted > 2400 ? super.canDespawn() : false;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.30000001192092896);
   }

   protected void fall(float par1) {
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("CatType", this.getTameSkin());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setTameSkin(par1NBTTagCompound.getInteger("CatType"));
   }

   protected String getLivingSound() {
      return this.isTamed() ? (this.isInLove() ? "mob.cat.purr" : (this.rand.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : null;
   }

   protected String getHurtSound() {
      return "mob.cat.hitt";
   }

   protected String getDeathSound() {
      return "mob.cat.hitt";
   }

   protected float getSoundVolume(String sound) {
      return 0.4F;
   }

   protected int getDropItemId() {
      return Item.leather.itemID;
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      return target.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this), 3.0F));
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (this.aiSit.isSitting()) {
            this.aiSit.setSitting(false);
            result.setEntityWasAffected();
         }

         return result;
      }
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
   }

   protected int getTamingOutcome(EntityPlayer player) {
      return this.rand.nextInt(3) == 0 ? 1 : 0;
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (this.isTamed()) {
         if (super.onEntityRightClicked(player, item_stack)) {
            return true;
         } else if (player.ownsEntity(this)) {
            if (player.onServer()) {
               this.aiSit.setSitting(!this.isSitting());
            }

            return true;
         } else {
            return false;
         }
      } else if (this.aiTempt.isRunning() && this.isFoodItem(item_stack) && player.getDistanceSqToEntity(this) < 9.0) {
         if (player.onServer() && this.taming_cooldown == 0) {
            int taming_outcome = this.getTamingOutcome(player);
            if (taming_outcome <= 0) {
               this.playTameEffect(false);
               this.worldObj.setEntityState(this, EnumEntityState.tame_failure);
               this.taming_cooldown = 400;
            } else {
               this.setTamed(true);
               this.setTameSkin(1 + this.rand.nextInt(3));
               this.setOwner(player.getCommandSenderName());
               this.playTameEffect(true);
               this.aiSit.setSitting(true);
               this.worldObj.setEntityState(this, EnumEntityState.tame_success);
            }

            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }

         return true;
      } else {
         return super.onEntityRightClicked(player, item_stack);
      }
   }

   public EntityOcelot spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
      EntityOcelot var2 = new EntityOcelot(this.worldObj);
      if (this.isTamed()) {
         var2.setOwner(this.getOwnerName());
         var2.setTamed(true);
         var2.setTameSkin(this.getTameSkin());
      }

      return var2;
   }

   public boolean isFoodItem(ItemStack item_stack) {
      if (item_stack != null && item_stack.getItem() instanceof ItemFood) {
         ItemFood food = (ItemFood)item_stack.getItem();
         return food == Item.fishRaw || food == Item.fishLargeRaw;
      } else {
         return false;
      }
   }

   public boolean canMateWith(EntityAnimal par1EntityAnimal) {
      if (par1EntityAnimal == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(par1EntityAnimal instanceof EntityOcelot)) {
         return false;
      } else {
         EntityOcelot var2 = (EntityOcelot)par1EntityAnimal;
         return !var2.isTamed() ? false : this.isInLove() && var2.isInLove();
      }
   }

   public int getTameSkin() {
      return this.dataWatcher.getWatchableObjectByte(18);
   }

   public void setTameSkin(int par1) {
      this.dataWatcher.updateObject(18, (byte)par1);
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      if (this.worldObj.rand.nextInt(3) == 0) {
         return false;
      } else {
         if (this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox)) {
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.boundingBox.minY);
            int var3 = MathHelper.floor_double(this.posZ);
            if (var2 < 63) {
               return false;
            }

            int var4 = this.worldObj.getBlockId(var1, var2 - 1, var3);
            if (var4 == Block.grass.blockID || var4 == Block.leaves.blockID) {
               return true;
            }
         }

         return false;
      }
   }

   public String getEntityName() {
      return this.hasCustomNameTag() ? this.getCustomNameTag() : (this.isTamed() ? "entity.Cat.name" : super.getEntityName());
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
      if (this.worldObj.rand.nextInt(7) == 0) {
         for(int var2 = 0; var2 < 2; ++var2) {
            EntityOcelot var3 = new EntityOcelot(this.worldObj);
            var3.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            var3.setGrowingAgeToNewborn();
            this.worldObj.spawnEntityInWorld(var3);
         }
      }

      return par1EntityLivingData;
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.spawnBabyAnimal(par1EntityAgeable);
   }
}
