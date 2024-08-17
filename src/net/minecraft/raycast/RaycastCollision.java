package net.minecraft.raycast;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AABBIntercept;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class RaycastCollision {
   public final Raycast raycast;
   public final World world;
   private final Object object_hit;
   public int block_hit_x;
   public int block_hit_y;
   public int block_hit_z;
   public float block_hit_offset_x;
   public float block_hit_offset_y;
   public float block_hit_offset_z;
   public int block_hit_metadata;
   public int neighbor_block_x;
   public int neighbor_block_y;
   public int neighbor_block_z;
   public final EnumFace face_hit;
   public final Vec3 position_hit;

   /** Used to determine what sub-segment is hit */
   public int subHit = -1;

   /** Used to add extra hit info */
   public Object hitInfo = null;

   public RaycastCollision(Raycast raycast, Entity entity_hit, AABBIntercept intercept) {
      this.raycast = raycast.setHasProducedCollisions();
      this.world = entity_hit.worldObj;
      this.object_hit = entity_hit;
      this.face_hit = intercept.face_hit;
      this.position_hit = intercept.position_hit;
   }

   public RaycastCollision(Raycast raycast, int x, int y, int z, EnumFace face_hit, Vec3 position_hit) {
      this.raycast = raycast.setHasProducedCollisions();
      this.world = raycast.getWorld();
      Block block = this.world.getBlock(x, y, z);
      this.object_hit = block;
      this.block_hit_metadata = this.world.getBlockMetadata(x, y, z);
      this.block_hit_x = x;
      this.block_hit_y = y;
      this.block_hit_z = z;
      this.face_hit = face_hit;
      this.position_hit = position_hit.myVec3LocalPool.getVecFromPool(position_hit.xCoord, position_hit.yCoord, position_hit.zCoord);
      this.block_hit_offset_x = (float)(position_hit.xCoord - (double)x);
      this.block_hit_offset_y = (float)(position_hit.yCoord - (double)y);
      this.block_hit_offset_z = (float)(position_hit.zCoord - (double)z);
      if (face_hit != null) {
         if (face_hit == EnumFace.BOTTOM) {
            --y;
         } else if (face_hit == EnumFace.TOP) {
            ++y;
         } else if (face_hit == EnumFace.NORTH) {
            --z;
         } else if (face_hit == EnumFace.SOUTH) {
            ++z;
         } else if (face_hit == EnumFace.WEST) {
            --x;
         } else if (face_hit == EnumFace.EAST) {
            ++x;
         }

         this.neighbor_block_x = x;
         this.neighbor_block_y = y;
         this.neighbor_block_z = z;
      }

   }

   public RaycastCollision(Entity entity_hit) {
      this.world = entity_hit.worldObj;
      this.object_hit = entity_hit;
      this.face_hit = null;
      this.position_hit = entity_hit.getCenterPoint();
      this.raycast = new Raycast(this.world, this.position_hit, this.position_hit);
   }

   public boolean isBlock() {
      return this.object_hit instanceof Block;
   }

   public boolean isSolidBlock() {
      Block block = this.getBlockHit();
      return block != null && block.isSolid(this.block_hit_metadata);
   }

   public boolean isLiquidBlock() {
      Block block = this.getBlockHit();
      return block != null && block.isLiquid();
   }

   public boolean isBlockAt(int x, int y, int z) {
      if (!this.isBlock()) {
         return false;
      } else {
         return x == this.block_hit_x && y == this.block_hit_y && z == this.block_hit_z;
      }
   }

   public boolean isEntity() {
      return this.object_hit instanceof Entity;
   }

   public Block getBlockHit() {
      return this.isBlock() ? (Block)this.object_hit : null;
   }

   public int getBlockHitID() {
      return this.isBlock() ? this.getBlockHit().blockID : 0;
   }

   public Material getBlockHitMaterial() {
      return this.isBlock() ? this.getBlockHit().blockMaterial : null;
   }

   public Block getNeighborOfBlockHit() {
      return this.isBlock() ? this.world.getBlock(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z) : null;
   }

   public int getNeighborOfBlockHitID() {
      return this.isBlock() ? this.world.getBlockId(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z) : 0;
   }

   public int getNeighborOfBlockHitMetadata() {
      return this.isBlock() ? this.world.getBlockMetadata(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z) : 0;
   }

   public Material getNeighborOfBlockHitMaterial() {
      return this.isBlock() ? this.world.getBlockMaterial(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z) : null;
   }

   public Entity getEntityHit() {
      return this.isEntity() ? (Entity)this.object_hit : null;
   }

   public boolean isBlockHitFullWaterBlock(boolean include_moving_water) {
      return this.isBlock() ? BlockFluid.isFullWaterBlock(this.getBlockHit(), this.block_hit_metadata, include_moving_water) : false;
   }

   public boolean isBlockHitFullLavaBlock(boolean include_moving_lava) {
      return this.isBlock() ? BlockFluid.isFullLavaBlock(this.getBlockHit(), this.block_hit_metadata, include_moving_lava) : false;
   }

   public boolean isBlockHitReplaceableBy(Block block, int metadata) {
      return this.isBlock() ? this.getBlockHit().canBeReplacedBy(this.block_hit_metadata, block, metadata) : false;
   }

   public boolean setBlockHitToAir() {
      if (this.world.isRemote) {
         Minecraft.setErrorMessage("setBlockHitToAir: why calling this on client?");
      }

      return this.world.setBlockToAir(this.block_hit_x, this.block_hit_y, this.block_hit_z);
   }

   public boolean isNeighborAirBlock() {
      if (this.isBlock()) {
         return this.world.isAirBlock(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z);
      } else {
         Minecraft.setErrorMessage("isNeighborAirBlock: object hit is not block");
         return false;
      }
   }

   public boolean setNeighborBlock(Block block) {
      if (this.world.isRemote) {
         Minecraft.setErrorMessage("setNeighborBlock: why calling this on client?");
      }

      return this.world.setBlock(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z, block == null ? 0 : block.blockID);
   }

   public void playSoundAtNeighborBlock(String name, float volume, float pitch) {
      if (this.world.isRemote) {
         Minecraft.setErrorMessage("playSoundAtNeighborBlock: not meant to be called on client (" + name + ")");
      }

      this.world.playSoundAtBlock(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z, name, volume, pitch);
   }

   public boolean canPlayerMineBlockHit(EntityPlayer player) {
      if (this.isBlock()) {
         return this.world.canMineBlock(player, this.block_hit_x, this.block_hit_y, this.block_hit_z);
      } else {
         Minecraft.setErrorMessage("canPlayerMineBlockHit: object hit is not block");
         return false;
      }
   }

   public boolean canPlayerEditBlockHit(EntityPlayer player, ItemStack item_stack) {
      if (this.isBlock()) {
         return player.canPlayerEdit(this.block_hit_x, this.block_hit_y, this.block_hit_z, item_stack);
      } else {
         Minecraft.setErrorMessage("canPlayerEditBlockHit: object hit is not block");
         return false;
      }
   }

   public boolean canPlayerMineNeighborOfBlockHit(EntityPlayer player) {
      if (this.isBlock()) {
         return this.world.canMineBlock(player, this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z);
      } else {
         Minecraft.setErrorMessage("canPlayerMineNeighborOfBlockHit: object hit is not block");
         return false;
      }
   }

   public boolean canPlayerEditNeighborOfBlockHit(EntityPlayer player, ItemStack item_stack) {
      if (this.isBlock()) {
         return player.canPlayerEdit(this.neighbor_block_x, this.neighbor_block_y, this.neighbor_block_z, item_stack);
      } else {
         Minecraft.setErrorMessage("canPlayerEditNeighborOfBlockHit: object hit is not block");
         return false;
      }
   }

   public double getDistanceFromOriginToCollisionPoint() {
      return this.raycast.getOrigin().distanceTo(this.position_hit);
   }

   public String toString() {
      if (this.isEntity()) {
         return this.getEntityHit().getEntityName();
      } else {
         return this.isBlock() ? this.getBlockHit().getLocalizedName() + " @ " + this.block_hit_x + "," + this.block_hit_y + "," + this.block_hit_z : "Raycast hit other?";
      }
   }
}
