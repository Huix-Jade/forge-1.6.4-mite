package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class BlockWood extends Block implements IBlockWithSubtypes {
   private BlockSubtypes subtypes = new BlockSubtypes(new String[]{"oak", "spruce", "birch", "jungle"});

   public BlockWood(int par1) {
      super(par1, Material.wood, new BlockConstants());
      this.setMaxStackSize(8);
      this.setHardness(BlockHardness.planks);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.subtypes.setIcons(this.registerIcons(par1IconRegister, this.getTextures(), this.getTextureName() + "_"));
   }

   public Icon getIcon(int side, int metadata) {
      return this.subtypes.getIcon(this.getBlockSubtype(metadata));
   }

   public String[] getTextures() {
      return this.subtypes.getTextures();
   }

   public String[] getNames() {
      return this.subtypes.getNames();
   }
}
