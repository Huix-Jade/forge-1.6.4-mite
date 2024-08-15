package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging {
   private float itemDropChance = 1.0F;

   public EntityItemFrame(World par1World) {
      super(par1World);
   }

   public EntityItemFrame(World par1World, int par2, int par3, int par4, int par5) {
      super(par1World, par2, par3, par4, par5);
      this.setDirection(par5);
   }

   protected void entityInit() {
      this.getDataWatcher().addObjectByDataType(2, 5);
      this.getDataWatcher().addObject(3, (byte)0);
   }

   public int getWidthPixels() {
      return 9;
   }

   public int getHeightPixels() {
      return 9;
   }

   public boolean isInRangeToRenderDist(double par1) {
      double var3 = 16.0;
      var3 *= 64.0 * this.renderDistanceWeight;
      return par1 < var3 * var3;
   }

   public void onBroken(Entity par1Entity) {
      ItemStack var2 = this.getDisplayedItem();
      if (par1Entity instanceof EntityPlayer) {
         EntityPlayer var3 = (EntityPlayer)par1Entity;
         if (var3.capabilities.isCreativeMode) {
            this.removeFrameFromMap(var2);
            return;
         }
      }

      this.dropItemStack(new ItemStack(Item.itemFrame), 0.0F);
      if (var2 != null && this.rand.nextFloat() < this.itemDropChance) {
         var2 = var2.copy();
         this.removeFrameFromMap(var2);
         this.dropItemStack(var2, 0.0F);
      }

   }

   private void removeFrameFromMap(ItemStack par1ItemStack) {
      if (par1ItemStack != null) {
         if (par1ItemStack.itemID == Item.map.itemID) {
            MapData var2 = ((ItemMap)par1ItemStack.getItem()).getMapData(par1ItemStack, this.worldObj);
            var2.playersVisibleOnMap.remove("frame-" + this.entityId);
         }

         par1ItemStack.setItemFrame((EntityItemFrame)null);
      }

   }

   public ItemStack getDisplayedItem() {
      return this.getDataWatcher().getWatchableObjectItemStack(2);
   }

   public void setDisplayedItem(ItemStack par1ItemStack) {
      par1ItemStack = par1ItemStack.copy();
      par1ItemStack.stackSize = 1;
      par1ItemStack.setItemFrame(this);
      this.getDataWatcher().updateObject(2, par1ItemStack);
      this.getDataWatcher().setObjectWatched(2);
   }

   public int getRotation() {
      return this.getDataWatcher().getWatchableObjectByte(3);
   }

   public void setItemRotation(int par1) {
      this.getDataWatcher().updateObject(3, (byte)(par1 % 4));
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      if (this.getDisplayedItem() != null) {
         par1NBTTagCompound.setCompoundTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
         par1NBTTagCompound.setByte("ItemRotation", (byte)this.getRotation());
         par1NBTTagCompound.setFloat("ItemDropChance", this.itemDropChance);
      }

      super.writeEntityToNBT(par1NBTTagCompound);
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      NBTTagCompound var2 = par1NBTTagCompound.getCompoundTag("Item");
      if (var2 != null && !var2.hasNoTags()) {
         this.setDisplayedItem(ItemStack.loadItemStackFromNBT(var2));
         this.setItemRotation(par1NBTTagCompound.getByte("ItemRotation"));
         if (par1NBTTagCompound.hasKey("ItemDropChance")) {
            this.itemDropChance = par1NBTTagCompound.getFloat("ItemDropChance");
         }
      }

      super.readEntityFromNBT(par1NBTTagCompound);
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (this.getDisplayedItem() == null) {
         if (item_stack == null) {
            return super.onEntityRightClicked(player, item_stack);
         }

         if (player.onServer()) {
            this.setDisplayedItem(item_stack);
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }
      } else if (player.onServer()) {
         this.setItemRotation(this.getRotation() + 1);
      }

      return true;
   }

   public Item getModelItem() {
      return Item.itemFrame;
   }

   public boolean canTakeDamageFromPlayerThrownSnowballs() {
      return true;
   }
}
