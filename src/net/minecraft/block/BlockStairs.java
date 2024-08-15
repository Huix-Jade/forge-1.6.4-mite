package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {
   private static final int[][] field_72159_a = new int[][]{{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
   public final Block modelBlock;
   private final int modelBlockMetadata;

   protected BlockStairs(int par1, Block par2Block, int par3) {
      super(par1, par2Block.blockMaterial, new BlockConstants());
      this.modelBlock = par2Block;
      this.modelBlockMetadata = par3;
      this.setHardness(par2Block.getBlockHardness(0));
      this.setStepSound(par2Block.stepSound);
      this.setLightOpacity(255);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for direction facing, and bit 4 set if inverted";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 8;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public int getRenderType() {
      return 10;
   }

   public void func_82541_d(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      if ((var5 & 4) != 0) {
         this.setBlockBoundsForCurrentThread(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
      }

   }

   public static boolean isBlockStairsID(int par0) {
      return par0 > 0 && Block.blocksList[par0] instanceof BlockStairs;
   }

   private boolean isBlockStairsDirection(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
      return isBlockStairsID(var6) && par1IBlockAccess.getBlockMetadata(par2, par3, par4) == par5;
   }

   public boolean func_82542_g(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 3;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if ((var5 & 4) != 0) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 1.0F;
      float var11 = 0.0F;
      float var12 = 0.5F;
      boolean var13 = true;
      int var14;
      int var15;
      int var16;
      if (var6 == 0) {
         var9 = 0.5F;
         var12 = 1.0F;
         var14 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4);
         var15 = par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var16 = var15 & 3;
            if (var16 == 3 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 + 1, var5)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var16 == 2 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 - 1, var5)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var6 == 1) {
         var10 = 0.5F;
         var12 = 1.0F;
         var14 = par1IBlockAccess.getBlockId(par2 - 1, par3, par4);
         var15 = par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var16 = var15 & 3;
            if (var16 == 3 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 + 1, var5)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var16 == 2 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 - 1, var5)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var6 == 2) {
         var11 = 0.5F;
         var12 = 1.0F;
         var14 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1);
         var15 = par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var16 = var15 & 3;
            if (var16 == 1 && !this.isBlockStairsDirection(par1IBlockAccess, par2 + 1, par3, par4, var5)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var16 == 0 && !this.isBlockStairsDirection(par1IBlockAccess, par2 - 1, par3, par4, var5)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      } else if (var6 == 3) {
         var14 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1);
         var15 = par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var16 = var15 & 3;
            if (var16 == 1 && !this.isBlockStairsDirection(par1IBlockAccess, par2 + 1, par3, par4, var5)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var16 == 0 && !this.isBlockStairsDirection(par1IBlockAccess, par2 - 1, par3, par4, var5)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      }

      this.setBlockBoundsForCurrentThread((double)var9, (double)var7, (double)var11, (double)var10, (double)var8, (double)var12);
      return var13;
   }

   public boolean func_82544_h(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      int var6 = var5 & 3;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if ((var5 & 4) != 0) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 0.5F;
      float var11 = 0.5F;
      float var12 = 1.0F;
      boolean var13 = false;
      int var14;
      int var15;
      int var16;
      if (var6 == 0) {
         var14 = par1IBlockAccess.getBlockId(par2 - 1, par3, par4);
         var15 = par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var16 = var15 & 3;
            if (var16 == 3 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 - 1, var5)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var16 == 2 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 + 1, var5)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var6 == 1) {
         var14 = par1IBlockAccess.getBlockId(par2 + 1, par3, par4);
         var15 = par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var9 = 0.5F;
            var10 = 1.0F;
            var16 = var15 & 3;
            if (var16 == 3 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 - 1, var5)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var16 == 2 && !this.isBlockStairsDirection(par1IBlockAccess, par2, par3, par4 + 1, var5)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var6 == 2) {
         var14 = par1IBlockAccess.getBlockId(par2, par3, par4 - 1);
         var15 = par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var11 = 0.0F;
            var12 = 0.5F;
            var16 = var15 & 3;
            if (var16 == 1 && !this.isBlockStairsDirection(par1IBlockAccess, par2 - 1, par3, par4, var5)) {
               var13 = true;
            } else if (var16 == 0 && !this.isBlockStairsDirection(par1IBlockAccess, par2 + 1, par3, par4, var5)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      } else if (var6 == 3) {
         var14 = par1IBlockAccess.getBlockId(par2, par3, par4 + 1);
         var15 = par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1);
         if (isBlockStairsID(var14) && (var5 & 4) == (var15 & 4)) {
            var16 = var15 & 3;
            if (var16 == 1 && !this.isBlockStairsDirection(par1IBlockAccess, par2 - 1, par3, par4, var5)) {
               var13 = true;
            } else if (var16 == 0 && !this.isBlockStairsDirection(par1IBlockAccess, par2 + 1, par3, par4, var5)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      }

      if (var13) {
         this.setBlockBoundsForCurrentThread((double)var9, (double)var7, (double)var11, (double)var10, (double)var8, (double)var12);
      }

      return var13;
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      this.modelBlock.randomDisplayTick(par1World, par2, par3, par4, par5Random);
   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
      this.modelBlock.onBlockClicked(par1World, par2, par3, par4, par5EntityPlayer);
   }

   public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return this.modelBlock.getMixedBrightnessForBlock(par1IBlockAccess, par2, par3, par4);
   }

   public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return this.modelBlock.getBlockBrightness(par1IBlockAccess, par2, par3, par4);
   }

   public float getExplosionResistance(Explosion explosion) {
      return this.modelBlock.getExplosionResistance(explosion);
   }

   public int getRenderBlockPass() {
      return this.modelBlock.getRenderBlockPass();
   }

   public Icon getIcon(int par1, int par2) {
      return this.modelBlock.getIcon(par1, this.modelBlockMetadata);
   }

   public int tickRate(World par1World) {
      return this.modelBlock.tickRate(par1World);
   }

   public void velocityToAddToEntity(World par1World, int par2, int par3, int par4, Entity par5Entity, Vec3 par6Vec3) {
      this.modelBlock.velocityToAddToEntity(par1World, par2, par3, par4, par5Entity, par6Vec3);
   }

   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   public boolean canCollideCheck(int par1, boolean par2) {
      return this.modelBlock.canCollideCheck(par1, par2);
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return this.modelBlock.isLegalAt(world, x, y, z, metadata);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      this.onNeighborBlockChange(par1World, par2, par3, par4, 0);
      this.modelBlock.onBlockAdded(par1World, par2, par3, par4);
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      this.modelBlock.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      this.modelBlock.onEntityWalking(par1World, par2, par3, par4, par5Entity);
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      return this.modelBlock.updateTick(world, x, y, z, random);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return this.modelBlock.onBlockActivated(world, x, y, z, player, EnumFace.BOTTOM, 0.0F, 0.0F, 0.0F);
   }

   public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {
      this.modelBlock.onBlockDestroyedByExplosion(par1World, par2, par3, par4, par5Explosion);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      EnumDirection direction = entity.getDirectionFromYaw();
      int metadata;
      if (direction.isEast()) {
         metadata = 0;
      } else if (direction.isWest()) {
         metadata = 1;
      } else if (direction.isSouth()) {
         metadata = 2;
      } else {
         metadata = 3;
      }

      if (face == EnumFace.BOTTOM || face != EnumFace.TOP && offset_y > 0.5F) {
         metadata |= 4;
      }

      return metadata;
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      AxisAlignedBB[] multiple_bounds = new AxisAlignedBB[3];
      AxisAlignedBB original_bounds = this.getBoundsFromPool();
      this.func_82541_d(world, x, y, z);
      multiple_bounds[0] = this.getBoundsFromPool();
      boolean has_extra_corner = this.func_82542_g(world, x, y, z);
      multiple_bounds[1] = this.getBoundsFromPool();
      if (has_extra_corner && this.func_82544_h(world, x, y, z)) {
         multiple_bounds[2] = this.getBoundsFromPool();
      }

      this.setBlockBoundsForCurrentThread(original_bounds);
      return multiple_bounds;
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.modelBlock.dropBlockAsEntityItem(info) : super.dropBlockAsEntityItem(info);
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return this.isFaceFlatAndSolid(block_access.getBlockMetadata(x, y, z), EnumFace.get(side).getOpposite());
   }

   public boolean isFaceFlatAndSolid(int metadata, EnumFace face) {
      if (metadata == 0) {
         return face.isEast() || face.isBottom();
      } else if (metadata == 1) {
         return face.isWest() || face.isBottom();
      } else if (metadata == 2) {
         return face.isSouth() || face.isBottom();
      } else if (metadata == 3) {
         return face.isNorth() || face.isBottom();
      } else if (metadata == 4) {
         return face.isEast() || face.isTop();
      } else if (metadata == 5) {
         return face.isWest() || face.isTop();
      } else if (metadata == 6) {
         return face.isSouth() || face.isTop();
      } else if (metadata != 7) {
         Minecraft.setErrorMessage("isFaceFlatAndSolid: unexpected metadata " + metadata);
         return false;
      } else {
         return face.isNorth() || face.isTop();
      }
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
