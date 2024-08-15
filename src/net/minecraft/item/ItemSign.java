package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;

public class ItemSign extends Item {
   public ItemSign(int par1) {
      super(par1, Material.wood, "sign");
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      return rc != null && rc.isBlock() ? player.tryPlaceHeldItemAsBlock(rc, rc.face_hit.isTop() ? Block.signPost : Block.signWall) : false;
   }
}
