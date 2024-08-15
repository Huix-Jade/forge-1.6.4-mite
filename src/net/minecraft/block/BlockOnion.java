package net.minecraft.block;

import net.minecraft.item.Item;

public class BlockOnion extends BlockCrops {
   public BlockOnion(int block_id) {
      super(block_id, 5);
   }

   protected int getSeedItem() {
      return Item.onion.itemID;
   }

   protected int getCropItem() {
      return Item.onion.itemID;
   }

   protected int getDeadCropBlockId() {
      return Block.onionsDead.blockID;
   }

   protected int getMatureYield() {
      return Math.random() < 0.25 ? 3 : 2;
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
