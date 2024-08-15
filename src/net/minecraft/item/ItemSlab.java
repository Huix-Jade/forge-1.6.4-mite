package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoubleSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   private final boolean isFullBlock;
   private final BlockSlab theHalfSlab;
   private final BlockDoubleSlab doubleSlab;

   public ItemSlab(BlockSlab half_slab, BlockDoubleSlab double_slab, boolean is_full_block) {
      super((Block)(is_full_block ? double_slab : half_slab));
      this.theHalfSlab = half_slab;
      this.doubleSlab = double_slab;
      this.isFullBlock = is_full_block;
   }

   public Icon getIconFromSubtype(int par1) {
      return Block.blocksList[this.itemID].getIcon(2, par1);
   }

   public int getMetadata(int par1) {
      return par1;
   }

   public String getUnlocalizedName(ItemStack par1ItemStack) {
      return par1ItemStack == null ? super.getUnlocalizedName() : this.theHalfSlab.getFullSlabName(par1ItemStack.getItemSubtype());
   }

   public boolean tryPlaceAsBlock(RaycastCollision rc, Block block, EntityPlayer player, ItemStack item_stack) {
      if (this.isFullBlock) {
         return super.tryPlaceAsBlock(rc, block, player, item_stack);
      } else if (!rc.isBlock()) {
         Minecraft.setErrorMessage("tryPlaceAsBlock: raycast collision is not block");
         return false;
      } else {
         World world = rc.world;
         boolean modifies_block_hit = false;
         boolean modifies_neighbor_of_block_hit = false;
         int x = rc.block_hit_x;
         int y = rc.block_hit_y;
         int z = rc.block_hit_z;
         BlockSlab block_half_slab;
         if (rc.getBlockHit() == this.getTheHalfSlab()) {
            block_half_slab = (BlockSlab)rc.getBlockHit();
            if (block_half_slab.getItemSubtype(rc.block_hit_metadata) == item_stack.getItemSubtype()) {
               if (rc.face_hit.isTop() && BlockSlab.isBottom(rc.block_hit_metadata)) {
                  modifies_block_hit = true;
               } else if (rc.face_hit.isBottom() && BlockSlab.isTop(rc.block_hit_metadata)) {
                  modifies_block_hit = true;
               }
            }
         }

         if (!modifies_block_hit && rc.getNeighborOfBlockHit() == this.getTheHalfSlab()) {
            block_half_slab = (BlockSlab)rc.getNeighborOfBlockHit();
            int metadata_of_neighbor = rc.getNeighborOfBlockHitMetadata();
            if (block_half_slab.getItemSubtype(metadata_of_neighbor) == item_stack.getItemSubtype()) {
               if (BlockSlab.isBottom(metadata_of_neighbor)) {
                  if (rc.face_hit.isBottom() || rc.block_hit_offset_y > 0.5F) {
                     modifies_neighbor_of_block_hit = true;
                  }
               } else if (rc.face_hit.isTop() || rc.block_hit_offset_y < 0.5F) {
                  modifies_neighbor_of_block_hit = true;
               }

               if (modifies_neighbor_of_block_hit) {
                  x = rc.neighbor_block_x;
                  y = rc.neighbor_block_y;
                  z = rc.neighbor_block_z;
               }
            }
         }

         return !modifies_block_hit && !modifies_neighbor_of_block_hit ? super.tryPlaceAsBlock(rc, block, player, item_stack) : this.getTheDoubleSlab().tryPlaceFromHeldItem(x, y, z, rc.face_hit, item_stack, player, rc.block_hit_offset_x, rc.block_hit_offset_y, rc.block_hit_offset_z, false, false);
      }
   }

   public BlockSlab getTheHalfSlab() {
      return this.theHalfSlab;
   }

   public BlockDoubleSlab getTheDoubleSlab() {
      return this.doubleSlab;
   }

   public boolean isFullBlock() {
      return this.isFullBlock;
   }
}
