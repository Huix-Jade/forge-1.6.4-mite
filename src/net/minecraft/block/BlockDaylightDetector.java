package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   private Icon[] iconArray = new Icon[2];

   public BlockDaylightDetector(int par1) {
      super(par1, Material.wood, new BlockConstants());
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.375, 1.0);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   public String getMetadataNotes() {
      return "All bits used for light value";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.375, 1.0);
   }

   public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par1IBlockAccess.getBlockMetadata(par2, par3, par4);
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      return false;
   }

   public boolean onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
      return false;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
   }

   public void updateLightLevel(World par1World, int par2, int par3, int par4) {
      if (!par1World.provider.hasNoSky) {
         int var5 = par1World.getBlockMetadata(par2, par3, par4);
         int var6 = par1World.getSavedLightValue(EnumSkyBlock.Sky, par2, par3, par4) - par1World.skylightSubtracted;
         float var7 = par1World.getCelestialAngleRadians(1.0F);
         if (var7 < 3.1415927F) {
            var7 += (0.0F - var7) * 0.2F;
         } else {
            var7 += (6.2831855F - var7) * 0.2F;
         }

         var6 = Math.round((float)var6 * MathHelper.cos(var7));
         if (var6 < 0) {
            var6 = 0;
         }

         if (var6 > 15) {
            var6 = 15;
         }

         if (var5 != var6) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6, 3);
         }
      }

   }

   public boolean canProvidePower() {
      return true;
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityDaylightDetector();
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.iconArray[0] : this.iconArray[1];
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.iconArray[0] = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.iconArray[1] = par1IconRegister.registerIcon(this.getTextureName() + "_side");
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
