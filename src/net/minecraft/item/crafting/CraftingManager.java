package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.MITEContainerCrafting;
import net.minecraft.mite.Skill;
import net.minecraft.world.World;

public class CraftingManager {
   private static final CraftingManager instance = new CraftingManager();
   private List recipes = new ArrayList();

   public static final CraftingManager getInstance() {
      return instance;
   }

   private CraftingManager() {
      (new RecipesTools()).addRecipes(this);
      (new RecipesWeapons()).addRecipes(this);
      (new RecipesIngots()).addRecipes(this);
      (new RecipesFood()).addRecipes(this);
      RecipesMITE.addCraftingRecipes(this);
      (new RecipesCrafting()).addRecipes(this);
      (new RecipesArmor()).addRecipes(this);
      (new RecipesDyes()).addRecipes(this);
      this.recipes.add(new RecipesArmorDyes());
      this.recipes.add(new RecipesMapCloning());
      this.recipes.add(new RecipesMapExtending());
      this.recipes.add(new RecipeFireworks());
      this.addRecipe(new ItemStack(Item.paper, 3), "###", '#', Item.reed).setSkillset(Skill.FINE_ARTS.id);
      this.addShapelessRecipe(new ItemStack(Item.book, 1), Item.paper, Item.paper, Item.paper, Item.leather).setSkillset(Skill.FINE_ARTS.id);
      this.addShapelessRecipe(new ItemStack(Item.writableBook, 1), Item.book, new ItemStack(Item.dyePowder, 1, 0), Item.feather).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.fence, 2), "###", "###", '#', Item.stick).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.cobblestoneWall, 8, 0), "###", "###", '#', Block.cobblestone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.cobblestoneWall, 8, 1), "###", "###", '#', Block.cobblestoneMossy).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.netherFence, 8), "###", "###", '#', Block.netherBrick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.fenceGate, 1), "#W#", "#W#", '#', Item.stick, 'W', Block.planks).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.jukebox, 1), "###", "#X#", "###", '#', Block.planks, 'X', Item.diamond).setSkillset(Skill.CARPENTRY.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.leash, 2), "~~ ", "~O ", "  ~", '~', Item.silk, 'O', Item.slimeBall).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Item.leash, 2), "~~ ", "~O ", "  ~", '~', Item.sinew, 'O', Item.slimeBall).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.music, 1), "###", "#X#", "###", '#', Block.planks, 'X', Item.redstone).setSkillset(Skill.CARPENTRY.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.bookShelf, 1), "###", "XXX", "###", '#', Block.planks, 'X', Item.book).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.blockClay, 1), "##", "##", '#', Item.clay);
      this.addRecipe(new ItemStack(Block.brick, 2), "###", "#X#", "###", '#', Item.brick, 'X', Block.sand).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.hardenedClay, 1), "##", "##", '#', Item.brick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.netherBrick, 2), "###", "#X#", "###", '#', Item.netherrackBrick, 'X', Block.slowSand).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Item.brick, 4), "#", '#', Block.hardenedClay).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.glowStone, 1), "##", "##", '#', Item.glowstone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.cloth, 1), "##", "##", '#', Item.silk).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.tnt, 1), "X#X", "#X#", "X#X", 'X', Item.gunpowder, '#', Block.sand).setSkillset(Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 3), "###", '#', Block.cobblestone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 0), "###", '#', Block.stone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 1), "###", '#', Block.sandStone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 4), "###", '#', Block.brick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 5), "###", '#', Block.stoneBrick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 6), "###", '#', Block.netherBrick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stoneSingleSlab, 6, 7), "###", '#', Block.blockNetherQuartz).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.woodSingleSlab, 6, 0), "###", '#', new ItemStack(Block.planks, 1, 0)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.woodSingleSlab, 6, 2), "###", '#', new ItemStack(Block.planks, 1, 2)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.woodSingleSlab, 6, 1), "###", '#', new ItemStack(Block.planks, 1, 1)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.woodSingleSlab, 6, 3), "###", '#', new ItemStack(Block.planks, 1, 3)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.ladder, 2), "# #", "###", "# #", '#', Item.stick).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.trapdoor, 2), "###", "###", '#', Block.planks).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Item.sign, 1), "#", "/", '#', Block.woodSingleSlab, '/', Item.stick).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Item.sugar, 1), "#", '#', Item.reed).scaleDifficulty(8.0F).setSkillset(Skill.FARMING.id);
      this.addRecipe(new ItemStack(Block.planks, 4, 0), "#", '#', new ItemStack(Block.wood, 1, 0)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.planks, 4, 1), "#", '#', new ItemStack(Block.wood, 1, 1)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.planks, 4, 2), "#", '#', new ItemStack(Block.wood, 1, 2)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.planks, 4, 3), "#", '#', new ItemStack(Block.wood, 1, 3)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Item.stick, 4), "#", "#", '#', Block.planks);
      this.addRecipe(new ItemStack(Block.torchWood, 4), "X", "#", 'X', Item.coal, '#', Item.stick);
      this.addRecipe(new ItemStack(Block.torchWood, 4), "X", "#", 'X', new ItemStack(Item.coal, 1, 1), '#', Item.stick);
      this.addRecipe(new ItemStack(Item.bowlEmpty, 4), "# #", " # ", '#', Block.planks).setSkillsets(new int[]{Skill.CARPENTRY.id, Skill.FINE_ARTS.id});
      this.addRecipe(new ItemStack(Item.glassBottle, 3), "# #", " # ", '#', Block.glass).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.rail, 16), "X X", "X#X", "X X", 'X', Item.ingotIron, '#', Item.stick).setSkillset(Skill.BLACKSMITHING.id);
      this.addRecipe(new ItemStack(Block.railPowered, 6), "X X", "X#X", "XRX", 'X', Item.ingotGold, 'R', Item.redstone, '#', Item.stick).setSkillset(Skill.BLACKSMITHING.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.railActivator, 6), "XSX", "X#X", "XSX", 'X', Item.ingotIron, '#', Block.torchRedstoneActive, 'S', Item.stick).setSkillset(Skill.BLACKSMITHING.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.railDetector, 6), "X X", "X#X", "XRX", 'X', Item.ingotIron, 'R', Item.redstone, '#', Block.pressurePlateStone).setSkillset(Skill.BLACKSMITHING.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.minecartEmpty, 1), "# #", "###", '#', Item.ingotIron).setSkillset(Skill.BLACKSMITHING.id);
      this.addRecipe(new ItemStack(Item.cauldron, 1), "# #", "# #", "###", '#', Item.ingotIron).setSkillset(Skill.BLACKSMITHING.id);
      this.addRecipe(new ItemStack(Item.brewingStand, 1), " B ", "###", '#', Block.cobblestone, 'B', Item.blazeRod).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.pumpkinLantern, 1), "A", "B", 'A', Block.pumpkin, 'B', Block.torchWood);
      this.addRecipe(new ItemStack(Item.minecartCrate, 1), "A", "B", 'A', Block.chest, 'B', Item.minecartEmpty).setDifficulty(25.0F);
      this.addRecipe(new ItemStack(Item.minecartPowered, 1), "A", "B", 'A', Block.furnaceIdle, 'B', Item.minecartEmpty).setDifficulty(25.0F);
      this.addRecipe(new ItemStack(Item.minecartTnt, 1), "A", "B", 'A', Block.tnt, 'B', Item.minecartEmpty);
      this.addRecipe(new ItemStack(Item.minecartHopper, 1), "A", "B", 'A', Block.hopperBlock, 'B', Item.minecartEmpty).setDifficulty(100.0F);
      this.addRecipe(new ItemStack(Item.boat, 1), "# #", "###", '#', Block.planks).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Item.flowerPot, 1), "# #", " # ", '#', Item.brick).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Item.flintAndSteel, 1), "A ", " B", 'A', Item.ironNugget, 'B', Item.flint);
      this.addRecipe(new ItemStack(Item.flour, 1), "###", '#', Item.wheat);
      this.addRecipe(new ItemStack(Item.flour, 1), "#", "#", "#", '#', Item.wheat);
      this.addRecipe(new ItemStack(Block.stairsWoodOak, 4), "#  ", "## ", "###", '#', new ItemStack(Block.planks, 1, 0)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.stairsWoodBirch, 4), "#  ", "## ", "###", '#', new ItemStack(Block.planks, 1, 2)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.stairsWoodSpruce, 4), "#  ", "## ", "###", '#', new ItemStack(Block.planks, 1, 1)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.stairsWoodJungle, 4), "#  ", "## ", "###", '#', new ItemStack(Block.planks, 1, 3)).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.stairsCobblestone, 4), "#  ", "## ", "###", '#', Block.cobblestone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stairsBrick, 4), "#  ", "## ", "###", '#', Block.brick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stairsStoneBrick, 4), "#  ", "## ", "###", '#', Block.stoneBrick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stairsNetherBrick, 4), "#  ", "## ", "###", '#', Block.netherBrick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stairsSandStone, 4), "#  ", "## ", "###", '#', Block.sandStone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stairsNetherQuartz, 4), "#  ", "## ", "###", '#', Block.blockNetherQuartz).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.stairsObsidian, 4), "#  ", "## ", "###", '#', Block.obsidian).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Item.painting, 1), "###", "#X#", "###", '#', Item.stick, 'X', Block.cloth).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Item.itemFrame, 1), "###", "#X#", "###", '#', Item.stick, 'X', Item.leather).setSkillsets(new int[]{Skill.CARPENTRY.id, Skill.FINE_ARTS.id});
      this.addRecipe(new ItemStack(Item.appleGold, 1, 0), "###", "#X#", "###", '#', Item.goldNugget, 'X', Item.appleRed).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Item.goldenCarrot, 1, 0), "###", "#X#", "###", '#', Item.goldNugget, 'X', Item.carrot).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Item.speckledMelon, 1), "###", "#X#", "###", '#', Item.goldNugget, 'X', Item.melon).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.lever, 1), "X", "#", '#', Block.cobblestone, 'X', Item.stick).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.tripWireSource, 2), "I", "S", "#", '#', Block.planks, 'S', Item.stick, 'I', Item.ingotIron).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.torchRedstoneActive, 1), "X", "#", '#', Item.stick, 'X', Item.redstone).setSkillset(Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.redstoneRepeater, 1), "#X#", "III", '#', Block.torchRedstoneActive, 'X', Item.redstone, 'I', Block.stone).setSkillset(Skill.MASONRY.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.comparator, 1), " # ", "#X#", "III", '#', Block.torchRedstoneActive, 'X', Item.netherQuartz, 'I', Block.stone).setSkillset(Skill.MASONRY.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.pocketSundial, 1), "###", "#X#", "###", '#', Item.goldNugget, 'X', Item.redstone).setSkillset(Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.compass, 1), "###", "#X#", "###", '#', Item.ironNugget, 'X', Item.redstone).setSkillset(Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Item.emptyMap, 1), "###", "#X#", "###", '#', Item.paper, 'X', Item.compass).setSkillset(Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.stoneButton, 1), "#", '#', Block.stone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.woodenButton, 1), "#", '#', Block.planks).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.pressurePlateStone, 1), "##", '#', Block.stone).setSkillset(Skill.MASONRY.id);
      this.addRecipe(new ItemStack(Block.pressurePlatePlanks, 1), "##", '#', Block.planks).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.pressurePlateIron, 1), "##", '#', Item.ingotIron).setSkillset(Skill.BLACKSMITHING.id);
      this.addRecipe(new ItemStack(Block.pressurePlateGold, 1), "##", '#', Item.ingotGold).setSkillset(Skill.BLACKSMITHING.id);
      this.addRecipe(new ItemStack(Block.dispenser, 1), "###", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.bow, 'R', Item.redstone).setSkillset(Skill.MASONRY.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.dropper, 1), "###", "# #", "#R#", '#', Block.cobblestone, 'R', Item.redstone).setSkillset(Skill.MASONRY.id | Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.pistonBase, 1), "TTT", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.ingotIron, 'R', Item.redstone, 'T', Block.planks).setSkillset(Skill.BLACKSMITHING.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.pistonStickyBase, 1), "S", "P", 'S', Item.slimeBall, 'P', Block.pistonBase);
      this.addRecipe(new ItemStack(Item.bed, 1), "###", "XXX", '#', Block.cloth, 'X', Block.planks).setSkillset(Skill.CARPENTRY.id);
      this.addRecipe(new ItemStack(Block.enchantmentTable, 1), " B ", "D#D", "###", '#', Block.obsidian, 'B', Item.book, 'D', Item.diamond).setSkillset(Skill.MASONRY.id + Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.enchantmentTableEmerald, 1), " B ", "E#E", "###", '#', Block.obsidian, 'B', Item.book, 'E', Item.emerald).setSkillset(Skill.MASONRY.id + Skill.FINE_ARTS.id);
      this.addRecipe(new ItemStack(Block.anvil, 1), "III", " i ", "iii", 'I', Block.blockIron, 'i', Item.ingotIron).setSkillset(Skill.BLACKSMITHING.id);
      this.addShapelessRecipe(new ItemStack(Item.fireballCharge, 3), Item.gunpowder, Item.blazePowder, Item.coal).setSkillset(Skill.TINKERING.id);
      this.addShapelessRecipe(new ItemStack(Item.fireballCharge, 3), Item.gunpowder, Item.blazePowder, new ItemStack(Item.coal, 1, 1)).setSkillset(Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.daylightSensor), "GGG", "QQQ", "WWW", 'G', Block.glass, 'Q', Item.netherQuartz, 'W', Block.woodSingleSlab).setSkillset(Skill.CARPENTRY.id + Skill.TINKERING.id);
      this.addRecipe(new ItemStack(Block.hopperBlock), "I I", "ICI", " I ", 'I', Item.ingotIron, 'C', Block.chest).setSkillset(Skill.BLACKSMITHING.id);
      Collections.sort(this.recipes, new RecipeSorter(this));
      Item.verifyThatAllItemsHaveMaterialsDefined();

      for(int i = 0; i < Item.itemsList.length; ++i) {
         Item item = Item.itemsList[i];
         if (item != null) {
            int num_subtypes = item.getNumSubtypes();
            if (num_subtypes == 0) {
               this.checkRecipe(item, 0);
            } else {
               for(int subtype = 0; subtype < num_subtypes; ++subtype) {
                  this.checkRecipe(item, subtype);
               }
            }
         }
      }

   }

   private void checkRecipe(Item item, int subtype_or_0) {
      if ((item.isCraftingProduct() || item.isRepairable()) && item.getLowestCraftingDifficultyToProduce() == Float.MAX_VALUE) {
         if (item.hasMaterial(Material.rusted_iron)) {
            Object peer;
            if (item instanceof ItemArmor) {
               ItemArmor var10000 = (ItemArmor)item;
               peer = ItemArmor.getMatchingArmor(item.getClass(), Material.copper, ((ItemArmor)item).isChainMail());
            } else {
               peer = Item.getMatchingItem(item.getClass(), Material.copper);
            }

            if (peer != null) {
               item.setLowestCraftingDifficultyToProduce(((Item)peer).getLowestCraftingDifficultyToProduce());
            }
         }

         if (item.getLowestCraftingDifficultyToProduce() == Float.MAX_VALUE) {
            Minecraft.setErrorMessage("Warning: " + item.getItemDisplayName((ItemStack)null) + " [" + item.itemID + "] is " + (item.isCraftingComponent(subtype_or_0) ? "a crafting product" : "repairable") + " but its lowest_crafting_difficulty_to_produce cannot be determined");
         }
      }

      if (item.isCraftingComponent(subtype_or_0) && item.getCraftingDifficultyAsComponent(new ItemStack(item, 1, subtype_or_0)) < 0.0F) {
         float lowest_crafting_difficulty_to_produce = item.getLowestCraftingDifficultyToProduce();
         if (lowest_crafting_difficulty_to_produce != Float.MAX_VALUE) {
            item.setCraftingDifficultyAsComponent(lowest_crafting_difficulty_to_produce);
         } else {
            Minecraft.setErrorMessage("Warning: " + item.getItemDisplayName((ItemStack)null) + " [" + item.itemID + "] is a crafting component but its crafting_difficulty_as_component has not been set");
         }
      }

   }

   ShapedRecipes addRecipe(ItemStack par1ItemStack, Object... par2ArrayOfObj) {
      return this.addRecipe(par1ItemStack, true, par2ArrayOfObj);
   }

   ShapedRecipes addRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj) {
      String var3 = "";
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      if (par2ArrayOfObj[var4] instanceof String[]) {
         String[] var7 = (String[])((String[])((String[])par2ArrayOfObj[var4++]));

         for(int var8 = 0; var8 < var7.length; ++var8) {
            String var9 = var7[var8];
            ++var6;
            var5 = var9.length();
            var3 = var3 + var9;
         }
      } else {
         while(par2ArrayOfObj[var4] instanceof String) {
            String var11 = (String)par2ArrayOfObj[var4++];
            ++var6;
            var5 = var11.length();
            var3 = var3 + var11;
         }
      }

      HashMap var12;
      for(var12 = new HashMap(); var4 < par2ArrayOfObj.length; var4 += 2) {
         Character var13 = (Character)par2ArrayOfObj[var4];
         ItemStack var14 = null;
         if (par2ArrayOfObj[var4 + 1] instanceof Item) {
            var14 = new ItemStack((Item)par2ArrayOfObj[var4 + 1]);
         } else if (par2ArrayOfObj[var4 + 1] instanceof Block) {
            var14 = new ItemStack((Block)par2ArrayOfObj[var4 + 1], 1, 32767);
         } else if (par2ArrayOfObj[var4 + 1] instanceof ItemStack) {
            var14 = (ItemStack)par2ArrayOfObj[var4 + 1];
         } else {
            Minecraft.setErrorMessage("Invalid recipe component for " + par1ItemStack.getDisplayName());
         }

         var12.put(var13, var14);
      }

      ItemStack[] var15 = new ItemStack[var5 * var6];

      for(int var16 = 0; var16 < var5 * var6; ++var16) {
         char var10 = var3.charAt(var16);
         if (var12.containsKey(var10)) {
            var15[var16] = ((ItemStack)var12.get(var10)).copy();
         } else {
            var15[var16] = null;
         }
      }

      ShapedRecipes var17 = new ShapedRecipes(var5, var6, var15, par1ItemStack, include_in_lowest_crafting_difficulty_determination);
      this.recipes.add(var17);
      return var17;
   }

   ShapelessRecipes addShapelessRecipe(ItemStack par1ItemStack, Object... par2ArrayOfObj) {
      return this.addShapelessRecipe(par1ItemStack, true, par2ArrayOfObj);
   }

   ShapelessRecipes addShapelessRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj) {
      ArrayList var3 = new ArrayList();
      Object[] var4 = par2ArrayOfObj;
      int var5 = par2ArrayOfObj.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var4[var6];
         if (var7 instanceof ItemStack) {
            ItemStack item_stack = (ItemStack)var7;

            for(int i = 0; i < item_stack.stackSize; ++i) {
               var3.add(((ItemStack)var7).copy().setStackSize(1));
            }
         } else if (var7 instanceof Item) {
            var3.add(new ItemStack((Item)var7));
         } else {
            if (!(var7 instanceof Block)) {
               throw new RuntimeException("Invalid shapeless recipy!");
            }

            var3.add(new ItemStack((Block)var7));
         }
      }

      ShapelessRecipes recipe = new ShapelessRecipes(par1ItemStack, var3, include_in_lowest_crafting_difficulty_determination);
      this.recipes.add(recipe);
      return recipe;
   }

   public static int getResultingDurabilityFromCombiningItems(ItemStack first, ItemStack second) {
      int max_damage = first.getMaxDamage();
      int durability_on_first_item_stack = max_damage - first.getItemDamage();
      int durability_on_second_item_stack = max_damage - second.getItemDamage();
      int resulting_durability = durability_on_first_item_stack + durability_on_second_item_stack;
      if (resulting_durability > max_damage) {
         resulting_durability = max_damage;
      }

      return max_damage - resulting_durability;
   }

   public CraftingResult findMatchingRecipe(InventoryCrafting par1InventoryCrafting, World par2World, EntityPlayer player) {
      if (player != null && player.openContainer != null) {
         player.openContainer.repair_fail_condition = 0;
      }

      int var3 = 0;
      ItemStack var4 = null;
      ItemStack var5 = null;

      int var6;
      ItemStack item_stack_sinew;
      for(var6 = 0; var6 < par1InventoryCrafting.getSizeInventory(); ++var6) {
         item_stack_sinew = par1InventoryCrafting.getStackInSlot(var6);
         if (item_stack_sinew != null) {
            if (var3 == 0) {
               var4 = item_stack_sinew;
            }

            if (var3 == 1) {
               var5 = item_stack_sinew;
            }

            ++var3;
         }
      }

      ItemStack item_stack_armor;
      CraftingResult crafting_result;
      if (var3 == 2 && var4.itemID == var5.itemID && var4.stackSize == 1 && var5.stackSize == 1 && Item.itemsList[var4.itemID].isRepairable()) {
         if (var4.isItemDamaged() && var5.isItemDamaged()) {
            if (var4.getQuality() != var5.getQuality()) {
               return null;
            } else if (!var4.isItemEnchanted() && !var5.isItemEnchanted()) {
               if (var4.isDyed() || var5.isDyed()) {
                  if (!var4.isDyed() || !var5.isDyed()) {
                     return null;
                  }

                  if (var4.getDyedColor() != var5.getDyedColor()) {
                     return null;
                  }
               }

               float crafting_difficulty = var4.getItem().getLowestCraftingDifficultyToProduce();
               if (var4.getItem().hasQuality() && player != null && var4.getQuality().isHigherThan(player.getMaxCraftingQuality(crafting_difficulty, var4.getItem(), var4.getItem().getSkillsetsThatCanRepairThis()))) {
                  player.openContainer.repair_fail_condition = 1;
               }

               item_stack_armor = (new ItemStack(var4.itemID, 1, var4.getItemSubtype())).setItemDamage(getResultingDurabilityFromCombiningItems(var4, var5));
               if (var4.isDyed()) {
                  item_stack_armor.copyDyedColor(var4);
               }

               crafting_result = (new CraftingResult(item_stack_armor, crafting_difficulty / 2.0F, var4.getItem().getSkillsetsThatCanRepairThis(), (IRecipe)null)).setExperienceCostExempt().setQualityOverride(var4.getQuality());
               crafting_result.setRepair();
               return crafting_result;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else if (var3 != 2 || var4.getItem() != Item.sinew && var5.getItem() != Item.sinew && var4.getItem() != Item.silk && var5.getItem() != Item.silk || (!(var4.getItem() instanceof ItemArmor) || !((ItemArmor)var4.getItem()).isLeather() || var4.stackSize != 1 || !var4.isItemDamaged()) && (!(var5.getItem() instanceof ItemArmor) || !((ItemArmor)var5.getItem()).isLeather() || var5.stackSize != 1 || !var5.isItemDamaged())) {
         Container event_handler = par1InventoryCrafting.getEventHandler();

         for(var6 = 0; var6 < this.recipes.size(); ++var6) {
            IRecipe var12 = (IRecipe)this.recipes.get(var6);
            if (var12.matches(par1InventoryCrafting, par2World) && (!(event_handler instanceof MITEContainerCrafting) || !((MITEContainerCrafting)event_handler).isRecipeForbidden(var12))) {
               crafting_result = var12.getCraftingResult(par1InventoryCrafting);
               if (crafting_result == null) {
                  return null;
               } else {
                  return event_handler instanceof MITEContainerCrafting && ((MITEContainerCrafting)event_handler).isCraftingResultForbidden(crafting_result) ? null : crafting_result;
               }
            }
         }

         return null;
      } else {
         if (var4.getItem() != Item.sinew && var4.getItem() != Item.silk) {
            item_stack_sinew = var5;
            item_stack_armor = var4;
         } else {
            item_stack_sinew = var4;
            item_stack_armor = var5;
         }

         if (item_stack_armor.getItem().hasQuality() && player != null && item_stack_armor.getQuality().isHigherThan(player.getMaxCraftingQuality(item_stack_armor.getItem().getLowestCraftingDifficultyToProduce(), item_stack_armor.getItem(), item_stack_armor.getItem().getSkillsetsThatCanRepairThis()))) {
            return null;
         } else {
            int damage = item_stack_armor.getItemDamage();
            int damage_repaired_per_sinew = item_stack_armor.getMaxDamage() / item_stack_armor.getItem().getRepairCost();
            int num_sinews_to_use = damage / damage_repaired_per_sinew;
            if (damage % damage_repaired_per_sinew != 0) {
               ++num_sinews_to_use;
            }

            if (num_sinews_to_use > 1 && num_sinews_to_use * damage_repaired_per_sinew > damage) {
               --num_sinews_to_use;
            }

            if (num_sinews_to_use > item_stack_sinew.stackSize) {
               num_sinews_to_use = item_stack_sinew.stackSize;
            }

            int damage_repaired = num_sinews_to_use * damage_repaired_per_sinew;
            int damage_after_repair = Math.max(damage - damage_repaired, 0);
            ItemStack resulting_stack = item_stack_armor.copy().setItemDamage(damage_after_repair);
            CraftingResult result = (new CraftingResult(resulting_stack, (float)(num_sinews_to_use * 50), item_stack_armor.getItem().getSkillsetsThatCanRepairThis(), (IRecipe)null)).setExperienceCostExempt().setQualityOverride(item_stack_armor.getQuality()).setConsumption(num_sinews_to_use);
            result.setRepair();
            return result;
         }
      }
   }

   public List getRecipeList() {
      return this.recipes;
   }

   private int getDefaultSkillsetForItem(Item item) {
      return item.hasMaterial(Material.wood, true) ? Skill.CARPENTRY.id : (item.containsCrystal() ? Skill.FINE_ARTS.id : (item.containsRockyMineral() ? Skill.MASONRY.id : (item.containsMetal() ? Skill.BLACKSMITHING.id : 0)));
   }

   public void addRecipes(Object[][] recipe_table, int skillset_override) {
      Object[] item_for_character = (Object[])((Object[])recipe_table[0][recipe_table[0].length - 1]);

      for(int material_index = 0; material_index < recipe_table[0].length - 1; ++material_index) {
         Object material = recipe_table[0][material_index];

         for(int line_index = 1; line_index < recipe_table.length; ++line_index) {
            Object[] line = recipe_table[line_index];
            Object item_or_block = line[material_index];
            Item item = item_or_block instanceof Item ? (Item)item_or_block : Item.getItem((Block)item_or_block);
            if (item != null) {
               Object pattern = line[recipe_table[0].length - 1];
               int quantity = line.length > recipe_table[0].length ? (Integer)line[line.length - 1] : 1;
               int skillset = skillset_override >= 0 ? skillset_override : this.getDefaultSkillsetForItem(item);
               Object[] pattern_and_items_map = new Object[3 + (item_for_character == null ? 0 : item_for_character.length)];
               pattern_and_items_map[0] = pattern;
               pattern_and_items_map[1] = '?';
               pattern_and_items_map[2] = material;
               if (item_for_character != null) {
                  for(int i = 0; i < item_for_character.length; ++i) {
                     pattern_and_items_map[i + 3] = item_for_character[i];
                  }
               }

               this.addRecipe(new ItemStack(item, quantity), pattern_and_items_map).setSkillset(skillset);
            }
         }
      }

   }

   public void addShapelessRecipes(Object[][] recipe_table, int skillset_override, boolean propagate_tag_compound) {
      this.addShapelessRecipes(recipe_table, skillset_override, propagate_tag_compound, true);
   }

   public void addShapelessRecipes(Object[][] recipe_table, int skillset_override, boolean propagate_tag_compound, boolean include_in_lowest_crafting_difficulty_determination) {
      for(int material_index = 0; material_index < recipe_table[0].length; ++material_index) {
         Object material = recipe_table[0][material_index];

         for(int line_index = 1; line_index < recipe_table.length; ++line_index) {
            Object[] line = recipe_table[line_index];
            Item item = (Item)line[material_index];
            if (item != null) {
               Object[] constant_items = (Object[])((Object[])line[recipe_table[0].length]);
               int quantity = line.length > recipe_table[0].length + 1 ? (Integer)line[recipe_table[0].length + 1] : 1;
               if (skillset_override < 0) {
                  this.getDefaultSkillsetForItem(item);
               }

               Object[] items;
               if (constant_items == null) {
                  items = new Object[1];
               } else {
                  items = new Object[1 + constant_items.length];

                  for(int i = 0; i < constant_items.length; ++i) {
                     items[i + 1] = constant_items[i];
                  }
               }

               items[0] = material;
               ShapelessRecipes shapeless_recipe = this.addShapelessRecipe(new ItemStack(item, quantity), include_in_lowest_crafting_difficulty_determination, items);
               if (propagate_tag_compound) {
                  shapeless_recipe.propagateTagCompound();
               }

               if (line.length > recipe_table[0].length + 2) {
                  shapeless_recipe.setDifficulty((Integer)line[recipe_table[0].length + 2]);
               }
            }
         }
      }

   }
}
