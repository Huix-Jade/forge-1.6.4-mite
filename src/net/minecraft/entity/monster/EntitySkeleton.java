package net.minecraft.entity.monster;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAncientBoneLord;
import net.minecraft.entity.EntityBoneLord;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EntityLongdeadGuardian;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToRepairItem;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISeekFiringPosition;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumQuality;
import net.minecraft.world.World;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob {
   private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0, 20, 60, 15.0F);
   private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2, false);
   private int frenzied_by_bone_lord_countdown;
   private int data_object_id_is_frenzied_by_bone_lord;
   public int forced_skeleton_type = -1;

   public EntitySkeleton(World par1World) {
      super(par1World);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIRestrictSun(this));
      this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0));
      this.tasks.addTask(5, new EntityAIWander(this, 0.75));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.tasks.addTask(4, new EntityAIMoveToRepairItem(this, 1.0F, true));
      if (par1World != null && !par1World.isRemote) {
         this.setCombatTask();
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 6.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.30000001192092896);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 4.0);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(13, new Byte((byte)0));
      this.data_object_id_is_frenzied_by_bone_lord = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
   }

   public boolean setFrenziedByBoneLordCountdown(int frenzied_by_bone_lord_countdown) {
      boolean was_frenzied_by_bone_lord = this.frenzied_by_bone_lord_countdown > 0;
      boolean is_frenzied_by_bone_lord = frenzied_by_bone_lord_countdown > 0;
      this.frenzied_by_bone_lord_countdown = frenzied_by_bone_lord_countdown;
      if (is_frenzied_by_bone_lord != was_frenzied_by_bone_lord) {
         this.dataWatcher.updateObject(this.data_object_id_is_frenzied_by_bone_lord, (byte)(is_frenzied_by_bone_lord ? -1 : 0));
      }

      return is_frenzied_by_bone_lord;
   }

   public boolean isFrenziedByBoneLord() {
      if (this.worldObj.isRemote) {
         return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_frenzied_by_bone_lord) != 0;
      } else {
         return this.frenzied_by_bone_lord_countdown > 0;
      }
   }

   public boolean isAIEnabled() {
      return true;
   }

   protected String getLivingSound() {
      return "mob.skeleton.say";
   }

   protected String getHurtSound() {
      return "mob.skeleton.hurt";
   }

   protected String getDeathSound() {
      return "mob.skeleton.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.skeleton.step", 0.15F, 1.0F);
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityLostHealth() && this.getSkeletonType() == 1 && target instanceof EntityLivingBase) {
            target.getAsEntityLivingBase().addPotionEffect(new PotionEffect(Potion.wither.id, 200));
         }

         return result;
      } else {
         return result;
      }
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   public void onLivingUpdate() {
      if (this.worldObj.isRemote && this.getSkeletonType() == 1) {
         this.setSize(0.72F, 2.34F);
      }

      if (this.frenzied_by_bone_lord_countdown > 0) {
         this.setFrenziedByBoneLordCountdown(this.frenzied_by_bone_lord_countdown - 1);
      }

      super.onLivingUpdate();
   }

   public void updateRidden() {
      super.updateRidden();
      if (this.ridingEntity instanceof EntityCreature) {
         EntityCreature var1 = (EntityCreature)this.ridingEntity;
         this.renderYawOffset = var1.renderYawOffset;
      }

   }

   public void onDeath(DamageSource par1DamageSource) {
      super.onDeath(par1DamageSource);
      if (par1DamageSource.getImmediateEntity() instanceof EntityArrow && par1DamageSource.getResponsibleEntity() != null && par1DamageSource.getResponsibleEntity() instanceof EntityPlayer && this.getSkeletonType() == 0) {
         EntityPlayer var2 = (EntityPlayer)par1DamageSource.getResponsibleEntity();
         double var3 = var2.posX - this.posX;
         double var5 = var2.posZ - this.posZ;
         if (var3 * var3 + var5 * var5 >= 2500.0) {
            var2.triggerAchievement(AchievementList.snipeSkeleton);
         }
      }

   }

   protected int getDropItemId() {
      return Item.arrowRustedIron.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int looting = damage_source.getLootingModifier();
      int num_drops;
      int i;
      if (this.getSkeletonType() == 1) {
         num_drops = this.rand.nextInt(3 + looting) - 1;
         if (num_drops > 0 && !recently_hit_by_player) {
            num_drops -= this.rand.nextInt(num_drops + 1);
         }

         for(i = 0; i < num_drops; ++i) {
            this.dropItem(Item.coal.itemID, 1);
         }

         if (recently_hit_by_player && !this.has_taken_massive_fall_damage && this.rand.nextInt(this.getBaseChanceOfRareDrop()) < 5 + looting * 2) {
            this.dropItemStack(new ItemStack(Item.skull.itemID, 1, 1), 0.0F);
         }
      } else if (this.getSkeletonType() != 2) {
         num_drops = this.rand.nextInt(2 + looting);
         if (num_drops > 0 && !recently_hit_by_player) {
            num_drops -= this.rand.nextInt(num_drops + 1);
         }

         if (this.isLongdead() && num_drops > 0) {
            num_drops = this.rand.nextInt(3) == 0 ? 1 : 0;
         }

         for(i = 0; i < num_drops; ++i) {
            this.dropItem(this.isLongdead() ? Item.arrowAncientMetal.itemID : Item.arrowRustedIron.itemID, 1);
         }
      }

      num_drops = this.rand.nextInt(3);
      if (num_drops > 0 && !recently_hit_by_player) {
         num_drops -= this.rand.nextInt(num_drops + 1);
      }

      for(i = 0; i < num_drops; ++i) {
         this.dropItem(Item.bone.itemID, 1);
      }

   }

   public void addRandomWeapon() {
      if (this.getSkeletonType() == 2 && this.rand.nextInt(20) == 0) {
         int day_of_world = MinecraftServer.getServer().getOverworld().getDayOfWorld();
         if (day_of_world >= 10) {
            this.setCurrentItemOrArmor(0, (new ItemStack(day_of_world >= 20 && !this.rand.nextBoolean() ? Item.swordRustedIron : Item.daggerRustedIron)).randomizeForMob(this, false));
            return;
         }
      }

      this.setCurrentItemOrArmor(0, (new ItemStack((Item)(this.getSkeletonType() == 2 ? Item.clubWood : Item.bow))).randomizeForMob(this, true));
   }

   protected void addRandomEquipment() {
      this.addRandomWeapon();
      this.addRandomArmor();
   }

   public int getRandomSkeletonType(World world) {
      if (world.isTheNether()) {
         return 1;
      } else {
         return (double)this.rand.nextFloat() < (this.isLongdead() ? 0.5 : 0.25) ? 2 : 0;
      }
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
      int skeleton_type = this.forced_skeleton_type >= 0 ? this.forced_skeleton_type : this.getRandomSkeletonType(this.worldObj);
      if (skeleton_type == 1) {
         this.tasks.addTask(4, this.aiAttackOnCollide);
         this.setSkeletonType(1);
         this.setCurrentItemOrArmor(0, (new ItemStack(Item.swordIron)).setQuality(EnumQuality.poor).randomizeForMob(this, false));
         this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0);
      } else {
         if (skeleton_type == 2) {
            this.setSkeletonType(2);
            this.tasks.addTask(4, this.aiAttackOnCollide);
         } else if (skeleton_type == 0) {
            this.tasks.addTask(4, this.aiArrowAttack);
         } else {
            Minecraft.setErrorMessage("onSpawnWithEgg: Unrecognized skeleton type " + skeleton_type);
         }

         this.addRandomEquipment();
      }

      this.setCanPickUpLoot(true);
      if (this.getCurrentItemOrArmor(4) == null) {
         Calendar var2 = this.worldObj.getCurrentDate();
         if (var2.get(2) + 1 == 10 && var2.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
            this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Block.pumpkinLantern : Block.pumpkin));
            this.equipmentDropChances[4] = 0.0F;
         }
      }

      return par1EntityLivingData;
   }

   public void setCombatTask() {
      this.tasks.removeTask(this.aiAttackOnCollide);
      this.tasks.removeTask(this.aiArrowAttack);
      ItemStack var1 = this.getHeldItemStack();
      if (var1 != null && var1.getItem() instanceof ItemBow) {
         this.tasks.addTask(4, this.aiArrowAttack);
         this.tasks.addTask(3, new EntityAISeekFiringPosition(this, 1.0F, true));
      } else {
         this.tasks.addTask(4, this.aiAttackOnCollide);
      }

   }

   public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
      EntityArrow var3 = new EntityArrow(this.worldObj, this, par1EntityLivingBase, 1.6F, (float)(14 - this.worldObj.difficultySetting * 4), this.isLongdead() ? Item.arrowAncientMetal : Item.arrowRustedIron, false);
      int var4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItemStack());
      int var5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItemStack());
      double damage = (double)(par2 * 2.0F) + this.rand.nextGaussian() * 0.25 + (double)((float)this.worldObj.difficultySetting * 0.11F);
      var3.setDamage(damage);
      if (var4 > 0) {
         var3.setDamage(var3.getDamage() + (double)var4 * 0.5 + 0.5);
      }

      if (var5 > 0) {
         var3.setKnockbackStrength(var5);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItemStack()) > 0 || this.getSkeletonType() == 1 || this.isBurning() && this.rand.nextInt(3) == 0) {
         var3.setFire(100);
      }

      this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.worldObj.spawnEntityInWorld(var3);
   }

   public int getSkeletonType() {
      return this.dataWatcher.getWatchableObjectByte(13);
   }

   public void setSkeletonType(int par1) {
      this.dataWatcher.updateObject(13, (byte)par1);
      if (par1 == 1) {
         this.setSize(0.72F, 2.34F);
      } else {
         this.setSize(0.6F, 1.8F);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("SkeletonType")) {
         byte var2 = par1NBTTagCompound.getByte("SkeletonType");
         this.setSkeletonType(var2);
      }

      this.setCombatTask();
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setByte("SkeletonType", (byte)this.getSkeletonType());
   }

   public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {
      super.setCurrentItemOrArmor(par1, par2ItemStack);
      if (!this.worldObj.isRemote && par1 == 0) {
         this.setCombatTask();
      }

   }

   public void setHeldItemStack(ItemStack item_stack) {
      super.setHeldItemStack(item_stack);
      if (this.onServer()) {
         this.setCombatTask();
      }

   }

   public double getYOffset() {
      return super.getYOffset() - 0.5;
   }

   public boolean canBeDamagedByCacti() {
      return false;
   }

   public boolean isRepairItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() == Item.bone;
   }

   public boolean isHarmedByFire() {
      return this.getSkeletonType() != 1;
   }

   public boolean isHarmedByLava() {
      return this.getSkeletonType() != 1;
   }

   public EnumEntityFX getHealFX() {
      return EnumEntityFX.repair;
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      return damage_source.isArrowDamage() && damage_source.getResponsibleEntity() instanceof EntitySkeleton ? true : super.isImmuneTo(damage_source);
   }

   public boolean isLongdead() {
      return false;
   }

   public boolean isLongdeadGuardian() {
      return this instanceof EntityLongdeadGuardian;
   }

   public boolean isBoneLord() {
      return this instanceof EntityBoneLord;
   }

   public boolean isAncientBoneLord() {
      return this instanceof EntityAncientBoneLord;
   }

   public void setFrenziedByBoneLord(EntityLivingBase target) {
      this.setFrenziedByBoneLordCountdown(20);
      if (this.getTarget() == null) {
         this.setTarget(target);
      }

   }

   public boolean isFrenzied() {
      return this.isFrenziedByBoneLord() || super.isFrenzied();
   }

   public boolean avoidsSunlight() {
      if (this.isWearingHelmet(true)) {
         return false;
      } else {
         EntityLivingBase target = this.getTarget();
         if (target != null && !target.isDead && target.getHealth() > 0.0F) {
            ItemStack held_item_stack = this.getHeldItemStack();
            if (held_item_stack == null || !(held_item_stack.getItem() instanceof ItemBow)) {
               return false;
            }
         }

         return true;
      }
   }
}
