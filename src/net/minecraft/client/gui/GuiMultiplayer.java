package net.minecraft.client.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.LanServer;
import net.minecraft.client.multiplayer.LanServerList;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ThreadLanServerFind;
import net.minecraft.client.renderer.GLHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ExternalTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet254ServerPing;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiMultiplayer extends GuiScreen {
   private static int threadsPending;
   private static Object lock = new Object();
   private GuiScreen parentScreen;
   private GuiSlotServer serverSlotContainer;
   private ServerList internetServerList;
   private int selectedServer = -1;
   private GuiButton field_96289_p;
   private GuiButton buttonSelect;
   private GuiButton buttonDelete;
   private boolean deleteClicked;
   private boolean addClicked;
   private boolean editClicked;
   private boolean directClicked;
   private String lagTooltip;
   private ServerData theServerData;
   private LanServerList localNetworkServerList;
   private ThreadLanServerFind localServerFindThread;
   private int ticksOpened;
   private boolean field_74024_A;
   private List listofLanServers = Collections.emptyList();
   private GuiButton button_info;
   private GuiButton button_website;
   private boolean info_showing;
   private static ExternalTexture server_image_texture;

   public GuiMultiplayer(GuiScreen par1GuiScreen) {
      this.parentScreen = par1GuiScreen;
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      if (!this.field_74024_A) {
         this.field_74024_A = true;
         this.internetServerList = new ServerList(this.mc);
         this.internetServerList.loadServerList();
         this.localNetworkServerList = new LanServerList();

         try {
            this.localServerFindThread = new ThreadLanServerFind(this.localNetworkServerList);
            this.localServerFindThread.start();
         } catch (Exception var2) {
            this.mc.getLogAgent().logWarning("Unable to start LAN server detection: " + var2.getMessage());
         }

         this.serverSlotContainer = new GuiSlotServer(this);
      } else {
         this.serverSlotContainer.func_77207_a(this.width, this.height, 32, this.height - 64);
      }

      this.initGuiControls();
   }

   public void initGuiControls() {
      this.buttonList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.getString("selectServer.select")));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.getString("selectServer.direct")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.getString("selectServer.add")));
      this.buttonList.add(this.field_96289_p = new GuiButton(7, this.width / 2 - 154, this.height - 28, 74, 20, I18n.getString("selectServer.edit")));
      this.buttonList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 74 - 2, this.height - 28, 74, 20, I18n.getString("selectServer.delete")));
      this.buttonList.add(new GuiButton(8, this.width / 2 + 4 - 2, this.height - 28, 74, 20, I18n.getString("selectServer.refresh")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 74, 20, I18n.getString("gui.cancel")));
      this.buttonList.add(this.button_info = new GuiButton(9, this.width / 2 - 154, this.height - 28, 74, 20, I18n.getString("selectServer.info")));
      this.buttonList.add(this.button_website = new GuiButton(10, this.width / 2 - 74 - 2, this.height - 28, 74, 20, I18n.getString("selectServer.website")));
      boolean var1 = this.selectedServer >= 0 && this.selectedServer < this.serverSlotContainer.getSize();
      this.buttonSelect.enabled = var1;
      this.updateButtonsForSelection();
   }

   private boolean isServerSelected() {
      return this.selectedServer >= 0 && this.selectedServer < this.serverSlotContainer.getSize();
   }

   private boolean isInternetServerSelected(boolean must_be_preset) {
      return this.isServerSelected() && this.selectedServer < this.internetServerList.countServers() && (!must_be_preset || this.internetServerList.getServerData(this.selectedServer).is_preset);
   }

   private boolean isLanServerSelected() {
      return this.isServerSelected() && !this.isInternetServerSelected(false);
   }

   public void updateButtonsForSelection() {
      this.field_96289_p.enabled = false;
      this.buttonDelete.enabled = false;
      this.field_96289_p.drawButton = true;
      this.buttonDelete.drawButton = true;
      this.button_info.enabled = false;
      this.button_website.enabled = false;
      this.button_info.drawButton = false;
      this.button_website.drawButton = false;
      if (this.isInternetServerSelected(false)) {
         ServerData server_data = this.internetServerList.getServerData(this.selectedServer);
         if (server_data.is_preset) {
            this.field_96289_p.drawButton = false;
            this.buttonDelete.drawButton = false;
            this.button_info.enabled = server_data.hasInfo();
            this.button_website.enabled = server_data.hasWebsite();
            this.button_info.drawButton = true;
            this.button_website.drawButton = true;
         } else {
            this.field_96289_p.enabled = true;
            this.buttonDelete.enabled = true;
            this.button_info.drawButton = false;
            this.button_website.drawButton = false;
         }

      }
   }

   public void updateScreen() {
      this.button_info.displayString = I18n.getString(this.info_showing ? "selectServer.list" : "selectServer.info");
      super.updateScreen();
      ++this.ticksOpened;
      if (this.localNetworkServerList.getWasUpdated()) {
         this.listofLanServers = this.localNetworkServerList.getLanServers();
         this.localNetworkServerList.setWasNotUpdated();
      }

   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      if (this.localServerFindThread != null) {
         this.localServerFindThread.interrupt();
         this.localServerFindThread = null;
      }

   }

   protected void actionPerformed(GuiButton par1GuiButton) {
      if (par1GuiButton.enabled) {
         if ((!this.isServerSelected() || this.isInternetServerSelected(true)) && (par1GuiButton.id == 2 || par1GuiButton.id == 7)) {
            return;
         }

         if (par1GuiButton.id == 2) {
            String var2 = this.internetServerList.getServerData(this.selectedServer).serverName;
            if (var2 != null) {
               this.deleteClicked = true;
               String var3 = I18n.getString("selectServer.deleteQuestion");
               String var4 = "'" + var2 + "' " + I18n.getString("selectServer.deleteWarning");
               String var5 = I18n.getString("selectServer.deleteButton");
               String var6 = I18n.getString("gui.cancel");
               GuiYesNoMITE var7 = new GuiYesNoMITE(this, var3, var4, var5, var6, this.selectedServer);
               this.mc.displayGuiScreen(var7);
            }
         } else if (par1GuiButton.id == 1) {
            this.joinServer(this.selectedServer);
         } else if (par1GuiButton.id == 4) {
            this.directClicked = true;
            this.mc.displayGuiScreen(new GuiScreenServerList(this, this.theServerData = new ServerData(I18n.getString("selectServer.defaultName"), "")));
         } else if (par1GuiButton.id == 3) {
            this.addClicked = true;
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.theServerData = new ServerData(I18n.getString("selectServer.defaultName"), ""), "add"));
         } else if (par1GuiButton.id == 7) {
            this.editClicked = true;
            ServerData var8 = this.internetServerList.getServerData(this.selectedServer);
            this.theServerData = new ServerData(var8.serverName, var8.serverIP);
            this.theServerData.setHideAddress(var8.isHidingAddress());
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.theServerData, "edit"));
         } else if (par1GuiButton.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (par1GuiButton.id == 8) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
         } else if (par1GuiButton.id == 9) {
            this.info_showing = !this.info_showing;
         } else if (par1GuiButton.id == 10) {
            try {
               Class var3 = Class.forName("java.awt.Desktop");
               Object var4 = var3.getMethod("getDesktop").invoke((Object)null);
               var3.getMethod("browse", URI.class).invoke(var4, new URI(this.internetServerList.getServerData(this.selectedServer).website));
            } catch (Throwable var8) {
               Throwable var5 = var8;
               var5.printStackTrace();
            }
         } else {
            this.serverSlotContainer.actionPerformed(par1GuiButton);
         }
      }

   }

   public void confirmClicked(boolean par1, int par2) {
      if (this.deleteClicked) {
         this.deleteClicked = false;
         if (par1) {
            this.internetServerList.removeServerData(par2);
            this.internetServerList.saveServerList();
            this.selectedServer = -1;
         }

         this.mc.displayGuiScreen(this);
      } else if (this.directClicked) {
         this.directClicked = false;
         if (par1) {
            this.connectToServer(this.theServerData);
         } else {
            this.mc.displayGuiScreen(this);
         }
      } else if (this.addClicked) {
         this.addClicked = false;
         if (par1) {
            this.internetServerList.addServerData(this.theServerData);
            this.internetServerList.saveServerList();
            this.selectedServer = -1;
         }

         this.mc.displayGuiScreen(this);
      } else if (this.editClicked) {
         this.editClicked = false;
         if (par1) {
            ServerData var3 = this.internetServerList.getServerData(this.selectedServer);
            var3.serverName = this.theServerData.serverName;
            var3.serverIP = this.theServerData.serverIP;
            var3.setHideAddress(this.theServerData.isHidingAddress());
            this.internetServerList.saveServerList();
         }

         this.mc.displayGuiScreen(this);
      }

   }

   protected void keyTyped(char par1, int par2) {
      int var3 = this.selectedServer;
      if (par2 == 1 && this.selectedServer >= 0) {
         if (this.info_showing) {
            this.actionPerformed(this.button_info);
         } else {
            this.selectedServer = -1;
            this.buttonSelect.enabled = false;
            this.field_96289_p.enabled = false;
            this.buttonDelete.enabled = false;
            this.button_info.enabled = false;
            this.button_website.enabled = false;
         }

      } else {
         if (par2 == 59) {
            this.mc.gameSettings.hideServerAddress = !this.mc.gameSettings.hideServerAddress;
            this.mc.gameSettings.saveOptions();
         } else if (isShiftKeyDown() && par2 == 200) {
            if (var3 > 0 && var3 < this.internetServerList.countServers()) {
               this.internetServerList.swapServers(var3, var3 - 1);
               --this.selectedServer;
               if (var3 < this.internetServerList.countServers() - 1) {
                  this.serverSlotContainer.func_77208_b(-this.serverSlotContainer.slotHeight);
               }
            }
         } else if (isShiftKeyDown() && par2 == 208) {
            if (var3 >= 0 & var3 < this.internetServerList.countServers() - 1) {
               this.internetServerList.swapServers(var3, var3 + 1);
               ++this.selectedServer;
               if (var3 > 0) {
                  this.serverSlotContainer.func_77208_b(this.serverSlotContainer.slotHeight);
               }
            }
         } else if (par2 != 28 && par2 != 156) {
            super.keyTyped(par1, par2);
         } else if (this.isServerSelected()) {
            this.actionPerformed(this.buttonSelect);
         } else {
            this.actionPerformed((GuiButton)this.buttonList.get(2));
         }

      }
   }

   private int calcTextHeight(int lines, int padding) {
      return this.fontRenderer.FONT_HEIGHT * lines + padding * (lines - 1);
   }

   private void drawServerInfoText(int margin_x, ServerData sd) {
      String start_date = sd.start_date == null ? "Not available" : sd.start_date;
      String description = sd.description == null ? "Not available" : sd.description;
      int center_x = this.width / 2;
      int heading_color = sd.theme_color;
      int text_color = 13684944;
      int y = 50;
      int lowest_line_y = this.height - 91;
      int extra_vertical_spacing = this.fontRenderer.FONT_HEIGHT / 2;
      this.drawDarkenedArea(margin_x, y - 5, Math.max(this.fontRenderer.getStringWidth("World Name: " + sd.serverName), this.fontRenderer.getStringWidth("Started On: " + start_date)) + 10, this.calcTextHeight(2, extra_vertical_spacing) + 10 - 1, sd.backdrop_opacity);
      String heading = "World Name: ";
      this.drawString(this.fontRenderer, heading, margin_x + 5, y, heading_color);
      this.drawString(this.fontRenderer, sd.serverName, margin_x + 5 + this.fontRenderer.getStringWidth(heading), y, text_color);
      y += this.fontRenderer.FONT_HEIGHT + extra_vertical_spacing;
      heading = "Started On: ";
      this.drawString(this.fontRenderer, heading, margin_x + 5 + 1, y, heading_color);
      this.drawString(this.fontRenderer, start_date, margin_x + 5 + 1 + this.fontRenderer.getStringWidth(heading), y, text_color);
      int var10000 = y + this.fontRenderer.FONT_HEIGHT + extra_vertical_spacing;
      String[] description_lines = description == null ? null : description.split(Pattern.quote("/"));
      int text_height = this.calcTextHeight(description_lines.length, 2);
      this.drawDarkenedArea(margin_x, lowest_line_y - text_height + this.fontRenderer.FONT_HEIGHT - 5, this.width - margin_x * 2, text_height + 10 - 1, sd.backdrop_opacity);
      y = lowest_line_y;
      heading = "Description: ";

      for(int i = description_lines.length - 1; i >= 0; --i) {
         if (i == 0) {
            this.drawString(this.fontRenderer, heading, margin_x + 5, y, heading_color);
         }

         this.drawString(this.fontRenderer, description_lines[i], i == 0 ? margin_x + 5 + this.fontRenderer.getStringWidth(heading) : margin_x + 5, y, text_color);
         y -= this.fontRenderer.FONT_HEIGHT + 2;
      }

   }

   private void drawDarkenedArea(int min_x, int min_y, int width, int height, float opacity) {
      GLHelper.disable(3553);
      GLHelper.enable(3042);
      int max_x = min_x + width;
      int max_y = min_y + height;
      Tessellator.instance.startDrawingQuads();
      Tessellator.instance.setColorRGBA_F(0.0F, 0.0F, 0.0F, opacity);
      Tessellator.instance.addVertex((double)min_x, (double)max_y, 0.0);
      Tessellator.instance.addVertex((double)max_x, (double)max_y, 0.0);
      Tessellator.instance.addVertex((double)max_x, (double)min_y, 0.0);
      Tessellator.instance.addVertex((double)min_x, (double)min_y, 0.0);
      Tessellator.instance.draw();
      GLHelper.restore(3042);
      GLHelper.restore(3553);
   }

   public void drawScreen(int par1, int par2, float par3) {
      Minecraft.clearWorldSessionClientData();
      this.lagTooltip = null;
      this.drawDefaultBackground();
      if (this.info_showing) {
         this.serverSlotContainer.drawDarkenedBackground(1);
         ServerData sd = this.internetServerList.getServerData(this.selectedServer);
         int margin_x = this.width / 2 - 154;
         if (server_image_texture != null) {
            TextureUtil.bindTexture(server_image_texture.getGlTextureId());
            Tessellator var18 = Tessellator.instance;
            int left = 0;
            int bottom = 0;
            int right = this.width;
            int top = this.height;
            var18.startDrawingQuads();
            var18.setColorOpaque_F(0.7F, 0.7F, 0.7F);
            var18.addVertexWithUV((double)left, (double)top, 0.0, 0.0, 1.0);
            var18.addVertexWithUV((double)right, (double)top, 0.0, 1.0, 1.0);
            var18.addVertexWithUV((double)right, (double)bottom, 0.0, 1.0, 0.0);
            var18.addVertexWithUV((double)left, (double)bottom, 0.0, 0.0, 0.0);
            var18.draw();
         }

         this.drawServerInfoText(margin_x, sd);
         this.serverSlotContainer.drawDarkenedBackground(2);
         GL11.glEnable(3553);
         GL11.glShadeModel(7424);
         GL11.glEnable(3008);
         GL11.glDisable(3042);
      } else {
         this.serverSlotContainer.drawScreen(par1, par2, par3);
      }

      this.drawCenteredString(this.fontRenderer, I18n.getString("multiplayer.title"), this.width / 2, 20, 16777215);
      super.drawScreen(par1, par2, par3);
      if (this.lagTooltip != null) {
         this.func_74007_a(this.lagTooltip, par1, par2);
      }

   }

   private void joinServer(int par1) {
      Minecraft.theMinecraft.increment_joinMultiplayerStat_asap = true;
      if (par1 < this.internetServerList.countServers()) {
         this.connectToServer(this.internetServerList.getServerData(par1));
      } else {
         par1 -= this.internetServerList.countServers();
         if (par1 < this.listofLanServers.size()) {
            LanServer var2 = (LanServer)this.listofLanServers.get(par1);
            this.connectToServer(new ServerData(var2.getServerMotd(), var2.getServerIpPort()));
         }
      }

   }

   private void connectToServer(ServerData par1ServerData) {
      this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, par1ServerData));
   }

   private static void func_74017_b(ServerData par0ServerData) throws IOException {
      ServerAddress var1 = ServerAddress.func_78860_a(par0ServerData.serverIP);
      Socket var2 = null;
      DataInputStream var3 = null;
      DataOutputStream var4 = null;

      try {
         var2 = new Socket();
         var2.setSoTimeout(3000);
         var2.setTcpNoDelay(true);
         var2.setTrafficClass(18);
         var2.connect(new InetSocketAddress(var1.getIP(), var1.getPort()), 3000);
         var3 = new DataInputStream(var2.getInputStream());
         var4 = new DataOutputStream(var2.getOutputStream());
         Packet254ServerPing var5 = new Packet254ServerPing(78, var1.getIP(), var1.getPort());
         var4.writeByte(var5.getPacketId());
         var5.writePacketData(var4);
         if (var3.read() != 255) {
            throw new IOException("Bad message");
         }

         String var6 = Packet.readString(var3, 256);
         char[] var7 = var6.toCharArray();

         int var9;
         for(var9 = 0; var9 < var7.length; ++var9) {
            if (var7[var9] != 167 && var7[var9] != 0 && ChatAllowedCharacters.allowedCharacters.indexOf(var7[var9]) < 0) {
               var7[var9] = '?';
            }
         }

         var6 = new String(var7);
         int var10;
         String[] var27;
         if (var6.startsWith("§") && var6.length() > 1) {
            var27 = var6.substring(1).split("\u0000");
            if (MathHelper.parseIntWithDefault(var27[0], 0) == 1) {
               par0ServerData.serverMOTD = var27[3];
               par0ServerData.field_82821_f = MathHelper.parseIntWithDefault(var27[1], par0ServerData.field_82821_f);
               par0ServerData.gameVersion = var27[2];
               var9 = MathHelper.parseIntWithDefault(var27[4], 0);
               var10 = MathHelper.parseIntWithDefault(var27[5], 0);
               if (var9 >= 0 && var10 >= 0) {
                  par0ServerData.populationInfo = EnumChatFormatting.GRAY + "" + var9 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var10;
               } else {
                  par0ServerData.populationInfo = "" + EnumChatFormatting.DARK_GRAY + "???";
               }
            } else {
               par0ServerData.gameVersion = "???";
               par0ServerData.serverMOTD = "" + EnumChatFormatting.DARK_GRAY + "???";
               par0ServerData.field_82821_f = 79;
               par0ServerData.populationInfo = "" + EnumChatFormatting.DARK_GRAY + "???";
            }
         } else {
            var27 = var6.split("§");
            var6 = var27[0];
            var9 = -1;
            var10 = -1;

            try {
               var9 = Integer.parseInt(var27[1]);
               var10 = Integer.parseInt(var27[2]);
            } catch (Exception var25) {
            }

            par0ServerData.serverMOTD = EnumChatFormatting.GRAY + var6;
            if (var9 >= 0 && var10 > 0) {
               par0ServerData.populationInfo = EnumChatFormatting.GRAY + "" + var9 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var10;
            } else {
               par0ServerData.populationInfo = "" + EnumChatFormatting.DARK_GRAY + "???";
            }

            par0ServerData.gameVersion = "1.3";
            par0ServerData.field_82821_f = 77;
         }
      } finally {
         try {
            if (var3 != null) {
               var3.close();
            }
         } catch (Throwable var24) {
         }

         try {
            if (var4 != null) {
               var4.close();
            }
         } catch (Throwable var23) {
         }

         try {
            if (var2 != null) {
               var2.close();
            }
         } catch (Throwable var22) {
         }

      }

   }

   protected void func_74007_a(String par1Str, int par2, int par3) {
      if (par1Str != null) {
         int var4 = par2 + 12;
         int var5 = par3 - 12;
         int var6 = this.fontRenderer.getStringWidth(par1Str);
         this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.fontRenderer.drawStringWithShadow(par1Str, var4, var5, -1);
      }

   }

   static ServerList getInternetServerList(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.internetServerList;
   }

   static List getListOfLanServers(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.listofLanServers;
   }

   static int getSelectedServer(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.selectedServer;
   }

   static int getAndSetSelectedServer(GuiMultiplayer par0GuiMultiplayer, int par1) {
      return par0GuiMultiplayer.selectedServer = par1;
   }

   static GuiButton getButtonSelect(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.buttonSelect;
   }

   static GuiButton getButtonEdit(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.field_96289_p;
   }

   static GuiButton getButtonDelete(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.buttonDelete;
   }

   static void func_74008_b(GuiMultiplayer par0GuiMultiplayer, int par1) {
      par0GuiMultiplayer.joinServer(par1);
   }

   static int getTicksOpened(GuiMultiplayer par0GuiMultiplayer) {
      return par0GuiMultiplayer.ticksOpened;
   }

   static Object getLock() {
      return lock;
   }

   static int getThreadsPending() {
      return threadsPending;
   }

   static int increaseThreadsPending() {
      return threadsPending++;
   }

   static void func_82291_a(ServerData par0ServerData) throws IOException {
      func_74017_b(par0ServerData);
   }

   static int decreaseThreadsPending() {
      return threadsPending--;
   }

   static String getAndSetLagTooltip(GuiMultiplayer par0GuiMultiplayer, String par1Str) {
      return par0GuiMultiplayer.lagTooltip = par1Str;
   }

   public static void loadServerImage(String filename) {
      if (filename == null) {
         server_image_texture = null;
      } else {
         server_image_texture = new ExternalTexture(new File("MITE/public_servers/" + filename));

         try {
            server_image_texture.loadTexture((ResourceManager)null);
         } catch (Exception var2) {
            server_image_texture = null;
         }

      }
   }
}
