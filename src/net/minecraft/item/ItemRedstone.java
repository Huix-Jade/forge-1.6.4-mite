package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;

public class ItemRedstone extends Item {
   public ItemRedstone(int par1) {
      super(par1, Material.redstone, "redstone_dust");
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setCraftingDifficultyAsComponent(25.0F);
      this.setXPReward(5);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      return rc != null && rc.isBlock() ? player.tryPlaceHeldItemAsBlock(rc, Block.redstoneWire) : false;
   }
}
