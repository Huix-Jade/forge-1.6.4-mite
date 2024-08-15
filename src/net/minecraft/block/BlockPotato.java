package net.minecraft.block;

import net.minecraft.item.Item;

public class BlockPotato extends BlockCrops {
   public BlockPotato(int block_id) {
      super(block_id, 4);
   }

   protected int getSeedItem() {
      return Item.potato.itemID;
   }

   protected int getCropItem() {
      return Item.potato.itemID;
   }

   protected int getDeadCropBlockId() {
      return Block.potatoDead.blockID;
   }

   protected int getMatureYield() {
      return Math.random() < 0.25 ? 3 : 2;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      int metadata = info.getMetadata();
      if (this.isBlighted(metadata) || this.getGrowth(metadata) > 0 && !this.isMature(metadata)) {
         if (info.wasSnowedUpon()) {
            playCropPopSound(info);
         }

         return this.dropBlockAsEntityItem(info, Item.poisonousPotato);
      } else {
         return super.dropBlockAsEntityItem(info);
      }
   }

   public float chanceOfBlightPerRandomTick() {
      return super.chanceOfBlightPerRandomTick() * 2.0F;
   }

   public int getGrowthStage(int metadata) {
      int growth = this.getGrowth(metadata);
      if (growth == 6) {
         growth = 5;
      }

      return growth / 2;
   }
}
