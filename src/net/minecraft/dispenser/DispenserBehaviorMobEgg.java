package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumFacing;

final class DispenserBehaviorMobEgg extends BehaviorDefaultDispenseItem {
   public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      EnumFacing var3 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      double var4 = par1IBlockSource.getX() + (double)var3.getFrontOffsetX();
      double var6 = (double)((float)par1IBlockSource.getYInt() + 0.2F);
      double var8 = par1IBlockSource.getZ() + (double)var3.getFrontOffsetZ();
      Entity var10 = ItemMonsterPlacer.spawnCreature(par1IBlockSource.getWorld(), par2ItemStack.getItemSubtype(), var4, var6, var8, false, (EnumFace)null);
      if (var10 instanceof EntityLivingBase && par2ItemStack.hasDisplayName()) {
         ((EntityLiving)var10).setCustomNameTag(par2ItemStack.getDisplayName());
      }

      par2ItemStack.splitStack(1);
      return par2ItemStack;
   }
}
