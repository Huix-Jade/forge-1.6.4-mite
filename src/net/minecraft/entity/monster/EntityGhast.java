package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
   public int courseChangeCooldown;
   public double waypointX;
   public double waypointY;
   public double waypointZ;
   private Entity targetedEntity;
   private int aggroCooldown;
   public int prevAttackCounter;
   public int attackCounter;
   private int explosionStrength = 1;

   public EntityGhast(World par1World) {
      super(par1World);
      this.setSize(4.0F, 4.0F);
   }

   public boolean func_110182_bF() {
      return this.dataWatcher.getWatchableObjectByte(16) != 0;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      if (damage.isFireballFromPlayer()) {
         damage.setAmount(1000.0F).setIgnoreSpecificImmunities();
      }

      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (result.entityWasDestroyed() && damage.isFireballFromPlayer()) {
            ((EntityPlayer)damage.getResponsibleEntity()).triggerAchievement(AchievementList.ghast);
         }

         return result;
      }
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0);
   }

   protected void updateEntityActionState() {
      if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0) {
         this.setDead();
      }

      this.tryDespawnEntity();
      this.prevAttackCounter = this.attackCounter;
      double var1 = this.waypointX - this.posX;
      double var3 = this.waypointY - this.posY;
      double var5 = this.waypointZ - this.posZ;
      double var7 = var1 * var1 + var3 * var3 + var5 * var5;
      if (var7 < 1.0 || var7 > 3600.0) {
         this.waypointX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.waypointY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.waypointZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
      }

      if (this.courseChangeCooldown-- <= 0) {
         this.courseChangeCooldown += this.rand.nextInt(5) + 2;
         var7 = (double)MathHelper.sqrt_double(var7);
         if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, var7)) {
            this.motionX += var1 / var7 * 0.1;
            this.motionY += var3 / var7 * 0.1;
            this.motionZ += var5 / var7 * 0.1;
         } else {
            this.waypointX = this.posX;
            this.waypointY = this.posY;
            this.waypointZ = this.posZ;
         }
      }

      if (this.targetedEntity != null && this.targetedEntity.isDead) {
         this.targetedEntity = null;
      }

      if (this.targetedEntity == null || this.aggroCooldown-- <= 0) {
         this.targetedEntity = this.getClosestVulnerablePlayer(100.0);
         if (this.targetedEntity != null) {
            this.aggroCooldown = 20;
         }
      }

      double var9 = 64.0;
      if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < var9 * var9) {
         Vec3 target_center = this.targetedEntity.getCenterPoint();
         this.renderYawOffset = this.rotationYaw = (float)MathHelper.getYawInDegrees(this.getCenterPoint(), target_center);
         if (this.canSeeEntity(this.targetedEntity)) {
            if (this.attackCounter == 10) {
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1007, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }

            ++this.attackCounter;
            if (this.attackCounter == 20) {
               double distance_sq = this.getCenterPoint().squareDistanceTo(target_center);
               float lead = (float)Math.pow(distance_sq, 0.44);
               lead *= 0.5F + this.rand.nextFloat();
               target_center.xCoord = this.targetedEntity.getPredictedPosX(lead);
               target_center.zCoord = this.targetedEntity.getPredictedPosZ(lead);
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
               EntityLargeFireball var17 = new EntityLargeFireball(this.worldObj, this, target_center, 4.0F);
               var17.field_92057_e = this.explosionStrength;
               this.worldObj.spawnEntityInWorld(var17);
               this.attackCounter = -40;
            }
         } else if (this.attackCounter > 0) {
            --this.attackCounter;
         }
      } else {
         this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / 3.1415927F;
         if (this.attackCounter > 0) {
            --this.attackCounter;
         }
      }

      if (!this.worldObj.isRemote) {
         byte var21 = this.dataWatcher.getWatchableObjectByte(16);
         byte var12 = (byte)(this.attackCounter > 10 ? 1 : 0);
         if (var21 != var12) {
            this.dataWatcher.updateObject(16, var12);
         }
      }

   }

   private boolean isCourseTraversable(double par1, double par3, double par5, double par7) {
      double var9 = (this.waypointX - this.posX) / par7;
      double var11 = (this.waypointY - this.posY) / par7;
      double var13 = (this.waypointZ - this.posZ) / par7;
      AxisAlignedBB var15 = this.boundingBox.copy();

      for(int var16 = 1; (double)var16 < par7; ++var16) {
         var15.offset(var9, var11, var13);
         if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   protected String getLivingSound() {
      return "mob.ghast.moan";
   }

   protected String getHurtSound() {
      return "mob.ghast.scream";
   }

   protected String getDeathSound() {
      return "mob.ghast.death";
   }

   protected int getDropItemId() {
      return Item.gunpowder.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(2);
      if (damage_source.isFireballFromPlayer() && num_drops < 1) {
         num_drops = 1;
      }

      int i;
      for(i = 0; i < num_drops; ++i) {
         this.dropItem(Item.ghastTear.itemID, 1);
      }

      num_drops = this.rand.nextInt(3);

      for(i = 0; i < num_drops; ++i) {
         this.dropItem(Item.gunpowder.itemID, 1);
      }

   }

   protected float getSoundVolume(String sound) {
      return 10.0F;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return this.rand.nextInt(20) == 0 && super.getCanSpawnHere(perform_light_check) && this.worldObj.difficultySetting > 0;
   }

   public int getMaxSpawnedInChunk() {
      return 1;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("ExplosionPower", this.explosionStrength);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("ExplosionPower")) {
         this.explosionStrength = par1NBTTagCompound.getInteger("ExplosionPower");
      }

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

   public boolean canSpawnInShallowWater() {
      return false;
   }
}
