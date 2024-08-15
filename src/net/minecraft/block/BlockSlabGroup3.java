package net.minecraft.block;

import net.minecraft.block.material.Material;

public final class BlockSlabGroup3 extends BlockSlab {
   private static String[] types = new String[]{"obsidian"};

   public BlockSlabGroup3(int id, Material material) {
      super(id, material);
   }

   public int getGroup() {
      return 3;
   }

   public String[] getTypes() {
      return types;
   }

   public boolean isValidMetadata(int metadata) {
      return metadata == 0 || metadata == 8;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return 0;
   }

   public Block getModelBlock(int metadata) {
      return obsidian;
   }
}
