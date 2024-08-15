package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWanderBackToSpawnPoint;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWitchRoaming;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Curse;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityWitch extends EntityMob implements IRangedAttackMob {
   private static final UUID field_110184_bp = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier field_110185_bq;
   private static final int[] witchDrops;
   private int witchAttackTimer;
   private EntityLivingBase summon_wolf_target;
   private int summon_wolf_countdown;
   private boolean has_summoned_wolves;
   private int curse_random_seed;

   public EntityWitch(World par1World) {
      super(par1World);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 60, 10.0F));
      this.tasks.addTask(2, new EntityAIWander(this, 1.0));
      this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(3, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.tasks.addTask(3, new EntityAIWanderBackToSpawnPoint(this, 1.0F, true));
      this.tasks.addTask(3, new EntityAIWitchRoaming(this));
      if (par1World != null && !par1World.isRemote) {
         this.curse_random_seed = (new Random()).nextInt();
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.getDataWatcher().addObject(21, (byte)0);
   }

   protected String getLivingSound() {
      return null;
   }

   protected String getHurtSound() {
      return "imported.mob.witch.hurt";
   }

   protected String getDeathSound() {
      return "imported.mob.witch.death";
   }

   protected String getLongDistanceLivingSound() {
      float chance;
      if (this.getAttackTarget() instanceof EntityPlayer) {
         chance = 0.3F;
      } else if (!this.worldObj.isDaytime()) {
         chance = 0.1F;
      } else {
         chance = 0.0F;
      }

      return this.rand.nextFloat() < chance ? "imported.mob.witch.cackle" : null;
   }

   protected float getSoundVolume(String sound) {
      return sound.equals("imported.mob.witch.cackle") ? 0.6F : 0.2F;
   }

   public void setAggressive(boolean par1) {
      this.getDataWatcher().updateObject(21, (byte)(par1 ? 1 : 0));
   }

   public boolean getAggressive() {
      return this.getDataWatcher().getWatchableObjectByte(21) == 1;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(26.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25);
   }

   public boolean isAIEnabled() {
      return true;
   }

   public void onLivingUpdate() {
      if (!this.worldObj.isRemote) {
         if (this.getAggressive()) {
            if (this.witchAttackTimer-- <= 0) {
               this.setAggressive(false);
               ItemStack var1 = this.getHeldItemStack();
               this.setCurrentItemOrArmor(0, (ItemStack)null);
               if (var1 != null && var1.itemID == Item.potion.itemID) {
                  List var2 = Item.potion.getEffects(var1);
                  if (var2 != null) {
                     Iterator var3 = var2.iterator();

                     while(var3.hasNext()) {
                        PotionEffect var4 = (PotionEffect)var3.next();
                        this.addPotionEffect(new PotionEffect(var4));
                     }
                  }
               }

               this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(field_110185_bq);
            }
         } else {
            short var5 = -1;
            if (this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(Potion.fireResistance)) {
               var5 = 16307;
            } else if (this.rand.nextFloat() < 0.01F && this.getHealth() < this.getMaxHealth()) {
               var5 = 16341;
            } else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0) {
               var5 = 16274;
            } else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0) {
               var5 = 16274;
            }

            if (var5 > -1) {
               this.setCurrentItemOrArmor(0, new ItemStack(Item.potion, 1, var5));
               this.witchAttackTimer = this.getHeldItemStack().getMaxItemUseDuration();
               this.setAggressive(true);
               AttributeInstance var6 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
               var6.removeModifier(field_110185_bq);
               var6.applyModifier(field_110185_bq);
            }
         }

         if (this.rand.nextFloat() < 7.5E-4F) {
            this.worldObj.setEntityState(this, EnumEntityState.witch_magic);
         }

         if (this.summon_wolf_countdown > 0) {
            if (--this.summon_wolf_countdown == 0) {
               this.has_summoned_wolves = this.summonWolves() > 0 || this.has_summoned_wolves;
            }
         } else if (!this.has_summoned_wolves && this.getLastHarmingEntity() instanceof EntityPlayer) {
            this.summon_wolf_target = (EntityLivingBase)this.getLastHarmingEntity();
            this.summon_wolf_countdown = 60;
         }
      }

      super.onLivingUpdate();
   }

   private int summonWolves() {
      EntityLivingBase target = (EntityLivingBase)this.worldObj.getEntityByID(this.summon_wolf_target.entityId);
      if (target != null && !target.isDead) {
         int target_x = target.getBlockPosX();
         int target_y = target.getFootBlockPosY();
         int target_z = target.getBlockPosZ();
         int max_wolves = this.rand.nextInt(3) + 1;
         int num_wolves_spawned = 0;

         for(int attempts = 0; attempts < 16; ++attempts) {
            EntityWolf wolf = (EntityWolf)((WorldServer)this.worldObj).tryCreateNewLivingEntityCloseTo(target_x, target_y, target_z, 8, 16, EntityWolf.class, EnumCreatureType.animal);
            if (wolf != null) {
               PathNavigate navigator = wolf.getNavigator();
               PathEntity path = this.worldObj.getEntityPathToXYZ(wolf, target_x, target_y, target_z, 32.0F, navigator.canPassOpenWoodenDoors, false, navigator.avoidsWater, navigator.canSwim);
               if (path != null) {
                  PathPoint final_point = path.getFinalPathPoint();
                  World var10000 = this.worldObj;
                  if (!(World.getDistanceSqFromDeltas((float)(final_point.xCoord - target_x), (float)(final_point.yCoord - target_y), (float)(final_point.zCoord - target_z)) > 2.0)) {
                     wolf.refreshDespawnCounter(-9600);
                     this.worldObj.spawnEntityInWorld(wolf);
                     wolf.onSpawnWithEgg((EntityLivingData)null);
                     wolf.setWitchAlly();
                     wolf.setAttackTarget(target);
                     wolf.entityFX(EnumEntityFX.summoned);
                     ++num_wolves_spawned;
                     if (num_wolves_spawned == max_wolves) {
                        break;
                     }
                  }
               }
            }
         }

         return num_wolves_spawned;
      } else {
         return 0;
      }
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.witch_magic) {
         for(int var2 = 0; var2 < this.rand.nextInt(35) + 10; ++var2) {
            this.worldObj.spawnParticle(EnumParticle.witchMagic, this.posX + this.rand.nextGaussian() * 0.12999999523162842, this.boundingBox.maxY + 0.5 + this.rand.nextGaussian() * 0.12999999523162842, this.posZ + this.rand.nextGaussian() * 0.12999999523162842, 0.0, 0.0, 0.0);
         }
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public float getNaturalDefense(DamageSource damage_source) {
      return damage_source.hasMagicAspect() && damage_source.isIndirect() ? 10.0F : super.getNaturalDefense(damage_source);
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(5 + damage_source.getLootingModifier()) + 1;

      for(int i = 0; i < num_drops; ++i) {
         int item_id = witchDrops[this.rand.nextInt(witchDrops.length)];
         if (item_id == Block.plantRed.blockID) {
            this.dropItemStack(new ItemStack(Block.plantRed, 1, 2));
         } else if (item_id == Item.potion.itemID) {
            int subtype = this.rand.nextInt(6);
            if (subtype == 0) {
               subtype = 8227;
            } else if (subtype == 1) {
               subtype = 8261;
            } else if (subtype == 2) {
               subtype = 16388;
            } else if (subtype == 3) {
               subtype = 16424;
            } else if (subtype == 4) {
               subtype = 16426;
            } else if (subtype == 5) {
               subtype = 16460;
            } else {
               Minecraft.setErrorMessage("dropFewItems: unhandled subtype " + subtype);
            }

            this.dropItemStack(new ItemStack(item_id, 1, subtype));
         } else {
            this.dropItem(item_id, 1);
         }
      }

   }

   public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
      if (!this.getAggressive()) {
         EntityPotion var3 = new EntityPotion(this.worldObj, this, 32732);
         var3.rotationPitch -= -20.0F;
         double var4 = par1EntityLivingBase.posX + par1EntityLivingBase.motionX - this.posX;
         double var6 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858 - this.posY;
         double var8 = par1EntityLivingBase.posZ + par1EntityLivingBase.motionZ - this.posZ;
         float var10 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
         if (var10 >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.moveSlowdown)) {
            var3.setPotionType(32698);
         } else if (par1EntityLivingBase.getHealth() >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.poison)) {
            var3.setPotionType(32660);
         } else if (var10 <= 3.0F && !par1EntityLivingBase.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25F) {
            var3.setPotionType(32696);
         }

         float distance_squared = (float)(var4 * var4 + var8 * var8);
         if (par1EntityLivingBase instanceof EntityPlayerMP) {
            float lead = (float)Math.pow((double)distance_squared, 0.5);
            lead *= 0.5F + this.rand.nextFloat();
            var4 = par1EntityLivingBase.getPredictedPosX(lead) - this.posX;
            var8 = par1EntityLivingBase.getPredictedPosZ(lead) - this.posZ;
         }

         var3.setThrowableHeading(var4, var6 + (double)(var10 * 0.2F), var8, 0.75F, 8.0F);
         double y_correction = (double)(distance_squared * 0.001F - 0.025F);
         var3.motionY += y_correction;
         var3.motionX *= 1.2000000476837158;
         var3.motionY *= 1.2000000476837158;
         var3.motionZ *= 1.2000000476837158;
         this.worldObj.spawnEntityInWorld(var3);
      }

   }

   public boolean canDespawn() {
      return false;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.has_summoned_wolves) {
         par1NBTTagCompound.setBoolean("has_summoned_wolves", this.has_summoned_wolves);
      }

      par1NBTTagCompound.setInteger("curse_random_seed", this.curse_random_seed);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.has_summoned_wolves = par1NBTTagCompound.getBoolean("has_summoned_wolves");
      if (par1NBTTagCompound.hasKey("curse_random_seed")) {
         this.curse_random_seed = par1NBTTagCompound.getInteger("curse_random_seed");
      }

   }

   public void cursePlayer(EntityPlayerMP player) {
      if (!this.worldObj.isRemote) {
         if (!(this.getHealth() <= 0.0F) && !player.is_cursed && !player.hasCursePending()) {
            int username_hash = 0;

            for(int i = 0; i < player.username.length(); ++i) {
               username_hash += player.username.charAt(i) * i;
            }

            ((WorldServer)this.worldObj).addCurse(player, this, Curse.getRandomCurse(new Random((long)(this.curse_random_seed + username_hash))), 6000);
         }
      }
   }

   public void onDeath(DamageSource par1DamageSource) {
      if (!this.worldObj.isRemote) {
         ((WorldServer)this.worldObj).removeCursesForWitch(this);
      }

      super.onDeath(par1DamageSource);
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 4;
   }

   static {
      field_110185_bq = (new AttributeModifier(field_110184_bp, "Drinking speed penalty", -0.25, 0)).setSaved(false);
      witchDrops = new int[]{Item.glowstone.itemID, Item.sugar.itemID, Item.redstone.itemID, Item.spiderEye.itemID, Item.glassBottle.itemID, Item.gunpowder.itemID, Item.stick.itemID, Item.stick.itemID, Item.knifeFlint.itemID, Item.ironNugget.itemID, Item.seeds.itemID, Item.pumpkinSeeds.itemID, Item.carrot.itemID, Item.potato.itemID, Item.onion.itemID, Block.plantYellow.blockID, Block.plantRed.blockID, Item.potion.itemID};
   }
}
