package net.minecraft.world;

import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateFlatWorld;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class WorldType {
   public static final WorldType[] worldTypes = new WorldType[16];
   public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
   public static final WorldType FLAT = new WorldType(1, "flat");
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
   private final int worldTypeId;
   private final String worldType;
   private final int generatorVersion;
   private boolean canBeCreated;
   private boolean isWorldTypeVersioned;

   protected BiomeGenBase[] biomesForWorldType;

   public static final BiomeGenBase[] base11Biomes =
           new BiomeGenBase[] {BiomeGenBase.ocean, BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.swampland,
                   BiomeGenBase.plains, BiomeGenBase.taiga};
   public static final BiomeGenBase[] base12Biomes =
           ObjectArrays.concat(base11Biomes, BiomeGenBase.jungle);

   private WorldType(int var1, String var2) {
      this(var1, var2, 0);
   }

   private WorldType(int var1, String var2, int var3) {
      this.worldType = var2;
      this.generatorVersion = var3;
      this.canBeCreated = true;
      this.worldTypeId = var1;
      worldTypes[var1] = this;

       if (var1 == 8) {
           biomesForWorldType = base11Biomes;
       } else {
           biomesForWorldType = base12Biomes;
       }
   }


   public double getHorizon(World world)
   {
      return this == FLAT ? 0.0D : 63.0D;
   }

   public String getWorldTypeName() {
      return this.worldType;
   }

   public String getTranslateName() {
      return "generator." + this.worldType;
   }

   public int getGeneratorVersion() {
      return this.generatorVersion;
   }

   public WorldType getWorldTypeForGeneratorVersion(int var1) {
      return this == DEFAULT && var1 == 0 ? DEFAULT_1_1 : this;
   }

   private WorldType setCanBeCreated(boolean var1) {
      this.canBeCreated = var1;
      return this;
   }

   public boolean getCanBeCreated() {
      return this.canBeCreated;
   }

   private WorldType setVersioned() {
      this.isWorldTypeVersioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.isWorldTypeVersioned;
   }

   public static WorldType parseWorldType(String var0) {
       for (WorldType type : worldTypes) {
           if (type != null && type.worldType.equalsIgnoreCase(var0)) {
               return type;
           }
       }

      return null;
   }

   public int getWorldTypeID() {
      return this.worldTypeId;
   }

   /**
    * Gets the spawn fuzz for players who join the world.
    * Useful for void world types.
    * @return Fuzz for entity initial spawn in blocks.
    */
   public int getSpawnFuzz()
   {
      return 20;
   }

   /**
    * Called when the 'Customize' button is pressed on world creation GUI
    * @param instance The minecraft instance
    * @param guiCreateWorld the createworld GUI
    */

   public void onCustomizeButton(Minecraft instance, GuiCreateWorld guiCreateWorld)
   {
      if (this == FLAT)
      {
         instance.displayGuiScreen(new GuiCreateFlatWorld(guiCreateWorld, guiCreateWorld.generatorOptionsToUse));
      }
   }

   /*
    * Should world creation GUI show 'Customize' button for this world type?
    * @return if this world type has customization parameters
    */
   public boolean isCustomizable()
   {
      return this == FLAT;
   }


   /**
    * the y level at which clouds are rendered.
    */
   public float getCloudHeight()
   {
      return 128.0F;
   }


   public WorldChunkManager getChunkManager(World world)
   {
      if (this == FLAT)
      {
         FlatGeneratorInfo flatgeneratorinfo = FlatGeneratorInfo.createFlatGeneratorFromString(world.getWorldInfo().getGeneratorOptions());
         return new WorldChunkManagerHell(BiomeGenBase.biomeList[flatgeneratorinfo.getBiome()], 0.5F, 0.5F);
      }
      else
      {
         return new WorldChunkManager(world);
      }
   }

   public IChunkProvider getChunkGenerator(World world, String generatorOptions)
   {
      return this == WorldType.FLAT ?
              new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions) :
              new ChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
   }

   public int getMinimumSpawnHeight(World world)
   {
      return this == FLAT ? 4 : 64;
   }

   public boolean hasVoidParticles(boolean flag)
   {
      return this != FLAT && !flag;
   }

   public double voidFadeMagnitude()
   {
      return this == FLAT ? 1.0D : 0.03125D;
   }

   public BiomeGenBase[] getBiomesForWorldType() {
      return biomesForWorldType;
   }

   public void addNewBiome(BiomeGenBase biome)
   {
      Set<BiomeGenBase> newBiomesForWorld = Sets.newLinkedHashSet(Arrays.asList(biomesForWorldType));
      newBiomesForWorld.add(biome);
      biomesForWorldType = newBiomesForWorld.toArray(new BiomeGenBase[0]);
   }

   public void removeBiome(BiomeGenBase biome)
   {
      Set<BiomeGenBase> newBiomesForWorld = Sets.newLinkedHashSet(Arrays.asList(biomesForWorldType));
      newBiomesForWorld.remove(biome);
      biomesForWorldType = newBiomesForWorld.toArray(new BiomeGenBase[0]);
   }

   public boolean handleSlimeSpawnReduction(Random random, World world)
   {
      return this == FLAT ? random.nextInt(4) != 1 : false;
   }

   /**
    * Called when 'Create New World' button is pressed before starting game
    */
   public void onGUICreateWorldPress() { }
}
