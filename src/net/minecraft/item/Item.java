package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockMounted;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.crafting.ComponentOfCraftingProductEntry;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public class Item {
   protected static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   private CreativeTabs tabToDisplayOn;
   public static Random itemRand = new Random();
   public static Item[] itemsList = new Item[32000];
   public static Item shovelIron;
   public static Item pickaxeIron;
   public static Item axeIron;
   public static Item flintAndSteel;
   public static Item appleRed;
   public static ItemBow bow;
   public static Item coal;
   public static Item diamond;
   public static Item ingotIron;
   public static Item ingotGold;
   public static ItemSword swordIron;
   public static Item shovelWood;
   public static Item stick;
   public static ItemBowl bowlEmpty;
   public static ItemBowl bowlMushroomStew;
   public static Item swordGold;
   public static Item shovelGold;
   public static Item pickaxeGold;
   public static Item axeGold;
   public static Item silk;
   public static Item feather;
   public static Item gunpowder;
   public static Item hoeIron;
   public static Item hoeGold;
   public static Item seeds;
   public static Item wheat;
   public static ItemFood bread;
   public static ItemHelmet helmetLeather;
   public static ItemCuirass plateLeather;
   public static ItemLeggings legsLeather;
   public static ItemBoots bootsLeather;
   public static ItemHelmet helmetChainIron;
   public static ItemCuirass plateChainIron;
   public static ItemLeggings legsChainIron;
   public static ItemBoots bootsChainIron;
   public static ItemHelmet helmetIron;
   public static ItemCuirass plateIron;
   public static ItemLeggings legsIron;
   public static ItemBoots bootsIron;
   public static ItemHelmet helmetGold;
   public static ItemCuirass plateGold;
   public static ItemLeggings legsGold;
   public static ItemBoots bootsGold;
   public static Item flint;
   public static ItemMeat porkRaw;
   public static ItemMeat porkCooked;
   public static Item painting;
   public static Item appleGold;
   public static Item sign;
   public static Item doorWood;
   public static ItemBucket bucketEmpty;
   public static ItemBucket bucketWater;
   public static ItemBucket bucketLava;
   public static Item minecartEmpty;
   public static Item saddle;
   public static Item doorIron;
   public static Item redstone;
   public static Item snowball;
   public static Item boat;
   public static Item leather;
   public static ItemBucketMilk bucketIronMilk;
   public static Item brick;
   public static Item clay;
   public static Item reed;
   public static Item paper;
   public static Item book;
   public static ItemGelatinousSphere slimeBall;
   public static Item minecartCrate;
   public static Item minecartPowered;
   public static Item egg;
   public static Item compass;
   public static ItemFishingRod fishingRodIron;
   public static Item pocketSundial;
   public static Item glowstone;
   public static ItemMeat fishRaw;
   public static ItemMeat fishCooked;
   public static Item dyePowder;
   public static Item bone;
   public static Item sugar;
   public static Item cake;
   public static Item bed;
   public static Item redstoneRepeater;
   public static Item cookie;
   public static ItemMap map;
   public static ItemShears shears;
   public static Item melon;
   public static Item pumpkinSeeds;
   public static Item melonSeeds;
   public static ItemMeat beefRaw;
   public static ItemMeat beefCooked;
   public static ItemMeat chickenRaw;
   public static ItemMeat chickenCooked;
   public static Item rottenFlesh;
   public static Item enderPearl;
   public static Item blazeRod;
   public static Item ghastTear;
   public static ItemNugget goldNugget;
   public static Item netherStalkSeeds;
   public static ItemPotion potion;
   public static Item glassBottle;
   public static Item spiderEye;
   public static Item fermentedSpiderEye;
   public static Item blazePowder;
   public static Item magmaCream;
   public static Item brewingStand;
   public static Item cauldron;
   public static Item eyeOfEnder;
   public static Item speckledMelon;
   public static Item monsterPlacer;
   public static Item expBottle;
   public static Item fireballCharge;
   public static Item writableBook;
   public static Item writtenBook;
   public static Item emerald;
   public static Item itemFrame;
   public static Item flowerPot;
   public static Item carrot;
   public static ItemFood potato;
   public static ItemFood bakedPotato;
   public static Item poisonousPotato;
   public static ItemEmptyMap emptyMap;
   public static Item goldenCarrot;
   public static Item skull;
   public static Item carrotOnAStickIron;
   public static Item netherStar;
   public static Item pumpkinPie;
   public static Item firework;
   public static Item fireworkCharge;
   public static ItemEnchantedBook enchantedBook;
   public static Item comparator;
   public static Item netherrackBrick;
   public static Item netherQuartz;
   public static Item minecartTnt;
   public static Item minecartHopper;
   public static ItemHorseArmor horseArmorIron;
   public static ItemHorseArmor horseArmorGold;
   public static Item leash;
   public static Item nameTag;
   public static ItemNugget copperNugget;
   public static ItemNugget silverNugget;
   public static ItemNugget ironNugget;
   public static ItemNugget mithrilNugget;
   public static ItemNugget adamantiumNugget;
   public static ItemNugget ancientMetalNugget;
   public static Item ingotCopper;
   public static Item ingotSilver;
   public static Item ingotMithril;
   public static Item ingotAdamantium;
   public static Item ingotAncientMetal;
   public static Item pickaxeCopper;
   public static Item pickaxeSilver;
   public static Item pickaxeMithril;
   public static Item pickaxeAdamantium;
   public static Item pickaxeRustedIron;
   public static Item pickaxeAncientMetal;
   public static Item shovelFlint;
   public static Item shovelCopper;
   public static Item shovelSilver;
   public static Item shovelMithril;
   public static Item shovelAdamantium;
   public static Item shovelRustedIron;
   public static Item shovelAncientMetal;
   public static Item hatchetFlint;
   public static Item axeFlint;
   public static Item axeCopper;
   public static Item axeSilver;
   public static Item axeMithril;
   public static Item axeAdamantium;
   public static Item axeRustedIron;
   public static Item axeAncientMetal;
   public static Item hoeCopper;
   public static Item hoeSilver;
   public static Item hoeMithril;
   public static Item hoeAdamantium;
   public static Item hoeRustedIron;
   public static Item hoeAncientMetal;
   public static Item warHammerCopper;
   public static Item warHammerSilver;
   public static Item warHammerGold;
   public static Item warHammerIron;
   public static Item warHammerMithril;
   public static Item warHammerAdamantium;
   public static Item warHammerRustedIron;
   public static Item warHammerAncientMetal;
   public static Item mattockCopper;
   public static Item mattockSilver;
   public static Item mattockGold;
   public static Item mattockIron;
   public static Item mattockMithril;
   public static Item mattockAdamantium;
   public static Item mattockRustedIron;
   public static Item mattockAncientMetal;
   public static Item battleAxeCopper;
   public static Item battleAxeSilver;
   public static Item battleAxeGold;
   public static Item battleAxeIron;
   public static Item battleAxeMithril;
   public static Item battleAxeAdamantium;
   public static Item battleAxeRustedIron;
   public static Item battleAxeAncientMetal;
   public static Item scytheCopper;
   public static Item scytheSilver;
   public static Item scytheGold;
   public static Item scytheIron;
   public static Item scytheMithril;
   public static Item scytheAdamantium;
   public static Item scytheRustedIron;
   public static Item scytheAncientMetal;
   public static ItemShears shearsCopper;
   public static ItemShears shearsSilver;
   public static ItemShears shearsGold;
   public static ItemShears shearsMithril;
   public static ItemShears shearsAdamantium;
   public static ItemShears shearsAncientMetal;
   public static Item knifeFlint;
   public static Item cudgelWood;
   public static Item clubWood;
   public static Item swordCopper;
   public static Item swordSilver;
   public static Item swordMithril;
   public static Item swordAdamantium;
   public static Item swordRustedIron;
   public static Item swordAncientMetal;
   public static Item daggerCopper;
   public static Item daggerSilver;
   public static Item daggerGold;
   public static Item daggerIron;
   public static Item daggerMithril;
   public static Item daggerAdamantium;
   public static Item daggerRustedIron;
   public static Item daggerAncientMetal;
   public static ItemArrow arrowFlint;
   public static ItemArrow arrowCopper;
   public static ItemArrow arrowSilver;
   public static ItemArrow arrowGold;
   public static ItemArrow arrowIron;
   public static ItemArrow arrowMithril;
   public static ItemArrow arrowAdamantium;
   public static ItemArrow arrowRustedIron;
   public static ItemArrow arrowAncientMetal;
   public static ItemBow bowMithril;
   public static ItemBow bowAncientMetal;
   public static ItemArrow arrowObsidian;
   public static Item knifeCopper;
   public static Item knifeSilver;
   public static Item knifeGold;
   public static Item knifeIron;
   public static Item knifeMithril;
   public static Item knifeAdamantium;
   public static Item knifeRustedIron;
   public static Item knifeAncientMetal;
   public static ItemHelmet helmetCopper;
   public static ItemHelmet helmetSilver;
   public static ItemHelmet helmetMithril;
   public static ItemHelmet helmetAdamantium;
   public static ItemHelmet helmetAncientMetal;
   public static ItemHelmet helmetChainCopper;
   public static ItemHelmet helmetChainSilver;
   public static ItemHelmet helmetChainGold;
   public static ItemHelmet helmetChainMithril;
   public static ItemHelmet helmetChainAdamantium;
   public static ItemHelmet helmetRustedIron;
   public static ItemHelmet helmetChainRustedIron;
   public static ItemHelmet helmetChainAncientMetal;
   public static ItemCuirass plateCopper;
   public static ItemCuirass plateSilver;
   public static ItemCuirass plateMithril;
   public static ItemCuirass plateAdamantium;
   public static ItemCuirass plateAncientMetal;
   public static ItemCuirass plateChainCopper;
   public static ItemCuirass plateChainSilver;
   public static ItemCuirass plateChainGold;
   public static ItemCuirass plateChainMithril;
   public static ItemCuirass plateChainAdamantium;
   public static ItemCuirass plateRustedIron;
   public static ItemCuirass plateChainRustedIron;
   public static ItemCuirass plateChainAncientMetal;
   public static ItemLeggings legsCopper;
   public static ItemLeggings legsSilver;
   public static ItemLeggings legsMithril;
   public static ItemLeggings legsAdamantium;
   public static ItemLeggings legsAncientMetal;
   public static ItemLeggings legsChainCopper;
   public static ItemLeggings legsChainSilver;
   public static ItemLeggings legsChainGold;
   public static ItemLeggings legsChainMithril;
   public static ItemLeggings legsChainAdamantium;
   public static ItemLeggings legsRustedIron;
   public static ItemLeggings legsChainRustedIron;
   public static ItemLeggings legsChainAncientMetal;
   public static ItemBoots bootsCopper;
   public static ItemBoots bootsSilver;
   public static ItemBoots bootsMithril;
   public static ItemBoots bootsAdamantium;
   public static ItemBoots bootsAncientMetal;
   public static ItemBoots bootsChainCopper;
   public static ItemBoots bootsChainSilver;
   public static ItemBoots bootsChainGold;
   public static ItemBoots bootsChainMithril;
   public static ItemBoots bootsChainAdamantium;
   public static ItemBoots bootsRustedIron;
   public static ItemBoots bootsChainRustedIron;
   public static ItemBoots bootsChainAncientMetal;
   public static Item doorCopper;
   public static Item doorSilver;
   public static Item doorGold;
   public static Item doorMithril;
   public static Item doorAdamantium;
   public static Item doorAncientMetal;
   public static Item shardEmerald;
   public static Item shardDiamond;
   public static Item shardNetherQuartz;
   public static Item shardGlass;
   public static Item chipFlint;
   public static Item shardObsidian;
   public static Item chainCopper;
   public static Item chainSilver;
   public static Item chainGold;
   public static Item chainIron;
   public static Item chainMithril;
   public static Item chainAdamantium;
   public static Item chainRustedIron;
   public static Item chainAncientMetal;
   public static ItemHorseArmor horseArmorCopper;
   public static ItemHorseArmor horseArmorSilver;
   public static ItemHorseArmor horseArmorMithril;
   public static ItemHorseArmor horseArmorAdamantium;
   public static ItemHorseArmor horseArmorAncientMetal;
   public static ItemBucket bucketCopperEmpty;
   public static ItemBucket bucketSilverEmpty;
   public static ItemBucket bucketGoldEmpty;
   public static ItemBucket bucketMithrilEmpty;
   public static ItemBucket bucketAdamantiumEmpty;
   public static ItemBucket bucketAncientMetalEmpty;
   public static ItemBucket bucketCopperWater;
   public static ItemBucket bucketSilverWater;
   public static ItemBucket bucketGoldWater;
   public static ItemBucket bucketMithrilWater;
   public static ItemBucket bucketAdamantiumWater;
   public static ItemBucket bucketAncientMetalWater;
   public static ItemBucket bucketCopperLava;
   public static ItemBucket bucketSilverLava;
   public static ItemBucket bucketGoldLava;
   public static ItemBucket bucketMithrilLava;
   public static ItemBucket bucketAdamantiumLava;
   public static ItemBucket bucketAncientMetalLava;
   public static ItemBucketMilk bucketCopperMilk;
   public static ItemBucketMilk bucketSilverMilk;
   public static ItemBucketMilk bucketGoldMilk;
   public static ItemBucketMilk bucketMithrilMilk;
   public static ItemBucketMilk bucketAdamantiumMilk;
   public static ItemBucketMilk bucketAncientMetalMilk;
   public static ItemBowl bowlMilk;
   public static ItemBowl bowlWater;
   public static ItemMeat lambchopRaw;
   public static ItemMeat lambchopCooked;
   public static Item sinew;
   public static Item hatchetCopper;
   public static Item hatchetSilver;
   public static Item hatchetGold;
   public static Item hatchetIron;
   public static Item hatchetMithril;
   public static Item hatchetAdamantium;
   public static Item hatchetRustedIron;
   public static Item hatchetAncientMetal;
   public static ItemShears shearsRustedIron;
   public static Item cheese;
   public static Item flour;
   public static ItemFood dough;
   public static Item chocolate;
   public static Item onion;
   public static ItemBowl bowlBeefStew;
   public static ItemBowl bowlChickenSoup;
   public static ItemBowl bowlVegetableSoup;
   public static Item manure;
   public static ItemFishingRod fishingRodCopper;
   public static ItemFishingRod fishingRodSilver;
   public static ItemFishingRod fishingRodGold;
   public static ItemFishingRod fishingRodMithril;
   public static ItemFishingRod fishingRodAdamantium;
   public static ItemFishingRod fishingRodAncientMetal;
   public static Item carrotOnAStickCopper;
   public static Item carrotOnAStickSilver;
   public static Item carrotOnAStickGold;
   public static Item carrotOnAStickMithril;
   public static Item carrotOnAStickAdamantium;
   public static Item carrotOnAStickAncientMetal;
   public static Item shovelObsidian;
   public static Item knifeObsidian;
   public static Item hatchetObsidian;
   public static Item axeObsidian;
   public static ItemBowl bowlIceCream;
   public static ItemFishingRod fishingRodFlint;
   public static Item carrotOnAStickFlint;
   public static ItemBowl bowlSalad;
   public static Item fragsCreeper;
   public static Item fragsInfernalCreeper;
   public static ItemFishingRod fishingRodObsidian;
   public static Item carrotOnAStickObsidian;
   public static Item bottleOfDisenchanting;
   public static ItemBowl bowlCreamOfMushroomSoup;
   public static ItemBowl bowlCreamOfVegetableSoup;
   public static ItemBowl bowlPumpkinSoup;
   public static ItemFood orange;
   public static ItemFood banana;
   public static ItemCoin coinCopper;
   public static ItemCoin coinSilver;
   public static ItemCoin coinGold;
   public static ItemBowl bowlMashedPotato;
   public static ItemBowl bowlSorbet;
   public static ItemFood blueberries;
   public static ItemBowl bowlPorridge;
   public static ItemBowl bowlCereal;
   public static Item referencedBook;
   public static ItemMeat fishLargeRaw;
   public static ItemMeat fishLargeCooked;
   public static ItemBucket bucketCopperStone;
   public static ItemBucket bucketSilverStone;
   public static ItemBucket bucketGoldStone;
   public static ItemBucket bucketIronStone;
   public static ItemBucket bucketMithrilStone;
   public static ItemBucket bucketAdamantiumStone;
   public static ItemBucket bucketAncientMetalStone;
   public static Item fragsNetherspawn;
   public static ItemCoin coinAncientMetal;
   public static ItemCoin coinMithril;
   public static ItemCoin coinAdamantium;
   public static ItemMeat wormRaw;
   public static ItemMeat wormCooked;
   public static Item thrownWeb;
   public static Item genericFood;
   public static Item record13;
   public static Item recordCat;
   public static Item recordBlocks;
   public static Item recordChirp;
   public static Item recordFar;
   public static Item recordMall;
   public static Item recordMellohi;
   public static Item recordStal;
   public static Item recordStrad;
   public static Item recordWard;
   public static Item record11;
   public static Item recordWait;
   public static Item recordUnderworld;
   public static Item recordDescent;
   public static Item recordWanderer;
   public static Item recordLegends;
   public final int itemID;
   private int maxStackSize;
   private int maxDamage;
   protected boolean bFull3D;
   private final int num_subtypes;
   private final boolean has_subtypes;
   private Item containerItem;
   private String potionEffect;
   private String unlocalizedName;
   protected Icon itemIcon;
   private String iconString;
   private float reach_bonus;
   public List materials;
   private float crafting_difficulty_as_component;
   private float lowest_crafting_difficulty_to_produce_override;
   public IRecipe[] recipes;
   public static final int MIN_SIMILARITY = 0;
   public static final int MAX_SIMILARITY = 100;
   private int xp_reward;
   private int satiation;
   private int nutrition;
   private int sugar_content;
   private boolean has_protein;
   private boolean has_essential_fats;
   private boolean has_phytonutrients;
   private boolean is_animal_product;
   private boolean is_plant_product;
   private boolean alwaysEdible;
   public int num_recipes;
   private boolean is_crafting_product;
   private List crafting_products_this_is_component_of;
   private int[] skillsets_that_can_repair_this;

   protected boolean canRepair = true;

   protected Item() {
      this.maxStackSize = 16;
      this.materials = new ArrayList();
      this.crafting_difficulty_as_component = -1.0F;
      this.lowest_crafting_difficulty_to_produce_override = Float.MAX_VALUE;
      this.recipes = new IRecipe[65];
      this.crafting_products_this_is_component_of = new ArrayList();
      this.itemID = 0;
      this.num_subtypes = 0;
      this.has_subtypes = false;
   }

   protected Item(int id) {
      this.maxStackSize = 16;
      this.materials = new ArrayList();
      this.crafting_difficulty_as_component = -1.0F;
      this.lowest_crafting_difficulty_to_produce_override = Float.MAX_VALUE;
      this.recipes = new IRecipe[65];
      this.crafting_products_this_is_component_of = new ArrayList();
      this.itemID = id;
      this.num_subtypes = 0;
      this.has_subtypes = false;
   }

   protected Item(int id, String texture) {
      this(id, texture, -1);
   }

   protected Item(int par1, String texture, int num_subtypes) {
      this.maxStackSize = 16;
      this.materials = new ArrayList();
      this.crafting_difficulty_as_component = -1.0F;
      this.lowest_crafting_difficulty_to_produce_override = Float.MAX_VALUE;
      this.recipes = new IRecipe[65];
      this.crafting_products_this_is_component_of = new ArrayList();
      this.itemID = 256 + par1;
      GameData.newItemAdded(this);
      this.setTextureName(texture);
      if (itemsList[256 + par1] != null) {
         System.out.println("CONFLICT @ " + par1 + " item slot already occupied by " + itemsList[256 + par1] + " while adding " + this);
      }

      itemsList[256 + par1] = this;
      if (this.isDamageable()) {
         this.setMaxStackSize(1);
      }

      if (this instanceof ItemMap) {
         num_subtypes = 32000;
      } else if (num_subtypes < 0) {
         num_subtypes = this.getSubItems().size();
      }

      if (num_subtypes == 0) {
         Debug.setErrorMessage("Item: subtype==0?");
      }

      this.num_subtypes = num_subtypes < 2 ? 0 : num_subtypes;
      this.has_subtypes = this.num_subtypes > 0;
   }

   protected Item(int id, Material material, String texture) {
      this(id, texture);
      this.setMaterial(material);
   }

   protected Item(int id, Material[] material_array, String texture) {
      this(id, texture);
      this.setMaterial(material_array);
   }

   public Item setMaxStackSize(int par1) {
      this.maxStackSize = par1;
      return this;
   }

   public int getSpriteNumber() {
      return 1;
   }

   public Icon getIconFromSubtype(int par1) {
      return this.itemIcon;
   }

   public final Icon getIconIndex(ItemStack par1ItemStack) {
      return this.getIconFromSubtype(par1ItemStack.getItemSubtype());
   }

   public float getStrVsBlock(Block block, int metadata) {
      return 0.0F;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      return false;
   }

   public void onItemUseFinish(ItemStack item_stack, World world, EntityPlayer player) {
      if (player.onServer() && !player.inCreativeMode()) {
         Item item = this.getItemProducedOnItemUseFinish();
         player.convertOneOfHeldItem(item == null ? null : new ItemStack(item));
      }

   }

   @Deprecated
   public int getItemStackLimit(int subtype, int damage) {
      return this.maxStackSize;
   }

   public int getMetadata(int par1) {
      return 0;
   }

   public final boolean getHasSubtypes() {
      return this.has_subtypes;
   }

   public final int getNumSubtypes() {
      return this.num_subtypes;
   }

   public final int getMaxDamage(EnumQuality quality) {
      if (!this.isDamageable()) {
         Minecraft.setErrorMessage("getMaxDamage: item is not damageable, " + this);
         return this.maxDamage;
      } else {
         return this.hasQuality() && quality != null && quality != EnumQuality.average ? Math.max(Math.round((float)this.maxDamage * quality.getDurabilityModifier()), 1) : this.maxDamage;
      }
   }

   public int getMaxDamage(ItemStack item_stack) {
      if (!this.isDamageable()) {
         Minecraft.setErrorMessage("getMaxDamage: item is not damageable, " + this);
      }

      return item_stack == null ? this.maxDamage : this.getMaxDamage(item_stack.getQuality());
   }

   public final Item setMaxDamage(int par1) {
      if (par1 <= 0) {
         Minecraft.setErrorMessage("setMaxDamage: max_damage should be > 0 for " + this);
         return this;
      } else {
         if (!this.isDamageable()) {
            Minecraft.setErrorMessage("setMaxDamage: called for non-damageable item " + this);
         }

         this.maxDamage = par1;
         return this;
      }
   }

   public final boolean isDamageable() {
      return this instanceof IDamageableItem;
   }

   public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
      if (par2EntityLivingBase.onClient()) {
         Minecraft.setErrorMessage("ItemTool.hitEntity: called on client?");
      }

      if (!par3EntityLivingBase.isEntityPlayer()) {
         return false;
      } else {
         EntityPlayer player = (EntityPlayer)par3EntityLivingBase;
         if (player.inCreativeMode()) {
            return false;
         } else {
            byte chance_of_not_breaking;
            if (this == stick) {
               chance_of_not_breaking = 50;
            } else {
               if (this != bone) {
                  return false;
               }

               chance_of_not_breaking = 100;
            }

            if (itemRand.nextInt(chance_of_not_breaking) == 0) {
               player.causeBreakingItemEffect(this, player.getHeldItemStack() == par1ItemStack ? player.inventory.currentItem : -1);
               player.convertOneOfHeldItem((ItemStack)null);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public boolean onBlockDestroyed(BlockBreakInfo info) {
      return false;
   }

   public Item setFull3D() {
      this.bFull3D = true;
      return this;
   }

   public boolean isFull3D() {
      return this.bFull3D;
   }

   public boolean shouldRotateAroundWhenRendering() {
      return false;
   }

   public Item setUnlocalizedName(String par1Str) {
      this.unlocalizedName = par1Str;
      return this;
   }

   public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
      String var2 = this.getUnlocalizedName(par1ItemStack);
      return var2 == null ? "" : StatCollector.translateToLocal(var2);
   }

   public String getUnlocalizedName() {
      return "item." + this.unlocalizedName;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return "item." + this.unlocalizedName;
   }

   public Item setContainerItem(Item par1Item) {
      this.containerItem = par1Item;
      return this;
   }

   public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
      return true;
   }

   public boolean getShareTag() {
      return true;
   }

   public Item getContainerItem() {
      return this.containerItem;
   }

   public boolean hasContainerItem() {
      return this.containerItem != null;
   }

   public String getStatName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
   }

   public String getItemStackDisplayName(ItemStack par1ItemStack) {
      return StatCollector.translateToLocal(this.getUnlocalizedName(par1ItemStack) + ".name");
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      return 16777215;
   }

   public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
   }

   public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
   }

   public boolean isMap() {
      return false;
   }

   public EnumItemInUseAction getItemInUseAction(ItemStack item_stack, EntityPlayer player) {
      if (this.isDrinkable(item_stack)) {
         return EnumItemInUseAction.DRINK;
      } else {
         return this.isEatable(item_stack) ? EnumItemInUseAction.EAT : null;
      }
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 0;
   }

   public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4) {
   }

   protected Item setPotionEffect(String par1Str) {
      this.potionEffect = par1Str;
      return this;
   }

   public String getPotionEffect() {
      return this.potionEffect;
   }

   public boolean isPotionIngredient() {
      return this.potionEffect != null;
   }

   public void addInformationBeforeEnchantments(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
   }

   public void addInformation(ItemStack item_stack, EntityPlayer player, List info, boolean extended_info, Slot slot) {
      if (extended_info) {
         int satiation = this.getSatiation(player);
         int nutrition = this.getNutrition();
         if (this.satiation > 0 || nutrition > 0) {
            info.add("");
            if (this instanceof ItemBlock) {
               ItemBlock item_block = (ItemBlock)this;
               if (item_block.getBlock() == Block.mushroomRed) {
                  info.add(EnumChatFormatting.RED + Translator.getFormatted("item.tooltip.satiation", satiation));
                  info.add(EnumChatFormatting.RED + Translator.getFormatted("item.tooltip.nutrition", nutrition));
                  return;
               }
            }

            if (this.satiation > 0) {
               info.add((this.sugar_content > 0 && player.isInsulinResistant() ? player.getInsulinResistanceLevel().getColor() : EnumChatFormatting.BROWN) + Translator.getFormatted("item.tooltip.satiation", satiation));
            }

            if (nutrition > 0) {
               info.add(EnumChatFormatting.BROWN + Translator.getFormatted("item.tooltip.nutrition", nutrition));
            }
         }
      }

   }

   public boolean canBeRenamed() {
      return true;
   }

   public String getItemDisplayName(ItemStack par1ItemStack) {
      return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(par1ItemStack) + ".name")).trim();
   }

   public String getItemDisplayName() {
      return this.getItemDisplayName((ItemStack)null);
   }

   public String toString() {
      return this.getItemDisplayName();
   }

   public final String getNameForReferenceFile(ItemStack item_stack) {
      String disambiguation = this.getNameDisambiguationForReferenceFile(item_stack == null ? 0 : item_stack.getItemSubtype());
      if (disambiguation == null && item_stack != null && this instanceof ItemPotion) {
         ItemPotion item_potion = (ItemPotion)this;
         List list = new ArrayList();
         this.addInformation(item_stack, (EntityPlayer)null, list, false, (Slot)null);
         if (list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            if (ItemPotion.isSplash(item_stack.getItemSubtype())) {
               sb.append("Splash ");
            }

            sb.append("Potion of ");
            disambiguation = StringUtils.stripControlCodes((String)list.get(0));
            return sb.append(disambiguation).toString();
         }
      }

      StringBuffer sb = new StringBuffer();
      sb.append(this.getItemDisplayName(item_stack));
      if (disambiguation != null) {
         sb.append(" (" + disambiguation + ")");
      }

      return sb.toString();
   }

   public final String getNameForReferenceFile() {
      return this.getNameForReferenceFile((ItemStack)null);
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this == clay ? "ball" : null;
   }

   @Deprecated //Render pass sensitive version below.
   public boolean hasEffect(ItemStack par1ItemStack) {
      return this == bottleOfDisenchanting || par1ItemStack.isItemEnchanted();
   }

   public EnumRarity getRarity(ItemStack par1ItemStack) {
      return par1ItemStack.isItemEnchanted() ? EnumRarity.rare : EnumRarity.common;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean requiresMultipleRenderPasses() {
      return false;
   }

   public Icon getIconFromSubtypeForRenderPass(int par1, int par2) {
      return this.getIconFromSubtype(par1);
   }

   public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
      par3List.add(new ItemStack(par1, 1, 0));
   }

   public List getSubItems() {
      List list = new ArrayList();
      this.getSubItems(this.itemID, (CreativeTabs)null, list);
      return list;
   }

   public CreativeTabs getCreativeTab() {
      return this.tabToDisplayOn;
   }

   public Item setCreativeTab(CreativeTabs par1CreativeTabs) {
      this.tabToDisplayOn = par1CreativeTabs;
      return this;
   }

   public boolean canItemEditBlocks() {
      return true;
   }

   public Material getMaterialForDurability() {
      return this.getExclusiveMaterial();
   }

   public Material getMaterialForRepairs() {
      return this.getMaterialForDurability();
   }

   public Item getRepairItem() {
      Material material_for_repairs = this.getMaterialForRepairs();
      if (material_for_repairs == Material.copper) {
         return copperNugget;
      } else if (material_for_repairs == Material.silver) {
         return silverNugget;
      } else if (material_for_repairs == Material.gold) {
         return goldNugget;
      } else if (material_for_repairs != Material.iron && material_for_repairs != Material.rusted_iron) {
         if (material_for_repairs == Material.mithril) {
            return mithrilNugget;
         } else if (material_for_repairs == Material.adamantium) {
            return adamantiumNugget;
         } else {
            return material_for_repairs == Material.ancient_metal ? ancientMetalNugget : null;
         }
      } else {
         return ironNugget;
      }
   }

   public int getRepairCost() {
      return 0;
   }

   public boolean hasRepairCost() {
      return this.getRepairCost() > 0;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.itemIcon = par1IconRegister.registerIcon(this.getIconString());
   }

   public Multimap getItemAttributeModifiers() {
      return HashMultimap.create();
   }

   protected Item setTextureName(String par1Str) {
      this.iconString = par1Str;
      return this;
   }

   protected String getIconString() {
      return this.iconString == null ? "MISSING_ICON_ITEM_" + this.itemID + "_" + this.unlocalizedName : this.iconString;
   }

   public boolean isEffectiveAgainstBlock(Block block, int metadata) {
      return false;
   }

   public float getReachBonus() {
      return this.reach_bonus;
   }

   public final float getReachBonus(Block block, int metadata) {
      return this.isEffectiveAgainstBlock(block, metadata) ? this.reach_bonus : 0.0F;
   }

   public float getReachBonus(Entity entity) {
      return 0.0F;
   }

   public Item setReachBonus(float reach_bonus) {
      this.reach_bonus = reach_bonus;
      return this;
   }

   public Class[] spliceClassArrays(Class[] first, Class[] second) {
      Class[] spliced = new Class[first.length + second.length];

      int i;
      for(i = 0; i < first.length; ++i) {
         spliced[i] = first[i];
      }

      for(i = 0; i < second.length; ++i) {
         spliced[first.length + i] = second[i];
      }

      return spliced;
   }

   public boolean similarToItemsOfSameClass() {
      return false;
   }

   public Class[] getSimilarClasses() {
      return new Class[0];
   }

   public int getSimilarityToItem(Item item) {
      if (item != null) {
         if (item == this || item.itemID == this.itemID) {
            return 100;
         }

         if (item.getClass() == this.getClass() && this.similarToItemsOfSameClass()) {
            return 99;
         }

         Class[] similar_classes = this.getSimilarClasses();

         for(int i = 0; i < similar_classes.length; ++i) {
            if (similar_classes[i] == item.getClass()) {
               return 100 - (i + 2);
            }
         }
      }

      return 0;
   }

   public Item setCraftingDifficultyAsComponent(float difficulty) {
      this.crafting_difficulty_as_component = difficulty;
      return this;
   }

   public boolean playerSwingsOnItemPlaced() {
      return true;
   }

   public boolean isEatable(int item_subtype) {
      return false;
   }

   public final boolean isEatable(ItemStack item_stack) {
      return this.isEatable(item_stack.getItemSubtype());
   }

   public boolean isDrinkable(int item_subtype) {
      return false;
   }

   public final boolean isDrinkable(ItemStack item_stack) {
      return this.isDrinkable(item_stack.getItemSubtype());
   }

   public final boolean isIngestable(ItemStack item_stack) {
      return this.isIngestable(item_stack.getItemSubtype());
   }

   public final boolean isIngestable(int item_subtype) {
      return this.isEatable(item_subtype) || this.isDrinkable(item_subtype);
   }

   public static Item getItem(int item_id) {
      return item_id > 0 && item_id < itemsList.length ? itemsList[item_id] : null;
   }

   public static Item getItem(Block block) {
      return block == null ? null : getItem(block.blockID);
   }

   public Material getMaterialForEnchantment() {
      Material most_enchantable_material = (Material)this.materials.get(0);

      for(int i = 1; i < this.materials.size(); ++i) {
         Material material = (Material)this.materials.get(i);
         if (material.enchantability > most_enchantable_material.enchantability) {
            most_enchantable_material = material;
         }
      }

      return most_enchantable_material;
   }

   public boolean hasMaterial(Material material, boolean exclusively) {
      if (this.materials.size() == 0) {
         return false;
      } else {
         int i;
         if (exclusively) {
            for(i = 0; i < this.materials.size(); ++i) {
               if (this.getMaterial(i) != material) {
                  return false;
               }
            }

            return true;
         } else {
            for(i = 0; i < this.materials.size(); ++i) {
               if (this.getMaterial(i) == material) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean hasMaterial(Material material) {
      return this.hasMaterial(material, false);
   }

   public boolean hasMaterial(Material... materials) {
      for(int i = 0; i < materials.length; ++i) {
         if (this.hasMaterial(materials[i])) {
            return true;
         }
      }

      return false;
   }

   public boolean isCompletelyMetal() {
      if (this.materials.size() == 0) {
         Minecraft.setErrorMessage("isCompletelyMetal: no materials defined for " + this);
         return false;
      } else {
         for(int i = 0; i < this.materials.size(); ++i) {
            if (!this.getMaterial(i).isMetal()) {
               return false;
            }
         }

         return true;
      }
   }

   public Item setXPReward(int xp_reward) {
      this.xp_reward = xp_reward;
      return this;
   }

   public int getExperienceReward(int subtype) {
      return this.xp_reward;
   }

   public Item setSatiation(int satiation) {
      this.satiation = satiation;
      return this;
   }

   public Item setNutrition(int nutrition) {
      this.nutrition = nutrition;
      return this;
   }

   public final Item setFoodValue(int satiation, int nutrition, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients) {
      return this.setFoodValue(satiation, nutrition, 0, has_protein, has_essential_fats, has_phytonutrients);
   }

   public final Item setFoodValue(int satiation, int nutrition, int sugar_content, boolean has_protein, boolean has_essential_fats, boolean has_phytonutrients) {
      this.satiation = satiation;
      this.nutrition = nutrition;
      this.sugar_content = sugar_content;
      this.has_protein = has_protein;
      this.has_essential_fats = has_essential_fats;
      this.has_phytonutrients = has_phytonutrients;
      if (satiation > 0 || nutrition > 0) {
         this.setCreativeTab(CreativeTabs.tabFood);
      }

      return this;
   }

   public final int getSatiation(EntityPlayer player) {
      if (player != null && !player.canMetabolizeFoodSugars() && this.sugar_content > 0) {
         return this.sugar_content < 1000 ? this.satiation - 1 : this.satiation - this.sugar_content / 1000;
      } else {
         return this.satiation;
      }
   }

   public int getNutrition() {
      return this.nutrition;
   }

   public int getSugarContent() {
      return this.sugar_content;
   }

   public int getInsulinResponse() {
      return (int)((float)this.getSugarContent() * 4.8F);
   }

   public boolean hasProtein() {
      return this.has_protein;
   }

   public boolean hasEssentialFats() {
      return this.has_essential_fats;
   }

   public boolean hasPhytonutrients() {
      return this.has_phytonutrients;
   }

   public Item setAlwaysEdible() {
      this.alwaysEdible = true;
      return this;
   }

   public boolean isAlwaysEdible() {
      return this.alwaysEdible;
   }

   public Item setAnimalProduct() {
      this.is_animal_product = true;
      return this;
   }

   public boolean isAnimalProduct() {
      return this.is_animal_product;
   }

   public Item setPlantProduct() {
      this.is_plant_product = true;
      return this;
   }

   public boolean isPlantProduct() {
      return this.is_plant_product;
   }

   public Item useVanillaTexture(String texture) {
      this.setTextureName(texture);
      return this;
   }

   public boolean hasQuality() {
      return false;
   }

   public boolean isCraftingComponent(int subtype_or_0) {
      for(int i = 0; i < this.crafting_products_this_is_component_of.size(); ++i) {
         ComponentOfCraftingProductEntry entry = (ComponentOfCraftingProductEntry)this.crafting_products_this_is_component_of.get(i);
         if (entry.subtype_of_component_or_0 == -1 || entry.subtype_of_component_or_0 == subtype_or_0) {
            return true;
         }
      }

      return false;
   }

   public Item setAsCraftingProduct() {
      this.is_crafting_product = true;
      return this;
   }

   public boolean isCraftingProduct() {
      return this.is_crafting_product;
   }

//   public boolean isRepairable() {
//      return this.isDamageable();
//   }

   public static Item getMatchingItem(Class item_class, Material material) {
      Item matching_item = null;

      for(int i = 0; i < itemsList.length; ++i) {
         Item item = getItem(i);
         if (item != null && item.getClass() == item_class && item.hasMaterial(material)) {
            if (matching_item == null) {
               matching_item = item;
            } else {
               Minecraft.setErrorMessage("getMatchingItem: more than one item matched " + item_class + ", " + material);
            }
         }
      }

      return matching_item;
   }

   public Material getMaterial(int index) {
      return index < this.materials.size() ? (Material)this.materials.get(index) : null;
   }

   public EnumQuality getMaxQuality() {
      if (!this.hasQuality()) {
         Minecraft.setErrorMessage("getMaxQuality: item does not have quality (" + this + ")");
         return null;
      } else if (this.materials.size() == 0) {
         Minecraft.setErrorMessage("getMaxQuality: no materials defined for " + this);
         return null;
      } else if (this.materials.size() == 1) {
         return this.getMaterial(0).getMaxQuality();
      } else {
         EnumQuality lowest_max_quality = Material.getLowestMaxQualityOfMaterials(this.materials);
         return lowest_max_quality.isLowerThan(EnumQuality.average) ? lowest_max_quality : Material.getHighestMaxQualityOfMaterials(this.materials);
      }
   }

   public EnumQuality getDefaultQuality() {
      return EnumQuality.getLowest(this.getMaxQuality(), EnumQuality.average);
   }

   public void setLowestCraftingDifficultyToProduce(float lowest_crafting_difficulty_to_produce_override) {
      this.lowest_crafting_difficulty_to_produce_override = lowest_crafting_difficulty_to_produce_override;
   }

   public float getLowestCraftingDifficultyToProduce() {
      if (this.lowest_crafting_difficulty_to_produce_override != Float.MAX_VALUE) {
         return this.lowest_crafting_difficulty_to_produce_override;
      } else {
         float lowest_difficulty = Float.MAX_VALUE;

         for(int i = 0; i < this.num_recipes; ++i) {
            IRecipe recipe = this.recipes[i];
            if (recipe.getIncludeInLowestCraftingDifficultyDetermination()) {
               float difficulty_per_unit = recipe.getUnmodifiedDifficulty() / (float)recipe.getRecipeOutput().stackSize;
               if (difficulty_per_unit < lowest_difficulty) {
                  lowest_difficulty = difficulty_per_unit;
               }
            }
         }

         return lowest_difficulty;
      }
   }

   public final void setAsComponentOfCraftingProduct(ItemStack crafting_product) {
      this.setAsComponentOfCraftingProduct(-1, crafting_product);
   }

   public final void setAsComponentOfCraftingProduct(int subtype_of_component, ItemStack crafting_product) {
      for(int i = 0; i < this.crafting_products_this_is_component_of.size(); ++i) {
         ComponentOfCraftingProductEntry entry = (ComponentOfCraftingProductEntry)this.crafting_products_this_is_component_of.get(i);
         if ((entry.subtype_of_component_or_0 == -1 || entry.subtype_of_component_or_0 == subtype_of_component) && ItemStack.areItemStacksEqual(entry.crafting_product, crafting_product, true, true, true, true)) {
            return;
         }
      }

      this.crafting_products_this_is_component_of.add(new ComponentOfCraftingProductEntry(subtype_of_component, crafting_product));
   }

   public List getCraftingProductsThisIsComponentOf(int subtype_or_0) {
      List crafting_products = new ArrayList();

      for(int i = 0; i < this.crafting_products_this_is_component_of.size(); ++i) {
         ComponentOfCraftingProductEntry entry = (ComponentOfCraftingProductEntry)this.crafting_products_this_is_component_of.get(i);
         if (entry.subtype_of_component_or_0 == -1 || entry.subtype_of_component_or_0 == subtype_or_0) {
            crafting_products.add(entry.crafting_product);
         }
      }

      return crafting_products;
   }

   public boolean doesSubtypeMatterForProduct(ItemStack crafting_product) {
      for(int i = 0; i < this.crafting_products_this_is_component_of.size(); ++i) {
         ComponentOfCraftingProductEntry entry = (ComponentOfCraftingProductEntry)this.crafting_products_this_is_component_of.get(i);
         if (ItemStack.areItemStacksEqual(entry.crafting_product, crafting_product, true, true, true, true)) {
            return entry.subtype_of_component_or_0 != -1;
         }
      }

      Minecraft.setErrorMessage("doesSubtypeMatterForProduct: " + this + " is not a crafting component of " + crafting_product.getNameForReferenceFile());
      return false;
   }

   public Item setMaterial(Material... materials) {
      this.materials.clear();
      return materials != null && materials.length != 0 ? this.addMaterial(materials) : this;
   }

   public Item addMaterial(Material... materials) {
      for(int i = 0; i < materials.length; ++i) {
         Material material = materials[i];
         if (material == null) {
            Minecraft.setErrorMessage("addMaterial: why adding null material to " + this + " [" + this.itemID + "]");
         } else if (this.hasMaterial(material)) {
            Minecraft.setErrorMessage("addMaterial: trying to add duplicate material " + material + " to " + this + " [" + this.itemID + "]");
         } else {
            this.materials.add(material);
         }
      }

      return this;
   }

   public int getBurnTime(ItemStack item_stack) {
      if (this == paper) {
         return 25;
      } else if (this == manure) {
         return 100;
      } else if (this != stick && !(this instanceof ItemArrow)) {
         if (this != book && this != writableBook && !(this instanceof ItemEditableBook) && this != enchantedBook) {
            if (this == doorWood) {
               return 400;
            } else if (this == blazeRod) {
               return 2400;
            } else if (this.hasMaterial(Material.wood)) {
               return 200;
            } else {
               return this.hasMaterial(Material.paper) ? 50 : 0;
            }
         } else {
            return 100;
         }
      } else {
         return 100;
      }
   }

   public int getHeatLevel(ItemStack item_stack) {
      if (this == blazeRod) {
         return 4;
      } else {
         return this.getBurnTime(item_stack) > 0 ? 1 : 0;
      }
   }

   public boolean containsMetal() {
      return Material.doesMaterialListContainMetal(this.materials);
   }

   public boolean containsRockyMineral() {
      return Material.doesMaterialListContainRockyMineral(this.materials);
   }

   public boolean containsCrystal() {
      return Material.doesMaterialListContainCrystal(this.materials);
   }

   public boolean canDouseFire() {
      return Material.doesMaterialListContainMaterialThatCanDouseFire(this.materials);
   }

   public boolean canCatchFire() {
      return Material.doesMaterialListContainMaterialThatCanCatchFire(this.materials);
   }

   public boolean canBurnAsFuelSource() {
      return Material.doesMaterialListContainMaterialThatCanBurnAsFuelSource(this.materials);
   }

   public boolean isHarmedByFire() {
      return Material.doesMaterialListContainMaterialThatIsHarmedByFire(this.materials);
   }

   public boolean isHarmedByLava() {
      return Material.doesMaterialListContainMaterialThatIsHarmedByLava(this.materials);
   }

   public static void verifyThatAllItemsHaveMaterialsDefined() {
      for(int i = 0; i < itemsList.length; ++i) {
         Item item = getItem(i);
         if (item != null && item.materials.size() == 0) {
            Minecraft.setErrorMessage("Warning: No materials defined for " + item);
         }
      }

   }

   public Material getExclusiveMaterial() {
      if (this.materials.size() == 0) {
         Minecraft.setErrorMessage("getExclusiveMaterial: no material defined for " + this);
         return null;
      } else if (this.materials.size() > 1) {
         Minecraft.setErrorMessage("getExclusiveMaterial: multiple materials defined for " + this);
         return null;
      } else {
         return this.getMaterial(0);
      }
   }

   public final boolean isBlock() {
      return this instanceof ItemBlock;
   }

   public final boolean isArmor() {
      return this instanceof ItemArmor;
   }

   public boolean isChainMail() {
      return false;
   }

   public boolean hasBreakingEffect() {
      return true;
   }

   public boolean hasCraftingEffect() {
      return this.hasBreakingEffect();
   }

   public boolean isTool() {
      return this instanceof ItemTool;
   }

   public Item getItemProducedOnItemUseFinish() {
      return null;
   }

   public ItemStack getItemProducedWhenDestroyed(ItemStack item_stack, DamageSource damage_source) {
      return null;
   }

   public boolean hasIngestionPriority(ItemStack item_stack, boolean ctrl_is_down) {
      return true;
   }

   public boolean tryPlaceAsBlock(RaycastCollision rc, Block block, EntityPlayer player, ItemStack item_stack) {
      if (!rc.isBlock()) {
         Minecraft.setErrorMessage("tryPlaceAsBlock: raycast collision is not block");
         return false;
      } else {
         World world = rc.world;
         EnumFace face = rc.face_hit;
         int metadata_if_replacing_block = block.getMetadataForPlacement(player.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, item_stack, player, EnumFace.TOP, 0.0F, 0.0F, 0.0F);
         int x;
         int y;
         int z;
         if (!block.canReplaceBlock(metadata_if_replacing_block, rc.getBlockHit(), rc.block_hit_metadata)) {
            x = rc.neighbor_block_x;
            y = rc.neighbor_block_y;
            z = rc.neighbor_block_z;
            if (world.isWithinTournamentSafeZone(x, y, z)) {
               return false;
            } else if (BlockSandStone.isSacredSandstone(block, item_stack.getItemSubtype()) && !world.getBiomeGenForCoords(x, z).isDesertBiome()) {
               return false;
            } else {
               return rc.getBlockHit() == Block.vine && block == Block.vine ? block.tryPlaceFromHeldItem(rc.block_hit_x, rc.block_hit_y - 1, rc.block_hit_z, EnumFace.BOTTOM, item_stack, player, 0.0F, 0.0F, 0.0F, true, true) : block.tryPlaceFromHeldItem(x, y, z, face, item_stack, player, rc.block_hit_offset_x, rc.block_hit_offset_y, rc.block_hit_offset_z, true, true);
            }
         } else {
            x = rc.block_hit_x;
            y = rc.block_hit_y;
            z = rc.block_hit_z;
            if (world.isWithinTournamentSafeZone(x, y, z)) {
               return false;
            } else if (BlockSandStone.isSacredSandstone(block, item_stack.getItemSubtype()) && !world.getBiomeGenForCoords(x, z).isDesertBiome()) {
               return false;
            } else {
               if (rc.getBlockHit() instanceof BlockMounted) {
                  BlockMounted block_mounted = (BlockMounted)rc.getBlockHit();
                  face = block_mounted.getFaceMountedTo(rc.block_hit_metadata);
               } else if (rc.getBlockHit() instanceof BlockVine && BlockVine.getNumVines(rc.block_hit_metadata) == 1) {
                  if (rc.block_hit_metadata == 1) {
                     face = EnumFace.NORTH;
                  } else if (rc.block_hit_metadata == 2) {
                     face = EnumFace.EAST;
                  } else if (rc.block_hit_metadata == 4) {
                     face = EnumFace.SOUTH;
                  } else {
                     if (rc.block_hit_metadata != 8) {
                        return false;
                     }

                     face = EnumFace.WEST;
                  }

                  if (world.getNeighborBlock(x, y, z, face.getOpposite()) == null) {
                     return false;
                  }
               } else {
                  face = EnumFace.TOP;
               }

               if (face == EnumFace.TOP && world.getBlock(x, y - 1, z) == null) {
                  Minecraft.setErrorMessage("tryPlaceAsBlock: replacing block clicked but it doesn't have a block beneath it, don't know how to handle");
                  return false;
               } else {
                  rc = null;
                  return block.tryPlaceFromHeldItem(x, y, z, face, item_stack, player, 0.0F, 0.0F, 0.0F, true, true);
               }
            }
         }
      }
   }

   public boolean isDissolvedByWater() {
      return Material.doesMaterialListContainMaterialThatDissolvesInWater(this.materials);
   }

   public boolean tryEntityInteraction(Entity entity, EntityPlayer player, ItemStack item_stack) {
      return false;
   }

   public boolean preventsHandDamage() {
      return this instanceof ItemTool || this == stick || this == bone;
   }

   public int getProtein() {
      return this.hasProtein() ? this.getNutrition() * 8000 : 0;
   }

   public int getEssentialFats() {
      return this.hasEssentialFats() ? this.getNutrition() * 8000 : 0;
   }

   public int getPhytonutrients() {
      return this.hasPhytonutrients() ? this.getNutrition() * 8000 : 0;
   }

   public static float getChanceOfSnowAndIceItemsMelting(float biome_temperature) {
      return (biome_temperature - 1.0F) * 1.0E-4F;
   }

   public Item setSkillsetsThatCanRepairThis(int[] skillsets) {
      this.skillsets_that_can_repair_this = skillsets;
      return this;
   }

   public Item setSkillsetThatCanRepairThis(int skillset) {
      this.skillsets_that_can_repair_this = skillset == -1 ? null : new int[]{skillset};
      return this;
   }

   public int[] getSkillsetsThatCanRepairThis() {
      return this.skillsets_that_can_repair_this;
   }

   public final ItemTool getAsTool() {
      return (ItemTool)this;
   }

   public final ItemArmor getAsArmor() {
      return (ItemArmor)this;
   }

   public final ItemBlock getAsItemBlock() {
      return (ItemBlock)this;
   }

   public float getCraftingDifficultyAsComponent(ItemStack item_stack) {
      return this.crafting_difficulty_as_component;
   }

   public ItemStack getItemStackForStatsIcon() {
      return new ItemStack(this);
   }

   public ItemFood getAsItemFood() {
      return (ItemFood)this;
   }

   public int getScaledDamage(float damage) {
      if (this instanceof ItemArmor) {
         damage *= 100.0F * (float)plateIron.getMaxDamage(EnumQuality.average) / (float)swordIron.getMaxDamage(EnumQuality.average);
      } else if (this instanceof ItemTool) {
         damage *= 100.0F;
      }

      return (int)damage;
   }

   public float getMeleeDamageBonus() {
      return 0.0F;
   }

   public boolean isHarmedByPepsin() {
      return Material.doesMaterialListContainMaterialThatIsHarmedByPepsin(this.materials);
   }

   public boolean isHarmedByAcid() {
      return Material.doesMaterialListContainMaterialThatIsHarmedByAcid(this.materials);
   }

   public boolean isHarmedBy(DamageSource damage_source) {
      if (damage_source.isFireDamage()) {
         return this.isHarmedByFire();
      } else if (damage_source.isLavaDamage()) {
         return this.isHarmedByLava();
      } else if (damage_source.isPepsinDamage()) {
         return this.isHarmedByPepsin();
      } else {
         return damage_source.isAcidDamage() ? this.isHarmedByAcid() : true;
      }
   }

   public IBehaviorDispenseItem getDispenserBehavior() {
      return null;
   }

   public Material getHardestMetalMaterial() {
      if (this.materials != null && this.materials.size() != 0) {
         Material hardest_metal;
         if (this.materials.size() == 1) {
            hardest_metal = (Material)this.materials.get(0);
            return hardest_metal.isMetal() ? hardest_metal : null;
         } else {
            hardest_metal = null;
            float highest_metal_durability = 0.0F;
            Iterator i = this.materials.iterator();

            while(true) {
               Material material;
               do {
                  do {
                     if (!i.hasNext()) {
                        return hardest_metal;
                     }

                     material = (Material)i.next();
                  } while(!material.isMetal());
               } while(hardest_metal != null && !(material.durability > highest_metal_durability));

               hardest_metal = material;
               highest_metal_durability = material.durability;
            }
         }
      } else {
         return null;
      }
   }

   public final boolean canBeCompostedByWorms(ItemStack item_stack) {
      return this.getCompostingValue() > 0.0F;
   }

   public float getCompostingValue() {
      if (this == flour) {
         return 0.8F;
      } else if (this == wheat) {
         return 0.5F;
      } else {
         return this == paper ? 0.1F : (float)(this.getSatiation((EntityPlayer)null) + this.getNutrition()) * 0.1F;
      }
   }

   public Item getCompostingRemains(ItemStack item_stack) {
      return null;
   }

   public void validate() {
      if (this instanceof IDamageableItem && this.maxDamage < 1) {
         Minecraft.setErrorMessage("Item: " + this + " is damageable but has maxDamage of " + this.maxDamage);
      }

   }

   static {
      shovelIron = (new ItemShovel(0, Material.iron)).setUnlocalizedName("shovelIron").useVanillaTexture("iron_shovel");
      pickaxeIron = (new ItemPickaxe(1, Material.iron)).setUnlocalizedName("pickaxeIron").useVanillaTexture("iron_pickaxe");
      axeIron = (new ItemAxe(2, Material.iron)).setUnlocalizedName("axeIron").useVanillaTexture("iron_axe");
      flintAndSteel = (new ItemFlintAndSteel(3)).setUnlocalizedName("flintAndSteel");
      appleRed = (new ItemFood(4, Material.fruit, 2, 1, 1000, false, false, true, "VANILLA")).setPlantProduct().setUnlocalizedName("apple").useVanillaTexture("apple");
      bow = (ItemBow)(new ItemBow(5, Material.wood)).setUnlocalizedName("bow");
      coal = (new ItemCoal(7)).setUnlocalizedName("coal");
      diamond = (new ItemRock(8, Material.diamond, "diamond")).setXPReward(30).setUnlocalizedName("diamond");
      ingotIron = (new ItemIngot(9, Material.iron)).setXPReward(10).setUnlocalizedName("ingotIron").useVanillaTexture("iron_ingot");
      ingotGold = (new ItemIngot(10, Material.gold)).setXPReward(20).setUnlocalizedName("ingotGold");
      swordIron = (ItemSword)(new ItemSword(11, Material.iron)).setUnlocalizedName("swordIron").useVanillaTexture("iron_sword");
      shovelWood = (new ItemShovel(13, Material.wood)).setUnlocalizedName("shovelWood").useVanillaTexture("wood_shovel");
      stick = (new Item(24, Material.wood, "stick")).setMaxStackSize(32).setCraftingDifficultyAsComponent(25.0F).setReachBonus(0.5F).setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials);
      bowlEmpty = (ItemBowl)(new ItemBowl(25, (Material)null, "VANILLA")).setUnlocalizedName("bowl").useVanillaTexture("bowl");
      bowlMushroomStew = (ItemBowl)(new ItemBowl(26, Material.mushroom_stew, "mushroom_stew")).setFoodValue(2, 4, false, false, false).setPlantProduct().setUnlocalizedName("mushroomStew");
      swordGold = (new ItemSword(27, Material.gold)).setUnlocalizedName("swordGold");
      shovelGold = (new ItemShovel(28, Material.gold)).setUnlocalizedName("shovelGold");
      pickaxeGold = (new ItemPickaxe(29, Material.gold)).setUnlocalizedName("pickaxeGold");
      axeGold = (new ItemAxe(30, Material.gold)).setUnlocalizedName("axeGold");
      silk = (new ItemReed(31, Block.tripWire, "string")).setMaterial(new Material[]{Material.silk}).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials);
      feather = (new Item(32, Material.feather, "feather")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials);
      gunpowder = (new Item(33, Material.gunpowder, "gunpowder")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("sulphur").setPotionEffect(PotionHelper.gunpowderEffect).setCreativeTab(CreativeTabs.tabMaterials);
      hoeIron = (new ItemHoe(36, Material.iron)).setUnlocalizedName("hoeIron").useVanillaTexture("iron_hoe");
      hoeGold = (new ItemHoe(38, Material.gold)).setUnlocalizedName("hoeGold");
      seeds = (new ItemSeeds(39, 1, 0, false, true, false, Block.crops.blockID, Block.tilledField.blockID, "VANILLA")).setUnlocalizedName("seeds").useVanillaTexture("seeds_wheat");
      wheat = (new Item(40, Material.plants, "wheat")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials);
      bread = (ItemFood)(new ItemFood(41, Material.bread, 8, 2, false, false, false, "VANILLA")).setPlantProduct().setUnlocalizedName("bread").useVanillaTexture("bread");
      helmetLeather = (ItemHelmet)(new ItemHelmet(42, Material.leather, false)).setUnlocalizedName("helmetCloth").useVanillaTexture("leather_helmet");
      plateLeather = (ItemCuirass)(new ItemCuirass(43, Material.leather, false)).setUnlocalizedName("chestplateCloth").useVanillaTexture("leather_chestplate");
      legsLeather = (ItemLeggings)(new ItemLeggings(44, Material.leather, false)).setUnlocalizedName("leggingsCloth").useVanillaTexture("leather_leggings");
      bootsLeather = (ItemBoots)(new ItemBoots(45, Material.leather, false)).setUnlocalizedName("bootsCloth").useVanillaTexture("leather_boots");
      helmetChainIron = (ItemHelmet)(new ItemHelmet(46, Material.iron, true)).setUnlocalizedName("helmetChainIron").useVanillaTexture("chainmail_helmet");
      plateChainIron = (ItemCuirass)(new ItemCuirass(47, Material.iron, true)).setUnlocalizedName("chestplateChainIron").useVanillaTexture("chainmail_chestplate");
      legsChainIron = (ItemLeggings)(new ItemLeggings(48, Material.iron, true)).setUnlocalizedName("leggingsChainIron").useVanillaTexture("chainmail_leggings");
      bootsChainIron = (ItemBoots)(new ItemBoots(49, Material.iron, true)).setUnlocalizedName("bootsChainIron").useVanillaTexture("chainmail_boots");
      helmetIron = (ItemHelmet)(new ItemHelmet(50, Material.iron, false)).setUnlocalizedName("helmetIron").useVanillaTexture("iron_helmet");
      plateIron = (ItemCuirass)(new ItemCuirass(51, Material.iron, false)).setUnlocalizedName("chestplateIron").useVanillaTexture("iron_chestplate");
      legsIron = (ItemLeggings)(new ItemLeggings(52, Material.iron, false)).setUnlocalizedName("leggingsIron").useVanillaTexture("iron_leggings");
      bootsIron = (ItemBoots)(new ItemBoots(53, Material.iron, false)).setUnlocalizedName("bootsIron").useVanillaTexture("iron_boots");
      helmetGold = (ItemHelmet)(new ItemHelmet(58, Material.gold, false)).setUnlocalizedName("helmetGold");
      plateGold = (ItemCuirass)(new ItemCuirass(59, Material.gold, false)).setUnlocalizedName("chestplateGold");
      legsGold = (ItemLeggings)(new ItemLeggings(60, Material.gold, false)).setUnlocalizedName("leggingsGold");
      bootsGold = (ItemBoots)(new ItemBoots(61, Material.gold, false)).setUnlocalizedName("bootsGold");
      flint = (new ItemRock(62, Material.flint, "flint")).setMaxStackSize(16).setUnlocalizedName("flint");
      porkRaw = (ItemMeat)(new ItemMeat(63, 4, 4, false, false, "VANILLA")).setUnlocalizedName("porkchopRaw").useVanillaTexture("porkchop_raw");
      porkCooked = (ItemMeat)(new ItemMeat(64, 8, 8, false, true, "porkchop_cooked")).setUnlocalizedName("porkchopCooked");
      painting = (new ItemHangingEntity(65, EntityPainting.class, "painting")).setMaterial(new Material[]{Material.cloth, Material.wood}).setUnlocalizedName("painting");
      appleGold = (new ItemAppleGold(66, 2, 1, "VANILLA")).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 60, 0, 1.0F).setUnlocalizedName("appleGold").useVanillaTexture("apple_golden");
      sign = (new ItemSign(67)).setUnlocalizedName("sign");
      doorWood = (new ItemDoor(68, Material.wood)).setUnlocalizedName("doorWood").useVanillaTexture("door_wood");
      bucketEmpty = (ItemBucket)(new ItemBucket(69, Material.iron, (Material)null)).setUnlocalizedName("bucket");
      bucketWater = (ItemBucket)(new ItemBucket(70, Material.iron, Material.water)).setUnlocalizedName("bucketWater").setContainerItem(bucketEmpty);
      bucketLava = (ItemBucket)(new ItemBucket(71, Material.iron, Material.lava)).setUnlocalizedName("bucketLava").setContainerItem(bucketEmpty);
      minecartEmpty = (new ItemMinecart(72, 0, "minecart_normal")).setUnlocalizedName("minecart");
      saddle = (new ItemSaddle(73)).setUnlocalizedName("saddle");
      doorIron = (new ItemDoor(74, Material.iron)).setUnlocalizedName("doorIron").useVanillaTexture("door_iron");
      redstone = (new ItemRedstone(75)).setUnlocalizedName("redstone").setPotionEffect(PotionHelper.redstoneEffect);
      snowball = (new ItemSnowball(76)).setUnlocalizedName("snowball");
      boat = (new ItemBoat(77)).setUnlocalizedName("boat");
      leather = (new Item(78, Material.leather, "leather")).setCraftingDifficultyAsComponent(100.0F).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials);
      bucketIronMilk = (ItemBucketMilk)(new ItemBucketMilk(79, Material.iron)).setUnlocalizedName("milk").setContainerItem(bucketEmpty);
      brick = (new ItemBrick(80, Material.clay, "brick")).setUnlocalizedName("brick");
      clay = (new Item(81, Material.clay, "clay_ball")).setUnlocalizedName("clay").setCraftingDifficultyAsComponent(25.0F).setMaxStackSize(16).setCreativeTab(CreativeTabs.tabMaterials);
      reed = (new ItemReed(82, Block.reed, "reeds")).setMaxStackSize(16).setCraftingDifficultyAsComponent(100.0F).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials);
      paper = (new Item(83, Material.paper, "paper")).setMaxStackSize(64).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc);
      book = (new ItemBook(84, "book_normal")).setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc);
      slimeBall = new ItemGelatinousSphere(85);
      minecartCrate = (new ItemMinecart(86, 1, "minecart_chest")).setUnlocalizedName("minecartChest");
      minecartPowered = (new ItemMinecart(87, 2, "minecart_furnace")).setUnlocalizedName("minecartFurnace");
      egg = (new ItemEgg(88)).setUnlocalizedName("egg").useVanillaTexture("egg");
      compass = (new Item(89, Material.iron, "compass")).addMaterial(Material.redstone).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools);
      fishingRodIron = (ItemFishingRod)(new ItemFishingRod(90, Material.iron)).setUnlocalizedName("fishingRod");
      pocketSundial = (new Item(91, Material.gold, "clock")).addMaterial(Material.redstone).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools);
      glowstone = (new Item(92, Material.glowstone, "glowstone_dust")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("yellowDust").setPotionEffect(PotionHelper.glowstoneEffect).setCreativeTab(CreativeTabs.tabMaterials);
      fishRaw = (ItemMeat)(new ItemMeat(93, 3, 3, true, false, "VANILLA")).setUnlocalizedName("fishRaw").useVanillaTexture("fish_raw");
      fishCooked = (ItemMeat)(new ItemMeat(94, 6, 6, true, true, "VANILLA")).setUnlocalizedName("fishCooked").useVanillaTexture("fish_cooked");
      dyePowder = (new ItemDye(95)).setUnlocalizedName("dyePowder");
      bone = (new Item(96, Material.bone, "bone")).setCraftingDifficultyAsComponent(100.0F).setReachBonus(0.5F).setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc);
      sugar = (new ItemFood(97, Material.sugar, 1, 0, 1000, false, false, false, "VANILLA")).setPlantProduct().setUnlocalizedName("sugar").setPotionEffect(PotionHelper.sugarEffect).useVanillaTexture("sugar");
      cake = (new ItemReed(98, Block.cake, "cake")).setMaxStackSize(8).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood);
      bed = (new ItemBed(99)).setMaxStackSize(1).setUnlocalizedName("bed");
      redstoneRepeater = (new ItemReed(100, Block.redstoneRepeaterIdle, "repeater")).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone);
      cookie = (new ItemFood(101, Material.desert, 3, 1, 250, false, false, false, "VANILLA")).setPlantProduct().setUnlocalizedName("cookie").useVanillaTexture("cookie");
      map = (ItemMap)(new ItemMap(102, "map_filled")).setUnlocalizedName("map");
      shears = (ItemShears)(new ItemShears(103, Material.iron)).setUnlocalizedName("shears").useVanillaTexture("shears");
      melon = (new ItemFood(104, Material.fruit, 1, 1, 1000, false, false, true, "VANILLA")).setPlantProduct().setUnlocalizedName("melon").useVanillaTexture("melon");
      pumpkinSeeds = (new ItemSeeds(105, 1, 2, false, true, false, Block.pumpkinStem.blockID, Block.tilledField.blockID, "VANILLA")).setUnlocalizedName("seeds_pumpkin").useVanillaTexture("seeds_pumpkin");
      melonSeeds = (new ItemSeeds(106, 1, 1, false, true, false, Block.melonStem.blockID, Block.tilledField.blockID, "seeds_melon")).setUnlocalizedName("seeds_melon");
      beefRaw = (ItemMeat)(new ItemMeat(107, 5, 5, false, false, "beef_raw")).setUnlocalizedName("beefRaw");
      beefCooked = (ItemMeat)(new ItemMeat(108, 10, 10, false, true, "beef_cooked")).setUnlocalizedName("beefCooked");
      chickenRaw = (ItemMeat)(new ItemMeat(109, 3, 3, false, false, "VANILLA")).setPotionEffect(Potion.poison.id, 20, 0, 0.3F).setUnlocalizedName("chickenRaw").useVanillaTexture("chicken_raw");
      chickenCooked = (ItemMeat)(new ItemMeat(110, 6, 6, false, true, "VANILLA")).setUnlocalizedName("chickenCooked").useVanillaTexture("chicken_cooked");
      rottenFlesh = (new ItemMeat(111, 2, 1, false, false, "VANILLA")).setPotionEffect(Potion.poison.id, 20, 0, 0.8F).setUnlocalizedName("rottenFlesh").useVanillaTexture("rotten_flesh");
      enderPearl = (new ItemEnderPearl(112)).setUnlocalizedName("enderPearl");
      blazeRod = (new Item(113, Material.blaze, "blaze_rod")).setCraftingDifficultyAsComponent(200.0F).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials);
      ghastTear = (new Item(114, Material.water, "ghast_tear")).setUnlocalizedName("ghastTear").setPotionEffect("+0-1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing);
      goldNugget = (ItemNugget)(new ItemNugget(115, Material.gold)).setUnlocalizedName("goldNugget");
      netherStalkSeeds = (new ItemSeeds(116, 1, 1, false, false, false, Block.netherStalk.blockID, Block.slowSand.blockID, "VANILLA")).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4").useVanillaTexture("nether_wart");
      potion = (ItemPotion)(new ItemPotion(117)).setUnlocalizedName("potion");
      glassBottle = (new ItemGlassBottle(118)).setUnlocalizedName("glassBottle");
      spiderEye = (new ItemFood(119, Material.meat, 0, 1, true, false, false, "VANILLA")).setAnimalProduct().setPotionEffect(Potion.poison.id, 5, 0, 1.0F).setUnlocalizedName("spiderEye").setPotionEffect(PotionHelper.spiderEyeEffect).useVanillaTexture("spider_eye");
      fermentedSpiderEye = (new Item(120, Material.meat, "spider_eye_fermented")).setUnlocalizedName("fermentedSpiderEye").setPotionEffect(PotionHelper.fermentedSpiderEyeEffect).setCreativeTab(CreativeTabs.tabBrewing);
      blazePowder = (new Item(121, Material.blaze, "blaze_powder")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("blazePowder").setPotionEffect(PotionHelper.blazePowderEffect).setCreativeTab(CreativeTabs.tabBrewing);
      magmaCream = (new Item(122, Material.stone, "magma_cream")).setUnlocalizedName("magmaCream").setPotionEffect(PotionHelper.magmaCreamEffect).setCreativeTab(CreativeTabs.tabBrewing);
      brewingStand = (new ItemReed(123, Block.brewingStand, "brewing_stand")).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing);
      cauldron = (new ItemReed(124, Block.cauldron, "cauldron")).setMaxStackSize(4).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing);
      eyeOfEnder = (new ItemEnderEye(125)).setUnlocalizedName("eyeOfEnder");
      speckledMelon = (new Item(126, Material.fruit, "melon_speckled")).addMaterial(Material.gold).setUnlocalizedName("speckledMelon").setPotionEffect(PotionHelper.speckledMelonEffect).setCreativeTab(CreativeTabs.tabBrewing);
      monsterPlacer = (new ItemMonsterPlacer(127)).setUnlocalizedName("monsterPlacer");
      expBottle = (new ItemExpBottle(128)).setContainerItem(glassBottle).setUnlocalizedName("expBottle");
      fireballCharge = (new ItemFireball(129)).setUnlocalizedName("fireball");
      writableBook = (new ItemWritableBook(130)).setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc);
      writtenBook = (new ItemEditableBook(131)).setUnlocalizedName("writtenBook");
      emerald = (new ItemRock(132, Material.emerald, "emerald")).setXPReward(20).setUnlocalizedName("emerald");
      itemFrame = (new ItemHangingEntity(133, EntityItemFrame.class, "item_frame")).setMaterial(new Material[]{Material.wood, Material.leather}).setUnlocalizedName("frame");
      flowerPot = (new ItemReed(134, Block.flowerPot, "flower_pot")).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations);
      carrot = (new ItemSeedFood(135, 1, 2, false, true, Block.carrot.blockID, Block.tilledField.blockID, "VANILLA")).setUnlocalizedName("carrots").useVanillaTexture("carrot");
      potato = (ItemFood)(new ItemSeedFood(136, 3, 1, false, false, Block.potato.blockID, Block.tilledField.blockID, "VANILLA")).setUnlocalizedName("potato").useVanillaTexture("potato");
      bakedPotato = (ItemFood)(new ItemFood(137, Material.vegetable, 6, 2, false, false, false, "VANILLA")).setPlantProduct().setUnlocalizedName("potatoBaked").useVanillaTexture("potato_baked");
      poisonousPotato = (new ItemFood(138, Material.vegetable, 2, 0, false, false, false, "VANILLA")).setPlantProduct().setPotionEffect(Potion.poison.id, 5, 0, 0.6F).setUnlocalizedName("potatoPoisonous").useVanillaTexture("potato_poisonous");
      emptyMap = (ItemEmptyMap)(new ItemEmptyMap(139)).setUnlocalizedName("emptyMap");
      goldenCarrot = (new ItemFood(140, Material.vegetable, 1, 2, false, false, true, "VANILLA")).setPlantProduct().setUnlocalizedName("carrotGolden").setPotionEffect(PotionHelper.goldenCarrotEffect).useVanillaTexture("carrot_golden");
      skull = (new ItemSkull(141)).setUnlocalizedName("skull");
      carrotOnAStickIron = (new ItemCarrotOnAStick(142, Material.iron)).setUnlocalizedName("carrotOnAStick");
      netherStar = (new ItemSimpleFoiled(143, "nether_star")).setMaterial(new Material[]{Material.stone}).setCraftingDifficultyAsComponent(100.0F).setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials);
      pumpkinPie = (new ItemFood(144, Material.pie, 10, 6, 1000, true, false, true, "VANILLA")).setMaxStackSize(8).setPlantProduct().setAnimalProduct().setUnlocalizedName("pumpkinPie").useVanillaTexture("pumpkin_pie");
      firework = (new ItemFirework(145)).setUnlocalizedName("fireworks");
      fireworkCharge = (new ItemFireworkCharge(146)).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc);
      enchantedBook = (ItemEnchantedBook)(new ItemEnchantedBook(147)).setMaxStackSize(1).setUnlocalizedName("enchantedBook");
      comparator = (new ItemReed(148, Block.redstoneComparatorIdle, "comparator")).setMaterial(new Material[]{Material.redstone, Material.coal, Material.quartz, Material.stone}).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone);
      netherrackBrick = (new ItemBrick(149, Material.stone, "netherbrick")).setUnlocalizedName("netherbrick");
      netherQuartz = (new ItemRock(150, Material.quartz, "quartz")).setXPReward(10).setMaxStackSize(16).setUnlocalizedName("netherquartz");
      minecartTnt = (new ItemMinecart(151, 3, "minecart_tnt")).setUnlocalizedName("minecartTnt");
      minecartHopper = (new ItemMinecart(152, 5, "minecart_hopper")).setUnlocalizedName("minecartHopper");
      horseArmorIron = (ItemHorseArmor)(new ItemHorseArmor(161, Material.iron)).setUnlocalizedName("horsearmormetal").useVanillaTexture("iron_horse_armor");
      horseArmorGold = (ItemHorseArmor)(new ItemHorseArmor(162, Material.gold)).setUnlocalizedName("horsearmorgold").useVanillaTexture("gold_horse_armor");
      leash = (new ItemLeash(164)).setUnlocalizedName("leash");
      nameTag = (new ItemNameTag(165)).setUnlocalizedName("nameTag");
      copperNugget = (ItemNugget)(new ItemNugget(700, Material.copper)).setUnlocalizedName("copperNugget");
      silverNugget = (ItemNugget)(new ItemNugget(701, Material.silver)).setUnlocalizedName("silverNugget");
      ironNugget = (ItemNugget)(new ItemNugget(702, Material.iron)).setUnlocalizedName("ironNugget");
      mithrilNugget = (ItemNugget)(new ItemNugget(703, Material.mithril)).setUnlocalizedName("mithrilNugget");
      adamantiumNugget = (ItemNugget)(new ItemNugget(704, Material.adamantium)).setUnlocalizedName("adamantiumNugget");
      ancientMetalNugget = (ItemNugget)(new ItemNugget(705, Material.ancient_metal)).setUnlocalizedName("ancientMetalNugget");
      ingotCopper = (new ItemIngot(706, Material.copper)).setXPReward(10).setUnlocalizedName("ingotCopper");
      ingotSilver = (new ItemIngot(707, Material.silver)).setXPReward(15).setUnlocalizedName("ingotSilver");
      ingotMithril = (new ItemIngot(708, Material.mithril)).setXPReward(40).setUnlocalizedName("ingotMithril");
      ingotAdamantium = (new ItemIngot(709, Material.adamantium)).setXPReward(100).setUnlocalizedName("ingotAdamantium");
      ingotAncientMetal = (new ItemIngot(710, Material.ancient_metal)).setXPReward(20).setUnlocalizedName("ingotAncientMetal");
      pickaxeCopper = (new ItemPickaxe(711, Material.copper)).setUnlocalizedName("pickaxeCopper");
      pickaxeSilver = (new ItemPickaxe(712, Material.silver)).setUnlocalizedName("pickaxeSilver");
      pickaxeMithril = (new ItemPickaxe(713, Material.mithril)).setUnlocalizedName("pickaxeMithril");
      pickaxeAdamantium = (new ItemPickaxe(714, Material.adamantium)).setUnlocalizedName("pickaxeAdamantium");
      pickaxeRustedIron = (new ItemPickaxe(715, Material.rusted_iron)).setUnlocalizedName("pickaxeRustedIron");
      pickaxeAncientMetal = (new ItemPickaxe(983, Material.ancient_metal)).setUnlocalizedName("pickaxeAncientMetal");
      shovelFlint = (new ItemShovel(716, Material.flint)).setUnlocalizedName("shovelFlint");
      shovelCopper = (new ItemShovel(717, Material.copper)).setUnlocalizedName("shovelCopper");
      shovelSilver = (new ItemShovel(718, Material.silver)).setUnlocalizedName("shovelSilver");
      shovelMithril = (new ItemShovel(719, Material.mithril)).setUnlocalizedName("shovelMithril");
      shovelAdamantium = (new ItemShovel(720, Material.adamantium)).setUnlocalizedName("shovelAdamantium");
      shovelRustedIron = (new ItemShovel(721, Material.rusted_iron)).setUnlocalizedName("shovelRustedIron");
      shovelAncientMetal = (new ItemShovel(984, Material.ancient_metal)).setUnlocalizedName("shovelAncientMetal");
      hatchetFlint = (new ItemHatchet(722, Material.flint)).setUnlocalizedName("hatchetFlint");
      axeFlint = (new ItemAxe(723, Material.flint)).setUnlocalizedName("axeFlint");
      axeCopper = (new ItemAxe(724, Material.copper)).setUnlocalizedName("axeCopper");
      axeSilver = (new ItemAxe(725, Material.silver)).setUnlocalizedName("axeSilver");
      axeMithril = (new ItemAxe(726, Material.mithril)).setUnlocalizedName("axeMithril");
      axeAdamantium = (new ItemAxe(727, Material.adamantium)).setUnlocalizedName("axeAdamantium");
      axeRustedIron = (new ItemAxe(728, Material.rusted_iron)).setUnlocalizedName("axeRustedIron");
      axeAncientMetal = (new ItemAxe(985, Material.ancient_metal)).setUnlocalizedName("axeAncientMetal");
      hoeCopper = (new ItemHoe(729, Material.copper)).setUnlocalizedName("hoeCopper");
      hoeSilver = (new ItemHoe(730, Material.silver)).setUnlocalizedName("hoeSilver");
      hoeMithril = (new ItemHoe(731, Material.mithril)).setUnlocalizedName("hoeMithril");
      hoeAdamantium = (new ItemHoe(732, Material.adamantium)).setUnlocalizedName("hoeAdamantium");
      hoeRustedIron = (new ItemHoe(733, Material.rusted_iron)).setUnlocalizedName("hoeRustedIron");
      hoeAncientMetal = (new ItemHoe(986, Material.ancient_metal)).setUnlocalizedName("hoeAncientMetal");
      warHammerCopper = (new ItemWarHammer(734, Material.copper)).setUnlocalizedName("warHammerCopper");
      warHammerSilver = (new ItemWarHammer(735, Material.silver)).setUnlocalizedName("warHammerSilver");
      warHammerGold = (new ItemWarHammer(736, Material.gold)).setUnlocalizedName("warHammerGold");
      warHammerIron = (new ItemWarHammer(737, Material.iron)).setUnlocalizedName("warHammerIron");
      warHammerMithril = (new ItemWarHammer(738, Material.mithril)).setUnlocalizedName("warHammerMithril");
      warHammerAdamantium = (new ItemWarHammer(739, Material.adamantium)).setUnlocalizedName("warHammerAdamantium");
      warHammerRustedIron = (new ItemWarHammer(740, Material.rusted_iron)).setUnlocalizedName("warHammerRustedIron");
      warHammerAncientMetal = (new ItemWarHammer(987, Material.ancient_metal)).setUnlocalizedName("warHammerAncientMetal");
      mattockCopper = (new ItemMattock(741, Material.copper)).setUnlocalizedName("mattockCopper");
      mattockSilver = (new ItemMattock(742, Material.silver)).setUnlocalizedName("mattockSilver");
      mattockGold = (new ItemMattock(743, Material.gold)).setUnlocalizedName("mattockGold");
      mattockIron = (new ItemMattock(744, Material.iron)).setUnlocalizedName("mattockIron");
      mattockMithril = (new ItemMattock(745, Material.mithril)).setUnlocalizedName("mattockMithril");
      mattockAdamantium = (new ItemMattock(746, Material.adamantium)).setUnlocalizedName("mattockAdamantium");
      mattockRustedIron = (new ItemMattock(747, Material.rusted_iron)).setUnlocalizedName("mattockRustedIron");
      mattockAncientMetal = (new ItemMattock(988, Material.ancient_metal)).setUnlocalizedName("mattockAncientMetal");
      battleAxeCopper = (new ItemBattleAxe(748, Material.copper)).setUnlocalizedName("battleAxeCopper");
      battleAxeSilver = (new ItemBattleAxe(749, Material.silver)).setUnlocalizedName("battleAxeSilver");
      battleAxeGold = (new ItemBattleAxe(750, Material.gold)).setUnlocalizedName("battleAxeGold");
      battleAxeIron = (new ItemBattleAxe(751, Material.iron)).setUnlocalizedName("battleAxeIron");
      battleAxeMithril = (new ItemBattleAxe(752, Material.mithril)).setUnlocalizedName("battleAxeMithril");
      battleAxeAdamantium = (new ItemBattleAxe(753, Material.adamantium)).setUnlocalizedName("battleAxeAdamantium");
      battleAxeRustedIron = (new ItemBattleAxe(754, Material.rusted_iron)).setUnlocalizedName("battleAxeRustedIron");
      battleAxeAncientMetal = (new ItemBattleAxe(989, Material.ancient_metal)).setUnlocalizedName("battleAxeAncientMetal");
      scytheCopper = (new ItemScythe(755, Material.copper)).setUnlocalizedName("scytheCopper");
      scytheSilver = (new ItemScythe(756, Material.silver)).setUnlocalizedName("scytheSilver");
      scytheGold = (new ItemScythe(757, Material.gold)).setUnlocalizedName("scytheGold");
      scytheIron = (new ItemScythe(758, Material.iron)).setUnlocalizedName("scytheIron");
      scytheMithril = (new ItemScythe(759, Material.mithril)).setUnlocalizedName("scytheMithril");
      scytheAdamantium = (new ItemScythe(760, Material.adamantium)).setUnlocalizedName("scytheAdamantium");
      scytheRustedIron = (new ItemScythe(761, Material.rusted_iron)).setUnlocalizedName("scytheRustedIron");
      scytheAncientMetal = (new ItemScythe(990, Material.ancient_metal)).setUnlocalizedName("scytheAncientMetal");
      shearsCopper = (ItemShears)(new ItemShears(762, Material.copper)).setUnlocalizedName("shearsCopper");
      shearsSilver = (ItemShears)(new ItemShears(763, Material.silver)).setUnlocalizedName("shearsSilver");
      shearsGold = (ItemShears)(new ItemShears(764, Material.gold)).setUnlocalizedName("shearsGold");
      shearsMithril = (ItemShears)(new ItemShears(765, Material.mithril)).setUnlocalizedName("shearsMithril");
      shearsAdamantium = (ItemShears)(new ItemShears(766, Material.adamantium)).setUnlocalizedName("shearsAdamantium");
      shearsAncientMetal = (ItemShears)(new ItemShears(991, Material.ancient_metal)).setUnlocalizedName("shearsAncientMetal");
      knifeFlint = (new ItemKnife(767, Material.flint)).setUnlocalizedName("knifeFlint");
      cudgelWood = (new ItemCudgel(768, Material.wood)).setUnlocalizedName("cudgelWood");
      clubWood = (new ItemClub(769, Material.wood)).setUnlocalizedName("clubWood");
      swordCopper = (new ItemSword(772, Material.copper)).setUnlocalizedName("swordCopper");
      swordSilver = (new ItemSword(773, Material.silver)).setUnlocalizedName("swordSilver");
      swordMithril = (new ItemSword(774, Material.mithril)).setUnlocalizedName("swordMithril");
      swordAdamantium = (new ItemSword(775, Material.adamantium)).setUnlocalizedName("swordAdamantium");
      swordRustedIron = (new ItemSword(776, Material.rusted_iron)).setUnlocalizedName("swordRustedIron");
      swordAncientMetal = (new ItemSword(992, Material.ancient_metal)).setUnlocalizedName("swordAncientMetal");
      daggerCopper = (new ItemDagger(777, Material.copper)).setUnlocalizedName("daggerCopper");
      daggerSilver = (new ItemDagger(778, Material.silver)).setUnlocalizedName("daggerSilver");
      daggerGold = (new ItemDagger(779, Material.gold)).setUnlocalizedName("daggerGold");
      daggerIron = (new ItemDagger(780, Material.iron)).setUnlocalizedName("daggerIron");
      daggerMithril = (new ItemDagger(781, Material.mithril)).setUnlocalizedName("daggerMithril");
      daggerAdamantium = (new ItemDagger(782, Material.adamantium)).setUnlocalizedName("daggerAdamantium");
      daggerRustedIron = (new ItemDagger(783, Material.rusted_iron)).setUnlocalizedName("daggerRustedIron");
      daggerAncientMetal = (new ItemDagger(993, Material.ancient_metal)).setUnlocalizedName("daggerAncientMetal");
      arrowFlint = (ItemArrow)(new ItemArrow(784, Material.flint)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowFlint");
      arrowCopper = (ItemArrow)(new ItemArrow(785, Material.copper)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowCopper");
      arrowSilver = (ItemArrow)(new ItemArrow(786, Material.silver)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowSilver");
      arrowGold = (ItemArrow)(new ItemArrow(787, Material.gold)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowGold");
      arrowIron = (ItemArrow)(new ItemArrow(788, Material.iron)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowIron");
      arrowMithril = (ItemArrow)(new ItemArrow(789, Material.mithril)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowMithril");
      arrowAdamantium = (ItemArrow)(new ItemArrow(790, Material.adamantium)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowAdamantium");
      arrowRustedIron = (ItemArrow)(new ItemArrow(791, Material.rusted_iron)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowRustedIron");
      arrowAncientMetal = (ItemArrow)(new ItemArrow(994, Material.ancient_metal)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowAncientMetal");
      bowMithril = (ItemBow)(new ItemBow(792, Material.mithril)).setUnlocalizedName("bowMithril");
      bowAncientMetal = (ItemBow)(new ItemBow(995, Material.ancient_metal)).setUnlocalizedName("bowAncientMetal");
      arrowObsidian = (ItemArrow)(new ItemArrow(793, Material.obsidian)).setCraftingDifficultyAsComponent(40.0F).setUnlocalizedName("arrowObsidian");
      knifeCopper = (new ItemKnife(794, Material.copper)).setUnlocalizedName("knifeCopper");
      knifeSilver = (new ItemKnife(795, Material.silver)).setUnlocalizedName("knifeSilver");
      knifeGold = (new ItemKnife(796, Material.gold)).setUnlocalizedName("knifeGold");
      knifeIron = (new ItemKnife(797, Material.iron)).setUnlocalizedName("knifeIron");
      knifeMithril = (new ItemKnife(798, Material.mithril)).setUnlocalizedName("knifeMithril");
      knifeAdamantium = (new ItemKnife(799, Material.adamantium)).setUnlocalizedName("knifeAdamantium");
      knifeRustedIron = (new ItemKnife(800, Material.rusted_iron)).setUnlocalizedName("knifeRustedIron");
      knifeAncientMetal = (new ItemKnife(801, Material.ancient_metal)).setUnlocalizedName("knifeAncientMetal");
      helmetCopper = (ItemHelmet)(new ItemHelmet(811, Material.copper, false)).setUnlocalizedName("helmetCopper");
      helmetSilver = (ItemHelmet)(new ItemHelmet(812, Material.silver, false)).setUnlocalizedName("helmetSilver");
      helmetMithril = (ItemHelmet)(new ItemHelmet(813, Material.mithril, false)).setUnlocalizedName("helmetMithril");
      helmetAdamantium = (ItemHelmet)(new ItemHelmet(814, Material.adamantium, false)).setUnlocalizedName("helmetAdamantium");
      helmetAncientMetal = (ItemHelmet)(new ItemHelmet(996, Material.ancient_metal, false)).setUnlocalizedName("helmetAncientMetal");
      helmetChainCopper = (ItemHelmet)(new ItemHelmet(815, Material.copper, true)).setUnlocalizedName("helmetChainCopper");
      helmetChainSilver = (ItemHelmet)(new ItemHelmet(816, Material.silver, true)).setUnlocalizedName("helmetChainSilver");
      helmetChainGold = (ItemHelmet)(new ItemHelmet(817, Material.gold, true)).setUnlocalizedName("helmetChainGold");
      helmetChainMithril = (ItemHelmet)(new ItemHelmet(818, Material.mithril, true)).setUnlocalizedName("helmetChainMithril");
      helmetChainAdamantium = (ItemHelmet)(new ItemHelmet(819, Material.adamantium, true)).setUnlocalizedName("helmetChainAdamantium");
      helmetRustedIron = (ItemHelmet)(new ItemHelmet(820, Material.rusted_iron, false)).setUnlocalizedName("helmetRustedIron");
      helmetChainRustedIron = (ItemHelmet)(new ItemHelmet(821, Material.rusted_iron, true)).setUnlocalizedName("helmetChainRustedIron");
      helmetChainAncientMetal = (ItemHelmet)(new ItemHelmet(997, Material.ancient_metal, true)).setUnlocalizedName("helmetChainAncientMetal");
      plateCopper = (ItemCuirass)(new ItemCuirass(822, Material.copper, false)).setUnlocalizedName("chestplateCopper");
      plateSilver = (ItemCuirass)(new ItemCuirass(823, Material.silver, false)).setUnlocalizedName("chestplateSilver");
      plateMithril = (ItemCuirass)(new ItemCuirass(824, Material.mithril, false)).setUnlocalizedName("chestplateMithril");
      plateAdamantium = (ItemCuirass)(new ItemCuirass(825, Material.adamantium, false)).setUnlocalizedName("chestplateAdamantium");
      plateAncientMetal = (ItemCuirass)(new ItemCuirass(998, Material.ancient_metal, false)).setUnlocalizedName("chestplateAncientMetal");
      plateChainCopper = (ItemCuirass)(new ItemCuirass(826, Material.copper, true)).setUnlocalizedName("chestplateChainCopper");
      plateChainSilver = (ItemCuirass)(new ItemCuirass(827, Material.silver, true)).setUnlocalizedName("chestplateChainSilver");
      plateChainGold = (ItemCuirass)(new ItemCuirass(828, Material.gold, true)).setUnlocalizedName("chestplateChainGold");
      plateChainMithril = (ItemCuirass)(new ItemCuirass(829, Material.mithril, true)).setUnlocalizedName("chestplateChainMithril");
      plateChainAdamantium = (ItemCuirass)(new ItemCuirass(830, Material.adamantium, true)).setUnlocalizedName("chestplateChainAdamantium");
      plateRustedIron = (ItemCuirass)(new ItemCuirass(831, Material.rusted_iron, false)).setUnlocalizedName("chestplateRustedIron");
      plateChainRustedIron = (ItemCuirass)(new ItemCuirass(832, Material.rusted_iron, true)).setUnlocalizedName("chestplateChainRustedIron");
      plateChainAncientMetal = (ItemCuirass)(new ItemCuirass(999, Material.ancient_metal, true)).setUnlocalizedName("chestplateChainAncientMetal");
      legsCopper = (ItemLeggings)(new ItemLeggings(833, Material.copper, false)).setUnlocalizedName("leggingsCopper");
      legsSilver = (ItemLeggings)(new ItemLeggings(834, Material.silver, false)).setUnlocalizedName("leggingsSilver");
      legsMithril = (ItemLeggings)(new ItemLeggings(835, Material.mithril, false)).setUnlocalizedName("leggingsMithril");
      legsAdamantium = (ItemLeggings)(new ItemLeggings(836, Material.adamantium, false)).setUnlocalizedName("leggingsAdamantium");
      legsAncientMetal = (ItemLeggings)(new ItemLeggings(1000, Material.ancient_metal, false)).setUnlocalizedName("leggingsAncientMetal");
      legsChainCopper = (ItemLeggings)(new ItemLeggings(837, Material.copper, true)).setUnlocalizedName("leggingsChainCopper");
      legsChainSilver = (ItemLeggings)(new ItemLeggings(838, Material.silver, true)).setUnlocalizedName("leggingsChainSilver");
      legsChainGold = (ItemLeggings)(new ItemLeggings(839, Material.gold, true)).setUnlocalizedName("leggingsChainGold");
      legsChainMithril = (ItemLeggings)(new ItemLeggings(840, Material.mithril, true)).setUnlocalizedName("leggingsChainMithril");
      legsChainAdamantium = (ItemLeggings)(new ItemLeggings(841, Material.adamantium, true)).setUnlocalizedName("leggingsChainAdamantium");
      legsRustedIron = (ItemLeggings)(new ItemLeggings(842, Material.rusted_iron, false)).setUnlocalizedName("leggingsRustedIron");
      legsChainRustedIron = (ItemLeggings)(new ItemLeggings(843, Material.rusted_iron, true)).setUnlocalizedName("leggingsChainRustedIron");
      legsChainAncientMetal = (ItemLeggings)(new ItemLeggings(1001, Material.ancient_metal, true)).setUnlocalizedName("leggingsChainAncientMetal");
      bootsCopper = (ItemBoots)(new ItemBoots(844, Material.copper, false)).setUnlocalizedName("bootsCopper");
      bootsSilver = (ItemBoots)(new ItemBoots(845, Material.silver, false)).setUnlocalizedName("bootsSilver");
      bootsMithril = (ItemBoots)(new ItemBoots(846, Material.mithril, false)).setUnlocalizedName("bootsMithril");
      bootsAdamantium = (ItemBoots)(new ItemBoots(847, Material.adamantium, false)).setUnlocalizedName("bootsAdamantium");
      bootsAncientMetal = (ItemBoots)(new ItemBoots(1002, Material.ancient_metal, false)).setUnlocalizedName("bootsAncientMetal");
      bootsChainCopper = (ItemBoots)(new ItemBoots(848, Material.copper, true)).setUnlocalizedName("bootsChainCopper");
      bootsChainSilver = (ItemBoots)(new ItemBoots(849, Material.silver, true)).setUnlocalizedName("bootsChainSilver");
      bootsChainGold = (ItemBoots)(new ItemBoots(850, Material.gold, true)).setUnlocalizedName("bootsChainGold");
      bootsChainMithril = (ItemBoots)(new ItemBoots(851, Material.mithril, true)).setUnlocalizedName("bootsChainMithril");
      bootsChainAdamantium = (ItemBoots)(new ItemBoots(852, Material.adamantium, true)).setUnlocalizedName("bootsChainAdamantium");
      bootsRustedIron = (ItemBoots)(new ItemBoots(853, Material.rusted_iron, false)).setUnlocalizedName("bootsRustedIron");
      bootsChainRustedIron = (ItemBoots)(new ItemBoots(854, Material.rusted_iron, true)).setUnlocalizedName("bootsChainRustedIron");
      bootsChainAncientMetal = (ItemBoots)(new ItemBoots(1003, Material.ancient_metal, true)).setUnlocalizedName("bootsChainAncientMetal");
      doorCopper = (new ItemDoor(855, Material.copper)).setUnlocalizedName("doorCopper");
      doorSilver = (new ItemDoor(856, Material.silver)).setUnlocalizedName("doorSilver");
      doorGold = (new ItemDoor(857, Material.gold)).setUnlocalizedName("doorGold");
      doorMithril = (new ItemDoor(858, Material.mithril)).setUnlocalizedName("doorMithril");
      doorAdamantium = (new ItemDoor(859, Material.adamantium)).setUnlocalizedName("doorAdamantium");
      doorAncientMetal = (new ItemDoor(1004, Material.ancient_metal)).setUnlocalizedName("doorAncientMetal");
      shardEmerald = (new ItemShard(861, Material.emerald)).setUnlocalizedName("shardEmerald");
      shardDiamond = (new ItemShard(862, Material.diamond)).setUnlocalizedName("shardDiamond");
      shardNetherQuartz = (new ItemShard(863, Material.quartz)).setUnlocalizedName("shardNetherQuartz");
      shardGlass = (new ItemShard(864, Material.glass)).setUnlocalizedName("shardGlass");
      chipFlint = (new ItemShard(865, Material.flint)).setUnlocalizedName("chipFlint");
      shardObsidian = (new ItemShard(866, Material.obsidian)).setUnlocalizedName("shardObsidian");
      chainCopper = (new ItemChain(867, Material.copper)).setUnlocalizedName("chainCopper");
      chainSilver = (new ItemChain(868, Material.silver)).setUnlocalizedName("chainSilver");
      chainGold = (new ItemChain(869, Material.gold)).setUnlocalizedName("chainGold");
      chainIron = (new ItemChain(870, Material.iron)).setUnlocalizedName("chainIron");
      chainMithril = (new ItemChain(871, Material.mithril)).setUnlocalizedName("chainMithril");
      chainAdamantium = (new ItemChain(872, Material.adamantium)).setUnlocalizedName("chainAdamantium");
      chainRustedIron = (new ItemChain(873, Material.rusted_iron)).setUnlocalizedName("chainRustedIron");
      chainAncientMetal = (new ItemChain(1005, Material.ancient_metal)).setUnlocalizedName("chainAncientMetal");
      horseArmorCopper = (ItemHorseArmor)(new ItemHorseArmor(874, Material.copper)).setUnlocalizedName("horsearmorcopper");
      horseArmorSilver = (ItemHorseArmor)(new ItemHorseArmor(875, Material.silver)).setUnlocalizedName("horsearmorsilver");
      horseArmorMithril = (ItemHorseArmor)(new ItemHorseArmor(876, Material.mithril)).setUnlocalizedName("horsearmormithril");
      horseArmorAdamantium = (ItemHorseArmor)(new ItemHorseArmor(877, Material.adamantium)).setUnlocalizedName("horsearmoradamantium");
      horseArmorAncientMetal = (ItemHorseArmor)(new ItemHorseArmor(878, Material.ancient_metal)).setUnlocalizedName("horsearmorancientmetal");
      bucketCopperEmpty = (ItemBucket)(new ItemBucket(886, Material.copper, (Material)null)).setUnlocalizedName("bucketCopper");
      bucketSilverEmpty = (ItemBucket)(new ItemBucket(887, Material.silver, (Material)null)).setUnlocalizedName("bucketSilver");
      bucketGoldEmpty = (ItemBucket)(new ItemBucket(888, Material.gold, (Material)null)).setUnlocalizedName("bucketGold");
      bucketMithrilEmpty = (ItemBucket)(new ItemBucket(889, Material.mithril, (Material)null)).setUnlocalizedName("bucketMithril");
      bucketAdamantiumEmpty = (ItemBucket)(new ItemBucket(890, Material.adamantium, (Material)null)).setUnlocalizedName("bucketAdamantium");
      bucketAncientMetalEmpty = (ItemBucket)(new ItemBucket(891, Material.ancient_metal, (Material)null)).setUnlocalizedName("bucketAncientMetal");
      bucketCopperWater = (ItemBucket)(new ItemBucket(892, Material.copper, Material.water)).setUnlocalizedName("bucketCopperWater").setContainerItem(bucketCopperEmpty);
      bucketSilverWater = (ItemBucket)(new ItemBucket(893, Material.silver, Material.water)).setUnlocalizedName("bucketSilverWater").setContainerItem(bucketSilverEmpty);
      bucketGoldWater = (ItemBucket)(new ItemBucket(894, Material.gold, Material.water)).setUnlocalizedName("bucketGoldWater").setContainerItem(bucketGoldEmpty);
      bucketMithrilWater = (ItemBucket)(new ItemBucket(895, Material.mithril, Material.water)).setUnlocalizedName("bucketMithrilWater").setContainerItem(bucketMithrilEmpty);
      bucketAdamantiumWater = (ItemBucket)(new ItemBucket(896, Material.adamantium, Material.water)).setUnlocalizedName("bucketAdamantiumWater").setContainerItem(bucketAdamantiumEmpty);
      bucketAncientMetalWater = (ItemBucket)(new ItemBucket(897, Material.ancient_metal, Material.water)).setUnlocalizedName("bucketAncientMetalWater").setContainerItem(bucketAncientMetalEmpty);
      bucketCopperLava = (ItemBucket)(new ItemBucket(898, Material.copper, Material.lava)).setUnlocalizedName("bucketCopperLava").setContainerItem(bucketCopperEmpty);
      bucketSilverLava = (ItemBucket)(new ItemBucket(899, Material.silver, Material.lava)).setUnlocalizedName("bucketSilverLava").setContainerItem(bucketSilverEmpty);
      bucketGoldLava = (ItemBucket)(new ItemBucket(900, Material.gold, Material.lava)).setUnlocalizedName("bucketGoldLava").setContainerItem(bucketGoldEmpty);
      bucketMithrilLava = (ItemBucket)(new ItemBucket(901, Material.mithril, Material.lava)).setUnlocalizedName("bucketMithrilLava").setContainerItem(bucketMithrilEmpty);
      bucketAdamantiumLava = (ItemBucket)(new ItemBucket(902, Material.adamantium, Material.lava)).setUnlocalizedName("bucketAdamantiumLava").setContainerItem(bucketAdamantiumEmpty);
      bucketAncientMetalLava = (ItemBucket)(new ItemBucket(903, Material.ancient_metal, Material.lava)).setUnlocalizedName("bucketAncientMetalLava").setContainerItem(bucketAncientMetalEmpty);
      bucketCopperMilk = (ItemBucketMilk)(new ItemBucketMilk(904, Material.copper)).setUnlocalizedName("bucketCopperMilk").setContainerItem(bucketCopperEmpty);
      bucketSilverMilk = (ItemBucketMilk)(new ItemBucketMilk(905, Material.silver)).setUnlocalizedName("bucketSilverMilk").setContainerItem(bucketSilverEmpty);
      bucketGoldMilk = (ItemBucketMilk)(new ItemBucketMilk(906, Material.gold)).setUnlocalizedName("bucketGoldMilk").setContainerItem(bucketGoldEmpty);
      bucketMithrilMilk = (ItemBucketMilk)(new ItemBucketMilk(907, Material.mithril)).setUnlocalizedName("bucketMithrilMilk").setContainerItem(bucketMithrilEmpty);
      bucketAdamantiumMilk = (ItemBucketMilk)(new ItemBucketMilk(908, Material.adamantium)).setUnlocalizedName("bucketAdamantiumMilk").setContainerItem(bucketAdamantiumEmpty);
      bucketAncientMetalMilk = (ItemBucketMilk)(new ItemBucketMilk(909, Material.ancient_metal)).setUnlocalizedName("bucketAncientMetalMilk").setContainerItem(bucketAncientMetalEmpty);
      bowlMilk = (ItemBowl)(new ItemBowl(910, Material.milk, "bowl_milk")).setFoodValue(0, 1, true, false, false).setAnimalProduct().setContainerItem(bowlEmpty).setAlwaysEdible().setUnlocalizedName("bowlMilk");
      bowlWater = (ItemBowl)(new ItemBowl(911, Material.water, "bowl_water")).setContainerItem(bowlEmpty).setUnlocalizedName("bowlWater");
      lambchopRaw = (ItemMeat)(new ItemMeat(916, 3, 3, false, false, "lambchop_raw")).setUnlocalizedName("lambchopRaw");
      lambchopCooked = (ItemMeat)(new ItemMeat(917, 6, 6, false, true, "lambchop_cooked")).setUnlocalizedName("lambchopCooked");
      sinew = (new Item(918, Material.leather, "sinew")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("sinew").setCreativeTab(CreativeTabs.tabMaterials);
      hatchetCopper = (new ItemHatchet(919, Material.copper)).setUnlocalizedName("hatchetCopper");
      hatchetSilver = (new ItemHatchet(920, Material.silver)).setUnlocalizedName("hatchetSilver");
      hatchetGold = (new ItemHatchet(921, Material.gold)).setUnlocalizedName("hatchetGold");
      hatchetIron = (new ItemHatchet(922, Material.iron)).setUnlocalizedName("hatchetIron");
      hatchetMithril = (new ItemHatchet(923, Material.mithril)).setUnlocalizedName("hatchetMithril");
      hatchetAdamantium = (new ItemHatchet(924, Material.adamantium)).setUnlocalizedName("hatchetAdamantium");
      hatchetRustedIron = (new ItemHatchet(925, Material.rusted_iron)).setUnlocalizedName("hatchetRustedIron");
      hatchetAncientMetal = (new ItemHatchet(1006, Material.ancient_metal)).setUnlocalizedName("hatchetAncientMetal");
      shearsRustedIron = (ItemShears)(new ItemShears(926, Material.rusted_iron)).setUnlocalizedName("shearsRustedIron");
      cheese = (new ItemFood(927, Material.cheese, 3, 3, true, false, false, "cheese")).setAnimalProduct().setUnlocalizedName("cheese");
      flour = (new Item(928, Material.flour, "food/flour")).setCraftingDifficultyAsComponent(25.0F).setUnlocalizedName("flour").setCreativeTab(CreativeTabs.tabFood);
      dough = (ItemFood)(new ItemFood(929, Material.dough, 6, 2, false, false, false, "dough")).setPlantProduct().setUnlocalizedName("dough");
      chocolate = (new ItemFood(930, Material.desert, 3, 3, 1000, false, false, false, "chocolate")).setPlantProduct().setUnlocalizedName("chocolate");
      onion = (new ItemSeedFood(931, 1, 1, false, true, Block.onions.blockID, Block.tilledField.blockID, "onion")).setUnlocalizedName("onion");
      bowlBeefStew = (ItemBowl)(new ItemBowl(932, Material.beef_stew, "beef_stew")).setFoodValue(16, 16, true, false, true).setPlantProduct().setAnimalProduct().setUnlocalizedName("beefStew");
      bowlChickenSoup = (ItemBowl)(new ItemBowl(933, Material.chicken_soup, "chicken_soup")).setFoodValue(10, 10, true, false, true).setPlantProduct().setAnimalProduct().setUnlocalizedName("chickenSoup");
      bowlVegetableSoup = (ItemBowl)(new ItemBowl(934, Material.vegetable_soup, "vegetable_soup")).setFoodValue(6, 6, false, false, true).setPlantProduct().setUnlocalizedName("vegetableSoup");
      manure = (new ItemManure(935)).setUnlocalizedName("manure");
      fishingRodCopper = (ItemFishingRod)(new ItemFishingRod(936, Material.copper)).setUnlocalizedName("fishingRod");
      fishingRodSilver = (ItemFishingRod)(new ItemFishingRod(937, Material.silver)).setUnlocalizedName("fishingRod");
      fishingRodGold = (ItemFishingRod)(new ItemFishingRod(938, Material.gold)).setUnlocalizedName("fishingRod");
      fishingRodMithril = (ItemFishingRod)(new ItemFishingRod(939, Material.mithril)).setUnlocalizedName("fishingRod");
      fishingRodAdamantium = (ItemFishingRod)(new ItemFishingRod(940, Material.adamantium)).setUnlocalizedName("fishingRod");
      fishingRodAncientMetal = (ItemFishingRod)(new ItemFishingRod(1007, Material.ancient_metal)).setUnlocalizedName("fishingRod");
      carrotOnAStickCopper = (new ItemCarrotOnAStick(941, Material.copper)).setUnlocalizedName("carrotOnAStick");
      carrotOnAStickSilver = (new ItemCarrotOnAStick(942, Material.silver)).setUnlocalizedName("carrotOnAStick");
      carrotOnAStickGold = (new ItemCarrotOnAStick(943, Material.gold)).setUnlocalizedName("carrotOnAStick");
      carrotOnAStickMithril = (new ItemCarrotOnAStick(944, Material.mithril)).setUnlocalizedName("carrotOnAStick");
      carrotOnAStickAdamantium = (new ItemCarrotOnAStick(945, Material.adamantium)).setUnlocalizedName("carrotOnAStick");
      carrotOnAStickAncientMetal = (new ItemCarrotOnAStick(1008, Material.ancient_metal)).setUnlocalizedName("carrotOnAStick");
      shovelObsidian = (new ItemShovel(946, Material.obsidian)).setUnlocalizedName("shovelObsidian");
      knifeObsidian = (new ItemKnife(947, Material.obsidian)).setUnlocalizedName("knifeObsidian");
      hatchetObsidian = (new ItemHatchet(948, Material.obsidian)).setUnlocalizedName("hatchetObsidian");
      axeObsidian = (new ItemAxe(949, Material.obsidian)).setUnlocalizedName("axeObsidian");
      bowlIceCream = (ItemBowl)(new ItemBowl(950, Material.ice_cream, "ice_cream")).setFoodValue(5, 4, 1000, true, false, false).setPlantProduct().setAnimalProduct().setUnlocalizedName("iceCream");
      fishingRodFlint = (ItemFishingRod)(new ItemFishingRod(951, Material.flint)).setUnlocalizedName("fishingRod");
      carrotOnAStickFlint = (new ItemCarrotOnAStick(952, Material.flint)).setUnlocalizedName("carrotOnAStick");
      bowlSalad = (ItemBowl)(new ItemBowl(953, Material.salad, "bowl_salad")).setFoodValue(1, 1, false, false, true).setPlantProduct().setUnlocalizedName("salad");
      fragsCreeper = (new Item(954, Material.frags, "frag/creeper")).setUnlocalizedName("frags_creeper");
      fragsInfernalCreeper = (new Item(955, Material.frags, "frag/infernal_creeper")).setUnlocalizedName("frags_infernal_creeper");
      fishingRodObsidian = (ItemFishingRod)(new ItemFishingRod(956, Material.obsidian)).setUnlocalizedName("fishingRod");
      carrotOnAStickObsidian = (new ItemCarrotOnAStick(957, Material.obsidian)).setUnlocalizedName("carrotOnAStick");
      bottleOfDisenchanting = (new ItemBottleOfDisenchanting(958)).setUnlocalizedName("bottleOfDisenchanting").setCreativeTab(CreativeTabs.tabMisc);
      bowlCreamOfMushroomSoup = (ItemBowl)(new ItemBowl(959, Material.cream_of_mushroom_soup, "cream_of_mushroom_soup")).setFoodValue(3, 5, true, false, false).setPlantProduct().setAnimalProduct().setUnlocalizedName("creamOfMushroomSoup");
      bowlCreamOfVegetableSoup = (ItemBowl)(new ItemBowl(960, Material.cream_of_vegetable_soup, "cream_of_vegetable_soup")).setFoodValue(7, 7, true, false, true).setPlantProduct().setAnimalProduct().setUnlocalizedName("creamOfVegetableSoup");
      bowlPumpkinSoup = (ItemBowl)(new ItemBowl(961, Material.pumpkin_soup, "pumpkin_soup")).setFoodValue(1, 2, false, false, true).setPlantProduct().setUnlocalizedName("pumpkinSoup");
      orange = (ItemFood)(new ItemFood(962, Material.fruit, 2, 1, 1000, false, false, true, "orange")).setPlantProduct().setUnlocalizedName("orange");
      banana = (ItemFood)(new ItemFood(963, Material.fruit, 2, 1, 1000, false, false, true, "banana")).setPlantProduct().setUnlocalizedName("banana");
      coinCopper = (ItemCoin)(new ItemCoin(964, Material.copper)).setUnlocalizedName("coinCopper");
      coinSilver = (ItemCoin)(new ItemCoin(965, Material.silver)).setUnlocalizedName("coinSilver");
      coinGold = (ItemCoin)(new ItemCoin(966, Material.gold)).setUnlocalizedName("coinGold");
      bowlMashedPotato = (ItemBowl)(new ItemBowl(967, Material.mashed_potato, "mashed_potato")).setFoodValue(12, 8, true, false, false).setPlantProduct().setAnimalProduct().setUnlocalizedName("mashedPotato");
      bowlSorbet = (ItemBowl)(new ItemBowl(968, Material.sorbet, "sorbet")).setFoodValue(4, 2, 2000, false, false, true).setPlantProduct().setUnlocalizedName("sorbet");
      blueberries = (ItemFood)(new ItemFood(969, Material.fruit, 1, 1, 1000, false, false, true, "blueberries")).setPlantProduct().setUnlocalizedName("blueberries");
      bowlPorridge = (ItemBowl)(new ItemBowl(970, Material.porridge, "porridge")).setFoodValue(4, 2, 2000, false, false, true).setPlantProduct().setUnlocalizedName("porridge");
      bowlCereal = (ItemBowl)(new ItemBowl(971, Material.cereal, "cereal")).setFoodValue(4, 2, 1000, true, false, false).setPlantProduct().setAnimalProduct().setUnlocalizedName("cereal");
      referencedBook = (new ItemReferencedBook(972)).setUnlocalizedName("referencedBook");
      fishLargeRaw = (ItemMeat)(new ItemMeat(973, 5, 5, true, false, "fish_salmon_raw")).setUnlocalizedName("fishRaw");
      fishLargeCooked = (ItemMeat)(new ItemMeat(974, 10, 10, true, true, "fish_salmon_cooked")).setUnlocalizedName("fishCooked");
      bucketCopperStone = (ItemBucket)(new ItemBucket(975, Material.copper, Material.stone)).setUnlocalizedName("bucketCopperStone").setContainerItem(bucketCopperEmpty);
      bucketSilverStone = (ItemBucket)(new ItemBucket(976, Material.silver, Material.stone)).setUnlocalizedName("bucketSilverStone").setContainerItem(bucketSilverEmpty);
      bucketGoldStone = (ItemBucket)(new ItemBucket(977, Material.gold, Material.stone)).setUnlocalizedName("bucketGoldStone").setContainerItem(bucketGoldEmpty);
      bucketIronStone = (ItemBucket)(new ItemBucket(978, Material.iron, Material.stone)).setUnlocalizedName("bucketIronStone").setContainerItem(bucketEmpty);
      bucketMithrilStone = (ItemBucket)(new ItemBucket(979, Material.mithril, Material.stone)).setUnlocalizedName("bucketMithrilStone").setContainerItem(bucketMithrilEmpty);
      bucketAdamantiumStone = (ItemBucket)(new ItemBucket(980, Material.adamantium, Material.stone)).setUnlocalizedName("bucketAdamantiumStone").setContainerItem(bucketAdamantiumEmpty);
      bucketAncientMetalStone = (ItemBucket)(new ItemBucket(981, Material.ancient_metal, Material.stone)).setUnlocalizedName("bucketAncientMetalStone").setContainerItem(bucketAncientMetalEmpty);
      fragsNetherspawn = (new Item(1020, Material.frags, "frag/netherspawn")).setUnlocalizedName("frags_netherspawn");
      coinAncientMetal = (ItemCoin)(new ItemCoin(1021, Material.ancient_metal)).setUnlocalizedName("coinAncientMetal");
      coinMithril = (ItemCoin)(new ItemCoin(1022, Material.mithril)).setUnlocalizedName("coinMithril");
      coinAdamantium = (ItemCoin)(new ItemCoin(1023, Material.adamantium)).setUnlocalizedName("coinAdamantium");
      wormRaw = (ItemMeat)(new ItemMeat(1024, 0, 1, false, false, "worm_raw")).setUnlocalizedName("wormRaw");
      wormCooked = (ItemMeat)(new ItemMeat(1025, 1, 1, false, true, "worm_cooked")).setUnlocalizedName("wormCooked");
      thrownWeb = (new Item(1026, Material.web, "web")).setUnlocalizedName("web");
      genericFood = new ItemFood();
      record13 = (new ItemRecord(2000, "13", "record_13")).setUnlocalizedName("record");
      recordCat = (new ItemRecord(2001, "cat", "record_cat")).setUnlocalizedName("record");
      recordBlocks = (new ItemRecord(2002, "blocks", "record_blocks")).setUnlocalizedName("record");
      recordChirp = (new ItemRecord(2003, "chirp", "record_chirp")).setUnlocalizedName("record");
      recordFar = (new ItemRecord(2004, "far", "record_far")).setUnlocalizedName("record");
      recordMall = (new ItemRecord(2005, "mall", "record_mall")).setUnlocalizedName("record");
      recordMellohi = (new ItemRecord(2006, "mellohi", "record_mellohi")).setUnlocalizedName("record");
      recordStal = (new ItemRecord(2007, "stal", "record_stal")).setUnlocalizedName("record");
      recordStrad = (new ItemRecord(2008, "strad", "record_strad")).setUnlocalizedName("record");
      recordWard = (new ItemRecord(2009, "ward", "record_ward")).setUnlocalizedName("record");
      record11 = (new ItemRecord(2010, "11", "record_11")).setUnlocalizedName("record");
      recordWait = (new ItemRecord(2011, "wait", "record_wait")).setUnlocalizedName("record");
      recordUnderworld = (new ItemRecord(2020, "imported.underworld", "record_underworld", "Underworld", "The Fat Man")).setUnlocalizedName("record");
      recordDescent = (new ItemRecord(2021, "imported.descent", "record_descent", "Descent", "The Fat Man")).setUnlocalizedName("record");
      recordWanderer = (new ItemRecord(2022, "imported.wanderer", "record_wanderer", "Wanderer", "The Fat Man")).setUnlocalizedName("record");
      recordLegends = (new ItemRecord(2023, "imported.legends", "record_legends", "Legends", "The Fat Man")).setUnlocalizedName("record");
      ItemFood.setCookingResult(porkRaw, porkCooked, 3);
      ItemFood.setCookingResult(fishRaw, fishCooked, 3);
      ItemFood.setCookingResult(beefRaw, beefCooked, 4);
      ItemFood.setCookingResult(chickenRaw, chickenCooked, 3);
      ItemFood.setCookingResult(lambchopRaw, lambchopCooked, 2);
      ItemFood.setCookingResult(fishLargeRaw, fishLargeCooked, 4);
      ItemFood.setCookingResult(potato, bakedPotato, 0);
      ItemFood.setCookingResult(dough, bread, 0);
      ItemFood.setCookingResult(wormRaw, wormCooked, 0);
      StatList.initStats();

      for(int i = 0; i < itemsList.length; ++i) {
         Item item = getItem(i);
         if (item != null) {
            item.validate();
         }
      }

   }

   /* =========================================================== FORGE START ===============================================================*/
   /**
    * Called when a player drops the item into the world,
    * returning false from this will prevent the item from
    * being removed from the players inventory and spawning
    * in the world
    *
    * @param player The player that dropped the item
    * @param item The item stack, before the item is removed.
    */
   public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
   {
      return true;
   }

   /**
    * This is called when the item is used, before the block is activated.
    * @param stack The Item Stack
    * @param player The Player that used the item
    * @param world The Current World
    * @param x Target X Position
    * @param y Target Y Position
    * @param z Target Z Position
    * @param side The side of the target hit
    * @return Return true to prevent any further processing.
    */
   public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
   {
      return false;
   }

   /**
    * Metadata-sensitive version of getStrVsBlock
    * @param itemstack The Item Stack
    * @param block The block the item is trying to break
    * @param metadata The items current metadata
    * @return The damage strength
    */
   public float getStrVsBlock(ItemStack itemstack, Block block, int metadata)
   {
      return getStrVsBlock(block, metadata);
   }

   /**
    * Called by CraftingManager to determine if an item is reparable.
    * @return True if reparable
    */
   public boolean isRepairable()
   {
      return canRepair && isDamageable();
   }

   /**
    * Call to disable repair recipes.
    * @return The current Item instance
    */
   public Item setNoRepair()
   {
      canRepair = false;
      return this;
   }

   /**
    * Called before a block is broken.  Return true to prevent default block harvesting.
    *
    * Note: In SMP, this is called on both client and server sides!
    *
    * @param itemstack The current ItemStack
    * @param X The X Position
    * @param Y The X Position
    * @param Z The X Position
    * @param player The Player that is wielding the item
    * @return True to prevent harvesting, false to continue as normal
    */
   public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
   {
      return false;
   }

   /**
    * Called each tick while using an item.
    * @param stack The Item being used
    * @param player The Player using the item
    * @param count The amount of time in tick the item has been used for continuously
    */
   public void onUsingItemTick(ItemStack stack, EntityPlayer player, int count)
   {
   }

   /**
    * Called when the player Left Clicks (attacks) an entity.
    * Processed before damage is done, if return value is true further processing is canceled
    * and the entity is not attacked.
    *
    * @param stack The Item being used
    * @param player The player that is attacking
    * @param entity The entity being attacked
    * @return True to cancel the rest of the interaction.
    */
   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
   {
      return false;
   }

   /**
    * Player, Render pass, and item usage sensitive version of getIconIndex.
    *
    * @param stack The item stack to get the icon for. (Usually this, and usingItem will be the same if usingItem is not null)
    * @param renderPass The pass to get the icon for, 0 is default.
    * @param player The player holding the item
    * @param usingItem The item the player is actively using. Can be null if not using anything.
    * @param useRemaining The ticks remaining for the active item.
    * @return The icon index
    */
   public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
   {
      return getIcon(stack, renderPass);
   }

   /**
    * Returns the number of render passes/layers this item has.
    * Usually equates to ItemRenderer.renderItem being called for this many passes.
    * Does not get called unless requiresMultipleRenderPasses() is true;
    *
    * @param metadata The item's metadata
    * @return The number of passes to run.
    */
   public int getRenderPasses(int metadata)
   {
      return requiresMultipleRenderPasses() ? 2 : 1;
   }

   /**
    * ItemStack sensitive version of getContainerItem.
    * Returns a full ItemStack instance of the result.
    *
    * @param itemStack The current ItemStack
    * @return The resulting ItemStack
    */
   public ItemStack getContainerItemStack(ItemStack itemStack)
   {
      if (!hasContainerItem())
      {
         return null;
      }
      return new ItemStack(getContainerItem());
   }

   /**
    * Retrieves the normal 'lifespan' of this item when it is dropped on the ground as a EntityItem.
    * This is in ticks, standard result is 6000, or 5 mins.
    *
    * @param itemStack The current ItemStack
    * @param world The world the entity is in
    * @return The normal lifespan in ticks.
    */
   public int getEntityLifespan(ItemStack itemStack, World world)
   {
      return 6000;
   }

   /**
    * Determines if this Item has a special entity for when they are in the world.
    * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
    * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
    *
    * @param stack The current item stack
    * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
    */
   public boolean hasCustomEntity(ItemStack stack)
   {
      return false;
   }

   /**
    * This function should return a new entity to replace the dropped item.
    * Returning null here will not kill the EntityItem and will leave it to function normally.
    * Called when the item it placed in a world.
    *
    * @param world The world object
    * @param location The EntityItem object, useful for getting the position of the entity
    * @param itemstack The current item stack
    * @return A new Entity object to spawn or null
    */
   public Entity createEntity(World world, Entity location, ItemStack itemstack)
   {
      return null;
   }

   /**
    * Called by the default implemetation of EntityItem's onUpdate method, allowing for cleaner
    * control over the update of the item without having to write a subclass.
    *
    * @param entityItem The entity Item
    * @return Return true to skip any further update code.
    */
   public boolean onEntityItemUpdate(EntityItem entityItem)
   {
      return false;
   }

   /**
    * Gets a list of tabs that items belonging to this class can display on,
    * combined properly with getSubItems allows for a single item to span
    * many sub-items across many tabs.
    *
    * @return A list of all tabs that this item could possibly be one.
    */
   public CreativeTabs[] getCreativeTabs()
   {
      return new CreativeTabs[]{ getCreativeTab() };
   }

   /**
    * Determines the base experience for a player when they remove this item from a furnace slot.
    * This number must be between 0 and 1 for it to be valid.
    * This number will be multiplied by the stack size to get the total experience.
    *
    * @param item The item stack the player is picking up.
    * @return The amount to award for each item.
    */
   public float getSmeltingExperience(ItemStack item)
   {
      return -1; //-1 will default to the old lookups.
   }

   /**
    * Return the correct icon for rendering based on the supplied ItemStack and render pass.
    *
    * Defers to {@link #getIconFromSubtypeForRenderPass(int, int)}
    * @param stack to render for
    * @param pass the multi-render pass
    * @return the icon
    */
   public Icon getIcon(ItemStack stack, int pass)
   {
      return getIconFromSubtypeForRenderPass(stack.getItemDamage(), pass);
   }

   /**
    * Generates the base Random item for a specific instance of the chest gen,
    * Enchanted books use this to pick a random enchantment.
    *
    * @param chest The chest category to generate for
    * @param rnd World RNG
    * @param original Original result registered with the chest gen hooks.
    * @return New values to use as the random item, typically this will be original
    */
   public WeightedRandomChestContent getChestGenBase(ChestGenHooks chest, Random rnd, WeightedRandomChestContent original)
   {
      if (this instanceof ItemEnchantedBook)
      {
         return ((ItemEnchantedBook)this).func_92112_a(rnd,
                 original.min_quantity,
                 original.max_quantity, original.itemWeight);
      }
      return original;
   }

   /**
    *
    * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
    *
    * @param par2World
    * @param par4
    * @param par5
    * @param par6
    * @return
    */
   public boolean shouldPassSneakingClickToBlock(World par2World, int par4, int par5, int par6)
   {
      return false;
   }


   /**
    * Called to tick armor in the armor slot. Override to do something
    *
    * @param world
    * @param player
    * @param itemStack
    */
   public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack)
   {

   }

   /**
    * Determines if the specific ItemStack can be placed in the specified armor slot.
    *
    * @param stack The ItemStack
    * @param armorType Armor slot ID: 0: Helmet, 1: Chest, 2: Legs, 3: Boots
    * @param entity The entity trying to equip the armor
    * @return True if the given ItemStack can be inserted in the slot
    */
   public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
   {
      if (this instanceof ItemArmor)
      {
         return ((ItemArmor)this).armorType == armorType;
      }

      if (armorType == 0)
      {
         return itemID == Block.pumpkin.blockID || itemID == Item.skull.itemID;
      }

      return false;
   }

   /**
    * ItemStack sensitive version of isPotionIngredient
    *
    * @param stack The item stack
    * @return True if this stack can be used as a potion ingredient
    */
   public boolean isPotionIngredient(ItemStack stack)
   {
      return isPotionIngredient();
   }

   /**
    * ItemStack sensitive version of getPotionEffect
    *
    * @param stack The item stack
    * @return A string containing the bit manipulation to apply the the potion.
    */
   public String getPotionEffect(ItemStack stack)
   {
      return getPotionEffect();
   }

   /**
    * Allow or forbid the specific book/item combination as an anvil enchant
    *
    * @param itemstack1 The item
    * @param itemstack2 The book
    * @return if the enchantment is allowed
    */
   public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2)
   {
      return true;
   }

   /**
    * An itemstack sensitive version of getDamageVsEntity - allows items to handle damage based on
    * itemstack data, like tags. Falls back to getDamageVsEntity.
    *
    * @param par1Entity The entity being attacked (or the attacking mob, if it's a mob - vanilla bug?)
    * @param itemStack The itemstack
    * @return the damage
    */
   @Deprecated //Need to find a new place to hook this
   public float getDamageVsEntity(Entity par1Entity, ItemStack itemStack)
   {
      return 0.0F; //getDamageVsEntity(par1Entity);
   }

   /**
    * Called by RenderBiped and RenderPlayer to determine the armor texture that
    * should be use for the currently equiped item.
    * This will only be called on instances of ItemArmor.
    *
    * Returning null from this function will use the default value.
    *
    * @param stack ItemStack for the equpt armor
    * @param entity The entity wearing the armor
    * @param slot The slot the armor is in
    * @param layer The render layer, either 1 or 2, 2 is only used for CLOTH armor by default
    * @return Path of texture to bind, or null to use default
    */
   @Deprecated //Replaced with more useful version below
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer)
   {
      return null;
   }

   /**
    * Called by RenderBiped and RenderPlayer to determine the armor texture that
    * should be use for the currently equiped item.
    * This will only be called on instances of ItemArmor.
    *
    * Returning null from this function will use the default value.
    *
    * @param stack ItemStack for the equpt armor
    * @param entity The entity wearing the armor
    * @param slot The slot the armor is in
    * @param type The subtype, can be null or "overlay"
    * @return Path of texture to bind, or null to use default
    */
   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
   {
      return getArmorTexture(stack, entity, slot, (slot == 2 ? 2 : 1));
   }


   /**
    * Returns the font renderer used to render tooltips and overlays for this item.
    * Returning null will use the standard font renderer.
    *
    * @param stack The current item stack
    * @return A instance of FontRenderer or null to use default
    */

   public FontRenderer getFontRenderer(ItemStack stack)
   {
      return null;
   }

   /**
    * Override this method to have an item handle its own armor rendering.
    *
    * @param  entityLiving  The entity wearing the armor
    * @param  itemStack  The itemStack to render the model of
    * @param  armorSlot  0=head, 1=torso, 2=legs, 3=feet
    *
    * @return  A ModelBiped to render instead of the default
    */

   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
   {
      return null;
   }

   /**
    * Called when a entity tries to play the 'swing' animation.
    *
    * @param entityLiving The entity swinging the item.
    * @param stack The Item stack
    * @return True to cancel any further processing by EntityLiving
    */
   public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
   {
      return false;
   }

   /**
    * Called when the client starts rendering the HUD, for whatever item the player currently has as a helmet.
    * This is where pumpkins would render there overlay.
    *
    * @param stack The ItemStack that is equipped
    * @param player Reference to the current client entity
    * @param resolution Resolution information about the current viewport and configured GUI Scale
    * @param partialTicks Partial ticks for the renderer, useful for interpolation
    * @param hasScreen If the player has a screen up, which will be rendered after this.
    * @param mouseX Mouse's X position on screen
    * @param mouseY Mouse's Y position on screen
    */

   public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks, boolean hasScreen, int mouseX, int mouseY){}

   /**
    * Return the itemDamage represented by this ItemStack. Defaults to the itemDamage field on ItemStack, but can be overridden here for other sources such as NBT.
    *
    * @param stack The itemstack that is damaged
    * @return the damage value
    */
   public int getDamage(ItemStack stack)
   {
      return 0;
   }

   /**
    * Return the itemDamage display value represented by this itemstack.
    * @param stack the stack
    * @return the damage value
    */
   public int getDisplayDamage(ItemStack stack)
   {
      return stack.getItemDamage();
   }

   /**
    * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in this item,
    * but can be overridden here for other sources such as NBT.
    *
    * @param stack The itemstack that is damaged
    * @return the damage value
    */
//   public int getMaxDamage(ItemStack stack)
//   {
//      return getMaxDamage();
//   }

   /**
    * Return if this itemstack is damaged. Note only called if {@link #isDamageable()} is true.
    * @param stack the stack
    * @return if the stack is damaged
    */
   public boolean isDamaged(ItemStack stack)
   {
      return stack.getItemDamage() > 0;
   }

   /**
    * Set the damage for this itemstack. Note, this method is responsible for zero checking.
    * @param stack the stack
    * @param damage the new damage value
    */
   public void setDamage(ItemStack stack, int damage)
   {
      stack.setItemDamage(damage);

      if (stack.getItemDamage() < 0)
      {
         stack.setItemDamage(0);
      }
   }

   /**
    * ItemStack sensitive version of { #canHarvestBlock(Block)}
    * @param par1Block The block trying to harvest
    * @param itemStack The itemstack used to harvest the block
    * @return true if can harvest the block
    */
   public boolean canHarvestBlock(Block par1Block, ItemStack itemStack)
   {
//      return canHarvestBlock(par1Block);
      return false;
   }


   /**
    * Render Pass sensitive version of hasEffect()
    */
   public boolean hasEffect(ItemStack par1ItemStack, int pass)
   {
      return hasEffect(par1ItemStack) && (pass == 0 || itemID != Item.potion.itemID);
   }

   /**
    * Gets the maximum number of items that this stack should be able to hold.
    * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
    *
    * @param stack The ItemStack
    * @return THe maximum number this item can be stacked to
    */
   public int getItemStackLimit(ItemStack stack)
   {
      return this.getItemStackLimit(stack.stackSize, stack.getItemSubtype());
   }

}
