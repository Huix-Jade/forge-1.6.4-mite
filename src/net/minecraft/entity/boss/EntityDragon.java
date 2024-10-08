package net.minecraft.entity.boss;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
   public double targetX;
   public double targetY;
   public double targetZ;
   public double[][] ringBuffer = new double[64][3];
   public int ringBufferIndex = -1;
   public EntityDragonPart[] dragonPartArray;
   public EntityDragonPart dragonPartHead;
   public EntityDragonPart dragonPartBody;
   public EntityDragonPart dragonPartTail1;
   public EntityDragonPart dragonPartTail2;
   public EntityDragonPart dragonPartTail3;
   public EntityDragonPart dragonPartWing1;
   public EntityDragonPart dragonPartWing2;
   public float prevAnimTime;
   public float animTime;
   public boolean forceNewTarget;
   public boolean slowed;
   private Entity target;
   public int deathTicks;
   public EntityEnderCrystal healingEnderCrystal;

   public EntityDragon(World par1World) {
      super(par1World);
      this.dragonPartArray = new EntityDragonPart[]{this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)};
      this.setHealth(this.getMaxHealth());
      this.setSize(16.0F, 8.0F);
      this.noClip = true;
      this.targetY = 100.0;
      this.ignoreFrustumCheck = true;
      this.renderDistanceWeight = 10.0;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(200.0);
   }

   protected void entityInit() {
      super.entityInit();
   }

   public double[] getMovementOffsets(int par1, float par2) {
      if (this.getHealth() <= 0.0F) {
         par2 = 0.0F;
      }

      par2 = 1.0F - par2;
      int var3 = this.ringBufferIndex - par1 * 1 & 63;
      int var4 = this.ringBufferIndex - par1 * 1 - 1 & 63;
      double[] var5 = new double[3];
      double var6 = this.ringBuffer[var3][0];
      double var8 = MathHelper.wrapAngleTo180_double(this.ringBuffer[var4][0] - var6);
      var5[0] = var6 + var8 * (double)par2;
      var6 = this.ringBuffer[var3][1];
      var8 = this.ringBuffer[var4][1] - var6;
      var5[1] = var6 + var8 * (double)par2;
      var5[2] = this.ringBuffer[var3][2] + (this.ringBuffer[var4][2] - this.ringBuffer[var3][2]) * (double)par2;
      return var5;
   }

   public void onLivingUpdate() {
      if (this.onServer() && this.worldObj.provider instanceof WorldProviderEnd && ((WorldProviderEnd)this.worldObj.provider).heal_ender_dragon) {
         this.healByPercentage(0.5F);
         ((WorldProviderEnd)this.worldObj.provider).heal_ender_dragon = false;
      }

      float var1;
      float var2;
      if (this.worldObj.isRemote) {
         var1 = MathHelper.cos(this.animTime * 3.1415927F * 2.0F);
         var2 = MathHelper.cos(this.prevAnimTime * 3.1415927F * 2.0F);
         if (var2 <= -0.3F && var1 >= -0.3F) {
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
         }
      }

      this.prevAnimTime = this.animTime;
      float var3;
      if (this.getHealth() <= 0.0F) {
         var1 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         var2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         var3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticle.largeexplode, this.posX + (double)var1, this.posY + 2.0 + (double)var2, this.posZ + (double)var3, 0.0, 0.0, 0.0);
      } else {
         this.updateDragonEnderCrystal();
         var1 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
         var1 *= (float)Math.pow(2.0, this.motionY);
         if (this.slowed) {
            this.animTime += var1 * 0.5F;
         } else {
            this.animTime += var1;
         }

         this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
         if (this.ringBufferIndex < 0) {
            for(int var25 = 0; var25 < this.ringBuffer.length; ++var25) {
               this.ringBuffer[var25][0] = (double)this.rotationYaw;
               this.ringBuffer[var25][1] = this.posY;
            }
         }

         if (++this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
         }

         this.ringBuffer[this.ringBufferIndex][0] = (double)this.rotationYaw;
         this.ringBuffer[this.ringBufferIndex][1] = this.posY;
         double var6;
         double var8;
         double var26;
         float var33;
         float var21;
         float var22;
         float var24;
         double var4;
         float var17;
         if (this.worldObj.isRemote) {
            if (this.newPosRotationIncrements > 0) {
               var26 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
               var4 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
               var6 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
               var8 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
               this.rotationYaw = (float)((double)this.rotationYaw + var8 / (double)this.newPosRotationIncrements);
               this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
               --this.newPosRotationIncrements;
               this.setPosition(var26, var4, var6);
               this.setRotation(this.rotationYaw, this.rotationPitch);
            }
         } else {
            var26 = this.targetX - this.posX;
            var4 = this.targetY - this.posY;
            var6 = this.targetZ - this.posZ;
            var8 = var26 * var26 + var4 * var4 + var6 * var6;
            double var11;
            double var13;
            if (this.target != null) {
               this.targetX = this.target.posX;
               this.targetZ = this.target.posZ;
               var11 = this.targetX - this.posX;
               var13 = this.targetZ - this.posZ;
               double var14 = Math.sqrt(var11 * var11 + var13 * var13);
               double var16 = 0.4000000059604645 + var14 / 80.0 - 1.0;
               if (var16 > 10.0) {
                  var16 = 10.0;
               }

               this.targetY = this.target.boundingBox.minY + var16;
            } else {
               this.targetX += this.rand.nextGaussian() * 2.0;
               this.targetZ += this.rand.nextGaussian() * 2.0;
            }

            if (this.forceNewTarget || var8 < 100.0 || var8 > 22500.0 || this.isCollidedHorizontally || this.isCollidedVertically) {
               this.setNewTarget();
            }

            var4 /= (double)MathHelper.sqrt_double(var26 * var26 + var6 * var6);
            var33 = 0.6F;
            if (var4 < (double)(-var33)) {
               var4 = (double)(-var33);
            }

            if (var4 > (double)var33) {
               var4 = (double)var33;
            }

            this.motionY += var4 * 0.10000000149011612;
            this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
            var11 = 180.0 - Math.atan2(var26, var6) * 180.0 / Math.PI;
            var13 = MathHelper.wrapAngleTo180_double(var11 - (double)this.rotationYaw);
            if (var13 > 50.0) {
               var13 = 50.0;
            }

            if (var13 < -50.0) {
               var13 = -50.0;
            }

            Vec3 var15 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
            Vec3 var40 = this.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F), this.motionY, (double)(-MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F))).normalize();
            var17 = (float)(var40.dotProduct(var15) + 0.5) / 1.5F;
            if (var17 < 0.0F) {
               var17 = 0.0F;
            }

            this.randomYawVelocity *= 0.8F;
            float var18 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
            double var19 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0 + 1.0;
            if (var19 > 40.0) {
               var19 = 40.0;
            }

            this.randomYawVelocity = (float)((double)this.randomYawVelocity + var13 * (0.699999988079071 / var19 / (double)var18));
            this.rotationYaw += this.randomYawVelocity * 0.1F;
            var21 = (float)(2.0 / (var19 + 1.0));
            var22 = 0.06F;
            this.moveFlying(0.0F, -1.0F, var22 * (var17 * var21 + (1.0F - var21)));
            if (this.slowed) {
               this.moveEntity(this.motionX * 0.800000011920929, this.motionY * 0.800000011920929, this.motionZ * 0.800000011920929);
            } else {
               this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }

            Vec3 var23 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.motionX, this.motionY, this.motionZ).normalize();
            var24 = (float)(var23.dotProduct(var40) + 1.0) / 2.0F;
            var24 = 0.8F + 0.15F * var24;
            this.motionX *= (double)var24;
            this.motionZ *= (double)var24;
            this.motionY *= 0.9100000262260437;
         }

         this.renderYawOffset = this.rotationYaw;
         this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
         this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
         this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
         this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
         this.dragonPartBody.height = 3.0F;
         this.dragonPartBody.width = 5.0F;
         this.dragonPartWing1.height = 2.0F;
         this.dragonPartWing1.width = 4.0F;
         this.dragonPartWing2.height = 3.0F;
         this.dragonPartWing2.width = 4.0F;
         var2 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
         var3 = MathHelper.cos(var2);
         float var28 = -MathHelper.sin(var2);
         float var5 = this.rotationYaw * 3.1415927F / 180.0F;
         float var27 = MathHelper.sin(var5);
         float var7 = MathHelper.cos(var5);
         this.dragonPartBody.onUpdate();
         this.dragonPartBody.setLocationAndAngles(this.posX + (double)(var27 * 0.5F), this.posY, this.posZ - (double)(var7 * 0.5F), 0.0F, 0.0F);
         this.dragonPartWing1.onUpdate();
         this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(var7 * 4.5F), this.posY + 2.0, this.posZ + (double)(var27 * 4.5F), 0.0F, 0.0F);
         this.dragonPartWing2.onUpdate();
         this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(var7 * 4.5F), this.posY + 2.0, this.posZ - (double)(var27 * 4.5F), 0.0F, 0.0F);
         if (!this.worldObj.isRemote && this.hurtTime == 0) {
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.boundingBox.expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.boundingBox.expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0)));
            this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.boundingBox.expand(1.0, 1.0, 1.0)));
         }

         double[] var29 = this.getMovementOffsets(5, 1.0F);
         double[] var9 = this.getMovementOffsets(0, 1.0F);
         var33 = MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F - this.randomYawVelocity * 0.01F);
         var17 = MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F - this.randomYawVelocity * 0.01F);
         this.dragonPartHead.onUpdate();
         this.dragonPartHead.setLocationAndAngles(this.posX + (double)(var33 * 5.5F * var3), this.posY + (var9[1] - var29[1]) * 1.0 + (double)(var28 * 5.5F), this.posZ - (double)(var17 * 5.5F * var3), 0.0F, 0.0F);

         for(int var30 = 0; var30 < 3; ++var30) {
            EntityDragonPart var31 = null;
            if (var30 == 0) {
               var31 = this.dragonPartTail1;
            }

            if (var30 == 1) {
               var31 = this.dragonPartTail2;
            }

            if (var30 == 2) {
               var31 = this.dragonPartTail3;
            }

            double[] var35 = this.getMovementOffsets(12 + var30 * 2, 1.0F);
            var21 = this.rotationYaw * 3.1415927F / 180.0F + this.simplifyAngle(var35[0] - var29[0]) * 3.1415927F / 180.0F * 1.0F;
            var22 = MathHelper.sin(var21);
            float var37 = MathHelper.cos(var21);
            var24 = 1.5F;
            float var39 = (float)(var30 + 1) * 2.0F;
            var31.onUpdate();
            var31.setLocationAndAngles(this.posX - (double)((var27 * var24 + var22 * var39) * var3), this.posY + (var35[1] - var29[1]) * 1.0 - (double)((var39 + var24) * var28) + 1.5, this.posZ + (double)((var7 * var24 + var37 * var39) * var3), 0.0F, 0.0F);
         }

         if (!this.worldObj.isRemote) {
            this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.boundingBox) | this.destroyBlocksInAABB(this.dragonPartBody.boundingBox);
         }
      }

   }

   private void updateDragonEnderCrystal() {
      if (this.healingEnderCrystal != null) {
         if (this.healingEnderCrystal.isDead) {
            if (!this.worldObj.isRemote) {
               this.attackEntityFromPart(this.dragonPartHead, new Damage(DamageSource.setExplosionSource((Explosion)null), 10.0F));
            }

            this.healingEnderCrystal = null;
         } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.rand.nextInt(10) == 0) {
         float var1 = 32.0F;
         List var2 = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand((double)var1, (double)var1, (double)var1));
         EntityEnderCrystal var3 = null;
         double var4 = Double.MAX_VALUE;
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            EntityEnderCrystal var7 = (EntityEnderCrystal)var6.next();
            double var8 = var7.getDistanceSqToEntity(this);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }

         this.healingEnderCrystal = var3;
      }

   }

   private void collideWithEntities(List par1List) {
      double var2 = (this.dragonPartBody.boundingBox.minX + this.dragonPartBody.boundingBox.maxX) / 2.0;
      double var4 = (this.dragonPartBody.boundingBox.minZ + this.dragonPartBody.boundingBox.maxZ) / 2.0;
      Iterator var6 = par1List.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         if (var7 instanceof EntityLivingBase) {
            double var8 = var7.posX - var2;
            double var10 = var7.posZ - var4;
            double var12 = var8 * var8 + var10 * var10;
            var7.addVelocity(var8 / var12 * 4.0, 0.20000000298023224, var10 / var12 * 4.0);
         }
      }

   }

   private void attackEntitiesInList(List par1List) {
      for(int var2 = 0; var2 < par1List.size(); ++var2) {
         Entity var3 = (Entity)par1List.get(var2);
         if (var3 instanceof EntityLivingBase) {
            var3.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this), 15.0F));
         }
      }

   }

   private void setNewTarget() {
      this.forceNewTarget = false;
      if (this.rand.nextInt(2) == 0 && !this.worldObj.playerEntities.isEmpty()) {
         this.target = (Entity)this.worldObj.playerEntities.get(this.rand.nextInt(this.worldObj.playerEntities.size()));
      } else {
         boolean var1 = false;

         do {
            this.targetX = 0.0;
            this.targetY = (double)(70.0F + this.rand.nextFloat() * 50.0F);
            this.targetZ = 0.0;
            this.targetX += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
            this.targetZ += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
            double var2 = this.posX - this.targetX;
            double var4 = this.posY - this.targetY;
            double var6 = this.posZ - this.targetZ;
            var1 = var2 * var2 + var4 * var4 + var6 * var6 > 100.0;
         } while(!var1);

         this.target = null;
      }

   }

   private float simplifyAngle(double par1) {
      return (float)MathHelper.wrapAngleTo180_double(par1);
   }

   private boolean destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
      int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX);
      int var3 = MathHelper.floor_double(par1AxisAlignedBB.minY);
      int var4 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
      int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX);
      int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY);
      int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var2; var10 <= var5; ++var10) {
         for(int var11 = var3; var11 <= var6; ++var11) {
            for(int var12 = var4; var12 <= var7; ++var12) {
               int var13 = this.worldObj.getBlockId(var10, var11, var12);
               Block block = Block.blocksList[var13];
               if (block != null) {
                  if (block.canEntityDestroy(worldObj, var10, var11, var12, this) && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                     var9 = this.worldObj.setBlockToAir(var10, var11, var12) || var9;
                  } else {
                     var8 = true;
                  }
               }
            }
         }
      }

      if (var9) {
         double var16 = par1AxisAlignedBB.minX + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double)this.rand.nextFloat();
         double var17 = par1AxisAlignedBB.minY + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double)this.rand.nextFloat();
         double var14 = par1AxisAlignedBB.minZ + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double)this.rand.nextFloat();
         this.worldObj.spawnParticle(EnumParticle.largeexplode, var16, var17, var14, 0.0, 0.0, 0.0);
      }

      return var8;
   }

   public EntityDamageResult attackEntityFromPart(EntityDragonPart par1EntityDragonPart, Damage damage) {
      if (par1EntityDragonPart != this.dragonPartHead && damage.getAmount() > 1.0F) {
         damage.scaleAmount(0.25F, 1.0F);
      }

      float var4 = this.rotationYaw * 3.1415927F / 180.0F;
      float var5 = MathHelper.sin(var4);
      float var6 = MathHelper.cos(var4);
      this.targetX = this.posX + (double)(var5 * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
      this.targetY = this.posY + (double)(this.rand.nextFloat() * 3.0F) + 1.0;
      this.targetZ = this.posZ - (double)(var6 * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
      this.target = null;
      return !(damage.getResponsibleEntity() instanceof EntityPlayer) && !damage.isExplosion() ? null : this.func_82195_e(damage);
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      return null;
   }

   protected EntityDamageResult func_82195_e(Damage damage) {
      return super.attackEntityFrom(damage);
   }

   protected void onDeathUpdate() {
      ++this.deathTicks;
      if (this.deathTicks >= 180 && this.deathTicks <= 200) {
         float var1 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.worldObj.spawnParticle(EnumParticle.hugeexplosion, this.posX + (double)var1, this.posY + 2.0 + (double)var2, this.posZ + (double)var3, 0.0, 0.0, 0.0);
      }

      int var4;
      int var5;
      if (!this.worldObj.isRemote) {
         if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
            var4 = 100;

            while(var4 > 0) {
               var5 = EntityXPOrb.getXPSplit(var4);
               var4 -= var5;
               this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
            }
         }

         if (this.deathTicks == 1) {
            this.worldObj.func_82739_e(1018, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
         }
      }

      this.moveEntity(0.0, 0.10000000149011612, 0.0);
      this.renderYawOffset = this.rotationYaw += 20.0F;
      if (this.deathTicks == 200 && !this.worldObj.isRemote) {
         var4 = 200;

         while(var4 > 0) {
            var5 = EntityXPOrb.getXPSplit(var4);
            var4 -= var5;
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
         }

         this.createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
         this.setDead();
      }

   }

   private void createEnderPortal(int par1, int par2) {
      byte var3 = 64;
      BlockEndPortal.bossDefeated = true;
      byte var4 = 4;

      for(int var5 = var3 - 1; var5 <= var3 + 32; ++var5) {
         for(int var6 = par1 - var4; var6 <= par1 + var4; ++var6) {
            for(int var7 = par2 - var4; var7 <= par2 + var4; ++var7) {
               double var8 = (double)(var6 - par1);
               double var10 = (double)(var7 - par2);
               double var12 = var8 * var8 + var10 * var10;
               if (var12 <= ((double)var4 - 0.5) * ((double)var4 - 0.5)) {
                  if (var5 < var3) {
                     if (var12 <= ((double)(var4 - 1) - 0.5) * ((double)(var4 - 1) - 0.5)) {
                        this.worldObj.setBlock(var6, var5, var7, Block.bedrock.blockID);
                     }
                  } else if (var5 > var3) {
                     this.worldObj.setBlock(var6, var5, var7, 0);
                  } else if (var12 > ((double)(var4 - 1) - 0.5) * ((double)(var4 - 1) - 0.5)) {
                     this.worldObj.setBlock(var6, var5, var7, Block.bedrock.blockID);
                  } else {
                     this.worldObj.setBlock(var6, var5, var7, Block.endPortal.blockID);
                  }
               }
            }
         }
      }

      this.worldObj.setBlock(par1, var3 + 0, par2, Block.bedrock.blockID);
      this.worldObj.setBlock(par1, var3 + 1, par2, Block.bedrock.blockID);
      this.worldObj.setBlock(par1, var3 + 2, par2, Block.bedrock.blockID);
      this.worldObj.setBlock(par1 - 1, var3 + 2, par2, Block.torchWood.blockID, Block.torchWood.getMetadataForDirectionFacing(0, EnumDirection.WEST), 3);
      this.worldObj.setBlock(par1 + 1, var3 + 2, par2, Block.torchWood.blockID, Block.torchWood.getMetadataForDirectionFacing(0, EnumDirection.EAST), 3);
      this.worldObj.setBlock(par1, var3 + 2, par2 - 1, Block.torchWood.blockID, Block.torchWood.getMetadataForDirectionFacing(0, EnumDirection.NORTH), 3);
      this.worldObj.setBlock(par1, var3 + 2, par2 + 1, Block.torchWood.blockID, Block.torchWood.getMetadataForDirectionFacing(0, EnumDirection.SOUTH), 3);
      this.worldObj.setBlock(par1, var3 + 3, par2, Block.bedrock.blockID);
      this.worldObj.setBlock(par1, var3 + 4, par2, Block.dragonEgg.blockID);
      BlockEndPortal.bossDefeated = false;
   }

   public void tryDespawnEntity() {
   }

   public Entity[] getParts() {
      return this.dragonPartArray;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public World func_82194_d() {
      return this.worldObj;
   }

   protected String getLivingSound() {
      return "mob.enderdragon.growl";
   }

   protected String getHurtSound() {
      return "mob.enderdragon.hit";
   }

   protected float getSoundVolume(String sound) {
      return 5.0F;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 20;
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
