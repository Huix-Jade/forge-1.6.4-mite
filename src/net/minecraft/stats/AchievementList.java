package net.minecraft.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AchievementList {
   public static int minDisplayColumn;
   public static int minDisplayRow;
   public static int maxDisplayColumn;
   public static int maxDisplayRow;
   public static List achievementList = new ArrayList();
   public static Achievement openInventory;
   public static Achievement stickPicker;
   public static Achievement cuttingEdge;
   public static Achievement mineWood;
   public static Achievement buildWorkBench;
   public static Achievement buildShovel;
   public static Achievement nuggets;
   public static Achievement betterTools;
   public static Achievement buildPickaxe;
   public static Achievement buildFurnace;
   public static Achievement acquireIron;
   public static Achievement buildHoe;
   public static Achievement flour;
   public static Achievement makeBread;
   public static Achievement bakeCake;
   public static Achievement buildBetterPickaxe;
   public static Achievement onARail;
   public static Achievement buildClub;
   public static Achievement killEnemy;
   public static Achievement killCow;
   public static Achievement flyPig;
   public static Achievement snipeSkeleton;
   public static Achievement obsidianFurnace;
   public static Achievement mithrilIngot;
   public static Achievement diamonds;
   public static Achievement portal;
   public static Achievement portalToNether;
   public static Achievement ghast;
   public static Achievement blazeRod;
   public static Achievement potion;
   public static Achievement theEnd;
   public static Achievement theEnd2;
   public static Achievement enchantments;
   public static Achievement overkill;
   public static Achievement bookcase;
   public static Achievement seeds;
   public static Achievement eggs;
   public static Achievement buildAxe;
   public static Achievement buildScythe;
   public static Achievement wearLeather;
   public static Achievement buildChainMail;
   public static Achievement wearAllPlateArmor;
   public static Achievement wearAllAdamantiumPlateArmor;
   public static Achievement buildOven;
   public static Achievement flintFinder;
   public static Achievement buildTorches;
   public static Achievement soilEnrichment;
   public static Achievement wellRested;
   public static Achievement seaworthy;
   public static Achievement explorer;
   public static Achievement enlightenment;
   public static Achievement runegate;
   public static Achievement fishingRod;
   public static Achievement cookFish;
   public static Achievement plantDoctor;
   public static Achievement makeMycelium;
   public static Achievement supersizeMe;
   public static Achievement emeralds;
   public static Achievement netherrackFurnace;
   public static Achievement adamantiumIngot;
   public static Achievement fineDining;
   public static Achievement crystalBreaker;

   public static void init() {
   }

   public static Achievement getAchievementForId(int id) {
      id += 5242880;
      Iterator i = achievementList.iterator();

      Achievement achievement;
      do {
         if (!i.hasNext()) {
            return null;
         }

         achievement = (Achievement)i.next();
      } while(achievement.statId != id);

      return achievement;
   }

   public static void clearAchievements() {
      Iterator i = achievementList.iterator();

      while(i.hasNext()) {
         Minecraft.theMinecraft.statFileWriter.clearAchievement((Achievement)i.next());
      }

   }

   static {
      openInventory = (new Achievement(0, "openInventory", -2, 0, Item.book, (Achievement)null)).setIndependent().registerAchievement();
      stickPicker = (new Achievement(102, "stickPicker", 0, 1, Item.stick, openInventory)).setFlipped().registerAchievement();
      cuttingEdge = (new Achievement(103, "cuttingEdge", 2, 1, Item.hatchetFlint, stickPicker)).registerAchievement();
      mineWood = (new Achievement(1, "mineWood", 2, -1, Block.wood, cuttingEdge)).setTooltipWidth(130).registerAchievement();
      buildWorkBench = (new Achievement(2, "buildWorkBench", 4, -1, Block.workbench, mineWood)).registerAchievement();
      buildShovel = (new Achievement(106, "buildShovel", 4, 1, Item.shovelWood, buildWorkBench)).registerAchievement();
      nuggets = (new Achievement(107, "nuggets", 4, 3, Item.copperNugget, buildShovel)).registerAchievement();
      betterTools = (new Achievement(135, "betterTools", 6, 3, new ItemStack(Block.workbench, 1, 4), nuggets)).setTooltipWidth(125).registerAchievement();
      buildPickaxe = (new Achievement(3, "buildPickaxe", 5, 5, Item.pickaxeCopper, betterTools)).registerAchievement();
      buildFurnace = (new Achievement(4, "buildFurnace", 5, 7, Block.furnaceIdle, buildPickaxe)).registerAchievement();
      acquireIron = (new Achievement(5, "acquireIron", 3, 7, Item.ingotIron, buildFurnace)).registerAchievement();
      buildHoe = (new Achievement(6, "buildHoe", 7, 6, Item.hoeCopper, betterTools)).registerAchievement();
      flour = (new Achievement(126, "flour", 7, 8, Item.flour, buildHoe)).registerAchievement();
      makeBread = (new Achievement(7, "makeBread", 6, 9, Item.bread, flour)).registerAchievement();
      bakeCake = (new Achievement(8, "bakeCake", 8, 10, Item.cake, flour)).registerAchievement();
      buildBetterPickaxe = (new Achievement(9, "buildBetterPickaxe", 1, 7, Item.pickaxeIron, acquireIron)).registerAchievement();
      onARail = (new Achievement(11, "onARail", 3, 5, Block.rail, acquireIron)).setSpecial().registerAchievement();
      buildClub = (new Achievement(104, "buildClub", 6, -1, Item.clubWood, buildWorkBench)).registerAchievement();
      killEnemy = (new Achievement(13, "killEnemy", 8, -1, Item.bone, buildClub)).registerAchievement();
      killCow = (new Achievement(14, "killCow", 7, -3, Item.leather, buildClub)).registerAchievement();
      flyPig = (new Achievement(15, "flyPig", 7, -5, Item.saddle, killCow)).setSpecial().registerAchievement();
      snipeSkeleton = (new Achievement(16, "snipeSkeleton", 10, -1, Item.bow, killEnemy)).setSpecial().registerAchievement();
      obsidianFurnace = (new Achievement(128, "obsidianFurnace", 0, 9, Block.furnaceObsidianIdle, buildBetterPickaxe)).registerAchievement();
      mithrilIngot = (new Achievement(129, "mithrilIngot", -2, 9, Item.ingotMithril, obsidianFurnace)).registerAchievement();
      diamonds = (new Achievement(17, "diamonds", -3, 7, Item.diamond, mithrilIngot)).setFlipped().registerAchievement();
      portal = (new Achievement(18, "portal", 2, 9, Block.obsidian, buildBetterPickaxe)).registerAchievement();
      portalToNether = (new Achievement(134, "portalToNether", 2, 11, Block.mantleOrCore, portal)).registerAchievement();
      ghast = (new Achievement(19, "ghast", 3, 13, Item.ghastTear, portalToNether)).setSpecial().registerAchievement();
      blazeRod = (new Achievement(20, "blazeRod", 1, 13, Item.blazeRod, portalToNether)).registerAchievement();
      potion = (new Achievement(21, "potion", -1, 13, Item.potion, blazeRod)).registerAchievement();
      theEnd = (new Achievement(22, "theEnd", 3, 16, Item.eyeOfEnder, blazeRod)).setSpecial().registerAchievement();
      theEnd2 = (new Achievement(23, "theEnd2", 6, 16, Block.dragonEgg, theEnd)).setSpecial().registerAchievement();
      enchantments = (new Achievement(24, "enchantments", -2, 5, Block.enchantmentTable, diamonds)).setFlipped().registerAchievement();
      overkill = (new Achievement(30, "overkill", 0, 5, Item.swordMithril, enchantments)).setFlipped().setSpecial().registerAchievement();
      bookcase = (new Achievement(26, "bookcase", -4, 5, Block.bookShelf, enchantments)).setFlipped().registerAchievement();
      seeds = (new Achievement(100, "seeds", -2, -2, Item.seeds, openInventory)).registerAchievement();
      eggs = (new Achievement(101, "eggs", -1, -3, Item.egg, seeds)).registerAchievement();
      buildAxe = (new Achievement(105, "buildAxe", 5, -3, Item.axeFlint, buildWorkBench)).registerAchievement();
      buildScythe = (new Achievement(108, "buildScythe", 8, 5, Item.scytheCopper, buildHoe)).registerAchievement();
      wearLeather = (new Achievement(109, "wearLeather", 9, -3, Item.plateLeather, killCow)).registerAchievement();
      buildChainMail = (new Achievement(110, "buildChainMail", 6, 1, Item.plateChainIron, betterTools)).registerAchievement();
      wearAllPlateArmor = (new Achievement(111, "wearAllPlateArmor", 8, 1, Item.plateIron, buildChainMail)).registerAchievement();
      wearAllAdamantiumPlateArmor = (new Achievement(112, "wearAllAdamantiumPlateArmor", 10, 1, Item.plateAdamantium, wearAllPlateArmor)).setSpecial().registerAchievement();
      buildOven = (new Achievement(113, "buildOven", -2, 2, Block.furnaceClayIdle, openInventory)).registerAchievement();
      flintFinder = (new Achievement(114, "flintFinder", 0, -1, Item.flint, openInventory)).setFlipped().registerAchievement();
      buildTorches = (new Achievement(115, "buildTorches", 5, -5, Block.torchWood, buildWorkBench)).registerAchievement();
      soilEnrichment = (new Achievement(116, "soilEnrichment", 9, 7, Item.manure, buildHoe)).setFlipped().registerAchievement();
      wellRested = (new Achievement(117, "wellRested", 3, -5, Item.bed, buildWorkBench)).registerAchievement();
      seaworthy = (new Achievement(118, "seaworthy", 4, -7, Item.boat, buildWorkBench)).registerAchievement();
      explorer = (new Achievement(119, "explorer", -4, 0, Item.bootsLeather, openInventory)).setSpecial().registerAchievement();
      enlightenment = (new Achievement(120, "enlightenment", -5, 6, Item.book, bookcase)).setFlipped().setSpecial().registerAchievement();
      runegate = (new Achievement(121, "runegate", 4, 9, Block.runestoneMithril, portal)).setSpecial().registerAchievement();
      fishingRod = (new Achievement(122, "fishingRod", 8, 3, Item.fishingRodCopper, betterTools)).registerAchievement();
      cookFish = (new Achievement(10, "cookFish", 10, 3, Item.fishCooked, fishingRod)).registerAchievement();
      plantDoctor = (new Achievement(123, "plantDoctor", 10, 5, new ItemStack(Item.dyePowder, 1, 15), buildHoe)).setFlipped().registerAchievement();
      makeMycelium = (new Achievement(124, "makeMycelium", 11, 7, Block.mycelium, soilEnrichment)).registerAchievement();
      supersizeMe = (new Achievement(125, "supersizeMe", 13, 7, new ItemStack(Block.mushroomCapBrown, 1, 5), makeMycelium)).registerAchievement();
      emeralds = (new Achievement(127, "emeralds", -1, 7, Item.emerald, buildBetterPickaxe)).registerAchievement();
      netherrackFurnace = (new Achievement(130, "netherrackFurnace", 0, 15, Block.furnaceNetherrackIdle, blazeRod)).registerAchievement();
      adamantiumIngot = (new Achievement(131, "adamantiumIngot", -2, 15, Item.ingotAdamantium, netherrackFurnace)).registerAchievement();
      fineDining = (new Achievement(132, "fineDining", 3, -3, Item.bowlSalad, buildWorkBench)).registerAchievement();
      crystalBreaker = (new Achievement(133, "crystalBreaker", -4, 15, Item.pickaxeAdamantium, adamantiumIngot)).setSpecial().registerAchievement();
      enchantments.setSecondParent(emeralds);
   }
}
