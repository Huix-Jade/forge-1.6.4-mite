package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

public class RegionFileCache {
   private static final Map regionsByFilename = new HashMap();
   public static RegionFileChunkBuffer chunk_buffer_to_close;
   public static DeflaterOutputStream deflater_output_stream_to_close;

   public static synchronized RegionFile createOrLoadRegionFile(File par0File, int par1, int par2) {
      File var3 = new File(par0File, "region");
      File var4 = new File(var3, "r." + (par1 >> 5) + "." + (par2 >> 5) + ".mca");
      RegionFile var5 = (RegionFile)regionsByFilename.get(var4);
      if (var5 != null) {
         return var5;
      } else {
         if (!var3.exists()) {
            var3.mkdirs();
         }

         if (regionsByFilename.size() >= 256) {
            clearRegionFileReferences();
         }

         RegionFile var6 = new RegionFile(var4);
         regionsByFilename.put(var4, var6);
         return var6;
      }
   }

   public static synchronized void clearRegionFileReferences() {
      Iterator var0 = regionsByFilename.values().iterator();

      while(var0.hasNext()) {
         RegionFile var1 = (RegionFile)var0.next();

         try {
            if (var1 != null) {
               var1.close();
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      regionsByFilename.clear();
   }

   public static DataInputStream getChunkInputStream(File par0File, int par1, int par2) {
      RegionFile var3 = createOrLoadRegionFile(par0File, par1, par2);
      return var3.getChunkDataInputStream(par1 & 31, par2 & 31);
   }

   public static DataOutputStream getChunkOutputStream(File par0File, int par1, int par2) {
      RegionFile var3 = createOrLoadRegionFile(par0File, par1, par2);
      DataOutputStream data_output_stream = var3.getChunkDataOutputStream(par1 & 31, par2 & 31);
      chunk_buffer_to_close = var3.chunk_buffer_to_close;
      deflater_output_stream_to_close = var3.deflater_output_stream_to_close;
      return data_output_stream;
   }
}
