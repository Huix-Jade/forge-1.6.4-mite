package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      World var3 = par1IBlockSource.getWorld();
      IPosition var4 = BlockDispenser.getIPositionFromBlockSource(par1IBlockSource);
      EnumFacing var5 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      IProjectile var6 = this.getProjectileEntity(var3, var4);
      var6.setThrowableHeading((double)var5.getFrontOffsetX(), (double)((float)var5.getFrontOffsetY() + 0.1F), (double)var5.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
      if (var6 instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)var6;
         arrow.scaleVelocity(2.0F);
      }

      var3.spawnEntityInWorld((Entity)var6);
      par2ItemStack.splitStack(1);
      return par2ItemStack;
   }

   protected void playDispenseSound(IBlockSource par1IBlockSource) {
      par1IBlockSource.getWorld().playAuxSFX(1002, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
   }

   protected abstract IProjectile getProjectileEntity(World var1, IPosition var2);

   protected float func_82498_a() {
      return 6.0F;
   }

   protected float func_82500_b() {
      return 1.1F;
   }
}
