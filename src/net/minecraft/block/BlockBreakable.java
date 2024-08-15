package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {
   private boolean localFlag;
   private String breakableBlockIcon;

   protected BlockBreakable(int par1, String par2Str, Material par3Material, boolean par4, BlockConstants block_constants) {
      super(par1, par3Material, block_constants);
      this.localFlag = par4;
      this.breakableBlockIcon = par2Str;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
      return !this.localFlag && var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.breakableBlockIcon);
   }
}
