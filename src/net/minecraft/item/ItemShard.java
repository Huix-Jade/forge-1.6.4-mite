package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemShard extends ItemRock {
   protected ItemShard(int id, Material material) {
      super(id, material, "shards/" + material.name);
      this.setMaxStackSize(64);
      this.setCraftingDifficultyAsComponent(ItemRock.getCraftingDifficultyAsComponent(material) / (float)(material == Material.flint ? 4 : 9));
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }
}
