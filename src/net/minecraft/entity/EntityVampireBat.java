package net.minecraft.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.world.World;

public class EntityVampireBat extends EntityBat {
   private int attack_cooldown;
   private int feed_cooldown;

   public EntityVampireBat(World world) {
      super(world);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 1.0 * (double)this.getScaleFactor());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.feed_cooldown = par1NBTTagCompound.getShort("feed_cooldown");
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.feed_cooldown > 0) {
         par1NBTTagCompound.setShort("feed_cooldown", (short)this.feed_cooldown);
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (this.attack_cooldown > 0) {
         --this.attack_cooldown;
      }

      if (this.feed_cooldown > 0) {
         if (this.getHealth() < this.getMaxHealth()) {
            this.feed_cooldown = 0;
         } else if (--this.feed_cooldown > 0) {
            Entity target = this.getAttackTarget();
            if (target != null && !this.preysUpon(target)) {
               this.setAttackTarget((EntityLivingBase)null);
            }
         }
      }

      if (this.getTicksExistedWithOffset() % 20 == 0) {
         this.setAttackTarget(this.worldObj.getClosestPrey(this, 32.0, true, false));
      }

   }

   public final boolean preysUpon(Entity entity) {
      if (this.feed_cooldown > 0) {
         return entity.isEntityPlayer() && !entity.getAsPlayer().inCreativeMode();
      } else {
         return entity.isEntityPlayer() && !entity.getAsPlayer().inCreativeMode() || entity.isTrueAnimal() || entity instanceof EntityVillager;
      }
   }

   protected void collideWithEntity(Entity entity) {
      super.collideWithEntity(entity);
      if (this.attack_cooldown <= 0 && entity == this.getAttackTarget()) {
         if (this.boundingBox.copy().scaleXZ(0.5).intersectsWith(entity.boundingBox)) {
            EntityDamageResult result = EntityMob.attackEntityAsMob(this, entity);
            if (result != null && result.entityLostHealth()) {
               this.heal(result.getAmountOfHealthLost(), EnumEntityFX.vampiric_gain);
               if (entity instanceof EntityOcelot) {
                  EntityOcelot ocelot = (EntityOcelot)entity;
                  if (ocelot.getHealth() > 0.0F && ocelot.getTarget() == null) {
                     ocelot.setTarget(this);
                  }
               }

               if (!(this instanceof EntityGiantVampireBat) && this.getHealth() >= this.getMaxHealth()) {
                  this.feed_cooldown = 1200;
               }
            }
         }

         this.attack_cooldown = 20;
      }

   }

   public int getMaxSpawnedInChunk() {
      return 8;
   }
}
