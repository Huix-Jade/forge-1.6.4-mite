package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockInfo;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHorseArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.mite.MITEConstant;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.Curse;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.DebugAttack;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;

public abstract class EntityLivingBase extends Entity {
   private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier sprintingSpeedBoostModifier;
   private BaseAttributeMap attributeMap;
   private final CombatTracker _combatTracker = new CombatTracker(this);
   private final HashMap activePotionsMap = new HashMap();
   private final ItemStack[] previousEquipment = new ItemStack[5];
   public boolean isSwingInProgress;
   public int swingProgressInt;
   public int arrowHitTimer;
   public float prevHealth;
   public int hurtTime;
   public int maxHurtTime;
   public float attackedAtYaw;
   public int deathTime;
   public int attackTime;
   public float prevSwingProgress;
   public float swingProgress;
   public float prevLimbSwingAmount;
   public float limbSwingAmount;
   public float limbSwing;
   public int maxHurtResistantTime = 20;
   public float prevCameraPitch;
   public float cameraPitch;
   public float field_70769_ao;
   public float field_70770_ap;
   public float renderYawOffset;
   public float prevRenderYawOffset;
   public float rotationYawHead;
   public float prevRotationYawHead;
   public float jumpMovementFactor = 0.02F;
   protected EntityPlayer attackingPlayer;
   private Entity last_harming_entity;
   private int last_harming_entity_memory_countdown;
   public boolean has_decided_to_flee;
   public boolean fleeing;
   private String last_harming_entity_unique_id_string;
   public int recentlyHit;
   protected int scoreValue;
   protected float lastDamage;
   protected boolean isJumping;
   public float moveStrafing;
   public float moveForward;
   protected float randomYawVelocity;
   protected int newPosRotationIncrements;
   public double newPosX;
   public double newPosY;
   public double newPosZ;
   protected double newRotationYaw;
   protected double newRotationPitch;
   private boolean potionsNeedUpdate = true;
   private EntityLivingBase entityLivingToAttack;
   private int revengeTimer;
   private EntityLivingBase lastAttackTarget;
   private int lastAttackTime;
   private float landMovementFactor;
   private int jumpTicks;
   private float field_110151_bq;
   public List contained_items = new ArrayList();
   public Entity was_killed_by = null;
   public boolean has_taken_massive_fall_damage;
   private int knockback_resistant_ticks;
   public static final int RESISTANCE_POISON = 1;
   public static final int RESISTANCE_PARALYSIS = 2;
   public static final int RESISTANCE_DRAIN = 3;
   public static final int RESISTANCE_SHADOW = 4;
   public boolean is_collided_with_entities;

   public EntityLivingBase(World par1World) {
      super(par1World);
      this.applyEntityAttributes();
      this.setHealth(this.getMaxHealth());
      this.preventEntitySpawning = true;
      this.field_70770_ap = (float)(Math.random() + 1.0) * 0.01F;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.field_70769_ao = (float)Math.random() * 12398.0F;
      this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
      this.rotationYawHead = this.rotationYaw;
      this.stepHeight = 0.5F;
   }

   protected void entityInit() {
      this.dataWatcher.addObject(7, 0);
      this.dataWatcher.addObject(8, (byte)0);
      this.dataWatcher.addObject(9, (byte)0);
      this.dataWatcher.addObject(6, 1.0F);
   }

   protected void applyEntityAttributes() {
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth);
      this.setEntityAttribute(SharedMonsterAttributes.knockbackResistance);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (!this.isAIEnabled()) {
         this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.10000000149011612);
      }

   }

   public void reapplyEntityAttributes() {
      float max_health_before = (float)this.getEntityAttributeValue(SharedMonsterAttributes.maxHealth);
      this.applyEntityAttributes();
      float max_health_after = (float)this.getEntityAttributeValue(SharedMonsterAttributes.maxHealth);
      if (max_health_after > max_health_before) {
         this.setHealth(this.getHealth() + max_health_after - max_health_before);
      } else if (this.getHealth() > max_health_after) {
         this.setHealth(max_health_after);
      }

   }

   protected void updateFallState(double par1, boolean par3) {
      if (!this.isInWater()) {
         this.handleWaterMovement();
      }

      if (par3 && this.fallDistance > 0.0F) {
         int var4 = MathHelper.floor_double(this.posX);
         int var5 = MathHelper.floor_double(this.posY - 0.20000000298023224 - (double)this.yOffset);
         int var6 = MathHelper.floor_double(this.posZ);
         int var7 = this.worldObj.getBlockId(var4, var5, var6);
         if (var7 == 0) {
            int var8 = this.worldObj.blockGetRenderType(var4, var5 - 1, var6);
            if (var8 == 11 || var8 == 32 || var8 == 21) {
               var7 = this.worldObj.getBlockId(var4, var5 - 1, var6);
            }
         }

         if (var7 > 0) {
            Block.blocksList[var7].onFallenUpon(this.worldObj, var4, var5, var6, this, this.fallDistance);
         }
      }

      super.updateFallState(par1, par3);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   public boolean breathesAir() {
      return this.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
   }

   public void onEntityUpdate() {
      this.prevSwingProgress = this.swingProgress;
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("livingEntityBaseTick");
      if (this.onServer() && this.isEntityAlive()) {
         if (this instanceof EntitySilverfish) {
            if (this.isSilverfishInsideDamagingOpaqueBlock()) {
               this.attackEntityFrom(new Damage(DamageSource.inWall, 1.0F));
            }
         } else if (this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(new Damage(DamageSource.inWall, 1.0F));
         }
      }

      if (!this.canCatchFire() || this.worldObj.isRemote) {
         this.extinguish();
      }

      boolean var1 = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;
      if (!this.breathesAir()) {
         var1 = true;
      }

      if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water)) {
         if (this instanceof EntityChicken && this.isChild() && this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ)) == 0) {
            this.setAir(300);
            var1 = true;
         }

         if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !var1) {
            this.setAir(this.decreaseAirSupply(this.getAir()));
            if (this.getAir() <= -20) {
               this.setAir(0);

               for(int var2 = 0; var2 < 8; ++var2) {
                  float var3 = this.rand.nextFloat() - this.rand.nextFloat();
                  float var4 = this.rand.nextFloat() - this.rand.nextFloat();
                  float var5 = this.rand.nextFloat() - this.rand.nextFloat();
                  this.worldObj.spawnParticle(EnumParticle.bubble, this.posX + (double)var3, this.posY + (double)var4, this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
               }

               if (this.onServer()) {
                  this.attackEntityFrom(new Damage(DamageSource.drown, 2.0F));
               }
            }
         }

         this.extinguish();
         if (!this.worldObj.isRemote && this.isRiding() && ridingEntity!=null && ridingEntity.shouldDismountInWater(this))
         {
            this.mountEntity((Entity)null);
         }
      } else if (this.isEntityPlayer()) {
         this.setAir(MathHelper.clamp_int(this.getAir() + 10, 0, 300));
      } else {
         this.setAir(300);
      }

      this.prevCameraPitch = this.cameraPitch;
      if (this.attackTime > 0) {
         --this.attackTime;
      }

      if (this.hurtTime > 0) {
         --this.hurtTime;
      }

      if (this.hurtResistantTime > 0) {
         --this.hurtResistantTime;
      }

      if (this.knockback_resistant_ticks > 0) {
         --this.knockback_resistant_ticks;
      }

      if (this.getHealth() <= 0.0F) {
         this.onDeathUpdate();
      }

      if (this.recentlyHit > 0) {
         --this.recentlyHit;
      } else {
         this.attackingPlayer = null;
      }

      if (this.lastAttackTarget != null && !this.lastAttackTarget.isEntityAlive()) {
         this.lastAttackTarget = null;
      }

      if (this.entityLivingToAttack != null && !this.entityLivingToAttack.isEntityAlive()) {
         this.setRevengeTarget((EntityLivingBase)null);
      }

      this.updatePotionEffects();
      this.prevRenderYawOffset = this.renderYawOffset;
      this.prevRotationYawHead = this.rotationYawHead;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.worldObj.theProfiler.endSection();
   }

   public boolean isChild() {
      return false;
   }

   protected void onDeathUpdate() {
      ++this.deathTime;
      if (this instanceof EntityShadow || this instanceof EntityFireElemental || this instanceof EntityNightwing) {
         this.deathTime = 20;
      }

      if (this.deathTime == 20) {
         int var1;
         EntityXPOrb xp_orb;
         if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isEntityPlayer()) && !this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
            for(var1 = this.getExperienceValue(); var1 > 0; this.worldObj.spawnEntityInWorld(xp_orb)) {
               int var2 = EntityXPOrb.getXPSplit(var1);
               var1 -= var2;
               xp_orb = new EntityXPOrb(this.worldObj, this.posX, this.posY + 0.5, this.posZ, var2);
               if (this.isEntityPlayer()) {
                  EntityPlayer player = (EntityPlayer)this;
                  xp_orb.setPlayerThisBelongsTo(player.username);
                  xp_orb.xpOrbAge = -18000;
               }
            }
         }

         this.setDead();

         for(var1 = 0; var1 < 20; ++var1) {
            double var8 = this.rand.nextGaussian() * 0.02;
            double var4 = this.rand.nextGaussian() * 0.02;
            double var6 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(EnumParticle.explode, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var8, var4, var6);
         }
      }

   }

   public void spawnDeathParticles() {
      for(int i = 0; i < 20; ++i) {
         double var8 = this.rand.nextGaussian() * 0.02;
         double var4 = this.rand.nextGaussian() * 0.02;
         double var6 = this.rand.nextGaussian() * 0.02;
         this.worldObj.spawnParticle(EnumParticle.explode, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var8, var4, var6);
      }

   }

   protected int decreaseAirSupply(int par1) {
      if (this instanceof EntityPlayer && ((EntityPlayer)this).hasCurse(Curse.cannot_hold_breath, true)) {
      }

      int var2 = EnchantmentHelper.getRespiration(this);
      return var2 > 0 && this.rand.nextInt(var2 + 1) > 0 ? par1 : par1 - 1;
   }

   public final Random getRNG() {
      return this.rand;
   }

   public EntityLivingBase getAITarget() {
      return this.entityLivingToAttack;
   }

   public void setAITarget(EntityLivingBase target) {
      this.entityLivingToAttack = target;
   }

   public int func_142015_aE() {
      return this.revengeTimer;
   }

   public void setRevengeTarget(EntityLivingBase par1EntityLivingBase) {
      this.entityLivingToAttack = par1EntityLivingBase;
      this.revengeTimer = this.ticksExisted;
      ForgeHooks.onLivingSetAttackTarget(this, par1EntityLivingBase);
   }

   public EntityLivingBase getLastAttackTarget() {
      return this.lastAttackTarget;
   }

   public int getLastAttackTime() {
      return this.lastAttackTime;
   }

   public void setLastAttackTarget(Entity par1Entity) {
      if (par1Entity instanceof EntityLivingBase) {
         this.lastAttackTarget = (EntityLivingBase)par1Entity;
      } else {
         this.lastAttackTarget = null;
      }

      this.lastAttackTime = this.ticksExisted;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setFloat("HealF", this.getHealth());
      par1NBTTagCompound.setShort("Health", (short)((int)Math.ceil((double)this.getHealth())));
      par1NBTTagCompound.setShort("HurtTime", (short)this.hurtTime);
      par1NBTTagCompound.setShort("DeathTime", (short)this.deathTime);
      par1NBTTagCompound.setShort("AttackTime", (short)this.attackTime);
      par1NBTTagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());
      ItemStack[] var2 = this.getLastActiveItems();
      int var3 = var2.length;

      int var4;
      ItemStack var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5 != null) {
            this.attributeMap.removeAttributeModifiers(var5.getAttributeModifiers());
         }
      }

      par1NBTTagCompound.setTag("Attributes", SharedMonsterAttributes.func_111257_a(this.getAttributeMap()));
      var2 = this.getLastActiveItems();
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5 != null) {
            this.attributeMap.applyAttributeModifiers(var5.getAttributeModifiers());
         }
      }

      if (!this.activePotionsMap.isEmpty()) {
         NBTTagList var6 = new NBTTagList();
         Iterator var7 = this.activePotionsMap.values().iterator();

         while(var7.hasNext()) {
            PotionEffect var8 = (PotionEffect)var7.next();
            var6.appendTag(var8.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         par1NBTTagCompound.setTag("ActiveEffects", var6);
      }

      par1NBTTagCompound.setString("contained_items", this.convertContainedItemsToString());
      par1NBTTagCompound.setString("last_harming_entity_unique_id", this.last_harming_entity == null ? "" : this.last_harming_entity.getUniqueID().toString());
      par1NBTTagCompound.setInteger("last_harming_entity_memory_countdown", this.last_harming_entity_memory_countdown);
      par1NBTTagCompound.setBoolean("has_decided_to_flee", this.has_decided_to_flee);
      par1NBTTagCompound.setBoolean("fleeing", this.fleeing);
      par1NBTTagCompound.setFloat("rotationYawHead", this.rotationYawHead);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      this.setAbsorptionAmount(par1NBTTagCompound.getFloat("AbsorptionAmount"));
      if (par1NBTTagCompound.hasKey("Attributes") && this.worldObj != null && !this.worldObj.isRemote) {
         SharedMonsterAttributes.func_111260_a(this.getAttributeMap(), par1NBTTagCompound.getTagList("Attributes"), this.worldObj == null ? null : this.worldObj.getWorldLogAgent());
      }

      if (par1NBTTagCompound.hasKey("ActiveEffects")) {
         NBTTagList var2 = par1NBTTagCompound.getTagList("ActiveEffects");

         for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            PotionEffect var5 = PotionEffect.readCustomPotionEffectFromNBT(var4);
            this.activePotionsMap.put(var5.getPotionID(), var5);
         }
      }

      if (par1NBTTagCompound.hasKey("HealF")) {
         this.setHealth(par1NBTTagCompound.getFloat("HealF"));
      } else {
         NBTBase var6 = par1NBTTagCompound.getTag("Health");
         if (var6 == null) {
            this.setHealth(this.getMaxHealth());
         } else if (var6.getId() == 5) {
            this.setHealth(((NBTTagFloat)var6).data);
         } else if (var6.getId() == 2) {
            this.setHealth((float)((NBTTagShort)var6).data);
         }
      }

      this.hurtTime = par1NBTTagCompound.getShort("HurtTime");
      this.deathTime = par1NBTTagCompound.getShort("DeathTime");
      this.attackTime = par1NBTTagCompound.getShort("AttackTime");
      this.obtainContainedItemsFromString(par1NBTTagCompound.getString("contained_items"));
      if (par1NBTTagCompound.hasKey("last_harming_entity_unique_id")) {
         this.last_harming_entity_unique_id_string = par1NBTTagCompound.getString("last_harming_entity_unique_id");
         if (this.last_harming_entity_unique_id_string.isEmpty()) {
            this.last_harming_entity_unique_id_string = null;
         }
      }

      this.last_harming_entity_memory_countdown = par1NBTTagCompound.getInteger("last_harming_entity_memory_countdown");
      this.has_decided_to_flee = par1NBTTagCompound.getBoolean("has_decided_to_flee");
      this.fleeing = par1NBTTagCompound.getBoolean("fleeing");
      this.rotationYawHead = par1NBTTagCompound.getFloat("rotationYawHead");
   }

   protected void updatePotionEffects() {
      Iterator var1 = this.activePotionsMap.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         PotionEffect var3 = (PotionEffect)this.activePotionsMap.get(var2);
         if (!var3.onUpdate(this)) {
            if (!this.worldObj.isRemote) {
               var1.remove();
               this.onFinishedPotionEffect(var3);
            }
         } else if (var3.getDuration() % 600 == 0) {
            this.onChangedPotionEffect(var3, false);
         }
      }

      int var11;
      if (this.potionsNeedUpdate) {
         if (!this.worldObj.isRemote) {
            if (this.activePotionsMap.isEmpty()) {
               this.dataWatcher.updateObject(8, (byte)0);
               this.dataWatcher.updateObject(7, 0);
               this.setInvisible(false);
            } else {
               var11 = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
               this.dataWatcher.updateObject(8, (byte)(PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0));
               this.dataWatcher.updateObject(7, var11);
               this.setInvisible(this.isPotionActive(Potion.invisibility.id));
            }
         }

         this.potionsNeedUpdate = false;
      }

      var11 = this.dataWatcher.getWatchableObjectInt(7);
      boolean var12 = this.dataWatcher.getWatchableObjectByte(8) > 0;
      if (var11 < 1 && this instanceof EntityMob) {
         EntityMob mob = (EntityMob)this;
         if (mob.isFrenzied()) {
            var11 = 8527390;
            var12 = false;
         }
      }

      if (var11 > 0) {
         boolean var4 = false;
         if (!this.isInvisible()) {
            var4 = this.rand.nextBoolean();
         } else {
            var4 = this.rand.nextInt(15) == 0;
         }

         if (var12) {
            var4 &= this.rand.nextInt(5) == 0;
         }

         if (var4 && var11 > 0) {
            double var5 = (double)(var11 >> 16 & 255) / 255.0;
            double var7 = (double)(var11 >> 8 & 255) / 255.0;
            double var9 = (double)(var11 >> 0 & 255) / 255.0;
            this.worldObj.spawnParticle(var12 ? EnumParticle.mobSpellAmbient : EnumParticle.mobSpell, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - (double)this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, var5, var7, var9);
         }
      }

   }

   public void clearActivePotions() {
      Iterator var1 = this.activePotionsMap.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         PotionEffect var3 = (PotionEffect)this.activePotionsMap.get(var2);
         if (!this.worldObj.isRemote) {
            var1.remove();
            this.onFinishedPotionEffect(var3);
         }
      }

   }

   public Collection getActivePotionEffects() {
      return this.activePotionsMap.values();
   }

   public boolean hasActivePotionEffects() {
      return this.activePotionsMap != null && !this.activePotionsMap.isEmpty();
   }

   public boolean isPotionActive(int par1) {
      return this.activePotionsMap.containsKey(par1);
   }

   public boolean isPotionActive(Potion par1Potion) {
      return this.activePotionsMap.containsKey(par1Potion.id);
   }

   public PotionEffect getActivePotionEffect(Potion par1Potion) {
      return (PotionEffect)this.activePotionsMap.get(par1Potion.id);
   }

   public PotionEffect getActivePotionEffect(int potion_id) {
      return (PotionEffect)this.activePotionsMap.get(potion_id);
   }

   public void addPotionEffect(PotionEffect par1PotionEffect) {
      if (this.isPotionApplicable(par1PotionEffect)) {
         if (this.onServer()) {
            int id = par1PotionEffect.getPotionID();
            Potion potion = Potion.get(id);
            if (potion == Potion.poison) {
               par1PotionEffect = (new PotionEffect(par1PotionEffect)).scaleDuration(1.0F - this.getResistanceToPoison());
            }
         }

         if (this.activePotionsMap.containsKey(par1PotionEffect.getPotionID())) {
            ((PotionEffect)this.activePotionsMap.get(par1PotionEffect.getPotionID())).combine(par1PotionEffect);
            this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(par1PotionEffect.getPotionID()), true);
         } else {
            this.activePotionsMap.put(par1PotionEffect.getPotionID(), par1PotionEffect);
            this.onNewPotionEffect(par1PotionEffect);
         }
      }

   }

   public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
      Potion potion = par1PotionEffect.getPotion();
      return potion != Potion.regeneration && potion != Potion.poison && potion != Potion.wither || this.isEntityBiologicallyAlive();
   }

   public boolean isEntityUndead() {
      return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
   }

   public boolean isEntityBiologicallyAlive() {
      return !this.isEntityUndead();
   }

   public boolean canBePoisoned() {
      return this.isEntityBiologicallyAlive();
   }

   public boolean isArthropod() {
      return this instanceof EntityArachnid || this.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD;
   }

   public void removePotionEffectClient(int par1) {
      this.activePotionsMap.remove(par1);
      if (MITEConstant.sync_client_potion_attribute_modifiers_with_server) {
         this.onFinishedPotionEffect(new PotionEffect(par1, 0, 0));
      }

   }

   public void removePotionEffect(int par1) {
      PotionEffect var2 = (PotionEffect)this.activePotionsMap.remove(par1);
      if (var2 != null) {
         this.onFinishedPotionEffect(var2);
      }

   }

   protected void onNewPotionEffect(PotionEffect par1PotionEffect) {
      this.potionsNeedUpdate = true;
      if (this.onServer() || MITEConstant.sync_client_potion_attribute_modifiers_with_server) {
         Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
      }

   }

   protected void onChangedPotionEffect(PotionEffect par1PotionEffect, boolean par2) {
      this.potionsNeedUpdate = true;
      if (par2 && (this.onServer() || MITEConstant.sync_client_potion_attribute_modifiers_with_server)) {
         Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
         Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
      }

   }

   protected void onFinishedPotionEffect(PotionEffect par1PotionEffect) {
      this.potionsNeedUpdate = true;
      if (this.onServer() || MITEConstant.sync_client_potion_attribute_modifiers_with_server) {
         Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
      }

   }

   public void heal(float par1, EnumEntityFX gain_fx) {
      if (par1 != 0.0F) {
         float var2 = this.getHealth();
         if (var2 > 0.0F) {
            this.setHealth(var2 + par1, true, gain_fx);
         }

      }
   }

   public final void heal(float amount) {
      this.heal(amount, this.getHealFX());
   }

   public final float getHealth() {
      return this.dataWatcher.getWatchableObjectFloat(6);
   }

   public final void setHealth(float par1) {
      this.setHealth(par1, true, this.getHealFX());
   }

   public EnumEntityFX getHealFX() {
      return this.isEntityBiologicallyAlive() ? EnumEntityFX.heal : null;
   }

   public final void setHealth(float par1, boolean check_limit, EnumEntityFX gain_fx) {
      int health_before = (int)this.getHealth();
      if (check_limit) {
         this.dataWatcher.updateObject(6, MathHelper.clamp_float(par1, 0.0F, this.getMaxHealth()));
      } else {
         this.dataWatcher.updateObject(6, Math.max(par1, 0.0F));
      }

      if (this.worldObj != null && (int)this.getHealth() > health_before) {
         this.onHealthRegained(gain_fx);
      }

   }

   public float getHealthFraction() {
      return this.getHealth() / this.getMaxHealth();
   }

   public boolean hasFullHealth() {
      return this.getHealth() >= this.getMaxHealth();
   }

   private EntityDamageResult attackEntityFromHelper(Damage damage, EntityDamageResult result) {
      float amount1 = ForgeHooks.onLivingHurt(this, damage.getSource(), damage.getAmount());
      if (amount1 <= 0) return result;

      Entity responsible_entity = damage.getResponsibleEntity();
      float knockback_amount = damage.isMelee() && responsible_entity instanceof EntityLivingBase && responsible_entity.getAsEntityLivingBase().canOnlyPerformWeakStrike() ? 0.6F : 1.0F;
      if (responsible_entity != null && this.knockBack(responsible_entity, knockback_amount)) {
         result.setEntityWasKnockedBack();
      }

      if (damage.isMelee() && damage.getResponsibleEntity() instanceof EntityLivingBase && damage.getResponsibleEntity().getAsEntityLivingBase().canOnlyPerformWeakStrike()) {
         return result;
      } else if (damage.isEggDamage() && !this.canTakeDamageFromThrownEggs()) {
         return result;
      } else if (damage.isSnowball() && damage.wasCausedByPlayer() && !this.canTakeDamageFromPlayerThrownSnowballs()) {
         return result;
      } else if (damage.isKnockbackOnly()) {
         return result;
      } else {
         float amount;
         if (this.hurtResistantTime * 2 <= this.maxHurtResistantTime) {
            this.lastDamage = damage.getAmount();
            this.prevHealth = this.getHealth();
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
         } else {
            if (damage.getAmount() <= this.lastDamage) {
               return result;
            }

            amount = this.lastDamage;
            this.lastDamage = damage.getAmount();
            damage.setAmount(damage.getAmount() - amount);
         }

         if (damage.isLessThanHalfAHeart()) {
            return result;
         } else {
            if (damage.isArrowFromPlayer()) {
               this.tryAddArrowToContainedItems((EntityArrow)damage.getImmediateEntity());
            }

            damage.setAmount(damage.applyTargetDefenseModifiers(this, result));
            amount = damage.getAmount();
            if (amount > 0.0F) {
               float var3 = amount;
               amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
               this.setAbsorptionAmount(this.getAbsorptionAmount() - (var3 - amount));
               if (amount < 1.0F) {
                  amount = 1.0F;
               }
            } else {
               this.lastDamage = 0.0F;
            }

            result.startTrackingHealth(this.getHealth());
            this.setHealth(this.getHealth() - amount);
            result.finishTrackingHealth(this.getHealth());
            if (result.entityLostHealth()) {
               this.onEntityDamaged(damage.getSource(), result.getAmountOfHealthLost());
            }

            if (this.getHealth() <= 0.0F) {
               result.setEntityWasDestroyed();
            }

            this.func_110142_aN().func_94547_a(damage.getSource(), this.getHealth(), amount);
            if (!this.isEntityPlayer()) {
               this.setAbsorptionAmount(this.getAbsorptionAmount() - amount);
            }

            DebugAttack.setResultingDamage(amount);
            DebugAttack.setHealthAfter(this.getHealth());
            return result;
         }
      }
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (ForgeHooks.onLivingAttack(this, damage.getSource(), damage.getAmount())) return result;

      if (result != null && !result.entityWasDestroyed()) {
         if (this.getHealth() <= 0.0F) {
            return null;
         } else if (damage.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
            return null;
         } else {
            if (damage.isAnvil() || damage.isFallingBlock()) {
               ItemStack head_armor = this.getHelmet();
               if (head_armor != null) {
                  result.applyArmorDamageResult(head_armor.tryDamageItem(damage.getSource(), (int)(damage.getAmount() * 4.0F + this.rand.nextFloat() * damage.getAmount() * 2.0F), this));
                  damage.scaleAmount(0.75F);
               }
            }

            if (damage.isNil()) {
               return result;
            } else {
               if (damage.isLessThanHalfAHeart()) {
                  damage.setAmount(1.0F);
               }

               boolean treat_as_completely_new_attack = this.hurtResistantTime * 2 <= this.maxHurtResistantTime;
               this.attackEntityFromHelper(damage, result);
               boolean make_death_sound = result.entityWasDestroyed();
               boolean make_hurt_sound = !make_death_sound && (result.entityWasKnockedBack() || result.entityLostHealth());
               if (result.entityWasNegativelyAffected()) {
                  if (treat_as_completely_new_attack) {
                     boolean refresh_red_tint = result.entityLostHealth();
                     if (refresh_red_tint && damage.isSunlight() && (this instanceof EntityShadow || this instanceof EntityNightwing)) {
                        refresh_red_tint = false;
                     }

                     this.worldObj.setEntityState(this, refresh_red_tint ? EnumEntityState.hurt_with_red_tint_refreshed : EnumEntityState.hurt_without_red_tint_refreshed);
                     if (make_death_sound) {
                        this.makeSound(this.getDeathSound());
                     } else if (make_hurt_sound) {
                        this.makeSound(this.getHurtSound());
                     }
                  }

                  if (!damage.isDrowning()) {
                     this.setBeenAttacked();
                  }

                  this.limbSwingAmount = 1.5F;
                  this.attackedAtYaw = 0.0F;
                  Entity responsible_entity = damage.getResponsibleEntity();
                  if (responsible_entity == null) {
                     this.attackedAtYaw = (float)((int)(Math.random() * 2.0) * 180);
                  } else {
                     this.refreshDespawnCounter(-1200);
                     responsible_entity.refreshDespawnCounter(-1200);
                     if (responsible_entity instanceof EntityLivingBase) {
                        this.setRevengeTarget((EntityLivingBase)responsible_entity);
                        this.setLastHarmingEntity(responsible_entity);
                        this.considerFleeing();
                     }

                     if (!(responsible_entity instanceof EntityPlayer)) {
                        if (responsible_entity instanceof EntityWolf) {
                           EntityWolf var5 = (EntityWolf)responsible_entity;
                           if (var5.isTamed()) {
                              this.recentlyHit = 100;
                              this.attackingPlayer = null;
                           }
                        }
                     } else {
                        if (!(damage.getImmediateEntity() instanceof EntitySnowball) || this.canTakeDamageFromPlayerThrownSnowballs()) {
                           this.recentlyHit = 100;
                        }

                        this.attackingPlayer = (EntityPlayer)responsible_entity;
                        this.refreshDespawnCounter(-9600);
                     }
                  }

                  if (result.entityWasDestroyed()) {
                     this.onDeath(damage.getSource());
                  }
               }

               return result;
            }
         }
      } else {
         return result;
      }
   }

   protected int getBaseChanceOfRareDrop() {
      return 200;
   }

   public void onDeath(DamageSource par1DamageSource) {
      if (ForgeHooks.onLivingDeath(this, par1DamageSource)) return;
      Entity var2 = par1DamageSource.getResponsibleEntity();
      EntityLivingBase var3 = this.func_94060_bK();
      if (this.scoreValue >= 0 && var3 != null) {
         var3.addToPlayerScore(this, this.scoreValue);
      }

      if (var2 != null) {
         var2.onKillEntity(this);
      }

      if (!this.worldObj.isRemote) {
         int var4 = 0;
         if (var2 instanceof EntityPlayer) {
            var4 = EnchantmentHelper.getLootingModifier((EntityLivingBase)var2);
         }

         captureDrops = true;
         capturedDrops.clear();
         int j = 0;


         if (!this.isChild() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
            if (!this.has_taken_massive_fall_damage || this.rand.nextFloat() < 0.1F) {
               j = this.rand.nextInt(200) - var4;
               this.dropFewItems(this.recentlyHit > 0, par1DamageSource);
            }

            this.dropContainedItems();
            this.dropEquipment(this.recentlyHit > 0, var4);
         }

         captureDrops = false;

         if (!ForgeHooks.onLivingDrops(this, par1DamageSource, capturedDrops, var4, recentlyHit > 0, j))
         {
            for (EntityItem item : capturedDrops)
            {
               worldObj.spawnEntityInWorld(item);
            }
         }
      }

      this.worldObj.setEntityState(this, EnumEntityState.dead);
   }

   protected void dropEquipment(boolean recently_hit_by_player, int par2) {
   }

   public void knockBack(Entity par1Entity, float par2, double par3, double par5) {
      if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
         this.isAirBorne = true;
         float var7 = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
         float var8 = 0.4F;
         this.motionX /= 2.0;
         this.motionY /= 2.0;
         this.motionZ /= 2.0;
         this.motionX -= par3 / (double)var7 * (double)var8;
         this.motionY += (double)var8;
         this.motionZ -= par5 / (double)var7 * (double)var8;
         if (this.motionY > 0.4000000059604645) {
            this.motionY = 0.4000000059604645;
         }

         if (!this.isEntityPlayer()) {
            this.motionX *= 0.800000011920929;
            this.motionY *= 0.800000011920929;
            this.motionZ *= 0.800000011920929;
         }

         if (this instanceof EntityEarthElemental) {
            this.motionX *= 0.4000000059604645;
            this.motionY *= 0.4000000059604645;
            this.motionZ *= 0.4000000059604645;
         }

         this.motionX *= (double)par2;
         this.motionY *= (double)par2;
         this.motionZ *= (double)par2;
         this.knockback_resistant_ticks = 10;
      }

   }

   public boolean knockBack(Entity attacker, float amount) {
      if (attacker != null && !(amount <= 0.0F) && this.canBeKnockedBack() && this.knockback_resistant_ticks <= 0) {
         double var9 = attacker.posX - this.posX;

         double var7;
         for(var7 = attacker.posZ - this.posZ; var9 * var9 + var7 * var7 < 1.0E-4; var7 = (Math.random() - Math.random()) * 0.01) {
            var9 = (Math.random() - Math.random()) * 0.01;
         }

         this.attackedAtYaw = (float)(Math.atan2(var7, var9) * 180.0 / Math.PI) - this.rotationYaw;
         this.knockBack(attacker, amount, var9, var7);
         return true;
      } else {
         return false;
      }
   }

   protected String getHurtSound() {
      return "damage.hit";
   }

   protected String getDeathSound() {
      return "damage.hit";
   }

   protected void dropFewItems(boolean par1, DamageSource damage_source) {
   }

   public boolean isOnLadder() {
      int var1 = MathHelper.floor_double(this.posX);
      int var2 = MathHelper.floor_double(this.boundingBox.minY);
      int var3 = MathHelper.floor_double(this.posZ);
      int var4 = this.worldObj.getBlockId(var1, var2, var3);
      return ForgeHooks.isLivingOnLadder(Block.blocksList[var4], worldObj, var1, var2, var3, this);
   }

   public float getClimbingSpeed() {
      float factor = this.getSpeedBoostOrSlowDownFactor();
      return factor < 1.0F ? 0.2F * (this.getSpeedBoostOrSlowDownFactor() * 0.7F + 0.3F) : 0.2F * this.getSpeedBoostOrSlowDownFactor();
   }

   public boolean isEntityAlive() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   public void calcFallDamage(float fall_distance, float[] damages) {
      damages[0] = 0.0F;
      damages[1] = 0.0F;
      if (!(fall_distance < 0.5F)) {
         PotionEffect var2 = this.getActivePotionEffect(Potion.jump);
         float var3 = var2 != null ? (float)(var2.getAmplifier() + 1) : 0.0F;
         float damage = fall_distance - 2.5F - var3;
         float damage_before_cushioning = damage;
         damage *= 2.0F;
         BlockInfo block_landed_on_info = this.getBlockRestingOn(0.1F);
         if (damage >= 1.0F) {
            if (block_landed_on_info != null) {
               int var5 = block_landed_on_info.block.blockID;
               if (var5 > 0) {
                  Block block = Block.blocksList[var5];
                  float cushioning = block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y, block_landed_on_info.z));
                  if (block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow) {
                     block = this.worldObj.getBlock(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z);
                     if (block == null) {
                        ++cushioning;
                     } else {
                        cushioning += block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z));
                     }
                  }

                  block = Block.blocksList[this.worldObj.getBlockId(block_landed_on_info.x, block_landed_on_info.y + 1, block_landed_on_info.z)];
                  if (block != null) {
                     cushioning += block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y + 1, block_landed_on_info.z));
                  }

                  if (cushioning > 1.0F) {
                     cushioning = 1.0F;
                  }

                  damage -= cushioning * 10.0F;
                  damage *= 1.0F - cushioning;
               }
            }

            if (this instanceof EntityArachnid) {
               damage *= this instanceof EntityWoodSpider ? 0.25F : 0.5F;
            }

            damages[0] = damage_before_cushioning;
            damages[1] = damage;
         }

      }
   }

   protected void fall(float fall_distance) {
      fall_distance = ForgeHooks.onLivingFall(this, fall_distance);
      if (fall_distance <= 0) return;

      super.fall(fall_distance);

      if (!this.worldObj.isRemote) {
         if (!(fall_distance < 0.5F)) {
            PotionEffect var2 = this.getActivePotionEffect(Potion.jump);
            float var3 = var2 != null ? (float)(var2.getAmplifier() + 1) : 0.0F;
            float damage = fall_distance - 2.5F - var3;
            float damage_before_cushioning = damage;
            damage *= 2.0F;
            BlockInfo block_landed_on_info = this.getBlockRestingOn(0.1F);
            if (damage >= 1.0F) {
               if (block_landed_on_info != null) {
                  int var5 = block_landed_on_info.block.blockID;
                  if (var5 > 0) {
                     Block block = Block.blocksList[var5];
                     float cushioning = block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y, block_landed_on_info.z));
                     if (block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow) {
                        block = this.worldObj.getBlock(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z);
                        if (block == null) {
                           ++cushioning;
                        } else {
                           cushioning += block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z));
                        }
                     }

                     block = Block.blocksList[this.worldObj.getBlockId(block_landed_on_info.x, block_landed_on_info.y + 1, block_landed_on_info.z)];
                     if (block != null) {
                        cushioning += block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y + 1, block_landed_on_info.z));
                     }

                     if (cushioning > 1.0F) {
                        cushioning = 1.0F;
                     }

                     damage -= cushioning * 10.0F;
                     damage *= 1.0F - cushioning;
                  }
               }

               if (this instanceof EntityArachnid) {
                  damage *= this instanceof EntityWoodSpider ? 0.25F : 0.5F;
               }

               if (damage_before_cushioning > 4.0F && !this.worldObj.isRemote && block_landed_on_info != null && (block_landed_on_info.block == Block.glass || block_landed_on_info.block == Block.blockSnow && this.worldObj.isAirOrPassableBlock(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z, true))) {
                  this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, block_landed_on_info.x, block_landed_on_info.y, block_landed_on_info.z)).setCollidedWith(this), true);
                  if (damage > 5.0F) {
                     damage = 5.0F;
                  }
               }

               if (damage >= 1.0F) {
                  if (damage >= this.getMaxHealth() * 0.5F) {
                     this.has_taken_massive_fall_damage = true;
                  }

                  this.makeSound(damage > 4.0F ? "damage.fallbig" : "damage.fallsmall");
                  this.attackEntityFrom(new Damage(DamageSource.fall, damage));
               }
            }

            if (block_landed_on_info != null && block_landed_on_info.block.stepSound != null) {
               if (this instanceof EntityPlayer) {
                  this.makeSound("step." + block_landed_on_info.block.stepSound.stepSoundName, Math.min(fall_distance * 0.1F, 1.0F), 1.0F);
               } else {
                  this.makeSound("step." + block_landed_on_info.block.stepSound.stepSoundName, Math.min(fall_distance * 0.2F, 2.0F), 1.0F);
               }
            }

         }
      }
   }

   public void performHurtAnimation() {
      this.hurtTime = this.maxHurtTime = 10;
      this.attackedAtYaw = 0.0F;
   }

   public float getNaturalDefense(DamageSource damage_source) {
      return 0.0F;
   }

   public abstract ItemStack[] getWornItems();

   public abstract boolean isWearing(ItemStack var1);

   public boolean isWearingArmor() {
      ItemStack[] worn_items = this.getWornItems();

      for(int i = 0; i < worn_items.length; ++i) {
         ItemStack item_stack = worn_items[i];
         if (item_stack != null && item_stack.getItem() instanceof ItemArmor) {
            return true;
         }
      }

      return false;
   }

   public float getProtectionFromArmor(DamageSource damage_source, boolean include_enchantments) {
      return ItemArmor.getTotalArmorProtection(this.getWornItems(), damage_source, include_enchantments, this);
   }

   public float getProtection(DamageSource damage_source, boolean include_natural_protection, boolean include_armor, boolean include_armor_enchantments, boolean include_potion_effects) {
      float total_protection = 0.0F;
      if (include_natural_protection) {
         total_protection += this.getNaturalDefense(damage_source);
      }

      if (include_armor) {
         total_protection += this.getProtectionFromArmor(damage_source, include_armor_enchantments);
      }

      if (include_potion_effects) {
         total_protection += this.getTotalProtectionOfPotionEffects(damage_source);
      }

      return total_protection;
   }

   public float getTotalProtection(DamageSource damage_source) {
      return this.getProtection(damage_source, true, true, true, true);
   }

   public float getTotalProtectionOfPotionEffects(DamageSource damage_source) {
      float total_protection = 0.0F;
      if (damage_source != null && damage_source.isAbsolute()) {
         return total_protection;
      } else {
         if (this.isPotionActive(Potion.resistance)) {
            total_protection += (float)(this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5.0F;
         }

         if (damage_source != null && this.isPotionActive(Potion.fireResistance) && damage_source.isFireDamage()) {
            total_protection += (float)(this.getActivePotionEffect(Potion.fireResistance).getAmplifier() + 1) * 5.0F;
         }

         return total_protection;
      }
   }

   public float getSilverArmorCoverage() {
      float coverage = 0.0F;
      ItemStack[] worn_items = this.getWornItems();

      for(int i = 0; i < worn_items.length; ++i) {
         ItemStack item_stack = worn_items[i];
         if (item_stack != null) {
            if (item_stack.isArmor()) {
               ItemArmor item_armor = item_stack.getItem().getAsArmor();
               if (item_armor.getArmorMaterial() == Material.silver) {
                  coverage += item_armor.getCoverage() * item_armor.getDamageFactor(item_stack, this);
               }
            } else if (item_stack.getItem() instanceof ItemHorseArmor) {
               ItemHorseArmor barding = (ItemHorseArmor)item_stack.getItem();
               if (barding.getArmorMaterial() == Material.silver) {
                  coverage += barding.getCoverage();
               }
            }
         }
      }

      return coverage;
   }

   public float getResistanceToPoison() {
      return this.getSilverArmorCoverage() * 0.5F;
   }

   public float getResistanceToParalysis() {
      return (float)EnchantmentHelper.getFreeActionModifier(this) * (0.8F / (float)Enchantment.free_action.getNumLevels());
   }

   public float getResistanceToDrain() {
      return this.getSilverArmorCoverage() * 0.5F;
   }

   public float getResistanceToShadow() {
      return this.getSilverArmorCoverage() * 0.5F;
   }

   public int getDrainAfterResistance(int drain) {
      return Math.round((float)drain * (1.0F - this.getResistanceToDrain()));
   }

   public float getAmountAfterResistance(float amount, int resistance_type) {
      float resistance = 0.0F;
      if (resistance_type == 1) {
         resistance = this.getResistanceToPoison();
      } else if (resistance_type == 2) {
         resistance = this.getResistanceToParalysis();
      } else if (resistance_type == 3) {
         Minecraft.setErrorMessage("getAmountAfterResistance: use getDrainAfterResistance() instead because it returns an int");
      } else if (resistance_type == 4) {
         resistance = this.getResistanceToShadow();
      } else {
         Minecraft.setErrorMessage("getAmountAfterResistance: unhandled resistance type " + resistance_type);
      }

      if (resistance < 0.0F) {
         Minecraft.setErrorMessage("getAmountAfterResistance: resistance was less than 0.0F");
      } else if (resistance > 1.0F) {
         Minecraft.setErrorMessage("getAmountAfterResistance: resistance was more than 1.0F");
      }

      return amount * (1.0F - resistance);
   }

   public boolean isWearingItems(boolean include_non_armor) {
      ItemStack[] worn_items = this.getWornItems();

      for(int i = 0; i < worn_items.length; ++i) {
         ItemStack item_stack = worn_items[i];
         if (item_stack != null && (include_non_armor || item_stack.getItem() instanceof ItemArmor)) {
            return true;
         }
      }

      return false;
   }

   public boolean isWearingDamageableItems(boolean include_non_armor) {
      ItemStack[] worn_items = this.getWornItems();

      for(int i = 0; i < worn_items.length; ++i) {
         ItemStack item_stack = worn_items[i];
         if (item_stack != null && (include_non_armor || item_stack.getItem() instanceof ItemArmor) && item_stack.isItemStackDamageable()) {
            return true;
         }
      }

      return false;
   }

   public final ItemStack getBoots() {
      return this.getWornItems()[0];
   }

   public final ItemStack getLeggings() {
      return this.getWornItems()[1];
   }

   public final ItemStack getCuirass() {
      return this.getWornItems()[2];
   }

   public final ItemStack getHelmet() {
      return this.getWornItems()[3];
   }

   public abstract boolean setWornItem(int var1, ItemStack var2);

   public final boolean setBoots(ItemStack item_stack) {
      return this.setWornItem(0, item_stack);
   }

   public final boolean setLeggings(ItemStack item_stack) {
      return this.setWornItem(1, item_stack);
   }

   public final boolean setCuirass(ItemStack item_stack) {
      return this.setWornItem(2, item_stack);
   }

   public final boolean setHelmet(ItemStack item_stack) {
      return this.setWornItem(3, item_stack);
   }

   public boolean isWearingHelmet(boolean include_non_armor) {
      ItemStack helmet = this.getHelmet();
      return helmet != null && (include_non_armor || helmet.getItem() instanceof ItemArmor);
   }

   public boolean isWearingPumpkinHelmet() {
      ItemStack helmet = this.getHelmet();
      return helmet != null && helmet.getItem() == Item.getItem(Block.pumpkin.blockID);
   }

   public boolean isWearingCuirass(boolean include_non_armor) {
      ItemStack cuirass = this.getCuirass();
      return cuirass != null && (include_non_armor || cuirass.getItem() instanceof ItemArmor);
   }

   public void tryDamageArmor(DamageSource damage_source, float amount, EntityDamageResult result) {
   }

   public void tryAddArrowToContainedItems(EntityArrow entity_arrow) {
      if (entity_arrow.canBePickedUp == 1) {
         ItemArrow item_arrow = entity_arrow.item_arrow;
         EntityPlayer player = entity_arrow.shootingEntity.getAsPlayer();
         if (this.rand.nextInt(5) < EnchantmentHelper.getEnchantmentLevel(Enchantment.arrow_recovery.effectId, player.getHeldItemStack())) {
            this.addContainedItem(item_arrow);
         } else {
            item_arrow.addToEntityContainedItemsWithChance(this.rand, this);
         }
      }

   }

   public CombatTracker func_110142_aN() {
      return this._combatTracker;
   }

   public EntityLivingBase func_94060_bK() {
      return (EntityLivingBase)(this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
   }

   public final float getMaxHealth() {
      return this instanceof EntityPlayer ? ((EntityPlayer)this).getHealthLimit() : (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
   }

   public final int getArrowCountInEntity() {
      return this.dataWatcher.getWatchableObjectByte(9);
   }

   public final void setArrowCountInEntity(int par1) {
      this.dataWatcher.updateObject(9, (byte)par1);
   }

   private int getArmSwingAnimationEnd() {
      return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
   }

   public void swingArm() {
      ItemStack stack = this.getHeldItemStack();

      if (stack != null && stack.getItem() != null)
      {
         if (stack.onEntitySwing(this, stack))
         {
            return;
         }
      }

      if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
         this.swingProgressInt = -1;
         this.isSwingInProgress = true;
         if (this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet18Animation(this, 1));
         }
      }

   }

   public final void onHealthRegained(EnumEntityFX gain_fx) {
      if (!this.worldObj.isRemote && gain_fx != null) {
         this.entityFX(gain_fx);
      }

   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 != EnumEntityState.hurt_with_red_tint_refreshed && par1 != EnumEntityState.hurt_without_red_tint_refreshed) {
         if (par1 == EnumEntityState.dead) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(this.getDeathSound()), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.setHealth(0.0F);
            this.onDeath(DamageSource.generic);
         } else {
            super.handleHealthUpdate(par1);
         }
      } else {
         if (par1 == EnumEntityState.hurt_with_red_tint_refreshed) {
            this.hurtTime = this.maxHurtTime = 10;
         }

         this.limbSwingAmount = 1.5F;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.attackedAtYaw = 0.0F;
      }

   }

   protected void kill() {
      if (this.onServer()) {
         this.attackEntityFrom(new Damage(DamageSource.outOfWorld, 4.0F));
      }

   }

   protected void updateArmSwingProgress() {
      int var1 = this.getArmSwingAnimationEnd();
      if (this.isSwingInProgress) {
         ++this.swingProgressInt;
         if (this.swingProgressInt >= var1) {
            this.swingProgressInt = 0;
            this.isSwingInProgress = false;
         }
      } else {
         this.swingProgressInt = 0;
      }

      this.swingProgress = (float)this.swingProgressInt / (float)var1;
   }

   public boolean hasEntityAttribute(Attribute attribute) {
      return this.getEntityAttribute(attribute) != null;
   }

   public AttributeInstance setEntityAttribute(Attribute par1Attribute) {
      AttributeInstance instance = this.getAttributeMap().getAttributeInstance(par1Attribute);
      if (instance == null) {
         instance = this.getAttributeMap().register(par1Attribute);
      }

      return instance;
   }

   public AttributeInstance setEntityAttribute(Attribute par1Attribute, double value) {
      AttributeInstance instance = this.getAttributeMap().getAttributeInstance(par1Attribute);
      if (instance == null) {
         instance = this.getAttributeMap().register(par1Attribute);
      }

      instance.setAttribute(value);
      return instance;
   }

   public AttributeInstance getEntityAttribute(Attribute par1Attribute) {
      return this.getAttributeMap().getAttributeInstance(par1Attribute);
   }

   public double getEntityAttributeValue(Attribute attribute) {
      return this.getAttributeMap().getAttributeInstance(attribute).getAttributeValue();
   }

   public double getEntityAttributeBaseValue(Attribute attribute) {
      return this.getAttributeMap().getAttributeInstance(attribute).getBaseValue();
   }

   public BaseAttributeMap getAttributeMap() {
      if (this.attributeMap == null) {
         this.attributeMap = new ServersideAttributeMap();
      }

      return this.attributeMap;
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEFINED;
   }

   public abstract void setHeldItemStack(ItemStack var1);

   public abstract ItemStack getHeldItemStack();

   public Item getHeldItem() {
      ItemStack item_stack = this.getHeldItemStack();
      return item_stack == null ? null : item_stack.getItem();
   }

   public int getHeldItemID() {
      Item item = this.getHeldItem();
      return item == null ? 0 : item.itemID;
   }

   public boolean hasHeldItem() {
      ItemStack item_stack = this.getHeldItemStack();
      if (item_stack == null) {
         return false;
      } else if (item_stack.stackSize < 1) {
         Minecraft.setErrorMessage("hasHeldItem: stack size is " + item_stack.stackSize);
         return false;
      } else {
         return true;
      }
   }

   public abstract ItemStack getCurrentItemOrArmor(int var1);

   public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);

   public void setSprinting(boolean par1) {
      super.setSprinting(par1);
      AttributeInstance var2 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (var2.getModifier(sprintingSpeedBoostModifierUUID) != null) {
         var2.removeModifier(sprintingSpeedBoostModifier);
      }

      if (par1) {
         var2.applyModifier(sprintingSpeedBoostModifier);
      }

   }

   public abstract ItemStack[] getLastActiveItems();

   protected float getSoundVolume(String sound) {
      return 1.0F;
   }

   protected float getSoundPitch(String sound) {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F;
   }

   public void setPositionAndUpdate(double par1, double par3, double par5) {
      this.setLocationAndAngles(par1, par3, par5, this.rotationYaw, this.rotationPitch);
   }

   public void dismountEntity(Entity par1Entity) {
      double var3 = par1Entity.posX;
      double var5 = par1Entity.boundingBox.minY + (double)par1Entity.height;
      double var7 = par1Entity.posZ;

      for(double var9 = -1.5; var9 < 2.0; ++var9) {
         for(double var11 = -1.5; var11 < 2.0; ++var11) {
            if (var9 != 0.0 || var11 != 0.0) {
               int var13 = (int)(this.posX + var9);
               int var14 = (int)(this.posZ + var11);
               AxisAlignedBB var2 = this.boundingBox.getOffsetBoundingBox(var9, 1.0, var11);
               if (this.worldObj.getCollidingBlockBounds(var2, this).isEmpty()) {
                  if (this.worldObj.doesBlockHaveSolidTopSurface(var13, (int)this.posY, var14)) {
                     this.setPositionAndUpdate(this.posX + var9, this.posY + 1.0, this.posZ + var11);
                     return;
                  }

                  if (this.worldObj.doesBlockHaveSolidTopSurface(var13, (int)this.posY - 1, var14) || this.worldObj.getBlockMaterial(var13, (int)this.posY - 1, var14) == Material.water) {
                     var3 = this.posX + var9;
                     var5 = this.posY + 1.0;
                     var7 = this.posZ + var11;
                  }
               }
            }
         }
      }

      this.setPositionAndUpdate(var3, var5, var7);
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return false;
   }

   public Icon getItemIcon(ItemStack par1ItemStack, int par2) {
      return par1ItemStack.getIconIndex();
   }

   protected void jump() {
      this.motionY = 0.42100000381469727;
      if (this.isPotionActive(Potion.jump)) {
         this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
      }

      if (this.isSprinting()) {
         float var1 = this.rotationYaw * 0.017453292F;
         this.motionX -= (double)(MathHelper.sin(var1) * 0.2F);
         this.motionZ += (double)(MathHelper.cos(var1) * 0.2F);
      }

      this.isAirBorne = true;
      ForgeHooks.onLivingJump(this);
   }

   private float handleSpecialArachnidMovement(float forward_movement) {
      if (forward_movement >= 0.5F) {
         return forward_movement;
      } else {
         EntityArachnid arachnid = (EntityArachnid)this;
         Entity target = arachnid.getEntityToAttack();
         if (target instanceof EntityPlayer) {
            boolean can_pass_through_leaves = this instanceof EntityWoodSpider;
            double dx = target.posX - this.posX;
            double dy = target.posY - this.posY;
            double dz = target.posZ - this.posZ;
            double dx_sq = dx * dx;
            double dz_sq = dz * dz;
            if (dy > 0.0 && dx_sq < 8.0 && dz_sq < 8.0) {
               if (!(dy > 1.0) && !(dx_sq > 0.10000000149011612) && !(dz_sq > 0.10000000149011612)) {
                  if (dy <= 1.0 && this.ticksExisted % 40 == 0) {
                     this.motionX = this.motionY = this.motionZ = 0.0;
                     this.posX = (double)((float)target.getBlockPosX() + 0.5F);
                     this.posY = target.posY;
                     this.posZ = (double)((float)target.getBlockPosZ() + 0.5F);
                     forward_movement = 0.0F;
                     this.attackEntityAsMob(target);
                  }
               } else {
                  int x = this.getBlockPosX();
                  int y = this.getBlockPosY();
                  int z = this.getBlockPosZ();
                  boolean is_in_leaves = this.isInsideOfMaterial(Material.tree_leaves);
                  int trial_x;
                  if (is_in_leaves) {
                     if (this.worldObj.getBlock(x, y + 1, z) == Block.snow) {
                        this.worldObj.destroyBlock(new BlockBreakInfo(this.worldObj, x, y + 1, z), false);
                     }

                     trial_x = MathHelper.floor_double(this.boundingBox.minX);
                     int trial_z = MathHelper.floor_double(this.boundingBox.minZ);
                     if ((trial_x != x || trial_z != z) && this.worldObj.getBlock(trial_x, y + 1, trial_z) == Block.snow && this.worldObj.getBlock(trial_x, y, trial_z) instanceof BlockLeaves) {
                        this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, trial_x, y + 1, trial_z)).setDestroyedBy(this), false);
                     }

                     trial_x = MathHelper.floor_double(this.boundingBox.minX);
                     trial_z = MathHelper.floor_double(this.boundingBox.maxZ);
                     if ((trial_x != x || trial_z != z) && this.worldObj.getBlock(trial_x, y + 1, trial_z) == Block.snow && this.worldObj.getBlock(trial_x, y, trial_z) instanceof BlockLeaves) {
                        this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, trial_x, y + 1, trial_z)).setDestroyedBy(this), false);
                     }

                     trial_x = MathHelper.floor_double(this.boundingBox.maxX);
                     trial_z = MathHelper.floor_double(this.boundingBox.minZ);
                     if ((trial_x != x || trial_z != z) && this.worldObj.getBlock(trial_x, y + 1, trial_z) == Block.snow && this.worldObj.getBlock(trial_x, y, trial_z) instanceof BlockLeaves) {
                        this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, trial_x, y + 1, trial_z)).setDestroyedBy(this), false);
                     }

                     trial_x = MathHelper.floor_double(this.boundingBox.maxX);
                     trial_z = MathHelper.floor_double(this.boundingBox.maxZ);
                     if ((trial_x != x || trial_z != z) && this.worldObj.getBlock(trial_x, y + 1, trial_z) == Block.snow && this.worldObj.getBlock(trial_x, y, trial_z) instanceof BlockLeaves) {
                        this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, trial_x, y + 1, trial_z)).setDestroyedBy(this), false);
                     }
                  }

                  trial_x = target.getBlockPosY();
                  boolean can_climb_up_to_player = true;

                  label84: {
                     do {
                        do {
                           ++y;
                           if (y > trial_x) {
                              break label84;
                           }
                        } while(this.worldObj.isAirOrPassableBlock(x, y, z, false));
                     } while(can_pass_through_leaves && this.worldObj.getBlock(x, y, z) instanceof BlockLeaves);

                     can_climb_up_to_player = false;
                  }

                  if (can_climb_up_to_player) {
                     forward_movement = 0.5F;
                  } else {
                     forward_movement = 0.0F;
                  }

                  if (this.isInsideOfMaterial(Material.tree_leaves)) {
                     if (can_climb_up_to_player) {
                        forward_movement *= 0.2F;
                     }

                     this.setJumping(true);
                  }
               }
            } else {
               forward_movement = 0.5F;
            }
         }

         return forward_movement;
      }
   }

   public void moveEntityWithHeading(float par1, float par2) {
      if (!this.worldObj.isRemote && this instanceof EntityArachnid) {
         par2 = this.handleSpecialArachnidMovement(par2);
      }

      Block block_at_feet = this.worldObj.getBlock(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ());
      if (block_at_feet != null && (block_at_feet.blockMaterial == Material.vine || block_at_feet.blockMaterial == Material.plants) && this.hasCurse(Curse.entanglement, true)) {
         float slow_down = block_at_feet == Block.vine ? 0.2F : 0.4F;
         par1 *= slow_down;
         par2 *= slow_down;
      }

      double var10;
      float var5;
      if (this.isInWater() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying) && !(this instanceof EntityOoze)) {
         var10 = this.posY;
         this.moveFlying(par1, par2, this instanceof EntityEarthElemental ? 0.055F : (this.isAIEnabled() ? 0.04F : 0.02F));
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.800000011920929;
         this.motionY *= 0.800000011920929;
         this.motionZ *= 0.800000011920929;
         this.motionY -= 0.02;
         if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579 - this.posY + var10, this.motionZ)) {
            this.motionY = 0.30000001192092896;
         }
      } else if (this.handleLavaMovement() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying)) {
         var10 = this.posY;
         this.moveFlying(par1, par2, !(this instanceof EntityFireElemental) && !(this instanceof EntityEarthElemental) ? (!this.isHarmedByLava() ? 0.05F : 0.02F) : 0.1F);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.5;
         this.motionY *= 0.5;
         this.motionZ *= 0.5;
         this.motionY -= 0.02;
         if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579 - this.posY + var10, this.motionZ)) {
            this.motionY = 0.30000001192092896;
         }
      } else {
         float var3 = 0.91F;
         if (this.onGround) {
            var3 = 0.54600006F;
            BlockInfo block_info = this.getBlockRestingOn(0.1F);
            int var4 = block_info == null ? 0 : block_info.block.blockID;
            if (block_info != null && this.worldObj.getBlock(block_info.x, block_info.y + 1, block_info.z) == Block.snow && BlockSnow.getDepth(this.worldObj.getBlockMetadata(block_info.x, block_info.y + 1, block_info.z)) == 1) {
               var4 = Block.snow.blockID;
            }

            if (var4 > 0) {
               var3 = Block.blocksList[var4].slipperiness * 0.91F;
            }
         }

         float var8 = 0.16277136F / (var3 * var3 * var3);
         if (this.onGround) {
            var5 = this.getAIMoveSpeed() * var8;
         } else {
            var5 = this.jumpMovementFactor;
         }

         this.moveFlying(par1, par2, var5);
         var3 = 0.91F;
         int z;
         if (this.onGround) {
            var3 = 0.54600006F;
            BlockInfo block_info = this.getBlockRestingOn(0.1F);
            z = block_info == null ? 0 : block_info.block.blockID;
            if (block_info != null && this.worldObj.getBlock(block_info.x, block_info.y + 1, block_info.z) == Block.snow && BlockSnow.getDepth(this.worldObj.getBlockMetadata(block_info.x, block_info.y + 1, block_info.z)) == 1) {
               z = Block.snow.blockID;
            }

            if (z > 0) {
               var3 = Block.blocksList[z].slipperiness * 0.91F;
            }
         }

         if (this.isOnLadder()) {
            float var11 = 0.15F;
            if (this.motionX < (double)(-var11)) {
               this.motionX = (double)(-var11);
            }

            if (this.motionX > (double)var11) {
               this.motionX = (double)var11;
            }

            if (this.motionZ < (double)(-var11)) {
               this.motionZ = (double)(-var11);
            }

            if (this.motionZ > (double)var11) {
               this.motionZ = (double)var11;
            }

            this.fallDistance = 0.0F;
            if (this.motionY < -0.15) {
               this.motionY = -0.15;
            }

            this.motionX *= 0.800000011920929;
            this.motionZ *= 0.800000011920929;
            boolean var7 = this.isSneaking() && this instanceof EntityPlayer;
            if (var7 && this.motionY < 0.0) {
               this.motionY = 0.0;
            }
         }

         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         if (this.isCollidedHorizontally && this.isOnLadder()) {
            this.motionY = (double)this.getClimbingSpeed();
         }

         int x = this.getBlockPosX();
         z = this.getBlockPosZ();
         if (!this.worldObj.isRemote || this.worldObj.blockExists(x, 0, z) && this.worldObj.getChunkFromBlockCoords(x, z).isChunkLoaded) {
            this.motionY -= 0.08;
         } else if (this.posY > 0.0) {
            this.motionY = -0.1;
         } else {
            this.motionY = 0.0;
         }

         this.motionY *= 0.9800000190734863;
         this.motionX *= (double)var3;
         this.motionZ *= (double)var3;
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      var10 = this.posX - this.prevPosX;
      double var9 = this.posZ - this.prevPosZ;
      var5 = MathHelper.sqrt_double(var10 * var10 + var9 * var9) * 4.0F;
      if (var5 > 1.0F) {
         var5 = 1.0F;
      }

      this.limbSwingAmount += (var5 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   protected boolean isAIEnabled() {
      return false;
   }

   public float getAIMoveSpeed() {
      return this.isAIEnabled() ? this.landMovementFactor : 0.1F;
   }

   public void setAIMoveSpeed(float par1) {
      this.landMovementFactor = par1;
   }

   public EntityDamageResult attackEntityAsMob(Entity par1Entity) {
      EntityDamageResult result = new EntityDamageResult(par1Entity);
      this.setLastAttackTarget(par1Entity);
      return result;
   }

   public boolean inBed() {
      return false;
   }

   private void tryReinstateLastHarmingEntityFromDisk() {
      if (!this.worldObj.isRemote && this.last_harming_entity_unique_id_string != null && this.last_harming_entity == null && this.ticksExisted % 10 == 0) {
         this.last_harming_entity = this.getNearbyEntityByUniqueID(this.last_harming_entity_unique_id_string);
         if (this.last_harming_entity != null) {
            this.last_harming_entity_unique_id_string = null;
         }
      }

   }

   public void onUpdate() {
      if (ForgeHooks.onLivingUpdate(this))
      {
         return;
      }

      if (this.last_harming_entity_memory_countdown > 0) {
         --this.last_harming_entity_memory_countdown;
      }

      if (this.last_harming_entity != null && (this.last_harming_entity.isDead || this.last_harming_entity_memory_countdown == 0)) {
         this.setLastHarmingEntity((Entity)null);
      }

      if (this.last_harming_entity_memory_countdown == 0) {
         this.last_harming_entity_unique_id_string = null;
      } else {
         this.tryReinstateLastHarmingEntityFromDisk();
      }

      if (this.fleeing && this.ticksExisted % 100 == 0) {
         this.considerStopFleeing();
      }

      super.onUpdate();
      int var2;
      if (!this.worldObj.isRemote) {
         var2 = this.getArrowCountInEntity();
         if (var2 > 0) {
            if (this.arrowHitTimer <= 0) {
               this.arrowHitTimer = 20 * (30 - var2);
            }

            --this.arrowHitTimer;
            if (this.arrowHitTimer <= 0) {
               this.setArrowCountInEntity(var2 - 1);
            }
         }
      }

      for(var2 = 0; var2 < 5; ++var2) {
         ItemStack var3 = this.previousEquipment[var2];
         ItemStack var4 = this.getCurrentItemOrArmor(var2);
         if (!ItemStack.areItemStacksEqual(var4, var3)) {
            if (this.worldObj instanceof WorldServer) {
               ((WorldServer)this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet5PlayerInventory(this.entityId, var2, var4));
            }

            if (var3 != null) {
               this.attributeMap.removeAttributeModifiers(var3.getAttributeModifiers());
            }

            if (var4 != null) {
               this.attributeMap.applyAttributeModifiers(var4.getAttributeModifiers());
            }

            this.previousEquipment[var2] = var4 == null ? null : var4.copy();
         }
      }

      this.onLivingUpdate();
      double var9 = this.posX - this.prevPosX;
      double var10 = this.posZ - this.prevPosZ;
      float var5 = (float)(var9 * var9 + var10 * var10);
      float var6 = this.renderYawOffset;
      float var7 = 0.0F;
      float var8 = 0.0F;
      if (var5 > 0.0025000002F) {
         var8 = 1.0F;
         var7 = (float)Math.sqrt((double)var5) * 3.0F;
         var6 = (float)Math.atan2(var10, var9) * 180.0F / 3.1415927F - 90.0F;
      }

      if (this instanceof EntityLivestock) {
         this.pushOutOfBlocks();
      }

      if (this.swingProgress > 0.0F) {
         var6 = this.rotationYaw;
      }

      if (!this.onGround) {
         var8 = 0.0F;
      }

      this.worldObj.theProfiler.startSection("headTurn");
      this.func_110146_f(var6, var7);
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("rangeChecks");

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      while(this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
         this.prevRenderYawOffset -= 360.0F;
      }

      while(this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
         this.prevRenderYawOffset += 360.0F;
      }

      while(this.rotationPitch - this.prevRotationPitch < -180.0F) {
         this.prevRotationPitch -= 360.0F;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
         this.prevRotationYawHead -= 360.0F;
      }

      while(this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
         this.prevRotationYawHead += 360.0F;
      }

      this.worldObj.theProfiler.endSection();
   }

   protected float func_110146_f(float par1, float par2) {
      float var3 = MathHelper.wrapAngleTo180_float(par1 - this.renderYawOffset);
      this.renderYawOffset += var3 * 0.3F;
      float var4 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
      boolean var5 = var4 < -90.0F || var4 >= 90.0F;
      if (var4 < -75.0F) {
         var4 = -75.0F;
      }

      if (var4 >= 75.0F) {
         var4 = 75.0F;
      }

      this.renderYawOffset = this.rotationYaw - var4;
      if (var4 * var4 > 2500.0F) {
         this.renderYawOffset += var4 * 0.2F;
      }

      if (var5) {
         par2 *= -1.0F;
      }

      return par2;
   }

   public void spawnInLoveHeartParticle() {
      double var2 = this.rand.nextGaussian() * 0.02;
      double var4 = this.rand.nextGaussian() * 0.02;
      double var6 = this.rand.nextGaussian() * 0.02;
      this.worldObj.spawnParticle(EnumParticle.heart, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.getFootPosY() + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var2, var4, var6);
   }

   public boolean healsWithTime() {
      return !this.isEntityUndead();
   }

   public final float getSpeedBoostVsSlowDown() {
      PotionEffect slowdown_effect = this.getActivePotionEffect(Potion.moveSlowdown);
      PotionEffect haste_effect = this.getActivePotionEffect(Potion.moveSpeed);
      float slow_amount = slowdown_effect == null ? 0.0F : (float)(slowdown_effect.getAmplifier() + 1) * -0.2F;
      float haste_amount = haste_effect == null ? 0.0F : (float)(haste_effect.getAmplifier() + 1) * 0.2F;
      if (this.isInWeb) {
         slow_amount -= 0.75F;
      }

      double overall_speed_modifier = (double)(slow_amount + haste_amount);
      if (overall_speed_modifier < 0.0) {
         overall_speed_modifier *= (double)(1.0F - this.getResistanceToParalysis());
      }

      return (float)overall_speed_modifier;
   }

   public final float getSpeedBoostOrSlowDownFactor() {
      return MathHelper.clamp_float(1.0F + this.getSpeedBoostVsSlowDown(), 0.0F, 4.0F);
   }

   public void onLivingUpdate() {
      if (!this.worldObj.isRemote && this.getTicksExistedWithOffset() % 1000 == 0 && !(this instanceof EntityPlayer) && this.healsWithTime()) {
         this.healByPercentage(0.1F);
      }

      if (this.jumpTicks > 0) {
         --this.jumpTicks;
      }

      if (this.newPosRotationIncrements > 0) {
         double var1 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
         double var3 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
         double var5 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
         double var7 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
         this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.newPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
         --this.newPosRotationIncrements;
         this.setPosition(var1, var3, var5);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else if (this.worldObj.isRemote && !(this instanceof EntityPlayerSP)) {
         this.motionX *= 0.98;
         this.motionY *= 0.98;
         this.motionZ *= 0.98;
      }

      if (Math.abs(this.motionX) < 0.005) {
         this.motionX = 0.0;
      }

      if (Math.abs(this.motionY) < 0.005) {
         this.motionY = 0.0;
      }

      if (Math.abs(this.motionZ) < 0.005) {
         this.motionZ = 0.0;
      }

      this.worldObj.theProfiler.startSection("ai");
      if (this.isMovementBlocked()) {
         this.isJumping = false;
         this.moveStrafing = 0.0F;
         this.moveForward = 0.0F;
         this.randomYawVelocity = 0.0F;
      } else if (!this.worldObj.isRemote || this instanceof EntityPlayerSP) {
         if (this.isAIEnabled()) {
            this.worldObj.theProfiler.startSection("newAi");
            this.updateAITasks();
            this.worldObj.theProfiler.endSection();
         } else {
            this.worldObj.theProfiler.startSection("oldAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
            this.rotationYawHead = this.rotationYaw;
         }
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("jump");
      if (this.isJumping) {
         if (!(this instanceof EntityCubic) && (this.isInWater() || this.handleLavaMovement())) {
            if (this.ridingEntity == null) {
               Material material_at_feet = this.worldObj.getBlockMaterial(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ());
               boolean outside_of_liquid = material_at_feet != Material.water && material_at_feet != Material.lava;
               boolean in_waterfall = this.worldObj.getBlockMetadata(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ()) == 9 && this.worldObj.getBlockId(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ()) == 9 && this.worldObj.getBlockMetadata(this.getBlockPosX(), this.getHeadBlockPosY(), this.getBlockPosZ()) == 9 && this.worldObj.getBlockId(this.getBlockPosX(), this.getHeadBlockPosY(), this.getBlockPosZ()) == 9;
               float swim_modifier = !outside_of_liquid && !in_waterfall ? 1.0F : 0.4375F;
               if (this instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)this;
                  if (this.worldObj.isRemote) {
                     player.addHungerClientSide(0.01F);
                  }

                  boolean has_both_hands_occupied = player.itemInUse != null && player.itemInUse.getItem() instanceof ItemBow;
                  if (has_both_hands_occupied && this.isSuspendedInLiquid()) {
                     swim_modifier *= 0.4375F;
                  }

                  float overall_speed_modifier = this.getSpeedBoostVsSlowDown();
                  if (overall_speed_modifier < 0.0F && 1.0F + overall_speed_modifier < swim_modifier) {
                     swim_modifier = 1.0F + overall_speed_modifier;
                  }

                  if (swim_modifier < 1.0F && this.isCollidedHorizontally && !has_both_hands_occupied && !outside_of_liquid && !in_waterfall) {
                     swim_modifier = swim_modifier * 0.7F + 0.3F;
                  }
               }

               if (swim_modifier < 0.0F) {
                  swim_modifier = 0.0F;
               } else if (swim_modifier > 1.0F) {
                  swim_modifier = 1.0F;
               }

               this.motionY += 0.04 * (double)swim_modifier;
            }
         } else if (this.onGround && this.jumpTicks == 0) {
            this.jump();
            this.jumpTicks = 10;
         }
      } else {
         this.jumpTicks = 0;
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("travel");
      this.moveStrafing *= 0.98F;
      this.moveForward *= 0.98F;
      this.randomYawVelocity *= 0.9F;
      this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("push");
      if (!this.worldObj.isRemote) {
         this.is_collided_with_entities = false;
         this.collideWithNearbyEntities();
      }

      this.worldObj.theProfiler.endSection();
   }

   protected void updateAITasks() {
   }

   protected void collideWithNearbyEntities() {
      List var1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224, 0.0, 0.20000000298023224));
      if (var1 != null && !var1.isEmpty()) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            Entity var3 = (Entity)var1.get(var2);
            if (var3.canBePushed()) {
               this.collideWithEntity(var3);
               this.is_collided_with_entities = true;
            }
         }
      }

   }

   protected void collideWithEntity(Entity par1Entity) {
      par1Entity.applyEntityCollision(this);
   }

   public void updateRidden() {
      super.updateRidden();
      this.fallDistance = 0.0F;
   }

   public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
      this.yOffset = 0.0F;
      this.newPosX = par1;
      this.newPosY = par3;
      this.newPosZ = par5;
      this.newRotationYaw = (double)par7;
      this.newRotationPitch = (double)par8;
      this.newPosRotationIncrements = par9;
   }

   protected void updateAITick() {
   }

   protected void updateEntityActionState() {
   }

   public void setJumping(boolean par1) {
      this.isJumping = par1;
   }

   public void onItemPickup(Entity par1Entity, int par2) {
      if (!par1Entity.isDead && !this.worldObj.isRemote) {
         EntityTracker var3 = ((WorldServer)this.worldObj).getEntityTracker();
         if (par1Entity instanceof EntityItem) {
            var3.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
         }

         if (par1Entity instanceof EntityArrow) {
            var3.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
         }

         if (par1Entity instanceof EntityXPOrb) {
            var3.sendPacketToAllPlayersTrackingEntity(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
         }
      }

   }

   public final boolean canSeeEntity(Entity par1Entity) {
      return this.canSeeEntity(par1Entity, false);
   }

   public boolean canSeeEntity(Entity par1Entity, boolean ignore_leaves) {
      boolean seen = par1Entity.canEntityBeSeenFrom(this.posX, this.getEyePosY(), this.posZ, Double.MAX_VALUE, ignore_leaves);
      if (seen) {
         this.onEntitySeen(par1Entity);
      }

      return seen;
   }

   public void onEntitySeen(Entity entity) {
      if (this instanceof EntityPlayer) {
         entity.refreshDespawnCounter(-1200);
      } else if (entity instanceof EntityPlayer) {
         this.refreshDespawnCounter(-1200);
      }

   }

   public Vec3 getLookVec() {
      return this.getLook(1.0F);
   }

   public Vec3 getLook(float par1) {
      float var2;
      float var3;
      float var4;
      float var5;
      if (par1 == 1.0F) {
         var2 = MathHelper.cos(-this.rotationYaw * 0.017453292F - 3.1415927F);
         var3 = MathHelper.sin(-this.rotationYaw * 0.017453292F - 3.1415927F);
         var4 = -MathHelper.cos(-this.rotationPitch * 0.017453292F);
         var5 = MathHelper.sin(-this.rotationPitch * 0.017453292F);
         return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(var3 * var4), (double)var5, (double)(var2 * var4));
      } else {
         var2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * par1;
         var3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * par1;
         var4 = MathHelper.cos(-var3 * 0.017453292F - 3.1415927F);
         var5 = MathHelper.sin(-var3 * 0.017453292F - 3.1415927F);
         float var6 = -MathHelper.cos(-var2 * 0.017453292F);
         float var7 = MathHelper.sin(-var2 * 0.017453292F);
         return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(var5 * var6), (double)var7, (double)(var4 * var6));
      }
   }

   public float getSwingProgress(float par1) {
      float var2 = this.swingProgress - this.prevSwingProgress;
      if (var2 < 0.0F) {
         ++var2;
      }

      return this.prevSwingProgress + var2 * par1;
   }

   public Vec3 getPosition(float par1) {
      if (par1 == 1.0F) {
         return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
      } else {
         double var2 = this.prevPosX + (this.posX - this.prevPosX) * (double)par1;
         double var4 = this.prevPosY + (this.posY - this.prevPosY) * (double)par1;
         double var6 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par1;
         return this.worldObj.getWorldVec3Pool().getVecFromPool(var2, var4, var6);
      }
   }

   public Vec3 getEyePosition(float par1) {
      Vec3 vec3 = this.getPosition(par1);
      double delta_y = this.getEyePosY() - this.posY;
      vec3.yCoord += delta_y;
      return vec3;
   }

   public boolean canBeCollidedWith() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   public boolean canBePushed() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public double getFootPosY() {
      return this.posY;
   }

   public int getFootBlockPosY() {
      return MathHelper.floor_double(this.getFootPosY() + 9.999999747378752E-5);
   }

   public double getEyePosY() {
      return this.posY + (double)this.getEyeHeight();
   }

   public int getEyeBlockPosY() {
      return MathHelper.floor_double(this.getEyePosY());
   }

   public int getHeadBlockPosY() {
      return MathHelper.floor_double(this.getFootPosY() + 9.999999747378752E-5 + (double)this.height);
   }

   protected void setBeenAttacked() {
      this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
   }

   public float getRotationYawHead() {
      return this.rotationYawHead;
   }

   public void setRotationYawHead(float par1) {
      this.rotationYawHead = par1;
   }

   public float getAbsorptionAmount() {
      return this.field_110151_bq;
   }

   public void setAbsorptionAmount(float par1) {
      if (par1 < 0.0F) {
         par1 = 0.0F;
      }

      this.field_110151_bq = par1;
   }

   public Team getTeam() {
      return null;
   }

   public boolean isOnSameTeam(EntityLivingBase par1EntityLivingBase) {
      return this.isOnTeam(par1EntityLivingBase.getTeam());
   }

   public boolean isOnTeam(Team par1Team) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(par1Team) : false;
   }

   public void healByPercentage(float percentage) {
      if (!this.isDead) {
         float health = this.getHealth();
         if (health > 0.0F) {
            float max_health = this.getMaxHealth();
            if (health < max_health) {
               this.setHealth(health + max_health * percentage);
            }
         }

      }
   }

   public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
      par1EntityLivingBase.was_killed_by = this;
   }

   public boolean preysUpon(Entity entity) {
      return false;
   }

   public void addContainedItem(Item item) {
      this.addContainedItem(item.itemID);
   }

   public void addContainedItem(int item_id) {
      this.contained_items.add(item_id);
   }

   public String convertContainedItemsToString() {
      if (this.contained_items.isEmpty()) {
         return "";
      } else {
         Iterator i = this.contained_items.iterator();

         String s;
         for(s = ""; i.hasNext(); s = s + "|" + i.next()) {
         }

         return s;
      }
   }

   public void obtainContainedItemsFromString(String s) {
      char[] char_array = s.toCharArray();
      int item_id = 0;
      int digit_power = 0;

      for(int i = char_array.length - 1; i >= 0; --i) {
         char c = char_array[i];
         if (c == '|') {
            this.addContainedItem(item_id);
            item_id = 0;
            digit_power = 0;
         } else {
            item_id = (int)((double)item_id + (double)(c - 48) * Math.pow(10.0, (double)digit_power));
            ++digit_power;
         }
      }

   }

   public ItemStack[] getContainedItems() {
      if (this.contained_items != null && this.contained_items.size() != 0) {
         ItemStack[] item_stacks = new ItemStack[this.contained_items.size()];

         for(int i = 0; i < this.contained_items.size(); ++i) {
            item_stacks[i] = new ItemStack((Integer)this.contained_items.get(i));
         }

         return item_stacks;
      } else {
         return null;
      }
   }

   public void dropContainedItems() {
      Iterator i = this.contained_items.iterator();

      while(i.hasNext()) {
         this.dropItem((Integer)i.next(), 1);
      }

   }

   public static float getScale() {
      return 1.0F;
   }

   public void makeSound(String sound) {
      this.makeSound(sound, 1.0F, 1.0F);
   }

   public void makeSound(String sound, float volume_multiplier, float pitch_multiplier) {
      if (!this.isZevimrgvInTournament()) {
         this.worldObj.playSoundAtEntity(this, sound, this.getSoundVolume(sound) * volume_multiplier, this.getSoundPitch(sound) * pitch_multiplier);
      }
   }

   public void makeLongDistanceSound(String sound) {
      this.makeLongDistanceSound(sound, 1.0F, 1.0F);
   }

   public void makeLongDistanceSound(String sound, float volume_multiplier, float pitch_multiplier) {
      this.worldObj.playLongDistanceSoundAtEntity(this, sound, this.getSoundVolume(sound) * volume_multiplier, this.getSoundPitch(sound) * pitch_multiplier);
   }

   public void setLastHarmingEntity(Entity entity) {
      this.last_harming_entity = entity;
      this.last_harming_entity_memory_countdown = entity == null ? 0 : 900 + this.rand.nextInt(200);
   }

   public Entity getLastHarmingEntity() {
      return this.last_harming_entity;
   }

   public boolean isBadlyWounded() {
      return this.getHealth() / this.getMaxHealth() < 0.2F;
   }

   public boolean fleesWhenBadlyWounded() {
      return true;
   }

   public boolean considerFleeing() {
      Entity last_harming_entity = this.getLastHarmingEntity();
      if (last_harming_entity != null && !this.isEntityUndead() && this.fleesWhenBadlyWounded() && this.isBadlyWounded() && !(this.getDistanceToEntity(last_harming_entity) > 32.0F)) {
         this.has_decided_to_flee = this.rand.nextInt(2) == 0;
      } else {
         this.has_decided_to_flee = false;
      }

      return this.has_decided_to_flee;
   }

   public boolean considerStopFleeing() {
      Entity last_harming_entity = this.getLastHarmingEntity();
      if (last_harming_entity != null && this.fleesWhenBadlyWounded() && this.isBadlyWounded()) {
         if (this.getDistanceToEntity(last_harming_entity) > 40.0F) {
            this.fleeing = false;
            return true;
         } else {
            return false;
         }
      } else {
         this.has_decided_to_flee = false;
         this.fleeing = false;
         return true;
      }
   }

   public void onFleeing() {
   }

   public boolean hasCurse(Curse curse, boolean learn_effect_if_so) {
      return false;
   }

   public int getExperienceValue() {
      return 5;
   }

   public boolean canCatchFire() {
      return this.isHarmedByFire();
   }

   public boolean isHarmedByFire() {
      return true;
   }

   public boolean isHarmedByLava() {
      return true;
   }

   public boolean isHarmedByPepsin() {
      return this.isEntityBiologicallyAlive();
   }

   public boolean isHoldingItemThatPreventsHandDamage() {
      Item held_item = this.getHeldItem();
      return held_item != null && held_item.preventsHandDamage();
   }

   public float getHeldItemReachBonus() {
      Item held_item = this.getHeldItem();
      return held_item == null ? 0.0F : held_item.getReachBonus();
   }

   public Vec3 getFootPos() {
      return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.getFootPosY(), this.posZ);
   }

   public Vec3 getFootPosPlusFractionOfHeight(float fraction) {
      return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.getFootPosY() + (double)(this.height * fraction), this.posZ);
   }

   public final Vec3 getEyePos() {
      return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.getEyePosY(), this.posZ);
   }

   public Vec3 getPrimaryPointOfAttack() {
      return this.isEntityPlayer() ? this.getEyePos() : this.getFootPosPlusFractionOfHeight(0.75F);
   }

   public boolean isHarmedByDrowning() {
      return this.breathesAir() && !this.canBreatheUnderwater();
   }

   public boolean canBeDamagedByCacti() {
      return true;
   }

   public boolean canTakeDamageFromThrownEggs() {
      return false;
   }

   public boolean canBeKnockedBack() {
      return true;
   }

   public List getTargetPoints() {
      List target_points = new ArrayList();
      target_points.add(this.getFootPosPlusFractionOfHeight(0.5F));
      target_points.add(this.getFootPosPlusFractionOfHeight(0.9F));
      target_points.add(this.getFootPosPlusFractionOfHeight(0.1F));
      return target_points;
   }

   public Vec3 getCenterPoint() {
      return this.getFootPosPlusFractionOfHeight(0.5F);
   }

   public boolean canOnlyPerformWeakStrike() {
      return false;
   }

   public boolean isHoldingAnEffectiveTool(Block block, int metadata) {
      ItemStack held_item_stack = this.getHeldItemStack();
      if (held_item_stack != null && held_item_stack.getItem().isTool()) {
         ItemTool tool = (ItemTool)held_item_stack.getItem();
         return tool.isEffectiveAgainstBlock(block, metadata);
      } else {
         return false;
      }
   }

   public boolean canEntityBeSeenFrom(double x, double y, double z, double max_range_sq, boolean ignore_leaves) {
      Vec3 origin = this.worldObj.getVec3(x, y, z);
      if (this.getCenterPoint().squareDistanceTo(origin) > max_range_sq) {
         return false;
      } else {
         RaycastPolicies policies = RaycastPolicies.for_vision(ignore_leaves);
         return this.worldObj.checkForNoBlockCollision(origin, this.getFootPosPlusFractionOfHeight(0.25F), policies) || this.worldObj.checkForNoBlockCollision(origin, this.getFootPosPlusFractionOfHeight(0.75F), policies);
      }
   }

   public final RaycastCollision getBlockCollisionAlongLookVector(RaycastPolicies policies, float partial_tick, double distance_to_limit) {
      Vec3 origin = this.getEyePosition(partial_tick);
      Vec3 limit = origin.applyDirectionAndDistance(this.getLook(partial_tick), distance_to_limit);
      return this.worldObj.getBlockCollisionForPolicies(origin, limit, policies, this);
   }

   public void setRotationForLookingAt(Vec3 target_pos) {
      Vec3 eye_pos = this.getEyePos();
      this.setRotation(EntityLookHelper.getYawForLookingAt(eye_pos, target_pos), EntityLookHelper.getPitchForLookingAt(eye_pos, target_pos));
   }

   public boolean isInRain() {
      return this.worldObj.isInRain(this.getBlockPosX(), MathHelper.floor_double(this.getFootPosY() + (double)this.height), this.getBlockPosZ());
   }

   public boolean isInPrecipitation() {
      return this.worldObj.isPrecipitatingAt(this.getBlockPosX(), MathHelper.floor_double(this.getFootPosY() + (double)this.height), this.getBlockPosZ());
   }

   public boolean canSilkHarvestBlock(Block block, int metadata) {
      if (block.canSilkHarvest(metadata) && EnchantmentHelper.getSilkTouchModifier(this)) {
         ItemStack held_item_stack = this.getHeldItemStack();
         return held_item_stack != null && held_item_stack.isTool() && held_item_stack.getItemAsTool().isEffectiveAgainstBlock(block, metadata);
      } else {
         return false;
      }
   }

   public Block getBlockAtFeet() {
      return this.worldObj.getBlock(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ());
   }

   public Material getBlockMaterialAtFeet() {
      return this.worldObj.getBlockMaterial(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ());
   }

   public Block getBlockBelow() {
      return this.worldObj.getBlock(this.getBlockPosX(), this.getFootBlockPosY() - 1, this.getBlockPosZ());
   }

   static {
      sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896, 2)).setSaved(false);
   }

   /***
    * Removes all potion effects that have curativeItem as a curative item for its effect
    * @param curativeItem The itemstack we are using to cure potion effects
    */
   public void curePotionEffects(ItemStack curativeItem)
   {
      Iterator<Integer> potionKey = activePotionsMap.keySet().iterator();

      if (worldObj.isRemote)
      {
         return;
      }

      while (potionKey.hasNext())
      {
         Integer key = potionKey.next();
         PotionEffect effect = (PotionEffect)activePotionsMap.get(key);

         if (effect.isCurativeItem(curativeItem))
         {
            potionKey.remove();
            onFinishedPotionEffect(effect);
         }
      }
   }

   /**
    * Returns true if the entity's rider (EntityPlayer) should face forward when mounted.
    * currently only used in vanilla code by pigs.
    *
    * @param player The player who is riding the entity.
    * @return If the player should orient the same direction as this entity.
    */
   public boolean shouldRiderFaceForward(EntityPlayer player)
   {
      return this instanceof EntityPig;
   }
}
