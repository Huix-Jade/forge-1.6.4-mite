package net.minecraft.dispenser;

import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.world.World;

final class DispenserBehaviorSnowball extends BehaviorProjectileDispense {
   protected IProjectile getProjectileEntity(World var1, IPosition var2) {
      return new EntitySnowball(var1, var2.getX(), var2.getY(), var2.getZ());
   }
}
