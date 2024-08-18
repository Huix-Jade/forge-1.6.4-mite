package net.minecraft.inventory;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBowl;
import net.minecraft.item.ItemCoin;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHatchet;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemMattock;
import net.minecraft.item.ItemRunestone;
import net.minecraft.item.ItemScythe;
import net.minecraft.item.ItemShovel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingResult;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.mite.MITEContainerCrafting;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.Curse;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class SlotCrafting extends Slot {
   private final IInventory craftMatrix;
   private EntityPlayer thePlayer;
   private int amountCrafted;
   public CraftingResult crafting_result;
   public int crafting_result_index;

   public SlotCrafting(EntityPlayer par1EntityPlayer, IInventory par2IInventory, IInventory par3IInventory, int par4, int par5, int par6) {
      super(par3IInventory, par4, par5, par6);
      this.thePlayer = par1EntityPlayer;
      this.craftMatrix = par2IInventory;
   }

   public boolean isItemValid(ItemStack par1ItemStack) {
      return false;
   }

   public ItemStack decrStackSize(int par1) {
      if (this.getHasStack()) {
         this.amountCrafted += Math.min(par1, this.getStack().stackSize);
      }

      return super.decrStackSize(par1);
   }

   protected void onCrafting(ItemStack par1ItemStack, int par2) {
      this.amountCrafted += par2;
      this.onCrafting(par1ItemStack);
   }

   protected void onCrafting(ItemStack par1ItemStack) {
      par1ItemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
      this.amountCrafted = 0;
      Item item = par1ItemStack.getItem();
      Block block = item instanceof ItemBlock ? ((ItemBlock)item).getBlock() : null;
      if (block instanceof BlockFurnace && ((BlockFurnace)block).isOven()) {
         this.thePlayer.addStat(AchievementList.buildOven, 1);
      } else if (par1ItemStack.itemID == Block.workbench.blockID) {
         Material tool_material = BlockWorkbench.getToolMaterial(par1ItemStack.getItemSubtype());
         if (tool_material.isMetal()) {
            this.thePlayer.addStat(AchievementList.betterTools, 1);
         } else {
            this.thePlayer.addStat(AchievementList.buildWorkBench, 1);
         }
      } else if (block == Block.torchWood) {
         this.thePlayer.addStat(AchievementList.buildTorches, 1);
      } else if (item != Item.pickaxeCopper && item != Item.pickaxeSilver && item != Item.pickaxeGold) {
         if (par1ItemStack.itemID == Block.furnaceIdle.blockID) {
            this.thePlayer.addStat(AchievementList.buildFurnace, 1);
         } else if (par1ItemStack.itemID == Block.furnaceObsidianIdle.blockID) {
            this.thePlayer.triggerAchievement(AchievementList.obsidianFurnace);
         } else if (par1ItemStack.itemID == Block.furnaceNetherrackIdle.blockID) {
            this.thePlayer.triggerAchievement(AchievementList.netherrackFurnace);
         } else if (!(item instanceof ItemHoe) && !(item instanceof ItemMattock)) {
            if (par1ItemStack.itemID == Item.cake.itemID) {
               this.thePlayer.addStat(AchievementList.bakeCake, 1);
            } else if (item instanceof ItemTool && item.getAsTool().isEffectiveAgainstBlock(Block.obsidian, 0)) {
               this.thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
               if (this.thePlayer.worldObj instanceof WorldServer) {
                  this.thePlayer.worldObj.worldInfo.fullfillVillageCondition(16, (WorldServer)this.thePlayer.worldObj);
               }

               if (item.getAsTool().isEffectiveAgainstBlock(Block.blockMithril, 0)) {
                  this.thePlayer.triggerAchievement(AchievementList.crystalBreaker);
               }
            } else if (item != Item.hatchetFlint && item != Item.knifeFlint) {
               if (item == Item.clubWood) {
                  this.thePlayer.addStat(AchievementList.buildClub, 1);
               } else if (item instanceof ItemAxe && !(item instanceof ItemHatchet)) {
                  this.thePlayer.addStat(AchievementList.buildAxe, 1);
               } else if (par1ItemStack.itemID != Block.enchantmentTable.blockID && par1ItemStack.itemID != Block.enchantmentTableEmerald.blockID) {
                  if (par1ItemStack.itemID == Block.bookShelf.blockID) {
                     this.thePlayer.addStat(AchievementList.bookcase, 1);
                  } else if (item instanceof ItemShovel && !(item instanceof ItemMattock)) {
                     this.thePlayer.addStat(AchievementList.buildShovel, 1);
                  } else if (item instanceof ItemScythe) {
                     this.thePlayer.addStat(AchievementList.buildScythe, 1);
                  } else if (item instanceof ItemArmor && ((ItemArmor)item).isChainMail()) {
                     this.thePlayer.addStat(AchievementList.buildChainMail, 1);
                  } else if (item instanceof ItemFishingRod) {
                     this.thePlayer.triggerAchievement(AchievementList.fishingRod);
                  } else if (item == Item.flour) {
                     this.thePlayer.triggerAchievement(AchievementList.flour);
                  } else if (item instanceof ItemBowl && (item == Item.bowlSalad || ItemBowl.isSoupOrStew(item))) {
                     this.thePlayer.triggerAchievement(AchievementList.fineDining);
                  }
               } else {
                  this.thePlayer.addStat(AchievementList.enchantments, 1);
               }
            } else {
               this.thePlayer.addStat(AchievementList.cuttingEdge, 1);
            }
         } else {
            this.thePlayer.addStat(AchievementList.buildHoe, 1);
         }
      } else {
         if (!this.thePlayer.worldObj.isRemote) {
            DedicatedServer.checkForTournamentWinner(this.thePlayer, EnumTournamentType.pickaxe);
         }

         this.thePlayer.addStat(AchievementList.buildPickaxe, 1);
      }

   }

   public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack) {
      GameRegistry.onItemCrafted(par1EntityPlayer, par2ItemStack, craftMatrix);
      int consumption = this.crafting_result.consumption;
      this.amountCrafted = par2ItemStack.stackSize;
      this.onCrafting(par2ItemStack);
      par1EntityPlayer.inventory.addItemStackToInventoryOrDropIt(par2ItemStack.copy());
      int xp_reclaimed = 0;

      for(int var3 = 0; var3 < this.craftMatrix.getSizeInventory(); ++var3) {
         ItemStack var4 = this.craftMatrix.getStackInSlot(var3);
         if (var4 != null) {
            Item item = var4.getItem();
            if (item instanceof ItemCoin) {
               ItemCoin coin = (ItemCoin) item;
                xp_reclaimed += coin.getExperienceValue();
            }

            this.craftMatrix.decrStackSize(var3, consumption);
            if (var4.getItem().hasContainerItem()) {
               ItemStack var5 = new ItemStack(var4.getItem().getContainerItem());
               Item container_item = var5.getItem();
               if (container_item.getClass() != par2ItemStack.getItem().getClass() && (!var4.getItem().doesContainerItemLeaveCraftingGrid(var4) || !this.thePlayer.inventory.addItemStackToInventory(var5))) {
                  MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, var5));
                  if (this.craftMatrix.getStackInSlot(var3) == null) {
                     this.craftMatrix.setInventorySlotContents(var3, var5);
                  } else {
                     this.thePlayer.dropPlayerItem(var5);
                  }
               }
            } else if (var4.itemID == Block.workbench.blockID) {
               this.thePlayer.inventory.addItemStackToInventoryOrDropIt(BlockWorkbench.getBlockComponent(var4.getItemSubtype()));
            }
         }
      }

      if (xp_reclaimed > 0) {
         par1EntityPlayer.addExperience(xp_reclaimed, true, false);
      }

   }

   public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
      return false;
   }

   public boolean canPlayerCraftItem(EntityPlayer player) {
      if (this.getStack() == null) {
         return false;
      } else if (player.isUpperBodyInWeb()) {
         return false;
      } else {
         Item item = this.getStack().getItem();
         if (player.worldObj.areSkillsEnabled() && !player.hasSkillsForCraftingResult(this.crafting_result) && (!item.hasQuality() || this.crafting_result.recipe instanceof RecipesArmorDyes)) {
            return false;
         } else {
            if (item instanceof ItemCoin) {
               ItemCoin coin = (ItemCoin)item;
               if (player.experience < coin.getExperienceValue() * this.getStack().stackSize) {
                  return false;
               }
            }

            if (player.openContainer.repair_fail_condition != 0) {
               return false;
            } else if (this.getContainer().crafting_result_shown_but_prevented) {
               return false;
            } else {
               if (this.getContainer() instanceof MITEContainerCrafting) {
                  MITEContainerCrafting container = (MITEContainerCrafting)this.getContainer();
                  if (container.craft_matrix.hasDamagedItem() && !container.current_crafting_result.isRepair()) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public void onSlotClicked(EntityPlayer player, int button, Container container) {
      if (this.getStack() != null) {
         if (button == 0) {
            if (!this.canPlayerCraftItem(player)) {
               return;
            }

            if (player instanceof EntityClientPlayerMP) {
               EntityClientPlayerMP entity_client_player_mp = (EntityClientPlayerMP)player;
               entity_client_player_mp.crafting_proceed = true;
               entity_client_player_mp.hasCurse(Curse.clumsiness, true);
            }
         } else if (button == 1) {
            this.tryIncrementCraftingResultIndex(player);
         }

      }
   }

   private void tryIncrementCraftingResultIndex(EntityPlayer player) {
      int num_crafting_results = this.getNumCraftingResults(player);
      if (num_crafting_results > 1) {
         Item item = this.getStack().getItem();
         if (item.hasQuality()) {
            if (this.crafting_result.quality_override != null) {
               return;
            }

            if (this.crafting_result_index + 1 <= player.getMaxCraftingQuality(this.crafting_result.getUnmodifiedDifficulty(), item, this.crafting_result.applicable_skillsets).ordinal()) {
               this.setCraftingResultIndex(this.crafting_result_index + 1, player);
            } else {
               this.setCraftingResultIndex(player.getMinCraftingQuality(item, this.crafting_result.applicable_skillsets).ordinal(), player);
            }
         } else if (this.crafting_result_index + 1 < num_crafting_results) {
            this.setCraftingResultIndex(this.crafting_result_index + 1, player);
         } else {
            this.setCraftingResultIndex(0, player);
         }

      }
   }

   protected int getMinCraftingResultIndex(EntityPlayer player) {
      if (this.crafting_result != null && this.crafting_result.item_stack != null && this.crafting_result.item_stack.getItem() != null) {
         ItemStack item_stack = this.crafting_result.item_stack;
         Item item = item_stack.getItem();
         return item.hasQuality() ? player.getMinCraftingQuality(item, this.crafting_result.applicable_skillsets).ordinal() : 0;
      } else {
         return 0;
      }
   }

   protected int getMaxCraftingResultIndex(EntityPlayer player) {
      if (this.crafting_result != null && this.crafting_result.item_stack != null && this.crafting_result.item_stack.getItem() != null) {
         ItemStack item_stack = this.crafting_result.item_stack;
         Item item = item_stack.getItem();
         if (item.hasQuality()) {
            return this.crafting_result.quality_override != null ? this.crafting_result.quality_override.ordinal() : player.getMaxCraftingQuality(this.crafting_result.getUnmodifiedDifficulty(), item, this.crafting_result.applicable_skillsets).ordinal();
         } else {
            return item instanceof ItemRunestone ? item.getNumSubtypes() - 1 : 0;
         }
      } else {
         return 0;
      }
   }

   public boolean checkCraftingResultIndex(EntityPlayer player) {
      int previous_crafting_result_index = this.crafting_result_index;
      this.setCraftingResultIndex(this.crafting_result_index, player);
      return this.crafting_result_index != previous_crafting_result_index;
   }

   private void setCraftingResultIndex(int crafting_result_index, EntityPlayer player) {
      if (this.crafting_result != null && this.getHasStack()) {
         if (this.crafting_result.quality_override == null) {
            crafting_result_index = MathHelper.clamp_int(crafting_result_index, this.getMinCraftingResultIndex(player), this.getMaxCraftingResultIndex(player));
         } else {
            crafting_result_index = this.crafting_result.quality_override.ordinal();
         }

         if (crafting_result_index != this.crafting_result_index) {
            this.crafting_result_index = crafting_result_index;
            player.resetCraftingProgress();
         }

         this.modifyStackForRightClicks(player);
      } else {
         player.clearCrafting();
      }
   }

   private void modifyStackForRightClicks(EntityPlayer player) {
      ItemStack item_stack = this.getStack();
      Item item = item_stack.getItem();
      if (item.hasQuality()) {
         if (this.crafting_result.quality_override != null) {
            this.crafting_result_index = this.crafting_result.quality_override.ordinal();
         }

         item_stack.setQuality(EnumQuality.values()[this.crafting_result_index]);
      } else if (item instanceof ItemRunestone) {
         item_stack.setItemSubtype(this.crafting_result_index);
      } else if (this.getNumCraftingResults(player) > 1) {
         Minecraft.setErrorMessage("onSlotClicked: multiple crafting results not handled for " + item);
      }

      this.updatePlayerCrafting(player);
   }

   private void updatePlayerCrafting(EntityPlayer player) {
      if (player instanceof EntityClientPlayerMP) {
         ItemStack item_stack = this.getStack();
         Item item = item_stack.getItem();
         EntityClientPlayerMP entity_client_player_mp = (EntityClientPlayerMP)player;
         entity_client_player_mp.crafting_item = item;
         float quality_adjusted_crafting_difficulty = this.crafting_result.getQualityAdjustedDifficulty(item_stack.getQuality());
         entity_client_player_mp.crafting_period = entity_client_player_mp.getCraftingPeriod(quality_adjusted_crafting_difficulty);
         entity_client_player_mp.crafting_experience_cost = item.hasQuality() && !item_stack.getQuality().isAverageOrLower() && !this.crafting_result.is_experience_cost_exempt ? player.getCraftingExperienceCost(quality_adjusted_crafting_difficulty) : 0;
      }

   }

   public int getNumCraftingResults(EntityPlayer player) {
      if (this.crafting_result != null && this.crafting_result.item_stack != null) {
         ItemStack item_stack = this.crafting_result.item_stack;
         Item item = item_stack.getItem();
         if (item == null) {
            return 0;
         } else if (item.hasQuality()) {
            return this.crafting_result.quality_override != null ? 1 : player.getMaxCraftingQuality(this.crafting_result.getUnmodifiedDifficulty(), item, this.crafting_result.applicable_skillsets).ordinal() - player.getMinCraftingQuality(item, this.crafting_result.applicable_skillsets).ordinal() + 1;
         } else {
            return item instanceof ItemRunestone ? item.getNumSubtypes() : 1;
         }
      } else {
         return 0;
      }
   }

   public void setInitialItemStack(EntityPlayer player, MITEContainerCrafting container) {
      this.crafting_result = container.current_crafting_result;
      if (this.crafting_result != null && this.crafting_result.item_stack != null && this.crafting_result.item_stack.getItem() != null) {
         ItemStack item_stack = this.crafting_result.item_stack.copy();
         Item item = item_stack.getItem();
         this.inventory.setInventorySlotContents(this.slotNumber, item_stack);
         if (item.hasQuality()) {
            if (this.crafting_result.quality_override == null) {
               this.setCraftingResultIndex(player.getMinCraftingQuality(item, this.crafting_result.applicable_skillsets).ordinal(), player);
            } else {
               this.setCraftingResultIndex(this.crafting_result.quality_override.ordinal(), player);
            }
         } else {
            this.setCraftingResultIndex(0, player);
         }

      } else {
         this.setCraftingResultIndex(0, player);
         this.inventory.setInventorySlotContents(this.slotNumber, (ItemStack)null);
      }
   }
}
