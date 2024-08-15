package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class EntityFishHook extends Entity {
   private int xTile;
   private int yTile;
   private int zTile;
   private int inTile;
   private boolean inGround;
   public int shake;
   public EntityPlayer angler;
   private int ticksInGround;
   private int ticksInAir;
   private int ticksCatchable;
   public Entity bobber;
   private int fishPosRotationIncrements;
   private double fishX;
   private double fishY;
   private double fishZ;
   private double fishYaw;
   private double fishPitch;
   private double velocityX;
   private double velocityY;
   private double velocityZ;
   private Material material;

   public EntityFishHook(World par1World) {
      super(par1World);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
      this.setSize(0.25F, 0.25F);
      this.ignoreFrustumCheck = true;
   }

   public EntityFishHook(World par1World, double par2, double par4, double par6, EntityPlayer par8EntityPlayer) {
      this(par1World);
      this.setPosition(par2, par4, par6);
      this.ignoreFrustumCheck = true;
      this.angler = par8EntityPlayer;
      par8EntityPlayer.fishEntity = this;
      this.material = this.getHookMaterialFromPlayer(par8EntityPlayer);
   }

   public EntityFishHook(World par1World, EntityPlayer par2EntityPlayer) {
      super(par1World);
      this.xTile = -1;
      this.yTile = -1;
      this.zTile = -1;
      this.ignoreFrustumCheck = true;
      this.angler = par2EntityPlayer;
      this.angler.fishEntity = this;
      this.setSize(0.25F, 0.25F);
      this.setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + 1.62 - (double)par2EntityPlayer.yOffset, par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
      this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F);
      this.posY -= 0.10000000149011612;
      this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.yOffset = 0.0F;
      float var3 = 0.4F;
      this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * var3);
      this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * var3);
      this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F) * var3);
      this.calculateVelocity(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
      this.material = this.getHookMaterialFromPlayer(par2EntityPlayer);
   }

   private Material getHookMaterialFromPlayer(EntityPlayer player) {
      Material material = player == null ? null : (player.getHeldItem() instanceof ItemFishingRod ? ((ItemFishingRod)player.getHeldItem()).getHookMaterial() : null);
      if (material == null) {
         Minecraft.setErrorMessage("getHookMaterialFromPlayer: was not able to determine hook material");
      }

      return material;
   }

   protected void entityInit() {
   }

   public boolean isInRangeToRenderDist(double par1) {
      double var3 = this.boundingBox.getAverageEdgeLength() * 4.0;
      var3 *= 64.0;
      return par1 < var3 * var3;
   }

   public void calculateVelocity(double par1, double par3, double par5, float par7, float par8) {
      float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
      par1 /= (double)var9;
      par3 /= (double)var9;
      par5 /= (double)var9;
      par1 += this.rand.nextGaussian() * 0.007499999832361937 * (double)par8;
      par3 += this.rand.nextGaussian() * 0.007499999832361937 * (double)par8;
      par5 += this.rand.nextGaussian() * 0.007499999832361937 * (double)par8;
      par1 *= (double)par7;
      par3 *= (double)par7;
      par5 *= (double)par7;
      this.motionX = par1;
      this.motionY = par3;
      this.motionZ = par5;
      float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
      this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var10) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      this.fishX = par1;
      this.fishY = par3;
      this.fishZ = par5;
      this.fishYaw = (double)par7;
      this.fishPitch = (double)par8;
      this.fishPosRotationIncrements = par9;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   public void setVelocity(double par1, double par3, double par5) {
      this.velocityX = this.motionX = par1;
      this.velocityY = this.motionY = par3;
      this.velocityZ = this.motionZ = par5;
   }

   private boolean isFishInhabitedWaterBlock(int x, int y, int z) {
      if (BlockFluid.isFullWaterBlock(this.worldObj, x, y, z, false)) {
         return true;
      } else {
         return this.worldObj.getBlock(x, y, z) == Block.waterStill && this.worldObj.getBlockMetadata(x, y, z) == 8;
      }
   }

   private boolean checkForBite() {
      int x = MathHelper.floor_double(this.posX);
      int y = MathHelper.floor_double(this.posY - 0.20000000298023224);
      int z = MathHelper.floor_double(this.posZ);
      if (BlockFluid.isFullWaterBlock(this.worldObj, x, y, z, false) && this.worldObj.isAirBlock(x, y + 1, z)) {
         int dx = this.rand.nextInt(7) - 3;
         int dy = -this.rand.nextInt(4);
         int dz = this.rand.nextInt(7) - 3;
         if (!this.isFishInhabitedWaterBlock(x + dx, y + dy, z + dz)) {
            return false;
         } else {
            Vec3 fish_hook_position = this.worldObj.getVec3((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F));
            Vec3 fish_position = this.worldObj.getVec3((double)((float)(x + dx) + 0.5F), (double)((float)(y + dy) + 0.5F), (double)((float)(z + dz) + 0.5F));
            if (!this.worldObj.checkForLineOfPhysicalReach(fish_hook_position, fish_position)) {
               return false;
            } else {
               int time_of_day = this.worldObj.getAdjustedTimeOfDay();
               float time_factor = (float)Math.min(Math.abs(time_of_day - 5500), Math.abs(time_of_day - 17500)) / 600.0F;
               int chance_in = this.worldObj.isBlueMoon(true) ? 600 : MathHelper.clamp_int((int)(600.0F * time_factor), 600, 2400);
               if (this.worldObj.canLightningStrikeAt(x, y + 1, z)) {
                  chance_in /= 2;
               }

               if (this.worldObj.areSkillsEnabled() && !this.angler.hasSkill(Skill.FISHING)) {
                  chance_in *= 2;
               }

               int fortune = EnchantmentHelper.getFishingFortuneModifier(this.angler);

               for(int i = 0; i < fortune; ++i) {
                  chance_in = chance_in * 9 / 10;
               }

               if (this.angler.inventory.getHotbarSlotContainItem(Item.wormRaw) >= 0) {
                  chance_in = (int)((float)chance_in * 0.5F);
               }

               return this.rand.nextInt(chance_in) == 0;
            }
         }
      } else {
         return false;
      }
   }

   public boolean cannotRaycastCollideWith(Entity entity) {
      if (entity == this.angler && this.ticksInAir < 5) {
         return true;
      } else if (entity instanceof EntityBoat) {
         return true;
      } else {
         return entity == this.angler.ridingEntity ? true : super.cannotRaycastCollideWith(entity);
      }
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.fishPosRotationIncrements > 0) {
         double var21 = this.posX + (this.fishX - this.posX) / (double)this.fishPosRotationIncrements;
         double var22 = this.posY + (this.fishY - this.posY) / (double)this.fishPosRotationIncrements;
         double var23 = this.posZ + (this.fishZ - this.posZ) / (double)this.fishPosRotationIncrements;
         double var7 = MathHelper.wrapAngleTo180_double(this.fishYaw - (double)this.rotationYaw);
         this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.fishPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.fishPitch - (double)this.rotationPitch) / (double)this.fishPosRotationIncrements);
         --this.fishPosRotationIncrements;
         this.setPosition(var21, var22, var23);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else {
         if (!this.worldObj.isRemote) {
            ItemStack var1 = this.angler.getHeldItemStack();
            if (this.angler.isDead || !this.angler.isEntityAlive() || var1 == null || !(var1.getItem() instanceof ItemFishingRod) || this.getDistanceSqToEntity(this.angler) > 1024.0) {
               this.setDead();
               this.angler.fishEntity = null;
               return;
            }

            if (this.bobber != null) {
               if (!this.bobber.isDead) {
                  this.setPosition(this.bobber.posX, this.bobber.boundingBox.minY + (double)this.bobber.height * 0.8, this.bobber.posZ);
                  this.setVelocity(this.bobber.motionX, this.bobber.motionY, this.bobber.motionZ);
                  return;
               }

               this.bobber = null;
            }
         }

         if (this.shake > 0) {
            --this.shake;
         }

         if (this.inGround) {
            int var19 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            if (var19 == this.inTile) {
               ++this.ticksInGround;
               if (this.ticksInGround == 1200) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else {
            ++this.ticksInAir;
         }

         Vec3 current_pos = this.worldObj.getVec3(this.posX, this.posY, this.posZ);
         Vec3 future_pos = this.worldObj.getVec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         Raycast raycast = (new Raycast(this.worldObj, current_pos, future_pos)).setOriginator(this).setForPhysicalReach().performVsBlocks();
         RaycastCollision var3 = raycast.getBlockCollision();
         if (var3 != null) {
            raycast.setLimitToBlockCollisionPoint();
         }

         if (raycast.performVsEntities().hasEntityCollisions()) {
            var3 = raycast.getNearestCollision();
         }

         if (var3 != null) {
            if (var3.isEntity()) {
               this.bobber = var3.getEntityHit();
               if (this.onServer() && this.bobber instanceof EntityLivingBase) {
                  this.bobber.attackEntityFrom(new Damage(DamageSource.causeThrownDamage(this, this.angler), 1.0F));
               }
            } else {
               this.inGround = true;
            }

            if (this.onServer()) {
               this.sendPacketToAllPlayersTrackingEntity((new Packet85SimpleSignal(EnumSignal.fish_hook_in_entity)).setInteger(this.inGround ? -1 : this.bobber.entityId).setEntityID(this));
            }
         }

         if (!this.inGround) {
            if (this.bobber == null) {
               this.moveEntity(this.motionX, this.motionY, this.motionZ);
            } else {
               this.setPosition(this.bobber.posX, this.bobber.boundingBox.minY + (double)this.bobber.height * 0.8, this.bobber.posZ);
               this.setVelocity(this.bobber.motionX, this.bobber.motionY, this.bobber.motionZ);
            }

            float var24 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);

            for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var24) * 180.0 / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
            float var25 = 0.92F;
            if (this.onGround || this.isCollidedHorizontally) {
               var25 = 0.5F;
            }

            byte var27 = 5;
            double var26 = 0.0;
            int var29 = 0;

            while(true) {
               if (var29 >= var27) {
                  if (var26 > 0.0) {
                     if (this.ticksCatchable > 0) {
                        --this.ticksCatchable;
                     } else if (this.checkForBite()) {
                        this.ticksCatchable = this.rand.nextInt(30) + 10;
                        this.ticksCatchable += 20;
                        this.motionY -= 0.20000000298023224;
                        this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                        float var30 = (float)MathHelper.floor_double(this.boundingBox.minY);

                        float var17;
                        int var15;
                        float var31;
                        for(var15 = 0; (float)var15 < 1.0F + this.width * 20.0F; ++var15) {
                           var31 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                           var17 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                           this.worldObj.spawnParticle(EnumParticle.bubble, this.posX + (double)var31, (double)(var30 + 1.0F), this.posZ + (double)var17, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
                        }

                        for(var15 = 0; (float)var15 < 1.0F + this.width * 20.0F; ++var15) {
                           var31 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                           var17 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                           this.worldObj.spawnParticle(EnumParticle.splash, this.posX + (double)var31, (double)(var30 + 1.0F), this.posZ + (double)var17, this.motionX, this.motionY, this.motionZ);
                        }
                     }
                  }

                  if (this.ticksCatchable > 0) {
                     this.motionY -= (double)(this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2;
                  }

                  double var13 = var26 * 2.0 - 1.0;
                  this.motionY += 0.03999999910593033 * var13;
                  if (var26 > 0.0) {
                     var25 = (float)((double)var25 * 0.9);
                     this.motionY *= 0.8;
                  }

                  this.motionX *= (double)var25;
                  this.motionY *= (double)var25;
                  this.motionZ *= (double)var25;
                  this.setPosition(this.posX, this.posY, this.posZ);
                  break;
               }

               double var14 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(var29 + 0) / (double)var27 - 0.125 + 0.125;
               double var16 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(var29 + 1) / (double)var27 - 0.125 + 0.125;
               AxisAlignedBB var18 = AxisAlignedBB.getAABBPool().getAABB(this.boundingBox.minX, var14, this.boundingBox.minZ, this.boundingBox.maxX, var16, this.boundingBox.maxZ);
               if (this.worldObj.isAABBInMaterial(var18, Material.water) || this.worldObj.isAABBInMaterial(var18, Material.lava)) {
                  var26 += 1.0 / (double)var27;
               }

               ++var29;
            }
         }

         if (this.isBurning() && !this.worldObj.isRemote && this.ticksExisted % 5 == 0) {
            this.angler.getHeldItemStack().tryDamageItem(DamageSource.inFire, 1, this.angler);
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("xTile", (short)this.xTile);
      par1NBTTagCompound.setShort("yTile", (short)this.yTile);
      par1NBTTagCompound.setShort("zTile", (short)this.zTile);
      par1NBTTagCompound.setByte("inTile", (byte)this.inTile);
      par1NBTTagCompound.setByte("shake", (byte)this.shake);
      par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.xTile = par1NBTTagCompound.getShort("xTile");
      this.yTile = par1NBTTagCompound.getShort("yTile");
      this.zTile = par1NBTTagCompound.getShort("zTile");
      this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
      this.shake = par1NBTTagCompound.getByte("shake") & 255;
      this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public Item getFishType() {
      if (this.rand.nextFloat() < 0.8F) {
         return Item.fishRaw;
      } else {
         int x = MathHelper.floor_double(this.posX);
         int y = MathHelper.floor_double(this.posY - 0.20000000298023224);
         int z = MathHelper.floor_double(this.posZ);
         if (this.worldObj.getBiomeGenForCoords(x, z) != BiomeGenBase.ocean) {
            return Item.fishRaw;
         } else {
            for(int dx = -16; dx <= 16; ++dx) {
               for(int dz = -16; dz <= 16; ++dz) {
                  for(int dy = -3; dy <= 0; ++dy) {
                     Block block = this.worldObj.getBlock(x + dx, y + dy, z + dz);
                     if (block == Block.dirt || block == Block.grass || block == Block.sand) {
                        return Item.fishRaw;
                     }
                  }
               }
            }

            return Item.fishLargeRaw;
         }
      }
   }

   public int catchFish() {
      if (this.worldObj.isRemote) {
         return 0;
      } else {
         byte var1 = 0;
         if (this.bobber != null) {
            double var2 = this.angler.posX - this.posX;
            double var4 = this.angler.posY - this.posY;
            double var6 = this.angler.posZ - this.posZ;
            double var8 = (double)MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
            double var10 = 0.1;
            Entity var10000 = this.bobber;
            var10000.motionX += var2 * var10;
            var10000 = this.bobber;
            var10000.motionY += var4 * var10 + (double)MathHelper.sqrt_double(var8) * 0.08;
            var10000 = this.bobber;
            var10000.motionZ += var6 * var10;
            var1 = 3;
         } else if (this.ticksCatchable > 0) {
            EntityItem var13 = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(this.getFishType()));
            double var3 = this.angler.posX - this.posX;
            double var5 = this.angler.posY - this.posY;
            double var7 = this.angler.posZ - this.posZ;
            double var9 = (double)MathHelper.sqrt_double(var3 * var3 + var5 * var5 + var7 * var7);
            double var11 = 0.1;
            var13.motionX = var3 * var11;
            var13.motionY = var5 * var11 + (double)MathHelper.sqrt_double(var9) * 0.08;
            var13.motionZ = var7 * var11;
            this.worldObj.spawnEntityInWorld(var13);
            this.angler.addStat(StatList.fishCaughtStat, 1);
            this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5, this.angler.posZ + 0.5, this.rand.nextInt(6) + 1));
            var1 = 1;
            if (DedicatedServer.tournament_type == EnumTournamentType.score) {
               DedicatedServer.getOrCreateTournamentStanding(this.angler).caught_a_fish = true;
               DedicatedServer.updateTournamentScoreOnClient(this.angler, true);
            }

            int worm_index = this.angler.inventory.getHotbarSlotContainItem(Item.wormRaw);
            if (worm_index >= 0) {
               this.angler.inventory.decrementSlotStackSize(worm_index);
            }
         }

         if (this.inGround) {
            var1 = 2;
         }

         this.setDead();
         this.angler.fishEntity = null;
         return var1;
      }
   }

   public void setDead() {
      super.setDead();
      if (this.angler != null) {
         this.angler.fishEntity = null;
      }

   }

   public boolean canCatchFire() {
      return this.isHarmedByLava();
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return this.material == null || this.material.isHarmedByLava();
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      return damage.isLavaDamage() ? null : super.attackEntityFrom(damage);
   }

   public boolean handleLavaMovement() {
      return this.worldObj.isMaterialInBB(this.boundingBox.expand(0.0, 0.0, 0.0), Material.lava);
   }
}
