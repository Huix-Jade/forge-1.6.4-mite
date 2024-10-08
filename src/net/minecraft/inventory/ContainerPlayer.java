package net.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemKnife;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.mite.MITEContainerCrafting;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumTournamentType;

public class ContainerPlayer extends MITEContainerCrafting {
   public ContainerPlayer(EntityPlayer player) {
      super(player);
   }

   public int getMatrixSize() {
      return 2;
   }

   public void createSlots() {
      this.addSlotToContainer(new SlotCrafting(this.player, this.craft_matrix, this.craft_result, 0, 144, 36));

      int hotbar_index;
      int x;
      for(hotbar_index = 0; hotbar_index < 2; ++hotbar_index) {
         for(x = 0; x < 2; ++x) {
            this.addSlotToContainer(new Slot(this.craft_matrix, x + hotbar_index * 2, 88 + x * 18, 26 + hotbar_index * 18));
         }
      }

      for(hotbar_index = 0; hotbar_index < 4; ++hotbar_index) {
         this.addSlotToContainer(new SlotArmor(this, this.player.inventory, this.player.inventory.getSizeInventory() - 1 - hotbar_index, 8, 8 + hotbar_index * 18, hotbar_index));
      }

      for(hotbar_index = 0; hotbar_index < 3; ++hotbar_index) {
         for(x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(this.player.inventory, x + (hotbar_index + 1) * 9, 8 + x * 18, 84 + hotbar_index * 18));
         }
      }

      for(hotbar_index = 0; hotbar_index < 9; ++hotbar_index) {
         this.addSlotToContainer(new Slot(this.player.inventory, hotbar_index, 8 + hotbar_index * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer entity_player) {
      return true;
   }

   public ItemStack transferStackInSlot(EntityPlayer entity_player, int slot_index) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(slot_index);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (slot_index == 0) {
            if (!this.mergeItemStack(var5, 9, 45, true)) {
               return null;
            }

            var4.onSlotChange(var5, var3);
         } else if (slot_index >= 1 && slot_index < 5) {
            if (!this.mergeItemStack(var5, 9, 45, false)) {
               return null;
            }
         } else if (slot_index >= 5 && slot_index < 9) {
            if (!this.mergeItemStack(var5, 9, 45, false)) {
               return null;
            }
         } else if (var3.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)var3.getItem()).armorType)).getHasStack() && !this.player.hasCurse(Curse.cannot_wear_armor, true)) {
            int var6 = 5 + ((ItemArmor)var3.getItem()).armorType;
            if (!this.mergeItemStack(var5, var6, var6 + 1, false)) {
               return null;
            }
         } else if (slot_index >= 9 && slot_index < 36) {
            if (!this.mergeItemStack(var5, 36, 45, false)) {
               return null;
            }
         } else if (slot_index >= 36 && slot_index < 45) {
            if (!this.mergeItemStack(var5, 9, 36, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var5, 9, 45, false)) {
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

         var4.onPickupFromSlot(entity_player, var5);
      }

      return var3;
   }

   public boolean isRecipeForbidden(IRecipe recipe) {
      ItemStack output = recipe.getRecipeOutput();
      if (output == null) {
         return false;
      } else {
         if (output.getItem().getClass() == ItemKnife.class) {
            if (output.getItem().containsMetal()) {
               return true;
            }
         } else if (output.getItem().itemID == Block.sandStone.blockID) {
            if (output.getItemSubtype() == 2) {
               if (DedicatedServer.tournament_type == EnumTournamentType.wonder) {
                  return true;
               }
            } else if (output.getItemSubtype() == 3 && DedicatedServer.tournament_type != EnumTournamentType.wonder) {
               return true;
            }
         }

         return super.isRecipeForbidden(recipe);
      }
   }
}
