package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public abstract class BlockMounted extends Block {
   EnumDirection[] default_metadata_direction_order = new EnumDirection[6];

   public BlockMounted(int id, Material material, BlockConstants constants) {
      super(id, material, constants);
   }

   public abstract EnumFace getFaceMountedTo(int var1);

   public EnumDirection getDirectionFacing(int metadata) {
      return this.getFaceMountedTo(metadata).getNormal();
   }

   public EnumDirection getDirectionOfSupportBlock(int metadata) {
      EnumFace face = this.getFaceMountedTo(metadata);
      return face == null ? null : face.getAntiNormal();
   }

   public boolean canMountToBlock(int metadata, Block neighbor_block, int neighbor_block_metadata, EnumFace face) {
      if (neighbor_block == tilledField) {
         return true;
      } else if (face.isTop() && neighbor_block == jukebox) {
         return false;
      } else if (!(neighbor_block instanceof BlockLeaves) && !neighbor_block.neverHidesAdjacentFaces() && neighbor_block.blockMaterial != Material.cloth && !(neighbor_block instanceof BlockSponge)) {
         if (!(neighbor_block instanceof BlockPistonBase) && !(neighbor_block instanceof BlockPistonMoving)) {
            if (!(neighbor_block instanceof BlockFurnace) && !(neighbor_block instanceof BlockDispenser)) {
               return neighbor_block.isFaceFlatAndSolid(neighbor_block_metadata, face);
            } else {
               return face != ((BlockDirectional)neighbor_block).getFrontFace(neighbor_block_metadata);
            }
         } else {
            return face != ((BlockDirectional)neighbor_block).getFrontFace(neighbor_block_metadata);
         }
      } else {
         return false;
      }
   }

   public final boolean canMountToBlock(World world, int metadata, int x, int y, int z, EnumDirection direction) {
      x = direction.getNeighborX(x);
      y = direction.getNeighborY(y);
      z = direction.getNeighborZ(z);
      Block block = world.getBlock(x, y, z);
      return block != null && this.canMountToBlock(metadata, block, world.getBlockMetadata(x, y, z), direction.getOppositeFace());
   }

   public final boolean canMountToBlockWithDefault(World world, int metadata, int x, int y, int z, EnumDirection direction, boolean result_if_block_does_not_exist) {
      if (direction == null) {
         return false;
      } else {
         return world.neighborBlockExists(x, y, z, direction) ? this.canMountToBlock(world, metadata, x, y, z, direction) : result_if_block_does_not_exist;
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return this.canMountToBlockWithDefault(world, metadata, x, y, z, this.getDirectionOfSupportBlock(metadata), false);
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      if (!this.getFaceMountedTo(metadata).isTop()) {
         return true;
      } else {
         return block_below != null && this.canMountToBlock(metadata, block_below, block_below_metadata, EnumFace.TOP);
      }
   }

   public abstract int getDefaultMetadataForFaceMountedTo(EnumFace var1);

   public int getDefaultMetadata(World world, int x, int y, int z) {
      this.default_metadata_direction_order[0] = EnumDirection.DOWN;
      this.default_metadata_direction_order[1] = EnumDirection.WEST;
      this.default_metadata_direction_order[2] = EnumDirection.EAST;
      this.default_metadata_direction_order[3] = EnumDirection.NORTH;
      this.default_metadata_direction_order[4] = EnumDirection.SOUTH;
      this.default_metadata_direction_order[5] = EnumDirection.UP;

      for(int i = 0; i < 6; ++i) {
         EnumDirection direction = this.default_metadata_direction_order[i];
         int metadata = this.getDefaultMetadataForFaceMountedTo(direction.getOppositeFace());
         if (metadata >= 0 && this.canMountToBlockWithDefault(world, metadata, x, y, z, direction, false)) {
            return metadata;
         }
      }

      return -1;
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return this.getDefaultMetadataForFaceMountedTo(face);
   }

   public boolean canReplaceBlock(int metadata, Block existing_block, int existing_block_metadata) {
      return existing_block != redstoneWire && super.canReplaceBlock(metadata, existing_block, existing_block_metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return this.getDefaultMetadataForFaceMountedTo(direction.getFace());
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
