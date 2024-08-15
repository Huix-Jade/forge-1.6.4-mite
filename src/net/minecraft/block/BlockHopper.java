package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.raycast.Raycast;
import net.minecraft.raycast.RaycastCollision;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   private final Random field_94457_a = new Random();
   private Icon hopperIcon;
   private Icon hopperTopIcon;
   private Icon hopperInsideIcon;
   private static final AxisAlignedBB[] multiple_bounds = getMultipleBounds();
   private static final AxisAlignedBB[] multiple_bounds_for_player_selection = new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};

   public BlockHopper(int par1) {
      super(par1, Material.iron, new BlockConstants());
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   public String getMetadataNotes() {
      return "0=Down, 2=North, 3=South, 4=West, 5=East";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 6 && metadata != 1;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   private static AxisAlignedBB[] getMultipleBounds() {
      float min_y = 0.0F;
      float var8 = 0.125F;
      AxisAlignedBB[] multiple_bounds = new AxisAlignedBB[]{new AxisAlignedBB(0.0, (double)min_y, 0.0, 1.0, 0.625, 1.0), new AxisAlignedBB(0.0, (double)min_y, 0.0, (double)var8, 1.0, 1.0), new AxisAlignedBB(0.0, (double)min_y, 0.0, 1.0, 1.0, (double)var8), new AxisAlignedBB((double)(1.0F - var8), (double)min_y, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, (double)min_y, (double)(1.0F - var8), 1.0, 1.0, 1.0)};
      return multiple_bounds;
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      return multiple_bounds;
   }

   public RaycastCollision tryRaycastVsBlock(Raycast raycast, int x, int y, int z, Vec3 origin, Vec3 limit) {
      return raycast.isForPlayerSelection() ? this.tryRaycastVsStandardFormBounds(raycast, x, y, z, origin, limit) : super.tryRaycastVsBlock(raycast, x, y, z, origin, limit);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (face.isTopOrBottom()) {
         return 0;
      } else if (face.isSouth()) {
         return 2;
      } else if (face.isNorth()) {
         return 3;
      } else {
         return face.isEast() ? 4 : 5;
      }
   }

   public TileEntity createNewTileEntity(World par1World) {
      return new TileEntityHopper();
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
      this.updateMetadata(par1World, par2, par3, par4);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offset_x, float offset_y, float offset_z) {
      if (world.isBlockFaceFlatAndSolid(x, y + 1, z, EnumFace.BOTTOM)) {
         return false;
      } else {
         if (player.onServer()) {
            TileEntityHopper tile_entity = (TileEntityHopper)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
               player.displayGUIHopper(tile_entity);
            }
         }

         return true;
      }
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      return this.updateMetadata(world, x, y, z);
   }

   private boolean updateMetadata(World par1World, int par2, int par3, int par4) {
      int var5 = par1World.getBlockMetadata(par2, par3, par4);
      int var6 = getDirectionFromMetadata(var5);
      boolean var7 = !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
      boolean var8 = getIsBlockNotPoweredFromMetadata(var5);
      if (var7 != var8) {
         par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | (var7 ? 0 : 8), 4);
         return true;
      } else {
         return false;
      }
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      TileEntityHopper var7 = (TileEntityHopper)par1World.getBlockTileEntity(par2, par3, par4);
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
            ItemStack var9 = var7.getStackInSlot(var8);
            if (var9 != null) {
               float var10 = this.field_94457_a.nextFloat() * 0.8F + 0.1F;
               float var11 = this.field_94457_a.nextFloat() * 0.8F + 0.1F;
               float var12 = this.field_94457_a.nextFloat() * 0.8F + 0.1F;

               while(var9.stackSize > 0) {
                  int var13 = this.field_94457_a.nextInt(21) + 10;
                  if (var13 > var9.stackSize) {
                     var13 = var9.stackSize;
                  }

                  var9.stackSize -= var13;
                  EntityItem var14 = new EntityItem(par1World, (double)((float)par2 + var10), (double)((float)par3 + var11), (double)((float)par4 + var12), new ItemStack(var9.itemID, var13, var9.getItemSubtype()));
                  if (var9.isItemDamaged()) {
                     var14.getEntityItem().setItemDamage(var9.getItemDamage());
                  }

                  if (var9.getItem().hasQuality()) {
                     var14.getEntityItem().setQuality(var9.getQuality());
                  }

                  if (var9.hasTagCompound()) {
                     var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                  }

                  float var15 = 0.05F;
                  var14.motionX = (double)((float)this.field_94457_a.nextGaussian() * var15);
                  var14.motionY = (double)((float)this.field_94457_a.nextGaussian() * var15 + 0.2F);
                  var14.motionZ = (double)((float)this.field_94457_a.nextGaussian() * var15);
                  par1World.spawnEntityInWorld(var14);
               }
            }
         }

         par1World.func_96440_m(par2, par3, par4, par5);
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
   }

   public int getRenderType() {
      return 38;
   }

   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return true;
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.hopperTopIcon : this.hopperIcon;
   }

   public static int getDirectionFromMetadata(int par0) {
      return par0 & 7;
   }

   public static boolean getIsBlockNotPoweredFromMetadata(int par0) {
      return (par0 & 8) != 8;
   }

   public boolean hasComparatorInputOverride() {
      return true;
   }

   public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
      return Container.calcRedstoneFromInventory(getHopperTile(par1World, par2, par3, par4));
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.hopperIcon = par1IconRegister.registerIcon("hopper_outside");
      this.hopperTopIcon = par1IconRegister.registerIcon("hopper_top");
      this.hopperInsideIcon = par1IconRegister.registerIcon("hopper_inside");
   }

   public static Icon getHopperIcon(String par0Str) {
      return par0Str.equals("hopper_outside") ? Block.hopperBlock.hopperIcon : (par0Str.equals("hopper_inside") ? Block.hopperBlock.hopperInsideIcon : null);
   }

   public String getItemIconName() {
      return "hopper";
   }

   public static TileEntityHopper getHopperTile(IBlockAccess par0IBlockAccess, int par1, int par2, int par3) {
      return (TileEntityHopper)par0IBlockAccess.getBlockTileEntity(par1, par2, par3);
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return side == 0;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
