package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFace;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block {
   private Icon field_94422_a;
   private Icon field_94421_b;

   protected BlockMycelium(int par1) {
      super(par1, Material.grass, (new BlockConstants()).setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.field_94422_a : (par1 == 0 ? Block.dirt.getBlockTextureFromSide(par1) : this.blockIcon);
   }

   public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (par5 == 1) {
         return this.field_94422_a;
      } else if (par5 == 0) {
         return Block.dirt.getBlockTextureFromSide(par5);
      } else {
         Material var6 = par1IBlockAccess.getBlockMaterial(par2, par3 + 1, par4);
         return var6 != Material.snow && var6 != Material.craftedSnow ? this.blockIcon : this.field_94421_b;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.field_94422_a = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.field_94421_b = par1IconRegister.registerIcon("grass_side_snowed");
   }

   public static int getLightValueTolerance() {
      return 13;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      if (world.isRemote) {
         Minecraft.setErrorMessage("BlockMycelium.updateTick: called on client?");
         return false;
      } else if (super.updateTick(world, x, y, z, random)) {
         return true;
      } else if (world.getBlockMaterial(x, y + 1, z) == Material.water) {
         return world.setBlock(x, y, z, Block.dirt.blockID);
      } else if (random.nextInt(4) > 0) {
         return false;
      } else {
         if (world.getBlockLightValue(x, y + 1, z) <= getLightValueTolerance() && !world.isOutdoors(x, y, z)) {
            int mushroom_count;
            int dx;
            int dy;
            int dz;
            if (random.nextInt(256) == 0 && world.isAirBlock(x, y + 1, z)) {
               mushroom_count = 0;

               for(dx = -4; dx <= 4; ++dx) {
                  for(dy = -2; dy <= 2; ++dy) {
                     for(dz = -4; dz <= 4; ++dz) {
                        if (world.getBlock(x + dx, y + dy, z + dz) instanceof BlockMushroom) {
                           ++mushroom_count;
                           if (mushroom_count > 2) {
                              return false;
                           }
                        }
                     }
                  }
               }

               world.setBlock(x, y + 1, z, Block.mushroomBrown.blockID);
               return false;
            }

            mushroom_count = 0;
            dx = 0;
            dy = 0;

            for(dz = 0; dz < 8; ++dz) {
               mushroom_count = random.nextInt(3) - 1;
               dx = random.nextInt(5) - 2;
               dy = random.nextInt(3) - 1;
               if (dx != -1 && dx != 0 && dx != 1 || mushroom_count == 0 || dy == 0) {
                  Block block = world.getBlock(x + mushroom_count, y + dx, z + dy);
                  if ((block == Block.dirt || block == Block.grass || block == Block.tilledField) && world.getBlockLightValue(x + mushroom_count, y + dx + 1, z + dy) <= getLightValueTolerance() && this.isLegalAt(world, x + mushroom_count, y + dx, z + dy, 0)) {
                     break;
                  }
               }
            }

            x += mushroom_count;
            y += dx;
            z += dy;
            if (!this.isLegalAt(world, x, y, z, 0)) {
               return false;
            }

            if (world.isLiquidBlock(x, y + 1, z)) {
               return false;
            }

            Block block = world.getBlock(x, y, z);
            if ((block == Block.dirt || block == Block.grass || block == Block.tilledField) && world.getBlockLightValue(x, y + 1, z) <= getLightValueTolerance() && !world.isOutdoors(x, y, z)) {
               world.setBlock(x, y, z, this.blockID);
            }
         } else if (world.isDaytime() && (!world.isPrecipitating(true) || world.isFreezing(x, z))) {
            return world.setBlock(x, y, z, Block.dirt.blockID);
         }

         return false;
      }
   }

   public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      super.randomDisplayTick(par1World, par2, par3, par4, par5Random);
      if (par5Random.nextInt(10) == 0) {
         par1World.spawnParticle(EnumParticle.townaura, (double)((float)par2 + par5Random.nextFloat()), (double)((float)par3 + 1.1F), (double)((float)par4 + par5Random.nextFloat()), 0.0, 0.0, 0.0);
      }

   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return this.dropBlockAsEntityItem(info, Block.dirt);
   }

   public boolean fertilize(World world, int x, int y, int z, ItemStack item_stack, EntityPlayer player) {
      Item item = item_stack.getItem();
      if (item == Item.manure) {
         Block block = Block.blocksList[world.getBlockId(x, y + 1, z)];
         if (block == Block.mushroomBrown) {
            if (!world.isRemote) {
               ((BlockMushroom)block).fertilizeMushroom(world, x, y + 1, z, item_stack, player);
               world.blockFX(EnumBlockFX.manure, x, y + 1, z);
            }

            return true;
         }
      }

      return false;
   }

   public boolean onNeighborBlockChange(World world, int x, int y, int z, int neighbor_block_id) {
      boolean changed = super.onNeighborBlockChange(world, x, y, z, neighbor_block_id);
      if (changed && world.getBlock(x, y, z) != this) {
         return true;
      } else if (!world.isLavaBlock(x, y + 1, z)) {
         return changed;
      } else {
         world.blockFX(EnumBlockFX.lava_mixing_with_water, x, y, z);
         return world.setBlock(x, y, z, Block.dirt.blockID) || changed;
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      Block block_above = world.getBlock(x, y + 1, z);
      return block_above == null || block_above instanceof BlockMushroomCap || !block_above.isFaceFlatAndSolid(world.getBlockMetadata(x, y + 1, z), EnumFace.BOTTOM);
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      return world.setBlock(x, y, z, dirt.blockID);
   }

   public Block getAlternativeBlockForPlacement() {
      return dirt;
   }
}
