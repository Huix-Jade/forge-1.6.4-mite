package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.mite.MITEConstant;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {
   private Icon cakeTopIcon;
   private Icon cakeBottomIcon;
   private Icon field_94382_c;

   protected BlockCake(int par1) {
      super(par1, Material.cake, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.setTickRandomly(true);
   }

   public String getMetadataNotes() {
      return "Metadata equals number of slices eaten";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < getMaxSlices();
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      float var6 = 0.0625F;
      float var7 = (float)(1 + var5 * 2) / 16.0F;
      float var8 = 0.5F;
      this.setBlockBoundsForCurrentThread((double)var7, 0.0, (double)var6, (double)(1.0F - var6), (double)var8, (double)(1.0F - var6));
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      float var1 = 0.0625F;
      float var2 = 0.5F;
      this.setBlockBoundsForCurrentThread((double)var1, 0.0, (double)var1, (double)(1.0F - var1), (double)var2, (double)(1.0F - var1));
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      int var5 = world.getBlockMetadata(x, y, z);
      float var6 = 0.0625F;
      float var7 = (float)(1 + var5 * 2) / 16.0F;
      float var8 = 0.5F;
      return AxisAlignedBB.getAABBPool().getAABB((double)((float)x + var7), (double)y, (double)((float)z + var6), (double)((float)(x + 1) - var6), (double)((float)y + var8 - var6), (double)((float)(z + 1) - var6));
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      float var6 = 0.0625F;
      float var7 = (float)(1 + var5 * 2) / 16.0F;
      float var8 = 0.5F;
      return AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var6), (double)((float)(par2 + 1) - var6), (double)((float)par3 + var8), (double)((float)(par4 + 1) - var6));
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.cakeTopIcon : (par1 == 0 ? this.cakeBottomIcon : (par2 > 0 && par1 == 4 ? this.field_94382_c : this.blockIcon));
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.field_94382_c = par1IconRegister.registerIcon(this.getTextureName() + "_inner");
      this.cakeTopIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.cakeBottomIcon = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      return this.tryEatCakeSlice(world, x, y, z, player);
   }

   public static final int getMaxSlices() {
      return 6;
   }

   private boolean tryEatCakeSlice(World world, int x, int y, int z, EntityPlayer player) {
      ItemFood cake_slice = (new ItemFood(2, 2, 1000 / getMaxSlices(), true, MITEConstant.egg_has_essential_fats, false)).setPlantProduct().setAnimalProduct();
      if (player.canIngest(cake_slice, 0)) {
         if (player.onServer()) {
            player.addFoodValue(cake_slice);
            world.playSoundAtEntity(player, "random.burp", 0.5F, player.rand.nextFloat() * 0.1F + 0.9F);
            int slices_consumed = world.getBlockMetadata(x, y, z) + 1;
            if (slices_consumed >= getMaxSlices()) {
               world.setBlockToAir(x, y, z);
            } else {
               world.setBlockMetadataWithNotify(x, y, z, slices_consumed, 2);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below != null && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata);
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      return Item.cake.itemID;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.getMetadata() == 0 ? super.dropBlockAsEntityItem(info, Item.cake) : 0;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return metadata == 0;
   }
}
