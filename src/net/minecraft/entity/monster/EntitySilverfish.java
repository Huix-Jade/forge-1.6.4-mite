package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCopperspine;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityHoarySilverfish;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityNetherspawn;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob {
   private int allySummonCooldown;

   public EntitySilverfish(World par1World) {
      super(par1World);
      this.setSize(0.3F, 0.7F);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.6000000238418579);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0);
   }

   public int getExperienceValue() {
      return this.isNormalSilverfish() ? 5 : 10;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected EntityPlayer findPlayerToAttack(float max_distance) {
      double var1 = 8.0;
      return super.findPlayerToAttack(8.0F);
   }

   protected Entity findNonPlayerToAttack(float max_distance) {
      return this.worldObj.getClosestPrey(this, (double)max_distance, true, true);
   }

   public boolean preysUpon(Entity entity) {
      return entity.isTrueAnimal() || entity instanceof EntityVillager;
   }

   protected String getLivingSound() {
      return "mob.silverfish.say";
   }

   protected String getHurtSound() {
      return "mob.silverfish.hit";
   }

   protected String getDeathSound() {
      return "mob.silverfish.kill";
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result == null) {
         return result;
      } else {
         if (result.entityWasNegativelyAffected() && this.allySummonCooldown <= 0 && (damage.getSource() instanceof EntityDamageSource || damage.getSource() == DamageSource.magic)) {
            this.allySummonCooldown = 20;
         }

         return result;
      }
   }

   protected void attackEntity(Entity par1Entity, float par2) {
      if (this.attackTime <= 0 && par2 < 1.2F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
         this.attackTime = 20;
         this.attackEntityAsMob(par1Entity);
      }

   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && target instanceof EntityLivingBase) {
            if (this.isHoarySilverfish()) {
               target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 50, 5));
            } else if (this.isCopperspine()) {
               target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.poison.id, 480, 0));
            }
         }

         return result;
      } else {
         return result;
      }
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   protected int getDropItemId() {
      return 0;
   }

   public void onUpdate() {
      this.renderYawOffset = this.rotationYaw;
      super.onUpdate();
   }

   protected void updateEntityActionState() {
      super.updateEntityActionState();
      if (!this.worldObj.isRemote) {
         if (this.allySummonCooldown > 0) {
            --this.allySummonCooldown;
            if (this.allySummonCooldown == 0) {
               int var1 = MathHelper.floor_double(this.posX);
               int var2 = MathHelper.floor_double(this.posY);
               int var3 = MathHelper.floor_double(this.posZ);
               boolean var4 = false;

               for(int var5 = 0; !var4 && var5 <= 5 && var5 >= -5; var5 = var5 <= 0 ? 1 - var5 : 0 - var5) {
                  for(int var6 = 0; !var4 && var6 <= 10 && var6 >= -10; var6 = var6 <= 0 ? 1 - var6 : 0 - var6) {
                     for(int var7 = 0; !var4 && var7 <= 10 && var7 >= -10; var7 = var7 <= 0 ? 1 - var7 : 0 - var7) {
                        int var8 = this.worldObj.getBlockId(var1 + var6, var2 + var5, var3 + var7);
                        if (var8 == Block.silverfish.blockID) {
                           if (!this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                              int var9 = this.worldObj.getBlockMetadata(var1 + var6, var2 + var5, var3 + var7);
                              Block var10 = Block.stone;
                              if (var9 == 1) {
                                 var10 = Block.cobblestone;
                              }

                              if (var9 == 2) {
                                 var10 = Block.stoneBrick;
                              }

                              this.worldObj.setBlock(var1 + var6, var2 + var5, var3 + var7, var10.blockID, 0, 3);
                           } else {
                              this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, var1 + var6, var2 + var5, var3 + var7)).setDestroyedBy(this), false);
                           }

                           Block.silverfish.dropBlockAsEntityItem((new BlockBreakInfo(this.worldObj, var1 + var6, var2 + var5, var3 + var7)).setSilverfish(this));
                           if (this.rand.nextInt(4) == 0) {
                              var4 = true;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!this.hasPath()) {
            if (this.entityToAttack == null) {
               this.updateWanderPath();
            } else {
               this.entityToAttack = null;
            }
         }
      }

   }

   public float getBlockPathWeight(int par1, int par2, int par3) {
      return this.worldObj.getBlockId(par1, par2 - 1, par3) == Block.stone.blockID ? 10.0F : super.getBlockPathWeight(par1, par2, par3);
   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      if (super.getCanSpawnHere(perform_light_check)) {
         EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity(this, 5.0, true);
         return var1 == null;
      } else {
         return false;
      }
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   public boolean isNormalSilverfish() {
      return this.getClass() == EntitySilverfish.class;
   }

   public boolean isNetherspawn() {
      return this instanceof EntityNetherspawn;
   }

   public boolean isCopperspine() {
      return this instanceof EntityCopperspine;
   }

   public boolean isHoarySilverfish() {
      return this.getClass() == EntityHoarySilverfish.class;
   }
}
