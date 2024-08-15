package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {
   private int homeCheckTimer;
   Village villageObj;
   private int attackTimer;
   private int holdRoseTick;

   public EntityIronGolem(World par1World) {
      super(par1World);
      this.setSize(1.4F, 2.9F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0, true));
      this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32.0F));
      this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(5, new EntityAILookAtVillager(this));
      this.tasks.addTask(6, new EntityAIWander(this, 0.6));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, IMob.mobSelector));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   public boolean isAIEnabled() {
      return true;
   }

   protected void updateAITick() {
      if (--this.homeCheckTimer <= 0) {
         this.homeCheckTimer = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
         if (this.villageObj == null) {
            this.detachHome();
         } else {
            ChunkCoordinates var1 = this.villageObj.getCenter();
            this.setHomeArea(var1.posX, var1.posY, var1.posZ, (int)((float)this.villageObj.getVillageRadius() * 0.6F));
         }
      }

      super.updateAITick();
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(100.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25);
   }

   protected int decreaseAirSupply(int par1) {
      return par1;
   }

   protected void collideWithEntity(Entity par1Entity) {
      if (par1Entity instanceof IMob && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((EntityLivingBase)par1Entity);
      }

      super.collideWithEntity(par1Entity);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.attackTimer > 0) {
         --this.attackTimer;
      }

      if (this.holdRoseTick > 0) {
         --this.holdRoseTick;
      }

      if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7 && this.rand.nextInt(5) == 0) {
         int var1 = MathHelper.floor_double(this.posX);
         int var2 = MathHelper.floor_double(this.posY - 0.20000000298023224 - (double)this.yOffset);
         int var3 = MathHelper.floor_double(this.posZ);
         int var4 = this.worldObj.getBlockId(var1, var2, var3);
         if (var4 > 0) {
            this.worldObj.spawnParticleEx(EnumParticle.tilecrack, var4, this.worldObj.getBlockMetadata(var1, var2, var3), this.posX + ((double)this.rand.nextFloat() - 0.5) * (double)this.width, this.boundingBox.minY + 0.1, this.posZ + ((double)this.rand.nextFloat() - 0.5) * (double)this.width, 4.0 * ((double)this.rand.nextFloat() - 0.5), 0.5, ((double)this.rand.nextFloat() - 0.5) * 4.0);
         }
      }

   }

   public boolean canAttackClass(Class par1Class) {
      return this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(par1Class) ? false : super.canAttackClass(par1Class);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("PlayerCreated", this.isPlayerCreated());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setPlayerCreated(par1NBTTagCompound.getBoolean("PlayerCreated"));
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      this.attackTimer = 10;
      this.worldObj.setEntityState(this, EnumEntityState.golem_throw);
      EntityDamageResult result = target.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this), (float)(7 + this.rand.nextInt(15))));
      if (result == null) {
         return result;
      } else {
         if (result.entityWasKnockedBack()) {
            target.motionY += 0.4000000059604645;
         }

         this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
         return result;
      }
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.golem_throw) {
         this.attackTimer = 10;
         this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
      } else if (par1 == EnumEntityState.golem_holding_rose) {
         this.holdRoseTick = 400;
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public Village getVillage() {
      return this.villageObj;
   }

   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean par1) {
      this.holdRoseTick = par1 ? 400 : 0;
      this.worldObj.setEntityState(this, EnumEntityState.golem_holding_rose);
   }

   protected String getLivingSound() {
      return null;
   }

   protected String getHurtSound() {
      return "mob.irongolem.hit";
   }

   protected String getDeathSound() {
      return "mob.irongolem.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(3);

      int i;
      for(i = 0; i < num_drops; ++i) {
         this.dropItem(Block.plantRed.blockID, 1);
      }

      if (recently_hit_by_player && !this.has_taken_massive_fall_damage) {
         num_drops = 3 + this.rand.nextInt(3 + damage_source.getLootingModifier());

         for(i = 0; i < num_drops; ++i) {
            this.dropItem(Item.ironNugget.itemID, 1);
         }
      }

   }

   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setPlayerCreated(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(var2 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var2 & -2));
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      if (!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
         this.villageObj.setReputationForPlayer(this.attackingPlayer.getCommandSenderName(), -5);
      }

      super.onDeath(par1DamageSource);
   }

   public boolean canCatchFire() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 4;
   }

   public float getReach() {
      return super.getReach() + 0.5F;
   }
}
