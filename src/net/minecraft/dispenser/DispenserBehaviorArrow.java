package net.minecraft.dispenser;

import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.world.World;

public final class DispenserBehaviorArrow extends BehaviorProjectileDispense {
   public ItemArrow item_arrow;

   public DispenserBehaviorArrow(ItemArrow item_arrow) {
      this.item_arrow = item_arrow;
   }

   protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
      EntityArrow var3 = new EntityArrow(par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ(), this.item_arrow, false);
      var3.canBePickedUp = 1;
      var3.shot_by_dispenser = true;
      return var3;
   }
}
