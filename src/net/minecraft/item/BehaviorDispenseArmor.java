package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySelectorArmoredMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

final class BehaviorDispenseArmor extends BehaviorDefaultDispenseItem {
   protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      EnumFacing var3 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      int var4 = par1IBlockSource.getXInt() + var3.getFrontOffsetX();
      int var5 = par1IBlockSource.getYInt() + var3.getFrontOffsetY();
      int var6 = par1IBlockSource.getZInt() + var3.getFrontOffsetZ();
      AxisAlignedBB var7 = AxisAlignedBB.getAABBPool().getAABB((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1));
      List var8 = par1IBlockSource.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, var7, new EntitySelectorArmoredMob(par2ItemStack));
      if (var8.size() > 0) {
         EntityLivingBase var9 = (EntityLivingBase)var8.get(0);
         int var10 = var9 instanceof EntityPlayer ? 1 : 0;
         int var11 = EntityLiving.getEquipmentPosition(par2ItemStack);
         ItemStack var12 = par2ItemStack.copy();
         var12.stackSize = 1;
         var9.setCurrentItemOrArmor(var11, var12);  //BUGFIX Forge: Vanilla bug fix associated with fixed setCurrentItemOrArmor indexs for players.
//         var9.setCurrentItemOrArmor(var11 - var10, var12);
         if (var9 instanceof EntityLiving) {
            ((EntityLiving)var9).setEquipmentDropChance(var11, 2.0F);
         }

         --par2ItemStack.stackSize;
         return par2ItemStack;
      } else {
         return super.dispenseStack(par1IBlockSource, par2ItemStack);
      }
   }
}
