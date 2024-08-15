package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay(int par1) {
      super(par1, Material.plants);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setCushioning(0.4F);
   }

   public String getMetadataNotes() {
      return "Bit 4 set if aligned WE, and bit 8 set if aligned NS";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata == 0 || metadata == 4 || metadata == 8;
   }

   public int getRenderType() {
      return 31;
   }

   protected Icon getSideIcon(int par1) {
      return this.blockIcon;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_111051_a = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? super.dropBlockAsEntityItem(info, Item.wheat.itemID, 0, 9, 0.5F) : super.dropBlockAsEntityItem(info);
   }
}
