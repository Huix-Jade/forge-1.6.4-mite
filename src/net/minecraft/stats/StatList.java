package net.minecraft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;

public class StatList {
   protected static Map oneShotStats = new HashMap();
   public static List allStats = new ArrayList();
   public static List generalStats = new ArrayList();
   public static List itemStats = new ArrayList();
   public static List objectMineStats = new ArrayList();
   public static StatBase startGameStat = (new StatBasic(1000, "stat.startGame")).initIndependentStat().registerStat();
   public static StatBase createWorldStat = (new StatBasic(1001, "stat.createWorld")).initIndependentStat().registerStat();
   public static StatBase loadWorldStat = (new StatBasic(1002, "stat.loadWorld")).initIndependentStat().registerStat();
   public static StatBase joinMultiplayerStat = (new StatBasic(1003, "stat.joinMultiplayer")).initIndependentStat().registerStat();
   public static StatBase leaveGameStat = (new StatBasic(1004, "stat.leaveGame")).initIndependentStat().registerStat();
   public static StatBase minutesPlayedStat;
   public static StatBase distanceWalkedStat;
   public static StatBase distanceSwumStat;
   public static StatBase distanceFallenStat;
   public static StatBase distanceClimbedStat;
   public static StatBase distanceFlownStat;
   public static StatBase distanceDoveStat;
   public static StatBase distanceByMinecartStat;
   public static StatBase distanceByBoatStat;
   public static StatBase distanceByPigStat;
   public static StatBase jumpStat;
   public static StatBase dropStat;
   public static StatBase damageDealtStat;
   public static StatBase damageTakenStat;
   public static StatBase deathsStat;
   public static StatBase mobKillsStat;
   public static StatBase playerKillsStat;
   public static StatBase fishCaughtStat;
   public static StatBase[] mineBlockStatArray;
   public static StatBase[] objectCraftStats;
   public static StatBase[] objectUseStats;
   public static StatBase[] objectBreakStats;
   private static boolean blockStatsInitialized;
   private static boolean itemStatsInitialized;

   public static void nopInit() {
   }

   public static void initBreakableStats() {
      objectUseStats = initUsableStats(objectUseStats, "stat.useItem", 16908288, 0, 256);
      objectBreakStats = initBreakStats(objectBreakStats, "stat.breakItem", 16973824, 0, 256);
      blockStatsInitialized = true;
      initCraftableStats();
   }

   public static void initStats() {
      objectUseStats = initUsableStats(objectUseStats, "stat.useItem", 16908288, 256, 32000);
      objectBreakStats = initBreakStats(objectBreakStats, "stat.breakItem", 16973824, 256, 32000);
      itemStatsInitialized = true;
      initCraftableStats();
   }

   public static void initCraftableStats() {
      if (blockStatsInitialized && itemStatsInitialized) {
         HashSet var0 = new HashSet();
         Iterator var1 = CraftingManager.getInstance().getRecipeList().iterator();

         while(var1.hasNext()) {
            IRecipe var2 = (IRecipe)var1.next();
            if (var2.getRecipeOutput() != null) {
               var0.add(var2.getRecipeOutput().itemID);
            }
         }

         var1 = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

         while(var1.hasNext()) {
            ItemStack var4 = (ItemStack)var1.next();
            var0.add(var4.itemID);
         }

         objectCraftStats = new StatBase[32000];
         var1 = var0.iterator();

         while(var1.hasNext()) {
            Integer var5 = (Integer)var1.next();
            if (Item.itemsList[var5] != null) {
               String var3 = StatCollector.translateToLocalFormatted("stat.craftItem", Item.itemsList[var5].getStatName());
               objectCraftStats[var5] = (new StatCrafting(16842752 + var5, var3, var5)).registerStat();
            }
         }

         replaceAllSimilarBlocks(objectCraftStats);
      }

   }

   private static StatBase[] initMinableStats(String par0Str, int par1) {
      StatBase[] var2 = new StatBase[256];

      for(int var3 = 0; var3 < 256; ++var3) {
         if (Block.blocksList[var3] != null && Block.blocksList[var3].getEnableStats()) {
            String var4 = StatCollector.translateToLocalFormatted(par0Str, Block.blocksList[var3].getLocalizedName());
            var2[var3] = (new StatCrafting(par1 + var3, var4, var3)).registerStat();
            objectMineStats.add((StatCrafting)var2[var3]);
         }
      }

      replaceAllSimilarBlocks(var2);
      return var2;
   }

   private static StatBase[] initUsableStats(StatBase[] par0ArrayOfStatBase, String par1Str, int par2, int par3, int par4) {
      if (par0ArrayOfStatBase == null) {
         par0ArrayOfStatBase = new StatBase[32000];
      }

      for(int var5 = par3; var5 < par4; ++var5) {
         if (Item.itemsList[var5] != null) {
            String var6 = StatCollector.translateToLocalFormatted(par1Str, Item.itemsList[var5].getStatName());
            par0ArrayOfStatBase[var5] = (new StatCrafting(par2 + var5, var6, var5)).registerStat();
            if (var5 >= 256) {
               itemStats.add((StatCrafting)par0ArrayOfStatBase[var5]);
            }
         }
      }

      replaceAllSimilarBlocks(par0ArrayOfStatBase);
      return par0ArrayOfStatBase;
   }

   private static StatBase[] initBreakStats(StatBase[] par0ArrayOfStatBase, String par1Str, int par2, int par3, int par4) {
      if (par0ArrayOfStatBase == null) {
         par0ArrayOfStatBase = new StatBase[32000];
      }

      for(int var5 = par3; var5 < par4; ++var5) {
         if (Item.itemsList[var5] != null && Item.itemsList[var5].isDamageable()) {
            String var6 = StatCollector.translateToLocalFormatted(par1Str, Item.itemsList[var5].getStatName());
            par0ArrayOfStatBase[var5] = (new StatCrafting(par2 + var5, var6, var5)).registerStat();
         }
      }

      replaceAllSimilarBlocks(par0ArrayOfStatBase);
      return par0ArrayOfStatBase;
   }

   private static void replaceAllSimilarBlocks(StatBase[] par0ArrayOfStatBase) {
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.waterStill.blockID, Block.waterMoving.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.lavaStill.blockID, Block.lavaStill.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.pumpkinLantern.blockID, Block.pumpkin.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.furnaceBurning.blockID, Block.furnaceIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.oreRedstoneGlowing.blockID, Block.oreRedstone.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.redstoneRepeaterActive.blockID, Block.redstoneRepeaterIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.torchRedstoneActive.blockID, Block.torchRedstoneIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.mushroomRed.blockID, Block.mushroomBrown.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.stoneDoubleSlab.blockID, Block.stoneSingleSlab.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.woodDoubleSlab.blockID, Block.woodSingleSlab.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.grass.blockID, Block.dirt.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.tilledField.blockID, Block.dirt.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.furnaceClayBurning.blockID, Block.furnaceClayIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.furnaceSandstoneBurning.blockID, Block.furnaceSandstoneIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.furnaceObsidianBurning.blockID, Block.furnaceObsidianIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.furnaceNetherrackBurning.blockID, Block.furnaceNetherrackIdle.blockID);
      replaceSimilarBlocks(par0ArrayOfStatBase, Block.obsidianDoubleSlab.blockID, Block.obsidianSingleSlab.blockID);
   }

   private static void replaceSimilarBlocks(StatBase[] par0ArrayOfStatBase, int par1, int par2) {
      if (par0ArrayOfStatBase[par1] != null && par0ArrayOfStatBase[par2] == null) {
         par0ArrayOfStatBase[par2] = par0ArrayOfStatBase[par1];
      } else {
         allStats.remove(par0ArrayOfStatBase[par1]);
         objectMineStats.remove(par0ArrayOfStatBase[par1]);
         generalStats.remove(par0ArrayOfStatBase[par1]);
         par0ArrayOfStatBase[par1] = par0ArrayOfStatBase[par2];
      }

   }

   public static StatBase getOneShotStat(int par0) {
      return (StatBase)oneShotStats.get(par0);
   }

   public static StatBase getStat(int id) {
      return getOneShotStat(id);
   }

   public static boolean isEitherZeroOrOne(StatBase stat) {
      return stat.isAchievement();
   }

   public static boolean hasLongValue(StatBase stat) {
      return stat.getType() == StatBase.distanceStatType || stat == minutesPlayedStat;
   }

   static {
      minutesPlayedStat = (new StatBasic(1100, "stat.playOneMinute", StatBase.timeStatType)).initIndependentStat().registerStat();
      distanceWalkedStat = (new StatBasic(2000, "stat.walkOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceSwumStat = (new StatBasic(2001, "stat.swimOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceFallenStat = (new StatBasic(2002, "stat.fallOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceClimbedStat = (new StatBasic(2003, "stat.climbOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceFlownStat = (new StatBasic(2004, "stat.flyOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceDoveStat = (new StatBasic(2005, "stat.diveOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceByMinecartStat = (new StatBasic(2006, "stat.minecartOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceByBoatStat = (new StatBasic(2007, "stat.boatOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      distanceByPigStat = (new StatBasic(2008, "stat.pigOneCm", StatBase.distanceStatType)).initIndependentStat().registerStat();
      jumpStat = (new StatBasic(2010, "stat.jump")).initIndependentStat().registerStat();
      dropStat = (new StatBasic(2011, "stat.drop")).initIndependentStat().registerStat();
      damageDealtStat = (new StatBasic(2020, "stat.damageDealt", StatBase.field_111202_k)).registerStat();
      damageTakenStat = (new StatBasic(2021, "stat.damageTaken", StatBase.field_111202_k)).registerStat();
      deathsStat = (new StatBasic(2022, "stat.deaths")).registerStat();
      mobKillsStat = (new StatBasic(2023, "stat.mobKills")).registerStat();
      playerKillsStat = (new StatBasic(2024, "stat.playerKills")).registerStat();
      fishCaughtStat = (new StatBasic(2025, "stat.fishCaught")).registerStat();
      mineBlockStatArray = initMinableStats("stat.mineBlock", 16777216);
      AchievementList.init();
   }
}
