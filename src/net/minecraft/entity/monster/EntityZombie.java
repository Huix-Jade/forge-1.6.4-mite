package net.minecraft.entity.monster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAnimalWatcher;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveToFoodItem;
import net.minecraft.entity.ai.EntityAIMoveToTree;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMeat;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.mite.RandomItemListEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public class EntityZombie extends EntityAnimalWatcher {
   protected static final Attribute field_110186_bp = (new RangedAttribute("zombie.spawnReinforcements", 0.0, 0.0, 1.0)).func_111117_a("Spawn Reinforcements Chance");
   private int conversionTime;
   Item[] rare_drops_standard;
   Item[] rare_drops_villager;
   private boolean is_smart;
   private int profession;

   public EntityZombie(World par1World) {
      super(par1World);
      this.rare_drops_standard = new Item[]{Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget};
      this.rare_drops_villager = new Item[]{Item.seeds, Item.pumpkinSeeds, Item.melonSeeds, Item.carrot, Item.potato, Item.onion};
      this.getNavigator().setBreakDoors(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIBreakDoor(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(5, new EntityAIMoveThroughVillage(this, 1.0, false));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
      this.tasks.addTask(2, new EntityAIMoveToFoodItem(this, 1.0F, true));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityAnimal.class, 1.0, true));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 10, true));
      this.tasks.addTask(3, new EntityAIMoveToTree(this, 1.0F));
      this.is_smart = this.isRevenant() || this.rand.nextInt(8) == 0;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.followRange, 40.0);
      this.setEntityAttribute(SharedMonsterAttributes.movementSpeed, 0.23000000417232513);
      this.setEntityAttribute(SharedMonsterAttributes.attackDamage, 5.0);
      this.setEntityAttribute(field_110186_bp, this.rand.nextDouble() * 0.10000000149011612);
   }

   protected void entityInit() {
      super.entityInit();
      this.getDataWatcher().addObject(12, (byte)0);
      this.getDataWatcher().addObject(13, (byte)0);
      this.getDataWatcher().addObject(14, (byte)0);
   }

   protected boolean isAIEnabled() {
      return true;
   }

   public boolean isChild() {
      return false;
   }

   public void setChild(boolean par1) {
      par1 = false;
      this.getDataWatcher().updateObject(12, (byte)(par1 ? 1 : 0));
      if (this.worldObj != null && !this.worldObj.isRemote) {
         AttributeInstance var2 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      }

   }

   public boolean isVillager() {
      return this.getDataWatcher().getWatchableObjectByte(13) == 1;
   }

   public void setVillager(boolean par1, int profession) {
      if (Minecraft.isInTournamentMode()) {
         par1 = false;
      }

      this.profession = par1 ? profession : 0;
      if (par1 && !this.isVillager() && this.wasVillagerThatWasGeneratedWithVillage()) {
         this.tasks.addTask(1, new EntityAIRestrictSun(this));
         this.tasks.addTask(2, new EntityAIFleeSun(this, 1.0));
      }

      this.getDataWatcher().updateObject(13, (byte)(par1 ? 1 : 0));
   }

   public boolean isRevenant() {
      return false;
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityWasNegativelyAffected() && damage.wasCausedByPlayer()) {
            this.is_smart = true;
         }

         return result;
      } else {
         return result;
      }
   }

   public void onUpdate() {
      if (!this.worldObj.isRemote && this.isConverting()) {
         int var1 = this.getConversionTimeBoost();
         this.conversionTime -= var1;
         if (this.conversionTime <= 0) {
            this.convertToVillager();
         }
      }

      super.onUpdate();
   }

   protected String getLivingSound() {
      return "mob.zombie.say";
   }

   protected String getHurtSound() {
      return "mob.zombie.hurt";
   }

   protected String getDeathSound() {
      return "mob.zombie.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.zombie.step", 0.15F, 1.0F);
   }

   protected int getDropItemId() {
      return 0;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (this.rand.nextFloat() < (recently_hit_by_player ? 0.5F : 0.25F)) {
         this.dropItem(Item.rottenFlesh.itemID, 1);
      }

      if (recently_hit_by_player && !this.has_taken_massive_fall_damage && this.rand.nextInt(this.getBaseChanceOfRareDrop()) < 5 + damage_source.getLootingModifier() * 2) {
         Item[] rare_drops = this.isVillager() ? this.rare_drops_villager : this.rare_drops_standard;
         this.dropItem(rare_drops[this.rand.nextInt(rare_drops.length)].itemID, 1);
      }

   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   protected int getBaseChanceOfRareDrop() {
      return !this.isVillager() && !this.isRevenant() ? 200 : 50;
   }

   public void addRandomWeapon() {
      if (this.rand.nextFloat() < (this.isVillager() ? 0.2F : 0.05F)) {
         List items = new ArrayList();
         items.add(new RandomItemListEntry(Item.shovelWood, 1));
         items.add(new RandomItemListEntry(Item.shovelRustedIron, 2));
         if (this.worldObj.getDayOfWorld() >= 10 && !Minecraft.isInTournamentMode()) {
            items.add(new RandomItemListEntry(Item.hatchetRustedIron, 1));
         }

         if (this.isVillager()) {
            items.add(new RandomItemListEntry(Item.shearsRustedIron, 1));
            items.add(new RandomItemListEntry(Item.scytheRustedIron, 1));
            if (this.worldObj.getDayOfWorld() >= 10) {
               items.add(new RandomItemListEntry(Item.hoeRustedIron, 1));
               items.add(new RandomItemListEntry(Item.mattockRustedIron, 1));
            }

            if (this.worldObj.getDayOfWorld() >= 20 && !Minecraft.isInTournamentMode()) {
               items.add(new RandomItemListEntry(Item.pickaxeRustedIron, 1));
            }
         } else {
            items.add(new RandomItemListEntry(Item.cudgelWood, 1));
            items.add(new RandomItemListEntry(Item.clubWood, 1));
            items.add(new RandomItemListEntry(Item.swordRustedIron, 1));
            items.add(new RandomItemListEntry(Item.daggerRustedIron, 1));
         }

         RandomItemListEntry entry = (RandomItemListEntry)WeightedRandom.getRandomItem(this.rand, (Collection)items);
         this.setCurrentItemOrArmor(0, (new ItemStack(entry.item)).randomizeForMob(this, true));
      }

   }

   protected void addRandomEquipment() {
      this.addRandomWeapon();
      this.addRandomArmor();
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.isChild()) {
         par1NBTTagCompound.setBoolean("IsBaby", true);
      }

      if (this.isVillager()) {
         par1NBTTagCompound.setBoolean("IsVillager", true);
         par1NBTTagCompound.setInteger("Profession", this.profession);
      }

      par1NBTTagCompound.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);
      par1NBTTagCompound.setBoolean("is_smart", this.is_smart);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.getBoolean("IsBaby")) {
         this.setChild(true);
      }

      if (par1NBTTagCompound.getBoolean("IsVillager")) {
         this.setVillager(true, par1NBTTagCompound.getInteger("Profession"));
      }

      if (par1NBTTagCompound.hasKey("ConversionTime") && par1NBTTagCompound.getInteger("ConversionTime") > -1) {
         this.startConversion(par1NBTTagCompound.getInteger("ConversionTime"));
      }

      this.is_smart = par1NBTTagCompound.getBoolean("is_smart");
   }

   public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
      super.onKillEntity(par1EntityLivingBase);
      if (this.worldObj.difficultySetting >= 2 && par1EntityLivingBase instanceof EntityVillager) {
         if (this.getHeldItem() instanceof ItemTool) {
            return;
         }

         if (this.worldObj.difficultySetting == 2 && this.rand.nextBoolean()) {
            return;
         }

         EntityZombie var2 = new EntityZombie(this.worldObj);
         var2.copyLocationAndAnglesFrom(par1EntityLivingBase);
         this.worldObj.removeEntity(par1EntityLivingBase);
         var2.onSpawnWithEgg((EntityLivingData)null);

         for(int i = 0; i < 5; ++i) {
            this.setCurrentItemOrArmor(i, (ItemStack)null);
         }

         var2.setVillager(true, ((EntityVillager)par1EntityLivingBase).getProfession());
         if (par1EntityLivingBase.isChild()) {
            var2.setChild(true);
         }

         this.worldObj.spawnEntityInWorld(var2);
         this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
      }

   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
      float var2 = this.worldObj.getLocationTensionFactor(this.posX, this.posY, this.posZ);
      this.setCanPickUpLoot(true);
      if (par1EntityLivingData1 == null) {
         par1EntityLivingData1 = new EntityZombieGroupData(this, this.worldObj.rand.nextFloat() < 0.05F, this.worldObj.rand.nextFloat() < 0.05F, (EntityZombieINNER1)null);
      }

      if (par1EntityLivingData1 instanceof EntityZombieGroupData && !this.isRevenant()) {
         EntityZombieGroupData var3 = (EntityZombieGroupData)par1EntityLivingData1;
         if (var3.field_142046_b) {
            this.setVillager(true, 0);
         }

         if (var3.field_142048_a) {
            this.setChild(true);
         }
      }

      this.addRandomEquipment();
      if (this.getCurrentItemOrArmor(4) == null) {
         Calendar var5 = this.worldObj.getCurrentDate();
         if (var5.get(2) + 1 == 10 && var5.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
            this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Block.pumpkinLantern : Block.pumpkin));
            this.equipmentDropChances[4] = 0.0F;
         }
      }

      this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05000000074505806, 0));
      if (this.rand.nextFloat() < var2 * 0.05F) {
         this.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25 + 0.5, 0));
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0 + 1.0, 2));
      }

      return (EntityLivingData)par1EntityLivingData1;
   }

   public void startConversion(int par1) {
      this.conversionTime = par1;
      this.getDataWatcher().updateObject(14, (byte)1);
      this.removePotionEffect(Potion.weakness.id);
      this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, par1, Math.min(this.worldObj.difficultySetting - 1, 0)));
      this.worldObj.setEntityState(this, EnumEntityState.zombie_conversion);
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.zombie_conversion) {
         this.worldObj.playSound(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "mob.zombie.remedy", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public boolean wasVillagerThatWasGeneratedWithVillage() {
      return false;
   }

   public boolean canDespawn() {
      return !this.isConverting() && !this.wasVillagerThatWasGeneratedWithVillage() ? super.canDespawn() : false;
   }

   public boolean isConverting() {
      return this.getDataWatcher().getWatchableObjectByte(14) == 1;
   }

   protected void convertToVillager() {
      EntityVillager var1 = new EntityVillager(this.worldObj);
      var1.copyLocationAndAnglesFrom(this);
      var1.onSpawnWithEgg((EntityLivingData)null);
      var1.setProfession(this.profession);
      var1.func_82187_q();
      if (this.isChild()) {
         var1.setGrowingAgeToNewborn();
      }

      this.worldObj.removeEntity(this);
      this.worldObj.spawnEntityInWorld(var1);
      var1.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1017, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
   }

   protected int getConversionTimeBoost() {
      int var1 = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int var2 = 0;

         for(int var3 = (int)this.posX - 4; var3 < (int)this.posX + 4 && var2 < 14; ++var3) {
            for(int var4 = (int)this.posY - 4; var4 < (int)this.posY + 4 && var2 < 14; ++var4) {
               for(int var5 = (int)this.posZ - 4; var5 < (int)this.posZ + 4 && var2 < 14; ++var5) {
                  int var6 = this.worldObj.getBlockId(var3, var4, var5);
                  if (var6 == Block.fenceIron.blockID || var6 == Block.bed.blockID) {
                     if (this.rand.nextFloat() < 0.3F) {
                        ++var1;
                     }

                     ++var2;
                  }
               }
            }
         }
      }

      return var1;
   }

	public boolean preysUpon(Entity entity) {
      return entity instanceof EntityAnimal;
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() instanceof ItemMeat;
   }

   public boolean isDiggingEnabled() {
      if (this.isHoldingItemThatPreventsDigging()) {
         return false;
      } else {
         return !this.is_smart && !this.isFrenzied() ? this.getHeldItem() instanceof ItemTool : true;
      }
   }

   public boolean drawBackFaces() {
      return this.isWearingItems(true);
   }

   public final boolean isHarmedByPepsin() {
      return true;
   }
}
