package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.GuiScreenClientOutdated;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumSpecialSplash;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen {
   private static final Random rand = new Random();
   private float updateCounter;
   private String splashText = "missingno";
   private GuiButton buttonResetDemo;
   private int panoramaTimer;
   private DynamicTexture viewportTexture;
   private boolean field_96141_q = true;
   private static boolean field_96140_r;
   private static boolean field_96139_s;
   private final Object field_104025_t = new Object();
   private final Object field_104025_t_MITE = new Object();
   private String field_92025_p;
   private String field_104024_v;
   public final String field_92025_p_MITE = "MITE Resource Pack 1.6.4 needs to be installed!";
   public final String field_104024_v_MITE = "http://minecraft-is-too-easy.com";
   private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
   private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation clickMeTexture = new ResourceLocation("textures/gui/title/click_me_angled.png");
   private static final ResourceLocation[] gutenTagTextures = getAnimatedTextures(7, "textures/gui/title/hans_grosse/", false);
   private static final ResourceLocation bulletHoleTexture = new ResourceLocation("textures/gui/title/bullet_hole.png");
   private static final ResourceLocation ice_cream = new ResourceLocation("textures/items/bowls/ice_cream.png");
   private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
   public static final String field_96138_a;
   public static final String field_96138_a_MITE;
   private int field_92024_r;
   private int field_92023_s;
   private int field_92022_t;
   private int field_92021_u;
   private int field_92020_v;
   private int field_92019_w;
   private ResourceLocation field_110351_G;
   private GuiButton minecraftRealmsButton;
   private int field_92024_r_MITE;
   private int field_92023_s_MITE;
   private int field_92022_t_MITE;
   private int field_92021_u_MITE;
   private int field_92020_v_MITE;
   private int field_92019_w_MITE;
   private ResourceLocation field_110351_G_MITE;
   private EnumSpecialSplash enum_special_splash;
   private final String ronin_pawn_url = "https://www.youtube.com/watch?v=UaSVsuklHjA";
   private final String mite_migos_url = "http://www.minecraftforum.net/forums/servers/pc-servers/survival-servers/2383945-mite-migos-fan-server-minecraft-is-too-easy";
   private final String cogmind_url = "http://www.gridsagegames.com/cogmind/";
   private final String ludwig_url = "http://imgur.com/a/YAzpR";
   private int animated_texture_index;
   private long next_animated_texture_ms = System.currentTimeMillis();
   private boolean gunshot_sound_preloaded;
   private final int max_bullet_holes = 16;
   private int[] bullet_hole_x = new int[16];
   private int[] bullet_hole_y = new int[16];
   private float[] bullet_hole_rotation = new float[16];
   private long[] bullet_hole_created_ms = new long[16];
   private int minimum_firing_loops;

   public GuiMainMenu() {
      BufferedReader var1 = null;

      String var3;
      try {
         ArrayList var2 = new ArrayList();
         var1 = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));

         while((var3 = var1.readLine()) != null) {
            var3 = var3.trim();
            if (!var3.isEmpty()) {
               var2.add(var3);
            }
         }

         Random random = new Random(System.currentTimeMillis() / 1000L / 60L);

         do {
            this.splashText = (String)var2.get(random.nextInt(var2.size()));
         } while(this.splashText.hashCode() == 125780783);
      } catch (IOException var13) {
      } finally {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var12) {
            }
         }

      }

      this.updateCounter = rand.nextFloat();
      this.field_92025_p = "";
      String var14 = System.getProperty("os_architecture");
      var3 = System.getProperty("java_version");
      if ("ppc".equalsIgnoreCase(var14)) {
         this.field_92025_p = "" + EnumChatFormatting.BOLD + "Notice!" + EnumChatFormatting.RESET + " PowerPC compatibility will be dropped in Minecraft 1.6";
         this.field_104024_v = "http://tinyurl.com/javappc";
      } else if (var3 != null && var3.startsWith("1.5")) {
         this.field_92025_p = "" + EnumChatFormatting.BOLD + "Notice!" + EnumChatFormatting.RESET + " Java 1.5 compatibility will be dropped in Minecraft 1.6";
         this.field_104024_v = "http://tinyurl.com/javappc";
      }

   }

   public void updateScreen() {
      ++this.panoramaTimer;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char par1, int par2) {
   }

   public void initGui() {
      this.viewportTexture = new DynamicTexture(256, 256);
      this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      if (var1.get(2) + 1 == 11 && var1.get(5) == 9) {
         this.splashText = "Happy birthday, ez!";
      } else if (var1.get(2) + 1 == 6 && var1.get(5) == 1) {
         this.splashText = "Happy birthday, Notch!";
      } else if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      boolean var2 = true;
      int var3 = this.height / 4 + 48;
      if (this.mc.isDemo()) {
         this.addDemoButtons(var3, 24);
      } else {
         this.addSingleplayerMultiplayerButtons(var3, 24);
      }

      GuiButton button_options = new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, 98, 20, I18n.getString("menu.options"));
      if (Main.is_MITE_DS) {
         button_options.enabled = false;
      }

      this.buttonList.add(button_options);
      this.func_130020_g();
      this.buttonList.add(new GuiButton(4, this.width / 2 + 2, var3 + 72 + 12, 98, 20, I18n.getString("menu.quit")));
      this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, var3 + 72 + 12));
      if (Minecraft.MITE_resource_pack != null) {
         this.buttonList.add(new GuiButtonForum(6, this.width / 2 + 124 - 20, var3 + 72 + 12));
      }

      Object var4 = this.field_104025_t;
      int var5;
      synchronized(this.field_104025_t) {
         this.field_92023_s = this.fontRenderer.getStringWidth(this.field_92025_p);
         this.field_92024_r = this.fontRenderer.getStringWidth(field_96138_a);
         var5 = Math.max(this.field_92023_s, this.field_92024_r);
         this.field_92022_t = (this.width - var5) / 2;
         this.field_92021_u = ((GuiButton)this.buttonList.get(0)).yPosition - 24;
         this.field_92020_v = this.field_92022_t + var5;
         this.field_92019_w = this.field_92021_u + 24;
      }

      synchronized(this.field_104025_t_MITE) {
         FontRenderer var10001 = this.fontRenderer;
         this.getClass();
         this.field_92023_s_MITE = var10001.getStringWidth("MITE Resource Pack 1.6.4 needs to be installed!");
         this.field_92024_r_MITE = this.fontRenderer.getStringWidth(field_96138_a_MITE);
         var5 = Math.max(this.field_92023_s_MITE, this.field_92024_r_MITE) + 4;
         this.field_92022_t_MITE = (this.width - var5) / 2;
         this.field_92021_u_MITE = ((GuiButton)this.buttonList.get(0)).yPosition - 24 + 78;
         this.field_92020_v_MITE = this.field_92022_t_MITE + var5;
         this.field_92019_w_MITE = this.field_92021_u_MITE + 24;
      }
   }

   private void func_130020_g() {
      if (this.field_96141_q) {
         if (!field_96140_r) {
            field_96140_r = true;
            (new RunnableTitleScreen(this)).start();
         } else if (field_96139_s) {
            this.func_130022_h();
         }
      }

   }

   private void func_130022_h() {
      this.minecraftRealmsButton.drawButton = !Main.is_MITE_DS;
      if (Minecraft.MITE_resource_pack == null) {
         this.minecraftRealmsButton.drawButton = false;
      }

   }

   private void addSingleplayerMultiplayerButtons(int par1, int par2) {
      GuiButton button_singleplayer = new GuiButton(1, this.width / 2 - 100, par1, I18n.getString("menu.singleplayer" + (Main.is_MITE_DS ? "DS" : "")));
      GuiButton button_multiplayer = new GuiButton(2, this.width / 2 - 100, par1 + par2 * 1, I18n.getString("menu.multiplayer"));
      if (Main.is_MITE_DS) {
         button_singleplayer.yPosition = (button_singleplayer.yPosition + button_multiplayer.yPosition) / 2;
         button_multiplayer.enabled = false;
         button_multiplayer.drawButton = false;
      }

      if (Minecraft.MITE_resource_pack == null) {
         button_singleplayer.enabled = false;
         button_multiplayer.enabled = false;
      } else if (Minecraft.java_version_is_outdated) {
         button_singleplayer.enabled = false;
         button_multiplayer.enabled = false;
      }

      this.buttonList.add(button_singleplayer);
      this.buttonList.add(button_multiplayer);
      this.buttonList.add(this.minecraftRealmsButton = new GuiButton(14, this.width / 2 - 100, par1 + par2 * 2, I18n.getString("menu.online")));
      this.minecraftRealmsButton.drawButton = false;
   }

   private void addDemoButtons(int par1, int par2) {
      this.buttonList.add(new GuiButton(11, this.width / 2 - 100, par1, I18n.getString("menu.playdemo")));
      this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, par1 + par2 * 1, I18n.getString("menu.resetdemo")));
      ISaveFormat var3 = this.mc.getSaveLoader();
      WorldInfo var4 = var3.getWorldInfo("Demo_World");
      if (var4 == null) {
         this.buttonResetDemo.enabled = false;
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.id == 0) {
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
      }

      if (par1GuiButton.id == 5) {
         this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
      }

      if (par1GuiButton.id == 1) {
         this.mc.displayGuiScreen(new GuiSelectWorld(this));
      }

      if (par1GuiButton.id == 2) {
         this.mc.displayGuiScreen(new GuiMultiplayer(this));
      }

      if (par1GuiButton.id == 14 && this.minecraftRealmsButton.drawButton) {
         this.func_140005_i();
      }

      if (par1GuiButton.id == 4) {
         this.mc.shutdown();
      }

      if (par1GuiButton.id == 6) {
         try {
            Class var3 = Class.forName("java.awt.Desktop");
            Object var4 = var3.getMethod("getDesktop").invoke((Object)null);
            var3.getMethod("browse", URI.class).invoke(var4, new URI("http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1294284-minecraft-is-too-easy-mite-mod"));
         } catch (Throwable var5) {
            var5.printStackTrace();
         }
      }

      if (par1GuiButton.id == 11) {
         this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
      }

      if (par1GuiButton.id == 12) {
         ISaveFormat var2 = this.mc.getSaveLoader();
         WorldInfo var3 = var2.getWorldInfo("Demo_World");
         if (var3 != null) {
            GuiYesNoMITE var4 = GuiSelectWorld.getDeleteWorldScreen(this, var3.getWorldName(), 12);
            this.mc.displayGuiScreen(var4);
         }
      }

   }

   private void func_140005_i() {
      McoClient var1 = new McoClient(this.mc.getSession());

      try {
         if (var1.func_140054_c()) {
            this.mc.displayGuiScreen(new GuiScreenClientOutdated(this));
         } else {
            this.mc.displayGuiScreen(new GuiScreenOnlineServers(this));
         }
      } catch (ExceptionMcoService var3) {
         this.mc.getLogAgent().logSevere(var3.toString());
      } catch (IOException var4) {
         this.mc.getLogAgent().logSevere(var4.getLocalizedMessage());
      }

   }

   public void confirmClicked(boolean par1, int par2) {
      if (par1 && par2 == 12) {
         ISaveFormat var6 = this.mc.getSaveLoader();
         var6.flushCache();
         var6.deleteWorldDirectory("Demo_World");
         this.mc.displayGuiScreen(this);
      } else {
         Throwable var5;
         Object var4;
         Class var3;
         if (par2 == 13) {
            if (par1) {
               try {
                  var3 = Class.forName("java.awt.Desktop");
                  var4 = var3.getMethod("getDesktop").invoke((Object)null);
                  var3.getMethod("browse", URI.class).invoke(var4, new URI(this.field_104024_v));
               } catch (Throwable var7) {
                  var5 = var7;
                  var5.printStackTrace();
               }
            }

            this.mc.displayGuiScreen(this);
         } else if (par2 == 14) {
            if (par1) {
               try {
                  var3 = Class.forName("java.awt.Desktop");
                  var4 = var3.getMethod("getDesktop").invoke((Object)null);
                  Method var10000 = var3.getMethod("browse", URI.class);
                  Object[] var10002 = new Object[1];
                  this.getClass();
                  var10002[0] = new URI("http://minecraft-is-too-easy.com");
                  var10000.invoke(var4, var10002);
               } catch (Throwable var6) {
                  var5 = var6;
                  var5.printStackTrace();
               }
            }

            this.mc.displayGuiScreen(this);
         } else if (MathHelper.isInRange(par2, 15, 15 + EnumSpecialSplash.values().length - 1)) {
            if (par1) {
               try {
                  var3 = Class.forName("java.awt.Desktop");
                  var4 = var3.getMethod("getDesktop").invoke((Object)null);
                  var3.getMethod("browse", URI.class).invoke(var4, new URI(EnumSpecialSplash.values()[par2 - 15].getURL()));
               } catch (Throwable throwable) {
                  throwable.printStackTrace();
               }
            }

            this.mc.displayGuiScreen(this);
         }
      }

   }

   private void drawPanorama(int par1, int par2, float par3) {
      Tessellator var4 = Tessellator.instance;
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glEnable(3042);
      GL11.glDisable(3008);
      GL11.glDisable(2884);
      GL11.glDepthMask(false);
      GL11.glBlendFunc(770, 771);
      byte var5 = 8;

      for(int var6 = 0; var6 < var5 * var5; ++var6) {
         GL11.glPushMatrix();
         float var7 = ((float)(var6 % var5) / (float)var5 - 0.5F) / 64.0F;
         float var8 = ((float)(var6 / var5) / (float)var5 - 0.5F) / 64.0F;
         float var9 = 0.0F;
         GL11.glTranslatef(var7, var8, var9);
         GL11.glRotatef(MathHelper.sin(((float)this.panoramaTimer + par3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-((float)this.panoramaTimer + par3) * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int var10 = 0; var10 < 6; ++var10) {
            GL11.glPushMatrix();
            if (var10 == 1) {
               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 2) {
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 3) {
               GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var10 == 4) {
               GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var10 == 5) {
               GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(titlePanoramaPaths[var10]);
            var4.startDrawingQuads();
            var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
            float var11 = 0.0F;
            var4.addVertexWithUV(-1.0, -1.0, 1.0, (double)(0.0F + var11), (double)(0.0F + var11));
            var4.addVertexWithUV(1.0, -1.0, 1.0, (double)(1.0F - var11), (double)(0.0F + var11));
            var4.addVertexWithUV(1.0, 1.0, 1.0, (double)(1.0F - var11), (double)(1.0F - var11));
            var4.addVertexWithUV(-1.0, 1.0, 1.0, (double)(0.0F + var11), (double)(1.0F - var11));
            var4.draw();
            GL11.glPopMatrix();
         }

         GL11.glPopMatrix();
         GL11.glColorMask(true, true, true, false);
      }

      var4.setTranslation(0.0, 0.0, 0.0);
      GL11.glColorMask(true, true, true, true);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(2884);
      GL11.glEnable(3008);
      GL11.glEnable(2929);
   }

   private void rotateAndBlurSkybox(float par1) {
      this.mc.getTextureManager().bindTexture(this.field_110351_G);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glColorMask(true, true, true, false);
      Tessellator var2 = Tessellator.instance;
      var2.startDrawingQuads();
      byte var3 = 3;

      for(int var4 = 0; var4 < var3; ++var4) {
         var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float)(var4 + 1));
         int var5 = this.width;
         int var6 = this.height;
         float var7 = (float)(var4 - var3 / 2) / 256.0F;
         var2.addVertexWithUV((double)var5, (double)var6, (double)this.zLevel, (double)(0.0F + var7), 0.0);
         var2.addVertexWithUV((double)var5, 0.0, (double)this.zLevel, (double)(1.0F + var7), 0.0);
         var2.addVertexWithUV(0.0, 0.0, (double)this.zLevel, (double)(1.0F + var7), 1.0);
         var2.addVertexWithUV(0.0, (double)var6, (double)this.zLevel, (double)(0.0F + var7), 1.0);
      }

      var2.draw();
      GL11.glColorMask(true, true, true, true);
   }

   private void renderSkybox(int par1, int par2, float par3) {
      GL11.glViewport(0, 0, 256, 256);
      this.drawPanorama(par1, par2, par3);
      GL11.glDisable(3553);
      GL11.glEnable(3553);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      this.rotateAndBlurSkybox(par3);
      GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      Tessellator var4 = Tessellator.instance;
      var4.startDrawingQuads();
      float var5 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
      float var6 = (float)this.height * var5 / 256.0F;
      float var7 = (float)this.width * var5 / 256.0F;
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
      int var8 = this.width;
      int var9 = this.height;
      var4.addVertexWithUV(0.0, (double)var9, (double)this.zLevel, (double)(0.5F - var6), (double)(0.5F + var7));
      var4.addVertexWithUV((double)var8, (double)var9, (double)this.zLevel, (double)(0.5F - var6), (double)(0.5F - var7));
      var4.addVertexWithUV((double)var8, 0.0, (double)this.zLevel, (double)(0.5F + var6), (double)(0.5F - var7));
      var4.addVertexWithUV(0.0, 0.0, (double)this.zLevel, (double)(0.5F + var6), (double)(0.5F + var7));
      var4.draw();
   }

   private String getModifiedSplashText() {
      if (this.enum_special_splash == EnumSpecialSplash.ronin_pawn) {
         return "Ronin        Pawn!";
      } else if (this.enum_special_splash == EnumSpecialSplash.mite_migos) {
         return "";
      } else if (this.enum_special_splash == EnumSpecialSplash.guten_tag) {
         return "Guten        Tag!";
      } else if (this.splashText.equals("Attackington's Aspect!")) {
         Calendar var1 = Calendar.getInstance();
         var1.setTime(new Date());
         int ran = var1.get(5) % 2;
         return ran == 0 ? "Attackington's Aspect!" : "Withstandington's Aspect!";
      } else {
         return this.splashText;
      }
   }

   private void addBulletHole() {
      for(int i = 0; i < 16; ++i) {
         if (this.bullet_hole_created_ms[i] == 0L) {
            this.bullet_hole_x[i] = rand.nextInt(this.width);
            this.bullet_hole_y[i] = rand.nextInt(this.height);

            for(int j = 0; j < 16; ++j) {
               if (j != i && this.bullet_hole_created_ms[j] != 0L) {
                  int delta_x = this.bullet_hole_x[j] - this.bullet_hole_x[i];
                  int delta_y = this.bullet_hole_y[j] - this.bullet_hole_y[i];
                  if (delta_x * delta_x + delta_y * delta_y < 512) {
                     return;
                  }
               }
            }

            this.bullet_hole_rotation[i] = rand.nextFloat() * 360.0F;
            this.bullet_hole_created_ms[i] = System.currentTimeMillis();
            this.mc.sndManager.playSoundFX("random.glass", 1.0F, rand.nextFloat() * 0.2F + 0.8F);
            break;
         }
      }

   }

   private void fireBullet() {
      this.mc.sndManager.playSoundFX("imported.random.gunshot", 0.1F, 0.5F);
      if (rand.nextInt(8) == 0) {
         this.addBulletHole();
      }

   }

   private void makeHeavyStepSound() {
   }

   private void drawBulletHoles() {
      for(int i = 0; i < 16; ++i) {
         if (this.bullet_hole_created_ms[i] != 0L) {
            float alpha = 5.0F - (float)(System.currentTimeMillis() - this.bullet_hole_created_ms[i]) * 5.0E-4F;
            if (alpha < 0.0F) {
               this.bullet_hole_created_ms[i] = 0L;
            } else {
               this.drawBulletHole(this.bullet_hole_x[i], this.bullet_hole_y[i], this.bullet_hole_rotation[i], alpha);
            }
         }
      }

   }

   private void drawBulletHole(int x, int y, float rotation, float alpha) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      this.mc.getTextureManager().bindTexture(bulletHoleTexture);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glAlphaFunc(516, 0.01F);
      int image_actual_width = 288;
      int image_actual_height = 250;
      float image_scaling = 0.2F;
      int image_scaled_width = (int)((float)image_actual_width * image_scaling);
      int image_scaled_height = (int)((float)image_actual_height * image_scaling);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)x, (float)y, 0.0F);
      GL11.glRotatef(rotation, 0.0F, 0.0F, 1.0F);
      this.drawTexturedModalRect2(-image_scaled_width / 2, -image_scaled_height / 2, image_scaled_width, image_scaled_height);
      GL11.glPopMatrix();
      GL11.glAlphaFunc(516, 0.1F);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void drawScreen(int par1, int par2, float par3) {
      Minecraft.clearWorldSessionClientData();
      this.renderSkybox(par1, par2, par3);
      Tessellator var4 = Tessellator.instance;
      short var5 = 274;
      int var6 = this.width / 2 - var5 / 2;
      byte var7 = 30;
      this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
      this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
      this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if ((double)this.updateCounter < 1.0E-4) {
         this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 99, 44);
         this.drawTexturedModalRect(var6 + 99, var7 + 0, 129, 0, 27, 44);
         this.drawTexturedModalRect(var6 + 99 + 26, var7 + 0, 126, 0, 3, 44);
         this.drawTexturedModalRect(var6 + 99 + 26 + 3, var7 + 0, 99, 0, 26, 44);
         this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);
      } else {
         this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 155, 44);
         this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);
      }

      this.enum_special_splash = EnumSpecialSplash.getSpecialSplash(this.splashText);
      var4.setColorOpaque_I(16777215);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
      GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      float var8 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * 3.1415927F * 2.0F) * 0.1F);
      if (this.enum_special_splash == EnumSpecialSplash.mite_migos) {
         var8 *= 0.86206895F;
      } else {
         var8 = var8 * 100.0F / (float)(this.fontRenderer.getStringWidth(this.getModifiedSplashText()) + 32);
      }

      GL11.glScalef(var8, var8, var8);
      this.drawCenteredString(this.fontRenderer, this.getModifiedSplashText(), 0, -8, 16776960);
      if (this.enum_special_splash == EnumSpecialSplash.elite_dangerous) {
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         this.drawCenteredString(this.fontRenderer, "It's a different kind of game", 28, 4, 16776960);
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         this.drawCenteredString(this.fontRenderer, "TM", 204, 6, 16776960);
         GL11.glPopMatrix();
      } else if (this.enum_special_splash == EnumSpecialSplash.ice_cream) {
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(ice_cream);
         this.drawTexturedModalRect2(-3, -2, 20, 20);
      } else if (this.enum_special_splash == EnumSpecialSplash.ronin_pawn) {
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(this.enum_special_splash.getSplashTexture());
         this.drawTexturedModalRect2(-14, -18, 28, 28);
         this.mc.getTextureManager().bindTexture(clickMeTexture);
         this.drawTexturedModalRect2(-1, 7, 8, 8);
      } else if (this.enum_special_splash == EnumSpecialSplash.mite_migos) {
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(this.enum_special_splash.getSplashTexture());
         this.drawTexturedModalRect2(-48, -18, 96, 24);
         this.mc.getTextureManager().bindTexture(clickMeTexture);
         this.drawTexturedModalRect2(-8, 0, 8, 8);
      } else if (this.enum_special_splash == EnumSpecialSplash.guten_tag) {
         if (!this.gunshot_sound_preloaded) {
            this.mc.sndManager.playSoundFX("imported.random.gunshot", 0.001F, 0.5F);
            this.gunshot_sound_preloaded = true;
         }

         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(gutenTagTextures[this.animated_texture_index]);
         this.drawTexturedModalRect2(-10, -16, 28, 28);
         if (System.currentTimeMillis() >= this.next_animated_texture_ms) {
            short delay_ms;
            if (this.animated_texture_index < 3) {
               ++this.animated_texture_index;
               delay_ms = 250;
               if (this.animated_texture_index == 2) {
                  this.makeHeavyStepSound();
               }
            } else if (this.animated_texture_index == 3) {
               if (rand.nextInt(5) == 0) {
                  ++this.animated_texture_index;
                  delay_ms = 500;
                  this.minimum_firing_loops = 4;
               } else {
                  this.animated_texture_index = 0;
                  delay_ms = 250;
                  this.makeHeavyStepSound();
               }
            } else if (this.animated_texture_index == 4) {
               ++this.animated_texture_index;
               delay_ms = 125;
               this.fireBullet();
            } else if (this.animated_texture_index == 5) {
               ++this.animated_texture_index;
               delay_ms = 125;
               this.fireBullet();
            } else if (--this.minimum_firing_loops <= 0 && !(rand.nextFloat() < 0.8F)) {
               if (rand.nextFloat() < 0.5F) {
                  this.animated_texture_index = 0;
                  delay_ms = 250;
               } else {
                  this.animated_texture_index = 4;
                  if (rand.nextInt(2) == 0) {
                     delay_ms = 250;
                  } else {
                     delay_ms = 500;
                  }

                  this.minimum_firing_loops = 2;
               }
            } else {
               this.animated_texture_index = 5;
               delay_ms = 125;
               this.fireBullet();
            }

            this.next_animated_texture_ms = System.currentTimeMillis() + (long)delay_ms;
         }
      } else if (this.enum_special_splash == EnumSpecialSplash.cogmind) {
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(clickMeTexture);
         this.drawTexturedModalRect2(-1, 0, 8, 8);
      } else if (this.enum_special_splash == EnumSpecialSplash.ludwig) {
         GL11.glEnable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(clickMeTexture);
         this.drawTexturedModalRect2(-1, 0, 8, 8);
      }

      GL11.glPopMatrix();
      if ("Double whammy!".equals(this.splashText)) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)(this.width / 2 - 90), 70.0F, 0.0F);
         GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
         var8 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * 3.1415927F * 2.0F) * 0.1F);
         var8 = var8 * 100.0F / (float)(this.fontRenderer.getStringWidth(this.getModifiedSplashText()) + 32);
         GL11.glScalef(var8, var8, var8);
         this.drawCenteredString(this.fontRenderer, this.getModifiedSplashText(), 0, -8, 16776960);
         GL11.glPopMatrix();
      }

      this.drawString(this.fontRenderer, "Minecraft " + Minecraft.getVersionDescriptor(true), 2, this.height - 10, 16777215);
      String var10 = "Copyright Mojang AB. Do not distribute!";
      this.drawString(this.fontRenderer, var10, this.width - this.fontRenderer.getStringWidth(var10) - 2, this.height - 10, 16777215);
      if (this.field_92025_p != null && this.field_92025_p.length() > 0) {
         drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
         this.drawString(this.fontRenderer, this.field_92025_p, this.field_92022_t, this.field_92021_u, 16777215);
         this.drawString(this.fontRenderer, field_96138_a, (this.width - this.field_92024_r) / 2, ((GuiButton)this.buttonList.get(0)).yPosition - 12, 16777215);
      }

      if (Minecraft.MITE_resource_pack == null && "MITE Resource Pack 1.6.4 needs to be installed!" != null && "MITE Resource Pack 1.6.4 needs to be installed!".length() > 0) {
         drawRect(this.field_92022_t_MITE - 2, this.field_92021_u_MITE - 2, this.field_92020_v_MITE + 2, this.field_92019_w_MITE - 1, 1428160512);
         this.drawString(this.fontRenderer, "MITE Resource Pack 1.6.4 needs to be installed!", (this.width - this.field_92023_s_MITE) / 2, this.field_92021_u_MITE, 16777215);
         this.drawString(this.fontRenderer, field_96138_a_MITE, (this.width - this.field_92024_r_MITE) / 2, this.field_92021_u_MITE + 12, 16777215);
      } else if (Minecraft.java_version_is_outdated) {
         String msg = "Minecraft Is Too Easy requires Java 1.7 or later!";
         drawRect(this.field_92022_t_MITE - 2, this.field_92021_u_MITE - 2, this.field_92020_v_MITE + 2, this.field_92019_w_MITE - 1 - 13, 1428160512);
         this.drawString(this.fontRenderer, msg, (this.width - this.fontRenderer.getStringWidth(msg)) / 2, this.field_92021_u_MITE, 16777215);
      }

      super.drawScreen(par1, par2, par3);
      this.drawBulletHoles();
   }

   protected void mouseClicked(int par1, int par2, int par3) {
      super.mouseClicked(par1, par2, par3);
      GuiConfirmOpenLink var5;
      synchronized(this.field_104025_t) {
         if (this.field_92025_p.length() > 0 && par1 >= this.field_92022_t && par1 <= this.field_92020_v && par2 >= this.field_92021_u && par2 <= this.field_92019_w) {
            var5 = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
            var5.func_92026_h();
            this.mc.displayGuiScreen(var5);
         }
      }

      if (Minecraft.MITE_resource_pack == null) {
         synchronized(this.field_104025_t_MITE) {
            this.getClass();
            if ("MITE Resource Pack 1.6.4 needs to be installed!".length() > 0 && par1 >= this.field_92022_t_MITE && par1 <= this.field_92020_v_MITE && par2 >= this.field_92021_u_MITE && par2 <= this.field_92019_w_MITE) {
               this.getClass();
               var5 = new GuiConfirmOpenLink(this, "http://minecraft-is-too-easy.com", 14, true);
               var5.func_92026_h();
               this.mc.displayGuiScreen(var5);
            }
         }
      }

      if (this.enum_special_splash != null && this.enum_special_splash.hasURL() && par1 >= (this.width + 42) / 2 && par1 <= (this.width + 302) / 2 && par2 >= 35 && par2 <= 90) {
         GuiConfirmOpenLink guiConfirmOpenLink = new GuiConfirmOpenLink(this, this.enum_special_splash);
         guiConfirmOpenLink.func_92026_h();
         this.mc.displayGuiScreen(guiConfirmOpenLink);
      }

   }

   static Minecraft func_110348_a(GuiMainMenu par0GuiMainMenu) {
      return par0GuiMainMenu.mc;
   }

   static void func_130021_b(GuiMainMenu par0GuiMainMenu) {
      par0GuiMainMenu.func_130022_h();
   }

   static boolean func_110349_a(boolean par0) {
      field_96139_s = par0;
      return par0;
   }

   static Minecraft func_130018_c(GuiMainMenu par0GuiMainMenu) {
      return par0GuiMainMenu.mc;
   }

   static Minecraft func_130019_d(GuiMainMenu par0GuiMainMenu) {
      return par0GuiMainMenu.mc;
   }

   static ResourceLocation[] getAnimatedTextures(int num_textures, String path, boolean generate_encoded_file) {
      if (generate_encoded_file && !Minecraft.inDevMode()) {
         Minecraft.setErrorMessage("getAnimatedTextures: Error occurred");
         generate_encoded_file = false;
      }

      ResourceLocation[] RLs = new ResourceLocation[num_textures];

      for(int i = 0; i < num_textures; ++i) {
         RLs[i] = (new ResourceLocation(path + i + ".png", false)).setGenerateEncodedFile(generate_encoded_file);
         if (!RLs[i].exists()) {
            RLs[i] = new ResourceLocation(path + i + ".enc");
         }
      }

      return RLs;
   }

   static {
      field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
      field_96138_a_MITE = "Visit " + EnumChatFormatting.UNDERLINE + "minecraft-is-too-easy.com" + EnumChatFormatting.RESET + " for more information.";
   }
}
