package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockCarpet extends Block {
   protected BlockCarpet(int par1) {
      super(par1, Material.materialCarpet, (new BlockConstants()).setNotAlwaysLegal());
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
      this.setTickRandomly(true);
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.func_111047_d(0, true);
   }

   public Icon getIcon(int par1, int par2) {
      return Block.cloth.getIcon(par1, par2);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return entity instanceof EntityItem ? getStandardFormBoundingBoxFromPool(x, y, z).setMaxY((double)y + 0.0625) : null;
   }

   public void addCollidingBoundsToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
      if (entity instanceof EntityItem) {
         addIntersectingBoundsToList((AxisAlignedBB)this.getCollisionBounds(world, x, y, z, entity), list, mask);
      }

   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.func_111047_d(0, false);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.func_111047_d(par1IBlockAccess.getBlockMetadata(par2, par3, par4), false);
   }

   protected void func_111047_d(int par1, boolean for_all_threads) {
      byte var2 = 0;
      float var3 = (float)(1 * (1 + var2)) / 16.0F;
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, (double)var3, 1.0, for_all_threads);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below != null && block_below.isTopFlatAndSolid(block_below_metadata);
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
   }

   public String getMetadataNotes() {
      return "All bits used for color";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata;
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      if (other_block == null) {
         return false;
      } else if (other_block.isLiquid()) {
         return true;
      } else {
         return other_block == this && this.getBlockSubtype(other_block_metadata) != this.getBlockSubtype(metadata);
      }
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return side == 1;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
