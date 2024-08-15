package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class ItemFireball extends Item {
   public ItemFireball(int par1) {
      super(par1, new Material[]{Material.gunpowder, Material.blaze, Material.coal}, "fireball");
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && rc.isNeighborAirBlock()) {
         if (!rc.canPlayerEditNeighborOfBlockHit(player, player.getHeldItemStack())) {
            return false;
         } else {
            if (player.onClient()) {
               player.swingArm();
            } else {
               World world = player.getWorld();
               int x = rc.neighbor_block_x;
               int y = rc.neighbor_block_y;
               int z = rc.neighbor_block_z;
               world.playSoundAtBlock(x, y, z, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
               world.setBlock(x, y, z, Block.fire.blockID);
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
