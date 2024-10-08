package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class DedicatedPlayerList extends ServerConfigurationManager {
   private File opsList;
   private File whiteList;

   public DedicatedPlayerList(DedicatedServer par1DedicatedServer) {
      super(par1DedicatedServer);
      this.opsList = par1DedicatedServer.getFile("ops.txt");
      this.whiteList = par1DedicatedServer.getFile("white-list.txt");
      this.viewDistance = par1DedicatedServer.getIntProperty("view-distance", 10);
      this.maxPlayers = par1DedicatedServer.getIntProperty("max-players", 20);
      if (!par1DedicatedServer.isSinglePlayer()) {
         this.getBannedPlayers().setListActive(true);
         this.getBannedIPs().setListActive(true);
      }

      this.getBannedPlayers().loadBanList();
      this.getBannedPlayers().saveToFileWithHeader();
      this.getBannedIPs().loadBanList();
      this.getBannedIPs().saveToFileWithHeader();
      this.loadOpsList();
      this.readWhiteList();
      this.saveOpsList();
      if (!this.whiteList.exists()) {
         this.getWhiteListedPlayers().add("*");
         this.saveWhiteList();
      }

   }

   public void addOp(String par1Str) {
      super.addOp(par1Str);
      this.saveOpsList();
   }

   public void removeOp(String par1Str) {
      super.removeOp(par1Str);
      this.saveOpsList();
   }

   public void removeFromWhitelist(String par1Str) {
      super.removeFromWhitelist(par1Str);
      this.saveWhiteList();
   }

   public void addToWhiteList(String par1Str) {
      super.addToWhiteList(par1Str);
      this.saveWhiteList();
   }

   public void loadWhiteList() {
      this.readWhiteList();
   }

   private void loadOpsList() {
      this.getOps().clear();
   }

   private void saveOpsList() {
   }

   private void readWhiteList() {
      try {
         this.getWhiteListedPlayers().clear();
         BufferedReader var1 = new BufferedReader(new FileReader(this.whiteList));
         String var2 = "";

         while((var2 = var1.readLine()) != null) {
            this.getWhiteListedPlayers().add(var2.trim().toLowerCase());
         }

         var1.close();
      } catch (Exception var3) {
         this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to load white-list: " + var3);
      }

   }

   private void saveWhiteList() {
      try {
         PrintWriter var1 = new PrintWriter(new FileWriter(this.whiteList, false));
         Iterator var2 = this.getWhiteListedPlayers().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.println(var3);
         }

         var1.close();
      } catch (Exception var4) {
         this.getDedicatedServerInstance().getLogAgent().logWarning("Failed to save white-list: " + var4);
      }

   }

   public boolean isAllowedToLogin(String par1Str) {
      par1Str = par1Str.trim().toLowerCase();
      return !this.isWhiteListEnabled() || this.isPlayerOpped(par1Str) || this.isPlayerWhiteListed(par1Str);
   }

   public DedicatedServer getDedicatedServerInstance() {
      return (DedicatedServer)super.getServerInstance();
   }

   public MinecraftServer getServerInstance() {
      return this.getDedicatedServerInstance();
   }
}
