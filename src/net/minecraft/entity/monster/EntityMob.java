package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
   public EntityMob(World par1World) {
      super(par1World);
   }

   public void onLivingUpdate() {
      this.updateArmSwingProgress();
      super.onLivingUpdate();
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0) {
         this.setDead();
      }

   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityWasNegativelyAffected()) {
            Entity var3 = damage.getResponsibleEntity();
            if (this.riddenByEntity != var3 && this.ridingEntity != var3 && var3 != this) {
               this.entityToAttack = var3;
            }
         }

         return result;
      } else {
         return result;
      }
   }

   public static EntityDamageResult attackEntityAsMob(EntityLiving attacker, Entity target) {
      if (attacker.isDecoy()) {
         return null;
      } else if (target instanceof EntityPlayer && target.getAsPlayer().isImmuneByGrace()) {
         return null;
      } else {
         ItemStack held_item = attacker.getHeldItemStack();
         Damage damage = new Damage(DamageSource.causeMobDamage(attacker), (float)attacker.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
         if (attacker.isFrenzied()) {
            damage.addAmount((float)attacker.getEntityAttributeBaseValue(SharedMonsterAttributes.attackDamage) * 0.5F);
         }

         int knockback_bonus = 0;
         if (target.isEntityLivingBase()) {
            damage.addAmount(EnchantmentDamage.getDamageModifiers(held_item, target.getAsEntityLivingBase()));
            knockback_bonus += EnchantmentHelper.getKnockbackModifier(attacker, target.getAsEntityLivingBase());
         }

         int fire_aspect = EnchantmentHelper.getFireAspectModifier(attacker);
         EntityDamageResult result = target.attackEntityFrom(damage.setFireAspect(fire_aspect > 0));
         if (result == null) {
            return result;
         } else {
            if (result.entityWasNegativelyAffected()) {
               if (knockback_bonus > 0) {
                  target.addVelocity((double)(-MathHelper.sin(attacker.rotationYaw * 3.1415927F / 180.0F) * (float)knockback_bonus * 0.5F), 0.1, (double)(MathHelper.cos(attacker.rotationYaw * 3.1415927F / 180.0F) * (float)knockback_bonus * 0.5F));
                  attacker.motionX *= 0.6;
                  attacker.motionZ *= 0.6;
               }

               if (fire_aspect > 0) {
                  target.setFire(fire_aspect * 4);
               }

               if (attacker.isBurning() && !attacker.hasHeldItem() && attacker.rand.nextFloat() < (float)attacker.worldObj.difficultySetting * 0.3F) {
                  target.setFire(2 * attacker.worldObj.difficultySetting);
               }

               if (target.isEntityLivingBase()) {
                  if (attacker.worldObj.isRemote) {
                     System.out.println("EntityMob.attackEntityAsMob() is calling EnchantmentThorns.func_92096_a() on client");
                     Minecraft.temp_debug = "mob";
                  }

                  EnchantmentThorns.func_92096_a(attacker, target.getAsEntityLivingBase(), attacker.rand);
                  int stunning = EnchantmentHelper.getStunModifier(attacker, target.getAsEntityLivingBase());
                  if ((double)stunning > Math.random() * 10.0) {
                     target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, stunning * 50, stunning * 5));
                  }

                  attacker.heal((float)EnchantmentHelper.getVampiricTransfer(attacker, target.getAsEntityLivingBase(), result.getAmountOfHealthLost()), EnumEntityFX.vampiric_gain);
               }

               if (target instanceof EntityPlayer) {
                  attacker.refreshDespawnCounter(-9600);
               }
            }

            return result;
         }
      }
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      if (this.isDecoy()) {
         return null;
      } else if (target instanceof EntityPlayer && target.getAsPlayer().isImmuneByGrace()) {
         return null;
      } else {
         ItemStack held_item = this.getHeldItemStack();
         Damage damage = new Damage(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
         if (this.isFrenzied()) {
            damage.addAmount((float)this.getEntityAttributeBaseValue(SharedMonsterAttributes.attackDamage) * 0.5F);
         }

         int knockback_bonus = 0;
         if (target.isEntityLivingBase()) {
            damage.addAmount(EnchantmentDamage.getDamageModifiers(held_item, target.getAsEntityLivingBase()));
            knockback_bonus += EnchantmentHelper.getKnockbackModifier(this, target.getAsEntityLivingBase());
         }

         int fire_aspect = EnchantmentHelper.getFireAspectModifier(this);
         EntityDamageResult result = target.attackEntityFrom(damage.setFireAspect(fire_aspect > 0));
         if (result == null) {
            return result;
         } else {
            if (result.entityWasNegativelyAffected()) {
               if (knockback_bonus > 0) {
                  target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F) * (float)knockback_bonus * 0.5F), 0.1, (double)(MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F) * (float)knockback_bonus * 0.5F));
                  this.motionX *= 0.6;
                  this.motionZ *= 0.6;
               }

               if (fire_aspect > 0) {
                  target.setFire(fire_aspect * 4);
               }

               if (this.isBurning() && !this.hasHeldItem() && this.rand.nextFloat() < (float)this.worldObj.difficultySetting * 0.3F) {
                  target.setFire(2 * this.worldObj.difficultySetting);
               }

               if (target.isEntityLivingBase()) {
                  if (this.worldObj.isRemote) {
                     System.out.println("EntityMob.attackEntityAsMob() is calling EnchantmentThorns.func_92096_a() on client");
                     Minecraft.temp_debug = "mob";
                  }

                  EnchantmentThorns.func_92096_a(this, target.getAsEntityLivingBase(), this.rand);
                  int stunning = EnchantmentHelper.getStunModifier(this, target.getAsEntityLivingBase());
                  if ((double)stunning > Math.random() * 10.0) {
                     target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, stunning * 50, stunning * 5));
                  }

                  this.heal((float)EnchantmentHelper.getVampiricTransfer(this, target.getAsEntityLivingBase(), result.getAmountOfHealthLost()), EnumEntityFX.vampiric_gain);
               }

               if (target instanceof EntityPlayer) {
                  this.refreshDespawnCounter(-9600);
               }
            }

            return result;
         }
      }
   }

   protected void attackEntity(Entity par1Entity, float par2) {
      if (this.attackTime <= 0 && par2 < 1.75F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
         if (this.getHeldItemStack() != null) {
            this.swingArm();
         }

         this.attackTime = 20;
         this.attackEntityAsMob(par1Entity);
      }

   }

   public float getBlockPathWeight(int par1, int par2, int par3) {
      return this.came_from_spawner ? 0.7F - this.worldObj.getLightBrightness(par1, par2, par3) : 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
   }

   public static boolean isValidLightLevel(EntityLiving entity_living) {
      if (entity_living.came_from_spawner) {
         return entity_living.getChanceOfCatchingFireFromSunlightThisTick() == 0.0F;
      } else {
         World world = entity_living.worldObj;
         Random rand = entity_living.rand;
         int x = MathHelper.floor_double(entity_living.posX);
         int y = MathHelper.floor_double(entity_living.boundingBox.minY);
         int z = MathHelper.floor_double(entity_living.posZ);
         if (world.getBlockMaterial(x, y, z).isLiquid()) {
            ++y;
         }

         if (world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > rand.nextInt(32)) {
            return false;
         } else {
            if (!world.isRemote && world.getBiomeGenForCoords(x, z).rainfall == 0.0F && !world.isBloodMoon24HourPeriod()) {
               world.ignore_rain_and_thunder_for_next_BLV = true;
            }

            int blv = entity_living.worldObj.getBlockLightValue(x, y, z);
            if (world.isThundering(true) && (world.getBiomeGenForCoords(x, z).rainfall > 0.0F || world.isBloodMoon24HourPeriod())) {
               int var5 = world.skylightSubtracted;
               world.skylightSubtracted = 10;
               blv = world.getBlockLightValue(x, y, z);
               world.skylightSubtracted = var5;
            }

            return blv <= rand.nextInt(entity_living.isUnderOpenSky() ? 8 : 5);
         }
      }
   }

   protected boolean isValidLightLevel() {
      return isValidLightLevel(this);
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return this.worldObj.difficultySetting > 0 && (!perform_light_check || this.isValidLightLevel()) && super.getCanSpawnHere(perform_light_check);
   }

   public boolean canSpawnInShallowWater() {
      return true;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 32.0);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage);
   }

   public boolean canDespawn() {
      if (!super.canDespawn()) {
         return false;
      } else {
         EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, 48.0, true);
         if (player != null && this.canPathTo(player.getBlockPosX(), player.getFootBlockPosY(), player.getBlockPosZ(), 48)) {
            this.refreshDespawnCounter(-1200);
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean isFrenzied() {
      return this.worldObj.isBloodMoon(true);
   }
}
