package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class DispenserBehaviorTNT extends BehaviorDefaultDispenseItem {
   protected ItemStack dispenseStack(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.getFacing(var1.getBlockMetadata());
      World var4 = var1.getWorld();
      int var5 = var1.getXInt() + var3.getFrontOffsetX();
      int var6 = var1.getYInt() + var3.getFrontOffsetY();
      int var7 = var1.getZInt() + var3.getFrontOffsetZ();
      EntityTNTPrimed var8 = new EntityTNTPrimed(var4, (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), (double)((float)var7 + 0.5F), (EntityLivingBase)null);
      var4.spawnEntityInWorld(var8);
      --var2.stackSize;
      return var2;
   }
}
