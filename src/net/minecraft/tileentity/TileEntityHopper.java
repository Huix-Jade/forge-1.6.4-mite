package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockStrongbox;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntity implements Hopper {
   private ItemStack[] hopperItemStacks = new ItemStack[5];
   private int transferCooldown = -1;

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
      this.hopperItemStacks = new ItemStack[this.getSizeInventory()];
      this.transferCooldown = par1NBTTagCompound.getInteger("TransferCooldown");

      for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
         byte var5 = var4.getByte("Slot");
         if (var5 >= 0 && var5 < this.hopperItemStacks.length) {
            this.hopperItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
         }
      }

   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.hopperItemStacks.length; ++var3) {
         if (this.hopperItemStacks[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte)var3);
            this.hopperItemStacks[var3].writeToNBT(var4);
            var2.appendTag(var4);
         }
      }

      par1NBTTagCompound.setTag("Items", var2);
      par1NBTTagCompound.setInteger("TransferCooldown", this.transferCooldown);
   }

   public void onInventoryChanged() {
      super.onInventoryChanged();
   }

   public int getSizeInventory() {
      return this.hopperItemStacks.length;
   }

   public ItemStack getStackInSlot(int par1) {
      return this.hopperItemStacks[par1];
   }

   public ItemStack decrStackSize(int par1, int par2) {
      if (this.hopperItemStacks[par1] != null) {
         ItemStack var3;
         if (this.hopperItemStacks[par1].stackSize <= par2) {
            var3 = this.hopperItemStacks[par1];
            this.hopperItemStacks[par1] = null;
            return var3;
         } else {
            var3 = this.hopperItemStacks[par1].splitStack(par2);
            if (this.hopperItemStacks[par1].stackSize == 0) {
               this.hopperItemStacks[par1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if (this.hopperItemStacks[par1] != null) {
         ItemStack var2 = this.hopperItemStacks[par1];
         this.hopperItemStacks[par1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.hopperItemStacks[par1] = par2ItemStack;
      if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
         par2ItemStack.stackSize = this.getInventoryStackLimit();
      }

   }

   public String getUnlocalizedInvName() {
      return "container.hopper";
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
   }

   public void openChest() {
   }

   public void closeChest() {
   }

   public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
      return true;
   }

   public void updateEntity() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         --this.transferCooldown;
         if (!this.isCoolingDown()) {
            this.setTransferCooldown(0);
            this.updateHopper();
         }
      }

   }

   public boolean updateHopper() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         if (!this.isCoolingDown() && BlockHopper.getIsBlockNotPoweredFromMetadata(this.getBlockMetadata())) {
            boolean var1 = this.insertItemToInventory();
            var1 = suckItemsIntoHopper(this) || var1;
            if (var1) {
               this.setTransferCooldown(8);
               this.onInventoryChanged();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean insertItemToInventory() {
      IInventory var1 = this.getOutputInventory();
      if (var1 == null) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.getSizeInventory(); ++var2) {
            if (this.getStackInSlot(var2) != null) {
               ItemStack var3 = this.getStackInSlot(var2).copy();
               ItemStack var4 = insertStack(var1, this.decrStackSize(var2, 1), Facing.oppositeSide[BlockHopper.getDirectionFromMetadata(this.getBlockMetadata())]);
               if (var4 == null || var4.stackSize == 0) {
                  var1.onInventoryChanged();
                  return true;
               }

               this.setInventorySlotContents(var2, var3);
            }
         }

         return false;
      }
   }

   private static boolean isFurnaceAbove(Hopper par0Hopper) {
      return par0Hopper.getWorldObj().getBlock(par0Hopper.getX(), par0Hopper.getY() + 1, par0Hopper.getZ()) instanceof BlockFurnace;
   }

   public static boolean suckItemsIntoHopper(Hopper par0Hopper) {
      IInventory var1 = getInventoryAboveHopper(par0Hopper);
      if (var1 != null) {
         byte var2 = 0;
         if (var1 instanceof ISidedInventory && var2 > -1) {
            ISidedInventory var7 = (ISidedInventory)var1;
            int[] var8 = var7.getAccessibleSlotsFromSide(var2);

            for(int var5 = 0; var5 < var8.length; ++var5) {
               if (insertStackFromInventory(par0Hopper, var1, var8[var5], var2)) {
                  return true;
               }
            }
         } else {
            int var3 = var1.getSizeInventory();

            for(int var4 = 0; var4 < var3; ++var4) {
               if (insertStackFromInventory(par0Hopper, var1, var4, var2)) {
                  return true;
               }
            }
         }
      } else if (!par0Hopper.getWorldObj().isBlockFaceFlatAndSolid(MathHelper.floor_double(par0Hopper.getXPos()), MathHelper.floor_double(par0Hopper.getYPos()) + 1, MathHelper.floor_double(par0Hopper.getZPos()), EnumFace.BOTTOM)) {
         EntityItem var6 = getEntityAbove(par0Hopper.getWorldObj(), par0Hopper.getXPos(), par0Hopper.getYPos() + 1.0, par0Hopper.getZPos());
         if (var6 != null) {
            return insertStackFromEntity(par0Hopper, var6);
         }
      }

      return false;
   }

   private static boolean insertStackFromInventory(Hopper par0Hopper, IInventory par1IInventory, int par2, int par3) {
      ItemStack var4 = par1IInventory.getStackInSlot(par2);
      if (var4 != null && canExtractItemFromInventory(par1IInventory, var4, par2, par3)) {
         ItemStack var5 = var4.copy();
         ItemStack var6 = insertStack(par0Hopper, par1IInventory.decrStackSize(par2, 1), -1);
         if (var6 == null || var6.stackSize == 0) {
            if (!par0Hopper.getWorldObj().isRemote && isFurnaceAbove(par0Hopper)) {
               int xp_reward = var4.getExperienceReward(1);
               if (xp_reward > 0) {
                  World world = par0Hopper.getWorldObj();
                  int x = par0Hopper.getX();
                  int y = par0Hopper.getY();
                  int z = par0Hopper.getZ();
                  ++y;
                  BlockFurnace block_furnace = (BlockFurnace)world.getBlock(x, y, z);
                  EnumDirection direction = block_furnace.getDirectionFacing(world.getBlockMetadata(x, y, z));
                  int[] coords = World.getNeighboringBlockCoords(x, y, z, direction.getFace());
                  EntityXPOrb xp_orb = new EntityXPOrb(world, (double)coords[0] + 0.5, (double)coords[1] + 0.5, (double)coords[2] + 0.5, xp_reward);
                  xp_orb.motionX *= 0.20000000298023224;
                  xp_orb.motionY *= 0.20000000298023224;
                  xp_orb.motionZ *= 0.20000000298023224;
                  par0Hopper.getWorldObj().spawnEntityInWorld(xp_orb);
               }
            }

            par1IInventory.onInventoryChanged();
            return true;
         }

         par1IInventory.setInventorySlotContents(par2, var5);
      }

      return false;
   }

   public static boolean insertStackFromEntity(IInventory par0IInventory, EntityItem par1EntityItem) {
      boolean var2 = false;
      if (par1EntityItem == null) {
         return false;
      } else {
         ItemStack var3 = par1EntityItem.getEntityItem().copy();
         ItemStack var4 = insertStack(par0IInventory, var3, -1);
         if (var4 != null && var4.stackSize != 0) {
            par1EntityItem.setEntityItemStack(var4);
         } else {
            var2 = true;
            par1EntityItem.setDead();
         }

         return var2;
      }
   }

   public static ItemStack insertStack(IInventory par0IInventory, ItemStack par1ItemStack, int par2) {
      if (par0IInventory instanceof ISidedInventory && par2 > -1) {
         ISidedInventory var6 = (ISidedInventory)par0IInventory;
         int[] var7 = var6.getAccessibleSlotsFromSide(par2);

         for(int var5 = 0; var5 < var7.length && par1ItemStack != null && par1ItemStack.stackSize > 0; ++var5) {
            par1ItemStack = func_102014_c(par0IInventory, par1ItemStack, var7[var5], par2);
         }
      } else {
         int var3 = par0IInventory.getSizeInventory();

         for(int var4 = 0; var4 < var3 && par1ItemStack != null && par1ItemStack.stackSize > 0; ++var4) {
            par1ItemStack = func_102014_c(par0IInventory, par1ItemStack, var4, par2);
         }
      }

      if (par1ItemStack != null && par1ItemStack.stackSize == 0) {
         par1ItemStack = null;
      }

      return par1ItemStack;
   }

   private static boolean canInsertItemToInventory(IInventory par0IInventory, ItemStack par1ItemStack, int par2, int par3) {
      return !par0IInventory.isItemValidForSlot(par2, par1ItemStack) ? false : !(par0IInventory instanceof ISidedInventory) || ((ISidedInventory)par0IInventory).canInsertItem(par2, par1ItemStack, par3);
   }

   private static boolean canExtractItemFromInventory(IInventory par0IInventory, ItemStack par1ItemStack, int par2, int par3) {
      if (par0IInventory instanceof TileEntityStrongbox) {
         return false;
      } else {
         return !(par0IInventory instanceof ISidedInventory) || ((ISidedInventory)par0IInventory).canExtractItem(par2, par1ItemStack, par3);
      }
   }

   private static ItemStack func_102014_c(IInventory par0IInventory, ItemStack par1ItemStack, int par2, int par3) {
      ItemStack var4 = par0IInventory.getStackInSlot(par2);
      if (canInsertItemToInventory(par0IInventory, par1ItemStack, par2, par3)) {
         boolean var5 = false;
         if (var4 == null) {
            int max = Math.min(par1ItemStack.getMaxStackSize(), par0IInventory.getInventoryStackLimit());
            if (max >= par1ItemStack.stackSize)
            {
               par0IInventory.setInventorySlotContents(par2, par1ItemStack);
               par1ItemStack = null;
            }
            else
            {
               par0IInventory.setInventorySlotContents(par2, par1ItemStack.splitStack(max));
            }
            var5 = true;
         } else if (areItemStacksEqualItem(var4, par1ItemStack)) {
            int max = Math.min(par1ItemStack.getMaxStackSize(), par0IInventory.getInventoryStackLimit());
            if (max > var4.stackSize)
            {
               int l = Math.min(par1ItemStack.stackSize, max - var4.stackSize);
               par1ItemStack.stackSize -= l;
               var4.stackSize += l;
               var5 = l > 0;
            }
         }

         if (var5) {
            if (par0IInventory instanceof TileEntityHopper) {
               ((TileEntityHopper)par0IInventory).setTransferCooldown(8);
               par0IInventory.onInventoryChanged();
            }

            par0IInventory.onInventoryChanged();
         }
      }

      return par1ItemStack;
   }

   private IInventory getOutputInventory() {
      int var1 = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());
      return getInventoryAtLocation(this.getWorldObj(), (double)(this.xCoord + Facing.offsetsXForSide[var1]), (double)(this.yCoord + Facing.offsetsYForSide[var1]), (double)(this.zCoord + Facing.offsetsZForSide[var1]));
   }

   public static IInventory getInventoryAboveHopper(Hopper par0Hopper) {
      return getInventoryAtLocation(par0Hopper.getWorldObj(), par0Hopper.getXPos(), par0Hopper.getYPos() + 1.0, par0Hopper.getZPos());
   }

   public static EntityItem getEntityAbove(World par0World, double par1, double par3, double par5) {
      List var7 = par0World.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(par1, par3 - 0.5, par5, par1 + 1.0, par3 + 1.0, par5 + 1.0), IEntitySelector.selectAnything);
      return var7.size() > 0 ? (EntityItem)var7.get(0) : null;
   }

   public static IInventory getInventoryAtLocation(World par0World, double par1, double par3, double par5) {
      IInventory var7 = null;
      int var8 = MathHelper.floor_double(par1);
      int var9 = MathHelper.floor_double(par3);
      int var10 = MathHelper.floor_double(par5);
      TileEntity var11 = par0World.getBlockTileEntity(var8, var9, var10);
      if (var11 != null && var11 instanceof IInventory) {
         var7 = (IInventory)var11;
         if (var7 instanceof TileEntityChest && !(var7 instanceof TileEntityStrongbox)) {
            int var12 = par0World.getBlockId(var8, var9, var10);
            Block var13 = Block.blocksList[var12];
            if (var13 instanceof BlockChest && !(var13 instanceof BlockStrongbox)) {
               var7 = ((BlockChest)var13).getInventory(par0World, var8, var9, var10);
            }
         }
      }

      if (var7 == null) {
         List var14 = par0World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB(par1, par3, par5, par1 + 1.0, par3 + 1.0, par5 + 1.0), IEntitySelector.selectInventories);
         if (var14 != null && var14.size() > 0) {
            var7 = (IInventory)var14.get(par0World.rand.nextInt(var14.size()));
         }
      }

      return var7;
   }

   private static boolean areItemStacksEqualItem(ItemStack par0ItemStack, ItemStack par1ItemStack) {
      return par0ItemStack.stackSize > par0ItemStack.getMaxStackSize() ? false : ItemStack.areItemStacksEqual(par0ItemStack, par1ItemStack, true, false, false, false);
   }

   public double getXPos() {
      return (double)this.xCoord;
   }

   public double getYPos() {
      return (double)this.yCoord;
   }

   public double getZPos() {
      return (double)this.zCoord;
   }

   public void setTransferCooldown(int par1) {
      this.transferCooldown = par1;
   }

   public boolean isCoolingDown() {
      return this.transferCooldown > 0;
   }

   public void destroyInventory() {
      ItemStack[] item_stacks = this.hopperItemStacks;

      for(int i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

   }

   public int getX() {
      return this.xCoord;
   }

   public int getY() {
      return this.yCoord;
   }

   public int getZ() {
      return this.zCoord;
   }
}
