package net.minecraft.dispenser;

import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class DispenserBehaviorPotionProjectile extends BehaviorProjectileDispense {
   // $FF: synthetic field
   final ItemStack potionItemStack;
   // $FF: synthetic field
   final DispenserBehaviorPotion dispenserPotionBehavior;

   DispenserBehaviorPotionProjectile(DispenserBehaviorPotion var1, ItemStack var2) {
      this.dispenserPotionBehavior = var1;
      this.potionItemStack = var2;
   }

   protected IProjectile getProjectileEntity(World var1, IPosition var2) {
      return new EntityPotion(var1, var2.getX(), var2.getY(), var2.getZ(), this.potionItemStack.copy());
   }

   protected float func_82498_a() {
      return super.func_82498_a() * 0.5F;
   }

   protected float func_82500_b() {
      return super.func_82500_b() * 1.25F;
   }
}
