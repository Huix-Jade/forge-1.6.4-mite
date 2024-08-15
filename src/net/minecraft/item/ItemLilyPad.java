package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;

public class ItemLilyPad extends ItemBlock {
   public ItemLilyPad(Block block) {
      super(block);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         return rc.face_hit.isTop() && rc.isBlockHitFullWaterBlock(false) ? player.tryPlaceHeldItemAsBlock(rc, Block.waterlily) : false;
      } else {
         return false;
      }
   }

   public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
      return Block.waterlily.getRenderColor(par1ItemStack.getItemSubtype());
   }
}
