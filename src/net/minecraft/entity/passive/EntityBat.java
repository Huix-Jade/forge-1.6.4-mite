package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityGiantVampireBat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityNightwing;
import net.minecraft.entity.EntityVampireBat;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Damage;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumFace;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature {
   private ChunkCoordinates currentFlightTarget;
   private float attack_waver_x;
   private float attack_waver_y;
   private float attack_waver_z;
   private int data_object_id_block_hanging_from_y;
   private boolean initial_hang_attempted;
   private int sound_cooldown;
   private int prevent_hang_countdown;

   public EntityBat(World par1World) {
      super(par1World);
      this.updateSize();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
      this.data_object_id_block_hanging_from_y = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Short((short)0));
   }

   public void setBlockHangingFromY(int y) {
      if (this.onClient()) {
         Debug.setErrorMessage("setBlockHangingFromY: only meant to be called on server");
      } else {
         this.dataWatcher.updateObject(this.data_object_id_block_hanging_from_y, (short)y);
      }
   }

   public int getBlockHangingFromY() {
      return this.dataWatcher.getWatchableObjectShort(this.data_object_id_block_hanging_from_y);
   }

   public float getScaleFactor() {
      return this.isGiantSized() ? 2.0F : 1.0F;
   }

   public void updateSize() {
      float scale_factor = this.getScaleFactor();
      this.setSize(0.5F * scale_factor, 0.9F * scale_factor);
   }

   protected float getSoundVolume(String sound) {
      return super.getSoundVolume(sound) * 0.1F * this.getScaleFactor();
   }

   protected float getSoundPitch(String sound) {
      return super.getSoundPitch(sound) * 0.95F / this.getScaleFactor();
   }

   protected String getLivingSound() {
      return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : "mob.bat.idle";
   }

   protected String getHurtSound() {
      return "mob.bat.hurt";
   }

   protected String getDeathSound() {
      return "mob.bat.death";
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity par1Entity) {
      if (this.sound_cooldown <= 0) {
         this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
         this.sound_cooldown = this.rand.nextInt(31) + 30;
      }

   }

   protected void collideWithNearbyEntities() {
      if (this.isVampireBat() || this.isNightwing()) {
         super.collideWithNearbyEntities();
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(3.0 * (double)this.getScaleFactor());
   }

   public int getExperienceValue() {
      if (this.isVampireBat()) {
         return this.isGiantVampireBat() ? 10 : 5;
      } else {
         return this.isNightwing() ? 10 : 0;
      }
   }

   public boolean getIsBatHanging() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setIsBatHanging(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(var2 | 1));
         this.setHangingPosition();
      } else {
         if (this.getIsBatHanging()) {
            this.prevent_hang_countdown = 60;
         }

         this.dataWatcher.updateObject(16, (byte)(var2 & -2));
      }

   }

   protected boolean isAIEnabled() {
      return true;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.sound_cooldown > 0) {
         --this.sound_cooldown;
      }

      if (this.prevent_hang_countdown > 0) {
         --this.prevent_hang_countdown;
      }

      if (this.onServer() && this.hurtResistantTime > 0 && this.getIsBatHanging() && this.getBlockHangingFromY() >= 0) {
         this.setIsBatHanging(false);
         this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
         this.setPosition(this.posX, (double)((float)this.getBlockHangingFromY() - this.height - 0.01F), this.posZ);
      }

      if (this.getIsBatHanging() && this.getBlockHangingFromY() >= 0) {
         this.setHangingPosition();
      } else {
         this.motionY *= 0.6000000238418579;
      }

   }

   private boolean canBatHangFromCurrentPosition() {
      return this.getNewBlockHangingFromY() >= 0;
   }

   public boolean canHangFromBlock(int x, int y, int z) {
      Block block = this.worldObj.getBlock(x, y, z);
      if (block != null && block != Block.glass) {
         if (this.isGiantSized() && !this.worldObj.isAirBlock(x, y - 2, z)) {
            return false;
         } else {
            return !block.isAlwaysSolidStandardFormCube() && !block.isFaceFlatAndSolid(this.worldObj.getBlockMetadata(x, y, z), EnumFace.BOTTOM) ? false : this.worldObj.isAirBlock(x, y - 1, z);
         }
      } else {
         return false;
      }
   }

   private int getNewBlockHangingFromY() {
      int y_of_block_hanging_from = (int)(this.posY + 0.11999999731779099 + (double)this.height);
      return this.canHangFromBlock(this.getBlockPosX(), y_of_block_hanging_from, this.getBlockPosZ()) ? y_of_block_hanging_from : -1;
   }

   private void setHangingPosition() {
      if (!this.getIsBatHanging()) {
         Debug.setErrorMessage("setHangingPosition: bat is not hanging");
      } else if (this.getBlockHangingFromY() < 0) {
         Debug.setErrorMessage("setHangingPosition: block hanging from y not valid on " + this.worldObj.getClientOrServerString());
      } else {
         this.motionX = this.motionY = this.motionZ = 0.0;
         this.posY = (double)((float)this.getBlockHangingFromY() - this.height);
         this.posY -= this.isGiantSized() ? 0.11749999970197678 : 0.0062500000931322575;
      }
   }

   private boolean isPreventedFromHanging() {
      return this.prevent_hang_countdown > 0 || this.getAttackTarget() != null || this.hurtResistantTime > 0 || this.worldObj.getClosestPlayerToEntity(this, 4.0, true) != null;
   }

   protected void updateAITasks() {
      super.updateAITasks();
      int y_of_block_hanging_from;
      if (this.getIsBatHanging()) {
         y_of_block_hanging_from = this.getNewBlockHangingFromY();
         this.setBlockHangingFromY(y_of_block_hanging_from);
         if (y_of_block_hanging_from < 0) {
            this.setIsBatHanging(false);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
         } else {
            if (this.rand.nextInt(200) == 0) {
               this.rotationYawHead = (float)this.rand.nextInt(360);
            }

            if (this.isPreventedFromHanging() || this.rand.nextInt(1000) == 0) {
               this.setIsBatHanging(false);
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1015, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
               this.setPosition(this.posX, (double)((float)y_of_block_hanging_from - this.height - 0.01F), this.posZ);
            }
         }
      } else {
         if (this.currentFlightTarget != null && (!this.worldObj.isAirBlock(this.currentFlightTarget.posX, this.currentFlightTarget.posY, this.currentFlightTarget.posZ) || this.currentFlightTarget.posY < 1)) {
            this.currentFlightTarget = null;
         }

         y_of_block_hanging_from = this.getBlockPosX();
         int z = this.getBlockPosZ();
         if (this.currentFlightTarget == null || this.rand.nextInt(30) == 0 || this.currentFlightTarget.getDistanceSquared(y_of_block_hanging_from, (int)this.posY, z) < 4.0F) {
            int range_xz = (int)(7.0F * this.getScaleFactor());
            this.currentFlightTarget = new ChunkCoordinates(y_of_block_hanging_from + this.rand.nextInt(range_xz) - this.rand.nextInt(range_xz), (int)this.posY + this.rand.nextInt(6) - 2, z + this.rand.nextInt(range_xz) - this.rand.nextInt(range_xz));
         }

         if (this.currentFlightTarget.posY < 1) {
            this.currentFlightTarget.posY = 1;
         }

         double var1 = (double)this.currentFlightTarget.posX + 0.5 - this.posX;
         double var3 = (double)this.currentFlightTarget.posY + 0.1 - this.posY;
         double var5 = (double)this.currentFlightTarget.posZ + 0.5 - this.posZ;
         EntityLivingBase attack_target = this.getAttackTarget();
         if (attack_target != null) {
            Vec3 eye_pos = attack_target.getEyePos();
            if (eye_pos.yCoord + (double)this.attack_waver_y >= 0.5) {
               var1 = eye_pos.xCoord + (double)this.attack_waver_x - this.posX;
               var3 = eye_pos.yCoord + (double)this.attack_waver_y - this.posY;
               var5 = eye_pos.zCoord + (double)this.attack_waver_z - this.posZ;
            }

            if (this.getTicksExistedWithOffset() % 20 == 0) {
               this.attack_waver_x = this.rand.nextFloat() - 0.5F;
               this.attack_waver_y = this.rand.nextFloat() - 0.5F;
               this.attack_waver_z = this.rand.nextFloat() - 0.5F;
            }
         }

         this.motionX += (Math.signum(var1) * 0.5 - this.motionX) * 0.10000000149011612;
         this.motionY += (Math.signum(var3) * 0.699999988079071 - this.motionY) * 0.10000000149011612;
         this.motionZ += (Math.signum(var5) * 0.5 - this.motionZ) * 0.10000000149011612;
         float var7 = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0F;
         float var8 = MathHelper.wrapAngleTo180_float(var7 - this.rotationYaw);
         this.moveForward = 0.5F;
         this.rotationYaw += var8;
         if (!this.isPreventedFromHanging() && this.rand.nextInt(this.initial_hang_attempted ? 10 : 1) == 0) {
            int newBlockHangingFromY = this.getNewBlockHangingFromY();
            if (newBlockHangingFromY >= 0) {
               this.setBlockHangingFromY(newBlockHangingFromY);
               this.setIsBatHanging(true);
            }
         }

         this.initial_hang_attempted = true;
      }

   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void fall(float par1) {
   }

   protected void updateFallState(double par1, boolean par3) {
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (this.getIsBatHanging()) {
            this.setIsBatHanging(false);
            result.setEntityWasAffected();
         }

         return result;
      }
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.dataWatcher.updateObject(16, par1NBTTagCompound.getByte("BatFlags"));
      this.initial_hang_attempted = par1NBTTagCompound.getBoolean("initial_hang_attempted");
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setByte("BatFlags", this.dataWatcher.getWatchableObjectByte(16));
      par1NBTTagCompound.setBoolean("initial_hang_attempted", this.initial_hang_attempted);
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      int var1 = MathHelper.floor_double(this.boundingBox.minY);
      if (this.worldObj.isOverworld() && var1 >= 63) {
         return false;
      } else {
         int var2 = MathHelper.floor_double(this.posX);
         int var3 = MathHelper.floor_double(this.posZ);
         int var4 = this.worldObj.getBlockLightValue(var2, var1, var3);
         byte var5 = 4;
         Calendar var6 = this.worldObj.getCurrentDate();
         if ((var6.get(2) + 1 != 10 || var6.get(5) < 20) && (var6.get(2) + 1 != 11 || var6.get(5) > 3)) {
            if (this.rand.nextBoolean()) {
               return false;
            }
         } else {
            var5 = 7;
         }

         while(var1 > 0) {
            --var1;
            if (this.worldObj.isBlockStandardFormOpaqueCube(var2, var1, var3)) {
               break;
            }

            int blv = this.worldObj.getBlockLightValue(var2, var1, var3);
            if (blv > var4) {
               var4 = blv;
            }
         }

         return var4 > this.rand.nextInt(var5) ? false : super.getCanSpawnHere(perform_light_check);
      }
   }

   public boolean isVampireBat() {
      return this instanceof EntityVampireBat;
   }

   public boolean isGiantVampireBat() {
      return this instanceof EntityGiantVampireBat;
   }

   public boolean isGiantSized() {
      return this.isGiantVampireBat();
   }

   public boolean isNightwing() {
      return this instanceof EntityNightwing;
   }

   public void setInitialHangAttempted() {
      this.initial_hang_attempted = true;
   }
}
