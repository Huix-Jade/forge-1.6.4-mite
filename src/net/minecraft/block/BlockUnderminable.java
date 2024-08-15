package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class BlockUnderminable extends BlockFalling {
   protected BlockUnderminable(int par1, Material material, BlockConstants constants) {
      super(par1, material, constants.setUseNewSandPhysics());
   }

   public String getMetadataNotes() {
      return "Bit 1 set if block has been undermined and should fall next tick";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 2;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (Minecraft.allow_new_sand_physics) {
         Block block_above = world.getBlock(x, y + 1, z);
         if (block_above != null && block_above.usesNewSandPhysics()) {
            block_above.checkIfNotLegal(world, x, y + 1, z);
         }
      }

      return false;
   }

   public int getBlockUnderminedBit() {
      return 1;
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      int metadata = par1World.getBlockMetadata(par2, par3, par4);
      int undermined_bit = this.getBlockUnderminedBit();
      if ((metadata & undermined_bit) != 0) {
         this.onUnderminedByPlayer(par1World, (EntityPlayer)null, par2, par3, par4);
         par1World.setBlock(par2, par3, par4, this.blockID, metadata ^ undermined_bit, 0);
         return true;
      } else {
         return false;
      }
   }

   public void onUnderminedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
      if (player == null || !player.capabilities.isCreativeMode) {
         if (this.tryToFall(world, x, y, z)) {
            int[] dx = new int[]{0, 1, 0, -1};
            int[] dz = new int[]{-1, 0, 1, 0};

            for(int i = 0; i < dx.length; ++i) {
               int block_id = world.getBlockId(x + dx[i], y, z + dz[i]);
               if (Block.blocksList[block_id] != null && Block.blocksList[block_id] instanceof BlockUnderminable) {
                  this.scheduleUndermine(world, x + dx[i], y, z + dz[i]);
               }
            }
         }

      }
   }

   public void scheduleUndermine(World world, int x, int y, int z) {
      int block_id = world.getBlockId(x, y, z);
      if (block_id != 0) {
         Block block = Block.getBlock(block_id);
         if (block instanceof BlockUnderminable) {
            int metadata = world.getBlockMetadata(x, y, z);
            metadata |= ((BlockUnderminable)block).getBlockUnderminedBit();
            world.setBlock(x, y, z, block_id, metadata, 0);
            world.scheduleBlockUpdate(x, y, z, block_id, this.tickRate(world));
         }
      }
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      return !test_only && placer instanceof EntityPlayer && this.tryToFall(world, x, y, z) ? true : super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      this.onUnderminedByPlayer(world, (EntityPlayer)null, x, y, z);
      super.onEntityWalking(world, x, y, z, entity);
   }

   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      this.onUnderminedByPlayer(world, (EntityPlayer)null, x, y, z);
   }
}
