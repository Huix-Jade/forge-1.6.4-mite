package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockSapling extends BlockPlant {
   public static final String[] WOOD_TYPES = new String[]{"oak", "spruce", "birch", "jungle"};
   private Icon[] saplingIcon;
   public static final int OAK_TREE = 0;
   public static final int SPRUCE_TREE = 1;
   public static final int BIRCH_TREE = 2;
   public static final int JUNGLE_TREE = 3;

   protected BlockSapling(int par1) {
      super(par1);
      float var2 = 0.4F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var2), 0.0, (double)(0.5F - var2), (double)(0.5F + var2), (double)(var2 * 2.0F), (double)(0.5F + var2));
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setCushioning(0.2F);
   }

   public boolean updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (super.updateTick(par1World, par2, par3, par4, par5Random)) {
         return true;
      } else {
         return par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9 && par5Random.nextInt(28) == 0 ? this.markOrGrowMarked(par1World, par2, par3, par4, par5Random) : false;
      }
   }

   public Icon getIcon(int par1, int par2) {
      return this.saplingIcon[this.getBlockSubtype(par2)];
   }

   public boolean markOrGrowMarked(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (!canGrowInBiome(this.getItemSubtype(par1World.getBlockMetadata(par2, par3, par4)), par1World.getBiomeGenForCoords(par2, par4))) {
         return false;
      } else {
         int var6 = par1World.getBlockMetadata(par2, par3, par4);
         if ((var6 & 8) == 0) {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 8, 4);
            return true;
         } else {
            this.growTree(par1World, par2, par3, par4, par5Random);
            return par1World.getBlock(par2, par3, par4) != this || par1World.getBlockMetadata(par2, par3, par4) != var6;
         }
      }
   }

   private void growTree(World par1World, int par2, int par3, int par4, Random par5Random) {
      if (!TerrainGen.saplingGrowTree(par1World, par5Random, par2, par3, par4)) return;
      int var6 = par1World.getBlockMetadata(par2, par3, par4) & 3;
      Object var7 = null;
      int var8 = 0;
      int var9 = 0;
      boolean var10 = false;
      if (var6 == 1) {
         var7 = new WorldGenTaiga2(true);
      } else if (var6 == 2) {
         var7 = new WorldGenForest(true);
      } else if (var6 == 3) {
         for(var8 = 0; var8 >= -1; --var8) {
            for(var9 = 0; var9 >= -1; --var9) {
               if (this.isSameSapling(par1World, par2 + var8, par3, par4 + var9, 3) && this.isSameSapling(par1World, par2 + var8 + 1, par3, par4 + var9, 3) && this.isSameSapling(par1World, par2 + var8, par3, par4 + var9 + 1, 3) && this.isSameSapling(par1World, par2 + var8 + 1, par3, par4 + var9 + 1, 3)) {
                  var7 = new WorldGenHugeTrees(true, 10 + par5Random.nextInt(20), 3, 3);
                  var10 = true;
                  break;
               }
            }

            if (var7 != null) {
               break;
            }
         }

         if (var7 == null) {
            var9 = 0;
            var8 = 0;
            var7 = new WorldGenTrees(true, 4 + par5Random.nextInt(7), 3, 3, false);
         }
      } else {
         var7 = new WorldGenTrees(true);
         if (par5Random.nextInt(10) == 0) {
            var7 = new WorldGenBigTree(true);
         }
      }

      if (var10) {
         par1World.setBlock(par2 + var8, par3, par4 + var9, 0, 0, 4);
         par1World.setBlock(par2 + var8 + 1, par3, par4 + var9, 0, 0, 4);
         par1World.setBlock(par2 + var8, par3, par4 + var9 + 1, 0, 0, 4);
         par1World.setBlock(par2 + var8 + 1, par3, par4 + var9 + 1, 0, 0, 4);
      } else {
         par1World.setBlock(par2, par3, par4, 0, 0, 4);
      }

      if (!((WorldGenerator)var7).generate(par1World, par5Random, par2 + var8, par3, par4 + var9)) {
         if (var10) {
            par1World.setBlock(par2 + var8, par3, par4 + var9, this.blockID, var6, 4);
            par1World.setBlock(par2 + var8 + 1, par3, par4 + var9, this.blockID, var6, 4);
            par1World.setBlock(par2 + var8, par3, par4 + var9 + 1, this.blockID, var6, 4);
            par1World.setBlock(par2 + var8 + 1, par3, par4 + var9 + 1, this.blockID, var6, 4);
         } else {
            par1World.setBlock(par2, par3, par4, this.blockID, var6, 4);
         }
      }

   }

   public boolean isSameSapling(World par1World, int par2, int par3, int par4, int par5) {
      return par1World.getBlockId(par2, par3, par4) == this.blockID && (par1World.getBlockMetadata(par2, par3, par4) & 3) == par5;
   }

   public String getMetadataNotes() {
      return "Bits 1 and 2 used for subtype, bit 8 used for (one) intermediate growth stage";
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < 4 || metadata >= 8 && metadata < 12;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata & 3;
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.saplingIcon = new Icon[WOOD_TYPES.length];

      for(int var2 = 0; var2 < this.saplingIcon.length; ++var2) {
         this.saplingIcon[var2] = par1IconRegister.registerIcon(this.getTextureName() + "_" + WOOD_TYPES[var2]);
      }

   }

   public static boolean canGrowInBiome(int subtype, BiomeGenBase biome) {
      if (!biome.hasRainfall()) {
         return false;
      } else if (subtype == 0) {
         return biome.temperature >= 0.4F;
      } else if (subtype == 2) {
         return biome.temperature >= 0.5F;
      } else if (subtype != 3) {
         return true;
      } else {
         return biome == BiomeGenBase.jungle || biome == BiomeGenBase.jungleHills || biome == BiomeGenBase.jungleRiver;
      }
   }
}
