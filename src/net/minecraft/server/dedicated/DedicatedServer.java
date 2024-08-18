package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSandStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.logging.ILogAgent;
import net.minecraft.logging.LogAgent;
import net.minecraft.mite.TournamentStanding;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.network.SoonestReconnectionTime;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MinecraftServerGuiMITE;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ThreadedFileIOBase;

public final class DedicatedServer extends MinecraftServer implements IServer {
   private final List pendingCommandList = Collections.synchronizedList(new ArrayList());
   private final ILogAgent field_98131_l;
   private RConThreadQuery theRConThreadQuery;
   private RConThreadMain theRConThreadMain;
   private PropertyManager settings;
   private boolean canSpawnStructures;
   private EnumGameType gameType;
   private NetworkListenThread networkThread;
   private boolean guiIsEnabled;
   private final ILogAgent achievements_log;
   private final ILogAgent performance_log;
   public static int shutdown_counter;
   public static EnumTournamentType tournament_type;
   public static String tournament_start_time;
   public static String tournament_notice_append;
   public static boolean tournament_won;
   public static DedicatedServer theDedicatedServer;
   public static List soonest_reconnection_times = new ArrayList();
   public static boolean disconnection_penalty_enabled = true;
   private boolean server_side_mapping_enabled;
   private static HashMap tournament_standings = new HashMap();
   public static HashMap players_kicked_for_depleted_time_shares = new HashMap();
   public static int allotted_time;
   private static int required_pyramid_height = 20;
   private static String newline = new String(System.getProperty("line.separator").getBytes());
   private final ILogAgent aux_log;
   private static boolean are_skills_enabled_in_settings_file;

   public DedicatedServer(File par1File) {
      super(par1File);
      theDedicatedServer = this;
      this.field_98131_l = new LogAgent("Minecraft-Server", (String)null, (new File(par1File, "server.log")).getAbsolutePath());
      this.achievements_log = new LogAgent("Achievements-Log", (String)null, (new File(par1File, "achievements.log")).getAbsolutePath());
      this.performance_log = new LogAgent("Performance-Log", (String)null, (new File(par1File, "performance.log")).getAbsolutePath());
      this.aux_log = new LogAgent(StringHelper.mirrorString("Hfhkrxrlfh-Olt"), (String)null, (new File(par1File, StringHelper.mirrorString("hfhkrxrlfh.olt"))).getAbsolutePath());
      new DedicatedServerSleepThread(this);
   }

   protected boolean startServer() throws IOException {
      DedicatedServerCommandThread var1 = new DedicatedServerCommandThread(this);
      var1.setDaemon(true);
      var1.start();
      this.getLogAgent().logInfo("Starting minecraft server version 1.6.4-MITE-HDS (R196)" + (Minecraft.inDevMode() ? " DEV" : ""));
      String os_arch = System.getProperty("os.arch");
      if (os_arch != null) {
         int JVM_bits = os_arch.contains("128") ? 128 : (os_arch.contains("64") ? 64 : (!os_arch.contains("86") && !os_arch.contains("32") ? 0 : 32));
         this.getLogAgent().logInfo("Using " + (JVM_bits == 0 ? os_arch : JVM_bits + "-bit") + " JVM" + (JVM_bits == 32 ? " (64-bit is recommended)" : ""));
      }

      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         this.getLogAgent().logWarning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      FMLCommonHandler.instance().onServerStart(this);

      this.getLogAgent().logInfo("Loading properties");
      this.settings = new PropertyManager(new File("server.properties"), this.getLogAgent(), "Minecraft server properties");
      String t_p = this.settings.getProperty(tE("glfimznvmg-kzhhdliw"));
      if (t_p != null) {
         if (t_p.equals(tE("alnyrvh"))) {
            this.setTournamentType(this.settings.getProperty("tournament-type"));
         } else {
            this.getLogAgent().logWarning(tE("Rmezorw glfimznvmg kzhhdliw!"));
         }
      }

      if (this.settings.getProperty("allotted-time") == null) {
         allotted_time = 432000;
      } else {
         allotted_time = this.settings.getIntProperty("allotted-time", 432000);
      }

      are_skills_enabled_in_settings_file = this.settings.getBooleanProperty("professions", false);
      are_skills_enabled_in_settings_file = false;
      if (this.isSinglePlayer()) {
         this.setHostname("127.0.0.1");
      } else {
         this.setOnlineMode(this.settings.getBooleanProperty("online-mode", true));
         this.setHostname(this.settings.getProperty("server-ip", ""));
      }

      this.setCanSpawnAnimals(this.settings.getBooleanProperty("spawn-animals", true));
      this.setCanSpawnNPCs(this.settings.getBooleanProperty("spawn-npcs", true));
      this.setAllowPvp(true);
      this.setAllowFlight(this.settings.getBooleanProperty("allow-flight", false));
      this.setTexturePack(this.settings.getProperty("texture-pack", ""));
      this.setMOTD(this.settings.getProperty("motd", "A 1.6.4-MITE Server"));
      this.setForceGamemode(this.settings.getBooleanProperty("force-gamemode", false));
      this.func_143006_e(this.settings.getIntProperty("player-idle-timeout", 0));
      this.settings.setProperty("difficulty", 3);
      this.canSpawnStructures = this.settings.getBooleanProperty("generate-structures", true);
      int var2 = this.settings.getIntProperty("gamemode", EnumGameType.SURVIVAL.getID());
      if (!Minecraft.inDevMode()) {
         var2 = EnumGameType.SURVIVAL.id;
      }

      this.gameType = WorldSettings.getGameTypeById(var2);
      this.getLogAgent().logInfo("Default game type: " + this.gameType);
      InetAddress var3 = null;
      if (this.getServerHostname().length() > 0) {
         var3 = InetAddress.getByName(this.getServerHostname());
      }

      if (this.getServerPort() < 0) {
         this.setServerPort(this.settings.getIntProperty("server-port", 25565));
      }

      this.getLogAgent().logInfo("Generating keypair");
      this.setKeyPair(CryptManager.createNewKeyPair());
      this.getLogAgent().logInfo("Starting Minecraft server on " + (this.getServerHostname().length() == 0 ? "*" : this.getServerHostname()) + ":" + this.getServerPort());

      try {
         this.networkThread = new DedicatedServerListenThread(this, var3, this.getServerPort());
      } catch (IOException var18) {
         this.getLogAgent().logWarning("**** FAILED TO BIND TO PORT!");
         this.getLogAgent().logWarningFormatted("The exception was: {0}", var18.toString());
         this.getLogAgent().logWarning("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.isServerInOnlineMode()) {
         this.getLogAgent().logWarning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         this.getLogAgent().logWarning("The server will make no attempt to authenticate usernames. Beware.");
         this.getLogAgent().logWarning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
         this.getLogAgent().logWarning("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      FMLCommonHandler.instance().onServerStarted();

      this.setConfigurationManager(new DedicatedPlayerList(this));
      if (this.getConfigurationManager().isWhiteListAllInclusive()) {
         this.getLogAgent().logInfo("White-list is ALL-INCLUSIVE");
      } else if (this.getConfigurationManager().getWhiteListedPlayers().size() == 0) {
         this.getLogAgent().logInfo("White-list contains NO USERNAMES!");
      } else {
         this.getLogAgent().logInfo("White-list contains " + this.getConfigurationManager().getWhiteListedPlayers().size() + " username(s)");
      }

      long var4 = System.nanoTime();
      if (this.getFolderName() == null) {
         this.setFolderName(this.settings.getProperty("level-name", "world"));
      }

      String var6 = this.settings.getProperty("level-seed", "");
      String var7 = "LARGEBIOMES";
      String var8 = this.settings.getProperty("generator-settings", "");
      long var9 = (new Random()).nextLong();
      if (var6.length() > 0) {
         try {
            long var11 = Long.parseLong(var6);
            if (var11 != 0L) {
               var9 = var11;
            }
         } catch (NumberFormatException var17) {
            var9 = (long)var6.hashCode();
         }
      }

      if (tournament_type == EnumTournamentType.open) {
         var9 = 2L;
      }

      WorldType var17 = WorldType.parseWorldType(var7);
      if (var17 == null) {
         var17 = WorldType.DEFAULT;
      }

      this.setBuildLimit(this.settings.getIntProperty("max-build-height", 256));
      this.setBuildLimit((this.getBuildLimit() + 8) / 16 * 16);
      this.setBuildLimit(MathHelper.clamp_int(this.getBuildLimit(), 64, 256));
      this.settings.setProperty("max-build-height", this.getBuildLimit());
      if (!FMLCommonHandler.instance().handleServerAboutToStart(this)) { return false; }
      this.getLogAgent().logInfo("Preparing level \"" + this.getFolderName() + "\"");
      this.loadAllWorlds(this.getFolderName(), this.getFolderName(), var9, var17, var8);
      this.getLogAgent().logInfo("World seed is " + this.worldServers[0].getSeed());
      long var12 = System.nanoTime() - var4;
      String var14 = String.format("%.3fs", (double)var12 / 1.0E9);
      this.getLogAgent().logInfo("Done (" + var14 + ")! For help, type \"help\" or \"?\"");
      if (this.settings.getBooleanProperty("enable-query", false)) {
         this.getLogAgent().logInfo("Starting GS4 status listener");
         this.theRConThreadQuery = new RConThreadQuery(this);
         this.theRConThreadQuery.startThread();
      }

      if (this.settings.getBooleanProperty("enable-rcon", false)) {
         this.getLogAgent().logInfo("Starting remote control listener");
         this.theRConThreadMain = new RConThreadMain(this);
         this.theRConThreadMain.startThread();
      }

      this.default_world_map_size = MathHelper.clamp_int(this.settings.getIntProperty("default-map-size", 4096), 256, 16384);
      if (isTournament()) {
         this.default_world_map_size = getTournamentArenaRadius() * 2;
      }

      this.setServerSideMapping(tournament_type != EnumTournamentType.open && this.settings.getBooleanProperty("enable-mapping", true));
      if (this.server_side_mapping_enabled) {
         this.getLogAgent().logInfo("Mapping enabled, default map size is " + this.default_world_map_size + "x" + this.default_world_map_size + " (" + this.default_world_map_size * this.default_world_map_size * 4 / 1024 / 1024 + "MB of memory)");
      } else {
         this.getLogAgent().logInfo("Mapping disabled");
      }

      return FMLCommonHandler.instance().handleServerStarting(this);
   }

   public boolean canStructuresSpawn() {
      return this.canSpawnStructures;
   }

   public EnumGameType getGameType() {
      if (!Minecraft.inDevMode()) {
         this.gameType = EnumGameType.SURVIVAL;
      }

      return this.gameType;
   }

   public int getDifficulty() {
      return 3;
   }

   public boolean isHardcore() {
      return this.settings.getBooleanProperty("hardcore", false);
   }

   public void updatePlayersFile() {
      File file = new File("players");

      try {
         FileWriter fw = new FileWriter(file);
         StringBuffer players = new StringBuffer();
         Iterator i = this.getConfigurationManager().playerEntityList.iterator();

         while(i.hasNext()) {
            EntityPlayerMP player = (EntityPlayerMP)i.next();
            players.append(player.username);
            if (i.hasNext()) {
               players.append(newline);
            }
         }

         fw.write(players.toString());
         fw.close();
      } catch (Exception var6) {
      }

   }

   public static void appendTournamentStandingsToFile(boolean server_is_shutting_down) {
      if (server_is_shutting_down) {
         getServer().getLogAgent().logInfo("Updating tournament standings file");
      }

      File file = new File("tournament_standings.txt");

      try {
         FileWriter fw = new FileWriter(file, true);
         StringBuffer sb = new StringBuffer();
         if (server_is_shutting_down) {
            sb.append("*** Server Shutting Down ***" + newline + newline);
         }

         WorldServer world = getServer().worldServerForDimension(0);
         String AMPM = world.getHourOfDayAMPM();
         String header;
         if (AMPM.equalsIgnoreCase("NOON")) {
            header = "Day " + world.getDayOfWorld() + ", Noon";
         } else if (AMPM.equalsIgnoreCase("MDNT")) {
            header = "End of Day " + (world.getDayOfWorld() - 1);
         } else {
            header = "Day " + world.getDayOfWorld() + ", " + world.getHourOfDayAMPM();
         }

         sb.append(header + newline + StringHelper.repeat("-", header.length()) + newline);
         Set usernames = tournament_standings.keySet();
         boolean is_avernite_in_list = false;
         Iterator i = usernames.iterator();

         while(i.hasNext()) {
            if ("avernite".equals((String)i.next())) {
               is_avernite_in_list = true;
               break;
            }
         }

         if (usernames.size() != 0 && (usernames.size() != 1 || !is_avernite_in_list)) {
            i = usernames.iterator();

            while(i.hasNext()) {
               String username = (String)i.next();
               if (!"avernite".equals(username)) {
                  TournamentStanding ts = (TournamentStanding)tournament_standings.get(username);
                  sb.append(ts.toString(username) + newline);
               }
            }
         } else {
            sb.append("(No players have joined yet)" + newline);
         }

         sb.append(newline);
         fw.append(sb.toString());
         fw.close();
      } catch (Exception var12) {
      }

   }

   public void tick() {
      super.tick();
      if (this.tickCounter % 1000 == 0) {
         this.getConfigurationManager().loadWhiteList();
         this.updatePlayersFile();
         if (this.getConfigurationManager().getCurrentPlayerCount() == 0) {
            System.gc();
         }

         this.logPerformance();
         if (tournament_type == EnumTournamentType.score) {
            appendTournamentStandingsToFile(false);
         }
      }

      boolean shutting_down_immediately = false;
      String username_to_kick;
      if (tournament_type != null && tournament_type.time_limit_in_days > 0 && shutdown_counter == 0 && this.worldServers[0].getDayOfWorld() > tournament_type.time_limit_in_days) {
         shutdown_counter = 200;
         username_to_kick = "The tournament is now over! Server is shutting down in 10 seconds.";
         this.getLogAgent().logInfo(username_to_kick);
         sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText("Notice: " + username_to_kick).setColor(EnumChatFormatting.YELLOW)));
         tournament_won = true;
      }

      if (this.tickCounter % 20 == 0) {
         username_to_kick = null;
         File file = new File("kick.txt");
         if (file.exists()) {
            try {
               BufferedReader bf = new BufferedReader(new FileReader(file));
               username_to_kick = bf.readLine();
               bf.close();
            } catch (Exception var8) {
            }

            if (username_to_kick != null && username_to_kick.length() > 0) {
               EntityPlayer player_to_kick = this.getConfigurationManager().getPlayerForUsername(username_to_kick);
               if (player_to_kick instanceof EntityPlayerMP) {
                  player_to_kick.getAsEntityPlayerMP().playerNetServerHandler.kickPlayerFromServer("Kicked by administrator");
                  file.delete();
               }
            }
         }

         String file_signal = null;
         file = new File("signal.txt");
         if (file.exists()) {
            try {
               BufferedReader bf = new BufferedReader(new FileReader(file));
               file_signal = bf.readLine();
               bf.close();
            } catch (Exception var7) {
            }

            if (file_signal != null) {
               boolean signal_processed = true;
               String msg;
               if (file_signal.equals("shutdown")) {
                  shutdown_counter = 400;
                  msg = "Server is shutting down in 20 seconds.";
                  this.getLogAgent().logInfo(msg);
                  sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText("Notice: " + msg).setColor(EnumChatFormatting.YELLOW)));
               } else if (file_signal.equals("shutdown immediately")) {
                  shutdown_counter = 1;
                  this.getLogAgent().logInfo("Server is shutting down immediately.");
                  shutting_down_immediately = true;
               } else if (file_signal.equals("shutdown for world backup")) {
                  shutdown_counter = 400;
                  msg = "Server is shutting down for world backup in 20 seconds.";
                  this.getLogAgent().logInfo(msg);
                  sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText("Notice: " + msg).setColor(EnumChatFormatting.YELLOW)));
               } else if (file_signal.equals("shutdown for update")) {
                  shutdown_counter = 400;
                  msg = "Server is shutting down for update in 20 seconds.";
                  this.getLogAgent().logInfo(msg);
                  sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText("Notice: " + msg).setColor(EnumChatFormatting.YELLOW)));
               } else {
                  signal_processed = false;
               }

               if (signal_processed) {
                  file.delete();
               }
            }
         }
      }

      if (shutdown_counter > 0) {
         if (this.getCurrentPlayerCount() == 0) {
            shutdown_counter = 1;
         }

         if (--shutdown_counter == 0) {
            this.save_world_maps_on_shutdown = this.server_side_mapping_enabled && !shutting_down_immediately;
            this.initiateShutdown();
            return;
         }

         if (tournament_won && shutdown_counter == 400) {
            sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText("Server will shutdown in 20 seconds.").setColor(EnumChatFormatting.YELLOW)));
         }
      }

   }

   protected void finalTick(CrashReport par1CrashReport) {
      while(this.isServerRunning()) {
         this.executePendingCommands();

         try {
            Thread.sleep(10L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }

   }

   public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport) {
      par1CrashReport = super.addServerInfoToCrashReport(par1CrashReport);
      par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new CallableType(this));
      par1CrashReport.getCategory().addCrashSectionCallable("Type", new CallableServerType(this));
      return par1CrashReport;
   }

   protected void systemExitNow() {
      ThreadedFileIOBase.reportErrorIfNotFinished();
      System.exit(0);
   }

   public void updateTimeLightAndEntities() {
      super.updateTimeLightAndEntities();
      this.executePendingCommands();
   }

   public boolean getAllowNether() {
      return this.settings.getBooleanProperty("allow-nether", true);
   }

   public boolean allowSpawnMonsters() {
      this.settings.getBooleanProperty("spawn-monsters", true);
      return true;
   }

   public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper) {
      par1PlayerUsageSnooper.addData("whitelist_enabled", this.getDedicatedPlayerList().isWhiteListEnabled());
      par1PlayerUsageSnooper.addData("whitelist_count", this.getDedicatedPlayerList().getWhiteListedPlayers().size());
      super.addServerStatsToSnooper(par1PlayerUsageSnooper);
   }

   public boolean isSnooperEnabled() {
      return this.settings.getBooleanProperty("snooper-enabled", true);
   }

   public void addPendingCommand(String par1Str, ICommandSender par2ICommandSender, boolean permission_override) {
      this.pendingCommandList.add(new ServerCommand(par1Str, par2ICommandSender, permission_override));
   }

   public void executePendingCommands() {
      while(!this.pendingCommandList.isEmpty()) {
         ServerCommand var1 = (ServerCommand)this.pendingCommandList.remove(0);
         this.getCommandManager().executeCommand(var1.sender, var1.command, var1.permission_override);
      }

   }

   public boolean isDedicatedServer() {
      return true;
   }

   public DedicatedPlayerList getDedicatedPlayerList() {
      return (DedicatedPlayerList)super.getConfigurationManager();
   }

   public NetworkListenThread getNetworkThread() {
      return this.networkThread;
   }

   public int getIntProperty(String par1Str, int par2) {
      return this.settings.getIntProperty(par1Str, par2);
   }

   public String getStringProperty(String par1Str, String par2Str) {
      return this.settings.getProperty(par1Str, par2Str);
   }

   public boolean getBooleanProperty(String par1Str, boolean par2) {
      return this.settings.getBooleanProperty(par1Str, par2);
   }

   public void setProperty(String par1Str, Object par2Obj) {
      this.settings.setProperty(par1Str, par2Obj);
   }

   public void saveProperties() {
      this.settings.saveProperties();
   }

   public String getSettingsFilename() {
      File var1 = this.settings.getPropertiesFile();
      return var1 != null ? var1.getAbsolutePath() : "No settings file";
   }

   public void func_120011_ar() {
      MinecraftServerGuiMITE.func_120016_a(this);
      this.guiIsEnabled = true;
   }

   public boolean getGuiEnabled() {
      return this.guiIsEnabled;
   }

   public String shareToLAN(EnumGameType par1EnumGameType, boolean par2) {
      return "";
   }

   public boolean isCommandBlockEnabled() {
      return this.settings.getBooleanProperty("enable-command-block", false);
   }

   public int getSpawnProtectionSize() {
      return this.settings.getIntProperty("spawn-protection", super.getSpawnProtectionSize());
   }

   public boolean isBlockProtected(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
      if (par1World.provider.dimensionId != 0) {
         return false;
      } else if (this.getDedicatedPlayerList().getOps().isEmpty()) {
         return false;
      } else if (this.getDedicatedPlayerList().isPlayerOpped(par5EntityPlayer.getCommandSenderName())) {
         return false;
      } else if (this.getSpawnProtectionSize() <= 0) {
         return false;
      } else {
         ChunkCoordinates var6 = par1World.getSpawnPoint();
         int var7 = MathHelper.abs_int(par2 - var6.posX);
         int var8 = MathHelper.abs_int(par4 - var6.posZ);
         int var9 = Math.max(var7, var8);
         return var9 <= this.getSpawnProtectionSize();
      }
   }

   public ILogAgent getLogAgent() {
      return this.field_98131_l;
   }

   public ILogAgent getAuxLogAgent() {
      return this.aux_log;
   }

   public int func_110455_j() {
      return 0;
   }

   public boolean areSkillsEnabled() {
      return areSkillsEnabledInSettingsFile();
   }

   public void func_143006_e(int par1) {
      super.func_143006_e(par1);
      this.settings.setProperty("player-idle-timeout", par1);
      this.saveProperties();
   }

   public ServerConfigurationManager getConfigurationManager() {
      return this.getDedicatedPlayerList();
   }

   public static boolean it() {
      return isTournament();
   }

   public void setTournamentType(String tournament_type) {
      if (tournament_type == null) {
         DedicatedServer.tournament_type = null;
      } else {
         DedicatedServer.tournament_type = EnumTournamentType.getTournamentType(tournament_type);
         if (DedicatedServer.tournament_type == null) {
            this.getLogAgent().logWarning("Invalid tournament type!");
         } else {
            if (this.settings.getProperty("required-pyramid-height") != null) {
               required_pyramid_height = this.settings.getIntProperty("required-pyramid-height", required_pyramid_height);
            }

            this.getLogAgent().logInfo("Server is running in TOURNAMENT MODE (" + tournament_type + ": " + getTournamentArenaRadius() * 2 + "x" + getTournamentArenaRadius() * 2 + ", " + getTournamentTimeLimitInDays() + " day limit)");
            if (DedicatedServer.tournament_type == EnumTournamentType.open) {
               tournament_start_time = this.settings.getProperty("tournament-start-time");
               if ("".equals(tournament_start_time)) {
                  tournament_start_time = null;
               }
            }

            tournament_notice_append = this.settings.getProperty("tournament-notice-append");
            if ("".equals(tournament_notice_append)) {
               tournament_notice_append = null;
            }

            this.getLogAgent().logInfo(getTournamentNotice());
         }

      }
   }

   public static String getTournamentObjective() {
      return EnumTournamentType.getTournamentObjective(tournament_type);
   }

   public static String getTournamentNotice() {
      String notice = "Notice:";
      if (tournament_type == EnumTournamentType.open) {
         if (tournament_start_time == null) {
            notice = notice + " The tournament hasn't started yet.";
         } else {
            notice = notice + " The tournament will be starting at " + tournament_start_time + ".";
         }
      } else {
         notice = notice + " This server is running in tournament mode.";
         notice = notice + " " + getTournamentObjective();
      }

      if (tournament_notice_append != null) {
         notice = notice + " " + tournament_notice_append;
      }

      return notice;
   }

   public static void checkForTournamentWinner(EntityPlayer player, EnumTournamentType tournament_type) {
      if (player.worldObj.isRemote) {
         Minecraft.setErrorMessage("checkForTournamentWinner: not supposed to be called on client");
      } else if (!tournament_won) {
         if (tournament_type == DedicatedServer.tournament_type) {
            tournament_won = true;
            shutdown_counter = 500;
            player.is_tournament_winner = true;
            player.capabilities.disableDamage = true;
            String victory_message = EnumTournamentType.getTournamentVictoryMessage(player, tournament_type);
            theDedicatedServer.getLogAgent().logInfo(victory_message);
            MinecraftServer.sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText(victory_message).setColor(EnumChatFormatting.YELLOW)));
         }
      }
   }

   public static void checkForTournamentCompletion() {
      if (tournament_type == EnumTournamentType.wonder && BlockSandStone.sacred_pyramid_completed) {
         tournament_won = true;
         shutdown_counter = 500;
         String victory_message = EnumTournamentType.getTournamentVictoryMessage((EntityPlayer)null, tournament_type);
         theDedicatedServer.getLogAgent().logInfo(victory_message);
         MinecraftServer.sendPacketToAllPlayersOnServer(new Packet3Chat(ChatMessageComponent.createFromText(victory_message).setColor(EnumChatFormatting.YELLOW)));
         File file = new File("pyramid");

         try {
            FileWriter fw = new FileWriter(file);
            fw.write(victory_message);
            fw.close();
         } catch (Exception var3) {
         }
      }

   }

   public static long getTickOfWorld() {
      return getServer().worldServers[0].getTotalWorldTime();
   }

   public static void setSoonestReconnectionTime(EntityPlayerMP player) {
      Iterator i = soonest_reconnection_times.iterator();

      SoonestReconnectionTime srt;
      do {
         if (!i.hasNext()) {
            soonest_reconnection_times.add(new SoonestReconnectionTime(player));
            return;
         }

         srt = (SoonestReconnectionTime)i.next();
      } while(!srt.username.equals(player.username));

      srt.update(player);
   }

   public static void clearSoonestReconnectionTime(EntityPlayerMP player) {
      Iterator i = soonest_reconnection_times.iterator();

      SoonestReconnectionTime srt;
      do {
         if (!i.hasNext()) {
            return;
         }

         srt = (SoonestReconnectionTime)i.next();
      } while(!srt.username.equals(player.username));

      i.remove();
   }

   public static SoonestReconnectionTime getSoonestReconnectionTime(String username) {
      Iterator i = soonest_reconnection_times.iterator();

      SoonestReconnectionTime srt;
      do {
         if (!i.hasNext()) {
            return null;
         }

         srt = (SoonestReconnectionTime)i.next();
      } while(!srt.username.equals(username));

      return srt;
   }

   public static void logAchievement(EntityPlayerMP player, StatBase stat_base) {
      if (theDedicatedServer != null && tournament_type != EnumTournamentType.open) {
         theDedicatedServer.achievements_log.logInfo(player.username + ": " + stat_base.getName());
      }

   }

   public void logPerformance() {
      if (theDedicatedServer != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("Server load: ");
         sb.append((int)(MinecraftServer.getServer().getLoadOnServer() * 100.0F));
         sb.append("% (");
         int num_players_online = this.getCurrentPlayerCount();
         sb.append(num_players_online);
         sb.append(num_players_online == 1 ? " player online, " : " players online, ");
         int num_chunks_active = 0;
         int num_entities_loaded = 0;
         int num_entity_livings_loaded = 0;
         int num_entity_items_loaded = 0;

         for(int i = 0; i < this.worldServers.length; ++i) {
            WorldServer world = this.worldServers[i];
            num_chunks_active += world.activeChunkSet.size();
            num_entities_loaded += world.getLoadedEntityList().size();
            Iterator iterator = world.getLoadedEntityList().iterator();

            while(iterator.hasNext()) {
               Entity entity = (Entity)iterator.next();
               if (entity instanceof EntityLiving) {
                  ++num_entity_livings_loaded;
               } else if (entity instanceof EntityItem) {
                  ++num_entity_items_loaded;
               }
            }
         }

         sb.append(num_chunks_active);
         sb.append(" chunks active, ");
         sb.append(num_entities_loaded);
         sb.append(" total entities loaded, ");
         sb.append(num_entity_livings_loaded);
         sb.append(" living entities loaded, ");
         sb.append(num_entity_items_loaded);
         sb.append(" item entities loaded, ");
         sb.append(this.getAverageTickTime());
         sb.append("ms average tick time, ");
         sb.append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L);
         sb.append("MB of ");
         sb.append(Runtime.getRuntime().totalMemory() / 1024L / 1024L);
         sb.append("MB memory in use, ");
         sb.append("time of day is ");
         sb.append(this.getEntityWorld().getHourOfDayAMPM());
         sb.append(")");
         this.performance_log.logInfo(sb.toString());
      }
   }

   public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP) {
      this.updatePlayersFile();
   }

   public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP) {
      this.updatePlayersFile();
      boolean player_is_exempt_from_disconnection_penalty = par1EntityPlayerMP.getHealth() <= 0.0F || par1EntityPlayerMP.is_disconnecting_while_in_bed || tournament_type == EnumTournamentType.open || par1EntityPlayerMP.isZevimrgvInTournament();
      if (player_is_exempt_from_disconnection_penalty) {
         clearSoonestReconnectionTime(par1EntityPlayerMP);
      } else {
         setSoonestReconnectionTime(par1EntityPlayerMP);
      }

   }

   public void setServerSideMapping(boolean server_side_mapping_enabled) {
      this.server_side_mapping_enabled = server_side_mapping_enabled;
   }

   public boolean isServerSideMappingEnabled() {
      return this.server_side_mapping_enabled;
   }

   public static TournamentStanding getOrCreateTournamentStanding(EntityPlayer player) {
      TournamentStanding ts = (TournamentStanding)tournament_standings.get(player.username);
      if (ts == null) {
         ts = new TournamentStanding();
         tournament_standings.put(player.username, ts);
      }

      return ts;
   }

   public static void incrementTournamentScoringCounter(EntityPlayer player, Item item_harvested) {
      if (player instanceof EntityPlayerMP && tournament_type == EnumTournamentType.score) {
         TournamentStanding ts = getOrCreateTournamentStanding(player);
         if (item_harvested == Item.copperNugget) {
            ++ts.copper_nuggets_harvested;
         } else if (item_harvested == Item.silverNugget) {
            ++ts.silver_nuggets_harvested;
         } else if (item_harvested == Item.goldNugget) {
            ++ts.gold_nuggets_harvested;
         } else if (item_harvested == Item.mithrilNugget) {
            ++ts.mithril_nuggets_harvested;
         } else if (item_harvested == Item.adamantiumNugget) {
            ++ts.adamantium_nuggets_harvested;
         } else if (item_harvested == Item.getItem(Block.oreCopper)) {
            ++ts.copper_ore_harvested;
         } else if (item_harvested == Item.getItem(Block.oreSilver)) {
            ++ts.silver_ore_harvested;
         } else if (item_harvested == Item.getItem(Block.oreGold)) {
            ++ts.gold_ore_harvested;
         } else if (item_harvested == Item.getItem(Block.oreIron)) {
            ++ts.iron_ore_harvested;
         } else if (item_harvested == Item.getItem(Block.oreMithril)) {
            ++ts.mithril_ore_harvested;
         } else if (item_harvested == Item.getItem(Block.oreAdamantium)) {
            ++ts.adamantium_ore_harvested;
         }

         updateTournamentScoreOnClient(player, true);
      }
   }

   public static void updateTournamentScoreOnClient(EntityPlayer player, boolean show_delta) {
      if (player instanceof EntityPlayerMP && tournament_type == EnumTournamentType.score) {
         player.getAsEntityPlayerMP().sendPacket((new Packet85SimpleSignal(EnumSignal.tournament_score)).setBoolean(show_delta).setInteger(getOrCreateTournamentStanding(player).calcScore()));
      }
   }

   public static void generatePrizeKeyFile(EntityPlayerMP player) {
      try {
         File dir = new File("prize_keys");
         if (!dir.exists()) {
            dir.mkdir();
         }

         FileWriter fw = new FileWriter(dir.getPath() + "/" + player.username + ".key");
         StringBuffer sb = new StringBuffer();
         int username_hash = 0;

         for(int i = 0; i < player.username.length(); ++i) {
            username_hash += player.username.charAt(i) * i;
         }

         Random random = new Random(player.worldObj.getSeed() * 37L + (long)(username_hash * 61));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         sb.append(random.nextInt(10));
         fw.write(sb.toString());
         fw.close();
         player.sendPacket((new Packet85SimpleSignal(EnumSignal.prize_key_code)).setInteger(Integer.valueOf(sb.toString())));
      } catch (Exception var6) {
      }

   }

   public static boolean isTournament() {
      return tournament_type != null;
   }

   public static boolean isTournament(EnumTournamentType tournament_type) {
      return DedicatedServer.tournament_type == tournament_type;
   }

   private static String tE(String s) {
      char[] chars = s.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         int c = chars[i];
         if (c >= 65 && c <= 90) {
            c = 90 - (c - 65);
         } else if (c >= 97 && c <= 122) {
            c = 122 - (c - 97);
         } else if (c >= 48 && c <= 57) {
            c = 57 - (c - 48);
         }

         chars[i] = (char)c;
      }

      return new String(chars);
   }

   public static int getRequiredPyramidHeight() {
      return Minecraft.inDevMode() ? 3 : required_pyramid_height;
   }

   public static boolean isTournamentThatHasSafeZone() {
      return tournament_type != null && tournament_type.has_safe_zone;
   }

   public static boolean isTournamentThatUsesAllottedTimes() {
      return tournament_type != null && tournament_type.uses_allotted_times;
   }

   public static boolean isTournamentThatAllowsAnimalSpawning() {
      return tournament_type != null && tournament_type.allows_animal_spawning;
   }

   public static boolean isTournamentThatPreventsTimeForwarding() {
      return tournament_type != null && tournament_type.prevents_time_forwarding;
   }

   public static int getTournamentArenaRadius() {
      return tournament_type == null ? 0 : tournament_type.arena_radius;
   }

   public static int getTournamentTimeLimitInDays() {
      return tournament_type == null ? 0 : tournament_type.time_limit_in_days;
   }

   public static boolean areSkillsEnabledInSettingsFile() {
      return are_skills_enabled_in_settings_file;
   }

   static {
      TextureManager.unloadTextures();
      Entity.resetEntityIds();
   }
}
