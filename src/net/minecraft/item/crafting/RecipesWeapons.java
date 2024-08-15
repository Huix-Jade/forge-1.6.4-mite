package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesWeapons {
   private static final Object[][] melee_weapons;
   private static final Object[][] arrows;

   public void addRecipes(CraftingManager par1CraftingManager) {
      par1CraftingManager.addRecipes(melee_weapons, -1);
      par1CraftingManager.addRecipes(arrows, Skill.ARCHERY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.bow, 1), " #X", "# X", " #X", 'X', Item.silk, '#', Item.stick).setSkillset(Skill.ARCHERY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.bowMithril, 1), " #X", "#IX", " #X", 'X', Item.silk, 'I', Item.ingotMithril, '#', Item.stick).setSkillset(Skill.ARCHERY.id + Skill.BLACKSMITHING.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.bowAncientMetal, 1), " #X", "#IX", " #X", 'X', Item.silk, 'I', Item.ingotAncientMetal, '#', Item.stick).setSkillset(Skill.ARCHERY.id + Skill.BLACKSMITHING.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.knifeFlint, 1), "Xs", "# ", 'X', Item.flint, '#', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.knifeObsidian, 1), "Xs", "# ", 'X', Block.obsidian, '#', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.cudgelWood, 1), "X", "#", 'X', Block.planks, '#', Item.stick).setSkillset(Skill.CARPENTRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.bow, 1), " #X", "# X", " #X", 'X', Item.sinew, '#', Item.stick).setSkillset(Skill.ARCHERY.id);
   }

   static {
      melee_weapons = new Object[][]{{Block.planks, Item.ingotCopper, Item.ingotSilver, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotGold, Item.ingotAncientMetal, new Object[]{'/', Item.stick}}, {Item.clubWood, Item.swordCopper, Item.swordSilver, Item.swordIron, Item.swordMithril, Item.swordAdamantium, Item.swordGold, Item.swordAncientMetal, new String[]{"?", "?", "/"}}, {null, Item.daggerCopper, Item.daggerSilver, Item.daggerIron, Item.daggerMithril, Item.daggerAdamantium, Item.daggerGold, Item.daggerAncientMetal, new String[]{"?", "/"}}, {null, Item.knifeCopper, Item.knifeSilver, Item.knifeIron, Item.knifeMithril, Item.knifeAdamantium, Item.knifeGold, Item.knifeAncientMetal, new String[]{"?", "/"}}};
      arrows = new Object[][]{{Item.chipFlint, Item.shardObsidian, Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, new Object[]{'/', Item.stick, 'F', Item.feather}}, {Item.arrowFlint, Item.arrowObsidian, Item.arrowCopper, Item.arrowSilver, Item.arrowGold, Item.arrowIron, Item.arrowMithril, Item.arrowAdamantium, Item.arrowAncientMetal, new String[]{"?", "/", "F"}}};
   }
}
