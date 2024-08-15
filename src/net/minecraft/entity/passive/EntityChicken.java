package net.minecraft.entity.passive;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityDamageResult;
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
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityChicken extends EntityLivestock {
   public float field_70886_e;
   public float destPos;
   public float field_70884_g;
   public float field_70888_h;
   public float field_70889_i = 1.0F;
   private int max_num_feathers;
   private int num_feathers;

   public EntityChicken(World par1World) {
      super(par1World);
      this.setSize(0.3F, 0.7F);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0, Item.seeds.itemID, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.tasks.addTask(1, new EntityAIFleeAttackerOrPanic(this, 1.3F, 0.75F, true));
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.setManurePeriod(this.getManurePeriod() * 8 * 2);
         this.max_num_feathers = this.rand.nextInt(2) + 1;
         if (this.max_num_feathers > 1 && this.rand.nextInt(2) == 0) {
            --this.max_num_feathers;
         }

         this.num_feathers = this.max_num_feathers;
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(4.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25);
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("max_num_feathers", this.max_num_feathers);
      par1NBTTagCompound.setInteger("num_feathers", this.num_feathers);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.max_num_feathers = par1NBTTagCompound.getInteger("max_num_feathers");
      this.num_feathers = par1NBTTagCompound.getInteger("num_feathers");
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.field_70888_h = this.field_70886_e;
      this.field_70884_g = this.destPos;
      this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3);
      if (this.destPos < 0.0F) {
         this.destPos = 0.0F;
      }

      if (this.destPos > 1.0F) {
         this.destPos = 1.0F;
      }

      if (!this.onGround && this.field_70889_i < 1.0F) {
         this.field_70889_i = 1.0F;
      }

      this.field_70889_i = (float)((double)this.field_70889_i * 0.9);
      if (!this.onGround && this.motionY < 0.0) {
         this.motionY *= 0.6;
      }

      this.field_70886_e += this.field_70889_i * 2.0F;
   }

   public void produceGoods() {
      int feather_threshold = 100;
      if (this.production_counter >= feather_threshold && this.rand.nextInt(feather_threshold * 5) == 0) {
         this.gainFeather();
         this.production_counter -= feather_threshold;
      } else {
         int egg_threshold = 200;
         if (this.production_counter >= egg_threshold && this.rand.nextInt(20) == 0) {
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(Item.egg.itemID, 1);
            this.production_counter -= egg_threshold;
         }

      }
   }

   protected void fall(float par1) {
   }

   protected String getLivingSound() {
      return "mob.chicken.say";
   }

   protected String getHurtSound() {
      return "mob.chicken.hurt";
   }

   protected String getDeathSound() {
      return "mob.chicken.hurt";
   }

   protected void playStepSound(int par1, int par2, int par3, int par4) {
      this.makeSound("mob.chicken.step", 0.15F, 1.0F);
   }

   protected int getDropItemId() {
      return Item.feather.itemID;
   }

   protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
      if (this.isBurning()) {
         if (this.isWell()) {
            this.dropItem(Item.chickenCooked);
         }
      } else {
         for(int i = 0; i < this.num_feathers; ++i) {
            this.dropItem(Item.feather);
         }

         if (this.isWell()) {
            this.dropItem(Item.chickenRaw);
         }
      }

   }

   public EntityChicken spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
      return new EntityChicken(this.worldObj);
   }

   public boolean isFoodItem(ItemStack item_stack) {
      return item_stack != null && item_stack.getItem() == Item.seeds;
   }

   public EntityAgeable createChild(EntityAgeable par1EntityAgeable) {
      return this.spawnBabyAnimal(par1EntityAgeable);
   }

   public EntityDamageResult attackEntityFrom(Damage damage) {
      EntityDamageResult result = super.attackEntityFrom(damage);
      if (result != null && !result.entityWasDestroyed()) {
         if (result.entityWasKnockedBack()) {
            if (this.getHealth() <= 0.0F || this.isChild() || damage.isFireDamage() || damage.isLavaDamage() || damage.hasMagicAspect() || damage.isDrowning() || damage.isStarving()) {
               return result;
            }

            for(int i = 0; i < (int)damage.getAmount() && this.num_feathers > 0; ++i) {
               if (this.rand.nextFloat() < 0.2F && this.tryDropFeather(false)) {
                  result.setEntityWasAffected();
               }
            }
         }

         return result;
      } else {
         return result;
      }
   }

   protected void jump() {
      super.jump();
      if (!this.isChild() && this.rand.nextInt(40) == 0 && !this.isInsideOfMaterial(Material.water)) {
         this.tryDropFeather(true);
      }

   }

   protected void gainFeather() {
      if (this.num_feathers < this.max_num_feathers) {
         ++this.num_feathers;
      } else {
         this.dropItem(Item.feather.itemID, 1);
      }

   }

   protected boolean tryDropFeather(boolean retain_at_least_one) {
      if (this.num_feathers >= (retain_at_least_one ? 2 : 1) && !this.isChild()) {
         this.dropItem(Item.feather.itemID, 1);
         --this.num_feathers;
         return true;
      } else {
         return false;
      }
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }
}
