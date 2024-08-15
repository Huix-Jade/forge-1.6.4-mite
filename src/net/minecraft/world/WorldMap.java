package net.minecraft.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.logging.ILogAgent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Debug;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;

public class WorldMap {
   private WorldServer world;
   private ILogAgent log_agent;
   public int size;
   public int size_divided_by_2;
   public int size_squared;
   public final int bytes_per_pixel = 4;
   public int bytes_in_map;
   protected ByteBuffer data;
   private byte[] copy_of_data;
   private String progressive_filename;
   private RandomAccessFile progressive_file;
   private final int ticks_between_progressive_writes = 1200;
   private int progressive_write_counter = -1200;
   private int bytes_written_to_temporary_file;
   private final int bytes_to_write_to_temporary_file_per_tick = 1024;

   public WorldMap(WorldServer world) {
      this.world = world;
      this.log_agent = MinecraftServer.getServer().getLogAgent();

      try {
         this.readFromFile();
      } catch (IOException var3) {
         this.size = MinecraftServer.getServer().default_world_map_size;
         this.size_divided_by_2 = this.size / 2;
         this.size_squared = this.size * this.size;
         this.bytes_in_map = this.size_squared * 4;
         this.data = ByteBuffer.allocateDirect(this.bytes_in_map);
         this.clearBiomeData();
      }

   }

   private static String getFilename(World world) {
      String dimension_name;
      if (world.provider.dimensionId == -2) {
         dimension_name = "underworld";
      } else if (world.provider.dimensionId == -1) {
         dimension_name = "nether";
      } else if (world.provider.dimensionId == 0) {
         dimension_name = "overworld";
      } else {
         dimension_name = "unknown-dimension";
      }

      return world.worldInfo.getWorldName().replaceAll(" ", "-").toLowerCase() + "-" + dimension_name + ".map";
   }

   private void clearBiomeData() {
      for(int i = 3; i < this.bytes_in_map; i += 4) {
         this.data.put(i, (byte)63);
      }

   }

   public void markPixelDirty(int x, int z) {
      if (this.isXZOnMap(x, z)) {
         int data_offset = this.getDataOffsetForPixel(x, z) + 3;
         this.data.put(data_offset, (byte)(this.data.get(data_offset) & 127));
      }

   }

   private int getPixelIndex(int x, int z) {
      return x + this.size_divided_by_2 + (z + this.size_divided_by_2) * this.size;
   }

   private int getDataOffsetForPixel(int x, int z) {
      return (x + this.size_divided_by_2 + (z + this.size_divided_by_2) * this.size) * 4;
   }

   private int getMinMapXZ() {
      return -this.size_divided_by_2;
   }

   private int getMaxMapXZ() {
      return this.size_divided_by_2 - 1;
   }

   private boolean isXZOnMap(int x_or_z) {
      return x_or_z >= -this.size_divided_by_2 && x_or_z < this.size_divided_by_2;
   }

   private boolean isXZOnMap(int x, int z) {
      return x >= -this.size_divided_by_2 && x < this.size_divided_by_2 && z >= -this.size_divided_by_2 && z < this.size_divided_by_2;
   }

   private void readFromFile() throws IOException {
      FileInputStream fis = new FileInputStream(new File(getFilename(this.world)));
      FileChannel channel = fis.getChannel();
      this.bytes_in_map = (int)channel.size();
      this.size_squared = this.bytes_in_map / 4;
      this.size = (int)MathHelper.sqrt_double((double)this.size_squared);
      this.size_divided_by_2 = this.size / 2;
      if (this.data != null && this.bytes_in_map <= this.data.capacity()) {
         this.data.clear();
      } else {
         this.data = ByteBuffer.allocateDirect(this.bytes_in_map);
      }

      channel.read(this.data);
      this.data.flip();
      fis.close();
      this.log_agent.logInfo("Map loaded, size is " + this.size + "x" + this.size + " (" + this.bytes_in_map / 1024 / 1024 + "MB of memory)");
   }

   private void writeMapColorsToFile() {
      try {
         FileWriter fw = new FileWriter("map_colors.txt");
         StringBuffer sb = new StringBuffer();

         for(int i = 0; i < 256; ++i) {
            Block block = Block.blocksList[i];
            if (i > 0) {
               sb.append('\n');
            }

            if (block != null && block.blockMaterial != null && block.blockMaterial.map_color != null) {
               MapColor map_color = block.blockMaterial.map_color;
               sb.append(i);
               sb.append(",");
               sb.append(map_color.colorValue >> 16 & 255);
               sb.append(",");
               sb.append(map_color.colorValue >> 8 & 255);
               sb.append(",");
               sb.append(map_color.colorValue & 255);
            } else {
               sb.append(i + ",0,0,0");
            }
         }

         fw.write(sb.toString());
         fw.close();
      } catch (Exception var9) {
      }

   }

   private void updateEmbeddedData() {
      int length_of_data_to_embed = 0;
      byte[] data_to_embed = new byte[64];
      int day_of_world = this.world.getDayOfWorld();
      int tick_of_day = this.world.getAdjustedTimeOfDay();
      data_to_embed[length_of_data_to_embed++] = (byte)(day_of_world & 255);
      data_to_embed[length_of_data_to_embed++] = (byte)(day_of_world >> 8 & 255);
      data_to_embed[length_of_data_to_embed++] = (byte)(day_of_world >> 16 & 255);
      data_to_embed[length_of_data_to_embed++] = (byte)(day_of_world >> 24 & 255);
      data_to_embed[length_of_data_to_embed++] = (byte)(tick_of_day & 255);
      data_to_embed[length_of_data_to_embed++] = (byte)(tick_of_day >> 8 & 255);
      int index_of_next_byte_to_embed = -1;
      int offset_of_last_hosting_pixel = -4;

      while(true) {
         ++index_of_next_byte_to_embed;
         if (index_of_next_byte_to_embed == length_of_data_to_embed) {
            return;
         }

         byte block_id;
         do {
            offset_of_last_hosting_pixel += 4;
            if (offset_of_last_hosting_pixel >= this.bytes_in_map) {
               return;
            }

            block_id = this.data.get(offset_of_last_hosting_pixel);
         } while(block_id != 0 && block_id != Block.sand.blockID);

         this.data.put(offset_of_last_hosting_pixel + 1, data_to_embed[index_of_next_byte_to_embed]);
      }
   }

   public void writeToFile() {
      if (!this.writeToFileProgressively(true)) {
         try {
            this.updateEmbeddedData();
            File temporary = new File(getFilename(this.world) + ".tmp");
            FileOutputStream fos = new FileOutputStream(temporary);
            FileChannel fc = fos.getChannel();
            this.data.rewind();
            fc.write(this.data);
            fos.flush();
            fos.close();
            File file = new File(getFilename(this.world));
            if (file.exists() && !file.delete()) {
               this.log_agent.logWarning("writeToFile: Wasn't able to delete " + file.getName());
            }

            if (!temporary.renameTo(file)) {
               this.log_agent.logWarning("writeToFile: Wasn't able to rename " + temporary.getName() + " to " + file.getName());
            }
         } catch (IOException var5) {
            this.log_agent.logWarning("writeToFile: Exception occured while trying to write world map to file");
         }

         this.writeMapColorsToFile();
      }

   }

   private void resetProgressiveFile() {
      this.progressive_filename = null;
      if (this.progressive_file != null) {
         try {
            this.progressive_file.close();
         } catch (IOException var2) {
         }

         this.progressive_file = null;
      }

      this.progressive_write_counter = -1200;
   }

   public boolean writeToFileProgressively(boolean flush) {
      ++this.progressive_write_counter;
      if (this.progressive_write_counter == 0) {
         if (this.copy_of_data == null || this.copy_of_data.length != this.data.capacity()) {
            this.copy_of_data = new byte[this.data.capacity()];
         }
      } else if (this.progressive_write_counter == 1) {
         this.updateEmbeddedData();
         this.data.rewind();
         this.data.get(this.copy_of_data);
         if (this.progressive_file != null) {
            try {
               this.progressive_file.close();
            } catch (IOException var6) {
            }
         }

         try {
            this.progressive_filename = getFilename(this.world) + ".unfinished";
            this.progressive_file = new RandomAccessFile(this.progressive_filename, "rw");
            this.progressive_file.setLength(0L);
            this.bytes_written_to_temporary_file = 0;
         } catch (IOException var5) {
            this.log_agent.logWarning("writeToFileProgressively: Exception occured while trying to write world map to file (Stage 1)");
            this.resetProgressiveFile();
         }
      } else if (this.progressive_write_counter == 2 && this.bytes_written_to_temporary_file < this.bytes_in_map) {
         --this.progressive_write_counter;

         try {
            int bytes_to_write = flush ? this.bytes_in_map - this.bytes_written_to_temporary_file : Math.min(1024, this.bytes_in_map - this.bytes_written_to_temporary_file);
            this.progressive_file.write(this.copy_of_data, this.bytes_written_to_temporary_file, bytes_to_write);
            this.bytes_written_to_temporary_file += bytes_to_write;
         } catch (IOException var4) {
            this.resetProgressiveFile();
         }

         if (flush) {
            this.writeToFileProgressively(false);
         }
      } else if (this.progressive_write_counter == 2) {
         try {
            this.progressive_file.close();
            File src = new File(this.progressive_filename);
            File dest = new File(getFilename(this.world));
            if (dest.exists() && !dest.delete()) {
               this.log_agent.logWarning("writeToFileProgressively: Wasn't able to delete " + dest.getName());
               this.resetProgressiveFile();
               return false;
            }

            if (!src.renameTo(dest)) {
               this.log_agent.logWarning("writeToFileProgressively: Wasn't able to rename " + src.getName() + " to " + dest.getName());
               this.resetProgressiveFile();
               return false;
            }

            this.writeMapColorsToFile();
         } catch (IOException var7) {
            this.log_agent.logWarning("writeToFileProgressively: Exception occured while trying to write world map to file (Stage 3)");
            this.resetProgressiveFile();
            return false;
         }

         this.resetProgressiveFile();
         return true;
      }

      return false;
   }

   public int addSurvey(WorldServer world, int center_x, int center_z, int radius, boolean done_by_map) {
      int minus_radius = -radius;
      int radius_times_2 = radius * 2;
      int radius_sq = radius * radius;
      int radius_minus_2 = radius - 2;
      int radius_minus_2_sq = radius_minus_2 * radius_minus_2;
      int min_XZ = this.getMinMapXZ();
      int max_XZ = this.getMaxMapXZ();
      int blocks_surveyed = 0;

      for(int dx = minus_radius; dx <= radius; ++dx) {
         int x = center_x + dx;
         if (x >= min_XZ) {
            if (x > max_XZ) {
               break;
            }

            for(int dz = minus_radius; dz <= radius; ++dz) {
               int z = center_z + dz;
               if (z >= min_XZ) {
                  if (z > max_XZ) {
                     break;
                  }

                  int distance_sq = dx * dx + dz * dz;
                  if (done_by_map || distance_sq < radius_sq && (distance_sq < radius_minus_2_sq || (x + z) % 2 != 0)) {
                     int data_offset = this.getDataOffsetForPixel(x, z);
                     byte biome_byte = this.data.get(data_offset + 3);
                     boolean has_been_surveyed_by_map = (biome_byte & 64) != 0;
                     if ((biome_byte & 128) == 0 || done_by_map && !has_been_surveyed_by_map) {
                        Chunk chunk = world.getChunkFromBlockCoords(x, z);
                        if (!chunk.isEmpty()) {
                           int x_in_chunk = x & 15;
                           int z_in_chunk = z & 15;
                           int y = chunk.getHeightValue(x_in_chunk, z_in_chunk) + 1 - 2;
                           int height_on_map = y;
                           int block_id = chunk.getBlockID(x_in_chunk, y, z_in_chunk);

                           int block_id_above;
                           while((block_id_above = chunk.getBlockID(x_in_chunk, y + 1, z_in_chunk)) != 0) {
                              ++y;
                              Block block_above = Block.blocksList[block_id_above];
                              if (block_above.blockMaterial.map_color != MapColor.airColor) {
                                 height_on_map = y;
                                 block_id = block_id_above;
                              }
                           }

                           Block block = Block.blocksList[block_id];
                           int depth;
                           if (block != null && block.blockMaterial.isLiquid()) {
                              depth = 1;
                              int y_below = y;

                              while(true) {
                                 --y_below;
                                 if (y_below < 0) {
                                    break;
                                 }

                                 int block_id_below = chunk.getBlockID(x_in_chunk, y_below, z_in_chunk);
                                 Block block_below = Block.blocksList[block_id_below];
                                 if (block_below == null || !block_below.blockMaterial.isLiquid()) {
                                    break;
                                 }

                                 ++depth;
                              }
                           } else {
                              depth = 0;
                           }

                           this.data.put(data_offset, (byte)block_id);
                           this.data.put(data_offset + 1, (byte)(world.getBlockMetadata(x, y, z) | depth << 4));
                           this.data.put(data_offset + 2, (byte)height_on_map);
                           if ((biome_byte & 63) == 63) {
                              biome_byte = (byte)world.getBiomeGenForCoords(x, z).biomeID;
                           }

                           biome_byte = (byte)(biome_byte | 128);
                           if (has_been_surveyed_by_map || done_by_map) {
                              biome_byte = (byte)(biome_byte | 64);
                           }

                           this.data.put(data_offset + 3, biome_byte);
                           ++blocks_surveyed;
                           if (blocks_surveyed >= 1024) {
                              return blocks_surveyed;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return blocks_surveyed;
   }

   public void XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXaddSurvey(WorldServer world, int center_x, int center_z, int radius) {
      if (world.provider.dimensionId == 0) {
         byte day_of_world_mod_128 = (byte)(world.getDayOfWorld() % 128);
         int mapping_radius = world.provider.hasNoSky ? radius / 2 : radius;
         int mapping_radius_times_2 = mapping_radius * 2;
         int mapping_radius_sq = mapping_radius * mapping_radius;
         int[] var24 = new int[256];
         int min_XZ = this.getMinMapXZ();
         int max_XZ = this.getMaxMapXZ();

         for(int raster_x = 1; raster_x < mapping_radius_times_2; ++raster_x) {
            double var16 = 0.0;
            int dx = raster_x - mapping_radius;
            int x = center_x + dx;
            if (x >= min_XZ) {
               if (x > max_XZ) {
                  break;
               }

               int smallest_raster_z_that_is_due_for_update = mapping_radius_times_2;

               int first_raster_z;
               int largest_raster_z_that_is_due_for_update;
               for(first_raster_z = 1; first_raster_z < mapping_radius_times_2; ++first_raster_z) {
                  largest_raster_z_that_is_due_for_update = center_z + first_raster_z - mapping_radius;
                  if (largest_raster_z_that_is_due_for_update >= min_XZ && largest_raster_z_that_is_due_for_update <= max_XZ && (this.data.get(this.getDataOffsetForPixel(x, largest_raster_z_that_is_due_for_update) + 3) & 128) == 0) {
                     smallest_raster_z_that_is_due_for_update = first_raster_z;
                     break;
                  }
               }

               if (smallest_raster_z_that_is_due_for_update != mapping_radius_times_2) {
                  first_raster_z = smallest_raster_z_that_is_due_for_update - 1;
                  largest_raster_z_that_is_due_for_update = -1;

                  int last_raster_z;
                  int raster_z;
                  for(last_raster_z = mapping_radius_times_2 - 1; last_raster_z >= 0; --last_raster_z) {
                     raster_z = center_z + last_raster_z - mapping_radius;
                     if (raster_z >= min_XZ && raster_z <= max_XZ && (this.data.get(this.getDataOffsetForPixel(x, raster_z) + 3) & 128) == 0) {
                        largest_raster_z_that_is_due_for_update = last_raster_z;
                        break;
                     }
                  }

                  if (largest_raster_z_that_is_due_for_update != -1) {
                     last_raster_z = largest_raster_z_that_is_due_for_update;

                     for(raster_z = first_raster_z; raster_z <= last_raster_z; ++raster_z) {
                        int dz = raster_z - mapping_radius;
                        int z = center_z + dz;
                        if (z >= min_XZ - 1) {
                           if (z > max_XZ) {
                              break;
                           }

                           int dx_sq = dx * dx;
                           int dz_sq = dz * dz;
                           boolean at_edge_of_survey = dx_sq + dz_sq > (mapping_radius - 2) * (mapping_radius - 2);
                           int x_plus_z_and_1 = x + z & 1;
                           if (dz <= 0 || dx_sq + dz_sq < mapping_radius_sq) {
                              for(int i = 0; i < var24.length; ++i) {
                                 var24[i] = 0;
                              }

                              Chunk chunk = world.getChunkFromBlockCoords(x, z);
                              if (!chunk.isEmpty()) {
                                 int depth = -1;
                                 int height = -1;
                                 int x_in_chunk = x & 15;
                                 int z_in_chunk = z & 15;
                                 int var28 = 0;
                                 double var29 = 0.0;
                                 int var31;
                                 int offset_for_pixel;
                                 if (world.provider.hasNoSky) {
                                    var31 = x + z * 231871;
                                    var31 = var31 * var31 * 31287121 + var31 * 11;
                                    int var10001;
                                    if ((var31 >> 20 & 1) == 0) {
                                       var10001 = Block.dirt.blockID;
                                       var24[var10001] += 10;
                                    } else {
                                       var10001 = Block.stone.blockID;
                                       var24[var10001] += 10;
                                    }

                                    var29 = 100.0;
                                 } else {
                                    int i = 0;
                                    int var32 = 0;
                                    int var33 = chunk.getHeightValue(i + x_in_chunk, var32 + z_in_chunk) + 1;
                                    height = var33 - 1;
                                    offset_for_pixel = 0;
                                    if (var33 > 1) {
                                       boolean var35;
                                       do {
                                          var35 = true;
                                          offset_for_pixel = chunk.getBlockID(i + x_in_chunk, var33 - 1, var32 + z_in_chunk);
                                          if (offset_for_pixel == 0) {
                                             var35 = false;
                                          } else if (var33 > 0 && offset_for_pixel > 0 && Block.blocksList[offset_for_pixel].blockMaterial.map_color == MapColor.airColor) {
                                             var35 = false;
                                          }

                                          if (!var35) {
                                             --var33;
                                             if (var33 <= 0) {
                                                break;
                                             }

                                             offset_for_pixel = chunk.getBlockID(i + x_in_chunk, var33 - 1, var32 + z_in_chunk);
                                             height = var33 - 1;
                                          }
                                       } while(var33 > 0 && !var35);

                                       if (var33 > 0 && offset_for_pixel != 0 && Block.blocksList[offset_for_pixel].blockMaterial.isLiquid()) {
                                          int var36 = var33 - 1;
                                          boolean var37 = false;

                                          int var43;
                                          do {
                                             var43 = chunk.getBlockID(i + x_in_chunk, var36--, var32 + z_in_chunk);
                                             ++var28;
                                          } while(var36 > 0 && var43 != 0 && Block.blocksList[var43].blockMaterial.isLiquid());

                                          depth = var28 - 1;
                                          if (depth > 15) {
                                             depth = 15;
                                          }
                                       } else {
                                          depth = 0;
                                       }
                                    }

                                    var29 += (double)var33;
                                    int var10002 = var24[offset_for_pixel]++;
                                 }

                                 var31 = 0;
                                 int block_id = -1;

                                 for(offset_for_pixel = 0; offset_for_pixel < 256; ++offset_for_pixel) {
                                    if (var24[offset_for_pixel] > var31) {
                                       block_id = offset_for_pixel;
                                       var31 = var24[offset_for_pixel];
                                    }
                                 }

                                 if (block_id == 0) {
                                    depth = 0;
                                    height = 0;
                                 } else if (block_id > 0) {
                                    boolean var10000;
                                    double var40;
                                    if (Block.blocksList[block_id].blockMaterial.map_color == MapColor.waterColor) {
                                       var40 = (double)var28 * 0.1 + (double)x_plus_z_and_1 * 0.2;
                                       if (var40 > 0.9) {
                                          var10000 = false;
                                       } else {
                                          var10000 = var40 < 0.5 ? true : true;
                                       }
                                    } else {
                                       var40 = (var29 - var16) * 4.0 / 5.0 + ((double)x_plus_z_and_1 - 0.5) * 0.4;
                                       if (var40 < -0.6) {
                                          var10000 = false;
                                       } else {
                                          var10000 = var40 > 0.6 ? true : true;
                                       }
                                    }
                                 }

                                 var16 = var29;
                                 if (raster_z <= first_raster_z || dx_sq + dz_sq >= mapping_radius_sq || at_edge_of_survey && x_plus_z_and_1 == 0) {
                                    block_id = -1;
                                 }

                                 if (block_id >= 0) {
                                    offset_for_pixel = this.getDataOffsetForPixel(x, z);
                                    byte biome_byte = this.data.get(offset_for_pixel + 3);
                                    if ((biome_byte >> 7 & 1) == 0) {
                                       this.data.put(offset_for_pixel, (byte)block_id);
                                       this.data.put(offset_for_pixel + 1, (byte)(world.getBlockMetadata(x, height, z) | depth << 4));
                                       this.data.put(offset_for_pixel + 2, (byte)height);
                                       if ((biome_byte & 127) == 127) {
                                          biome_byte = (byte)world.getBiomeGenForCoords(x, z).biomeID;
                                       }

                                       this.data.put(offset_for_pixel + 3, (byte)(biome_byte | 128));
                                       ++Debug.general_counter;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public String getPixelInfo(int x, int z) {
      if (!this.isXZOnMap(x, z)) {
         return "Off-map";
      } else {
         int offset = this.getDataOffsetForPixel(x, z);
         int block_id = this.data.get(offset);
         int complex_byte = this.data.get(offset + 1);
         int metadata = complex_byte & 15;
         int depth = complex_byte >> 4 & 15;
         int height = this.data.get(offset + 2);
         int biome_id = this.data.get(offset + 3);
         return "block_id=" + block_id + " metadata=" + metadata + " depth=" + depth + " height=" + height + " biome_id=" + biome_id;
      }
   }

   public static void deleteMapFile(World world) {
      String filename = getFilename(world);
      File file = new File(filename);
      if (file.exists() && !file.delete()) {
         world.getWorldLogAgent().logWarning("Unable to delete previous world map file " + filename);
      }

   }
}
