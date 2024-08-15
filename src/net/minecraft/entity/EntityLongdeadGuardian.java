package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemDagger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityLongdeadGuardian extends EntityLongdead {
   ItemStack stowed_item_stack;

   public EntityLongdeadGuardian(World world) {
      super(world);
   }

   public void addRandomWeapon() {
      super.addRandomWeapon();
      if (this.getHeldItem() instanceof ItemBow) {
         this.stowed_item_stack = (new ItemStack(Item.daggerAncientMetal)).randomizeForMob(this, true);
      }

   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      if (par1NBTTagCompound.hasKey("stowed_item_stack")) {
         this.stowed_item_stack = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("stowed_item_stack"));
      } else {
         this.stowed_item_stack = null;
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      if (this.stowed_item_stack != null) {
         NBTTagCompound compound = new NBTTagCompound();
         this.stowed_item_stack.writeToNBT(compound);
         par1NBTTagCompound.setCompoundTag("stowed_item_stack", compound);
      }

   }

   public ItemStack getStowedItemStack() {
      return this.stowed_item_stack;
   }

   public void swapHeldItemStackWithStowed() {
      ItemStack item_stack = this.stowed_item_stack;
      this.stowed_item_stack = this.getHeldItemStack();
      this.setHeldItemStack(item_stack);
   }

   public boolean canStowItem(Item item) {
      if (this.getSkeletonType() != 0) {
         return false;
      } else {
         return item instanceof ItemDagger || item instanceof ItemBow;
      }
   }

   public boolean canNeverPickUpItem(Item item) {
      return this.getSkeletonType() == 0 && !this.canStowItem(item);
   }

   public boolean isHoldingRangedWeapon() {
      return this.getHeldItem() instanceof ItemBow;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.stowed_item_stack != null && (this.getHeldItemStack() == null || this.getTicksExistedWithOffset() % 10 == 0)) {
         if (this.getHeldItemStack() == null) {
            this.swapHeldItemStackWithStowed();
         } else {
            Entity target = this.getTarget();
            if (target != null && this.canSeeTarget(true)) {
               double distance = (double)this.getDistanceToEntity(target);
               if (this.isHoldingRangedWeapon()) {
                  if (distance < 5.0) {
                     this.swapHeldItemStackWithStowed();
                  }
               } else if (distance > 6.0) {
                  this.swapHeldItemStackWithStowed();
               }
            }
         }
      }

   }
}
