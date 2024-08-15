package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.StringHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class BlockFlowerMulti extends BlockFlower {
   public static final int ROSE = 0;
   public static final int ORCHID = 1;
   public static final int ALLIUM = 2;
   public static final int TULIP = 5;
   public static final int DAHLIA = 7;
   public static final int DAISY = 8;
   public static final String[] types = new String[]{"rose", "orchid", "allium", null, null, "tulip", null, "dahlia", "daisy"};
   private Icon[] icons;
   private static int[] candidates;

   protected BlockFlowerMulti(int id, Material material) {
      super(id, material);
   }

   protected BlockFlowerMulti(int id) {
      this(id, Material.plants);
   }

   public void registerIcons(IconRegister par1IconRegister) {
      this.icons = this.registerIcons(par1IconRegister, types, this.getTextureName());
   }

   public Icon getIcon(int side, int metadata) {
      return this.icons[this.getBlockSubtype(metadata)];
   }

   public String getMetadataNotes() {
      String[] array = new String[types.length];

      for(int i = 0; i < types.length; ++i) {
         if (types[i] != null) {
            array[i] = i + "=" + StringHelper.capitalize(types[i]);
         }
      }

      return StringHelper.implode(array, ", ", true, false);
   }

   public boolean isValidMetadata(int metadata) {
      return metadata >= 0 && metadata < types.length && types[metadata] != null;
   }

   public int getBlockSubtypeUnchecked(int metadata) {
      return metadata;
   }

   public boolean canBeReplacedBy(int metadata, Block other_block, int other_block_metadata) {
      return other_block == this ? other_block_metadata != metadata : super.canBeReplacedBy(metadata, other_block, other_block_metadata);
   }

   public void setBlockBoundsBasedOnStateAndNeighbors(IBlockAccess block_access, int x, int y, int z) {
      int metadata = block_access.getBlockMetadata(x, y, z);
      float width = 0.2F;
      if (metadata == 0) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), 0.6000000238418579, (double)(0.5F + width));
      } else if (metadata == 1) {
         width = 0.3F;
         this.setBlockBoundsForCurrentThread((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), 0.8125, (double)(0.5F + width));
      } else if (metadata == 2) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), 0.875, (double)(0.5F + width));
      } else if (metadata == 5) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), 0.75, (double)(0.5F + width));
      } else if (metadata == 7) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), 0.8125, (double)(0.5F + width));
      } else if (metadata == 8) {
         this.setBlockBoundsForCurrentThread((double)(0.5F - width), 0.0, (double)(0.5F - width), (double)(0.5F + width), 0.8125, (double)(0.5F + width));
      } else {
         Minecraft.setErrorMessage("setBlockBoundsBasedOnStateAndNeighbors: unhandled case");
      }

   }

   public int getRandomSubtypeForBiome(Random random, BiomeGenBase biome) {
      if (biome == BiomeGenBase.plains && random.nextInt(2) == 0) {
         return 8;
      } else {
         int num_candidates = 0;

         for(int i = 0; i < types.length; ++i) {
            if (types[i] != null && this.isBiomeSuitable(biome, i)) {
               candidates[num_candidates++] = i;
            }
         }

         return num_candidates == 0 ? -1 : candidates[random.nextInt(num_candidates)];
      }
   }

   public int getRandomSubtypeThatCanOccurAt(World world, int x, int y, int z) {
      BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
      int subtype = this.getRandomSubtypeForBiome(world.rand, biome);
      if (subtype < 0) {
         return -1;
      } else {
         while(!this.canOccurAt(world, x, y, z, subtype)) {
            subtype = this.getRandomSubtypeForBiome(world.rand, biome);
         }

         return subtype;
      }
   }

   public boolean isBiomeSuitable(BiomeGenBase biome, int metadata) {
      if (!this.isValidMetadata(metadata)) {
         Minecraft.setErrorMessage("isBiomeSuitable: invalid metadata " + metadata);
         return false;
      } else {
         int subtype = this.getBlockSubtype(metadata);
         if (types[subtype] == null) {
            Minecraft.setErrorMessage("isBiomeSuitable: invalid subtype " + subtype);
            return false;
         } else if (subtype == 2 && !biome.isSwampBiome()) {
            return false;
         } else if (biome.isSwampBiome() && subtype != 2) {
            return false;
         } else if (subtype == 1 && biome.temperature <= BiomeGenBase.plains.temperature) {
            return false;
         } else if ((subtype == 5 || subtype == 7) && biome.temperature < BiomeGenBase.forestHills.temperature) {
            return false;
         } else {
            return !biome.isJungleBiome() || subtype != 8;
         }
      }
   }

   public boolean canOccurAt(World world, int x, int y, int z, int metadata) {
      return this.isBiomeSuitable(world.getBiomeGenForCoords(x, z), metadata) && super.canOccurAt(world, x, y, z, metadata);
   }

   public int getPatchSize(int metadata, BiomeGenBase biome) {
      if (!this.isValidMetadata(metadata)) {
         Minecraft.setErrorMessage("getPatchSize: invalid metadata " + metadata);
      }

      int subtype = this.getBlockSubtype(metadata);
      if (subtype == 2) {
         return 8;
      } else {
         return biome != BiomeGenBase.plains && !biome.isJungleBiome() ? 16 : 64;
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return this.isBiomeSuitable(world.getBiomeGenForCoords(x, z), metadata) && super.isLegalAt(world, x, y, z, metadata);
   }

   static {
      candidates = new int[types.length];
   }
}
