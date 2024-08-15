package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.Hopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements Hopper {
   private boolean isBlocked = true;
   private int transferTicker = -1;

   public EntityMinecartHopper(World par1World) {
      super(par1World);
   }

   public EntityMinecartHopper(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public int getMinecartType() {
      return 5;
   }

   public Block getDefaultDisplayTile() {
      return Block.hopperBlock;
   }

   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   public int getSizeInventory() {
      return 5;
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (player.onServer()) {
         player.displayGUIHopperMinecart(this);
      }

      return true;
   }

   public void onActivatorRailPass(int par1, int par2, int par3, boolean par4) {
      boolean var5 = !par4;
      if (var5 != this.getBlocked()) {
         this.setBlocked(var5);
      }

   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   public void setBlocked(boolean par1) {
      this.isBlocked = par1;
   }

   public World getWorldObj() {
      return this.worldObj;
   }

   public double getXPos() {
      return this.posX;
   }

   public double getYPos() {
      return this.posY;
   }

   public double getZPos() {
      return this.posZ;
   }

   public int getX() {
      return MathHelper.floor_double(this.posX);
   }

   public int getY() {
      return MathHelper.floor_double(this.posY);
   }

   public int getZ() {
      return MathHelper.floor_double(this.posZ);
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked()) {
         --this.transferTicker;
         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.func_96112_aD()) {
               this.setTransferTicker(4);
               this.onInventoryChanged();
            }
         }
      }

   }

   public boolean func_96112_aD() {
      if (TileEntityHopper.suckItemsIntoHopper(this)) {
         return true;
      } else {
         List var1 = this.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.25, 0.0, 0.25), IEntitySelector.selectAnything);
         if (var1.size() > 0) {
            TileEntityHopper.insertStackFromEntity(this, (EntityItem)var1.get(0));
         }

         return false;
      }
   }

   public void killMinecart(DamageSource par1DamageSource) {
      super.killMinecart(par1DamageSource);
      this.dropItem(Block.hopperBlock.blockID, 1, 0.0F);
   }

   protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeEntityToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setInteger("TransferCooldown", this.transferTicker);
   }

   protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readEntityFromNBT(par1NBTTagCompound);
      this.transferTicker = par1NBTTagCompound.getInteger("TransferCooldown");
   }

   public void setTransferTicker(int par1) {
      this.transferTicker = par1;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   public Item getModelItem() {
      return Item.minecartHopper;
   }
}
