package net.minecraft.world.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;

public class SaveFormatOld implements ISaveFormat {
   protected final File savesDirectory;

   public SaveFormatOld(File par1File) {
      if (!par1File.exists()) {
         par1File.mkdirs();
      }

      this.savesDirectory = par1File;
   }

   public List getSaveList() throws AnvilConverterException {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < 5; ++var2) {
         String var3 = "World" + (var2 + 1);
         WorldInfo var4 = this.getWorldInfo(var3);
         if (var4 != null) {
            var1.add(new SaveFormatComparator(var3, "", var4.getLastTimePlayed(), var4.getSizeOnDisk(), var4.getGameType(), false, var4.isHardcoreModeEnabled(), var4.areCommandsAllowed(), var4.areSkillsEnabled(), var4.isValidMITEWorld(), var4.getIsNotValidReason()));
         }
      }

      return var1;
   }

   public void flushCache() {
   }

   public WorldInfo getWorldInfo(String par1Str) {
      File var2 = new File(this.savesDirectory, par1Str);
      if (!var2.exists()) {
         return null;
      } else {
         File var3 = new File(var2, "level.dat");
         NBTTagCompound var4;
         NBTTagCompound var5;
         if (var3.exists()) {
            try {
               var4 = CompressedStreamTools.readCompressed(new FileInputStream(var3));
               var5 = var4.getCompoundTag("Data");
               return new WorldInfo(var5);
            } catch (Exception var7) {
               NBTBase.loading_world_info = false;
               return null;
            }
         } else {
            var3 = new File(var2, "level.dat_old");
            if (var3.exists()) {
               try {
                  var4 = CompressedStreamTools.readCompressed(new FileInputStream(var3));
                  var5 = var4.getCompoundTag("Data");
                  return new WorldInfo(var5);
               } catch (Exception var8) {
                  NBTBase.loading_world_info = false;
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }

   public void renameWorld(String par1Str, String par2Str) {
      File var3 = new File(this.savesDirectory, par1Str);
      if (var3.exists()) {
         File var4 = new File(var3, "level.dat");
         if (var4.exists()) {
            try {
               NBTTagCompound var5 = CompressedStreamTools.readCompressed(new FileInputStream(var4));
               NBTTagCompound var6 = var5.getCompoundTag("Data");
               var6.setString("LevelName", par2Str);
               CompressedStreamTools.writeCompressed(var5, new FileOutputStream(var4));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }
      }

   }

   public boolean deleteWorldDirectory(String par1Str) {
      File var2 = new File(this.savesDirectory, par1Str);
      if (!var2.exists()) {
         return true;
      } else {
         System.out.println("Deleting level " + par1Str);

         for(int var3 = 1; var3 <= 5; ++var3) {
            System.out.println("Attempt " + var3 + "...");
            if (deleteFiles(var2.listFiles())) {
               break;
            }

            System.out.println("Unsuccessful in deleting contents.");
            if (var3 < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return var2.delete();
      }
   }

   protected static boolean deleteFiles(File[] par0ArrayOfFile) {
      for(int var1 = 0; var1 < par0ArrayOfFile.length; ++var1) {
         File var2 = par0ArrayOfFile[var1];
         System.out.println("Deleting " + var2);
         if (var2.isDirectory() && !deleteFiles(var2.listFiles())) {
            System.out.println("Couldn't delete directory " + var2);
            return false;
         }

         if (!var2.delete()) {
            System.out.println("Couldn't delete file " + var2);
            return false;
         }
      }

      return true;
   }

   public ISaveHandler getSaveLoader(String par1Str, boolean par2) {
      return new SaveHandler(this.savesDirectory, par1Str, par2);
   }

   public boolean isOldMapFormat(String par1Str) {
      return false;
   }

   public boolean convertMapFormat(String par1Str, IProgressUpdate par2IProgressUpdate) {
      return false;
   }

   public boolean canLoadWorld(String par1Str) {
      File var2 = new File(this.savesDirectory, par1Str);
      return var2.isDirectory();
   }
}
