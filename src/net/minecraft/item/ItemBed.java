package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;

public class ItemBed extends Item {
   public ItemBed(int par1) {
      super(par1, "bed");
      this.setMaterial(new Material[]{Material.wood, Material.cloth});
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      return rc != null && rc.isBlock() ? player.tryPlaceHeldItemAsBlock(rc, Block.bed) : false;
   }
}
