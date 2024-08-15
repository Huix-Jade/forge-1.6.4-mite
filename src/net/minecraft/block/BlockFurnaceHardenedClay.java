package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockFurnaceHardenedClay extends BlockFurnace {
   protected BlockFurnaceHardenedClay(int par1, boolean par2) {
      super(par1, Material.hardened_clay, par2);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace/hardened_clay/side");
      this.furnaceIconFront = par1IconRegister.registerIcon(this.isActive ? "furnace/hardened_clay/front_on" : "furnace/hardened_clay/front_off");
      this.furnaceIconTop = par1IconRegister.registerIcon("furnace/hardened_clay/top");
   }

   public int getIdleBlockID() {
      return Block.furnaceHardenedClayIdle.blockID;
   }

   public int getActiveBlockID() {
      return Block.furnaceHardenedClayBurning.blockID;
   }

   public int getMaxHeatLevel() {
      return 1;
   }

   public boolean isOven() {
      return true;
   }
}
