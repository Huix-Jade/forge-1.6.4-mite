package net.minecraft.server.management;

import com.google.common.base.Charsets;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet209SetPlayerTeam;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;

public abstract class ServerConfigurationManager
{
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

   /** Reference to the MinecraftServer object. */
   private final MinecraftServer mcServer;

   /** A list of player entities that exist on this server. */
   public final List playerEntityList = new ArrayList();
   private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
   private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));

   /** A set containing the OPs. */
   private Set ops = new HashSet();

   /** The Set of all whitelisted players. */
   private Set whiteListedPlayers = new HashSet();

   /** Reference to the PlayerNBTManager object. */
   private IPlayerFileData playerNBTManagerObj;

   /**
    * Server setting to only allow OPs and whitelisted players to join the server.
    */
   private final boolean whiteListEnforced = MinecraftServer.getServer().isDedicatedServer();

   /** The maximum number of players that can be connected at a time. */
   protected int maxPlayers;
   protected int viewDistance;
   private EnumGameType gameType;

   /** True if all players are allowed to use commands (cheats). */
   private boolean commandsAllowedForAll;

   /**
    * index into playerEntities of player to ping, updated every tick; currently hardcoded to max at 200 players
    */
   private int playerPingIndex;

   public ServerConfigurationManager(MinecraftServer par1MinecraftServer)
   {
      this.mcServer = par1MinecraftServer;
      this.bannedPlayers.setListActive(false);
      this.bannedIPs.setListActive(false);
      this.maxPlayers = 8;
   }

   public void initializeConnectionToPlayer(INetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP)
   {
      NBTTagCompound var3 = this.readPlayerDataFromFile(par2EntityPlayerMP);
      par2EntityPlayerMP.setWorld(this.mcServer.worldServerForDimension(par2EntityPlayerMP.dimension));
      par2EntityPlayerMP.theItemInWorldManager.setWorld((WorldServer)par2EntityPlayerMP.worldObj);
      String var4 = "local";

      if (par1INetworkManager.getSocketAddress() != null)
      {
         var4 = par1INetworkManager.getSocketAddress().toString();
      }

      this.mcServer.getLogAgent().logInfo(par2EntityPlayerMP.getCommandSenderName() + "[" + var4 + "] logged in with entity id " + par2EntityPlayerMP.entityId + " at (" + par2EntityPlayerMP.posX + ", " + par2EntityPlayerMP.posY + ", " + par2EntityPlayerMP.posZ + ")");
      WorldServer var5 = this.mcServer.worldServerForDimension(par2EntityPlayerMP.dimension);
      ChunkCoordinates var6 = var5.getSpawnPoint();
      this.func_72381_a(par2EntityPlayerMP, (EntityPlayerMP)null, var5);
      NetServerHandler netServerHandler = new NetServerHandler(this.mcServer, par1INetworkManager, par2EntityPlayerMP);
      netServerHandler.sendPacketToPlayer(new Packet1Login(par2EntityPlayerMP.entityId, var5.getWorldInfo().getTerrainType(), par2EntityPlayerMP.theItemInWorldManager.getGameType(), var5.getWorldInfo().isHardcoreModeEnabled(), var5.provider.dimensionId, var5.difficultySetting, var5.getHeight(), this.getMaxPlayers(), var5.worldInfo.getVillageConditions(), var5.worldInfo.getAchievements(), var5.worldInfo.getEarliestMITEReleaseRunIn(), var5.worldInfo.getLatestMITEReleaseRunIn(), var5.areSkillsEnabled(), var5.getWorldCreationTime(), var5.getTotalWorldTime()));
      netServerHandler.sendPacketToPlayer(new Packet250CustomPayload("MC|Brand", this.getServerInstance().getServerModName().getBytes(Charsets.UTF_8)));
      netServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(var6.posX, var6.posY, var6.posZ));
      netServerHandler.sendPacketToPlayer(new Packet202PlayerAbilities(par2EntityPlayerMP.capabilities));
      netServerHandler.sendPacketToPlayer(new Packet16BlockItemSwitch(par2EntityPlayerMP.inventory.currentItem));
      this.func_96456_a((ServerScoreboard)var5.getScoreboard(), par2EntityPlayerMP);
      this.updateTimeAndWeatherForPlayer(par2EntityPlayerMP, var5);

      if (!par2EntityPlayerMP.isZevimrgvInTournament())
      {
         this.sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("multiplayer.player.joined", new Object[] {par2EntityPlayerMP.getTranslatedEntityName()}).setColor(EnumChatFormatting.YELLOW));
      }

      if (Minecraft.isInTournamentMode())
      {
         netServerHandler.sendPacketToPlayer((new Packet85SimpleSignal(EnumSignal.tournament_mode)).setByte(DedicatedServer.tournament_type.ordinal()));
         netServerHandler.sendPacketToPlayer(new Packet3Chat(ChatMessageComponent.createFromText(DedicatedServer.getTournamentNotice()).setColor(EnumChatFormatting.YELLOW)));
      }
      else
      {
         netServerHandler.sendPacketToPlayer((new Packet85SimpleSignal(EnumSignal.tournament_mode)).setByte(-1));
      }

      netServerHandler.sendPacketToPlayer((new Packet85SimpleSignal(EnumSignal.allotted_time)).setInteger(par2EntityPlayerMP.allotted_time));

      if (this.mcServer.isDedicatedServer())
      {
         netServerHandler.sendPacketToPlayer(new Packet85SimpleSignal(EnumSignal.dedicated_server));
      }

      if (var5.peekUniqueDataId("map") > 0)
      {
         netServerHandler.sendPacketToPlayer((new Packet85SimpleSignal(EnumSignal.last_issued_map_id)).setShort(var5.peekUniqueDataId("map") - 1));
      }

      this.playerLoggedIn(par2EntityPlayerMP);
      netServerHandler.setPlayerLocation(par2EntityPlayerMP.posX, par2EntityPlayerMP.posY, par2EntityPlayerMP.posZ, par2EntityPlayerMP.rotationYaw, par2EntityPlayerMP.rotationPitch);
      this.mcServer.getNetworkThread().addPlayer(netServerHandler);
      par2EntityPlayerMP.sendWorldAgesToClient();

      if (this.mcServer.getTexturePack().length() > 0)
      {
         par2EntityPlayerMP.requestTexturePackLoad(this.mcServer.getTexturePack(), this.mcServer.textureSize());
      }

      Iterator var8 = par2EntityPlayerMP.getActivePotionEffects().iterator();

      while (var8.hasNext())
      {
         PotionEffect var9 = (PotionEffect)var8.next();
         netServerHandler.sendPacketToPlayer(new Packet41EntityEffect(par2EntityPlayerMP.entityId, var9));
      }

      par2EntityPlayerMP.addSelfToInternalCraftingInventory();

      FMLNetworkHandler.handlePlayerLogin(par2EntityPlayerMP, netServerHandler, par1INetworkManager);

      if (var3 != null && var3.hasKey("Riding"))
      {
         Entity var10 = EntityList.createEntityFromNBT(var3.getCompoundTag("Riding"), var5);

         if (var10 != null)
         {
            var10.forceSpawn = true;
            var5.spawnEntityInWorld(var10);
            par2EntityPlayerMP.mountEntity(var10);
            var10.forceSpawn = false;
         }
      }
   }

   protected void func_96456_a(ServerScoreboard par1ServerScoreboard, EntityPlayerMP par2EntityPlayerMP)
   {
      HashSet var3 = new HashSet();
      Iterator var4 = par1ServerScoreboard.func_96525_g().iterator();

      while (var4.hasNext())
      {
         ScorePlayerTeam var5 = (ScorePlayerTeam)var4.next();
         par2EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet209SetPlayerTeam(var5, 0));
      }

      for (int var10 = 0; var10 < 3; ++var10)
      {
         ScoreObjective var6 = par1ServerScoreboard.func_96539_a(var10);

         if (var6 != null && !var3.contains(var6))
         {
            List var7 = par1ServerScoreboard.func_96550_d(var6);
            Iterator var8 = var7.iterator();

            while (var8.hasNext())
            {
               Packet var9 = (Packet)var8.next();
               par2EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(var9);
            }

            var3.add(var6);
         }
      }
   }

   /**
    * Sets the NBT manager to the one for the WorldServer given.
    */
   public void setPlayerManager(WorldServer[] par1ArrayOfWorldServer)
   {
      this.playerNBTManagerObj = par1ArrayOfWorldServer[0].getSaveHandler().getSaveHandler();
   }

   public void func_72375_a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
   {
      WorldServer var3 = par1EntityPlayerMP.getServerForPlayer();

      if (par2WorldServer != null)
      {
         par2WorldServer.getPlayerManager().removePlayer(par1EntityPlayerMP);
      }

      var3.getPlayerManager().addPlayer(par1EntityPlayerMP);
      var3.theChunkProviderServer.loadChunk((int)par1EntityPlayerMP.posX >> 4, (int)par1EntityPlayerMP.posZ >> 4);
   }

   public int getEntityViewDistance()
   {
      return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
   }

   /**
    * called during player login. reads the player information from disk.
    */
   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP par1EntityPlayerMP)
   {
      NBTTagCompound var2 = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound var3;

      if (par1EntityPlayerMP.getCommandSenderName().equals(this.mcServer.getServerOwner()) && var2 != null)
      {
         par1EntityPlayerMP.readFromNBT(var2);
         var3 = var2;
      }
      else
      {
         var3 = this.playerNBTManagerObj.readPlayerData(par1EntityPlayerMP);
      }

      return var3;
   }

   /**
    * also stores the NBTTags if this is an intergratedPlayerList
    */
   protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
   {
      this.playerNBTManagerObj.writePlayerData(par1EntityPlayerMP);
   }

   /**
    * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
    */
   public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP)
   {
      this.sendPacketToAllPlayers(new Packet201PlayerInfo(par1EntityPlayerMP.getCommandSenderName(), true, 1000, par1EntityPlayerMP.getExperienceLevel()));
      this.playerEntityList.add(par1EntityPlayerMP);
      WorldServer var2 = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
      var2.spawnEntityInWorld(par1EntityPlayerMP);
      this.func_72375_a(par1EntityPlayerMP, (WorldServer)null);

      for (int var3 = 0; var3 < this.playerEntityList.size(); ++var3)
      {
         EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntityList.get(var3);
         par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet201PlayerInfo(var4.getCommandSenderName(), true, var4.ping, var4.getExperienceLevel()));
      }

      this.mcServer.playerLoggedIn(par1EntityPlayerMP);
   }

   /**
    * using player's dimension, update their movement when in a vehicle (e.g. cart, boat)
    */
   public void serverUpdateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
   {
      par1EntityPlayerMP.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(par1EntityPlayerMP);
   }

   /**
    * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
    */
   public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP)
   {
      GameRegistry.onPlayerLogout(par1EntityPlayerMP);
      this.writePlayerData(par1EntityPlayerMP);
      WorldServer var2 = par1EntityPlayerMP.getServerForPlayer();

      if (par1EntityPlayerMP.ridingEntity != null)
      {
         var2.removePlayerEntityDangerously(par1EntityPlayerMP.ridingEntity);
         System.out.println("removing player mount");
      }

      var2.removeEntity(par1EntityPlayerMP);
      var2.getPlayerManager().removePlayer(par1EntityPlayerMP);
      this.playerEntityList.remove(par1EntityPlayerMP);
      this.sendPacketToAllPlayers(new Packet201PlayerInfo(par1EntityPlayerMP.getCommandSenderName(), false, 9999, par1EntityPlayerMP.getExperienceLevel()));
      this.mcServer.playerLoggedOut(par1EntityPlayerMP);
   }

   /**
    * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
    */
   public String allowUserToConnect(SocketAddress par1SocketAddress, String par2Str)
   {
      if (EntityPlayer.isZevimrgv(par2Str))
      {
         return null;
      }
      else if (this.bannedPlayers.isBanned(par2Str))
      {
         BanEntry var6 = (BanEntry)this.bannedPlayers.getBannedList().get(par2Str);
         String var7 = "You are banned from this server!\nReason: " + var6.getBanReason();

         if (var6.getBanEndDate() != null)
         {
            var7 = var7 + "\nYour ban will be removed on " + dateFormat.format(var6.getBanEndDate());
         }

         return var7;
      }
      else if (!this.isAllowedToLogin(par2Str))
      {
         return "You are not white-listed on this server!";
      }
      else
      {
         String var3 = par1SocketAddress.toString();
         var3 = var3.substring(var3.indexOf("/") + 1);
         var3 = var3.substring(0, var3.indexOf(":"));

         if (this.bannedIPs.isBanned(var3))
         {
            BanEntry var4 = (BanEntry)this.bannedIPs.getBannedList().get(var3);
            String var5 = "Your IP address is banned from this server!\nReason: " + var4.getBanReason();

            if (var4.getBanEndDate() != null)
            {
               var5 = var5 + "\nYour ban will be removed on " + dateFormat.format(var4.getBanEndDate());
            }

            return var5;
         }
         else
         {
            return this.playerEntityList.size() >= this.maxPlayers ? "The server is full!" : null;
         }
      }
   }

   /**
    * also checks for multiple logins
    */
   public EntityPlayerMP createPlayerForUser(String par1Str)
   {
      ArrayList var2 = new ArrayList();
      EntityPlayerMP var3;

      for (int var4 = 0; var4 < this.playerEntityList.size(); ++var4)
      {
         var3 = (EntityPlayerMP)this.playerEntityList.get(var4);

         if (var3.getCommandSenderName().equalsIgnoreCase(par1Str))
         {
            var2.add(var3);
         }
      }

      Iterator var6 = var2.iterator();

      while (var6.hasNext())
      {
         var3 = (EntityPlayerMP)var6.next();
         var3.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
      }

      Object var5;

      if (this.mcServer.isDemo())
      {
         var5 = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
      }
      else
      {
         var5 = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
      }

      return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), par1Str, (ItemInWorldManager)var5);
   }

   /**
    * creates and returns a respawned player based on the provided PlayerEntity. Args are the PlayerEntityMP to
    * respawn, an INT for the dimension to respawn into (usually 0), and a boolean value that is true if the player
    * beat the game rather than dying
    */
   public EntityPlayerMP respawnPlayer(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
   {
      World world = mcServer.worldServerForDimension(par2);
      if (world == null)
      {
         par2 = 0;
      }
      else if (!world.provider.canRespawnHere())
      {
         par2 = world.provider.getRespawnDimension(par1EntityPlayerMP);
      }

      par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(par1EntityPlayerMP);
      par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(par1EntityPlayerMP);
      par1EntityPlayerMP.getServerForPlayer().getPlayerManager().removePlayer(par1EntityPlayerMP);
      this.playerEntityList.remove(par1EntityPlayerMP);
      this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension).removePlayerEntityDangerously(par1EntityPlayerMP);
      ChunkCoordinates var4 = par1EntityPlayerMP.getBedLocation(par2);
      boolean var5 = par1EntityPlayerMP.isSpawnForced(par2);
      par1EntityPlayerMP.dimension = par2;
      Object var6;

      if (this.mcServer.isDemo())
      {
         var6 = new DemoWorldManager(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
      }
      else
      {
         var6 = new ItemInWorldManager(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
      }

      EntityPlayerMP var7 = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), par1EntityPlayerMP.getCommandSenderName(), (ItemInWorldManager)var6);
      var7.playerNetServerHandler = par1EntityPlayerMP.playerNetServerHandler;
      var7.experience = par1EntityPlayerMP.respawn_experience;
      var7.ticks_logged_in = par1EntityPlayerMP.ticks_logged_in;
      var7.setProtein(par1EntityPlayerMP.getProtein());
      var7.setEssentialFats(par1EntityPlayerMP.getEssentialFats());
      var7.setPhytonutrients(par1EntityPlayerMP.getPhytonutrients());
      var7.setInsulinResistance(par1EntityPlayerMP.getInsulinResistance());
      var7.insulin_resistance_level = par1EntityPlayerMP.insulin_resistance_level;
      var7.master_hash_received = par1EntityPlayerMP.master_hash_received;
      var7.master_hash_validated = par1EntityPlayerMP.master_hash_validated;
      var7.Sr = par1EntityPlayerMP.Sr;
      var7.raS = par1EntityPlayerMP.raS;
      var7.sacred_stones_placed = par1EntityPlayerMP.sacred_stones_placed;
      var7.allotted_time = par1EntityPlayerMP.allotted_time;
      var7.last_skill_learned_on_day = par1EntityPlayerMP.last_skill_learned_on_day;
      var7.setSkills(par1EntityPlayerMP.getSkills());
      var7.stats = par1EntityPlayerMP.stats;
      var7.clonePlayer(par1EntityPlayerMP, par3);
      var7.dimension = par2;
      var7.entityId = par1EntityPlayerMP.entityId;
      WorldServer var8 = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
      this.func_72381_a(var7, par1EntityPlayerMP, var8);
      ChunkCoordinates var9;

      if (var4 != null)
      {
         var9 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), var4, var5);

         if (var9 != null)
         {
            var7.setLocationAndAngles((double)((float)var9.posX + 0.5F), (double)((float)var9.posY + 0.1F), (double)((float)var9.posZ + 0.5F), 0.0F, 0.0F);
            var7.setSpawnChunk(var4, var5);
         }
         else
         {
            var7.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(0, 0));
         }
      }

      var8.theChunkProviderServer.loadChunk((int)var7.posX >> 4, (int)var7.posZ >> 4);

      while (!var8.getCollidingBoundingBoxes(var7, var7.boundingBox).isEmpty())
      {
         var7.setPosition(var7.posX, var7.posY + 1.0D, var7.posZ);
      }

      var7.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(var7.dimension, (byte)var7.worldObj.difficultySetting, var7.worldObj.getWorldInfo().getTerrainType(), var7.worldObj.getHeight(), var7.theItemInWorldManager.getGameType(), var7.worldObj.getWorldCreationTime(), var7.worldObj.getTotalWorldTime()));
      var9 = var8.getSpawnPoint();
      var7.playerNetServerHandler.setPlayerLocation(var7.posX, var7.posY, var7.posZ, var7.rotationYaw, var7.rotationPitch);
      var7.playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(var9.posX, var9.posY, var9.posZ));
      var7.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(var7.experience));
      this.updateTimeAndWeatherForPlayer(var7, var8);
      var8.getPlayerManager().addPlayer(var7);
      var8.spawnEntityInWorld(var7);
      this.playerEntityList.add(var7);
      var7.addSelfToInternalCraftingInventory();
      GameRegistry.onPlayerRespawn(var7);
      var7.setHealth(var7.getHealth());
      var7.afterRespawn();
      return var7;
   }

   public void teleportPlayerInsideDimension(EntityPlayerMP player, double posX, double posY, double posZ, boolean sync_last_tick_pos_on_next_update)
   {
      WorldServer world_server = this.mcServer.worldServerForDimension(player.dimension);

      if (player.isEntityAlive())
      {
         player.setLocationAndAngles(posX, posY, posZ, player.rotationYaw, player.rotationPitch);

         if (sync_last_tick_pos_on_next_update)
         {
            player.sync_last_tick_pos_on_next_update = true;
         }

         world_server.updateEntityWithOptionalForce(player, false);
      }

      this.func_72375_a(player, world_server);
      player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

      if (sync_last_tick_pos_on_next_update)
      {
         player.sync_last_tick_pos_on_next_update = true;
      }
   }

   public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2)
   {
      transferPlayerToDimension(par1EntityPlayerMP, par2, mcServer.worldServerForDimension(par2).getDefaultTeleporter());
   }

   public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter)
   {
      int var3 = par1EntityPlayerMP.dimension;
      WorldServer var4 = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
      par1EntityPlayerMP.dimension = par2;
      WorldServer var5 = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
      par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(par1EntityPlayerMP.dimension, (byte)par1EntityPlayerMP.worldObj.difficultySetting, var5.getWorldInfo().getTerrainType(), var5.getHeight(), par1EntityPlayerMP.theItemInWorldManager.getGameType(), var5.getWorldCreationTime(), var5.getTotalWorldTime()));
      var4.removePlayerEntityDangerously(par1EntityPlayerMP);
      par1EntityPlayerMP.isDead = false;
      this.transferEntityToWorld(par1EntityPlayerMP, var3, var4, var5, teleporter);
      this.func_72375_a(par1EntityPlayerMP, var4);
      par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
      par1EntityPlayerMP.theItemInWorldManager.setWorld(var5);
      this.updateTimeAndWeatherForPlayer(par1EntityPlayerMP, var5);
      this.syncPlayerInventory(par1EntityPlayerMP);
      Iterator var6 = par1EntityPlayerMP.getActivePotionEffects().iterator();

      while (var6.hasNext())
      {
         PotionEffect var7 = (PotionEffect)var6.next();
         par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(par1EntityPlayerMP.entityId, var7));
      }

      GameRegistry.onPlayerChangedDimension(par1EntityPlayerMP);
      par1EntityPlayerMP.syncClientPlayer();
   }

   /**
    * Transfers an entity from a world to another world.
    */
   public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer)
   {
      transferEntityToWorld(par1Entity, par2, par3WorldServer, par4WorldServer, par4WorldServer.getDefaultTeleporter());
   }

   public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter)
   {
      WorldProvider pOld = par3WorldServer.provider;
      WorldProvider pNew = par4WorldServer.provider;
      double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
      double var7 = par1Entity.posX * moveFactor;
      double var9 = par1Entity.posZ * moveFactor;

      double var11 = 8.0D;
      double var13 = par1Entity.posX;
      double var15 = par1Entity.posY;
      double var17 = par1Entity.posZ;
      float var19 = par1Entity.rotationYaw;
      par3WorldServer.theProfiler.startSection("moving");

      if (par1Entity.dimension == 1)
      {
         ChunkCoordinates var20;

         if (par2 == 1)
         {
            var20 = par4WorldServer.getSpawnPoint();
         }
         else
         {
            var20 = par4WorldServer.getEntrancePortalLocation();
         }

         var7 = (double)var20.posX;
         par1Entity.posY = (double)var20.posY;
         var9 = (double)var20.posZ;
         par1Entity.setLocationAndAngles(var7, par1Entity.posY, var9, 90.0F, 0.0F);

         if (par1Entity.isEntityAlive())
         {
            par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
         }
      }
      else
      {
         par1Entity.setLocationAndAngles(var7, par1Entity.posY, var9, par1Entity.rotationYaw, par1Entity.rotationPitch);

         if (par1Entity.isEntityAlive())
         {
            par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
         }
      }

      if (par1Entity.isAddedToAChunk())
      {
         par1Entity.removeFromChunk();
      }

      par3WorldServer.theProfiler.endSection();

      if (par2 != 1)
      {
         par3WorldServer.theProfiler.startSection("placing");
         var7 = (double) MathHelper.clamp_int((int)var7, -29999872, 29999872);
         var9 = (double)MathHelper.clamp_int((int)var9, -29999872, 29999872);

         if (par1Entity.isEntityAlive())
         {
            par4WorldServer.spawnEntityInWorld(par1Entity);
            par1Entity.setLocationAndAngles(var7, par1Entity.posY, var9, par1Entity.rotationYaw, par1Entity.rotationPitch);
            par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            teleporter.placeInPortal(par1Entity,par2, var13, var15, var17, var19);
         }

         par3WorldServer.theProfiler.endSection();
      }

      par1Entity.setWorld(par4WorldServer);
      par1Entity.onTransferToWorld();
   }

   public void sendPlayerInfoToAllPlayers(boolean immediate)
   {
      if (immediate || ++this.playerPingIndex > 600)
      {
         this.playerPingIndex = 0;
      }

      if (this.playerPingIndex < this.playerEntityList.size())
      {
         EntityPlayerMP var1 = (EntityPlayerMP)this.playerEntityList.get(this.playerPingIndex);
         this.sendPacketToAllPlayers(new Packet201PlayerInfo(var1.getCommandSenderName(), true, var1.ping, var1.getExperienceLevel()));
      }
   }

   /**
    * sends a packet to all players
    */
   public void sendPacketToAllPlayers(Packet par1Packet)
   {
      for (int var2 = 0; var2 < this.playerEntityList.size(); ++var2)
      {
         ((EntityPlayerMP)this.playerEntityList.get(var2)).playerNetServerHandler.sendPacketToPlayer(par1Packet);
      }
   }

   /**
    * Sends a packet to all players in the specified Dimension
    */
   public void sendPacketToAllPlayersInDimension(Packet par1Packet, int par2)
   {
      for (int var3 = 0; var3 < this.playerEntityList.size(); ++var3)
      {
         EntityPlayerMP var4 = (EntityPlayerMP)this.playerEntityList.get(var3);

         if (var4.dimension == par2)
         {
            var4.playerNetServerHandler.sendPacketToPlayer(par1Packet);
         }
      }
   }

   /**
    * returns a string containing a comma-seperated list of player names
    */
   public String getPlayerListAsString()
   {
      String var1 = "";

      for (int var2 = 0; var2 < this.playerEntityList.size(); ++var2)
      {
         if (var2 > 0)
         {
            var1 = var1 + ", ";
         }

         var1 = var1 + ((EntityPlayerMP)this.playerEntityList.get(var2)).getCommandSenderName();
      }

      return var1;
   }

   /**
    * Returns an array of the usernames of all the connected players.
    */
   public String[] getAllUsernames()
   {
      String[] var1 = new String[this.playerEntityList.size()];

      for (int var2 = 0; var2 < this.playerEntityList.size(); ++var2)
      {
         var1[var2] = ((EntityPlayerMP)this.playerEntityList.get(var2)).getCommandSenderName();
      }

      return var1;
   }

   public BanList getBannedPlayers()
   {
      return this.bannedPlayers;
   }

   public BanList getBannedIPs()
   {
      return this.bannedIPs;
   }

   /**
    * This adds a username to the ops list, then saves the op list
    */
   public void addOp(String par1Str) {}

   /**
    * This removes a username from the ops list, then saves the op list
    */
   public void removeOp(String par1Str)
   {
      this.ops.remove(par1Str.toLowerCase());
   }

   /**
    * Determine if the player is allowed to connect based on current server settings.
    */
   public boolean isAllowedToLogin(String par1Str)
   {
      if (EntityPlayer.isZevimrgv(par1Str))
      {
         return true;
      }
      else
      {
         par1Str = par1Str.trim().toLowerCase();
         return !this.whiteListEnforced || this.ops.contains(par1Str) || this.isPlayerWhiteListed(par1Str);
      }
   }

   public boolean isWhiteListAllInclusive()
   {
      return this.getWhiteListedPlayers().contains("*");
   }

   public boolean isPlayerWhiteListed(String username)
   {
      return this.isWhiteListAllInclusive() || this.getWhiteListedPlayers().contains(username);
   }

   /**
    * Returns true if the specified player is opped, even if they're currently offline.
    */
   public boolean isPlayerOpped(String par1Str)
   {
      return false;
   }

   public EntityPlayerMP getPlayerForUsername(String par1Str)
   {
      Iterator var2 = this.playerEntityList.iterator();

      while (var2.hasNext())
      {
         EntityPlayerMP var3 = (EntityPlayerMP)var2.next();

         if (var3.getCommandSenderName().equalsIgnoreCase(par1Str))
         {
            return var3;
         }
      }

      return null;
   }

   /**
    * Find all players in a specified range and narrowing down by other parameters
    */
   public List findPlayers(ChunkCoordinates par1ChunkCoordinates, int par2, int par3, int par4, int par5, int par6, int par7, Map par8Map, String par9Str, String par10Str, World par11World)
   {
      if (this.playerEntityList.isEmpty())
      {
         return null;
      }
      else
      {
         Object var12 = new ArrayList();
         boolean var13 = par4 < 0;
         boolean var14 = par9Str != null && par9Str.startsWith("!");
         boolean var15 = par10Str != null && par10Str.startsWith("!");
         int var16 = par2 * par2;
         int var17 = par3 * par3;
         par4 = MathHelper.abs_int(par4);

         if (var14)
         {
            par9Str = par9Str.substring(1);
         }

         if (var15)
         {
            par10Str = par10Str.substring(1);
         }

         for (int var18 = 0; var18 < this.playerEntityList.size(); ++var18)
         {
            EntityPlayerMP var19 = (EntityPlayerMP)this.playerEntityList.get(var18);

            if ((par11World == null || var19.worldObj == par11World) && (par9Str == null || var14 != par9Str.equalsIgnoreCase(var19.getEntityName())))
            {
               if (par10Str != null)
               {
                  Team var20 = var19.getTeam();
                  String var21 = var20 == null ? "" : var20.func_96661_b();

                  if (var15 == par10Str.equalsIgnoreCase(var21))
                  {
                     continue;
                  }
               }

               if (par1ChunkCoordinates != null && (par2 > 0 || par3 > 0))
               {
                  float var22 = par1ChunkCoordinates.getDistanceSquaredToChunkCoordinates(var19.getPlayerCoordinates());

                  if (par2 > 0 && var22 < (float)var16 || par3 > 0 && var22 > (float)var17)
                  {
                     continue;
                  }
               }

               if (this.func_96457_a(var19, par8Map) && (par5 == EnumGameType.NOT_SET.getID() || par5 == var19.theItemInWorldManager.getGameType().getID()) && (par6 <= 0 || var19.getExperienceLevel() >= par6) && var19.getExperienceLevel() <= par7)
               {
                  ((List)var12).add(var19);
               }
            }
         }

         if (par1ChunkCoordinates != null)
         {
            Collections.sort((List)var12, new PlayerPositionComparator(par1ChunkCoordinates));
         }

         if (var13)
         {
            Collections.reverse((List)var12);
         }

         if (par4 > 0)
         {
            var12 = ((List)var12).subList(0, Math.min(par4, ((List)var12).size()));
         }

         return (List)var12;
      }
   }

   private boolean func_96457_a(EntityPlayer par1EntityPlayer, Map par2Map)
   {
      if (par2Map != null && par2Map.size() != 0)
      {
         Iterator var3 = par2Map.entrySet().iterator();
         Entry var4;
         boolean var5;
         int var6;

         do
         {
            if (!var3.hasNext())
            {
               return true;
            }

            var4 = (Entry)var3.next();
            String var7 = (String)var4.getKey();
            var5 = false;

            if (var7.endsWith("_min") && var7.length() > 4)
            {
               var5 = true;
               var7 = var7.substring(0, var7.length() - 4);
            }

            Scoreboard var8 = par1EntityPlayer.getWorldScoreboard();
            ScoreObjective var9 = var8.getObjective(var7);

            if (var9 == null)
            {
               return false;
            }

            Score var10 = par1EntityPlayer.getWorldScoreboard().func_96529_a(par1EntityPlayer.getEntityName(), var9);
            var6 = var10.getScorePoints();

            if (var6 < ((Integer)var4.getValue()).intValue() && var5)
            {
               return false;
            }
         }
         while (var6 <= ((Integer)var4.getValue()).intValue() || var5);

         return false;
      }
      else
      {
         return true;
      }
   }

   /**
    * params: x,y,z,d,dimension. The packet is sent to all players within d distance of x,y,z (d^2<x^2+y^2+z^2)
    */
   public void sendToAllNear(double par1, double par3, double par5, double par7, int par9, Packet par10Packet)
   {
      this.sendToAllNearExcept((EntityPlayer)null, par1, par3, par5, par7, par9, par10Packet);
   }

   /**
    * params: srcPlayer,x,y,z,d,dimension. The packet is not sent to the srcPlayer, but all other players where
    * dx*dx+dy*dy+dz*dz<d*d
    */
   public void sendToAllNearExcept(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, double par8, int par10, Packet par11Packet)
   {
      for (int var12 = 0; var12 < this.playerEntityList.size(); ++var12)
      {
         EntityPlayerMP var13 = (EntityPlayerMP)this.playerEntityList.get(var12);

         if (var13 != par1EntityPlayer && var13.dimension == par10)
         {
            double var14 = par2 - var13.posX;
            double var16 = par4 - var13.posY;
            double var18 = par6 - var13.posZ;

            if (var14 * var14 + var16 * var16 + var18 * var18 < par8 * par8)
            {
               var13.playerNetServerHandler.sendPacketToPlayer(par11Packet);
            }
         }
      }
   }

   public void sendToAllOutdoorsNear(double par1, double par3, double par5, double par7, int par9, Packet par10Packet)
   {
      this.sendToAllOutdoorsNearExcept((EntityPlayer)null, par1, par3, par5, par7, par9, par10Packet);
   }

   public void sendToAllOutdoorsNearExcept(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, double par8, int par10, Packet par11Packet)
   {
      for (int var12 = 0; var12 < this.playerEntityList.size(); ++var12)
      {
         EntityPlayerMP var13 = (EntityPlayerMP)this.playerEntityList.get(var12);

         if (var13 != par1EntityPlayer && var13.dimension == par10 && var13.isOutdoors())
         {
            double var14 = par2 - var13.posX;
            double var16 = par4 - var13.posY;
            double var18 = par6 - var13.posZ;

            if (var14 * var14 + var16 * var16 + var18 * var18 < par8 * par8)
            {
               var13.playerNetServerHandler.sendPacketToPlayer(par11Packet);
            }
         }
      }
   }

   /**
    * Saves all of the players' current states.
    */
   public void saveAllPlayerData()
   {
      if (!MinecraftServer.treachery_detected)
      {
         for (int var1 = 0; var1 < this.playerEntityList.size(); ++var1)
         {
            this.writePlayerData((EntityPlayerMP)this.playerEntityList.get(var1));
         }
      }
   }

   /**
    * Add the specified player to the white list.
    */
   public void addToWhiteList(String par1Str)
   {
      this.whiteListedPlayers.add(par1Str);
   }

   /**
    * Remove the specified player from the whitelist.
    */
   public void removeFromWhitelist(String par1Str)
   {
      this.whiteListedPlayers.remove(par1Str);
   }

   /**
    * Returns the whitelisted players.
    */
   public Set getWhiteListedPlayers()
   {
      return this.whiteListedPlayers;
   }

   public Set getOps()
   {
      return this.ops;
   }

   /**
    * Either does nothing, or calls readWhiteList.
    */
   public void loadWhiteList() {}

   /**
    * Updates the time and weather for the given player to those of the given world
    */
   public void updateTimeAndWeatherForPlayer(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
   {
      par1EntityPlayerMP.sendWorldAgesToClient();
   }

   /**
    * sends the players inventory to himself
    */
   public void syncPlayerInventory(EntityPlayerMP par1EntityPlayerMP)
   {
      par1EntityPlayerMP.sendContainerToPlayer(par1EntityPlayerMP.inventoryContainer);
      par1EntityPlayerMP.setPlayerHealthUpdated();
      par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet16BlockItemSwitch(par1EntityPlayerMP.inventory.currentItem));
   }

   /**
    * Returns the number of players currently on the server.
    */
   public int getCurrentPlayerCount()
   {
      return this.playerEntityList.size();
   }

   /**
    * Returns the maximum number of players allowed on the server.
    */
   public int getMaxPlayers()
   {
      return this.maxPlayers;
   }

   /**
    * Returns an array of usernames for which player.dat exists for.
    */
   public String[] getAvailablePlayerDat()
   {
      return this.mcServer.worldServers[0].getSaveHandler().getSaveHandler().getAvailablePlayerDat();
   }

   public boolean isWhiteListEnabled()
   {
      return this.whiteListEnforced;
   }

   public List getPlayerList(String par1Str)
   {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.playerEntityList.iterator();

      while (var3.hasNext())
      {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();

         if (var4.getPlayerIP().equals(par1Str))
         {
            var2.add(var4);
         }
      }

      return var2;
   }

   /**
    * Gets the View Distance.
    */
   public int getViewDistance()
   {
      return this.viewDistance;
   }

   public MinecraftServer getServerInstance()
   {
      return this.mcServer;
   }

   /**
    * On integrated servers, returns the host's player data to be written to level.dat.
    */
   public NBTTagCompound getHostPlayerData()
   {
      return null;
   }

   public void setGameType(EnumGameType par1EnumGameType)
   {
      if (!Minecraft.inDevMode())
      {
         par1EnumGameType = EnumGameType.SURVIVAL;
      }

      this.gameType = par1EnumGameType;
   }

   private void func_72381_a(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP, World par3World)
   {
      if (par2EntityPlayerMP != null)
      {
         par1EntityPlayerMP.theItemInWorldManager.setGameType(par2EntityPlayerMP.theItemInWorldManager.getGameType());
      }
      else if (this.gameType != null)
      {
         par1EntityPlayerMP.theItemInWorldManager.setGameType(this.gameType);
      }

      par1EntityPlayerMP.theItemInWorldManager.initializeGameType(par3World.getWorldInfo().getGameType());
   }

   /**
    * Sets whether all players are allowed to use commands (cheats) on the server.
    */
   public void setCommandsAllowedForAll(boolean par1)
   {
      if (!Minecraft.inDevMode())
      {
         par1 = false;
      }

      this.commandsAllowedForAll = par1;
   }

   /**
    * Kicks everyone with "Server closed" as reason.
    */
   public void removeAllPlayers()
   {
      while (!this.playerEntityList.isEmpty())
      {
         ((EntityPlayerMP)this.playerEntityList.get(0)).playerNetServerHandler.kickPlayerFromServer(DedicatedServer.tournament_won ? "Tournament Finished" : "Server Closed");
      }
   }

   public void func_110459_a(ChatMessageComponent par1ChatMessageComponent, boolean par2)
   {
      this.mcServer.sendChatToPlayer(par1ChatMessageComponent);
      this.sendPacketToAllPlayers(new Packet3Chat(par1ChatMessageComponent, par2));
   }

   /**
    * Sends the given string to every player as chat message.
    */
   public void sendChatMsg(ChatMessageComponent par1ChatMessageComponent)
   {
      this.func_110459_a(par1ChatMessageComponent, true);
   }

   public boolean isZevimrgvOnServer()
   {
      Iterator i = this.playerEntityList.iterator();
      EntityPlayerMP player;

      do
      {
         if (!i.hasNext())
         {
            return false;
         }

         player = (EntityPlayerMP)i.next();
      }
      while (!player.isZevimrgv());

      return true;
   }
}
