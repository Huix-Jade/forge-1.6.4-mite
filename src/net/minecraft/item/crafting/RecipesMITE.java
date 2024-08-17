package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemCoin;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemIngot;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.Skill;

public class RecipesMITE {
   private static final Object[][] nugget_based_items;
   private static final Object[][] plank_or_ingot_based_items;
   private static final Object[][] chain_based_items;
   private static final Object[][] arrow_dismantling_based_items;
   private static final Object[][] shard_or_nugget_based_items;
   private static final Object[][] fishing_rod_based_items;
   private static final Object[][] carrot_on_a_stick_dismantling_based_items;
   private static final Object[][] ingot_based_items;
   private static final Object[][] bucket_of_stone_dismantling_based_items;

   public static void addCraftingRecipes(CraftingManager crafting_manager)
   {
      crafting_manager.addShapelessRecipe(new ItemStack(Block.sandStone, 1, 3), new Object[] {new ItemStack(Block.sandStone.blockID, 1, 0), Item.goldNugget}).scaleDifficulty(8.0F).setSkillset(Skill.FINE_ARTS.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Block.sandStone, 1, 3), new Object[] {new ItemStack(Block.sandStone.blockID, 1, 2), Item.goldNugget}).scaleDifficulty(4.0F).setSkillset(Skill.FINE_ARTS.id);
      crafting_manager.addRecipe(new ItemStack(Block.obsidianSingleSlab, 6), new Object[] {"###", '#', Block.obsidian}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Block.stone, 2), new Object[] {"##", "##", '#', Block.cobblestone}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Block.glass, 1), new Object[] {"###", "###", '#', Block.thinGlass}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Item.saddle, 1), new Object[] {"LLL", "L L", "i i", 'L', Item.leather, 'i', Item.ironNugget}).setSkillset(Skill.FINE_ARTS.id);
      crafting_manager.addRecipe(new ItemStack(Item.snowball, 1), new Object[] {"#", '#', new ItemStack(Block.snow, 1, 0)});
      crafting_manager.addRecipe(new ItemStack(Block.snow, 1), new Object[] {"#", '#', Item.snowball});
      crafting_manager.addRecipe(new ItemStack(Block.snow, 4), new Object[] {"#", '#', new ItemStack(Block.snow, 1, 3)});
      crafting_manager.addRecipe(new ItemStack(Block.snow, 2, 3), new Object[] {"#", '#', Block.blockSnow});
      crafting_manager.addRecipe(new ItemStack(Block.snow, 1, 3), new Object[] {"##", "##", '#', Item.snowball});
      crafting_manager.addRecipe(new ItemStack(Block.snow, 1, 3), new Object[] {"##", "##", '#', new ItemStack(Block.snow, 1, 0)});
      crafting_manager.addRecipe(new ItemStack(Block.blockSnow, 1), new Object[] {"#", "#", '#', new ItemStack(Block.snow, 1, 3)});
      crafting_manager.addShapelessRecipe(new ItemStack(Item.eyeOfEnder, 1), new Object[] {Item.enderPearl, Item.blazePowder}).setSkillset(Skill.FINE_ARTS.id);
      crafting_manager.addRecipe(new ItemStack(Block.enderChest), new Object[] {"###", "#E#", "###", '#', Block.obsidian, 'E', Item.eyeOfEnder}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipes(plank_or_ingot_based_items, -1);
      crafting_manager.addRecipes(nugget_based_items, -1);
      crafting_manager.addRecipes(chain_based_items, 0);
      crafting_manager.addShapelessRecipes(arrow_dismantling_based_items, 0, false, false);
      crafting_manager.addRecipes(shard_or_nugget_based_items, Skill.FISHING.id);
      crafting_manager.addShapelessRecipes(fishing_rod_based_items, 0, true);
      crafting_manager.addShapelessRecipes(carrot_on_a_stick_dismantling_based_items, 0, true, false);
      crafting_manager.addRecipe(new ItemStack(Item.seeds, 2), new Object[] {"#", '#', Item.wheat});
      crafting_manager.addRecipe(new ItemStack(Item.sinew, 4), new Object[] {"#", '#', Item.leather}).setDifficulty(50.0F);
      crafting_manager.addRecipes(ingot_based_items, -1);
      crafting_manager.addRecipe(new ItemStack(Block.furnaceClayIdle, 1), new Object[] {"##", "##", '#', Block.blockClay});
      crafting_manager.addRecipe(new ItemStack(Block.furnaceHardenedClayIdle, 1), new Object[] {"###", "# #", "###", '#', Block.hardenedClay}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Block.furnaceSandstoneIdle, 1), new Object[] {"###", "# #", "###", '#', Block.sandStone}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Block.furnaceObsidianIdle, 1), new Object[] {"###", "# #", "###", '#', Block.obsidian}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Block.furnaceNetherrackIdle, 1), new Object[] {"###", "# #", "###", '#', Block.netherrack}).setSkillset(Skill.MASONRY.id);
      crafting_manager.addRecipe(new ItemStack(Block.anvilCopper, 1), new Object[] {"III", " i ", "iii", 'I', Block.blockCopper, 'i', Item.ingotCopper}).setSkillset(Skill.BLACKSMITHING.id);
      crafting_manager.addRecipe(new ItemStack(Block.anvilSilver, 1), new Object[] {"III", " i ", "iii", 'I', Block.blockSilver, 'i', Item.ingotSilver}).setSkillset(Skill.BLACKSMITHING.id);
      crafting_manager.addRecipe(new ItemStack(Block.anvilGold, 1), new Object[] {"III", " i ", "iii", 'I', Block.blockGold, 'i', Item.ingotGold}).setSkillset(Skill.BLACKSMITHING.id);
      crafting_manager.addRecipe(new ItemStack(Block.anvilMithril, 1), new Object[] {"III", " i ", "iii", 'I', Block.blockMithril, 'i', Item.ingotMithril}).setSkillset(Skill.BLACKSMITHING.id);
      crafting_manager.addRecipe(new ItemStack(Block.anvilAdamantium, 1), new Object[] {"III", " i ", "iii", 'I', Block.blockAdamantium, 'i', Item.ingotAdamantium}).setSkillset(Skill.BLACKSMITHING.id);
      crafting_manager.addRecipe(new ItemStack(Block.anvilAncientMetal, 1), new Object[] {"III", " i ", "iii", 'I', Block.blockAncientMetal, 'i', Item.ingotAncientMetal}).setSkillset(Skill.BLACKSMITHING.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.cake), new Object[] {Item.flour, Item.sugar, Item.egg, Item.bowlMilk}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.cheese, 1), new Object[] {new ItemStack(Item.bowlMilk, 4)}).setDifficulty(6400).setSkillset(Skill.BREWING.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.cheese, 2), new Object[] {new ItemStack(Item.bowlMilk, 8)}).setDifficulty(6400).setSkillset(Skill.BREWING.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.chocolate, 1), new Object[] {new ItemStack(Item.dyePowder, 1, 3), Item.sugar}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addRecipe(new ItemStack(Block.runestoneMithril, 1), new Object[] {" n ", "n#n", " n ", '#', Block.obsidian, 'n', Item.mithrilNugget}).setSkillset(Skill.MASONRY.id + Skill.FINE_ARTS.id);
      crafting_manager.addRecipe(new ItemStack(Block.runestoneAdamantium, 1), new Object[] {" n ", "n#n", " n ", '#', Block.obsidian, 'n', Item.adamantiumNugget}).setSkillset(Skill.MASONRY.id + Skill.FINE_ARTS.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.appleGold, 1, 1), new Object[] {Item.appleGold, Item.expBottle}).setSkillset(Skill.FINE_ARTS.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bottleOfDisenchanting, 1), new Object[] {new ItemStack(Item.potion, 1, 0), Item.netherStalkSeeds, new ItemStack(Item.coal, 1, 0)});
      ItemBucketMilk[] milk_buckets = new ItemBucketMilk[] {Item.bucketCopperMilk, Item.bucketSilverMilk, Item.bucketGoldMilk, Item.bucketIronMilk, Item.bucketAncientMetalMilk, Item.bucketMithrilMilk, Item.bucketAdamantiumMilk};
      int coins;

      for (int water_buckets = 0; water_buckets < milk_buckets.length; ++water_buckets)
      {
         crafting_manager.addShapelessRecipe(new ItemStack(Item.cake), false, new Object[] {Item.flour, Item.sugar, Item.egg, milk_buckets[water_buckets]}).setSkillset(Skill.FOOD_PREPARATION.id);

         for (coins = 1; coins <= 9; ++coins)
         {
            crafting_manager.addShapelessRecipe(new ItemStack(Item.cheese, coins), new Object[] {new ItemStack(milk_buckets[water_buckets], coins)}).setDifficulty(6400).setSkillset(Skill.BREWING.id);
         }

         for (coins = 1; coins <= 4; ++coins)
         {
            crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlMilk, coins), true, new Object[] {milk_buckets[water_buckets], new ItemStack(Item.bowlEmpty, coins)}).setDifficulty(25);
         }

         crafting_manager.addShapelessRecipe(new ItemStack(milk_buckets[water_buckets]), true, new Object[] {milk_buckets[water_buckets].getEmptyVessel(), Item.bowlMilk, Item.bowlMilk, Item.bowlMilk, Item.bowlMilk}).setDifficulty(25);
      }

      crafting_manager.addShapelessRecipe(new ItemStack(Item.dough, 1), new Object[] {Item.flour, Item.bowlWater});
      ItemBucket[] var7 = new ItemBucket[] {Item.bucketCopperWater, Item.bucketSilverWater, Item.bucketGoldWater, Item.bucketWater, Item.bucketAncientMetalWater, Item.bucketMithrilWater, Item.bucketAdamantiumWater};
      int i;

      for (coins = 0; coins < milk_buckets.length; ++coins)
      {
         for (i = 1; i <= 4; ++i)
         {
            crafting_manager.addShapelessRecipe(new ItemStack(Item.dough, i), false, new Object[] {var7[coins], new ItemStack(Item.flour, i)});
            crafting_manager.addShapelessRecipe(new ItemStack(Item.cookie, i * 4), false, new Object[] {var7[coins], new ItemStack(Item.flour, i), new ItemStack(Item.chocolate, i)}).setSkillset(Skill.FOOD_PREPARATION.id);
            crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlWater, i), true, new Object[] {var7[coins], new ItemStack(Item.bowlEmpty, i)}).setDifficulty(25);
         }

         for (i = 1; i <= 2; ++i)
         {
            crafting_manager.addShapelessRecipe(new ItemStack(Item.cookie, i * 4), false, new Object[] {var7[coins], new ItemStack(Item.flour, i), new ItemStack(Item.dyePowder, i, 3), new ItemStack(Item.sugar, i)}).setSkillset(Skill.FOOD_PREPARATION.id);
         }

         crafting_manager.addShapelessRecipe(new ItemStack(var7[coins]), true, new Object[] {var7[coins].getEmptyVessel(), new ItemStack(Item.bowlWater, 4)}).setDifficulty(25);
      }

      for (coins = 1; coins <= 4; ++coins)
      {
         crafting_manager.addShapelessRecipe(new ItemStack(Item.cookie, coins * 4), false, new Object[] {new ItemStack(Item.dough, coins), new ItemStack(Item.chocolate, coins)}).setSkillset(Skill.FOOD_PREPARATION.id);
      }

      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlBeefStew), new Object[] {Item.beefCooked, Block.mushroomBrown, Item.potato, Item.bowlWater}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlChickenSoup), new Object[] {Item.chickenCooked, Item.carrot, Item.onion, Item.bowlWater}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlVegetableSoup), new Object[] {Item.potato, Item.carrot, Item.onion, Item.bowlWater}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlIceCream), new Object[] {Item.chocolate, Item.bowlMilk, Item.snowball}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlIceCream), new Object[] {new ItemStack(Item.dyePowder, 1, 3), Item.sugar, Item.bowlMilk, Item.snowball}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlSalad), new Object[] {Block.plantYellow, Block.plantYellow, Block.plantYellow, Item.bowlEmpty}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlCreamOfMushroomSoup), new Object[] {Block.mushroomBrown, Block.mushroomBrown, Item.bowlMilk}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlCreamOfVegetableSoup), new Object[] {Item.potato, Item.carrot, Item.onion, Item.bowlMilk}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlPumpkinSoup), new Object[] {Block.pumpkin, Item.bowlWater}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlMashedPotato), new Object[] {Item.bakedPotato, Item.cheese, Item.bowlMilk}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlSorbet), new Object[] {Item.orange, Item.sugar, Item.snowball, Item.bowlEmpty}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlPorridge), new Object[] {Item.seeds, Item.blueberries, Item.sugar, Item.bowlWater}).setSkillset(Skill.FOOD_PREPARATION.id);
      crafting_manager.addShapelessRecipe(new ItemStack(Item.bowlCereal), new Object[] {Item.wheat, Item.sugar, Item.bowlMilk}).setSkillset(Skill.FOOD_PREPARATION.id);
      ItemCoin[] var9 = new ItemCoin[] {Item.coinCopper, Item.coinSilver, Item.coinGold, Item.coinAncientMetal, Item.coinMithril, Item.coinAdamantium};
      int plank_subtype;

      for (i = 0; i < var9.length; ++i)
      {
         ItemCoin tool_material = var9[i];

         for (plank_subtype = 1; plank_subtype <= 9; ++plank_subtype)
         {
            crafting_manager.addShapelessRecipe(new ItemStack(tool_material.getNuggetPeer(), plank_subtype), new Object[] {new ItemStack(tool_material, plank_subtype)}).setDifficulty(25);
         }

         crafting_manager.addShapelessRecipe(new ItemStack(tool_material), new Object[] {new ItemStack(tool_material.getNuggetPeer())}).setDifficulty(100);
      }

      crafting_manager.addShapelessRecipes(bucket_of_stone_dismantling_based_items, 0, true, false);

      for (i = 0; i < Block.workbench.getNumSubBlocks(); ++i)
      {
         Material var8 = BlockWorkbench.getToolMaterial(i);

         if (var8 == Material.flint)
         {
            crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"K", "#", 'K', Item.knifeFlint, '#', BlockWorkbench.getBlockComponent(i)});
            crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"FS", "s#", 'F', Item.flint, 'S', Item.silk, 's', Item.stick, '#', BlockWorkbench.getBlockComponent(i)});
            crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"FS", "s#", 'F', Item.flint, 'S', Item.sinew, 's', Item.stick, '#', BlockWorkbench.getBlockComponent(i)});
            crafting_manager.addRecipe(new ItemStack(Item.knifeFlint, 1), false, new Object[] {"#", '#', new ItemStack(Block.workbench, 1, i)}).setDifficulty(25.0F);
         }
         else if (var8 == Material.obsidian)
         {
            crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"K", "#", 'K', Item.knifeObsidian, '#', BlockWorkbench.getBlockComponent(i)});
            crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"OS", "s#", 'O', Block.obsidian, 'S', Item.silk, 's', Item.stick, '#', BlockWorkbench.getBlockComponent(i)});
            crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"OS", "s#", 'O', Block.obsidian, 'S', Item.sinew, 's', Item.stick, '#', BlockWorkbench.getBlockComponent(i)});
            crafting_manager.addRecipe(new ItemStack(Item.knifeObsidian, 1), false, new Object[] {"#", '#', new ItemStack(Block.workbench, 1, i)}).setDifficulty(25.0F);
         }
         else
         {
            for (plank_subtype = 0; plank_subtype < 4; ++plank_subtype)
            {
               crafting_manager.addRecipe(new ItemStack(Block.workbench, 1, i), new Object[] {"IL", "s#", 'I', ItemIngot.getMatchingItem(ItemIngot.class, var8), 'L', Item.leather, 's', Item.stick, '#', new ItemStack(Block.planks, 1, plank_subtype)});
            }
         }
      }
   }

   public static void addFurnaceRecipes(FurnaceRecipes furnace_recipes) {
      furnace_recipes.addSmelting(Block.sand.blockID, new ItemStack(Block.sandStone));
      furnace_recipes.addSmelting(Block.sandStone.blockID, new ItemStack(Block.glass));
      furnace_recipes.addSmelting(Item.clay.itemID, new ItemStack(Item.brick));
      furnace_recipes.addSmelting(Block.blockClay.blockID, new ItemStack(Block.hardenedClay));
      furnace_recipes.addSmelting(Block.cactus.blockID, new ItemStack(Item.dyePowder, 1, 2));
      furnace_recipes.addSmelting(Block.wood.blockID, new ItemStack(Item.coal, 1, 1));
      furnace_recipes.addSmelting(Block.netherrack.blockID, new ItemStack(Item.netherrackBrick));
      furnace_recipes.addSmelting(Block.oreCoal.blockID, new ItemStack(Item.coal));
      furnace_recipes.addSmelting(Block.oreRedstone.blockID, new ItemStack(Item.redstone, 4));
      furnace_recipes.addSmelting(Block.oreLapis.blockID, new ItemStack(Item.dyePowder, 4, 4));
      furnace_recipes.addSmelting(Block.oreNetherQuartz.blockID, new ItemStack(Item.netherQuartz));
      furnace_recipes.addSmelting(Block.oreCopper.blockID, new ItemStack(Item.ingotCopper));
      furnace_recipes.addSmelting(Block.oreSilver.blockID, new ItemStack(Item.ingotSilver));
      furnace_recipes.addSmelting(Block.oreIron.blockID, new ItemStack(Item.ingotIron));
      furnace_recipes.addSmelting(Block.oreMithril.blockID, new ItemStack(Item.ingotMithril));
      furnace_recipes.addSmelting(Block.oreAdamantium.blockID, new ItemStack(Item.ingotAdamantium));
      furnace_recipes.addSmelting(Block.oreGold.blockID, new ItemStack(Item.ingotGold));
      furnace_recipes.addSmelting(Block.oreEmerald.blockID, new ItemStack(Item.emerald));
      furnace_recipes.addSmelting(Block.oreDiamond.blockID, new ItemStack(Item.diamond));

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.getItem(i);
         if (item instanceof ItemFood) {
            ItemFood food = (ItemFood)item;
            if (food.getCookedItem() != null) {
               furnace_recipes.addSmelting(i, new ItemStack(food.getCookedItem()));
            }
         }
      }

   }

   static {
      nugget_based_items = new Object[][]{{Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, new Object[0]}, {Item.chainCopper, Item.chainSilver, Item.chainGold, Item.chainIron, Item.chainMithril, Item.chainAdamantium, Item.chainAncientMetal, new String[]{" ? ", "? ?", " ? "}}};
      plank_or_ingot_based_items = new Object[][]{{Block.planks, Item.ingotCopper, Item.ingotSilver, Item.ingotGold, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotAncientMetal, new Object[]{'/', Item.stick}}, {Item.doorWood, Item.doorCopper, Item.doorSilver, Item.doorGold, Item.doorIron, Item.doorMithril, Item.doorAdamantium, Item.doorAncientMetal, new String[]{"??", "??", "??"}}, {Block.chest, Block.chestCopper, Block.chestSilver, Block.chestGold, Block.chestIron, Block.chestMithril, Block.chestAdamantium, Block.chestAncientMetal, new String[]{"???", "? ?", "???"}}};
      chain_based_items = new Object[][]{{Item.chainCopper, Item.chainSilver, Item.chainGold, Item.chainIron, Item.chainMithril, Item.chainAdamantium, Item.chainAncientMetal, null}, {Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, new String[]{"?"}, 4}};
      arrow_dismantling_based_items = new Object[][]{{Item.arrowFlint, Item.arrowObsidian, Item.arrowCopper, Item.arrowSilver, Item.arrowGold, Item.arrowIron, Item.arrowMithril, Item.arrowAdamantium, Item.arrowAncientMetal}, {Item.chipFlint, Item.shardObsidian, Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, null}};
      shard_or_nugget_based_items = new Object[][]{{Item.chipFlint, Item.shardObsidian, Item.copperNugget, Item.silverNugget, Item.goldNugget, Item.ironNugget, Item.mithrilNugget, Item.adamantiumNugget, Item.ancientMetalNugget, new Object[]{'/', Item.stick, '|', Item.silk}}, {Item.fishingRodFlint, Item.fishingRodObsidian, Item.fishingRodCopper, Item.fishingRodSilver, Item.fishingRodGold, Item.fishingRodIron, Item.fishingRodMithril, Item.fishingRodAdamantium, Item.fishingRodAncientMetal, new String[]{"  /", " /|", "/?|"}}};
      fishing_rod_based_items = new Object[][]{{Item.fishingRodFlint, Item.fishingRodObsidian, Item.fishingRodCopper, Item.fishingRodSilver, Item.fishingRodGold, Item.fishingRodIron, Item.fishingRodMithril, Item.fishingRodAdamantium, Item.fishingRodAncientMetal}, {Item.carrotOnAStickFlint, Item.carrotOnAStickObsidian, Item.carrotOnAStickCopper, Item.carrotOnAStickSilver, Item.carrotOnAStickGold, Item.carrotOnAStickIron, Item.carrotOnAStickMithril, Item.carrotOnAStickAdamantium, Item.carrotOnAStickAncientMetal, new Object[]{Item.carrot}, 1, 40}};
      carrot_on_a_stick_dismantling_based_items = new Object[][]{{Item.carrotOnAStickFlint, Item.carrotOnAStickObsidian, Item.carrotOnAStickCopper, Item.carrotOnAStickSilver, Item.carrotOnAStickGold, Item.carrotOnAStickIron, Item.carrotOnAStickMithril, Item.carrotOnAStickAdamantium, Item.carrotOnAStickAncientMetal}, {Item.fishingRodFlint, Item.fishingRodObsidian, Item.fishingRodCopper, Item.fishingRodSilver, Item.fishingRodGold, Item.fishingRodIron, Item.fishingRodMithril, Item.fishingRodAdamantium, Item.fishingRodAncientMetal, null, 1, 40}};
      ingot_based_items = new Object[][]{{Item.ingotCopper, Item.ingotSilver, Item.ingotGold, Item.ingotIron, Item.ingotMithril, Item.ingotAdamantium, Item.ingotAncientMetal, null}, {Item.bucketCopperEmpty, Item.bucketSilverEmpty, Item.bucketGoldEmpty, Item.bucketEmpty, Item.bucketMithrilEmpty, Item.bucketAdamantiumEmpty, Item.bucketAncientMetalEmpty, new String[]{"? ?", " ? "}}, {Block.fenceCopper, Block.fenceSilver, Block.fenceGold, Block.fenceIron, Block.fenceMithril, Block.fenceAdamantium, Block.fenceAncientMetal, new String[]{"???", "???"}, 16}};
      bucket_of_stone_dismantling_based_items = new Object[][]{{Item.bucketCopperStone, Item.bucketSilverStone, Item.bucketGoldStone, Item.bucketIronStone, Item.bucketMithrilStone, Item.bucketAdamantiumStone, Item.bucketAncientMetalStone}, {Item.bucketCopperEmpty, Item.bucketSilverEmpty, Item.bucketGoldEmpty, Item.bucketEmpty, Item.bucketMithrilEmpty, Item.bucketAdamantiumEmpty, Item.bucketAncientMetalEmpty, null, 1, 100}};
   }
}
