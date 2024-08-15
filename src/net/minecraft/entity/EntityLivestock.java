package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.EntityAIAvoidPotentialPredators;
import net.minecraft.entity.ai.EntityAIGetOutOfWater;
import net.minecraft.entity.ai.EntityAISeekFoodIfHungry;
import net.minecraft.entity.ai.EntityAISeekOpenSpaceIfCrowded;
import net.minecraft.entity.ai.EntityAISeekShelterFromRain;
import net.minecraft.entity.ai.EntityAISeekWaterIfThirsty;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityLivestock extends EntityAnimal {
   private float food;
   private float water;
   private float freedom;
   protected int data_object_id_is_well;
   protected int data_object_id_is_thirsty;
   protected int production_counter;
   private int manure_period;
   private int manure_countdown;
   public int last_trampled_x;
   public int last_trampled_y;
   public int last_trampled_z;
   public boolean has_been_spooked_by_other_animal;

   public EntityLivestock(World world) {
      super(world);
      this.tasks.addTask(2, new EntityAISeekFoodIfHungry(this, 1.0F, true));
      this.tasks.addTask(2, new EntityAISeekWaterIfThirsty(this, 1.0F, false));
      this.tasks.addTask(3, new EntityAISeekOpenSpaceIfCrowded(this, 1.0F));
      this.tasks.addTask(2, new EntityAIAvoidPotentialPredators(this, 1.0F, true));
      this.tasks.addTask(4, new EntityAISeekShelterFromRain(this, 1.0F, true));
      this.tasks.addTask(4, new EntityAIGetOutOfWater(this, 1.0F));
      if (world != null && !world.isRemote) {
         this.setFood(0.8F + this.rand.nextFloat() * 0.2F);
         this.setWater(0.8F + this.rand.nextFloat() * 0.2F);
         this.setFreedom(0.8F + this.rand.nextFloat() * 0.2F);
         this.setManurePeriod(24000);
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.data_object_id_is_well = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)-1));
      this.data_object_id_is_thirsty = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Byte((byte)0));
   }

   public void setFood(float food) {
      if (!this.worldObj.isRemote) {
         this.food = MathHelper.clamp_float(food, 0.0F, 1.0F);
         this.setIsWell(this.isWell());
      }
   }

   protected void addFood(float food) {
      this.setFood(this.getFood() + food);
   }

   public float getFood() {
      return this.food;
   }

   public void setWater(float water) {
      if (!this.worldObj.isRemote) {
         this.water = MathHelper.clamp_float(water, 0.0F, 1.0F);
         this.setIsWell(this.isWell());
         this.setIsThirsty(this.isThirsty());
      }
   }

   public void addWater(float water) {
      this.setWater(this.getWater() + water);
   }

   public float getWater() {
      return this.water;
   }

   protected void setFreedom(float freedom) {
      if (!this.worldObj.isRemote) {
         this.freedom = MathHelper.clamp_float(freedom, 0.0F, 1.0F);
         this.setIsWell(this.isWell());
      }
   }

   protected void addFreedom(float freedom) {
      this.setFreedom(this.getFreedom() + freedom);
   }

   public float getFreedom() {
      return this.freedom;
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setFloat("food", this.getFood());
      par1NBTTagCompound.setFloat("water", this.getWater());
      par1NBTTagCompound.setFloat("freedom", this.getFreedom());
      par1NBTTagCompound.setInteger("production_counter", this.production_counter);
      par1NBTTagCompound.setInteger("manure_countdown", this.manure_countdown);
      par1NBTTagCompound.setBoolean("has_been_spooked_by_other_animal", this.has_been_spooked_by_other_animal);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setFood(par1NBTTagCompound.getFloat("food"));
      this.setWater(par1NBTTagCompound.getFloat("water"));
      this.setFreedom(par1NBTTagCompound.getFloat("freedom"));
      this.production_counter = par1NBTTagCompound.getInteger("production_counter");
      this.manure_countdown = par1NBTTagCompound.getInteger("manure_countdown");
      this.has_been_spooked_by_other_animal = par1NBTTagCompound.getBoolean("has_been_spooked_by_other_animal");
   }

   protected boolean setIsWell(boolean is_well) {
      this.dataWatcher.updateObject(this.data_object_id_is_well, (byte)(is_well ? -1 : 0));
      return is_well;
   }

   public boolean isWell() {
      if (this.worldObj.isRemote) {
         return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_well) != 0;
      } else {
         return Math.min(this.getFreedom(), Math.min(this.getFood(), this.getWater())) >= 0.25F;
      }
   }

   protected boolean setIsThirsty(boolean is_thirsty) {
      this.dataWatcher.updateObject(this.data_object_id_is_thirsty, (byte)(is_thirsty ? -1 : 0));
      return is_thirsty;
   }

   public boolean isThirsty() {
      if (this.worldObj.isRemote) {
         return this.dataWatcher.getWatchableObjectByte(this.data_object_id_is_thirsty) != 0;
      } else {
         return this.getWater() < 0.5F;
      }
   }

   public boolean isHungry() {
      return this.getFood() < 0.5F;
   }

   public boolean isVeryHungry() {
      return this.getFood() < 0.25F;
   }

   public boolean isDesperateForFood() {
      return this.getFood() < 0.05F;
   }

   public boolean isVeryThirsty() {
      return this.getWater() < 0.25F;
   }

   public boolean isDesperateForWater() {
      return this.getWater() < 0.05F;
   }

   public final boolean isNearFood() {
      return this.isNearFood(0.0F);
   }

   public final boolean isNearFood(float chance_of_destroying_food) {
      return this.isNearFood(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), chance_of_destroying_food);
   }

   public final boolean isNearFood(int x, int y, int z) {
      return this.isNearFood(x, y, z, 0.0F);
   }

   public final boolean isNearFood(int x, int y, int z, float chance_of_destroying_food) {
      int height = MathHelper.floor_double((double)this.height);

      for(int dx = -1; dx <= 1; ++dx) {
         for(int dy = -1; dy <= height; ++dy) {
            for(int dz = -1; dz <= 1; ++dz) {
               if (this.isFoodSource(this.worldObj.getBlockId(x + dx, y + dy, z + dz))) {
                  if (chance_of_destroying_food > 0.0F && this.rand.nextFloat() < chance_of_destroying_food) {
                     this.worldObj.destroyBlock((new BlockBreakInfo(this.worldObj, x + dx, y + dy, z + dz)).setEatenBy(this), false);
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isNearWaterSource() {
      return this.isNearWaterSource(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
   }

   public boolean isNearWaterSource(int x, int y, int z) {
      int height = MathHelper.floor_double((double)this.height);

      for(int dx = -1; dx <= 1; ++dx) {
         for(int dy = -1; dy <= height; ++dy) {
            for(int dz = -1; dz <= 1; ++dz) {
               if (this.isWaterSource(x + dx, y + dy, z + dz) && !this.worldObj.isBlockFaceFlatAndSolid(x + dx, y + dy + 1, z + dz, EnumFace.BOTTOM)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isCrowded() {
      return !this.isOutdoors() || this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(2.0, 0.5, 2.0)).size() > 2;
   }

   public boolean isCrowded(int x, int y, int z) {
      if (!this.worldObj.isOutdoors(x, y, z)) {
         return true;
      } else {
         AxisAlignedBB bounding_box = new AxisAlignedBB((double)(x - 2), (double)((float)y - 0.5F), (double)(z - 2), (double)(x + 2), (double)((float)y + 0.5F), (double)(z + 2));
         return this.worldObj.getEntitiesWithinAABB(EntityLiving.class, bounding_box).size() > 2;
      }
   }

   public boolean updateWellness() {
      float benefit = 0.1F;
      float penalty = -0.005F;
      if (this.isNearFood(this instanceof EntityCow ? 0.01F : 0.0F)) {
         this.addFood(benefit);
      } else {
         this.addFood(penalty);
      }

      if (this.isNearWaterSource()) {
         this.addWater(benefit);
      } else if (this.isInRain()) {
         this.addWater(benefit / 10.0F);
      } else {
         this.addWater(penalty);
      }

      if (!this.isCrowded()) {
         this.addFreedom(benefit);
      } else {
         this.addFreedom(penalty);
      }

      return this.isWell();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         if (this.ticksExisted % 100 == 0) {
            if (this.rand.nextInt(10) > 0 && this.updateWellness() && !this.isChild()) {
               ++this.production_counter;
            }

            this.produceGoods();
         }

         if (!this.isChild()) {
            if (!this.isDesperateForFood() && --this.manure_countdown <= 0) {
               this.dropItem(Item.manure.itemID, 1);
               this.manure_countdown = this.manure_period / 2 + this.rand.nextInt(this.manure_period);
            }

            if (this.onGround) {
               if (this.ticksExisted % 1000 == 0) {
                  this.last_trampled_x = this.last_trampled_y = this.last_trampled_z = 0;
               }

               int x = MathHelper.floor_double(this.posX);
               int y = MathHelper.floor_double(this.posY) - 1;
               int z = MathHelper.floor_double(this.posZ);
               if (x != this.last_trampled_x || y != this.last_trampled_y || z != this.last_trampled_z) {
                  this.last_trampled_x = x;
                  this.last_trampled_y = y;
                  this.last_trampled_z = z;
                  Block block = Block.blocksList[this.worldObj.getBlockId(x, y, z)];
                  if (block != null) {
                     block.onTrampledBy(this.worldObj, x, y, z, this);
                  }
               }
            }
         }

         if (this.has_been_spooked_by_other_animal && this.worldObj.total_time % 4000L == 0L) {
            this.has_been_spooked_by_other_animal = false;
         }

         if ((this.has_decided_to_flee || this.fleeing || this.isSpooked()) && this.getTicksExistedWithOffset() % 20 == 0) {
            List list = this.worldObj.getEntitiesWithinAABB(EntityLivestock.class, this.boundingBox.expand(8.0, 4.0, 8.0));
            Iterator i = list.iterator();

            while(i.hasNext()) {
               EntityLivestock livestock = (EntityLivestock)i.next();
               if (livestock != this && !livestock.has_decided_to_flee && !livestock.fleeing && !livestock.isSpooked() && !livestock.has_been_spooked_by_other_animal && !livestock.isDead && livestock.canSeeEntity(this) && livestock.canPathTo(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ(), 8)) {
                  livestock.spooked_until = this.worldObj.getTotalWorldTime() + 400L + (long)this.worldObj.rand.nextInt(400);
                  livestock.has_been_spooked_by_other_animal = true;
               }
            }
         }
      }

   }

   public abstract void produceGoods();

   protected boolean isFoodSource(int block_id) {
      Block block = Block.blocksList[block_id];
      return block == Block.grass || block == Block.tallGrass;
   }

   public int[] getFoodBlockIDs() {
      int[] block_ids = new int[]{Block.grass.blockID, Block.tallGrass.blockID};
      return block_ids;
   }

   protected boolean isWaterSource(int x, int y, int z) {
      Block block = Block.blocksList[this.worldObj.getBlockId(x, y, z)];
      if (block != Block.waterStill && block != Block.waterMoving && block != Block.snow && block != Block.blockSnow) {
         return block == Block.cauldron && (double)(y + 1) < this.getEyePosY() && this.worldObj.getBlockMetadata(x, y, z) > 0;
      } else {
         return true;
      }
   }

   public int[] getWaterBlockIDs() {
      int[] block_ids = new int[]{Block.waterStill.blockID, Block.waterMoving.blockID, Block.snow.blockID, Block.blockSnow.blockID, Block.cauldron.blockID};
      return block_ids;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int block_id = this.worldObj.getBlockId(x, y, z);
      int block_id_below = this.worldObj.getBlockId(x, y - 1, z);
      if (!this.isFoodSource(block_id) && !this.isFoodSource(block_id_below) && !this.isNearFood(x, y, z)) {
         return !this.isWaterSource(x, y, z) && !this.isWaterSource(x, y - 1, z) && this.isNearWaterSource(x, y, z) ? 20.0F : super.getBlockPathWeight(x, y, z);
      } else {
         return 20.0F;
      }
   }

   public void onFoodEaten(ItemStack item_stack) {
      if (!this.worldObj.isRemote) {
         this.addFood(0.5F);
      }

      super.onFoodEaten(item_stack);
   }

   public void func_110196_bT() {
      if (this.isWell()) {
         super.func_110196_bT();
      }

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

   public int getManurePeriod() {
      return this.manure_period;
   }

   public void setManurePeriod(int manure_period) {
      this.manure_period = manure_period;
      this.manure_countdown = (int)(Math.random() * (double)manure_period);
   }

   public int getExperienceValue() {
      return 0;
   }

   public void setGrowingAge(int par1) {
      if (!this.isChild() || par1 <= this.getGrowingAge() || !this.isDesperateForFood() && !this.isDesperateForWater()) {
         super.setGrowingAge(par1);
      }
   }

   public void adoptWellnessFromParents(EntityAnimal parent_a, EntityAnimal parent_b) {
      if (this.onClient()) {
         Minecraft.setErrorMessage("adoptWellnessFromParents: cannot be called on client");
      } else {
         float food_parent_a = parent_a instanceof EntityLivestock ? ((EntityLivestock)parent_a).getFood() : 1.0F;
         float food_parent_b = parent_b instanceof EntityLivestock ? ((EntityLivestock)parent_b).getFood() : 1.0F;
         float water_parent_a = parent_a instanceof EntityLivestock ? ((EntityLivestock)parent_a).getWater() : 1.0F;
         float water_parent_b = parent_b instanceof EntityLivestock ? ((EntityLivestock)parent_b).getWater() : 1.0F;
         float freedom_parent_a = parent_a instanceof EntityLivestock ? ((EntityLivestock)parent_a).getFreedom() : 1.0F;
         float freedom_parent_b = parent_b instanceof EntityLivestock ? ((EntityLivestock)parent_b).getFreedom() : 1.0F;
         this.setFood(Math.min(food_parent_a, food_parent_b));
         this.setWater(Math.min(water_parent_a, water_parent_b));
         this.setFreedom(Math.min(freedom_parent_a, freedom_parent_b));
      }
   }

   public boolean willEat(ItemStack item_stack) {
      return this.hasFullHealth() && !this.isWell() && !this.isHungry() ? false : super.willEat(item_stack);
   }
}
