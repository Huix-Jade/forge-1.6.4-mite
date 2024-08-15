package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class ContainerBrewingStand extends Container {
   private TileEntityBrewingStand tileBrewingStand;
   private final Slot theSlot;
   private int brewTime;

   public ContainerBrewingStand(EntityPlayer player, TileEntityBrewingStand par2TileEntityBrewingStand) {
      super(player);
      this.tileBrewingStand = par2TileEntityBrewingStand;
      this.addSlotToContainer(new SlotBrewingStandPotion(player, par2TileEntityBrewingStand, 0, 56, 46));
      this.addSlotToContainer(new SlotBrewingStandPotion(player, par2TileEntityBrewingStand, 1, 79, 53));
      this.addSlotToContainer(new SlotBrewingStandPotion(player, par2TileEntityBrewingStand, 2, 102, 46));
      this.theSlot = this.addSlotToContainer(new SlotBrewingStandIngredient(this, par2TileEntityBrewingStand, 3, 79, 17));

      int var3;
      for(var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(player.inventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 9; ++var3) {
         this.addSlotToContainer(new Slot(player.inventory, var3, 8 + var3 * 18, 142));
      }

   }

   public void addCraftingToCrafters(ICrafting par1ICrafting) {
      super.addCraftingToCrafters(par1ICrafting);
      par1ICrafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int var1 = 0; var1 < this.crafters.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.crafters.get(var1);
         if (this.brewTime != this.tileBrewingStand.getBrewTime()) {
            var2.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
            if (this.brewTime == 0) {
               this.tileBrewingStand.worldObj.playSoundEffect((double)((float)this.tileBrewingStand.xCoord + 0.5F), (double)((float)this.tileBrewingStand.yCoord + 0.5F), (double)((float)this.tileBrewingStand.zCoord + 0.5F), "imported.random.boil", 0.2F);
            }
         }
      }

      this.brewTime = this.tileBrewingStand.getBrewTime();
   }

   public void updateProgressBar(int par1, int par2) {
      if (par1 == 0) {
         this.tileBrewingStand.setBrewTime(par2);
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.tileBrewingStand.isUseableByPlayer(par1EntityPlayer);
   }

   public boolean canPlayerAddIngredients() {
      return !this.player.worldObj.areSkillsEnabled() || this.player.hasSkill(Skill.BREWING);
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(par2);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if ((par2 < 0 || par2 > 2) && par2 != 3) {
            if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(var5) && this.canPlayerAddIngredients()) {
               if (!this.mergeItemStack(var5, 3, 4, false)) {
                  return null;
               }
            } else if (SlotBrewingStandPotion.canHoldPotion(var3)) {
               if (!this.mergeItemStack(var5, 0, 3, false)) {
                  return null;
               }
            } else if (par2 >= 4 && par2 < 31) {
               if (!this.mergeItemStack(var5, 31, 40, false)) {
                  return null;
               }
            } else if (par2 >= 31 && par2 < 40) {
               if (!this.mergeItemStack(var5, 4, 31, false)) {
                  return null;
               }
            } else if (!this.mergeItemStack(var5, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.mergeItemStack(var5, 4, 40, true)) {
               return null;
            }

            var4.onSlotChange(var5, var3);
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
}
