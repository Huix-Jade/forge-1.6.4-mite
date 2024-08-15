package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.SlotFuel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.mite.Skill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public class TileEntityFurnace extends TileEntity implements ISidedInventory {
   private static final int[] slots_top = new int[]{0};
   private static final int[] slots_bottom = new int[]{2, 1};
   private static final int[] slots_sides = new int[]{1};
   public static final int INPUT = 0;
   public static final int FUEL = 1;
   public static final int OUTPUT = 2;
   public static final int HEAT_LEVEL_WOOD_AND_CHARCOAL = 1;
   public static final int HEAT_LEVEL_COAL = 2;
   public static final int HEAT_LEVEL_LAVA = 3;
   public static final int HEAT_LEVEL_BLAZE_ROD = 4;
   private ItemStack[] furnaceItemStacks = new ItemStack[3];
   public int furnaceBurnTime;
   public int heat_level = 0;
   public int currentItemBurnTime;
   public int furnaceCookTime;

   public int getSizeInventory() {
      return this.furnaceItemStacks.length;
   }

   public ItemStack getStackInSlot(int par1) {
      return this.furnaceItemStacks[par1];
   }

   public ItemStack decrStackSize(int par1, int par2) {
      if (this.furnaceItemStacks[par1] != null) {
         ItemStack var3;
         if (this.furnaceItemStacks[par1].stackSize <= par2) {
            var3 = this.furnaceItemStacks[par1];
            this.furnaceItemStacks[par1] = null;
            return var3;
         } else {
            var3 = this.furnaceItemStacks[par1].splitStack(par2);
            if (this.furnaceItemStacks[par1].stackSize == 0) {
               this.furnaceItemStacks[par1] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   public ItemStack getStackInSlotOnClosing(int par1) {
      if (this.furnaceItemStacks[par1] != null) {
         ItemStack var2 = this.furnaceItemStacks[par1];
         this.furnaceItemStacks[par1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
      this.furnaceItemStacks[par1] = par2ItemStack;
      if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
         par2ItemStack.stackSize = this.getInventoryStackLimit();
      }

   }

   public String getUnlocalizedInvName() {
      return this.getBlockType().getUnlocalizedName() + ".name";
   }

   public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
      super.readFromNBT(par1NBTTagCompound);
      NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
      this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

      for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
         NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
         byte var5 = var4.getByte("Slot");
         if (var5 >= 0 && var5 < this.furnaceItemStacks.length) {
            this.furnaceItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
         }
      }

      this.furnaceBurnTime = par1NBTTagCompound.getShort("BurnTime");
      this.furnaceCookTime = par1NBTTagCompound.getShort("CookTime");
      this.currentItemBurnTime = par1NBTTagCompound.getShort("CurrentItemBurnTime");
      this.heat_level = par1NBTTagCompound.getByte("heat_level");
   }

   public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
      super.writeToNBT(par1NBTTagCompound);
      par1NBTTagCompound.setShort("BurnTime", (short)this.furnaceBurnTime);
      par1NBTTagCompound.setShort("CookTime", (short)this.furnaceCookTime);
      par1NBTTagCompound.setShort("CurrentItemBurnTime", (short)this.currentItemBurnTime);
      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.furnaceItemStacks.length; ++var3) {
         if (this.furnaceItemStacks[var3] != null) {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("Slot", (byte)var3);
            this.furnaceItemStacks[var3].writeToNBT(var4);
            var2.appendTag(var4);
         }
      }

      par1NBTTagCompound.setTag("Items", var2);
      par1NBTTagCompound.setByte("heat_level", (byte)this.heat_level);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public int getCookProgressScaled(int par1) {
      return this.furnaceCookTime * par1 / 200;
   }

   public int getBurnTimeRemainingScaled(int par1) {
      if (this.currentItemBurnTime == 0) {
         this.currentItemBurnTime = 200;
      }

      return this.furnaceBurnTime * par1 / this.currentItemBurnTime;
   }

   public boolean isBurning() {
      return this.furnaceBurnTime > 0;
   }

   public boolean isFlooded() {
      return this.getFurnaceBlock() != null && this.worldObj.getNeighborBlockMaterial(this.xCoord, this.yCoord, this.zCoord, this.getFurnaceBlock().getDirectionFacing(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord))) == Material.water;
   }

   public boolean isSmotheredBySolidBlock() {
      if (this.getFurnaceBlock() == null) {
         return false;
      } else {
         EnumFace facing = this.getFurnaceBlock().getDirectionFacing(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)).getFace();
         World var10000 = this.worldObj;
         int[] neighbor_coords = World.getNeighboringBlockCoords(this.xCoord, this.yCoord, this.zCoord, facing);
         return this.worldObj.isBlockFaceFlatAndSolid(neighbor_coords[0], neighbor_coords[1], neighbor_coords[2], facing.getOpposite());
      }
   }

   public void updateEntity() {
      if (this.worldObj.isRemote || this.furnaceBurnTime == 1 || !this.isFlooded() && !this.isSmotheredBySolidBlock()) {
         boolean var1 = this.furnaceBurnTime > 0;
         boolean var2 = false;
         if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
         } else {
            this.heat_level = 0;
         }

         if (!this.worldObj.isRemote) {
            if (this.furnaceBurnTime == 0 && this.canSmelt(this.getFuelHeatLevel())) {
               this.currentItemBurnTime = this.furnaceBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
               if (this.furnaceBurnTime > 0) {
                  this.heat_level = this.getItemHeatLevel(this.furnaceItemStacks[1]);
                  var2 = true;
                  if (this.furnaceItemStacks[1] != null) {
                     --this.furnaceItemStacks[1].stackSize;
                     if (this.furnaceItemStacks[1].stackSize == 0) {
                        Item var3 = this.furnaceItemStacks[1].getItem().getContainerItem();
                        this.furnaceItemStacks[1] = var3 != null ? new ItemStack(var3) : null;
                     }
                  }
               }
            }

            if (this.isBurning() && this.canSmelt(this.heat_level)) {
               ++this.furnaceCookTime;
               if (this.furnaceCookTime == 200) {
                  this.furnaceCookTime = 0;
                  this.smeltItem(this.heat_level);
                  var2 = true;
               }
            } else {
               this.furnaceCookTime = 0;
            }

            if (var1 != this.furnaceBurnTime > 0) {
               var2 = true;
               BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
         }

         if (var2) {
            this.onInventoryChanged();
         }

      } else {
         if (this.furnaceBurnTime > 0) {
            if (this.isFlooded()) {
               this.worldObj.blockFX(EnumBlockFX.steam, this.xCoord, this.yCoord, this.zCoord);
            }

            BlockFurnace.updateFurnaceBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
         }

         this.furnaceBurnTime = 0;
         this.furnaceCookTime = 0;
      }
   }

   public static int getHeatLevelRequired(int item_id) {
      if (item_id == Block.oreAdamantium.blockID) {
         return 4;
      } else if (item_id == Block.oreMithril.blockID) {
         return 3;
      } else if (item_id != Block.oreCopper.blockID && item_id != Block.oreSilver.blockID && item_id != Block.oreGold.blockID && item_id != Block.oreIron.blockID) {
         if (item_id != Block.oreNetherQuartz.blockID && item_id != Block.oreEmerald.blockID && item_id != Block.oreDiamond.blockID && item_id != Block.oreRedstone.blockID) {
            if (item_id == Block.oreLapis.blockID) {
               return 2;
            } else if (item_id == Block.sandStone.blockID) {
               return 2;
            } else {
               return item_id == Block.sand.blockID ? 1 : 1;
            }
         } else {
            return 2;
         }
      } else {
         return 2;
      }
   }

   public static int[] getSkillsetsThatCanSmelt(Item item) {
      if (item instanceof ItemFood) {
         return new int[]{Skill.FOOD_PREPARATION.id};
      } else if (item == Item.clay) {
         return new int[]{Skill.MASONRY.id};
      } else {
         if (item instanceof ItemBlock) {
            ItemBlock item_block = (ItemBlock)item;
            Block block = item_block.getBlock();
            if (block == Block.sand || block instanceof BlockClay || block instanceof BlockNetherrack) {
               return new int[]{Skill.MASONRY.id};
            }

            if (block instanceof BlockOre || block instanceof BlockRedstoneOre) {
               return new int[]{Skill.MINING.id};
            }
         }

         return null;
      }
   }

   private boolean canSmelt(int heat_level) {
      if (this.furnaceItemStacks[0] == null) {
         return false;
      } else {
         BlockFurnace furnace = this.getFurnaceBlock();
         if (furnace == null || !this.acceptsLargeItems() && Slot.isLargeItem(this.getInputItemStack().getItem())) {
            return false;
         } else {
            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.getInputItemStack(), heat_level);
            if (var1 == null) {
               return false;
            } else {
               ItemStack output_item_stack = this.getOutputItemStack();
               return output_item_stack == null ? true : (!output_item_stack.isItemStackEqual(var1, true, false, false, true) ? false : (output_item_stack.stackSize < this.getInventoryStackLimit() && output_item_stack.stackSize < output_item_stack.getMaxStackSize() ? true : output_item_stack.stackSize < var1.getMaxStackSize()));
            }
         }
      }
   }

   public void smeltItem(int heat_level) {
      if (this.canSmelt(heat_level)) {
         ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.getInputItemStack(), heat_level);
         ItemStack var10000;
         if (this.furnaceItemStacks[2] == null) {
            this.furnaceItemStacks[2] = var1.copy();
         } else if (this.furnaceItemStacks[2].itemID == var1.itemID) {
            var10000 = this.furnaceItemStacks[2];
            var10000.stackSize += var1.stackSize;
         }

         byte consumption;
         if (this.getInputItemStack().itemID == Block.sand.blockID && var1.itemID == Block.sandStone.blockID) {
            consumption = 4;
         } else if (this.getInputItemStack().itemID == Block.sand.blockID && var1.itemID == Block.glass.blockID) {
            consumption = 4;
         } else {
            consumption = 1;
         }

         var10000 = this.getInputItemStack();
         var10000.stackSize -= consumption;
         if (this.getInputItemStack().getItem() == Item.clay && var1.getItem() == Item.brick) {
            int extra_converted = Math.min(this.getOutputItemStack().getMaxStackSize() - this.getOutputItemStack().stackSize, this.getInputItemStack().stackSize);
            if (extra_converted > 3) {
               extra_converted = 3;
            }

            var10000 = this.getOutputItemStack();
            var10000.stackSize += extra_converted;
            var10000 = this.getInputItemStack();
            var10000.stackSize -= extra_converted;
         }

         if (this.furnaceItemStacks[0].stackSize <= 0) {
            this.furnaceItemStacks[0] = null;
         }
      }

   }

   public int getItemBurnTime(ItemStack item_stack) {
      return item_stack == null ? 0 : item_stack.getItem().getBurnTime(item_stack);
   }

   public int getItemHeatLevel(ItemStack item_stack) {
      return item_stack == null ? 0 : item_stack.getItem().getHeatLevel(item_stack);
   }

   public boolean isItemFuel(ItemStack item_stack) {
      return this.getItemHeatLevel(item_stack) > 0;
   }

   public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
      return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
   }

   public void openChest() {
   }

   public void closeChest() {
   }

   public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
      if (par1 == 0) {
         Slot slot = new Slot(this, 0, 56, 17, this.acceptsLargeItems());
         if (!slot.isItemValid(par2ItemStack)) {
            return false;
         }

         if (!FurnaceRecipes.smelting().doesSmeltingRecipeExistFor(par2ItemStack)) {
            return false;
         }
      } else if (par1 == 1) {
         SlotFuel slot_fuel = new SlotFuel(this, 1, 56, 53, this);
         if (!slot_fuel.isItemValid(par2ItemStack)) {
            return false;
         }
      }

      return par1 == 2 ? false : (par1 == 1 ? this.isItemFuel(par2ItemStack) : true);
   }

   public int[] getAccessibleSlotsFromSide(int par1) {
      return par1 == 0 ? slots_bottom : (par1 == 1 ? slots_top : slots_sides);
   }

   public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
      return this.isItemValidForSlot(par1, par2ItemStack);
   }

   public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
      if (par3 == 0 && par1 == 1) {
         if (!(par2ItemStack.getItem() instanceof ItemBucket)) {
            return false;
         } else {
            ItemBucket bucket = (ItemBucket)par2ItemStack.getItem();
            return bucket.isEmpty();
         }
      } else {
         return true;
      }
   }

   public BlockFurnace getFurnaceBlock() {
      return (BlockFurnace)this.getBlockType();
   }

   public int getMaxHeatLevel() {
      return this.getFurnaceBlock().getMaxHeatLevel();
   }

   public boolean acceptsLargeItems() {
      return this.getFurnaceBlock().acceptsLargeItems();
   }

   public void destroyInventory() {
      ItemStack[] item_stacks = this.furnaceItemStacks;

      for(int i = 0; i < item_stacks.length; ++i) {
         item_stacks[i] = null;
      }

   }

   public ItemStack getInputItemStack() {
      return this.furnaceItemStacks[0];
   }

   public ItemStack getFuelItemStack() {
      return this.furnaceItemStacks[1];
   }

   public ItemStack getOutputItemStack() {
      return this.furnaceItemStacks[2];
   }

   public int getFuelHeatLevel() {
      return this.getItemHeatLevel(this.getFuelItemStack());
   }
}
