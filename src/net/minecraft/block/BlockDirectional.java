package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Axis;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public abstract class BlockDirectional extends Block {
   private EnumDirection[] direction_facing_for_metadata = new EnumDirection[16];
   private int[] default_metadata_for_direction_facing = new int[6];
   private boolean are_maps_ready;
   private final boolean can_face_up;
   private final boolean can_face_down;
   EnumDirection[] default_metadata_direction_order = new EnumDirection[6];

   protected BlockDirectional(int id, Material material, BlockConstants block_constants) {
      super(id, material, block_constants);
      this.createMaps();
      this.can_face_up = this.canFaceDirection(EnumDirection.UP);
      this.can_face_down = this.canFaceDirection(EnumDirection.DOWN);
   }

   private void createMaps() {
      int i;
      for(i = 0; i < this.direction_facing_for_metadata.length; ++i) {
         this.direction_facing_for_metadata[i] = this.getDirectionFacing(i);
      }

      for(i = 0; i < this.default_metadata_for_direction_facing.length; ++i) {
         this.default_metadata_for_direction_facing[i] = -1;
         EnumDirection direction = EnumDirection.get(i);

         for(int j = 0; j < this.direction_facing_for_metadata.length; ++j) {
            if (this.direction_facing_for_metadata[j] == direction) {
               this.default_metadata_for_direction_facing[i] = j;
               break;
            }
         }
      }

      this.are_maps_ready = true;
   }

   public abstract EnumDirection getDirectionFacing(int var1);

   public final EnumDirection getDirectionFacingStandard4(int metadata) {
      if (this.are_maps_ready) {
         return this.direction_facing_for_metadata[metadata];
      } else {
         int direction = metadata & 3;
         if (direction == 0) {
            return EnumDirection.SOUTH;
         } else if (direction == 1) {
            return EnumDirection.WEST;
         } else {
            return direction == 2 ? EnumDirection.NORTH : EnumDirection.EAST;
         }
      }
   }

   public final EnumDirection getDirectionFacingStandard6(int metadata) {
      if (this.are_maps_ready) {
         return this.direction_facing_for_metadata[metadata];
      } else {
         int direction = metadata & 7;
         if (direction == 0) {
            return EnumDirection.DOWN;
         } else if (direction == 1) {
            return EnumDirection.UP;
         } else if (direction == 2) {
            return EnumDirection.NORTH;
         } else if (direction == 3) {
            return EnumDirection.SOUTH;
         } else if (direction == 4) {
            return EnumDirection.WEST;
         } else {
            return direction == 5 ? EnumDirection.EAST : null;
         }
      }
   }

   public final EnumDirection getDirectionFacingStandard6(int metadata, boolean include_up_and_down) {
      EnumDirection direction = this.getDirectionFacingStandard6(metadata);
      return include_up_and_down || direction != EnumDirection.DOWN && direction != EnumDirection.UP ? direction : null;
   }

   public abstract int getMetadataForDirectionFacing(int var1, EnumDirection var2);

   public final int getDefaultMetadataForDirectionFacing(EnumDirection direction) {
      if (this.are_maps_ready) {
         return this.default_metadata_for_direction_facing[direction.ordinal()];
      } else {
         for(int i = 0; i < this.direction_facing_for_metadata.length; ++i) {
            if (this.direction_facing_for_metadata[i] == direction) {
               return i;
            }
         }

         return -1;
      }
   }

   public final boolean canFaceDirection(EnumDirection direction) {
      return this.getDefaultMetadataForDirectionFacing(direction) >= 0;
   }

   public final boolean canFaceUp() {
      return this.can_face_up;
   }

   public final boolean canFaceDown() {
      return this.can_face_down;
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (this.can_face_up || this.can_face_down) {
         double entity_foot_pos_y = entity.posY - (double)entity.yOffset;
         int entity_foot_block_y = (int)Math.round(entity_foot_pos_y);
         int dy = y - entity_foot_block_y;
         if (dy < 0 && entity.rotationPitch > 55.0F && this.can_face_up) {
            return this.getDefaultMetadataForDirectionFacing(EnumDirection.UP);
         }

         if (dy > 1 && entity.rotationPitch < -35.0F && this.can_face_down) {
            return this.getDefaultMetadataForDirectionFacing(EnumDirection.DOWN);
         }
      }

      return this.getDefaultMetadataForDirectionFacing(entity.getDirectionFromYaw().getOpposite());
   }

   public int getDefaultMetadata(World world, int x, int y, int z) {
      this.default_metadata_direction_order[0] = EnumDirection.WEST;
      this.default_metadata_direction_order[1] = EnumDirection.EAST;
      this.default_metadata_direction_order[2] = EnumDirection.NORTH;
      this.default_metadata_direction_order[3] = EnumDirection.SOUTH;
      this.default_metadata_direction_order[4] = EnumDirection.UP;
      this.default_metadata_direction_order[5] = EnumDirection.DOWN;
      Block[] blocks = new Block[6];
      int[] metadatas = new int[6];

      int i;
      EnumDirection direction;
      for(i = 0; i < 6; ++i) {
         direction = this.default_metadata_direction_order[i];
         if (this.canFaceDirection(direction)) {
            Block block = world.getBlock(x + direction.dx, y + direction.dy, z + direction.dz);
            if (block == null) {
               return this.getDefaultMetadataForDirectionFacing(direction);
            }

            blocks[i] = block;
            metadatas[i] = world.getBlockMetadata(x + direction.dx, y + direction.dy, z + direction.dz);
         }
      }

      for(i = 0; i < 6; ++i) {
         direction = this.default_metadata_direction_order[i];
         if (this.canFaceDirection(direction) && !blocks[i].isSolid(metadatas[i])) {
            return this.getDefaultMetadataForDirectionFacing(direction);
         }
      }

      for(i = 0; i < 6; ++i) {
         direction = this.default_metadata_direction_order[i];
         if (this.canFaceDirection(direction)) {
            int metadata = world.getBlockMetadata(x + direction.dx, y + direction.dy, z + direction.dz);
            if (!blocks[i].isStandardFormCube(metadata)) {
               return this.getDefaultMetadataForDirectionFacing(direction);
            }
         }
      }

      return 0;
   }

   public final int getDefaultMetadataAdjustedForCoordBaseMode(EnumDirection direction_facing_in_coord_base_mode_2, int coord_base_mode) {
      return this.getMetadataForDirectionFacing(0, direction_facing_in_coord_base_mode_2.adjustForCoordBaseMode(coord_base_mode));
   }

   public final EnumFace getFrontFace(int metadata) {
      return this.getDirectionFacing(metadata).getFace();
   }

   public final EnumFace getBackFace(int metadata) {
      return this.getDirectionFacing(metadata).getOppositeFace();
   }

   public final boolean isAlignedWith(int metadata, Axis axis) {
      return this.getDirectionFacing(metadata).isAlignedWith(axis);
   }
}
