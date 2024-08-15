package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDye;
import net.minecraft.util.Icon;

public class BlockColored extends Block {
   private Icon[] iconArray;

   public BlockColored(int par1, Material par2Material, BlockConstants block_constants) {
      super(par1, par2Material, block_constants);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      return this.iconArray[par2 % this.iconArray.length];
   }

   public String getMetadataNotes() {
      return "All bits used for color";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata;
   }

   public static int getBlockFromDye(int par0) {
      return ~par0 & 15;
   }

   public static int getDyeFromBlock(int par0) {
      return ~par0 & 15;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray = new Icon[16];

      for(int var2 = 0; var2 < this.iconArray.length; ++var2) {
         this.iconArray[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_" + ItemDye.dyeItemNames[getDyeFromBlock(var2)]);
      }

   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.blockMaterial == Material.hardened_clay ? "colored" : null;
   }
}
