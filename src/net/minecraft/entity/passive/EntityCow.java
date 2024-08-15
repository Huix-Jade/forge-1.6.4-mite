package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFleeAttackerOrPanic;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCow extends EntityLivestock {
   int data_object_id_milk;

   public EntityCow(World par1World) {
      super(par1World);
      this.setSize(0.9F, 1.3F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.25, Item.wheat.itemID, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.tasks.addTask(1, new EntityAIFleeAttackerOrPanic(this, 1.6F, 0.5F, true));
      this.setMilk(100);
   }

   protected void entityInit() {
      super.entityInit();
      this.data_object_id_milk = this.dataWatcher.addObject(this.dataWatcher.getNextAvailableId(), new Integer(0));
   }

   public void setMilk(int milk) {
      this.dataWatcher.updateObject(this.data_object_id_milk, MathHelper.clamp_int(milk, 0, 100));
   }

   public int getMilk() {
      return this.isChild() ? 0 : this.dataWatcher.getWatchableObjectInt(this.data_object_id_milk);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.setEntityAttribute(SharedMonsterAttributes.maxHealth, 20.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.20000000298023224);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("milk", this.getMilk());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setMilk(par1NBTTagCompound.getInteger("milk"));
   }

   protected String getLivingSound() {
      return "mob.cow.say";
   }

   protected String getHurtSound() {
      return "mob.cow.hurt";
   }

   protected String getDeathSound() {
      return "mob.cow.hurt";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.cow.step", 0.15F, 1.0F);
   }

   protected float getSoundVolume(String sound) {
      return sound.equals("mob.cow.step") ? 0.8F : 0.4F;
   }

   public void produceGoods() {
      this.setMilk(this.getMilk() + this.production_counter);
      this.production_counter = 0;
   }

   protected int getDropItemId() {
      return Item.leather.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      int num_drops = this.rand.nextInt(3) + 1;

      int i;
      for(i = 0; i < num_drops; ++i) {
         this.dropItem(Item.leather.itemID, 1);
      }

      if (this.isWell()) {
         num_drops = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + damage_source.getButcheringModifier());
         if (num_drops == 1 && this.rand.nextInt(2) == 0) {
            ++num_drops;
         }

         for(i = 0; i < num_drops; ++i) {
            this.dropItem(this.isBurning() ? Item.beefCooked.itemID : Item.beefRaw.itemID, 1);
         }
      }

   }

   public EntityCow spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
      return new EntityCow(this.worldObj);
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.spawnBabyAnimal(par1EntityAgeable);
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() == Item.wheat;
   }

   protected boolean isFoodSource(int block_id) {
      Block block = Block.blocksList[block_id];
      return block == Block.tallGrass || block == Block.plantYellow;
   }

   public int[] getFoodBlockIDs() {
      int[] block_ids = new int[]{Block.tallGrass.blockID, Block.plantYellow.blockID};
      return block_ids;
   }
}
