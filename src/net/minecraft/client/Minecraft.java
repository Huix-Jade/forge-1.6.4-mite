package net.minecraft.client;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Proxy;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMITEDS;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.LoadingScreenRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.Main;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ReloadableResourceManager;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.client.resources.ResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepositoryEntry;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityStatsDump;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.logging.ILogAgent;
import net.minecraft.logging.LogAgent;
import net.minecraft.mite.MITEConstant;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.RightClickFilter;
import net.minecraft.network.packet.Packet81RightClick;
import net.minecraft.network.packet.Packet85SimpleSignal;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.ProfilerResult;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ThreadMinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Debug;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumEntityReachContext;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumOS;
import net.minecraft.util.EnumSignal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ReferenceFileWriter;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public final class Minecraft implements IPlayerUsage {
	private static boolean in_dev_mode = false;
	public static final short MITE_release_number = 196;
	public static final int earliest_allowable_MITE_release = 149;
	public static final int[] incompatible_releases = new int[]{153, 155, 156, 157, 162, 163, 167, 168, 171, 173, 174, 179, 180, 181, 183, 186, 187, 190, 191, 194, 195};
	public static final String MC_version = "1.6.4";
	private static char dev_mode_key_drive_letter = 'c';
	private static char dev_mode_key_colon = ':';
	private static char dev_mode_key_backslash = '\\';
	private static boolean dev_mode_key_exists;
	private static final ResourceLocation locationMojangPng;
	public static final boolean isRunningOnMac;
	public static byte[] memoryReserve;
	private static final List macDisplayModes = Lists.newArrayList(new DisplayMode[] {new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
	private final ILogAgent mcLogAgent;
	public static ILogAgent MITE_log;
	private final File fileResourcepacks;
	private ServerData currentServerData;
	public TextureManager renderEngine;
	public static Minecraft theMinecraft;
	public PlayerControllerMP playerController;
	private boolean fullscreen;
	private boolean hasCrashed;
	private CrashReport crashReporter;
	public int displayWidth;
	public int displayHeight;
	private Timer timer = new Timer(20.0F);
	private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getSystemTimeMillis());
	public WorldClient theWorld;
	public RenderGlobal renderGlobal;
	public EntityClientPlayerMP thePlayer;
	public EntityLivingBase renderViewEntity;
	public float raining_strength_for_render_view_entity;
	public float prev_raining_strength_for_render_view_entity;
	public EntityLivingBase pointedEntityLiving;
	public EffectRenderer effectRenderer;
	private final Session session;
	public boolean isGamePaused;
	public FontRenderer fontRenderer;
	public FontRenderer standardGalacticFontRenderer;
	public GuiScreen currentScreen;
	public GuiChat imposed_gui_chat;
	public LoadingScreenRenderer loadingScreen;
	public EntityRenderer entityRenderer;
	private int leftClickCounter;
	public int right_click_counter;
	private int tempDisplayWidth;
	private int tempDisplayHeight;
	private IntegratedServer theIntegratedServer;
	public GuiAchievement guiAchievement;
	public GuiIngame ingameGUI;
	public boolean skipRenderWorld;
	public RaycastCollision objectMouseOver;
	public GameSettings gameSettings;
	public SoundManager sndManager;
	public MouseHelper mouseHelper;
	public final File mcDataDir;
	public final File saves_dir_MITE;
	private final File fileAssets;
	private final String launchedVersion;
	private final Proxy proxy;
	private ISaveFormat saveLoader;
	private static int debugFPS;
	private boolean refreshTexturePacksScheduled;
	public StatFileWriter statFileWriter;
	private String serverName;
	private int serverPort;
	boolean isTakingScreenshot;
	public boolean inGameHasFocus;
	long systemTime = getSystemTime();
	private int joinPlayerCounter;
	private final boolean isDemo;
	private INetworkManager myNetworkManager;
	private boolean integratedServerIsRunning;
	public final Profiler mcProfiler = new Profiler();
	private long field_83002_am = -1L;
	private ReloadableResourceManager mcResourceManager;
	private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
	private List defaultResourcePacks = Lists.newArrayList();
	public DefaultResourcePack mcDefaultResourcePack;
	private ResourcePackRepository mcResourcePackRepository;
	private LanguageManager mcLanguageManager;
	public volatile boolean running = true;
	public String debug = "";
	long debugUpdateTime = getSystemTime();
	int fpsCounter;
	long prevFrameTime = -1L;
	private String debugProfilerName = "root";
	public static ResourcePack MITE_resource_pack;
	private static String error_message;
	public static final int max_height = 255;
	public static int last_fps;
	public static int last_fp10s;
	public static boolean see_through_block_tops;
	public static int adjusted_hour_of_disconnection;
	public static long soonest_reconnection_time;
	public static String[] hit_list;
	public boolean take_screenshot_next_tick;
	public boolean force_rendering_for_screenshot;
	private float block_hit_effect_progress;
	public int last_known_delta_tournament_score;
	public int last_known_delta_tournament_score_opacity;
	public int last_known_tournament_score;
	public static boolean is_dedicated_server_running;
	private PropertyManager settings;
	public static String temp_debug;
	public static final boolean professions_suppressed = true;
	public static boolean allow_new_sand_physics;
	public boolean increment_startGameStat_asap;
	public boolean increment_loadWorldStat_asap;
	public boolean increment_joinMultiplayerStat_asap;
	public static ThreadMinecraftServer server_thread;
	public static Thread client_thread;
	public static String last_aborted_chat;
	public static long disable_clicks_until;
	public static boolean night_vision_override;
	public static String server_pools_string;
	public static final String java_version;
	public static final boolean java_version_is_outdated;

	public static void setErrorMessage(String text) {
		setErrorMessage(text, true);
	}

	public static void setErrorMessage(String text, boolean echo_to_err) {
		if (echo_to_err && (error_message == null || !error_message.equals(text))) {
			System.err.println(text);
		}

		if (error_message == null) {
			error_message = text.replaceAll("\n", "");
		}
	}

	public static String getErrorMessage() {
		return error_message;
	}

	public static void clearErrorMessage() {
		error_message = null;
	}

	private static boolean doesDevModeKeyExist() {
		File file = new File("" + dev_mode_key_drive_letter + dev_mode_key_colon + dev_mode_key_backslash + "dm");
		if (file.exists() && file.canRead()) {
			try {
				BufferedReader bf = new BufferedReader(new FileReader(file));
				String contents = bf.readLine();
				bf.close();
				if ("dm".equals(contents)) {
					return true;
				}
			} catch (Exception var3) {
			}
		}

		return false;
	}

	public static boolean inDevMode() {
		if (in_dev_mode && !dev_mode_key_exists) {
			setErrorMessage(getSecretErrorMessage());
			in_dev_mode = false;
		}

		return true;
	}

	public Minecraft(Session par1Session, int par2, int par3, boolean par4, boolean par5, File par6File, File par7File, File par8File, Proxy par9Proxy, String par10Str) {
		theMinecraft = this;
		this.mcLogAgent = new LogAgent("Minecraft-Client", " [CLIENT]", (new File(par6File, "output-client.log")).getAbsolutePath());
		MITE_log = new LogAgent("MITE-Client", " [MITE-CLIENT]", (new File(par6File, "output-client-MITE.log")).getAbsolutePath());
		this.mcDataDir = par6File;
		this.saves_dir_MITE = new File(this.mcDataDir, "MITE/saves/1.6.4/");
		this.fileAssets = par7File;
		this.fileResourcepacks = par8File;
		File MITE_resource_pack_file = new File(this.fileResourcepacks, "MITE Resource Pack 1.6.4.zip");
		if (!MITE_resource_pack_file.exists()) {
			MITE_resource_pack_file = new File(this.fileResourcepacks, "MITE Resource Pack 1.6.4");
		}

		if (MITE_resource_pack_file.exists()) {
			if (MITE_resource_pack_file.isFile()) {
				MITE_resource_pack = new FileResourcePack(MITE_resource_pack_file);
			} else {
				MITE_resource_pack = new FolderResourcePack(MITE_resource_pack_file);
			}
		}

		this.launchedVersion = par10Str;
		this.mcDefaultResourcePack = new DefaultResourcePack(this.fileAssets);
		this.addDefaultResourcePack();
		this.proxy = par9Proxy;
		this.session = par1Session;
		this.mcLogAgent.logInfo("Starting minecraft client version 1.6.4-MITE (R" + 196 + ")" + (inDevMode() ? " DEV" : ""));
		this.mcLogAgent.logInfo("Setting user: " + par1Session.getUsername());
		this.mcLogAgent.logInfo("(Session ID is " + par1Session.getSessionID() + ")");
		this.isDemo = par5;
		this.displayWidth = par2;
		this.displayHeight = par3;
		this.tempDisplayWidth = par2;
		this.tempDisplayHeight = par3;
		this.fullscreen = par4;
		ImageIO.setUseCache(false);
		StatList.nopInit();
		if (hit_list == null) {
			hit_list = getHitList();
		}

		new ClientProperties("client.properties", this.getLogAgent());
	}

	public void crashed(CrashReport par1CrashReport) {
		this.hasCrashed = true;
		this.crashReporter = par1CrashReport;
	}

	public void displayCrashReport(CrashReport par1CrashReport) {
		File var2 = new File(getMinecraft().mcDataDir, "crash-reports");
		File var3 = new File(var2, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
		System.out.println(par1CrashReport.getCompleteReport());
		if (par1CrashReport.getFile() != null) {
			System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + par1CrashReport.getFile());
			System.exit(-1);
		} else if (par1CrashReport.saveToFile(var3, this.getLogAgent())) {
			System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + var3.getAbsolutePath());
			System.exit(-1);
		} else {
			System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
			System.exit(-2);
		}

	}

	public void setServer(String par1Str, int par2) {
		this.serverName = par1Str;
		this.serverPort = par2;
	}

	public static String getVersionDescriptor(boolean include_formatting) {
		if (include_formatting) {
			EnumChatFormatting.GREEN.toString();
		}

		String red = include_formatting ? EnumChatFormatting.RED.toString() : "";
		return "1.6.4-MITE" + (Main.is_MITE_DS ? "-DS" : "") + (inDevMode() ? red + " DEV" : "");
	}

	private void startGame() throws LWJGLException {
		this.gameSettings = new GameSettings(this, this.mcDataDir);
		if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
			this.displayWidth = this.gameSettings.overrideWidth;
			this.displayHeight = this.gameSettings.overrideHeight;
		}

		if (this.fullscreen) {
			Display.setFullscreen(true);
			this.displayWidth = Display.getDisplayMode().getWidth();
			this.displayHeight = Display.getDisplayMode().getHeight();
			if (this.displayWidth <= 0) {
				this.displayWidth = 1;
			}

			if (this.displayHeight <= 0) {
				this.displayHeight = 1;
			}
		} else {
			Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
		}

		Display.setTitle("Minecraft " + getVersionDescriptor(false));
		Display.setResizable(!Main.is_MITE_DS);
		this.getLogAgent().logInfo("LWJGL Version: " + Sys.getVersion());
		if (Util.getOSType() != EnumOS.MACOS) {
			try {
				Display.setIcon(new ByteBuffer[]{this.readImage(new File(this.fileAssets, "/icons/icon_16x16.png")), this.readImage(new File(this.fileAssets, "/icons/icon_32x32.png"))});
			} catch (IOException var5) {
				var5.printStackTrace();
			}
		}

		try {
			Display.create((new PixelFormat()).withDepthBits(24));
		} catch (LWJGLException var4) {
			var4.printStackTrace();

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException var3) {
			}

			if (this.fullscreen) {
				this.updateDisplayMode();
			}

			Display.create();
		}

		OpenGlHelper.initializeTextures();
		this.guiAchievement = new GuiAchievement(this);
		this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
		this.saveLoader = new AnvilSaveConverter(this.saves_dir_MITE);
		this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
		this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
		this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
		this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
		this.refreshResources();
		this.renderEngine = new TextureManager(this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.renderEngine);
		this.sndManager = new SoundManager(this.mcResourceManager, this.gameSettings, this.fileAssets);
		this.mcResourceManager.registerReloadListener(this.sndManager);
		this.loadScreen();
		this.fontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
		if (this.gameSettings.language != null) {
			this.fontRenderer.setUnicodeFlag(this.mcLanguageManager.isCurrentLocaleUnicode());
			this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
		}

		this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
		this.mcResourceManager.registerReloadListener(this.fontRenderer);
		this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
		this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
		this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
		RenderManager.instance.itemRenderer = new ItemRenderer(this);
		this.entityRenderer = new EntityRenderer(this);
		this.statFileWriter = new StatFileWriter(this.session, new File(this.mcDataDir, "MITE"));
		AchievementList.openInventory.setStatStringFormatter(new StatStringFormatKeyInv(this));
		this.mouseHelper = new MouseHelper();
		this.checkGLError("Pre startup");
		GL11.glEnable(3553);
		GL11.glShadeModel(7425);
		GL11.glClearDepth(1.0);
		GL11.glEnable(2929);
		GL11.glDepthFunc(515);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.1F);
		GL11.glCullFace(1029);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(5888);
		this.checkGLError("Startup");
		this.renderGlobal = new RenderGlobal(this);
		this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, new TextureMap(0, "textures/blocks"));
		this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items"));
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);
		if (this.serverName != null) {
			this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
		} else {
			this.displayGuiScreen(new GuiMainMenu());
		}

		this.loadingScreen = new LoadingScreenRenderer(this);
		if (this.gameSettings.isFullscreen() && !this.fullscreen) {
			this.toggleFullscreen();
		} else {
			Display.setVSyncEnabled(this.gameSettings.isVsyncEnabled());
		}

		ReferenceFileWriter.write();
	}

	public void refreshResources() {
		ArrayList var1 = Lists.newArrayList((Iterable)this.defaultResourcePacks);
		Iterator var2 = this.mcResourcePackRepository.getRepositoryEntries().iterator();

		while(var2.hasNext()) {
			ResourcePackRepositoryEntry var3 = (ResourcePackRepositoryEntry)var2.next();
			var1.add(var3.getResourcePack());
		}

		this.mcLanguageManager.parseLanguageMetadata(var1);
		this.mcResourceManager.reloadResources(var1);
		if (this.renderGlobal != null) {
			this.renderGlobal.loadRenderers();
		}

	}

	private void addDefaultResourcePack() {
		this.defaultResourcePacks.add(this.mcDefaultResourcePack);
	}

	private ByteBuffer readImage(File par1File) throws IOException {
		BufferedImage var2 = ImageIO.read(par1File);
		int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), (int[])null, 0, var2.getWidth());
		ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);
		int[] var5 = var3;
		int var6 = var3.length;

		for(int var7 = 0; var7 < var6; ++var7) {
			int var8 = var5[var7];
			var4.putInt(var8 << 8 | var8 >> 24 & 255);
		}

		var4.flip();
		return var4;
	}

	private void updateDisplayMode() throws LWJGLException {
		HashSet var1 = new HashSet();
		Collections.addAll(var1, Display.getAvailableDisplayModes());
		DisplayMode var2 = Display.getDesktopDisplayMode();
		if (!var1.contains(var2) && Util.getOSType() == EnumOS.MACOS) {
			Iterator var3 = macDisplayModes.iterator();

			label49:
			while(true) {
				while(true) {
					DisplayMode var4;
					boolean var5;
					Iterator var6;
					DisplayMode var7;
					do {
						if (!var3.hasNext()) {
							break label49;
						}

						var4 = (DisplayMode)var3.next();
						var5 = true;
						var6 = var1.iterator();

						while(var6.hasNext()) {
							var7 = (DisplayMode)var6.next();
							if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() && var7.getHeight() == var4.getHeight()) {
								var5 = false;
								break;
							}
						}
					} while(var5);

					var6 = var1.iterator();

					while(var6.hasNext()) {
						var7 = (DisplayMode)var6.next();
						if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() / 2 && var7.getHeight() == var4.getHeight() / 2) {
							var2 = var7;
							break;
						}
					}
				}
			}
		}

		Display.setDisplayMode(var2);
		this.displayWidth = var2.getWidth();
		this.displayHeight = var2.getHeight();
	}

	private void loadScreen() throws LWJGLException {
		ScaledResolution var1 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
		GL11.glClear(16640);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, var1.getScaledWidth_double(), var1.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		GL11.glDisable(2896);
		GL11.glEnable(3553);
		GL11.glDisable(2912);
		this.renderEngine.bindTexture(locationMojangPng);
		Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();
		var2.setColorOpaque_I(16777215);
		var2.addVertexWithUV(0.0, (double)this.displayHeight, 0.0, 0.0, 0.0);
		var2.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, 0.0, 0.0, 0.0);
		var2.addVertexWithUV((double)this.displayWidth, 0.0, 0.0, 0.0, 0.0);
		var2.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
		var2.draw();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		var2.setColorOpaque_I(16777215);
		short var3 = 256;
		short var4 = 256;
		this.scaledTessellator((var1.getScaledWidth() - var3) / 2, (var1.getScaledHeight() - var4) / 2, 0, 0, var3, var4);
		GL11.glDisable(2896);
		GL11.glDisable(2912);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.1F);
		Display.update();
	}

	public void scaledTessellator(int par1, int par2, int par3, int par4, int par5, int par6) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), 0.0, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
		var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), 0.0, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
		var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), 0.0, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
		var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), 0.0, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
		var9.draw();
	}

	public ISaveFormat getSaveLoader() {
		return this.saveLoader;
	}

	public void openChat(GuiChat gui_chat) {
		this.imposed_gui_chat = gui_chat;
		ScaledResolution var2 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
		int var3 = var2.getScaledWidth();
		int var4 = var2.getScaledHeight();
		gui_chat.setWorldAndResolution(this, var3, var4);
	}

	public void closeImposedChat() {
		if (this.imposed_gui_chat != null) {
			this.imposed_gui_chat.onGuiClosed();
			this.imposed_gui_chat = null;
		}
	}

	public void displayGuiScreen(GuiScreen par1GuiScreen) {
		if (this.thePlayer == null || !this.thePlayer.isGhost() || !(par1GuiScreen instanceof GuiInventory)) {
			if (par1GuiScreen != null) {
				this.right_click_counter = 0;
			}

			if (this.currentScreen != null) {
				this.currentScreen.onGuiClosed();
			}

			this.statFileWriter.syncStats();
			if (par1GuiScreen == null && this.theWorld == null) {
				par1GuiScreen = new GuiMainMenu();
			} else if (par1GuiScreen == null && this.thePlayer.getHealth() <= 0.0F) {
				par1GuiScreen = new GuiGameOver();
			}

			if (par1GuiScreen instanceof GuiMainMenu) {
				this.gameSettings.showDebugInfo = false;
				this.ingameGUI.getChatGUI().clearChatMessages();
			}

			this.currentScreen = (GuiScreen)par1GuiScreen;
			if (par1GuiScreen != null) {
				this.setIngameNotInFocus();
				ScaledResolution var2 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
				int var3 = var2.getScaledWidth();
				int var4 = var2.getScaledHeight();
				((GuiScreen)par1GuiScreen).setWorldAndResolution(this, var3, var4);
				this.skipRenderWorld = false;
				if (this.isChatImposed() && !((GuiScreen)par1GuiScreen).allowsImposedChat()) {
					this.closeImposedChat();
				}
			} else {
				this.setIngameFocus();
			}

		}
	}

	private void checkGLError(String par1Str) {
		int var2 = GL11.glGetError();
		if (var2 != 0) {
			String var3 = GLU.gluErrorString(var2);
			this.getLogAgent().logSevere("########## GL ERROR ##########");
			this.getLogAgent().logSevere("@ " + par1Str);
			this.getLogAgent().logSevere(var2 + ": " + var3);
		}

	}

	public void shutdownMinecraftApplet() {
		try {
			this.statFileWriter.syncStats();
			this.getLogAgent().logInfo("Stopping!");

			try {
				this.loadWorld((WorldClient)null);
			} catch (Throwable var7) {
			}

			try {
				GLAllocation.deleteTexturesAndDisplayLists();
			} catch (Throwable var6) {
			}

			this.sndManager.cleanup();
		} finally {
			Display.destroy();
			if (!this.hasCrashed) {
				ThreadedFileIOBase.reportErrorIfNotFinished();
				System.exit(0);
			}

		}

		System.gc();
	}

	public void run() {
		this.running = true;

		CrashReport var2;
		try {
			this.startGame();
		} catch (Throwable var11) {
			var2 = CrashReport.makeCrashReport(var11, "Initializing game");
			var2.makeCategory("Initialization");
			this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(var2));
			return;
		}

		try {
			while (this.running) {
				if (this.running) {
					if (!this.running) {
						continue;
					}

					if (!this.hasCrashed || this.crashReporter == null) {
						if (this.refreshTexturePacksScheduled) {
							this.refreshTexturePacksScheduled = false;
							this.refreshResources();
						}

						try {
							this.runGameLoop();
						} catch (OutOfMemoryError var10) {
							this.freeMemory();
							this.displayGuiScreen(new GuiMemoryErrorScreen());
							System.gc();
						}

						continue;
					}

					this.displayCrashReport(this.crashReporter);
					return;
				}
			}
		}
		catch(MinecraftError var12)
		{
			var12.printStackTrace();
		}
		catch(ReportedException var13)
		{
			this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
			this.freeMemory();
			var13.printStackTrace();
			this.displayCrashReport(var13.getCrashReport());
		}
		catch(Throwable var14)
		{
			var2 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var14));
			this.freeMemory();
			var14.printStackTrace();
			this.displayCrashReport(var2);
		}
		finally
		{
			this.shutdownMinecraftApplet();
		}

		return;
	}

	private void runGameLoop() {
		this.gameSettings.difficulty = 3;
		this.gameSettings.gammaSetting = 0.0F;
		AxisAlignedBB.getAABBPool().cleanPool();
		if (this.theWorld != null) {
			this.theWorld.getWorldVec3Pool().clear();
		}

		this.mcProfiler.startSection("root");
		if (Display.isCloseRequested()) {
			this.shutdown();
		}

		if (this.isGamePaused && this.theWorld != null) {
			float var1 = this.timer.renderPartialTicks;
			this.timer.updateTimer();
			this.timer.renderPartialTicks = var1;
		} else {
			this.timer.updateTimer();
		}

		long var6 = System.nanoTime();
		this.mcProfiler.startSection("tick");

		for(int var3 = 0; var3 < this.timer.elapsedTicks; ++var3) {
			this.runTick();
		}

		this.mcProfiler.endStartSection("preRenderErrors");
		long var7 = System.nanoTime() - var6;
		this.checkGLError("Pre render");
		RenderBlocks.fancyGrass = this.gameSettings.isFancyGraphicsEnabled();
		this.mcProfiler.endStartSection("sound");
		this.sndManager.setListener(this.thePlayer, this.timer.renderPartialTicks);
		if (!this.isGamePaused) {
			this.sndManager.func_92071_g();
		}

		this.mcProfiler.endSection();
		this.mcProfiler.startSection("render");
		this.mcProfiler.startSection("display");
		GL11.glEnable(3553);
		if (!Keyboard.isKeyDown(65)) {
			Display.update();
		}

		if (this.thePlayer != null && (this.thePlayer.isEntityInsideOpaqueBlock() || this.thePlayer.isLockedInFirstPersonView())) {
			this.gameSettings.thirdPersonView = 0;
		}

		this.mcProfiler.endSection();
		if (!this.skipRenderWorld) {
			this.mcProfiler.endStartSection("gameRenderer");
			this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
			this.mcProfiler.endSection();
		}

		GL11.glFlush();
		this.mcProfiler.endSection();
		if (!Display.isActive() && this.fullscreen) {
			this.toggleFullscreen();
		}

		if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
			if (!this.mcProfiler.profilingEnabled) {
				this.mcProfiler.clearProfiling();
			}

			this.mcProfiler.profilingEnabled = true;
			this.displayDebugInfo(var7);
		} else {
			this.mcProfiler.profilingEnabled = false;
			this.prevFrameTime = System.nanoTime();
		}

		this.guiAchievement.updateAchievementWindow();
		this.mcProfiler.startSection("root");
		Thread.yield();
		if (Keyboard.isKeyDown(65)) {
			Display.update();
		}

		this.screenshotListener();
		if (!this.fullscreen && Display.wasResized()) {
			this.displayWidth = Display.getWidth();
			this.displayHeight = Display.getHeight();
			if (this.displayWidth <= 0) {
				this.displayWidth = 1;
			}

			if (this.displayHeight <= 0) {
				this.displayHeight = 1;
			}

			this.resize(this.displayWidth, this.displayHeight);
		}

		this.checkGLError("Post render");
		++this.fpsCounter;
		boolean var5 = this.isGamePaused;
		this.isGamePaused = isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
		if (this.isIntegratedServerRunning() && this.thePlayer != null && this.thePlayer.sendQueue != null && this.isGamePaused != var5) {
			((MemoryConnection)this.thePlayer.sendQueue.getNetManager()).setGamePaused(this.isGamePaused);
		}

		while(getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
			WorldRenderer.chunksUpdated = 0;
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0;
			this.usageSnooper.addMemoryStatsToSnooper();
			if (!this.usageSnooper.isSnooperRunning()) {
				this.usageSnooper.startSnooper();
			}
		}

		this.mcProfiler.endSection();
		if (this.getLimitFramerate() > 0) {
			Display.sync(EntityRenderer.performanceToFps(this.getLimitFramerate()));
		}

	}

	public void downtimeProcessing(long milliseconds_of_downtime) {
		if (milliseconds_of_downtime > 0L && !this.thePlayer.isGhost() && !Main.no_downtime_processing) {
			long time_to_move_on = System.currentTimeMillis() + milliseconds_of_downtime;
			if (!this.gameSettings.anaglyph && !this.gameSettings.isVsyncEnabled()) {
				double[] viewer_pos = RenderGlobal.getViewerPos(this.timer.renderPartialTicks, 12.0F);
				double cloud_recompile_urgency = Math.max(Math.abs(this.renderGlobal.last_cloud_compile_x - viewer_pos[0]), Math.abs(this.renderGlobal.last_cloud_compile_z - viewer_pos[2]));
				if (cloud_recompile_urgency > 0.5 && (double)milliseconds_of_downtime > 8.0 - cloud_recompile_urgency * 4.0) {
					long t = System.nanoTime();
					this.renderGlobal.compileCloudsFancy(this.timer.renderPartialTicks, viewer_pos[0], viewer_pos[1], viewer_pos[2]);
				}
			}

			for(int i = 0; System.currentTimeMillis() < time_to_move_on && i < MITEConstant.maxRandomRaycastsPerTickForCorrectiveLightingUpdates(this.thePlayer.worldObj); ++i) {
				this.theWorld.checkLightingOfRandomBlockInView(false);
			}

		}
	}

	private int getLimitFramerate() {
		return this.currentScreen != null && this.currentScreen instanceof GuiMainMenu ? 2 : this.gameSettings.limitFramerate;
	}

	public void freeMemory() {
		try {
			memoryReserve = new byte[0];
			this.renderGlobal.deleteAllDisplayLists();
		} catch (Throwable var4) {
		}

		try {
			System.gc();
			AxisAlignedBB.getAABBPool().clearPool();
			this.theWorld.getWorldVec3Pool().clearAndFreeCache();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			this.loadWorld((WorldClient)null);
		} catch (Throwable var2) {
		}

		System.gc();
	}

	private void screenshotListener() {
		if (inDevMode() && GuiScreen.isCtrlKeyDown()) {
			this.screenshotListenerForForcedRendering();
		} else {
			if (!Keyboard.isKeyDown(60) && !this.take_screenshot_next_tick) {
				this.isTakingScreenshot = false;
			} else {
				if (!this.isTakingScreenshot) {
					this.isTakingScreenshot = true;
					this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight));
					this.sndManager.playSoundFX("imported.random.camera", 1.0F, 1.0F);
				}

				if (this.take_screenshot_next_tick) {
					this.take_screenshot_next_tick = false;
				}
			}

		}
	}

	private void takeScreenshot() {
		this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight));
		this.sndManager.playSoundFX("imported.random.camera", 1.0F, 1.0F);
	}

	private void screenshotListenerForForcedRendering() {
		boolean is_screenshot_key_down = Keyboard.isKeyDown(60);
		if (this.take_screenshot_next_tick) {
			this.takeScreenshot();
			this.take_screenshot_next_tick = false;
		} else if (is_screenshot_key_down && !this.isTakingScreenshot) {
			this.isTakingScreenshot = true;
			if (GuiScreen.isCtrlKeyDown()) {
				this.take_screenshot_next_tick = this.force_rendering_for_screenshot = true;
			} else {
				this.takeScreenshot();
			}
		}

		if (!is_screenshot_key_down) {
			this.isTakingScreenshot = false;
		}

	}

	private void updateDebugProfilerName(int par1) {
		List var2 = this.mcProfiler.getProfilingData(this.debugProfilerName);
		if (var2 != null && !var2.isEmpty()) {
			ProfilerResult var3 = (ProfilerResult)var2.remove(0);
			if (par1 == 0) {
				if (var3.field_76331_c.length() > 0) {
					int var4 = this.debugProfilerName.lastIndexOf(".");
					if (var4 >= 0) {
						this.debugProfilerName = this.debugProfilerName.substring(0, var4);
					}
				}
			} else {
				--par1;
				if (par1 < var2.size() && !((ProfilerResult)var2.get(par1)).field_76331_c.equals("unspecified")) {
					if (this.debugProfilerName.length() > 0) {
						this.debugProfilerName = this.debugProfilerName + ".";
					}

					this.debugProfilerName = this.debugProfilerName + ((ProfilerResult)var2.get(par1)).field_76331_c;
				}
			}
		}

	}

	private void displayDebugInfo(long par1) {
		if (this.mcProfiler.profilingEnabled) {
			List var3 = this.mcProfiler.getProfilingData(this.debugProfilerName);
			ProfilerResult var4 = (ProfilerResult)var3.remove(0);
			GL11.glClear(256);
			GL11.glMatrixMode(5889);
			GL11.glEnable(2903);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0, (double)this.displayWidth, (double)this.displayHeight, 0.0, 1000.0, 3000.0);
			GL11.glMatrixMode(5888);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
			GL11.glLineWidth(1.0F);
			GL11.glDisable(3553);
			Tessellator var5 = Tessellator.instance;
			short var6 = 160;
			int var7 = this.displayWidth - var6 - 10;
			int var8 = this.displayHeight - var6 * 2;
			GL11.glEnable(3042);
			var5.startDrawingQuads();
			var5.setColorRGBA_I(0, 200);
			var5.addVertex((double)((float)var7 - (float)var6 * 1.1F), (double)((float)var8 - (float)var6 * 0.6F - 16.0F), 0.0);
			var5.addVertex((double)((float)var7 - (float)var6 * 1.1F), (double)(var8 + var6 * 2), 0.0);
			var5.addVertex((double)((float)var7 + (float)var6 * 1.1F), (double)(var8 + var6 * 2), 0.0);
			var5.addVertex((double)((float)var7 + (float)var6 * 1.1F), (double)((float)var8 - (float)var6 * 0.6F - 16.0F), 0.0);
			var5.draw();
			GL11.glDisable(3042);
			double var9 = 0.0;

			int var13;
			int var21;
			for(int var11 = 0; var11 < var3.size(); ++var11) {
				ProfilerResult var12 = (ProfilerResult)var3.get(var11);
				var13 = MathHelper.floor_double(var12.field_76332_a / 4.0) + 1;
				var5.startDrawing(6);
				var5.setColorOpaque_I(var12.func_76329_a());
				var5.addVertex((double)var7, (double)var8, 0.0);

				float var15;
				float var17;
				float var16;
				for(var21 = var13; var21 >= 0; --var21) {
					var15 = (float)((var9 + var12.field_76332_a * (double)var21 / (double)var13) * Math.PI * 2.0 / 100.0);
					var16 = MathHelper.sin(var15) * (float)var6;
					var17 = MathHelper.cos(var15) * (float)var6 * 0.5F;
					var5.addVertex((double)((float)var7 + var16), (double)((float)var8 - var17), 0.0);
				}

				var5.draw();
				var5.startDrawing(5);
				var5.setColorOpaque_I((var12.func_76329_a() & 16711422) >> 1);

				for(var21 = var13; var21 >= 0; --var21) {
					var15 = (float)((var9 + var12.field_76332_a * (double)var21 / (double)var13) * Math.PI * 2.0 / 100.0);
					var16 = MathHelper.sin(var15) * (float)var6;
					var17 = MathHelper.cos(var15) * (float)var6 * 0.5F;
					var5.addVertex((double)((float)var7 + var16), (double)((float)var8 - var17), 0.0);
					var5.addVertex((double)((float)var7 + var16), (double)((float)var8 - var17 + 10.0F), 0.0);
				}

				var5.draw();
				var9 += var12.field_76332_a;
			}

			DecimalFormat var19 = new DecimalFormat("##0.00");
			GL11.glEnable(3553);
			String var18 = "";
			if (!var4.field_76331_c.equals("unspecified")) {
				var18 = var18 + "[0] ";
			}

			if (var4.field_76331_c.length() == 0) {
				var18 = var18 + "ROOT ";
			} else {
				var18 = var18 + var4.field_76331_c + " ";
			}

			var13 = 16777215;
			this.fontRenderer.drawStringWithShadow(var18, var7 - var6, var8 - var6 / 2 - 16, var13);
			this.fontRenderer.drawStringWithShadow(var18 = var19.format(var4.field_76330_b) + "%", var7 + var6 - this.fontRenderer.getStringWidth(var18), var8 - var6 / 2 - 16, var13);

			for(var21 = 0; var21 < var3.size(); ++var21) {
				ProfilerResult var20 = (ProfilerResult)var3.get(var21);
				String var22 = "";
				if (var20.field_76331_c.equals("unspecified")) {
					var22 = var22 + "[?] ";
				} else {
					var22 = var22 + "[" + (var21 + 1) + "] ";
				}

				var22 = var22 + var20.field_76331_c;
				this.fontRenderer.drawStringWithShadow(var22, var7 - var6, var8 + var6 / 2 + var21 * 8 + 20, var20.func_76329_a());
				this.fontRenderer.drawStringWithShadow(var22 = var19.format(var20.field_76332_a) + "%", var7 + var6 - 50 - this.fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var21 * 8 + 20, var20.func_76329_a());
				this.fontRenderer.drawStringWithShadow(var22 = var19.format(var20.field_76330_b) + "%", var7 + var6 - this.fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var21 * 8 + 20, var20.func_76329_a());
			}
		}

	}

	public void shutdown() {
		ThreadedFileIOBase.waitForFinish();
		this.running = false;
	}

	public void setIngameFocus() {
		if (Display.isActive() && !this.inGameHasFocus) {
			this.inGameHasFocus = true;
			this.mouseHelper.grabMouseCursor();
			this.displayGuiScreen((GuiScreen)null);
			this.closeImposedChat();
			this.leftClickCounter = 10000;
		}

	}

	public void setIngameNotInFocus() {
		if (this.inGameHasFocus) {
			KeyBinding.unPressAllKeys();
			this.inGameHasFocus = false;
			this.mouseHelper.ungrabMouseCursor();
		}

	}

	public void displayInGameMenu() {
		if (this.currentScreen == null) {
			this.displayGuiScreen(new GuiIngameMenu());
			if (isSingleplayer() && !this.theIntegratedServer.getPublic()) {
				this.sndManager.pauseAllSounds();
			}
		}

	}

	private void sendClickBlockToController(int par1, boolean par2) {
		if (!this.thePlayer.isGhost() && !this.thePlayer.isZevimrgvInTournament()) {
			if (this.thePlayer.fishEntity == null) {
				if (this.playerController.auto_use_mode_item == null) {
					if (par1 == 0 && par2) {
						this.playerController.last_auto_harvest_ms = System.currentTimeMillis();
					}

					if (!par2) {
						this.leftClickCounter = 0;
					}

					if (par1 != 0 || this.leftClickCounter <= 0) {
						if (par2 && this.objectMouseOver != null && this.objectMouseOver.isBlock() && par1 == 0) {
							int var3 = this.objectMouseOver.block_hit_x;
							int var4 = this.objectMouseOver.block_hit_y;
							int var5 = this.objectMouseOver.block_hit_z;
							boolean player_damaged_block = this.thePlayer.getDamageVsBlock(var3, var4, var5, true) > 0.0F;
							this.playerController.onPlayerDamageBlock(var3, var4, var5, this.objectMouseOver.face_hit);
							if (this.thePlayer.isCurrentToolAdventureModeExempt(var3, var4, var5)) {
								if (player_damaged_block) {
									float damage = this.thePlayer.getDamageVsBlock(var3, var4, var5, true);
									float block_hit_effect_progress_per_tick = damage * 80.0F;
									this.block_hit_effect_progress += block_hit_effect_progress_per_tick;
									if (this.block_hit_effect_progress >= 1.0F) {
										this.effectRenderer.addBlockHitEffects(var3, var4, var5, this.objectMouseOver.face_hit);
										this.thePlayer.sendPacketToAssociatedPlayers((new Packet85SimpleSignal(EnumSignal.block_hit_fx)).setBlockCoords(var3, var4, var5).setByte(this.objectMouseOver.face_hit.ordinal()), false);
										if (block_hit_effect_progress_per_tick < 1.0F) {
											--this.block_hit_effect_progress;
										} else {
											this.block_hit_effect_progress = 0.0F;
										}
									}
								}

								this.thePlayer.swingArm();
							}
						} else if (this.playerController.isHittingBlock || this.objectMouseOver == null || !this.playerController.sameToolAndBlock(this.objectMouseOver.block_hit_x, this.objectMouseOver.block_hit_y, this.objectMouseOver.block_hit_z)) {
							this.playerController.resetBlockRemoving();
						}
					}

				}
			}
		}
	}

	private boolean tryClickEntity(int button) {
		boolean done = false;
		if (button == 0) {
			if (!this.thePlayer.canReachEntity(this.objectMouseOver, EnumEntityReachContext.FOR_MELEE_ATTACK)) {
				return false;
			}

			if (this.objectMouseOver.getEntityHit().canBeAttackedBy(this.thePlayer)) {
				this.playerController.leftClickEntity(this.objectMouseOver.getEntityHit());
			} else {
				this.playerController.cancel_swing = true;
			}

			done = true;
		} else if (button == 1) {
			setErrorMessage("tryClickEntity: should no longer be called for right clicks");
		}

		return done;
	}

	private boolean blockClicked(int button) {
		boolean done = false;
		ItemStack held_item_stack = this.thePlayer.inventory.getCurrentItemStack();
		int held_item_slot_index = this.thePlayer.inventory.currentItem;
		int x = this.objectMouseOver.block_hit_x;
		int y = this.objectMouseOver.block_hit_y;
		int z = this.objectMouseOver.block_hit_z;
		EnumFace face = this.objectMouseOver.face_hit;
		if (button == 0) {
			this.playerController.clickBlock(x, y, z, face);
			done = true;
		} else if (button == 1) {
			setErrorMessage("blockClicked: no longer used for right clicks");
		}

		return done;
	}

	public boolean tryAutoSwitchOrRestock() {
		ItemStack hotbar_selection = this.thePlayer.inventory.getCurrentItemStack();
		if ((hotbar_selection == null || hotbar_selection.stackSize == 0) && this.playerController.last_used_item != null && this.playerController.autoStockEnabled()) {
			this.playerController.item_switch_or_restock_pending = true;
			return true;
		} else {
			return false;
		}
	}

	private void clickMouse(int button) {
		if (disable_clicks_until < System.currentTimeMillis()) {
			if (inDevMode() && button == 0 && GuiScreen.isCtrlKeyDown()) {
				RaycastCollision rc = this.thePlayer.getSelectedObject(this.timer.renderPartialTicks, false, true, (EnumEntityReachContext)null);
				if (rc != null && rc.isEntity() && rc.getEntityHit().isEntityLivingBase()) {
					if (Keyboard.isKeyDown(157)) {
						this.getNetHandler().handleCreateFile(EntityStatsDump.generatePacketFor(rc.getEntityHit().getAsEntityLivingBase()));
					} else {
						this.thePlayer.sendPacket((new Packet85SimpleSignal(EnumSignal.entity_stats_dump)).setEntityID(rc.getEntityHit()));
					}
				}

			} else if (!this.thePlayer.isGhost() && !this.thePlayer.isZevimrgvInTournament()) {
				if (button == 0) {
					if (this.leftClickCounter > 0) {
						return;
					}

					if (this.thePlayer.fishEntity != null) {
						return;
					}

					if (this.playerController.listening_for_auto_use_mode_click && this.thePlayer.getHeldItemStack() != null && this.playerController.isItemStackEligibleForAUM(this.thePlayer.getHeldItemStack())) {
						this.leftClickCounter = 5;
						return;
					}
				} else if (button == 1) {
					if (this.right_click_counter > 0) {
						return;
					}

					if (this.gameSettings.keyBindAttack.pressed) {
						return;
					}

					if (this.playerController.curBlockDamageMP > 0.0F) {
						if (this.tryAutoSwitchOrRestock()) {
							this.playerController.setUseButtonDelay();
						}

						return;
					}

					this.playerController.clearAutoHarvestMode();
					if (!this.playerController.useButtonEnabled()) {
						return;
					}
				}

				ItemStack hotbar_selection = this.thePlayer.inventory.getCurrentItemStack();
				int hotbar_selection_index = this.thePlayer.inventory.currentItem;
				if (button == 0) {
					boolean done = false;
					if (this.objectMouseOver == null) {
						if (button == 0 && this.playerController.isNotCreative()) {
							this.leftClickCounter = 10;
						}
					} else if (this.objectMouseOver.isEntity()) {
						this.tryClickEntity(button);
					} else if (this.objectMouseOver.isBlock()) {
						this.blockClicked(button);
					}

					if (button == 0 && !this.playerController.cancel_swing) {
						this.thePlayer.swingArm();
					} else {
						this.playerController.cancel_swing = false;
					}

					if (this.thePlayer.inventory.currentItem != hotbar_selection_index) {
						return;
					}
				} else {
					RightClickFilter filter = new RightClickFilter();
					boolean click_caused_by_auto_use_mode = !this.gameSettings.keyBindUseItem.pressed && this.playerController.inAutoUseMode();
					if (click_caused_by_auto_use_mode) {
						filter.setExclusive(4);
					}

					filter = this.thePlayer.onPlayerRightClickChecked(this.objectMouseOver, filter, this.timer.renderPartialTicks, GuiScreen.isCtrlKeyDown());
					if (this.thePlayer.rightClickCancelled()) {
						this.thePlayer.clearRightClickCancelled();
					} else if (!filter.allowsNoActions()) {
						if (this.thePlayer.isBlocking()) {
							this.playerController.setUseButtonDelayOverride(10);
						} else {
							this.playerController.setUseButtonDelay();
						}

						if (filter.allowsEntityInteraction()) {
							this.thePlayer.sendPacket(new Packet81RightClick(this.thePlayer, this.objectMouseOver.getEntityHit()));
						} else {
							this.thePlayer.sendPacket(new Packet81RightClick(this.thePlayer, this.timer.renderPartialTicks, filter));
						}
					} else if (!this.thePlayer.hasHeldItem() && this.tryAutoSwitchOrRestock()) {
						this.playerController.setUseButtonDelay();
					}
				}

			}
		}
	}

	public void toggleFullscreen() {
		if (Main.is_MITE_DS) {
			this.fullscreen = false;
		} else {
			try {
				this.fullscreen = !this.fullscreen;
				if (this.fullscreen) {
					this.updateDisplayMode();
					this.displayWidth = Display.getDisplayMode().getWidth();
					this.displayHeight = Display.getDisplayMode().getHeight();
					if (this.displayWidth <= 0) {
						this.displayWidth = 1;
					}

					if (this.displayHeight <= 0) {
						this.displayHeight = 1;
					}
				} else {
					Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
					this.displayWidth = this.tempDisplayWidth;
					this.displayHeight = this.tempDisplayHeight;
					if (this.displayWidth <= 0) {
						this.displayWidth = 1;
					}

					if (this.displayHeight <= 0) {
						this.displayHeight = 1;
					}
				}

				if (this.isGuiOpen()) {
					this.resize(this.displayWidth, this.displayHeight);
				}

				Display.setFullscreen(this.fullscreen);
				Display.setVSyncEnabled(this.gameSettings.isVsyncEnabled());
				Display.update();
			} catch (Exception var2) {
				var2.printStackTrace();
			}

		}
	}

	public boolean isGuiOpen(boolean that_handles_mouse_input) {
		if (that_handles_mouse_input) {
			return this.currentScreen != null && this.currentScreen.handlesMouseInput();
		} else {
			return this.currentScreen != null || this.isChatImposed();
		}
	}

	public boolean isGuiOpen() {
		return this.isGuiOpen(false);
	}

	public boolean isChatImposed() {
		return this.imposed_gui_chat != null;
	}

	public boolean isAnyChatOpen() {
		return this.isChatImposed() || this.currentScreen instanceof GuiChat;
	}

	public GuiChat getOpenChatGui() {
		if (this.isChatImposed()) {
			return this.imposed_gui_chat;
		} else {
			return this.currentScreen instanceof GuiChat ? (GuiChat)this.currentScreen : null;
		}
	}

	private void resize(int par1, int par2) {
		this.displayWidth = par1 <= 0 ? 1 : par1;
		this.displayHeight = par2 <= 0 ? 1 : par2;
		if (this.isGuiOpen()) {
			ScaledResolution var3 = new ScaledResolution(this.gameSettings, par1, par2);
			int var4 = var3.getScaledWidth();
			int var5 = var3.getScaledHeight();
			if (this.currentScreen != null) {
				this.currentScreen.setWorldAndResolution(this, var4, var5);
			}

			if (this.imposed_gui_chat != null) {
				this.imposed_gui_chat.setWorldAndResolution(this, var4, var5);
			}
		}

	}

	public void sendInputToGui() {
		if (this.isChatImposed()) {
			this.imposed_gui_chat.handleInput();
		} else if (this.currentScreen != null) {
			this.currentScreen.handleInput();
		}

	}

	public void runTick() {
		if (this.thePlayer != null && this.theWorld != null && this.currentScreen instanceof GuiGameOver && !this.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
			this.thePlayer.sendPacket((new Packet85SimpleSignal(EnumSignal.respawn_screen)).setShort(0));
		}

		boolean game_is_paused_or_player_is_sleeping = this.isGamePaused || this.thePlayer != null && this.thePlayer.isSleeping();
		this.mcProfiler.startSection("stats");
		this.statFileWriter.func_77449_e();
		this.mcProfiler.endStartSection("gui");
		if (!game_is_paused_or_player_is_sleeping) {
			this.ingameGUI.updateTick();
		}

		this.mcProfiler.endStartSection("pick");
		this.entityRenderer.getMouseOver(1.0F);
		this.mcProfiler.endStartSection("gameMode");
		if (!game_is_paused_or_player_is_sleeping && this.theWorld != null) {
			this.playerController.updateController();
		}

		this.mcProfiler.endStartSection("textures");
		if (!game_is_paused_or_player_is_sleeping) {
			this.renderEngine.tick();
		}

		if (this.currentScreen == null && this.thePlayer != null) {
			if (this.thePlayer.getHealth() <= 0.0F) {
				this.displayGuiScreen((GuiScreen)null);
			} else if (this.thePlayer.inBed() && this.theWorld != null) {
				this.displayGuiScreen(new GuiSleepMP());
			}
		} else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.inBed()) {
			this.displayGuiScreen((GuiScreen)null);
		}

		if (this.isGuiOpen(true)) {
			this.leftClickCounter = 10000;
		}

		CrashReport var2;
		CrashReportCategory var3;
		if (this.isGuiOpen()) {
			try {
				this.sendInputToGui();
			} catch (Throwable var10) {
				var2 = CrashReport.makeCrashReport(var10, "Updating screen events");
				var3 = var2.makeCategory("Affected screen");
				var3.addCrashSectionCallable("Screen name", new CallableUpdatingScreenName(this));
				throw new ReportedException(var2);
			}

			if (this.isGuiOpen()) {
				try {
					if (this.currentScreen != null) {
						this.currentScreen.updateScreen();
					}

					if (this.isChatImposed()) {
						this.imposed_gui_chat.updateScreen();
					}
				} catch (Throwable var9) {
					var2 = CrashReport.makeCrashReport(var9, "Ticking screen");
					var3 = var2.makeCategory("Affected screen");
					var3.addCrashSectionCallable("Screen name", new CallableParticleScreenName(this));
					throw new ReportedException(var2);
				}
			}
		}

		if (Main.is_MITE_DS) {
			if (this.currentScreen == null) {
				this.displayGuiScreen(new GuiMITEDS());
			}

			if (this.getIntegratedServer() != null && !this.getIntegratedServer().getPublic()) {
				GuiShareToLan.shareToLAN();
			}
		}

		boolean initializing_imposed_chat = false;
		if (this.thePlayer != null && this.gameSettings.chatVisibility != 2 && !this.isChatImposed() && (this.currentScreen == null || this.currentScreen.allowsImposedChat()) && !GuiScreen.isCtrlKeyDown()) {
			if (Keyboard.isKeyDown(this.gameSettings.keyBindChat.keyCode)) {
				while(true) {
					if (!this.gameSettings.keyBindChat.isPressed()) {
						this.openChat(new GuiChat(last_aborted_chat));
						initializing_imposed_chat = true;
						break;
					}
				}
			}

			if (Keyboard.isKeyDown(this.gameSettings.keyBindCommand.keyCode)) {
				while(true) {
					if (!this.gameSettings.keyBindCommand.isPressed()) {
						this.openChat(new GuiChat("/"));
						initializing_imposed_chat = true;
						break;
					}
				}
			}
		}

		if (initializing_imposed_chat) {
			KeyBinding var10000 = this.gameSettings.keyBindUseItem;
			KeyBinding.unPressAllKeys();
		}

		if (this.thePlayer != null && this.playerController.inAutoUseMode()) {
			ItemStack current_item_stack = this.thePlayer.inventory.getCurrentItemStack();
			if (current_item_stack == null) {
				if (this.isGuiOpen(true)) {
					this.playerController.clearAutoUseMode();
				}
			} else if (current_item_stack.getItem() != this.playerController.auto_use_mode_item) {
				this.playerController.clearAutoUseMode();
			}
		}

		if (this.currentScreen == null || this.currentScreen.allowUserInput || this.isChatImposed()) {
			this.mcProfiler.endStartSection("mouse");

			int var1;
			while(Mouse.next()) {
				var1 = Mouse.getEventButton();
				if (isRunningOnMac && var1 == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
					var1 = 1;
				}

				KeyBinding.setKeyBindState(var1 - 100, Mouse.getEventButtonState());
				if (Mouse.getEventButtonState()) {
					KeyBinding.onTick(var1 - 100);
				}

				long var9 = getSystemTime() - this.systemTime;
				if (var9 <= 200L) {
					int var4 = Mouse.getEventDWheel();
					if (var4 != 0) {
						this.thePlayer.inventory.changeCurrentItem(var4);
						if (this.gameSettings.noclip) {
							if (var4 > 0) {
								var4 = 1;
							}

							if (var4 < 0) {
								var4 = -1;
							}

							GameSettings var18 = this.gameSettings;
							var18.noclipRate += (float)var4 * 0.25F;
						}
					}

					if (!this.isGuiOpen()) {
						if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
							this.setIngameFocus();
						}
					} else if (this.currentScreen != null) {
						this.currentScreen.handleMouseInput();
					}
				}
			}

			if (this.leftClickCounter > 0) {
				--this.leftClickCounter;
			}

			if (this.right_click_counter > 0) {
				--this.right_click_counter;
			}

			this.mcProfiler.endStartSection("keyboard");

			label752:
			while(true) {
				while(true) {
					boolean var8;
					do {
						if (!Keyboard.next()) {
							var8 = this.gameSettings.chatVisibility != 2;

							while(this.gameSettings.keyBindInventory.isPressed()) {
								if (this.playerController.func_110738_j()) {
									this.thePlayer.func_110322_i();
								} else {
									this.displayGuiScreen(new GuiInventory(this.thePlayer));
								}
							}

							while(this.gameSettings.keyBindDrop.isPressed()) {
								this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
							}

							if (this.playerController.curBlockDamageMP > 0.0F && this.playerController.inAutoUseMode()) {
								this.playerController.resetBlockRemoving();
							}

							if (this.thePlayer.isUsingItem()) {
								if (this.isGuiOpen(true) || !this.gameSettings.keyBindUseItem.pressed && !this.playerController.inAutoUseMode()) {
									this.thePlayer.stopUsingItem();
								}

								while(this.gameSettings.keyBindAttack.isPressed()) {
								}

								while(true) {
									if (!this.gameSettings.keyBindUseItem.isPressed()) {
										while(this.gameSettings.keyBindPickBlock.isPressed()) {
										}
										break;
									}
								}
							} else {
								while(this.gameSettings.keyBindAttack.isPressed()) {
									this.clickMouse(0);
								}

								while(this.gameSettings.keyBindUseItem.isPressed()) {
									this.clickMouse(1);
								}

								while(this.gameSettings.keyBindPickBlock.isPressed()) {
									this.clickMiddleMouseButton();
								}
							}

							ItemStack current_item_stack = this.thePlayer.inventory.getCurrentItemStack();
							if (!this.isGuiOpen(true) && (this.gameSettings.keyBindUseItem.pressed || this.playerController.inAutoUseMode()) && !this.thePlayer.isUsingItem()) {
								this.clickMouse(1);
							}

							if (this.thePlayer.swing_item_pending) {
								this.thePlayer.swingArm(true);
							}

							if (current_item_stack != null && current_item_stack.stackSize > 0 && current_item_stack.getItem() == this.playerController.last_used_item) {
								this.playerController.item_switch_or_restock_pending = false;
							}

							if (this.playerController.item_switch_or_restock_pending && !this.thePlayer.inventory.trySwitchItemOrRestock(this.playerController.last_used_item, this.playerController.last_used_item_subtype, true) && this.playerController.autoStockEnabled()) {
								this.playerController.clearAutoUseMode();
							}

							if (this.playerController.auto_harvest_block != null && this.playerController.autoHarvestModeHasExpired()) {
								this.playerController.clearAutoHarvestMode();
							}

							if (!this.thePlayer.isUsingItem() && (this.gameSettings.keyBindAttack.pressed || !this.gameSettings.keyBindUseItem.pressed)) {
								this.sendClickBlockToController(0, !this.isGuiOpen(true) && (this.gameSettings.keyBindAttack.pressed || this.objectMouseOver != null && this.objectMouseOver.isBlock() && this.playerController.matchesAutoHarvestBlock(this.objectMouseOver.block_hit_x, this.objectMouseOver.block_hit_y, this.objectMouseOver.block_hit_z)) && this.inGameHasFocus);
							}

							if (this.playerController.listening_for_auto_harvest_mode_click && this.playerController.listening_for_auto_use_mode_click) {
								setErrorMessage("Listening for both AHM and AUM clicks");
							}

							if (this.gameSettings.keyBindAttack.pressed && this.gameSettings.keyBindUseItem.pressed) {
								if (this.playerController.listening_for_auto_harvest_mode_click && this.objectMouseOver != null && this.objectMouseOver.isBlock()) {
									this.playerController.setAutoHarvestMode(this.objectMouseOver.block_hit_x, this.objectMouseOver.block_hit_y, this.objectMouseOver.block_hit_z);
								}

								if (this.playerController.listening_for_auto_use_mode_click && current_item_stack != null) {
									this.playerController.setAutoUseMode(current_item_stack);
								}
							} else if (this.gameSettings.keyBindAttack.pressed) {
								if (this.playerController.cancel_auto_harvest_on_next_click) {
									this.playerController.clearAutoHarvestMode();
								} else {
									this.playerController.setListeningForAutoHarvestMode();
								}

								if (this.playerController.cancel_auto_use_mode_on_next_click) {
									this.playerController.clearAutoUseMode();
								}
							} else if (this.gameSettings.keyBindUseItem.pressed) {
								if (current_item_stack != null && (!(current_item_stack.getItem() instanceof ItemBlock) || current_item_stack.getItemInUseAction(this.thePlayer) != null)) {
									this.playerController.setListeningForAutoUseMode();
								}
							} else if (this.playerController.inAutoUseMode()) {
								this.playerController.cancel_auto_use_mode_on_next_click = true;
							}

							if (!this.gameSettings.keyBindAttack.pressed) {
								this.leftClickCounter = 0;
								this.playerController.cancel_auto_harvest_on_next_click = true;
							}

							if (!this.gameSettings.keyBindUseItem.pressed) {
								this.playerController.listening_for_auto_use_mode_click = false;
							}
							break label752;
						}

						KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
						if (Keyboard.getEventKeyState()) {
							KeyBinding.onTick(Keyboard.getEventKey());
						}

						if (this.field_83002_am > 0L) {
							if (getSystemTime() - this.field_83002_am >= 6000L) {
								throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
							}

							if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
								this.field_83002_am = -1L;
							}
						} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
							this.field_83002_am = getSystemTime();
						}
					} while(!Keyboard.getEventKeyState());

					if (Main.is_MITE_DS) {
						if (Keyboard.getEventKey() == 1) {
							this.displayInGameMenu();
						}
					} else if (Keyboard.getEventKey() == 87) {
						this.toggleFullscreen();
					} else {
						if (this.isChatImposed()) {
							if (!initializing_imposed_chat) {
								this.imposed_gui_chat.handleKeyboardInput();
							}
						} else if (this.currentScreen != null) {
							this.currentScreen.handleKeyboardInput();
						} else {
							if (Keyboard.getEventKey() == 1) {
								this.displayInGameMenu();
							}

							if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
								this.refreshResources();
							}

							if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
								this.refreshResources();
							}

							if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
								var8 = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
								this.gameSettings.setOptionValue(EnumOptions.RENDER_DISTANCE, var8 ? -1 : 1);
							}

							if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
								this.renderGlobal.loadRenderers();
							}

							if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
								this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
								this.gameSettings.saveOptions();
							}

							if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
								RenderManager.field_85095_o = !RenderManager.field_85095_o;
							}

							if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
								this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
								this.gameSettings.saveOptions();
							}

							if (Keyboard.getEventKey() == 59 && ++this.gameSettings.gui_mode > 2) {
								this.gameSettings.gui_mode = 0;
							}

							if (Keyboard.getEventKey() == 61) {
								if (!inDevMode()) {
									this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
									this.gameSettings.showDebugProfilerChart = false;
								}

								if (inDevMode() && Keyboard.isKeyDown(157)) {
									this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
									Debug.is_active = !Debug.is_active;
									if (Debug.is_active) {
										this.gameSettings.showDebugInfo = true;
									} else if (this.thePlayer.capabilities.isCreativeMode) {
										this.thePlayer.sendChatMessage("/gamemode survival", true);
									}
								}
							}

							if (this.gameSettings.keyBindRedrawChunks.isPressed()) {
								this.renderGlobal.markAllRenderersUninitialized();
							}

							if (this.gameSettings.keyBindZoom.isPressed()) {
								this.thePlayer.zoomed = !this.thePlayer.zoomed;
							}

							if (this.gameSettings.keyBindToggleRun.isPressed()) {
								this.playerController.toggleRun(this.thePlayer);
							}

							if (Keyboard.isKeyDown(46)) {
								clearErrorMessage();
							}

							if (inDevMode()) {
								if (Keyboard.isKeyDown(19)) {
									Debug.general_counter = 0;
								}

								if (Keyboard.isKeyDown(20) && GuiScreen.isShiftKeyDown()) {
									this.playerController.sendPacket(new Packet85SimpleSignal(EnumSignal.terraform));
								}

								if (Keyboard.isKeyDown(20) && GuiScreen.isCtrlKeyDown()) {
									RaycastCollision rc = this.thePlayer.getSelectedObject(this.timer.renderPartialTicks, false, true, (EnumEntityReachContext)null);
									if (rc != null && rc.isEntity()) {
										Entity entity = rc.getEntityHit();
										entity.tagged = !entity.tagged;
										this.thePlayer.sendPacket((new Packet85SimpleSignal(EnumSignal.tag_entity)).setBoolean(entity.tagged).setEntityID(entity));
									}
								}

								if (Keyboard.isKeyDown(12)) {
									int delta_ticks = GuiScreen.isCtrlKeyDown() ? -this.theWorld.getTimeOfDay() : -1000;
									this.playerController.sendPacket((new Packet85SimpleSignal(EnumSignal.change_world_time)).setBoolean(false).setInteger(delta_ticks));
								}

								if (Keyboard.isKeyDown(78)) {
									int delta_ticks = 1000;
									this.playerController.sendPacket((new Packet85SimpleSignal(EnumSignal.change_world_time)).setBoolean(GuiScreen.isCtrlKeyDown()).setInteger(delta_ticks));
								}

								if (Keyboard.isKeyDown(33)) {
									if (GuiScreen.isCtrlKeyDown()) {
										EntityRenderer.disable_fog = !EntityRenderer.disable_fog;
									} else {
										Debug.flag = !Debug.flag;
									}
								}

								if (Debug.is_active && Keyboard.getEventKey() == 46 && Keyboard.isKeyDown(157)) {
									if (this.thePlayer.capabilities.isCreativeMode) {
										this.thePlayer.sendChatMessage("/gamemode survival", true);
									} else {
										this.thePlayer.sendChatMessage("/gamemode creative", true);
									}
								}

								if (Keyboard.isKeyDown(52)) {
									see_through_block_tops = !see_through_block_tops;
									this.renderGlobal.loadRenderers();
								}

								if (Keyboard.isKeyDown(211)) {
									this.thePlayer.sendPacket(new Packet85SimpleSignal(EnumSignal.delete_selection));
								}

								if (Keyboard.isKeyDown(49)) {
									night_vision_override = !night_vision_override;
								}

								if (Keyboard.isKeyDown(34)) {
									System.gc();
								}
							}

							if (Keyboard.getEventKey() == 63 && !this.thePlayer.isLockedInFirstPersonView()) {
								++this.gameSettings.thirdPersonView;
								if (this.gameSettings.thirdPersonView > 2) {
									this.gameSettings.thirdPersonView = 0;
								}
							}

							if (Keyboard.getEventKey() == 66) {
								this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
							}
						}

						for(var1 = 0; var1 < 9; ++var1) {
							if (Keyboard.getEventKey() == 2 + var1) {
								this.thePlayer.inventory.currentItem = var1;
							}
						}

						if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
							if (Keyboard.getEventKey() == 11) {
								this.updateDebugProfilerName(0);
							}

							for(var1 = 0; var1 < 9; ++var1) {
								if (Keyboard.getEventKey() == 2 + var1) {
									this.updateDebugProfilerName(var1 + 1);
								}
							}
						}
					}
				}
			}
		}

		if (this.theWorld != null) {
			if (this.thePlayer != null) {
				++this.joinPlayerCounter;
				if (this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.theWorld.joinEntityInSurroundings(this.thePlayer);
				}
			}

			this.mcProfiler.endStartSection("gameRenderer");
			if (!game_is_paused_or_player_is_sleeping) {
				this.entityRenderer.updateRenderer();
			}

			this.mcProfiler.endStartSection("levelRenderer");
			if (!game_is_paused_or_player_is_sleeping) {
				this.renderGlobal.updateClouds();
			}

			this.mcProfiler.endStartSection("level");
			if (!game_is_paused_or_player_is_sleeping) {
				if (this.theWorld.lastLightningBolt > 0) {
					--this.theWorld.lastLightningBolt;
				}

				this.theWorld.updateEntities();
			}

			if (!this.isGamePaused) {
				this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting > 0, true);

				try {
					this.theWorld.tick();
				} catch (Throwable var11) {
					var2 = CrashReport.makeCrashReport(var11, "Exception in world tick");
					if (this.theWorld == null) {
						var3 = var2.makeCategory("Affected level");
						var3.addCrashSection("Problem", "Level is null!");
					} else {
						this.theWorld.addWorldInfoToCrashReport(var2);
					}

					throw new ReportedException(var2);
				}
			}

			this.mcProfiler.endStartSection("animateTick");
			if (!game_is_paused_or_player_is_sleeping && this.theWorld != null) {
				this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
			}

			this.mcProfiler.endStartSection("particles");
			if (!game_is_paused_or_player_is_sleeping) {
				this.effectRenderer.updateEffects();
			}
		} else if (this.myNetworkManager != null) {
			this.mcProfiler.endStartSection("pendingConnection");
			this.myNetworkManager.processReadPackets();
		}

		this.mcProfiler.endSection();
		this.systemTime = getSystemTime();
	}

	public void launchIntegratedServer(String par1Str, String par2Str, WorldSettings par3WorldSettings) {
		this.loadWorld((WorldClient)null);
		System.gc();
		ISaveHandler var4 = this.saveLoader.getSaveLoader(par1Str, false);
		WorldInfo var5 = var4.loadWorldInfo();
		if (var5 == null && par3WorldSettings != null) {
			var5 = new WorldInfo(par3WorldSettings, par1Str);
			var4.saveWorldInfo(var5);
		}

		if (par3WorldSettings == null) {
			par3WorldSettings = new WorldSettings(var5);
		}

		this.statFileWriter.readStat(StatList.startGameStat, 1);
		this.theIntegratedServer = new IntegratedServer(this, par1Str, par2Str, par3WorldSettings);
		this.theIntegratedServer.startServerThread();
		this.integratedServerIsRunning = true;
		this.loadingScreen.displayProgressMessage(I18n.getString("menu.loadingLevel"));

		while(!this.theIntegratedServer.serverIsInRunLoop()) {
			String var6 = this.theIntegratedServer.getUserMessage();
			if (var6 != null) {
				this.loadingScreen.resetProgresAndWorkingMessage(I18n.getString(var6));
			} else {
				this.loadingScreen.resetProgresAndWorkingMessage("");
			}

			try {
				Thread.sleep(200L);
			} catch (InterruptedException var9) {
			}
		}

		this.displayGuiScreen((GuiScreen)null);
		this.closeImposedChat();

		try {
			NetClientHandler var10 = new NetClientHandler(this, this.theIntegratedServer);
			this.myNetworkManager = var10.getNetManager();
		} catch (IOException var8) {
			this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(new CrashReport("Connecting to integrated server", var8)));
		}

		this.increment_startGameStat_asap = true;
	}

	public void loadWorld(WorldClient par1WorldClient) {
		this.loadWorld(par1WorldClient, "");
	}

	public void loadWorld(WorldClient par1WorldClient, String par2Str) {
		this.statFileWriter.syncStats();
		if (par1WorldClient == null) {
			NetClientHandler var3 = this.getNetHandler();
			if (var3 != null) {
				var3.cleanup();
			}

			if (this.myNetworkManager != null) {
				this.myNetworkManager.closeConnections();
			}

			if (this.theIntegratedServer != null) {
				this.theIntegratedServer.initiateShutdown();
			}

			this.theIntegratedServer = null;
		}

		this.renderViewEntity = null;
		this.myNetworkManager = null;
		if (this.loadingScreen != null) {
			this.loadingScreen.resetProgressAndMessage(par2Str);
			this.loadingScreen.resetProgresAndWorkingMessage("");
		}

		if (par1WorldClient == null && this.theWorld != null) {
			this.setServerData((ServerData)null);
			this.integratedServerIsRunning = false;
		}

		this.sndManager.playStreaming((String)null, 0.0F, 0.0F, 0.0F);
		this.sndManager.stopAllSounds();
		this.theWorld = par1WorldClient;
		if (par1WorldClient != null) {
			if (this.renderGlobal != null) {
				this.renderGlobal.setWorldAndLoadRenderers(par1WorldClient);
			}

			if (this.effectRenderer != null) {
				this.effectRenderer.clearEffects(par1WorldClient);
			}

			if (this.thePlayer == null) {
				this.thePlayer = this.playerController.func_78754_a(par1WorldClient);
				this.playerController.flipPlayer(this.thePlayer);
			}

			this.thePlayer.preparePlayerToSpawn();
			par1WorldClient.spawnEntityInWorld(this.thePlayer);
			this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
			this.playerController.setPlayerCapabilities(this.thePlayer);
			this.renderViewEntity = this.thePlayer;
		} else {
			this.saveLoader.flushCache();
			this.thePlayer = null;
		}

		System.gc();
		this.systemTime = 0L;
	}

	private static String getSecretErrorMessage() {
		String[] message = new String[]{"!", "d", "e", "r", "e", "t", "n", "e", " ", "e", "b", " ", "t", "o", "n", " ", "d", "l", "u", "o", "c", " ", "e", "d", "o", "m", " ", "v", "e", "D"};
		StringBuffer sb = new StringBuffer();

		for(int i = message.length - 1; i >= 0; --i) {
			sb.append(message[i]);
		}

		return sb.toString();
	}

	public String debugInfoRenders() {
		return this.renderGlobal.getDebugInfoRenders();
	}

	public String getEntityDebug() {
		return this.renderGlobal.getDebugInfoEntities();
	}

	public String getWorldProviderName() {
		return this.theWorld.getProviderName();
	}

	public String debugInfoEntities() {
		return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
	}

	public void setDimensionAndSpawnPlayer(int par1) {
		this.theWorld.setSpawnLocation();
		this.theWorld.removeAllEntities();
		int var2 = 0;
		String var3 = null;
		if (this.thePlayer != null) {
			var2 = this.thePlayer.entityId;
			this.theWorld.removeEntity(this.thePlayer);
			var3 = this.thePlayer.func_142021_k();
		}

		this.renderViewEntity = null;
		this.thePlayer = this.playerController.func_78754_a(this.theWorld);
		this.thePlayer.dimension = par1;
		this.renderViewEntity = this.thePlayer;
		this.thePlayer.preparePlayerToSpawn();
		this.thePlayer.func_142020_c(var3);
		this.theWorld.spawnEntityInWorld(this.thePlayer);
		this.playerController.flipPlayer(this.thePlayer);
		this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
		this.thePlayer.entityId = var2;
		this.playerController.setPlayerCapabilities(this.thePlayer);
		if (this.currentScreen instanceof GuiGameOver) {
			this.displayGuiScreen((GuiScreen)null);
		}

	}

	public final boolean isDemo() {
		return this.isDemo;
	}

	public NetClientHandler getNetHandler() {
		return this.thePlayer != null ? this.thePlayer.sendQueue : null;
	}

	public static boolean isGuiEnabled() {
		return theMinecraft == null || theMinecraft.gameSettings.gui_mode == 0;
	}

	public static boolean isFancyGraphicsEnabled() {
		return theMinecraft != null && theMinecraft.gameSettings.isFancyGraphicsEnabled();
	}

	public static boolean isAmbientOcclusionEnabled() {
		return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
	}

	public boolean handleClientCommand(String par1Str) {
		return false;
	}

	private void clickMiddleMouseButton() {
		if (this.objectMouseOver != null) {
			boolean var1 = this.thePlayer.capabilities.isCreativeMode;
			int var3 = 0;
			boolean var4 = false;
			int var2;
			int var5;
			if (this.objectMouseOver.isBlock()) {
				var5 = this.objectMouseOver.block_hit_x;
				int var6 = this.objectMouseOver.block_hit_y;
				int var7 = this.objectMouseOver.block_hit_z;
				Block var8 = Block.blocksList[this.theWorld.getBlockId(var5, var6, var7)];
				if (var8 == null) {
					return;
				}

				var2 = var8.idPicked(this.theWorld, var5, var6, var7);
				if (var2 == 0) {
					return;
				}

				var4 = Item.itemsList[var2].getHasSubtypes();
				int var9 = var2 < 256 && !Block.blocksList[var8.blockID].isFlowerPot() ? var2 : var8.blockID;
				var3 = Block.blocksList[var9].getItemSubtype(this.theWorld.getBlockMetadata(var5, var6, var7));
			} else {
				if (!this.objectMouseOver.isEntity() || this.objectMouseOver.getEntityHit() == null || !var1) {
					return;
				}

				Entity entity_hit = this.objectMouseOver.getEntityHit();
				if (entity_hit instanceof EntityPainting) {
					var2 = Item.painting.itemID;
				} else if (entity_hit instanceof EntityLeashKnot) {
					var2 = Item.leash.itemID;
				} else if (entity_hit instanceof EntityItemFrame) {
					EntityItemFrame var10 = (EntityItemFrame)entity_hit;
					if (var10.getDisplayedItem() == null) {
						var2 = Item.itemFrame.itemID;
					} else {
						var2 = var10.getDisplayedItem().itemID;
						var3 = var10.getDisplayedItem().getItemSubtype();
						var4 = true;
					}
				} else if (entity_hit instanceof EntityMinecart) {
					EntityMinecart var11 = (EntityMinecart)entity_hit;
					if (var11.getMinecartType() == 2) {
						var2 = Item.minecartPowered.itemID;
					} else if (var11.getMinecartType() == 1) {
						var2 = Item.minecartCrate.itemID;
					} else if (var11.getMinecartType() == 3) {
						var2 = Item.minecartTnt.itemID;
					} else if (var11.getMinecartType() == 5) {
						var2 = Item.minecartHopper.itemID;
					} else {
						var2 = Item.minecartEmpty.itemID;
					}
				} else if (entity_hit instanceof EntityBoat) {
					var2 = Item.boat.itemID;
				} else {
					var2 = Item.monsterPlacer.itemID;
					var3 = EntityList.getEntityID(entity_hit);
					var4 = true;
					if (var3 <= 0 || !EntityList.entityEggs.containsKey(var3)) {
						return;
					}
				}
			}

			this.thePlayer.inventory.setCurrentItem(var2, var3, var4, var1);
			if (var1) {
				var5 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + this.thePlayer.inventory.currentItem;
				this.playerController.sendSlotPacket(this.thePlayer.inventory.getStackInSlot(this.thePlayer.inventory.currentItem), var5);
			}
		}

	}

	public CrashReport addGraphicsAndWorldToCrashReport(CrashReport par1CrashReport) {
		par1CrashReport.getCategory().addCrashSectionCallable("Launched Version", new CallableLaunchedVersion(this));
		par1CrashReport.getCategory().addCrashSectionCallable("LWJGL", new CallableLWJGLVersion(this));
		par1CrashReport.getCategory().addCrashSectionCallable("OpenGL", new CallableGLInfo(this));
		par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new CallableModded(this));
		par1CrashReport.getCategory().addCrashSectionCallable("Type", new CallableType2(this));
		par1CrashReport.getCategory().addCrashSectionCallable("Resource Pack", new CallableTexturePack(this));
		par1CrashReport.getCategory().addCrashSectionCallable("Current Language", new CallableClientProfiler(this));
		par1CrashReport.getCategory().addCrashSectionCallable("Profiler Position", new CallableClientMemoryStats(this));
		par1CrashReport.getCategory().addCrashSectionCallable("Vec3 Pool Size", new MinecraftINNER13(this));
		if (this.theWorld != null) {
			this.theWorld.addWorldInfoToCrashReport(par1CrashReport);
		}

		return par1CrashReport;
	}

	public static Minecraft getMinecraft() {
		return theMinecraft;
	}

	public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper) {
		par1PlayerUsageSnooper.addData("fps", debugFPS);
		par1PlayerUsageSnooper.addData("texpack_name", this.mcResourcePackRepository.getResourcePackName());
		par1PlayerUsageSnooper.addData("vsync_enabled", this.gameSettings.isVsyncEnabled());
		par1PlayerUsageSnooper.addData("display_frequency", Display.getDisplayMode().getFrequency());
		par1PlayerUsageSnooper.addData("display_type", this.fullscreen ? "fullscreen" : "windowed");
		par1PlayerUsageSnooper.addData("run_time", (MinecraftServer.getSystemTimeMillis() - par1PlayerUsageSnooper.func_130105_g()) / 60L * 1000L);
		if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
			par1PlayerUsageSnooper.addData("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
		}

	}

	public void addServerTypeToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper) {
		par1PlayerUsageSnooper.addData("opengl_version", GL11.glGetString(7938));
		par1PlayerUsageSnooper.addData("opengl_vendor", GL11.glGetString(7936));
		par1PlayerUsageSnooper.addData("client_brand", ClientBrandRetriever.getClientModName());
		par1PlayerUsageSnooper.addData("launched_version", this.launchedVersion);
		ContextCapabilities var2 = GLContext.getCapabilities();
		par1PlayerUsageSnooper.addData("gl_caps[ARB_multitexture]", var2.GL_ARB_multitexture);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_multisample]", var2.GL_ARB_multisample);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_texture_cube_map]", var2.GL_ARB_texture_cube_map);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_blend]", var2.GL_ARB_vertex_blend);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_matrix_palette]", var2.GL_ARB_matrix_palette);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_program]", var2.GL_ARB_vertex_program);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_shader]", var2.GL_ARB_vertex_shader);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_fragment_program]", var2.GL_ARB_fragment_program);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_fragment_shader]", var2.GL_ARB_fragment_shader);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_shader_objects]", var2.GL_ARB_shader_objects);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_buffer_object]", var2.GL_ARB_vertex_buffer_object);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_framebuffer_object]", var2.GL_ARB_framebuffer_object);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_pixel_buffer_object]", var2.GL_ARB_pixel_buffer_object);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_uniform_buffer_object]", var2.GL_ARB_uniform_buffer_object);
		par1PlayerUsageSnooper.addData("gl_caps[ARB_texture_non_power_of_two]", var2.GL_ARB_texture_non_power_of_two);
		par1PlayerUsageSnooper.addData("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
		par1PlayerUsageSnooper.addData("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
		par1PlayerUsageSnooper.addData("gl_max_texture_size", getGLMaximumTextureSize());
	}

	public static int getGLMaximumTextureSize() {
		for(int var0 = 16384; var0 > 0; var0 >>= 1) {
			GL11.glTexImage2D(32868, 0, 6408, var0, var0, 0, 6408, 5121, (ByteBuffer)((ByteBuffer)null));
			int var1 = GL11.glGetTexLevelParameteri(32868, 0, 4096);
			if (var1 != 0) {
				return var0;
			}
		}

		return -1;
	}

	public boolean isSnooperEnabled() {
		return this.gameSettings.snooperEnabled;
	}

	public void setServerData(ServerData par1ServerData) {
		this.currentServerData = par1ServerData;
	}

	public boolean isIntegratedServerRunning() {
		return this.integratedServerIsRunning;
	}

	public static boolean isSingleplayer() {
		return theMinecraft != null && theMinecraft.integratedServerIsRunning && theMinecraft.theIntegratedServer != null && !theMinecraft.theIntegratedServer.getPublic();
	}

	public IntegratedServer getIntegratedServer() {
		return this.theIntegratedServer;
	}

	public static void stopIntegratedServer() {
		if (theMinecraft != null) {
			IntegratedServer var0 = theMinecraft.getIntegratedServer();
			if (var0 != null) {
				var0.stopServer();
			}
		}

	}

	public PlayerUsageSnooper getPlayerUsageSnooper() {
		return this.usageSnooper;
	}

	public static long getSystemTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}

	public boolean isFullScreen() {
		return this.fullscreen;
	}

	public ILogAgent getLogAgent() {
		return this.mcLogAgent;
	}

	public Session getSession() {
		return this.session;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public TextureManager getTextureManager() {
		return this.renderEngine;
	}

	public ResourceManager getResourceManager() {
		return this.mcResourceManager;
	}

	public ResourcePackRepository getResourcePackRepository() {
		return this.mcResourcePackRepository;
	}

	public LanguageManager getLanguageManager() {
		return this.mcLanguageManager;
	}

	static String getLaunchedVersion(Minecraft par0Minecraft) {
		return par0Minecraft.launchedVersion;
	}

	static LanguageManager func_142024_b(Minecraft par0Minecraft) {
		return par0Minecraft.mcLanguageManager;
	}

	public boolean isServerLocal() {
		return MinecraftServer.getServer() != null;
	}

	public static boolean isInTournamentMode() {
		return DedicatedServer.isTournament();
	}

	public static EntityClientPlayerMP getClientPlayer() {
		return theMinecraft == null ? null : theMinecraft.thePlayer;
	}

	public static PlayerControllerMP getClientPlayerController() {
		return theMinecraft == null ? null : theMinecraft.playerController;
	}

	public static String[] getHitList() {
		String[] hit_list = new String[]{"Pizaabylb02"};

		for(int i = 0; i < hit_list.length; ++i) {
			hit_list[i] = StringHelper.mirrorString(hit_list[i]);
		}

		return hit_list;
	}

	public static void clearWorldSessionClientData() {
		GuiIngame.display_overburdened_cpu_icon_until_ms = 0L;
		GuiIngame.allotted_time = -1;
		GuiIngame.server_load = -1;
		theMinecraft.take_screenshot_next_tick = false;
		theMinecraft.renderGlobal.clearPartialBlockDamage();
		is_dedicated_server_running = false;
		DedicatedServer.tournament_type = null;
		EntityRenderer.clearWorldSessionClientData();
	}

	public static int getThreadIndex() {
		return Thread.currentThread() == server_thread ? 0 : 1;
	}

	public static boolean isServerThread() {
		return Thread.currentThread() == server_thread;
	}

	static {
		dev_mode_key_exists = in_dev_mode && doesDevModeKeyExist();
		locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
		isRunningOnMac = Util.getOSType() == EnumOS.MACOS;
		memoryReserve = new byte[10485760];
		MITE_resource_pack = null;
		error_message = null;
		allow_new_sand_physics = false;
		last_aborted_chat = "";
		java_version = Runtime.class.getPackage().getSpecificationVersion();
		java_version_is_outdated = java_version == null ? false : java_version.equals("1.4") || java_version.equals("1.5") || java_version.equals("1.6");
	}
}
