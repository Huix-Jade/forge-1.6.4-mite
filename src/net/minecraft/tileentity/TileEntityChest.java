package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStrongbox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChestType;

public class TileEntityChest extends TileEntity implements IInventory {
   private ItemStack[] chestContents = new ItemStack[36];
   public boolean adjacentChestChecked;
   public TileEntityChest adjacentChestZNeg;
   public TileEntityChest adjacentChestXPos;
   public TileEntityChest adjacentChestXNeg;
   public TileEntityChest adjacentChestZPosition;
   public float lidAngle;
   public float prevLidAngle;
   public int numUsingPlayers;
   private int ticksSinceSync;
   private EnumChestType cached_chest_type;
   private float compost;

   public TileEntityChest() {
      this.cached_chest_type = null;
   }

   public TileEntityChest(EnumChestType chest_type, Block block) {
      this.cached_chest_type = chest_type;
      this.setBlock(block);
   }

   public int getSizeInventory() {
      return 27;
   }

   public ItemStack getStackInSlot(int par1) {
      return this.chestContents[par1];
   }

   public ItemStack decrStackSize(int par1, int par2) {
      if (this.chestContents[par1] != null) {
         ItemStack var3;
         if (this.chestContents[par1].stackSize <= par2) {
            var3 = this.chestContents[par1];
            this.chestContents[par1] = null;
            this.onInventoryChanged();
            return var3;
         } else {
            var3 = this.chestContents[par1].splitStack(par2);
            if (this.chestContents[par1].stackSize == 0) {
               this.chestContents[par1] = null;
            }

            this.onInventoryChanged();
            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if (this.chestContents[par1] != null) {
         ItemStack var2 = this.chestContents[par1];
         this.chestContents[par1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.chestContents[par1] = par2ItemStack;
      if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
         par2ItemStack.stackSize = this.getInventoryStackLimit();
      }

      this.onInventoryChanged();
   }

   public String getUnlocalizedInvName() {
      return "container.chest";
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
      this.chestContents = new ItemStack[this.getSizeInventory()];

      for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
         int var5 = var4.getByte("Slot") & 255;
         if (var5 >= 0 && var5 < this.chestContents.length) {
            this.chestContents[var5] = ItemStack.loadItemStackFromNBT(var4);
         }
      }

      this.compost = par1NBTTagCompound.getFloat("compost");
   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.chestContents.length; ++var3) {
         if (this.chestContents[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte)var3);
            this.chestContents[var3].writeToNBT(var4);
            var2.appendTag(var4);
         }
      }

      par1NBTTagCompound.setTag("Items", var2);
      par1NBTTagCompound.setFloat("compost", this.compost);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
   }

   public void updateContainingBlockInfo() {
      super.updateContainingBlockInfo();
      this.adjacentChestChecked = false;
   }

   private void func_90009_a(TileEntityChest par1TileEntityChest, int par2) {
      if (par1TileEntityChest.isInvalid()) {
         this.adjacentChestChecked = false;
      } else if (this.adjacentChestChecked) {
         switch (par2) {
            case 0:
               if (this.adjacentChestZPosition != par1TileEntityChest) {
                  this.adjacentChestChecked = false;
               }
               break;
            case 1:
               if (this.adjacentChestXNeg != par1TileEntityChest) {
                  this.adjacentChestChecked = false;
               }
               break;
            case 2:
               if (this.adjacentChestZNeg != par1TileEntityChest) {
                  this.adjacentChestChecked = false;
               }
               break;
            case 3:
               if (this.adjacentChestXPos != par1TileEntityChest) {
                  this.adjacentChestChecked = false;
               }
         }
      }

   }

   public void checkForAdjacentChests() {
      if (!this.adjacentChestChecked) {
         this.adjacentChestChecked = true;
         this.adjacentChestZNeg = null;
         this.adjacentChestXPos = null;
         this.adjacentChestXNeg = null;
         this.adjacentChestZPosition = null;
         if (this.func_94044_a(this.xCoord - 1, this.yCoord, this.zCoord)) {
            this.adjacentChestXNeg = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
         }

         if (this.func_94044_a(this.xCoord + 1, this.yCoord, this.zCoord)) {
            this.adjacentChestXPos = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
         }

         if (this.func_94044_a(this.xCoord, this.yCoord, this.zCoord - 1)) {
            this.adjacentChestZNeg = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
         }

         if (this.func_94044_a(this.xCoord, this.yCoord, this.zCoord + 1)) {
            this.adjacentChestZPosition = (TileEntityChest)this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
         }

         if (this.adjacentChestZNeg != null) {
            this.adjacentChestZNeg.func_90009_a(this, 0);
         }

         if (this.adjacentChestZPosition != null) {
            this.adjacentChestZPosition.func_90009_a(this, 2);
         }

         if (this.adjacentChestXPos != null) {
            this.adjacentChestXPos.func_90009_a(this, 1);
         }

         if (this.adjacentChestXNeg != null) {
            this.adjacentChestXNeg.func_90009_a(this, 3);
         }
      }

   }

   private boolean func_94044_a(int par1, int par2, int par3) {
      Block var4 = Block.blocksList[this.worldObj.getBlockId(par1, par2, par3)];
      return var4 != null && var4 instanceof BlockChest && !(var4 instanceof BlockStrongbox) ? ((BlockChest)var4).chest_type == this.getChestType() : false;
   }

   public void updateEntity() {
      super.updateEntity();
      this.checkForAdjacentChests();
      ++this.ticksSinceSync;
      float var1;
      if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
         this.numUsingPlayers = 0;
         var1 = 5.0F;
         List var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)this.xCoord - var1), (double)((float)this.yCoord - var1), (double)((float)this.zCoord - var1), (double)((float)(this.xCoord + 1) + var1), (double)((float)(this.yCoord + 1) + var1), (double)((float)(this.zCoord + 1) + var1)));
         Iterator var3 = var2.iterator();

         label93:
         while(true) {
            IInventory var5;
            do {
               EntityPlayer var4;
               do {
                  if (!var3.hasNext()) {
                     break label93;
                  }

                  var4 = (EntityPlayer)var3.next();
               } while(!(var4.openContainer instanceof ContainerChest));

               var5 = ((ContainerChest)var4.openContainer).getLowerChestInventory();
            } while(var5 != this && (!(var5 instanceof InventoryLargeChest) || !((InventoryLargeChest)var5).isPartOfLargeChest(this)));

            ++this.numUsingPlayers;
         }
      }

      this.prevLidAngle = this.lidAngle;
      var1 = 0.1F;
      double var11;
      if (this.numUsingPlayers > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
         double var8 = (double)this.xCoord + 0.5;
         var11 = (double)this.zCoord + 0.5;
         if (this.adjacentChestZPosition != null) {
            var11 += 0.5;
         }

         if (this.adjacentChestXPos != null) {
            var8 += 0.5;
         }

         this.worldObj.playSoundEffect(var8, (double)this.yCoord + 0.5, var11, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
         float var9 = this.lidAngle;
         if (this.numUsingPlayers > 0) {
            this.lidAngle += var1;
         } else {
            this.lidAngle -= var1;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float var10 = 0.5F;
         if (this.lidAngle < var10 && var9 >= var10 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            var11 = (double)this.xCoord + 0.5;
            double var6 = (double)this.zCoord + 0.5;
            if (this.adjacentChestZPosition != null) {
               var6 += 0.5;
            }

            if (this.adjacentChestXPos != null) {
               var11 += 0.5;
            }

            this.worldObj.playSoundEffect(var11, (double)this.yCoord + 0.5, var6, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   public int getNumLiveWorms() {
      int num_live_worms = 0;
      ItemStack[] item_stacks = this.chestContents;

      for(int i = 0; i < item_stacks.length; ++i) {
         ItemStack item_stack = item_stacks[i];
         if (item_stack != null && item_stack.getItem() == Item.wormRaw) {
            num_live_worms += item_stack.stackSize;
         }
      }

      return num_live_worms;
   }

   public void checkForWormComposting() {
      int num_worms = this.getNumLiveWorms();
      if (num_worms >= 1) {
         if (this.compost >= 1.0F) {
            this.convertAsMuchCompostAsPossible();
         } else {
            for(int i = 0; i < num_worms; ++i) {
               int source_index = this.getIndexOfRandomWormFood();
               if (source_index < 0) {
                  return;
               }

               ItemStack source_item_stack = this.getStackInSlot(source_index);
               float composting_value = source_item_stack.getItem().getCompostingValue();
               int chance_in = (int)(100.0F * composting_value);
               if (this.worldObj.rand.nextInt(chance_in) == 0) {
                  int num_required_empty_slots = 0;
                  int destination_for_manure_index = this.canChestAcceptOneItem(Item.manure, true);
                  if (destination_for_manure_index < 0) {
                     ++num_required_empty_slots;
                  }

                  Item composting_remains = source_item_stack.getItem().getCompostingRemains(source_item_stack);
                  int destination_for_composting_remains_index = composting_remains == null ? -1 : this.canChestAcceptOneItem(composting_remains, true);
                  if (composting_remains != null && destination_for_composting_remains_index < 0) {
                     ++num_required_empty_slots;
                  }

                  if (source_item_stack.itemID == Block.pumpkinLantern.blockID && this.canChestAcceptOneItem(Item.pumpkinSeeds, true) < 0) {
                     ++num_required_empty_slots;
                  }

                  if (num_required_empty_slots > 0 && source_item_stack.stackSize == 1) {
                     --num_required_empty_slots;
                  }

                  if (this.getNumEmptySlots() >= num_required_empty_slots) {
                     this.decrStackSize(source_index, 1);
                     this.compost += composting_value;
                     ItemStack destination_item_stack;
                     if (composting_remains != null) {
                        if (destination_for_composting_remains_index < 0) {
                           destination_for_composting_remains_index = this.canChestAcceptOneItem(composting_remains, false);
                        }

                        destination_item_stack = this.getStackInSlot(destination_for_composting_remains_index);
                        if (destination_item_stack == null) {
                           this.setInventorySlotContents(destination_for_composting_remains_index, new ItemStack(composting_remains));
                        } else {
                           ++destination_item_stack.stackSize;
                        }
                     }

                     if (source_item_stack.itemID == Block.pumpkinLantern.blockID) {
                        destination_for_composting_remains_index = this.canChestAcceptOneItem(Item.getItem(Block.torchWood), false);
                        destination_item_stack = this.getStackInSlot(destination_for_composting_remains_index);
                        if (destination_item_stack == null) {
                           this.setInventorySlotContents(destination_for_composting_remains_index, new ItemStack(Block.torchWood));
                        } else {
                           ++destination_item_stack.stackSize;
                        }
                     }

                     this.convertAsMuchCompostAsPossible();
                  }
               }
            }

         }
      }
   }

   private void convertAsMuchCompostAsPossible() {
      int destination_for_manure_index;
      for(; this.compost >= 1.0F && (destination_for_manure_index = this.canChestAcceptOneItem(Item.manure, false)) >= 0; --this.compost) {
         ItemStack destination_item_stack = this.getStackInSlot(destination_for_manure_index);
         if (destination_item_stack == null) {
            this.setInventorySlotContents(destination_for_manure_index, new ItemStack(Item.manure));
         } else {
            ++destination_item_stack.stackSize;
         }
      }

   }

   private int canChestAcceptOneItem(Item item, boolean ignore_empty_slots) {
      int size = this.getSizeInventory();

      int i;
      ItemStack item_stack;
      for(i = 0; i < size; ++i) {
         item_stack = this.getStackInSlot(i);
         if (item_stack != null && item_stack.getItem() == item && item_stack.stackSize < item_stack.getMaxStackSize()) {
            return i;
         }
      }

      if (!ignore_empty_slots) {
         for(i = 0; i < size; ++i) {
            item_stack = this.getStackInSlot(i);
            if (item_stack == null) {
               return i;
            }
         }
      }

      return -1;
   }

   private int getIndexOfRandomWormFood() {
      List list = new ArrayList();
      int size = this.getSizeInventory();

      for(int i = 0; i < size; ++i) {
         ItemStack item_stack = this.getStackInSlot(i);
         if (item_stack != null && item_stack.canBeCompostedByWorms()) {
            list.add(i);
         }
      }

      return list.isEmpty() ? -1 : (Integer)list.get(this.worldObj.rand.nextInt(list.size()));
   }

   private int getNumEmptySlots() {
      int num_empty_slots = 0;
      int size = this.getSizeInventory();

      for(int i = 0; i < size; ++i) {
         ItemStack item_stack = this.getStackInSlot(i);
         if (item_stack == null) {
            ++num_empty_slots;
         }
      }

      return num_empty_slots;
   }

   public boolean receiveClientEvent(int par1, int par2) {
      if (par1 == 1) {
         this.numUsingPlayers = par2;
         return true;
      } else {
         return super.receiveClientEvent(par1, par2);
      }
   }

   public void openChest() {
      if (this.numUsingPlayers < 0) {
         this.numUsingPlayers = 0;
      }

      ++this.numUsingPlayers;
      this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
      this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
      this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
   }

   public void closeChest() {
      if (this.getBlockType() != null && this.getBlockType() instanceof BlockChest) {
         --this.numUsingPlayers;
         this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
         this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
         this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
      }

   }

   public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
      return true;
   }

   public void invalidate() {
      super.invalidate();
      this.updateContainingBlockInfo();
      this.checkForAdjacentChests();
   }

   public EnumChestType getChestType() {
      if (this.cached_chest_type == null) {
         if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
            return EnumChestType.normal;
         }

         this.cached_chest_type = ((BlockChest)this.getBlockType()).chest_type;
      }

      return this.cached_chest_type;
   }

   public void destroyInventory() {
      ItemStack[] item_stacks = this.chestContents;

      for(int i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

   }
}
