package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {
   public BlockNote(int par1) {
      super(par1, Material.wood, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      boolean is_indirectly_powered = world.isBlockIndirectlyGettingPowered(x, y, z);
      TileEntityNote tile_entity = (TileEntityNote)world.getBlockTileEntity(x, y, z);
      if (tile_entity != null && tile_entity.previousRedstoneState != is_indirectly_powered) {
         if (is_indirectly_powered) {
            tile_entity.triggerNote(world, x, y, z);
         }

         tile_entity.previousRedstoneState = is_indirectly_powered;
      }

      return false;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         TileEntityNote tile_entity = (TileEntityNote)world.getBlockTileEntity(x, y, z);
         if (tile_entity != null) {
            tile_entity.changePitch();
            tile_entity.triggerNote(world, x, y, z);
         }
      }

      return true;
   }

   public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
      if (!par1World.isRemote) {
         TileEntityNote var6 = (TileEntityNote)par1World.getBlockTileEntity(par2, par3, par4);
         if (var6 != null) {
            var6.triggerNote(par1World, par2, par3, par4);
         }
      }

   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityNote();
   }

   public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
      float var7 = (float)Math.pow(2.0, (double)(par6 - 12) / 12.0);
      String var8 = "harp";
      if (par5 == 1) {
         var8 = "bd";
      }

      if (par5 == 2) {
         var8 = "snare";
      }

      if (par5 == 3) {
         var8 = "hat";
      }

      if (par5 == 4) {
         var8 = "bassattack";
      }

      par1World.playSoundEffect((double)par2 + 0.5, (double)par3 + 0.5, (double)par4 + 0.5, "note." + var8, 3.0F, var7);
      par1World.spawnParticle(EnumParticle.note, (double)par2 + 0.5, (double)par3 + 1.2, (double)par4 + 0.5, (double)par6 / 24.0, 0.0, 0.0);
      return true;
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.wood, Material.redstone});
   }
}
