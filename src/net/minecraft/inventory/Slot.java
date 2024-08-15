package net.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.Icon;

public class Slot {
   private final int slotIndex;
   public final IInventory inventory;
   public int slotNumber;
   public int xDisplayPosition;
   public int yDisplayPosition;
   public boolean accepts_large_items;
   public boolean locked;
   private Container container;

   public Slot(IInventory inventory, int slot_index, int display_x, int display_y) {
      this(inventory, slot_index, display_x, display_y, true);
   }

   public Slot(IInventory inventory, int slot_index, int display_x, int display_y, boolean accepts_large_items) {
      this.inventory = inventory;
      this.slotIndex = slot_index;
      this.xDisplayPosition = display_x;
      this.yDisplayPosition = display_y;
      this.accepts_large_items = accepts_large_items;
   }

   public Slot setContainer(Container container) {
      this.container = container;
      return this;
   }

   public Container getContainer() {
      return this.container;
   }

   public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack) {
      if (par1ItemStack != null && par2ItemStack != null && par1ItemStack.itemID == par2ItemStack.itemID) {
         int var3 = par2ItemStack.stackSize - par1ItemStack.stackSize;
         if (var3 > 0) {
            this.onCrafting(par1ItemStack, var3);
         }
      }

   }

   protected void onCrafting(ItemStack par1ItemStack, int par2) {
   }

   protected void onCrafting(ItemStack par1ItemStack) {
   }

   public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack) {
      this.onSlotChanged();
   }

   public static boolean isLargeItem(Item item) {
      if (item instanceof ItemBlock) {
         Block block = ((ItemBlock)item).getBlock();
         return !(block instanceof BlockTorch) && !(block instanceof BlockSapling) && !(block instanceof BlockFlower) && !(block instanceof BlockTallGrass) && !(block instanceof BlockMushroom) && !(block instanceof BlockButton) && !(block instanceof BlockLilyPad) && !(block instanceof BlockVine);
      } else {
         return item instanceof ItemDoor || item instanceof ItemBoat || item instanceof ItemBed;
      }
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return true;
      } else {
         if (this.getContainer() instanceof ContainerFurnace && this.getContainer().getSlot(0) == this) {
            ContainerFurnace container_furnace = (ContainerFurnace)this.getContainer();
            if (!container_furnace.canPlayerAddItemToSmelt(par1ItemStack.getItem())) {
               return false;
            }

            if (!FurnaceRecipes.smelting().doesSmeltingRecipeExistFor(par1ItemStack)) {
               return false;
            }
         }

         return !this.locked && (!isLargeItem(par1ItemStack.getItem()) || this.accepts_large_items);
      }
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public boolean getHasStack() {
      return this.getStack() != null;
   }

   public void putStack(ItemStack par1ItemStack) {
      this.inventory.setInventorySlotContents(this.slotIndex, par1ItemStack);
      this.onSlotChanged();
   }

   public void onSlotChanged() {
      if (this.container != null) {
         if (this.container.world != null) {
            if (this.container.world.isRemote) {
               if (this.getHasStack()) {
                  this.setLocked(true);
               }
            } else if (this.getHasStack()) {
               ((EntityPlayerMP)this.container.player).sendPacket(new Packet85SimpleSignal(EnumSignal.unlock_slots));
            }
         } else {
            System.out.println("world was null for " + this + ",  " + this.inventory);
         }
      } else {
         System.out.println("container was null for " + this + ",  " + this.inventory);
      }

      this.inventory.onInventoryChanged();
   }

   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public Icon getBackgroundIconIndex() {
      return null;
   }

   public ItemStack decrStackSize(int par1) {
      return this.inventory.decrStackSize(this.slotIndex, par1);
   }

   public boolean isSlotInInventory(IInventory par1IInventory, int par2) {
      return par1IInventory == this.inventory && par2 == this.slotIndex;
   }

   public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
      return !this.locked;
   }

   public boolean func_111238_b() {
      return true;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
      if (locked) {
         this.container.slot_was_locked_this_tick = true;
      }

   }

   public void onSlotClicked(EntityPlayer entity_player, int button, Container container) {
   }
}
