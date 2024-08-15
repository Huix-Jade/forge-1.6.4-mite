package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockFurnaceClay extends BlockFurnace {
   protected BlockFurnaceClay(int par1, boolean par2) {
      super(par1, Material.clay, par2);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace/clay/side");
      this.furnaceIconFront = par1IconRegister.registerIcon(this.isActive ? "furnace/clay/front_on" : "furnace/clay/front_off");
      this.furnaceIconTop = par1IconRegister.registerIcon("furnace/clay/top");
   }

   public int getIdleBlockID() {
      return Block.furnaceClayIdle.blockID;
   }

   public int getActiveBlockID() {
      return Block.furnaceClayBurning.blockID;
   }

   public int getMaxHeatLevel() {
      return 1;
   }

   public boolean isOven() {
      return true;
   }

   public boolean acceptsLargeItems() {
      return false;
   }
}
