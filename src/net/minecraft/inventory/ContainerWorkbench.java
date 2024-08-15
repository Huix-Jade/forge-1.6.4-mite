package net.minecraft.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemKnife;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.mite.MITEContainerCrafting;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.EnumTournamentType;

public class ContainerWorkbench extends MITEContainerCrafting {
   private int x;
   private int y;
   private int z;

   public ContainerWorkbench(EntityPlayer player, int x, int y, int z) {
      super(player);
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public int getMatrixSize() {
      return 3;
   }

   public void createSlots() {
      this.addSlotToContainer(new SlotCrafting(this.player, this.craft_matrix, this.craft_result, 0, 124, 35));

      int var6;
      int var7;
      for(var6 = 0; var6 < 3; ++var6) {
         for(var7 = 0; var7 < 3; ++var7) {
            this.addSlotToContainer(new Slot(this.craft_matrix, var7 + var6 * 3, 30 + var7 * 18, 17 + var6 * 18));
         }
      }

      for(var6 = 0; var6 < 3; ++var6) {
         for(var7 = 0; var7 < 9; ++var7) {
            this.addSlotToContainer(new Slot(this.player.inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
         }
      }

      for(var6 = 0; var6 < 9; ++var6) {
         this.addSlotToContainer(new Slot(this.player.inventory, var6, 8 + var6 * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.world.getBlockId(this.x, this.y, this.z) != Block.workbench.blockID ? false : par1EntityPlayer.getDistanceSq((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(par2);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (par2 == 0) {
            if (!this.mergeItemStack(var5, 10, 46, true)) {
               return null;
            }

            var4.onSlotChange(var5, var3);
         } else if (par2 >= 10 && par2 < 37) {
            if (!this.mergeItemStack(var5, 37, 46, false)) {
               return null;
            }
         } else if (par2 >= 37 && par2 < 46) {
            if (!this.mergeItemStack(var5, 10, 37, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(var5, 10, 46, false)) {
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

   public int getBlockMetadata() {
      return this.world.getBlockMetadata(this.x, this.y, this.z);
   }
}
