package net.minecraft.block;

import net.minecraft.block.material.Material;

public class BlockBedrock extends Block {
   public BlockBedrock(int id, Material material, BlockConstants constants) {
      super(id, material, constants);
   }

   public boolean canBeCarried() {
      return false;
   }
}
