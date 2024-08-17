package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockOperation;
import net.minecraft.util.EnumDirection;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockPumpkin extends BlockDirectional {
   private boolean blockType;
   private Icon field_94474_b;
   private Icon field_94475_c;

   protected BlockPumpkin(int par1, boolean par2) {
      super(par1, Material.pumpkin, (new BlockConstants()).setNeverConnectsWithFence());
      this.setTickRandomly(true);
      this.blockType = par2;
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public String getMetadataNotes() {
      String[] array = new String[4];

      for(int i = 0; i < array.length; ++i) {
         array[i] = i + "=" + this.getDirectionFacing(i).getDescriptor(true);
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.field_94474_b : (par1 == 0 ? this.field_94474_b : (par2 == 2 && par1 == 2 ? this.field_94475_c : (par2 == 3 && par1 == 5 ? this.field_94475_c : (par2 == 0 && par1 == 3 ? this.field_94475_c : (par2 == 1 && par1 == 4 ? this.field_94475_c : this.blockIcon)))));
   }

   public void onBlockAdded(World par1World, int par2, int par3, int par4) {
      super.onBlockAdded(par1World, par2, par3, par4);
      if (par1World.getBlockId(par2, par3 - 1, par4) == Block.blockSnow.blockID && par1World.getBlockId(par2, par3 - 2, par4) == Block.blockSnow.blockID) {
         if (!par1World.isRemote) {
            par1World.setBlock(par2, par3, par4, 0, 0, 2);
            par1World.setBlock(par2, par3 - 1, par4, 0, 0, 2);
            par1World.setBlock(par2, par3 - 2, par4, 0, 0, 2);
            EntitySnowman var9 = new EntitySnowman(par1World);
            var9.setLocationAndAngles((double)par2 + 0.5, (double)par3 - 1.95, (double)par4 + 0.5, 0.0F, 0.0F);
            par1World.spawnEntityInWorld(var9);
            par1World.notifyBlockChange(par2, par3, par4, 0);
            par1World.notifyBlockChange(par2, par3 - 1, par4, 0);
            par1World.notifyBlockChange(par2, par3 - 2, par4, 0);
         }

         for(int var10 = 0; var10 < 120; ++var10) {
            par1World.spawnParticle(EnumParticle.snowshovel, (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 2.5, (double)par4 + par1World.rand.nextDouble(), 0.0, 0.0, 0.0);
         }
      } else if (par1World.getBlockId(par2, par3 - 1, par4) == Block.blockIron.blockID && par1World.getBlockId(par2, par3 - 2, par4) == Block.blockIron.blockID) {
         boolean var5 = par1World.getBlockId(par2 - 1, par3 - 1, par4) == Block.blockIron.blockID && par1World.getBlockId(par2 + 1, par3 - 1, par4) == Block.blockIron.blockID;
         boolean var6 = par1World.getBlockId(par2, par3 - 1, par4 - 1) == Block.blockIron.blockID && par1World.getBlockId(par2, par3 - 1, par4 + 1) == Block.blockIron.blockID;
         if (var5 || var6) {
            par1World.setBlock(par2, par3, par4, 0, 0, 2);
            par1World.setBlock(par2, par3 - 1, par4, 0, 0, 2);
            par1World.setBlock(par2, par3 - 2, par4, 0, 0, 2);
            if (var5) {
               par1World.setBlock(par2 - 1, par3 - 1, par4, 0, 0, 2);
               par1World.setBlock(par2 + 1, par3 - 1, par4, 0, 0, 2);
            } else {
               par1World.setBlock(par2, par3 - 1, par4 - 1, 0, 0, 2);
               par1World.setBlock(par2, par3 - 1, par4 + 1, 0, 0, 2);
            }

            EntityIronGolem var7 = new EntityIronGolem(par1World);
            var7.setPlayerCreated(true);
            var7.setLocationAndAngles((double)par2 + 0.5, (double)par3 - 1.95, (double)par4 + 0.5, 0.0F, 0.0F);
            par1World.spawnEntityInWorld(var7);

            for(int var8 = 0; var8 < 120; ++var8) {
               par1World.spawnParticle(EnumParticle.snowballpoof, (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9, (double)par4 + par1World.rand.nextDouble(), 0.0, 0.0, 0.0);
            }

            par1World.notifyBlockChange(par2, par3, par4, 0);
            par1World.notifyBlockChange(par2, par3 - 1, par4, 0);
            par1World.notifyBlockChange(par2, par3 - 2, par4, 0);
            if (var5) {
               par1World.notifyBlockChange(par2 - 1, par3 - 1, par4, 0);
               par1World.notifyBlockChange(par2 + 1, par3 - 1, par4, 0);
            } else {
               par1World.notifyBlockChange(par2, par3 - 1, par4 - 1, 0);
               par1World.notifyBlockChange(par2, par3 - 1, par4 + 1, 0);
            }
         }
      }

   }

   public final EnumDirection getDirectionFacing(int metadata) {
      return this.getDirectionFacingStandard4(metadata);
   }

   public int getMetadataForDirectionFacing(int metadata, EnumDirection direction) {
      metadata &= -4;
      metadata |= direction.isSouth() ? 0 : (direction.isWest() ? 1 : (direction.isNorth() ? 2 : (direction.isEast() ? 3 : -1)));
      return metadata;
   }

   public boolean canOccurAt(World world, int x, int y, int z, int metadata) {
      Block block_below = world.getBlock(x, y - 1, z);
      return block_below != null && block_below.isBlockTopFacingSurfaceSolid(world.getBlockMetadata(x, y - 1, z)) ? super.canOccurAt(world, x, y, z, metadata) : false;
   }

   public boolean canBePlacedAt(World world, int x, int y, int z, int metadata) {
      int l = world.getBlockId(x, y, z);
      Block block = Block.blocksList[l];
      return (block == null || block.isBlockReplaceable(world, x, y, z)) && world.doesBlockHaveSolidTopSurface(x, y - 1, z);
   }

   public boolean canBePlacedOnBlock(int metadata, Block block_below, int block_below_metadata, double block_below_bounds_max_y) {
      return block_below != null && block_below.isBlockTopFacingSurfaceSolid(block_below_metadata) && super.canBePlacedOnBlock(metadata, block_below, block_below_metadata, block_below_bounds_max_y);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_94475_c = par1IconRegister.registerIcon(this.getTextureName() + "_face_" + (this.blockType ? "on" : "off"));
      this.field_94474_b = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return info.wasExploded() ? this.dropBlockAsEntityItem(info, Item.pumpkinSeeds) : super.dropBlockAsEntityItem(info);
   }

   public void addItemBlockMaterials(ItemBlock item_block) {
      item_block.addMaterial(new Material[]{Material.pumpkin});
      if (this.blockType) {
         item_block.addMaterial(new Material[]{Material.wood, Material.coal});
      }

   }

   public boolean checkForFlooding(World world, int x, int y, int z) {
      if (world instanceof WorldServer && this.blockType && world.getBlock(x, y, z) == pumpkinLantern) {
         BlockFluid flooding_block = null;

         for(int dx = -1; dx <= 1 && flooding_block == null; ++dx) {
            for(int dz = -1; dz <= 1 && flooding_block == null; ++dz) {
               if (dx == 0 || dz == 0) {
                  Block block = world.getBlock(x + dx, y, z + dz);
                  if (block instanceof BlockFluid) {
                     flooding_block = (BlockFluid)block;
                     break;
                  }
               }
            }
         }

         if (flooding_block == null) {
            Block block = world.getBlock(x, y + 1, z);
            if (block instanceof BlockFluid) {
               flooding_block = (BlockFluid)block;
            }
         }

         if (flooding_block != null) {
            world.getAsWorldServer().addScheduledBlockOperation(EnumBlockOperation.pumpkin_lantern_flooded, x, y, z, world.getTotalWorldTime() + 10L, false);
         }
      }

      return false;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else {
         return this.blockType && this.checkForFlooding(world, x, y, z);
      }
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      return !test_only && this.blockType && this.checkForFlooding(world, x, y, z) ? true : super.onBlockPlacedMITE(world, x, y, z, metadata, placer, test_only);
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      if (this.blockType) {
         this.checkForFlooding(world, x, y, z);
      }

      return super.onNeighborBlockChange(world, x, y, z, neighbor_block_id);
   }
}
