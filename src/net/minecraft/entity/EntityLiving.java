package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemLeash;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.SignalData;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;

public abstract class EntityLiving extends EntityLivingBase {
   public int livingSoundTime;
   private EntityLookHelper lookHelper;
   private EntityMoveHelper moveHelper;
   private EntityJumpHelper jumpHelper;
   private EntityBodyHelper bodyHelper;
   private PathNavigate navigator;
   public final EntityAITasks tasks;
   protected final EntityAITasks targetTasks;
   private EntityLivingBase attackTarget;
   private EntitySenses senses;
   private ItemStack[] equipment = new ItemStack[5];
   protected float[] equipmentDropChances = new float[5];
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   protected float defaultPitch;
   private Entity currentTarget;
   protected int numTicksToChaseTarget;
   private boolean isLeashed;
   private Entity leashedToEntity;
   private NBTTagCompound field_110170_bx;
   public boolean picked_up_a_held_item;
   public int food_or_repair_item_pickup_cooldown;
   public long spooked_until;
   private boolean is_decoy;
   public boolean came_from_spawner;
   protected boolean came_from_spawn_block;
   private int spawn_block_x;
   private int spawn_block_y;
   private int spawn_block_z;
   public int ticks_disarmed;
   private String target_unique_id_string;
   public EntityLivingBase AI_retarget;
   public int increased_chance_of_spreading_fire_countdown;
   public EntityItem target_entity_item;
   public long last_tick_harmed_by_cactus;

   public EntityLiving(World par1World) {
      super(par1World);
      this.tasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
      this.targetTasks = new EntityAITasks(par1World != null && par1World.theProfiler != null ? par1World.theProfiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = new EntityBodyHelper(this);
      this.navigator = new PathNavigate(this, par1World);
      this.senses = new EntitySenses(this);

      for(int var2 = 0; var2 < this.equipmentDropChances.length; ++var2) {
         this.equipmentDropChances[var2] = 0.085F;
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 16.0);
   }

   public EntityLookHelper getLookHelper() {
      return this.lookHelper;
   }

   public EntityMoveHelper getMoveHelper() {
      return this.moveHelper;
   }

   public EntityJumpHelper getJumpHelper() {
      return this.jumpHelper;
   }

   public PathNavigate getNavigator() {
      return this.navigator;
   }

   public EntitySenses getEntitySenses() {
      return this.senses;
   }

   public EntityLivingBase getAttackTarget() {
      if (!this.isAIEnabled()) {
         Minecraft.setErrorMessage("getAttackTarget() called for " + this.getEntityName());
      }

      if (this.attackTarget != null && this.attackTarget.isDead) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      return this.attackTarget;
   }

   public void setAttackTarget(EntityLivingBase par1EntityLivingBase) {
      if (!this.isAIEnabled()) {
         Minecraft.setErrorMessage("setAttackTarget() called for " + this.getEntityName());
      }

      if (par1EntityLivingBase != null && par1EntityLivingBase.isDead) {
         par1EntityLivingBase = null;
      }

      this.attackTarget = par1EntityLivingBase;
      ForgeHooks.onLivingSetAttackTarget(this, par1EntityLivingBase);
   }

   public boolean canAttackClass(Class par1Class) {
      return EntityCreeper.class != par1Class && EntityGhast.class != par1Class;
   }

   public void eatGrassBonus() {
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(11, (byte)0);
      this.dataWatcher.addObject(10, "");
   }

   public int getTalkInterval() {
      return 80;
   }

   public void makeLivingSound() {
      String var1;
      if (this.isSpooked() && this.rand.nextInt(2) == 0) {
         var1 = this.getHurtSound();
         if (var1 != null) {
            this.makeSound(var1);
            return;
         }
      }

      var1 = this.getLivingSound();
      if (var1 != null) {
         this.makeSound(var1);
      }

   }

   public void makeLongDistanceLivingSound() {
      String var1 = this.getLongDistanceLivingSound();
      if (var1 != null) {
         this.makeLongDistanceSound(var1);
      }

   }

   public boolean isPlayerNearby(double range) {
      return this.worldObj.isPlayerNearby(this.posX, this.posY, this.posZ, range);
   }

   public double distanceToNearestPlayer() {
      return (double)this.worldObj.distanceToNearestPlayer(this.posX, this.posY, this.posZ);
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("mobBaseTick");
      if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.livingSoundTime = -this.getTalkInterval();
         if (this.worldObj.isRemote) {
            return;
         }

         double distance_to_nearest_player = this.distanceToNearestPlayer();
         if (distance_to_nearest_player <= 16.0) {
            this.makeLivingSound();
         } else if (distance_to_nearest_player <= 64.0) {
            this.makeLongDistanceLivingSound();
         }
      }

      this.worldObj.theProfiler.endSection();
   }

   public void spawnExplosionParticle() {
      for(int var1 = 0; var1 < 20; ++var1) {
         double var2 = this.rand.nextGaussian() * 0.02;
         double var4 = this.rand.nextGaussian() * 0.02;
         double var6 = this.rand.nextGaussian() * 0.02;
         double var8 = 10.0;
         this.worldObj.spawnParticle(EnumParticle.explode, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - var2 * var8, this.posY + (double)(this.rand.nextFloat() * this.height) - var4 * var8, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - var6 * var8, var2, var4, var6);
      }

   }

   public void onUpdate() {
      this.senses.clearSensingCache();
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         if (this.isEntityAlive()) {
            this.func_110159_bB();
         } else {
            this.clearLeashed(true, true);
         }

         if (this.increased_chance_of_spreading_fire_countdown > 0) {
            --this.increased_chance_of_spreading_fire_countdown;
         }
      }

      if (!this.worldObj.isRemote && this.target_unique_id_string != null && this.getTarget() == null && this.ticksExisted % 10 == 0) {
         EntityLivingBase target = (EntityLivingBase)this.getNearbyEntityByUniqueID(this.target_unique_id_string);
         if (target != null) {
            this.setTarget(target);
            this.target_unique_id_string = null;
         } else if (this.ticksExisted > 0 && this.ticksExisted % 1000 == 0) {
            this.target_unique_id_string = null;
         }
      }

   }

   protected float func_110146_f(float par1, float par2) {
      if (!this.isAIEnabled() && !(this instanceof EntityOoze)) {
         return super.func_110146_f(par1, par2);
      } else {
         this.bodyHelper.func_75664_a();
         return par2;
      }
   }

   protected String getLivingSound() {
      return null;
   }

   protected String getLongDistanceLivingSound() {
      return null;
   }

   protected int getDropItemId() {
      return 0;
   }

   protected int getDropItemSubtype() {
      return 0;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(3);
      int fortune = damage_source.getLootingModifier();
      if (fortune > 0) {
         num_drops += this.rand.nextInt(fortune + 1);
      }

      if (num_drops > 0 && !recently_hit_by_player) {
         num_drops -= this.rand.nextInt(num_drops + 1);
      }

      for(int i = 0; i < num_drops; ++i) {
         int item_id = this.getDropItemId();
         if (item_id >= 1) {
            ItemStack item_stack = new ItemStack(item_id);
            if (item_stack.getHasSubtypes()) {
               item_stack.setItemSubtype(this.getDropItemSubtype());
            }

            this.dropItemStack(item_stack);
         }
      }

   }

   private boolean hasEquipment() {
      for(int i = 0; i < this.equipment.length; ++i) {
         if (this.equipment[i] != null) {
            return true;
         }
      }

      return false;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
      par1NBTTagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
      NBTTagCompound var4;
      NBTTagList var2;
      int var3;
      if (this.hasEquipment()) {
         var2 = new NBTTagList();

         for(var3 = 0; var3 < this.equipment.length; ++var3) {
            var4 = new NBTTagCompound();
            if (this.equipment[var3] != null) {
               this.equipment[var3].writeToNBT(var4);
            }

            var2.appendTag(var4);
         }

         par1NBTTagCompound.setTag("Equipment", var2);
      }

      var2 = new NBTTagList();

      for(var3 = 0; var3 < this.equipmentDropChances.length; ++var3) {
         var2.appendTag(new NBTTagFloat(var3 + "", this.equipmentDropChances[var3]));
      }

      par1NBTTagCompound.setTag("DropChances", var2);
      par1NBTTagCompound.setString("CustomName", this.getCustomNameTag());
      par1NBTTagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
      par1NBTTagCompound.setBoolean("Leashed", this.isLeashed);
      if (this.leashedToEntity != null) {
         var4 = new NBTTagCompound("Leash");
         if (this.leashedToEntity instanceof EntityLivingBase) {
            var4.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
            var4.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
         } else if (this.leashedToEntity instanceof EntityHanging) {
            EntityHanging var5 = (EntityHanging)this.leashedToEntity;
            var4.setInteger("X", var5.xPosition);
            var4.setInteger("Y", var5.yPosition);
            var4.setInteger("Z", var5.zPosition);
         }

         par1NBTTagCompound.setTag("Leash", var4);
      }

      par1NBTTagCompound.setBoolean("picked_up_a_held_item", this.picked_up_a_held_item);
      par1NBTTagCompound.setLong("spooked_until", this.spooked_until);
      if (this.is_decoy) {
         par1NBTTagCompound.setBoolean("is_decoy", this.is_decoy);
      }

      if (this.came_from_spawner) {
         par1NBTTagCompound.setBoolean("came_from_spawner", true);
      }

      if (this.came_from_spawn_block) {
         par1NBTTagCompound.setBoolean("came_from_spawn_block", true);
         par1NBTTagCompound.setInteger("spawn_block_x", this.spawn_block_x);
         par1NBTTagCompound.setInteger("spawn_block_y", this.spawn_block_y);
         par1NBTTagCompound.setInteger("spawn_block_z", this.spawn_block_z);
      }

      if (this.ticks_disarmed > 0) {
         par1NBTTagCompound.setInteger("ticks_disarmed", this.ticks_disarmed);
      }

      EntityLivingBase target = this.getTarget();
      if (target != null) {
         par1NBTTagCompound.setString("target_unique_id_string", target.getUniqueID().toString());
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setCanPickUpLoot(par1NBTTagCompound.getBoolean("CanPickUpLoot"));
      this.persistenceRequired = par1NBTTagCompound.getBoolean("PersistenceRequired");
      if (par1NBTTagCompound.hasKey("CustomName") && par1NBTTagCompound.getString("CustomName").length() > 0) {
         this.setCustomNameTag(par1NBTTagCompound.getString("CustomName"));
      }

      this.setAlwaysRenderNameTag(par1NBTTagCompound.getBoolean("CustomNameVisible"));
      NBTTagList var2;
      int var3;
      if (par1NBTTagCompound.hasKey("Equipment")) {
         var2 = par1NBTTagCompound.getTagList("Equipment");

         for(var3 = 0; var3 < this.equipment.length; ++var3) {
            this.equipment[var3] = ItemStack.loadItemStackFromNBT((NBTTagCompound)var2.tagAt(var3));
         }
      }

      if (par1NBTTagCompound.hasKey("DropChances")) {
         var2 = par1NBTTagCompound.getTagList("DropChances");

         for(var3 = 0; var3 < var2.tagCount(); ++var3) {
            this.equipmentDropChances[var3] = ((NBTTagFloat)var2.tagAt(var3)).data;
         }
      }

      this.isLeashed = par1NBTTagCompound.getBoolean("Leashed");
      if (this.isLeashed && par1NBTTagCompound.hasKey("Leash")) {
         this.field_110170_bx = par1NBTTagCompound.getCompoundTag("Leash");
      }

      if (par1NBTTagCompound.hasKey("picked_up_a_held_item")) {
         this.picked_up_a_held_item = par1NBTTagCompound.getBoolean("picked_up_a_held_item");
      }

      if (par1NBTTagCompound.hasKey("spooked_until")) {
         this.spooked_until = par1NBTTagCompound.getLong("spooked_until");
      }

      this.is_decoy = par1NBTTagCompound.getBoolean("is_decoy");
      if (par1NBTTagCompound.hasKey("came_from_spawner")) {
         this.came_from_spawner = true;
      }

      if (par1NBTTagCompound.hasKey("came_from_spawn_block")) {
         this.came_from_spawn_block = true;
         this.spawn_block_x = par1NBTTagCompound.getInteger("spawn_block_x");
         this.spawn_block_y = par1NBTTagCompound.getInteger("spawn_block_y");
         this.spawn_block_z = par1NBTTagCompound.getInteger("spawn_block_z");
      }

      if (par1NBTTagCompound.hasKey("ticks_disarmed")) {
         this.ticks_disarmed = par1NBTTagCompound.getInteger("ticks_disarmed");
      }

      if (par1NBTTagCompound.hasKey("target_unique_id_string")) {
         this.target_unique_id_string = par1NBTTagCompound.getString("target_unique_id_string");
         if (this.target_unique_id_string.isEmpty()) {
            this.target_unique_id_string = null;
         }
      }

   }

   public void setMoveForward(float par1) {
      this.moveForward = par1;
   }

   public void setAIMoveSpeed(float par1) {
      super.setAIMoveSpeed(par1);
      this.setMoveForward(par1);
   }

   public boolean canWearItem(ItemStack item_stack) {
      if (item_stack == null) {
         return false;
      } else {
         Item item = item_stack.getItem();
         return item instanceof ItemArmor || item == Item.getItem(Block.pumpkin);
      }
   }

   public AxisAlignedBB getBoundingBoxForItemPickup() {
      return this.boundingBox.expand(1.0, 0.25, 1.0);
   }

   public List getItemsWithinPickupDistance() {
      return this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getBoundingBoxForItemPickup());
   }

   public boolean isItemWithinPickupDistance(EntityItem entity_item) {
      List entity_items = this.getItemsWithinPickupDistance();
      Iterator iterator = entity_items.iterator();

      do {
         if (!iterator.hasNext()) {
            return false;
         }
      } while(entity_item != iterator.next());

      return true;
   }

   public boolean canNeverPickUpItem(Item item) {
      return false;
   }

   private void tryPickUpItems() {
      if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.isDead && !(this.getHealth() <= 0.0F) && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.getTicksExistedWithOffset() % 10 == 0) {
         List entity_items = this.getItemsWithinPickupDistance();
         Iterator iterator = entity_items.iterator();

         while(iterator.hasNext()) {
            EntityItem entity_item = (EntityItem)iterator.next();
            if (!entity_item.isDead && entity_item.getEntityItem() != null) {
               ItemStack item_stack_on_ground = entity_item.getEntityItem();
               Item item_on_ground = item_stack_on_ground.getItem();
               if (!this.canNeverPickUpItem(item_on_ground) && (item_on_ground instanceof ItemTool || this.canWearItem(item_stack_on_ground) || this.getHeldItem() instanceof ItemBow && item_on_ground instanceof ItemBow)) {
                  int var5 = getEquipmentPosition(item_stack_on_ground);
                  if (var5 > -1) {
                     boolean pickup = true;
                     ItemStack current_item_stack = this.getCurrentItemOrArmor(var5);
                     if (current_item_stack != null) {
                        Item current_item = current_item_stack.getItem();
                        if (var5 == 0) {
                           if (current_item instanceof ItemBow) {
                              if (!(item_on_ground instanceof ItemBow)) {
                                 pickup = false;
                              } else if (current_item_stack.isItemEnchanted()) {
                                 pickup = false;
                              } else if (item_stack_on_ground.isItemEnchanted()) {
                                 pickup = true;
                              } else {
                                 pickup = item_stack_on_ground.getItemDamage() < current_item_stack.getItemDamage();
                              }
                           } else if (item_on_ground.isTool() && !current_item.isTool()) {
                              pickup = true;
                           } else if (item_on_ground.isTool() && current_item.isTool()) {
                              ItemTool tool_on_ground = (ItemTool)item_on_ground;
                              ItemTool current_tool = (ItemTool)current_item;
                              if (tool_on_ground.getCombinedDamageVsEntity() == current_tool.getCombinedDamageVsEntity()) {
                                 if (current_item_stack.isItemEnchanted()) {
                                    pickup = false;
                                 } else if (item_stack_on_ground.isItemEnchanted() && !current_item_stack.isItemEnchanted()) {
                                    pickup = true;
                                 } else {
                                    pickup = item_stack_on_ground.getItemDamage() < current_item_stack.getItemDamage() || item_stack_on_ground.hasTagCompound() && !current_item_stack.hasTagCompound();
                                 }
                              } else {
                                 pickup = tool_on_ground.getCombinedDamageVsEntity() > current_tool.getCombinedDamageVsEntity();
                              }
                           } else {
                              pickup = false;
                           }
                        } else if (item_on_ground instanceof ItemArmor && !(current_item instanceof ItemArmor)) {
                           pickup = true;
                        } else if (item_on_ground instanceof ItemArmor && current_item instanceof ItemArmor) {
                           ItemArmor armor_on_ground = (ItemArmor)item_on_ground;
                           ItemArmor current_armor = (ItemArmor)current_item;
                           if (armor_on_ground.getMultipliedProtection(item_stack_on_ground) == current_armor.getMultipliedProtection(current_item_stack)) {
                              if (item_stack_on_ground.isItemEnchanted() && !current_item_stack.isItemEnchanted()) {
                                 pickup = true;
                              } else {
                                 pickup = item_stack_on_ground.getItemDamage() < current_item_stack.getItemDamage() || item_stack_on_ground.hasTagCompound() && !current_item_stack.hasTagCompound();
                              }
                           } else {
                              pickup = armor_on_ground.getMultipliedProtection(item_stack_on_ground) > current_armor.getMultipliedProtection(current_item_stack);
                           }
                        } else {
                           pickup = false;
                        }
                     }

                     if (pickup) {
                        this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        if (current_item_stack != null && this.rand.nextFloat() < this.equipmentDropChances[var5]) {
                           this.dropItemStack(current_item_stack, 0.0F);
                        }

                        boolean set_dead;
                        if (item_stack_on_ground.stackSize == 1) {
                           this.setCurrentItemOrArmor(var5, item_stack_on_ground);
                           set_dead = true;
                        } else {
                           this.setCurrentItemOrArmor(var5, item_stack_on_ground.copy().setStackSize(1));
                           --item_stack_on_ground.stackSize;
                           set_dead = false;
                        }

                        this.equipmentDropChances[var5] = 2.0F;
                        this.persistenceRequired = true;
                        if (set_dead) {
                           this.onItemPickup(entity_item, 1);
                           entity_item.setDead();
                        }

                        if (var5 == 0) {
                           this.picked_up_a_held_item = true;
                        }
                        break;
                     }
                  }
               }
            }
         }

      }
   }

   public float getChanceOfCatchingFireFromSunlightThisTick() {
      if (this.worldObj.isRemote) {
         Minecraft.setErrorMessage("getChanceOfCatchingFireFromSunlightThisTick: called on client?");
         return 0.0F;
      } else {
         if (this.catchesFireInSunlight() && this.worldObj.isDaytime() && !this.isChild() && !this.isWearingHelmet(true) && !this.isInRain() && this.isEntityAlive() && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ))) {
            float brightness = this.getBrightness(1.0F);
            if (brightness > 0.5F) {
               return (brightness - 0.4F) * 2.0F / 30.0F;
            }
         }

         return 0.0F;
      }
   }

   public void onLivingUpdate() {
      if (this.getHealth() <= 0.0F) {
         super.onLivingUpdate();
      }

      if (!this.worldObj.isRemote && this.catchesFireInSunlight() && this.worldObj.isDaytime() && !this.isChild() && !this.isWearingHelmet(true) && !this.isInRain() && !this.worldObj.isSkyOvercast(this.getBlockPosX(), this.getBlockPosZ()) && this.isEntityAlive()) {
         float brightness = this.getBrightness(1.0F);
         if (brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ))) {
            if (this.ticks_since_last_wet < 3) {
               this.causeQuenchEffect();
            } else {
               this.setFire(8);
            }
         }
      }

      super.onLivingUpdate();
      this.worldObj.theProfiler.startSection("looting");
      if (this.ticks_disarmed > 0) {
         --this.ticks_disarmed;
      }

      if (!this.worldObj.isRemote && this.canPickUpLoot()) {
         this.tryPickUpItems();
      }

      if (!this.worldObj.isRemote && !this.isDead && this.getHealth() > 0.0F) {
         boolean refrains_from_eating = false;
         boolean refrains_from_pickup = false;
         if (this instanceof EntityTameable && ((EntityTameable)this).isTamed() && this.getHealthFraction() > 0.5F) {
            refrains_from_eating = true;
         }

         if (this.food_or_repair_item_pickup_cooldown > 0) {
            --this.food_or_repair_item_pickup_cooldown;
            refrains_from_pickup = true;
         }

         if (!refrains_from_pickup) {
            List items = this.getItemsWithinPickupDistance();
            Iterator i = items.iterator();

            label96:
            while(true) {
               EntityItem entity_item;
               do {
                  do {
                     if (!i.hasNext()) {
                        break label96;
                     }

                     entity_item = (EntityItem)i.next();
                  } while(entity_item.isDead);
               } while(!entity_item.canBePickedUpBy(this));

               ItemStack item_stack = entity_item.getEntityItem();
               boolean picked_up = false;
               boolean picked_up_as_valuable = false;
               if (!refrains_from_eating && this.willEat(item_stack)) {
                  this.onFoodEaten(item_stack);
                  picked_up = true;
               } else if (this.willUseForRepair(item_stack)) {
                  this.onRepairItemPickup(item_stack);
                  picked_up = true;
               } else if (this.willPickupAsValuable(item_stack)) {
                  this.addContainedItem(item_stack.getItem());
                  picked_up = true;
                  picked_up_as_valuable = true;
               }

               if (picked_up) {
                  this.food_or_repair_item_pickup_cooldown = 400;
                  if (picked_up_as_valuable) {
                     this.food_or_repair_item_pickup_cooldown = 40;
                  }

                  --item_stack.stackSize;
                  this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                  if (item_stack.stackSize == 0) {
                     entity_item.setDead();
                  } else {
                     entity_item.age = -18000;
                  }

                  this.refreshDespawnCounter(-2400);
                  break;
               }
            }
         }
      }

      this.worldObj.theProfiler.endSection();
   }

   protected boolean isAIEnabled() {
      return false;
   }

   public final boolean isConsideredInViewOfPlayerForDespawningPurposes(EntityPlayer player) {
      if (!player.isGhost() && !player.isZevimrgvInTournament()) {
         Vec3 entity_pos = this.getFootPos();
         double foot_pos_y = entity_pos.yCoord;
         double dx = player.posX - this.posX;
         double dy = player.posY - this.posY;
         double dz = player.posZ - this.posZ;
         double dist_sq = dx * dx + dy * dy + dz * dz;
         if (dist_sq > 4096.0 && !this.isInRangeToRenderDist(dist_sq)) {
            return false;
         } else {
            Vec3 player_eye_pos = player.getEyePos();
            entity_pos.yCoord = foot_pos_y + (double)(this.height * 0.75F);
            if (this.worldObj.checkForLineOfSight(player_eye_pos, entity_pos, false)) {
               return true;
            } else {
               entity_pos.yCoord = foot_pos_y + 0.1;
               if (this.worldObj.checkForLineOfSight(player_eye_pos, entity_pos, false)) {
                  return true;
               } else {
                  entity_pos.yCoord = foot_pos_y + (double)this.height + 0.5;
                  if (this.worldObj.checkForLineOfSight(player_eye_pos, entity_pos, false)) {
                     return true;
                  } else if (this.worldObj.checkForLineOfSight(player_eye_pos.addY(1.0), entity_pos, false)) {
                     return true;
                  } else {
                     if (this.inWater) {
                     }

                     return false;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean isConsideredInViewOfAnyPlayerForDespawningPurposes() {
      int num_players = this.worldObj.playerEntities.size();

      for(int i = 0; i < num_players; ++i) {
         if (this.isConsideredInViewOfPlayerForDespawningPurposes((EntityPlayer)this.worldObj.playerEntities.get(i))) {
            return true;
         }
      }

      return false;
   }

   protected boolean canDespawn() {
      if (this.isConsideredInViewOfAnyPlayerForDespawningPurposes()) {
         this.refreshDespawnCounter(this instanceof EntityWaterMob ? -9600 : -400);
         return false;
      } else {
         return true;
      }
   }

   public void tryDespawnEntity() {
      Result result = null;
      if (this.persistenceRequired) {
         this.despawn_counter = 0;
      } else if (this.despawn_counter >= 200) {
         EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, -1.0, false);
         if (player != null) {
            double dx = player.posX - this.posX;
            double dy = player.posY - this.posY;
            double dz = player.posZ - this.posZ;
            boolean hour_of_grace = this.worldObj.getHourOfDay() == 5;
            if (!hour_of_grace) {
               dy *= 2.0;
            }

            double distance_sq = dx * dx + dy * dy + dz * dz;
            if (distance_sq > (double)(hour_of_grace ? 256 : 256) && this.contained_items.isEmpty() && this.canDespawn()) {
               this.setDead();
            } else {
               this.despawn_counter = (int)(Math.random() * 100.0) - 50;
            }

         }
      } else if ((this.despawn_counter & 0x1F) == 0x1F && (result = ForgeEventFactory.canEntityDespawn(this)) != Result.DEFAULT) {
         if (result == Result.DENY) {
            this.despawn_counter = 0;
         } else {
            this.setDead();
         }
      }
   }

   protected void updateAITasks() {
      if (!this.is_decoy) {
         this.worldObj.theProfiler.startSection("checkDespawn");
         this.tryDespawnEntity();
         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("targetSelector");
         this.targetTasks.onUpdateTasks();
         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("goalSelector");
         this.tasks.onUpdateTasks();
         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("navigation");
         this.navigator.onUpdateNavigation();
         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("mob tick");
         this.updateAITick();
         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.startSection("controls");
         this.worldObj.theProfiler.startSection("move");
         this.moveHelper.onUpdateMoveHelper();
         this.worldObj.theProfiler.endStartSection("look");
         this.lookHelper.onUpdateLook();
         this.worldObj.theProfiler.endStartSection("jump");
         this.jumpHelper.doJump();
         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.endSection();
      }
   }

   protected void updateEntityActionState() {
      super.updateEntityActionState();
      this.moveStrafing = 0.0F;
      this.moveForward = 0.0F;
      this.tryDespawnEntity();
      float var1 = 8.0F;
      if (this.rand.nextFloat() < 0.02F) {
         EntityPlayer var2 = this.worldObj.getClosestPlayerToEntity(this, (double)var1, false);
         if (var2 != null) {
            this.currentTarget = var2;
            this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
         } else {
            this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
         }
      }

      if (this.currentTarget != null) {
         this.faceEntity(this.currentTarget, 10.0F, (float)this.getVerticalFaceSpeed());
         if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > (double)(var1 * var1)) {
            this.currentTarget = null;
         }
      } else {
         if (this.rand.nextFloat() < 0.05F) {
            this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
         }

         this.rotationYaw += this.randomYawVelocity;
         this.rotationPitch = this.defaultPitch;
      }

      boolean var4 = this.isInWater();
      boolean var3 = this.handleLavaMovement();
      if ((var4 || var3) && !this.isPreventedFromSwimmingDueToParalyzation()) {
         this.isJumping = this.rand.nextFloat() < 0.8F;
      }

   }

   public boolean isPreventedFromSwimmingDueToParalyzation() {
      return this.getSpeedBoostVsSlowDown() < -0.5F;
   }

   public int getVerticalFaceSpeed() {
      return 40;
   }

   public void faceEntity(Entity par1Entity, float par2, float par3) {
      double var4 = par1Entity.posX - this.posX;
      double var8 = par1Entity.posZ - this.posZ;
      double var6;
      if (par1Entity instanceof EntityLivingBase) {
         EntityLivingBase var10 = (EntityLivingBase)par1Entity;
         var6 = var10.posY + (double)var10.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
      } else {
         var6 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0 - (this.posY + (double)this.getEyeHeight());
      }

      double var14 = (double)MathHelper.sqrt_double(var4 * var4 + var8 * var8);
      float var12 = (float)(Math.atan2(var8, var4) * 180.0 / Math.PI) - 90.0F;
      float var13 = (float)(-(Math.atan2(var6, var14) * 180.0 / Math.PI));
      this.rotationPitch = this.updateRotation(this.rotationPitch, var13, par3);
      this.rotationYaw = this.updateRotation(this.rotationYaw, var12, par2);
   }

   private float updateRotation(float par1, float par2, float par3) {
      float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);
      if (var4 > par3) {
         var4 = par3;
      }

      if (var4 < -par3) {
         var4 = -par3;
      }

      return par1 + var4;
   }

   public final boolean canSpawnOnLeaves() {
      return this.getClass() == EntityWoodSpider.class || this.getClass() == EntityBlackWidowSpider.class || this instanceof EntityOcelot;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      if (!this.worldObj.checkNoEntityCollision(this.boundingBox)) {
         return false;
      } else if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
         return false;
      } else if (!this.canSpawnOnLeaves() && this.worldObj.getBlock(this.getBlockPosX(), this.getFootBlockPosY() - 1, this.getBlockPosZ()) instanceof BlockLeaves) {
         return false;
      } else if (this instanceof IMob && ((IMob)this).canSpawnInShallowWater()) {
         int x = this.getBlockPosX();
         int y = this.getFootBlockPosY();
         int z = this.getBlockPosZ();
         Block block_above = this.worldObj.getBlock(x, y + 1, z);
         if (block_above != null) {
            if (block_above.blockMaterial == Material.water) {
               return false;
            }

            if (this.worldObj.getBlockMaterial(x, y, z) == Material.water && block_above.isSolid(this.worldObj, x, y + 1, z)) {
               return false;
            }
         }

         return block_above != Block.waterlily && !this.worldObj.isAnyLava(this.boundingBox);
      } else {
         return !this.worldObj.isAnyLiquid(this.boundingBox);
      }
   }

   public float getRenderSizeModifier() {
      return 1.0F;
   }

   public int getMaxSpawnedInChunk() {
      return 4;
   }

   public int getMaxSafePointTries() {
      if (this.getTarget() == null) {
         return 3;
      } else {
         int var1 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         var1 -= (3 - this.worldObj.difficultySetting) * 4;
         if (var1 < 0) {
            var1 = 0;
         }

         return var1 + 3;
      }
   }

   public void setHeldItemStack(ItemStack item_stack) {
      this.equipment[0] = item_stack;
   }

   public ItemStack getHeldItemStack() {
      return this.equipment[0];
   }

   public ItemStack getCurrentItemOrArmor(int par1) {
      return this.equipment[par1];
   }

   public ItemStack func_130225_q(int par1) {
      return this.equipment[par1 + 1];
   }

   public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {
      this.equipment[par1] = par2ItemStack;
   }

   public ItemStack[] getLastActiveItems() {
      return this.equipment;
   }

   protected void dropEquipment(boolean recently_hit_by_player, int par2) {
      for(int var3 = 0; var3 < this.getLastActiveItems().length; ++var3) {
         ItemStack var4 = this.getCurrentItemOrArmor(var3);
         boolean var5 = this.equipmentDropChances[var3] > 1.0F;
         if (var4 != null) {
            boolean is_held_item = var3 == 0;
            if (!this.has_taken_massive_fall_damage || !(this.equipmentDropChances[var3] < 1.0F) || !(this.rand.nextFloat() < 0.9F) || is_held_item && this.picked_up_a_held_item) {
               if (is_held_item && !this.picked_up_a_held_item && !var4.isItemEnchanted() && var4.isItemDamaged() && var4.getItem().hasQuality() && var4.getQuality().isLowerThan(EnumQuality.average)) {
                  float fraction_damaged = (float)var4.getItemDamage() / (float)var4.getMaxDamage();
                  if (fraction_damaged > 0.7F && this.rand.nextFloat() < 0.75F) {
                     continue;
                  }
               }

               if (this instanceof EntityLongdead && var4.getQuality().isAverageOrHigher() && is_held_item && !this.picked_up_a_held_item) {
                  is_held_item = false;
               }

               if (is_held_item && (recently_hit_by_player || this.picked_up_a_held_item || this.rand.nextFloat() < 0.05F) || (recently_hit_by_player || var5) && this.rand.nextFloat() - (float)par2 * 0.01F < this.equipmentDropChances[var3]) {
                  if (!var5 && var4.isItemStackDamageable()) {
                  }

                  this.dropItemStack(var4, 0.0F);
                  this.setCurrentItemOrArmor(var3, (ItemStack)null);
               }
            }
         }
      }

   }

   protected void addRandomArmor() {
      if (this.rand.nextFloat() < 0.15F * this.worldObj.getLocationTensionFactor(this.posX, this.posY, this.posZ)) {
         int var1 = this.rand.nextInt(2);
         float var2 = this.worldObj.difficultySetting == 3 ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            ++var1;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++var1;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++var1;
         }

         for(int var3 = 3; var3 >= 0; --var3) {
            ItemStack var4 = this.func_130225_q(var3);
            if (var3 < 3 && this.rand.nextFloat() < var2) {
               break;
            }

            if (var4 == null) {
               Item var5 = getArmorItemForSlot(var3 + 1, var1);
               if (var5 != null) {
                  this.setCurrentItemOrArmor(var3 + 1, (new ItemStack(var5)).randomizeForMob(this, true));
               }
            }
         }
      }

   }

   public static int getEquipmentPosition(ItemStack par0ItemStack) {
      if (par0ItemStack.itemID != Block.pumpkin.blockID && par0ItemStack.itemID != Item.skull.itemID) {
         if (par0ItemStack.getItem() instanceof ItemArmor) {
            switch (((ItemArmor)par0ItemStack.getItem()).armorType) {
               case 0:
                  return 4;
               case 1:
                  return 3;
               case 2:
                  return 2;
               case 3:
                  return 1;
            }
         }

         return 0;
      } else {
         return 4;
      }
   }

   public static Item getArmorItemForSlot(int par0, int par1) {
      switch (par0) {
         case 4:
            if (par1 == 0) {
               return Item.helmetLeather;
            } else if (par1 == 1) {
               return Item.helmetChainRustedIron;
            } else if (par1 == 2) {
               return Item.helmetChainCopper;
            } else if (par1 == 3) {
               return Item.helmetRustedIron;
            } else if (par1 == 4) {
               return Item.helmetCopper;
            }
         case 3:
            if (par1 == 0) {
               return Item.plateLeather;
            } else if (par1 == 1) {
               return Item.plateChainRustedIron;
            } else if (par1 == 2) {
               return Item.plateChainCopper;
            } else if (par1 == 3) {
               return Item.plateRustedIron;
            } else if (par1 == 4) {
               return Item.plateCopper;
            }
         case 2:
            if (par1 == 0) {
               return Item.legsLeather;
            } else if (par1 == 1) {
               return Item.legsChainRustedIron;
            } else if (par1 == 2) {
               return Item.legsChainCopper;
            } else if (par1 == 3) {
               return Item.legsRustedIron;
            } else if (par1 == 4) {
               return Item.legsCopper;
            }
         case 1:
            if (par1 == 0) {
               return Item.bootsLeather;
            } else if (par1 == 1) {
               return Item.bootsChainRustedIron;
            } else if (par1 == 2) {
               return Item.bootsChainCopper;
            } else if (par1 == 3) {
               return Item.bootsRustedIron;
            } else if (par1 == 4) {
               return Item.bootsCopper;
            }
         default:
            return null;
      }
   }

   public void enchantEquipment(ItemStack item_stack) {
      float tension = this.worldObj.getLocationTensionFactor(this.posX, this.posY, this.posZ);
      if (this.rand.nextFloat() < 0.1F * tension) {
         EnchantmentHelper.addRandomEnchantment(this.rand, item_stack, (int)(5.0F + tension * (float)this.rand.nextInt(18)));
      }

   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
      return par1EntityLivingData;
   }

   public boolean canBeSteered() {
      return false;
   }

   public String getEntityName() {
      return this.hasCustomNameTag() ? this.getCustomNameTag() : super.getEntityName();
   }

   public void func_110163_bv() {
      this.persistenceRequired = true;
   }

   public void setCustomNameTag(String par1Str) {
      this.dataWatcher.updateObject(10, par1Str);
   }

   public String getCustomNameTag() {
      return this.dataWatcher.getWatchableObjectString(10);
   }

   public boolean hasCustomNameTag() {
      return this.dataWatcher.getWatchableObjectString(10).length() > 0;
   }

   public void setAlwaysRenderNameTag(boolean par1) {
      this.dataWatcher.updateObject(11, (byte)(par1 ? 1 : 0));
   }

   public boolean getAlwaysRenderNameTag() {
      return this.dataWatcher.getWatchableObjectByte(11) == 1;
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return this.getAlwaysRenderNameTag();
   }

   public void setEquipmentDropChance(int par1, float par2) {
      this.equipmentDropChances[par1] = par2;
   }

   public boolean canPickUpLoot() {
      if (!this.isDead && !(this.getHealth() <= 0.0F)) {
         return this.ticks_disarmed > 0 ? false : this.canPickUpLoot;
      } else {
         return false;
      }
   }

   public void setCanPickUpLoot(boolean par1) {
      this.canPickUpLoot = par1;
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (!this.getLeashed() || this.getLeashedToEntity().isEntityPlayer() && this.getLeashedToEntity() != player) {
         return false;
      } else {
         if (this.onClient()) {
            player.swingArm();
         } else {
            this.clearLeashed(!player.inCreativeMode(), true);
         }

         return true;
      }
   }

   protected void func_110159_bB() {
      if (this.field_110170_bx != null) {
         this.recreateLeash();
      }

      if (this.isLeashed && (this.leashedToEntity == null || this.leashedToEntity.isDead)) {
         this.clearLeashed(true, true);
      }

   }

   public final void clearLeashed(boolean drop_leash_item, boolean send_packet_to_tracking_players) {
      if (this.isLeashed) {
         if (this.leashedToEntity instanceof EntityLeashKnot) {
            List entities_tied_to_leash_knot = ItemLeash.getEntitiesThatAreLeashedToEntity(this.leashedToEntity);
            if (entities_tied_to_leash_knot.size() == 1) {
               this.leashedToEntity.setDead();
            }
         }

         this.isLeashed = false;
         this.leashedToEntity = null;
         if (this.onServer()) {
            if (drop_leash_item) {
               this.dropItem(Item.leash.itemID, 1);
            }

            if (send_packet_to_tracking_players) {
               this.sendPacketToAllPlayersTrackingEntity(new Packet39AttachEntity(1, this, (Entity)null));
            }
         }
      }

   }

   public boolean allowLeashing() {
      return !this.getLeashed() && !(this instanceof IMob);
   }

   public boolean getLeashed() {
      return this.isLeashed;
   }

   public Entity getLeashedToEntity() {
      return this.leashedToEntity;
   }

   public void setLeashedToEntity(Entity par1Entity, boolean par2) {
      this.isLeashed = true;
      this.leashedToEntity = par1Entity;
      if (!this.worldObj.isRemote && par2 && this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet39AttachEntity(1, this, this.leashedToEntity));
      }

   }

   private void recreateLeash() {
      if (this.isLeashed && this.field_110170_bx != null) {
         if (this.field_110170_bx.hasKey("UUIDMost") && this.field_110170_bx.hasKey("UUIDLeast")) {
            UUID var5 = new UUID(this.field_110170_bx.getLong("UUIDMost"), this.field_110170_bx.getLong("UUIDLeast"));
            List var6 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0, 10.0, 10.0));
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               EntityLivingBase var8 = (EntityLivingBase)var7.next();
               if (var8.getUniqueID().equals(var5)) {
                  this.leashedToEntity = var8;
                  break;
               }
            }
         } else if (this.field_110170_bx.hasKey("X") && this.field_110170_bx.hasKey("Y") && this.field_110170_bx.hasKey("Z")) {
            int var1 = this.field_110170_bx.getInteger("X");
            int var2 = this.field_110170_bx.getInteger("Y");
            int var3 = this.field_110170_bx.getInteger("Z");
            EntityLeashKnot var4 = EntityLeashKnot.getKnotForBlock(this.worldObj, var1, var2, var3);
            if (var4 == null) {
               var4 = EntityLeashKnot.func_110129_a(this.worldObj, var1, var2, var3);
            }

            this.setLeashedToEntity(var4, true);
         } else {
            this.clearLeashed(true, false);
         }
      }

      this.field_110170_bx = null;
   }

   public float getMaxTargettingRange() {
      AttributeInstance var1 = this.getEntityAttribute(SharedMonsterAttributes.followRange);
      float max_range;
      if (var1 == null) {
         max_range = 16.0F;
      } else {
         max_range = (float)var1.getAttributeValue();
      }

      if (this.recentlyHit > 0) {
         max_range *= 2.0F;
      }

      return max_range;
   }

   public PathEntity findPathTowardXYZ(int x, int y, int z, int max_path_length, boolean use_navigator) {
      return this.worldObj.findEntityPathTowardXYZ(this, x, y, z, max_path_length, use_navigator);
   }

   public PathEntity findPathAwayFromXYZ(int x, int y, int z, int min_distance, int max_path_length, boolean use_navigator) {
      return this.worldObj.findEntityPathAwayFromXYZ(this, x, y, z, min_distance, max_path_length, use_navigator);
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return false;
   }

   public boolean isRepairItem(ItemStack item_stack) {
      return false;
   }

   public boolean canEat() {
      return !this.isChild() && !this.isDead && this.getHealth() > 0.0F;
   }

   public boolean willEat(ItemStack item_stack) {
      return this.isFoodItem(item_stack) && this.canEat();
   }

   public void onFoodEaten(ItemStack item_stack) {
      if (!this.isEntityUndead()) {
         this.healByPercentage(0.5F);
      }

   }

   public boolean willUseForRepair(ItemStack item_stack) {
      return this.getHealthFraction() < 1.0F && this.isRepairItem(item_stack);
   }

   public void onRepairItemPickup(ItemStack item_stack) {
      this.healByPercentage(0.5F);
   }

   public boolean willPickupAsValuable(ItemStack item_stack) {
      return false;
   }

   public void warnPeersOfAttacker(Class peer_class, Entity attacker) {
      if (attacker != null) {
         List peers = this.worldObj.getEntitiesWithinAABB(peer_class, this.boundingBox.expand(8.0, 4.0, 8.0));

         for(int i = 0; i < peers.size(); ++i) {
            EntityLiving entity_living = (EntityLiving)peers.get(i);
            if ((!(entity_living instanceof EntityTameable) || !((EntityTameable)entity_living).isTamed()) && entity_living.getLastHarmingEntity() == null) {
               entity_living.setLastHarmingEntity(attacker);
               entity_living.considerFleeing();
               if (entity_living.has_decided_to_flee) {
                  if (entity_living instanceof EntityAnimal && ((EntityAnimal)entity_living).isInLove()) {
                     ((EntityAnimal)entity_living).setInLove(0);
                  }
               } else {
                  entity_living.setLastHarmingEntity((Entity)null);
               }
            }
         }

      }
   }

   public boolean isNearLitTorch() {
      return this.isNearLitTorch(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
   }

   public boolean isNearLitTorch(int x, int y, int z) {
      for(int dx = -1; dx <= 1; ++dx) {
         for(int dy = -1; dy <= 1 + (int)this.height; ++dy) {
            for(int dz = -1; dz <= 1; ++dz) {
               int block_id = this.worldObj.getBlockId(x + dx, y + dy, z + dz);
               if (block_id == Block.torchWood.blockID || block_id == Block.torchRedstoneActive.blockID || block_id == Block.pumpkinLantern.blockID) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isNearBlock(int x, int y, int z, Block block) {
      for(int dx = -1; dx <= 1; ++dx) {
         for(int dy = -1; dy <= 1 + (int)this.height; ++dy) {
            for(int dz = -1; dz <= 1; ++dz) {
               if (this.worldObj.getBlock(x + dx, y + dy, z + dz) == block) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean tryDisableNearbyLightSource() {
      if (!this.worldObj.isRemote && this.recentlyHit == 0 && this.distanceToNearestPlayer() > 4.0) {
         int x = MathHelper.floor_double(this.posX);
         int y = MathHelper.floor_double(this.posY);
         int z = MathHelper.floor_double(this.posZ);

         for(int dx = -1; dx <= 1; ++dx) {
            for(int dy = -1; dy <= 1 + (int)this.height; ++dy) {
               for(int dz = -1; dz <= 1; ++dz) {
                  int block_id = this.worldObj.getBlockId(x + dx, y + dy, z + dz);
                  if (block_id != Block.torchWood.blockID && block_id != Block.torchRedstoneActive.blockID) {
                     if (block_id == Block.pumpkinLantern.blockID && this.worldObj.setBlock(x + dx, y + dy, z + dz, Block.pumpkin.blockID, this.worldObj.getBlockMetadata(x + dx, y + dy, z + dz), 3)) {
                        EntityItem entity_item = new EntityItem(this.worldObj, (double)(x + dx), (double)(y + dy), (double)(z + dz), new ItemStack(Block.torchWood));
                        entity_item.delayBeforeCanPickup = 10;
                        this.worldObj.spawnEntityInWorld(entity_item);
                        this.playSound("random.pop", 0.05F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        return true;
                     }
                  } else {
                     BlockBreakInfo info = (new BlockBreakInfo(this.worldObj, x + dx, y + dy, z + dz)).setHarvestedBy(this);
                     if (this.worldObj.setBlockToAir(x + dx, y + dy, z + dz)) {
                        Block.blocksList[block_id].dropBlockAsEntityItem(info);
                        this.playSound("random.pop", 0.05F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean isSpooked() {
      boolean is_spooked = this.spooked_until > this.worldObj.getTotalWorldTime();
      if (!is_spooked) {
         this.spooked_until = 0L;
      }

      return is_spooked;
   }

   public ItemStack[] getWornItems() {
      ItemStack[] equipment = this.getLastActiveItems();
      ItemStack[] armors = new ItemStack[equipment.length - 1];

      for(int i = 1; i < equipment.length; ++i) {
         armors[i - 1] = equipment[i];
      }

      return armors;
   }

   public boolean setWornItem(int slot_index, ItemStack item_stack) {
      ++slot_index;
      if (item_stack == this.equipment[slot_index]) {
         return false;
      } else {
         this.equipment[slot_index] = item_stack;
         return true;
      }
   }

   public boolean isWearing(ItemStack item_stack) {
      ItemStack[] worn_items = this.getWornItems();

      for(int i = 0; i < worn_items.length; ++i) {
         if (worn_items[i] == item_stack) {
            return true;
         }
      }

      return false;
   }

   public boolean canPathTo(int x, int y, int z, int max_distance) {
      PathEntity path = this.getNavigator().getPathToXYZ(x, y, z, max_distance);
      if (path == null) {
         return false;
      } else {
         PathPoint final_point = path.getFinalPathPoint();
         return final_point.xCoord == x && final_point.yCoord == y && final_point.zCoord == z;
      }
   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public void moveEntityWithHeading(float par1, float par2) {
      if (!this.is_decoy) {
         super.moveEntityWithHeading(par1, par2);
      }
   }

   public boolean isEntityInvulnerable() {
      return this.is_decoy ? true : super.isEntityInvulnerable();
   }

   public void setAsDecoy() {
      this.is_decoy = true;
   }

   public boolean isDecoy() {
      return this.is_decoy;
   }

   public void setSpawnBlock(int x, int y, int z) {
      this.came_from_spawner = true;
      this.came_from_spawn_block = true;
      this.spawn_block_x = x;
      this.spawn_block_y = y;
      this.spawn_block_z = z;
   }

   public void onDeath(DamageSource par1DamageSource) {
      if (this.recentlyHit > 0 && !this.worldObj.isRemote && this.came_from_spawn_block) {
         BlockMobSpawner.incrementSpawnsKilled(this.worldObj, this.spawn_block_x, this.spawn_block_y, this.spawn_block_z);
      }

      super.onDeath(par1DamageSource);
   }

   public void clearMatchingEquipmentSlot(ItemStack item_stack) {
      for(int i = 0; i < this.equipment.length; ++i) {
         if (this.equipment[i] == item_stack) {
            this.equipment[i] = null;
            return;
         }
      }

   }

   public void causeBreakingItemEffect(Item item) {
      if (item.hasBreakingEffect()) {
         this.entityFX(EnumEntityFX.item_breaking, (new SignalData()).setByte(-1).setShort(item.itemID));
      }

   }

   public final EntityPlayer getClosestVulnerablePlayer(double max_distance) {
      return this.worldObj.getClosestVulnerablePlayer(this, max_distance, this.requiresLineOfSightToTargets());
   }

   public boolean requiresLineOfSightToTargets() {
      return true;
   }

   protected EntityPlayer findPlayerToAttack(float max_distance) {
      if (this.isAIEnabled()) {
         Minecraft.setErrorMessage(this.getEntityName() + " using findPlayerToAttack?");
      }

      EntityPlayer player = this.getClosestVulnerablePlayer((double)max_distance);
      return player;
   }

   protected Entity findNonPlayerToAttack(float max_distance) {
      if (this.isAIEnabled()) {
         Minecraft.setErrorMessage(this.getEntityName() + " using findNonPlayerToAttack?");
      }

      return null;
   }

   public final EntityLivingBase getTarget() {
      if (this.isAIEnabled()) {
         return this.getAttackTarget();
      } else if (this instanceof EntityCreature) {
         EntityCreature creature = (EntityCreature)this;
         Entity entity = creature.getEntityToAttack();
         if (entity != null && !(entity instanceof EntityLivingBase)) {
            Minecraft.setErrorMessage("getTarget: target is not an EntityLivingBase (" + entity.getEntityName() + ")");
            return null;
         } else {
            return (EntityLivingBase)entity;
         }
      } else if (!(this instanceof EntityCubic) && !(this instanceof EntityGhast) && !(this instanceof EntityDragon)) {
         Minecraft.setErrorMessage("getTarget: unsure how to handle " + this.getEntityName());
         return null;
      } else {
         return null;
      }
   }

   public void setTarget(EntityLivingBase target) {
      if (target != null && target.isDead) {
         target = null;
      }

      if (this.isAIEnabled()) {
         if (this.getAttackTarget() == null) {
            this.AI_retarget = target;
         }

         this.setAttackTarget(target);
      } else if (this instanceof EntityCreature) {
         EntityCreature creature = (EntityCreature)this;
         creature.setEntityToAttack(target);
      } else if (!(this instanceof EntityCubic) && !(this instanceof EntityGhast) && !(this instanceof EntityDragon)) {
         Minecraft.setErrorMessage("setTarget: unsure how to handle " + this.getEntityName());
      } else {
         Minecraft.setErrorMessage("setTarget: cannot set a target for " + this.getEntityName());
      }

   }

   public boolean catchesFireInSunlight() {
      return this.isEntityUndead();
   }

   public float getReach() {
      if (!this.isAIEnabled()) {
         Minecraft.setErrorMessage("getReach: doesn't handle old AI mobs yet");
         return 0.0F;
      } else {
         return 1.5F + this.getHeldItemReachBonus() * 0.6F;
      }
   }

   public List getMeleeAttackPoints() {
      List melee_attack_points = new ArrayList();
      melee_attack_points.add(this.getPrimaryPointOfAttack());
      return melee_attack_points;
   }

   public boolean hasLineOfStrike(Vec3 target_pos) {
      Iterator i = this.getMeleeAttackPoints().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(!this.worldObj.checkForLineOfPhysicalReach((Vec3)i.next(), target_pos));

      return true;
   }

   public boolean hasLineOfStrike(Entity target) {
      List target_points = target.getTargetPoints();
      if (target_points != null) {
         Iterator i = target_points.iterator();

         while(i.hasNext()) {
            if (this.hasLineOfStrike((Vec3)i.next())) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isTargetWithinStrikingDistance(EntityLivingBase target) {
      if (!this.isAIEnabled()) {
         Minecraft.setErrorMessage("isTargetWithinStrikingDistance: doesn't handle old AI mobs yet");
         return false;
      } else if (this instanceof EntityAnimal) {
         double var2 = (double)(this.width * 1.75F * this.width * 1.75F + target.width);
         if (this.getHeldItemStack() != null) {
            var2 += (double)this.getHeldItemStack().getItem().getReachBonus();
         }

         return this.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ) <= var2;
      } else {
         return this.getDistance(target.posX, target.boundingBox.minY, target.posZ) <= (double)this.getReach();
      }
   }

   public boolean hasLineOfStrikeAndTargetIsWithinStrikingDistance(EntityLivingBase target) {
      return this.isTargetWithinStrikingDistance(target) && this.hasLineOfStrike((Entity)target);
   }

   public boolean canSeeTarget(boolean ignore_leaves) {
      return this.getEntitySenses().canSee(this.getTarget(), ignore_leaves);
   }

   public EntityAIBase getEntityAITask(Class _class) {
      if (!this.isAIEnabled()) {
         Minecraft.setErrorMessage("getEntityAITask: being called for " + this.getEntityName() + " which uses old AI");
      }

      return this.tasks.getTask(_class);
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      if (damage.getSource() == DamageSource.inWall && this.ticksExisted < 3) {
         this.pushOutOfBlocks();
         return null;
      } else {
         return super.attackEntityFrom(damage);
      }
   }

   public void onMeleeAttacked(EntityLivingBase attacker, EntityDamageResult result) {
      super.onMeleeAttacked(attacker, result);
      if (!attacker.isHoldingItemThatPreventsHandDamage()) {
         float chance_of_back_damage = 0.0F;
         float amount_of_back_damage = 1.0F;
         if (this instanceof EntityCubic) {
            EntityCubic entity_cubic = (EntityCubic)this;
            chance_of_back_damage = 1.0F;
            amount_of_back_damage = (float)entity_cubic.getAttackStrengthMultiplierForType();
         }

         if (!(this instanceof EntityFireElemental) && !(this instanceof EntityBlaze)) {
            if (this.getTarget() == attacker && !attacker.canOnlyPerformWeakStrike()) {
               chance_of_back_damage = 0.125F;
            }
         } else {
            chance_of_back_damage = 1.0F;
         }

         if (this.rand.nextFloat() < chance_of_back_damage) {
            attacker.attackEntityFrom(new Damage(DamageSource.causeMobDamage(this).setHandDamage(), amount_of_back_damage));
         }
      }

   }

   public boolean isTargettingAPlayer() {
      return this.getTarget() instanceof EntityPlayer;
   }

   public EnumCreatureType getCreatureType() {
      return EnumCreatureType.getCreatureType(this);
   }

   public boolean isFrenzied() {
      return false;
   }

   public boolean isComfortableInLava() {
      return false;
   }

   public void onSendToClient(Packet24MobSpawn packet) {
   }

   public double getCurrentSpeed() {
      return Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
   }
}
