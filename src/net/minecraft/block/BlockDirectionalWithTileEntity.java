package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockDirectionalWithTileEntity extends BlockDirectional implements ITileEntityProvider {
   protected BlockDirectionalWithTileEntity(int id, Material material, BlockConstants constants) {
      super(id, material, constants);
   }

   public abstract TileEntity createNewTileEntity(World var1);

   public void breakBlock(World world, int x, int y, int z, int block_id, int metadata) {
      super.breakBlock(world, x, y, z, block_id, metadata);
      world.removeBlockTileEntity(x, y, z);
   }

   public boolean onBlockEventReceived(World world, int x, int y, int z, int block_id, int event_id) {
      super.onBlockEventReceived(world, x, y, z, block_id, event_id);
      TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
      return tile_entity != null ? tile_entity.receiveClientEvent(block_id, event_id) : false;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }
}
