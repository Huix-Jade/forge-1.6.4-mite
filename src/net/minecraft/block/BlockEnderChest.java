package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockDirectionalWithTileEntity {
   protected BlockEnderChest(int par1) {
      super(par1, Material.stone, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBoundsForAllThreads(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + 2 + "=" + this.getDirectionFacing(i + 2).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata > 1 && metadata < 6;
   }

   public int getRenderType() {
      return 22;
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard6(metadata, false);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata = direction.isNorth() ? 2 : (direction.isSouth() ? 3 : (direction.isWest() ? 4 : (direction.isEast() ? 5 : -1)));
      return metadata;
   }

   public boolean canOpenChest(World world, int x, int y, int z, EntityLivingBase entity_living_base) {
      return world.isAirOrPassableBlock(x, y + 1, z, true);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (player.onServer()) {
         if (this.canOpenChest(world, x, y, z, player)) {
            InventoryEnderChest inventory = player.getInventoryEnderChest();
            TileEntityEnderChest tile_entity = (TileEntityEnderChest)world.getBlockTileEntity(x, y, z);
            if (inventory != null && tile_entity != null) {
               inventory.setAssociatedChest(tile_entity);
               player.displayGUIChest(x, y, z, inventory);
            }
         } else {
            world.playSoundAtBlock(x, y, z, "imported.random.chest_locked", 0.2F);
         }
      }

      return true;
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityEnderChest();
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      for(int var6 = 0; var6 < 3; ++var6) {
         double var10000 = (double)((float)par2 + par5Random.nextFloat());
         double var9 = (double)((float)par3 + par5Random.nextFloat());
         var10000 = (double)((float)par4 + par5Random.nextFloat());
         double var13 = 0.0;
         double var15 = 0.0;
         double var17 = 0.0;
         int var19 = par5Random.nextInt(2) * 2 - 1;
         int var20 = par5Random.nextInt(2) * 2 - 1;
         var13 = ((double)par5Random.nextFloat() - 0.5) * 0.125;
         var15 = ((double)par5Random.nextFloat() - 0.5) * 0.125;
         var17 = ((double)par5Random.nextFloat() - 0.5) * 0.125;
         double var11 = (double)par4 + 0.5 + 0.25 * (double)var20;
         var17 = (double)(par5Random.nextFloat() * 1.0F * (float)var20);
         double var7 = (double)par2 + 0.5 + 0.25 * (double)var19;
         var13 = (double)(par5Random.nextFloat() * 1.0F * (float)var19);
         par1World.spawnParticle(EnumParticle.portal_underworld, var7, var9, var11, var13, var15, var17);
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("obsidian");
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Block.obsidian.blockID, 0, 8, 1.0F);
   }

   public boolean playerSwingsOnBlockActivated(boolean empty_handed) {
      return empty_handed;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
