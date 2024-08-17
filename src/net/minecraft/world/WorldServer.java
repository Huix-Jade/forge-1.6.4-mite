package net.minecraft.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakInfo;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockOperation;
import net.minecraft.block.BlockStem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.client.renderer.RNG;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBlackWidowSpider;
import net.minecraft.entity.EntityBlob;
import net.minecraft.entity.EntityBoneLord;
import net.minecraft.entity.EntityDemonSpider;
import net.minecraft.entity.EntityEarthElemental;
import net.minecraft.entity.EntityGhoul;
import net.minecraft.entity.EntityHellhound;
import net.minecraft.entity.EntityInfernalCreeper;
import net.minecraft.entity.EntityInvisibleStalker;
import net.minecraft.entity.EntityJelly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EntityLongdead;
import net.minecraft.entity.EntityNightwing;
import net.minecraft.entity.EntityOoze;
import net.minecraft.entity.EntityPhaseSpider;
import net.minecraft.entity.EntityPudding;
import net.minecraft.entity.EntityRevenant;
import net.minecraft.entity.EntityShadow;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityVampireBat;
import net.minecraft.entity.EntityWight;
import net.minecraft.entity.EntityWoodSpider;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.MapGenCaveNetwork;
import net.minecraft.network.ScheduledBlockChange;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Curse;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumBlockOperation;
import net.minecraft.util.EnumConsciousState;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.WorldEvent;

import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;

public class WorldServer extends World
{
   private final MinecraftServer mcServer;
   private final EntityTracker theEntityTracker;
   private final PlayerManager thePlayerManager;
   private Set pendingTickListEntriesHashSet;

   /** All work to do in future ticks. */
   private TreeSet pendingTickListEntriesTreeSet;
   public ChunkProviderServer theChunkProviderServer;

   /** set by CommandServerSave{all,Off,On} */
   public boolean canNotSave;
   private int updateEntityTick;

   /**
    * the teleporter to use when the entity is being transferred into the dimension
    */
   private final Teleporter worldTeleporter;
   private final SpawnerAnimals animalSpawner = new SpawnerAnimals();

   /**
    * Double buffer of ServerBlockEventList[] for holding pending BlockEventData's
    */
   private ServerBlockEventList[] blockEventCache = new ServerBlockEventList[] {new ServerBlockEventList((ServerBlockEvent)null), new ServerBlockEventList((ServerBlockEvent)null)};

   /**
    * The index into the blockEventCache; either 0, or 1, toggled in sendBlockEventPackets  where all BlockEvent are
    * applied locally and send to clients.
    */
   private int blockEventCacheIndex;
   public static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Item.stick.itemID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.planks.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.wood.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Item.hatchetFlint.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.hatchetFlint.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.axeCopper.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.pickaxeCopper.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.appleRed.itemID, 0, 2, 3, 5), new WeightedRandomChestContent(Item.bread.itemID, 0, 2, 3, 3)};
   private List pendingTickListEntriesThisTick = new ArrayList();

   /** An IntHashMap of entity IDs (integers) to their Entity objects. */
   private IntHashMap entityIdMap;
   private EntityLiving wake_causing_entity;
   public boolean fast_forwarding;
   public WorldMap world_map;
   private List scheduled_block_changes;
   public int decreased_hostile_mob_spawning_counter;
   public int increased_hostile_mob_spawning_counter;
   public int no_hostile_mob_spawning_counter;
   private int wm_value;
   private boolean wms_checked;
   private List queued_block_operations = new ArrayList();
   public int last_mob_spawn_limit_under_60;
   public int last_mob_spawn_limit_at_60_or_higher;

   /** Stores the recently processed (lighting) chunks */
   protected Set<ChunkCoordIntPair> doneChunks = new HashSet<ChunkCoordIntPair>();
   public List<Teleporter> customTeleporters = new ArrayList<Teleporter>();

   public WorldServer(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7ILogAgent)
   {
      super(par2ISaveHandler, par3Str, par5WorldSettings, WorldProvider.getProviderForDimension(par4), par6Profiler, par7ILogAgent);
      this.mcServer = par1MinecraftServer;
      this.theEntityTracker = new EntityTracker(this);
      this.thePlayerManager = new PlayerManager(this, par1MinecraftServer.getConfigurationManager().getViewDistance());

      if (this.entityIdMap == null)
      {
         this.entityIdMap = new IntHashMap();
      }

      if (this.pendingTickListEntriesHashSet == null)
      {
         this.pendingTickListEntriesHashSet = new HashSet();
      }

      if (this.pendingTickListEntriesTreeSet == null)
      {
         this.pendingTickListEntriesTreeSet = new TreeSet();
      }

      this.worldTeleporter = new Teleporter(this);
      this.worldScoreboard = new ServerScoreboard(par1MinecraftServer);
      ScoreboardSaveData var8 = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

      if (var8 == null)
      {
         var8 = new ScoreboardSaveData();
         this.mapStorage.setData("scoreboard", var8);
      }

      if (!(this instanceof WorldServerMulti)) //Forge: We fix the global mapStorage, which causes us to share scoreboards early. So don't associate the save data with the temporary scoreboard
      {
         var8.func_96499_a(this.worldScoreboard);
      }
      ((ServerScoreboard)this.worldScoreboard).func_96547_a(var8);
      DimensionManager.setWorld(par4, this);
   }

   public void sendPacketToAllPlayersInThisDimension(Packet packet)
   {
      Iterator i = this.playerEntities.iterator();

      while (i.hasNext())
      {
         EntityPlayerMP player_mp = (EntityPlayerMP)i.next();
         player_mp.playerNetServerHandler.sendPacketToPlayer(packet);
      }
   }

   public void sendPacketToAllNearbyPlayers(int x, int y, int z, Packet packet, double max_distance_sq)
   {
      Iterator i = this.playerEntities.iterator();

      while (i.hasNext())
      {
         EntityPlayerMP player_mp = (EntityPlayerMP)i.next();

         if (player_mp.getDistanceSqToBlock(x, y, z) <= max_distance_sq)
         {
            player_mp.playerNetServerHandler.sendPacketToPlayer(packet);
         }
      }
   }

   public void sendPacketToAllPlayersInAllDimensions(Packet packet)
   {
      MinecraftServer mc_server = MinecraftServer.getServer();

      for (int i = 0; i < mc_server.worldServers.length; ++i)
      {
         Iterator iterator = mc_server.worldServers[i].playerEntities.iterator();

         while (iterator.hasNext())
         {
            EntityPlayerMP player_mp = (EntityPlayerMP)iterator.next();
            player_mp.playerNetServerHandler.sendPacketToPlayer(packet);
         }
      }
   }

   private void signalPlayerToStartFallingAsleep(EntityPlayerMP player)
   {
      if (player.conscious_state != EnumConsciousState.falling_asleep && player.conscious_state != EnumConsciousState.sleeping)
      {
         player.conscious_state = EnumConsciousState.falling_asleep;
         player.playerNetServerHandler.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.start_falling_asleep));
      }
      else
      {
         Minecraft.setErrorMessage("signalPlayerToStartFallingAsleep: player is already sleeping or falling asleep");
      }
   }

   private void signalAllPlayersToStartFallingAsleep()
   {
      Iterator i = this.playerEntities.iterator();

      while (i.hasNext())
      {
         EntityPlayerMP player = (EntityPlayerMP)i.next();

         if (!player.isGhost() && !player.isZevimrgvInTournament() && (player.conscious_state == EnumConsciousState.waking_up || player.conscious_state == EnumConsciousState.fully_awake))
         {
            this.signalPlayerToStartFallingAsleep(player);
         }
      }
   }

   public boolean shouldTimeProgress()
   {
      boolean non_ghost_players_in_overworld_or_nether = this.mcServer.worldServerForDimension(-1).hasNonGhostPlayers() || this.mcServer.worldServerForDimension(0).hasNonGhostPlayers();
      boolean non_ghost_players_in_overworld_or_underworld_or_nether = non_ghost_players_in_overworld_or_nether || this.mcServer.getUnderworld().hasNonGhostPlayers();
      return this.mcServer.isDedicatedServer() ? (this.isUnderworld() ? this.worldInfo.getUnderworldHasBeenVisited() && non_ghost_players_in_overworld_or_underworld_or_nether : (!this.isTheNether() ? (this.isTheEnd() ? this.hasNonGhostPlayers() : true) : this.worldInfo.getNetherHasBeenVisited() && non_ghost_players_in_overworld_or_underworld_or_nether)) : (this.mcServer.hasOnlyGhostPlayersConnected() ? false : (this.isUnderworld() ? this.worldInfo.getUnderworldHasBeenVisited() && non_ghost_players_in_overworld_or_underworld_or_nether : (this.isTheNether() ? this.worldInfo.getNetherHasBeenVisited() && non_ghost_players_in_overworld_or_underworld_or_nether : (this.isTheEnd() ? this.hasNonGhostPlayers() : this.mcServer.hasNoPlayersOfAnyKindConnected() || non_ghost_players_in_overworld_or_underworld_or_nether))));
   }

   public boolean shouldRandomBlockTicksBePerformed()
   {
      return !this.shouldTimeProgress() ? false : (this.mcServer.isDedicatedServer() ? this.mcServer.hasNonGhostPlayersConnected(false) : this.mcServer.hasNonGhostPlayersConnected(false));
   }

   public boolean shouldTimeForwardingBeSkipped()
   {
      return !Main.no_time_forwarding && !DedicatedServer.isTournamentThatPreventsTimeForwarding() ? this.mcServer.isDedicatedServer() && this.isOverworld() : true;
   }

   /**
    * Runs a single tick for the world
    */
   public void tick()
   {
      if (this.no_hostile_mob_spawning_counter > 0 && Minecraft.inDevMode() && this.no_hostile_mob_spawning_counter % 200 == 0)
      {
         System.out.println("no_hostile_mob_spawning_counter=" + this.no_hostile_mob_spawning_counter);
      }

      this.worldInfo.setEarliestAllowableMITERelease(149);
      super.tick();

      if (!this.worldInfo.isValidMITEWorld())
      {
         MinecraftServer.setTreacheryDetected();
      }

      if (this.provider.dimensionId == 0 && this.mcServer.isServerSideMappingEnabled() && this.world_map == null)
      {
         this.world_map = new WorldMap(this);
      }

      if (this.world_map != null)
      {
         this.world_map.writeToFileProgressively(false);
      }

      this.checkCurses();

      if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3)
      {
         this.difficultySetting = 3;
      }

      this.provider.worldChunkMgr.cleanupCache();

      if (this.hasNonGhostPlayers())
      {
         boolean var1 = this.isBloodMoon(false) || DedicatedServer.isTournament();

         if (!var1 && this.allPlayersInBedOrDead() && (this.getAdjustedTimeOfDay() < getTimeOfSunrise() - 1000 || this.getAdjustedTimeOfDay() >= getTimeOfSleeping()))
         {
            if (this.allPlayersAsleepOrDead())
            {
               if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
               {
                  this.runSleepTicks(this.getTimeTillSunrise());
               }
               else
               {
                  this.wakeAllPlayersGently();
               }
            }
            else
            {
               this.signalAllPlayersToStartFallingAsleep();
            }
         }
         else
         {
            this.wakeAllPlayersGently();
         }
      }

      this.theProfiler.startSection("mobSpawner");

      if (this.getGameRules().getGameRuleBooleanValue("doMobSpawning"))
      {
         this.animalSpawner.performRandomLivingEntitySpawning(this);
      }

      this.theProfiler.endStartSection("chunkSource");
      this.chunkProvider.unloadQueuedChunks();
      this.tickBlocksInFastForward();
      this.checkScheduledBlockChanges(false);
      int var3 = this.calculateSkylightSubtracted(1.0F);

      if (var3 != this.skylightSubtracted)
      {
         this.skylightSubtracted = var3;
      }

      int var2 = this.shouldTimeProgress() ? 1 : 0;

      if (var2 > 0)
      {
         this.advanceTotalWorldTime((long)var2);
      }

      this.theProfiler.endStartSection("tickPending");
      this.tickUpdates(false);
      this.theProfiler.endStartSection("tickTiles");
      this.performQueuedBlockOperations();

      if (var2 <= 0)
      {
         this.setActivePlayerChunks();
      }
      else
      {
         this.tickBlocksAndAmbiance();
      }

      this.theProfiler.endStartSection("chunkMap");
      this.thePlayerManager.updatePlayerInstances();
      this.theProfiler.endStartSection("village");
      this.villageCollectionObj.tick();
      this.villageSiegeObj.tick();
      this.theProfiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
      for (Teleporter tele : customTeleporters)
      {
         tele.removeStalePortalLocations(getTotalWorldTime());
      }
      this.theProfiler.endSection();
      this.sendAndApplyBlockEvents();
   }

   private void performQueuedBlockOperations()
   {
      long total_world_time = this.getTotalWorldTime();
      Iterator i = this.queued_block_operations.iterator();

      while (i.hasNext())
      {
         BlockOperation block_operation = (BlockOperation)i.next();

         if (block_operation.tick <= total_world_time)
         {
            if (block_operation.tick == total_world_time)
            {
               block_operation.perform(this);
            }

            i.remove();
         }
      }
   }

   private void flushQueuedBlockOperations()
   {
      long total_world_time = this.getTotalWorldTime();
      Iterator i = this.queued_block_operations.iterator();

      while (i.hasNext())
      {
         BlockOperation block_operation = (BlockOperation)i.next();

         if (block_operation.isFlushedOnExit() && block_operation.tick >= total_world_time)
         {
            block_operation.perform(this);
            i.remove();
         }
      }
   }

   public boolean doesQueuedBlockOperationExist(int x, int y, int z, EnumBlockOperation type)
   {
      Iterator i = this.queued_block_operations.iterator();
      BlockOperation block_operation;

      do
      {
         if (!i.hasNext())
         {
            return false;
         }

         block_operation = (BlockOperation)i.next();
      }
      while (block_operation.x != x || block_operation.y != y || block_operation.z != z || block_operation.type != type);

      return true;
   }

   public EntityLiving tryCreateNewLivingEntityCloseTo(int x, int y, int z, int min_distance, int max_distance, Class entity_living_class, EnumCreatureType enum_creature_type)
   {
      boolean is_skeleton = entity_living_class == EntitySkeleton.class || entity_living_class == EntityLongdead.class;
      boolean can_spawn_in_shallow_water = is_skeleton;
      int min_distance_sq = min_distance * min_distance;
      int max_distance_sq = max_distance * max_distance;
      int random_number_index = this.rand.nextInt();

      for (int attempt = 0; attempt < 16; ++attempt)
      {
         ++random_number_index;
         int dx = RNG.int_max[random_number_index & 32767] % (max_distance * 2 + 1) - max_distance;
         ++random_number_index;
         int dy = RNG.int_7_minus_3[random_number_index & 32767];
         ++random_number_index;
         int dz = RNG.int_max[random_number_index & 32767] % (max_distance * 2 + 1) - max_distance;
         int trial_x = x + dx;
         int trial_y = y + dy;
         int trial_z = z + dz;
         int distance_sq;

         for (distance_sq = 0; distance_sq < 8 && this.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, can_spawn_in_shallow_water); ++distance_sq)
         {
            --trial_y;
         }

         for (distance_sq = 0; distance_sq < 8 && !this.isAirOrPassableBlock(trial_x, trial_y, trial_z, can_spawn_in_shallow_water); ++distance_sq)
         {
            ++trial_y;
         }

         double var26 = getDistanceSqFromDeltas((float)dx, (float)(trial_y - y), (float)dz);

         if (var26 >= (double)min_distance_sq && var26 <= (double)max_distance_sq && this.blockExists(trial_x, trial_y - 1, trial_z) && this.blockExists(trial_x, trial_y, trial_z) && this.blockExists(trial_x, trial_y + 1, trial_z) && !this.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, true) && this.isAirOrPassableBlock(trial_x, trial_y, trial_z, can_spawn_in_shallow_water) && this.isAirOrPassableBlock(trial_x, trial_y + 1, trial_z, false))
         {
            double[] resulting_y_pos = new double[1];

            if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(enum_creature_type, this, trial_x, trial_y, trial_z, false, resulting_y_pos))
            {
               EntityLiving entity_living;

               try
               {
                  entity_living = (EntityLiving)entity_living_class.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this});
               }
               catch (Exception var25)
               {
                  var25.printStackTrace();
                  continue;
               }

               entity_living.setLocationAndAngles((double)((float)trial_x + 0.5F), resulting_y_pos[0], (double)((float)trial_z + 0.5F), this.rand.nextFloat() * 360.0F, 0.0F);

               if (is_skeleton || entity_living.getCanSpawnHere(false))
               {
                  return entity_living;
               }
            }
         }
      }

      return null;
   }

   protected EntityLiving tryPlaceNewSuitableMob(int x, int y, int z, int min_distance_from_players, int max_distance_from_players)
   {
      int min_distance_sq_from_players = min_distance_from_players * min_distance_from_players;
      int max_distance_sq_from_players = max_distance_from_players * max_distance_from_players;
      int random_number_index = this.rand.nextInt();

      for (int attempt = 0; attempt < 16; ++attempt)
      {
         ++random_number_index;
         int dx = RNG.int_max[random_number_index & 32767] % (max_distance_from_players * 2 + 1) - max_distance_from_players;
         ++random_number_index;
         int dy = RNG.int_7_minus_3[random_number_index & 32767];
         ++random_number_index;
         int dz = RNG.int_max[random_number_index & 32767] % (max_distance_from_players * 2 + 1) - max_distance_from_players;
         int trial_x = x + dx;
         int trial_y = y + dy;
         int trial_z = z + dz;
         int distance_sq_to_nearest_player;

         for (distance_sq_to_nearest_player = 0; distance_sq_to_nearest_player < 8 && this.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, false); ++distance_sq_to_nearest_player)
         {
            --trial_y;
         }

         for (distance_sq_to_nearest_player = 0; distance_sq_to_nearest_player < 8 && !this.isAirOrPassableBlock(trial_x, trial_y, trial_z, false); ++distance_sq_to_nearest_player)
         {
            ++trial_y;
         }

         double var23 = this.getDistanceSqToNearestPlayer(trial_x, trial_y, trial_z);

         if (var23 >= (double)min_distance_sq_from_players && var23 <= (double)max_distance_sq_from_players && this.blockExists(trial_x, trial_y - 1, trial_z) && this.blockExists(trial_x, trial_y, trial_z) && this.blockExists(trial_x, trial_y + 1, trial_z) && !this.isAirOrPassableBlock(trial_x, trial_y - 1, trial_z, true) && this.isAirOrPassableBlock(trial_x, trial_y, trial_z, false) && this.isAirOrPassableBlock(trial_x, trial_y + 1, trial_z, false))
         {
            if (this.isOutdoors(trial_x, trial_y, trial_z))
            {
               if (this.getLightBrightness(trial_x, trial_y, trial_z) > 0.4F)
               {
                  continue;
               }
            }
            else if (this.getLightBrightness(trial_x, trial_y, trial_z) > 0.1F)
            {
               continue;
            }

            double[] resulting_y_pos = new double[1];

            if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, this, trial_x, trial_y, trial_z, false, resulting_y_pos))
            {
               Class suitable_creature_class = this.getSuitableCreature(EnumCreatureType.monster, trial_x, trial_y, trial_z);

               if (suitable_creature_class != null && suitable_creature_class != EntityEnderman.class && suitable_creature_class != EntitySlime.class && suitable_creature_class != EntityJelly.class && suitable_creature_class != EntityBlob.class && suitable_creature_class != EntityOoze.class && suitable_creature_class != EntityPudding.class)
               {
                  EntityLiving entity_living;

                  try
                  {
                     entity_living = (EntityLiving)suitable_creature_class.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this});
                  }
                  catch (Exception var22)
                  {
                     var22.printStackTrace();
                     continue;
                  }

                  entity_living.setLocationAndAngles((double)((float)trial_x + 0.5F), resulting_y_pos[0], (double)((float)trial_z + 0.5F), this.rand.nextFloat() * 360.0F, 0.0F);

                  if (entity_living.getCanSpawnHere(false))
                  {
                     return entity_living;
                  }
               }
            }
         }
      }

      return null;
   }

   public void spawnDecoy(Class entity_living_class, EntityPlayer player)
   {
      try
      {
         EntityLiving var33 = (EntityLiving)entity_living_class.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this});
         var33.setAsDecoy();
         var33.setLocationAndAngles(player.posX, player.posY + 2.0D, player.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
         var33.onSpawnWithEgg((EntityLivingData)null);
         this.spawnEntityInWorld(var33);
      }
      catch (Exception var4)
      {
         var4.printStackTrace();
      }
   }

   public EntityPlayer pathNearbyMobToRandomSleepingPlayer()
   {
      EntityPlayer player = this.getRandomNonGhostPlayer(true);

      if (player == null)
      {
         return null;
      }
      else
      {
         List mobs = null;
         int distance = 0;

         while (distance < 64)
         {
            distance += 16;
            mobs = this.getEntitiesWithinAABB(IMob.class, player.boundingBox.expand((double)distance, (double)(distance / 4), (double)distance));

            if (mobs.size() >= 16)
            {
               break;
            }
         }

         boolean[] tried = new boolean[mobs.size()];
         int attempts = Math.min(mobs.size(), 16);

         for (int attempt = 0; attempt < attempts; ++attempt)
         {
            int entity_index = this.rand.nextInt(mobs.size());

            if (!tried[entity_index])
            {
               EntityLiving entity_living = (EntityLiving)mobs.get(entity_index);

               if (entity_living.ticksExisted > 0 && entity_living instanceof IMob && !(entity_living instanceof EntityEnderman) && this.tryPathMobToSleepingPlayer(entity_living, player, distance, true))
               {
                  return player;
               }

               tried[entity_index] = true;
            }
         }

         return null;
      }
   }

   public boolean tryPathMobToSleepingPlayer(EntityLiving entity_living, EntityPlayer player, int max_path_length, boolean sync_last_tick_pos_on_next_update)
   {
      PathNavigate navigator = entity_living.getNavigator();
      PathEntity path = this.getEntityPathToXYZ(entity_living, player.bed_location.posX, player.bed_location.posY, player.bed_location.posZ, (float)max_path_length, navigator.canPassOpenWoodenDoors, false, navigator.avoidsWater, navigator.canSwim);

      if (path == null)
      {
         return false;
      }
      else
      {
         PathPoint final_point = path.getFinalPathPoint();

         if (getDistanceSqFromDeltas((float)(final_point.xCoord - player.bed_location.posX), (float)(final_point.yCoord - player.bed_location.posY), (float)(final_point.zCoord - player.bed_location.posZ)) > 2.0D)
         {
            return false;
         }
         else
         {
            PathPoint path_point = null;
            int path_point_index = Math.max(path.getCurrentPathLength() - 8, 0);
            Vec3 player_pos = this.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
            Vec3 entity_eye_pos = this.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);

            for (int block_below = path_point_index; block_below < path.getCurrentPathLength(); ++block_below)
            {
               path_point = path.getPathPointFromIndex(block_below);
               player_pos.setComponents((double)((float)player.bed_location.posX + 0.5F), (double)((float)player.bed_location.posY + 1.5F), (double)((float)player.bed_location.posZ + 0.5F));
               entity_eye_pos.setComponents((double)((float)path_point.xCoord + 0.5F), (double)((float)path_point.yCoord + entity_living.getEyeHeight()), (double)((float)path_point.zCoord + 0.5F));
               boolean seen = this.checkForLineOfSight(entity_eye_pos, player_pos, false);

               if (seen)
               {
                  break;
               }

               path_point = null;
            }

            if (path_point == null)
            {
               path_point = path.getPathPointFromIndex(path_point_index);
            }

            entity_living.sync_last_tick_pos_on_next_update = sync_last_tick_pos_on_next_update;
            Block var14 = this.getBlock(path_point.xCoord, path_point.yCoord - 1, path_point.zCoord);

            if (var14 == null)
            {
               entity_living.setPosition((double)((float)path_point.xCoord + 0.5F), (double)path_point.yCoord, (double)((float)path_point.zCoord + 0.5F));
            }
            else
            {
               var14.setBlockBoundsBasedOnStateAndNeighbors(this, path_point.xCoord, path_point.yCoord - 1, path_point.zCoord);
               entity_living.setPosition((double)((float)path_point.xCoord + 0.5F), (double)(path_point.yCoord - 1) + var14.maxY[Minecraft.getThreadIndex()], (double)((float)path_point.zCoord + 0.5F));
            }

            this.wake_causing_entity = entity_living;
            return true;
         }
      }
   }

   public boolean runSleepTicks(int ticks)
   {
      int ticks_slept = 0;
      boolean player_woke_up;
      Iterator i;
      EntityPlayerMP player;

      for (player_woke_up = false; ticks_slept < ticks && !player_woke_up; ++ticks_slept)
      {
         i = this.playerEntities.iterator();

         while (i.hasNext())
         {
            player = (EntityPlayerMP)i.next();

            if (!player.isGhost() && !player.isZevimrgvInTournament() && player.getHealth() > 0.0F)
            {
               player.foodStats.onUpdate(player);

               if (!player.hasFoodEnergy())
               {
                  player.wakeUpPlayer(true, (Entity)null);
                  player.addChatMessage("tile.bed.wakeHungry");
                  player_woke_up = true;
               }
               else if (!player.isSleeping())
               {
                  player_woke_up = true;
               }
            }
         }

         if (!player_woke_up && this.rand.nextInt(1000) == 0)
         {
            EntityPlayer var8 = this.pathNearbyMobToRandomSleepingPlayer();

            if (var8 == null)
            {
               EntityPlayer player1 = this.getRandomNonGhostPlayer(true);

               if (player1 != null)
               {
                  EntityLiving entity_living = this.tryPlaceNewSuitableMob(MathHelper.floor_double(player1.posX), MathHelper.floor_double(player1.posY), MathHelper.floor_double(player1.posZ), 8, 48);

                  if (entity_living != null)
                  {
                     entity_living.refreshDespawnCounter(-2400);

                     if (this.tryPathMobToSleepingPlayer(entity_living, player1, 32, false))
                     {
                        var8 = player1;
                     }

                     if (var8 != null || this.rand.nextInt(2) == 0)
                     {
                        entity_living.onSpawnWithEgg((EntityLivingData)null);
                        this.spawnEntityInWorld(entity_living);
                     }
                  }
               }
            }

            if (var8 != null)
            {
               this.wake_causing_entity.extinguish();
               var8.wakeUpPlayer(true, this.wake_causing_entity);
               var8.addChatMessage("tile.bed.wakeMobs");
               player_woke_up = true;
            }
         }
      }

      if (ticks_slept == ticks && !player_woke_up && ticks_slept >= 6000)
      {
         i = this.playerEntities.iterator();

         while (i.hasNext())
         {
            player = (EntityPlayerMP)i.next();

            if (!player.isGhost() && !player.isZevimrgvInTournament() && player.getHealth() > 0.0F && player.inBed())
            {
               player.triggerAchievement(AchievementList.wellRested);
            }
         }
      }

      this.advanceTotalWorldTime((long)ticks_slept);

      if (ticks_slept >= 1000)
      {
         i = this.playerEntities.iterator();

         while (i.hasNext())
         {
            player = (EntityPlayerMP)i.next();

            if (!player.isGhost() && !player.isZevimrgvInTournament() && player.getHealth() > 0.0F && player.inBed())
            {
               player.clearActivePotions();
            }
         }
      }

      this._calculateInitialWeather();
      return ticks_slept == ticks;
   }

   public Class getSuitableCreature(EnumCreatureType creature_type, int x, int y, int z)
   {
      boolean check_depth = this.isOverworld();
      boolean is_blood_moon_up = this.isBloodMoon(true);
      boolean is_freezing_biome = this.getBiomeGenForCoords(x, z).isFreezing();
      boolean is_desert_biome = this.getBiomeGenForCoords(x, z).isDesertBiome();
      boolean can_spawn_ghouls_on_surface = is_blood_moon_up;
      boolean can_spawn_wights_on_surface = is_blood_moon_up && is_freezing_biome;
      boolean can_spawn_shadows_on_surface = is_blood_moon_up && is_desert_biome;
      boolean can_spawn_revenants_on_surface = is_blood_moon_up;
      boolean can_spawn_bone_lords_on_surface = is_blood_moon_up;

      for (int attempt = 0; attempt < 16; ++attempt)
      {
         List possible_creatures = this.getChunkProvider().getPossibleCreatures(creature_type, x, y, z);
         possible_creatures = ForgeEventFactory.getPotentialSpawns(this, creature_type, x, y, z, possible_creatures);
         if (possible_creatures == null || possible_creatures.isEmpty())
         {
            return null;
         }

         SpawnListEntry entry = (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, possible_creatures);
         Class entity_class = entry.entityClass;

         if (entity_class == EntityCreeper.class)
         {
            if (!this.hasSkylight() || this.isDaytime() || this.rand.nextInt(4) == 0 || !this.isOutdoors(x, y, z))
            {
               if (this.rand.nextInt(40) >= y && this.rand.nextFloat() < 0.5F)
               {
                  return EntityInfernalCreeper.class;
               }

               return entity_class;
            }
         }
         else if (entity_class == EntitySlime.class)
         {
            if (!this.blockTypeIsAbove(Block.stone, x, y, z))
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityGhoul.class)
         {
            if (!check_depth || y <= 56 || can_spawn_ghouls_on_surface)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityJelly.class)
         {
            if (this.blockTypeIsAbove(Block.stone, x, y, z))
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityWight.class)
         {
            if (!check_depth || y <= 48 || can_spawn_wights_on_surface)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityVampireBat.class)
         {
            if (!check_depth || y <= 48 || is_blood_moon_up)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityRevenant.class)
         {
            if (!check_depth || y <= 44 || can_spawn_revenants_on_surface)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityInvisibleStalker.class)
         {
            if (!check_depth || y <= 40)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityEarthElemental.class)
         {
            if (!check_depth || y <= 40)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityBlob.class)
         {
            if ((!check_depth || y <= 40) && this.blockTypeIsAbove(Block.stone, x, y, z))
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityOoze.class)
         {
            if ((!check_depth || y <= 32) && this.getBlock(x, y - 1, z) == Block.stone && this.blockTypeIsAbove(Block.stone, x, y, z))
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityNightwing.class)
         {
            if (!check_depth || y <= 32 || is_blood_moon_up)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityBoneLord.class)
         {
            if (!check_depth || y <= 32 || can_spawn_bone_lords_on_surface)
            {
               return entity_class;
            }
         }
         else if (entity_class == EntityPudding.class)
         {
            if ((!check_depth || y <= 24) && this.getBlock(x, y - 1, z) == Block.stone && this.blockTypeIsAbove(Block.stone, x, y, z))
            {
               return entity_class;
            }
         }
         else if (entity_class != EntityDemonSpider.class && entity_class != EntityPhaseSpider.class)
         {
            if (entity_class == EntityHellhound.class)
            {
               if (!check_depth || y <= 32)
               {
                  return entity_class;
               }
            }
            else if (entity_class == EntityShadow.class)
            {
               if (!check_depth || y <= 32 || can_spawn_shadows_on_surface)
               {
                  return entity_class;
               }
            }
            else if (entity_class == EntitySpider.class)
            {
               if (!this.hasSkylight() || this.rand.nextInt(4) != 0 || !this.isOutdoors(x, y, z))
               {
                  return entity_class;
               }
            }
            else if (entity_class == EntityWoodSpider.class)
            {
               if ((this.canBlockSeeTheSky(x, y, z) || this.blockTypeIsAbove(Block.leaves, x, y, z) || this.blockTypeIsAbove(Block.wood, x, y, z)) && this.blockTypeIsNearTo(Block.wood.blockID, x, y, z, 5, 2) && this.blockTypeIsNearTo(Block.leaves.blockID, x, y + 5, z, 5, 5))
               {
                  return entity_class;
               }
            }
            else
            {
               if (entity_class != EntityBlackWidowSpider.class)
               {
                  if (entity_class == EntityGhast.class)
                  {
                     Iterator i = this.loadedEntityList.iterator();

                     while (i.hasNext())
                     {
                        Entity entity = (Entity)i.next();

                        if (entity instanceof EntityGhast && entity.getDistanceSqToBlock(x, y, z) < 2304.0D && this.rand.nextFloat() < 0.8F)
                        {
                           entity_class = null;
                        }
                     }
                  }

                  return entity_class;
               }

               if (this.rand.nextFloat() >= 0.5F)
               {
                  return entity_class;
               }
            }
         }
         else if (!check_depth || y <= 32)
         {
            return entity_class;
         }
      }

      return null;
   }

   private boolean allPlayersInBedOrDead()
   {
      if (!this.hasNonGhostPlayers())
      {
         return false;
      }
      else
      {
         boolean at_least_one_player_in_bed = false;
         Iterator i = this.playerEntities.iterator();

         while (i.hasNext())
         {
            EntityPlayer player = (EntityPlayer)i.next();

            if (!player.isGhost() && !player.isZevimrgvInTournament() && player.getHealth() > 0.0F)
            {
               if (!player.inBed())
               {
                  return false;
               }

               at_least_one_player_in_bed = true;
            }
         }

         return at_least_one_player_in_bed;
      }
   }

   protected void wakeAllPlayersGently()
   {
      Iterator i = this.playerEntities.iterator();

      while (i.hasNext())
      {
         EntityPlayerMP player = (EntityPlayerMP)i.next();

         if (!player.isGhost() && !player.isZevimrgvInTournament() && (player.conscious_state == EnumConsciousState.falling_asleep || player.conscious_state == EnumConsciousState.sleeping))
         {
            player.wakeUpPlayer(false, (Entity)null);
         }
      }
   }

   public boolean allPlayersAsleepOrDead()
   {
      if (!this.hasNonGhostPlayers())
      {
         return false;
      }
      else
      {
         boolean at_least_one_player_asleep = false;
         Iterator i = this.playerEntities.iterator();

         while (i.hasNext())
         {
            EntityPlayer player = (EntityPlayer)i.next();

            if (!player.isGhost() && !player.isZevimrgvInTournament() && player.getHealth() > 0.0F)
            {
               if (!player.isSleeping())
               {
                  return false;
               }

               at_least_one_player_asleep = true;
            }
         }

         return at_least_one_player_asleep;
      }
   }

   /**
    * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
    */
   public void setSpawnLocation()
   {
      if (this.worldInfo.getSpawnY() <= 0)
      {
         this.worldInfo.setSpawnY(64);
      }

      int var1 = this.worldInfo.getSpawnX();
      int var2 = this.worldInfo.getSpawnZ();
      int var3 = 0;

      while (this.getFirstUncoveredBlock(var1, var2) == 0)
      {
         var1 += this.rand.nextInt(8) - this.rand.nextInt(8);
         var2 += this.rand.nextInt(8) - this.rand.nextInt(8);
         ++var3;

         if (var3 == 10000)
         {
            break;
         }
      }

      this.worldInfo.setSpawnX(var1);
      this.worldInfo.setSpawnZ(var2);
   }

   protected void tickBlocksInFastForward(Chunk chunk)
   {
      long current_total_world_time = this.getTotalWorldTime();

      if (chunk.last_total_world_time != 0L && chunk.last_total_world_time < current_total_world_time && !this.shouldTimeForwardingBeSkipped())
      {
         if (current_total_world_time - chunk.last_total_world_time > 768000L)
         {
            chunk.last_total_world_time = current_total_world_time - 768000L;
         }

         int delta_total_world_time = (int)(chunk.last_total_world_time - current_total_world_time);

         if (delta_total_world_time <= -600)
         {
            chunk.should_be_saved_once_time_forwarding_is_completed = true;
         }

         boolean simulate_day_cycles = this.provider.dimensionId == 0 && delta_total_world_time <= -2000;
         int saved_skylight_subtracted = this.skylightSubtracted;
         float saved_prev_raining_strength = this.prevRainingStrength;
         float saved_raining_strength = this.rainingStrength;
         float saved_prev_thundering_strength = this.prevThunderingStrength;
         float saved_thundering_strength = this.thunderingStrength;
         int var5 = chunk.xPosition * 16;
         int var6 = chunk.zPosition * 16;
         int distance_sq_from_nearest_player = 100;
         Iterator iterator = this.playerEntities.iterator();
         int total_ticks_to_fast_forward;
         int remaining_ticks_to_fast_forward;
         int ticks_to_run;

         while (iterator.hasNext())
         {
            EntityPlayer max_ticks_to_fast_forward_this_tick = (EntityPlayer)iterator.next();
            total_ticks_to_fast_forward = chunk.xPosition - max_ticks_to_fast_forward_this_tick.getChunkPosX();
            remaining_ticks_to_fast_forward = chunk.zPosition - max_ticks_to_fast_forward_this_tick.getChunkPosZ();
            ticks_to_run = total_ticks_to_fast_forward * total_ticks_to_fast_forward + remaining_ticks_to_fast_forward * remaining_ticks_to_fast_forward;

            if (ticks_to_run < distance_sq_from_nearest_player)
            {
               distance_sq_from_nearest_player = ticks_to_run;
            }
         }

         int var32 = distance_sq_from_nearest_player <= 2 ? 8000 : (distance_sq_from_nearest_player <= 5 ? 4000 : (distance_sq_from_nearest_player <= 10 ? 2000 : (distance_sq_from_nearest_player <= 20 ? 1000 : (distance_sq_from_nearest_player <= 29 ? 500 : 250))));
         total_ticks_to_fast_forward = Math.min(-delta_total_world_time, var32);

         for (remaining_ticks_to_fast_forward = total_ticks_to_fast_forward; remaining_ticks_to_fast_forward > 0; remaining_ticks_to_fast_forward -= ticks_to_run)
         {
            ticks_to_run = simulate_day_cycles ? Math.min(remaining_ticks_to_fast_forward, 4000) : remaining_ticks_to_fast_forward;

            if (simulate_day_cycles)
            {
               this.setTotalWorldTime(chunk.last_total_world_time);
               WeatherEvent var19 = this.getCurrentWeatherEvent();

               if (var19 == null)
               {
                  this.prevRainingStrength = 0.0F;
                  this.rainingStrength = 0.0F;
                  this.prevThunderingStrength = 0.0F;
                  this.thunderingStrength = 0.0F;
               }
               else
               {
                  this.prevRainingStrength = 1.0F;
                  this.rainingStrength = 1.0F;

                  if (var19.isStormingAt(chunk.last_total_world_time))
                  {
                     this.prevThunderingStrength = 1.0F;
                     this.thunderingStrength = 1.0F;
                  }
               }

               this.skylightSubtracted = this.calculateSkylightSubtracted(1.0F);
               chunk.updateSkylight(false);
            }

            ExtendedBlockStorage[] var33 = chunk.getBlockStorageArray();
            int var9 = var33.length;

            for (int i = 0; i < ticks_to_run; ++i)
            {
               int var10;
               int y_location;
               int var20;
               int var14;

               if (RNG.chance_in_16[++RNG.random_number_index & 32767])
               {
                  this.updateLCG = this.updateLCG * 3 + 1013904223;
                  var10 = this.updateLCG >> 2;
                  int var21 = var10 & 15;
                  y_location = var10 >> 8 & 15;
                  var20 = this.getPrecipitationHeight(var21 + var5, y_location + var6);

                  if (this.isBlockFreezableNaturally(var21 + var5, var20 - 1, y_location + var6))
                  {
                     this.setBlock(var21 + var5, var20 - 1, y_location + var6, Block.ice.blockID);
                  }

                  if (this.isPrecipitating(false))
                  {
                     if (this.canSnowAt(var21 + var5, var20, y_location + var6))
                     {
                        this.placeSnowfallAt(var21 + var5, var20, y_location + var6);
                     }

                     BiomeGenBase var13 = this.getBiomeGenForCoords(var21 + var5, y_location + var6);

                     if (var13.canSpawnLightningBolt(this.isBloodMoon24HourPeriod()))
                     {
                        var14 = this.getBlockId(var21 + var5, var20 - 1, y_location + var6);

                        if (var14 != 0)
                        {
                           Block.blocksList[var14].fillWithRain(this, var21 + var5, var20 - 1, y_location + var6);
                        }
                     }
                  }
               }

               for (var10 = 0; var10 < var9; ++var10)
               {
                  ExtendedBlockStorage var34 = var33[var10];

                  if (var34 != null && var34.getNeedsRandomTick())
                  {
                     y_location = var34.getYLocation();

                     for (var20 = 0; var20 < 3; ++var20)
                     {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int var35 = this.updateLCG >> 2;
                        var14 = var35 & 15;
                        int var15 = var35 >> 8 & 15;
                        int var16 = var35 >> 16 & 15;
                        int var17 = var34.getExtBlockID(var14, var16, var15);
                        Block var18 = Block.blocksList[var17];

                        if (var18 != null && var18.getTickRandomly())
                        {
                           var18.updateTick(this, var14 + var5, var16 + y_location, var15 + var6, this.rand);
                        }
                     }
                  }
               }
            }

            chunk.last_total_world_time += (long)ticks_to_run;
         }

         if (chunk.last_total_world_time > current_total_world_time)
         {
            Minecraft.setErrorMessage("Fast forwarding error: chunk time (" + chunk.last_total_world_time + ") greater than world time (" + current_total_world_time + ")");
         }

         if (simulate_day_cycles)
         {
            this.setTotalWorldTime(current_total_world_time);
            this.skylightSubtracted = saved_skylight_subtracted;
            this.prevRainingStrength = saved_prev_raining_strength;
            this.rainingStrength = saved_raining_strength;
            this.prevThunderingStrength = saved_prev_thundering_strength;
            this.thunderingStrength = saved_thundering_strength;
         }
      }
      else
      {
         chunk.last_total_world_time = current_total_world_time;
      }
   }

   protected void tickBlocksInFastForward()
   {
      if (!DedicatedServer.isTournamentThatPreventsTimeForwarding())
      {
         float server_load = this.mcServer.getLoadOnServer();
         this.fast_forwarding = true;
         Iterator var3 = this.activeChunkSet.iterator();

         if (server_load < 0.5F)
         {
            while (var3.hasNext())
            {
               ChunkCoordIntPair tick_of_day = (ChunkCoordIntPair)var3.next();
               Chunk modulus = this.getChunkFromChunkCoords(tick_of_day.chunkXPos, tick_of_day.chunkZPos);
               this.tickBlocksInFastForward(modulus);
            }
         }
         else
         {
            int var71 = this.getTimeOfDay();
            int var8 = (int)((server_load - 0.29F) * 10.0F);

            while (var3.hasNext())
            {
               ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
               ++var71;

               if (var71 % var8 == 0)
               {
                  Chunk var7 = this.getChunkFromChunkCoords(var4.chunkXPos, var4.chunkZPos);
                  this.tickBlocksInFastForward(var7);
               }
            }
         }

         if (this.worldInfo.getEarliestMITEReleaseRunIn() < 105)
         {
            this.mcServer.initiateShutdown();
         }

         this.fast_forwarding = false;
      }
   }

   public boolean placeSnowfallAt(int x, int y, int z)
   {
      Block block = this.getBlock(x, y, z);

      if (block instanceof BlockCrops || block instanceof BlockStem)
      {
         block.dropBlockAsEntityItem((new BlockBreakInfo(this, x, y, z)).setSnowedUpon());
      }

      return this.setBlock(x, y, z, Block.snow.blockID);
   }

   /**
    * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
    * player
    */
   protected void tickBlocksAndAmbiance()
   {
      super.tickBlocksAndAmbiance();
      int var1 = 0;
      int var2 = 0;
      Iterator var3 = this.activeChunkSet.iterator();

      doneChunks.retainAll(activeChunkSet);
      if (doneChunks.size() == activeChunkSet.size())
      {
         doneChunks.clear();
      }

      final long startTime = System.nanoTime();

      boolean var4 = this.shouldRandomBlockTicksBePerformed();
      boolean var5 = this.isBloodMoon24HourPeriod();

      for (int var6 = var5 ? 20000 : 100000; var3.hasNext(); this.theProfiler.endSection())
      {
         ChunkCoordIntPair var7 = (ChunkCoordIntPair)var3.next();
         int var8 = var7.chunkXPos * 16;
         int var9 = var7.chunkZPos * 16;
         this.theProfiler.startSection("getChunk");
         Chunk var10 = this.getChunkFromChunkCoords(var7.chunkXPos, var7.chunkZPos);
         this.moodSoundAndLightCheck(var8, var9, var10);
         this.theProfiler.endStartSection("tickChunk");
//         var10.updateSkylight(false);
         //Limits and evenly distributes the lighting update time
         if (System.nanoTime() - startTime <= 4000000 && doneChunks.add(var7))
         {
            var10.updateSkylight(false);
         }

         var10.performPendingSandFallsIfPossible();
         this.theProfiler.endStartSection("thunder");
         int var11;
         int var12;
         int var13;
         int var14;

         if (provider.canDoLightning(var10) && this.rand.nextInt(var6) == 0 && this.isPrecipitating(true) && this.isThundering(true))
         {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            var11 = this.updateLCG >> 2;
            var12 = var8 + (var11 & 15);
            var13 = var9 + (var11 >> 8 & 15);
            var14 = this.getPrecipitationHeight(var12, var13);

            if (this.canLightningStrikeAt(var12, var14, var13))
            {
               this.addWeatherEffect(new EntityLightningBolt(this, (double)var12, (double)var14, (double)var13));
            }
         }

         this.theProfiler.endStartSection("iceandsnow");
         int var15;

         if (provider.canDoRainSnowIce(var10) && this.rand.nextInt(16) == 0)
         {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            var11 = this.updateLCG >> 2;
            var12 = var11 & 15;
            var13 = var11 >> 8 & 15;
            var14 = this.getPrecipitationHeight(var12 + var8, var13 + var9);

            if (this.isBlockFreezableNaturally(var12 + var8, var14 - 1, var13 + var9))
            {
               this.setBlock(var12 + var8, var14 - 1, var13 + var9, Block.ice.blockID);
            }

            if (this.isPrecipitating(true) && this.canSnowAt(var12 + var8, var14, var13 + var9))
            {
               this.placeSnowfallAt(var12 + var8, var14, var13 + var9);
            }

            if (this.isPrecipitating(true))
            {
               BiomeGenBase var16 = this.getBiomeGenForCoords(var12 + var8, var13 + var9);

               if (var16.canSpawnLightningBolt(var5))
               {
                  var15 = this.getBlockId(var12 + var8, var14 - 1, var13 + var9);

                  if (var15 != 0)
                  {
                     Block.blocksList[var15].fillWithRain(this, var12 + var8, var14 - 1, var13 + var9);
                  }
               }
            }
         }

         this.theProfiler.endStartSection("tickTiles");
         ExtendedBlockStorage[] var25 = var10.getBlockStorageArray();
         var12 = var25.length;

         for (var13 = 0; var13 < var12; ++var13)
         {
            ExtendedBlockStorage var17 = var25[var13];

            if (var17 != null && var17.getNeedsRandomTick())
            {
               int var18 = var17.getYLocation();

               for (int var19 = 0; var19 < 3; ++var19)
               {
                  this.updateLCG = this.updateLCG * 3 + 1013904223;
                  var15 = this.updateLCG >> 2;
                  int var20 = var15 & 15;
                  int var21 = var15 >> 8 & 15;
                  int var22 = var15 >> 16 & 15;
                  int var23 = var17.getExtBlockID(var20, var22, var21);
                  ++var2;
                  Block var24 = Block.blocksList[var23];

                  if (var24 != null && var24.getTickRandomly())
                  {
                     ++var1;

                     if (var4)
                     {
                        var24.updateTick(this, var20 + var8, var22 + var18, var21 + var9, this.rand);
                     }
                  }
               }
            }
         }

         if (var10.last_total_world_time == 0L)
         {
            var10.last_total_world_time = this.getTotalWorldTime();
         }
         else
         {
            ++var10.last_total_world_time;
         }
      }
   }

   /**
    * Returns true if the given block will receive a scheduled tick in this tick. Args: X, Y, Z, blockID
    */
   public boolean isBlockTickScheduledThisTick(int par1, int par2, int par3, int par4)
   {
      NextTickListEntry var5 = new NextTickListEntry(par1, par2, par3, par4);
      return this.pendingTickListEntriesThisTick.contains(var5);
   }

   /**
    * Schedules a tick to a block with a delay (Most commonly the tick rate)
    */
   public void scheduleBlockUpdate(int par1, int par2, int par3, int par4, int par5)
   {
      this.scheduleBlockUpdateWithPriority(par1, par2, par3, par4, par5, 0);
   }

   public void scheduleBlockUpdateWithPriority(int par1, int par2, int par3, int par4, int par5, int par6)
   {
      NextTickListEntry var7 = new NextTickListEntry(par1, par2, par3, par4);
      byte var8 = 0;

      if (this.scheduledUpdatesAreImmediate && par4 > 0)
      {
         if (Block.blocksList[par4].func_82506_l())
         {
            var8 = 8;

            if (this.checkChunksExist(var7.xCoord - var8, var7.yCoord - var8, var7.zCoord - var8, var7.xCoord + var8, var7.yCoord + var8, var7.zCoord + var8))
            {
               int var9 = this.getBlockId(var7.xCoord, var7.yCoord, var7.zCoord);

               if (var9 == var7.blockID && var9 > 0)
               {
                  Block.blocksList[var9].updateTick(this, var7.xCoord, var7.yCoord, var7.zCoord, this.rand);
               }
            }

            return;
         }

         par5 = 1;
      }

      if (this.checkChunksExist(par1 - var8, par2 - var8, par3 - var8, par1 + var8, par2 + var8, par3 + var8))
      {
         if (par4 > 0)
         {
            var7.setScheduledTime((long)par5 + this.getTotalWorldTime());
            var7.setPriority(par6);
         }

         if (!this.pendingTickListEntriesHashSet.contains(var7))
         {
            this.pendingTickListEntriesHashSet.add(var7);
            this.pendingTickListEntriesTreeSet.add(var7);
         }
      }
   }

   /**
    * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
    */
   public void scheduleBlockUpdateFromLoad(int par1, int par2, int par3, int par4, int par5, int par6)
   {
      NextTickListEntry var7 = new NextTickListEntry(par1, par2, par3, par4);
      var7.setPriority(par6);

      if (par4 > 0)
      {
         var7.setScheduledTime((long)par5 + this.getTotalWorldTime());
      }

      if (!this.pendingTickListEntriesHashSet.contains(var7))
      {
         this.pendingTickListEntriesHashSet.add(var7);
         this.pendingTickListEntriesTreeSet.add(var7);
      }
   }

   /**
    * Updates (and cleans up) entities and tile entities
    */
   public void updateEntities()
   {
      if (this.playerEntities.isEmpty() && getPersistentChunks().isEmpty())
      {
         if (this.updateEntityTick++ >= 1200)
         {
            return;
         }
      }
      else
      {
         this.resetUpdateEntityTick();
      }

      if (!this.allPlayersAsleepOrDead())
      {
         Iterator var1 = this.playerEntities.iterator();

         while (var1.hasNext())
         {
            EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
            var2.updateRespawnCountdown();
         }
      }

      super.updateEntities();
   }

   /**
    * Resets the updateEntityTick field to 0
    */
   public void resetUpdateEntityTick()
   {
      this.updateEntityTick = 0;
   }

   /**
    * Runs through the list of updates to run and ticks them
    */
   public boolean tickUpdates(boolean par1)
   {
      int var2 = this.pendingTickListEntriesTreeSet.size();

      if (var2 != this.pendingTickListEntriesHashSet.size())
      {
         throw new IllegalStateException("TickNextTick list out of synch");
      }
      else
      {
         if (var2 > 1000)
         {
            var2 = 1000;
         }

         this.theProfiler.startSection("cleaning");
         NextTickListEntry var3;

         for (int var4 = 0; var4 < var2; ++var4)
         {
            var3 = (NextTickListEntry)this.pendingTickListEntriesTreeSet.first();

            if (!par1 && var3.scheduledTime > this.getTotalWorldTime())
            {
               break;
            }

            this.pendingTickListEntriesTreeSet.remove(var3);
            this.pendingTickListEntriesHashSet.remove(var3);
            this.pendingTickListEntriesThisTick.add(var3);
         }

         this.theProfiler.endSection();
         this.theProfiler.startSection("ticking");
         Iterator var14 = this.pendingTickListEntriesThisTick.iterator();

         while (var14.hasNext())
         {
            var3 = (NextTickListEntry)var14.next();
            var14.remove();
            byte var5 = 0;

            if (this.checkChunksExist(var3.xCoord - var5, var3.yCoord - var5, var3.zCoord - var5, var3.xCoord + var5, var3.yCoord + var5, var3.zCoord + var5))
            {
               int var6 = this.getBlockId(var3.xCoord, var3.yCoord, var3.zCoord);

               if (var6 > 0 && Block.isAssociatedBlockID(var6, var3.blockID))
               {
                  try
                  {
                     Block.blocksList[var6].updateTick(this, var3.xCoord, var3.yCoord, var3.zCoord, this.rand);
                  }
                  catch (Throwable var13)
                  {
                     CrashReport var8 = CrashReport.makeCrashReport(var13, "Exception while ticking a block");
                     CrashReportCategory var9 = var8.makeCategory("Block being ticked");
                     int var10;

                     try
                     {
                        var10 = this.getBlockMetadata(var3.xCoord, var3.yCoord, var3.zCoord);
                     }
                     catch (Throwable var12)
                     {
                        var10 = -1;
                     }

                     CrashReportCategory.addBlockCrashInfo(var9, var3.xCoord, var3.yCoord, var3.zCoord, var6, var10);
                     throw new ReportedException(var8);
                  }
               }
            }
            else
            {
               this.scheduleBlockUpdate(var3.xCoord, var3.yCoord, var3.zCoord, var3.blockID, 0);
            }
         }

         this.theProfiler.endSection();
         this.pendingTickListEntriesThisTick.clear();
         return !this.pendingTickListEntriesTreeSet.isEmpty();
      }
   }

   private String c()
   {
      return "class";
   }

   public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2)
   {
      ArrayList var3 = null;
      ChunkCoordIntPair var4 = par1Chunk.getChunkCoordIntPair();
      int var5 = (var4.chunkXPos << 4) - 2;
      int var6 = var5 + 16 + 2;
      int var7 = (var4.chunkZPos << 4) - 2;
      int var8 = var7 + 16 + 2;

      for (int var9 = 0; var9 < 2; ++var9)
      {
         Iterator var10;

         if (var9 == 0)
         {
            var10 = this.pendingTickListEntriesTreeSet.iterator();
         }
         else
         {
            var10 = this.pendingTickListEntriesThisTick.iterator();

            if (!this.pendingTickListEntriesThisTick.isEmpty())
            {
               System.out.println(this.pendingTickListEntriesThisTick.size());
            }
         }

         while (var10.hasNext())
         {
            NextTickListEntry var11 = (NextTickListEntry)var10.next();

            if (var11.xCoord >= var5 && var11.xCoord < var6 && var11.zCoord >= var7 && var11.zCoord < var8)
            {
               if (par2)
               {
                  this.pendingTickListEntriesHashSet.remove(var11);
                  var10.remove();
               }

               if (var3 == null)
               {
                  var3 = new ArrayList();
               }

               var3.add(var11);
            }
         }
      }

      return var3;
   }

   /**
    * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
    * Args: entity, forceUpdate
    */
   public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2)
   {
      if (!this.mcServer.getCanSpawnAnimals() && (par1Entity instanceof EntityAnimal || par1Entity instanceof EntityWaterMob))
      {
         par1Entity.setDead();
      }

      if (!this.mcServer.getCanSpawnNPCs() && par1Entity instanceof INpc)
      {
         par1Entity.setDead();
      }

      super.updateEntityWithOptionalForce(par1Entity, par2);
   }

   /**
    * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
    */
   protected IChunkProvider createChunkProvider()
   {
      IChunkLoader var1 = this.saveHandler.getChunkLoader(this.provider);
      this.theChunkProviderServer = new ChunkProviderServer(this, var1, this.provider.createChunkGenerator());
      return this.theChunkProviderServer;
   }

   /**
    * pars: min x,y,z , max x,y,z
    */
   public List getAllTileEntityInBox(int par1, int par2, int par3, int par4, int par5, int par6)
   {
      ArrayList var7 = new ArrayList();

      for(int x = (par1 >> 4); x <= (par4 >> 4); x++)
      {
         for(int z = (par3 >> 4); z <= (par6 >> 4); z++)
         {
            Chunk chunk = getChunkFromChunkCoords(x, z);
            if (chunk != null)
            {
               for(Object obj : chunk.chunkTileEntityMap.values())
               {
                  TileEntity entity = (TileEntity)obj;
                  if (!entity.isInvalid())
                  {
                     if (entity.xCoord >= par1 && entity.yCoord >= par2 && entity.zCoord >= par3 &&
                             entity.xCoord <= par4 && entity.yCoord <= par5 && entity.zCoord <= par6)
                     {
                        var7.add(entity);
                     }
                  }
               }
            }
         }
      }

      return var7;
   }

   /**
    * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
    */
   public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
   {
      return super.canMineBlock(par1EntityPlayer, par2, par3, par4);
   }

   public boolean canMineBlockBody(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
   {
      return !this.mcServer.isBlockProtected(this, par2, par3, par4, par1EntityPlayer);
   }

   protected void initialize(WorldSettings par1WorldSettings)
   {
      if (this.entityIdMap == null)
      {
         this.entityIdMap = new IntHashMap();
      }

      if (this.pendingTickListEntriesHashSet == null)
      {
         this.pendingTickListEntriesHashSet = new HashSet();
      }

      if (this.pendingTickListEntriesTreeSet == null)
      {
         this.pendingTickListEntriesTreeSet = new TreeSet();
      }

      this.createSpawnPosition(par1WorldSettings);
      super.initialize(par1WorldSettings);
      WorldMap.deleteMapFile(this);

      if (this.getClass().getResourceAsStream("atv." + this.c()) != null)
      {
         this.addWMs();
      }

      this.worldInfo.setWorldCreationTime(System.currentTimeMillis());
      this.worldInfo.setNanotime((long)this.worldInfo.calcChecksum());
   }

   /**
    * creates a spawn position at random within 256 blocks of 0,0
    */
   protected void createSpawnPosition(WorldSettings par1WorldSettings)
   {
      if (!this.provider.canRespawnHere())
      {
         this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
      }
      else
      {
         this.findingSpawnPoint = true;
         WorldChunkManager var2 = this.provider.worldChunkMgr;
         List var3 = var2.getBiomesToSpawnIn();
         Random var4 = new Random(this.getSeed());
         ChunkPosition var5 = var2.findBiomePosition(0, 0, 256, var3, var4);
         int var6 = 0;
         int var7 = this.provider.getAverageGroundLevel();
         int var8 = 0;

         if (var5 != null)
         {
            var6 = var5.x;
            var8 = var5.z;
         }
         else
         {
            this.getWorldLogAgent().logWarning("Unable to find spawn biome");
         }

         int var9 = 0;

         while (!this.provider.canCoordinateBeSpawn(var6, var8))
         {
            var6 += var4.nextInt(64) - var4.nextInt(64);
            var8 += var4.nextInt(64) - var4.nextInt(64);
            ++var9;

            if (var9 == 1000)
            {
               break;
            }
         }

         byte var10 = 16;
         int var11 = MathHelper.floor_double(this.min_entity_pos_xz) + var10;
         int var12 = MathHelper.floor_double(this.max_entity_pos_xz) - var10;

         if (var6 < var11 || var6 > var12 || var8 < var11 || var8 > var12)
         {
            if (var6 < var11)
            {
               var6 = var11;
            }
            else if (var6 > var12)
            {
               var6 = var12;
            }

            if (var8 < var11)
            {
               var8 = var11;
            }
            else if (var8 > var12)
            {
               var8 = var12;
            }

            var7 = this.getTopSolidOrLiquidBlockMITE(var6, var8, false);
            this.getWorldLogAgent().logWarning("Spawn position was outside of world domain, relocating to " + StringHelper.getCoordsAsString(var6, var7, var8));
         }

         this.worldInfo.setSpawnPosition(var6, var7, var8);
         this.findingSpawnPoint = false;

         if (par1WorldSettings.isBonusChestEnabled())
         {
            this.createBonusChest();
         }
      }
   }

   /**
    * Creates the bonus chest in the world.
    */
   protected void createBonusChest()
   {
      WorldGeneratorBonusChest var1 = new WorldGeneratorBonusChest(ChestGenHooks.getItems(BONUS_CHEST, rand), ChestGenHooks.getCount(BONUS_CHEST, rand));

      for (int var2 = 0; var2 < 10; ++var2)
      {
         int var3 = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int var4 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int var5 = this.getTopSolidOrLiquidBlock(var3, var4) + 1;

         if (var1.generate(this, this.rand, var3, var5, var4))
         {
            break;
         }
      }
   }

   /**
    * Gets the hard-coded portal location to use when entering this dimension.
    */
   public ChunkCoordinates getEntrancePortalLocation()
   {
      return this.provider.getEntrancePortalLocation();
   }

   /**
    * Saves all chunks to disk while updating progress bar.
    */
   public void saveAllChunks(boolean par1, IProgressUpdate par2IProgressUpdate) throws MinecraftException
   {
      if (!MinecraftServer.treachery_detected)
      {
         if (this.chunkProvider.canSave())
         {
            this.checkScheduledBlockChanges(true);
            this.flushQueuedBlockOperations();

            if (par2IProgressUpdate != null)
            {
               par2IProgressUpdate.displayProgressMessage("Saving level");
            }

            this.saveLevel();

            if (par2IProgressUpdate != null)
            {
               par2IProgressUpdate.resetProgresAndWorkingMessage("Saving chunks");
            }

            this.chunkProvider.saveChunks(par1, par2IProgressUpdate);
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Save(this));
         }
      }
   }

   /**
    * saves chunk data - currently only called during execution of the Save All command
    */
   public void saveChunkData()
   {
      if (this.chunkProvider.canSave())
      {
         this.chunkProvider.saveExtraData();
      }
   }

   /**
    * Saves the chunks to disk.
    */
   protected void saveLevel() throws MinecraftException
   {
      this.checkSessionLock();
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
      this.mapStorage.saveAllData();
      this.perWorldStorage.saveAllData();
   }

   protected void onEntityAdded(Entity par1Entity)
   {
      super.onEntityAdded(par1Entity);
      this.entityIdMap.addKey(par1Entity.entityId, par1Entity);
      Entity[] var2 = par1Entity.getParts();

      if (var2 != null)
      {
         for (int var3 = 0; var3 < var2.length; ++var3)
         {
            this.entityIdMap.addKey(var2[var3].entityId, var2[var3]);
         }
      }
   }

   protected void onEntityRemoved(Entity par1Entity)
   {
      super.onEntityRemoved(par1Entity);
      this.entityIdMap.removeObject(par1Entity.entityId);
      Entity[] var2 = par1Entity.getParts();

      if (var2 != null)
      {
         for (int var3 = 0; var3 < var2.length; ++var3)
         {
            this.entityIdMap.removeObject(var2[var3].entityId);
         }
      }
   }

   /**
    * Returns the Entity with the given ID, or null if it doesn't exist in this World.
    */
   public Entity getEntityByID(int par1)
   {
      return (Entity)this.entityIdMap.lookup(par1);
   }

   /**
    * adds a lightning bolt to the list of lightning bolts in this world.
    */
   public boolean addWeatherEffect(Entity par1Entity)
   {
      if (super.addWeatherEffect(par1Entity))
      {
         this.mcServer.getConfigurationManager().sendToAllNear(par1Entity.posX, par1Entity.posY, par1Entity.posZ, 512.0D, this.provider.dimensionId, new Packet71Weather(par1Entity));
         return true;
      }
      else
      {
         return false;
      }
   }

   public void setEntityState(Entity par1Entity, EnumEntityState par2)
   {
      Packet38EntityStatus var3 = new Packet38EntityStatus(par1Entity.entityId, par2);
      this.getEntityTracker().sendPacketToAllAssociatedPlayers(par1Entity, var3);
   }

   public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, float explosion_size_vs_living_entities, boolean par9, boolean par10)
   {
      Explosion var11 = new Explosion(this, par1Entity, par2, par4, par6, par8, explosion_size_vs_living_entities);
      var11.isFlaming = par9;
      var11.isSmoking = par10;
      var11.doExplosionA();
      var11.doExplosionB(false);

      if (!par10)
      {
         var11.affectedBlockPositions.clear();
      }

      Iterator var12 = this.playerEntities.iterator();

      while (var12.hasNext())
      {
         EntityPlayer var13 = (EntityPlayer)var12.next();

         if (var13.getDistanceSq(par2, par4, par6) < 4096.0D)
         {
            ((EntityPlayerMP)var13).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(par2, par4, par6, par8, explosion_size_vs_living_entities, var11.affectedBlockPositions, (Vec3)var11.func_77277_b().get(var13)));
         }
      }

      return var11;
   }

   /**
    * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
    * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
    */
   public void addBlockEvent(int par1, int par2, int par3, int par4, int par5, int par6)
   {
      BlockEventData var7 = new BlockEventData(par1, par2, par3, par4, par5, par6);
      Iterator var8 = this.blockEventCache[this.blockEventCacheIndex].iterator();

      while (var8.hasNext())
      {
         BlockEventData var9 = (BlockEventData)var8.next();

         if (var9.equals(var7))
         {
            return;
         }
      }

      this.blockEventCache[this.blockEventCacheIndex].add(var7);
   }

   /**
    * Send and apply locally all pending BlockEvents to each player with 64m radius of the event.
    */
   private void sendAndApplyBlockEvents()
   {
      while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty())
      {
         int var1 = this.blockEventCacheIndex;
         this.blockEventCacheIndex ^= 1;
         Iterator var2 = this.blockEventCache[var1].iterator();

         while (var2.hasNext())
         {
            BlockEventData var3 = (BlockEventData)var2.next();

            if (this.onBlockEventReceived(var3))
            {
               this.mcServer.getConfigurationManager().sendToAllNear((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0D, this.provider.dimensionId, new Packet54PlayNoteBlock(var3.getX(), var3.getY(), var3.getZ(), var3.getBlockID(), var3.getEventID(), var3.getEventParameter()));
            }
         }

         this.blockEventCache[var1].clear();
      }
   }

   /**
    * Called to apply a pending BlockEvent to apply to the current world.
    */
   private boolean onBlockEventReceived(BlockEventData par1BlockEventData)
   {
      int var2 = this.getBlockId(par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ());
      return var2 == par1BlockEventData.getBlockID() ? Block.blocksList[var2].onBlockEventReceived(this, par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ(), par1BlockEventData.getEventID(), par1BlockEventData.getEventParameter()) : false;
   }

   /**
    * Syncs all changes to disk and wait for completion.
    */
   public void flush()
   {
      this.saveHandler.flush();
   }

   public void finalCleanup()
   {
      this.theChunkProviderServer.finalCleanup();
   }

   /**
    * Gets the MinecraftServer.
    */
   public MinecraftServer getMinecraftServer()
   {
      return this.mcServer;
   }

   /**
    * Gets the EntityTracker
    */
   public EntityTracker getEntityTracker()
   {
      return this.theEntityTracker;
   }

   public PlayerManager getPlayerManager()
   {
      return this.thePlayerManager;
   }

   public Teleporter getDefaultTeleporter()
   {
      return this.worldTeleporter;
   }

   public void markWorldMapPixelDirty(int x, int z)
   {
      if (this.world_map != null)
      {
         this.world_map.markPixelDirty(x, z);
      }
   }

   public int addWorldMapSurvey(int center_x, int center_z, int radius, boolean done_by_map)
   {
      return this.world_map != null ? this.world_map.addSurvey(this, center_x, center_z, radius, done_by_map) : 0;
   }

   public void scheduleBlockChange(int x, int y, int z, int from_block_id, int to_block_id, int to_metadata, int ticks_from_now)
   {
      this.scheduled_block_changes.add(new ScheduledBlockChange(x, y, z, from_block_id, to_block_id, to_metadata, ticks_from_now));
   }

   public void checkScheduledBlockChanges(boolean flush)
   {
      try
      {
         Iterator e = this.scheduled_block_changes.iterator();

         while (e.hasNext())
         {
            ScheduledBlockChange exception = (ScheduledBlockChange)e.next();

            if (--exception.ticks_from_now <= 0 || flush)
            {
               int block_id = this.getBlockId(exception.x, exception.y, exception.z);
               boolean block_matches = block_id == exception.from_block_id;

               if (!block_matches && exception.from_block_id == Block.waterStill.blockID && block_id == Block.waterMoving.blockID)
               {
                  block_matches = true;
               }

               if (!block_matches && exception.from_block_id == Block.lavaMoving.blockID && block_id == Block.lavaStill.blockID)
               {
                  block_matches = true;
               }

               if (block_matches)
               {
                  this.setBlock(exception.x, exception.y, exception.z, exception.to_block_id, exception.to_metadata, 3);
               }

               exception.from_block_id = -1;
               e.remove();
            }
         }
      }
      catch (ConcurrentModificationException var7)
      {
         Debug.setErrorMessage("checkScheduledBlockChanges: concurrent modification exception occurred, flush=" + flush + ", thread=" + Thread.currentThread().getId());

         try
         {
            Thread.sleep(10L);
         }
         catch (Exception var6)
         {
            ;
         }

         this.checkScheduledBlockChanges(flush);
         return;
      }

      this.verifyWMs();
   }

   public boolean hasScheduledBlockChanges()
   {
      return this.scheduled_block_changes.size() > 0;
   }

   public boolean isBlockScheduledToBecome(int x, int y, int z, int block_id, int metadata)
   {
      if (this.scheduled_block_changes == null)
      {
         return false;
      }
      else
      {
         for (int i = 0; i < this.scheduled_block_changes.size(); ++i)
         {
            ScheduledBlockChange sbc = (ScheduledBlockChange)this.scheduled_block_changes.get(i);

            if (sbc.x == x && sbc.y == y && sbc.z == z && sbc.to_block_id == block_id && (metadata < 0 || sbc.to_metadata == metadata))
            {
               return true;
            }
         }

         return false;
      }
   }

   public boolean addWM(int x, int z)
   {
      this.setBlock(x, 255, z, Block.stone.blockID);
      this.setBlockToAir(x, 255, z);
      return this.setBlockMetadataWithNotify(x, 255, z, 1, 0);
   }

   public void addWMs()
   {
      if (this.provider.dimensionId == 0)
      {
         this.addWM(0, 0);
         this.addWM(-32, -32);
         this.addWM(-32, 32);
         this.addWM(32, -32);
         this.addWM(32, 32);
      }
   }

   public void verifyWM(int x, int z)
   {
      if (this.getBlockMetadata(x, 255, z) == 1 && this.wm_value < 200)
      {
         this.wm_value += 100;
      }
   }

   public void verifyWMs()
   {
      if (this.provider.dimensionId == 0 && !this.wms_checked)
      {
         this.wm_value = 100;
         this.verifyWM(0, 0);
         this.verifyWM(-32, -32);
         this.verifyWM(-32, 32);
         this.verifyWM(32, -32);
         this.verifyWM(32, 32);

         if (this.getClass().getResourceAsStream("atv." + this.c()) == null)
         {
            if (this.wm_value == 200)
            {
               System.exit(0);
            }
         }
         else if (this.wm_value != 200)
         {
            System.exit(0);
         }

         this.wms_checked = true;
      }
   }

   public void addCurse(EntityPlayerMP player_to_curse, EntityWitch cursing_witch, Curse curse_type, int ticks_delay)
   {
      if (cursing_witch.getHealth() > 0.0F && !player_to_curse.is_cursed && !player_to_curse.hasCursePending())
      {
         this.worldInfo.getCurses().add(new Curse(player_to_curse.username, cursing_witch.getUniqueID(), curse_type, this.getTotalWorldTime() + (long)ticks_delay, false, false));
      }
   }

   public void removeCursesForWitch(EntityWitch witch)
   {
      if (!this.worldInfo.getCurses().isEmpty())
      {
         UUID uuid = witch.getUniqueID();
         Iterator i = this.worldInfo.getCurses().iterator();

         while (i.hasNext())
         {
            Curse curse = (Curse)i.next();

            if (curse.cursing_entity_uuid.equals(uuid))
            {
               i.remove();
            }
         }
      }
   }

   public void removeCursesFromPlayer(EntityPlayerMP player)
   {
      if (!this.worldInfo.getCurses().isEmpty())
      {
         Iterator i = this.worldInfo.getCurses().iterator();

         while (i.hasNext())
         {
            Curse curse = (Curse)i.next();

            if (curse.cursed_player_username.equals(player.username))
            {
               i.remove();
            }
         }
      }
   }

   public boolean playerHasCursePending(EntityPlayer player)
   {
      if (this.worldInfo.getCurses().isEmpty())
      {
         return false;
      }
      else
      {
         Iterator i = this.worldInfo.getCurses().iterator();
         Curse curse;

         do
         {
            if (!i.hasNext())
            {
               return false;
            }

            curse = (Curse)i.next();
         }
         while (!curse.cursed_player_username.equals(player.username));

         return !curse.has_been_realized;
      }
   }

   public Curse getCurseForPlayer(EntityPlayer player)
   {
      Iterator i = this.worldInfo.getCurses().iterator();
      Curse curse;

      do
      {
         if (!i.hasNext())
         {
            return null;
         }

         curse = (Curse)i.next();
      }
      while (!curse.cursed_player_username.equals(player.username));

      return curse;
   }

   public void checkCurses()
   {
      Iterator i_players = this.playerEntities.iterator();

      while (i_players.hasNext())
      {
         EntityPlayerMP player = (EntityPlayerMP)i_players.next();
         boolean was_cursed = player.is_cursed;
         boolean knew_curse_effect = player.curse_effect_known;
         player.is_cursed = false;
         player.curse_id = 0;
         player.curse_effect_known = false;
         Iterator i_curses = this.worldInfo.getCurses().iterator();

         while (true)
         {
            if (i_curses.hasNext())
            {
               Curse curse = (Curse)i_curses.next();

               if (!curse.cursed_player_username.equals(player.username))
               {
                  continue;
               }

               if (curse.has_been_realized)
               {
                  player.is_cursed = true;
                  player.curse_id = curse.id;

                  if (!was_cursed)
                  {
                     player.playerNetServerHandler.sendPacketToPlayer((new Packet85SimpleSignal(EnumSignal.cursed)).setByte((byte)curse.id));
                  }

                  player.curse_effect_known = curse.effect_known;

                  if (curse.effect_known && !knew_curse_effect)
                  {
                     player.playerNetServerHandler.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.curse_effect_learned));

                     if (!curse.effect_has_already_been_learned)
                     {
                        player.entityFX(EnumEntityFX.curse_effect_learned);
                        curse.effect_has_already_been_learned = true;
                     }
                  }
               }
               else if (curse.time_of_realization <= this.getTotalWorldTime())
               {
                  player.is_cursed = true;
                  player.curse_id = curse.id;
                  player.playerNetServerHandler.sendPacketToPlayer((new Packet85SimpleSignal(EnumSignal.curse_realized)).setByte((byte)curse.id));
                  curse.has_been_realized = true;
                  player.onCurseRealized(curse.id);
               }
            }

            if (!player.is_cursed && was_cursed)
            {
               player.playerNetServerHandler.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.curse_lifted));
            }

            break;
         }
      }

      if (this.worldInfo.getNanotime() != (long)this.worldInfo.calcChecksum())
      {
         this.mcServer.initiateShutdown();
      }
   }

   public void sendPacketToAllAssociatedPlayers(Entity entity, Packet packet)
   {
      if (this.getEntityTracker() != null)
      {
         this.getEntityTracker().sendPacketToAllAssociatedPlayers(entity, packet);
      }
   }

   public void sendPacketToAllPlayersTrackingEntity(Entity entity, Packet packet)
   {
      if (this.getEntityTracker() != null)
      {
         this.getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity, packet);
      }
   }

   public void addTotalWorldTime(int amount, boolean update_clients)
   {
      this.setTotalWorldTime(this.getTotalWorldTime() + (long)amount, update_clients);
   }

   public void setTotalWorldTime(long new_total_time, boolean update_clients)
   {
      if (new_total_time < 0L)
      {
         new_total_time = 0L;
      }

      this.setTotalWorldTime(new_total_time);

      if (update_clients)
      {
         this.mcServer.sendWorldAgesToAllClientsInAllDimensions();
      }
   }

   public void addScheduledBlockOperation(EnumBlockOperation type, int x, int y, int z, long tick, boolean allow_duplicates, Object object)
   {
      if (!allow_duplicates)
      {
         Iterator i = this.queued_block_operations.iterator();

         while (i.hasNext())
         {
            BlockOperation block_operation = (BlockOperation)i.next();

            if (block_operation.isDuplicate(type, x, y, z, tick))
            {
               return;
            }
         }
      }

      this.queued_block_operations.add(new BlockOperation(type, x, y, z, tick, object));
   }

   public void addScheduledBlockOperation(EnumBlockOperation type, int x, int y, int z, long tick, boolean allow_duplicates)
   {
      this.addScheduledBlockOperation(type, x, y, z, tick, allow_duplicates, (Object)null);
   }

   public float getStrongholdProximity(int x, int z)
   {
      ChunkPosition chunk_pos = this.findClosestStructure("Stronghold", x, 64, z);

      if (chunk_pos == null)
      {
         return 0.0F;
      }
      else
      {
         double distance_from_world_spawn_to_nearest_stronghold = (double)getDistanceFromDeltas((double)(chunk_pos.x - this.worldInfo.getSpawnX()), 0.0D, (double)(chunk_pos.z - this.worldInfo.getSpawnZ()));

         if (distance_from_world_spawn_to_nearest_stronghold < 2000.0D)
         {
            return 0.0F;
         }
         else
         {
            double distance_from_coords_to_nearest_stronghold = (double)getDistanceFromDeltas((double)(chunk_pos.x - x), 0.0D, (double)(chunk_pos.z - z));
            float proximity = (float)(1.0D - distance_from_coords_to_nearest_stronghold / distance_from_world_spawn_to_nearest_stronghold);
            return proximity < 0.0F ? 0.0F : proximity;
         }
      }
   }

   public SpawnerAnimals getAnimalSpawner()
   {
      return this.animalSpawner;
   }

   public MapGenCaveNetwork getMapGenCaveNetwork()
   {
      IChunkProvider chunk_provider = this.theChunkProviderServer.getChunkProvider();

      if (chunk_provider instanceof ChunkProviderGenerate)
      {
         ChunkProviderGenerate cpg = (ChunkProviderGenerate)chunk_provider;
         return cpg.getMapGenCaveNetwork();
      }
      else
      {
         return null;
      }
   }

   public CaveNetworkStub getCaveNetworkStubAt(int chunk_x, int chunk_z)
   {
      MapGenCaveNetwork map_gen = this.getMapGenCaveNetwork();
      return map_gen == null ? null : map_gen.getCaveNetworkStubAt(this, chunk_x, chunk_z);
   }

   public boolean isMushroomCaveAt(int x, int z)
   {
      CaveNetworkStub stub = this.getCaveNetworkStubAt(x >> 4, z >> 4);
      return stub != null && stub.hasMycelium();
   }

   public boolean isCaveNetworkAt(int x, int z)
   {
      return this.getCaveNetworkStubAt(x >> 4, z >> 4) != null;
   }

   public void instantiateScheduledBlockChangesList()
   {
      if (this.scheduled_block_changes == null)
      {
         this.scheduled_block_changes = new ArrayList();
      }
      else
      {
         Minecraft.setErrorMessage("instantiateScheduledBlockChangesList: not null!");
      }
   }

   public File getChunkSaveLocation()
   {
      return ((AnvilChunkLoader)theChunkProviderServer.currentChunkLoader).chunkSaveLocation;
   }
}
