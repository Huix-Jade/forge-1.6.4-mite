package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.dispenser.DispenserBehaviors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet92UpdateTimeSmall;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Debug;
import net.minecraft.util.DebugAttack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringHelper;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public abstract class MinecraftServer implements ICommandSender, Runnable, IPlayerUsage
{
   /** Instance of Minecraft Server. */
   private static MinecraftServer mcServer;
   private final ISaveFormat anvilConverterForAnvilFile;

   /** The PlayerUsageSnooper instance. */
   private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, getSystemTimeMillis());
   private final File anvilFile;
   private final List playersOnline = new ArrayList();

   /**
    * Collection of objects to update every tick. Type: List<IUpdatePlayerListBox>
    */
   private final List tickables = new ArrayList();
   private final ICommandManager commandManager;
   public final Profiler theProfiler = new Profiler();

   /** The server's hostname. */
   private String hostname;

   /** The server's port. */
   private int serverPort = -1;

   /** The server world instances. */
   public WorldServer[] worldServers;

   /** The ServerConfigurationManager instance. */
   private ServerConfigurationManager serverConfigManager;

   /**
    * Indicates whether the server is running or not. Set to false to initiate a shutdown.
    */
   private boolean serverRunning = true;

   /** Indicates to other classes that the server is safely stopped. */
   private boolean serverStopped;

   /** Incremented every tick. */
   protected int tickCounter;
   protected Proxy serverProxy;

   /**
    * The task the server is currently working on(and will output on outputPercentRemaining).
    */
   public String currentTask;

   /** The percentage of the current task finished so far. */
   public int percentDone;

   /** True if the server is in online mode. */
   private boolean onlineMode;

   /** True if the server has animals turned on. */
   private boolean canSpawnAnimals;
   private boolean canSpawnNPCs;

   /** Indicates whether PvP is active on the server or not. */
   private boolean pvpEnabled;

   /** Determines if flight is allowed or not. */
   private boolean allowFlight;

   /** The server MOTD string. */
   private String motd;

   /** Maximum build height. */
   private int buildLimit;
   private int field_143008_E;
   private long lastSentPacketID;
   private long lastSentPacketSize;
   private long lastReceivedID;
   private long lastReceivedSize;
   public final long[] sentPacketCountArray;
   public final long[] sentPacketSizeArray;
   public final long[] receivedPacketCountArray;
   public final long[] receivedPacketSizeArray;
   public final long[] tickTimeArray;

   /** Stats are [dimension][tick%100] system.nanoTime is stored. */
   //public long[][] timeOfLastDimensionTick;
   public Hashtable<Integer, long[]> worldTickTimes = new Hashtable<Integer, long[]>();
   private KeyPair serverKeyPair;

   /** Username of the server owner (for integrated servers) */
   private String serverOwner;
   private String folderName;
   private String worldName;
   private boolean isDemo;
   private boolean enableBonusChest;

   /**
    * If true, there is no need to save chunks or stop the server, because that is already being done.
    */
   private boolean worldIsBeingDeleted;
   private String texturePack;
   private boolean serverIsRunning;

   /**
    * Set when warned for "Can't keep up", which triggers again after 15 seconds.
    */
   private long timeOfLastWarning;
   private String userMessage;
   private boolean startProfiling;
   private boolean isGamemodeForced;
   public int default_world_map_size = 1024;
   public boolean save_world_maps_on_shutdown;
   public int ms_taken_for_last_100_ticks;
   public static boolean treachery_detected;
   private static int treachery_shutdown_counter;
   public static final int num_world_servers = 4;
   public final ThreadMinecraftServer thread;
   public static final int WORLD_INDEX_OVERWORLD = 0;
   public static final int WORLD_INDEX_NETHER = 1;
   public static final int WORLD_INDEX_THE_END = 2;
   public static final int WORLD_INDEX_UNDERWORLD = 3;

   public MinecraftServer(File par1File)
   {
      this.worldServers = new WorldServer[DimensionManager.world_size];
      this.serverProxy = Proxy.NO_PROXY;
      this.field_143008_E = 0;
      this.sentPacketCountArray = new long[100];
      this.sentPacketSizeArray = new long[100];
      this.receivedPacketCountArray = new long[100];
      this.receivedPacketSizeArray = new long[100];
      this.tickTimeArray = new long[100];
      this.texturePack = "";
      mcServer = this;
      this.anvilFile = par1File;
      this.commandManager = new ServerCommandManager();
      this.anvilConverterForAnvilFile = new AnvilSaveConverter(par1File);
      this.registerDispenseBehaviors();

      if (Minecraft.hit_list == null)
      {
         Minecraft.hit_list = Minecraft.getHitList();
      }

      this.thread = new ThreadMinecraftServer(this, "Server thread");
      Minecraft.server_thread = this.thread;
   }

   /**
    * Register all dispense behaviors.
    */
   private void registerDispenseBehaviors()
   {
      DispenserBehaviors.registerDispenserBehaviours();
   }

   /**
    * Initialises the server and starts it.
    */
   protected abstract boolean startServer() throws IOException;

   protected void convertMapIfNeeded(String par1Str)
   {
      if (this.getActiveAnvilConverter().isOldMapFormat(par1Str))
      {
         this.getLogAgent().logInfo("Converting map!");
         this.setUserMessage("menu.convertingLevel");
         this.getActiveAnvilConverter().convertMapFormat(par1Str, new ConvertingProgressUpdate(this));
      }
   }

   /**
    * Typically "menu.convertingLevel", "menu.loadingLevel" or others.
    */
   protected synchronized void setUserMessage(String par1Str)
   {
      this.userMessage = par1Str;
   }

   public synchronized String getUserMessage()
   {
      return this.userMessage;
   }

   protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str)
   {
      this.convertMapIfNeeded(par1Str);
      this.setUserMessage("menu.loadingLevel");
      this.worldServers = new WorldServer[DimensionManager.world_size];
//      this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
      ISaveHandler saveLoader = this.anvilConverterForAnvilFile.getSaveLoader(par1Str, true);
      WorldInfo var8 = saveLoader.loadWorldInfo();
      WorldSettings var9;

      if (var8 == null)
      {
         var9 = new WorldSettings(par3, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), par5WorldType, this.areSkillsEnabled());
         var9.func_82750_a(par6Str);
      }
      else
      {
         var9 = new WorldSettings(var8);
      }

      if (this.enableBonusChest)
      {
         var9.enableBonusChest();
      }

      WorldServer overWorld = (isDemo() ? new DemoWorldServer(this, saveLoader, par2Str, 0, theProfiler, getLogAgent()) : new WorldServer(this, saveLoader, par2Str, 0, var9, theProfiler, getLogAgent()));
      for (int dim : DimensionManager.getStaticDimensionIDs())
      {
         int i = 0;
         WorldServer world = (dim == 0 ? overWorld : new WorldServerMulti(this, saveLoader, par2Str, dim, var9, overWorld, theProfiler, getLogAgent()));
         worldServers[i] = world;
         world.addWorldAccess(new WorldManager(this, world));

         if (!this.isSinglePlayer())
         {
            world.getWorldInfo().setGameType(this.getGameType());
         }
         ++i;
         this.serverConfigManager.setPlayerManager(this.worldServers);
         MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
      }

      this.serverConfigManager.setPlayerManager(new WorldServer[]{ overWorld });

      this.setDifficultyForAllWorlds(this.getDifficulty());
      this.initialWorldChunkLoad();
   }

   protected void initialWorldChunkLoad()
   {
      int var5 = 0;
      this.setUserMessage("menu.generatingTerrain");
      byte var6 = 0;
      this.getLogAgent().logInfo("Preparing start region for level " + var6);
      WorldServer var7 = this.worldServers[var6];
      ChunkCoordinates var8 = var7.getSpawnPoint();
      long var9 = getSystemTimeMillis();

      for (int var11 = -192; var11 <= 192 && this.isServerRunning(); var11 += 16)
      {
         for (int var12 = -192; var12 <= 192 && this.isServerRunning(); var12 += 16)
         {
            long var13 = getSystemTimeMillis();

            if (var13 - var9 > 1000L)
            {
               this.outputPercentRemaining("Preparing spawn area", var5 * 100 / 625);
               var9 = var13;
            }

            ++var5;
            var7.theChunkProviderServer.loadChunk(var8.posX + var11 >> 4, var8.posZ + var12 >> 4);
         }
      }

      this.clearCurrentTask();
   }

   public abstract boolean canStructuresSpawn();

   public abstract EnumGameType getGameType();

   /**
    * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
    */
   public abstract int getDifficulty();

   /**
    * Defaults to false.
    */
   public abstract boolean isHardcore();

   public abstract int func_110455_j();

   public abstract boolean areSkillsEnabled();

   /**
    * Used to display a percent remaining given text and the percentage.
    */
   protected void outputPercentRemaining(String par1Str, int par2)
   {
      this.currentTask = par1Str;
      this.percentDone = par2;
      this.getLogAgent().logInfo(par1Str + ": " + par2 + "%");
   }

   /**
    * Set current task to null and set its percentage to 0.
    */
   protected void clearCurrentTask()
   {
      this.currentTask = null;
      this.percentDone = 0;
   }

   protected void saveAllWorlds(boolean par1, boolean wait_until_finished)
   {
      if (!treachery_detected)
      {
         if (!this.worldIsBeingDeleted)
         {
            WorldServer[] var2 = this.worldServers;
            if (var2 == null) return; //ForgE: Just in case, NPE protection as it has been encountered.
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
               WorldServer var5 = var2[var4];

               if (var5 != null)
               {
                  try
                  {
                     var5.saveAllChunks(true, (IProgressUpdate)null);
                  }
                  catch (MinecraftException var8)
                  {
                     this.getLogAgent().logWarning(var8.getMessage());
                  }
               }
            }

            if (wait_until_finished)
            {
               ThreadedFileIOBase.waitForFinish();
            }
         }
      }
   }

   /**
    * Saves all necessary data as preparation for stopping the server.
    */
   public void stopServer()
   {
      if (!this.worldIsBeingDeleted)
      {
         this.getLogAgent().logInfo("Stopping server");

         if (this.getNetworkThread() != null)
         {
            this.getNetworkThread().stopListening();
         }

         if (this.serverConfigManager != null)
         {
            if (!treachery_detected)
            {
               this.getLogAgent().logInfo("Saving players");
               this.serverConfigManager.saveAllPlayerData();
            }

            this.serverConfigManager.removeAllPlayers();
         }

         if (!treachery_detected)
         {
            this.getLogAgent().logInfo("Saving worlds");
            long var1 = System.currentTimeMillis();
            this.saveAllWorlds(false, true);
            this.getLogAgent().logInfo("Finished saving all worlds in " + StringHelper.formatFloat((float)(System.currentTimeMillis() - var1) / 1000.0F) + " seconds");

            if (DedicatedServer.tournament_type == EnumTournamentType.score)
            {
               DedicatedServer.appendTournamentStandingsToFile(true);
            }

            if (this.save_world_maps_on_shutdown)
            {
               this.saveWorldMaps();
            }
            else
            {
               this.getLogAgent().logInfo("Discarding world maps");
            }
         }

         if (treachery_detected)
         {
            clearTreacheryDetected();
         }

          for (WorldServer var2 : this.worldServers) {
              MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(var2));
              var2.flush();
          }

         WorldServer[] tmp = worldServers;
         for (WorldServer world : tmp)
         {
            DimensionManager.setWorld(world.provider.dimensionId, null);
         }

         if (this.usageSnooper != null && this.usageSnooper.isSnooperRunning())
         {
            this.usageSnooper.stopSnooper();
         }

         this.finalCleanup();
      }
      else if (treachery_detected)
      {
         clearTreacheryDetected();
      }
   }

   private void finalCleanup()
   {
      boolean display_amount_freed = false;
      long free_memory = 0L;

      if (display_amount_freed)
      {
         System.gc();
         free_memory = Runtime.getRuntime().freeMemory();
      }

      for (int i = 0; i < this.worldServers.length; ++i)
      {
         this.worldServers[i].finalCleanup();
      }

      if (display_amount_freed)
      {
         System.gc();
         Debug.println("finalCleanup: " + (Runtime.getRuntime().freeMemory() - free_memory) / 1024L / 1024L + " MB of memory was freed");
      }
   }

   /**
    * "getHostname" is already taken, but both return the hostname.
    */
   public String getServerHostname()
   {
      return this.hostname;
   }

   public void setHostname(String par1Str)
   {
      this.hostname = par1Str;
   }

   public boolean isServerRunning()
   {
      return this.serverRunning;
   }

   /**
    * Sets the serverRunning variable to false, in order to get the server to shut down.
    */
   public void initiateShutdown()
   {
      this.serverRunning = false;
   }

   public void run()
   {
      try
      {
         if (this.startServer())
         {
            FMLCommonHandler.instance().handleServerStarted();

            long var1 = getSystemTimeMillis();

            FMLCommonHandler.instance().onWorldLoadTick(worldServers);
            for (long var50 = 0L; this.serverRunning; this.serverIsRunning = true)
            {
               long var5 = getSystemTimeMillis();
               long var7 = var5 - var1;

               if (var7 > 2000L && var1 - this.timeOfLastWarning >= 15000L)
               {
                  this.getLogAgent().logWarning("Can\'t keep up! Did the system time change, or is the server overloaded?");
                  var7 = 2000L;
                  this.timeOfLastWarning = var1;
               }

               if (var7 < 0L)
               {
                  this.getLogAgent().logWarning("Time ran backwards! Did the system time change?");
                  var7 = 0L;
               }

               var50 += var7;
               var1 = var5;

               if (this.worldServers[0].allPlayersAsleepOrDead())
               {
                  this.tick();
                  var50 = 0L;
               }
               else
               {
                  while (var50 > 50L)
                  {
                     var50 -= 50L;
                     this.tick();
                  }
               }

               Thread.sleep(1L);
            }
            FMLCommonHandler.instance().handleServerStopping();
         }
         else
         {
            this.finalTick((CrashReport)null);
         }
      }
      catch (Throwable var48)
      {
         if (FMLCommonHandler.instance().shouldServerBeKilledQuietly()) {
            return;
         }
         var48.printStackTrace();
         this.getLogAgent().logSevereException("Encountered an unexpected exception " + var48.getClass().getSimpleName(), var48);
         CrashReport var2 = null;

         if (var48 instanceof ReportedException)
         {
            var2 = this.addServerInfoToCrashReport(((ReportedException)var48).getCrashReport());
         }
         else
         {
            var2 = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", var48));
         }

         File var3 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

         if (var2.saveToFile(var3, this.getLogAgent()))
         {
            this.getLogAgent().logSevere("This crash report has been saved to: " + var3.getAbsolutePath());
         }
         else
         {
            this.getLogAgent().logSevere("We were unable to save this crash report to disk.");
         }

         this.finalTick(var2);
      }
      finally
      {
         try
         {
            this.save_world_maps_on_shutdown = this.isServerSideMappingEnabled();
            if (FMLCommonHandler.instance().shouldServerBeKilledQuietly()) {
               return;
            }
            this.stopServer();
            this.serverStopped = true;
         }
         catch (Throwable var46)
         {
            var46.printStackTrace();
         }
         finally
         {
            FMLCommonHandler.instance().handleServerStopped();
            this.serverStopped = true;
            this.systemExitNow();
         }
      }
   }

   protected File getDataDirectory()
   {
      return new File(".");
   }

   /**
    * Called on exit from the main run() loop.
    */
   protected void finalTick(CrashReport par1CrashReport) {}

   /**
    * Directly calls System.exit(0), instantly killing the program.
    */
   protected void systemExitNow() {}

   /**
    * Main function called by run() every loop.
    */
   public void tick()
   {
      FMLCommonHandler.instance().onPreServerTick();
      if (treachery_detected)
      {
         if (treachery_shutdown_counter == 200)
         {
            this.serverConfigManager.sendChatMsg(ChatMessageComponent.createFromText(this.scramble("Givzxsvib wvgvxgvw! Tznv droo hsfg wldm rm 89 hvxlmwh.")).setColor(EnumChatFormatting.YELLOW));
         }

         if (--treachery_shutdown_counter <= 0)
         {
            this.initiateShutdown();
         }
      }

      if (!this.isDedicatedServer() && this.tickCounter % 20 == 0)
      {
         ResourceLocation.verifyResourceLocations();
      }

      long var1 = System.currentTimeMillis();
      StringBuilder var3 = new StringBuilder();

      for (int var4 = 0; var4 < this.worldServers.length; ++var4)
      {
         var3.append(this.worldServers[var4].getWorldVec3Pool().getPoolSize());

         if (var4 < this.worldServers.length - 1)
         {
            var3.append(" | ");
         }
      }

      Minecraft.server_pools_string = var3.toString();
      long var8 = System.nanoTime();
      AxisAlignedBB.getAABBPool().cleanPool();
      FMLCommonHandler.instance().onPreServerTick();
      ++this.tickCounter;

      if (this.startProfiling)
      {
         this.startProfiling = false;
         this.theProfiler.profilingEnabled = true;
         this.theProfiler.clearProfiling();
      }

      this.theProfiler.startSection("root");
      this.updateTimeLightAndEntities();

      if (!treachery_detected && this.tickCounter % 900 == 0)
      {
         boolean var6 = false;

         for (int var7 = 0; var7 < this.worldServers.length; ++var7)
         {
            if (this.worldServers[var7] != null && this.worldServers[var7].hasScheduledBlockChanges())
            {
               var6 = true;
               break;
            }
         }

         if (!var6)
         {
            this.theProfiler.startSection("save");
            this.serverConfigManager.saveAllPlayerData();
            this.saveAllWorlds(true, false);
            this.theProfiler.endSection();
         }
      }

      this.theProfiler.startSection("tallying");
      this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - var8;
      this.sentPacketCountArray[this.tickCounter % 100] = Packet.sentID - this.lastSentPacketID;
      this.lastSentPacketID = Packet.sentID;
      this.sentPacketSizeArray[this.tickCounter % 100] = Packet.sentSize - this.lastSentPacketSize;
      this.lastSentPacketSize = Packet.sentSize;
      this.receivedPacketCountArray[this.tickCounter % 100] = Packet.receivedID - this.lastReceivedID;
      this.lastReceivedID = Packet.receivedID;
      this.receivedPacketSizeArray[this.tickCounter % 100] = Packet.receivedSize - this.lastReceivedSize;
      this.lastReceivedSize = Packet.receivedSize;
      this.theProfiler.endSection();
      this.theProfiler.startSection("snooper");

      if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100)
      {
         this.usageSnooper.startSnooper();
      }

      if (this.tickCounter % 6000 == 0)
      {
         this.usageSnooper.addMemoryStatsToSnooper();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
      this.ms_taken_for_last_100_ticks = (int)((long)this.ms_taken_for_last_100_ticks + (System.currentTimeMillis() - var1));

      if (this.tickCounter >= 100)
      {
         int var9 = this.getAverageTickTime();
         this.ms_taken_for_last_100_ticks -= var9;

         if (this.tickCounter % 20 == 0 && this.getLoadOnServer() >= 1.0F)
         {
            this.serverConfigManager.sendPacketToAllPlayers(new Packet85SimpleSignal(EnumSignal.cpu_overburdened));
         }
      }

      if (DebugAttack.instance != null)
      {
         DebugAttack.flush();
      }
      FMLCommonHandler.instance().onPostServerTick();
   }

   public int getAverageTickTime()
   {
      return this.ms_taken_for_last_100_ticks / 100;
   }

   public float getLoadOnServer()
   {
      return this.tickCounter < 100 ? -1.0F : (float)this.ms_taken_for_last_100_ticks / 5000.0F;
   }

   public void sendWorldAgesToAllClientsInAllDimensions()
   {
      if (Packet92UpdateTimeSmall.areAllWorldTotalTimesSuitable(this.worldServers))
      {
         this.serverConfigManager.sendPacketToAllPlayers(new Packet92UpdateTimeSmall(this));
      }
      else
      {
         this.serverConfigManager.sendPacketToAllPlayers(new Packet4UpdateTime(this));
      }
   }

   public void sendWorldAgesToClient(EntityPlayerMP player)
   {
      if (Packet92UpdateTimeSmall.areAllWorldTotalTimesSuitable(this.worldServers))
      {
         player.sendPacket(new Packet92UpdateTimeSmall(this));
      }
      else
      {
         player.sendPacket(new Packet4UpdateTime(this));
      }
   }

   public void updateTimeLightAndEntities()
   {
      this.theProfiler.startSection("levels");

      if (this.tickCounter % 20 == 0)
      {
         this.sendWorldAgesToAllClientsInAllDimensions();
      }

      int var1;

      Integer[] ids = DimensionManager.getIDs(this.tickCounter % 200 == 0);
      for (var1 = 0; var1 < ids.length; var1++) {
         int id = ids[var1];
         long var2 = System.nanoTime();

         if (id == 0 || this.getAllowNether())
         {
            WorldServer var4 = DimensionManager.getWorld(id);
            this.theProfiler.startSection(var4.getWorldInfo().getWorldName());
            this.theProfiler.startSection("pools");
            var4.getWorldVec3Pool().clear();
            this.theProfiler.endSection();
            this.theProfiler.startSection("timeSync");
            this.theProfiler.endSection();
            this.theProfiler.startSection("tick");
            FMLCommonHandler.instance().onPreWorldTick(var4);
            CrashReport var5;

            try
            {
               var4.tick();
            }
            catch (Throwable var8)
            {
               var5 = CrashReport.makeCrashReport(var8, "Exception ticking world");
               var4.addWorldInfoToCrashReport(var5);
               throw new ReportedException(var5);
            }

            try
            {
               var4.updateEntities();
            }
            catch (Throwable var7)
            {
               var5 = CrashReport.makeCrashReport(var7, "Exception ticking world entities");
               var4.addWorldInfoToCrashReport(var5);
               throw new ReportedException(var5);
            }

            FMLCommonHandler.instance().onPostWorldTick(var4);
            this.theProfiler.endSection();
            this.theProfiler.startSection("tracker");
            var4.getEntityTracker().updateTrackedEntities();
            this.theProfiler.endSection();
            this.theProfiler.endSection();
         }

         worldTickTimes.get(id)[this.tickCounter % 100] = System.nanoTime() - var1;
      }

      this.theProfiler.endStartSection("dim_unloading");
      DimensionManager.unloadWorlds(worldTickTimes);

      this.theProfiler.endStartSection("connection");
      this.getNetworkThread().networkTick();
      this.theProfiler.endStartSection("players");
      this.serverConfigManager.sendPlayerInfoToAllPlayers(false);
      this.theProfiler.endStartSection("tickables");

      for (var1 = 0; var1 < this.tickables.size(); ++var1)
      {
         ((IUpdatePlayerListBox)this.tickables.get(var1)).update();
      }

      this.theProfiler.endSection();
   }

   public boolean getAllowNether()
   {
      return true;
   }

   public void func_82010_a(IUpdatePlayerListBox p_82010_1_)
   {
      this.playersOnline.add(p_82010_1_);
   }

   public static void main(String[] par0ArrayOfStr)
   {
      if (Minecraft.java_version_is_outdated)
      {
         System.out.println("Minecraft Is Too Easy requires Java 1.7 or later!");
      }
      else
      {
         StatList.nopInit();
         ILogAgent var1 = null;

         try
         {
            boolean var2 = !GraphicsEnvironment.isHeadless();
            String var3 = null;
            String var4 = ".";
            String var5 = null;
            boolean var6 = false;
            boolean var7 = false;
            int var8 = -1;

            for (int var9 = 0; var9 < par0ArrayOfStr.length; ++var9)
            {
               String var10 = par0ArrayOfStr[var9];
               String var11 = var9 == par0ArrayOfStr.length - 1 ? null : par0ArrayOfStr[var9 + 1];
               boolean var12 = false;

               if (!var10.equals("nogui") && !var10.equals("--nogui"))
               {
                  if (var10.equals("--port") && var11 != null)
                  {
                     var12 = true;

                     try
                     {
                        var8 = Integer.parseInt(var11);
                     }
                     catch (NumberFormatException var14)
                     {
                        ;
                     }
                  }
                  else if (var10.equals("--singleplayer") && var11 != null)
                  {
                     var12 = true;
                     var3 = var11;
                  }
                  else if (var10.equals("--universe") && var11 != null)
                  {
                     var12 = true;
                     var4 = var11;
                  }
                  else if (var10.equals("--world") && var11 != null)
                  {
                     var12 = true;
                     var5 = var11;
                  }
                  else if (var10.equals("--demo"))
                  {
                     var6 = true;
                  }
                  else if (var10.equals("--bonusChest"))
                  {
                     var7 = true;
                  }
               }
               else
               {
                  var2 = false;
               }

               if (var12)
               {
                  ++var9;
               }
            }

            DedicatedServer var16 = new DedicatedServer(new File(var4));
            var1 = var16.getLogAgent();

            if (var3 != null)
            {
               var16.setServerOwner(var3);
            }

            if (var5 != null)
            {
               var16.setFolderName(var5);
            }

            if (var8 >= 0)
            {
               var16.setServerPort(var8);
            }

            if (var6)
            {
               var16.setDemo(true);
            }

            if (var7)
            {
               var16.canCreateBonusChest(true);
            }

            if (var2)
            {
               var16.func_120011_ar();
            }

            var16.startServerThread();
            Runtime.getRuntime().addShutdownHook(new ThreadDedicatedServerMITE(var16));
         }
         catch (Exception var15)
         {
            if (var1 != null)
            {
               var1.logSevereException("Failed to start the net.minecraft server", var15);
            }
            else
            {
               Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to start the net.minecraft server", var15);
            }
         }
      }
   }

   public void startServerThread()
   {
      this.thread.start();
   }

   /**
    * Returns a File object from the specified string.
    */
   public File getFile(String par1Str)
   {
      return new File(this.getDataDirectory(), par1Str);
   }

   /**
    * Logs the message with a level of INFO.
    */
   public void logInfo(String par1Str)
   {
      this.getLogAgent().logInfo(par1Str);
   }

   /**
    * Logs the message with a level of WARN.
    */
   public void logWarning(String par1Str)
   {
      this.getLogAgent().logWarning(par1Str);
   }

   /**
    * Gets the worldServer by the given dimension.
    */
   public WorldServer worldServerForDimension(int par1)
   {
      WorldServer ret = DimensionManager.getWorld(par1);
      if (ret == null)
      {
         DimensionManager.initDimension(par1);
         ret = DimensionManager.getWorld(par1);
      }
      return ret;
   }

   /**
    * Returns the server's hostname.
    */
   public String getHostname()
   {
      return this.hostname;
   }

   /**
    * Never used, but "getServerPort" is already taken.
    */
   public int getPort()
   {
      return this.serverPort;
   }

   /**
    * Returns the server message of the day
    */
   public String getServerMOTD()
   {
      return this.getMOTD();
   }

   /**
    * Returns the server's Minecraft version as string.
    */
   public String getMinecraftVersion()
   {
      return "1.6.4";
   }

   /**
    * Returns the number of players currently on the server.
    */
   public int getCurrentPlayerCount()
   {
      return this.serverConfigManager.getCurrentPlayerCount();
   }

   /**
    * Returns the maximum number of players allowed on the server.
    */
   public int getMaxPlayers()
   {
      return this.serverConfigManager.getMaxPlayers();
   }

   /**
    * Returns an array of the usernames of all the connected players.
    */
   public String[] getAllUsernames()
   {
      return this.serverConfigManager.getAllUsernames();
   }

   /**
    * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
    */
   public String getPlugins()
   {
      return "";
   }

   public String executeCommand(String par1Str, boolean permission_override)
   {
      RConConsoleSource.consoleBuffer.resetLog();
      this.commandManager.executeCommand(RConConsoleSource.consoleBuffer, par1Str, permission_override);
      return RConConsoleSource.consoleBuffer.getChatBuffer();
   }

   /**
    * Returns true if debugging is enabled, false otherwise.
    */
   public boolean isDebuggingEnabled()
   {
      return false;
   }

   /**
    * Logs the error message with a level of SEVERE.
    */
   public void logSevere(String par1Str)
   {
      this.getLogAgent().logSevere(par1Str);
   }

   /**
    * If isDebuggingEnabled(), logs the message with a level of INFO.
    */
   public void logDebug(String par1Str)
   {
      if (this.isDebuggingEnabled())
      {
         this.getLogAgent().logInfo(par1Str);
      }
   }

   public String getServerModName()
   {
      return FMLCommonHandler.instance().getModName();
   }

   /**
    * Adds the server info, including from theWorldServer, to the crash report.
    */
   public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
   {
      par1CrashReport.getCategory().addCrashSectionCallable("Profiler Position", new CallableIsServerModded(this));

      if (this.worldServers != null && this.worldServers.length > 0 && this.worldServers[0] != null)
      {
         par1CrashReport.getCategory().addCrashSectionCallable("Vec3 Pool Size", new CallableServerProfiler(this));
      }

      if (this.serverConfigManager != null)
      {
         par1CrashReport.getCategory().addCrashSectionCallable("Player Count", new CallableServerMemoryStats(this));
      }

      return par1CrashReport;
   }

   /**
    * If par2Str begins with /, then it searches for commands, otherwise it returns players.
    */
   public List getPossibleCompletions(ICommandSender par1ICommandSender, String par2Str)
   {
      ArrayList var3 = new ArrayList();

      if (par2Str.startsWith("/")) {
         par2Str = par2Str.substring(1);
         boolean var10 = !par2Str.contains(" ");
         List var11 = this.commandManager.getPossibleCommands(par1ICommandSender, par2Str);

         if (var11 != null) {

             for (Object o : var11) {
                 String var13 = (String) o;

                 if (var10) {
                     var3.add("/" + var13);
                 } else {
                     var3.add(var13);
                 }
             }
         }

         return var3;
      }
      else
      {
         String[] var4 = par2Str.split(" ", -1);
         String var5 = var4[var4.length - 1];
         String[] var6 = this.serverConfigManager.getAllUsernames();
         int var7 = var6.length;

         for (int var8 = 0; var8 < var7; ++var8)
         {
            String var9 = var6[var8];

            if (CommandBase.doesStringStartWith(var5, var9))
            {
               var3.add(var9);
            }
         }

         return var3;
      }
   }

   /**
    * Gets mcServer.
    */
   public static MinecraftServer getServer()
   {
      return mcServer;
   }

   /**
    * Gets the name of this command sender (usually username, but possibly "Rcon")
    */
   public String getCommandSenderName()
   {
      return "Server";
   }

   public void sendChatToPlayer(ChatMessageComponent par1ChatMessageComponent)
   {
      this.getLogAgent().logInfo(par1ChatMessageComponent.toString());
   }

   /**
    * Returns true if the command sender is allowed to use the given command.
    */
   public boolean canCommandSenderUseCommand(int par1, String par2Str)
   {
      return true;
   }

   public ICommandManager getCommandManager()
   {
      return this.commandManager;
   }

   /**
    * Gets KeyPair instanced in MinecraftServer.
    */
   public KeyPair getKeyPair()
   {
      return this.serverKeyPair;
   }

   /**
    * Gets serverPort.
    */
   public int getServerPort()
   {
      return this.serverPort;
   }

   public void setServerPort(int par1)
   {
      this.serverPort = par1;
   }

   /**
    * Returns the username of the server owner (for integrated servers)
    */
   public String getServerOwner()
   {
      return this.serverOwner;
   }

   /**
    * Sets the username of the owner of this server (in the case of an integrated server)
    */
   public void setServerOwner(String par1Str)
   {
      this.serverOwner = par1Str;
   }

   public boolean isSinglePlayer()
   {
      return this.serverOwner != null;
   }

   public String getFolderName()
   {
      return this.folderName;
   }

   public void setFolderName(String par1Str)
   {
      this.folderName = par1Str;
   }

   public void setWorldName(String par1Str)
   {
      this.worldName = par1Str;
   }

   public String getWorldName()
   {
      return this.worldName;
   }

   public void setKeyPair(KeyPair par1KeyPair)
   {
      this.serverKeyPair = par1KeyPair;
   }

   public void setDifficultyForAllWorlds(int par1)
   {
      for (int var2 = 0; var2 < this.worldServers.length; ++var2)
      {
         WorldServer var3 = this.worldServers[var2];

         if (var3 != null)
         {
            var3.difficultySetting = 3;
            var3.setAllowedSpawnTypes(true, true);
         }
      }
   }

   protected boolean allowSpawnMonsters()
   {
      return true;
   }

   /**
    * Gets whether this is a demo or not.
    */
   public boolean isDemo()
   {
      return this.isDemo;
   }

   /**
    * Sets whether this is a demo or not.
    */
   public void setDemo(boolean par1)
   {
      this.isDemo = par1;
   }

   public void canCreateBonusChest(boolean par1)
   {
      this.enableBonusChest = par1;
   }

   public ISaveFormat getActiveAnvilConverter()
   {
      return this.anvilConverterForAnvilFile;
   }

   /**
    * WARNING : directly calls
    * getActiveAnvilConverter().deleteWorldDirectory(theWorldServer[0].getSaveHandler().getWorldDirectoryName());
    */
   public void deleteWorldAndStopServer()
   {
      this.worldIsBeingDeleted = true;
      this.getActiveAnvilConverter().flushCache();

      for (int var1 = 0; var1 < this.worldServers.length; ++var1)
      {
         WorldServer var2 = this.worldServers[var1];

         if (var2 != null)
         {
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(var2));
            var2.flush();
         }
      }

      this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getWorldDirectoryName());
      this.initiateShutdown();
   }

   public String getTexturePack()
   {
      return this.texturePack;
   }

   public void setTexturePack(String par1Str)
   {
      this.texturePack = par1Str;
   }

   public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
   {
      par1PlayerUsageSnooper.addData("whitelist_enabled", Boolean.valueOf(false));
      par1PlayerUsageSnooper.addData("whitelist_count", Integer.valueOf(0));
      par1PlayerUsageSnooper.addData("players_current", Integer.valueOf(this.getCurrentPlayerCount()));
      par1PlayerUsageSnooper.addData("players_max", Integer.valueOf(this.getMaxPlayers()));
      par1PlayerUsageSnooper.addData("players_seen", Integer.valueOf(this.serverConfigManager.getAvailablePlayerDat().length));
      par1PlayerUsageSnooper.addData("uses_auth", Boolean.valueOf(this.onlineMode));
      par1PlayerUsageSnooper.addData("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
      par1PlayerUsageSnooper.addData("run_time", Long.valueOf((getSystemTimeMillis() - par1PlayerUsageSnooper.func_130105_g()) / 60L * 1000L));
      par1PlayerUsageSnooper.addData("avg_tick_ms", Integer.valueOf((int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D)));
      par1PlayerUsageSnooper.addData("avg_sent_packet_count", Integer.valueOf((int)MathHelper.average(this.sentPacketCountArray)));
      par1PlayerUsageSnooper.addData("avg_sent_packet_size", Integer.valueOf((int)MathHelper.average(this.sentPacketSizeArray)));
      par1PlayerUsageSnooper.addData("avg_rec_packet_count", Integer.valueOf((int)MathHelper.average(this.receivedPacketCountArray)));
      par1PlayerUsageSnooper.addData("avg_rec_packet_size", Integer.valueOf((int)MathHelper.average(this.receivedPacketSizeArray)));
      int var2 = 0;

      for (int var3 = 0; var3 < this.worldServers.length; ++var3)
      {
         if (this.worldServers[var3] != null)
         {
            WorldServer var4 = this.worldServers[var3];
            WorldInfo var5 = var4.getWorldInfo();
            par1PlayerUsageSnooper.addData("world[" + var2 + "][dimension]", Integer.valueOf(var4.provider.dimensionId));
            par1PlayerUsageSnooper.addData("world[" + var2 + "][mode]", var5.getGameType());
            par1PlayerUsageSnooper.addData("world[" + var2 + "][difficulty]", Integer.valueOf(var4.difficultySetting));
            par1PlayerUsageSnooper.addData("world[" + var2 + "][hardcore]", Boolean.valueOf(var5.isHardcoreModeEnabled()));
            par1PlayerUsageSnooper.addData("world[" + var2 + "][generator_name]", var5.getTerrainType().getWorldTypeName());
            par1PlayerUsageSnooper.addData("world[" + var2 + "][generator_version]", Integer.valueOf(var5.getTerrainType().getGeneratorVersion()));
            par1PlayerUsageSnooper.addData("world[" + var2 + "][height]", Integer.valueOf(this.buildLimit));
            par1PlayerUsageSnooper.addData("world[" + var2 + "][chunks_loaded]", Integer.valueOf(var4.getChunkProvider().getLoadedChunkCount()));
            ++var2;
         }
      }

      par1PlayerUsageSnooper.addData("worlds", Integer.valueOf(var2));
   }

   public void addServerTypeToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
   {
      par1PlayerUsageSnooper.addData("singleplayer", Boolean.valueOf(this.isSinglePlayer()));
      par1PlayerUsageSnooper.addData("server_brand", this.getServerModName());
      par1PlayerUsageSnooper.addData("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
      par1PlayerUsageSnooper.addData("dedicated", Boolean.valueOf(this.isDedicatedServer()));
   }

   /**
    * Returns whether snooping is enabled or not.
    */
   public boolean isSnooperEnabled()
   {
      return true;
   }

   /**
    * This is checked to be 16 upon receiving the packet, otherwise the packet is ignored.
    */
   public int textureSize()
   {
      return 16;
   }

   public abstract boolean isDedicatedServer();

   public boolean isServerInOnlineMode()
   {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean par1)
   {
      this.onlineMode = par1;
   }

   public boolean getCanSpawnAnimals()
   {
      return this.canSpawnAnimals;
   }

   public void setCanSpawnAnimals(boolean par1)
   {
      this.canSpawnAnimals = par1;
   }

   public boolean getCanSpawnNPCs()
   {
      return this.canSpawnNPCs;
   }

   public void setCanSpawnNPCs(boolean par1)
   {
      this.canSpawnNPCs = par1;
   }

   public boolean isPVPEnabled()
   {
      return this.pvpEnabled;
   }

   public void setAllowPvp(boolean par1)
   {
      this.pvpEnabled = par1;
   }

   public boolean isFlightAllowed()
   {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean par1)
   {
      this.allowFlight = par1;
   }

   /**
    * Return whether command blocks are enabled.
    */
   public abstract boolean isCommandBlockEnabled();

   public String getMOTD()
   {
      return Minecraft.isInTournamentMode() ? "1.6.4-MITE Tournament Server " + this.getEntityWorld().getHourOfDayAMPM() : (mcServer.isDedicatedServer() ? this.motd + " (" + this.getEntityWorld().getHourOfDayAMPM() + ")" : this.motd);
   }

   public void setMOTD(String par1Str)
   {
      this.motd = par1Str;
   }

   public int getBuildLimit()
   {
      return this.buildLimit;
   }

   public void setBuildLimit(int par1)
   {
      this.buildLimit = par1;
   }

   public boolean isServerStopped()
   {
      return this.serverStopped;
   }

   public ServerConfigurationManager getConfigurationManager()
   {
      return this.serverConfigManager;
   }

   public void setConfigurationManager(ServerConfigurationManager par1ServerConfigurationManager)
   {
      this.serverConfigManager = par1ServerConfigurationManager;
   }

   /**
    * Sets the game type for all worlds.
    */
   public void setGameType(EnumGameType par1EnumGameType)
   {
      for (int var2 = 0; var2 < this.worldServers.length; ++var2)
      {
         getServer().worldServers[var2].getWorldInfo().setGameType(par1EnumGameType);
      }
   }

   public abstract NetworkListenThread getNetworkThread();

   public boolean serverIsInRunLoop()
   {
      return this.serverIsRunning;
   }

   public boolean getGuiEnabled()
   {
      return false;
   }

   /**
    * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
    */
   public abstract String shareToLAN(EnumGameType var1, boolean var2);

   public int getTickCounter()
   {
      return this.tickCounter;
   }

   public void enableProfiling()
   {
      this.startProfiling = true;
   }

   public PlayerUsageSnooper getPlayerUsageSnooper()
   {
      return this.usageSnooper;
   }

   /**
    * Return the position for this command sender.
    */
   public ChunkCoordinates getPlayerCoordinates()
   {
      return new ChunkCoordinates(0, 0, 0);
   }

   public World getEntityWorld()
   {
      return this.worldServers[0];
   }

   /**
    * Return the spawn protection area's size.
    */
   public int getSpawnProtectionSize()
   {
      return 16;
   }

   /**
    * Returns true if a player does not have permission to edit the block at the given coordinates.
    */
   public boolean isBlockProtected(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
   {
      return false;
   }

   public abstract ILogAgent getLogAgent();

   public abstract ILogAgent getAuxLogAgent();

   public void setForceGamemode(boolean par1)
   {
      this.isGamemodeForced = par1;
   }

   public boolean getForceGamemode()
   {
      return this.isGamemodeForced;
   }

   public Proxy getServerProxy()
   {
      return this.serverProxy;
   }

   /**
    * returns the difference, measured in milliseconds, between the current system time and midnight, January 1, 1970
    * UTC.
    */
   public static long getSystemTimeMillis()
   {
      return System.currentTimeMillis();
   }

   public int func_143007_ar()
   {
      return this.field_143008_E;
   }

   public void func_143006_e(int par1)
   {
      this.field_143008_E = par1;
   }

   /**
    * Gets the current player count, maximum player count, and player entity list.
    */
   public static ServerConfigurationManager getServerConfigurationManager(MinecraftServer par0MinecraftServer)
   {
      return par0MinecraftServer.serverConfigManager;
   }

   public static void sendPacketToAllPlayersOnServer(Packet packet)
   {
      Iterator i = mcServer.getConfigurationManager().playerEntityList.iterator();

      while (i.hasNext())
      {
         EntityPlayerMP player = (EntityPlayerMP)i.next();
         player.playerNetServerHandler.sendPacketToPlayer(packet);
      }
   }

   public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP) {}

   public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP) {}

   public abstract boolean isServerSideMappingEnabled();

   public void saveWorldMaps()
   {
      if (!treachery_detected)
      {
         this.getLogAgent().logInfo("Saving world maps...");

         for (int i = 0; i < this.worldServers.length; ++i)
         {
            WorldServer world = this.worldServers[i];

            if (world.world_map != null)
            {
               world.world_map.writeToFile();
            }
         }

         this.getLogAgent().logInfo("Finished saving world maps");
      }
   }

   public static void setTreacheryDetected()
   {
      if (!treachery_detected)
      {
         treachery_detected = true;

         if (treachery_shutdown_counter == 0)
         {
            treachery_shutdown_counter = 400;
         }
      }
   }

   public static void clearTreacheryDetected()
   {
      treachery_detected = false;
      treachery_shutdown_counter = 0;
   }

   public static boolean isPlayerHostingGame(EntityPlayer player)
   {
      return Minecraft.theMinecraft != null && Minecraft.theMinecraft.thePlayer != null ? Minecraft.theMinecraft.thePlayer == player || Minecraft.theMinecraft.thePlayer.entityId == player.entityId : false;
   }

   public boolean isZevimrgvOnServer()
   {
      return this.getConfigurationManager().isZevimrgvOnServer();
   }

   public void addTotalTimeForAllWorlds(int time_to_add)
   {
      for (int i = 0; i < this.worldServers.length; ++i)
      {
         WorldServer world = this.worldServers[i];
         long new_total_time = world.getTotalWorldTime() + (long)time_to_add;

         if (new_total_time < 0L)
         {
            new_total_time = 0L;
         }

         int new_time_of_day = (int)(new_total_time % 24000L);
         world.setTotalWorldTime(new_total_time);
      }

      this.sendWorldAgesToAllClientsInAllDimensions();
   }

   public String scramble(String s)
   {
      char[] chars = s.toCharArray();

      for (int i = 0; i < chars.length; ++i)
      {
         int c = chars[i];

         if (c >= 65 && c <= 90)
         {
            c = 90 - (c - 65);
         }
         else if (c >= 97 && c <= 122)
         {
            c = 122 - (c - 97);
         }
         else if (c >= 48 && c <= 57)
         {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   public final boolean hasPlayers(boolean must_be_alive, boolean must_not_be_ghosts)
   {
      Iterator i = this.getConfigurationManager().playerEntityList.iterator();
      EntityPlayerMP player;

      do
      {
         do
         {
            if (!i.hasNext())
            {
               return false;
            }

            player = (EntityPlayerMP)i.next();
         }
         while (must_be_alive && player.getHealth() <= 0.0F);
      }
      while (must_not_be_ghosts && (player.isGhost() || player.isZevimrgvInTournament()));

      return true;
   }

   public final boolean hasNoPlayersOfAnyKindConnected()
   {
      return this.getConfigurationManager().playerEntityList.isEmpty();
   }

   public final boolean hasPlayersOfAnyKindConnected()
   {
      return !this.hasNoPlayersOfAnyKindConnected();
   }

   public final boolean hasNonGhostPlayersConnected(boolean must_be_alive)
   {
      return this.hasPlayers(must_be_alive, true);
   }

   public final boolean hasOnlyGhostPlayersConnected()
   {
      return this.hasPlayersOfAnyKindConnected() && !this.hasNonGhostPlayersConnected(false);
   }

   public final boolean hasOnlyGhostsOrDeadPlayersConnected()
   {
      return this.hasPlayersOfAnyKindConnected() && !this.hasNonGhostPlayersConnected(true);
   }

   public final WorldServer getOverworld()
   {
      return this.worldServers[0];
   }

   public final WorldServer getUnderworld()
   {
      return this.worldServers[3];
   }

   public final WorldServer getNether()
   {
      return this.worldServers[1];
   }

   public final WorldServer getTheEnd()
   {
      return this.worldServers[2];
   }

   public final int getVillageConditions()
   {
      return this.getOverworld().getWorldInfo().getVillageConditions();
   }

   public static int getWorldIndexForDimensionId(int dimension_id)
   {
      if (dimension_id == 0)
      {
         return 0;
      }
      else if (dimension_id == -2)
      {
         return 3;
      }
      else if (dimension_id == -1)
      {
         return 1;
      }
      else if (dimension_id == 1)
      {
         return 2;
      }
      else
      {
         Minecraft.setErrorMessage("getWorldIndexForDimensionId: unable to map dimension id " + dimension_id + " to a world index");
         return 0;
      }
   }

   public static int getWorldDimensionIdFromIndex(int index)
   {
      if (index == 0)
      {
         return 0;
      }
      else if (index == 3)
      {
         return -2;
      }
      else if (index == 1)
      {
         return -1;
      }
      else if (index == 2)
      {
         return 1;
      }
      else
      {
         Minecraft.setErrorMessage("getWorldDimensionIdFromIndex: unable to map index " + index + " to a dimension id");
         return 0;
      }
   }

   static
   {
      TextureManager.unloadTextures();
      Entity.resetEntityIds();
   }
}
