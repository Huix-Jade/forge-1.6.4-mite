package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumFace;

public class ItemSnowBlock extends ItemBlock {
   public ItemSnowBlock(Block block) {
      super(block);
   }

   public boolean tryPlaceAsBlock(RaycastCollision rc, Block block, EntityPlayer player, ItemStack item_stack) {
      if (block != Block.blockSnow) {
         Minecraft.setErrorMessage("tryPlaceAsBlock: block!=Block.blockSnow");
      }

      if (block == Block.blockSnow && !block.canReplaceBlock(block.getMetadataForPlacement(player.worldObj, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, item_stack, player, EnumFace.TOP, 0.0F, 0.0F, 0.0F), rc.getBlockHit(), rc.block_hit_metadata)) {
         return ItemSnow.tryAddToExistingSnow(rc, player, 8) || super.tryPlaceAsBlock(rc, block, player, item_stack);
      } else {
         return super.tryPlaceAsBlock(rc, block, player, item_stack);
      }
   }
}
