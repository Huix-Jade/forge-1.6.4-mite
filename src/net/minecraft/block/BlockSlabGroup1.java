package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public final class BlockSlabGroup1 extends BlockSlab {
   private static String[] types = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
   private static Block[] model_blocks;
   private Icon side_icon;

   public BlockSlabGroup1(int id, Material material) {
      super(id, material);
   }

   public int getGroup() {
      return 1;
   }

   public String[] getTypes() {
      return types;
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16 && this.getBlockSubtypeUnchecked(metadata) != 2;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 7;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("stone_slab_top");
      this.side_icon = par1IconRegister.registerIcon("stone_slab_side");
   }

   public Block getModelBlock(int metadata) {
      if (model_blocks == null) {
         model_blocks = new Block[]{stone, sandStone, planks, cobblestone, brick, stoneBrick, netherBrick, blockNetherQuartz};
      }

      return model_blocks[this.getBlockSubtype(metadata)];
   }

   public Icon getIcon(int side, int metadata) {
      Block model_block = this.getModelBlock(metadata);
      if (model_block != stone) {
         if (model_block == stoneBrick) {
            return model_block.getIcon(side, 0);
         } else {
            if (model_block == netherBrick) {
               side = 1;
            }

            return model_block.getBlockTextureFromSide(side);
         }
      } else {
         return side != 0 && side != 1 ? this.side_icon : this.blockIcon;
      }
   }
}
