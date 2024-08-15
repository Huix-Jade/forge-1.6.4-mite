package net.minecraft.world;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.util.Debug;

public final class CaveNetworkGenerator {
   private static boolean use_optimized_code = true;
   private final CaveNetworkStub stub;
   private final int size_x;
   private final int size_z;
   private final int size_y;
   private final int volume;
   private final byte[] cells;
   private final byte[] cells_copy;
   private final byte[] wall_mask;
   private final Random rand;
   private final int size_xz;
   private static final int CELL_AIR = 0;
   private static final int CELL_WALL = 1;
   private final boolean has_mycelium;
   private boolean has_raised_shell_floor;

   public CaveNetworkGenerator(CaveNetworkStub stub) {
      this.stub = stub;
      this.size_x = stub.getSizeX();
      this.size_y = stub.getSizeY();
      this.size_z = stub.getSizeZ();
      this.volume = this.size_x * this.size_y * this.size_z;
      this.cells = new byte[this.volume];
      this.cells_copy = new byte[this.cells.length];
      this.wall_mask = new byte[this.cells.length];
      if (stub.hasLegacySeed()) {
         this.rand = new Random(stub.getLegacySeed());
         this.rand.nextInt(200);
         this.rand.nextInt(2);
         this.rand.nextLong();
      } else {
         this.rand = new Random(stub.getSeed());
         this.rand.nextInt();
      }

      this.size_xz = this.size_x * this.size_z;
      this.has_mycelium = stub.hasMycelium();
      this.generate();
   }

   public int getOriginChunkX() {
      return this.stub.getOriginChunkX();
   }

   public int getOriginChunkZ() {
      return this.stub.getOriginChunkZ();
   }

   public void fill(int value) {
      Arrays.fill(this.cells, (byte)value);
   }

   public void fill(int value, float percentage) {
      for(int i = 0; i < this.cells.length; ++i) {
         if (this.rand.nextFloat() < percentage) {
            this.cells[i] = (byte)value;
         }
      }

   }

   private void fillRandomSubVolumeWithBias(float bias, float min_volume_fraction, float max_volume_fraction) {
      int sub_size_x;
      int sub_size_y;
      int sub_size_z;
      float volume_fraction;
      do {
         sub_size_x = this.rand.nextInt(this.size_x) + 1;
         sub_size_y = this.rand.nextInt(this.size_y) + 1;
         sub_size_z = this.rand.nextInt(this.size_z) + 1;
         volume_fraction = (float)(sub_size_x * sub_size_y * sub_size_z) / (float)this.volume;
      } while(volume_fraction < min_volume_fraction || volume_fraction > max_volume_fraction);

      int x = this.rand.nextBoolean() ? 0 : this.size_x - sub_size_x;
      int y = this.rand.nextBoolean() ? 0 : this.size_y - sub_size_y;
      int z = this.rand.nextBoolean() ? 0 : this.size_z - sub_size_z;

      for(int dx = 0; dx < sub_size_x; ++dx) {
         for(int dz = 0; dz < sub_size_z; ++dz) {
            for(int dy = 0; dy < sub_size_y; ++dy) {
               this.setValueAt(x + dx, y + dy, z + dz, this.rand.nextFloat() < bias ? 0 : 1);
            }
         }
      }

   }

   private void fillBottomHalfWithAirBias() {
      int x = 0;
      int y = 0;
      int z = 0;
      float bias = 0.51F + this.rand.nextFloat() * 0.01F;

      for(int dx = 0; dx < this.size_x; ++dx) {
         for(int dz = 0; dz < this.size_z; ++dz) {
            for(int dy = 0; dy < this.size_y / 2; ++dy) {
               this.setValueAt(x + dx, y + dy, z + dz, this.rand.nextFloat() < bias ? 0 : 1);
            }
         }
      }

   }

   private void raiseFloor() {
      int amount = this.rand.nextInt(this.size_y / 2 + 1);

      for(int x = 0; x < this.size_x; ++x) {
         for(int z = 0; z < this.size_z; ++z) {
            for(int y = 0; y < amount; ++y) {
               this.setValueAt(x, y, z, this.rand.nextFloat() < 0.47F ? 0 : 1);
            }
         }
      }

   }

   private void lowerCeiling() {
      int amount = this.rand.nextInt(this.size_y / 2 + 1);

      for(int x = 0; x < this.size_x; ++x) {
         for(int z = 0; z < this.size_z; ++z) {
            for(int y = this.size_y - amount; y < this.size_y; ++y) {
               this.setValueAt(x, y, z, this.rand.nextFloat() < 0.47F ? 0 : 1);
            }
         }
      }

   }

   public void generateInterior(int smoothening) {
      this.fill(1);
      this.fill(0, 0.5F);
      if (!this.stub.hasLegacySeed() && this.rand.nextBoolean()) {
         this.fillRandomSubVolumeWithBias(0.51F, 0.0F, 0.5F);
      }

      if (!this.stub.hasLegacySeed() && this.rand.nextBoolean()) {
         this.fillRandomSubVolumeWithBias(0.49F, 0.0F, 0.5F);
      }

      if (this.has_mycelium) {
         if (!this.stub.hasLegacySeed() && this.rand.nextBoolean()) {
            this.lowerCeiling();
         }

         this.fillBottomHalfWithAirBias();
      } else {
         if (this.rand.nextBoolean()) {
            this.fillRandomSubVolumeWithBias(0.48F, 0.0F, 0.5F);
         }

         if (this.rand.nextFloat() < 0.8F) {
            if (this.rand.nextBoolean()) {
               this.raiseFloor();
            } else {
               this.lowerCeiling();
            }
         } else if (this.rand.nextBoolean()) {
            this.fillBottomHalfWithAirBias();
         }
      }

      this.smoothen(smoothening);
   }

   private void excavate(int iterations) {
      if (use_optimized_code) {
         this.excavate_optimized(iterations);
      } else {
         for(int i = 0; i < iterations; ++i) {
            System.arraycopy(this.cells, 0, this.cells_copy, 0, this.cells.length);

            for(int x = 1; x < this.size_x - 1; ++x) {
               for(int z = 1; z < this.size_z - 1; ++z) {
                  for(int y = 1; y < this.size_y - 1; ++y) {
                     if (this.cells_copy[this.getIndex(x, y, z)] == 0) {
                        int ran = this.rand.nextInt(6);
                        if (ran == 0) {
                           this.setValueAt(x, y - 1, z, 0);
                        } else if (ran == 1) {
                           this.setValueAt(x, y + 1, z, 0);
                        } else if (ran == 2) {
                           this.setValueAt(x - 1, y, z, 0);
                        } else if (ran == 3) {
                           this.setValueAt(x + 1, y, z, 0);
                        } else if (ran == 4) {
                           this.setValueAt(x, y, z - 1, 0);
                        } else {
                           this.setValueAt(x, y, z + 1, 0);
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private void excavate_optimized(int iterations) {
      for(int i = 0; i < iterations; ++i) {
         System.arraycopy(this.cells, 0, this.cells_copy, 0, this.cells.length);

         for(int x = 1; x < this.size_x - 1; ++x) {
            for(int z = 1; z < this.size_z - 1; ++z) {
               for(int y = 1; y < this.size_y - 1; ++y) {
                  int index = x + z * this.size_x + y * this.size_xz;
                  if (this.cells_copy[index] == 0) {
                     int ran = this.rand.nextInt(6);
                     if (ran == 0) {
                        this.cells[index - this.size_xz] = 0;
                     } else if (ran == 1) {
                        this.cells[index + this.size_xz] = 0;
                     } else if (ran == 2) {
                        this.cells[index - 1] = 0;
                     } else if (ran == 3) {
                        this.cells[index + 1] = 0;
                     } else if (ran == 4) {
                        this.cells[index - this.size_x] = 0;
                     } else {
                        this.cells[index + this.size_x] = 0;
                     }
                  }
               }
            }
         }
      }

   }

   public void generateShell(int smoothening) {
      this.fill(1);
      float center_x = (float)this.size_x * 0.5F;
      float center_y = (float)this.size_y * 0.5F;
      float center_z = (float)this.size_z * 0.5F;
      this.has_raised_shell_floor = this.has_mycelium || this.rand.nextBoolean();

      for(int x = 16; x < this.size_x - 16; ++x) {
         for(int z = 16; z < this.size_z - 16; ++z) {
            for(int y = this.has_raised_shell_floor ? 24 : 16; y < this.size_y - 16; ++y) {
               float dx = (float)x + 0.5F - center_x;
               float dz = (float)z + 0.5F - center_z;
               if (!(dx * dx + dz * dz > 256.0F)) {
                  this.setValueAt(x, y, z, 0);
               }
            }
         }
      }

      this.excavate(Math.max(this.size_x / 2, Math.max(this.size_z / 2, this.size_y / 2)));
      this.smoothen(smoothening);
      System.arraycopy(this.cells, 0, this.wall_mask, 0, this.cells.length);
   }

   public int getIndex(int x, int y, int z) {
      return x + (z << 6) + (y << 12);
   }

   public void setValueAt(int x, int y, int z, int value) {
      this.cells[this.getIndex(x, y, z)] = (byte)value;
   }

   public byte getValueAt(int x, int y, int z) {
      return this.cells[this.getIndex(x, y, z)];
   }

   public byte getValueAt(byte[] cells, int x, int y, int z) {
      return cells[this.getIndex(x, y, z)];
   }

   public void smoothen() {
      if (use_optimized_code) {
         this.smoothen_optimized();
      } else {
         System.arraycopy(this.cells, 0, this.cells_copy, 0, this.cells.length);

         for(int x = 1; x < this.size_x - 1; ++x) {
            for(int z = 1; z < this.size_z - 1; ++z) {
               for(int y = 1; y < this.size_y - 1; ++y) {
                  byte wall_value = this.getValueAt(this.cells_copy, x, y, z);
                  if (wall_value == 0) {
                     wall_value = 1;
                  }

                  int num_walls = 0;

                  for(int dx = -1; dx <= 1 && num_walls < 14; ++dx) {
                     for(int dz = -1; dz <= 1 && num_walls < 14; ++dz) {
                        for(int dy = -1; dy <= 1 && num_walls < 14; ++dy) {
                           if (this.getValueAt(this.cells_copy, x + dx, y + dy, z + dz) != 0) {
                              ++num_walls;
                           }
                        }
                     }
                  }

                  this.setValueAt(x, y, z, num_walls < 14 ? 0 : wall_value);
               }
            }
         }

      }
   }

   public void smoothen_optimized() {
      System.arraycopy(this.cells, 0, this.cells_copy, 0, this.cells.length);
      int[] index_deltas = new int[27];
      int index_deltas_index = -1;

      int index;
      int dz;
      int z;
      for(index = -1; index <= 1; ++index) {
         for(dz = -1; dz <= 1; ++dz) {
            for(z = -1; z <= 1; ++z) {
               ++index_deltas_index;
               index_deltas[index_deltas_index] = index + dz * this.size_x + z * this.size_xz;
            }
         }
      }

      for(dz = 1; dz < this.size_y - 1; ++dz) {
         index = dz * this.size_xz;

         for(z = 1; z < this.size_z - 1; ++z) {
            index += this.size_x;

            for(int x = 1; x < this.size_x - 1; ++x) {
               ++index;
               byte wall_value = this.cells_copy[index];
               if (wall_value == 0) {
                  wall_value = 1;
               }

               int num_walls = 0;

               for(int i = 0; i < index_deltas.length; ++i) {
                  if (this.cells_copy[index + index_deltas[i]] != 0) {
                     ++num_walls;
                     if (num_walls > 13) {
                        break;
                     }
                  }
               }

               this.cells[index] = num_walls < 14 ? 0 : wall_value;
            }

            index -= this.size_x - 2;
         }
      }

   }

   public void smoothen(int num_iterations) {
      while(true) {
         --num_iterations;
         if (num_iterations < 0) {
            return;
         }

         this.smoothen();
      }
   }

   public void applyWallMask(int smoothening) {
      for(int i = 0; i < this.cells.length; ++i) {
         if (this.wall_mask[i] != 0) {
            this.cells[i] = this.wall_mask[i];
         }
      }

      this.smoothen(smoothening);
   }

   private void removePockmarks() {
      int index;
      int z;
      int y;
      for(z = 0; z < this.size_x; ++z) {
         for(y = 0; y < this.size_z; ++y) {
            index = this.getIndex(z, 0, y);
            if (this.cells[index] == 0 && this.cells[index + this.size_xz] != 0) {
               this.cells[index] = 1;
            }

            index = this.getIndex(z, this.size_y - 1, y);
            if (this.cells[index] == 0 && this.cells[index - this.size_xz] != 0) {
               this.cells[index] = 1;
            }
         }
      }

      for(z = 0; z < this.size_x; ++z) {
         for(y = 0; y < this.size_y; ++y) {
            index = this.getIndex(z, y, 0);
            if (this.cells[index] == 0 && this.cells[index + this.size_x] != 0) {
               this.cells[index] = 1;
            }

            index = this.getIndex(z, y, this.size_z - 1);
            if (this.cells[index] == 0 && this.cells[index - this.size_x] != 0) {
               this.cells[index] = 1;
            }
         }
      }

      for(z = 0; z < this.size_z; ++z) {
         for(y = 0; y < this.size_y; ++y) {
            index = this.getIndex(0, y, z);
            if (this.cells[index] == 0 && this.cells[index + 1] != 0) {
               this.cells[index] = 1;
            }

            index = this.getIndex(this.size_x - 1, y, z);
            if (this.cells[index] == 0 && this.cells[index - 1] != 0) {
               this.cells[index] = 1;
            }
         }
      }

   }

   private void generate() {
      int[] smoothening = new int[3];
      int ran = this.rand.nextInt(3);
      if (ran == 0) {
         smoothening[0] = 2;
         smoothening[1] = this.rand.nextInt(57) + 8;
         smoothening[2] = 2 - smoothening[0];
      } else if (ran == 1) {
         smoothening[0] = this.rand.nextInt(3);
         int total_smoothening = this.rand.nextInt(57) + 8;
         smoothening[1] = this.rand.nextInt(total_smoothening + 1);
         smoothening[2] = total_smoothening - smoothening[1];
         if (smoothening[0] + smoothening[2] < 2) {
            smoothening[1] -= 2;
            smoothening[2] += 2;
         }
      } else {
         smoothening[0] = 0;
         smoothening[1] = 0;
         smoothening[2] = this.rand.nextInt(57) + 8;
      }

      this.generateShell(smoothening[0]);
      this.generateInterior(smoothening[1]);
      this.applyWallMask(smoothening[2]);
      this.removePockmarks();
      Debug.println("generateCaveNetwork: " + this.stub);
   }

   public void apply(World world, int x, int y, int z) {
      int lowest_air_cell;
      int dx;
      int dz;
      if (this.has_mycelium) {
         lowest_air_cell = Integer.MAX_VALUE;

         int dy;
         for(dx = 0; dx < this.size_x; ++dx) {
            for(dz = 0; dz < this.size_z; ++dz) {
               for(dy = 0; dy < this.size_y; ++dy) {
                  int value = this.getValueAt(dx, dy, dz);
                  world.setBlock(x + dx, y + dy, z + dz, value);
                  if (value == 0 && dy < lowest_air_cell) {
                     lowest_air_cell = dy;
                  }
               }
            }
         }

         for(dx = 0; dx < this.size_x; ++dx) {
            for(dz = 0; dz < this.size_z; ++dz) {
               for(dy = Math.max(lowest_air_cell, 1); dy < Math.min(lowest_air_cell + 6, this.size_y - 1); ++dy) {
                  if (this.getValueAt(dx, dy, dz) == 0 && this.getValueAt(dx, dy - 1, dz) == 1) {
                     world.setBlock(x + dx, y + dy, z + dz, world.isAirBlock(x + dx, y + dy + 1, z + dz) ? Block.mycelium.blockID : Block.stone.blockID);
                  }
               }
            }
         }
      } else {
         for(lowest_air_cell = 0; lowest_air_cell < this.size_x; ++lowest_air_cell) {
            for(dx = 0; dx < this.size_z; ++dx) {
               for(dz = 0; dz < this.size_y; ++dz) {
                  world.setBlock(x + lowest_air_cell, y + dz, z + dx, this.getValueAt(lowest_air_cell, dz, dx));
               }
            }
         }
      }

   }

   private void replaceBlockAboveIfUnstable(int local_x, int y, int local_z, byte[] block_ids) {
      int block_above_id = block_ids[(local_x * 16 + local_z) * 128 + y + 1];
      if (block_above_id > 0) {
         Block block = Block.getBlock(block_above_id);
         if (block instanceof BlockFalling) {
            if (block_ids[(local_x * 16 + local_z) * 128 + y + 2] == Block.waterStill.blockID) {
               block_ids[(local_x * 16 + local_z) * 128 + y + 1] = (byte)Block.blockClay.blockID;
            } else {
               block_ids[(local_x * 16 + local_z) * 128 + y + 1] = (byte)(block == Block.sand ? Block.sandStone.blockID : Block.stone.blockID);
            }
         }
      }

   }

   public void apply(int chunk_x, int chunk_z, int origin_chunk_x, int origin_chunk_z, byte[] block_ids) {
      int shift_x = (chunk_x - origin_chunk_x) * 16;
      int shift_z = (chunk_z - origin_chunk_z) * 16;
      int lowest_air_cell;
      int local_x;
      int local_z;
      if (this.has_mycelium) {
         lowest_air_cell = this.size_y;

         int cell_y;
         for(local_x = 0; local_x < 16; ++local_x) {
            for(local_z = 0; local_z < 16; ++local_z) {
               for(cell_y = 0; cell_y < this.size_y; ++cell_y) {
                  int value = this.getValueAt(shift_x + local_x, cell_y, shift_z + local_z);
                  if (value == 0) {
                     block_ids[(local_x * 16 + local_z) * 128 + cell_y + 8] = 0;
                     if (cell_y < lowest_air_cell) {
                        lowest_air_cell = cell_y;
                     }
                  }
               }
            }
         }

         for(local_x = 0; local_x < this.size_x; ++local_x) {
            for(local_z = 0; local_z < this.size_z; ++local_z) {
               for(cell_y = 0; cell_y < lowest_air_cell; ++cell_y) {
                  if (this.getValueAt(local_x, cell_y, local_z) == 0) {
                     lowest_air_cell = cell_y;
                     break;
                  }
               }
            }
         }

         for(local_x = 0; local_x < 16; ++local_x) {
            for(local_z = 0; local_z < 16; ++local_z) {
               for(cell_y = Math.max(lowest_air_cell, 1); cell_y < Math.min(lowest_air_cell + 6, this.size_y - 1); ++cell_y) {
                  if (this.getValueAt(shift_x + local_x, cell_y, shift_z + local_z) == 0 && this.getValueAt(shift_x + local_x, cell_y - 1, shift_z + local_z) == 1) {
                     block_ids[(local_x * 16 + local_z) * 128 + cell_y + 8] = (byte)(block_ids[(local_x * 16 + local_z) * 128 + cell_y + 8 + 1] == 0 ? Block.mycelium.blockID : Block.stone.blockID);
                     block_ids[(local_x * 16 + local_z) * 128 + cell_y + 8 - 1] = (byte)Block.stone.blockID;
                  }
               }
            }
         }
      } else {
         for(lowest_air_cell = 0; lowest_air_cell < 16; ++lowest_air_cell) {
            for(local_x = 0; local_x < 16; ++local_x) {
               for(local_z = 8; local_z < 8 + this.size_y; ++local_z) {
                  if (this.getValueAt(shift_x + lowest_air_cell, local_z - 8, shift_z + local_x) == 0) {
                     block_ids[(lowest_air_cell * 16 + local_x) * 128 + local_z] = 0;
                  }
               }
            }
         }
      }

      for(lowest_air_cell = 0; lowest_air_cell < 16; ++lowest_air_cell) {
         for(local_x = 0; local_x < 16; ++local_x) {
            for(local_z = 8 + this.size_y - 1; local_z >= 8; --local_z) {
               if (this.getValueAt(shift_x + lowest_air_cell, local_z - 8, shift_z + local_x) == 0) {
                  this.replaceBlockAboveIfUnstable(lowest_air_cell, local_z, local_x, block_ids);
                  break;
               }
            }
         }
      }

   }
}
