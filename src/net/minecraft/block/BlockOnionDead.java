package net.minecraft.block;

public class BlockOnionDead extends BlockCropsDead {
   public BlockOnionDead(int block_id) {
      super(block_id, 4);
   }

   public int getGrowthStage(int metadata) {
      int growth = this.getGrowth(metadata);
      if (growth == 0) {
         return 0;
      } else {
         return growth == 7 ? 4 : (growth + 1) / 2;
      }
   }
}
