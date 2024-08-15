package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   public EntityMinecartEmpty(World par1World) {
      super(par1World);
   }

   public EntityMinecartEmpty(World par1World, double par2, double par4, double par6) {
      super(par1World, par2, par4, par6);
   }

   public boolean onEntityRightClicked(EntityPlayer player, ItemStack item_stack) {
      if (this.riddenByEntity != null) {
         return false;
      } else {
         if (player.onServer()) {
            player.mountEntity(this);
         }

         return true;
      }
   }

   public int getMinecartType() {
      return 0;
   }

   public Item getModelItem() {
      return Item.minecartEmpty;
   }
}
