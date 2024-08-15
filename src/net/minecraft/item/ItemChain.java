package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ItemChain extends Item {
   protected ItemChain(int id, Material material) {
      super(id, material, "chains/" + material.name);
      this.setMaxStackSize(8);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }
}
