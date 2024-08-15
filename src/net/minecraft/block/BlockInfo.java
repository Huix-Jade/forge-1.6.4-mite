package net.minecraft.block;

import net.minecraft.client.Minecraft;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class BlockInfo {
   public Block block;
   public int x;
   public int y;
   public int z;
   public int metadata;

   public BlockInfo(Block block, int x, int y, int z) {
      this(block, x, y, z, 0);
   }

   public BlockInfo(Block block, int x, int y, int z, int metadata) {
      this.block = block;
      this.x = x;
      this.y = y;
      this.z = z;
      this.metadata = metadata;
   }

   public BlockInfo(World world, Block block, int x, int y, int z) {
      this(block, x, y, z, world.getBlockMetadata(x, y, z));
   }

   public BlockInfo(RaycastCollision rc) {
      this(rc.getBlockHit(), rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, rc.block_hit_metadata);
      if (rc.getBlockHit() == null) {
         Minecraft.setErrorMessage("BlockInfo(rc): rc.getBlockHit() was null");
      }

   }

   public String toString() {
      return this.block.getLocalizedName() + " @ [" + this.x + "," + this.y + "," + this.z + "]";
   }
}
