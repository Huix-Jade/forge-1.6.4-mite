package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class hangingEntityClass;

   public ItemHangingEntity(int par1, Class par2Class, String texture) {
      super(par1, texture);
      this.hangingEntityClass = par2Class;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && rc.face_hit.isSide() && rc.isNeighborAirBlock() && rc.canPlayerEditBlockHit(player, player.getHeldItemStack())) {
         World world = player.getWorld();
         EntityHanging hanging_entity = this.createHangingEntity(world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, Direction.facingToDirection[rc.face_hit.ordinal()]);
         if (hanging_entity != null && hanging_entity.onValidSurface()) {
            if (player.onClient()) {
               player.swingArm();
            } else {
               world.spawnEntityInWorld(hanging_entity);
               if (!player.inCreativeMode()) {
                  player.convertOneOfHeldItem((ItemStack)null);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private EntityHanging createHangingEntity(World par1World, int par2, int par3, int par4, int par5) {
      return (EntityHanging)(this.hangingEntityClass == EntityPainting.class ? new EntityPainting(par1World, par2, par3, par4, par5) : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(par1World, par2, par3, par4, par5) : null));
   }
}
