package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements EntityOwnable {
   protected EntityAISit aiSit = new EntityAISit(this);
   protected EntityLiving threatening_entity;
   private int threatening_entity_countdown;
   public int taming_cooldown;

   public EntityTameable(World par1World) {
      super(par1World);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, "");
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.getOwnerName() == null) {
         par1NBTTagCompound.setString("Owner", "");
      } else {
         par1NBTTagCompound.setString("Owner", this.getOwnerName());
      }

      par1NBTTagCompound.setBoolean("Sitting", this.isSitting());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      String var2 = par1NBTTagCompound.getString("Owner");
      if (var2.length() > 0) {
         this.setOwner(var2);
         this.setTamed(true);
      }

      this.aiSit.setSitting(par1NBTTagCompound.getBoolean("Sitting"));
      this.setSitting(par1NBTTagCompound.getBoolean("Sitting"));
   }

   protected void playTameEffect(boolean par1) {
      EnumParticle particle = par1 ? EnumParticle.heart : EnumParticle.smoke;

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.rand.nextGaussian() * 0.02;
         double var6 = this.rand.nextGaussian() * 0.02;
         double var8 = this.rand.nextGaussian() * 0.02;
         this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var4, var6, var8);
      }

   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.tame_success) {
         this.playTameEffect(true);
      } else if (par1 == EnumEntityState.tame_failure) {
         this.playTameEffect(false);
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public boolean isTamed() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
   }

   public void setTamed(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(var2 | 4));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var2 & -5));
      }

   }

   public boolean isSitting() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSitting(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(var2 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var2 & -2));
      }

   }

   public String getOwnerName() {
      return this.dataWatcher.getWatchableObjectString(17);
   }

   public void setOwner(String par1Str) {
      this.dataWatcher.updateObject(17, par1Str);
   }

   public EntityLivingBase func_130012_q() {
      return this.worldObj.getPlayerEntityByName(this.getOwnerName());
   }

   public EntityAISit func_70907_r() {
      return this.aiSit;
   }

   public boolean func_142018_a(EntityLivingBase par1EntityLivingBase, EntityLivingBase par2EntityLivingBase) {
      return true;
   }

   public Team getTeam() {
      if (this.isTamed()) {
         EntityLivingBase var1 = this.func_130012_q();
         if (var1 != null) {
            return var1.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isOnSameTeam(EntityLivingBase par1EntityLivingBase) {
      if (this.isTamed()) {
         EntityLivingBase var2 = this.func_130012_q();
         if (par1EntityLivingBase == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.isOnSameTeam(par1EntityLivingBase);
         }
      }

      return super.isOnSameTeam(par1EntityLivingBase);
   }

   public Entity getOwner() {
      return this.func_130012_q();
   }

   public boolean isAThreat(EntityLiving entity_living) {
      if (!entity_living.isEntityAlive()) {
         return false;
      } else if (!(entity_living instanceof IMob)) {
         return false;
      } else {
         if (entity_living instanceof EntityHorse) {
            EntityHorse horse = (EntityHorse)entity_living;
            if (horse.getHorseType() < 3) {
               return false;
            }
         } else if (entity_living instanceof EntityLivestock) {
            return false;
         }

         if (entity_living instanceof EntityWolf) {
            EntityWolf wolf = (EntityWolf)entity_living;
            if (wolf.getOwner() == this.getOwner()) {
               return false;
            }
         }

         Entity owner = this.getOwner();
         EntityLivingBase target = entity_living.getTarget();
         return (target == this || target == owner) && this.getEntitySenses().canSee(entity_living);
      }
   }

   public void warnOwner() {
   }

   public void callToOwner() {
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         if (this.isTamed() && (this.isSitting() || this.getAttackTarget() == null) && this.getTicksExistedWithOffset() % 10 == 0 && Math.random() < 0.25 && this.distanceToNearestPlayer() <= 16.0) {
            Entity owner = this.getOwner();
            List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(16.0, 8.0, 16.0));
            Iterator i = entities.iterator();

            while(i.hasNext()) {
               Entity entity = (Entity)i.next();
               if (entity instanceof EntityLiving && this.isAThreat((EntityLiving)entity)) {
                  this.warnOwner();
                  this.setThreateningEntity((EntityLiving)entity);
                  break;
               }
            }
         }

         if (this.threatening_entity_countdown > 0) {
            --this.threatening_entity_countdown;
         }

         if (this.threatening_entity_countdown == 0) {
            this.threatening_entity = null;
         }

         if (this.getThreateningEntity() != null) {
            this.getLookHelper().setLookPositionWithEntity(this.getThreateningEntity(), 10.0F, (float)this.getVerticalFaceSpeed());
         }
      }

   }

   private void setThreateningEntity(EntityLiving entity_living) {
      if (entity_living != null && !entity_living.isEntityAlive()) {
         entity_living = null;
      }

      this.threatening_entity = entity_living;
      if (entity_living != null) {
         this.threatening_entity_countdown = (this.rand.nextInt(10) + 5) * 10;
      }

   }

   public EntityLiving getThreateningEntity() {
      if (this.threatening_entity != null && !this.threatening_entity.isEntityAlive()) {
         this.threatening_entity = null;
      } else if (this.threatening_entity_countdown == 0 || this.getTarget() != null) {
         this.threatening_entity = null;
      }

      return this.threatening_entity;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.taming_cooldown > 0) {
         --this.taming_cooldown;
      }

   }

   protected abstract int getTamingOutcome(EntityPlayer var1);

   public void setAttackTarget(EntityLivingBase target) {
      if (target == null || target != this.getOwner()) {
         super.setAttackTarget(target);
      }
   }

   public void setTarget(EntityLivingBase target) {
      if (target != this.getOwner()) {
         super.setTarget(target);
      }
   }
}
