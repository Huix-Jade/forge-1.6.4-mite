package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.Icon;

public class BlockButtonStone extends BlockButton {
   protected BlockButtonStone(int par1) {
      super(par1, false);
   }

   public Icon getIcon(int par1, int par2) {
      return Block.stone.getBlockTextureFromSide(1);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "stone";
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.stone});
   }
}
