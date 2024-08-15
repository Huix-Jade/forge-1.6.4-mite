package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesDyes {
   public void addRecipes(CraftingManager par1CraftingManager) {
      int var2;
      for(var2 = 0; var2 < 16; ++var2) {
         par1CraftingManager.addShapelessRecipe(new ItemStack(Block.cloth, 1, BlockColored.getDyeFromBlock(var2)), new ItemStack(Item.dyePowder, 1, var2), new ItemStack(Item.itemsList[Block.cloth.blockID], 1, 0)).setSkillset(Skill.FINE_ARTS.id);
         par1CraftingManager.addRecipe(new ItemStack(Block.stainedClay, 8, BlockColored.getDyeFromBlock(var2)), "###", "#X#", "###", '#', new ItemStack(Block.hardenedClay), 'X', new ItemStack(Item.dyePowder, 1, var2)).setSkillset(Skill.FINE_ARTS.id);
      }

      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 11), Block.plantYellow).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 1), Block.plantRed).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 12), new ItemStack(Block.plantRed, 1, 1)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 13), new ItemStack(Block.plantRed, 1, 2)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 14), new ItemStack(Block.plantRed, 1, 5)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 9), new ItemStack(Block.plantRed, 1, 7)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 7), new ItemStack(Block.plantRed, 1, 8)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 1, 15), Item.bone);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 9), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 14), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 11)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 10), new ItemStack(Item.dyePowder, 1, 2), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 8), new ItemStack(Item.dyePowder, 1, 0), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 7), new ItemStack(Item.dyePowder, 1, 8), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 3, 7), new ItemStack(Item.dyePowder, 1, 0), new ItemStack(Item.dyePowder, 1, 15), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 12), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 6), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 2)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 5), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 1)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 13), new ItemStack(Item.dyePowder, 1, 5), new ItemStack(Item.dyePowder, 1, 9)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 3, 13), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 9)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 4, 13), new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 1), new ItemStack(Item.dyePowder, 1, 15)).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 2, 14), Block.plantYellow, Block.plantRed).setSkillset(Skill.FINE_ARTS.id);

      for(var2 = 0; var2 < 16; ++var2) {
         par1CraftingManager.addRecipe(new ItemStack(Block.carpet, 3, var2), "##", '#', new ItemStack(Block.cloth, 1, var2)).setSkillset(Skill.FINE_ARTS.id);
      }

   }
}
