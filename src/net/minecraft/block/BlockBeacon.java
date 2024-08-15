package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon(int par1) {
      super(par1, Material.glass, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setHardness(3.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityBeacon();
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         TileEntityBeacon tile_entity = (TileEntityBeacon)world.getBlockTileEntity(x, y, z);
         if (tile_entity != null) {
            player.displayGUIBeacon(tile_entity);
         }
      }

      return true;
   }

   public int getRenderType() {
      return 34;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      super.registerIcons(par1IconRegister);
   }
}
