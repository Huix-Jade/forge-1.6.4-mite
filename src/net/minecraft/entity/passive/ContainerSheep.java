package net.minecraft.entity.passive;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

class ContainerSheep extends Container {
   final EntitySheep field_90034_a;

   ContainerSheep(EntitySheep par1EntitySheep) {
      super((EntityPlayer)null);
      this.field_90034_a = par1EntitySheep;
   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return false;
   }
}
