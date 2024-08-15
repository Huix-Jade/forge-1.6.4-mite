package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RNG;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFace;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class BlockVine extends Block {
   public BlockVine(int par1) {
      super(par1, Material.vine, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public String getMetadataNotes() {
      return "Vines can appear on any combination of sides, and each bit represents one side. If no bits set then vine appears only on bottom surface, which sometimes occurs during chunk generation when swamp trees overlap";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public final int getRenderType() {
      return 20;
   }

   public final void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      float var5 = 0.0625F;
      int var6 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      float var7 = 1.0F;
      float var8 = 1.0F;
      float var9 = 1.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      boolean var13 = var6 > 0;
      if ((var6 & 2) != 0) {
         var10 = Math.max(var10, 0.0625F);
         var7 = 0.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
         var13 = true;
      }

      if ((var6 & 8) != 0) {
         var7 = Math.min(var7, 0.9375F);
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
         var13 = true;
      }

      if ((var6 & 4) != 0) {
         var12 = Math.max(var12, 0.0625F);
         var9 = 0.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var13 = true;
      }

      if ((var6 & 1) != 0) {
         var9 = Math.min(var9, 0.9375F);
         var12 = 1.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var8 = 0.0F;
         var11 = 1.0F;
         var13 = true;
      }

      if (!var13 && this.canBePlacedOn(par1IBlockAccess.getBlockId(par2, par3 + 1, par4))) {
         var8 = Math.min(var8, 0.9375F);
         var11 = 1.0F;
         var7 = 0.0F;
         var10 = 1.0F;
         var9 = 0.0F;
         var12 = 1.0F;
      }

      this.setBlockBoundsForCurrentThread((double)var7, (double)var8, (double)var9, (double)var10, (double)var11, (double)var12);
   }

   public final boolean canPlaceBlockOnSide(World world, int x, int y, int z, EnumFace face) {
      if (face.isTop()) {
         ++y;
      } else if (face.isNorth()) {
         ++z;
      } else if (face.isSouth()) {
         --z;
      } else if (face.isWest()) {
         ++x;
      } else {
         if (!face.isEast()) {
            return false;
         }

         --x;
      }

      return this.canBePlacedOn(world.getBlockId(x, y, z));
   }

   private int getMetadataForFace(EnumFace face) {
      return face.isNorth() ? 1 : (face.isEast() ? 2 : (face.isSouth() ? 4 : (face.isWest() ? 8 : -1)));
   }

   public final int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      int existing_metadata;
      int num_sides;
      if (face.isSide() && world.getBlock(x, y, z) == this) {
         existing_metadata = world.getBlockMetadata(x, y, z);
         num_sides = existing_metadata | this.getMetadataForFace(face);
         return num_sides == existing_metadata ? -1 : num_sides;
      } else if (entity.isEntityPlayer() && face.isBottom() && world.getBlock(x, y, z) == this && world.getBlock(x, y + 1, z) == this) {
         existing_metadata = world.getBlockMetadata(x, y + 1, z);
         num_sides = 0;

         int i;
         for(i = 1; i <= 8; i <<= 1) {
            if (BitHelper.isBitSet(existing_metadata, i)) {
               ++num_sides;
               if (num_sides > 1) {
                  return -1;
               }
            }
         }

         existing_metadata = world.getBlockMetadata(x, y, z);
         i = existing_metadata | world.getBlockMetadata(x, y + 1, z);
         return i == existing_metadata ? -1 : i;
      } else if (face.isNorth()) {
         return 1;
      } else if (face.isEast()) {
         return 2;
      } else if (face.isSouth()) {
         return 4;
      } else if (face.isWest()) {
         return 8;
      } else {
         return face.isBottom() && world.getBlock(x, y + 1, z) == vine ? world.getBlockMetadata(x, y + 1, z) : -1;
      }
   }

   private final boolean canBePlacedOn(int par1) {
      if (par1 == 0) {
         return false;
      } else {
         Block var2 = Block.blocksList[par1];
         return var2.isAlwaysSolidOpaqueStandardFormCube() || var2 instanceof BlockLeaves;
      }
   }

   private final boolean canVineStay(World par1World, int par2, int par3, int par4, int metadata) {
      int var5 = metadata;
      int var6 = var5;
      if (var5 > 0) {
         for(int var7 = 0; var7 <= 3; ++var7) {
            int var8 = 1 << var7;
            if ((var5 & var8) != 0 && !this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var7], par3, par4 + Direction.offsetZ[var7])) && (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID || (par1World.getBlockMetadata(par2, par3 + 1, par4) & var8) == 0)) {
               var6 &= ~var8;
            }
         }
      }

      if (var6 == 0 && !this.canBePlacedOn(par1World.getBlockId(par2, par3 + 1, par4))) {
         return false;
      } else {
         if (var6 != var5) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6, 2);
         }

         return true;
      }
   }

   public final boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return super.isLegalAt(world, x, y, z, metadata) && this.canVineStay(world, x, y, z, metadata);
   }

   public final int getBlockColor() {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   public final int getRenderColor(int par1) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   public final int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return par1IBlockAccess.getBiomeGenForCoords(par2, par4).getBiomeFoliageColor();
   }

   public final int tickRate(World world) {
      return 200;
   }

   public final boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else {
         if (((WorldServer)par1World).fast_forwarding) {
            if (!RNG.chance_in_16[++RNG.random_number_index & 32767]) {
               return false;
            }
         } else if (!RNG.chance_in_4[++RNG.random_number_index & 32767]) {
            return false;
         }

         byte var6 = 4;
         int var7 = 5;
         boolean var8 = false;

         int var9;
         int var10;
         int var11;
         label147:
         for(var9 = par2 - var6; var9 <= par2 + var6; ++var9) {
            for(var10 = par4 - var6; var10 <= par4 + var6; ++var10) {
               for(var11 = par3 - 1; var11 <= par3 + 1; ++var11) {
                  if (par1World.getBlockId(var9, var11, var10) == this.blockID) {
                     --var7;
                     if (var7 <= 0) {
                        var8 = true;
                        break label147;
                     }
                  }
               }
            }
         }

         var9 = par1World.getBlockMetadata(par2, par3, par4);
         var10 = par1World.rand.nextInt(6);
         var11 = Direction.facingToDirection[var10];
         int var12;
         int var13;
         if (var10 == 1 && par3 < 255 && par1World.isAirBlock(par2, par3 + 1, par4)) {
            if (var8) {
               return false;
            }

            var12 = par1World.rand.nextInt(16) & var9;
            if (var12 > 0) {
               for(var13 = 0; var13 <= 3; ++var13) {
                  if (!this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var13], par3 + 1, par4 + Direction.offsetZ[var13]))) {
                     var12 &= ~(1 << var13);
                  }
               }

               if (var12 > 0) {
                  par1World.setBlock(par2, par3 + 1, par4, this.blockID, var12, 2);
               }
            }
         } else {
            int var14;
            if (var10 >= 2 && var10 <= 5 && (var9 & 1 << var11) == 0) {
               if (var8) {
                  return false;
               }

               var12 = par1World.getBlockId(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11]);
               if (var12 != 0 && Block.blocksList[var12] != null) {
                  Block block = getBlock(var12);
                  if (block.isSolidOpaqueStandardFormCube(par1World, par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11])) {
                     par1World.setBlockMetadataWithNotify(par2, par3, par4, var9 | 1 << var11, 2);
                     return true;
                  }
               } else {
                  var13 = var11 + 1 & 3;
                  var14 = var11 + 3 & 3;
                  if ((var9 & 1 << var13) != 0 && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var11] + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var13]))) {
                     par1World.setBlock(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11], this.blockID, 1 << var13, 2);
                  } else if ((var9 & 1 << var14) != 0 && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var11] + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var14]))) {
                     par1World.setBlock(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11], this.blockID, 1 << var14, 2);
                  } else if ((var9 & 1 << var13) != 0 && par1World.isAirBlock(par2 + Direction.offsetX[var11] + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var13]) && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var13]))) {
                     par1World.setBlock(par2 + Direction.offsetX[var11] + Direction.offsetX[var13], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var13], this.blockID, 1 << (var11 + 2 & 3), 2);
                  } else if ((var9 & 1 << var14) != 0 && par1World.isAirBlock(par2 + Direction.offsetX[var11] + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var14]) && this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var14]))) {
                     par1World.setBlock(par2 + Direction.offsetX[var11] + Direction.offsetX[var14], par3, par4 + Direction.offsetZ[var11] + Direction.offsetZ[var14], this.blockID, 1 << (var11 + 2 & 3), 2);
                  } else if (this.canBePlacedOn(par1World.getBlockId(par2 + Direction.offsetX[var11], par3 + 1, par4 + Direction.offsetZ[var11]))) {
                     par1World.setBlock(par2 + Direction.offsetX[var11], par3, par4 + Direction.offsetZ[var11], this.blockID, 0, 2);
                  }
               }
            } else if (par3 > 1) {
               var12 = par1World.getBlockId(par2, par3 - 1, par4);
               if (var12 == 0) {
                  if (!RNG.chance_in_4[par2 * 25498 + par3 * 544685 + par4 * 8567567 & 32767] || par1World.getBlock(par2, par3 + 1, par4) != this) {
                     var13 = par1World.rand.nextInt(16) & var9;
                     if (var13 > 0) {
                        par1World.setBlock(par2, par3 - 1, par4, this.blockID, var13, 2);
                     }
                  }
               } else if (var12 == this.blockID) {
                  var13 = par1World.rand.nextInt(16) & var9;
                  var14 = par1World.getBlockMetadata(par2, par3 - 1, par4);
                  if (var14 != (var14 | var13)) {
                     par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, var14 | var13, 2);
                  }
               }
            }
         }

         return false;
      }
   }

   public float getBlockHardness(int metadata) {
      return super.getBlockHardness(metadata) * (float)getNumVines(metadata);
   }

   public static int getNumVines(int metadata) {
      int num_sides = 0;
      if (BitHelper.isBitSet(metadata, 1)) {
         ++num_sides;
      }

      if (BitHelper.isBitSet(metadata, 2)) {
         ++num_sides;
      }

      if (BitHelper.isBitSet(metadata, 4)) {
         ++num_sides;
      }

      if (BitHelper.isBitSet(metadata, 8)) {
         ++num_sides;
      }

      if (num_sides == 0) {
         num_sides = 1;
      }

      return num_sides;
   }

   public ItemStack createStackedBlock(int metadata) {
      return new ItemStack(this, getNumVines(metadata), this.getItemSubtype(metadata));
   }

   public final int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (!info.wasReplaced()) {
         info.world.destroyBlock(info, false, true);
      }

      return 0;
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      Block existing_block = world.getBlock(x, y, z);
      if (existing_block == this) {
         int existing_metadata = world.getBlockMetadata(x, y, z);

         for(int i = 1; i <= 8; i <<= 1) {
            if (BitHelper.isBitSet(metadata, i) && !BitHelper.isBitSet(existing_metadata, i)) {
               return true;
            }
         }
      }

      return super.canBePlacedAt(world, x, y, z, metadata);
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean isAlwaysReplaceable() {
      return true;
   }

   public boolean showDestructionParticlesWhenReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return true;
   }
}
