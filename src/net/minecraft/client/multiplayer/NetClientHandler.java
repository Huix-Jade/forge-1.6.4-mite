package net.minecraft.client.multiplayer;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.crypto.SecretKey;
import net.minecraft.block.BitHelper;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenDisconnectedOnline;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBrick;
import net.minecraft.entity.EntityGelatinousSphere;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityWeb;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemManure;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemRock;
import net.minecraft.item.ItemStack;
import net.minecraft.mite.MITEConstant;
import net.minecraft.mite.MITEContainerCrafting;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet104WindowItems;
import net.minecraft.network.packet.Packet105UpdateProgressbar;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet133TileEditorOpen;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet200Statistic;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet203AutoComplete;
import net.minecraft.network.packet.Packet205ClientCommand;
import net.minecraft.network.packet.Packet206SetObjective;
import net.minecraft.network.packet.Packet207SetScore;
import net.minecraft.network.packet.Packet208SetDisplayObjective;
import net.minecraft.network.packet.Packet209SetPlayerTeam;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet252SharedKey;
import net.minecraft.network.packet.Packet253ServerAuthData;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet26EntityExpOrb;
import net.minecraft.network.packet.Packet27PlayerInput;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet32EntityLook;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet35EntityHeadRotation;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet40EntityMetadata;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet42RemoveEntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet44UpdateAttributes;
import net.minecraft.network.packet.Packet44UpdateAttributesSnapshot;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet52MultiBlockChange;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet55BlockDestroy;
import net.minecraft.network.packet.Packet56MapChunks;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet62LevelSound;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.network.packet.Packet80LongDistanceSound;
import net.minecraft.network.packet.Packet82AddHunger;
import net.minecraft.network.packet.Packet84EntityStateWithData;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.network.packet.Packet88UpdateStrongboxOwner;
import net.minecraft.network.packet.Packet8UpdateHealth;
import net.minecraft.network.packet.Packet91PlayerStat;
import net.minecraft.network.packet.Packet92UpdateTimeSmall;
import net.minecraft.network.packet.Packet93WorldAchievement;
import net.minecraft.network.packet.Packet94CreateFile;
import net.minecraft.network.packet.Packet97MultiBlockChange;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStrongbox;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumCommand;
import net.minecraft.util.EnumConsciousState;
import net.minecraft.util.EnumEntityFX;
import net.minecraft.util.EnumEntityState;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumInsulinResistanceLevel;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.SpatialScaler;
import net.minecraft.util.Vec3;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

public class NetClientHandler extends NetHandler {
   private boolean disconnected;
   private static String five = "5";
   private INetworkManager netManager;
   public String field_72560_a;
   private Minecraft mc;
   private WorldClient worldClient;
   private static String em = "M";
   private boolean doneLoadingTerrain;
   public MapStorage mapStorage = new MapStorage((ISaveHandler)null);
   private Map playerInfoMap = new HashMap();
   public List playerInfoList = new ArrayList();
   public int currentServerMaxPlayers = 20;
   private GuiScreen field_98183_l;
   private static String dee = "D";
   Random rand = new Random();
   private static Class[] classes = new Class[]{ContainerPlayer.class, ContainerWorkbench.class, MITEConstant.class, MITEContainerCrafting.class, EntityClientPlayerMP.class, EntityPlayer.class, EntityPlayerSP.class, FoodStats.class, Minecraft.class, MovementInputFromOptions.class, NetClientHandler.class, PlayerControllerMP.class, Packet13PlayerLookMove.class, Packet27PlayerInput.class, Packet82AddHunger.class, Packet85SimpleSignal.class, Packet202PlayerAbilities.class, PlayerCapabilities.class, TcpConnection.class};
   private static int class_hash_sum = 0;

   public NetClientHandler(Minecraft par1Minecraft, String par2Str, int par3) throws IOException {
      this.mc = par1Minecraft;
      Socket var4 = new Socket(InetAddress.getByName(par2Str), par3);
      this.netManager = new TcpConnection(par1Minecraft.getLogAgent(), var4, "Client", this);
   }

   public NetClientHandler(Minecraft par1Minecraft, String par2Str, int par3, GuiScreen par4GuiScreen) throws IOException {
      this.mc = par1Minecraft;
      this.field_98183_l = par4GuiScreen;
      Socket var5 = new Socket(InetAddress.getByName(par2Str), par3);
      this.netManager = new TcpConnection(par1Minecraft.getLogAgent(), var5, "Client", this);
   }

   public NetClientHandler(Minecraft par1Minecraft, IntegratedServer par2IntegratedServer) throws IOException {
      this.mc = par1Minecraft;
      this.netManager = new MemoryConnection(par1Minecraft.getLogAgent(), this);
      par2IntegratedServer.getServerListeningThread().func_71754_a((MemoryConnection)this.netManager, par1Minecraft.getSession().getUsername());
   }

   public void cleanup() {
      if (this.netManager != null) {
         this.netManager.wakeThreads();
      }

      this.netManager = null;
      this.worldClient = null;
   }

   public void processReadPackets() {
      if (!this.disconnected && this.netManager != null) {
         this.netManager.processReadPackets();
      }

   }

   public void handleServerAuthData(Packet253ServerAuthData par1Packet253ServerAuthData) {
      Minecraft.soonest_reconnection_time = 0L;
      String var2 = par1Packet253ServerAuthData.getServerId().trim();
      PublicKey var3 = par1Packet253ServerAuthData.getPublicKey();
      SecretKey var4 = CryptManager.createNewSharedKey();
      if (!"-".equals(var2)) {
         String var5 = (new BigInteger(CryptManager.getServerIdHash(var2, var3, var4))).toString(16);
         String var6 = this.sendSessionRequest(this.mc.getSession().getUsername(), this.mc.getSession().getSessionID(), var5);
         if (!"ok".equalsIgnoreCase(var6)) {
            this.netManager.networkShutdown("disconnect.loginFailedInfo", var6);
            return;
         }
      }

      this.addToSendQueue(new Packet252SharedKey(var4, var3, par1Packet253ServerAuthData.getVerifyToken()));
   }

   private String sendSessionRequest(String par1Str, String par2Str, String par3Str) {
      try {
         URL var4 = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + urlEncode(par1Str) + "&sessionId=" + urlEncode(par2Str) + "&serverId=" + urlEncode(par3Str));
         InputStream var5 = var4.openConnection(this.mc.getProxy()).getInputStream();
         BufferedReader var6 = new BufferedReader(new InputStreamReader(var5));
         String var7 = var6.readLine();
         var6.close();
         return var7;
      } catch (IOException var8) {
         return var8.toString();
      }
   }

   private static String urlEncode(String par0Str) throws IOException {
      return URLEncoder.encode(par0Str, "UTF-8");
   }

   public void handleSharedKey(Packet252SharedKey par1Packet252SharedKey) {
      this.addToSendQueue(new Packet205ClientCommand(0));
   }

   public void handleLogin(Packet1Login par1Packet1Login) {
      this.mc.playerController = new PlayerControllerMP(this.mc, this);
      this.worldClient = new WorldClient(this, new WorldSettings(0L, par1Packet1Login.gameType, false, par1Packet1Login.hardcoreMode, par1Packet1Login.terrainType, par1Packet1Login.are_skills_enabled), par1Packet1Login.dimension, par1Packet1Login.difficultySetting, this.mc.mcProfiler, this.mc.getLogAgent(), par1Packet1Login.world_creation_time, par1Packet1Login.total_world_time);
      this.mc.loadWorld(this.worldClient);
      this.mc.thePlayer.dimension = par1Packet1Login.dimension;
      this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
      this.mc.thePlayer.entityId = par1Packet1Login.clientEntityId;
      this.currentServerMaxPlayers = par1Packet1Login.maxPlayers;
      this.mc.playerController.setGameType(par1Packet1Login.gameType);
      this.mc.gameSettings.sendSettingsToServer();
      this.mc.theWorld.worldInfo.setVillageConditions(par1Packet1Login.village_conditions);
      this.mc.theWorld.worldInfo.setAchievements(par1Packet1Login.achievements);
      this.mc.theWorld.worldInfo.setEarliestMITEReleaseRunIn(par1Packet1Login.earliest_MITE_release_run_in);
      this.mc.theWorld.worldInfo.setLatestMITEReleaseRunIn(par1Packet1Login.latest_MITE_release_run_in);
      this.netManager.addToSendQueue(new Packet250CustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
   }

   public void handleVehicleSpawn(Packet23VehicleSpawn par1Packet23VehicleSpawn) {
      double var2;
      double var4;
      double var6;
      if (par1Packet23VehicleSpawn.position_set_using_unscaled_integers) {
         var2 = (double)par1Packet23VehicleSpawn.unscaled_pos_x;
         var4 = (double)par1Packet23VehicleSpawn.unscaled_pos_y;
         var6 = (double)par1Packet23VehicleSpawn.unscaled_pos_z;
      } else {
         var2 = SpatialScaler.getPosX(par1Packet23VehicleSpawn.scaled_pos_x);
         var4 = SpatialScaler.getPosY(par1Packet23VehicleSpawn.scaled_pos_y);
         var6 = SpatialScaler.getPosZ(par1Packet23VehicleSpawn.scaled_pos_z);
      }

      Entity var8 = null;
      if (par1Packet23VehicleSpawn.type == 10) {
         var8 = EntityMinecart.createMinecart(this.worldClient, var2, var4, var6, par1Packet23VehicleSpawn.throwerEntityId);
      } else if (par1Packet23VehicleSpawn.type == 90) {
         Entity var9 = this.getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);
         if (var9 instanceof EntityPlayer) {
            var8 = new EntityFishHook(this.worldClient, var2, var4, var6, (EntityPlayer)var9);
         }

         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 60) {
         if (par1Packet23VehicleSpawn.arrow_item_id == -1) {
            var8 = null;
         } else {
            var2 = par1Packet23VehicleSpawn.exact_pos_x;
            var4 = par1Packet23VehicleSpawn.exact_pos_y;
            var6 = par1Packet23VehicleSpawn.exact_pos_z;
            var8 = new EntityArrow(this.worldClient, var2, var4, var6, (ItemArrow)Item.itemsList[par1Packet23VehicleSpawn.arrow_item_id], par1Packet23VehicleSpawn.launcher_was_enchanted);
            if (par1Packet23VehicleSpawn.arrow_stuck_in_block) {
               ((EntityArrow)var8).setInGround();
            }

            EntityArrow arrow = (EntityArrow)var8;
            arrow.xTile = par1Packet23VehicleSpawn.arrow_tile_x;
            arrow.yTile = par1Packet23VehicleSpawn.arrow_tile_y;
            arrow.zTile = par1Packet23VehicleSpawn.arrow_tile_z;
            arrow.setInTile(par1Packet23VehicleSpawn.arrow_in_tile);
            arrow.setInData(par1Packet23VehicleSpawn.arrow_in_data);
         }
      } else if (par1Packet23VehicleSpawn.type == 61) {
         var8 = new EntitySnowball(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 71) {
         var8 = new EntityItemFrame(this.worldClient, par1Packet23VehicleSpawn.unscaled_pos_x, par1Packet23VehicleSpawn.unscaled_pos_y, par1Packet23VehicleSpawn.unscaled_pos_z, par1Packet23VehicleSpawn.throwerEntityId);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 77) {
         var8 = new EntityLeashKnot(this.worldClient, par1Packet23VehicleSpawn.unscaled_pos_x, par1Packet23VehicleSpawn.unscaled_pos_y, par1Packet23VehicleSpawn.unscaled_pos_z);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 65) {
         var8 = new EntityEnderPearl(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 72) {
         var8 = new EntityEnderEye(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 76) {
         var8 = new EntityFireworkRocket(this.worldClient, var2, var4, var6, (ItemStack)null);
      } else if (par1Packet23VehicleSpawn.type == 63) {
         var8 = new EntityLargeFireball(this.worldClient, var2, var4, var6, (double)par1Packet23VehicleSpawn.approx_motion_x, (double)par1Packet23VehicleSpawn.approx_motion_y, (double)par1Packet23VehicleSpawn.approx_motion_z);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 64) {
         var8 = new EntitySmallFireball(this.worldClient, var2, var4, var6, (double)par1Packet23VehicleSpawn.approx_motion_x, (double)par1Packet23VehicleSpawn.approx_motion_y, (double)par1Packet23VehicleSpawn.approx_motion_z);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 66) {
         var8 = new EntityWitherSkull(this.worldClient, var2, var4, var6, (double)par1Packet23VehicleSpawn.approx_motion_x, (double)par1Packet23VehicleSpawn.approx_motion_y, (double)par1Packet23VehicleSpawn.approx_motion_z);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 62) {
         var8 = new EntityEgg(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 500) {
         var8 = new EntityBrick(this.worldClient, var2, var4, var6, Item.brick);
      } else if (par1Packet23VehicleSpawn.type == 501) {
         var8 = new EntityBrick(this.worldClient, var2, var4, var6, Item.netherrackBrick);
      } else if (MathHelper.isInRange(par1Packet23VehicleSpawn.type, 600, 699)) {
         var8 = new EntityGelatinousSphere(this.worldClient, var2, var4, var6, Item.slimeBall, par1Packet23VehicleSpawn.type - 600);
      } else if (par1Packet23VehicleSpawn.type == 700) {
         var8 = new EntityWeb(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 73) {
         var8 = new EntityPotion(this.worldClient, var2, var4, var6, par1Packet23VehicleSpawn.throwerEntityId);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 75) {
         var8 = new EntityExpBottle(this.worldClient, var2, var4, var6);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      } else if (par1Packet23VehicleSpawn.type == 1) {
         var8 = new EntityBoat(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 50) {
         var8 = new EntityTNTPrimed(this.worldClient, var2, var4, var6, (EntityLivingBase)null);
      } else if (par1Packet23VehicleSpawn.type == 51) {
         var8 = new EntityEnderCrystal(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 2) {
         var8 = new EntityItem(this.worldClient, var2, var4, var6);
      } else if (par1Packet23VehicleSpawn.type == 70) {
         var8 = new EntityFallingSand(this.worldClient, (double)MathHelper.floor_double(var2) + 0.5, (double)MathHelper.floor_double(var4) + 0.5, (double)MathHelper.floor_double(var6) + 0.5, par1Packet23VehicleSpawn.throwerEntityId & '\uffff', par1Packet23VehicleSpawn.throwerEntityId >> 16);
         par1Packet23VehicleSpawn.throwerEntityId = 0;
      }

      if (var8 != null) {
         ((Entity)var8).rotationYaw = SpatialScaler.getRotation(par1Packet23VehicleSpawn.scaled_yaw);
         ((Entity)var8).rotationPitch = SpatialScaler.getRotation(par1Packet23VehicleSpawn.scaled_pitch);
         if (var8 instanceof EntityBoat) {
            ((EntityBoat)var8).setPositionAndRotation2(((Entity)var8).posX, ((Entity)var8).posY, ((Entity)var8).posZ, ((Entity)var8).rotationYaw, ((Entity)var8).rotationPitch, 3);
            ((Entity)var8).prevRotationYaw = ((Entity)var8).rotationYaw;
         }

         Entity[] var12 = ((Entity)var8).getParts();
         if (var12 != null) {
            int var10 = par1Packet23VehicleSpawn.entityId - ((Entity)var8).entityId;

            for(int var11 = 0; var11 < var12.length; ++var11) {
               var12[var11].entityId += var10;
            }
         }

         ((Entity)var8).entityId = par1Packet23VehicleSpawn.entityId;
         this.worldClient.addEntityToWorld(par1Packet23VehicleSpawn.entityId, (Entity)var8);
         if (par1Packet23VehicleSpawn.throwerEntityId > 0) {
            if (par1Packet23VehicleSpawn.type == 60) {
               Entity var13 = this.getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);
               if (var13 instanceof EntityLivingBase) {
                  EntityArrow var14 = (EntityArrow)var8;
                  var14.shootingEntity = var13;
               }

               ((Entity)var8).setVelocity(par1Packet23VehicleSpawn.exact_motion_x, par1Packet23VehicleSpawn.exact_motion_y, par1Packet23VehicleSpawn.exact_motion_z);
               return;
            }

            ((Entity)var8).setVelocity((double)par1Packet23VehicleSpawn.approx_motion_x, (double)par1Packet23VehicleSpawn.approx_motion_y, (double)par1Packet23VehicleSpawn.approx_motion_z);
         }
      }

   }

   public void handleEntityExpOrb(Packet26EntityExpOrb par1Packet26EntityExpOrb) {
      EntityXPOrb var2 = new EntityXPOrb(this.worldClient, SpatialScaler.getPosX(par1Packet26EntityExpOrb.scaled_pos_x), SpatialScaler.getPosY(par1Packet26EntityExpOrb.scaled_pos_y), SpatialScaler.getPosZ(par1Packet26EntityExpOrb.scaled_pos_z), par1Packet26EntityExpOrb.xp_value);
      var2.entityId = par1Packet26EntityExpOrb.entity_id;
      var2.setPlayerThisBelongsTo(par1Packet26EntityExpOrb.player_this_belongs_to);
      var2.setVelocity(SpatialScaler.getMotion(par1Packet26EntityExpOrb.scaled_motion_x), SpatialScaler.getMotion(par1Packet26EntityExpOrb.scaled_motion_y), SpatialScaler.getMotion(par1Packet26EntityExpOrb.scaled_motion_z));
      this.worldClient.addEntityToWorld(par1Packet26EntityExpOrb.entity_id, var2);
   }

   public void handleWeather(Packet71Weather par1Packet71Weather) {
      EntityLightningBolt var8 = null;
      if (par1Packet71Weather.isLightningBolt == 1) {
         var8 = new EntityLightningBolt(this.worldClient, SpatialScaler.getPosX(par1Packet71Weather.scaled_pos_x), SpatialScaler.getPosY(par1Packet71Weather.scaled_pos_y), SpatialScaler.getPosZ(par1Packet71Weather.scaled_pos_z));
      }

      if (var8 != null) {
         var8.entityId = par1Packet71Weather.entityID;
         this.worldClient.addWeatherEffect(var8);
      }

   }

   public void handleEntityPainting(Packet25EntityPainting par1Packet25EntityPainting) {
      EntityPainting var2 = new EntityPainting(this.worldClient, par1Packet25EntityPainting.xPosition, par1Packet25EntityPainting.yPosition, par1Packet25EntityPainting.zPosition, par1Packet25EntityPainting.direction, par1Packet25EntityPainting.title);
      this.worldClient.addEntityToWorld(par1Packet25EntityPainting.entityId, var2);
   }

   public void handleEntityVelocity(Packet28EntityVelocity par1Packet28EntityVelocity) {
      Entity var2 = this.getEntityByID(par1Packet28EntityVelocity.entityId);
      if (var2 != null) {
         par1Packet28EntityVelocity.applyToEntity(var2);
      }

   }

   public void handleEntityMetadata(Packet40EntityMetadata par1Packet40EntityMetadata) {
      Entity var2 = this.getEntityByID(par1Packet40EntityMetadata.entityId);
      if (var2 != null && par1Packet40EntityMetadata.getMetadata() != null) {
         var2.getDataWatcher().updateWatchedObjectsFromList(par1Packet40EntityMetadata.getMetadata());
      }

   }

   public void handleNamedEntitySpawn(Packet20NamedEntitySpawn par1Packet20NamedEntitySpawn) {
      double var2 = SpatialScaler.getPosX(par1Packet20NamedEntitySpawn.scaled_pos_x);
      double var4 = SpatialScaler.getPosY(par1Packet20NamedEntitySpawn.scaled_pos_y);
      double var6 = SpatialScaler.getPosZ(par1Packet20NamedEntitySpawn.scaled_pos_z);
      float var8 = SpatialScaler.getRotation(par1Packet20NamedEntitySpawn.scaled_yaw);
      float var9 = SpatialScaler.getRotation(par1Packet20NamedEntitySpawn.scaled_pitch);
      EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.mc.theWorld, par1Packet20NamedEntitySpawn.name);
      var10.prevPosX = var10.lastTickPosX = var10.posX = var2;
      var10.prevPosY = var10.lastTickPosY = var10.posY = var4;
      var10.prevPosZ = var10.lastTickPosZ = var10.posZ = var6;
      int var11 = par1Packet20NamedEntitySpawn.currentItem;
      if (var11 == 0) {
         var10.inventory.mainInventory[var10.inventory.currentItem] = null;
      } else {
         var10.inventory.mainInventory[var10.inventory.currentItem] = new ItemStack(var11, 1, 0);
      }

      var10.setPositionAndRotation(var2, var4, var6, var8, var9);
      this.worldClient.addEntityToWorld(par1Packet20NamedEntitySpawn.entityId, var10);
      List var12 = par1Packet20NamedEntitySpawn.getWatchedMetadata();
      if (var12 != null) {
         var10.getDataWatcher().updateWatchedObjectsFromList(var12);
      }

   }

   public void handleEntityTeleport(Packet34EntityTeleport par1Packet34EntityTeleport) {
      Entity var2 = this.getEntityByID(par1Packet34EntityTeleport.entity_id);
      if (var2 != null) {
         par1Packet34EntityTeleport.applyToEntity(var2);
      }

   }

   public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch) {
      if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.getHotbarSize()) {
         this.mc.thePlayer.inventory.currentItem = par1Packet16BlockItemSwitch.id;
      }

   }

   public void handleEntityLook(Packet32EntityLook packet) {
      Entity entity = this.getEntityByID(packet.entity_id);
      if (entity != null) {
         packet.applyToEntity(entity);
      }
   }

   public void handleEntityHeadRotation(Packet35EntityHeadRotation par1Packet35EntityHeadRotation) {
      Entity var2 = this.getEntityByID(par1Packet35EntityHeadRotation.entityId);
      if (var2 != null) {
         float var3 = (float)(par1Packet35EntityHeadRotation.headRotationYaw * 360) / 256.0F;
         var2.setRotationYawHead(var3);
      }

   }

   public void handleDestroyEntity(Packet29DestroyEntity par1Packet29DestroyEntity) {
      for(int var2 = 0; var2 < par1Packet29DestroyEntity.entityId.length; ++var2) {
         this.worldClient.removeEntityFromWorld(par1Packet29DestroyEntity.entityId[var2]);
      }

   }

   public void handleFlying(Packet10Flying par1Packet10Flying) {
      EntityClientPlayerMP var2 = this.mc.thePlayer;
      double var3 = var2.posX;
      double var5 = var2.posY;
      double var7 = var2.posZ;
      float var9 = var2.rotationYaw;
      float var10 = var2.rotationPitch;
      if (par1Packet10Flying.moving) {
         var3 = par1Packet10Flying.xPosition;
         var5 = par1Packet10Flying.yPosition;
         var7 = par1Packet10Flying.zPosition;
      }

      if (par1Packet10Flying.rotating) {
         var9 = par1Packet10Flying.yaw;
         var10 = par1Packet10Flying.pitch;
      }

      var2.ySize = 0.0F;
      var2.motionX = var2.motionY = var2.motionZ = 0.0;
      var2.setPositionAndRotation(var3, var5, var7, var9, var10);
      par1Packet10Flying.xPosition = var2.posX;
      par1Packet10Flying.yPosition = var2.boundingBox.minY;
      par1Packet10Flying.zPosition = var2.posZ;
      par1Packet10Flying.stance = var2.posY;
      this.netManager.addToSendQueue(par1Packet10Flying);
      if (!this.doneLoadingTerrain) {
         this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
         this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
         this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
         this.doneLoadingTerrain = true;
         this.mc.displayGuiScreen((GuiScreen)null);
      }

   }

   public void handleMultiBlockChange(Packet52MultiBlockChange par1Packet52MultiBlockChange) {
      Debug.println("Handling packet52?");
      int var2 = par1Packet52MultiBlockChange.xPosition * 16;
      int var3 = par1Packet52MultiBlockChange.zPosition * 16;
      if (par1Packet52MultiBlockChange.metadataArray != null) {
         DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(par1Packet52MultiBlockChange.metadataArray));

         try {
            long before = System.nanoTime();

            int delay;
            for(delay = 0; delay < par1Packet52MultiBlockChange.size; ++delay) {
               short var6 = var4.readShort();
               short var7 = var4.readShort();
               int var8 = var7 >> 4 & 4095;
               int var9 = var7 & 15;
               int var10 = var6 >> 12 & 15;
               int var11 = var6 >> 8 & 15;
               int var12 = var6 & 255;
               this.worldClient.setBlockAndMetadataAndInvalidate(var10 + var2, var12, var11 + var3, var8, var9);
            }

            delay = (int)(System.nanoTime() - before) / 10000000;
            if (delay > 0) {
               Minecraft.MITE_log.logInfo("Long time processing handleMultiBlockChange (delay=" + delay + ") #Blocks=" + par1Packet52MultiBlockChange.size);
            }
         } catch (IOException var15) {
            System.out.println("Exception occured, packet52");
         }
      }

   }

   public void handleMultiBlockChange(Packet97MultiBlockChange packet) {
      byte[] bytes = packet.getBytes();
      int base_x = packet.chunk_x * 16;
      int base_z = packet.chunk_z * 16;
      long before = System.nanoTime();

      int delay;
      for(delay = 0; delay < packet.num_blocks; ++delay) {
         int offset = delay * 5;
         int x = base_x + bytes[offset];
         int y = bytes[offset + 1] & 255;
         int z = base_z + bytes[offset + 2];
         int block_id = bytes[offset + 3] & 255;
         int metadata = bytes[offset + 4];
         this.worldClient.setBlockAndMetadataAndInvalidate(x, y, z, block_id, metadata);
         if (this.worldClient.hasSkylight()) {
            this.worldClient.getChunkFromBlockCoords(x, z).addPendingSkylightUpdate(x, y, z);
         }
      }

      delay = (int)(System.nanoTime() - before) / 10000000;
      if (delay > 0) {
         Minecraft.MITE_log.logInfo("Long time processing handleMultiBlockChange97 (delay=" + delay + ") #Blocks=" + packet.num_blocks);
      }

   }

   public void handleMapChunk(Packet51MapChunk par1Packet51MapChunk) {
      if (par1Packet51MapChunk.includeInitialize) {
         if (par1Packet51MapChunk.yChMin == 0) {
            this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, false);
            return;
         }

         this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, true);
      }

      this.worldClient.invalidateBlockReceiveRegion(par1Packet51MapChunk.xCh << 4, 0, par1Packet51MapChunk.zCh << 4, (par1Packet51MapChunk.xCh << 4) + 15, 256, (par1Packet51MapChunk.zCh << 4) + 15);
      Chunk var2 = this.worldClient.getChunkFromChunkCoords(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh);
      if (par1Packet51MapChunk.includeInitialize && var2 == null) {
         this.worldClient.doPreChunk(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh, true);
         var2 = this.worldClient.getChunkFromChunkCoords(par1Packet51MapChunk.xCh, par1Packet51MapChunk.zCh);
      }

      if (var2 != null) {
         var2.fillChunk(par1Packet51MapChunk.getUncompressedChunkData(), par1Packet51MapChunk.yChMin, par1Packet51MapChunk.yChMax, par1Packet51MapChunk.includeInitialize);
         this.worldClient.markBlockRangeForRenderUpdate(par1Packet51MapChunk.xCh << 4, 0, par1Packet51MapChunk.zCh << 4, (par1Packet51MapChunk.xCh << 4) + 15, 256, (par1Packet51MapChunk.zCh << 4) + 15);
         if (!par1Packet51MapChunk.includeInitialize || !(this.worldClient.provider instanceof WorldProviderSurface)) {
            var2.resetRelightChecks();
         }
      } else {
         Minecraft.setErrorMessage("Wasn't able to mark chunk at " + par1Packet51MapChunk.xCh * 16 + ", " + par1Packet51MapChunk.zCh + " for render update");
      }

   }

   public void handleBlockChange(Packet53BlockChange par1Packet53BlockChange) {
      this.worldClient.setBlockAndMetadataAndInvalidate(par1Packet53BlockChange.xPosition, par1Packet53BlockChange.yPosition, par1Packet53BlockChange.zPosition, par1Packet53BlockChange.type, par1Packet53BlockChange.metadata);
   }

   public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect) {
      this.netManager.networkShutdown("disconnect.kicked");
      this.disconnected = true;
      this.mc.loadWorld((WorldClient)null);
      if (this.field_98183_l != null) {
         this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.disconnected", "disconnect.genericReason", new Object[]{par1Packet255KickDisconnect.reason}));
      } else {
         this.mc.displayGuiScreen(new GuiDisconnected((GuiScreen)(par1Packet255KickDisconnect.playerWasHosting() ? new GuiMainMenu() : new GuiMultiplayer(new GuiMainMenu())), "disconnect.disconnected", "disconnect.genericReason", new Object[]{par1Packet255KickDisconnect.reason == null ? null : par1Packet255KickDisconnect.reason.trim()}));
      }

   }

   public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj) {
      if (!this.disconnected) {
         this.disconnected = true;
         this.mc.loadWorld((WorldClient)null);
         if (this.field_98183_l != null) {
            this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.lost", par1Str, par2ArrayOfObj));
         } else {
            this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", par1Str, par2ArrayOfObj));
         }
      }

   }

   public void quitWithPacket(Packet par1Packet) {
      if (!this.disconnected) {
         this.netManager.addToSendQueue(par1Packet);
         this.netManager.serverShutdown();
      }

   }

   public void addToSendQueue(Packet par1Packet) {
      if (!this.disconnected) {
         this.netManager.addToSendQueue(par1Packet);
      }

   }

   public void handleCollect(Packet22Collect par1Packet22Collect) {
      Entity var2 = this.getEntityByID(par1Packet22Collect.collectedEntityId);
      Object var3 = (EntityLivingBase)this.getEntityByID(par1Packet22Collect.collectorEntityId);
      if (var3 == null) {
         var3 = this.mc.thePlayer;
      }

      if (var2 != null) {
         if (!(var2 instanceof EntityXPOrb)) {
            this.worldClient.playSoundAtEntity(var2, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var2, (Entity)var3, -0.5F));
         this.worldClient.removeEntityFromWorld(par1Packet22Collect.collectedEntityId);
      }

   }

   public void handleChat(Packet3Chat par1Packet3Chat) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(ChatMessageComponent.createFromJson(par1Packet3Chat.message).toStringWithFormatting(true));
   }

   public void handleAnimation(Packet18Animation par1Packet18Animation) {
      Entity var2 = this.getEntityByID(par1Packet18Animation.entityId);
      if (var2 != null) {
         if (par1Packet18Animation.animate == 1) {
            EntityLivingBase var3 = (EntityLivingBase)var2;
            var3.swingArm();
         } else if (par1Packet18Animation.animate == 2) {
            var2.performHurtAnimation();
         } else if (par1Packet18Animation.animate == 3) {
            EntityPlayer var4 = (EntityPlayer)var2;
            var4.wakeUpPlayer(true, this.getEntityByID(par1Packet18Animation.other_entity_id));
         } else if (par1Packet18Animation.animate != 4) {
            if (par1Packet18Animation.animate == 6) {
               this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, var2));
            } else if (par1Packet18Animation.animate == 7) {
               EntityCrit2FX var5 = new EntityCrit2FX(this.mc.theWorld, var2, EnumParticle.magicCrit);
               this.mc.effectRenderer.addEffect(var5);
            } else if (par1Packet18Animation.animate == 5 && var2 instanceof EntityOtherPlayerMP) {
            }
         }
      }

   }

   public void handleSleep(Packet17Sleep par1Packet17Sleep) {
      Entity var2 = this.getEntityByID(par1Packet17Sleep.entityID);
      if (var2 != null && par1Packet17Sleep.field_73622_e == 0) {
         EntityPlayer var3 = (EntityPlayer)var2;
         var3.pos_x_before_bed = par1Packet17Sleep.pos_x_before_bed;
         var3.pos_y_before_bed = par1Packet17Sleep.pos_y_before_bed;
         var3.pos_z_before_bed = par1Packet17Sleep.pos_z_before_bed;
         var3.getIntoBed(par1Packet17Sleep.bedX, par1Packet17Sleep.bedY, par1Packet17Sleep.bedZ, par1Packet17Sleep.direction);
      }

   }

   public void disconnect() {
      this.disconnected = true;
      this.netManager.wakeThreads();
      this.netManager.networkShutdown("disconnect.closed");
   }

   public void handleMobSpawn(Packet24MobSpawn par1Packet24MobSpawn) {
      double var2 = SpatialScaler.getPosX(par1Packet24MobSpawn.scaled_pos_x);
      double var4 = SpatialScaler.getPosY(par1Packet24MobSpawn.scaled_pos_y);
      double var6 = SpatialScaler.getPosZ(par1Packet24MobSpawn.scaled_pos_z);
      float var8 = SpatialScaler.getRotation(par1Packet24MobSpawn.scaled_yaw);
      float var9 = SpatialScaler.getRotation(par1Packet24MobSpawn.scaled_pitch);
      EntityLiving var10 = (EntityLiving)EntityList.createEntityByID(par1Packet24MobSpawn.type, this.mc.theWorld);
      var10.rotationYawHead = (float)(par1Packet24MobSpawn.scaled_head_yaw * 360) / 256.0F;
      Entity[] var11 = var10.getParts();
      if (var11 != null) {
         int var12 = par1Packet24MobSpawn.entity_id - var10.entityId;

         for(int var13 = 0; var13 < var11.length; ++var13) {
            var11[var13].entityId += var12;
         }
      }

      var10.entityId = par1Packet24MobSpawn.entity_id;
      var10.getDataWatcher().updateWatchedObjectsFromList(par1Packet24MobSpawn.getMetadata());
      var10.onSendToClient(par1Packet24MobSpawn);
      var10.setPositionAndRotation(var2, var4, var6, var8, var9);
      var10.prevRenderYawOffset = var10.renderYawOffset = var8;
      var10.prevRotationYawHead = var10.rotationYawHead;
      var10.motionX = SpatialScaler.getMotion(par1Packet24MobSpawn.scaled_motion_x);
      var10.motionY = SpatialScaler.getMotion(par1Packet24MobSpawn.scaled_motion_y);
      var10.motionZ = SpatialScaler.getMotion(par1Packet24MobSpawn.scaled_motion_z);
      if (par1Packet24MobSpawn.is_decoy && var10 instanceof EntityLiving) {
         var10.setAsDecoy();
      }

      this.worldClient.addEntityToWorld(par1Packet24MobSpawn.entity_id, var10);
   }

   public void handleUpdateTime(Packet4UpdateTime par1Packet4UpdateTime) {
      this.mc.theWorld.worldInfo.setTotalWorldTimes(par1Packet4UpdateTime.world_age, this.mc.theWorld);
   }

   public void handleUpdateTimeSmall(Packet92UpdateTimeSmall packet) {
      this.mc.theWorld.worldInfo.setTotalWorldTimes(packet.world_age, this.mc.theWorld);
   }

   public void handleWorldAchievement(Packet93WorldAchievement packet) {
      this.mc.theWorld.worldInfo.unlockAchievement(packet.achievement, packet.username, packet.day, false);
   }

   public void handleCreateFile(Packet94CreateFile packet) {
      boolean result = packet.writeFile();
      if (packet.getContext() == 1) {
         if (result) {
            this.mc.thePlayer.receiveChatMessage("Stats sent to \"" + packet.getFilepath() + "\"", EnumChatFormatting.YELLOW);
         } else {
            this.mc.thePlayer.receiveChatMessage("Failed to send stats to \"" + packet.getFilepath() + "\"", EnumChatFormatting.RED);
         }
      }

   }

   public void handleSpawnPosition(Packet6SpawnPosition par1Packet6SpawnPosition) {
      this.mc.thePlayer.setSpawnChunk(new ChunkCoordinates(par1Packet6SpawnPosition.xPosition, par1Packet6SpawnPosition.yPosition, par1Packet6SpawnPosition.zPosition), true);
      this.mc.theWorld.getWorldInfo().setSpawnPosition(par1Packet6SpawnPosition.xPosition, par1Packet6SpawnPosition.yPosition, par1Packet6SpawnPosition.zPosition);
   }

   public void handleAttachEntity(Packet39AttachEntity par1Packet39AttachEntity) {
      Object var2 = this.getEntityByID(par1Packet39AttachEntity.ridingEntityId);
      Entity var3 = this.getEntityByID(par1Packet39AttachEntity.vehicleEntityId);
      if (par1Packet39AttachEntity.attachState == 0) {
         boolean var4 = false;
         if (par1Packet39AttachEntity.ridingEntityId == this.mc.thePlayer.entityId) {
            var2 = this.mc.thePlayer;
            if (var3 instanceof EntityBoat) {
               ((EntityBoat)var3).func_70270_d(false);
            }

            var4 = ((Entity)var2).ridingEntity == null && var3 != null;
         } else if (var3 instanceof EntityBoat) {
            ((EntityBoat)var3).func_70270_d(true);
         }

         if (var2 == null) {
            return;
         }

         ((Entity)var2).mountEntity(var3);
         if (var4) {
            GameSettings var5 = this.mc.gameSettings;
            this.mc.ingameGUI.func_110326_a(I18n.getStringParams("mount.onboard", GameSettings.getKeyDisplayString(var5.keyBindSneak.keyCode)), false);
         }
      } else if (par1Packet39AttachEntity.attachState == 1 && var2 != null && var2 instanceof EntityLiving) {
         if (var3 != null) {
            ((EntityLiving)var2).setLeashedToEntity(var3, false);
         } else {
            ((EntityLiving)var2).clearLeashed(false, false);
         }
      }

   }

   public void handleEntityStatus(Packet38EntityStatus par1Packet38EntityStatus) {
      Entity var2 = this.getEntityByID(par1Packet38EntityStatus.entityId);
      if (var2 != null) {
         if (par1Packet38EntityStatus instanceof Packet84EntityStateWithData) {
            if (par1Packet38EntityStatus.entity_state == EnumEntityState.in_love && var2 instanceof EntityAnimal) {
               EntityAnimal entity_animal = (EntityAnimal)var2;
               entity_animal.setInLove(((Packet84EntityStateWithData)par1Packet38EntityStatus).data);
            }

            return;
         }

         var2.handleHealthUpdate(par1Packet38EntityStatus.entity_state);
      }

   }

   private Entity getEntityByID(int par1) {
      return (Entity)(par1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : this.worldClient.getEntityByID(par1));
   }

   public void handleUpdateHealth(Packet8UpdateHealth par1Packet8UpdateHealth) {
      this.mc.thePlayer.setPlayerSPHealth(par1Packet8UpdateHealth.healthMP);
      this.mc.thePlayer.getFoodStats().setSatiation(par1Packet8UpdateHealth.satiation, false);
      this.mc.thePlayer.getFoodStats().setNutrition(par1Packet8UpdateHealth.nutrition, false);
      if (this.mc.thePlayer.vision_dimming < par1Packet8UpdateHealth.vision_dimming) {
         this.mc.thePlayer.vision_dimming = par1Packet8UpdateHealth.vision_dimming;
      }

   }

   public void handleExperience(Packet43Experience par1Packet43Experience) {
      this.mc.thePlayer.setXPStats(par1Packet43Experience.experience);
   }

   public void handleRespawn(Packet9Respawn par1Packet9Respawn) {
      if (par1Packet9Respawn.respawnDimension != this.mc.thePlayer.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard var2 = this.worldClient.getScoreboard();
         this.worldClient = new WorldClient(this, new WorldSettings(0L, par1Packet9Respawn.gameType, false, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), par1Packet9Respawn.terrainType, this.mc.theWorld.areSkillsEnabled()), par1Packet9Respawn.respawnDimension, par1Packet9Respawn.difficulty, this.mc.mcProfiler, this.mc.getLogAgent(), par1Packet9Respawn.world_creation_time, par1Packet9Respawn.total_world_time);
         this.worldClient.func_96443_a(var2);
         this.mc.loadWorld(this.worldClient);
         this.mc.thePlayer.dimension = par1Packet9Respawn.respawnDimension;
         this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
      }

      this.mc.setDimensionAndSpawnPlayer(par1Packet9Respawn.respawnDimension);
      this.mc.playerController.setGameType(par1Packet9Respawn.gameType);
      this.mc.thePlayer.countdown_to_mark_all_nearby_chunks_for_render_update = 20;
   }

   public void handleExplosion(Packet60Explosion par1Packet60Explosion) {
      Explosion var2 = new Explosion(this.mc.theWorld, (Entity)null, par1Packet60Explosion.explosionX, par1Packet60Explosion.explosionY, par1Packet60Explosion.explosionZ, par1Packet60Explosion.explosion_size_vs_blocks, par1Packet60Explosion.explosion_size_vs_living_entities);
      var2.affectedBlockPositions = par1Packet60Explosion.chunkPositionRecords;
      var2.doExplosionB(true);
      EntityClientPlayerMP var10000 = this.mc.thePlayer;
      var10000.motionX += (double)par1Packet60Explosion.getPlayerVelocityX();
      var10000 = this.mc.thePlayer;
      var10000.motionY += (double)par1Packet60Explosion.getPlayerVelocityY();
      var10000 = this.mc.thePlayer;
      var10000.motionZ += (double)par1Packet60Explosion.getPlayerVelocityZ();
   }

   public void handleOpenWindow(Packet100OpenWindow par1Packet100OpenWindow) {
      par1Packet100OpenWindow.handleOpenWindow(this.mc.thePlayer);
   }

   public void handleSetSlot(Packet103SetSlot par1Packet103SetSlot) {
      EntityClientPlayerMP var2 = this.mc.thePlayer;
      if (par1Packet103SetSlot.windowId == -1) {
         var2.inventory.setItemStack(par1Packet103SetSlot.myItemStack);
      } else {
         boolean var3 = false;
         if (this.mc.currentScreen instanceof GuiContainerCreative) {
            GuiContainerCreative var4 = (GuiContainerCreative)this.mc.currentScreen;
            var3 = var4.getCurrentTabIndex() != CreativeTabs.tabInventory.getTabIndex();
         }

         if (par1Packet103SetSlot.windowId == 0 && par1Packet103SetSlot.itemSlot >= 36 && par1Packet103SetSlot.itemSlot < 45) {
            ItemStack var5 = var2.inventoryContainer.getSlot(par1Packet103SetSlot.itemSlot).getStack();
            if (par1Packet103SetSlot.myItemStack != null && (var5 == null || var5.stackSize < par1Packet103SetSlot.myItemStack.stackSize)) {
               par1Packet103SetSlot.myItemStack.animationsToGo = 5;
            }

            var2.inventoryContainer.putStackInSlot(par1Packet103SetSlot.itemSlot, par1Packet103SetSlot.myItemStack);
         } else if (par1Packet103SetSlot.windowId == var2.openContainer.windowId && (par1Packet103SetSlot.windowId != 0 || !var3)) {
            var2.openContainer.putStackInSlot(par1Packet103SetSlot.itemSlot, par1Packet103SetSlot.myItemStack);
         }
      }

   }

   public void handleTransaction(Packet106Transaction par1Packet106Transaction) {
      Container var2 = null;
      EntityClientPlayerMP var3 = this.mc.thePlayer;
      if (par1Packet106Transaction.windowId == 0) {
         var2 = var3.inventoryContainer;
      } else if (par1Packet106Transaction.windowId == var3.openContainer.windowId) {
         var2 = var3.openContainer;
      }

      if (var2 != null && !par1Packet106Transaction.accepted) {
         this.addToSendQueue(new Packet106Transaction(par1Packet106Transaction.windowId, par1Packet106Transaction.shortWindowId, true));
      }

   }

   public void handleWindowItems(Packet104WindowItems par1Packet104WindowItems) {
      EntityClientPlayerMP var2 = this.mc.thePlayer;
      if (par1Packet104WindowItems.windowId == 0) {
         var2.inventoryContainer.putStacksInSlots(par1Packet104WindowItems.itemStack);
      } else if (par1Packet104WindowItems.windowId == var2.openContainer.windowId) {
         var2.openContainer.putStacksInSlots(par1Packet104WindowItems.itemStack);
      }

   }

   public void func_142031_a(Packet133TileEditorOpen par1Packet133TileEditorOpen) {
      TileEntity var2 = this.worldClient.getBlockTileEntity(par1Packet133TileEditorOpen.field_142035_b, par1Packet133TileEditorOpen.field_142036_c, par1Packet133TileEditorOpen.field_142034_d);
      if (var2 != null) {
         this.mc.thePlayer.displayGUIEditSign(var2);
      } else if (par1Packet133TileEditorOpen.field_142037_a == 0) {
         TileEntitySign var3 = new TileEntitySign();
         var3.setWorldObj(this.worldClient);
         var3.xCoord = par1Packet133TileEditorOpen.field_142035_b;
         var3.yCoord = par1Packet133TileEditorOpen.field_142036_c;
         var3.zCoord = par1Packet133TileEditorOpen.field_142034_d;
         this.mc.thePlayer.displayGUIEditSign(var3);
      }

   }

   public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign) {
      boolean var2 = false;
      if (this.mc.theWorld.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition)) {
         TileEntity var3 = this.mc.theWorld.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);
         if (var3 instanceof TileEntitySign) {
            TileEntitySign var4 = (TileEntitySign)var3;
            if (var4.isEditable()) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  var4.signText[var5] = par1Packet130UpdateSign.signLines[var5];
               }

               var4.onInventoryChanged();
            }

            var2 = true;
         }
      }

      if (!var2 && this.mc.thePlayer != null) {
         this.mc.thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Unable to locate sign at " + par1Packet130UpdateSign.xPosition + ", " + par1Packet130UpdateSign.yPosition + ", " + par1Packet130UpdateSign.zPosition));
      }

   }

   public void handleTileEntityData(Packet132TileEntityData par1Packet132TileEntityData) {
      if (this.mc.theWorld.blockExists(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition)) {
         TileEntity var2 = this.mc.theWorld.getBlockTileEntity(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition);
         if (var2 != null) {
            if (par1Packet132TileEntityData.actionType == 1 && var2 instanceof TileEntityMobSpawner) {
               var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (par1Packet132TileEntityData.actionType == 2 && var2 instanceof TileEntityCommandBlock) {
               var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (par1Packet132TileEntityData.actionType == 3 && var2 instanceof TileEntityBeacon) {
               var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (par1Packet132TileEntityData.actionType == 4 && var2 instanceof TileEntitySkull) {
               var2.readFromNBT(par1Packet132TileEntityData.data);
            }
         }
      }

   }

   public void handleUpdateProgressbar(Packet105UpdateProgressbar par1Packet105UpdateProgressbar) {
      EntityClientPlayerMP var2 = this.mc.thePlayer;
      this.unexpectedPacket(par1Packet105UpdateProgressbar);
      if (var2.openContainer != null && var2.openContainer.windowId == par1Packet105UpdateProgressbar.windowId) {
         var2.openContainer.updateProgressBar(par1Packet105UpdateProgressbar.progressBar, par1Packet105UpdateProgressbar.progressBarValue);
      }

   }

   public void handlePlayerInventory(Packet5PlayerInventory par1Packet5PlayerInventory) {
      Entity var2 = this.getEntityByID(par1Packet5PlayerInventory.entityID);
      if (var2 != null) {
         if (par1Packet5PlayerInventory.full_inventory) {
            int slot_index = par1Packet5PlayerInventory.slot;
            if (slot_index < this.mc.thePlayer.inventory.mainInventory.length) {
               this.mc.thePlayer.inventory.mainInventory[slot_index] = par1Packet5PlayerInventory.getItemSlot();
            } else {
               this.mc.thePlayer.inventory.armorInventory[slot_index - this.mc.thePlayer.inventory.mainInventory.length] = par1Packet5PlayerInventory.getItemSlot();
            }

            return;
         }

         var2.setCurrentItemOrArmor(par1Packet5PlayerInventory.slot, par1Packet5PlayerInventory.getItemSlot());
      }

   }

   public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow) {
      this.mc.thePlayer.func_92015_f();
   }

   public void handleBlockEvent(Packet54PlayNoteBlock par1Packet54PlayNoteBlock) {
      this.mc.theWorld.addBlockEvent(par1Packet54PlayNoteBlock.xLocation, par1Packet54PlayNoteBlock.yLocation, par1Packet54PlayNoteBlock.zLocation, par1Packet54PlayNoteBlock.blockId, par1Packet54PlayNoteBlock.instrumentType, par1Packet54PlayNoteBlock.pitch);
   }

   public void handleBlockDestroy(Packet55BlockDestroy par1Packet55BlockDestroy) {
      this.mc.theWorld.destroyBlockInWorldPartially(par1Packet55BlockDestroy.getEntityId(), par1Packet55BlockDestroy.getPosX(), par1Packet55BlockDestroy.getPosY(), par1Packet55BlockDestroy.getPosZ(), par1Packet55BlockDestroy.getDestroyedStage());
   }

   public void handleMapChunks(Packet56MapChunks par1Packet56MapChunks) {
      for(int var2 = 0; var2 < par1Packet56MapChunks.getNumberOfChunkInPacket(); ++var2) {
         int var3 = par1Packet56MapChunks.getChunkPosX(var2);
         int var4 = par1Packet56MapChunks.getChunkPosZ(var2);
         this.worldClient.doPreChunk(var3, var4, true);
         this.worldClient.invalidateBlockReceiveRegion(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         Chunk var5 = this.worldClient.getChunkFromChunkCoords(var3, var4);
         if (var5 == null) {
            this.worldClient.doPreChunk(var3, var4, true);
            var5 = this.worldClient.getChunkFromChunkCoords(var3, var4);
         }

         if (var5 != null) {
            var5.fillChunk(par1Packet56MapChunks.getChunkCompressedData(var2), par1Packet56MapChunks.field_73590_a[var2], par1Packet56MapChunks.field_73588_b[var2], true);
            this.worldClient.markBlockRangeForRenderUpdate(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
            if (!(this.worldClient.provider instanceof WorldProviderSurface)) {
               var5.resetRelightChecks();
            }
         }
      }

   }

   public boolean canProcessPacketsAsync() {
      return this.mc != null && this.mc.theWorld != null && this.mc.thePlayer != null && this.worldClient != null;
   }

   public void handleGameEvent(Packet70GameEvent par1Packet70GameEvent) {
      EntityClientPlayerMP var2 = this.mc.thePlayer;
      int var3 = par1Packet70GameEvent.eventType;
      int var4 = par1Packet70GameEvent.gameMode;
      if (var3 >= 0 && var3 < Packet70GameEvent.clientMessage.length && Packet70GameEvent.clientMessage[var3] != null) {
         var2.addChatMessage(Packet70GameEvent.clientMessage[var3]);
      }

      if (var3 == 1) {
         Minecraft.setErrorMessage("handleGameEvent: event type 1 is no longer handled");
      } else if (var3 == 2) {
         Minecraft.setErrorMessage("handleGameEvent: event type 2 is no longer handled");
      } else if (var3 == 3) {
         this.mc.playerController.setGameType(EnumGameType.getByID(var4));
      } else if (var3 == 4) {
         this.mc.displayGuiScreen(new GuiWinGame());
      } else if (var3 == 5) {
         GameSettings var5 = this.mc.gameSettings;
         if (var4 == 0) {
            this.mc.displayGuiScreen(new GuiScreenDemo());
         } else if (var4 == 101) {
            this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.movement", Keyboard.getKeyName(var5.keyBindForward.keyCode), Keyboard.getKeyName(var5.keyBindLeft.keyCode), Keyboard.getKeyName(var5.keyBindBack.keyCode), Keyboard.getKeyName(var5.keyBindRight.keyCode));
         } else if (var4 == 102) {
            this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.jump", Keyboard.getKeyName(var5.keyBindJump.keyCode));
         } else if (var4 == 103) {
            this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.inventory", Keyboard.getKeyName(var5.keyBindInventory.keyCode));
         }
      } else if (var3 == 6) {
         this.worldClient.playSound(var2.posX, var2.posY + (double)var2.getEyeHeight(), var2.posZ, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (var3 == 7) {
         this.mc.theWorld.worldInfo.setVillageConditions((byte)var4);
      }

   }

   public void handleMapData(Packet131MapData par1Packet131MapData) {
      if (par1Packet131MapData.itemID == Item.map.itemID) {
         ItemMap.getMPMapData(par1Packet131MapData.uniqueID, this.mc.theWorld).updateMPMapData(par1Packet131MapData.itemData);
      } else {
         this.mc.getLogAgent().logWarning("Unknown itemid: " + par1Packet131MapData.uniqueID);
      }

   }

   public void handleDoorChange(Packet61DoorChange par1Packet61DoorChange) {
      if (par1Packet61DoorChange.getRelativeVolumeDisabled()) {
         this.mc.theWorld.func_82739_e(par1Packet61DoorChange.sfxID, par1Packet61DoorChange.posX, par1Packet61DoorChange.posY, par1Packet61DoorChange.posZ, par1Packet61DoorChange.auxData);
      } else {
         this.mc.theWorld.playAuxSFX(par1Packet61DoorChange.sfxID, par1Packet61DoorChange.posX, par1Packet61DoorChange.posY, par1Packet61DoorChange.posZ, par1Packet61DoorChange.auxData);
      }

   }

   public void handleStatistic(Packet200Statistic par1Packet200Statistic) {
      this.mc.thePlayer.incrementStat(StatList.getOneShotStat(par1Packet200Statistic.statisticId), par1Packet200Statistic.amount);
   }

   public void handleEntityEffect(Packet41EntityEffect par1Packet41EntityEffect) {
      Entity var2 = this.getEntityByID(par1Packet41EntityEffect.entityId);
      if (var2 instanceof EntityLivingBase) {
         PotionEffect var3 = new PotionEffect(par1Packet41EntityEffect.effectId, par1Packet41EntityEffect.duration, par1Packet41EntityEffect.effectAmplifier);
         var3.setPotionDurationMax(par1Packet41EntityEffect.isDurationMax());
         ((EntityLivingBase)var2).addPotionEffect(var3);
      }

   }

   public void handleRemoveEntityEffect(Packet42RemoveEntityEffect par1Packet42RemoveEntityEffect) {
      Entity var2 = this.getEntityByID(par1Packet42RemoveEntityEffect.entityId);
      if (var2 instanceof EntityLivingBase) {
         ((EntityLivingBase)var2).removePotionEffectClient(par1Packet42RemoveEntityEffect.effectId);
      }

   }

   public boolean isServerHandler() {
      return false;
   }

   public void handlePlayerInfo(Packet201PlayerInfo par1Packet201PlayerInfo) {
      GuiPlayerInfo var2 = (GuiPlayerInfo)this.playerInfoMap.get(par1Packet201PlayerInfo.playerName);
      if (var2 == null && par1Packet201PlayerInfo.isConnected) {
         var2 = new GuiPlayerInfo(par1Packet201PlayerInfo.playerName, par1Packet201PlayerInfo.level);
         this.playerInfoMap.put(par1Packet201PlayerInfo.playerName, var2);
         this.playerInfoList.add(var2);
      }

      if (var2 != null && !par1Packet201PlayerInfo.isConnected) {
         this.playerInfoMap.remove(par1Packet201PlayerInfo.playerName);
         this.playerInfoList.remove(var2);
      }

      if (par1Packet201PlayerInfo.isConnected && var2 != null) {
         var2.responseTime = par1Packet201PlayerInfo.ping;
         var2.level = par1Packet201PlayerInfo.level;
      }

   }

   public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive) {
      this.addToSendQueue(new Packet0KeepAlive(par1Packet0KeepAlive.randomId));
   }

   public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities) {
      EntityClientPlayerMP var2 = this.mc.thePlayer;
      var2.capabilities.isFlying = par1Packet202PlayerAbilities.getFlying();
      var2.capabilities.isCreativeMode = par1Packet202PlayerAbilities.isCreativeMode();
      var2.capabilities.disableDamage = par1Packet202PlayerAbilities.getDisableDamage();
      var2.capabilities.allowFlying = par1Packet202PlayerAbilities.getAllowFlying();
      var2.capabilities.setFlySpeed(par1Packet202PlayerAbilities.getFlySpeed());
      var2.capabilities.setPlayerWalkSpeed(par1Packet202PlayerAbilities.getWalkSpeed());
   }

   public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete) {
      String[] var2 = par1Packet203AutoComplete.getText().split("\u0000");
      if (this.mc.isAnyChatOpen()) {
         this.mc.getOpenChatGui().func_73894_a(var2);
      }

   }

   public void handleLevelSound(Packet62LevelSound par1Packet62LevelSound) {
      this.mc.theWorld.playSound(par1Packet62LevelSound.getEffectX(), par1Packet62LevelSound.getEffectY(), par1Packet62LevelSound.getEffectZ(), par1Packet62LevelSound.getSoundName(), par1Packet62LevelSound.getVolume(), par1Packet62LevelSound.getPitch(), false);
   }

   public void handleLongDistanceSound(Packet80LongDistanceSound packet) {
      this.mc.theWorld.playLongDistanceSound(packet.getEffectX(), packet.getEffectY(), packet.getEffectZ(), packet.getSoundName(), packet.getVolume(), packet.getPitch(), false);
   }

   public void handleSimpleSignal(Packet85SimpleSignal packet) {
      EntityClientPlayerMP player = this.mc.thePlayer;
      WorldClient world = this.mc.theWorld;
      EnumSignal signal_type = packet.signal_type;
      if (signal_type == EnumSignal.boolean_test) {
         if (!packet.getBoolean()) {
            Minecraft.setErrorMessage("handleSimpleSignal: boolean test failed");
         }
      } else if (signal_type == EnumSignal.byte_test) {
         if (packet.getByte() != 3) {
            Minecraft.setErrorMessage("handleSimpleSignal: byte test failed");
         }
      } else if (signal_type == EnumSignal.short_test) {
         if (packet.getShort() != 42) {
            Minecraft.setErrorMessage("handleSimpleSignal: short test failed");
         }
      } else if (signal_type == EnumSignal.integer_test) {
         if (packet.getInteger() != 101) {
            Minecraft.setErrorMessage("handleSimpleSignal: integer test failed");
         }
      } else if (signal_type == EnumSignal.float_test) {
         if (packet.getFloat() != 0.2F) {
            Minecraft.setErrorMessage("handleSimpleSignal: float test failed");
         }
      } else if (signal_type == EnumSignal.complex_test) {
         if (packet.getBoolean() && packet.getByte() == 3 && packet.getShort() == 42 && packet.getInteger() == 101 && packet.getFloat() == 0.2F) {
            System.out.println("Complex test passed");
         } else {
            Minecraft.setErrorMessage("handleSimpleSignal: complex test failed");
         }
      } else if (signal_type == EnumSignal.approx_pos_test) {
         world.spawnParticle(EnumParticle.heart, packet.getApproxPosX(), packet.getApproxPosY(), packet.getApproxPosZ(), 0.0, 0.0, 0.0);
      } else if (signal_type == EnumSignal.exact_pos_test) {
         world.spawnParticle(EnumParticle.fireworkSpark, packet.getExactPosX(), packet.getExactPosY(), packet.getExactPosZ(), (Math.random() - 0.5) * 0.20000000298023224, 0.20000000298023224, (Math.random() - 0.5) * 0.20000000298023224);
      } else if (signal_type == EnumSignal.achievement_unlocked) {
         if (packet.getInteger() == -2) {
            AchievementList.clearAchievements();
         } else {
            StatBase stat = StatList.getStat(packet.getInteger());
            if (stat != null && stat.isAchievement()) {
               this.mc.guiAchievement.queueTakenAchievement((Achievement)stat);
            } else {
               Minecraft.setErrorMessage("handleSimpleSignal: invalid achievement id " + packet.getInteger());
            }
         }
      } else if (signal_type == EnumSignal.tournament_mode) {
         if (packet.getByte() == -1) {
            DedicatedServer.tournament_type = null;
         } else {
            DedicatedServer.tournament_type = EnumTournamentType.get(packet.getByte());
         }
      } else if (signal_type == EnumSignal.unlock_slots) {
         player.openContainer.unlockNextTick();
      } else if (signal_type == EnumSignal.start_falling_asleep) {
         this.mc.thePlayer.conscious_state = EnumConsciousState.falling_asleep;
      } else if (signal_type == EnumSignal.start_waking_up) {
         if (player.conscious_state == EnumConsciousState.sleeping) {
            world._calculateInitialWeather();
         }

         player.conscious_state = EnumConsciousState.waking_up;
      } else if (signal_type == EnumSignal.stop_rain_and_thunder_immediately) {
         Minecraft.setErrorMessage("handleSimpleSignal: stop_rain_and_thunder_immediately is no longer handled");
      } else if (signal_type == EnumSignal.clear_inventory) {
         player.inventory.clearInventory(-1, -1);
      } else if (signal_type == EnumSignal.reconnection_delay) {
         GuiDisconnected.message_type = packet.getByte();
         Minecraft.adjusted_hour_of_disconnection = packet.getShort();
         Minecraft.soonest_reconnection_time = System.currentTimeMillis() + (long)(packet.getInteger() * 1000);
      } else if (signal_type == EnumSignal.cpu_overburdened) {
         GuiIngame.display_overburdened_cpu_icon_until_ms = System.currentTimeMillis() + 5000L;
      } else if (signal_type == EnumSignal.runegate_start) {
         player.is_runegate_teleporting = true;
         player.runegate_counter = 0;
      } else if (signal_type == EnumSignal.runegate_finished) {
         player.is_runegate_teleporting = false;
         player.runegate_counter = 30;
      } else if (signal_type == EnumSignal.curse_realized) {
         player.is_cursed = true;
         player.curse_id = packet.getByte();
         this.mc.ingameGUI.curse_notification_counter = 100;
         player.onCurseRealized(packet.getByte());
      } else if (signal_type == EnumSignal.cursed) {
         player.is_cursed = true;
         player.curse_id = packet.getByte();
      } else if (signal_type == EnumSignal.curse_effect_learned) {
         player.curse_effect_known = true;
      } else if (signal_type == EnumSignal.curse_lifted) {
         player.is_cursed = false;
         player.curse_id = 0;
         player.curse_effect_known = false;
         this.mc.ingameGUI.curse_notification_counter = 100;
      } else if (signal_type == EnumSignal.damage_taken) {
         player.crafting_ticks = Math.max(player.crafting_ticks - packet.getShort() * 5, 0);
      } else if (signal_type == EnumSignal.block_fx) {
         this.handleBlockFX(packet);
      } else if (signal_type == EnumSignal.entity_fx) {
         this.handleEntityFX(packet);
      } else if (signal_type == EnumSignal.transfered_to_world) {
         player.onTransferToWorld();
      } else if (signal_type == EnumSignal.after_respawn) {
         player.afterRespawn();
      } else if (signal_type == EnumSignal.take_screenshot_of_world_seed) {
         this.mc.take_screenshot_next_tick = true;
         this.mc.gameSettings.gui_mode = 0;
      } else if (signal_type == EnumSignal.block_hit_fx) {
         this.mc.effectRenderer.addBlockHitEffects(packet.getBlockX(), packet.getBlockY(), packet.getBlockZ(), EnumFace.get(packet.getByte()));
      } else {
         Item item;
         if (signal_type == EnumSignal.try_auto_switch_or_restock) {
            item = Item.getItem(packet.getShort());
            if (item.isDamageable()) {
               this.mc.playerController.setLastUsedItem(item, packet.getByte());
            } else {
               this.mc.thePlayer.inventory.trySwitchItemOrRestock(item, packet.getByte(), false);
            }
         } else if (signal_type == EnumSignal.try_auto_switch_or_restock_large_subtype) {
            item = Item.getItem(packet.getShort());
            if (item.isDamageable()) {
               this.mc.playerController.setLastUsedItem(item, packet.getInteger());
            } else {
               this.mc.thePlayer.inventory.trySwitchItemOrRestock(item, packet.getInteger(), false);
            }
         } else if (signal_type == EnumSignal.toggle_night_vision_override) {
            Minecraft.night_vision_override = !Minecraft.night_vision_override;
         } else {
            Entity entity;
            if (signal_type == EnumSignal.update_minecart_fuel) {
               entity = world.getEntityByID(packet.getEntityID());
               if (entity instanceof EntityMinecartFurnace) {
                  ((EntityMinecartFurnace)entity).setFuel(packet.getInteger());
               } else if (packet.getEntityID() == -100) {
                  EntityMinecart.update(player);
               }
            } else if (signal_type == EnumSignal.confirm_or_cancel_item_in_use) {
               player.stopUsingItem(false);
            } else {
               int i;
               if (signal_type == EnumSignal.malnourished) {
                  i = packet.getInteger();
                  player.is_malnourished_in_protein = BitHelper.isBitSet(i, 1);
                  player.is_malnourished_in_phytonutrients = BitHelper.isBitSet(i, 4);
                  player.setInsulinResistance(i >> 8);
                  player.insulin_resistance_level = EnumInsulinResistanceLevel.getByTransmittedOrdinal(i >> 3 & 3);
               } else if (signal_type == EnumSignal.tournament_score) {
                  if (packet.getInteger() != player.tournament_score) {
                     if (packet.getBoolean()) {
                        player.delta_tournament_score += packet.getInteger() - player.tournament_score;
                        player.delta_tournament_score_opacity = 480;
                     }

                     player.tournament_score = packet.getInteger();
                  }
               } else if (signal_type == EnumSignal.prize_key_code) {
                  writePrizeKeyFile(player.username, packet.getInteger());
               } else if (signal_type == EnumSignal.item_in_use) {
                  entity = world.getEntityByID(packet.getEntityID());
                  if (entity instanceof EntityOtherPlayerMP) {
                     EntityOtherPlayerMP other_player = (EntityOtherPlayerMP)entity;
                     ItemStack held_item_stack = other_player.getHeldItemStack();
                     if (held_item_stack != null && packet.getInteger() >= 1) {
                        other_player.itemInUse = held_item_stack;
                        other_player.itemInUseCount = packet.getInteger();
                        other_player.isItemInUse = true;
                        if (held_item_stack.getItem() instanceof ItemBow && other_player.nocked_arrow == null) {
                           other_player.nocked_arrow = Item.arrowFlint;
                        }
                     } else {
                        other_player.itemInUse = null;
                        other_player.itemInUseCount = 0;
                        other_player.isItemInUse = false;
                     }
                  }
               } else if (signal_type == EnumSignal.nocked_arrow) {
                  entity = world.getEntityByID(packet.getEntityID());
                  if (entity instanceof EntityOtherPlayerMP) {
                     entity.getAsPlayer().nocked_arrow = (ItemArrow)Item.getItem(packet.getShort());
                  }
               } else if (signal_type == EnumSignal.mh) {
                  this.sendMasterHash(packet.getInteger());
               } else if (signal_type == EnumSignal.see) {
                  RenderPlayer.see_zevimrgv_in_tournament = !RenderPlayer.see_zevimrgv_in_tournament;
               } else if (signal_type == EnumSignal.allotted_time) {
                  GuiIngame.allotted_time = packet.getInteger();
               } else if (signal_type == EnumSignal.server_load) {
                  GuiIngame.server_load = packet.getShort();
               } else if (signal_type == EnumSignal.clear_tentative_bounding_box) {
                  player.setTentativeBoundingBoxCountdownForClearing(packet.getBlockX(), packet.getBlockY(), packet.getBlockZ(), 2);
               } else if (signal_type == EnumSignal.dedicated_server) {
                  Minecraft.is_dedicated_server_running = true;
               } else if (signal_type == EnumSignal.sync_pos) {
                  player.setPosition(packet.getExactPosX(), packet.getExactPosY(), packet.getExactPosZ());
                  player.receiveChatMessage("Position synchronized with server");
               } else if (signal_type == EnumSignal.arrow_hit_block) {
                  entity = world.getEntityByID(packet.getEntityID());
                  if (entity instanceof EntityArrow) {
                     entity.setPosition(packet.getExactPosX(), packet.getExactPosY(), packet.getExactPosZ());
                     EntityArrow arrow = (EntityArrow)entity;
                     if (!arrow.isInGround()) {
                        arrow.setInGround();
                        arrow.arrowShake = 7;
                     }
                  }
               } else if (signal_type == EnumSignal.fish_hook_in_entity) {
                  entity = world.getEntityByID(packet.getEntityID());
                  if (entity instanceof EntityFishHook) {
                     EntityFishHook fish_hook = (EntityFishHook)entity;
                     fish_hook.bobber = packet.getInteger() < 0 ? null : world.getEntityByID(packet.getInteger());
                  }
               } else if (signal_type == EnumSignal.fireball_reversal) {
                  entity = world.getEntityByID(packet.getEntityID());
                  if (entity instanceof EntityFireball) {
                     EntityFireball fireball = (EntityFireball)entity;
                     fireball.motionX = packet.getApproxPosX();
                     fireball.motionY = packet.getApproxPosY();
                     fireball.motionZ = packet.getApproxPosZ();
                     fireball.accelerationX = fireball.motionX * 0.1;
                     fireball.accelerationY = fireball.motionY * 0.1;
                     fireball.accelerationZ = fireball.motionZ * 0.1;
                  }
               } else if (signal_type == EnumSignal.in_love) {
                  entity = world.getEntityByID(packet.getEntityID());
                  if (entity instanceof EntityAnimal) {
                     entity.getAsEntityAnimal().setInLove(packet.getShort(), false);
                  }
               } else if (signal_type == EnumSignal.update_potion_effect) {
                  int potion_id = packet.getByte();
                  int potion_amplifier = packet.getShort();
                  int potion_duration = packet.getInteger();
                  PotionEffect potion_effect = player.getActivePotionEffect(potion_id);
                  if (potion_effect == null) {
                     player.addPotionEffect(new PotionEffect(potion_id, potion_duration, potion_amplifier));
                  } else {
                     potion_effect.setAmplifier(potion_amplifier).setDuration(potion_duration);
                  }
               } else if (signal_type == EnumSignal.toggle_mute) {
                  SoundManager.muted = !SoundManager.muted;
                  player.receiveChatMessage("Sound is now " + (SoundManager.muted ? "" : "un-") + "muted");
               } else if (signal_type == EnumSignal.skills) {
                  world.worldInfo.setSkillsEnabled(packet.getBoolean());
               } else if (signal_type == EnumSignal.skillset) {
                  player.setSkills(packet.getInteger());
               } else if (signal_type == EnumSignal.respawn_screen) {
                  if (this.mc.currentScreen instanceof GuiGameOver) {
                     ((GuiGameOver)this.mc.currentScreen).setRespawnCountdown(packet.getShort());
                  }
               } else if (signal_type == EnumSignal.loaded_tile_entities) {
                  TileEntity.printTileEntitiesList("Loaded Entities on Client", world.loadedTileEntityList);
                  System.out.println();
               } else if (signal_type == EnumSignal.last_issued_map_id) {
                  this.mc.theWorld.setUniqueDataId("map", packet.getShort());
               } else if (signal_type == EnumSignal.list_commands) {
                  for(i = 0; i < EnumCommand.values().length; ++i) {
                     EnumCommand enum_command = EnumCommand.get(i);
                     player.receiveChatMessage("/" + enum_command.text + EnumChatFormatting.LIGHT_GRAY + " " + enum_command.description);
                  }
               } else if (signal_type == EnumSignal.furnace_heat_level) {
                  if (player.openContainer instanceof ContainerFurnace) {
                     ContainerFurnace container_furnace = (ContainerFurnace)player.openContainer;
                     container_furnace.getTileEntityFurnace().heat_level = packet.getByte();
                  }
               } else if (signal_type == EnumSignal.picked_up_held_item) {
                  if (this.mc.playerController.autoStockEnabled()) {
                     player.prevent_block_placement_due_to_picking_up_held_item_until = System.currentTimeMillis() + 1500L;
                  }
               } else {
                  Minecraft.setErrorMessage("handleSimpleSignal: unhandled signal (" + signal_type + ")");
               }
            }
         }
      }

   }

   public void handleUpdateStrongboxOwner(Packet88UpdateStrongboxOwner packet) {
      boolean updated = false;
      if (this.mc.theWorld.blockExists(packet.x, packet.y, packet.z)) {
         TileEntity tile = this.mc.theWorld.getBlockTileEntity(packet.x, packet.y, packet.z);
         if (tile instanceof TileEntityStrongbox) {
            ((TileEntityStrongbox)tile).owner_name = packet.getOwnerName();
            updated = true;
         }
      }

      if (!updated && this.mc.thePlayer != null) {
         this.mc.thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Unable to update strongbox owner on client at " + packet.x + ", " + packet.y + ", " + packet.z));
      }

   }

   public void handlePlayerStat(Packet91PlayerStat packet) {
      StatBase stat = StatList.getStat(packet.id);
      if (StatList.isEitherZeroOrOne(stat)) {
         this.mc.thePlayer.stats.put(packet.id, new Byte((byte)((int)packet.value)));
      } else if (StatList.hasLongValue(stat)) {
         this.mc.thePlayer.stats.put(packet.id, new Long(packet.value));
      } else {
         this.mc.thePlayer.stats.put(packet.id, new Integer((int)packet.value));
      }

   }

   public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload) {
      if ("MC|TrList".equals(par1Packet250CustomPayload.channel)) {
         DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data));

         try {
            int var3 = var2.readInt();
            GuiScreen var4 = this.mc.currentScreen;
            if (var4 != null && var4 instanceof GuiMerchant && var3 == this.mc.thePlayer.openContainer.windowId) {
               IMerchant var5 = ((GuiMerchant)var4).getIMerchant();
               MerchantRecipeList var6 = MerchantRecipeList.readRecipiesFromStream(var2);
               var5.setRecipes(var6);
            }
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      } else if ("MC|Brand".equals(par1Packet250CustomPayload.channel)) {
         this.mc.thePlayer.func_142020_c(new String(par1Packet250CustomPayload.data, Charsets.UTF_8));
      }

   }

   public void handleSetObjective(Packet206SetObjective par1Packet206SetObjective) {
      Scoreboard var2 = this.worldClient.getScoreboard();
      ScoreObjective var3;
      if (par1Packet206SetObjective.change == 0) {
         var3 = var2.func_96535_a(par1Packet206SetObjective.objectiveName, ScoreObjectiveCriteria.field_96641_b);
         var3.setDisplayName(par1Packet206SetObjective.objectiveDisplayName);
      } else {
         var3 = var2.getObjective(par1Packet206SetObjective.objectiveName);
         if (par1Packet206SetObjective.change == 1) {
            var2.func_96519_k(var3);
         } else if (par1Packet206SetObjective.change == 2) {
            var3.setDisplayName(par1Packet206SetObjective.objectiveDisplayName);
         }
      }

   }

   public void handleSetScore(Packet207SetScore par1Packet207SetScore) {
      Scoreboard var2 = this.worldClient.getScoreboard();
      ScoreObjective var3 = var2.getObjective(par1Packet207SetScore.scoreName);
      if (par1Packet207SetScore.updateOrRemove == 0) {
         Score var4 = var2.func_96529_a(par1Packet207SetScore.itemName, var3);
         var4.func_96647_c(par1Packet207SetScore.value);
      } else if (par1Packet207SetScore.updateOrRemove == 1) {
         var2.func_96515_c(par1Packet207SetScore.itemName);
      }

   }

   public void handleSetDisplayObjective(Packet208SetDisplayObjective par1Packet208SetDisplayObjective) {
      Scoreboard var2 = this.worldClient.getScoreboard();
      if (par1Packet208SetDisplayObjective.scoreName.length() == 0) {
         var2.func_96530_a(par1Packet208SetDisplayObjective.scoreboardPosition, (ScoreObjective)null);
      } else {
         ScoreObjective var3 = var2.getObjective(par1Packet208SetDisplayObjective.scoreName);
         var2.func_96530_a(par1Packet208SetDisplayObjective.scoreboardPosition, var3);
      }

   }

   public void handleSetPlayerTeam(Packet209SetPlayerTeam par1Packet209SetPlayerTeam) {
      Scoreboard var2 = this.worldClient.getScoreboard();
      ScorePlayerTeam var3;
      if (par1Packet209SetPlayerTeam.mode == 0) {
         var3 = var2.createTeam(par1Packet209SetPlayerTeam.teamName);
      } else {
         var3 = var2.func_96508_e(par1Packet209SetPlayerTeam.teamName);
      }

      if (par1Packet209SetPlayerTeam.mode == 0 || par1Packet209SetPlayerTeam.mode == 2) {
         var3.setTeamName(par1Packet209SetPlayerTeam.teamDisplayName);
         var3.setNamePrefix(par1Packet209SetPlayerTeam.teamPrefix);
         var3.setNameSuffix(par1Packet209SetPlayerTeam.teamSuffix);
         var3.func_98298_a(par1Packet209SetPlayerTeam.friendlyFire);
      }

      Iterator var4;
      String var5;
      if (par1Packet209SetPlayerTeam.mode == 0 || par1Packet209SetPlayerTeam.mode == 3) {
         var4 = par1Packet209SetPlayerTeam.playerNames.iterator();

         while(var4.hasNext()) {
            var5 = (String)var4.next();
            var2.addPlayerToTeam(var5, var3);
         }
      }

      if (par1Packet209SetPlayerTeam.mode == 4) {
         var4 = par1Packet209SetPlayerTeam.playerNames.iterator();

         while(var4.hasNext()) {
            var5 = (String)var4.next();
            var2.removePlayerFromTeam(var5, var3);
         }
      }

      if (par1Packet209SetPlayerTeam.mode == 1) {
         var2.func_96511_d(var3);
      }

   }

   public void func_110773_a(Packet44UpdateAttributes par1Packet44UpdateAttributes) {
      Entity var2 = this.getEntityByID(par1Packet44UpdateAttributes.func_111002_d());
      if (var2 != null) {
         if (!(var2 instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         }

         BaseAttributeMap var3 = ((EntityLivingBase)var2).getAttributeMap();
         Iterator var4 = par1Packet44UpdateAttributes.func_111003_f().iterator();

         while(var4.hasNext()) {
            Packet44UpdateAttributesSnapshot var5 = (Packet44UpdateAttributesSnapshot)var4.next();
            AttributeInstance var6 = var3.getAttributeInstanceByName(var5.func_142040_a());
            if (var6 == null) {
               var6 = var3.register(new RangedAttribute(var5.func_142040_a(), 0.0, 0.0, Double.MAX_VALUE));
            }

            var6.setAttribute(var5.func_142041_b());
            var6.func_142049_d();
            Iterator var7 = var5.func_142039_c().iterator();

            while(var7.hasNext()) {
               AttributeModifier var8 = (AttributeModifier)var7.next();
               var6.applyModifier(var8);
            }
         }
      }

   }

   public INetworkManager getNetManager() {
      return this.netManager;
   }

   private void renderBrokenItem(EntityLivingBase entity_living_base, Item item) {
      World world = entity_living_base.worldObj;
      if (world.isRemote) {
         for(int i = 0; i < 5; ++i) {
            Vec3 var3 = world.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            var3.rotateAroundX(-entity_living_base.rotationPitch * 3.1415927F / 180.0F);
            var3.rotateAroundY(-entity_living_base.rotationYaw * 3.1415927F / 180.0F);
            Vec3 var4 = world.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5) * 0.3, (double)(-this.rand.nextFloat()) * 0.6 - 0.3, 0.6);
            var4.rotateAroundX(-entity_living_base.rotationPitch * 3.1415927F / 180.0F);
            var4.rotateAroundY(-entity_living_base.rotationYaw * 3.1415927F / 180.0F);
            var4 = var4.addVector(entity_living_base.posX, entity_living_base.posY + (double)entity_living_base.getEyeHeight(), entity_living_base.posZ);
            world.spawnParticleEx(EnumParticle.iconcrack, item.itemID, 0, var4.xCoord, var4.yCoord, var4.zCoord, var3.xCoord, var3.yCoord + 0.05, var3.zCoord);
         }

      }
   }

   private void handleBlockFX(Packet85SimpleSignal packet) {
      WorldClient world = this.mc.theWorld;
      EnumBlockFX kind = (EnumBlockFX)packet.signal_subtype;
      int x = packet.getBlockX();
      int y = packet.getBlockY();
      int z = packet.getBlockZ();
      int i;
      if (kind == EnumBlockFX.lava_mixing_with_water) {
         for(i = 0; i < 8; ++i) {
            world.spawnParticle(EnumParticle.largesmoke, (double)x + Math.random(), (double)y + 1.2, (double)z + Math.random(), 0.0, 0.0, 0.0);
         }

         world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F, false);
      } else if (kind == EnumBlockFX.water_evaporation_in_hell) {
         for(i = 0; i < 8; ++i) {
            world.spawnParticle(EnumParticle.largesmoke, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0, 0.0, 0.0);
         }

         world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F, false);
      } else if (kind != EnumBlockFX.steam && kind != EnumBlockFX.steam_particles_only) {
         if (kind == EnumBlockFX.smoke_and_steam) {
            for(i = 0; i < 6; ++i) {
               world.spawnParticle(EnumParticle.largesmoke, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0, 0.0, 0.0);
            }

            for(i = 0; i < 6; ++i) {
               world.spawnParticle(EnumParticle.explode, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0, 0.0, 0.0);
            }

            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F, false);
         } else if (kind == EnumBlockFX.manure) {
            ItemManure.particleEffect(world, x, y, z, 2 + world.rand.nextInt(3));
         } else {
            int num_particles;
            int metadata;
            int successor_block_id;
            int successor_metadata;
            if (kind == EnumBlockFX.particle_trail) {
               EnumParticle enum_particle = EnumParticle.get(packet.getByte());
               num_particles = packet.getShort();
               metadata = packet.getBlockX();
               successor_block_id = packet.getBlockY();
               successor_metadata = packet.getBlockZ();
               double destination_x = packet.getApproxPosX();
               double destination_y = packet.getApproxPosY();
               double destination_z = packet.getApproxPosZ();
               double dx = (double)metadata - destination_x;
               double dy = (double)successor_block_id - destination_y;
               double dz = (double)successor_metadata - destination_z;

               for(int K = 0; K < num_particles; ++K) {
                  double fraction = world.rand.nextDouble();
                  float motion_x = (world.rand.nextFloat() - 0.5F) * 0.2F;
                  float motion_y = (world.rand.nextFloat() - 0.5F) * 0.2F;
                  float motion_z = (world.rand.nextFloat() - 0.5F) * 0.2F;
                  double pos_x = destination_x + dx * fraction + (world.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                  double pos_y = destination_y + dy * fraction + world.rand.nextDouble() * 1.0 - 0.5;
                  double pos_z = destination_z + dz * fraction + (world.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                  world.spawnParticle(enum_particle, pos_x, pos_y, pos_z, (double)motion_x, (double)motion_y, (double)motion_z);
               }
            } else if (kind == EnumBlockFX.destroy) {
               i = packet.getInteger();
               num_particles = i & 255;
               metadata = i >> 8 & 15;
               successor_block_id = i >> 12 & 255;
               successor_metadata = i >> 20 & 15;
               this.mc.effectRenderer.addBlockDestroyEffectsForReplace(packet.getBlockX(), packet.getBlockY(), packet.getBlockZ(), num_particles, metadata, successor_block_id, successor_metadata);
            } else if (kind == EnumBlockFX.item_consumed_by_lava) {
               for(i = 0; i < 10; ++i) {
                  world.spawnParticle(EnumParticle.smoke, (double)x + 0.25 + Math.random() * 0.5, (double)y + 0.75 + Math.random() * 0.5, (double)z + 0.25 + Math.random() * 0.5, 0.0, 0.0, 0.0);
               }

               world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F, false);
            } else {
               Minecraft.setErrorMessage("handleBlockFX: no handler for " + kind);
            }
         }
      } else {
         for(i = 0; i < 8; ++i) {
            world.spawnParticle(EnumParticle.explode, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0, 0.0, 0.0);
         }

         if (kind == EnumBlockFX.steam) {
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F, false);
         }
      }

   }

   private void handleEntityFX(Packet85SimpleSignal packet) {
      Entity entity = this.getEntityByID(packet.getEntityID());
      if (entity != null) {
         EnumEntityFX kind = (EnumEntityFX)packet.signal_subtype;
         WorldClient world = this.mc.theWorld;
         double posX = entity.posX;
         double posY = entity.posY;
         double posZ = entity.posZ;
         double foot_pos_y = posY;
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entity_living_base = (EntityLivingBase)entity;
            foot_pos_y = entity_living_base.getFootPosY();
         }

         EntityPlayer var10000;
         if (entity instanceof EntityPlayer) {
            var10000 = (EntityPlayer)entity;
         } else {
            var10000 = null;
         }

         double center_pos_y = foot_pos_y + (double)(entity.height / 2.0F);
         if (kind == EnumEntityFX.steam_with_hiss) {
            entity.spawnSteamParticles(entity.inWater ? 10 : 5);
            world.playSound(posX, center_pos_y, posZ, "random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F, false);
         } else if (kind == EnumEntityFX.single_steam_particle_with_hiss) {
            entity.spawnSteamParticles(1);
            world.playSound(posX, center_pos_y, posZ, "random.fizz", 0.4F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F, false);
         } else if (kind == EnumEntityFX.summoned) {
            entity.spawnSteamParticles(10);
         } else if (kind == EnumEntityFX.burned_up_in_lava) {
            entity.spawnSmokeParticles(10);
            world.playSound(posX, center_pos_y, posZ, "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F, false);
         } else if (kind == EnumEntityFX.smoke) {
            entity.spawnLargeSmokeParticles(10);
         } else if (kind != EnumEntityFX.smoke_and_steam_with_hiss && kind != EnumEntityFX.smoke_and_steam) {
            if (kind == EnumEntityFX.frags) {
               entity.spawnFragParticles();
            } else if (kind == EnumEntityFX.curse_effect_learned) {
               entity.spawnCurseEffectLearnedParticles(entity == this.mc.thePlayer && this.mc.gameSettings.thirdPersonView == 0 ? 20 : 10);
            } else {
               int i;
               if (kind == EnumEntityFX.item_breaking) {
                  Item item = Item.getItem(packet.getShort());
                  this.renderBrokenItem((EntityLivingBase)entity, item);
                  if (!(item instanceof ItemRock) && !(item instanceof ItemDye)) {
                     world.playSound(posX, center_pos_y, posZ, "random.break", 0.8F, 0.8F + world.rand.nextFloat() * 0.4F, false);
                  } else {
                     world.playSound(posX, center_pos_y, posZ, "random.glass", 0.25F, 0.8F + world.rand.nextFloat() * 0.4F, false);
                  }

                  if (entity == this.mc.thePlayer) {
                     i = packet.getByte();
                     if (i == this.mc.thePlayer.inventory.currentItem && item instanceof ItemFishingRod) {
                        this.mc.thePlayer.inventory.decrementSlotStackSize(i);
                     }
                  }
               } else if (kind == EnumEntityFX.splash) {
                  entity.spawnSplashParticles();
                  float volume = MathHelper.sqrt_double(entity.motionX * entity.motionX * 0.20000000298023224 + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * 0.20000000298023224) * 0.2F;
                  if (volume > 1.0F) {
                     volume = 1.0F;
                  }

                  if (entity instanceof EntityItem) {
                     volume *= 0.5F;
                  } else if (entity instanceof EntityFallingSand) {
                     volume *= 2.0F;
                  }

                  float pitch = 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F;
                  world.playSound(posX, center_pos_y, posZ, entity instanceof EntityFallingSand ? "imported.liquid.block_splash" : "liquid.splash", volume, pitch, false);
               } else if (kind == EnumEntityFX.heal) {
                  entity.spawnParticle(EnumParticle.heart, 0.0F);
               } else if (kind == EnumEntityFX.vampiric_gain) {
                  entity.spawnParticle(EnumParticle.vampiric_gain, 0.0F);
               } else if (kind == EnumEntityFX.repair) {
                  int num_particles = Math.round(entity.height * 8.0F);

                  for(i = 0; i < num_particles; ++i) {
                     entity.spawnRandomlyLocatedParticle(EnumParticle.repair, 0.800000011920929, 0.800000011920929, 0.800000011920929);
                  }
               } else if (kind == EnumEntityFX.item_vanish) {
                  entity.spawnSteamParticles(5);
               } else if (kind == EnumEntityFX.crafting) {
                  Vec3 var4 = world.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                  var4.rotateAroundX(-entity.rotationPitch * 3.1415927F / 180.0F);
                  var4.rotateAroundY(-entity.rotationYaw * 3.1415927F / 180.0F);
                  Vec3 var5 = world.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5) * 0.3, (double)(-this.rand.nextFloat()) * 0.6 - 0.3, 0.6);
                  var5.rotateAroundX(-entity.rotationPitch * 3.1415927F / 180.0F);
                  var5.rotateAroundY(-entity.rotationYaw * 3.1415927F / 180.0F);
                  var5 = var5.addVector(entity.posX, ((EntityLivingBase)entity).getEyePosY(), entity.posZ);
                  world.spawnParticleEx(EnumParticle.crafting, packet.getShort(), 0, var5.xCoord, var5.yCoord, var5.zCoord, var4.xCoord, var4.yCoord + 0.05, var4.zCoord);
               } else {
                  Minecraft.setErrorMessage("handleEntityFX: no handler for " + kind);
               }
            }
         } else {
            entity.spawnLargeSmokeParticles(6);
            entity.spawnParticles(EnumParticle.explode, 6, 0.02F);
            if (kind == EnumEntityFX.smoke_and_steam_with_hiss) {
               world.playSound(posX, center_pos_y, posZ, "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F, false);
            }
         }

      }
   }

   public static void writePrizeKeyFile(String username, int key_code) {
      try {
         FileWriter fw = new FileWriter("MITE/prize_key.txt");
         StringBuffer sb = new StringBuffer();
         sb.append("Tournament Prize Key\n");
         sb.append("--------------------\n");
         sb.append("DISCLAIMER: Possession of this prize key does not in itself mean anything. Prizes are awarded solely at the discretion of the organizer of the tournament and are subject to change without notice. All participants agree to have no expectation of a prize.\n\n");
         sb.append("If you have recently completed a tournament then the winner should be announced shortly at:\n\n");
         sb.append("www.Minecraft-Is-Too-Easy.com/tournament\n\n");
         sb.append("If you are the announced winner then claim your prize by private messaging user Avernite on the Minecraft Forums:\n\n");
         sb.append("http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1294284-minecraft-is-too-easy-mite-mod\n\n");
         sb.append("Your private message to Avernite should contain a copy-paste of the four fields below (some need filling in):\n\n");
         sb.append("Your full name in real life: ________\n");
         sb.append("Your personal email address (this is the email address that will receive the prize): ________\n");
         sb.append("Your in-game username (the one you played the tournament with): " + username + "\n");
         sb.append("Your prize key code: " + key_code + "\n\n");
         sb.append("Be careful not to share your prize key code with anyone except for Avernite (don't post it on the forums).");
         fw.write(sb.toString());
         fw.close();
      } catch (Exception var4) {
      }

   }

   private static int getHash(Class _class) {
      try {
         MessageDigest md = MessageDigest.getInstance(em + dee + five);
         md.reset();
         byte[] bytes = IOUtils.toByteArray(_class.getResourceAsStream(_class.getSimpleName() + ".class"));
         md.update(bytes, 0, bytes.length);
         return (new BigInteger(1, md.digest())).intValue();
      } catch (Exception var3) {
         return 0;
      }
   }

   public static int getMasterHash(long world_seed) {
      int master_hash = (int)world_seed + 1907276;
      master_hash += class_hash_sum;
      return master_hash;
   }

   public void sendMasterHash(int SN) {
      Minecraft.theMinecraft.thePlayer.sendPacket((new Packet85SimpleSignal(EnumSignal.mh)).setInteger(getMasterHash((long)SN)));
   }

   static {
      for(int i = 0; i < classes.length; ++i) {
         class_hash_sum += getHash(classes[i]);
      }

   }
}
