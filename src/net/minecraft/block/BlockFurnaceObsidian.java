package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockFurnaceObsidian extends BlockFurnace {
   protected BlockFurnaceObsidian(int par1, boolean par2) {
      super(par1, Material.obsidian, par2);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("furnace/obsidian/side");
      this.furnaceIconFront = par1IconRegister.registerIcon(this.isActive ? "furnace/obsidian/front_on" : "furnace/obsidian/front_off");
      this.furnaceIconTop = par1IconRegister.registerIcon("furnace/obsidian/top");
   }

   public int getIdleBlockID() {
      return Block.furnaceObsidianIdle.blockID;
   }

   public int getActiveBlockID() {
      return Block.furnaceObsidianBurning.blockID;
   }

   public int getMaxHeatLevel() {
      return 3;
   }
}
