package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RNG;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDamageResult;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Damage;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public final class BlockCactus extends Block implements IPlantable {
   private Icon cactusTopIcon;
   private Icon cactusBottomIcon;

   protected BlockCactus(int par1) {
      super(par1, Material.cactus, (new BlockConstants()).setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public String getMetadataNotes() {
      return "All bits used to track growth";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public static int getMaxHeight(int x, int y, int z) {
      return RNG.int_3[x + y * 501 + z * 9043 & 32767] + 1;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else if (world.getBlock(x, y + 1, z) == cactus) {
         return false;
      } else if (world.rand.nextFloat() < 0.5F) {
         return tryDecrementKillCount(world, x, y, z);
      } else if (random.nextFloat() < 0.9F) {
         return false;
      } else if (!world.getBiomeGenForCoords(x, z).isDesertBiome()) {
         this.dropBlockAsEntityItem((new BlockBreakInfo(world, x, y, z)).setDroppedSelf());
         world.setBlockToAir(x, y, z);
         return false;
      } else {
         if (world.isAirBlock(x, y + 1, z) && this.isLegalAt(world, x, y + 1, z, 0)) {
            int cactus_height;
            for(cactus_height = 1; world.getBlockId(x, y - cactus_height, z) == this.blockID; ++cactus_height) {
            }

            int max_height = getMaxHeight(x, y - cactus_height, z);
            if (cactus_height < max_height) {
               int metadata = world.getBlockMetadata(x, y, z);
               if (metadata == 15) {
                  world.setBlock(x, y + 1, z, this.blockID);
                  world.setBlockMetadataWithNotify(x, y, z, 0, 4);
                  this.onNeighborBlockChange(world, x, y + 1, z, this.blockID);
                  return true;
               }

               return world.setBlockMetadataWithNotify(x, y, z, metadata + 1, 4);
            }
         }

         return false;
      }
   }

   public Object getCollisionBounds(World world, int x, int y, int z, Entity entity) {
      float var5 = 0.0625F;
      return AxisAlignedBB.getAABBPool().getAABB((double)((float)x + var5), (double)y, (double)((float)z + var5), (double)((float)(x + 1) - var5), (double)((float)(y + 1) - var5), (double)((float)(z + 1) - var5));
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
      float var5 = 0.0625F;
      return AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var5), (double)par3, (double)((float)par4 + var5), (double)((float)(par2 + 1) - var5), (double)(par3 + 1), (double)((float)(par4 + 1) - var5));
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.cactusTopIcon : (par1 == 0 ? this.cactusBottomIcon : this.blockIcon);
   }

   public int getRenderType() {
      return 13;
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      if (!world.getNeighborBlockMaterial(x, y, z, EnumFace.WEST).isSolid() && !world.getNeighborBlockMaterial(x, y, z, EnumFace.EAST).isSolid()
              && !world.getNeighborBlockMaterial(x, y, z, EnumFace.NORTH).isSolid() && !world.getNeighborBlockMaterial(x, y, z, EnumFace.SOUTH).isSolid()) {
         int new_height = 1;
         int dy = 0;

         while(true) {
            --dy;
            if (world.getBlockId(x, y + dy, z) != this.blockID) {
               return new_height > 3 ? false : super.isLegalAt(world, x, y, z, metadata);
            }

            ++new_height;
         }
      } else {
         return false;
      }
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below == cactus || block_below == sand;
   }

   @Override
   public EnumPlantType getPlantType(World world, int x, int y, int z)
   {
      return EnumPlantType.Desert;
   }

   @Override
   public int getPlantID(World world, int x, int y, int z)
   {
      return blockID;
   }

   @Override
   public int getPlantMetadata(World world, int x, int y, int z)
   {
      return -1;
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      if (world.getChunkFromBlockCoords(x, z).last_total_world_time == 0L) {
         world.setBlockToAir(x, y, z);
         return true;
      } else {
         return super.onNotLegal(world, x, y, z, metadata);
      }
   }

   public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
      if (par1World.isWorldServer()) {
         if (par5Entity.isEntityLiving()) {
            EntityLiving entity_living = par5Entity.getAsEntityLiving();
            boolean is_moving_recklessly = entity_living.getCurrentSpeed() > 0.20000000298023224;
            if (!is_moving_recklessly) {
               BlockInfo info = entity_living.getBlockRestingOn3();
               boolean is_standing_on_cactus = info != null && info.block == this;
               if (!is_standing_on_cactus && !entity_living.is_collided_with_entities && entity_living.getCollidingBlockBounds().size() == 0) {
                  return;
               }
            }
         }

         EntityDamageResult result = par5Entity.attackEntityFrom(new Damage(DamageSource.cactus.setBlock(par1World, par2, par3, par4), 1.0F));
         if (result != null && result.entityWasDestroyed()) {
            tryIncrementKillCount(par1World, par2, par3, par4);
         }

         if (par5Entity instanceof EntityLiving && result != null && result.entityWasNegativelyAffected()) {
            par5Entity.getAsEntityLiving().last_tick_harmed_by_cactus = par1World.getTotalWorldTime();
         }
      }

   }

   public static int getKillCountBits() {
      return 7;
   }

   private static int getKillCount(int metadata) {
      return metadata & getKillCountBits();
   }

   private static int setKillCount(int metadata, int kills) {
      return metadata & ~getKillCountBits() | kills & getKillCountBits();
   }

   public static int getKillCount(World world, int x, int y, int z) {
      y = getYCoordOfSandBeneath(world, x, y, z);
      return y < 0 ? 0 : getKillCount(world.getBlockMetadata(x, y, z));
   }

   private static void tryIncrementKillCount(World world, int x, int y, int z) {
      y = getYCoordOfSandBeneath(world, x, y, z);
      if (y >= 0) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (getKillCount(metadata) < getKillCountBits()) {
            world.setBlockMetadataWithNotify(x, y, z, setKillCount(metadata, getKillCount(metadata) + 1), 4);
         }

      }
   }

   private static boolean tryDecrementKillCount(World world, int x, int y, int z) {
      y = getYCoordOfSandBeneath(world, x, y, z);
      if (y < 0) {
         return false;
      } else {
         int metadata = world.getBlockMetadata(x, y, z);
         return getKillCount(metadata) > 0 ? world.setBlockMetadataWithNotify(x, y, z, setKillCount(metadata, getKillCount(metadata) - 1), 4) : false;
      }
   }

   private static int getYCoordOfSandBeneath(World world, int x, int y, int z) {
      Block block;
      do {
         --y;
      } while((block = world.getBlock(x, y, z)) == Block.cactus);

      return block == sand ? y : -1;
   }

   public void onBlockPreDestroy(World world, int x, int y, int z, int old_metadata) {
      --y;
      if (world.getBlock(x, y, z) == sand) {
         world.setBlockMetadataWithNotify(x, y, z, 0, 4);
      }

   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.cactusTopIcon = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.cactusBottomIcon = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
   }

   public boolean hidesAdjacentSide(IBlockAccess block_access, int x, int y, int z, Block neighbor, int side) {
      return (side == 0 || side == 1) && neighbor == this;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }

   public boolean blocksPrecipitation(boolean[] blocks_precipitation, int metadata) {
      return true;
   }
}
