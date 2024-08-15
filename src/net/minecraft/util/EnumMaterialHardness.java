package net.minecraft.util;

import net.minecraft.block.material.Material;

public enum EnumMaterialHardness {
   stone(Material.stone, 2.4F),
   obsidian(Material.obsidian, 2.4F),
   wood(Material.wood, 1.2F),
   tree_leaves(Material.tree_leaves, 0.2F),
   dirt(Material.dirt, 0.5F),
   sand(Material.sand, 0.4F),
   snow(Material.snow, 0.4F),
   crafted_snow(Material.craftedSnow, 0.4F),
   netherrack(Material.netherrack, 1.6F),
   pumpkin(Material.pumpkin, 0.6F),
   quartz(Material.quartz, 0.8F);

   final Material material;
   final float hardness;

   private EnumMaterialHardness(Material material, float hardness) {
      this.material = material;
      this.hardness = hardness;
   }

   public static float getHardnessFor(Material material) {
      EnumMaterialHardness[] values = values();

      for(int i = 0; i < values.length; ++i) {
         EnumMaterialHardness enum_material_hardness = values[i];
         if (enum_material_hardness.material == material) {
            return enum_material_hardness.hardness;
         }
      }

      return 0.0F;
   }
}
