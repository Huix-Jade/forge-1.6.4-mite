package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemIngot;
import net.minecraft.item.ItemKnife;
import net.minecraft.item.ItemStack;

public class RecipeHelper {
   public static void addRecipe(IRecipe recipe, boolean include_in_lowest_crafting_difficulty_determination) {
      if (include_in_lowest_crafting_difficulty_determination) {
         recipe.setIncludeInLowestCraftingDifficultyDetermination();
      }

      ItemStack[] recipe_items = recipe.getComponents();
      ItemStack recipe_output = recipe.getRecipeOutput();
      Item output_item = recipe_output.getItem();
      output_item.setAsCraftingProduct();
      output_item.recipes[output_item.num_recipes++] = recipe;
      float difficulty = 0.0F;

      ItemStack output_item_stack;
      float highest_durability_that_is_less_than_tool_material;
      for(int i = 0; i < recipe_items.length; ++i) {
         output_item_stack = recipe_items[i];
         if (output_item_stack != null) {
            Item item = output_item_stack.getItem();
            if (item.getHasSubtypes() && output_item_stack.getItemSubtype() != 32767) {
               output_item_stack.setAsComponentOfCraftingProduct(recipe_output);
            } else {
               item.setAsComponentOfCraftingProduct(recipe_output);
            }

            float component_difficulty = output_item_stack.getCraftingDifficultyAsComponent();
            if (component_difficulty < 0.0F) {
               highest_durability_that_is_less_than_tool_material = output_item_stack.getItem().getLowestCraftingDifficultyToProduce();
               if (highest_durability_that_is_less_than_tool_material != Float.MAX_VALUE) {
                  output_item_stack.getItem().setCraftingDifficultyAsComponent(highest_durability_that_is_less_than_tool_material);
                  component_difficulty = highest_durability_that_is_less_than_tool_material;
               } else if (item.hasMaterial(Material.rusted_iron)) {
                  Item peer = Item.getMatchingItem(item.getClass(), Material.copper);
                  if (peer != null) {
                     if (item.getMaterialForDurability() == null) {
                        Minecraft.setErrorMessage("addRecipe: getMaterialForDurability()==null for component " + item);
                     }

                     item.setCraftingDifficultyAsComponent(peer.getCraftingDifficultyAsComponent((ItemStack)null) * item.getMaterialForDurability().durability / peer.getMaterialForDurability().durability);
                     component_difficulty = item.getCraftingDifficultyAsComponent((ItemStack)null);
                  }
               }
            }

            if (component_difficulty < 0.0F) {
               Minecraft.setErrorMessage("Warning: recipe for " + recipe_output.getDisplayName() + ", component crafting difficulty not set: " + output_item_stack.getItem().getItemDisplayName(output_item_stack) + " [" + (output_item_stack.itemID - 256) + "]");
            } else {
               difficulty += component_difficulty;
            }
         }
      }

      if (difficulty < 0.0F) {
         Minecraft.setErrorMessage("addRecipe: recipe output cannot have a crafting difficulty < 0.0F");
      }

      recipe.setDifficulty(difficulty);
      if (recipe_output.stackSize < 1) {
         Minecraft.setErrorMessage("stackSize is 0 for recipe output (" + recipe_output.getDisplayName() + ")");
      } else {
         float var10000 = difficulty / (float)recipe_output.stackSize;
      }

      output_item_stack = recipe.getRecipeOutput();
      if (output_item_stack != null) {
         output_item = output_item_stack.getItem();
         if (output_item.containsMetal()) {
            if (output_item instanceof ItemIngot) {
               return;
            }

            if (output_item.getClass() == ItemKnife.class) {
               return;
            }

            if (output_item.getClass() == ItemBucket.class && hasAsComponent(recipe, ItemBucket.class)) {
               return;
            }

            recipe.setMaterialToCheckToolBenchHardnessAgainst(output_item.getHardestMetalMaterial());
         } else if (output_item.itemID == Block.planks.blockID) {
            recipe.setMaterialToCheckToolBenchHardnessAgainst(Material.wood);
         } else if (output_item.itemID == Block.workbench.blockID) {
            Material tool_material = BlockWorkbench.getToolMaterial(output_item_stack.getItemSubtype());
            if (tool_material.isMetal()) {
               Material next_strongest_material = null;
               highest_durability_that_is_less_than_tool_material = 0.0F;

               for(int i = 0; i < BlockWorkbench.tool_materials.length; ++i) {
                  Material material = BlockWorkbench.tool_materials[i];
                  if (material != Material.obsidian && material.durability < tool_material.durability && material.durability > highest_durability_that_is_less_than_tool_material) {
                     next_strongest_material = material;
                     highest_durability_that_is_less_than_tool_material = material.durability;
                  }
               }

               recipe.setMaterialToCheckToolBenchHardnessAgainst(next_strongest_material);
            }
         }
      }

   }

   public static boolean hasAsComponent(IRecipe recipe, ItemStack item_stack) {
      ItemStack[] components = recipe.getComponents();

      for(int i = 0; i < components.length; ++i) {
         if (ItemStack.areItemStacksEqual(components[i], item_stack, true, true, true, true)) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasAsComponent(IRecipe recipe, Class item_class) {
      ItemStack[] components = recipe.getComponents();

      for(int i = 0; i < components.length; ++i) {
         ItemStack item_stack = components[i];
         if (item_stack != null && item_stack.getItem().getClass() == item_class) {
            return true;
         }
      }

      return false;
   }
}
