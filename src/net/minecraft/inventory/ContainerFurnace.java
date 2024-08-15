package net.minecraft.inventory;

import net.minecraft.client.gui.inventory.SlotFuel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMeat;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumSignal;

public class ContainerFurnace extends Container {
   private TileEntityFurnace furnace;
   private int lastCookTime;
   private int lastBurnTime;
   private int lastItemBurnTime;
   private int last_sent_heat_level;
   private boolean prevent_sizzle_sound = true;
   private int sizzle_sound_cooldown;
   public static final int SLOT_INDEX_INPUT = 0;
   public static final int SLOT_INDEX_FUEL = 1;

   public ContainerFurnace(EntityPlayer player, TileEntityFurnace par2TileEntityFurnace) {
      super(player);
      this.furnace = par2TileEntityFurnace;
      this.addSlotToContainer(new Slot(par2TileEntityFurnace, 0, 56, 17, this.furnace.acceptsLargeItems()));
      this.addSlotToContainer(new SlotFuel(par2TileEntityFurnace, 1, 56, 53, this.furnace));
      this.addSlotToContainer(new SlotFurnace(player, par2TileEntityFurnace, 2, 116, 35));

      int col;
      for(int row = 0; row < 3; ++row) {
         for(col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for(col = 0; col < 9; ++col) {
         this.addSlotToContainer(new Slot(player.inventory, col, 8 + col * 18, 142));
      }

   }

   public void addCraftingToCrafters(ICrafting par1ICrafting) {
      super.addCraftingToCrafters(par1ICrafting);
      par1ICrafting.sendProgressBarUpdate(this, 0, this.furnace.furnaceCookTime);
      par1ICrafting.sendProgressBarUpdate(this, 1, this.furnace.furnaceBurnTime);
      par1ICrafting.sendProgressBarUpdate(this, 2, this.furnace.currentItemBurnTime);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int var1 = 0; var1 < this.crafters.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.crafters.get(var1);
         if (this.lastCookTime != this.furnace.furnaceCookTime) {
            var2.sendProgressBarUpdate(this, 0, this.furnace.furnaceCookTime);
         }

         if (this.lastBurnTime != this.furnace.furnaceBurnTime) {
            var2.sendProgressBarUpdate(this, 1, this.furnace.furnaceBurnTime);
         }

         if (this.lastItemBurnTime != this.furnace.currentItemBurnTime) {
            var2.sendProgressBarUpdate(this, 2, this.furnace.currentItemBurnTime);
         }

         if (this.last_sent_heat_level != this.furnace.heat_level) {
            this.player.sendPacket((new Packet85SimpleSignal(EnumSignal.furnace_heat_level)).setByte(this.furnace.heat_level));
         }
      }

      this.lastCookTime = this.furnace.furnaceCookTime;
      this.lastBurnTime = this.furnace.furnaceBurnTime;
      this.lastItemBurnTime = this.furnace.currentItemBurnTime;
      this.last_sent_heat_level = this.furnace.heat_level;
      if (this.sizzle_sound_cooldown == 0) {
         if (this.furnace.getStackInSlot(0) == null || (this.furnace.getStackInSlot(1) == null || this.furnace.getStackInSlot(2) == null) && this.furnace.furnaceBurnTime == 0) {
            this.prevent_sizzle_sound = false;
         }
      } else if (this.sizzle_sound_cooldown > 0) {
         --this.sizzle_sound_cooldown;
      }

      if (this.furnace.getStackInSlot(0) != null && this.furnace.furnaceBurnTime > 0 && this.furnace.furnaceCookTime == 1) {
         Item cooking_item = this.furnace.getStackInSlot(0).getItem();
         if (cooking_item instanceof ItemMeat && !this.prevent_sizzle_sound) {
            this.furnace.worldObj.playSoundEffect((double)((float)this.furnace.xCoord + 0.5F), (double)((float)this.furnace.yCoord + 0.5F), (double)((float)this.furnace.zCoord + 0.5F), "imported.random.sizzle");
            this.prevent_sizzle_sound = true;
            this.sizzle_sound_cooldown = 100;
         }
      }

   }

   public void updateProgressBar(int par1, int par2) {
      if (par1 == 0) {
         this.furnace.furnaceCookTime = par2;
      }

      if (par1 == 1) {
         this.furnace.furnaceBurnTime = par2;
      }

      if (par1 == 2) {
         this.furnace.currentItemBurnTime = par2;
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.furnace.isUseableByPlayer(par1EntityPlayer);
   }

   public boolean canPlayerAddItemToSmelt(Item item) {
      return this.player.worldObj.areSkillsEnabled() ? this.player.hasAnyOfTheseSkillsets(TileEntityFurnace.getSkillsetsThatCanSmelt(item)) : true;
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(par2);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (par2 == 2) {
            if (!this.mergeItemStack(var5, 3, 39, true)) {
               return null;
            }

            var4.onSlotChange(var5, var3);
         } else if (par2 != 1 && par2 != 0) {
            if (FurnaceRecipes.smelting().getSmeltingResult(var5, -1) != null && this.canPlayerAddItemToSmelt(var5.getItem())) {
               if (!this.mergeItemStack(var5, 0, 1, false)) {
                  return null;
               }
            } else if (this.furnace.isItemFuel(var5)) {
               if (!this.mergeItemStack(var5, 1, 2, false)) {
                  return null;
               }
            } else if (par2 >= 3 && par2 < 30) {
               if (!this.mergeItemStack(var5, 30, 39, false)) {
                  return null;
               }
            } else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(var5, 3, 30, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var5, 3, 39, false)) {
            return null;
         }

         if (var5.stackSize == 0) {
            var4.putStack((ItemStack)null);
         } else {
            var4.onSlotChanged();
         }

         if (var5.stackSize == var3.stackSize) {
            return null;
         }

         var4.onPickupFromSlot(par1EntityPlayer, var5);
      }

      return var3;
   }

   public TileEntityFurnace getTileEntityFurnace() {
      return this.furnace;
   }
}
