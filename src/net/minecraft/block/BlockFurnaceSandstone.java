package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockFurnaceSandstone extends BlockFurnace {
   protected BlockFurnaceSandstone(int par1, boolean par2) {
      super(par1, Material.sand, par2);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace/sandstone/side");
      this.furnaceIconFront = par1IconRegister.registerIcon(this.isActive ? "furnace/sandstone/front_on" : "furnace/sandstone/front_off");
      this.furnaceIconTop = par1IconRegister.registerIcon("furnace/sandstone/top");
   }

   public int getIdleBlockID() {
      return Block.furnaceSandstoneIdle.blockID;
   }

   public int getActiveBlockID() {
      return Block.furnaceSandstoneBurning.blockID;
   }

   public int getMaxHeatLevel() {
      return 1;
   }

   public boolean isOven() {
      return true;
   }
}
