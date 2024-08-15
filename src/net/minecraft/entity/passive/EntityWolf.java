package net.minecraft.entity.passive;

import net.minecraft.block.BlockColored;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFleeAttackerOrPanic;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIGetOutOfWater;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetIfHostileToPlayers;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMeat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.Curse;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWolf extends EntityTameable {
   private float field_70926_e;
   private float field_70924_f;
   private boolean isShaking;
   private boolean field_70928_h;
   private float timeWolfIsShaking;
   private float prevTimeWolfIsShaking;
   protected int data_object_id_is_attacking;
   protected int data_object_id_hostile_to_players;
   private int target_countdown;
   public boolean is_witch_ally;

   public EntityWolf(World par1World) {
      super(par1World);
      this.setSize(0.70000005F, 0.8F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit);
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, true));
      this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0F, 2.0F));
      this.tasks.addTask(6, new EntityAIMate(this, 1.0));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityChicken.class, 200, false));
      this.targetTasks.addTask(5, new EntityAITargetNonTamed(this, EntitySheep.class, 400, false));
      this.targetTasks.addTask(6, new EntityAITargetNonTamed(this, EntityPig.class, 600, false));
      this.targetTasks.addTask(7, new EntityAITargetNonTamed(this, EntityCow.class, 800, false));
      this.targetTasks.addTask(5, new EntityAITargetNonTamed(this, EntityZombie.class, 3200, true));
      this.targetTasks.addTask(5, new EntityAITargetNonTamed(this, EntityGhoul.class, 3200, true));
      this.targetTasks.addTask(3, new EntityAITargetIfHostileToPlayers(this, EntityPlayer.class, 0, true));
      this.tasks.addTask(1, new EntityAIFleeAttackerOrPanic(this, 1.0F, 0.0F, true));
      this.tasks.addTask(4, new EntityAIGetOutOfWater(this, 1.0F));
      this.setTamed(false);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.4000000059604645);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, this.isTamed() ? 12.0 : 8.0);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 3.0);
      this.setEntityAttribute(SharedMonsterAttributes.followRange, this.isTamed() ? 32.0 : 16.0);
   }

   protected void updateAITick() {
      this.dataWatcher.updateObject(18, this.getHealth());
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, new Float(this.getHealth()));
      this.dataWatcher.addObject(19, new Byte((byte)0));
      this.dataWatcher.addObject(20, new Byte((byte)BlockColored.getBlockFromDye(1)));
      this.data_object_id_is_attacking = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
      this.data_object_id_hostile_to_players = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
   }

   protected void setTargetCountdown(int value) {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("setTargetCountdown: only meant to be called on server");
      } else {
         this.target_countdown = MathHelper.clamp_int(value, 0, 800);
      }

   }

   protected int getTargetCountdown() {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("getTargetCountdown: only meant to be called on server");
      }

      return this.target_countdown;
   }

   protected void setHostileToPlayers(boolean value) {
      if (!this.worldObj.isRemote) {
         this.dataWatcher.updateObject(this.data_object_id_hostile_to_players, (byte)(value ? -1 : 0));
      }

   }

   public boolean isHostileToPlayers() {
      return this.dataWatcher.getWatchableObjectByte(this.data_object_id_hostile_to_players) != 0;
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.wolf.step", 0.15F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setByte("CollarColor", (byte)this.getCollarColor());
      par1NBTTagCompound.setInteger("target_countdown", this.getTargetCountdown());
      par1NBTTagCompound.setBoolean("hostile_to_players", this.isHostileToPlayers());
      par1NBTTagCompound.setBoolean("is_witch_ally", this.is_witch_ally);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("CollarColor")) {
         this.setCollarColor(par1NBTTagCompound.getByte("CollarColor"));
      }

      this.setTargetCountdown(par1NBTTagCompound.getInteger("target_countdown"));
      this.setHostileToPlayers(par1NBTTagCompound.getBoolean("hostile_to_players"));
      this.is_witch_ally = par1NBTTagCompound.getBoolean("is_witch_ally");
   }

   protected String getLivingSound() {
      if (this.looksAngry()) {
         return "mob.wolf.growl";
      } else if (this.fleeing) {
         return "mob.wolf.whine";
      } else if (this.isTamed()) {
         return this.getHealthFraction() < 0.5F ? "mob.wolf.whine" : "mob.wolf.panting";
      } else {
         return "mob.wolf.panting";
      }
   }

   protected String getLongDistanceLivingSound() {
      return !this.worldObj.isDaytime() && !this.isAttacking() && !this.fleeing && this.rand.nextFloat() < 0.04F && this.isOutdoors() ? "imported.mob.wolf.howl" : null;
   }

   protected String getHurtSound() {
      return "mob.wolf.hurt";
   }

   protected String getDeathSound() {
      return "mob.wolf.death";
   }

   protected float getSoundVolume(String sound) {
      return this.isChild() ? 0.2F : 0.4F;
   }

   protected int getDropItemId() {
      return -1;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      this.dropItem(Item.leather.itemID, 1);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.worldObj.isRemote && this.isShaking && !this.field_70928_h && !this.hasPath() && this.onGround) {
         this.field_70928_h = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
         this.worldObj.setEntityState(this, EnumEntityState.wolf_shaking);
      }

      if (!this.worldObj.isRemote && this.is_witch_ally) {
         this.setHostileToPlayers(true);
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         int target_countdown = this.getTargetCountdown();
         if (target_countdown > 0) {
            --target_countdown;
            this.setTargetCountdown(target_countdown);
         }

         if (target_countdown == 0) {
            if (this.getAttackTarget() != null) {
               this.setAttackTarget((EntityLivingBase)null);
            }

            this.setHostileToPlayers(false);
         }

         if (this.getAttackTarget() == null) {
            if (this.isAttacking()) {
               this.setIsAttacking(false);
            }
         } else if (!this.isAttacking()) {
            this.setIsAttacking(true);
         }

         if (!this.isTamed() && this.worldObj.isBloodMoon(true)) {
            this.setHostileToPlayers(true);
         }
      }

      this.field_70924_f = this.field_70926_e;
      if (this.func_70922_bv()) {
         this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
      } else {
         this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
      }

      if (this.func_70922_bv()) {
         this.numTicksToChaseTarget = 10;
      }

      if (this.isWet()) {
         this.isShaking = true;
         this.field_70928_h = false;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else if ((this.isShaking || this.field_70928_h) && this.field_70928_h) {
         if (this.timeWolfIsShaking == 0.0F) {
            this.playSound("mob.wolf.shake", this.getSoundVolume("mob.wolf.shake"), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
         this.timeWolfIsShaking += 0.05F;
         if (this.prevTimeWolfIsShaking >= 2.0F) {
            this.isShaking = false;
            this.field_70928_h = false;
            this.prevTimeWolfIsShaking = 0.0F;
            this.timeWolfIsShaking = 0.0F;
         }

         if (this.timeWolfIsShaking > 0.4F) {
            float var1 = (float)this.boundingBox.minY;
            int var2 = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * 3.1415927F) * 7.0F);

            for(int var3 = 0; var3 < var2; ++var3) {
               float var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               float var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               this.worldObj.spawnParticle(EnumParticle.splash, this.posX + (double)var4, (double)(var1 + 0.8F), this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

   }

   public boolean getWolfShaking() {
      return this.isShaking;
   }

   public float getShadingWhileShaking(float par1) {
      return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * par1) / 2.0F * 0.25F;
   }

   public float getShakeAngle(float par1, float par2) {
      float var3 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * par1 + par2) / 1.8F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      } else if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return MathHelper.sin(var3 * 3.1415927F) * MathHelper.sin(var3 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
   }

   public float getInterestedAngle(float par1) {
      return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * par1) * 0.15F * 3.1415927F;
   }

   public float getEyeHeight() {
      return this.height * 0.8F;
   }

   public int getVerticalFaceSpeed() {
      return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      Entity var3 = damage.getImmediateEntity();
      if (var3 != null && !var3.isEntityPlayer() && !var3.isArrow()) {
         damage.setAmount((damage.getAmount() + 1.0F) / 2.0F);
      }

      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (result.entityWasNegativelyAffected()) {
            this.aiSit.setSitting(false);
         }

         return result;
      }
   }

   public void startTargetCountdown() {
      this.setTargetCountdown(800 + this.rand.nextInt(100) - this.rand.nextInt(100));
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = target.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this), (float)this.getEntityAttributeValue(SharedMonsterAttributes.attackDamage)));
      if (result == null) {
         return result;
      } else {
         if (result.entityWasNegativelyAffected()) {
            if (result.entityWasDestroyed()) {
               this.setAttackTarget((EntityLivingBase)null);
            } else {
               this.startTargetCountdown();
            }
         }

         return result;
      }
   }

   public void setAttackTarget(EntityLivingBase par1EntityLivingBase) {
      if (par1EntityLivingBase == null || par1EntityLivingBase != this.getOwner()) {
         super.setAttackTarget(par1EntityLivingBase);
         if (this.getAttackTarget() != null) {
            this.startTargetCountdown();
            if (this.getAttackTarget() instanceof EntityPlayer && !this.isTamed()) {
               this.setHostileToPlayers(true);
            }
         }

      }
   }

   public void setTamed(boolean par1) {
      super.setTamed(par1);
      float max_health_before = (float)this.getEntityAttributeValue(SharedMonsterAttributes.maxHealth);
      this.applyEntityAttributes();
      float max_health_after = (float)this.getEntityAttributeValue(SharedMonsterAttributes.maxHealth);
      if (max_health_after > max_health_before) {
         this.setHealth(this.getHealth() + max_health_after - max_health_before);
      } else if (this.getHealth() > max_health_after) {
         this.setHealth(max_health_after);
      }

   }

   protected int getTamingOutcome(EntityPlayer player) {
      float roll = this.rand.nextFloat();
      if (roll < 0.05F) {
         return -1;
      } else if (roll < 0.1F) {
         return 0;
      } else if (roll > 0.9F) {
         return 1;
      } else {
         roll += this.rand.nextFloat() * (float)player.getExperienceLevel() * 0.02F;
         return roll < 0.2F ? -1 : (roll < 0.8F ? 0 : 1);
      }
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (!this.looksAngry() && !this.isHostileToPlayers()) {
         if (item_stack != null) {
            Item item = item_stack.getItem();
            int taming_outcome;
            if (this.isTamed()) {
               if (item == Item.dyePowder) {
                  taming_outcome = BlockColored.getBlockFromDye(item_stack.getItemSubtype());
                  if (taming_outcome != this.getCollarColor()) {
                     if (player.onServer()) {
                        this.setCollarColor(taming_outcome);
                        if (!player.inCreativeMode()) {
                           player.convertOneOfHeldItem((ItemStack)null);
                        }
                     }

                     return true;
                  }
               } else if (this.willEat(item_stack) && (double)(this.getHealth() + 1.0F) < this.getEntityAttributeValue(SharedMonsterAttributes.maxHealth)) {
                  if (player.onServer()) {
                     this.heal((float)item_stack.getNutrition());
                     this.makeSound("mob.wolf.bark");
                     if (!player.inCreativeMode()) {
                        player.convertOneOfHeldItem((ItemStack)null);
                     }
                  }

                  return true;
               }
            } else if (item == Item.bone && !this.isAttacking()) {
               if (player.onServer() && this.taming_cooldown == 0) {
                  taming_outcome = this.getTamingOutcome(player);
                  if (taming_outcome <= 0) {
                     this.playTameEffect(false);
                     this.worldObj.setEntityState(this, EnumEntityState.tame_failure);
                     this.taming_cooldown = 100;
                     if (taming_outcome < 0 && !this.worldObj.isBlueMoonNight()) {
                        this.setAttackTarget(player);
                     }
                  } else {
                     this.setTamed(true);
                     this.setPathToEntity((PathEntity)null);
                     this.setAttackTarget((EntityLivingBase)null);
                     this.aiSit.setSitting(true);
                     this.setOwner(player.getCommandSenderName());
                     this.playTameEffect(true);
                     this.worldObj.setEntityState(this, EnumEntityState.tame_success);
                     this.makeSound(this.rand.nextInt(2) == 0 ? "mob.wolf.bark" : "mob.wolf.panting");
                  }

                  if (!player.inCreativeMode()) {
                     player.convertOneOfHeldItem((ItemStack)null);
                  }
               }

               return true;
            }
         }

         if (super.onEntityRightClicked(player, item_stack)) {
            return true;
         } else if (player.ownsEntity(this)) {
            if (player.onClient()) {
               if (player.isLocalClient()) {
                  Minecraft.getClientPlayerController().setUseButtonDelayOverride(600);
               }
            } else {
               if (Math.random() < 0.5) {
                  this.makeSound(this.isSitting() ? "mob.wolf.bark" : "mob.wolf.panting");
               }

               this.aiSit.setSitting(!this.isSitting());
               this.isJumping = false;
               this.setPathToEntity((PathEntity)null);
               this.setTarget((EntityLivingBase)null);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.wolf_shaking) {
         this.field_70928_h = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public float getTailRotation() {
      return this.looksAngry() ? 1.5393804F : (this.isTamed() ? (0.55F - (20.0F - this.dataWatcher.getWatchableObjectFloat(18)) * 0.02F) * 3.1415927F : 0.62831855F);
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() instanceof ItemMeat;
   }

   public int getMaxSpawnedInChunk() {
      return 8;
   }

   public boolean isAttacking() {
      return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_attacking) != 0;
   }

   public void setIsAttacking(boolean is_attacking) {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("setIsAttacking: only meant to be called on server");
      } else if (is_attacking != this.isAttacking()) {
         this.dataWatcher.updateObject(this.data_object_id_is_attacking, (byte)(is_attacking ? -1 : 0));
         if (is_attacking) {
            this.makeSound("mob.wolf.growl");
         }

      }
   }

   public int getCollarColor() {
      return this.dataWatcher.getWatchableObjectByte(20) & 15;
   }

   public void setCollarColor(int par1) {
      this.dataWatcher.updateObject(20, (byte)(par1 & 15));
   }

   public EntityWolf spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
      EntityWolf var2;
      try {
         var2 = (EntityWolf)this.getClass().getConstructor(World.class).newInstance(this.worldObj);
      } catch (Exception var4) {
         Exception e = var4;
         e.printStackTrace();
         return null;
      }

      String var3 = this.getOwnerName();
      if (var3 != null && var3.trim().length() > 0) {
         var2.setOwner(var3);
         var2.setTamed(true);
      }

      return var2;
   }

   public void func_70918_i(boolean par1) {
      if (par1) {
         this.dataWatcher.updateObject(19, (byte)1);
      } else {
         this.dataWatcher.updateObject(19, (byte)0);
      }

   }

   public boolean canMateWith(EntityAnimal par1EntityAnimal) {
      if (par1EntityAnimal == this) {
         return false;
      } else if (par1EntityAnimal.getClass() != this.getClass()) {
         return false;
      } else {
         EntityWolf var2 = (EntityWolf)par1EntityAnimal;
         return var2.isSitting() ? false : this.isInLove() && var2.isInLove();
      }
   }

   public boolean func_70922_bv() {
      return this.dataWatcher.getWatchableObjectByte(19) == 1;
   }

   protected boolean canDespawn() {
      return !this.is_witch_ally && (this.ticksExisted > 2400 || this instanceof IMob) && !this.isTamed() && super.canDespawn();
   }

   public boolean func_142018_a(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase) {
      if (!(par1EntityLivingBase instanceof EntityCreeper) && !(par1EntityLivingBase instanceof EntityGhast)) {
         if (par1EntityLivingBase instanceof EntityWolf) {
            EntityWolf var3 = (EntityWolf)par1EntityLivingBase;
            if (var3.isTamed() && var3.func_130012_q() == par2EntityLivingBase) {
               return false;
            }
         }

         return par1EntityLivingBase instanceof EntityPlayer && par2EntityLivingBase instanceof EntityPlayer && !((EntityPlayer)par2EntityLivingBase).canAttackPlayer((EntityPlayer)par1EntityLivingBase) ? false : !(par1EntityLivingBase instanceof EntityHorse) || !((EntityHorse)par1EntityLivingBase).isTame();
      } else {
         return false;
      }
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.spawnBabyAnimal(par1EntityAgeable);
   }

   public boolean preysUpon(Entity entity) {
      return !this.isChild() && !this.isTamed() && entity instanceof EntityAnimal;
   }

   public void onFleeing() {
      this.setHostileToPlayers(false);
      this.setAttackTarget((EntityLivingBase)null);
   }

   public boolean looksAngry() {
      return this.isAttacking() && !this.isTamed() || this.isHostileToPlayers();
   }

   public void warnOwner() {
      this.makeSound(this.rand.nextInt(8) == 0 ? "mob.wolf.growl" : "mob.wolf.bark");
   }

   public void callToOwner() {
      this.makeLongDistanceSound("mob.wolf.bark");
   }

   public boolean drawBackFaces() {
      return false;
   }

   public void setWitchAlly() {
      this.is_witch_ally = true;
   }

   public boolean canBeAttackedBy(EntityLivingBase attacker) {
      return this.rand.nextInt(4) > 0 && attacker.hasCurse(Curse.fear_of_wolves, true) ? false : super.canBeAttackedBy(attacker);
   }
}
