package net.minecraft.block;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RNG;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.EnumParticle;
import net.minecraft.util.EnumTournamentType;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockSandStone extends Block {
   public static final String[] SAND_STONE_TYPES = new String[]{"default", "chiseled", "smooth", "sacred"};
   private static final String[] field_94405_b = new String[]{"normal", "carved", "smooth", "smooth"};
   private Icon[] field_94406_c;
   private Icon field_94403_cO;
   private Icon field_94404_cP;
   public static boolean sacred_pyramid_completed;

   public BlockSandStone(int par1) {
      super(par1, Material.stone, new BlockConstants());
      this.modifyMinHarvestLevel(-1);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public Icon getIcon(int par1, int par2) {
      int subtype = this.getBlockSubtype(par2);
      if (par1 == 0) {
         return subtype == 0 ? this.field_94404_cP : this.field_94403_cO;
      } else {
         return par1 == 1 ? this.field_94403_cO : this.field_94406_c[subtype];
      }
   }

   public String getMetadataNotes() {
      return "0=Regular, 1=Chiseled, 2=Smooth, 3=Sacred";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.field_94406_c = new Icon[field_94405_b.length];

      for(int var2 = 0; var2 < this.field_94406_c.length; ++var2) {
         this.field_94406_c[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_" + field_94405_b[var2]);
      }

      this.field_94403_cO = par1IconRegister.registerIcon(this.getTextureName() + "_top");
      this.field_94404_cP = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
   }

   public static boolean isSacredSandstone(Block block, int metadata) {
      return block == sandStone && block.getBlockSubtype(metadata) == 3;
   }

   public boolean isSacredSandstone(int metadata) {
      return isSacredSandstone(this, metadata);
   }

   public int dropBlockAsEntityItem(BlockBreakInfo info) {
      return !info.wasExploded() && !this.isSacredSandstone(info.getMetadata()) ? super.dropBlockAsEntityItem(info) : this.dropBlockAsEntityItem(info, sand);
   }

   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      if (!world.isRemote) {
         Minecraft.setErrorMessage("Why this called on server?");
      } else if (world.getBlock(x, y, z) == sandStone && world.getBlockMetadata(x, y, z) == 3) {
         if (!RNG.chance_in_2[++RNG.random_number_index & 32767]) {
            Random var5 = world.rand;
            double var6 = 0.0625;

            for(int var8 = 0; var8 < 6; ++var8) {
               double var9 = (double)((float)x + var5.nextFloat());
               double var11 = (double)((float)y + var5.nextFloat());
               double var13 = (double)((float)z + var5.nextFloat());
               if (var8 == 0 && !world.isBlockStandardFormOpaqueCube(x, y + 1, z)) {
                  var11 = (double)(y + 1) + var6;
               }

               if (var8 == 1 && !world.isBlockStandardFormOpaqueCube(x, y - 1, z)) {
                  var11 = (double)(y + 0) - var6;
               }

               if (var8 == 2 && !world.isBlockStandardFormOpaqueCube(x, y, z + 1)) {
                  var13 = (double)(z + 1) + var6;
               }

               if (var8 == 3 && !world.isBlockStandardFormOpaqueCube(x, y, z - 1)) {
                  var13 = (double)(z + 0) - var6;
               }

               if (var8 == 4 && !world.isBlockStandardFormOpaqueCube(x + 1, y, z)) {
                  var9 = (double)(x + 1) + var6;
               }

               if (var8 == 5 && !world.isBlockStandardFormOpaqueCube(x - 1, y, z)) {
                  var9 = (double)(x + 0) - var6;
               }

               if (var9 < (double)x || var9 > (double)(x + 1) || var11 < 0.0 || var11 > (double)(y + 1) || var13 < (double)z || var13 > (double)(z + 1)) {
                  world.spawnParticle(EnumParticle.sacred, var9, var11, var13, 0.0, 0.0, 0.0);
               }
            }

         }
      }
   }

   public static boolean isSacredSandstoneBlock(World world, int x, int y, int z) {
      return isSacredSandstone(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
   }

   private void checkForCompletionOfPyramid(World world, int x, int y, int z) {
      if (!sacred_pyramid_completed) {
         boolean is_topmost = true;
         if (isSacredSandstoneBlock(world, x - 1, y + 1, z - 1)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x - 1, y + 1, z - 1);
         }

         if (isSacredSandstoneBlock(world, x - 1, y + 1, z)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x - 1, y + 1, z);
         }

         if (isSacredSandstoneBlock(world, x - 1, y + 1, z + 1)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x - 1, y + 1, z + 1);
         }

         if (isSacredSandstoneBlock(world, x, y + 1, z - 1)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x, y + 1, z - 1);
         }

         if (isSacredSandstoneBlock(world, x, y + 1, z + 1)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x, y + 1, z + 1);
         }

         if (isSacredSandstoneBlock(world, x + 1, y + 1, z - 1)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x + 1, y + 1, z - 1);
         }

         if (isSacredSandstoneBlock(world, x + 1, y + 1, z)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x + 1, y + 1, z);
         }

         if (isSacredSandstoneBlock(world, x + 1, y + 1, z + 1)) {
            is_topmost = false;
            this.checkForCompletionOfPyramid(world, x + 1, y + 1, z + 1);
         }

         if (is_topmost) {
            int dy = -1;

            while(true) {
               if (dy <= -DedicatedServer.getRequiredPyramidHeight()) {
                  sacred_pyramid_completed = true;
                  break;
               }

               for(int dx = dy; dx <= -dy; ++dx) {
                  for(int dz = dy; dz <= -dy; ++dz) {
                     if ((dx == dy || dx == -dy || dz == dy || dz == -dy) && (!isSacredSandstoneBlock(world, x + dx, y + dy, z + dz) || !world.getBiomeGenForCoords(x + dx, z + dz).isDesertBiome())) {
                        return;
                     }
                  }
               }

               --dy;
            }
         }

      }
   }

   public boolean onBlockPlacedMITE(World world, int x, int y, int z, int metadata, Entity placer, boolean test_only) {
      if (!world.isRemote && metadata == 3 && DedicatedServer.tournament_type == EnumTournamentType.wonder) {
         if (sacred_pyramid_completed) {
            return true;
         } else {
            world.worldInfo.incrementSacredSandstonesPlaced();
            if (placer instanceof EntityPlayerMP) {
               EntityPlayerMP player = (EntityPlayerMP)placer;
               ++player.sacred_stones_placed;

               try {
                  File dir = new File("sacred_stones_placed");
                  if (!dir.exists()) {
                     dir.mkdir();
                  }

                  FileWriter fw = new FileWriter(dir.getPath() + "/" + player.username);
                  fw.write("" + player.sacred_stones_placed);
                  fw.close();
               } catch (Exception var11) {
               }
            }

            this.checkForCompletionOfPyramid(world, x, y, z);
            if (sacred_pyramid_completed) {
               DedicatedServer.checkForTournamentCompletion();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public boolean canSilkHarvest(int metadata) {
      return !this.isSacredSandstone(metadata) && super.canSilkHarvest(metadata);
   }
}
