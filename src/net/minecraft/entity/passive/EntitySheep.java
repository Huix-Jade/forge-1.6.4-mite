package net.minecraft.entity.passive;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityGelatinousCube;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFleeAttackerOrPanic;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.CraftingResult;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class EntitySheep extends EntityLivestock implements IShearable {
   private final InventoryCrafting field_90016_e = new InventoryCrafting(new ContainerSheep(this), 2, 1);
   public static final float[][] fleeceColorTable = new float[][]{{1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};
   private int sheepTimer;
   private EntityAIEatGrass aiEatGrass = new EntityAIEatGrass(this);
   private int fire_damage_to_wool;

   public EntitySheep(World par1World) {
      super(par1World);
      this.setSize(0.9F, 1.3F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.1, Item.wheat.itemID, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(5, this.aiEatGrass);
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.field_90016_e.setInventorySlotContents(0, new ItemStack(Item.dyePowder, 1, 0));
      this.field_90016_e.setInventorySlotContents(1, new ItemStack(Item.dyePowder, 1, 0));
      this.tasks.addTask(1, new EntityAIFleeAttackerOrPanic(this, 1.4F, 0.5F, true));
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.setManurePeriod(this.getManurePeriod() * 2);
      }

   }

   protected void updateAITasks() {
      this.sheepTimer = this.aiEatGrass.getEatGrassTick();
      super.updateAITasks();
   }

   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         this.sheepTimer = Math.max(0, this.sheepTimer - 1);
      }

      if (this.fire_damage_to_wool > 0 && this.onServer() && !this.isBurning() && this.ticksExisted % 100 == 0) {
         --this.fire_damage_to_wool;
      }

      super.onLivingUpdate();
   }

   public void onEntityDamaged(DamageSource damage_source, float amount) {
      if (damage_source.isFireDamage() && !this.getSheared()) {
         if (++this.fire_damage_to_wool > 5) {
            this.setSheared(true);
            this.fire_damage_to_wool = 0;
            this.extinguish();
         }
      } else if (!this.getSheared()) {
         if (damage_source.getResponsibleEntity() instanceof EntityGelatinousCube) {
            this.setSheared(true);
            this.entityFX(EnumEntityFX.steam_with_hiss);
         } else if (damage_source.isGelatinousSphereDamage()) {
            this.setSheared(true);
         }
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (!this.getSheared() && !this.isBurning() && this.rand.nextInt(2) == 0) {
         this.dropItemStack(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()));
      }

      if (this.isWell()) {
         int num_drops = this.rand.nextInt(2) + this.rand.nextInt(1 + damage_source.getButcheringModifier());
         if (num_drops < 1) {
            num_drops = 1;
         }

         for(int i = 0; i < num_drops; ++i) {
            this.dropItem(this.isBurning() ? Item.lambchopCooked.itemID : Item.lambchopRaw.itemID, 1);
         }
      }

      if (this.rand.nextInt(2) == 0) {
         this.dropItem(Item.leather.itemID, 1);
      }

   }

   protected int getDropItemId() {
      return Block.cloth.blockID;
   }

   public void handleHealthUpdate(EnumEntityState par1) {
      if (par1 == EnumEntityState.tnt_ignite_or_eating_grass) {
         this.sheepTimer = 40;
      } else {
         super.handleHealthUpdate(par1);
      }

   }

   public float func_70894_j(float par1) {
      return this.sheepTimer <= 0 ? 0.0F : (this.sheepTimer >= 4 && this.sheepTimer <= 36 ? 1.0F : (this.sheepTimer < 4 ? ((float)this.sheepTimer - par1) / 4.0F : -((float)(this.sheepTimer - 40) - par1) / 4.0F));
   }

   public float func_70890_k(float par1) {
      if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
         float var2 = ((float)(this.sheepTimer - 4) - par1) / 32.0F;
         return 0.62831855F + 0.2199115F * MathHelper.sin(var2 * 28.7F);
      } else {
         return this.sheepTimer > 0 ? 0.62831855F : this.rotationPitch / 57.295776F;
      }
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("Sheared", this.getSheared());
      par1NBTTagCompound.setByte("Color", (byte)this.getFleeceColor());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setSheared(par1NBTTagCompound.getBoolean("Sheared"));
      this.setFleeceColor(par1NBTTagCompound.getByte("Color"));
   }

   protected String getLivingSound() {
      return "mob.sheep.say";
   }

   protected String getHurtSound() {
      return "mob.sheep.say";
   }

   protected String getDeathSound() {
      return "mob.sheep.say";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.sheep.step", 0.15F, 1.0F);
   }

   public int getFleeceColor() {
      return this.dataWatcher.getWatchableObjectByte(16) & 15;
   }

   public void setFleeceColor(int par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      this.dataWatcher.updateObject(16, (byte)(var2 & 240 | par1 & 15));
   }

   public boolean getSheared() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
   }

   public void setSheared(boolean par1) {
      byte var2 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)(var2 | 16));
      } else {
         this.dataWatcher.updateObject(16, (byte)(var2 & -17));
      }

   }

   public static int getRandomFleeceColor(Random par0Random) {
      int var1 = par0Random.nextInt(100);
      return var1 < 5 ? 15 : (var1 < 10 ? 7 : (var1 < 15 ? 8 : (var1 < 18 ? 12 : (par0Random.nextInt(500) == 0 ? 6 : 0))));
   }

   public EntitySheep func_90015_b(EntityAgeable par1EntityAgeable) {
      EntitySheep var2 = (EntitySheep)par1EntityAgeable;
      EntitySheep var3 = new EntitySheep(this.worldObj);
      int var4 = this.func_90014_a(this, var2);
      var3.setFleeceColor(15 - var4);
      return var3;
   }

   public void eatGrassBonus() {
      this.setSheared(false);
      if (this.isChild()) {
         this.addGrowth(60);
      }

      this.addFood(0.5F);
   }

   public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
      par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
      this.setFleeceColor(getRandomFleeceColor(this.worldObj.rand));
      return par1EntityLivingData;
   }

   private int func_90014_a(EntityAnimal par1EntityAnimal, EntityAnimal par2EntityAnimal) {
      int var3 = this.func_90013_b(par1EntityAnimal);
      int var4 = this.func_90013_b(par2EntityAnimal);
      this.field_90016_e.getStackInSlot(0).setItemSubtype(var3);
      this.field_90016_e.getStackInSlot(1).setItemSubtype(var4);
      CraftingResult crafting_result = CraftingManager.getInstance().findMatchingRecipe(this.field_90016_e, ((EntitySheep)par1EntityAnimal).worldObj, (EntityPlayer)null);
      ItemStack var5 = crafting_result == null ? null : crafting_result.item_stack;
      int var6;
      if (var5 != null && var5.getItem().itemID == Item.dyePowder.itemID) {
         var6 = var5.getItemSubtype();
      } else {
         var6 = this.worldObj.rand.nextBoolean() ? var3 : var4;
      }

      return var6;
   }

   private int func_90013_b(EntityAnimal par1EntityAnimal) {
      return 15 - ((EntitySheep)par1EntityAnimal).getFleeceColor();
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.func_90015_b(par1EntityAgeable);
   }

   public void produceGoods() {
      this.production_counter = 0;
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() == Item.wheat;
   }

   public boolean tryDyeing(ItemStack item_stack) {
      if (item_stack != null && item_stack.getItem() == Item.dyePowder) {
         int color = BlockColored.getBlockFromDye(item_stack.getItemSubtype());
         if (!this.getSheared() && this.getFleeceColor() != color) {
            if (this.onServer()) {
               this.setFleeceColor(color);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isShearable(ItemStack item, World world, int X, int Y, int Z)
   {
      return !getSheared() && !isChild();
   }

   @Override
   public ArrayList<ItemStack> onSheared(ItemStack item, World world, int X, int Y, int Z, int fortune)
   {
      ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
      setSheared(true);
      int i = 1 + rand.nextInt(3);
      for (int j = 0; j < i; j++)
      {
         ret.add(new ItemStack(Block.cloth.blockID, 1, getFleeceColor()));
      }
      this.worldObj.playSoundAtEntity(this, "mob.sheep.shear", 1.0F, 1.0F);
      return ret;
   }
}
