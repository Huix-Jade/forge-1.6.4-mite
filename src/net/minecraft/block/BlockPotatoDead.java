package net.minecraft.block;

public class BlockPotatoDead extends BlockCropsDead {
   public BlockPotatoDead(int block_id) {
      super(block_id, 3);
   }

   public int getGrowthStage(int metadata) {
      int growth = this.getGrowth(metadata);
      if (growth == 6) {
         growth = 5;
      }

      return growth / 2;
   }
}
