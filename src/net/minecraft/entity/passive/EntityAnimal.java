package net.minecraft.entity.passive;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidFire;
import net.minecraft.entity.ai.EntityAIMoveToFoodItem;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet84EntityStateWithData;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.Damage;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
   private int inLove;
   private int breeding;

   public EntityAnimal(World par1World) {
      super(par1World);
      this.tasks.addTask(1, new EntityAIMoveToFoodItem(this, 1.0F, true));
      this.tasks.addTask(2, new EntityAIAvoidFire(this, 1.0F, true));
   }

   public final boolean isAIEnabled() {
      return true;
   }

   protected void updateAITick() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITick();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         String var1 = "heart";
         if (this.inLove % 10 == 0) {
            this.spawnInLoveHeartParticle();
         }
      } else {
         this.breeding = 0;
      }

      if (this.inLove > 0 && this.inLove % 10 == 0 && this.worldObj instanceof WorldServer) {
         WorldServer world_server = (WorldServer)this.worldObj;
         if (world_server.playerEntities.size() > 0) {
            Iterator i = world_server.playerEntities.iterator();

            while(i.hasNext()) {
               EntityPlayer player = (EntityPlayer)i.next();
               if (player.getDistanceSqToEntity(this) < 256.0) {
                  world_server.getEntityTracker().sendPacketToAllAssociatedPlayers(this, new Packet84EntityStateWithData(this.entityId, EnumEntityState.in_love, this.inLove));
               }
            }
         }
      }

   }

   protected void attackEntity(Entity par1Entity, float par2) {
      if (par1Entity instanceof EntityPlayer) {
         if (par2 < 3.0F) {
            double var3 = par1Entity.posX - this.posX;
            double var5 = par1Entity.posZ - this.posZ;
            this.rotationYaw = (float)(Math.atan2(var5, var3) * 180.0 / Math.PI) - 90.0F;
            this.hasAttacked = true;
         }

         EntityPlayer var7 = (EntityPlayer)par1Entity;
         if (var7.getHeldItemStack() == null || !this.willEat(var7.getHeldItemStack())) {
            this.entityToAttack = null;
         }
      } else if (par1Entity instanceof EntityAnimal) {
         EntityAnimal var8 = (EntityAnimal)par1Entity;
         if (this.getGrowingAge() > 0 && var8.getGrowingAge() < 0) {
            if ((double)par2 < 2.5) {
               this.hasAttacked = true;
            }
         } else if (this.inLove > 0 && var8.inLove > 0) {
            if (var8.entityToAttack == null) {
               var8.entityToAttack = this;
            }

            if (var8.entityToAttack == this && (double)par2 < 3.5) {
               ++var8.inLove;
               ++this.inLove;
               ++this.breeding;
               if (this.breeding % 4 == 0) {
                  this.worldObj.spawnParticle(EnumParticle.heart, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0, 0.0, 0.0);
               }

               if (this.breeding == 60) {
                  this.procreate((EntityAnimal)par1Entity);
               }
            } else {
               this.breeding = 0;
            }
         } else {
            this.breeding = 0;
            this.entityToAttack = null;
         }
      }

   }

   private void procreate(EntityAnimal par1EntityAnimal) {
      EntityAgeable var2 = this.createChild(par1EntityAnimal);
      if (var2 != null) {
         this.setGrowingAgeAfterBreeding();
         par1EntityAnimal.setGrowingAgeAfterBreeding();
         this.inLove = 0;
         this.breeding = 0;
         this.entityToAttack = null;
         par1EntityAnimal.entityToAttack = null;
         par1EntityAnimal.breeding = 0;
         par1EntityAnimal.inLove = 0;
         var2.setGrowingAgeToNewborn();
         var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         if (this.onServer() && var2 instanceof EntityLivestock) {
            ((EntityLivestock)var2).adoptWellnessFromParents(this, par1EntityAnimal);
         }

         for(int var3 = 0; var3 < 7; ++var3) {
            double var4 = this.rand.nextGaussian() * 0.02;
            double var6 = this.rand.nextGaussian() * 0.02;
            double var8 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(EnumParticle.heart, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var4, var6, var8);
         }

         this.worldObj.spawnEntityInWorld(var2);
      }

   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (result.entityWasNegativelyAffected()) {
            if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
               this.warnPeersOfAttacker(this.getClass(), damage.getResponsibleEntity());
            }

            this.fleeingTick = 60;
            if (!this.isAIEnabled()) {
               AttributeInstance var3 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
               if (var3.getModifier(field_110179_h) == null) {
                  var3.applyModifier(field_110181_i);
               }
            }

            this.entityToAttack = null;
            this.setInLove(0, true);
         }

         return result;
      }
   }

   public float getBlockPathWeight(int par1, int par2, int par3) {
      return this.worldObj.getBlockId(par1, par2 - 1, par3) == Block.grass.blockID ? 10.0F : this.worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("InLove", this.inLove);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.inLove = par1NBTTagCompound.getInteger("InLove");
   }

   public void setInLove(int in_love) {
      this.setInLove(in_love, this.onServer());
   }

   public void setInLove(int in_love, boolean update_client) {
      this.inLove = in_love;
      if (update_client) {
         if (this.onServer()) {
            this.sendPacketToAllPlayersTrackingEntity((new Packet85SimpleSignal(EnumSignal.in_love)).setShort(0).setEntityID(this));
         } else {
            Minecraft.setErrorMessage("setInLove: update_client is true but calling on client");
         }
      }

   }

   protected EntityPlayer findPlayerToAttack(float max_distance) {
      Minecraft.setErrorMessage("EntityAnimal using old AI system: " + this.getEntityName());
      return null;
   }

   protected EntityPlayer findNonPlayerToAttack(float max_distance) {
      Minecraft.setErrorMessage("EntityAnimal using old AI system: " + this.getEntityName());
      return null;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.boundingBox.minY);
      int var3 = MathHelper.floor_double(this.posZ);
      if (this instanceof EntityHellhound) {
         return (!perform_light_check || this.isValidLightLevel()) && super.getCanSpawnHere(perform_light_check);
      } else {
         return this.worldObj.getBlockId(var1, var2 - 1, var3) == Block.grass.blockID && (!perform_light_check || this.worldObj.getFullBlockLightValue(var1, var2, var3) > 8) && super.getCanSpawnHere(perform_light_check);
      }
   }

   public int getTalkInterval() {
      return 120;
   }

   protected boolean canDespawn() {
      if (!(this instanceof EntityHellhound)) {
         if (this.despawn_counter < 800) {
            return false;
         }

         if (this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 1024.0, false) != null || this.worldObj.blockTypeIsNearTo(Block.fence.blockID, this.posX, this.posY, this.posZ, 16, 1)) {
            this.despawn_counter = 0;
            return false;
         }

         if (this.despawn_counter <= 2400) {
            return false;
         }
      }

      return super.canDespawn();
   }

   public boolean canEat() {
      return !this.isInLove() && this.getGrowingAge() == 0 && super.canEat();
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (item_stack != null && this.willEat(item_stack)) {
         if (player.onServer()) {
            this.onFoodEaten(item_stack);
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }

         return true;
      } else {
         return super.onEntityRightClicked(player, item_stack);
      }
   }

   public void onFoodEaten(ItemStack item_stack) {
      if (item_stack != null) {
         if (item_stack.getItem() != Item.rottenFlesh) {
            this.func_110196_bT();
         }

         super.onFoodEaten(item_stack);
      }
   }

   public static int getMaxInLove() {
      return 600;
   }

   public void func_110196_bT() {
      if (!(this.getHealth() < this.getMaxHealth())) {
         this.inLove = getMaxInLove();
         this.entityToAttack = null;
         if (!this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, EnumEntityState.in_love);
         }

      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   public boolean canMateWith(EntityAnimal par1EntityAnimal) {
      return par1EntityAnimal == this ? false : (par1EntityAnimal.getClass() != this.getClass() ? false : this.isInLove() && par1EntityAnimal.isInLove());
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.in_love) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.rand.nextGaussian() * 0.02;
            double var5 = this.rand.nextGaussian() * 0.02;
            double var7 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(EnumParticle.heart, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var3, var5, var7);
         }

         this.func_110196_bT();
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public void setAttackTarget(EntityLivingBase par1EntityLivingBase) {
      if (this.isChild()) {
         par1EntityLivingBase = null;
      }

      super.setAttackTarget(par1EntityLivingBase);
   }

   public boolean isOnLadder() {
      return false;
   }
}
