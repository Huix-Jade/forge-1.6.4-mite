package net.minecraft.util;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemCoin;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemIngot;
import net.minecraft.item.ItemMattock;
import net.minecraft.item.ItemNugget;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemReferencedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public class WeightedRandomChestContent extends WeightedRandomItem {
   public final ItemStack theItemId;
   public int min_quantity;
   public int max_quantity;

   public WeightedRandomChestContent(int item_id, int subtype, int min_quantity, int max_quantity, int weight) {
      super(weight);
      this.theItemId = new ItemStack(item_id, 1, subtype);
      this.min_quantity = min_quantity;
      this.max_quantity = max_quantity;
   }

   public WeightedRandomChestContent(ItemStack item_stack, int min_quantity, int max_quantity, int weight) {
      super(weight);
      this.theItemId = item_stack;
      this.min_quantity = min_quantity;
      this.max_quantity = max_quantity;
   }

   private static ItemStack tryAddArtifact(World world, Random random, IInventory inventory, float[] chances_of_artifact) {
      if (chances_of_artifact == null) {
         return null;
      } else {
         int artifact_type = -1;

         for(int i = 0; i < chances_of_artifact.length; ++i) {
            if (random.nextFloat() < chances_of_artifact[i]) {
               artifact_type = i;
               break;
            }
         }

         if (artifact_type < 0) {
            return null;
         } else {
            ItemStack item_stack = null;
            int i;
            if (artifact_type == 0 && world.getDayOfWorld() >= 40 && world.worldInfo.hasAchievementUnlockedOrIsNull(AchievementList.bookcase)) {
               for(i = 0; i < 10; ++i) {
                  int index = random.nextInt(9) + 1;
                  ItemStack trial = ItemReferencedBook.generateBook(index);
                  if (!world.worldInfo.hasSignatureBeenAdded(trial.getSignature())) {
                     item_stack = trial;
                     break;
                  }
               }
            }

            if (item_stack == null) {
               return null;
            } else if (item_stack.stackSize >= 1 && item_stack.stackSize <= item_stack.getMaxStackSize()) {
               for(i = 0; i < chances_of_artifact.length; ++i) {
                  chances_of_artifact[i] = 0.0F;
               }

               return item_stack;
            } else {
               Minecraft.setErrorMessage("tryAddArtifact: stackSize of " + item_stack.stackSize + " is not suitable for " + item_stack);
               return null;
            }
         }
      }
   }

   public static void generateChestContents(World world, int y, Random par0Random, WeightedRandomChestContent[] par1ArrayOfWeightedRandomChestContent, IInventory par2IInventory, int par3, float[] chances_of_artifact) {
      ItemStack unique_item_stack = null;
      if (par3 > 0) {
         unique_item_stack = tryAddArtifact(world, par0Random, par2IInventory, chances_of_artifact);
         if (unique_item_stack != null) {
            --par3;
         }
      }

      for(int var4 = 0; var4 < par3; ++var4) {
         WeightedRandomChestContent var5 = null;

         int var6;
         int min_day_of_world;
         for(var6 = 0; var6 < 4; ++var6) {
            var5 = (WeightedRandomChestContent)WeightedRandom.getRandomItem(par0Random, (WeightedRandomItem[])par1ArrayOfWeightedRandomChestContent);
            Item item = var5.theItemId.getItem();
            if (var5.theItemId.hasSignature()) {
               if (unique_item_stack == null && !var5.theItemId.hasSignatureThatHasBeenAddedToWorld(world)) {
                  unique_item_stack = var5.theItemId;
                  var5 = null;
               }
            } else if ((item instanceof ItemHoe || item instanceof ItemFishingRod) && y < 48) {
               var5 = null;
            } else {
               if (!(item instanceof ItemAxe) && !(item instanceof ItemHoe) && !(item instanceof ItemMattock)) {
                  if (!(item instanceof ItemPickaxe) && !(item instanceof ItemIngot) && !(item instanceof ItemCoin)) {
                     min_day_of_world = 0;
                  } else {
                     min_day_of_world = 20;
                  }
               } else {
                  min_day_of_world = 10;
               }

               if (world.isOverworld() && world.getDayOfWorld() < min_day_of_world) {
                  var5 = null;
               } else {
                  if (!Minecraft.isInTournamentMode() || !(item instanceof ItemIngot) && !(item instanceof ItemNugget) && !(item instanceof ItemAxe) && !(item instanceof ItemPickaxe) && !(item instanceof ItemCoin)) {
                     break;
                  }

                  var5 = null;
               }
            }
         }

         if (var5 != null) {
            ItemStack[] stacks = var5.generateChestContent(par0Random, par2IInventory);
            for (ItemStack item : stacks) {
               par2IInventory.setInventorySlotContents(par0Random.nextInt(par2IInventory.getSizeInventory()), item);
            }

//            if (item_stack.getMaxStackSize() >= var6) {
//               ItemStack var7 = item_stack.copy();
//               var7.stackSize = var6;
//               var7.applyRandomItemStackDamageForChest();
//               par2IInventory.setInventorySlotContents(par0Random.nextInt(par2IInventory.getSizeInventory()), var7);
//            } else {
//               for(min_day_of_world = 0; min_day_of_world < var6; ++min_day_of_world) {
//                  ItemStack var8 = item_stack.copy();
//                  var8.stackSize = 1;
//                  var8.applyRandomItemStackDamageForChest();
//                  par2IInventory.setInventorySlotContents(par0Random.nextInt(par2IInventory.getSizeInventory()), var8);
//               }
//            }
         }
      }

      if (unique_item_stack != null) {
         Debug.println("Adding unique to chest: " + unique_item_stack);
         unique_item_stack.addSignatureToTheWorld(world);
         par2IInventory.setInventorySlotContents(par0Random.nextInt(par2IInventory.getSizeInventory()), unique_item_stack.copy());
      }

   }

   public static void generateDispenserContents(Random par0Random, WeightedRandomChestContent[] par1ArrayOfWeightedRandomChestContent, TileEntityDispenser par2TileEntityDispenser, int par3) {
      for(int var4 = 0; var4 < par3; ++var4) {
         WeightedRandomChestContent var5 = (WeightedRandomChestContent)WeightedRandom.getRandomItem(par0Random, (WeightedRandomItem[])par1ArrayOfWeightedRandomChestContent);
         ItemStack[] stacks = var5.generateChestContent(par0Random, par2TileEntityDispenser);
         for (ItemStack item : stacks) {
            par2TileEntityDispenser.setInventorySlotContents(par0Random.nextInt(par2TileEntityDispenser.getSizeInventory()), item);
         }
      }

   }

   public static WeightedRandomChestContent[] func_92080_a(WeightedRandomChestContent[] par0ArrayOfWeightedRandomChestContent, WeightedRandomChestContent... par1ArrayOfWeightedRandomChestContent) {
      WeightedRandomChestContent[] var2 = new WeightedRandomChestContent[par0ArrayOfWeightedRandomChestContent.length + par1ArrayOfWeightedRandomChestContent.length];
      int var3 = 0;

      for(int var4 = 0; var4 < par0ArrayOfWeightedRandomChestContent.length; ++var4) {
         var2[var3++] = par0ArrayOfWeightedRandomChestContent[var4];
      }

      WeightedRandomChestContent[] var8 = par1ArrayOfWeightedRandomChestContent;
      int var5 = par1ArrayOfWeightedRandomChestContent.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         WeightedRandomChestContent var7 = var8[var6];
         var2[var3++] = var7;
      }

      return var2;
   }

   // -- Forge hooks
   /**
    * Allow a mod to submit a custom implementation that can delegate item stack generation beyond simple stack lookup
    *
    * @param random The current random for generation
    * @param newInventory The inventory being generated (do not populate it, but you can refer to it)
    * @return An array of {@link ItemStack} to put into the chest
    */
   protected ItemStack[] generateChestContent(Random random, IInventory newInventory)
   {
      return ChestGenHooks.generateStacks(random, theItemId, min_quantity, max_quantity);
   }
}
