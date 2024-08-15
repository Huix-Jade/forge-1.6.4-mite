package net.minecraft.block;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockCropsDead extends BlockCrops {
   protected BlockCropsDead(int block_id, int num_growth_stages) {
      super(block_id, num_growth_stages);
      this.setTickRandomly(false);
   }

   public String getMetadataNotes() {
      return "Bits 1, 2, and 4 used to track growth";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 8;
   }

   public boolean fertilize(World world, int x, int y, int z, ItemStack item_stack) {
      return false;
   }

   public Icon getIcon(int side, int metadata) {
      return this.iconArray[this.getGrowthStage(metadata)];
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[this.num_growth_stages];

      for(int i = 0; i < this.num_growth_stages; ++i) {
         this.iconArray[i] = par1IconRegister.registerIcon("crops/" + this.getTextureName() + "/dead/" + i);
      }

   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }

   public boolean isDead() {
      return true;
   }
}
