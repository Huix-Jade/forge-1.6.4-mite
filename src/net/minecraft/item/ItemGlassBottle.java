package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.util.Icon;

public class ItemGlassBottle extends Item {
   public ItemGlassBottle(int id) {
      super(id, Material.glass, "potion_bottle_empty");
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabBrewing);
   }

   public Icon getIconFromSubtype(int par1) {
      return Item.potion.getIconFromSubtype(0);
   }

   public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
      RaycastCollision rc = player.getSelectedObject(partial_tick, true);
      if (rc != null && rc.isBlock()) {
         if (rc.getBlockHitMaterial() != Material.water && rc.getNeighborOfBlockHitMaterial() != Material.water) {
            return false;
         } else {
            if (player.onServer()) {
               player.convertOneOfHeldItem(new ItemStack(potion, 1, 0));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }
}
