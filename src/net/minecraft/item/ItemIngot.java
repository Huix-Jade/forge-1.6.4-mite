package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemIngot extends Item {
   protected ItemIngot(int id, Material material) {
      super(id, material, "ingots/" + material.name);
      this.setMaxStackSize(8);
      this.setCraftingDifficultyAsComponent(getCraftingDifficultyAsComponent(material));
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   public static float getCraftingDifficultyAsComponent(Material material) {
      return material.durability * 100.0F;
   }
}
