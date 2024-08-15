package net.minecraft.entity.projectile;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityGelatinousCube;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLongdead;
import net.minecraft.entity.EntityLongdeadGuardian;
import net.minecraft.entity.EntityPhaseSpider;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile {
   public int xTile = -1;
   public int yTile = -1;
   public int zTile = -1;
   private int inTile;
   private int inData;
   private boolean inGround;
   public int canBePickedUp;
   public int arrowShake;
   public Entity shootingEntity;
   private int ticksInGround;
   private int ticksInAir;
   private double damage = 2.0;
   private int knockbackStrength;
   public ItemArrow item_arrow;
   public boolean launcher_was_enchanted;
   public float speed_before_collision_sq;
   private boolean was_burning;
   private int ticks_until_next_fizz_sound;
   public boolean shot_by_dispenser;
   private Entity last_entity_harmed;

   public EntityArrow(World par1World) {
      super(par1World);
   }

   public EntityArrow(World par1World, ItemArrow item_arrow, boolean launcher_was_enchanted) {
      super(par1World);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
      this.item_arrow = item_arrow;
      this.launcher_was_enchanted = launcher_was_enchanted;
      this.damage = (double)item_arrow.getDamage();
   }

   public EntityArrow(World par1World, double par2, double par4, double par6, ItemArrow item_arrow, boolean launcher_was_enchanted) {
      super(par1World);
      this.renderDistanceWeight = 10.0;
      this.setSize(0.5F, 0.5F);
      this.setPosition(par2, par4, par6);
      this.yOffset = 0.0F;
      this.item_arrow = item_arrow;
      this.launcher_was_enchanted = launcher_was_enchanted;
      this.damage = (double)item_arrow.getDamage();
   }

   public EntityArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float velocity, float par5, ItemArrow item_arrow, boolean launcher_was_enchanted) {
      super(par1World);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = par2EntityLivingBase;
      if (par2EntityLivingBase instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.posY = par2EntityLivingBase.posY + (double)par2EntityLivingBase.getEyeHeight() - 0.10000000149011612;
      double var6 = par3EntityLivingBase.posX - par2EntityLivingBase.posX;
      double var8 = par3EntityLivingBase.boundingBox.minY + (double)(par3EntityLivingBase.height / 3.0F) - this.posY;
      double var10 = par3EntityLivingBase.posZ - par2EntityLivingBase.posZ;
      double distance_squared;
      if (this.shootingEntity instanceof EntitySkeleton) {
         distance_squared = var6 * var6 + var10 * var10;
         float lead = (float)Math.pow(distance_squared, 0.44);
         lead *= 0.5F + this.rand.nextFloat();
         var6 = par3EntityLivingBase.getPredictedPosX(lead) - par2EntityLivingBase.posX;
         var10 = par3EntityLivingBase.getPredictedPosZ(lead) - par2EntityLivingBase.posZ;
      }

      distance_squared = var6 * var6 + var10 * var10;
      double var12 = (double)MathHelper.sqrt_double(var6 * var6 + var10 * var10);
      if (var12 >= 1.0E-7) {
         float var14 = (float)(Math.atan2(var10, var6) * 180.0 / Math.PI) - 90.0F;
         float var15 = (float)(-(Math.atan2(var8, var12) * 180.0 / Math.PI));
         double var16 = var6 / var12;
         double var18 = var10 / var12;
         this.setLocationAndAngles(par2EntityLivingBase.posX + var16, this.posY, par2EntityLivingBase.posZ + var18, var14, var15);
         this.yOffset = 0.0F;
         float var20 = (float)var12 * 0.2F;
         if (par2EntityLivingBase instanceof EntitySkeleton) {
            par5 *= 1.5F;
         }

         this.setThrowableHeading(var6, var8 + (double)var20, var10, velocity, par5);
      }

      this.item_arrow = item_arrow;
      this.launcher_was_enchanted = launcher_was_enchanted;
      this.damage = (double)item_arrow.getDamage();
      if (par2EntityLivingBase instanceof EntitySkeleton) {
         double y_correction = distance_squared * 5.000000237487257E-4 * distance_squared * 5.000000237487257E-4 - 0.05000000074505806;
         if (distance_squared > 576.0) {
            y_correction += 0.05999999865889549;
         }

         this.motionY += y_correction;
         float dy = (float)par3EntityLivingBase.posY - (float)par2EntityLivingBase.posY;
         if (dy > 5.0F) {
            this.motionY += (double)((dy - 5.0F) * 0.025F) * (1.2000000476837158 - distance_squared * 5.000000237487257E-4);
         }
      }

   }

   public EntityArrow(World par1World, EntityLivingBase par2EntityLivingBase, float velocity, ItemArrow item_arrow, boolean launcher_was_enchanted) {
      super(par1World);
      this.renderDistanceWeight = 10.0;
      this.shootingEntity = par2EntityLivingBase;
      if (par2EntityLivingBase instanceof EntityPlayer) {
         this.canBePickedUp = 1;
      }

      this.setSize(0.5F, 0.5F);
      this.setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY + (double)par2EntityLivingBase.getEyeHeight(), par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
      this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F) * 0.30000001192092896;
      this.posY -= 0.10000000149011612;
      this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F) * 0.30000001192092896;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.yOffset = 0.0F;
      this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F));
      this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F));
      this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F));
      float wander;
      if (par2EntityLivingBase instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)par2EntityLivingBase;
         int effective_player_level = player.getExperienceLevel() >= 0 && par1World.areSkillsEnabled() && !player.hasSkill(Skill.ARCHERY) ? 0 : player.getExperienceLevel();
         effective_player_level += EnchantmentHelper.getEnchantmentLevelFractionOfInteger(Enchantment.true_flight, this.getLauncher(), 40);
         if (effective_player_level < 0) {
            wander = 5.0F + (float)effective_player_level * -0.5F;
         } else {
            wander = (float)(0.5 + 4.5 / Math.pow((double)(0.8F + (float)(effective_player_level + 1) / 5.0F), 2.0));
         }
      } else {
         wander = 1.0F;
      }

      if (par2EntityLivingBase.isSuspendedInLiquid()) {
         wander *= 2.0F;
      }

      this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity * 1.5F, wander);
      this.item_arrow = item_arrow;
      this.launcher_was_enchanted = launcher_was_enchanted;
      this.damage = (double)item_arrow.getDamage();
   }

   public ItemStack getLauncher() {
      if (this.shootingEntity instanceof EntityLivingBase) {
         EntityLivingBase entity_living_base = (EntityLivingBase)this.shootingEntity;
         return entity_living_base.getHeldItemStack();
      } else {
         return null;
      }
   }

   protected void entityInit() {
      this.dataWatcher.addObject(16, (byte)0);
   }

   public void setThrowableHeading(double par1, double par3, double par5, float velocity, float par8) {
      ItemStack launcher = this.getLauncher();
      if (launcher != null && launcher.getItem() == Item.bowMithril && this.shootingEntity instanceof EntityPlayer) {
         velocity *= 1.25F;
      }

      if (launcher != null && launcher.getItem() == Item.bowAncientMetal && this.shootingEntity instanceof EntityPlayer) {
         velocity *= 1.1F;
      }

      float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
      par1 /= (double)var9;
      par3 /= (double)var9;
      par5 /= (double)var9;
      par1 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)par8;
      par3 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)par8;
      par5 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * (double)par8;
      par1 *= (double)velocity;
      par3 *= (double)velocity;
      par5 *= (double)velocity;
      this.motionX = par1;
      this.motionY = par3;
      this.motionZ = par5;
      float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
      this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var10) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      this.setPosition(par1, par3, par5);
      this.setRotation(par7, par8);
   }

   public void setVelocity(double par1, double par3, double par5) {
      this.motionX = par1;
      this.motionY = par3;
      this.motionZ = par5;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
         this.prevRotationYaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
         this.prevRotationPitch = (float)(Math.atan2(par3, (double)var7) * 180.0 / Math.PI);
         this.ticksInGround = 0;
      }

   }

   public float getVelocity() {
      return MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
   }

   public void spentTickInLava() {
      if (this.inGround) {
         super.spentTickInLava();
      }

   }

   private void bounceBack() {
      this.motionX *= -0.10000000149011612;
      this.motionY *= -0.10000000149011612;
      this.motionZ *= -0.10000000149011612;
      this.rotationYaw += 180.0F;
      this.prevRotationYaw += 180.0F;
      this.ticksInAir = 0;
      this.motionX /= 4.0;
      this.motionY /= -4.0;
      this.motionZ /= 4.0;
   }

   public boolean cannotRaycastCollideWith(Entity entity) {
      return entity == this.shootingEntity && this.ticksInAir < 5 ? true : super.cannotRaycastCollideWith(entity);
   }

   public void onUpdate() {
      super.onUpdate();
      this.speed_before_collision_sq = (float)(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
         this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var1) * 180.0 / Math.PI);
      }

      int var16 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
      AxisAlignedBB bb;
      if (var16 > 0) {
         bb = Block.blocksList[var16].getCollisionBoundsCombined(this.worldObj, this.xTile, this.yTile, this.zTile, this, true);
         if (bb != null && bb.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.arrowShake > 0) {
         --this.arrowShake;
      }

      if (!this.worldObj.isRemote && this.was_burning) {
         Block block = this.inGround ? Block.getBlock(this.inTile) : null;
         if (block != null && block.blockMaterial.isFreezing()) {
            if (!this.isWet()) {
               this.causeQuenchEffect();
            }

            this.extinguish();
         } else if (this.isInWater()) {
            this.causeQuenchEffect();
         } else if (this.isWet() && --this.ticks_until_next_fizz_sound <= 0) {
            this.spawnSingleSteamParticle(true);
            this.ticks_until_next_fizz_sound = this.rand.nextInt(17) + 3;
         }
      }

      this.was_burning = this.isBurning();
      if (this.inGround) {
         int var18 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
         int var19 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
         if (var18 == this.inTile && (var19 == this.inData || this.inTile == Block.grass.blockID)) {
            ++this.ticksInGround;
            if (this.ticksInGround >= (this.shootingEntity instanceof EntityPlayer ? 24000 : 1000)) {
               this.setDead();
            }
         } else {
            this.inGround = false;
            Random rand = new Random((long)this.entityId);
            this.motionX *= (double)(rand.nextFloat() * 0.2F);
            this.motionY *= (double)(rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         }
      } else {
         ++this.ticksInAir;
         if (this.onServer()) {
            bb = this.boundingBox.copy();
            if (this.worldObj.isBoundingBoxBurning(bb.contract(0.001, 0.001, 0.001), true)) {
               this.setFire(8);
            } else if (this.worldObj.isBoundingBoxBurning(bb.contract(0.001, 0.001, 0.001).translate(this.motionX / 2.0, this.motionY / 2.0, this.motionZ / 2.0), true)) {
               this.setFire(8);
            }
         }

         Vec3 current_pos = this.worldObj.getVec3(this.posX, this.posY, this.posZ);
         Vec3 future_pos = this.worldObj.getVec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         Raycast raycast = (new Raycast(this.worldObj, current_pos, future_pos)).setForPiercingProjectile(this).performVsBlocks();
         RaycastCollision var4 = raycast.getBlockCollision();
         RaycastCollision block_collision = var4;
         if (var4 != null) {
            raycast.setLimitToBlockCollisionPoint();
         }

         if (raycast.performVsEntities().hasEntityCollisions()) {
            var4 = raycast.getNearestCollision();
         }

         if (var4 != null && var4.getEntityHit() instanceof EntityPlayer) {
            EntityPlayer var21 = (EntityPlayer)var4.getEntityHit();
            if (var21.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(var21)) {
               var4 = null;
            }
         }

         if (var4 == null || !var4.isEntity()) {
            this.last_entity_harmed = null;
         }

         float var27;
         float var11;
         float var20;
         if (var4 != null) {
            if (!var4.isEntity()) {
               this.handleCollisionWithBlock(var4);
            } else {
               Entity entity_hit = var4.getEntityHit();
               var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
               var11 = 1.0F;
               if (entity_hit instanceof EntityLivingBase) {
                  EntityLivingBase entity_living_base = (EntityLivingBase)entity_hit;
                  if (entity_living_base.isEntityUndead() && this.item_arrow.getArrowheadMaterial() == Material.silver) {
                     var11 *= 1.25F;
                  }

                  if (entity_hit instanceof EntitySkeleton) {
                     var11 *= 0.25F;
                  }
               }

               float var24 = var20 * (float)this.damage * var11;
               float min_damage;
               if (this.shootingEntity instanceof EntitySkeleton) {
                  min_damage = this.item_arrow.getDamage() * 2.0F + 2.0F;
                  if (this.shootingEntity.getClass() == EntityLongdead.class) {
                     min_damage += 2.0F;
                  } else if (this.shootingEntity.getClass() == EntityLongdeadGuardian.class) {
                     min_damage += 3.0F;
                  }

                  if (var24 < min_damage) {
                     var24 = min_damage;
                  } else if (!this.launcher_was_enchanted) {
                     var24 = min_damage;
                  }
               } else if (this.shot_by_dispenser) {
                  min_damage = this.item_arrow.getDamage() * 2.0F + 2.0F;
                  if (var24 < min_damage) {
                     var24 = min_damage;
                  }
               }

               if (this.getIsCritical()) {
                  var24 *= 1.5F + this.rand.nextFloat() * 0.5F;
               }

               DamageSource var22 = null;
               if (this.shootingEntity == null) {
                  var22 = DamageSource.causeArrowDamage(this, this);
               } else {
                  var22 = DamageSource.causeArrowDamage(this, this.shootingEntity);
               }

               if (this.isBurning() && !(entity_hit instanceof EntityEnderman)) {
                  if (entity_hit instanceof EntityGelatinousCube) {
                     if (this.onServer()) {
                        if (this.getVelocity() < 1.0F) {
                           entity_hit.attackEntityFrom(new Damage(DamageSource.inFire, 1.0F));
                           this.extinguish(true);
                        } else {
                           ++var24;
                           entity_hit.entityFX(EnumEntityFX.steam_with_hiss);
                        }
                     }
                  } else {
                     entity_hit.setFire(5);
                  }
               }

               if (entity_hit instanceof EntityGelatinousCube && ((EntityGelatinousCube)entity_hit).isAcidic() && this.item_arrow.isHarmedByAcid()) {
                  if (this.onServer()) {
                     this.entityFX(EnumEntityFX.steam_with_hiss);
                  }

                  this.setDead();
               }

               if (entity_hit instanceof EntitySkeleton && this.shootingEntity instanceof EntitySkeleton) {
                  this.setDead();
               }

               Damage damage = new Damage(var22, var24);
               boolean entity_immune_to_arrow = entity_hit.isEntityInvulnerable() || entity_hit.isImmuneTo(damage.getSource());
               if (entity_hit != this.last_entity_harmed) {
                  if (!(this.getVelocity() < 1.0F) && !entity_immune_to_arrow) {
                     if (this.onServer()) {
                        EntityDamageResult result = entity_hit.attackEntityFrom(damage);
                        if (result == null && entity_hit instanceof EntityPhaseSpider) {
                           if (block_collision != null) {
                              this.handleCollisionWithBlock(block_collision);
                           }
                        } else if (result != null && result.entityWasNegativelyAffected()) {
                           this.last_entity_harmed = entity_hit;
                           if (entity_hit instanceof EntityLivingBase) {
                              if (this.shootingEntity instanceof EntityLivingBase) {
                                 ((EntityLivingBase)this.shootingEntity).setLastAttackTarget(entity_hit);
                              }

                              EntityLivingBase var25 = (EntityLivingBase)entity_hit;
                              var25.setArrowCountInEntity(var25.getArrowCountInEntity() + 1);
                              ItemStack launcher = this.getLauncher();
                              if (launcher != null && this.rand.nextFloat() < launcher.getEnchantmentLevelFraction(Enchantment.poison)) {
                                 var25.addPotionEffect(new PotionEffect(Potion.poison.id, 160 + launcher.getEnchantmentLevelFractionOfInteger(Enchantment.poison, 240), 0));
                              }

                              if (this.knockbackStrength > 0) {
                                 var27 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                                 if (var27 > 0.0F) {
                                    entity_hit.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579 / (double)var27, 0.1, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579 / (double)var27);
                                 }
                              }

                              if (this.shootingEntity != null) {
                                 if (this.worldObj.isRemote) {
                                    System.out.println("EntityArrow.onUpdate() is calling EnchantmentThorns.func_92096_a() on client");
                                    Minecraft.temp_debug = "arrow";
                                 }

                                 EnchantmentThorns.func_92096_a(this.shootingEntity, var25, this.rand);
                              }

                              if (this.shootingEntity != null && entity_hit != this.shootingEntity && entity_hit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                                 ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(6, 0));
                              }
                           }

                           this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                           if (!(entity_hit instanceof EntityEnderman)) {
                              this.setDead();
                           }

                           if (this.shootingEntity != null && entity_hit instanceof EntityPlayer) {
                              this.shootingEntity.refreshDespawnCounter(-9600);
                           }
                        } else {
                           this.bounceBack();
                        }
                     }
                  } else {
                     this.bounceBack();
                  }
               }
            }
         }

         if (this.getIsCritical()) {
            for(int var9 = 0; var9 < 4; ++var9) {
               this.worldObj.spawnParticle(EnumParticle.crit, this.posX + this.motionX * (double)var9 / 4.0, this.posY + this.motionY * (double)var9 / 4.0, this.posZ + this.motionZ * (double)var9 / 4.0, -this.motionX, -this.motionY + 0.2, -this.motionZ);
            }
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);

         for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var20) * 180.0 / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
         float var23 = 0.99F;
         var11 = 0.05F;
         if (this.isInWater()) {
            for(int var26 = 0; var26 < 4; ++var26) {
               var27 = 0.25F;
               this.worldObj.spawnParticle(EnumParticle.bubble, this.posX - this.motionX * (double)var27, this.posY - this.motionY * (double)var27, this.posZ - this.motionZ * (double)var27, this.motionX, this.motionY, this.motionZ);
            }

            var23 = 0.8F;
         }

         this.motionX *= (double)var23;
         this.motionY *= (double)var23;
         this.motionZ *= (double)var23;
         this.motionY -= (double)var11;
         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }

   }

   private void handleCollisionWithBlock(RaycastCollision var4) {
      this.xTile = var4.block_hit_x;
      this.yTile = var4.block_hit_y;
      this.zTile = var4.block_hit_z;
      this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
      this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
      this.motionX = (double)((float)(var4.position_hit.xCoord - this.posX));
      this.motionY = (double)((float)(var4.position_hit.yCoord - this.posY));
      this.motionZ = (double)((float)(var4.position_hit.zCoord - this.posZ));
      float var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.posX -= this.motionX / (double)var20 * 0.05000000074505806;
      this.posY -= this.motionY / (double)var20 * 0.05000000074505806;
      this.posZ -= this.motionZ / (double)var20 * 0.05000000074505806;
      this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
      this.inGround = true;
      this.arrowShake = 7;
      this.setIsCritical(false);
      if (this.inTile != 0) {
         Block.blocksList[this.inTile].onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
      }

      if (this.onServer()) {
         this.sendPacketToAllPlayersTrackingEntity((new Packet85SimpleSignal(EnumSignal.arrow_hit_block)).setEntityID(this).setExactPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ));
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("xTile", (short)this.xTile);
      par1NBTTagCompound.setShort("yTile", (short)this.yTile);
      par1NBTTagCompound.setShort("zTile", (short)this.zTile);
      par1NBTTagCompound.setByte("inTile", (byte)this.inTile);
      par1NBTTagCompound.setByte("inData", (byte)this.inData);
      par1NBTTagCompound.setByte("shake", (byte)this.arrowShake);
      par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
      par1NBTTagCompound.setByte("pickup", (byte)this.canBePickedUp);
      par1NBTTagCompound.setDouble("damage", this.damage);
      par1NBTTagCompound.setInteger("arrow_item_id", this.item_arrow.itemID);
      par1NBTTagCompound.setBoolean("launcher_was_enchanted", this.launcher_was_enchanted);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.xTile = par1NBTTagCompound.getShort("xTile");
      this.yTile = par1NBTTagCompound.getShort("yTile");
      this.zTile = par1NBTTagCompound.getShort("zTile");
      this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
      this.inData = par1NBTTagCompound.getByte("inData") & 255;
      this.arrowShake = par1NBTTagCompound.getByte("shake") & 255;
      this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
      if (par1NBTTagCompound.hasKey("damage")) {
         this.damage = par1NBTTagCompound.getDouble("damage");
      }

      if (par1NBTTagCompound.hasKey("pickup")) {
         this.canBePickedUp = par1NBTTagCompound.getByte("pickup");
      } else if (par1NBTTagCompound.hasKey("player")) {
         this.canBePickedUp = par1NBTTagCompound.getBoolean("player") ? 1 : 0;
      }

      this.item_arrow = (ItemArrow)Item.itemsList[par1NBTTagCompound.getInteger("arrow_item_id")];
      this.launcher_was_enchanted = par1NBTTagCompound.getBoolean("launcher_was_enchanted");
   }

   public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
      if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
         boolean var2 = this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;
         if (this.canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(new ItemStack(this.item_arrow, 1))) {
            var2 = false;
         }

         if (var2) {
            this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            par1EntityPlayer.onItemPickup(this, 1);
            this.setDead();
         }
      }

   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public float getShadowSize() {
      return 0.0F;
   }

   public void setDamage(double par1) {
      this.damage = par1;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setKnockbackStrength(int par1) {
      this.knockbackStrength = par1;
   }

   public boolean canAttackWithItem() {
      return false;
   }

   public void setIsCritical(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(var2 | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var2 & -2));
      }

   }

   public boolean getIsCritical() {
      byte var1 = this.dataWatcher.getWatchableObjectByte(16);
      return (var1 & 1) != 0;
   }

   public Item getModelItem() {
      return this.item_arrow;
   }

   public void setInGround() {
      this.inGround = true;
   }

   public boolean isInGround() {
      return this.inGround;
   }

   public void setInTile(int inTile) {
      this.inTile = inTile;
   }

   public void setInData(int inData) {
      this.inData = inData;
   }

   public int getInTile() {
      return this.inTile;
   }

   public int getInData() {
      return this.inData;
   }

   public void scaleVelocity(float amount) {
      this.motionX *= (double)amount;
      this.motionY *= (double)amount;
      this.motionZ *= (double)amount;
   }
}
