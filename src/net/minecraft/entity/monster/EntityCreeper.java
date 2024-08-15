package net.minecraft.entity.monster;

import net.minecraft.block.BlockCactus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Curse;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob
{
   /**
    * Time when this creeper was last in an active state (Messed up code here, probably causes creeper animation to go
    * weird)
    */
   private int lastActiveTime;

   /**
    * The amount of time since the creeper was close enough to the player to ignite
    */
   private int timeSinceIgnited;
   private int fuseTime = 30;

   /** Explosion radius for this creeper. */
   protected float explosionRadius = 1.0F;
   private boolean has_exploded;
   public int recently_took_damage_from_conspicuous_cactus;

   public EntityCreeper(World par1World)
   {
      super(par1World);
      this.setSize(this.width * getScale(), this.height * getScale());
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAICreeperSwell(this));
      this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, false));
      this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
   }

   protected void applyEntityAttributes()
   {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
   }

   /**
    * Returns true if the newer Entity AI code should be run
    */
   public boolean isAIEnabled()
   {
      return true;
   }

   /**
    * The number of iterations PathFinder.getSafePoint will execute before giving up.
    */
   public int getMaxSafePointTries()
   {
      return this.getAttackTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   /**
    * Called when the mob is falling. Calculates and applies fall damage.
    */
   protected void fall(float par1)
   {
      super.fall(par1);
      this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + par1 * 1.5F);

      if (this.timeSinceIgnited > this.fuseTime - 5)
      {
         this.timeSinceIgnited = this.fuseTime - 5;
      }
   }

   protected void entityInit()
   {
      super.entityInit();
      this.dataWatcher.addObject(16, Byte.valueOf((byte) - 1));
      this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
   }

   /**
    * (abstract) Protected helper method to write subclass entity data to NBT.
    */
   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
   {
      super.writeEntityToNBT(par1NBTTagCompound);

      if (this.dataWatcher.getWatchableObjectByte(17) == 1)
      {
         par1NBTTagCompound.setBoolean("powered", true);
      }

      par1NBTTagCompound.setShort("Fuse", (short)this.fuseTime);
      par1NBTTagCompound.setFloat("ExplosionRadius", this.explosionRadius);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
   {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.dataWatcher.updateObject(17, Byte.valueOf((byte)(par1NBTTagCompound.getBoolean("powered") ? 1 : 0)));

      if (par1NBTTagCompound.hasKey("Fuse"))
      {
         this.fuseTime = par1NBTTagCompound.getShort("Fuse");
      }

      if (par1NBTTagCompound.hasKey("ExplosionRadius"))
      {
         this.explosionRadius = par1NBTTagCompound.getFloat("ExplosionRadius");
      }
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void onUpdate()
   {
      if (this.recently_took_damage_from_conspicuous_cactus > 0)
      {
         --this.recently_took_damage_from_conspicuous_cactus;
      }

      if (this.isEntityAlive())
      {
         this.lastActiveTime = this.timeSinceIgnited;
         int var1 = this.getCreeperState();

         if (var1 > 0 && this.timeSinceIgnited == 0)
         {
            this.playSound("random.fuse", 1.0F, 0.5F);
         }

         this.timeSinceIgnited += var1;

         if (this.timeSinceIgnited < 0)
         {
            this.timeSinceIgnited = 0;
         }

         if (this.timeSinceIgnited >= this.fuseTime)
         {
            this.timeSinceIgnited = this.fuseTime;

            if (!this.worldObj.isRemote)
            {
               boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
               float var3 = this.explosionRadius * 0.715F;
               float var4 = this.explosionRadius * 1.1F;

               if (this.getPowered())
               {
                  this.worldObj.createExplosion(this, this.posX, this.posY + (double)(this.height / 4.0F), this.posZ, var3 * 2.0F, var4 * 2.0F, var2);
               }
               else
               {
                  this.worldObj.createExplosion(this, this.posX, this.posY + (double)(this.height / 4.0F), this.posZ, var3, var4, var2);
               }

               this.has_exploded = true;
               this.entityFX(EnumEntityFX.frags);
               this.setDead();
            }
         }
      }

      super.onUpdate();
   }

   /**
    * Returns the sound this mob makes when it is hurt.
    */
   protected String getHurtSound()
   {
      return "mob.creeper.say";
   }

   /**
    * Returns the sound this mob makes on death.
    */
   protected String getDeathSound()
   {
      return "mob.creeper.death";
   }

   public void onEntityDamaged(DamageSource damage_source, float amount)
   {
      if (damage_source.isCactus() && BlockCactus.getKillCount(this.worldObj, damage_source.block_x, damage_source.block_y, damage_source.block_z) > 1 && this.rand.nextInt(2) == 0)
      {
         this.recently_took_damage_from_conspicuous_cactus = 120;
      }

      super.onEntityDamaged(damage_source, amount);
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource par1DamageSource)
   {
      super.onDeath(par1DamageSource);

      if (par1DamageSource.getResponsibleEntity() instanceof EntitySkeleton)
      {
         int var2 = Item.record13.itemID + this.rand.nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
         this.dropItem(var2, 1);
      }
   }

   /**
    * Returns true if the creeper is powered by a lightning bolt.
    */
   public boolean getPowered()
   {
      return this.dataWatcher.getWatchableObjectByte(17) == 1;
   }

   /**
    * Params: (Float)Render tick. Returns the intensity of the creeper's flash when it is ignited.
    */
   public float getCreeperFlashIntensity(float par1)
   {
      return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * par1) / (float)(this.fuseTime - 2);
   }

   /**
    * Returns the item ID for the item the mob drops on death.
    */
   protected int getDropItemId()
   {
      return this.recentlyHit <= 0 && this.rand.nextInt(3) != 0 ? 0 : Item.gunpowder.itemID;
   }

   /**
    * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
    */
   public int getCreeperState()
   {
      return this.dataWatcher.getWatchableObjectByte(16);
   }

   /**
    * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
    */
   public void setCreeperState(int par1)
   {
      this.dataWatcher.updateObject(16, Byte.valueOf((byte)par1));
   }

   /**
    * Called when a lightning bolt hits the entity.
    */
   public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt)
   {
      super.onStruckByLightning(par1EntityLightningBolt);
      this.dataWatcher.updateObject(17, Byte.valueOf((byte)1));
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk()
   {
      return 2;
   }

   public boolean hasExploded()
   {
      return this.has_exploded;
   }

   public int getFragParticle()
   {
      return Item.fragsCreeper.itemID;
   }

   public boolean canSeeEntity(Entity entity, boolean ignore_leaves)
   {
      if (entity.canEntityBeSeenFrom(this.posX, this.getFootPosY() + (double)(this.height / 4.0F), this.posZ, Double.MAX_VALUE, ignore_leaves))
      {
         this.onEntitySeen(entity);
         return true;
      }
      else
      {
         return super.canSeeEntity(entity, ignore_leaves);
      }
   }

   public boolean drawBackFaces()
   {
      return this.getPowered() || this.isWearingItems(true);
   }

   public boolean canBeAttackedBy(EntityLivingBase attacker)
   {
      return this.rand.nextInt(4) > 0 && attacker.hasCurse(Curse.fear_of_creepers, true) ? false : super.canBeAttackedBy(attacker);
   }
}
