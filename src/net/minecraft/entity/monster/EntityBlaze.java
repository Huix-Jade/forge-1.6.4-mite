package net.minecraft.entity.monster;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlaze extends EntityMob {
   private float heightOffset = 0.5F;
   private int heightOffsetUpdateTime;
   private int field_70846_g;

   public EntityBlaze(World par1World) {
      super(par1World);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   protected String getLivingSound() {
      return "mob.blaze.breathe";
   }

   protected String getHurtSound() {
      return "mob.blaze.hit";
   }

   protected String getDeathSound() {
      return "mob.blaze.death";
   }

   public int getBrightnessForRender(float par1) {
      return 15728880;
   }

   public float getBrightness(float par1) {
      return 1.0F;
   }

   public void onLivingUpdate() {
      if (!this.worldObj.isRemote) {
         if (this.isWet()) {
            this.attackEntityFrom(new Damage(DamageSource.water, 1.0F));
         }

         --this.heightOffsetUpdateTime;
         if (this.heightOffsetUpdateTime <= 0) {
            this.heightOffsetUpdateTime = 100;
            this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
         }

         if (this.getEntityToAttack() != null && this.getEntityToAttack().posY + (double)this.getEntityToAttack().getEyeHeight() > this.posY + (double)this.getEyeHeight() + (double)this.heightOffset) {
            this.motionY += (0.30000001192092896 - this.motionY) * 0.30000001192092896;
         }
      }

      if (this.rand.nextInt(24) == 0) {
         this.worldObj.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
      }

      if (!this.onGround && this.motionY < 0.0) {
         this.motionY *= 0.6;
      }

      for(int var1 = 0; var1 < 2; ++var1) {
         this.worldObj.spawnParticle(EnumParticle.largesmoke, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, 0.0, 0.0, 0.0);
      }

      super.onLivingUpdate();
   }

   protected void attackEntity(Entity par1Entity, float par2) {
      if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
         this.attackTime = 20;
         this.attackEntityAsMob(par1Entity);
      } else if (par2 < 30.0F) {
         double var3 = par1Entity.posX - this.posX;
         double var5 = par1Entity.boundingBox.minY + (double)(par1Entity.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
         double var7 = par1Entity.posZ - this.posZ;
         World var10000 = this.worldObj;
         float lead = (float)Math.pow(World.getDistanceSqFromDeltas(var3, var5, var7), 0.42) * 2.0F;
         lead *= Math.min(0.5F + this.rand.nextFloat(), 1.0F);
         var3 = par1Entity.getPredictedPosX(lead) - this.posX;
         var7 = par1Entity.getPredictedPosZ(lead) - this.posZ;
         if (this.attackTime == 0) {
            ++this.field_70846_g;
            if (this.field_70846_g == 1) {
               this.attackTime = 20;
               this.func_70844_e(true);
            } else if (this.field_70846_g <= 4) {
               this.attackTime = 6;
            } else {
               this.attackTime = 20;
               this.field_70846_g = 0;
               this.func_70844_e(false);
            }

            if (this.field_70846_g > 1) {
               float var9 = MathHelper.sqrt_float(par2) * 0.5F;
               var9 *= 0.5F;
               this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);

               for(int var10 = 0; var10 < 1; ++var10) {
                  EntitySmallFireball var11 = new EntitySmallFireball(this.worldObj, this, var3 + this.rand.nextGaussian() * (double)var9, var5, var7 + this.rand.nextGaussian() * (double)var9);
                  var11.posY = this.posY + (double)(this.height / 2.0F) + 0.5;
                  this.worldObj.spawnEntityInWorld(var11);
               }
            }
         }

         this.rotationYaw = (float)(Math.atan2(var7, var3) * 180.0 / Math.PI) - 90.0F;
         this.hasAttacked = true;
      }

   }

   protected void fall(float par1) {
   }

   protected int getDropItemId() {
      return Item.blazeRod.itemID;
   }

   public boolean isBurning() {
      return this.func_70845_n();
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (recently_hit_by_player) {
         int num_drops = this.rand.nextInt(2 + damage_source.getLootingModifier());

         for(int i = 0; i < num_drops; ++i) {
            this.dropItem(Item.blazeRod.itemID, 1);
         }
      }

   }

   public boolean func_70845_n() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void func_70844_e(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 &= -2;
      }

      this.dataWatcher.updateObject(16, var2);
   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 4;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public boolean isComfortableInLava() {
      return true;
   }

   public boolean canSpawnInShallowWater() {
      return false;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      if (!damage_source.isWater() && !(damage_source.getImmediateEntity() instanceof EntitySnowball)) {
         if (damage_source.hasMagicAspect()) {
            if (damage_source.isArrowDamage()) {
               EntityArrow arrow = (EntityArrow)damage_source.getImmediateEntity();
               if (arrow.getLauncher() == null || !arrow.getLauncher().hasEnchantment(Enchantment.flame, true)) {
                  return false;
               }
            } else {
               ItemStack item_stack = damage_source.getItemAttackedWith();
               if (item_stack == null || !item_stack.hasEnchantment(Enchantment.fireAspect, true)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
