package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Damage;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity {
   private int xTile;
   private int yTile;
   private int zTile;
   private int inTile;
   private boolean inGround;
   public EntityLivingBase shootingEntity;
   private int ticksAlive;
   private int ticksInAir;
   public double accelerationX;
   public double accelerationY;
   public double accelerationZ;

   public EntityFireball(World par1World) {
      super(par1World);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
      this.setSize(1.0F, 1.0F);
   }

   protected void entityInit() {
   }

   public boolean isInRangeToRenderDist(double par1) {
      double var3 = this.boundingBox.getAverageEdgeLength() * 4.0;
      var3 *= 64.0;
      return par1 < var3 * var3;
   }

   public EntityFireball(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
      super(par1World);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
      this.setSize(1.0F, 1.0F);
      this.setLocationAndAngles(par2, par4, par6, this.rotationYaw, this.rotationPitch);
      this.setPosition(par2, par4, par6);
      double var14 = (double)MathHelper.sqrt_double(par8 * par8 + par10 * par10 + par12 * par12);
      this.accelerationX = par8 / var14 * 0.1;
      this.accelerationY = par10 / var14 * 0.1;
      this.accelerationZ = par12 / var14 * 0.1;
   }

   public EntityFireball(World par1World, EntityLivingBase par2EntityLivingBase, double par3, double par5, double par7) {
      super(par1World);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
      this.shootingEntity = par2EntityLivingBase;
      this.setSize(1.0F, 1.0F);
      this.setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY, par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.yOffset = 0.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      par3 += this.rand.nextGaussian() * 0.4;
      par5 += this.rand.nextGaussian() * 0.4;
      par7 += this.rand.nextGaussian() * 0.4;
      double var9 = (double)MathHelper.sqrt_double(par3 * par3 + par5 * par5 + par7 * par7);
      this.accelerationX = par3 / var9 * 0.1;
      this.accelerationY = par5 / var9 * 0.1;
      this.accelerationZ = par7 / var9 * 0.1;
   }

   public EntityFireball(World world, EntityLivingBase shooter, Vec3 target, float initial_distance) {
      this(world, shooter, shooter.getCenterPoint(), target, initial_distance);
   }

   public EntityFireball(World world, EntityLivingBase shooter, Vec3 origin, Vec3 target, float initial_distance) {
      super(world);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
      this.shootingEntity = shooter;
      this.setSize(1.0F, 1.0F);
      float yaw = (float)MathHelper.getYawInDegrees(origin, target);
      float pitch = (float)MathHelper.getPitchInDegrees(origin, target);
      this.setLocationAndAngles(origin.xCoord, origin.yCoord, origin.zCoord, yaw, pitch);
      this.yOffset = 0.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.setTarget(target, initial_distance, 0.1F);
   }

   public void setTarget(Vec3 target, float initial_distance, float wander) {
      this.setTrajectory(Vec3.getDifference(this.getCenterPoint(), target), initial_distance, wander);
   }

   public void setTrajectory(Vec3 trajectory, float initial_distance, float wander) {
      if (wander > 0.0F) {
         trajectory = trajectory.copy();
         trajectory.xCoord *= (double)(1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * wander);
         trajectory.yCoord *= (double)(1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * wander);
         trajectory.zCoord *= (double)(1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * wander);
      }

      trajectory = trajectory.normalize();
      this.accelerationX = trajectory.xCoord * 0.1;
      this.accelerationY = trajectory.yCoord * 0.1;
      this.accelerationZ = trajectory.zCoord * 0.1;
      this.posX += trajectory.xCoord * (double)initial_distance;
      this.posY += trajectory.yCoord * (double)initial_distance;
      this.posZ += trajectory.zCoord * (double)initial_distance;
   }

   public boolean cannotRaycastCollideWith(Entity entity) {
      return entity.isEntityEqual(this.shootingEntity) && this.ticksInAir < 25 ? true : super.cannotRaycastCollideWith(entity);
   }

   public void onUpdate() {
      if (!this.worldObj.isRemote && (this.shootingEntity != null && this.shootingEntity.isDead || !this.worldObj.blockExists(this.getBlockPosX(), MathHelper.floor_double(this.posY), this.getBlockPosZ()))) {
         this.setDead();
      } else {
         super.onUpdate();
         this.setFire(1);
         if (this.inGround) {
            int var1 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            if (var1 == this.inTile) {
               ++this.ticksAlive;
               if (this.ticksAlive == 600) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksAlive = 0;
            this.ticksInAir = 0;
         } else {
            ++this.ticksInAir;
         }

         Vec3 current_pos = this.worldObj.getVec3(this.posX, this.posY, this.posZ);
         Vec3 future_pos = this.worldObj.getVec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         Raycast raycast = (new Raycast(this.worldObj, current_pos, future_pos)).setOriginator(this);
         this.setCollisionPolicies(raycast);
         raycast.performVsBlocks();
         RaycastCollision var3 = raycast.getBlockCollision();
         if (var3 != null) {
            raycast.setLimitToBlockCollisionPoint();
         }

         if (raycast.performVsEntities().hasEntityCollisions()) {
            var3 = raycast.getNearestCollision();
         }

         if (var3 != null) {
            this.onImpact(var3);
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         float var16 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) + 90.0F;

         for(this.rotationPitch = (float)(Math.atan2((double)var16, this.motionY) * 180.0 / Math.PI) - 90.0F; this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         }

         while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         float var17 = this.getMotionFactor();
         if (this.isInWater()) {
            for(int var19 = 0; var19 < 4; ++var19) {
               float var18 = 0.25F;
               this.worldObj.spawnParticle(EnumParticle.bubble, this.posX - this.motionX * (double)var18, this.posY - this.motionY * (double)var18, this.posZ - this.motionZ * (double)var18, this.motionX, this.motionY, this.motionZ);
            }

            var17 = 0.8F;
         }

         this.motionX += this.accelerationX;
         this.motionY += this.accelerationY;
         this.motionZ += this.accelerationZ;
         this.motionX *= (double)var17;
         this.motionY *= (double)var17;
         this.motionZ *= (double)var17;
         this.worldObj.spawnParticle(EnumParticle.smoke, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
         this.setPosition(this.posX, this.posY, this.posZ);
      }

   }

   protected float getMotionFactor() {
      return 0.95F;
   }

   protected abstract void onImpact(RaycastCollision var1);

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("xTile", (short)this.xTile);
      par1NBTTagCompound.setShort("yTile", (short)this.yTile);
      par1NBTTagCompound.setShort("zTile", (short)this.zTile);
      par1NBTTagCompound.setByte("inTile", (byte)this.inTile);
      par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      par1NBTTagCompound.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
      par1NBTTagCompound.setTag("acceleration", this.newDoubleNBTList(new double[]{this.accelerationX, this.accelerationY, this.accelerationZ}));
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.xTile = par1NBTTagCompound.getShort("xTile");
      this.yTile = par1NBTTagCompound.getShort("yTile");
      this.zTile = par1NBTTagCompound.getShort("zTile");
      this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
      this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
      if (par1NBTTagCompound.hasKey("direction")) {
         NBTTagList var2 = par1NBTTagCompound.getTagList("direction");
         this.motionX = ((NBTTagDouble)var2.tagAt(0)).data;
         this.motionY = ((NBTTagDouble)var2.tagAt(1)).data;
         this.motionZ = ((NBTTagDouble)var2.tagAt(2)).data;
         var2 = par1NBTTagCompound.getTagList("acceleration");
         this.accelerationX = ((NBTTagDouble)var2.tagAt(0)).data;
         this.accelerationY = ((NBTTagDouble)var2.tagAt(1)).data;
         this.accelerationZ = ((NBTTagDouble)var2.tagAt(2)).data;
      } else {
         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public float getCollisionBorderSize(Entity for_raycast_from_this_entity) {
      return 1.0F;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         Entity responsible_entity = damage.getResponsibleEntity();
         if (responsible_entity instanceof EntityLivingBase) {
            Vec3 var3 = responsible_entity.getLookVec();
            if (var3 != null) {
               this.motionX = var3.xCoord;
               this.motionY = var3.yCoord;
               this.motionZ = var3.zCoord;
               this.accelerationX = this.motionX * 0.1;
               this.accelerationY = this.motionY * 0.1;
               this.accelerationZ = this.motionZ * 0.1;
               this.shootingEntity = (EntityLivingBase)responsible_entity;
               this.setBeenAttacked();
               result.setEntityWasAffected();
               this.sendPacketToAllPlayersTrackingEntity((new Packet85SimpleSignal(EnumSignal.fireball_reversal)).setEntityID(this).setApproxPosition(this.motionX, this.motionY, this.motionZ));
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public float getBrightness(float par1) {
      return 1.0F;
   }

   public int getBrightnessForRender(float par1) {
      return 15728880;
   }

   public boolean canCatchFire() {
      return true;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public abstract void setCollisionPolicies(Raycast var1);

   public float adjustPlayerReachForAttacking(EntityPlayer player, float reach) {
      return super.adjustPlayerReachForAttacking(player, reach) + 2.0F;
   }
}
