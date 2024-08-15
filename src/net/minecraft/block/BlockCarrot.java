package net.minecraft.block;

import net.minecraft.item.Item;

public class BlockCarrot extends BlockCrops {
   public BlockCarrot(int par1) {
      super(par1, 4);
   }

   protected int getSeedItem() {
      return Item.carrot.itemID;
   }

   protected int getCropItem() {
      return Item.carrot.itemID;
   }

   protected int getDeadCropBlockId() {
      return Block.carrotDead.blockID;
   }

   protected int getMatureYield() {
      return Math.random() < 0.25 ? 3 : 2;
   }

   public int getGrowthStage(int metadata) {
      int growth = this.getGrowth(metadata);
      if (growth == 6) {
         growth = 5;
      }

      return growth / 2;
   }
}
