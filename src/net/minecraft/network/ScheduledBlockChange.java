package net.minecraft.network;

public class ScheduledBlockChange {
   public int x;
   public int y;
   public int z;
   public int from_block_id;
   public int to_block_id;
   public int to_metadata;
   public int ticks_from_now;

   public ScheduledBlockChange(int x, int y, int z, int from_block_id, int to_block_id, int to_metadata, int ticks_from_now) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.from_block_id = from_block_id;
      this.to_block_id = to_block_id;
      this.to_metadata = to_metadata;
      this.ticks_from_now = ticks_from_now;
   }
}
