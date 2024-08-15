package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.SlotRepair;
import net.minecraft.client.gui.inventory.SlotRepairOrEnchantConsumable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityAnvil;
import org.apache.commons.lang3.StringUtils;

public class ContainerRepair extends Container {
   private IInventory outputSlot = new InventoryCraftResult();
   private IInventory inputSlots = new ContainerRepairINNER1(this, "Repair", true, 2);
   private int field_82861_i;
   private int field_82858_j;
   private int field_82859_k;
   private int stackSizeToBeUsedInRepair;
   private String repairedItemName;
   public Block block;
   public boolean play_anvil_sound_on_pickup;
   public static final int SLOT_INDEX_INPUT_1 = 0;
   public static final int SLOT_INDEX_INPUT_2 = 1;
   public static final int SLOT_INDEX_OUTPUT = 2;

   public ContainerRepair(EntityPlayer player, int par3, int par4, int par5) {
      super(player);
      this.field_82861_i = par3;
      this.field_82858_j = par4;
      this.field_82859_k = par5;
      this.block = player.worldObj.getBlock(par3, par4, par5);
      this.addSlotToContainer(new SlotRepair(this.inputSlots, 0, 27, 47));
      this.addSlotToContainer(new SlotRepairOrEnchantConsumable(this.inputSlots, 1, 76, 47, this.getBlockAnvil()));
      this.addSlotToContainer(new ContainerRepairINNER2(this, this.outputSlot, 2, 134, 47, par3, par4, par5));

      int var7;
      for(var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlotToContainer(new Slot(player.inventory, var8 + var7 * 9 + 9, 8 + var8 * 18, 84 + var7 * 18));
         }
      }

      for(var7 = 0; var7 < 9; ++var7) {
         this.addSlotToContainer(new Slot(player.inventory, var7, 8 + var7 * 18, 142));
      }

   }

   public BlockAnvil getBlockAnvil() {
      return (BlockAnvil)this.block;
   }

   public void onCraftMatrixChanged(IInventory par1IInventory) {
      super.onCraftMatrixChanged(par1IInventory);
      if (par1IInventory == this.inputSlots) {
         this.updateRepairOutput();
      }

   }

   public boolean isRepairing(boolean by_consumable) {
      this.repair_fail_condition = 0;
      ItemStack item_stack_in_first_slot = this.inputSlots.getStackInSlot(0);
      if (item_stack_in_first_slot == null) {
         this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
         return false;
      } else {
         ItemStack item_stack_in_second_slot = this.inputSlots.getStackInSlot(1);
         Item item_in_first_slot = item_stack_in_first_slot.getItem();
         Item item_in_second_slot = item_stack_in_second_slot == null ? null : item_stack_in_second_slot.getItem();
         boolean is_repairing_by_combination = ItemStack.areItemStacksEqual(item_stack_in_first_slot, item_stack_in_second_slot, true, false, true, true) && item_stack_in_first_slot.isItemDamaged() && !item_stack_in_second_slot.isItemEnchanted();
         if (is_repairing_by_combination) {
            if (!item_stack_in_first_slot.isItemEnchanted() && !item_stack_in_second_slot.isItemEnchanted() && (!item_stack_in_first_slot.isItemDamaged() || !item_stack_in_second_slot.isItemDamaged())) {
               is_repairing_by_combination = false;
            }

            Material material_for_repairs = item_in_first_slot.getMaterialForRepairs();
            if (material_for_repairs != null && material_for_repairs.isMetal() && material_for_repairs.durability > this.getBlockAnvil().getMetalType().durability) {
               this.repair_fail_condition = 2;
            }
         }

         boolean is_repairing_by_consumable = item_stack_in_first_slot.isItemDamaged() && item_stack_in_first_slot.hasRepairCost() && item_stack_in_first_slot.getRepairItem() == item_in_second_slot && this.getBlockAnvil().getIsRepairable(item_stack_in_first_slot, item_stack_in_second_slot);
         boolean is_repairing = is_repairing_by_combination || is_repairing_by_consumable;
         if (is_repairing_by_consumable) {
            Material material_for_repairs = item_in_second_slot.getMaterialForRepairs();
            if (material_for_repairs != null && material_for_repairs.isMetal() && material_for_repairs.durability > this.getBlockAnvil().getMetalType().durability) {
               this.repair_fail_condition = 2;
            }
         }

         if (is_repairing) {
            if (item_stack_in_first_slot.getItem().hasQuality() && item_stack_in_first_slot.getQuality().isHigherThan(this.player.getMaxCraftingQuality(item_in_first_slot.getLowestCraftingDifficultyToProduce(), item_in_first_slot, item_in_first_slot.getSkillsetsThatCanRepairThis()))) {
               if (this.repair_fail_condition == 0) {
                  this.repair_fail_condition = 1;
               }
            } else if (this.world.areSkillsEnabled() && !this.player.hasAnyOfTheseSkillsets(item_stack_in_first_slot.getItem().getSkillsetsThatCanRepairThis())) {
               is_repairing_by_combination = false;
               is_repairing_by_consumable = false;
               is_repairing = false;
            }
         }

         return by_consumable ? is_repairing_by_consumable : is_repairing;
      }
   }

   public void updateRepairOutput() {
      ItemStack item_stack_in_first_slot = this.inputSlots.getStackInSlot(0);
      if (item_stack_in_first_slot == null) {
         this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
      } else {
         ItemStack item_stack_in_second_slot = this.inputSlots.getStackInSlot(1);
         Item item_in_first_slot = item_stack_in_first_slot.getItem();
         Item item_in_second_slot = item_stack_in_second_slot == null ? null : item_stack_in_second_slot.getItem();
         boolean is_repairing = this.isRepairing(false);
         boolean is_repairing_by_consumable = this.isRepairing(true);
         boolean is_repairing_by_combination = is_repairing && !is_repairing_by_consumable;
         boolean is_enchanting = false;
         Map enchantments_on_item_stack_in_second_slot = null;
         if (item_stack_in_first_slot.isEnchantable() && !item_stack_in_first_slot.isItemEnchanted() && item_in_second_slot == Item.enchantedBook) {
            NBTTagList stored_enchantments = item_stack_in_second_slot.getStoredEnchantmentTagList();
            if (stored_enchantments != null && EnchantmentHelper.hasValidEnchantmentForItem(stored_enchantments, item_in_first_slot)) {
               is_enchanting = true;
               enchantments_on_item_stack_in_second_slot = EnchantmentHelper.getEnchantmentsMapFromTags(stored_enchantments);
            }
         }

         boolean is_disenchanting = item_stack_in_first_slot.isItemEnchanted() && item_stack_in_second_slot != null && item_stack_in_second_slot.getItem() == Item.bottleOfDisenchanting;
         boolean is_renaming = (!StringUtils.isBlank(this.repairedItemName) || item_stack_in_first_slot.hasDisplayName()) && !this.repairedItemName.equals(item_stack_in_first_slot.getDisplayName()) && (item_stack_in_second_slot == null || is_repairing || is_enchanting || is_disenchanting);
         this.play_anvil_sound_on_pickup = true;
         if (!is_repairing && !is_enchanting && !is_disenchanting && !is_renaming) {
            this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
         } else {
            ItemStack copy_of_item_stack_in_first_slot = item_stack_in_first_slot.copy();
            this.stackSizeToBeUsedInRepair = 0;
            int index;
            if (is_repairing) {
               if (is_repairing_by_combination) {
                  copy_of_item_stack_in_first_slot.setItemDamage(CraftingManager.getResultingDurabilityFromCombiningItems(item_stack_in_first_slot, item_stack_in_second_slot));
               } else {
                  int repair_amount;
                  if (copy_of_item_stack_in_first_slot.isChainMail()) {
                     repair_amount = Math.min(copy_of_item_stack_in_first_slot.getItemDamageForDisplay(), copy_of_item_stack_in_first_slot.getMaxDamage() * 2 / copy_of_item_stack_in_first_slot.getItemAsArmor().getRepairCost(false));
                  } else {
                     repair_amount = Math.min(copy_of_item_stack_in_first_slot.getItemDamageForDisplay(), copy_of_item_stack_in_first_slot.getMaxDamage() / copy_of_item_stack_in_first_slot.getRepairCost());
                  }

                  if (repair_amount <= 0) {
                     this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                     return;
                  }

                  int initial_repair_amount = repair_amount;

                  for(index = 0; repair_amount > 0 && repair_amount == initial_repair_amount && index < item_stack_in_second_slot.stackSize; ++index) {
                     int item_damage_after_repair = copy_of_item_stack_in_first_slot.getItemDamageForDisplay() - repair_amount;
                     copy_of_item_stack_in_first_slot.setItemDamage(item_damage_after_repair);
                     if (copy_of_item_stack_in_first_slot.isChainMail()) {
                        repair_amount = Math.min(copy_of_item_stack_in_first_slot.getItemDamageForDisplay(), copy_of_item_stack_in_first_slot.getMaxDamage() * 2 / copy_of_item_stack_in_first_slot.getItemAsArmor().getRepairCost(false));
                     } else {
                        repair_amount = Math.min(copy_of_item_stack_in_first_slot.getItemDamageForDisplay(), copy_of_item_stack_in_first_slot.getMaxDamage() / copy_of_item_stack_in_first_slot.getRepairCost());
                     }
                  }

                  this.stackSizeToBeUsedInRepair = index;
               }
            } else if (is_enchanting) {
               Map enchantments_on_copy_of_item_stack_in_first_slot = EnchantmentHelper.getEnchantmentsMap(copy_of_item_stack_in_first_slot);
               Iterator i = enchantments_on_item_stack_in_second_slot.keySet().iterator();

               while(i.hasNext()) {
                  index = (Integer)i.next();
                  Enchantment enchantment = Enchantment.enchantmentsList[index];
                  int var13 = enchantments_on_copy_of_item_stack_in_first_slot.containsKey(index) ? (Integer)enchantments_on_copy_of_item_stack_in_first_slot.get(index) : 0;
                  int var14 = (Integer)enchantments_on_item_stack_in_second_slot.get(index);
                  int var10000;
                  if (var13 == var14) {
                     ++var14;
                     var10000 = var14;
                  } else {
                     var10000 = Math.max(var14, var13);
                  }

                  var14 = var10000;
                  boolean var16 = enchantment.canEnchantItem(item_in_first_slot);
                  if (var16) {
                     if (var14 > enchantment.getNumLevels()) {
                        var14 = enchantment.getNumLevels();
                     }

                     enchantments_on_copy_of_item_stack_in_first_slot.put(index, var14);
                     int var23 = 0;
                     switch (enchantment.getWeight()) {
                        case 1:
                           var23 = 8;
                           break;
                        case 2:
                           var23 = 4;
                        case 3:
                        case 4:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        default:
                           break;
                        case 5:
                           var23 = 2;
                           break;
                        case 10:
                           var23 = 1;
                     }

                     var23 = Math.max(1, var23 / 2);
                  }
               }

               EnchantmentHelper.setEnchantments(enchantments_on_copy_of_item_stack_in_first_slot, copy_of_item_stack_in_first_slot);
            } else if (is_disenchanting) {
               copy_of_item_stack_in_first_slot.clearEnchantTagList();
            }

            if (StringUtils.isBlank(this.repairedItemName)) {
               if (item_stack_in_first_slot.hasDisplayName()) {
                  copy_of_item_stack_in_first_slot.func_135074_t();
               }
            } else if (!this.repairedItemName.equals(item_stack_in_first_slot.getDisplayName())) {
               copy_of_item_stack_in_first_slot.setItemName(this.repairedItemName);
            }

            this.outputSlot.setInventorySlotContents(0, copy_of_item_stack_in_first_slot);
            this.detectAndSendChanges();
         }
      }
   }

   public void addCraftingToCrafters(ICrafting par1ICrafting) {
      super.addCraftingToCrafters(par1ICrafting);
      par1ICrafting.sendProgressBarUpdate(this, 0, 0);
   }

   public void updateProgressBar(int par1, int par2) {
   }

   public void onContainerClosed(EntityPlayer par1EntityPlayer) {
      super.onContainerClosed(par1EntityPlayer);
      if (!this.world.isRemote) {
         for(int var2 = 0; var2 < this.inputSlots.getSizeInventory(); ++var2) {
            ItemStack var3 = this.inputSlots.getStackInSlotOnClosing(var2);
            if (var3 != null) {
               par1EntityPlayer.dropPlayerItem(var3);
            }
         }
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      if (this.world.getBlock(this.field_82861_i, this.field_82858_j, this.field_82859_k) instanceof BlockAnvil && this.world.getBlockTileEntity(this.field_82861_i, this.field_82858_j, this.field_82859_k) instanceof TileEntityAnvil) {
         return par1EntityPlayer.getDistanceSq((double)this.field_82861_i + 0.5, (double)this.field_82858_j + 0.5, (double)this.field_82859_k + 0.5) <= 64.0;
      } else {
         return false;
      }
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
         } else if (par2 != 0 && par2 != 1) {
            if (par2 >= 3 && par2 < 39) {
               if (!var5.isRepairItem() && var5.getItem() != Item.enchantedBook && var5.getItem() != Item.bottleOfDisenchanting) {
                  if (!this.mergeItemStack(var5, 0, 2, false)) {
                     return null;
                  }
               } else if (!this.mergeItemStack(var5, 1, 2, false) && !this.mergeItemStack(var5, 0, 2, false)) {
                  return null;
               }
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

   public void updateItemName(String par1Str) {
      par1Str = par1Str.trim();
      this.repairedItemName = par1Str;
      if (this.getSlot(2).getHasStack()) {
         ItemStack var2 = this.getSlot(2).getStack();
         if (StringUtils.isBlank(par1Str)) {
            var2.func_135074_t();
         } else {
            var2.setItemName(this.repairedItemName);
         }
      }

      this.updateRepairOutput();
   }

   static IInventory getRepairInputInventory(ContainerRepair par0ContainerRepair) {
      return par0ContainerRepair.inputSlots;
   }

   static int getStackSizeUsedInRepair(ContainerRepair par0ContainerRepair) {
      return par0ContainerRepair.stackSizeToBeUsedInRepair;
   }

   public void onUpdate() {
      if (!(this.player instanceof EntityOtherPlayerMP)) {
         this.updateRepairOutput();
         super.onUpdate();
      }
   }
}
