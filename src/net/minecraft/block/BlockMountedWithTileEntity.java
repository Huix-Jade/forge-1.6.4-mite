package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockMountedWithTileEntity extends BlockMounted implements ITileEntityProvider {
   private Class tile_entity_class;

   protected BlockMountedWithTileEntity(int id, Material material, Class tile_entity_class, BlockConstants constants) {
      super(id, material, constants);
      this.tile_entity_class = tile_entity_class;
   }

   public TileEntity createNewTileEntity(World par1World) {
      try {
         return (TileEntity)this.tile_entity_class.newInstance();
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

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
      return true;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }
}
