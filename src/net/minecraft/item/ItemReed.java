package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.mite.Skill;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.EnumFace;

public class ItemReed extends Item {
   private int spawnID;

   public ItemReed(int par1, Block par2Block, String texture) {
      super(par1, par2Block.blockMaterial, texture);
      this.spawnID = par2Block.blockID;
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock()) {
         return player.worldObj.areSkillsEnabled() && this.spawnID == Block.reed.blockID && !player.hasSkill(Skill.FARMING) ? false : player.tryPlaceHeldItemAsBlock(rc, Block.getBlock(this.spawnID));
      } else {
         return false;
      }
   }

   public boolean tryPlaceAsBlock(RaycastCollision rc, Block block, EntityPlayer player, ItemStack item_stack) {
      if (block == Block.reed) {
         int x;
         int y;
         int z;
         if (rc.getBlockHit() == block) {
            x = rc.block_hit_x;
            y = rc.block_hit_y;
            z = rc.block_hit_z;
            ++y;
            if (Block.reed.tryPlaceBlock(rc.world, x, y, z, EnumFace.TOP, 0, player, true, true)) {
               return true;
            }
         }

         x = rc.neighbor_block_x;
         y = rc.neighbor_block_y;
         z = rc.neighbor_block_z;
         if (rc.world.getBlock(x, y, z) != block) {
            return super.tryPlaceAsBlock(rc, block, player, item_stack);
         }

         if (block.isLegalAt(rc.world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, 0) && block.canReplaceBlock(0, rc.getBlockHit(), rc.block_hit_metadata)) {
            return super.tryPlaceAsBlock(rc, block, player, item_stack);
         }

         ++y;
         if (Block.reed.tryPlaceBlock(rc.world, x, y, z, EnumFace.TOP, 0, player, true, true)) {
            return true;
         }
      }

      return super.tryPlaceAsBlock(rc, block, player, item_stack);
   }
}
