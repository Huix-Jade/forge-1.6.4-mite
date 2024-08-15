package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesIngots {
   private static final Object[][] ingot_based_items;
   private static final Object[][] nugget_based_items;
   private static final Object[][] shard_3x3_based_items;
   private static final Object[][] block_or_crystal_dismantling_based_items;
   private Object[][] recipeItems;

   public RecipesIngots() {
      this.recipeItems = new Object[][]{{Block.blockCopper, new ItemStack(Item.ingotCopper, 9), Skill.BLACKSMITHING.id}, {Block.blockSilver, new ItemStack(Item.ingotSilver, 9), Skill.BLACKSMITHING.id}, {Block.blockGold, new ItemStack(Item.ingotGold, 9), Skill.BLACKSMITHING.id}, {Block.blockIron, new ItemStack(Item.ingotIron, 9), Skill.BLACKSMITHING.id}, {Block.blockMithril, new ItemStack(Item.ingotMithril, 9), Skill.BLACKSMITHING.id}, {Block.blockAdamantium, new ItemStack(Item.ingotAdamantium, 9), Skill.BLACKSMITHING.id}, {Block.blockAncientMetal, new ItemStack(Item.ingotAncientMetal, 9), Skill.BLACKSMITHING.id}, {Block.blockDiamond, new ItemStack(Item.diamond, 9), Skill.MASONRY.id + Skill.FINE_ARTS.id}, {Block.blockEmerald, new ItemStack(Item.emerald, 9), Skill.MASONRY.id + Skill.FINE_ARTS.id}, {Block.blockLapis, new ItemStack(Item.dyePowder, 9, 4), Skill.MASONRY.id + Skill.FINE_ARTS.id}, {Block.blockRedstone, new ItemStack(Item.redstone, 9), Skill.MASONRY.id}, {Block.coalBlock, new ItemStack(Item.coal, 9, 0), 0}, {Block.hay, new ItemStack(Item.wheat, 9), Skill.FARMING.id}, {Block.blockNetherQuartz, new ItemStack(Item.netherQuartz, 4), Skill.MASONRY.id + Skill.FINE_ARTS.id, true}, {Item.flint, new ItemStack(Item.chipFlint, 4), 0}};
   }

   public void addRecipes(CraftingManager par1CraftingManager) {
      for(int var2 = 0; var2 < this.recipeItems.length; ++var2) {
         Object var3 = this.recipeItems[var2][0];
         ItemStack var4 = (ItemStack)this.recipeItems[var2][1];
         int skillset = (Integer)this.recipeItems[var2][2];
         par1CraftingManager.addRecipe(var3 instanceof Block ? new ItemStack((Block)var3) : (var3 instanceof Item ? new ItemStack((Item)var3) : (ItemStack)var3), var4.stackSize == 4 ? new Object[]{"##", "##", '#', var4} : new Object[]{"###", "###", "###", '#', var4}).setSkillset(skillset);
         if (this.recipeItems[var2].length == 4) {
            Object object = this.recipeItems[var2][3];
            par1CraftingManager.addRecipe(var4, "#", '#', var3).setSkillset(object instanceof Boolean ? skillset : (Integer)object);
         } else {
            par1CraftingManager.addRecipe(var4, "#", '#', var3);
         }
      }

      par1CraftingManager.addShapelessRecipes(ingot_based_items, -1, false);
      par1CraftingManager.addRecipes(nugget_based_items, -1);
      par1CraftingManager.addRecipes(shard_3x3_based_items, -1);
      par1CraftingManager.addShapelessRecipes(block_or_crystal_dismantling_based_items, -1, false);
   }

   static {
      ingot_based_items = new Object[][]{{Item.ingotCopper, Item.ingotSilver, Item.ingotGold, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotAncientMetal}, {Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, null, 9}};
      nugget_based_items = new Object[][]{{Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, null}, {Item.ingotCopper, Item.ingotSilver, Item.ingotGold, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotAncientMetal, new String[]{"???", "???", "???"}}};
      shard_3x3_based_items = new Object[][]{{Item.shardEmerald, Item.shardDiamond, Item.shardNetherQuartz, Item.shardGlass, Item.shardObsidian, null}, {Item.emerald, Item.diamond, Item.netherQuartz, Block.thinGlass, Block.obsidian, new String[]{"???", "???", "???"}}};
      block_or_crystal_dismantling_based_items = new Object[][]{{Item.emerald, Item.diamond, Item.netherQuartz, Block.thinGlass, Block.obsidian}, {Item.shardEmerald, Item.shardDiamond, Item.shardNetherQuartz, Item.shardGlass, Item.shardObsidian, null, 9}};
   }
}
