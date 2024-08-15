package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockComparator extends BlockRedstoneLogic implements ITileEntityProvider {
   public BlockComparator(int par1, boolean par2) {
      super(par1, par2);
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bit 4 set if toggled, and bit 8 set if powered";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.comparator.itemID;
   }

   protected int func_94481_j_(int par1) {
      return 2;
   }

   protected BlockRedstoneLogic func_94485_e() {
      return Block.redstoneComparatorActive;
   }

   protected BlockRedstoneLogic func_94484_i() {
      return Block.redstoneComparatorIdle;
   }

   public int getRenderType() {
      return 37;
   }

   public Icon getIcon(int par1, int par2) {
      boolean var3 = this.isRepeaterPowered || (par2 & 8) != 0;
      return par1 == 0 ? (var3 ? Block.torchRedstoneActive.getBlockTextureFromSide(par1) : Block.torchRedstoneIdle.getBlockTextureFromSide(par1)) : (par1 == 1 ? (var3 ? Block.redstoneComparatorActive.blockIcon : this.blockIcon) : Block.stoneDoubleSlab.getBlockTextureFromSide(1));
   }

   protected boolean func_96470_c(int par1) {
      return this.isRepeaterPowered || (par1 & 8) != 0;
   }

   protected int func_94480_d(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return this.getTileEntityComparator(par1IBlockAccess, par2, par3, par4).getOutputSignal();
   }

   private int getOutputStrength(World par1World, int par2, int par3, int par4, int par5) {
      return !this.func_94490_c(par5) ? this.getInputStrength(par1World, par2, par3, par4, par5) : Math.max(this.getInputStrength(par1World, par2, par3, par4, par5) - this.func_94482_f(par1World, par2, par3, par4, par5), 0);
   }

   public boolean func_94490_c(int par1) {
      return (par1 & 4) == 4;
   }

   protected boolean isGettingInput(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = this.getInputStrength(par1World, par2, par3, par4, par5);
      if (var6 >= 15) {
         return true;
      } else if (var6 == 0) {
         return false;
      } else {
         int var7 = this.func_94482_f(par1World, par2, par3, par4, par5);
         return var7 == 0 ? true : var6 >= var7;
      }
   }

   protected int getInputStrength(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = super.getInputStrength(par1World, par2, par3, par4, par5);
      int var7 = j(par5);
      int var8 = par2 + Direction.offsetX[var7];
      int var9 = par4 + Direction.offsetZ[var7];
      int var10 = par1World.getBlockId(var8, par3, var9);
      if (var10 > 0) {
         if (Block.blocksList[var10].hasComparatorInputOverride()) {
            var6 = Block.blocksList[var10].getComparatorInputOverride(par1World, var8, par3, var9, Direction.rotateOpposite[var7]);
         } else if (var6 < 15 && Block.isNormalCube(var10)) {
            var8 += Direction.offsetX[var7];
            var9 += Direction.offsetZ[var7];
            var10 = par1World.getBlockId(var8, par3, var9);
            if (var10 > 0 && Block.blocksList[var10].hasComparatorInputOverride()) {
               var6 = Block.blocksList[var10].getComparatorInputOverride(par1World, var8, par3, var9, Direction.rotateOpposite[var7]);
            }
         }
      }

      return var6;
   }

   public TileEntityComparator getTileEntityComparator(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      return (TileEntityComparator)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         int metadata = world.getBlockMetadata(x, y, z);
         boolean var11 = this.isRepeaterPowered | (metadata & 8) != 0;
         boolean var12 = !this.func_94490_c(metadata);
         int var13 = var12 ? 4 : 0;
         var13 |= var11 ? 8 : 0;
         world.playSoundAtBlock(x, y, z, "random.click", 0.3F, var12 ? 0.55F : 0.5F);
         world.setBlockMetadataWithNotify(x, y, z, var13 | metadata & 3, 2);
         this.func_96476_c(world, x, y, z, world.rand);
      }

      return true;
   }

   protected void func_94479_f(World par1World, int par2, int par3, int par4, int par5) {
      if (!par1World.isBlockTickScheduledThisTick(par2, par3, par4, this.blockID)) {
         int var6 = par1World.getBlockMetadata(par2, par3, par4);
         int var7 = this.getOutputStrength(par1World, par2, par3, par4, var6);
         int var8 = this.getTileEntityComparator(par1World, par2, par3, par4).getOutputSignal();
         if (var7 != var8 || this.func_96470_c(var6) != this.isGettingInput(par1World, par2, par3, par4, var6)) {
            if (this.func_83011_d(par1World, par2, par3, par4, var6)) {
               par1World.scheduleBlockUpdateWithPriority(par2, par3, par4, this.blockID, this.func_94481_j_(0), -1);
            } else {
               par1World.scheduleBlockUpdateWithPriority(par2, par3, par4, this.blockID, this.func_94481_j_(0), 0);
            }
         }
      }

   }

   private boolean func_96476_c(World par1World, int par2, int par3, int par4, Random par5Random) {
      int var6 = par1World.getBlockMetadata(par2, par3, par4);
      int var7 = this.getOutputStrength(par1World, par2, par3, par4, var6);
      int var8 = this.getTileEntityComparator(par1World, par2, par3, par4).getOutputSignal();
      this.getTileEntityComparator(par1World, par2, par3, par4).setOutputSignal(var7);
      if (var8 == var7 && this.func_94490_c(var6)) {
         return false;
      } else {
         boolean var9 = this.isGettingInput(par1World, par2, par3, par4, var6);
         boolean var10 = this.isRepeaterPowered || (var6 & 8) != 0;
         boolean changed_state;
         if (var10 && !var9) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 & -9, 2);
            changed_state = true;
         } else if (!var10 && var9) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 8, 2);
            changed_state = true;
         } else {
            changed_state = false;
         }

         this.func_94483_i_(par1World, par2, par3, par4);
         return changed_state;
      }
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (this.isRepeaterPowered) {
         int metadata = world.getBlockMetadata(x, y, z);
         world.setBlock(x, y, z, this.func_94484_i().blockID, metadata | 8, 4);
         return true;
      } else {
         return this.func_96476_c(world, x, y, z, random);
      }
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
      par1World.setBlockTileEntity(par2, par3, par4, this.createNewTileEntity(par1World));
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      super.breakBlock(par1World, par2, par3, par4, par5, par6);
      par1World.removeBlockTileEntity(par2, par3, par4);
      this.func_94483_i_(par1World, par2, par3, par4);
   }

   public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
      super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
      TileEntity var7 = par1World.getBlockTileEntity(par2, par3, par4);
      return var7 != null ? var7.receiveClientEvent(par5, par6) : false;
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityComparator();
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Item.comparator);
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
