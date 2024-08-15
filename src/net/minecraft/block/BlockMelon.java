package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class BlockMelon extends Block {
   private Icon theIcon;

   protected BlockMelon(int par1) {
      super(par1, Material.pumpkin, new BlockConstants());
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      return par1 != 1 && par1 != 0 ? this.blockIcon : this.theIcon;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.melonSeeds) : this.dropBlockAsEntityItem(info, Item.melon.itemID, 0, 4, 1.0F);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.theIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
   }
}
