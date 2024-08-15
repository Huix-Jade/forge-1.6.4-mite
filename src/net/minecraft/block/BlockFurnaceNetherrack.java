package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockFurnaceNetherrack extends BlockFurnace {
   protected BlockFurnaceNetherrack(int par1, boolean par2) {
      super(par1, Material.netherrack, par2);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace/netherrack/side");
      this.furnaceIconFront = par1IconRegister.registerIcon(this.isActive ? "furnace/netherrack/front_on" : "furnace/netherrack/front_off");
      this.furnaceIconTop = par1IconRegister.registerIcon("furnace/netherrack/top");
   }

   public int getIdleBlockID() {
      return Block.furnaceNetherrackIdle.blockID;
   }

   public int getActiveBlockID() {
      return Block.furnaceNetherrackBurning.blockID;
   }

   public int getMaxHeatLevel() {
      return 4;
   }
}
