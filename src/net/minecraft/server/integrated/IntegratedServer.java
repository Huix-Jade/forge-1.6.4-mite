package net.minecraft.server.integrated;

import java.io.File;
import java.io.IOException;


import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;

import net.minecraft.crash.CrashReport;
import net.minecraft.logging.ILogAgent;
import net.minecraft.logging.LogAgent;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.StringHelper;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public final class IntegratedServer extends MinecraftServer
{
   /** The Minecraft instance. */
   private final Minecraft mc;
   private final WorldSettings theWorldSettings;
   private final ILogAgent serverLogAgent;

   /** Instance of IntegratedServerListenThread. */
   private IntegratedServerListenThread theServerListeningThread;
   private boolean isGamePaused;
   private boolean isPublic;
   private ThreadLanServerPing lanServerPing;
   private final ILogAgent aux_log;

   public IntegratedServer(Minecraft par1Minecraft, String par2Str, String par3Str, WorldSettings par4WorldSettings)
   {
      super(par1Minecraft.saves_dir_MITE);
      this.serverLogAgent = new LogAgent("Minecraft-Server", " [SERVER]", (new File(par1Minecraft.mcDataDir, "output-server.log")).getAbsolutePath());
      this.aux_log = new LogAgent(StringHelper.mirrorString("Hfhkrxrlfh-Olt"), (String)null, (new File(par1Minecraft.mcDataDir, StringHelper.mirrorString("hfhkrxrlfh.olt"))).getAbsolutePath());
      this.setServerOwner(par1Minecraft.getSession().getUsername());
      this.setFolderName(par2Str);
      this.setWorldName(par3Str);
      this.setDemo(par1Minecraft.isDemo());
      this.canCreateBonusChest(par4WorldSettings.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setConfigurationManager(new IntegratedPlayerList(this));
      this.mc = par1Minecraft;
      this.serverProxy = par1Minecraft.getProxy();
      this.theWorldSettings = par4WorldSettings;

      try
      {
         this.theServerListeningThread = new IntegratedServerListenThread(this);
      }
      catch (IOException var6)
      {
         throw new Error();
      }
   }

   protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str)
   {
      this.worldServers = new WorldServer[DimensionManager.world_size];
      this.convertMapIfNeeded(par1Str);
      ISaveHandler var7 = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);

      int i = 0;
      WorldServer overWorld = (isDemo() ? new DemoWorldServer(this, var7, par2Str, 0, theProfiler, getLogAgent()) : new WorldServer(this, var7, par2Str, 0, theWorldSettings, theProfiler, getLogAgent()));
      for (int dim : DimensionManager.getStaticDimensionIDs()) {
         WorldServer world = (dim == 0 ? overWorld :
                 new WorldServerMulti(this, var7, par2Str, dim, theWorldSettings, overWorld, theProfiler, getLogAgent()));
         worldServers[i] = world;
         world.addWorldAccess(new WorldManager(this, world));

         if (!this.isSinglePlayer()) {
            world.getWorldInfo().setGameType(this.getGameType());
         }
         ++i;
         MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
      }

      this.getConfigurationManager().setPlayerManager(new WorldServer[]{ overWorld });
      this.setDifficultyForAllWorlds(this.getDifficulty());
      this.initialWorldChunkLoad();
   }

   /**
    * Initialises the server and starts it.
    */
   protected boolean startServer() {
      this.serverLogAgent.logInfo("Starting integrated net.minecraft server version 1.6.4");
      this.setOnlineMode(false);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      this.serverLogAgent.logInfo("Generating keypair");
      this.setKeyPair(CryptManager.createNewKeyPair());
      if (!FMLCommonHandler.instance().handleServerAboutToStart(this)) { return false; }
      this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());

      if (this.getServerOwner().equals("Dedicated_Server"))
      {
         this.setMOTD("MITE 1.6.4 Server - " + this.worldServers[0].getWorldInfo().getWorldName());
      }
      else
      {
         this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
      }

      return FMLCommonHandler.instance().handleServerStarting(this);
   }

   /**
    * Main function called by run() every loop.
    */
   public void tick()
   {
      boolean var1 = this.isGamePaused;
      this.isGamePaused = this.theServerListeningThread.isGamePaused();

      if (!var1 && this.isGamePaused && !treachery_detected)
      {
         this.serverLogAgent.logInfo("Saving and pausing game...");
         this.getConfigurationManager().saveAllPlayerData();
         this.saveAllWorlds(false, false);
      }

      if (!this.isGamePaused)
      {
         super.tick();
      }
   }

   public void saveAllPlayersAndWorlds()
   {
      if (!treachery_detected)
      {
         this.serverLogAgent.logInfo("Saving all players and worlds...");
         this.getConfigurationManager().saveAllPlayerData();
         this.saveAllWorlds(false, false);
      }
   }

   public boolean canStructuresSpawn()
   {
      return false;
   }

   public EnumGameType getGameType()
   {
      return this.theWorldSettings.getGameType();
   }

   /**
    * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
    */
   public int getDifficulty()
   {
      return 3;
   }

   /**
    * Defaults to false.
    */
   public boolean isHardcore()
   {
      return this.theWorldSettings.getHardcoreEnabled();
   }

   protected File getDataDirectory()
   {
      return this.mc.mcDataDir;
   }

   public boolean isDedicatedServer()
   {
      return false;
   }

   /**
    * Gets the IntergratedServerListenThread.
    */
   public IntegratedServerListenThread getServerListeningThread()
   {
      return this.theServerListeningThread;
   }

   /**
    * Called on exit from the main run() loop.
    */
   protected void finalTick(CrashReport par1CrashReport)
   {
      this.mc.crashed(par1CrashReport);
   }

   /**
    * Adds the server info, including from theWorldServer, to the crash report.
    */
   public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport)
   {
      par1CrashReport = super.addServerInfoToCrashReport(par1CrashReport);
      par1CrashReport.getCategory().addCrashSectionCallable("Type", new CallableType3(this));
      par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new CallableIsModded(this));
      return par1CrashReport;
   }

   public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper)
   {
      super.addServerStatsToSnooper(par1PlayerUsageSnooper);
      par1PlayerUsageSnooper.addData("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
   }

   /**
    * Returns whether snooping is enabled or not.
    */
   public boolean isSnooperEnabled()
   {
      return Minecraft.getMinecraft().isSnooperEnabled();
   }

   /**
    * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
    */
   public String shareToLAN(EnumGameType par1EnumGameType, boolean par2)
   {
      if (Minecraft.isInTournamentMode())
      {
         return null;
      }
      else
      {
         if (!Minecraft.inDevMode())
         {
            par1EnumGameType = EnumGameType.SURVIVAL;
            par2 = false;
         }

         try
         {
            String var3 = this.theServerListeningThread.func_71755_c();
            this.getLogAgent().logInfo("Started on " + var3);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var3);
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(par1EnumGameType);
            this.getConfigurationManager().setCommandsAllowedForAll(par2);
            return var3;
         }
         catch (IOException var4)
         {
            return null;
         }
      }
   }

   public ILogAgent getLogAgent()
   {
      return this.serverLogAgent;
   }

   public ILogAgent getAuxLogAgent()
   {
      return this.aux_log;
   }

   /**
    * Saves all necessary data as preparation for stopping the server.
    */
   public void stopServer()
   {
      super.stopServer();

      if (this.lanServerPing != null)
      {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   /**
    * Sets the serverRunning variable to false, in order to get the server to shut down.
    */
   public void initiateShutdown()
   {
      super.initiateShutdown();

      if (this.lanServerPing != null)
      {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   /**
    * Returns true if this integrated server is open to LAN
    */
   public boolean getPublic()
   {
      return this.isPublic;
   }

   /**
    * Sets the game type for all worlds.
    */
   public void setGameType(EnumGameType par1EnumGameType)
   {
      this.getConfigurationManager().setGameType(par1EnumGameType);
   }

   /**
    * Return whether command blocks are enabled.
    */
   public boolean isCommandBlockEnabled()
   {
      return true;
   }

   public int func_110455_j()
   {
      return 4;
   }

   public boolean areSkillsEnabled()
   {
      return this.theWorldSettings.areSkillsEnabled();
   }

   public NetworkListenThread getNetworkThread()
   {
      return this.getServerListeningThread();
   }



   public boolean isServerSideMappingEnabled()
   {
      return false;
   }
}
