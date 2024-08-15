package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   private Icon field_94400_a;
   private Icon field_94399_b;
   private static final AxisAlignedBB[] multiple_bounds_without_eye = new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0)};
   private static final AxisAlignedBB[] multiple_bounds_with_eye = new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0), new AxisAlignedBB(0.25, 0.8125, 0.25, 0.75, 1.0, 0.75)};

   public BlockEndPortalFrame(int par1) {
      super(par1, Material.stone, (new BlockConstants()).setAlwaysImmutable());
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for orientation, bit 4 set if ender eye inserted";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 8;
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.field_94400_a : (par1 == 0 ? Block.whiteStone.getBlockTextureFromSide(par1) : this.blockIcon);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.field_94400_a = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.field_94399_b = par1IconRegister.registerIcon(this.getTextureName() + "_eye");
   }

   public Icon func_94398_p() {
      return this.field_94399_b;
   }

   public int getRenderType() {
      return 26;
   }

   public void setBlockBoundsForItemRender(int item_damage) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return isEnderEyeInserted(world.getBlockMetadata(x, y, z)) ? multiple_bounds_with_eye : multiple_bounds_without_eye;
   }

   public static boolean isEnderEyeInserted(int par0) {
      return (par0 & 4) != 0;
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!test_only && placer != null) {
         int placer_direction = ((MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5) & 3) + 2) % 4;
         world.setBlockMetadataWithNotify(x, y, z, placer_direction, 2);
      }

      return super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      int var6 = par1World.getBlockMetadata(par2, par3, par4);
      return isEnderEyeInserted(var6) ? 15 : 0;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return 0;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "frame";
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
