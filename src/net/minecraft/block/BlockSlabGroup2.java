package net.minecraft.block;

import net.minecraft.block.material.Material;

public final class BlockSlabGroup2 extends BlockSlab {
   private static String[] types = new String[]{"oak", "spruce", "birch", "jungle"};

   public BlockSlabGroup2(int id, Material material) {
      super(id, material);
   }

   public int getGroup() {
      return 2;
   }

   public String[] getTypes() {
      return types;
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16 && !BitHelper.isBitSet(metadata, 4);
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public Block getModelBlock(int metadata) {
      return planks;
   }
}
