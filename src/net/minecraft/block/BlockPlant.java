package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.IPlantable;

public abstract class BlockPlant extends Block implements IPlantable {
   protected BlockPlant(int id, Material material) {
      this(id, material, (new BlockConstants()).setNeverHidesAdjacentFaces().setNotAlwaysLegal());
   }

   protected BlockPlant(int id, Material material, BlockConstants constants) {
      super(id, material, constants);
      this.setTickRandomly(true);
      float var3 = 0.2F;
      this.setBlockBoundsForAllThreads((double)(0.5F - var3), 0.0, (double)(0.5F - var3), (double)(0.5F + var3), (double)(var3 * 3.0F), (double)(0.5F + var3));
      this.setMaxStackSize(32);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   protected BlockPlant(int id) {
      this(id, Material.plants);
   }

   public boolean ignoresLightCheckIfUnderOpenSky() {
      return true;
   }

   public int getMinAllowedLightValue() {
      return 8;
   }

   public int getMaxAllowedLightValue() {
      return 15;
   }

   public final boolean isLightLevelSuitable(World world, int x, int y, int z) {
      int min_allowed_light_value = this.getMinAllowedLightValue();
      int max_allowed_light_value = this.getMaxAllowedLightValue();
      if (min_allowed_light_value < 1 && max_allowed_light_value > 14) {
         return true;
      } else {
         int block_light_value = world.getFullBlockLightValue(x, y, z);
         return block_light_value >= min_allowed_light_value && block_light_value <= max_allowed_light_value;
      }
   }

   public boolean isLegalAt(World world, int x, int y, int z, int metadata) {
      return super.isLegalAt(world, x, y, z, metadata) && (this.ignoresLightCheckIfUnderOpenSky() && world.canBlockSeeTheSky(x, y, z) || this.isLightLevelSuitable(world, x, y, z));
   }

   public boolean isLegalOn(int metadata, Block block_below, int block_below_metadata) {
      return block_below == grass || block_below == dirt || block_below == tilledField;
   }

   public boolean onNotLegal(World world, int x, int y, int z, int metadata) {
      if (world.getClosestPlayer((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 16.0, false) == null) {
         world.setBlockToAir(x, y, z);
         return true;
      } else {
         return super.onNotLegal(world, x, y, z, metadata);
      }
   }

   public int getRenderType() {
      return 1;
   }

   public boolean dropsAsSelfWhenTrampled(Entity entity) {
      return !(entity instanceof EntityCow);
   }

   public void onTrampledBy(World world, int x, int y, int z, Entity entity) {
      world.destroyBlock((new BlockBreakInfo(world, x, y, z)).setTrampledBy(entity), this.dropsAsSelfWhenTrampled(entity));
   }

   public int getPatchSize(BiomeGenBase biome) {
      return 64;
   }

   public boolean isSolid(boolean[] is_solid, int metadata) {
      return false;
   }

   public boolean isStandardFormCube(boolean[] is_standard_form_cube, int metadata) {
      return false;
   }
}
