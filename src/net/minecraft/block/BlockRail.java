package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {
   private Icon theIcon;

   protected BlockRail(int par1) {
      super(par1, false);
   }

   public String getMetadataNotes() {
      return "0=Flat NS, 1=Flat EW, 2=Inclined East, 3=Inclined West, 4=Inclined North, 5=Inclined South, 6=Curved SE, 7=Curved SW, 8=Curved NW, 9=Curved NE";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 10;
   }

   public Icon getIcon(int par1, int par2) {
      return par2 >= 6 ? this.theIcon : this.blockIcon;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
      this.theIcon = par1IconRegister.registerIcon(this.getTextureName() + "_turned");
   }

   protected void func_94358_a(World par1World, int par2, int par3, int par4, int par5, int par6, int par7) {
      if (par7 > 0 && Block.blocksList[par7].canProvidePower() && (new BlockBaseRailLogic(this, par1World, par2, par3, par4)).getNumberOfAdjacentTracks() == 3) {
         this.refreshTrackShape(par1World, par2, par3, par4, false);
      }

   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.iron, Material.wood});
   }
}
