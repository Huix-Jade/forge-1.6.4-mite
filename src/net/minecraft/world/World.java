package net.minecraft.world;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockInfo;
import net.minecraft.block.IBlockWithPartner;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RNG;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPendingEntry;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.logging.ILogAgent;
import net.minecraft.mite.MITEConstant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.SignalData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.profiler.Profiler;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.raycast.RaycastPolicies;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Debug;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.Facing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.LongHashMapEntry;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TentativeBoundingBox;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPostField;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess {
   public boolean scheduledUpdatesAreImmediate;
   public List loadedEntityList = new ArrayList();
   public List unloadedEntityList = new ArrayList();
   public List loadedTileEntityList = new ArrayList();
   private List addedTileEntityList = new ArrayList();
   private List entityRemoval = new ArrayList();
   public List playerEntities = new ArrayList();
   public List weatherEffects = new ArrayList();
   private long cloudColour = 16777215L;
   public int skylightSubtracted;
   public int skylight_subtracted_ignoring_rain_and_thunder;
   protected int updateLCG = (new Random()).nextInt();
   protected final int DIST_HASH_MAGIC = 1013904223;
   protected float prevRainingStrength;
   protected float rainingStrength;
   protected float prevThunderingStrength;
   protected float thunderingStrength;
   public int lastLightningBolt;
   public int difficultySetting;
   public Random rand = new Random();
   public final WorldProvider provider;
   protected List worldAccesses = new ArrayList();
   public IChunkProvider chunkProvider;
   protected final ISaveHandler saveHandler;
   public WorldInfo worldInfo;
   public boolean findingSpawnPoint;
   public MapStorage mapStorage;
   public final VillageCollection villageCollectionObj;
   protected final VillageSiege villageSiegeObj = new VillageSiege(this);
   public final Profiler theProfiler;
   private final Vec3Pool vecPool = new Vec3Pool(300, 2000);
   private final Calendar theCalendar = Calendar.getInstance();
   protected Scoreboard worldScoreboard = new Scoreboard();
   private final ILogAgent worldLogAgent;
   private ArrayList collidingBoundingBoxes = new ArrayList();
   private boolean scanningTileEntities;
   protected boolean spawnHostileMobs = true;
   protected boolean spawnPeacefulMobs = true;
   public Set activeChunkSet = new HashSet();
   private int ambientTickCountdown;
   int[] lightUpdateBlockList;
   public final boolean isRemote;
   public boolean decorating;
   public List pending_entity_spawns = new ArrayList();
   public boolean ignore_rain_and_thunder_for_next_BLV;
   private int times_checkLightingOfRandomBlockInView_called;
   public long total_time;
   private final boolean is_overworld;
   private final boolean is_underworld;
   private final boolean is_nether;
   private final boolean is_the_end;
   private final boolean has_skylight;
   private final int block_domain_radius;
   public final int min_chunk_xz;
   public final int max_chunk_xz;
   public final int min_block_xz;
   public final int max_block_xz;
   public final double min_entity_pos_xz;
   public final double max_entity_pos_xz;
   public final int runegate_mithril_domain_radius;
   public final int runegate_adamantium_domain_radius;
   public float biome_temperature_transition_for_sky_color = Float.NaN;
   private List weather_events_for_today;
   private int weather_events_for_day;
   private long tick_flags_last_updated = -1L;
   private WeatherEvent current_weather_event;
   private boolean is_precipitating;
   private boolean is_storming;
   private boolean is_harvest_moon_24_hour_period;
   private boolean is_harvest_moon_day;
   private boolean is_harvest_moon_night;
   private boolean is_blood_moon_24_hour_period;
   private boolean is_blood_moon_day;
   private boolean is_blood_moon_night;
   private boolean is_blue_moon_24_hour_period;
   private boolean is_blue_moon_day;
   private boolean is_blue_moon_night;
   private boolean is_moon_dog_24_hour_period;
   private boolean is_moon_dog_day;
   private boolean is_moon_dog_night;
   public static final int DIMENSION_ID_UNDERWORLD = -2;
   public static final int DIMENSION_ID_NETHER = -1;
   public static final int DIMENSION_ID_OVERWORLD = 0;
   public static final int DIMENSION_ID_THE_END = 1;
   private final ChunkPostField mycelium_posts;
   public static final int Y_OFFSET_FOR_UNDERWORLD = 120;
   public final int underworld_y_offset;
   private final Block bottom_block;
   private final int bottom_block_metadata;
   public HashMap pending_sand_falls;

   public BiomeGenBase getBiomeGenForCoords(int par1, int par2) {
      if (this.blockExists(par1, 0, par2)) {
         Chunk var3 = this.getChunkFromBlockCoords(par1, par2);
         if (var3 != null) {
            return var3.getBiomeGenForWorldCoords(par1 & 15, par2 & 15, this.provider.worldChunkMgr);
         }
      }

      return this.provider.worldChunkMgr.getBiomeGenAt(par1, par2);
   }

   public WorldChunkManager getWorldChunkManager() {
      return this.provider.worldChunkMgr;
   }

   private int getNextAmbientTickCountdown(boolean is_first_count_down) {
      return is_first_count_down ? this.rand.nextInt(12000) : this.rand.nextInt(12000) + 6000;
   }

   public World(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent, long world_creation_time, long total_world_time) {
      if (!(this instanceof WorldClient)) {
         Minecraft.setErrorMessage("World: This constructor must only be invoked by WorldClient!");
      }

      this.isRemote = this instanceof WorldClient;
      this.ambientTickCountdown = this.getNextAmbientTickCountdown(true);
      this.lightUpdateBlockList = new int['Ꝼ'];
      this.saveHandler = par1ISaveHandler;
      this.theProfiler = par5Profiler;
      this.worldInfo = new WorldInfo(par4WorldSettings, par2Str);
      this.worldInfo.setDimensionId(par3WorldProvider.dimensionId);
      this.provider = par3WorldProvider;
      this.worldInfo.setWorldCreationTime(world_creation_time);
      this.is_underworld = this.provider.isUnderworld();
      this.is_nether = this.provider.isTheNether();
      this.is_overworld = this.provider.isSurfaceWorld();
      this.is_the_end = this.provider.isTheEnd();
      this.bottom_block = (Block)(this.isOverworld() ? Block.bedrock : (!this.isUnderworld() && !this.isTheNether() ? null : Block.mantleOrCore));
      this.bottom_block_metadata = this.isUnderworld() ? 0 : (this.isTheNether() ? 1 : -1);
      this.has_skylight = !this.provider.hasNoSky && this.is_overworld;
      this.block_domain_radius = this.provider.getBlockDomainRadius();
      this.min_chunk_xz = -this.block_domain_radius / 16;
      this.max_chunk_xz = this.block_domain_radius / 16 - 1;
      this.min_block_xz = -this.block_domain_radius;
      this.max_block_xz = this.block_domain_radius - 1;
      this.min_entity_pos_xz = (double)this.min_block_xz;
      this.max_entity_pos_xz = (double)this.max_block_xz + 0.9999;
      this.runegate_adamantium_domain_radius = Math.min(this.block_domain_radius / 2, 40000);
      this.runegate_mithril_domain_radius = this.runegate_adamantium_domain_radius / 8;
      this.validateDomainValues();
      this.worldInfo.setTotalWorldTime(total_world_time, this);
      this.mapStorage = new MapStorage(par1ISaveHandler);
      this.worldLogAgent = par6ILogAgent;
      VillageCollection var7 = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");
      if (var7 == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData("villages", this.villageCollectionObj);
      } else {
         this.villageCollectionObj = var7;
         this.villageCollectionObj.func_82566_a(this);
      }

      par3WorldProvider.registerWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.calculateInitialSkylight();
      this._calculateInitialWeather();
      RNG.init(this);
      this.mycelium_posts = this.createMyceliumPostField();
      this.underworld_y_offset = this.is_underworld ? 120 : 0;
   }

   public World(ISaveHandler par1ISaveHandler, String par2Str, WorldSettings par3WorldSettings, WorldProvider par4WorldProvider, Profiler par5Profiler, ILogAgent par6ILogAgent) {
      if (!(this instanceof WorldServer)) {
         Minecraft.setErrorMessage("World: This constructor must only be invoked by WorldServer!");
      }

      this.getAsWorldServer().instantiateScheduledBlockChangesList();
      this.isRemote = this instanceof WorldClient;
      this.ambientTickCountdown = this.getNextAmbientTickCountdown(true);
      this.lightUpdateBlockList = new int['Ꝼ'];
      this.saveHandler = par1ISaveHandler;
      this.theProfiler = par5Profiler;
      this.mapStorage = new MapStorage(par1ISaveHandler);
      this.worldLogAgent = par6ILogAgent;
      this.worldInfo = par1ISaveHandler.loadWorldInfo();
      if (par4WorldProvider != null) {
         this.provider = par4WorldProvider;
      } else if (this.worldInfo != null && this.worldInfo.getVanillaDimension() != 0) {
         this.provider = WorldProvider.getProviderForDimension(this.worldInfo.getVanillaDimension());
      } else {
         this.provider = WorldProvider.getProviderForDimension(0);
      }

      this.is_underworld = this.provider.isUnderworld();
      this.is_nether = this.provider.isTheNether();
      this.is_overworld = this.provider.isSurfaceWorld();
      this.is_the_end = this.provider.isTheEnd();
      this.bottom_block = (Block)(this.isOverworld() ? Block.bedrock : (!this.isUnderworld() && !this.isTheNether() ? null : Block.mantleOrCore));
      this.bottom_block_metadata = this.isUnderworld() ? 0 : (this.isTheNether() ? 1 : -1);
      this.has_skylight = !this.provider.hasNoSky && this.is_overworld;
      this.block_domain_radius = this.provider.getBlockDomainRadius();
      this.min_chunk_xz = -this.block_domain_radius / 16;
      this.max_chunk_xz = this.block_domain_radius / 16 - 1;
      this.min_block_xz = -this.block_domain_radius;
      this.max_block_xz = this.block_domain_radius - 1;
      this.min_entity_pos_xz = (double)this.min_block_xz;
      this.max_entity_pos_xz = (double)this.max_block_xz + 0.9999;
      this.runegate_adamantium_domain_radius = Math.min(this.block_domain_radius / 2, 40000);
      this.runegate_mithril_domain_radius = this.runegate_adamantium_domain_radius / 8;
      this.validateDomainValues();
      if (this.worldInfo == null) {
         this.worldInfo = new WorldInfo(par3WorldSettings, par2Str);
      } else {
         this.worldInfo.setWorldName(par2Str);
      }

      this.provider.registerWorld(this);
      this.updateTickFlags();
      this.chunkProvider = this.createChunkProvider();
      if (!this.worldInfo.isInitialized()) {
         try {
            this.initialize(par3WorldSettings);
         } catch (Throwable var11) {
            CrashReport var8 = CrashReport.makeCrashReport(var11, "Exception initializing level");

            try {
               this.addWorldInfoToCrashReport(var8);
            } catch (Throwable var10) {
            }

            throw new ReportedException(var8);
         }

         this.worldInfo.setServerInitialized(true);
      }

      VillageCollection var7 = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");
      if (var7 == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData("villages", this.villageCollectionObj);
      } else {
         this.villageCollectionObj = var7;
         this.villageCollectionObj.func_82566_a(this);
      }

      if (this.worldInfo != null) {
         this.total_time = this.worldInfo.getWorldTotalTime(this.provider.dimensionId);
      }

      this.calculateInitialSkylight();
      this._calculateInitialWeather();
      RNG.init(this);
      this.mycelium_posts = this.createMyceliumPostField();
      this.underworld_y_offset = this.is_underworld ? 120 : 0;
   }

   private ChunkPostField createMyceliumPostField() {
      return this.isUnderworld() ? new ChunkPostField(1, this.getHashedSeed(), 24, 0.0625F) : null;
   }

   public ChunkPostField getMyceliumPostField() {
      return this.mycelium_posts;
   }

   protected abstract IChunkProvider createChunkProvider();

   protected void initialize(WorldSettings par1WorldSettings) {
      this.worldInfo.setServerInitialized(true);
   }

   public void setSpawnLocation() {
      this.setSpawnLocation(8, 64, 8);
   }

   public int getFirstUncoveredBlock(int par1, int par2) {
      int var3;
      for(var3 = 63; !this.isAirBlock(par1, var3 + 1, par2); ++var3) {
      }

      return this.getBlockId(par1, var3, par2);
   }

   public float getBlockHardness(int x, int y, int z) {
      return Block.blocksList[this.getBlockId(x, y, z)].getBlockHardness(this.getBlockMetadata(x, y, z));
   }

   public final int getBlockId(int par1, int par2, int par3) {
      if (!this.isWithinBlockDomain(par1, par3)) {
         return 0;
      } else if ((par2 & -256) == 0) {
         Chunk var4 = null;

         try {
            int var7_2;
            LongHashMapEntry var4_1;
            LongHashMap lhm;
            long key;
            if (!this.isRemote) {
               ChunkProviderServer cps = (ChunkProviderServer)this.chunkProvider;
               lhm = cps.loadedChunkHashMap;
               key = (long)(par1 >> 4) & 4294967295L | ((long)(par3 >> 4) & 4294967295L) << 32;
               var7_2 = (int)(key ^ key >>> 32);
               var7_2 ^= var7_2 >>> 20 ^ var7_2 >>> 12;

               for(var4_1 = lhm.hashArray[(var7_2 ^ var7_2 >>> 7 ^ var7_2 >>> 4) & lhm.hashArray.length - 1]; var4_1 != null; var4_1 = var4_1.nextEntry) {
                  if (var4_1.key == key) {
                     var4 = (Chunk)var4_1.value;
                     break;
                  }
               }

               if (var4 == null) {
                  var4 = !cps.worldObj.findingSpawnPoint && !cps.loadChunkOnProvideRequest ? cps.defaultEmptyChunk : cps.loadChunk(par1 >> 4, par3 >> 4);
               }
            } else {
               ChunkProviderClient cps = (ChunkProviderClient)this.chunkProvider;
               lhm = cps.chunkMapping;
               key = (long)(par1 >> 4) & 4294967295L | ((long)(par3 >> 4) & 4294967295L) << 32;
               var7_2 = (int)(key ^ key >>> 32);
               var7_2 ^= var7_2 >>> 20 ^ var7_2 >>> 12;
               var7_2 = var7_2 ^ var7_2 >>> 7 ^ var7_2 >>> 4;

               for(var4_1 = lhm.hashArray[var7_2 & lhm.hashArray.length - 1]; var4_1 != null; var4_1 = var4_1.nextEntry) {
                  if (var4_1.key == key) {
                     var4 = (Chunk)var4_1.value;
                     break;
                  }
               }

               if (var4 == null) {
                  var4 = cps.blankChunk;
               }
            }

            if (var4.isEmpty()) {
               return 0;
            } else {
               ExtendedBlockStorage extended_block_storage = var4.storageArrays[par2 >> 4];
               if (extended_block_storage == null) {
                  return 0;
               } else {
                  int par1_and_15 = par1 & 15;
                  int par2_and_15 = par2 & 15;
                  int par3_and_15 = par3 & 15;
                  if (extended_block_storage.blockMSBArray == null) {
                     return extended_block_storage.blockLSBArray[par2_and_15 << 8 | par3_and_15 << 4 | par1_and_15] & 255;
                  } else {
                     var7_2 = par2_and_15 << 8 | par3_and_15 << 4 | par1_and_15;
                     return (var7_2 & 1) == 0 ? extended_block_storage.blockMSBArray.data[var7_2 >> 1] & 15 : extended_block_storage.blockMSBArray.data[var7_2 >> 1] >> 4 & 15;
                  }
               }
            }
         } catch (Throwable var11) {
            Throwable var8 = var11;
            CrashReport var6 = CrashReport.makeCrashReport(var8, "Exception getting block type in world");
            CrashReportCategory var7 = var6.makeCategory("Requested block coordinates");
            var7.addCrashSection("Found chunk", var4 == null);
            var7.addCrashSection("Location", CrashReportCategory.getLocationInfo(par1, par2, par3));
            throw new ReportedException(var6);
         }
      } else {
         return 0;
      }
   }

   public boolean isAdjacentToBlock(double x, double y, double z, int block_id) {
      return this.isAdjacentToBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), block_id);
   }

   public boolean isAdjacentToBlock(int x, int y, int z, int block_id) {
      for(int a = -1; a < 2; ++a) {
         for(int b = -1; b < 2; ++b) {
            for(int c = -1; c < 2; ++c) {
               if (this.getBlockId(x + a, y + b, z + c) == block_id) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final boolean blockTypeIsAbove(Block block, double x, double y, double z) {
      return this.blockTypeIsAbove(block, MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
   }

   public final boolean blockTypeIsAbove(Block block, int x, int y, int z) {
      int block_id = block.blockID;
      int max_y = (Block.lightOpacity[block_id] == 0 ? this.getActualHeight() : this.getHeightValue(x, z)) - 1;

      int id;
      do {
         ++y;
         if (y > max_y) {
            return false;
         }

         id = this.getBlockId(x, y, z);
         if (id == block_id) {
            return true;
         }
      } while(id <= 0);

      return false;
   }

   public static float getDistanceFromDeltas(double dx, double dy, double dz) {
      return MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
   }

   public static double getDistanceSqFromDeltas(double dx, double dy, double dz) {
      return dx * dx + dy * dy + dz * dz;
   }

   public static double getDistanceSqFromDeltas(float dx, float dy, float dz) {
      return (double)(dx * dx + dy * dy + dz * dz);
   }

   public static double getDistanceSqFromDeltas(double dx, double dz) {
      return dx * dx + dz * dz;
   }

   public static double getDistanceFromDeltas(double dx, double dz) {
      return (double)MathHelper.sqrt_double(getDistanceSqFromDeltas(dx, dz));
   }

   public boolean blockTypeIsNearTo(int block_id, double origin_x, double origin_y, double origin_z, int horizontal_radius, int vertical_radius) {
      return this.blockTypeIsNearTo(block_id, MathHelper.floor_double(origin_x), MathHelper.floor_double(origin_y), MathHelper.floor_double(origin_z), horizontal_radius, vertical_radius);
   }

   public boolean blockTypeIsNearTo(int block_id, int origin_x, int origin_y, int origin_z, int horizontal_radius, int vertical_radius) {
      int x = origin_x;
      int y = origin_y;
      int z = origin_z;
      int width = horizontal_radius * 2 + 1;
      int run = 1;
      int min_y = y - vertical_radius;
      int max_y = y + vertical_radius;

      for(y = min_y; y <= max_y; ++y) {
         if (this.getBlockId(x, y, z) == block_id) {
            return true;
         }
      }

      int i;
      while(run < width) {
         for(i = 1; i <= run; ++i) {
            ++x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  return true;
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            ++z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  return true;
               }
            }
         }

         ++run;

         for(i = 1; i <= run; ++i) {
            --x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  return true;
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            --z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  return true;
               }
            }
         }

         ++run;
      }

      for(i = 1; i < run; ++i) {
         ++x;

         for(y = min_y; y <= max_y; ++y) {
            if (this.getBlockId(x, y, z) == block_id) {
               return true;
            }
         }
      }

      return false;
   }

   public double shortestDistanceToBlockType(int block_id, double origin_x, double origin_y, double origin_z, int horizontal_radius, int vertical_radius) {
      int x = MathHelper.floor_double(origin_x);
      int y = MathHelper.floor_double(origin_y);
      int z = MathHelper.floor_double(origin_z);
      origin_x -= 0.5;
      origin_y -= 0.5;
      origin_z -= 0.5;
      int width = horizontal_radius * 2 + 1;
      int height = vertical_radius * 2 + 1;
      int run = 1;
      int min_y = y - vertical_radius;
      int max_y = y + vertical_radius;
      List distances = new ArrayList();

      for(y = min_y; y <= max_y; ++y) {
         if (this.getBlockId(x, y, z) == block_id) {
            distances.add(getDistanceFromDeltas((double)x - origin_x, (double)y - origin_y, (double)z - origin_z));
         }
      }

      int i;
      while(run < width && (run < height || distances.isEmpty())) {
         for(i = 1; i <= run; ++i) {
            ++x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  distances.add(getDistanceFromDeltas((double)x - origin_x, (double)y - origin_y, (double)z - origin_z));
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            ++z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  distances.add(getDistanceFromDeltas((double)x - origin_x, (double)y - origin_y, (double)z - origin_z));
               }
            }
         }

         ++run;

         for(i = 1; i <= run; ++i) {
            --x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  distances.add(getDistanceFromDeltas((double)x - origin_x, (double)y - origin_y, (double)z - origin_z));
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            --z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  distances.add(getDistanceFromDeltas((double)x - origin_x, (double)y - origin_y, (double)z - origin_z));
               }
            }
         }

         ++run;
      }

      for(i = 1; i < run; ++i) {
         ++x;

         for(y = min_y; y <= max_y; ++y) {
            if (this.getBlockId(x, y, z) == block_id) {
               distances.add(getDistanceFromDeltas((double)x - origin_x, (double)y - origin_y, (double)z - origin_z));
            }
         }
      }

      if (distances.isEmpty()) {
         return -1.0;
      } else {
         double least_distance = (double)(Float)distances.get(0);

         for(int k = 1; k < distances.size(); ++k) {
            if ((double)(Float)distances.get(k) < least_distance) {
               least_distance = (double)(Float)distances.get(k);
            }
         }

         return least_distance;
      }
   }

   private int getNearestCandidateIndex(double[] candidate_distance_sq, int num_candidates) {
      int nearest_candidate_index = 0;
      double nearest_candidate_distance_sq = candidate_distance_sq[nearest_candidate_index];

      for(int candidate_index = 1; candidate_index < num_candidates; ++candidate_index) {
         if (candidate_distance_sq[candidate_index] < nearest_candidate_distance_sq) {
            nearest_candidate_index = candidate_index;
            nearest_candidate_distance_sq = candidate_distance_sq[candidate_index];
         }
      }

      return nearest_candidate_index;
   }

   public boolean nearestBlockCoords(double origin_x, double origin_y, double origin_z, int horizontal_radius, int vertical_radius, int block_id, int[] block_coords) {
      return this.nearestBlockCoords((float)origin_x, (float)origin_y, (float)origin_z, horizontal_radius, vertical_radius, block_id, block_coords);
   }

   public boolean nearestBlockCoords(float origin_x, float origin_y, float origin_z, int horizontal_radius, int vertical_radius, int block_id, int[] block_coords) {
      int x = MathHelper.floor_double((double)origin_x);
      int y = MathHelper.floor_double((double)origin_y);
      int z = MathHelper.floor_double((double)origin_z);
      origin_x -= 0.5F;
      origin_y -= 0.5F;
      origin_z -= 0.5F;
      int width = horizontal_radius * 2 + 1;
      int height = vertical_radius * 2 + 1;
      int run = 1;
      int min_y = y - vertical_radius;
      int max_y = y + vertical_radius;
      int max_candidates = 64;
      int[] candidate_x = new int[max_candidates];
      int[] candidate_y = new int[max_candidates];
      int[] candidate_z = new int[max_candidates];
      double[] candidate_distance_sq = new double[max_candidates];
      int next_candidate_index = 0;

      int i;
      for(y = min_y; y <= max_y; ++y) {
         if (this.getBlockId(x, y, z) == block_id) {
            candidate_x[next_candidate_index] = x;
            candidate_y[next_candidate_index] = y;
            candidate_z[next_candidate_index] = z;
            candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
            ++next_candidate_index;
            if (next_candidate_index == max_candidates) {
               i = this.getNearestCandidateIndex(candidate_distance_sq, max_candidates);
               block_coords[0] = candidate_x[i];
               block_coords[1] = candidate_y[i];
               block_coords[2] = candidate_z[i];
               return true;
            }
         }
      }

      int nearest_candidate_index;
      while(run < width && (run < height || next_candidate_index <= 0)) {
         for(i = 1; i <= run; ++i) {
            ++x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     nearest_candidate_index = this.getNearestCandidateIndex(candidate_distance_sq, max_candidates);
                     block_coords[0] = candidate_x[nearest_candidate_index];
                     block_coords[1] = candidate_y[nearest_candidate_index];
                     block_coords[2] = candidate_z[nearest_candidate_index];
                     return true;
                  }
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            ++z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     nearest_candidate_index = this.getNearestCandidateIndex(candidate_distance_sq, max_candidates);
                     block_coords[0] = candidate_x[nearest_candidate_index];
                     block_coords[1] = candidate_y[nearest_candidate_index];
                     block_coords[2] = candidate_z[nearest_candidate_index];
                     return true;
                  }
               }
            }
         }

         ++run;

         for(i = 1; i <= run; ++i) {
            --x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     nearest_candidate_index = this.getNearestCandidateIndex(candidate_distance_sq, max_candidates);
                     block_coords[0] = candidate_x[nearest_candidate_index];
                     block_coords[1] = candidate_y[nearest_candidate_index];
                     block_coords[2] = candidate_z[nearest_candidate_index];
                     return true;
                  }
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            --z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.getBlockId(x, y, z) == block_id) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     nearest_candidate_index = this.getNearestCandidateIndex(candidate_distance_sq, max_candidates);
                     block_coords[0] = candidate_x[nearest_candidate_index];
                     block_coords[1] = candidate_y[nearest_candidate_index];
                     block_coords[2] = candidate_z[nearest_candidate_index];
                     return true;
                  }
               }
            }
         }

         ++run;
      }

      for(i = 1; i < run; ++i) {
         ++x;

         for(y = min_y; y <= max_y; ++y) {
            if (this.getBlockId(x, y, z) == block_id) {
               candidate_x[next_candidate_index] = x;
               candidate_y[next_candidate_index] = y;
               candidate_z[next_candidate_index] = z;
               candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
               ++next_candidate_index;
               if (next_candidate_index == max_candidates) {
                  nearest_candidate_index = this.getNearestCandidateIndex(candidate_distance_sq, max_candidates);
                  block_coords[0] = candidate_x[nearest_candidate_index];
                  block_coords[1] = candidate_y[nearest_candidate_index];
                  block_coords[2] = candidate_z[nearest_candidate_index];
                  return true;
               }
            }
         }
      }

      if (next_candidate_index == 0) {
         return false;
      } else {
         i = this.getNearestCandidateIndex(candidate_distance_sq, next_candidate_index);
         block_coords[0] = candidate_x[i];
         block_coords[1] = candidate_y[i];
         block_coords[2] = candidate_z[i];
         return true;
      }
   }

   private boolean intArrayContains(int[] array, int value) {
      int len = array.length;

      do {
         --len;
         if (len < 0) {
            return false;
         }
      } while(array[len] != value);

      return true;
   }

   public int getNearestBlockCandidates(double origin_x, double origin_y, double origin_z, int horizontal_radius, int vertical_radius, int max_candidates, int[] block_ids, int[] candidate_x, int[] candidate_y, int[] candidate_z, double[] candidate_distance_sq) {
      return this.getNearestBlockCandidates((float)origin_x, (float)origin_y, (float)origin_z, horizontal_radius, vertical_radius, max_candidates, block_ids, candidate_x, candidate_y, candidate_z, candidate_distance_sq);
   }

   public int getNearestBlockCandidates(float origin_x, float origin_y, float origin_z, int horizontal_radius, int vertical_radius, int max_candidates, int[] block_ids, int[] candidate_x, int[] candidate_y, int[] candidate_z, double[] candidate_distance_sq) {
      int x = MathHelper.floor_double((double)origin_x);
      int y = MathHelper.floor_double((double)origin_y);
      int z = MathHelper.floor_double((double)origin_z);
      origin_x -= 0.5F;
      origin_y -= 0.5F;
      origin_z -= 0.5F;
      int width = horizontal_radius * 2 + 1;
      int height = vertical_radius * 2 + 1;
      int run = 1;
      int min_y = y - vertical_radius;
      int max_y = y + vertical_radius;
      int next_candidate_index = 0;

      for(y = min_y; y <= max_y; ++y) {
         if (this.intArrayContains(block_ids, this.getBlockId(x, y, z))) {
            candidate_x[next_candidate_index] = x;
            candidate_y[next_candidate_index] = y;
            candidate_z[next_candidate_index] = z;
            candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
            ++next_candidate_index;
            if (next_candidate_index == max_candidates) {
               return max_candidates;
            }
         }
      }

      int i;
      while(run < width) {
         for(i = 1; i <= run; ++i) {
            ++x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.intArrayContains(block_ids, this.getBlockId(x, y, z))) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     return max_candidates;
                  }
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            ++z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.intArrayContains(block_ids, this.getBlockId(x, y, z))) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     return max_candidates;
                  }
               }
            }
         }

         ++run;

         for(i = 1; i <= run; ++i) {
            --x;

            for(y = min_y; y <= max_y; ++y) {
               if (this.intArrayContains(block_ids, this.getBlockId(x, y, z))) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     return max_candidates;
                  }
               }
            }
         }

         for(i = 1; i <= run; ++i) {
            --z;

            for(y = min_y; y <= max_y; ++y) {
               if (this.intArrayContains(block_ids, this.getBlockId(x, y, z))) {
                  candidate_x[next_candidate_index] = x;
                  candidate_y[next_candidate_index] = y;
                  candidate_z[next_candidate_index] = z;
                  candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
                  ++next_candidate_index;
                  if (next_candidate_index == max_candidates) {
                     return max_candidates;
                  }
               }
            }
         }

         ++run;
      }

      for(i = 1; i < run; ++i) {
         ++x;

         for(y = min_y; y <= max_y; ++y) {
            if (this.intArrayContains(block_ids, this.getBlockId(x, y, z))) {
               candidate_x[next_candidate_index] = x;
               candidate_y[next_candidate_index] = y;
               candidate_z[next_candidate_index] = z;
               candidate_distance_sq[next_candidate_index] = getDistanceSqFromDeltas((float)x - origin_x, (float)y - origin_y, (float)z - origin_z);
               ++next_candidate_index;
               if (next_candidate_index == max_candidates) {
                  return max_candidates;
               }
            }
         }
      }

      return next_candidate_index;
   }

   public final boolean isAirBlock(int par1, int par2, int par3) {
      return this.getBlockId(par1, par2, par3) == 0;
   }

   public boolean blockHasTileEntity(int par1, int par2, int par3) {
      int var4 = this.getBlockId(par1, par2, par3);
      return Block.blocksList[var4] != null && Block.blocksList[var4].hasTileEntity();
   }

   public int blockGetRenderType(int par1, int par2, int par3) {
      int var4 = this.getBlockId(par1, par2, par3);
      return Block.blocksList[var4] != null ? Block.blocksList[var4].getRenderType() : -1;
   }

   public boolean blockExists(int par1, int par2, int par3) {
      return par2 >= 0 && par2 < 256 ? this.chunkExists(par1 >> 4, par3 >> 4) : false;
   }

   public final boolean doChunksNearChunkExist(int par1, int par2, int par3, int par4, boolean include_empty_chunks) {
      return this.checkChunksExist(par1 - par4, par2 - par4, par3 - par4, par1 + par4, par2 + par4, par3 + par4, include_empty_chunks);
   }

   public final boolean doChunksNearChunkExist(int par1, int par2, int par3, int par4) {
      return this.doChunksNearChunkExist(par1, par2, par3, par4, true);
   }

   public final boolean checkChunksExist(int par1, int par2, int par3, int par4, int par5, int par6, boolean include_empty_chunks) {
      if (par5 >= 0 && par2 < 256) {
         par1 >>= 4;
         par3 >>= 4;
         par4 >>= 4;
         par6 >>= 4;
         int var7;
         int var8;
         if (include_empty_chunks) {
            for(var7 = par1; var7 <= par4; ++var7) {
               for(var8 = par3; var8 <= par6; ++var8) {
                  if (!this.chunkExists(var7, var8)) {
                     return false;
                  }
               }
            }
         } else {
            for(var7 = par1; var7 <= par4; ++var7) {
               for(var8 = par3; var8 <= par6; ++var8) {
                  Chunk chunk = this.getChunkIfItExists(var7, var8);
                  if (chunk == null || chunk.isEmpty()) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         Debug.setErrorMessage("checkChunksExist: got here");
         return false;
      }
   }

   public final boolean checkChunksExist(int par1, int par2, int par3, int par4, int par5, int par6) {
      return this.checkChunksExist(par1, par2, par3, par4, par5, par6, true);
   }

   public final boolean chunkExists(int par1, int par2) {
      return this.chunkProvider.chunkExists(par1, par2);
   }

   protected final boolean chunkExistsAndIsNotEmpty(int chunk_x, int chunk_z) {
      return this.chunkProvider.chunkExists(chunk_x, chunk_z) && !this.getChunkFromChunkCoords(chunk_x, chunk_z).isEmpty();
   }

   public final boolean chunkExistsAndIsNotEmptyFromBlockCoords(int x, int z) {
      return this.chunkExistsAndIsNotEmpty(x >> 4, z >> 4);
   }

   public final boolean doesChunkAndAllNeighborsExist(int chunk_x, int chunk_z, int range, boolean include_empty_chunks) {
      if (!this.chunkExists(chunk_x, chunk_z)) {
         return false;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(chunk_x, chunk_z);
         return !include_empty_chunks && chunk.isEmpty() ? false : chunk.doAllNeighborsExist(range, false, include_empty_chunks);
      }
   }

   public final Chunk getChunkIfItExists(int chunk_x, int chunk_z) {
      return this.chunkProvider.getChunkIfItExists(chunk_x, chunk_z);
   }

   public final Chunk getChunkFromBlockCoordsIfItExists(int x, int z) {
      return this.getChunkIfItExists(x >> 4, z >> 4);
   }

   public final Chunk getChunkFromBlockCoords(int par1, int par2) {
      return this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
   }

   public final Chunk getChunkFromChunkCoords(int par1, int par2) {
      return this.chunkProvider.provideChunk(par1, par2);
   }

   public final Chunk getChunkFromPosition(double pos_x, double pos_z) {
      return this.getChunkFromChunkCoords(Chunk.getChunkCoordFromDouble(pos_x), Chunk.getChunkCoordFromDouble(pos_z));
   }

   public final boolean setBlockWithDefaultMetadata(int x, int y, int z, Block block, int flags, boolean report_metadata_failure) {
      int metadata = block.getDefaultMetadata(this, x, y, z);
      if (metadata < 0) {
         if (report_metadata_failure) {
            Minecraft.setErrorMessage("setBlockWithDefaultMetadata: unable to place " + block.getLocalizedName() + " at " + x + "," + y + "," + z + " because a valid default metadata could not be obtained");
         }

         return false;
      } else {
         boolean result = this.setBlock(x, y, z, block.blockID, metadata, flags);
         return result;
      }
   }

   public final boolean setBlockWithMetadataAdjustedForCoordBaseMode(int x, int y, int z, Block block, int metadata_in_coord_base_mode_2, int flags, int coord_base_mode) {
      EnumDirection direction_facing_in_coord_base_mode_2 = block.getDirectionFacing(metadata_in_coord_base_mode_2);
      int adjusted_metadata = direction_facing_in_coord_base_mode_2 == null ? metadata_in_coord_base_mode_2 : block.getMetadataForDirectionFacing(metadata_in_coord_base_mode_2, direction_facing_in_coord_base_mode_2.adjustForCoordBaseMode(coord_base_mode), coord_base_mode);
      if (adjusted_metadata < 0) {
         Minecraft.setErrorMessage("setBlockWithMetadataAdjustedForCoordBaseMode: invalid adjusted metadata for " + block + " at " + x + "," + y + "," + z);
         return false;
      } else {
         return this.setBlock(x, y, z, block.blockID, adjusted_metadata, flags);
      }
   }

   public final boolean setBlock(int par1, int par2, int par3, int par4, int par5, int par6) {
      if (!this.isWithinBlockBounds(par1, par2, par3)) {
         return false;
      } else {
         Chunk var7 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
         if (var7.isEmpty()) {
            Debug.setErrorMessage("setBlock: called for coords in empty chunk");
            Debug.printStackTrace();
            return false;
         } else {
            int block_id_before = var7.getBlockID(par1 & 15, par2, par3 & 15);
            boolean var9 = var7.setBlockIDWithMetadata(par1 & 15, par2, par3 & 15, par4, par5, block_id_before);
            if (Block.lightOpacity[par4] != Block.lightOpacity[block_id_before]) {
               this.updateAllLightTypes(par1, par2, par3, var7);
            } else if (Block.lightValue[par4] != Block.lightValue[block_id_before]) {
               this.propagateBlocklight(par1, par2, par3, false, var7);
            }

            if (var9) {
               if ((par6 & 2) != 0 && (!this.isRemote || (par6 & 4) == 0)) {
                  this.markBlockForUpdate(par1, par2, par3);
               }

               if (!this.isRemote && (par6 & 1) != 0) {
                  this.notifyBlockChange(par1, par2, par3, block_id_before);
                  Block var10 = Block.blocksList[par4];
                  if (var10 != null && var10.hasComparatorInputOverride()) {
                     this.func_96440_m(par1, par2, par3, par4);
                  }
               }
            }

            return var9;
         }
      }
   }

   public final Material getBlockMaterial(int par1, int par2, int par3) {
      int var4 = this.getBlockId(par1, par2, par3);
      return var4 == 0 ? Material.air : Block.blocksList[var4].blockMaterial;
   }

   public final Material getBlockMaterial(int block_id) {
      return block_id == 0 ? Material.air : Block.blocksList[block_id].blockMaterial;
   }

   public final int getBlockMetadata(int x, int y, int z) {
      return this.isWithinBlockBounds(x, y, z) ? this.getChunkFromChunkCoords(x >> 4, z >> 4).getBlockMetadata(x & 15, y, z & 15) : 0;
   }

   public final boolean setBlockMetadataWithNotify(int par1, int par2, int par3, int par4, int par5) {
      if (!this.isWithinBlockDomain(par1, par3)) {
         return false;
      } else if (par2 < 0) {
         return false;
      } else if (par2 >= 256) {
         return false;
      } else {
         Chunk var6 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
         int var7 = par1 & 15;
         int var8 = par3 & 15;
         boolean var9 = var6.setBlockMetadata(var7, par2, var8, par4);
         if (var9) {
            int var10 = var6.getBlockID(var7, par2, var8);
            if ((par5 & 2) != 0 && (!this.isRemote || (par5 & 4) == 0)) {
               this.markBlockForUpdate(par1, par2, par3);
            }

            if (!this.isRemote && (par5 & 1) != 0) {
               this.notifyBlockChange(par1, par2, par3, var10);
               Block var11 = Block.blocksList[var10];
               if (var11 != null && var11.hasComparatorInputOverride()) {
                  this.func_96440_m(par1, par2, par3, var10);
               }
            }
         }

         return var9;
      }
   }

   public final boolean setBlockToAir(int x, int y, int z) {
      return this.setBlock(x, y, z, 0, 0, 3);
   }

   public final boolean setBlockToAir(int x, int y, int z, int flags) {
      return this.setBlock(x, y, z, 0, 0, flags);
   }

   public boolean destroyBlock(BlockBreakInfo info, boolean drop_as_item) {
      return this.destroyBlock(info, drop_as_item, false);
   }

   public boolean destroyBlock(BlockBreakInfo info, boolean drop_as_item, boolean suppress_sound) {
      if (info.wasSilkHarvested()) {
         Minecraft.setErrorMessage("destroyBlock: not meant to handle silk harvesting");
      }

      int x = info.x;
      int y = info.y;
      int z = info.z;
      int block_id = this.getBlockId(x, y, z);
      if (block_id > 0) {
         if (block_id != info.block_id) {
            Minecraft.setErrorMessage("destroyBlock: block mismatch");
            return false;
         } else {
            int data = block_id + (info.getMetadata() << 12);
            if (suppress_sound) {
               data |= RenderGlobal.SFX_2001_SUPPRESS_SOUND;
            }

            if (info.wasNotLegal()) {
               data |= RenderGlobal.SFX_2001_WAS_NOT_LEGAL;
            }

            this.playAuxSFX(2001, x, y, z, data);
            if (drop_as_item) {
               Block block = Block.getBlock(block_id);
               block.dropBlockAsEntityItem(info);
            }

            return this.setBlock(x, y, z, 0, 0, 3);
         }
      } else {
         return false;
      }
   }

   public boolean destroyBlockWithoutDroppingItem(int x, int y, int z, EnumBlockFX fx) {
      Block block = this.getBlock(x, y, z);
      if (block == null) {
         Debug.setErrorMessage("destroyBlockWithoutDroppingItem: no block found at " + StringHelper.getCoordsAsString(x, y, z));
         return false;
      } else {
         if (block instanceof IBlockWithPartner) {
            IBlockWithPartner block_with_partner = (IBlockWithPartner)block;
            int metadata = this.getBlockMetadata(x, y, z);
            if (block_with_partner.requiresPartner(metadata) && block_with_partner.isPartnerPresent(this, x, y, z)) {
               int partner_x = x + block_with_partner.getPartnerOffsetX(metadata);
               int partner_y = y + block_with_partner.getPartnerOffsetY(metadata);
               int partner_z = z + block_with_partner.getPartnerOffsetZ(metadata);
               if (fx != null) {
                  this.blockFX(fx, partner_x, partner_y, partner_z);
               }

               if (block_with_partner.partnerDropsAsItem(metadata)) {
                  this.setBlockToAir(partner_x, partner_y, partner_z);
               }
            }
         }

         if (fx != null) {
            this.blockFX(fx, x, y, z);
         }

         return this.setBlockToAir(x, y, z);
      }
   }

   public boolean destroyBlockWithoutDroppingItem(int x, int y, int z) {
      return this.destroyBlockWithoutDroppingItem(x, y, z, (EnumBlockFX)null);
   }

   public boolean setBlock(int par1, int par2, int par3, int par4) {
      return this.setBlock(par1, par2, par3, par4, 0, 3);
   }

   public void markBlockForUpdate(int par1, int par2, int par3) {
      for(int var4 = 0; var4 < this.worldAccesses.size(); ++var4) {
         ((IWorldAccess)this.worldAccesses.get(var4)).markBlockForUpdate(par1, par2, par3);
      }

   }

   public void notifyBlockChange(int par1, int par2, int par3, int par4) {
      this.notifyBlocksOfNeighborChange(par1, par2, par3, par4);
   }

   public void markBlockRangeForRenderUpdate(int par1, int par2, int par3, int par4, int par5, int par6) {
      for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
         ((IWorldAccess)this.worldAccesses.get(var7)).markBlockRangeForRenderUpdate(par1, par2, par3, par4, par5, par6);
      }

   }

   public void notifyBlocksOfNeighborChange(int par1, int par2, int par3, int par4) {
      this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
      this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
      this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
      this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
      this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
      this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
   }

   public void notifyBlocksOfNeighborChange(int par1, int par2, int par3, int par4, int par5) {
      if (par5 != 4) {
         this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
      }

      if (par5 != 5) {
         this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
      }

      if (par5 != 0) {
         this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
      }

      if (par5 != 1) {
         this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
      }

      if (par5 != 2) {
         this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
      }

      if (par5 != 3) {
         this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
      }

   }

   public final void notifyBlockOfNeighborChange(int par1, int par2, int par3, int par4) {
      if (!this.isRemote) {
         int var5 = this.getBlockId(par1, par2, par3);
         Block var6 = Block.blocksList[var5];
         if (var6 != null) {
            try {
               var6.onNeighborBlockChange(this, par1, par2, par3, par4);
            } catch (Throwable var13) {
               CrashReport var8 = CrashReport.makeCrashReport(var13, "Exception while updating neighbours");
               CrashReportCategory var9 = var8.makeCategory("Block being updated");

               int var10;
               try {
                  var10 = this.getBlockMetadata(par1, par2, par3);
               } catch (Throwable var12) {
                  var10 = -1;
               }

               var9.addCrashSectionCallable("Source block type", new CallableLvl1(this, par4));
               CrashReportCategory.addBlockCrashInfo(var9, par1, par2, par3, var5, var10);
               throw new ReportedException(var8);
            }
         }
      }

   }

   public boolean isBlockTickScheduledThisTick(int par1, int par2, int par3, int par4) {
      return false;
   }

   public boolean canBlockSeeTheSky(int par1, int par2, int par3) {
      return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).canBlockSeeTheSky(par1 & 15, par2, par3 & 15);
   }

   public int getFullBlockLightValue(int par1, int par2, int par3) {
      if (par2 < 0) {
         return 0;
      } else {
         if (par2 >= 256) {
            par2 = 255;
         }

         return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).getBlockLightValue(par1 & 15, par2, par3 & 15, 0);
      }
   }

   public final int getBlockLightValue(int par1, int par2, int par3) {
      return this.getBlockLightValue_do(par1, par2, par3, true);
   }

   public int getBlockLightValue_do(int par1, int par2, int par3, boolean par4) {
      if (!this.isWithinBlockDomain(par1, par3)) {
         return 15;
      } else {
         if (par4) {
            int var5 = this.getBlockId(par1, par2, par3);
            if (Block.useNeighborBrightness[var5]) {
               Block block = Block.getBlock(var5);
               int metadata = this.getBlockMetadata(par1, par2, par3);
               int brightness = 0;

               for(int ordinal = 0; ordinal < 6; ++ordinal) {
                  EnumDirection direction = EnumDirection.get(ordinal);
                  if (block.useNeighborBrightness(metadata, direction)) {
                     brightness = Math.max(brightness, this.getBlockLightValue_do(par1 + direction.dx, par2 + direction.dy, par3 + direction.dz, false));
                     if (brightness > 14) {
                        break;
                     }
                  }
               }

               return brightness;
            }
         }

         if (par2 < 0) {
            return 0;
         } else {
            if (par2 >= 256) {
               par2 = 255;
            }

            Chunk var11 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
            par1 &= 15;
            par3 &= 15;
            int blv;
            if (this.ignore_rain_and_thunder_for_next_BLV) {
               blv = var11.getBlockLightValue(par1, par2, par3, this.skylight_subtracted_ignoring_rain_and_thunder);
               this.ignore_rain_and_thunder_for_next_BLV = false;
            } else {
               blv = var11.getBlockLightValue(par1, par2, par3, this.skylightSubtracted);
            }

            return blv;
         }
      }
   }

   public int getHeightValue(int par1, int par2) {
      if (this.isWithinBlockDomain(par1, par2)) {
         if (!this.chunkExists(par1 >> 4, par2 >> 4)) {
            return 0;
         } else {
            Chunk var3 = this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
            return var3.getHeightValue(par1 & 15, par2 & 15);
         }
      } else {
         return 0;
      }
   }

   public final int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
      if (this.provider.hasNoSky && par1EnumSkyBlock == EnumSkyBlock.Sky) {
         return 0;
      } else {
         if (par3 < 0) {
            par3 = 0;
         }

         if (par3 >= 256) {
            return par1EnumSkyBlock.defaultLightValue;
         } else if (!this.isWithinBlockDomain(par2, par4)) {
            return par1EnumSkyBlock.defaultLightValue;
         } else {
            int var5 = par2 >> 4;
            int var6 = par4 >> 4;
            if (!this.chunkExists(var5, var6)) {
               return par1EnumSkyBlock.defaultLightValue;
            } else {
               int block_id = this.getBlockId(par2, par3, par4);
               if (!Block.useNeighborBrightness[block_id]) {
                  Chunk chunk = this.getChunkFromChunkCoords(var5, var6);
                  return chunk.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
               } else {
                  Block block = Block.getBlock(block_id);
                  int metadata = this.getBlockMetadata(par2, par3, par4);
                  int brightness = 0;

                  for(int ordinal = 0; ordinal < 6; ++ordinal) {
                     EnumDirection direction = EnumDirection.get(ordinal);
                     int x = par2 + direction.dx;
                     int z = par4 + direction.dz;
                     if (block.useNeighborBrightness(metadata, direction) && this.chunkExistsAndIsNotEmptyFromBlockCoords(x, z)) {
                        brightness = Math.max(brightness, this.getSavedLightValue(par1EnumSkyBlock, x, par3 + direction.dy, z));
                        if (brightness > 14) {
                           break;
                        }
                     }
                  }

                  return brightness;
               }
            }
         }
      }
   }

   public final int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
      if (par3 < 0) {
         par3 = 0;
      } else if (par3 > 255) {
         par3 = 255;
      }

      if (this.isWithinBlockDomain(par2, par4)) {
         int var5 = par2 >> 4;
         int var6 = par4 >> 4;
         if (!this.chunkExists(var5, var6)) {
            return par1EnumSkyBlock.defaultLightValue;
         } else {
            Chunk var7 = this.getChunkFromChunkCoords(var5, var6);
            return var7.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
         }
      } else {
         return par1EnumSkyBlock.defaultLightValue;
      }
   }

   public final int getSavedSkylightValue(int par2, int par3, int par4) {
      if (!this.isWithinBlockDomain(par2, par4)) {
         return 15;
      } else {
         Chunk chunk = this.getChunkIfItExists(par2 >> 4, par4 >> 4);
         return chunk == null ? 15 : chunk.getSavedSkylightValue(par2 & 15, par3 < 0 ? 0 : (par3 > 255 ? 255 : par3), par4 & 15);
      }
   }

   public final int getSavedBlocklightValue(int par2, int par3, int par4) {
      if (!this.isWithinBlockDomain(par2, par4)) {
         return 0;
      } else {
         Chunk chunk = this.getChunkIfItExists(par2 >> 4, par4 >> 4);
         return chunk == null ? 0 : chunk.getSavedBlocklightValue(par2 & 15, par3 < 0 ? 0 : (par3 > 255 ? 255 : par3), par4 & 15);
      }
   }

   public final int getSavedLightValueMITE(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, Chunk chunk) {
      if (par3 < 0) {
         par3 = 0;
      } else if (par3 > 255) {
         par3 = 255;
      }

      return chunk.isWithinBlockDomain() ? chunk.getSavedLightValueForNonEmptyChunk(par1EnumSkyBlock, par2 & 15, par3, par4 & 15) : par1EnumSkyBlock.defaultLightValue;
   }

   public final int getSavedSkylightValueMITE(int x, int y, int z, Chunk chunk) {
      return chunk.isWithinBlockDomain() ? chunk.getSavedSkylightValueForNonEmptyChunk(x & 15, y < 0 ? 0 : (y > 255 ? 255 : y), z & 15) : 15;
   }

   public final int getSavedBlocklightValueMITE(int x, int y, int z, Chunk chunk) {
      return chunk.isWithinBlockDomain() ? chunk.getSavedBlocklightValueForNonEmptyChunk(x & 15, y < 0 ? 0 : (y > 255 ? 255 : y), z & 15) : 0;
   }

   public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5) {
      if (this.isWithinBlockDomain(par2, par4) && par3 >= 0 && par3 < 256 && this.chunkExists(par2 >> 4, par4 >> 4)) {
         Chunk var6 = this.getChunkFromChunkCoords(par2 >> 4, par4 >> 4);
         var6.setLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15, par5);

         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(par2, par3, par4);
         }
      }

   }

   public void setSkylightValue(int par2, int par3, int par4, int par5) {
      if (this.isWithinBlockBounds(par2, par3, par4)) {
         Chunk var6 = this.getChunkIfItExists(par2 >> 4, par4 >> 4);
         if (var6 == null) {
            return;
         }

         var6.setSkylightValue(par2 & 15, par3, par4 & 15, par5);

         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(par2, par3, par4);
         }
      }

   }

   public void setBlocklightValue(int par2, int par3, int par4, int par5) {
      if (this.isWithinBlockBounds(par2, par3, par4)) {
         Chunk var6 = this.getChunkIfItExists(par2 >> 4, par4 >> 4);
         if (var6 == null) {
            return;
         }

         var6.setBlocklightValue(par2 & 15, par3, par4 & 15, par5);

         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(par2, par3, par4);
         }
      }

   }

   public void setLightValueMITE(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5, Chunk chunk) {
      if (this.isWithinBlockDomain(par2, par4) && par3 >= 0 && par3 < 256) {
         chunk.setLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15, par5);

         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(par2, par3, par4);
         }
      }

   }

   public void setSkylightValueMITE(int par2, int par3, int par4, int par5, Chunk chunk) {
      if (this.isWithinBlockBounds(par2, par3, par4)) {
         chunk.setSkylightValue(par2 & 15, par3, par4 & 15, par5);

         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(par2, par3, par4);
         }
      }

   }

   public void setBlocklightValueMITE(int par2, int par3, int par4, int par5, Chunk chunk) {
      if (this.isWithinBlockBounds(par2, par3, par4)) {
         chunk.setBlocklightValue(par2 & 15, par3, par4 & 15, par5);

         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).markBlockForRenderUpdate(par2, par3, par4);
         }
      }

   }

   public void markBlockForRenderUpdate(int par1, int par2, int par3) {
      for(int var4 = 0; var4 < this.worldAccesses.size(); ++var4) {
         ((IWorldAccess)this.worldAccesses.get(var4)).markBlockForRenderUpdate(par1, par2, par3);
      }

   }

   public final int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4) {
      int var5 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
      int var6 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);
      if (var6 < par4) {
         var6 = par4;
      }

      return var5 << 20 | var6 << 4;
   }

   public float getBrightness(int par1, int par2, int par3, int par4) {
      int var5 = this.getBlockLightValue(par1, par2, par3);
      if (var5 < par4) {
         var5 = par4;
      }

      return this.provider.lightBrightnessTable[var5];
   }

   public float getLightBrightness(int par1, int par2, int par3) {
      return this.provider.lightBrightnessTable[this.getBlockLightValue(par1, par2, par3)];
   }

   public static final int getUnadjustedTimeOfDay(long unadjusted_tick) {
      return (int)(unadjusted_tick % 24000L);
   }

   public final int getTimeOfDay() {
      return this.worldInfo.getWorldTimeOfDay(this.getDimensionId());
   }

   public static final int getTimeOfSunrise() {
      return 5000;
   }

   public static final int getTimeOfSunset() {
      return 19000;
   }

   public static final int getHourOfLatestReconnection() {
      return getTimeOfSleeping() / 1000 - 1;
   }

   public static final int getTimeOfSleeping() {
      return 21000;
   }

   public int getAdjustedTimeOfDay() {
      return getAdjustedTimeOfDay(this.getTimeOfDay());
   }

   public static int getAdjustedTimeOfDay(int unadjusted_time_of_day) {
      return (unadjusted_time_of_day + 6000) % 24000;
   }

   public int getTimeTillSunrise() {
      int time_of_day = this.getAdjustedTimeOfDay();
      return time_of_day < getTimeOfSunrise() ? getTimeOfSunrise() - time_of_day : getTimeOfSunrise() - time_of_day + 24000;
   }

   public static boolean isDaytime(long unadjusted_tick) {
      long time_of_day = (long)getAdjustedTimeOfDay(getUnadjustedTimeOfDay(unadjusted_tick));
      return time_of_day > (long)getTimeOfSunrise() && time_of_day < (long)getTimeOfSunset();
   }

   public boolean isDaytime() {
      long time_of_day = (long)this.getAdjustedTimeOfDay();
      return time_of_day > (long)getTimeOfSunrise() && time_of_day < (long)getTimeOfSunset();
   }

   public final RaycastCollision tryRaycastVsBlocks(Raycast raycast) {
      boolean hit_liquids = !raycast.alwaysIgnoreLiquids();
      boolean par4 = raycast.getOriginator() instanceof EntityArrow;
      raycast.clearImpedance();
      Vec3 par1Vec3 = raycast.getOrigin().copy();
      Vec3 par2Vec3 = raycast.getLimit().copy();
      if (!Double.isNaN(par1Vec3.xCoord) && !Double.isNaN(par1Vec3.yCoord) && !Double.isNaN(par1Vec3.zCoord) && !Double.isNaN(par2Vec3.xCoord) && !Double.isNaN(par2Vec3.yCoord) && !Double.isNaN(par2Vec3.zCoord)) {
         int var5 = MathHelper.floor_double(par2Vec3.xCoord);
         int var6 = MathHelper.floor_double(par2Vec3.yCoord);
         int var7 = MathHelper.floor_double(par2Vec3.zCoord);
         int var8 = MathHelper.floor_double(par1Vec3.xCoord);
         int var9 = MathHelper.floor_double(par1Vec3.yCoord);
         int var10 = MathHelper.floor_double(par1Vec3.zCoord);
         int var11 = this.getBlockId(var8, var9, var10);
         if (var11 > 0) {
            Block var13 = Block.blocksList[var11];
            if (var13.canCollideCheck(this.getBlockMetadata(var8, var9, var10), hit_liquids)) {
               RaycastCollision var14 = var13.tryRaycastVsBlock(raycast, var8, var9, var10, par1Vec3, par2Vec3);
               if (var14 != null) {
                  return var14;
               }
            }
         }

         var11 = 200;

         while(var11-- >= 0) {
            if (Double.isNaN(par1Vec3.xCoord) || Double.isNaN(par1Vec3.yCoord) || Double.isNaN(par1Vec3.zCoord)) {
               return null;
            }

            if (var8 == var5 && var9 == var6 && var10 == var7) {
               return null;
            }

            boolean var39 = true;
            boolean var40 = true;
            boolean var41 = true;
            double var15 = 999.0;
            double var17 = 999.0;
            double var19 = 999.0;
            if (var5 > var8) {
               var15 = (double)var8 + 1.0;
            } else if (var5 < var8) {
               var15 = (double)var8 + 0.0;
            } else {
               var39 = false;
            }

            if (var6 > var9) {
               var17 = (double)var9 + 1.0;
            } else if (var6 < var9) {
               var17 = (double)var9 + 0.0;
            } else {
               var40 = false;
            }

            if (var7 > var10) {
               var19 = (double)var10 + 1.0;
            } else if (var7 < var10) {
               var19 = (double)var10 + 0.0;
            } else {
               var41 = false;
            }

            double var21 = 999.0;
            double var23 = 999.0;
            double var25 = 999.0;
            double var27 = par2Vec3.xCoord - par1Vec3.xCoord;
            double var29 = par2Vec3.yCoord - par1Vec3.yCoord;
            double var31 = par2Vec3.zCoord - par1Vec3.zCoord;
            if (var39) {
               var21 = (var15 - par1Vec3.xCoord) / var27;
            }

            if (var40) {
               var23 = (var17 - par1Vec3.yCoord) / var29;
            }

            if (var41) {
               var25 = (var19 - par1Vec3.zCoord) / var31;
            }

            boolean var33 = false;
            byte var42;
            if (var21 < var23 && var21 < var25) {
               if (var5 > var8) {
                  var42 = 4;
               } else {
                  var42 = 5;
               }

               par1Vec3.xCoord = var15;
               par1Vec3.yCoord += var29 * var21;
               par1Vec3.zCoord += var31 * var21;
            } else if (var23 < var25) {
               if (var6 > var9) {
                  var42 = 0;
               } else {
                  var42 = 1;
               }

               par1Vec3.xCoord += var27 * var23;
               par1Vec3.yCoord = var17;
               par1Vec3.zCoord += var31 * var23;
            } else {
               if (var7 > var10) {
                  var42 = 2;
               } else {
                  var42 = 3;
               }

               par1Vec3.xCoord += var27 * var25;
               par1Vec3.yCoord += var29 * var25;
               par1Vec3.zCoord = var19;
            }

            Vec3 var34 = this.getWorldVec3Pool().getVecFromPool(par1Vec3.xCoord, par1Vec3.yCoord, par1Vec3.zCoord);
            var8 = (int)(var34.xCoord = (double)MathHelper.floor_double(par1Vec3.xCoord));
            if (var42 == 5) {
               --var8;
               ++var34.xCoord;
            }

            var9 = (int)(var34.yCoord = (double)MathHelper.floor_double(par1Vec3.yCoord));
            if (var42 == 1) {
               --var9;
               ++var34.yCoord;
            }

            var10 = (int)(var34.zCoord = (double)MathHelper.floor_double(par1Vec3.zCoord));
            if (var42 == 3) {
               --var10;
               ++var34.zCoord;
            }

            int var35 = this.getBlockId(var8, var9, var10);
            if (var35 > 0) {
               Block var37 = Block.blocksList[var35];
               if (var37.canCollideCheck(this.getBlockMetadata(var8, var9, var10), hit_liquids)) {
                  RaycastCollision var38 = var37.tryRaycastVsBlock(raycast, var8, var9, var10, par1Vec3, par2Vec3);
                  if (var38 != null) {
                     return var38;
                  }
               }
            }
         }
      }

      return null;
   }

   public void playSoundAtBlock(int x, int y, int z, String name, float volume, float pitch) {
      if (this.isRemote) {
         Minecraft.setErrorMessage("playSoundAtBlock: only meant to be called on server");
      } else if (name != null) {
         for(int i = 0; i < this.worldAccesses.size(); ++i) {
            ((IWorldAccess)this.worldAccesses.get(i)).playSound(name, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), volume, pitch);
         }

      }
   }

   public void playSoundAtBlock(int x, int y, int z, String name, float volume) {
      this.playSoundAtBlock(x, y, z, name, volume, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F);
   }

   public void playSoundAtEntity(Entity par1Entity, String par2Str) {
      this.playSoundAtEntity(par1Entity, par2Str, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F);
   }

   public void playSoundAtEntity(Entity par1Entity, String par2Str, float par3) {
      this.playSoundAtEntity(par1Entity, par2Str, par3, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F);
   }

   public void playSoundAtEntity(Entity par1Entity, String par2Str, float par3, float par4) {
      if (par1Entity != null && par2Str != null) {
         if (par1Entity.isZevimrgvInTournament()) {
            return;
         }

         for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
            ((IWorldAccess)this.worldAccesses.get(var5)).playSound(par2Str, par1Entity.posX, par1Entity.posY - (double)par1Entity.yOffset, par1Entity.posZ, par3, par4);
         }
      }

   }

   public void playLongDistanceSoundAtEntity(Entity par1Entity, String par2Str, float par3, float par4) {
      if (par1Entity != null && par2Str != null) {
         for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
            ((IWorldAccess)this.worldAccesses.get(var5)).playLongDistanceSound(par2Str, par1Entity.posX, par1Entity.posY - (double)par1Entity.yOffset, par1Entity.posZ, par3, par4);
         }
      }

   }

   public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, float par3, float par4) {
      if (par1EntityPlayer != null && par2Str != null) {
         for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
            ((IWorldAccess)this.worldAccesses.get(var5)).playSoundToNearExcept(par1EntityPlayer, par2Str, par1EntityPlayer.posX, par1EntityPlayer.posY - (double)par1EntityPlayer.yOffset, par1EntityPlayer.posZ, par3, par4);
         }
      }

   }

   public void playSoundEffect(double par1, double par3, double par5, String par7Str) {
      this.playSoundEffect(par1, par3, par5, par7Str, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F);
   }

   public void playSoundEffect(double par1, double par3, double par5, String par7Str, float par8) {
      this.playSoundEffect(par1, par3, par5, par7Str, par8, 0.9F + (this.rand.nextFloat() + this.rand.nextFloat()) / 10.0F);
   }

   public void playSoundEffect(double par1, double par3, double par5, String par7Str, float par8, float par9) {
      if (par7Str != null) {
         for(int var10 = 0; var10 < this.worldAccesses.size(); ++var10) {
            ((IWorldAccess)this.worldAccesses.get(var10)).playSound(par7Str, par1, par3, par5, par8, par9);
         }
      }

   }

   public void playSound(double par1, double par3, double par5, String par7Str, float par8, float par9, boolean par10) {
   }

   public void playRecord(String par1Str, int par2, int par3, int par4) {
      for(int var5 = 0; var5 < this.worldAccesses.size(); ++var5) {
         ((IWorldAccess)this.worldAccesses.get(var5)).playRecord(par1Str, par2, par3, par4);
      }

   }

   public void spawnParticle(EnumParticle enum_particle, double par2, double par4, double par6, double par8, double par10, double par12) {
      if (this.isWithinEntityDomain(par2, par6)) {
         for(int var14 = 0; var14 < this.worldAccesses.size(); ++var14) {
            ((IWorldAccess)this.worldAccesses.get(var14)).spawnParticle(enum_particle, par2, par4, par6, par8, par10, par12);
         }

      }
   }

   public void spawnParticleEx(EnumParticle enum_particle, int index, int data, double par2, double par4, double par6, double par8, double par10, double par12) {
      if (this.isWithinEntityDomain(par2, par6)) {
         for(int var14 = 0; var14 < this.worldAccesses.size(); ++var14) {
            ((IWorldAccess)this.worldAccesses.get(var14)).spawnParticleEx(enum_particle, index, data, par2, par4, par6, par8, par10, par12);
         }

      }
   }

   public boolean addWeatherEffect(Entity par1Entity) {
      this.weatherEffects.add(par1Entity);
      return true;
   }

   public boolean spawnEntityInWorld(Entity par1Entity) {
      int var2 = MathHelper.floor_double(par1Entity.posX / 16.0);
      int var3 = MathHelper.floor_double(par1Entity.posZ / 16.0);
      boolean var4 = par1Entity.forceSpawn;
      if (par1Entity instanceof EntityPlayer) {
         var4 = true;
      }

      if (var4 || this.chunkExists(var2, var3) && this.isWithinEntityDomain(par1Entity.posX, par1Entity.posZ)) {
         if (par1Entity instanceof EntityPlayer) {
            EntityPlayer var5 = (EntityPlayer)par1Entity;
            this.playerEntities.add(var5);
         }

         this.getChunkFromChunkCoords(var2, var3).addEntity(par1Entity);
         this.loadedEntityList.add(par1Entity);
         this.onEntityAdded(par1Entity);
         par1Entity.spawn_x = MathHelper.floor_double(par1Entity.posX);
         par1Entity.spawn_y = MathHelper.floor_double(par1Entity.posY);
         par1Entity.spawn_z = MathHelper.floor_double(par1Entity.posZ);
         par1Entity.onSpawned();
         return true;
      } else {
         return false;
      }
   }

   protected void onEntityAdded(Entity par1Entity) {
      for(int var2 = 0; var2 < this.worldAccesses.size(); ++var2) {
         ((IWorldAccess)this.worldAccesses.get(var2)).onEntityCreate(par1Entity);
      }

   }

   protected void onEntityRemoved(Entity par1Entity) {
      for(int var2 = 0; var2 < this.worldAccesses.size(); ++var2) {
         ((IWorldAccess)this.worldAccesses.get(var2)).onEntityDestroy(par1Entity);
      }

   }

   public void removeEntity(Entity par1Entity) {
      if (par1Entity.riddenByEntity != null) {
         par1Entity.riddenByEntity.mountEntity((Entity)null);
      }

      if (par1Entity.ridingEntity != null) {
         par1Entity.mountEntity((Entity)null);
      }

      par1Entity.setDead();
      if (par1Entity instanceof EntityPlayer) {
         this.playerEntities.remove(par1Entity);
      }

   }

   public void removePlayerEntityDangerously(Entity par1Entity) {
      par1Entity.setDead();
      if (par1Entity instanceof EntityPlayer) {
         this.playerEntities.remove(par1Entity);
      }

      if (par1Entity.isAddedToAChunk()) {
         Chunk chunk = par1Entity.getChunkAddedTo();
         if (this.chunkExists(chunk.xPosition, chunk.zPosition)) {
            par1Entity.removeFromChunk();
         }
      }

      this.loadedEntityList.remove(par1Entity);
      this.onEntityRemoved(par1Entity);
   }

   public void addWorldAccess(IWorldAccess par1IWorldAccess) {
      this.worldAccesses.add(par1IWorldAccess);
   }

   public void removeWorldAccess(IWorldAccess par1IWorldAccess) {
      this.worldAccesses.remove(par1IWorldAccess);
   }

   public final List getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {
      this.getCollidingBlockBounds(par2AxisAlignedBB, par1Entity);
      if (par1Entity instanceof EntityPlayer && par1Entity.getAsPlayer().tentative_bounding_boxes.size() > 0) {
         Iterator i = par1Entity.getAsPlayer().tentative_bounding_boxes.iterator();

         while(i.hasNext()) {
            TentativeBoundingBox tbb = (TentativeBoundingBox)i.next();
            if (par2AxisAlignedBB.intersectsWith(tbb.bb)) {
               this.collidingBoundingBoxes.add(tbb.bb);
            }
         }
      }

      if (!par1Entity.isEntityPlayer() || !par1Entity.getAsPlayer().isGhost() && !par1Entity.isZevimrgvInTournament()) {
         double var14 = 0.25;
         List var16 = this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB.expand(var14, var14, var14));

         for(int var15 = 0; var15 < var16.size(); ++var15) {
            Entity entity = (Entity)var16.get(var15);
            if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isGhost() && !entity.isZevimrgvInTournament()) {
               AxisAlignedBB var13 = entity.getBoundingBox();
               if (var13 != null && var13.intersectsWith(par2AxisAlignedBB)) {
                  this.collidingBoundingBoxes.add(var13);
               }

               var13 = par1Entity.getCollisionBox((Entity)var16.get(var15));
               if (var13 != null && var13.intersectsWith(par2AxisAlignedBB)) {
                  this.collidingBoundingBoxes.add(var13);
               }
            }
         }

         return this.collidingBoundingBoxes;
      } else {
         return this.collidingBoundingBoxes;
      }
   }

   public final List getCollidingBlockBounds(AxisAlignedBB bounding_box, Entity entity) {
      this.collidingBoundingBoxes.clear();
      int min_x = bounding_box.getBlockCoordForMinX();
      int max_x = bounding_box.getBlockCoordForMaxX();
      int min_y = bounding_box.getBlockCoordForMinY();
      int max_y = bounding_box.getBlockCoordForMaxY();
      int min_z = bounding_box.getBlockCoordForMinZ();
      int max_z = bounding_box.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int z = min_z; z <= max_z; ++z) {
            if (this.blockExists(x, 64, z)) {
               for(int y = min_y - 1; y <= max_y; ++y) {
                  Block block = this.getBlock(x, y, z);
                  if (block != null) {
                     block.addCollidingBoundsToList(this, x, y, z, bounding_box, this.collidingBoundingBoxes, entity);
                  }
               }
            }
         }
      }

      return this.collidingBoundingBoxes;
   }

   public final BlockInfo getHighestCollidingBlockClosestTo(AxisAlignedBB bounding_box, Entity entity, double pos_x, double pos_z) {
      BlockInfo info = null;
      double highest_max_y = 0.0;
      double shortest_distance_sq = 0.0;
      int min_x = bounding_box.getBlockCoordForMinX();
      int max_x = bounding_box.getBlockCoordForMaxX();
      int min_y = bounding_box.getBlockCoordForMinY();
      int max_y = bounding_box.getBlockCoordForMaxY();
      int min_z = bounding_box.getBlockCoordForMinZ();
      int max_z = bounding_box.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int z = min_z; z <= max_z; ++z) {
            if (this.blockExists(x, 64, z)) {
               label51:
               for(int y = min_y - 1; y <= max_y; ++y) {
                  Block block = this.getBlock(x, y, z);
                  if (block != null) {
                     this.collidingBoundingBoxes.clear();
                     block.addCollidingBoundsToList(this, x, y, z, bounding_box, this.collidingBoundingBoxes, entity);
                     Iterator i = this.collidingBoundingBoxes.iterator();

                     while(true) {
                        while(true) {
                           if (!i.hasNext()) {
                              continue label51;
                           }

                           AxisAlignedBB bb = (AxisAlignedBB)i.next();
                           double dx;
                           double dz;
                           if (info != null && !(bb.maxY > highest_max_y)) {
                              if (bb.maxY == highest_max_y) {
                                 dx = (double)x + 0.5 - pos_x;
                                 dz = (double)z + 0.5 - pos_z;
                                 double distance_sq = dx * dx + dz * dz;
                                 if (distance_sq < shortest_distance_sq) {
                                    info = new BlockInfo(block, x, y, z);
                                    shortest_distance_sq = distance_sq;
                                 }
                              }
                           } else {
                              info = new BlockInfo(block, x, y, z);
                              highest_max_y = bb.maxY;
                              dx = (double)x + 0.5 - pos_x;
                              dz = (double)z + 0.5 - pos_z;
                              shortest_distance_sq = dx * dx + dz * dz;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return info;
   }

   public boolean isInsideSolidBlock(double pos_x, double pos_y, double pos_z) {
      int x = MathHelper.floor_double(pos_x);
      int y = MathHelper.floor_double(pos_y);
      int z = MathHelper.floor_double(pos_z);
      int block_id = this.getBlockId(x, y, z);
      if (block_id == 0) {
         return false;
      } else {
         Block block = Block.getBlock(block_id);
         if (block.is_always_solid_standard_form_cube) {
            return true;
         } else if (block.isNeverSolid()) {
            return false;
         } else if (this.isBlockSolid(block, x, y, z)) {
            block.setBlockBoundsBasedOnStateAndNeighbors(this, x, y, z);
            int index = Minecraft.getThreadIndex();
            pos_x -= (double)x;
            pos_y -= (double)y;
            pos_z -= (double)z;
            if (!(pos_x < block.minX[index]) && !(pos_x >= block.maxX[index])) {
               if (!(pos_y < block.minY[index]) && !(pos_y >= block.maxY[index])) {
                  return !(pos_z < block.minZ[index]) && !(pos_z >= block.maxZ[index]);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public int calculateSkylightSubtracted(float par1) {
      float var2 = this.getCelestialAngle(par1);
      float var3 = 1.0F - (MathHelper.cos(var2 * 3.1415927F * 2.0F) * 2.0F + 0.5F);
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      this.skylight_subtracted_ignoring_rain_and_thunder = (int)(var3 * 11.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0 - (double)(this.getRainStrength(par1) * 5.0F) / 16.0));
      var3 = (float)((double)var3 * (1.0 - (double)(this.getWeightedThunderStrength(par1) * 5.0F) / 16.0));
      var3 = 1.0F - var3;
      return (int)(var3 * 11.0F);
   }

   public float getSunBrightness(float par1) {
      float var2 = this.getCelestialAngle(par1);
      float var3 = 1.0F - (MathHelper.cos(var2 * 3.1415927F * 2.0F) * 2.0F + 0.2F);
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0 - (double)(this.getRainStrength(par1) * 5.0F) / 16.0));
      var3 = (float)((double)var3 * (1.0 - (double)(this.getWeightedThunderStrength(par1) * 5.0F) / 16.0));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(Entity par1Entity, float par2) {
      float var3 = this.getCelestialAngle(par2);
      float var4 = MathHelper.cos(var3 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      int var5 = MathHelper.floor_double(par1Entity.posX);
      int var6 = MathHelper.floor_double(par1Entity.posZ);
      BiomeGenBase var7 = this.getBiomeGenForCoords(var5, var6);
      float var8 = var7.getFloatTemperature();
      if (Float.isNaN(this.biome_temperature_transition_for_sky_color)) {
         this.biome_temperature_transition_for_sky_color = var8;
      } else {
         float delta_temperature = var8 - this.biome_temperature_transition_for_sky_color;
         if (delta_temperature < -0.005F) {
            this.biome_temperature_transition_for_sky_color -= 0.005F;
         } else if (delta_temperature > 0.005F) {
            this.biome_temperature_transition_for_sky_color += 0.005F;
         } else {
            this.biome_temperature_transition_for_sky_color = var8;
         }

         var8 = this.biome_temperature_transition_for_sky_color;
      }

      int var9 = var7.getSkyColorByTemp(var8);
      float var10 = (float)(var9 >> 16 & 255) / 255.0F;
      float var11 = (float)(var9 >> 8 & 255) / 255.0F;
      float var12 = (float)(var9 & 255) / 255.0F;
      var10 *= var4;
      var11 *= var4;
      var12 *= var4;
      float var13 = this.getRainStrength(par2);
      float var14;
      float var15;
      if (var13 > 0.0F) {
         var14 = (var10 * 0.3F + var11 * 0.59F + var12 * 0.11F) * 0.6F;
         var15 = 1.0F - var13 * 0.75F;
         var10 = var10 * var15 + var14 * (1.0F - var15);
         var11 = var11 * var15 + var14 * (1.0F - var15);
         var12 = var12 * var15 + var14 * (1.0F - var15);
      }

      var14 = this.getWeightedThunderStrength(par2);
      float var10_before_flash;
      if (var14 > 0.0F) {
         var15 = (var10 * 0.3F + var11 * 0.59F + var12 * 0.11F) * 0.2F;
         var10_before_flash = 1.0F - var14 * 0.75F;
         var10 = var10 * var10_before_flash + var15 * (1.0F - var10_before_flash);
         var11 = var11 * var10_before_flash + var15 * (1.0F - var10_before_flash);
         var12 = var12 * var10_before_flash + var15 * (1.0F - var10_before_flash);
      }

      if (this.lastLightningBolt > 0) {
         var15 = (float)this.lastLightningBolt - par2;
         if (var15 > 1.0F) {
            var15 = 1.0F;
         }

         var10_before_flash = var10;
         float var11_before_flash = var11;
         float var12_before_flash = var12;
         var15 *= 0.45F;
         var10 = var10 * (1.0F - var15) + 0.8F * var15;
         var11 = var11 * (1.0F - var15) + 0.8F * var15;
         var12 = var12 * (1.0F - var15) + 1.0F * var15;
         if (this == Minecraft.theMinecraft.theWorld) {
            float raining_strength_for_render_view_entity = Minecraft.theMinecraft.raining_strength_for_render_view_entity;
            float distance_factor = (float)Math.pow((double)raining_strength_for_render_view_entity, 4.0);
            var10 = var10 * distance_factor + var10_before_flash * (1.0F - distance_factor);
            var11 = var11 * distance_factor + var11_before_flash * (1.0F - distance_factor);
            var12 = var12 * distance_factor + var12_before_flash * (1.0F - distance_factor);
         }
      }

      return this.getWorldVec3Pool().getVecFromPool((double)var10, (double)var11, (double)var12);
   }

   public float getCelestialAngle(float par1) {
      return this.provider.calculateCelestialAngle((long)this.getTimeOfDay(), par1);
   }

   public int getMoonPhase() {
      return this.provider.getMoonPhase(this.getTotalWorldTime());
   }

   public float getCurrentMoonPhaseFactor() {
      return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.getTotalWorldTime())];
   }

   public final float getMoonBrightness(float partial_tick, boolean include_weather) {
      float brightness;
      if (this.isBloodMoon24HourPeriod()) {
         brightness = 0.6F;
      } else if (this.isHarvestMoon24HourPeriod()) {
         brightness = 1.0F;
      } else if (this.isBlueMoon24HourPeriod()) {
         brightness = 1.1F;
      } else {
         brightness = this.getCurrentMoonPhaseFactor() * 0.5F + 0.75F;
      }

      if (include_weather && brightness > 0.75F) {
         float apparent_raining_strength = this.getRainStrength(partial_tick);
         if (apparent_raining_strength > 0.0F) {
            brightness = brightness * (1.0F - apparent_raining_strength) + 0.75F * apparent_raining_strength;
         }
      }

      return brightness;
   }

   public final boolean isFullMoon() {
      return this.getMoonPhase() == 0;
   }

   public final boolean isNewMoon() {
      return this.getMoonPhase() == 4;
   }

   public final float getMoonAscensionFactor() {
      int time_of_day = this.getAdjustedTimeOfDay();
      float factor;
      if (time_of_day <= getTimeOfSunrise()) {
         factor = 1.0F - (float)time_of_day / (float)getTimeOfSunrise();
      } else if (time_of_day >= getTimeOfSunset()) {
         factor = (float)(time_of_day - getTimeOfSunset()) / (float)(24000 - getTimeOfSunset());
      } else {
         factor = 0.0F;
      }

      if (factor < 0.0F || factor > 1.0F) {
         Debug.setErrorMessage("getMoonAscensionFactor: value out of bounds " + factor);
      }

      return factor;
   }

   public static final boolean isBloodMoon(long unadjusted_tick, boolean exclusively_at_night) {
      if (exclusively_at_night && isDaytime(unadjusted_tick)) {
         return false;
      } else {
         return (unadjusted_tick / 24000L + 1L) % 32L == 0L && !isBlueMoon(unadjusted_tick, exclusively_at_night);
      }
   }

   public final boolean isBloodMoon(boolean exclusively_at_night) {
      if (!this.isOverworld()) {
         return false;
      } else if (exclusively_at_night && this.isDaytime()) {
         return false;
      } else {
         return (this.getTotalWorldTime() / 24000L + 1L) % 32L == 0L && !this.isBlueMoon(exclusively_at_night);
      }
   }

   public static final boolean isBlueMoon(long unadjusted_tick, boolean exclusively_at_night) {
      if (exclusively_at_night && isDaytime(unadjusted_tick)) {
         return false;
      } else {
         return (unadjusted_tick / 24000L + 1L) % 128L == 0L;
      }
   }

   public final boolean isBlueMoon(boolean exclusively_at_night) {
      if (!this.isOverworld()) {
         return false;
      } else if (exclusively_at_night && this.isDaytime()) {
         return false;
      } else {
         return (this.getTotalWorldTime() / 24000L + 1L) % 128L == 0L;
      }
   }

   public static final boolean isHarvestMoon(long unadjusted_tick, boolean exclusively_at_night) {
      return exclusively_at_night && isDaytime(unadjusted_tick) ? false : isBloodMoon(unadjusted_tick + 192000L, exclusively_at_night);
   }

   public final boolean isHarvestMoon(boolean exclusively_at_night) {
      return isHarvestMoon(this.getTotalWorldTime(), exclusively_at_night);
   }

   public static final boolean isMoonDog(long unadjusted_tick, boolean exclusively_at_night) {
      return exclusively_at_night && isDaytime(unadjusted_tick) ? false : isBlueMoon(unadjusted_tick + 192000L, exclusively_at_night);
   }

   public final boolean isMoonDog(boolean exclusively_at_night) {
      return isMoonDog(this.getTotalWorldTime(), exclusively_at_night);
   }

   public float getCelestialAngleRadians(float par1) {
      float var2 = this.getCelestialAngle(par1);
      return var2 * 3.1415927F * 2.0F;
   }

   public Vec3 getCloudColour(float par1) {
      float var2 = this.getCelestialAngle(par1);
      float var3 = MathHelper.cos(var2 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      float var4 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
      float var5 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
      float var6 = (float)(this.cloudColour & 255L) / 255.0F;
      float var7 = this.getRainStrength(par1);
      float var8;
      float var9;
      if (var7 > 0.0F) {
         var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      var8 = this.getWeightedThunderStrength(par1);
      if (var8 > 0.0F) {
         var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var8 * 0.95F;
         var4 = var4 * var10 + var9 * (1.0F - var10);
         var5 = var5 * var10 + var9 * (1.0F - var10);
         var6 = var6 * var10 + var9 * (1.0F - var10);
      }

      return this.getWorldVec3Pool().getVecFromPool((double)var4, (double)var5, (double)var6);
   }

   public final Vec3 getFogColor(float par1, EntityLivingBase viewer) {
      float var2 = this.getCelestialAngle(par1);
      return this.provider.getFogColor(var2, par1, viewer);
   }

   public int getPrecipitationHeight(int par1, int par2) {
      return this.getChunkFromBlockCoords(par1, par2).getPrecipitationHeight(par1 & 15, par2 & 15);
   }

   public int getTopSolidOrLiquidBlock(int par1, int par2) {
      int x = par1;
      int z = par2;
      Chunk var3 = this.getChunkFromBlockCoords(par1, par2);
      int var4 = var3.getTopFilledSegment() + 15;
      par1 &= 15;

      for(par2 &= 15; var4 > 0; --var4) {
         int var5 = var3.getBlockID(par1, var4, par2);
         if (var5 != 0) {
            Block block = Block.getBlock(var5);
            if (this.isBlockSolid(block, x, var4, z) && block.blockMaterial != Material.tree_leaves) {
               return var4 + 1;
            }
         }
      }

      return -1;
   }

   public int getTopSolidOrLiquidBlockMITE(int par1, int par2, boolean ignore_leaves) {
      int x = par1;
      int z = par2;
      Chunk var3 = this.getChunkFromBlockCoords(par1, par2);
      int var4 = var3.getTopFilledSegment() + 15;
      par1 &= 15;

      for(par2 &= 15; var4 > 0; --var4) {
         Block block = Block.getBlock(var3.getBlockID(par1, var4, par2));
         if (block != null && (!ignore_leaves || block.blockMaterial != Material.tree_leaves) && (block.isSolid(this, x, var4, z) || block.blockMaterial.isLiquid())) {
            return var4;
         }
      }

      return -1;
   }

   public float getStarBrightness(float par1) {
      float var2 = this.getCelestialAngle(par1);
      float var3 = 1.0F - (MathHelper.cos(var2 * 3.1415927F * 2.0F) * 2.0F + 0.25F);
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return var3 * var3 * 0.5F;
   }

   public void scheduleBlockUpdate(int par1, int par2, int par3, int par4, int par5) {
   }

   public void scheduleBlockUpdateWithPriority(int par1, int par2, int par3, int par4, int par5, int par6) {
   }

   public void scheduleBlockUpdateFromLoad(int par1, int par2, int par3, int par4, int par5, int par6) {
   }

   public int getHourOfDay() {
      return this.getAdjustedTimeOfDay() / 1000;
   }

   public String getHourOfDayAMPM() {
      return getHourOfDayAMPM(this.getHourOfDay());
   }

   public static String getHourOfDayAMPM(int hour_of_day) {
      return hour_of_day == 0 ? "MDNT" : (hour_of_day == 12 ? "NOON" : (hour_of_day < 12 ? hour_of_day + "AM" : hour_of_day - 12 + "PM"));
   }

   public void updateEntities() {
      this.theProfiler.startSection("entities");
      this.theProfiler.startSection("global");

      int var1;
      Entity var2;
      CrashReport var4;
      CrashReportCategory var5;
      for(var1 = 0; var1 < this.weatherEffects.size(); ++var1) {
         var2 = (Entity)this.weatherEffects.get(var1);

         try {
            ++var2.ticksExisted;
            ++var2.despawn_counter;
            var2.onUpdate();
         } catch (Throwable var13) {
            Throwable var8 = var13;
            var4 = CrashReport.makeCrashReport(var8, "Ticking entity");
            var5 = var4.makeCategory("Entity being ticked");
            if (var2 == null) {
               var5.addCrashSection("Entity", "~~NULL~~");
            } else {
               var2.addEntityCrashInfo(var5);
            }

            throw new ReportedException(var4);
         }

         if (var2.isDead) {
            this.weatherEffects.remove(var1--);
         }
      }

      this.theProfiler.endStartSection("remove");
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      Chunk chunk;
      for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
         var2 = (Entity)this.unloadedEntityList.get(var1);
         if (var2.isAddedToAChunk()) {
            chunk = var2.getChunkAddedTo();
            if (this.chunkExists(chunk.xPosition, chunk.zPosition)) {
               var2.removeFromChunk();
            }
         }
      }

      for(var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
         this.onEntityRemoved((Entity)this.unloadedEntityList.get(var1));
      }

      this.unloadedEntityList.clear();
      this.theProfiler.endStartSection("regular");

      for(var1 = 0; var1 < this.loadedEntityList.size(); ++var1) {
         var2 = (Entity)this.loadedEntityList.get(var1);
         if (var2.ridingEntity != null) {
            if (!var2.ridingEntity.isDead && var2.ridingEntity.riddenByEntity == var2) {
               continue;
            }

            var2.ridingEntity.riddenByEntity = null;
            var2.ridingEntity = null;
         }

         this.theProfiler.startSection("tick");
         if (!var2.isDead) {
            try {
               this.updateEntity(var2);
            } catch (Throwable var12) {
               var4 = CrashReport.makeCrashReport(var12, "Ticking entity");
               var5 = var4.makeCategory("Entity being ticked");
               var2.addEntityCrashInfo(var5);
               throw new ReportedException(var4);
            }
         }

         this.theProfiler.endSection();
         this.theProfiler.startSection("remove");
         if (var2.isDead) {
            if (var2.isAddedToAChunk()) {
               chunk = var2.getChunkAddedTo();
               if (this.chunkExists(chunk.xPosition, chunk.zPosition)) {
                  var2.removeFromChunk();
               }
            }

            this.loadedEntityList.remove(var1--);
            this.onEntityRemoved(var2);
         }

         this.theProfiler.endSection();
      }

      this.theProfiler.endStartSection("tileEntities");
      this.scanningTileEntities = true;
      Iterator var14 = this.loadedTileEntityList.iterator();

      while(var14.hasNext()) {
         TileEntity var9 = (TileEntity)var14.next();
         if (!var9.isInvalid() && var9.hasWorldObj() && this.blockExists(var9.xCoord, var9.yCoord, var9.zCoord)) {
            try {
               var9.updateEntity();
            } catch (Throwable var11) {
               Throwable var6 = var11;
               var4 = CrashReport.makeCrashReport(var6, "Ticking tile entity");
               var5 = var4.makeCategory("Tile entity being ticked");
               var9.func_85027_a(var5);
               throw new ReportedException(var4);
            }
         }

         if (var9.isInvalid()) {
            var14.remove();
            if (this.chunkExists(var9.xCoord >> 4, var9.zCoord >> 4)) {
               Chunk var11 = this.getChunkFromChunkCoords(var9.xCoord >> 4, var9.zCoord >> 4);
               if (var11 != null) {
                  var11.removeChunkBlockTileEntity(var9.xCoord & 15, var9.yCoord, var9.zCoord & 15);
               }
            }
         }
      }

      this.scanningTileEntities = false;
      if (!this.entityRemoval.isEmpty()) {
         this.loadedTileEntityList.removeAll(this.entityRemoval);
         this.entityRemoval.clear();
      }

      this.theProfiler.endStartSection("pendingTileEntities");
      if (!this.addedTileEntityList.isEmpty()) {
         for(int var10 = 0; var10 < this.addedTileEntityList.size(); ++var10) {
            TileEntity var12 = (TileEntity)this.addedTileEntityList.get(var10);
            if (!var12.isInvalid()) {
               if (!this.loadedTileEntityList.contains(var12)) {
                  this.loadedTileEntityList.add(var12);
               }

               if (this.chunkExists(var12.xCoord >> 4, var12.zCoord >> 4)) {
                  Chunk var15 = this.getChunkFromChunkCoords(var12.xCoord >> 4, var12.zCoord >> 4);
                  if (var15 != null) {
                     var15.setChunkBlockTileEntity(var12.xCoord & 15, var12.yCoord, var12.zCoord & 15, var12);
                  }
               }

               this.markBlockForUpdate(var12.xCoord, var12.yCoord, var12.zCoord);
            }
         }

         this.addedTileEntityList.clear();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
   }

   public void addTileEntity(Collection par1Collection) {
      if (this.scanningTileEntities) {
         this.addedTileEntityList.addAll(par1Collection);
      } else {
         this.loadedTileEntityList.addAll(par1Collection);
      }

   }

   public void updateEntity(Entity par1Entity) {
      this.updateEntityWithOptionalForce(par1Entity, true);
   }

   public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2) {
      int var3 = MathHelper.floor_double(par1Entity.posX);
      int var4 = MathHelper.floor_double(par1Entity.posZ);
      byte var5 = 32;
      if (par2 && !this.checkChunksExist(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5)) {
         if (par1Entity instanceof IMob && par1Entity instanceof EntityLiving && par1Entity.ridingEntity == null && par1Entity.riddenByEntity == null) {
            ++par1Entity.despawn_counter;
            ((EntityLiving)par1Entity).tryDespawnEntity();
         }
      } else {
         par1Entity.lastTickPosX = par1Entity.posX;
         par1Entity.lastTickPosY = par1Entity.posY;
         par1Entity.lastTickPosZ = par1Entity.posZ;
         par1Entity.prevRotationYaw = par1Entity.rotationYaw;
         par1Entity.prevRotationPitch = par1Entity.rotationPitch;
         if (par2 && par1Entity.isAddedToAChunk()) {
            ++par1Entity.ticksExisted;
            ++par1Entity.despawn_counter;
            if (par1Entity.ridingEntity != null) {
               par1Entity.updateRidden();
            } else {
               par1Entity.onUpdate();
            }
         }

         this.theProfiler.startSection("chunkCheck");
         if (Double.isNaN(par1Entity.posX) || Double.isInfinite(par1Entity.posX)) {
            par1Entity.posX = par1Entity.lastTickPosX;
         }

         if (Double.isNaN(par1Entity.posY) || Double.isInfinite(par1Entity.posY)) {
            par1Entity.posY = par1Entity.lastTickPosY;
         }

         if (Double.isNaN(par1Entity.posZ) || Double.isInfinite(par1Entity.posZ)) {
            par1Entity.posZ = par1Entity.lastTickPosZ;
         }

         if (Double.isNaN((double)par1Entity.rotationPitch) || Double.isInfinite((double)par1Entity.rotationPitch)) {
            par1Entity.rotationPitch = par1Entity.prevRotationPitch;
         }

         if (Double.isNaN((double)par1Entity.rotationYaw) || Double.isInfinite((double)par1Entity.rotationYaw)) {
            par1Entity.rotationYaw = par1Entity.prevRotationYaw;
         }

         int var6 = par1Entity.getChunkPosX();
         int var7 = par1Entity.getChunkCurrentlyInSectionIndex();
         int var8 = par1Entity.getChunkPosZ();
         if (par1Entity.isAddedToAChunk() && par1Entity.getChunkAddedTo().worldObj != this) {
            par1Entity.removeFromChunk();
         }

         if (!par1Entity.isAddedToAChunk() || par1Entity.getChunkAddedTo().xPosition != var6 || par1Entity.chunk_added_to_section_index != var7 || par1Entity.getChunkAddedTo().zPosition != var8) {
            if (par1Entity.isAddedToAChunk()) {
               Chunk chunk = par1Entity.getChunkAddedTo();
               if (chunk.worldObj != this) {
                  Minecraft.setErrorMessage("updateEntityWithOptionalForce: entity still belongs to a chunk of a different world");
                  (new Exception()).printStackTrace();
               }

               if (this.chunkExists(chunk.xPosition, chunk.zPosition)) {
                  par1Entity.removeFromChunk();
               } else {
                  Minecraft.setErrorMessage("updateEntityWithOptionalForce: " + par1Entity.getEntityName() + " was added to a chunk that no longer exists?");
               }
            }

            if (this.chunkExists(var6, var8)) {
               this.getChunkFromChunkCoords(var6, var8).addEntity(par1Entity);
            } else {
               par1Entity.setChunkAddedToUnchecked((Chunk)null, -1);
            }
         }

         this.theProfiler.endSection();
         if (par2 && par1Entity.isAddedToAChunk() && par1Entity.riddenByEntity != null) {
            if (!par1Entity.riddenByEntity.isDead && par1Entity.riddenByEntity.ridingEntity == par1Entity) {
               this.updateEntity(par1Entity.riddenByEntity);
            } else {
               par1Entity.riddenByEntity.ridingEntity = null;
               par1Entity.riddenByEntity = null;
            }
         }
      }

   }

   public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB) {
      return this.checkNoEntityCollision(par1AxisAlignedBB, (Entity)null);
   }

   public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB, Entity par2Entity) {
      List var3 = this.getEntitiesWithinAABBExcludingEntity((Entity)null, par1AxisAlignedBB);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         Entity var5 = (Entity)var3.get(var4);
         if (!var5.isDead && var5.preventEntitySpawning && var5 != par2Entity) {
            return false;
         }
      }

      return true;
   }

   public final boolean checkBlockCollision(AxisAlignedBB bounding_box) {
      int min_x = bounding_box.getBlockCoordForMinX();
      int max_x = bounding_box.getBlockCoordForMaxX();
      int min_y = bounding_box.getBlockCoordForMinY();
      int max_y = bounding_box.getBlockCoordForMaxY();
      int min_z = bounding_box.getBlockCoordForMinZ();
      int max_z = bounding_box.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               if (this.getBlock(x, y, z) != null) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final boolean isAnyLiquid(AxisAlignedBB bounding_box) {
      int min_x = bounding_box.getBlockCoordForMinX();
      int max_x = bounding_box.getBlockCoordForMaxX();
      int min_y = bounding_box.getBlockCoordForMinY();
      int max_y = bounding_box.getBlockCoordForMaxY();
      int min_z = bounding_box.getBlockCoordForMinZ();
      int max_z = bounding_box.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               if (this.getBlockMaterial(x, y, z).isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final boolean isAnyLava(AxisAlignedBB bounding_box) {
      int min_x = bounding_box.getBlockCoordForMinX();
      int max_x = bounding_box.getBlockCoordForMaxX();
      int min_y = bounding_box.getBlockCoordForMinY();
      int max_y = bounding_box.getBlockCoordForMaxY();
      int min_z = bounding_box.getBlockCoordForMinZ();
      int max_z = bounding_box.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               if (this.getBlockMaterial(x, y, z) == Material.lava) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final boolean isOnlyWater(AxisAlignedBB bounding_box) {
      int min_x = bounding_box.getBlockCoordForMinX();
      int max_x = bounding_box.getBlockCoordForMaxX();
      int min_y = bounding_box.getBlockCoordForMinY();
      int max_y = bounding_box.getBlockCoordForMaxY();
      int min_z = bounding_box.getBlockCoordForMinZ();
      int max_z = bounding_box.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               if (this.getBlockMaterial(x, y, z) != Material.water) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public final boolean isBoundingBoxBurning(AxisAlignedBB par1AxisAlignedBB, boolean include_lava) {
      int min_x = par1AxisAlignedBB.getBlockCoordForMinX();
      int max_x = par1AxisAlignedBB.getBlockCoordForMaxX();
      int min_y = par1AxisAlignedBB.getBlockCoordForMinY();
      int max_y = par1AxisAlignedBB.getBlockCoordForMaxY();
      int min_z = par1AxisAlignedBB.getBlockCoordForMinZ();
      int max_z = par1AxisAlignedBB.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               int block_id = this.getBlockId(x, y, z);
               if (block_id == Block.fire.blockID || include_lava && (block_id == Block.lavaMoving.blockID || block_id == Block.lavaStill.blockID)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final boolean doesBoundingBoxContainBlock(AxisAlignedBB par1AxisAlignedBB, int block_id, int metadata) {
      int min_x = par1AxisAlignedBB.getBlockCoordForMinX();
      int max_x = par1AxisAlignedBB.getBlockCoordForMaxX();
      int min_y = par1AxisAlignedBB.getBlockCoordForMinY();
      int max_y = par1AxisAlignedBB.getBlockCoordForMaxY();
      int min_z = par1AxisAlignedBB.getBlockCoordForMinZ();
      int max_z = par1AxisAlignedBB.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               if (this.getBlockId(x, y, z) == block_id && (metadata < 0 || this.getBlockMetadata(x, y, z) == metadata)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final boolean extinguishAllFireBlocksInBoundingBox(AxisAlignedBB bb) {
      boolean result = false;
      int min_x = bb.getBlockCoordForMinX();
      int max_x = bb.getBlockCoordForMaxX();
      int min_y = bb.getBlockCoordForMinY();
      int max_y = bb.getBlockCoordForMaxY();
      int min_z = bb.getBlockCoordForMinZ();
      int max_z = bb.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               int block_id = this.getBlockId(x, y, z);
               if (block_id == Block.fire.blockID) {
                  this.douseFire(x, y, z, (Entity)null);
                  result = true;
               }
            }
         }
      }

      return result;
   }

   public final boolean handleMaterialAcceleration(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity par3Entity) {
      int min_x = par1AxisAlignedBB.getBlockCoordForMinX();
      int max_x = par1AxisAlignedBB.getBlockCoordForMaxX();
      int min_y = par1AxisAlignedBB.getBlockCoordForMinY();
      int max_y = par1AxisAlignedBB.getBlockCoordForMaxY();
      int min_z = par1AxisAlignedBB.getBlockCoordForMinZ();
      int max_z = par1AxisAlignedBB.getBlockCoordForMaxZ();
      boolean is_in_material = false;
      Vec3 vec3 = null;
      boolean is_pushed_by_material = par3Entity.isPushedByWater();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               int block_id = this.getBlockId(x, y, z);
               if (block_id != 0) {
                  Block block = Block.getBlock(block_id);
                  if (block.blockMaterial == par2Material) {
                     double fluid_top_y = (double)((float)(y + 1) - BlockFluid.getFluidHeightPercent(this.getBlockMetadata(x, y, z)));
                     if ((double)max_y + 1.0 >= fluid_top_y) {
                        is_in_material = true;
                        if (is_pushed_by_material) {
                           if (vec3 == null) {
                              vec3 = this.getWorldVec3Pool().getVecFromPool(0.0, 0.0, 0.0);
                           }

                           block.velocityToAddToEntity(this, x, y, z, par3Entity, vec3);
                        }
                     }
                  }
               }
            }
         }
      }

      if (!is_in_material) {
         return false;
      } else {
         if (is_pushed_by_material && vec3.lengthVector() > 0.0) {
            vec3 = vec3.normalize();
            double var18 = 0.014;
            par3Entity.motionX += vec3.xCoord * var18;
            par3Entity.motionY += vec3.yCoord * var18;
            par3Entity.motionZ += vec3.zCoord * var18;
         }

         return true;
      }
   }

   public boolean isMaterialInBB(AxisAlignedBB par1AxisAlignedBB, Material par2Material) {
      int var3 = MathHelper.floor_double(par1AxisAlignedBB.minX);
      int var4 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0);
      int var5 = MathHelper.floor_double(par1AxisAlignedBB.minY);
      int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0);
      int var7 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
      int var8 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var5; var10 < var6; ++var10) {
            for(int var11 = var7; var11 < var8; ++var11) {
               Block var12 = Block.blocksList[this.getBlockId(var9, var10, var11)];
               if (var12 != null && var12.blockMaterial == par2Material) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isAABBInMaterial(AxisAlignedBB par1AxisAlignedBB, Material par2Material) {
      int var3 = MathHelper.floor_double(par1AxisAlignedBB.minX);
      int var4 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0);
      int var5 = MathHelper.floor_double(par1AxisAlignedBB.minY);
      int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0);
      int var7 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
      int var8 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var5; var10 < var6; ++var10) {
            for(int var11 = var7; var11 < var8; ++var11) {
               Block var12 = Block.blocksList[this.getBlockId(var9, var10, var11)];
               if (var12 != null && var12.blockMaterial == par2Material) {
                  int var13 = this.getBlockMetadata(var9, var10, var11);
                  double var14 = (double)(var10 + 1);
                  if (var13 < 8) {
                     var14 = (double)(var10 + 1) - (double)var13 / 8.0;
                  }

                  if (var14 >= par1AxisAlignedBB.minY) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion createExplosion(Entity exploder, double posX, double posY, double posZ, float explosion_size_vs_blocks, float explosion_size_vs_living_entities, boolean is_smoking) {
      return this.newExplosion(exploder, posX, posY, posZ, explosion_size_vs_blocks, explosion_size_vs_living_entities, false, is_smoking);
   }

   public Explosion newExplosion(Entity exploder, double posX, double posY, double posZ, float explosion_size_vs_blocks, float explosion_size_vs_living_entities, boolean is_flaming, boolean is_smoking) {
      Explosion explosion = new Explosion(this, exploder, posX, posY, posZ, explosion_size_vs_blocks, explosion_size_vs_living_entities);
      explosion.isFlaming = is_flaming;
      explosion.isSmoking = is_smoking;
      explosion.doExplosionA();
      explosion.doExplosionB(true);
      return explosion;
   }

   public float getBlockDensity(Vec3 par1Vec3, AxisAlignedBB par2AxisAlignedBB) {
      double var3 = 1.0 / ((par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * 2.0 + 1.0);
      double var5 = 1.0 / ((par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * 2.0 + 1.0);
      double var7 = 1.0 / ((par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * 2.0 + 1.0);
      int var9 = 0;
      int var10 = 0;

      for(float var11 = 0.0F; var11 <= 1.0F; var11 = (float)((double)var11 + var3)) {
         for(float var12 = 0.0F; var12 <= 1.0F; var12 = (float)((double)var12 + var5)) {
            for(float var13 = 0.0F; var13 <= 1.0F; var13 = (float)((double)var13 + var7)) {
               double var14 = par2AxisAlignedBB.minX + (par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * (double)var11;
               double var16 = par2AxisAlignedBB.minY + (par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * (double)var12;
               double var18 = par2AxisAlignedBB.minZ + (par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * (double)var13;
               if (this.checkForLineOfPhysicalReach(this.getVec3(var14, var16, var18), par1Vec3)) {
                  ++var9;
               }

               ++var10;
            }
         }
      }

      return (float)var9 / (float)var10;
   }

   public final RaycastCollision getBlockCollisionForSelection(Vec3 origin, Vec3 limit, boolean hit_liquids) {
      return (new Raycast(this, origin, limit)).setForSelection(hit_liquids).performVsBlocks().getBlockCollision();
   }

   public final RaycastCollision getBlockCollisionForVision(Vec3 origin, Vec3 limit, boolean ignore_leaves) {
      return (new Raycast(this, origin, limit)).setForVision(ignore_leaves).performVsBlocks().getBlockCollision();
   }

   public final boolean checkForLineOfSight(Vec3 origin, Vec3 limit, boolean ignore_leaves) {
      return this.getBlockCollisionForVision(origin, limit, ignore_leaves) == null;
   }

   public final RaycastCollision getBlockCollisionForPhysicalReach(Vec3 origin, Vec3 limit) {
      return (new Raycast(this, origin, limit)).setForPhysicalReach().performVsBlocks().getBlockCollision();
   }

   public final boolean checkForLineOfPhysicalReach(Vec3 origin, Vec3 limit) {
      return this.getBlockCollisionForPhysicalReach(origin, limit) == null;
   }

   public final RaycastCollision getBlockCollisionForPolicies(Vec3 origin, Vec3 limit, RaycastPolicies policies) {
      return (new Raycast(this, origin, limit)).setPolicies(policies).performVsBlocks().getBlockCollision();
   }

   public final boolean checkForNoBlockCollision(Vec3 origin, Vec3 limit, RaycastPolicies policies) {
      return this.getBlockCollisionForPolicies(origin, limit, policies) == null;
   }

   public final RaycastCollision getBlockCollisionForPolicies(Vec3 origin, Vec3 limit, RaycastPolicies policies, Entity originator) {
      return (new Raycast(this, origin, limit)).setPolicies(policies).setOriginator(originator).performVsBlocks().getBlockCollision();
   }

   public boolean extinguishFire(EntityPlayer player, int x, int y, int z, EnumFace face) {
      x = face.getNeighborX(x);
      y = face.getNeighborY(y);
      z = face.getNeighborZ(z);
      if (this.getBlock(x, y, z) != Block.fire) {
         return false;
      } else {
         this.playAuxSFXAtEntity(player, 1004, x, y, z, 0);
         this.setBlockToAir(x, y, z);
         return true;
      }
   }

   public String getDebugLoadedEntities() {
      return "All: " + this.loadedEntityList.size();
   }

   public String getProviderName() {
      return this.chunkProvider.makeString();
   }

   public TileEntity getBlockTileEntity(int par1, int par2, int par3) {
      if (par2 >= 0 && par2 < 256) {
         TileEntity var4 = null;
         int var5;
         TileEntity var6;
         if (this.scanningTileEntities) {
            for(var5 = 0; var5 < this.addedTileEntityList.size(); ++var5) {
               var6 = (TileEntity)this.addedTileEntityList.get(var5);
               if (!var6.isInvalid() && var6.xCoord == par1 && var6.yCoord == par2 && var6.zCoord == par3) {
                  var4 = var6;
                  break;
               }
            }
         }

         if (var4 == null) {
            Chunk var7 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
            if (var7 != null) {
               var4 = var7.getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
            }
         }

         if (var4 == null) {
            for(var5 = 0; var5 < this.addedTileEntityList.size(); ++var5) {
               var6 = (TileEntity)this.addedTileEntityList.get(var5);
               if (!var6.isInvalid() && var6.xCoord == par1 && var6.yCoord == par2 && var6.zCoord == par3) {
                  var4 = var6;
                  break;
               }
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   public void setBlockTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity) {
      if (par4TileEntity != null && !par4TileEntity.isInvalid()) {
         if (this.scanningTileEntities) {
            par4TileEntity.xCoord = par1;
            par4TileEntity.yCoord = par2;
            par4TileEntity.zCoord = par3;
            Iterator var5 = this.addedTileEntityList.iterator();

            while(var5.hasNext()) {
               TileEntity var6 = (TileEntity)var5.next();
               if (var6.xCoord == par1 && var6.yCoord == par2 && var6.zCoord == par3) {
                  var6.invalidate();
                  var5.remove();
               }
            }

            this.addedTileEntityList.add(par4TileEntity);
         } else {
            this.loadedTileEntityList.add(par4TileEntity);
            Chunk var7 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
            if (var7 != null) {
               var7.setChunkBlockTileEntity(par1 & 15, par2, par3 & 15, par4TileEntity);
            }
         }
      }

   }

   public void removeBlockTileEntity(int par1, int par2, int par3) {
      TileEntity var4 = this.getBlockTileEntity(par1, par2, par3);
      if (var4 != null && this.scanningTileEntities) {
         var4.invalidate();
         this.addedTileEntityList.remove(var4);
      } else {
         if (var4 != null) {
            this.addedTileEntityList.remove(var4);
            this.loadedTileEntityList.remove(var4);
         }

         Chunk var5 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
         if (var5 != null) {
            var5.removeChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
         }
      }

   }

   public void markTileEntityForDespawn(TileEntity par1TileEntity) {
      this.entityRemoval.add(par1TileEntity);
   }

   public final boolean isBlockStandardFormOpaqueCube(int par1, int par2, int par3) {
      return Block.isBlockOpaqueStandardFormCube(this, par1, par2, par3);
   }

   public final boolean isBlockNormalCube(int par1, int par2, int par3) {
      return Block.isNormalCube(this.getBlockId(par1, par2, par3));
   }

   public final boolean isBlockSolidStandardFormCube(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      if (block == null) {
         return false;
      } else {
         return block.isAlwaysSolidStandardFormCube() ? true : block.isSolidStandardFormCube(this.getBlockMetadata(x, y, z));
      }
   }

   public final boolean isBlockFullSolidCube(int x, int y, int z) {
      int block_id = this.getBlockId(x, y, z);
      if (block_id == 0) {
         return false;
      } else {
         Block block = Block.getBlock(block_id);
         if (block == null) {
            return false;
         } else if (block.isAlwaysSolidStandardFormCube()) {
            return true;
         } else {
            return block.isNeverSolidStandardFormCube() ? false : block.isSolidStandardFormCube(this.getBlockMetadata(x, y, z));
         }
      }
   }

   public boolean isBlockTopFlatAndSolid(int x, int y, int z) {
      Block block = Block.blocksList[this.getBlockId(x, y, z)];
      return block != null && block.isTopFlatAndSolid(this.getBlockMetadata(x, y, z));
   }

   public static boolean isBlockTopFlatAndSolid(Block block, int metadata) {
      return block != null && block.isTopFlatAndSolid(metadata);
   }

   public final boolean isBlockNormalCubeDefault(int par1, int par2, int par3, boolean par4) {
      if (this.isWithinBlockDomain(par1, par3)) {
         Chunk var5 = this.chunkProvider.provideChunk(par1 >> 4, par3 >> 4);
         return var5 != null && !var5.isEmpty() ? Block.is_normal_cube_lookup[this.getBlockId(par1, par2, par3)] : par4;
      } else {
         return par4;
      }
   }

   public final void calculateInitialSkylight() {
      int var1 = this.calculateSkylightSubtracted(1.0F);
      if (var1 != this.skylightSubtracted) {
         this.skylightSubtracted = var1;
      }

   }

   public void setAllowedSpawnTypes(boolean par1, boolean par2) {
      this.spawnHostileMobs = par1;
      this.spawnPeacefulMobs = par2;
   }

   public void tick() {
      if (this.isRemote) {
         if (this instanceof WorldServer) {
            Minecraft.setErrorMessage("tick: isRemote is true but world is instanceof WorldServer");
         }
      } else if (!(this instanceof WorldServer)) {
         Minecraft.setErrorMessage("tick: isRemote is false but world is not instanceof WorldServer");
      }

      long tt = this.total_time;
      long twt = this.getTotalWorldTime();
      if (Minecraft.inDevMode() && tt != twt) {
         Minecraft.setErrorMessage("tick: total world time discrepency: " + tt + " vs " + twt + " for " + this.getDimensionId() + ", " + this.isRemote);
         this.total_time = twt;
      }

      this.updateWeather();
   }

   public void checkPendingEntitySpawns() {
      if (!this.isRemote) {
         long total_world_time = this.getTotalWorldTime();
         Iterator i = this.pending_entity_spawns.iterator();

         while(i.hasNext()) {
            EntitySpawnPendingEntry entry = (EntitySpawnPendingEntry)i.next();
            if (entry.scheduled_spawn_time <= total_world_time) {
               this.spawnEntityInWorld(entry.entity);
               i.remove();
            }
         }

      }
   }

   public final void _calculateInitialWeather() {
      if (this.canPrecipitate()) {
         this.rainingStrength = 0.0F;
         this.thunderingStrength = 0.0F;
         if (this.isPrecipitating(false)) {
            this.rainingStrength = 1.0F;
            if (this.isThundering(false)) {
               this.thunderingStrength = 1.0F;
            }
         }

      }
   }

   protected void updateWeather() {
      if (this.isRemote) {
         Minecraft.setErrorMessage("updateWeather: Called on client?");
      }

      if (!this.provider.hasNoSky) {
         this.prevRainingStrength = this.rainingStrength;
         if (this.isPrecipitating(false)) {
            this.rainingStrength = (float)((double)this.rainingStrength + 0.01);
         } else {
            this.rainingStrength = (float)((double)this.rainingStrength - 0.01);
         }

         if (this.rainingStrength < 0.0F) {
            this.rainingStrength = 0.0F;
         }

         if (this.rainingStrength > 1.0F) {
            this.rainingStrength = 1.0F;
         }

         this.prevThunderingStrength = this.thunderingStrength;
         if (this.isThundering(false)) {
            this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01);
         } else {
            this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01);
         }

         if (this.thunderingStrength < 0.0F) {
            this.thunderingStrength = 0.0F;
         }

         if (this.thunderingStrength > 1.0F) {
            this.thunderingStrength = 1.0F;
         }
      }

   }

   public void checkLightingOfRandomBlockInView(boolean minimal) {
      if (!this.hasSkylight()) {
         Debug.setErrorMessage("checkLightingOfRandomBlockInView: why called for world without skylight?");
      } else {
         ++this.times_checkLightingOfRandomBlockInView_called;
         EntityLivingBase viewer = Minecraft.theMinecraft.renderViewEntity;
         if (viewer != null) {
            if ((!minimal || this.times_checkLightingOfRandomBlockInView_called % 40 != 0) && (!minimal || this.times_checkLightingOfRandomBlockInView_called % 20 != 0) && MITEConstant.maxRandomRaycastsPerTickForCorrectiveLightingUpdates(this) > 0) {
               int raycast_seed_offset = viewer.raycast_seed_offset;
               viewer.raycast_seed_offset = this.getTimeOfDay();

               for(int i = 0; i < (minimal ? 4 : 4); ++i) {
                  float rotationYaw = viewer.rotationYaw;
                  float rotationPitch = viewer.rotationPitch;
                  viewer.rotationYaw = (float)((double)viewer.rotationYaw + (Math.random() * 181.0 - 90.0));
                  viewer.rotationPitch = (float)((double)viewer.rotationPitch + (Math.random() * 181.0 - 90.0));
                  Raycast raycast = new Raycast(viewer, 1.0F, 128.0);
                  int raycast_type = i % 4;
                  if (raycast_type == 0) {
                     raycast.setForSelection(true);
                  } else if (raycast_type == 1) {
                     raycast.setForVision(false);
                  } else if (raycast_type == 2) {
                     raycast.setForBluntProjectile(viewer);
                  } else if (raycast_type == 3) {
                     raycast.setForPiercingProjectile(viewer);
                  }

                  RaycastCollision rc = raycast.performVsBlocksSingle().getBlockCollision();
                  if (rc != null && rc.isBlock() && MathHelper.isInRange(rc.neighbor_block_y, 0, 255)) {
                     int x = rc.neighbor_block_x;
                     int z = rc.neighbor_block_z;
                     Chunk chunk = this.getChunkFromBlockCoords(x, z);
                     if (!chunk.isEmpty()) {
                        this.updateLightByType(EnumSkyBlock.Sky, x, rc.neighbor_block_y, z, this.canUpdateLightByType(x, z), chunk);
                     }
                  }

                  viewer.rotationYaw = rotationYaw;
                  viewer.rotationPitch = rotationPitch;
               }

               viewer.raycast_seed_offset = raycast_seed_offset;
            }

         }
      }
   }

   protected void setActivePlayerChunks() {
      this.activeChunkSet.clear();
      this.theProfiler.startSection("buildList");

      for(int var1 = 0; var1 < this.playerEntities.size(); ++var1) {
         EntityPlayer var2 = (EntityPlayer)this.playerEntities.get(var1);
         int var3 = MathHelper.floor_double(var2.posX / 16.0);
         int var4 = MathHelper.floor_double(var2.posZ / 16.0);
         byte var5 = 7;

         for(int var6 = -var5; var6 <= var5; ++var6) {
            for(int var7 = -var5; var7 <= var5; ++var7) {
               this.activeChunkSet.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
            }
         }
      }

      this.theProfiler.endSection();
   }

   protected void setActivePlayerChunksAndCheckLight() {
      this.setActivePlayerChunks();
      if (this.ambientTickCountdown > 0) {
         --this.ambientTickCountdown;
      }

      this.theProfiler.startSection("playerCheckLight");
      if (this.isRemote && this.hasSkylight()) {
         this.checkLightingOfRandomBlockInView(true);
      }

      this.theProfiler.endSection();
   }

   protected void moodSoundAndLightCheck(int par1, int par2, Chunk par3Chunk) {
      this.theProfiler.endStartSection("moodSound");
      if (this.ambientTickCountdown == 0 && !this.isRemote) {
         this.updateLCG = this.updateLCG * 3 + 1013904223;
         int var4 = this.updateLCG >> 2;
         int var5 = var4 & 15;
         int var6 = var4 >> 8 & 15;
         int var7 = var4 >> 16 & 127;
         int var8 = par3Chunk.getBlockID(var5, var7, var6);
         var5 += par1;
         var6 += par2;
         if (var8 == 0 && this.getFullBlockLightValue(var5, var7, var6) <= this.rand.nextInt(8) && this.getSavedLightValue(EnumSkyBlock.Sky, var5, var7, var6) <= 0) {
            EntityPlayer var9 = this.getClosestPlayer((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5, 8.0, true);
            if (var9 != null && var9.getDistanceSq((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5) > 4.0) {
               if (Minecraft.getMinecraft() == null) {
                  this.playSoundEffect((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
               } else {
                  Minecraft.getMinecraft().sndManager.playSoundFX("ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
               }

               this.ambientTickCountdown = this.getNextAmbientTickCountdown(false);
            }
         }
      }

      this.theProfiler.endStartSection("checkLight");
      if (!this.isRemote) {
         par3Chunk.performPendingSkylightUpdatesIfPossible();
         par3Chunk.performPendingBlocklightUpdatesIfPossible();
      }

   }

   protected void tickBlocksAndAmbiance() {
      this.setActivePlayerChunksAndCheckLight();
   }

   public boolean isBlockFreezable(int par1, int par2, int par3) {
      return this.canBlockFreeze(par1, par2, par3, false);
   }

   public boolean isBlockFreezableNaturally(int par1, int par2, int par3) {
      return this.canBlockFreeze(par1, par2, par3, true);
   }

   public boolean isFreezing(int x, int z) {
      return this.getBiomeGenForCoords(x, z).isFreezing();
   }

   public boolean canBlockFreeze(int par1, int par2, int par3, boolean par4) {
      BiomeGenBase var5 = this.getBiomeGenForCoords(par1, par3);
      float var6 = var5.getFloatTemperature();
      if (var6 > 0.15F) {
         return false;
      } else {
         if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10) {
            int var7 = this.getBlockId(par1, par2, par3);
            if ((var7 == Block.waterStill.blockID || var7 == Block.waterMoving.blockID) && this.getBlockMetadata(par1, par2, par3) == 0) {
               if (!par4) {
                  return true;
               }

               boolean var8 = true;
               if (var8 && this.getBlockMaterial(par1 - 1, par2, par3) != Material.water) {
                  var8 = false;
               }

               if (var8 && this.getBlockMaterial(par1 + 1, par2, par3) != Material.water) {
                  var8 = false;
               }

               if (var8 && this.getBlockMaterial(par1, par2, par3 - 1) != Material.water) {
                  var8 = false;
               }

               if (var8 && this.getBlockMaterial(par1, par2, par3 + 1) != Material.water) {
                  var8 = false;
               }

               if (!var8) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public final boolean canSnowAt(int par1, int par2, int par3) {
      BiomeGenBase var4 = this.getBiomeGenForCoords(par1, par3);
      float var5 = var4.getFloatTemperature();
      if (var5 > 0.15F) {
         return false;
      } else {
         if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10) {
            int var6 = this.getBlockId(par1, par2 - 1, par3);
            int var7 = this.getBlockId(par1, par2, par3);
            Block block_below = Block.getBlock(var6);
            Block block = Block.getBlock(var7);
            if (block_below == Block.tilledField && block != Block.pumpkinStem) {
               return true;
            }

            if (var7 == 0 && Block.snow.isLegalAt(this, par1, par2, par3, 0) && var6 != Block.ice.blockID) {
               return true;
            }
         }

         return false;
      }
   }

   public final void placeNaturallyOccurringSnow(int min_x, int min_z, int max_x, int max_z) {
      boolean freezing_biome_nearby = this.isBiomeFreezing(min_x, min_z) || this.isBiomeFreezing(min_x, max_z) || this.isBiomeFreezing(max_x, min_z) || this.isBiomeFreezing(max_x, max_z);
      if (freezing_biome_nearby) {
         for(int x = min_x; x <= max_x; ++x) {
            label41:
            for(int z = min_z; z <= max_z; ++z) {
               int y = this.getPrecipitationHeight(x, z);
               if (this.canSnowAt(x, y, z) && this.setBlock(x, y, z, Block.snow.blockID, 0, 2)) {
                  do {
                     --y;
                     if (y <= 62) {
                        continue label41;
                     }
                  } while(this.getBlockId(x, y, z) != Block.snow.blockID);

                  this.setBlockToAir(x, y, z, 2);
               }
            }
         }

      }
   }

   public final void updateAllLightTypes(int par1, int par2, int par3, Chunk chunk) {
      boolean trusted_xz = this.canUpdateLightByType(par1, par3);
      if (this.hasSkylight()) {
         this.updateLightByType(EnumSkyBlock.Sky, par1, par2, par3, trusted_xz, chunk);
      }

      this.updateLightByType(EnumSkyBlock.Block, par1, par2, par3, trusted_xz, chunk);
   }

   private int computeLightValue(int par1, int par2, int par3, EnumSkyBlock par4EnumSkyBlock) {
      if (par4EnumSkyBlock == EnumSkyBlock.Sky && this.canBlockSeeTheSky(par1, par2, par3)) {
         return 15;
      } else {
         int var5 = this.getBlockId(par1, par2, par3);
         int var6 = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : Block.lightValue[var5];
         int var7 = Block.lightOpacity[var5];
         if (var7 < 1) {
            var7 = 1;
         } else {
            if (this.getHeightValue(par1, par3) - 1 > par2) {
               ++var7;
            }

            if (var7 >= 15) {
               if (Block.lightValue[var5] <= 0) {
                  return 0;
               }

               var7 = 1;
            }
         }

         if (var6 >= 14) {
            return var6;
         } else {
            int local_x = par1 & 15;
            int local_z = par3 & 15;
            Chunk chunk = this.getChunkFromBlockCoords(par1, par3);
            int var12;
            if (chunk.isEmpty()) {
               if (Minecraft.inDevMode()) {
                  Minecraft.setErrorMessage("computeLightValue: chunk was empty at " + par1 + "," + par3, false);
               }

               if (local_z == 0 && (var12 = this.getSavedLightValue(par4EnumSkyBlock, par1, par2, par3 - 1) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if (local_z == 15 && (var12 = this.getSavedLightValue(par4EnumSkyBlock, par1, par2, par3 + 1) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if (local_x == 0 && (var12 = this.getSavedLightValue(par4EnumSkyBlock, par1 - 1, par2, par3) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if (local_x == 15 && (var12 = this.getSavedLightValue(par4EnumSkyBlock, par1 + 1, par2, par3) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               return var6;
            } else {
               if ((var12 = this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2 - 1, par3, chunk) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if ((var12 = this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2 + 1, par3, chunk) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if ((var12 = (local_z > 0 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2, par3 - 1, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1, par2, par3 - 1)) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if ((var12 = (local_z < 15 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2, par3 + 1, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1, par2, par3 + 1)) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if ((var12 = (local_x > 0 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1 - 1, par2, par3, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1 - 1, par2, par3)) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               if ((var12 = (local_x < 15 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1 + 1, par2, par3, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1 + 1, par2, par3)) - var7) > var6) {
                  var6 = var12;
                  if (var12 >= 14) {
                     return var6;
                  }
               }

               return var6;
            }
         }
      }
   }

   private int computeLightValueMITE(int par1, int par2, int par3, EnumSkyBlock par4EnumSkyBlock, Chunk chunk) {
      if (par4EnumSkyBlock == EnumSkyBlock.Sky && chunk.canBlockSeeTheSkyForNonEmptyChunk(par1 & 15, par2, par3 & 15)) {
         return 15;
      } else {
         int var5;
         if (this.isWithinBlockBounds(par1, par2, par3)) {
            var5 = chunk.getBlockIDOptimized((par1 & 15) + (par3 & 15) * 16, par2);
         } else {
            var5 = 0;
         }

         int var6 = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : Block.lightValue[var5];
         int var7 = Block.lightOpacity[var5];
         if (var7 < 1) {
            var7 = 1;
         } else {
            if (chunk.getHeightValue(par1 & 15, par3 & 15) - 1 > par2) {
               ++var7;
            }

            if (var7 >= 15) {
               if (Block.lightValue[var5] <= 0) {
                  return 0;
               }

               var7 = 1;
            }
         }

         if (var6 >= 14) {
            return var6;
         } else {
            int local_x = par1 & 15;
            int local_z = par3 & 15;
            int var12;
            if ((var12 = this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2 - 1, par3, chunk) - var7) > var6) {
               var6 = var12;
               if (var12 >= 14) {
                  return var6;
               }
            }

            if ((var12 = this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2 + 1, par3, chunk) - var7) > var6) {
               var6 = var12;
               if (var12 >= 14) {
                  return var6;
               }
            }

            if ((var12 = (local_z > 0 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2, par3 - 1, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1, par2, par3 - 1)) - var7) > var6) {
               var6 = var12;
               if (var12 >= 14) {
                  return var6;
               }
            }

            if ((var12 = (local_z < 15 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1, par2, par3 + 1, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1, par2, par3 + 1)) - var7) > var6) {
               var6 = var12;
               if (var12 >= 14) {
                  return var6;
               }
            }

            if ((var12 = (local_x > 0 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1 - 1, par2, par3, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1 - 1, par2, par3)) - var7) > var6) {
               var6 = var12;
               if (var12 >= 14) {
                  return var6;
               }
            }

            if ((var12 = (local_x < 15 ? this.getSavedLightValueMITE(par4EnumSkyBlock, par1 + 1, par2, par3, chunk) : this.getSavedLightValue(par4EnumSkyBlock, par1 + 1, par2, par3)) - var7) > var6) {
               var6 = var12;
               if (var12 >= 14) {
                  return var6;
               }
            }

            return var6;
         }
      }
   }

   private int computeSkylightValueMITE(int par1, int par2, int par3, Chunk chunk) {
      if (chunk.canBlockSeeTheSkyForNonEmptyChunk(par1 & 15, par2, par3 & 15)) {
         return 15;
      } else {
         int var5 = this.isWithinBlockBounds(par1, par2, par3) ? chunk.getBlockIDOptimized((par1 & 15) + (par3 & 15) * 16, par2) : 0;
         int var7 = Block.lightOpacity[var5];
         if (var7 < 1) {
            var7 = 1;
         } else {
            if (chunk.getHeightValue(par1 & 15, par3 & 15) - 1 > par2) {
               ++var7;
            }

            if (var7 >= 15) {
               if (Block.lightValue[var5] <= 0) {
                  return 0;
               }

               var7 = 1;
            }
         }

         int var6 = 0;
         int var12;
         if ((var12 = this.getSavedSkylightValueMITE(par1, par2 - 1, par3, chunk) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         if ((var12 = this.getSavedSkylightValueMITE(par1, par2 + 1, par3, chunk) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         int local_z = par3 & 15;
         if ((var12 = (local_z > 0 ? this.getSavedSkylightValueMITE(par1, par2, par3 - 1, chunk) : this.getSavedSkylightValue(par1, par2, par3 - 1)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         if ((var12 = (local_z < 15 ? this.getSavedSkylightValueMITE(par1, par2, par3 + 1, chunk) : this.getSavedSkylightValue(par1, par2, par3 + 1)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         int local_x = par1 & 15;
         if ((var12 = (local_x > 0 ? this.getSavedSkylightValueMITE(par1 - 1, par2, par3, chunk) : this.getSavedSkylightValue(par1 - 1, par2, par3)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         if ((var12 = (local_x < 15 ? this.getSavedSkylightValueMITE(par1 + 1, par2, par3, chunk) : this.getSavedSkylightValue(par1 + 1, par2, par3)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         return var6;
      }
   }

   private int computeBlocklightValueMITE(int par1, int par2, int par3, Chunk chunk) {
      int var5 = this.isWithinBlockBounds(par1, par2, par3) ? chunk.getBlockIDOptimized((par1 & 15) + (par3 & 15) * 16, par2) : 0;
      int var6 = Block.lightValue[var5];
      int var7 = Block.lightOpacity[var5];
      if (var7 < 1) {
         var7 = 1;
      } else {
         if (chunk.getHeightValue(par1 & 15, par3 & 15) - 1 > par2) {
            ++var7;
         }

         if (var7 >= 15) {
            if (var6 <= 0) {
               return 0;
            }

            var7 = 1;
         }
      }

      if (var6 >= 14) {
         return var6;
      } else {
         int var12;
         if ((var12 = this.getSavedBlocklightValueMITE(par1, par2 - 1, par3, chunk) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         if ((var12 = this.getSavedBlocklightValueMITE(par1, par2 + 1, par3, chunk) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         int local_z = par3 & 15;
         if ((var12 = (local_z > 0 ? this.getSavedBlocklightValueMITE(par1, par2, par3 - 1, chunk) : this.getSavedBlocklightValue(par1, par2, par3 - 1)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         if ((var12 = (local_z < 15 ? this.getSavedBlocklightValueMITE(par1, par2, par3 + 1, chunk) : this.getSavedBlocklightValue(par1, par2, par3 + 1)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         int local_x = par1 & 15;
         if ((var12 = (local_x > 0 ? this.getSavedBlocklightValueMITE(par1 - 1, par2, par3, chunk) : this.getSavedBlocklightValue(par1 - 1, par2, par3)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         if ((var12 = (local_x < 15 ? this.getSavedBlocklightValueMITE(par1 + 1, par2, par3, chunk) : this.getSavedBlocklightValue(par1 + 1, par2, par3)) - var7) > var6) {
            var6 = var12;
            if (var12 >= 14) {
               return var6;
            }
         }

         return var6;
      }
   }

   public final boolean canUpdateLightByType(int x, int z) {
      return this.doesChunkAndAllNeighborsExist(x >> 4, z >> 4, 1, MITEConstant.includeEmptyChunksForLighting());
   }

   public final boolean canUpdateLightByType(int x, int z, Chunk chunk) {
      return chunk.isEmpty() && !MITEConstant.includeEmptyChunksForLighting() ? false : chunk.doAllNeighborsExist(1, false, MITEConstant.includeEmptyChunksForLighting());
   }

   public void updateLightByType(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, boolean trusted_xz, Chunk chunk) {
      if (par1EnumSkyBlock == EnumSkyBlock.Sky) {
         this.propagateSkylight(par2, par3, par4, trusted_xz, chunk);
      } else {
         this.propagateBlocklight(par2, par3, par4, trusted_xz, chunk);
      }

   }

   public void propagateSkylight(int par2, int par3, int par4, boolean trusted_xz, Chunk chunk) {
      if (chunk.isEmpty()) {
         Debug.setErrorMessage("propagateSkylight: Why called for empty chunk?");
      }

      if (this.decorating || !trusted_xz && !this.canUpdateLightByType(par2, par4, chunk)) {
         chunk.addPendingSkylightUpdate(par2, par3, par4);
      } else {
         int var5 = 0;
         int var6 = 0;
         int var7 = this.getSavedSkylightValueMITE(par2, par3, par4, chunk);
         int var8 = this.computeSkylightValueMITE(par2, par3, par4, chunk);
         int par2_minus_32 = par2 - 32;
         int par3_minus_32 = par3 - 32;
         int par4_minus_32 = par4 - 32;
         int minus_par2_plus_32 = -par2 + 32;
         int minus_par3_plus_32 = -par3 + 32;
         int minus_par4_plus_32 = -par4 + 32;
         int pos_max_distance = Math.max(var7, var8) + 1;
         int neg_max_distance = -pos_max_distance;
         int var9;
         int var10;
         int var11;
         int var12;
         int var13;
         int var15;
         int var16;
         int var17;
         boolean same_chunk;
         int x;
         int y;
         int z;
         int opacity;
         if (var8 > var7) {
            this.lightUpdateBlockList[var6++] = 133152;
         } else if (var8 < var7) {
            this.lightUpdateBlockList[var6++] = 133152 | var7 << 18;

            while(var5 < var6) {
               var9 = this.lightUpdateBlockList[var5++];
               var10 = (var9 & 63) + par2_minus_32;
               var11 = (var9 >> 6 & 63) + par3_minus_32;
               var12 = (var9 >> 12 & 63) + par4_minus_32;
               var13 = var9 >> 18 & 15;
               same_chunk = chunk.hasCoords(var10 >> 4, var12 >> 4);
               if ((same_chunk ? this.getSavedSkylightValueMITE(var10, var11, var12, chunk) : this.getSavedSkylightValue(var10, var11, var12)) == var13) {
                  if (same_chunk) {
                     this.setSkylightValueMITE(var10, var11, var12, 0, chunk);
                  } else {
                     this.setSkylightValue(var10, var11, var12, 0);
                  }

                  if (var13 > 0) {
                     var15 = var10 - par2;
                     var16 = var11 - par3;
                     var17 = var12 - par4;
                     if (var15 >= neg_max_distance && var15 <= pos_max_distance && var16 >= neg_max_distance && var16 <= pos_max_distance && var17 >= neg_max_distance && var17 <= pos_max_distance) {
                        for(int var18 = 0; var18 < 6; ++var18) {
                           x = var10 + Facing.offsetsXForSide[var18];
                           y = var11 + Facing.offsetsYForSide[var18];
                           z = var12 + Facing.offsetsZForSide[var18];
                           opacity = Block.lightOpacity[this.getBlockId(x, y, z)];
                           if (opacity == 0 || this.getHeightValue(x, z) - 1 > y) {
                              ++opacity;
                           }

                           Chunk chunk2 = chunk.hasCoords(x >> 4, z >> 4) ? chunk : this.getChunkFromBlockCoords(x, z);
                           if (chunk2.isEmpty()) {
                              Debug.setErrorMessage("You need to handle an empty chunk");
                           }

                           if (this.getSavedSkylightValueMITE(x, y, z, chunk2) == var13 - opacity) {
                              this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 | y + minus_par3_plus_32 << 6 | z + minus_par4_plus_32 << 12 | var13 - opacity << 18;
                           }
                        }
                     }
                  }
               }
            }

            var5 = 0;
         }

         while(true) {
            while(true) {
               int var14;
               boolean var23;
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       do {
                                          if (var5 >= var6) {
                                             return;
                                          }

                                          var9 = this.lightUpdateBlockList[var5++];
                                          var10 = (var9 & 63) + par2_minus_32;
                                          var11 = (var9 >> 6 & 63) + par3_minus_32;
                                          var12 = (var9 >> 12 & 63) + par4_minus_32;
                                          same_chunk = chunk.hasCoords(var10 >> 4, var12 >> 4);
                                          if (same_chunk) {
                                             var13 = this.getSavedSkylightValueMITE(var10, var11, var12, chunk);
                                             var14 = this.computeSkylightValueMITE(var10, var11, var12, chunk);
                                          } else {
                                             var13 = this.getSavedSkylightValue(var10, var11, var12);
                                             var14 = this.computeLightValue(var10, var11, var12, EnumSkyBlock.Sky);
                                          }
                                       } while(var14 == var13);

                                       if (same_chunk) {
                                          this.setSkylightValueMITE(var10, var11, var12, var14, chunk);
                                       } else {
                                          this.setSkylightValue(var10, var11, var12, var14);
                                       }
                                    } while(var14 <= var13);

                                    var15 = var10 - par2;
                                    var16 = var11 - par3;
                                    var17 = var12 - par4;
                                    var23 = var6 < this.lightUpdateBlockList.length - 6;
                                 } while(var15 <= -18);
                              } while(var15 >= 18);
                           } while(var16 <= -18);
                        } while(var16 >= 18);
                     } while(var17 <= -18);
                  } while(var17 >= 18);
               } while(!var23);

               opacity = var10 & 15;
               int local_z = var12 & 15;
               if (opacity > 0 && opacity < 15 && local_z > 0 && local_z < 15) {
                  Chunk chunk2 = chunk.hasCoords(var10 >> 4, var12 >> 4) ? chunk : this.getChunkFromBlockCoords(var10, var12);
                  if (chunk2.isEmpty()) {
                     Debug.setErrorMessage("updateLightByType: chunk was empty");
                  }

                  x = var10 - 1;
                  y = var11;
                  z = var12;
                  if (this.getSavedSkylightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10 + 1;
                  y = var11;
                  z = var12;
                  if (this.getSavedSkylightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 - 1;
                  z = var12;
                  if (this.getSavedSkylightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 + 1;
                  z = var12;
                  if (this.getSavedSkylightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 - 1;
                  if (this.getSavedSkylightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 + 1;
                  if (this.getSavedSkylightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }
               } else {
                  x = var10 - 1;
                  y = var11;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedSkylightValueMITE(x, y, z, chunk) : this.getSavedSkylightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10 + 1;
                  y = var11;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedSkylightValueMITE(x, y, z, chunk) : this.getSavedSkylightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 - 1;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedSkylightValueMITE(x, y, z, chunk) : this.getSavedSkylightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 + 1;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedSkylightValueMITE(x, y, z, chunk) : this.getSavedSkylightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 - 1;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedSkylightValueMITE(x, y, z, chunk) : this.getSavedSkylightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 + 1;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedSkylightValueMITE(x, y, z, chunk) : this.getSavedSkylightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }
               }
            }
         }
      }
   }

   public void propagateBlocklight(int par2, int par3, int par4, boolean trusted_xz, Chunk chunk) {
      if (chunk.isEmpty()) {
         Debug.setErrorMessage("propagateBlocklight: Why called for empty chunk?");
      }

      if (!this.decorating && (trusted_xz || this.canUpdateLightByType(par2, par4, chunk))) {
         int var5 = 0;
         int var6 = 0;
         int var7 = this.getSavedBlocklightValueMITE(par2, par3, par4, chunk);
         int var8 = this.computeBlocklightValueMITE(par2, par3, par4, chunk);
         int par2_minus_32 = par2 - 32;
         int par3_minus_32 = par3 - 32;
         int par4_minus_32 = par4 - 32;
         int minus_par2_plus_32 = -par2 + 32;
         int minus_par3_plus_32 = -par3 + 32;
         int minus_par4_plus_32 = -par4 + 32;
         int pos_max_distance = Math.max(var7, var8) + 1;
         int neg_max_distance = -pos_max_distance;
         int var9;
         int var10;
         int var11;
         int var12;
         int var13;
         int var15;
         int var16;
         int var17;
         boolean same_chunk;
         int x;
         int y;
         int z;
         int var21;
         int opacity;
         Chunk chunk2;
         if (var8 > var7) {
            this.lightUpdateBlockList[var6++] = 133152;
         } else {
            this.lightUpdateBlockList[var6++] = 133152 | var7 << 18;

            while(var5 < var6) {
               var9 = this.lightUpdateBlockList[var5++];
               var10 = (var9 & 63) + par2_minus_32;
               var11 = (var9 >> 6 & 63) + par3_minus_32;
               var12 = (var9 >> 12 & 63) + par4_minus_32;
               var13 = var9 >> 18 & 15;
               same_chunk = chunk.hasCoords(var10 >> 4, var12 >> 4);
               if ((same_chunk ? this.getSavedBlocklightValueMITE(var10, var11, var12, chunk) : this.getSavedBlocklightValue(var10, var11, var12)) == var13) {
                  if (same_chunk) {
                     this.setBlocklightValueMITE(var10, var11, var12, 0, chunk);
                  } else {
                     this.setBlocklightValue(var10, var11, var12, 0);
                  }

                  if (var13 > 0) {
                     var15 = var10 - par2;
                     var16 = var11 - par3;
                     var17 = var12 - par4;
                     if (var15 < 0) {
                        var15 = -var15;
                     }

                     if (var16 < 0) {
                        var16 = -var16;
                     }

                     if (var17 < 0) {
                        var17 = -var17;
                     }

                     if (var15 + var16 + var17 <= pos_max_distance) {
                        for(x = 0; x < 6; ++x) {
                           y = var10 + Facing.offsetsXForSide[x];
                           z = var11 + Facing.offsetsYForSide[x];
                           var21 = var12 + Facing.offsetsZForSide[x];
                           opacity = Block.lightOpacity[this.getBlockId(y, z, var21)];
                           if (opacity == 0 || this.getHeightValue(y, var21) - 1 > z) {
                              ++opacity;
                           }

                           chunk2 = chunk.hasCoords(y >> 4, var21 >> 4) ? chunk : this.getChunkFromBlockCoords(y, var21);
                           if (chunk2.isEmpty()) {
                              Debug.setErrorMessage("You need to handle an empty chunk");
                           }

                           if (this.getSavedBlocklightValueMITE(y, z, var21, chunk2) == var13 - opacity) {
                              this.lightUpdateBlockList[var6++] = y + minus_par2_plus_32 | z + minus_par3_plus_32 << 6 | var21 + minus_par4_plus_32 << 12 | var13 - opacity << 18;
                           }
                        }
                     }
                  }
               }
            }

            var5 = 0;
         }

         while(true) {
            while(true) {
               int var14;
               do {
                  do {
                     do {
                        do {
                           if (var5 >= var6) {
                              return;
                           }

                           var9 = this.lightUpdateBlockList[var5++];
                           var10 = (var9 & 63) + par2_minus_32;
                           var11 = (var9 >> 6 & 63) + par3_minus_32;
                           var12 = (var9 >> 12 & 63) + par4_minus_32;
                           same_chunk = chunk.hasCoords(var10 >> 4, var12 >> 4);
                           if (same_chunk) {
                              var13 = this.getSavedBlocklightValueMITE(var10, var11, var12, chunk);
                              var14 = this.computeBlocklightValueMITE(var10, var11, var12, chunk);
                           } else {
                              var13 = this.getSavedBlocklightValue(var10, var11, var12);
                              var14 = this.computeLightValue(var10, var11, var12, EnumSkyBlock.Block);
                           }
                        } while(var14 == var13);

                        if (same_chunk) {
                           this.setBlocklightValueMITE(var10, var11, var12, var14, chunk);
                        } else {
                           this.setBlocklightValue(var10, var11, var12, var14);
                        }
                     } while(var6 >= this.lightUpdateBlockList.length - 6);
                  } while(var14 <= var13);

                  var15 = var10 - par2;
                  var16 = var11 - par3;
                  var17 = var12 - par4;
                  if (var15 < 0) {
                     var15 = -var15;
                  }

                  if (var16 < 0) {
                     var16 = -var16;
                  }

                  if (var17 < 0) {
                     var17 = -var17;
                  }
               } while(var15 + var16 + var17 > pos_max_distance);

               var21 = var10 & 15;
               opacity = var12 & 15;
               if (var21 > 0 && var21 < 15 && opacity > 0 && opacity < 15) {
                  chunk2 = chunk.hasCoords(var10 >> 4, var12 >> 4) ? chunk : this.getChunkFromBlockCoords(var10, var12);
                  if (chunk2.isEmpty()) {
                     Debug.setErrorMessage("updateLightByType: chunk was empty");
                  }

                  x = var10 - 1;
                  y = var11;
                  z = var12;
                  if (this.getSavedBlocklightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10 + 1;
                  y = var11;
                  z = var12;
                  if (this.getSavedBlocklightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 - 1;
                  z = var12;
                  if (this.getSavedBlocklightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 + 1;
                  z = var12;
                  if (this.getSavedBlocklightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 - 1;
                  if (this.getSavedBlocklightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 + 1;
                  if (this.getSavedBlocklightValueMITE(x, y, z, chunk2) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }
               } else {
                  x = var10 - 1;
                  y = var11;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedBlocklightValueMITE(x, y, z, chunk) : this.getSavedBlocklightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10 + 1;
                  y = var11;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedBlocklightValueMITE(x, y, z, chunk) : this.getSavedBlocklightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 - 1;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedBlocklightValueMITE(x, y, z, chunk) : this.getSavedBlocklightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11 + 1;
                  z = var12;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedBlocklightValueMITE(x, y, z, chunk) : this.getSavedBlocklightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 - 1;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedBlocklightValueMITE(x, y, z, chunk) : this.getSavedBlocklightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }

                  x = var10;
                  y = var11;
                  z = var12 + 1;
                  if ((chunk.hasCoords(x >> 4, z >> 4) ? this.getSavedBlocklightValueMITE(x, y, z, chunk) : this.getSavedBlocklightValue(x, y, z)) < var14) {
                     this.lightUpdateBlockList[var6++] = x + minus_par2_plus_32 + (y + minus_par3_plus_32 << 6) + (z + minus_par4_plus_32 << 12);
                  }
               }
            }
         }
      } else {
         chunk.addPendingBlocklightUpdate(par2, par3, par4);
      }
   }

   public boolean tickUpdates(boolean par1) {
      return false;
   }

   public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2) {
      return null;
   }

   public boolean occupiedByLivingEntity(int x, int y, int z) {
      List entities = this.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1)));
      return entities != null && entities.size() > 0;
   }

   public List getPredatorsWithinAABBForEntity(Entity prey, AxisAlignedBB bounding_box) {
      List entities = this.getEntitiesWithinAABBExcludingEntity(prey, bounding_box);
      List predators = new ArrayList();
      Iterator i = entities.iterator();

      while(i.hasNext()) {
         Entity entity = (Entity)i.next();
         if (entity instanceof EntityLiving && ((EntityLiving)entity).preysUpon(prey)) {
            predators.add(entity);
         }
      }

      return predators;
   }

   public List getFoodItemEntitiesWithinAABBForLivingEntity(EntityLiving entity_living, AxisAlignedBB bounding_box) {
      List entities = this.getEntitiesWithinAABB(EntityItem.class, bounding_box);
      List food_item_entities = new ArrayList();
      Iterator i = entities.iterator();

      while(i.hasNext()) {
         EntityItem entity_item = (EntityItem)i.next();
         if (entity_living.isFoodItem(entity_item.getEntityItem())) {
            food_item_entities.add(entity_item);
         }
      }

      return food_item_entities;
   }

   public List getRepairItemEntitiesWithinAABBForLivingEntity(EntityLiving entity_living, AxisAlignedBB bounding_box) {
      List entities = this.getEntitiesWithinAABB(EntityItem.class, bounding_box);
      List repair_item_entities = new ArrayList();
      Iterator i = entities.iterator();

      while(i.hasNext()) {
         EntityItem entity_item = (EntityItem)i.next();
         if (entity_living.isRepairItem(entity_item.getEntityItem())) {
            repair_item_entities.add(entity_item);
         }
      }

      return repair_item_entities;
   }

   public List getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {
      return this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB, (IEntitySelector)null);
   }

   public List getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, IEntitySelector par3IEntitySelector) {
      ArrayList var4 = new ArrayList();
      int var5 = MathHelper.floor_double((par2AxisAlignedBB.minX - 2.0) / 16.0);
      int var6 = MathHelper.floor_double((par2AxisAlignedBB.maxX + 2.0) / 16.0);
      int var7 = MathHelper.floor_double((par2AxisAlignedBB.minZ - 2.0) / 16.0);
      int var8 = MathHelper.floor_double((par2AxisAlignedBB.maxZ + 2.0) / 16.0);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            if (this.chunkExists(var9, var10)) {
               this.getChunkFromChunkCoords(var9, var10).getEntitiesWithinAABBForEntity(par1Entity, par2AxisAlignedBB, var4, par3IEntitySelector);
            }
         }
      }

      return var4;
   }

   public List getEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB) {
      return this.selectEntitiesWithinAABB(par1Class, par2AxisAlignedBB, (IEntitySelector)null);
   }

   public List selectEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, IEntitySelector par3IEntitySelector) {
      int var4 = MathHelper.floor_double((par2AxisAlignedBB.minX - 2.0) / 16.0);
      int var5 = MathHelper.floor_double((par2AxisAlignedBB.maxX + 2.0) / 16.0);
      int var6 = MathHelper.floor_double((par2AxisAlignedBB.minZ - 2.0) / 16.0);
      int var7 = MathHelper.floor_double((par2AxisAlignedBB.maxZ + 2.0) / 16.0);
      ArrayList var8 = new ArrayList();

      for(int var9 = var4; var9 <= var5; ++var9) {
         for(int var10 = var6; var10 <= var7; ++var10) {
            if (this.chunkExists(var9, var10)) {
               this.getChunkFromChunkCoords(var9, var10).getEntitiesOfTypeWithinAAAB(par1Class, par2AxisAlignedBB, var8, par3IEntitySelector);
            }
         }
      }

      return var8;
   }

   public Entity findNearestEntityWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, Entity par3Entity) {
      List var4 = this.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB);
      Entity var5 = null;
      double var6 = Double.MAX_VALUE;

      for(int var8 = 0; var8 < var4.size(); ++var8) {
         Entity var9 = (Entity)var4.get(var8);
         if (var9 != par3Entity) {
            double var10 = par3Entity.getDistanceSqToEntity(var9);
            if (var10 <= var6) {
               var5 = var9;
               var6 = var10;
            }
         }
      }

      return var5;
   }

   public Entity findNearestSeenEntityWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, Entity par3Entity, EntitySenses entity_senses) {
      List var4 = this.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB);
      Entity var5 = null;
      double var6 = Double.MAX_VALUE;

      for(int var8 = 0; var8 < var4.size(); ++var8) {
         Entity var9 = (Entity)var4.get(var8);
         if (!var9.isDead && (!var9.isEntityLivingBase() || !(var9.getAsEntityLivingBase().getHealth() <= 0.0F)) && var9 != par3Entity && entity_senses.canSee(var9)) {
            double var10 = par3Entity.getDistanceSqToEntity(var9);
            if (var10 <= var6) {
               var5 = var9;
               var6 = var10;
            }
         }
      }

      return var5;
   }

   public abstract Entity getEntityByID(int var1);

   public List getLoadedEntityList() {
      return this.loadedEntityList;
   }

   public void markTileEntityChunkModified(int par1, int par2, int par3, TileEntity par4TileEntity) {
      if (this.blockExists(par1, par2, par3)) {
         this.getChunkFromBlockCoords(par1, par3).setChunkModified();
      }

   }

   public int countEntities(Class par1Class) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.loadedEntityList.size(); ++var3) {
         Entity var4 = (Entity)this.loadedEntityList.get(var3);
         if ((!(var4 instanceof EntityLiving) || !((EntityLiving)var4).isNoDespawnRequired()) && par1Class.isAssignableFrom(var4.getClass())) {
            ++var2;
         }
      }

      return var2;
   }

   public int countMobs(boolean include_mobs_below_height_of_60, boolean include_mobs_at_height_of_60_or_higher) {
      int count = 0;

      for(int i = 0; i < this.loadedEntityList.size(); ++i) {
         Entity entity = (Entity)this.loadedEntityList.get(i);
         if (entity instanceof IMob) {
            if (entity.getBlockPosY() < 60) {
               if (include_mobs_below_height_of_60) {
                  ++count;
               }
            } else if (include_mobs_at_height_of_60_or_higher) {
               ++count;
            }
         }
      }

      return count;
   }

   public final Entity getEntityWithSameClassAndUUIDInLoadedEntityList(Entity entity, boolean report_error_on_object_match) {
      Iterator i = this.loadedEntityList.iterator();

      Entity loaded_entity;
      do {
         if (!i.hasNext()) {
            return null;
         }

         loaded_entity = (Entity)i.next();
         if (loaded_entity == entity) {
            if (report_error_on_object_match) {
               System.out.println("getEntityWithSameClassAndUUIDInLoadedEntityList: object match!");
            }

            return loaded_entity;
         }
      } while(loaded_entity.getClass() != entity.getClass() || !loaded_entity.getUniqueID().equals(entity.getUniqueID()));

      return loaded_entity;
   }

   public static final Entity getEntityWithSameClassAndUUIDInEntityList(String name_of_calling_function, Entity entity, List entity_list, boolean report_error_on_object_match) {
      Iterator i = entity_list.iterator();

      Entity entity_in_list;
      do {
         if (!i.hasNext()) {
            return null;
         }

         entity_in_list = (Entity)i.next();
         if (entity_in_list == entity) {
            if (report_error_on_object_match) {
               System.out.println(name_of_calling_function + ": object match!");
            }

            return entity_in_list;
         }
      } while(entity_in_list.getClass() != entity.getClass() || !entity_in_list.getUniqueID().equals(entity.getUniqueID()));

      return entity_in_list;
   }

   public final boolean isEntityObjectInLoadedEntityList(Entity entity) {
      return this.loadedEntityList.contains(entity);
   }

   public final boolean isEntityWithSameClassAndUUIDInUnloadedEntityList(Entity entity) {
      Iterator i = this.unloadedEntityList.iterator();

      Entity unloaded_entity;
      do {
         if (!i.hasNext()) {
            return false;
         }

         unloaded_entity = (Entity)i.next();
         if (unloaded_entity == entity) {
            return true;
         }
      } while(unloaded_entity.getClass() != entity.getClass() || !unloaded_entity.getUniqueID().equals(entity.getUniqueID()));

      return true;
   }

   public final boolean isEntityObjectInUnloadedEntityList(Entity entity) {
      return this.unloadedEntityList.contains(entity);
   }

   public void addLoadedEntities(List par1List) {
      this.loadedEntityList.addAll(par1List);

      for(int var2 = 0; var2 < par1List.size(); ++var2) {
         this.onEntityAdded((Entity)par1List.get(var2));
      }

   }

   public void unloadEntities(List par1List) {
      this.unloadedEntityList.addAll(par1List);
   }

   public PathEntity getPathEntityToEntity(Entity par1Entity, Entity par2Entity, float max_path_length, boolean can_pass_open_wooden_doors, boolean can_path_through_closed_wooden_doors, boolean avoid_water, boolean can_entity_swim) {
      if (max_path_length > 32.0F) {
         max_path_length = 32.0F;
      }

      this.theProfiler.startSection("pathfind");
      int var8 = MathHelper.floor_double(par1Entity.posX);
      int var9 = MathHelper.floor_double(par1Entity.posY + 1.0);
      int var10 = MathHelper.floor_double(par1Entity.posZ);
      int var11 = (int)(max_path_length + 16.0F);
      int var12 = var8 - var11;
      int var13 = var9 - var11;
      int var14 = var10 - var11;
      int var15 = var8 + var11;
      int var16 = var9 + var11;
      int var17 = var10 + var11;
      ChunkCache var18 = new ChunkCache(this, var12, var13, var14, var15, var16, var17, 0);
      PathEntity var19 = (new PathFinder(var18, can_pass_open_wooden_doors, can_path_through_closed_wooden_doors, avoid_water, can_entity_swim)).createEntityPathTo(par1Entity, par2Entity, max_path_length);
      this.theProfiler.endSection();
      return var19;
   }

   public PathEntity findEntityPathTowardXYZ(Entity entity, int x, int y, int z, int max_path_length, boolean use_navigator) {
      int entity_x = MathHelper.floor_double(entity.posX);
      int entity_y = MathHelper.floor_double(entity.posY);
      int entity_z = MathHelper.floor_double(entity.posZ);
      double shortest_distance_to_xyz_sq = getDistanceSqFromDeltas((float)(entity_x - x), (float)(entity_y - y), (float)(entity_z - z));
      PathEntity selected_path = null;
      int random_number_index = this.rand.nextInt();

      for(int attempt = 0; attempt < 16; ++attempt) {
         ++random_number_index;
         int dx = RNG.int_max[random_number_index & 32767] % (max_path_length * 2 + 1) - max_path_length;
         ++random_number_index;
         int dy = RNG.int_7_minus_3[random_number_index & 32767];
         ++random_number_index;
         int dz = RNG.int_max[random_number_index & 32767] % (max_path_length * 2 + 1) - max_path_length;
         int trial_x = entity_x + dx;
         int trial_y = entity_y + dy;
         int trial_z = entity_z + dz;

         int i;
         for(i = 0; i < 8 && this.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, false); ++i) {
            --trial_y;
         }

         for(i = 0; i < 8 && !this.isAirOrPassableBlock(trial_x, trial_y, trial_z, false); ++i) {
            ++trial_y;
         }

         double distance_to_xyz_sq = getDistanceSqFromDeltas((float)(trial_x - x), (float)(trial_y - y), (float)(trial_z - z));
         if (distance_to_xyz_sq < shortest_distance_to_xyz_sq) {
            PathEntity path;
            if (use_navigator && entity instanceof EntityLiving) {
               path = ((EntityLiving)entity).getNavigator().getPathToXYZ(trial_x, trial_y, trial_z, max_path_length);
            } else {
               path = this.getEntityPathToXYZ(entity, trial_x, trial_y, trial_z, (float)max_path_length, true, false, false, true);
            }

            if (path != null) {
               PathPoint final_point = path.getFinalPathPoint();
               distance_to_xyz_sq = getDistanceSqFromDeltas((float)(final_point.xCoord - x), (float)(final_point.yCoord - y), (float)(final_point.zCoord - z));
               if (distance_to_xyz_sq < shortest_distance_to_xyz_sq) {
                  shortest_distance_to_xyz_sq = distance_to_xyz_sq;
                  selected_path = path;
               }
            }
         }
      }

      return selected_path;
   }

   public PathEntity findEntityPathAwayFromXYZ(Entity entity, int x, int y, int z, int min_distance, int max_path_length, boolean use_navigator) {
      int entity_x = MathHelper.floor_double(entity.posX);
      int entity_y = MathHelper.floor_double(entity.posY);
      int entity_z = MathHelper.floor_double(entity.posZ);
      int min_distance_sq = min_distance * min_distance;
      double furthest_distance_from_xyz_sq = getDistanceSqFromDeltas((float)(entity_x - x), (float)(entity_y - y), (float)(entity_z - z));
      PathEntity selected_path = null;
      int random_number_index = this.rand.nextInt();

      for(int attempt = 0; attempt < 16; ++attempt) {
         ++random_number_index;
         int dx = RNG.int_max[random_number_index & 32767] % (max_path_length * 2 + 1) - max_path_length;
         ++random_number_index;
         int dy = RNG.int_7_minus_3[random_number_index & 32767];
         ++random_number_index;
         int dz = RNG.int_max[random_number_index & 32767] % (max_path_length * 2 + 1) - max_path_length;
         int trial_x = entity_x + dx;
         int trial_y = entity_y + dy;
         int trial_z = entity_z + dz;

         int i;
         for(i = 0; i < 8 && this.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, false); ++i) {
            --trial_y;
         }

         for(i = 0; i < 8 && !this.isAirOrPassableBlock(trial_x, trial_y, trial_z, false); ++i) {
            ++trial_y;
         }

         double distance_from_xyz_sq = getDistanceSqFromDeltas((float)(trial_x - x), (float)(trial_y - y), (float)(trial_z - z));
         if (distance_from_xyz_sq >= (double)min_distance_sq && distance_from_xyz_sq > furthest_distance_from_xyz_sq) {
            PathEntity path;
            if (use_navigator && entity instanceof EntityLiving) {
               path = ((EntityLiving)entity).getNavigator().getPathToXYZ(trial_x, trial_y, trial_z, max_path_length);
            } else {
               path = this.getEntityPathToXYZ(entity, trial_x, trial_y, trial_z, (float)max_path_length, true, false, false, true);
            }

            if (path != null) {
               PathPoint final_point = path.getFinalPathPoint();
               distance_from_xyz_sq = getDistanceSqFromDeltas((float)(final_point.xCoord - x), (float)(final_point.yCoord - y), (float)(final_point.zCoord - z));
               if (distance_from_xyz_sq >= (double)min_distance_sq && distance_from_xyz_sq > furthest_distance_from_xyz_sq) {
                  furthest_distance_from_xyz_sq = distance_from_xyz_sq;
                  selected_path = path;
               }
            }
         }
      }

      return selected_path;
   }

   public PathEntity getEntityPathToXYZ(Entity par1Entity, int par2, int par3, int par4, float par5, boolean can_pass_open_wooden_doors, boolean can_path_through_closed_wooden_doors, boolean avoid_water, boolean can_entity_swim) {
      this.theProfiler.startSection("pathfind");
      int var10 = MathHelper.floor_double(par1Entity.posX);
      int var11 = MathHelper.floor_double(par1Entity.posY);
      int var12 = MathHelper.floor_double(par1Entity.posZ);
      int var13 = (int)(par5 + 8.0F);
      int var14 = var10 - var13;
      int var15 = var11 - var13;
      int var16 = var12 - var13;
      int var17 = var10 + var13;
      int var18 = var11 + var13;
      int var19 = var12 + var13;
      ChunkCache var20 = new ChunkCache(this, var14, var15, var16, var17, var18, var19, 0);
      PathEntity var21 = (new PathFinder(var20, can_pass_open_wooden_doors, can_path_through_closed_wooden_doors, avoid_water, can_entity_swim)).createEntityPathTo(par1Entity, par2, par3, par4, par5);
      this.theProfiler.endSection();
      return var21;
   }

   public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4) {
      int var5 = this.getBlockId(par1, par2, par3);
      return var5 == 0 ? 0 : Block.blocksList[var5].isProvidingStrongPower(this, par1, par2, par3, par4);
   }

   public int getBlockPowerInput(int par1, int par2, int par3) {
      byte var4 = 0;
      int var5 = Math.max(var4, this.isBlockProvidingPowerTo(par1, par2 - 1, par3, 0));
      if (var5 >= 15) {
         return var5;
      } else {
         var5 = Math.max(var5, this.isBlockProvidingPowerTo(par1, par2 + 1, par3, 1));
         if (var5 >= 15) {
            return var5;
         } else {
            var5 = Math.max(var5, this.isBlockProvidingPowerTo(par1, par2, par3 - 1, 2));
            if (var5 >= 15) {
               return var5;
            } else {
               var5 = Math.max(var5, this.isBlockProvidingPowerTo(par1, par2, par3 + 1, 3));
               if (var5 >= 15) {
                  return var5;
               } else {
                  var5 = Math.max(var5, this.isBlockProvidingPowerTo(par1 - 1, par2, par3, 4));
                  if (var5 >= 15) {
                     return var5;
                  } else {
                     var5 = Math.max(var5, this.isBlockProvidingPowerTo(par1 + 1, par2, par3, 5));
                     return var5 >= 15 ? var5 : var5;
                  }
               }
            }
         }
      }
   }

   public boolean getIndirectPowerOutput(int par1, int par2, int par3, int par4) {
      return this.getIndirectPowerLevelTo(par1, par2, par3, par4) > 0;
   }

   public int getIndirectPowerLevelTo(int par1, int par2, int par3, int par4) {
      if (this.isBlockNormalCube(par1, par2, par3)) {
         return this.getBlockPowerInput(par1, par2, par3);
      } else {
         int var5 = this.getBlockId(par1, par2, par3);
         return var5 == 0 ? 0 : Block.blocksList[var5].isProvidingWeakPower(this, par1, par2, par3, par4);
      }
   }

   public boolean isBlockIndirectlyGettingPowered(int par1, int par2, int par3) {
      return this.getIndirectPowerLevelTo(par1, par2 - 1, par3, 0) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2 + 1, par3, 1) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2, par3 - 1, 2) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2, par3 + 1, 3) > 0 ? true : (this.getIndirectPowerLevelTo(par1 - 1, par2, par3, 4) > 0 ? true : this.getIndirectPowerLevelTo(par1 + 1, par2, par3, 5) > 0))));
   }

   public int getStrongestIndirectPower(int par1, int par2, int par3) {
      int var4 = 0;

      for(int var5 = 0; var5 < 6; ++var5) {
         int var6 = this.getIndirectPowerLevelTo(par1 + Facing.offsetsXForSide[var5], par2 + Facing.offsetsYForSide[var5], par3 + Facing.offsetsZForSide[var5], var5);
         if (var6 >= 15) {
            return 15;
         }

         if (var6 > var4) {
            var4 = var6;
         }
      }

      return var4;
   }

   public EntityPlayer getClosestPlayerToEntity(Entity par1Entity, double max_distance, boolean must_be_alive) {
      return this.getClosestPlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, max_distance, must_be_alive);
   }

   public EntityPlayer getClosestPlayer(double par1, double par3, double par5, double max_distance, boolean must_be_alive) {
      double var9 = -1.0;
      EntityPlayer var11 = null;

      for(int var12 = 0; var12 < this.playerEntities.size(); ++var12) {
         EntityPlayer var13 = (EntityPlayer)this.playerEntities.get(var12);
         if (!var13.isGhost() && !var13.isZevimrgvInTournament() && (!must_be_alive || !var13.isDead && !(var13.getHealth() <= 0.0F))) {
            double var14 = var13.getDistanceSq(par1, par3, par5);
            if ((max_distance < 0.0 || var14 < max_distance * max_distance) && (var9 == -1.0 || var14 < var9)) {
               var9 = var14;
               var11 = var13;
            }
         }
      }

      return var11;
   }

   public EntityPlayer getClosestVulnerablePlayer(EntityLiving attacker, double par7, boolean requires_line_of_sight) {
      double par1 = attacker.posX;
      double par3 = attacker.posY + (double)(attacker.height / 2.0F);
      double par5 = attacker.posZ;
      if (this.isRemote) {
         Minecraft.setErrorMessage("getClosestVulnerablePlayer: no meant to be called on client");
      }

      double var9 = -1.0;
      EntityPlayer var11 = null;

      for(int var12 = 0; var12 < this.playerEntities.size(); ++var12) {
         EntityPlayer var13 = (EntityPlayer)this.playerEntities.get(var12);
         if (!var13.isGhost() && !var13.isZevimrgvInTournament() && !var13.isImmuneByGrace() && !var13.capabilities.disableDamage && var13.isEntityAlive()) {
            double var14 = var13.getDistanceSq(par1, par3, par5);
            double var16 = par7;
            if (var13.isSneaking()) {
               var16 = par7 * 0.800000011920929;
            }

            if (var13.isInvisible()) {
               float var18 = var13.getArmorVisibility();
               if (var18 < 0.1F) {
                  var18 = 0.1F;
               }

               var16 *= (double)(0.7F * var18);
            }

            if ((par7 < 0.0 || var14 < var16 * var16) && (var9 == -1.0 || var14 < var9) && (!requires_line_of_sight || attacker.getEntitySenses().canSee(var13))) {
               var9 = var14;
               var11 = var13;
            }
         }
      }

      return var11;
   }

   public EntityAnimal getClosestAnimal(EntityLiving attacker, double max_distance, boolean requires_line_of_sight, boolean requires_path) {
      List animals = this.getEntitiesWithinAABB(EntityAnimal.class, attacker.boundingBox.expand(max_distance, max_distance, max_distance));
      if (animals.isEmpty()) {
         return null;
      } else {
         EntityAnimal closest_animal = null;
         float closest_distance = 0.0F;
         Iterator i = animals.iterator();

         while(true) {
            EntityAnimal animal;
            float distance;
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!i.hasNext()) {
                                 return closest_animal;
                              }

                              animal = (EntityAnimal)i.next();
                           } while(animal.isDead);
                        } while(animal.getHealth() <= 0.0F);
                     } while(!animal.isTrueAnimal());
                  } while(requires_line_of_sight && !attacker.getEntitySenses().canSee(animal));
               } while(requires_path && !attacker.canPathTo(animal.getBlockPosX(), animal.getFootBlockPosY(), animal.getBlockPosZ(), (int)max_distance));

               distance = attacker.getDistanceToEntity(animal);
            } while(closest_animal != null && !(distance < closest_distance));

            closest_animal = animal;
            closest_distance = distance;
         }
      }
   }

   public EntityLivingBase getClosestEntityLivingBase(EntityLiving attacker, Class[] target_classes, double max_distance, boolean requires_line_of_sight, boolean requires_path) {
      List targets = this.getEntitiesWithinAABB(EntityLivingBase.class, attacker.boundingBox.expand(max_distance, max_distance, max_distance));
      if (targets.isEmpty()) {
         return null;
      } else {
         EntityLivingBase closest_target = null;
         float closest_distance = 0.0F;
         Iterator i = targets.iterator();

         while(true) {
            EntityLivingBase target;
            float distance;
            do {
               do {
                  boolean is_a_target_class;
                  do {
                     do {
                        do {
                           do {
                              if (!i.hasNext()) {
                                 return closest_target;
                              }

                              target = (EntityLivingBase)i.next();
                           } while(target.isDead);
                        } while(target.getHealth() <= 0.0F);

                        is_a_target_class = false;

                        for(int index = 0; index < target_classes.length; ++index) {
                           if ((target_classes[index] != EntityAnimal.class || !(target instanceof EntityAnimal) || target.isTrueAnimal()) && target.getClass().isAssignableFrom(target_classes[index])) {
                              is_a_target_class = true;
                              break;
                           }
                        }
                     } while(!is_a_target_class);
                  } while(requires_line_of_sight && !attacker.getEntitySenses().canSee(target));
               } while(requires_path && !attacker.canPathTo(target.getBlockPosX(), target.getFootBlockPosY(), target.getBlockPosZ(), (int)max_distance));

               distance = attacker.getDistanceToEntity(target);
            } while(closest_target != null && !(distance < closest_distance));

            closest_target = target;
            closest_distance = distance;
         }
      }
   }

   public EntityLivingBase getClosestPrey(EntityLiving attacker, double max_distance, boolean requires_line_of_sight, boolean requires_path) {
      List targets = this.getEntitiesWithinAABB(EntityLivingBase.class, attacker.boundingBox.expand(max_distance, max_distance, max_distance));
      if (targets.isEmpty()) {
         return null;
      } else {
         EntityLivingBase closest_target = null;
         float closest_distance = 0.0F;
         Iterator i = targets.iterator();

         while(true) {
            EntityLivingBase target;
            float distance;
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!i.hasNext()) {
                                 return closest_target;
                              }

                              target = (EntityLivingBase)i.next();
                           } while(target.isDead);
                        } while(target.getHealth() <= 0.0F);
                     } while(!attacker.preysUpon(target));
                  } while(requires_line_of_sight && !attacker.getEntitySenses().canSee(target));
               } while(requires_path && !attacker.canPathTo(target.getBlockPosX(), target.getFootBlockPosY(), target.getBlockPosZ(), (int)max_distance));

               distance = attacker.getDistanceToEntity(target);
            } while(closest_target != null && !(distance < closest_distance));

            closest_target = target;
            closest_distance = distance;
         }
      }
   }

   public boolean isPlayerNearby(double x, double y, double z, double range) {
      double range_sq = range * range;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer player = (EntityPlayer)this.playerEntities.get(i);
         if (!player.isGhost() && !player.isZevimrgvInTournament() && player.getDistanceSq(x, y, z) <= range_sq) {
            return true;
         }
      }

      return false;
   }

   public boolean hasNonGhostPlayers() {
      int size = this.playerEntities.size();

      for(int i = 0; i < size; ++i) {
         EntityPlayer player = (EntityPlayer)this.playerEntities.get(i);
         if (!player.isGhost() && !player.isZevimrgvInTournament()) {
            return true;
         }
      }

      return false;
   }

   public double getDistanceSqToNearestPlayer(int x, int y, int z) {
      if (!this.hasNonGhostPlayers()) {
         return Double.MAX_VALUE;
      } else {
         double distance_sq_to_nearest_player = Double.MAX_VALUE;
         Iterator i = this.playerEntities.iterator();

         while(i.hasNext()) {
            EntityPlayer player = (EntityPlayer)i.next();
            if (!player.isGhost() && !player.isZevimrgvInTournament()) {
               double distance_sq_to_player = getDistanceSqFromDeltas((float)(x - player.getBlockPosX()), (float)(y - player.getBlockPosY()), (float)(z - player.getBlockPosZ()));
               if (distance_sq_to_player < distance_sq_to_nearest_player) {
                  distance_sq_to_nearest_player = distance_sq_to_player;
               }
            }
         }

         if (distance_sq_to_nearest_player > 65536.0) {
            return Double.MAX_VALUE;
         } else {
            return distance_sq_to_nearest_player;
         }
      }
   }

   public float distanceToNearestPlayer(double x, double y, double z) {
      if (!this.hasNonGhostPlayers()) {
         return Float.MAX_VALUE;
      } else {
         double distance_to_nearest_player_sq = Double.MAX_VALUE;
         int size = this.playerEntities.size();

         for(int i = 0; i < size; ++i) {
            EntityPlayer player = (EntityPlayer)this.playerEntities.get(i);
            if (!player.isGhost() && !player.isZevimrgvInTournament()) {
               double distance_sq = player.getDistanceSq(x, y, z);
               if (distance_sq < distance_to_nearest_player_sq) {
                  distance_to_nearest_player_sq = distance_sq;
               }
            }
         }

         if (distance_to_nearest_player_sq > 65536.0) {
            return Float.MAX_VALUE;
         } else {
            return (float)Math.sqrt(distance_to_nearest_player_sq);
         }
      }
   }

   public double getDistanceSqToNearestPlayer(int x, int z) {
      if (!this.hasNonGhostPlayers()) {
         return Double.MAX_VALUE;
      } else {
         double distance_sq_to_nearest_player = Double.MAX_VALUE;
         Iterator i = this.playerEntities.iterator();

         while(i.hasNext()) {
            EntityPlayer player = (EntityPlayer)i.next();
            if (!player.isGhost() && !player.isZevimrgvInTournament()) {
               double distance_sq_to_player = getDistanceSqFromDeltas((double)(x - player.getBlockPosX()), (double)(z - player.getBlockPosZ()));
               if (distance_sq_to_player < distance_sq_to_nearest_player) {
                  distance_sq_to_nearest_player = distance_sq_to_player;
               }
            }
         }

         if (distance_sq_to_nearest_player > 65536.0) {
            return Double.MAX_VALUE;
         } else {
            return distance_sq_to_nearest_player;
         }
      }
   }

   public EntityPlayer getPlayerEntityByName(String par1Str) {
      for(int var2 = 0; var2 < this.playerEntities.size(); ++var2) {
         if (par1Str.equals(((EntityPlayer)this.playerEntities.get(var2)).getCommandSenderName())) {
            return (EntityPlayer)this.playerEntities.get(var2);
         }
      }

      return null;
   }

   public void sendQuittingDisconnectingPacket() {
   }

   public void checkSessionLock() throws MinecraftException {
      this.saveHandler.checkSessionLock();
   }

   public final void setTotalWorldTime(long par1) {
      this.worldInfo.setTotalWorldTime(par1, this);
   }

   public final void advanceTotalWorldTime(long ticks) {
      this.setTotalWorldTime(this.getTotalWorldTime() + ticks);
   }

   public long getSeed() {
      return this.worldInfo.getSeed();
   }

   public long getHashedSeed() {
      return this.worldInfo.getHashedSeed();
   }

   public final long getTotalWorldTime() {
      return this.worldInfo.getWorldTotalTime(this.getDimensionId());
   }

   public final long getTotalWorldTimeAtStartOfToday() {
      return getTotalWorldTimeAtStartOfDay(this.getDayOfWorld());
   }

   public final long getTotalWorldTimeAtEndOfToday() {
      return getTotalWorldTimeAtEndOfDay(this.getDayOfWorld());
   }

   public static final long getTotalWorldTimeAtStartOfDay(int day) {
      return (long)((day - 1) * 24000 - 6000);
   }

   public static final long getTotalWorldTimeAtEndOfDay(int day) {
      return getTotalWorldTimeAtStartOfDay(day) + 24000L - 1L;
   }

   public static final long getAdjustedTotalWorldTime(long unadjusted_tick) {
      return unadjusted_tick + 6000L;
   }

   public static final int getDayOfWorld(long unadjusted_tick) {
      return (int)(getAdjustedTotalWorldTime(unadjusted_tick) / 24000L) + 1;
   }

   public final int getDayOfWorld() {
      return getDayOfWorld(this.getTotalWorldTime());
   }

   public ChunkCoordinates getSpawnPoint() {
      return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
   }

   public int getSpawnX() {
      return this.worldInfo.getSpawnX();
   }

   public int getSpawnY() {
      return this.worldInfo.getSpawnY();
   }

   public int getSpawnZ() {
      return this.worldInfo.getSpawnZ();
   }

   public void setSpawnLocation(int par1, int par2, int par3) {
      this.worldInfo.setSpawnPosition(par1, par2, par3);
   }

   public void joinEntityInSurroundings(Entity par1Entity) {
      int var2 = MathHelper.floor_double(par1Entity.posX / 16.0);
      int var3 = MathHelper.floor_double(par1Entity.posZ / 16.0);
      byte var4 = 2;

      for(int var5 = var2 - var4; var5 <= var2 + var4; ++var5) {
         for(int var6 = var3 - var4; var6 <= var3 + var4; ++var6) {
            this.getChunkFromChunkCoords(var5, var6);
         }
      }

      if (!this.loadedEntityList.contains(par1Entity)) {
         this.loadedEntityList.add(par1Entity);
      }

   }

   public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4) {
      return true;
   }

   public void setEntityState(Entity par1Entity, EnumEntityState par2) {
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void addBlockEvent(int par1, int par2, int par3, int par4, int par5, int par6) {
      if (par4 > 0) {
         Block.blocksList[par4].onBlockEventReceived(this, par1, par2, par3, par5, par6);
      }

   }

   public ISaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public final WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public GameRules getGameRules() {
      return this.worldInfo.getGameRulesInstance();
   }

   public float getWeightedThunderStrength(float par1) {
      return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * par1) * this.getRainStrength(par1);
   }

   public float getRainStrength(float par1) {
      return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * par1;
   }

   public void setRainStrength(float par1) {
      this.prevRainingStrength = par1;
      this.rainingStrength = par1;
   }

   public final boolean isPrecipitatingAt(int x, int y, int z) {
      return (this.getBiomeGenForCoords(x, z).rainfall > 0.0F || this.isBloodMoon24HourPeriod()) && this.isPrecipitating(true) && this.getPrecipitationHeight(x, z) <= y;
   }

   public final boolean isSnowing(int x, int z) {
      return this.isPrecipitating(true) && this.isFreezing(x, z);
   }

   public final boolean isInRain(int x, int y, int z) {
      if (!this.isPrecipitating(true)) {
         return false;
      } else {
         BiomeGenBase biome = this.getBiomeGenForCoords(x, z);
         if ((biome.rainfall != 0.0F || this.isBloodMoon24HourPeriod()) && !biome.isFreezing()) {
            return this.canBlockSeeTheSky(x, y, z) && this.getPrecipitationHeight(x, z) <= y;
         } else {
            return false;
         }
      }
   }

   public boolean canLightningStrikeAt(int par1, int par2, int par3) {
      if (!this.isPrecipitating(true)) {
         return false;
      } else if (!this.canBlockSeeTheSky(par1, par2, par3)) {
         return false;
      } else if (this.getPrecipitationHeight(par1, par3) > par2) {
         return false;
      } else {
         BiomeGenBase var4 = this.getBiomeGenForCoords(par1, par3);
         return var4.getEnableSnow() ? false : var4.canSpawnLightningBolt(this.isBloodMoon24HourPeriod());
      }
   }

   public boolean isSkyOvercast(int x, int z) {
      if (!this.isPrecipitating(true)) {
         return false;
      } else {
         BiomeGenBase biome = this.getBiomeGenForCoords(x, z);
         return biome.hasRainfall() || this.isBloodMoon24HourPeriod();
      }
   }

   public boolean isBlockHighHumidity(int par1, int par2, int par3) {
      BiomeGenBase var4 = this.getBiomeGenForCoords(par1, par3);
      return var4.isHighHumidity();
   }

   public void setItemData(String par1Str, WorldSavedData par2WorldSavedData) {
      this.mapStorage.setData(par1Str, par2WorldSavedData);
   }

   public WorldSavedData loadItemData(Class par1Class, String par2Str) {
      return this.mapStorage.loadData(par1Class, par2Str);
   }

   public int peekUniqueDataId(String prefix) {
      return this.mapStorage.peekUniqueDataId(prefix);
   }

   public void setUniqueDataId(String prefix, short value) {
      this.mapStorage.setUniqueDataId(this, prefix, value);
   }

   public int getUniqueDataId(String par1Str) {
      return this.mapStorage.getUniqueDataId(this, par1Str);
   }

   public void func_82739_e(int par1, int par2, int par3, int par4, int par5) {
      for(int var6 = 0; var6 < this.worldAccesses.size(); ++var6) {
         ((IWorldAccess)this.worldAccesses.get(var6)).broadcastSound(par1, par2, par3, par4, par5);
      }

   }

   public void playAuxSFX(int id, int x, int y, int z, int data) {
      this.playAuxSFXAtEntity((EntityPlayer)null, id, x, y, z, data);
   }

   public void playAuxSFXAtEntity(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6) {
      try {
         for(int var7 = 0; var7 < this.worldAccesses.size(); ++var7) {
            ((IWorldAccess)this.worldAccesses.get(var7)).playAuxSFX(par1EntityPlayer, par2, par3, par4, par5, par6);
         }

      } catch (Throwable var10) {
         CrashReport var8 = CrashReport.makeCrashReport(var10, "Playing level event");
         CrashReportCategory var9 = var8.makeCategory("Level event being played");
         var9.addCrashSection("Block coordinates", CrashReportCategory.getLocationInfo(par3, par4, par5));
         var9.addCrashSection("Event source", par1EntityPlayer);
         var9.addCrashSection("Event type", par2);
         var9.addCrashSection("Event data", par6);
         throw new ReportedException(var8);
      }
   }

   public final int getHeight() {
      return 256;
   }

   public static final int getMaxBlockY() {
      return 255;
   }

   public int getActualHeight() {
      if (this.underworld_y_offset != 0) {
         return 256;
      } else {
         return this.provider.hasNoSky ? 128 : 256;
      }
   }

   public IUpdatePlayerListBox getMinecartSoundUpdater(EntityMinecart par1EntityMinecart) {
      return null;
   }

   public Random setRandomSeed(int par1, int par2, int par3) {
      long var4 = (long)par1 * 341873128712L + (long)par2 * 132897987541L + this.getWorldInfo().getSeed() + (long)par3;
      this.rand.setSeed(var4);
      return this.rand;
   }

   public ChunkPosition findClosestStructure(String par1Str, int par2, int par3, int par4) {
      return this.getChunkProvider().findClosestStructure(this, par1Str, par2, par3, par4);
   }

   public boolean extendedLevelsInChunkCache() {
      return false;
   }

   public double getHorizon() {
      return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport par1CrashReport) {
      CrashReportCategory var2 = par1CrashReport.makeCategoryDepth("Affected level", 1);
      var2.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
      var2.addCrashSectionCallable("All players", new CallableLvl2(this));
      var2.addCrashSectionCallable("Chunk stats", new CallableLvl3(this));

      try {
         this.worldInfo.addToCrashReport(var2);
      } catch (Throwable var4) {
         var2.addCrashSectionThrowable("Level Data Unobtainable", var4);
      }

      return var2;
   }

   public void destroyBlockInWorldPartially(int destroying_entity_id, int x, int y, int z, int tenths_destroyed) {
      for(int i = 0; i < this.worldAccesses.size(); ++i) {
         IWorldAccess world_access = (IWorldAccess)this.worldAccesses.get(i);
         world_access.destroyBlockPartially(destroying_entity_id, x, y, z, tenths_destroyed);
      }

   }

   public Vec3Pool getWorldVec3Pool() {
      return this.vecPool;
   }

   public Calendar getCurrentDate() {
      if (this.getTotalWorldTime() % 600L == 0L) {
         this.theCalendar.setTimeInMillis(MinecraftServer.getSystemTimeMillis());
      }

      return this.theCalendar;
   }

   public void func_92088_a(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13NBTTagCompound) {
   }

   public Scoreboard getScoreboard() {
      return this.worldScoreboard;
   }

   public void func_96440_m(int par1, int par2, int par3, int par4) {
      for(int var5 = 0; var5 < 4; ++var5) {
         int var6 = par1 + Direction.offsetX[var5];
         int var7 = par3 + Direction.offsetZ[var5];
         int var8 = this.getBlockId(var6, par2, var7);
         if (var8 != 0) {
            Block var9 = Block.blocksList[var8];
            if (Block.redstoneComparatorIdle.func_94487_f(var8)) {
               var9.onNeighborBlockChange(this, var6, par2, var7, par4);
            } else if (Block.isNormalCube(var8)) {
               var6 += Direction.offsetX[var5];
               var7 += Direction.offsetZ[var5];
               var8 = this.getBlockId(var6, par2, var7);
               var9 = Block.blocksList[var8];
               if (Block.redstoneComparatorIdle.func_94487_f(var8)) {
                  var9.onNeighborBlockChange(this, var6, par2, var7, par4);
               }
            }
         }
      }

   }

   public ILogAgent getWorldLogAgent() {
      return this.worldLogAgent;
   }

   public float getLocationTensionFactor(double par1, double par3, double par5) {
      return this.getTensionFactorForBlock(MathHelper.floor_double(par1), MathHelper.floor_double(par3), MathHelper.floor_double(par5));
   }

   public float getTensionFactorForBlock(int par1, int par2, int par3) {
      float var4 = 0.0F;
      boolean var5 = this.difficultySetting == 3;
      if (this.blockExists(par1, par2, par3)) {
         float var6 = this.getCurrentMoonPhaseFactor();
         var4 += MathHelper.clamp_float((float)this.getChunkFromBlockCoords(par1, par3).inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (var5 ? 1.0F : 0.75F);
         var4 += var6 * 0.25F;
      }

      if (this.difficultySetting < 2) {
         var4 *= (float)this.difficultySetting / 2.0F;
      }

      return MathHelper.clamp_float(var4, 0.0F, var5 ? 1.5F : 1.0F);
   }

   public boolean isServerRunning() {
      return DedicatedServer.it();
   }

   public Block getNearestBlockDirectlyAbove(int x, int y, int z) {
      while(true) {
         ++y;
         if (y < 256) {
            int block_id = this.getBlockId(x, y, z);
            if (block_id == 0) {
               continue;
            }

            return Block.blocksList[block_id];
         }

         return null;
      }
   }

   public final boolean isAirOrPassableBlock(int x, int y, int z, boolean include_liquid) {
      if (y >= 0 && y <= 255) {
         if (!this.blockExists(x, y, z)) {
            return false;
         } else {
            int block_id = this.getBlockId(x, y, z);
            if (block_id == 0) {
               return true;
            } else {
               Block block = Block.getBlock(block_id);
               if (block == null) {
                  return false;
               } else {
                  return !include_liquid && block.isLiquid() ? false : !block.isSolid(this, x, y, z);
               }
            }
         }
      } else {
         return true;
      }
   }

   public final boolean isAirOrPassableBlock(int[] coords, boolean include_liquid) {
      return this.isAirOrPassableBlock(coords[0], coords[1], coords[2], include_liquid);
   }

   public boolean isOutdoors(int x, int y, int z, boolean initial_call) {
      if (this.provider.hasNoSky) {
         return false;
      } else {
         boolean[] check_block = new boolean[49];
         if (initial_call && this.isAirOrPassableBlock(x, y + 1, z, false)) {
            if (this.isOutdoors(x, y + 1, z, false)) {
               return true;
            }

            if (this.isAirOrPassableBlock(x, y + 2, z, false) && this.isOutdoors(x, y + 2, z, false)) {
               return true;
            }
         }

         if (this.isAirOrPassableBlock(x, y, z, false) && this.getPrecipitationHeight(x, z) <= y) {
            return true;
         } else {
            if (this.isAirOrPassableBlock(x + 1, y, z, false)) {
               if (this.getPrecipitationHeight(x + 1, z) <= y) {
                  return true;
               }

               check_block[18] = true;
               check_block[26] = true;
               check_block[32] = true;
            }

            if (this.isAirOrPassableBlock(x - 1, y, z, false)) {
               if (this.getPrecipitationHeight(x - 1, z) <= y) {
                  return true;
               }

               check_block[16] = true;
               check_block[22] = true;
               check_block[30] = true;
            }

            if (this.isAirOrPassableBlock(x, y, z + 1, false)) {
               if (this.getPrecipitationHeight(x, z + 1) <= y) {
                  return true;
               }

               check_block[30] = true;
               check_block[38] = true;
               check_block[32] = true;
            }

            if (this.isAirOrPassableBlock(x, y, z - 1, false)) {
               if (this.getPrecipitationHeight(x, z - 1) <= y) {
                  return true;
               }

               check_block[16] = true;
               check_block[10] = true;
               check_block[18] = true;
            }

            if (check_block[18] && this.isAirOrPassableBlock(x + 1, y, z - 1, false)) {
               if (this.getPrecipitationHeight(x + 1, z - 1) <= y) {
                  return true;
               }

               check_block[11] = true;
               check_block[19] = true;
            }

            if (check_block[32] && this.isAirOrPassableBlock(x + 1, y, z + 1, false)) {
               if (this.getPrecipitationHeight(x + 1, z + 1) <= y) {
                  return true;
               }

               check_block[33] = true;
               check_block[39] = true;
            }

            if (check_block[30] && this.isAirOrPassableBlock(x - 1, y, z + 1, false)) {
               if (this.getPrecipitationHeight(x - 1, z + 1) <= y) {
                  return true;
               }

               check_block[29] = true;
               check_block[37] = true;
            }

            if (check_block[16] && this.isAirOrPassableBlock(x - 1, y, z - 1, false)) {
               if (this.getPrecipitationHeight(x - 1, z - 1) <= y) {
                  return true;
               }

               check_block[9] = true;
               check_block[15] = true;
            }

            if (check_block[26] && this.isAirOrPassableBlock(x + 2, y, z, false)) {
               if (this.getPrecipitationHeight(x + 2, z) <= y) {
                  return true;
               }

               check_block[19] = true;
               check_block[27] = true;
               check_block[33] = true;
            }

            if (check_block[38] && this.isAirOrPassableBlock(x, y, z + 2, false)) {
               if (this.getPrecipitationHeight(x, z + 2) <= y) {
                  return true;
               }

               check_block[37] = true;
               check_block[39] = true;
               check_block[45] = true;
            }

            if (check_block[22] && this.isAirOrPassableBlock(x - 2, y, z, false)) {
               if (this.getPrecipitationHeight(x - 2, z) <= y) {
                  return true;
               }

               check_block[15] = true;
               check_block[21] = true;
               check_block[29] = true;
            }

            if (check_block[10] && this.isAirOrPassableBlock(x, y, z - 2, false)) {
               if (this.getPrecipitationHeight(x, z - 2) <= y) {
                  return true;
               }

               check_block[3] = true;
               check_block[9] = true;
               check_block[11] = true;
            }

            if (check_block[11] && this.isAirOrPassableBlock(x + 1, y, z - 2, false)) {
               if (this.getPrecipitationHeight(x + 1, z - 2) <= y) {
                  return true;
               }

               check_block[12] = true;
            }

            if (check_block[19] && this.isAirOrPassableBlock(x + 2, y, z - 1, false)) {
               if (this.getPrecipitationHeight(x + 2, z - 1) <= y) {
                  return true;
               }

               check_block[12] = true;
            }

            if (check_block[33] && this.isAirOrPassableBlock(x + 2, y, z + 1, false)) {
               if (this.getPrecipitationHeight(x + 2, z + 1) <= y) {
                  return true;
               }

               check_block[40] = true;
            }

            if (check_block[39] && this.isAirOrPassableBlock(x + 1, y, z + 2, false)) {
               if (this.getPrecipitationHeight(x + 1, z + 2) <= y) {
                  return true;
               }

               check_block[40] = true;
            }

            if (check_block[37] && this.isAirOrPassableBlock(x - 1, y, z + 2, false)) {
               if (this.getPrecipitationHeight(x - 1, z + 2) <= y) {
                  return true;
               }

               check_block[36] = true;
            }

            if (check_block[29] && this.isAirOrPassableBlock(x - 2, y, z + 1, false)) {
               if (this.getPrecipitationHeight(x - 2, z + 1) <= y) {
                  return true;
               }

               check_block[36] = true;
            }

            if (check_block[15] && this.isAirOrPassableBlock(x - 2, y, z - 1, false)) {
               if (this.getPrecipitationHeight(x - 2, z - 1) <= y) {
                  return true;
               }

               check_block[8] = true;
            }

            if (check_block[9] && this.isAirOrPassableBlock(x - 1, y, z - 2, false)) {
               if (this.getPrecipitationHeight(x - 1, z - 2) <= y) {
                  return true;
               }

               check_block[8] = true;
            }

            if (check_block[12] && this.isAirOrPassableBlock(x + 2, y, z - 2, false) && this.getPrecipitationHeight(x + 2, z - 2) <= y) {
               return true;
            } else if (check_block[40] && this.isAirOrPassableBlock(x + 2, y, z + 2, false) && this.getPrecipitationHeight(x + 2, z + 2) <= y) {
               return true;
            } else if (check_block[36] && this.isAirOrPassableBlock(x - 2, y, z + 2, false) && this.getPrecipitationHeight(x - 2, z + 2) <= y) {
               return true;
            } else if (check_block[8] && this.isAirOrPassableBlock(x - 2, y, z - 2, false) && this.getPrecipitationHeight(x - 2, z - 2) <= y) {
               return true;
            } else if (check_block[27] && this.isAirOrPassableBlock(x + 3, y, z, false) && this.getPrecipitationHeight(x + 3, z) <= y) {
               return true;
            } else if (check_block[45] && this.isAirOrPassableBlock(x, y, z + 3, false) && this.getPrecipitationHeight(x, z + 3) <= y) {
               return true;
            } else if (check_block[21] && this.isAirOrPassableBlock(x - 3, y, z, false) && this.getPrecipitationHeight(x - 3, z) <= y) {
               return true;
            } else {
               return check_block[3] && this.isAirOrPassableBlock(x, y, z - 3, false) && this.getPrecipitationHeight(x, z - 3) <= y;
            }
         }
      }
   }

   public boolean isOutdoors(int x, int y, int z) {
      return this.isOutdoors(x, y, z, true);
   }

   public boolean isInSunlight(int x, int y, int z) {
      return this.isDaytime() && this.canBlockSeeTheSky(x, y, z) && !this.isPrecipitating(true);
   }

   public final EntityPlayer getRandomNonGhostPlayer(boolean must_be_alive) {
      return this.getRandomPlayer(true, must_be_alive);
   }

   public final EntityPlayer getRandomPlayer(boolean must_not_be_ghost, boolean must_be_alive) {
      if (this.playerEntities.size() == 0) {
         return null;
      } else {
         EntityPlayer[] candidates = new EntityPlayer[100];
         int num_candidates = 0;
         Iterator i = this.playerEntities.iterator();

         while(true) {
            EntityPlayer player;
            do {
               do {
                  do {
                     if (!i.hasNext()) {
                        return num_candidates == 0 ? null : candidates[this.rand.nextInt(num_candidates)];
                     }

                     player = (EntityPlayer)i.next();
                  } while(player.isZevimrgvInTournament());
               } while(must_not_be_ghost && player.isGhost());
            } while(must_be_alive && player.getHealth() <= 0.0F);

            candidates[num_candidates++] = player;
         }
      }
   }

   public static int[] getNeighboringBlockCoords(int x, int y, int z, EnumFace face) {
      if (face == EnumFace.BOTTOM) {
         --y;
      } else if (face == EnumFace.TOP) {
         ++y;
      } else if (face == EnumFace.NORTH) {
         --z;
      } else if (face == EnumFace.SOUTH) {
         ++z;
      } else if (face == EnumFace.WEST) {
         --x;
      } else if (face == EnumFace.EAST) {
         ++x;
      }

      return new int[]{x, y, z};
   }

   public final Block getBlock(int x, int y, int z) {
      return Block.blocksList[this.getBlockId(x, y, z)];
   }

   public final Block getBlock(int[] coords) {
      return this.getBlock(coords[0], coords[1], coords[2]);
   }

   public final Block getBlockWithRefreshedBounds(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      if (block != null) {
         block.setBlockBoundsBasedOnStateAndNeighbors(this, x, y, z);
      }

      return block;
   }

   public BlockInfo getBlockInfo(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      return block == null ? null : new BlockInfo(block, x, y, z);
   }

   public void watchAnimal(int par1, int par2, int par3, int par4, int par5) {
      for(int var6 = 0; var6 < this.worldAccesses.size(); ++var6) {
         IWorldAccess var7 = (IWorldAccess)this.worldAccesses.get(var6);
         var7.destroyBlockPartially(par1, par2, par3, par4, par5);
      }

   }

   public Vec3 getBlockCenterPos(int x, int y, int z) {
      return this.getWorldVec3Pool().getVecFromPool((double)x + 0.50000001, (double)((float)y + 0.5F), (double)((float)z + 0.5F));
   }

   public final boolean canCastRayBetweenBlockCenters(RaycastPolicies policies, int origin_x, int origin_y, int origin_z, int target_x, int target_y, int target_z, boolean allow_collision_at_target_coords) {
      RaycastCollision rc = this.getBlockCollisionForPolicies(this.getBlockCenterPos(origin_x, origin_y, origin_z), this.getBlockCenterPos(target_x, target_y, target_z), policies);
      return rc == null || allow_collision_at_target_coords && rc.isBlockAt(target_x, target_y, target_z);
   }

   public final boolean canCastRayBetweenBlockCenters(Raycast raycast, int origin_x, int origin_y, int origin_z, int target_x, int target_y, int target_z, boolean allow_collision_at_target_coords) {
      RaycastCollision rc = raycast.getBlockCollision(this.getBlockCenterPos(origin_x, origin_y, origin_z), this.getBlockCenterPos(target_x, target_y, target_z));
      return rc == null || allow_collision_at_target_coords && rc.isBlockAt(target_x, target_y, target_z);
   }

   public void addToSpawnPendingList(Entity entity, long spawn_time) {
      if (!this.isRemote) {
         this.pending_entity_spawns.add(new EntitySpawnPendingEntry(entity, spawn_time));
      }
   }

   public Vec3 getVec3(double x, double y, double z) {
      return this.vecPool.getVecFromPool(x, y, z);
   }

   public Vec3 getVec3() {
      return this.vecPool.getVecFromPool(0.0, 0.0, 0.0);
   }

   public final AxisAlignedBB getBoundingBoxFromPool(double min_x, double min_y, double min_z, double max_x, double max_y, double max_z) {
      return AxisAlignedBB.getBoundingBoxFromPool(min_x, min_y, min_z, max_x, max_y, max_z);
   }

   public final AxisAlignedBB getBoundingBoxFromPool(int x, int y, int z) {
      return AxisAlignedBB.getBoundingBoxFromPool((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
   }

   public void markWorldMapPixelDirty(int x, int z) {
   }

   public void scheduleBlockChange(int x, int y, int z, int from_block_id, int to_block_id, int to_metadata, int ticks_from_now) {
   }

   public void blockFX(EnumBlockFX kind, int x, int y, int z, SignalData data) {
      if (this.isRemote) {
         Minecraft.setErrorMessage("blockFX: only valid on server (" + kind + ")");
      } else {
         Packet85SimpleSignal packet = (new Packet85SimpleSignal(EnumSignal.block_fx, kind)).setBlockCoords(x, y, z);
         if (data != null) {
            packet.addData(data);
         }

         MinecraftServer.getServer().getConfigurationManager().sendToAllNearExcept((EntityPlayer)null, (double)x, (double)y, (double)z, 64.0, this.provider.dimensionId, packet);
      }
   }

   public void blockFX(EnumBlockFX kind, int x, int y, int z) {
      this.blockFX(kind, x, y, z, (SignalData)null);
   }

   public boolean isFullWaterBlock(int x, int y, int z, boolean include_moving_water) {
      return BlockFluid.isFullWaterBlock(this.getBlock(x, y, z), this.getBlockMetadata(x, y, z), include_moving_water);
   }

   public boolean isFullLavaBlock(int x, int y, int z, boolean include_moving_lava) {
      return BlockFluid.isFullLavaBlock(this.getBlock(x, y, z), this.getBlockMetadata(x, y, z), include_moving_lava);
   }

   public boolean isWaterBlock(int x, int y, int z) {
      return this.getBlockMaterial(x, y, z) == Material.water;
   }

   public boolean isLavaBlock(int x, int y, int z) {
      return this.getBlockMaterial(x, y, z) == Material.lava;
   }

   public boolean isLiquidBlock(int x, int y, int z) {
      return this.getBlockMaterial(x, y, z).isLiquid();
   }

   public void sendPacketToAllAssociatedPlayers(Entity entity, Packet packet) {
      if (this.isRemote) {
         Minecraft.setErrorMessage("sendPacketToAllAssociatedPlayers: only meant to be called on server");
      }

   }

   public final boolean isOverworld() {
      return this.is_overworld;
   }

   public void douseFire(int x, int y, int z, Entity dousing_entity) {
      if (this.isRemote) {
         Minecraft.setErrorMessage("douseFire: not meant to be called on client");
      } else if (this.getBlock(x, y, z) == Block.fire) {
         this.blockFX(EnumBlockFX.smoke_and_steam, x, y, z);
         this.setBlockToAir(x, y, z);
         if (dousing_entity != null) {
            dousing_entity.causeQuenchEffect();
         }

      }
   }

   public boolean tryConvertLavaToCobblestoneOrObsidian(int x, int y, int z) {
      if (this instanceof WorldServer) {
         if (this.getBlockMaterial(x, y, z) != Material.lava) {
            return false;
         } else {
            this.blockFX(EnumBlockFX.lava_mixing_with_water, x, y, z);
            if (this.isFullLavaBlock(x, y, z, true) && !((WorldServer)this).isBlockScheduledToBecome(x, y, z, Block.lavaMoving.blockID, -1)) {
               this.setBlock(x, y, z, Block.obsidian.blockID, 0, 3);
            } else {
               this.setBlock(x, y, z, Block.cobblestone.blockID, 0, 3);
            }

            return true;
         }
      } else {
         Minecraft.setErrorMessage("tryConvertLavaToCobblestoneOrObsidian: only meant to be called on server");
         return false;
      }
   }

   public boolean tryConvertWaterToCobblestone(int x, int y, int z) {
      if (this instanceof WorldServer) {
         if (this.getBlockMaterial(x, y, z) != Material.water) {
            return false;
         } else {
            this.blockFX(EnumBlockFX.lava_mixing_with_water, x, y, z);
            this.setBlock(x, y, z, Block.cobblestone.blockID, 0, 3);
            return true;
         }
      } else {
         Minecraft.setErrorMessage("tryConvertWaterToCobblestone: only meant to be called on server");
         return false;
      }
   }

   public Block getNeighborBlock(int x, int y, int z, EnumFace face) {
      return this.getBlock(face.getNeighborX(x), face.getNeighborY(y), face.getNeighborZ(z));
   }

   public Block getNeighborBlock(int x, int y, int z, EnumDirection direction) {
      return this.getBlock(direction.getNeighborX(x), direction.getNeighborY(y), direction.getNeighborZ(z));
   }

   public int getNeighborBlockMetadata(int x, int y, int z, EnumFace face) {
      return this.getBlockMetadata(face.getNeighborX(x), face.getNeighborY(y), face.getNeighborZ(z));
   }

   public int getNeighborBlockMetadata(int x, int y, int z, EnumDirection direction) {
      return this.getBlockMetadata(direction.getNeighborX(x), direction.getNeighborY(y), direction.getNeighborZ(z));
   }

   public Material getNeighborBlockMaterial(int x, int y, int z, EnumFace face) {
      return this.getBlockMaterial(face.getNeighborX(x), face.getNeighborY(y), face.getNeighborZ(z));
   }

   public final Material getNeighborBlockMaterial(int x, int y, int z, EnumDirection direction) {
      return this.getBlockMaterial(direction.getNeighborX(x), direction.getNeighborY(y), direction.getNeighborZ(z));
   }

   public boolean isNeighborBlockNormalCube(int x, int y, int z, EnumFace face) {
      return this.isBlockNormalCube(face.getNeighborX(x), face.getNeighborY(y), face.getNeighborZ(z));
   }

   public boolean isNeighborBlockNormalCubeDefault(int x, int y, int z, EnumDirection direction, boolean return_value_if_chunk_does_not_exist) {
      return this.isBlockNormalCubeDefault(direction.getNeighborX(x), direction.getNeighborY(y), direction.getNeighborZ(z), return_value_if_chunk_does_not_exist);
   }

   public boolean isNeighborBlockSolidStandardFormCube(int x, int y, int z, EnumFace face) {
      return this.isBlockSolidStandardFormCube(face.getNeighborX(x), face.getNeighborY(y), face.getNeighborZ(z));
   }

   public boolean neighborBlockExists(int x, int y, int z, EnumDirection direction) {
      return this.blockExists(direction.getNeighborX(x), direction.getNeighborY(y), direction.getNeighborZ(z));
   }

   public boolean isBlockFaceFlatAndSolid(int x, int y, int z, EnumFace face) {
      Block block = this.getBlock(x, y, z);
      return block != null && block.isFaceFlatAndSolid(this.getBlockMetadata(x, y, z), face);
   }

   public final boolean isWorldClient() {
      return this instanceof WorldClient;
   }

   public final boolean isWorldServer() {
      return this instanceof WorldServer;
   }

   public final WorldClient getAsWorldClient() {
      return (WorldClient)this;
   }

   public final WorldServer getAsWorldServer() {
      return (WorldServer)this;
   }

   public final double getDistanceSqFromWorldSpawn(int x, int y, int z) {
      return getDistanceSqFromDeltas((float)(x - this.worldInfo.getSpawnX()), (float)(y - this.worldInfo.getSpawnY()), (float)(z - this.worldInfo.getSpawnZ()));
   }

   public final double getDistanceSqFromWorldSpawn(int x, int z) {
      return getDistanceSqFromDeltas((float)(x - this.worldInfo.getSpawnX()), 0.0F, (float)(z - this.worldInfo.getSpawnZ()));
   }

   public final double getDistanceFromWorldSpawn(int x, int y, int z) {
      return (double)MathHelper.sqrt_double(this.getDistanceSqFromWorldSpawn(x, y, z));
   }

   public final double getDistanceFromWorldSpawn(int x, int z) {
      return (double)MathHelper.sqrt_double(this.getDistanceSqFromWorldSpawn(x, z));
   }

   public final double getDistanceSqFromWorldOrigin(int x, int z) {
      return getDistanceSqFromDeltas((float)x, 0.0F, (float)z);
   }

   public final double getDistanceFromWorldOrigin(int x, int z) {
      return (double)MathHelper.sqrt_double(this.getDistanceSqFromWorldOrigin(x, z));
   }

   public boolean isWithinTournamentSafeZone(int x, int y, int z) {
      return DedicatedServer.isTournamentThatHasSafeZone() && this.getDistanceSqFromWorldSpawn(x, y, z) < 1024.0;
   }

   public boolean isWithinTournamentArena(int x, int z) {
      if (!DedicatedServer.isTournament()) {
         return false;
      } else {
         int spawn_x = this.worldInfo.getSpawnX();
         int spawn_z = this.worldInfo.getSpawnZ();
         int domain = DedicatedServer.getTournamentArenaRadius();
         int min_x = spawn_x - domain;
         int max_x = spawn_x + domain;
         int min_z = spawn_z - domain;
         int max_z = spawn_z + domain;
         return x >= min_x && x <= max_x && z >= min_z && z <= max_z;
      }
   }

   public final int getDimensionId() {
      return this.provider.dimensionId;
   }

   public final boolean isUnderworld() {
      return this.is_underworld;
   }

   public final boolean isTheNether() {
      return this.is_nether;
   }

   public final boolean isTheEnd() {
      return this.is_the_end;
   }

   public final boolean hasSkylight() {
      return this.has_skylight;
   }

   public boolean areSkillsEnabled() {
      return this.worldInfo.areSkillsEnabled();
   }

   public boolean areCoinsEnabled() {
      return this.worldInfo.areCoinsEnabled();
   }

   public World getWorld() {
      return this;
   }

   public final boolean isBiomeFreezing(int x, int z) {
      return this.getBiomeGenForCoords(x, z).isFreezing();
   }

   public String getDimensionName() {
      return this.provider.getDimensionName();
   }

   public String getClientOrServerString() {
      return this.isRemote ? "client" : (this instanceof WorldServer ? "server" : "unknown");
   }

   public final AxisAlignedBB getCollisionBoundsCombined(int x, int y, int z, Entity entity) {
      Block block = this.getBlock(x, y, z);
      return block == null ? null : block.getCollisionBoundsCombined(this, x, y, z, entity, true);
   }

   public final AxisAlignedBB getCollisionBoundsCombined(Block block, int x, int y, int z, Entity entity) {
      return block == null ? null : block.getCollisionBoundsCombined(this, x, y, z, entity, true);
   }

   public final boolean isBlockSolid(int x, int y, int z) {
      return Block.isBlockSolid(this, x, y, z);
   }

   public final boolean isBlockSolid(Block block, int x, int y, int z) {
      return Block.isBlockSolid(this, block, x, y, z);
   }

   public final boolean isBlockLiquid(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      return block != null && block.isLiquid();
   }

   public final boolean checkIfBlockIsNotLegal(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      return block == null ? false : block.checkIfNotLegal(this, x, y, z);
   }

   public final boolean doesBlockBlockFluids(int x, int y, int z) {
      int block_id = this.getBlockId(x, y, z);
      if (block_id == 0) {
         return false;
      } else {
         Block block = Block.getBlock(block_id);
         return block.always_blocks_fluids ? true : (block.never_blocks_fluids ? false : block.blocksFluids(this.getBlockMetadata(x, y, z)));
      }
   }

   public final double getBlockCollisionTopY(int x, int y, int z, Entity entity) {
      AxisAlignedBB bb = this.getCollisionBoundsCombined(x, y, z, entity);
      if (bb == null) {
         --y;
         bb = this.getCollisionBoundsCombined(x, y, z, entity);
      }

      return bb == null ? (double)y : bb.maxY;
   }

   public final double getBlockRenderTopY(int x, int y, int z) {
      return this.getBlockRenderTopY(this.getBlock(x, y, z), x, y, z);
   }

   public final double getBlockRenderTopY(Block block, int x, int y, int z) {
      if (block == null) {
         return (double)y;
      } else {
         block.setBlockBoundsBasedOnStateAndNeighbors(this, x, y, z);
         return (double)y + block.maxY[Minecraft.getThreadIndex()];
      }
   }

   public final boolean canBlockBePathedInto(int x, int y, int z, Entity entity, boolean allow_closed_wooden_portals) {
      if (!this.blockExists(x, y, z)) {
         return false;
      } else {
         int block_id = this.getBlockId(x, y, z);
         return block_id == 0 || Block.getBlock(block_id).canBePathedInto(this, x, y, z, entity, allow_closed_wooden_portals);
      }
   }

   public final boolean isPointInsideBlockCollisionBounds(Vec3 point) {
      int x = point.getBlockX();
      int y = point.getBlockY();
      int z = point.getBlockZ();
      int block_id = this.getBlockId(x, y, z);
      return block_id != 0 && Block.getBlock(block_id).doCollisionBoundsContain(this, x, y, z, point);
   }

   public final boolean doBlockCollisionBoundsIntersectWithBB(int x, int y, int z, AxisAlignedBB bb) {
      int block_id = this.getBlockId(x, y, z);
      return block_id != 0 && Block.getBlock(block_id).doCollisionBoundsIntersectWith(this, x, y, z, bb);
   }

   public final boolean doesBBIntersectWithBlockCollisionBounds(AxisAlignedBB bb) {
      int min_x = bb.getBlockCoordForMinX();
      int min_y = bb.getBlockCoordForMinY();
      int min_z = bb.getBlockCoordForMinZ();
      int max_x = bb.getBlockCoordForMaxX();
      int max_y = bb.getBlockCoordForMaxY();
      int max_z = bb.getBlockCoordForMaxZ();

      for(int x = min_x; x <= max_x; ++x) {
         for(int y = min_y; y <= max_y; ++y) {
            for(int z = min_z; z <= max_z; ++z) {
               if (this.doBlockCollisionBoundsIntersectWithBB(x, y, z, bb)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean doesEBSExist(int x, int y, int z) {
      if (y >= 0 && y <= 255) {
         Chunk chunk = this.getChunkFromBlockCoords(x, z);
         return chunk.storageArrays[y >> 4] != null;
      } else {
         return false;
      }
   }

   public final int getBlockDomainRadius() {
      return this.block_domain_radius;
   }

   public final int getRunegateDomainRadius(Material material) {
      return material == Material.adamantium ? this.runegate_adamantium_domain_radius : this.runegate_mithril_domain_radius;
   }

   public final boolean isWithinBlockDomain(int x, int z) {
      return x >= this.min_block_xz && x <= this.max_block_xz && z >= this.min_block_xz && z <= this.max_block_xz;
   }

   public final boolean isWithinBlockBounds(int x, int y, int z) {
      return y >= 0 && y < 256 && x >= this.min_block_xz && x <= this.max_block_xz && z >= this.min_block_xz && z <= this.max_block_xz;
   }

   public final boolean isChunkWithinBlockDomain(int chunk_x, int chunk_z) {
      return chunk_x >= this.min_chunk_xz && chunk_x <= this.max_chunk_xz && chunk_z >= this.min_chunk_xz && chunk_z <= this.max_chunk_xz;
   }

   public final boolean isWithinEntityDomain(double pos_x, double pos_z) {
      return pos_x >= this.min_entity_pos_xz && pos_x <= this.max_entity_pos_xz && pos_z >= this.min_entity_pos_xz && pos_z <= this.max_entity_pos_xz;
   }

   public final void validateDomainValues() {
      int domain_radius = this.block_domain_radius;
      int multiple_of = this.isOverworld() ? 128 : 16;
      if (domain_radius % multiple_of != 0) {
         Debug.setErrorMessage("Minecraft: domain_radius of " + this.getDimensionName() + " must be a multiple of " + multiple_of);
      }

      if (this.min_block_xz != -domain_radius) {
         Debug.setErrorMessage("Minecraft: min_block_xz is invalid");
      }

      if (this.max_block_xz != domain_radius - 1) {
         Debug.setErrorMessage("Minecraft: max_block_xz is invalid");
      }

      if (this.min_chunk_xz != -domain_radius / 16) {
         Debug.setErrorMessage("Minecraft: min_chunk_xz is invalid");
      }

      if (this.max_chunk_xz != domain_radius / 16 - 1) {
         Debug.setErrorMessage("Minecraft: max_chunk_xz is invalid");
      }

      if (this.min_chunk_xz % 32 != 0) {
         Debug.setErrorMessage("Minecraft: min_chunk_xz must be a multiple of 32");
      }

      if (this.max_chunk_xz % 32 != 31) {
         Debug.setErrorMessage("Minecraft: max_chunk_xz is not aligned to a region boundary");
      }

      int min_chunk_xz = this.min_chunk_xz;
      int max_chunk_xz = this.max_chunk_xz;
      if (min_chunk_xz * 16 != this.min_block_xz) {
         Debug.setErrorMessage("Minecraft: min_chunk_xz vs min_block_xz discrepency");
      }

      if (max_chunk_xz * 16 + 15 != this.max_block_xz) {
         Debug.setErrorMessage("Minecraft: max_chunk_xz vs max_block_x discrepency");
      }

   }

   public final boolean hasStandardFormOpaqueBlockAbove(int x, int y, int z) {
      int hmv = this.getHeightValue(x, z);

      do {
         ++y;
         if (y >= hmv) {
            return false;
         }
      } while(!this.isBlockStandardFormOpaqueCube(x, y, z));

      return true;
   }

   public final boolean tryToMeltBlock(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      return block != null && block.melt(this, x, y, z);
   }

   public final List generateWeatherEvents(int day) {
      if (!this.isOverworld()) {
         Debug.setErrorMessage("generateWeatherEvents: called for " + this.getDimensionName());
      }

      List events = new ArrayList();
      if (day < 2) {
         return events;
      } else {
         long first_tick_of_day = (long)((day - 1) * 24000 - 6000);
         Random random = new Random(this.getWorldCreationTime() + (long)(this.getDimensionId() * 938473) + (long)day);
         random.nextInt();

         for(int i = 0; i < 3 && random.nextInt(4) <= 0; ++i) {
            WeatherEvent event = new WeatherEvent(first_tick_of_day + (long)random.nextInt(24000), random.nextInt(12000) + 6000);
            if (!isHarvestMoon(event.start, true) && !isHarvestMoon(event.end, true) && !isHarvestMoon(event.start + 6000L, true) && !isHarvestMoon(event.end - 6000L, true) && !isBloodMoon(event.start, false) && !isBloodMoon(event.end, false) && !isBlueMoon(event.start, false) && !isBlueMoon(event.end, false)) {
               events.add(event);
            }
         }

         if (isBloodMoon(first_tick_of_day + 6000L, false)) {
            WeatherEvent event = new WeatherEvent(first_tick_of_day + 6000L, 13000);
            event.setStorm(event.start, event.end);
            events.add(event);
         }

         return events;
      }
   }

   public final List generateWeatherEvents(int from_day, int to_day) {
      List events = new ArrayList();

      for(int day = from_day; day <= to_day; ++day) {
         events.addAll(this.generateWeatherEvents(day));
      }

      return events;
   }

   private static List mergeWeatherEvents(List events) {
      Iterator i = events.iterator();

      while(true) {
         WeatherEvent a;
         do {
            if (!i.hasNext()) {
               i = events.iterator();

               while(i.hasNext()) {
                  a = (WeatherEvent)i.next();
                  if (a.removed) {
                     i.remove();
                  }
               }

               Collections.sort(events, new WeatherEventComparator());
               return events;
            }

            a = (WeatherEvent)i.next();
         } while(a.removed);

         Iterator j = events.iterator();

         while(j.hasNext()) {
            WeatherEvent b = (WeatherEvent)j.next();
            if (b != a && !b.removed) {
               int padding = 6000;
               if (a.end + (long)padding >= b.start && a.start <= b.end + (long)padding) {
                  a.setStartAndEnd(Math.min(a.start, b.start), Math.max(a.end, b.end));
                  if (b.hasStorm() && !a.hasStorm()) {
                     a.setStorm(b.start_of_storm, b.end_of_storm);
                  }

                  b.removed = true;
               }
            }
         }
      }
   }

   public static List addRandomWindAndStormsToWeatherEvents(List events) {
      Iterator i = events.iterator();

      while(i.hasNext()) {
         WeatherEvent event = (WeatherEvent)i.next();
         event.randomizeType();
         event.addStorm();
      }

      return events;
   }

   public final boolean canPrecipitate() {
      return this.isOverworld();
   }

   public final List getWeatherEventsForToday() {
      if (!this.canPrecipitate()) {
         return null;
      } else {
         int day = this.getDayOfWorld();
         if (this.weather_events_for_today == null || this.weather_events_for_day != day) {
            this.weather_events_for_day = day;
            this.weather_events_for_today = addRandomWindAndStormsToWeatherEvents(mergeWeatherEvents(this.generateWeatherEvents(day - 1, day)));
         }

         return this.weather_events_for_today;
      }
   }

   public final WeatherEvent getWeatherEventAt(long unadjusted_tick, boolean must_have_storm, boolean must_have_storm_at_unadjusted_tick) {
      int day = this.getDayOfWorld();
      if (unadjusted_tick < getTotalWorldTimeAtStartOfDay(day - 1) || unadjusted_tick > getTotalWorldTimeAtEndOfDay(day)) {
         Debug.setErrorMessage("getWeatherEventAt: params out of bounds");
      }

      if (!must_have_storm && must_have_storm_at_unadjusted_tick) {
         Debug.setErrorMessage("getWeatherEventAt: must_have_storm_at_specified_time=true but must_have_storm=false");
      }

      List events = this.getWeatherEventsForToday();
      if (events == null) {
         return null;
      } else {
         Iterator i = events.iterator();

         WeatherEvent event;
         do {
            do {
               do {
                  if (!i.hasNext()) {
                     return null;
                  }

                  event = (WeatherEvent)i.next();
               } while(!event.isOccurringAt(unadjusted_tick));
            } while(must_have_storm && !event.hasStorm());
         } while(must_have_storm_at_unadjusted_tick && !event.isStormingAt(unadjusted_tick));

         return event;
      }
   }

   public final WeatherEvent getCurrentWeatherEvent(boolean with_storm, boolean with_storm_at_unadjusted_tick) {
      return this.getWeatherEventAt(this.getTotalWorldTime(), with_storm, with_storm_at_unadjusted_tick);
   }

   public final WeatherEvent getCurrentWeatherEvent() {
      return this.getCurrentWeatherEvent(false, false);
   }

   public final WeatherEvent getNextWeatherEvent(boolean with_storm) {
      return this.getNextWeatherEvent(with_storm, this.getTotalWorldTime());
   }

   public final WeatherEvent getNextWeatherEvent(boolean with_storm, long from_tick) {
      List events = this.getWeatherEventsForToday();
      if (events == null) {
         return null;
      } else {
         Iterator i = events.iterator();

         WeatherEvent event;
         do {
            do {
               if (!i.hasNext()) {
                  return null;
               }

               event = (WeatherEvent)i.next();
            } while(with_storm && !event.hasStorm());
         } while(event.start < from_tick);

         return event;
      }
   }

   public final WeatherEvent getPreviousWeatherEvent(boolean with_storm) {
      List events = this.getWeatherEventsForToday();
      if (events == null) {
         return null;
      } else {
         WeatherEvent previous_event = null;
         long latest_end = 0L;
         Iterator i = events.iterator();

         while(true) {
            WeatherEvent event;
            do {
               if (!i.hasNext()) {
                  return previous_event;
               }

               event = (WeatherEvent)i.next();
            } while(with_storm && !event.hasStorm());

            if (event.end > latest_end && event.end < this.getTotalWorldTime()) {
               latest_end = event.end;
               previous_event = event;
            }
         }
      }
   }

   public final boolean isPrecipitating(boolean based_on_rain_strength) {
      if (based_on_rain_strength) {
         return (double)this.getRainStrength(1.0F) > 0.2;
      } else {
         return this.is_precipitating;
      }
   }

   public final boolean isThundering(boolean based_on_thunder_strength) {
      if (based_on_thunder_strength) {
         return (double)this.getWeightedThunderStrength(1.0F) > 0.9;
      } else {
         return this.is_storming;
      }
   }

   public final int getPrecipitationType(int default_type) {
      WeatherEvent event = this.getCurrentWeatherEvent();
      return event == null ? default_type : event.type;
   }

   public final boolean willPrecipitationStart(long unadjusted_tick_from, long unadjusted_tick_to) {
      int day = this.getDayOfWorld();
      if (unadjusted_tick_from < getTotalWorldTimeAtStartOfDay(day - 1) || unadjusted_tick_to > getTotalWorldTimeAtEndOfDay(day)) {
         Debug.setErrorMessage("willPrecipitationStart: params out of bounds");
      }

      List events = this.getWeatherEventsForToday();
      if (events == null) {
         return false;
      } else {
         Iterator i = events.iterator();

         WeatherEvent event;
         do {
            if (!i.hasNext()) {
               return false;
            }

            event = (WeatherEvent)i.next();
         } while(!event.startsPrecipitating(unadjusted_tick_from, unadjusted_tick_to));

         return true;
      }
   }

   public final boolean willStormStart(long unadjusted_tick_from, long unadjusted_tick_to) {
      int day = this.getDayOfWorld();
      if (unadjusted_tick_from < getTotalWorldTimeAtStartOfDay(day - 1) || unadjusted_tick_to > getTotalWorldTimeAtEndOfDay(day)) {
         Debug.setErrorMessage("willStormStart: params out of bounds");
      }

      List events = this.getWeatherEventsForToday();
      if (events == null) {
         return false;
      } else {
         Iterator i = events.iterator();

         WeatherEvent event;
         do {
            if (!i.hasNext()) {
               return false;
            }

            event = (WeatherEvent)i.next();
         } while(!event.startsStorming(unadjusted_tick_from, unadjusted_tick_to));

         return true;
      }
   }

   public final boolean willPrecipitationStartToday(int time_offset_from_start_of_day) {
      if (time_offset_from_start_of_day < 0 || time_offset_from_start_of_day > 24000) {
         Debug.setErrorMessage("willPrecipitationStartToday: time_offset out of bounds " + time_offset_from_start_of_day);
      }

      return this.willPrecipitationStart(this.getTotalWorldTimeAtStartOfToday() + (long)time_offset_from_start_of_day, this.getTotalWorldTimeAtEndOfToday());
   }

   public final boolean willStormStartToday(int time_offset_from_start_of_day) {
      if (time_offset_from_start_of_day < 0 || time_offset_from_start_of_day >= 24000) {
         Debug.setErrorMessage("willStormStartToday: time_offset out of bounds " + time_offset_from_start_of_day);
      }

      return this.willStormStart(this.getTotalWorldTimeAtStartOfToday() + (long)time_offset_from_start_of_day, this.getTotalWorldTimeAtEndOfToday());
   }

   public final boolean willPrecipitationStartToday() {
      return this.willPrecipitationStartToday(0);
   }

   public final boolean willStormStartToday() {
      return this.willStormStartToday(0);
   }

   public final boolean isPrecipitatingAt(long unadjusted_tick) {
      int day = this.getDayOfWorld();
      if (unadjusted_tick < getTotalWorldTimeAtStartOfDay(day - 1) || unadjusted_tick > getTotalWorldTimeAtEndOfDay(day)) {
         Debug.setErrorMessage("willBePrecipitatingAt: params out of bounds");
      }

      List events = this.getWeatherEventsForToday();
      Iterator i = events.iterator();

      WeatherEvent event;
      do {
         if (!i.hasNext()) {
            return false;
         }

         event = (WeatherEvent)i.next();
      } while(!event.isPrecipitatingAt(unadjusted_tick));

      return true;
   }

   public final boolean isStormingAt(long unadjusted_tick) {
      int day = this.getDayOfWorld();
      if (unadjusted_tick < getTotalWorldTimeAtStartOfDay(day - 1) || unadjusted_tick > getTotalWorldTimeAtEndOfDay(day)) {
         Debug.setErrorMessage("isStormingAt: params out of bounds");
      }

      List events = this.getWeatherEventsForToday();
      Iterator i = events.iterator();

      WeatherEvent event;
      do {
         if (!i.hasNext()) {
            return false;
         }

         event = (WeatherEvent)i.next();
      } while(!event.isStormingAt(unadjusted_tick));

      return true;
   }

   public final boolean isPrecipitatingTodayAt(int time_offset_from_start_of_day) {
      return this.isPrecipitatingAt(this.getTotalWorldTimeAtStartOfToday() + (long)time_offset_from_start_of_day);
   }

   public final boolean isStormingTodayAt(int time_offset_from_start_of_day) {
      return this.isStormingAt(this.getTotalWorldTimeAtStartOfToday() + (long)time_offset_from_start_of_day);
   }

   public final long getWorldCreationTime() {
      return this.worldInfo.getWorldCreationTime();
   }

   public final boolean isOpenPortal(int x, int y, int z) {
      Block block = this.getBlock(x, y, z);
      return block != null && block.isOpenPortal(this, x, y, z);
   }

   public void generateWeatherReport(int from_day, int to_day) {
      WeatherEvent.printWeatherEvents(addRandomWindAndStormsToWeatherEvents(mergeWeatherEvents(this.generateWeatherEvents(from_day, to_day))));
   }

   public final void updateTickFlags() {
      long total_world_time = this.getTotalWorldTime();
      if (this.tick_flags_last_updated != total_world_time) {
         this.total_time = total_world_time;
         if (this.isOverworld()) {
            this.updateWeatherFlags(total_world_time);
            this.updateMoonFlags(total_world_time);
         }

         this.tick_flags_last_updated = total_world_time;
      }
   }

   public final void updateWeatherFlags(long total_world_time) {
      if (!this.isOverworld()) {
         Minecraft.setErrorMessage("updateWeatherFlags: Why called for " + this.getDimensionName());
      } else {
         if (this.current_weather_event != null && !this.current_weather_event.isOccurringAt(total_world_time)) {
            this.current_weather_event = null;
         }

         if (this.current_weather_event == null) {
            this.current_weather_event = this.getWeatherEventAt(total_world_time, false, false);
         }

         if (this.current_weather_event == null) {
            this.is_precipitating = false;
            this.is_storming = false;
         } else {
            this.is_precipitating = true;
            this.is_storming = this.current_weather_event.isStormingAt(total_world_time);
         }

      }
   }

   public final void updateMoonFlags(long total_world_time) {
      if (!this.isOverworld()) {
         Minecraft.setErrorMessage("updateMoonFlags: Why called for " + this.getDimensionName());
      } else {
         boolean is_daytime = isDaytime(total_world_time);
         this.is_harvest_moon_24_hour_period = isHarvestMoon(total_world_time, false);
         this.is_harvest_moon_day = this.is_harvest_moon_24_hour_period && is_daytime;
         this.is_harvest_moon_night = this.is_harvest_moon_24_hour_period && !this.is_harvest_moon_day;
         this.is_blood_moon_24_hour_period = isBloodMoon(total_world_time, false);
         this.is_blood_moon_day = this.is_blood_moon_24_hour_period && is_daytime;
         this.is_blood_moon_night = this.is_blood_moon_24_hour_period && !this.is_blood_moon_day;
         this.is_blue_moon_24_hour_period = isBlueMoon(total_world_time, false);
         this.is_blue_moon_day = this.is_blue_moon_24_hour_period && is_daytime;
         this.is_blue_moon_night = this.is_blue_moon_24_hour_period && !this.is_blue_moon_day;
         this.is_moon_dog_24_hour_period = isMoonDog(total_world_time, false);
         this.is_moon_dog_day = this.is_moon_dog_24_hour_period && is_daytime;
         this.is_moon_dog_night = this.is_moon_dog_24_hour_period && !this.is_moon_dog_day;
      }
   }

   public final boolean isHarvestMoon24HourPeriod() {
      return this.is_harvest_moon_24_hour_period;
   }

   public final boolean isHarvestMoonDay() {
      return this.is_harvest_moon_day;
   }

   public final boolean isHarvestMoonNight() {
      return this.is_harvest_moon_night;
   }

   public final boolean isBloodMoon24HourPeriod() {
      return this.is_blood_moon_24_hour_period;
   }

   public final boolean isBloodMoonDay() {
      return this.is_blood_moon_day;
   }

   public final boolean isBloodMoonNight() {
      return this.is_blood_moon_night;
   }

   public final boolean isBlueMoon24HourPeriod() {
      return this.is_blue_moon_24_hour_period;
   }

   public final boolean isBlueMoonDay() {
      return this.is_blue_moon_day;
   }

   public final boolean isBlueMoonNight() {
      return this.is_blue_moon_night;
   }

   public final boolean isMoonDog24HourPeriod() {
      return this.is_moon_dog_24_hour_period;
   }

   public final boolean isMoonDogDay() {
      return this.is_moon_dog_day;
   }

   public final boolean isMoonDogNight() {
      return this.is_moon_dog_night;
   }

   public final void tryRemoveFromWorldUniques(ItemStack item_stack) {
      if (this.isRemote) {
         Minecraft.setErrorMessage("tryRemoveFromWorldUniques: called on client");
      } else {
         if (item_stack.hasSignature()) {
            this.worldInfo.removeSignature(item_stack.getSignature());
         }

      }
   }

   public boolean doesLavaFlowQuicklyInThisWorld() {
      return this.isTheNether();
   }

   public boolean isCeilingBedrock(int x, int y, int z) {
      return y > 31 && this.getBlockId(x, y, z) == Block.bedrock.blockID;
   }

   public final Block getBottomBlock() {
      return this.bottom_block;
   }

   public final int getBottomBlockMetadata() {
      return this.bottom_block_metadata;
   }

   public final boolean isBottomBlock(Block block, int metadata) {
      if (this.bottom_block == null) {
         return false;
      } else if (block != this.bottom_block) {
         return false;
      } else {
         return this.bottom_block_metadata < 0 || metadata == this.bottom_block_metadata;
      }
   }

   public final boolean isBottomBlock(int x, int y, int z) {
      return this.isBottomBlock(this.getBlock(x, y, z), this.getBlockMetadata(x, y, z));
   }
}
