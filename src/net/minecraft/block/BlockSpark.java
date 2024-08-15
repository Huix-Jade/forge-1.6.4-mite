package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockSpark extends BlockFire {
   protected BlockSpark(int id) {
      super(id);
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return null;
   }

   public boolean isValidMetadata(int metadata) {
      return metadata == 0;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (!Block.portal.tryToCreatePortal(par1World, par2, par3, par4)) {
         par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 2);
      }
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (!this.canNeighborBurn(par1World, par2, par3, par4) && par1World.getBlock(par2, par3 - 1, par4) != Block.netherrack) {
         par1World.setBlockToAir(par2, par3, par4);
      } else {
         par1World.setBlock(par2, par3, par4, Block.fire.blockID);
      }

      return true;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName());
   }

   public Icon getFireIcon(int par1) {
      return this.blockIcon;
   }

   public Icon getIcon(int par1, int par2) {
      return this.blockIcon;
   }
}
