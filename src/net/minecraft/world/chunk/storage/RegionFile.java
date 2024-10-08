package net.minecraft.world.chunk.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Debug;

public class RegionFile {
   private static final byte[] emptySector = new byte[4096];
   private final File fileName;
   private RandomAccessFile dataFile;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private ArrayList sectorFree;
   private int sizeDelta;
   private long lastModified;
   public RegionFileChunkBuffer chunk_buffer_to_close;
   public DeflaterOutputStream deflater_output_stream_to_close;
   private boolean is_data_file_closed;
   public Thread thread_that_closed_the_data_file;
   public int times_the_data_file_was_closed;

   public RegionFile(File par1File) {
      this.fileName = par1File;
      this.sizeDelta = 0;

      try {
         if (par1File.exists()) {
            this.lastModified = par1File.lastModified();
         }

         this.dataFile = new RandomAccessFile(par1File, "rw");
         int var2;
         if (this.dataFile.length() < 4096L) {
            for(var2 = 0; var2 < 1024; ++var2) {
               this.dataFile.writeInt(0);
            }

            for(var2 = 0; var2 < 1024; ++var2) {
               this.dataFile.writeInt(0);
            }

            this.sizeDelta += 8192;
         }

         if ((this.dataFile.length() & 4095L) != 0L) {
            for(var2 = 0; (long)var2 < (this.dataFile.length() & 4095L); ++var2) {
               this.dataFile.write(0);
            }
         }

         var2 = (int)this.dataFile.length() / 4096;
         this.sectorFree = new ArrayList(var2);

         int var3;
         for(var3 = 0; var3 < var2; ++var3) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0L);

         int var4;
         for(var3 = 0; var3 < 1024; ++var3) {
            var4 = this.dataFile.readInt();
            this.offsets[var3] = var4;
            if (var4 != 0 && (var4 >> 8) + (var4 & 255) <= this.sectorFree.size()) {
               for(int var5 = 0; var5 < (var4 & 255); ++var5) {
                  this.sectorFree.set((var4 >> 8) + var5, false);
               }
            }
         }

         for(var3 = 0; var3 < 1024; ++var3) {
            var4 = this.dataFile.readInt();
            this.chunkTimestamps[var3] = var4;
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public synchronized DataInputStream getChunkDataInputStream(int par1, int par2) {
      if (this.outOfBounds(par1, par2)) {
         return null;
      } else {
         try {
            int var3 = this.getOffset(par1, par2);
            if (var3 == 0) {
               return null;
            } else {
               int var4 = var3 >> 8;
               int var5 = var3 & 255;
               if (var4 + var5 > this.sectorFree.size()) {
                  return null;
               } else {
                  this.dataFile.seek((long)(var4 * 4096));
                  int var6 = this.dataFile.readInt();
                  if (var6 > 4096 * var5) {
                     return null;
                  } else if (var6 <= 0) {
                     return null;
                  } else {
                     byte var7 = this.dataFile.readByte();
                     byte[] var8;
                     if (var7 == 1) {
                        var8 = new byte[var6 - 1];
                        this.dataFile.read(var8);
                        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var8))));
                     } else if (var7 == 2) {
                        var8 = new byte[var6 - 1];
                        this.dataFile.read(var8);
                        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(var8))));
                     } else {
                        return null;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }
   }

   public DataOutputStream getChunkDataOutputStream(int par1, int par2) {
      this.chunk_buffer_to_close = new RegionFileChunkBuffer(this, par1, par2);
      this.deflater_output_stream_to_close = new DeflaterOutputStream(this.chunk_buffer_to_close);
      return this.outOfBounds(par1, par2) ? null : new DataOutputStream(this.deflater_output_stream_to_close);
   }

   protected synchronized void write(int par1, int par2, byte[] par3ArrayOfByte, int par4) {
      try {
         int var5 = this.getOffset(par1, par2);
         int var6 = var5 >> 8;
         int var7 = var5 & 255;
         int var8 = (par4 + 5) / 4096 + 1;
         if (var8 >= 256) {
            return;
         }

         if (var6 != 0 && var7 == var8) {
            this.write(var6, par3ArrayOfByte, par4);
         } else {
            int var9;
            for(var9 = 0; var9 < var7; ++var9) {
               this.sectorFree.set(var6 + var9, true);
            }

            var9 = this.sectorFree.indexOf(true);
            int var10 = 0;
            int var11;
            if (var9 != -1) {
               for(var11 = var9; var11 < this.sectorFree.size(); ++var11) {
                  if (var10 != 0) {
                     if ((Boolean)this.sectorFree.get(var11)) {
                        ++var10;
                     } else {
                        var10 = 0;
                     }
                  } else if ((Boolean)this.sectorFree.get(var11)) {
                     var9 = var11;
                     var10 = 1;
                  }

                  if (var10 >= var8) {
                     break;
                  }
               }
            }

            if (var10 >= var8) {
               var6 = var9;
               this.setOffset(par1, par2, var9 << 8 | var8);

               for(var11 = 0; var11 < var8; ++var11) {
                  this.sectorFree.set(var6 + var11, false);
               }

               this.write(var6, par3ArrayOfByte, par4);
            } else {
               this.dataFile.seek(this.dataFile.length());
               var6 = this.sectorFree.size();

               for(var11 = 0; var11 < var8; ++var11) {
                  this.dataFile.write(emptySector);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * var8;
               this.write(var6, par3ArrayOfByte, par4);
               this.setOffset(par1, par2, var6 << 8 | var8);
            }
         }

         this.setChunkTimestamp(par1, par2, (int)(MinecraftServer.getSystemTimeMillis() / 1000L));
      } catch (IOException var12) {
         var12.printStackTrace();
         Debug.println("Information: Thread that closed the file: " + this.thread_that_closed_the_data_file + ", thread that tried to write to data file: " + Thread.currentThread() + ", times the files was closed: " + this.times_the_data_file_was_closed);
      }

   }

   private void write(int par1, byte[] par2ArrayOfByte, int par3) throws IOException {
      this.dataFile.seek((long)(par1 * 4096));
      this.dataFile.writeInt(par3 + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(par2ArrayOfByte, 0, par3);
   }

   private boolean outOfBounds(int par1, int par2) {
      return par1 < 0 || par1 >= 32 || par2 < 0 || par2 >= 32;
   }

   private int getOffset(int par1, int par2) {
      return this.offsets[par1 + par2 * 32];
   }

   public boolean isChunkSaved(int par1, int par2) {
      return this.getOffset(par1, par2) != 0;
   }

   private void setOffset(int par1, int par2, int par3) throws IOException {
      this.offsets[par1 + par2 * 32] = par3;
      this.dataFile.seek((long)((par1 + par2 * 32) * 4));
      this.dataFile.writeInt(par3);
   }

   private void setChunkTimestamp(int par1, int par2, int par3) throws IOException {
      this.chunkTimestamps[par1 + par2 * 32] = par3;
      this.dataFile.seek((long)(4096 + (par1 + par2 * 32) * 4));
      this.dataFile.writeInt(par3);
   }

   public void close() throws IOException {
      if (this.dataFile != null) {
         this.dataFile.close();
         this.is_data_file_closed = true;
         this.thread_that_closed_the_data_file = Thread.currentThread();
         ++this.times_the_data_file_was_closed;
      }

   }
}
