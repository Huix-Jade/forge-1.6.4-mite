package net.minecraft.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntityAnvil;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public class ContainerRepairINNER2 extends Slot {
   final World field_135071_a;
   final int field_135069_b;
   final int field_135070_c;
   final int field_135067_d;
   final ContainerRepair repairContainer;

   ContainerRepairINNER2(ContainerRepair par1ContainerRepair, IInventory par2IInventory, int par3, int par4, int par5, int par7, int par8, int par9) {
      super(par2IInventory, par3, par4, par5);
      this.repairContainer = par1ContainerRepair;
      this.field_135071_a = par1ContainerRepair.world;
      this.field_135069_b = par7;
      this.field_135070_c = par8;
      this.field_135067_d = par9;
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      return false;
   }

   public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
      return this.getHasStack();
   }

   public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack) {
      if (!this.field_135071_a.isRemote && this.repairContainer.isRepairing(true)) {
         int total_amount_repaired = this.repairContainer.getSlot(0).getStack().getItemDamage() - par2ItemStack.getItemDamage();
         int anvil_damage_from_repair = total_amount_repaired;
         Item item = par2ItemStack.getItem();
         int ratio_of_tool_to_armor = Item.mattockIron.getMaxDamage(EnumQuality.average) / Item.bootsIron.getMaxDamage(EnumQuality.average);
         int ratio_of_tool_to_bow = Item.shovelMithril.getMaxDamage(EnumQuality.average) / Item.bowMithril.getMaxDamage(EnumQuality.average);
         if (item instanceof ItemArmor) {
            anvil_damage_from_repair *= ratio_of_tool_to_armor;
         } else if (item instanceof ItemBow) {
            anvil_damage_from_repair *= ratio_of_tool_to_bow;
         } else if (item instanceof ItemFishingRod) {
            anvil_damage_from_repair *= ratio_of_tool_to_armor / 9;
         } else if (!(item instanceof ItemTool)) {
            Minecraft.setErrorMessage("Anvil damage not handled for " + item);
         }

         if (Minecraft.inDevMode()) {
            System.out.println("Causing " + anvil_damage_from_repair + " damage to anvil @ " + StringHelper.getCoordsAsString(this.field_135069_b, this.field_135070_c, this.field_135067_d));
         }

         ((TileEntityAnvil)this.field_135071_a.getBlockTileEntity(this.field_135069_b, this.field_135070_c, this.field_135067_d)).addDamage(this.field_135071_a, this.field_135069_b, this.field_135070_c, this.field_135067_d, anvil_damage_from_repair);
      }

      ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(0, (ItemStack)null);
      if (ContainerRepair.getStackSizeUsedInRepair(this.repairContainer) > 0) {
         ItemStack var3 = ContainerRepair.getRepairInputInventory(this.repairContainer).getStackInSlot(1);
         if (var3 != null && var3.stackSize > ContainerRepair.getStackSizeUsedInRepair(this.repairContainer)) {
            var3.stackSize -= ContainerRepair.getStackSizeUsedInRepair(this.repairContainer);
            ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(1, var3);
         } else {
            ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(1, (ItemStack)null);
         }
      } else {
         ContainerRepair.getRepairInputInventory(this.repairContainer).setInventorySlotContents(1, (ItemStack)null);
      }

      if (!this.field_135071_a.isRemote && this.repairContainer.play_anvil_sound_on_pickup) {
         this.field_135071_a.playAuxSFX(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
      }

   }
}
