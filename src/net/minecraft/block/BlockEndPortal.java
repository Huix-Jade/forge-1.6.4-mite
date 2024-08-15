package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumParticle;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEndPortal extends BlockContainer {
   public static boolean bossDefeated;

   protected BlockEndPortal(int par1, Material par2Material) {
      super(par1, par2Material, (new BlockConstants()).setNeverHidesAdjacentFaces());
      this.setLightValue(1.0F);
      this.setUnlocalizedName("endPortal");
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityEndPortal();
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      float var5 = 0.0625F;
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, (double)var5, 1.0);
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return par5 != 0 ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null && !par1World.isRemote) {
         par5Entity.travelToDimension(1);
      }

   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      double var6 = (double)((float)par2 + par5Random.nextFloat());
      double var8 = (double)((float)par3 + 0.8F);
      double var10 = (double)((float)par4 + par5Random.nextFloat());
      double var12 = 0.0;
      double var14 = 0.0;
      double var16 = 0.0;
      par1World.spawnParticle(EnumParticle.smoke, var6, var8, var10, var12, var14, var16);
   }

   public int getRenderType() {
      return -1;
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (!bossDefeated && par1World.provider.dimensionId != 0) {
         par1World.setBlockToAir(par2, par3, par4);
      }

   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return 0;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon("portal");
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }
}
