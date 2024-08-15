package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends BlockDirectional implements IBlockWithPartner {
   private Icon headTexture;
   private static final AxisAlignedBB[] bounds_for_plate = new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0), new AxisAlignedBB(0.0, 0.75, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.25), new AxisAlignedBB(0.0, 0.0, 0.75, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.25, 1.0, 1.0), new AxisAlignedBB(0.75, 0.0, 0.0, 1.0, 1.0, 1.0)};
   private static final AxisAlignedBB[] bounds_for_rod = new AxisAlignedBB[]{new AxisAlignedBB(0.375, 0.25, 0.375, 0.625, 1.0, 0.625), new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 0.75, 0.625), new AxisAlignedBB(0.375, 0.375, 0.25, 0.625, 0.625, 1.0), new AxisAlignedBB(0.375, 0.375, 0.0, 0.625, 0.625, 0.75), new AxisAlignedBB(0.25, 0.375, 0.375, 1.0, 0.625, 0.625), new AxisAlignedBB(0.0, 0.375, 0.375, 0.75, 0.625, 0.625)};
   private static final AxisAlignedBB[][] multiple_bounds = getMultipleBounds();

   public BlockPistonExtension(int par1) {
      super(par1, Material.piston, (new BlockConstants()).setNotAlwaysLegal());
      this.setStepSound(soundStoneFootstep);
      this.setHardness(0.5F);
      this.setUnlocalizedName("pistonExtension");
   }

   public String getMetadataNotes() {
      String[] array = new String[6];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=" + pistonBase.getDirectionFacing(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false) + ", bit 8 set if extension is sticky";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 14;
   }

   public void setHeadTexture(Icon par1Icon) {
      this.headTexture = par1Icon;
   }

   public void clearHeadTexture() {
      this.headTexture = null;
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      int block_id = par1World.getBlockId(par2, par3, par4);
      super.breakBlock(par1World, par2, par3, par4, par5, par6);
      int var7 = Facing.oppositeSide[getDirectionMeta(par6)];
      par2 += Facing.offsetsXForSide[var7];
      par3 += Facing.offsetsYForSide[var7];
      par4 += Facing.offsetsZForSide[var7];
      int var8 = par1World.getBlockId(par2, par3, par4);
      if (var8 == Block.pistonBase.blockID || var8 == Block.pistonStickyBase.blockID) {
         par6 = par1World.getBlockMetadata(par2, par3, par4);
         if (BlockPistonBase.isExtended(par6)) {
            Block.blocksList[var8].dropBlockAsEntityItem((new BlockBreakInfo(par1World, par2, par3, par4)).setNeighborChanged(block_id));
            par1World.setBlockToAir(par2, par3, par4);
         }
      }

   }

   public void onBlockAboutToBeBroken(BlockBreakInfo info) {
      if (info.isResponsiblePlayerInCreativeMode()) {
         int x = info.x;
         int y = info.y;
         int z = info.z;
         int var7 = Facing.oppositeSide[getDirectionMeta(info.getMetadata())];
         x += Facing.offsetsXForSide[var7];
         y += Facing.offsetsYForSide[var7];
         z += Facing.offsetsZForSide[var7];
         int var8 = info.world.getBlockId(x, y, z);
         if ((var8 == Block.pistonBase.blockID || var8 == Block.pistonStickyBase.blockID) && BlockPistonBase.isExtended(info.world.getBlockMetadata(x, y, z))) {
            info.world.setBlockToAir(x, y, z);
         }

      }
   }

   public Icon getIcon(int par1, int par2) {
      int var3 = getDirectionMeta(par2);
      return par1 == var3 ? (this.headTexture != null ? this.headTexture : ((par2 & 8) != 0 ? BlockPistonBase.getPistonBaseIcon("piston_top_sticky") : BlockPistonBase.getPistonBaseIcon("piston_top_normal"))) : (var3 < 6 && par1 == Facing.oppositeSide[var3] ? BlockPistonBase.getPistonBaseIcon("piston_top_normal") : BlockPistonBase.getPistonBaseIcon("piston_side"));
   }

   public void registerIcons(IconRegister par1IconRegister) {
   }

   public int getRenderType() {
      return 17;
   }

   private void setBoundsForPlate(int metadata) {
      this.setBlockBoundsForCurrentThread(bounds_for_plate[getDirectionMeta(metadata)]);
   }

   public static AxisAlignedBB[][] getMultipleBounds() {
      AxisAlignedBB[][] multiple_bounds = new AxisAlignedBB[bounds_for_plate.length][];

      for(int i = 0; i < multiple_bounds.length; ++i) {
         multiple_bounds[i] = new AxisAlignedBB[2];
         multiple_bounds[i][0] = bounds_for_plate[i];
         multiple_bounds[i][1] = bounds_for_rod[i];
      }

      return multiple_bounds;
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return multiple_bounds[getDirectionMeta(world.getBlockMetadata(x, y, z))];
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBoundsForPlate(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else {
         int direction = getDirectionMeta(world.getBlockMetadata(x, y, z));
         int base_x = x - Facing.offsetsXForSide[direction];
         int base_y = y - Facing.offsetsYForSide[direction];
         int base_z = z - Facing.offsetsZForSide[direction];
         Block block = world.getBlock(base_x, base_y, base_z);
         return block.onNeighborBlockChange(world, base_x, base_y, base_z, neighbor_block_id);
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      int direction = getDirectionMeta(world.getBlockMetadata(x, y, z));
      Block block = world.getBlock(x - Facing.offsetsXForSide[direction], y - Facing.offsetsYForSide[direction], z - Facing.offsetsZForSide[direction]);
      return block == pistonBase || block == pistonStickyBase;
   }

   public static int getDirectionMeta(int par0) {
      return par0 & 7;
   }

   public int idPicked(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      return (var5 & 8) != 0 ? Block.pistonStickyBase.blockID : Block.pistonBase.blockID;
   }

   public boolean canBeCarried() {
      return false;
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.isResponsiblePlayerInCreativeMode()) {
         int var7 = getDirectionMeta(info.getMetadata());
         int var8 = info.world.getBlockId(info.x - Facing.offsetsXForSide[var7], info.y - Facing.offsetsYForSide[var7], info.z - Facing.offsetsZForSide[var7]);
         if (var8 == Block.pistonBase.blockID || var8 == Block.pistonStickyBase.blockID) {
            info.world.setBlockToAir(info.x - Facing.offsetsXForSide[var7], info.y - Facing.offsetsYForSide[var7], info.z - Facing.offsetsZForSide[var7]);
         }
      }

      return 0;
   }

   public String getNameDisambiguationForReferenceFile(int metadata) {
      return "extension";
   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return pistonBase.getDirectionFacing(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      return pistonBase.getMetadataForDirectionFacing(metadata, direction);
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean canSupportEntityShadow(int metadata) {
      return this.getDirectionFacing(metadata).isUp();
   }

   public int getPartnerOffsetX(int metadata) {
      return -this.getDirectionFacing(metadata).dx;
   }

   public int getPartnerOffsetY(int metadata) {
      return -this.getDirectionFacing(metadata).dy;
   }

   public int getPartnerOffsetZ(int metadata) {
      return -this.getDirectionFacing(metadata).dz;
   }

   public boolean requiresPartner(int metadata) {
      return true;
   }

   public boolean isPartner(int metadata, Block neighbor_block, int neighbor_block_metadata) {
      return neighbor_block instanceof BlockPistonBase && ((BlockPistonBase)neighbor_block).getDirectionFacing(neighbor_block_metadata) == this.getDirectionFacing(metadata);
   }

   public boolean partnerDropsAsItem(int metadata) {
      return true;
   }
}
