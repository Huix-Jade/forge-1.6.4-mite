package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumFace;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {
   public BlockCommandBlock(int par1) {
      super(par1, Material.iron, new BlockConstants());
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityCommandBlock();
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      boolean is_indirectly_powered = world.isBlockIndirectlyGettingPowered(x, y, z);
      int metadata = world.getBlockMetadata(x, y, z);
      boolean var8 = (metadata & 1) != 0;
      if (is_indirectly_powered && !var8) {
         world.setBlockMetadataWithNotify(x, y, z, metadata | 1, 4);
         world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
         return true;
      } else if (!is_indirectly_powered && var8) {
         world.setBlockMetadataWithNotify(x, y, z, metadata & -2, 4);
         return true;
      } else {
         return false;
      }
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
      if (!(tile_entity instanceof TileEntityCommandBlock)) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         TileEntityCommandBlock tile_entity_command_block = (TileEntityCommandBlock)tile_entity;
         tile_entity_command_block.setSignalStrength(tile_entity_command_block.executeCommandOnPowered(world));
         world.func_96440_m(x, y, z, this.blockID);
         return world.getBlock(x, y, z) != this || world.getBlockMetadata(x, y, z) != metadata;
      }
   }

   public int tickRate(World par1World) {
      return 1;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float dx, float dy, float dz) {
      if (player.onServer()) {
         TileEntityCommandBlock tile_entity = (TileEntityCommandBlock)world.getBlockTileEntity(x, y, z);
         if (tile_entity != null) {
            player.displayGUIEditSign(tile_entity);
         }
      }

      return true;
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      TileEntity var6 = par1World.getBlockTileEntity(par2, par3, par4);
      return var6 != null && var6 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var6).getSignalStrength() : 0;
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer instanceof EntityLivingBase) {
         ItemStack item_stack = placer.getAsEntityLivingBase().getHeldItemStack();
         if (item_stack.hasDisplayName()) {
            ((TileEntityCommandBlock)world.getBlockTileEntity(x, y, z)).setCommandSenderName(item_stack.getDisplayName());
         }
      }

      return super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }
}
