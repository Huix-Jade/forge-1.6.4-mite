package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringHelper;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldAchievement;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class SaveHandler implements ISaveHandler, IPlayerFileData {
   private final File worldDirectory;
   private final File playersDirectory;
   private final File mapDataDir;
   private final long initializationTime = MinecraftServer.getSystemTimeMillis();
   private final String saveDirectoryName;

   public SaveHandler(File par1File, String par2Str, boolean par3) {
      this.worldDirectory = new File(par1File, par2Str);
      this.worldDirectory.mkdirs();
      this.playersDirectory = new File(this.worldDirectory, "players");
      this.mapDataDir = new File(this.worldDirectory, "data");
      this.mapDataDir.mkdirs();
      this.saveDirectoryName = par2Str;
      if (par3) {
         this.playersDirectory.mkdirs();
      }

      this.setSessionLock();
   }

   private void setSessionLock() {
      try {
         File var1 = new File(this.worldDirectory, "session.lock");
         DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

         try {
            var2.writeLong(this.initializationTime);
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         var7.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   public File getWorldDirectory() {
      return this.worldDirectory;
   }

   public void checkSessionLock() throws MinecraftException {
      try {
         File var1 = new File(this.worldDirectory, "session.lock");
         DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

         try {
            if (var2.readLong() != this.initializationTime) {
               Minecraft.setErrorMessage("checkSessionLock: aborting world save due to session lock");
               throw new MinecraftException("The save is being accessed from another location, aborting");
            }
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         throw new MinecraftException("Failed to check session lock, aborting");
      }
   }

   public IChunkLoader getChunkLoader(WorldProvider par1WorldProvider) {
      throw new RuntimeException("Old Chunk Storage is no longer supported.");
   }

   public WorldInfo loadWorldInfo() {
      File var1 = new File(this.worldDirectory, "level.dat");
      NBTTagCompound var2;
      NBTTagCompound var3;
      Exception var4;
      WorldInfo worldInfo = null;
      if (var1.exists()) {
         try {
            var2 = CompressedStreamTools.readCompressed(new FileInputStream(var1));
            var3 = var2.getCompoundTag("Data");
            worldInfo = new WorldInfo(var3);
            FMLCommonHandler.instance().handleWorldDataLoad(this, worldInfo, var2);
            return worldInfo;
         } catch (Exception var6) {
            if (FMLCommonHandler.instance().shouldServerBeKilledQuietly())
            {
               throw (RuntimeException)var6;
            }
            NBTBase.loading_world_info = false;
            var6.printStackTrace();
         }
      }

      var1 = new File(this.worldDirectory, "level.dat_old");
      if (var1.exists()) {
         try {
            var2 = CompressedStreamTools.readCompressed(new FileInputStream(var1));
            var3 = var2.getCompoundTag("Data");
            worldInfo = new WorldInfo(var3);
            FMLCommonHandler.instance().handleWorldDataLoad(this, worldInfo, var2);
            return worldInfo;
         } catch (Exception var5) {
            var4 = var5;
            NBTBase.loading_world_info = false;
            var4.printStackTrace();
         }
      }

      return null;
   }

   public void saveWorldInfoWithPlayer(WorldInfo par1WorldInfo, NBTTagCompound par2NBTTagCompound) {
      NBTTagCompound var3 = par1WorldInfo.cloneNBTCompound(par2NBTTagCompound);
      NBTTagCompound var4 = new NBTTagCompound();
      var4.setTag("Data", var3);
      FMLCommonHandler.instance().handleWorldDataSave(this, par1WorldInfo, var4);
      try {
         File var5 = new File(this.worldDirectory, "level.dat_new");
         File var6 = new File(this.worldDirectory, "level.dat_old");
         File var7 = new File(this.worldDirectory, "level.dat");
         CompressedStreamTools.writeCompressed(var4, new FileOutputStream(var5));
         if (var6.exists()) {
            var6.delete();
         }

         var7.renameTo(var6);
         if (var7.exists()) {
            var7.delete();
         }

         var5.renameTo(var7);
         if (var5.exists()) {
            var5.delete();
         }

         File achievements = new File(this.worldDirectory, "achievements.txt");
         FileWriter fw = new FileWriter(achievements);
         StringBuffer sb = new StringBuffer();
         sb.append("World Achievements" + StringHelper.newline);
         sb.append("------------------" + StringHelper.newline);
         Iterator i = par1WorldInfo.getAchievements().entrySet().iterator();

         while(i.hasNext()) {
            Map.Entry entry = (Map.Entry)i.next();
            WorldAchievement wa = (WorldAchievement)entry.getValue();
            sb.append(wa.achievement);
            sb.append(" taken by ");
            sb.append(wa.username);
            sb.append(" on Day ");
            sb.append(wa.day);
            sb.append(StringHelper.newline);
         }

         fw.write(sb.toString());
         fw.close();
      } catch (Exception var14) {
         Exception var8 = var14;
         var8.printStackTrace();
      }

   }

   public void saveWorldInfo(WorldInfo par1WorldInfo) {
      NBTTagCompound var2 = par1WorldInfo.getNBTTagCompound();
      NBTTagCompound var3 = new NBTTagCompound();
      var3.setTag("Data", var2);
      FMLCommonHandler.instance().handleWorldDataSave(this, par1WorldInfo, var3);
      try {
         File var4 = new File(this.worldDirectory, "level.dat_new");
         File var5 = new File(this.worldDirectory, "level.dat_old");
         File var6 = new File(this.worldDirectory, "level.dat");
         CompressedStreamTools.writeCompressed(var3, new FileOutputStream(var4));
         if (var5.exists()) {
            var5.delete();
         }

         var6.renameTo(var5);
         if (var6.exists()) {
            var6.delete();
         }

         var4.renameTo(var6);
         if (var4.exists()) {
            var4.delete();
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void writePlayerData(EntityPlayer par1EntityPlayer) {
      try {
         NBTTagCompound var2 = new NBTTagCompound();
         par1EntityPlayer.writeToNBT(var2);
         File var3 = new File(this.playersDirectory, par1EntityPlayer.getCommandSenderName() + ".dat.tmp");
         File var4 = new File(this.playersDirectory, par1EntityPlayer.getCommandSenderName() + ".dat");
         CompressedStreamTools.writeCompressed(var2, new FileOutputStream(var3));
         if (var4.exists()) {
            var4.delete();
         }

         var3.renameTo(var4);
      } catch (Exception var5) {
         MinecraftServer.getServer().getLogAgent().logWarning("Failed to save player data for " + par1EntityPlayer.getCommandSenderName());
      }

   }

   public NBTTagCompound readPlayerData(EntityPlayer par1EntityPlayer) {
      NBTTagCompound var2 = this.getPlayerData(par1EntityPlayer.getCommandSenderName());
      if (var2 != null) {
         par1EntityPlayer.readFromNBT(var2);
      }

      return var2;
   }

   public NBTTagCompound getPlayerData(String par1Str) {
      try {
         File var2 = new File(this.playersDirectory, par1Str + ".dat");
         if (var2.exists()) {
            return CompressedStreamTools.readCompressed(new FileInputStream(var2));
         }
      } catch (Exception var3) {
         MinecraftServer.getServer().getLogAgent().logWarning("Failed to load player data for " + par1Str);
      }

      return null;
   }

   public IPlayerFileData getSaveHandler() {
      return this;
   }

   public String[] getAvailablePlayerDat() {
      String[] var1 = this.playersDirectory.list();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].endsWith(".dat")) {
            var1[var2] = var1[var2].substring(0, var1[var2].length() - 4);
         }
      }

      return var1;
   }

   public void flush() {
   }

   public File getMapFileFromName(String par1Str) {
      return new File(this.mapDataDir, par1Str + ".dat");
   }

   public String getWorldDirectoryName() {
      return this.saveDirectoryName;
   }
}
