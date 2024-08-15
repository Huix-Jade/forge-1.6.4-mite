package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.Raycast;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntitySquid extends EntityWaterMob {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   public float squidRotation;
   public float prevSquidRotation;
   public float tentacleAngle;
   public float prevTentacleAngle;
   private float randomMotionSpeed;
   private float rotationVelocity;
   private float field_70871_bB;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;
   private EntityLivingBase target;

   public EntitySquid(World par1World) {
      super(par1World);
      this.setSize(0.95F, 0.95F);
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0);
   }

   protected String getLivingSound() {
      return null;
   }

   protected String getHurtSound() {
      return null;
   }

   protected String getDeathSound() {
      return null;
   }

   protected float getSoundVolume(String sound) {
      return 0.4F;
   }

   protected int getDropItemId() {
      return 0;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (recently_hit_by_player) {
         this.dropItemStack(new ItemStack(Item.dyePowder, 1, 0), 0.0F);
      }

   }

   public boolean isInWater() {
      return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0, -0.6000000238418579, 0.0), Material.water, this);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.prevTentacleAngle = this.tentacleAngle;
      this.squidRotation += this.rotationVelocity;
      if (this.squidRotation > 6.2831855F) {
         this.squidRotation -= 6.2831855F;
         if (this.rand.nextInt(10) == 0) {
            this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
         }
      }

      AxisAlignedBB bb = this.boundingBox.copy();
      bb.minY = bb.minY * 0.2 + bb.maxY * 0.8;
      if (this.worldObj.isAABBInMaterial(bb, Material.water)) {
         float var1;
         if (this.squidRotation < 3.1415927F) {
            var1 = this.squidRotation / 3.1415927F;
            this.tentacleAngle = MathHelper.sin(var1 * var1 * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)var1 > 0.75) {
               this.randomMotionSpeed = 1.0F;
               this.field_70871_bB = 1.0F;
            } else {
               this.field_70871_bB *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.field_70871_bB *= 0.99F;
         }

         if (!this.worldObj.isRemote) {
            this.motionX = (double)(this.randomMotionVecX * this.randomMotionSpeed);
            this.motionY = (double)(this.randomMotionVecY * this.randomMotionSpeed);
            this.motionZ = (double)(this.randomMotionVecZ * this.randomMotionSpeed);
         }

         var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset += (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / 3.1415927F - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw += 3.1415927F * this.field_70871_bB * 1.5F;
         this.squidPitch += (-((float)Math.atan2((double)var1, this.motionY)) * 180.0F / 3.1415927F - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * 3.1415927F * 0.25F;
         if (!this.worldObj.isRemote) {
            this.motionX = 0.0;
            this.motionY -= 0.08;
            this.motionY *= 0.9800000190734863;
            this.motionZ = 0.0;
         }

         this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02);
      }

   }

   public void moveEntityWithHeading(float par1, float par2) {
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
   }

   public static boolean isAPlayerThatIsNotInOrAboveDeepWater(Entity entity) {
      if (entity instanceof EntityPlayer && entity.ridingEntity == null) {
         EntityPlayer player = entity.getAsPlayer();
         World world = player.worldObj;
         int x = player.getBlockPosX();
         int y = player.getEyeBlockPosY();
         int z = player.getBlockPosZ();
         if (world.isWaterBlock(x, y, z)) {
            return false;
         }

         if (player.getBlockRestingOn3() != null) {
            return true;
         }

         --y;
         if (world.isWaterBlock(x, y, z)) {
            --y;
            return !world.isWaterBlock(x, y, z);
         }

         --y;
         if (world.isWaterBlock(x, y, z)) {
            --y;
            return !world.isWaterBlock(x, y, z);
         }
      }

      return false;
   }

   public boolean preysUpon(Entity entity) {
      if (entity instanceof EntityWaterMob) {
         return false;
      } else if (this.worldObj.isBlueMoonNight()) {
         return false;
      } else if (!(entity instanceof EntityPlayer)) {
         return entity.isTrueAnimal();
      } else {
         EntityPlayer player = entity.getAsPlayer();
         if (player.inCreativeMode() || player.ridingEntity instanceof EntityBoat && this.worldObj.isDaytime()) {
            return false;
         } else {
            return !isAPlayerThatIsNotInOrAboveDeepWater(player);
         }
      }
   }

   public static boolean areOnlyFullWaterBlocksBetween(World worldObj, Vec3 origin, Vec3 limit) {
      int[] coords = Raycast.getFullBlockIntercepts(origin, limit);
      int num_blocks = coords.length / 3;

      for(int i = 0; i < num_blocks; ++i) {
         int offset = i * 3;
         int x = coords[offset];
         int y = coords[offset + 1];
         int z = coords[offset + 2];
         Block block = worldObj.getBlock(x, y, z);
         boolean is_full_water_block;
         if (block != Block.waterStill) {
            if (block == Block.waterMoving) {
               is_full_water_block = worldObj.getBlockMetadata(x, y, z) == 0;
            } else {
               is_full_water_block = false;
            }
         } else {
            int metadata = worldObj.getBlockMetadata(x, y, z);
            is_full_water_block = metadata == 0 || metadata == 8;
         }

         if (!is_full_water_block) {
            return false;
         }
      }

      return true;
   }

   protected void updateEntityActionState() {
      if (this.rand.nextInt(50) == 0 || !this.inWater || this.randomMotionVecX == 0.0F && this.randomMotionVecY == 0.0F && this.randomMotionVecZ == 0.0F) {
         if (isAPlayerThatIsNotInOrAboveDeepWater(this.target)) {
            this.target = null;
         }

         if (this.target == null || this.target.isDead || this.target.getHealth() <= 0.0F || this.rand.nextInt(10) == 0) {
            this.target = this.worldObj.getClosestPrey(this, 16.0, true, false);
         }

         if (this.target != null && this.worldObj.isBlueMoonNight()) {
            this.target = null;
         }

         Vec3 center_pos;
         Vec3 target_pos;
         if (this.target != null) {
            center_pos = this.getCenterPoint();
            target_pos = this.target.getCenterPoint();
            if (!areOnlyFullWaterBlocksBetween(this.worldObj, center_pos, target_pos)) {
               target_pos.yCoord = this.target.boundingBox.minY + 0.01;
               if (!areOnlyFullWaterBlocksBetween(this.worldObj, center_pos, target_pos)) {
                  target_pos.yCoord = this.target.boundingBox.maxY - 0.01;
                  if (!areOnlyFullWaterBlocksBetween(this.worldObj, center_pos, target_pos)) {
                     this.target = null;
                  }
               }
            }
         }

         if (this.target == null) {
            float var1 = this.rand.nextFloat() * 3.1415927F * 2.0F;
            this.randomMotionVecX = MathHelper.cos(var1) * 0.2F;
            this.randomMotionVecY = -0.1F + this.rand.nextFloat() * 0.2F;
            this.randomMotionVecZ = MathHelper.sin(var1) * 0.2F;
         } else {
            center_pos = this.getCenterPoint();
            target_pos = this.target.getCenterPoint();
            Vec3 vec3 = this.worldObj.getVec3(target_pos.xCoord - center_pos.xCoord, target_pos.yCoord - center_pos.yCoord, target_pos.zCoord - center_pos.zCoord).normalize();
            this.randomMotionVecX = (float)vec3.xCoord * 0.2F;
            this.randomMotionVecY = (float)vec3.yCoord * 0.2F;
            this.randomMotionVecZ = (float)vec3.zCoord * 0.2F;
         }
      }

      this.tryDespawnEntity();
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      if (!(this.posY <= 45.0) && !(this.posY >= 63.0)) {
         return !this.worldObj.isOnlyWater(this.boundingBox.expand(2.0, 2.0, 2.0)) ? false : super.getCanSpawnHere(perform_light_check);
      } else {
         return false;
      }
   }

   public int getExperienceValue() {
      return 0;
   }

   public void onCollideWithPlayer(EntityPlayer player) {
      if (!this.worldObj.isRemote && this.getDistanceToEntity(player) < 1.0F && !(player.ridingEntity instanceof EntityBoat)) {
         player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 2));
      }

   }

   protected void collideWithEntity(Entity entity) {
      if (this.onServer() && this.preysUpon(entity) && entity.isEntityLiving() && this.hasLineOfStrike(entity)) {
         entity.getAsEntityLiving().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 2));
      }

      super.collideWithEntity(entity);
   }

   public boolean canDestroyBoatOnCollision(EntityBoat entity_boat) {
      return this.target == entity_boat.riddenByEntity;
   }
}
