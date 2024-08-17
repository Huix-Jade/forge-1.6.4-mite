package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumFace;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected final boolean isPowered;

   public static final boolean isRailBlockAt(World par0World, int par1, int par2, int par3) {
      return isRailBlock(par0World.getBlockId(par1, par2, par3));
   }

   public static final boolean isRailBlock(int par0) {
      return Block.blocksList[par0] instanceof BlockRailBase;
   }

   protected BlockRailBase(int par1, boolean par2) {
      super(par1, Material.circuits, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
      this.isPowered = par2;
      this.setBlockBoundsForAllThreads(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   public boolean isPowered() {
      return this.isPowered;
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
      if (var5 >= 2 && var5 <= 5) {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.625, 1.0);
      } else {
         this.setBlockBoundsForCurrentThread(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
      }

   }

   public int getRenderType() {
      return renderType;
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below != null && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata);
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      if (!par1World.isRemote) {
         this.refreshTrackShape(par1World, par2, par3, par4, true);
         if (this.isPowered) {
            this.onNeighborBlockChange(par1World, par2, par3, par4, this.blockID);
         }
      }

   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (super.onNeighborBlockChange(world, x, y, z, neighbor_block_id)) {
         return true;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         this.func_94358_a(world, x, y, z, metadata, this.isPowered ? metadata & 7 : metadata, neighbor_block_id);
         return world.getBlock(x, y, z) != this || world.getBlockMetadata(x, y, z) != metadata;
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      int var7;
      if (this.isPowered) {
         var7 = metadata & 7;
      } else {
         var7 = metadata;
      }

      boolean var8 = false;
      if (var7 == 2 && !world.doesBlockHaveSolidTopSurface(x + 1, y, z)) {
         return false;
      } else if (var7 == 3 && !world.doesBlockHaveSolidTopSurface(x - 1, y, z)) {
         return false;
      } else if (var7 == 4 && !world.doesBlockHaveSolidTopSurface(x, y, z - 1)) {
         return false;
      } else {
         return var7 == 5 && !world.doesBlockHaveSolidTopSurface(x, y, z + 1) ? false : super.isLegalAt(world, x, y, z, metadata);
      }
   }

   protected void func_94358_a(World par1World, int par2, int par3, int par4, int par5, int par6, int par7) {
   }

   protected void refreshTrackShape(World par1World, int par2, int par3, int par4, boolean par5) {
      if (!par1World.isRemote) {
         (new BlockBaseRailLogic(this, par1World, par2, par3, par4)).func_94511_a(par1World.isBlockIndirectlyGettingPowered(par2, par3, par4), par5);
      }

   }

   public int getMobilityFlag() {
      return 0;
   }

   public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
      int var7 = par6;
      if (this.isPowered) {
         var7 = par6 & 7;
      }

      super.breakBlock(par1World, par2, par3, par4, par5, par6);
      if (var7 == 2 || var7 == 3 || var7 == 4 || var7 == 5) {
         par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, par5);
      }

      if (this.isPowered) {
         par1World.notifyBlocksOfNeighborChange(par2, par3, par4, par5);
         par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, par5);
      }

   }

   public float getExplosionResistance(Explosion explosion) {
      return explosion.exploder instanceof EntityMinecartTNT ? Float.MAX_VALUE : super.getExplosionResistance(explosion);
   }

   public int getMetadataForPlacement(World world, int x, int y, int z, ItemStack item_stack, Entity entity, EnumFace face, float offset_x, float offset_y, float offset_z) {
      EnumDirection direction = entity.getDirectionFromYaw();
      return direction.isNorthOrSouth() ? 0 : 1;
   }

   public boolean isDislodgedOrCrushedByFallingBlock(int metadata, Block falling_block, int falling_block_metadata) {
      return false;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksFluids(boolean[] blocks_fluids, int metadata) {
      return true;
   }


   /**
    * Return true if the rail can make corners.
    * Used by placement logic.
    * @param world The world.
    * @param x The rail X coordinate.
    * @param y The rail Y coordinate.
    * @param z The rail Z coordinate.
    * @return True if the rail can make corners.
    */
   public boolean isFlexibleRail(World world, int y, int x, int z)
   {
      return !isPowered;
   }

   /**
    * Returns true if the rail can make up and down slopes.
    * Used by placement logic.
    * @param world The world.
    * @param x The rail X coordinate.
    * @param y The rail Y coordinate.
    * @param z The rail Z coordinate.
    * @return True if the rail can make slopes.
    */
   public boolean canMakeSlopes(World world, int x, int y, int z)
   {
      return true;
   }

   /**
    * Return the rail's metadata (without the power bit if the rail uses one).
    * Can be used to make the cart think the rail something other than it is,
    * for example when making diamond junctions or switches.
    * The cart parameter will often be null unless it it called from EntityMinecart.
    *
    * Valid rail metadata is defined as follows:
    * 0x0: flat track going North-South
    * 0x1: flat track going West-East
    * 0x2: track ascending to the East
    * 0x3: track ascending to the West
    * 0x4: track ascending to the North
    * 0x5: track ascending to the South
    * 0x6: WestNorth corner (connecting East and South)
    * 0x7: EastNorth corner (connecting West and South)
    * 0x8: EastSouth corner (connecting West and North)
    * 0x9: WestSouth corner (connecting East and North)
    *
    * @param world The world.
    * @param cart The cart asking for the metadata, null if it is not called by EntityMinecart.
    * @param y The rail X coordinate.
    * @param x The rail Y coordinate.
    * @param z The rail Z coordinate.
    * @return The metadata.
    */
   public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z)
   {
      int meta = world.getBlockMetadata(x, y, z);
      if(isPowered)
      {
         meta = meta & 7;
      }
      return meta;
   }

   /**
    * Returns the max speed of the rail at the specified position.
    * @param world The world.
    * @param cart The cart on the rail, may be null.
    * @param x The rail X coordinate.
    * @param y The rail Y coordinate.
    * @param z The rail Z coordinate.
    * @return The max speed of the current rail.
    */
   public float getRailMaxSpeed(World world, EntityMinecart cart, int y, int x, int z)
   {
      return 0.4f;
   }

   /**
    * This function is called by any minecart that passes over this rail.
    * It is called once per update tick that the minecart is on the rail.
    * @param world The world.
    * @param cart The cart on the rail.
    * @param y The rail X coordinate.
    * @param x The rail Y coordinate.
    * @param z The rail Z coordinate.
    */
   public void onMinecartPass(World world, EntityMinecart cart, int y, int x, int z)
   {
   }

   /**
    * Forge: Moved render type to a field and a setter.
    * This allows for a mod to change the render type
    * for vanilla rails, and any mod rails that extend
    * this class.
    */
   private int renderType = 9;

   public void setRenderType(int value)
   {
      renderType = value;
   }
}

