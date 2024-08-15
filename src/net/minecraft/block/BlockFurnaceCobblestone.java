package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockFurnaceCobblestone extends BlockFurnace {
   protected BlockFurnaceCobblestone(int par1, boolean par2) {
      super(par1, Material.stone, par2);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace/cobblestone/side");
      this.furnaceIconFront = par1IconRegister.registerIcon(this.isActive ? "furnace/cobblestone/front_on" : "furnace/cobblestone/front_off");
      this.furnaceIconTop = par1IconRegister.registerIcon("furnace/cobblestone/top");
   }

   public int getIdleBlockID() {
      return Block.furnaceIdle.blockID;
   }

   public int getActiveBlockID() {
      return Block.furnaceBurning.blockID;
   }

   public int getMaxHeatLevel() {
      return 2;
   }
}
