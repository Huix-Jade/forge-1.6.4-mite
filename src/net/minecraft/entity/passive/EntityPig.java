package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAIFleeAttackerOrPanic;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPig extends EntityLivestock {
   private final EntityAIControlledByPlayer aiControlledByPlayer;

   public EntityPig(World par1World) {
      super(par1World);
      this.setSize(0.9F, 0.9F);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiControlledByPlayer = new EntityAIControlledByPlayer(this, 0.3F));
      this.tasks.addTask(3, new EntityAIMate(this, 1.0));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickFlint.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickObsidian.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickCopper.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickSilver.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickGold.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickIron.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickMithril.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickAdamantium.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrotOnAStickAncientMetal.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.carrot.itemID, false));
      this.tasks.addTask(4, new EntityAITempt(this, 1.2, Item.getItem(Block.mushroomBrown).itemID, false));
      this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(1, new EntityAIFleeAttackerOrPanic(this, 1.4F, 0.5F, true));
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.setManurePeriod(this.getManurePeriod() * 2);
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25);
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public boolean canBeSteered() {
      ItemStack var1 = ((EntityPlayer)this.riddenByEntity).getHeldItemStack();
      return var1 != null && var1.getItem() instanceof ItemCarrotOnAStick;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setBoolean("Saddle", this.getSaddled());
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.setSaddled(par1NBTTagCompound.getBoolean("Saddle"));
   }

   protected String getLivingSound() {
      return "mob.pig.say";
   }

   protected String getHurtSound() {
      return "mob.pig.say";
   }

   protected String getDeathSound() {
      return "mob.pig.death";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.pig.step", 0.15F, 1.0F);
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (super.onEntityRightClicked(player, item_stack)) {
         return true;
      } else if (this.getSaddled() && this.riddenByEntity == null) {
         if (player.onServer()) {
            player.mountEntity(this);
         }

         return true;
      } else {
         return false;
      }
   }

   protected int getDropItemId() {
      return this.isBurning() ? Item.porkCooked.itemID : Item.porkRaw.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (this.getSaddled()) {
         this.dropItem(Item.saddle);
      }

      if (this.isWell()) {
         int num_drops = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + damage_source.getButcheringModifier());

         for(int i = 0; i < num_drops; ++i) {
            this.dropItem(this.isBurning() ? Item.porkCooked : Item.porkRaw);
         }
      }

   }

   public boolean getSaddled() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSaddled(boolean par1) {
      if (par1) {
         this.dataWatcher.updateObject(16, (byte)1);
      } else {
         this.dataWatcher.updateObject(16, (byte)0);
      }

   }

   public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
      if (!this.worldObj.isRemote) {
         EntityPigZombie var2 = new EntityPigZombie(this.worldObj);
         var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.worldObj.spawnEntityInWorld(var2);
         this.setDead();
      }

   }

   protected void fall(float par1) {
      super.fall(par1);
      if (par1 > 5.0F && this.riddenByEntity instanceof EntityPlayer) {
         ((EntityPlayer)this.riddenByEntity).triggerAchievement(AchievementList.flyPig);
      }

   }

   public EntityPig spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
      return new EntityPig(this.worldObj);
   }

   public boolean isFoodItem(ItemStack item_stack) {
      if (item_stack != null) {
         Item item = item_stack.getItem();
         if (item == Item.carrot) {
            return true;
         }

         if (item instanceof ItemBlock) {
            ItemBlock item_block = (ItemBlock)item;
            if (item_block.getBlock() == Block.mushroomBrown) {
               return true;
            }
         }
      }

      return false;
   }

   public EntityAIControlledByPlayer getAIControlledByPlayer() {
      return this.aiControlledByPlayer;
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.spawnBabyAnimal(par1EntityAgeable);
   }

   public void produceGoods() {
      this.production_counter = 0;
   }

   public float getAIMoveSpeed() {
      return this.riddenByEntity == null ? super.getAIMoveSpeed() : 0.25F;
   }
}
