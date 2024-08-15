package net.minecraft.block.material;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumEquipmentMaterial;
import net.minecraft.util.EnumMaterialHardness;
import net.minecraft.util.EnumQuality;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Translator;

public class Material {
   public static Material[] materials = new Material[1024];
   public static int num_materials;
   public static final Material air;
   public static final Material leather;
   public static final Material wood;
   public static final Material flint;
   public static final Material stone;
   public static final Material obsidian;
   public static final Material rusted_iron;
   public static final Material copper;
   public static final Material silver;
   public static final Material gold;
   public static final Material iron;
   public static final Material ancient_metal;
   public static final Material mithril;
   public static final Material adamantium;
   public static final Material netherrack;
   public static final Material glass;
   public static final Material quartz;
   public static final Material emerald;
   public static final Material diamond;
   public static final Material grass;
   public static final Material dirt;
   public static final Material redstone;
   public static final Material anvil;
   public static final Material water;
   public static final Material lava;
   public static final Material tree_leaves;
   public static final Material plants;
   public static final Material vine;
   public static final Material sponge;
   public static final Material cloth;
   public static final Material fire;
   public static final Material sand;
   public static final Material circuits;
   public static final Material materialCarpet;
   public static final Material redstoneLight;
   public static final Material tnt;
   public static final Material coral;
   public static final Material ice;
   public static final Material snow;
   public static final Material craftedSnow;
   public static final Material cactus;
   public static final Material clay;
   public static final Material hardened_clay;
   public static final Material pumpkin;
   public static final Material dragonEgg;
   public static final Material portal;
   public static final Material cake;
   public static final Material web;
   public static final Material piston;
   public static final Material milk;
   public static final Material mushroom_stew;
   public static final Material beef_stew;
   public static final Material chicken_soup;
   public static final Material vegetable_soup;
   public static final Material cream_of_mushroom_soup;
   public static final Material cream_of_vegetable_soup;
   public static final Material pumpkin_soup;
   public static final Material mashed_potato;
   public static final Material sorbet;
   public static final Material ice_cream;
   public static final Material salad;
   public static final Material fruit;
   public static final Material vegetable;
   public static final Material meat;
   public static final Material bread;
   public static final Material desert;
   public static final Material pie;
   public static final Material porridge;
   public static final Material cereal;
   public static final Material sugar;
   public static final Material cheese;
   public static final Material flour;
   public static final Material dough;
   public static final Material seed;
   public static final Material bone;
   public static final Material paper;
   public static final Material manure;
   public static final Material coal;
   public static final Material lapis_lazuli;
   public static final Material feather;
   public static final Material gunpowder;
   public static final Material slime;
   public static final Material glowstone;
   public static final Material dye;
   public static final Material ender_pearl;
   public static final Material blaze;
   public static final Material silk;
   public static final Material frags;
   public static final Material vinyl;
   private boolean is_liquid;
   private boolean is_edible;
   private boolean is_drinkable;
   private boolean can_catch_fire;
   private boolean can_burn_as_fuel_source;
   private boolean is_harmed_by_fire;
   private boolean can_douse_fire;
   private boolean dissolves_in_water;
   private boolean is_replaceable;
   private boolean is_translucent;
   private boolean is_adventure_mode_exempt;
   private boolean is_metal;
   private boolean is_rocky_mineral;
   private boolean is_crystal;
   private boolean requires_tool;
   private boolean is_harmed_by_lava;
   private boolean is_harmed_by_pepsin;
   private boolean is_harmed_by_acid;
   public MapColor map_color;
   private int mobility_flag;
   public float durability;
   public int enchantability;
   public String name;
   private float full_block_hardness;
   public int min_harvest_level;
   private EnumQuality max_quality;

   public Material(String name) {
      this(name, (MapColor)null);
   }

   public Material(String name, MapColor map_color) {
      this.is_harmed_by_lava = true;
      this.is_harmed_by_pepsin = false;
      this.is_harmed_by_acid = true;
      this.max_quality = EnumQuality.getHighestQuality();
      this.setName(name);
      this.setMapColor(map_color);
      materials[num_materials++] = this;
   }

   public Material(EnumEquipmentMaterial enum_crafting_material) {
      this(enum_crafting_material.name, (MapColor)null);
      this.setDurability(enum_crafting_material.durability);
      this.setEnchantability(enum_crafting_material.enchantability);
      this.setMaxQuality(enum_crafting_material.max_quality);
   }

   public Material setMapColor(MapColor map_color) {
      this.map_color = map_color;
      return this;
   }

   public Material setDurability(float durability) {
      this.durability = durability;
      return this;
   }

   public Material setEnchantability(int enchantability) {
      this.enchantability = enchantability;
      return this;
   }

   public Material setName(String material_name) {
      this.name = material_name;
      return this;
   }

   public final Material setFullBlockHardness(float full_block_hardness) {
      this.full_block_hardness = full_block_hardness;
      return this;
   }

   public final float getFullBlockHardness() {
      return this.full_block_hardness;
   }

   public Material setMinHarvestLevel(int min_harvest_level) {
      this.min_harvest_level = min_harvest_level;
      return this;
   }

   public final boolean isLiquid() {
      return this.is_liquid;
   }

   public Material setLiquid(boolean can_douse_fire) {
      if (can_douse_fire) {
         this.setCanDouseFire();
      }

      this.is_liquid = true;
      this.setHarmedByAcid(false);
      return this;
   }

   public boolean isSolid() {
      return true;
   }

   public Material setEdible() {
      this.is_edible = true;
      return this;
   }

   public boolean isEdible() {
      return this.is_edible;
   }

   public Material setDrinkable() {
      this.is_drinkable = true;
      return this;
   }

   public boolean isDrinkable() {
      return this.is_drinkable;
   }

   private Material setTranslucent() {
      this.is_translucent = true;
      return this;
   }

   protected Material setRequiresTool() {
      this.requires_tool = true;
      return this;
   }

   public Material setFlammability(boolean can_catch_fire, boolean can_burn_as_fuel_source, boolean is_harmed_by_fire) {
      this.can_catch_fire = can_catch_fire;
      this.can_burn_as_fuel_source = can_burn_as_fuel_source;
      this.is_harmed_by_fire = is_harmed_by_fire;
      return this;
   }

   public Material setHarmedByLava(boolean is_harmed_by_lava) {
      this.is_harmed_by_lava = is_harmed_by_lava;
      return this;
   }

   public boolean canCatchFire() {
      return this.can_catch_fire;
   }

   public boolean canBurnAsFuelSource() {
      return this.can_burn_as_fuel_source;
   }

   public boolean isHarmedByFire() {
      return this.is_harmed_by_fire;
   }

   public boolean isHarmedByLava() {
      return this.is_harmed_by_lava;
   }

   public boolean isHarmedByPepsin() {
      return this.is_harmed_by_pepsin;
   }

   public boolean isHarmedByAcid() {
      return this.is_harmed_by_acid;
   }

   protected Material setCanDouseFire() {
      this.setFlammability(false, false, true);
      this.can_douse_fire = true;
      return this;
   }

   public boolean canDouseFire() {
      return this.can_douse_fire;
   }

   public Material setDissolvesInWater() {
      this.dissolves_in_water = true;
      return this;
   }

   public boolean dissolvesInWater() {
      return this.dissolves_in_water;
   }

   public Material setReplaceable() {
      this.is_replaceable = true;
      return this;
   }

   public boolean isReplaceable() {
      return this.is_replaceable;
   }

   public Material setMetal(boolean is_harmed_by_acid) {
      this.is_metal = true;
      this.setHarmedByAcid(is_harmed_by_acid);
      return this;
   }

   public boolean isMetal() {
      return this.is_metal;
   }

   public Material setRockyMineral() {
      return this.setRockyMineral(false);
   }

   public Material setRockyMineral(boolean is_crystal) {
      this.is_rocky_mineral = true;
      this.is_crystal = is_crystal;
      this.setHarmedByAcid(false);
      return this;
   }

   public boolean isRockyMineral() {
      return this.is_rocky_mineral;
   }

   public boolean isCrystal() {
      return this.is_crystal;
   }

   public Material setMaxQuality(EnumQuality max_quality) {
      this.max_quality = max_quality;
      return this;
   }

   public EnumQuality getMaxQuality() {
      return this.max_quality;
   }

   public final boolean requiresTool(Block block, int metadata) {
      return this.requires_tool || block.getMinHarvestLevel(metadata) > 0;
   }

   public int getMaterialMobility() {
      return this.mobility_flag;
   }

   protected Material setNoPushMobility() {
      this.mobility_flag = 1;
      return this;
   }

   protected Material setImmovableMobility() {
      this.mobility_flag = 2;
      return this;
   }

   protected Material setAdventureModeExempt() {
      this.is_adventure_mode_exempt = true;
      return this;
   }

   public boolean isAdventureModeExempt() {
      return this.is_adventure_mode_exempt;
   }

   public float getDamageVsEntity() {
      if (this == wood) {
         return 0.0F;
      } else if (this == flint) {
         return 1.0F;
      } else if (this == obsidian) {
         return 2.0F;
      } else if (this == rusted_iron) {
         return 2.0F;
      } else if (this == copper) {
         return 3.0F;
      } else if (this == silver) {
         return 3.0F;
      } else if (this == gold) {
         return 2.0F;
      } else if (this == iron) {
         return 4.0F;
      } else if (this == ancient_metal) {
         return 4.0F;
      } else if (this == mithril) {
         return 5.0F;
      } else if (this == adamantium) {
         return 6.0F;
      } else if (this == diamond) {
         return 4.0F;
      } else {
         Minecraft.setErrorMessage("getDamageVsEntity: unhandled material " + this.name);
         return 0.0F;
      }
   }

   public String getCapitalizedName() {
      return StringHelper.capitalizeEachWord(this.name.replaceAll("_", " "));
   }

   public String getTranslationKey() {
      return StringHelper.convertUnderscoresToCamelCase(this.toString());
   }

   public String getLocalizedName() {
      return Translator.get("material." + this.getTranslationKey() + ".name");
   }

   public String toString() {
      return this.name;
   }

   public static Material getFromList(List list, int index) {
      return (Material)list.get(index);
   }

   public static EnumQuality getLowestMaxQualityOfMaterials(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("getLowestMaxQualityOfMaterials: materials list was empty");
         return null;
      } else if (materials.size() == 1) {
         return ((Material)materials.get(0)).getMaxQuality();
      } else {
         List max_qualities = new ArrayList();

         for(int i = 0; i < materials.size(); ++i) {
            max_qualities.add(((Material)materials.get(i)).getMaxQuality());
         }

         return EnumQuality.getLowest(max_qualities);
      }
   }

   public static EnumQuality getHighestMaxQualityOfMaterials(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("getHighestMaxQualityOfMaterials: materials list is empty");
         return null;
      } else if (materials.size() == 1) {
         return ((Material)materials.get(0)).getMaxQuality();
      } else {
         List max_qualities = new ArrayList();

         for(int i = 0; i < materials.size(); ++i) {
            max_qualities.add(((Material)materials.get(i)).getMaxQuality());
         }

         return EnumQuality.getHighest(max_qualities);
      }
   }

   public static boolean doesMaterialListContainMetal(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMetal: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isMetal()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainRockyMineral(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainRockyMineral: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isRockyMineral()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainCrystal(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainCrystal: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isCrystal()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatCanDouseFire(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatCanDouseFire: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).canDouseFire()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatCanCatchFire(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatCanCatchFire: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).canCatchFire()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatCanBurnAsFuelSource(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatCanBurnAsFuelSource: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).canBurnAsFuelSource()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatIsHarmedByFire(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatIsHarmedByFire: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isHarmedByFire()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatIsHarmedByLava(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatIsHarmedByLava: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isHarmedByLava()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatIsHarmedByPepsin(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatIsHarmedByPepsin: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isHarmedByPepsin()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatIsHarmedByAcid(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatIsHarmedByAcid: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).isHarmedByAcid()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean doesMaterialListContainMaterialThatDissolvesInWater(List materials) {
      if (materials.size() == 0) {
         Minecraft.setErrorMessage("doesMaterialListContainMaterialThatDissolvesInWater: materials list is empty");
         return false;
      } else {
         for(int i = 0; i < materials.size(); ++i) {
            if (((Material)materials.get(i)).dissolvesInWater()) {
               return true;
            }
         }

         return false;
      }
   }

   public static String[] getMaterialNames(List materials) {
      String[] names = new String[materials.size()];

      for(int i = 0; i < materials.size(); ++i) {
         names[i] = getFromList(materials, i).name;
      }

      return names;
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

   public boolean isSnow() {
      return this == snow || this == craftedSnow;
   }

   public boolean isFreezing() {
      return this.isSnow() || this == ice;
   }

   public Material setHarmedByPepsin() {
      this.is_harmed_by_pepsin = true;
      return this;
   }

   public Material setHarmedByAcid(boolean is_harmed_by_acid) {
      this.is_harmed_by_acid = is_harmed_by_acid;
      return this;
   }

   private void initialize() {
      this.setFullBlockHardness(EnumMaterialHardness.getHardnessFor(this));
   }

   static {
      air = new MaterialTransparent("air", MapColor.airColor);
      leather = (new Material(EnumEquipmentMaterial.leather)).setMapColor(MapColor.leatherColor).setFlammability(true, false, true).setHarmedByPepsin();
      wood = (new Material(EnumEquipmentMaterial.wood)).setMapColor(MapColor.woodColor).setFlammability(true, true, true).setMinHarvestLevel(0);
      flint = (new Material(EnumEquipmentMaterial.flint)).setMapColor(MapColor.stoneColor).setRockyMineral().setRequiresTool().setMinHarvestLevel(2);
      stone = (new Material("stone", MapColor.stoneColor)).setRockyMineral().setRequiresTool().setMinHarvestLevel(2);
      obsidian = (new Material(EnumEquipmentMaterial.obsidian)).setMapColor(MapColor.obsidianColor).setRockyMineral().setRequiresTool().setMinHarvestLevel(3);
      rusted_iron = (new Material(EnumEquipmentMaterial.rusted_iron)).setMapColor(MapColor.rustedIronColor).setMetal(true).setRequiresTool().setMinHarvestLevel(2);
      copper = (new Material(EnumEquipmentMaterial.copper)).setMapColor(MapColor.copperColor).setMetal(true).setRequiresTool().setMinHarvestLevel(2);
      silver = (new Material(EnumEquipmentMaterial.silver)).setMapColor(MapColor.silverColor).setMetal(true).setRequiresTool().setMinHarvestLevel(2);
      gold = (new Material(EnumEquipmentMaterial.gold)).setMapColor(MapColor.goldColor).setMetal(false).setRequiresTool().setMinHarvestLevel(2);
      iron = (new Material(EnumEquipmentMaterial.iron)).setMapColor(MapColor.ironColor).setMetal(true).setRequiresTool().setMinHarvestLevel(3);
      ancient_metal = (new Material(EnumEquipmentMaterial.ancient_metal)).setMapColor(MapColor.ancientMetalColor).setMetal(true).setRequiresTool().setMinHarvestLevel(3);
      mithril = (new Material(EnumEquipmentMaterial.mithril)).setMapColor(MapColor.mithrilColor).setMetal(false).setRequiresTool().setMinHarvestLevel(4);
      adamantium = (new Material(EnumEquipmentMaterial.adamantium)).setMapColor(MapColor.adamantiumColor).setMetal(false).setHarmedByLava(false).setRequiresTool().setMinHarvestLevel(5);
      netherrack = (new Material(EnumEquipmentMaterial.netherrack)).setFlammability(true, false, false).setHarmedByLava(false).setMapColor(MapColor.netherrackColor).setRockyMineral().setRequiresTool().setMinHarvestLevel(2);
      glass = (new Material(EnumEquipmentMaterial.glass)).setMapColor(MapColor.airColor).setRockyMineral(true).setTranslucent().setAdventureModeExempt();
      quartz = (new Material(EnumEquipmentMaterial.quartz)).setMapColor(MapColor.quartzColor).setRockyMineral(true).setRequiresTool().setMinHarvestLevel(2);
      emerald = (new Material(EnumEquipmentMaterial.emerald)).setMapColor(MapColor.emeraldColor).setRockyMineral(true).setRequiresTool().setMinHarvestLevel(3);
      diamond = (new Material(EnumEquipmentMaterial.diamond)).setMapColor(MapColor.diamondColor).setRockyMineral(true).setRequiresTool().setMinHarvestLevel(4);
      grass = (new Material("grass", MapColor.grassColor)).setFlammability(true, false, true);
      dirt = new Material("dirt", MapColor.dirtColor);
      redstone = (new Material("redstone", MapColor.redstoneColor)).setRockyMineral().setRequiresTool().setMinHarvestLevel(2);
      anvil = (new Material("anvil", MapColor.ironColor)).setImmovableMobility();
      water = (new MaterialLiquid("water", MapColor.waterColor)).setNoPushMobility().setDrinkable();
      lava = (new MaterialLiquid("lava", MapColor.tntColor)).setHarmedByLava(false).setNoPushMobility();
      tree_leaves = (new Material("tree_leaves", MapColor.foliageColor)).setFlammability(true, false, true).setTranslucent().setNoPushMobility();
      plants = (new MaterialLogic("plants", MapColor.foliageColor)).setNoPushMobility().setFlammability(true, false, true);
      vine = (new MaterialLogic("vine", MapColor.foliageColor)).setFlammability(true, false, true).setNoPushMobility().setReplaceable();
      sponge = (new Material("sponge", MapColor.clothColor)).setFlammability(true, true, true);
      cloth = (new Material("cloth", MapColor.clothColor)).setFlammability(true, true, true).setHarmedByPepsin();
      fire = (new MaterialTransparent("fire", MapColor.airColor)).setNoPushMobility();
      sand = new Material("sand", MapColor.sandColor);
      circuits = (new MaterialLogic("circuits", MapColor.airColor)).setNoPushMobility();
      materialCarpet = (new MaterialLogic("carpet", MapColor.clothColor)).setFlammability(true, true, true).setHarmedByPepsin();
      redstoneLight = (new Material("redstone_light", MapColor.airColor)).setRockyMineral().setAdventureModeExempt();
      tnt = (new Material("tnt", MapColor.tntColor)).setFlammability(true, false, true).setTranslucent();
      coral = (new Material("coral", MapColor.foliageColor)).setRockyMineral().setNoPushMobility();
      ice = (new Material("ice", MapColor.iceColor)).setTranslucent().setAdventureModeExempt().setCanDouseFire();
      snow = (new MaterialLogic("snow", MapColor.snowColor)).setReplaceable().setTranslucent().setNoPushMobility().setCanDouseFire().setDissolvesInWater().setHarmedByAcid(false);
      craftedSnow = (new Material("crafted_snow", MapColor.snowColor)).setCanDouseFire().setDissolvesInWater().setHarmedByAcid(false);
      cactus = (new Material("cactus", MapColor.foliageColor)).setTranslucent().setNoPushMobility().setFlammability(true, false, true);
      clay = new Material("clay", MapColor.clayColor);
      hardened_clay = new Material("hardened_clay", MapColor.clayColor);
      pumpkin = (new Material("pumpkin", MapColor.foliageColor)).setNoPushMobility().setFlammability(true, false, true);
      dragonEgg = new Material("dragon_egg", MapColor.obsidianColor);
      portal = (new MaterialPortal(MapColor.airColor)).setImmovableMobility();
      cake = (new Material("cake", MapColor.airColor)).setNoPushMobility().setEdible().setHarmedByPepsin();
      web = (new MaterialWeb(MapColor.clothColor)).setFlammability(true, false, true).setNoPushMobility();
      piston = (new Material("piston", MapColor.stoneColor)).setImmovableMobility();
      milk = (new Material("milk")).setLiquid(true).setDrinkable().setHarmedByPepsin().setHarmedByAcid(true);
      mushroom_stew = new MaterialStew("mushroom_stew");
      beef_stew = (new MaterialStew("beef_stew")).setHarmedByPepsin();
      chicken_soup = (new MaterialSoup("chicken_soup")).setHarmedByPepsin();
      vegetable_soup = new MaterialSoup("vegetable_soup");
      cream_of_mushroom_soup = (new MaterialSoup("cream_of_mushroom_soup")).setHarmedByPepsin();
      cream_of_vegetable_soup = (new MaterialSoup("cream_of_vegetable_soup")).setHarmedByPepsin();
      pumpkin_soup = new MaterialSoup("pumpkin_soup");
      mashed_potato = (new MaterialFood("mashed_potato")).setHarmedByPepsin();
      sorbet = new MaterialFood("sorbet");
      ice_cream = (new MaterialFood("ice_cream")).setCanDouseFire().setHarmedByPepsin();
      salad = new MaterialFood("salad");
      fruit = new MaterialFood("fruit");
      vegetable = new MaterialFood("vegetable");
      meat = (new MaterialFood("meat")).setHarmedByPepsin();
      bread = new MaterialFood("bread");
      desert = new MaterialFood("desert");
      pie = (new MaterialFood("pie")).setHarmedByPepsin();
      porridge = new MaterialFood("porridge");
      cereal = (new MaterialFood("cereal")).setHarmedByPepsin();
      sugar = (new MaterialFood("sugar")).setDissolvesInWater();
      cheese = (new MaterialFood("cheese")).setHarmedByPepsin();
      flour = (new MaterialFood("flour")).setDissolvesInWater();
      dough = new MaterialFood("dough");
      seed = new MaterialFood("seed");
      bone = new Material("bone");
      paper = (new Material("paper")).setFlammability(true, true, true);
      manure = (new Material("manure")).setFlammability(true, false, true);
      coal = (new Material("coal")).setFlammability(true, true, true);
      lapis_lazuli = (new Material("lapis_lazuli")).setRockyMineral(true);
      feather = (new Material("feather")).setFlammability(true, false, true).setHarmedByPepsin();
      gunpowder = (new Material("gunpowder")).setFlammability(true, false, true);
      slime = (new Material("slime")).setFlammability(true, false, true);
      glowstone = (new Material("glowstone")).setRockyMineral();
      dye = (new Material("dye")).setFlammability(true, false, true);
      ender_pearl = new Material("ender_pearl");
      blaze = new Material("blaze");
      silk = (new Material("silk")).setFlammability(true, false, true);
      frags = new Material("frags");
      vinyl = (new Material("vinyl")).setFlammability(true, false, true);

      for(int i = 0; i < materials.length; ++i) {
         Material material = materials[i];
         if (material != null) {
            material.initialize();
         }
      }

   }
}
