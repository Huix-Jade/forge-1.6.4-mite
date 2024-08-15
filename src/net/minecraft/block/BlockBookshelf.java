package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.Icon;

public class BlockBookshelf extends Block {
   public BlockBookshelf(int par1) {
      super(par1, Material.wood, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      return par1 != 1 && par1 != 0 ? super.getIcon(par1, par2) : Block.planks.getBlockTextureFromSide(par1);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.stick.itemID, 0, 1, 1.5F) + this.dropBlockAsEntityItem(info, Item.paper.itemID, 0, 1, 1.5F) : this.dropBlockAsEntityItem(info, Item.book.itemID, 0, 3, 1.0F);
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.paper, Material.leather});
   }
}
