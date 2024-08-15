package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidPotentialPredators;
import net.minecraft.entity.ai.EntityAIFleeAttackerOrPanic;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIGetOutOfWater;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISeekShelterFromRain;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHorseArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
   private static final IEntitySelector horseBreedingSelector = new EntityHorseBredSelector();
   private static final Attribute horseJumpStrength = (new RangedAttribute("horse.jumpStrength", 0.7, 0.0, 2.0)).func_111117_a("Jump Strength").setShouldWatch(true);
   private static final String[] horseArmorTextures;
   private static final String[] field_110273_bx = new String[]{"", "cop", "sil", "goo", "meo", "dio", "ada", "anc"};
   private static final ItemHorseArmor[] armors;
   private static final String[] horseTextures;
   private static final String[] field_110269_bA;
   private static final String[] horseMarkingTextures;
   private static final String[] field_110292_bC;
   private int eatingHaystackCounter;
   private int openMouthCounter;
   private int jumpRearingCounter;
   public int field_110278_bp;
   public int field_110279_bq;
   protected boolean horseJumping;
   private AnimalChest horseChest;
   private boolean hasReproduced;
   protected int temper;
   protected float jumpPower;
   private boolean field_110294_bI;
   private float headLean;
   private float prevHeadLean;
   private float rearingAmount;
   private float prevRearingAmount;
   private float mouthOpenness;
   private float prevMouthOpenness;
   private int field_110285_bP;
   private String field_110286_bQ;
   private String[] field_110280_bR = new String[3];
   private int rebellious_for_eating_counter;
   private int data_object_id_is_rebellious_for_eating;
   private int rebellious_for_riding_counter;
   private int data_object_id_is_rebellious_for_riding;

   public EntityHorse(World par1World) {
      super(par1World);
      this.setSize(1.4F, 1.6F);
      this.setChested(false);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAIFollowParent(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 0.9));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(1, new EntityAIFleeAttackerOrPanic(this, 1.2F, 0.25F, true));
      this.tasks.addTask(2, new EntityAIAvoidPotentialPredators(this, 1.05F, true));
      this.tasks.addTask(4, new EntityAISeekShelterFromRain(this, 1.0F, true));
      this.tasks.addTask(4, new EntityAIGetOutOfWater(this, 1.0F));
      this.func_110226_cD();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(19, (byte)0);
      this.dataWatcher.addObject(20, 0);
      this.dataWatcher.addObject(21, String.valueOf(""));
      this.dataWatcher.addObject(22, 0);
      this.data_object_id_is_rebellious_for_eating = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
      this.data_object_id_is_rebellious_for_riding = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
   }

   public void setHorseType(int par1) {
      this.dataWatcher.updateObject(19, (byte)par1);
      this.func_110230_cF();
   }

   public int getHorseType() {
      return this.dataWatcher.getWatchableObjectByte(19);
   }

   public void setHorseVariant(int par1) {
      this.dataWatcher.updateObject(20, par1);
      this.func_110230_cF();
   }

   public int getHorseVariant() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   public boolean isRebelliousForEating() {
      if (this.worldObj.isRemote) {
         return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_rebellious_for_eating) != 0;
      } else {
         return this.rebellious_for_eating_counter > 0;
      }
   }

   public boolean setRebelliousForEatingCounter(int rebellious_for_eating_counter) {
      boolean was_rebellious_for_eating = this.isRebelliousForEating();
      this.rebellious_for_eating_counter = rebellious_for_eating_counter;
      boolean is_rebellious_for_eating = this.isRebelliousForEating();
      if (is_rebellious_for_eating != was_rebellious_for_eating) {
         this.dataWatcher.updateObject(this.data_object_id_is_rebellious_for_eating, (byte)(is_rebellious_for_eating ? -1 : 0));
      }

      return is_rebellious_for_eating;
   }

   public boolean isRebelliousForRiding() {
      if (this.worldObj.isRemote) {
         return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_rebellious_for_riding) != 0;
      } else {
         return this.rebellious_for_riding_counter > 0;
      }
   }

   public boolean setRebelliousForRidingCounter(int rebellious_for_riding_counter) {
      boolean was_rebellious_for_riding = this.isRebelliousForRiding();
      this.rebellious_for_riding_counter = rebellious_for_riding_counter;
      boolean is_rebellious_for_riding = this.isRebelliousForRiding();
      if (is_rebellious_for_riding != was_rebellious_for_riding) {
         this.dataWatcher.updateObject(this.data_object_id_is_rebellious_for_riding, (byte)(is_rebellious_for_riding ? -1 : 0));
      }

      return is_rebellious_for_riding;
   }

   public String getEntityName() {
      if (this.hasCustomNameTag()) {
         return this.getCustomNameTag();
      } else {
         int var1 = this.getHorseType();
         switch (var1) {
            case 0:
            default:
               return StatCollector.translateToLocal("entity.horse.name");
            case 1:
               return StatCollector.translateToLocal("entity.donkey.name");
            case 2:
               return StatCollector.translateToLocal("entity.mule.name");
            case 3:
               return StatCollector.translateToLocal("entity.zombiehorse.name");
            case 4:
               return StatCollector.translateToLocal("entity.skeletonhorse.name");
         }
      }
   }

   private boolean getHorseWatchableBoolean(int par1) {
      return (this.dataWatcher.getWatchableObjectInt(16) & par1) != 0;
   }

   private void setHorseWatchableBoolean(int par1, boolean par2) {
      int var3 = this.dataWatcher.getWatchableObjectInt(16);
      if (par2) {
         this.dataWatcher.updateObject(16, var3 | par1);
      } else {
         this.dataWatcher.updateObject(16, var3 & ~par1);
      }

   }

   public boolean isAdultHorse() {
      return !this.isChild();
   }

   public boolean isTame() {
      return this.getHorseWatchableBoolean(2);
   }

   public String getOwnerName() {
      return this.dataWatcher.getWatchableObjectString(21);
   }

   public void setOwnerName(String par1Str) {
      this.dataWatcher.updateObject(21, par1Str);
   }

   public float getHorseSize() {
      int var1 = this.getGrowingAge();
      return var1 >= 0 ? 1.0F : 0.5F + (float)(EntityAgeable.getGrowingAgeOfNewborn() - var1) / (float)EntityAgeable.getGrowingAgeOfNewborn() * 0.5F;
   }

   public void setScaleForAge(boolean par1) {
      if (par1) {
         this.setScale(this.getHorseSize());
      } else {
         this.setScale(1.0F);
      }

   }

   public boolean isHorseJumping() {
      return this.horseJumping;
   }

   public void setHorseTamed(boolean par1) {
      this.setHorseWatchableBoolean(2, par1);
      if (par1 && this.onServer()) {
         this.setRebelliousForEatingCounter(0);
         this.setRebelliousForRidingCounter(0);
      }

   }

   public void setHorseJumping(boolean par1) {
      this.horseJumping = par1;
   }

   public boolean allowLeashing() {
      return !this.isHorseUndead() && super.allowLeashing();
   }

   protected void func_142017_o(float par1) {
      if (par1 > 6.0F && this.isEatingHaystack()) {
         this.setEatingHaystack(false);
      }

   }

   public boolean isChested() {
      return this.getHorseWatchableBoolean(8);
   }

   public int func_110241_cb() {
      return this.dataWatcher.getWatchableObjectInt(22);
   }

   public int getHorseArmorIndex(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return 0;
      } else {
         Item item = Item.itemsList[par1ItemStack.itemID];

         for(int i = 0; i < armors.length; ++i) {
            if (armors[i] == item) {
               return i;
            }
         }

         return 0;
      }
   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(32);
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(64);
   }

   public boolean func_110205_ce() {
      return this.getHorseWatchableBoolean(16);
   }

   public boolean getHasReproduced() {
      return this.hasReproduced;
   }

   public void func_110236_r(int par1) {
      this.dataWatcher.updateObject(22, par1);
      this.func_110230_cF();
   }

   public void func_110242_l(boolean par1) {
      this.setHorseWatchableBoolean(16, par1);
   }

   public void setChested(boolean par1) {
      this.setHorseWatchableBoolean(8, par1);
   }

   public void setHasReproduced(boolean par1) {
      this.hasReproduced = par1;
   }

   public void setHorseSaddled(boolean par1) {
      this.setHorseWatchableBoolean(4, par1);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int par1) {
      this.temper = par1;
   }

   public int increaseTemper(int par1) {
      int var2 = MathHelper.clamp_int(this.getTemper() + par1, 0, this.getMaxTemper());
      this.setTemper(var2);
      return var2;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      if (this.riddenByEntity != null && this.riddenByEntity.equals(damage.getResponsibleEntity())) {
         return null;
      } else {
         EntityDamageResult result = super.attackEntityFrom(damage);
         if (result != null && !result.entityWasDestroyed()) {
            if (result.getAmountOfHealthLost() >= 4.0F && damage.getResponsibleEntity() instanceof EntityLiving && this.riddenByEntity != null && this.rand.nextInt(2) == 0) {
               if (this.onServer()) {
                  this.jumpRearingCounter = 1;
                  this.setEatingHaystack(false);
                  this.setHorseWatchableBoolean(64, true);
               }

               String var1 = this.getAngrySoundName();
               if (var1 != null) {
                  this.makeSound(var1);
               }

               if (this.riddenByEntity instanceof EntityLivingBase && this.rand.nextInt(3) == 0) {
                  this.riddenByEntity.getAsEntityLivingBase().mountEntity((Entity)null);
               }
            }

            if (result.entityWasNegativelyAffected() && damage.wasCausedByPlayer() && !this.isTame()) {
               this.increaseTemper(-10);
            }

            if (result.entityWasNegativelyAffected() && this.isEatingHaystack()) {
               this.setEatingHaystack(false);
            }

            return result;
         } else {
            return result;
         }
      }
   }

   public ItemStack[] getWornItems() {
      return new ItemStack[]{this.getSaddle(), this.getBarding()};
   }

   public ItemStack getSaddle() {
      return this.isHorseSaddled() ? new ItemStack(Item.saddle) : null;
   }

   public ItemStack getBarding() {
      Item barding_item = this.getBardingItem();
      return barding_item == null ? null : new ItemStack(barding_item);
   }

   public float getProtectionFromArmor(DamageSource damage_source, boolean include_enchantments) {
      return ItemArmor.getTotalArmorProtection(this.getWornItems(), damage_source, include_enchantments, this);
   }

   public ItemHorseArmor getBardingItem() {
      return armors[this.func_110241_cb()];
   }

   public boolean canBePushed() {
      return this.riddenByEntity == null;
   }

   public boolean prepareChunkForSpawn() {
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.posZ);
      this.worldObj.getBiomeGenForCoords(var1, var2);
      return true;
   }

   public void dropChests() {
      if (!this.worldObj.isRemote && this.isChested()) {
         this.dropItem(Block.chest.blockID, 1);
         this.setChested(false);
      }

   }

   private void func_110266_cB() {
      this.openHorseMouth();
   }

   protected void fall(float par1) {
      if (!this.onClient()) {
         if (par1 > 1.0F) {
            this.playSound("mob.horse.land", 0.4F, 1.0F);
         }

         float[] damages = new float[2];
         this.calcFallDamage(par1, damages);
         float var2 = damages[1];
         if (var2 >= 1.0F) {
            this.attackEntityFrom(new Damage(DamageSource.fall, var2));
            if (this.riddenByEntity != null) {
               this.riddenByEntity.attackEntityFrom(new Damage(DamageSource.fall, var2 * 0.5F));
            }

            int var3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2 - (double)this.prevRotationYaw), MathHelper.floor_double(this.posZ));
            if (var3 > 0) {
               StepSound var4 = Block.blocksList[var3].stepSound;
               this.worldObj.playSoundAtEntity(this, var4.getStepSound(), var4.getVolume() * 0.5F, var4.getPitch() * 0.75F);
            }
         }

      }
   }

   private int func_110225_cC() {
      int var1 = this.getHorseType();
      return !this.isChested() || var1 != 1 && var1 != 2 ? 2 : 17;
   }

   private void func_110226_cD() {
      AnimalChest var1 = this.horseChest;
      this.horseChest = new AnimalChest("HorseChest", this.func_110225_cC());
      this.horseChest.func_110133_a(this.getEntityName());
      if (var1 != null) {
         var1.func_110132_b(this);
         int var2 = Math.min(var1.getSizeInventory(), this.horseChest.getSizeInventory());

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1.getStackInSlot(var3);
            if (var4 != null) {
               this.horseChest.setInventorySlotContents(var3, var4.copy());
            }
         }

         var1 = null;
      }

      this.horseChest.func_110134_a(this);
      this.func_110232_cE();
   }

   private void func_110232_cE() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);
         if (this.isNormalHorse()) {
            this.func_110236_r(this.getHorseArmorIndex(this.horseChest.getStackInSlot(1)));
         }
      }

   }

   public void onInventoryChanged(InventoryBasic par1InventoryBasic) {
      int var2 = this.func_110241_cb();
      boolean var3 = this.isHorseSaddled();
      this.func_110232_cE();
      if (this.ticksExisted > 20) {
         if (var2 != this.func_110241_cb() && this.func_110241_cb() != 0) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         }

         if (!var3 && this.isHorseSaddled()) {
            this.playSound("mob.horse.leather", 0.5F, 1.0F);
         }
      }

   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      this.prepareChunkForSpawn();
      return super.getCanSpawnHere(perform_light_check);
   }

   protected EntityHorse getClosestHorse(Entity par1Entity, double par2) {
      double var4 = Double.MAX_VALUE;
      Entity var6 = null;
      List var7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(par1Entity, par1Entity.boundingBox.addCoord(par2, par2, par2), horseBreedingSelector);
      Iterator var8 = var7.iterator();

      while(var8.hasNext()) {
         Entity var9 = (Entity)var8.next();
         double var10 = var9.getDistanceSq(par1Entity.posX, par1Entity.posY, par1Entity.posZ);
         if (var10 < var4) {
            var6 = var9;
            var4 = var10;
         }
      }

      return (EntityHorse)var6;
   }

   public double getHorseJumpStrength() {
      return this.getEntityAttribute(horseJumpStrength).getAttributeValue();
   }

   protected String getDeathSound() {
      this.openHorseMouth();
      int var1 = this.getHorseType();
      return var1 == 3 ? "mob.horse.zombie.death" : (var1 == 4 ? "mob.horse.skeleton.death" : (var1 != 1 && var1 != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
   }

   protected int getDropItemId() {
      boolean var1 = this.rand.nextInt(4) == 0;
      int var2 = this.getHorseType();
      return var2 == 4 ? Item.bone.itemID : (var2 == 3 ? (var1 ? 0 : Item.rottenFlesh.itemID) : Item.leather.itemID);
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(3) + 1;

      int i;
      for(i = 0; i < num_drops; ++i) {
         this.dropItem(this.getDropItemId(), 1);
      }

      if (this.getHorseType() <= 2) {
         num_drops = 1 + this.rand.nextInt(1 + damage_source.getButcheringModifier());
         if (this.getHorseType() == 0) {
            num_drops += this.rand.nextInt(2);
         }

         for(i = 0; i < num_drops; ++i) {
            this.dropItem(this.isBurning() ? Item.beefCooked.itemID : Item.beefRaw.itemID, 1);
         }

      }
   }

   protected String getHurtSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(3) == 0) {
         this.makeHorseRear();
      }

      int var1 = this.getHorseType();
      return var1 == 3 ? "mob.horse.zombie.hit" : (var1 == 4 ? "mob.horse.skeleton.hit" : (var1 != 1 && var1 != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
   }

   public boolean isHorseSaddled() {
      return this.getHorseWatchableBoolean(4);
   }

   protected String getLivingSound() {
      this.openHorseMouth();
      if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
         this.makeHorseRear();
      }

      int var1 = this.getHorseType();
      return var1 == 3 ? "mob.horse.zombie.idle" : (var1 == 4 ? "mob.horse.skeleton.idle" : (var1 != 1 && var1 != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
   }

   protected String getAngrySoundName() {
      this.openHorseMouth();
      this.makeHorseRear();
      int var1 = this.getHorseType();
      return var1 != 3 && var1 != 4 ? (var1 != 1 && var1 != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      StepSound var5 = Block.blocksList[par4].stepSound;
      if (this.worldObj.getBlockId(par1, par2 + 1, par3) == Block.snow.blockID) {
         var5 = Block.snow.stepSound;
      }

      if (!Block.blocksList[par4].blockMaterial.isLiquid()) {
         int var6 = this.getHorseType();
         if (this.riddenByEntity != null && var6 != 1 && var6 != 2) {
            ++this.field_110285_bP;
            if (this.field_110285_bP > 5 && this.field_110285_bP % 3 == 0) {
               this.playSound("mob.horse.gallop", var5.getVolume() * 0.15F, var5.getPitch());
               if (var6 == 0 && this.rand.nextInt(10) == 0) {
                  this.playSound("mob.horse.breathe", var5.getVolume() * 0.6F, var5.getPitch());
               }
            } else if (this.field_110285_bP <= 5) {
               this.playSound("mob.horse.wood", var5.getVolume() * 0.15F, var5.getPitch());
            }
         } else if (var5 == Block.soundWoodFootstep) {
            this.playSound("mob.horse.soft", var5.getVolume() * 0.15F, var5.getPitch());
         } else {
            this.playSound("mob.horse.wood", var5.getVolume() * 0.15F, var5.getPitch());
         }
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(horseJumpStrength);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 53.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.22499999403953552);
   }

   public int getMaxSpawnedInChunk() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   protected float getSoundVolume(String sound) {
      return 0.8F;
   }

   public int getTalkInterval() {
      return 400;
   }

   public boolean func_110239_cn() {
      return this.getHorseType() == 0 || this.func_110241_cb() > 0;
   }

   private void func_110230_cF() {
      this.field_110286_bQ = null;
   }

   private void setHorseTexturePaths() {
      this.field_110286_bQ = "horse/";
      this.field_110280_bR[0] = null;
      this.field_110280_bR[1] = null;
      this.field_110280_bR[2] = null;
      int var1 = this.getHorseType();
      int var2 = this.getHorseVariant();
      int var3;
      if (var1 == 0) {
         var3 = var2 & 255;
         int var4 = (var2 & '\uff00') >> 8;
         this.field_110280_bR[0] = horseTextures[var3];
         this.field_110286_bQ = this.field_110286_bQ + field_110269_bA[var3];
         this.field_110280_bR[1] = horseMarkingTextures[var4];
         this.field_110286_bQ = this.field_110286_bQ + field_110292_bC[var4];
      } else {
         this.field_110280_bR[0] = "";
         this.field_110286_bQ = this.field_110286_bQ + "_" + var1 + "_";
      }

      var3 = this.func_110241_cb();
      this.field_110280_bR[2] = horseArmorTextures[var3];
      this.field_110286_bQ = this.field_110286_bQ + field_110273_bx[var3];
   }

   public String getHorseTexture() {
      if (this.field_110286_bQ == null) {
         this.setHorseTexturePaths();
      }

      return this.field_110286_bQ;
   }

   public String[] getVariantTexturePaths() {
      if (this.field_110286_bQ == null) {
         this.setHorseTexturePaths();
      }

      return this.field_110280_bR;
   }

   public boolean tryOpenGUI(EntityPlayer player) {
      if (!this.isTame()) {
         return false;
      } else if (this.riddenByEntity != null && this.riddenByEntity != player) {
         return false;
      } else {
         if (player.onServer()) {
            this.horseChest.func_110133_a(this.getEntityName());
            player.displayGUIHorse(this, this.horseChest);
         }

         return true;
      }
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (this.isTame()) {
         if (this.isAdultHorse() && player.isSneaking() && this.tryOpenGUI(player)) {
            return true;
         }
      } else if (this.isHorseUndead()) {
         return super.onEntityRightClicked(player, item_stack);
      }

      if (this.isAdultHorse() && this.riddenByEntity != null) {
         return false;
      } else if (this.getLeashed() && this.getLeashedToEntity() == player) {
         if (this.onServer()) {
            this.clearLeashed(!player.inCreativeMode(), true);
         }

         return true;
      } else {
         if (item_stack != null) {
            boolean horse_accepted_item = false;
            boolean horse_was_healed = false;
            if (this.isNormalHorse() && this.getHorseArmorIndex(item_stack) > 0) {
               if (!this.isTame()) {
                  this.makeHorseRearWithSound();
                  return true;
               }

               if (this.tryOpenGUI(player)) {
                  return true;
               }
            }

            if (!this.isHorseUndead()) {
               float healing = 0.0F;
               short growth = 0;
               byte temper_modifier = 0;
               Item item = item_stack.getItem();
               if (item == Item.wheat) {
                  healing = 2.0F;
                  growth = 60;
                  temper_modifier = 3;
               } else if (item == Item.sugar) {
                  healing = 1.0F;
                  growth = 30;
                  temper_modifier = 3;
               } else if (item == Item.bread) {
                  healing = 7.0F;
                  growth = 180;
                  temper_modifier = 3;
               } else if (item == Item.getItem(Block.hay)) {
                  healing = 20.0F;
                  growth = 180;
               } else if (item == Item.appleRed) {
                  healing = 3.0F;
                  growth = 60;
                  temper_modifier = 3;
               } else if (item == Item.goldenCarrot) {
                  healing = 4.0F;
                  growth = 60;
                  temper_modifier = 5;
                  if (this.isTame() && this.getGrowingAge() == 0) {
                     horse_accepted_item = true;
                     this.func_110196_bT();
                  }
               } else if (item == Item.appleGold) {
                  healing = 10.0F;
                  growth = 240;
                  temper_modifier = 10;
                  if (this.isTame() && this.getGrowingAge() == 0) {
                     horse_accepted_item = true;
                     this.func_110196_bT();
                  }
               }

               if (this.getHealth() < this.getMaxHealth() && healing > 0.0F) {
                  this.heal(healing);
                  horse_accepted_item = true;
                  horse_was_healed = true;
               } else if (!this.isTame() && this.isRebelliousForEating()) {
                  this.makeHorseRearWithSound();
                  return true;
               }

               if (!this.isAdultHorse() && growth > 0) {
                  this.addGrowth(growth);
                  horse_accepted_item = true;
               }

               if (temper_modifier > 0 && (horse_accepted_item || !this.isTame()) && temper_modifier < this.getMaxTemper()) {
                  if (this.onServer()) {
                     this.entityFX(EnumEntityFX.heal);
                  }

                  horse_accepted_item = true;
                  this.increaseTemper(temper_modifier);
               }

               if (horse_accepted_item) {
                  this.func_110266_cB();
               }

               if (this.onServer() && !this.isTame() && horse_accepted_item && (!horse_was_healed || this.getHealth() >= this.getMaxHealth())) {
                  this.setRebelliousForEatingCounter(4000);
               }
            }

            if (!this.isTame() && !horse_accepted_item) {
               this.makeHorseRearWithSound();
               return true;
            }

            if (!horse_accepted_item && this.isPackHorse() && !this.isChested() && item_stack.itemID == Block.chest.blockID) {
               this.setChested(true);
               this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
               horse_accepted_item = true;
               this.func_110226_cD();
            }

            if (!horse_accepted_item && this.isAdultHorse() && !this.isHorseSaddled() && item_stack.itemID == Item.saddle.itemID && this.tryOpenGUI(player)) {
               return true;
            }

            if (horse_accepted_item) {
               if (player.onServer() && !player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }

               return true;
            }
         }

         if (this.isAdultHorse() && this.riddenByEntity == null) {
            if (this.isRebelliousForRiding()) {
               this.makeHorseRearWithSound();
               return true;
            } else {
               this.func_110237_h(player);
               return true;
            }
         } else {
            return super.onEntityRightClicked(player, item_stack);
         }
      }
   }

   private void func_110237_h(EntityPlayer par1EntityPlayer) {
      par1EntityPlayer.setRotation(this.rotationYaw, this.rotationPitch);
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.worldObj.isRemote) {
         par1EntityPlayer.mountEntity(this);
      }

   }

   public boolean isNormalHorse() {
      return this.getHorseType() == 0;
   }

   public boolean isPackHorse() {
      return this.getHorseType() == 1 || this.getHorseType() == 2;
   }

   protected boolean isMovementBlocked() {
      return this.riddenByEntity != null && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
   }

   public boolean isHorseUndead() {
      return this.getHorseType() == 3 || this.getHorseType() == 4;
   }

   public boolean canHorseNeverBreed() {
      return this.getHorseType() == 2 || this.isHorseUndead();
   }

   private void func_110210_cH() {
      this.field_110278_bp = 1;
   }

   public void onDeath(DamageSource par1DamageSource) {
      super.onDeath(par1DamageSource);
      if (!this.worldObj.isRemote) {
         this.dropChestItems();
      }

   }

   public void onLivingUpdate() {
      if (this.rand.nextInt(200) == 0) {
         this.func_110210_cH();
      }

      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         if (this.rebellious_for_eating_counter > 0) {
            this.setRebelliousForEatingCounter(this.rebellious_for_eating_counter - 1);
         }

         if (this.rebellious_for_riding_counter > 0) {
            this.setRebelliousForRidingCounter(this.rebellious_for_riding_counter - 1);
         }

         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (!this.isEatingHaystack() && this.riddenByEntity == null && this.rand.nextInt(300) == 0 && this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ)) == Block.grass.blockID) {
            this.setEatingHaystack(true);
         }

         if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50) {
            this.eatingHaystackCounter = 0;
            this.setEatingHaystack(false);
         }

         if (this.func_110205_ce() && !this.isAdultHorse() && !this.isEatingHaystack()) {
            EntityHorse var1 = this.getClosestHorse(this, 16.0);
            if (var1 != null && this.getDistanceSqToEntity(var1) > 4.0) {
               PathEntity var2 = this.worldObj.getPathEntityToEntity(this, var1, 16.0F, true, false, false, true);
               this.setPathToEntity(var2);
            }
         }
      }

   }

   public void onUpdate() {
      super.onUpdate();
      if (this.worldObj.isRemote && this.dataWatcher.hasChanges()) {
         this.dataWatcher.func_111144_e();
         this.func_110230_cF();
      }

      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(128, false);
      }

      if (!this.worldObj.isRemote && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.field_110278_bp > 0 && ++this.field_110278_bp > 8) {
         this.field_110278_bp = 0;
      }

      if (this.field_110279_bq > 0) {
         ++this.field_110279_bq;
         if (this.field_110279_bq > 300) {
            this.field_110279_bq = 0;
         }
      }

      this.prevHeadLean = this.headLean;
      if (this.isEatingHaystack()) {
         this.headLean += (1.0F - this.headLean) * 0.4F + 0.05F;
         if (this.headLean > 1.0F) {
            this.headLean = 1.0F;
         }
      } else {
         this.headLean += (0.0F - this.headLean) * 0.4F - 0.05F;
         if (this.headLean < 0.0F) {
            this.headLean = 0.0F;
         }
      }

      this.prevRearingAmount = this.rearingAmount;
      if (this.isRearing()) {
         this.prevHeadLean = this.headLean = 0.0F;
         this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
         if (this.rearingAmount > 1.0F) {
            this.rearingAmount = 1.0F;
         }
      } else {
         this.field_110294_bI = false;
         this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F;
         if (this.rearingAmount < 0.0F) {
            this.rearingAmount = 0.0F;
         }
      }

      this.prevMouthOpenness = this.mouthOpenness;
      if (this.getHorseWatchableBoolean(128)) {
         this.mouthOpenness += (1.0F - this.mouthOpenness) * 0.7F + 0.05F;
         if (this.mouthOpenness > 1.0F) {
            this.mouthOpenness = 1.0F;
         }
      } else {
         this.mouthOpenness += (0.0F - this.mouthOpenness) * 0.7F - 0.05F;
         if (this.mouthOpenness < 0.0F) {
            this.mouthOpenness = 0.0F;
         }
      }

   }

   private void openHorseMouth() {
      if (!this.worldObj.isRemote) {
         this.openMouthCounter = 1;
         this.setHorseWatchableBoolean(128, true);
      }

   }

   private boolean canHorseMateAtThisMoment() {
      return this.riddenByEntity == null && this.ridingEntity == null && this.isTame() && this.isAdultHorse() && !this.canHorseNeverBreed() && this.getHealth() >= this.getMaxHealth();
   }

   public boolean isShy() {
      return this.isAdultHorse() && !this.isTame() && this.getTemper() <= 0;
   }

   public void setEating(boolean par1) {
      if (!par1 || this.hurtTime <= 0 && !this.has_decided_to_flee) {
         this.setHorseWatchableBoolean(32, par1);
      }
   }

   public void setEatingHaystack(boolean par1) {
      if (par1 && this.onClient()) {
         Debug.setErrorMessage("setEatingHackstack: set to true on client");
         Debug.printStackTrace();
      }

      if (!par1 || this.hurtTime <= 0 && !this.has_decided_to_flee) {
         if (par1) {
            List predators;
            if (this.isShy()) {
               predators = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(16.0, 2.0, 16.0));
               if (!predators.isEmpty()) {
                  return;
               }
            }

            if (this.tasks.isTaskExecuting(EntityAIAvoidPotentialPredators.class)) {
               return;
            }

            if (this.tasks.isTaskExecuting(EntityAIFleeAttackerOrPanic.class)) {
               return;
            }

            predators = this.worldObj.getPredatorsWithinAABBForEntity(this, this.boundingBox.expand(16.0, 2.0, 16.0));
            if (!predators.isEmpty()) {
               return;
            }
         }

         this.setEating(par1);
      }
   }

   public void setRearing(boolean par1) {
      if (!par1 || this.hurtTime <= 0 && !this.has_decided_to_flee) {
         if (par1) {
            this.setEatingHaystack(false);
         }

         this.setHorseWatchableBoolean(64, par1);
      }
   }

   private void makeHorseRear() {
      if (this.hurtTime <= 0 && !this.has_decided_to_flee) {
         if (!this.worldObj.isRemote) {
            this.jumpRearingCounter = 1;
            this.setRearing(true);
         }

      }
   }

   public void makeHorseRearWithSound() {
      if (this.hurtTime <= 0 && !this.has_decided_to_flee) {
         this.makeHorseRear();
         String var1 = this.getAngrySoundName();
         if (var1 != null) {
            this.makeSound(var1);
         }

      }
   }

   public void dropChestItems() {
      this.dropItemsInChest(this, this.horseChest);
      this.dropChests();
   }

   private void dropItemsInChest(Entity par1Entity, AnimalChest par2AnimalChest) {
      if (par2AnimalChest != null && !this.worldObj.isRemote) {
         for(int var3 = 0; var3 < par2AnimalChest.getSizeInventory(); ++var3) {
            ItemStack var4 = par2AnimalChest.getStackInSlot(var3);
            if (var4 != null) {
               this.dropItemStack(var4, 0.0F);
            }
         }
      }

   }

   public boolean setTamedBy(EntityPlayer par1EntityPlayer) {
      this.setOwnerName(par1EntityPlayer.getCommandSenderName());
      this.setHorseTamed(true);
      return true;
   }

   public void moveEntityWithHeading(float par1, float par2) {
      if (this.riddenByEntity != null && this.isHorseSaddled()) {
         this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
         this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
         par1 = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
         par2 = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (par2 <= 0.0F) {
            par2 *= 0.25F;
            this.field_110285_bP = 0;
         }

         if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.field_110294_bI) {
            par1 = 0.0F;
            par2 = 0.0F;
         }

         if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
            this.motionY = this.getHorseJumpStrength() * (double)this.jumpPower;
            if (this.isPotionActive(Potion.jump)) {
               this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
            }

            this.setHorseJumping(true);
            this.isAirBorne = true;
            if (par2 > 0.0F) {
               float var3 = MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F);
               float var4 = MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F);
               this.motionX += (double)(-0.4F * var3 * this.jumpPower);
               this.motionZ += (double)(0.4F * var4 * this.jumpPower);
               this.playSound("mob.horse.jump", 0.4F, 1.0F);
            }

            this.jumpPower = 0.0F;
         }

         this.stepHeight = 1.0F;
         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (!this.worldObj.isRemote) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            super.moveEntityWithHeading(par1, par2);
         }

         if (this.onGround) {
            this.jumpPower = 0.0F;
            this.setHorseJumping(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double var8 = this.posX - this.prevPosX;
         double var5 = this.posZ - this.prevPosZ;
         float var7 = MathHelper.sqrt_double(var8 * var8 + var5 * var5) * 4.0F;
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         this.limbSwingAmount += (var7 - this.limbSwingAmount) * 0.4F;
         this.limbSwing += this.limbSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.jumpMovementFactor = 0.02F;
         super.moveEntityWithHeading(par1, par2);
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("EatingHaystack", this.isEatingHaystack());
      par1NBTTagCompound.setBoolean("ChestedHorse", this.isChested());
      par1NBTTagCompound.setBoolean("HasReproduced", this.getHasReproduced());
      par1NBTTagCompound.setBoolean("Bred", this.func_110205_ce());
      par1NBTTagCompound.setInteger("Type", this.getHorseType());
      par1NBTTagCompound.setInteger("Variant", this.getHorseVariant());
      par1NBTTagCompound.setInteger("Temper", this.getTemper());
      par1NBTTagCompound.setBoolean("Tame", this.isTame());
      par1NBTTagCompound.setString("OwnerName", this.getOwnerName());
      if (this.isChested()) {
         NBTTagList var2 = new NBTTagList();

         for(int var3 = 2; var3 < this.horseChest.getSizeInventory(); ++var3) {
            ItemStack var4 = this.horseChest.getStackInSlot(var3);
            if (var4 != null) {
               NBTTagCompound var5 = new NBTTagCompound();
               var5.setByte("Slot", (byte)var3);
               var4.writeToNBT(var5);
               var2.appendTag(var5);
            }
         }

         par1NBTTagCompound.setTag("Items", var2);
      }

      if (this.horseChest.getStackInSlot(1) != null) {
         par1NBTTagCompound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound("ArmorItem")));
      }

      if (this.horseChest.getStackInSlot(0) != null) {
         par1NBTTagCompound.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound("SaddleItem")));
      }

      if (this.rebellious_for_eating_counter > 0) {
         par1NBTTagCompound.setShort("rebellious_for_eating_counter", (short)this.rebellious_for_eating_counter);
      }

      if (this.rebellious_for_riding_counter > 0) {
         par1NBTTagCompound.setShort("rebellious_for_riding_counter", (short)this.rebellious_for_riding_counter);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setEatingHaystack(par1NBTTagCompound.getBoolean("EatingHaystack"));
      this.func_110242_l(par1NBTTagCompound.getBoolean("Bred"));
      this.setChested(par1NBTTagCompound.getBoolean("ChestedHorse"));
      this.setHasReproduced(par1NBTTagCompound.getBoolean("HasReproduced"));
      this.setHorseType(par1NBTTagCompound.getInteger("Type"));
      this.setHorseVariant(par1NBTTagCompound.getInteger("Variant"));
      this.setTemper(par1NBTTagCompound.getInteger("Temper"));
      this.setHorseTamed(par1NBTTagCompound.getBoolean("Tame"));
      if (par1NBTTagCompound.hasKey("OwnerName")) {
         this.setOwnerName(par1NBTTagCompound.getString("OwnerName"));
      }

      AttributeInstance var2 = this.getAttributeMap().getAttributeInstanceByName("Speed");
      if (var2 != null) {
         this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, var2.getBaseValue() * 0.25);
      }

      if (this.isChested()) {
         NBTTagList var3 = par1NBTTagCompound.getTagList("Items");
         this.func_110226_cD();

         for(int var4 = 0; var4 < var3.tagCount(); ++var4) {
            NBTTagCompound var5 = (NBTTagCompound)var3.tagAt(var4);
            int var6 = var5.getByte("Slot") & 255;
            if (var6 >= 2 && var6 < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(var6, ItemStack.loadItemStackFromNBT(var5));
            }
         }
      }

      ItemStack var7;
      if (par1NBTTagCompound.hasKey("ArmorItem")) {
         var7 = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("ArmorItem"));
         if (var7 != null && func_110211_v(var7.itemID)) {
            this.horseChest.setInventorySlotContents(1, var7);
         }
      }

      if (par1NBTTagCompound.hasKey("SaddleItem")) {
         var7 = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("SaddleItem"));
         if (var7 != null && var7.itemID == Item.saddle.itemID) {
            this.horseChest.setInventorySlotContents(0, var7);
         }
      } else if (par1NBTTagCompound.getBoolean("Saddle")) {
         this.horseChest.setInventorySlotContents(0, new ItemStack(Item.saddle));
      }

      this.func_110232_cE();
      this.setRebelliousForEatingCounter(par1NBTTagCompound.getShort("rebellious_for_eating_counter"));
      this.setRebelliousForRidingCounter(par1NBTTagCompound.getShort("rebellious_for_riding_counter"));
   }

   public boolean canMateWith(EntityAnimal par1EntityAnimal) {
      if (par1EntityAnimal == this) {
         return false;
      } else if (par1EntityAnimal.getClass() != this.getClass()) {
         return false;
      } else {
         EntityHorse var2 = (EntityHorse)par1EntityAnimal;
         if (this.canHorseMateAtThisMoment() && var2.canHorseMateAtThisMoment()) {
            int var3 = this.getHorseType();
            int var4 = var2.getHorseType();
            return var3 == var4 || var3 == 0 && var4 == 1 || var3 == 1 && var4 == 0;
         } else {
            return false;
         }
      }
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      EntityHorse var2 = (EntityHorse)par1EntityAgeable;
      EntityHorse var3 = new EntityHorse(this.worldObj);
      int var4 = this.getHorseType();
      int var5 = var2.getHorseType();
      int var6 = 0;
      if (var4 == var5) {
         var6 = var4;
      } else if (var4 == 0 && var5 == 1 || var4 == 1 && var5 == 0) {
         var6 = 2;
      }

      if (var6 == 0) {
         int var8 = this.rand.nextInt(9);
         int var7;
         if (var8 < 4) {
            var7 = this.getHorseVariant() & 255;
         } else if (var8 < 8) {
            var7 = var2.getHorseVariant() & 255;
         } else {
            var7 = this.rand.nextInt(7);
         }

         int var9 = this.rand.nextInt(5);
         if (var9 < 4) {
            var7 |= this.getHorseVariant() & '\uff00';
         } else if (var9 < 8) {
            var7 |= var2.getHorseVariant() & '\uff00';
         } else {
            var7 |= this.rand.nextInt(5) << 8 & '\uff00';
         }

         var3.setHorseVariant(var7);
      }

      var3.setHorseType(var6);
      double var14 = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + par1EntityAgeable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + (double)this.func_110267_cL();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, var14 / 3.0);
      double var13 = this.getEntityAttribute(horseJumpStrength).getBaseValue() + par1EntityAgeable.getEntityAttribute(horseJumpStrength).getBaseValue() + this.func_110245_cM();
      this.setEntityAttribute(horseJumpStrength, var13 / 3.0);
      double var11 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() + par1EntityAgeable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() + this.func_110203_cN();
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, var11 / 3.0);
      return var3;
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
      boolean var2 = false;
      int var3 = 0;
      int var7;
      if (par1EntityLivingData1 instanceof EntityHorseGroupData) {
         var7 = ((EntityHorseGroupData)par1EntityLivingData1).field_111107_a;
         var3 = ((EntityHorseGroupData)par1EntityLivingData1).field_111106_b & 255 | this.rand.nextInt(5) << 8;
      } else {
         if (this.rand.nextInt(10) == 0) {
            var7 = 1;
         } else {
            int var4 = this.rand.nextInt(7);
            int var5 = this.rand.nextInt(5);
            var7 = 0;
            var3 = var4 | var5 << 8;
         }

         par1EntityLivingData1 = new EntityHorseGroupData(var7, var3);
      }

      this.setHorseType(var7);
      this.setHorseVariant(var3);
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAgeToNewborn();
      }

      if (var7 != 4 && var7 != 3) {
         this.setEntityAttribute(SharedMonsterAttributes.maxHealth, (double)this.func_110267_cL());
         if (var7 == 0) {
            this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, this.func_110203_cN());
         } else {
            this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.17499999701976776);
         }
      } else {
         this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 15.0);
         this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.20000000298023224);
      }

      if (var7 != 2 && var7 != 1) {
         this.setEntityAttribute(horseJumpStrength, this.func_110245_cM());
      } else {
         this.setEntityAttribute(horseJumpStrength, 0.5);
      }

      this.setHealth(this.getMaxHealth());
      return (EntityLivingData)par1EntityLivingData1;
   }

   public float getGrassEatingAmount(float par1) {
      return this.prevHeadLean + (this.headLean - this.prevHeadLean) * par1;
   }

   public float getRearingAmount(float par1) {
      return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * par1;
   }

   public float func_110201_q(float par1) {
      return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * par1;
   }

   public void setJumpPower(int par1) {
      if (this.isHorseSaddled()) {
         if (par1 < 0) {
            par1 = 0;
         } else {
            this.field_110294_bI = true;
            this.makeHorseRear();
         }

         if (par1 >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * (float)par1 / 90.0F;
         }
      }

   }

   protected void spawnHorseParticles(boolean par1) {
      EnumParticle var2 = par1 ? EnumParticle.heart : EnumParticle.smoke;

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.rand.nextGaussian() * 0.02;
         double var6 = this.rand.nextGaussian() * 0.02;
         double var8 = this.rand.nextGaussian() * 0.02;
         this.worldObj.spawnParticle(var2, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var4, var6, var8);
      }

   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.tame_success) {
         this.spawnHorseParticles(true);
      } else if (par1 == EnumEntityState.tame_failure) {
         this.spawnHorseParticles(false);
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public void updateRiderPosition() {
      super.updateRiderPosition();
      if (this.prevRearingAmount > 0.0F) {
         float var1 = MathHelper.sin(this.renderYawOffset * 3.1415927F / 180.0F);
         float var2 = MathHelper.cos(this.renderYawOffset * 3.1415927F / 180.0F);
         float var3 = 0.7F * this.prevRearingAmount;
         float var4 = 0.15F * this.prevRearingAmount;
         this.riddenByEntity.setPosition(this.posX + (double)(var3 * var1), this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + (double)var4, this.posZ - (double)(var3 * var2));
         if (this.riddenByEntity instanceof EntityLivingBase) {
            ((EntityLivingBase)this.riddenByEntity).renderYawOffset = this.renderYawOffset;
         }
      }

   }

   private float func_110267_cL() {
      return 15.0F + (float)this.rand.nextInt(8) + (float)this.rand.nextInt(9);
   }

   private double func_110245_cM() {
      return 0.4000000059604645 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2;
   }

   private double func_110203_cN() {
      return (0.44999998807907104 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3) * 0.25;
   }

   public static boolean func_110211_v(int par0) {
      return Item.getItem(par0) instanceof ItemHorseArmor;
   }

   public boolean isOnLadder() {
      return false;
   }

   public boolean considerFleeing() {
      Entity last_attacking_entity = this.getLastHarmingEntity();
      this.has_decided_to_flee = last_attacking_entity != null && this.getDistanceToEntity(last_attacking_entity) < 32.0F;
      return this.has_decided_to_flee;
   }

   public boolean considerStopFleeing() {
      Entity last_attacking_entity = this.getLastHarmingEntity();
      if (last_attacking_entity == null) {
         this.has_decided_to_flee = false;
         this.fleeing = false;
         return true;
      } else if (this.getDistanceToEntity(last_attacking_entity) > 40.0F) {
         this.fleeing = false;
         return true;
      } else {
         return false;
      }
   }

   public float getAIMoveSpeed() {
      if (this.riddenByEntity != null) {
         return super.getAIMoveSpeed();
      } else {
         int type = this.getHorseType();
         float speed;
         if (type == 0) {
            speed = 0.5F;
         } else if (type == 1) {
            speed = 0.4F;
         } else if (type == 2) {
            speed = 0.4F;
         } else if (type == 3) {
            speed = 0.3F;
         } else if (type == 4) {
            speed = 0.5F;
         } else {
            speed = 0.0F;
         }

         if (speed == 0.0F) {
            Debug.setErrorMessage("getAIMoveSpeed: unrecognized horse type " + type);
         }

         return this.isChild() ? speed * 0.75F : speed;
      }
   }

   public boolean isHarmedByFire() {
      return true;
   }

   public boolean isHarmedByLava() {
      return true;
   }

   public int getExperienceValue() {
      return 0;
   }

   public void modifyEffectiveCollisionBoxForRaycastFromEntity(AxisAlignedBB effective_collision_box, Entity entity) {
      if (entity == this.riddenByEntity) {
         effective_collision_box.scale(0.5);
      }

   }

   static {
      armors = new ItemHorseArmor[]{null, Item.horseArmorCopper, Item.horseArmorSilver, Item.horseArmorGold, Item.horseArmorIron, Item.horseArmorMithril, Item.horseArmorAdamantium, Item.horseArmorAncientMetal};
      horseArmorTextures = new String[armors.length];

      for(int i = 0; i < armors.length; ++i) {
         horseArmorTextures[i] = armors[i] == null ? null : "textures/entity/horse/armor/horse_armor_" + armors[i].effective_material.name + ".png";
      }

      if (field_110273_bx.length != armors.length) {
         Debug.setErrorMessage("EntityHorse: field_110273_bx must have same number of elements as armors");
      }

      horseTextures = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
      field_110269_bA = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
      horseMarkingTextures = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
      field_110292_bC = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   }
}
