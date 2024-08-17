package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   private static final int pulled_bit = 1;
   private static final int raised_bit = 2;
   private static final int taut_bit = 4;

   public BlockTripWire(int par1) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.15625, 1.0);
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return "Bit 1 set if entity has collided with wire, bit 2 set if wire is not on ground, and bit 4 set if wire is attached to hooks";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 8;
   }

   public int tickRate(World par1World) {
      return 10;
   }

   public int getRenderBlockPass() {
      return 0;
   }

   public int getRenderType() {
      return 30;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.silk.itemID;
   }

   public static boolean isPulled(int metadata) {
      return BitHelper.isBitSet(metadata, 1);
   }

   public static boolean isRaised(int metadata) {
      return BitHelper.isBitSet(metadata, 2);
   }

   public static boolean isTaut(int metadata) {
      return BitHelper.isBitSet(metadata, 4);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (isRaised(metadata)) {
         return true;
      } else {
         return block_below != null && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata);
      }
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return this.isLegalAt(world, x, y, z, 0) ? 0 : 2;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      boolean var6 = (var5 & 4) == 4;
      boolean var7 = (var5 & 2) == 2;
      if (!var7) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.09375, 1.0);
      } else if (!var6) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.0625, 0.0, 1.0, 0.15625, 1.0);
      }

   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) ? 0 : 2;
      par1World.setBlockMetadataWithNotify(par2, par3, par4, var5, 3);
      this.func_72149_e(par1World, par2, par3, par4, var5);
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      this.func_72149_e(par1World, par2, par3, par4, par6 | 1);
   }

   private void func_72149_e(World par1World, int par2, int par3, int par4, int par5) {
      for(int var6 = 0; var6 < 2; ++var6) {
         for(int var7 = 1; var7 < 42; ++var7) {
            int var8 = par2 + Direction.offsetX[var6] * var7;
            int var9 = par4 + Direction.offsetZ[var6] * var7;
            int var10 = par1World.getBlockId(var8, par3, var9);
            if (var10 == Block.tripWireSource.blockID) {
               int var11 = par1World.getBlockMetadata(var8, par3, var9) & 3;
               if (var11 == Direction.rotateOpposite[var6]) {
                  Block.tripWireSource.func_72143_a(par1World, var8, par3, var9, var10, par1World.getBlockMetadata(var8, par3, var9), true, var7, par5);
               }
               break;
            }

            if (var10 != Block.tripWire.blockID) {
               break;
            }
         }
      }

   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (!par1World.isRemote && (par1World.getBlockMetadata(par2, par3, par4) & 1) != 1) {
         this.updateTripWireState(par1World, par2, par3, par4);
      }

   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else {
         return isPulled(world.getBlockMetadata(x, y, z)) ? this.updateTripWireState(world, x, y, z) : false;
      }
   }

   private boolean updateTripWireState(World par1World, int par2, int par3, int par4) {
      boolean changed_state = false;
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      boolean var6 = (var5 & 1) == 1;
      boolean var7 = false;
      int index = Minecraft.getThreadIndex();
      List var8 = par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX[index], (double)par3 + this.minY[index], (double)par4 + this.minZ[index], (double)par2 + this.maxX[index], (double)par3 + this.maxY[index], (double)par4 + this.maxZ[index]));
      if (!var8.isEmpty()) {
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            Entity var10 = (Entity)var9.next();
            if (!var10.doesEntityNotTriggerPressurePlate()) {
               var7 = true;
               break;
            }
         }
      }

      if (var7 && !var6) {
         var5 |= 1;
      }

      if (!var7 && var6) {
         var5 &= -2;
      }

      if (var7 != var6) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, var5, 3);
         changed_state = true;
         this.func_72149_e(par1World, par2, par3, par4, var5);
      }

      if (var7) {
         par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
      }

      return changed_state;
   }

   public static boolean func_72148_a(IBlockAccess par0IBlockAccess, int par1, int par2, int par3, int par4, int par5) {
      int var6 = par1 + Direction.offsetX[par5];
      int var8 = par3 + Direction.offsetZ[par5];
      int var9 = par0IBlockAccess.getBlockId(var6, par2, var8);
      boolean var10 = (par4 & 2) == 2;
      int var11;
      if (var9 == Block.tripWireSource.blockID) {
         var11 = par0IBlockAccess.getBlockMetadata(var6, par2, var8);
         int var13 = var11 & 3;
         return var13 == Direction.rotateOpposite[par5];
      } else if (var9 == Block.tripWire.blockID) {
         var11 = par0IBlockAccess.getBlockMetadata(var6, par2, var8);
         boolean var12 = (var11 & 2) == 2;
         return var10 == var12;
      } else {
         return false;
      }
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.silk);
   }

   public void makeSoundWhenPlaced(World world, int x, int y, int z, int metadata) {
      if (this.stepSound != null) {
         world.playSoundAtBlock(x, y, z, this.stepSound.getPlaceSound(), this.stepSound.getVolume() / 4.0F, this.stepSound.getPitch() * 0.8F);
      }

   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
