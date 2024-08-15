package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet89PlaySoundOnServerAtEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {
   private boolean field_70279_a;
   private double speedMultiplier;
   private int boatPosRotationIncrements;
   private double boatX;
   private double boatY;
   private double boatZ;
   private double boatYaw;
   private double boatPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;
   private float phase_offset;
   private boolean has_made_splash_sound;
   private long last_bump_sound_time;
   private int recent_hits_from_squid;

   public EntityBoat(World par1World) {
      super(par1World);
      this.field_70279_a = true;
      this.speedMultiplier = 0.07;
      this.preventEntitySpawning = true;
      this.setSize(1.5F, 0.6F);
      this.yOffset = this.height / 2.0F;
      this.phase_offset = (float)((double)this.rand.nextFloat() * Math.PI * 2.0);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
   }

   public AxisAlignedBB getCollisionBox(Entity par1Entity) {
      return par1Entity.boundingBox;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public boolean canBePushed() {
      return true;
   }

   public EntityBoat(World par1World, double par2, double par4, double par6) {
      this(par1World);
      this.setPosition(par2, par4 + (double)this.yOffset, par6);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = par2;
      this.prevPosY = par4;
      this.prevPosZ = par6;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.0 - 0.30000001192092896;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         float damage_taken = damage.wasCausedByPlayerInCreative() ? 1000.0F : 10.0F;
         if (damage_taken > 0.0F) {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            result.setEntityWasAffected();
            result.startTrackingHealth(this.getDamageTaken());
            this.setDamageTaken(this.getDamageTaken() + damage_taken);
            result.finishTrackingHealth(this.getDamageTaken());
            this.setBeenAttacked();
            if (this.getDamageTaken() > 40.0F) {
               if (this.riddenByEntity != null) {
                  this.riddenByEntity.mountEntity(this);
               }

               if (!damage.wasCausedByPlayerInCreative()) {
                  this.dropItem(Item.boat.itemID, 1, 0.0F);
               }

               this.setDead();
               result.setEntityWasDestroyed();
            }
         }

         return result;
      }
   }

   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      if (this.field_70279_a) {
         this.boatPosRotationIncrements = par9 + 5;
      } else {
         double var10 = par1 - this.posX;
         double var12 = par3 - this.posY;
         double var14 = par5 - this.posZ;
         double var16 = var10 * var10 + var12 * var12 + var14 * var14;
         if (var16 <= 0.25) {
            return;
         }

         this.boatPosRotationIncrements = 3;
      }

      this.boatX = par1;
      this.boatY = par3;
      this.boatZ = par5;
      this.boatYaw = (double)par7;
      this.boatPitch = (double)par8;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   public void setVelocity(double par1, double par3, double par5) {
      this.velocityX = this.motionX = par1;
      this.velocityY = this.motionY = par3;
      this.velocityZ = this.motionZ = par5;
   }

   public void handlePacket89(Packet89PlaySoundOnServerAtEntity packet) {
      if (packet.sound == Packet89PlaySoundOnServerAtEntity.enum_sound.boat_bump) {
         this.playBumpSound(packet.volume, packet.pitch);
      }

   }

   public void playBumpSound(float volume, float pitch) {
      if (this.worldObj.getTotalWorldTime() >= this.last_bump_sound_time + 20L) {
         this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, Block.wood.stepSound.getPlaceSound(), volume, pitch);
         this.last_bump_sound_time = this.worldObj.getTotalWorldTime();
      }
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.onServer() && this.recent_hits_from_squid > 0 && this.getTicksExistedWithOffset() % 200 == 0) {
         --this.recent_hits_from_squid;
      }

      if (!this.has_made_splash_sound && !this.worldObj.isRemote && (this.isInWater() || this.worldObj.getBlockMaterial(this.getBlockPosX(), MathHelper.floor_double(this.posY - 0.2), this.getBlockPosZ()) == Material.water && this.getBlockRestingOn(0.1F) == null)) {
         this.playSound("random.splash", 0.1F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
         this.has_made_splash_sound = true;
      }

      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      byte var1 = 10;
      float submergence = 0.0F;
      float dy = 0.2F / (float)var1;
      float wave_height = (float)(Math.sin((double)((float)this.ticksExisted * 0.1F + this.phase_offset)) * 0.05000000074505806);

      for(int i = 0; i < var1; ++i) {
         float offset_y = (float)i * dy - 0.25F + wave_height;
         if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)offset_y), MathHelper.floor_double(this.posZ)) != Material.water) {
            break;
         }

         submergence += 1.0F / (float)var1;
      }

      double top_speed = 0.25;
      double var23 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      double var6;
      double var8;
      if (var23 > top_speed * 0.75) {
         var6 = Math.cos((double)this.rotationYaw * Math.PI / 180.0);
         var8 = Math.sin((double)this.rotationYaw * Math.PI / 180.0);

         for(int var10 = 0; (double)var10 < 1.0 + var23 * 60.0; ++var10) {
            double var11 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double var13 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7;
            double var15;
            double var17;
            if (this.rand.nextBoolean()) {
               var15 = this.posX - var6 * var11 * 0.8 + var8 * var13;
               var17 = this.posZ - var8 * var11 * 0.8 - var6 * var13;
               this.worldObj.spawnParticle(EnumParticle.splash, var15, this.posY - 0.125, var17, this.motionX, this.motionY, this.motionZ);
            } else {
               var15 = this.posX + var6 + var8 * var11 * 0.7;
               var17 = this.posZ + var8 - var6 * var11 * 0.7;
               this.worldObj.spawnParticle(EnumParticle.splash, var15, this.posY - 0.125, var17, this.motionX, this.motionY, this.motionZ);
            }
         }
      }

      double var25;
      double var12;
      if (this.worldObj.isRemote && this.field_70279_a) {
         if (this.boatPosRotationIncrements > 0) {
            var6 = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
            var8 = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
            var25 = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
            var12 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + var12 / (double)this.boatPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
            --this.boatPosRotationIncrements;
            this.setPosition(var6, var8, var25);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            var6 = this.posX + this.motionX;
            var8 = this.posY + this.motionY;
            var25 = this.posZ + this.motionZ;
            this.setPosition(var6, var8, var25);
            if (this.onGround) {
               this.motionX *= 0.5;
               this.motionY *= 0.5;
               this.motionZ *= 0.5;
            }

            this.motionX *= 0.9900000095367432;
            this.motionY *= 0.949999988079071;
            this.motionZ *= 0.9900000095367432;
         }
      } else {
         if (submergence < 0.05F) {
            this.motionY -= 0.019999999552965164;
         } else {
            this.motionY += (double)((submergence - 0.6F) * 0.01F);
         }

         this.motionY *= 0.949999988079071;
         if (this.motionY < -0.15000000596046448) {
            this.motionY = -0.15000000596046448;
         } else if (this.motionY > 0.019999999552965164) {
            this.motionY = 0.019999999552965164;
         }

         if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
            var6 = (double)((EntityLivingBase)this.riddenByEntity).moveForward;
            if (var6 > 0.0) {
               var8 = -Math.sin((double)(this.riddenByEntity.rotationYaw * 3.1415927F / 180.0F));
               var25 = Math.cos((double)(this.riddenByEntity.rotationYaw * 3.1415927F / 180.0F));
               this.motionX += var8 * this.speedMultiplier * 0.05000000074505806;
               this.motionZ += var25 * this.speedMultiplier * 0.05000000074505806;
            } else if (var6 < 0.0) {
               this.motionX *= 0.9800000190734863;
               this.motionZ *= 0.9800000190734863;
            }
         }

         var6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (var6 > top_speed) {
            var8 = top_speed / var6;
            this.motionX *= var8;
            this.motionZ *= var8;
            var6 = top_speed;
         }

         if (var6 > var23 && this.speedMultiplier < top_speed) {
            this.speedMultiplier += (top_speed - this.speedMultiplier) / (top_speed * 100.0);
            if (this.speedMultiplier > top_speed) {
               this.speedMultiplier = top_speed;
            }
         } else {
            this.speedMultiplier -= (this.speedMultiplier - 0.07) / (top_speed * 100.0);
            if (this.speedMultiplier < 0.07) {
               this.speedMultiplier = 0.07;
            }
         }

         if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
         }

         if (this.boatPosRotationIncrements > 0) {
            this.motionX += (this.boatX - this.posX) / 32.0;
            this.motionY += (this.boatY - this.posY) / 32.0;
            this.motionZ += (this.boatZ - this.posZ) / 32.0;
            --this.boatPosRotationIncrements;
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if (!this.isCollidedHorizontally) {
            this.motionX *= 0.9900000095367432;
            this.motionY *= 0.949999988079071;
            this.motionZ *= 0.9900000095367432;
         } else {
            if (var23 > 0.004999999888241291 && this.worldObj.isRemote && this.last_bump_sound_time + 20L < this.worldObj.getTotalWorldTime()) {
               Minecraft.theMinecraft.thePlayer.sendQueue.addToSendQueue(new Packet89PlaySoundOnServerAtEntity(Packet89PlaySoundOnServerAtEntity.enum_sound.boat_bump, this, (Block.wood.stepSound.getVolume() + 0.5F) * (float)var23, Block.wood.stepSound.getPitch() * 0.8F));
               this.last_bump_sound_time = this.worldObj.getTotalWorldTime();
            }

            if (var23 > 0.30000001192092896 && !this.worldObj.isRemote && !this.isDead) {
               this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);
               this.setDead();

               int var24;
               for(var24 = 0; var24 < 3; ++var24) {
                  this.dropItem(Block.planks.blockID, 1, 0.0F);
               }

               for(var24 = 0; var24 < 2; ++var24) {
                  this.dropItem(Item.stick.itemID, 1, 0.0F);
               }
            }
         }

         this.rotationPitch = 0.0F;
         var8 = (double)this.rotationYaw;
         var25 = this.prevPosX - this.posX;
         var12 = this.prevPosZ - this.posZ;
         if (var25 * var25 + var12 * var12 > 0.001) {
            var8 = (double)((float)(Math.atan2(var12, var25) * 180.0 / Math.PI));
         }

         double var14 = MathHelper.wrapAngleTo180_double(var8 - (double)this.rotationYaw);
         if (var14 > 20.0) {
            var14 = 20.0;
         }

         if (var14 < -20.0) {
            var14 = -20.0;
         }

         this.rotationYaw = (float)((double)this.rotationYaw + var14);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (!this.worldObj.isRemote) {
            List var16 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224, 0.0, 0.20000000298023224));
            int var26;
            if (var16 != null && !var16.isEmpty()) {
               for(var26 = 0; var26 < var16.size(); ++var26) {
                  Entity var18 = (Entity)var16.get(var26);
                  if (var18 != this.riddenByEntity && var18.canBePushed() && var18 instanceof EntityBoat) {
                     var18.applyEntityCollision(this);
                  }
               }
            }

            for(var26 = 0; var26 < 4; ++var26) {
               int var27 = MathHelper.floor_double(this.posX + ((double)(var26 % 2) - 0.5) * 0.8);
               int var19 = MathHelper.floor_double(this.posZ + ((double)(var26 / 2) - 0.5) * 0.8);

               for(int var20 = 0; var20 < 2; ++var20) {
                  int var21 = MathHelper.floor_double(this.posY) + var20;
                  int var22 = this.worldObj.getBlockId(var27, var21, var19);
                  if (var22 == Block.snow.blockID) {
                     this.worldObj.setBlockToAir(var27, var21, var19);
                  } else if (var22 == Block.waterlily.blockID) {
                     this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, var27, var21, var19)).setCollidedWith(this), true);
                  }
               }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
               this.riddenByEntity = null;
            }
         }
      }

      if (this.riddenByEntity == null) {
         this.motionX *= 0.949999988079071;
         this.motionZ *= 0.949999988079071;
      }

   }

   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         double var1 = Math.cos((double)this.rotationYaw * Math.PI / 180.0) * 0.4;
         double var3 = Math.sin((double)this.rotationYaw * Math.PI / 180.0) * 0.4;
         this.riddenByEntity.setPosition(this.posX + var1, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + var3);
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setBoolean("has_made_splash_sound", this.has_made_splash_sound);
      par1NBTTagCompound.setByte("recent_hits_from_squid", (byte)this.recent_hits_from_squid);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.has_made_splash_sound = par1NBTTagCompound.getBoolean("has_made_splash_sound");
      this.recent_hits_from_squid = par1NBTTagCompound.getByte("recent_hits_from_squid");
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (this.riddenByEntity != null) {
         return super.onEntityRightClicked(player, item_stack);
      } else {
         if (player.onServer()) {
            player.mountEntity(this);
         }

         return true;
      }
   }

   public void setDamageTaken(float par1) {
      this.dataWatcher.updateObject(19, par1);
   }

   public float getDamageTaken() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setTimeSinceHit(int par1) {
      this.dataWatcher.updateObject(17, par1);
   }

   public int getTimeSinceHit() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setForwardDirection(int par1) {
      this.dataWatcher.updateObject(18, par1);
   }

   public int getForwardDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void func_70270_d(boolean par1) {
      this.field_70279_a = par1;
   }

   public Item getModelItem() {
      return Item.boat;
   }

   public float adjustPlayerReachForInteraction(EntityPlayer player, float reach) {
      return 2.5F;
   }

   public void applyEntityCollision(Entity entity) {
      if (this.onServer() && !this.isDead && entity instanceof EntitySquid) {
         EntitySquid entity_squid = (EntitySquid)entity;
         if (entity_squid.canDestroyBoatOnCollision(this)) {
            if (++this.recent_hits_from_squid >= 6) {
               this.worldObj.playAuxSFX(1012, this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ(), 0);
               this.setDead();

               int var24;
               for(var24 = 0; var24 < 3; ++var24) {
                  this.dropItem(Block.planks.blockID, 1, 0.0F);
               }

               for(var24 = 0; var24 < 2; ++var24) {
                  this.dropItem(Item.stick.itemID, 1, 0.0F);
               }

               return;
            }

            this.worldObj.playAuxSFX(1010, this.getBlockPosX(), this.getBlockPosY(), this.getBlockPosZ(), 0);
         }
      }

      super.applyEntityCollision(entity);
   }
}
