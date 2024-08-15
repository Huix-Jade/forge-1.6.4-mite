//package net.minecraft.block;
//
//import java.util.List;
//import java.util.Random;
//import net.minecraft.block.material.Material;
//import net.minecraft.entity.Entity;
//import net.minecraft.util.AxisAlignedBB;
//import net.minecraft.util.Facing;
//import net.minecraft.world.IBlockAccess;
//import net.minecraft.world.World;
//
//public abstract class BlockHalfSlab extends Block {
//   protected final boolean isDoubleSlab;
//
//   public BlockHalfSlab(int var1, boolean var2, Material var3) {
//      super(var1, var3);
//      this.isDoubleSlab = var2;
//      if (var2) {
//         opaqueCubeLookup[var1] = true;
//      } else {
//         this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
//      }
//
//      this.setLightOpacity(255);
//   }
//
//   public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
//      if (this.isDoubleSlab) {
//         this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
//      } else {
//         boolean var5 = (var1.getBlockMetadata(var2, var3, var4) & 8) != 0;
//         if (var5) {
//            this.a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
//         } else {
//            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
//         }
//      }
//
//   }
//
//   public void setBlockBoundsForItemRender() {
//      if (this.isDoubleSlab) {
//         this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
//      } else {
//         this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
//      }
//
//   }
//
//   public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
//      this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
//      super.a(var1, var2, var3, var4, var5, var6, var7);
//   }
//
//   public boolean isOpaqueCube() {
//      return this.isDoubleSlab;
//   }
//
//   public int onBlockPlaced(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
//      if (this.isDoubleSlab) {
//         return var9;
//      } else {
//         return var5 != 0 && (var5 == 1 || !((double)var7 > 0.5)) ? var9 : var9 | 8;
//      }
//   }
//
//   public int quantityDropped(Random var1) {
//      return this.isDoubleSlab ? 2 : 1;
//   }
//
//   public int damageDropped(int var1) {
//      return var1 & 7;
//   }
//
//   public boolean renderAsNormalBlock() {
//      return this.isDoubleSlab;
//   }
//
//   public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
//      if (this.isDoubleSlab) {
//         return super.shouldSideBeRendered(var1, var2, var3, var4, var5);
//      } else if (var5 != 1 && var5 != 0 && !super.shouldSideBeRendered(var1, var2, var3, var4, var5)) {
//         return false;
//      } else {
//         int var6 = var2 + Facing.offsetsXForSide[Facing.oppositeSide[var5]];
//         int var7 = var3 + Facing.offsetsYForSide[Facing.oppositeSide[var5]];
//         int var8 = var4 + Facing.offsetsZForSide[Facing.oppositeSide[var5]];
//         boolean var9 = (var1.getBlockMetadata(var6, var7, var8) & 8) != 0;
//         if (var9) {
//            if (var5 == 0) {
//               return true;
//            } else if (var5 == 1 && super.shouldSideBeRendered(var1, var2, var3, var4, var5)) {
//               return true;
//            } else {
//               return !isBlockSingleSlab(var1.getBlockId(var2, var3, var4)) || (var1.getBlockMetadata(var2, var3, var4) & 8) == 0;
//            }
//         } else if (var5 == 1) {
//            return true;
//         } else if (var5 == 0 && super.shouldSideBeRendered(var1, var2, var3, var4, var5)) {
//            return true;
//         } else {
//            return !isBlockSingleSlab(var1.getBlockId(var2, var3, var4)) || (var1.getBlockMetadata(var2, var3, var4) & 8) != 0;
//         }
//      }
//   }
//
//   private static boolean isBlockSingleSlab(int var0) {
//      return var0 == Block.ap.blockID || var0 == Block.bT.blockID;
//   }
//
//   public abstract String getFullSlabName(int var1);
//
//   public int getDamageValue(World var1, int var2, int var3, int var4) {
//      return super.h(var1, var2, var3, var4) & 7;
//   }
//
//   public int idPicked(World var1, int var2, int var3, int var4) {
//      if (isBlockSingleSlab(this.blockID)) {
//         return this.blockID;
//      } else if (this.blockID == Block.ao.blockID) {
//         return Block.ap.blockID;
//      } else {
//         return this.blockID == Block.bS.blockID ? Block.bT.blockID : Block.ap.blockID;
//      }
//   }
//}
