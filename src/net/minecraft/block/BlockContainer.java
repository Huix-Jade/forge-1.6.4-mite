package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   protected BlockContainer(int par1, Material par2Material, BlockConstants block_constants) {
      super(par1, par2Material, block_constants);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      super.breakBlock(par1World, par2, par3, par4, par5, par6);
      par1World.removeBlockTileEntity(par2, par3, par4);
   }

   public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
      super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
      TileEntity var7 = par1World.getBlockTileEntity(par2, par3, par4);
      return var7 != null ? var7.receiveClientEvent(par5, par6) : false;
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return false;
   }

   public boolean isPortable(World world, EntityLivingBase entity_living_base, int x, int y, int z) {
      return true;
   }
}
