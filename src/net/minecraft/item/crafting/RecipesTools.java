package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesTools {
   private static final Object[][] tools;

   public void addRecipes(CraftingManager par1CraftingManager) {
      par1CraftingManager.addRecipes(tools, -1);
      par1CraftingManager.addRecipe(new ItemStack(Item.shovelFlint), "# ", "- ", "-s", '#', Item.flint, '-', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.hatchetFlint), "#-", "s-", '#', Item.flint, '-', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.axeFlint), "##", "#-", "s-", '#', Item.flint, '-', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.shovelObsidian), "# ", "- ", "-s", '#', Block.obsidian, '-', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.hatchetObsidian), "#-", "s-", '#', Block.obsidian, '-', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
      par1CraftingManager.addRecipe(new ItemStack(Item.axeObsidian), "##", "#-", "s-", '#', Block.obsidian, '-', Item.stick, 's', Item.sinew).setSkillset(Skill.MASONRY.id);
   }

   static {
      tools = new Object[][]{{Block.planks, Item.ingotCopper, Item.ingotSilver, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotGold, Item.ingotAncientMetal, new Object[]{'/', Item.stick}}, {null, Item.pickaxeCopper, Item.pickaxeSilver, Item.pickaxeIron, Item.pickaxeMithril, Item.pickaxeAdamantium, Item.pickaxeGold, Item.pickaxeAncientMetal, new String[]{"???", " / ", " / "}}, {Item.shovelWood, Item.shovelCopper, Item.shovelSilver, Item.shovelIron, Item.shovelMithril, Item.shovelAdamantium, Item.shovelGold, Item.shovelAncientMetal, new String[]{"?", "/", "/"}}, {null, Item.axeCopper, Item.axeSilver, Item.axeIron, Item.axeMithril, Item.axeAdamantium, Item.axeGold, Item.axeAncientMetal, new String[]{"??", "?/", " /"}}, {null, Item.hoeCopper, Item.hoeSilver, Item.hoeIron, Item.hoeMithril, Item.hoeAdamantium, Item.hoeGold, Item.hoeAncientMetal, new String[]{"??", " /", " /"}}, {null, Item.mattockCopper, Item.mattockSilver, Item.mattockIron, Item.mattockMithril, Item.mattockAdamantium, Item.mattockGold, Item.mattockAncientMetal, new String[]{"???", " /?", " / "}}, {null, Item.battleAxeCopper, Item.battleAxeSilver, Item.battleAxeIron, Item.battleAxeMithril, Item.battleAxeAdamantium, Item.battleAxeGold, Item.battleAxeAncientMetal, new String[]{"? ?", "?/?", " / "}}, {null, Item.warHammerCopper, Item.warHammerSilver, Item.warHammerIron, Item.warHammerMithril, Item.warHammerAdamantium, Item.warHammerGold, Item.warHammerAncientMetal, new String[]{"???", "?/?", " / "}}, {null, Item.scytheCopper, Item.scytheSilver, Item.scytheIron, Item.scytheMithril, Item.scytheAdamantium, Item.scytheGold, Item.scytheAncientMetal, new String[]{"/? ", "/ ?", "/  "}}, {null, Item.hatchetCopper, Item.hatchetSilver, Item.hatchetIron, Item.hatchetMithril, Item.hatchetAdamantium, Item.hatchetGold, Item.hatchetAncientMetal, new String[]{"/?", "/ "}}, {null, Item.shearsCopper, Item.shearsSilver, Item.shears, Item.shearsMithril, Item.shearsAdamantium, Item.shearsGold, Item.shearsAncientMetal, new String[]{" ?", "? "}}};
   }
}
