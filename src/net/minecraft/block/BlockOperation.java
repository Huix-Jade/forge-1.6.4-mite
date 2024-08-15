package net.minecraft.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockOperation;
import net.minecraft.world.WorldServer;

public final class BlockOperation {
   public final EnumBlockOperation type;
   public final int x;
   public final int y;
   public final int z;
   public final long tick;
   public final Object object;

   public BlockOperation(EnumBlockOperation type, int x, int y, int z, long tick, Object object) {
      this.type = type;
      this.x = x;
      this.y = y;
      this.z = z;
      this.tick = tick;
      this.object = object;
   }

   public BlockOperation(EnumBlockOperation type, int x, int y, int z, long tick) {
      this(type, x, y, z, tick, (Object)null);
   }

   public boolean isDuplicate(EnumBlockOperation type, int x, int y, int z, long tick) {
      return type == this.type && x == this.x && y == this.y && z == this.z && tick == this.tick;
   }

   public void perform(WorldServer world) {
      Block block = world.getBlock(this.x, this.y, this.z);
      if (this.type == EnumBlockOperation.try_extinguish_by_items) {
         if (block == Block.fire) {
            ((BlockFire)block).tryExtinguishByItems(world, this.x, this.y, this.z);
         }
      } else if (this.type == EnumBlockOperation.pumpkin_lantern_flooded) {
         if (block == Block.pumpkinLantern) {
            block.dropBlockAsEntityItem((new BlockBreakInfo(world, this.x, this.y, this.z)).setFlooded((BlockFluid)null), Block.torchWood);
            world.setBlock(this.x, this.y, this.z, Block.pumpkin.blockID, world.getBlockMetadata(this.x, this.y, this.z), 3);
            world.playSoundEffect((double)((float)this.x + 0.5F), (double)((float)this.y + 0.5F), (double)((float)this.z + 0.5F), "random.pop", 0.05F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }
      } else if (this.type == EnumBlockOperation.spawn_silverfish && block == Block.silverfish) {
         world.watchAnimal(-1, this.x, this.y, this.z, -2);
         int metadata = world.getBlockMetadata(this.x, this.y, this.z);
         world.destroyBlock((new BlockBreakInfo(world, this.x, this.y, this.z)).setSilverfish((EntityPlayer)this.object), false);
         BlockSilverfish.spawnSilverfishEntity(world, this.x, this.y, this.z, metadata, (EntityPlayer)this.object);
         Block block_above = world.getBlock(this.x, this.y + 1, this.z);
         if (block_above instanceof BlockUnderminable) {
            ((BlockUnderminable)block_above).tryToFall(world, this.x, this.y + 1, this.z);
         }
      }

   }

   public boolean isFlushedOnExit() {
      return this.type == EnumBlockOperation.spawn_silverfish;
   }
}
