package net.minecraft.entity;

import net.minecraft.block.BitHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.world.World;

public class EntityEarthElemental extends EntityAnimalWatcher {
   private static final int MAGMA_BIT = 256;
   public static final int STONE_NORMAL;
   public static final int STONE_MAGMA;
   public static final int OBSIDIAN_NORMAL;
   public static final int OBSIDIAN_MAGMA;
   public static final int NETHERRACK_NORMAL;
   public static final int NETHERRACK_MAGMA;
   public static final int END_STONE_NORMAL;
   public static final int END_STONE_MAGMA;
   public static final int CLAY_NORMAL;
   public static final int CLAY_HARDENED;
   private int heat = 0;
   private int data_object_id_type;
   private int ticks_until_next_fizz_sound;

   public EntityEarthElemental(World world) {
      super(world);
      this.getNavigator().setBreakDoors(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIBreakDoor(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
   }

   public static int getType(Block block, boolean is_magma) {
      return block.blockID + (is_magma ? 256 : 0);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 20.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.20000000298023224);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 12.0);
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 30.0);
   }

   public float getNaturalDefense() {
      return 4.0F;
   }

   public float getNaturalDefense(DamageSource damage_source) {
      return super.getNaturalDefense(damage_source) + (damage_source.bypassesMundaneArmor() ? 0.0F : this.getNaturalDefense());
   }

   public int getBlockHarvestLevel() {
      return this.getBlock().getMinHarvestLevel(0);
   }

   protected void entityInit() {
      super.entityInit();
      this.data_object_id_type = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Short((short)0));
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setShort("type", (short)this.getType());
      par1NBTTagCompound.setShort("heat", (short)this.heat);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setType(par1NBTTagCompound.getShort("type"));
      this.heat = par1NBTTagCompound.getShort("heat");
   }

   protected boolean isValidLightLevel() {
      return this.isInNether() || super.isValidLightLevel();
   }

   public float getBlockPathWeight(int x, int y, int z) {
      return 0.0F;
   }

   public Block getBlockBelow() {
      return this.worldObj.getBlock(this.getBlockPosX(), this.getFootBlockPosY() - 1, this.getBlockPosZ());
   }

   public boolean isValidBlock(Block block) {
      return block == Block.stone || block == Block.obsidian || block == Block.netherrack || block == Block.whiteStone;
   }

   public boolean getCanSpawnHere(boolean perform_light_check) {
      return this.isValidBlock(this.getBlockBelow()) && super.getCanSpawnHere(perform_light_check);
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
      this.setTypeForBlock(this.getBlockBelow(), this.getBlockMaterialAtFeet() == Material.lava || this.isClay() && this.getBlockMaterialAtFeet() == Material.fire);
      return super.onSpawnWithEgg(data);
   }

   protected int setType(int type) {
      this.dataWatcher.updateObject(this.data_object_id_type, (short)type);
      return type;
   }

   public int getType() {
      return this.dataWatcher.getWatchableObjectShort(this.data_object_id_type);
   }

   public void setTypeForBlock(Block block, boolean heated) {
      this.setType(block == Block.whiteStone ? END_STONE_NORMAL : (block == Block.netherrack ? NETHERRACK_NORMAL : (block == Block.obsidian ? OBSIDIAN_NORMAL : STONE_NORMAL)));
      if (heated) {
         this.convertToMagma();
      }

   }

   public boolean isMagma() {
      if (this.onServer()) {
         return this.heat >= 100;
      } else {
         return BitHelper.isBitSet(this.getType(), 256);
      }
   }

   public boolean isClay() {
      return this.getClass() == EntityClayGolem.class;
   }

   public boolean isNormalClay() {
      return this.isClay() && !this.isHardenedClay();
   }

   public boolean isHardenedClay() {
      if (!this.isClay()) {
         return false;
      } else if (this.onServer() && this.heat >= 100) {
         return true;
      } else {
         return this.getType() == CLAY_HARDENED;
      }
   }

   public boolean isImmuneTo(DamageSource damage_source) {
      if (this.isNormalClay()) {
         return super.isImmuneTo(damage_source);
      } else if (damage_source == DamageSource.fall) {
         return false;
      } else if (damage_source.isMelee() && damage_source.getResponsibleEntity() instanceof EntityIronGolem) {
         return false;
      } else {
         ItemStack item_stack = damage_source.getItemAttackedWith();
         if (item_stack != null && item_stack.getItem() instanceof ItemTool && item_stack.getItemAsTool().isEffectiveAgainstBlock(this.getBlock(), 0)) {
            return false;
         } else {
            return !damage_source.isExplosion();
         }
      }
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public void convertToMagma() {
      this.heat = 1000;
      if (this.isClay()) {
         if (this.getType() != this.setType(CLAY_HARDENED)) {
            this.entityFX(EnumEntityFX.smoke);
         }

      } else {
         if (this.getType() != this.setType(this.getType() | 256)) {
            this.entityFX(EnumEntityFX.smoke);
         }

      }
   }

   public void convertToNormal(boolean steam) {
      this.heat = 0;
      if (this.isClay()) {
         Debug.setErrorMessage("convertToNormal: Why called for Clay Golem?");
      } else {
         if (this.getType() != this.setType(BitHelper.clearBit(this.getType(), 256)) && steam) {
            this.causeQuenchEffect();
         }

      }
   }

   public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
      super.onStruckByLightning(par1EntityLightningBolt);
      if (!this.isMagma() && !this.isHardenedClay()) {
         this.convertToMagma();
      } else if (this.heat < 1000) {
         this.heat = 1000;
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getClass() == EntityClayGolem.class) {
         if (this.getType() != CLAY_NORMAL && this.getType() != CLAY_HARDENED) {
            Debug.setErrorMessage("onLivingUpdate: EntityClayGolem has invalid type: " + this.getType());
         }
      } else if (this.getType() == CLAY_NORMAL || this.getType() == CLAY_HARDENED) {
         Debug.setErrorMessage("onLivingUpdate: EntityEarthElemental has invalid type: " + this.getType());
      }

      if (this.onClient()) {
         if (!this.inWater && this.isMagma() && this.isInPrecipitation()) {
            this.spawnSteamParticles(1);
         }
      } else if (this.inWater) {
         if (this.isMagma()) {
            this.convertToNormal(true);
         }

         this.heat = 0;
      } else {
         if (this.handleLavaMovement() || this.isClay() && this.isInFire()) {
            if (this.heat < 1000 && ++this.heat == 100) {
               this.convertToMagma();
            }
         } else if (this.heat > 0) {
            boolean was_magma = this.isMagma();
            int cooling = 1;
            if (this.isInPrecipitation()) {
               ++cooling;
            }

            int x = this.getBlockPosX();
            int y = this.getFootBlockPosY();
            int z = this.getBlockPosZ();
            if (this.worldObj.isFreezing(x, z)) {
               ++cooling;
            }

            if (this.worldObj.getBlockMaterial(x, y, z).isFreezing()) {
               ++cooling;
            }

            --y;
            if (this.worldObj.getBlockMaterial(x, y, z).isFreezing()) {
               ++cooling;
            }

            this.heat -= cooling;
            if (this.heat < 0) {
               this.heat = 0;
            }

            if (was_magma && !this.isMagma()) {
               this.convertToNormal(this.isInPrecipitation());
            }
         }

         if (this.isMagma() && this.isInPrecipitation() && --this.ticks_until_next_fizz_sound <= 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.ticks_until_next_fizz_sound = this.rand.nextInt(7) + 2;
         }
      }

   }

   public final boolean preysUpon(Entity entity) {
      return entity instanceof EntityVillager;
   }

   public EntityDamageResult attackEntityAsMob(Entity target) {
      this.swingArm();
      EntityDamageResult result = super.attackEntityAsMob(target);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityWasNegativelyAffected() && this.isMagma() && this.getRNG().nextFloat() < 0.4F) {
            target.setFire(1 + this.rand.nextInt(8));
         }

         return result;
      } else {
         return result;
      }
   }

   public Block getBlock() {
      return Block.getBlock(this.getType() & 255);
   }

   protected int getDropItemId() {
      Block block = this.getBlock();
      return block == Block.stone ? Block.cobblestone.blockID : block.blockID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      this.dropItem(this.getDropItemId(), 1);
   }

   public int getExperienceValue() {
      return super.getExperienceValue() * 3;
   }

   public int getMaxSpawnedInChunk() {
      return 1;
   }

   public boolean healsWithTime() {
      return true;
   }

   public boolean isEntityBiologicallyAlive() {
      return false;
   }

   public boolean isHarmedByFire() {
      return false;
   }

   public boolean isHarmedByLava() {
      return false;
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean breathesAir() {
      return false;
   }

   public int getBrightnessForRender(float par1) {
      int brightness = super.getBrightnessForRender(par1);
      if (!this.isMagma()) {
         return brightness;
      } else {
         int blocklight = brightness & 255;
         int skylight = brightness >> 16;
         blocklight = Math.max(blocklight, 80);
         skylight = Math.max(skylight, 80);
         return skylight << 16 | blocklight;
      }
   }

   protected String getHurtSound() {
      return "mob.irongolem.hit";
   }

   protected String getDeathSound() {
      return "mob.irongolem.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      super.playStepSound(par1, par2, par3, par4);
      this.playSound("mob.irongolem.walk", 0.25F, 1.0F);
   }

   static {
      STONE_NORMAL = getType(Block.stone, false);
      STONE_MAGMA = getType(Block.stone, true);
      OBSIDIAN_NORMAL = getType(Block.obsidian, false);
      OBSIDIAN_MAGMA = getType(Block.obsidian, true);
      NETHERRACK_NORMAL = getType(Block.netherrack, false);
      NETHERRACK_MAGMA = getType(Block.netherrack, true);
      END_STONE_NORMAL = getType(Block.whiteStone, false);
      END_STONE_MAGMA = getType(Block.whiteStone, true);
      CLAY_NORMAL = getType(Block.blockClay, false);
      CLAY_HARDENED = getType(Block.hardenedClay, false);
   }
}
