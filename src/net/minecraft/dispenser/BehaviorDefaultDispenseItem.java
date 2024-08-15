package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
   public boolean suppress_dispense_particles;

   public final ItemStack dispense(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      if (par1IBlockSource.getWorld().isRemote) {
         Minecraft.setErrorMessage("dispense: called on client?");
      }

      ItemStack var3 = this.dispenseStack(par1IBlockSource, par2ItemStack);
      this.playDispenseSound(par1IBlockSource);
      if (this.suppress_dispense_particles) {
         this.suppress_dispense_particles = false;
      } else {
         this.spawnDispenseParticles(par1IBlockSource, BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata()));
      }

      return var3;
   }

   protected ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
      EnumFacing var3 = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
      IPosition var4 = BlockDispenser.getIPositionFromBlockSource(par1IBlockSource);
      ItemStack var5 = par2ItemStack.splitStack(1);
      doDispense(par1IBlockSource.getWorld(), var5, 6, var3, var4);
      return par2ItemStack;
   }

   public static void doDispense(World par0World, ItemStack par1ItemStack, int par2, EnumFacing par3EnumFacing, IPosition par4IPosition) {
      double var5 = par4IPosition.getX();
      double var7 = par4IPosition.getY();
      double var9 = par4IPosition.getZ();
      EntityItem var11 = new EntityItem(par0World, var5, var7 - 0.3, var9, par1ItemStack);
      double var12 = par0World.rand.nextDouble() * 0.1 + 0.2;
      var11.motionX = (double)par3EnumFacing.getFrontOffsetX() * var12;
      var11.motionY = 0.20000000298023224;
      var11.motionZ = (double)par3EnumFacing.getFrontOffsetZ() * var12;
      var11.motionX += par0World.rand.nextGaussian() * 0.007499999832361937 * (double)par2;
      var11.motionY += par0World.rand.nextGaussian() * 0.007499999832361937 * (double)par2;
      var11.motionZ += par0World.rand.nextGaussian() * 0.007499999832361937 * (double)par2;
      par0World.spawnEntityInWorld(var11);
   }

   protected void playDispenseSound(IBlockSource par1IBlockSource) {
      par1IBlockSource.getWorld().playAuxSFX(1000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), 0);
   }

   protected void spawnDispenseParticles(IBlockSource par1IBlockSource, EnumFacing par2EnumFacing) {
      par1IBlockSource.getWorld().playAuxSFX(2000, par1IBlockSource.getXInt(), par1IBlockSource.getYInt(), par1IBlockSource.getZInt(), this.func_82488_a(par2EnumFacing));
   }

   private int func_82488_a(EnumFacing par1EnumFacing) {
      return par1EnumFacing.getFrontOffsetX() + 1 + (par1EnumFacing.getFrontOffsetZ() + 1) * 3;
   }
}
