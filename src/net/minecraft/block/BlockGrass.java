package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivestock;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockFX;
import net.minecraft.util.EnumFace;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public final class BlockGrass extends Block {
   private Icon iconGrassTop;
   private Icon iconSnowSide;
   private Icon iconGrassSideOverlay;

   protected BlockGrass(int par1) {
      super(par1, Material.grass, (new BlockConstants()).setNotAlwaysLegal());
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setCushioning(0.2F);
      this.has_grass_top_icon = true;
   }

   public String getMetadataNotes() {
      return "All bits used to track number of recent tramplings";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 16;
   }

   public Icon getIcon(int par1, int par2) {
      return par1 == 1 ? this.iconGrassTop : (par1 == 0 ? Block.dirt.getBlockTextureFromSide(par1) : this.blockIcon);
   }

   public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      if (par5 == 1) {
         return this.iconGrassTop;
      } else if (par5 == 0) {
         return Block.dirt.getBlockTextureFromSide(par5);
      } else {
         Material var6 = par1IBlockAccess.getBlockMaterial(par2, par3 + 1, par4);
         return var6 != Material.snow && var6 != Material.craftedSnow ? this.blockIcon : this.iconSnowSide;
      }
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
      this.iconGrassTop = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.iconSnowSide = par1IconRegister.registerIcon(this.getTextureName() + "_side_snowed");
      this.iconGrassSideOverlay = par1IconRegister.registerIcon(this.getTextureName() + "_side_overlay");
   }

   public int getBlockColor() {
      double var1 = 0.5;
      double var3 = 1.0;
      return ColorizerGrass.getGrassColor(var1, var3);
   }

   public int getRenderColor(int par1) {
      return this.getBlockColor();
   }

   public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      int r;
      int g;
      int b;
      for(r = -1; r <= 1; ++r) {
         for(g = -1; g <= 1; ++g) {
            b = par1IBlockAccess.getBiomeGenForCoords(par2 + g, par4 + r).getBiomeGrassColor();
            var5 += (b & 16711680) >> 16;
            var6 += (b & '\uff00') >> 8;
            var7 += b & 255;
         }
      }

      r = var5 / 9 & 255;
      g = var6 / 9 & 255;
      b = var7 / 9 & 255;
      float trampling_effect = getTramplingEffect(getTramplings(par1IBlockAccess.getBlockMetadata(par2, par3, par4)));
      if (trampling_effect > 0.0F) {
         float weight_grass = 1.0F - trampling_effect;
         r = (int)((float)r * weight_grass + 134.0F * trampling_effect);
         g = (int)((float)g * weight_grass + 96.0F * trampling_effect);
         b = (int)((float)b * weight_grass + 67.0F * trampling_effect);
      }

      return r << 16 | g << 8 | b;
   }

   public boolean updateTick(World world, int x, int y, int z, Random random) {
      int block_light_value = world.getBlockLightValue(x, y + 1, z);
      int attempts;
      if (block_light_value < 4) {
         attempts = world.getBlockId(x, y + 1, z);
         if (lightOpacity[attempts] > 2) {
            Block block_above = Block.getBlock(attempts);
            if (block_above != blockSnow && block_above != ice) {
               return world.setBlock(x, y, z, dirt.blockID);
            }
         }
      } else if (block_light_value >= 13) {
         for(attempts = 0; attempts < 4; ++attempts) {
            int try_x = x + random.nextInt(3) - 1;
            int try_y = y + random.nextInt(5) - 3;
            int try_z = z + random.nextInt(3) - 1;
            if (this.isLegalAt(world, try_x, try_y, try_z, 0)) {
               int block_above_id = world.getBlockId(try_x, try_y + 1, try_z);
               Block block = Block.blocksList[world.getBlockId(try_x, try_y, try_z)];
               if ((block == Block.dirt || block == Block.grass) && world.getBlockLightValue(try_x, try_y + 1, try_z) >= 4 && Block.lightOpacity[block_above_id] <= 2) {
                  if (block == Block.grass) {
                     if (((WorldServer)world).fast_forwarding) {
                        return false;
                     }

                     int metadata = world.getBlockMetadata(try_x, try_y, try_z);
                     int tramplings = getTramplings(metadata);
                     if (tramplings > 0) {
                        --tramplings;
                        return world.setBlockMetadataWithNotify(try_x, try_y, try_z, this.setTramplings(metadata, tramplings), 2);
                     }

                     return false;
                  }

                  return world.setBlock(try_x, try_y, try_z, Block.grass.blockID);
               }
            }
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
      if (block_above != null && block_above != leaves && block_above.blockMaterial != Material.snow && block_above.blockMaterial != Material.craftedSnow) {
         if (block_above instanceof BlockPistonBase) {
            return false;
         } else if (!block_above.hidesAdjacentSide(world, x, y + 1, z, this, 1)) {
            return true;
         } else {
            return !block_above.isFaceFlatAndSolid(world.getBlockMetadata(x, y + 1, z), EnumFace.BOTTOM);
         }
      } else {
         return true;
      }
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      return world.setBlock(x, y, z, dirt.blockID);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      if (info.wasHarvestedByPlayer() && !info.world.isFreezing(info.x, info.z)) {
         int fortune = info.getHarvesterFortune();
         if (fortune > 3) {
            fortune = 3;
         }

         if (info.world.isInRain(info.x, info.y + 1, info.z)) {
            fortune += 12;
         }

         if (fortune > 14) {
            fortune = 14;
         }

         if (info.world.rand.nextInt(16 - fortune) == 0) {
            return this.dropBlockAsEntityItem(info, Item.wormRaw);
         }
      }

      return this.dropBlockAsEntityItem(info, Block.dirt.blockID);
   }

   public static Icon getIconSideOverlay() {
      return Block.grass.iconGrassSideOverlay;
   }

   public void onTrampledBy(World world, int x, int y, int z, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivestock) {
         int metadata = world.getBlockMetadata(x, y, z);
         int tramplings = getTramplings(metadata);
         if (tramplings < getTramplingBits()) {
            ++tramplings;
            world.setBlockMetadataWithNotify(x, y, z, this.setTramplings(metadata, tramplings), 2);
         }

         float trampling_effect = getTramplingEffect(tramplings);
         if (entity instanceof EntityCow) {
            trampling_effect *= 2.0F;
         }

         if (trampling_effect >= 0.2F && Math.random() < (double)(trampling_effect * 2.0F)) {
            Block block = Block.blocksList[world.getBlockId(x, y + 1, z)];
            if (block != null) {
               block.onTrampledBy(world, x, y + 1, z, entity);
            }
         }
      }

      super.onEntityWalking(world, x, y, z, entity);
   }

   public static int getTramplingBits() {
      return 15;
   }

   public static int getTramplings(int metadata) {
      return metadata & getTramplingBits();
   }

   public int setTramplings(int metadata, int tramplings) {
      return metadata & ~getTramplingBits() | tramplings & getTramplingBits();
   }

   public static float getTramplingEffect(int tramplings) {
      return MathHelper.clamp_float((float)(tramplings - 3) * 0.05F, 0.0F, 0.5F);
   }

   public Block getAlternativeBlockForPlacement() {
      return dirt;
   }

   public boolean fertilize(World world, int x, int y, int z, ItemStack item_stack) {
      Item item = item_stack.getItem();
      if (item != Item.dyePowder) {
         return false;
      } else {
         world.getBlockMetadata(x, y, z);
         Random itemRand = Item.itemRand;
         int dx;
         int var7;
         int var8;
         if (!world.isRemote) {
            ItemDye var10000 = (ItemDye)item;
            ItemDye.suppress_standard_particle_effect = true;

            for(dx = -2; dx <= 2; ++dx) {
               for(var7 = -1; var7 <= 1; ++var7) {
                  for(var8 = -2; var8 <= 2; ++var8) {
                     if (world.getBlock(x + dx, y + var7, z + var8) == Block.grass && world.isAirBlock(x + dx, y + var7 + 1, z + var8)) {
                        world.playAuxSFX(2005, x + dx, y + var7 + 1, z + var8, 1);
                     }
                  }
               }
            }
         }

         if (!world.isRemote) {
            label70:
            for(dx = 0; dx < 128; ++dx) {
               var7 = x;
               var8 = y + 1;
               int var9 = z;

               int subtype;
               for(subtype = 0; subtype < dx / 16; ++subtype) {
                  var7 += itemRand.nextInt(3) - 1;
                  var8 += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
                  var9 += itemRand.nextInt(3) - 1;
                  if (world.getBlockId(var7, var8 - 1, var9) != Block.grass.blockID || world.isBlockNormalCube(var7, var8, var9)) {
                     continue label70;
                  }
               }

               if (world.getBlockId(var7, var8, var9) == 0 && itemRand.nextInt(2) == 0) {
                  if (itemRand.nextInt(10) != 0) {
                     if (Block.tallGrass.isLegalAt(world, var7, var8, var9, 1)) {
                        world.setBlock(var7, var8, var9, Block.tallGrass.blockID, 1, 3);
                     }
                  } else if (itemRand.nextInt(3) != 0) {
                     if (Block.plantYellow.isLegalAt(world, var7, var8, var9, 0)) {
                        world.setBlock(var7, var8, var9, Block.plantYellow.blockID);
                     }
                  } else {
                     subtype = Block.plantRed.getRandomSubtypeThatCanOccurAt(world, var7, var8, var9);
                     if (subtype == 2 && itemRand.nextFloat() < 0.8F) {
                        subtype = -1;
                     }

                     if (subtype >= 0) {
                        world.setBlock(var7, var8, var9, Block.plantRed.blockID, subtype, 3);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
