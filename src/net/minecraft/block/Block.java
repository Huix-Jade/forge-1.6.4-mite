package net.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemDeadBush;
import net.minecraft.item.ItemGravel;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemMantleOrCore;
import net.minecraft.item.ItemMultiTextureTile;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemRunestone;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemSnowBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.MITEConstant;
import net.minecraft.network.SignalData;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraftforge.common.*;
import net.minecraftforge.event.ForgeEventFactory;

import static net.minecraftforge.common.ForgeDirection.UP;

public class Block {
   private CreativeTabs displayOnCreativeTab;
   protected String textureName;
   public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
   public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
   public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
   public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
   public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
   public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
   public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
   public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
   public static final StepSound soundSandFootstep = new StepSound("sand", 1.0F, 1.0F);
   public static final StepSound soundSnowFootstep = new StepSound("snow", 1.0F, 1.0F);
   public static final StepSound soundLadderFootstep = new StepSoundSand("ladder", 1.0F, 1.0F);
   public static final StepSound soundAnvilFootstep = new StepSoundAnvil("stone", 0.3F, 1.0F);
   public static final Block[] blocksList = new Block[4096];
   public static final boolean[] opaqueCubeLookup = new boolean[4096];
   public static final int[] lightOpacity = new int[4096];
   public static final boolean[] canHaveLightValue = new boolean[4096];
   public static final int[] lightValue = new int[4096];
   public static boolean[] useNeighborBrightness = new boolean[4096];
   public static final boolean[] is_normal_cube_lookup = new boolean[256];
   public static final Block stone;
   public static final BlockGrass grass;
   public static final Block dirt;
   public static final Block cobblestone;
   public static final BlockWood planks;
   public static final Block sapling;
   public static final Block bedrock;
   public static final BlockFluid waterMoving;
   public static final Block waterStill;
   public static final BlockFluid lavaMoving;
   public static final Block lavaStill;
   public static final BlockSand sand;
   public static final BlockGravel gravel;
   public static final Block oreGold;
   public static final Block oreIron;
   public static final Block oreCoal;
   public static final BlockLog wood;
   public static final BlockLeaves leaves;
   public static final Block sponge;
   public static final Block glass;
   public static final Block oreLapis;
   public static final Block blockLapis;
   public static final BlockDispenser dispenser;
   public static final Block sandStone;
   public static final Block music;
   public static final BlockBed bed;
   public static final Block railPowered;
   public static final Block railDetector;
   public static final BlockPistonBase pistonStickyBase;
   public static final Block web;
   public static final BlockTallGrass tallGrass;
   public static final BlockDeadBush deadBush;
   public static final BlockPistonBase pistonBase;
   public static final BlockPistonExtension pistonExtension;
   public static final Block cloth;
   public static final BlockPistonMoving pistonMoving;
   public static final BlockFlower plantYellow;
   public static final BlockFlowerMulti plantRed;
   public static final BlockMushroom mushroomBrown;
   public static final BlockMushroom mushroomRed;
   public static final Block blockGold;
   public static final Block blockIron;
   public static final BlockSlabGroup1 stoneSingleSlab;
   public static final BlockDoubleSlab stoneDoubleSlab;
   public static final Block brick;
   public static final BlockTNT tnt;
   public static final Block bookShelf;
   public static final Block cobblestoneMossy;
   public static final Block obsidian;
   public static final Block torchWood;
   public static final BlockFire fire;
   public static final Block mobSpawner;
   public static final Block stairsWoodOak;
   public static final BlockChest chest;
   public static final BlockRedstoneWire redstoneWire;
   public static final Block oreDiamond;
   public static final Block blockDiamond;
   public static final Block workbench;
   public static final Block crops;
   public static final Block tilledField;
   public static final Block furnaceIdle;
   public static final Block furnaceBurning;
   public static final Block signPost;
   public static final Block doorWood;
   public static final Block ladder;
   public static final Block rail;
   public static final Block stairsCobblestone;
   public static final Block signWall;
   public static final Block lever;
   public static final Block pressurePlateStone;
   public static final Block doorIron;
   public static final Block pressurePlatePlanks;
   public static final Block oreRedstone;
   public static final Block oreRedstoneGlowing;
   public static final Block torchRedstoneIdle;
   public static final Block torchRedstoneActive;
   public static final Block stoneButton;
   public static final Block snow;
   public static final Block ice;
   public static final Block blockSnow;
   public static final Block cactus;
   public static final Block blockClay;
   public static final Block reed;
   public static final BlockJukeBox jukebox;
   public static final Block fence;
   public static final Block pumpkin;
   public static final Block netherrack;
   public static final Block slowSand;
   public static final Block glowStone;
   public static final BlockPortal portal;
   public static final Block pumpkinLantern;
   public static final Block cake;
   public static final BlockRedstoneRepeater redstoneRepeaterIdle;
   public static final BlockRedstoneRepeater redstoneRepeaterActive;
   public static final Block trapdoor;
   public static final BlockSilverfish silverfish;
   public static final Block stoneBrick;
   public static final Block mushroomCapBrown;
   public static final Block mushroomCapRed;
   public static final Block fenceIron;
   public static final Block thinGlass;
   public static final Block melon;
   public static final Block pumpkinStem;
   public static final Block melonStem;
   public static final BlockVine vine;
   public static final Block fenceGate;
   public static final Block stairsBrick;
   public static final Block stairsStoneBrick;
   public static final BlockMycelium mycelium;
   public static final Block waterlily;
   public static final Block netherBrick;
   public static final Block netherFence;
   public static final Block stairsNetherBrick;
   public static final Block netherStalk;
   public static final Block enchantmentTable;
   public static final Block brewingStand;
   public static final BlockCauldron cauldron;
   public static final Block endPortal;
   public static final Block endPortalFrame;
   public static final Block whiteStone;
   public static final Block dragonEgg;
   public static final Block redstoneLampIdle;
   public static final Block redstoneLampActive;
   public static final BlockSlabGroup2 woodSingleSlab;
   public static final BlockDoubleSlab woodDoubleSlab;
   public static final Block cocoaPlant;
   public static final Block stairsSandStone;
   public static final Block oreEmerald;
   public static final Block enderChest;
   public static final BlockTripWireSource tripWireSource;
   public static final Block tripWire;
   public static final Block blockEmerald;
   public static final Block stairsWoodSpruce;
   public static final Block stairsWoodBirch;
   public static final Block stairsWoodJungle;
   public static final Block commandBlock;
   public static final BlockBeacon beacon;
   public static final Block cobblestoneWall;
   public static final Block flowerPot;
   public static final Block carrot;
   public static final Block potato;
   public static final Block woodenButton;
   public static final Block skull;
   public static final BlockAnvil anvil;
   public static final Block chestTrapped;
   public static final Block pressurePlateGold;
   public static final Block pressurePlateIron;
   public static final BlockComparator redstoneComparatorIdle;
   public static final BlockComparator redstoneComparatorActive;
   public static final BlockDaylightDetector daylightSensor;
   public static final Block blockRedstone;
   public static final Block oreNetherQuartz;
   public static final BlockHopper hopperBlock;
   public static final Block blockNetherQuartz;
   public static final Block stairsNetherQuartz;
   public static final Block railActivator;
   public static final Block dropper;
   public static final Block hay;
   public static final Block carpet;
   public static final Block coalBlock;
   public static final Block stainedClay;
   public static final Block hardenedClay;
   public static final Block fenceAncientMetal;
   public static final Block oreCopper;
   public static final Block oreSilver;
   public static final Block oreMithril;
   public static final Block oreAdamantium;
   public static final Block blockCopper;
   public static final Block blockSilver;
   public static final Block blockMithril;
   public static final Block blockAdamantium;
   public static final Block doorCopper;
   public static final Block doorSilver;
   public static final Block doorGold;
   public static final Block doorMithril;
   public static final Block doorAdamantium;
   public static final Block fenceCopper;
   public static final Block fenceSilver;
   public static final Block fenceGold;
   public static final Block fenceMithril;
   public static final Block fenceAdamantium;
   public static final Block furnaceClayIdle;
   public static final Block furnaceClayBurning;
   public static final Block furnaceSandstoneIdle;
   public static final Block furnaceSandstoneBurning;
   public static final Block furnaceObsidianIdle;
   public static final Block furnaceObsidianBurning;
   public static final Block furnaceNetherrackIdle;
   public static final Block furnaceNetherrackBurning;
   public static final BlockSlabGroup3 obsidianSingleSlab;
   public static final BlockDoubleSlab obsidianDoubleSlab;
   public static final Block stairsObsidian;
   public static final BlockAnvil anvilCopper;
   public static final BlockAnvil anvilSilver;
   public static final BlockAnvil anvilGold;
   public static final BlockAnvil anvilMithril;
   public static final BlockAnvil anvilAdamantium;
   public static final Block onions;
   public static final Block cropsDead;
   public static final Block carrotDead;
   public static final Block potatoDead;
   public static final Block onionsDead;
   public static final BlockStrongbox chestCopper;
   public static final BlockStrongbox chestSilver;
   public static final BlockStrongbox chestGold;
   public static final BlockStrongbox chestIron;
   public static final BlockStrongbox chestMithril;
   public static final BlockStrongbox chestAdamantium;
   public static final Block enchantmentTableEmerald;
   public static final BlockSpark spark;
   public static final BlockRunestone runestoneMithril;
   public static final Block flowerPotMulti;
   public static final BlockBush bush;
   public static final Block furnaceHardenedClayIdle;
   public static final Block furnaceHardenedClayBurning;
   public static final Block blockAncientMetal;
   public static final Block doorAncientMetal;
   public static final BlockAnvil anvilAncientMetal;
   public static final BlockStrongbox chestAncientMetal;
   public static final BlockRunestone runestoneAdamantium;
   public static final BlockMantleOrCore mantleOrCore;
   public final int blockID;
   private float blockHardness;
   protected boolean blockConstructorCalled = true;
   protected boolean enableStats = true;
   protected boolean needsRandomTick;
   public final double[] minX = new double[2];
   public final double[] minY = new double[2];
   public final double[] minZ = new double[2];
   public final double[] maxX = new double[2];
   public final double[] maxY = new double[2];
   public final double[] maxZ = new double[2];
   public StepSound stepSound;
   public float blockParticleGravity;
   public final Material blockMaterial;
   public float slipperiness;
   private String unlocalizedName;
   protected Icon blockIcon;
   private int min_harvest_level;
   private final boolean has_item_subtypes;
   private float cushioning;
   private int max_stack_size = 4;
   public boolean has_grass_top_icon;
   private final boolean[] is_solid = new boolean[16];
   public final boolean is_always_solid;
   public final boolean is_never_solid;
   private final boolean[] is_standard_form_cube = new boolean[16];
   public final boolean is_always_standard_form_cube;
   public final boolean is_never_standard_form_cube;
   private final boolean[] is_solid_and_standard_form_cube = new boolean[16];
   public final boolean is_always_solid_standard_form_cube;
   public final boolean is_never_solid_standard_form_cube;
   public final boolean is_always_opaque_standard_form_cube;
   public final boolean is_never_opaque_standard_form_cube;
   public final boolean is_always_solid_opaque_standard_form_cube;
   public final boolean is_never_solid_opaque_standard_form_cube;
   public final boolean never_hides_adjacent_faces;
   public final boolean is_always_legal;
   public final boolean is_always_immutable;
   private final boolean[] blocks_precipitation = new boolean[16];
   public final boolean always_blocks_precipitation;
   public final boolean never_blocks_precipitation;
   private final boolean[] blocks_fluids = new boolean[16];
   public final boolean always_blocks_fluids;
   public final boolean never_blocks_fluids;
   public final boolean connects_with_fence;
   public boolean is_being_placed;
   public final boolean is_normal_cube;
   public final boolean uses_new_sand_physics;
   private final int num_item_subtypes;
   private final int[] item_subtypes;
   private final boolean is_tree_leaves;
   private final boolean[] use_neighbor_brightness = new boolean[96];
   private static AxisAlignedBB standard_form_bounding_box;

   protected static int[] blockFireSpreadSpeed = new int[4096];
   protected static int[] blockFlammability = new int[4096];

   public Block setMinHarvestLevel(int min_harvest_level) {
      this.min_harvest_level = min_harvest_level;
      return this;
   }

   public Block modifyMinHarvestLevel(int change) {
      this.min_harvest_level += change;
      if (this.min_harvest_level < 0) {
         Debug.setErrorMessage("modifyMinHarvestLevel: min_harvest_level was set to less than 0");
         Debug.printStackTrace();
         this.min_harvest_level = 0;
      }

      return this;
   }

   public Block(int par1, Material par2Material) {
      this(par1, par2Material, new BlockConstants());
   }

   protected Block(int par1, Material par2Material, BlockConstants constants) {
      this.stepSound = soundPowderFootstep;
      this.blockParticleGravity = 1.0F;
      this.slipperiness = 0.6F;
      if (blocksList[par1] != null) {
         throw new IllegalArgumentException("Slot " + par1 + " is already occupied by " + blocksList[par1] + " when adding " + this);
      } else {
         this.blockMaterial = par2Material;
         blocksList[par1] = this;
         this.blockID = par1;
         constants.validate(this);
         boolean is_always_solid = true;
         boolean is_never_solid = true;
         boolean is_always_standard_form = true;
         boolean is_never_standard_form = true;
         boolean always_blocks_precipitation = true;
         boolean never_blocks_precipitation = true;
         boolean always_blocks_fluids = true;
         boolean never_blocks_fluids = true;

         int metadata;
         for(metadata = 0; metadata < 16; ++metadata) {
            if (this.is_solid[metadata] = this.isSolid(this.is_solid, metadata)) {
               is_never_solid = false;
            } else {
               is_always_solid = false;
            }

            if (this.is_standard_form_cube[metadata] = this.isStandardFormCube(this.is_standard_form_cube, metadata)) {
               is_never_standard_form = false;
            } else {
               is_always_standard_form = false;
            }

            this.is_solid_and_standard_form_cube[metadata] = this.is_solid[metadata] && this.is_standard_form_cube[metadata];
         }

         this.is_always_solid = is_always_solid;
         this.is_never_solid = is_never_solid;
         this.is_always_standard_form_cube = is_always_standard_form;
         this.is_never_standard_form_cube = is_never_standard_form;
         this.is_always_solid_standard_form_cube = is_always_solid && is_always_standard_form;
         this.is_never_solid_standard_form_cube = is_never_solid || is_never_standard_form;

         for(metadata = 0; metadata < 16; ++metadata) {
            if (this.blocks_precipitation[metadata] = this.blocksPrecipitation(this.blocks_precipitation, metadata)) {
               never_blocks_precipitation = false;
            } else {
               always_blocks_precipitation = false;
            }

            if (this.blocks_fluids[metadata] = this.blocksFluids(this.blocks_fluids, metadata)) {
               never_blocks_fluids = false;
            } else {
               always_blocks_fluids = false;
            }
         }

         this.always_blocks_precipitation = always_blocks_precipitation;
         this.never_blocks_precipitation = never_blocks_precipitation;
         this.always_blocks_fluids = always_blocks_fluids;
         this.never_blocks_fluids = never_blocks_fluids;
         this.is_tree_leaves = this instanceof BlockLeavesBase;
         if (this instanceof BlockLeavesBase) {
            this.is_always_opaque_standard_form_cube = false;
            this.is_never_opaque_standard_form_cube = true;
         } else if (this instanceof BlockPistonBase) {
            this.is_always_opaque_standard_form_cube = false;
            this.is_never_opaque_standard_form_cube = true;
         } else {
            this.is_always_opaque_standard_form_cube = this.is_always_standard_form_cube && !constants.neverHidesAdjacentFaces();
            this.is_never_opaque_standard_form_cube = this.is_never_standard_form_cube || constants.neverHidesAdjacentFaces();
         }

         this.is_always_solid_opaque_standard_form_cube = is_always_solid && this.is_always_opaque_standard_form_cube;
         this.is_never_solid_opaque_standard_form_cube = is_never_solid || this.is_never_opaque_standard_form_cube;
         if (constants.connectsWithFence() == null) {
            this.connects_with_fence = this.is_always_solid_opaque_standard_form_cube && !this.hasTileEntity();
         } else {
            this.connects_with_fence = constants.connectsWithFence();
         }

         this.never_hides_adjacent_faces = constants.neverHidesAdjacentFaces();
         this.is_always_legal = constants.isAlwaysLegal();
         this.is_always_immutable = constants.isAlwaysImmutable();
         if (!this.is_always_standard_form_cube && this.getClass() == Block.class) {
            Minecraft.setErrorMessage("Block[" + par1 + "] must be standard form cube if it doesn't override Block");
         }

         this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         opaqueCubeLookup[par1] = this.isAlwaysOpaqueStandardFormCube();
         this.setLightOpacity(this.isAlwaysOpaqueStandardFormCube() ? 255 : 0);
         this.item_subtypes = this.getItemSubtypes();
         this.num_item_subtypes = this.item_subtypes.length;
         this.has_item_subtypes = this.num_item_subtypes > 1;
         this.min_harvest_level = this.blockMaterial.min_harvest_level;
         this.is_normal_cube = this.is_always_solid_opaque_standard_form_cube && !this.canProvidePower();
         is_normal_cube_lookup[this.blockID] = this.is_normal_cube;
         if (constants.usesNewSandPhysics() && !(this instanceof BlockFalling)) {
            Minecraft.setErrorMessage("Block cannot use new sand physics if it is not an instanceof BlockFalling");
         }

         if (constants.usesNewSandPhysics() && !Minecraft.allow_new_sand_physics) {
            Minecraft.setErrorMessage("BlockConstants uses new sand physics but Minecraft.allow_new_sand_physics is set to false");
         }

         this.uses_new_sand_physics = Minecraft.allow_new_sand_physics && constants.usesNewSandPhysics();
      }
   }

   protected void initializeBlock() {
   }

   private final void validate() {
      if (this.is_always_solid && this.is_never_solid) {
         Minecraft.setErrorMessage("validate: is_always_solid and is_never_solid");
      }

      if (this.is_always_standard_form_cube && this.is_never_standard_form_cube) {
         Minecraft.setErrorMessage("validate: is_always_standard_form_cube and is_never_standard_form_cube");
      }

      if (this.is_always_solid_standard_form_cube && !this.is_always_solid) {
         Minecraft.setErrorMessage("validate: is_always_solid_standard_form_cube and !is_always_solid " + this);
      }

      if (this.is_always_solid_standard_form_cube && !this.is_always_standard_form_cube) {
         Minecraft.setErrorMessage("validate: is_always_solid_standard_form_cube and !is_always_standard_form_cube " + this);
      }

      if (this.is_always_solid_opaque_standard_form_cube && !this.is_always_solid) {
         Minecraft.setErrorMessage("validate: is_always_solid_opaque_standard_form_cube and !is_always_solid");
      }

      if (this.is_always_solid_opaque_standard_form_cube && !this.is_always_standard_form_cube) {
         Minecraft.setErrorMessage("validate: is_always_solid_opaque_standard_form_cube and !is_always_standard_form_cube");
      }

      if (this.is_always_solid_opaque_standard_form_cube && !this.is_always_solid_standard_form_cube) {
         Minecraft.setErrorMessage("validate: is_always_solid_opaque_standard_form_cube and !is_always_solid_standard_form_cube");
      }

      if (this.num_item_subtypes != this.item_subtypes.length) {
         Debug.setErrorMessage("validate: num_item_subtypes=" + this.num_item_subtypes + " vs item_subtypes.length=" + this.item_subtypes.length + " for " + this + " (id=" + this.blockID + ")");
      }

      if (this.num_item_subtypes != this.getItemStacks().size()) {
         Debug.setErrorMessage("validate: num_item_subtypes=" + this.num_item_subtypes + " vs " + this.getItemStacks().size() + " for " + this + " (id=" + this.blockID + ")");
      }

      if (this.num_item_subtypes < 1) {
         Debug.setErrorMessage("validate: num_item_subtypes==" + this.num_item_subtypes + " for " + this + "?");
      } else {
         List list = this.getItemStacks();

         for(int i = 0; i < this.item_subtypes.length; ++i) {
            ItemStack item_stack = (ItemStack)list.get(i);
            if (item_stack.getItemSubtype() != this.item_subtypes[i]) {
               Debug.setErrorMessage("validate: subtype value mismatch, " + item_stack.getItemSubtype() + " vs " + this.item_subtypes[i]);
            }
         }
      }

      int metadata;
      if (this.has_item_subtypes) {
         for(metadata = 0; metadata < this.item_subtypes.length; ++metadata) {
            if (!this.isValidMetadata(this.item_subtypes[metadata])) {
               Debug.setErrorMessage("validate: " + this + " has a subtype metadata of " + this.item_subtypes[metadata] + " but isValidMetadata() returns false for it");
            }
         }
      }

      if (this.canBeCarried()) {
         if (this.getCreativeTabToDisplayOn() == null) {
            Minecraft.setErrorMessage("No creative tab for [" + this.blockID + "] " + this);
         }
      } else if (this != bedrock && this != mobSpawner && this != dragonEgg && this != endPortalFrame && this != mantleOrCore && this.getCreativeTabToDisplayOn() != null) {
         Minecraft.setErrorMessage("Creative tab for [" + this.blockID + "] " + this);
      }

      for(metadata = 0; metadata < 16; ++metadata) {
         if (this.isValidMetadata(metadata)) {
            ItemStack item_stack = this.createStackedBlock(metadata);
            if (item_stack == null) {
               if (this.canBeCarried()) {
                  Minecraft.setErrorMessage("validate: " + this + " can be carried but createStackedBlock() returns null");
                  break;
               }
            } else if (item_stack.isBlock() && !item_stack.getItemAsBlock().getBlock().canBeCarried()) {
               Minecraft.setErrorMessage("validate: createStackedBlock() returns a block that cannot be carried for " + this);
               break;
            }

            if (this.canSilkHarvest(metadata) && !this.canBeCarried()) {
               Minecraft.setErrorMessage("validate: " + this + " canSilkHarvest but cannot be carried");
            }
         }
      }

      boolean can_be_solid = false;
      boolean can_be_not_solid = false;

      for(int i = 0; i < 16; ++i) {
         if (this.isValidMetadata(i)) {
            if (this.isSolid(i)) {
               can_be_solid = true;
            } else {
               can_be_not_solid = true;
            }
         }
      }

      if (!can_be_solid && !can_be_not_solid) {
         Minecraft.setErrorMessage("validate: " + this + " can neither be solid or not solid?");
      } else if (can_be_solid && can_be_not_solid) {
         if (this.is_always_solid) {
            Minecraft.setErrorMessage("validate: " + this + " can be solid or not solid but is_always_solid==true");
         } else if (this.is_never_solid) {
            Minecraft.setErrorMessage("validate: " + this + " can be solid or not solid but is_never_solid==true");
         }
      } else if (can_be_solid) {
         if (this.is_never_solid) {
            Minecraft.setErrorMessage("validate: " + this + " can only be solid but is_never_solid==true");
         } else if (!this.is_always_solid) {
            Minecraft.setErrorMessage("validate: " + this + " can only be solid but is_always_solid==false");
         }
      } else if (this.is_always_solid) {
         Minecraft.setErrorMessage("validate: " + this + " can only be not solid but is_always_solid==true");
      } else if (!this.is_never_solid) {
         Minecraft.setErrorMessage("validate: " + this + " can only be not solid but is_never_solid==false");
      }

      if (this.getRenderType() == 1) {
         if (!this.neverHidesAdjacentFaces()) {
            Minecraft.setErrorMessage("validate: " + this + " has render type " + this.getRenderType() + " but never_hides_adjacent_faces==false");
         }

         if (!this.is_never_standard_form_cube) {
            Minecraft.setErrorMessage("validate: " + this + " has render type " + this.getRenderType() + " but is_never_standard_form_cube==false");
         }
      }

      if (!this.is_always_standard_form_cube && this.renderAsNormalBlock()) {
         Minecraft.setErrorMessage("validate: " + this + " renders as normal block but is_always_standard_form_cube==false");
      }

      if (is_normal_cube_lookup[this.blockID] != this.is_normal_cube) {
         Minecraft.setErrorMessage("validate: " + this + " normal cube lookup discrepency");
      }

      if (!this.is_always_opaque_standard_form_cube && !this.is_never_opaque_standard_form_cube) {
         Minecraft.setErrorMessage("validate: " + this + " is neither always opaque standard form cube or never opaque standard form cube");
      }

      if (this.renderAsNormalBlock() != this.isAlwaysStandardFormCube()) {
         Debug.println("validate: " + this + ", renderAsNormalBlock=" + this.renderAsNormalBlock() + " vs isAlwaysStandardFormCube=" + this.isAlwaysStandardFormCube());
      }

      if (lightOpacity[this.blockID] == 0 && !canHaveLightValue[this.blockID]) {
         Debug.println("validate: " + this + " has light opacity 0 but canBlockGrass==false");
      }

      if (lightOpacity[this.blockID] >= 15 && canHaveLightValue[this.blockID]) {
         Debug.println("validate: " + this + " has light opacity 255 but canBlockGrass==true");
      }

      if (lightOpacity[this.blockID] >= 15 && this.isAlwaysStandardFormCube() && !this.isAlwaysOpaqueStandardFormCube()) {
         Debug.println("validate: " + this + " has light opacity >= 15 and is always standard form but is_always_opaque_standard_form_cube==false");
      }

      if (lightOpacity[this.blockID] >= 15 && !this.isAlwaysStandardFormCube() && !useNeighborBrightness[this.blockID]) {
         Debug.println("validate: " + this + " has light opacity 255 and is not always standard form but useNeighborBrightness==false");
      }

      if (lightOpacity[this.blockID] >= 15 && this.isAlwaysStandardFormCube() && useNeighborBrightness[this.blockID]) {
         Debug.println("validate: " + this + " has light opacity 255 and is always standard form but useNeighborBrightness==true");
      }

      if (canHaveLightValue[this.blockID] && useNeighborBrightness[this.blockID]) {
         Debug.println("validate: " + this + " canHaveLightValue and useNeighborBrightness are mutually exclusive");
      }

      if (useNeighborBrightness[this.blockID] && this.neverHidesAdjacentFaces()) {
         Debug.println("validate: " + this + " uses neighbor brightness but neverHidesAdjacentFaces");
      }

      boolean uses_neighbor_brightness = false;
      boolean always_uses_neighbor_brightness = true;

      for(int i = 0; i < 96; ++i) {
         if (this.use_neighbor_brightness[i]) {
            uses_neighbor_brightness = true;
         } else {
            always_uses_neighbor_brightness = false;
         }
      }

      if (useNeighborBrightness[this.blockID] != uses_neighbor_brightness) {
         Debug.println("validate: " + this + " useNeighborBrightness mismatch");
      }

      if (always_uses_neighbor_brightness) {
         Debug.println("validate: " + this + " always uses neighbor brightness");
      }

   }

   public boolean isTreeLeaves() {
      return this.is_tree_leaves;
   }

   protected Block setStepSound(StepSound par1StepSound) {
      this.stepSound = par1StepSound;
      return this;
   }

   protected Block setLightOpacity(int par1) {
      if (par1 == 0) {
         canHaveLightValue[this.blockID] = true;
      } else if (par1 >= 15) {
         canHaveLightValue[this.blockID] = false;
      }

      lightOpacity[this.blockID] = par1;
      return this;
   }

   protected Block setLightValue(float par1) {
      lightValue[this.blockID] = (int)(15.0F * par1);
      return this;
   }

   protected Block setResistance(float par1) {
      return this;
   }

   public static final boolean isNormalCube(int block_id) {
      return is_normal_cube_lookup[block_id];
   }

   public static final boolean isNormalCube(Block block) {
      return block != null && block.is_normal_cube;
   }

   public boolean renderAsNormalBlock() {
      return this.isAlwaysStandardFormCube();
   }

   public final boolean isWoodenPortal() {
      return this == doorWood || this == trapdoor || this == fenceGate;
   }

   public boolean canBePathedInto(World world, int x, int y, int z, Entity entity, boolean allow_closed_wooden_portals) {
      if (this.isNeverSolid()) {
         return true;
      } else {
         if (this.isPortal()) {
            if (this.isOpenPortal(world, x, y, z)) {
               return true;
            }

            if (allow_closed_wooden_portals && this.isWoodenPortal()) {
               return true;
            }
         }

         if (this.isAlwaysSolid()) {
            return false;
         } else {
            return !this.isSolid(world, x, y, z);
         }
      }
   }

   public int getRenderType() {
      return 0;
   }

   protected Block setHardness(float par1) {
      this.blockHardness = par1;
      return this;
   }

   protected Block setHardnessRelativeToWood(float wood_block_hardness) {
      Material material;
      if (this instanceof BlockAnvil) {
         material = ((BlockAnvil)this).metal_type;
      } else {
         material = this.blockMaterial;
      }

      this.blockHardness = wood_block_hardness * material.durability;
      return this;
   }

   protected Block setBlockUnbreakable() {
      this.setHardness(-1.0F);
      this.setMinHarvestLevel(100);
      return this;
   }

   public float getBlockHardness(int metadata) {
      return this.blockHardness;
   }

   protected Block setTickRandomly(boolean par1) {
      this.needsRandomTick = par1;
      return this;
   }

   public final boolean getTickRandomly() {
      return this.needsRandomTick;
   }

   public boolean hasTileEntity() {
      return hasTileEntity(0);
   }

   protected final void setBlockBoundsForCurrentThread(double par1, double par2, double par3, double par4, double par5, double par6) {
      int index = Minecraft.getThreadIndex();
      this.minX[index] = par1;
      this.minY[index] = par2;
      this.minZ[index] = par3;
      this.maxX[index] = par4;
      this.maxY[index] = par5;
      this.maxZ[index] = par6;
   }

   protected final void setBlockBoundsForAllThreads(double par1, double par2, double par3, double par4, double par5, double par6) {
      for(int i = 0; i < this.minX.length; ++i) {
         this.minX[i] = par1;
         this.minY[i] = par2;
         this.minZ[i] = par3;
         this.maxX[i] = par4;
         this.maxY[i] = par5;
         this.maxZ[i] = par6;
      }

   }

   protected final void setBlockBounds(double par1, double par2, double par3, double par4, double par5, double par6) {
      this.setBlockBounds(par1, par2, par3, par4, par5, par6, true);
   }

   protected final void setBlockBounds(double par1, double par2, double par3, double par4, double par5, double par6, boolean for_all_threads) {
      if (for_all_threads) {
         this.setBlockBoundsForAllThreads(par1, par2, par3, par4, par5, par6);
      } else {
         this.setBlockBoundsForCurrentThread(par1, par2, par3, par4, par5, par6);
      }

   }

   public final void setBlockBoundsForCurrentThread(AxisAlignedBB bb) {
      this.setBlockBoundsForCurrentThread(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
   }

   public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return par1IBlockAccess.getBrightness(par2, par3, par4, getLightValue(par1IBlockAccess, par2, par3, par4));
   }

   public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3, par4, getLightValue(par1IBlockAccess, par2, par3, par4));
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return this.isOpaqueStandardFormCube(block_access, x, y, z);
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (this.isAlwaysStandardFormCube()) {
         return !doesBlockHideAdjacentSide(par1IBlockAccess, par2, par3, par4, this, par5);
      } else {
         int index = Minecraft.getThreadIndex();
         if (par5 == 0) {
            if (this.minY[index] > 0.0) {
               return true;
            }
         } else if (par5 == 1) {
            if (this.maxY[index] < 1.0) {
               return true;
            }
         } else if (par5 == 2) {
            if (this.minZ[index] > 0.0) {
               return true;
            }
         } else if (par5 == 3) {
            if (this.maxZ[index] < 1.0) {
               return true;
            }
         } else if (par5 == 4) {
            if (this.minX[index] > 0.0) {
               return true;
            }
         } else if (par5 == 5 && this.maxX[index] < 1.0) {
            return true;
         }

         return !doesBlockHideAdjacentSide(par1IBlockAccess, par2, par3, par4, this, par5);
      }
   }

   public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return this.getIcon(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public Icon getIcon(int par1, int par2) {
      return this.blockIcon;
   }

   public final Icon getBlockTextureFromSide(int par1) {
      return this.getIcon(par1, 0);
   }

   public boolean canCollideWithEntity(Entity entity) {
      return true;
   }

   public final boolean useFullBlockForCollisions(Entity entity) {
      if (!(entity instanceof EntityArachnid) && !(entity instanceof EntityOoze)) {
         if (entity instanceof EntityBat) {
            return false;
         } else if (entity instanceof EntityPudding) {
            return false;
         } else {
            return entity instanceof EntityLiving ? this instanceof BlockFence || this instanceof BlockFenceGate || this instanceof BlockWall || this instanceof BlockPane : false;
         }
      } else {
         return false;
      }
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
   {
      return this.getSelectedBoundingBoxFromPool(world, x, y, z);
   }


   public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
      if (this.isAlwaysStandardFormCube()) {
         return getStandardFormBoundingBoxFromPool(par2, par3, par4);
      } else {
         this.setBlockBoundsBasedOnStateAndNeighbors(par1World, par2, par3, par4);
         int index = Minecraft.getThreadIndex();
         return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX[index], (double)par3 + this.minY[index], (double)par4 + this.minZ[index], (double)par2 + this.maxX[index], (double)par3 + this.maxY[index], (double)par4 + this.maxZ[index]);
      }
   }

   public static final AxisAlignedBB getBoundingBoxFromPool(double min_x, double min_y, double min_z, double max_x, double max_y, double max_z) {
      return AxisAlignedBB.getBoundingBoxFromPool(min_x, min_y, min_z, max_x, max_y, max_z);
   }

   public static final AxisAlignedBB getBoundingBoxFromPool(int x, int y, int z, double min_x, double min_y, double min_z, double max_x, double max_y, double max_z) {
      return AxisAlignedBB.getBoundingBoxFromPool(x, y, z, min_x, min_y, min_z, max_x, max_y, max_z);
   }

   public static final void addIntersectingBoundsToList(AxisAlignedBB bb, List list, AxisAlignedBB mask) {
      if (bb != null && mask.intersectsWith(bb)) {
         list.add(bb);
      }

   }

   public static final AxisAlignedBB getStandardFormBoundingBoxFromPool() {
      return AxisAlignedBB.getBoundingBoxFromPool(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public static final AxisAlignedBB getStandardFormBoundingBoxFromPool(int x, int y, int z) {
      return AxisAlignedBB.getBoundingBoxFromPool(x, y, z, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public static final void addIntersectingStandardFormBoundsToList(int x, int y, int z, AxisAlignedBB mask, List list) {
      addIntersectingBoundsToList(getStandardFormBoundingBoxFromPool(x, y, z), list, mask);
   }

   public void addCollidingBoundsToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
      Object collision_bounds = this.getCollisionBounds(world, x, y, z, entity);
      if (collision_bounds != null) {
         if (collision_bounds instanceof AxisAlignedBB) {
            addIntersectingBoundsToList((AxisAlignedBB)collision_bounds, list, mask);
         } else {
            AxisAlignedBB[] array = (AxisAlignedBB[])((AxisAlignedBB[])collision_bounds);

            for(int i = 0; i < array.length; ++i) {
               if (array[i] != null) {
                  addIntersectingBoundsToList(array[i].translateCopy((double)x, (double)y, (double)z), list, mask);
               }
            }
         }

      }
   }

   public final AxisAlignedBB getBoundsFromPool(int x, int y, int z) {
      int index = Minecraft.getThreadIndex();
      return AxisAlignedBB.getAABBPool().getAABB((double)x + this.minX[index], (double)y + this.minY[index], (double)z + this.minZ[index], (double)x + this.maxX[index], (double)y + this.maxY[index], (double)z + this.maxZ[index]);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      if (this.isNeverSolid()) {
         return null;
      } else if (this.isAlwaysStandardFormCube()) {
         return AxisAlignedBB.getAABBPool().getAABB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
      } else {
         this.setBlockBoundsBasedOnStateAndNeighbors(world, x, y, z);
         return this.getBoundsFromPool(x, y, z);
      }
   }

   public Object getRenderBounds(World world, int x, int y, int z, Entity entity) {
      if (this.isAlwaysStandardFormCube()) {
         return AxisAlignedBB.getAABBPool().getAABB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
      } else {
         this.setBlockBoundsBasedOnStateAndNeighbors(world, x, y, z);
         return this.getBoundsFromPool(x, y, z);
      }
   }

   public final AxisAlignedBB getCollisionBoundsCombined(World world, int x, int y, int z, Entity entity, boolean as_non_local) {
      Object collision_bounds = this.getCollisionBounds(world, x, y, z, entity);
      if (collision_bounds == null) {
         return null;
      } else if (collision_bounds instanceof AxisAlignedBB) {
         return as_non_local ? (AxisAlignedBB)collision_bounds : ((AxisAlignedBB)collision_bounds).translate((double)(-x), (double)(-y), (double)(-z));
      } else {
         AxisAlignedBB[] array = (AxisAlignedBB[])((AxisAlignedBB[])collision_bounds);
         AxisAlignedBB combined = null;

         for(int i = 0; i < array.length; ++i) {
            AxisAlignedBB bb = array[i];
            if (bb != null) {
               if (combined == null) {
                  combined = bb.copy();
               } else {
                  combined.include(bb);
               }
            }
         }

         return combined == null ? null : (as_non_local ? combined.translate((double)x, (double)y, (double)z) : combined);
      }
   }

   public final AxisAlignedBB getBoundsFromPool() {
      int index = Minecraft.getThreadIndex();
      return AxisAlignedBB.getAABBPool().getAABB(this.minX[index], this.minY[index], this.minZ[index], this.maxX[index], this.maxY[index], this.maxZ[index]);
   }

   public final boolean isOpaqueStandardFormCube(int metadata) {
      Minecraft.setErrorMessage("isOpaqueStandardFormCube: This is not supposed to be based on metadata yet " + this);
      return false;
   }

   public final boolean isAlwaysOpaqueStandardFormCube() {
      return this.is_always_opaque_standard_form_cube;
   }

   public final boolean isNeverOpaqueStandardFormCube() {
      return this.is_never_opaque_standard_form_cube;
   }

   public final boolean isAlwaysStandardFormCube() {
      return this.is_always_standard_form_cube;
   }

   public final boolean isAlwaysSolid() {
      return this.is_always_solid;
   }

   public final boolean isNeverSolid() {
      return this.is_never_solid;
   }

   public final boolean isNeverStandardFormCube() {
      return this.is_never_standard_form_cube;
   }

   public final boolean isNeverSolidStandardFormCube() {
      return this.is_never_solid_standard_form_cube;
   }

   public final boolean isAlwaysSolidStandardFormCube() {
      return this.is_always_solid_standard_form_cube;
   }

   public final boolean isSolidStandardFormCube(int metadata) {
      if (this.isAlwaysSolidStandardFormCube()) {
         return true;
      } else if (this.isNeverSolidStandardFormCube()) {
         return false;
      } else {
         return this.isSolid(metadata) && this.isStandardFormCube(metadata);
      }
   }

   public static final boolean isAlwaysSolidStandardFormCube(Block block) {
      return block != null && block.isAlwaysSolidStandardFormCube();
   }

   public final boolean isAlwaysSolidOpaqueStandardFormCube() {
      return this.is_always_solid_opaque_standard_form_cube;
   }

   public final boolean isNeverSolidOpaqueStandardFormCube() {
      return this.is_never_solid_opaque_standard_form_cube;
   }

   public final boolean isSolidOpaqueStandardFormCube(int metadata) {
      Minecraft.setErrorMessage("isSolidOpaqueStandardFormCube: This is not supposed to be based on metadata yet " + this);
      return false;
   }

   public final boolean neverHidesAdjacentFaces() {
      return this.never_hides_adjacent_faces;
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      return this.isAlwaysSolidStandardFormCube() || this.isSolidStandardFormCube(metadata);
   }

   public boolean canCollideCheck(int par1, boolean par2) {
      return this.isCollidable();
   }

   public boolean isCollidable() {
      return true;
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      if (world.isRemote) {
         Minecraft.setErrorMessage("onNotLegal: not meant to be called on client");
      }

      this.dropBlockAsEntityItem((new BlockBreakInfo(world, x, y, z)).setWasNotLegal());
      world.setBlockToAir(x, y, z);
      return true;
   }

   public final boolean checkIfNotLegal(World world, int x, int y, int z) {
      if (this.isAlwaysLegal()) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         return this.isLegalAt(world, x, y, z, metadata) ? false : this.onNotLegal(world, x, y, z, metadata);
      }
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      return this.checkIfNotLegal(world, x, y, z);
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (world.isRemote) {
         Minecraft.setErrorMessage("onNeighborBlockChange: called on client?");
      }

      if (Minecraft.allow_new_sand_physics) {
         Block block_above = world.getBlock(x, y + 1, z);
         if (block_above != null && block_above.usesNewSandPhysics()) {
            block_above.checkIfNotLegal(world, x, y + 1, z);
         }
      }

      return this.checkIfNotLegal(world, x, y, z);
   }

   public int tickRate(World par1World) {
      return 10;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      if (hasTileEntity(par6) && !(this instanceof BlockContainer))
      {
         par1World.removeBlockTileEntity(par2, par3, par4);
      }
   }

   public void onTrampledBy(World world, int x, int y, int z, Entity entity) {
   }

   public void onBlockAboutToBeBroken(BlockBreakInfo info) {
   }

   public boolean isDislodgedOrCrushedByFallingBlock(int metadata, Block falling_block, int falling_block_metadata) {
      return !this.isSolid(metadata) || falling_block.canReplaceBlock(falling_block_metadata, this, metadata);
   }

   public int dropBlockAsItself(BlockBreakInfo info) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsItself: info.block!=this");
      }

      if (!info.block.canBeCarried()) {
         Minecraft.setErrorMessage("dropBlockAsItself: " + this + " cannot be carried");
      }

      return this.dropBlockAsEntityItem(info, this.createStackedBlock(info.getMetadata()));
   }

   public static int dropBlockAsEntityItem(Block block, BlockBreakInfo info) {
      return info.wasSilkHarvested() ? block.dropBlockAsItself(info) : block.dropBlockAsEntityItem(info);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: info.block!=this");
      }

      if (info.wasSilkHarvested()) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: This function is not meant to be used for silk harvested blocks " + this);
         (new Exception()).printStackTrace();
      }

      if (info.wasCrushed()) {
         return 0;
      } else {
         if (info.wasExploded()) {
            if (this == brick) {
               return this.dropBlockAsEntityItem(info, Item.brick.itemID, 0, 1, 1.5F);
            }

            if (this == cobblestone || this == cobblestoneMossy) {
               return this.dropBlockAsEntityItem((BlockBreakInfo)info, (Block)gravel);
            }

            if (this == blockLapis) {
               return this.dropBlockAsEntityItem(info, Item.dyePowder.itemID, 4, 9, 0.5F);
            }

            if (this.blockMaterial == Material.cloth) {
               return this.dropBlockAsEntityItem(info, Item.silk);
            }

            if (this.blockMaterial == Material.wood) {
               return this.dropBlockAsEntityItem(info, Item.stick);
            }

            if (this.blockMaterial == Material.hardened_clay) {
               return 0;
            }

            if (this.blockMaterial == Material.stone) {
               return this.dropBlockAsEntityItem(info, cobblestone);
            }

            if (this.blockMaterial == Material.netherrack) {
               return 0;
            }
         }

         return this == coalBlock ? this.dropBlockAsEntityItem(info, Item.coal.itemID, 0, 9, info.wasExploded() ? 0.5F : 1.0F) : this.dropBlockAsEntityItem(info, this.createStackedBlock(info.getMetadata()));
      }
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info, Block block) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: info.block!=this");
      }

      return block == null ? 0 : this.dropBlockAsEntityItem(info, block.blockID, 0, 1, 1.0F);
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info, Item item) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: info.block!=this");
      }

      return item == null ? 0 : this.dropBlockAsEntityItem(info, item.itemID, 0, 1, 1.0F);
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info, int id_dropped) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: info.block!=this");
      }

      return id_dropped < 1 ? 0 : this.dropBlockAsEntityItem(info, id_dropped, 0, 1, 1.0F);
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info, ItemStack item_stack) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: info.block!=this");
      }

      if (item_stack == null) {
         return 0;
      } else {
         if (item_stack.isItemDamaged() && info.damage != item_stack.getItemDamage()) {
            if (info.damage != 0) {
               Minecraft.setErrorMessage("dropBlockAsEntityItem: info.damage was already set to " + info.damage + " (vs " + item_stack.getItemDamage() + ")");
            }

            info.damage = item_stack.getItemDamage();
         }

         return this.dropBlockAsEntityItem(info, item_stack.itemID, item_stack.getItemSubtype(), item_stack.stackSize, 1.0F);
      }
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info, int id_dropped, int subtype) {
      if (info.block != this) {
         Minecraft.setErrorMessage("dropBlockAsItself: info.block!=this");
      }

      return this.dropBlockAsEntityItem(info, id_dropped, subtype, 1, 1.0F);
   }

   public final int dropBlockAsEntityItem(World world, int x, int y, int z, int subtype, float chance) {
      BlockBreakInfo info = new BlockBreakInfo(world, x, y, z);
      return this.dropBlockAsEntityItem(info, this.blockID, subtype, 1, chance);
   }

   public final int dropBlockAsEntityItem(World world, int x, int y, int z, int id_dropped, int subtype, int quantity, float chance) {
      BlockBreakInfo info = new BlockBreakInfo(world, x, y, z);
      return this.dropBlockAsEntityItem(info, id_dropped, subtype, quantity, chance);
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info, int id_dropped, int subtype, int quantity, float chance) {
      if (info.world.isRemote) {
         Minecraft.setErrorMessage("dropBlockAsEntityItem: not meant to be called on client " + info.x + "," + info.y + "," + info.z);
         return 0;
      } else {
         if (info.block != this) {
            Minecraft.setErrorMessage("dropBlockAsEntityItem: info.block!=this");
         }

         if (id_dropped >= 1 && quantity >= 1 && !(chance <= 0.0F)) {
            if (info.wasExploded()) {
               if (this.blockMaterial != Material.snow && this.blockMaterial != Material.craftedSnow && this.blockMaterial != Material.glass) {
                  if (this.blockMaterial != Material.plants && this.blockMaterial != Material.cactus && this.blockMaterial != Material.vine) {
                     if (this.blockMaterial == Material.cake) {
                        chance = 0.0F;
                     }
                  } else {
                     chance = 0.0F;
                  }
               } else {
                  chance *= 0.4F;
               }
            }

            info.chance = chance;

            if (chance <= 0.0F) {
               return 0;
            } else {

               int damage = info.damage;
               int num_drops = 0;

               ArrayList<ItemStack> items = getBlockDropped(info.world, info.x, info.y, info.z, info.getMetadata(), info.getHarvesterFortune());
               chance = ForgeEventFactory.fireBlockHarvesting(items, info);

               for (ItemStack item_stack : items) {
                  if (info.world.rand.nextFloat() < chance) {
                     if (item_stack.isBlock() && !item_stack.getItemAsBlock().getBlock().canBeCarried()) {
                        ItemStack substitute = item_stack.getItemAsBlock().getBlock().createStackedBlock(subtype);
                        Minecraft.setErrorMessage("dropBlockAsEntityItem: " + item_stack + " can not be carried, substituting with " + substitute);
                        if (substitute == null) {
                           Minecraft.setErrorMessage("dropBlockAsEntityItem: createStackedBlock returned null for " + item_stack);
                           return num_drops;
                        }

                        if (substitute.isBlock() && !substitute.getItemAsBlock().getBlock().canBeCarried()) {
                           Minecraft.setErrorMessage("dropBlockAsEntityItem: substitute " + substitute + " can not be carried either. Aborting");
                           return num_drops;
                        }

                        item_stack = substitute;
                     }

                     if (id_dropped > 0) {
                        EntityItem entity_item = this.dropBlockAsItem_do(info, item_stack.copy());
                        if (damage != 0) {
                           entity_item.getEntityItem().setItemDamage(damage);
                        }

                        if (quantity == 1 && chance <= 1.0F && info.tile_entity != null && info.tile_entity.getCustomInvName() != null) {
                           entity_item.getEntityItem().setItemName(info.tile_entity.getCustomInvName());
                        }

                        if (chance > 1.0F && info.world.rand.nextFloat() < chance - 1.0F) {
                           entity_item = this.dropBlockAsItem_do(info, item_stack.copy());
                           if (damage != 0) {
                              entity_item.getEntityItem().setItemDamage(damage);
                           }
                        }

                        ++num_drops;
                     }
                  }
               }

               if (this.canDropExperienceOrbs()) {
                  int i = Item.getItem(id_dropped).getExperienceReward(subtype);
                  if (i > 0) {
                     this.dropXpOnBlockBreak(info.world, info.x, info.y, info.z, i * num_drops);
                  }
               }

               return num_drops;
            }
         } else {
            return 0;
         }
      }
   }

   public boolean canDropExperienceOrbs() {
      return this != blockLapis;
   }

   private EntityItem dropBlockAsItem_do(BlockBreakInfo info, ItemStack item_stack) {
      World world = info.world;
      if (world.isRemote) {
         Minecraft.setErrorMessage("dropBlockAsItem_do: not meant to be called on client");
         return null;
      } else if (world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
         EntityItem entity_item = info.createEntityItem(item_stack);
         entity_item.delayBeforeCanPickup = 10;
         if (info.wasPickedByPlayer()) {
            EntityPlayer player = info.getResponsiblePlayer();
            entity_item.motionX = entity_item.motionY = entity_item.motionZ = 0.0;
            Vec3 player_center = player.getCenterPoint();
            entity_item.setPosition(player_center.xCoord, player_center.yCoord, player_center.zCoord);
            entity_item.delayBeforeCanPickup = 0;
         } else if (info.wasWindfall()) {
            entity_item.motionX = entity_item.motionY = entity_item.motionZ = 0.0;
         }

         if (info.wasExploded()) {
            world.addToSpawnPendingList(entity_item, world.getTotalWorldTime() + 1L);
         } else {
            world.spawnEntityInWorld(entity_item);
         }

         if (info.wasExploded()) {
            entity_item.applyExplosionMotion(info.explosion);
         }

         return entity_item;
      } else {
         return null;
      }
   }

   public void dropXpOnBlockBreak(World par1World, int par2, int par3, int par4, int par5) {
      if (!par1World.isRemote) {
         while(par5 > 0) {
            int var6 = EntityXPOrb.getXPSplit(par5);
            par5 -= var6;
            par1World.spawnEntityInWorld(new EntityXPOrb(par1World, (double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, var6));
         }
      }

   }

   public float getExplosionResistance(Explosion explosion) {
      return this.blockHardness;
   }

   public RaycastCollision tryRaycastVsBlock(Raycast raycast, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3) {
      World par1World = raycast.getWorld();
      Object object = this.getCollisionBounds(par1World, par2, par3, par4, (Entity)null);
      if (object instanceof AxisAlignedBB[]) {
         AxisAlignedBB[] multiple_bounds = (AxisAlignedBB[])((AxisAlignedBB[])object);
         RaycastCollision[] rc = new RaycastCollision[multiple_bounds.length];

         for(int i = 0; i < rc.length; ++i) {
            if (multiple_bounds[i] != null) {
               rc[i] = this.tryRaycastVsBlock(raycast, par2, par3, par4, par5Vec3, par6Vec3, multiple_bounds[i]);
            }
         }

         return Raycast.getShortestRaycastCollision(rc);
      } else {
         this.setBlockBoundsBasedOnStateAndNeighbors(par1World, par2, par3, par4);
         return this.tryRaycastVsBlock(raycast, par2, par3, par4, par5Vec3, par6Vec3, this.getBoundsFromPool());
      }
   }

   public final RaycastCollision tryRaycastVsStandardFormBounds(Raycast raycast, int x, int y, int z, Vec3 origin, Vec3 limit) {
      return this.tryRaycastVsBlock(raycast, x, y, z, origin, limit, standard_form_bounding_box);
   }

   public final RaycastCollision tryRaycastVsBlock(Raycast raycast, int x, int y, int z, Vec3 origin, Vec3 limit, AxisAlignedBB bounds) {
      World world = raycast.getWorld();
      if (raycast.ignoreBlock(this, world, x, y, z)) {
         return null;
      } else {
         origin = origin.addVector((double)(-x), (double)(-y), (double)(-z));
         limit = limit.addVector((double)(-x), (double)(-y), (double)(-z));
         AABBIntercept intercept = bounds.calculateIntercept(world, origin, limit);
         return intercept == null ? null : new RaycastCollision(raycast, x, y, z, intercept.face_hit, intercept.position_hit.translate((double)x, (double)y, (double)z));
      }
   }

   public boolean isPortal() {
      return false;
   }

   public boolean isOpenPortal(World world, int x, int y, int z) {
      return false;
   }

   private final boolean isVecInsideYZBounds(Vec3 par1Vec3) {
      if (this.isAlwaysStandardFormCube()) {
         return par1Vec3.yCoord >= 0.0 && par1Vec3.yCoord <= 1.0 && par1Vec3.zCoord >= 0.0 && par1Vec3.zCoord <= 1.0;
      } else {
         int index = Minecraft.getThreadIndex();
         return par1Vec3.yCoord >= this.minY[index] && par1Vec3.yCoord <= this.maxY[index] && par1Vec3.zCoord >= this.minZ[index] && par1Vec3.zCoord <= this.maxZ[index];
      }
   }

   private final boolean isVecInsideXZBounds(Vec3 par1Vec3) {
      if (this.isAlwaysStandardFormCube()) {
         return par1Vec3.xCoord >= 0.0 && par1Vec3.xCoord <= 1.0 && par1Vec3.zCoord >= 0.0 && par1Vec3.zCoord <= 1.0;
      } else {
         int index = Minecraft.getThreadIndex();
         return par1Vec3.xCoord >= this.minX[index] && par1Vec3.xCoord <= this.maxX[index] && par1Vec3.zCoord >= this.minZ[index] && par1Vec3.zCoord <= this.maxZ[index];
      }
   }

   private final boolean isVecInsideXYBounds(Vec3 par1Vec3) {
      if (this.isAlwaysStandardFormCube()) {
         return par1Vec3.xCoord >= 0.0 && par1Vec3.xCoord <= 1.0 && par1Vec3.yCoord >= 0.0 && par1Vec3.yCoord <= 1.0;
      } else {
         int index = Minecraft.getThreadIndex();
         return par1Vec3.xCoord >= this.minX[index] && par1Vec3.xCoord <= this.maxX[index] && par1Vec3.yCoord >= this.minY[index] && par1Vec3.yCoord <= this.maxY[index];
      }
   }

   public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {
      Block block_above = blocksList[par1World.getBlockId(par2, par3 + 1, par4)];
      if (block_above instanceof BlockUnderminable) {
         ((BlockUnderminable)block_above).scheduleUndermine(par1World, par2, par3 + 1, par4);
      }

   }

   public int getRenderBlockPass() {
      return 0;
   }

   public final boolean isBlockTopFacingSurfaceSolid(int metadata) {
      return this.isFaceFlatAndSolid(metadata, EnumFace.TOP);
   }

   public boolean canBePlacedOnBlock(int metadata, Block block_below, int block_below_metadata, double block_below_bounds_max_y) {
      if (block_below == null) {
         return false;
      } else if (block_below != tilledField && !(block_below instanceof BlockWall)) {
         if (block_below == snow && BlockSnow.getDepth(block_below_metadata) == BlockSnow.getMaxDepth()) {
            return this.isLegalOn(metadata, block_below, block_below_metadata);
         } else {
            return block_below_bounds_max_y == 1.0 && block_below.isSolid(block_below_metadata) && this.isLegalOn(metadata, block_below, block_below_metadata);
         }
      } else {
         return this.isLegalOn(metadata, block_below, block_below_metadata);
      }
   }

   public final boolean isAlwaysLegal() {
      return this.is_always_legal;
   }

   public final boolean usesNewSandPhysics() {
      return this.uses_new_sand_physics;
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return this.isAlwaysLegal() ? true : this.isLegalOn(metadata, world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return true;
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      Block existing_block = world.getBlock(x, y, z);
      return existing_block != null &&
              !this.canReplaceBlock(metadata, existing_block, world.getBlockMetadata(x, y, z)) ? false : this.isLegalAt(world, x, y, z, metadata);
   }

   public boolean canOccurAt(World world, int x, int y, int z, int metadata) {
      return world.isAirBlock(x, y, z) && this.canBePlacedAt(world, x, y, z, metadata);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return item_stack.getHasSubtypes() ? item_stack.getItemSubtype() : 0;
   }

   public boolean isAlwaysReplaceable() {
      return false;
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return other_block != null && other_block != this && (this.isLiquid() || this.isAlwaysReplaceable() || this.getBlockHardness(metadata) == 0.0F);
   }

   public boolean canReplaceBlock(int metadata, Block existing_block, int existing_block_metadata) {
      if (existing_block != null && existing_block.blockMaterial != Material.air) {
         return existing_block.canBeReplacedBy(existing_block_metadata, this, metadata);
      } else {
         Minecraft.setErrorMessage("canReplaceBlock: there is no existing block for " + this.getLocalizedName() + " to replace");
         return false;
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return false;
   }

   public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {
   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
   }

   public void velocityToAddToEntity(World par1World, int par2, int par3, int par4, Entity par5Entity, Vec3 par6Vec3) {
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
   }

   public final double getBlockBoundsMinX(int index) {
      return this.minX[index];
   }

   public final double getBlockBoundsMaxX(int index) {
      return this.maxX[index];
   }

   public final double getBlockBoundsMinY(int index) {
      return this.minY[index];
   }

   public final double getBlockBoundsMaxY(int index) {
      return this.maxY[index];
   }

   public final double getBlockBoundsMinZ(int index) {
      return this.minZ[index];
   }

   public final double getBlockBoundsMaxZ(int index) {
      return this.maxZ[index];
   }

   public int getBlockColor() {
      return 16777215;
   }

   public int getRenderColor(int par1) {
      return 16777215;
   }

   public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return 16777215;
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return 0;
   }

   public boolean canProvidePower() {
      return false;
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
   }

   public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return 0;
   }

   public void setBlockBoundsForItemRender(int item_damage) {
   }

   public boolean canBeCarried() {
      return true;
   }

   public boolean canSilkHarvest(int metadata) {
      return this.canBeCarried();
   }

   public ItemStack createStackedBlock(int metadata) {
      return this.canBeCarried() ? new ItemStack(this, 1, this.getItemSubtype(metadata)) : null;
   }

   public void onUnderminedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
   }

   public Block setUnlocalizedName(String par1Str) {
      this.unlocalizedName = par1Str;
      return this;
   }

   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
   }

   public String getUnlocalizedName() {
      return "tile." + this.unlocalizedName;
   }

   public final String getNameForReferenceFile(int metadata, boolean include_disambiguation) {
      if (include_disambiguation) {
         String disambiguation = this.getNameDisambiguationForReferenceFile(metadata);
         return disambiguation == null ? this.getLocalizedName() : this.getLocalizedName() + " (" + disambiguation + ")";
      } else {
         return this.getLocalizedName();
      }
   }

   public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
      return false;
   }

   public boolean getEnableStats() {
      return this.enableStats;
   }

   protected Block disableStats() {
      this.enableStats = false;
      return this;
   }

   public int getMobilityFlag() {
      return this.blockMaterial.getMaterialMobility();
   }

   public final float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int block_id = par1IBlockAccess.getBlockId(par2, par3, par4);
      if (block_id == 0) {
         return 1.0F;
      } else if (is_normal_cube_lookup[block_id]) {
         return 0.2F;
      } else {
         return getBlock(block_id).is_tree_leaves ? 0.4F : 1.0F;
      }
   }

   public void onFallenUpon(World par1World, int par2, int par3, int par4, Entity par5Entity, float par6) {
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return this.blockID;
   }

   public String getMetadataNotes() {
      return null;
   }

   public boolean isValidMetadata(int metadata) {
      return metadata == 0;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return 0;
   }

   public final int getBlockSubtype(int metadata) {
      if (!this.isValidMetadata(metadata)) {
         this.reportInvalidMetadata(metadata);
         return 0;
      } else {
         return this.getBlockSubtypeUnchecked(metadata);
      }
   }

   public int getItemSubtype(int metadata) {
      return this.getBlockSubtype(metadata);
   }

   public void getItemStacks(int id, CreativeTabs creative_tabs, List list) {
      for(int i = 0; i < this.item_subtypes.length; ++i) {
         list.add(new ItemStack(id, 1, this.item_subtypes[i]));
      }

   }

   public final List getItemStacks() {
      List list = new ArrayList();
      this.getItemStacks(this.blockID, (CreativeTabs)null, list);
      return list;
   }

   public void reportInvalidMetadata(int metadata) {
      Minecraft.setErrorMessage("Block: invalid metadata value of " + metadata + " for Block[" + this.blockID + "]");
      (new Exception()).printStackTrace();
   }

   private final int[] getItemSubtypes() {
      boolean[] subtype_exists = new boolean[16];

      int num;
      for(num = 0; num < 16; ++num) {
         if (this.isValidMetadata(num)) {
            subtype_exists[this.getItemSubtype(num)] = true;
         }
      }

      num = 0;

      for(int metadata = 0; metadata < 16; ++metadata) {
         if (subtype_exists[metadata]) {
            ++num;
         }
      }

      int[] subtypes = new int[num];
      int i = 0;

      for(int metadata = 0; metadata < 16; ++metadata) {
         if (subtype_exists[metadata]) {
            subtypes[i++] = metadata;
         }
      }

      return subtypes;
   }

   public final int getNumSubBlocks() {
      return this.num_item_subtypes;
   }

   public CreativeTabs getCreativeTabToDisplayOn() {
      return this.displayOnCreativeTab;
   }

   public Block setCreativeTab(CreativeTabs par1CreativeTabs) {
      this.displayOnCreativeTab = par1CreativeTabs;
      return this;
   }

   public void onBlockPreDestroy(World par1World, int par2, int par3, int par4, int par5) {
   }

   public void fillWithRain(World par1World, int par2, int par3, int par4) {
   }

   public boolean isFlowerPot() {
      return false;
   }

   public boolean func_82506_l() {
      return true;
   }

   public boolean canDropFromExplosion(Explosion par1Explosion) {
      return true;
   }

   public boolean isAssociatedBlockID(int par1) {
      return this.blockID == par1;
   }

   public static boolean isAssociatedBlockID(int par0, int par1) {
      return par0 == par1 ? true : (par0 != 0 && par1 != 0 && blocksList[par0] != null && blocksList[par1] != null ? blocksList[par0].isAssociatedBlockID(par1) : false);
   }

   public boolean hasComparatorInputOverride() {
      return false;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      return 0;
   }

   protected Block setTextureName(String par1Str) {
      this.textureName = par1Str;
      return this;
   }

   protected String getTextureName() {
      return this.textureName == null ? "MISSING_ICON_TILE_" + this.blockID + "_" + this.unlocalizedName : this.textureName;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName());
   }

   public Icon[] registerIcons(IconRegister par1IconRegister, String[] types) {
      return this.registerIcons(par1IconRegister, types, (String)null, (String)null);
   }

   public Icon[] registerIcons(IconRegister par1IconRegister, String[] types, String prefix) {
      return this.registerIcons(par1IconRegister, types, prefix, (String)null);
   }

   public Icon[] registerIcons(IconRegister par1IconRegister, String[] types, String prefix, String suffix) {
      Icon[] icons = new Icon[types.length];

      for(int i = 0; i < icons.length; ++i) {
         if (types[i] != null) {
            StringBuilder sb = new StringBuilder();
            if (prefix != null) {
               sb.append(prefix);
            }

            sb.append(types[i]);
            if (suffix != null) {
               sb.append(suffix);
            }

            icons[i] = par1IconRegister.registerIcon(sb.toString());
         }
      }

      return icons;
   }

   public String getItemIconName() {
      return null;
   }

   public Block setCushioning(float cushioning) {
      this.cushioning = cushioning;
      return this;
   }

   public float getCushioning(int metadata) {
      return this.cushioning;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return true;
   }

   public Block setMaxStackSize(int max_stack_size) {
      this.max_stack_size = max_stack_size;
      return this;
   }

   public int getItemStackLimit() {
      return this.max_stack_size;
   }

   public float getCraftingDifficultyAsComponent(int metadata) {
      float hardness = this.getBlockHardness(metadata);
      return hardness < 0.0F ? -1.0F : hardness * 100.0F;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return false;
   }

   public static Block getBlock(int block_id) {
      return block_id > 0 && block_id < blocksList.length ? blocksList[block_id] : null;
   }

   public static Block getBlock(String localized_name) {
      for(int i = 1; i < 256; ++i) {
         Block block = getBlock(i);
         if (block != null && localized_name.equalsIgnoreCase(block.getLocalizedName())) {
            return block;
         }
      }

      return null;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return null;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{this.blockMaterial});
   }

   public final boolean isLiquid() {
      return this.blockMaterial.isLiquid();
   }

   public static int getStageOfBlockDestruction(float block_damage) {
      return (int)(block_damage * 10.0F) - 1;
   }

   private boolean doesNeighborBlockExist(World world, int x, int y, int z, EnumFace face) {
      x = face.getNeighborX(x);
      y = face.getNeighborY(y);
      z = face.getNeighborZ(z);
      if (world.getBlock(x, y, z) == null) {
         Minecraft.setErrorMessage("doesNeighborBlockExist: face hit was " + face + " but no neighbor block exists at " + x + "," + y + "," + z);
         return false;
      } else {
         return true;
      }
   }

   public boolean tryPlaceBlock(World world, int x, int y, int z, EnumFace face, int metadata, Entity placer, boolean perform_placement_check, boolean drop_existing_block, boolean test_only) {
      if (world.isRemote && !test_only) {
         Minecraft.setErrorMessage("tryPlaceBlock: must be test only on client");
         return false;
      } else {
         if (!world.isRemote && placer instanceof EntityPlayer) {
            placer.getAsPlayer().sendPacket((new Packet85SimpleSignal(EnumSignal.clear_tentative_bounding_box)).setBlockCoords(x, y, z));
         }

         if (y < 0) {
            return false;
         } else {
            int saved_metadata = world.getBlockMetadata(x, y, z);
            world.setBlockMetadataWithNotify(x, y, z, metadata, 0);
            AxisAlignedBB bounding_box = this.getCollisionBoundsCombined(world, x, y, z, (Entity)null, true);
            world.setBlockMetadataWithNotify(x, y, z, saved_metadata, 0);
            if (this instanceof BlockLadder) {
               bounding_box = null;
            }

            if (bounding_box != null && !world.checkNoEntityCollision(bounding_box)) {
               return false;
            } else {
               if (this instanceof BlockTorch) {
                  AxisAlignedBB bb = world.getBoundingBoxFromPool(x, y, z);
                  List gelatinous_cubes = world.getEntitiesWithinAABB(EntityGelatinousCube.class, bb);
                  if (!gelatinous_cubes.isEmpty()) {
                     return false;
                  }
               }

               Block existing_block;
               if (perform_placement_check) {
                  if (face.isTop()) {
                     existing_block = world.getBlock(x, y - 1, z);
                     int block_below_metadata = world.getBlockMetadata(x, y - 1, z);
                     existing_block.setBlockBoundsBasedOnStateAndNeighbors(world, x, y - 1, z);
                     if (!this.canBePlacedOnBlock(metadata, existing_block, block_below_metadata, existing_block.maxY[Minecraft.getThreadIndex()])) {
                        return false;
                     }
                  }

                  if (!this.canBePlacedAt(world, x, y, z, metadata)) {
                     return false;
                  }
               }

               if (test_only) {
                  boolean result = this.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
                  if (result && bounding_box != null && placer instanceof EntityPlayer && placer.onClient()) {
                     placer.getAsPlayer().tentative_bounding_boxes.add(new TentativeBoundingBox(x, y, z, new AxisAlignedBB(bounding_box)));
                  }

                  return result;
               } else if (y >= MinecraftServer.getServer().getBuildLimit() - 1) {
                  return false;
               } else {
                  existing_block = drop_existing_block ? world.getBlock(x, y, z) : null;
                  BlockBreakInfo info = existing_block == null ? null : (new BlockBreakInfo(world, x, y, z)).setReplaced();
                  if (existing_block != null && existing_block != this && existing_block.showDestructionParticlesWhenReplacedBy(info.getMetadata(), this, metadata)) {
                     world.blockFX(EnumBlockFX.destroy, x, y, z, (new SignalData()).setInteger(existing_block.blockID + (info.getMetadata() << 8) + (this.blockID << 12) + (metadata << 20)));
                  }

                  if (world.setBlock(x, y, z, this.blockID, metadata, 3)) {
                     if (existing_block != null) {
                        existing_block.dropBlockAsEntityItem(info);
                     }

                     if (placer instanceof EntityPlayer) {
                        EntityPlayer player = placer.getAsPlayer();
                        player.block_placement_tick = world.getTotalWorldTime();
                        player.block_placement_pos_x = player.posX;
                        player.block_placement_pos_y = player.posY;
                        player.block_placement_pos_z = player.posZ;
                        player.block_placement_world = world;
                     }

                     this.makeSoundWhenPlaced(world, x, y, z, metadata);
                     return this.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   public void makeSoundWhenPlaced(World world, int x, int y, int z, int metadata) {
      if (this.stepSound != null) {
         world.playSoundAtBlock(x, y, z, this.stepSound.getPlaceSound(), (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
      }

   }

   public boolean showDestructionParticlesWhenReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return false;
   }

   public final boolean tryPlaceBlock(World world, int x, int y, int z, EnumFace face, int metadata, Entity placer, boolean perform_placement_check, boolean drop_existing_block) {
      return this.tryPlaceBlock(world, x, y, z, face, metadata, placer, perform_placement_check, drop_existing_block, world.isRemote);
   }

   public boolean tryPlaceFromHeldItem(int x, int y, int z, EnumFace face, ItemStack item_stack, EntityPlayer player, float offset_x, float offset_y, float offset_z, boolean perform_placement_check, boolean drop_existing_block, boolean test_only) {
      if (player.onServer()) {
         player.sendPacket((new Packet85SimpleSignal(EnumSignal.clear_tentative_bounding_box)).setBlockCoords(x, y, z));
      }

      if (player.worldObj.getBlockMaterial(x, y, z) == Material.lava) {
         World world = player.worldObj;
         if (this == blockSnow || this == ice) {
            if (player.onServer()) {
               world.tryConvertLavaToCobblestoneOrObsidian(x, y, z);
            }

            return true;
         }

         if (this == snow) {
            if (player.onServer()) {
               world.blockFX(EnumBlockFX.steam, x, y, z);
            }

            return true;
         }
      } else if ((player.isInNether() || player.worldObj.getBlock(x, y - 1, z) == mantleOrCore) && (this == blockSnow || this == ice || this == snow)) {
         if (player.onServer()) {
            if (player.worldObj.getBlock(x, y, z) == fire) {
               player.worldObj.douseFire(x, y, z, (Entity)null);
            } else {
               player.worldObj.blockFX(EnumBlockFX.steam, x, y, z);
            }
         }

         return true;
      }

      int metadata = this.getMetadataForPlacement(player.worldObj, x, y, z, item_stack, player, face, offset_x, offset_y, offset_z);
      if (metadata < 0) {
         return false;
      } else if (this.getBlockHardness(metadata) > 0.0F && !player.hasFoodEnergy() && !player.inCreativeMode()) {
         return false;
      } else {
         return this.tryPlaceBlock(player.worldObj, x, y, z, face, metadata, player, perform_placement_check, drop_existing_block, test_only);
      }
   }

   public final boolean tryPlaceFromHeldItem(int x, int y, int z, EnumFace face, ItemStack item_stack, EntityPlayer player, float offset_x, float offset_y, float offset_z, boolean perform_placement_check, boolean drop_existing_block) {
      return this.tryPlaceFromHeldItem(x, y, z, face, item_stack, player, offset_x, offset_y, offset_z, perform_placement_check, drop_existing_block, player.onClient());
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer instanceof EntityLivingBase) {
         ItemStack item_stack = placer.getAsEntityLivingBase().getHeldItemStack();
         if (item_stack.hasDisplayName()) {
            TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               tile_entity.setCustomInvName(item_stack.getDisplayName());
            }
         }
      }

      return true;
   }

   public int getDefaultMetadata(World world, int x, int y, int z) {
      return 0;
   }

   public final boolean hasItemSubtypes() {
      return this.has_item_subtypes;
   }

   public EnumDirection getDirectionFacing(int metadata) {
      return null;
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return metadata;
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction, int base_coord_mode) {
      return this.getMetadataForDirectionFacing(metadata, direction);
   }

   public Block getAlternativeBlockForPlacement() {
      return null;
   }

   public void onBlockDamageStageChange(int x, int y, int z, Entity entity, int damage_stage) {
   }

   public static final boolean isBlockSingleSlab(int id) {
      return getBlock(id) instanceof BlockSlab;
   }

   public final boolean isSingleSlab() {
      return this instanceof BlockSlab;
   }

   public final boolean isSingleSlabLower(int metadata) {
      return this.isSingleSlab() && BlockSlab.isBottom(metadata);
   }

   public final boolean isSingleSlabUpper(int metadata) {
      return this.isSingleSlab() && BlockSlab.isTop(metadata);
   }

   public int getMinHarvestLevel(int metadata) {
      return this.min_harvest_level;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return true;
   }

   public final boolean isSolid(int metadata) {
      return this.is_solid[metadata];
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return true;
   }

   public final boolean isStandardFormCube(int metadata) {
      return this.is_standard_form_cube[metadata];
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return this.isLiquid() || this.isStandardFormCube(metadata) && this.isSolid(metadata);
   }

   public final boolean blocksPrecipitation(int metadata) {
      return this.blocks_precipitation[metadata];
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return this.isSolid(metadata);
   }

   public final boolean blocksFluids(int metadata) {
      return this.blocks_fluids[metadata];
   }

   public boolean canSupportEntityShadow(int metadata) {
      if (!(this instanceof BlockCactus) && !(this instanceof BlockCauldron) && !(this instanceof BlockHopper) && !(this instanceof BlockPistonExtension)) {
         return !this.neverHidesAdjacentFaces();
      } else {
         return false;
      }
   }

   public static final boolean doesBlockHideAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      int block_id = block_access.getBlockId(x, y, z);
      if (block_id == 0) {
         return false;
      } else {
         Block block = getBlock(block_id);
         return block.neverHidesAdjacentFaces() ? false : block.hidesAdjacentSide(block_access, x, y, z, neighbor, side);
      }
   }

   public static final boolean isBlockSolid(IBlockAccess block_access, int x, int y, int z) {
      return isBlockSolid(block_access, getBlock(block_access.getBlockId(x, y, z)), x, y, z);
   }

   public static final boolean isBlockSolid(IBlockAccess block_access, Block block, int x, int y, int z) {
      return block != null && block.isSolid(block_access, x, y, z);
   }

   public final boolean isSolid(IBlockAccess block_access, int x, int y, int z) {
      if (this.isAlwaysSolid()) {
         return true;
      } else {
         return this.isNeverSolid() ? false : this.isSolid(block_access.getBlockMetadata(x, y, z));
      }
   }

   public static final boolean isBlockOpaqueStandardFormCube(IBlockAccess block_access, int x, int y, int z) {
      return isBlockOpaqueStandardFormCube(block_access, getBlock(block_access.getBlockId(x, y, z)), x, y, z);
   }

   public static final boolean isBlockOpaqueStandardFormCube(IBlockAccess block_access, Block block, int x, int y, int z) {
      return block != null && block.isOpaqueStandardFormCube(block_access, x, y, z);
   }

   public final boolean isOpaqueStandardFormCube(IBlockAccess block_access, int x, int y, int z) {
      if (this.isAlwaysOpaqueStandardFormCube()) {
         return true;
      } else {
         return this.isNeverOpaqueStandardFormCube() ? false : this.isOpaqueStandardFormCube(block_access.getBlockMetadata(x, y, z));
      }
   }

   public static final boolean isBlockSolidOpaqueStandardFormCube(IBlockAccess block_access, int x, int y, int z) {
      return isBlockSolidOpaqueStandardFormCube(block_access, getBlock(block_access.getBlockId(x, y, z)), x, y, z);
   }

   public static final boolean isBlockSolidOpaqueStandardFormCube(IBlockAccess block_access, Block block, int x, int y, int z) {
      return block != null && block.isSolidOpaqueStandardFormCube(block_access, x, y, z);
   }

   public final boolean isSolidOpaqueStandardFormCube(IBlockAccess block_access, int x, int y, int z) {
      if (this.isAlwaysSolidOpaqueStandardFormCube()) {
         return true;
      } else {
         return this.isNeverSolidOpaqueStandardFormCube() ? false : this.isSolidOpaqueStandardFormCube(block_access.getBlockMetadata(x, y, z));
      }
   }

   public boolean connectsWithFence() {
      return this.connects_with_fence;
   }

   public static boolean doesBlockConnectWithFence(IBlockAccess block_access, int x, int y, int z) {
      Block block = block_access.getBlock(x, y, z);
      return block != null && block.connectsWithFence();
   }

   public int getPartnerX(int x, int metadata) {
      if (this instanceof IBlockWithPartner) {
         return x + ((IBlockWithPartner)this).getPartnerOffsetX(metadata);
      } else {
         Minecraft.setErrorMessage("getPartnerX: " + this + " is not an instanceof IBlockWithPartner");
         return x;
      }
   }

   public int getPartnerY(int y, int metadata) {
      if (this instanceof IBlockWithPartner) {
         return y + ((IBlockWithPartner)this).getPartnerOffsetY(metadata);
      } else {
         Minecraft.setErrorMessage("getPartnerY: " + this + " is not an instanceof IBlockWithPartner");
         return y;
      }
   }

   public int getPartnerZ(int z, int metadata) {
      if (this instanceof IBlockWithPartner) {
         return z + ((IBlockWithPartner)this).getPartnerOffsetZ(metadata);
      } else {
         Minecraft.setErrorMessage("getPartnerZ: " + this + " is not an instanceof IBlockWithPartner");
         return z;
      }
   }

   public final boolean isPartnerPresent(World world, int x, int y, int z) {
      if (this instanceof IBlockWithPartner) {
         int metadata = world.getBlockMetadata(x, y, z);
         x = this.getPartnerX(x, metadata);
         y = this.getPartnerY(y, metadata);
         z = this.getPartnerZ(z, metadata);
         return ((IBlockWithPartner)this).isPartner(metadata, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
      } else {
         Minecraft.setErrorMessage("isPartnerPresent: " + this + " is not an instanceof IBlockWithPartner");
         return false;
      }
   }

   public final boolean doCollisionBoundsIntersectWith(World world, int x, int y, int z, AxisAlignedBB bb) {
      Object collision_bounds = this.getCollisionBounds(world, x, y, z, (Entity)null);
      if (collision_bounds == null) {
         return false;
      } else if (collision_bounds instanceof AxisAlignedBB) {
         return bb.intersectsWith((AxisAlignedBB)collision_bounds);
      } else {
         bb = bb.translateCopy((double)(-x), (double)(-y), (double)(-z));
         AxisAlignedBB[] multiple_bounds = (AxisAlignedBB[])((AxisAlignedBB[])collision_bounds);

         for(int i = 0; i < multiple_bounds.length; ++i) {
            if (multiple_bounds[i] != null && bb.intersectsWith(multiple_bounds[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public final boolean doRenderBoundsIntersectWith(World world, int x, int y, int z, AxisAlignedBB bb) {
      Object render_bounds = this.getRenderBounds(world, x, y, z, (Entity)null);
      if (render_bounds == null) {
         return false;
      } else if (render_bounds instanceof AxisAlignedBB) {
         return bb.intersectsWith((AxisAlignedBB)render_bounds);
      } else {
         bb = bb.translateCopy((double)(-x), (double)(-y), (double)(-z));
         AxisAlignedBB[] multiple_bounds = (AxisAlignedBB[])((AxisAlignedBB[])render_bounds);

         for(int i = 0; i < multiple_bounds.length; ++i) {
            if (multiple_bounds[i] != null && bb.intersectsWith(multiple_bounds[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public final boolean doCollisionBoundsContain(World world, int x, int y, int z, Vec3 point) {
      Object collision_bounds = this.getCollisionBounds(world, x, y, z, (Entity)null);
      if (collision_bounds == null) {
         return false;
      } else if (collision_bounds instanceof AxisAlignedBB) {
         return ((AxisAlignedBB)collision_bounds).isVecInside(point);
      } else {
         point = point.translateCopy((double)(-x), (double)(-y), (double)(-z));
         AxisAlignedBB[] multiple_bounds = (AxisAlignedBB[])((AxisAlignedBB[])collision_bounds);

         for(int i = 0; i < multiple_bounds.length; ++i) {
            if (multiple_bounds[i] != null && point.isInsideBB(multiple_bounds[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public final boolean isWater() {
      return this.blockMaterial == Material.water;
   }

   public final boolean useNeighborBrightness(World world, int x, int y, int z, EnumDirection direction) {
      return this.neverHidesAdjacentFaces() || !this.hidesAdjacentSide(world, x, y, z, (Block)null, direction.ordinal());
   }

   private final boolean useNeighborBrightness(boolean[] use_neighbor_brightness, int metadata, EnumDirection direction) {
      return lightOpacity[this.blockID] >= 15 && !this.isStandardFormCube(metadata) && !this.neverHidesAdjacentFaces() && !this.isFaceFlatAndSolid(metadata, direction.getFace());
   }

   public final boolean useNeighborBrightness(int metadata, EnumDirection direction) {
      return this.use_neighbor_brightness[metadata + direction.ordinal() * 16];
   }

   public boolean melt(World world, int x, int y, int z) {
      return false;
   }

   public String toString() {
      int index = Minecraft.getThreadIndex();
      return "" + this.getClass() + " [" + this.minX[index] + "," + this.minY[index] + "," + this.minZ[index] + "->" + this.maxX[index] + "," + this.maxY[index] + "," + this.maxZ[index] + "]";
   }

   public boolean onContactWithPepsin(World world, int x, int y, int z, EnumFace face, boolean show_particle_fx_for_gradual) {
      boolean on_server = !world.isRemote;
      int period = this.getDissolvePeriod(world, x, y, z, DamageSource.pepsin);
      if (period < 0) {
         return false;
      } else {
         if (on_server) {
            if (period == 0 || show_particle_fx_for_gradual) {
               world.blockFX(EnumBlockFX.steam, x, y, z);
            }

            if (period == 0) {
               return world.setBlockToAir(x, y, z);
            }
         }

         return period == 0;
      }
   }

   public boolean onContactWithAcid(World world, int x, int y, int z, EnumFace face, boolean show_particle_fx_for_gradual) {
      boolean on_server = !world.isRemote;
      if (this != grass && this != mycelium) {
         int period = this.getDissolvePeriod(world, x, y, z, DamageSource.acid);
         if (period >= 0) {
            if (on_server) {
               if (period == 0 || show_particle_fx_for_gradual) {
                  world.blockFX(period == 0 ? EnumBlockFX.smoke_and_steam : EnumBlockFX.steam, x, y, z);
               }

               if (period == 0) {
                  return world.setBlockToAir(x, y, z);
               }
            }

            return period == 0;
         }
      } else if (face == null || face.isTop()) {
         if (on_server) {
            world.setBlock(x, y, z, dirt.blockID);
            world.blockFX(EnumBlockFX.smoke_and_steam, x, y, z);
         }

         return true;
      }

      return false;
   }

   public int getDissolvePeriod(int metadata, DamageSource damage_source) {
      if (damage_source == DamageSource.pepsin) {
         if (this == tripWire) {
            return 400;
         }

         if (this.blockMaterial.isHarmedByPepsin()) {
            if (this.blockMaterial != Material.cloth && this.blockMaterial != Material.materialCarpet && this.blockMaterial != Material.cake) {
               return 0;
            }

            return 400;
         }
      } else if (damage_source == DamageSource.acid) {
         if (this instanceof BlockDoor || this instanceof BlockChest || this instanceof BlockBasePressurePlate || this instanceof BlockPane || this instanceof BlockOreStorage) {
            return this.blockMaterial.isHarmedByAcid() ? 400 : -1;
         }

         if (this instanceof BlockAnvil) {
            return ((BlockAnvil)this).getMetalType().isHarmedByAcid() ? 400 : -1;
         }

         if (this instanceof BlockRailBase || this instanceof BlockButtonWood || this instanceof BlockLever || this instanceof BlockSign || this instanceof BlockLadder || this instanceof BlockBed || this instanceof BlockPistonBase || this instanceof BlockPistonExtension || this instanceof BlockCactus || this instanceof BlockMelon || this instanceof BlockPumpkin || this instanceof BlockRedstoneRepeater || this instanceof BlockTrapDoor || this instanceof BlockEnchantmentTable || this == skull || this instanceof BlockComparator || this == daylightSensor || this instanceof BlockHopper || this == hay) {
            return 400;
         }

         if (this.blockMaterial.isHarmedByAcid()) {
            if (this instanceof BlockButtonStone || this instanceof BlockRedstoneWire) {
               return -1;
            }

            if (this instanceof BlockFence || this instanceof BlockFenceGate || this instanceof BlockCauldron || this == cocoaPlant) {
               return 400;
            }

            if (this instanceof BlockLilyPad || this instanceof BlockLeaves || this.blockMaterial == Material.cloth || this.blockMaterial == Material.cake) {
               return 0;
            }

            if (!this.isSolid(metadata)) {
               if (this == tripWireSource) {
                  return 400;
               }

               return 0;
            }
         }
      }

      return -1;
   }

   public int getDissolvePeriod(World world, int x, int y, int z, DamageSource damage_source) {
      boolean dissolve_period_depends_on_metadata = false;
      return this.getDissolvePeriod(dissolve_period_depends_on_metadata ? world.getBlockMetadata(x, y, z) : 0, damage_source);
   }

   public boolean isDissolvedInstantly(World world, int x, int y, int z, DamageSource damage_source) {
      return this.getDissolvePeriod(world, x, y, z, damage_source) == 0;
   }

   public static boolean isBedrockOrMantleOrCore(Block block) {
      return block == bedrock || block == mantleOrCore;
   }

   static {
      stone = (new BlockStone(1)).setHardness(BlockHardness.stone).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stone").setTextureName("stone");
      grass = (BlockGrass)(new BlockGrass(2)).setHardness(BlockHardness.grass).setStepSound(soundGrassFootstep).setUnlocalizedName("grass").setTextureName("grass");
      dirt = (new BlockDirt(3)).setHardness(BlockHardness.dirt).setStepSound(soundGravelFootstep).setUnlocalizedName("dirt").setTextureName("dirt");
      cobblestone = (new Block(4, Material.stone, new BlockConstants())).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebrick").setCreativeTab(CreativeTabs.tabBlock).setTextureName("cobblestone");
      planks = (BlockWood)(new BlockWood(5)).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("wood").setTextureName("planks");
      sapling = (new BlockSapling(6)).setHardness(0.02F).setStepSound(soundGrassFootstep).setUnlocalizedName("sapling").setTextureName("sapling");
      bedrock = (new BlockBedrock(7, Material.stone, (new BlockConstants()).setAlwaysImmutable())).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock).setTextureName("bedrock");
      waterMoving = (BlockFluid)(new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats().setTextureName("water_flow");
      waterStill = (new BlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats().setTextureName("water_still");
      lavaMoving = (BlockFluid)(new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(0.9F).setUnlocalizedName("lava").disableStats().setTextureName("lava_flow");
      lavaStill = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(0.9F).setUnlocalizedName("lava").disableStats().setTextureName("lava_still");
      sand = (BlockSand)(new BlockSand(12)).setHardness(0.4F).setCushioning(0.4F).setStepSound(soundSandFootstep).setUnlocalizedName("sand").setTextureName("sand");
      gravel = (BlockGravel)(new BlockGravel(13)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("gravel").setTextureName("gravel");
      oreGold = (new BlockGoldOre(14, Material.gold, 2)).setHardness(2.4F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreGold").setTextureName("gold_ore");
      oreIron = (new BlockOre(15, Material.iron, 2)).setHardness(3.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreIron").setTextureName("iron_ore");
      oreCoal = (new BlockOre(16, Material.coal, 2)).setHardness(1.2F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreCoal").setTextureName("coal_ore");
      wood = (BlockLog)(new BlockLog(17)).setStepSound(soundWoodFootstep).setUnlocalizedName("log").setTextureName("log");
      leaves = (BlockLeaves)(new BlockLeaves(18)).setHardness(0.2F).setLightOpacity(1).setStepSound(soundGrassFootstep).setUnlocalizedName("leaves").setTextureName("leaves");
      sponge = (new BlockSponge(19)).setHardness(0.6F).setCushioning(1.0F).setStepSound(soundClothFootstep).setUnlocalizedName("sponge").setTextureName("sponge");
      glass = (new BlockGlass(20, Material.glass, false)).setHardness(2.0F).setStepSound(soundGlassFootstep).setUnlocalizedName("glass").setTextureName("glass");
      oreLapis = (new BlockOre(21, Material.lapis_lazuli, 2)).setHardness(3.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreLapis").setTextureName("lapis_ore");
      blockLapis = (new Block(22, Material.stone, new BlockConstants())).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("blockLapis").setCreativeTab(CreativeTabs.tabBlock).setTextureName("lapis_block");
      dispenser = (BlockDispenser)(new BlockDispenser(23)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("dispenser").setTextureName("dispenser");
      sandStone = (new BlockSandStone(24)).setStepSound(soundStoneFootstep).setHardness(0.8F).setUnlocalizedName("sandStone").setTextureName("sandstone");
      music = (new BlockNote(25)).setHardness(0.8F).setUnlocalizedName("musicBlock").setTextureName("noteblock");
      bed = (BlockBed)(new BlockBed(26)).setHardness(0.2F).setCushioning(0.6F).setUnlocalizedName("bed").disableStats().setTextureName("bed");
      railPowered = (new BlockRailPowered(27)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("goldenRail").setTextureName("rail_golden");
      railDetector = (new BlockDetectorRail(28)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("detectorRail").setTextureName("rail_detector");
      pistonStickyBase = (BlockPistonBase)(new BlockPistonBase(29, true)).setUnlocalizedName("pistonStickyBase");
      web = (new BlockWeb(30)).setLightOpacity(1).setHardness(0.1F).setUnlocalizedName("web").setTextureName("web");
      tallGrass = (BlockTallGrass)(new BlockTallGrass(31)).setHardness(0.02F).setCushioning(0.2F).setStepSound(soundGrassFootstep).setUnlocalizedName("tallgrass");
      deadBush = (BlockDeadBush)(new BlockDeadBush(32)).setHardness(0.02F).setStepSound(soundGrassFootstep).setUnlocalizedName("deadbush").setTextureName("deadbush");
      pistonBase = (BlockPistonBase)(new BlockPistonBase(33, false)).setUnlocalizedName("pistonBase");
      pistonExtension = new BlockPistonExtension(34);
      cloth = (new BlockColored(35, Material.cloth, (new BlockConstants()).setNeverConnectsWithFence())).setMaxStackSize(8).setHardness(0.8F).setCushioning(0.8F).setStepSound(soundClothFootstep).setUnlocalizedName("cloth").setTextureName("wool_colored");
      pistonMoving = new BlockPistonMoving(36);
      plantYellow = (BlockFlower)(new BlockFlower(37)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("dandelion").setTextureName("flowers/dandelion");
      plantRed = (BlockFlowerMulti)(new BlockFlowerMulti(38)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("flower").setTextureName("flowers/");
      mushroomBrown = (BlockMushroom)(new BlockMushroom(39)).setHardness(0.0F).setStepSound(soundGrassFootstep).setLightValue(0.15F).setUnlocalizedName("mushroom").setTextureName("mushroom_brown");
      mushroomRed = (BlockMushroom)(new BlockMushroom(40)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("mushroom").setTextureName("mushroom_red");
      blockGold = (new BlockOreStorage(41, Material.gold)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockGold").setTextureName("gold_block");
      blockIron = (new BlockOreStorage(42, Material.iron)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockIron").setTextureName("iron_block");
      stoneSingleSlab = (BlockSlabGroup1)(new BlockSlabGroup1(44, Material.stone)).setStepSound(soundStoneFootstep);
      stoneDoubleSlab = (BlockDoubleSlab)(new BlockDoubleSlab(43, stoneSingleSlab)).setStepSound(soundStoneFootstep);
      brick = (new Block(45, Material.stone, new BlockConstants())).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabBlock).setTextureName("brick");
      tnt = (BlockTNT)(new BlockTNT(46)).setHardness(1.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("tnt").setTextureName("tnt");
      bookShelf = (new BlockBookshelf(47)).setHardness(1.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("bookshelf").setTextureName("bookshelf");
      cobblestoneMossy = (new Block(48, Material.stone, new BlockConstants())).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneMoss").setCreativeTab(CreativeTabs.tabBlock).setTextureName("cobblestone_mossy");
      obsidian = (new BlockObsidian(49)).setHardness(2.4F).setResistance(20.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("obsidian").setTextureName("obsidian");
      torchWood = (new BlockTorch(50)).setHardness(0.0F).setLightValue(0.9375F).setStepSound(soundWoodFootstep).setUnlocalizedName("torch").setTextureName("torch_on");
      fire = (BlockFire)(new BlockFire(51)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fire").disableStats().setTextureName("fire");
      mobSpawner = (new BlockMobSpawner(52)).setHardness(3.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("mobSpawner").disableStats().setTextureName("mob_spawner");
      stairsWoodOak = (new BlockStairs(53, planks, 0)).setUnlocalizedName("stairsWood");
      chest = (BlockChest)(new BlockChest(54, EnumChestType.normal, Material.wood)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("chest");
      redstoneWire = (BlockRedstoneWire)(new BlockRedstoneWire(55)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("redstoneDust").disableStats().setTextureName("redstone_dust");
      oreDiamond = (new BlockOre(56, Material.diamond, 4)).setHardness(3.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreDiamond").setTextureName("diamond_ore");
      blockDiamond = (new BlockOreStorage(57, Material.diamond)).setMaxStackSize(4).setStepSound(soundMetalFootstep).setUnlocalizedName("blockDiamond").setTextureName("diamond_block");
      workbench = (new BlockWorkbench(58)).setStepSound(soundWoodFootstep).setUnlocalizedName("workbench");
      crops = (new BlockCrops(59, 8)).setUnlocalizedName("crops").setTextureName("wheat");
      tilledField = (new BlockFarmland(60)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("farmland").setTextureName("farmland");
      furnaceIdle = (new BlockFurnaceCobblestone(61, false)).setHardness(2.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnace").setCreativeTab(CreativeTabs.tabDecorations);
      furnaceBurning = (new BlockFurnaceCobblestone(62, true)).setHardness(2.0F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnace");
      signPost = (new BlockSign(63, TileEntitySign.class, true)).setStepSound(soundWoodFootstep).setUnlocalizedName("sign").disableStats();
      doorWood = (new BlockDoor(64, Material.wood)).setStepSound(soundWoodFootstep).setUnlocalizedName("doorWood").disableStats().setTextureName("door_wood");
      ladder = (new BlockLadder(65)).setStepSound(soundLadderFootstep).setUnlocalizedName("ladder").setTextureName("ladder");
      rail = (new BlockRail(66)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("rail").setTextureName("rail_normal");
      stairsCobblestone = (new BlockStairs(67, cobblestone, 0)).setUnlocalizedName("stairsStone");
      signWall = (new BlockSign(68, TileEntitySign.class, false)).setStepSound(soundWoodFootstep).setUnlocalizedName("sign").disableStats();
      lever = (new BlockLever(69)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("lever").setTextureName("lever");
      pressurePlateStone = (new BlockPressurePlate(70, "stone", Material.stone, EnumMobType.mobs)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("pressurePlate");
      doorIron = (new BlockDoor(71, Material.iron)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorIron").disableStats().setTextureName("door_iron");
      pressurePlatePlanks = (new BlockPressurePlate(72, "planks_oak", Material.wood, EnumMobType.everything)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("pressurePlate");
      oreRedstone = (new BlockRedstoneOre(73, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setCreativeTab(CreativeTabs.tabBlock).setTextureName("redstone_ore");
      oreRedstoneGlowing = (new BlockRedstoneOre(74, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setTextureName("redstone_ore");
      torchRedstoneIdle = (new BlockRedstoneTorch(75, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate").setTextureName("redstone_torch_off");
      torchRedstoneActive = (new BlockRedstoneTorch(76, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("redstone_torch_on");
      stoneButton = (new BlockButtonStone(77)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("button");
      snow = (new BlockSnow(78)).setMaxStackSize(32).setHardness(0.05F).setStepSound(soundSnowFootstep).setUnlocalizedName("snow").setLightOpacity(0).setTextureName("snow");
      ice = (new BlockIce(79)).setHardness(1.0F).setLightOpacity(3).setStepSound(soundGlassFootstep).setUnlocalizedName("ice").setTextureName("ice");
      blockSnow = (new BlockSnowBlock(80)).setStepSound(soundSnowFootstep).setUnlocalizedName("snow").setTextureName("snow");
      cactus = (new BlockCactus(81)).setHardness(0.4F).setStepSound(soundClothFootstep).setUnlocalizedName("cactus").setTextureName("cactus");
      blockClay = (new BlockClay(82)).setHardness(0.8F).setStepSound(soundGravelFootstep).setUnlocalizedName("clay").setTextureName("clay");
      reed = (new BlockReed(83)).setHardness(0.08F).setStepSound(soundGrassFootstep).setUnlocalizedName("reeds").disableStats().setTextureName("reeds");
      jukebox = (BlockJukeBox)(new BlockJukeBox(84)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("jukebox").setTextureName("jukebox");
      fence = (new BlockFence(85, "planks_oak", Material.wood)).setHardness(0.4F).setResistance(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fence");
      pumpkin = (new BlockPumpkin(86, false)).setHardness(0.6F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkin").setTextureName("pumpkin");
      netherrack = (new BlockNetherrack(87)).setHardness(1.6F).setStepSound(soundStoneFootstep).setUnlocalizedName("hellrock").setTextureName("netherrack");
      slowSand = (new BlockSoulSand(88)).setHardness(0.5F).setStepSound(soundSandFootstep).setUnlocalizedName("hellsand").setTextureName("soul_sand");
      glowStone = (new BlockGlowStone(89, Material.glass)).setHardness(0.3F).setStepSound(soundGlassFootstep).setLightValue(1.0F).setUnlocalizedName("lightgem").setTextureName("glowstone");
      portal = (BlockPortal)(new BlockPortal(90)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(0.75F).setUnlocalizedName("portal").setTextureName("portal");
      pumpkinLantern = (new BlockPumpkin(91, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setLightValue(0.8F).setUnlocalizedName("litpumpkin").setTextureName("pumpkin");
      cake = (new BlockCake(92)).setHardness(0.5F).setStepSound(soundClothFootstep).setUnlocalizedName("cake").disableStats().setTextureName("cake");
      redstoneRepeaterIdle = (BlockRedstoneRepeater)(new BlockRedstoneRepeater(93, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats().setTextureName("repeater_off");
      redstoneRepeaterActive = (BlockRedstoneRepeater)(new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats().setTextureName("repeater_on");
      trapdoor = (new BlockTrapDoor(96, Material.wood)).setStepSound(soundWoodFootstep).setUnlocalizedName("trapdoor").disableStats().setTextureName("trapdoor");
      silverfish = (BlockSilverfish)(new BlockSilverfish(97)).setHardness(0.75F).setUnlocalizedName("monsterStoneEgg");
      stoneBrick = (new BlockStoneBrick(98)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebricksmooth").setTextureName("stonebrick");
      mushroomCapBrown = (new BlockMushroomCap(99, Material.wood, 0)).setHardness(0.2F).setStepSound(soundClothFootstep).setUnlocalizedName("mushroom").setTextureName("mushroom_block");
      mushroomCapRed = (new BlockMushroomCap(100, Material.wood, 1)).setHardness(0.2F).setStepSound(soundClothFootstep).setUnlocalizedName("mushroom").setTextureName("mushroom_block");
      fenceIron = (new BlockPane(101, "iron_bars", "iron_bars", Material.iron, true)).setHardness(5.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceIron");
      thinGlass = (new BlockPane(102, "glass", "glass_pane_top", Material.glass, false)).setHardness(0.1F).setStepSound(soundGlassFootstep).setUnlocalizedName("thinGlass");
      melon = (new BlockMelon(103)).setHardness(0.6F).setStepSound(soundWoodFootstep).setUnlocalizedName("melon").setTextureName("melon");
      pumpkinStem = (new BlockStem(104, pumpkin)).setHardness(0.02F).setStepSound(soundGrassFootstep).setUnlocalizedName("pumpkinStem").setTextureName("pumpkin_stem");
      melonStem = (new BlockStem(105, melon)).setHardness(0.02F).setStepSound(soundGrassFootstep).setUnlocalizedName("pumpkinStem").setTextureName("melon_stem");
      vine = (BlockVine)(new BlockVine(106)).setHardness(0.2F).setStepSound(soundGrassFootstep).setUnlocalizedName("vine").setTextureName("vine");
      fenceGate = (new BlockFenceGate(107)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fenceGate");
      stairsBrick = (new BlockStairs(108, brick, 0)).setUnlocalizedName("stairsBrick");
      stairsStoneBrick = (new BlockStairs(109, stoneBrick, 0)).setUnlocalizedName("stairsStoneBrickSmooth");
      mycelium = (BlockMycelium)(new BlockMycelium(110)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("mycel").setTextureName("mycelium");
      waterlily = (new BlockLilyPad(111)).setHardness(0.02F).setStepSound(soundGrassFootstep).setUnlocalizedName("waterlily").setTextureName("waterlily");
      netherBrick = (new Block(112, Material.netherrack, new BlockConstants())).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherBrick").setCreativeTab(CreativeTabs.tabBlock).setTextureName("nether_brick");
      netherFence = (new BlockFence(113, "nether_brick", Material.netherrack)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherFence");
      stairsNetherBrick = (new BlockStairs(114, netherBrick, 0)).setUnlocalizedName("stairsNetherBrick");
      netherStalk = (new BlockNetherStalk(115)).setUnlocalizedName("netherStalk").setTextureName("nether_wart");
      enchantmentTable = (new BlockEnchantmentTable(116, Material.diamond)).setHardness(2.4F).setResistance(20.0F).setUnlocalizedName("enchantmentTable").setTextureName("enchanting_table");
      brewingStand = (new BlockBrewingStand(117)).setHardness(0.5F).setLightValue(0.125F).setUnlocalizedName("brewingStand").setTextureName("brewing_stand");
      cauldron = (BlockCauldron)(new BlockCauldron(118)).setHardness(2.0F).setUnlocalizedName("cauldron").setTextureName("cauldron");
      endPortal = (new BlockEndPortal(119, Material.portal)).setHardness(-1.0F).setResistance(6000000.0F);
      endPortalFrame = (new BlockEndPortalFrame(120)).setStepSound(soundGlassFootstep).setLightValue(0.125F).setHardness(-1.0F).setUnlocalizedName("endPortalFrame").setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabDecorations).setTextureName("endframe");
      whiteStone = (new Block(121, Material.stone, new BlockConstants())).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("whiteStone").setCreativeTab(CreativeTabs.tabBlock).setTextureName("end_stone");
      dragonEgg = (new BlockDragonEgg(122)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setLightValue(0.125F).setUnlocalizedName("dragonEgg").setTextureName("dragon_egg");
      redstoneLampIdle = (new BlockRedstoneLight(123, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("redstone_lamp_off");
      redstoneLampActive = (new BlockRedstoneLight(124, true)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight").setTextureName("redstone_lamp_on");
      woodSingleSlab = (BlockSlabGroup2)(new BlockSlabGroup2(126, Material.wood)).setResistance(5.0F).setStepSound(soundWoodFootstep);
      woodDoubleSlab = (BlockDoubleSlab)(new BlockDoubleSlab(125, woodSingleSlab)).setResistance(5.0F).setStepSound(soundWoodFootstep);
      cocoaPlant = (new BlockCocoa(127)).setHardness(0.2F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("cocoa").setTextureName("cocoa");
      stairsSandStone = (new BlockStairs(128, sandStone, 0)).setUnlocalizedName("stairsSandStone");
      oreEmerald = (new BlockOre(129, Material.emerald, 3)).setHardness(3.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreEmerald").setTextureName("emerald_ore");
      enderChest = (new BlockEnderChest(130)).setHardness(22.5F).setResistance(1000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("enderChest").setLightValue(0.5F);
      tripWireSource = (BlockTripWireSource)(new BlockTripWireSource(131)).setUnlocalizedName("tripWireSource").setTextureName("trip_wire_source");
      tripWire = (new BlockTripWire(132)).setUnlocalizedName("tripWire").setTextureName("trip_wire");
      blockEmerald = (new BlockOreStorage(133, Material.emerald)).setMaxStackSize(4).setStepSound(soundMetalFootstep).setUnlocalizedName("blockEmerald").setTextureName("emerald_block");
      stairsWoodSpruce = (new BlockStairs(134, planks, 1)).setUnlocalizedName("stairsWoodSpruce");
      stairsWoodBirch = (new BlockStairs(135, planks, 2)).setUnlocalizedName("stairsWoodBirch");
      stairsWoodJungle = (new BlockStairs(136, planks, 3)).setUnlocalizedName("stairsWoodJungle");
      commandBlock = (new BlockCommandBlock(137)).setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("commandBlock").setTextureName("command_block");
      beacon = (BlockBeacon)(new BlockBeacon(138)).setUnlocalizedName("beacon").setLightValue(1.0F).setTextureName("beacon");
      cobblestoneWall = (new BlockWall(139, cobblestone)).setUnlocalizedName("cobbleWall");
      flowerPot = (new BlockFlowerPot(140)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("flowerPot").setTextureName("flower_pot");
      carrot = (new BlockCarrot(141)).setUnlocalizedName("carrots").setTextureName("carrots");
      potato = (new BlockPotato(142)).setUnlocalizedName("potatoes").setTextureName("potatoes");
      woodenButton = (new BlockButtonWood(143)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("button");
      skull = (new BlockSkull(144)).setHardness(1.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("skull").setTextureName("skull");
      anvil = (BlockAnvil)(new BlockAnvil(145, Material.iron)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilIron");
      chestTrapped = (new BlockChest(146, EnumChestType.trapped, Material.wood)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("chestTrap");
      pressurePlateGold = (new BlockPressurePlateWeighted(147, "gold_block", Material.gold, 64)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("weightedPlate_light");
      pressurePlateIron = (new BlockPressurePlateWeighted(148, "iron_block", Material.iron, 640)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("weightedPlate_heavy");
      redstoneComparatorIdle = (BlockComparator)(new BlockComparator(149, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats().setTextureName("comparator_off");
      redstoneComparatorActive = (BlockComparator)(new BlockComparator(150, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats().setTextureName("comparator_on");
      daylightSensor = (BlockDaylightDetector)(new BlockDaylightDetector(151)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("daylightDetector").setTextureName("daylight_detector");
      blockRedstone = (new BlockPoweredOre(152)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockRedstone").setTextureName("redstone_block");
      oreNetherQuartz = (new BlockOre(153, Material.quartz, 2)).setHardness(3.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherquartz").setTextureName("quartz_ore");
      hopperBlock = (BlockHopper)(new BlockHopper(154)).setHardness(3.0F).setResistance(8.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("hopper").setTextureName("hopper");
      blockNetherQuartz = (new BlockQuartz(155)).setStepSound(soundStoneFootstep).setHardness(0.8F).setUnlocalizedName("quartzBlock").setTextureName("quartz_block");
      stairsNetherQuartz = (new BlockStairs(156, blockNetherQuartz, 0)).setUnlocalizedName("stairsQuartz");
      railActivator = (new BlockRailPowered(157)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("activatorRail").setTextureName("rail_activator");
      dropper = (new BlockDropper(158)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("dropper").setTextureName("dropper");
      hay = (new BlockHay(170)).setHardness(0.5F).setStepSound(soundGrassFootstep).setUnlocalizedName("hayBlock").setCreativeTab(CreativeTabs.tabBlock).setTextureName("hay_block");
      carpet = (new BlockCarpet(171)).setHardness(0.1F).setStepSound(soundClothFootstep).setUnlocalizedName("woolCarpet").setLightOpacity(0);
      coalBlock = (new Block(173, Material.coal, new BlockConstants())).setHardness(1.2F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("blockCoal").setCreativeTab(CreativeTabs.tabBlock).setTextureName("coal_block");
      stainedClay = (new BlockColored(159, Material.hardened_clay, new BlockConstants())).setHardness(1.8F).setMinHarvestLevel(1).setStepSound(soundStoneFootstep).setUnlocalizedName("clayHardenedStained").setTextureName("hardened_clay_stained");
      hardenedClay = (new Block(172, Material.hardened_clay, new BlockConstants())).setHardness(1.8F).setMinHarvestLevel(1).setStepSound(soundStoneFootstep).setUnlocalizedName("clayHardened").setCreativeTab(CreativeTabs.tabBlock).setTextureName("hardened_clay");
      fenceAncientMetal = (new BlockPane(199, "ancient_metal_bars", "ancient_metal_bars", Material.ancient_metal, true)).setHardnessRelativeToWood(0.4F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceAncientMetal");
      oreCopper = (new BlockOre(200, Material.copper, 2)).setHardness(2.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreCopper").setTextureName("copper_ore");
      oreSilver = (new BlockOre(201, Material.silver, 2)).setHardness(2.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreSilver").setTextureName("silver_ore");
      oreMithril = (new BlockOre(202, Material.mithril, 3)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreMithril").setTextureName("mithril_ore");
      oreAdamantium = (new BlockOre(203, Material.adamantium, 4)).setHardness(4.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreAdamantium").setTextureName("adamantium_ore");
      blockCopper = (new BlockOreStorage(204, Material.copper)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockCopper").setTextureName("copper_block");
      blockSilver = (new BlockOreStorage(205, Material.silver)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockSilver").setTextureName("silver_block");
      blockMithril = (new BlockOreStorage(206, Material.mithril)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockMithril").setTextureName("mithril_block");
      blockAdamantium = (new BlockOreStorage(207, Material.adamantium)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockAdamantium").setTextureName("adamantium_block");
      doorCopper = (new BlockDoor(208, Material.copper)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorCopper").disableStats().setTextureName("door_copper");
      doorSilver = (new BlockDoor(209, Material.silver)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorSilver").disableStats().setTextureName("door_silver");
      doorGold = (new BlockDoor(210, Material.gold)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorGold").disableStats().setTextureName("door_gold");
      doorMithril = (new BlockDoor(211, Material.mithril)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorMithril").disableStats().setTextureName("door_mithril");
      doorAdamantium = (new BlockDoor(212, Material.adamantium)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorAdamantium").disableStats().setTextureName("door_adamantium");
      fenceCopper = (new BlockPane(213, "copper_bars", "copper_bars", Material.copper, true)).setHardnessRelativeToWood(0.4F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceCopper");
      fenceSilver = (new BlockPane(214, "silver_bars", "silver_bars", Material.silver, true)).setHardnessRelativeToWood(0.4F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceSilver");
      fenceGold = (new BlockPane(215, "gold_bars", "gold_bars", Material.gold, true)).setHardnessRelativeToWood(0.4F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceGold");
      fenceMithril = (new BlockPane(216, "mithril_bars", "mithril_bars", Material.mithril, true)).setHardnessRelativeToWood(0.4F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceMithril");
      fenceAdamantium = (new BlockPane(217, "adamantium_bars", "adamantium_bars", Material.adamantium, true)).setHardnessRelativeToWood(0.4F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceAdamantium");
      furnaceClayIdle = (new BlockFurnaceClay(218, false)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnaceClay").setCreativeTab(CreativeTabs.tabDecorations);
      furnaceClayBurning = (new BlockFurnaceClay(219, true)).setHardness(0.5F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnaceClay");
      furnaceSandstoneIdle = (new BlockFurnaceSandstone(220, false)).setHardness(1.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnaceSandstone").setCreativeTab(CreativeTabs.tabDecorations);
      furnaceSandstoneBurning = (new BlockFurnaceSandstone(221, true)).setHardness(1.0F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnaceSandstone");
      furnaceObsidianIdle = (new BlockFurnaceObsidian(222, false)).setHardness(4.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnaceObsidian").setCreativeTab(CreativeTabs.tabDecorations);
      furnaceObsidianBurning = (new BlockFurnaceObsidian(223, true)).setHardness(4.0F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnaceObsidian");
      furnaceNetherrackIdle = (new BlockFurnaceNetherrack(224, false)).setHardness(8.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnaceNetherrack").setCreativeTab(CreativeTabs.tabDecorations);
      furnaceNetherrackBurning = (new BlockFurnaceNetherrack(225, true)).setHardness(8.0F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnaceNetherrack");
      obsidianSingleSlab = (BlockSlabGroup3)(new BlockSlabGroup3(227, Material.obsidian)).setStepSound(soundStoneFootstep);
      obsidianDoubleSlab = (BlockDoubleSlab)(new BlockDoubleSlab(226, obsidianSingleSlab)).setStepSound(soundStoneFootstep);
      stairsObsidian = (new BlockStairs(228, obsidian, 0)).setUnlocalizedName("stairsObsidian");
      anvilCopper = (BlockAnvil)(new BlockAnvil(229, Material.copper)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilCopper");
      anvilSilver = (BlockAnvil)(new BlockAnvil(230, Material.silver)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilSilver");
      anvilGold = (BlockAnvil)(new BlockAnvil(231, Material.gold)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilGold");
      anvilMithril = (BlockAnvil)(new BlockAnvil(232, Material.mithril)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilMithril");
      anvilAdamantium = (BlockAnvil)(new BlockAnvil(233, Material.adamantium)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilAdamantium");
      onions = (new BlockOnion(234)).setUnlocalizedName("onions").setTextureName("onions");
      cropsDead = (new BlockCropsDead(235, 7)).setUnlocalizedName("crops").setTextureName("wheat");
      carrotDead = (new BlockCarrotDead(236)).setUnlocalizedName("carrots").setTextureName("carrots");
      potatoDead = (new BlockPotatoDead(237)).setUnlocalizedName("potatoes").setTextureName("potatoes");
      onionsDead = (new BlockOnionDead(238)).setUnlocalizedName("onions").setTextureName("onions");
      chestCopper = (BlockStrongbox)(new BlockStrongbox(239, Material.copper)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestCopper");
      chestSilver = (BlockStrongbox)(new BlockStrongbox(240, Material.silver)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestSilver");
      chestGold = (BlockStrongbox)(new BlockStrongbox(241, Material.gold)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestGold");
      chestIron = (BlockStrongbox)(new BlockStrongbox(242, Material.iron)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestIron");
      chestMithril = (BlockStrongbox)(new BlockStrongbox(243, Material.mithril)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestMithril");
      chestAdamantium = (BlockStrongbox)(new BlockStrongbox(244, Material.adamantium)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestAdamantium");
      enchantmentTableEmerald = (new BlockEnchantmentTable(245, Material.emerald)).setHardness(2.4F).setResistance(20.0F).setUnlocalizedName("enchantmentTable").setTextureName("emerald_enchanting_table");
      spark = (BlockSpark)(new BlockSpark(246)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("spark").disableStats().setTextureName("invisible");
      runestoneMithril = (BlockRunestone)(new BlockRunestone(247, Material.mithril)).setHardness(2.4F).setResistance(20.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("runestone").setTextureName("obsidian");
      flowerPotMulti = (new BlockFlowerPotMulti(248)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("flowerPot").setTextureName("flower_pot");
      bush = (BlockBush)(new BlockBush(249)).setHardness(0.05F).setStepSound(soundGrassFootstep).setUnlocalizedName("bush").setTextureName("bushes");
      furnaceHardenedClayIdle = (new BlockFurnaceHardenedClay(250, false)).setHardness(1.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnaceHardenedClay").setCreativeTab(CreativeTabs.tabDecorations);
      furnaceHardenedClayBurning = (new BlockFurnaceHardenedClay(251, true)).setHardness(1.0F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnaceHardenedClay");
      blockAncientMetal = (new BlockOreStorage(252, Material.ancient_metal)).setStepSound(soundMetalFootstep).setUnlocalizedName("blockAncientMetal").setTextureName("ancient_metal_block");
      doorAncientMetal = (new BlockDoor(253, Material.ancient_metal)).setStepSound(soundMetalFootstep).setUnlocalizedName("doorAncientMetal").disableStats().setTextureName("door_ancient_metal");
      anvilAncientMetal = (BlockAnvil)(new BlockAnvil(254, Material.ancient_metal)).setStepSound(soundAnvilFootstep).setUnlocalizedName("anvilAncientMetal");
      chestAncientMetal = (BlockStrongbox)(new BlockStrongbox(255, Material.ancient_metal)).setStepSound(soundMetalFootstep).setUnlocalizedName("chestAncientMetal");
      runestoneAdamantium = (BlockRunestone)(new BlockRunestone(198, Material.adamantium)).setHardness(2.4F).setResistance(20.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("runestone").setTextureName("obsidian");
      mantleOrCore = (BlockMantleOrCore)(new BlockMantleOrCore(95, Material.stone, (new BlockConstants()).setAlwaysImmutable())).setBlockUnbreakable().setResistance(6000000.0F).setLightValue(0.9F).setStepSound(soundStoneFootstep).disableStats().setCreativeTab(CreativeTabs.tabBlock).setUnlocalizedName("mantle").setTextureName("mantle");
      standard_form_bounding_box = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

      int var0;
      Block block;
      for(var0 = 0; var0 < 256; ++var0) {
         block = getBlock(var0);
         if (block != null) {
            block.initializeBlock();
         }
      }

      Item.itemsList[cloth.blockID] = (new ItemCloth(cloth)).setUnlocalizedName("cloth");
      Item.itemsList[stainedClay.blockID] = (new ItemCloth(stainedClay)).setUnlocalizedName("clayHardenedStained");
      Item.itemsList[carpet.blockID] = (new ItemCloth(carpet)).setUnlocalizedName("woolCarpet");
      Item.itemsList[wood.blockID] = (new ItemMultiTextureTile(wood, wood.getNames())).setUnlocalizedName("log");
      Item.itemsList[planks.blockID] = (new ItemMultiTextureTile(planks, planks.getNames())).setUnlocalizedName("wood");
      Item.itemsList[silverfish.blockID] = (new ItemMultiTextureTile(silverfish, BlockSilverfish.silverfishStoneTypes)).setUnlocalizedName("monsterStoneEgg");
      Item.itemsList[stoneBrick.blockID] = (new ItemMultiTextureTile(stoneBrick, BlockStoneBrick.STONE_BRICK_TYPES)).setUnlocalizedName("stonebricksmooth");
      Item.itemsList[sandStone.blockID] = (new ItemMultiTextureTile(sandStone, BlockSandStone.SAND_STONE_TYPES)).setUnlocalizedName("sandStone");
      Item.itemsList[blockNetherQuartz.blockID] = (new ItemMultiTextureTile(blockNetherQuartz, BlockQuartz.quartzBlockTypes)).setUnlocalizedName("quartzBlock");
      Item.itemsList[stoneSingleSlab.blockID] = (new ItemSlab(stoneSingleSlab, stoneDoubleSlab, false)).setUnlocalizedName("stoneSlab");
      Item.itemsList[stoneDoubleSlab.blockID] = (new ItemSlab(stoneSingleSlab, stoneDoubleSlab, true)).setUnlocalizedName("stoneSlab");
      Item.itemsList[woodSingleSlab.blockID] = (new ItemSlab(woodSingleSlab, woodDoubleSlab, false)).setUnlocalizedName("woodSlab");
      Item.itemsList[woodDoubleSlab.blockID] = (new ItemSlab(woodSingleSlab, woodDoubleSlab, true)).setUnlocalizedName("woodSlab");
      Item.itemsList[sapling.blockID] = (new ItemMultiTextureTile(sapling, BlockSapling.WOOD_TYPES)).setUnlocalizedName("sapling");
      Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves)).setUnlocalizedName("leaves");
      Item.itemsList[vine.blockID] = new ItemColored(vine);
      Item.itemsList[tallGrass.blockID] = (new ItemColored(tallGrass)).setBlockNames(new String[]{"shrub", "grass", "fern"});
      Item.itemsList[snow.blockID] = new ItemSnow(snow);
      Item.itemsList[blockSnow.blockID] = new ItemSnowBlock(blockSnow);
      Item.itemsList[waterlily.blockID] = new ItemLilyPad(waterlily);
      Item.itemsList[pistonBase.blockID] = new ItemPiston(pistonBase);
      Item.itemsList[pistonStickyBase.blockID] = new ItemPiston(pistonStickyBase);
      Item.itemsList[cobblestoneWall.blockID] = (new ItemMultiTextureTile(cobblestoneWall, BlockWall.types)).setUnlocalizedName("cobbleWall");
      Item.itemsList[anvil.blockID] = (new ItemAnvilBlock(anvil)).setUnlocalizedName("anvil");
      Item.itemsList[gravel.blockID] = new ItemGravel(gravel, gravel.getNames());
      Item.itemsList[deadBush.blockID] = new ItemDeadBush(deadBush, BlockDeadBush.types);
      Item.itemsList[plantRed.blockID] = (new ItemMultiTextureTile(plantRed, BlockFlowerMulti.types)).setUnlocalizedName("flower");
      Item.itemsList[obsidianSingleSlab.blockID] = (new ItemSlab(obsidianSingleSlab, obsidianDoubleSlab, false)).setUnlocalizedName("obsidianSlab");
      Item.itemsList[obsidianDoubleSlab.blockID] = (new ItemSlab(obsidianSingleSlab, obsidianDoubleSlab, true)).setUnlocalizedName("obsidianSlab");
      Item.itemsList[anvilCopper.blockID] = (new ItemAnvilBlock(anvilCopper)).setUnlocalizedName("anvilCopper");
      Item.itemsList[anvilSilver.blockID] = (new ItemAnvilBlock(anvilSilver)).setUnlocalizedName("anvilSilver");
      Item.itemsList[anvilGold.blockID] = (new ItemAnvilBlock(anvilGold)).setUnlocalizedName("anvilGold");
      Item.itemsList[anvilMithril.blockID] = (new ItemAnvilBlock(anvilMithril)).setUnlocalizedName("anvilMithril");
      Item.itemsList[anvilAdamantium.blockID] = (new ItemAnvilBlock(anvilAdamantium)).setUnlocalizedName("anvilAdamantium");
      Item.itemsList[anvilAncientMetal.blockID] = (new ItemAnvilBlock(anvilAncientMetal)).setUnlocalizedName("anvilAncientMetal");
      Item.itemsList[runestoneMithril.blockID] = (new ItemRunestone(runestoneMithril)).setUnlocalizedName("runestone");
      Item.itemsList[runestoneAdamantium.blockID] = (new ItemRunestone(runestoneAdamantium)).setUnlocalizedName("runestone");
      Item.itemsList[bush.blockID] = (new ItemMultiTextureTile(bush, BlockBush.types)).setUnlocalizedName("bush");
      Item.itemsList[mantleOrCore.blockID] = new ItemMantleOrCore(mantleOrCore, mantleOrCore.getNames());

      for(var0 = 0; var0 < 256; ++var0) {
         block = getBlock(var0);
         if (block != null) {
            if (Item.itemsList[var0] == null) {
               Item.itemsList[var0] = new ItemBlock(block);
            }

            int i;
            for(i = 0; i < 6; ++i) {
               for(int metadata = 0; metadata < 16; ++metadata) {
                  if (block.isValidMetadata(metadata) && (block.use_neighbor_brightness[metadata + i * 16] = block.useNeighborBrightness(block.use_neighbor_brightness, metadata, EnumDirection.get(i)))) {
                     useNeighborBrightness[var0] = true;
                  }
               }
            }

            if (var0 > 0) {
               Item item = Item.itemsList[var0];
               item.setCraftingDifficultyAsComponent(block.getCraftingDifficultyAsComponent(0));
               item.setMaxStackSize(block.getItemStackLimit());
            }

            for(i = 1; i < block.minX.length; ++i) {
               if (block.minX[i] != block.minX[0]) {
                  Minecraft.setErrorMessage("minX discrepency for " + block);
               }

               if (block.minY[i] != block.minY[0]) {
                  Minecraft.setErrorMessage("minY discrepency for " + block);
               }

               if (block.minZ[i] != block.minZ[0]) {
                  Minecraft.setErrorMessage("minZ discrepency for " + block);
               }

               if (block.maxX[i] != block.maxX[0]) {
                  Minecraft.setErrorMessage("maxX discrepency for " + block);
               }

               if (block.maxY[i] != block.maxY[0]) {
                  Minecraft.setErrorMessage("maxY discrepency for " + block);
               }

               if (block.maxZ[i] != block.maxZ[0]) {
                  Minecraft.setErrorMessage("maxZ discrepency for " + block);
               }
            }
         }
      }

      Item.itemsList[mushroomBrown.blockID].setFoodValue(1, 1, false, false, false).setPlantProduct();
      Item.itemsList[mushroomRed.blockID].setFoodValue(1, 1, false, false, false).setPlantProduct();
      Item.itemsList[cake.blockID].setFoodValue(2, 2, true, MITEConstant.egg_has_essential_fats, false).setPlantProduct().setAnimalProduct();
      canHaveLightValue[0] = true;
      StatList.initBreakableStats();

      for(var0 = 0; var0 < 256; ++var0) {
         block = getBlock(var0);
         if (block != null) {
            block.validate();
         }
      }

   }


   /* =================================================== FORGE START =====================================*/
   /**
    * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return The light value
    */
   public int getLightValue(IBlockAccess world, int x, int y, int z)
   {
      Block block = blocksList[world.getBlockId(x, y, z)];
      if (block != null && block != this)
      {
         return block.getLightValue(world, x, y, z);
      }
      return lightValue[blockID];
   }

   /**
    * Checks if a player or entity can use this block to 'climb' like a ladder.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @param entity The entity trying to use the ladder, CAN be null.
    * @return True if the block should act like a ladder
    */
   public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity)
   {
      return false;
   }

   /**
    * Return true if the block is a normal, solid cube.  This
    * determines indirect power state, entity ejection from blocks, and a few
    * others.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return True if the block is a full cube
    */
   public boolean isBlockNormalCube(World world, int x, int y, int z)
   {
      return this.isAlwaysOpaqueStandardFormCube() && renderAsNormalBlock() && !canProvidePower();
   }

   /**
    * Checks if the block is a solid face on the given side, used by placement logic.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @param side The side to check
    * @return True if the block is solid on the specified side.
    */
   public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
   {
      int meta = world.getBlockMetadata(x, y, z);
//      if (this instanceof BlockHalfSlab)
//      {
//         return (((meta & 8) == 8 && (side == ForgeDirection.UP)) || this.isAlwaysOpaqueStandardFormCube());
//      }
//      else
         if (this instanceof BlockFarmland)
      {
         return (side != ForgeDirection.DOWN && side != UP);
      }
      else if (this instanceof BlockStairs)
      {
         boolean flipped = ((meta & 4) != 0);
         return ((meta & 3) + side.ordinal() == 5) || (side == UP && flipped);
      }
      else if (this instanceof BlockHopper && side == UP)
      {
         return true;
      }
      else if (this instanceof BlockPoweredOre)
      {
         return true;
      }
      return isBlockNormalCube(world, x, y, z);
   }

   /**
    * Determines if a new block can be replace the space occupied by this one,
    * Used in the player's placement code to make the block act like water, and lava.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return True if the block is replaceable by another block
    */
   public boolean isBlockReplaceable(World world, int x, int y, int z)
   {
      return blockMaterial.isReplaceable();
   }

   /**
    * Determines if this block should set fire and deal fire damage
    * to entities coming into contact with it.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return True if the block should deal damage
    */
   public boolean isBlockBurning(World world, int x, int y, int z)
   {
      return false;
   }

   /**
    * Determines this block should be treated as an air block
    * by the rest of the code. This method is primarily
    * useful for creating pure logic-blocks that will be invisible
    * to the player and otherwise interact as air would.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return True if the block considered air
    */
   public boolean isAirBlock(World world, int x, int y, int z)
   {
      return false;
   }

   /**
    * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
    *
    * @param player The player damaging the block, may be null
    * @param meta The block's current metadata
    * @return True to spawn the drops
    */
   public boolean canHarvestBlock(EntityPlayer player, int meta)
   {
      return ForgeHooks.canHarvestBlock(this, player, meta);
   }

   /**
    * Called when a player removes a block.  This is responsible for
    * actually destroying the block, and the block is intact at time of call.
    * This is called regardless of whether the player can harvest the block or
    * not.
    *
    * Return true if the block is actually destroyed.
    *
    * Note: When used in multiplayer, this is called on both client and
    * server sides!
    *
    * @param world The current world
    * @param player The player damaging the block, may be null
    * @param x X Position
    * @param y Y position
    * @param z Z position
    * @return True if the block is actually destroyed.
    */
   public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
   {
      return world.setBlockToAir(x, y, z);
   }

   /**
    * Called when a new CreativeContainer is opened, populate the list
    * with all of the items for this block you want a player in creative mode
    * to have access to.
    *
    * @param itemList The list of items to display on the creative inventory.
    */
   @Deprecated
   public void addCreativeItems(ArrayList itemList)
   {
   }

   /**
    * Chance that fire will spread and consume this block.
    * 300 being a 100% chance, 0, being a 0% chance.
    *
    * @param world The current world
    * @param x The blocks X position
    * @param y The blocks Y position
    * @param z The blocks Z position
    * @param metadata The blocks current metadata
    * @param face The face that the fire is coming from
    * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
    */
   public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
   {
      return blockFlammability[blockID];
   }

   /**
    * Called when fire is updating, checks if a block face can catch fire.
    *
    *
    * @param world The current world
    * @param x The blocks X position
    * @param y The blocks Y position
    * @param z The blocks Z position
    * @param metadata The blocks current metadata
    * @param face The face that the fire is coming from
    * @return True if the face can be on fire, false otherwise.
    */
   public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
   {
      return getFlammability(world, x, y, z, metadata, face) > 0;
   }

   /**
    * Called when fire is updating on a neighbor block.
    * The higher the number returned, the faster fire will spread around this block.
    *
    * @param world The current world
    * @param x The blocks X position
    * @param y The blocks Y position
    * @param z The blocks Z position
    * @param metadata The blocks current metadata
    * @param face The face that the fire is coming from
    * @return A number that is used to determine the speed of fire growth around the block
    */
   public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face)
   {
      return blockFireSpreadSpeed[blockID];
   }

   /**
    * Currently only called by fire when it is on top of this block.
    * Returning true will prevent the fire from naturally dying during updating.
    * Also prevents firing from dying from rain.
    *
    * @param world The current world
    * @param x The blocks X position
    * @param y The blocks Y position
    * @param z The blocks Z position
    * @param metadata The blocks current metadata
    * @param side The face that the fire is coming from
    * @return True if this block sustains fire, meaning it will never go out.
    */
   public boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side)
   {
      if (blockID == Block.netherrack.blockID && side == UP)
      {
         return true;
      }
      if ((world.provider instanceof WorldProviderEnd) && blockID == Block.bedrock.blockID && side == UP)
      {
         return true;
      }
      return false;
   }

   /**
    * Called by BlockFire to setup the burn values of vanilla blocks.
    * @param id The block id
    * @param encouragement How much the block encourages fire to spread
    * @param flammability how easy a block is to catch fire
    */
   public static void setBurnProperties(int id, int encouragement, int flammability)
   {
      blockFireSpreadSpeed[id] = encouragement;
      blockFlammability[id] = flammability;
   }

   private boolean isTileProvider = this instanceof ITileEntityProvider;
   /**
    * Called throughout the code as a replacement for block instanceof BlockContainer
    * Moving this to the Block base class allows for mods that wish to extend vinella
    * blocks, and also want to have a tile entity on that block, may.
    *
    * Return true from this function to specify this block has a tile entity.
    *
    * @param metadata Metadata of the current block
    * @return True if block has a tile entity, false otherwise
    */
   public boolean hasTileEntity(int metadata)
   {
      return isTileProvider;
   }

   /**
    * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
    * Return the same thing you would from that function.
    * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
    *
    * @param metadata The Metadata of the current block
    * @return A instance of a class extending TileEntity
    */
   public TileEntity createTileEntity(World world, int metadata)
   {
      if (isTileProvider)
      {
         return ((ITileEntityProvider)this).createNewTileEntity(world);
      }
      return null;
   }

   /**
    * Metadata and fortune sensitive version, this replaces the old (int meta, Random rand)
    * version in 1.1.
    *
    * @return The number of items to drop
    */
   public int quantityDropped(int meta, int fortune, Random random)
   {
      return fortune;
   }

//   /**
//    * This returns a complete list of items dropped from this block.
//    *
//    * @param world The current world
//    * @param x X Position
//    * @param y Y Position
//    * @param z Z Position
//    * @param metadata Current metadata
//    * @param fortune Breakers fortune level
//    * @return A ArrayList containing all items this block drops
//    */
   public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
   {
      ArrayList<ItemStack> ret = new ArrayList<>();
      BlockBreakInfo info = new BlockBreakInfo(world, x, y, z);

      int count = quantityDropped(metadata, fortune, world.rand);
      for(int i = 0; i < count; i++)
      {
         int id = info.block_id;
         if (id > 0)
         {
            ret.add(new ItemStack(id, 1, info.damage));
         }
      }
      return ret;
   }

   /**
    * Return true from this function if the player with silk touch can harvest this block directly, and not it's normal drops.
    *
    * @param world The world
    * @param player The player doing the harvesting
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param metadata The metadata
    * @return True if the block can be directly harvested using silk touch
    */
   public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
   {
       return this.canSilkHarvest(metadata);
   }

   /**
    * Determines if a specified mob type can spawn on this block, returning false will
    * prevent any mob from spawning on the block.
    *
    * @param type The Mob Category Type
    * @param world The current world
    * @param x The X Position
    * @param y The Y Position
    * @param z The Z Position
    * @return True to allow a mob of the specified category to spawn, false to prevent it.
    */
   public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
   {
      int meta = world.getBlockMetadata(x, y, z);
//      if (this instanceof BlockStep)
//      {
//         return (((meta & 8) == 8) || isOpaqueCube());
//      }
//      else
         if (this instanceof BlockStairs)
      {
         return ((meta & 4) != 0);
      }
      return isBlockSolidOnSide(world, x, y, z, UP);
   }

   /**
    * Determines if this block is classified as a Bed, Allowing
    * players to sleep in it, though the block has to specifically
    * perform the sleeping functionality in it's activated event.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param player The player or camera entity, null in some cases.
    * @return True to treat this as a bed
    */
   public boolean isBed(World world, int x, int y, int z, EntityLivingBase player)
   {
      return blockID == Block.bed.blockID;
   }

   /**
    * Returns the position that the player is moved to upon
    * waking up, or respawning at the bed.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param player The player or camera entity, null in some cases.
    * @return The spawn position
    */
   public ChunkCoordinates getBedSpawnPosition(World world, int x, int y, int z, EntityPlayer player)
   {
      return BlockBed.getNearestEmptyChunkCoordinates(world, x, y, z, 0, Vec3.createVectorHelper(x, y, z));
   }

   /**
    * Called when a user either starts or stops sleeping in the bed.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param player The player or camera entity, null in some cases.
    * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
    */
   public void setBedOccupied(World world, int x, int y, int z, EntityPlayer player, boolean occupied)
   {
      BlockBed.setBedOccupied(world,  x, y, z, occupied);
   }

   /**
    * Returns the direction of the block. Same values that
    * are returned by BlockDirectional
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return Bed direction
    */
   public int getBedDirection(IBlockAccess world, int x, int y, int z)
   {
      return this.getDirectionFacing(world.getBlockMetadata(x,  y, z)).ordinal();
   }

   /**
    * Determines if the current block is the foot half of the bed.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return True if the current block is the foot side of a bed.
    */
   public boolean isBedFoot(IBlockAccess world, int x, int y, int z)
   {
      return BlockBed.isBlockHeadOfBed(world.getBlockMetadata(x,  y, z));
   }

   /**
    * Called when a leaf should start its decay process.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    */
   public void beginLeavesDecay(World world, int x, int y, int z){}

   /**
    * Determines if this block can prevent leaves connected to it from decaying.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return true if the presence this block can prevent leaves from decaying.
    */
   public boolean canSustainLeaves(World world, int x, int y, int z)
   {
      return false;
   }

   /**
    * Determines if this block is considered a leaf block, used to apply the leaf decay and generation system.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return true if this block is considered leaves.
    */
   public boolean isLeaves(World world, int x, int y, int z)
   {
      return false;
   }

   /**
    * Used during tree growth to determine if newly generated leaves can replace this block.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return true if this block can be replaced by growing leaves.
    */
   public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
   {
      return !Block.opaqueCubeLookup[this.blockID];
   }

   /**
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return  true if the block is wood (logs)
    */
   public boolean isWood(World world, int x, int y, int z)
   {
      return false;
   }

   /**
    * Determines if the current block is replaceable by Ore veins during world generation.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param target The generic target block the gen is looking for, Standards define stone
    *      for overworld generation, and neatherack for the nether.
    * @return True to allow this block to be replaced by a ore
    */
   public boolean isGenMineableReplaceable(World world, int x, int y, int z, int target)
   {
      return blockID == target;
   }

   /**
    * Location sensitive version of getExplosionRestance
    *
    * @param par1Entity The entity that caused the explosion
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param explosionX Explosion source X Position
    * @param explosionY Explosion source X Position
    * @param explosionZ Explosion source X Position
    * @return The amount of the explosion absorbed.
    */
   public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
   {
      return getExplosionResistance(new Explosion(world, par1Entity, explosionX, explosionY, explosionZ, 1.0F, 1.0F));
   }

   /**
    * Called when the block is destroyed by an explosion.
    * Useful for allowing the block to take into account tile entities,
    * metadata, etc. when exploded, before it is removed.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    */
   public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
   {
      world.setBlockToAir(x, y, z);
      onBlockDestroyedByExplosion(world, x, y, z, explosion);
   }

   /**
    * Determine if this block can make a redstone connection on the side provided,
    * Useful to control which sides are inputs and outputs for redstone wires.
    *
    * Side:
    *  -1: UP
    *   0: NORTH
    *   1: EAST
    *   2: SOUTH
    *   3: WEST
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @param side The side that is trying to make the connection
    * @return True to make the connection
    */
   public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
   {
      return Block.blocksList[blockID].canProvidePower() && side != -1;
   }


   public boolean canPlaceTorchOnTop(Block block) {
      return block == Block.fence || block == Block.netherFence || block == Block.glass || block == Block.cobblestoneWall;
   }


   /**
    * Determines if a torch can be placed on the top surface of this block.
    * Useful for creating your own block that torches can be on, such as fences.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z Position
    * @return True to allow the torch to be placed
    */
   public boolean canPlaceTorchOnTop(World world, int x, int y, int z)
   {
      if (world.isBlockSolid(x, y, z))
      {
         return true;
      }
      else
      {
         int id = world.getBlockId(x, y, z);
         return id == Block.fence.blockID || id == Block.netherFence.blockID || id == Block.glass.blockID || id == Block.cobblestoneWall.blockID;
      }
   }


   /**
    * Determines if this block should render in this pass.
    *
    * @param pass The pass in question
    * @return True to render
    */
   public boolean canRenderInPass(int pass)
   {
      return pass == getRenderBlockPass();
   }

   /**
    * Called when a user uses the creative pick block button on this block
    *
    * @param target The full target the player is looking at
    * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
    */
   public ItemStack getPickBlock(RaycastCollision target, World world, int x, int y, int z)
   {
      int id = idPicked(world, x, y, z);

      if (id == 0)
      {
         return null;
      }

      Item item = Item.itemsList[id];
      if (item == null)
      {
         return null;
      }

      return new ItemStack(id, 1, (new BlockBreakInfo(world, x, y, z)).damage);
   }

   /**
    * Used by getTopSolidOrLiquidBlock while placing biome decorations, villages, etc
    * Also used to determine if the player can spawn on this block.
    *
    * @return False to disallow spawning
    */
   public boolean isBlockFoliage(World world, int x, int y, int z)
   {
      return false;
   }

   /**
    * Spawn a digging particle effect in the world, this is a wrapper
    * around EffectRenderer.addBlockHitEffects to allow the block more
    * control over the particles. Useful when you have entirely different
    * texture sheets for different sides/locations in the world.
    *

    * @param target The target the player is looking at {x/y/z/side/sub}
    * @param effectRenderer A reference to the current effect renderer.
    * @return True to prevent vanilla digging particles form spawning.
    */

   public boolean addBlockHitEffects(World worldObj, RaycastCollision target, EffectRenderer effectRenderer)
   {
      return false;
   }

   /**
    * Spawn particles for when the block is destroyed. Due to the nature
    * of how this is invoked, the x/y/z locations are not always guaranteed
    * to host your block. So be sure to do proper sanity checks before assuming
    * that the location is this block.
    *
    * @param world The current world
    * @param x X position to spawn the particle
    * @param y Y position to spawn the particle
    * @param z Z position to spawn the particle
    * @param meta The metadata for the block before it was destroyed.
    * @param effectRenderer A reference to the current effect renderer.
    * @return True to prevent vanilla break particles from spawning.
    */

   public boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
   {
      return false;
   }

   /**
    * Determines if this block can support the passed in plant, allowing it to be planted and grow.
    * Some examples:
    *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
    *   Cacti checks if its a cacti, or if its sand
    *   Nether types check for soul sand
    *   Crops check for tilled soil
    *   Caves check if it's a colid surface
    *   Plains check if its grass or dirt
    *   Water check if its still water
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z position
    * @param direction The direction relative to the given position the plant wants to be, typically its UP
    * @param plant The plant that wants to check
    * @return True to allow the plant to be planted/stay.
    */
   public boolean canSustainPlant(World world, int x, int y, int z, ForgeDirection direction, IPlantable plant)
   {
      int plantID = plant.getPlantID(world, x, y + 1, z);
      EnumPlantType plantType = plant.getPlantType(world, x, y + 1, z);

      if (plantID == cactus.blockID && blockID == cactus.blockID)
      {
         return true;
      }

      if (plantID == reed.blockID && blockID == reed.blockID)
      {
         return true;
      }

//      if (plant instanceof BlockFlower && ((BlockFlower)plant).canThisPlantGrowOnThisBlockID(blockID))
//      {
//         return true;
//      }

      switch (plantType)
      {
         case Desert: return blockID == sand.blockID;
         case Nether: return blockID == slowSand.blockID;
         case Crop:   return blockID == tilledField.blockID;
         case Cave:   return isBlockSolidOnSide(world, x, y, z, UP);
         case Plains: return blockID == grass.blockID || blockID == dirt.blockID;
         case Water:  return world.getBlockMaterial(x, y, z) == Material.water && world.getBlockMetadata(x, y, z) == 0;
         case Beach:
            boolean isBeach = (blockID == Block.grass.blockID || blockID == Block.dirt.blockID || blockID == Block.sand.blockID);
            boolean hasWater = (world.getBlockMaterial(x - 1, y, z    ) == Material.water ||
                    world.getBlockMaterial(x + 1, y, z    ) == Material.water ||
                    world.getBlockMaterial(x,     y, z - 1) == Material.water ||
                    world.getBlockMaterial(x,     y, z + 1) == Material.water);
            return isBeach && hasWater;
      }

      return false;
   }

   /**
    * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
    * Modder may implement this for custom plants.
    * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
    * so the source location is specified.
    * Currently this just changes the block to dirt if it was grass.
    *
    * Note: This happens DURING the generation, the generation may not be complete when this is called.
    *
    * @param world Current world
    * @param x Soil X
    * @param y Soil Y
    * @param z Soil Z
    * @param sourceX Plant growth location X
    * @param sourceY Plant growth location Y
    * @param sourceZ Plant growth location Z
    */
   public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
   {
      if (blockID == grass.blockID)
      {
         world.setBlock(x, y, z, dirt.blockID, 0, 2);
      }
   }

   /**
    * Checks if this soil is fertile, typically this means that growth rates
    * of plants on this soil will be slightly sped up.
    * Only vanilla case is tilledField when it is within range of water.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z position
    * @return True if the soil should be considered fertile.
    */
   public boolean isFertile(World world, int x, int y, int z)
   {
      if (blockID == tilledField.blockID)
      {
         return world.getBlockMetadata(x, y, z) > 0;
      }

      return false;
   }

   /**
    * Location aware and overrideable version of the lightOpacity array,
    * return the number to subtract from the light value when it passes through this block.
    *
    * This is not guaranteed to have the tile entity in place before this is called, so it is
    * Recommended that you have your tile entity call relight after being placed if you
    * rely on it for light info.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z position
    * @return The amount of light to block, 0 for air, 255 for fully opaque.
    */
   public int getLightOpacity(World world, int x, int y, int z)
   {
      return lightOpacity[blockID];
   }

   /**
    * Determines if this block is can be destroyed by the specified entities normal behavior.
    *
    * @param world The current world
    * @param x X Position
    * @param y Y Position
    * @param z Z position
    * @return True to allow the ender dragon to destroy this block
    */
   public boolean canEntityDestroy(World world, int x, int y, int z, Entity entity)
   {
      if (entity instanceof EntityWither)
      {
         return blockID != Block.bedrock.blockID && blockID != Block.endPortal.blockID && blockID != Block.endPortalFrame.blockID  && blockID != Block.mantleOrCore.blockID;
      }
      else if (entity instanceof EntityDragon)
      {
         return canDragonDestroy(world, x, y, z);
      }

      return true;
   }
   @Deprecated
   public boolean canDragonDestroy(World world, int x, int y, int z)
   {
      return blockID != obsidian.blockID && blockID != whiteStone.blockID && blockID != bedrock.blockID;
   }

   /**
    * Determines if this block can be used as the base of a beacon.
    *

    * @param x X Position
    * @param y Y Position
    * @param z Z position
    * @param beaconX Beacons X Position
    * @param beaconY Beacons Y Position
    * @param beaconZ Beacons Z Position
    * @return True, to support the beacon, and make it active with this block.
    */
   public boolean isBeaconBase(World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
   {
      return (blockID == blockEmerald.blockID || blockID == blockGold.blockID || blockID == blockDiamond.blockID || blockID == blockIron.blockID);
   }

   /**
    * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
    * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
    * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
    * The method should return true if the rotation was successful though.
    *
    * @param worldObj The world
    * @param x X position
    * @param y Y position
    * @param z Z position
    * @param axis The axis to rotate around
    * @return True if the rotation was successful, False if the rotation failed, or is not possible
    */
   public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
   {
      return RotationHelper.rotateVanillaBlock(this, worldObj, x, y, z, axis);
   }

   /**
    * Get the rotations that can apply to the block at the specified coordinates. Null means no rotations are possible.
    * Note, this is up to the block to decide. It may not be accurate or representative.
    * @param worldObj The world
    * @param x X position
    * @param y Y position
    * @param z Z position
    * @return An array of valid axes to rotate around, or null for none or unknown
    */
   public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
   {
      return RotationHelper.getValidVanillaBlockRotations(this);
   }

   /**
    * Determines the amount of enchanting power this block can provide to an enchanting table.
    * @param world The World
    * @param x X position
    * @param y Y position
    * @param z Z position
    * @return The amount of enchanting power this block produces.
    */
   public float getEnchantPowerBonus(World world, int x, int y, int z)
   {
      return blockID == bookShelf.blockID ? 1 : 0;
   }
   /**
    * Common way to recolour a block with an external tool
    * @param world The world
    * @param x X
    * @param y Y
    * @param z Z
    * @param side The side hit with the colouring tool
    * @param colour The colour to change to
    * @return If the recolouring was successful
    */
   public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
   {
      if (blockID == cloth.blockID)
      {
         int meta = world.getBlockMetadata(x, y, z);
         if (meta != colour)
         {
            world.setBlockMetadataWithNotify(x, y, z, colour, 3);
            return true;
         }
      }
      return false;
   }

   /**
    * @return the amount of XP that this block should drop when it is broken
    */
   public int getExpDrop(World world, int data, int enchantmentLevel)
   {
      return 0;
   }

   /**
    * Called when a tile entity on a side of this block changes is created or is destroyed.
    * @param world The world
    * @param x The x position of this block instance
    * @param y The y position of this block instance
    * @param z The z position of this block instance
    * @param tileX The x position of the tile that changed
    * @param tileY The y position of the tile that changed
    * @param tileZ The z position of the tile that changed
    */
   public void onNeighborTileChange(World world, int x, int y, int z, int tileX, int tileY, int tileZ)
   {
   }

   /**
    * @return true if this block is to be notified of TileEntity changes directly through one solid block like comparators
    */
   public boolean weakTileChanges()
   {
      return false;
   }

   /**
    * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
    * @param world The world
    * @param x The x position of this block instance
    * @param y The y position of this block instance
    * @param z The z position of this block instance
    * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
    * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
    */
   public boolean shouldCheckWeakPower(World world, int x, int y, int z, int side)
   {
      return !this.isNormalCube(world.getBlockId(x, y, z));
   }

   @Deprecated //Implemented here as we changed the IFluidBlock interface, and this allows us to do so without breaking exisitng mods.
   // To be removed next MC version {1.6.3+}
   public float getFilledPercentage(World world, int x, int y, int z){ return 1; }

}
