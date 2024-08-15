package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneLogic {
   public static final double[] repeaterTorchOffset = new double[]{-0.0625, 0.0625, 0.1875, 0.3125};
   private static final int[] repeaterState = new int[]{1, 2, 3, 4};

   protected BlockRedstoneRepeater(int par1, boolean par2) {
      super(par1, par2);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bits 4 and 8 used for switch position";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         int metadata = world.getBlockMetadata(x, y, z);
         int var11 = (metadata & 12) >> 2;
         var11 = var11 + 1 << 2 & 12;
         world.setBlockMetadataWithNotify(x, y, z, var11 | metadata & 3, 3);
         world.playSoundAtBlock(x, y, z, "random.click", 0.3F, 0.5F);
      }

      return true;
   }

   protected int func_94481_j_(int par1) {
      return repeaterState[(par1 & 12) >> 2] * 2;
   }

   protected BlockRedstoneLogic func_94485_e() {
      return Block.redstoneRepeaterActive;
   }

   protected BlockRedstoneLogic func_94484_i() {
      return Block.redstoneRepeaterIdle;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.redstoneRepeater.itemID;
   }

   public int getRenderType() {
      return 15;
   }

   public boolean func_94476_e(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return this.func_94482_f(par1IBlockAccess, par2, par3, par4, par5) > 0;
   }

   protected boolean func_94477_d(int par1) {
      return isRedstoneRepeaterBlockID(par1);
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (this.isRepeaterPowered) {
         int var6 = par1World.getBlockMetadata(par2, par3, par4);
         int var7 = j(var6);
         double var8 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2;
         double var10 = (double)((float)par3 + 0.4F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2;
         double var12 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2;
         double var14 = 0.0;
         double var16 = 0.0;
         if (par5Random.nextInt(2) == 0) {
            switch (var7) {
               case 0:
                  var16 = -0.3125;
                  break;
               case 1:
                  var14 = 0.3125;
                  break;
               case 2:
                  var16 = 0.3125;
                  break;
               case 3:
                  var14 = -0.3125;
            }
         } else {
            int var18 = (var6 & 12) >> 2;
            switch (var7) {
               case 0:
                  var16 = repeaterTorchOffset[var18];
                  break;
               case 1:
                  var14 = -repeaterTorchOffset[var18];
                  break;
               case 2:
                  var16 = -repeaterTorchOffset[var18];
                  break;
               case 3:
                  var14 = repeaterTorchOffset[var18];
            }
         }

         par1World.spawnParticle(EnumParticle.reddust, var8 + var14, var10, var12 + var16, 0.0, 0.0, 0.0);
      }

   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      super.breakBlock(par1World, par2, par3, par4, par5, par6);
      this.func_94483_i_(par1World, par2, par3, par4);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.redstoneRepeater);
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return this.isRepeaterPowered ? "active" : "idle";
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard4(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata &= -4;
      metadata |= direction.isSouth() ? 0 : (direction.isWest() ? 1 : (direction.isNorth() ? 2 : (direction.isEast() ? 3 : -1)));
      return metadata;
   }
}
