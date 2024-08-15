package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
   public IInventory tableInventory = new SlotEnchantmentTable(this, "Enchant", true, 1);
   private int posX;
   private int posY;
   private int posZ;
   private Random rand = new Random();
   public long nameSeed;
   public int[] enchantLevels = new int[3];

   public ContainerEnchantment(EntityPlayer player, World par2World, int par3, int par4, int par5) {
      super(player);
      this.posX = par3;
      this.posY = par4;
      this.posZ = par5;
      this.addSlotToContainer(new SlotEnchantment(this, this.tableInventory, 0, 25, 47));

      int var6;
      for(var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.addSlotToContainer(new Slot(player.inventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
         }
      }

      for(var6 = 0; var6 < 9; ++var6) {
         this.addSlotToContainer(new Slot(player.inventory, var6, 8 + var6 * 18, 142));
      }

   }

   public void addCraftingToCrafters(ICrafting par1ICrafting) {
      super.addCraftingToCrafters(par1ICrafting);
      par1ICrafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
      par1ICrafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
      par1ICrafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int var1 = 0; var1 < this.crafters.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.crafters.get(var1);
         var2.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
         var2.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
         var2.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
      }

   }

   public void updateProgressBar(int par1, int par2) {
      if (par1 >= 0 && par1 <= 2) {
         this.enchantLevels[par1] = par2;
      } else {
         super.updateProgressBar(par1, par2);
      }

   }

   private int getNumAccessibleBookshelves() {
      int num_bookshelves = 0;

      for(int dy = 0; dy <= 1 && (dy != 1 || this.world.isAirOrPassableBlock(this.posX, this.posY + 1, this.posZ, false)); ++dy) {
         boolean[][] is_block_accessible = new boolean[5][5];
         if (this.world.isAirOrPassableBlock(this.posX, this.posY + dy, this.posZ - 1, false)) {
            is_block_accessible[2][0] = true;
            if (this.world.isAirOrPassableBlock(this.posX - 1, this.posY + dy, this.posZ - 1, false)) {
               is_block_accessible[0][1] = true;
               is_block_accessible[1][0] = true;
            }

            if (this.world.isAirOrPassableBlock(this.posX + 1, this.posY + dy, this.posZ - 1, false)) {
               is_block_accessible[3][0] = true;
               is_block_accessible[4][1] = true;
            }
         }

         if (this.world.isAirOrPassableBlock(this.posX + 1, this.posY + dy, this.posZ, false)) {
            is_block_accessible[4][2] = true;
            if (this.world.isAirOrPassableBlock(this.posX + 1, this.posY + dy, this.posZ - 1, false)) {
               is_block_accessible[3][0] = true;
               is_block_accessible[4][1] = true;
            }

            if (this.world.isAirOrPassableBlock(this.posX + 1, this.posY + dy, this.posZ + 1, false)) {
               is_block_accessible[4][3] = true;
               is_block_accessible[3][4] = true;
            }
         }

         if (this.world.isAirOrPassableBlock(this.posX, this.posY + dy, this.posZ + 1, false)) {
            is_block_accessible[2][4] = true;
            if (this.world.isAirOrPassableBlock(this.posX + 1, this.posY + dy, this.posZ + 1, false)) {
               is_block_accessible[4][3] = true;
               is_block_accessible[3][4] = true;
            }

            if (this.world.isAirOrPassableBlock(this.posX - 1, this.posY + dy, this.posZ + 1, false)) {
               is_block_accessible[1][4] = true;
               is_block_accessible[0][3] = true;
            }
         }

         if (this.world.isAirOrPassableBlock(this.posX - 1, this.posY + dy, this.posZ, false)) {
            is_block_accessible[0][2] = true;
            if (this.world.isAirOrPassableBlock(this.posX - 1, this.posY + dy, this.posZ + 1, false)) {
               is_block_accessible[1][4] = true;
               is_block_accessible[0][3] = true;
            }

            if (this.world.isAirOrPassableBlock(this.posX - 1, this.posY + dy, this.posZ - 1, false)) {
               is_block_accessible[0][1] = true;
               is_block_accessible[1][0] = true;
            }
         }

         for(int dx = -2; dx <= 2; ++dx) {
            for(int dz = -2; dz <= 2; ++dz) {
               if (is_block_accessible[2 + dx][2 + dz] && this.world.getBlock(this.posX + dx, this.posY + dy, this.posZ + dz) == Block.bookShelf) {
                  ++num_bookshelves;
               }
            }
         }
      }

      return num_bookshelves;
   }

   public void onCraftMatrixChanged(IInventory par1IInventory) {
      if (par1IInventory == this.tableInventory) {
         ItemStack var2 = par1IInventory.getStackInSlot(0);
         if (var2 != null && var2.isEnchantable()) {
            this.nameSeed = this.rand.nextLong();
            if (!this.world.isRemote) {
               int num_accessible_bookshelves = this.getNumAccessibleBookshelves();

               for(int slot_index = 0; slot_index < 3; ++slot_index) {
                  this.enchantLevels[slot_index] = this.calcEnchantmentLevelsForSlot(this.rand, slot_index, num_accessible_bookshelves, var2);
               }

               this.detectAndSendChanges();
            }
         } else {
            for(int var3 = 0; var3 < 3; ++var3) {
               this.enchantLevels[var3] = 0;
            }
         }
      }

   }

   public boolean enchantItem(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = this.tableInventory.getStackInSlot(0);
      int experience_cost = Enchantment.getExperienceCost(this.enchantLevels[par2]);
      if (this.enchantLevels[par2] <= 0 || var3 == null || par1EntityPlayer.experience < experience_cost && !par1EntityPlayer.capabilities.isCreativeMode) {
         return false;
      } else {
         if (!this.world.isRemote) {
            if (ItemPotion.isBottleOfWater(var3)) {
               par1EntityPlayer.addExperience(-experience_cost);
               this.tableInventory.setInventorySlotContents(0, new ItemStack(Item.expBottle));
               return true;
            }

            if (ItemAppleGold.isUnenchantedGoldenApple(var3)) {
               par1EntityPlayer.addExperience(-experience_cost);
               this.tableInventory.setInventorySlotContents(0, new ItemStack(Item.appleGold, 1, 1));
               return true;
            }

            List var4 = EnchantmentHelper.buildEnchantmentList(this.rand, var3, this.enchantLevels[par2]);
            boolean var5 = var3.itemID == Item.book.itemID;
            if (var4 != null) {
               par1EntityPlayer.addExperience(-experience_cost);
               if (var5) {
                  var3.itemID = Item.enchantedBook.itemID;
               }

               int var6 = var5 ? this.rand.nextInt(var4.size()) : -1;

               for(int var7 = 0; var7 < var4.size(); ++var7) {
                  EnchantmentData var8 = (EnchantmentData)var4.get(var7);
                  if (!var5 || var7 == var6) {
                     if (var5) {
                        Item.enchantedBook.addEnchantment(var3, var8);
                     } else {
                        var3.addEnchantment(var8.enchantmentobj, var8.enchantmentLevel);
                     }
                  }
               }

               this.getSlot(0).onSlotChanged();
            }
         }

         return true;
      }
   }

   public void onContainerClosed(EntityPlayer par1EntityPlayer) {
      super.onContainerClosed(par1EntityPlayer);
      if (!this.world.isRemote) {
         ItemStack var2 = this.tableInventory.getStackInSlotOnClosing(0);
         if (var2 != null) {
            par1EntityPlayer.dropPlayerItem(var2);
         }
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return !(Block.blocksList[this.world.getBlockId(this.posX, this.posY, this.posZ)] instanceof BlockEnchantmentTable) ? false : par1EntityPlayer.getDistanceSq((double)this.posX + 0.5, (double)this.posY + 0.5, (double)this.posZ + 0.5) <= 64.0;
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.inventorySlots.get(par2);
      if (var4 != null && var4.getHasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (par2 == 0) {
            if (!this.mergeItemStack(var5, 1, 37, true)) {
               return null;
            }
         } else {
            if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(var5)) {
               return null;
            }

            if (var5.hasTagCompound() && var5.stackSize == 1) {
               ((Slot)this.inventorySlots.get(0)).putStack(var5.copy());
               var5.stackSize = 0;
            } else if (var5.stackSize >= 1) {
               ItemStack item_stack = new ItemStack(var5.itemID, 1, var5.getItemSubtype());
               if (var5.isItemDamaged()) {
                  item_stack.setItemDamage(var5.getItemDamage());
               }

               if (var5.getItem().hasQuality()) {
                  item_stack.setQuality(var5.getQuality());
               }

               ((Slot)this.inventorySlots.get(0)).putStack(item_stack);
               --var5.stackSize;
            }
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

   public int calcEnchantmentLevelsForSlot(Random random, int slot_index, int num_accessible_bookshelves, ItemStack item_stack) {
      Item item = item_stack.getItem();
      if (!ItemPotion.isBottleOfWater(item_stack) && !ItemAppleGold.isUnenchantedGoldenApple(item_stack)) {
         if (item.getItemEnchantability() <= 0) {
            return 0;
         } else {
            if (num_accessible_bookshelves > 24) {
               num_accessible_bookshelves = 24;
            }

            Block enchantment_table_block = this.world.getBlock(this.posX, this.posY, this.posZ);
            int enchantment_table_power = (1 + num_accessible_bookshelves) * (enchantment_table_block == Block.enchantmentTableEmerald ? 2 : 4);
            int enchantment_levels = EnchantmentHelper.getEnchantmentLevelsAlteredByItemEnchantability(enchantment_table_power, item);
            float fraction = (1.0F + (float)slot_index) / 3.0F;
            if (slot_index < 2) {
               fraction += (random.nextFloat() - 0.5F) * 0.2F;
            }

            return Math.max(Math.round((float)enchantment_levels * fraction), 1);
         }
      } else {
         return 2;
      }
   }
}
