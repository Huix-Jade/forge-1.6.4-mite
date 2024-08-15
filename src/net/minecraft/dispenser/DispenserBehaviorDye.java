package net.minecraft.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class DispenserBehaviorDye extends BehaviorDefaultDispenseItem {
   private boolean field_96461_b = true;

   protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      if (par2ItemStack.getItemSubtype() != 15) {
         return super.dispenseStack(par1IBlockSource, par2ItemStack);
      } else {
         EnumFacing var3 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
         World var4 = par1IBlockSource.getWorld();
         int var5 = par1IBlockSource.getXInt() + var3.getFrontOffsetX();
         int var6 = par1IBlockSource.getYInt() + var3.getFrontOffsetY();
         int var7 = par1IBlockSource.getZInt() + var3.getFrontOffsetZ();
         boolean fertilization_successful = false;

         for(int dy = 0; dy >= -1 && (dy != 0 || var4.isAirOrPassableBlock(var5, var6 + dy, var7, false)); --dy) {
            if (ItemDye.tryFertilize(par2ItemStack, var4, var5, var6 + dy, var7, EnumFace.TOP)) {
               fertilization_successful = true;
               var6 += dy;
               break;
            }

            if (dy < 0 && !var4.isAirOrPassableBlock(var5, var6 + dy, var7, false)) {
               break;
            }
         }

         if (fertilization_successful) {
            --par2ItemStack.stackSize;
            if (!var4.isRemote) {
               if (var4.getBlockId(var5, var6, var7) == Block.tilledField.blockID) {
                  ++var6;
               }

               var4.playAuxSFX(2005, var5, var6, var7, 0);
            }
         } else {
            this.field_96461_b = false;
         }

         return par2ItemStack;
      }
   }

   protected void playDispenseSound(IBlockSource par1IBlockSource) {
      if (this.field_96461_b) {
         par1IBlockSource.getWorld().playAuxSFX(1000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
      } else {
         par1IBlockSource.getWorld().playAuxSFX(1001, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
      }

   }
}
