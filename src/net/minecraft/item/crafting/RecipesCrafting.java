package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesCrafting {
   public void addRecipes(CraftingManager par1CraftingManager) {
      par1CraftingManager.addRecipe(new ItemStack(Block.chestTrapped), "#-", '#', Block.chest, '-', Block.tripWireSource);
      par1CraftingManager.addRecipe(new ItemStack(Block.furnaceIdle), "###", "# #", "###", '#', Block.cobblestone).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.sandStone, 4, 2), "##", "##", '#', Block.sandStone).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.sandStone, 1, 1), "#", "#", '#', new ItemStack(Block.stoneSingleSlab, 1, 1)).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.blockNetherQuartz, 1, 1), "#", "#", '#', new ItemStack(Block.stoneSingleSlab, 1, 7)).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.blockNetherQuartz, 2, 2), "#", "#", '#', new ItemStack(Block.blockNetherQuartz, 1, 0)).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.stoneBrick, 2), "##", "##", '#', Block.stone).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.thinGlass, 6), "#", '#', Block.glass).setSkillset(Skill.MASONRY.id + Skill.FINE_ARTS.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.redstoneLampIdle, 1), " R ", "RGR", " R ", 'R', Item.redstone, 'G', Block.glowStone).setSkillset(Skill.MASONRY.id + Skill.TINKERING.id);
      par1CraftingManager.addRecipe(new ItemStack(Block.beacon, 1), "GGG", "GSG", "OOO", 'G', Block.glass, 'S', Item.netherStar, 'O', Block.obsidian).setSkillset(Skill.MASONRY.id);
   }
}
