package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDispenseMinecart();
   public int minecartType;

   public ItemMinecart(int par1, int par2, String texture) {
      super(par1, Material.iron, texture);
      this.setMaxStackSize(1);
      this.minecartType = par2;
      this.setCreativeTab(CreativeTabs.tabTransport);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
      if (par2 == 1) {
         this.addMaterial(new Material[]{Material.wood});
      } else if (par2 == 2) {
         this.addMaterial(new Material[]{Material.stone});
      } else if (par2 == 3) {
         this.addMaterial(new Material[]{Material.tnt});
      }

   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, false);
      if (rc != null && rc.isBlock() && BlockRailBase.isRailBlock(rc.getBlockHitID()) && rc.canPlayerEditBlockHit(player, player.getHeldItemStack())) {
         if (player.onClient()) {
            player.swingArm();
         } else {
            World world = player.getWorld();
            EntityMinecart minecart = EntityMinecart.createMinecart(world, (double)((float)rc.block_hit_x + 0.5F), (double)((float)rc.block_hit_y + 0.5F), (double)((float)rc.block_hit_z + 0.5F), this.minecartType);
            ItemStack item_stack = player.getHeldItemStack();
            if (item_stack.hasDisplayName()) {
               minecart.setMinecartName(item_stack.getDisplayName());
            }

            world.spawnEntityInWorld(minecart);
            Block.rail.makeSoundWhenPlaced(world, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, 0);
            if (!player.inCreativeMode()) {
               player.convertOneOfHeldItem((ItemStack)null);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public String getNameDisambiguationForReferenceFile(int subtype) {
      return this.minecartType == 0 ? "empty" : null;
   }
}
