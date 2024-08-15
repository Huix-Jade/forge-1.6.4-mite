package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.Icon;

public class BlockButtonWood extends BlockButton {
   protected BlockButtonWood(int par1) {
      super(par1, true);
   }

   public Icon getIcon(int par1, int par2) {
      return Block.planks.getBlockTextureFromSide(1);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "wood";
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood});
   }
}
