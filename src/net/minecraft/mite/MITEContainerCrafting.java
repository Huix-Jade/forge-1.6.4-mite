package net.minecraft.mite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemCoin;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.CraftingResult;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet90BroadcastToAssociatedPlayers;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumSignal;

public abstract class MITEContainerCrafting extends Container {
   public InventoryCrafting craft_matrix = new InventoryCrafting(this, this.getMatrixSize(), this.getMatrixSize());
   public IInventory craft_result = new InventoryCraftResult();
   public CraftingResult current_crafting_result;
   private CraftingResult previous_crafting_result;

   public MITEContainerCrafting(EntityPlayer player) {
      super(player);
      this.createSlots();
      this.onCraftMatrixChanged(this.craft_matrix);
   }

   public abstract int getMatrixSize();

   public abstract void createSlots();

   private SlotCrafting getCraftingSlot() {
      return (SlotCrafting)this.getSlot(0);
   }

   public void onCraftMatrixChanged(IInventory par1IInventory) {
      this.current_crafting_result = CraftingManager.getInstance().findMatchingRecipe(this.craft_matrix, this.player.worldObj, this.player);
      if (!CraftingResult.haveEquivalentItemStacks(this.current_crafting_result, this.previous_crafting_result)) {
         this.player.clearCrafting();
         this.getCraftingSlot().setInitialItemStack(this.player, this);
      }

      this.previous_crafting_result = this.current_crafting_result;
   }

   public void onContainerClosed(EntityPlayer entity_player) {
      this.player.clearCrafting();
      super.onContainerClosed(entity_player);
      if (!this.world.isRemote) {
         for(int var2 = 0; var2 < this.craft_matrix.getSizeInventory(); ++var2) {
            ItemStack var3 = this.craft_matrix.getStackInSlotOnClosing(var2);
            if (var3 != null) {
               entity_player.dropPlayerItem(var3);
            }
         }
      }

      this.craft_result.setInventorySlotContents(0, (ItemStack)null);
   }

   public abstract boolean canInteractWith(EntityPlayer var1);

   public abstract ItemStack transferStackInSlot(EntityPlayer var1, int var2);

   public boolean func_94530_a(ItemStack item_stack, Slot slot) {
      return slot.inventory != this.craft_result && super.func_94530_a(item_stack, slot);
   }

   public void onUpdate() {
      if (!(this.player instanceof EntityOtherPlayerMP)) {
         this.onCraftMatrixChanged((IInventory)null);
         SlotCrafting crafting_slot = this.getCraftingSlot();
         if (crafting_slot.checkCraftingResultIndex(this.player)) {
            this.player.clearCrafting();
         }

         if (this.player instanceof EntityClientPlayerMP) {
            this.player.getAsEntityClientPlayerMP().crafting_experience_cost_tentative = 0;
         }

         if (!crafting_slot.canPlayerCraftItem(this.player)) {
            if (this.player instanceof EntityClientPlayerMP && this.crafting_result_shown_but_prevented) {
               this.player.getAsEntityClientPlayerMP().crafting_experience_cost_tentative = this.player.getAsEntityClientPlayerMP().crafting_experience_cost;
            }

            this.player.clearCrafting();
         }

         if (this.player.worldObj.isRemote) {
            EntityClientPlayerMP player = (EntityClientPlayerMP)this.player;
            ItemStack item_stack;
            if (player.crafting_proceed && player.hasFoodEnergy()) {
               item_stack = crafting_slot.getStack();
               if (item_stack.getItem().hasCraftingEffect() && player.ticksExisted % 5 == 0 && player.rand.nextInt(5) == 0) {
                  player.sendPacket(new Packet90BroadcastToAssociatedPlayers((new Packet85SimpleSignal(EnumSignal.entity_fx, EnumEntityFX.crafting)).setEntityID(player.entityId).setShort(item_stack.itemID), false));
               }
            }

            if (player.crafting_proceed && player.hasFoodEnergy() && ++player.crafting_ticks >= player.crafting_period) {
               item_stack = crafting_slot.getStack();
               int crafting_experience_cost = player.crafting_experience_cost;
               this.recordSlotStackSizes();
               crafting_slot.onPickupFromSlot(player, item_stack);
               this.lockSlotsThatChanged();
               Minecraft.theMinecraft.thePlayer.sendQueue.addToSendQueue((new Packet85SimpleSignal(EnumSignal.crafting_completed)).setInteger(crafting_experience_cost));
               if (crafting_experience_cost > 0) {
                  player.crafting_proceed = false;
               }

               player.crafting_ticks = 0;
            }
         }

         super.onUpdate();
      }
   }

   public boolean isRecipeForbidden(IRecipe recipe) {
      return recipe.getRecipeOutput().getItem() instanceof ItemCoin && !this.world.areCoinsEnabled();
   }

   public boolean isCraftingResultForbidden(CraftingResult crafting_result) {
      ItemStack item_stack = crafting_result.item_stack;
      return ItemMap.isBeingExtended(item_stack) && !ItemMap.isAnotherMapIdAvailable(this.world);
   }

   public IRecipe getRecipe() {
      return this.current_crafting_result == null ? null : this.current_crafting_result.recipe;
   }
}
