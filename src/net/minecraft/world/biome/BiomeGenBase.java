package net.minecraft.world.biome;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityBlob;
import net.minecraft.entity.EntityBoneLord;
import net.minecraft.entity.EntityClayGolem;
import net.minecraft.entity.EntityDemonSpider;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityInvisibleStalker;
import net.minecraft.entity.EntityJelly;
import net.minecraft.entity.EntityNightwing;
import net.minecraft.entity.EntityOoze;
import net.minecraft.entity.EntityPhaseSpider;
import net.minecraft.entity.EntityPudding;
import net.minecraft.entity.EntityRevenant;
import net.minecraft.entity.EntityShadow;
import net.minecraft.entity.EntityVampireBat;
import net.minecraft.entity.EntityWight;
import net.minecraft.entity.EntityWoodSpider;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.BiomeGenUnderworld;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.DeferredBiomeDecorator;

public abstract class BiomeGenBase {
   public static final BiomeGenBase[] biomeList = new BiomeGenBase[256];
   public static final BiomeGenBase ocean = (new BiomeGenOcean(0)).setColor(112).setBiomeName("Ocean").setMinMaxHeight(-1.0F, 0.4F);
   public static final BiomeGenBase plains = (new BiomeGenPlains(1)).setColor(9286496).setBiomeName("Plains").setTemperatureRainfall(0.8F, 0.4F);
   public static final BiomeGenBase desert = (new BiomeGenDesert(2)).setColor(16421912).setBiomeName("Desert").setDisableRain().setTemperatureRainfall(1.6F, 0.0F).setMinMaxHeight(0.1F, 0.2F);
   public static final BiomeGenBase extremeHills = (new BiomeGenHills(3)).setColor(6316128).setBiomeName("Extreme Hills").setMinMaxHeight(0.3F, 1.5F).setTemperatureRainfall(0.4F, 0.3F);
   public static final BiomeGenBase forest = (new BiomeGenForest(4)).setColor(353825).setBiomeName("Forest").func_76733_a(5159473).setTemperatureRainfall(0.7F, 0.8F);
   public static final BiomeGenBase taiga = (new BiomeGenTaiga(5)).setColor(747097).setBiomeName("Taiga").func_76733_a(5159473).setEnableSnow().setTemperatureRainfall(0.05F, 0.8F).setMinMaxHeight(0.1F, 0.4F);
   public static final BiomeGenBase swampland = (new BiomeGenSwamp(6)).setColor(522674).setBiomeName("Swampland").func_76733_a(9154376).setMinMaxHeight(-0.2F, 0.1F).setTemperatureRainfall(0.8F, 0.9F);
   public static final BiomeGenBase river = (new BiomeGenRiver(7)).setColor(255).setBiomeName("River").setMinMaxHeight(-0.5F, 0.0F);
   public static final BiomeGenBase hell = (new BiomeGenHell(8)).setColor(16711680).setBiomeName("Hell").setDisableRain().setTemperatureRainfall(2.0F, 0.0F);
   public static final BiomeGenBase underworld = (new BiomeGenUnderworld(26)).setColor(16711680).setBiomeName("Underworld").setDisableRain().setTemperatureRainfall(1.0F, 0.0F);
   public static final BiomeGenBase sky = (new BiomeGenEnd(9)).setColor(8421631).setBiomeName("Sky").setDisableRain();
   public static final BiomeGenBase frozenOcean = (new BiomeGenOcean(10)).setColor(9474208).setBiomeName("FrozenOcean").setEnableSnow().setMinMaxHeight(-1.0F, 0.5F).setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase frozenRiver = (new BiomeGenRiver(11)).setColor(10526975).setBiomeName("FrozenRiver").setEnableSnow().setMinMaxHeight(-0.5F, 0.0F).setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase icePlains = (new BiomeGenSnow(12)).setColor(16777215).setBiomeName("Ice Plains").setEnableSnow().setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase iceMountains = (new BiomeGenSnow(13)).setColor(10526880).setBiomeName("Ice Mountains").setEnableSnow().setMinMaxHeight(0.3F, 1.3F).setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase beach = (new BiomeGenBeach(16)).setColor(16440917).setBiomeName("Beach").setTemperatureRainfall(1.0F, 0.4F).setMinMaxHeight(0.0F, 0.1F);
   public static final BiomeGenBase desertHills = (new BiomeGenDesert(17)).setColor(13786898).setBiomeName("DesertHills").setDisableRain().setTemperatureRainfall(1.6F, 0.0F).setMinMaxHeight(0.3F, 0.8F);
   public static final BiomeGenBase forestHills = (new BiomeGenForest(18)).setColor(2250012).setBiomeName("ForestHills").func_76733_a(5159473).setTemperatureRainfall(0.7F, 0.8F).setMinMaxHeight(0.3F, 0.7F);
   public static final BiomeGenBase taigaHills = (new BiomeGenTaiga(19)).setColor(1456435).setBiomeName("TaigaHills").setEnableSnow().func_76733_a(5159473).setTemperatureRainfall(0.05F, 0.8F).setMinMaxHeight(0.3F, 0.8F);
   public static final BiomeGenBase extremeHillsEdge = (new BiomeGenHills(20)).setColor(7501978).setBiomeName("Extreme Hills Edge").setMinMaxHeight(0.2F, 0.8F).setTemperatureRainfall(0.4F, 0.3F);
   public static final BiomeGenBase jungle = (new BiomeGenJungle(21)).setColor(5470985).setBiomeName("Jungle").func_76733_a(5470985).setTemperatureRainfall(1.2F, 0.9F).setMinMaxHeight(0.2F, 0.4F);
   public static final BiomeGenBase jungleHills = (new BiomeGenJungle(22)).setColor(2900485).setBiomeName("JungleHills").func_76733_a(5470985).setTemperatureRainfall(1.2F, 0.9F).setMinMaxHeight(1.8F, 0.5F);
   public static final BiomeGenBase desertRiver = (new BiomeGenRiver(23)).setColor(255).setBiomeName("DesertRiver").setMinMaxHeight(-0.5F, 0.0F).setDisableRain().setTemperatureRainfall(1.4F, 0.0F);
   public static final BiomeGenBase jungleRiver = (new BiomeGenRiver(24)).setColor(255).setBiomeName("JungleRiver").setMinMaxHeight(-0.5F, 0.0F).setTemperatureRainfall(1.0F, 0.9F);
   public static final BiomeGenBase swampRiver = (new BiomeGenRiver(25)).setColor(255).setBiomeName("SwampRiver").setMinMaxHeight(-0.5F, 0.0F).setTemperatureRainfall(0.8F, 0.9F);
   public String biomeName;
   public int color;
   public byte topBlock;
   public byte fillerBlock;
   public int field_76754_C;
   public float minHeight;
   public float maxHeight;
   public float temperature;
   public float rainfall;
   public int waterColorMultiplier;
   public BiomeDecorator theBiomeDecorator;
   protected List spawnableMonsterList;
   protected List spawnableCreatureList;
   protected List spawnableWaterCreatureList;
   protected List spawnableCaveCreatureList;
   private boolean enableSnow;
   private boolean enableRain;
   public final int biomeID;
   protected WorldGenTrees worldGeneratorTrees;
   protected WorldGenBigTree worldGeneratorBigTree;
   protected WorldGenForest worldGeneratorForest;
   protected WorldGenSwamp worldGeneratorSwamp;

   public BiomeGenBase(int par1)
   {
      this(par1, true);
   }

   public BiomeGenBase(int par1, boolean register) {
      this.topBlock = (byte)Block.grass.blockID;
      this.fillerBlock = (byte)Block.dirt.blockID;
      this.field_76754_C = 5169201;
      this.minHeight = 0.1F;
      this.maxHeight = 0.3F;
      this.temperature = 0.5F;
      this.rainfall = 0.5F;
      this.waterColorMultiplier = 16777215;
      this.spawnableMonsterList = new ArrayList();
      this.spawnableCreatureList = new ArrayList();
      this.spawnableWaterCreatureList = new ArrayList();
      this.spawnableCaveCreatureList = new ArrayList();
      this.enableRain = true;
      this.worldGeneratorTrees = new WorldGenTrees(false);
      this.worldGeneratorBigTree = new WorldGenBigTree(false);
      this.worldGeneratorForest = new WorldGenForest(false);
      this.worldGeneratorSwamp = new WorldGenSwamp();
      this.biomeID = par1;
      if (register)
      biomeList[par1] = this;
      this.theBiomeDecorator = this.createBiomeDecorator();
      this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 10, 1, 1));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10, 1, 1));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10, 1, 1));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 80, 1, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 100, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 100, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 100, 1, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 100, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 4));
      this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
      this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 100, 8, 8));
      this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityVampireBat.class, 20, 8, 8));
      this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityNightwing.class, 4, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityGhoul.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityInvisibleStalker.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityDemonSpider.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityHellhound.class, 10, 1, 2));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityWoodSpider.class, 20, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityShadow.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityRevenant.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityEarthElemental.class, 10, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityJelly.class, 30, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityBlob.class, 30, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityOoze.class, 20, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityPudding.class, 30, 1, 4));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityClayGolem.class, 50, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityBoneLord.class, 5, 1, 1));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityPhaseSpider.class, 5, 1, 4));
   }

   protected BiomeDecorator createBiomeDecorator() {
      return getModdedBiomeDecorator(new BiomeDecorator(this));
   }

   private BiomeGenBase setTemperatureRainfall(float par1, float par2) {
      if (par1 > 0.1F && par1 < 0.2F) {
         throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
      } else {
         this.temperature = par1;
         this.rainfall = par2;
         return this;
      }
   }

   public boolean hasRainfall() {
      return this.enableRain && this.rainfall > 0.0F;
   }

   private BiomeGenBase setMinMaxHeight(float par1, float par2) {
      this.minHeight = par1;
      this.maxHeight = par2;
      return this;
   }

   private BiomeGenBase setDisableRain() {
      this.enableRain = false;
      return this;
   }

   public WorldGenerator getRandomWorldGenForTrees(Random par1Random) {
      return (WorldGenerator)(par1Random.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees);
   }

   public WorldGenerator getRandomWorldGenForGrass(Random par1Random) {
      return new WorldGenTallGrass(Block.tallGrass.blockID, 1);
   }

   protected BiomeGenBase setEnableSnow() {
      this.enableSnow = true;
      return this;
   }

   protected BiomeGenBase setBiomeName(String par1Str) {
      this.biomeName = par1Str;
      return this;
   }

   protected BiomeGenBase func_76733_a(int par1) {
      this.field_76754_C = par1;
      return this;
   }

   protected BiomeGenBase setColor(int par1) {
      this.color = par1;
      return this;
   }

   public int getSkyColorByTemp(float par1) {
      par1 /= 3.0F;
      if (par1 < -1.0F) {
         par1 = -1.0F;
      }

      if (par1 > 1.0F) {
         par1 = 1.0F;
      }

      return Color.getHSBColor(0.62222224F - par1 * 0.05F, 0.5F + par1 * 0.1F, 1.0F).getRGB();
   }

   public List getSpawnableList(EnumCreatureType par1EnumCreatureType) {
      return par1EnumCreatureType == EnumCreatureType.monster ? this.spawnableMonsterList : (par1EnumCreatureType == EnumCreatureType.animal ? this.spawnableCreatureList : (par1EnumCreatureType == EnumCreatureType.aquatic ? this.spawnableWaterCreatureList : (par1EnumCreatureType == EnumCreatureType.ambient ? this.spawnableCaveCreatureList : null)));
   }

   public boolean getEnableSnow() {
      return this.enableSnow;
   }

   public boolean canSpawnLightningBolt(boolean is_blood_moon) {
      return this.enableSnow ? false : this.enableRain || is_blood_moon;
   }

   public boolean isHighHumidity() {
      return this.rainfall > 0.85F;
   }

   public float getSpawningChance() {
      return this.isFreezing() ? 0.05F : 0.1F;
   }

   public final int getIntRainfall() {
      return (int)(this.rainfall * 65536.0F);
   }

   public final int getIntTemperature() {
      return (int)(this.temperature * 65536.0F);
   }

   public final float getFloatRainfall() {
      return this.rainfall;
   }

   public final float getFloatTemperature() {
      return this.temperature;
   }

   public void generateSilverfishBlocks(World par1World, Random par2Random, int par3, int par4) {
      int max_height = 64;
      int sum_height = 0;

      int dx;
      int num_vein_placement_attempts;
      for(dx = 0; dx < 16; ++dx) {
         for(num_vein_placement_attempts = 0; num_vein_placement_attempts < 16; ++num_vein_placement_attempts) {
            int height = par1World.getHeightValue(par3 + dx, par4 + num_vein_placement_attempts);
            if (height > max_height) {
               max_height = height;
            }

            sum_height += height;
         }
      }

      dx = Math.max((sum_height - 8192) / 256 / 8, 2);
      num_vein_placement_attempts = dx - 1;
      WorldGenerator silverfish_generator = new WorldGenMinable(Block.silverfish.blockID, dx);

      for(int var5 = 0; var5 < num_vein_placement_attempts; ++var5) {
         int var6 = par3 + par2Random.nextInt(16);
         int var7 = par2Random.nextInt(max_height);
         int var8 = par4 + par2Random.nextInt(16);
         silverfish_generator.generate(par1World, par2Random, var6, var7, var8);
      }

   }

   public void decorate(World par1World, Random par2Random, int par3, int par4) {
      this.theBiomeDecorator.decorate(par1World, par2Random, par3, par4);
      if (par1World.isOverworld()) {
         this.generateSilverfishBlocks(par1World, par2Random, par3, par4);
      }

   }

   public int getBiomeGrassColor() {
      double var1 = (double)MathHelper.clamp_float(this.getFloatTemperature(), 0.0F, 1.0F);
      double var3 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
      return getModdedBiomeGrassColor(ColorizerGrass.getGrassColor(var1, var3));
   }

   public int getBiomeFoliageColor() {
      double var1 = (double)MathHelper.clamp_float(this.getFloatTemperature(), 0.0F, 1.0F);
      double var3 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
      return getModdedBiomeFoliageColor(ColorizerFoliage.getFoliageColor(var1, var3));
   }

   public BiomeDecorator getModdedBiomeDecorator(BiomeDecorator original)
   {
      return new DeferredBiomeDecorator(this, original);
   }

   public int getWaterColorMultiplier()
   {
      BiomeEvent.GetWaterColor event = new BiomeEvent.GetWaterColor(this, waterColorMultiplier);
      MinecraftForge.EVENT_BUS.post(event);
      return event.newColor;
   }

   public int getModdedBiomeGrassColor(int original)
   {
      BiomeEvent.GetGrassColor event = new BiomeEvent.GetGrassColor(this, original);
      MinecraftForge.EVENT_BUS.post(event);
      return event.newColor;
   }

   public int getModdedBiomeFoliageColor(int original)
   {
      BiomeEvent.GetFoliageColor event = new BiomeEvent.GetFoliageColor(this, original);
      MinecraftForge.EVENT_BUS.post(event);
      return event.newColor;
   }

   public boolean isFreezing() {
      return this.temperature <= 0.15F;
   }

   public boolean isJungleBiome() {
      return this == jungle || this == jungleHills || this == jungleRiver;
   }

   public boolean isDesertBiome() {
      return this == desert || this == desertHills || this == desertRiver;
   }

   public boolean isSwampBiome() {
      return this == swampland || this == swampRiver;
   }

   public boolean isHillyOrMountainous() {
      return this == extremeHills || this == iceMountains || this == desertHills || this == forestHills || this == taigaHills || this == extremeHillsEdge || this == jungleHills;
   }

   public boolean canHaveMineshafts() {
      return this.isHillyOrMountainous();
   }

   public void removeEntityFromSpawnableList(List list, Class _class) {
      Iterator i = list.iterator();

      while(i.hasNext()) {
         SpawnListEntry entry = (SpawnListEntry)i.next();
         if (entry.entityClass == _class) {
            i.remove();
         }
      }

   }

   public void removeEntityFromSpawnableLists(Class _class) {
      this.removeEntityFromSpawnableList(this.spawnableMonsterList, _class);
      this.removeEntityFromSpawnableList(this.spawnableCreatureList, _class);
      this.removeEntityFromSpawnableList(this.spawnableWaterCreatureList, _class);
      this.removeEntityFromSpawnableList(this.spawnableCaveCreatureList, _class);
   }
}
