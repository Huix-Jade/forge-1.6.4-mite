package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesFood {
   public void addRecipes(CraftingManager par1CraftingManager) {
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.bowlMushroomStew), Block.mushroomBrown, Block.mushroomRed, Item.bowlWater).setSkillset(Skill.FOOD_PREPARATION.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.cookie, 4), Item.dough, new ItemStack(Item.dyePowder, 1, 3), Item.sugar).setSkillset(Skill.FOOD_PREPARATION.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.cookie, 4), Item.dough, Item.chocolate).setSkillset(Skill.FOOD_PREPARATION.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.cookie, 4), Item.flour, Item.bowlWater, new ItemStack(Item.dyePowder, 1, 3), Item.sugar).setSkillset(Skill.FOOD_PREPARATION.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.cookie, 4), Item.flour, Item.bowlWater, Item.chocolate).setSkillset(Skill.FOOD_PREPARATION.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.melonSeeds), "MM", "MM", 'M', Item.melon);
      par1CraftingManager.addRecipe(new ItemStack(Item.pumpkinSeeds, 1), "M", 'M', Block.pumpkin);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.pumpkinPie), Block.pumpkin, Item.sugar, Item.egg, Item.flour).setSkillset(Skill.FOOD_PREPARATION.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.fermentedSpiderEye), Item.spiderEye, Block.mushroomBrown, Item.sugar).setSkillset(Skill.BREWING.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.blazePowder, 2), Item.blazeRod).setSkillset(Skill.FINE_ARTS.id);
      par1CraftingManager.addShapelessRecipe(new ItemStack(Item.magmaCream), Item.blazePowder, Item.slimeBall).setSkillset(Skill.FINE_ARTS.id);
   }
}
