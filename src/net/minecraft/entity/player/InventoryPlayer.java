package net.minecraft.entity.player;

import net.minecraft.block.BlockGravel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDamageResult;
import net.minecraft.item.ItemShovel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory {
   public ItemStack[] mainInventory = new ItemStack[36];
   public ItemStack[] armorInventory = new ItemStack[4];
   public int currentItem;
   private ItemStack currentItemStack;
   public EntityPlayer player;
   private ItemStack itemStack;
   public boolean inventoryChanged;

   public InventoryPlayer(EntityPlayer par1EntityPlayer) {
      this.player = par1EntityPlayer;
   }

   public ItemStack getCurrentItemStack() {
      ItemStack item_stack = this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
      if (this.player.isLocalClient() && Minecraft.inDevMode()) {
         if (item_stack == null) {
            Debug.equipped_item_info = "No item equipped";
         } else if (item_stack.isItemStackDamageable()) {
            Debug.equipped_item_info = "Equipped item durability=" + item_stack.getRemainingDurability() + "/" + item_stack.getItem().getMaxDamage(item_stack);
         } else {
            Debug.equipped_item_info = "";
         }
      }

      return item_stack;
   }

   public static int getHotbarSize() {
      return 9;
   }

   public boolean canCompletelyMergeWithExistingItemStacks(ItemStack item_stack) {
      int quantity_remaining = item_stack.stackSize;

      for(int i = 0; i < this.mainInventory.length; ++i) {
         ItemStack slot_item_stack = this.mainInventory[i];
         if (ItemStack.areItemStacksEqual(slot_item_stack, item_stack, true, false, false, true)) {
            quantity_remaining -= slot_item_stack.getMaxStackSize() - slot_item_stack.stackSize;
            if (quantity_remaining <= 0) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasEmptyMainInventorySlot() {
      for(int i = 0; i < this.mainInventory.length; ++i) {
         if (this.mainInventory[i] == null) {
            return true;
         }
      }

      return false;
   }

   public int getInventorySlotContainItem(int par1, boolean include_hotbar) {
      for(int var2 = include_hotbar ? 0 : getHotbarSize(); var2 < this.mainInventory.length; ++var2) {
         if (this.mainInventory[var2] != null && this.mainInventory[var2].itemID == par1) {
            return var2;
         }
      }

      return -1;
   }

   private int getSimilarityOfItems(Item item, int item_subtype, ItemStack item_stack) {
      if (item_stack != null && item_stack.stackSize != 0) {
         if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof BlockGravel && item_stack.getItem() instanceof ItemShovel) {
            return 1;
         } else if (item.getHasSubtypes() && item_subtype != item_stack.getItemSubtype()) {
            return 0;
         } else {
            return item_stack != null && item_stack.stackSize != 0 ? (item.itemID == item_stack.itemID ? 100 : MathHelper.clamp_int(item.getSimilarityToItem(item_stack.getItem()), 0, 99)) : 0;
         }
      } else {
         return 0;
      }
   }

   public int getNextHotbarOrInventorySlotContainingMostSimilarItem(Item item, int item_subtype, int hotbar_slot_index) {
      if (item == null) {
         return -1;
      } else {
         int minimum_similarity = 1;
         int most_similar_item_slot_index = -1;
         int closest_similarity = 0;

         int i;
         int similarity;
         for(i = hotbar_slot_index; i < getHotbarSize(); ++i) {
            similarity = this.getSimilarityOfItems(item, item_subtype, this.mainInventory[i]);
            if (similarity >= minimum_similarity && similarity > closest_similarity) {
               most_similar_item_slot_index = i;
               closest_similarity = similarity;
            }
         }

         for(i = 0; i < hotbar_slot_index; ++i) {
            similarity = this.getSimilarityOfItems(item, item_subtype, this.mainInventory[i]);
            if (similarity >= minimum_similarity && similarity > closest_similarity) {
               most_similar_item_slot_index = i;
               closest_similarity = similarity;
            }
         }

         for(i = getHotbarSize(); i < this.mainInventory.length; ++i) {
            similarity = this.getSimilarityOfItems(item, item_subtype, this.mainInventory[i]);
            if (similarity >= minimum_similarity && similarity > closest_similarity) {
               most_similar_item_slot_index = i;
               closest_similarity = similarity;
            }
         }

         return most_similar_item_slot_index;
      }
   }

   public boolean trySwitchItemOrRestock(Item previous_item, int item_subtype, boolean immediately) {
      if (!this.player.worldObj.isRemote) {
         Minecraft.setErrorMessage("trySwitchItemOrRestock is only meant to be called on client");
         return false;
      } else if (previous_item == null) {
         Minecraft.setErrorMessage("trySwitchItemOrRestock: previous_item is null");
         return false;
      } else {
         PlayerControllerMP player_controller = Minecraft.theMinecraft.playerController;
         if (immediately) {
            player_controller.item_switch_or_restock_pending = false;
            int slot_index = this.getNextHotbarOrInventorySlotContainingMostSimilarItem(previous_item, item_subtype, this.currentItem);
            if (slot_index == -1) {
               return false;
            } else if (slot_index == this.currentItem) {
               return false;
            } else if (slot_index < getHotbarSize()) {
               this.currentItem = slot_index;
               ((EntityClientPlayerMP)this.player).change_rendering_for_item_equipping = true;
               player_controller.syncCurrentPlayItem();
               return true;
            } else if (!player_controller.autoStockEnabled()) {
               return false;
            } else if (this.mainInventory[this.currentItem] != null) {
               return false;
            } else {
               ItemStack item_stack = this.getStackInSlot(slot_index);
               this.setInventorySlotContents(this.currentItem, item_stack);
               this.setInventorySlotContents(slot_index, (ItemStack)null);
               player_controller.netClientHandler.addToSendQueue(new Packet5PlayerInventory(this.player.entityId, this.currentItem, item_stack));
               player_controller.netClientHandler.addToSendQueue(new Packet5PlayerInventory(this.player.entityId, slot_index, (ItemStack)null));
               player_controller.setLastUsedItem((Item)null, 0);
               return true;
            }
         } else {
            player_controller.setLastUsedItem(previous_item, item_subtype);
            player_controller.item_switch_or_restock_pending = true;
            return false;
         }
      }
   }

   public int getHotbarSlotContainArrow() {
      for(int var2 = 0; var2 < getHotbarSize(); ++var2) {
         if (this.mainInventory[var2] != null && this.mainInventory[var2].getItem() instanceof ItemArrow && this.mainInventory[var2].stackSize > 0) {
            return var2;
         }
      }

      return -1;
   }

   public int getHotbarSlotContainItem(Item item) {
      for(int i = 0; i < getHotbarSize(); ++i) {
         ItemStack item_stack = this.mainInventory[i];
         if (item_stack != null && item_stack.stackSize >= 1 && item_stack.getItem() == item) {
            return i;
         }
      }

      return -1;
   }

   public ItemArrow getReadiedArrow() {
      int slot_index = this.getHotbarSlotContainArrow();
      if (slot_index < 0) {
         return null;
      } else {
         ItemStack item_stack = this.mainInventory[slot_index];
         return item_stack == null ? null : (ItemArrow)item_stack.getItem();
      }
   }

   private int getInventorySlotContainingItemAndSubtype(int id, int subtype) {
      for(int i = 0; i < this.mainInventory.length; ++i) {
         if (this.mainInventory[i] != null && this.mainInventory[i].itemID == id && this.mainInventory[i].getItemSubtype() == subtype) {
            return i;
         }
      }

      return -1;
   }

   private int storeItemStack(ItemStack par1ItemStack) {
      int inventory_stack_limit = this.getInventoryStackLimit();

      for(int i = 0; i < this.mainInventory.length; ++i) {
         ItemStack item_stack = this.mainInventory[i];
         if (item_stack != null && item_stack.isStackable() && item_stack.stackSize < item_stack.getMaxStackSize() && item_stack.stackSize < inventory_stack_limit && ItemStack.areItemStacksEqual(item_stack, par1ItemStack, true)) {
            return i;
         }
      }

      return -1;
   }

   public int getFirstEmptyStack() {
      for(int var1 = 0; var1 < this.mainInventory.length; ++var1) {
         if (this.mainInventory[var1] == null) {
            return var1;
         }
      }

      return -1;
   }

   public void setCurrentItem(int par1, int par2, boolean par3, boolean par4) {
      boolean var5 = true;
      this.currentItemStack = this.getCurrentItemStack();
      int var7;
      if (par3) {
         var7 = this.getInventorySlotContainingItemAndSubtype(par1, par2);
      } else {
         var7 = this.getInventorySlotContainItem(par1, true);
      }

      if (var7 >= 0 && var7 < 9) {
         this.currentItem = var7;
      } else if (par4 && par1 > 0) {
         int var6 = this.getFirstEmptyStack();
         if (var6 >= 0 && var6 < 9) {
            this.currentItem = var6;
         }

         this.func_70439_a(Item.itemsList[par1], par2);
      }

   }

   public void changeCurrentItem(int par1) {
      if (par1 > 0) {
         par1 = 1;
      }

      if (par1 < 0) {
         par1 = -1;
      }

      int previous_current_item = this.currentItem;

      for(this.currentItem -= par1; this.currentItem < 0; this.currentItem += 9) {
      }

      while(this.currentItem >= 9) {
         this.currentItem -= 9;
      }

      if (this.currentItem != previous_current_item) {
         if (this.player.worldObj.isRemote) {
            Minecraft.theMinecraft.playerController.setLastUsedItem((Item)null, 0);
            Minecraft.theMinecraft.playerController.clearAutoHarvestMode();
            Minecraft.theMinecraft.playerController.clearAutoUseMode();
         }

      }
   }

   public int clearInventory(int par1, int par2) {
      int var3 = 0;

      int var4;
      ItemStack var5;
      for(var4 = 0; var4 < this.mainInventory.length; ++var4) {
         var5 = this.mainInventory[var4];
         if (var5 != null && (par1 <= -1 || var5.itemID == par1) && (par2 <= -1 || var5.getItemSubtype() == par2)) {
            var3 += var5.stackSize;
            this.mainInventory[var4] = null;
         }
      }

      for(var4 = 0; var4 < this.armorInventory.length; ++var4) {
         var5 = this.armorInventory[var4];
         if (var5 != null && (par1 <= -1 || var5.itemID == par1) && (par2 <= -1 || var5.getItemSubtype() == par2)) {
            var3 += var5.stackSize;
            this.armorInventory[var4] = null;
         }
      }

      if (this.itemStack != null) {
         if (par1 > -1 && this.itemStack.itemID != par1) {
            return var3;
         }

         if (par2 > -1 && this.itemStack.getItemSubtype() != par2) {
            return var3;
         }

         var3 += this.itemStack.stackSize;
         this.setItemStack((ItemStack)null);
      }

      return var3;
   }

   public void func_70439_a(Item par1Item, int par2) {
      if (par1Item != null) {
         if (this.currentItemStack != null && this.currentItemStack.isEnchantable() && this.getInventorySlotContainingItemAndSubtype(this.currentItemStack.itemID, this.currentItemStack.getItemDamageForDisplay()) == this.currentItem) {
            return;
         }

         int var3 = this.getInventorySlotContainingItemAndSubtype(par1Item.itemID, par2);
         if (var3 >= 0) {
            int var4 = this.mainInventory[var3].stackSize;
            this.setInventorySlotContents(var3, this.mainInventory[this.currentItem]);
            this.setInventorySlotContents(this.currentItem, new ItemStack(Item.itemsList[par1Item.itemID], var4, par2));
         } else {
            this.setInventorySlotContents(this.currentItem, new ItemStack(Item.itemsList[par1Item.itemID], 1, par2));
         }
      }

   }

   private int storePartialItemStack(ItemStack par1ItemStack) {
      if (par1ItemStack.stackSize == 0) {
         return 0;
      } else {
         int item_id = par1ItemStack.itemID;
         int stack_size = par1ItemStack.stackSize;
         int slot_index;
         if (par1ItemStack.getMaxStackSize() == 1) {
            slot_index = this.getFirstEmptyStack();
            if (slot_index < 0) {
               return stack_size;
            } else {
               if (this.mainInventory[slot_index] == null) {
                  this.setInventorySlotContents(slot_index, par1ItemStack.copy());
               }

               return 0;
            }
         } else {
            slot_index = this.storeItemStack(par1ItemStack);
            if (slot_index < 0) {
               slot_index = this.getFirstEmptyStack();
            }

            if (slot_index < 0) {
               return stack_size;
            } else if (this.mainInventory[slot_index] == null) {
               this.setInventorySlotContents(slot_index, par1ItemStack.copy());
               return 0;
            } else {
               int var5 = stack_size;
               if (stack_size > this.mainInventory[slot_index].getMaxStackSize() - this.mainInventory[slot_index].stackSize) {
                  var5 = this.mainInventory[slot_index].getMaxStackSize() - this.mainInventory[slot_index].stackSize;
               }

               if (var5 > this.getInventoryStackLimit() - this.mainInventory[slot_index].stackSize) {
                  var5 = this.getInventoryStackLimit() - this.mainInventory[slot_index].stackSize;
               }

               if (var5 == 0) {
                  if (!this.player.worldObj.isRemote) {
                     this.inventorySlotChangedOnServer(slot_index);
                  }

                  return stack_size;
               } else {
                  stack_size -= var5;
                  ItemStack var10000 = this.mainInventory[slot_index];
                  var10000.stackSize += var5;
                  this.mainInventory[slot_index].animationsToGo = 5;
                  if (!this.player.worldObj.isRemote) {
                     this.inventorySlotChangedOnServer(slot_index);
                  }

                  return stack_size;
               }
            }
         }
      }
   }

   public void decrementAnimations() {
      for(int var1 = 0; var1 < this.mainInventory.length; ++var1) {
         if (this.mainInventory[var1] != null) {
            this.mainInventory[var1].updateAnimation(this.player.worldObj, this.player, var1, this.currentItem == var1);
         }
      }

      for (int i = 0; i < this.armorInventory.length; i++)
      {
         if (this.armorInventory[i] != null)
         {
            this.armorInventory[i].getItem().onArmorTickUpdate(this.player.worldObj, this.player, this.armorInventory[i]);
         }
      }

   }

   public boolean consumeInventoryItem(int par1) {
      int var2 = this.getInventorySlotContainItem(par1, true);
      if (var2 < 0) {
         return false;
      } else {
         if (--this.mainInventory[var2].stackSize <= 0) {
            this.setInventorySlotContents(var2, (ItemStack)null);
         }

         return true;
      }
   }

   public boolean consumeArrow() {
      int var2 = this.getHotbarSlotContainArrow();
      if (var2 < 0) {
         return false;
      } else {
         if (--this.mainInventory[var2].stackSize <= 0) {
            if (this.getReadiedArrow() == null) {
               int slot_index = this.getInventorySlotContainItem(this.mainInventory[var2].itemID, false);
               if (slot_index != -1) {
                  this.setInventorySlotContents(var2, this.getStackInSlot(slot_index));
                  this.setInventorySlotContents(slot_index, (ItemStack)null);
                  return true;
               }
            }

            this.setInventorySlotContents(var2, (ItemStack)null);
         }

         return true;
      }
   }

   public boolean hasItem(int par1) {
      int var2 = this.getInventorySlotContainItem(par1, true);
      return var2 >= 0;
   }

   public void addItemStackToInventoryOrDropIt(ItemStack item_stack) {
      if (item_stack != null && item_stack.stackSize >= 1) {
         for(item_stack = item_stack.copy(); item_stack.stackSize > item_stack.getMaxStackSize(); item_stack.stackSize -= item_stack.getMaxStackSize()) {
            this.addItemStackToInventoryOrDropIt(item_stack.copy().setStackSize(item_stack.getMaxStackSize()));
         }

         if (!this.addItemStackToInventory(item_stack)) {
            this.player.dropPlayerItem(item_stack);
         }

      }
   }

   public void inventorySlotChangedOnServer(int slot_index) {
      if (this.player.worldObj.isRemote) {
         Minecraft.setErrorMessage("inventorySlotChangedOnServer is not meant to called on client");
      } else {
         this.player.sendPacket((new Packet5PlayerInventory(this.player.entityId, slot_index, this.mainInventory[slot_index])).setFullInventory());
      }

   }

   public boolean addItemStackToInventory(ItemStack par1ItemStack) {
      if (par1ItemStack == null) {
         return false;
      } else if (par1ItemStack.stackSize == 0) {
         return false;
      } else {
         try {
            int var2;
            if (par1ItemStack.isItemDamaged()) {
               var2 = this.getFirstEmptyStack();
               if (var2 >= 0) {
                  this.setInventorySlotContents(var2, ItemStack.copyItemStack(par1ItemStack));
                  this.mainInventory[var2].animationsToGo = 5;
                  par1ItemStack.stackSize = 0;
                  return true;
               } else if (this.player.capabilities.isCreativeMode) {
                  par1ItemStack.stackSize = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               do {
                  var2 = par1ItemStack.stackSize;
                  par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack);
               } while(par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < var2);

               if (par1ItemStack.stackSize == var2 && this.player.capabilities.isCreativeMode) {
                  par1ItemStack.stackSize = 0;
                  return true;
               } else {
                  return par1ItemStack.stackSize < var2;
               }
            }
         } catch (Throwable var5) {
            CrashReport var3 = CrashReport.makeCrashReport(var5, "Adding item to inventory");
            CrashReportCategory var4 = var3.makeCategory("Item being added");
            var4.addCrashSection("Item ID", par1ItemStack.itemID);
            var4.addCrashSection("Item data", par1ItemStack.getItemSubtype());
            var4.addCrashSectionCallable("Item name", new CallableItemName(this, par1ItemStack));
            throw new ReportedException(var3);
         }
      }
   }

   public ItemStack decrStackSize(int par1, int par2) {
      ItemStack[] var3 = this.mainInventory;
      if (par1 >= this.mainInventory.length) {
         var3 = this.armorInventory;
         par1 -= this.mainInventory.length;
      }

      if (var3[par1] != null) {
         ItemStack var4;
         if (var3[par1].stackSize <= par2) {
            var4 = var3[par1];
            var3[par1] = null;
            return var4;
         } else {
            var4 = var3[par1].splitStack(par2);
            if (var3[par1].stackSize == 0) {
               var3[par1] = null;
            }

            return var4;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      ItemStack[] var2 = this.mainInventory;
      if (par1 >= this.mainInventory.length) {
         var2 = this.armorInventory;
         par1 -= this.mainInventory.length;
      }

      if (var2[par1] != null) {
         ItemStack var3 = var2[par1];
         var2[par1] = null;
         return var3;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      ItemStack[] var3 = this.mainInventory;
      if (par1 >= var3.length) {
         par1 -= var3.length;
         var3 = this.armorInventory;
      }

      if (var3[par1] != par2ItemStack) {
         var3[par1] = par2ItemStack;
         if (var3 == this.mainInventory && !this.player.worldObj.isRemote) {
            this.inventorySlotChangedOnServer(par1);
         }
      }

   }

   public ItemStack getInventorySlotContents(int index) {
      return index >= this.mainInventory.length ? this.armorInventory[index - this.mainInventory.length] : this.mainInventory[index];
   }

   public NBTTagList writeToNBT(NBTTagList par1NBTTagList) {
      int var2;
      NBTTagCompound var3;
      for(var2 = 0; var2 < this.mainInventory.length; ++var2) {
         if (this.mainInventory[var2] != null) {
            var3 = new NBTTagCompound();
            var3.setByte("Slot", (byte)var2);
            this.mainInventory[var2].writeToNBT(var3);
            par1NBTTagList.appendTag(var3);
         }
      }

      for(var2 = 0; var2 < this.armorInventory.length; ++var2) {
         if (this.armorInventory[var2] != null) {
            var3 = new NBTTagCompound();
            var3.setByte("Slot", (byte)(var2 + 100));
            this.armorInventory[var2].writeToNBT(var3);
            par1NBTTagList.appendTag(var3);
         }
      }

      return par1NBTTagList;
   }

   public void readFromNBT(NBTTagList par1NBTTagList) {
      this.mainInventory = new ItemStack[36];
      this.armorInventory = new ItemStack[4];

      for(int var2 = 0; var2 < par1NBTTagList.tagCount(); ++var2) {
         NBTTagCompound var3 = (NBTTagCompound)par1NBTTagList.tagAt(var2);
         int var4 = var3.getByte("Slot") & 255;
         ItemStack var5 = ItemStack.loadItemStackFromNBT(var3);
         if (var5 != null) {
            if (var4 >= 0 && var4 < this.mainInventory.length) {
               this.mainInventory[var4] = var5;
            }

            if (var4 >= 100 && var4 < this.armorInventory.length + 100) {
               this.armorInventory[var4 - 100] = var5;
            }
         }
      }

   }

   public int getSizeInventory() {
      return this.mainInventory.length + 4;
   }

   public ItemStack getStackInSlot(int par1) {
      ItemStack[] var2 = this.mainInventory;
      if (par1 >= var2.length) {
         par1 -= var2.length;
         var2 = this.armorInventory;
      }

      return var2[par1];
   }

   public String getCustomNameOrUnlocalized() {
      return "container.inventory";
   }

   public boolean hasCustomName() {
      return false;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public ItemStack armorItemInSlot(int par1) {
      return this.armorInventory[par1];
   }

   public int getNumberOfArmorPiecesEquipped() {
      int number = 0;

      for(int i = 0; i < this.armorInventory.length; ++i) {
         if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
            ++number;
         }
      }

      return number;
   }

   public void tryDamageArmor(DamageSource damage_source, float amount, EntityDamageResult result) {
      if (this.player.onClient()) {
         Minecraft.setErrorMessage("InventoryPlayer.damageArmor: called on client?");
      }

      if (this.player.isWearingDamageableItems(true) && !this.player.inCreativeMode()) {
         if (damage_source == null || !damage_source.isUnblockable()) {
            if (result == null) {
               result = new EntityDamageResult(this.player);
            }

            int amount_remaining = (int)amount;
            if (amount_remaining < 1) {
               amount_remaining = 1;
            }

            while(amount_remaining > 0 && this.player.isWearingDamageableItems(true)) {
               int armor_index = this.player.rand.nextInt(this.armorInventory.length);
               ItemStack item_stack = this.armorInventory[armor_index];
               if (item_stack != null && item_stack.getItem() instanceof ItemArmor) {
                  int portion;
                  if (this.getNumberOfArmorPiecesEquipped() == 1) {
                     portion = amount_remaining;
                  } else {
                     portion = this.player.rand.nextInt(amount_remaining) + 1;
                  }

                  result.applyArmorDamageResult(item_stack.tryDamageItem(damage_source, portion, this.player));
                  if (item_stack.stackSize == 0) {
                     this.armorInventory[armor_index] = null;
                  }

                  amount_remaining -= portion;
               } else {
                  amount_remaining -= this.player.rand.nextInt(amount_remaining) + 1;
               }
            }

         }
      }
   }

   public boolean dropAllArmor() {
      boolean armor_was_dropped = false;

      for(int var1 = 0; var1 < this.armorInventory.length; ++var1) {
         if (this.armorInventory[var1] != null) {
            this.player.dropPlayerItemWithNoTrajectory(this.armorInventory[var1]);
            this.armorInventory[var1] = null;
            armor_was_dropped = true;
         }
      }

      return armor_was_dropped;
   }

   public void dropAllItems() {
      int var1;
      for(var1 = 0; var1 < this.mainInventory.length; ++var1) {
         if (this.mainInventory[var1] != null) {
            this.player.dropPlayerItemWithRandomChoice(this.mainInventory[var1], true);
            this.mainInventory[var1] = null;
         }
      }

      for(var1 = 0; var1 < this.armorInventory.length; ++var1) {
         if (this.armorInventory[var1] != null) {
            this.player.dropPlayerItemWithRandomChoice(this.armorInventory[var1], true);
            this.armorInventory[var1] = null;
         }
      }

      this.player.sendPacket(new Packet85SimpleSignal(EnumSignal.clear_inventory));
   }

   public void onInventoryChanged() {
      this.inventoryChanged = true;
   }

   public void setItemStack(ItemStack par1ItemStack) {
      this.itemStack = par1ItemStack;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return this.player.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this.player) <= 64.0;
   }

   public boolean hasItemStack(ItemStack par1ItemStack) {
      int i;
      for(i = 0; i < this.armorInventory.length; ++i) {
         if (ItemStack.areItemStacksEqual(this.armorInventory[i], par1ItemStack, true, false, false, true)) {
            return true;
         }
      }

      for(i = 0; i < this.mainInventory.length; ++i) {
         if (ItemStack.areItemStacksEqual(this.mainInventory[i], par1ItemStack, true, false, false, true)) {
            return true;
         }
      }

      return false;
   }

   public void openChest() {
   }

   public void closeChest() {
   }

   public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
      return true;
   }

   public void copyInventory(InventoryPlayer par1InventoryPlayer) {
      int var2;
      for(var2 = 0; var2 < this.mainInventory.length; ++var2) {
         this.mainInventory[var2] = ItemStack.copyItemStack(par1InventoryPlayer.mainInventory[var2]);
      }

      for(var2 = 0; var2 < this.armorInventory.length; ++var2) {
         this.armorInventory[var2] = ItemStack.copyItemStack(par1InventoryPlayer.armorInventory[var2]);
      }

      this.currentItem = par1InventoryPlayer.currentItem;
   }

   public void convertOneOfCurrentItem(ItemStack created_item_stack) {
      this.convertOneItem(this.currentItem, created_item_stack);
   }

   public void convertOneItem(int slot_index, ItemStack created_item_stack) {
      this.convertOneItem(slot_index, this.getInventorySlotContents(slot_index), created_item_stack);
   }

   public void convertOneItem(int slot_index, ItemStack item_stack, ItemStack created_item_stack) {
      if (this.player.worldObj.isRemote) {
         Minecraft.setErrorMessage("convertOneItem: only meant to be called on server");
      } else {
         if (item_stack.stackSize == 1 && slot_index == this.currentItem) {
            this.player.getAsEntityPlayerMP().sendTryAutoSwitchOrRestockPacket(item_stack);
         }

         if (--item_stack.stackSize <= 0 && (created_item_stack == null || !this.canCompletelyMergeWithExistingItemStacks(created_item_stack))) {
            this.setInventorySlotContents(this.currentItem, created_item_stack);
         } else {
            if (item_stack.stackSize == 0) {
               this.setInventorySlotContents(this.currentItem, (ItemStack)null);
            }

            this.addItemStackToInventoryOrDropIt(created_item_stack);
         }

      }
   }

   public ItemStack decrementSlotStackSize(int slot_index) {
      ItemStack item_stack = this.mainInventory[slot_index];
      if (item_stack != null && item_stack.stackSize >= 0 && --item_stack.stackSize <= 0) {
         this.setInventorySlotContents(slot_index, (ItemStack)null);
      }

      return this.mainInventory[slot_index];
   }

   public int getSlotIndex(ItemStack item_stack, ItemStack[] inventory) {
      for(int i = 0; i < inventory.length; ++i) {
         if (inventory[i] == item_stack) {
            return i;
         }
      }

      return -1;
   }

   public int getSlotIndex(ItemStack item_stack) {
      int i;
      for(i = 0; i < this.mainInventory.length; ++i) {
         if (this.mainInventory[i] == item_stack) {
            return i;
         }
      }

      for(i = 0; i < this.armorInventory.length; ++i) {
         if (this.armorInventory[i] == item_stack) {
            return i + this.mainInventory.length;
         }
      }

      return -1;
   }

   public void destroyInventoryItemStack(int slot_index, ItemStack[] inventory) {
      ItemStack item_stack = inventory[slot_index];
      if (item_stack != null) {
         if (inventory == this.armorInventory) {
            slot_index += this.mainInventory.length;
         }

         this.setInventorySlotContents(slot_index, (ItemStack)null);
      }
   }

   public void destroyInventoryItemStack(ItemStack item_stack) {
      if (item_stack != null) {
         int slot_index = this.getSlotIndex(item_stack, this.mainInventory);
         if (slot_index >= 0) {
            this.destroyInventoryItemStack(slot_index, this.mainInventory);
         } else {
            slot_index = this.getSlotIndex(item_stack, this.armorInventory);
            if (slot_index >= 0) {
               this.destroyInventoryItemStack(slot_index, this.armorInventory);
            }

         }
      }
   }

   public void destroyCurrentItemStack() {
      this.destroyInventoryItemStack(this.currentItem, this.mainInventory);
   }

   public int calcChecksum(int for_release_number) {
      int checksum = 0;

      int i;
      ItemStack item_stack;
      for(i = 0; i < this.mainInventory.length; ++i) {
         item_stack = this.mainInventory[i];
         if (item_stack != null) {
            checksum += item_stack.calcChecksum(for_release_number);
         }
      }

      for(i = 0; i < this.armorInventory.length; ++i) {
         item_stack = this.armorInventory[i];
         if (item_stack != null) {
            checksum += item_stack.calcChecksum(for_release_number);
         }
      }

      return checksum;
   }

   public void dropItem(int slot_index, int quantity) {
      if (slot_index >= 0 && slot_index < this.mainInventory.length) {
         ItemStack item_stack = this.decrStackSize(slot_index, quantity);
         if (!this.player.worldObj.isRemote) {
            this.player.dropPlayerItemWithRandomChoice(item_stack, false);
         }

      }
   }

   public void destroyInventory() {
      ItemStack[] item_stacks = this.mainInventory;

      int i;
      for(i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

      item_stacks = this.armorInventory;

      for(i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

   }

   public boolean isWearing(ItemStack item_stack) {
      for(int i = 0; i < this.armorInventory.length; ++i) {
         if (this.armorInventory[i] == item_stack) {
            return true;
         }
      }

      return false;
   }

   public int getNumItems(Item item) {
      int num = 0;

      int i;
      for(i = 0; i < this.mainInventory.length; ++i) {
         if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == item) {
            num += this.mainInventory[i].stackSize;
         }
      }

      for(i = 0; i < this.armorInventory.length; ++i) {
         if (this.armorInventory[i] != null && this.armorInventory[i].getItem() == item) {
            num += this.armorInventory[i].stackSize;
         }
      }

      return num;
   }

   public boolean setWornItem(int slot_index, ItemStack item_stack) {
      if (this.armorInventory[slot_index] == item_stack) {
         return false;
      } else {
         this.armorInventory[slot_index] = item_stack;
         return true;
      }
   }

   public boolean takeDamage(DamageSource damage_source, float chance_per_item, float amount, boolean include_worn_items) {
      if (this.player.onClient()) {
         Debug.setErrorMessage("takeDamage: called on client?");
         return false;
      } else {
         boolean damage_occurred = false;

         int slot_index;
         ItemStack item_stack;
         int i;
         for(slot_index = 0; slot_index < this.mainInventory.length; ++slot_index) {
            item_stack = this.mainInventory[slot_index];
            if (item_stack != null) {
               for(i = 0; i < item_stack.stackSize; ++i) {
                  if (!(this.player.rand.nextFloat() >= chance_per_item) && this.takeDamage(item_stack, damage_source, amount)) {
                     damage_occurred = true;
                  }
               }
            }
         }

         if (include_worn_items) {
            chance_per_item *= 5.0F;

            for(slot_index = 0; slot_index < this.armorInventory.length; ++slot_index) {
               item_stack = this.armorInventory[slot_index];
               if (item_stack != null) {
                  for(i = 0; i < item_stack.stackSize; ++i) {
                     if (!(this.player.rand.nextFloat() >= chance_per_item) && this.takeDamage(item_stack, damage_source, amount)) {
                        damage_occurred = true;
                     }
                  }
               }
            }
         }

         if (damage_occurred) {
            if (damage_source.isPepsinDamage()) {
               this.player.entityFX(EnumEntityFX.steam_with_hiss);
            }

            if (damage_source.isAcidDamage()) {
               this.player.entityFX(EnumEntityFX.steam_with_hiss);
            }
         }

         return damage_occurred;
      }
   }

   public boolean takeDamage(ItemStack item_stack, DamageSource damage_source, float amount) {
      if (this.player.onClient()) {
         Debug.setErrorMessage("takeDamage: called on client?");
         return false;
      } else if (this.player.inCreativeMode()) {
         return false;
      } else {
         if (damage_source.isPepsinDamage()) {
            if (!item_stack.isHarmedByPepsin()) {
               return false;
            }
         } else if (damage_source.isAcidDamage() && !item_stack.isHarmedByAcid()) {
            return false;
         }

         int slot_index = this.getSlotIndex(item_stack);
         if (slot_index < 0) {
            Debug.setErrorMessage("takeDamage: item_stack not found in either main or armor inventory " + item_stack);
            return false;
         } else if (item_stack.isItemStackDamageable()) {
            ItemDamageResult idr = item_stack.tryDamageItem(damage_source, item_stack.getScaledDamage(amount), this.player);
            if (idr == null) {
               return false;
            } else {
               if (idr.itemWasDestroyed()) {
                  this.setInventorySlotContents(slot_index, (ItemStack)null);
               }

               return true;
            }
         } else if (this.player.rand.nextFloat() * 10.0F < amount) {
            if (--item_stack.stackSize < 1) {
               this.setInventorySlotContents(slot_index, (ItemStack)null);
               this.player.worldObj.tryRemoveFromWorldUniques(item_stack);
            }

            if (item_stack.getItem().hasContainerItem()) {
               Item container = item_stack.getItem().getContainerItem();
               if (!container.isHarmedBy(damage_source)) {
                  this.addItemStackToInventoryOrDropIt(new ItemStack(container));
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean convertAllItemsInSlot(int slot_index, Item item) {
      ItemStack existing_stack = this.getInventorySlotContents(slot_index);
      if (existing_stack != null && existing_stack.getItem() != item) {
         ItemStack item_stack;
         ItemStack created_item_stack;
         for(item_stack = new ItemStack(item, existing_stack.stackSize); item_stack.stackSize > item_stack.getMaxStackSize(); item_stack.stackSize -= created_item_stack.stackSize) {
            created_item_stack = new ItemStack(item);
            created_item_stack.stackSize = !this.canCompletelyMergeWithExistingItemStacks(created_item_stack) && !this.hasEmptyMainInventorySlot() ? Math.min(item_stack.stackSize - item_stack.getMaxStackSize(), created_item_stack.getMaxStackSize()) : 1;
            this.addItemStackToInventoryOrDropIt(created_item_stack);
         }

         this.setInventorySlotContents(slot_index, item_stack);
         return true;
      } else {
         return false;
      }
   }
}
