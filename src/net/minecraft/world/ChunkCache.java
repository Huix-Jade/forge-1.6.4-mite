package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;

public final class ChunkCache implements IBlockAccess {
   private int chunkX;
   private int chunkZ;
   private Chunk[][] chunkArray;
   private boolean isEmpty;
   private World worldObj;

   public ChunkCache(World par1World, int par2, int par3, int par4, int par5, int par6, int par7, int par8) {
      this.worldObj = par1World;
      this.chunkX = par2 - par8 >> 4;
      this.chunkZ = par4 - par8 >> 4;
      int var9 = par5 + par8 >> 4;
      int var10 = par7 + par8 >> 4;
      this.chunkArray = new Chunk[var9 - this.chunkX + 1][var10 - this.chunkZ + 1];
      this.isEmpty = true;

      int var11;
      int var12;
      Chunk var13;
      for(var11 = this.chunkX; var11 <= var9; ++var11) {
         for(var12 = this.chunkZ; var12 <= var10; ++var12) {
            var13 = par1World.getChunkFromChunkCoords(var11, var12);
            if (var13 != null) {
               this.chunkArray[var11 - this.chunkX][var12 - this.chunkZ] = var13;
            }
         }
      }

      for(var11 = par2 >> 4; var11 <= par5 >> 4; ++var11) {
         for(var12 = par4 >> 4; var12 <= par7 >> 4; ++var12) {
            var13 = this.chunkArray[var11 - this.chunkX][var12 - this.chunkZ];
            if (var13 != null && !var13.getAreLevelsEmpty(par3, par6)) {
               this.isEmpty = false;
               return;
            }
         }
      }

   }

   public boolean extendedLevelsInChunkCache() {
      return this.isEmpty;
   }

   public int getBlockId(int par1, int par2, int par3) {
      if (par2 < 0) {
         return 0;
      } else if (par2 >= 256) {
         return 0;
      } else {
         int var4 = (par1 >> 4) - this.chunkX;
         int var5 = (par3 >> 4) - this.chunkZ;
         if (var4 >= 0 && var4 < this.chunkArray.length && var5 >= 0 && var5 < this.chunkArray[var4].length) {
            Chunk var6 = this.chunkArray[var4][var5];
            return var6 == null ? 0 : var6.getBlockID(par1 & 15, par2, par3 & 15);
         } else {
            return 0;
         }
      }
   }

   public Block getBlock(int x, int y, int z) {
      return Block.getBlock(this.getBlockId(x, y, z));
   }

   public final boolean isBlockSolid(int x, int y, int z) {
      return Block.isBlockSolid(this, x, y, z);
   }

   public TileEntity getBlockTileEntity(int par1, int par2, int par3) {
      int l = (par1 >> 4) - this.chunkX;
      int i1 = (par3 >> 4) - this.chunkZ;
      if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
      {
         Chunk chunk = this.chunkArray[l][i1];
         return chunk == null ? null : chunk.getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
      }
      else
      {
         return null;
      }
   }

   public float getBrightness(int par1, int par2, int par3, int par4) {
      int var5 = this.getLightValue(par1, par2, par3);
      if (var5 < par4) {
         var5 = par4;
      }

      return this.worldObj.provider.lightBrightnessTable[var5];
   }

   public final int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4) {
      int var5 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
      int var6 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);
      if (var6 < par4) {
         var6 = par4;
      }

      return var5 << 20 | var6 << 4;
   }

   public float getLightBrightness(int par1, int par2, int par3) {
      return this.worldObj.provider.lightBrightnessTable[this.getLightValue(par1, par2, par3)];
   }

   private int getLightValue(int par1, int par2, int par3) {
      return this.getLightValueExt(par1, par2, par3, true);
   }

   private int getLightValueExt(int par1, int par2, int par3, boolean par4) {
      if (!this.worldObj.isWithinBlockDomain(par1, par3)) {
         return 15;
      } else {
         int var5;
         if (par4) {
            var5 = this.getBlockId(par1, par2, par3);
            if (Block.useNeighborBrightness[var5]) {
               Block block = Block.getBlock(var5);
               int metadata = this.getBlockMetadata(par1, par2, par3);
               int brightness = 0;

               for(int ordinal = 0; ordinal < 6; ++ordinal) {
                  EnumDirection direction = EnumDirection.get(ordinal);
                  if (block.useNeighborBrightness(metadata, direction)) {
                     brightness = Math.max(brightness, this.getLightValueExt(par1 + direction.dx, par2 + direction.dy, par3 + direction.dz, false));
                     if (brightness > 14) {
                        break;
                     }
                  }
               }

               return brightness;
            }
         }

         if (par2 < 0) {
            return 0;
         } else if (par2 >= 256) {
            var5 = 15 - this.worldObj.skylightSubtracted;
            if (var5 < 0) {
               var5 = 0;
            }

            return var5;
         } else {
            var5 = (par1 >> 4) - this.chunkX;
            int var6 = (par3 >> 4) - this.chunkZ;
            return this.chunkArray[var5][var6].getBlockLightValue(par1 & 15, par2, par3 & 15, this.worldObj.skylightSubtracted);
         }
      }
   }

   public int getBlockMetadata(int par1, int par2, int par3) {
      if (par2 < 0) {
         return 0;
      } else if (par2 >= 256) {
         return 0;
      } else {
         int l = (par1 >> 4) - this.chunkX;
         int i1 = (par3 >> 4) - this.chunkZ;
         if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
         {
            Chunk chunk = this.chunkArray[l][i1];
            return chunk == null ? 0 : chunk.getBlockMetadata(par1 & 15, par2, par3 & 15);
         }
         return 0;
      }
   }

   public Material getBlockMaterial(int par1, int par2, int par3) {
      int var4 = this.getBlockId(par1, par2, par3);
      return var4 == 0 ? Material.air : Block.blocksList[var4].blockMaterial;
   }

   public Material getBlockMaterial(int block_id) {
      return block_id == 0 ? Material.air : Block.blocksList[block_id].blockMaterial;
   }

   public BiomeGenBase getBiomeGenForCoords(int par1, int par2) {
      return this.worldObj.getBiomeGenForCoords(par1, par2);
   }

   public final boolean isBlockStandardFormOpaqueCube(int par1, int par2, int par3) {
      return Block.isBlockOpaqueStandardFormCube(this, par1, par2, par3);
   }

   public final boolean isBlockNormalCube(int par1, int par2, int par3) {
      return Block.isNormalCube(this.getBlockId(par1, par2, par3));
   }

   public boolean doesBlockHaveSolidTopSurface(int x, int y, int z) {
      Block block = Block.blocksList[this.getBlockId(x, y, z)];
      return block != null && block.isBlockTopFacingSurfaceSolid(this.getBlockMetadata(x, y, z));
   }

   public Vec3Pool getWorldVec3Pool() {
      return this.worldObj.getWorldVec3Pool();
   }

   public boolean isAirBlock(int par1, int par2, int par3) {
      int id = getBlockId(par1, par2, par3);
      return id == 0 || Block.blocksList[id] == null || Block.blocksList[id].isAirBlock(this.worldObj, par1, par2, par3);
   }

   public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
      if (par3 < 0) {
         par3 = 0;
      } else if (par3 > 255) {
         par3 = 255;
      }

      if (!this.worldObj.isWithinBlockDomain(par2, par4)) {
         return par1EnumSkyBlock.defaultLightValue;
      } else if (par1EnumSkyBlock == EnumSkyBlock.Sky && this.worldObj.provider.hasNoSky) {
         return 0;
      } else {
         int block_id = this.getBlockId(par2, par3, par4);
         if (!Block.useNeighborBrightness[block_id]) {
            int var5 = (par2 >> 4) - this.chunkX;
            int var6 = (par4 >> 4) - this.chunkZ;
            return this.chunkArray[var5][var6].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
         } else {
            Block block = Block.getBlock(block_id);
            int metadata = this.getBlockMetadata(par2, par3, par4);
            int brightness = 0;

            for(int ordinal = 0; ordinal < 6; ++ordinal) {
               EnumDirection direction = EnumDirection.get(ordinal);
               if (block.useNeighborBrightness(metadata, direction)) {
                  brightness = Math.max(brightness, this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 + direction.dx, par3 + direction.dy, par4 + direction.dz));
                  if (brightness > 14) {
                     break;
                  }
               }
            }

            return brightness;
         }
      }
   }

   public int getSpecialBlockBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
      if (par3 < 0) {
         par3 = 0;
      } else if (par3 > 255) {
         par3 = 255;
      }

      if (this.worldObj.isWithinBlockDomain(par2, par4)) {
         int var5 = (par2 >> 4) - this.chunkX;
         int var6 = (par4 >> 4) - this.chunkZ;
         return this.chunkArray[var5][var6].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
      } else {
         return par1EnumSkyBlock.defaultLightValue;
      }
   }

   public int getHeight() {
      return 256;
   }

   public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4) {
      int var5 = this.getBlockId(par1, par2, par3);
      return var5 == 0 ? 0 : Block.blocksList[var5].isProvidingStrongPower(this, par1, par2, par3, par4);
   }

   public World getWorld() {
      return this.worldObj;
   }

   public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default) {
      if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
         return _default;
      }

      int blockId = getBlockId(x, y, z);
      Block block = Block.blocksList[blockId];

      if (block != null) {
         return block.isBlockSolidOnSide(this.worldObj, x, y, z, side);
      }

      return false;
   }
}
