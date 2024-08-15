package net.minecraft.client.multiplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.KeyedValuesString;

public class ServerList {
   private final Minecraft mc;
   private final List servers = new ArrayList();
   private final String servers_dat_filepath = "MITE/servers.dat";
   public static final List preset_server_ips = new ArrayList();
   public static final String public_servers_dir = "MITE/public_servers/";
   public static final String public_servers_filepath = "MITE/public_servers/public_servers.txt";

   private void loadPresetServerIPs() {
      try {
         BufferedReader br = new BufferedReader(new FileReader(new File("MITE/public_servers/public_servers.txt")));

         String line;
         while((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
               KeyedValuesString kvs = new KeyedValuesString(line);
               String server_address = kvs.getValue("server_address", true);
               String world_name = kvs.getValue("world_name", true);
               String start_date = kvs.getValue("start_date", true);
               String description = kvs.getValue("description", true);
               String website = kvs.getValue("website", true);
               String image_url = kvs.getValue("image_url", true);

               int color;
               try {
                  String color_string = kvs.getValue("theme_color", true);
                  color = color_string == null ? 8947848 : Integer.parseInt(color_string, 16);
               } catch (NumberFormatException var14) {
                  NumberFormatException e = var14;
                  color = 8947848;
                  e.printStackTrace();
               }

               float backdrop_opacity;
               try {
                  String backdrop_opacity_string = kvs.getValue("backdrop_opacity", true);
                  backdrop_opacity = backdrop_opacity_string == null ? 0.5F : Float.parseFloat(backdrop_opacity_string);
               } catch (NumberFormatException var13) {
                  NumberFormatException e = var13;
                  backdrop_opacity = 0.5F;
                  e.printStackTrace();
               }

               this.servers.add((new ServerData(world_name == null ? "Public MITE Server" : world_name, server_address, true)).setInfo(start_date, description, website, image_url, color, backdrop_opacity));
            }
         }

         br.close();
      } catch (Exception var15) {
      }

   }

   public ServerList(Minecraft par1Minecraft) {
      this.mc = par1Minecraft;
      this.loadServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         NBTTagCompound var1 = CompressedStreamTools.read(new File(this.mc.mcDataDir, "MITE/servers.dat"));
         if (var1 == null) {
            return;
         }

         NBTTagList var2 = var1.getTagList("servers");

         for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
            this.servers.add(ServerData.getServerDataFromNBTCompound((NBTTagCompound)var2.tagAt(var3)));
         }
      } catch (Exception var4) {
      }

   }

   public void saveServerList() {
      try {
         NBTTagList var1 = new NBTTagList();
         Iterator var2 = this.servers.iterator();

         while(var2.hasNext()) {
            ServerData var3 = (ServerData)var2.next();
            if (!var3.is_preset) {
               var1.appendTag(var3.getNBTCompound());
            }
         }

         NBTTagCompound var5 = new NBTTagCompound();
         var5.setTag("servers", var1);
         CompressedStreamTools.safeWrite(var5, new File(this.mc.mcDataDir, "MITE/servers.dat"));
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public ServerData getServerData(int par1) {
      return (ServerData)this.servers.get(par1);
   }

   public void removeServerData(int par1) {
      this.servers.remove(par1);
   }

   public void addServerData(ServerData par1ServerData) {
      this.servers.add(par1ServerData);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int par1, int par2) {
      ServerData var3 = this.getServerData(par1);
      this.servers.set(par1, this.getServerData(par2));
      this.servers.set(par2, var3);
      this.saveServerList();
   }
}
